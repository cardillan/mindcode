<script lang="ts">
	import { EditorView } from 'codemirror';
	import { onMount, untrack } from 'svelte';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import * as Select from '$lib/components/ui/select';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { page } from '$app/state';
	import { ApiHandler, type CompileResponseMessage, type Sample } from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';
	import type { PageProps } from './$types';
	import { setDiagnostics } from '@codemirror/lint';
	import { baseExtensions, compileMessagesToDiagnostics, updateEditor } from '$lib/codemirror';
	import { LocalCompilerTarget, LocalSource, syncUrl } from '$lib/hooks.svelte';
	import { goto } from '$app/navigation';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';

	let { data }: PageProps = $props();
	const api = new ApiHandler();

	let schemacodeContainer = $state<HTMLElement>();
	let encodedContainer = $state<HTMLElement>();

	let schemacodeEditor = $state<EditorView>();
	let encodedEditor = $state<EditorView>();

	let loading = $state(false);
	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);
	const localSource = new LocalSource(
		api,
		() => schemacodeEditor,
		untrack(() => data.samples)
	);
	const compilerTarget = new LocalCompilerTarget();

	onMount(() => {
		if (schemacodeContainer) {
			schemacodeEditor = new EditorView({
				parent: schemacodeContainer,
				extensions: [baseExtensions(), schemacodeLanguage]
			});
		}
		if (encodedContainer) {
			encodedEditor = new EditorView({
				parent: encodedContainer,
				extensions: [baseExtensions(), EditorView.lineWrapping]
			});
		}

		return () => {
			schemacodeEditor?.destroy();
			encodedEditor?.destroy();
		};
	});

	function selectSample(sample: Sample) {
		updateEditor(schemacodeEditor, sample.source);
	}

	async function handleBuild() {
		if (!schemacodeEditor) return;
		const source = schemacodeEditor.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		infos = [];

		try {
			const data = await api.compileSchemacode({
				sourceId: localSource.id,
				source,
				target: compilerTarget.value
			});
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			localSource.id = data.sourceId;

			if (encodedEditor) {
				updateEditor(encodedEditor, data.compiled);
			}

			if (schemacodeEditor) {
				const { state } = schemacodeEditor;
				const diagnostics = compileMessagesToDiagnostics(state.doc, errors, warnings);

				schemacodeEditor.dispatch(setDiagnostics(state, diagnostics));
			}

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
		} finally {
			loading = false;
		}
	}

	const targetOptions = {
		'6': 'Target: Mindustry 6',
		'7': 'Target: Mindustry 7',
		'7w': 'Target: Mindustry 7 WP',
		'8': 'Target: Mindustry 8',
		'8w': 'Target: Mindustry 8 WP'
	};

	async function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
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
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={schemacodeContainer}
			></div>
		</div>

		<!-- Target Editor -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Encoded schematic:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={encodedContainer}
			></div>
		</div>
	</div>

	<!-- Controls & Output -->
	<div class="grid shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<div class="w-55">
					<TargetPicker {compilerTarget} />
				</div>

				<Button onclick={handleBuild} disabled={loading}>Build</Button>
				<Button variant="outline" onclick={cleanEditors}>Start with a new schematic</Button>
			</div>

			<ProjectLinks variant="schemacode" />
		</div>

		<CompilerMessages {errors} {warnings} {infos} title="Build messages:" />
	</div>
</div>
