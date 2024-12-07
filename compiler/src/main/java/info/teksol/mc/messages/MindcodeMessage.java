package info.teksol.mc.messages;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.common.SourcePosition;

public interface MindcodeMessage {

    default SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    default MindcodeMessage translatePosition(SourcePositionTranslator translator) {
        return this;
    }

    /**
     * Unstable messages may change from run to run, and therefore aren't suitable for storing
     * in log files which are under version control.
     */
    default boolean isStable() {
        return true;
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

    default String formatMessage(PositionFormatter positionFormatter) {
        return sourcePosition().isEmpty() ? formatMessage() : positionFormatter.apply(sourcePosition()) + " " + formatMessage();
    }
}
