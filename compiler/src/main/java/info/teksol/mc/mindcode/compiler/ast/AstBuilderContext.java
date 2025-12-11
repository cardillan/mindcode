package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstBuilderContext extends MessageContext {
    void addRequirement(AstRequire requirement);
}
