<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { LoaderCircle } from '@lucide/svelte';
	import type { Component, Snippet } from 'svelte';

	type Action = {
		label: string;
		icon?: Component;
		onclick: () => void;
		variant?: 'default' | 'secondary' | 'outline' | 'ghost';
	};

	let {
		primaryActions = [],
		loading = false,
		children
	}: {
		primaryActions?: Action[];
		loading?: boolean;
		children?: Snippet;
	} = $props();
</script>

<div class="mb-4 flex flex-wrap items-center gap-2">
	{#each primaryActions as action}
		<Button
			variant={action.variant || 'default'}
			size="default"
			onclick={action.onclick}
			disabled={loading}
		>
			{#if loading}
				<LoaderCircle class="mr-2 h-4 w-4 animate-spin" />
			{:else if action.icon}
				{@const Icon = action.icon}
				<Icon class="mr-2 h-4 w-4" />
			{/if}
			{action.label}
		</Button>
	{/each}

	<!-- Settings/other controls slot -->
	{#if children}
		<div class="flex items-center gap-2">
			{@render children()}
		</div>
	{/if}
</div>
