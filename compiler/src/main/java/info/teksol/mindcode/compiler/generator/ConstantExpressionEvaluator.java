package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.processor.DoubleVariable;
import info.teksol.mindcode.processor.ExpressionEvaluator;
import info.teksol.mindcode.processor.Operation;
import info.teksol.mindcode.processor.Variable;
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
        if (node instanceof BinaryOp) {
            return evaluateBinaryOp((BinaryOp) node);
        } else if (node instanceof Constant) {
            return evaluateConstant((Constant) node);
        } else if (node instanceof FunctionCall) {
            return evaluateFunctionCall((FunctionCall) node);
        } else if (node instanceof IfExpression) {
            return evaluateIfExpression((IfExpression) node);
        } else if (node instanceof UnaryOp) {
            return evaluateUnaryOp((UnaryOp) node);
        } else if (node instanceof VarRef) {
            return evaluateVarRef((VarRef) node);
        } else {
            return node;
        }
    }

    private AstNode evaluateBinaryOp(BinaryOp node) {
        Operation operation = ExpressionEvaluator.getOperation(ExpressionEvaluator.translateOperator(node.getOp()));
        if (operation != null) {
            AstNode first = evaluate(node.getLeft());
            AstNode second = evaluate(node.getRight());

            if (first instanceof ConstantExpression && second instanceof ConstantExpression) {
                Variable a = new DoubleVariable("a", ((ConstantExpression) first).getAsDouble());
                Variable b = new DoubleVariable("b", ((ConstantExpression) second).getAsDouble());
                Variable result = new DoubleVariable("result", null);
                operation.execute(result, a, b);
                return result.toAstNode();
            }
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
            List<ConstantExpression> evaluated = node.getParams().stream()
                    .map(this::evaluate)
                    .filter(n -> n instanceof ConstantExpression)
                    .map(n -> (ConstantExpression) n)
                    .collect(Collectors.toList());

            if (evaluated.size() == numArgs) {
                // All parameters are constant
                Variable a = new DoubleVariable("a", ((ConstantExpression) evaluated.get(0)).getAsDouble());
                Variable b = new DoubleVariable("b", ((ConstantExpression) evaluated.get(numArgs - 1)).getAsDouble());
                Variable result = new DoubleVariable("result", null);
                operation.execute(result, a, b);
                return result.toAstNode();
            }
        }

        return node;
    }

    private AstNode evaluateIfExpression(IfExpression node) {
        AstNode conditionValue = evaluate(node.getCondition());
        if (conditionValue instanceof ConstantExpression) {
            boolean isTrue = !ExpressionEvaluator.equals(((ConstantExpression) conditionValue).getAsDouble(), 0.0);
            return evaluate(isTrue ? node.getTrueBranch() : node.getFalseBranch());
        } else {
            return node;
        }
    }

    private AstNode evaluateUnaryOp(UnaryOp node) {
        Operation operation = ExpressionEvaluator.getOperation(ExpressionEvaluator.translateOperator(node.getOp()));
        if (operation != null) {
            AstNode value = evaluate(node.getExpression());

            if (value instanceof ConstantExpression) {
                Variable a = new DoubleVariable("a", ((ConstantExpression) value).getAsDouble());
                Variable b = new DoubleVariable("b", null);
                Variable result = new DoubleVariable("result", null);
                operation.execute(result, a, b);
                return result.toAstNode();
            }
        }

        return node;
    }

    private AstNode evaluateVarRef(VarRef node) {
        return constants.getOrDefault(node.getName(), node);
    }
}
