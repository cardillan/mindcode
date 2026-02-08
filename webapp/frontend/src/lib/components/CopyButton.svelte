<script lang="ts">
	import { CopyIcon, CheckIcon } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';

	let {
		getText,
		class: className = '',
		floating = false
	}: {
		getText: () => string;
		class?: string;
		floating?: boolean;
	} = $props();

	let copied = $state(false);

	async function handleCopy() {
		const text = getText();
		if (!text) return;

		await navigator.clipboard.writeText(text);
		copied = true;
		setTimeout(() => (copied = false), 2000);
	}
</script>

<Button
	variant="outline"
	size="icon"
	class={[
		floating && 'absolute top-2 right-2 z-10 h-8 gap-1.5 opacity-80 hover:opacity-100',
		className
	]}
	onclick={handleCopy}
>
	{#if copied}
		<CheckIcon class="h-4 w-4" />
	{:else}
		<CopyIcon class="h-4 w-4" />
	{/if}
</Button>
