<script lang="ts">
	import { CopyIcon, CheckIcon } from '@lucide/svelte';
	import { Button } from '$lib/components/ui/button';

	let {
		getText,
		class: className = ''
	}: {
		getText: () => string;
		class?: string;
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
	size="sm"
	class="absolute top-2 right-2 z-10 h-8 gap-1.5 opacity-80 hover:opacity-100 {className}"
	onclick={handleCopy}
>
	{#if copied}
		<CheckIcon class="h-4 w-4" />
		Copied!
	{:else}
		<CopyIcon class="h-4 w-4" />
		Copy
	{/if}
</Button>
