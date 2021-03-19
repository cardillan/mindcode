package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;
import info.teksol.mindcode.grammar.MindcodeBaseVisitor;
import info.teksol.mindcode.grammar.MindcodeParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstNodeBuilder extends MindcodeBaseVisitor<AstNode> {
    private int temp;
    private HeapAllocation allocatedHeap;
    private Map<String, Integer> heapAllocations = new HashMap<>();

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

    @Override
    public AstNode visitProgram(MindcodeParser.ProgramContext ctx) {
        final AstNode parent = super.visitProgram(ctx);
        if (parent == null) return new Seq(new NoOp());
        return parent;
    }

    @Override
    public AstNode visitExpression_list(MindcodeParser.Expression_listContext ctx) {
        if (ctx.expression_list() != null) {
            final AstNode rest = visit(ctx.expression_list());
            final AstNode expr = visit(ctx.expression());
            return new Seq(rest, expr);
        } else if (ctx.expression() != null) {
            final AstNode expr = visit(ctx.expression());
            if (expr instanceof Seq) return expr;
            return new Seq(expr);
        } else {
            return new NoOp();
        }
    }

    @Override
    public AstNode visitInt_t(MindcodeParser.Int_tContext ctx) {
        return new NumericLiteral(ctx.getText());
    }

    @Override
    public AstNode visitFloat_t(MindcodeParser.Float_tContext ctx) {
        return new NumericLiteral(ctx.getText());
    }

    @Override
    public AstNode visitLiteral_string(MindcodeParser.Literal_stringContext ctx) {
        final String str = ctx.getText();
        return new StringLiteral(str.substring(1, str.length() - 1).replaceAll("\\\\\"", "\""));
    }

    @Override
    public AstNode visitVar_ref(MindcodeParser.Var_refContext ctx) {
        return new VarRef(ctx.getText());
    }

    @Override
    public AstNode visitBinop_exp(MindcodeParser.Binop_expContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                "**",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_mul_div_mod(MindcodeParser.Binop_mul_div_modContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_plus_minus(MindcodeParser.Binop_plus_minusContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitNot_expr(MindcodeParser.Not_exprContext ctx) {
        return new UnaryOp("not", visit(ctx.expression()));
    }

    @Override
    public AstNode visitBinop_and(MindcodeParser.Binop_andContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                "and",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_or(MindcodeParser.Binop_orContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                "or",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_inequality_comparison(MindcodeParser.Binop_inequality_comparisonContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_equality_comparison(MindcodeParser.Binop_equality_comparisonContext ctx) {
        return new BinaryOp(
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitFunction_call(MindcodeParser.Function_callContext ctx) {
        if (ctx.funcall().END() != null) {
            // The end function is a bit special: because the keyword "end" is also used to
            // close blocks, the grammar needed to special-case the end function directly.
            return new FunctionCall("end");
        } else {
            final String name = ctx.funcall().name.getText();
            final List<AstNode> params = new ArrayList<>();

            if (ctx.funcall().params != null) {
                params.add(visit(ctx.funcall().params.arg()));
                MindcodeParser.Arg_listContext arglist = ctx.funcall().params.arg_list();
                while (arglist != null) {
                    params.add(visit(arglist.arg()));
                    arglist = arglist.arg_list();
                }
            }

            final List<AstNode> result = new ArrayList<>();
            for (int i = params.size() - 1; i >= 0; i--) {
                result.add(params.get(i));
            }

            return new FunctionCall(name, result);
        }
    }

    @Override
    public AstNode visitSimple_assign(MindcodeParser.Simple_assignContext ctx) {
        final AstNode lvalue = visit(ctx.target);
        final AstNode value = visit(ctx.value);
        return new Assignment(lvalue, value);
    }

    @Override
    public AstNode visitExp_assign(MindcodeParser.Exp_assignContext ctx) {
        final AstNode target = visit(ctx.target);
        return new Assignment(
                target,
                new BinaryOp(
                        target,
                        "**",
                        visit(ctx.value)
                )
        );
    }

    @Override
    public AstNode visitBinop_mul_div_assign(MindcodeParser.Binop_mul_div_assignContext ctx) {
        final AstNode target = visit(ctx.target);
        return new Assignment(
                target,
                new BinaryOp(
                        target,
                        ctx.op.getText().replace("=", ""),
                        visit(ctx.value)
                )
        );
    }

    @Override
    public AstNode visitBinop_plus_minus_assign(MindcodeParser.Binop_plus_minus_assignContext ctx) {
        final AstNode target = visit(ctx.target);
        return new Assignment(
                target,
                new BinaryOp(
                        target,
                        ctx.op.getText().replace("=", ""),
                        visit(ctx.value)
                )
        );
    }

    @Override
    public AstNode visitUnary_minus(MindcodeParser.Unary_minusContext ctx) {
        return new NumericLiteral(ctx.getText());
    }

    @Override
    public AstNode visitHeap_ref(MindcodeParser.Heap_refContext ctx) {
        return new HeapAccess(
                ctx.name.getText(),
                visit(ctx.address)
        );
    }

    @Override
    public AstNode visitAllocation(MindcodeParser.AllocationContext ctx) {
        MindcodeParser.Alloc_listContext alloc_list = ctx.alloc().alloc_list();
        while (alloc_list != null) {
            switch (alloc_list.type.getText()) {
                case "heap":
                    allocateHeap(alloc_list);
                    break;

                case "stack":
                    throw new RuntimeException("TODO");
            }

            alloc_list = alloc_list.alloc_list();
        }

        return new NoOp();
    }

    private void allocateHeap(MindcodeParser.Alloc_listContext ctx) {
        if (allocatedHeap != null) {
            throw new HeapAlreadyAllocatedException("Only one heap/stack can be allocated, found a second declaration in " + ctx.getText());
        }

        final String name = ctx.id().getText();
        final Range range;
        if (ctx.alloc_range() != null) {
            range = (Range) visit(ctx.alloc_range());
        } else {
            range = new ExclusiveRange(new NumericLiteral("0"), new NumericLiteral("64"));
        }

        allocatedHeap = new HeapAllocation(name, range);
    }

    @Override
    public AstNode visitInclusive_range(MindcodeParser.Inclusive_rangeContext ctx) {
        return new InclusiveRange(visit(ctx.start), visit(ctx.end));
    }

    @Override
    public AstNode visitExclusive_range(MindcodeParser.Exclusive_rangeContext ctx) {
        return new ExclusiveRange(visit(ctx.start), visit(ctx.end));
    }

    @Override
    public AstNode visitGlobal_ref(MindcodeParser.Global_refContext ctx) {
        if (allocatedHeap == null) {
            throw new UnallocatedHeapException("The heap must be allocated before using it in " + ctx.getText());
        }

        final String name = ctx.name.getText();
        final int location;
        if (heapAllocations.containsKey(name)) {
            location = heapAllocations.get(name);
        } else {
            if (heapAllocations.size() >= allocatedHeap.size()) {
                throw new OutOfHeapSpaceException("Allocated heap is too small! Increase the size of the allocation, or switch to a Memory Bank to give the heap even more space; in " + ctx.getText());
            }

            location = heapAllocations.size();
            heapAllocations.put(name, location);
        }

        return new HeapAccess(allocatedHeap.getName(), allocatedHeap.addressOf(location));
    }

    @Override
    public AstNode visitComment(MindcodeParser.CommentContext ctx) {
        return new Comment(ctx.getText().substring(2).replaceAll("\r?\n", ""));
    }

    @Override
    public AstNode visitUnit_ref(MindcodeParser.Unit_refContext ctx) {
        return new Ref(ctx.ref().getText());
    }

    @Override
    public AstNode visitPropaccess(MindcodeParser.PropaccessContext ctx) {
        final AstNode target;
        if (ctx.var_ref() != null) {
            target = visit(ctx.var_ref());
        } else if (ctx.unit_ref() != null) {
            target = visit(ctx.unit_ref());
        } else {
            throw new ParsingException("Expected var ref or unit ref in " + ctx.getText());
        }

        return new PropertyAccess(
                target,
                ctx.prop.getText()
        );
    }

    @Override
    public AstNode visitRanged_for(MindcodeParser.Ranged_forContext ctx) {
        final Range range = (Range) visit(ctx.range());
        final AstNode var = visit(ctx.lvalue());
        return new Seq(
                new Assignment(
                        var,
                        range.getFirstValue()
                ),
                new WhileStatement(
                        new BinaryOp(
                                var,
                                range instanceof InclusiveRange ? "<=" : "<",
                                range.getLastValue()
                        ),
                        new Seq(
                                visit(ctx.loop_body()),
                                new Assignment(
                                        var,
                                        new BinaryOp(
                                                var,
                                                "+",
                                                new NumericLiteral("1")
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public AstNode visitIterated_for(MindcodeParser.Iterated_forContext ctx) {
        return new Seq(
                visit(ctx.init),
                new WhileStatement(
                        visit(ctx.cond),
                        new Seq(
                                visit(ctx.loop_body()),
                                visit(ctx.increment)
                        )
                )
        );
    }

    @Override
    public AstNode visitIncr_list(MindcodeParser.Incr_listContext ctx) {
        if (ctx.incr_list() != null) {
            final AstNode down = visit(ctx.incr_list());
            return new Seq(down, visit(ctx.expression()));
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public AstNode visitInit_list(MindcodeParser.Init_listContext ctx) {
        if (ctx.init_list() != null) {
            final AstNode down = visit(ctx.init_list());
            return new Seq(down, visit(ctx.expression()));
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public AstNode visitWhile_expression(MindcodeParser.While_expressionContext ctx) {
        return new WhileStatement(visit(ctx.cond), visit(ctx.loop_body()));
    }

    @Override
    public AstNode visitLiteral_null(MindcodeParser.Literal_nullContext ctx) {
        return new NullLiteral();
    }

    @Override
    public AstNode visitIf_expression(MindcodeParser.If_expressionContext ctx) {
        final AstNode trailer;
        if (ctx.if_expr().if_trailer() != null) {
            trailer = visit(ctx.if_expr().if_trailer());
        } else {
            trailer = new NoOp();
        }

        return new IfExpression(
                visit(ctx.if_expr().cond),
                visit(ctx.if_expr().true_branch),
                trailer
        );
    }

    @Override
    public AstNode visitTrue_bool_literal(MindcodeParser.True_bool_literalContext ctx) {
        return new BooleanLiteral(true);
    }

    @Override
    public AstNode visitFalse_bool_literal(MindcodeParser.False_bool_literalContext ctx) {
        return new BooleanLiteral(false);
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

        return new Seq(
                new Assignment(new VarRef(tmp), visit(ctx.case_expr().cond)),
                new CaseExpression(
                        new VarRef(tmp),
                        alternatives,
                        elseBranch
                )
        );
    }

    private void gatherAlternatives(AstNode alternative, List<CaseAlternative> accumulator) {
        if (alternative instanceof CaseAlternative) {
            accumulator.add((CaseAlternative) alternative);
        } else if (alternative instanceof Seq) {
            final Seq seq = (Seq) alternative;
            gatherAlternatives(seq.getRest(), accumulator);
            gatherAlternatives(seq.getLast(), accumulator);
        }
    }

    @Override
    public AstNode visitAlternative_list(MindcodeParser.Alternative_listContext ctx) {
        if (ctx.alternative_list() != null) {
            return new Seq(visit(ctx.alternative_list()), visit(ctx.alternative()));
        } else {
            return visit(ctx.alternative());
        }
    }

    @Override
    public AstNode visitAlternative(MindcodeParser.AlternativeContext ctx) {
        return new CaseAlternative(visit(ctx.value), visit(ctx.body));
    }

    private String nextTemp() {
        return "ast" + temp++;
    }
}
