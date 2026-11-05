package io.github.cardillan.mindcode.compiler.optimization.cases;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CaseSwitcherConfigurations(SourcePosition sourcePosition, int configurationsCount) {
}
