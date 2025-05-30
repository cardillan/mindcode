package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.instructions.BaseInstructionProcessor.InstructionProcessorParameters;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class InstructionProcessorFactory {
    private static final MessageConsumer nullMessageConsumer = msg -> {};

    public static InstructionProcessor getInstructionProcessor(MessageConsumer messageConsumer, CompilerProfile profile) {
        return create(new InstructionProcessorParameters(messageConsumer, profile.getProcessorVersion(),
                profile.getProcessorEdition(), profile.isShortFunctionPrefix(), true));
    }

    public static InstructionProcessor getInstructionProcessorNoValidate(MessageConsumer messageConsumer, CompilerProfile profile) {
        return create(new InstructionProcessorParameters(messageConsumer, profile.getProcessorVersion(),
                profile.getProcessorEdition(), profile.isShortFunctionPrefix(), false));
    }

    public static InstructionProcessor getInstructionProcessor(MessageConsumer messageConsumer,
            ProcessorVersion version, ProcessorEdition edition) {
        return create(new InstructionProcessorParameters(messageConsumer, version, edition, false, false));
    }

    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition) {
        return create(new InstructionProcessorParameters(nullMessageConsumer, version, edition, false, true));
    }

    // To be used by unit tests - returns new, non-cached instances based off whatever processor is given
    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition,
            List<OpcodeVariant> opcodeVariants) {
        return create(new InstructionProcessorParameters(nullMessageConsumer, version, edition,
                false, true, opcodeVariants));
    }

    private static InstructionProcessor create(InstructionProcessorParameters parameters) {
        return switch (parameters.version) {
            case V6, V7, V7A    -> new MindustryInstructionProcessor7(parameters);
            case V8A, MAX       -> new MindustryInstructionProcessor8(parameters);
        };
    }

    private InstructionProcessorFactory() { }
}
