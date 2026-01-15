package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.blocks.*;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.generated.ast.AstIndentedPrinter;
import info.teksol.mc.messages.*;
import info.teksol.mc.mindcode.compiler.antlr.LexerParser;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeParser.AstModuleContext;
import info.teksol.mc.mindcode.compiler.ast.AstBuilder;
import info.teksol.mc.mindcode.compiler.ast.AstBuilderContext;
import info.teksol.mc.mindcode.compiler.ast.ParserAbort;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraphCreator;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraphCreatorContext;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.generation.*;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionLabelResolver;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.compiler.postprocess.VirtualInstructionResolver;
import info.teksol.mc.mindcode.compiler.preprocess.DirectivePreprocessor;
import info.teksol.mc.mindcode.compiler.preprocess.PreprocessorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructorContext;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.DirectiveProcessor;
import info.teksol.mc.profile.FinalCodeOutput;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.util.CollectionUtils;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class MindcodeCompiler extends AbstractMessageEmitter implements AstBuilderContext, PreprocessorContext,
        ArrayConstructorContext, CallGraphCreatorContext, CompileTimeEvaluatorContext, CodeGeneratorContext,
        VariablesContext, ForcedVariableContext, OptimizerContext {

    public static final String REMOTE_PROTOCOL_VERSION = "v1";

    // Inputs, configurations, and tools
    private final CompilationPhase targetPhase;
    private final CompilerProfile globalProfile;
    private final InputFiles inputFiles;
    private final DirectiveProcessor directiveProcessor;
    private @Nullable InstructionProcessor instructionProcessor;
    private @Nullable NameCreator nameCreator;
    private @Nullable MindustryMetadata metadata;
    private @Nullable CompileTimeEvaluator compileTimeEvaluator;

    // Intermediate and final results
    private final Map<InputFile, CommonTokenStream> tokenStreams = new HashMap<>();
    private final Map<InputFile, AstModuleContext> parseTrees = new HashMap<>();
    private final Map<InputFile, AstModule> modules = new HashMap<>();
    private final Map<AstRequire, InputFile> requiredFiles = new HashMap<>();
    private final List<AstRequire> requirements = new ArrayList<>();
    private final Set<LogicVariable> forcedVariables = new LinkedHashSet<>();
    private final ReturnStack returnStack;
    private final StackTracker stackTracker;
    private @Nullable AstProgram astProgram;
    private @Nullable AstAllocation heapAllocation;
    private @Nullable CallGraph callGraph;
    private @Nullable AstContext rootAstContext;
    private @Nullable CodeAssembler assembler;
    private @Nullable Variables variables;

    private List<LogicInstruction> executableInstructions = List.of();
    private List<LogicInstruction> instructions = List.of();
    private List<LogicInstruction> unoptimized = List.of();
    private List<LogicInstruction> unresolved = List.of();
    private String output = "";

    private Function<Integer, DebugPrinter> debugPrinterProvider = DiffDebugPrinter::new;

    // Run output
    private Consumer<LogicBlock> logicBlockInitializer = _ -> {};
    private @Nullable Emulator emulator;
    private boolean runtimeError = false;
    private List<Assertion> assertions = List.of();
    private int steps;
    private int[] executionProfile = new int[0];

    // Message logger
    private final ListMessageLogger messageLogger;
    private boolean internalError;

    // Diagnostic data
    private final Map<Class<?>, List<Object>> diagnosticData = new HashMap<>();

    public MindcodeCompiler(MessageConsumer messageConsumer, CompilerProfile globalProfile, InputFiles inputFiles) {
        this(CompilationPhase.ALL, messageConsumer, globalProfile, inputFiles);
    }

    public MindcodeCompiler(CompilationPhase targetPhase, MessageConsumer messageConsumer,
            CompilerProfile globalProfile, InputFiles inputFiles) {
        super(new ListMessageLogger(
                new TranslatingMessageConsumer(messageConsumer, globalProfile.getPositionTranslator())));
        this.messageLogger = (ListMessageLogger) super.messageConsumer();
        this.targetPhase = targetPhase;
        this.globalProfile = globalProfile;
        this.inputFiles = inputFiles;
        this.directiveProcessor = new DirectiveProcessor(messageLogger);
        returnStack = new ReturnStack();
        stackTracker = new StackTracker(messageLogger);

        ContextFactory.setCompilerContext(this);
    }

    public void setLogicBlockInitializer(Consumer<LogicBlock> logicBlockInitializer) {
        this.logicBlockInitializer = logicBlockInitializer;
    }

    public void setDebugPrinterProvider(Function<Integer, DebugPrinter> debugPrinterProvider) {
        this.debugPrinterProvider = debugPrinterProvider;
    }

    public void safeCompile() {
        try {
            compile();
        } catch (Exception e) {
            internalError = true;
            error(ERR.INTERNAL_ERROR);
        }
    }

    public void compile() {
        compile(inputFiles.getInputFiles());
    }

    public void compile(InputFile file) {
        compile(List.of(file));
    }

    private void compile(List<InputFile> files) {
        Queue<ModulePlacement> inputs = files.stream()
                .map(f -> new ModulePlacement(f, Collections.emptySortedSet()))
                .collect(Collectors.toCollection(LinkedList::new));
        Set<InputFile> processedFiles = new HashSet<>();
        List<AstModule> moduleList = new LinkedList<>();
        RequirementsProcessor requirementsProcessor = new RequirementsProcessor(messageConsumer, globalProfile, inputFiles);
        Map<AstIdentifier, List<AstIdentifier>> foundProcessors = new HashMap<>();

        long parseTime = 0;
        // Process all input files including files discovered through the `require` directive.
        // The first processed module is the main one
        try {
            while (!inputs.isEmpty()) {
                ModulePlacement input = inputs.remove();
                if (processedFiles.add(input.inputFile)) {
                    requirements.clear();

                    long parseStart = System.nanoTime();
                    CommonTokenStream tokenStream = LexerParser.createTokenStream(messageConsumer, input.inputFile);
                    tokenStreams.put(input.inputFile, tokenStream);
                    if (targetPhase == CompilationPhase.LEXER) continue;

                    AstModuleContext parseTree = LexerParser.parseTree(messageConsumer, input.inputFile, tokenStream);
                    parseTrees.put(input.inputFile, parseTree);
                    if (targetPhase == CompilationPhase.PARSER) continue;

                    // The main module is the first processed one -- the list is still empty
                    AstModule module = AstBuilder.build(this, input.inputFile, tokenStream, parseTree,
                            input.remoteProcessors, moduleList.isEmpty());
                    modules.put(input.inputFile, module);
                    if (!module.isMain() && module.getDeclaration() == null) {
                        error(new SourcePosition(input.inputFile, 1, 1), ERR.MISSING_MODULE_DECLARATION);
                    }
                    moduleList.addFirst(module);
                    parseTime += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

                    Set<InputFile> inputFilesInModule = new HashSet<>();

                    // Requirements are added by the AstBuilder via AstBuilderContext
                    for (AstRequire requirement : requirements) {
                        InputFile inputFile = requirementsProcessor.loadFile(requirement);

                        // Error (already reported) loading or resolving the file
                        if (inputFile == null) continue;

                        if (!inputFilesInModule.add(inputFile)) {
                            error(requirement, ERR.MULTIPLE_MODULE_REQUESTS, requirement.getName());
                            continue;
                        }

                        requiredFiles.put(requirement, inputFile);

                        // Do not process requirements of modules representing remote processors
                        if (input.remoteProcessors.isEmpty()) {
                            inputs.add(new ModulePlacement(inputFile, requirement.getProcessors()));

                            for (AstIdentifier processor : requirement.getProcessors()) {
                                foundProcessors.computeIfAbsent(processor, _ -> new ArrayList<>()).add(processor);
                            }
                        }
                    }
                }
            }
        } catch (ParserAbort ignored) {
            // The error has already been reported
        }

        // Detect and report errors where the same processor is used with different modules
        foundProcessors.values().stream().filter(l -> l.size() > 1)
                .flatMap(Collection::stream)
                .forEach(p -> error(p, ERR.MULTIPLE_PROCESSOR_BINDINGS, p.getName()));

        if (!modules.isEmpty()) {
            astProgram = new AstProgram(globalProfile, new SourcePosition(inputFiles.getMainInputFile(), 1, 1), moduleList);
        }
        if (hasErrors() || targetPhase.compareTo(CompilationPhase.AST_BUILDER) <= 0) return;

        if (astProgram == null) {
            throw new MindcodeInternalError("Program is empty.");
        }

        long compileStart = System.nanoTime();

        DirectivePreprocessor.processGlobalDirectives(this, globalProfile, astProgram.getMainModule());
        astProgram.getModules().stream().filter(m -> !m.isMain())
                .forEach(module -> DirectivePreprocessor.processModuleDirectives(this, globalProfile, module));

        if (globalProfile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(new AstIndentedPrinter().print(astProgram));
            debug("");
        }

        DirectivePreprocessor.processLocalDirectives(this, globalProfile, astProgram);

        if (targetPhase.compareTo(CompilationPhase.PREPROCESSOR) <= 0) return;

        nameCreator = new StandardNameCreator(globalProfile);
        instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, nameCreator, globalProfile);
        metadata = instructionProcessor.getMetadata();

        callGraph = CallGraphCreator.createCallGraph(this, astProgram);

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.CALL_GRAPH) <= 0) return;

        rootAstContext = AstContext.createRootNode(globalProfile);
        variables = new Variables(this);
        assembler = new CodeAssembler(this);
        compileTimeEvaluator = new CompileTimeEvaluator(this);

        CodeGenerator.generateCode(this, astProgram);

        if (assembler.isInternalError() && !hasErrors()) {
            throw new MindcodeInternalError("Internal error encountered.");
        }
        unoptimized = assembler.getInstructions();
        instructions = unoptimized;
        long compileTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - compileStart);
        if (hasErrors() || targetPhase.compareTo(CompilationPhase.COMPILER) <= 0) return;

        VirtualInstructionResolver virtualInstructionResolver = new VirtualInstructionResolver(instructionProcessor);

        // OPTIMIZE
        long optimizeStart = System.nanoTime();
        if (globalProfile.optimizationsActive() && instructions.size() > 1) {
            final DebugPrinter debugPrinter = globalProfile.getDebugMessages() > 0 && globalProfile.optimizationsActive()
                    ? debugPrinterProvider.apply(globalProfile.getDebugMessages()) : new NullDebugPrinter();
            OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, globalProfile, messageConsumer,
                    this, virtualInstructionResolver, !astProgram.isMainProgram());
            optimizer.setDebugPrinter(debugPrinter);
            instructions = optimizer.optimize(callGraph, instructions, rootAstContext);
            debugPrinter.print(this::debug);
        }
        unresolved = instructions;
        long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.OPTIMIZER) <= 0) return;

        // Run the program through the array expander again, as optimizations might have been inactive.
        instructions = virtualInstructionResolver.resolveVirtualInstructions(instructions);

        // Check there are no direct access instructions
        if (globalProfile.isSymbolicLabels()) {
            boolean hasLabels = instructions.stream()
                    .filter(CollectionUtils.resultIn(LogicInstruction::getOpcode, JUMP, CALL, CALLREC, LABEL, MULTIJUMP, MULTILABEL).negate())
                    .flatMap(MlogInstruction::inputArgumentsStream)
                    .anyMatch(LogicLabel.class::isInstance);

            boolean hasDirectAssignment = instructions.stream()
                    .filter(OpInstruction.class::isInstance)
                    .map(OpInstruction.class::cast)
                    .anyMatch(ix -> ix.getResultArgument().equals(LogicBuiltIn.COUNTER)
                            && !ix.getX().equals(LogicBuiltIn.COUNTER) && !ix.getY().equals(LogicBuiltIn.COUNTER));

            if (hasLabels || hasDirectAssignment) {
                warn(WARN.ABSOLUTE_ADDRESSING);
            }
        }

        // Sort variables
        LogicInstructionLabelResolver resolver = new LogicInstructionLabelResolver(globalProfile, instructionProcessor, rootAstContext);
        instructions = resolver.sortVariables(instructions);

        // Print unresolved code
        if (globalProfile.getFinalCodeOutput() != FinalCodeOutput.NONE) {
            debug("\nFinal code before resolving virtual instructions:\n");
            debug(LogicInstructionPrinter.toString(globalProfile.getFinalCodeOutput(), instructionProcessor, instructions));
        }

        // Label resolving
        instructions = resolver.resolveLabels(instructions, forcedVariables);
        executableInstructions = instructions.stream().filter(ix -> !(ix instanceof CommentInstruction)).toList();

        if (globalProfile.isPrintCodeSize()) {
            outputFunctionSizes();
        }

        output = LogicInstructionPrinter.toString(instructionProcessor, resolver.generateSymbolicLabels(instructions),
                globalProfile.isSymbolicLabels(), globalProfile.getMlogIndent());

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.PRINTER) <= 0) return;

        // RUN if requested
        // Timing output
        if (globalProfile.isRun()) {
            long runStart = System.nanoTime();
            run();
            long runTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStart);
            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms, run in %,d ms.",
                    parseTime, compileTime, optimizeTime, runTime);
        } else {
            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.",
                    parseTime, compileTime, optimizeTime);
        }
    }

    private String functionName(LogicInstruction  instruction) {
        MindcodeFunction function = instruction.getAstContext().getFunctionBody();
        return function == null || function.isMain()
                ? "<no function>"
                : function.getDeclaration().toSourceCode();
    }

    private int functionCopyNumber(LogicInstruction instruction) {
        return instruction.getAstContext().getFunctionCopyNumber();
    }

    private void outputFunctionSizes() {
        List<LogicInstruction> list = executableInstructions.stream().filter(ix -> ix.getOpcode() != LABEL).toList();
        Map<String, Integer> sizes = list.stream()
                .collect(Collectors.groupingBy(this::functionName, Collectors.summingInt(_ -> 1)));

        Map<String, Integer> instances = list.stream().collect(Collectors.groupingBy(this::functionName,
                Collectors.collectingAndThen(Collectors.mapping(this::functionCopyNumber,
                        Collectors.toSet()), Set::size)));

        StringBuilder sbr = new StringBuilder()
                .append("\nCode size and number of instantiations by function:")
                .append("\n  Size  Times  AvgSize  Function");
        Formatter fmt = new Formatter(sbr);
        sizes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(e -> {
                    int size = e.getValue();
                    int number = instances.getOrDefault(e.getKey(), 1);
                    fmt.format("\n%6d  %4dx  %7.1f  %s", e.getValue(), number, (double) size / number, e.getKey());
                });
        fmt.close();

        info("%s", sbr);
    }

    private void addBlocks(LogicBlock logicBlock, String name, Function<Integer, MindustryBuilding> creator) {
        for (int i = 1; i < 10; i++) {
            logicBlock.addBlock(name + i, creator.apply(i));
        }
    }

    private void initializeLogicBlock(LogicBlock logicBlock) {
        assert metadata != null;

        // All flags are already set as we want them to be
        addBlocks(logicBlock, "cell", _ -> MemoryBlock.createMemoryCell(metadata, BlockPosition.ZERO_POSITION));
        addBlocks(logicBlock, "bank", _ -> MemoryBlock.createMemoryBank(metadata, BlockPosition.ZERO_POSITION));
        addBlocks(logicBlock, "display", i -> LogicDisplay.createLogicDisplay(metadata, i < 5, BlockPosition.ZERO_POSITION));
        addBlocks(logicBlock, "message", _ -> MessageBlock.createMessage(metadata, BlockPosition.ZERO_POSITION));

        logicBlockInitializer.accept(logicBlock);
    }

    private void run() {
        assert metadata != null;
        LogicBlock logicBlock = LogicBlock.createProcessor(metadata, compilerProfile().getEmulatorProcessor(),
                BlockPosition.ZERO_POSITION, output);
        initializeLogicBlock(logicBlock);
        EmulatorSchematic schematic = new EmulatorSchematic(List.of(logicBlock));
        emulator = new BasicEmulator(messageConsumer, globalProfile, schematic, globalProfile.getTraceLimit());

        emulator.run(globalProfile.getStepLimit());
        runtimeError = emulator.isError();
        assertions = emulator.getAllAssertions();
        steps = emulator.executionSteps();
        executionProfile = emulator.getExecutorResults(0).getProfile();
    }

    public boolean hasErrors() {
        return messageLogger.hasErrors();
    }

    // Compiler outputs
    public CommonTokenStream getTokenStream(InputFile inputFile) {
        return tokenStreams.get(inputFile);
    }

    public AstModuleContext getParseTree(InputFile inputFile) {
        return parseTrees.get(inputFile);
    }

    public AstModule getModule(InputFile inputFile) {
        return modules.get(inputFile);
    }

    public AstProgram getAstProgram() {
        return Objects.requireNonNull(astProgram);
    }

    public CallGraph getCallGraph() {
        return Objects.requireNonNull(callGraph);
    }

    public AstContext getRootAstContext() {
        return Objects.requireNonNull(rootAstContext);
    }

    public List<MindcodeMessage> getMessages() {
        return messageLogger.getMessages();
    }

    public List<LogicInstruction> getExecutableInstructions() {
        return executableInstructions;
    }

    public List<LogicInstruction> getInstructions() {
        return instructions;
    }

    public List<LogicInstruction> getUnoptimized() {
        return unoptimized;
    }

    public List<LogicInstruction> getUnresolved() {
        return unresolved;
    }

    public String getOutput() {
        return output;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public String getTextBufferOutput() {
        return emulator == null ? "" : emulator.getExecutorResults(0).getFormattedOutput();
    }

    public int getSteps() {
        return steps;
    }

    public int[] getExecutionProfile() {
        return executionProfile;
    }

    public Emulator getEmulator() {
        return Objects.requireNonNull(emulator);
    }

    public boolean isInternalError() {
        return internalError;
    }

    public boolean isRuntimeError() {
        return runtimeError;
    }

    // Context implementations
    @Override
    public MessageConsumer messageConsumer() {
        return super.messageConsumer();
    }

    @Override
    public DirectiveProcessor directiveProcessor() {
        return directiveProcessor;
    }

    @Override
    public GlobalCompilerProfile globalCompilerProfile() {
        return globalProfile;
    }

    public CompilerProfile compilerProfile() {
        return globalProfile;
    }

    @Override
    public InstructionProcessor instructionProcessor() {
        return Objects.requireNonNull(instructionProcessor);
    }

    @Override
    public NameCreator nameCreator() {
        return Objects.requireNonNull(nameCreator);
    }

    @Override
    public MindustryMetadata metadata() {
        return Objects.requireNonNull(metadata);
    }

    @Override
    public CompileTimeEvaluator compileTimeEvaluator() {
        return Objects.requireNonNull(compileTimeEvaluator);
    }

    @Override
    public void addRequirement(AstRequire requirement) {
        requirements.add(requirement);
    }

    public AstModule getModule(AstRequire node) {
        return modules.get(requiredFiles.get(node));
    }

    @Override
    public void setHeapAllocation(AstAllocation heapAllocation) {
        this.heapAllocation = heapAllocation;
    }

    @Override
    public ReturnStack returnStack() {
        return returnStack;
    }

    @Override
    public StackTracker stackTracker() {
        return stackTracker;
    }

    @Override
    public @Nullable AstAllocation heapAllocation() {
        return heapAllocation;
    }

    @Override
    public CallGraph callGraph() {
        return Objects.requireNonNull(callGraph);
    }

    @Override
    public AstContext rootAstContext() {
        return Objects.requireNonNull(rootAstContext);
    }

    @Override
    public CodeAssembler assembler() {
        return Objects.requireNonNull(assembler);
    }

    @Override
    public Variables variables() {
        return Objects.requireNonNull(variables);
    }

    @Override
    public void addForcedVariable(LogicVariable variable) {
        forcedVariables.add(variable);
    }

    public Set<LogicVariable> getForcedVariables() {
        return forcedVariables;
    }

    public void addDiagnosticData(Object data) {
        diagnosticData.computeIfAbsent(data.getClass(), _ -> new ArrayList<>()).add(data);
    }

    public <T> void addDiagnosticData(Class<T> dataClass, List<T> data) {
        diagnosticData.computeIfAbsent(dataClass, _ -> new ArrayList<>()).addAll(data);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getDiagnosticData(Class<T> type) {
        return (List<T>) diagnosticData.getOrDefault(type, List.of());
    }

    private record ModulePlacement(InputFile inputFile, SortedSet<AstIdentifier> remoteProcessors) {
    }
}
