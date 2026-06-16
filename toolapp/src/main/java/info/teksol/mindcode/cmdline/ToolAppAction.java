package info.teksol.mindcode.cmdline;

import info.teksol.mc.async.AsyncExecutor;
import info.teksol.mc.profile.CompilerProfile;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.concurrent.TimeUnit;

public enum ToolAppAction {
    COMPILE_MINDCODE("cm", new CompileMindcodeAction(), false),
    PROCESS_MLOG("pm", new ProcessMlogAction(), false),
    COMPILE_SCHEMA("cs", new CompileSchemacodeAction(), true),
    PROCESS_SCHEMA("ps", new ProcessSchemacodeAction(), false),
    ;

    private final String shortcut;
    private final ActionHandler handler;
    private final boolean supportsParallelism;

    ToolAppAction(String shortcut, ActionHandler handler, boolean supportsParallelism) {
        this.shortcut = shortcut;
        this.handler = handler;
        this.supportsParallelism = supportsParallelism;
    }

    public Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        return handler.appendSubparser(subparsers, inputFileType, defaults);
    }

    public String getShortcut() {
        return shortcut;
    }

    void handle(Namespace arguments) {
        long runStart = System.nanoTime();
        try {
            if (supportsParallelism) {
                AsyncExecutor.start(arguments.getInt("parallel"));
            }

            handler.handle(arguments);
        } finally {
            AsyncExecutor.stop();
        }
        long runTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStart);
        //System.out.printf("Total time: %d ms%n", runTime);
    }

    public CompilerProfile createCompilerProfile(Namespace arguments) {
        return handler.createCompilerProfile(this == COMPILE_SCHEMA, arguments);
    }

    ActionHandler getHandler() {
        return handler;
    }

    static ToolAppAction fromShortcut(String shortcut) {
        for (ToolAppAction a : ToolAppAction.values()) {
            if (shortcut.equals(a.shortcut)) {
                return a;
            }
        }

        throw new IllegalArgumentException("Unknown shortcut value " + shortcut);
    }
}
