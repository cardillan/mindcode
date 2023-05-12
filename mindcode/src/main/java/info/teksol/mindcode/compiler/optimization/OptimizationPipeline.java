package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Consumer;

import static info.teksol.mindcode.compiler.optimization.Optimization.values;

public class OptimizationPipeline implements LogicInstructionPipeline {
    private final LogicInstructionPipeline optimizationPipeline;

    private OptimizationPipeline(LogicInstructionPipeline optimizationPipeline) {
        this.optimizationPipeline = Objects.requireNonNull(optimizationPipeline);
    }

    @Override
    public void emit(LogicInstruction instruction) {
        optimizationPipeline.emit(instruction);
    }

    @Override
    public void flush() {
        optimizationPipeline.flush();
    }

    @Override
    public void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient) {
        optimizationPipeline.setMessagesRecipient(messagesRecipient);
    }

    public static LogicInstructionPipeline createPipelineForProfile(InstructionProcessor instructionProcessor,
            LogicInstructionPipeline terminus, CompilerProfile profile, DebugPrinter debugPrinter, Consumer<CompilerMessage> messageRecipient) {
        LogicInstructionPipeline pipeline = new InstructionCounter(terminus, messageRecipient,
                "%6d instructions after optimizations.", new NullDebugPrinter());

        Optimization[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            OptimizationLevel level = profile.getOptimizationLevel(values[i]);
            if (level != OptimizationLevel.OFF) {
                BaseOptimizer optimizer = values[i].createInstance(instructionProcessor, pipeline);
                optimizer.setLevel(level);
                optimizer.setMessagesRecipient(messageRecipient);
                optimizer.setDebugPrinter(debugPrinter);
                pipeline = optimizer;
            }
        }

        return new OptimizationPipeline(
                new InstructionCounter(pipeline, messageRecipient, "%6d instructions before optimizations.", debugPrinter)
        );
    }

    public static LogicInstructionPipeline createPipelineOf(InstructionProcessor instructionProcessor,
            LogicInstructionPipeline terminus, Optimization... optimization) {
        EnumSet<Optimization> includes = EnumSet.of(optimization[0], optimization);
        LogicInstructionPipeline pipeline = terminus;
        Optimization[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (includes.contains(values[i])) {
                pipeline = values[i].createInstance(instructionProcessor, pipeline);
            }
        }
        return new OptimizationPipeline(pipeline);
    }

    private static class InstructionCounter implements Optimizer {
        private final LogicInstructionPipeline next;
        private final Consumer<CompilerMessage> messageRecipient;
        private final String message;
        private final DebugPrinter debugPrinter;
        private int count = 0;

        public InstructionCounter(LogicInstructionPipeline next, Consumer<CompilerMessage> messageRecipient, String message,
                DebugPrinter debugPrinter) {
            this.next = next;
            this.messageRecipient = messageRecipient;
            this.message = message;
            this.debugPrinter = debugPrinter;
        }

        @Override
        public void emit(LogicInstruction instruction) {
            if (!(instruction instanceof LabelInstruction)) {
                count++;
            }
            debugPrinter.instructionEmitted(this, instruction);
            next.emit(instruction);
        }

        @Override
        public void flush() {
            messageRecipient.accept(MindcodeMessage.info(message, count));
            next.flush();
        }

        @Override
        public String getName() {
            return "InstructionCounter";
        }

        @Override
        public void setLevel(OptimizationLevel level) {
            // Do nothing
        }

        @Override
        public void setDebugPrinter(DebugPrinter debugPrinter) {
            // Do nothing - debugPrinter is set in constructor
        }

        @Override
        public void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient) {
            // Do nothing - messageRecipient is set in constructor
        }
    }
}
