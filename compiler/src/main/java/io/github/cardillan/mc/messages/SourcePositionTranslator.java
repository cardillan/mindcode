package io.github.cardillan.mc.messages;

import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcePositionTranslator {
    SourcePositionTranslator EMPTY = position -> position;

    SourcePosition apply(SourcePosition position);
}
