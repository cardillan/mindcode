package info.teksol.mindcode;

public interface AstWalker<T> {
    void visit(AstNode node, T accumulator);

    void visitWhileStatement(WhileStatement node, T accumulator);

    void visitVarRef(VarRef node, T accumulator);

    void visitVarAssignment(VarAssignment node, T accumulator);

    void visitUnaryOp(UnaryOp node, T accumulator);

    void visitStringLiteral(StringLiteral node, T accumulator);

    void visitNumericLiteral(NumericLiteral node, T accumulator);

    void visitNullLiteral(NullLiteral node, T accumulator);

    void visitNoOp(NoOp node, T accumulator);

    void visitFunctionCall(FunctionCall node, T accumulator);

    void visitBooleanLiteral(BooleanLiteral node, T accumulator);

    void visitBinaryOp(BinaryOp node, T accumulator);

    void visitSeq(Seq seq, T accumulator);
}
