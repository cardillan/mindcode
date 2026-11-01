package io.github.cardillan.mc.mindcode.compiler.generation;

import io.github.cardillan.mc.mindcode.compiler.ForcedVariableContext;
import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstAllocation;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstModule;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstRequire;
import io.github.cardillan.mc.mindcode.compiler.callgraph.CallGraph;
import io.github.cardillan.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import io.github.cardillan.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import io.github.cardillan.mc.mindcode.compiler.functions.FunctionMapperContext;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.Variables;
import io.github.cardillan.mc.mindcode.compiler.preprocess.PreprocessorContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
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
    boolean hasCompilerErrors();
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
