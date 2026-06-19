package info.teksol.mc.common;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record TextFilePosition(int line, int column) implements Comparable<TextFilePosition> {
    public TextFilePosition {
        if (line <= 0) {
            throw new IllegalArgumentException("Line must be greater than 0.");
        }
        if (column <= 0) {
            throw new IllegalArgumentException("Column must be greater than 0.");
        }
    }

    public TextFilePosition offset(int lineOffset, int columnOffset) {
        return new TextFilePosition(line + lineOffset, column + columnOffset);
    }

    @Override
    public int compareTo(TextFilePosition o) {
        return (line != o.line) ? Integer.compare(line, o.line) : Integer.compare(column, o.column);
    }
}
