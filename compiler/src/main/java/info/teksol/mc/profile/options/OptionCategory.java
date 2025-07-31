package info.teksol.mc.profile.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum OptionCategory {
    INPUT_OUTPUT("Input/output options", """
            """),

    SCHEMATICS("Schematic creation", """
            """),

    ENVIRONMENT("Environment options", """
            Options to specify the target environment for the code being compiled. This includes the Mindustry version,
            as well as prescribing which specific processor features may or may not be used.
            """),

    MLOG_FORMAT("Mlog formatting options", """
            Options determining how the mlog code is generated and formatted.
            """),

    COMPILER("Compiler options", """
            Options affecting the way the source code is compiled.
            """),

    OPTIMIZATIONS("Optimization options", """
            Options guiding the overall optimization of the compiled code, or activating/deactivating specific
            optimization actions.
            """),

    OPTIMIZATION_LEVELS("Optimization levels", """
            Options specifying the global and individual optimization levels. Individual optimizers use global level
            when not explicitly set. Available optimization levels are {none,basic,advanced, experimental}.
            """),

    DEBUGGING("Debugging options", """
            Options to activate debugging features or additional output from the compiler.
            """),

    RUN("Run options", """
            Options to specify whether and how to run the compiled code on an emulated processor. The emulated
            processor is much faster than Mindustry processors, but can't run instructions which obtain information
            from the Mindustry World. Sole exceptions are memory cells ('cell1' to 'cell9') and memory banks
            ('bank1' to 'bank9'), which can be read and written.
            """),

    ;

    public final String title;
    public final String description;

    OptionCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
