<script lang="ts">
	import { mlogLanguageExtension } from '$lib/grammars/mlog_language';
	import { EditorView } from 'codemirror';
	import { Code, Play, Trash2 } from '@lucide/svelte';

	import * as Card from '$lib/components/ui/card';
	import EditorLayout from '$lib/components/EditorLayout.svelte';
	import ControlBar from '$lib/components/ControlBar.svelte';
	import BottomActionBar from '$lib/components/BottomActionBar.svelte';
	import { ApiHandler, type RunResult, type SourceRange } from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';
	import {
		EditorStore,
		getThemeContext,
		LocalCompilerTarget,
		LocalSource,
		syncUrl
	} from '$lib/stores.svelte';
	import { jumpToRange, updateEditor } from '$lib/codemirror';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import EditorActionButton from '$lib/components/EditorActionButton.svelte';

	const theme = getThemeContext();
	const mlogEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mlogLanguageExtension] });
	});
	const mindcodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mindcodeLanguage] });
	});

	let runResults = $state<RunResult[]>([]);

	let loadingAction = $state<'decompile' | 'decompile-run' | null>(null);
	let errors = $state<any[]>([]);
	let warnings = $state<any[]>([]);
	let infos = $state<any[]>([]);
	const api = new ApiHandler();
	const localSource = new LocalSource(api, () => mlogEditor.view, []);
	const compilerTarget = new LocalCompilerTarget();

	function handleJumpToPosition(range: SourceRange) {
		if (!mindcodeEditor.view) return;
		jumpToRange(mindcodeEditor.view, range);
	}

	async function handleDecompile(run: boolean) {
		if (!mlogEditor.view) return;
		const source = mlogEditor.view.state.doc.toString();
		loadingAction = run ? 'decompile-run' : 'decompile';
		runResults = [];
		errors = [];
		warnings = [];
		infos = [];
		updateEditor(mindcodeEditor.view, '');

		try {
			const data = await api.decompileMlog({
				sourceId: localSource.id,
				source,
				target: compilerTarget.value,
				run
			});
			runResults = data.runResults;

			if (mindcodeEditor.view) {
				updateEditor(mindcodeEditor.view, data.source);
			}
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			localSource.id = data.sourceId;

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
			runResults = [];
		} finally {
			loadingAction = null;
		}
	}

	async function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
		updateEditor(mindcodeEditor.view, '');
		updateEditor(mlogEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];

		await syncUrl({ localSource, compilerTarget });
	}
</script>

<svelte:head>
	<title>Mindcode: Mlog decompiler</title>
</svelte:head>

<div class="container mx-auto flex flex-col gap-4 px-4 py-4">
	<!-- Info Card -->
	<Card.Root class="shrink-0 border-dashed bg-muted/50">
		<Card.Content class="p-4 text-sm">
			<p>
				Here you can partially decompile an mlog code into Mindcode. <strong
					>The resulting code cannot be directly compiled by Mindcode.</strong
				>
				Jump instructions in the original mlog are transcribed as <strong>if</strong>
				and <strong>goto</strong> statements which aren't supported by Mindcode and must be rewritten
				using conditional statements, loops and other high-level constructs. The decompiler is mainly
				useful to produce expressions and function calls in the correct Mindcode syntax, saving some time
				and possibly helping to avoid some mistakes compared to a manual rewrite of the entire mlog code
				from scratch.
			</p>
		</Card.Content>
	</Card.Root>

	<!-- Control Bar (Desktop) -->
	<div class="hidden shrink-0 md:block">
		<ControlBar
			primaryActions={[
				{ label: 'Decompile', onclick: () => handleDecompile(false), icon: Code },
				{ label: 'Decompile and Run', onclick: () => handleDecompile(true), icon: Play }
			]}
			loading={localSource.isLoading || loadingAction !== null}
		>
			<TargetPicker {compilerTarget} />
		</ControlBar>
	</div>

	<!-- Mobile: Settings -->
	<div class="flex shrink-0 items-center gap-2 md:hidden">
		<TargetPicker {compilerTarget} />
	</div>

	<!-- Editor Layout -->
	<EditorLayout
		inputLabel="Mlog code"
		inputEditor={mlogEditor}
		inputLoading={localSource.isLoading}
		outputEditor={mindcodeEditor}
		outputLoading={localSource.isLoading || loadingAction !== null}
		{runResults}
		{errors}
		{warnings}
		{infos}
		onJumpToPosition={handleJumpToPosition}
	>
		{#snippet inputActions()}
			<EditorActionButton tooltip="Erase mlog" onClick={cleanEditors}>
				<Trash2 class="size-4" />
			</EditorActionButton>
		{/snippet}
	</EditorLayout>

	<!-- Bottom Action Bar (Mobile) -->
	<BottomActionBar
		primaryAction={{
			label: 'Decompile',
			icon: Code,
			onclick: () => handleDecompile(false)
		}}
		secondaryAction={{
			label: 'Decompile and Run',
			icon: Play,
			onclick: () => handleDecompile(true)
		}}
		loading={localSource.isLoading || loadingAction !== null}
	/>
	<ProjectLinks />
</div>
