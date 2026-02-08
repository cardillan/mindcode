<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { LoaderCircle } from '@lucide/svelte';

	type Action = {
		label: string;
		icon?: any;
		onclick: () => void;
		variant?: 'default' | 'secondary' | 'outline' | 'ghost';
	};

	let {
		primaryActions = [],
		secondaryActions = [],
		loading = false,
		children
	}: {
		primaryActions?: Action[];
		secondaryActions?: Action[];
		loading?: boolean;
		children?: any;
	} = $props();
</script>

<div class="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
	<!-- Primary Actions -->
	<div class="flex flex-wrap items-center gap-2">
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

	<!-- Secondary Actions -->
	{#if secondaryActions.length > 0}
		<div class="flex flex-wrap items-center gap-2">
			{#each secondaryActions as action}
				<Button
					variant={action.variant || 'outline'}
					size="default"
					onclick={action.onclick}
					disabled={loading}
				>
					{#if action.icon}
						{@const Icon = action.icon}
						<Icon class="mr-2 h-4 w-4" />
					{/if}
					{action.label}
				</Button>
			{/each}
		</div>
	{/if}
</div>
