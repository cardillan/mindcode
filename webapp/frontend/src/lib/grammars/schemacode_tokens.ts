import { LinkPattern } from './schemacode.grammar.terms';

export function specializeIdentifier(symbol: string) {
	if (symbol.includes('*')) return LinkPattern;

	return -1;
}
