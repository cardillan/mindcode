package info.teksol.mindcode.cmdline;

import info.teksol.mc.profile.CompilerProfile;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public enum ToolAppAction {
    COMPILE_MINDCODE("cm", new CompileMindcodeAction()),
    DECOMPILE_MLOG("dm", new DecompileMlogAction()),
    COMPILE_SCHEMA("cs", new CompileSchemacodeAction()),
    DECOMPILE_SCHEMA("ds", new DecompileSchemacodeAction()),
    ;

    private final String shortcut;
    private final ActionHandler handler;

    ToolAppAction(String shortcut, ActionHandler handler) {
        this.shortcut = shortcut;
        this.handler = handler;
    }

    public Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        return handler.appendSubparser(subparsers, inputFileType, defaults);
    }

    public String getShortcut() {
        return shortcut;
    }

    void handle(Namespace namespace) {
        handler.handle(namespace);
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
