package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record LogicStatement(
        String opcode,
        List<String> arguments,
        List<String> argumentTypes,
        List<String> argumentNames,
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
