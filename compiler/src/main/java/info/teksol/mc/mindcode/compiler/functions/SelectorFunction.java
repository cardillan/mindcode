package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import org.jspecify.annotations.NullMarked;

@NullMarked
interface SelectorFunction extends FunctionHandler {
    NamedParameter getSelector();
}
