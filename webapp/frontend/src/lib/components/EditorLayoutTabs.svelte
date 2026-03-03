<script lang="ts">
	import * as Tabs from '$lib/components/ui/tabs';
	import * as ButtonGroup from '$lib/components/ui/button-group';
	import type { ComponentProps, Snippet } from 'svelte';
	import { ChevronsDownUp, ChevronsUpDown, Maximize2, Minimize2 } from '@lucide/svelte';
	import EditorActionButton from './EditorActionButton.svelte';

	export type CollapsibleTabsMode = 'minimized' | 'normal' | 'maximized';

	interface CollapsibleTabsProps extends ComponentProps<typeof Tabs.Root> {
		mode?: CollapsibleTabsMode;
		minimizeLabel: string;
		restoreLabel: string;
		maximizeLabel: string;
		tabTriggers: Snippet;
		tabActions?: Snippet<[tab: string]>;
		children?: Snippet;
		onModeChange?: (mode: CollapsibleTabsMode) => void;
	}

	let {
		ref = $bindable(null),
		value = $bindable(''),
		mode = $bindable('normal'),
		tabTriggers,
		tabActions,
		children,
		onModeChange,
		minimizeLabel,
		restoreLabel,
		maximizeLabel,
		...restProps
	}: CollapsibleTabsProps = $props();

	function updateMode(newMode: CollapsibleTabsMode) {
		mode = newMode;
		if (onModeChange) {
			onModeChange(newMode);
		}
	}
</script>

<Tabs.Root bind:ref bind:value {...restProps}>
	<Tabs.List class="w-full flex-1">
		{@render tabTriggers()}
		<ButtonGroup.Root>
			{@render tabActions?.(value)}
			<EditorActionButton
				tooltip={mode === 'minimized' ? restoreLabel : minimizeLabel}
				class={[mode !== 'minimized' && 'md:hidden']}
				onClick={() => updateMode(mode === 'minimized' ? 'normal' : 'minimized')}
			>
				{#if mode === 'minimized'}
					<ChevronsUpDown class="size-4" />
				{:else}
					<ChevronsDownUp class="size-4" />
				{/if}
				<span class="sr-only">{mode === 'minimized' ? restoreLabel : minimizeLabel}</span>
			</EditorActionButton>
			<EditorActionButton
				tooltip={mode === 'maximized' ? restoreLabel : maximizeLabel}
				class="hidden md:inline-flex"
				onClick={() => updateMode(mode === 'maximized' ? 'normal' : 'maximized')}
			>
				{#if mode === 'maximized'}
					<Minimize2 class="h-4 w-4" />
					<span class="sr-only">{restoreLabel}</span>
				{:else}
					<Maximize2 class="h-4 w-4" />
					<span class="sr-only">{maximizeLabel}</span>
				{/if}
			</EditorActionButton>
		</ButtonGroup.Root>
	</Tabs.List>

	<div class={['flex h-(--editor-height) flex-col', mode === 'minimized' && 'hidden']}>
		{@render children?.()}
	</div>
</Tabs.Root>
