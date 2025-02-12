package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallGraphCreatorContext extends CompilerContext {
    CompilerProfile compilerProfile();
    InstructionProcessor instructionProcessor();
}
