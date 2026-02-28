import { getSchemacodeSamples } from '$lib/server';

export const load = async () => {
	const samples = await getSchemacodeSamples();
	return { samples };
};
