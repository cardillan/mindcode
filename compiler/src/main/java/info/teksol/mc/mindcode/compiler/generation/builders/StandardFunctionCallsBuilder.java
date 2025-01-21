package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.functions.BaseFunctionMapper;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.util.Tuple2;
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

    public ValueStore handleMethodCall(AstFunctionCall call) {
        // Evaluate target and function arguments and create value list
        ValueStore target = evaluate(Objects.requireNonNull(call.getObject()));
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);

        ValueStore result = functionMapper.handleProperty(call, target, arguments);
        assembler.clearSubcontextType();
        if (result == null) {
            error(call.getIdentifier(), "Undefined function '%s'.", call.getFunctionName());
            return LogicNull.NULL;
        } else {
            return result;
        }
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
        List<MindcodeFunction> exactMatches = callGraph.getExactMatches(call, arguments);
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
            List<MindcodeFunction> matches) {
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

    private ValueStore processMatchedFunctionCall(MindcodeFunction function, AstFunctionCall call, List<FunctionArgument> arguments) {
        String functionName = call.getFunctionName();
        int parameterCount = function.getStandardParameterCount();

        // Make sure IdentifierFunctionArguments get evaluated now and not in a different context
        // (in case of inline functions, they could get evaluated in the context of the function)
        arguments.forEach(FunctionArgument::getArgumentValue);

        if (function.isVarargs()) {
            if (arguments.size() < parameterCount) {
                error(call, "Function '%s': wrong number of arguments (expected at least %d, found %d).",
                        functionName, parameterCount, arguments.size());
                return function.isVoid() ? LogicVoid.VOID : LogicNull.NULL;
            }
        } else {
            if (!function.getParameterCount().contains(arguments.size())) {
                error(call, "Function '%s': wrong number of arguments (expected %s, found %d).",
                        functionName, function.getParameterCount().getRangeString(), arguments.size());
                return function.isVoid() ? LogicVoid.VOID : LogicNull.NULL;
            }
        }
        validateUserFunctionArguments(function, arguments.subList(0, Math.min(parameterCount, arguments.size())));

        if (function.isInline()) {
            return handleInlineFunctionCall(function, arguments);
        } else if (!function.isRecursive()) {
            return handleStacklessFunctionCall(function, arguments);
        } else {
            return handleRecursiveFunctionCall(function, arguments);
        }
    }

    private void validateUserFunctionArguments(MindcodeFunction function, List<FunctionArgument> arguments) {
        if (function.getDeclaredParameters().size() < arguments.size()) {
            // This needs to be checked beforehand
            throw new MindcodeInternalError("Argument size mismatch.");
        }

        for (int i = 0; i < arguments.size(); i++) {
            AstFunctionParameter parameter = function.getDeclaredParameter(i);
            FunctionArgument argument = arguments.get(i);

            if (parameter.isCompulsory() && !argument.hasValue()) {
                error(argument.sourcePosition(), "Parameter '%s' isn't optional, a value must be provided.", parameter.getName());
            }

            if (parameter.isOutput()) {
                if (parameter.isInput()) {
                    // In or out modifier needs to be used
                    if (!argument.hasInModifier() && !argument.hasOutModifier()) {
                        error(argument.sourcePosition(), "Parameter '%s' is declared 'in out' and no 'in' or 'out' argument modifier was used.", parameter.getName());
                    }
                } else {
                    // Output parameter: the 'in' modifier is forbidden
                    if (argument.hasInModifier()) {
                        error(argument.sourcePosition(), "Parameter '%s' isn't input, 'in' modifier not allowed.", parameter.getName());
                    } else if (argument.hasValue() && !argument.hasOutModifier()) {
                        // Out modifier needs to be used
                        error(argument.sourcePosition(), "Parameter '%s' is output and 'out' modifier was not used.", parameter.getName());
                    }
                }
            } else {
                // Input parameter: the 'out' modifier is forbidden
                if (argument.hasOutModifier()) {
                    error(argument.sourcePosition(), "Parameter '%s' isn't output, 'out' modifier not allowed.", parameter.getName());
                }
            }
        }
    }

    private LogicValue handleInlineFunctionCall(MindcodeFunction inlineFunction, List<FunctionArgument> arguments) {
        MindcodeFunction function = inlineFunction.prepareInlinedForCall(processor.nextFunctionPrefix());
        final int nonVarargCount = Math.min(function.getStandardParameterCount(), arguments.size());

        // Varargs handling and validation: vararg arguments can't be unspecified.
        List<FunctionArgument> varargs = function.isVarargs() ? arguments.subList(nonVarargCount, arguments.size()) : List.of();
        varargs.stream().filter(v -> !v.hasValue()).forEach(v -> error(v.sourcePosition(), "Missing value"));

        assembler.setSubcontextType(AstSubcontextType.INLINE_CALL, 1.0);
        enterFunction(function,  varargs);
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
        exitFunction(function);
        return returnValue;
    }

    private LogicValue handleStacklessFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments) {
        String functionPrefix = function.getPrefix();

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, arguments, false);
        final boolean isVoid = function.isVoid();

        assembler.setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
        final LogicLabel returnLabel = nextLabel();
        assembler.createSetAddress(LogicVariable.fnRetAddr(functionPrefix), returnLabel);
        assembler.createCallStackless(function.getLabel(), LogicVariable.fnRetVal(function));
        // Mark position where the function must return
        assembler.createLabel(returnLabel);

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        retrieveFunctionParameters(function, arguments, false);
        return passReturnValue(function);
    }

    private List<LogicVariable> getStackVariables(MindcodeFunction function, List<FunctionArgument> arguments) {
        LinkedHashSet<LogicVariable> result = variables.getActiveVariables().stream()
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(function::isNotOutputFunctionParameter)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Add output function parameters passed in as input only arguments
        arguments.stream()
                .filter(FunctionArgument::isInputOnly)
                .map(FunctionArgument::getArgumentValue)
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(function::isOutputFunctionParameter)
                .forEach(result::add);

        return new ArrayList<>(result);
    }

    private LogicValue handleRecursiveFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments) {
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
    private boolean misplacedInput(MindcodeFunction function, FunctionArgument argument, LogicVariable functionParameter) {
        // TODO When function parameters become ValueStores, the isInputFunctionParameter
        //        will be called directly without casting
        return functionParameter.isInput()
               && argument.getArgumentValue() instanceof LogicVariable variable
               && function.isInputFunctionParameter(variable)
               && !functionParameter.equals(argument.getArgumentValue());
    }

    private void setupFunctionParameters(MindcodeFunction function, List<FunctionArgument> arguments, boolean recursiveCall) {
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
    private boolean misplacedOutput(MindcodeFunction function, FunctionArgument argument, LogicVariable functionParameter) {
        // TODO When function parameters become ValueStores, the isOutputFunctionParameter
        //        will be called directly without casting
        return argument.isOutput()
               && argument.getArgumentValue() instanceof LogicVariable variable
               && function.isOutputFunctionParameter(variable)
               && !functionParameter.equals(argument.getArgumentValue());
    }

    private void retrieveFunctionParameters(MindcodeFunction function, List<FunctionArgument> arguments, boolean recursiveCall) {
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

    private LogicValue passReturnValue(MindcodeFunction function) {
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

