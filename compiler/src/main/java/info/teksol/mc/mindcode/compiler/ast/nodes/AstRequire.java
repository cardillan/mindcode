package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.SortedSet;

@NullMarked
public abstract class AstRequire extends AstDeclaration {

    public AstRequire(SourcePosition sourcePosition, AstMindcodeNode child) {
        super(sourcePosition, children(child));
    }

    public abstract boolean isLibrary();

    public abstract String getName();

    public abstract SortedSet<AstIdentifier> getProcessors();

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    public @Nullable AstIdentifier getProcessor() {
        return getProcessors().isEmpty() ? null : getProcessors().first();
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.INIT;
    }
}
