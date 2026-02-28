import { mdsvex } from 'mdsvex';
import adapter from '@sveltejs/adapter-static';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	kit: {
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
	},
	preprocess: [mdsvex()],
	extensions: ['.svelte', '.svx']
};

export default config;
