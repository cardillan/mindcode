package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstNodeHandler {
    NodeValue visit(AstMindcodeNode node);
}
