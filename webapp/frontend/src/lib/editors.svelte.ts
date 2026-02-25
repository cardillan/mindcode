import {
	currentDocId,
	defaultDocId,
	getTheme,
	invertUpdateDocId,
	themeCompartment,
	updateDocId
} from './codemirror';
import { sourceIdKey, syncUrl, type ThemeStore } from './stores.svelte';
import { defaultKeymap, history, historyKeymap, insertTab } from '@codemirror/commands';
import {
	crosshairCursor,
	drawSelection,
	dropCursor,
	EditorView,
	highlightActiveLine,
	highlightActiveLineGutter,
	highlightSpecialChars,
	keymap,
	lineNumbers,
	rectangularSelection
} from '@codemirror/view';
import type { Attachment } from 'svelte/attachments';
import { mount, unmount, untrack } from 'svelte';
import { Annotation, EditorState, Transaction, type Extension } from '@codemirror/state';
import { browser } from '$app/environment';
import { page } from '$app/state';
import type { ApiHandler, Sample } from './api';
import {
	bracketMatching,
	defaultHighlightStyle,
	foldGutter,
	foldKeymap,
	indentOnInput,
	syntaxHighlighting
} from '@codemirror/language';
import { lintKeymap } from '@codemirror/lint';
import {
	autocompletion,
	closeBrackets,
	closeBracketsKeymap,
	completionKeymap
} from '@codemirror/autocomplete';
import { highlightSelectionMatches, search, searchKeymap } from '@codemirror/search';
import EditorSearchPanel from './components/EditorSearchPanel.svelte';

export const foldChevronDownId = 'fold-chevron-down';
export const foldChevronRightId = 'fold-chevron-right';

export type EditorStoreType = 'input' | 'output';

export interface InputEditorStoreOptions {
	api: ApiHandler;
	theme: ThemeStore;
	samples?: Sample[];
	extensions?: Extension[];
}

const keepCurrentUrl = Annotation.define<boolean>();

