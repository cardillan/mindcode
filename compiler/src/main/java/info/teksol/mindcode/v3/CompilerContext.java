package info.teksol.mindcode.v3;

/**
 * Represents a context used during compilation.
 * Provides utilities and services needed during the compilation process.
 * Specific interfaces extending this interface are created for specific classes
 * accessing the context, providing only the necessary methods for the class.
 * <p>
 * Where possible, the context is passed as a parameter to the class that needs it.
 * In other cases the context can be accesses via appropriate static method of the
 * {@code MindcodeCompiler} class.
 * <p>
 * Context interfaces declare getter methods without the {@code get} prefix, so that
 * a record matching the interface can be easily created for testing.
 */
public interface CompilerContext {
    MessageConsumer messageConsumer();
}
