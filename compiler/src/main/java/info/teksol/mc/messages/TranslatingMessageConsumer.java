package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record TranslatingMessageConsumer(MessageConsumer consumer, SourcePositionTranslator translator) implements MessageConsumer {

    @Override
    public void addMessage(MindcodeMessage message) {
        consumer.accept(message.translatePosition(translator));
    }
}
