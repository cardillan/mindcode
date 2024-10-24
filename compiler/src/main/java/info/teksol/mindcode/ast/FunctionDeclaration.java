package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration extends BaseAstNode {
    private final boolean inline;
    private final boolean noinline;
    private final boolean varArgs;
    private final String name;

    private final List<FunctionParameter> params;
    private final AstNode body;

    public FunctionDeclaration(InputPosition inputPosition, boolean inline, boolean noinline, boolean varArgs,
            String name, List<FunctionParameter> params, AstNode body) {
        super(inputPosition, body);
        if (inline && noinline) {
            throw new IllegalArgumentException("Both inline and noinline specified.");
        }
        this.inline = inline;
        this.noinline = noinline;
        this.varArgs = varArgs;
        this.name = Objects.requireNonNull(name);
        this.params = Objects.requireNonNull(params);
        this.body = Objects.requireNonNull(body);
    }

    public boolean isInline() {
        return inline;
    }

    public boolean isNoinline() {
        return noinline;
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getParams() {
        return params;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionDeclaration that = (FunctionDeclaration) o;
        return inline == that.inline && noinline == that.noinline && varArgs == that.varArgs
                && name.equals(that.name) && params.equals(that.params) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(inline);
        result = 31 * result + Boolean.hashCode(noinline);
        result = 31 * result + Boolean.hashCode(varArgs);
        result = 31 * result + name.hashCode();
        result = 31 * result + params.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FunctionDeclaration{" +
                "inline=" + inline +
                ", noinline=" + noinline +
                ", varArgs=" + varArgs +
                ", name='" + name + '\'' +
                ", params=" + params +
                ", body=" + body +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.FUNCTION;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BODY;
    }
}
