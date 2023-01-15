package info.teksol.mindcode.ast;

import info.teksol.mindcode.MindcodeException;

public class AstPrettyPrinter extends BaseAstVisitor<String> {
    private final StringBuilder buffer = new StringBuilder();

    public String prettyPrint(AstNode node) {
        visit(node);
        return buffer.toString();
    }

    @Override
    public String visitRef(Ref node) {
        buffer.append("@");
        buffer.append(node.getName());
        return null;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        buffer.append("if ");
        visit(node.getCondition());
        buffer.append("\n");
        visit(node.getTrueBranch());
        buffer.append("\nelse\n");
        visit(node.getFalseBranch());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitHeapAccess(HeapAccess node) {
        buffer.append(node.getCellName());
        buffer.append("[");
        visit(node.getAddress());
        buffer.append("]");
        return null;
    }

    @Override
    public String visitControl(Control node) {
        visit(node.getTarget());
        buffer.append(".");
        buffer.append(node.getProperty());
        switch (node.getParams().size()) {
            case 0:
                throw new MindcodeException("Unexpected Control AST node with no parameters");

            case 1:
                buffer.append(" = ");
                visit(node.getParams().get(0));
                break;

            default:
                buffer.append("(");
                for (int i = 0; i < node.getParams().size(); i++) {
                    if (i > 0) buffer.append(", ");
                    visit(node.getParams().get(i));
                }
                buffer.append(")");
                break;
        }
        return null;
    }

    @Override
    public String visitWhileStatement(WhileExpression node) {
        buffer.append(" while ");
        visit(node.getCondition());
        buffer.append("\n");
        visit(node.getBody());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitVarRef(VarRef node) {
        buffer.append(node.getName());
        return null;
    }

    @Override
    public String visitAssignment(Assignment node) {
        visit(node.getVar());
        buffer.append(" = ");
        visit(node.getValue());
        return null;
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        buffer.append(node.getOp());
        visit(node.getExpression());
        return null;
    }

    @Override
    public String visitStringLiteral(StringLiteral node) {
        buffer.append('"');
        buffer.append(node.getText());
        buffer.append('"');
        return null;
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        buffer.append(node.getLiteral());
        return null;
    }

    @Override
    public String visitNullLiteral(NullLiteral node) {
        buffer.append("null");
        return null;
    }

    @Override
    public String visitNoOp(NoOp node) {
        return null;
    }

    @Override
    public String visitFunctionCall(FunctionCall node) {
        buffer.append(node.getFunctionName());
        buffer.append("(");
        for (int i = 0; i < node.getParams().size() - 1; i++) {
            visit(node.getParams().get(i));
            buffer.append(", ");
        }
        if (!node.getParams().isEmpty()) {
            visit(node.getParams().get(node.getParams().size() - 1));
        }
        buffer.append(")");
        return null;
    }

    @Override
    public String visitBooleanLiteral(BooleanLiteral node) {
        buffer.append(node.getValue());
        return null;
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        visit(node.getLeft());
        buffer.append(" ");
        buffer.append(node.getOp());
        buffer.append(" ");
        visit(node.getRight());
        return null;
    }

    @Override
    public String visitSeq(Seq seq) {
        visit(seq.getRest());
        buffer.append("\n");
        visit(seq.getLast());
        buffer.append("\n");
        return null;
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        visit(node.getTarget());
        buffer.append(".");
        buffer.append(node.getProperty());
        return null;
    }

    @Override
    public String visitCaseExpression(CaseExpression node) {
        buffer.append("case ");
        visit(node.getCondition());
        buffer.append("\n");
        for (int i = 0; i < node.getAlternatives().size() - 1; i++) {
            buffer.append("when ");
            visit(node.getAlternatives().get(i).getValue());
            buffer.append("\n");
            visit(node.getAlternatives().get(i).getBody());
        }
        if (!node.getAlternatives().isEmpty()) {
            buffer.append("when ");
            visit(node.getAlternatives().get(node.getAlternatives().size() - 1).getValue());
            buffer.append("\n");
            visit(node.getAlternatives().get(node.getAlternatives().size() - 1).getBody());
        }
        buffer.append("else\n");
        visit(node.getElseBranch());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        buffer.append("def ");
        buffer.append(node.getName());
        if (!node.getParams().isEmpty()) {
            buffer.append("(");
            for (int i = 0; i < node.getParams().size() - 1; i++) {
                visit(node.getParams().get(i));
                buffer.append(", ");
            }
            if (!node.getParams().isEmpty()) {
                visit(node.getParams().get(node.getParams().size() - 1));
            }
            buffer.append(")");
        }
        buffer.append("\n");
        visit(node.getBody());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitStackAllocation(StackAllocation node) {
        buffer.append("allocate stack in ");
        buffer.append(node.getName());
        buffer.append("[");
        buffer.append(node.getFirst());
        buffer.append("..");
        buffer.append(node.getLast());
        buffer.append("]");
        return null;
    }
}
