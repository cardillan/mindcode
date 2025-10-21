package info.teksol.mc.mindcode.compiler.astcontext;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDocComment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstNodeScope;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class TestNode implements AstMindcodeNode {
    private final AstContextType contextType;
    private final AstSubcontextType subcontextType;
    private CompilerProfile profile;

    public TestNode(CompilerProfile profile, AstContextType contextType, AstSubcontextType subcontextType) {
        this.profile = profile;
        this.contextType = contextType;
        this.subcontextType = subcontextType;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    @Override
    public void setProfile(CompilerProfile profile) {
        this.profile = profile;
    }

    @Override
    public CompilerProfile getProfile() {
        return profile;
    }

    @Override
    public @Nullable AstDocComment getDocComment() {
        return null;
    }

    @Override
    public void setDocComment(@Nullable AstDocComment docComment) {
    }

    @Override
    public List<AstMindcodeNode> getChildren() {
        return List.of();
    }

    @Override
    public SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    @Override
    public AstContextType getContextType() {
        return contextType;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return subcontextType;
    }
}
