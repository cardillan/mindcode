package io.github.cardillan.mc.mindcode.compiler.postprocess;

import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.compiler.callgraph.CallGraph;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.StackContext;
import io.github.cardillan.mc.mindcode.logic.instructions.InstructionProcessor;

public interface StackBuilderContext extends StackContext, MessageContext {
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
    AstContext rootAstContext();
    CallGraph callGraph();
}
