package info.teksol.mc.mindcode.compiler.declarations;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DeclarationsProcessorContext extends MessageContext {
    Variables variables();
}
