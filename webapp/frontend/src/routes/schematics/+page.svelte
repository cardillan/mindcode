<script lang="ts">
	import { EditorView } from 'codemirror';
	import { tick, untrack } from 'svelte';
	import { Code, Play } from '@lucide/svelte';

	import * as Card from '$lib/components/ui/card';
	import {
		ApiHandler,
		type CompileResponseMessage,
		type RunResult,
		type Sample,
		type SourceRange
	} from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';
	import type { PageProps } from './$types';
	import { setDiagnostics } from '@codemirror/lint';
	import { compileMessagesToDiagnostics, jumpToRange, updateEditor } from '$lib/codemirror';
	import {
		EditorStore,
		getThemeContext,
		LocalCompilerTarget,
		LocalSource,
		syncUrl
	} from '$lib/stores.svelte';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import ControlBar from '$lib/components/ControlBar.svelte';
	import SamplePicker from '$lib/components/SamplePicker.svelte';
	import EditorLayout from '$lib/components/EditorLayout.svelte';
	import BottomActionBar from '$lib/components/BottomActionBar.svelte';
	import { Button } from '$lib/components/ui/button';

	let { data }: PageProps = $props();
	const api = new ApiHandler();

	const theme = getThemeContext();

	const schemacodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, schemacodeLanguage] });
	});
	const encodedEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, EditorView.lineWrapping] });
	});

	let runResults = $state<RunResult[]>([]);
	let loadingAction = $state<'build' | 'build-run' | null>(null);
	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);
	const localSource = new LocalSource(
		api,
		() => schemacodeEditor.view,
		untrack(() => data.samples)
	);
	const compilerTarget = new LocalCompilerTarget();

	function handleJumpToPosition(range: SourceRange) {
		if (!schemacodeEditor.view) return;

		jumpToRange(schemacodeEditor.view, range);
	}

	async function handleBuild(run: boolean) {
		if (!schemacodeEditor.view) return;
		const source = schemacodeEditor.view.state.doc.toString();
		loadingAction = run ? 'build-run' : 'build';
		errors = [];
		warnings = [];
		infos = [];
		updateEditor(encodedEditor.view, '');

		try {
			const data = await api.compileSchemacode({
				sourceId: localSource.id,
				source,
				target: compilerTarget.value,
				run
			});
			runResults = data.runResults;
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			localSource.id = data.sourceId;

			if (encodedEditor.view) {
				updateEditor(encodedEditor.view, data.compiled);
			}

			if (schemacodeEditor.view) {
				const { state } = schemacodeEditor.view;
				const diagnostics = compileMessagesToDiagnostics(state.doc, errors, warnings);

				schemacodeEditor.view.dispatch(setDiagnostics(state, diagnostics));
			}

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
		} finally {
			loadingAction = null;
		}
	}

	async function selectSample(sample: Sample) {
		localSource.selectSample(sample);
		await tick();
		await handleBuild(false);
	}

	async function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
		updateEditor(encodedEditor.view, '');
		updateEditor(schemacodeEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];

		await syncUrl({ localSource, compilerTarget });
	}
</script>

<div class="container mx-auto flex flex-1 flex-col gap-4 overflow-hidden px-4 py-4">
	<!-- Samples -->
	<Card.Root class="shrink-0">
		<Card.Content class="flex flex-wrap items-center gap-2 p-4">
			<p class="text-sm">
				Here you can compile a schematics definition written in <a
					href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown"
					class="text-primary underline">Schemacode</a
				>, a schema definition language, into a text representation to be pasted into Mindustry
				using the <strong>Import schematics...</strong> button. If your schematics contain
				processors, you can specify code for the processor using either Mindustry Logic or Mindcode.
				You can also decompile an existing schematics using the
				<a href="/decompiler" class="text-primary underline">Decompiler</a> and modify the resulting code.
			</p>
		</Card.Content>
	</Card.Root>

	<!-- Control Bar (Desktop) -->
	<div class="hidden shrink-0 md:block">
		<ControlBar
			primaryActions={[
				{ label: 'Build', onclick: () => handleBuild(false), icon: Code },
				{ label: 'Build and Run', onclick: () => handleBuild(true), icon: Play }
			]}
			secondaryActions={[{ label: 'Erase schemacode', onclick: cleanEditors, variant: 'outline' }]}
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
		<div class="flex-1"></div>
		<Button
			variant="outline"
			onclick={cleanEditors}
			disabled={localSource.isLoading || loadingAction !== null}
		>
			Erase schemacode
		</Button>
	</div>

	<EditorLayout
		inputLabel="Schemacode definition"
		inputEditor={schemacodeEditor}
		inputLoading={localSource.isLoading}
		outputEditor={encodedEditor}
		outputLoading={localSource.isLoading || loadingAction !== null}
		{runResults}
		{errors}
		{warnings}
		{infos}
		onJumpToPosition={handleJumpToPosition}
	/>

	<!-- Bottom Action Bar (Mobile) -->
	<BottomActionBar
		primaryAction={{
			label: 'Build',
			icon: Code,
			onclick: () => handleBuild(false)
		}}
		secondaryAction={{
			label: 'Build and Run',
			icon: Play,
			onclick: () => handleBuild(true)
		}}
		loading={localSource.isLoading || loadingAction !== null}
	/>
	<ProjectLinks variant="schemacode" />
</div>
