<script lang="ts">
	import type { Snippet } from 'svelte';
	import * as Card from './ui/card';
	let { children }: { children?: Snippet } = $props();
</script>

<Card.Root class="shrink-0 border-dashed bg-muted/50">
	<Card.Content class="p-4 text-sm">
		{@render children?.()}
	</Card.Content>
</Card.Root>
