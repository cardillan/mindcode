import { parser } from './schemacode.grammar';
import {
	LRLanguage,
	LanguageSupport,
	indentNodeProp,
	foldNodeProp,
	foldInside,
	delimitedIndent
} from '@codemirror/language';
import { styleTags, tags as t } from '@lezer/highlight';
import { parseMixed, type Input, type SyntaxNode } from '@lezer/common';
import { mlogLanguage } from './mlog_language';
import { mindcodeLanguage } from './mindcode_language';

interface LookupData {
	mlog: Set<string>;
	mindcode: Set<string>;
}

const lookupCache = new WeakMap<SyntaxNode, LookupData>();

export const schemacodeLanguage = LRLanguage.define({
	name: 'schemacode',
	parser: parser.configure({
		wrap: parseMixed((ref, input) => {
			if (ref.name !== 'TextBlockContent' && ref.name !== 'StringContent') {
				return null;
			}

			// the first parent is TextBlock or String, the second is the "true" parent
			const parent = ref.node.parent?.parent;
			if (parent?.name === 'ProcessorSource') {
				// parent.parent is ProcessorSourceAssignment, which
				// should contain either a mindcode or mlog keyword
				if (parent.parent?.getChild('mindcode')) return { parser: mindcodeLanguage.parser };
				return { parser: mlogLanguage.parser };
			}
			if (parent?.name === 'StringAssignment') {
				let root = ref.node;
				while (root.parent) {
					root = root.parent;
				}

				const data = getLookupData(root, input);
				const identifier = parent.getChild('Identifier');
				if (!identifier) return null;

				const name = input.read(identifier.from, identifier.to);
				if (data.mlog.has(name)) return { parser: mlogLanguage.parser };
				if (data.mindcode.has(name)) return { parser: mindcodeLanguage.parser };
			}
			return null;
		}),
		props: [
			indentNodeProp.add({
				SchematicDefinition: delimitedIndent({ closing: 'end', align: false }),
				BlockItem: delimitedIndent({ closing: 'end', align: false }),
				ProcessorConfiguration: delimitedIndent({ closing: 'end', align: false }),
				ProcessorLinks: delimitedIndent({ closing: 'end', align: false })
			}),
			foldNodeProp.add({
				'SchematicDefinition ProcessorConfiguration ProcessorLinks': foldInside
			}),
			styleTags({
				Identifier: t.variableName,
				LinkPattern: t.regexp,
				Ref: t.string,
				String: t.string,
				StringContent: t.string,
				TextBlock: t.string,
				TextBlockContent: t.string,
				Int: t.integer,
				Version: t.number,

				'schematic end': t.keyword,
				'name description tag filename dimensions target': t.propertyName,
				'at facing virtual color connected to block command item liquid unit text enabled disabled processor mindcode mlog file links as':
					t.keyword,
				'north south east west rgba': t.atom,

				LineComment: t.lineComment,
				BlockComment: t.blockComment,
				'( )': t.paren,
				', : . .. ...': t.punctuation,
				'=': t.operator
			})
		]
	}),
	languageData: {
		commentTokens: { line: '//', block: { open: '/*', close: '*/' } },
		indentOnInput: /^\s*(?:end)$/
	}
});

export function schemacode() {
	return new LanguageSupport(schemacodeLanguage);
}

function getLookupData(root: SyntaxNode, input: Input): LookupData {
	if (lookupCache.has(root)) {
		const cached = lookupCache.get(root)!;
		return cached;
	}

	const consumers = new Map<string, Set<string>>();

	const cursor = root.cursor();

	cursor.iterate((ref) => {
		const node = ref.node;
		if (node.name === 'ProcessorSourceAssignment') {
			const sourceNode = node.getChild('ProcessorSource');
			if (!sourceNode) return;

			const children = sourceNode.getChildren('Identifier');
			// using angle brackets to avoid name collisions
			const target = node.getChild('mindcode') ? '<mindcode>' : '<mlog>';

			for (const child of children) {
				let set = consumers.get(target);
				if (!set) {
					set = new Set<string>();
					consumers.set(target, set);
				}
				set.add(input.read(child.from, child.to));
			}
		}
		if (node.name === 'StringAssignment') {
			const [variable, value] = node.getChildren('Identifier');
			if (!variable || !value) return;

			const consumer = input.read(variable.from, variable.to);
			const consumed = input.read(value.from, value.to);
			let set = consumers.get(consumer);
			if (!set) {
				set = new Set<string>();
				consumers.set(consumer, set);
			}
			set.add(consumed);
		}
	});

	function traverse(consumer: string, visited: Set<string>, root = false) {
		if (visited.has(consumer)) return;
		if (!root) {
			visited.add(consumer);
		}

		for (const proc of consumers.get(consumer) || []) {
			traverse(proc, visited);
		}
	}

	const mindcode = new Set<string>();
	const mlog = new Set<string>();
	traverse('<mindcode>', mindcode, true);
	traverse('<mlog>', mlog, true);

	const data = { mindcode, mlog };
	lookupCache.set(root, data);
	return data;
}
