package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.CompilerProfile;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.EnumSet;
import java.util.function.Consumer;

import static info.teksol.mindcode.mindustry.optimisation.Optimisation.values;

public class OptimisationPipeline implements LogicInstructionPipeline {
    private final LogicInstructionPipeline optimisationPipeline;

    private OptimisationPipeline(LogicInstructionPipeline optimisationPipeline) {
        this.optimisationPipeline = optimisationPipeline;
    }

    @Override
    public void emit(LogicInstruction instruction) {
        optimisationPipeline.emit(instruction);
    }

    @Override
    public void flush() {
        optimisationPipeline.flush();
    }

    public static LogicInstructionPipeline createPipelineForProfile(InstructionProcessor instructionProcessor,
            LogicInstructionPipeline terminus, CompilerProfile profile, DebugPrinter debugPrinter, Consumer<String> messageRecipient) {
        LogicInstructionPipeline pipeline = new InstructionCounter(terminus, messageRecipient,
                "%6d instructions after optimizations.", new NullDebugPrinter());

        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (profile.getOptimisations().contains(values[i])) {
                BaseOptimizer optimizer = values[i].createInstance(instructionProcessor, pipeline);
                optimizer.setMessagesRecipient(messageRecipient);
                optimizer.setDebugPrinter(debugPrinter);
                pipeline = optimizer;
            }
        }

        return new OptimisationPipeline(
                new InstructionCounter(pipeline, messageRecipient, "%6d instructions before optimizations.", debugPrinter)
        );
    }

    public static LogicInstructionPipeline createPipelineOf(InstructionProcessor instructionProcessor,
            LogicInstructionPipeline terminus, Optimisation... optimisation) {
        EnumSet<Optimisation> includes = EnumSet.of(optimisation[0], optimisation);
        LogicInstructionPipeline pipeline = terminus;
        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (includes.contains(values[i])) {
                pipeline = values[i].createInstance(instructionProcessor, pipeline);
            }
        }
        return new OptimisationPipeline(pipeline);
    }

    private static class InstructionCounter implements Optimizer {
        private final LogicInstructionPipeline next;
        private final Consumer<String> messageRecipient;
        private final String message;
        private final DebugPrinter debugPrinter;
        private int count = 0;

        public InstructionCounter(LogicInstructionPipeline next, Consumer<String> messageRecipient, String message,
                DebugPrinter debugPrinter) {
            this.next = next;
            this.messageRecipient = messageRecipient;
            this.message = message;
            this.debugPrinter = debugPrinter;
        }

        @Override
        public void emit(LogicInstruction instruction) {
            if (!instruction.isLabel()) {
                count++;
            }
            debugPrinter.instructionEmmited(this, instruction);
            next.emit(instruction);
        }

        @Override
        public void flush() {
            messageRecipient.accept(String.format(message, count));
            next.flush();
        }

        @Override
        public String getName() {
            return "InstructionCounter";
        }

        @Override
        public void setDebugPrinter(DebugPrinter debugPrinter) {
            // Do nothing - debugPrinter is set in constructor
        }

        @Override
        public void setMessagesRecipient(Consumer<String> messagesRecipient) {
            // Do nothing - messageRecipient is set in constructor
        }
    }
}
