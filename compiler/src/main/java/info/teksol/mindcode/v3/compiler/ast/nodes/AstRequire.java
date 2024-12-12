package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstRequire extends AstDeclaration {

    public AstRequire(InputPosition inputPosition) {
        super(inputPosition);
    }

    public abstract boolean isLibrary();

    public abstract String getName();
}
