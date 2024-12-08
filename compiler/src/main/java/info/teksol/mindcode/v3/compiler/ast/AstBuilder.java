package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ParserAbort;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.v3.DataType;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeLexer;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.ProgramContext;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParserBaseVisitor;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.antlr.v4.runtime.CommonTokenStream;
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
    private final CommonTokenStream tokenStream;

    public AstBuilder(Consumer<MindcodeMessage> messageConsumer, InputFile inputFile, CommonTokenStream tokenStream) {
        this.messageConsumer = messageConsumer;
        this.inputFile = inputFile;
        this.tokenStream = tokenStream;
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
        return ctx.statementList() == null
                ? new AstStatementList(pos(ctx), List.of())
                : visit(ctx.statementList());
    }
    //</editor-fold>

    //<editor-fold desc="Rule: structures">
    @Override
    public AstStatementList visitStatementList(MindcodeParser.StatementListContext ctx) {
        return new AstStatementList(pos(ctx), ctx.expression().stream().map(this::visit).toList());
    }

    @Override
    public AstMindcodeNode visitExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx) {
        return ctx.exp == null
                ? new AstCodeBlock(pos(ctx), List.of())
                : new AstCodeBlock(visitStatementList(ctx.exp));
    }

    @Override
    public AstMindcodeNode visitExpCaseExpression(MindcodeParser.ExpCaseExpressionContext ctx) {
        return new AstCaseExpression(pos(ctx),
                visit(ctx.exp),
                processCaseAlternatives(ctx.alternatives),
                visit(ctx.elseBranch));
    }

    private List<AstCaseAlternative> processCaseAlternatives(MindcodeParser.CaseAlternativesContext ctx) {
        return ctx.caseAlternative() != null
                ? ctx.caseAlternative().stream().map(this::visitCaseAlternative).toList()
                : List.of();
    }

    @Override
    public AstCaseAlternative visitCaseAlternative(MindcodeParser.CaseAlternativeContext ctx) {
        return new AstCaseAlternative(pos(ctx),
                ctx.values != null
                        ? ctx.values.whenValue().stream().map(this::visit).toList()
                        : List.of(),
                visit(ctx.body));
    }
    //</editor-fold>

    //<editor-fold desc="Rule: declarations">
    @Override
    public AstMindcodeNode visitExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx) {
        AstIdentifier library = createIdentifier(ctx.library);
        return new AstRequireLibrary(pos(ctx), library);
    }

    @Override
    public AstMindcodeNode visitExpRequireFile(MindcodeParser.ExpRequireFileContext ctx) {
        return new AstRequireFile(pos(ctx), createLiteralString(ctx.STRING()));
    }

    @Override
    public AstMindcodeNode visitExpParameter(MindcodeParser.ExpParameterContext ctx) {
        return new AstParameter(pos(ctx),
                findDocComment(ctx.getStart()),
                createIdentifier(ctx.name),
                visit(ctx.value));
    }

    @Override
    public AstMindcodeNode visitExpConstant(MindcodeParser.ExpConstantContext ctx) {
        return new AstConstant(pos(ctx),
                findDocComment(ctx.getStart()),
                createIdentifier(ctx.name),
                visit(ctx.value));
    }

    @Override
    public AstMindcodeNode visitExpAllocations(MindcodeParser.ExpAllocationsContext ctx) {
        List<AstMindcodeNode> allocations = ctx.allocations().allocation().stream().map(this::visit).toList();
        return allocations.size() > 1
                ? new AstStatementList(pos(ctx), allocations)
                : allocations.getFirst();
    }

    @Override
    public AstMindcodeNode visitStrHeapAllocation(MindcodeParser.StrHeapAllocationContext ctx) {
        return new AstAllocation(pos(ctx),
                AstAllocation.AllocationType.HEAP,
                createIdentifier(ctx.id),
                ctx.range != null ? visitRangeExpression(ctx.rangeExpression()) : null);
    }

    @Override
    public AstMindcodeNode visitStrStackAllocation(MindcodeParser.StrStackAllocationContext ctx) {
        return new AstAllocation(pos(ctx),
                AstAllocation.AllocationType.STACK,
                createIdentifier(ctx.id),
                ctx.range != null ? visitRangeExpression(ctx.rangeExpression()) : null);
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expressions">
    @Override
    public AstMindcodeNode visitExpBuiltInIdentifier(MindcodeParser.ExpBuiltInIdentifierContext ctx) {
        return new AstBuiltInIdentifier(pos(ctx), ctx.builtin.getText());
    }

    @Override
    public AstMindcodeNode visitExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx) {
        // Empty placeholders aren't supported in enhanced comment
        // The check will be done in code generator
        List<AstMindcodeNode> parts = ctx.children.stream().map(this::visit).filter(Objects::nonNull).toList();
        return new AstEnhancedComment(pos(ctx), parts);
    }

    @Override
    public AstMindcodeNode visitExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx) {
        List<AstMindcodeNode> parts = ctx.children.stream().map(this::visit).filter(Objects::nonNull).toList();
        return new AstFormattable(pos(ctx), parts);
    }

    public AstMindcodeNode visitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx) {
        return createIdentifier(ctx.id);
    }

    @Override
    public AstMindcodeNode visitExpIdentifierExt(MindcodeParser.ExpIdentifierExtContext ctx) {
        return createIdentifier(ctx.id);
    }

    @Override
    public AstMindcodeNode visitExpPropertyAccess(MindcodeParser.ExpPropertyAccessContext ctx) {
        return new AstMemberAccess(pos(ctx),
                visit(ctx.object),
                new AstBuiltInIdentifier(pos(ctx.property), ctx.property.getText()));
    }

    @Override
    public AstMindcodeNode visitExpMemeberAccess(MindcodeParser.ExpMemeberAccessContext ctx) {
        AstMindcodeNode node = visit(ctx.object);
        AstIdentifier right = createIdentifier(ctx.member);
        return switch (node) {
            case AstIdentifier left             -> new AstQualifiedIdentifier(left, right);
            case AstQualifiedIdentifier left    -> new AstQualifiedIdentifier(left, right);
            case null                           -> throw new MindcodeInternalError("Unexpected null");
            default                             -> new AstMemberAccess(pos(ctx), node, right);
        };
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expressions/arrays">
    @Override
    public AstMindcodeNode visitExpArrayAccess(MindcodeParser.ExpArrayAccessContext ctx) {
        return new AstArrayAccess(pos(ctx),
                createIdentifier(ctx.array),
                visit(ctx.index));
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expressions/assignments">
    @Override
    public AstMindcodeNode visitExpCompoundAssignment(MindcodeParser.ExpCompoundAssignmentContext ctx) {
        int type = switch (ctx.operation.getType()) {
            case MindcodeLexer.ASSIGN_BITWISE_AND  -> MindcodeLexer.BITWISE_AND;
            case MindcodeLexer.ASSIGN_BITWISE_OR   -> MindcodeLexer.BITWISE_OR;
            case MindcodeLexer.ASSIGN_BITWISE_XOR  -> MindcodeLexer.BITWISE_XOR;
            case MindcodeLexer.ASSIGN_BOOLEAN_AND  -> MindcodeLexer.BOOLEAN_AND;
            case MindcodeLexer.ASSIGN_BOOLEAN_OR   -> MindcodeLexer.BOOLEAN_OR;
            case MindcodeLexer.ASSIGN_DIV          -> MindcodeLexer.DIV;
            case MindcodeLexer.ASSIGN_IDIV         -> MindcodeLexer.IDIV;
            case MindcodeLexer.ASSIGN_MINUS        -> MindcodeLexer.MINUS;
            case MindcodeLexer.ASSIGN_MOD          -> MindcodeLexer.MOD;
            case MindcodeLexer.ASSIGN_MUL          -> MindcodeLexer.MUL;
            case MindcodeLexer.ASSIGN_PLUS         -> MindcodeLexer.PLUS;
            case MindcodeLexer.ASSIGN_POW          -> MindcodeLexer.POW;
            case MindcodeLexer.ASSIGN_SHIFT_LEFT   -> MindcodeLexer.SHIFT_LEFT;
            case MindcodeLexer.ASSIGN_SHIFT_RIGHT  -> MindcodeLexer.SHIFT_RIGHT;
            default -> throw new MindcodeInternalError("Cannot map assignment token %s to operation token.", ctx.operation.getText());
        };

        return new AstAssignmentCompound(pos(ctx),
                Operation.fromToken(type),
                visit(ctx.target),
                visit(ctx.value));
    }

    @Override
    public AstMindcodeNode visitExpAssignment(MindcodeParser.ExpAssignmentContext ctx) {
        return new AstAssignmentSimple(pos(ctx),
                visit(ctx.target),
                visit(ctx.value));
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expressions/literals">
    @Override
    public AstMindcodeNode visitExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx) {
        String literal = ctx.BINARY().getText();
        return new AstLiteralBinary(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpBooleanLiteralTrue(MindcodeParser.ExpBooleanLiteralTrueContext ctx) {
        String literal = ctx.TRUE().getText();
        return new AstLiteralBoolean(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpBooleanLiteralFalse(MindcodeParser.ExpBooleanLiteralFalseContext ctx) {
        String literal = ctx.FALSE().getText();
        return new AstLiteralBoolean(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx) {
        String literal = ctx.DECIMAL().getText();
        return new AstLiteralDecimal(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpFloatLiteral(MindcodeParser.ExpFloatLiteralContext ctx) {
        String literal = ctx.FLOAT().getText();
        return new AstLiteralFloat(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx) {
        String literal = ctx.HEXADECIMAL().getText();
        return new AstLiteralHexadecimal(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpNullLiteral(MindcodeParser.ExpNullLiteralContext ctx) {
        String literal = ctx.NULL().getText();
        return new AstLiteralNull(pos(ctx), literal);
    }

    @Override
    public AstMindcodeNode visitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx) {
        return createLiteralString(ctx.STRING());
    }
    //</editor-fold>

    //<editor-fold desc="Rule: expressions/operators">
    @Override
    public AstMindcodeNode visitExpAddition(MindcodeParser.ExpAdditionContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpBitShift(MindcodeParser.ExpBitShiftContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpBitwiseAnd(MindcodeParser.ExpBitwiseAndContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpBitwiseOr(MindcodeParser.ExpBitwiseOrContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpEqualityRelation(MindcodeParser.ExpEqualityRelationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpExponentiation(MindcodeParser.ExpExponentiationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpInequalityRelation(MindcodeParser.ExpInequalityRelationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpLogicalAnd(MindcodeParser.ExpLogicalAndContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpLogicalOr(MindcodeParser.ExpLogicalOrContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpMultiplication(MindcodeParser.ExpMultiplicationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.left),
                visit(ctx.right));
    }

    @Override
    public AstMindcodeNode visitExpPostfix(MindcodeParser.ExpPostfixContext ctx) {
        AstOperatorIncDec.Operation operation = switch (ctx.postfix.getType()) {
            case MindcodeLexer.INCREMENT -> AstOperatorIncDec.Operation.INCREMENT;
            case MindcodeLexer.DECREMENT -> AstOperatorIncDec.Operation.DECREMENT;
            default -> throw new MindcodeInternalError("Unexpected postfix operator " + ctx.postfix.getText());
        };
        return new AstOperatorIncDec(pos(ctx.exp.getStart()),
                AstOperatorIncDec.Type.POSTFIX,
                operation,
                visit(ctx.exp));
    }

    @Override
    public AstMindcodeNode visitExpPrefix(MindcodeParser.ExpPrefixContext ctx) {
        AstOperatorIncDec.Operation operation = switch (ctx.prefix.getType()) {
            case MindcodeLexer.INCREMENT -> AstOperatorIncDec.Operation.INCREMENT;
            case MindcodeLexer.DECREMENT -> AstOperatorIncDec.Operation.DECREMENT;
            default -> throw new MindcodeInternalError("Unexpected postfix operator " + ctx.prefix.getText());
        };
        return new AstOperatorIncDec(pos(ctx.exp.getStart()),
                AstOperatorIncDec.Type.PREFIX,
                operation,
                visit(ctx.exp));
    }

    @Override
    public AstRange visitRangeExpression(MindcodeParser.RangeExpressionContext ctx) {
        return new AstRange(pos(ctx),
                visit(ctx.firstValue),
                visit(ctx.lastValue),
                ctx.operator.getType() == MindcodeLexer.DOT3);
    }

    @Override
    public AstMindcodeNode visitExpTernary(MindcodeParser.ExpTernaryContext ctx) {
        return new AstOperatorTernary(pos(ctx.condition.getStart()),
                visit(ctx.condition),
                visit(ctx.trueBranch),
                visit(ctx.falseBranch));
    }

    @Override
    public AstMindcodeNode visitExpUnary(MindcodeParser.ExpUnaryContext ctx) {
        return new AstOperatorUnary(pos(ctx.op),
                operation(ctx.op),
                visit(ctx.exp));
    }
    //</editor-fold>

    //<editor-fold desc="Rule: directives">
    @Override
    public AstMindcodeNode visitDirectiveSet(MindcodeParser.DirectiveSetContext ctx) {
        AstDirectiveValue option = new AstDirectiveValue(pos(ctx.option), ctx.option.getText());
        if (ctx.directiveValues() == null) {
            return new AstDirectiveSet(pos(ctx), option, List.of());
        } else {
            AstMindcodeNode list = visit(ctx.directiveValues());
            if (list instanceof AstDirectiveValueList valueList) {
                return new AstDirectiveSet(pos(ctx), option, valueList.getValues());
            } else {
                throw new MindcodeInternalError("Unexpected result of value list evaluation: " + list);
            }
        }
    }

    @Override
    public AstDirectiveValue visitDirectiveValue(MindcodeParser.DirectiveValueContext ctx) {
        return new AstDirectiveValue(pos(ctx), ctx.DIRECTIVEVALUE().getText());
    }

    @Override
    public AstDirectiveValueList visitDirectiveValueList(MindcodeParser.DirectiveValueListContext ctx) {
        List<AstDirectiveValue> values = ctx.directiveValue().stream().map(this::visitDirectiveValue).toList();
        return new AstDirectiveValueList(pos(ctx), values);
    }
    //</editor-fold>

    //<editor-fold desc="Rule: formattableContents">
    @Override
    public AstMindcodeNode visitFmtText(MindcodeParser.FmtTextContext ctx) {
        // Can't use createLiteralString here
        // We're parsing inside a formattable string and there aren't double quotes around the token
        return new AstLiteralString(pos(ctx.TEXT()), ctx.TEXT().getText());
    }

    @Override
    public AstMindcodeNode visitFmtEscaped(MindcodeParser.FmtEscapedContext ctx) {
        String text = ctx.ESCAPESEQUENCE().getText();
        char escaped = text.charAt(1);
        return escaped == '\\' || escaped == '$'
                ? new AstLiteralEscape(pos(ctx), escaped + "")
                : new AstLiteralString(pos(ctx), text);
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
        return new AstFormattablePlaceholder(pos(ctx));
    }

    @Override
    public AstMindcodeNode visitFmtPlaceholderVariable(MindcodeParser.FmtPlaceholderVariableContext ctx) {
        return ctx.VARIABLE() != null
                ? createIdentifier(ctx.id)
                : new AstFormattablePlaceholder(pos(ctx));
    }
    //</editor-fold>

    //<editor-fold desc="Rule: function calls">
    @Override
    public AstFunctionArgument visitArgument(MindcodeParser.ArgumentContext ctx) {
        return new AstFunctionArgument(pos(ctx),
                visit(ctx.arg),
                ctx.modifier_in != null,
                ctx.modifier_out != null);
    }

    @Override
    public AstFunctionArgument visitOptionalArgument(MindcodeParser.OptionalArgumentContext ctx) {
        return ctx.argument() != null ? visitArgument(ctx.argument()) : new AstFunctionArgument(pos(ctx));
    }

    public List<AstFunctionArgument> processArgumentList(MindcodeParser.ArgumentListContext ctx) {
        if (ctx.argument() != null) {
            return List.of(visitArgument(ctx.argument()));
        } else if (ctx.optionalArgument() != null) {
            return ctx.optionalArgument().stream().map(this::visitOptionalArgument).toList();
        } else {
            return List.of();
        }
    }

    @Override
    public AstMindcodeNode visitExpCallEnd(MindcodeParser.ExpCallEndContext ctx) {
        return new AstFunctionCall(pos(ctx), null,
                createIdentifier(ctx.END().getSymbol()),
                List.of());
    }

    @Override
    public AstMindcodeNode visitExpCallFunction(MindcodeParser.ExpCallFunctionContext ctx) {
        return new AstFunctionCall(pos(ctx),
                null,
                createIdentifier(ctx.function),
                processArgumentList(ctx.argumentList()));
    }

    @Override
    public AstMindcodeNode visitExpCallMethod(MindcodeParser.ExpCallMethodContext ctx) {
        return new AstFunctionCall(pos(ctx),
                visit(ctx.object),
                createIdentifier(ctx.function),
                processArgumentList(ctx.argumentList()));
    }

    //</editor-fold>

    //<editor-fold desc="Rule: function declarations">
    @Override
    public AstMindcodeNode visitExpDeclareFunction(MindcodeParser.ExpDeclareFunctionContext ctx) {
        DataType dataType = switch (ctx.type.getType()) {
            case MindcodeLexer.VOID -> DataType.VOID;
            case MindcodeLexer.DEF  -> DataType.VAR;
            default -> throw new MindcodeInternalError("Unsupported type: " + ctx.type.getText());
        };

        return new AstFunctionDeclaration(pos(ctx),
                findDocComment(ctx.getStart()),
                createIdentifier(ctx.name),
                dataType,
                processParameterList(ctx.parameterList()),
                ctx.body != null ? visit(ctx.body) : new AstStatementList(pos(ctx), List.of()),
                ctx.INLINE() != null,
                ctx.NOINLINE() != null);
    }

    private List<AstFunctionParameter> processParameterList(MindcodeParser.ParameterListContext ctx) {
        return ctx.parameter() != null ? ctx.parameter().stream().map(this::visitParameter).toList() : List.of();
    }

    @Override
    public AstFunctionParameter visitParameter(MindcodeParser.ParameterContext ctx) {
        return new AstFunctionParameter(pos(ctx),
                createIdentifier(ctx.name),
                ctx.modifier_in != null,
                ctx.modifier_out != null,
                ctx.varargs != null);
    }
    //</editor-fold>

    //<editor-fold desc="Helper functions">
    private AstLiteralString createLiteralString(TerminalNode node) {
        String literal = node.getText();
        return new AstLiteralString(pos(node), literal.substring(1, literal.length() - 1));
    }

    private AstIdentifier createIdentifier(Token token) {
        return new AstIdentifier(pos(token), token.getText(), token.getType() == MindcodeLexer.EXTIDENTIFIER);
    }

    private AstDocComment findDocComment(Token token) {
        int tokenIndex = token.getTokenIndex();
        List<Token> docTokens = Objects.requireNonNullElse(
                tokenStream.getHiddenTokensToLeft(tokenIndex, Token.HIDDEN_CHANNEL), List.of());

        // Note: the reduce trick to get the last item on the stream is not very effective, but we don't expect
        // the token list to be long. The length should be at most 1 in practically all cases.
        return docTokens.stream()
                .reduce((first, second) -> second)
                .filter(t -> t.getType() == MindcodeLexer.DOC_COMMENT)
                .map(docToken -> new AstDocComment(pos(docToken), docToken.getText()))
                .orElse(null);
    }

    private Operation operation(Token token) {
        return Operation.fromToken(token.getType());
    }

    private InputPosition pos(ParserRuleContext ctx) {
        return InputPosition.create(inputFile, ctx.getStart());
    }

    private InputPosition pos(Token token) {
        return InputPosition.create(inputFile, token);
    }

    private InputPosition pos(TerminalNode node) {
        return InputPosition.create(inputFile, node.getSymbol());
    }
}
