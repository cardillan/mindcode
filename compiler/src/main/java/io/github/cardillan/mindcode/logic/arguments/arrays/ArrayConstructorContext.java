package io.github.cardillan.mindcode.logic.arguments.arrays;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.callgraph.CallGraphCreatorContext;

public interface ArrayConstructorContext extends CallGraphCreatorContext {
    AstContext rootAstContext();
}
