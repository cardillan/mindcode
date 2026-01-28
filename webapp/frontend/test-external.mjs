import { parser } from './src/lib/grammars/mindcode.grammar.js';

const tests = [
  'external var foo',
  'external cell1 var foo', 
  'external cell1 foo',
];

tests.forEach(code => {
  const tree = parser.parse(code);
  console.log(`\n${'='.repeat(60)}`);
  console.log(`Input: "${code}"`);
  console.log(`Tree: ${tree.toString()}`);
  
  const cursor = tree.cursor();
  do {
    const node = cursor.node;
    const indent = '  '.repeat(cursor.depth);
    const text = code.slice(node.from, node.to);
    const error = cursor.type.isError ? ' ❌' : '';
    console.log(`${indent}${node.type.name} [${node.from}-${node.to}]: "${text}"${error}`);
  } while (cursor.next());
});
