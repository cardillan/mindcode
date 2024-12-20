package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluatorContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CodeGeneratorContext extends CompileTimeEvaluatorContext {
    CompilerProfile compilerProfile();
    InstructionProcessor instructionProcessor();
    CompileTimeEvaluator compileTimeEvaluator();
    @Nullable AstAllocation stackAllocation();
    @Nullable AstAllocation heapAllocation();
    CallGraph callGraph();
}
