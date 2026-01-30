package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherClient;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherCommand;
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

import static info.teksol.mindcode.cmdline.mlogwatcher.api.UpdateProcessorsOnMapParams.*;

@NullMarked
public class CompileMindcodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(ToolAppAction.COMPILE_MINDCODE.getShortcut())
                .aliases("compile-mindcode")
                .description("Compile a Mindcode source file into text mlog file.")
                .help("Compile a Mindcode source file into text mlog file.");

        subparser.addArgument("-c", "--clipboard")
                .help("copy compiled mlog code to clipboard")
                .action(Arguments.storeTrue());

        addMlogWatcherOptions(subparser, ToolAppAction.COMPILE_MINDCODE);

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
                .help("output file to receive compiled mlog code; uses input file with .mlog extension when not specified, " +
                        "or stdout when input is stdin. Use \"-\" to force stdout output.")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        files.addArgument("--output-directory")
                .dest("output-directory")
                .help("specifies the directory where the output files will be placed")
                .type(Arguments.fileType().verifyIsDirectory());

        files.addArgument("-l", "--log")
                .help("output file to receive additional procesing messages; uses input file with .log extension when no file is specified.")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate())
                .nargs("?");

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
        CompilerProfile globalProfile = createCompilerProfile(false, arguments);
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

        ConsoleMessageLogger messageLogger = ConsoleMessageLogger.create(positionFormatter, output, logFile);
        EmulatorMessageEmitter emulatorMessages = new EmulatorMessageEmitter(messageLogger);
        ToolMessageEmitter toolMessages = new ToolMessageEmitter(messageLogger);
        MindcodeCompiler compiler = new MindcodeCompiler(messageLogger, globalProfile, inputFiles);
        compiler.compile();

        if (!compiler.hasCompilerErrors()) {
            if (globalProfile.isRun()) {
                processEmulatorResults(emulatorMessages, compiler.getEmulator(), globalProfile.isOutputProfiling());
            }

            writeOutput(output, compiler.getOutput());

            if (arguments.getBoolean("clipboard")) {
                if (compiler.getOutput().indexOf(0) >= 0) {
                    toolMessages.error("Compiled mlog code contains zero characters, can't copy to clipboard.");
                } else {
                    writeToClipboard(compiler.getOutput());
                    toolMessages.info("Compiled mlog code was copied to the clipboard.");
                }
            }

            MlogWatcherClient mlogWatcherClient = createMlogWatcherClient(arguments, toolMessages,
                    globalProfile.isPrintStackTrace());
            if (mlogWatcherClient != null) {
                try {
                    switch (arguments.get("watcher")) {
                        case MlogWatcherCommand.UPDATE -> mlogWatcherClient.updateSelectedProcessor(compiler.getOutput());
                        case MlogWatcherCommand.UPDATE_ALL -> updateAll(mlogWatcherClient, compiler, VERSION_SELECTION_EXACT);
                        case MlogWatcherCommand.UPGRADE_ALL -> updateAll(mlogWatcherClient, compiler, VERSION_SELECTION_COMPATIBLE);
                        case MlogWatcherCommand.FORCE_UPDATE_ALL -> updateAll(mlogWatcherClient, compiler, VERSION_SELECTION_ANY);
                        default -> throw new IllegalArgumentException("Invalid value for --watcher: " + arguments.get("watcher"));
                    }
                } finally {
                    mlogWatcherClient.close();
                }
            }
        }

        if (!isStdInOut(logFile)) {
            List<String> messages = messageLogger.getMessages().stream().map(m -> m.formatMessage(positionFormatter)).toList();
            writeOutputToFile(logFile, messages);
        }

        if (compiler.hasCompilerErrors()) {
            System.exit(1);
        }
    }

    private void updateAll(MlogWatcherClient mlogWatcherClient, MindcodeCompiler compiler, String versionSelection) {
        if (compiler.getProgramId() == null) {
            compiler.addMessage(ToolMessage.error("Mlog Watcher: cannot update all processors on the map, no program ID defined."));
        } else {
            mlogWatcherClient.updateProcessorsOnMap(compiler.getOutput(), compiler.getProgramId(), versionSelection);
        }
    }
}
