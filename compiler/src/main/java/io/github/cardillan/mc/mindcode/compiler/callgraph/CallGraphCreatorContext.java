package io.github.cardillan.mc.mindcode.compiler.callgraph;

import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallGraphCreatorContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
}
