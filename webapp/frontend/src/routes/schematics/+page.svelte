<script lang="ts">
	import { EditorView } from 'codemirror';
	import { tick, untrack } from 'svelte';
	import { LoaderCircle } from '@lucide/svelte';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler, type CompileResponseMessage, type Sample, type SourceRange } from '$lib/api';
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
	import CopyButton from '$lib/components/CopyButton.svelte';

	let { data }: PageProps = $props();
	const api = new ApiHandler();

	const theme = getThemeContext();

	const schemacodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, schemacodeLanguage] });
	});
	const encodedEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, EditorView.lineWrapping] });
	});

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
			<div class="mb-2 w-full">
				<p class="mb-2 text-sm">
					Here you can compile a schematics definition written in <a
						href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown"
						class="text-primary underline">Schemacode</a
					>, a schema definition language, into a text representation to be pasted into Mindustry
					using the <strong>Import schematics...</strong> button. If your schematics contain
					processors, you can specify code for the processor using either Mindustry Logic or
					Mindcode. You can also decompile an existing schematics using the
					<a href="/decompiler" class="text-primary underline">Decompiler</a> and modify the resulting
					code.
				</p>
				<p class="text-sm font-semibold">Examples:</p>
			</div>
			{#each data.samples as sample}
				<Button
					variant="ghost"
					size="sm"
					class="h-auto px-2 py-1 text-primary underline"
					disabled={localSource.isLoading || loadingAction !== null}
					onclick={() => selectSample(sample)}
					>{sample.title}
				</Button>
			{/each}
		</Card.Content>
	</Card.Root>

	<div class="grid flex-1 grid-cols-1 gap-4 md:grid-cols-2">
		<!-- Source Editor -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Schemacode definition:</Label>
			<div
				class={[
					'h-[60vh] overflow-hidden rounded-md border bg-muted transition-opacity',
					localSource.isLoading && 'pointer-events-none opacity-50'
				]}
				{@attach schemacodeEditor.attach}
			></div>
		</div>

		<!-- Target Editor -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Encoded schematic:</Label>
			<div
				class={[
					'relative transition-opacity',
					(localSource.isLoading || loadingAction !== null) && 'pointer-events-none opacity-50'
				]}
			>
				<CopyButton getText={() => encodedEditor.view?.state.doc.toString() ?? ''} />
				<div
					class="h-[60vh] overflow-hidden rounded-md border bg-muted"
					{@attach encodedEditor.attach}
				></div>
			</div>
		</div>
	</div>

	<!-- Controls & Output -->
	<div class="grid shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<div class="w-55">
					<TargetPicker {compilerTarget} />
				</div>

				<Button
					onclick={() => handleBuild(false)}
					disabled={localSource.isLoading || loadingAction !== null}
				>
					{#if loadingAction === 'build'}<LoaderCircle class="mr-2 h-4 w-4 animate-spin" />{/if}
					Build
				</Button>
				<Button
					onclick={() => handleBuild(true)}
					disabled={localSource.isLoading || loadingAction !== null}
				>
					{#if loadingAction === 'build-run'}<LoaderCircle class="mr-2 h-4 w-4 animate-spin" />{/if}
					Build and Run
				</Button>
				<Button variant="outline" onclick={cleanEditors}>Start with a new schematic</Button>
			</div>

			<ProjectLinks variant="schemacode" />
		</div>

		<CompilerMessages
			{errors}
			{warnings}
			{infos}
			title="Build messages:"
			onJumpToPosition={handleJumpToPosition}
		/>
	</div>
</div>
