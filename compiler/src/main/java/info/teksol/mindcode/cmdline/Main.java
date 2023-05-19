package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.DefaultSettings;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ArgumentParser parser = createArgumentParser(Arguments.fileType().verifyCanRead());
        handleCommandLine(parser, args);
    }

    static ArgumentParser createArgumentParser(FileArgumentType inputFileType) {
        ArgumentParser parser = ArgumentParsers.newFor("mindcode", DefaultSettings.VERSION_0_9_0_DEFAULT_SETTINGS)
                .singleMetavar(true)
                .defaultFormatWidth(79)
                //.defaultFormatWidth(120)
                .terminalWidthDetection(true)
                .build()
                .description("Mindcode/Schemacode command-line compiler.");

        Subparsers subparsers = parser.addSubparsers()
                .title("Actions")
                .description("Specifies the compilation type to be performed")
                .help("Type of compilation")
                .metavar("ACTION")
                .dest("action");

        Arrays.asList(Action.values()).forEach(action -> action.configureSubparsers(subparsers, inputFileType));

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

    private enum Action {
        cm(new CompileMindcodeAction()),
        cs(new CompileSchemacodeAction()),
        ds(new DecompileSchemacodeAction()),
        ;

        private final ActionHandler handler;

        Action(ActionHandler handler) {
            this.handler = handler;
        }

        public void configureSubparsers(Subparsers subparsers, FileArgumentType inputFileType) {
            handler.configureSubparsers(subparsers, inputFileType);
        }

        void handle(Namespace namespace) {
            handler.handle(namespace);
        }
    }
}