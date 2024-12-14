package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.v3.MessageConsumer;
import org.intellij.lang.annotations.PrintFormat;

// TODO remove completely - always use messageConsumer, obtain from CompilerContext when unavailable.
//      Refactor after switching to the new compiler
@Deprecated
public interface MessageEmitter {
    MessageConsumer getMessageConsumer();

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
