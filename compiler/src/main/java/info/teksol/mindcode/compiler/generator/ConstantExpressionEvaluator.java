package info.teksol.mindcode.compiler.generator;

import info.teksol.emulator.MindustryObject;
import info.teksol.emulator.MindustryString;
import info.teksol.emulator.MindustryVariable;
import info.teksol.evaluator.ExpressionEvaluator;
import info.teksol.evaluator.LogicOperation;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.Operation;
import info.teksol.mindcode.mimex.Icons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConstantExpressionEvaluator extends AbstractMessageEmitter {

    private final InstructionProcessor instructionProcessor;

    private final Map<String, ConstantAstNode> constants = new HashMap<>();

    public ConstantExpressionEvaluator(InstructionProcessor instructionProcessor, Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
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
        return switch (node) {
            case BinaryOp n     -> evaluateBinaryOp(n);
            case Constant n     -> evaluateConstant(n);
            case FunctionCall n -> evaluateFunctionCall(n);
            case IfExpression n -> evaluateIfExpression(n);
            case UnaryOp n      -> evaluateUnaryOp(n);
            case VarRef n       -> evaluateVarRef(n);
            default             -> node;
        };
    }

    private AstNode evaluateBinaryOp(BinaryOp node) {
        Operation operation = Operation.fromMindcode(node.getOp());
        if (operation.isDeterministic()) {
            LogicOperation eval = ExpressionEvaluator.getOperation(operation);
            if (eval != null) {
                MindustryVariable a = variableFromNode("a", evaluateInner(node.getLeft()));
                MindustryVariable b = variableFromNode("b", evaluateInner(node.getRight()));
                if (getObject(a) instanceof MindustryString || getObject(b) instanceof MindustryString) {
                    // Only addition of a string and a non-null value is supported
                    if (operation == Operation.ADD && a != null && b != null) {
                        return new StringLiteral(node.inputPosition(), a.print(instructionProcessor) + b.print(instructionProcessor));
                    } else {
                        error(node, "Unsupported string expression.");
                        return node;
                    }
                } else if (a != null && b != null) {
                    MindustryVariable result = MindustryVariable.createVar("result");
                    eval.execute(result, a, b);
                    return result.toAstNode();
                } else if (a != null || b != null) {
                    return evaluatePartially(node, a == null ? b : a, a == null ? node.getLeft() : node.getRight());
                }

            }
        }

        return node;
    }

    private static MindustryObject getObject(MindustryVariable variable) {
        return variable == null ? null : variable.getObject();
    }

    private AstNode evaluatePartially(BinaryOp original, MindustryVariable fixed, AstNode other) {
        // Note: fixed.getDoubleValue() is 0 when fixed represents null

        return switch (original.getOp()) {
            // If the fixed value is zero, evaluates to the other node
            // Nonzero values cannot be resolved
            case "|" -> fixed.getDoubleValue() == 0 ? other : original;

            // Cannot be partially evaluated at all
            case "||" -> original;

            // If the fixed value is nonzero, collapses to true
            // If the fixed value is zero, evaluates to the other node
            case "or" -> fixed.getDoubleValue() != 0 ? new BooleanLiteral(original.inputPosition(), true) : other;

            // If the fixed value is zero, evaluates to false
            // Nonzero values cannot be resolved
            case "&", "&&" -> fixed.getDoubleValue() == 0 ? new BooleanLiteral(original.inputPosition(), false) : original;

            // If the fixed value is zero (= false), evaluates to false
            // If the fixed value is nonzero, evaluates to the other node
            case "and" -> fixed.getDoubleValue() == 0 ? new BooleanLiteral(original.inputPosition(), false) : other;

            default -> original;
        };
    }

    private AstNode evaluateConstant(Constant node) {
        AstNode evaluated = evaluateInner(node.getValue());
        if (evaluated instanceof ConstantAstNode constant) {
            ConstantAstNode result = constant instanceof NumericValue value ? ensureMlog(node, value) : constant;
            constants.put(node.getName(), constant);
        } else {
            error(node, "Value assigned to constant '%s' is not a constant expression.", node.getName());
        }
        return node;
    }

    private NumericLiteral ensureMlog(Constant node, NumericValue value) {
        NumericLiteral numericLiteral = value.toNumericLiteral(instructionProcessor);
        if (numericLiteral == null) {
            error(node, "Value assigned to constant '%s' (%s) doesn't have a valid mlog representation.",
                    node.getName(), value.getAsDouble());
            return new NumericLiteral(node.inputPosition(), "0");
        }
        return numericLiteral;
    }

    private AstNode evaluateFunctionCall(FunctionCall node) {
        Operation operation = Operation.fromMindcode(node.getFunctionName());
        LogicOperation eval = ExpressionEvaluator.getOperation(operation);
        int numArgs = ExpressionEvaluator.getNumberOfArguments(operation);
        if (eval != null && numArgs == node.getArguments().size()) {
            List<ConstantAstNode> evaluated = node.getArguments().stream()
                    .map(FunctionArgument::getExpression)
                    .map(this::evaluateInner)
                    .filter(ConstantAstNode.class::isInstance)
                    .map(ConstantAstNode.class::cast)
                    .toList();

            if (evaluated.size() == numArgs) {
                // All parameters are constant
                // a and b are the same argument for unary functions
                MindustryVariable a = variableFromNode("a", evaluated.getFirst());
                MindustryVariable b = variableFromNode("b", evaluated.getLast());
                if (getObject(a) instanceof MindustryString || getObject(b) instanceof MindustryString) {
                    error(node, "Unsupported string expression.");
                    return node;
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
        LogicOperation eval = ExpressionEvaluator.getOperation(Operation.fromMindcode(node.getOp()));
        if (eval != null) {
            MindustryVariable a = variableFromNode("a", evaluateInner(node.getExpression()));
            if (a != null) {
                if (a.getObject() instanceof MindustryString) {
                    error(node, "Unsupported string expression.");
                    return node;
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
        if (constants.containsKey(node.getName())) {
            return constants.get(node.getName()).withInputPosition(node.inputPosition());
        } else {
            return node;
        }
    }

    private MindustryVariable variableFromNode(String name, AstNode exp) {
        return switch (exp) {
            case NullLiteral n      -> MindustryVariable.createNull();
            case BooleanLiteral n   -> MindustryVariable.createConst(name, n.getValue());
            case NumericLiteral n   -> MindustryVariable.createConst(name, n.getAsDouble());
            case NumericValue n     -> MindustryVariable.createConst(name, n.getAsDouble());
            case StringLiteral n    -> MindustryVariable.createConstString(n.getText());
            case ConstantAstNode n  -> throw new UnsupportedOperationException("Unhandled constant node " + exp.getClass().getSimpleName());
            case VarRef n -> Icons.isIconName(n.getName())
                    ? MindustryVariable.createConstString(Icons.getIconValue(n.getName()).format(instructionProcessor))
                    : null;
            default -> null;
        };
    }
}
