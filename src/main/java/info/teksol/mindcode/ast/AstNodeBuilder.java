package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;
import info.teksol.mindcode.grammar.MindcodeBaseVisitor;
import info.teksol.mindcode.grammar.MindcodeParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AstNodeBuilder extends MindcodeBaseVisitor<AstNode> {
    private Optional<HeapAllocation> allocatedHeap = Optional.empty();
    private Map<String, Integer> heapAllocations = new HashMap<>();

    public static Seq generate(MindcodeParser.ProgramContext program) {
        final AstNodeBuilder builder = new AstNodeBuilder();
        final AstNode node = builder.visit(program);
        return (Seq) node;
    }

    @Override
    public AstNode visitExpression_list(MindcodeParser.Expression_listContext ctx) {
        if (ctx.expression_list() != null) {
            return new Seq(visit(ctx.expression_list()), visit(ctx.expression()));
        } else if (ctx.expression() != null) {
            return visit(ctx.expression());
        } else {
            return new NoOp();
        }
    }

    @Override
    public AstNode visitWhile_statement(MindcodeParser.While_statementContext ctx) {
        return new WhileStatement(
                visitRvalue(ctx.rvalue()),
                visitBlock_body(ctx.block_body())
        );
    }

    @Override
    public AstNode visitBlock_statement_list(MindcodeParser.Block_statement_listContext ctx) {
        if (ctx.block_statement_list() != null) {
            return new Seq(
                    visitBlock_statement_list(ctx.block_statement_list()),
                    visitExpression(ctx.expression())
            );
        } else if (ctx.expression() != null) {
            return visitExpression(ctx.expression());
        } else {
            throw new ParsingException("Failed to understand block statement list in " + ctx.getText());
        }
    }

    @Override
    public AstNode visitSingle_line_comment(MindcodeParser.Single_line_commentContext ctx) {
        final String text = ctx.getText();
        return new Comment(text.substring(2));
    }

    @Override
    public AstNode visitControl_statement(MindcodeParser.Control_statementContext ctx) {
        return new Control(ctx.target.getText(), ctx.property.getText(), visitRvalue(ctx.value));
    }

    @Override
    public AstNode visitFuncall(MindcodeParser.FuncallContext ctx) {
        final List<AstNode> params = new ArrayList<>();
        visitParams_list(ctx.params_list(), params);

        return new FunctionCall(
                ctx.name.getText(),
                params
        );
    }

    private void visitParams_list(MindcodeParser.Params_listContext ctx, List<AstNode> params) {
        if (ctx.rvalue() != null) {
            params.add(visitRvalue(ctx.rvalue()));
        }

        if (ctx.params_list() != null) {
            visitParams_list(ctx.params_list(), params);
        }
    }

    @Override
    public AstNode visitHeap_allocation(MindcodeParser.Heap_allocationContext ctx) {
        if (allocatedHeap.isPresent()) {
            throw new ParsingException("Only a single heap may be allocated per script, found 2nd declaration in " + ctx.getText());
        }

        final HeapAllocation heapAllocation = new HeapAllocation(
                ((VarRef) visit(ctx.id())).getName(),
                (Range) visit(ctx.range()));
        allocatedHeap = Optional.of(heapAllocation);
        return new NoOp();
    }

    @Override
    public AstNode visitGlobalvar(MindcodeParser.GlobalvarContext ctx) {
        if (!allocatedHeap.isPresent()) {
            throw new UnallocatedHeapException("Found reference to global variable without a heap allocation. Please allocate a heap at the top of your script. Found " + ctx.getText());
        }

        final String name = ctx.global().name.getText();
        final int location;
        if (heapAllocations.containsKey(name)) {
            location = heapAllocations.get(name);
        } else {
            if (heapAllocations.size() >= allocatedHeap.get().size()) {
                throw new OutOfHeapSpaceException("increase your heap size or reduce the number of global variables used: " + ctx.getText());
            }

            location = heapAllocations.size();
            heapAllocations.put(name, location);
        }

        return new HeapRead(
                allocatedHeap.get().getName(),
                new NumericLiteral(String.valueOf(allocatedHeap.get().getFirst() + location)));
    }

    @Override
    public AstNode visitId(MindcodeParser.IdContext ctx) {
        return new VarRef(ctx.getText());
    }

    @Override
    public AstNode visitAssignment(MindcodeParser.AssignmentContext ctx) {
        if (ctx.target != null) {
            final AstNode target = visit(ctx.target);
            final AstNode value;
            if (ctx.op != null) {
                value = new BinaryOp(
                        visit(ctx.lvalue()),
                        ctx.op.getText().replace("=", ""),
                        visit(ctx.rvalue())
                );
            } else {
                value = visit(ctx.rvalue());
            }

            if (target instanceof VarRef) {
                // +=, -=, etc...
                return new VarAssignment(
                        ((VarRef) target).getName(),
                        value
                );
            } else if (target instanceof HeapRead) {
                return new HeapWrite(
                        ((HeapRead) target).getCellName(),
                        ((HeapRead) target).getAddress(),
                        value
                );
            } else {
                throw new ParsingException("parsing failed during assignment at " + ctx.getText());
            }
        }

        if (ctx.heap_ref() != null) {
            final AstNode lvalue = visit(ctx.heap_ref());
            final AstNode rvalue = visit(ctx.value);
            if (ctx.op != null) {
                // +=, -=, etc...
                return new HeapWrite(
                        ctx.heap_ref().target.getText(),
                        visit(ctx.heap_ref().address()),
                        new BinaryOp(
                                lvalue,
                                ctx.op.getText().replace("=", ""),
                                rvalue
                        )
                );
            } else {
                // simple assignment
                return new HeapWrite(
                        ctx.heap_ref().target.getText(),
                        visit(ctx.heap_ref().address()),
                        rvalue
                );
            }
        }

        throw new ParsingException("Expected lvalue or heap_ref in " + ctx.getText());
    }

    @Override
    protected AstNode aggregateResult(AstNode aggregate, AstNode nextResult) {
        if (nextResult != null) return nextResult;
        return aggregate;
    }

    @Override
    public AstNode visitHeap_ref(MindcodeParser.Heap_refContext ctx) {
        return new HeapRead(ctx.target.getText(), visit(ctx.addr));
    }

    @Override
    public AstNode visitLiteral_t(MindcodeParser.Literal_tContext ctx) {
        final String str = ctx.LITERAL().getSymbol().getText();
        return new StringLiteral(str.substring(1, str.length() - 1).replaceAll("\\\\\"", "\""));
    }

    @Override
    public AstNode visitBool_t(MindcodeParser.Bool_tContext ctx) {
        if (ctx.TRUE() != null) {
            return new BooleanLiteral(true);
        } else if (ctx.FALSE() != null) {
            return new BooleanLiteral(false);
        } else {
            throw new ParsingException("Failed to parse bool_t expression: " + ctx.getText());
        }
    }

    @Override
    public AstNode visitNumeric(MindcodeParser.NumericContext ctx) {
        return new NumericLiteral(ctx.getText());
    }

    @Override
    public AstNode visitNull_t(MindcodeParser.Null_tContext ctx) {
        return new NullLiteral();
    }

    @Override
    public AstNode visitSensor_read(MindcodeParser.Sensor_readContext ctx) {
        final String target;
        if (ctx.target != null) {
            target = ctx.target.getText();
        } else if (ctx.unit != null) {
            target = ctx.unit.getText();
        } else {
            throw new ParsingException("Unable to determine sensor read target in " + ctx.getText());
        }

        return new SensorReading(target, "@" + ctx.resource().getText());
    }

    @Override
    public AstNode visitRvalue(MindcodeParser.RvalueContext ctx) {
        if (ctx.op == null) {
            return super.visitRvalue(ctx);
        }

        switch (ctx.rvalue().size()) {
            case 1:
                return new UnaryOp(ctx.op.getText(), visitRvalue(ctx.rvalue(0)));

            case 2:
                final AstNode left = visitRvalue(ctx.rvalue(0));
                final String op = ctx.op.getText();
                final AstNode right = visitRvalue(ctx.rvalue(1));
                return new BinaryOp(left, op, right);

            default:
                throw new ParsingException("Expected 1 or 2 rvalues, found " + ctx.rvalue().size() + " in " + ctx.getText());
        }
    }

    @Override
    public AstNode visitRef(MindcodeParser.RefContext ctx) {
        return new Ref(ctx.name.getText().substring(1));
    }

    @Override
    public AstNode visitIf_expression(MindcodeParser.If_expressionContext ctx) {
        return new IfExpression(
                visit(ctx.cond),
                visit(ctx.true_branch),
                ctx.false_branch == null ? new NoOp() : visit(ctx.false_branch)
        );
    }

    @Override
    public AstNode visitProgram(MindcodeParser.ProgramContext ctx) {
        // flatMap to the rescue!
        final AstNode last = visitExpression_list(ctx.expression_list());
        if (last instanceof Seq) {
            return last;
        } else {
            return new Seq(last);
        }
    }

    @Override
    public AstNode visitCStyleLoop(MindcodeParser.CStyleLoopContext ctx) {
        final AstNode init = visit(ctx.init_expr());
        final AstNode cond = visit(ctx.cond_expr());
        final AstNode loop = visit(ctx.loop_expr());
        final AstNode body = visit(ctx.body);
        return new Seq(
                init,
                new WhileStatement(
                        cond,
                        new Seq(body, loop)
                )
        );
    }

    @Override
    public AstNode visitRangeStyleLoop(MindcodeParser.RangeStyleLoopContext ctx) {
        final VarRef name = (VarRef) visit(ctx.name);
        final Range range = (Range) visit(ctx.range());
        final AstNode body = visit(ctx.body);

        return new Seq(
                new VarAssignment(name.getName(), range.getFirstValue()),
                new WhileStatement(
                        range.buildLoopExitCondition(name),
                        new Seq(
                                body,
                                new VarAssignment(
                                        name.getName(),
                                        new BinaryOp(
                                                name,
                                                "+",
                                                new NumericLiteral("1")
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public AstNode visitInit_expr(MindcodeParser.Init_exprContext ctx) {
        return buildMultiAssignments(
                ctx.assignment().stream()
                        .map(this::visit)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public AstNode visitLoop_expr(MindcodeParser.Loop_exprContext ctx) {
        return buildMultiAssignments(
                ctx.assignment().stream()
                        .map(this::visit)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public AstNode visitInclusiveRange(MindcodeParser.InclusiveRangeContext ctx) {
        return new InclusiveRange(visit(ctx.firstValue), visit(ctx.lastValue));
    }

    @Override
    public AstNode visitExclusiveRange(MindcodeParser.ExclusiveRangeContext ctx) {
        return new ExclusiveRange(visit(ctx.firstValue), visit(ctx.lastValue));
    }

    @NotNull
    private AstNode buildMultiAssignments(List<AstNode> assignments) {
        switch (assignments.size()) {
            case 0:
                return new Seq(new NoOp());

            case 1:
                return new Seq(assignments.get(0));

            case 2:
                return new Seq(assignments.get(0), assignments.get(1));

            case 3:
                return new Seq(assignments.get(0), new Seq(assignments.get(1), assignments.get(2)));

            case 4:
                return new Seq(new Seq(assignments.get(0), assignments.get(1)), new Seq(assignments.get(2), assignments.get(3)));

            default:
                throw new ParsingException("Loop expression too complex -- expected 4 or less assignments");
        }
    }
}
