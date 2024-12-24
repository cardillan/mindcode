package info.teksol.mindcode.v3.compiler.callgraph;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.CompilerContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CallGraphCreatorContext extends CompilerContext {
    InstructionProcessor instructionProcessor();
}
