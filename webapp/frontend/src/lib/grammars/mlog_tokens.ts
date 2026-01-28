import { Color, NumberLiteral } from "./mlog.grammar.terms";

export function specializeIdentifier(symbol: string) {
  // color literals
  if (symbol.startsWith("%") && (symbol.length === 7 || symbol.length === 9))
    return Color;

  // color tag literals
  if (symbol.startsWith("%[") && symbol.endsWith("]") && symbol.length > 3)
    return Color;

  // binary literals
  if (symbol.match(/^[-+]?0b[01]+$/)) return NumberLiteral;
  // hex literals
  if (symbol.match(/^[-+]?0x[0-9a-fA-F]+$/)) return NumberLiteral;

  // doubles
  if (symbol.match(/^[+-]?(\.\d+|\d+(\.\d+)?|\d+[eE][-+]?\d+)[fF.]?$/))
    return NumberLiteral;

  return -1;
}
