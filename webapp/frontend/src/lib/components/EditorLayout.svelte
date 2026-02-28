<script lang="ts">
	import CopyButton from './CopyButton.svelte';
	import TabsOutput from './TabsOutput.svelte';
	import type { CompileResponseMessage, RunResult, SourceRange } from '$lib/api';
	import CompilerMessages from './CompilerMessages.svelte';
	import EditorLayoutTabs, { type CollapsibleTabsMode } from './EditorLayoutTabs.svelte';
	import { TabsTrigger } from './ui/tabs';
	import TabsContent from './ui/tabs/tabs-content.svelte';
	import * as ButtonGroup from './ui/button-group';
	import type { Snippet } from 'svelte';
	import type { InputEditorStore, OutputEditorStore } from '$lib/editors.svelte';

	let {
		inputLabel,
		inputEditor,
		inputLoading = false,
		outputEditor,
		outputLoading = false,
		runResults = [],
		errors = [],
		warnings = [],
		infos = [],
		onJumpToPosition,
		inputActions,
		outputActions
	}: {
		inputLabel: string;
		inputEditor: InputEditorStore;
		inputLoading?: boolean;
		outputEditor: OutputEditorStore;
		outputLoading?: boolean;
		runResults?: RunResult[];
		errors?: CompileResponseMessage[];
		warnings?: CompileResponseMessage[];
		infos?: CompileResponseMessage[];
		onJumpToPosition?: (range: SourceRange) => void;
		inputActions?: Snippet;
		outputActions?: Snippet;
	} = $props();

	let inputMode = $state<CollapsibleTabsMode>('normal');
	let outputMode = $state<CollapsibleTabsMode>('normal');

	export function ensureOutputIsVisible() {
		if (outputMode === 'minimized') {
			outputMode = 'normal';
		}
		if (inputMode === 'maximized') {
			inputMode = 'normal';
		}
	}
</script>

<div class="flex h-full flex-col gap-4">
	<div
		class={[
			'grid flex-1 grid-cols-1 gap-4',
			inputMode !== 'maximized' && outputMode !== 'maximized' && 'md:grid-cols-2'
		]}
	>
		<!-- Input Editor Section -->
		<div
			class={[
				'flex flex-col gap-2',
				outputMode === 'maximized' && 'hidden',
				inputMode === 'maximized' && 'md:col-span-2'
			]}
		>
			<EditorLayoutTabs
				bind:mode={inputMode}
				value="code"
				minimizeLabel="Minimize input"
				restoreLabel="Restore input"
				maximizeLabel="Maximize input"
				onModeChange={(mode) => {
					if (mode === 'maximized') {
						outputMode = 'normal';
					}
				}}
			>
				{#snippet tabTriggers()}
					<TabsTrigger value="code">{inputLabel}</TabsTrigger>
				{/snippet}

				<TabsContent value="code" class="h-full">
					<div
						class={[
							'relative h-full transition-opacity ',
							inputLoading && 'pointer-events-none opacity-50'
						]}
					>
						<div class="h-full rounded-md border bg-muted" {@attach inputEditor.attach}></div>
						{#if inputActions}
							<ButtonGroup.Root class="absolute top-2 right-2 z-10 opacity-60 hover:opacity-100">
								{@render inputActions()}
							</ButtonGroup.Root>
						{/if}
					</div>
				</TabsContent>
			</EditorLayoutTabs>

			<CompilerMessages {errors} {warnings} {onJumpToPosition} />
		</div>

		<TabsOutput
			bind:mode={outputMode}
			minimizeLabel="Minimize output"
			restoreLabel="Restore output"
			maximizeLabel="Maximize output"
			disableTriggers={outputLoading}
			{runResults}
			messages={infos}
			{onJumpToPosition}
			class={[
				'self-start',
				inputMode === 'maximized' && 'hidden',
				outputMode === 'maximized' && 'md:col-span-2'
			]}
			onModeChange={(mode) => {
				if (mode === 'maximized') {
					inputMode = 'normal';
				}
			}}
		>
			{#snippet editor()}
				<div
					class={[
						'relative h-full transition-opacity',
						outputLoading && 'pointer-events-none opacity-50'
					]}
				>
					<div class="h-full rounded-md border bg-muted" {@attach outputEditor.attach}></div>
					<ButtonGroup.Root class="absolute top-2 right-2 z-10 opacity-60 hover:opacity-100">
						{@render outputActions?.()}
						<CopyButton getText={() => outputEditor.view?.state.doc.toString() ?? ''} />
					</ButtonGroup.Root>
				</div>
			{/snippet}
		</TabsOutput>
	</div>
</div>
