package info.teksol.mindcode;

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
            throw new MindcodeParseException("Failed to parse expression list: " + ctx.getText());
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
            throw new MindcodeParseException("Failed to understand block statement list in " + ctx.getText());
        }
    }

    @Override
    public AstNode visitExpression(MindcodeParser.ExpressionContext ctx) {
        if (ctx.rvalue() != null) {
            return visitRvalue(ctx.rvalue());
        } else if (ctx.while_statement() != null) {
            return visitWhile_statement(ctx.while_statement());
        } else if (ctx.funcall_statement() != null) {
            return visitFuncall_statement(ctx.funcall_statement());
        } else if (ctx.control_statement() != null) {
            return visitControl_statement(ctx.control_statement());
        } else {
            throw new MindcodeParseException("Missing expression in " + ctx.getText());
        }
    }

    @Override
    public AstNode visitControl_statement(MindcodeParser.Control_statementContext ctx) {
        return new Control(ctx.target.getText(), ctx.property.getText(), visitRvalue(ctx.value));
    }

    @Override
    public AstNode visitFuncall_statement(MindcodeParser.Funcall_statementContext ctx) {
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
    public AstNode visitRvalue(MindcodeParser.RvalueContext ctx) {
        if (ctx.lvalue() != null) {
            return visitLvalue(ctx.lvalue());
        }

        if (ctx.assignment() != null) {
            if (ctx.assignment().lvalue().id() != null) {
                if (ctx.assignment().op != null) {
                    return new VarAssignment(
                            ctx.assignment().lvalue().getText(),
                            new BinaryOp(
                                    visitLvalue(ctx.assignment().lvalue()),
                                    ctx.assignment().op.getText().replace("=", ""),
                                    visitRvalue(ctx.assignment().rvalue())
                            )
                    );
                } else {
                    return new VarAssignment(
                            ctx.assignment().lvalue().getText(),
                            visitRvalue(ctx.assignment().rvalue())
                    );
                }
            }

            throw new MindcodeParseException("Expected lvalue in " + ctx.assignment().getText());
        }

        if (ctx.literal_t() != null) {
            String str = ctx.literal_t().LITERAL().getSymbol().getText();
            return new StringLiteral(str.substring(1, str.length() - 1).replaceAll("\\\\\"", "\""));
        }

        if (ctx.bool_t() != null) {
            if (ctx.bool_t().TRUE() != null) {
                return new BooleanLiteral(true);
            } else if (ctx.bool_t().FALSE() != null) {
                return new BooleanLiteral(false);
            } else {
                throw new MindcodeParseException("Failed to parse bool_t expression: " + ctx.getText());
            }
        }

        if (ctx.float_t() != null) {
            return new NumericLiteral(ctx.float_t().FLOAT().getSymbol().getText());
        }

        if (ctx.int_t() != null) {
            return new NumericLiteral(ctx.int_t().INT().getSymbol().getText());
        }

        if (ctx.null_t() != null) {
            return new NullLiteral();
        }

        if (ctx.sensor_read() != null) {
            final StringBuilder resource = new StringBuilder();
            if (ctx.sensor_read().resource() != null) {
                resource.append("@");
                resource.append(ctx.sensor_read().resource().getText());
            } else if (ctx.sensor_read().liquid() != null) {
                resource.append("@");
                resource.append(ctx.sensor_read().liquid().getText());
            } else if (ctx.sensor_read().sensor() != null) {
                resource.append(ctx.sensor_read().sensor().getText());
            } else {
                throw new MindcodeParseException("Unable to convert sensor reading: " + ctx.sensor_read().getText());
            }

            return new SensorReading(ctx.sensor_read().target.getText(), resource.toString());
        }

        if (ctx.rvalue() != null) {
            switch (ctx.rvalue().size()) {
                case 1:
                    if (ctx.op != null) {
                        return new UnaryOp(ctx.op.getText(), visitRvalue(ctx.rvalue(0)));
                    } else {
                        return visitRvalue(ctx.rvalue(0));
                    }

                case 2:
                    return new BinaryOp(
                            visitRvalue(ctx.rvalue(0)),
                            ctx.op.getText(),
                            visitRvalue(ctx.rvalue(1))
                    );

                default:
                    throw new MindcodeParseException("Expected 1 or 2 rvalues, found " + ctx.rvalue().size() + " in " + ctx.getText());
            }
        }

        throw new MindcodeParseException("RValue parsing failed: " + ctx.getText());
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
