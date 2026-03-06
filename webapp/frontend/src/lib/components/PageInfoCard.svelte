<script lang="ts">
	import type { Snippet } from 'svelte';
	import * as Collapsible from './ui/collapsible';
	import { browser } from '$app/environment';
	import { ChevronDown } from '@lucide/svelte';
	import { onMount } from 'svelte';

	export interface PageInfoCardProps {
		children?: Snippet;
		/** The ID used to preserve the collapse state in localStorage */
		cardId: string;
		/** The heading text for the collapsible card */
		heading?: string;
	}

	let { children, cardId, heading = 'About this page' }: PageInfoCardProps = $props();

	const localStorageKey = $derived(`page-info-card-${cardId}-state`);
	let isOpen = $state(true);

	onMount(() => {
		if (browser) {
			isOpen = localStorage.getItem(localStorageKey) !== 'collapsed';
		}
	});

	function handleOpenChange(open: boolean) {
		isOpen = open;
		if (browser) {
			localStorage.setItem(localStorageKey, open ? 'expanded' : 'collapsed');
		}
	}
</script>

<Collapsible.Root
	open={isOpen}
	onOpenChange={handleOpenChange}
	class="shrink-0 rounded-lg border border-dashed border-border bg-muted/50"
>
	<Collapsible.Trigger
		class="flex w-full items-center justify-between px-4 py-3 text-left transition-colors hover:bg-muted/70 data-[state=open]:border-b data-[state=open]:border-dashed data-[state=open]:border-border"
	>
		<span class="text-sm font-medium">{heading}</span>
		<ChevronDown
			class="h-4 w-4 shrink-0 transition-transform duration-200 in-data-[state=open]:rotate-180"
		/>
	</Collapsible.Trigger>
	<Collapsible.Content class="px-4 py-4 text-sm">
		{@render children?.()}
	</Collapsible.Content>
</Collapsible.Root>
