<script lang="ts">
	import CopyButton from './CopyButton.svelte';
	import TabsOutput from './TabsOutput.svelte';
	import type { EditorStore } from '$lib/stores.svelte';
	import type { CompileResponseMessage, RunResult, SourceRange } from '$lib/api';
	import CompilerMessages from './CompilerMessages.svelte';
	import EditorLayoutTabs from './EditorLayoutTabs.svelte';
	import { TabsTrigger } from './ui/tabs';
	import TabsContent from './ui/tabs/tabs-content.svelte';
	import * as ButtonGroup from './ui/button-group';
	import type { Snippet } from 'svelte';

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
		inputEditor: EditorStore;
		inputLoading?: boolean;
		outputEditor: EditorStore;
		outputLoading?: boolean;
		runResults?: RunResult[];
		errors?: CompileResponseMessage[];
		warnings?: CompileResponseMessage[];
		infos?: CompileResponseMessage[];
		onJumpToPosition?: (range: SourceRange) => void;
		inputActions?: Snippet;
		outputActions?: Snippet;
	} = $props();

	let fullscreen = $state<'input' | 'output' | null>(null);
</script>

<div class="flex h-full flex-col gap-4">
	<div
		class={[
			'grid flex-1 gap-4',
			fullscreen !== null ? 'grid-cols-1' : 'grid-cols-1 md:grid-cols-2'
		]}
	>
		<!-- Input Editor Section -->
		<div
			class={[
				'flex flex-col gap-2',
				fullscreen === 'output' && 'hidden',
				fullscreen === 'input' && 'md:col-span-2'
			]}
		>
			<EditorLayoutTabs
				value="code"
				minimizeLabel="Minimize input"
				restoreLabel="Restore input"
				maximizeLabel="Maximize input"
				onModeChange={(mode) => {
					if (mode === 'maximized') {
						fullscreen = 'input';
					} else if (fullscreen === 'input') {
						fullscreen = null;
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
			minimizeLabel="Minimize output"
			restoreLabel="Restore output"
			maximizeLabel="Maximize output"
			disableTriggers={outputLoading}
			{runResults}
			messages={infos}
			{onJumpToPosition}
			class={[
				'self-start',
				fullscreen === 'input' && 'hidden',
				fullscreen === 'output' && 'md:col-span-2'
			]}
			onModeChange={(mode) => {
				if (mode === 'maximized') {
					fullscreen = 'output';
				} else if (fullscreen === 'output') {
					fullscreen = null;
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
