package info.teksol.mindcode.v3.compiler.callgraph;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.CompilerContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CallGraphCreatorContext extends CompilerContext {
    InstructionProcessor instructionProcessor();
    @Nullable AstAllocation stackAllocation();
}
