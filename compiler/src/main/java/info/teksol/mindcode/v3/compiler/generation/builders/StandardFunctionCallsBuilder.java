package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunctionV3;
import info.teksol.mindcode.v3.compiler.functions.BaseFunctionMapper;
import info.teksol.mindcode.v3.compiler.functions.FunctionMapper;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.StackTracker;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
public class StandardFunctionCallsBuilder extends AbstractFunctionBuilder {
    private final FunctionMapper functionMapper;
    private final StackTracker stackTracker;

    public StandardFunctionCallsBuilder(AbstractBuilder builder) {
        super(builder);
        functionMapper = new BaseFunctionMapper(context);
        stackTracker = context.stackTracker();
    }

    public ValueStore handleFunctionCall(AstFunctionCall call) {
        // Evaluate function arguments and create value list
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);

        ValueStore result = processFunctionCall(call, arguments);
        assembler.clearSubcontextType();
        return result;
    }

    private ValueStore processFunctionCall(AstFunctionCall call, List<FunctionArgument> arguments) {
        List<LogicFunctionV3> exactMatches = callGraph.getExactMatches(call);
        if (!exactMatches.isEmpty()) {
            // There are user functions exactly matching the call. Process them.
            return processMatchedFunctionCalls(call, arguments, exactMatches);
        } else {
            // No exact match. Try built-in function, and if it fails, evaluate possible loose matches
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            return Objects.requireNonNullElseGet(
                    functionMapper.handleFunction(call, arguments),
                    () -> processMatchedFunctionCalls(call, arguments, callGraph.getLooseMatches(call)));
        }
    }

    private ValueStore processMatchedFunctionCalls(AstFunctionCall call, List<FunctionArgument> arguments,
            List<LogicFunctionV3> matches) {
        if (matches.size() == 1) {
            // Exactly one match by name: process the function
            // If it was a loose match, the call will provide detailed error messages on argument mismatches
            return processMatchedFunctionCall(matches.getFirst(), call, arguments);
        } else {
            // No matches or multiple matches.
            error(call, matches.isEmpty() ? "Unknown function '%s'." : "Cannot resolve function '%s'.",
                    call.getFunctionName());
            return LogicNull.NULL;
        }
    }

    private LogicValue processMatchedFunctionCall(LogicFunctionV3 function, AstFunctionCall call, List<FunctionArgument> arguments) {
        String functionName = call.getFunctionName();

        if (function.isVarargs()) {
            if (arguments.size() < function.getStandardParameterCount()) {
                error(call, "Function '%s': wrong number of arguments (expected at least %d, found %d).",
                        functionName, function.getStandardParameterCount(), arguments.size());
                return function.isVoid() ? LogicVoid.VOID : LogicNull.NULL;
            }
        } else {
            if (!function.getParameterCount().contains(arguments.size())) {
                error(call, "Function '%s': wrong number of arguments (expected %s, found %d).",
                        functionName, function.getParameterCount().getRangeString(), arguments.size());
                return function.isVoid() ? LogicVoid.VOID : LogicNull.NULL;
            }
        }
        validateUserFunctionArguments(function, arguments.subList(0, function.getStandardParameterCount()));

        if (function.isInline()) {
            return handleInlineFunctionCall(function, arguments);
        } else if (!function.isRecursive()) {
            return handleStacklessFunctionCall(function, arguments);
        } else {
            return handleRecursiveFunctionCall(function, arguments);
        }
    }

    private void validateUserFunctionArguments(LogicFunctionV3 function, List<FunctionArgument> arguments) {
        if (function.getDeclaredParameters().size() < arguments.size()) {
            // This needs to be checked beforehand
            throw new MindcodeInternalError("Argument size mismatch.");
        }

        for (int i = 0; i < arguments.size(); i++) {
            AstFunctionParameter parameter = function.getDeclaredParameter(i);
            FunctionArgument argument = arguments.get(i);

            if (parameter.isCompulsory() && !argument.hasValue()) {
                error(argument.inputPosition(), "Parameter '%s' isn't optional, a value must be provided.", parameter.getName());
            }

            if (parameter.isOutput()) {
                if (parameter.isInput()) {
                    // In or out modifier needs to be used
                    if (!argument.hasInModifier() && !argument.hasOutModifier()) {
                        error(argument.inputPosition(), "Parameter '%s' is declared 'in out' and no 'in' or 'out' argument modifier was used.", parameter.getName());
                    }
                } else {
                    // Output parameter: the 'in' modifier is forbidden
                    if (argument.hasInModifier()) {
                        error(argument.inputPosition(), "Parameter '%s' isn't input, 'in' modifier not allowed.", parameter.getName());
                    } else if (argument.hasValue() && !argument.hasOutModifier()) {
                        // Out modifier needs to be used
                        error(argument.inputPosition(), "Parameter '%s' is output and 'out' modifier was not used.", parameter.getName());
                    }
                }
            } else {
                // Input parameter: the 'out' modifier is forbidden
                if (argument.hasOutModifier()) {
                    error(argument.inputPosition(), "Parameter '%s' isn't output, 'out' modifier not allowed.", parameter.getName());
                }
            }
        }
    }

    private LogicValue handleInlineFunctionCall(LogicFunctionV3 inlineFunction, List<FunctionArgument> arguments) {
        LogicFunctionV3 function = inlineFunction.inlineForCall(processor.nextFunctionPrefix());
        final int nonVarargCount = function.getStandardParameterCount();

        // Varargs handling and validation: vararg arguments can't be unspecified.
        List<FunctionArgument> varargs = function.isVarargs() ? arguments.subList(nonVarargCount, arguments.size()) : List.of();
        varargs.stream().filter(v -> !v.hasValue()).forEach(v -> error(v.inputPosition(), "Missing value"));

        variables.enterFunction(function,  varargs);
        setupFunctionParameters(function, arguments.subList(0, nonVarargCount), false);

        final LogicValue returnValue = function.isVoid() ? LogicVoid.VOID : nextNodeResultTemp();
        final LogicLabel returnLabel = nextLabel();
        returnStack.enterFunction(returnLabel, returnValue);

        ValueStore result = evaluateBody(function.getBody());
        if (!function.isVoid()) {
            returnValue.setValue(assembler, result.getValue(assembler));
        }

        // Return statements write directly to returnValue. The return label is placed
        // after the code which sets the return value from the function body.
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        assembler.createLabel(returnLabel);
        retrieveFunctionParameters(function, arguments.subList(0, nonVarargCount), false);
        returnStack.exitFunction();
        variables.exitFunction(function);
        return returnValue;
    }

    private LogicValue handleStacklessFunctionCall(LogicFunctionV3 function, List<FunctionArgument> arguments) {
        String functionPrefix = function.getPrefix();

        setupFunctionParameters(function, arguments, false);
        final boolean isVoid = function.isVoid();

        assembler.setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
        final LogicLabel returnLabel = nextLabel();
        assembler.createSetAddress(LogicVariable.fnRetAddr(functionPrefix), returnLabel);
        assembler.createCallStackless(function.getLabel(), LogicVariable.fnRetVal(function));
        // Mark position where the function must return
        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        assembler.createGotoLabel(returnLabel, LogicLabel.symbolic(functionPrefix));

        retrieveFunctionParameters(function, arguments, false);
        return passReturnValue(function);
    }

    private List<LogicVariable> getStackVariables(LogicFunction function, List<FunctionArgument> arguments) {
        return variables.getActiveVariables().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(function::isNotOutputFunctionParameter)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private LogicValue handleRecursiveFunctionCall(LogicFunctionV3 function, List<FunctionArgument> arguments) {
        LogicVariable stack = stackTracker.getStackMemory();

        assembler.setSubcontextType(function, AstSubcontextType.RECURSIVE_CALL);
        boolean recursiveCall = variables.currentFunction().isRecursiveCall(function);
        List<LogicVariable> variables = recursiveCall ? getStackVariables(function, arguments) : List.of();

        if (recursiveCall) {
            // Store all local variables (both user defined and temporary) on the stack
            variables.forEach(v -> assembler.createPush(stack, v));
        }

        setupFunctionParameters(function, arguments, recursiveCall);

        // Recursive function call
        final LogicLabel returnLabel = nextLabel();
        assembler.createCallRecursive(stack, function.getLabel(), returnLabel, LogicVariable.fnRetVal(function));
        assembler.createLabel(returnLabel); // where the function must return

        retrieveFunctionParameters(function, arguments, recursiveCall);

        if (recursiveCall) {
            // Restore all local variables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> assembler.createPop(stack, v));
        }

        return passReturnValue(function);
    }

    /// Returns a predicate which detects misplaced input arguments. Such arguments need to be handled specifically
    /// in recursive function calls:
    ///
    /// Let's have a recursive function `foo(x, y)`, which calls itself (`foo(z, x)`). When setting up
    /// the parameter values, we need to assign z to x and x to y. Doing it directly would cause x to be set
    /// to z before its own value gets copied to y.
    ///
    /// A variable is affected when it is used as an input argument to a parameter different from its own.
    /// When setting up function parameters, all affected variables must be copied via temporary storage.
    /// Superfluous writes are then removed by the Data Flow optimization.
    ///
    /// @param function function being called
    /// @param argument argument being processed
    /// @param parameter parameter corresponding to this argument
    /// @return `true` if the argument is a misplaced input argument
    private boolean misplacedInput(LogicFunctionV3 function, FunctionArgument argument, LogicVariable functionParameter) {
        // FINISH When function parameters become ValueStores, the isInputFunctionParameter
        //        will be called directly without casting
        return functionParameter.isInput()
               && argument.getArgumentValue() instanceof LogicVariable variable
               && function.isInputFunctionParameter(variable)
               && !functionParameter.equals(argument.getArgumentValue());
    }

    private void setupFunctionParameters(LogicFunctionV3 function, List<FunctionArgument> arguments, boolean recursiveCall) {
        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);

        // List of values to be passed to parameters
        Queue<@NonNull ValueStore> argumentValues = new ArrayDeque<>(arguments.size());
        int limit = Math.min(arguments.size(), function.getDeclaredParameters().size());

        for (int index = 0; index < limit; index++) {
            FunctionArgument argument = arguments.get(index);
            if (function.getDeclaredParameter(index).isInput()) {
                if (misplacedInput(function, argument, function.getParameters().get(index))) {
                    LogicVariable temp = unprotectedTemp();
                    assembler.createSet(temp, argument.getValue(assembler));
                    argumentValues.offer(temp);
                } else {
                    argumentValues.offer(argument);
                }
            }
        }

        // We use the in/out information of FunctionArgument to decide whether to set a parameter as input
        // Should FunctionArgument be incompatible with the parameter, an error would have been produced earlier
        for (int index = 0; index < limit; index++) {
            if (function.getDeclaredParameter(index).isInput()) {
                assembler.createSet(function.getParameter(index), argumentValues.remove().getValue(assembler));
            }
        }
    }

    /// Returns a predicate which detects misplaced output arguments. Such arguments need to be handled specifically
    /// in recursive function calls:
    ///
    /// Let's have a recursive function `foo(out x, out y)`, which calls itself (`foo(out y, out z)`).
    /// When retrieving the parameter values, we need to assign y to x and z to y. Doing it directly
    /// would cause y to be set to x before its own value gets copied to z.
    ///
    /// A variable is affected when it is used as an output argument to a parameter different from its own.
    /// When retrieving function outputs, all affected variables must be copied via temporary storage.
    /// Superfluous writes are then removed by the Data Flow optimization.
    ///
    /// Misplaced inputs and misplaced outputs are two complementary problems. A separate implementation is
    /// provided for both for better readability.
    ///
    /// @param function function being called
    /// @param argument argument being processed
    /// @param parameter parameter corresponding to this argument
    /// @return `true` if the argument is a misplaced output argument
    private boolean misplacedOutput(LogicFunctionV3 function, FunctionArgument argument, LogicVariable functionParameter) {
        // FINISH When function parameters become ValueStores, the isOutputFunctionParameter
        //        will be called directly without casting
        return argument.isOutput()
               && argument.getArgumentValue() instanceof LogicVariable variable
               && function.isOutputFunctionParameter(variable)
               && !functionParameter.equals(argument.getArgumentValue());
    }

    private void retrieveFunctionParameters(LogicFunctionV3 function, List<FunctionArgument> arguments,
            boolean recursiveCall) {
        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        List<Tuple2<ValueStore, LogicVariable>> finalizers = new ArrayList<>();

        // Setup variables representing function parameters with values from this call
        for (int i = 0; i < arguments.size(); i++) {
            FunctionArgument argument = arguments.get(i);
            if (argument.isOutput()) {
                LogicVariable parameter = function.getParameter(i);
                if (misplacedOutput(function, argument, parameter)) {
                    LogicVariable temp = unprotectedTemp();
                    assembler.createSet(temp, parameter.getValue(assembler));
                    finalizers.add(Tuple2.of(argument, temp));
                } else {
                    argument.setValue(assembler, parameter.getValue(assembler));
                }
            }
        }

        finalizers.forEach(t -> t.e1().setValue(assembler, t.e2()));
    }

    private LogicValue passReturnValue(LogicFunctionV3 function) {
        if (function.isVoid()) {
            return LogicVoid.VOID;
        } else if (variables.currentFunction().isRepeatedCall(function)) {
            // Copy default return variable to new temp, for the function is called multiple times,
            // and we must not overwrite result of previous call(s) with this one
            //
            // Allocate 'return value' type temp variable for it, so that it won't be eliminated;
            // this is easier than ensuring optimizers do not eliminate normal temporary variables
            // that received return values from functions.
            final LogicVariable resultVariable = nextNodeResultTemp();
            assembler.setSubcontextType(function, AstSubcontextType.RETURN_VALUE);
            assembler.createSet(resultVariable, LogicVariable.fnRetVal(function));
            return resultVariable;
        } else {
            // Use the function return value directly - there's only one place where it is produced
            // within this function's call tree
            return LogicVariable.fnRetVal(function);
        }
    }
}

