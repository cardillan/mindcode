package io.github.cardillan.mindcode.compiler.ast;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstRequire;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstBuilderContext extends MessageContext {
    void addRequirement(AstRequire requirement);
}
