package info.teksol.mindcode.v3.compiler.evaluator;

import info.teksol.evaluator.ExpressionEvaluator;
import info.teksol.evaluator.LogicOperation;
import info.teksol.evaluator.LogicReadable;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The CompileTimeEvaluator class evaluates parts of an abstract syntax tree - represented by an AstMindcodeNode
 * class - at compile-time. This allows optimization and replacement of constant or partially constant expressions
 * with their evaluated results. It supports operations like numeric and string calculations, conditional expressions
 * and function calls of Mindustry Logic functions.
 * <p>
 * Non-deterministic operations or operations with side effects remain unevaluated.
 */
@NullMarked
public class CompileTimeEvaluator extends AbstractMessageEmitter {
    private final InstructionProcessor processor;

    private final Map<String, AstLiteral> constants = new HashMap<>();

    public CompileTimeEvaluator(CompileTimeEvaluatorContext context) {
        super(context.messageConsumer());
        this.processor = context.instructionProcessor();
    }

    /**
     * If the node can be compile-time evaluated, returns the evaluation, otherwise returns the node itself.
     * The evaluation can be partial, for example when the AstIfExpression node has a constant condition,
     * it can return just the true/false branch (depending on the compile-time value of the condition)
     * even if those branches aren't constant themselves.
     * <p>
     * At this moment, only numeric values and strings are handled. Built-in identifiers cannot be evaluated.
     *
     * @param node node to evaluate
     * @return compile-time evaluation of the node
     */
    public AstMindcodeNode evaluate(AstMindcodeNode node) {
        return switch (node) {
            case AstConstant n          -> evaluateConstant(n);
            case AstIdentifier n        -> evaluateIdentifier(n);
            case AstOperatorUnary n     -> evaluateUnaryOp(n);
            case AstOperatorBinary n    -> evaluateBinaryOp(n);
            case AstOperatorTernary n   -> evaluateTernaryOp(n);
            case AstIfExpression n      -> evaluateIfExpression(n);
            case AstFunctionCall n when !n.hasObject() -> evaluateFunctionCall(n);
            default                     -> node;
        };
    }

    // TODO When implementing namespaces, constant management will have to be moved to the
    //      class responsible for variable management.
    private AstMindcodeNode evaluateConstant(AstConstant node) {
        AstMindcodeNode evaluated = evaluate(node.getValue());
        if (evaluated instanceof AstLiteral constant) {
            AstLiteral result = constant instanceof IntermediateValue value ? ensureMlog(node, value) : constant;
            constants.put(node.getConstantName(), constant);
        } else {
            error(node, "Value assigned to constant '%s' is not a constant expression.", node.getConstantName());
        }
        return node;
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

    // TODO When implementing namespaces, variable management class will have to be queried for known constants.
    private AstMindcodeNode evaluateIdentifier(AstIdentifier node) {
        if (constants.containsKey(node.getName())) {
            return constants.get(node.getName()).withInputPosition(node.inputPosition());
        } else {
            return node;
        }
    }

    private AstMindcodeNode evaluateUnaryOp(AstOperatorUnary node) {
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
            case BOOLEAN_OR, LOGICAL_OR -> fixed.getLongValue() != 0 ? new AstLiteralBoolean(original.inputPosition(), true) : original;

            // If the fixed value is zero, evaluates to 0
            case BITWISE_AND -> fixed.getDoubleValue() == 0 ? new AstLiteralDecimal(original.inputPosition(), "0") : original;

            // If the fixed value is zero, evaluates to false
            // Can be applied to BOOLEAN_AND: if the expression can be compile-time evaluated, it doesn't have side effects.
            case BOOLEAN_AND, LOGICAL_AND -> fixed.getDoubleValue() == 0 ? new AstLiteralBoolean(original.inputPosition(), false) : original;

            default -> original;
        };
    }

    private AstMindcodeNode evaluateTernaryOp(AstOperatorTernary node) {
        AstMindcodeNode conditionValue = evaluate(node.getCondition());
        if (conditionValue instanceof AstLiteralNumeric n) {
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
        Operation operation = Operation.fromMindcode(node.getName());
        LogicOperation eval = info.teksol.evaluator.ExpressionEvaluator.getOperation(operation);
        int numArgs = info.teksol.evaluator.ExpressionEvaluator.getNumberOfArguments(operation);
        if (eval != null && numArgs == node.getArguments().size()) {
            List<AstLiteral> evaluated = node.getArguments().stream()
                    .map(AstFunctionArgument::getExpression)
                    .filter(Objects::nonNull)
                    .map(this::evaluate)
                    .filter(AstLiteral.class::isInstance)
                    .map(AstLiteral.class::cast)
                    .toList();

            if (evaluated.size() == numArgs) {
                // All parameters are constant
                // left and right are the same argument for unary functions
                ExpressionValue left = ExpressionValue.create(processor, evaluated.getFirst());
                ExpressionValue right = ExpressionValue.create(processor, evaluated.getLast());
                if (left.isString()  || right.isString()) {
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

    private boolean isString(@Nullable LogicReadable variable) {
        return variable != null && variable.getObject() instanceof String;
    }

    private String print(LogicReadable variable) {
        return variable.getObject() instanceof String string ? string
                : processor.formatNumber(variable.getDoubleValue());
    }
}
