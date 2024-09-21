package info.teksol.mindcode.ast;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.grammar.MindcodeBaseVisitor;
import info.teksol.mindcode.grammar.MindcodeParser;

import java.util.*;

public class AstNodeBuilder extends MindcodeBaseVisitor<AstNode> {
    public static final String AST_PREFIX = "__ast";
    private final Map<String, Integer> heapAllocations = new HashMap<>();
    private int temp;
    private HeapAllocation allocatedHeap;
    private StackAllocation allocatedStack;

    public static Seq generate(MindcodeParser.ProgramContext program) {
        final AstNodeBuilder builder = new AstNodeBuilder();
        final AstNode node = builder.visit(program);
        return (Seq) node;
    }

    @Override
    protected AstNode aggregateResult(AstNode aggregate, AstNode nextResult) {
        if (nextResult != null) return nextResult;
        return aggregate;
    }

    private void allocateHeap(MindcodeParser.Alloc_listContext ctx) {
        if (allocatedHeap != null) {
            throw new MindcodeException(ctx.getStart(), "multiple stack/heap allocations.");
        }

        final String name = ctx.id().getText();
        final Range range;
        if (ctx.alloc_range() != null) {
            range = (Range) visit(ctx.alloc_range());
        } else {
            range = new ExclusiveRange(ctx.getStart(),
                    new NumericLiteral(ctx.getStart(), "0"), new NumericLiteral(ctx.getStart(), "64"));
        }

        allocatedHeap = new HeapAllocation(ctx.getStart(), name, range);
    }

    private void allocateStack(MindcodeParser.Alloc_listContext ctx) {
        if (allocatedStack != null) {
            throw new MindcodeException(ctx.getStart(), "multiple stack declarations.");
        }

        final String name = ctx.id().getText();
        if (ctx.alloc_range() != null) {
            allocatedStack = new StackAllocation(ctx.getStart(), name, (Range) visit(ctx.alloc_range()));
        } else {
            allocatedStack = new StackAllocation(ctx.getStart(), name);
        }
    }

    private List<AstNode> createListOfIterators(MindcodeParser.Iterator_listContext values) {
        return values.iterator().stream()
                .map(this::visit)
                .toList();
    }

    private List<AstNode> createListOfValues(MindcodeParser.Value_listContext values) {
        return values.expression().stream()
                .map(this::visit)
                .toList();
    }

    private List<AstNode> createListOfValues(MindcodeParser.When_value_listContext values) {
        final List<AstNode> result = new ArrayList<>();

        if (values != null) {
            final AstNode nodes;
            if (values.when_value_list() != null) {
                nodes = visit(values.when_value_list());
            } else {
                nodes = new NoOp();
            }

            gatherValues(new Seq(values.getStart(), nodes, visit(values.when_expression())), result);
        }

        return result;
    }

    private void gatherAlternatives(AstNode alternative, List<CaseAlternative> accumulator) {
        if (alternative instanceof CaseAlternative caseAlternative) {
            accumulator.add(caseAlternative);
        } else if (alternative instanceof Seq seq) {
            gatherAlternatives(seq.getRest(), accumulator);
            gatherAlternatives(seq.getLast(), accumulator);
        }
    }

    private void gatherArgs(AstNode arg, List<AstNode> accumulator) {
        if (arg instanceof Seq seq) {
            gatherArgs(seq.getRest(), accumulator);
            gatherArgs(seq.getLast(), accumulator);
        } else if (arg instanceof NoOp) {
            // ignore
        } else {
            accumulator.add(arg);
        }
    }

    private void gatherValues(AstNode arg, List<AstNode> accumulator) {
        if (arg instanceof Seq seq) {
            gatherArgs(seq.getRest(), accumulator);
            gatherArgs(seq.getLast(), accumulator);
        } else if (arg instanceof NoOp) {
            // ignore
        } else {
            accumulator.add(arg);
        }
    }

    private String nextTemp() {
        return AST_PREFIX + temp++;
    }

