<script lang="ts">
	import { mlogLanguageExtension } from '$lib/grammars/mlog_language';
	import { setDiagnostics } from '@codemirror/lint';
	import { tick, untrack } from 'svelte';
	import { Play, Code, Trash2 } from '@lucide/svelte';

	import EditorLayout from '$lib/components/EditorLayout.svelte';
	import ControlBar from '$lib/components/ControlBar.svelte';
	import BottomActionBar from '$lib/components/BottomActionBar.svelte';
	import {
		ApiHandler,
		type CompileResponseMessage,
		type RunResult,
		type Sample,
		type SourceRange
	} from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';
	import type { PageProps } from './$types';
	import {
		compileMessagesToDiagnostics,
		jumpToRange,
		updateDocId,
		updateEditor
	} from '$lib/codemirror';
	import { LocalCompilerTarget, syncUrl, getThemeContext } from '$lib/stores.svelte';
	import { getSettingsContext } from '$lib/settings.svelte';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import { Compartment } from '@codemirror/state';
	import SamplePicker from '$lib/components/SamplePicker.svelte';
	import EditorActionButton from '$lib/components/EditorActionButton.svelte';
	import { InputEditorStore, OutputEditorStore } from '$lib/editors.svelte';
	import { MlogWatcherStore } from '$lib/mlog_watcher';
	import MlogWatcherButton from '$lib/components/MlogWatcherButton.svelte';

	let { data }: PageProps = $props();

	const theme = getThemeContext();
	const settings = getSettingsContext();
	const api = new ApiHandler();

	const mindcodeEditor = new InputEditorStore({
		api,
		theme,
		samples: untrack(() => data.samples),
		extensions: [mindcodeLanguage],
		settings
	});

	const outLanguage = new Compartment();
	const mlogEditor = new OutputEditorStore(
		theme,
		[outLanguage.of(mlogLanguageExtension)],
		settings
	);
	const channel = new MlogWatcherStore(() => settings.mlogWatcherPort);

	let runResults = $state<RunResult[]>([]);

	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);
	let loadingAction = $state<'compile' | 'compile-run' | null>(null);
	let isPlainText = $state(false);
	let editorLayout = $state<EditorLayout>();

	const compilerTarget = new LocalCompilerTarget();

	$effect(() => {
		if (!mlogEditor.view) return;
		const view = mlogEditor.view;

		const isCurrentlyPlainText = outLanguage.get(view.state) !== mlogLanguageExtension;
		if (isPlainText === isCurrentlyPlainText) return;

		view.dispatch({
			effects: outLanguage.reconfigure(isPlainText ? [] : mlogLanguageExtension)
		});
	});

	function handleJumpToPosition(range: SourceRange) {
		if (!mindcodeEditor.view) return;
		jumpToRange(mindcodeEditor.view, range);
	}

	async function compile(run: boolean) {
		if (!mindcodeEditor.view) return;
		const source = mindcodeEditor.view.state.doc.toString();

		loadingAction = run ? 'compile-run' : 'compile';
		runResults = [];
		errors = [];
		warnings = [];
		infos = [];
		updateEditor(mlogEditor.view, '');
		editorLayout?.ensureOutputIsVisible();

		try {
			const data = await api.compileMindcode({
				sourceId: mindcodeEditor.sourceId,
				source,
				target: compilerTarget.value,
				run
			});
			runResults = data.runResult ? [data.runResult] : [];
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			isPlainText = data.isPlainText;
			mindcodeEditor.setEditorId(data.sourceId);

			if (mlogEditor.view) {
				updateEditor(mlogEditor.view, data.compiled);
			}

			if (mindcodeEditor.view) {
				const { state } = mindcodeEditor.view;
				const diagnostics = compileMessagesToDiagnostics(state.doc, errors, warnings);
				mindcodeEditor.view.dispatch(setDiagnostics(state, diagnostics), {
					effects: updateDocId.of(data.sourceId)
				});
			}
		} catch (e) {
			console.error(e);
			runResults = [];
		} finally {
			loadingAction = null;
		}
	}

	async function selectSample(sample: Sample) {
		mindcodeEditor.selectSample(sample);
		await tick();
		await compile(sample.runnable);
	}

	async function cleanEditors() {
		mindcodeEditor.clear({ preserveUrl: true });
		compilerTarget.value = '7';
		updateEditor(mlogEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];
		runResults = [];

		await syncUrl({ sourceId: null, compilerTarget: compilerTarget.value });
	}
</script>

<svelte:head>
	<title>Mindcode: a Mindustry Logic high-level language</title>
</svelte:head>

<div class="container mx-auto flex flex-col gap-4 px-4 py-4">
	<!-- Control Bar (Desktop) -->
	<div class="hidden shrink-0 md:block">
		<ControlBar
			primaryActions={[
				{ label: 'Compile', onclick: () => compile(false), icon: Code },
				{ label: 'Compile and Run', onclick: () => compile(true), icon: Play }
			]}
			loading={mindcodeEditor.isLoading || loadingAction !== null}
		>
			<TargetPicker {compilerTarget} pickProcessor />
			<SamplePicker
				samples={data.samples}
				onSelect={selectSample}
				disabled={mindcodeEditor.isLoading || loadingAction !== null}
				selectedId={mindcodeEditor.sourceId}
			/>
		</ControlBar>
	</div>

	<!-- Mobile: Samples and Settings -->
	<div class="flex shrink-0 flex-wrap items-center gap-2 md:hidden">
		<TargetPicker {compilerTarget} pickProcessor />
		<SamplePicker
			samples={data.samples}
			onSelect={selectSample}
			disabled={mindcodeEditor.isLoading || loadingAction !== null}
			selectedId={mindcodeEditor.sourceId}
		/>
	</div>

	<!-- Editor Layout -->
	<EditorLayout
		bind:this={editorLayout}
		inputLabel="Mindcode Source Code"
		inputEditor={mindcodeEditor}
		inputLoading={mindcodeEditor.isLoading}
		outputEditor={mlogEditor}
		outputLoading={mindcodeEditor.isLoading || loadingAction !== null}
		{runResults}
		{errors}
		{warnings}
		{infos}
		onJumpToPosition={handleJumpToPosition}
	>
		{#snippet inputActions()}
			<EditorActionButton tooltip="Erase mindcode" onClick={cleanEditors}>
				<Trash2 class="size-4" />
			</EditorActionButton>
		{/snippet}
		{#snippet outputActions()}
			<MlogWatcherButton {channel} getText={() => mlogEditor.view?.state.doc.toString() ?? ''} />
		{/snippet}
	</EditorLayout>

	<!-- Bottom Action Bar (Mobile) -->
	<BottomActionBar
		primaryAction={{
			label: 'Compile',
			icon: Code,
			onclick: () => compile(false)
		}}
		secondaryAction={{
			label: 'Compile and Run',
			icon: Play,
			onclick: () => compile(true)
		}}
		loading={mindcodeEditor.isLoading || loadingAction !== null}
	/>
	<ProjectLinks />
</div>
