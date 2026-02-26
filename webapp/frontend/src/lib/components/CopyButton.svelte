<script lang="ts">
	import { CopyIcon, CheckIcon } from '@lucide/svelte';
	import EditorActionButton from './EditorActionButton.svelte';

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

<EditorActionButton
	onClick={handleCopy}
	tooltip={copied ? 'Copied!' : 'Copy to clipboard'}
	class={[
		floating && 'absolute top-2 right-2 z-10 h-8 gap-1.5 opacity-80 hover:opacity-100',
		className
	]}
>
	{#if copied}
		<CheckIcon class="size-4" />
	{:else}
		<CopyIcon class="size-4" />
	{/if}
</EditorActionButton>
