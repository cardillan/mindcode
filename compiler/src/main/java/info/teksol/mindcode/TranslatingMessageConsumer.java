package info.teksol.mindcode;

import info.teksol.mindcode.v3.MessageConsumer;

public class TranslatingMessageConsumer implements MessageConsumer {
     private final MessageConsumer consumer;
     private final InputPositionTranslator translator;

     public TranslatingMessageConsumer(MessageConsumer consumer, InputPositionTranslator translator) {
          this.consumer = consumer;
          this.translator = translator;
     }

     @Override
     public void accept(MindcodeMessage message) {
          consumer.accept(message.translatePosition(translator));
     }
}
