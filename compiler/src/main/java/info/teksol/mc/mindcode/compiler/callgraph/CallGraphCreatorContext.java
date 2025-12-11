package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallGraphCreatorContext extends MessageContext {
    GlobalCompilerProfile globalCompilerProfile();
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
}
