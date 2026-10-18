import { getVersionString } from '#lib/server.js';

export const prerender = true;

export const load = async () => {
	const version = await getVersionString();
	return { version };
};
