package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.mindcode.compiler.MessageContext;
import info.teksol.mc.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PreprocessorContext extends MessageContext {
    DirectiveProcessor directiveProcessor();
}
