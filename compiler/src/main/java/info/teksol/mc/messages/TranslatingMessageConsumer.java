package info.teksol.mc.messages;

import org.jetbrains.annotations.NotNull;

public class TranslatingMessageConsumer implements MessageConsumer {
     private final MessageConsumer consumer;
     private final InputPositionTranslator translator;

     public TranslatingMessageConsumer(MessageConsumer consumer, InputPositionTranslator translator) {
          this.consumer = consumer;
          this.translator = translator;
     }

     @Override
     public void addMessage(@NotNull MindcodeMessage message) {
          consumer.accept(message.translatePosition(translator));
     }
}
