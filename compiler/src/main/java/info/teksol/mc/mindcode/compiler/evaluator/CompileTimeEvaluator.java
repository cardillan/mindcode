package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicOperation;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// The CompileTimeEvaluator class evaluates parts of an abstract syntax tree - represented by an AstMindcodeNode
/// class - at compile-time. This allows optimization and replacement of constant or partially constant expressions
/// with their evaluated results. It supports operations like numeric and string calculations, conditional expressions
/// and function calls of Mindustry Logic functions.
///
/// Non-deterministic operations or operations with side effects remain unevaluated.
// MUSTDO This method must use Variables instance for handling constants, otherwise it might
//        evaluate a local variable which hides a constant as the constant
@NullMarked
public class CompileTimeEvaluator extends AbstractMessageEmitter {
    private final InstructionProcessor processor;
    private final Map<String, AstLiteral> constants = new HashMap<>();
    private final IdentityHashMap<AstMindcodeNode, AstMindcodeNode> cache = new IdentityHashMap<>();
    private final Variables variables;

    public CompileTimeEvaluator(CompileTimeEvaluatorContext context) {
        super(context.messageConsumer());
        processor = context.instructionProcessor();
        variables = context.variables();
    }


    /// If the node can be compile-time evaluated, returns the evaluation, otherwise returns the node itself.
    /// The evaluation can be partial, for example when the AstIfExpression node has a constant condition,
    /// it can return just the true/false branch (depending on the compile-time value of the condition)
    /// even if those branches aren't constant themselves.
    ///
    /// At this moment, only numeric values and strings are handled. Built-in identifiers cannot be evaluated.
    ///
    /// @param expression node to evaluate
    /// @return compile-time evaluation of the node
    public AstMindcodeNode evaluate(AstMindcodeNode expression) {
        return cache.computeIfAbsent(expression, node -> switch (node) {
            case AstIdentifier n -> evaluateIdentifier(n);
            case AstOperatorUnary n -> evaluateUnaryOp(n);
            case AstOperatorBinary n -> evaluateBinaryOp(n);
            case AstOperatorTernary n -> evaluateTernaryOp(n);
            case AstIfExpression n -> evaluateIfExpression(n);
            case AstParentheses n -> evaluateParentheses(n);
            case AstFunctionCall n when !n.hasObject() -> evaluateFunctionCall(n);
            default -> node;
        });
    }


    /// Purges all nodes in and their children from cache. If these nodes get evaluated again, they
    /// will be resolved from a fresh start. Prevents the compile-time evaluator remembering values
    /// based on constant definitions that might not be valid in a new context.
    public void purgeFromCache(List<? extends AstMindcodeNode> nodes) {
        nodes.forEach(cache.keySet()::remove);
        nodes.forEach(node -> purgeFromCache(node.getChildren()));
    }

    private AstMindcodeNode evaluateParentheses(AstParentheses node) {
        AstMindcodeNode evaluated = evaluate(node.getExpression());
        return evaluated == node.getExpression() ? node : evaluated;
    }

    private AstLiteral ensureMlog(AstConstant node, IntermediateValue value) {
        AstLiteral numericLiteral = value.toNumericLiteral();
        if (numericLiteral == null) {
            error(node, "Value assigned to constant '%s' (%s) doesn't have a valid mlog representation.",
                    node.getConstantName(), value.getAsDouble());
            return new AstLiteralDecimal(node.inputPosition(), "0");
        }
        return numericLiteral;
    }

    private AstMindcodeNode evaluateIdentifier(AstIdentifier node) {
        if (variables.resolveVariable(node) instanceof LogicLiteral literal) {
            return literal.asAstNode(node.inputPosition());
        } else {
            return node;
        }
    }

