package info.teksol.mindcode.v3.compiler.ast;

import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;

public interface AstBuilderContext {

   MessageConsumer getMessageConsumer();

    void addRequirement(AstRequire requirement);

}
