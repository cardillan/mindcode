package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.*;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class ActionHandler {
    private final List<BiConsumer<CompilerProfile, Namespace>> optionSetters = new ArrayList<>();

    abstract Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults);

    abstract void handle(Namespace arguments);

    void addAllCompilerOptions(Subparser subparser, CompilerProfile defaults) {
        addCompilerOptions(subparser, defaults);
        addOptimizationOptions(subparser, defaults);
        addDebugOptions(subparser, defaults);
    }

    protected interface OptionSetter {
        void set(CompilerProfile profile, Namespace arguments, String optionName);
    }

    protected Argument createArgument(ArgumentContainer container, CompilerProfile defaults,
            Function<CompilerProfile, Object> optionGetter, OptionSetter optionSetter, String... nameOrFlags) {
        Argument argument = container.addArgument(nameOrFlags);
        String optionName = extractOptionName(nameOrFlags);
        optionSetters.add((profile, arguments) -> optionSetter.set(profile, arguments, optionName));
        return argument.dest(optionName).setDefault(optionGetter.apply(defaults));
    }

    protected String extractOptionName(String... nameOrFlags) {
        return nameOrFlags[nameOrFlags.length - 1].substring(2);
    }

    void addCompilerOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup container = subparser.addArgumentGroup("compiler options");

        createArgument(container, defaults,
                CompilerProfile::getTarget,
                (profile, arguments, name) -> profile.setTarget(arguments.get(name)),
                "-t", "--target")
                .help("selects target processor version and edition ('w' suffix specifies the world processor)")
                .type(String.class)
                .choices(new CaseInsensitiveChoices(ProcessorVersion.getPossibleVersions()));

        createArgument(container, defaults,
                CompilerProfile::getBuiltinEvaluation,
                (profile, arguments, name) -> profile.setBuiltinEvaluation(arguments.get(name)),
                "--builtin-evaluation")
                .help("sets the level of compile-time evaluation of numeric builtin constants")
                .type(LowerCaseEnumArgumentType.forEnum(BuiltinEvaluation.class));

        createArgument(container, defaults,
                CompilerProfile::isTargetGuard,
                (profile, arguments, name) -> profile.setTargetGuard(arguments.getBoolean(name)),
                "--target-guard")
                .help("generates guard code at the beginning of the program ensuring the processor runs in the " +
                        "Mindustry version compatible with the 'target' and 'builtin-evaluation' options")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                CompilerProfile::getSyntacticMode,
                (profile, arguments, name) -> profile.setSyntacticMode(arguments.get(name)),
                "-y", "--syntax")
                .help("specifies syntactic mode used to compile the source code")
                .type(LowerCaseEnumArgumentType.forEnum(SyntacticMode.class));

        createArgument(container, defaults,
                CompilerProfile::getInstructionLimit,
                (profile, arguments, name) -> profile.setInstructionLimit(arguments.getInt(name)),
                "-i", "--instruction-limit")
                .help("sets the maximal number of instructions for the speed optimizations")
                .type(Integer.class)
                .choices(Arguments.range(1, CompilerProfile.MAX_INSTRUCTIONS_CMDLINE));

        createArgument(container, defaults,
                CompilerProfile::getGoal,
                (profile, arguments, name) -> profile.setGoal(arguments.get(name)),
                "-g", "--goal")
                .help("sets code generation goal: minimize code size, minimize execution speed, or no specific preference")
                .type(LowerCaseEnumArgumentType.forEnum(GenerationGoal.class));

        createArgument(container, defaults,
                CompilerProfile::getOptimizationPasses,
                (profile, arguments, name) -> profile.setOptimizationPasses(arguments.getInt(name)),
                "-e", "--passes")
                .help("sets maximal number of optimization passes to be made")
                .type(Integer.class)
                .choices(Arguments.range(1, CompilerProfile.MAX_PASSES_CMDLINE));

        createArgument(container, defaults,
                CompilerProfile::isUnsafeCaseOptimization,
                (profile, arguments, name) -> profile.setUnsafeCaseOptimization(arguments.getBoolean(name)),
                "--unsafe-case-optimization")
                .help("omits range checking of case expressions without an else branch during optimization")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                CompilerProfile::isTextJumpTables,
                (profile, arguments, name) -> profile.setTextJumpTables(arguments.getBoolean(name)),
                "--text-jump-tables")
                .help("when active, generates jump tables by encoding instruction addresses into a single String value, and uses " +
                      "a single 'read' instruction to directly set the counter to the target address (target 8 or higher required)")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                CompilerProfile::getCaseOptimizationStrength,
                (profile, arguments, name) -> profile.setCaseOptimizationStrength(arguments.getInt(name)),
                "--case-optimization-strength")
                .help("sets the strength of case switching optimization: higher number means more case configurations are considered," +
                        "potentially producing a more efficient code, at the cost of longer compilation time")
                .type(Integer.class)
                .choices(Arguments.range(0, CompilerProfile.MAX_CASE_OPTIMIZATION_STRENGTH_CMDLINE));

        createArgument(container, defaults,
                CompilerProfile::isNullCounterIsNoop,
                (profile, arguments, name) -> profile.setNullCounterIsNoop(arguments.getBoolean(name)),
                "--null-counter-is-noop")
                .help("when active, Mindcode assumes assigning a 'null' to '@counter' is ignored by the processor " +
                        "and may produce code depending on this behavior")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                CompilerProfile::isMlogBlockOptimization,
                (profile, arguments, name) -> profile.setMlogBlockOptimization(arguments.getBoolean(name)),
                "--mlog-block-optimization")
                .help("allows (limited) optimization of code inside mlog blocks")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                CompilerProfile::getRemarks,
                (profile, arguments, name) -> profile.setRemarks(arguments.get(name)),
                "-r", "--remarks")
                .help("controls remarks propagation to the compiled code: none (remarks are removed), " +
                      "passive (remarks are not executed), or active (remarks are printed)")
                .type(LowerCaseEnumArgumentType.forEnum(Remarks.class));

        createArgument(container, defaults,
                CompilerProfile::isSymbolicLabels,
                (profile, arguments, name) -> profile.setSymbolicLabels(arguments.getBoolean(name)),
                "--symbolic-labels")
                .help("generate symbolic labels for jump instructions where possible")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                _ -> -1,
                (profile, arguments, name) -> profile.setMlogIndent(arguments.getInt(name)),
                "--mlog-indent")
                .help("the amount of indenting applied to logical blocks in the compiled mlog code")
                .type(Integer.class)
                .choices(Arguments.range(0, CompilerProfile.MAX_MLOG_INDENT));

        createArgument(container, defaults,
                CompilerProfile::getBoundaryChecks,
                (profile, arguments, name) -> profile.setBoundaryChecks(arguments.get(name)),
                "--boundary-checks")
                .help("governs the runtime checks generated by compiler to catch indexes out of bounds when " +
                      "accessing internal array elements")
                .type(LowerCaseEnumArgumentType.forEnum(RuntimeChecks.class));

        createArgument(container, defaults,
                CompilerProfile::isShortFunctionPrefix,
                (profile, arguments, name) -> profile.setShortFunctionPrefix(arguments.getBoolean(name)),
                "--function-prefix")
                .help("specifies the how the function prefix of local variables is generated (either a short common prefix " +
                      "for all functions, or a potentially long prefix derived from function name)")
                .type(Arguments.booleanType("short", "long"));

        createArgument(container, defaults,
                CompilerProfile::isSignature,
                (profile, arguments, name) -> profile.setSignature(arguments.getBoolean(name)),
                "--no-signature")
                .help("prevents appending a signature \"" + CompilerProfile.SIGNATURE + "\" at the end of the final code")
                .action(Arguments.storeFalse());

        createArgument(container, defaults,
                CompilerProfile::isAutoPrintflush,
                (profile, arguments, name) -> profile.setAutoPrintflush(arguments.getBoolean(name)),
                "--printflush")
                .help("automatically add a 'printflush message1' instruction at the end of the program if one is missing")
                .type(Arguments.booleanType());

        createArgument(container, defaults,
                CompilerProfile::getSortVariables,
                (profile, arguments, name) -> profile.setSortVariables(normalizeSortVariables(arguments.get(name))),
                "--sort-variables")
                .help("prepends the final code with instructions which ensure variables are created inside the processor" +
                      " in a defined order. The variables are sorted according to their categories in order, and then alphabetically. " +
                      " Category ALL represents all remaining, not-yet processed variables. When --sort-variables is given without" +
                      " specifying any category, " + SortCategory.usefulCategories() + " are used.")
                .type(LowerCaseEnumArgumentType.forEnum(SortCategory.class))
                .nargs("*")
                .setDefault(List.of(SortCategory.NONE));
    }

    private List<SortCategory> normalizeSortVariables(@Nullable List<SortCategory> sortVariables) {
        if (sortVariables == null || sortVariables.isEmpty()) {
            return SortCategory.getAllCategories();
        } else if (sortVariables.equals(List.of(SortCategory.NONE))) {
            return List.of();
        } else {
            return sortVariables;
        }
    }

    void addOptimizationOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup container = subparser.addArgumentGroup("optimization levels")
                .description("Options to specify global and individual optimization levels. " +
                             "Individual optimizers use global level when not explicitly set. Available optimization levels " +
                             "are {none,basic,advanced}.");

        createArgument(container, defaults,
                profile -> OptimizationLevel.EXPERIMENTAL,
                (profile, arguments, name) -> profile.setAllOptimizationLevels(arguments.get(name)),
                "--optimization")
                .help("sets global optimization level for all optimizers")
                .type(LowerCaseEnumArgumentType.forEnum(OptimizationLevel.class))
                .metavar("LEVEL");

        createArgument(container, defaults,
                profile -> null,
                (profile, arguments, name) -> {
                    if (arguments.get(name) != null) {
                        profile.setAllOptimizationLevels(OptimizationLevel.byIndex(arguments.getInt(name)));
                    }
                },
                "-O")
                .help("sets global optimization level for all optimizers using numeric codes 0-3 (none, basic, advanced, experimental)")
                .choices(Arguments.range(0, 3))
                .type(Integer.class);

        for (Optimization optimization : Optimization.LIST) {
            createArgument(container, defaults,
                    profile -> null,
                    (profile, arguments, name) -> {
                        OptimizationLevel value = arguments.get(name);
                        if (value != null) {
                            profile.setOptimizationLevel(optimization, value);
                        }
                    },
                    "--" + optimization.getOptionName())
                    .help("optimization level of " + optimization.getDescription())
                    .type(LowerCaseEnumArgumentType.forEnum(OptimizationLevel.class))
                    .metavar("LEVEL");
        }
    }

    void addInputOutputOptions(ArgumentContainer container, CompilerProfile defaults) {
        createArgument(container, defaults,
                CompilerProfile::getFileReferences,
                (profile, arguments, name) -> profile.setFileReferences(arguments.get(name)),
                "--file-references")
                .help("specifies the format in which a reference to a location in a source file is output on console and into the log")
                .type(LowerCaseEnumArgumentType.forEnum(FileReferences.class));
    }

    void addDebugOptions(Subparser subparser, CompilerProfile defaults) {
        ArgumentGroup container = subparser.addArgumentGroup("debug output options");

        createArgument(container, defaults,
                CompilerProfile::getParseTreeLevel,
                (profile, arguments, name) -> profile.setParseTreeLevel(arguments.getInt(name)),
                "-p", "--parse-tree")
                .help("sets the detail level of parse tree output into the log file, 0 = off")
                .type(Integer.class)
                .choices(Arguments.range(0, 2));

        createArgument(container, defaults,
                CompilerProfile::getDebugLevel,
                (profile, arguments, name) -> profile.setDebugLevel(arguments.getInt(name)),
                "-d", "--debug-messages")
                .help("sets the detail level of debug messages, 0 = off")
                .type(Integer.class)
                .choices(Arguments.range(0, 3));

        createArgument(container, defaults,
                CompilerProfile::getFinalCodeOutput,
                (profile, arguments, name) -> profile.setFinalCodeOutput(arguments.get(name)),
                "-u", "--print-unresolved")
                .help("activates output of the unresolved code (before virtual instructions resolution) of given type" +
                      " (instruction numbers are included in the output)")
                .type(LowerCaseEnumArgumentType.forEnum(FinalCodeOutput.class))
                .nargs("?")
                .setConst(FinalCodeOutput.PLAIN);

        createArgument(container, defaults,
                CompilerProfile::isPrintStackTrace,
                (profile, arguments, name) -> profile.setPrintStackTrace(arguments.getBoolean(name)),
                "-s", "--stacktrace")
                .help("outputs a stack trace onto stderr when an unhandled exception occurs")
                .action(Arguments.storeTrue());
    }

    protected CompilerProfile createCompilerProfile(Namespace arguments) {
        CompilerProfile profile = CompilerProfile.fullOptimizations(false);
        for (BiConsumer<CompilerProfile, Namespace> setter : optionSetters) {
            setter.accept(profile, arguments);
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

    static void readFile(InputFiles inputFiles, File file) {
        String source = readInput(file);
        Path path = isStdInOut(file) ? Path.of("") : file.toPath();
        inputFiles.registerFile(path, source);
    }

    static void readFile(InputFiles inputFiles, File file, ExcerptSpecification excerpt) {
        String source = readInput(file);
        Path path = isStdInOut(file) ? Path.of("") : file.toPath();
        inputFiles.registerFile(path, excerpt == null ? source : excerpt.apply(source));
    }

    static String readInput(File inputFile) {
        if (isStdInOut(inputFile)) {
            return readStdin();
        } else {
            try {
                return Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ProcessingException(e, "Error reading file '%s'.", inputFile.getPath());
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

    static void writeOutputToFile(File outputFile, List<String> data) {
        try {
            Files.write(outputFile.toPath(), data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ProcessingException(e, "Error writing file %s.", outputFile.getPath());
        }
    }

    static void writeOutput(File outputFile, List<String> data, boolean useErrorOutput) {
        if (isStdInOut(outputFile)) {
            data.forEach(useErrorOutput ? System.err::println : System.out::println);
        } else {
            writeOutputToFile(outputFile, data);
        }
    }

    static void writeOutput(File outputFile, String data) {
        writeOutput(outputFile, List.of(data), false);
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

    static void outputMessages(CompilerOutput<?> result, File outputFile, File logFile, PositionFormatter positionFormatter) {
        // If mlog gets written to stdout, write log to stderr
        if (isStdInOut(logFile)) {
            boolean alwaysErr = isStdInOut(outputFile);
            result.messages().forEach(m -> (alwaysErr || m.isErrorOrWarning() ? System.err : System.out).println(m.formatMessage(positionFormatter)));
        } else {
            writeOutput(logFile, result.texts(m -> m.formatMessage(positionFormatter)), isStdInOut(outputFile));
            // Print errors and warnings to stderr anyway
            result.messages().stream()
                    .filter(m -> m.isErrorOrWarning() || m.isInfo())
                    .forEach(m -> (m.isErrorOrWarning() ? System.err : System.out).println(m.formatMessage(positionFormatter)));
        }

    }

    static ConsoleMessageLogger createMessageLogger(File outputFile, File logFile, PositionFormatter positionFormatter) {
        if (isStdInOut(logFile)) {
            // If mlog gets written to stdout, write log to stderr
            return new ConsoleMessageLogger(positionFormatter, MessageLevel.DEBUG, isStdInOut(outputFile));
        } else {
            return new ConsoleMessageLogger(positionFormatter, MessageLevel.INFO, isStdInOut(outputFile));
        }
    }
}
