package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;
import info.teksol.mindcode.grammar.MindcodeBaseVisitor;
import info.teksol.mindcode.grammar.MindcodeParser;

import java.util.ArrayList;
import java.util.List;

public class AstNodeBuilder extends MindcodeBaseVisitor<AstNode> {
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
            throw new ParsingException("Failed to parse expression list: " + ctx.getText());
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
    public AstNode visitLvalue(MindcodeParser.LvalueContext ctx) {
        return new VarRef(ctx.id().ID().getSymbol().getText());
    }

    @Override
    public AstNode visitAssignment(MindcodeParser.AssignmentContext ctx) {
        if (ctx.target != null) {
            // simple assignment
            if (ctx.op != null) {
                // +=, -=, etc...
                return new VarAssignment(
                        ctx.target.getText(),
                        new BinaryOp(
                                visitLvalue(ctx.lvalue()),
                                ctx.op.getText().replace("=", ""),
                                visitRvalue(ctx.rvalue())
                        )
                );
            } else {
                return new VarAssignment(
                        ctx.target.getText(),
                        visitRvalue(ctx.rvalue())
                );
            }
        }

        if (ctx.heap_ref() != null) {
            final AstNode lvalue = visitHeap_ref(ctx.heap_ref());
            final AstNode rvalue = visitRvalue(ctx.value);
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

        throw new ParsingException("Expected lvalue in " + ctx.getText());
    }

    @Override
    protected AstNode aggregateResult(AstNode aggregate, AstNode nextResult) {
        if (nextResult != null) return nextResult;
        return aggregate;
    }

    @Override
    public AstNode visitHeap_ref(MindcodeParser.Heap_refContext ctx) {
        return new HeapRead(ctx.target.getText(), ctx.addr.getText());
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
    public AstNode visitUnit_ref(MindcodeParser.Unit_refContext ctx) {
        return new UnitRef(ctx.name.getText());
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
}
