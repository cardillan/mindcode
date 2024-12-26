package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.logic.NamedParameter;
import org.jspecify.annotations.NullMarked;

@NullMarked
interface SelectorFunction extends FunctionHandler {
    NamedParameter getSelector();
}
