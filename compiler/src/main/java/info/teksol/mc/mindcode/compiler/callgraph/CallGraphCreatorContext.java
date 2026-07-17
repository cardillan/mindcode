package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallGraphCreatorContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
}
