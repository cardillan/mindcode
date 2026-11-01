package io.github.cardillan.mc.mindcode.logic.arguments.arrays;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.compiler.callgraph.CallGraphCreatorContext;

public interface ArrayConstructorContext extends CallGraphCreatorContext {
    AstContext rootAstContext();
}
