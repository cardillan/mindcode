package info.teksol.mindcode;

public abstract class BaseAstVisitor<T> implements AstVisitor<T> {
    @Override
    public T visit(AstNode node) {
        if (node instanceof BinaryOp) {
            return visitBinaryOp((BinaryOp) node);
        } else if (node instanceof BooleanLiteral) {
            return visitBooleanLiteral((BooleanLiteral) node);
        } else if (node instanceof Control) {
            return visitControl((Control) node);
        } else if (node instanceof FunctionCall) {
            return visitFunctionCall((FunctionCall) node);
        } else if (node instanceof NoOp) {
            return visitNoOp((NoOp) node);
        } else if (node instanceof NullLiteral) {
            return visitNullLiteral((NullLiteral) node);
        } else if (node instanceof NumericLiteral) {
            return visitNumericLiteral((NumericLiteral) node);
        } else if (node instanceof Seq) {
            return visitSeq((Seq) node);
        } else if (node instanceof SensorReading) {
            return visitSensorReading((SensorReading) node);
        } else if (node instanceof StringLiteral) {
            return visitStringLiteral((StringLiteral) node);
        } else if (node instanceof UnaryOp) {
            return visitUnaryOp((UnaryOp) node);
        } else if (node instanceof VarAssignment) {
            return visitVarAssignment((VarAssignment) node);
        } else if (node instanceof VarRef) {
            return visitVarRef((VarRef) node);
        } else if (node instanceof WhileStatement) {
            return visitWhileStatement((WhileStatement) node);
        } else {
            throw new AstWalkerException("Unrecognized node type " + node.getClass() + ": " + node);
        }
    }

    @Override
    public T visitControl(Control node) {
        return null;
    }

    @Override
    public T visitWhileStatement(WhileStatement node) {
        return null;
    }

    @Override
    public T visitVarRef(VarRef node) {
        return null;
    }

    @Override
    public T visitVarAssignment(VarAssignment node) {
        return null;
    }

    @Override
    public T visitUnaryOp(UnaryOp node) {
        return null;
    }

    @Override
    public T visitStringLiteral(StringLiteral node) {
        return null;
    }

    @Override
    public T visitSensorReading(SensorReading node) {
        return null;
    }

    @Override
    public T visitNumericLiteral(NumericLiteral node) {
        return null;
    }

    @Override
    public T visitNullLiteral(NullLiteral node) {
        return null;
    }

    @Override
    public T visitNoOp(NoOp node) {
        return null;
    }

    @Override
    public T visitFunctionCall(FunctionCall node) {
        return null;
    }

    @Override
    public T visitBooleanLiteral(BooleanLiteral node) {
        return null;
    }

    @Override
    public T visitBinaryOp(BinaryOp node) {
        return null;
    }

    @Override
    public T visitSeq(Seq seq) {
        return null;
    }
}
