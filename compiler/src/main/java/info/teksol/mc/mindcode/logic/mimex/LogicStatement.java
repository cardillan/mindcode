package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record LogicStatement(
        String opcode,
        String arguments,
        String name,
        String typeName,
        boolean hidden,
        boolean privileged,
        boolean nonPrivileged,
        String hint,
        String categoryName,
        double categoryColor,
        String categoryDescription
) {
}
