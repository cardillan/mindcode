package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.logic.opcodes.NamedParameter;
import org.jspecify.annotations.NullMarked;

@NullMarked
interface SelectorFunction extends FunctionHandler {
    NamedParameter getSelector();
}
