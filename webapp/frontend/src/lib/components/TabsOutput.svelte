<script lang="ts">
	import * as Tabs from '$lib/components/ui/tabs';
	import * as Card from '$lib/components/ui/card';
	import CopyButton from './CopyButton.svelte';
	import CompilerMessages from './CompilerMessages.svelte';
	import type { CompileResponseMessage, RunResult, SourceRange } from '$lib/api';
	import { Textarea } from './ui/textarea';
	import * as Select from './ui/select';
	import { untrack, type Snippet } from 'svelte';
	import EditorLayoutTabs, { type CollapsibleTabsMode } from './EditorLayoutTabs.svelte';
	import type { ClassValue } from 'svelte/elements';

	type TabName = 'code' | 'run-results' | 'messages';

	interface TabsOutputProps {
		class?: ClassValue;
		runResults?: RunResult[];
		messages?: CompileResponseMessage[];
		disableTriggers?: boolean;
		maximizeLabel: string;
		minimizeLabel: string;
		restoreLabel: string;
		onJumpToPosition?: (range: SourceRange) => void;
		onModeChange?: (mode: CollapsibleTabsMode) => void;
		editor: Snippet;
	}
	let {
		runResults = [],
		messages = [],
		disableTriggers = false,
		class: className,
		maximizeLabel,
		minimizeLabel,
		restoreLabel,
		onJumpToPosition,
		onModeChange,
		editor
	}: TabsOutputProps = $props();

	// Determine which tabs are available
	let hasRunResults = $derived(runResults.length > 0);
	let hasMessages = $derived(messages.length > 0);

	// Determine default active tab (first available)
	let preferredTab = $state<TabName>('code');

	const selectedTab = $derived.by((): TabName => {
		if (preferredTab === 'run-results' && !hasRunResults) return 'code';
		if (preferredTab === 'messages' && !hasMessages) return 'code';
		return preferredTab;
	});

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

<EditorLayoutTabs
	bind:value={() => selectedTab, (tab) => (preferredTab = tab)}
	{maximizeLabel}
	{minimizeLabel}
	{restoreLabel}
	{onModeChange}
	class={className}
>
	{#snippet tabTriggers()}
		<Tabs.Trigger value="code" disabled={disableTriggers}>Code</Tabs.Trigger>
		{#if hasRunResults}
			<Tabs.Trigger value="run-results" disabled={disableTriggers}>Output</Tabs.Trigger>
		{/if}
		{#if hasMessages}
			<Tabs.Trigger value="messages" disabled={disableTriggers}>Messages</Tabs.Trigger>
		{/if}
	{/snippet}

	<Tabs.Content value="code" class="h-full">
		{@render editor()}
	</Tabs.Content>

	{#if hasRunResults}
		<Tabs.Content value="run-results" class="h-full">
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
							<Textarea
								readonly
								value={selectedProcessor.content}
								class="h-full w-full resize-none"
							/>
							<CopyButton floating getText={() => selectedProcessor.content} />
						</div>
					{/if}
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{/if}

	{#if hasMessages}
		<Tabs.Content value="messages" class="h-full">
			<Card.Root class="h-full overflow-scroll">
				<Card.Content>
					<CompilerMessages infos={messages} title="Compiler messages:" {onJumpToPosition} />
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{/if}
</EditorLayoutTabs>
