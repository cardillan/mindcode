package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface VariablesContext extends MessageContext {
    GlobalCompilerProfile globalCompilerProfile();
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
    MindustryMetadata metadata();
}
