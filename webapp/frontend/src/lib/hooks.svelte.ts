import { page } from '$app/state';
import { untrack } from 'svelte';
import type { ApiHandler, Sample } from './api';
import { goto } from '$app/navigation';
import { EditorView } from 'codemirror';
import { updateEditor } from './codemirror';
import { browser } from '$app/environment';

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
