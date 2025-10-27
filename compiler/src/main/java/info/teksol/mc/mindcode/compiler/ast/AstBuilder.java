package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.CallType;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeParser;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeParser.*;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeParserBaseVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

@NullMarked
public class AstBuilder extends MindcodeParserBaseVisitor<AstMindcodeNode> {
    private final AstBuilderContext context;
    private final InputFile inputFile;
    private final CommonTokenStream tokenStream;
    private final SortedSet<AstIdentifier> remoteProcessors;
    private final boolean main;

    @Nullable AstModuleDeclaration moduleDeclaration;

    private AstBuilder(AstBuilderContext context, InputFile inputFile, CommonTokenStream tokenStream,
            SortedSet<AstIdentifier> remoteProcessors, boolean main) {
        this.context = context;
        this.inputFile = inputFile;
        this.tokenStream = tokenStream;
        this.remoteProcessors = remoteProcessors;
        this.main = main;
    }

    public static AstModule build(AstBuilderContext context, InputFile inputFile, CommonTokenStream tokenStream, ParseTree tree,
            SortedSet<AstIdentifier> remoteProcessors, boolean main) {
        AstBuilder astBuilder = new AstBuilder(context, inputFile, tokenStream, remoteProcessors, main);
        return (AstModule) astBuilder.visitNonNull(tree);
    }

    @Override
    public @Nullable AstMindcodeNode visit(ParseTree tree) {
        if (tree instanceof ParserRuleContext ctx && ctx.exception != null) {
            throw new ParserAbort();
        }
        return super.visit(tree);
    }

    //<editor-fold desc="Helper functions (visiting)">
    private @Nullable AstMindcodeNode visitNullable(ParseTree tree) {
        return visit(tree);
    }

    private AstMindcodeNode visitNonNull(ParseTree tree) {
        return Objects.requireNonNull(visitNullable(tree));
    }

    private @Nullable AstExpression visitAstExpressionNullable(ParseTree tree) {
        AstMindcodeNode result = visitNullable(tree);
        if (result == null) {
            return null;
        } else if (result instanceof AstExpression expression) {
            return expression;
        } else {
            // The syntax probably does not even permit this
            context.error(result, ERR.EXPRESSION_REQUIRED);
            return new AstLiteralNull(result.sourcePosition(), "null");
        }
    }

    private AstExpression visitAstExpression(ParseTree tree) {
        return Objects.requireNonNull(visitAstExpressionNullable(tree));
    }

    public @Nullable AstRange visitAstRangeIfNonNull(@Nullable AstRangeContext ctx) {
        return ctx == null ? null : visitAstRange(ctx);
    }

    public @Nullable AstExpression visitAstExpressionIfNonNull(@Nullable ParseTree node) {
        return node == null ? null : visitAstExpressionNullable(node);
    }

    private List<AstExpression> processExpressionList(@Nullable ExpressionListContext ctx) {
        return ctx != null ? ctx.expression().stream().map(this::visitAstExpression).toList() : List.of();
    }

    private List<AstExpression> processExpressionList(@Nullable DeclarationOrExpressionListContext ctx) {
        if (ctx == null) {
            return List.of();
        } else if (ctx.expList != null) {
            return ctx.expList.expression().stream().map(this::visitAstExpression).toList();
        } else if (ctx.variableDeclaration() != null) {
            return List.of(visitAstExpression(ctx.decl));
        } else {
            throw new MindcodeInternalError("No variable declaration or expression list in init context.");
        }
    }

    private List<AstMindcodeNode> processBody(@Nullable AstStatementListContext ctx) {
        if (ctx == null) return List.of();

        List<AstMindcodeNode> body = ctx.statement().stream().map(this::visitNonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        int currentLine = -1;
        int lineStart = -1;
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i).sourcePosition().line() != currentLine) {
                currentLine = body.get(i).sourcePosition().line();
                lineStart = i;
            }

