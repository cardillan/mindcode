package info.teksol.mindcode.ast;

public interface AstVisitor<T> {
    T visit(AstNode node);

    T visitAssignment(Assignment node);

    T visitBinaryOp(BinaryOp node);

    T visitBoolBinaryOp(BoolBinaryOp node);

    T visitBooleanLiteral(BooleanLiteral node);

    T visitBreakStatement(BreakStatement node);

    T visitCaseAlternative(CaseAlternative node);

    T visitCaseExpression(CaseExpression node);

    T visitConstant(Constant node);

    T visitContinueStatement(ContinueStatement node);

    T visitControl(Control node);

    T visitDirective(Directive node);

    T visitDoWhileStatement(DoWhileExpression node);

    T visitForEachStatement(ForEachExpression node);

    T visitFunctionCall(FunctionCall node);

    T visitFunctionDeclaration(FunctionDeclaration node);

    T visitHeapAccess(HeapAccess node);

    T visitHeapAllocation(HeapAllocation node);

    T visitIfExpression(IfExpression node);

    T visitNoOp(NoOp node);

    T visitNullLiteral(NullLiteral node);

    T visitNumericLiteral(NumericLiteral node);

    T visitPropertyAccess(PropertyAccess node);

    T visitRange(Range node);

    T visitRef(Ref node);

    T visitReturnStatement(ReturnStatement node);

    T visitSeq(Seq seq);

    T visitStackAllocation(StackAllocation node);

    T visitStringLiteral(StringLiteral node);

    T visitUnaryOp(UnaryOp node);

    T visitVarRef(VarRef node);

    T visitWhileStatement(WhileExpression node);
}
