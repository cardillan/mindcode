package io.github.cardillan.mindcode.compiler.generation;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.generation.variables.Variables;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CodeAssemblerContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    Variables variables();
    AstContext rootAstContext();
}
