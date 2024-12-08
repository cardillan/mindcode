// Generated from MindcodeParser.g4 by ANTLR 4.13.1
package info.teksol.mindcode.v3.compiler.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MindcodeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MindcodeParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MindcodeParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#statementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementList(MindcodeParser.StatementListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expRequireFile}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpRequireFile(MindcodeParser.ExpRequireFileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expMultiplication}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpMultiplication(MindcodeParser.ExpMultiplicationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expCaseExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpCaseExpression(MindcodeParser.ExpCaseExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expTernary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpTernary(MindcodeParser.ExpTernaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expNullLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpNullLiteral(MindcodeParser.ExpNullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expEqualityRelation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpEqualityRelation(MindcodeParser.ExpEqualityRelationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expCodeBlock}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expCallFunction}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpCallFunction(MindcodeParser.ExpCallFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expForRangeLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpForRangeLoop(MindcodeParser.ExpForRangeLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expForIteratedLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpForIteratedLoop(MindcodeParser.ExpForIteratedLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBitwiseOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBitwiseOr(MindcodeParser.ExpBitwiseOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expFloatLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpFloatLiteral(MindcodeParser.ExpFloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expLvalue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpLvalue(MindcodeParser.ExpLvalueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBitwiseAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBitwiseAnd(MindcodeParser.ExpBitwiseAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expDoWhileLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpDoWhileLoop(MindcodeParser.ExpDoWhileLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expCallMethod}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpCallMethod(MindcodeParser.ExpCallMethodContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expHexadecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBooleanLiteralFalse}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBooleanLiteralFalse(MindcodeParser.ExpBooleanLiteralFalseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expCompoundAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpCompoundAssignment(MindcodeParser.ExpCompoundAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expStringLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expCallEnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpCallEnd(MindcodeParser.ExpCallEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expIfExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpIfExpression(MindcodeParser.ExpIfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBooleanLiteralTrue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBooleanLiteralTrue(MindcodeParser.ExpBooleanLiteralTrueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expContinue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpContinue(MindcodeParser.ExpContinueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpAssignment(MindcodeParser.ExpAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expExponentiation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpExponentiation(MindcodeParser.ExpExponentiationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expWhileLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpWhileLoop(MindcodeParser.ExpWhileLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expDirective}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpDirective(MindcodeParser.ExpDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expPrefix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpPrefix(MindcodeParser.ExpPrefixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expParameter}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpParameter(MindcodeParser.ExpParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expUnary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpUnary(MindcodeParser.ExpUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expDecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expReturn}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpReturn(MindcodeParser.ExpReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expInequalityRelation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpInequalityRelation(MindcodeParser.ExpInequalityRelationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBreak}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBreak(MindcodeParser.ExpBreakContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expPostfix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpPostfix(MindcodeParser.ExpPostfixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expLogicalOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpLogicalOr(MindcodeParser.ExpLogicalOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expAllocations}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpAllocations(MindcodeParser.ExpAllocationsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expMemeberAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpMemeberAccess(MindcodeParser.ExpMemeberAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expForEachLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpForEachLoop(MindcodeParser.ExpForEachLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expDeclareFunction}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpDeclareFunction(MindcodeParser.ExpDeclareFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expAddition}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpAddition(MindcodeParser.ExpAdditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expPropertyAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpPropertyAccess(MindcodeParser.ExpPropertyAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expLogicalAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpLogicalAnd(MindcodeParser.ExpLogicalAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expParentheses}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpParentheses(MindcodeParser.ExpParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBitShift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBitShift(MindcodeParser.ExpBitShiftContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expConstant}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpConstant(MindcodeParser.ExpConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBinaryLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#allocations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAllocations(MindcodeParser.AllocationsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strHeapAllocation}
	 * labeled alternative in {@link MindcodeParser#allocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrHeapAllocation(MindcodeParser.StrHeapAllocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strStackAllocation}
	 * labeled alternative in {@link MindcodeParser#allocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrStackAllocation(MindcodeParser.StrStackAllocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveSet(MindcodeParser.DirectiveSetContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveValueList}
	 * labeled alternative in {@link MindcodeParser#directiveValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveValueList(MindcodeParser.DirectiveValueListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#directiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveValue(MindcodeParser.DirectiveValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(MindcodeParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#optionalArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptionalArgument(MindcodeParser.OptionalArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(MindcodeParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(MindcodeParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(MindcodeParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#caseAlternatives}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseAlternatives(MindcodeParser.CaseAlternativesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#caseAlternative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseAlternative(MindcodeParser.CaseAlternativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#whenValueList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenValueList(MindcodeParser.WhenValueListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strWhenExpression}
	 * labeled alternative in {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrWhenExpression(MindcodeParser.StrWhenExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strWhenRangeExpression}
	 * labeled alternative in {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrWhenRangeExpression(MindcodeParser.StrWhenRangeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#elsifBranches}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElsifBranches(MindcodeParser.ElsifBranchesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strElsifBranch}
	 * labeled alternative in {@link MindcodeParser#elsifBranch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrElsifBranch(MindcodeParser.StrElsifBranchContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(MindcodeParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#iteratorList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIteratorList(MindcodeParser.IteratorListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code strIterator}
	 * labeled alternative in {@link MindcodeParser#iterator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrIterator(MindcodeParser.StrIteratorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expIdentifierExt}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpIdentifierExt(MindcodeParser.ExpIdentifierExtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBuiltInIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBuiltInIdentifier(MindcodeParser.ExpBuiltInIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expArrayAccess}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpArrayAccess(MindcodeParser.ExpArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fmtText}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtText(MindcodeParser.FmtTextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fmtEscaped}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtEscaped(MindcodeParser.FmtEscapedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fmtInterpolation}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtInterpolation(MindcodeParser.FmtInterpolationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fmtPlaceholder}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtPlaceholder(MindcodeParser.FmtPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fmtPlaceholderEmpty}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtPlaceholderEmpty(MindcodeParser.FmtPlaceholderEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fmtPlaceholderVariable}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFmtPlaceholderVariable(MindcodeParser.FmtPlaceholderVariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#rangeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRangeExpression(MindcodeParser.RangeExpressionContext ctx);
}