package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.variables.*;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public abstract class AbstractFunctionBuilder extends AbstractBuilder {

    public AbstractFunctionBuilder(AbstractBuilder builder) {
        super(builder);
    }

    protected boolean validateStandardFunctionArguments(AstFunctionCall call, List<FunctionArgument> arguments, int expectedCount) {
        FunctionArgument.validateAsInput(messageConsumer(), arguments);
        if (arguments.size() != expectedCount) {
            error(call, ERR.FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                    call.getFunctionName(), expectedCount, arguments.size());
            return false;
        }

        return true;
    }

    /// Creates a list of FunctionArgument instances representing arguments of this function call.
    /// Resolves vararg parameters passed into another function into the full list of values
    /// passed as varargs to current function.
    protected List<FunctionArgument> processArguments(AstFunctionCall call) {
        return variables.excludeVariablesFromTracking(
                () -> call.getArguments().stream().mapMulti(this::convertArguments).toList());
    }

    /// Converts argument declarations from function call to actual argument values. The mapping is
    /// 1:1 except varargs and arrays, where the vararg/array arguments are mapped to the full list of values.
    protected void convertArguments(AstFunctionArgument argument, Consumer<FunctionArgument> consumer) {
        if (variables.isVarargParameter(argument.getExpression())) {
            variables.getVarargs().forEach(consumer);
        } else {
            convertArgument(argument, consumer);
        }
    }

    protected void convertArgument(AstFunctionArgument argument, Consumer<FunctionArgument> consumer) {
        if (!argument.hasModifier() && argument.getExpression() instanceof AstIdentifier identifier) {
            // If it is an array, expand it right here
            ValueStore valueStore = variables.findVariable(identifier.getName());
            if (valueStore instanceof ArrayStore array) {
                expandArray(argument, array, consumer);
            } else {
                consumer.accept(new IdentifierFunctionArgument(() -> evaluate(identifier), identifier, argument.hasRefModifier()));
            }
        } else if (argument.hasRefModifier()) {
            if (argument.getExpression() instanceof AstIdentifier identifier) {
                ValueStore valueStore = variables.findVariable(identifier.getName());
                consumer.accept(new IdentifierFunctionArgument(() -> evaluate(identifier), identifier, true));
            } else {
                error(argument, ERR.ARGUMENT_REF_IDENTIFIER_REQUESTED);
            }
        } else if (argument.hasExpression()) {
            final ValueStore value = evaluate(Objects.requireNonNull(argument.getExpression()));
            if (value == LogicVoid.VOID) {
                warn(argument, ERR.VOID_ARGUMENT);
            }

            if (value instanceof ArrayStore array && !argument.isReference()) {
                expandArray(argument, array, consumer);
            } else {
                consumer.accept(wrapArgumentValue(argument, value));
            }
        } else {
            consumer.accept(new MissingFunctionArgument(argument));
        }
    }

    protected void expandArray(AstFunctionArgument argument, ArrayStore array, Consumer<FunctionArgument> consumer) {
        array.getElements().stream().map(value -> wrapArgumentValue(argument, value)).forEach(consumer);
    }

    protected FunctionArgument wrapArgumentValue(AstFunctionArgument argument, ValueStore value) {
        if (argument.isOutput()) {
            // Only provide the transfer variable when needed - for complex values
            assert argument.getExpression() != null;
            return new OutputFunctionArgument(argument, resolveLValue(argument.getExpression(), value),
                    value.isComplex() ? assembler.nextTemp() : null);
        } else {
            return new InputFunctionArgument(argument, value);
        }
    }

    protected FunctionArgument convertArgument(AstFunctionArgument argument) {
        if (argument.hasExpression()) {
            final ValueStore value = evaluate(Objects.requireNonNull(argument.getExpression()));
            if (value == LogicVoid.VOID) {
                warn(argument, ERR.VOID_ARGUMENT);
            }
            return wrapArgumentValue(argument, value);
        } else {
            return new MissingFunctionArgument(argument);
        }
    }
}
