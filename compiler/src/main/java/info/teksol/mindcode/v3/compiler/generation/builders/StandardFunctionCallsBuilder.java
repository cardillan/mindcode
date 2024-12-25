package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicNull;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunctionV3;
import info.teksol.mindcode.v3.compiler.functions.FunctionMapper;
import info.teksol.mindcode.v3.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.LoopStack;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NullMarked
public class StandardFunctionCallsBuilder extends AbstractFunctionBuilder {
    private final FunctionMapper functionMapper;

    public StandardFunctionCallsBuilder(AbstractBuilder builder) {
        super(builder);
        functionMapper = FunctionMapperFactory.getFunctionMapper(context.instructionProcessor(),
                assembler::getAstContext, messageConsumer);
    }

    public NodeValue handleStandardFunctionCall(AstFunctionCall call) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);
        LogicValue output = handleFunctionCall(call, arguments);
        assembler.clearSubcontextType();
        return output;
    }

    private LogicValue handleFunctionCall(AstFunctionCall call, List<FunctionArgument> arguments) {
        final String functionName = call.getFunctionName();
        List<LogicFunctionV3> exactMatches = callGraph.getExactMatches(call);

        // Try built-in function if there's not an exact match
        if (!exactMatches.isEmpty()) {
            if (exactMatches.size() > 1) {
                error(call,"Cannot resolve function '%s'.", functionName);
                return LogicNull.NULL;
            } else {
                return handleUserFunctionCall(exactMatches.getFirst(), call, arguments);
            }
        } else {
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            LogicValue output = functionMapper.handleFunction(call, assembler, functionName, arguments);
            if (output != null) {
                return output;
            }

            // We know there wasn't an exact match. Try loose matches to obtain better error messages
            List<LogicFunctionV3> looseMatches = callGraph.getLooseMatches(call);
            return switch (looseMatches.size()) {
                case 0  -> {
                    error(call,"Unknown function '%s'.", functionName);
                    yield LogicNull.NULL;
                }
                case 1  -> {
                    yield handleUserFunctionCall(looseMatches.getFirst(), call, arguments);
                }
                default -> {
                    error(call, "Cannot resolve function '%s'.", functionName);
                    yield LogicNull.NULL;
                }
            };
        }
    }


    private LogicValue handleUserFunctionCall(LogicFunctionV3 function, AstFunctionCall call, List<FunctionArgument> callArguments) {
        String functionName = call.getFunctionName();

        List<FunctionArgument> arguments = addOptionalArguments(function, callArguments);
        if (function.isVarArgs()) {
            if (arguments.size() < function.getStandardParameterCount()) {
                error(call, "Function '%s': wrong number of arguments (expected at least %d, found %d).",
                        functionName, function.getStandardParameterCount(), arguments.size());
                return function.isVoid() ? VOID : LogicNull.NULL;
            }
            validateUserFunctionArguments(function, arguments.subList(0, function.getStandardParameterCount()));
        } else {
            if (arguments.size() != function.getParameterCount().max()) {
                error(call, "Function '%s': wrong number of arguments (expected %d, found %d).",
                        functionName, function.getParameterCount().max(), arguments.size());
                return function.isVoid() ? VOID : LogicNull.NULL;
            }
            validateUserFunctionArguments(function, arguments);
        }

        // Switching to new function prefix -- save/restore old one
        String previousPrefix = functionPrefix;
        try {
            // Entire inline function evaluates using given prefix (different invocations use different variables).
            // For other functions, the prefix is only used to set up variables representing function parameters.
            functionPrefix = function.isInline() ? instructionProcessor.nextFunctionPrefix() : function.getPrefix();

            if (function.isInline()) {
                return handleInlineFunctionCall(function, arguments);
            } else if (!function.isRecursive()) {
                return handleStacklessFunctionCall(function, arguments);
            } else {
                return handleRecursiveFunctionCall(function, arguments);
            }
        } finally {
            functionPrefix = previousPrefix;
        }
    }

    private LogicValue handleInlineFunctionCall(LogicFunctionV3 function, List<FunctionArgument> arguments) {
        // Switching to inline function context -- save/restore old one
        final LogicFunctionV3 previousFunction = currentFunction;
        final LogicInstructionGenerator.LocalContext previousContext = functionContext;
        final LoopStack previousLoopStack = loopStack;
        final String previousVarArgName = varArgName;
        final List<FunctionArgument> previousVarArgValues = varArgValues;
        final boolean isVoid = function.isVoid();
        final int standardArgumentCount = function.getStandardParameterCount();

        setSubcontextType(AstSubcontextType.INLINE_CALL, 1.0);
        currentFunction = function;
        functionContext = new LogicInstructionGenerator.LocalContext();
        loopStack = new LoopStack(messageConsumer);

        emit(createLabel(nextLabel()));
        setupFunctionParameters(function, arguments.subList(0, standardArgumentCount));

        if (function.isVarArgs()) {
            varArgName = function.getDeclaredParameter(standardArgumentCount).getName();
            varArgValues = arguments.subList(standardArgumentCount, arguments.size());
            // Validations
            varArgValues.stream().filter(v -> !v.hasValue()).forEach(v -> error(v.pos(), "Missing value"));
            varArgValues.stream().filter(v -> v.hasValue() && v.outModifier() && !v.value().isUserWritable()).forEach(v -> error(v.pos(),
                    "Argument marked with 'out' modifier is not writable."));
        } else {
            varArgName = null;
            varArgValues = null;
        }

        // Retval gets registered in nodeContext, but we don't mind -- inline functions do not use stack
        final LogicValue returnValue = isVoid ? VOID : nextReturnValue();
        final LogicLabel returnLabel = nextLabel();
        returnStack.enterFunction(returnLabel, returnValue);
        LogicValue result = visit(function.getBody());
        if (!isVoid) {
            emit(createSet((LogicVariable) returnValue, result));
        }
        setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        emit(createLabel(returnLabel));
        returnStack.exitFunction();

        retrieveFunctionParameters(function, arguments.subList(0, standardArgumentCount), false);
        varArgValues = previousVarArgValues;
        varArgName = previousVarArgName;
        loopStack = previousLoopStack;
        functionContext = previousContext;
        currentFunction = previousFunction;
        return returnValue;
    }

    private LogicValue handleStacklessFunctionCall(LogicFunctionV3 function, List<FunctionArgument> arguments) {
        setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, arguments);
        final boolean isVoid = function.isVoid();

        setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
        final LogicLabel returnLabel = nextLabel();
        emit(createSetAddress(LogicVariable.fnRetAddr(functionPrefix), returnLabel));
        emit(createCallStackless(function.getLabel(), LogicVariable.fnRetVal(function)));
        // Mark position where the function must return
        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        emit(createGotoLabel(returnLabel, LogicLabel.symbolic(functionPrefix)));

        setSubcontextType(function, AstSubcontextType.PARAMETERS);
        retrieveFunctionParameters(function, arguments, false);
        return passReturnValue(function);
    }

    private List<FunctionArgument> substituteArguments(LogicFunction function, List<FunctionArgument> arguments) {
        // Filter variables representing input parameters
        Predicate<FunctionArgument> predicate =
                a -> a.value() instanceof LogicVariable variable && function.isInputFunctionParameter(variable);

        long count = IntStream.range(0, arguments.size())
                .filter(i -> predicate.test(arguments.get(i)) && !function.getParameter(i).equals(arguments.get(i).value()))
                .count();

        // One reassignment is ok
        if (count > 1) {
            List<FunctionArgument> result = new ArrayList<>(arguments);
            for (int i = 0; i < result.size(); i++) {
                // Handle arguments if assigned to a different argument
                FunctionArgument argument = result.get(i);
                if (predicate.test(argument) && !function.getParameter(i).equals(argument.value())) {
                    LogicVariable temp = nextTemp();
                    emit(createSet(temp, argument.value()));
                    result.set(i, new FunctionArgument(null, temp, false, false));
                }
            }
            return result;
        } else {
            return arguments;
        }
    }

    private List<LogicVariable> getStackVariables(LogicFunction function, List<FunctionArgument> arguments) {
        LinkedHashSet<LogicVariable> variables = getContextVariables().stream().filter(function::isNotOutputFunctionParameter)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Add output function parameters passed in as input only arguments
        arguments.stream()
                .filter(FunctionArgument::hasInModifierOnly)
                .map(FunctionArgument::value)
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(function::isOutputFunctionParameter)
                .forEach(variables::add);

        return new ArrayList<>(variables);
    }

    private LogicValue handleRecursiveFunctionCall(LogicFunctionV3 function, List<FunctionArgument> arguments) {
        setSubcontextType(function, AstSubcontextType.RECURSIVE_CALL);
        boolean recursiveCall = currentFunction.isRecursiveCall(function);
        List<LogicVariable> variables = recursiveCall ? getStackVariables(function, arguments) : List.of();

        if (recursiveCall) {
            // Store all local variables (both user defined and temporary) on the stack
            variables.forEach(v -> emit(createPush(stackName(), v)));
        }

        setupFunctionParameters(function, substituteArguments(function, arguments));

        // Recursive function call
        final LogicLabel returnLabel = nextLabel();
        emit(createCallRecursive(stackName(), function.getLabel(), returnLabel, LogicVariable.fnRetVal(function)));
        emit(createLabel(returnLabel)); // where the function must return

        retrieveFunctionParameters(function, arguments, recursiveCall);

        if (recursiveCall) {
            // Restore all local variables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> emit(createPop(stackName(), v)));
        }

        return passReturnValue(function);
    }

}

