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
import info.teksol.mc.mindcode.compiler.ast.nodes.AstAllocation;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstModule;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstProgram;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraphCreator;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraphCreatorContext;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.generation.*;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.compiler.generation.variables.VariablesContext;
import info.teksol.mc.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionArrayExpander;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionLabelResolver;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.compiler.preprocess.DirectivePreprocessor;
import info.teksol.mc.mindcode.compiler.preprocess.PreprocessorContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public class MindcodeCompiler extends AbstractMessageEmitter implements AstBuilderContext, PreprocessorContext,
        CallGraphCreatorContext, CompileTimeEvaluatorContext, CodeGeneratorContext, VariablesContext {
    // MindcodeCompiler serves as a compiler context too
    private static final ThreadLocal<MindcodeCompiler> context = new ThreadLocal<>();

    // Inputs, configurations and tools
    private final CompilationPhase targetPhase;
    private final CompilerProfile profile;
    private final InputFiles inputFiles;
    private @Nullable InstructionProcessor instructionProcessor;
    private @Nullable CompileTimeEvaluator compileTimeEvaluator;

    // Intermediate and final results
    private final Map<InputFile, CommonTokenStream> tokenStreams = new HashMap<>();
    private final Map<InputFile, AstModuleContext> parseTrees = new HashMap<>();
    private final Map<InputFile, AstModule> modules = new HashMap<>();
    private final List<AstRequire> requirements = new ArrayList<>();
    private final ReturnStack returnStack;
    private final StackTracker stackTracker;
    private @Nullable AstProgram astProgram;
    private @Nullable AstAllocation heapAllocation;
    private @Nullable CallGraph callGraph;
    private @Nullable AstContext rootAstContext;
    private @Nullable CodeAssembler assembler;
    private @Nullable Variables variables;

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

    // Message logger
    private final ListMessageLogger messageLogger;

    public MindcodeCompiler(MessageConsumer messageConsumer, CompilerProfile profile, InputFiles inputFiles) {
        this(CompilationPhase.ALL, messageConsumer, profile, inputFiles);
    }

    public MindcodeCompiler(CompilationPhase targetPhase, MessageConsumer messageConsumer,
            CompilerProfile profile, InputFiles inputFiles) {
        super(new ListMessageLogger(
                new TranslatingMessageConsumer(messageConsumer, profile.getPositionTranslator())));
        this.messageLogger = (ListMessageLogger) super.messageConsumer();
        this.targetPhase = targetPhase;
        this.profile = profile;
        this.inputFiles = inputFiles;
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

    public void compile() {
        compile(new LinkedList<>(inputFiles.getInputFiles()));
    }

    public void compile(InputFile file) {
        compile(new LinkedList<>(List.of(file)));
    }

    private void compile(Queue<InputFile> files) {
        Set<InputFile> processedFiles = new HashSet<>();
        List<AstModule> moduleList = new LinkedList<>();
        RequirementsProcessor requirementsProcessor = new RequirementsProcessor(messageConsumer, profile, inputFiles);

        long parseTime = 0;
        // Process all input files including files discovered through require directive.
        try {
            while (!files.isEmpty()) {
                InputFile inputFile = files.remove();
                if (processedFiles.add(inputFile)) {
                    long parseStart = System.nanoTime();
                    CommonTokenStream tokenStream = LexerParser.createTokenStream(messageConsumer, inputFile);
                    tokenStreams.put(inputFile, tokenStream);
                    if (targetPhase == CompilationPhase.LEXER) continue;

                    AstModuleContext parseTree = LexerParser.parseTree(messageConsumer, inputFile, tokenStream);
                    parseTrees.put(inputFile, parseTree);
                    if (targetPhase == CompilationPhase.PARSER) continue;

                    AstModule module = AstBuilder.build(this, inputFile, tokenStream, parseTree);
                    modules.put(inputFile, module);
                    moduleList.addFirst(module);
                    parseTime += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

                    // Requirements are added by the AstBuilder via AstBuilderContext
                    requirements.stream()
                            .map(requirementsProcessor::processRequirement)
                            .filter(Objects::nonNull)
                            .forEach(files::add);
                }
            }
        } catch (ParserAbort ignored) {
            // The error has already been reported
        }

        astProgram = new AstProgram(new SourcePosition(inputFiles.getMainInputFile(), 1, 1), moduleList);
        if (hasErrors() || targetPhase.compareTo(CompilationPhase.AST_BUILDER) <= 0) return;

        if (profile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(new AstIndentedPrinter().print(astProgram));
            debug("");
        }

        long compileStart = System.nanoTime();
        DirectivePreprocessor.processDirectives(this, astProgram);

        instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);

        callGraph = CallGraphCreator.createCallGraph(this, astProgram);

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.CALL_GRAPH) <= 0) return;

        rootAstContext = AstContext.createRootNode(profile);
        variables = new Variables(this);
        assembler = new CodeAssembler(this);
        compileTimeEvaluator = new CompileTimeEvaluator(this);

        new CodeGenerator(this).generateCode(astProgram);

        if (assembler.isInternalError() && !hasErrors()) {
            throw new MindcodeInternalError("Internal error encountered.");
        }
        unoptimized = assembler.getInstructions();
        instructions = unoptimized;
        long compileTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - compileStart);
        if (hasErrors() || targetPhase.compareTo(CompilationPhase.COMPILER) <= 0) return;

        LogicInstructionArrayExpander arrayExpander = new LogicInstructionArrayExpander(profile, instructionProcessor);

        // OPTIMIZE
        long optimizeStart = System.nanoTime();
        if (profile.optimizationsActive() && instructions.size() > 1) {
            final DebugPrinter debugPrinter = profile.getDebugLevel() > 0 && profile.optimizationsActive()
                    ? debugPrinterProvider.apply(profile.getDebugLevel()) : new NullDebugPrinter();
            OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, profile, messageConsumer, arrayExpander);
            optimizer.setDebugPrinter(debugPrinter);
            instructions = optimizer.optimize(callGraph, instructions, rootAstContext);
            debugPrinter.print(this::debug);
        }
        unresolved = instructions;
        long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.OPTIMIZER) <= 0) return;

        // Run program through the array expander again, as optimizations might have been inactive.
        instructions = arrayExpander.expandArrayInstructions(instructions);

        // Sort variables
        LogicInstructionLabelResolver resolver = new LogicInstructionLabelResolver(profile, instructionProcessor);
        instructions = resolver.sortVariables(instructions);

        // Print unresolved code
        if (profile.getFinalCodeOutput() != null) {
            debug("\nFinal code before resolving virtual instructions:\n");
            debug(LogicInstructionPrinter.toString(profile.getFinalCodeOutput(), instructionProcessor, instructions));
        }

        // Label resolving
        instructions = resolver.resolveLabels(instructions);

        output = LogicInstructionPrinter.toString(instructionProcessor, instructions);

        if (hasErrors() || targetPhase.compareTo(CompilationPhase.PRINTER) <= 0) return;

        // RUN if requested
        // Timing output
        if (profile.isRun()) {
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

    private Processor createEmulator() {
        Objects.requireNonNull(instructionProcessor);

        // All flags are already set as we want them to be
        Processor processor = new Processor(instructionProcessor, messageConsumer, profile.getExecutionFlags(), profile.getTraceLimit());
        addBlocks(processor, "cell", i -> Memory.createMemoryCell());
        addBlocks(processor, "bank", i -> Memory.createMemoryBank());
        addBlocks(processor, "display", i -> LogicDisplay.createLogicDisplay(i < 5));
        addBlocks(processor, "message", i -> MessageBlock.createMessage());
        return processor;
    }

    private void addBlocks(Processor processor, String name, Function<Integer, MindustryBlock> creator) {
        for (int i = 1; i < 10; i++) {
            processor.addBlock(name + i, creator.apply(i));
        }
    }

    private void run(List<LogicInstruction> instructions) {
        List<LogicInstruction> program = instructions.stream().map(instructionProcessor()::convertCustomInstruction).toList();
        emulator = createEmulator();
        emulatorInitializer.accept(emulator);

        try {
            emulator.run(program, profile.getStepLimit());
        } catch (ExecutionException e) {
            executionException = e;
        }
        assertions = emulator.getAssertions();
        textBuffer = emulator.getTextBuffer();
        steps = emulator.getSteps();
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

    public Processor getEmulator() {
        return Objects.requireNonNull(emulator);
    }

    // Context implementations
    @Override
    public MessageConsumer messageConsumer() {
        return super.messageConsumer();
    }

    @Override
    public CompilerProfile compilerProfile() {
        return profile;
    }

    @Override
    public InstructionProcessor instructionProcessor() {
        return Objects.requireNonNull(instructionProcessor);
    }

    @Override
    public CompileTimeEvaluator compileTimeEvaluator() {
        return Objects.requireNonNull(compileTimeEvaluator);
    }

    @Override
    public void addRequirement(AstRequire requirement) {
        requirements.add(requirement);
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
}
