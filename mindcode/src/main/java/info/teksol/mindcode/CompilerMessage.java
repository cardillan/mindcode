package info.teksol.mindcode;

import java.util.function.Function;

public interface CompilerMessage extends MindcodeMessage {

    InputPosition inputPosition();

    default String formatMessage(Function<InputPosition, String> positionFormatter) {
        return inputPosition() != null ? positionFormatter.apply(inputPosition()) + " " + message() : message();
    }
}
