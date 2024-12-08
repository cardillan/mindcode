package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;

public interface AstBuilderContext {

    void addRequirement(AstRequire requirement);
}
