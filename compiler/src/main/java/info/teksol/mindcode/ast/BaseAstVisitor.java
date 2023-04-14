package info.teksol.mindcode.ast;

public abstract class BaseAstVisitor<T> implements AstVisitor<T> {
    @Override
    public T visit(AstNode node) {
        return switch(node) {
            case Assignment n           -> visitAssignment(n);
            case BoolBinaryOp n         -> visitBoolBinaryOp(n);
            case BinaryOp n             -> visitBinaryOp(n);
            case BooleanLiteral n       -> visitBooleanLiteral(n);
            case BreakStatement n       -> visitBreakStatement(n);
            case CaseAlternative n      -> visitCaseAlternative(n);
            case CaseExpression n       -> visitCaseExpression(n);
            case Constant n             -> visitConstant(n);
            case ContinueStatement n    -> visitContinueStatement(n);
            case Control n              -> visitControl(n);
            case Directive n            -> visitDirective(n);
            case DoWhileExpression n    -> visitDoWhileStatement(n);
            case ForEachExpression n    -> visitForEachStatement(n);
            case FunctionCall n         -> visitFunctionCall(n);
            case FunctionDeclaration n  -> visitFunctionDeclaration(n);
            case HeapAccess n           -> visitHeapAccess(n);
            case HeapAllocation n       -> visitHeapAllocation(n);
            case IfExpression n         -> visitIfExpression(n);
            case NoOp n                 -> visitNoOp(n);
            case NullLiteral n          -> visitNullLiteral(n);
            case NumericLiteral n       -> visitNumericLiteral(n);
            case PropertyAccess n       -> visitPropertyAccess(n);
            case Range n                -> visitRange(n);
            case Ref n                  -> visitRef(n);
            case ReturnStatement n      -> visitReturnStatement(n);
            case Seq n                  -> visitSeq(n);
            case StackAllocation n      -> visitStackAllocation(n);
            case StringLiteral n        -> visitStringLiteral(n);
            case UnaryOp n              -> visitUnaryOp(n);
            case VarRef n               -> visitVarRef(n);
            case WhileExpression n      -> visitWhileStatement(n);
            default  -> throw new AstWalkerException("Unrecognized node type " + node.getClass() + ": " + node);
        };
    }
}
