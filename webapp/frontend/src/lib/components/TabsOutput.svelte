<script lang="ts">
	import * as Tabs from '$lib/components/ui/tabs';
	import * as Card from '$lib/components/ui/card';
	import CopyButton from './CopyButton.svelte';
	import CompilerMessages from './CompilerMessages.svelte';
	import type { CompileResponseMessage, RunResult, SourceRange } from '$lib/api';
	import { Textarea } from './ui/textarea';
	import * as Select from './ui/select';
	import { type Snippet } from 'svelte';
	import EditorLayoutTabs, { type CollapsibleTabsMode } from './EditorLayoutTabs.svelte';
	import type { ClassValue } from 'svelte/elements';

	type TabName = 'code' | 'output';

	interface TabsOutputProps {
		mode?: CollapsibleTabsMode;
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
		mode = $bindable('normal'),
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
	let hasOutput = $derived(runResults.length > 0 || messages.length > 0);

	// Determine default active tab (first available)
	let preferredTab = $state<TabName>('code');

	const selectedTab = $derived.by((): TabName => {
		if (preferredTab === 'output' && !hasOutput) return 'code';
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

	let selectedOutput = $state('compiler-messages');
	const selectedProcessor = $derived(processorTabs.find((tab) => tab.id === selectedOutput));
</script>

<EditorLayoutTabs
	bind:value={() => selectedTab, (tab) => (preferredTab = tab)}
	bind:mode
	{maximizeLabel}
	{minimizeLabel}
	{restoreLabel}
	{onModeChange}
	class={className}
>
	{#snippet tabTriggers()}
		<Tabs.Trigger value="code" disabled={disableTriggers}>Code</Tabs.Trigger>
		{#if hasOutput}
			<Tabs.Trigger value="output" disabled={disableTriggers}>Output</Tabs.Trigger>
		{/if}
	{/snippet}

	<Tabs.Content value="code" class="h-full">
		{@render editor()}
	</Tabs.Content>

	{#if hasOutput}
		<Tabs.Content value="output" class="h-full">
			<Card.Root class="h-full p-1">
				<Card.Content class="flex h-full flex-col gap-2 p-0">
					<Select.Root type="single" bind:value={selectedOutput}>
						<Select.Trigger class="w-full">
							{#if selectedOutput === 'compiler-messages'}
								Compiler messages
							{:else}
								{selectedProcessor?.label ?? 'Select an output...'}
							{/if}
						</Select.Trigger>
						<Select.Content>
							<Select.Item value="compiler-messages">Compiler messages</Select.Item>
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
					{:else if selectedOutput === 'compiler-messages'}
						<div class="flex-1 overflow-scroll px-5">
							<CompilerMessages infos={messages} title="Compiler messages:" {onJumpToPosition} />
						</div>
					{/if}
				</Card.Content>
			</Card.Root>
		</Tabs.Content>
	{/if}
</EditorLayoutTabs>
