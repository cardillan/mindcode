package info.teksol.mc.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class TraceFile implements AutoCloseable {
    public static final TraceFile NULL_TRACE = new TraceFilePassive();

    public static TraceFile createTraceFile(boolean trace, boolean debugPrint, boolean systemOut) {
        String mindcodeTraceFile = System.getenv("MINDCODE_TRACE_FILE");
        return mindcodeTraceFile != null && (trace || debugPrint)
                ? new TraceFileActive(mindcodeTraceFile, trace, debugPrint, systemOut)
                : NULL_TRACE;
    }

    public abstract void close();
    public abstract void indentInc() ;
    public abstract void indentDec();
    public abstract void trace(Stream<String> text);
    public abstract void trace(Supplier<String> text);
    public abstract void trace(String text);
    public abstract void outputProgram(String text);

    private static class TraceFilePassive extends TraceFile {
        @Override public void close() { }
        @Override public void indentInc() { }
        @Override public void indentDec() { }
        @Override public void trace(Stream<String> text) { }
        @Override public void trace(Supplier<String> text) { }
        @Override public void trace(String text) { }
        @Override public void outputProgram(String text) { }
    }

    private static class TraceFileActive extends TraceFile {
        private final Indenter indenter = new Indenter("    ");

        private final boolean trace;
        private final boolean debugPrint;
        private final boolean systemOut;
        private final PrintStream stream;
        private int level = 0;

        private TraceFileActive(String mindcodeTraceFile, boolean trace, boolean debugPrint, boolean systemOut) {
            System.err.println("OPTIMIZATION TRACING IS ACTIVE");
            this.trace = trace;
            this.debugPrint = debugPrint;
            this.systemOut = systemOut;
            this.stream = createTraceStream(mindcodeTraceFile);
        }

        @Override
        public void close() {
            stream.close();
        }

        public void indentInc() {
            if (trace) {
                level++;
            }
        }

        public void indentDec() {
            if (level > 0) {
                level--;
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
                if (systemOut) {
                    System.out.print(indenter.getIndent(level));
                    System.out.println(text);
                }
                stream.print(indenter.getIndent(level));
                stream.println(text);
            }
        }

        public void outputProgram(String text) {
            if (debugPrint) {
                if (systemOut) System.out.println(text);
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
}
