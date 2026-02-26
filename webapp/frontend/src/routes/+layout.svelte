<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { page } from '$app/state';
	import * as Select from '$lib/components/ui/select';
	import { setThemeContext } from '$lib/stores.svelte.js';
	import { setSettingsContext } from '$lib/settings.svelte.js';
	import * as Tooltip from '$lib/components/ui/tooltip';
	import { foldChevronDownId, foldChevronRightId } from '$lib/editors.svelte.js';
	import { ChevronDown, ChevronRight } from '@lucide/svelte';
	import { Toaster } from '$lib/components/ui/sonner';

	interface Tool {
		value: string;
		label: string;
		path: string;
	}

	let { children, data } = $props();

	// Determine active path and tool
	let currentPath = $derived(page.url.pathname);

	let currentTool = $derived.by(() => {
		if (currentPath === '/') return 'compiler';
		if (currentPath === '/decompiler') return 'decompiler';
		if (currentPath === '/schematics') return 'schematics';
		if (currentPath === '/schematics/decompiler') return 'schematics-decompiler';
		return '';
	});

	const tools: Tool[] = [
		{ value: 'compiler', label: 'Mindcode Compiler', path: '/' },
		{ value: 'decompiler', label: 'Mlog Decompiler', path: '/decompiler' },
		{ value: 'schematics', label: 'Schematics Builder', path: '/schematics' },
		{
			value: 'schematics-decompiler',
			label: 'Schematics Decompiler',
			path: '/schematics/decompiler'
		}
	];

	setThemeContext();
	setSettingsContext();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<!-- Templates for CodeMirror fold gutter icons -->
<template id={foldChevronRightId}>
	<ChevronRight />
</template>

<template id={foldChevronDownId}>
	<ChevronDown />
</template>

<Toaster />

<div class="flex min-h-screen flex-col bg-background text-foreground">
	<header class="shrink-0 border-b bg-card">
		<div class="container mx-auto px-4 py-3">
			<div class="flex items-center gap-4">
				<h1 class="text-xl font-bold md:text-2xl">Mindcode</h1>

				<Select.Root type="single" value={currentTool}>
					<Select.Trigger class="w-50 md:w-62.5">
						{tools.find((t) => t.value === currentTool)?.label || 'Select tool...'}
					</Select.Trigger>
					<Select.Content>
						{#each tools as tool}
							<a href={tool.path}>
								<Select.Item value={tool.value}>{tool.label}</Select.Item>
							</a>
						{/each}
					</Select.Content>
				</Select.Root>
			</div>
		</div>
	</header>

	<main class="flex-1">
		<Tooltip.Provider>
			{@render children()}
		</Tooltip.Provider>
	</main>

	<footer class="mt-8 shrink-0 border-t bg-card">
		<div class="container mx-auto px-4 py-4">
			<div
				class="flex flex-col gap-4 text-center text-sm md:flex-row md:items-start md:justify-between md:text-left"
			>
				<div class="font-semibold text-muted-foreground">
					{data.version}
				</div>
				<div class="max-w-xl text-muted-foreground">
					<strong>PRIVACY POLICY</strong>: This website does not track its users. The Mindcode and
					schematics you submit for compilation/decompilation is kept for later analysis. No other
					information is kept about you or your actions on the site.
				</div>
				<div class="text-muted-foreground">
					Created by François (<a class="text-primary underline" href="https://github.com/francois"
						>GitHub</a
					>,
					<a class="text-primary underline" href="https://twitter.com/fbeausoleil">Twitter</a>).<br
					/>
					Maintained by
					<a class="text-primary underline" href="https://github.com/cardillan">Cardillan</a>.
				</div>
			</div>
		</div>
	</footer>
</div>
