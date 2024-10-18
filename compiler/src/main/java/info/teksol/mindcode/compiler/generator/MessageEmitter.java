package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.AstNode;
import org.intellij.lang.annotations.PrintFormat;

import java.util.function.Consumer;

public interface MessageEmitter {
    void error(AstNode node, @PrintFormat String format, Object... args);

    void error(InputPosition position, @PrintFormat String format, Object... args);

    Consumer<MindcodeMessage> getMessageConsumer();

    void warn(AstNode node, @PrintFormat String format, Object... args);

    void warn(InputPosition position, @PrintFormat String format, Object... args);
}
