<script lang="ts">
	import { Settings } from '@lucide/svelte';
	import * as Drawer from './ui/drawer';
	import { Button } from './ui/button';
	import { Input } from './ui/input';
	import { Label } from './ui/label';
	import { Separator } from './ui/separator';
	import { getSettingsContext } from '$lib/settings.svelte';
	import Switch from './ui/switch/switch.svelte';

	const settings = getSettingsContext();
</script>

<Drawer.Root direction="right">
	<Drawer.Trigger>
		{#snippet child({ props })}
			<Button {...props} variant="outline" size="icon">
				<Settings class="size-4" />
			</Button>
		{/snippet}
	</Drawer.Trigger>
	<Drawer.Content>
		<div class="flex h-full flex-col">
			<Drawer.Header>
				<Drawer.Title>Settings</Drawer.Title>
				<Drawer.Description>Configure editor and integration options.</Drawer.Description>
			</Drawer.Header>

			<div class="flex-1 overflow-auto px-4">
				<div class="grid gap-6 pb-6">
					<!-- MlogWatcher Settings -->
					<div class="grid gap-3">
						<h5 class="text-xs font-medium tracking-wide text-muted-foreground uppercase">
							MlogWatcher Integration
						</h5>
						<div class="grid gap-3">
							<div class="grid grid-cols-3 items-center gap-4">
								<Label for="mlog-port" class="text-sm">Port</Label>
								<Input
									id="mlog-port"
									type="number"
									bind:value={settings.mlogWatcherPort}
									class="col-span-2"
									min="1"
									max="65535"
								/>
							</div>
							<p class="text-xs text-muted-foreground">
								WebSocket port for the Mindustry mod integration.
							</p>
						</div>
					</div>

					<Separator />

					<!-- Editor Options -->
					<div class="grid gap-3">
						<h5 class="text-xs font-medium tracking-wide text-muted-foreground uppercase">
							Editor Options
						</h5>
						<div class="flex items-center justify-between">
							<div class="space-y-0.5">
								<Label for="line-wrap" class="text-sm">Line Wrapping</Label>
								<p class="text-xs text-muted-foreground">Wrap long lines in the editor</p>
							</div>
							<Switch id="line-wrap" bind:checked={settings.lineWrapping} />
						</div>
					</div>
				</div>
			</div>

			<Drawer.Footer>
				<Drawer.Close>
					{#snippet child({ props })}
						<Button {...props} variant="outline" class="w-full">Close</Button>
					{/snippet}
				</Drawer.Close>
			</Drawer.Footer>
		</div>
	</Drawer.Content>
</Drawer.Root>
