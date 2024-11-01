package info.teksol.mindcode;

import java.util.function.Consumer;

public class TranslatingMessageConsumer implements Consumer<MindcodeMessage> {
     private final Consumer<MindcodeMessage> consumer;
     private final InputPositionTranslator translator;

     public TranslatingMessageConsumer(Consumer<MindcodeMessage> consumer, InputPositionTranslator translator) {
          this.consumer = consumer;
          this.translator = translator;
     }

     @Override
     public void accept(MindcodeMessage message) {
          consumer.accept(message.translatePosition(translator));
     }
}
