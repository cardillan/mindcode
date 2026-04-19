<script lang="ts">
	import {
		defaultProcessorType,
		isValidGameVersion,
		parseCompilerTarget,
		syncUrl,
		type GameVersion,
		type LocalCompilerTarget,
		type ProcessorType
	} from '$lib/stores.svelte';
	import { untrack } from 'svelte';
	import * as Select from './ui/select';

	let {
		compilerTarget,
		pickProcessor = false
	}: {
		compilerTarget: LocalCompilerTarget;
		pickProcessor?: boolean;
	} = $props();

	const gameVersions: Record<GameVersion, string> = {
		'6': 'Mindustry 6',
		'7': 'Mindustry 7',
		'8': 'Mindustry 8'
	};
	const processorTypes: Record<ProcessorType, string> = {
		// can't use an empty string as a value for the "none" option
		// because it will cause the Select component to have "disabled" styles
		none: 'No processor',
		m: 'Micro processor',
		l: 'Logic processor',
		h: 'Hyper processor',
		w: 'World processor'
	};

	const allowedProcessorTypes: Record<GameVersion, ProcessorType[]> = {
		'6': ['m', 'l', 'h'],
		'7': ['m', 'l', 'h', 'w'],
		'8': ['none', 'm', 'l', 'h', 'w']
	};

	const [initialGameVersion, initialProcessorType] = getInitialValues(
		untrack(() => compilerTarget.value)
	);
	let gameVersion = $state(initialGameVersion);
	let processorType = $state(initialProcessorType);

	function getInitialValues(value: string): [GameVersion, ProcessorType] {
		let { version, processor } = parseCompilerTarget(value);

		if (!pickProcessor) {
			return [version, 'none'];
		}

		if (!isValidProcessorType(version, processor)) {
			processor = defaultProcessorType;
		}

		return [version, processor];
	}

	function isValidProcessorType(gameVersion: GameVersion, type: string): type is ProcessorType {
		const allowed = allowedProcessorTypes[gameVersion];
		return allowed.includes(type as ProcessorType);
	}

	async function setValue() {
		if (pickProcessor && !isValidProcessorType(gameVersion, processorType)) {
			processorType = defaultProcessorType;
		}
		compilerTarget.version = gameVersion;
		compilerTarget.processor = processorType;
		await syncUrl({ compilerTarget: compilerTarget.value });
	}
</script>

<Select.Root
	type="single"
	bind:value={
		() => gameVersion.toString(),
		(value) => {
			const version = Number(value);
			if (isValidGameVersion(version)) {
				gameVersion = version;
			}
		}
	}
	onValueChange={setValue}
>
	<Select.Trigger>
		{gameVersions[gameVersion] || 'Select Target'}
	</Select.Trigger>
	<Select.Content>
		{#each Object.entries(gameVersions) as [key, label]}
			<Select.Item value={key}>{label}</Select.Item>
		{/each}
	</Select.Content>
</Select.Root>

{#if pickProcessor}
	<Select.Root type="single" bind:value={processorType} onValueChange={setValue}>
		<Select.Trigger>
			{processorTypes[processorType] || 'Select Processor'}
		</Select.Trigger>
		<Select.Content>
			{#each allowedProcessorTypes[gameVersion] as processorType}
				<Select.Item value={processorType}>{processorTypes[processorType]}</Select.Item>
			{/each}
		</Select.Content>
	</Select.Root>
{/if}
