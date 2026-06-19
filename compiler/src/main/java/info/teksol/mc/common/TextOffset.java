package info.teksol.mc.common;

public record TextOffset(int line, int firstLineColumn, int indent) {
    public static final TextOffset ZERO_OFFSET = new TextOffset(0, 0, 0);

    public static TextOffset of(int line, int firstLineColumn, int indent) {
        return line == 0 &&firstLineColumn == 0 && indent == 0 ? ZERO_OFFSET : new TextOffset(line, firstLineColumn, indent);
    }

    public SourcePosition apply(SourcePosition sourcePosition) {
        if (this == ZERO_OFFSET) return sourcePosition;
        int column = sourcePosition.line() == 1 ? firstLineColumn : indent;
        return new SourcePosition(sourcePosition.inputFile(),
                sourcePosition.start().offset(line, column),
                sourcePosition.end().offset(line, column),
                sourcePosition.token().offset(line, column));
    }

    public SourcePosition apply(SourcePosition sourcePosition, InputFile inputFile) {
        if (this == ZERO_OFFSET) return sourcePosition;
        int column = sourcePosition.line() == 1 ? firstLineColumn : indent;
        return new SourcePosition(inputFile,
                sourcePosition.start().offset(line, column),
                sourcePosition.end().offset(line, column),
                sourcePosition.token().offset(line, column));
    }
}
