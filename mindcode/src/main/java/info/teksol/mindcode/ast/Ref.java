package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class Ref extends BaseAstNode {
    protected final String name;

    Ref(Token startToken, InputFile inputFile, String name) {
        super(startToken, inputFile);
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
