package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstAllocation;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstModule;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapperContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CodeGeneratorContext extends
        CodeAssemblerContext,
        CompileTimeEvaluatorContext,
        FunctionMapperContext {
    boolean hasErrors();
    CompileTimeEvaluator compileTimeEvaluator();
    ReturnStack returnStack();
    StackTracker stackTracker();
    AstModule getModule(AstRequire node);
    void setHeapAllocation(AstAllocation heapAllocation);
    @Nullable AstAllocation heapAllocation();
    MindustryMetadata metadata();
    CallGraph callGraph();
    Variables variables();

    void addRemoteVariable(LogicVariable variable);
}
