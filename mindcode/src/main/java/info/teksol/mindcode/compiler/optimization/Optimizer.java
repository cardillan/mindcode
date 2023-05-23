package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;
import java.util.function.Consumer;

public interface Optimizer {

    String getName();

    void setLevel(OptimizationLevel level);

    void setGoal(GenerationGoal goal);

    void setMessageRecipient(Consumer<CompilerMessage> messageRecipient);

    void setDebugPrinter(DebugPrinter debugPrinter);

    void optimizeProgram(List<LogicInstruction> program, AstContext rootContext);
}
