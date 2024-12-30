package info.teksol.mc.messages;

import info.teksol.mc.common.AstElement;
import info.teksol.mc.common.InputPosition;
import org.intellij.lang.annotations.PrintFormat;

public interface MessageLogger {

    void error(AstElement node, @PrintFormat String format, Object... args);

    void error(InputPosition position, @PrintFormat String format, Object... args);

    void error(@PrintFormat String format, Object... args);

    void warn(AstElement node, @PrintFormat String format, Object... args);

    void warn(InputPosition position, @PrintFormat String format, Object... args);

    void info(@PrintFormat String format, Object... args);

    void timing(@PrintFormat String format, Object... args);

    void debug(String message);
}
