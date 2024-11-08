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
	 * Visit a parse tree produced by {@link MindcodeParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(MindcodeParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expDirective}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpDirective(MindcodeParser.ExpDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expIdentifier}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpIdentifier(MindcodeParser.ExpIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expMindustryIdentifier}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpMindustryIdentifier(MindcodeParser.ExpMindustryIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpEnhancedComment(MindcodeParser.ExpEnhancedCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpFormattableLiteral(MindcodeParser.ExpFormattableLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expStringLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpStringLiteral(MindcodeParser.ExpStringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expBinaryLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpBinaryLiteral(MindcodeParser.ExpBinaryLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expHexadecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpHexadecimalLiteral(MindcodeParser.ExpHexadecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expDecimalLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpDecimalLiteral(MindcodeParser.ExpDecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expFLoatLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpFLoatLiteral(MindcodeParser.ExpFLoatLiteralContext ctx);
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
}