    @Override
    public AstNode visitAllocation(MindcodeParser.AllocationContext ctx) {
        MindcodeParser.Alloc_listContext alloc_list = ctx.alloc().alloc_list();
        while (alloc_list != null) {
            switch (alloc_list.type.getText()) {
                case "heap"  -> allocateHeap(alloc_list);
                case "stack" -> allocateStack(alloc_list);
            }

            alloc_list = alloc_list.alloc_list();
        }

        return new Seq(ctx.getStart(),
                Objects.requireNonNullElseGet(allocatedHeap, NoOp::new),
                Objects.requireNonNullElseGet(allocatedStack, NoOp::new)
        );
    }

    @Override
    public AstNode visitAlternative(MindcodeParser.AlternativeContext ctx) {
        return new CaseAlternative(ctx.getStart(),
                createListOfValues(ctx.values),
                ctx.body == null ? new Seq(ctx.getStart(), new NoOp()) : visit(ctx.body));
    }

    @Override
    public AstNode visitAlternative_list(MindcodeParser.Alternative_listContext ctx) {
        if (ctx.alternative_list() != null) {
            return new Seq(ctx.getStart(), visit(ctx.alternative_list()), visit(ctx.alternative()));
        } else {
            return visit(ctx.alternative());
        }
    }

    @Override
    public AstNode visitArg_decl_list(MindcodeParser.Arg_decl_listContext ctx) {
        final AstNode rest;
        if (ctx.arg_decl_list() != null) {
            rest = visit(ctx.arg_decl_list());
        } else {
            rest = new NoOp();
        }

        final AstNode last = visit(ctx.var_ref());
        return new Seq(ctx.getStart(), rest, last);
    }

    @Override
    public AstNode visitArg_list(MindcodeParser.Arg_listContext ctx) {
        if (ctx.arg_list() != null) {
            return new Seq(ctx.getStart(), visit(ctx.arg_list()), visit(ctx.arg()));
        } else {
            final AstNode last = visit(ctx.arg());
            return new Seq(ctx.getStart(), last);
        }
    }

    @Override
    public AstNode visitBinop_and(MindcodeParser.Binop_andContext ctx) {
        return ctx.op.getText().equals("and")
                ? new BoolBinaryOp(ctx.getStart(), visit(ctx.left), "and", visit(ctx.right))
                : new BinaryOp(ctx.getStart(), visit(ctx.left), "and", visit(ctx.right));
    }

