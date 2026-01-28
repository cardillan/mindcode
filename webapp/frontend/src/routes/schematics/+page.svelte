<script lang="ts">
	import { keymap } from '@codemirror/view';
	import { insertTab } from '@codemirror/commands';
	import { basicSetup, EditorView } from 'codemirror';
	import { onMount } from 'svelte';
	import { abyss } from '@fsegurai/codemirror-theme-abyss';

	import { Button } from '$lib/components/ui/button';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import * as Select from '$lib/components/ui/select';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { page } from '$app/state';
	import { ApiHandler, type CompileResponseMessage, type Sample } from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();
	const api = new ApiHandler();
	let schemacodeContainer = $state<HTMLElement>();
	let encodedContainer = $state<HTMLElement>();

	let schemacodeEditor = $state<EditorView>();
	let encodedEditor = $state<EditorView>();

	let compilerTarget = $state('8');

	let loading = $state(false);
	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let messages = $state<CompileResponseMessage[]>([]);

	$effect(() => {
		const s = page.url.searchParams.get('s');
		if (s) {
			loadSource(s);
		}
	});

	onMount(async () => {
		if (schemacodeContainer) {
			schemacodeEditor = new EditorView({
				parent: schemacodeContainer,
				extensions: [commonExtensions(), schemacodeLanguage]
			});
		}
		if (encodedContainer) {
			encodedEditor = new EditorView({
				parent: encodedContainer,
				extensions: [commonExtensions(), EditorView.lineWrapping]
			});
		}
		const s = page.url.searchParams.get('s');
		if (s) {
			loadSource(s);
		}
	});

	async function loadSource(id: string) {
		if (id === 'clean' || !id) {
			updateEditor(schemacodeEditor, '');
			updateEditor(encodedEditor, '');
			return;
		}
		try {
			const data = await api.loadSource(id);
			updateEditor(schemacodeEditor, data.source);
		} catch (e) {
			console.error(e);
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

	function selectSample(sample: Sample) {
		updateEditor(schemacodeEditor, sample.source);
	}

	async function handleBuild() {
		if (!schemacodeEditor) return;
		const source = schemacodeEditor.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		messages = [];

		try {
			const data = await api.compileSchemacode({
				source,
				target: compilerTarget
			});

			if (encodedEditor) {
				updateEditor(encodedEditor, data.compiledCode);
			}

			errors = data.errors;
			warnings = data.warnings;
			messages = data.messages;
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
					>{sample.name}
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

				<Button onclick={handleBuild} disabled={loading}>Build</Button>
				<Button variant="outline" href="/schematics?s=clean">Start with a new schematic</Button>
			</div>

			<div class="flex flex-wrap gap-2 text-xs text-muted-foreground">
				<a
					href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown"
					class="underline hover:text-primary">Schemacode syntax</a
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
			<CompilerMessages {errors} {warnings} {messages} title="Build messages:" />
		</div>
	</div>
</div>
