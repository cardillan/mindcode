package info.teksol.mc.profile;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.messages.SourcePositionTranslator;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/// Represents the configuration profile for a compiler/schematics builder/processor emulator, encapsulating various
/// parameters and settings that influence code compilation, schematics construction and code execution
/// on the emulated processor.
///
/// Some default values and limits differ between the command-line tool and the web application.
///
/// Generally, the settings in the CompilerProfile can be set using compiler directives (#set and others),
/// and by the command-line arguments.
///
/// Variables holding instances of this interface should be named "profile".
@NullMarked
public class CompilerProfile {
    public static final String SIGNATURE = "Compiled by Mindcode - github.com/cardillan/mindcode";

    public static final int MAX_PASSES = 1000;
    public static final int MAX_INSTRUCTIONS = 100_000;
    public static final int MAX_INSTRUCTIONS_WEBAPP = 1500;
    public static final int DEFAULT_INSTRUCTIONS = 1000;
    public static final int DEFAULT_WEBAPP_PASSES = 5;
    public static final int DEFAULT_CMDLINE_PASSES = 25;
    public static final int DEFAULT_STEP_LIMIT_WEBAPP = 1_000_000;
    public static final int DEFAULT_STEP_LIMIT_CMDLINE = 10_000_000;

    private final boolean webApplication;

    // Settable options
    private final Map<Optimization, OptimizationLevel> levels;
    private ProcessorVersion processorVersion = ProcessorVersion.V7A;
    private ProcessorEdition processorEdition = ProcessorEdition.WORLD_PROCESSOR;
    private int instructionLimit = 1000;
    private int optimizationPasses = DEFAULT_WEBAPP_PASSES;
    private SyntacticMode syntacticMode = SyntacticMode.RELAXED;
    private boolean linkedBlockGuards = true;
    private GenerationGoal goal = GenerationGoal.AUTO;
    private Remarks remarks = Remarks.PASSIVE;
    private MemoryModel memoryModel = MemoryModel.VOLATILE;
    private boolean shortCircuitEval = false;
    private boolean autoPrintflush = true;
    private @Nullable FinalCodeOutput finalCodeOutput = null;
    private int parseTreeLevel = 0;
    private int debugLevel = 0;
    private boolean printStackTrace = false;

    private List<SortCategory> sortVariables = List.of();
    private boolean signature = true;

    // Compile and run
    private boolean run = false;
    private int stepLimit = DEFAULT_STEP_LIMIT_WEBAPP;
    private final EnumSet<ExecutionFlag> executionFlags = ExecutionFlag.getDefaultFlags();

    // Schematics Builder
    private List<String> additionalTags = List.of();
    private SourcePositionTranslator positionTranslator = p -> p;

