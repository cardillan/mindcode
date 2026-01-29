import { getMindcodeSamples } from '$lib/server';

export const load = async () => {
	const samples = await getMindcodeSamples();
	return { samples };
};
