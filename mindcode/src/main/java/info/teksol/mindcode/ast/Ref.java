package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class Ref extends BaseAstNode {
    protected final String name;

    Ref(Token startToken, SourceFile sourceFile, String name) {
        super(startToken, sourceFile);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ref ref = (Ref) o;
        return Objects.equals(name, ref.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Ref{" +
                "name='" + name + '\'' +
                '}';
    }
}
