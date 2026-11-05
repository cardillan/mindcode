package io.github.cardillan.mindcode.compiler.preprocess;

import io.github.cardillan.mindcode.compiler.MessageContext;
import io.github.cardillan.mindcode.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PreprocessorContext extends MessageContext {
    DirectiveProcessor directiveProcessor();
}
