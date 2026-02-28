import { HTMLInputAttributes } from 'svelte/elements';

// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces
declare global {
	namespace App {
		// interface Error {}
		// interface Locals {}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

declare module 'svelte/elements' {
	interface HTMLInputAttributes {
		/** Used by Codemirror to find the main input field in a search panel */
		'main-field'?: string;
	}
}

export {};
