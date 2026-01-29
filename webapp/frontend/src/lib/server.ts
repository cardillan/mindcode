import { existsSync } from 'node:fs';
import { readFile, readdir } from 'node:fs/promises';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';
import type { Sample } from './api';

/**
 * Finds the project root directory by looking for the root pom.xml file.
 * Starts from the current file's directory and walks up the directory tree.
 *
 * @returns The absolute path to the project root directory
 * @throws Error if the project root cannot be found
 */
export async function findProjectRoot(): Promise<string> {
	// Start from this file's directory
	const currentDir = dirname(fileURLToPath(import.meta.url));
	let dir = currentDir;

	// Walk up the directory tree looking for the root pom.xml
	// The root pom.xml contains <packaging>pom</packaging> and the modules list
	while (dir !== dirname(dir)) {
		// Stop at filesystem root
		const pomPath = join(dir, 'pom.xml');
		if (existsSync(pomPath)) {
			const content = await readFile(pomPath, 'utf-8');
			// Check if this is the root pom (has <packaging>pom</packaging> and <modules>)
			if (content.includes('<packaging>pom</packaging>') && content.includes('<modules>')) {
				return dir;
			}
		}
		dir = dirname(dir);
	}

	throw new Error(
		'Could not find project root. Make sure you are running from within the mindcode project.'
	);
}

/**
 * Reads the revision (version) from the root pom.xml file.
 *
 * @returns The revision string (e.g., "3.13.0")
 * @throws Error if the revision cannot be read
 */
export async function getProjectRevision(): Promise<string> {
	const projectRoot = await findProjectRoot();
	const pomPath = join(projectRoot, 'pom.xml');
	const content = await readFile(pomPath, 'utf-8');

	// Extract revision from <revision>X.Y.Z</revision>
	const match = content.match(/<revision>([^<]+)<\/revision>/);
	if (!match) {
		throw new Error('Could not find <revision> in pom.xml');
	}

	return match[1];
}

/**
 * Reads mindcode samples from the samples directory.
 *
 * @returns Array of Sample objects with id, title, and source
 */
export async function getMindcodeSamples(): Promise<Sample[]> {
	const projectRoot = await findProjectRoot();
	const samplesDir = join(
		projectRoot,
		'samples',
		'src',
		'main',
		'resources',
		'samples',
		'mindcode'
	);

	return readSamplesFromDirectory(samplesDir, '.mnd');
}

/**
 * Reads schemacode samples from the samples directory.
 *
 * @returns Array of Sample objects with id, title, and source
 */
export async function getSchemacodeSamples(): Promise<Sample[]> {
	const projectRoot = await findProjectRoot();
	const samplesDir = join(
		projectRoot,
		'samples',
		'src',
		'main',
		'resources',
		'samples',
		'schematics'
	);

	return readSamplesFromDirectory(samplesDir, '.sdf');
}

/**
 * Reads all sample files from a directory.
 *
 * @param dir The directory to read samples from
 * @param extension The file extension to filter by
 * @returns Array of Sample objects
 */
async function readSamplesFromDirectory(dir: string, extension: string): Promise<Sample[]> {
	if (!existsSync(dir)) {
		console.warn(`Samples directory not found: ${dir}`);
		return [];
	}

	const files = (await readdir(dir)).filter((f) => f.endsWith(extension));

	return Promise.all(
		files.map(async (file): Promise<Sample> => {
			const filePath = join(dir, file);
			const source = await readFile(filePath, 'utf-8');
			const id = file.replace(extension, '');
			const title = formatSampleTitle(id);

			return { id, title, source };
		})
	);
}

/**
 * Converts a kebab-case filename to a Title Case title.
 * e.g., "sum-of-primes" -> "Sum Of Primes"
 *
 * @param id The sample id (filename without extension)
 * @returns The formatted title
 */
function formatSampleTitle(id: string): string {
	return id
		.split('-')
		.map((word) => word.charAt(0).toUpperCase() + word.slice(1))
		.join(' ');
}

/**
 * Gets the full version string for display.
 *
 * @returns Version string like "Mindcode 3.13.0"
 */
export async function getVersionString(): Promise<string> {
	try {
		const revision = await getProjectRevision();
		return `Mindcode ${revision}`;
	} catch (e) {
		console.warn('Could not read project revision:', e);
		return 'Mindcode';
	}
}
