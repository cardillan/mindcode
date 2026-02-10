<script lang="ts">
	import * as Tabs from '$lib/components/ui/tabs';
	import type { ComponentProps, Snippet } from 'svelte';
	import { Button } from './ui/button';
	import { ChevronsDownUp, ChevronsUpDown, Maximize2, Minimize2 } from '@lucide/svelte';

	export type CollapsibleTabsMode = 'minimized' | 'normal' | 'maximized';
	interface CollapsibleTabsProps extends ComponentProps<typeof Tabs.Root> {
		mode?: CollapsibleTabsMode;
		minimizeLabel: string;
		restoreLabel: string;
		maximizeLabel: string;
		tabTriggers: Snippet;
		children?: Snippet;
		onModeChange?: (mode: CollapsibleTabsMode) => void;
	}

	let {
		ref = $bindable(null),
		value = $bindable(''),
		mode = $bindable('normal'),
		tabTriggers,
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
		<Button
			variant="secondary"
			size="icon"
			class={['h-8 w-8 cursor-pointer', mode !== 'minimized' && 'md:hidden']}
			onclick={() => updateMode(mode === 'minimized' ? 'normal' : 'minimized')}
		>
			{#if mode === 'minimized'}
				<ChevronsUpDown class="h-4 w-4" />
				<!-- <ChevronDown class="h-4 w-4" /> -->
			{:else}
				<ChevronsDownUp class="h-4 w-4" />
				<!-- <FoldVertical class="h-4 w-4" /> -->
				<!-- <ChevronUp class="h-4 w-4" /> -->
			{/if}
			<span class="sr-only">{mode === 'minimized' ? minimizeLabel : restoreLabel}</span>
		</Button>
		<Button
			variant="secondary"
			size="icon"
			class="hidden h-8 w-8 cursor-pointer md:inline-flex"
			onclick={() => updateMode(mode === 'maximized' ? 'normal' : 'maximized')}
		>
			{#if mode === 'maximized'}
				<Minimize2 class="h-4 w-4" />
				<span class="sr-only">{restoreLabel}</span>
			{:else}
				<Maximize2 class="h-4 w-4" />
				<span class="sr-only">{maximizeLabel}</span>
			{/if}
		</Button>
	</Tabs.List>

	<div class={['flex h-(--editor-height) flex-col', mode === 'minimized' && 'hidden']}>
		{@render children?.()}
	</div>
</Tabs.Root>
