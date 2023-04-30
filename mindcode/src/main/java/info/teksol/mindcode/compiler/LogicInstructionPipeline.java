package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.function.Consumer;

public interface LogicInstructionPipeline {
    void emit(LogicInstruction instruction);

    void flush();

    void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient);
}
