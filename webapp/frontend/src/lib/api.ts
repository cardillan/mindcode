export interface SourceRange {
	path: string;
	startLine: number;
	startColumn: number;
	endLine: number;
	endColumn: number;
}

export interface RunResult {
	processorId: string;
	output: string;
	steps: number;
}

export interface CompileRequest {
	sourceId: string | null;
	source: string;
	target: string;
	run: boolean;
}

export interface CompileResponseMessage {
	message: string;
	prefix: string;
	range?: SourceRange;
}

export interface CompileResponse {
	sourceId: string;
	compiled: string;
	runResult: RunResult | null;
	errors: CompileResponseMessage[];
	warnings: CompileResponseMessage[];
	infos: CompileResponseMessage[];
	isPlainText: boolean;
}

export interface SchemacodeCompileRequest {
	sourceId: string | null;
	source: string;
	target: string;
	run: boolean;
}

export interface SchemacodeCompileResponse {
	sourceId: string;
	compiled: string;
	runResults: RunResult[];
	errors: CompileResponseMessage[];
	warnings: CompileResponseMessage[];
	infos: CompileResponseMessage[];
}

export interface DecompileRequest {
	sourceId: string | null;
	source: string;
	target: string;
	run: boolean;
}

export interface DecompileResponse {
	sourceId: string;
	source: string;
	errors: CompileResponseMessage[];
	warnings: CompileResponseMessage[];
	infos: CompileResponseMessage[];
	runResults: RunResult[];
}

export interface ServerSource {
	id: string;
	source: string;
	/** ISO 8601 formatted date string */
	createdAt: string;
}

export interface Sample {
	id: string;
	title: string;
	source: string;
	runnable: boolean;
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
			throw new ApiError('/api/compile', response.status);
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
			throw new ApiError('/api/schemacode/compile', response.status);
		}

		return await response.json();
	}

	async decompileSchematic(request: DecompileRequest): Promise<DecompileResponse> {
		const response = await this.fetch('/api/schemacode/decompile', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(request)
		});

		if (!response.ok) {
			throw new ApiError('/api/schemacode/decompile', response.status);
		}
		return await response.json();
	}

	async decompileMlog(request: DecompileRequest): Promise<DecompileResponse> {
		const response = await this.fetch('/api/decompile', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(request)
		});

		if (!response.ok) {
			throw new ApiError('/api/decompile', response.status);
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
