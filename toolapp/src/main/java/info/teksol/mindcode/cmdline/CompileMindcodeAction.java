package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CompileMindcodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Subparser subparser = subparsers.addParser(Action.COMPILE_MINDCODE.getShortcut())
                .aliases("compile-mindcode")
                .description("Compile a Mindcode source file into text mlog file.")
                .help("Compile a Mindcode source file into text mlog file.");

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

        files.addArgument("--excerpt")
                .help("Allows to specify a portion of the input file as input, parts outside the specified excerpt are ignored. " +
                      "The excerpt needs to be specified as 'line:column-line:column' (':column' may be omitted if it is equal to 1), " +
                      "giving two positions inside the main input file separated by a dash. The start position must precede " +
                      "the end position.")
                .type(ExcerptSpecification.class)
                .nargs("?");

        files.addArgument("-l", "--log")
                .help("Output file to receive compiler messages; uses input file with .log extension when no file is specified.")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate())
                .nargs("?")
                .setDefault(new File("-"));

        files.addArgument("-a", "--append")
                .help("Additional Mindcode source file to be compiled along with the input file. Such additional files may " +
                      "contain common functions. More than one file may be added this way. The excerpt argument isn't applied to " +
                      "additional files.")
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
                        (bank1 to bank9), which can be read and written.""");

        runOptions.addArgument("--run")
                .help("run the compiled code on an emulated processor.")
                .action(Arguments.storeTrue());

        runOptions.addArgument("--run-steps")
                .help("the maximum number of instruction executions to emulate, the execution stops when this limit is reached.")
                .choices(Arguments.range(1, 1_000_000_000))
                .type(Integer.class)
                .setDefault(defaults.getStepLimit());

        for (ExecutionFlag flag : ExecutionFlag.LIST) {
            if (flag.isSettable()) {
                runOptions.addArgument("--" + flag.getOptionName())
                        .help(flag.getDescription())
                        .type(Arguments.booleanType())
                        .dest(flag.name());
            }
        }
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile compilerProfile = createCompilerProfile(arguments);
        File baseFile = arguments.get("input");
        List<File> others = arguments.get("append");
        ExcerptSpecification excerpt = arguments.get("excerpt");
        if (excerpt != null) {
            compilerProfile.setPositionTranslator(excerpt.toPositionTranslator());
        }

        final Path basePath = isStdInOut(baseFile) ? Paths.get("") : baseFile.toPath().toAbsolutePath().normalize().getParent();
        final InputFiles inputFiles = InputFiles.create(basePath);
        readFile(inputFiles, baseFile, excerpt);
        if (others != null) {
            others.forEach(file -> readFile(inputFiles, file));
        }

        final File output = resolveOutputFile(arguments.get("input"), arguments.get("output"), ".mlog");
        final File logFile = resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        final PositionFormatter positionFormatter = SourcePosition::formatForIde;

        ConsoleMessageLogger messageLogger = createMessageLogger(output, logFile, positionFormatter);
        MindcodeCompiler compiler = new MindcodeCompiler(messageLogger, compilerProfile, inputFiles);
        compiler.compile();

        if (!messageLogger.hasErrors()) {
            writeOutput(output, compiler.getOutput());

            if (arguments.getBoolean("clipboard")) {
                writeToClipboard(compiler.getOutput());
                messageLogger.info("\nCompiled mlog code was copied to the clipboard.");
            }

            if (arguments.getBoolean("watcher")) {
                int port = arguments.getInt("watcher_port");
                int timeout = arguments.getInt("watcher_timeout");
                MlogWatcherClient.sendMlog(port, timeout, messageLogger, compiler.getOutput());
            }

            if (compilerProfile.isRun()) {
                messageLogger.info("");
                messageLogger.info("Program output (%,d steps):", compiler.getSteps());
                if (!compiler.getTextBuffer().isEmpty()) {
                    messageLogger.info(compiler.getTextBuffer().getFormattedOutput());
                } else {
                    messageLogger.info("The program didn't generate any output.");
                }
                if (!compiler.getAssertions().isEmpty()) {
                    messageLogger.info("The program generated the following assertions:");
                    compiler.getAssertions().forEach(a -> messageLogger.addMessage(a.createMessage()));
                }
                if (compiler.getExecutionException() != null) {
                    messageLogger.error(compiler.getExecutionException().getMessage());
                }
            }
        }

        if (!isStdInOut(logFile)) {
            List<String> errors = messageLogger.getMessages().stream().map(m -> m.formatMessage(positionFormatter)).toList();
            writeOutput(logFile, errors, true);
        }

        if (messageLogger.hasErrors()) {
            System.exit(1);
        }
    }
}
