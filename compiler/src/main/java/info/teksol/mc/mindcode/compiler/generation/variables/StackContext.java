package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.CompilerContext;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;

public interface StackContext extends CompilerContext {
    StackTracker stackTracker();
}
