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
				'Block IfExpression FunctionDefinition CaseExpression AtomicBlock DebugBlock ForLoop Loop WhileLoop MlogBlock':
					foldInside,
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
