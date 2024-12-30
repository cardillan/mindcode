package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRequire;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstBuilderContext extends CompilerContext {
    void addRequirement(AstRequire requirement);
}
