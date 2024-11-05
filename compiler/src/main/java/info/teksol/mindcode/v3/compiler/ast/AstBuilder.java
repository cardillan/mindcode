package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ParserAbort;
import info.teksol.mindcode.ast.HeapAllocation;
import info.teksol.mindcode.ast.StackAllocation;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ProgramContext;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParserBaseVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AstBuilder extends MindcodeParserBaseVisitor<AstMindcodeNode> {
    private final Consumer<MindcodeMessage> messageConsumer;
    private final InputFile inputFile;
    private final Map<String, Integer> heapAllocations = new HashMap<>();
    private int temp;
    private HeapAllocation allocatedHeap;
    private StackAllocation allocatedStack;

    public static AstMindcodeNode generate(InputFile inputFile, Consumer<MindcodeMessage> messageConsumer,
            ProgramContext program) {
        final AstBuilder builder = new AstBuilder(messageConsumer, inputFile);
        return builder.visit(program);
    }

    public AstBuilder(Consumer<MindcodeMessage> messageConsumer, InputFile inputFile) {
        this.messageConsumer = messageConsumer;
        this.inputFile = inputFile;
    }

    @Override
    public AstMindcodeNode visit(ParseTree tree) {
        if (tree instanceof ParserRuleContext ctx && ctx.exception != null) {
            throw new ParserAbort();
        }
        return super.visit(tree);
    }

    @Override
    public AstMindcodeNode visitProgram(ProgramContext ctx) {
        return super.visitProgram(ctx);
    }
}
