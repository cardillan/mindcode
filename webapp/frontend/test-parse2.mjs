import { parser } from './src/lib/grammars/mindcode.grammar.js';

const tests = [
  'foo',
  'foo = bar',
  'var foo',
  'var foo = bar;',
  'external foo',
  'external var foo',
];

tests.forEach(code => {
  const tree = parser.parse(code);
  console.log(`\nInput: "${code}"`);
  console.log(`Tree: ${tree.toString()}`);
  
  // Check for errors
  const cursor = tree.cursor();
  do {
    if (cursor.type.isError) {
      console.log(`  ERROR at [${cursor.from}-${cursor.to}]: "${code.slice(cursor.from, cursor.to)}"`);
    }
  } while (cursor.next());
});
