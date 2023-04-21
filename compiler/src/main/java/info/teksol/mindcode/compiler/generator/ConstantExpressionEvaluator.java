package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.processor.DoubleVariable;
import info.teksol.mindcode.processor.ExpressionEvaluator;
import info.teksol.mindcode.processor.OperationEval;
import info.teksol.mindcode.processor.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantExpressionEvaluator {

    private final InstructionProcessor instructionProcessor;

    private final Map<String, AstNode> constants = new HashMap<>();

    public ConstantExpressionEvaluator(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }

    /**
     * If the node can be compile-time evaluated, returns the evaluation, otherwise returns {@code this}.
     * The evaluation can be partial, for example when the IfExpression node has constant condition,
     * it can return just the true/false branch (depending on the compile-time value of the condition)
     * even if those branches aren't constant themselves.
     *
     * @param node node to evaluate
     * @return compile-time evaluation of the node
     */
    public AstNode evaluate(AstNode node) {
        AstNode result = evaluateInner(node);

        if (result instanceof NumericValue value) {
            AstNode literal = value.toNumericLiteral(instructionProcessor);
            return literal != null ? literal : node;
        } else {
            return result;
        }
    }

    private AstNode evaluateInner(AstNode node) {
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
        Operation operation = Operation.fromMindcode(node.getOp());
        if (ExpressionEvaluator.isDeterministic(operation)) {
            OperationEval eval = ExpressionEvaluator.getOperation(operation);
            if (eval != null) {
                Variable a = variableFromNode("a", evaluateInner(node.getLeft()));
                Variable b = variableFromNode("b", evaluateInner(node.getRight()));
                if (a != null && b != null) {
                    Variable result = DoubleVariable.newNullValue(false, "result");
                    eval.execute(result, a, b);
                    return result.toAstNode();
                } else if (a != null || b != null) {
                    // One of them is not null
                    return evaluatePartially(node, a == null ? b : a, a == null ? node.getLeft() : node.getRight());
                }
            }
        }

        return node;
    }

    private AstNode evaluatePartially(BinaryOp node, Variable fixed, AstNode exp) {
        return switch (node.getOp()) {
            // If the fixed value is zero, evaluates to the other node
            case "|" -> fixed.getDoubleValue() == 0 ? exp : node;

            // If the fixed value is zero, evaluates to zero
            case "&" -> fixed.getDoubleValue() == 0 ? new NumericLiteral("0") : node;

            // If the fixed value is zero (= false), evaluates to false
            // TODO: return exp instead of node if exp is known to be a boolean expression
            case "or", "||" -> fixed.getDoubleValue() != 0 ? new BooleanLiteral(true) : node;

            // If the fixed value is zero (= false), evaluates to false
            // TODO: return exp instead of node if exp is known to be a boolean expression
            case "and", "&&" -> fixed.getDoubleValue() == 0 ? new BooleanLiteral(false) : node;
            default -> node;
        };
    }

    private AstNode evaluateConstant(Constant node) {
        AstNode evaluated = evaluateInner(node.getValue());
        if (!(evaluated instanceof ConstantAstNode)) {
            throw new GenerationException("Value assigned to constant [" + node.getName() + "] is not a constant expression.");
        } else if (evaluated instanceof NumericValue value) {
            evaluated = value.toNumericLiteral(instructionProcessor);
            if (evaluated == null) {
                throw new GenerationException("Value assigned to constant [" + node.getName() + "] (" + value.getAsDouble() + ") doesn't have a valid mlog representation.");
            }
        }
        constants.put(node.getName(), evaluated);
        return node;
    }

    private AstNode evaluateFunctionCall(FunctionCall node) {
        Operation operation = Operation.fromMindcode(node.getFunctionName());
        OperationEval eval = ExpressionEvaluator.getOperation(operation);
        int numArgs = ExpressionEvaluator.getNumberOfArguments(operation);
        if (eval != null && numArgs == node.getParams().size()) {
            List<ConstantAstNode> evaluated = node.getParams().stream()
                    .map(this::evaluateInner)
                    .filter(n -> n instanceof ConstantAstNode)
                    .map(n -> (ConstantAstNode) n)
                    .toList();

            if (evaluated.size() == numArgs) {
                // All parameters are constant
                // a and b are the same argument for unary functions
                Variable a = variableFromNode("a", evaluated.get(0));
                Variable b = variableFromNode("b", evaluated.get(numArgs - 1));
                Variable result = DoubleVariable.newNullValue(false, "result");
                eval.execute(result, a, b);
                return result.toAstNode();
            }
        }

        return node;
    }

    private AstNode evaluateIfExpression(IfExpression node) {
        AstNode conditionValue = evaluateInner(node.getCondition());
        if (conditionValue instanceof ConstantAstNode n) {
            boolean isTrue = !ExpressionEvaluator.equals(n.getAsDouble(), 0.0);
            return evaluateInner(isTrue ? node.getTrueBranch() : node.getFalseBranch());
        } else {
            return node;
        }
    }

    private AstNode evaluateUnaryOp(UnaryOp node) {
        OperationEval eval = ExpressionEvaluator.getOperation(Operation.fromMindcode(node.getOp()));
        if (eval != null) {
            Variable a = variableFromNode("a", evaluateInner(node.getExpression()));
            if (a != null) {
                Variable b = DoubleVariable.newNullValue(false, "result");
                Variable result = DoubleVariable.newNullValue(false, "result");
                eval.execute(result, a, b);
                return result.toAstNode();
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
            case NumericValue n     -> DoubleVariable.newDoubleValue(false, name, n.getAsDouble());
            case StringLiteral n    -> DoubleVariable.newStringValue(false, name, n.getText());
            case ConstantAstNode n  -> throw new UnsupportedOperationException("Unhandled constant node " + exp.getClass().getSimpleName());
            default                 -> null;
        };
    }
}
