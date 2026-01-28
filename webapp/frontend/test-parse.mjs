import { parser } from './src/lib/grammars/mindcode.grammar.js';

const code = 'var foo = bar;';
const tree = parser.parse(code);

console.log('Input:', code);
console.log('\nParse tree: ');
console.log(tree.toString());

console.log('\nCursor traversal:');
const cursor = tree.cursor();
do {
  const node = cursor.node;
  console.log(`${' '.repeat(cursor.depth * 2)}${node.type.name} [${node.from}-${node.to}]: "${code.slice(node.from, node.to)}"`);
} while (cursor.next());
