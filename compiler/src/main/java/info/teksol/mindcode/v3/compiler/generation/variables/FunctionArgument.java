package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Represents an argument to a function (user-defined or built-in). Provides actual argument value
/// if one was provided in the function call, plus information about possible `in`/`out` modifiers
/// used at the call site.
@NullMarked
public interface FunctionArgument extends NodeValue {

    ///@return the position of the argument in the source code
    InputPosition inputPosition();

    /// @return `true` if an actual value was supplied with this argument
    boolean hasValue();

    /// Provides the actual value of the argument. If the argument has no value, an invalid
    /// placeholder is used.
    ///
    /// @return the value of this argument
    NodeValue getArgumentValue();

    /// @return `true` if the `in` modifier was used with this argument
    boolean hasInModifier();

    /// @return `true` if the `out` modifier was used with this argument
    boolean hasOutModifier();

    /// @return `true` if the argument is effectively output
    boolean isOutput();

    /// Provides a variable to be passed as an output variable to an instruction.
    /// The value will be subsequently written
    LogicValue getTargetVariable(CodeAssembler assembler);

    /// Writes the resulting value
    void storeValue(CodeAssembler assembler);

    /// Validates function arguments in calls to functions that only have input parameters
    default boolean validateAsInput(MessageConsumer messageConsumer) {
        return validateMissingArgument(messageConsumer) && validateOutModifier(messageConsumer);
    }

    default boolean validateMissingArgument(MessageConsumer messageConsumer) {
        if (!hasValue()) {
            messageConsumer.addMessage(CompilerMessage.error(inputPosition(),
                    "Parameter corresponding to this argument isn't optional, a value must be provided."));
        }
        return hasValue();
    }

    default boolean validateOutModifier(MessageConsumer messageConsumer) {
        if (hasOutModifier()) {
            messageConsumer.addMessage(CompilerMessage.error(inputPosition(),
                    "Parameter corresponding to this argument isn't output, 'out' modifier cannot be used."));
        }
        return !hasOutModifier();
    }

    /// Validates function arguments in calls to functions that only have input parameters
    static boolean validateAsInput(MessageConsumer messageConsumer, List<FunctionArgument> arguments) {
        // Can't use allMatch, since that would stop validating further arguments upon first failure
        return arguments.stream().filter(a -> (a.validateAsInput(messageConsumer))).count() == arguments.size();
    }
}
