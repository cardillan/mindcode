package info.teksol.mindcode.ast;

import java.util.List;
import java.util.Objects;

public class Printf extends BaseAstNode {
    private final StringLiteral format;
    private final List<AstNode> params;

    public Printf(StringLiteral format, List<AstNode> params) {
        this.format = format;
        this.params = params;
    }

    public StringLiteral getFormat() {
        return format;
    }

    public List<AstNode> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Printf functionCall = (Printf) o;
        return Objects.equals(format, functionCall.format) &&
                Objects.equals(params, functionCall.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, params);
    }

    @Override
    public String toString() {
        return "FunctionCall{" +
                "format=" + format +
                ", params=" + params +
                '}';
    }
}
