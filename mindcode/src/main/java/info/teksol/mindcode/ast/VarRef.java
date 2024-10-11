package info.teksol.mindcode.ast;


import info.teksol.mindcode.InputFile;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class VarRef extends BaseAstNode {
    private final String name;

    public VarRef(Token startToken, InputFile inputFile, String name) {
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
        VarRef varRef = (VarRef) o;
        return Objects.equals(name, varRef.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "VarRef{" +
                "name='" + name + '\'' +
                '}';
    }
}
