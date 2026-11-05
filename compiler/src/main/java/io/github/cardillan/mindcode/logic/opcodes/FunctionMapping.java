package io.github.cardillan.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum FunctionMapping {
    NONE,       // No mapping
    FUNC,       // Mapping to function
    PROP,       // Mapping to property access - block.method(arguments)
    BOTH,       // Mapping to both a function and a property access
}
