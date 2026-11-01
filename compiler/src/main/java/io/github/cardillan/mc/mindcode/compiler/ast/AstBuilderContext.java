package io.github.cardillan.mc.mindcode.compiler.ast;

import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstRequire;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstBuilderContext extends MessageContext {
    void addRequirement(AstRequire requirement);
}
