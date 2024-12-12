package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AstMindcodeNode extends AstElement {

    @Nullable AstDocComment getDocComment();

    List<AstMindcodeNode> getChildren();

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
