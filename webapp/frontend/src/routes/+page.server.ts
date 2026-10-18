import { getMindcodeSamples } from '#lib/server.js';

export const load = async () => {
	const samples = await getMindcodeSamples();
	return { samples };
};
