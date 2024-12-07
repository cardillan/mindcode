package info.teksol.mc.messages;

import org.jetbrains.annotations.NotNull;

public class TranslatingMessageConsumer implements MessageConsumer {
     private final MessageConsumer consumer;
     private final SourcePositionTranslator translator;

     public TranslatingMessageConsumer(MessageConsumer consumer, SourcePositionTranslator translator) {
          this.consumer = consumer;
          this.translator = translator;
     }

     @Override
     public void addMessage(@NotNull MindcodeMessage message) {
          consumer.accept(message.translatePosition(translator));
     }
}
