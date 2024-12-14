package info.teksol.mindcode.v3;

import info.teksol.generated.ast.AstIndentedPrinter;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.ast.Requirement;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeLexer;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ModuleContext;
import info.teksol.mindcode.v3.compiler.ast.AstBuilder;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstModule;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstProgram;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraphCreator;
import info.teksol.mindcode.v3.compiler.directives.DirectiveProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class MindcodeCompiler extends AbstractMessageEmitter {
    // Global cache
    private static final Map<String, InputFile> LIBRARY_SOURCES = new ConcurrentHashMap<>();
    private static final Map<InputFile, ParsedLibrary> LIBRARY_PARSES = new ConcurrentHashMap<>();

    private final CompilationPhase targetPhase;
    private final CompilerProfile profile;
    private final InputFiles inputFiles;
    private final CompilerContext context;

    private final Map<InputFile, CommonTokenStream> tokenStreams = new HashMap<>();
    private final Map<InputFile, ModuleContext> parseTrees = new HashMap<>();
    private final Map<InputFile, AstModule> modules = new HashMap<>();
    private @Nullable AstProgram program;
    private @Nullable CallGraph callGraph;
    private @Nullable InstructionProcessor instructionProcessor;

    public MindcodeCompiler(CompilationPhase targetPhase, MessageConsumer messageConsumer,
            CompilerProfile profile, InputFiles inputFiles) {
        super(messageConsumer);
        this.targetPhase = targetPhase;
        this.profile = profile;
        this.inputFiles = inputFiles;
        this.context = CompilerContext.initialize(messageConsumer, profile);
    }

    public CommonTokenStream getTokenStream(InputFile inputFile) {
        return tokenStreams.get(inputFile);
    }

    public ModuleContext getParseTree(InputFile inputFile) {
        return parseTrees.get(inputFile);
    }

    public AstModule getModule(InputFile inputFile) {
        return modules.get(inputFile);
    }

    public @Nullable AstProgram getProgram() {
        return program;
    }

    public CallGraph getCallGraph() {
        return Objects.requireNonNull(callGraph);
    }

    public void compile() {
        Queue<InputFile> files = new LinkedList<>(inputFiles.getInputFiles());
        Set<InputFile> processedFiles = new HashSet<>();
        List<AstModule> moduleList = new ArrayList<>();

        // Process all input files including files discovered through require directive.
        while (!files.isEmpty()) {
            InputFile inputFile = files.remove();
            if (processedFiles.add(inputFile)) {

                CommonTokenStream tokenStream = createTokenStream(inputFile);
                if (targetPhase == CompilationPhase.LEXER) continue;

                ModuleContext parseTree = parseTree(inputFile, tokenStream);
                if (targetPhase == CompilationPhase.PARSER) continue;

                AstModule module = new AstBuilder(context, inputFile, tokenStream).build(parseTree);
                modules.put(inputFile, module);
                moduleList.add(module);

                context.getRequirements().stream()
                        .map(r -> processRequirement(inputFiles.getBasePath(), r))
                        .filter(Objects::nonNull)
                        .forEach(files::add);
            }
        }

        program = new AstProgram(new InputPosition(inputFiles.getMainInputFile(), 1, 1), moduleList);
        if (targetPhase.compareTo(CompilationPhase.AST_BUILDER) <= 0) return;

        DirectiveProcessor.processDirectives(messageConsumer, profile, program);
        if (profile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(new AstIndentedPrinter().print(program));
            debug("");
        }

        instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);

        callGraph = CallGraphCreator.createCallGraph(messageConsumer, instructionProcessor, program);

        System.out.println("Compiling...");
    }

    private CommonTokenStream createTokenStream(InputFile inputFile) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        // We're adding a newline at the end, because it makes some grammar definitions way easier
        MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.getCode() + "\n"));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStreams.put(inputFile, tokenStream);
        return tokenStream;
    }

    private ModuleContext parseTree(InputFile inputFile, CommonTokenStream tokenStream) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        MindcodeParser parser = new MindcodeParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setErrorHandler(new MindcodeErrorStrategy(tokenStream));
        ModuleContext parseTree = parser.module();
        parseTrees.put(inputFile, parseTree);
        messageConsumer.accept(ToolMessage.info("%s: number of reported ambiguities: %d",
                inputFile.getDistinctTitle(), errorListener.getAmbiguities()));
        return parseTree;
    }

    private @Nullable InputFile processRequirement(Path relativePath, AstRequire requirement) {
        if (requirement.isLibrary()) {
            return loadLibrary(requirement);
        } else if (profile.isWebApplication()) {
            error(requirement, "Loading code from external file not supported in web application.");
            return null;
        } else {
            return loadFile(requirement, relativePath.resolve(requirement.getName()));
        }
    }

    private @Nullable InputFile loadFile(AstRequire requirement, Path path) {
        try {
            String code = Files.readString(path, StandardCharsets.UTF_8);
            return inputFiles.registerFile(path, code);
        } catch (IOException e) {
            error(requirement, "Error reading file '%s'.", path);
            return null;
        }
    }

    private @Nullable InputFile loadLibrary(AstRequire requirement) {
        return LIBRARY_SOURCES.computeIfAbsent(requirement.getName(), s -> loadLibraryFromResource(requirement));
    }

    private @Nullable InputFile loadLibraryFromResource(AstRequire requirement) {
        String libraryName = requirement.getName();
        try {
            InputFile library = loadSystemLibrary(libraryName);
            if (library == null) {
                error(requirement, "Unknown system library '%s'.", libraryName);
            }
            return library;
        } catch (IOException e) {
            error(requirement, "Error reading system library file '%s'.", libraryName);
            throw new MindcodeInternalError(e, "Error reading system library file '%s'.", libraryName);
        }
    }

    private @Nullable InputFile loadSystemLibrary(String libraryName) throws IOException {
        try (InputStream resource = MindcodeCompiler.class.getResourceAsStream("/library/" + libraryName + ".mnd")) {
            if (resource == null) {
                return null;
            }
            try (final InputStreamReader reader = new InputStreamReader(resource)) {
                final StringWriter out = new StringWriter();
                reader.transferTo(out);
                return inputFiles.registerLibraryFile(Path.of(libraryName), out.toString());
            }
        }
    }

    public enum CompilationPhase {
        LEXER,
        PARSER,
        AST_BUILDER,
        COMPILER,
        OPTIMIZER,
        ALL
        ;

        public boolean includes(CompilationPhase phase) {
            return this.ordinal() >= phase.ordinal();
        }
    }

    private record ParsedLibrary(AstMindcodeNode program, List<Requirement> requirements) {
    }
}
