package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import java.util.function.Consumer;

public interface Optimizer extends LogicInstructionPipeline {

    String getName();

    void setDebugPrinter(DebugPrinter debugPrinter);

    void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient);

}
