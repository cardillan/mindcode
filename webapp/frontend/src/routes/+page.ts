import { ApiHandler } from '$lib/api';

export const load = async ({ fetch }) => {
	const api = new ApiHandler(fetch);
	const samples = await api.getSamples();

	return { samples };
};
