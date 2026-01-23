package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// Represents a class capable of accepting and processing messages generated during the compilation.
/// Default convenience methods are provided for generating various kinds of messages.
///
/// In the test code, the ExpectedMessages implementation provides mechanisms to validate the compiler
/// generates only the expected messages.
///
/// Variables holding instances of this interface should be named "messageConsumer",
/// or "expectedMessages" in the test code.
@FunctionalInterface
@NullMarked
public interface MessageConsumer extends Consumer<MindcodeMessage> {

    void addMessage(MindcodeMessage message);

    default void accept(MindcodeMessage message) {
        addMessage(message);
    }
}
