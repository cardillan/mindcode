<script lang="ts">
	import { mlogLanguage } from '$lib/grammars/mlog_language';
	import { EditorView } from 'codemirror';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler } from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';
	import { EditorStore, getThemeContext, LocalSource, syncUrl } from '$lib/stores.svelte';
	import { updateEditor } from '$lib/codemirror';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';

	const theme = getThemeContext();
	const mlogEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mlogLanguage] });
	});
	const mindcodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mindcodeLanguage] });
	});

	let loading = $state(false);
	let errors = $state<any[]>([]);
	let warnings = $state<any[]>([]);
	let infos = $state<any[]>([]);
	const api = new ApiHandler();
	const localSource = new LocalSource(api, () => mlogEditor.view, []);

	async function handleDecompile() {
		if (!mlogEditor.view) return;
		const source = mlogEditor.view.state.doc.toString();
		loading = true;
		errors = [];
		warnings = [];
		infos = [];
		try {
			const data = await api.decompileMlog({
				sourceId: localSource.id,
				source
			});

			if (mindcodeEditor.view) {
				updateEditor(mindcodeEditor.view, data.source);
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
		updateEditor(mindcodeEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];

		await syncUrl({ localSource });
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
				{@attach mlogEditor.attach}
			></div>
		</div>

		<!-- Target Editor (Mindcode) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Decompiled Mindcode:</Label>
			<div
				class="max-h-[60vh] min-h-[60vh] flex-1 overflow-hidden rounded-md border bg-muted"
				{@attach mindcodeEditor.attach}
			></div>
		</div>
	</div>

	<!-- Controls -->
	<div class="grid max-h-[20vh] shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<Button onclick={handleDecompile} disabled={loading}>Decompile</Button>
				<Button variant="outline" onclick={cleanEditors}>Erase mlog</Button>
			</div>

			<ProjectLinks />
		</div>

		<CompilerMessages {errors} {warnings} {infos} title="Decompiler messages:" />
	</div>
</div>
