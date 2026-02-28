import type { Stack } from '@lezer/lr';
import {
	Color,
	NumberLiteral,
	GlobalIdentifier,
	Dialect_mindcodeEmbed
} from './mlog.grammar.terms';

const binaryRegex = /^[-+]?0b[01]+$/;
const hexRegex = /^[-+]?0x[0-9a-fA-F]+$/;
// mindcode's float regex is a bit different from mlog's,
// as it allows for things like 1.2e10, which mlog doesn't parse,
// conversely, mlog allows suffixes like 'f' or '.', which mindcode doesn't parse.
const mlogFloatRegex = /^[+-]?(\.\d+|\d+(\.\d+)?|\d+[eE][-+]?\d+)[fF.]?$/;
// Since this is used for highlighting purposes,
// letting mindcode floats end with [fF.] is not a big deal,
// and it allows us to only need to check one regex based on the dialect.
const mindcodeFloatRegex = /^[+-]?(\d+|\d*\.\d+)([eE][-+]?\d+)?[fF.]?$/;

export function specializeIdentifier(symbol: string, stack: Stack) {
	if (symbol.startsWith('@') && symbol.length > 1) return GlobalIdentifier;

	// color literals
	if (symbol.startsWith('%') && (symbol.length === 7 || symbol.length === 9)) return Color;

	// color tag literals
	if (symbol.startsWith('%[') && symbol.endsWith(']') && symbol.length > 3) return Color;

	if (binaryRegex.test(symbol)) return NumberLiteral;
	if (hexRegex.test(symbol)) return NumberLiteral;

	const floatRegex = stack.dialectEnabled(Dialect_mindcodeEmbed)
		? mindcodeFloatRegex
		: mlogFloatRegex;
	if (floatRegex.test(symbol)) return NumberLiteral;

	return -1;
}
