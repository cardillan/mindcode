package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MessageEmitter;
import org.jspecify.annotations.NullMarked;

/// Represents a context used during compilation.
/// Provides utilities and services needed during the compilation process.
/// Specific interfaces extending this interface are created for specific classes
/// accessing the context, providing only the necessary methods for the class.
///
/// Where possible, the context is passed as a parameter to the class that needs it.
/// In other cases the context can be accesses via appropriate static method of the
/// `MindcodeCompiler` class.
///
/// Context interfaces declare getter methods without the `get` prefix, so that
/// a record matching the interface can be easily created for testing.
@NullMarked
public interface CompilerContext extends MessageEmitter {
    MessageConsumer messageConsumer();
}
