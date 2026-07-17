package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CodeAssemblerContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    Variables variables();
    AstContext rootAstContext();
}
