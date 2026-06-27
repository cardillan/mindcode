import { toast } from 'svelte-sonner';
import { isMlogWatcherChannelError, MlogWatcherChannel } from './channel';
import { Completer } from './completer';
import type { UpdateProcessorsOnMapParams } from './protocol';

/**
 * Manages a lazily initialized MlogWatcherChannel.
 * Integrates with the app's toaster to show error notifications.
 */
export class MlogWatcherStore {
	private channel = new Completer<MlogWatcherChannel>();
	/**
	 * This reactive property helps us have a lazily intialized channel
	 * that is disposed and recreated when the port changes.
	 */
	#initialized = $state(false);

	constructor(port: () => number) {
		$effect.pre(() => {
			port();
			this.#initialized = false;
		});

		$effect.pre(() => {
			if (!this.#initialized) return;
			const channel = new MlogWatcherChannel(port());
			this.channel.complete(channel);

			channel.ready.catch((error) => {
				this.channel = new Completer();
				this.#initialized = false;

				toast.error(
					'Mlog Watcher connection failed. Please ensure the Mlog Watcher server is running and the port is correct.'
				);
				if (!isMlogWatcherChannelError(error)) {
					console.error('Mlog Watcher connection error:', error);
				}
			});

			channel.closed.then((e) => {
				if (!e.wasOpen) return;

				this.channel = new Completer();
				this.#initialized = false;

				if (e.code !== 1000) {
					toast.error(
						`Mlog Watcher connection closed unexpectedly. Websocket close code: ${e.code}`
					);
				}
			});

			return () => {
				channel.close();
				this.channel = new Completer();
			};
		});
	}

	/** Ensures the channel is/has been initialized, returns a promise resolving to the channel */
	private getChannel() {
		this.#initialized = true;
		return this.channel.promise;
	}

	async updateSelectedProcessor(code: string) {
		const channel = await this.getChannel();
		return await channel.updateSelectedProcessor(code);
	}

	async updateProcessorsOnMap(params: UpdateProcessorsOnMapParams) {
		const channel = await this.getChannel();
		return await channel.updateProcessorsOnMap(params);
	}

	async putSchematicInLibrary(schematic: string, overwrite: boolean) {
		const channel = await this.getChannel();
		return await channel.putSchematicInLibrary(schematic, overwrite);
	}

	async extractSelectedProcessorCode() {
		const channel = await this.getChannel();
		return await channel.extractSelectedProcessorCode();
	}
}
