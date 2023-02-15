package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessorFactory;

public class CompilerFactory {

    public static Compiler createCompiler(CompilerProfile profile) {
        return new MindcodeCompiler(profile, createInstructionGenerator(profile));
    }

    private static InstructionProcessor createInstructionGenerator(CompilerProfile profile) {
        return InstructionProcessorFactory.getInstructionProcessor(profile.getProcessorVersion(), profile.getProcessorEdition());
    }
}
