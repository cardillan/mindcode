<script lang="ts">
	import { browser } from '$app/environment';
	import type { Sample } from '$lib/api';
	import { Button } from '$lib/components/ui/button/index.js';
	import * as Command from '$lib/components/ui/command/index.js';
	import * as Drawer from '$lib/components/ui/drawer/index.js';
	import * as Popover from '$lib/components/ui/popover/index.js';
	import { onMount } from 'svelte';

	interface ButtonArgs {
		props: Record<string, unknown>;
	}

	let {
		samples,
		onSelect,
		disabled = false
	}: { samples: Sample[]; onSelect: (sample: Sample) => void; disabled?: boolean } = $props();

	let open = $state(false);
	let isDesktop = $state(false);

	function checkScreenSize() {
		isDesktop = window.innerWidth >= 768;
	}

	onMount(() => {
		if (browser) {
			checkScreenSize();
			window.addEventListener('resize', checkScreenSize);
			return () => window.removeEventListener('resize', checkScreenSize);
		}
	});

	function handleSampleSelect(sample: Sample) {
		open = false;
		onSelect(sample);
	}
</script>

{#snippet button({ props }: ButtonArgs)}
	<Button {...props} variant="outline">Select a sample...</Button>
{/snippet}

{#if isDesktop}
	<Popover.Root bind:open>
		<Popover.Trigger {disabled} child={button}></Popover.Trigger>
		<Popover.Content class="w-50 p-0" align="start">
			<Command.Root>
				<Command.Input placeholder="Filter sample..." />
				<Command.List>
					<Command.Empty>No results found.</Command.Empty>
					<Command.Group>
						{#each samples as sample (sample.title)}
							<Command.Item value={sample.title} onSelect={() => handleSampleSelect(sample)}>
								{sample.title}
							</Command.Item>
						{/each}
					</Command.Group>
				</Command.List>
			</Command.Root>
		</Popover.Content>
	</Popover.Root>
{:else}
	<Drawer.Root bind:open>
		<Drawer.Trigger {disabled} child={button}></Drawer.Trigger>
		<Drawer.Content>
			<div class="mt-4 border-t">
				<Command.Root>
					<Command.Input placeholder="Filter sample..." />
					<Command.List>
						<Command.Empty>No results found.</Command.Empty>
						<Command.Group>
							{#each samples as sample (sample.title)}
								<Command.Item value={sample.title} onSelect={() => handleSampleSelect(sample)}>
									{sample.title}
								</Command.Item>
							{/each}
						</Command.Group>
					</Command.List>
				</Command.Root>
			</div>
		</Drawer.Content>
	</Drawer.Root>
{/if}
