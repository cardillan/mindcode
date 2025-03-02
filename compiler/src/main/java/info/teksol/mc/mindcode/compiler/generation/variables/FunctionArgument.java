package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Represents an argument to a function (user-defined or built-in). Provides actual argument value
/// if one was provided in the function call, plus information about possible `in`/`out` modifiers
/// used at the call site.
@NullMarked
public interface FunctionArgument extends ValueStore {

    /// @return `true` if an actual value was supplied with this argument
    boolean hasValue();

    /// Provides the actual value of the argument. If the argument has no value, an invalid
    /// placeholder is used.
    ///
    /// @return the value of this argument
    ValueStore getArgumentValue();

    /// @return `true` if the `in` modifier was used with this argument
    boolean hasInModifier();

    /// @return `true` if the `out` modifier was used with this argument
    boolean hasOutModifier();

    /// @return `true` if the argument is effectively input
    boolean isInput();

    /// @return `true` if the argument is effectively output
    boolean isOutput();

    /// Determines that the argument has only the in modifier, meaning it is passed as input-only
    /// argument to an input-output function parameter. No other combination of modifiers can achieve that.
    ///
    /// @return `true` if the argument is input only argument to an input-output parameter
    default boolean isInputOnly() {
        return hasInModifier() && !hasOutModifier();
    }

    /// Validates function arguments in calls to functions that only have input parameters
    default FunctionArgument validateAsInput(MessageConsumer messageConsumer) {
        if (!hasValue()) {
            messageConsumer.addMessage(CompilerMessage.error(sourcePosition(), ERR.ARGUMENT_UNNAMED_NOT_OPTIONAL));
        }
        if (hasOutModifier()) {
            messageConsumer.addMessage(CompilerMessage.error(sourcePosition(), ERR.ARGUMENT_UNNAMED_OUT_MODIFIER_REQUESTED));
        }
        return this;
    }

    /// Validates function arguments in calls to functions that only have input parameters
    static void validateAsInput(MessageConsumer messageConsumer, List<FunctionArgument> arguments) {
        arguments.forEach(a -> a.validateAsInput(messageConsumer));
    }
}
