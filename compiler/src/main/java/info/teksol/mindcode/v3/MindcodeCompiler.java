package info.teksol.mindcode.v3;

import info.teksol.mindcode.MindcodeErrorListener;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeLexer;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ProgramContext;
import info.teksol.mindcode.v3.compiler.ast.AstBuilder;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MindcodeCompiler extends AbstractMessageEmitter {
    private final CompilationPhase targetPhase;

    private Map<InputFile, CommonTokenStream> tokenStreams = new HashMap<>();
    private Map<InputFile, ProgramContext> parseTrees = new HashMap<>();
    private Map<InputFile, AstMindcodeNode> syntaxTrees = new HashMap<>();

    public MindcodeCompiler(CompilationPhase targetPhase, Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        this.targetPhase = targetPhase;
    }

    public CommonTokenStream getTokenStream(InputFile inputFile) {
        return tokenStreams.get(inputFile);
    }

    public ProgramContext getParseTree(InputFile inputFile) {
        return parseTrees.get(inputFile);
    }

    public AstMindcodeNode getSyntaxTree(InputFile inputFile) {
        return syntaxTrees.get(inputFile);
    }

    public void compile(InputFile inputFile) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        // We're adding a newline at the end, because it makes some grammar definitions way easier
        MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.getCode() + "\n"));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStreams.put(inputFile, tokenStream);
        if (targetPhase == CompilationPhase.LEXER) return;

        MindcodeParser parser = new MindcodeParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        ProgramContext parseTree = parser.program();
        parseTrees.put(inputFile, parseTree);
        messageConsumer.accept(ToolMessage.info("%s: number of reported ambiguities: %d",
                inputFile.getDistinctTitle(), errorListener.getAmbiguities()));
        if (targetPhase == CompilationPhase.PARSER) return;

        AstBuilder builder = new AstBuilder(messageConsumer, inputFile, tokenStream);
        AstMindcodeNode syntaxTree = builder.visit(parseTree);
        syntaxTrees.put(inputFile, syntaxTree);
        if (targetPhase == CompilationPhase.AST_BUILDER) return;


    }


    public enum CompilationPhase {
        LEXER,
        PARSER,
        AST_BUILDER,
        COMPILER,
        ;

        public boolean includes(CompilationPhase phase) {
            return this.ordinal() >= phase.ordinal();
        }
    }
}
