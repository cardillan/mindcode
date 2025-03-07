package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstAllocation;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapperContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CodeGeneratorContext extends
        CodeAssemblerContext,
        CompileTimeEvaluatorContext,
        FunctionMapperContext {
    CompileTimeEvaluator compileTimeEvaluator();
    ReturnStack returnStack();
    StackTracker stackTracker();
    void setHeapAllocation(AstAllocation heapAllocation);
    @Nullable AstAllocation heapAllocation();
    CallGraph callGraph();
    Variables variables();
}
