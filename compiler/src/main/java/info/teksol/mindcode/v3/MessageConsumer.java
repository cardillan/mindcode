package info.teksol.mindcode.v3;

import info.teksol.mindcode.MindcodeMessage;

import java.util.function.Consumer;

@FunctionalInterface
public interface MessageConsumer extends Consumer<MindcodeMessage> {
}
