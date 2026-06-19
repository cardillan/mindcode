package info.teksol.mc.messages;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcePositionTranslator {
    SourcePositionTranslator EMPTY = position -> position;

    SourcePosition apply(SourcePosition position);
}
