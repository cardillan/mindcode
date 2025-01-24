package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.common.TextFilePosition;
import info.teksol.mc.messages.SourcePositionTranslator;
import org.jspecify.annotations.NullMarked;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public record ExcerptSpecification(TextFilePosition start, TextFilePosition end) {

    public ExcerptSpecification {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        if (start.compareTo(end) >= 0) {
            throw new IllegalArgumentException("The start position doesn't precede the end position.");
        }
    }

    public ExcerptSpecification(int startLine, int startColumn, int endLine, int endColumn) {
        this(new TextFilePosition(startLine, startColumn), new TextFilePosition(endLine, endColumn));
    }

    public String apply(String source) {
        List<String> list = source.lines()
                .skip(start.line() - 1)
                .limit(end.line() - start.line() + 1)
                .collect(Collectors.toCollection(ArrayList::new));

        if (end.column() < list.getLast().length()) {
            list.set(list.size() - 1, list.getLast().substring(0, end.column() - 1));
        }

        if (start.column() > 1) {
            list.set(0, list.getFirst().substring(Math.min(list.getFirst().length(), start.column() - 1)));
        }

        return String.join("\n", list);
    }

    public SourcePositionTranslator toPositionTranslator() {
        final int lineOffset = start.line() - 1;
        final int columnOffset = start.column() - 1;
        return p -> new SourcePosition(p.inputFile(), p.line() + lineOffset, p.column() + columnOffset);
    }

    public static ExcerptSpecification valueOf(String value) throws ParseException {
        if (value.isEmpty()) {
            throw new ParseException("Cannot parse empty string.", 0);
        }

        String[] values = value.trim().split("-");
        if (values.length != 2) {
            throw new ParseException("Expected two position specifications separated by a hyphen.", 0);
        }

        TextFilePosition start = parsePosition(values[0], 0);
        TextFilePosition end = parsePosition(values[1], values[0].length() + 1);

        if (start.compareTo(end) >= 0) {
            throw new ParseException("The start position doesn't precede the end position.", 0);
        }

        return new ExcerptSpecification(start, end);
    }

    public static TextFilePosition parsePosition(String value, int offset) throws ParseException {
        if (value.isEmpty()) {
            throw new ParseException("Position specification is empty.", offset);
        }

        String[] values = value.trim().split(":");
        if (values.length > 2) {
            throw new ParseException("Expected at most two integers separated by a colon.", offset);
        }

        int line = Integer.parseInt(values[0]);
        if (line <= 0) {
            throw new ParseException("Line must be an integer number greater than 0.", offset);
        }

        int column = values.length == 1 ? 0 : Integer.parseInt(values[1]);
        if (column <= 0) {
            throw new ParseException("Column must be an integer number greater than 0.", offset + values[0].length() + 1);
        }

        return new TextFilePosition(line, column);
    }
}
