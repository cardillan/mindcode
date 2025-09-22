package info.teksol.mc.profile.options;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
public class CompilerOptionFactory {
    public static final int MAX_INSTRUCTIONS_CMDLINE = 100_000;
    public static final int MAX_INSTRUCTIONS_WEBAPP = 1500;

    public static final int DEFAULT_PASSES_CMDLINE = 25;
    public static final int DEFAULT_PASSES_WEBAPP = 5;
    public static final int MAX_PASSES_CMDLINE = 1000;
    public static final int MAX_PASSES_WEBAPP = 25;

    public static final int DEFAULT_CASE_OPTIMIZATION_STRENGTH_CMDLINE = 3;
    public static final int DEFAULT_CASE_OPTIMIZATION_STRENGTH_WEBAPP = 2;
    public static final int MAX_CASE_OPTIMIZATION_STRENGTH_CMDLINE = 6;
    public static final int MAX_CASE_OPTIMIZATION_STRENGTH_WEBAPP = 4;

    public static final int MAX_MLOG_INDENT = 8;

    public static final int DEFAULT_STEP_LIMIT_CMDLINE = 10_000_000;
    public static final int DEFAULT_STEP_LIMIT_WEBAPP = 1_000_000;
    public static final int MAX_STEP_LIMIT_CMDLINE = 1_000_000_000;
    public static final int MAX_STEP_LIMIT_WEBAPP = 100_000_000;

    public static Map<Enum<?>, CompilerOptionValue<?>> createCompilerOptions(boolean webApp) {
        List<CompilerOptionValue<?>> list = new ArrayList<>();

        addInputOutputOptions(list, webApp);
        addSchematicsOptions(list, webApp);
        addEnvironmentOptions(list, webApp);
        addMlogFormatOptions(list, webApp);
        addCompilerOptions(list, webApp);
        addOptimizationsOptions(list, webApp);
        addOptimizationLevels(list, webApp);
        addDebuggingOptions(list, webApp);
        addRunOptions(list, webApp);

        return list.stream().collect(Collectors.toMap(CompilerOptionValue::getOption, e -> e,
                (v1, v2) -> {throw new IllegalStateException("YOUR MESSAGE HERE");}, LinkedHashMap::new));
    }

    private static void addInputOutputOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.INPUT_OUTPUT;

