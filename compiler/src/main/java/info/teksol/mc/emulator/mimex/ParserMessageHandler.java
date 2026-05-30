package info.teksol.mc.emulator.mimex;

import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ParserMessageHandler {
    boolean error(@PrintFormat String message, Object... args);
}
