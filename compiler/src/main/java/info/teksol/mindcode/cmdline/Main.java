package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.DefaultSettings;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

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
                .description("Specifies the compilation type to be performed")
                .help("Type of compilation")
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
            Action.valueOf(arguments.get("action")).handle(arguments);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (ProcessingException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public enum Action {
        COMPILE_MINDCODE(new CompileMindcodeAction()),
        COMPILE_SCHEMA(new CompileSchemacodeAction()),
        DECOMPILE_SCHEMA(new DecompileSchemacodeAction()),
        ;

        private final ActionHandler handler;

        Action(ActionHandler handler) {
            this.handler = handler;
        }

        public Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType) {
            return handler.appendSubparser(subparsers, inputFileType);
        }

        void handle(Namespace namespace) {
            handler.handle(namespace);
        }
    }
}