            if (body.get(i) instanceof AstEnhancedComment comment && lineStart != i) {
                //noinspection SuspiciousListRemoveInLoop
                body.remove(i);
                body.add(lineStart, comment);
            }
        }

        return List.copyOf(body);
    }
    //</editor-fold>

    //<editor-fold desc="Helper functions (other)">
    private SourcePosition pos(ParserRuleContext ctx) {
        return SourcePosition.create(inputFile, ctx.getStart(), ctx.getStop(), ctx.getStart());
    }

    private SourcePosition pos(Token token) {
        return SourcePosition.create(inputFile, token);
    }

    private SourcePosition pos(Token start, Token end, Token token) {
        return SourcePosition.create(inputFile, start, end, token);
    }

    private SourcePosition pos(TerminalNode node) {
        return SourcePosition.create(inputFile, node.getSymbol());
    }

    private AstIdentifier identifier(Token token) {
        return new AstIdentifier(pos(token), token.getText(), token.getType() == MindcodeLexer.EXTIDENTIFIER);
    }

    private @Nullable AstIdentifier identifierIfNonNull(@Nullable Token token) {
        return token != null ? identifier(token) : null;
    }

    private List<AstIdentifier> identifiers(List<TerminalNode> tokens) {
        return tokens.stream().map(TerminalNode::getSymbol).map(this::identifier).toList();
    }

    private Operation operation(Token token) {
        return Operation.fromToken(token.getType());
    }

    private AstLiteralString literalString(Token token) {
        String literal = token.getText();
        return new AstLiteralString(pos(token), literal.substring(1, literal.length() - 1));
    }

    private @Nullable AstLiteralString literalStringIfNonNull(@Nullable Token token) {
        return token != null ? literalString(token) : null;
    }

    private AstLiteralColor color(TerminalNode node) {
        String literal = node.getText();
        if (literal.length() != 7 && literal.length() != 9) {
            context.error(pos(node), ERR.LITERAL_INVALID_COLOR_FORMAT);
            if (literal.length() < 9) {
                // pad with zeroes
                return new AstLiteralColor(pos(node), literal + "%00000000".substring(literal.length(), 9));
            } else {
                // trim to size
                return new AstLiteralColor(pos(node), literal.substring(0, 9));
            }
        } else {
            return new AstLiteralColor(pos(node), literal);
        }
    }

    private AstLiteralChar charLiteral(TerminalNode node) {
        String literal = node.getText();
        return switch (literal.length()) {
            case 3 -> new AstLiteralChar(pos(node), literal.charAt(1));
            case 4 -> new AstLiteralChar(pos(node), literal.charAt(2) == 'n' ? 10 : literal.charAt(2));
            default -> {
                context.error(pos(node), ERR.LITERAL_INVALID_CHAR_FORMAT);
                yield new AstLiteralChar(pos(node), ' ');
            }
        };
    }

    private @Nullable AstDocComment findDocComment(Token token) {
        int tokenIndex = token.getTokenIndex();
        List<Token> docTokens = Objects.requireNonNullElse(
                tokenStream.getHiddenTokensToLeft(tokenIndex, Token.HIDDEN_CHANNEL), List.of());

        // Note: the "reduce" trick to get the last item on the stream is not very efficient, but we don't expect
        // the token list to be long. The length should be at most 1 in practically all cases.
        return docTokens.stream()
                .reduce((first, second) -> second)
                .filter(t -> t.getType() == MindcodeLexer.DOC_COMMENT)
                .map(docToken -> new AstDocComment(pos(docToken), docToken.getText()))
                .orElse(null);
    }
    //</editor-fold>

    //<editor-fold desc="Rules: basic structures">
    @Override
    public AstModule visitAstModule(MindcodeParser.AstModuleContext ctx) {
        moduleDeclaration = null;
        List<AstMindcodeNode> body = processBody(ctx.astStatementList());
        return new AstModule(pos(ctx), moduleDeclaration, body, remoteProcessors, main);
    }

    @Override
    public AstStatementList visitAstStatementList(AstStatementListContext ctx) {
        return new AstStatementList(pos(ctx), processBody(ctx));
    }

    @Override
    public AstCodeBlock visitAstCodeBlock(MindcodeParser.AstCodeBlockContext ctx) {
        return new AstCodeBlock(pos(ctx), processBody(ctx.exp), false);
    }

    @Override
    public AstCodeBlock visitAstDebugBlock(MindcodeParser.AstDebugBlockContext ctx) {
        return new AstCodeBlock(pos(ctx), processBody(ctx.exp), true);
    }

    @Override
    public AstMindcodeNode visitAstParentheses(MindcodeParser.AstParenthesesContext ctx) {
        return new AstParentheses(pos(ctx), visitAstExpression(ctx.exp));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: directives">
    @Override
    public AstMindcodeNode visitAstKeywordOrBuiltin(AstKeywordOrBuiltinContext ctx) {
        if (ctx.KEYWORD() != null) return new AstKeyword(pos(ctx), ctx.KEYWORD().getText());
        if (ctx.BUILTINIDENTIFIER() != null) return new AstBuiltInIdentifier(pos(ctx), ctx.BUILTINIDENTIFIER().getText());
        if (ctx.IDENTIFIER() != null) return new AstIdentifier(pos(ctx), ctx.IDENTIFIER().getText());
        throw new MindcodeInternalError("Unhandled or missing identifier");
    }

    @Override
    public AstDirectiveDeclare visitAstDirectiveDeclare(AstDirectiveDeclareContext ctx) {
        @SuppressWarnings("NullableProblems")
        List<AstMindcodeNode> list = ctx.elements.astKeywordOrBuiltin().stream()
                .map(this::visit)
                .filter(Objects::nonNull)
                .toList();

        return new AstDirectiveDeclare(pos(ctx),
                identifier(ctx.category),
                list);
    }

    private List<AstDirectiveValue> processDirectiveValues(MindcodeParser.DirectiveValuesContext ctx) {
        return ctx.astDirectiveValue().stream().map(this::visitAstDirectiveValue).toList();
    }

    @Override
    public AstDirectiveValue visitAstDirectiveValue(MindcodeParser.AstDirectiveValueContext ctx) {
        return new AstDirectiveValue(pos(ctx), ctx.DIRECTIVEVALUE().getText());
    }

    @Override
    public AstDirectiveSet visitAstDirectiveSet(MindcodeParser.AstDirectiveSetContext ctx) {
        return createAstDirectiveSet(ctx, false, ctx.option, ctx.directiveValues());
    }

    @Override
    public AstDirectiveSet visitAstDirectiveSetLocal(MindcodeParser.AstDirectiveSetLocalContext ctx) {
        return createAstDirectiveSet(ctx, true, ctx.option, ctx.directiveValues());
    }

    private AstDirectiveSet createAstDirectiveSet(MindcodeParser.DirectiveContext ctx, boolean local,
            AstDirectiveValueContext optionContext, @Nullable DirectiveValuesContext valueContext) {
        AstDirectiveValue option = new AstDirectiveValue(pos(ctx), optionContext.getText());
        if (valueContext == null) {
            return new AstDirectiveSet(pos(ctx), findDocComment(ctx.getStart()), local, option, List.of());
        } else {
            return new AstDirectiveSet(pos(ctx), findDocComment(ctx.getStart()), local, option,
                    processDirectiveValues(valueContext));
        }
    }
    //</editor-fold>

    //<editor-fold desc="Rules: declarations">
    @Override
    public AstEnhancedComment visitAstEnhancedComment(MindcodeParser.AstEnhancedCommentContext ctx) {
        // Empty placeholders aren't supported in enhanced comment
        // The check will be done in code generator
        List<AstExpression> parts = ctx.children.stream().map(this::visitAstExpressionNullable)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        if (!parts.isEmpty() && parts.getFirst() instanceof AstLiteralString str) {
            parts.set(0, new AstLiteralString(str.sourcePosition(), str.getLiteral().stripLeading()));
        }

        return new AstEnhancedComment(pos(ctx), parts);
    }

    @Override
    public AstMindcodeNode visitAstAllocations(MindcodeParser.AstAllocationsContext ctx) {
        List<AstAllocation> allocations = ctx.allocations().astAllocation().stream()
                .map(this::visitNullable).map(AstAllocation.class::cast).toList();
        return new AstAllocations(pos(ctx), allocations);
    }

    @Override
    public AstAllocation visitAstAllocation(MindcodeParser.AstAllocationContext ctx) {
        AstAllocation.AllocationType type = switch (ctx.type.getType()) {
            case MindcodeLexer.HEAP  -> AstAllocation.AllocationType.HEAP;
            case MindcodeLexer.STACK -> AstAllocation.AllocationType.STACK;
            default -> throw new MindcodeInternalError("Unsupported allocation type: " + ctx.type.getText());
        };
        return new AstAllocation(pos(ctx),
                type,
                identifier(ctx.id),
                visitAstRangeIfNonNull(ctx.range));
    }

    @Override
    public AstParameter visitAstParameter(MindcodeParser.AstParameterContext ctx) {
        return new AstParameter(pos(ctx),
                findDocComment(ctx.getStart()),
                identifier(ctx.name),
                visitAstExpression(ctx.value));
    }

    @Override
    public AstModuleDeclaration visitAstModuleDeclaration(AstModuleDeclarationContext ctx) {
        AstModuleDeclaration declaration = new AstModuleDeclaration(pos(ctx), identifier(ctx.name));
        if (moduleDeclaration != null) {
            context.error(declaration, ERR.MULTIPLE_MODULE_DECLARATIONS);
        } else {
            moduleDeclaration = declaration;
        }

        return declaration;
    }

    @Override
    public AstRequireFile visitAstRequireFile(MindcodeParser.AstRequireFileContext ctx) {
        AstRequireFile requirement = new AstRequireFile(pos(ctx), literalString(ctx.file),
                ctx.processors == null ? List.of() : identifiers(ctx.processors.IDENTIFIER()));
        context.addRequirement(requirement);
        return requirement;
    }

    @Override
    public AstRequireLibrary visitAstRequireLibrary(MindcodeParser.AstRequireLibraryContext ctx) {
        AstRequireLibrary requirement = new AstRequireLibrary(pos(ctx), identifier(ctx.library),
                ctx.processors == null ? List.of() : identifiers(ctx.processors.IDENTIFIER()));
        context.addRequirement(requirement);
        return requirement;
    }

    private AstVariableModifier createVariableModifier(DeclModifierContext ctx) {
        Modifier modifier = Modifier.fromToken(ctx.modifier.getType());
        return new AstVariableModifier(pos(ctx), modifier, createModifierParametrization(modifier, ctx));
    }

    private @Nullable AstMindcodeNode createModifierParametrization(Modifier modifier, DeclModifierContext ctx) {
        if (modifier == Modifier.EXTERNAL && ctx.memory != null) {
            if (ctx.LPAREN() == null) {
                context.warn(pos(ctx), WARN.MISSING_MODIFIER_PARENS, ctx.modifier.getText());
            }
            return new AstExternalParameters(pos(ctx), identifier(ctx.memory),
                    visitAstRangeIfNonNull(ctx.astRange()), visitAstExpressionIfNonNull(ctx.index));
        } else if (modifier == Modifier.REMOTE && ctx.processor != null) {
            if (ctx.LPAREN() == null) {
                context.warn(pos(ctx), WARN.MISSING_MODIFIER_PARENS, ctx.modifier.getText());
            }
            return new AstRemoteParameters(pos(ctx), identifier(ctx.processor));
        } else if (modifier == Modifier.MLOG) {
            return new AstMlogParameters(pos(ctx), processExpressionList(ctx.mlog));
        } else {
            return null;
        }
    }

    private AstVariableSpecification createVariableSpecification(VariableSpecificationContext ctx) {
        if (ctx.LBRACKET() != null) {
            return new AstVariableSpecification(pos(ctx),
                    identifier(ctx.id),
                    true,
                    visitAstExpressionIfNonNull(ctx.length),
                    processInitialArrayValues(ctx.valueList()));
        } else {
            return new AstVariableSpecification(pos(ctx),
                    identifier(ctx.id),
                    false,
                    null,
                    processInitialValue(ctx.expression()));
        }
    }

    private List<AstExpression> processInitialValue(@Nullable ExpressionContext ctx) {
        return ctx == null ? List.of() : List.of(visitAstExpression(ctx));
    }

    private List<AstExpression> processInitialArrayValues(@Nullable ValueListContext ctx) {
        return ctx == null ? List.of() : processExpressionList(ctx.expressionList());
    }

    @Override
    public AstMindcodeNode visitVariableDeclaration(VariableDeclarationContext ctx) {
        List<AstVariableModifier> modifiers = ctx.declModifier().stream().map(this::createVariableModifier).toList();
        List<AstVariableSpecification> variables = ctx.variables == null ? List.of()
                : ctx.variables.variableSpecification().stream().map(this::createVariableSpecification).toList();
        return new AstVariablesDeclaration(pos(ctx), findDocComment(ctx.getStart()), modifiers, variables);
    }

    //</editor-fold>

    //<editor-fold desc="Rules: function declarations">
    @Override
    public AstFunctionDeclaration visitAstFunctionDeclaration(MindcodeParser.AstFunctionDeclarationContext ctx) {
        DataType dataType = switch (ctx.type.getType()) {
            case MindcodeLexer.VOID -> DataType.VOID;
            case MindcodeLexer.DEF  -> DataType.VAR;
            default -> throw new MindcodeInternalError("Unsupported type: " + ctx.type.getText());
        };

        return new AstFunctionDeclaration(pos(ctx),
                findDocComment(ctx.getStart()),
                identifier(ctx.name),
                dataType,
                processParameterList(ctx.parameterList()),
                processBody(ctx.body),
                ctx.callType == null ? CallType.NONE : CallType.fromToken(ctx.callType.getType()),
                ctx.debug != null
        );
    }

    private List<AstFunctionParameter> processParameterList(MindcodeParser.ParameterListContext ctx) {
        return ctx.astFunctionParameter() != null
                ? ctx.astFunctionParameter().stream().map(this::visitAstFunctionParameter).toList()
                : List.of();
    }

    @Override
    public AstFunctionParameter visitAstFunctionParameter(MindcodeParser.AstFunctionParameterContext ctx) {
        return new AstFunctionParameter(pos(ctx),
                identifier(ctx.name),
                ctx.modifier_in != null,
                ctx.modifier_out != null,
                ctx.modifier_ref != null,
                ctx.varargs != null);
    }
    //</editor-fold>

    //<editor-fold desc="Rules: statements">
    @Override
    public AstForEachLoopStatement visitAstForEachLoopStatement(MindcodeParser.AstForEachLoopStatementContext ctx) {
        return new AstForEachLoopStatement(pos(ctx),
                identifierIfNonNull(ctx.label),
                ctx.iteratorsValuesGroups().astIteratorsValuesGroup().stream().map(this::visitAstIteratorsValuesGroup).toList(),
                processBody(ctx.body));
    }

    @Override
    public AstIteratorsValuesGroup visitAstIteratorsValuesGroup(AstIteratorsValuesGroupContext ctx) {
        return new AstIteratorsValuesGroup(pos(ctx),
                ctx.iteratorGroup().type != null,
                ctx.iteratorGroup().astIterator().stream().map(this::visitAstIterator).toList(),
                new AstExpressionList(pos(ctx.expressionList()), processExpressionList(ctx.expressionList())),
                ctx.DESCENDING() != null
        );
    }

    @Override
    public AstIterator visitAstIterator(MindcodeParser.AstIteratorContext ctx) {
        return new AstIterator(pos(ctx),
                visitAstExpression(ctx.variable),
                ctx.modifier != null);
    }

    @Override
    public AstIteratedForLoopStatement visitAstIteratedForLoopStatement(MindcodeParser.AstIteratedForLoopStatementContext ctx) {
        return new AstIteratedForLoopStatement(pos(ctx),
                identifierIfNonNull(ctx.label),
                processExpressionList(ctx.init),
                visitAstExpressionIfNonNull(ctx.condition),
                processExpressionList(ctx.update),
                processBody(ctx.body));
    }

    @Override
    public AstRangedForLoopStatement visitAstRangedForLoopStatement(MindcodeParser.AstRangedForLoopStatementContext ctx) {
        return new AstRangedForLoopStatement(pos(ctx),
                identifierIfNonNull(ctx.label),
                ctx.type != null,
                visitAstExpression(ctx.control),
                visitAstRange(ctx.range),
                ctx.DESCENDING() != null,
                processBody(ctx.body));
    }

    @Override
    public AstWhileLoopStatement visitAstDoWhileLoopStatement(MindcodeParser.AstDoWhileLoopStatementContext ctx) {
        return new AstWhileLoopStatement(pos(ctx),
                identifierIfNonNull(ctx.label),
                visitAstExpression(ctx.condition),
                processBody(ctx.body),
                false);
    }

    @Override
    public AstWhileLoopStatement visitAstWhileLoopStatement(MindcodeParser.AstWhileLoopStatementContext ctx) {
        return new AstWhileLoopStatement(pos(ctx),
                identifierIfNonNull(ctx.label),
                visitAstExpression(ctx.condition),
                processBody(ctx.body),
                true);
    }

    @Override
    public AstBreakStatement visitAstBreakStatement(MindcodeParser.AstBreakStatementContext ctx) {
        return new AstBreakStatement(pos(ctx), identifierIfNonNull(ctx.label));
    }

    @Override
    public AstContinueStatement visitAstContinueStatement(MindcodeParser.AstContinueStatementContext ctx) {
        return new AstContinueStatement(pos(ctx), identifierIfNonNull(ctx.label));
    }

    @Override
    public AstReturnStatement visitAstReturnStatement(MindcodeParser.AstReturnStatementContext ctx) {
        return new AstReturnStatement(pos(ctx), visitAstExpressionIfNonNull(ctx.value));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: lvalues">
    public AstIdentifier visitAstIdentifier(MindcodeParser.AstIdentifierContext ctx) {
        return identifier(ctx.id);
    }

    @Override
    public AstIdentifier visitAstIdentifierExt(MindcodeParser.AstIdentifierExtContext ctx) {
        return identifier(ctx.id);
    }

    @Override
    public AstBuiltInIdentifier visitAstBuiltInIdentifier(AstBuiltInIdentifierContext ctx) {
        return new AstBuiltInIdentifier(pos(ctx), ctx.builtin.getText());
    }

    @Override
    public AstArrayAccess visitAstArrayAccess(MindcodeParser.AstArrayAccessContext ctx) {
        return new AstArrayAccess(pos(ctx),
                identifier(ctx.array),
                visitAstExpression(ctx.index));
    }

    @Override
    public AstMindcodeNode visitAstSubarray(AstSubarrayContext ctx) {
        return new AstSubarray(pos(ctx),
                identifier(ctx.array),
                visitAstRange(ctx.range));
    }

    @Override
    public AstMindcodeNode visitAstRemoteArray(AstRemoteArrayContext ctx) {
        return new AstArrayAccess(pos(ctx),
                identifier(ctx.processor),
                identifier(ctx.array),
                visitAstExpression(ctx.index));
    }

    @Override
    public AstMindcodeNode visitAstRemoteSubarray(AstRemoteSubarrayContext ctx) {
        return new AstSubarray(pos(ctx),
                identifier(ctx.processor),
                identifier(ctx.array),
                visitAstRange(ctx.range));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: function calls">
    @Override
    public AstFunctionCall visitAstFunctionCallEnd(MindcodeParser.AstFunctionCallEndContext ctx) {
        return new AstFunctionCall(pos(ctx), null,
                identifier(ctx.END().getSymbol()),
                List.of());
    }

    @Override
    public AstFunctionCall visitAstFunctionCall(MindcodeParser.AstFunctionCallContext ctx) {
        return new AstFunctionCall(pos(ctx),
                null,
                identifier(ctx.function),
                processArgumentList(ctx.argumentList()));
    }

    @Override
    public AstFunctionCall visitAstMethodCall(MindcodeParser.AstMethodCallContext ctx) {
        return new AstFunctionCall(pos(ctx),
                visitAstExpressionNullable(ctx.object),
                identifier(ctx.function),
                processArgumentList(ctx.argumentList()));
    }

    @Override
    public AstFunctionArgument visitAstFunctionArgument(MindcodeParser.AstFunctionArgumentContext ctx) {
        return new AstFunctionArgument(pos(ctx),
                visitAstExpression(ctx.arg),
                ctx.modifier_in != null,
                ctx.modifier_out != null,
                ctx.modifier_ref != null);
    }

    @Override
    public AstFunctionArgument visitAstOptionalFunctionArgument(MindcodeParser.AstOptionalFunctionArgumentContext ctx) {
        return ctx.astFunctionArgument() != null
                ? visitAstFunctionArgument(ctx.astFunctionArgument())
                : new AstFunctionArgument(pos(ctx));
    }

    public List<AstFunctionArgument> processArgumentList(MindcodeParser.ArgumentListContext ctx) {
        if (ctx.astFunctionArgument() != null) {
            return List.of(visitAstFunctionArgument(ctx.astFunctionArgument()));
        } else if (ctx.astOptionalFunctionArgument() != null) {
            return ctx.astOptionalFunctionArgument().stream().map(this::visitAstOptionalFunctionArgument).toList();
        } else {
            return List.of();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/member access">
    @Override
    public AstPropertyAccess visitAstPropertyAccess(MindcodeParser.AstPropertyAccessContext ctx) {
        return new AstPropertyAccess(pos(ctx),
                visitAstExpression(ctx.object),
                new AstBuiltInIdentifier(pos(ctx.property), ctx.property.getText()));
    }

    @Override
    public AstMindcodeNode visitAstMemberAccess(MindcodeParser.AstMemberAccessContext ctx) {
        return new AstMemberAccess(pos(ctx),
                visitAstExpression(ctx.object),
                identifier(ctx.member));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/control flow">
    @Override
    public AstCaseExpression visitAstCaseExpression(MindcodeParser.AstCaseExpressionContext ctx) {
        return new AstCaseExpression(pos(ctx),
                visitAstExpression(ctx.exp),
                processCaseAlternatives(ctx.alternatives),
                processBody(ctx.elseBranch),
                ctx.elseBranch != null);
    }

    private List<AstCaseAlternative> processCaseAlternatives(@Nullable CaseAlternativesContext ctx) {
        return ctx != null && ctx.astCaseAlternative() != null
                ? ctx.astCaseAlternative().stream().map(this::visitAstCaseAlternative).toList()
                : List.of();
    }

    @Override
    public AstCaseAlternative visitAstCaseAlternative(MindcodeParser.AstCaseAlternativeContext ctx) {
        return new AstCaseAlternative(pos(ctx),
                ctx.values != null
                        ? ctx.values.whenValue().stream().map(this::visitAstExpression).toList()
                        : List.of(),
                processBody(ctx.body));
    }

    @Override
    public AstIfExpression visitAstIfExpression(MindcodeParser.AstIfExpressionContext ctx) {
        return new AstIfExpression(pos(ctx),
                new AstIfBranch(pos(ctx), visitAstExpression(ctx.condition), processBody(ctx.trueBranch)),
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
                visitAstExpression(ctx.condition),
                processBody(ctx.body));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/literals">
    @Override
    public AstFormattableLiteral visitAstFormattableLiteral(MindcodeParser.AstFormattableLiteralContext ctx) {
        @SuppressWarnings("NullableProblems")
        List<AstExpression> parts = ctx.children.stream().map(this::visitAstExpressionNullable).filter(Objects::nonNull).toList();
        return new AstFormattableLiteral(pos(ctx), parts);
    }

    @Override
    public AstMindcodeNode visitAstKeyword(AstKeywordContext ctx) {
        return new AstKeyword(pos(ctx), ctx.KEYWORD().getText());
    }

    @Override
    public AstLiteralString visitAstLiteralString(MindcodeParser.AstLiteralStringContext ctx) {
        return literalString(ctx.string);
    }

    @Override
    public AstLiteralColor visitAstLiteralColor(AstLiteralColorContext ctx) {
        return color(ctx.COLOR());
    }

    @Override
    public AstLiteralNamedColor visitAstLiteralNamedColor(AstLiteralNamedColorContext ctx) {
        String literal = ctx.NAMEDCOLOR().getText();
        return new AstLiteralNamedColor(pos(ctx), literal);
    }

    @Override
    public AstLiteralBinary visitAstLiteralBinary(MindcodeParser.AstLiteralBinaryContext ctx) {
        String literal = ctx.BINARY().getText();
        return new AstLiteralBinary(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralHexadecimal visitAstLiteralHexadecimal(MindcodeParser.AstLiteralHexadecimalContext ctx) {
        String literal = ctx.HEXADECIMAL().getText();
        return new AstLiteralHexadecimal(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralDecimal visitAstLiteralDecimal(MindcodeParser.AstLiteralDecimalContext ctx) {
        String literal = ctx.DECIMAL().getText();
        return new AstLiteralDecimal(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralFloat visitAstLiteralFloat(MindcodeParser.AstLiteralFloatContext ctx) {
        String literal = ctx.FLOAT().getText();
        return new AstLiteralFloat(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralChar visitAstLiteralChar(MindcodeParser.AstLiteralCharContext ctx) {
        return charLiteral(ctx.CHAR());
    }

    @Override
    public AstLiteralNull visitAstLiteralNull(MindcodeParser.AstLiteralNullContext ctx) {
        String literal = ctx.NULL().getText();
        return new AstLiteralNull(pos(ctx), literal);
    }

    @Override
    public AstLiteralBoolean visitAstLiteralBoolean(MindcodeParser.AstLiteralBooleanContext ctx) {
        String literal = ctx.value.getText();
        return new AstLiteralBoolean(pos(ctx), literal);
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/operators (unary)">
    @Override
    public AstOperatorUnary visitAstOperatorUnary(MindcodeParser.AstOperatorUnaryContext ctx) {
        return new AstOperatorUnary(pos(ctx.op, ctx.exp.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.exp));
    }

    @Override
    public AstOperatorIncDec visitAstOperatorIncDecPostfix(MindcodeParser.AstOperatorIncDecPostfixContext ctx) {
        return new AstOperatorIncDec(pos(ctx.exp.getStart()),
                AstOperatorIncDec.Type.POSTFIX,
                resolveOperation(ctx.postfix),
                visitAstExpression(ctx.exp));
    }

    @Override
    public AstOperatorIncDec visitAstOperatorIncDecPrefix(MindcodeParser.AstOperatorIncDecPrefixContext ctx) {
        return new AstOperatorIncDec(pos(ctx.exp.getStart()),
                AstOperatorIncDec.Type.PREFIX,
                resolveOperation(ctx.prefix),
                visitAstExpression(ctx.exp));
    }

    private Operation resolveOperation(Token operatorToken) {
        return switch (operatorToken.getType()) {
            case MindcodeLexer.INCREMENT -> Operation.ADD;
            case MindcodeLexer.DECREMENT -> Operation.SUB;
            default -> throw new MindcodeInternalError("Unexpected prefix/postfix operator " + operatorToken.getText());
        };

    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/operators (binary)">
    @Override
    public AstOperatorBinary visitAstOperatorBinaryExp(MindcodeParser.AstOperatorBinaryExpContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryMul(MindcodeParser.AstOperatorBinaryMulContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryAdd(MindcodeParser.AstOperatorBinaryAddContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryShift(MindcodeParser.AstOperatorBinaryShiftContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryBitwiseAnd(MindcodeParser.AstOperatorBinaryBitwiseAndContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryBitwiseOr(MindcodeParser.AstOperatorBinaryBitwiseOrContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryInequality(MindcodeParser.AstOperatorBinaryInequalityContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryEquality(MindcodeParser.AstOperatorBinaryEqualityContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryLogicalAnd(MindcodeParser.AstOperatorBinaryLogicalAndContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }

    @Override
    public AstOperatorBinary visitAstOperatorBinaryLogicalOr(MindcodeParser.AstOperatorBinaryLogicalOrContext ctx) {
        return new AstOperatorBinary(pos(ctx.left.start, ctx.right.stop, ctx.op),
                operation(ctx.op),
                visitAstExpression(ctx.left),
                visitAstExpression(ctx.right));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/operators (ternary)">
    @Override
    public AstOperatorTernary visitAstOperatorTernary(MindcodeParser.AstOperatorTernaryContext ctx) {
        return new AstOperatorTernary(pos(ctx.condition.getStart()),
                visitAstExpression(ctx.condition),
                visitAstExpression(ctx.trueBranch),
                visitAstExpression(ctx.falseBranch));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: expressions/assignments">
    @Override
    public AstAssignment visitAstAssignment(MindcodeParser.AstAssignmentContext ctx) {
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
            case MindcodeLexer.ASSIGN_EMOD         -> MindcodeLexer.EMOD;
            case MindcodeLexer.ASSIGN_MUL          -> MindcodeLexer.MUL;
            case MindcodeLexer.ASSIGN_PLUS         -> MindcodeLexer.PLUS;
            case MindcodeLexer.ASSIGN_POW          -> MindcodeLexer.POW;
            case MindcodeLexer.ASSIGN_SHIFT_LEFT   -> MindcodeLexer.SHIFT_LEFT;
            case MindcodeLexer.ASSIGN_SHIFT_RIGHT  -> MindcodeLexer.SHIFT_RIGHT;
            case MindcodeLexer.ASSIGN_USHIFT_RIGHT -> MindcodeLexer.USHIFT_RIGHT;
            default -> throw new MindcodeInternalError("Cannot map assignment token %s to operation token.", ctx.operation.getText());
        };

        return new AstAssignment(pos(ctx),
                type >= 0 ? Operation.fromToken(type) : null,
                visitAstExpression(ctx.target),
                visitAstExpression(ctx.value));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: formattable">
    @Override
    public AstLiteralString visitFormattableText(MindcodeParser.FormattableTextContext ctx) {
        // Can't use createLiteralString here
        // We're parsing inside a formattable string, and there aren't double quotes around the token
        return new AstLiteralString(pos(ctx.TEXT()), ctx.TEXT().getText());
    }

    @Override
    public AstLiteralString visitFormattableEscaped(MindcodeParser.FormattableEscapedContext ctx) {
        String text = ctx.ESCAPESEQUENCE().getText();
        char escaped = text.charAt(1);
        return escaped == '\\' || escaped == '$'
                ? new AstLiteralEscape(pos(ctx), escaped + "")
                : new AstLiteralString(pos(ctx), text);
    }

    @Override
    public AstMindcodeNode visitFormattableInterpolation(MindcodeParser.FormattableInterpolationContext ctx) {
        // Just evaluate the expression
        return visitNonNull(ctx.expression());
    }

    @Override
    public AstFormattablePlaceholder visitFormattablePlaceholderEmpty(MindcodeParser.FormattablePlaceholderEmptyContext ctx) {
        return new AstFormattablePlaceholder(pos(ctx));
    }

    @Override
    public AstMindcodeNode visitFormattablePlaceholderVariable(MindcodeParser.FormattablePlaceholderVariableContext ctx) {
        return ctx.id != null
                ? identifier(ctx.id)
                : new AstFormattablePlaceholder(pos(ctx));
    }
    //</editor-fold>

    //<editor-fold desc="Rules: fragments">
    @Override
    public AstRange visitAstRange(AstRangeContext ctx) {
        return new AstRange(pos(ctx),
                visitAstExpression(ctx.firstValue),
                visitAstExpression(ctx.lastValue),
                ctx.operator.getType() == MindcodeLexer.DOT3);
    }
    //</editor-fold>

    //<editor-fold desc="Rules: mlog blocks">

    @Override
    public AstMlogBlock visitAstMlogBlock(AstMlogBlockContext ctx) {
        return new AstMlogBlock(pos(ctx),
                processMlogVariables(ctx.variables),
                processMlogStatements(ctx.mlogBlock().mlogStatement())
        );
    }

    private List<AstMlogVariable> processMlogVariables(@Nullable MlogVariableListContext ctx) {
        return ctx != null
                ? ctx.astMlogVariable().stream().map(this::visitAstMlogVariable).toList()
                : List.of();
    }

    @Override
    public AstMlogVariable visitAstMlogVariable(AstMlogVariableContext ctx) {
        return new AstMlogVariable(pos(ctx),
                identifier(ctx.name),
                ctx.modifier_in != null,
                ctx.modifier_out != null);
    }

    private List<AstMlogStatement> processMlogStatements(@Nullable List<MlogStatementContext> ctx) {
        return ctx != null
                ? ctx.stream().map(this::visit).map(AstMlogStatement.class::cast).toList()
                : List.of();
    }

    @Override
    public AstMlogStatement visitAstMlogLabel(AstMlogLabelContext ctx) {
        return new AstMlogStatement(pos(ctx),
                identifier(ctx.label),  // Note: the identifier contains the colon at the end
                null,
                null);
    }

    @Override
    public AstMindcodeNode visitAstMlogLabelWithComment(AstMlogLabelWithCommentContext ctx) {
        return new AstMlogStatement(pos(ctx),
                identifier(ctx.label),  // Note: the identifier contains the colon at the end
                null,
                processComment(ctx.whitespace, ctx.comment));
    }

    @Override
    public AstMindcodeNode visitAstMlogInstruction(AstMlogInstructionContext ctx) {
        return new AstMlogStatement(pos(ctx),
                null,
                processMlogInstruction(ctx.mlogInstruction()),
                null);
    }

    @Override
    public AstMindcodeNode visitAstMlogInstructionWithComment(AstMlogInstructionWithCommentContext ctx) {
        return new AstMlogStatement(pos(ctx),
                null,
                processMlogInstruction(ctx.mlogInstruction()),
                processComment(ctx.whitespace, ctx.comment));
    }

    @Override
    public AstMindcodeNode visitAstMlogComment(AstMlogCommentContext ctx) {
        return new AstMlogStatement(pos(ctx),
                null,
                null,
                processComment(null, ctx.comment));
    }

    private AstMlogInstruction processMlogInstruction(MlogInstructionContext ctx) {
        return new AstMlogInstruction(pos(ctx),
                processMlogToken(ctx.opcode),
                ctx.mlogTokenOrLiteral().stream().map(this::visit).map(AstExpression.class::cast).toList()
        );
    }

    private AstMlogComment processComment(@Nullable Token whitespace, Token comment) {
        return new AstMlogComment(pos(comment),
                whitespace == null ? "" : whitespace.getText(),
                comment.getText());
    }

    private AstMlogToken processMlogToken(Token token) {
        return new AstMlogToken(pos(token), token.getText());
    }

    @Override
    public AstMlogToken visitAstMlogToken(AstMlogTokenContext ctx) {
        return new AstMlogToken(pos(ctx.token), ctx.token.getText());
    }

    @Override
    public AstBuiltInIdentifier visitAstMlogBuiltin(AstMlogBuiltinContext ctx) {
        return new AstBuiltInIdentifier(pos(ctx), ctx.builtin.getText());
    }

    @Override
    public AstLiteralString visitAstMlogString(AstMlogStringContext ctx) {
        return literalString(ctx.literal);
    }

    @Override
    public AstLiteralColor visitAstMlogColor(AstMlogColorContext ctx) {
        return color(ctx.MLOGCOLOR());
    }

    @Override
    public AstLiteralNamedColor visitAstMlogNamedColor(AstMlogNamedColorContext ctx) {
        String literal = ctx.MLOGNAMEDCOLOR().getText();
        return new AstLiteralNamedColor(pos(ctx), literal);
    }

    @Override
    public AstLiteralBinary visitAstMlogBinary(AstMlogBinaryContext ctx) {
        String literal = ctx.MLOGBINARY().getText();
        return new AstLiteralBinary(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralHexadecimal visitAstMlogHexadecimal(AstMlogHexadecimalContext ctx) {
        String literal = ctx.MLOGHEXADECIMAL().getText();
        return new AstLiteralHexadecimal(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralDecimal visitAstMlogDecimal(AstMlogDecimalContext ctx) {
        String literal = ctx.MLOGDECIMAL().getText();
        return new AstLiteralDecimal(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralFloat visitAstMlogFloat(AstMlogFloatContext ctx) {
        String literal = ctx.MLOGFLOAT().getText();
        return new AstLiteralFloat(pos(ctx), literal, false);
    }

    @Override
    public AstLiteralChar visitAstMlogChar(AstMlogCharContext ctx) {
        return charLiteral(ctx.MLOGCHAR());
    }
    //</editor-fold>
}
