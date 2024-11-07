package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ParserAbort;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ProgramContext;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParserBaseVisitor;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AstBuilder extends MindcodeParserBaseVisitor<AstMindcodeNode> {
    private final Consumer<MindcodeMessage> messageConsumer;
    private final InputFiles.InputFile inputFile;

    AstBuilder(Consumer<MindcodeMessage> messageConsumer, InputFiles.InputFile inputFile) {
        this.messageConsumer = messageConsumer;
        this.inputFile = inputFile;
    }

    private InputPosition pos(Token token) {
        return InputPosition.create(inputFile, token);
    }

    private InputPosition pos(TerminalNode node) {
        return InputPosition.create(inputFile, node.getSymbol());
    }

    @Override
    public AstMindcodeNode visit(ParseTree tree) {
        //System.out.println("Visiting: " + tree.getText());
        if (tree instanceof ParserRuleContext ctx && ctx.exception != null) {
            throw new ParserAbort();
        }
        return super.visit(tree);
    }

    //<editor-fold desc="Rule: program">
    @Override
    public AstMindcodeNode visitProgram(ProgramContext ctx) {
        if (ctx.expressionList() == null) {
            return new AstExpressionList(pos(ctx.EOF()), List.of());
        } else {
            List<AstMindcodeNode> expressions = ctx.expressionList().expression().stream().map(this::visit).toList();
            return new AstExpressionList(pos(ctx.start), expressions);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expressionList">
    @Override
    public AstMindcodeNode visitExpressionList(MindcodeParser.ExpressionListContext ctx) {
        return super.visitExpressionList(ctx);
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expression">
    @Override
    public AstMindcodeNode visitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx) {
        return new AstIdentifier(pos(ctx.Identifier()), ctx.Identifier().getText());
    }

    @Override
    public AstMindcodeNode visitExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx) {
        // Empty placeholders aren't supported in enhanced comment
        // The check will be done in code generator
        List<AstMindcodeNode> parts = ctx.children.stream().map(this::visit).filter(Objects::nonNull).toList();
        return new AstEnhancedComment(pos(ctx.getStart()), parts);
    }

    @Override
    public AstMindcodeNode visitExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx) {
        List<AstMindcodeNode> parts = ctx.children.stream().map(this::visit).filter(Objects::nonNull).toList();
        return new AstFormattable(pos(ctx.getStart()), parts);
    }

    @Override
    public AstMindcodeNode visitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx) {
        String text = ctx.StringLiteral().getText();
        return new AstLiteralString(pos(ctx.StringLiteral()), text.substring(1, text.length() - 1));
    }
    //</editor-fold>

    //<editor-fold desc="Rule: formattableContents">
    @Override
    public AstMindcodeNode visitFmtText(MindcodeParser.FmtTextContext ctx) {
        return new AstLiteralString(pos(ctx.Text()), ctx.Text().getText());
    }

    @Override
    public AstMindcodeNode visitFmtEscaped(MindcodeParser.FmtEscapedContext ctx) {
        String text = ctx.EscapeSequence().getText();
        char escaped = text.charAt(1);
        return escaped == '\\' || escaped == '$'
                ? new AstLiteralEscape(pos(ctx.EscapeSequence()), escaped + "")
                : new AstLiteralString(pos(ctx.EscapeSequence()), text);
    }

    @Override
    public AstMindcodeNode visitFmtInterpolation(MindcodeParser.FmtInterpolationContext ctx) {
        // Just evaluate the expression
        return visit(ctx.expression());
    }
    //</editor-fold>

    //<editor-fold desc="Rule: formattablePlaceholder">
    @Override
    public AstMindcodeNode visitFmtPlaceholderEmpty(MindcodeParser.FmtPlaceholderEmptyContext ctx) {
        return new AstFormattablePlaceholder(pos(ctx.getStart()));
    }

    @Override
    public AstMindcodeNode visitFmtPlaceholderVariable(MindcodeParser.FmtPlaceholderVariableContext ctx) {
        return ctx.Variable() != null
                ? new AstIdentifier(pos(ctx.Variable()), ctx.Variable().getText())
                : new AstFormattablePlaceholder(pos(ctx.getStart()));
    }
    //</editor-fold>
}
