package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.BaseInstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

public class CompilerFactory {

    public static Compiler createCompiler(CompilerProfile profile) {
        return new MindcodeCompiler(profile, createInstructionGenerator(profile));
    }

    private static InstructionProcessor createInstructionGenerator(CompilerProfile profile) {
        return new BaseInstructionProcessor();
    }
}
