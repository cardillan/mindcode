<script lang="ts">
	import type { Snippet } from 'svelte';
	import { Button } from './ui/button';
	import * as Tooltip from './ui/tooltip';
	import type { ClassValue } from 'svelte/elements';

	let {
		onClick,
		children,
		tooltip,
		class: className = ''
	}: {
		onClick?: () => void;
		children: Snippet;
		tooltip: string;
		class?: ClassValue;
	} = $props();
</script>

<Tooltip.Root>
	<Tooltip.Trigger>
		{#snippet child({ props })}
			<Button
				{...props}
				variant="outline"
				size="icon"
				class={[props.class, className]}
				onclick={onClick}
			>
				{@render children()}
			</Button>
		{/snippet}
	</Tooltip.Trigger>
	<Tooltip.Content>
		{tooltip}
	</Tooltip.Content>
</Tooltip.Root>
