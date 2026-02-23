import { page } from '$app/state';
import { getContext, setContext, untrack } from 'svelte';
import { goto } from '$app/navigation';
import { browser } from '$app/environment';

export const sourceIdKey = 's';
export const compilerTargetKey = 'compilerTarget';

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

function updateParams(searchParams: URLSearchParams, key: string, value: string | null) {
	if (value !== null) {
		searchParams.set(key, value);
	} else {
		searchParams.delete(key);
	}
}

export async function syncUrl({
	sourceId,
	compilerTarget,
	replaceState = true
}: {
	sourceId?: string | null;
	compilerTarget?: string;
	replaceState?: boolean;
}) {
	const url = new URL(page.url);

	if (
		(sourceId === undefined || url.searchParams.get(sourceIdKey) === sourceId) &&
		(!compilerTarget || url.searchParams.get(compilerTargetKey) === compilerTarget)
	) {
		return;
	}

	if (sourceId !== undefined) {
		updateParams(url.searchParams, sourceIdKey, sourceId);
	}
	if (compilerTarget !== undefined) {
		updateParams(url.searchParams, compilerTargetKey, compilerTarget);
	}

	await goto(url, { replaceState, noScroll: true, keepFocus: true });
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
