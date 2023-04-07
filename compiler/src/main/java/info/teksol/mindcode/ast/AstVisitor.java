package info.teksol.mindcode.ast;

public interface AstVisitor<T> {
    T visit(AstNode node);

    T visitRef(Ref node);

    T visitIfExpression(IfExpression node);

    T visitHeapAllocation(HeapAllocation node);

    T visitHeapAccess(HeapAccess node);

    T visitControl(Control node);

    T visitConstant(Constant node);

    T visitDirective(Directive node);

    T visitDoWhileStatement(DoWhileExpression node);

    T visitForEachStatement(ForEachExpression node);

    T visitWhileStatement(WhileExpression node);

    T visitVarRef(VarRef node);

    T visitAssignment(Assignment node);

    T visitUnaryOp(UnaryOp node);

    T visitStringLiteral(StringLiteral node);

    T visitNumericLiteral(NumericLiteral node);

    T visitNullLiteral(NullLiteral node);

    T visitNoOp(NoOp node);

    T visitFunctionCall(FunctionCall node);

    T visitBooleanLiteral(BooleanLiteral node);

    T visitBinaryOp(BinaryOp node);

    T visitSeq(Seq seq);

    T visitPropertyAccess(PropertyAccess node);

    T visitCaseExpression(CaseExpression node);

    T visitCaseAlternative(CaseAlternative node);

    T visitRange(Range node);

    T visitFunctionDeclaration(FunctionDeclaration node);

    T visitStackAllocation(StackAllocation node);

    T visitBreakStatement(BreakStatement node);

    T visitContinueStatement(ContinueStatement node);

    T visitReturnStatement(ReturnStatement node);
}
