package info.teksol.mc.messages;

import info.teksol.mc.common.SourcePosition;

public interface SourcePositionTranslator {

    SourcePosition apply(SourcePosition position);
}
