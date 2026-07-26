import type { Stack } from '@lezer/lr';
import { LinkPattern } from './schemacode.grammar.terms';

export function specializeIdentifier(symbol: string, stack: Stack) {
	if (symbol.includes('*') && (symbol.length > 1 || stack.canShift(LinkPattern)))
		return LinkPattern;

	return -1;
}
