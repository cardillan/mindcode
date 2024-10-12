package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class DirectiveText extends BaseAstNode {
    private final String text;

    public DirectiveText(InputPosition inputPosition, String text) {
        super(inputPosition);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectiveText that = (DirectiveText) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "DirectiveText{" +
                "text='" + text +
                "'}";
    }
}
