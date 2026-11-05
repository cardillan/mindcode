package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.compiler.CompilerContext;
import io.github.cardillan.mindcode.compiler.generation.StackTracker;

public interface StackContext extends CompilerContext {
    StackTracker stackTracker();
}
