<script lang="ts">
	let { variant = 'mindcode' }: { variant?: 'mindcode' | 'schemacode' } = $props();
</script>

<div class="flex flex-wrap gap-2 text-xs text-muted-foreground">
	{#if variant === 'mindcode'}
		<a
			href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SYNTAX.markdown"
			class="underline hover:text-primary">Mindcode syntax</a
		>
		|
		<a
			href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SYSTEM-LIBRARY.markdown"
			class="underline hover:text-primary">System library</a
		>
		|
	{:else}
		<a
			href="https://github.com/cardillan/mindcode/blob/main/doc/syntax/SCHEMACODE.markdown"
			class="underline hover:text-primary">Schemacode syntax</a
		>
		|
	{/if}
	<a
		href="https://github.com/cardillan/mindcode/blob/main/README.markdown"
		class="underline hover:text-primary">Readme</a
	>
	|
	<a
		href="https://github.com/cardillan/mindcode/blob/main/CHANGELOG.markdown"
		class="underline hover:text-primary">Changelog</a
	>
</div>

<div class="text-xs text-muted-foreground">
	Bug reports, suggestions and questions are welcome at the <a
		class="underline hover:text-primary"
		href="https://github.com/cardillan/mindcode">project page</a
	>.
</div>
