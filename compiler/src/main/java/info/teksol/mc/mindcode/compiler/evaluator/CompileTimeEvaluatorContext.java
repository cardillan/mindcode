package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompileTimeEvaluatorContext extends CompilerContext {
    CompilerProfile globalCompilerProfile();
    InstructionProcessor instructionProcessor();
    Variables variables();
}
