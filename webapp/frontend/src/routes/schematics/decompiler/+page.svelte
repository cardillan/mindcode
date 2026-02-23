<script lang="ts">
	import { EditorView } from 'codemirror';
	import { Code, Play, Trash2 } from '@lucide/svelte';
	import * as Card from '$lib/components/ui/card';
	import {
		ApiHandler,
		type CompileResponseMessage,
		type SourceRange,
		type RunResult
	} from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';
	import { getThemeContext, LocalCompilerTarget, syncUrl } from '$lib/stores.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import { jumpToRange, updateEditor } from '$lib/codemirror';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import ControlBar from '$lib/components/ControlBar.svelte';
	import BottomActionBar from '$lib/components/BottomActionBar.svelte';
	import EditorLayout from '$lib/components/EditorLayout.svelte';
	import EditorActionButton from '$lib/components/EditorActionButton.svelte';
	import { InputEditorStore, OutputEditorStore } from '$lib/editors.svelte';

	const theme = getThemeContext();

	const api = new ApiHandler();
	const encodedEditor = new InputEditorStore({
		theme,
		api,
		createEditor(baseExtensions) {
			return new EditorView({ extensions: [baseExtensions, EditorView.lineWrapping] });
		}
	});
	const schemacodeEditor = new OutputEditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, schemacodeLanguage] });
	});

	let runResults = $state<RunResult[]>([]);
	let loadingAction = $state<'decompile' | 'decompile-run' | null>(null);
	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);

	const compilerTarget = new LocalCompilerTarget();

	function handleJumpToPosition(range: SourceRange) {
		if (!schemacodeEditor.view) return;

		jumpToRange(schemacodeEditor.view, range);
	}

	async function handleDecompile(run: boolean) {
		if (!encodedEditor.view) return;
		const source = encodedEditor.view.state.doc.toString();
		runResults = [];
		loadingAction = run ? 'decompile-run' : 'decompile';
		errors = [];
		warnings = [];
		infos = [];
		updateEditor(schemacodeEditor.view, '');

		try {
			const data = await api.decompileSchematic({
				sourceId: encodedEditor.sourceId,
				source,
				run,
				target: compilerTarget.value
			});
			runResults = data.runResults;

			if (schemacodeEditor.view) {
				updateEditor(schemacodeEditor.view, data.source);
			}
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			encodedEditor.setEditorId(data.sourceId);

			await syncUrl({ sourceId: encodedEditor.sourceId, compilerTarget: compilerTarget.value });
		} catch (e) {
			console.error(e);
			runResults = [];
		} finally {
			loadingAction = null;
		}
	}

	async function cleanEditors() {
		encodedEditor.clear({ preserveUrl: true });
		compilerTarget.value = '7';
		updateEditor(schemacodeEditor.view, '');
		updateEditor(encodedEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];
		runResults = [];

		await syncUrl({ sourceId: encodedEditor.sourceId, compilerTarget: compilerTarget.value });
	}
</script>

<svelte:head>
	<title>Mindcode: Schematics decompiler</title>
</svelte:head>

<div class="container mx-auto flex flex-col gap-4 overflow-hidden px-4 py-4">
	<Card.Root class="shrink-0 border-dashed bg-muted/50">
		<Card.Content class="p-4 text-sm">
			<p class="mb-2">
				Here you can decompile schematics copied from Mindustry into Schemacode, a schema definition
				language, which can then be modified and compiled back into a schema again. Press the <strong
					>Export</strong
				>
				button on a Mindustry schematic, choose <strong>Copy to clipboard</strong> and paste the
				text into the left pane. Then press <strong>Decompile</strong>. You can compile the
				Schematic definition back to Mindustry schematics on the
				<a href="/schematics" class="text-primary underline">Schematics Builder</a> page.
			</p>
			<p>If your schematic contains processor(s), the code is decompiled into mlog.</p>
		</Card.Content>
	</Card.Root>

	<!-- Control Bar (Desktop) -->
	<div class="hidden shrink-0 md:block">
		<ControlBar
			primaryActions={[
				{ label: 'Decompile', onclick: () => handleDecompile(false), icon: Code },
				{ label: 'Decompile and Run', onclick: () => handleDecompile(true), icon: Play }
			]}
			loading={encodedEditor.isLoading || loadingAction !== null}
		>
			<TargetPicker {compilerTarget} />
		</ControlBar>
	</div>

	<!-- Mobile: Samples and Settings -->
	<div class="flex shrink-0 flex-wrap items-center gap-2 md:hidden">
		<TargetPicker {compilerTarget} />
	</div>

	<!-- Editor Layout -->
	<EditorLayout
		inputLabel="Encoded schematic"
		inputEditor={encodedEditor}
		inputLoading={encodedEditor.isLoading}
		outputEditor={schemacodeEditor}
		outputLoading={encodedEditor.isLoading || loadingAction !== null}
		{runResults}
		{errors}
		{warnings}
		{infos}
		onJumpToPosition={handleJumpToPosition}
	>
		{#snippet inputActions()}
			<EditorActionButton tooltip="Erase schematic" onClick={cleanEditors}>
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
		loading={encodedEditor.isLoading || loadingAction !== null}
	/>
	<ProjectLinks variant="schemacode" />
</div>
