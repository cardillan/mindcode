package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mindcode.cmdline.Main.Action;
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
import java.util.List;
import java.util.Map;

@NullMarked
public class CompileSchemacodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(Action.COMPILE_SCHEMA.getShortcut())
                .aliases("compile-schema", "compile-schematic")
                .description("Compile a schematic definition file into binary msch file.")
                .help("Compile a schematic definition file into binary msch file.");

        subparser.addArgument("-c", "--clipboard")
                .help("encode the created schematic into text representation and paste into clipboard")
                .action(Arguments.storeTrue());

        ArgumentGroup files = subparser.addArgumentGroup("Input/output files");

        files.addArgument("input")
                .help("Schematic definition file to be compiled into a binary msch file.")
                .nargs("?")
                .type(inputFileType.acceptSystemIn())
                .setDefault(new File("-"));

        files.addArgument("-o", "--output")
                .help("Output file to receive the resulting binary Mindustry schematic file (.msch).")
                .nargs("?")
                .type(Arguments.fileType().verifyCanCreate());

        files.addArgument("--output-directory")
                .dest("output-directory")
                .help("show program's version number and exit")
                .type(Arguments.fileType().verifyIsDirectory());

        files.addArgument("-l", "--log")
                .help("output file to receive compiler messages; uses stdout/stderr when not specified")
                .nargs("?")
                .type(Arguments.fileType().verifyCanCreate())
                .setDefault(new File("-"));

        addCompilerOptions(files, options, OptionCategory.INPUT_OUTPUT);

        addAllCompilerOptions(subparser, options,
                OptionCategory.SCHEMATICS,
                OptionCategory.ENVIRONMENT,
                OptionCategory.MLOG_FORMAT,
                OptionCategory.COMPILER,
                OptionCategory.OPTIMIZATIONS,
                OptionCategory.OPTIMIZATION_LEVELS,
                OptionCategory.DEBUGGING,
                OptionCategory.RUN);

        return subparser;
    }

    private @Nullable File resolveSdfOutputFile(@Nullable File cmdLineFile, String sdfFile) {
        return cmdLineFile != null ? cmdLineFile : sdfFile.isEmpty() ? null : new File(sdfFile);
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile compilerProfile = createCompilerProfile(arguments);
        final File inputFile = arguments.get("input");
        final Path basePath = isStdInOut(inputFile) ? Paths.get("") : inputFile.toPath().toAbsolutePath().normalize().getParent();
        final InputFiles inputFiles = InputFiles.create(basePath);
        readFile(inputFiles, inputFile);

        final CompilerOutput<byte[]> result = SchemacodeCompiler.compile(inputFiles, compilerProfile);

        final File outputDirectory = arguments.get("output-directory");
        final File outputFile = resolveSdfOutputFile(arguments.get("output"), result.fileName());
        final File outputFileLog = arguments.get("log");

        final File output = resolveOutputFile(inputFile, outputDirectory, outputFile, ".msch");
        final File logFile = resolveOutputFile(inputFile, outputDirectory, outputFileLog, ".log");
        final PositionFormatter positionFormatter = sp -> sp.formatForIde(compilerProfile.getFileReferences());

        if (!result.hasErrors()) {
            writeOutput(output, result.output());

            if (arguments.getBoolean("clipboard")) {
                writeToClipboard(Base64.getEncoder().encodeToString(result.output()));
                result.addMessage(ToolMessage.info("\nCreated schematic was copied to the clipboard."));
            }

            outputMessages(result, output, logFile, positionFormatter);
        } else {
            // Errors: print just them into stderr
            List<String> errors = result.errors(m -> m.formatMessage(positionFormatter));

            errors.forEach(System.err::println);
            if (!isStdInOut(logFile)) {
                writeOutput(logFile, errors, true);
            }
            System.exit(1);
        }
    }
}

