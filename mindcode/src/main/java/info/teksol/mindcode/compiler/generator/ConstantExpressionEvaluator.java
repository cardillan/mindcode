package info.teksol.mindcode.compiler.generator;

import info.teksol.emulator.MindustryObject;
import info.teksol.emulator.MindustryString;
import info.teksol.emulator.MindustryVariable;
import info.teksol.emulator.processor.ExpressionEvaluator;
import info.teksol.emulator.processor.OperationEval;
import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.mimex.Icons;

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
                MindustryVariable a = variableFromNode("a", evaluateInner(node.getLeft()));
                MindustryVariable b = variableFromNode("b", evaluateInner(node.getRight()));
                if (getObject(a) instanceof MindustryString || getObject(b) instanceof MindustryString) {
                    // Only addition a string and a non-null value is supported
                    if (operation == Operation.ADD && a != null && b != null) {
                        return new StringLiteral(node.startToken(), node.sourceFile(), a.print() + b.print());
                    } else {
                        throw new MindcodeException(node.startToken(), "unsupported string expression.");
                    }
                } else if (a != null && b != null) {
                    MindustryVariable result = MindustryVariable.createVar("result");
                    eval.execute(result, a, b);
                    return result.toAstNode();
                } else if (a != null || b != null) {
                    if (getObject(a) instanceof MindustryString || getObject(b) instanceof MindustryString) {
                        throw new MindcodeException(node.startToken(), "unsupported string expression.");
                    }
                    // One of them is not null
                    return evaluatePartially(node, a == null ? b : a, a == null ? node.getLeft() : node.getRight());
                }

            }
        }

        return node;
    }

    private static MindustryObject getObject(MindustryVariable variable) {
        return variable == null ? null : variable.getObject();
    }

    private AstNode evaluatePartially(BinaryOp node, MindustryVariable fixed, AstNode exp) {
        return switch (node.getOp()) {
            // If the fixed value is zero, evaluates to the other node
            case "|" -> fixed.getDoubleValue() == 0 ? exp : node;

            // If the fixed value is zero, evaluates to zero
            case "&" -> fixed.getDoubleValue() == 0 ? new NumericLiteral(node.startToken(), node.sourceFile(), "0") : node;

            // If the fixed value is zero (= false), evaluates to false
            // TODO: return exp instead of node if exp is known to be a boolean expression
            case "or", "||" -> fixed.getDoubleValue() != 0 ? new BooleanLiteral(node.startToken(), node.sourceFile(), true) : node;

            // If the fixed value is zero (= false), evaluates to false
            // TODO: return exp instead of node if exp is known to be a boolean expression
            case "and", "&&" -> fixed.getDoubleValue() == 0 ? new BooleanLiteral(node.startToken(), node.sourceFile(), false) : node;
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
                MindustryVariable a = variableFromNode("a", evaluated.get(0));
                MindustryVariable b = variableFromNode("b", evaluated.get(numArgs - 1));
                if (getObject(a) instanceof MindustryString || getObject(b) instanceof MindustryString) {
                    throw new MindcodeException(node.startToken(), "unsupported string expression.");
                }
                MindustryVariable result = MindustryVariable.createVar("result");
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
            MindustryVariable a = variableFromNode("a", evaluateInner(node.getExpression()));
            if (a != null) {
                if (a.getObject() instanceof MindustryString) {
                    throw new MindcodeException(node.startToken(), "unsupported string expression.");
                }
                MindustryVariable b = MindustryVariable.createVar("temp");
                MindustryVariable result = MindustryVariable.createVar("result");
                eval.execute(result, a, b);
                return result.toAstNode();
            }
        }

        return node;
    }

    private AstNode evaluateVarRef(VarRef node) {
        return constants.getOrDefault(node.getName(), node);
    }

    private static MindustryVariable variableFromNode(String name, AstNode exp) {
        if (exp instanceof NullLiteral n)        return MindustryVariable.createNull();
        if (exp instanceof BooleanLiteral n)     return MindustryVariable.createConst(name, n.getValue());
        if (exp instanceof NumericLiteral n)     return MindustryVariable.createConst( name, n.getAsDouble());
        if (exp instanceof NumericValue n)       return MindustryVariable.createConst(name, n.getAsDouble());
        if (exp instanceof StringLiteral n)      return MindustryVariable.createConstString(n.getText());
        if (exp instanceof ConstantAstNode n)    throw new UnsupportedOperationException("Unhandled constant node " + exp.getClass().getSimpleName());
        if (exp instanceof VarRef n) {
            return Icons.isIconName(n.getName())
                    ? MindustryVariable.createConstString(Icons.getIconValue(n.getName()).format())
                    : null;
        }
        return null;
    }
}
