package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.logic.MindustryOpcodeVariants;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;

public class InstructionProcessorFactory {

    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition) {
        return new BaseInstructionProcessor(version, edition, MindustryOpcodeVariants.getSpecificOpcodeVariants(version, edition));
    }

    private InstructionProcessorFactory() { }
}
