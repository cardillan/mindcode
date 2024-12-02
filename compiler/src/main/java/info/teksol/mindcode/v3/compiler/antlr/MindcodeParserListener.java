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
	 * Enter a parse tree produced by the {@code expRequireFile}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpRequireFile(MindcodeParser.ExpRequireFileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expRequireFile}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpRequireFile(MindcodeParser.ExpRequireFileContext ctx);
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
	 * Enter a parse tree produced by the {@code expRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpRequireLibrary(MindcodeParser.ExpRequireLibraryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expWhileLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpWhileLoop(MindcodeParser.ExpWhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expWhileLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpWhileLoop(MindcodeParser.ExpWhileLoopContext ctx);
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
	 * Enter a parse tree produced by the {@code expDirective}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpDirective(MindcodeParser.ExpDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expDirective}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpDirective(MindcodeParser.ExpDirectiveContext ctx);
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
	 * Enter a parse tree produced by the {@code expCodeBlock}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expCodeBlock}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpCodeBlock(MindcodeParser.ExpCodeBlockContext ctx);
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
	 * Enter a parse tree produced by the {@code expDoWhileLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpDoWhileLoop(MindcodeParser.ExpDoWhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expDoWhileLoop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpDoWhileLoop(MindcodeParser.ExpDoWhileLoopContext ctx);
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
	 * Enter a parse tree produced by the {@code directiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveSet(MindcodeParser.DirectiveSetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveSet(MindcodeParser.DirectiveSetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveValueList}
	 * labeled alternative in {@link MindcodeParser#directiveValues}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveValueList(MindcodeParser.DirectiveValueListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveValueList}
	 * labeled alternative in {@link MindcodeParser#directiveValues}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveValueList(MindcodeParser.DirectiveValueListContext ctx);
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
}