        list.add(new EnumCompilerOptionValue<>(InputOutputOptions.FILE_REFERENCES, "",
                "specifies the format in which a reference to a location in a source file is output on console and into the log",
                FileReferences.class,
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.COMMAND_LINE, category,
                FileReferences.PATH));
    }

    private static void addSchematicsOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.SCHEMATICS;

        list.add(new StringCompilerOptionValue(SchematicOptions.ADD_TAG, "a",
                "defines additional tag(s) to add to the schematic, plain text and symbolic icon names are supported",
                "TAG",
                OptionMultiplicity.ZERO_OR_MORE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.COMMAND_LINE, category,
                List.of()));
    }

    private static void addEnvironmentOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.ENVIRONMENT;

        list.add(new TargetCompilerOptionValue(EnvironmentOptions.TARGET, "t",
                "selects target processor version and edition (a 'w' suffix specifies the world processor)",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.MODULE,
                OptionAvailability.UNIVERSAL, category,
                new Target(ProcessorVersion.V7A, ProcessorEdition.S)));

        list.add(new IntegerCompilerOptionValue(OptimizationOptions.INSTRUCTION_LIMIT, "i",
                "sets the maximal number of instructions for the speed optimizations",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                1, webApp ? MAX_INSTRUCTIONS_WEBAPP : MAX_INSTRUCTIONS_CMDLINE, 1000));

        list.add(new EnumCompilerOptionValue<>(EnvironmentOptions.BUILTIN_EVALUATION, "",
                "sets the level of compile-time evaluation of numeric builtin constants",
                BuiltinEvaluation.class,
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                BuiltinEvaluation.COMPATIBLE));

        list.add(new BooleanCompilerOptionValue(EnvironmentOptions.NULL_COUNTER_IS_NOOP, "",
                "when active, Mindcode assumes assigning a 'null' to '@counter' is ignored by the processor " +
                "and may produce code depending on this behavior",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                true));
    }

    private static void addMlogFormatOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.MLOG_FORMAT;

        list.add(new BooleanCompilerOptionValue(MlogFormatOptions.SYMBOLIC_LABELS, "",
                "generate symbolic labels for jump instructions where possible",
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                false).setConstValue(true));

        list.add(new IntegerCompilerOptionValue(MlogFormatOptions.MLOG_INDENT, "",
                "the amount of indenting applied to logical blocks in the compiled mlog code",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                0, MAX_MLOG_INDENT, -1));

        list.add(new BooleanCompilerOptionValue(MlogFormatOptions.FUNCTION_PREFIX, "",
                "specifies the how the function prefix of local variables is generated (either a short common prefix " +
                "for all functions, or a potentially long prefix derived from function name)",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                "short", "long", false));

        list.add(new BooleanCompilerOptionValue(MlogFormatOptions.SIGNATURE, "no-signature", "",
                "prevents appending a signature '" + CompilerProfile.SIGNATURE + "' at the end of the final code",
                OptionMultiplicity.ZERO, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.COMMAND_LINE, category,
                true).setConstValue(false));
    }

    private static void addCompilerOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.COMPILER;

        list.add(new EnumCompilerOptionValue<>(CompilerOptions.SYNTAX, "y",
                "specifies syntactic mode used to compile the source code",
                SyntacticMode.class,
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.MODULE,
                OptionAvailability.UNIVERSAL, category,
                SyntacticMode.RELAXED));

        list.add(new BooleanCompilerOptionValue(CompilerOptions.TARGET_GUARD, "",
                "generates guard code at the beginning of the program ensuring the processor runs in the " +
                "Mindustry version compatible with the 'target' and 'builtin-evaluation' options",
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                false).setConstValue(true));

        list.add(new EnumCompilerOptionValue<>(CompilerOptions.BOUNDARY_CHECKS, "",
                "governs the runtime checks generated by compiler to catch indexes out of bounds when " +
                "accessing internal array elements",
                RuntimeChecks.class,
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                RuntimeChecks.NONE));

        list.add(new EnumCompilerOptionValue<>(CompilerOptions.REMARKS, "r",
                "controls remarks propagation to the compiled code: none (remarks are removed), " +
                "comments (included as mlog comments), passive (included as 'print' but not not executed), or " +
                "active (included as 'print' and printed)",
                Remarks.class,
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                Remarks.PASSIVE));

        list.add(new BooleanCompilerOptionValue(CompilerOptions.AUTO_PRINTFLUSH, "",
                "automatically add a 'printflush message1' instruction at the end of the program if one is missing",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                true));
    }

    private static void addOptimizationsOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.OPTIMIZATIONS;

        list.add(new EnumCompilerOptionValue<>(OptimizationOptions.GOAL, "g",
                "sets code generation goal: minimize code size, minimize execution speed, or no specific preference",
                GenerationGoal.class,
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                GenerationGoal.SPEED));

        list.add(new IntegerCompilerOptionValue(OptimizationOptions.PASSES, "e",
                "sets the number of optimization passes to perform",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                1, webApp ? MAX_PASSES_WEBAPP : MAX_PASSES_CMDLINE,
                webApp ? DEFAULT_PASSES_WEBAPP : DEFAULT_PASSES_CMDLINE));

        list.add(new BooleanCompilerOptionValue(OptimizationOptions.UNSAFE_CASE_OPTIMIZATION, "",
                "omits range checking of case expressions without an else branch during optimization",
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.UNSTABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                false).setConstValue(true));

        list.add(new IntegerCompilerOptionValue(OptimizationOptions.CASE_OPTIMIZATION_STRENGTH, "",
                "sets the strength of case switching optimization: higher number means more case configurations " +
                "are considered, potentially producing a more efficient code, at the cost of longer compilation time",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                0, webApp ? MAX_CASE_OPTIMIZATION_STRENGTH_WEBAPP : MAX_CASE_OPTIMIZATION_STRENGTH_CMDLINE,
                webApp ? DEFAULT_CASE_OPTIMIZATION_STRENGTH_WEBAPP : DEFAULT_CASE_OPTIMIZATION_STRENGTH_CMDLINE));

        list.add(new BooleanCompilerOptionValue(OptimizationOptions.MLOG_BLOCK_OPTIMIZATION, "",
                "allows (limited) optimization of code inside mlog blocks",
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                false).setConstValue(true));

        list.add(new BooleanCompilerOptionValue(EnvironmentOptions.TEXT_JUMP_TABLES, "",
                "when active, generates jump tables by encoding instruction addresses into a single String value, and uses " +
                "a single 'read' instruction to directly set the counter to the target address (target 8 or higher required)",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.UNIVERSAL, category,
                true));
    }

    private static void addOptimizationLevels(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.OPTIMIZATION_LEVELS;
        List<CompilerOptionValue<OptimizationLevel>> optList = new ArrayList<>();

        for (Optimization optimization : Optimization.LIST) {
            optList.add(new OptimizationOptionValue(optimization, optimization.getOptionName(), "",
                    "sets the optimization level of " + optimization.getDescription(),
                    OptimizationLevel.class,
                    OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                    OptionAvailability.UNIVERSAL, category,
                    OptimizationLevel.EXPERIMENTAL));
        }

        list.add(new OptimizationLevelCompilerValue(OptimizationOptions.OPTIMIZATION, "",
                "sets global optimization level for all optimizers",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.DIRECTIVE, category,
                optList));

        list.add(new IntOptimizationLevelCompilerValue(OptimizationOptions.OPTIMIZATION_LEVEL, "",  "O",
                "sets global optimization level for all optimizers",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.LOCAL,
                OptionAvailability.COMMAND_LINE, category,
                optList));

        list.addAll(optList);
    }

    private static void addDebuggingOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.DEBUGGING;

        list.add(new EnumCompilerOptionValue<>(DebuggingOptions.SORT_VARIABLES, "",
                "prepends the final code with instructions that ensure variables are created inside the processor " +
                " in a well-defined order. The variables are sorted according to their categories in order, and then alphabetically. " +
                " Category ALL represents all remaining, not-yet processed variables. When --sort-variables is given without" +
                " specifying any category, " + SortCategory.usefulCategories() + " are used.",
                SortCategory.class,
                OptionMultiplicity.ZERO_OR_MORE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                SortCategory.NONE).setListProcessor(CompilerOptionFactory::normalizeSortVariables));

        list.add(new IntegerCompilerOptionValue(DebuggingOptions.PARSE_TREE, "p",
                "sets the detail level of parse tree output into the log file, 0 = off",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                0, 2, 0));

        list.add(new IntegerCompilerOptionValue(DebuggingOptions.DEBUG_MESSAGES, "d",
                "sets the detail level of debug messages, 0 = off",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                0, 3, 0));

        list.add(new BooleanCompilerOptionValue(DebuggingOptions.DEBUG_OUTPUT, "",
                "activates debug output on stdout",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.DIRECTIVE, category,
                false));

        list.add(new EnumCompilerOptionValue<>(DebuggingOptions.PRINT_UNRESOLVED, "u",
                "activates output of the unresolved code (before virtual instructions resolution) of given type" +
                " (instruction numbers are included in the output)",
                FinalCodeOutput.class,
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                FinalCodeOutput.NONE).setConstValue(FinalCodeOutput.PLAIN));

        list.add(new BooleanCompilerOptionValue(DebuggingOptions.PRINT_STACKTRACE, "stacktrace", "s",
                "outputs a stack trace onto stderr when an unhandled exception occurs",
                OptionMultiplicity.ZERO, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.COMMAND_LINE, category,
                false).setConstValue(true));

        list.add(new IntegerCompilerOptionValue(DebuggingOptions.CASE_CONFIGURATION, "",
                "sets a specific id of case configuration to perform",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.DIRECTIVE, category,
                0, Integer.MAX_VALUE, 0));
    }

    private static void addRunOptions(List<CompilerOptionValue<?>> list, boolean webApp) {
        final OptionCategory category = OptionCategory.RUN;

        list.add(new BooleanCompilerOptionValue(RunOptions.RUN, "",
                "run the compiled code on an emulated processor",
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                false).setConstValue(true));

        list.add(new IntegerCompilerOptionValue(RunOptions.RUN_STEPS, "",
                "the maximum number of instruction executions to emulate, the execution stops when this limit is reached",
                OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.COMMAND_LINE, category,
                0, webApp ? MAX_STEP_LIMIT_WEBAPP : MAX_STEP_LIMIT_CMDLINE,
                webApp ? DEFAULT_STEP_LIMIT_WEBAPP : DEFAULT_STEP_LIMIT_CMDLINE));

        list.add(new BooleanCompilerOptionValue(RunOptions.OUTPUT_PROFILING, "",
                "output the profiling data into the log file",
                OptionMultiplicity.ZERO_OR_ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                OptionAvailability.UNIVERSAL, category,
                false).setConstValue(true));

        for (ExecutionFlag flag : ExecutionFlag.LIST) {
            list.add(new BooleanCompilerOptionValue(flag, "",
                    flag.getDescription(),
                    OptionMultiplicity.ONCE, SemanticStability.STABLE, OptionScope.GLOBAL,
                    flag.isSettable() ? OptionAvailability.UNIVERSAL : OptionAvailability.NONE, category,
                    flag.getDefaultValue()));
        }
    }

    private static List<SortCategory> normalizeSortVariables(@Nullable List<SortCategory> sortVariables) {
        if (sortVariables == null || sortVariables.isEmpty()) {
            return SortCategory.getAllCategories();
        } else {
            return sortVariables;
        }
    }
}
