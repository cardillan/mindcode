package info.teksol.mindcode.v3;

import info.teksol.generated.ast.AstIndentedPrinter;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ParserAbort;
import info.teksol.mindcode.ast.Requirement;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.AstModuleContext;
import info.teksol.mindcode.v3.compiler.ast.AstBuilder;
import info.teksol.mindcode.v3.compiler.ast.AstBuilderContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstModule;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstProgram;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraphCreator;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraphCreatorContext;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mindcode.v3.compiler.generation.*;
import info.teksol.mindcode.v3.compiler.generation.variables.Variables;
import info.teksol.mindcode.v3.compiler.preprocessor.DirectivePreprocessor;
import info.teksol.mindcode.v3.compiler.preprocessor.PreprocessorContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class MindcodeCompiler extends AbstractMessageEmitter implements AstBuilderContext, PreprocessorContext,
        CallGraphCreatorContext, CompileTimeEvaluatorContext, CodeGeneratorContext {
    // MindcodeCompiler serves as a compiler context too
    private static final ThreadLocal<MindcodeCompiler> context = new ThreadLocal<>();

    // Global parser cache
    private record ParsedLibrary(AstProgram program, List<Requirement> requirements) { }
    private static final Map<InputFile, ParsedLibrary> LIBRARY_PARSES = new ConcurrentHashMap<>();

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
    private @Nullable AstContext topAstContext;
    private @Nullable CodeAssembler assembler;
    private @Nullable Variables variables;

    // Error detection
    private final ErrorsDetector errorsDetector;

    public MindcodeCompiler(CompilationPhase targetPhase, MessageConsumer messageConsumer,
            CompilerProfile profile, InputFiles inputFiles) {
        super(new ErrorsDetector( messageConsumer));
        this.errorsDetector = (ErrorsDetector) super.messageConsumer();
        this.targetPhase = targetPhase;
        this.profile = profile;
        this.inputFiles = inputFiles;
        returnStack = new ReturnStack();
        stackTracker = new StackTracker(messageConsumer);

        context.set(this);

        // Default return stack
    }

    public void compile() {
        Queue<InputFile> files = new LinkedList<>(inputFiles.getInputFiles());
        Set<InputFile> processedFiles = new HashSet<>();
        List<AstModule> moduleList = new ArrayList<>();
        RequirementsProcessor requirementsProcessor = new RequirementsProcessor(messageConsumer, profile, inputFiles);

        // Process all input files including files discovered through require directive.
        try {
            while (!files.isEmpty()) {
                InputFile inputFile = files.remove();
                if (processedFiles.add(inputFile)) {
                    CommonTokenStream tokenStream = LexerParser.createTokenStream(messageConsumer, inputFile);
                    tokenStreams.put(inputFile, tokenStream);
                    if (targetPhase == CompilationPhase.LEXER) continue;

                    AstModuleContext parseTree = LexerParser.parseTree(messageConsumer, inputFile, tokenStream);
                    parseTrees.put(inputFile, parseTree);
                    if (targetPhase == CompilationPhase.PARSER) continue;

                    AstModule module = AstBuilder.build(this, inputFile, tokenStream, parseTree);
                    modules.put(inputFile, module);
                    moduleList.add(module);

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

        astProgram = new AstProgram(new InputPosition(inputFiles.getMainInputFile(), 1, 1), moduleList);
        if (error() || targetPhase.compareTo(CompilationPhase.AST_BUILDER) <= 0) return;

        if (profile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(new AstIndentedPrinter().print(astProgram));
            debug("");
        }

        DirectivePreprocessor.processDirectives(this, astProgram);

        instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);
        compileTimeEvaluator = new CompileTimeEvaluator(this);

        callGraph = CallGraphCreator.createCallGraph(this, astProgram);

        if (error() || targetPhase.compareTo(CompilationPhase.CALL_GRAPH) <= 0) return;

        rootAstContext = AstContext.createRootNode(profile);
        variables = new Variables(this);
        assembler = new CodeAssembler(this);

        new CodeGenerator(this).generateCode(astProgram);

        if (assembler.isInternalError() && !error()) {
            throw new MindcodeInternalError("Internal error encountered.");
        }
        if (error() || targetPhase.compareTo(CompilationPhase.COMPILER) <= 0) return;

//        // OPTIMIZE
//        final DebugPrinter debugPrinter = profile.getDebugLevel() > 0 && profile.optimizationsActive()
//                ? new DiffDebugPrinter(profile.getDebugLevel()) : new NullDebugPrinter();
//        OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, profile, messageConsumer);
//        optimizer.setDebugPrinter(debugPrinter);
//        GeneratorOutput output = new GeneratorOutput(callGraph, assembler.getInstructions(), rootAstContext);
//        List<LogicInstruction> result = optimizer.optimize(output);
//        debugPrinter.print(this::debug);
//
//
//        long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);
//
//        // Sort variables
//        LogicInstructionLabelResolver resolver = new LogicInstructionLabelResolver(instructionProcessor, profile);
//        List<LogicInstruction> instructions = resolver.sortVariables(optimized);
//
//        // Print unresolved code
//        if (profile.getFinalCodeOutput() != null) {
//            debug("\nFinal code before resolving virtual instructions:\n");
//            debug(LogicInstructionPrinter.toString(profile.getFinalCodeOutput(), instructionProcessor, instructions));
//        }
//
//        // Label resolving
//        List<LogicInstruction> result = resolver.resolveLabels(instructions);
//
//        // RUN if requested
//        // Timing output
//        final info.teksol.mindcode.compiler.MindcodeCompiler.RunResults runResults;
//        if (profile.isRun()) {
//            long runStart = System.nanoTime();
//            runResults = run(result);
//            long runTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStart);
//            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms, run in %,d ms.",
//                    parseTime, compileTime, optimizeTime, runTime);
//        } else {
//            runResults = new info.teksol.mindcode.compiler.MindcodeCompiler.RunResults(null, List.of(), null,0);
//            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.",
//                    parseTime, compileTime, optimizeTime);
//        }
//
//        String output = LogicInstructionPrinter.toString(instructionProcessor, result);
//
//        return new CompilerOutput<>(output, messages, runResults.exception, runResults.assertions(),
//                runResults.textBuffer(), runResults.steps());
    }

    private boolean error() {
        return errorsDetector.errorsDetected;
    }

    private static class ErrorsDetector implements MessageConsumer {
        private final MessageConsumer delegate;
        private boolean errorsDetected = false;

        public ErrorsDetector(MessageConsumer messageConsumer) {
            this.delegate = messageConsumer;
        }

        @Override
        public void accept(MindcodeMessage mindcodeMessage) {
            if (mindcodeMessage.isError()) {
                errorsDetected = true;
            }
            delegate.accept(mindcodeMessage);
        }
    }

    // Root method for obtaining compiler contexts
    // Allows finding all out-of-line usages of compiler context through call hierarchy.
    private static MindcodeCompiler getContext() {
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

    public AstContext getRootAstContext() {
        return Objects.requireNonNull(rootAstContext);
    }

    public List<LogicInstruction> getInstructions() {
        return Objects.requireNonNull(assembler).getInstructions();
    }

    public CallGraph getCallGraph() {
        return Objects.requireNonNull(callGraph);
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

    @Override
    public void setTopAstContext(AstContext topAstContext) {
        this.topAstContext = Objects.requireNonNull(topAstContext);
    }

    @Override
    public AstContext topAstContext() {
        return Objects.requireNonNull(topAstContext);
    }
}
