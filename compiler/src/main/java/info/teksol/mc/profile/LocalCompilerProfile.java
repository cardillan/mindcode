package info.teksol.mc.profile;

import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.options.*;
import org.jspecify.annotations.NullMarked;

/// Provides access to module/local compiler options.
@NullMarked
public interface LocalCompilerProfile {
    <T> CompilerOptionValue<T> getOption(Enum<?> option);

    int getIntValue(Enum<?> option);

    double getDoubleValue(Enum<?> option);

    boolean getBooleanValue(Enum<?> option);

    String getStringValue(Enum<?> option);

    <T extends Enum<T>> T getEnumValue(Enum<?> option);

    //<editor-fold desc="Environment Options">
    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    Target getTarget();

    boolean useTextJumpTables();
    //</editor-fold>

    //<editor-fold desc="Debugging Options">
    default boolean isDebug() {
        return getBooleanValue(DebuggingOptions.DEBUG);
    }
    //</editor-fold>

    //<editor-fold desc="Compiler Options">
    default boolean isBoundaryChecks() {
        return getBooleanValue(CompilerOptions.BOUNDARY_CHECKS);
    }

    default boolean isErrorFunction() {
        return getBooleanValue(CompilerOptions.ERROR_FUNCTION);
    }

    default RuntimeErrorReporting getErrorReporting() {
        return getEnumValue(CompilerOptions.ERROR_REPORTING);
    }

    default Remarks getRemarks() {
        return getEnumValue(CompilerOptions.REMARKS);
    }

    SyntacticMode getSyntacticMode();
    //</editor-fold>

    //<editor-fold desc="Optimizations Options">
    default int getCaseOptimizationStrength() {
        return getIntValue(OptimizationOptions.CASE_OPTIMIZATION_STRENGTH);
    }

    default double getCodeWeight() {
        return getDoubleValue(OptimizationOptions.WEIGHT);
    }

    default GenerationGoal getGoal() {
        return getEnumValue(OptimizationOptions.GOAL);
    }

    default boolean isMlogBlockOptimization() {
        return getBooleanValue(OptimizationOptions.MLOG_BLOCK_OPTIMIZATION);
    }

    default boolean isUnsafeCaseOptimization() {
        return getBooleanValue(OptimizationOptions.UNSAFE_CASE_OPTIMIZATION);
    }

    default boolean isUseTextJumpTables() {
        return getBooleanValue(OptimizationOptions.USE_TEXT_JUMP_TABLES);
    }

    default boolean isUseTextTranslations() {
        return getBooleanValue(OptimizationOptions.USE_TEXT_TRANSLATIONS);
    }
    //</editor-fold>

    //<editor-fold desc="Optimization Levels">
    OptimizationLevel getOptimizationLevel(Optimization optimization);

    boolean optimizationsActive();
    //</editor-fold>
}
