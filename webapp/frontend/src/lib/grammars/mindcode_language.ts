import { parser } from './mindcode.grammar';
import {
	LRLanguage,
	LanguageSupport,
	indentNodeProp,
	foldNodeProp,
	foldInside,
	delimitedIndent
} from '@codemirror/language';
import { parseMixed } from '@lezer/common';
import { styleTags, tags as t } from '@lezer/highlight';
import { mlogLanguage } from './mlog_language';

const mlogParser = mlogLanguage.parser.configure({
	dialect: 'mindcodeEmbed'
});

export const mindcodeLanguage = LRLanguage.define({
	name: 'mindcode',
	parser: parser.configure({
		wrap: parseMixed((node) => {
			if (node.name !== 'MlogBlockContent') return null;
			return { parser: mlogParser };
		}),
		props: [
			styleTags({
				'var const param': t.definitionKeyword,
				'cached export external guarded linked noinit remote volatile in out ref inline noinline':
					t.keyword,
				'if then else elsif loop for do while break continue return case when begin end':
					t.controlKeyword,
				'def void': t.definitionKeyword,
				'allocate heap stack require module mlog atomic debug and or not': t.keyword,
				'CallExpression/Identifier!': t.function(t.variableName),
				'FunctionDefinition/Identifier!': t.function(t.definition(t.variableName)),
				'MemberExpression/MemberProperty/Identifier/SimpleIdentifier': t.propertyName,
				'MemberExpression/MemberProperty/Identifier/ExternalIdentifier': t.propertyName,
				'Label/Identifier!': t.labelName,
				'BreakStatement/Identifier! ContinueStatement/Identifier!': t.labelName,
				SimpleIdentifier: t.variableName,
				ExternalIdentifier: t.variableName,
				BuiltinIdentifier: t.special(t.variableName),
				'String FormatString': t.string,
				CharLiteral: t.character,
				Escape: t.escape,
				'BinaryLiteral HexLiteral FloatLiteral IntLiteral': t.number,
				'true false': t.bool,
				null: t.null,
				KeywordLiteral: t.atom,
				ColorLiteral: t.color,
				'LineComment BlockComment': t.comment,
				EnhancedComment: t.special(t.comment),
				'#declare #set #setlocal': t.processingInstruction,
				DirectiveValue: t.attributeValue,
				'( ) [ ]': t.paren,
				'MlogBodyStart MlogBodyEnd': t.brace,
				'InterpolationStart InterpolationEnd FormatPlaceholderStart': t.special(t.brace),
				ArithOp: t.arithmeticOperator,
				UpdateOp: t.updateOperator,
				CompareOp: t.compareOperator,
				LogicOp: t.logicOperator,
				BitOp: t.bitwiseOperator,
				'? :': t.punctuation,
				'.. ...': t.punctuation,
				', ;': t.separator,
				'.': t.derefOperator
			}),
			indentNodeProp.add({
				'Block IfExpression FunctionDefinition CaseExpression AtomicBlock DebugBlock':
					delimitedIndent({
						closing: 'end',
						align: false
					}),
				'Loop ForLoop WhileLoop': delimitedIndent({
					closing: 'end',
					align: false
				}),
				DoWhileLoop: delimitedIndent({ closing: 'while', align: false }),
				MlogBlock: delimitedIndent({ closing: '}', align: false })
			}),
			foldNodeProp.add({
				'Block AtomicBlock DebugBlock Loop': foldInside,
				BlockComment(node, state) {
					const len = state.doc.length;
					// fold everything between the /* */ delimiters
					const from = Math.min(node.from + 2, len);
					const to = Math.min(node.to - 2, len);
					return { from, to };
				},
				IfExpression(node) {
					const first = node.getChild('if')?.nextSibling;
					if (!first) return null;

					const continuation = node.getChild('Elsif') || node.getChild('Else');
					if (continuation) {
						return {
							from: first.to,
							to: continuation.prevSibling?.to ?? continuation.from
						};
					}

					const last = node.getChild('end');
					if (!last) return null;
					return { from: first.to, to: last.from };
				},
				Elsif(node) {
					const first = node.getChild('elsif')?.nextSibling;
					const last = node.lastChild;
					if (!first || !last) return null;
					return { from: first.to, to: last.to };
				},
				Else(node) {
					const first = node.getChild('else');
					const last = node.lastChild;
					if (!first || !last) return null;
					return { from: first.to, to: last.to };
				},
				CaseExpression(node) {
					const first = node.getChild('case')?.nextSibling;
					const last = node.getChild('end');
					if (!first || !last) return null;

					return { from: first.to, to: last.from };
				},
				CaseAlternative(node) {
					const first = node.getChild('when')?.nextSibling;
					const last = node.lastChild;
					if (!first || !last) return null;
					return { from: first.to, to: last.to };
				},
				ValueList(node) {
					return { from: node.from + 1, to: node.to - 1 };
				},
				ForLoop(node) {
					const first = node.getChild('do');
					const last = node.getChild('end');
					if (!first || !last) return null;
					return { from: first.to, to: last.from };
				},
				WhileLoop(node) {
					const first = node.getChild('do');
					const last = node.getChild('end');
					if (!first || !last) return null;
					return { from: first.to, to: last.from };
				},
				DoWhileLoop(node) {
					const firstChild = node.getChild('do');
					const lastChild = node.getChild('while') || node.lastChild;
					if (!lastChild || !firstChild || lastChild.to <= firstChild.to) return null;

					return { from: firstChild.to, to: lastChild.from };
				},
				FunctionDefinition(node) {
					const name = node.getChild('Identifier');
					const end = node.getChild('end');
					if (!name || !end) return null;
					return { from: name.to, to: end.from };
				},
				MlogBlock(node) {
					const start = node.getChild('MlogBodyStart');
					const end = node.getChild('MlogBodyEnd');
					if (!start || !end) return null;
					return { from: start.to, to: end.from };
				}
			})
		]
	}),
	languageData: {
		commentTokens: { line: '//', block: { open: '/*', close: '*/' } }
	}
});

export function mindcode() {
	return new LanguageSupport(mindcodeLanguage);
}
