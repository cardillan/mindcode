package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.StackContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

public interface StackBuilderContext extends StackContext, MessageContext {
    InstructionProcessor instructionProcessor();
    NameCreator nameCreator();
    AstContext rootAstContext();
    CallGraph callGraph();
}
