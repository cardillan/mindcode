package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstRequire extends AstDeclaration {

    public AstRequire(SourcePosition sourcePosition, AstMindcodeNode child) {
        super(sourcePosition, children(child));
    }

    public abstract boolean isLibrary();

    public abstract String getName();
}
