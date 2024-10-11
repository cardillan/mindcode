package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.cmdline.Main.Action;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static info.teksol.mindcode.compiler.CompilerFacade.compile;

public class CompileMindcodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Subparser subparser = subparsers.addParser(Action.COMPILE_MINDCODE.getShortcut())
                .aliases("compile-mindcode")
                .description("Compile a mindcode source file into text mlog file.")
                .help("Compile a mindcode source file into text mlog file.");

        subparser.addArgument("-c", "--clipboard")
                .help("copy compiled mlog code to clipboard")
                .action(Arguments.storeTrue());

        subparser.addArgument("-w", "--watcher")
                .help("send compiled mlog code to the Mlog Watcher mod in Mindustry (the code will be injected into the selected processor)")
                .action(Arguments.storeTrue());

        subparser.addArgument("--watcher-port")
                .help("port number for communication with Mlog Watcher")
                .choices(Arguments.range(0, 65535))
                .type(Integer.class)
                .setDefault(9992);

        subparser.addArgument("--watcher-timeout")
                .help("timeout in milliseconds when trying to establish a connection to Mlog Watcher")
                .choices(Arguments.range(0, 3_600_000))
                .type(Integer.class)
                .setDefault(500);

        ArgumentGroup files = subparser.addArgumentGroup("input/output files");

        files.addArgument("input")
                .help("Mindcode file to be compiled into an mlog file; uses stdin when not specified")
                .nargs("?")
                .type(inputFileType.acceptSystemIn())
                .setDefault(new File("-"));

        files.addArgument("output")
                .help("Output file to receive compiled mlog code; uses input file with .mlog extension when not specified, " +
                        "or stdout when input is stdin. Use \"-\" to force stdout output.")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        files.addArgument("-l", "--log")
                .help("Output file to receive compiler messages; uses input file with .log extension when no file is specified.")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate())
                .nargs("?")
                .setDefault(new File("-"));

        files.addArgument("-a", "--append")
                .help("Additional Mindcode source file to be compiled along with the input file. Such additional files may " +
                        "contain common functions. More than one file may be added this way.")
                .type(Arguments.fileType())
                .nargs("+")
                .metavar("FILE");

        addCompilerOptions(subparser, defaults);
        addRunOptions(subparser, defaults);
        addOptimizationOptions(subparser, defaults);
        addDebugOptions(subparser, defaults);

        return subparser;
    }

    void addRunOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup runOptions = subparser.addArgumentGroup("run options")
                .description("""
                        Options to specify if and how to run the compiled code on an emulated processor. The emulated \
                        processor is much faster than Mindustry processors, but can't run instructions which obtain information \
                        from the Mindustry World. Sole exceptions are memory cells (cell1 to cell9) and memory banks \
                        (bank1 to bank9), which can be read and written.
                        """);

        runOptions.addArgument("--run")
                .help("run the compiled code on an emulated processor.")
                .action(Arguments.storeTrue());

        runOptions.addArgument("--run-steps")
                .help("the maximum number of instruction executions to emulate, the execution stops when this limit is reached.")
                .choices(Arguments.range(1, 1_000_000_000))
                .type(Integer.class)
                .setDefault(defaults.getStepLimit());
    }

    private InputFile readFile(File file, boolean multiple) {
        return new InputFile(
                isStdInOut(file) || !multiple ? "" : file.getPath(),
                isStdInOut(file) ? "" : file.getAbsolutePath(),
                readInput(file));
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile compilerProfile = createCompilerProfile(arguments);
        List<File> inputs = new ArrayList<>();
        inputs.add(arguments.get("input"));
        List<File> others = arguments.get("append");
        if (others != null) {
            inputs.addAll(others);
        }

        List<InputFile> inputFiles = inputs.stream().map(f -> readFile(f, inputs.size() >1)).toList();

        final CompilerOutput<String> result = compile(inputFiles, compilerProfile);

        File output = resolveOutputFile(arguments.get("input"), arguments.get("output"), ".mlog");
        File logFile = resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        boolean mlogToStdErr = isStdInOut(output);
        Function<InputPosition, String> positionFormatter = InputPosition::formatForIde;

        if (!result.hasErrors()) {
            writeOutput(output, result.output(), false);

            if (arguments.getBoolean("clipboard")) {
                writeToClipboard(result.output());
                result.addMessage(ToolMessage.info("\nCompiled mlog code was copied to the clipboard."));
            }

            if (arguments.getBoolean("watcher")) {
                int port = arguments.getInt("watcher_port");
                int timeout = arguments.getInt("watcher_timeout");
                MlogWatcherClient.sendMlog(port, timeout, result, result.output());
            }

            if (compilerProfile.isRun()) {
                result.addMessage(ToolMessage.info(""));
                result.addMessage(ToolMessage.info("Program output (%,d steps):", result.steps()));
                if (result.textBuffer() == null) {
                    result.addMessage(ToolMessage.error("Couldn't obtain program output."));
                } else if (result.textBuffer().isEmpty()) {
                    result.addMessage(ToolMessage.info("The program didn't generate any output."));
                } else {
                    result.addMessage(ToolMessage.info(result.textBuffer()));
                }
            }

            // If mlog gets written to stdout, write log to stderr
            if (isStdInOut(logFile)) {
                boolean alwaysErr = isStdInOut(output);
                result.messages().forEach(m -> (alwaysErr || m.isErrorOrWarning() ? System.err : System.out).println(m.formatMessage(positionFormatter)));
            } else {
                writeOutput(logFile, result.texts(), mlogToStdErr);
                // Print errors and warnings to stderr anyway
                result.messages().stream()
                        .filter(m -> m.isErrorOrWarning() || m.isInfo())
                        .forEach(m -> (m.isErrorOrWarning() ? System.err : System.out).println(m.formatMessage(positionFormatter)));
            }
        } else {
            // Errors: print just them into stderr
            List<String> errors = result.messages().stream()
                    .filter(MindcodeMessage::isError)
                    .map(m -> m.formatMessage(positionFormatter))
                    .toList();

            errors.forEach(System.err::println);
            if (!isStdInOut(logFile)) {
                writeOutput(logFile, errors, true);
            }
            System.exit(1);
        }
    }
}
