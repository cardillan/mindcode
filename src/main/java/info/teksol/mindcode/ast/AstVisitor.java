package info.teksol.mindcode.ast;

public interface AstVisitor<T> {
    T visit(AstNode node);

    T visitUnitAssignment(UnitAssignment node);

    T visitUnitRef(UnitRef node);

    T visitIfExpression(IfExpression node);

    T visitHeapRead(HeapRead node);

    T visitHeapWrite(HeapWrite node);

    T visitControl(Control node);

    T visitWhileStatement(WhileStatement node);

    T visitVarRef(VarRef node);

    T visitVarAssignment(VarAssignment node);

    T visitUnaryOp(UnaryOp node);

    T visitSensorReading(SensorReading node);

    T visitStringLiteral(StringLiteral node);

    T visitNumericLiteral(NumericLiteral node);

    T visitNullLiteral(NullLiteral node);

    T visitNoOp(NoOp node);

    T visitFunctionCall(FunctionCall node);

    T visitBooleanLiteral(BooleanLiteral node);

    T visitBinaryOp(BinaryOp node);

    T visitSeq(Seq seq);

    T visitComment(Comment node);
}
