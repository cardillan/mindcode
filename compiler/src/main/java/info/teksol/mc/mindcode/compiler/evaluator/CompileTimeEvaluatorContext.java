package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompileTimeEvaluatorContext extends MessageContext {
    GlobalCompilerProfile globalCompilerProfile();
    InstructionProcessor instructionProcessor();
    CallGraph callGraph();
    Variables variables();
}
