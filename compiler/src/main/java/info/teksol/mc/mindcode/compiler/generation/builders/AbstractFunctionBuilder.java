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
    /// 1:1 except varargs, where the vararg argument is mapped to the full list of values.
    protected void convertArguments(AstFunctionArgument argument, Consumer<FunctionArgument> consumer) {
        if (variables.isVarargParameter(argument.getExpression())) {
            variables.getVarargs().forEach(consumer);
        } else {
            consumer.accept(convertArgument(argument));
        }
    }

    protected FunctionArgument convertArgument(AstFunctionArgument argument) {
        if (!argument.hasModifier() && argument.getExpression() instanceof AstIdentifier identifier) {
            return new IdentifierFunctionArgument(() -> evaluate(identifier), identifier);
        } else if (argument.hasExpression()) {
            final ValueStore value = evaluate(Objects.requireNonNull(argument.getExpression()));
            if (value == LogicVoid.VOID) {
                warn(argument, ERR.VOID_ARGUMENT);
            }

            if (argument.isOutput()) {
                // Only provide the transfer variable when needed - for complex values
                return new OutputFunctionArgument(argument, resolveLValue(argument.getExpression(), value),
                        value.isComplex() ? assembler.nextTemp() : null);
            } else {
                return new InputFunctionArgument(argument, value);
            }
        } else {
            return new MissingFunctionArgument(argument);
        }
    }
}
