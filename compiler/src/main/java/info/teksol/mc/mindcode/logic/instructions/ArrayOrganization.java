package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.arrays.*;

import java.util.function.Function;

public enum ArrayOrganization {
    NONE                ("none",        _ -> null),
    INTERNAL_REGULAR    ("regular",     RegularArrayConstructor::new),
    INTERNAL_SIZE1      ("short:1",     ArraySize1Constructor::new),
    INTERNAL_SIZE2      ("short:2",     ArraySize2Constructor::new),
    INTERNAL_SIZE3      ("short:3",     ArraySize3Constructor::new),
    EXTERNAL_ARRAY      ("external",    ExternalArrayConstructor::new),
    ;

    private final String name;
    private final Function<ArrayAccessInstruction, ArrayConstructor> expander;

    ArrayOrganization(String name, Function<ArrayAccessInstruction, ArrayConstructor> expander) {
        this.name = name;
        this.expander = expander;
    }

    public String getName() {
        return name;
    }

    public ArrayConstructor getExpander(ArrayAccessInstruction instruction) {
        return expander.apply(instruction);
    }
}
