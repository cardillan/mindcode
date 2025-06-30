package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PreprocessorContext extends CompilerContext {
    DirectiveProcessor directiveProcessor();
    CompilerProfile compilerProfile();
}
