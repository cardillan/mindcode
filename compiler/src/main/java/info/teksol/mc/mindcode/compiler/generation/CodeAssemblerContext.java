package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CodeAssemblerContext extends CompilerContext {
    GlobalCompilerProfile globalCompilerProfile();
    InstructionProcessor instructionProcessor();
    Variables variables();
    AstContext rootAstContext();
}
