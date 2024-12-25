package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.MissingValue;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public abstract class AbstractFunctionBuilder extends AbstractBuilder {

    public AbstractFunctionBuilder(AbstractBuilder builder) {
        super(builder);
    }

    protected boolean validateStandardFunctionArguments(AstFunctionCall call, int expectedCount) {
        validateStandardFunctionArguments(call.getArguments());
        if (call.getArguments().size() != expectedCount) {
            error(call, "Function '%s': wrong number of arguments (expected %d, found %d).",
                    call.getFunctionName(), 3, call.getArguments().size());

            // Compile the arguments to report possible errors in them
            processArguments(call);
            return false;
        }

        return true;
    }

    protected void validateStandardFunctionArguments(List<AstFunctionArgument> arguments) {
        for (AstFunctionArgument argument : arguments) {
            if (!argument.hasExpression()) {
                error(argument, "Parameter corresponding to this argument isn't optional, a value must be provided.");
            }
            if (argument.hasOutModifier()) {
                error(argument, "Parameter corresponding to this argument isn't output, 'out' modifier cannot be used.");
            }
        }
    }

    protected List<FunctionArgument> processArguments(AstFunctionCall call) {
        // Do not track temporary variables created by evaluating function parameter expressions.
        // They'll be used solely to pass values to actual function parameters and won't be used subsequently
        // TODO What if there's a nested call to a recursive function - `foo(foo(1, 2), foo(3, 4))`? Hm?
        return variables.excludeVariablesFromTracking(() -> call.getArguments().stream()
                .<FunctionArgument>mapMulti((argument, consumer) -> {
                    if (variables.isVarargParameter(argument.getExpression())) {
                        variables.getVarargs().forEach(consumer);
                    } else {
                        consumer.accept(process(argument));
                    }
                }).toList());
    }

    private FunctionArgument process(AstFunctionArgument argument) {
        if (argument.hasExpression()) {
            final NodeValue value = evaluate(Objects.requireNonNull(argument.getExpression()));
            if (value == LogicVoid.VOID) {
                warn(argument, "Expression doesn't have any value. Using no-value expressions in function calls is deprecated.");
            }
            return new FunctionArgument(argument, value);
        } else {
            return new FunctionArgument(argument, MissingValue.UNSPECIFIED_FUNCTION_ARGUMENT);
        }
    }
}
