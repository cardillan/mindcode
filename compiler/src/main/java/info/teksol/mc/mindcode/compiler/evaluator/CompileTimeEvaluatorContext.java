package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompileTimeEvaluatorContext extends CompilerContext {
    InstructionProcessor instructionProcessor();
    Variables variables();
}
