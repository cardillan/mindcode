// Generated from /Users/francois/Projects/mindcode/src/main/java/info/teksol/mindcode/grammar/Mindcode.g4 by ANTLR 4.9.1
package info.teksol.mindcode.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MindcodeParser}.
 */
public interface MindcodeListener extends ParseTreeListener {
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
	 * Enter a parse tree produced by {@link MindcodeParser#expression_list}.
	 * @param ctx the parse tree
	 */
	void enterExpression_list(MindcodeParser.Expression_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#expression_list}.
	 * @param ctx the parse tree
	 */
	void exitExpression_list(MindcodeParser.Expression_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(MindcodeParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(MindcodeParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#control_statement}.
	 * @param ctx the parse tree
	 */
	void enterControl_statement(MindcodeParser.Control_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#control_statement}.
	 * @param ctx the parse tree
	 */
	void exitControl_statement(MindcodeParser.Control_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_statement(MindcodeParser.While_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_statement(MindcodeParser.While_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#block_body}.
	 * @param ctx the parse tree
	 */
	void enterBlock_body(MindcodeParser.Block_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#block_body}.
	 * @param ctx the parse tree
	 */
	void exitBlock_body(MindcodeParser.Block_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#block_statement_list}.
	 * @param ctx the parse tree
	 */
	void enterBlock_statement_list(MindcodeParser.Block_statement_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#block_statement_list}.
	 * @param ctx the parse tree
	 */
	void exitBlock_statement_list(MindcodeParser.Block_statement_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterLvalue(MindcodeParser.LvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitLvalue(MindcodeParser.LvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#rvalue}.
	 * @param ctx the parse tree
	 */
	void enterRvalue(MindcodeParser.RvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#rvalue}.
	 * @param ctx the parse tree
	 */
	void exitRvalue(MindcodeParser.RvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#numeric}.
	 * @param ctx the parse tree
	 */
	void enterNumeric(MindcodeParser.NumericContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#numeric}.
	 * @param ctx the parse tree
	 */
	void exitNumeric(MindcodeParser.NumericContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#unary_minus}.
	 * @param ctx the parse tree
	 */
	void enterUnary_minus(MindcodeParser.Unary_minusContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#unary_minus}.
	 * @param ctx the parse tree
	 */
	void exitUnary_minus(MindcodeParser.Unary_minusContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#unit_ref}.
	 * @param ctx the parse tree
	 */
	void enterUnit_ref(MindcodeParser.Unit_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#unit_ref}.
	 * @param ctx the parse tree
	 */
	void exitUnit_ref(MindcodeParser.Unit_refContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#funcall}.
	 * @param ctx the parse tree
	 */
	void enterFuncall(MindcodeParser.FuncallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#funcall}.
	 * @param ctx the parse tree
	 */
	void exitFuncall(MindcodeParser.FuncallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#params_list}.
	 * @param ctx the parse tree
	 */
	void enterParams_list(MindcodeParser.Params_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#params_list}.
	 * @param ctx the parse tree
	 */
	void exitParams_list(MindcodeParser.Params_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#if_expression}.
	 * @param ctx the parse tree
	 */
	void enterIf_expression(MindcodeParser.If_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#if_expression}.
	 * @param ctx the parse tree
	 */
	void exitIf_expression(MindcodeParser.If_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#heap_ref}.
	 * @param ctx the parse tree
	 */
	void enterHeap_ref(MindcodeParser.Heap_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#heap_ref}.
	 * @param ctx the parse tree
	 */
	void exitHeap_ref(MindcodeParser.Heap_refContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#address}.
	 * @param ctx the parse tree
	 */
	void enterAddress(MindcodeParser.AddressContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#address}.
	 * @param ctx the parse tree
	 */
	void exitAddress(MindcodeParser.AddressContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#sensor_read}.
	 * @param ctx the parse tree
	 */
	void enterSensor_read(MindcodeParser.Sensor_readContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#sensor_read}.
	 * @param ctx the parse tree
	 */
	void exitSensor_read(MindcodeParser.Sensor_readContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(MindcodeParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(MindcodeParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(MindcodeParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(MindcodeParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#literal_t}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_t(MindcodeParser.Literal_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#literal_t}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_t(MindcodeParser.Literal_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#float_t}.
	 * @param ctx the parse tree
	 */
	void enterFloat_t(MindcodeParser.Float_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#float_t}.
	 * @param ctx the parse tree
	 */
	void exitFloat_t(MindcodeParser.Float_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#int_t}.
	 * @param ctx the parse tree
	 */
	void enterInt_t(MindcodeParser.Int_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#int_t}.
	 * @param ctx the parse tree
	 */
	void exitInt_t(MindcodeParser.Int_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 */
	void enterBool_t(MindcodeParser.Bool_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 */
	void exitBool_t(MindcodeParser.Bool_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#null_t}.
	 * @param ctx the parse tree
	 */
	void enterNull_t(MindcodeParser.Null_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#null_t}.
	 * @param ctx the parse tree
	 */
	void exitNull_t(MindcodeParser.Null_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#terminator}.
	 * @param ctx the parse tree
	 */
	void enterTerminator(MindcodeParser.TerminatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#terminator}.
	 * @param ctx the parse tree
	 */
	void exitTerminator(MindcodeParser.TerminatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#crlf}.
	 * @param ctx the parse tree
	 */
	void enterCrlf(MindcodeParser.CrlfContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#crlf}.
	 * @param ctx the parse tree
	 */
	void exitCrlf(MindcodeParser.CrlfContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(MindcodeParser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(MindcodeParser.ResourceContext ctx);
}