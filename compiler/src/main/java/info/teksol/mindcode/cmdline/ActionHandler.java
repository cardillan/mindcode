package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

abstract class ActionHandler {

    abstract void configureSubparsers(Subparsers subparsers, FileArgumentType inputFileType);

    abstract void handle(Namespace arguments);

    void configureMindcodeCompiler(Subparser subparser) {
        ArgumentGroup optimizations = subparser.addArgumentGroup("optimization levels")
                .description("Options to specify global and individual optimization levels. " +
                        "Individual optimizers use global level when not explicitly set. Available optimization levels " +
                        "are {off,basic,aggressive}.");

        optimizations.addArgument("-o", "--optimization")
                .help("sets global optimization level for all optimizers")
                .type(Arguments.caseInsensitiveEnumType(OptimizationLevel.class))
                .metavar("LEVEL")
                .setDefault(OptimizationLevel.AGGRESSIVE);

        for (Optimization optimization : Optimization.LIST) {
            optimizations.addArgument("--" + optimization.getOptionName())
                    .help("optimization level of " + optimization.getDescription())
                    .type(Arguments.caseInsensitiveEnumType(OptimizationLevel.class))
                    .dest(optimization.name())
                    .metavar("LEVEL");
        }

        subparser.addArgument("-t", "--target")
                .help("selects target processor version and edition (version 6, version 7 with standard processor or world processor, version 7 rev. A with standard processor or world processor)")
                .choices("6", "7s", "7w", "7as", "7aw")
                .setDefault("7s");

        subparser.addArgument("-g", "--goal")
                .help("sets code generation goal: minimize code size, minimize execution speed, or choose automatically")
                .type(Arguments.caseInsensitiveEnumType(GenerationGoal.class));

        ArgumentGroup debug = subparser.addArgumentGroup("debug output options");

        debug.addArgument("-p", "--parse-tree")
                .help("sets the detail level of parse tree output into the log file, 0 = off")
                .choices(Arguments.range(0, 2))
                .type(Integer.class)
                .setDefault(0);

        debug.addArgument("-d", "--debug-messages")
                .help("sets the detail level of debug messages, 0 = off")
                .choices(Arguments.range(0, 3))
                .type(Integer.class)
                .setDefault(0);

        debug.addArgument("-r", "--print-virtual")
                .help("prints compiled code before virtual instructions resolution")
                .action(Arguments.storeTrue());

        debug.addArgument("-s", "--stacktrace")
                .help("prints stack trace into stderr when an exception occurs")
                .action(Arguments.storeTrue());
    }

    static CompilerProfile createCompilerProfile(Namespace arguments) {
        CompilerProfile profile = CompilerProfile.fullOptimizations();

        // Setup optimization levels
        profile.setAllOptimizationLevels(arguments.get("optimization"));
        Optimization.LIST.stream()
                .filter(opt -> arguments.get(opt.name()) != null)
                .forEachOrdered(opt -> profile.setOptimizationLevel(opt, arguments.get(opt.name())));

        switch (arguments.getString("target")) {
            case "6"    -> profile.setProcessorVersionEdition(ProcessorVersion.V6, ProcessorEdition.S);
            case "7s"   -> profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.S);
            case "7w"   -> profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.W);
            case "7as"  -> profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.S);
            case "7aw"  -> profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.W);
        }

        profile.setParseTreeLevel(arguments.getInt("parse_tree"));
        profile.setDebugLevel(arguments.getInt("debug_messages"));
        profile.setPrintFinalCode(arguments.getBoolean("print_virtual"));
        profile.setPrintStackTrace(arguments.getBoolean("stacktrace"));

        return profile;
    }

    static boolean isStdInOut(File file) {
        return file.getPath().equals("-");
    }

    static File resolveOutputFile(File inputFile, File outputFile, String extension) {
        if (outputFile == null) {
            if (isStdInOut(inputFile)) {
                return inputFile;
            } else {
                String name = inputFile.getName();
                int inputExt = name.lastIndexOf('.');
                if (inputExt < 0) {
                    return new File(inputFile.getPath() + extension);
                } else {
                    String path = inputFile.getPath();
                    int outputExt = inputExt - name.length() + path.length();
                    return new File(path.substring(0, outputExt) + extension);
                }
            }
        } else {
            return outputFile;
        }
    }

    static String readInput(File inputFile) {
        if (isStdInOut(inputFile)) {
            return readStdin();
        } else {
            try {
                return Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ProcessingException(e, "Error reading file %s.", inputFile.getPath());
            }
        }
    }

    static String readStdin() {
        try {
            // Not exactly sure about encodings here. Keep the default.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.lines().collect(Collectors.joining("\n"));
        } catch (UncheckedIOException e) {
            throw new ProcessingException(e, "Error reading from stdin.");
        }
    }

    static void writeOutput(File outputFile, List<String> data, boolean useErrorOutput) {
        if (isStdInOut(outputFile)) {
            data.forEach(useErrorOutput ? System.err::println : System.out::println);
        } else {
            try {
                Files.write(outputFile.toPath(), data, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ProcessingException(e, "Error writing file %s.", outputFile.getPath());
            }
        }
    }

    static void writeOutput(File outputFile, String data, boolean useErrorOutput) {
        writeOutput(outputFile, List.of(data), useErrorOutput);
    }

    static void writeOutput(File outputFile, byte[] data) {
        try {
            Files.write(outputFile.toPath(), data);
        } catch (IOException e) {
            throw new ProcessingException(e, "Error writing file %s.", outputFile.getPath());
        }
    }

    static void writeToClipboard(String string) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(string);
        c.setContents(data, data);
    }
}
