<script lang="ts">
	import type { MlogWatcherStore } from '$lib/mlog_watcher';
	import { Check, CircleAlert, Cpu, LoaderCircle } from '@lucide/svelte';
	import EditorActionButton from './EditorActionButton.svelte';

	let { channel, getText }: { channel: MlogWatcherStore; getText: () => string } = $props();

	let status = $state<'idle' | 'sent' | 'loading' | 'error'>('idle');
	// using ReturnType to avoid issues caused by @types/node
	let timeoutId: ReturnType<typeof setTimeout> | null = null;

	async function sendToMlogWatcher() {
		if (timeoutId !== null) clearTimeout(timeoutId);

		const text = getText();
		status = 'loading';
		try {
			await channel.send(text);
			status = 'sent';
		} catch {
			status = 'error';
		}
		timeoutId = setTimeout(() => (status = 'idle'), 2000);
	}
</script>

<EditorActionButton
	tooltip={status === 'sent' ? 'Sent!' : 'Send to MlogWatcher'}
	onClick={sendToMlogWatcher}
>
	{#if status === 'sent'}
		<Check class="size-4" />
	{:else if status === 'loading'}
		<LoaderCircle class="size-4 animate-spin" />
	{:else if status === 'error'}
		<CircleAlert class="size-4" />
	{:else}
		<Cpu class="size-4" />
	{/if}
</EditorActionButton>
