package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.function.Consumer;

public interface Optimizer extends LogicInstructionPipeline {

    String getName();

    void setDebugPrinter(DebugPrinter debugPrinter);

    void setMessagesRecipient(Consumer<String> messagesRecipient);

}
