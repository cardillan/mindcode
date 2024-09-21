package info.teksol.mindcode.ast;

public abstract class BaseAstVisitor<T> implements AstVisitor<T> {
    @Override
    public T visit(AstNode node) {
        if (node instanceof Assignment n)               return visitAssignment(n);
        if (node instanceof BoolBinaryOp n)             return visitBoolBinaryOp(n);
        if (node instanceof BinaryOp n)                 return visitBinaryOp(n);
        if (node instanceof BooleanLiteral n)           return visitBooleanLiteral(n);
        if (node instanceof BreakStatement n)           return visitBreakStatement(n);
        if (node instanceof CaseAlternative n)          return visitCaseAlternative(n);
        if (node instanceof CaseExpression n)           return visitCaseExpression(n);
        if (node instanceof Constant n)                 return visitConstant(n);
        if (node instanceof ContinueStatement n)        return visitContinueStatement(n);
        if (node instanceof Control n)                  return visitControl(n);
        if (node instanceof Directive n)                return visitDirective(n);
        if (node instanceof DoWhileExpression n)        return visitDoWhileStatement(n);
        if (node instanceof ForEachExpression n)        return visitForEachStatement(n);
        if (node instanceof FunctionCall n)             return visitFunctionCall(n);
        if (node instanceof FunctionDeclaration n)      return visitFunctionDeclaration(n);
        if (node instanceof HeapAccess n)               return visitHeapAccess(n);
        if (node instanceof HeapAllocation n)           return visitHeapAllocation(n);
        if (node instanceof IfExpression n)             return visitIfExpression(n);
        if (node instanceof Iterator n)                 return visitIterator(n);
        if (node instanceof NoOp n)                     return visitNoOp(n);
        if (node instanceof NullLiteral n)              return visitNullLiteral(n);
        if (node instanceof NumericLiteral n)           return visitNumericLiteral(n);
        if (node instanceof NumericValue n)             return visitNumericValue(n);
        if (node instanceof Parameter n)                return visitParameter(n);
        if (node instanceof PropertyAccess n)           return visitPropertyAccess(n);
        if (node instanceof Range n)                    return visitRange(n);
        if (node instanceof RangedForExpression n)      return visitRangedForExpression(n);
        if (node instanceof Ref n)                      return visitRef(n);
        if (node instanceof ReturnStatement n)          return visitReturnStatement(n);
        if (node instanceof Seq n)                      return visitSeq(n);
        if (node instanceof StackAllocation n)          return visitStackAllocation(n);
        if (node instanceof StringLiteral n)            return visitStringLiteral(n);
        if (node instanceof UnaryOp n)                  return visitUnaryOp(n);
        if (node instanceof VarRef n)                   return visitVarRef(n);
        if (node instanceof WhileExpression n)          return visitWhileStatement(n);
        throw new AstWalkerException("Unrecognized node type " + node.getClass() + ": " + node);
    }
}
