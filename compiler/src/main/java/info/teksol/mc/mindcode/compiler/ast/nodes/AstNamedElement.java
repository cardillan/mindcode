package info.teksol.mc.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstNamedElement extends AstMindcodeNode {
    String getName();
}
