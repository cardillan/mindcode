package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.AstCommonNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AstMindcodeNode extends AstCommonNode {

    @Nullable AstDocComment getDocComment();

    List<AstMindcodeNode> getChildren();
}
