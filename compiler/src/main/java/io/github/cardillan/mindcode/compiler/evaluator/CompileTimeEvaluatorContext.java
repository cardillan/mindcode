package io.github.cardillan.mindcode.compiler.evaluator;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.compiler.callgraph.CallGraph;
import io.github.cardillan.mindcode.compiler.generation.variables.Variables;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompileTimeEvaluatorContext extends MessageContext {
    InstructionProcessor instructionProcessor();
    CallGraph callGraph();
    Variables variables();
}
