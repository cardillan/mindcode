<script lang="ts">
	import { syncUrl, type LocalCompilerTarget } from '$lib/stores.svelte';
	import * as Select from './ui/select';

	let {
		compilerTarget
	}: {
		compilerTarget: LocalCompilerTarget;
	} = $props();

	const targetOptions = {
		'6': 'Target: Mindustry 6',
		'7': 'Target: Mindustry 7',
		'7w': 'Target: Mindustry 7 WP',
		'8': 'Target: Mindustry 8',
		'8w': 'Target: Mindustry 8 WP'
	};

	async function setValue(value: string) {
		compilerTarget.value = value;
		await syncUrl({ compilerTarget });
	}
</script>

<Select.Root type="single" bind:value={() => compilerTarget.value, setValue}>
	<Select.Trigger>
		{targetOptions[compilerTarget.value as keyof typeof targetOptions] || 'Select Target'}
	</Select.Trigger>
	<Select.Content>
		{#each Object.entries(targetOptions) as [key, label]}
			<Select.Item value={key}>{label}</Select.Item>
		{/each}
	</Select.Content>
</Select.Root>
