package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class AstRequire extends AstDeclaration {

    public AstRequire(SourcePosition sourcePosition, AstMindcodeNode child) {
        super(sourcePosition, children(child));
    }

    public abstract boolean isLibrary();

    public abstract String getName();

    public abstract @Nullable AstIdentifier getProcessor();

    @Override
    public AstContextType getContextType() {
        return AstContextType.INIT;
    }
}
