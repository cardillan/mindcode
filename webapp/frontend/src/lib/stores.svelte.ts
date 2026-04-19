import { page } from '$app/state';
import { getContext, setContext } from 'svelte';
import { goto } from '$app/navigation';
import { browser } from '$app/environment';

const validProcessors = ['none', 'm', 'l', 'h', 'w'] as const;

export type GameVersion = 6 | 7 | 8;
export type ProcessorType = (typeof validProcessors)[number];

export const sourceIdKey = 's';
export const compilerTargetKey = 'compilerTarget';
export const defaultGameVersion = 8;
export const defaultProcessorType = 'm';

export class LocalCompilerTarget {
	version: GameVersion;
	processor: ProcessorType;

	constructor(
		public defaultVersion: GameVersion = defaultGameVersion,
		public defaultProcessor: ProcessorType = defaultProcessorType
	) {
		const defaults = {
			version: defaultVersion,
			processor: defaultProcessor
		};

		const parsed = $derived.by(() => {
			if (!browser) return defaults;
			const value = page.url.searchParams.get(compilerTargetKey);

			if (!value) return defaults;
			return parseCompilerTarget(value);
		});
		this.version = $derived(parsed.version);
		this.processor = $derived(parsed.processor);
	}

	get value() {
		return this.version + (this.processor !== 'none' ? this.processor : '');
	}

	resetToDefault() {
		this.version = this.defaultVersion;
		this.processor = this.defaultProcessor;
	}
}

export function isValidGameVersion(value: number): value is GameVersion {
	if (!Number.isInteger(value)) return false;
	return value >= 6 && value <= 8;
}

export function isValidProcessorType(value: string): value is ProcessorType {
	return validProcessors.includes(value as ProcessorType);
}

export function parseCompilerTarget(value: string): {
	version: GameVersion;
	processor: ProcessorType;
} {
	const version = Number(value.slice(0, 1));
	const processor = value.slice(1) || 'none';

	if (!isValidGameVersion(version) || !isValidProcessorType(processor)) {
		return { version: defaultGameVersion, processor: defaultProcessorType };
	}

	return { version, processor };
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
