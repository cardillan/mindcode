package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.v3.CompilerContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstBuilderContext extends CompilerContext {
    void addRequirement(AstRequire requirement);
}
