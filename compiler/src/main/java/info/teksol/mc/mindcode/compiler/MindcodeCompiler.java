package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.blocks.Memory;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.blocks.MindustryBlock;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.emulator.processor.Assertion;
import info.teksol.mc.emulator.processor.ExecutionException;
import info.teksol.mc.emulator.processor.Processor;
import info.teksol.mc.emulator.processor.TextBuffer;
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
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.generation.*;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionArrayExpander;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionLabelResolver;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.compiler.preprocess.DirectivePreprocessor;
import info.teksol.mc.mindcode.compiler.preprocess.PreprocessorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class MindcodeCompiler extends AbstractMessageEmitter implements AstBuilderContext, PreprocessorContext,
        CallGraphCreatorContext, CompileTimeEvaluatorContext, CodeGeneratorContext, VariablesContext, OptimizerContext {

    public static final String REMOTE_PROTOCOL_VERSION = "v1";

    // MindcodeCompiler serves as a compiler context too
    private static final ThreadLocal<MindcodeCompiler> context = new ThreadLocal<>();

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
    private final List<LogicVariable> forcedVariables = new ArrayList<>();
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
    private Consumer<Processor> emulatorInitializer = emulator -> {};
    private @Nullable Processor emulator;
    private @Nullable ExecutionException executionException;
    private List<Assertion> assertions = List.of();
    private TextBuffer textBuffer = new TextBuffer(0, 0, true);
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
        this.directiveProcessor = new DirectiveProcessor(messageConsumer);
        returnStack = new ReturnStack();
        stackTracker = new StackTracker(messageConsumer);

        context.set(this);
    }

    public void setEmulatorInitializer(Consumer<Processor> emulatorInitializer) {
        this.emulatorInitializer = emulatorInitializer;
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
                                foundProcessors.computeIfAbsent(processor, k -> new ArrayList<>()).add(processor);
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

        if (globalProfile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(new AstIndentedPrinter().print(astProgram));
            debug("");
        }

        long compileStart = System.nanoTime();

        DirectivePreprocessor.processGlobalDirectives(this, globalProfile, astProgram.getMainModule());
        astProgram.getModules().stream().filter(m -> !m.isMain())
                .forEach(module -> DirectivePreprocessor.processModuleDirectives(this, globalProfile, module));

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

        LogicInstructionArrayExpander arrayExpander = new LogicInstructionArrayExpander();

        // OPTIMIZE
        long optimizeStart = System.nanoTime();
        if (globalProfile.optimizationsActive() && instructions.size() > 1) {
            final DebugPrinter debugPrinter = globalProfile.getDebugMessages() > 0 && globalProfile.optimizationsActive()
                    ? debugPrinterProvider.apply(globalProfile.getDebugMessages()) : new NullDebugPrinter();
            OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, globalProfile, messageConsumer,
                    this, arrayExpander, !astProgram.isMainProgram());
            optimizer.setDebugPrinter(debugPrinter);
            instructions = optimizer.optimize(callGraph, instructions, rootAstContext);
            debugPrinter.print(this::debug);
        }
        unresolved = instructions;
        long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.OPTIMIZER) <= 0) return;

        // Run the program through the array expander again, as optimizations might have been inactive.
        instructions = arrayExpander.expandArrayInstructions(instructions);

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

        // Set of all user variables in the program
        generateUnusedVolatileWarnings();

        output = LogicInstructionPrinter.toString(instructionProcessor, resolver.generateSymbolicLabels(instructions),
                globalProfile.isSymbolicLabels(), globalProfile.getMlogIndent());

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.PRINTER) <= 0) return;

        // RUN if requested
        // Timing output
        if (globalProfile.isRun()) {
            long runStart = System.nanoTime();
            run(instructions);
            long runTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStart);
            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms, run in %,d ms.",
                    parseTime, compileTime, optimizeTime, runTime);
        } else {
            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.",
                    parseTime, compileTime, optimizeTime);
        }
    }

    private void generateUnusedVolatileWarnings() {
        Set<LogicArgument> existing = instructions.stream()
                .flatMap(ix -> ix.inputOutputArgumentsStream().filter(LogicArgument::isUserVariable))
                .collect(Collectors.toSet());

        assert variables != null;
        variables.getVolatileVariables().stream()
                .filter(Predicate.not(existing::contains))
                .sorted(Comparator.comparing(LogicVariable::sourcePosition))
                .forEach(v -> warn(v.sourcePosition(), WARN.VOLATILE_VARIABLE_NOT_USED, v.getName()));
    }

    private Processor createEmulator() {
        assert instructionProcessor != null;
        assert metadata != null;

        // All flags are already set as we want them to be
        Processor processor = new Processor(instructionProcessor, messageConsumer, globalProfile.getExecutionFlags(), globalProfile.getTraceLimit());
        addBlocks(processor, "cell", i -> Memory.createMemoryCell(metadata));
        addBlocks(processor, "bank", i -> Memory.createMemoryBank(metadata));
        addBlocks(processor, "display", i -> LogicDisplay.createLogicDisplay(metadata, i < 5));
        addBlocks(processor, "message", i -> MessageBlock.createMessage(metadata));
        return processor;
    }

    private void addBlocks(Processor processor, String name, Function<Integer, MindustryBlock> creator) {
        for (int i = 1; i < 10; i++) {
            processor.addBlock(name + i, creator.apply(i));
        }
    }

    private void run(List<LogicInstruction> instructions) {
        List<LogicInstruction> program = executableInstructions.stream()
                .map(instructionProcessor()::convertCustomInstruction)
                .toList();
        emulator = createEmulator();
        emulatorInitializer.accept(emulator);

        try {
            emulator.run(program, globalProfile.getStepLimit());
        } catch (ExecutionException e) {
            executionException = e;
        }
        assertions = emulator.getAssertions();
        textBuffer = emulator.getTextBuffer();
        steps = emulator.getSteps();
        executionProfile = emulator.getProfile();
    }

    public boolean hasErrors() {
        return messageLogger.hasErrors();
    }

    // Root method for obtaining compiler contexts
    // Allows finding all out-of-line usages of compiler context through call hierarchy.
    public static MindcodeCompiler getContext() {
        return context.get();
    }

    public static CompileTimeEvaluatorContext getCompileTimeEvaluatorContext() {
        return getContext();
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

    public @Nullable ExecutionException getExecutionException() {
        return executionException;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public TextBuffer getTextBuffer() {
        return textBuffer;
    }

    public int getSteps() {
        return steps;
    }

    public int[] getExecutionProfile() {
        return executionProfile;
    }

    public Processor getEmulator() {
        return Objects.requireNonNull(emulator);
    }

    public boolean isInternalError() {
        return internalError;
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

    public void addDiagnosticData(Object data) {
        diagnosticData.computeIfAbsent(data.getClass(), k -> new ArrayList<>()).add(data);
    }

    public <T> void addDiagnosticData(Class<T> dataClass, List<T> data) {
        diagnosticData.computeIfAbsent(dataClass, k -> new ArrayList<>()).addAll(data);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getDiagnosticData(Class<T> type) {
        return (List<T>) diagnosticData.getOrDefault(type, List.of());
    }

    private record ModulePlacement(InputFile inputFile, SortedSet<AstIdentifier> remoteProcessors) {
    }
}
