package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.processor.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ConstantExpressionEvaluator {
    private final Map<String, AstNode> constants = new HashMap<>();

    /**
     * If the node can be compile-time evaluated, returns the evaluation, otherwise returns this.
     * The evaluation can be partial, for example when the IfExpression node has constant condition,
     * it can return just the true/false branch (depending on the compile-time value of the condition)
     * even if those branches aren't constant themselves.
     *
     * @param node node to evaluate
     * @return compile-time evaluation of the node
     */
    public AstNode evaluate(AstNode node) {
        return switch (node) {
            case BinaryOp n         -> evaluateBinaryOp(n);
            case Constant n         -> evaluateConstant(n);
            case FunctionCall n     -> evaluateFunctionCall(n);
            case IfExpression n     -> evaluateIfExpression(n);
            case UnaryOp n          -> evaluateUnaryOp(n);
            case VarRef n           -> evaluateVarRef(n);
            default                 -> node;
        };
    }

    private AstNode evaluateBinaryOp(BinaryOp node) {
        String machineCode = ExpressionEvaluator.translateOperator(node.getOp());
        if (ExpressionEvaluator.isDeterministic(machineCode)) {
            Operation operation = ExpressionEvaluator.getOperation(machineCode);
            if (operation != null) {
                Variable a = variableFromNode("a", evaluate(node.getLeft()));
                Variable b = variableFromNode("b", evaluate(node.getRight()));
                if (a != null && b != null) {
                    Variable result = DoubleVariable.newNullValue(false, "result");
                    operation.execute(result, a, b);
                    return result.toAstNode().orElse(node);
                } else if (a != null || b != null) {
                    // One of them is not null
                    return evaluatePartially(node, a == null ? b : a, a == null ? node.getLeft() : node.getRight());
                }
            }
        }

        return node;
    }

    private AstNode evaluatePartially(BinaryOp node, Variable fixed, AstNode exp) {
        switch (node.getOp()) {
            case "|":
                // If the fixed value is zero, evaluates to the other node
                return fixed.getDoubleValue() == 0 ? exp : node;

            case "&":
                // If the fixed value is zero, evaluates to zero
                return fixed.getDoubleValue() == 0 ? new NumericLiteral("0") : node;

            case "or":
            case "||":
                // If the fixed value is zero (= false), evaluates to false
                // TODO: return exp instead of node if exp is known to be a boolean expression
                return fixed.getDoubleValue() != 0 ? new BooleanLiteral(true) : node;

            case "and":
            case "&&":
                // If the fixed value is zero (= false), evaluates to false
                // TODO: return exp instead of node if exp is known to be a boolean expression
                return fixed.getDoubleValue() == 0 ? new BooleanLiteral(false) : node;
        }

        return node;
    }

    private AstNode evaluateConstant(Constant node) {
        AstNode evaluated = evaluate(node.getValue());
        if (!(evaluated instanceof ConstantAstNode)) {
            throw new GenerationException("Value assigned to constant [" + node.getName() + "] is not a constant expression.");
        }
        constants.put(node.getName(), evaluated);
        return node;
    }

    private AstNode evaluateFunctionCall(FunctionCall node) {
        Operation operation = ExpressionEvaluator.getOperation(node.getFunctionName());
        int numArgs = ExpressionEvaluator.getNumberOfArguments(node.getFunctionName());
        if (operation != null && numArgs == node.getParams().size()) {
            List<ConstantAstNode> evaluated = node.getParams().stream()
                    .map(this::evaluate)
                    .filter(n -> n instanceof ConstantAstNode)
                    .map(n -> (ConstantAstNode) n)
                    .collect(Collectors.toList());

            if (evaluated.size() == numArgs) {
                // All parameters are constant
                // a and b are the same argument for unary functions
                Variable a = variableFromNode("a", evaluated.get(0));
                Variable b = variableFromNode("b", evaluated.get(numArgs - 1));
                Variable result = DoubleVariable.newNullValue(false, "result");
                operation.execute(result, a, b);
                return result.toAstNode().orElse(node);
            }
        }

        return node;
    }

    private AstNode evaluateIfExpression(IfExpression node) {
        AstNode conditionValue = evaluate(node.getCondition());
        if (conditionValue instanceof ConstantAstNode n) {
            boolean isTrue = !ExpressionEvaluator.equals(n.getAsDouble(), 0.0);
            return evaluate(isTrue ? node.getTrueBranch() : node.getFalseBranch());
        } else {
            return node;
        }
    }

    private AstNode evaluateUnaryOp(UnaryOp node) {
        Operation operation = ExpressionEvaluator.getOperation(ExpressionEvaluator.translateOperator(node.getOp()));
        if (operation != null) {
            Variable a = variableFromNode("a", evaluate(node.getExpression()));
            if (a != null) {
                Variable b = DoubleVariable.newNullValue(false, "result");
                Variable result = DoubleVariable.newNullValue(false, "result");
                operation.execute(result, a, b);
                return result.toAstNode().orElse(node);
            }
        }

        return node;
    }

    private AstNode evaluateVarRef(VarRef node) {
        return constants.getOrDefault(node.getName(), node);
    }

    private static DoubleVariable variableFromNode(String name, AstNode exp) {
        return switch (exp) {
            case NullLiteral n      -> DoubleVariable.newNullValue(false, name);
            case BooleanLiteral n   -> DoubleVariable.newBooleanValue(false, name, n.getValue());
            case NumericLiteral n   -> DoubleVariable.newDoubleValue(false, name, n.getAsDouble());
            case StringLiteral n    -> DoubleVariable.newStringValue(false, name, n.getText());
            case ConstantAstNode n  -> throw new UnsupportedOperationException("Unhandled constant node " + exp.getClass().getSimpleName());
            default                 -> null;
        };
    }
}
