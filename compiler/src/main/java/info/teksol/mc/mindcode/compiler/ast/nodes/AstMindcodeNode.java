package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AstMindcodeNode extends SourceElement {

    /// Provides the scope of the current node. `AstProgram`, `AstModule` and `AstRequire` provide global scope;
    /// all other nodes correspond to either a local, or unspecified scope.
    ///
    /// `AstRequire` provides a global scope because remote variable declarations are handled in it.
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
    /// exceptions, declarations must reside in a global scope even in RELAXED and MIXED nodes.
    default boolean reportAllScopeErrors() {
        return false;
    }

    /// Sets the compiler profile for this AST node.
    void setProfile(CompilerProfile profile);

    /// Returns the compiler profile holding compiler options valid for this AST node.
    CompilerProfile getProfile();

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();

    @Nullable AstDocComment getDocComment();

    void setDocComment(@Nullable AstDocComment docComment);

    List<AstMindcodeNode> getChildren();
}
