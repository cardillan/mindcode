package info.teksol.mc.profile;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.messages.SourcePositionTranslator;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

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

    public static final int DEFAULT_INSTRUCTIONS = 1000;
    public static final int DEFAULT_PASSES_CMDLINE = 25;
    public static final int DEFAULT_PASSES_WEBAPP = 5;
    public static final int DEFAULT_STEP_LIMIT_CMDLINE = 10_000_000;
    public static final int DEFAULT_STEP_LIMIT_WEBAPP = 1_000_000;
    public static final int MAX_INSTRUCTIONS_CMDLINE = 100_000;
    public static final int MAX_INSTRUCTIONS_WEBAPP = 1500;
    public static final int MAX_MLOG_INDENT = 8;
    public static final int MAX_PASSES_CMDLINE = 1000;
    public static final int MAX_PASSES_WEBAPP = 25;

    private final boolean webApplication;

    // Settable options
    private final Map<Optimization, OptimizationLevel> levels;
    private final EnumSet<ExecutionFlag> executionFlags = ExecutionFlag.getDefaultFlags();
    private ProcessorVersion processorVersion = ProcessorVersion.V7A;
    private ProcessorEdition processorEdition = ProcessorEdition.WORLD_PROCESSOR;
    private boolean autoPrintflush = true;
    private RuntimeChecks boundaryChecks = RuntimeChecks.NONE;
    private int debugLevel = 0;
    private FileReferences fileReferences = FileReferences.PATH;
    private FinalCodeOutput finalCodeOutput = FinalCodeOutput.NONE;
    private GenerationGoal goal = GenerationGoal.AUTO;
    private int instructionLimit = 1000;
    private boolean linkedBlockGuards = true;
    private int mlogIndent = -1;
    private int optimizationPasses;
    private int parseTreeLevel = 0;
    private boolean printStackTrace = false;
    private Remarks remarks = Remarks.PASSIVE;
    private boolean shortCircuitEval = false;
    private boolean shortFunctionPrefix = false;
    private boolean signature = true;
    private List<SortCategory> sortVariables = List.of();
    private boolean symbolicLabels = false;
    private SyntacticMode syntacticMode = SyntacticMode.RELAXED;

    // Compile and run
    private boolean run = false;
    private int stepLimit = DEFAULT_STEP_LIMIT_WEBAPP;

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
        this.optimizationPasses = webApplication ? DEFAULT_PASSES_WEBAPP : DEFAULT_PASSES_CMDLINE;
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
        this.optimizationPasses = webApplication ? DEFAULT_PASSES_WEBAPP : DEFAULT_PASSES_CMDLINE;
        this.stepLimit = webApplication ? DEFAULT_STEP_LIMIT_WEBAPP : DEFAULT_STEP_LIMIT_CMDLINE;
        Set<Optimization> optimSet = Set.of(optimizations);
        this.levels = Optimization.LIST.stream().collect(Collectors.toMap(o -> o,
                o -> optimSet.contains(o) ? OptimizationLevel.EXPERIMENTAL : OptimizationLevel.NONE));
    }

    public int getMaxPasses() {
        return webApplication ? MAX_PASSES_WEBAPP : MAX_PASSES_CMDLINE;
    }

    public int getMaxInstructionLimit() {
        return webApplication ? MAX_INSTRUCTIONS_WEBAPP : MAX_INSTRUCTIONS_CMDLINE;
    }

    public CompilerProfile clearExecutionFlags(ExecutionFlag... flags) {
        Arrays.stream(flags).filter(f -> !f.isSettable())
                .forEach(f -> { throw new MindcodeInternalError("Trying to clear unmodifiable flag %s", f); });

        Arrays.asList(flags).forEach(executionFlags::remove);
        return this;
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

    public String encode() {
        int len = OptimizationLevel.values().length;
        long value = 0;
        for (Optimization optimization : Optimization.LIST) {
            value = value * len + getOptimizationLevel(optimization).ordinal();
        }
        value = value * GenerationGoal.values().length + getGoal().ordinal();
        return Long.toString(value);
    }

    public List<String> getAdditionalTags() {
        return additionalTags;
    }

    public CompilerProfile setAdditionalTags(List<String> additionalTags) {
        this.additionalTags = Objects.requireNonNull(additionalTags);
        return this;
    }

    public RuntimeChecks getBoundaryChecks() {
        return boundaryChecks;
    }

    public CompilerProfile setBoundaryChecks(RuntimeChecks boundaryChecks) {
        this.boundaryChecks = boundaryChecks;
        return this;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public CompilerProfile setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
        return this;
    }

    public Set<ExecutionFlag> getExecutionFlags() {
        return EnumSet.copyOf(executionFlags);
    }

    public CompilerProfile setExecutionFlags(ExecutionFlag... flags) {
        executionFlags.addAll(Arrays.asList(flags));
        return this;
    }

    public FileReferences getFileReferences() {
        return fileReferences;
    }

    public CompilerProfile setFileReferences(FileReferences fileReferences) {
        this.fileReferences = fileReferences;
        return this;
    }

    public FinalCodeOutput getFinalCodeOutput() {
        return finalCodeOutput;
    }

    public CompilerProfile setFinalCodeOutput(FinalCodeOutput finalCodeOutput) {
        this.finalCodeOutput = finalCodeOutput;
        return this;
    }

    public GenerationGoal getGoal() {
        return goal;
    }

    public CompilerProfile setGoal(GenerationGoal goal) {
        this.goal = goal;
        return this;
    }

    public int getInstructionLimit() {
        return instructionLimit;
    }

    public CompilerProfile setInstructionLimit(int instructionLimit) {
        this.instructionLimit = Math.min(instructionLimit, getMaxInstructionLimit());
        return this;
    }

    public boolean isLinkedBlockGuards() {
        return linkedBlockGuards;
    }

    public CompilerProfile setLinkedBlockGuards(boolean linkedBlockGuards) {
        this.linkedBlockGuards = linkedBlockGuards;
        return this;
    }

    public int getMlogIndent() {
        return mlogIndent >= 0 ? mlogIndent : symbolicLabels ? 4 : 0;
    }

    public CompilerProfile setMlogIndent(int mlogIndent) {
        this.mlogIndent = mlogIndent;
        return this;
    }

    public OptimizationLevel getOptimizationLevel(Optimization optimization) {
        return levels.getOrDefault(optimization, OptimizationLevel.NONE);
    }

    public Map<Optimization, OptimizationLevel> getOptimizationLevels() {
        return Map.copyOf(levels);
    }

    public int getOptimizationPasses() {
        return optimizationPasses;
    }

    public CompilerProfile setOptimizationPasses(int optimizationPasses) {
        this.optimizationPasses = Math.min(optimizationPasses, getMaxPasses());
        return this;
    }

    public int getParseTreeLevel() {
        return parseTreeLevel;
    }

    public CompilerProfile setParseTreeLevel(int parseTreeLevel) {
        this.parseTreeLevel = parseTreeLevel;
        return this;
    }

    public SourcePositionTranslator getPositionTranslator() {
        return positionTranslator;
    }

    public void setPositionTranslator(SourcePositionTranslator positionTranslator) {
        this.positionTranslator = positionTranslator;
    }

    public ProcessorEdition getProcessorEdition() {
        return processorEdition;
    }

    public CompilerProfile setProcessorEdition(ProcessorEdition processorEdition) {
        this.processorEdition = processorEdition;
        return this;
    }

    public ProcessorVersion getProcessorVersion() {
        return processorVersion;
    }

    public CompilerProfile setProcessorVersion(ProcessorVersion processorVersion) {
        this.processorVersion = processorVersion;
        return this;
    }

    public Remarks getRemarks() {
        return remarks;
    }

    public CompilerProfile setRemarks(Remarks remarks) {
        this.remarks = remarks;
        return this;
    }

    public List<SortCategory> getSortVariables() {
        return sortVariables;
    }

    public CompilerProfile setSortVariables(List<SortCategory> sortVariables) {
        this.sortVariables = sortVariables;
        return this;
    }

    public int getStepLimit() {
        return stepLimit;
    }

    public CompilerProfile setStepLimit(int stepLimit) {
        this.stepLimit = stepLimit;
        return this;
    }

    public SyntacticMode getSyntacticMode() {
        return syntacticMode;
    }

    public CompilerProfile setSyntacticMode(SyntacticMode syntacticMode) {
        this.syntacticMode = syntacticMode;
        return this;
    }

    public String getTarget() {
        String suffix = processorEdition == ProcessorEdition.W ? "w" : "";
        return switch (processorVersion) {
            case V6 -> "6";
            case V7 -> "7.0" + suffix;
            case V7A -> "7" + suffix;
            case V8A -> "8" + suffix;
            case MAX -> "8" + suffix;
        };
    }

    public CompilerProfile setTarget(String target) {
        String processor = target.endsWith("w") ? target.substring(0, target.length() - 1) : target;
        ProcessorEdition edition = target.endsWith("w") ? ProcessorEdition.W : ProcessorEdition.S;
        ProcessorVersion version = ProcessorVersion.byCode(processor);
        if (version == null) {
            throw new MindcodeInternalError("Unknown processor version: %s", processor);
        }
        setProcessorVersionEdition(version, edition);
        return this;
    }

    public int getTraceLimit() {
        return webApplication ? 1000 : 10_000;
    }

    public boolean isAutoPrintflush() {
        return autoPrintflush;
    }

    public CompilerProfile setAutoPrintflush(boolean autoPrintflush) {
        this.autoPrintflush = autoPrintflush;
        return this;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }

    public CompilerProfile setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
        return this;
    }

    public boolean isRun() {
        return run;
    }

    public CompilerProfile setRun(boolean run) {
        this.run = run;
        return this;
    }

    public boolean isShortCircuitEval() {
        return shortCircuitEval;
    }

    public CompilerProfile setShortCircuitEval(boolean shortCircuitEval) {
        this.shortCircuitEval = shortCircuitEval;
        return this;
    }

    public boolean isShortFunctionPrefix() {
        return shortFunctionPrefix;
    }

    public CompilerProfile setShortFunctionPrefix(boolean shortFunctionPrefix) {
        this.shortFunctionPrefix = shortFunctionPrefix;
        return this;
    }

    public boolean isSignature() {
        return signature;
    }

    public CompilerProfile setSignature(boolean signature) {
        this.signature = signature;
        return this;
    }

    public boolean isSymbolicLabels() {
        return symbolicLabels;
    }

    public CompilerProfile setSymbolicLabels(boolean symbolicLabels) {
        this.symbolicLabels = symbolicLabels;
        return this;
    }

    public boolean isWebApplication() {
        return webApplication;
    }

    public boolean optimizationsActive() {
        return levels.values().stream().anyMatch(l -> l != OptimizationLevel.NONE);
    }

    public CompilerProfile setAllOptimizationLevels(OptimizationLevel level) {
        Optimization.LIST.forEach(o -> levels.put(o, level));
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

    public CompilerProfile setOptimizationLevel(Optimization optimization, OptimizationLevel level) {
        this.levels.put(optimization, level);
        return this;
    }

    public CompilerProfile setProcessorVersionEdition(ProcessorVersion processorVersion, ProcessorEdition processorEdition) {
        this.processorVersion = processorVersion;
        this.processorEdition = processorEdition;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CompilerProfile profile = (CompilerProfile) o;
        return webApplication == profile.webApplication
               && instructionLimit == profile.instructionLimit
               && optimizationPasses == profile.optimizationPasses
               && symbolicLabels == profile.symbolicLabels
               && shortFunctionPrefix == profile.shortFunctionPrefix
               && linkedBlockGuards == profile.linkedBlockGuards
               && shortCircuitEval == profile.shortCircuitEval
               && autoPrintflush == profile.autoPrintflush
               && parseTreeLevel == profile.parseTreeLevel
               && debugLevel == profile.debugLevel
               && printStackTrace == profile.printStackTrace
               && signature == profile.signature
               && run == profile.run
               && stepLimit == profile.stepLimit
               && levels.equals(profile.levels)
               && processorVersion == profile.processorVersion
               && processorEdition == profile.processorEdition
               && fileReferences == profile.fileReferences
               && syntacticMode == profile.syntacticMode
               && goal == profile.goal
               && boundaryChecks == profile.boundaryChecks
               && remarks == profile.remarks
               && finalCodeOutput == profile.finalCodeOutput
               && sortVariables.equals(profile.sortVariables)
               && executionFlags.equals(profile.executionFlags)
               && additionalTags.equals(profile.additionalTags);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(webApplication);
        result = 31 * result + levels.hashCode();
        result = 31 * result + processorVersion.hashCode();
        result = 31 * result + processorEdition.hashCode();
        result = 31 * result + instructionLimit;
        result = 31 * result + optimizationPasses;
        result = 31 * result + fileReferences.hashCode();
        result = 31 * result + syntacticMode.hashCode();
        result = 31 * result + Boolean.hashCode(symbolicLabels);
        result = 31 * result + Boolean.hashCode(shortFunctionPrefix);
        result = 31 * result + Boolean.hashCode(linkedBlockGuards);
        result = 31 * result + goal.hashCode();
        result = 31 * result + boundaryChecks.hashCode();
        result = 31 * result + remarks.hashCode();
        result = 31 * result + Boolean.hashCode(shortCircuitEval);
        result = 31 * result + Boolean.hashCode(autoPrintflush);
        result = 31 * result + finalCodeOutput.hashCode();
        result = 31 * result + parseTreeLevel;
        result = 31 * result + debugLevel;
        result = 31 * result + Boolean.hashCode(printStackTrace);
        result = 31 * result + sortVariables.hashCode();
        result = 31 * result + Boolean.hashCode(signature);
        result = 31 * result + Boolean.hashCode(run);
        result = 31 * result + stepLimit;
        result = 31 * result + executionFlags.hashCode();
        result = 31 * result + additionalTags.hashCode();
        return result;
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
}
