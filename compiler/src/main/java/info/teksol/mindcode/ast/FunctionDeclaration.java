package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.IntRange;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration extends BaseAstNode {
    private final boolean inline;
    private final boolean noinline;
    private final boolean procedure;
    private final String name;

    private final List<FunctionParameter> params;
    private final AstNode body;

    public FunctionDeclaration(InputPosition inputPosition, boolean inline, boolean noinline, boolean procedure,
            String name, List<FunctionParameter> params, AstNode body) {
        super(inputPosition, body);
        if (inline && noinline) {
            throw new IllegalArgumentException("Both inline and noinline specified.");
        }
        this.inline = inline;
        this.noinline = noinline;
        this.procedure = procedure;
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

    public boolean isProcedure() {
        return procedure;
    }

    public boolean isVarArg() {
        return !params.isEmpty() && params.get(params.size() - 1).isVarArgs();
    }

    public IntRange getParameterCount() {
        if (params.isEmpty()) {
            return new IntRange(0, 0);
        } else if (isVarArg()) {
            int min = params.size() == 1 ? 0 :
                    CollectionUtils.findLastIndex(params.subList(0, params.size() - 1), FunctionParameter::isCompulsory) + 1;
            return new IntRange(min, Integer.MAX_VALUE);
        } else {
            int min = CollectionUtils.findLastIndex(params, FunctionParameter::isCompulsory) + 1;
            return new IntRange(min, params.size());
        }
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
        return inline == that.inline && noinline == that.noinline
                && name.equals(that.name) && params.equals(that.params) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(inline);
        result = 31 * result + Boolean.hashCode(noinline);
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
                ", procedure=" + procedure +
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
