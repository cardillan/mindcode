package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructor;
import info.teksol.mc.mindcode.logic.arguments.arrays.ExternalArrayConstructor;
import info.teksol.mc.mindcode.logic.arguments.arrays.RegularArrayConstructor;

import java.util.function.Function;

public enum ArrayOrganization {
    NONE                (_ -> null),
    REGULAR_INTERNAL    (RegularArrayConstructor::new),
    EXTERNAL_ARRAY      (ExternalArrayConstructor::new),
    ;

    private final Function<ArrayAccessInstruction, ArrayConstructor> expander;

    ArrayOrganization(Function<ArrayAccessInstruction, ArrayConstructor> expander) {
        this.expander = expander;
    }

    public ArrayConstructor getExpander(ArrayAccessInstruction instruction) {
        return expander.apply(instruction);
    }
}
