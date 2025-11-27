package info.teksol.mc.profile;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.options.*;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/// Provides access to global/module compiler options.
@NullMarked
public interface GlobalCompilerProfile {
    <T> CompilerOptionValue<T> getOption(Enum<?> option);

    int getIntValue(Enum<?> option);

    boolean getBooleanValue(Enum<?> option);

    String getStringValue(Enum<?> option);

    <T extends Enum<T>> T getEnumValue(Enum<?> option);

    //<editor-fold desc="Input/output options">
    default FileReferences getFileReferences() {
        return getEnumValue(InputOutputOptions.FILE_REFERENCES);
    }
    //</editor-fold>

    //<editor-fold desc="Schematics options">
    default List<String> getAdditionalTags() {
        return this.<String>getOption(SchematicOptions.ADD_TAG).getValues();
    }
    //</editor-fold>

    //<editor-fold desc="Environment Options">
    default BuiltinEvaluation getBuiltinEvaluation() {
        return getEnumValue(EnvironmentOptions.BUILTIN_EVALUATION);
    }

    default boolean isNullCounterIsNoop() {
        return getBooleanValue(EnvironmentOptions.NULL_COUNTER_IS_NOOP);
    }

    default int getInstructionLimit() {
        return getIntValue(EnvironmentOptions.INSTRUCTION_LIMIT);
    }

    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    Target getTarget();
    //</editor-fold>

    //<editor-fold desc="Mlog FormatOptions">
    default boolean isNoArgumentPadding() {
        return getBooleanValue(MlogFormatOptions.NO_ARGUMENT_PADDING);
    }

    default int getMlogIndent() {
        int mlogIndent = getIntValue(MlogFormatOptions.MLOG_INDENT);
        boolean symbolicLabels = getBooleanValue(MlogFormatOptions.SYMBOLIC_LABELS);
        return mlogIndent >= 0 ? mlogIndent : symbolicLabels ? 4 : 0;
    }

    default boolean isSignature() {
        return getBooleanValue(MlogFormatOptions.SIGNATURE);
    }

    default boolean isShortFunctionPrefix() {
        return getBooleanValue(MlogFormatOptions.FUNCTION_PREFIX);
    }

    default boolean isSymbolicLabels() {
        return getBooleanValue(MlogFormatOptions.SYMBOLIC_LABELS);
    }
    //</editor-fold>

    //<editor-fold desc="Compiler Options">
    default boolean isAutoPrintflush() {
        return getBooleanValue(CompilerOptions.AUTO_PRINTFLUSH);
    }

    default boolean isEmulateStrictNotEqual() {
        return getBooleanValue(CompilerOptions.EMULATE_STRICT_NOT_EQUAL);
    }

    boolean useEmulatedStrictNotEqual();

    SyntacticMode getSyntacticMode();

    default boolean isTargetGuard() {
        return getBooleanValue(CompilerOptions.TARGET_GUARD);
    }
    //</editor-fold>

    //<editor-fold desc="Optimizations Options">
    default int getOptimizationPasses() {
        return getIntValue(OptimizationOptions.PASSES);
    }

    default boolean useLookupArrays() {
        return getBooleanValue(OptimizationOptions.USE_LOOKUP_ARRAYS);
    }

    default boolean useShortArrays() {
        return getBooleanValue(OptimizationOptions.USE_SHORT_ARRAYS);
    }
    //</editor-fold>

    //<editor-fold desc="Optimization Levels">
    OptimizationLevel getOptimizationLevel(Optimization optimization);

    boolean optimizationsActive();
    //</editor-fold>

    //<editor-fold desc="Debugging Options">
    default int getCaseConfiguration() {
        return getIntValue(DebuggingOptions.CASE_CONFIGURATION);
    }

    default int getDebugMessages() {
        return getIntValue(DebuggingOptions.DEBUG_MESSAGES);
    }

    default boolean isDebugOutput() {
        return getBooleanValue(DebuggingOptions.DEBUG_OUTPUT);
    }

    default FinalCodeOutput getFinalCodeOutput() {
        return getEnumValue(DebuggingOptions.PRINT_UNRESOLVED);
    }

    default int getParseTreeLevel() {
        return getIntValue(DebuggingOptions.PARSE_TREE);
    }

    default boolean isPrintCodeSize() {
        return getBooleanValue(DebuggingOptions.PRINT_CODE_SIZE);
    }

    default boolean isPrintStackTrace() {
        return getBooleanValue(DebuggingOptions.PRINT_STACKTRACE);
    }

    default List<SortCategory> getSortVariables() {
        return this.<SortCategory>getOption(DebuggingOptions.SORT_VARIABLES).getValues();
    }
    //</editor-fold>

    //<editor-fold desc="Run Options">
    default Set<ExecutionFlag> getExecutionFlags() {
        EnumSet<ExecutionFlag> executionFlags = EnumSet.noneOf(ExecutionFlag.class);
        for (ExecutionFlag flag : ExecutionFlag.LIST) {
            if (this.<Boolean>getOption(flag).getValue()) executionFlags.add(flag);
        }
        return executionFlags;
    }

    default boolean isOutputProfiling() {
        return getBooleanValue(RunOptions.OUTPUT_PROFILING);
    }

    default boolean isRun() {
        return getBooleanValue(RunOptions.RUN);
    }

    default int getStepLimit() {
        return getIntValue(RunOptions.RUN_STEPS);
    }

    //</editor-fold>
}
