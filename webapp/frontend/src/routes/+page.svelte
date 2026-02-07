<script lang="ts">
	import { mlogLanguageExtension } from '$lib/grammars/mlog_language';
	import { setDiagnostics } from '@codemirror/lint';
	import { EditorView } from 'codemirror';
	import { untrack } from 'svelte';

	import { Button } from '$lib/components/ui/button';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler, type CompileResponseMessage, type SourceRange } from '$lib/api';
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
	import CopyButton from '$lib/components/CopyButton.svelte';
	import { Compartment } from '@codemirror/state';

	let { data }: PageProps = $props();

	const theme = getThemeContext();

	const mindcodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mindcodeLanguage] });
	});

	const mlogEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, outLanguage.of(mlogLanguageExtension)] });
	});

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
			runOutput = data.runResult?.output ?? '';
			runSteps = data.runResult?.steps ?? 0;
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			isPlainText = data.isPlainText;
			localSource.id = data.sourceId;

			if (mlogEditor.view) {
				const { state } = mlogEditor.view;
				const transaction = state.update({
					changes: { from: 0, to: state.doc.length, insert: data.compiled }
				});
				mlogEditor.view.dispatch(transaction);
			}

			if (mindcodeEditor.view) {
				const { state } = mindcodeEditor.view;
				const diagnostics = compileMessagesToDiagnostics(state.doc, errors, warnings);
				mindcodeEditor.view.dispatch(setDiagnostics(state, diagnostics));
			}

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
			runOutput = 'Error connecting to server.';
		} finally {
			loading = false;
		}
	}

	async function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
		updateEditor(mlogEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];
		runOutput = '';
		runSteps = 0;

		await syncUrl({ localSource, compilerTarget });
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
				class="h-[60vh] overflow-hidden rounded-md border bg-muted"
				{@attach mindcodeEditor.attach}
			></div>
		</div>

		<!-- Target Editor -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Mindustry Logic:</Label>
			<div class="relative">
				<CopyButton getText={() => mlogEditor.view?.state.doc.toString() ?? ''} />
				<div
					class="h-[60vh] overflow-hidden rounded-md border bg-muted"
					{@attach mlogEditor.attach}
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
