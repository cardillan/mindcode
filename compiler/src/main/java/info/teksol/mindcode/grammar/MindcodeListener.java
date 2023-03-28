// Generated from Mindcode.g4 by ANTLR 4.9.1
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
	 * Enter a parse tree produced by the {@code constant}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterConstant(MindcodeParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constant}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitConstant(MindcodeParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_equality_comparison}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_equality_comparison(MindcodeParser.Binop_equality_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_equality_comparison}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_equality_comparison(MindcodeParser.Binop_equality_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function_call}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call(MindcodeParser.Function_callContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function_call}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call(MindcodeParser.Function_callContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterWhile_loop(MindcodeParser.While_loopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitWhile_loop(MindcodeParser.While_loopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literal_bool}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_bool(MindcodeParser.Literal_boolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literal_bool}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_bool(MindcodeParser.Literal_boolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unary_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnary_minus(MindcodeParser.Unary_minusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unary_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnary_minus(MindcodeParser.Unary_minusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bitwise_not_expr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBitwise_not_expr(MindcodeParser.Bitwise_not_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bitwise_not_expr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBitwise_not_expr(MindcodeParser.Bitwise_not_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code property_access}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterProperty_access(MindcodeParser.Property_accessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code property_access}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitProperty_access(MindcodeParser.Property_accessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literal_string}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_string(MindcodeParser.Literal_stringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literal_string}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_string(MindcodeParser.Literal_stringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code indirect_prop_access}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIndirect_prop_access(MindcodeParser.Indirect_prop_accessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code indirect_prop_access}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIndirect_prop_access(MindcodeParser.Indirect_prop_accessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code not_expr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNot_expr(MindcodeParser.Not_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code not_expr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNot_expr(MindcodeParser.Not_exprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literal_null}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_null(MindcodeParser.Literal_nullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literal_null}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_null(MindcodeParser.Literal_nullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literal_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_minus(MindcodeParser.Literal_minusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literal_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_minus(MindcodeParser.Literal_minusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFor_loop(MindcodeParser.For_loopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFor_loop(MindcodeParser.For_loopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continue_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterContinue_exp(MindcodeParser.Continue_expContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continue_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitContinue_exp(MindcodeParser.Continue_expContext ctx);
	/**
	 * Enter a parse tree produced by the {@code value}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterValue(MindcodeParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code value}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitValue(MindcodeParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_bitwise_or}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_bitwise_or(MindcodeParser.Binop_bitwise_orContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_bitwise_or}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_bitwise_or(MindcodeParser.Binop_bitwise_orContext ctx);
	/**
	 * Enter a parse tree produced by the {@code function_declaration}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunction_declaration(MindcodeParser.Function_declarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code function_declaration}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunction_declaration(MindcodeParser.Function_declarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code do_while_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterDo_while_loop(MindcodeParser.Do_while_loopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code do_while_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitDo_while_loop(MindcodeParser.Do_while_loopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code allocation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAllocation(MindcodeParser.AllocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code allocation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAllocation(MindcodeParser.AllocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code if_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIf_expression(MindcodeParser.If_expressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code if_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIf_expression(MindcodeParser.If_expressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code print_format}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrint_format(MindcodeParser.Print_formatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code print_format}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrint_format(MindcodeParser.Print_formatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(MindcodeParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(MindcodeParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literal_numeric}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteral_numeric(MindcodeParser.Literal_numericContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literal_numeric}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteral_numeric(MindcodeParser.Literal_numericContext ctx);
	/**
	 * Enter a parse tree produced by the {@code case_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCase_expression(MindcodeParser.Case_expressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code case_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCase_expression(MindcodeParser.Case_expressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_plus_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_plus_minus(MindcodeParser.Binop_plus_minusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_plus_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_plus_minus(MindcodeParser.Binop_plus_minusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_mul_div_mod}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_mul_div_mod(MindcodeParser.Binop_mul_div_modContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_mul_div_mod}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_mul_div_mod(MindcodeParser.Binop_mul_div_modContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_inequality_comparison}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_inequality_comparison(MindcodeParser.Binop_inequality_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_inequality_comparison}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_inequality_comparison(MindcodeParser.Binop_inequality_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenthesized_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesized_expression(MindcodeParser.Parenthesized_expressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenthesized_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesized_expression(MindcodeParser.Parenthesized_expressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code return_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterReturn_exp(MindcodeParser.Return_expContext ctx);
	/**
	 * Exit a parse tree produced by the {@code return_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitReturn_exp(MindcodeParser.Return_expContext ctx);
	/**
	 * Enter a parse tree produced by the {@code break_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBreak_exp(MindcodeParser.Break_expContext ctx);
	/**
	 * Exit a parse tree produced by the {@code break_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBreak_exp(MindcodeParser.Break_expContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_exp(MindcodeParser.Binop_expContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_exp(MindcodeParser.Binop_expContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_shift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_shift(MindcodeParser.Binop_shiftContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_shift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_shift(MindcodeParser.Binop_shiftContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ternary_op}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTernary_op(MindcodeParser.Ternary_opContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ternary_op}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTernary_op(MindcodeParser.Ternary_opContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_and}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_and(MindcodeParser.Binop_andContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_and}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_and(MindcodeParser.Binop_andContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_or}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_or(MindcodeParser.Binop_orContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_or}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_or(MindcodeParser.Binop_orContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binop_bitwise_and}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinop_bitwise_and(MindcodeParser.Binop_bitwise_andContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binop_bitwise_and}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinop_bitwise_and(MindcodeParser.Binop_bitwise_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#indirectpropaccess}.
	 * @param ctx the parse tree
	 */
	void enterIndirectpropaccess(MindcodeParser.IndirectpropaccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#indirectpropaccess}.
	 * @param ctx the parse tree
	 */
	void exitIndirectpropaccess(MindcodeParser.IndirectpropaccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#propaccess}.
	 * @param ctx the parse tree
	 */
	void enterPropaccess(MindcodeParser.PropaccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#propaccess}.
	 * @param ctx the parse tree
	 */
	void exitPropaccess(MindcodeParser.PropaccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#numeric_t}.
	 * @param ctx the parse tree
	 */
	void enterNumeric_t(MindcodeParser.Numeric_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#numeric_t}.
	 * @param ctx the parse tree
	 */
	void exitNumeric_t(MindcodeParser.Numeric_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#alloc}.
	 * @param ctx the parse tree
	 */
	void enterAlloc(MindcodeParser.AllocContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#alloc}.
	 * @param ctx the parse tree
	 */
	void exitAlloc(MindcodeParser.AllocContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#alloc_list}.
	 * @param ctx the parse tree
	 */
	void enterAlloc_list(MindcodeParser.Alloc_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#alloc_list}.
	 * @param ctx the parse tree
	 */
	void exitAlloc_list(MindcodeParser.Alloc_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#alloc_range}.
	 * @param ctx the parse tree
	 */
	void enterAlloc_range(MindcodeParser.Alloc_rangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#alloc_range}.
	 * @param ctx the parse tree
	 */
	void exitAlloc_range(MindcodeParser.Alloc_rangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#const_decl}.
	 * @param ctx the parse tree
	 */
	void enterConst_decl(MindcodeParser.Const_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#const_decl}.
	 * @param ctx the parse tree
	 */
	void exitConst_decl(MindcodeParser.Const_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#fundecl}.
	 * @param ctx the parse tree
	 */
	void enterFundecl(MindcodeParser.FundeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#fundecl}.
	 * @param ctx the parse tree
	 */
	void exitFundecl(MindcodeParser.FundeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#arg_decl_list}.
	 * @param ctx the parse tree
	 */
	void enterArg_decl_list(MindcodeParser.Arg_decl_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#arg_decl_list}.
	 * @param ctx the parse tree
	 */
	void exitArg_decl_list(MindcodeParser.Arg_decl_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#while_expression}.
	 * @param ctx the parse tree
	 */
	void enterWhile_expression(MindcodeParser.While_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#while_expression}.
	 * @param ctx the parse tree
	 */
	void exitWhile_expression(MindcodeParser.While_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#do_while_expression}.
	 * @param ctx the parse tree
	 */
	void enterDo_while_expression(MindcodeParser.Do_while_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#do_while_expression}.
	 * @param ctx the parse tree
	 */
	void exitDo_while_expression(MindcodeParser.Do_while_expressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ranged_for}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 */
	void enterRanged_for(MindcodeParser.Ranged_forContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ranged_for}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 */
	void exitRanged_for(MindcodeParser.Ranged_forContext ctx);
	/**
	 * Enter a parse tree produced by the {@code iterated_for}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 */
	void enterIterated_for(MindcodeParser.Iterated_forContext ctx);
	/**
	 * Exit a parse tree produced by the {@code iterated_for}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 */
	void exitIterated_for(MindcodeParser.Iterated_forContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for_each}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 */
	void enterFor_each(MindcodeParser.For_eachContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for_each}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 */
	void exitFor_each(MindcodeParser.For_eachContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#loop_body}.
	 * @param ctx the parse tree
	 */
	void enterLoop_body(MindcodeParser.Loop_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#loop_body}.
	 * @param ctx the parse tree
	 */
	void exitLoop_body(MindcodeParser.Loop_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#loop_value_list}.
	 * @param ctx the parse tree
	 */
	void enterLoop_value_list(MindcodeParser.Loop_value_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#loop_value_list}.
	 * @param ctx the parse tree
	 */
	void exitLoop_value_list(MindcodeParser.Loop_value_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#continue_st}.
	 * @param ctx the parse tree
	 */
	void enterContinue_st(MindcodeParser.Continue_stContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#continue_st}.
	 * @param ctx the parse tree
	 */
	void exitContinue_st(MindcodeParser.Continue_stContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#break_st}.
	 * @param ctx the parse tree
	 */
	void enterBreak_st(MindcodeParser.Break_stContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#break_st}.
	 * @param ctx the parse tree
	 */
	void exitBreak_st(MindcodeParser.Break_stContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#return_st}.
	 * @param ctx the parse tree
	 */
	void enterReturn_st(MindcodeParser.Return_stContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#return_st}.
	 * @param ctx the parse tree
	 */
	void exitReturn_st(MindcodeParser.Return_stContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inclusive_range}
	 * labeled alternative in {@link MindcodeParser#range}.
	 * @param ctx the parse tree
	 */
	void enterInclusive_range(MindcodeParser.Inclusive_rangeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inclusive_range}
	 * labeled alternative in {@link MindcodeParser#range}.
	 * @param ctx the parse tree
	 */
	void exitInclusive_range(MindcodeParser.Inclusive_rangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exclusive_range}
	 * labeled alternative in {@link MindcodeParser#range}.
	 * @param ctx the parse tree
	 */
	void enterExclusive_range(MindcodeParser.Exclusive_rangeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exclusive_range}
	 * labeled alternative in {@link MindcodeParser#range}.
	 * @param ctx the parse tree
	 */
	void exitExclusive_range(MindcodeParser.Exclusive_rangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inclusive_range_exp}
	 * labeled alternative in {@link MindcodeParser#range_expression}.
	 * @param ctx the parse tree
	 */
	void enterInclusive_range_exp(MindcodeParser.Inclusive_range_expContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inclusive_range_exp}
	 * labeled alternative in {@link MindcodeParser#range_expression}.
	 * @param ctx the parse tree
	 */
	void exitInclusive_range_exp(MindcodeParser.Inclusive_range_expContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exclusive_range_exp}
	 * labeled alternative in {@link MindcodeParser#range_expression}.
	 * @param ctx the parse tree
	 */
	void enterExclusive_range_exp(MindcodeParser.Exclusive_range_expContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exclusive_range_exp}
	 * labeled alternative in {@link MindcodeParser#range_expression}.
	 * @param ctx the parse tree
	 */
	void exitExclusive_range_exp(MindcodeParser.Exclusive_range_expContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#init_list}.
	 * @param ctx the parse tree
	 */
	void enterInit_list(MindcodeParser.Init_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#init_list}.
	 * @param ctx the parse tree
	 */
	void exitInit_list(MindcodeParser.Init_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#incr_list}.
	 * @param ctx the parse tree
	 */
	void enterIncr_list(MindcodeParser.Incr_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#incr_list}.
	 * @param ctx the parse tree
	 */
	void exitIncr_list(MindcodeParser.Incr_listContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#printf}.
	 * @param ctx the parse tree
	 */
	void enterPrintf(MindcodeParser.PrintfContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#printf}.
	 * @param ctx the parse tree
	 */
	void exitPrintf(MindcodeParser.PrintfContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void enterArg_list(MindcodeParser.Arg_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void exitArg_list(MindcodeParser.Arg_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterArg(MindcodeParser.ArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitArg(MindcodeParser.ArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#if_expr}.
	 * @param ctx the parse tree
	 */
	void enterIf_expr(MindcodeParser.If_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#if_expr}.
	 * @param ctx the parse tree
	 */
	void exitIf_expr(MindcodeParser.If_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#if_trailer}.
	 * @param ctx the parse tree
	 */
	void enterIf_trailer(MindcodeParser.If_trailerContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#if_trailer}.
	 * @param ctx the parse tree
	 */
	void exitIf_trailer(MindcodeParser.If_trailerContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#case_expr}.
	 * @param ctx the parse tree
	 */
	void enterCase_expr(MindcodeParser.Case_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#case_expr}.
	 * @param ctx the parse tree
	 */
	void exitCase_expr(MindcodeParser.Case_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#alternative_list}.
	 * @param ctx the parse tree
	 */
	void enterAlternative_list(MindcodeParser.Alternative_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#alternative_list}.
	 * @param ctx the parse tree
	 */
	void exitAlternative_list(MindcodeParser.Alternative_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#alternative}.
	 * @param ctx the parse tree
	 */
	void enterAlternative(MindcodeParser.AlternativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#alternative}.
	 * @param ctx the parse tree
	 */
	void exitAlternative(MindcodeParser.AlternativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#when_expression}.
	 * @param ctx the parse tree
	 */
	void enterWhen_expression(MindcodeParser.When_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#when_expression}.
	 * @param ctx the parse tree
	 */
	void exitWhen_expression(MindcodeParser.When_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#when_value_list}.
	 * @param ctx the parse tree
	 */
	void enterWhen_value_list(MindcodeParser.When_value_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#when_value_list}.
	 * @param ctx the parse tree
	 */
	void exitWhen_value_list(MindcodeParser.When_value_listContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simple_assign}
	 * labeled alternative in {@link MindcodeParser#assign}.
	 * @param ctx the parse tree
	 */
	void enterSimple_assign(MindcodeParser.Simple_assignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simple_assign}
	 * labeled alternative in {@link MindcodeParser#assign}.
	 * @param ctx the parse tree
	 */
	void exitSimple_assign(MindcodeParser.Simple_assignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compound_assign}
	 * labeled alternative in {@link MindcodeParser#assign}.
	 * @param ctx the parse tree
	 */
	void enterCompound_assign(MindcodeParser.Compound_assignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compound_assign}
	 * labeled alternative in {@link MindcodeParser#assign}.
	 * @param ctx the parse tree
	 */
	void exitCompound_assign(MindcodeParser.Compound_assignContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#loop_label}.
	 * @param ctx the parse tree
	 */
	void enterLoop_label(MindcodeParser.Loop_labelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#loop_label}.
	 * @param ctx the parse tree
	 */
	void exitLoop_label(MindcodeParser.Loop_labelContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#global_ref}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_ref(MindcodeParser.Global_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#global_ref}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_ref(MindcodeParser.Global_refContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#var_ref}.
	 * @param ctx the parse tree
	 */
	void enterVar_ref(MindcodeParser.Var_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#var_ref}.
	 * @param ctx the parse tree
	 */
	void exitVar_ref(MindcodeParser.Var_refContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#ref}.
	 * @param ctx the parse tree
	 */
	void enterRef(MindcodeParser.RefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#ref}.
	 * @param ctx the parse tree
	 */
	void exitRef(MindcodeParser.RefContext ctx);
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
	 * Enter a parse tree produced by the {@code true_bool_literal}
	 * labeled alternative in {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 */
	void enterTrue_bool_literal(MindcodeParser.True_bool_literalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code true_bool_literal}
	 * labeled alternative in {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 */
	void exitTrue_bool_literal(MindcodeParser.True_bool_literalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code false_bool_literal}
	 * labeled alternative in {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 */
	void enterFalse_bool_literal(MindcodeParser.False_bool_literalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code false_bool_literal}
	 * labeled alternative in {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 */
	void exitFalse_bool_literal(MindcodeParser.False_bool_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#true_t}.
	 * @param ctx the parse tree
	 */
	void enterTrue_t(MindcodeParser.True_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#true_t}.
	 * @param ctx the parse tree
	 */
	void exitTrue_t(MindcodeParser.True_tContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#false_t}.
	 * @param ctx the parse tree
	 */
	void enterFalse_t(MindcodeParser.False_tContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#false_t}.
	 * @param ctx the parse tree
	 */
	void exitFalse_t(MindcodeParser.False_tContext ctx);
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
	 * Enter a parse tree produced by {@link MindcodeParser#decimal_int}.
	 * @param ctx the parse tree
	 */
	void enterDecimal_int(MindcodeParser.Decimal_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#decimal_int}.
	 * @param ctx the parse tree
	 */
	void exitDecimal_int(MindcodeParser.Decimal_intContext ctx);
	/**
	 * Enter a parse tree produced by {@link MindcodeParser#hex_int}.
	 * @param ctx the parse tree
	 */
	void enterHex_int(MindcodeParser.Hex_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link MindcodeParser#hex_int}.
	 * @param ctx the parse tree
	 */
	void exitHex_int(MindcodeParser.Hex_intContext ctx);
}