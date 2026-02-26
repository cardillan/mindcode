import { toast } from 'svelte-sonner';
import { isMlogWatcherChannelError, MlogWatcherChannel } from './channel';
import { Completer } from './completer';

/**
 * Manages a lazily initialized MlogWatcherChannel.
 * Integrates with the app's toaster to show error notifications.
 */
export class MlogWatcherStore {
	private channel = new Completer<MlogWatcherChannel>();
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

			return () => {
				channel.close();
				this.channel = new Completer();
			};
		});
	}

	async send(mlog: string) {
		this.#initialized = true;
		const channel = await this.channel.promise;
		await channel.send(mlog);
	}
}
