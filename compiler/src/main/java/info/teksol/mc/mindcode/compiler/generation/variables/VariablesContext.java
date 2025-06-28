package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface VariablesContext extends CompilerContext {
    CompilerProfile compilerProfile();
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
    MindustryMetadata metadata();
}
