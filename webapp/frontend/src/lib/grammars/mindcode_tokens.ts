import { ExternalTokenizer } from '@lezer/lr';
import { EndIdentifier } from './mindcode.grammar.terms';

const e = 101,
	n = 110,
	d = 100,
	leftParen = 40,
	space = 32,
	tab = 9,
	newline = 10,
	carriageReturn = 13;

export const endCall = new ExternalTokenizer((input, stack) => {
	if (input.next == e && input.peek(1) == n && input.peek(2) == d) {
		let pos = 3;
		let ch = input.peek(pos);
		while (ch == space || ch == tab || ch == newline || ch == carriageReturn) {
			pos++;
			ch = input.peek(pos);
		}

		if (ch !== leftParen) return;
		input.acceptToken(EndIdentifier, 3);
	}
});
