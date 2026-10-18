import { getSchemacodeSamples } from '#lib/server.js';

export const load = async () => {
	const samples = await getSchemacodeSamples();
	return { samples };
};
