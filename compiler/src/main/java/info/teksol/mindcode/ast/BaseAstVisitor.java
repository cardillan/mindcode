package info.teksol.mindcode.ast;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;

import java.util.function.Consumer;

public abstract class BaseAstVisitor<T> extends AbstractMessageEmitter implements AstVisitor<T> {
    public BaseAstVisitor(Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
    }

    @Override
    public T visit(AstNode node) {
        return switch (node) {
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
            case DirectiveText n        -> visitDirectiveText(n);
            case DoWhileExpression n    -> visitDoWhileStatement(n);
            case ForEachExpression n    -> visitForEachStatement(n);
            case FormattableLiteral n   -> visitFormattableLiteral(n);
            case FunctionArgument n     -> visitFunctionArgument(n);
            case FunctionCall n         -> visitFunctionCall(n);
            case FunctionDeclaration n  -> visitFunctionDeclaration(n);
            case FunctionParameter n    -> visitFunctionParameter(n);
            case HeapAccess n           -> visitHeapAccess(n);
            case HeapAllocation n       -> visitHeapAllocation(n);
            case IfExpression n         -> visitIfExpression(n);
            case Iterator n             -> visitIterator(n);
            case NoOp n                 -> visitNoOp(n);
            case NullLiteral n          -> visitNullLiteral(n);
            case NumericLiteral n       -> visitNumericLiteral(n);
            case NumericValue n         -> visitNumericValue(n);
            case ProgramParameter n     -> visitParameter(n);
            case PropertyAccess n       -> visitPropertyAccess(n);
            case Range n                -> visitRange(n);
            case RangedForExpression n  -> visitRangedForExpression(n);
            case Ref n                  -> visitRef(n);
            case Requirement n          -> visitRequirement(n);
            case ReturnStatement n      -> visitReturnStatement(n);
            case Seq n                  -> visitSeq(n);
            case StackAllocation n      -> visitStackAllocation(n);
            case StringLiteral n        -> visitStringLiteral(n);
            case UnaryOp n              -> visitUnaryOp(n);
            case VarRef n               -> visitVarRef(n);
            case VoidLiteral n          -> visitVoidLiteral(n);
            case WhileExpression n      -> visitWhileStatement(n);
            default -> throw new AstWalkerException("Unrecognized node type " + node.getClass() + ": " + node);
        };
    }
}
