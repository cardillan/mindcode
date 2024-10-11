package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class DoWhileExpression extends ControlBlockAstNode {
    private final String label;
    private final AstNode body;
    private final AstNode condition;

    DoWhileExpression(Token startToken, InputFile inputFile, String label, AstNode body, AstNode condition) {
        super(startToken, inputFile, body, condition);
        this.label = label;
        this.body = body;
        this.condition = condition;
    }

    public String getLabel() {
        return label;
    }

    public AstNode getCondition() {
        return condition;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoWhileExpression that = (DoWhileExpression) o;
        return Objects.equals(label, that.label) && Objects.equals(body, that.body) &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }

    @Override
    public String toString() {
        return "DoWhileExpression{" +
                "label=" + label +
                ", body=" + body +
                ", condition=" + condition +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.LOOP;
    }
}
