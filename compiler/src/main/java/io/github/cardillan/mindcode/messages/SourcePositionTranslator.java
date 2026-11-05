package io.github.cardillan.mindcode.messages;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcePositionTranslator {
    SourcePositionTranslator EMPTY = position -> position;

    SourcePosition apply(SourcePosition position);
}
