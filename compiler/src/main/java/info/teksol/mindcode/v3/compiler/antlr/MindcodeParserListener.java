// Generated from MindcodeParser.g4 by ANTLR 4.13.1
package info.teksol.mindcode.v3.compiler.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MindcodeParser}.
 */
public interface MindcodeParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MindcodeParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MindcodeParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(MindcodeParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(MindcodeParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(MindcodeParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(MindcodeParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expExpression}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpExpression(MindcodeParser.ExpExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expExpression}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpExpression(MindcodeParser.ExpExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expDirective}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpDirective(MindcodeParser.ExpDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expDirective}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpDirective(MindcodeParser.ExpDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expRequireFile}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpRequireFile(MindcodeParser.ExpRequireFileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expRequireFile}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpRequireFile(MindcodeParser.ExpRequireFileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expAllocations}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpAllocations(MindcodeParser.ExpAllocationsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expAllocations}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpAllocations(MindcodeParser.ExpAllocationsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expDeclareFunction}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpDeclareFunction(MindcodeParser.ExpDeclareFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expDeclareFunction}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpDeclareFunction(MindcodeParser.ExpDeclareFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expParameter}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpParameter(MindcodeParser.ExpParameterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expParameter}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpParameter(MindcodeParser.ExpParameterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expConstant}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpConstant(MindcodeParser.ExpConstantContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expConstant}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpConstant(MindcodeParser.ExpConstantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expCodeBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCodeBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expForEachLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpForEachLoop(MindcodeParser.ExpForEachLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expForEachLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpForEachLoop(MindcodeParser.ExpForEachLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expForIteratedLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpForIteratedLoop(MindcodeParser.ExpForIteratedLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expForIteratedLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpForIteratedLoop(MindcodeParser.ExpForIteratedLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expForRangeLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpForRangeLoop(MindcodeParser.ExpForRangeLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expForRangeLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpForRangeLoop(MindcodeParser.ExpForRangeLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expWhileLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpWhileLoop(MindcodeParser.ExpWhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expWhileLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpWhileLoop(MindcodeParser.ExpWhileLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expDoWhileLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpDoWhileLoop(MindcodeParser.ExpDoWhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expDoWhileLoop}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpDoWhileLoop(MindcodeParser.ExpDoWhileLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBreak}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpBreak(MindcodeParser.ExpBreakContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBreak}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpBreak(MindcodeParser.ExpBreakContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expContinue}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpContinue(MindcodeParser.ExpContinueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expContinue}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpContinue(MindcodeParser.ExpContinueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expReturn}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpReturn(MindcodeParser.ExpReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expReturn}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpReturn(MindcodeParser.ExpReturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterExpIdentifier(MindcodeParser.ExpIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expIdentifierExt}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterExpIdentifierExt(MindcodeParser.ExpIdentifierExtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expIdentifierExt}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitExpIdentifierExt(MindcodeParser.ExpIdentifierExtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBuiltInIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterExpBuiltInIdentifier(MindcodeParser.ExpBuiltInIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBuiltInIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitExpBuiltInIdentifier(MindcodeParser.ExpBuiltInIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expArrayAccess}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterExpArrayAccess(MindcodeParser.ExpArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expArrayAccess}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitExpArrayAccess(MindcodeParser.ExpArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBooleanLiteralTrue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpBooleanLiteralTrue(MindcodeParser.ExpBooleanLiteralTrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBooleanLiteralTrue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpBooleanLiteralTrue(MindcodeParser.ExpBooleanLiteralTrueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expMultiplication}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpMultiplication(MindcodeParser.ExpMultiplicationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expMultiplication}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpMultiplication(MindcodeParser.ExpMultiplicationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpAssignment(MindcodeParser.ExpAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpAssignment(MindcodeParser.ExpAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expExponentiation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpExponentiation(MindcodeParser.ExpExponentiationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expExponentiation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpExponentiation(MindcodeParser.ExpExponentiationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expCaseExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpCaseExpression(MindcodeParser.ExpCaseExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCaseExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpCaseExpression(MindcodeParser.ExpCaseExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expTernary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpTernary(MindcodeParser.ExpTernaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expTernary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpTernary(MindcodeParser.ExpTernaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expPrefix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpPrefix(MindcodeParser.ExpPrefixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expPrefix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpPrefix(MindcodeParser.ExpPrefixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expUnary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpUnary(MindcodeParser.ExpUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expUnary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpUnary(MindcodeParser.ExpUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expDecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expDecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expNullLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpNullLiteral(MindcodeParser.ExpNullLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expNullLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpNullLiteral(MindcodeParser.ExpNullLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expInequalityRelation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpInequalityRelation(MindcodeParser.ExpInequalityRelationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expInequalityRelation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpInequalityRelation(MindcodeParser.ExpInequalityRelationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expEqualityRelation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpEqualityRelation(MindcodeParser.ExpEqualityRelationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expEqualityRelation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpEqualityRelation(MindcodeParser.ExpEqualityRelationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expCallFunction}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpCallFunction(MindcodeParser.ExpCallFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCallFunction}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpCallFunction(MindcodeParser.ExpCallFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBitwiseOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpBitwiseOr(MindcodeParser.ExpBitwiseOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBitwiseOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpBitwiseOr(MindcodeParser.ExpBitwiseOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expFloatLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpFloatLiteral(MindcodeParser.ExpFloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expFloatLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpFloatLiteral(MindcodeParser.ExpFloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expPostfix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpPostfix(MindcodeParser.ExpPostfixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expPostfix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpPostfix(MindcodeParser.ExpPostfixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expLvalue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpLvalue(MindcodeParser.ExpLvalueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expLvalue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpLvalue(MindcodeParser.ExpLvalueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expLogicalOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpLogicalOr(MindcodeParser.ExpLogicalOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expLogicalOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpLogicalOr(MindcodeParser.ExpLogicalOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expMemeberAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpMemeberAccess(MindcodeParser.ExpMemeberAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expMemeberAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpMemeberAccess(MindcodeParser.ExpMemeberAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBitwiseAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpBitwiseAnd(MindcodeParser.ExpBitwiseAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBitwiseAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpBitwiseAnd(MindcodeParser.ExpBitwiseAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expCallMethod}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpCallMethod(MindcodeParser.ExpCallMethodContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCallMethod}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpCallMethod(MindcodeParser.ExpCallMethodContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expHexadecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expHexadecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBooleanLiteralFalse}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpBooleanLiteralFalse(MindcodeParser.ExpBooleanLiteralFalseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBooleanLiteralFalse}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpBooleanLiteralFalse(MindcodeParser.ExpBooleanLiteralFalseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expAddition}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpAddition(MindcodeParser.ExpAdditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expAddition}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpAddition(MindcodeParser.ExpAdditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expPropertyAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpPropertyAccess(MindcodeParser.ExpPropertyAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expPropertyAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpPropertyAccess(MindcodeParser.ExpPropertyAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expCompoundAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpCompoundAssignment(MindcodeParser.ExpCompoundAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCompoundAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpCompoundAssignment(MindcodeParser.ExpCompoundAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expLogicalAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpLogicalAnd(MindcodeParser.ExpLogicalAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expLogicalAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpLogicalAnd(MindcodeParser.ExpLogicalAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expParentheses}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpParentheses(MindcodeParser.ExpParenthesesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expParentheses}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpParentheses(MindcodeParser.ExpParenthesesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expStringLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expStringLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBitShift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpBitShift(MindcodeParser.ExpBitShiftContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBitShift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpBitShift(MindcodeParser.ExpBitShiftContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expCallEnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpCallEnd(MindcodeParser.ExpCallEndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCallEnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpCallEnd(MindcodeParser.ExpCallEndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expIfExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpIfExpression(MindcodeParser.ExpIfExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expIfExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpIfExpression(MindcodeParser.ExpIfExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expBinaryLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expBinaryLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fmtText}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFmtText(MindcodeParser.FmtTextContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fmtText}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFmtText(MindcodeParser.FmtTextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fmtEscaped}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFmtEscaped(MindcodeParser.FmtEscapedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fmtEscaped}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFmtEscaped(MindcodeParser.FmtEscapedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fmtInterpolation}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFmtInterpolation(MindcodeParser.FmtInterpolationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fmtInterpolation}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFmtInterpolation(MindcodeParser.FmtInterpolationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fmtPlaceholder}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFmtPlaceholder(MindcodeParser.FmtPlaceholderContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fmtPlaceholder}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFmtPlaceholder(MindcodeParser.FmtPlaceholderContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fmtPlaceholderEmpty}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void enterFmtPlaceholderEmpty(MindcodeParser.FmtPlaceholderEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fmtPlaceholderEmpty}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void exitFmtPlaceholderEmpty(MindcodeParser.FmtPlaceholderEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fmtPlaceholderVariable}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void enterFmtPlaceholderVariable(MindcodeParser.FmtPlaceholderVariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fmtPlaceholderVariable}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void exitFmtPlaceholderVariable(MindcodeParser.FmtPlaceholderVariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtDirectiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterStmtDirectiveSet(MindcodeParser.StmtDirectiveSetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtDirectiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitStmtDirectiveSet(MindcodeParser.StmtDirectiveSetContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#directiveValues}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveValues(MindcodeParser.DirectiveValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#directiveValues}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveValues(MindcodeParser.DirectiveValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#directiveValue}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveValue(MindcodeParser.DirectiveValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#directiveValue}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveValue(MindcodeParser.DirectiveValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#allocations}.
	 * @param ctx the parse tree
	 */
	void enterAllocations(MindcodeParser.AllocationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#allocations}.
	 * @param ctx the parse tree
	 */
	void exitAllocations(MindcodeParser.AllocationsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtHeapAllocation}
	 * labeled alternative in {@link MindcodeParser#allocation}.
	 * @param ctx the parse tree
	 */
	void enterStmtHeapAllocation(MindcodeParser.StmtHeapAllocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtHeapAllocation}
	 * labeled alternative in {@link MindcodeParser#allocation}.
	 * @param ctx the parse tree
	 */
	void exitStmtHeapAllocation(MindcodeParser.StmtHeapAllocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtStackAllocation}
	 * labeled alternative in {@link MindcodeParser#allocation}.
	 * @param ctx the parse tree
	 */
	void enterStmtStackAllocation(MindcodeParser.StmtStackAllocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtStackAllocation}
	 * labeled alternative in {@link MindcodeParser#allocation}.
	 * @param ctx the parse tree
	 */
	void exitStmtStackAllocation(MindcodeParser.StmtStackAllocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(MindcodeParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(MindcodeParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MindcodeParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MindcodeParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(MindcodeParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(MindcodeParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#optionalArgument}.
	 * @param ctx the parse tree
	 */
	void enterOptionalArgument(MindcodeParser.OptionalArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#optionalArgument}.
	 * @param ctx the parse tree
	 */
	void exitOptionalArgument(MindcodeParser.OptionalArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(MindcodeParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(MindcodeParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#caseAlternatives}.
	 * @param ctx the parse tree
	 */
	void enterCaseAlternatives(MindcodeParser.CaseAlternativesContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#caseAlternatives}.
	 * @param ctx the parse tree
	 */
	void exitCaseAlternatives(MindcodeParser.CaseAlternativesContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#caseAlternative}.
	 * @param ctx the parse tree
	 */
	void enterCaseAlternative(MindcodeParser.CaseAlternativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#caseAlternative}.
	 * @param ctx the parse tree
	 */
	void exitCaseAlternative(MindcodeParser.CaseAlternativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#whenValueList}.
	 * @param ctx the parse tree
	 */
	void enterWhenValueList(MindcodeParser.WhenValueListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#whenValueList}.
	 * @param ctx the parse tree
	 */
	void exitWhenValueList(MindcodeParser.WhenValueListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whenValueExpression}
	 * labeled alternative in {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 */
	void enterWhenValueExpression(MindcodeParser.WhenValueExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whenValueExpression}
	 * labeled alternative in {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 */
	void exitWhenValueExpression(MindcodeParser.WhenValueExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whenValueRangeExpression}
	 * labeled alternative in {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 */
	void enterWhenValueRangeExpression(MindcodeParser.WhenValueRangeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whenValueRangeExpression}
	 * labeled alternative in {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 */
	void exitWhenValueRangeExpression(MindcodeParser.WhenValueRangeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#rangeExpression}.
	 * @param ctx the parse tree
	 */
	void enterRangeExpression(MindcodeParser.RangeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#rangeExpression}.
	 * @param ctx the parse tree
	 */
	void exitRangeExpression(MindcodeParser.RangeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#elsifBranches}.
	 * @param ctx the parse tree
	 */
	void enterElsifBranches(MindcodeParser.ElsifBranchesContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#elsifBranches}.
	 * @param ctx the parse tree
	 */
	void exitElsifBranches(MindcodeParser.ElsifBranchesContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#elsifBranch}.
	 * @param ctx the parse tree
	 */
	void enterElsifBranch(MindcodeParser.ElsifBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#elsifBranch}.
	 * @param ctx the parse tree
	 */
	void exitElsifBranch(MindcodeParser.ElsifBranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#iteratorList}.
	 * @param ctx the parse tree
	 */
	void enterIteratorList(MindcodeParser.IteratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#iteratorList}.
	 * @param ctx the parse tree
	 */
	void exitIteratorList(MindcodeParser.IteratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#iterator}.
	 * @param ctx the parse tree
	 */
	void enterIterator(MindcodeParser.IteratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#iterator}.
	 * @param ctx the parse tree
	 */
	void exitIterator(MindcodeParser.IteratorContext ctx);
}