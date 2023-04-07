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
            Variable a = variableFromNode("a", evaluate(node.getLeft()));
            Variable b = variableFromNode("b", evaluate(node.getRight()));
            if (a != null && b != null) {
                Variable result = DoubleVariable.newNullValue(false, "result");
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
                return result.toAstNode();
            }
        }

        return node;
    }

    private AstNode evaluateIfExpression(IfExpression node) {
        AstNode conditionValue = evaluate(node.getCondition());
        if (conditionValue instanceof ConstantAstNode) {
            boolean isTrue = !ExpressionEvaluator.equals(((ConstantAstNode) conditionValue).getAsDouble(), 0.0);
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
                return result.toAstNode();
            }
        }

        return node;
    }

    private AstNode evaluateVarRef(VarRef node) {
        return constants.getOrDefault(node.getName(), node);
    }

    private static DoubleVariable variableFromNode(String name, AstNode exp) {
        if (exp instanceof NullLiteral) {
            return DoubleVariable.newNullValue(false, name);
        } else if (exp instanceof BooleanLiteral) {
            return DoubleVariable.newBooleanValue(false, name, ((BooleanLiteral)exp).getValue());
        } else if (exp instanceof NumericLiteral) {
            return DoubleVariable.newDoubleValue(false, name, ((NumericLiteral)exp).getAsDouble());
        } else if (exp instanceof StringLiteral) {
            return DoubleVariable.newStringValue(false, name, ((StringLiteral)exp).getText());
        } else if (exp instanceof ConstantAstNode) {
            throw new UnsupportedOperationException("Unhandled constant node " + exp.getClass().getSimpleName());
        } else {
            // Not a constant expression
            return null;
        }
    }
}
