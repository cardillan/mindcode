type CommonErrorCodes =
	| 'invalid_arguments'
	| 'internal_error'
	| 'unknown_method'
	| 'unsupported_method_version';

export type ErrorCode =
	| 'invalid_arguments'
	| 'invalid_program_id'
	| 'invalid_version_selection'
	| 'no_processor_attached'
	| 'no_active_map'
	| 'no_processors_found'
	| 'schematic_import_failed'
	| 'unknown_method'
	| 'unsupported_method_version'
	| 'internal_error'
	| 'no_schematic_selected'
	| 'schematic_extraction_failed';

// omitting result_type because we can just use the objects directly
// without having to deserialize them into classes first
export type MlogWatcherResponse<Result, ErrorCodes> =
	| {
			status: 'success';
			invocation_id: number;
			result: Result;
	  }
	| {
			status: 'error';
			invocation_id: number;
			result: {
				text: ErrorCodes | CommonErrorCodes;
			};
	  };

export interface MlogWatcherRequest<Params> {
	method: string;
	method_version: number;
	invocation_id: number;
	params: Params;
}

export interface ProgramId {
	id_prefix: string;
	major: number;
	minor: number;
	revision: number;
}

export interface LogicProcessor {
	x: number;
	y: number;
	type: string;
	program_id: ProgramId;
	status: 'updated' | 'incompatible_version' | 'missing_program_id';
}

export interface UpdateSelectedProcessorParams {
	code: string;
}

export interface UpdateSelectedProcessorRequest extends MlogWatcherRequest<UpdateSelectedProcessorParams> {
	method: 'update_selected_processor';
}

export type UpdateSelectedProcessorResponse = MlogWatcherResponse<void, 'no_processor_attached'>;

export interface UpdateProcessorsOnMapParams {
	code: string;
	program_id: ProgramId;
	variable_name: string;
	version_selection: 'exact' | 'compatible' | 'any';
}

export interface ProcessorUpdateResults {
	processor_updates: LogicProcessor[];
}

export interface UpdateProcessorsOnMapRequest extends MlogWatcherRequest<UpdateProcessorsOnMapParams> {
	method: 'update_processors_on_map';
}

export type UpdateProcessorsOnMapResponse = MlogWatcherResponse<
	ProcessorUpdateResults,
	'invalid_program_id' | 'invalid_version_selection' | 'no_active_map' | 'no_processors_found'
>;

export interface PutSchematicInLibraryParams {
	schematic: string;
	overwrite: boolean;
}

export interface PutSchematicInLibraryRequest extends MlogWatcherRequest<PutSchematicInLibraryParams> {
	method: 'put_schematic_in_library';
}

export type PutSchematicInLibraryResponse = MlogWatcherResponse<void, 'schematic_import_failed'>;

export interface ExtractSelectedProcessorCodeRequest extends MlogWatcherRequest<null> {
	method: 'extract_selected_processor_code';
}

export interface ProcessorExtractResults {
	code: string;
}

export type ExtractSelectedProcessorCodeResponse = MlogWatcherResponse<
	ProcessorExtractResults,
	'no_processor_attached'
>;

export interface ExtractSelectedSchematicRequest extends MlogWatcherRequest<void> {
	method: 'extract_selected_schematic';
}

export interface TextResult {
	text: string;
}

export type ExtractSelectedSchematicResponse = MlogWatcherResponse<
	Text,
	'schematic_extraction_failed' | 'no_schematic_selected'
>;
