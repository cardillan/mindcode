package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.DefaultSettings;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.*;

import java.util.EnumMap;
import java.util.Map;

public class Main {

    public static final Map<Action, Subparser> ACTION_PARSERS = new EnumMap<>(Action.class);

    public static void main(String[] args) {
        ArgumentParser parser = createArgumentParser(Arguments.fileType().verifyCanRead(), 79);
        handleCommandLine(parser, args);
    }

    static ArgumentParser createArgumentParser(FileArgumentType inputFileType, int defaultFormatWidth) {
        ArgumentParser parser = ArgumentParsers.newFor("mindcode", DefaultSettings.VERSION_0_9_0_DEFAULT_SETTINGS)
                .singleMetavar(true)
                .defaultFormatWidth(defaultFormatWidth)
                .terminalWidthDetection(true)
                .build()
                .description("Mindcode/Schemacode command-line compiler.");

        Subparsers subparsers = parser.addSubparsers()
                .title("Actions")
                .description("Specifies the type of processing to be performed")
                .help("Type of processing")
                .metavar("ACTION")
                .dest("action");

        for (Action action : Action.values()) {
            Main.ACTION_PARSERS.put(action, action.appendSubparser(subparsers, inputFileType));
        }

        return parser;
    }

    static void handleCommandLine(ArgumentParser parser, String[] args) {
        try {
            Namespace arguments = parser.parseArgs(args);
            Action.fromShortcut(arguments.get("action")).handle(arguments);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (ProcessingException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public enum Action {
        COMPILE_MINDCODE("cm", new CompileMindcodeAction()),
        COMPILE_SCHEMA("cs", new CompileSchemacodeAction()),
        DECOMPILE_SCHEMA("ds", new DecompileSchemacodeAction()),
        ;

        private final String shortcut;
        private final ActionHandler handler;

        Action(String shortcut, ActionHandler handler) {
            this.shortcut = shortcut;
            this.handler = handler;
        }

        public Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType) {
            return handler.appendSubparser(subparsers, inputFileType);
        }

        public String getShortcut() {
            return shortcut;
        }

        void handle(Namespace namespace) {
            handler.handle(namespace);
        }

        static Action fromShortcut(String shortcut) {
            for (Action a : Action.values()) {
                if (shortcut.equals(a.shortcut)) {
                    return a;
                }
            }

            throw new IllegalArgumentException("Unknwon shortcut value " + shortcut);
        }
    }
}