import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import { lezer } from '@lezer/generator/rollup';

export default defineConfig({
	plugins: [tailwindcss(), lezer(), sveltekit()],
	server: {
		proxy: {
			'/api': 'http://localhost:8080'
		}
	}
});
