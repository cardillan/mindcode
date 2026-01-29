<script lang="ts">
	import { EditorView } from 'codemirror';
	import { onMount } from 'svelte';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler } from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';
	import { baseExtensions } from '$lib/codemirror';
	import { LocalSource, syncUrl } from '$lib/hooks.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';

	let encodedContainer = $state<HTMLElement>();
	let schemacodeContainer = $state<HTMLElement>();

	let encodedEditor = $state<EditorView>();
	let schemacodeEditor = $state<EditorView>();

	let loading = $state(false);
	let errors = $state<any[]>([]);
	let warnings = $state<any[]>([]);
	let infos = $state<any[]>([]);

	const api = new ApiHandler();
	const localSource = new LocalSource(api, () => encodedEditor, []);

	onMount(() => {
		if (encodedContainer) {
			encodedEditor = new EditorView({
				parent: encodedContainer,
				extensions: [baseExtensions()]
			});
		}
		if (schemacodeContainer) {
			schemacodeEditor = new EditorView({
				parent: schemacodeContainer,
				extensions: [baseExtensions(), schemacodeLanguage]
			});
		}

		return () => {
			encodedEditor?.destroy();
			schemacodeEditor?.destroy();
		};
	});

	async function handleDecompile() {
		if (!encodedEditor) return;
		const source = encodedEditor.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		infos = [];

		try {
			const data = await api.decompileSchematic({
				sourceId: localSource.id,
				source
			});

			if (schemacodeEditor) {
				const transaction = schemacodeEditor.state.update({
					changes: { from: 0, to: schemacodeEditor.state.doc.length, insert: data.source }
				});
				schemacodeEditor.dispatch(transaction);
			}
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			localSource.id = data.sourceId;

			await syncUrl({ localSource });
		} catch (e) {
			console.error(e);
		} finally {
			loading = false;
		}
	}

	function cleanEditors() {
		if (encodedEditor) {
			const transaction = encodedEditor.state.update({
				changes: { from: 0, to: encodedEditor.state.doc.length, insert: '' }
			});
			encodedEditor.dispatch(transaction);
		}
		if (schemacodeEditor) {
			const transaction = schemacodeEditor.state.update({
				changes: { from: 0, to: schemacodeEditor.state.doc.length, insert: '' }
			});
			schemacodeEditor.dispatch(transaction);
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

			<ProjectLinks variant="schemacode" />
		</div>

		<CompilerMessages {errors} {warnings} {infos} title="Decompiler messages:" />
	</div>
</div>
