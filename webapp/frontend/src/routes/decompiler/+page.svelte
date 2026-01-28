<script lang="ts">
	import { mlogLanguage } from '$lib/grammars/mlog_language';
	import { keymap } from '@codemirror/view';
	import { insertTab } from '@codemirror/commands';
	import { basicSetup, EditorView } from 'codemirror';
	import { onMount } from 'svelte';
	import { abyss } from '@fsegurai/codemirror-theme-abyss';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { page } from '$app/state';
	import { ApiHandler } from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';

	const api = new ApiHandler();
	let mlogContainer = $state<HTMLElement>();
	let mindcodeContainer = $state<HTMLElement>();

	let mlogEditor = $state<EditorView>();
	let mindcodeEditor = $state<EditorView>();

	let loading = $state(false);
	let errors = $state<any[]>([]);
	let warnings = $state<any[]>([]);
	let messages = $state<any[]>([]);

	$effect(() => {
		const s = page.url.searchParams.get('s');
		if (s) {
			loadSource(s);
		}
	});

	onMount(() => {
		if (mlogContainer) {
			mlogEditor = new EditorView({
				parent: mlogContainer,
				extensions: [commonExtensions(), mlogLanguage]
			});
		}
		if (mindcodeContainer) {
			mindcodeEditor = new EditorView({
				parent: mindcodeContainer,
				extensions: [commonExtensions(), mindcodeLanguage]
			});
		}
		const s = page.url.searchParams.get('s');
		if (s) {
			loadSource(s);
		}
	});

	async function loadSource(id: string) {
		if (id === 'clean' || !id) {
			updateEditor(mlogEditor, '');
			updateEditor(mindcodeEditor, '');
			return;
		}
		try {
			const data = await api.loadSource(id);
			updateEditor(mlogEditor, data.source);
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

	async function handleDecompile() {
		if (!mlogEditor) return;
		const source = mlogEditor.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		messages = [];
		try {
			const data = await api.decompileMlog(source);
			if (mindcodeEditor) {
				updateEditor(mindcodeEditor, data.source);
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
</script>

<div class="container mx-auto flex flex-1 flex-col gap-4 overflow-hidden px-4 py-4">
	<Card.Root class="shrink-0 border-dashed bg-muted/50">
		<Card.Content class="p-4 text-sm">
			<p>
				Here you can partially decompile an mlog code into Mindcode. <strong
					>The resulting code cannot be directly compiled by Mindcode.</strong
				>
				Jump instructions in the original mlog are transcribed as <strong>if</strong>
				and <strong>goto</strong> statements which aren't supported by Mindcode and must be rewritten
				using conditional statements, loops and other high-level constructs. The decompiler is mainly
				useful to produce expressions and function calls in the correct Mindcode syntax, saving some time
				and possibly helping to avoid some mistakes compared to a manual rewrite of the entire mlog code
				from scratch.
			</p>
		</Card.Content>
	</Card.Root>

	<div class="grid flex-1 grid-cols-1 gap-4 md:grid-cols-2">
		<!-- Source Editor (Mlog) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Mlog code:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={mlogContainer}
			></div>
		</div>

		<!-- Target Editor (Mindcode) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Decompiled Mindcode:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={mindcodeContainer}
			></div>
		</div>
	</div>

	<!-- Controls -->
	<div class="grid max-h-[20vh] shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<Button onclick={handleDecompile} disabled={loading}>Decompile</Button>
				<Button variant="outline" href="/mlog-decompiler">Erase mlog</Button>
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

		<!-- Compiler/Decompiler messages placeholder -->
		<div class="flex flex-col gap-2">
			<CompilerMessages {errors} {warnings} {messages} title="Decompiler messages:" />
		</div>
	</div>
</div>
