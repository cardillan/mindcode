<script lang="ts">
	import { mlogLanguageExtension } from '$lib/grammars/mlog_language';
	import { EditorView } from 'codemirror';
	import { LoaderCircle } from '@lucide/svelte';

	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Label } from '$lib/components/ui/label';
	import CompilerMessages from '$lib/components/CompilerMessages.svelte';
	import { ApiHandler, type SourceRange } from '$lib/api';
	import { mindcodeLanguage } from '$lib/grammars/mindcode_language';
	import {
		EditorStore,
		getThemeContext,
		LocalCompilerTarget,
		LocalSource,
		syncUrl
	} from '$lib/stores.svelte';
	import { jumpToRange, updateEditor } from '$lib/codemirror';
	import ProjectLinks from '$lib/components/ProjectLinks.svelte';
	import CopyButton from '$lib/components/CopyButton.svelte';
	import TargetPicker from '$lib/components/TargetPicker.svelte';
	import { Textarea } from '$lib/components/ui/textarea';

	const theme = getThemeContext();
	const mlogEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mlogLanguageExtension] });
	});
	const mindcodeEditor = new EditorStore(theme, (parent, baseExtensions) => {
		return new EditorView({ parent, extensions: [baseExtensions, mindcodeLanguage] });
	});

	let runOutput = $state('');
	let runSteps = $state(0);

	let loadingAction = $state<'decompile' | 'decompile-run' | null>(null);
	let errors = $state<any[]>([]);
	let warnings = $state<any[]>([]);
	let infos = $state<any[]>([]);
	const api = new ApiHandler();
	const localSource = new LocalSource(api, () => mlogEditor.view, []);
	const compilerTarget = new LocalCompilerTarget();

	function handleJumpToPosition(range: SourceRange) {
		if (!mindcodeEditor.view) return;

		jumpToRange(mindcodeEditor.view, range);
	}
	async function handleDecompile(run: boolean) {
		if (!mlogEditor.view) return;
		const source = mlogEditor.view.state.doc.toString();
		loadingAction = run ? 'decompile-run' : 'decompile';
		runOutput = '';
		runSteps = 0;
		errors = [];
		warnings = [];
		infos = [];
		updateEditor(mindcodeEditor.view, '');

		try {
			const data = await api.decompileMlog({
				sourceId: localSource.id,
				source,
				target: compilerTarget.value,
				run
			});
			runOutput = data.runResults[0]?.output ?? '';
			runSteps = data.runResults[0]?.steps ?? 0;

			if (mindcodeEditor.view) {
				updateEditor(mindcodeEditor.view, data.source);
			}
			errors = data.errors;
			warnings = data.warnings;
			infos = data.infos;
			localSource.id = data.sourceId;

			await syncUrl({ localSource, compilerTarget });
		} catch (e) {
			console.error(e);
			runOutput = 'Error connecting to server.';
			runSteps = 0;
		} finally {
			loadingAction = null;
		}
	}

	async function cleanEditors() {
		localSource.clear();
		compilerTarget.value = '7';
		updateEditor(mindcodeEditor.view, '');
		updateEditor(mlogEditor.view, '');

		errors = [];
		warnings = [];
		infos = [];

		await syncUrl({ localSource, compilerTarget });
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
				class={[
					'h-[60vh] overflow-hidden rounded-md border bg-muted transition-opacity',
					localSource.isLoading && 'pointer-events-none opacity-50'
				]}
				{@attach mlogEditor.attach}
			></div>
		</div>

		<!-- Target Editor (Mindcode) -->
		<div class="flex flex-col gap-2">
			<Label class="text-lg font-bold">Decompiled Mindcode:</Label>
			<div
				class={[
					'relative transition-opacity',
					(localSource.isLoading || loadingAction !== null) && 'pointer-events-none opacity-50'
				]}
			>
				<CopyButton getText={() => mindcodeEditor.view?.state.doc.toString() ?? ''} />
				<div
					class="h-[60vh] overflow-hidden rounded-md border bg-muted"
					{@attach mindcodeEditor.attach}
				></div>
			</div>
		</div>
	</div>

	<!-- Controls -->
	<div class="grid max-h-[20vh] shrink-0 grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2">
		<div class="flex flex-col gap-4">
			<div class="flex flex-wrap items-center gap-2">
				<div class="w-55">
					<TargetPicker {compilerTarget} />
				</div>

				<Button
					onclick={() => handleDecompile(false)}
					disabled={localSource.isLoading || loadingAction !== null}
				>
					{#if loadingAction === 'decompile'}<LoaderCircle class="mr-2 h-4 w-4 animate-spin" />{/if}
					Decompile
				</Button>
				<Button
					onclick={() => handleDecompile(true)}
					disabled={localSource.isLoading || loadingAction !== null}
				>
					{#if loadingAction === 'decompile-run'}<LoaderCircle
							class="mr-2 h-4 w-4 animate-spin"
						/>{/if}
					Decompile and Run
				</Button>
				<Button variant="outline" onclick={cleanEditors}>Erase mlog</Button>
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
