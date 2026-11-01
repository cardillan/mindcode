package io.github.cardillan.mc.mindcode.compiler.functions;

import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeAssembler;
import io.github.cardillan.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FunctionMapperContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    CodeAssembler assembler();
}
