package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.CompilerContext;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CodeGeneratorContext extends CompilerContext {
    CompilerProfile compilerProfile();
    InstructionProcessor instructionProcessor();
    CallGraph callGraph();
}
