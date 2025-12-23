package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class NodeTransformation {

    public static AstMindcodeNode transform(AstMindcodeNode node) {
        return node instanceof AstExpression expression ? transform(expression) : node;
    }

    public static AstExpression transform(AstExpression expression) {
        return switch (expression) {
            case AstIfExpression node       -> transformIfExpression(node);
            case AstOperatorInList node     -> transformInList(node);
            case AstOperatorInRange node    -> transformInRange(node);
            case AstOperatorTernary node    -> transformTernary(node);
            case AstOperatorUnary node      -> transformUnaryNegation(node);
            case AstParentheses node        -> transform(node.getExpression());
            default -> expression;
        };
    }


    // TODO In the past, elsif branches were emulated as nested else if expressions.
    //      Code optimizers might therefore have problems if we encoded elsif branches more effectively.
    //      Change the implementation to a more efficient one and update the optimizers.
    private static AstExpression transformIfExpression(AstIfExpression node) {
        if (node.getIfBranches().isEmpty()) {
            throw new MindcodeInternalError("Invalid If expression structure (missing if branch).");
        }

        AstIfBranch last = node.getIfBranches().getLast();
        AstOperatorTernary result = new AstOperatorTernary(last.sourcePosition(), last.getCondition(),
                repackageBody(last, last.getBody()),
                repackageBody(last, node.getElseBranch()));
        result.setProfile(node.getProfile());

        for (int i = node.getIfBranches().size() - 2; i >= 0; i--) {
            final AstIfBranch branch = node.getIfBranches().get(i);
            result = new AstOperatorTernary(branch.sourcePosition(), branch.getCondition(),
                    repackageBody(branch, branch.getBody()),
                    result);
            result.setProfile(branch.getProfile());
        }

        return transformTernary(result);
    }

    private static AstMindcodeNode repackageBody(AstIfBranch branch, List<AstMindcodeNode> expressions) {
        SourcePosition position = branch.getCondition().sourcePosition();
        if (expressions.size() == 1 && expressions.getFirst() instanceof AstExpression exp) return exp;

        AstStatementList result = new AstStatementList(expressions.isEmpty() ? position : expressions.getFirst().sourcePosition(), expressions);
        result.setProfile(branch.getProfile());
        return result;
    }

    private static SourcePosition pos(SourceElement start, SourceElement end) {
        return start.sourcePosition().upTo(end.sourcePosition());
    }

    private static SourcePosition pos(List<? extends SourceElement> elements) {
        return elements.getFirst().sourcePosition().upTo(elements.getLast().sourcePosition());
    }

    private static AstExpression transformInList(AstOperatorInList node) {
        AstLiteralBoolean trueBranch = new AstLiteralBoolean(SourcePosition.EMPTY, true);
        AstLiteralBoolean falseBranch = new AstLiteralBoolean(SourcePosition.EMPTY, false);
        trueBranch.setProfile(node.getProfile());
        falseBranch.setProfile(node.getProfile());
        return transformInList(node, trueBranch, falseBranch, node.isNegated());
    }

    private static AstExpression transformInList(AstOperatorInList node,
            AstMindcodeNode trueBranch, AstMindcodeNode falseBranch, boolean swapBranches) {
        AstCaseAlternative alternative = new AstCaseAlternative(pos(node.getValues()),
                node.getValues(), List.of(swapBranches ? falseBranch : trueBranch));

        AstCaseExpression result = new AstCaseExpression(node.sourcePosition(),
                node.getValue(),
                List.of(alternative),
                List.of(swapBranches ? trueBranch : falseBranch),
                true);

        result.setProfile(node.getProfile());
        alternative.setProfile(node.getProfile());
        return result;
    }

    private static AstOperatorShortCircuiting transformInRange(AstOperatorInRange node) {
        AstOperatorShortCircuiting result;
        AstCachedNode cached = new AstCachedNode(node.getValue());

        if (node.isNegated()) {
            result = new AstOperatorShortCircuiting(node.sourcePosition(),
                    Operation.LOGICAL_OR,
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            Operation.LESS_THAN, cached, node.getRange().getFirstValue()),
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            AbstractCodeBuilder.outsideRangeCondition(node.getRange()).toOperation(),
                            cached, node.getRange().getLastValue()));
        } else {
            result = new AstOperatorShortCircuiting(node.sourcePosition(),
                    Operation.LOGICAL_AND,
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            Operation.GREATER_THAN_EQ, cached, node.getRange().getFirstValue()),
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            AbstractCodeBuilder.insideRangeCondition(node.getRange()).toOperation(),
                            cached, node.getRange().getLastValue()));
        }

        result.setProfile(node.getProfile());
        result.getLeft().setProfile(node.getProfile());
        result.getRight().setProfile(node.getProfile());
        return result;
    }

    private static AstExpression transformTernary(AstOperatorTernary node) {
        return node.getCondition() instanceof AstOperatorInList inListNode
                ? transformInList(inListNode, node.getTrueBranch(), node.getFalseBranch(), inListNode.isNegated())
                : transform(node.getCondition()) instanceof AstOperatorInList inListNode
                ? transformInList(inListNode, node.getTrueBranch(), node.getFalseBranch(), inListNode.isNegated())
                : node;
    }

    private static AstExpression transformUnaryNegation(AstOperatorUnary node) {
        boolean negated = false;
        AstExpression current = node;

        while (true) {
            switch (current) {
                case AstOperatorUnary n when n.getOperation().isBooleanNegation():
                    negated = !negated;
                    current = n.getOperand();
                    break;

                case AstParentheses n:  current = n.getExpression(); break;
                case AstNegatable<?> n: return negated ? n.negate() : (AstExpression) n;
                default:                return node;
            }
        }
    }
}
