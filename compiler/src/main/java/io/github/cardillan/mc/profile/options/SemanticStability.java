package io.github.cardillan.mc.profile.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SemanticStability {
    /// The compiler option doesn't alter the behavior of the program
    STABLE,

    /// The compiler option does, at least potentially, alter the behavior of the program
    UNSTABLE,
}
