package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraphCreatorContext;

public interface ArrayConstructorContext extends CallGraphCreatorContext {
    AstContext getRootAstContext();
}
