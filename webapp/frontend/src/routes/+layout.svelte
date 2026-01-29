<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { page } from '$app/stores';
	import { Button } from '$lib/components/ui/button';

	let { children, data } = $props();

	// Determine active path for highlighting
	let currentPath = $derived($page.url.pathname);
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="flex min-h-screen flex-col bg-background text-foreground">
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

	<main class="flex-1">
		{@render children()}
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
