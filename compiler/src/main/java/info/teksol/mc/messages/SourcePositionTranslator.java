package info.teksol.mc.messages;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcePositionTranslator {

    SourcePosition apply(SourcePosition position);
}
