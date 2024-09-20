package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.mimex.Icons;
import info.teksol.mindcode.processor.*;

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
     * If the node can be compile-time evaluated, returns the evaluation, otherwise returns the node itself.
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
        if (node instanceof BinaryOp n)       return evaluateBinaryOp(n);
        if (node instanceof Constant n)       return evaluateConstant(n);
        if (node instanceof FunctionCall n)   return evaluateFunctionCall(n);
        if (node instanceof IfExpression n)   return evaluateIfExpression(n);
        if (node instanceof UnaryOp n)        return evaluateUnaryOp(n);
        if (node instanceof VarRef n)         return evaluateVarRef(n);
        return node;
    }

    private AstNode evaluateBinaryOp(BinaryOp node) {
        Operation operation = Operation.fromMindcode(node.getOp());
        if (operation.isDeterministic()) {
            OperationEval eval = ExpressionEvaluator.getOperation(operation);
            if (eval != null) {
                Variable a = variableFromNode("a", evaluateInner(node.getLeft()));
                Variable b = variableFromNode("b", evaluateInner(node.getRight()));
                if (a != null && b != null) {
                    if (operation == Operation.ADD && (a instanceof StringVariable || b instanceof StringVariable)) {
                        String concat = a.toString().concat(b.toString());
                        return new StringLiteral(node.startToken(), concat);
                    } else {
                        Variable result = DoubleVariable.newNullValue(false, "result");
                        eval.execute(result, a, b);
                        return result.toAstNode();
                    }
                } else if (a != null || b != null) {
                    if (a instanceof StringVariable || b instanceof StringVariable) {
                        throw new MindcodeException(node.startToken(), "unsupported string expression.");
                    }
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
            case "&" -> fixed.getDoubleValue() == 0 ? new NumericLiteral(node.startToken(), "0") : node;

            // If the fixed value is zero (= false), evaluates to false
            // TODO: return exp instead of node if exp is known to be a boolean expression
            case "or", "||" -> fixed.getDoubleValue() != 0 ? new BooleanLiteral(node.startToken(), true) : node;

            // If the fixed value is zero (= false), evaluates to false
            // TODO: return exp instead of node if exp is known to be a boolean expression
            case "and", "&&" -> fixed.getDoubleValue() == 0 ? new BooleanLiteral(node.startToken(), false) : node;
            default -> node;
        };
    }

    private AstNode evaluateConstant(Constant node) {
        AstNode evaluated = evaluateInner(node.getValue());
        if (!(evaluated instanceof ConstantAstNode)) {
            throw new MindcodeException(node.startToken(), "value assigned to constant '%s' is not a constant expression.", node.getName());
        } else if (evaluated instanceof NumericValue value) {
            evaluated = value.toNumericLiteral(instructionProcessor);
            if (evaluated == null) {
                throw new MindcodeException(node.startToken(), "value assigned to constant '%s' (%s) doesn't have a valid mlog representation.",
                        node.getName(), value.getAsDouble());
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
                    .filter(ConstantAstNode.class::isInstance)
                    .map(ConstantAstNode.class::cast)
                    .toList();

            if (evaluated.size() == numArgs) {
                // All parameters are constant
                // a and b are the same argument for unary functions
                Variable a = variableFromNode("a", evaluated.get(0));
                Variable b = variableFromNode("b", evaluated.get(numArgs - 1));
                if (a instanceof StringVariable || b instanceof StringVariable) {
                    throw new MindcodeException(node.startToken(), "unsupported string expression.");
                }
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
                if (a instanceof StringVariable) {
                    throw new MindcodeException(node.startToken(), "unsupported string expression.");
                }
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

    private static Variable variableFromNode(String name, AstNode exp) {
        if (exp instanceof NullLiteral n)        return DoubleVariable.newNullValue(false, name);
        if (exp instanceof BooleanLiteral n)     return DoubleVariable.newBooleanValue(false, name, n.getValue());
        if (exp instanceof NumericLiteral n)     return DoubleVariable.newDoubleValue(false, name, n.getAsDouble());
        if (exp instanceof NumericValue n)       return DoubleVariable.newDoubleValue(false, name, n.getAsDouble());
        if (exp instanceof StringLiteral n)      return StringVariable.newStringValue(false, name, n.getText());
        if (exp instanceof ConstantAstNode n)    throw new UnsupportedOperationException("Unhandled constant node " + exp.getClass().getSimpleName());
        if (exp instanceof VarRef n) {
            return Icons.isIconName(n.getName())
                    ? StringVariable.newStringValue(false, n.getName(), Icons.getIconValue(n.getName()).format())
                    : null;
        }
        return null;
    }
}
