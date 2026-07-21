<script lang="ts">
	import type {MlogWatcherStore} from '$lib/mlog_watcher';
	import {Check, CircleAlert, Cpu, LoaderCircle} from '@lucide/svelte';
	import EditorActionButton from './EditorActionButton.svelte';
	import {toast} from 'svelte-sonner';

	let {
		channel,
		getText,
		disabled = false,
		schematic = false
	}: { channel: MlogWatcherStore; getText: () => string; disabled?: boolean; schematic?: boolean } = $props();

	let status = $state<'idle' | 'sent' | 'loading' | 'error'>('idle');
	// using ReturnType to avoid issues caused by @types/node
	let timeoutId: ReturnType<typeof setTimeout> | null = null;

	async function sendToMlogWatcher() {
		if (timeoutId !== null) clearTimeout(timeoutId);

		const text = getText();
		status = 'loading';
		try {
			const response = await channel.updateSelectedProcessor(text);
			if (response.status === 'success') {
				status = 'sent';
			} else {
				status = 'error';
				const errorCode = response.result.text;
				let description = '';
				switch (errorCode) {
					case 'no_processor_attached':
						description = 'No processor has been selected.';
						break;
					case 'internal_error':
						description = 'An internal error occurred while processing the mlog code.';
						break;
					case 'invalid_arguments':
					case 'unknown_method':
					case 'unsupported_method_version':
						description = 'The MlogWatcher version is incompatible with this feature.';
						break;
					default:
						description = 'An unknown error occurred.';
						// typescript will generate an error if there is a case that is not covered
						// by any case body
						errorCode satisfies never;
				}
				toast.error(`Failed to send to MlogWatcher: ${description} (Error code: ${errorCode})`);
			}
		} catch (e) {
			status = 'error';
			console.error(e);
		}
		timeoutId = setTimeout(() => (status = 'idle'), 2000);
	}

	async function sendSchematicToMlogWatcher() {
		if (timeoutId !== null) clearTimeout(timeoutId);

		const text = getText();
		status = 'loading';
		try {
			const response = await channel.putSchematicInLibrary(text, true);
			if (response.status === 'success') {
				status = 'sent';
			} else {
				status = 'error';
				const errorCode = response.result.text;
				let description = '';
				switch (errorCode) {
					case 'schematic_import_failed':
						description = 'Schematic import failed (invalid file?).';
						break;
					case 'internal_error':
						description = 'An internal error occurred while processing the mlog code.';
						break;
					case 'invalid_arguments':
					case 'unknown_method':
					case 'unsupported_method_version':
						description = 'The MlogWatcher version is incompatible with this feature.';
						break;
					default:
						description = 'An unknown error occurred.';
						// typescript will generate an error if there is a case that is not covered
						// by any case body
						errorCode satisfies never;
				}
				toast.error(`Failed to send to MlogWatcher: ${description} (Error code: ${errorCode})`);
			}
		} catch (e) {
			status = 'error';
			console.error(e);
		}
		timeoutId = setTimeout(() => (status = 'idle'), 2000);
	}
</script>

<EditorActionButton
	tooltip={status === 'sent' ? 'Sent!' : 'Send to MlogWatcher'}
	onClick={schematic ? sendSchematicToMlogWatcher : sendToMlogWatcher }
	{disabled}
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
