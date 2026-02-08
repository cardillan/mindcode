<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { LoaderCircle, X } from '@lucide/svelte';

	let {
		primaryAction,
		secondaryAction,
		loading = false,
		visible = true,
		onDismiss
	}: {
		primaryAction: { label: string; icon?: any; onclick: () => void };
		secondaryAction?: { label: string; icon?: any; onclick: () => void };
		loading?: boolean;
		visible?: boolean;
		onDismiss?: () => void;
	} = $props();

	let dismissed = $state(false);
	let isVisible = $derived(visible && !dismissed);

	function handleDismiss() {
		dismissed = true;
		onDismiss?.();
	}
</script>

{#if isVisible}
	<div
		class="fixed right-0 bottom-0 left-0 z-40 border-t bg-background/95 backdrop-blur-lg supports-backdrop-filter:bg-background/80 md:hidden"
		style="padding-bottom: env(safe-area-inset-bottom, 0px);"
	>
		<div class="container mx-auto flex items-center gap-2 px-4 py-3">
			<!-- Primary Action -->
			<Button
				variant="default"
				size="lg"
				class="flex-1"
				onclick={primaryAction.onclick}
				disabled={loading}
			>
				{#if loading}
					<LoaderCircle class="mr-2 h-5 w-5 animate-spin" />
				{:else if primaryAction.icon}
					{@const Icon = primaryAction.icon}
					<Icon class="mr-2 h-5 w-5" />
				{/if}
				{primaryAction.label}
			</Button>

			<!-- Secondary Action -->
			{#if secondaryAction}
				<Button
					variant="secondary"
					size="lg"
					class="flex-1"
					onclick={secondaryAction.onclick}
					disabled={loading}
				>
					{#if loading}
						<LoaderCircle class="mr-2 h-5 w-5 animate-spin" />
					{:else if secondaryAction.icon}
						{@const Icon = secondaryAction.icon}
						<Icon class="mr-2 h-5 w-5" />
					{/if}
					{secondaryAction.label}
				</Button>
			{/if}

			<!-- Dismiss Button -->
			{#if onDismiss}
				<Button variant="ghost" size="icon" onclick={handleDismiss} class="shrink-0">
					<X class="h-5 w-5" />
					<span class="sr-only">Dismiss</span>
				</Button>
			{/if}
		</div>
	</div>
{/if}

<!-- Spacer to prevent content from being hidden behind the bar -->
{#if isVisible}
	<div class="h-20 md:hidden" style="height: calc(5rem + env(safe-area-inset-bottom, 0px));"></div>
{/if}
