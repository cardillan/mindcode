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
			if (parent?.name === 'SchematicPrologue') {
				if (parent.getChild('mindcode')) return { parser: mindcodeLanguage.parser };
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
				'SchematicDefinition ProcessorConfiguration ProcessorLinks': foldInside,
				TextBlock(node, state) {
					const len = state.doc.length;
					// fold everything between the """ delimiters
					const from = Math.min(node.from + 3, len);
					const to = Math.min(node.to - 3, len);
					return { from, to };
				}
			}),
			styleTags({
				Identifier: t.variableName,
				IdentifierTemplate: t.special(t.variableName),
				'LinkPattern!': t.regexp,
				Type: t.typeName,
				String: t.string,
				StringContent: t.string,
				TextBlock: t.string,
				TextBlockContent: t.string,
				Int: t.integer,
				Version: t.number,

				'schematic end': t.keyword,
				'name description tag filename dimensions target': t.propertyName,
				'at facing virtual color connected to block command item': t.keyword,
				'liquid unit text enabled disabled processor mindcode mlog': t.keyword,
				'file links as param region flip fill replace': t.keyword,
				'global local parent': t.keyword,
				'north south east west rgba horizontal vertical': t.atom,

				LineComment: t.lineComment,
				BlockComment: t.blockComment,
				'( )': t.paren,
				', : . .. ... "*"': t.punctuation,
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

	// using angle brackets to avoid name collisions
	const mindcodeKey = '<mindcode>';
	const mlogKey = '<mlog>';
	const consumers = new Map<string, Set<string>>();

	const cursor = root.cursor();

	cursor.iterate((ref) => {
		const node = ref.node;
		if (node.name === 'ProcessorSourceAssignment') {
			const sourceNode = node.getChild('ProcessorSource');
			if (!sourceNode) return;

			const children = sourceNode.getChildren('Identifier');
			const consumer = node.getChild('mindcode') ? mindcodeKey : mlogKey;

			for (const child of children) {
				addConsumer(consumer, child);
			}
		}
		if (node.name === 'SchematicPrologue') {
			const children = node.getChildren('Identifier');
			const consumer = node.getChild('mindcode') ? mindcodeKey : mlogKey;

			for (const child of children) {
				addConsumer(consumer, child);
			}
		}
		if (node.name === 'StringAssignment') {
			const [variable, value] = node.getChildren('Identifier');
			if (!variable || !value) return;

			const consumer = input.read(variable.from, variable.to);
			addConsumer(consumer, value);
		}
	});

	function addConsumer(consumer: string, node: SyntaxNode) {
		let set = consumers.get(consumer);
		if (!set) {
			set = new Set<string>();
			consumers.set(consumer, set);
		}
		set.add(input.read(node.from, node.to));
	}

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
	traverse(mindcodeKey, mindcode, true);
	traverse(mlogKey, mlog, true);

	const data = { mindcode, mlog };
	lookupCache.set(root, data);
	return data;
}
