package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public abstract class AstRequire extends AstDeclaration {

    public AstRequire(@NotNull InputPosition inputPosition) {
        super(inputPosition);
    }

    public abstract boolean isLibrary();

    public abstract String getName();
}
