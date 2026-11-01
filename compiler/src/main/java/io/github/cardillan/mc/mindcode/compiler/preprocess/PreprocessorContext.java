package io.github.cardillan.mc.mindcode.compiler.preprocess;

import io.github.cardillan.mc.mindcode.compiler.MessageContext;
import io.github.cardillan.mc.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PreprocessorContext extends MessageContext {
    DirectiveProcessor directiveProcessor();
}
