package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicOperation;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.BuiltinEvaluation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static info.teksol.mc.messages.ERR.FUNCTION_REQUIRES_TARGET_8;

/// The CompileTimeEvaluator class evaluates parts of an abstract syntax tree - represented by an AstMindcodeNode
/// class - at compile-time. This allows optimization and replacement of constant or partially constant expressions
/// with their evaluated results. It supports operations like numeric and string calculations, conditional expressions
/// and function calls of Mindustry Logic functions.
///
/// Non-deterministic operations or operations with side effects remain unevaluated.
@NullMarked
public class CompileTimeEvaluator extends AbstractMessageEmitter {
    private final BuiltinEvaluation builtinEvaluation;
    private final InstructionProcessor processor;
    private final Map<String, AstLiteral> constants = new HashMap<>();
    private final CallGraph callGraph;
    private final Variables variables;

    private IdentityHashMap<AstMindcodeNode, AstMindcodeNode> cache = new IdentityHashMap<>();
    private final List<Map<AstIdentifier, AstExpression>> functionVariables = new ArrayList<>();

    public CompileTimeEvaluator(CompileTimeEvaluatorContext context) {
        super(context.messageConsumer());
        builtinEvaluation = context.globalCompilerProfile().getBuiltinEvaluation();
        processor = context.instructionProcessor();
        callGraph = context.callGraph();
        variables = context.variables();
    }

    /// Purges all nodes in and their children from the cache. If these nodes get evaluated again, they
    /// will be resolved from a fresh start. Prevents the compile-time evaluator remembering values
    /// based on constant definitions that might not be valid in a new context.
    public void purgeFromCache(List<? extends AstMindcodeNode> nodes) {
        nodes.forEach(cache.keySet()::remove);
        nodes.forEach(node -> purgeFromCache(node.getChildren()));
    }

    /// If the node can be compile-time evaluated, returns the evaluation, otherwise returns the node itself.
    /// The evaluation can be partial, for example, when the AstIfExpression node has a constant condition,
    /// it can return just the true/false branch (depending on the compile-time value of the condition)
    /// even if those branches aren't constant themselves.
    ///
    /// At this moment, only numeric values and strings are handled. Built-in identifiers cannot be evaluated.
    ///
    /// @param node node to evaluate
    /// @param local `true` if the node is being evaluated in a local context
    /// @param requireMlogConstant  `true` if the evaluation is being required for an mlog constant. The value is not
    ///         unwrapped, so that a proper error message can be generated if the value cannot be represented in mlog
    /// @return compile-time evaluation of the node
    public AstMindcodeNode evaluate(AstMindcodeNode node, boolean local, boolean requireMlogConstant) {
        if (node instanceof AstIdentifier || node instanceof AstBuiltInIdentifier) {
            // Identifiers aren't evaluated to prevent a possible loss of precision warning generated when using imprecise constants.
            // Logic builtins aren't evaluated to prevent their conversion to a numeric literal
            return node;
        }

        AstMindcodeNode evaluated = evaluateNode(node, local);

        if (!requireMlogConstant && evaluated instanceof IntermediateValue value) {
            AstLiteral literal = value.toNumericLiteral(processor);
            return literal == null ? node : literal;
        } else {
            return evaluated;
        }
    }

    private AstMindcodeNode evaluateNode(AstMindcodeNode node, boolean local) {
        return cache.computeIfAbsent(node, exp -> switch (exp) {
            case AstIdentifier n -> evaluateIdentifier(n, local);
            case AstOperatorUnary n -> evaluateUnaryOp(n, local);
            case AstOperatorBinary n -> evaluateBinaryOp(n, local);
            case AstOperatorTernary n -> evaluateTernaryOp(n, local);
            case AstIfExpression n -> evaluateIfExpression(n, local);
            case AstParentheses n -> evaluateParentheses(n, local);
            case AstPropertyAccess n -> evaluatePropertyAccess(n, local);
            case AstFunctionCall n when !n.hasObject() -> evaluateFunctionCall(n, local);
            default -> exp;
        });
    }

    private AstMindcodeNode evaluateParentheses(AstParentheses node, boolean local) {
        AstMindcodeNode evaluated = evaluateNode(node.getExpression(), local);
        return evaluated == node.getExpression() ? node : evaluated;
    }

    private AstMindcodeNode evaluateIdentifier(AstIdentifier node, boolean local) {
        if (!functionVariables.isEmpty() && functionVariables.getLast().containsKey(node)) {
            return functionVariables.getLast().get(node);
        } else if (variables.resolveVariable(node, local, true) instanceof LogicLiteral literal) {
            return literal.asAstNode(node.sourcePosition());
        } else {
            return node;
        }
    }

