<script lang="ts">
	import { syncUrl, type LocalCompilerTarget } from '$lib/stores.svelte';
	import { untrack } from 'svelte';
	import * as Select from './ui/select';

	let {
		compilerTarget
	}: {
		compilerTarget: LocalCompilerTarget;
	} = $props();

	let gameVersion = $state(untrack(() => compilerTarget.value.slice(0, 1)));
	let processorType = $state(untrack(() => compilerTarget.value.slice(1) || 'none'));

	const gameVersions = {
		'6': 'Mindustry 6',
		'7': 'Mindustry 7',
		'8': 'Mindustry 8'
	};
	const processorTypes = {
		// can't use an empty string as a value for the "none" option
		// because it will cause the Select component to have "disabled" styles
		none: 'No processor',
		m: 'Micro processor',
		l: 'Logic processor',
		h: 'Hyper processor',
		w: 'World processor'
	};

	async function setValue() {
		const suffix = processorType === 'none' ? '' : processorType;
		compilerTarget.value = gameVersion + suffix;
		await syncUrl({ compilerTarget: compilerTarget.value });
	}
</script>

<Select.Root type="single" bind:value={gameVersion} onValueChange={setValue}>
	<Select.Trigger>
		{gameVersions[gameVersion as keyof typeof gameVersions] || 'Select Target'}
	</Select.Trigger>
	<Select.Content>
		{#each Object.entries(gameVersions) as [key, label]}
			<Select.Item value={key}>{label}</Select.Item>
		{/each}
	</Select.Content>
</Select.Root>

<Select.Root type="single" bind:value={processorType} onValueChange={setValue}>
	<Select.Trigger>
		{processorTypes[processorType as keyof typeof processorTypes] || 'Select Processor'}
	</Select.Trigger>
	<Select.Content>
		{#each Object.entries(processorTypes) as [key, label]}
			<Select.Item value={key}>{label}</Select.Item>
		{/each}
	</Select.Content>
</Select.Root>
