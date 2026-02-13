<script lang="ts">
	import { mlogLanguageExtension } from '$lib/grammars/mlog_language';
	import { setDiagnostics } from '@codemirror/lint';
	import { EditorView } from 'codemirror';
	import { tick, untrack } from 'svelte';
	import { Play, Code, Cpu, Trash2 } from '@lucide/svelte';

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
	import { compileMessagesToDiagnostics, jumpToRange, updateEditor } from '$lib/codemirror';
	import {
		LocalSource,
		LocalCompilerTarget,
		syncUrl,
		EditorStore,
		getThemeContext
	} from '$lib/stores.svelte';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import { Compartment } from '@codemirror/state';
	import SamplePicker from '$lib/components/SamplePicker.svelte';
	import EditorActionButton from '$lib/components/EditorActionButton.svelte';

	let { data }: PageProps = $props();

	const theme = getThemeContext();

	const mindcodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mindcodeLanguage] });
	});

	const mlogEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({
			parent,
			extensions: [baseExtensions, outLanguage.of(mlogLanguageExtension)]
		});
	});

	let runResults = $state<RunResult[]>([]);

	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);
	let loadingAction = $state<'compile' | 'compile-run' | null>(null);
	let isPlainText = $state(false);
	const outLanguage = new Compartment();

	const api = new ApiHandler();
	const localSource = new LocalSource(
		api,
		() => mindcodeEditor.view,
		untrack(() => data.samples)
	);
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

		try {
			const data = await api.compileMindcode({
				sourceId: localSource.id,
				source,
				target: compilerTarget.value,
				run
			});
			runResults = data.runResult ? [data.runResult] : [];
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			isPlainText = data.isPlainText;
			localSource.id = data.sourceId;

			if (mlogEditor.view) {
				updateEditor(mlogEditor.view, data.compiled);
			}

			if (mindcodeEditor.view) {
				const { state } = mindcodeEditor.view;
				const diagnostics = compileMessagesToDiagnostics(state.doc, errors, warnings);
				mindcodeEditor.view.dispatch(setDiagnostics(state, diagnostics));
			}

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
			runResults = [];
		} finally {
			loadingAction = null;
		}
	}

	async function selectSample(sample: Sample) {
		localSource.selectSample(sample);
		await tick();
		await compile(false);
	}

	async function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
		updateEditor(mlogEditor.view, '');
		updateEditor(mindcodeEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];
		runResults = [];

		await syncUrl({ localSource, compilerTarget });
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
			loading={localSource.isLoading || loadingAction !== null}
		>
			<TargetPicker {compilerTarget} />
			<SamplePicker
				samples={data.samples}
				onSelect={selectSample}
				disabled={localSource.isLoading || loadingAction !== null}
			/>
		</ControlBar>
	</div>

	<!-- Mobile: Samples and Settings -->
	<div class="flex shrink-0 flex-wrap items-center gap-2 md:hidden">
		<TargetPicker {compilerTarget} />
		<SamplePicker
			samples={data.samples}
			onSelect={selectSample}
			disabled={localSource.isLoading || loadingAction !== null}
		/>
	</div>

	<!-- Editor Layout -->
	<EditorLayout
		inputLabel="Mindcode Source Code"
		inputEditor={mindcodeEditor}
		inputLoading={localSource.isLoading}
		outputEditor={mlogEditor}
		outputLoading={localSource.isLoading || loadingAction !== null}
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
			<EditorActionButton tooltip="Send to MlogWatcher">
				<Cpu class="size-4" />
			</EditorActionButton>
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
		loading={localSource.isLoading || loadingAction !== null}
	/>
	<ProjectLinks />
</div>
