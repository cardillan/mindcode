<script lang="ts">
	import { Settings } from '@lucide/svelte';
	import * as Popover from './ui/popover';
	import { Button } from './ui/button';
	import { Input } from './ui/input';
	import { Label } from './ui/label';
	import { Separator } from './ui/separator';
	import { getSettingsContext } from '$lib/settings.svelte';

	const settings = getSettingsContext();
</script>

<Popover.Root>
	<Popover.Trigger>
		{#snippet child({ props })}
			<Button {...props} variant="outline" size="icon">
				<Settings class="size-4" />
			</Button>
		{/snippet}
	</Popover.Trigger>
	<Popover.Content class="w-80">
		<div class="grid gap-4">
			<div class="space-y-2">
				<h4 class="text-lg leading-none font-semibold">Settings</h4>
				<p class="text-sm text-muted-foreground">Configure editor and integration options.</p>
			</div>

			<Separator />

			<div class="grid gap-4">
				<!-- MlogWatcher Settings -->
				<div class="grid gap-2">
					<h5 class="text-sm font-medium tracking-wide text-muted-foreground">
						MlogWatcher Integration
					</h5>
					<div class="grid grid-cols-3 items-center gap-4">
						<Label for="mlog-port" class="text-sm">Port</Label>
						<Input
							id="mlog-port"
							type="number"
							bind:value={settings.mlogWatcherPort}
							class="col-span-2 h-8"
							min="1"
							max="65535"
						/>
					</div>
				</div>
			</div>
		</div>
	</Popover.Content>
</Popover.Root>
