<script lang="ts">
	import { keymap } from '@codemirror/view';
	import { insertTab } from '@codemirror/commands';
	import { basicSetup, EditorView } from 'codemirror';
	import { onMount } from 'svelte';
	import { abyss } from '@fsegurai/codemirror-theme-abyss';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler } from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';

	const api = new ApiHandler();
	let encodedContainer = $state<HTMLElement>();
	let schemacodeContainer = $state<HTMLElement>();

	let encodedEditor = $state<EditorView>();
	let schemacodeEditor = $state<EditorView>();

	let loading = $state(false);
	let errors = $state<any[]>([]);
	let warnings = $state<any[]>([]);
	let messages = $state<any[]>([]);

	onMount(() => {
		if (encodedContainer) {
			encodedEditor = new EditorView({
				parent: encodedContainer,
				extensions: [commonExtensions()]
			});
		}
		if (schemacodeContainer) {
			schemacodeEditor = new EditorView({
				parent: schemacodeContainer,
				extensions: [commonExtensions(), schemacodeLanguage]
			});
		}
	});

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
		if (!encodedEditor) return;
		const source = encodedEditor.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		messages = [];

		try {
			const data = await api.decompileSchematic(source);

			if (schemacodeEditor) {
				const transaction = schemacodeEditor.state.update({
					changes: { from: 0, to: schemacodeEditor.state.doc.length, insert: data.source }
				});
				schemacodeEditor.dispatch(transaction);
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

	<div class="grid flex-1 grid-cols-1 gap-4 md:grid-cols-2">
		<!-- Source Editor (Encoded) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Encoded schematic:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={encodedContainer}
			></div>
		</div>

		<!-- Target Editor (Schemacode) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Decompiled schemacode:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				bind:this={schemacodeContainer}
			></div>
		</div>
	</div>

	<!-- Controls -->
	<div class="grid max-h-[20vh] shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<Button onclick={handleDecompile} disabled={loading}>Decompile</Button>
				<Button variant="outline" href="/decompiler">Erase schematic</Button>
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

		<!-- Messages placeholder -->
		<div class="flex flex-col gap-2">
			<CompilerMessages {errors} {warnings} {messages} title="Decompiler messages:" />
		</div>
	</div>
</div>
