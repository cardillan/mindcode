package info.teksol.mindcode.ast;

import java.util.Objects;

public class StringLiteral extends BaseAstNode {
    private final String text;

    StringLiteral(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String encode() {
        return "\"" + getText().replaceAll("\"", "'") + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteral that = (StringLiteral) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "StringLiteral{" +
                "text='" + text + '\'' +
                '}';
    }
}