    @Override
    public AstNode visitBinop_bitwise_and(MindcodeParser.Binop_bitwise_andContext ctx) {
        return new BinaryOp(ctx.getStart(),
                visit(ctx.left),
                "&",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_bitwise_or(MindcodeParser.Binop_bitwise_orContext ctx) {
        return new BinaryOp(ctx.getStart(), visit(ctx.left), ctx.op.getText(), visit(ctx.right));
    }

    @Override
    public AstNode visitBinop_equality_comparison(MindcodeParser.Binop_equality_comparisonContext ctx) {
        if (ctx.op.getText().equals("!==")) {
            return new BinaryOp(ctx.getStart(),
                    new BinaryOp(ctx.getStart(),
                            visit(ctx.left),
                            "===",
                            visit(ctx.right)
                    ),
                    "==",
                    new BooleanLiteral(ctx.getStart(), false)
            );
        } else {
            return new BinaryOp(ctx.getStart(),
                    visit(ctx.left),
                    ctx.op.getText(),
                    visit(ctx.right)
            );
        }
    }

    @Override
    public AstNode visitBinop_exp(MindcodeParser.Binop_expContext ctx) {
        return new BinaryOp(ctx.getStart(),
                visit(ctx.left),
                "**",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_inequality_comparison(MindcodeParser.Binop_inequality_comparisonContext ctx) {
        return new BinaryOp(ctx.getStart(),
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_mul_div_mod(MindcodeParser.Binop_mul_div_modContext ctx) {
        return new BinaryOp(ctx.getStart(),
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_or(MindcodeParser.Binop_orContext ctx) {
        return ctx.op.getText().equals("or")
                ? new BoolBinaryOp(ctx.getStart(), visit(ctx.left), "or", visit(ctx.right))
                : new BinaryOp(ctx.getStart(), visit(ctx.left), "or", visit(ctx.right));
    }

    @Override
    public AstNode visitBinop_plus_minus(MindcodeParser.Binop_plus_minusContext ctx) {
        return new BinaryOp(ctx.getStart(),
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_shift(MindcodeParser.Binop_shiftContext ctx) {
        return new BinaryOp(ctx.getStart(), visit(ctx.left), ctx.op.getText(), visit(ctx.right));
    }

    @Override
    public AstNode visitBitwise_not_expr(MindcodeParser.Bitwise_not_exprContext ctx) {
        return new UnaryOp(ctx.getStart(), "~", visit(ctx.expression()));
    }

    @Override
    public AstNode visitBreak_exp(MindcodeParser.Break_expContext ctx) {
        String label = ctx.break_st().label == null ? null : ctx.break_st().label.getText();
        return new BreakStatement(ctx.getStart(), label);
    }

    @Override
    public AstNode visitCase_expression(MindcodeParser.Case_expressionContext ctx) {
        final String tmp = nextTemp();
        final AstNode elseBranch;
        if (ctx.case_expr().else_branch != null) {
            elseBranch = visit(ctx.case_expr().else_branch);
        } else {
            elseBranch = new NoOp();
        }

        final List<CaseAlternative> alternatives = new ArrayList<>();
        gatherAlternatives(visit(ctx.case_expr().alternative_list()), alternatives);

        return new Seq(ctx.getStart(),
                new Assignment(ctx.getStart(),
                        new VarRef(ctx.getStart(), tmp),
                        visit(ctx.case_expr().cond)),
                new CaseExpression(ctx.getStart(),
                        new VarRef(ctx.getStart(), tmp),
                        alternatives,
                        elseBranch
                )
        );
    }

    @Override
    public AstNode visitCompound_assign(MindcodeParser.Compound_assignContext ctx) {
        final AstNode target = visit(ctx.target);
        return new Assignment(ctx.getStart(),
                target,
                new BinaryOp(ctx.getStart(),
                        target,
                        ctx.op.getText().replace("=", ""),
                        visit(ctx.value)
                )
        );
    }

    @Override
    public AstNode visitConst_decl(MindcodeParser.Const_declContext ctx) {
        final String name = ctx.name.getText();
        final AstNode value = visit(ctx.value);
        return new Constant(ctx.getStart(), name, value);
    }

    @Override
    public AstNode visitContinue_exp(MindcodeParser.Continue_expContext ctx) {
        String label = ctx.continue_st().label == null ? null : ctx.continue_st().label.getText();
        return new ContinueStatement(ctx.getStart(), label);
    }

    @Override
    public AstNode visitDo_while_expression(MindcodeParser.Do_while_expressionContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new DoWhileExpression(ctx.getStart(), label, visit(ctx.loop_body()), visit(ctx.cond));
    }

    @Override
    public AstNode visitExclusive_range_exp(MindcodeParser.Exclusive_range_expContext ctx) {
        return new ExclusiveRange(ctx.getStart(),visit(ctx.start), visit(ctx.end));
    }

    @Override
    public AstNode visitExpression_list(MindcodeParser.Expression_listContext ctx) {
        if (ctx.expression_list() != null) {
            final AstNode rest = visit(ctx.expression_list());
            final AstNode expr = visit(ctx.expression());
            return new Seq(ctx.getStart(), rest, expr);
        } else if (ctx.expression() != null) {
            final AstNode expr = visit(ctx.expression());
            if (expr instanceof Seq) return expr;
            return new Seq(ctx.getStart(), expr);
        } else {
            return new NoOp();
        }
    }

    @Override
    public AstNode visitFalse_bool_literal(MindcodeParser.False_bool_literalContext ctx) {
        return new BooleanLiteral(ctx.getStart(), false);
    }

    @Override
    public AstNode visitFloat_t(MindcodeParser.Float_tContext ctx) {
        return new NumericLiteral(ctx.getStart(), ctx.getText());
    }

    @Override
    public AstNode visitFor_each_1(MindcodeParser.For_each_1Context ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new ForEachExpression(ctx.getStart(), label,
                createListOfIterators(ctx.iterators),
                createListOfValues(ctx.values),
                visit(ctx.loop_body()));
    }

    @Override
    public AstNode visitFor_each_2(MindcodeParser.For_each_2Context ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new ForEachExpression(ctx.getStart(), label,
                createListOfIterators(ctx.iterators),
                createListOfValues(ctx.values),
                visit(ctx.loop_body()));
    }

    @Override
    public AstNode visitFunction_call(MindcodeParser.Function_callContext ctx) {
        final List<AstNode> params = new ArrayList<>();

        if (ctx.funcall().params != null) {
            final AstNode nodes;
            if (ctx.funcall().params.arg_list() != null) {
                nodes = visit(ctx.funcall().params.arg_list());
            } else {
                nodes = new NoOp();
            }

            gatherArgs(new Seq(ctx.getStart(), nodes, visit(ctx.funcall().params.arg())), params);
        }

        if (ctx.funcall().END() != null) {
            // The end function is a bit special: because the keyword "end" is also used to
            // close blocks, the grammar needed to special-case the end function directly.
            return new FunctionCall(ctx.getStart(), "end");
        } else if (ctx.funcall().obj != null) {
            final AstNode target;
            if (ctx.funcall().obj.unit_ref() != null) {
                target = visit(ctx.funcall().obj.unit_ref());
            } else if (ctx.funcall().obj.var_ref() != null) {
                target = visit(ctx.funcall().obj.var_ref());
            } else {
                throw new MindcodeInternalError("Failed to parse function call on a property access at " + ctx.getText());
            }

            return new Control(ctx.getStart(),
                    target,
                    ctx.funcall().obj.prop.getText(),
                    params
            );
        } else {
            final String name = ctx.funcall().name.getText();
            return new FunctionCall(ctx.getStart(), name, params);
        }
    }

    @Override
    public AstNode visitFunction_declaration(MindcodeParser.Function_declarationContext ctx) {
        final AstNode args;
        if (ctx.fundecl().args == null) {
            args = new NoOp();
        } else {
            args = visit(ctx.fundecl().args);
        }

        final List<AstNode> params = new ArrayList<>();
        gatherArgs(args, params);
        String strInline = ctx.fundecl().inline == null ? null : ctx.fundecl().inline.getText();

        return new FunctionDeclaration(ctx.getStart(),
                "inline".equals(strInline),
                "noinline".equals(strInline),
                ctx.fundecl().name.getText(),
                params.stream().map(VarRef.class::cast).toList(),
                visit(ctx.fundecl().body)
        );
    }

    @Override
    public AstNode visitGlobal_ref(MindcodeParser.Global_refContext ctx) {
        if (allocatedHeap == null) {
            throw new MindcodeException(ctx.getStart(), "the heap must be allocated before using it.");
        }

        final String name = ctx.name.getText();
        final int location;
        if (heapAllocations.containsKey(name)) {
            location = heapAllocations.get(name);
        } else {
            location = heapAllocations.size();
            heapAllocations.put(name, location);
        }

        return new HeapAccess(ctx.getStart(), allocatedHeap.getName(), location);
    }

    @Override
    public AstNode visitHeap_ref(MindcodeParser.Heap_refContext ctx) {
        return new HeapAccess(ctx.getStart(),
                ctx.name.getText(),
                visit(ctx.address)
        );
    }

    @Override
    public AstNode visitIf_expression(MindcodeParser.If_expressionContext ctx) {
        final AstNode trailer;
        if (ctx.if_expr().if_trailer() != null) {
            trailer = visit(ctx.if_expr().if_trailer());
        } else {
            trailer = new NoOp();
        }

        return new IfExpression(ctx.getStart(),
                visit(ctx.if_expr().cond),
                ctx.if_expr().true_branch == null ? new Seq(ctx.getStart(), new NoOp()) : visit(ctx.if_expr().true_branch),
                trailer
        );
    }

    @Override
    public AstNode visitIf_trailer(MindcodeParser.If_trailerContext ctx) {
        if (ctx.ELSIF() != null) {
            final AstNode trailer;
            if (ctx.if_trailer() != null) {
                trailer = visit(ctx.if_trailer());
            } else {
                trailer = new NoOp();
            }

            return new IfExpression(ctx.getStart(),
                    visit(ctx.cond),
                    ctx.true_branch == null ? new Seq(ctx.getStart(), new NoOp()) : visit(ctx.true_branch),
                    trailer);
        } else if (ctx.ELSE() != null) {
            return ctx.false_branch == null ? new Seq(ctx.getStart(), new NoOp()) : visit(ctx.false_branch);
        } else {
            throw new MindcodeInternalError("Unhandled if/elsif/else; neither ELSIF nor ELSE were true in " + ctx.getText());
        }
    }

    @Override
    public AstNode visitInclusive_range_exp(MindcodeParser.Inclusive_range_expContext ctx) {
        return new InclusiveRange(ctx.getStart(),visit(ctx.start), visit(ctx.end));
    }

    @Override
    public AstNode visitIncr_list(MindcodeParser.Incr_listContext ctx) {
        if (ctx.incr_list() != null) {
            final AstNode down = visit(ctx.incr_list());
            return new Seq(ctx.getStart(), down, visit(ctx.expression()));
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public AstNode visitIndirectpropaccess(MindcodeParser.IndirectpropaccessContext ctx) {
        return new PropertyAccess(ctx.getStart(), visit(ctx.target), visit(ctx.expr));
    }

    @Override
    public AstNode visitInit_list(MindcodeParser.Init_listContext ctx) {
        if (ctx.init_list() != null) {
            final AstNode down = visit(ctx.init_list());
            return new Seq(ctx.getStart(), down, visit(ctx.expression()));
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public AstNode visitInt_t(MindcodeParser.Int_tContext ctx) {
        return new NumericLiteral(ctx.getStart(), ctx.getText());
    }

    @Override
    public AstNode visitIterated_for(MindcodeParser.Iterated_forContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new WhileExpression(ctx.getStart(),
                label,
                visit(ctx.init),
                visit(ctx.cond),
                visit(ctx.loop_body()),
                visit(ctx.increment)
        );
    }

    @Override
    public AstNode visitIterator(MindcodeParser.IteratorContext ctx) {
        // At this point, no other modifier is allowed by the syntax
        boolean outModifier = ctx.modifier != null;
        final VarRef varRef = (VarRef) visit(ctx.lvalue());
        return new Iterator(ctx.getStart(), true, outModifier, varRef);
    }

    @Override
    public AstNode visitLiteral_minus(MindcodeParser.Literal_minusContext ctx) {
        return new NumericLiteral(ctx.getStart(), ctx.getText());
    }

    @Override
    public AstNode visitLiteral_null(MindcodeParser.Literal_nullContext ctx) {
        return new NullLiteral(ctx.getStart());
    }

    @Override
    public AstNode visitLiteral_string(MindcodeParser.Literal_stringContext ctx) {
        final String str = ctx.getText();
        return new StringLiteral(ctx.getStart(), str.substring(1, str.length() - 1).replaceAll("\\\\\"", "\""));
    }

    @Override
    public AstNode visitNot_expr(MindcodeParser.Not_exprContext ctx) {
        return new BinaryOp(ctx.getStart(),
                visit(ctx.expression()),
                "==",
                new BooleanLiteral(ctx.getStart(), false));
    }

    @Override
    public AstNode visitNumeric_directive(MindcodeParser.Numeric_directiveContext ctx) {
        return new Directive(ctx.getStart(), ctx.option.getText(), ctx.value.getText());
    }

    @Override
    public AstNode visitParam_decl(MindcodeParser.Param_declContext ctx) {
        final String name = ctx.name.getText();
        final AstNode value = visit(ctx.value);
        return new Parameter(ctx.getStart(), name, value);
    }

    @Override
    public AstNode visitProgram(MindcodeParser.ProgramContext ctx) {
        final AstNode parent = super.visitProgram(ctx);
        if (parent == null) return new Seq(ctx.getStart(), new NoOp());
        return parent;
    }

    @Override
    public AstNode visitPropaccess(MindcodeParser.PropaccessContext ctx) {
        final AstNode target;
        if (ctx.var_ref() != null) {
            target = visit(ctx.var_ref());
        } else if (ctx.unit_ref() != null) {
            target = visit(ctx.unit_ref());
        } else {
            throw new MindcodeInternalError("Expected var ref or unit ref in " + ctx.getText());
        }

        return new PropertyAccess(ctx.getStart(), target, new Ref(ctx.getStart(), ctx.prop.getText()));
    }

    @Override
    public AstNode visitRanged_for(MindcodeParser.Ranged_forContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        final AstNode var = visit(ctx.lvalue());
        final Range range = (Range) visit(ctx.range_expression());
        final AstNode body = visit(ctx.loop_body());
        return new RangedForExpression(ctx.getStart(), label, var, range, body);
    }

    @Override
    public AstNode visitReturn_exp(MindcodeParser.Return_expContext ctx) {
        AstNode retval = ctx.return_st().retval == null ? new NullLiteral(ctx.getStart()) : visit(ctx.return_st().retval);
        return new ReturnStatement(ctx.getStart(), retval);
    }

    @Override
    public AstNode visitSimple_assign(MindcodeParser.Simple_assignContext ctx) {
        final AstNode lvalue = visit(ctx.target);
        final AstNode value = visit(ctx.value);
        return new Assignment(ctx.getStart(),lvalue, value);
    }

    @Override
    public AstNode visitString_directive(MindcodeParser.String_directiveContext ctx) {
        return new Directive(ctx.getStart(), ctx.option.getText(), ctx.value.getText());
    }

    @Override
    public AstNode visitTernary_op(MindcodeParser.Ternary_opContext ctx) {
        return new IfExpression(ctx.getStart(),
                visit(ctx.cond),
                visit(ctx.true_branch),
                visit(ctx.false_branch)
        );
    }

    @Override
    public AstNode visitTrue_bool_literal(MindcodeParser.True_bool_literalContext ctx) {
        return new BooleanLiteral(ctx.getStart(), true);
    }

    @Override
    public AstNode visitUnary_minus(MindcodeParser.Unary_minusContext ctx) {
        return new BinaryOp(ctx.getStart(),
                new NumericLiteral(ctx.getStart(), "-1"),
                "*",
                visit(ctx.expression()));
    }

    @Override
    public AstNode visitUnit_ref(MindcodeParser.Unit_refContext ctx) {
        return new Ref(ctx.getStart(), ctx.ref().getText());
    }

    @Override
    public AstNode visitVar_ref(MindcodeParser.Var_refContext ctx) {
        return new VarRef(ctx.getStart(),  ctx.getText());
    }

    @Override
    public AstNode visitWhen_value_list(MindcodeParser.When_value_listContext ctx) {
        if (ctx.when_value_list()!= null) {
            return new Seq(ctx.getStart(), visit(ctx.when_value_list()), visit(ctx.when_expression()));
        } else {
            final AstNode last = visit(ctx.when_expression());
            return new Seq(ctx.getStart(), last);
        }
    }

    @Override
    public AstNode visitWhile_expression(MindcodeParser.While_expressionContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new WhileExpression(ctx.getStart(),label, new NoOp(),
                visit(ctx.cond), visit(ctx.loop_body()), new NoOp());
    }
}
