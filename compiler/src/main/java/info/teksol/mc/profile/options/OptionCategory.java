package info.teksol.mc.profile.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum OptionCategory {
    INPUT_OUTPUT("input/output options", """
            """),

    SCHEMATICS("schematic creation", """
            """),

    ENVIRONMENT("environment options", """
            Options to specify the target environment for the code being compiled. This includes the Mindustry version,
            as well as some specific processor features that may or may not be used.
            """),

    MLOG_FORMAT("mlog formatting options", """
            These options affect the way the mlog code is generated and formatted.
            """),

    COMPILER("compiler options", """
            Specifies options that affect the way the source code is compiled.
            """),

    OPTIMIZATIONS("optimization options", """
            Options to affecting optimization of the compiled code, or activating/deactivating specific
            optimization actions.
            """),

    OPTIMIZATION_LEVELS("optimization levels", """
            Options to specify global and individual optimization levels. Individual optimizers use global level
            when not explicitly set. Available optimization levels are {none,basic,advanced, experimental}.
            """),

    DEBUGGING("debugging options", """
            Options to activate debugging features or additional output from the compiler.
            """),

    RUN("run options", """
            Options to specify if and how to run the compiled code on an emulated processor. The emulated
            processor is much faster than Mindustry processors, but can't run instructions which obtain information
            from the Mindustry World. Sole exceptions are memory cells (cell1 to cell9) and memory banks
            (bank1 to bank9), which can be read and written.
            """),

    ;

    public final String title;
    public final String description;

    OptionCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
