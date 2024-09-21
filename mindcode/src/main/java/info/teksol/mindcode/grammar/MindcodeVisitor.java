// Generated from Mindcode.g4 by ANTLR 4.13.1
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
	 * Visit a parse tree produced by the {@code constant}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(MindcodeParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_equality_comparison}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_equality_comparison(MindcodeParser.Binop_equality_comparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function_call}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(MindcodeParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_loop(MindcodeParser.While_loopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literal_bool}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_bool(MindcodeParser.Literal_boolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unary_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_minus(MindcodeParser.Unary_minusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bitwise_not_expr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwise_not_expr(MindcodeParser.Bitwise_not_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code property_access}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty_access(MindcodeParser.Property_accessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literal_string}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_string(MindcodeParser.Literal_stringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indirect_prop_access}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndirect_prop_access(MindcodeParser.Indirect_prop_accessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code not_expr}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot_expr(MindcodeParser.Not_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literal_null}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_null(MindcodeParser.Literal_nullContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compiler_directive}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompiler_directive(MindcodeParser.Compiler_directiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parameter}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(MindcodeParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literal_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_minus(MindcodeParser.Literal_minusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_loop(MindcodeParser.For_loopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continue_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinue_exp(MindcodeParser.Continue_expContext ctx);
	/**
	 * Visit a parse tree produced by the {@code value}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(MindcodeParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_bitwise_or}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_bitwise_or(MindcodeParser.Binop_bitwise_orContext ctx);
	/**
	 * Visit a parse tree produced by the {@code function_declaration}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_declaration(MindcodeParser.Function_declarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code do_while_loop}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDo_while_loop(MindcodeParser.Do_while_loopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code allocation}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAllocation(MindcodeParser.AllocationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code if_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_expression(MindcodeParser.If_expressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(MindcodeParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literal_numeric}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_numeric(MindcodeParser.Literal_numericContext ctx);
	/**
	 * Visit a parse tree produced by the {@code case_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase_expression(MindcodeParser.Case_expressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_plus_minus}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_plus_minus(MindcodeParser.Binop_plus_minusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_mul_div_mod}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_mul_div_mod(MindcodeParser.Binop_mul_div_modContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_inequality_comparison}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_inequality_comparison(MindcodeParser.Binop_inequality_comparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesized_expression}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesized_expression(MindcodeParser.Parenthesized_expressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code return_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_exp(MindcodeParser.Return_expContext ctx);
	/**
	 * Visit a parse tree produced by the {@code break_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak_exp(MindcodeParser.Break_expContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_exp}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_exp(MindcodeParser.Binop_expContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_shift}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_shift(MindcodeParser.Binop_shiftContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ternary_op}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernary_op(MindcodeParser.Ternary_opContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_and}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_and(MindcodeParser.Binop_andContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_or}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_or(MindcodeParser.Binop_orContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binop_bitwise_and}
	 * labeled alternative in {@link MindcodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop_bitwise_and(MindcodeParser.Binop_bitwise_andContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numeric_directive}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric_directive(MindcodeParser.Numeric_directiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code string_directive}
	 * labeled alternative in {@link MindcodeParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_directive(MindcodeParser.String_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#indirectpropaccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndirectpropaccess(MindcodeParser.IndirectpropaccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#propaccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropaccess(MindcodeParser.PropaccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#numeric_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric_t(MindcodeParser.Numeric_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc(MindcodeParser.AllocContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#alloc_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc_list(MindcodeParser.Alloc_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#alloc_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc_range(MindcodeParser.Alloc_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#const_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst_decl(MindcodeParser.Const_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#param_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam_decl(MindcodeParser.Param_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#fundecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFundecl(MindcodeParser.FundeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#arg_decl_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_decl_list(MindcodeParser.Arg_decl_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#while_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_expression(MindcodeParser.While_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#do_while_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDo_while_expression(MindcodeParser.Do_while_expressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ranged_for}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRanged_for(MindcodeParser.Ranged_forContext ctx);
	/**
	 * Visit a parse tree produced by the {@code iterated_for}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterated_for(MindcodeParser.Iterated_forContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for_each_1}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_each_1(MindcodeParser.For_each_1Context ctx);
	/**
	 * Visit a parse tree produced by the {@code for_each_2}
	 * labeled alternative in {@link MindcodeParser#for_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_each_2(MindcodeParser.For_each_2Context ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#iterator_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterator_list(MindcodeParser.Iterator_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#loop_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop_body(MindcodeParser.Loop_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#loop_value_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop_value_list(MindcodeParser.Loop_value_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#continue_st}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinue_st(MindcodeParser.Continue_stContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#break_st}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak_st(MindcodeParser.Break_stContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#return_st}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_st(MindcodeParser.Return_stContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inclusive_range_exp}
	 * labeled alternative in {@link MindcodeParser#range_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInclusive_range_exp(MindcodeParser.Inclusive_range_expContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exclusive_range_exp}
	 * labeled alternative in {@link MindcodeParser#range_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExclusive_range_exp(MindcodeParser.Exclusive_range_expContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#init_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInit_list(MindcodeParser.Init_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#incr_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncr_list(MindcodeParser.Incr_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#funcall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncall(MindcodeParser.FuncallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#arg_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_list(MindcodeParser.Arg_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg(MindcodeParser.ArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#if_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_expr(MindcodeParser.If_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#if_trailer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_trailer(MindcodeParser.If_trailerContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#case_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase_expr(MindcodeParser.Case_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#alternative_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlternative_list(MindcodeParser.Alternative_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#alternative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlternative(MindcodeParser.AlternativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#when_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhen_expression(MindcodeParser.When_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#when_value_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhen_value_list(MindcodeParser.When_value_listContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simple_assign}
	 * labeled alternative in {@link MindcodeParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_assign(MindcodeParser.Simple_assignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compound_assign}
	 * labeled alternative in {@link MindcodeParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompound_assign(MindcodeParser.Compound_assignContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue(MindcodeParser.LvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#loop_label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop_label(MindcodeParser.Loop_labelContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#heap_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeap_ref(MindcodeParser.Heap_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#global_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobal_ref(MindcodeParser.Global_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#unit_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit_ref(MindcodeParser.Unit_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#var_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_ref(MindcodeParser.Var_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRef(MindcodeParser.RefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#int_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_t(MindcodeParser.Int_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#float_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloat_t(MindcodeParser.Float_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#literal_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral_t(MindcodeParser.Literal_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#null_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNull_t(MindcodeParser.Null_tContext ctx);
	/**
	 * Visit a parse tree produced by the {@code true_bool_literal}
	 * labeled alternative in {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrue_bool_literal(MindcodeParser.True_bool_literalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code false_bool_literal}
	 * labeled alternative in {@link MindcodeParser#bool_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalse_bool_literal(MindcodeParser.False_bool_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#true_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrue_t(MindcodeParser.True_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#false_t}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalse_t(MindcodeParser.False_tContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(MindcodeParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#decimal_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimal_int(MindcodeParser.Decimal_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#hex_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHex_int(MindcodeParser.Hex_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link MindcodeParser#binary_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_int(MindcodeParser.Binary_intContext ctx);
}