    /// Constructs a new instance of the CompilerProfile class.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                       If true, the default settings for web applications are applied; otherwise,
    ///                       default settings for the command-line tool are used.
    /// @param level          the global optimization level to be applied across all optimization types.
    public CompilerProfile(boolean webApplication, OptimizationLevel level) {
        this.webApplication = webApplication;
        this.stepLimit = webApplication ? DEFAULT_STEP_LIMIT_WEBAPP : DEFAULT_STEP_LIMIT_CMDLINE;
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o, o -> level));
    }

    /// Constructs a new instance of the CompilerProfile class.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                        If true, the default settings for web applications are applied; otherwise,
    ///                        default settings for the command-line tool are used.
    /// @param optimizations   a varargs parameter containing a set of optimizations to be applied. Each optimization
    ///                        is configured with an advanced level if specified.
    public CompilerProfile(boolean webApplication, Optimization... optimizations) {
        this.webApplication = webApplication;
        this.stepLimit = webApplication ? DEFAULT_STEP_LIMIT_WEBAPP : DEFAULT_STEP_LIMIT_CMDLINE;
        Set<Optimization> optimSet = Set.of(optimizations);
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o,
                o -> optimSet.contains(o) ? OptimizationLevel.ADVANCED : OptimizationLevel.NONE));
    }

    /// Creates a [CompilerProfile] instance configured with command-line tool defaults and
    /// experimental optimizations.
    ///
    /// @return a [CompilerProfile] instance configured with the experimental optimization level.
    public static CompilerProfile experimentalOptimizations() {
        return new CompilerProfile(false,OptimizationLevel.EXPERIMENTAL);
    }

    /// Creates a [CompilerProfile] instance configured with full optimizations.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                       If true, it applies optimizations specific to web applications; otherwise,
    ///                       it applies optimizations for general-purpose environments.
    /// @return a [CompilerProfile] instance configured with the advanced optimization level.
    public static CompilerProfile fullOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.ADVANCED);
    }

    /// Creates a [CompilerProfile] instance configured with the standard optimizations.
    ///
    /// @param webApplication a boolean indicating whether the profile is intended for a web application.
    ///                       If true, it applies settings optimized for web applications; otherwise,
    ///                       it applies settings suitable for command-line environments.
    /// @return a [CompilerProfile] instance configured with the standard optimization level.
    public static CompilerProfile standardOptimizations(boolean webApplication) {
        return new CompilerProfile(webApplication, OptimizationLevel.BASIC);
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

    public boolean isWebApplication() {
        return webApplication;
    }

    public int getTraceLimit() {
        return webApplication ? 1000 : 10_000;
    }

    public ProcessorVersion getProcessorVersion() {
        return processorVersion;
    }

    public ProcessorEdition getProcessorEdition() {
        return processorEdition;
    }

    public CompilerProfile setProcessorVersionEdition(ProcessorVersion processorVersion, ProcessorEdition processorEdition) {
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
        return this;
    }

    public CompilerProfile setProcessorEdition(ProcessorEdition processorEdition) {
        this.processorEdition = processorEdition;
        return this;
    }

    public CompilerProfile setProcessorVersion(ProcessorVersion processorVersion) {
        this.processorVersion = processorVersion;
        return this;
    }

    public OptimizationLevel getOptimizationLevel(Optimization optimization) {
        return levels.getOrDefault(optimization, OptimizationLevel.NONE);
    }

    public CompilerProfile setOptimizationLevel(Optimization optimization, OptimizationLevel level) {
        this.levels.put(optimization, level);
        return this;
    }

    public Map<Optimization, OptimizationLevel> getOptimizationLevels() {
        return Map.copyOf(levels);
    }

    public CompilerProfile setAllOptimizationLevels(OptimizationLevel level) {
        Optimization.LIST.forEach(o -> levels.put(o, level));
        return this;
    }

    public boolean optimizationsActive() {
        return levels.values().stream().anyMatch(l -> l != OptimizationLevel.NONE);
    }

    public int getInstructionLimit() {
        return instructionLimit;
    }

    public CompilerProfile setInstructionLimit(int instructionLimit) {
        int max = webApplication ? MAX_INSTRUCTIONS_WEBAPP : MAX_INSTRUCTIONS;
        this.instructionLimit = Math.min(max, instructionLimit);
        return this;
    }

    public int getOptimizationPasses() {
        return optimizationPasses;
    }

    public CompilerProfile setOptimizationPasses(int optimizationPasses) {
        this.optimizationPasses = optimizationPasses;
        return this;
    }

    public SyntacticMode getSyntacticMode() {
        return syntacticMode;
    }

    public CompilerProfile setSyntacticMode(SyntacticMode syntacticMode) {
        this.syntacticMode = syntacticMode;
        return this;
    }

    public boolean isLinkedBlockGuards() {
        return linkedBlockGuards;
    }

    public CompilerProfile setLinkedBlockGuards(boolean linkedBlockGuards) {
        this.linkedBlockGuards = linkedBlockGuards;
        return this;
    }

    public GenerationGoal getGoal() {
        return goal;
    }

    public CompilerProfile setGoal(GenerationGoal goal) {
        this.goal = goal;
        return this;
    }

    public Remarks getRemarks() {
        return remarks;
    }

    public CompilerProfile setRemarks(Remarks remarks) {
        this.remarks = remarks;
        return this;
    }

    public MemoryModel getMemoryModel() {
        return memoryModel;
    }

    public CompilerProfile setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
        return this;
    }

    public boolean isShortCircuitEval() {
        return shortCircuitEval;
    }

    public CompilerProfile setShortCircuitEval(boolean shortCircuitEval) {
        this.shortCircuitEval = shortCircuitEval;
        return this;
    }

    public boolean isAutoPrintflush() {
        return autoPrintflush;
    }

    public CompilerProfile setAutoPrintflush(boolean autoPrintflush) {
        this.autoPrintflush = autoPrintflush;
        return this;
    }

    public CompilerProfile setFinalCodeOutput(@Nullable FinalCodeOutput finalCodeOutput) {
        this.finalCodeOutput = finalCodeOutput;
        return this;
    }

    public @Nullable FinalCodeOutput getFinalCodeOutput() {
        return finalCodeOutput;
    }

    public int getParseTreeLevel() {
        return parseTreeLevel;
    }

    public CompilerProfile setParseTreeLevel(int parseTreeLevel) {
        this.parseTreeLevel = parseTreeLevel;
        return this;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public CompilerProfile setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
        return this;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }

    public CompilerProfile setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
        return this;
    }

    public List<SortCategory> getSortVariables() {
        return sortVariables;
    }

    public CompilerProfile setSortVariables(List<SortCategory> sortVariables) {
        this.sortVariables = sortVariables;
        return this;
    }

    public boolean isSignature() {
        return signature;
    }

    public CompilerProfile setSignature(boolean signature) {
        this.signature = signature;
        return this;
    }

    public List<String> getAdditionalTags() {
        return additionalTags;
    }

    public CompilerProfile setAdditionalTags(List<String> additionalTags) {
        this.additionalTags = Objects.requireNonNull(additionalTags);
        return this;
    }

    public SourcePositionTranslator getPositionTranslator() {
        return positionTranslator;
    }

    public void setPositionTranslator(SourcePositionTranslator positionTranslator) {
        this.positionTranslator = positionTranslator;
    }

    public boolean isRun() {
        return run;
    }

    public CompilerProfile setRun(boolean run) {
        this.run = run;
        return this;
    }

    public int getStepLimit() {
        return stepLimit;
    }

    public CompilerProfile setStepLimit(int stepLimit) {
        this.stepLimit = stepLimit;
        return this;
    }

    public CompilerProfile setExecutionFlags(ExecutionFlag... flags) {
        executionFlags.addAll(Arrays.asList(flags));
        return this;
    }

    public CompilerProfile setExecutionFlag(ExecutionFlag flag, boolean value) {
        if (!flag.isSettable()) {
            throw new MindcodeInternalError("Trying to update unmodifiable flag %s", flag);
        }
        if (value) {
            executionFlags.add(flag);
        } else {
            executionFlags.remove(flag);
        }
        return this;
    }

    public CompilerProfile clearExecutionFlags(ExecutionFlag... flags) {
        Arrays.stream(flags).filter(f -> !f.isSettable())
                .forEach(f -> { throw new MindcodeInternalError("Trying to clear unmodifiable flag %s", f); });

        Arrays.asList(flags).forEach(executionFlags::remove);
        return this;
    }

    public Set<ExecutionFlag> getExecutionFlags() {
        return EnumSet.copyOf(executionFlags);
    }

    public String encode() {
        int len = OptimizationLevel.values().length;
        long value = 0;
        for (Optimization optimization : Optimization.LIST) {
            value = value * len + getOptimizationLevel(optimization).ordinal();
        }
        value = value * GenerationGoal.values().length + getGoal().ordinal();
        return Long.toString(value);
    }

    public CompilerProfile decode(String encoded) {
        GenerationGoal[] goals = GenerationGoal.values();
        OptimizationLevel[] levels = OptimizationLevel.values();
        int len = levels.length;
        long value = Long.parseLong(encoded);
        setGoal(goals[(int) (value % goals.length)]);
        value /= goals.length;
        for (int i = Optimization.LIST.size() - 1; i >= 0; i--) {
            setOptimizationLevel(Optimization.LIST.get(i), levels[(int) (value % len)]);
            value /= len;
        }
        return this;
    }
}
