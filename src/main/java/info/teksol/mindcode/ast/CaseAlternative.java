package info.teksol.mindcode.ast;

import java.util.Objects;

public class CaseAlternative implements AstNode {
    private final AstNode value;
    private final AstNode body;

    CaseAlternative(AstNode value, AstNode body) {
        this.value = value;
        this.body = body;
    }

    public AstNode getValue() {
        return value;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseAlternative that = (CaseAlternative) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, body);
    }

    @Override
    public String toString() {
        return "CaseAlternative{" +
                "value=" + value +
                ", body=" + body +
                '}';
    }
}
