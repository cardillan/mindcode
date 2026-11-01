package io.github.cardillan.mc.mindcode.compiler.functions;

import io.github.cardillan.mc.mindcode.logic.opcodes.NamedParameter;
import org.jspecify.annotations.NullMarked;

@NullMarked
interface SelectorFunction extends FunctionHandler {
    NamedParameter getSelector();
}
