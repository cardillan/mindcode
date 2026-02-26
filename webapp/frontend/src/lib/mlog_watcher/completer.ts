/** A wrapper around a promise that makes it easy to programatically resolve a value. */
export class Completer<T> {
	readonly promise: Promise<T>;
	hasResolved = false;
	private resolver!: (value: T) => void;
	private rejecter!: (reason?: any) => void;

	constructor() {
		this.promise = new Promise<T>((resolve, reject) => {
			this.resolver = resolve;
			this.rejecter = reject;
		});
	}

	complete(value: T) {
		this.hasResolved = true;
		this.resolver(value);
	}

	reject(reason?: any) {
		this.rejecter(reason);
	}
}
