const mlogWatcherChannelErrorTag = Symbol('MlogWatcherChannelError');

export type MlogWatcherChannelError = {
	[mlogWatcherChannelErrorTag]: true;
} & ({ type: 'disconnected' } | { type: 'connectionError' });

export class MlogWatcherChannel {
	private socket: WebSocket;
	ready: Promise<void>;

	constructor(port: number) {
		this.socket = new WebSocket(`ws://localhost:${port}/mlog-watcher`);

		this.ready = new Promise((resolve, reject) => {
			this.socket.onopen = () => {
				resolve();
			};

			this.socket.onclose = () => {
				reject(channelError({ type: 'disconnected' }));
			};

			this.socket.onerror = () => {
				reject(channelError({ type: 'connectionError' }));
			};
		});
	}

	private assertSocketOpen(socket: WebSocket) {
		if (socket.readyState === WebSocket.OPEN) return;
		throw channelError({ type: 'disconnected' });
	}

	async send(mlog: string) {
		await this.ready;
		this.assertSocketOpen(this.socket);
		this.socket.send(mlog);
	}

	close() {
		this.socket.close();
	}
}

export function isMlogWatcherChannelError(value: unknown): value is MlogWatcherChannelError {
	if (!value || typeof value !== 'object') return false;
	return mlogWatcherChannelErrorTag in value && value[mlogWatcherChannelErrorTag] === true;
}

/** Helper function to create mlog watcher channel errors with strong typing. */
function channelError(
	error: Omit<MlogWatcherChannelError, typeof mlogWatcherChannelErrorTag>
): MlogWatcherChannelError {
	return {
		[mlogWatcherChannelErrorTag]: true,
		...error
	};
}