    private AstMindcodeNode evaluateUnaryOp(AstOperatorUnary node, boolean local) {
        Operation operation = node.getOperation();
        LogicOperation eval = ExpressionEvaluator.getOperation(node.getOperation());
        if (operation.isDeterministic() && eval != null) {
            if (operation == Operation.ADD || operation == Operation.SUB || operation.getOperands() == 1) {
                int base = switch (node.getOperand()) {
                    case AstLiteralBinary n -> 2;
                    case AstLiteralHexadecimal n -> 16;
                    default -> 10;
                };
                ExpressionValue right = ExpressionValue.create(builtinEvaluation, processor, evaluateNode(node.getOperand(), local));
                ExpressionValue left = operation.getOperands() == 1 ? right : ExpressionValue.zero(processor);
                if (right.isString()) {
                    error(node, ERR.UNSUPPORTED_STRING_EXPRESSION);
                } else if (right.isValid()) {
                    Result result = new Result();
                    eval.execute(result, left, right);
                    return result.toAstMindcodeNode(processor, node, base);
                }
            }
        }
        return node;
    }

    private AstMindcodeNode evaluateBinaryOp(AstOperatorBinary node, boolean local) {
        Operation operation = node.getOperation();
        if (operation.isDeterministic()) {
            LogicOperation eval = ExpressionEvaluator.getOperation(operation);
            if (eval != null) {
                ExpressionValue left = ExpressionValue.create(builtinEvaluation, processor, evaluateNode(node.getLeft(), local));
                ExpressionValue right = ExpressionValue.create(builtinEvaluation, processor, evaluateNode(node.getRight(), local));
                if (left.isString() || right.isString()) {
                    if (operation.isCompileTimeCondition()) {
                        if (left.isString() && right.isString()) {
                            Result result = new Result();
                            eval.execute(result, left, right);
                            return result.toAstMindcodeNode(processor, node);
                        } else {
                            return node;
                        }
                    } else if (operation == Operation.ADD && !left.isNull() && !right.isNull()) {
                        // Only the addition of a string and a non-null value is supported
                        return new AstLiteralString(node.sourcePosition(), left.print() + right.print());
                    } else {
                        error(node, ERR.UNSUPPORTED_STRING_EXPRESSION);
                        return node;
                    }
                } else if (left.isValid() && right.isValid()) {
                    Result result = new Result();
                    eval.execute(result, left, right);
                    return result.toAstMindcodeNode(processor, node);
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
                    fixed.getLongValue() != 0 ? new AstLiteralBoolean(original.sourcePosition(), true) : original;

            // If the fixed value is zero, evaluates to 0
            case BITWISE_AND ->
                    fixed.getDoubleValue() == 0 ? new AstLiteralDecimal(original.sourcePosition(), "0") : original;

            // If the fixed value is zero, evaluates to false
            // Can be applied to BOOLEAN_AND: if the expression can be compile-time evaluated, it doesn't have side effects.
            case BOOLEAN_AND, LOGICAL_AND ->
                    fixed.getDoubleValue() == 0 ? new AstLiteralBoolean(original.sourcePosition(), false) : original;

            default -> original;
        };
    }

    private AstMindcodeNode evaluateTernaryOp(AstOperatorTernary node, boolean local) {
        AstMindcodeNode conditionValue = evaluateNode(node.getCondition(), local);
        if (conditionValue instanceof AstLiteral n) {
            return evaluateNode(ExpressionEvaluator.isTrue(n.getDoubleValue())
                    ? node.getTrueBranch() : node.getFalseBranch(), local);
        } else {
            return node;
        }
    }

    private AstMindcodeNode evaluateIfExpression(AstIfExpression node, boolean local) {
        // AstIfExpression may have multiple `if` branches and an else branch
        // We're going through all `if` branches and evaluate their conditions:
        // - true: the body of the `if` branch is selected,
        // - false: the next `if` branch is evaluated,
        // - indeterminate: the expression can't be completely evaluated, but we know that
        //   none of the previous `if` branches gets executed and strip them away.
        // When all `if` branches' conditions evaluate to false, the else branch is selected.
        int size = node.getIfBranches().size();
        for (int i = 0; i < size; i++) {
            AstIfBranch branch = node.getIfBranches().get(i);
            AstMindcodeNode conditionValue = evaluateNode(branch.getCondition(), local);
            if (conditionValue instanceof AstLiteralNumeric n) {
                // When true, return the branch's body
                // We need to wrap it in a statement list -- this is a hack to prevent not well-understood problems
                // with case expressions, but the entire conditional compilation should be implemented differently
                // in the future.
                if (ExpressionEvaluator.isTrue(n.getDoubleValue())) {
                    AstMindcodeNode body = evaluateBody(branch.getBody(), local);
                    return new AstStatementList(body.sourcePosition(), List.of(body));
                }

                //  When false, continue evaluating the other branches
            } else {
                // Can't determine the value of this branch.
                // Peel away the branches known to be false
                return i == 0 ? node : new AstIfExpression(node.sourcePosition(),
                        node.getIfBranches().subList(i, size), node.getElseBranch());
            }
        }
        // All `if` branch conditions evaluated to false: the result is the else branch.
        return evaluateBody(node.getElseBranch(), local);
    }

    private static final Map<String, BiFunction<SourcePosition, MindustryContent, AstLiteral>> PROPERTY_MAPPER = Map.of(
            "@id",(sp, c) -> new AstLiteralDecimal(sp, String.valueOf(c.logicId())),
            "@name", (sp, c) -> new AstLiteralString(sp, c.contentName())
    );

    private AstMindcodeNode evaluatePropertyAccess(AstPropertyAccess node, boolean local) {
        BuiltinEvaluation evaluation = node.getProfile().getBuiltinEvaluation();

        // We're only compile-time evaluating IDs
        AstBuiltInIdentifier property = node.getProperty();
        if (!PROPERTY_MAPPER.containsKey(property.getName())) return node;

        AstMindcodeNode object = evaluateNode(node.getObject(), local);
        if (!(object instanceof AstBuiltInIdentifier item)) return node;

        MindustryMetadata metadata = processor.getMetadata();
        MindustryContent namedContent = metadata.getNamedContent(item.getName());
        if (namedContent == null) return node;

        if ("@id".equals(property.getName())) {
            if (evaluation == BuiltinEvaluation.NONE) return node;
            if (evaluation == BuiltinEvaluation.COMPATIBLE && !metadata.isStableBuiltin(item.getName())) return node;
            if (namedContent.logicId() < 0) return node;
        }

        return PROPERTY_MAPPER.get(property.getName()).apply(node.sourcePosition(), namedContent);
    }

    private AstMindcodeNode evaluateBody(List<AstMindcodeNode> body, boolean local) {
        return body.size() == 1 ? evaluateNode(body.getFirst(), local)
                : new AstStatementList(body.isEmpty() ? SourcePosition.EMPTY : body.getFirst().sourcePosition(), body);
    }

    private AstMindcodeNode evaluateFunctionCall(AstFunctionCall node, boolean local) {
        switch (node.getFunctionName()) {
            case "packcolor":   return processor.isSupported(Opcode.PACKCOLOR) ? evaluatePackColor(node, local) : node;
            case "length":      return evaluateLength(node, local);
            case "encode":      return evaluateEncode(node, local);
        }

        Operation operation = Operation.fromMindcode(node.getFunctionName());
        if (operation != null && processor.isSupported(Opcode.OP, List.of(operation))) {
            return evaluateOp(node, operation, local);
        }

        IdentityHashMap<AstMindcodeNode, AstMindcodeNode> original = new IdentityHashMap<>(cache);

        try {
            List<AstExpression> evaluated = evaluateArguments(node, local, AstExpression.class);
            if (evaluated.size() != node.getArguments().size()) return node;

            List<MindcodeFunction> exactMatches = callGraph.getExactMatches(node, node.getArguments().size());
            if (exactMatches.size() != 1) return node;

            MindcodeFunction function = exactMatches.getFirst();
            if (!function.getDeclaration().canEvaluate() || function.isVarargs() || function.isRecursive()
                || function.isVoid() || function.getDeclaration().getBody().size() != 1) return node;

            AstMindcodeNode body = function.getDeclaration().getBody().getFirst();
            if (body instanceof AstReturnStatement n && n.getReturnValue() != null) body = n.getReturnValue();

            Map<AstIdentifier, AstExpression> arguments = new HashMap<>();
            for (int i = 0; i < evaluated.size(); i++) {
                arguments.put(function.getDeclaration().getParameter(i).getIdentifier(), evaluated.get(i));
            }

            functionVariables.add(arguments);
            AstMindcodeNode result = evaluateNode(body, true);
            functionVariables.removeLast();

            // We successfully evaluated a function call
            if (result instanceof AstLiteral) function.setEvaluated();

            return result instanceof AstLiteral ? result : node;
        } finally {
            cache = original;
        }
    }

    private AstMindcodeNode evaluateOp(AstFunctionCall node, Operation operation, boolean local) {
        LogicOperation eval = ExpressionEvaluator.getOperation(operation);
        int numArgs = ExpressionEvaluator.getNumberOfArguments(operation);
        if (eval != null && numArgs == node.getArguments().size()) {
            List<AstLiteral> evaluated = evaluateArguments(node, local, AstLiteral.class);
            if (evaluated.size() == numArgs) {
                // All parameters are constants
                // left and right are the same argument for unary functions
                ExpressionValue left = ExpressionValue.create(builtinEvaluation, processor, evaluated.getFirst());
                ExpressionValue right = ExpressionValue.create(builtinEvaluation, processor, evaluated.getLast());
                if (left.isString() || right.isString()) {
                    error(node, ERR.UNSUPPORTED_STRING_EXPRESSION);
                    return node;
                }
                Result result = new Result();
                eval.execute(result, left, right);
                return result.toAstMindcodeNode(processor, node);
            }
        }

        return node;
    }

    private AstMindcodeNode evaluatePackColor(AstFunctionCall node, boolean local) {
        if (node.getArguments().size() == 4) {
            List<AstLiteral> evaluated = evaluateArguments(node, local, AstLiteral.class);
            if (evaluated.size() == 4) {
                String literal = Color.toColorLiteral(
                        evaluated.get(0).getDoubleValue(),
                        evaluated.get(1).getDoubleValue(),
                        evaluated.get(2).getDoubleValue(),
                        evaluated.get(3).getDoubleValue());
                return new AstLiteralColor(node.sourcePosition(), literal);
            }
        }

        return node;
    }

    private AstMindcodeNode evaluateLength(AstFunctionCall node, boolean local) {
        if (node.getArguments().size() == 1) {
            AstFunctionArgument argument = node.getArgument(0);
            if (argument.hasExpression() && argument.getExpression() instanceof AstIdentifier identifier) {
                ValueStore valueStore = variables.resolveVariable(identifier, local, true);
                if (valueStore instanceof ArrayStore array) {
                    return new AstLiteralDecimal(node.sourcePosition(), String.valueOf(array.getSize()));
                }
            }
        }

        return node;
    }

    private static final Set<Integer> invalidCharacters = Set.of((int) '\r',  (int) '"');

    private AstMindcodeNode evaluateEncode(AstFunctionCall node, boolean local) {
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(node.getIdentifier(), FUNCTION_REQUIRES_TARGET_8, node.getFunctionName());
            return new AstLiteralString(node.sourcePosition(), "");
        }

        if (node.getArguments().size() < 2) {
            error(node, ERR.FUNCTION_CALL_NOT_ENOUGH_ARGS,
                    node.getFunctionName(), 2, node.getArguments().size());
            return new AstLiteralString(node.sourcePosition(), "");
        }

        List<AstLiteral> literals = evaluateConstantArguments(node, local, l -> {
            double value = l.getDoubleValue();
            return l instanceof AstLiteralNumeric && value == (int) value;
        }, f -> error(f, ERR.ENCODE_INVALID_ARGUMENT, node.getFunctionName()));

        if (literals.size() != node.getArguments().size()) return new AstLiteralString(node.sourcePosition(), "");

        int offset = literals.removeFirst().getIntValue();
        StringBuilder sbr = new StringBuilder(2 * literals.size());
        int index = 0;
        for (AstLiteral literal : literals) {
            int value = literal.getIntValue();
            index++;
            int charValue = value + offset;
            if (charValue < 0 || charValue > 0xFFFF || (charValue >= 0xD800 && charValue <= 0xDFFF) || invalidCharacters.contains(charValue)) {
                error(node.getArgument(index), ERR.ENCODE_INVALID_CHARACTER, charValue);
            } else {
                sbr.appendCodePoint(charValue);
            }
        }

        String value = sbr.toString();
        if (value.contains("\\n")) {
            error(node.getArgument(index), ERR.ENCODE_INVALID_STRING, node.getFunctionName());
            return new AstLiteralString(node.sourcePosition(), "");
        }

        value = value.replace("\n", "\\n");
        return new AstLiteralString(node.sourcePosition(), value);
    }

    private <N extends AstMindcodeNode> List<N> evaluateArguments(AstFunctionCall node, boolean local, Class<N> requiredClass) {
        return node.getArguments().stream()
                .filter(AstFunctionArgument::isInput)
                .map(AstFunctionArgument::getExpression)
                .filter(Objects::nonNull)
                .map(n -> evaluateNode(n, local))
                .filter(requiredClass::isInstance)
                .map(requiredClass::cast)
                .toList();
    }

    // Reports error on arguments that aren't constant
    private List<AstLiteral> evaluateConstantArguments(AstFunctionCall node, boolean local, Predicate<AstLiteral> validator,
            Consumer<AstFunctionArgument> errorReporter) {
        List<AstLiteral> result = new ArrayList<>();

        for (AstFunctionArgument argument : node.getArguments()) {
            if (argument.isInput() && argument.getExpression() != null
                && evaluateNode(argument.getExpression(), local) instanceof AstLiteralNumeric literal
                && validator.test(literal)) {
                result.add(literal);
            } else {
                errorReporter.accept(argument);
            }
        }

        return result;
    }

    private boolean isString(@Nullable LogicReadable variable) {
        return variable != null && variable.getObject() instanceof String;
    }

    private String print(LogicReadable variable) {
        return variable.getObject() instanceof String string ? string
                : processor.formatNumber(variable.getDoubleValue());
    }
}
