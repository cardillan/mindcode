package io.github.cardillan.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstNegatable<T extends AstExpression & AstNegatable<T>> {
    T negate();

    boolean isNegated();
}