    private AstMindcodeNode evaluateUnaryOp(AstOperatorUnary node) {
        Operation operation = node.getOperation();
        if (operation.getOperands() == 2) {
            if (operation == Operation.ADD || operation == Operation.SUB) {
                ExpressionValue left = ExpressionValue.zero(processor);
                ExpressionValue right = ExpressionValue.create(processor, evaluate(node.getOperand()));
                if (right.isString()) {
                    error(node, "Unsupported string expression.");
                } else if (right.isValid()) {
                    Result result = new Result();
                    ExpressionEvaluator.getOperation(operation).execute(result, left, right);
                    return result.toAstMindcodeNode(node);
                }
            }
        } else if (operation.isDeterministic()) {
            LogicOperation eval = ExpressionEvaluator.getOperation(node.getOperation());
            if (eval != null) {
                ExpressionValue operand = ExpressionValue.create(processor, evaluate(node.getOperand()));
                if (operand.isString()) {
                    error(node, "Unsupported string expression.");
                } else if (operand.isValid()) {
                    Result result = new Result();
                    eval.execute(result, operand, operand);
                    return result.toAstMindcodeNode(node);
                }
            }
        }
        return node;
    }

    private AstMindcodeNode evaluateBinaryOp(AstOperatorBinary node) {
        Operation operation = node.getOperation();
        if (operation.isDeterministic()) {
            LogicOperation eval = ExpressionEvaluator.getOperation(operation);
            if (eval != null) {
                ExpressionValue left = ExpressionValue.create(processor, evaluate(node.getLeft()));
                ExpressionValue right = ExpressionValue.create(processor, evaluate(node.getRight()));
                if (left.isString() || right.isString()) {
                    // Only addition of a string and a non-null value is supported
                    if (operation == Operation.ADD && !left.isNull() && !right.isNull()) {
                        return new AstLiteralString(node.inputPosition(), left.print() + right.print());
                    } else {
                        error(node, "Unsupported string expression.");
                        return node;
                    }
                } else if (left.isValid() && right.isValid()) {
                    Result result = new Result();
                    eval.execute(result, left, right);
                    return result.toAstMindcodeNode(node);
                } else if (left.isValid() != right.isValid()) {
                    // We know just one of them is valid
                    return evaluatePartially(node, left.isValid() ? left : right, left.isValid() ? node.getRight() : node.getLeft());
                }
            }
        }

        return node;
    }

    private AstMindcodeNode evaluatePartially(AstOperatorBinary original, ExpressionValue fixed, AstMindcodeNode other) {
        // Note: getDoubleValue() is 0 when ExpressionValue represents null
        // TODO After introducing types, additional partial evaluations will become possible for integer values

        return switch (original.getOperation()) {
            // Can't evaluate bitwise or, as it truncates the other operand if one is zero.
            case BITWISE_OR -> original;

            // If the fixed value is nonzero, collapses to true
            // Can be applied to BOOLEAN_OR: if the expression can be compile-time evaluated, it doesn't have side effects.
            case BOOLEAN_OR, LOGICAL_OR ->
                    fixed.getLongValue() != 0 ? new AstLiteralBoolean(original.inputPosition(), true) : original;

            // If the fixed value is zero, evaluates to 0
            case BITWISE_AND ->
                    fixed.getDoubleValue() == 0 ? new AstLiteralDecimal(original.inputPosition(), "0") : original;

            // If the fixed value is zero, evaluates to false
            // Can be applied to BOOLEAN_AND: if the expression can be compile-time evaluated, it doesn't have side effects.
            case BOOLEAN_AND, LOGICAL_AND ->
                    fixed.getDoubleValue() == 0 ? new AstLiteralBoolean(original.inputPosition(), false) : original;

            default -> original;
        };
    }

    private AstMindcodeNode evaluateTernaryOp(AstOperatorTernary node) {
        AstMindcodeNode conditionValue = evaluate(node.getCondition());
        if (conditionValue instanceof AstLiteral n) {
            return evaluate(ExpressionEvaluator.isTrue(n.getDoubleValue()) ? node.getTrueBranch() : node.getFalseBranch());
        } else {
            return node;
        }
    }

