<script lang="ts">
	import { page } from '$app/state';
	import * as Card from '$lib/components/ui/card';
	import { Button } from '$lib/components/ui/button';
	import { CircleAlert } from '@lucide/svelte';
</script>

<div class="container mx-auto flex min-h-[calc(100vh-8rem)] items-center justify-center px-4 py-8">
	<Card.Root class="w-full max-w-md border-destructive/50">
		<Card.Header class="text-center">
			<div
				class="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-destructive/10"
			>
				<CircleAlert class="h-6 w-6 text-destructive" />
			</div>
			<Card.Title class="text-2xl">Error {page.status}</Card.Title>
			<Card.Description class="text-base"
				>{page.error?.message ?? 'An unexpected error occurred'}</Card.Description
			>
		</Card.Header>
		<Card.Content class="flex justify-center">
			<Button href="/" class="w-full sm:w-auto">Go back home</Button>
		</Card.Content>
	</Card.Root>
</div>
