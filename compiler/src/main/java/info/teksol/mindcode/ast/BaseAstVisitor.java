package info.teksol.mindcode.ast;

public abstract class BaseAstVisitor<T> implements AstVisitor<T> {
    @Override
    public T visit(AstNode node) {
        if (node instanceof BinaryOp) {
            return visitBinaryOp((BinaryOp) node);
        } else if (node instanceof BooleanLiteral) {
            return visitBooleanLiteral((BooleanLiteral) node);
        } else if (node instanceof BreakStatement) {
            return visitBreakStatement((BreakStatement) node);
        } else if (node instanceof CaseAlternative) {
            return visitCaseAlternative((CaseAlternative) node);
        } else if (node instanceof CaseExpression) {
            return visitCaseExpression((CaseExpression) node);
        } else if (node instanceof ContinueStatement) {
            return visitContinueStatement((ContinueStatement) node);
        } else if (node instanceof Control) {
            return visitControl((Control) node);
        } else if (node instanceof Constant) {
            return visitConstant((Constant) node);
        } else if (node instanceof DoWhileExpression) {
            return visitDoWhileStatement((DoWhileExpression) node);
        } else if (node instanceof ForEachExpression) {
            return visitForEachStatement((ForEachExpression) node);
        } else if (node instanceof FunctionCall) {
            return visitFunctionCall((FunctionCall) node);
        } else if (node instanceof FunctionDeclaration) {
            return visitFunctionDeclaration((FunctionDeclaration) node);
        } else if (node instanceof HeapAccess) {
            return visitHeapAccess((HeapAccess) node);
        } else if (node instanceof IfExpression) {
            return visitIfExpression((IfExpression) node);
        } else if (node instanceof NoOp) {
            return visitNoOp((NoOp) node);
        } else if (node instanceof NullLiteral) {
            return visitNullLiteral((NullLiteral) node);
        } else if (node instanceof NumericLiteral) {
            return visitNumericLiteral((NumericLiteral) node);
        } else if (node instanceof Printf) {
            return visitPrintf((Printf) node);
        } else if (node instanceof Ref) {
            return visitRef((Ref) node);
        } else if (node instanceof PropertyAccess) {
            return visitPropertyAccess((PropertyAccess) node);
        } else if (node instanceof Range) {
            return visitRange((Range) node);
        } else if (node instanceof ReturnStatement) {
            return visitReturnStatement((ReturnStatement) node);
        } else if (node instanceof Seq) {
            return visitSeq((Seq) node);
        } else if (node instanceof StackAllocation) {
            return visitStackAllocation((StackAllocation) node);
        } else if (node instanceof StringLiteral) {
            return visitStringLiteral((StringLiteral) node);
        } else if (node instanceof UnaryOp) {
            return visitUnaryOp((UnaryOp) node);
        } else if (node instanceof Assignment) {
            return visitAssignment((Assignment) node);
        } else if (node instanceof VarRef) {
            return visitVarRef((VarRef) node);
        } else if (node instanceof WhileExpression) {
            return visitWhileStatement((WhileExpression) node);
        } else {
            throw new AstWalkerException("Unrecognized node type " + node.getClass() + ": " + node);
        }
    }
}
