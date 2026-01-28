<script lang="ts">
	import { mlogLanguage } from '$lib/grammars/mlog_language';
	import { keymap } from '@codemirror/view';
	import { insertTab } from '@codemirror/commands';
	import { EditorSelection, SelectionRange, StateEffect, Text } from '@codemirror/state';
	import { setDiagnostics, type Diagnostic } from '@codemirror/lint';
	import { basicSetup, EditorView } from 'codemirror';
	import { onMount } from 'svelte';
	import { abyss } from '@fsegurai/codemirror-theme-abyss';
	import { page } from '$app/state';

	import { Button } from '$lib/components/ui/button';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import * as Select from '$lib/components/ui/select';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import {
		ApiHandler,
		type CompileResponse,
		type CompileResponseMessage,
		type Sample,
		type SourceRange
	} from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();
	let mindcodeContainer = $state<HTMLElement>();
	let mlogContainer = $state<HTMLElement>();

	let mindcodeEditor = $state<EditorView>();
	let mlogEditor = $state<EditorView>();

	let compilerTarget = $state('8');
	let runOutput = $state('');
	let runSteps = $state(0);

	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let messages = $state<CompileResponseMessage[]>([]);
	let loading = $state(false);
	const api = new ApiHandler();

	$effect(() => {
		const s = page.url.searchParams.get('s');
		if (s) {
			loadSource(s);
		}
	});

	onMount(() => {
		if (mindcodeContainer) {
			mindcodeEditor = new EditorView({
				parent: mindcodeContainer,
				extensions: [commonExtensions(), mindcodeLanguage]
			});
		}
		if (mlogContainer) {
			mlogEditor = new EditorView({
				parent: mlogContainer,
				extensions: [commonExtensions(), mlogLanguage]
			});
		}

		// Initial load if s present
		const s = page.url.searchParams.get('s');
		if (s) {
			loadSource(s);
		}
	});

	async function loadSource(id: string) {
		if (id === 'clean') {
			updateEditor(mindcodeEditor, '');
			updateEditor(mlogEditor, '');
			errors = [];
			warnings = [];
			messages = [];
			runOutput = '';
			return;
		}

		try {
			const data = await api.loadSource(id);
			updateEditor(mindcodeEditor, data.source);
		} catch (e) {
			console.error('Failed to load source', e);
		}
	}

	function updateEditor(editor: EditorView | undefined, text: string) {
		if (editor) {
			const transaction = editor.state.update({
				changes: { from: 0, to: editor.state.doc.length, insert: text }
			});
			editor.dispatch(transaction);
		}
	}

	function commonExtensions() {
		return [
			basicSetup,
			abyss,
			keymap.of([{ key: 'Tab', run: insertTab }]),
			EditorView.theme({
				'&': {
					height: '100%',
					width: '100%',
					fontSize: '14px'
				},
				'.cm-scroller': {
					fontFamily: 'monospace'
				}
			})
		];
	}

	function handleCompile() {
		compile(false);
	}

	function handleCompileAndRun() {
		compile(true);
	}

	function handleJumpToPosition(range: SourceRange) {
		if (!mindcodeEditor) return;

		const line = mindcodeEditor.state.doc.line(range.startLine);
		const pos = line.from + range.startColumn - 1;

		mindcodeEditor.dispatch({
			selection: EditorSelection.single(pos),
			scrollIntoView: true
		});
	}

	async function compile(run: boolean) {
		if (!mindcodeEditor) return;
		const source = mindcodeEditor.state.doc.toString();

		loading = true;
		runOutput = '';
		runSteps = 0;
		errors = [];
		warnings = [];
		messages = [];

		try {
			const data = await api.compileMindcode({
				source,
				target: compilerTarget,
				run
			});
			runOutput = data.runOutput;
			runSteps = data.runSteps;
			errors = data.errors;
			warnings = data.warnings;
			messages = data.messages;

			if (mlogEditor) {
				const transaction = mlogEditor.state.update({
					changes: { from: 0, to: mlogEditor.state.doc.length, insert: data.compiledCode || '' }
				});
				mlogEditor.dispatch(transaction);
			}

			if (mindcodeEditor) {
				const diagnostics: Diagnostic[] = [];
				const { doc } = mindcodeEditor.state;
				for (const err of errors) {
					diagnostics.push({
						from: err.range ? posToOffset(doc, err.range.startLine, err.range.startColumn) : 0,
						to: err.range ? posToOffset(doc, err.range.endLine, err.range.endColumn) : 0,
						message: err.message,
						severity: 'error'
					});
				}
				for (const warn of warnings) {
					diagnostics.push({
						from: warn.range ? posToOffset(doc, warn.range.startLine, warn.range.startColumn) : 0,
						to: warn.range ? posToOffset(doc, warn.range.endLine, warn.range.endColumn) : 0,
						message: warn.message,
						severity: 'warning'
					});
				}
				mindcodeEditor.dispatch(setDiagnostics(mindcodeEditor.state, diagnostics));
			}
		} catch (e) {
			console.error(e);
			runOutput = 'Error connecting to server.';
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

	function posToOffset(doc: Text, line: number, column: number): number {
		const lineInfo = doc.line(line);
		return lineInfo.from + column - 1;
	}

	function selectSample(sample: Sample) {
		updateEditor(mindcodeEditor, sample.source);
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
					onclick={() => selectSample(sample)}>{sample.name}</Button
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
	<div class="grid max-h-[40vh] shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<div class="w-55">
					<Select.Root type="single" bind:value={compilerTarget}>
						<Select.Trigger>
							{targetOptions[compilerTarget as keyof typeof targetOptions] || 'Select Target'}
						</Select.Trigger>
						<Select.Content>
							{#each Object.entries(targetOptions) as [key, label]}
								<Select.Item value={key}>{label}</Select.Item>
							{/each}
						</Select.Content>
					</Select.Root>
				</div>

				<Button onclick={handleCompile} disabled={loading}>Compile</Button>
				<Button onclick={handleCompileAndRun} disabled={loading}>Compile and Run</Button>
				<Button variant="outline" href="/?s=clean">Start with a new script</Button>
			</div>

			<div class="flex flex-wrap gap-2 text-xs text-muted-foreground">
				<a
					href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SYNTAX.markdown"
					class="underline hover:text-primary">Mindcode syntax</a
				>
				|
				<a
					href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SYSTEM-LIBRARY.markdown"
					class="underline hover:text-primary">System library</a
				>
				|
				<a
					href="https://github.com/cardillan/mindcode/blob/main/README.markdown"
					class="underline hover:text-primary">Readme</a
				>
				|
				<a
					href="https://github.com/cardillan/mindcode/blob/main/CHANGELOG.markdown"
					class="underline hover:text-primary">Changelog</a
				>
			</div>

			<div class="text-xs text-muted-foreground">
				Bug reports, suggestions and questions are welcome at the <a
					class="underline hover:text-primary"
					href="https://github.com/cardillan/mindcode">project page</a
				>.
			</div>
		</div>

		<div class="flex flex-col gap-2">
			{#if runOutput}
				<Label for="output">Program output ({runSteps} steps):</Label>
				<Textarea id="output" readonly value={runOutput} rows={4} class="bg-muted font-mono" />
			{/if}

			<CompilerMessages
				{errors}
				{warnings}
				{messages}
				title="Compiler messages:"
				onJumpToPosition={handleJumpToPosition}
			/>
		</div>
	</div>
</div>
