package info.teksol.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TraceFile implements AutoCloseable {
    public static final TraceFile NULL_TRACE = new TraceFile(false, false);

    private static final boolean SYSTEM_OUT = true;

    // Indentation cache
    private final List<String> indents  = new ArrayList<>(List.of(""));

    private final boolean trace;
    private final boolean debugPrint;
    private final PrintStream stream;
    private int indent = 0;

    public TraceFile(boolean trace, boolean debugPrint) {
        String mindcodeTraceFile = System.getenv("MINDCODE_TRACE_FILE");
        if (mindcodeTraceFile == null) {
            this.trace = false;
            this.debugPrint = false;
            this.stream = null;
        } else {
            this.trace = trace;
            this.debugPrint = debugPrint;
            this.stream = trace || debugPrint ? createTraceStream(mindcodeTraceFile) : null;
        }
    }

    @Override
    public void close() {
        if (stream != null) {
            stream.close();
        }
    }

    public void indentInc() {
        if (trace) {
            indent++;
            if (indent >= indents.size()) {
                indents.add(indents.get(indent - 1) + "    ");
            }
        }
    }

    public void indentDec() {
        if (indent > 0) {
            indent--;
        }
    }

    public void trace(Stream<String> text) {
        if (trace) {
            text.forEach(this::trace);
        }
    }

    public void trace(Supplier<String> text) {
        if (trace) {
            trace(text.get());
        }
    }

    public void trace(String text) {
        if (trace) {
            if (SYSTEM_OUT) {
                System.out.print(indents.get(indent));
                System.out.println(text);
            }
            stream.print(indents.get(indent));
            stream.println(text);
        }
    }

    public void outputProgram(String text) {
        if (debugPrint) {
            if (SYSTEM_OUT) System.out.println(text);
            stream.println(text);
        }
    }

    private static PrintStream createTraceStream(String mindcodeTraceFile) {
        try {
            return new PrintStream(mindcodeTraceFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not create trace file", e);
        }
    }
}
