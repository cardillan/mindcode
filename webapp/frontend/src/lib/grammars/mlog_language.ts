import { indentNodeProp, LRLanguage } from '@codemirror/language';
import { parser } from './mlog.grammar';
import { styleTags, tags as t } from '@lezer/highlight';

// use this https://lezer-playground.vercel.app/
// playground to test the grammar
export const mlogLanguage = LRLanguage.define({
	name: 'mlog',
	parser: parser.configure({
		props: [
			indentNodeProp.add(() => {
				return (context) => {
					let parent = context.node;

					while (parent.parent) {
						parent = parent.parent;
					}

					const { doc } = context.state;

					const nextChild = parent.childAfter(context.pos);
					const posLine = doc.lineAt(context.pos).number;

					if (
						nextChild?.name === 'LabelDeclaration' &&
						doc.lineAt(nextChild.from).number === posLine
					)
						return null;

					let previousChild = parent.childBefore(context.pos);

					if (!previousChild) return null;

					if (previousChild.type.name === 'Comment') {
						const prev = previousChild.prevSibling;

						if (prev?.type.name !== 'LabelDeclaration') return null;
						if (doc.lineAt(prev.from).number !== posLine) return null;
						previousChild = prev;
					}

					if (previousChild.type.name !== 'LabelDeclaration') return null;

					return context.column(previousChild.from) + context.unit;
				};
			}),
			styleTags({
				Identifier: t.variableName,
				Comment: t.lineComment,
				Boolean: t.bool,
				Color: t.number,
				LabelDeclaration: t.function(t.name),
				InstructionName: t.keyword,
				NullLiteral: t.null,
				NumberLiteral: t.number,
				String: t.string,
				ControlInstructionName: t.controlKeyword,
				StringFormatPlaceholder: t.tagName,
				StringEscapedBracket: t.escape,
				StringColorTag: t.tagName,
				StringNewLineSequence: t.escape,
				// TODO: fix
				"Instruction/';'": t.punctuation
			})
		]
	}),
	languageData: {
		commentTokens: { line: '#' }
	}
});
