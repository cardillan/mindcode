package io.github.cardillan.mc.mindcode.compiler.declarations;

import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DeclarationsProcessorContext extends MessageContext {
    Variables variables();
}
