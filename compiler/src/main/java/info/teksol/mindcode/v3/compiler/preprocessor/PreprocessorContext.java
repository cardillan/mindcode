package info.teksol.mindcode.v3.compiler.preprocessor;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.CompilerContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PreprocessorContext extends CompilerContext {
    CompilerProfile compilerProfile();
}
