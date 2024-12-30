package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.AstElement;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AstMindcodeNode extends AstElement {

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();

    @Nullable AstDocComment getDocComment();

    List<AstMindcodeNode> getChildren();
}
