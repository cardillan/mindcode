// Generated from MindcodeParser.g4 by ANTLR 4.13.1
package info.teksol.mc.mindcode.compiler.antlr;
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
	 * Visit a parse tree produced by {@link MindcodeParser#astModule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstModule(MindcodeParser.AstModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astStatementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstStatementList(MindcodeParser.AstStatementListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(MindcodeParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#identifierList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierList(MindcodeParser.IdentifierListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#mlogBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMlogBlock(MindcodeParser.MlogBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#mlogSeparators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMlogSeparators(MindcodeParser.MlogSeparatorsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogLabel}
	 * labeled alternative in {@link MindcodeParser#mlogStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogLabel(MindcodeParser.AstMlogLabelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogLabelWithComment}
	 * labeled alternative in {@link MindcodeParser#mlogStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogLabelWithComment(MindcodeParser.AstMlogLabelWithCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogInstruction}
	 * labeled alternative in {@link MindcodeParser#mlogStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogInstruction(MindcodeParser.AstMlogInstructionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogInstructionWithComment}
	 * labeled alternative in {@link MindcodeParser#mlogStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogInstructionWithComment(MindcodeParser.AstMlogInstructionWithCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogComment}
	 * labeled alternative in {@link MindcodeParser#mlogStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogComment(MindcodeParser.AstMlogCommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#mlogInstruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMlogInstruction(MindcodeParser.MlogInstructionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogToken}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogToken(MindcodeParser.AstMlogTokenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogBuiltin}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogBuiltin(MindcodeParser.AstMlogBuiltinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogString}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogString(MindcodeParser.AstMlogStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogColor}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogColor(MindcodeParser.AstMlogColorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogNamedColor}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogNamedColor(MindcodeParser.AstMlogNamedColorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogBinary}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogBinary(MindcodeParser.AstMlogBinaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogHexadecimal}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogHexadecimal(MindcodeParser.AstMlogHexadecimalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogDecimal}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogDecimal(MindcodeParser.AstMlogDecimalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogFloat}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogFloat(MindcodeParser.AstMlogFloatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogChar}
	 * labeled alternative in {@link MindcodeParser#mlogTokenOrLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogChar(MindcodeParser.AstMlogCharContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#mlogVariableList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMlogVariableList(MindcodeParser.MlogVariableListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astMlogVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogVariable(MindcodeParser.AstMlogVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astExpression}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstExpression(MindcodeParser.AstExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astDirective}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDirective(MindcodeParser.AstDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astVariableDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstVariableDeclaration(MindcodeParser.AstVariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astModuleDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstModuleDeclaration(MindcodeParser.AstModuleDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstEnhancedComment(MindcodeParser.AstEnhancedCommentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astAllocations}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstAllocations(MindcodeParser.AstAllocationsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astParameter}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstParameter(MindcodeParser.AstParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astRequireFile}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstRequireFile(MindcodeParser.AstRequireFileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstRequireLibrary(MindcodeParser.AstRequireLibraryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astFunctionDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstFunctionDeclaration(MindcodeParser.AstFunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astForEachLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstForEachLoopStatement(MindcodeParser.AstForEachLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astIteratedForLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstIteratedForLoopStatement(MindcodeParser.AstIteratedForLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astRangedForLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstRangedForLoopStatement(MindcodeParser.AstRangedForLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astWhileLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstWhileLoopStatement(MindcodeParser.AstWhileLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astDoWhileLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDoWhileLoopStatement(MindcodeParser.AstDoWhileLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astBreakStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstBreakStatement(MindcodeParser.AstBreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astContinueStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstContinueStatement(MindcodeParser.AstContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astReturnStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstReturnStatement(MindcodeParser.AstReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astCodeBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstCodeBlock(MindcodeParser.AstCodeBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astDebugBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDebugBlock(MindcodeParser.AstDebugBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMlogBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMlogBlock(MindcodeParser.AstMlogBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#declarationOrExpressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationOrExpressionList(MindcodeParser.DeclarationOrExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(MindcodeParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#declModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclModifier(MindcodeParser.DeclModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#typeSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSpec(MindcodeParser.TypeSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#variableSpecList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableSpecList(MindcodeParser.VariableSpecListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#variableSpecification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableSpecification(MindcodeParser.VariableSpecificationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#valueList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueList(MindcodeParser.ValueListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstIdentifier(MindcodeParser.AstIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astIdentifierExt}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstIdentifierExt(MindcodeParser.AstIdentifierExtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astBuiltInIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstBuiltInIdentifier(MindcodeParser.AstBuiltInIdentifierContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astArrayAccess}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstArrayAccess(MindcodeParser.AstArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astSubarray}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstSubarray(MindcodeParser.AstSubarrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astRemoteArray}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstRemoteArray(MindcodeParser.AstRemoteArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astRemoteSubarray}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstRemoteSubarray(MindcodeParser.AstRemoteSubarrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astKeyword}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstKeyword(MindcodeParser.AstKeywordContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralNamedColor}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralNamedColor(MindcodeParser.AstLiteralNamedColorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryOr(MindcodeParser.AstOperatorBinaryOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryAdd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryAdd(MindcodeParser.AstOperatorBinaryAddContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMethodCall}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMethodCall(MindcodeParser.AstMethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryExp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryExp(MindcodeParser.AstOperatorBinaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryEquality}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryEquality(MindcodeParser.AstOperatorBinaryEqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryAnd(MindcodeParser.AstOperatorBinaryAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorTernary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorTernary(MindcodeParser.AstOperatorTernaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorIncDecPostfix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorIncDecPostfix(MindcodeParser.AstOperatorIncDecPostfixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astIfExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstIfExpression(MindcodeParser.AstIfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralBoolean}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralBoolean(MindcodeParser.AstLiteralBooleanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralNull}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralNull(MindcodeParser.AstLiteralNullContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryInequality}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryInequality(MindcodeParser.AstOperatorBinaryInequalityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorUnary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorUnary(MindcodeParser.AstOperatorUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralBinary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralBinary(MindcodeParser.AstLiteralBinaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstFormattableLiteral(MindcodeParser.AstFormattableLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryBitwiseAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryBitwiseAnd(MindcodeParser.AstOperatorBinaryBitwiseAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryBitwiseOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryBitwiseOr(MindcodeParser.AstOperatorBinaryBitwiseOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astCaseExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstCaseExpression(MindcodeParser.AstCaseExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astPropertyAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstPropertyAccess(MindcodeParser.AstPropertyAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralFloat}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralFloat(MindcodeParser.AstLiteralFloatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astFunctionCallEnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstFunctionCallEnd(MindcodeParser.AstFunctionCallEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expLvalue}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpLvalue(MindcodeParser.ExpLvalueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralDecimal}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralDecimal(MindcodeParser.AstLiteralDecimalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryMul}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryMul(MindcodeParser.AstOperatorBinaryMulContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralChar}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralChar(MindcodeParser.AstLiteralCharContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorBinaryShift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorBinaryShift(MindcodeParser.AstOperatorBinaryShiftContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralHexadecimal}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralHexadecimal(MindcodeParser.AstLiteralHexadecimalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralString}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralString(MindcodeParser.AstLiteralStringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astFunctionCall}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstFunctionCall(MindcodeParser.AstFunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astMemberAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstMemberAccess(MindcodeParser.AstMemberAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astParentheses}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstParentheses(MindcodeParser.AstParenthesesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astLiteralColor}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstLiteralColor(MindcodeParser.AstLiteralColorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astOperatorIncDecPrefix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOperatorIncDecPrefix(MindcodeParser.AstOperatorIncDecPrefixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstAssignment(MindcodeParser.AstAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formattableText}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormattableText(MindcodeParser.FormattableTextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formattableEscaped}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormattableEscaped(MindcodeParser.FormattableEscapedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formattableInterpolation}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormattableInterpolation(MindcodeParser.FormattableInterpolationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code placeholder}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlaceholder(MindcodeParser.PlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formattablePlaceholderEmpty}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormattablePlaceholderEmpty(MindcodeParser.FormattablePlaceholderEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formattablePlaceholderVariable}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormattablePlaceholderVariable(MindcodeParser.FormattablePlaceholderVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astDirectiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDirectiveSet(MindcodeParser.AstDirectiveSetContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astDirectiveSetLocal}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDirectiveSetLocal(MindcodeParser.AstDirectiveSetLocalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code astDirectiveDeclare}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDirectiveDeclare(MindcodeParser.AstDirectiveDeclareContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#directiveValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveValues(MindcodeParser.DirectiveValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astDirectiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstDirectiveValue(MindcodeParser.AstDirectiveValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astKeywordOrBuiltin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstKeywordOrBuiltin(MindcodeParser.AstKeywordOrBuiltinContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astKeywordOrBuiltinList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstKeywordOrBuiltinList(MindcodeParser.AstKeywordOrBuiltinListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#allocations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAllocations(MindcodeParser.AllocationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astAllocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstAllocation(MindcodeParser.AstAllocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(MindcodeParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astFunctionParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstFunctionParameter(MindcodeParser.AstFunctionParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astFunctionArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstFunctionArgument(MindcodeParser.AstFunctionArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astOptionalFunctionArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstOptionalFunctionArgument(MindcodeParser.AstOptionalFunctionArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(MindcodeParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#caseAlternatives}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseAlternatives(MindcodeParser.CaseAlternativesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astCaseAlternative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstCaseAlternative(MindcodeParser.AstCaseAlternativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#whenValueList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenValueList(MindcodeParser.WhenValueListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenValue(MindcodeParser.WhenValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstRange(MindcodeParser.AstRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#elsifBranches}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElsifBranches(MindcodeParser.ElsifBranchesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#elsifBranch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElsifBranch(MindcodeParser.ElsifBranchContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#iteratorsValuesGroups}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIteratorsValuesGroups(MindcodeParser.IteratorsValuesGroupsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astIteratorsValuesGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstIteratorsValuesGroup(MindcodeParser.AstIteratorsValuesGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#iteratorGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIteratorGroup(MindcodeParser.IteratorGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#astIterator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAstIterator(MindcodeParser.AstIteratorContext ctx);
}