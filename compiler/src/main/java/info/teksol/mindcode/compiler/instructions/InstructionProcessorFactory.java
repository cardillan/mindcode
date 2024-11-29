package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.instructions.BaseInstructionProcessor.InstructionProcessorParameters;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;

import java.util.List;
import java.util.function.Consumer;

public class InstructionProcessorFactory {
    private static final Consumer<MindcodeMessage> nullMessageConsumer = msg -> {};

    public static InstructionProcessor getInstructionProcessor(Consumer<MindcodeMessage> messageConsumer, CompilerProfile profile) {
        return create(new InstructionProcessorParameters(messageConsumer,
                profile.getProcessorVersion(), profile.getProcessorEdition()));
    }

    public static InstructionProcessor getInstructionProcessor(Consumer<MindcodeMessage> messageConsumer,
            ProcessorVersion version, ProcessorEdition edition) {
        return create(new InstructionProcessorParameters(messageConsumer, version, edition));
    }

    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition) {
        return create(new InstructionProcessorParameters(nullMessageConsumer, version, edition));
    }

    // To be used by unit tests - returns new, non-cached instances based off whatever processor is given
    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition,
            List<OpcodeVariant> opcodeVariants) {
        return create(new InstructionProcessorParameters(nullMessageConsumer, version, edition, opcodeVariants));
    }

    private static InstructionProcessor create(InstructionProcessorParameters parameters) {
        return switch (parameters.version) {
            case V6, V7, V7A    -> new MindustryInstructionProcessor7(parameters);
            case V8A, MAX       -> new MindustryInstructionProcessor8(parameters);
        };
    }

    private InstructionProcessorFactory() { }
}
