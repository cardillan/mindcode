package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.functions.BaseFunctionMapper;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.logic.arguments.*;
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

    public ValueStore handleCall(AstFunctionCall call, boolean async) {
        ValueStore target = call.getObject() == null ? null : evaluate(call.getObject());

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);

        ValueStore result = processCall(call, arguments, target, async);

        assembler.clearSubcontextType();
        return result;
    }

    private ValueStore processCall(AstFunctionCall call, List<FunctionArgument> arguments,
            @Nullable ValueStore target, boolean async) {
        List<MindcodeFunction> exactMatches = callGraph.getExactMatches(call, arguments.size());
        if (!exactMatches.isEmpty() && (call.getProfile().isLibraryPrecedence()
                || exactMatches.stream().noneMatch(f -> f.isLibrary() && !f.getParameters().isEmpty()))) {
            // There are user functions exactly matching the call. Process them.
            return processMatchedCalls(call, arguments, target, exactMatches, async);
        } else {
            // No exact non-library match. Try a built-in function, and if it fails, evaluate possible loose matches
            if (call.getArguments().stream().noneMatch(AstFunctionArgument::isReference)) {
                ValueStore result = target == null
                        ? functionMapper.handleFunction(call, arguments)
                        : functionMapper.handleProperty(call, target, arguments);
                if (result != null) {
                    if (async) error(call, ERR.FUNCTION_CALL_ASYNC_UNSUPPORTED, call.getFunctionName());
                    return result;
                }
            }

            return processMatchedCalls(call, arguments, target,
                    exactMatches.isEmpty() ? callGraph.getLooseMatches(call) : exactMatches, async);
        }
    }

    private ValueStore processMatchedCalls(AstFunctionCall call, List<FunctionArgument> arguments,
            @Nullable ValueStore target, List<MindcodeFunction> matches, boolean async) {
        if (matches.size() == 1) {
            // Exactly one match by name: process the function
            // If it was a loose match, the call will provide detailed error messages on argument mismatches
            return processMatchedFunctionCall(matches.getFirst(), call, target, arguments, async);
        } else {
            // No matches or multiple matches.
            error(call.getIdentifier(), target == null
                    ? matches.isEmpty() ? ERR.FUNCTION_CALL_UNDEFINED : ERR.FUNCTION_CALL_UNRESOLVED
                    : matches.isEmpty() ? ERR.METHOD_CALL_UNDEFINED : ERR.METHOD_CALL_UNRESOLVED,
                    call.getFunctionName());
            return LogicNull.NULL;
        }
    }

    private ValueStore processMatchedFunctionCall(MindcodeFunction function, AstFunctionCall call,
            @Nullable ValueStore target, List<FunctionArgument> arguments, boolean async) {
        String functionName = call.getFunctionName();
        int parameterCount = function.getStandardParameterCount();

        function.setCalled();

        // Make sure IdentifierFunctionArguments get evaluated now and not in a different context
        // (in the case of inline functions, they could get evaluated in the context of the function)
        arguments.forEach(FunctionArgument::unwrap);

        if (function.isRemote()) {
            if (function.getModule().getRemoteProcessors().isEmpty()) {
                error(call, ERR.FUNCTION_REMOTE_CALLED_LOCALLY, functionName);
                return LogicVariable.INVALID;
            }
        } else if (target != null) {
            throw new MindcodeInternalError("Target should be null for non-remote function calls.");
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

            return handleRemoteFunctionCall(function, arguments, target, async);
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

            if (parameter.isReference()) {
                if (!argument.hasRefModifier()) {
                    error(argument, ERR.ARGUMENT_REF_MODIFIER_REQUESTED, parameter.getName());
                }
                if (!(argument instanceof IdentifierFunctionArgument)) {
                    error(argument, ERR.PARAMETER_REF_IDENTIFIER_REQUESTED, parameter.getName());
                }
            } else {
                if (argument.hasRefModifier()) {
                    error(argument, ERR.ARGUMENT_REF_MODIFIER_DISALLOWED, parameter.getName());
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
    }

    private LogicValue handleInlineFunctionCall(MindcodeFunction inlineFunction, List<FunctionArgument> arguments) {
        if (inlineFunction.getProfile().isSymbolicLabels()) {
            assembler.createComment("Function: " + inlineFunction.getDeclaration().toSourceCode());
        }

        MindcodeFunction function = inlineFunction.prepareInlinedForCall(nameCreator);
        final int nonVarargCount = Math.min(function.getStandardParameterCount(), arguments.size());

        // Varargs handling and validation: vararg arguments can't be unspecified.
        List<FunctionArgument> varargs = function.isVarargs() ? arguments.subList(nonVarargCount, arguments.size()) : List.of();
        varargs.stream().filter(v -> !v.hasValue()).forEach(v -> error(v, ERR.ARGUMENT_UNNAMED_NOT_OPTIONAL));

        assembler.setSubcontextType(AstSubcontextType.INLINE_CALL, 1.0);
        enterFunction(function,  varargs);
        setupFunctionParameters(function, function.getParameters(), arguments.subList(0, nonVarargCount), false);

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
        retrieveFunctionParameters(function, function.getParameters(), arguments.subList(0, nonVarargCount), false);
        returnStack.exitFunction();
        exitFunction(function);
        return returnValue;
    }

    private ValueStore handleStacklessFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments) {
        assert function.getLabel() != null;
        String functionPrefix = function.getPrefix();

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, function.getParameters(), arguments, false);
        final boolean isVoid = function.isVoid();

        LogicVariable retAddr = function.getFnRetAddr();
        if (function.getProfile().isSymbolicLabels()) {
            assembler.setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
            assembler.createCallStackless(function.getLabel(), retAddr,function.getFnRetVal());
        } else {
            final LogicLabel returnLabel = assembler.nextLabel();
            assembler.createSetAddress(retAddr, returnLabel).setHoistId(returnLabel);
            assembler.setSubcontextType(function, AstSubcontextType.OUT_OF_LINE_CALL);
            // We're putting INVALID as retAddr: in absolute addressing mode, the CALL instruction doesn't
            // set function return address, it is set up separately by the previous instruction
            assembler.createCallStackless(function.getLabel(), LogicVariable.INVALID,function.getFnRetVal())
                    .setMarker(returnLabel).setHoistId(returnLabel);
            // Mark the position where the function must return
            assembler.createLabel(returnLabel);
        }

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        retrieveFunctionParameters(function, function.getParameters(), arguments, false);
        return passReturnValue(function, null);
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

    private ValueStore handleRecursiveFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments) {
        assert function.getLabel() != null;
        LogicVariable stack = stackTracker.getStackMemory();

        assembler.setSubcontextType(function, AstSubcontextType.RECURSIVE_CALL);
        boolean recursiveCall = variables.currentFunction().isRecursiveCall(function);
        List<LogicVariable> variables = recursiveCall ? getStackVariables(function, arguments) : List.of();

        if (recursiveCall) {
            // Store all local variables (both user defined and temporary) on the stack
            variables.forEach(v -> assembler.createPush(stack, v));
        }

        setupFunctionParameters(function, function.getParameters(), arguments, recursiveCall);

        // Recursive function call
        final LogicLabel returnLabel = assembler.nextLabel();
        assembler.createCallRecursive(stack, function.getLabel(), returnLabel, function.getFnRetVal());
        assembler.createLabel(returnLabel); // where the function must return

        retrieveFunctionParameters(function, function.getParameters(), arguments, recursiveCall);

        if (recursiveCall) {
            // Restore all local variables (both user defined and temporary) from the stack
            Collections.reverse(variables);
            variables.forEach(v -> assembler.createPop(stack, v));
        }

        return passReturnValue(function, null);
    }

    private LogicVariable getRemoteProcessor(MindcodeFunction function, @Nullable ValueStore target) {
        return switch(target) {
            case LogicVariable v when v.isGlobalVariable() || v.getType() == ArgumentType.BLOCK -> v;
            case null -> (LogicVariable) evaluate(function.getModule().getRemoteProcessors().getFirst());
            default -> {
                error(target.sourcePosition(), ERR.REMOTE_WRONG_PROCESSOR);
                yield LogicVariable.INVALID;
            }
        };
    }

    private ValueStore handleRemoteFunctionCall(MindcodeFunction function, List<FunctionArgument> arguments,
            @Nullable ValueStore target, boolean async) {
        assert !function.getModule().getRemoteProcessors().isEmpty();
        LogicVariable processor = getRemoteProcessor(function, target);

        List<FunctionParameter> parameters = target == null
                ? function.getParameters() : function.createRemoteParameters(assembler, processor);

        // Function parameters will be set up using RemoteVariable value stores created for the remote function
        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        setupFunctionParameters(function, parameters, arguments, false);

        LogicString mlogFinished = function.getFnFinished().getMlogString();
        assembler.setSubcontextType(function, AstSubcontextType.REMOTE_CALL);
        assembler.createWrite(LogicBoolean.FALSE, processor, mlogFinished);
        assembler.createWrite(LogicNumber.create(function.getRemoteIndex()), processor, LogicString.create("@counter"));

        if (async) return LogicVoid.VOID;

        LogicLabel label = assembler.createNextLabel();
        assembler.createYieldExecution();
        LogicVariable tmp = assembler.nextTemp();
        assembler.createRead(tmp, processor, mlogFinished);
        assembler.createJump(label, Condition.EQUAL, tmp, LogicBoolean.FALSE);

        assembler.setSubcontextType(function, AstSubcontextType.PARAMETERS);
        retrieveFunctionParameters(function, parameters, arguments, false);

        return passReturnValue(function, processor);
    }

    private @Nullable RemoteFunction verifyRemoteFunctionName(AstFunctionCall call, boolean outputValue) {
        if (call.getArguments().size() != 1) {
            if (!outputValue || call.getArguments().size() != 2 || !call.getArgument(1).hasOutModifier()) {
                error(call, FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                        call.getFunctionName(), outputValue ? 1 : 2, call.getArguments().size());
                return null;
            }
        }

        AstExpression functionNode = Objects.requireNonNull(call.getArgument(0).getExpression());

        AstIdentifier functionName = switch (functionNode) {
            case AstIdentifier identifier -> identifier;
            case AstMemberAccess memberAccess -> memberAccess.getMember();
            default -> null;
        };

        if (functionName == null) {
            error(call, ERR.REMOTE_WRONG_ARGUMENT, call.getFunctionName());
            evaluate(functionNode);
            return null;
        }

        List<MindcodeFunction> functions = callGraph.getFunctions().stream()
                .filter(f -> f.isRemote() && f.getName().equals(functionName.getName())).toList();
        if (functions.isEmpty()) {
            error(call, ERR.REMOTE_WRONG_ARGUMENT, call.getFunctionName());
            return null;
        } else if (functions.size() > 1) {
            error(call, ERR.REMOTE_MULTIPLE_FUNCTIONS, functionName.getName());
            return null;
        } else {
            MindcodeFunction function = functions.getFirst();
            AstExpression processorExpression = functionNode instanceof AstMemberAccess memberAccess
                ? memberAccess.getObject() : function.getModule().getRemoteProcessors().getFirst();
            ValueStore processor = evaluate(processorExpression);

            if (processor instanceof LogicVariable v && (v.isGlobalVariable() || v.getType() == ArgumentType.BLOCK)) {
                return new RemoteFunction(function, v);
            } else {
                error(processorExpression, ERR.REMOTE_WRONG_PROCESSOR);
                return null;
            }
        }
    }

    public ValueStore handleVerifySignature(AstFunctionCall call) {
        if (call.getArguments().size() != 1) {
            error(call, FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                    call.getFunctionName(), 1, call.getArguments().size());
            return LogicVariable.INVALID;
        }

        AstExpression expression = Objects.requireNonNull(call.getArgument(0).getExpression());
        if (!(expression instanceof AstIdentifier remoteProcessorId) || !(evaluate(expression) instanceof LogicVariable remoteProcessor)
                || !remoteProcessor.isGlobalVariable() && remoteProcessor.getType() != ArgumentType.BLOCK) {
            error(call.getArgument(0), ERR.REMOTE_WRONG_PROCESSOR);
            return LogicVariable.INVALID;
        }

        // Find module using callGraph.
        // This is very convoluted, but essentially a temporary solution until types get introduced
        Optional<AstModule> module = callGraph.getFunctions().stream()
                .filter(f -> f.isRemote() && f.getModule().matchesProcessor(remoteProcessorId.getName()))
                .map(MindcodeFunction::getModule)
                .findFirst();

        if (module.isEmpty()) {
            error(call.getArgument(0), ERR.REMOTE_WRONG_PROCESSOR);
            return LogicVariable.INVALID;
        }

        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        String remoteSignature = createRemoteSignature(module.get());
        LogicString initializedName = LogicString.create(nameCreator.remoteSignature());
        LogicVariable tmp = assembler.unprotectedTemp();
        LogicLabel label = assembler.createNextLabel();
        assembler.createRead(tmp, remoteProcessor, initializedName);
        assembler.createJump(label, Condition.EQUAL, tmp, LogicNull.NULL);
        LogicVariable result = assembler.nextNodeResultTemp();
        assembler.createOp(Operation.EQUAL, result, tmp, LogicString.create(remoteSignature));
        assembler.clearSubcontextType();

        return result;
    }

    public ValueStore handleFinished(AstFunctionCall call) {
        RemoteFunction remote = verifyRemoteFunctionName(call, true);
        if (remote == null) return LogicVariable.INVALID;

        final List<FunctionArgument> arguments = processArguments(call);

        assembler.setSubcontextType(remote.function, AstSubcontextType.REMOTE_CALL);
        LogicVariable result = assembler.nextNodeResultTemp();
        assembler.createRead(result, remote.processor, remote.function.getFnFinished().getMlogString());
        if (arguments.size() > 1) {
            LogicVariable tmp = assembler.nextTemp();
            assembler.createRead(tmp, remote.processor, remote.function.getFnRetVal().getMlogString());
            arguments.get(1).setValue(assembler, tmp);
        }
        assembler.clearSubcontextType();

        return result;
    }

    public ValueStore handleAwait(AstFunctionCall call) {
        RemoteFunction remote = verifyRemoteFunctionName(call, false);
        if (remote == null) return LogicVariable.INVALID;

        assembler.setSubcontextType(remote.function, AstSubcontextType.REMOTE_CALL);
        LogicLabel label = assembler.createNextLabel();
        assembler.createYieldExecution();
        LogicVariable tmp = assembler.nextTemp();
        assembler.createRead(tmp, remote.processor, remote.function.getFnFinished().getMlogString());
        assembler.createJump(label, Condition.EQUAL, tmp, LogicBoolean.FALSE);
        assembler.clearSubcontextType();

        return new RemoteVariable(remote.function.getSourcePosition(), remote.processor, remote.function.getName() + "()",
                remote.function.getFnRetVal().getMlogString(),
                assembler.nextTemp(), false, false);
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
        return functionParameter.isInput()
               && argument.unwrap() instanceof LogicVariable variable
               && function.isInputFunctionParameter(variable)
               && !functionParameter.equals(argument.unwrap());
    }

    private void setupFunctionParameters(MindcodeFunction function, List<FunctionParameter> parameters,
            List<FunctionArgument> arguments, boolean recursiveCall) {
        // List of values to be passed to parameters
        Queue<@NonNull ValueStore> argumentValues = new ArrayDeque<>(arguments.size());
        int limit = Math.min(arguments.size(), function.getDeclaredParameters().size());

        for (int index = 0; index < limit; index++) {
            FunctionArgument argument = arguments.get(index);
            if (function.getDeclaredParameter(index).isReference()) {
                argumentValues.offer(argument);
            } else if (function.getDeclaredParameter(index).isInput()) {
                if (misplacedInput(function, argument, parameters.get(index))) {
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
            AstFunctionParameter parameter = function.getDeclaredParameter(index);
            if (parameter.isReference()) {
                ValueStore argument = argumentValues.remove();
                variables.replaceFunctionVariable(parameter.getIdentifier(), argument.unwrap());
            } else if (parameter.isInput()) {
                ValueStore argument = argumentValues.remove();
                if (function.getDeclaration().isInline() && !argument.unwrap().isMlogRepresentable()) {
                    variables.replaceFunctionVariable(parameter.getIdentifier(), argument.unwrap());
                } else {
                    parameters.get(index).setValue(assembler, argument.getValue(assembler));
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
        return argument.isOutput()
               && argument.unwrap() instanceof LogicVariable variable
               && function.isOutputFunctionParameter(variable)
               && !functionParameter.equals(argument.unwrap());
    }

    private void retrieveFunctionParameters(MindcodeFunction function, List<FunctionParameter> parameters,
            List<FunctionArgument> arguments, boolean recursiveCall) {
        List<Tuple2<ValueStore, LogicVariable>> finalizers = new ArrayList<>();

        // Setup variables representing function parameters with values from this call
        for (int i = 0; i < arguments.size(); i++) {
            FunctionArgument argument = arguments.get(i);
            if (argument.isOutput()) {
                FunctionParameter parameter = parameters.get(i);
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

    private ValueStore passReturnValue(MindcodeFunction function, @Nullable LogicVariable processor) {
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
            if (processor != null) {
                assembler.createRead(resultVariable, processor, function.getFnRetVal().getMlogString());
            } else {
                assembler.createSet(resultVariable, function.getFnRetVal());
            }
            return resultVariable;
        } else {
            // Use the function return value directly - there's only one place where it is produced
            // within this function's call tree
            return processor != null
                    ? new RemoteVariable(function.getSourcePosition(), processor, function.getName() + "()",
                    function.getFnRetVal().getMlogString(),assembler.nextTemp(),
                        false, false)
                    :   function.getFnRetVal();
        }
    }

    private record RemoteFunction(MindcodeFunction function, LogicVariable processor) {
    }
}

