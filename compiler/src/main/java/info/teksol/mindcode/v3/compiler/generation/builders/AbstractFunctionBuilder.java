package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.variables.*;
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
            error(call, "Function '%s': wrong number of arguments (expected %d, found %d).",
                    call.getFunctionName(), expectedCount, arguments.size());
            return false;
        }

        return true;
    }

    /// Creates a list of FunctionArgument instances representing arguments of this function call.
    /// Resolves vararg parameters passed into another function into the full list of values
    /// passed as varargs to current function.
    protected List<FunctionArgument> processArguments(AstFunctionCall call) {
        return call.getArguments().stream().mapMulti(this::convertArguments).toList();
    }

    /// Converts argument declarations from function call to actual argument values. The mapping is
    /// 1:1 except varargs, where the vararg argument is mapped to the full list of values.
    private void convertArguments(AstFunctionArgument argument, Consumer<FunctionArgument> consumer) {
        if (variables.isVarargParameter(argument.getExpression())) {
            variables.getVarargs().forEach(consumer);
        } else {
            consumer.accept(convertArgument(argument));
        }
    }

    private FunctionArgument convertArgument(AstFunctionArgument argument) {
        if (argument.hasExpression()) {
            final ValueStore value = evaluate(Objects.requireNonNull(argument.getExpression()));
            if (value == LogicVoid.VOID) {
                warn(argument, "Expression doesn't have any value. Using no-value expressions in function calls is deprecated.");
            }

            if (argument.isOutput()) {
                // Only provide the transfer variable when needed - for complex values
                return new OutputFunctionArgument(argument, resolveLValue(argument.getExpression()),
                        value.isComplex() ? nextTemp() : null);
            } else {
                return new InputFunctionArgument(argument, value);
            }
        } else {
            return new MissingFunctionArgument(argument);
        }
    }
}
