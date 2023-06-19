package info.teksol.mindcode.compiler;

public interface CompilerMessage {

    String source();

    MessageLevel level();

    String message();

    default boolean isError() {
        return level() == MessageLevel.ERROR;
    }

    default boolean isWarning() {
        return level() == MessageLevel.WARNING;
    }

    default boolean isErrorOrWarning() {
        return isError() || isWarning();
    }

    default boolean isInfo() {
        return level() == MessageLevel.INFO;
    }

    default boolean isDebug() {
        return level() == MessageLevel.DEBUG;
    }
}
