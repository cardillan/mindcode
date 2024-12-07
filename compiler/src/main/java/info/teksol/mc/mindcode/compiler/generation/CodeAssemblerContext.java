package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;

public interface CodeAssemblerContext extends CompilerContext {
    CompilerProfile compilerProfile();
    InstructionProcessor instructionProcessor();
    AstContext rootAstContext();
}
