<script lang="ts">
	import { EditorView } from 'codemirror';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler, type CompileResponseMessage } from '$lib/api';
	import { schemacodeLanguage } from '$lib/grammars/schemacode_language';
	import { EditorStore, getThemeContext, LocalSource, syncUrl } from '$lib/stores.svelte';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import { updateEditor } from '$lib/codemirror';
	import CopyButton from '$lib/components/CopyButton.svelte';

	const theme = getThemeContext();

	const encodedEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, EditorView.lineWrapping] });
	});
	const schemacodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, schemacodeLanguage] });
	});

	let loading = $state(false);
	let errors = $state<CompileResponseMessage[]>([]);
	let warnings = $state<CompileResponseMessage[]>([]);
	let infos = $state<CompileResponseMessage[]>([]);

	const api = new ApiHandler();
	const localSource = new LocalSource(api, () => encodedEditor.view, []);

	async function handleDecompile() {
		if (!encodedEditor.view) return;
		const source = encodedEditor.view.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		infos = [];

		try {
			const data = await api.decompileSchematic({
				sourceId: localSource.id,
				source
			});

			if (schemacodeEditor.view) {
				const transaction = schemacodeEditor.view.state.update({
					changes: { from: 0, to: schemacodeEditor.view.state.doc.length, insert: data.source }
				});
				schemacodeEditor.view.dispatch(transaction);
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

	async function cleanEditors() {
		localSource.clear();
		updateEditor(schemacodeEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];

		await syncUrl({ localSource });
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
				class="h-[60vh] overflow-hidden rounded-md border bg-muted"
				{@attach encodedEditor.attach}
			></div>
		</div>

		<!-- Target Editor (Schemacode) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Decompiled schemacode:</Label>
			<div class="relative">
				<CopyButton getText={() => schemacodeEditor.view?.state.doc.toString() ?? ''} />
				<div
					class="h-[60vh] overflow-hidden rounded-md border bg-muted"
					{@attach schemacodeEditor.attach}
				></div>
			</div>
		</div>
	</div>

	<!-- Controls -->
	<div class="grid max-h-[20vh] shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<Button onclick={handleDecompile} disabled={loading}>Decompile</Button>
				<Button variant="outline" onclick={cleanEditors}>Erase schematic</Button>
			</div>

			<ProjectLinks variant="schemacode" />
		</div>

		<CompilerMessages {errors} {warnings} {infos} title="Decompiler messages:" />
	</div>
</div>
