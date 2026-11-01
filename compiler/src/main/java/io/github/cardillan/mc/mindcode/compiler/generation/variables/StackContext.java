package io.github.cardillan.mc.mindcode.compiler.generation.variables;

import io.github.cardillan.mc.mindcode.compiler.CompilerContext;
import io.github.cardillan.mc.mindcode.compiler.generation.StackTracker;

public interface StackContext extends CompilerContext {
    StackTracker stackTracker();
}
