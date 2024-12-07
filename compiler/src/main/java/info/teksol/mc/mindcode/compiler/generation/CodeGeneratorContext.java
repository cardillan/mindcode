package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstAllocation;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluatorContext;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapperContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CodeGeneratorContext extends
        CodeAssemblerContext,
        CompileTimeEvaluatorContext,
        FunctionMapperContext {
    CompilerProfile compilerProfile();
    InstructionProcessor instructionProcessor();
    CompileTimeEvaluator compileTimeEvaluator();
    ReturnStack returnStack();
    StackTracker stackTracker();
    void setHeapAllocation(AstAllocation heapAllocation);
    @Nullable AstAllocation heapAllocation();
    CallGraph callGraph();
    AstContext rootAstContext();
    CodeAssembler assembler();
    Variables variables();
}
