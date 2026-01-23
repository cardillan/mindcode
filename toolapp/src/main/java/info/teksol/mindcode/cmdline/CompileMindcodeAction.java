package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@NullMarked
public class CompileMindcodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(Action.COMPILE_MINDCODE.getShortcut())
                .aliases("compile-mindcode")
                .description("Compile a Mindcode source file into text mlog file.")
                .help("Compile a Mindcode source file into text mlog file.");

        subparser.addArgument("-c", "--clipboard")
                .help("copy compiled mlog code to clipboard")
                .action(Arguments.storeTrue());

        addMlogWatcherOptions(subparser, false);

        ArgumentGroup files = subparser.addArgumentGroup("Input/output files");

        files.addArgument("input")
                .help("Mindcode file to be compiled into an mlog file; uses stdin when not specified")
                .nargs("?")
                .type(inputFileType.acceptSystemIn())
                .setDefault(new File("-"));

        files.addArgument("--excerpt")
                .help("Allows to specify a portion of the input file for processing, parts outside the specified excerpt are ignored. " +
                      "The excerpt needs to be specified as 'line:column-line:column' (':column' may be omitted if it is equal to 1), " +
                      "giving two positions inside the main input file separated by a dash. The start position must precede " +
                      "the end position.")
                .type(ExcerptSpecification.class)
                .nargs("?");

        files.addArgument("-o", "--output")
                .help("Output file to receive compiled mlog code; uses input file with .mlog extension when not specified, " +
                        "or stdout when input is stdin. Use \"-\" to force stdout output.")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        files.addArgument("--output-directory")
                .dest("output-directory")
                .help("show program's version number and exit")
                .type(Arguments.fileType().verifyIsDirectory());

        files.addArgument("-l", "--log")
                .help("Output file to receive compiler messages; uses input file with .log extension when no file is specified.")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate())
                .nargs("?")
                .setDefault(new File("-"));

        addCompilerOptions(files, options, OptionCategory.INPUT_OUTPUT);

        files.addArgument("-a", "--append")
                .help("Additional Mindcode source file to be compiled along with the input file. Such additional files may " +
                      "contain common functions. More than one file may be added this way. The excerpt argument isn't applied to " +
                      "additional files.")
                .type(Arguments.fileType())
                .nargs("+")
                .metavar("FILE");

        addAllCompilerOptions(subparser, options,
                OptionCategory.ENVIRONMENT,
                OptionCategory.MLOG_FORMAT,
                OptionCategory.COMPILER,
                OptionCategory.OPTIMIZATIONS,
                OptionCategory.OPTIMIZATION_LEVELS,
                OptionCategory.DEBUGGING,
                OptionCategory.EMULATOR);

        return subparser;
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile globalProfile = createCompilerProfile(arguments);
        File baseFile = arguments.get("input");
        List<File> others = arguments.get("append");
        ExcerptSpecification excerpt = arguments.get("excerpt");
        if (excerpt != null) {
            globalProfile.setPositionTranslator(excerpt.toPositionTranslator());
        }

        final Path basePath = isStdInOut(baseFile) ? Paths.get("") : baseFile.toPath().toAbsolutePath().normalize().getParent();
        final InputFiles inputFiles = InputFiles.create(basePath);
        readFile(inputFiles, baseFile, excerpt);
        if (others != null) {
            others.forEach(file -> readFile(inputFiles, file));
        }

        final File inputFile = arguments.get("input");
        final File outputDirectory = arguments.get("output-directory");
        final File outputFile = arguments.get("output");
        final File outputFileLog = arguments.get("log");

        final File output = resolveOutputFile(inputFile, outputDirectory, outputFile, ".mlog");
        final File logFile = resolveOutputFile(inputFile, outputDirectory, outputFileLog, ".log");
        final PositionFormatter positionFormatter = sp -> sp.formatForIde(globalProfile.getFileReferences());

        ConsoleMessageLogger messageLogger = createMessageLogger(output, logFile, positionFormatter);
        EmulatorMessageEmitter emulatorMessages = new EmulatorMessageEmitter(messageLogger);
        ToolMessageEmitter toolMessages = new ToolMessageEmitter(messageLogger);
        MindcodeCompiler compiler = new MindcodeCompiler(messageLogger, globalProfile, inputFiles);
        compiler.compile();

        if (!compiler.hasCompilerErrors()) {
            writeOutput(output, compiler.getOutput());

            if (arguments.getBoolean("clipboard")) {
                if (compiler.getOutput().indexOf(0) >= 0) {
                    toolMessages.error("Compiled mlog code contains zero characters, can't copy to clipboard.");
                } else {
                    writeToClipboard(compiler.getOutput());
                    toolMessages.info("Compiled mlog code was copied to the clipboard.");
                }
            }

            if (arguments.getBoolean("watcher")) {
                int port = arguments.getInt("watcher_port");
                int timeout = arguments.getInt("watcher_timeout");
                MlogWatcherClient.sendMlog(port, timeout, messageLogger, compiler.getOutput());
            }

            if (globalProfile.isRun()) {
                emulatorMessages.info("");
                emulatorMessages.info("Program output (%,d steps):", compiler.getSteps());
                String textBufferOutput = compiler.getTextBufferOutput();
                if (!textBufferOutput.isEmpty()) {
                    emulatorMessages.info(textBufferOutput);
                } else {
                    emulatorMessages.info("The program didn't generate any output.");
                }
                if (!compiler.getAssertions().isEmpty()) {
                    emulatorMessages.info("The program generated the following assertions:");
                    compiler.getAssertions().forEach(a -> messageLogger.addMessage(a.createMessage()));
                }

                if (globalProfile.isOutputProfiling()) {
                    int[] executionProfile = compiler.getExecutionProfile();
                    if (executionProfile.length >= compiler.getExecutableInstructions().size()) {
                        String profileResult = LogicInstructionPrinter.toStringWithProfiling(compiler.instructionProcessor(),
                                compiler.getExecutableInstructions(), false, 0, executionProfile);
                        emulatorMessages.debug("\n\nCode profiling result:\n\n%s", profileResult);
                    }
                }
            }
        }

        if (!isStdInOut(logFile)) {
            List<String> errors = messageLogger.getMessages().stream().map(m -> m.formatMessage(positionFormatter)).toList();
            writeOutputToFile(logFile, errors);
        }

        if (compiler.hasCompilerErrors()) {
            System.exit(1);
        }
    }
}
