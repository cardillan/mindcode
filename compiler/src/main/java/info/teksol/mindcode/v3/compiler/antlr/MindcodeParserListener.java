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
	 * Enter a parse tree produced by the {@code expIdentifier}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpIdentifier(MindcodeParser.ExpIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expIdentifier}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expMindustryIdentifier}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpMindustryIdentifier(MindcodeParser.ExpMindustryIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expMindustryIdentifier}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpMindustryIdentifier(MindcodeParser.ExpMindustryIdentifierContext ctx);
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
	 * Enter a parse tree produced by the {@code expFLoatLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpFLoatLiteral(MindcodeParser.ExpFLoatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expFLoatLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpFLoatLiteral(MindcodeParser.ExpFLoatLiteralContext ctx);
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