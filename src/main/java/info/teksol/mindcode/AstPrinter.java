package info.teksol.mindcode;

public class AstPrinter extends BaseAstWalker<StringBuilder> {
    public static String print(AstNode program) {
        final StringBuilder accum = new StringBuilder();
        new AstPrinter().visit(program, accum);
        return accum.toString();
    }

    @Override
    public void visitWhileStatement(WhileStatement node, StringBuilder accumulator) {
        accumulator.append("while ");
        visit(node.getCondition(), accumulator);
        accumulator.append(" {\n");
        visit(node.getBody(), accumulator);
        accumulator.append("\n}\n");
    }

    @Override
    public void visitVarRef(VarRef node, StringBuilder accumulator) {
        accumulator.append(node.getName());
    }

    @Override
    public void visitVarAssignment(VarAssignment node, StringBuilder accumulator) {
        accumulator.append(node.getVarName());
        accumulator.append(" = ");
        visit(node.getRvalue(), accumulator);
    }

    @Override
    public void visitUnaryOp(UnaryOp node, StringBuilder accumulator) {
        accumulator.append(node.getOp());
        accumulator.append(" ");
        visit(node.getExpression(), accumulator);
    }

    @Override
    public void visitStringLiteral(StringLiteral node, StringBuilder accumulator) {
        accumulator.append("\"");
        accumulator.append(node.getText());
        accumulator.append("\"");
    }

    @Override
    public void visitNumericLiteral(NumericLiteral node, StringBuilder accumulator) {
        accumulator.append(node.getLiteral());
    }

    @Override
    public void visitNullLiteral(NullLiteral node, StringBuilder accumulator) {
        accumulator.append("null");
    }

    @Override
    public void visitFunctionCall(FunctionCall node, StringBuilder accumulator) {
        accumulator.append(node.getFunctionName());
        accumulator.append("(");
        if (!node.getParams().isEmpty()) {
            for (int i = 0; i < node.getParams().size() - 1; i++) {
                final AstNode n = node.getParams().get(i);
                visit(n, accumulator);
                accumulator.append(", ");
            }

            visit(node.getParams().get(node.getParams().size() - 1), accumulator);
        }

        accumulator.append(")");
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral node, StringBuilder accumulator) {
        accumulator.append(node.getValue());
    }

    @Override
    public void visitBinaryOp(BinaryOp node, StringBuilder accumulator) {
        visit(node.getLeft(), accumulator);
        accumulator.append(" ");
        accumulator.append(node.getOp());
        accumulator.append(" ");
        visit(node.getRight(), accumulator);
    }

    @Override
    public void visitSeq(Seq seq, StringBuilder accumulator) {
        visit(seq.getRest(), accumulator);
        accumulator.append("\n");
        visit(seq.getLast(), accumulator);
        accumulator.append("\n");
    }
}
