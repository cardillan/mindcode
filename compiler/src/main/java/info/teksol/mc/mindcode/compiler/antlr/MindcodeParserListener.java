// Generated from MindcodeParser.g4 by ANTLR 4.13.1
package info.teksol.mc.mindcode.compiler.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MindcodeParser}.
 */
public interface MindcodeParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#astModule}.
	 * @param ctx the parse tree
	 */
	void enterAstModule(MindcodeParser.AstModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astModule}.
	 * @param ctx the parse tree
	 */
	void exitAstModule(MindcodeParser.AstModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#astStatementList}.
	 * @param ctx the parse tree
	 */
	void enterAstStatementList(MindcodeParser.AstStatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astStatementList}.
	 * @param ctx the parse tree
	 */
	void exitAstStatementList(MindcodeParser.AstStatementListContext ctx);
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
	 * Enter a parse tree produced by the {@code astExpression}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstExpression(MindcodeParser.AstExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astExpression}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstExpression(MindcodeParser.AstExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astDirective}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstDirective(MindcodeParser.AstDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astDirective}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstDirective(MindcodeParser.AstDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astVariableDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstVariableDeclaration(MindcodeParser.AstVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astVariableDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstVariableDeclaration(MindcodeParser.AstVariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstEnhancedComment(MindcodeParser.AstEnhancedCommentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astEnhancedComment}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstEnhancedComment(MindcodeParser.AstEnhancedCommentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astAllocations}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstAllocations(MindcodeParser.AstAllocationsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astAllocations}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstAllocations(MindcodeParser.AstAllocationsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astConstant}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstConstant(MindcodeParser.AstConstantContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astConstant}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstConstant(MindcodeParser.AstConstantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astParameter}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstParameter(MindcodeParser.AstParameterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astParameter}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstParameter(MindcodeParser.AstParameterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astRequireFile}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstRequireFile(MindcodeParser.AstRequireFileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astRequireFile}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstRequireFile(MindcodeParser.AstRequireFileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstRequireLibrary(MindcodeParser.AstRequireLibraryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astRequireLibrary}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstRequireLibrary(MindcodeParser.AstRequireLibraryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astFunctionDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstFunctionDeclaration(MindcodeParser.AstFunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astFunctionDeclaration}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstFunctionDeclaration(MindcodeParser.AstFunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astForEachLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstForEachLoopStatement(MindcodeParser.AstForEachLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astForEachLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstForEachLoopStatement(MindcodeParser.AstForEachLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astIteratedForLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstIteratedForLoopStatement(MindcodeParser.AstIteratedForLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astIteratedForLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstIteratedForLoopStatement(MindcodeParser.AstIteratedForLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astRangedForLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstRangedForLoopStatement(MindcodeParser.AstRangedForLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astRangedForLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstRangedForLoopStatement(MindcodeParser.AstRangedForLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astWhileLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstWhileLoopStatement(MindcodeParser.AstWhileLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astWhileLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstWhileLoopStatement(MindcodeParser.AstWhileLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astDoWhileLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstDoWhileLoopStatement(MindcodeParser.AstDoWhileLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astDoWhileLoopStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstDoWhileLoopStatement(MindcodeParser.AstDoWhileLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astBreakStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstBreakStatement(MindcodeParser.AstBreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astBreakStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstBreakStatement(MindcodeParser.AstBreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astContinueStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstContinueStatement(MindcodeParser.AstContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astContinueStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstContinueStatement(MindcodeParser.AstContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astReturnStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstReturnStatement(MindcodeParser.AstReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astReturnStatement}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstReturnStatement(MindcodeParser.AstReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astCodeBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAstCodeBlock(MindcodeParser.AstCodeBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astCodeBlock}
	 * labeled alternative in {@link MindcodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAstCodeBlock(MindcodeParser.AstCodeBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#declarationOrExpressionList}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationOrExpressionList(MindcodeParser.DeclarationOrExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#declarationOrExpressionList}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationOrExpressionList(MindcodeParser.DeclarationOrExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(MindcodeParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(MindcodeParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#declModifier}.
	 * @param ctx the parse tree
	 */
	void enterDeclModifier(MindcodeParser.DeclModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#declModifier}.
	 * @param ctx the parse tree
	 */
	void exitDeclModifier(MindcodeParser.DeclModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#typeSpec}.
	 * @param ctx the parse tree
	 */
	void enterTypeSpec(MindcodeParser.TypeSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#typeSpec}.
	 * @param ctx the parse tree
	 */
	void exitTypeSpec(MindcodeParser.TypeSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#variableSpecList}.
	 * @param ctx the parse tree
	 */
	void enterVariableSpecList(MindcodeParser.VariableSpecListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#variableSpecList}.
	 * @param ctx the parse tree
	 */
	void exitVariableSpecList(MindcodeParser.VariableSpecListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#variableSpecification}.
	 * @param ctx the parse tree
	 */
	void enterVariableSpecification(MindcodeParser.VariableSpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#variableSpecification}.
	 * @param ctx the parse tree
	 */
	void exitVariableSpecification(MindcodeParser.VariableSpecificationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#valueList}.
	 * @param ctx the parse tree
	 */
	void enterValueList(MindcodeParser.ValueListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#valueList}.
	 * @param ctx the parse tree
	 */
	void exitValueList(MindcodeParser.ValueListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterAstIdentifier(MindcodeParser.AstIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitAstIdentifier(MindcodeParser.AstIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astIdentifierExt}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterAstIdentifierExt(MindcodeParser.AstIdentifierExtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astIdentifierExt}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitAstIdentifierExt(MindcodeParser.AstIdentifierExtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astBuiltInIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterAstBuiltInIdentifier(MindcodeParser.AstBuiltInIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astBuiltInIdentifier}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitAstBuiltInIdentifier(MindcodeParser.AstBuiltInIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astArrayAccess}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterAstArrayAccess(MindcodeParser.AstArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astArrayAccess}
	 * labeled alternative in {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitAstArrayAccess(MindcodeParser.AstArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryAdd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryAdd(MindcodeParser.AstOperatorBinaryAddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryAdd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryAdd(MindcodeParser.AstOperatorBinaryAddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astMethodCall}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstMethodCall(MindcodeParser.AstMethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astMethodCall}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstMethodCall(MindcodeParser.AstMethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryExp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryExp(MindcodeParser.AstOperatorBinaryExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryExp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryExp(MindcodeParser.AstOperatorBinaryExpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryEquality}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryEquality(MindcodeParser.AstOperatorBinaryEqualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryEquality}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryEquality(MindcodeParser.AstOperatorBinaryEqualityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorTernary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorTernary(MindcodeParser.AstOperatorTernaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorTernary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorTernary(MindcodeParser.AstOperatorTernaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorIncDecPostfix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorIncDecPostfix(MindcodeParser.AstOperatorIncDecPostfixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorIncDecPostfix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorIncDecPostfix(MindcodeParser.AstOperatorIncDecPostfixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astIfExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstIfExpression(MindcodeParser.AstIfExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astIfExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstIfExpression(MindcodeParser.AstIfExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralBoolean}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralBoolean(MindcodeParser.AstLiteralBooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralBoolean}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralBoolean(MindcodeParser.AstLiteralBooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralNull}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralNull(MindcodeParser.AstLiteralNullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralNull}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralNull(MindcodeParser.AstLiteralNullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryInequality}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryInequality(MindcodeParser.AstOperatorBinaryInequalityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryInequality}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryInequality(MindcodeParser.AstOperatorBinaryInequalityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorUnary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorUnary(MindcodeParser.AstOperatorUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorUnary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorUnary(MindcodeParser.AstOperatorUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralBinary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralBinary(MindcodeParser.AstLiteralBinaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralBinary}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralBinary(MindcodeParser.AstLiteralBinaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstFormattableLiteral(MindcodeParser.AstFormattableLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astFormattableLiteral}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstFormattableLiteral(MindcodeParser.AstFormattableLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryBitwiseAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryBitwiseAnd(MindcodeParser.AstOperatorBinaryBitwiseAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryBitwiseAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryBitwiseAnd(MindcodeParser.AstOperatorBinaryBitwiseAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryBitwiseOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryBitwiseOr(MindcodeParser.AstOperatorBinaryBitwiseOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryBitwiseOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryBitwiseOr(MindcodeParser.AstOperatorBinaryBitwiseOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astCaseExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstCaseExpression(MindcodeParser.AstCaseExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astCaseExpression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstCaseExpression(MindcodeParser.AstCaseExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astPropertyAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstPropertyAccess(MindcodeParser.AstPropertyAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astPropertyAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstPropertyAccess(MindcodeParser.AstPropertyAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralFloat}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralFloat(MindcodeParser.AstLiteralFloatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralFloat}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralFloat(MindcodeParser.AstLiteralFloatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astFunctionCallEnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstFunctionCallEnd(MindcodeParser.AstFunctionCallEndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astFunctionCallEnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstFunctionCallEnd(MindcodeParser.AstFunctionCallEndContext ctx);
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
	 * Enter a parse tree produced by the {@code astLiteralDecimal}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralDecimal(MindcodeParser.AstLiteralDecimalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralDecimal}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralDecimal(MindcodeParser.AstLiteralDecimalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryMul}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryMul(MindcodeParser.AstOperatorBinaryMulContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryMul}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryMul(MindcodeParser.AstOperatorBinaryMulContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryShift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryShift(MindcodeParser.AstOperatorBinaryShiftContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryShift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryShift(MindcodeParser.AstOperatorBinaryShiftContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralHexadecimal}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralHexadecimal(MindcodeParser.AstLiteralHexadecimalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralHexadecimal}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralHexadecimal(MindcodeParser.AstLiteralHexadecimalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralString}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralString(MindcodeParser.AstLiteralStringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralString}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralString(MindcodeParser.AstLiteralStringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astFunctionCall}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstFunctionCall(MindcodeParser.AstFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astFunctionCall}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstFunctionCall(MindcodeParser.AstFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astMemberAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstMemberAccess(MindcodeParser.AstMemberAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astMemberAccess}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstMemberAccess(MindcodeParser.AstMemberAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astParentheses}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstParentheses(MindcodeParser.AstParenthesesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astParentheses}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstParentheses(MindcodeParser.AstParenthesesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astLiteralColor}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstLiteralColor(MindcodeParser.AstLiteralColorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astLiteralColor}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstLiteralColor(MindcodeParser.AstLiteralColorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryLogicalOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryLogicalOr(MindcodeParser.AstOperatorBinaryLogicalOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryLogicalOr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryLogicalOr(MindcodeParser.AstOperatorBinaryLogicalOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorIncDecPrefix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorIncDecPrefix(MindcodeParser.AstOperatorIncDecPrefixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorIncDecPrefix}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorIncDecPrefix(MindcodeParser.AstOperatorIncDecPrefixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astOperatorBinaryLogicalAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstOperatorBinaryLogicalAnd(MindcodeParser.AstOperatorBinaryLogicalAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astOperatorBinaryLogicalAnd}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstOperatorBinaryLogicalAnd(MindcodeParser.AstOperatorBinaryLogicalAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAstAssignment(MindcodeParser.AstAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astAssignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAstAssignment(MindcodeParser.AstAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formattableText}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFormattableText(MindcodeParser.FormattableTextContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formattableText}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFormattableText(MindcodeParser.FormattableTextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formattableEscaped}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFormattableEscaped(MindcodeParser.FormattableEscapedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formattableEscaped}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFormattableEscaped(MindcodeParser.FormattableEscapedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formattableInterpolation}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterFormattableInterpolation(MindcodeParser.FormattableInterpolationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formattableInterpolation}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitFormattableInterpolation(MindcodeParser.FormattableInterpolationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code placeholder}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void enterPlaceholder(MindcodeParser.PlaceholderContext ctx);
	/**
	 * Exit a parse tree produced by the {@code placeholder}
	 * labeled alternative in {@link MindcodeParser#formattableContents}.
	 * @param ctx the parse tree
	 */
	void exitPlaceholder(MindcodeParser.PlaceholderContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formattablePlaceholderEmpty}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void enterFormattablePlaceholderEmpty(MindcodeParser.FormattablePlaceholderEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formattablePlaceholderEmpty}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void exitFormattablePlaceholderEmpty(MindcodeParser.FormattablePlaceholderEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formattablePlaceholderVariable}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void enterFormattablePlaceholderVariable(MindcodeParser.FormattablePlaceholderVariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formattablePlaceholderVariable}
	 * labeled alternative in {@link MindcodeParser#formattablePlaceholder}.
	 * @param ctx the parse tree
	 */
	void exitFormattablePlaceholderVariable(MindcodeParser.FormattablePlaceholderVariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code astDirectiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterAstDirectiveSet(MindcodeParser.AstDirectiveSetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code astDirectiveSet}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitAstDirectiveSet(MindcodeParser.AstDirectiveSetContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#astDirectiveValue}.
	 * @param ctx the parse tree
	 */
	void enterAstDirectiveValue(MindcodeParser.AstDirectiveValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astDirectiveValue}.
	 * @param ctx the parse tree
	 */
	void exitAstDirectiveValue(MindcodeParser.AstDirectiveValueContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#astAllocation}.
	 * @param ctx the parse tree
	 */
	void enterAstAllocation(MindcodeParser.AstAllocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astAllocation}.
	 * @param ctx the parse tree
	 */
	void exitAstAllocation(MindcodeParser.AstAllocationContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#astFunctionParameter}.
	 * @param ctx the parse tree
	 */
	void enterAstFunctionParameter(MindcodeParser.AstFunctionParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astFunctionParameter}.
	 * @param ctx the parse tree
	 */
	void exitAstFunctionParameter(MindcodeParser.AstFunctionParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#astFunctionArgument}.
	 * @param ctx the parse tree
	 */
	void enterAstFunctionArgument(MindcodeParser.AstFunctionArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astFunctionArgument}.
	 * @param ctx the parse tree
	 */
	void exitAstFunctionArgument(MindcodeParser.AstFunctionArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#astOptionalFunctionArgument}.
	 * @param ctx the parse tree
	 */
	void enterAstOptionalFunctionArgument(MindcodeParser.AstOptionalFunctionArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astOptionalFunctionArgument}.
	 * @param ctx the parse tree
	 */
	void exitAstOptionalFunctionArgument(MindcodeParser.AstOptionalFunctionArgumentContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#astCaseAlternative}.
	 * @param ctx the parse tree
	 */
	void enterAstCaseAlternative(MindcodeParser.AstCaseAlternativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astCaseAlternative}.
	 * @param ctx the parse tree
	 */
	void exitAstCaseAlternative(MindcodeParser.AstCaseAlternativeContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 */
	void enterWhenValue(MindcodeParser.WhenValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#whenValue}.
	 * @param ctx the parse tree
	 */
	void exitWhenValue(MindcodeParser.WhenValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#astRange}.
	 * @param ctx the parse tree
	 */
	void enterAstRange(MindcodeParser.AstRangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astRange}.
	 * @param ctx the parse tree
	 */
	void exitAstRange(MindcodeParser.AstRangeContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#loopIteratorList}.
	 * @param ctx the parse tree
	 */
	void enterLoopIteratorList(MindcodeParser.LoopIteratorListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#loopIteratorList}.
	 * @param ctx the parse tree
	 */
	void exitLoopIteratorList(MindcodeParser.LoopIteratorListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#astLoopIterator}.
	 * @param ctx the parse tree
	 */
	void enterAstLoopIterator(MindcodeParser.AstLoopIteratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#astLoopIterator}.
	 * @param ctx the parse tree
	 */
	void exitAstLoopIterator(MindcodeParser.AstLoopIteratorContext ctx);
}