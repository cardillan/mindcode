package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.arrays.*;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@NullMarked
public enum ArrayOrganization {
    NONE        ("none"),
    INTERNAL    ("internal", CompactArrayConstructor::new, RegularArrayConstructor::new),
    INLINED     ("inlined", CompactInlinedArrayConstructor::new, RegularInlinedArrayConstructor::new),
    SINGLE      ("single", ArraySingleConstructor::new),
    SHORT       ("short", CompactShortArrayConstructor::new, RegularShortArrayConstructor::new),
    LOOKUP      ("lookup", LookupArrayConstructor::new),
    EXTERNAL    ("external",ExternalArrayConstructor::new),
    ;

    private final String name;
    private final Map<ArrayConstruction, Function<ArrayAccessInstruction, ArrayConstructor>> constructors;

    ArrayOrganization(String name) {
        this.name = name;
        constructors = Map.of();
    }

    ArrayOrganization(String name, Function<ArrayAccessInstruction, ArrayConstructor> constructor) {
        this(name, constructor, constructor);
    }

    ArrayOrganization(String name, Function<ArrayAccessInstruction, ArrayConstructor> compactConstructor,
            Function<ArrayAccessInstruction, ArrayConstructor> regularConstructor) {
        this.name = name;
        constructors = Map.of(
                ArrayConstruction.COMPACT, compactConstructor,
                ArrayConstruction.REGULAR, regularConstructor
        );
    }

    public String getName() {
        return name;
    }

    public ArrayConstructor getConstructor(ArrayAccessInstruction instruction) {
        Function<ArrayAccessInstruction, ArrayConstructor> constructor = Objects.requireNonNull(constructors.get(instruction.getArrayConstruction()));
        return constructor.apply(instruction);
    }

    public boolean canInline() {
        return this == INTERNAL;
    }

    public boolean supportsLookup() {
        return switch(this) {
            case INTERNAL, INLINED, SINGLE, SHORT, LOOKUP -> true;
            default -> false;
        };
    }

    public boolean isInlined() {
        return switch(this) {
            case INLINED, SINGLE, SHORT, LOOKUP -> true;
            default -> false;
        };
    }
}
