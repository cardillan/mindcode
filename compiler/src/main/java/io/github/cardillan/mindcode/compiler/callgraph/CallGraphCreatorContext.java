package io.github.cardillan.mindcode.compiler.callgraph;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallGraphCreatorContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
}
