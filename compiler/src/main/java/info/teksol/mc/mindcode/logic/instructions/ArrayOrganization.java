package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.arrays.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
public enum ArrayOrganization {
    NONE                ("none",        null),
    INTERNAL_REGULAR    ("regular",     RegularArrayConstructor::new),
    INTERNAL_SIZE1      ("short:1",     ArraySize1Constructor::new),
    INTERNAL_SIZE2      ("short:2",     ArraySize2Constructor::new),
    INTERNAL_SIZE3      ("short:3",     ArraySize3Constructor::new),
    EXTERNAL_ARRAY      ("external",    ExternalArrayConstructor::new),
    ;

    private final String name;
    private final @Nullable Function<ArrayAccessInstruction, ArrayConstructor> expander;

    ArrayOrganization(String name, @Nullable Function<ArrayAccessInstruction, ArrayConstructor> expander) {
        this.name = name;
        this.expander = expander;
    }

    public String getName() {
        return name;
    }

    public ArrayConstructor getExpander(ArrayAccessInstruction instruction) {
        assert expander != null;
        return expander.apply(instruction);
    }
}
