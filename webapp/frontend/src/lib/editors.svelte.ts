import {
	currentDocId,
	defaultDocId,
	getTheme,
	invertUpdateDocId,
	lineWrappingCompartment,
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
import type { Settings } from './settings.svelte';
import { toast } from 'svelte-sonner';

export const foldChevronDownId = 'fold-chevron-down';
export const foldChevronRightId = 'fold-chevron-right';

export type EditorStoreType = 'input' | 'output';

export interface InputEditorStoreOptions {
	api: ApiHandler;
	theme: ThemeStore;
	samples?: Sample[];
	extensions?: Extension[];
	settings: Settings;
}

const keepCurrentUrl = Annotation.define<boolean>();
const internalUpdate = Annotation.define<boolean>();

export class InputEditorStore {
	view = $state<EditorView>();
	isLoading = $state(true);
	#id = $derived(browser ? page.url.searchParams.get(sourceIdKey) : null);
	/** Reactive value kept in sync with the id stored in the editor's state */
	sourceId = $state<string | null>(this.#id);

	constructor({ api, theme, samples = [], extensions = [], settings }: InputEditorStoreOptions) {
		const sampleIds = new Set(samples.map((s) => s.id));
		// handle editor creation and destruction
		$effect.pre(() => {
			untrack(() => {
				this.view = new EditorView({
					extensions: [
						commonExtensions(theme, settings),
						customFoldGutter(),
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

							this.#id = docId;
							syncUrl({ sourceId: docId, replaceState: false });
						}),
						extensions
					],
					// Intercepts transactions on the input editor to reset the
					// source id when a sample is modified. This allows the server to not have to
					// save the source code if the sourceId is a sample id.
					dispatchTransactions(trs, view) {
						const docId = view.state.field(currentDocId);

						if (shouldResetDocId(trs, docId, sampleIds)) {
							const tr = view.state.update(
								{
									effects: updateDocId.of(null)
								},
								...trs
							);
							view.update([tr]);
							return;
						}
						view.update(trs);
					}
				});
			});

			return () => {
				this.view?.destroy();
				this.view = undefined;
			};
		});

		syncEditorTheme(() => this.view, theme);
		syncEditorLineWrapping(() => this.view, settings);

		// sync editor content with sourceId from URL
		$effect(() => {
			const view = this.view;
			if (!view) return;

			const id = this.#id;
			const docId = view.state.field(currentDocId);

			untrack(() => {
				// don't update if the ids are the same
				if (docId === id) {
					this.isLoading = false;
					return;
				}

				if (!id) {
					view.dispatch({
						effects: updateDocId.of(null),
						changes: { from: 0, to: view.state.doc.length, insert: '' },
						annotations: internalUpdate.of(true)
					});
					this.isLoading = false;
					return;
				}

				const sample = samples.find((s) => s.id === id);

				if (sample) {
					view.dispatch({
						effects: updateDocId.of(sample.id),
						changes: { from: 0, to: view.state.doc.length, insert: sample.source },
						annotations: internalUpdate.of(true)
					});
					this.isLoading = false;
					return;
				}

				this.isLoading = true;
				api
					.loadSource(id)
					.then((serverSource) => {
						view.dispatch({
							effects: updateDocId.of(serverSource.id),
							changes: { from: 0, to: view.state.doc.length, insert: serverSource.source },
							annotations: internalUpdate.of(true)
						});
						this.isLoading = false;
					})
					.catch((error) => {
						toast.error('Failed to load the source. Please check the URL and try again.');
						console.error('Error loading source:', error);

						view.dispatch({
							// preserve the id in case the user wants to reload
							effects: updateDocId.of(id),
							changes: { from: 0, to: view.state.doc.length, insert: '' },
							annotations: internalUpdate.of(true)
						});
						this.isLoading = false;
						return;
					});
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
			annotations: [keepCurrentUrl.of(preserveUrl), internalUpdate.of(true)]
		});
	}

	clear({ preserveUrl = true } = {}) {
		if (!this.view) return;
		this.isLoading = false;
		this.view.dispatch({
			effects: updateDocId.of(null),
			changes: { from: 0, to: this.view.state.doc.length, insert: '' },
			annotations: [keepCurrentUrl.of(preserveUrl), internalUpdate.of(true)]
		});
	}

	setEditorId(newId: string | null, { addToHistory = false, preserveUrl = false } = {}) {
		if (!this.view) return;
		const id = this.view.state.field(currentDocId);
		if (newId === id) return;

		this.view.dispatch({
			effects: updateDocId.of(newId),
			annotations: [
				Transaction.addToHistory.of(addToHistory),
				keepCurrentUrl.of(preserveUrl),
				internalUpdate.of(true)
			]
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
		public extensions: Extension[] = [],
		public settings: Settings
	) {
		$effect.pre(() => {
			untrack(() => {
				this.view = new EditorView({
					extensions: [commonExtensions(theme, settings), ...extensions]
				});
			});

			return () => {
				this.view?.destroy();
				this.view = undefined;
			};
		});

		syncEditorTheme(() => this.view, theme);
		syncEditorLineWrapping(() => this.view, settings);
	}

	attach: Attachment = (element) => {
		const view = this.view;
		if (!view) return;
		element.appendChild(view.dom);
	};

	get state(): EditorState | undefined {
		return this.view?.state;
	}
}

function syncEditorTheme(getView: () => EditorView | undefined, themeStore: ThemeStore) {
	$effect(() => {
		const view = getView();
		if (!view) return;
		const editor = view;
		const newTheme = getTheme(themeStore.isDark);
		if (themeCompartment.get(editor.state) === newTheme) return;

		untrack(() => {
			view.dispatch({
				effects: themeCompartment.reconfigure(newTheme)
			});
		});
	});
}

function syncEditorLineWrapping(getView: () => EditorView | undefined, settings: Settings) {
	$effect(() => {
		const view = getView();
		if (!view) return;
		const { lineWrapping } = settings;

		untrack(() => {
			view.dispatch({
				effects: lineWrappingCompartment.reconfigure(lineWrapping ? EditorView.lineWrapping : [])
			});
		});
	});
}

function commonExtensions(themeStore: ThemeStore, settings: Settings): Extension[] {
	return [
		lineNumbers(),
		highlightActiveLineGutter(),
		highlightSpecialChars(),
		history(),
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
		lineWrappingCompartment.of(settings.lineWrapping ? EditorView.lineWrapping : [])
	];
}

function customFoldGutter(): Extension {
	return foldGutter({
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
	});
}

export function shouldResetDocId(
	transactions: readonly Transaction[],
	docId: string | null,
	sampleIds: Set<string>
): boolean {
	const isSample = docId && sampleIds.has(docId);

	if (!isSample) return false;

	const hasUserEdit = transactions.some(
		(transaction) => transaction.docChanged && !transaction.annotation(internalUpdate)
	);

	return hasUserEdit;
}
