package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.functions.BaseFunctionMapper;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionParameter;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import info.teksol.mc.util.Tuple2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.messages.ERR.FUNCTION_CALL_WRONG_NUMBER_OF_ARGS;

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
            error(call.getIdentifier(), ERR.METHOD_CALL_UNDEFINED, call.getFunctionName());
            return LogicNull.NULL;
        } else {
            return result;
        }
    }

    public ValueStore handleFunctionCall(AstFunctionCall call, boolean async) {
        // Evaluate function arguments and create value list
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);

        ValueStore result = processFunctionCall(call, arguments, async);
        assembler.clearSubcontextType();
        return result;
    }

    private ValueStore processFunctionCall(AstFunctionCall call, List<FunctionArgument> arguments, boolean async) {
        List<MindcodeFunction> exactMatches = callGraph.getExactMatches(call, arguments);
        if (!exactMatches.isEmpty()) {
            // There are user functions exactly matching the call. Process them.
            return processMatchedFunctionCalls(call, arguments, exactMatches, async);
        } else {
            // No exact match. Try built-in function, and if it fails, evaluate possible loose matches
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);

            ValueStore result = functionMapper.handleFunction(call, arguments);
            if (result != null) {
                if (async) error(call, ERR.FUNCTION_CALL_ASYNC_UNSUPPORTED, call.getFunctionName());
                return result;
            }

            return processMatchedFunctionCalls(call, arguments, callGraph.getLooseMatches(call), async);
        }
    }

    private ValueStore processMatchedFunctionCalls(AstFunctionCall call, List<FunctionArgument> arguments,
            List<MindcodeFunction> matches, boolean async) {
        if (matches.size() == 1) {
            // Exactly one match by name: process the function
            // If it was a loose match, the call will provide detailed error messages on argument mismatches
            return processMatchedFunctionCall(matches.getFirst(), call, arguments, async);
        } else {
            // No matches or multiple matches.
            error(call.getIdentifier(), matches.isEmpty() ? ERR.FUNCTION_CALL_UNDEFINED : ERR.FUNCTION_CALL_UNRESOLVED,
                    call.getFunctionName());
            return LogicNull.NULL;
        }
    }

    private ValueStore processMatchedFunctionCall(MindcodeFunction function, AstFunctionCall call,
            List<FunctionArgument> arguments, boolean async) {
        String functionName = call.getFunctionName();
        int parameterCount = function.getStandardParameterCount();

        // Make sure IdentifierFunctionArguments get evaluated now and not in a different context
        // (in case of inline functions, they could get evaluated in the context of the function)
        arguments.forEach(FunctionArgument::unwrap);

        if (function.isRemote() && function.getModule().getRemoteProcessor() == null) {
            error(call, ERR.FUNCTION_REMOTE_CALLED_LOCALLY, functionName);
            return LogicVariable.INVALID;
        }

        if (function.isVarargs()) {
            if (arguments.size() < parameterCount) {
                error(call, ERR.FUNCTION_CALL_NOT_ENOUGH_ARGS,
                        functionName, parameterCount, arguments.size());
                return function.isVoid() ? LogicVoid.VOID : LogicNull.NULL;
            }
        } else {
            if (!function.getParameterCount().contains(arguments.size())) {
                error(call, ERR.FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                        functionName, function.getParameterCount().getRangeString(), arguments.size());
                return function.isVoid() ? LogicVoid.VOID : LogicNull.NULL;
            }
        }
        validateUserFunctionArguments(function, arguments.subList(0, Math.min(parameterCount, arguments.size())));

        if (function.isRemote()) {
            if (async && arguments.stream().filter(a -> !a.isInput()).anyMatch(FunctionArgument::hasValue)) {
                error(call, ERR.ASYNC_OUTPUT_ARGUMENT, function.getName());
            }

            return handleRemoteFunctionCall(function, arguments, async);
        }

        if (async) {
            error(call, ERR.FUNCTION_CALL_ASYNC_UNSUPPORTED, call.getFunctionName());
        }

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
            throw new MindcodeInternalError("Argument count mismatch.");
        }

        for (int i = 0; i < arguments.size(); i++) {
            AstFunctionParameter parameter = function.getDeclaredParameter(i);
            FunctionArgument argument = arguments.get(i);

            if (parameter.isCompulsory() && !argument.hasValue()) {
                error(argument, ERR.ARGUMENT_NOT_OPTIONAL, parameter.getName());
            }

            if (parameter.isOutput()) {
                if (parameter.isInput()) {
                    // In or out modifier needs to be used
                    if (!argument.hasInModifier() && !argument.hasOutModifier()) {
                        error(argument, ERR.ARGUMENT_IN_OUT_MODIFIER_REQUESTED, parameter.getName());
                    }
                } else {
                    // Output parameter: the 'in' modifier is forbidden
                    if (argument.hasInModifier()) {
                        error(argument, ERR.ARGUMENT_IN_MODIFIER_NOT_ALLOWED, parameter.getName());
                    } else if (argument.hasValue() && !argument.hasOutModifier()) {
                        // Out modifier needs to be used
                        error(argument, ERR.ARGUMENT_OUT_MODIFIER_REQUESTED, parameter.getName());
                    }
                }
            } else {
                // Input parameter: the 'out' modifier is forbidden
                if (argument.hasOutModifier()) {
                    error(argument, ERR.ARGUMENT_OUT_MODIFIER_DISALLOWED, parameter.getName());
                }
            }
        }
    }

    private LogicValue handleInlineFunctionCall(MindcodeFunction inlineFunction, List<FunctionArgument> arguments) {
        if (profile.isSymbolicLabels()) {
            assembler.createComment("Function: " + inlineFunction.getDeclaration().toSourceCode());
        }

        MindcodeFunction function = inlineFunction.prepareInlinedForCall(processor.nextFunctionPrefix(inlineFunction));
        final int nonVarargCount = Math.min(function.getStandardParameterCount(), arguments.size());

        // Varargs handling and validation: vararg arguments can't be unspecified.
        List<FunctionArgument> varargs = function.isVarargs() ? arguments.subList(nonVarargCount, arguments.size()) : List.of();
        varargs.stream().filter(v -> !v.hasValue()).forEach(v -> error(v, ERR.ARGUMENT_UNNAMED_NOT_OPTIONAL));

        assembler.setSubcontextType(AstSubcontextType.INLINE_CALL, 1.0);
        enterFunction(function,  varargs);
        setupFunctionParameters(function, arguments.subList(0, nonVarargCount), false);

        final LogicValue returnValue = function.isVoid() ? LogicVoid.VOID : assembler.nextNodeResultTemp();
        final LogicLabel returnLabel = assembler.nextLabel();
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
        assert function.getLabel() != null;
        String functionPrefix = function.getPrefix();

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, arguments, false);
        final boolean isVoid = function.isVoid();

        LogicVariable retAddr = LogicVariable.fnRetAddr(functionPrefix);
        if (profile.isSymbolicLabels()) {
            assembler.setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
            assembler.createCallStackless(function.getLabel(), retAddr,LogicVariable.fnRetVal(function));
        } else {
            final LogicLabel returnLabel = assembler.nextLabel();
            assembler.createSetAddress(retAddr, returnLabel).setHoistId(returnLabel);
            assembler.setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
            // We're putting INVALID as retAddr: in no symbolic labels mode, the CALL instruction doesn't
            // set function return address, it is set up separately by the previous instruction
            assembler.createCallStackless(function.getLabel(), LogicVariable.INVALID,LogicVariable.fnRetVal(function))
                    .setMarker(returnLabel).setHoistId(returnLabel);
            // Mark position where the function must return
            assembler.createLabel(returnLabel);
        }

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
                .map(FunctionArgument::unwrap)
                .filter(LogicVariable.class::isInstance)
                .map(LogicVariable.class::cast)
                .filter(function::isOutputFunctionParameter)
                .forEach(result::add);

        return new ArrayList<>(result);
    }

    private LogicValue handleRecursiveFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments) {
        assert function.getLabel() != null;
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
        final LogicLabel returnLabel = assembler.nextLabel();
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

    private LogicValue handleRemoteFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments, boolean async) {
        assert function.getModule().getRemoteProcessor() != null;
        LogicVariable processor = (LogicVariable) evaluate(function.getModule().getRemoteProcessor());

        // Function parameters will be set up using RemoteVariable value stores created for the remote function
        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, arguments, false);

        LogicVariable finished = LogicVariable.fnFinished(function);
        assembler.setSubcontextType(function, AstSubcontextType.REMOTE_CALL);
        assembler.createSet(finished, LogicBoolean.FALSE);
        assembler.createWrite(LogicVariable.fnAddress(function), processor, LogicString.create("@counter"));

        if (async) return LogicVoid.VOID;

        SideEffects sideEffects = createRemoteCallSideEffects(function);
        LogicLabel label = assembler.createNextLabel();
        assembler.createYieldExecution().setSideEffects(sideEffects);
        assembler.createJump(label, Condition.EQUAL, finished, LogicBoolean.FALSE);

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        retrieveFunctionParameters(function, arguments, false);
        return passReturnValue(function);
    }

    private @Nullable MindcodeFunction verifyRemoteFunctionName(AstFunctionCall call) {
        if (call.getArguments().size() != 1) {
            error(call, FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                    call.getFunctionName(), 1, call.getArguments().size());
            return null;
        }

        AstExpression function = Objects.requireNonNull(call.getArgument(0).getExpression());

        if (function instanceof AstIdentifier functionName) {
            List<MindcodeFunction> functions = callGraph.getFunctions().stream()
                    .filter(f -> f.isRemote() && f.getName().equals(functionName.getName())).toList();
            if (functions.isEmpty()) {
                error(call, ERR.REMOTE_WRONG_ARGUMENT, call.getFunctionName());
                return null;
            } else if (functions.size() > 1) {
                error(call, ERR.REMOTE_MULTIPLE_FUNCTIONS, functionName.getName());
                return null;
            } else {
                return functions.getFirst();
            }
        } else {
            error(call, ERR.REMOTE_WRONG_ARGUMENT, call.getFunctionName());
            evaluate(function);
            return null;
        }
    }

    private SideEffects createRemoteCallSideEffects(MindcodeFunction function) {
        List<LogicVariable> outputs = new ArrayList<>();
        function.getLocalParameters().stream()
                .filter(FunctionParameter::isOutput)
                .map(LogicVariable.class::cast)
                .forEach(outputs::add);
        if (!function.isVoid()) {
            outputs.add(LogicVariable.fnRetVal(function));
        }

        return SideEffects.writes(outputs);
    }

    public ValueStore handleFinished(AstFunctionCall call) {
        MindcodeFunction function = verifyRemoteFunctionName(call);
        return function == null ? LogicVariable.INVALID : LogicVariable.fnFinished(function);
    }

    public ValueStore handleAwait(AstFunctionCall call) {
        MindcodeFunction function = verifyRemoteFunctionName(call);
        if (function == null) return LogicVariable.INVALID;
        SideEffects sideEffects = createRemoteCallSideEffects(function);

        assembler.setSubcontextType(function, AstSubcontextType.REMOTE_CALL);
        LogicLabel label = assembler.createNextLabel();
        assembler.createYieldExecution().setSideEffects(sideEffects);
        assembler.createJump(label, Condition.EQUAL, LogicVariable.fnFinished(function), LogicBoolean.FALSE);
        assembler.clearSubcontextType();
        return LogicVariable.fnRetVal(function);
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
    private boolean misplacedInput(MindcodeFunction function, FunctionArgument argument, FunctionParameter functionParameter) {
        // TODO When function parameters become ValueStores, the isInputFunctionParameter
        //        will be called directly without casting
        return functionParameter.isInput()
               && argument.unwrap() instanceof LogicVariable variable
               && function.isInputFunctionParameter(variable)
               && !functionParameter.equals(argument.unwrap());
    }

    private void setupFunctionParameters(MindcodeFunction function, List<FunctionArgument> arguments, boolean recursiveCall) {
        // List of values to be passed to parameters
        Queue<@NonNull ValueStore> argumentValues = new ArrayDeque<>(arguments.size());
        int limit = Math.min(arguments.size(), function.getDeclaredParameters().size());

        for (int index = 0; index < limit; index++) {
            FunctionArgument argument = arguments.get(index);
            if (function.getDeclaredParameter(index).isInput()) {
                if (misplacedInput(function, argument, function.getParameter(index))) {
                    LogicVariable temp = assembler.unprotectedTemp();
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
                ValueStore argument = argumentValues.remove();
                if (function.getDeclaration().isInline() && !argument.unwrap().isMlogRepresentable()) {
                    variables.replaceFunctionVariable(function.getDeclaredParameter(index).getIdentifier(), argument.unwrap());
                } else {
                    function.getParameter(index).setValue(assembler, argument.getValue(assembler));
                }
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
    private boolean misplacedOutput(MindcodeFunction function, FunctionArgument argument, FunctionParameter functionParameter) {
        // TODO When function parameters become ValueStores, the isOutputFunctionParameter
        //        will be called directly without casting
        return argument.isOutput()
               && argument.unwrap() instanceof LogicVariable variable
               && function.isOutputFunctionParameter(variable)
               && !functionParameter.equals(argument.unwrap());
    }

    private void retrieveFunctionParameters(MindcodeFunction function, List<FunctionArgument> arguments, boolean recursiveCall) {
        List<Tuple2<ValueStore, LogicVariable>> finalizers = new ArrayList<>();

        // Setup variables representing function parameters with values from this call
        for (int i = 0; i < arguments.size(); i++) {
            FunctionArgument argument = arguments.get(i);
            if (argument.isOutput()) {
                FunctionParameter parameter = function.getLocalParameter(i);
                if (misplacedOutput(function, argument, parameter)) {
                    LogicVariable temp = assembler.unprotectedTemp();
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
            final LogicVariable resultVariable = assembler.nextNodeResultTemp();
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

