package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.function.Consumer;

public interface MessageEmitter {
    Consumer<MindcodeMessage> getMessageConsumer();

    void addMessage(MindcodeMessage message);

    void error(AstElement node, @PrintFormat String format, Object... args);

    void error(InputPosition position, @PrintFormat String format, Object... args);

    void warn(AstElement node, @PrintFormat String format, Object... args);

    void warn(InputPosition position, @PrintFormat String format, Object... args);
}