export class InputEditorStore {
	view = $state<EditorView>();
	isLoading = $state(true);
	readonly #id = $derived(browser ? page.url.searchParams.get(sourceIdKey) : null);
	/** Reactive value kept in sync with the id stored in the editor's state */
	sourceId = $state<string | null>(this.#id);

	constructor({ api, theme, samples = [], extensions = [] }: InputEditorStoreOptions) {
		const sampleIds = new Set(samples.map((s) => s.id));
		// handle editor creation and destruction
		$effect.pre(() => {
			untrack(() => {
				this.view = new EditorView({
					extensions: [
						commonExtensions(theme),
						defaultDocId.of(null),
						currentDocId,
						invertUpdateDocId,
						EditorView.updateListener.of((update) => {
							this.sourceId = update.state.field(currentDocId);
						}),
						EditorView.updateListener.of((update) => {
							if (this.isLoading) return;

							const docId = update.state.field(currentDocId);
							if (this.#id === docId) return;
							if (update.transactions.some((tr) => tr.annotation(keepCurrentUrl))) return;

							syncUrl({ sourceId: docId, replaceState: false });
						}),
						extensions
					],
					// Intercepts transactions on the input editor to reset the
					// source id when a sample is modified. This allows the server to not have to
					// save the source code if the sourceId is a sample id.
					dispatchTransactions(trs, view) {
						// get the id before the changes
						const originalDocId = view.state.field(currentDocId);
						view.update(trs);

						if (!originalDocId || !sampleIds.has(originalDocId)) return;
						const newDocId = view.state.field(currentDocId);

						if (trs.some((tr) => tr.docChanged) && newDocId === originalDocId) {
							view.dispatch(view.state.update({ effects: updateDocId.of(null) }));
						}
					}
				});
			});

			return () => {
				this.view?.destroy();
				this.view = undefined;
			};
		});

		// sync editor theme with theme store
		$effect(() => {
			if (!this.view) return;
			const editor = this.view;
			const newTheme = getTheme(theme.isDark);
			if (themeCompartment.get(editor.state) === newTheme) return;

			editor.dispatch({
				effects: themeCompartment.reconfigure(newTheme)
			});
		});

		let firstLoad = true;
		// sync editor content with sourceId from URL
		$effect(() => {
			const view = this.view;
			if (!view) return;

			// don't update if the ids are the same
			if (view.state.field(currentDocId) === this.#id) {
				this.isLoading = false;
				return;
			}

			if (!this.#id) {
				view.dispatch({
					effects: updateDocId.of(null),
					changes: { from: 0, to: view.state.doc.length, insert: '' }
				});
				this.isLoading = false;
				firstLoad = false;
				return;
			}

			const sample = samples.find((s) => s.id === this.#id);

			if (sample) {
				view.dispatch({
					effects: updateDocId.of(sample.id),
					changes: { from: 0, to: view.state.doc.length, insert: sample.source }
				});
				this.isLoading = false;
				firstLoad = false;
				return;
			}

			untrack(() => (this.isLoading = true));
			api.loadSource(this.#id).then((serverSource) => {
				this.isLoading = false;
				// console.log(serverSource);
				view.dispatch({
					effects: updateDocId.of(serverSource.id),
					changes: { from: 0, to: view.state.doc.length, insert: serverSource.source },
					// don't add this change to the undo history
					annotations: Transaction.addToHistory.of(!firstLoad)
				});
				firstLoad = false;
			});
		});
	}

	selectSample(sample: Sample, { preserveUrl = true } = {}) {
		if (!this.view) return;

		this.isLoading = false;

		// avoid triggering an id reset if the same sample is selected again
		if (this.sourceId === sample.id) return;

		this.view.dispatch({
			effects: updateDocId.of(sample.id),
			changes: { from: 0, to: this.view.state.doc.length, insert: sample.source },
			annotations: [keepCurrentUrl.of(preserveUrl)]
		});
	}

	clear({ preserveUrl = true } = {}) {
		if (!this.view) return;
		this.isLoading = false;
		this.view.dispatch({
			effects: updateDocId.of(null),
			changes: { from: 0, to: this.view.state.doc.length, insert: '' },
			annotations: [keepCurrentUrl.of(preserveUrl)]
		});
	}

	setEditorId(newId: string | null, { addToHistory = false, preserveUrl = false } = {}) {
		if (!this.view) return;
		const id = this.view.state.field(currentDocId);
		if (newId === id) return;

		this.view.dispatch({
			effects: updateDocId.of(newId),
			annotations: [Transaction.addToHistory.of(addToHistory), keepCurrentUrl.of(preserveUrl)]
		});
	}

	attach: Attachment = (element) => {
		const view = this.view;
		if (!view) return;
		element.appendChild(view.dom);
	};
}

export class OutputEditorStore {
	view = $state<EditorView>();

	constructor(
		public theme: ThemeStore,
		public extensions: Extension[] = []
	) {
		$effect(() => {
			if (!this.view) return;
			const editor = this.view;
			const newTheme = getTheme(this.theme.isDark);
			if (themeCompartment.get(editor.state) === newTheme) return;

			editor.dispatch({
				effects: themeCompartment.reconfigure(newTheme)
			});
		});
	}

	attach: Attachment = (element) => {
		this.view = untrack(() => {
			return new EditorView({
				parent: element,
				extensions: [commonExtensions(this.theme), this.extensions]
			});
		});

		return () => {
			this.view?.destroy();
			this.view = undefined;
		};
	};

	get state(): EditorState | undefined {
		return this.view?.state;
	}
}

function commonExtensions(themeStore: ThemeStore) {
	return [
		lineNumbers(),
		highlightActiveLineGutter(),
		highlightSpecialChars(),
		history(),
		foldGutter({
			markerDOM(open) {
				const div = document.createElement('div');
				const templateId = open ? foldChevronDownId : foldChevronRightId;
				const template = document.getElementById(templateId) as HTMLTemplateElement | null;
				if (template) {
					div.appendChild(template.content.cloneNode(true));
				} else {
					// fallback to a simple marker if the template is missing (shouldn't happen)
					div.textContent = open ? '⌄' : '›';
				}
				return div;
			}
		}),
		drawSelection(),
		dropCursor(),
		EditorState.allowMultipleSelections.of(true),
		indentOnInput(),
		syntaxHighlighting(defaultHighlightStyle, { fallback: true }),
		bracketMatching(),
		closeBrackets(),
		autocompletion(),
		rectangularSelection(),
		crosshairCursor(),
		highlightActiveLine(),
		highlightSelectionMatches(),
		keymap.of([
			...closeBracketsKeymap,
			...defaultKeymap,
			...searchKeymap,
			...historyKeymap,
			...foldKeymap,
			...completionKeymap,
			...lintKeymap,
			{ key: 'Tab', run: insertTab }
		]),
		search({
			createPanel(view) {
				const dom = document.createElement('div');
				const component = mount(EditorSearchPanel, {
					target: dom,
					props: {
						view
					}
				});

				return {
					dom,
					mount() {
						component.selectInput();
					},
					update(update) {
						component.update(update);
					},
					destroy() {
						unmount(component);
					}
				};
			}
		}),
		EditorView.theme({
			'&': {
				height: '100%',
				width: '100%',
				fontSize: '14px'
			},
			'.cm-scroller': {
				fontFamily: 'monospace'
			},
			'.cm-gutters': {
				// prevent the theme libraries from adding padding
				// to the gutter because it breaks active line highlighting
				// by leaving a between the highlighted gutter and the highlighted line
				paddingRight: 0
			}
		}),
		themeCompartment.of(getTheme(themeStore.isDark)),
		EditorView.lineWrapping
	];
}
