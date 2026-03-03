<script lang="ts">
	import type { Snippet } from 'svelte';
	import { Button, type ButtonVariant } from './ui/button';
	import * as Tooltip from './ui/tooltip';
	import type { ClassValue } from 'svelte/elements';

	let {
		onClick,
		children,
		tooltip,
		class: className = '',
		disabled = false,
		variant = 'secondary'
	}: {
		onClick?: () => void;
		children: Snippet;
		tooltip: string;
		disabled?: boolean;
		class?: ClassValue;
		variant?: ButtonVariant;
	} = $props();
</script>

<Tooltip.Root>
	<Tooltip.Trigger>
		{#snippet child({ props })}
			<Button
				{...props}
				{variant}
				size="icon"
				class={[props.class, className, 'cursor-pointer']}
				onclick={onClick}
				{disabled}
			>
				{@render children()}
			</Button>
		{/snippet}
	</Tooltip.Trigger>
	<Tooltip.Content>
		{tooltip}
	</Tooltip.Content>
</Tooltip.Root>
