package info.teksol.mc.profile;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.messages.SourcePositionTranslator;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.options.*;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/// Represents the configuration profile for a compiler/schematics builder/processor emulator, encapsulating various
/// parameters and settings that influence code compilation, schematic construction, and code execution
/// on the emulated processor.
///
/// Some default values and limits differ between the command-line tool and the web application.
///
/// Generally, the settings in the CompilerProfile can be set using compiler directives (#set and others),
/// and by the command-line arguments.
///
/// Variables holding instances of this interface should be named "profile".
@NullMarked
public class CompilerProfile implements GlobalCompilerProfile, LocalCompilerProfile {
    public static final String SIGNATURE = "Compiled by Mindcode - github.com/cardillan/mindcode";

    private final boolean webApplication;
    private final Map<Enum<?>, CompilerOptionValue<?>> options;

    // System library functions take precedence over built-in mlog functions
    // Only used to generate library documentation
    private boolean libraryPrecedence = false;

    // Schematics Builder
    private SourcePositionTranslator positionTranslator = p -> p;

    /// Constructs a new instance of the CompilerProfile class.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                       If true, the default settings for web applications are applied; otherwise,
    ///                       default settings for the command-line tool are used.
    /// @param level          the global optimization level to be applied across all optimization types.
    private CompilerProfile(boolean webApplication, OptimizationLevel level) {
        this.webApplication = webApplication;
        this.options = CompilerOptionFactory.createCompilerOptions(webApplication);
        setAllOptimizationLevels(level);
    }

    @SuppressWarnings("unchecked")
    private CompilerProfile(CompilerProfile other, boolean includeUnstable) {
        this.webApplication = other.webApplication;
        this.options = CompilerOptionFactory.createCompilerOptions(webApplication);
        for (CompilerOptionValue<?> option : other.options.values()) {
            if (option.option != OptimizationOptions.OPTIMIZATION && (includeUnstable || option.stability == SemanticStability.STABLE)) {
                getOption(option.option).setValues((List<Object>) option.getValues());
            }
        }
    }

    public CompilerProfile duplicate(boolean includeUnstable) {
        return new CompilerProfile(this, includeUnstable);
    }

    @SuppressWarnings("unchecked")
    public void copyUnstableFrom(CompilerProfile other) {
        for (CompilerOptionValue<?> option : other.options.values()) {
            if (option.stability == SemanticStability.UNSTABLE) {
                getOption(option.option).setValues((List<Object>) option.getValues());
            }
        }
    }

    public Map<Enum<?>, CompilerOptionValue<?>> getOptions() {
        return options;
    }

    @SuppressWarnings("unchecked")
    public <T> CompilerOptionValue<T> getOption(Enum<?> option) {
        return (CompilerOptionValue<T>) options.get(option);
    }

    public int getIntValue(Enum<?> option) {
        return this.<Integer>getOption(option).getValue();
    }

    public double getDoubleValue(Enum<?> option) {
        return this.<Double>getOption(option).getValue();
    }

    public boolean getBooleanValue(Enum<?> option) {
        return this.<Boolean>getOption(option).getValue();
    }

    public String getStringValue(Enum<?> option) {
        return this.<String>getOption(option).getValue();
    }

    public <T extends Enum<T>> T getEnumValue(Enum<?> option) {
        return this.<T>getOption(option).getValue();
    }

    public boolean isWebApplication() {
        return webApplication;
    }

    public boolean isLibraryPrecedence() {
        return libraryPrecedence;
    }

    public CompilerProfile setLibraryPrecedence(boolean libraryPrecedence) {
        this.libraryPrecedence = libraryPrecedence;
        return this;
    }

    public SourcePositionTranslator getPositionTranslator() {
        return positionTranslator;
    }

    public void setPositionTranslator(SourcePositionTranslator positionTranslator) {
        this.positionTranslator = positionTranslator;
    }

    public int getTraceLimit() {
        return webApplication ? 1000 : 10_000;
    }

