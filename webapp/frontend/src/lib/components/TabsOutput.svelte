<script lang="ts">
	import * as Tabs from '$lib/components/ui/tabs';
	import * as Card from '$lib/components/ui/card';
	import CopyButton from './CopyButton.svelte';
	import CompilerMessages from './CompilerMessages.svelte';
	import type { CompileResponseMessage, RunResult, SourceRange } from '$lib/api';
	import { Textarea } from './ui/textarea';
	import * as Select from './ui/select';
	import { untrack } from 'svelte';

	let {
		runResults = [],
		messages = [],
		onJumpToPosition
	}: {
		runResults?: RunResult[];
		messages?: CompileResponseMessage[];
		onJumpToPosition?: (range: SourceRange) => void;
	} = $props();

	// Determine which tabs are available
	let hasRunResults = $derived(runResults.length > 0);
	let hasMessages = $derived(messages.length > 0);

	// Determine default active tab (first available)
	let defaultTab = $derived(
		hasRunResults ? 'run-results' : hasMessages ? 'messages' : 'run-results'
	);

	// For multi-processor outputs, create sub-tabs
	let processorTabs = $derived(
		runResults.map((p, idx) => ({
			id: `processor-${idx}`,
			label: `${p.processorId} (${p.steps} steps)`,
			content: p.output,
			processorId: p.processorId,
			stepCount: p.steps
		}))
	);

	let selectedProcessorId = $derived(untrack(() => processorTabs[0]?.id));
	const selectedProcessor = $derived(processorTabs.find((tab) => tab.id === selectedProcessorId));
</script>

<Tabs.Root value={defaultTab} class="flex h-full flex-1 flex-col">
	<Tabs.List class="w-full">
		<Tabs.Trigger value="run-results">Run Output</Tabs.Trigger>
		<Tabs.Trigger value="messages">Messages</Tabs.Trigger>
	</Tabs.List>

	{#if hasRunResults}
		<Tabs.Content value="run-results" class="flex-1">
			<Card.Root class="h-full p-1">
				<Card.Content class="flex h-full flex-col gap-2 p-0">
					<Select.Root type="single" bind:value={selectedProcessorId}>
						<Select.Trigger class="w-full">
							{selectedProcessor?.label ?? 'Select a processor...'}
						</Select.Trigger>
						<Select.Content>
							{#each processorTabs as tab}
								<Select.Item value={tab.id}>{tab.label}</Select.Item>
							{/each}
						</Select.Content>
					</Select.Root>
					{#if selectedProcessor}
						<div class="relative flex-1">
							<Textarea readonly value={selectedProcessor.content} class="h-full w-full" />
							<CopyButton floating getText={() => selectedProcessor.content} />
						</div>
					{/if}
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{:else}
		<Tabs.Content value="run-results" class="flex-1">
			<Card.Root class="h-full">
				<Card.Content class="flex h-full items-center justify-center text-muted-foreground">
					No run outputs to display.
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{/if}

	{#if hasMessages}
		<Tabs.Content value="messages" class="flex-1 overflow-scroll">
			<Card.Root>
				<Card.Content>
					<CompilerMessages infos={messages} title="Compiler messages:" {onJumpToPosition} />
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{:else}
		<Tabs.Content value="messages" class="flex-1">
			<Card.Root class="h-full">
				<Card.Content class="flex h-full items-center justify-center text-muted-foreground">
					No messages to display.
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{/if}
</Tabs.Root>
