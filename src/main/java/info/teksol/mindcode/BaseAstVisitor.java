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
        } else if (node instanceof HeapRead) {
            return visitHeapRead((HeapRead) node);
        } else if (node instanceof HeapWrite) {
            return visitHeapWrite((HeapWrite) node);
        } else if (node instanceof IfExpression) {
            return visitIfExpression((IfExpression) node);
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
        } else if (node instanceof UnitAssignment) {
            return visitUnitAssignment((UnitAssignment) node);
        } else if (node instanceof UnitRef) {
            return visitUnitRef((UnitRef) node);
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
}
