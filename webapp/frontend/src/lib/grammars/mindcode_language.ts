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

export const mindcodeLanguage = LRLanguage.define({
	name: 'mindcode',
	parser: parser.configure({
		wrap: parseMixed((node) => {
			if (node.name !== 'MlogBlockContent') return null;
			return { parser: mlogLanguage.parser };
		}),
		props: [
			styleTags({
				'var const param': t.definitionKeyword,
				'cached export external guarded linked noinit remote volatile in out ref inline noinline':
					t.modifier,
				'if then else elsif for do while break continue return case when': t.controlKeyword,
				'def void': t.definitionKeyword,
				'allocate heap stack require module mlog atomic debug begin end and or not': t.keyword,
				'CallExpression/Identifier/...': t.function(t.variableName),
				'FunctionDefinition/Identifier/...': t.function(t.definition(t.variableName)),
				'MemberExpression/MemberProperty/Identifier/...': t.propertyName,
				'Label/Identifier/...': t.labelName,
				'BreakStatement/Identifier/... ContinueStatement/Identifier/...': t.labelName,
				SimpleIdentifier: t.variableName,
				ExternalIdentifier: t.special(t.variableName),
				BuiltinIdentifier: t.standard(t.variableName),
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
				'#declare #set #setlocal': t.keyword,
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
				'ForLoop WhileLoop': delimitedIndent({
					closing: 'end',
					align: false
				}),
				DoWhileLoop: delimitedIndent({ closing: 'while', align: false }),
				MlogBlock: delimitedIndent({ closing: '}', align: false })
			}),
			foldNodeProp.add({
				'Block IfExpression FunctionDefinition CaseExpression AtomicBlock DebugBlock ForLoop WhileLoop MlogBlock':
					foldInside,
				DoWhileLoop(node) {
					const firstChild = node.getChild('do');
					const lastChild = node.getChild('while') || node.lastChild;
					if (!lastChild || !firstChild || lastChild.to <= firstChild.to) return null;

					return { from: firstChild.to, to: lastChild.from };
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
