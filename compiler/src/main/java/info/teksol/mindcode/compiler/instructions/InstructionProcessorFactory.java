package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.logic.MindustryOpcodeVariants;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;

import java.util.List;
import java.util.function.Consumer;

public class InstructionProcessorFactory {

    public static InstructionProcessor getInstructionProcessor(Consumer<MindcodeMessage> messageConsumer, CompilerProfile profile) {
        return getInstructionProcessor(messageConsumer, profile.getProcessorVersion(), profile.getProcessorEdition());
    }

    public static InstructionProcessor getInstructionProcessor(Consumer<MindcodeMessage> messageConsumer,
            ProcessorVersion version, ProcessorEdition edition) {
        return new BaseInstructionProcessor(messageConsumer, version, edition, MindustryOpcodeVariants.getSpecificOpcodeVariants(version, edition));
    }

    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition) {
        return new BaseInstructionProcessor(msg ->{}, version, edition, MindustryOpcodeVariants.getSpecificOpcodeVariants(version, edition));
    }

    // To be used by unit tests - returns new, non-cached instances based off whatever processor is given
    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition,
            List<OpcodeVariant> opcodeVariants) {
        return new BaseInstructionProcessor(msg -> {}, version, edition, opcodeVariants);
    }
    
    private InstructionProcessorFactory() { }
}
