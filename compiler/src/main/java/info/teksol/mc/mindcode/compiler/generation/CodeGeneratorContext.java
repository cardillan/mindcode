package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.ForcedVariableContext;
import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstAllocation;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstModule;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapperContext;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.compiler.preprocess.PreprocessorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public interface CodeGeneratorContext extends
        MessageContext,
        PreprocessorContext,
        CodeAssemblerContext,
        CompileTimeEvaluatorContext,
        FunctionMapperContext,
        ForcedVariableContext {
    boolean hasErrors();
    CompileTimeEvaluator compileTimeEvaluator();
    ReturnStack returnStack();
    StackTracker stackTracker();
    AstModule getModule(AstRequire node);
    void setHeapAllocation(AstAllocation heapAllocation);
    @Nullable AstAllocation heapAllocation();
    NameCreator nameCreator();
    MindustryMetadata metadata();
    CallGraph callGraph();
    Variables variables();

    void addForcedVariable(LogicVariable variable);
    Set<LogicVariable> getForcedVariables();
}
