<script lang="ts">
	import type { CompileResponseMessage, SourceRange } from '$lib/api';
	import { cn } from '$lib/utils';

	let {
		errors = [],
		warnings = [],
		messages = [],
		title = 'Compiler messages:',
		onJumpToPosition
	}: {
		errors?: CompileResponseMessage[];
		warnings?: CompileResponseMessage[];
		messages?: CompileResponseMessage[];
		title?: string;
		onJumpToPosition?: (range: SourceRange) => void;
	} = $props();
</script>

{#snippet messageItem(label: string, range: SourceRange)}
	{#if onJumpToPosition}
		<a
			href="./#"
			class="cursor-pointer font-mono underline"
			onclick={(e) => {
				e.preventDefault();
				onJumpToPosition(range);
			}}>{label} at line {range.startLine}, column {range.startColumn}:</a
		>
	{:else}
		<span class="font-mono">{label} at line {range.startLine}, column {range.startColumn}: </span>
	{/if}
{/snippet}

{#if errors.length > 0 || warnings.length > 0 || messages.length > 0}
	<div class="text-sm">
		<p class="mt-2 font-bold">{errors.length > 0 ? 'Syntax Errors:' : title}</p>

		{#if errors.length > 0}
			<ul class="list-inside list-disc">
				{#each errors as error}
					<li class="text-destructive">
						{#if error.range}
							{@render messageItem('Error', error.range)}
						{/if}
						{error.message}
					</li>
				{/each}
			</ul>
			<div class="my-2">
				Errors were encountered. See
				<a
					class="text-blue-500 underline"
					href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/TROUBLESHOOTING.markdown"
					>Mindcode Troubleshooting</a
				>
				for some tips, or
				<a
					class="text-blue-500 underline"
					href="https://github.com/cardillan/mindcode/discussions/157">ask for help</a
				>.
			</div>
		{/if}

		{#if warnings.length > 0}
			<ul class="list-inside list-disc">
				{#each warnings as warning}
					<li class="text-yellow-600 dark:text-yellow-400">
						{#if warning.range}
							{@render messageItem('Warning', warning.range)}
						{/if}
						{warning.message}
					</li>
				{/each}
			</ul>
		{/if}

		{#if messages.length > 0}
			<ul class="list-inside list-disc">
				{#each messages as msg}
					<li>{msg.message}</li>
				{/each}
			</ul>
		{/if}
	</div>
{/if}