    @Override
    public boolean useTextJumpTables() {
        return isTextJumpTables() && !isSymbolicLabels() && getProcessorVersion().atLeast(ProcessorVersion.V8A);
    }

    public CompilerProfile decode(String encoded) {
        GenerationGoal[] goals = GenerationGoal.values();
        OptimizationLevel[] levels = OptimizationLevel.values();
        int len = levels.length;
        long value = Long.parseLong(encoded);
        setSymbolicLabels(value % 2 == 1);
        value /= 2;
        setGoal(goals[(int) (value % goals.length)]);
        value /= goals.length;
        for (int i = Optimization.LIST.size() - 1; i >= 0; i--) {
            setOptimizationLevel(Optimization.LIST.get(i), levels[(int) (value % len)]);
            value /= len;
        }
        return this;
    }

    public String encode() {
        int len = OptimizationLevel.values().length;
        long value = 0;
        for (Optimization optimization : Optimization.LIST) {
            value = value * len + getOptimizationLevel(optimization).ordinal();
        }
        value = value * GenerationGoal.values().length + getGoal().ordinal();
        value = value * 2 + (isSymbolicLabels() ? 1 : 0);
        return Long.toString(value);
    }

    //<editor-fold desc="Input/output options">
    public CompilerProfile setFileReferences(FileReferences fileReferences) {
        getOption(InputOutputOptions.FILE_REFERENCES).setValue(fileReferences);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Schematics options">
    public CompilerProfile setAdditionalTags(List<String> additionalTags) {
        this.<String>getOption(SchematicOptions.ADD_TAG).setValues(additionalTags);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Environment Options">
    public CompilerProfile setBuiltinEvaluation(BuiltinEvaluation builtinEvaluation) {
        getOption(EnvironmentOptions.BUILTIN_EVALUATION).setValue(builtinEvaluation);
        return this;
    }

    public CompilerProfile setNullCounterIsNoop(boolean nullCounterIsNoop) {
        getOption(EnvironmentOptions.NULL_COUNTER_IS_NOOP).setValue(nullCounterIsNoop);
        return this;
    }

    public ProcessorVersion getProcessorVersion() {
        return this.<Target>getOption(EnvironmentOptions.TARGET).getValue().version();
    }

    public ProcessorEdition getProcessorEdition() {
        return this.<Target>getOption(EnvironmentOptions.TARGET).getValue().edition();
    }

    public Target getTarget() {
        return this.<Target>getOption(EnvironmentOptions.TARGET).getValue();
    }

    public CompilerProfile setTarget(ProcessorVersion version, ProcessorEdition edition) {
        getOption(EnvironmentOptions.TARGET).setValue(new Target(version, edition));
        return this;
    }

    public CompilerProfile setTarget(Target target) {
        getOption(EnvironmentOptions.TARGET).setValue(target);
        return this;
    }

    public CompilerProfile setTextJumpTables(boolean textJumpTables) {
        getOption(OptimizationOptions.TEXT_TABLES).setValue(textJumpTables);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Mlog FormatOptions">
    public CompilerProfile setMlogIndent(int mlogIndent) {
        getOption(MlogFormatOptions.MLOG_INDENT).setValue(mlogIndent);
        return this;
    }

    public CompilerProfile setSignature(boolean signature) {
        getOption(MlogFormatOptions.SIGNATURE).setValue(signature);
        return this;
    }

    public CompilerProfile setShortFunctionPrefix(boolean shortFunctionPrefix) {
        getOption(MlogFormatOptions.FUNCTION_PREFIX).setValue(shortFunctionPrefix);
        return this;
    }

    public CompilerProfile setSymbolicLabels(boolean symbolicLabels) {
        getOption(MlogFormatOptions.SYMBOLIC_LABELS).setValue(symbolicLabels);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Compiler Options">
    public CompilerProfile setAutoPrintflush(boolean autoPrintflush) {
        getOption(CompilerOptions.AUTO_PRINTFLUSH).setValue(autoPrintflush);
        return this;
    }

    public CompilerProfile setBoundaryChecks(boolean boundaryChecks) {
        getOption(CompilerOptions.BOUNDARY_CHECKS).setValue(boundaryChecks);
        return this;
    }

    public CompilerProfile setErrorFunction(boolean errorFunction) {
        getOption(CompilerOptions.ERROR_FUNCTION).setValue(errorFunction);
        return this;
    }

    public CompilerProfile setErrorReporting(RuntimeErrorReporting errorReporting) {
        getOption(CompilerOptions.ERROR_REPORTING).setValue(errorReporting);
        return this;
    }

    public CompilerProfile setEmulateStrictNotEqual(boolean emulateStrictNotEqual) {
        getOption(CompilerOptions.EMULATE_STRICT_NOT_EQUAL).setValue(emulateStrictNotEqual);
        return this;
    }

    public boolean useEmulatedStrictNotEqual() {
        return isEmulateStrictNotEqual() && getProcessorVersion().atLeast(ProcessorVersion.V8B) && !isSymbolicLabels();
    }

    public CompilerProfile setRemarks(Remarks remarks) {
        getOption(CompilerOptions.REMARKS).setValue(remarks);
        return this;
    }

    public SyntacticMode getSyntacticMode() {
        return getEnumValue(CompilerOptions.SYNTAX);
    }

    public CompilerProfile setSyntacticMode(SyntacticMode syntacticMode) {
        getOption(CompilerOptions.SYNTAX).setValue(syntacticMode);
        return this;
    }

    public CompilerProfile setTargetGuard(boolean targetGuard) {
        getOption(CompilerOptions.TARGET_GUARD).setValue(targetGuard);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Optimizations Options">
    public CompilerProfile setCaseOptimizationStrength(int caseOptimizationStrength) {
        getOption(OptimizationOptions.CASE_OPTIMIZATION_STRENGTH).setValue(caseOptimizationStrength);
        return this;
    }

    public CompilerProfile setCodeWeight(double codeWeight) {
        getOption(OptimizationOptions.WEIGHT).setValue(codeWeight);
        return this;
    }

    public CompilerProfile setGoal(GenerationGoal goal) {
        getOption(OptimizationOptions.GOAL).setValue(goal);
        return this;
    }

    public CompilerProfile setInstructionLimit(int instructionLimit) {
        getOption(EnvironmentOptions.INSTRUCTION_LIMIT).setValue(instructionLimit);
        return this;
    }

    public CompilerProfile setMlogBlockOptimization(boolean mlogBlockOptimization) {
        getOption(OptimizationOptions.MLOG_BLOCK_OPTIMIZATION).setValue(mlogBlockOptimization);
        return this;
    }

    public CompilerProfile setOptimizationPasses(int optimizationPasses) {
        getOption(OptimizationOptions.PASSES).setValue(optimizationPasses);
        return this;
    }

    public CompilerProfile setUnsafeCaseOptimization(boolean unsafeCaseOptimization) {
        getOption(OptimizationOptions.UNSAFE_CASE_OPTIMIZATION).setValue(unsafeCaseOptimization);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Optimization Levels">
    public OptimizationLevel getOptimizationLevel(Optimization optimization) {
        return getEnumValue(optimization);
    }

    public CompilerProfile setOptimizationLevel(Optimization optimization, OptimizationLevel level) {
        getOption(optimization).setValue(level);
        return this;
    }

    public CompilerProfile setAllOptimizationLevels(OptimizationLevel level) {
        Optimization.LIST.forEach(o -> getOption(o).setValue(level));
        return this;
    }

    public boolean optimizationsActive() {
        return Optimization.LIST.stream().anyMatch(o -> getEnumValue(o) != OptimizationLevel.NONE);
    }
    //</editor-fold>

    //<editor-fold desc="Debugging Options">
    public CompilerProfile setCaseConfiguration(int caseConfiguration) {
        getOption(DebuggingOptions.CASE_CONFIGURATION).setValue(caseConfiguration);
        return this;
    }

    public CompilerProfile setDebug(boolean debug) {
        getOption(DebuggingOptions.DEBUG).setValue(debug);
        return this;
    }

    public CompilerProfile setDebugMessages(int debugMessages) {
        getOption(DebuggingOptions.DEBUG_MESSAGES).setValue(debugMessages);
        return this;
    }

    public CompilerProfile setDebugOutput(boolean debugOutput) {
        getOption(DebuggingOptions.DEBUG_OUTPUT).setValue(debugOutput);
        return this;
    }

    public CompilerProfile setFinalCodeOutput(FinalCodeOutput finalCodeOutput) {
        getOption(DebuggingOptions.PRINT_UNRESOLVED).setValue(finalCodeOutput);
        return this;
    }

    public CompilerProfile setParseTreeLevel(int parseTreeLevel) {
        getOption(DebuggingOptions.PARSE_TREE).setValue(parseTreeLevel);
        return this;
    }

    public CompilerProfile setPrintCodeSize(boolean printCodeSize) {
        getOption(DebuggingOptions.PRINT_CODE_SIZE).setValue(printCodeSize);
        return this;
    }

    public CompilerProfile setPrintStackTrace(boolean printStackTrace) {
        getOption(DebuggingOptions.PRINT_STACKTRACE).setValue(printStackTrace);
        return this;
    }

    public CompilerProfile setSortVariables(List<SortCategory> sortVariables) {
        this.<SortCategory>getOption(DebuggingOptions.SORT_VARIABLES).setValues(sortVariables);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="Run Options">
    public CompilerProfile setExecutionFlag(ExecutionFlag flag, boolean value) {
        if (!flag.isSettable()) {
            throw new MindcodeInternalError("Trying to update unmodifiable flag %s", flag);
        }
        getOption(flag).setValue(value);
        return this;
    }

    public CompilerProfile setOutputProfiling(boolean outputProfiling) {
        getOption(RunOptions.OUTPUT_PROFILING).setValue(outputProfiling);
        return this;
    }

    public CompilerProfile setRun(boolean run) {
        getOption(RunOptions.RUN).setValue(run);
        return this;
    }

    private CompilerProfile setStepLimit(int stepLimit) {
        getOption(RunOptions.RUN_STEPS).setValue(stepLimit);
        return this;
    }
    //</editor-fold>

    /// Creates a [CompilerProfile] instance configured for a given optimization level.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                       If true, it applies optimizations specific to web applications; otherwise,
    ///                       it applies optimizations for general-purpose environments.
    /// @param level          the optimization level to be applied across all optimizations.
    /// @return a [CompilerProfile] instance configured with the advanced optimization level.
    public static CompilerProfile forOptimizations(boolean webApplication, OptimizationLevel level) {
        return new CompilerProfile(webApplication, level);
    }

    /// Creates a [CompilerProfile] instance configured with full optimizations.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                       If true, it applies optimizations specific to web applications; otherwise,
    ///                       it applies optimizations for general-purpose environments.
    /// @return a [CompilerProfile] instance configured with the advanced optimization level.
    public static CompilerProfile fullOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.EXPERIMENTAL);
    }

    /// Creates a [CompilerProfile] instance with no optimizations applied.
    ///
    /// @param webApplication a boolean indicating whether the compiler profile is intended for a web application.
    ///                       If true, the profile is optimized for web application settings; otherwise, it is
    ///                       configured for command-line tools.
    /// @return a [CompilerProfile] instance configured with no optimizations.
    public static CompilerProfile noOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.NONE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CompilerProfile that = (CompilerProfile) o;
        return webApplication == that.webApplication && libraryPrecedence == that.libraryPrecedence && options.equals(that.options) && positionTranslator.equals(that.positionTranslator);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(webApplication);
        result = 31 * result + options.hashCode();
        result = 31 * result + Boolean.hashCode(libraryPrecedence);
        result = 31 * result + positionTranslator.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompilerProfile:\n" +
               options.values().stream().map(CompilerOptionValue::toString).collect(Collectors.joining("\n"));
    }
}
