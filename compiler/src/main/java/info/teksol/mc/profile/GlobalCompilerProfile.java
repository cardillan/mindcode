package info.teksol.mc.profile;

import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
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

    double getDoubleValue(Enum<?> option);

    boolean getBooleanValue(Enum<?> option);

    String getStringValue(Enum<?> option);

    <T extends Enum<T>> T getEnumValue(Enum<?> option);

    boolean isDefault(Enum<?> option);

    boolean isSpecified(Enum<?> option);

    boolean isWebApplication();

    boolean isSchematic();

    //<editor-fold desc="Input/output options">
    default FileReferences getFileReferences() {
        return getEnumValue(InputOutputOptions.FILE_REFERENCES);
    }
    //</editor-fold>

    //<editor-fold desc="Schematics options">
    default List<String> getAdditionalTags() {
        return this.<String>getOption(SchematicOptions.ADD_TAG).getValues();
    }

    default Target getSchematicTarget() {
        return this.<Target>getOption(SchematicOptions.SCHEMATIC_TARGET).getValue();
    }
    //</editor-fold>

    //<editor-fold desc="Environment Options">
    default BuiltinEvaluation getBuiltinEvaluation() {
        return getEnumValue(EnvironmentOptions.BUILTIN_EVALUATION);
    }

    default boolean isNullCounterIsNoop() {
        return isDefault(EnvironmentOptions.NULL_COUNTER_IS_NOOP)
                ? getTarget().version() != ProcessorVersion.V8A : getBooleanValue(EnvironmentOptions.NULL_COUNTER_IS_NOOP);
    }

    default int getInstructionLimit() {
        return getIntValue(EnvironmentOptions.INSTRUCTION_LIMIT);
    }

    ProcessorVersion getProcessorVersion();

    ProcessorType getProcessorType();

    Target getTarget();
    //</editor-fold>

    //<editor-fold desc="Mlog FormatOptions">
    default boolean isEncodeZeroCharacters() {
        return !isWebApplication() && getBooleanValue(MlogFormatOptions.ENCODE_ZERO_CHARACTERS);
    }

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

    default List<String> getAuthors() {
        return this.<String>getOption(MlogFormatOptions.AUTHOR).getValues();
    }

    default boolean isShortFunctionPrefix() {
        return getBooleanValue(MlogFormatOptions.FUNCTION_PREFIX);
    }

    default boolean isSymbolicLabels() {
        return getBooleanValue(MlogFormatOptions.SYMBOLIC_LABELS);
    }

    default String getProcessorId() {
        return getStringValue(MlogFormatOptions.PROCESSOR_ID);
    }

    default String getProgramVersion() {
        return getStringValue(MlogFormatOptions.PROGRAM_VERSION);
    }

    default String getProgramName() {
        return getStringValue(MlogFormatOptions.PROGRAM_NAME);
    }
    //</editor-fold>

    //<editor-fold desc="Compiler Options">
    default boolean isAutoPrintflush() {
        return getBooleanValue(CompilerOptions.AUTO_PRINTFLUSH);
    }

    default boolean isEmulateStrictNotEqual() {
        return getBooleanValue(CompilerOptions.EMULATE_STRICT_NOT_EQUAL);
    }

    default boolean isEnforceInstructionLimit() {
        return isDefault(CompilerOptions.ENFORCE_INSTRUCTION_LIMIT) ? isSchematic()
                : getBooleanValue(CompilerOptions.ENFORCE_INSTRUCTION_LIMIT);
    }

    boolean useEmulatedStrictNotEqual();

    SyntacticMode getSyntacticMode();

    default int getSetrate() {
        return getIntValue(CompilerOptions.SETRATE);
    }

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

    //<editor-fold desc="Emulator Options">
    default Target getEmulatorTarget() {
        return isDefault(EmulatorOptions.EMULATOR_TARGET)
                ? getTarget()
                : this.<Target>getOption(EmulatorOptions.EMULATOR_TARGET).getValue();
    }

    default double getEmulatorFps() {
        return getDoubleValue(EmulatorOptions.EMULATOR_FPS);
    }

    default Set<ExecutionFlag> getExecutionFlags() {
        EnumSet<ExecutionFlag> executionFlags = EnumSet.noneOf(ExecutionFlag.class);
        for (ExecutionFlag flag : ExecutionFlag.LIST) {
            if (this.<Boolean>getOption(flag).getValue()) executionFlags.add(flag);
        }
        return executionFlags;
    }

    default boolean isOutputProfiling() {
        return getBooleanValue(EmulatorOptions.OUTPUT_PROFILING);
    }

    default boolean isRun() {
        return getBooleanValue(EmulatorOptions.RUN);
    }

    default int getStepLimit() {
        return getIntValue(EmulatorOptions.RUN_STEPS);
    }

    default int getTraceLimit() {
        return isWebApplication() ? 1000 : 10_000;
    }
    //</editor-fold>
}
