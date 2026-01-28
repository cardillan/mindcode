package info.teksol.mindcode.cmdline;

import info.teksol.mc.Version;
import info.teksol.mc.profile.CompilerProfile;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.DefaultSettings;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.*;
import org.jspecify.annotations.NullMarked;

import java.util.EnumMap;
import java.util.Map;

@NullMarked
public class Main {

    public static final Map<ToolAppAction, Subparser> ACTION_PARSERS = new EnumMap<>(ToolAppAction.class);

    private static final CompilerProfile defaults = CompilerProfile.fullOptimizations(false, false);

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
                .version("Mindcode/Schemacode command-line compiler, version " + Version.getVersion())
                .description("Mindcode/Schemacode command-line compiler.");

        parser.addArgument("-v", "--version")
                .action(Arguments.version()) // Use the version action
                .help("show program's version number and exit");

        Subparsers subparsers = parser.addSubparsers()
                .title("Actions")
                .description("Specifies the type of processing to be performed")
                .help("Type of processing")
                .metavar("ACTION")
                .dest("action");

        for (ToolAppAction action : ToolAppAction.values()) {
            Main.ACTION_PARSERS.put(action, action.appendSubparser(subparsers, inputFileType, defaults));
        }

        return parser;
    }

    static void handleCommandLine(ArgumentParser parser, String[] args) {
        try {
            Namespace arguments = parser.parseArgs(args);
            ToolAppAction.fromShortcut(arguments.get("action")).handle(arguments);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (ProcessingException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
