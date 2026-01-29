import { page } from '$app/state';
import { getContext, setContext, untrack } from 'svelte';
import type { ApiHandler, Sample } from './api';
import { goto } from '$app/navigation';
import { basicSetup, EditorView } from 'codemirror';
import { getTheme, themeCompartment, updateEditor } from './codemirror';
import { browser } from '$app/environment';
import type { Attachment } from 'svelte/attachments';
import type { EditorState, Extension } from '@codemirror/state';
import { keymap } from '@codemirror/view';
import { insertTab } from '@codemirror/commands';

export const sourceIdKey = 's';
export const compilerTargetKey = 'compilerTarget';

export class LocalSource {
	isLoading = $state(true);
	id = $state<string>();
	#value = $state('');
	#getEditor: () => EditorView | undefined;

	constructor(
		public api: ApiHandler,
		getEditor: () => EditorView | undefined,
		samples: Sample[]
	) {
		this.#getEditor = getEditor;

		const id = $derived(browser ? page.url.searchParams.get(sourceIdKey) : null);

		$effect(() => {
			if (!id) {
				untrack(() => {
					this.id = undefined;
					this.isLoading = false;
					this.#value = '';
				});
				return;
			}

			const sample = samples.find((s) => s.id === id);

			if (sample) {
				untrack(() => {
					this.id = undefined;
					this.isLoading = false;
					this.#value = sample.source;
				});
				return;
			}

			untrack(() => (this.isLoading = true));
			api.loadSource(id).then((serverSource) => {
				this.id = serverSource.id;
				this.isLoading = false;
				this.#value = serverSource.source;
			});
		});

		$effect(() => {
			updateEditor(this.#getEditor(), this.#value);
		});
	}

	selectSample(sample: Sample) {
		this.id = undefined;
		this.isLoading = false;
		this.#value = sample.source;
	}

	clear() {
		this.id = undefined;
		this.isLoading = false;
		this.#value = '';
	}

	updateParams(searchParams: URLSearchParams) {
		if (this.id) {
			searchParams.set(sourceIdKey, this.id);
		} else {
			searchParams.delete(sourceIdKey);
		}
	}
}

export class LocalCompilerTarget {
	value = $state('7');

	constructor() {
		this.value = untrack(
			() => (browser ? page.url.searchParams.get(compilerTargetKey) : null) || '7'
		);

		$effect(() => {
			this.value = page.url.searchParams.get(compilerTargetKey) || '7';
		});
	}

	updateParams(searchParams: URLSearchParams) {
		searchParams.set(compilerTargetKey, this.value);
	}
}

export async function syncUrl({
	localSource,
	compilerTarget
}: {
	localSource?: LocalSource;
	compilerTarget?: LocalCompilerTarget;
}) {
	const url = new URL(page.url);

	if (
		(!localSource || url.searchParams.get(sourceIdKey) === localSource.id) &&
		(!compilerTarget || url.searchParams.get(compilerTargetKey) === compilerTarget.value)
	) {
		return;
	}

	localSource?.updateParams(url.searchParams);
	compilerTarget?.updateParams(url.searchParams);

	await goto(url, { replaceState: true, noScroll: true, keepFocus: true });
}

export class ThemeStore {
	isDark = $state(false);

	constructor() {
		if (!browser) return;
		this.isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

		$effect(() => {
			const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
			const onChange = (e: MediaQueryListEvent) => {
				this.isDark = e.matches;
			};

			mediaQuery.addEventListener('change', onChange);
			return () => mediaQuery.removeEventListener('change', onChange);
		});
	}
}

export function setThemeContext() {
	setContext('themeStore', new ThemeStore());
}

export function getThemeContext(): ThemeStore {
	return getContext<ThemeStore>('themeStore');
}

export class EditorStore {
	view = $state<EditorView>();

	constructor(
		public themeStore: ThemeStore,
		public createEditor: (parent: Element, baseExtensions: Extension[]) => EditorView
	) {
		$effect(() => {
			if (!this.view) return;
			const editor = this.view;
			const newTheme = getTheme(this.themeStore.isDark);
			if (themeCompartment.get(editor.state) === newTheme) return;

			editor.dispatch({
				effects: themeCompartment.reconfigure(newTheme)
			});
		});
	}

	attach: Attachment = (element) => {
		this.view = untrack(() =>
			this.createEditor(element, [
				basicSetup,
				themeCompartment.of(getTheme(this.themeStore.isDark)),
				keymap.of([{ key: 'Tab', run: insertTab }]),
				EditorView.theme({
					'&': {
						height: '100%',
						width: '100%',
						fontSize: '14px'
					},
					'.cm-scroller': {
						fontFamily: 'monospace'
					}
				})
			])
		);

		return () => {
			this.view?.destroy();
			this.view = undefined;
		};
	};

	get state(): EditorState | undefined {
		return this.view?.state;
	}
}
