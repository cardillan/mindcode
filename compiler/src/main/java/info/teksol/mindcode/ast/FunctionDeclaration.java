package info.teksol.mindcode.ast;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration extends BaseAstNode {
    private final String name;
    private final List<AstNode> params;
    private final AstNode body;

    FunctionDeclaration(String name, List<AstNode> params, AstNode body) {
        super(params, body);
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<AstNode> getParams() {
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
        return Objects.equals(name, that.name) &&
                Objects.equals(params, that.params) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, params, body);
    }

    @Override
    public String toString() {
        return "FunctionDeclaration{" +
                "name='" + name + '\'' +
                ", params=" + params +
                ", body=" + body +
                '}';
    }
}