    private AstMindcodeNode evaluateIfExpression(AstIfExpression node) {
        // AstIfExpression may have multiple if branches and an else branch
        // We're going through all if branches and evaluate their conditions:
        // - true: the body of the if branch is selected,
        // - false: the next if branch is evaluated,
        // - indeterminate: the expression can't be completely evaluated, but we know that
        //   none of the previous if branches gets executed and strip them away.
        // When all if branches' conditions evaluate to false, the else branch is selected.
        int size = node.getIfBranches().size();
        for (int i = 0; i < size; i++) {
            AstIfBranch branch = node.getIfBranches().get(i);
            AstMindcodeNode conditionValue = evaluate(branch.getCondition());
            if (conditionValue instanceof AstLiteralNumeric n) {
                // When true, return the branch's body
                if (ExpressionEvaluator.isTrue(n.getDoubleValue())) {
                    return evaluateBody(branch.getBody());
                }

                //  When false, continue evaluating the other branches
            } else {
                // Can't determine the value of this branch.
                // Pell away the branches known to be false
                return i == 0 ? node : new AstIfExpression(node.inputPosition(),
                        node.getIfBranches().subList(i, size), node.getElseBranch());
            }
        }
        // All if branch conditions evaluated to false
        // The result is the else branch
        return evaluateBody(node.getElseBranch());
    }

    private AstMindcodeNode evaluateBody(List<AstMindcodeNode> body) {
        return body.size() == 1 ? evaluate(body.getFirst())
                : new AstStatementList(body.isEmpty() ? InputPosition.EMPTY : body.getFirst().inputPosition(), body);
    }

    private AstMindcodeNode evaluateFunctionCall(AstFunctionCall node) {
        if (node.getFunctionName().equals("packcolor")) {
            return evaluatePackColor(node);
        }

        Operation operation = Operation.fromMindcode(node.getFunctionName());
        LogicOperation eval = info.teksol.mc.evaluator.ExpressionEvaluator.getOperation(operation);
        int numArgs = info.teksol.mc.evaluator.ExpressionEvaluator.getNumberOfArguments(operation);
        if (eval != null && numArgs == node.getArguments().size()) {
            List<AstLiteral> evaluated = evaluateArguments(node);
            if (evaluated.size() == numArgs) {
                // All parameters are constant
                // left and right are the same argument for unary functions
                ExpressionValue left = ExpressionValue.create(processor, evaluated.getFirst());
                ExpressionValue right = ExpressionValue.create(processor, evaluated.getLast());
                if (left.isString() || right.isString()) {
                    error(node, "Unsupported string expression.");
                    return node;
                }
                Result result = new Result();
                eval.execute(result, left, right);
                return result.toAstMindcodeNode(node);
            }
        }

        return node;
    }

    private AstMindcodeNode evaluatePackColor(AstFunctionCall node) {
        if (node.getArguments().size() == 4) {
            List<AstLiteral> evaluated = evaluateArguments(node);
            if (evaluated.size() == 4) {
                String literal = Color.toColorLiteral(
                        evaluated.get(0).getDoubleValue(),
                        evaluated.get(1).getDoubleValue(),
                        evaluated.get(2).getDoubleValue(),
                        evaluated.get(3).getDoubleValue());
                return new AstLiteralColor(node.inputPosition(), literal);
            }
        }

        return node;
    }

    private List<AstLiteral> evaluateArguments(AstFunctionCall node) {
        return node.getArguments().stream()
                .map(AstFunctionArgument::getExpression)
                .filter(Objects::nonNull)
                .map(this::evaluate)
                .filter(AstLiteral.class::isInstance)
                .map(AstLiteral.class::cast)
                .toList();
    }


    private boolean isString(@Nullable LogicReadable variable) {
        return variable != null && variable.getObject() instanceof String;
    }

    private String print(LogicReadable variable) {
        return variable.getObject() instanceof String string ? string
                : processor.formatNumber(variable.getDoubleValue());
    }
}