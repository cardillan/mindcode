import type { Diagnostic } from '@codemirror/lint';
import { EditorView } from 'codemirror';
import type { CompileResponseMessage, SourceRange } from './api';
import { EditorSelection, Text, Compartment } from '@codemirror/state';
import { forest } from '@fsegurai/codemirror-theme-forest';
import { vsCodeLight } from '@fsegurai/codemirror-theme-vscode-light';

// Compartment for dynamically switching themes
export const themeCompartment = new Compartment();

export function getTheme(dark: boolean) {
	return dark ? forest : vsCodeLight;
}

export function updateEditor(editor: EditorView | undefined, text: string) {
	if (editor) {
		const transaction = editor.state.update({
			changes: { from: 0, to: editor.state.doc.length, insert: text },
			selection: editor.state.selection
		});
		editor.dispatch(transaction);
	}
}

export function compileMessagesToDiagnostics(
	doc: Text,
	errors: CompileResponseMessage[],
	warnings: CompileResponseMessage[]
): Diagnostic[] {
	const diagnostics: Diagnostic[] = [];

	for (const err of errors) {
		diagnostics.push({
			from: err.range ? posToOffset(doc, err.range.startLine, err.range.startColumn) : 0,
			to: err.range ? posToOffset(doc, err.range.endLine, err.range.endColumn) : 0,
			message: err.message,
			severity: 'error'
		});
	}
	for (const warn of warnings) {
		diagnostics.push({
			from: warn.range ? posToOffset(doc, warn.range.startLine, warn.range.startColumn) : 0,
			to: warn.range ? posToOffset(doc, warn.range.endLine, warn.range.endColumn) : 0,
			message: warn.message,
			severity: 'warning'
		});
	}

	return diagnostics;
}

export function posToOffset(doc: Text, line: number, column: number): number {
	const lineInfo = doc.line(line);
	return lineInfo.from + column - 1;
}

export function jumpToRange(editor: EditorView, range: SourceRange) {
	const offset = posToOffset(editor.state.doc, range.startLine, range.startColumn);

	editor.dispatch({
		selection: EditorSelection.single(offset),
		scrollIntoView: true
	});
}
