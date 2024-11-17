package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ParserAbort;
import info.teksol.mindcode.v3.InputFile;
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
    private final InputFile inputFile;

    AstBuilder(Consumer<MindcodeMessage> messageConsumer, InputFile inputFile) {
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
    public AstMindcodeNode visitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx) {
        return new AstIdentifier(pos(ctx.IDENTIFIER()), ctx.IDENTIFIER().getText());
    }

    @Override
    public AstMindcodeNode visitExpBuiltInIdentifier(MindcodeParser.ExpBuiltInIdentifierContext ctx) {
        return new AstBuiltInIdentifier(pos(ctx.BUILTINIDENTIFIER()), ctx.BUILTINIDENTIFIER().getText());
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
    //</editor-fold>

    //<editor-fold desc="Rule: expression/literals">
    @Override
    public AstMindcodeNode visitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx) {
        String literal = ctx.STRING().getText();
        return new AstLiteralString(pos(ctx.STRING()), literal.substring(1, literal.length() - 1));
    }

    @Override
    public AstMindcodeNode visitExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx) {
        String literal = ctx.BINARY().getText();
        return new AstLiteralBinary(pos(ctx.BINARY()), literal);
    }

    @Override
    public AstMindcodeNode visitExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx) {
        String literal = ctx.HEXADECIMAL().getText();
        return new AstLiteralHexadecimal(pos(ctx.HEXADECIMAL()), literal);
    }

    @Override
    public AstMindcodeNode visitExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx) {
        String literal = ctx.DECIMAL().getText();
        return new AstLiteralDecimal(pos(ctx.DECIMAL()), literal);
    }

    @Override
    public AstMindcodeNode visitExpFLoatLiteral(MindcodeParser.ExpFLoatLiteralContext ctx) {
        String literal = ctx.FLOAT().getText();
        return new AstLiteralFloat(pos(ctx.FLOAT()), literal);
    }

    @Override
    public AstMindcodeNode visitExpNullLiteral(MindcodeParser.ExpNullLiteralContext ctx) {
        String literal = ctx.NULL().getText();
        return new AstLiteralNull(pos(ctx.NULL()), literal);
    }

    @Override
    public AstMindcodeNode visitExpBooleanLiteralTrue(MindcodeParser.ExpBooleanLiteralTrueContext ctx) {
        String literal = ctx.TRUE().getText();
        return new AstLiteralBoolean(pos(ctx.TRUE()), literal);
    }

    @Override
    public AstMindcodeNode visitExpBooleanLiteralFalse(MindcodeParser.ExpBooleanLiteralFalseContext ctx) {
        String literal = ctx.FALSE().getText();
        return new AstLiteralBoolean(pos(ctx.FALSE()), literal);
    }
    //</editor-fold>

    //<editor-fold desc="Rule: directives">
    @Override
    public AstMindcodeNode visitDirectiveSet(MindcodeParser.DirectiveSetContext ctx) {
        AstDirectiveValue option = new AstDirectiveValue(pos(ctx.option.DIRECTIVEVALUE()), ctx.option.getText());
        if (ctx.directiveValues() == null) {
            return new AstDirectiveSet(pos(ctx.HASHSET()), option, List.of());
        } else {
            AstMindcodeNode list = visit(ctx.directiveValues());
            if (list instanceof AstDirectiveValueList valueList) {
                return new AstDirectiveSet(pos(ctx.HASHSET()), option, valueList.getValues());
            } else {
                throw new MindcodeInternalError("Unexpected result of value list evaluation: " + list);
            }
        }
    }

    @Override
    public AstDirectiveValueList visitDirectiveValueList(MindcodeParser.DirectiveValueListContext ctx) {
        List<AstDirectiveValue> values = ctx.directiveValue().stream().map(this::visitDirectiveValue).toList();
        return new AstDirectiveValueList(pos(ctx.getStart()), values);
    }

    @Override
    public AstDirectiveValue visitDirectiveValue(MindcodeParser.DirectiveValueContext ctx) {
        return new AstDirectiveValue(pos(ctx.DIRECTIVEVALUE()), ctx.DIRECTIVEVALUE().getText());
    }
    //</editor-fold>

    //<editor-fold desc="Rule: formattableContents">
    @Override
    public AstMindcodeNode visitFmtText(MindcodeParser.FmtTextContext ctx) {
        return new AstLiteralString(pos(ctx.TEXT()), ctx.TEXT().getText());
    }

    @Override
    public AstMindcodeNode visitFmtEscaped(MindcodeParser.FmtEscapedContext ctx) {
        String text = ctx.ESCAPESEQUENCE().getText();
        char escaped = text.charAt(1);
        return escaped == '\\' || escaped == '$'
                ? new AstLiteralEscape(pos(ctx.ESCAPESEQUENCE()), escaped + "")
                : new AstLiteralString(pos(ctx.ESCAPESEQUENCE()), text);
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
        return ctx.VARIABLE() != null
                ? new AstIdentifier(pos(ctx.VARIABLE()), ctx.VARIABLE().getText())
                : new AstFormattablePlaceholder(pos(ctx.getStart()));
    }
    //</editor-fold>
}
