export interface SourceRange {
	path: string;
	startLine: number;
	startColumn: number;
	endLine: number;
	endColumn: number;
}

export interface CompileResponseMessage {
	message: string;
	prefix: string;
	range?: SourceRange;
}

export interface CompileResponse {
	compiledCode: string;
	runOutput: string;
	runSteps: number;
	errors: CompileResponseMessage[];
	warnings: CompileResponseMessage[];
	messages: CompileResponseMessage[];
}

export interface CompileRequest {
	source: string;
	target: string;
	run: boolean;
}

export interface SchemacodeCompileRequest {
	source: string;
	target: string;
}

export interface SchemacodeCompileResponse {
	compiledCode: string;
	errors: CompileResponseMessage[];
	warnings: CompileResponseMessage[];
	messages: CompileResponseMessage[];
}

export interface DecompileResponse {
	source: string;
	errors: CompileResponseMessage[];
	warnings: CompileResponseMessage[];
	messages: CompileResponseMessage[];
}

export interface ServerSource {
	id: string;
	source: string;
	/** ISO 8601 formatted date string */
	createdAt: string;
}

export interface Sample {
	name: string;
	title: string;
	source: string;
}

export class ApiHandler {
	private fetch: typeof fetch;

	constructor(fetchImpl = fetch) {
		this.fetch = fetchImpl;
	}

	async compileMindcode(request: CompileRequest): Promise<CompileResponse> {
		const response = await this.fetch('/api/compile', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(request)
		});

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async compileSchemacode(request: SchemacodeCompileRequest): Promise<SchemacodeCompileResponse> {
		const response = await this.fetch('/api/schemacode/compile', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(request)
		});

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async decompileSchematic(base64EncodedSchematic: string): Promise<DecompileResponse> {
		const response = await this.fetch('/api/decompile/schematic', {
			method: 'POST',
			headers: {
				'Content-Type': 'text/plain'
			},
			body: base64EncodedSchematic
		});

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}
		return await response.json();
	}

	async decompileMlog(mlogSource: string): Promise<DecompileResponse> {
		const response = await this.fetch('/api/decompile/mlog', {
			method: 'POST',
			headers: {
				'Content-Type': 'text/plain'
			},
			body: mlogSource
		});

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async loadSource(id: string): Promise<ServerSource> {
		const response = await this.fetch(`/api/source/${encodeURIComponent(id)}`);

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async saveSource(source: string): Promise<ServerSource> {
		const response = await this.fetch('/api/source', {
			method: 'POST',
			headers: {
				'Content-Type': 'text/plain'
			},
			body: source
		});

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async updateSource(id: string, source: string): Promise<ServerSource> {
		const response = await this.fetch(`/api/source/${encodeURIComponent(id)}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'text/plain'
			},
			body: source
		});

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async getSamples(): Promise<Sample[]> {
		const response = await this.fetch('/api/samples');

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}

		return await response.json();
	}

	async getSchemacodeSamples(): Promise<Sample[]> {
		const response = await this.fetch('/api/schemacode/samples');

		if (!response.ok) {
			throw new ApiError(response.url, response.status);
		}
		return await response.json();
	}
}

export class ApiError extends Error {
	constructor(
		public path: string,
		public status: number
	) {
		super(`API request to ${path} failed with status ${status}`);
		this.name = 'ApiError';
	}
}
