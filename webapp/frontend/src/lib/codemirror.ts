import type { Diagnostic } from '@codemirror/lint';
import { EditorView } from 'codemirror';
import type { CompileResponseMessage, SourceRange } from './api';
import {
	EditorSelection,
	Text,
	Compartment,
	StateField,
	Facet,
	StateEffect,
	type TransactionSpec
} from '@codemirror/state';
import { forest } from '@fsegurai/codemirror-theme-forest';
import { vsCodeLight } from '@fsegurai/codemirror-theme-vscode-light';
import { invertedEffects } from '@codemirror/commands';

// Compartment for dynamically switching themes
export const themeCompartment = new Compartment();
export const lineWrappingCompartment = new Compartment();

export const defaultDocId = Facet.define<string | null, string | null>({
	combine: (values) => values[values.length - 1]
});

export const updateDocId = StateEffect.define<string | null>();
export const invertUpdateDocId = invertedEffects.of((tr) => {
	for (const effect of tr.effects) {
		if (effect.is(updateDocId)) {
			const currentId = tr.startState.field(currentDocId);
			return [updateDocId.of(currentId)];
		}
	}

	return [];
});

export const currentDocId = StateField.define<string | null>({
	create(state) {
		return state.facet(defaultDocId);
	},
	update(value, tr) {
		for (const effect of tr.effects) {
			if (effect.is(updateDocId)) {
				value = effect.value;
			}
		}

		return value;
	}
});

export function getTheme(dark: boolean) {
	return dark ? forest : vsCodeLight;
}

export function updateEditor(editor: EditorView | undefined, text: string, spec?: TransactionSpec) {
	if (editor) {
		const transaction = editor.state.update({
			changes: { from: 0, to: editor.state.doc.length, insert: text },
			...spec
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
	const start = posToOffset(editor.state.doc, range.startLine, range.startColumn);
	const end = posToOffset(editor.state.doc, range.endLine, range.endColumn);

	editor.dispatch({
		selection: EditorSelection.range(start, end),
		scrollIntoView: true
	});
	editor.focus();
}
