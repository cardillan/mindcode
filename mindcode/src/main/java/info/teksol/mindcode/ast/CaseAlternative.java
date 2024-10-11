package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Objects;

public class CaseAlternative extends ControlBlockAstNode {
    private final List<AstNode> values;
    private final AstNode body;

    CaseAlternative(Token startToken, InputFile inputFile, AstNode value, AstNode body) {
        super(startToken, inputFile, value, body);
        this.values = List.of(value);
        this.body = body;
    }

    CaseAlternative(Token startToken, InputFile inputFile, List<AstNode> values, AstNode body) {
        super(startToken, inputFile, values, body);
        this.values = values;
        this.body = body;
    }

    public List<AstNode> getValues() {
        return values;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseAlternative that = (CaseAlternative) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, body);
    }

    @Override
    public String toString() {
        return "CaseAlternative{" +
                "values=" + values +
                ", body=" + body +
                '}';
    }
}
