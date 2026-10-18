import { mdsvex } from 'mdsvex';
import adapter from '@sveltejs/adapter-static';
import { defineConfig } from 'vitest/config';
import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { lezer } from '@lezer/generator/rollup';

export default defineConfig({
	plugins: [
		tailwindcss(),
		lezer(),
		sveltekit({
			preprocess: [mdsvex()],
			extensions: ['.svelte', '.svx'],
			adapter: adapter({
				// Output directly into Spring Boot's static folder
				pages: '../src/main/resources/static',
				assets: '../src/main/resources/static',
				// SPA fallback so client routing works for non-prerendered routes
				fallback: '200.html'
			}),

			prerender: {
				// Crawl and prerender all reachable pages
				entries: ['*']
			}
		})
	],
	server: { proxy: { '/api': 'http://localhost:8080' } },
	test: {
		expect: { requireAssertions: true },
		projects: [
			{
				extends: './vite.config.ts',
				test: {
					name: 'server',
					environment: 'node',
					include: ['src/**/*.{test,spec}.{js,ts}'],
					exclude: ['src/**/*.svelte.{test,spec}.{js,ts}']
				}
			}
		]
	}
});
