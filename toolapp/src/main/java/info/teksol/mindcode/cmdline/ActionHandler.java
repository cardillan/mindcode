package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.*;
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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

abstract class ActionHandler {

    abstract Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults);

    abstract void handle(Namespace arguments);

    void addAllCompilerOptions(Subparser subparser, CompilerProfile defaults) {
        addCompilerOptions(subparser, defaults);
        addOptimizationOptions(subparser, defaults);
        addDebugOptions(subparser, defaults);
    }

    void addCompilerOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup compiler = subparser.addArgumentGroup("compiler options");

        compiler.addArgument("-t", "--target")
                .help("selects target processor version and edition (version 6, version 7 with standard processor or world processor," +
                        " version 7 rev. A with standard processor or world processor)")
                .choices("6", "7s", "7w", "7as", "7aw")
                .setDefault("7w");

        compiler.addArgument("-i", "--instruction-limit")
                .help("sets the maximal number of instructions for the speed optimizations")
                .choices(Arguments.range(1, CompilerProfile.MAX_INSTRUCTIONS))
                .type(Integer.class)
                .setDefault(CompilerProfile.DEFAULT_INSTRUCTIONS);

        compiler.addArgument("-e", "--passes")
                .help("sets maximal number of optimization passes to be made")
                .choices(Arguments.range(1, CompilerProfile.MAX_PASSES))
                .type(Integer.class)
                .setDefault(CompilerProfile.DEFAULT_CMDLINE_PASSES);

        compiler.addArgument("-g", "--goal")
                .help("sets code generation goal: minimize code size, minimize execution speed, or choose automatically")
                .type(Arguments.caseInsensitiveEnumType(GenerationGoal.class))
                .setDefault(defaults.getGoal());

        compiler.addArgument("-r", "--remarks")
                .help("controls remarks propagation to the compiled code: none (remarks are removed), " +
                        "passive (remarks are not executed), or active (remarks are printed)")
                .type(Arguments.caseInsensitiveEnumType(Remarks.class))
                .setDefault(defaults.getRemarks());

        compiler.addArgument("--sort-variables")
                .help("prepends the final code with instructions which ensure variables are created inside the processor" +
                        " in a defined order. The variables are sorted according to their categories in order, and then alphabetically. " +
                        " Category ALL represents all remaining, not-yet processed variables. When --sort-variables is given without" +
                        " specifying any category, " + SortCategory.usefulCategories()  + " are used.")
                .type(Arguments.caseInsensitiveEnumType(SortCategory.class))
                .nargs("*")
                .setDefault(List.of(SortCategory.NONE));
        /*
        compiler.addArgument("-m", "--memory-model")
                .help("sets model for handling linked memory blocks: volatile (shared with different processor), " +
                        "aliased (a memory block may be accessed through different variables), or restricted " +
                        "(a memory block will never be accessed through different variables)")
                .type(Arguments.caseInsensitiveEnumType(MemoryModel.class))
                .setDefault(defaults.getMemoryModel());
        */

        compiler.addArgument("--no-signature")
                .help("prevents appending a signature \"" + CompilerProfile.SIGNATURE + "\" at the end of the final code")
                .action(Arguments.storeTrue());
    }

    void addOptimizationOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup optimizations = subparser.addArgumentGroup("optimization levels")
                .description("Options to specify global and individual optimization levels. " +
                        "Individual optimizers use global level when not explicitly set. Available optimization levels " +
                        "are {none,basic,advanced}.");

        optimizations.addArgument("-o", "--optimization")
                .help("sets global optimization level for all optimizers")
                .type(Arguments.caseInsensitiveEnumType(OptimizationLevel.class))
                .metavar("LEVEL")
                .setDefault(OptimizationLevel.ADVANCED);

        for (Optimization optimization : Optimization.LIST) {
            optimizations.addArgument("--" + optimization.getOptionName())
                    .help("optimization level of " + optimization.getDescription())
                    .type(Arguments.caseInsensitiveEnumType(OptimizationLevel.class))
                    .dest(optimization.name())
                    .metavar("LEVEL");
        }
    }

    void addDebugOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup debug = subparser.addArgumentGroup("debug output options");

        debug.addArgument("-p", "--parse-tree")
                .help("sets the detail level of parse tree output into the log file, 0 = off")
                .choices(Arguments.range(0, 2))
                .type(Integer.class)
                .setDefault(defaults.getParseTreeLevel());

        debug.addArgument("-d", "--debug-messages")
                .help("sets the detail level of debug messages, 0 = off")
                .choices(Arguments.range(0, 3))
                .type(Integer.class)
                .setDefault(defaults.getDebugLevel());

        debug.addArgument("-u", "--print-unresolved")
                .help("activates output of the unresolved code (before virtual instructions resolution) of given type" +
                        " (instruction numbers are included in the output)")
                .type(Arguments.caseInsensitiveEnumType(FinalCodeOutput.class))
                .nargs("?")
                .setConst(FinalCodeOutput.PLAIN)
                .setDefault(defaults.getFinalCodeOutput());

        debug.addArgument("-s", "--stacktrace")
                .help("prints stack trace into stderr when an exception occurs")
                .action(Arguments.storeTrue());
    }

    static CompilerProfile createCompilerProfile(Namespace arguments) {
        CompilerProfile profile = CompilerProfile.fullOptimizations(false);

        // Setup optimization levels
        profile.setAllOptimizationLevels(arguments.get("optimization"));
        Optimization.LIST.stream()
                .filter(opt -> arguments.get(opt.name()) != null)
                .forEachOrdered(opt -> profile.setOptimizationLevel(opt, arguments.get(opt.name())));

        switch (arguments.getString("target")) {
            case "6" -> profile.setProcessorVersionEdition(ProcessorVersion.V6, ProcessorEdition.S);
            case "7s" -> profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.S);
            case "7w" -> profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.W);
            case "7as" -> profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.S);
            case "7aw" -> profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.W);
        }

        profile.setParseTreeLevel(arguments.getInt("parse_tree"));
        profile.setDebugLevel(arguments.getInt("debug_messages"));
        profile.setInstructionLimit(arguments.get("instruction_limit"));
        profile.setOptimizationPasses(arguments.get("passes"));
        profile.setGoal(arguments.get("goal"));
        profile.setRemarks(arguments.get("remarks"));
        //profile.setMemoryModel(arguments.get("memory_model"));
        profile.setFinalCodeOutput(arguments.get("print_unresolved"));
        profile.setPrintStackTrace(arguments.getBoolean("stacktrace"));

        List<SortCategory> sortVariables = arguments.get("sort_variables");
        if (sortVariables == null || sortVariables.isEmpty()) {
            profile.setSortVariables(SortCategory.getAllCategories());
        } else if (sortVariables.equals(List.of(SortCategory.NONE))) {
            profile.setSortVariables(List.of());
        } else {
            profile.setSortVariables(sortVariables);
        }

        if (arguments.getBoolean("no_signature")) {
            profile.setSignature(false);
        }

        if (arguments.get("run") != null) {
            profile.setRun(arguments.getBoolean("run"));
            profile.setStepLimit(arguments.getInt("run_steps"));
        }

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

    static InputFile readFile(File file, boolean multiple) {
        return new InputFile(
                isStdInOut(file) || !multiple ? "" : file.getPath(),
                isStdInOut(file) ? "" : file.getAbsolutePath(),
                readInput(file));
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
