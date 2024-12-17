package info.teksol.mindcode.v3.compiler.directives;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.CompilerContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DirectiveProcessorContext extends CompilerContext {
    CompilerProfile compilerProfile();
}
