package io.github.cardillan.mindcode.compiler.generation;

import io.github.cardillan.mindcode.common.SourceElement;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.compiler.ast.nodes.*;
import io.github.cardillan.mindcode.logic.arguments.Operation;
import io.github.cardillan.mindcode.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class NodeTransformation {
    private static final AstIdentifier INVALID = new AstIdentifier(SourcePosition.EMPTY, "");

    public static AstMindcodeNode transform(AstMindcodeNode node) {
        return node instanceof AstExpression expression ? transform(expression, true) : node;
    }

    public static AstExpression transform(AstExpression expression, boolean topLevel) {
        return switch (expression) {
            case AstIfExpression node       -> transformIfExpression(node, topLevel);
            case AstOperatorInList node     -> transformInList(node);
            case AstOperatorInRange node    -> transformInRange(node);
            case AstOperatorTernary node    -> transformTernary(node);
            case AstOperatorUnary node      -> transformUnaryNegation(node);
            case AstParentheses node        -> transform(node.getExpression(), topLevel);
            default -> expression;
        };
    }


    // TODO In the past, elsif branches were emulated as nested else if expressions.
    //      Code optimizers might therefore have problems if we encoded elsif branches more effectively.
    //      Change the implementation to a more efficient one and update the optimizers.
    private static AstExpression transformIfExpression(AstIfExpression node, boolean topLevel) {
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

        return topLevel ? transformTernary(result) : result;
    }

    private static AstMindcodeNode repackageBody(AstIfBranch branch, List<AstMindcodeNode> expressions) {
        SourcePosition position = branch.getCondition().sourcePosition();
        if (expressions.size() == 1 && expressions.getFirst() instanceof AstExpression exp)
            return transform(exp, false);

        AstStatementList result = new AstStatementList(expressions.isEmpty() ? position : expressions.getFirst().sourcePosition(), expressions);
        result.setProfile(branch.getProfile());
        return result;
    }

    private static SourcePosition pos(List<? extends SourceElement> elements) {
        return elements.getFirst().sourcePosition().upTo(elements.getLast().sourcePosition());
    }

    private static AstExpression transformInList(AstOperatorInList node) {
        if (node.getValues().size() == 1 && node.getValues().getFirst() instanceof AstRange range) {
            AstOperatorInRange rangeNode = new AstOperatorInRange(node.sourcePosition(), node.isNegated(), node.getValue(), range);
            rangeNode.setProfile(node.getProfile());
            return transformInRange(rangeNode);
        }

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
                            Operation.LESS_THAN, cached, node.getRange().getFirst()),
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            AbstractCodeBuilder.outsideRangeCondition(node.getRange()).toOperation(),
                            cached, node.getRange().getLast()));
        } else {
            result = new AstOperatorShortCircuiting(node.sourcePosition(),
                    Operation.LOGICAL_AND,
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            Operation.GREATER_THAN_EQ, cached, node.getRange().getFirst()),
                    new AstOperatorBinary(node.getRange().sourcePosition(),
                            AbstractCodeBuilder.insideRangeCondition(node.getRange()).toOperation(),
                            cached, node.getRange().getLast()));
        }

        result.setProfile(node.getProfile());
        result.getLeft().setProfile(node.getProfile());
        result.getRight().setProfile(node.getProfile());
        return result;
    }

    private static AstExpression transformTernary(AstOperatorTernary node) {
        if (node.getCondition() instanceof AstOperatorInList inListNode) {
            return transformInList(inListNode, node.getTrueBranch(), node.getFalseBranch(), inListNode.isNegated());
        }

        if (transform(node.getCondition(), false) instanceof AstOperatorInList inListNode) {
            return transformInList(inListNode, node.getTrueBranch(), node.getFalseBranch(), inListNode.isNegated());
        }

        List<AstExpression> values = new ArrayList<>();

        AstIdentifier decisionVariable = extractDecisionVariable(node.getCondition(), values);
        if (decisionVariable == INVALID) return node;

        // Initialize
        List<IfBranch> branches = new ArrayList<>();
        branches.add(new IfBranch(node.sourcePosition(), node.getCondition(), node.getTrueBranch(), node.getProfile()));
        AstMindcodeNode elseBranch = node.getFalseBranch();

        while (elseBranch instanceof AstOperatorTernary ternary) {
            AstIdentifier variable = extractDecisionVariable(ternary.getCondition(), values);
            if (!variable.equals(decisionVariable)) break;

            branches.add(new IfBranch(ternary.sourcePosition(), ternary.getCondition(), ternary.getTrueBranch(), ternary.getProfile()));
            elseBranch = ternary.getFalseBranch();
        }

        if (values.size() < 2) return node;

        List<AstCaseAlternative> alternatives = branches.stream().map(NodeTransformation::convertToWhenBranch).toList();
        AstCaseExpression caseStatement = new AstCaseExpression(node.sourcePosition(), decisionVariable, alternatives, List.of(elseBranch), true);
        caseStatement.setProfile(node.getProfile());
        return caseStatement;
    }

    private static AstExpression transformUnaryNegation(AstOperatorUnary node) {
        boolean negated = false;
        AstExpression current = node;

        while (true) {
            switch (current) {
                case AstOperatorUnary n when n.getOperation().isBooleanNegation() -> {
                    negated = !negated;
                    current = n.getOperand();
                }
                case AstParentheses n -> current = n.getExpression();
                case AstNegatable<?> n -> {
                    return negated ? n.negate() : (AstExpression) n;
                }
                default -> {
                    return node;
                }
            }
        }
    }

    private record IfBranch(SourcePosition sourcePosition, AstExpression condition, AstMindcodeNode branch, CompilerProfile profile) {}

    private static AstIdentifier extractDecisionVariable(AstExpression condition, List<AstExpression> values) {
        List<AstExpression> conditions = new ArrayList<>();
        unwrapOr(condition, conditions);

        AstIdentifier result = extractDecisionVariableSingle(conditions.getFirst(), values);
        if (result == INVALID) {
            return INVALID;
        }

        for (int i = 1; i < conditions.size(); i++) {
            AstIdentifier variable = extractDecisionVariableSingle(conditions.get(i), values);
            if (!result.equals(variable)) {
                return INVALID;
            }
        }

        return result;
    }

    private static AstIdentifier extractDecisionVariableSingle(AstExpression condition, List<AstExpression> values) {
        switch (condition) {
            case AstOperatorBinary op when op.getOperation() == Operation.EQUAL -> {
                if (op.getLeft() instanceof AstIdentifier id && builtinOrLiteral(op.getRight())) {
                    values.add(op.getRight());
                    return id;
                } else if (op.getRight() instanceof AstIdentifier id && builtinOrLiteral(op.getLeft())) {
                    values.add(op.getLeft());
                    return id;
                }
            }
            case AstOperatorInRange op -> {
                if (op.getValue() instanceof AstIdentifier id && op.getRange().getFirst() instanceof AstLiteralNumeric
                        && op.getRange().getLast() instanceof AstLiteralNumeric) {
                    values.add(op.getRange());
                    return id;
                }
            }
            case AstOperatorInList op -> {
                if (op.getValue() instanceof AstIdentifier id && op.getValues().stream().allMatch(v -> v instanceof AstLiteralNumeric)) {
                    values.addAll(op.getValues());
                    return id;
                }
            }
            default -> {
            }
        }
        return INVALID;
    }

    private static void unwrapOr(AstExpression condition, List<AstExpression> conditions) {
        if (condition instanceof AstOperatorShortCircuiting op && op.getOperation() == Operation.LOGICAL_OR) {
            unwrapOr(op.getLeft(), conditions);
            unwrapOr(op.getRight(), conditions);
        } else {
            conditions.add(condition);
        }
    }

    private static boolean builtinOrLiteral(AstExpression value) {
        return value instanceof AstLiteral || value instanceof AstBuiltInIdentifier;
    }

    private static AstCaseAlternative convertToWhenBranch(IfBranch branch) {
        List<AstExpression> conditions = new ArrayList<>();
        unwrapOr(branch.condition, conditions);

        List<AstExpression> whenValues = new ArrayList<>();
        for (AstExpression condition : conditions) {
            switch (condition) {
                case AstOperatorBinary op when op.getOperation() == Operation.EQUAL ->
                        whenValues.add(op.getLeft() instanceof AstIdentifier ? op.getRight() : op.getLeft());
                case AstOperatorInRange op -> whenValues.add(op.getRange());
                case AstOperatorInList op -> whenValues.addAll(op.getValues());
                default -> {
                    throw new MindcodeInternalError("Unexpected condition: " + condition);
                }
            }
        }

        AstCaseAlternative caseAlternative = new AstCaseAlternative(branch.sourcePosition, whenValues, List.of(branch.branch));
        caseAlternative.setProfile(branch.profile);
        return caseAlternative;
    }
}
