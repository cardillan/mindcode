package info.teksol.mc.messages;

import info.teksol.mc.common.AstElement;
import info.teksol.mc.common.InputPosition;
import org.intellij.lang.annotations.PrintFormat;

// TODO remove completely - always use messageConsumer, obtain from CompilerContext when unavailable.
//      Refactor after switching to the new compiler
@Deprecated
public interface MessageEmitter {
    MessageConsumer messageConsumer();

    void addMessage(MindcodeMessage message);

    void error(AstElement node, @PrintFormat String format, Object... args);

    void error(InputPosition position, @PrintFormat String format, Object... args);

    void error(@PrintFormat String format, Object... args);

    void warn(AstElement node, @PrintFormat String format, Object... args);

    void warn(InputPosition position, @PrintFormat String format, Object... args);

    void info(@PrintFormat String format, Object... args);

    void timing(@PrintFormat String format, Object... args);

    void debug(String message);
}
