package info.teksol.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TraceFile implements AutoCloseable {
    public static final TraceFile NULL_TRACE = new TraceFile(false, false);

    private static final boolean SYSTEM_OUT = false;

    private final boolean trace;
    private final boolean debugPrint;
    private final PrintStream stream;

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

    public void trace(Stream<String> text) {
        if (trace) {
            text.forEach(this::trace);
        }
    }

    public void trace(Supplier<String> text) {
        if (trace) {
            String output = text.get();
            if (SYSTEM_OUT) System.out.println(output);
            stream.println(output);
        }
    }

    public void trace(String text) {
        if (trace) {
            if (SYSTEM_OUT) System.out.println(text);
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
