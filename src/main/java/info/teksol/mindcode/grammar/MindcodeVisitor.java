// Generated from /Users/francois/Projects/mindcode/src/main/java/info/teksol/mindcode/grammar/Mindcode.g4 by ANTLR 4.9.1
package info.teksol.mindcode.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MindcodeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MindcodeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MindcodeParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#expression_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_list(MindcodeParser.Expression_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MindcodeParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#control_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitControl_statement(MindcodeParser.Control_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#while_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_statement(MindcodeParser.While_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#block_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_body(MindcodeParser.Block_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#block_statement_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_statement_list(MindcodeParser.Block_statement_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue(MindcodeParser.LvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#rvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRvalue(MindcodeParser.RvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#numeric}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric(MindcodeParser.NumericContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#unary_minus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_minus(MindcodeParser.Unary_minusContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#unit_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit_ref(MindcodeParser.Unit_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#funcall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncall(MindcodeParser.FuncallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#params_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams_list(MindcodeParser.Params_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#if_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_expression(MindcodeParser.If_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#heap_read}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeap_read(MindcodeParser.Heap_readContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#address}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddress(MindcodeParser.AddressContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#sensor_read}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSensor_read(MindcodeParser.Sensor_readContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(MindcodeParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(MindcodeParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#literal_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_t(MindcodeParser.Literal_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#float_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloat_t(MindcodeParser.Float_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#int_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_t(MindcodeParser.Int_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool_t(MindcodeParser.Bool_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#null_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNull_t(MindcodeParser.Null_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#terminator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminator(MindcodeParser.TerminatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#crlf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCrlf(MindcodeParser.CrlfContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#resource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource(MindcodeParser.ResourceContext ctx);
}