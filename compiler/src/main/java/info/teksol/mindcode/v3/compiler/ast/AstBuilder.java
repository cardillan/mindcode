package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.ParserAbort;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.v3.DataType;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeLexer;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.*;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParserBaseVisitor;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class AstBuilder extends MindcodeParserBaseVisitor<AstMindcodeNode> {
    private final AstBuilderContext context;
    private final InputFile inputFile;
    private final CommonTokenStream tokenStream;

    private AstBuilder(AstBuilderContext context, InputFile inputFile, CommonTokenStream tokenStream) {
        this.context = context;
        this.inputFile = inputFile;
        this.tokenStream = tokenStream;
    }

    public static AstModule build(AstBuilderContext context, InputFile inputFile, CommonTokenStream tokenStream,
            ParseTree tree) {
        AstBuilder astBuilder = new AstBuilder(context, inputFile, tokenStream);
        return (AstModule) astBuilder.visitNonNull(tree);
    }

    @Override
    public @Nullable AstMindcodeNode visit(ParseTree tree) {
        if (tree instanceof ParserRuleContext ctx && ctx.exception != null) {
            throw new ParserAbort();
        }
        return super.visit(tree);
    }

    //<editor-fold desc="Helper functions">
    private @Nullable AstMindcodeNode visitNullable(ParseTree tree) {
        return visit(tree);
    }

    private AstMindcodeNode visitNonNull(ParseTree tree) {
        return Objects.requireNonNull(visitNullable(tree));
    }

    private @Nullable AstExpression visitNullableExpression(ParseTree tree) {
        AstMindcodeNode result = visitNullable(tree);
        if (result == null) {
            return null;
        } else if (result instanceof AstExpression expression) {
            return expression;
        } else {
            context.messageConsumer().addMessage(CompilerMessage.error(result.inputPosition(), "Expression is required."));
            return new AstLiteralNull(result.inputPosition(), "null");
        }
    }

    private AstExpression visitExpression(ParseTree tree) {
        return Objects.requireNonNull(visitNullableExpression(tree));
    }

    public @Nullable AstRange visitRangeIfNonNull(@Nullable RangeExpressionContext ctx) {
        return ctx == null ? null : visitRangeExpression(ctx);
    }

    public @Nullable AstExpression visitExpressionIfNonNull(@Nullable ParseTree node) {
        return node == null ? null : visitNullableExpression(node);
    }

    private AstLiteralString createLiteralString(TerminalNode node) {
        String literal = node.getText();
        return new AstLiteralString(pos(node), literal.substring(1, literal.length() - 1));
    }

    private AstIdentifier createIdentifier(Token token) {
        return new AstIdentifier(pos(token), token.getText(), token.getType() == MindcodeLexer.EXTIDENTIFIER);
    }

    private @Nullable AstIdentifier createIdentifierOrNull(@Nullable Token token) {
        return token != null
                ? new AstIdentifier(pos(token), token.getText(), token.getType() == MindcodeLexer.EXTIDENTIFIER)
                : null;
    }

    private @Nullable AstDocComment findDocComment(Token token) {
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
    //</editor-fold>

    //<editor-fold desc="Rules: basics">
    @Override
    public AstMindcodeNode visitModule(MindcodeParser.ModuleContext ctx) {
        return new AstModule(pos(ctx), processBody(ctx.statementList()));
    }

    private List<AstExpression> processList(@Nullable ExpressionListContext ctx) {
        return ctx != null ? ctx.expression().stream().map(this::visitExpression).toList() : List.of();
    }

    private List<AstMindcodeNode> processBody(@Nullable StatementListContext ctx) {
        return ctx != null ? ctx.statement().stream().map(this::visitNonNull).toList() : List.of();
    }

    @Override
    public AstStatementList visitStatementList(StatementListContext ctx) {
        return new AstStatementList(pos(ctx), processBody(ctx));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: declarations">
    @Override
    public AstRequireLibrary visitExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx) {
        AstRequireLibrary requirement = new AstRequireLibrary(pos(ctx), createIdentifier(ctx.library));
        context.addRequirement(requirement);
        return requirement;
    }

    @Override
    public AstRequireFile visitExpRequireFile(MindcodeParser.ExpRequireFileContext ctx) {
        AstRequireFile requirement = new AstRequireFile(pos(ctx), createLiteralString(ctx.STRING()));
        context.addRequirement(requirement);
        return requirement;
    }

    @Override
    public AstParameter visitExpParameter(MindcodeParser.ExpParameterContext ctx) {
        return new AstParameter(pos(ctx),
                findDocComment(ctx.getStart()),
                createIdentifier(ctx.name),
                visitExpression(ctx.value));
    }

    @Override
    public AstConstant visitExpConstant(MindcodeParser.ExpConstantContext ctx) {
        return new AstConstant(pos(ctx),
                findDocComment(ctx.getStart()),
                createIdentifier(ctx.name),
                visitExpression(ctx.value));
    }

    @Override
    public AstMindcodeNode visitExpAllocations(MindcodeParser.ExpAllocationsContext ctx) {
        List<AstAllocation> allocations = ctx.allocations().allocation().stream()
                .map(this::visitNullable).map(AstAllocation.class::cast).toList();
        return new AstAllocations(pos(ctx), allocations);
    }

    @Override
    public AstAllocation visitStmtHeapAllocation(MindcodeParser.StmtHeapAllocationContext ctx) {
        return new AstAllocation(pos(ctx),
                AstAllocation.AllocationType.HEAP,
                createIdentifier(ctx.id),
                visitRangeIfNonNull(ctx.range));
    }

    @Override
    public AstAllocation visitStmtStackAllocation(MindcodeParser.StmtStackAllocationContext ctx) {
        return new AstAllocation(pos(ctx),
                AstAllocation.AllocationType.STACK,
                createIdentifier(ctx.id),
                visitRangeIfNonNull(ctx.range));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: directives">
    @Override
    public AstDirectiveSet visitStmtDirectiveSet(MindcodeParser.StmtDirectiveSetContext ctx) {
        AstDirectiveValue option = new AstDirectiveValue(pos(ctx.option), ctx.option.getText());
        if (ctx.directiveValues() == null) {
            return new AstDirectiveSet(pos(ctx), option, List.of());
        } else {
            return new AstDirectiveSet(pos(ctx), option, processDirectiveValues(ctx.directiveValues()));
        }
    }

    @Override
    public AstDirectiveValue visitDirectiveValue(MindcodeParser.DirectiveValueContext ctx) {
        return new AstDirectiveValue(pos(ctx), ctx.DIRECTIVEVALUE().getText());
    }

    private List<AstDirectiveValue> processDirectiveValues(MindcodeParser.DirectiveValuesContext ctx) {
        return ctx.directiveValue().stream().map(this::visitDirectiveValue).toList();
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions">
    @Override
    public AstBuiltInIdentifier visitExpBuiltInIdentifier(MindcodeParser.ExpBuiltInIdentifierContext ctx) {
        return new AstBuiltInIdentifier(pos(ctx), ctx.builtin.getText());
    }

    @Override
    public AstEnhancedComment visitExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx) {
        // Empty placeholders aren't supported in enhanced comment
        // The check will be done in code generator
        List<AstExpression> parts = ctx.children.stream().map(this::visitNullableExpression).filter(Objects::nonNull).toList();
        return new AstEnhancedComment(pos(ctx), parts);
    }

    @Override
    public AstFormattable visitExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx) {
        List<AstExpression> parts = ctx.children.stream().map(this::visitNullableExpression).filter(Objects::nonNull).toList();
        return new AstFormattable(pos(ctx), parts);
    }

    public AstIdentifier visitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx) {
        return createIdentifier(ctx.id);
    }

    @Override
    public AstIdentifier visitExpIdentifierExt(MindcodeParser.ExpIdentifierExtContext ctx) {
        return createIdentifier(ctx.id);
    }

    @Override
    public AstMemberAccess visitExpPropertyAccess(MindcodeParser.ExpPropertyAccessContext ctx) {
        return new AstMemberAccess(pos(ctx),
                visitExpression(ctx.object),
                new AstBuiltInIdentifier(pos(ctx.property), ctx.property.getText()));
    }

    @Override
    public AstMindcodeNode visitExpMemberAccess(MindcodeParser.ExpMemberAccessContext ctx) {
        return new AstMemberAccess(pos(ctx),
                visitExpression(ctx.object),
                createIdentifier(ctx.member));
    }

    @Override
    public AstMindcodeNode visitExpParentheses(MindcodeParser.ExpParenthesesContext ctx) {
        return new AstParentheses(pos(ctx), visitExpression(ctx.exp));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/arrays">
    @Override
    public AstArrayAccess visitExpArrayAccess(MindcodeParser.ExpArrayAccessContext ctx) {
        return new AstArrayAccess(pos(ctx),
                createIdentifier(ctx.array),
                visitExpression(ctx.index));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/assignments">
    @Override
    public AstExpression visitExpCompoundAssignment(MindcodeParser.ExpCompoundAssignmentContext ctx) {
        int type = switch (ctx.operation.getType()) {
            case MindcodeLexer.ASSIGN              -> -1;
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

        return new AstAssignment(pos(ctx),
                type >= 0 ? Operation.fromToken(type) : null,
                visitExpression(ctx.target),
                visitExpression(ctx.value));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/literals">
    @Override
    public AstLiteralBinary visitExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx) {
        String literal = ctx.BINARY().getText();
        return new AstLiteralBinary(pos(ctx), literal);
    }

    @Override
    public AstLiteralBoolean visitExpBooleanLiteralTrue(MindcodeParser.ExpBooleanLiteralTrueContext ctx) {
        String literal = ctx.TRUE().getText();
        return new AstLiteralBoolean(pos(ctx), literal);
    }

    @Override
    public AstLiteralBoolean visitExpBooleanLiteralFalse(MindcodeParser.ExpBooleanLiteralFalseContext ctx) {
        String literal = ctx.FALSE().getText();
        return new AstLiteralBoolean(pos(ctx), literal);
    }

    @Override
    public AstLiteralDecimal visitExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx) {
        String literal = ctx.DECIMAL().getText();
        return new AstLiteralDecimal(pos(ctx), literal);
    }

    @Override
    public AstLiteralFloat visitExpFloatLiteral(MindcodeParser.ExpFloatLiteralContext ctx) {
        String literal = ctx.FLOAT().getText();
        return new AstLiteralFloat(pos(ctx), literal);
    }

    @Override
    public AstLiteralHexadecimal visitExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx) {
        String literal = ctx.HEXADECIMAL().getText();
        return new AstLiteralHexadecimal(pos(ctx), literal);
    }

    @Override
    public AstLiteralNull visitExpNullLiteral(MindcodeParser.ExpNullLiteralContext ctx) {
        String literal = ctx.NULL().getText();
        return new AstLiteralNull(pos(ctx), literal);
    }

    @Override
    public AstLiteralString visitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx) {
        return createLiteralString(ctx.STRING());
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/operators">
    @Override
    public AstOperatorBinary visitExpAddition(MindcodeParser.ExpAdditionContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpBitShift(MindcodeParser.ExpBitShiftContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpBitwiseAnd(MindcodeParser.ExpBitwiseAndContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpBitwiseOr(MindcodeParser.ExpBitwiseOrContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpEqualityRelation(MindcodeParser.ExpEqualityRelationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpExponentiation(MindcodeParser.ExpExponentiationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpInequalityRelation(MindcodeParser.ExpInequalityRelationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpLogicalAnd(MindcodeParser.ExpLogicalAndContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpLogicalOr(MindcodeParser.ExpLogicalOrContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitExpMultiplication(MindcodeParser.ExpMultiplicationContext ctx) {
        return new AstOperatorBinary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.left),
                visitExpression(ctx.right));
    }

    private Operation resolveOperation(Token operatorToken) {
        return switch (operatorToken.getType()) {
            case MindcodeLexer.INCREMENT -> Operation.ADD;
            case MindcodeLexer.DECREMENT -> Operation.SUB;
            default -> throw new MindcodeInternalError("Unexpected prefix/postfix operator " + operatorToken.getText());
        };

    }

    @Override
    public AstOperatorIncDec visitExpPostfix(MindcodeParser.ExpPostfixContext ctx) {
        return new AstOperatorIncDec(pos(ctx.exp.getStart()),
                AstOperatorIncDec.Type.POSTFIX,
                resolveOperation(ctx.postfix),
                visitExpression(ctx.exp));
    }

    @Override
    public AstOperatorIncDec visitExpPrefix(MindcodeParser.ExpPrefixContext ctx) {
        return new AstOperatorIncDec(pos(ctx.exp.getStart()),
                AstOperatorIncDec.Type.PREFIX,
                resolveOperation(ctx.prefix),
                visitExpression(ctx.exp));
    }

    @Override
    public AstRange visitRangeExpression(RangeExpressionContext ctx) {
        return new AstRange(pos(ctx),
                visitExpression(ctx.firstValue),
                visitExpression(ctx.lastValue),
                ctx.operator.getType() == MindcodeLexer.DOT3);
    }

    @Override
    public AstOperatorTernary visitExpTernary(MindcodeParser.ExpTernaryContext ctx) {
        return new AstOperatorTernary(pos(ctx.condition.getStart()),
                visitExpression(ctx.condition),
                visitExpression(ctx.trueBranch),
                visitExpression(ctx.falseBranch));
    }

    @Override
    public AstOperatorUnary visitExpUnary(MindcodeParser.ExpUnaryContext ctx) {
        return new AstOperatorUnary(pos(ctx.op),
                operation(ctx.op),
                visitExpression(ctx.exp));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: formattableContents">
    @Override
    public AstLiteralString visitFmtText(MindcodeParser.FmtTextContext ctx) {
        // Can't use createLiteralString here
        // We're parsing inside a formattable string and there aren't double quotes around the token
        return new AstLiteralString(pos(ctx.TEXT()), ctx.TEXT().getText());
    }

    @Override
    public AstLiteralString visitFmtEscaped(MindcodeParser.FmtEscapedContext ctx) {
        String text = ctx.ESCAPESEQUENCE().getText();
        char escaped = text.charAt(1);
        return escaped == '\\' || escaped == '$'
                ? new AstLiteralEscape(pos(ctx), escaped + "")
                : new AstLiteralString(pos(ctx), text);
    }

    @Override
    public AstMindcodeNode visitFmtInterpolation(MindcodeParser.FmtInterpolationContext ctx) {
        // Just evaluate the expression
        return visitNonNull(ctx.expression());
    }
    //</editor-fold>

    //<editor-fold desc="Rules: formattablePlaceholder">
    @Override
    public AstFormattablePlaceholder visitFmtPlaceholderEmpty(MindcodeParser.FmtPlaceholderEmptyContext ctx) {
        return new AstFormattablePlaceholder(pos(ctx));
    }

    @Override
    public AstMindcodeNode visitFmtPlaceholderVariable(MindcodeParser.FmtPlaceholderVariableContext ctx) {
        return ctx.id != null
                ? createIdentifier(ctx.id)
                : new AstFormattablePlaceholder(pos(ctx));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: function calls">
    @Override
    public AstFunctionArgument visitArgument(MindcodeParser.ArgumentContext ctx) {
        return new AstFunctionArgument(pos(ctx),
                visitExpression(ctx.arg),
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
    public AstFunctionCall visitExpCallEnd(MindcodeParser.ExpCallEndContext ctx) {
        return new AstFunctionCall(pos(ctx), null,
                createIdentifier(ctx.END().getSymbol()),
                List.of());
    }

    @Override
    public AstFunctionCall visitExpCallFunction(MindcodeParser.ExpCallFunctionContext ctx) {
        return new AstFunctionCall(pos(ctx),
                null,
                createIdentifier(ctx.function),
                processArgumentList(ctx.argumentList()));
    }

    @Override
    public AstFunctionCall visitExpCallMethod(MindcodeParser.ExpCallMethodContext ctx) {
        return new AstFunctionCall(pos(ctx),
                visitNullableExpression(ctx.object),
                createIdentifier(ctx.function),
                processArgumentList(ctx.argumentList()));
    }

    //</editor-fold>

    //<editor-fold desc="Rules: function declarations">
    @Override
    public AstFunctionDeclaration visitExpDeclareFunction(MindcodeParser.ExpDeclareFunctionContext ctx) {
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
                processBody(ctx.body),
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

    //<editor-fold desc="Rules: structures">
    @Override
    public AstCodeBlock visitExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx) {
        return new AstCodeBlock(pos(ctx), processBody(ctx.exp));
    }

    @Override
    public AstCaseExpression visitExpCaseExpression(MindcodeParser.ExpCaseExpressionContext ctx) {
        return new AstCaseExpression(pos(ctx),
                visitExpression(ctx.exp),
                processCaseAlternatives(ctx.alternatives),
                processBody(ctx.elseBranch));
    }

    private List<AstCaseAlternative> processCaseAlternatives(@Nullable CaseAlternativesContext ctx) {
        return ctx != null && ctx.caseAlternative() != null
                ? ctx.caseAlternative().stream().map(this::visitCaseAlternative).toList()
                : List.of();
    }

    @Override
    public AstCaseAlternative visitCaseAlternative(MindcodeParser.CaseAlternativeContext ctx) {
        return new AstCaseAlternative(pos(ctx),
                ctx.values != null
                        ? ctx.values.whenValue().stream().map(this::visitExpression).toList()
                        : List.of(),
                processBody(ctx.body));
    }

    @Override
    public AstIfExpression visitExpIfExpression(MindcodeParser.ExpIfExpressionContext ctx) {
        return new AstIfExpression(pos(ctx),
                new AstIfBranch(pos(ctx), visitExpression(ctx.condition), processBody(ctx.trueBranch)),
                processElsifBranches(ctx.elsifBranches()),
                processBody(ctx.falseBranch));
    }

    private List<AstIfBranch> processElsifBranches(@Nullable ElsifBranchesContext ctx) {
        return ctx != null && ctx.elsifBranch() != null
                ? ctx.elsifBranch().stream().map(this::visitElsifBranch).toList()
                : List.of();
    }

    @Override
    public AstIfBranch visitElsifBranch(MindcodeParser.ElsifBranchContext ctx) {
        return new AstIfBranch(pos(ctx),
                visitExpression(ctx.condition),
                processBody(ctx.body));
    }

    @Override
    public AstForEachLoopStatement visitExpForEachLoop(MindcodeParser.ExpForEachLoopContext ctx) {
        return new AstForEachLoopStatement(pos(ctx),
                createIdentifierOrNull(ctx.label),
                processIteratorList(ctx.iterators),
                processList(ctx.values),
                processBody(ctx.body));
    }

    private List<AstLoopIterator> processIteratorList(MindcodeParser.IteratorListContext ctx) {
        return ctx.iterator().stream().map(this::visitIterator).toList();
    }

    @Override
    public AstLoopIterator visitIterator(MindcodeParser.IteratorContext ctx) {
        return new AstLoopIterator(pos(ctx),
                visitExpression(ctx.variable),
                ctx.modifier != null);
    }

    @Override
    public AstIteratedForLoopStatement visitExpForIteratedLoop(MindcodeParser.ExpForIteratedLoopContext ctx) {
        return new AstIteratedForLoopStatement(pos(ctx),
                createIdentifierOrNull(ctx.label),
                processList(ctx.init),
                visitExpressionIfNonNull(ctx.condition),
                processList(ctx.update),
                processBody(ctx.body));
    }

    @Override
    public AstRangedForLoopStatement visitExpForRangeLoop(MindcodeParser.ExpForRangeLoopContext ctx) {
        return new AstRangedForLoopStatement(pos(ctx),
                createIdentifierOrNull(ctx.label),
                visitExpression(ctx.control),
                visitRangeExpression(ctx.range),
                processBody(ctx.body));
    }

    @Override
    public AstWhileLoopStatement visitExpDoWhileLoop(MindcodeParser.ExpDoWhileLoopContext ctx) {
        if (ctx.loop != null) {
            context.messageConsumer().accept(CompilerMessage.warn(pos(ctx.loop),
                    "The 'loop' keyword is deprecated. Use just 'while' instead."));
        }

        return new AstWhileLoopStatement(pos(ctx),
                createIdentifierOrNull(ctx.label),
                visitExpression(ctx.condition),
                processBody(ctx.body),
                false);
    }

    @Override
    public AstWhileLoopStatement visitExpWhileLoop(MindcodeParser.ExpWhileLoopContext ctx) {
        return new AstWhileLoopStatement(pos(ctx),
                createIdentifierOrNull(ctx.label),
                visitExpression(ctx.condition),
                processBody(ctx.body),
                true);
    }

    @Override
    public AstBreakStatement visitExpBreak(MindcodeParser.ExpBreakContext ctx) {
        return new AstBreakStatement(pos(ctx), createIdentifierOrNull(ctx.label));
    }

    @Override
    public AstContinueStatement visitExpContinue(MindcodeParser.ExpContinueContext ctx) {
        return new AstContinueStatement(pos(ctx), createIdentifierOrNull(ctx.label));
    }

    @Override
    public AstReturnStatement visitExpReturn(MindcodeParser.ExpReturnContext ctx) {
        return new AstReturnStatement(pos(ctx), visitExpressionIfNonNull(ctx.value));
    }
    //</editor-fold>

}
