package info.teksol.mindcode;

import java.util.function.Function;

public interface MindcodeMessage {

    default InputPosition inputPosition() {
        return InputPosition.EMPTY;
    }

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

    default String formatMessage() {
        return message();
    }

    default String formatMessage(Function<InputPosition, String> positionFormatter) {
        return inputPosition().isEmpty() ? formatMessage() : positionFormatter.apply(inputPosition()) + " " + formatMessage();
    }
}
