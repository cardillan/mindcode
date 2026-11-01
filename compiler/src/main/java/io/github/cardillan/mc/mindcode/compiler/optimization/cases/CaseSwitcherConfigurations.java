package io.github.cardillan.mc.mindcode.compiler.optimization.cases;

import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CaseSwitcherConfigurations(SourcePosition sourcePosition, int configurationsCount) {
}
