package info.teksol.mindcode.v3;

import info.teksol.generated.ast.AstIndentedPrinter;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.Requirement;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ModuleContext;
import info.teksol.mindcode.v3.compiler.ast.AstBuilder;
import info.teksol.mindcode.v3.compiler.ast.AstBuilderContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstModule;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstProgram;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraphCreator;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraphCreatorContext;
import info.teksol.mindcode.v3.compiler.directives.DirectiveProcessor;
import info.teksol.mindcode.v3.compiler.directives.DirectiveProcessorContext;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class MindcodeCompiler extends AbstractMessageEmitter implements AstBuilderContext, DirectiveProcessorContext,
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

    // Intermediate and final results
    private final Map<InputFile, CommonTokenStream> tokenStreams = new HashMap<>();
    private final Map<InputFile, ModuleContext> parseTrees = new HashMap<>();
    private final Map<InputFile, AstModule> modules = new HashMap<>();
    private final List<AstRequire> requirements = new ArrayList<>();
    private @Nullable AstProgram program;
    private @Nullable CallGraph callGraph;

    public MindcodeCompiler(CompilationPhase targetPhase, MessageConsumer messageConsumer,
            CompilerProfile profile, InputFiles inputFiles) {
        super(messageConsumer);
        this.targetPhase = targetPhase;
        this.profile = profile;
        this.inputFiles = inputFiles;
        context.set(this);
    }

    public void compile() {
        Queue<InputFile> files = new LinkedList<>(inputFiles.getInputFiles());
        Set<InputFile> processedFiles = new HashSet<>();
        List<AstModule> moduleList = new ArrayList<>();
        RequirementsProcessor requirementsProcessor = new RequirementsProcessor(messageConsumer, profile, inputFiles);

        // Process all input files including files discovered through require directive.
        while (!files.isEmpty()) {
            InputFile inputFile = files.remove();
            if (processedFiles.add(inputFile)) {
                CommonTokenStream tokenStream = LexerParser.createTokenStream(messageConsumer, inputFile);
                tokenStreams.put(inputFile, tokenStream);
                if (targetPhase == CompilationPhase.LEXER) continue;

                ModuleContext parseTree = LexerParser.parseTree(messageConsumer, inputFile, tokenStream);
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

        program = new AstProgram(new InputPosition(inputFiles.getMainInputFile(), 1, 1), moduleList);
        if (targetPhase.compareTo(CompilationPhase.AST_BUILDER) <= 0) return;

        DirectiveProcessor.processDirectives(this, program);
        if (profile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(new AstIndentedPrinter().print(program));
            debug("");
        }

        instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);

        callGraph = CallGraphCreator.createCallGraph(this, program);
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

    public ModuleContext getParseTree(InputFile inputFile) {
        return parseTrees.get(inputFile);
    }

    public AstModule getModule(InputFile inputFile) {
        return modules.get(inputFile);
    }

    public AstProgram getProgram() {
        return Objects.requireNonNull(program);
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
    public void addRequirement(AstRequire requirement) {
        requirements.add(requirement);
    }

    @Override
    public CallGraph callGraph() {
        return Objects.requireNonNull(callGraph);
    }
}
