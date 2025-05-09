package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AstMindcodeNode extends SourceElement {

    /// Defines scope of the current node. `AstProgram`, `AstModule` and `AstRequire` provide global scope, all other
    /// nodes correspond to either a local, or unspecified scope.
    ///
    /// `AstRequire` provides a global scope, because remote variable declarations are handled in it.
    ///
    /// @return the scope which is valid inside this node
    default AstNodeScope getScope() {
        return AstNodeScope.LOCAL;
    }

    /// Defines scope restriction. The node may only appear in the scope returned by this method.
    /// `AstNodeScope.NONE` means no restriction.
    ///
    /// @return the scope this node must reside in
    AstNodeScope getScopeRestriction();

    /// Enforces reporting scope violations as errors regardless of the current syntactic mode. With some possible
    /// exceptions, declarations must reside in global scope even in RELAXED and MIXED nodes.
    default boolean reportAllScopeErrors() {
        return false;
    }

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();

    @Nullable AstDocComment getDocComment();

    List<AstMindcodeNode> getChildren();
}
