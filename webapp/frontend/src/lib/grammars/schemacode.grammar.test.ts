import { parser } from './schemacode.grammar';
import { fileTests } from '@lezer/generator/test';
import { readdir, readFile } from 'node:fs/promises';
import { join } from 'node:path';
import { test, describe, expect } from 'vitest';

describe('schemacode grammar', async () => {
	const directory = join(import.meta.dirname, 'test/schemacode');
	const fileNames = await readdir(directory);
	const fileContents = await Promise.all(
		fileNames.map((file) => readFile(join(directory, file), 'utf-8'))
	);

	const testFiles = fileContents.flatMap((file, index) => fileTests(file, fileNames[index]));

	for (const { name, run } of testFiles) {
		test.concurrent(name, () => {
			run(parser);
			expect(true).toBe(true); // prevent "no assertions" error
		});
	}
});
