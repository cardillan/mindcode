package info.teksol.mc.messages;

import info.teksol.mc.common.InputPosition;

public interface InputPositionTranslator {

    InputPosition apply(InputPosition position);
}
