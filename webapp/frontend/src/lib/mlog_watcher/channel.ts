import { Completer } from './completer';
import {
	type UpdateProcessorsOnMapRequest,
	type UpdateProcessorsOnMapResponse,
	type ExtractSelectedProcessorCodeRequest,
	type ExtractSelectedProcessorCodeResponse,
	type MlogWatcherRequest,
	type MlogWatcherResponse,
	type PutSchematicInLibraryRequest,
	type PutSchematicInLibraryResponse,
	type UpdateProcessorsOnMapParams,
	type UpdateSelectedProcessorRequest,
	type UpdateSelectedProcessorResponse
} from './protocol';

const mlogWatcherChannelErrorTag = Symbol('MlogWatcherChannelError');

export type MlogWatcherChannelError = {
	[mlogWatcherChannelErrorTag]: true;
} & ({ type: 'disconnected' } | { type: 'connectionError' });

export class MlogWatcherChannel {
	private nextId = 0;
	private pendingInvocations = new Map<number, Completer<unknown>>();
	private socket: WebSocket;
	ready: Promise<void>;

	constructor(port: number) {
		this.socket = new WebSocket(`ws://localhost:${port}/v1`);

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

			this.socket.onmessage = (event) => {
				console.log(event.data);
				const response: MlogWatcherResponse<unknown, unknown> = JSON.parse(event.data);
				const completer = this.pendingInvocations.get(response.invocation_id);
				if (!completer) {
					console.warn(`Received response for unknown invocation_id ${response.invocation_id}`);
					return;
				}

				completer.complete(response);
				this.pendingInvocations.delete(response.invocation_id);
			};
		});
	}

	private assertSocketOpen(socket: WebSocket) {
		if (socket.readyState === WebSocket.OPEN) return;
		throw channelError({ type: 'disconnected' });
	}

	private async invokeMethod<
		Request extends MlogWatcherRequest<unknown>,
		Response extends MlogWatcherResponse<unknown, unknown>
	>(method: Request['method'], methodVersion: number, params: Request['params']) {
		await this.ready;
		this.assertSocketOpen(this.socket);

		const invocation: MlogWatcherRequest<unknown> = {
			method,
			method_version: methodVersion,
			invocation_id: this.nextId++,
			params
		};

		const completer = new Completer();
		this.pendingInvocations.set(invocation.invocation_id, completer);
		const content = JSON.stringify(invocation);
		console.log('Sending invocation:', content);
		this.socket.send(content);
		return completer.promise as Promise<Response>;
	}

	updateSelectedProcessor(code: string) {
		return this.invokeMethod<UpdateSelectedProcessorRequest, UpdateSelectedProcessorResponse>(
			'update_selected_processor',
			1,
			{ code }
		);
	}

	updateProcessorsOnMap(params: UpdateProcessorsOnMapParams) {
		return this.invokeMethod<UpdateProcessorsOnMapRequest, UpdateProcessorsOnMapResponse>(
			'update_processors_on_map',
			1,
			params
		);
	}

	putSchematicInLibrary(schematic: string, overwrite: boolean) {
		return this.invokeMethod<PutSchematicInLibraryRequest, PutSchematicInLibraryResponse>(
			'put_schematic_in_library',
			1,
			{ schematic, overwrite }
		);
	}

	extractSelectedProcessorCode() {
		return this.invokeMethod<
			ExtractSelectedProcessorCodeRequest,
			ExtractSelectedProcessorCodeResponse
		>('extract_selected_processor_code', 1, null);
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
