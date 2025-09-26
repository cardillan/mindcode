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

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    public TextFilePosition withColumn(int column) {
        return new TextFilePosition(line, column);
    }

    public TextFilePosition nextLine() {
        return new TextFilePosition(line + 1, 1);
    }


    @Override
    public int compareTo(TextFilePosition o) {
        return (line != o.line) ? Integer.compare(line, o.line) : Integer.compare(column, o.column);
    }
}
