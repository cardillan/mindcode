package info.teksol.mindcode.logic;

import java.util.Objects;

public record NamedParameter(LogicParameter type, String name) {
    public NamedParameter {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
    }
}
