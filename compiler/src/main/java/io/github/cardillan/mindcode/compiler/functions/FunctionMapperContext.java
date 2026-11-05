package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.compiler.generation.CodeAssembler;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FunctionMapperContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    CodeAssembler assembler();
}
