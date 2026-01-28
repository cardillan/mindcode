<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { page } from '$app/stores';
	import { Button } from '$lib/components/ui/button';

	let { children } = $props();

	// Determine active path for highlighting
	// Simple path check
	let currentPath = $derived($page.url.pathname);
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="bg-background text-foreground">
	<header class="shrink-0 border-b bg-card">
		<div class="container mx-auto px-4 py-3">
			<div class="mb-2 flex items-center justify-between">
				<h1 class="text-2xl font-bold">Mindcode Compiler</h1>
			</div>
			<nav>
				<ul class="flex gap-2 overflow-x-auto pb-2">
					<li>
						<Button variant={currentPath === '/' ? 'default' : 'secondary'} size="sm" href="/">
							Mindcode Compiler
						</Button>
					</li>
					<li>
						<Button
							variant={currentPath.startsWith('/decompiler') ? 'default' : 'secondary'}
							size="sm"
							href="/decompiler"
						>
							Mlog Decompiler
						</Button>
					</li>
					<li>
						<Button
							variant={currentPath === '/schematics' ? 'default' : 'secondary'}
							size="sm"
							href="/schematics"
						>
							Schematics Builder
						</Button>
					</li>
					<li>
						<Button
							variant={currentPath === '/schematics/decompiler' ? 'default' : 'secondary'}
							size="sm"
							href="/schematics/decompiler"
						>
							Schematics Decompiler
						</Button>
					</li>
				</ul>
			</nav>
		</div>
	</header>

	{@render children()}
</div>
