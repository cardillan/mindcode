package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherClient;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherCommand;
import info.teksol.schemacode.SchemacodeCompiler;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

@NullMarked
public class CompileSchemacodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(ToolAppAction.COMPILE_SCHEMA.getShortcut())
                .aliases("compile-schema", "compile-schematic")
                .description("Compile a schematic definition file into binary msch file.")
                .help("Compile a schematic definition file into binary msch file.");

        subparser.addArgument("-c", "--clipboard")
                .help("encode the created schematic into text representation and paste into clipboard")
                .action(Arguments.storeTrue());

        addMlogWatcherOptions(subparser, ToolAppAction.COMPILE_SCHEMA);

        ArgumentGroup files = subparser.addArgumentGroup("Input/output files");

        files.addArgument("input")
                .help("Schematic definition file to be compiled into a binary msch file; uses stdin when not specified.")
                .nargs("?")
                .type(inputFileType.acceptSystemIn())
                .setDefault(new File("-"));

        addOutputFileOption(files, false,
                "output file to receive the resulting binary Mindustry schematic file (.msch).",
                "-o", "--output");

        addOutputFileOption(files, true,
                "output file to receive compiler messages; uses input file with .log extension when no file is specified.",
                "-l", "--log");

        addOutputDirectoryOption(files);

        addCompilerOptions(files, options, OptionCategory.INPUT_OUTPUT);

        addAllCompilerOptions(subparser, options,
                OptionCategory.SCHEMATICS,
                OptionCategory.ENVIRONMENT,
                OptionCategory.MLOG_FORMAT,
                OptionCategory.COMPILER,
                OptionCategory.OPTIMIZATIONS,
                OptionCategory.OPTIMIZATION_LEVELS,
                OptionCategory.DEBUGGING,
                OptionCategory.EMULATOR);

        return subparser;
    }

    private @Nullable File resolveSdfOutputFile(@Nullable File cmdLineFile, String sdfFile) {
        return cmdLineFile != null ? cmdLineFile : sdfFile.isEmpty() ? null : new File(sdfFile);
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile compilerProfile = createCompilerProfile(true, arguments);
        final File inputFile = arguments.get("input");
        final Path basePath = isStdInOut(inputFile) ? Paths.get("") : inputFile.toPath().toAbsolutePath().normalize().getParent();
        final InputFiles inputFiles = InputFiles.create(basePath);
        readFile(inputFiles, inputFile);

        final File outputDirectory = arguments.get("output-directory");
        final File outputFileLog = arguments.get("log");
        final File logFile = resolveOutputFile(inputFile, outputDirectory, outputFileLog, ".log");

        final PositionFormatter positionFormatter = sp -> sp.formatForIde(compilerProfile.getFileReferences());
        ConsoleMessageLogger messageLogger = ConsoleMessageLogger.create(positionFormatter,
                isStdInOut(arguments.get("output")), isStdInOut(logFile));
        EmulatorMessageEmitter emulatorMessages = new EmulatorMessageEmitter(messageLogger);
        ToolMessageEmitter toolMessages = new ToolMessageEmitter(messageLogger);

        final CompilerOutput<byte[]> result = SchemacodeCompiler.compile(messageLogger, inputFiles, compilerProfile);

        final File outputFile = resolveSdfOutputFile(arguments.get("output"), result.fileName());
        final File output = resolveOutputFile(inputFile, outputDirectory, outputFile, ".msch");

        if (result.output() != null) {
            if (result.emulator() != null) {
                processEmulatorResults(emulatorMessages, result.emulator(), compilerProfile.isOutputProfiling());
            }

            writeOutput(output, result.existingOutput());

            String encoded = Base64.getEncoder().encodeToString(result.output());

            if (arguments.getBoolean("clipboard")) {
                writeToClipboard(encoded);
                toolMessages.info("\nCreated schematic was copied to the clipboard.");
            }

            MlogWatcherCommand watcherCommand = getMlogWatcherCommand(arguments);
            MlogWatcherClient mlogWatcherClient = createMlogWatcherClient(arguments, toolMessages);
            if (mlogWatcherClient != null) {
                try {
                    switch (watcherCommand) {
                        case MlogWatcherCommand.UPDATE -> mlogWatcherClient.updateSchematic( encoded, true);
                        case MlogWatcherCommand.ADD -> mlogWatcherClient.updateSchematic( encoded, false);
                        case null, default -> throw new IllegalArgumentException("Invalid value for --watcher: " + arguments.get("watcher"));
                    }
                } finally {
                    mlogWatcherClient.close();
                }
            }
        } else {
            System.exit(1);
        }
    }
}
