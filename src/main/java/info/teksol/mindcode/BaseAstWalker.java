package info.teksol.mindcode;

public abstract class BaseAstWalker<T> implements AstWalker<T> {
    @Override
    public void visit(AstNode node, T accumulator) {
        if (node instanceof BinaryOp) {
            visitBinaryOp((BinaryOp) node, accumulator);
        } else if (node instanceof BooleanLiteral) {
            visitBooleanLiteral((BooleanLiteral) node, accumulator);
        } else if (node instanceof FunctionCall) {
            visitFunctionCall((FunctionCall) node, accumulator);
        } else if (node instanceof NoOp) {
            visitNoOp((NoOp) node, accumulator);
        } else if (node instanceof NullLiteral) {
            visitNullLiteral((NullLiteral) node, accumulator);
        } else if (node instanceof NumericLiteral) {
            visitNumericLiteral((NumericLiteral) node, accumulator);
        } else if (node instanceof Seq) {
            visitSeq((Seq) node, accumulator);
        } else if (node instanceof StringLiteral) {
            visitStringLiteral((StringLiteral) node, accumulator);
        } else if (node instanceof UnaryOp) {
            visitUnaryOp((UnaryOp) node, accumulator);
        } else if (node instanceof VarAssignment) {
            visitVarAssignment((VarAssignment) node, accumulator);
        } else if (node instanceof VarRef) {
            visitVarRef((VarRef) node, accumulator);
        } else if (node instanceof WhileStatement) {
            visitWhileStatement((WhileStatement) node, accumulator);
        } else {
            throw new AstWalkerException("Unrecognized node type " + node.getClass() + ": " + node);
        }
    }

    @Override
    public void visitWhileStatement(WhileStatement node, T accumulator) {
        visit(node.getCondition(), accumulator);
        visit(node.getBody(), accumulator);
    }

    @Override
    public void visitVarRef(VarRef node, T accumulator) {
    }

    @Override
    public void visitVarAssignment(VarAssignment node, T accumulator) {
        visit(node.getRvalue(), accumulator);
    }

    @Override
    public void visitUnaryOp(UnaryOp node, T accumulator) {
        visit(node.getExpression(), accumulator);
    }

    @Override
    public void visitStringLiteral(StringLiteral node, T accumulator) {
    }

    @Override
    public void visitNumericLiteral(NumericLiteral node, T accumulator) {
    }

    @Override
    public void visitNullLiteral(NullLiteral node, T accumulator) {
    }

    @Override
    public void visitNoOp(NoOp node, T accumulator) {
    }

    @Override
    public void visitFunctionCall(FunctionCall node, T accumulator) {
        node.getParams().forEach((n) -> visit(n, accumulator));
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral node, T accumulator) {
    }

    @Override
    public void visitBinaryOp(BinaryOp node, T accumulator) {
        visit(node.getLeft(), accumulator);
        visit(node.getRight(), accumulator);
    }

    @Override
    public void visitSeq(Seq seq, T accumulator) {
        visit(seq.getRest(), accumulator);
        visit(seq.getLast(), accumulator);
    }
}
