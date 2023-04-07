package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.MindustryOpcodeVariants;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import java.util.List;

public class InstructionProcessorFactory {

    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition) {
        return new BaseInstructionProcessor(version, edition, MindustryOpcodeVariants.getSpecificOpcodeVariants(version, edition));
    }

    // To be used by unit tests - returns new, non-cached instances based off whatever processor is given
    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorEdition edition,
            List<OpcodeVariant> opcodeVariants) {
        return new BaseInstructionProcessor(version, edition, opcodeVariants);
    }
    
    private InstructionProcessorFactory() { }
}
