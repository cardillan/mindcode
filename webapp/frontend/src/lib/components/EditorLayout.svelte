<script lang="ts">
	import { Label } from '$lib/components/ui/label';
	import { Button } from '$lib/components/ui/button';
	import CopyButton from './CopyButton.svelte';
	import TabsOutput from './TabsOutput.svelte';
	import { Maximize2, Minimize2, ChevronDown, ChevronUp } from '@lucide/svelte';
	import type { EditorStore } from '$lib/stores.svelte';
	import type { CompileResponseMessage, RunResult, SourceRange } from '$lib/api';
	import CompilerMessages from './CompilerMessages.svelte';

	let {
		inputLabel,
		inputEditor,
		inputLoading = false,
		outputLabel,
		outputEditor,
		outputLoading = false,
		runResults = [],
		errors = [],
		warnings = [],
		infos = [],
		onJumpToPosition,
		// Layout options
		collapsible = true
	}: {
		inputLabel: string;
		inputEditor: EditorStore;
		inputLoading?: boolean;
		outputLabel: string;
		outputEditor: EditorStore;
		outputLoading?: boolean;
		runResults?: RunResult[];
		errors?: CompileResponseMessage[];
		warnings?: CompileResponseMessage[];
		infos?: CompileResponseMessage[];
		onJumpToPosition?: (range: SourceRange) => void;
		// Layout options
		collapsible?: boolean;
	} = $props();

	let inputCollapsed = $state(false);
	let outputCollapsed = $state(false);
	let fullscreen = $state<'input' | 'output' | null>(null);

	function toggleFullscreen(section: 'input' | 'output') {
		fullscreen = fullscreen === section ? null : section;
	}
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
			<!-- Header with label and collapse/fullscreen controls -->
			<div class="flex items-center justify-between">
				<Label class="text-lg font-bold">{inputLabel}</Label>
				<div class="flex gap-1">
					{#if collapsible}
						<Button
							variant="ghost"
							size="icon"
							class="h-8 w-8 md:hidden"
							onclick={() => (inputCollapsed = !inputCollapsed)}
						>
							{#if inputCollapsed}
								<ChevronDown class="h-4 w-4" />
							{:else}
								<ChevronUp class="h-4 w-4" />
							{/if}
							<span class="sr-only">{inputCollapsed ? 'Expand' : 'Collapse'} input</span>
						</Button>
					{/if}
					<Button
						variant="ghost"
						size="icon"
						class="h-8 w-8"
						onclick={() => toggleFullscreen('input')}
					>
						{#if fullscreen === 'input'}
							<Minimize2 class="h-4 w-4" />
							<span class="sr-only">Exit fullscreen</span>
						{:else}
							<Maximize2 class="h-4 w-4" />
							<span class="sr-only">Fullscreen input</span>
						{/if}
					</Button>
				</div>
			</div>

			<!-- Editor container -->
			<div
				class={[
					'h-[60dvh] rounded-md border bg-muted transition-opacity',
					inputLoading && 'pointer-events-none opacity-50',
					inputCollapsed && 'hidden'
				]}
				{@attach inputEditor.attach}
			></div>

			<CompilerMessages {errors} {warnings} {onJumpToPosition} />
		</div>

		<!-- Output Section -->
		<div
			class={[
				'flex flex-col gap-2',
				fullscreen === 'input' && 'hidden',
				fullscreen === 'output' && 'md:col-span-2'
			]}
		>
			<!-- Header with label and collapse/fullscreen controls -->
			<div class="flex items-center justify-between">
				<Label class="text-lg font-bold">{outputLabel}</Label>
				<div class="flex gap-1">
					{#if collapsible}
						<Button
							variant="ghost"
							size="icon"
							class="h-8 w-8 md:hidden"
							onclick={() => (outputCollapsed = !outputCollapsed)}
						>
							{#if outputCollapsed}
								<ChevronDown class="h-4 w-4" />
							{:else}
								<ChevronUp class="h-4 w-4" />
							{/if}
							<span class="sr-only">{outputCollapsed ? 'Expand' : 'Collapse'} output</span>
						</Button>
					{/if}
					<Button
						variant="ghost"
						size="icon"
						class="h-8 w-8"
						onclick={() => toggleFullscreen('output')}
					>
						{#if fullscreen === 'output'}
							<Minimize2 class="h-4 w-4" />
							<span class="sr-only">Exit fullscreen</span>
						{:else}
							<Maximize2 class="h-4 w-4" />
							<span class="sr-only">Fullscreen output</span>
						{/if}
					</Button>
				</div>
			</div>

			<!-- Editor with tabs for run results/messages -->
			<div class={['flex flex-col gap-2', outputCollapsed && 'hidden']}>
				<!-- Main editor output -->
				<div
					class={['relative transition-opacity', outputLoading && 'pointer-events-none opacity-50']}
				>
					<CopyButton floating getText={() => outputEditor.view?.state.doc.toString() ?? ''} />
					<div
						class={['h-[30dvh] rounded-md border bg-muted', fullscreen === 'output' && 'h-[60dvh]']}
						{@attach outputEditor.attach}
					></div>
				</div>

				<!-- Tabs for run results and messages -->
				<div
					class={[
						'h-[calc(30dvh-(--spacing(2)))] rounded-md border bg-muted',
						fullscreen === 'output' && 'hidden'
					]}
				>
					<!-- // fullscreen === 'output' ? 'h-[calc(100vh-55rem)]' : 'h-[15dvh] md:h-[15dvh]' -->
					<TabsOutput {runResults} messages={infos} {onJumpToPosition} />
				</div>
			</div>
		</div>
	</div>
</div>
