<script lang="ts">
	import { mlogLanguage } from '$lib/grammars/mlog_language';
	import { setDiagnostics } from '@codemirror/lint';
	import { EditorView } from 'codemirror';
	import { onMount, untrack } from 'svelte';
	import { page } from '$app/state';

	import { Button } from '$lib/components/ui/button';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler, type CompileResponseMessage, type SourceRange } from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';
	import type { PageProps } from './$types';
	import { baseExtensions, compileMessagesToDiagnostics, jumpToRange } from '$lib/codemirror';
	import { LocalSource, LocalCompilerTarget, syncUrl } from '$lib/hooks.svelte';
	import { goto } from '$app/navigation';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import { Compartment } from '@codemirror/state';
	import { OutliningSpanKind } from 'typescript';

	let { data }: PageProps = $props();
	let mindcodeContainer = $state<HTMLElement>();
	let mlogContainer = $state<HTMLElement>();

	let mindcodeEditor = $state<EditorView>();
	let mlogEditor = $state<EditorView>();

	let runOutput = $state('');
	let runSteps = $state(0);

	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);
	let loading = $state(false);
	let isPlainText = $state(false);
	const outLanguage = new Compartment();

	const api = new ApiHandler();
	const localSource = new LocalSource(
		api,
		() => mindcodeEditor,
		untrack(() => data.samples)
	);
	const compilerTarget = new LocalCompilerTarget();

	onMount(() => {
		if (mindcodeContainer) {
			mindcodeEditor = new EditorView({
				parent: mindcodeContainer,
				extensions: [baseExtensions(), mindcodeLanguage]
			});
		}
		if (mlogContainer) {
			mlogEditor = new EditorView({
				parent: mlogContainer,
				extensions: [baseExtensions(), outLanguage.of(mlogLanguage)]
			});
		}

		return () => {
			mindcodeEditor?.destroy();
			mlogEditor?.destroy();
		};
	});

	$effect(() => {
		if (!mlogEditor) return;

		const isCurrentlyPlainText = outLanguage.get(mlogEditor.state) !== mlogLanguage;
		if (isPlainText === isCurrentlyPlainText) return;

		mlogEditor.dispatch({
			effects: outLanguage.reconfigure(isPlainText ? [] : mlogLanguage)
		});
	});

	function handleJumpToPosition(range: SourceRange) {
		if (!mindcodeEditor) return;

		jumpToRange(mindcodeEditor, range);
	}

	async function compile(run: boolean) {
		if (!mindcodeEditor) return;
		const source = mindcodeEditor.state.doc.toString();

		loading = true;
		runOutput = '';
		runSteps = 0;
		errors = [];
		warnings = [];
		infos = [];

		try {
			const data = await api.compileMindcode({
				sourceId: localSource.id,
				source,
				target: compilerTarget.value,
				run
			});
			runOutput = data.runOutput;
			runSteps = data.runSteps;
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			isPlainText = data.isPlainText;
			localSource.id = data.sourceId;

			if (mlogEditor) {
				const transaction = mlogEditor.state.update({
					changes: { from: 0, to: mlogEditor.state.doc.length, insert: data.compiled }
				});
				mlogEditor.dispatch(transaction);
			}

			if (mindcodeEditor) {
				const { state } = mindcodeEditor;
				const diagnostics = compileMessagesToDiagnostics(state.doc, errors, warnings);
				mindcodeEditor.dispatch(setDiagnostics(state, diagnostics));
			}

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
			runOutput = 'Error connecting to server.';
		} finally {
			loading = false;
		}
	}

	function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
		errors = [];
		warnings = [];
		infos = [];
		runOutput = '';

		const url = new URL(page.url);
		localSource.updateParams(url.searchParams);
		compilerTarget.updateParams(url.searchParams);
		goto(url, { replaceState: true, noScroll: true, keepFocus: true });
	}
</script>

<div class="container mx-auto flex flex-col gap-4 overflow-hidden px-4 py-4">
	<!-- Samples -->
	<Card.Root class="shrink-0">
		<Card.Content class="flex flex-wrap items-center gap-2 p-4">
			<span class="text-sm font-semibold">Examples:</span>
			{#each data.samples as sample}
				<Button
					variant="ghost"
					size="sm"
					class="h-auto px-2 py-1 text-primary underline"
					onclick={() => localSource.selectSample(sample)}>{sample.title}</Button
				>
			{/each}
		</Card.Content>
	</Card.Root>

	<div class="grid flex-1 grid-cols-1 gap-4 md:grid-cols-2">
		<!-- Source Editor -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Mindcode Source Code:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={mindcodeContainer}
			></div>
		</div>

		<!-- Target Editor -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Mindustry Logic:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={mlogContainer}
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

				<Button onclick={() => compile(false)} disabled={loading}>Compile</Button>
				<Button onclick={() => compile(true)} disabled={loading}>Compile and Run</Button>
				<Button variant="outline" onclick={cleanEditors}>Start with a new script</Button>
			</div>

			<ProjectLinks />
		</div>

		<div class="flex flex-col gap-2">
			{#if runOutput}
				<Label for="output">Program output ({runSteps} steps):</Label>
				<Textarea id="output" readonly value={runOutput} rows={4} class="bg-muted font-mono" />
			{/if}

			<CompilerMessages
				{errors}
				{warnings}
				{infos}
				title="Compiler messages:"
				onJumpToPosition={handleJumpToPosition}
			/>
		</div>
	</div>
</div>
