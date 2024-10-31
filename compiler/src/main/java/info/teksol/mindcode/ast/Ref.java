package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class Ref extends BaseAstNode {
    protected final String name;
    protected final boolean strict;

    Ref(InputPosition inputPosition, String name, boolean strict) {
        super(inputPosition);
        this.name = name;
        this.strict = strict;
        if (name.charAt(0) != '@') {
            throw new IllegalArgumentException("Missing '@' prefix");
        } else if (name.startsWith("@@")) {
            throw new IllegalArgumentException("Superfluous '@' prefix");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ref ref = (Ref) o;
        return Objects.equals(name, ref.name);
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Ref{" +
                "name='" + name + '\'' +
                '}';
    }
}
