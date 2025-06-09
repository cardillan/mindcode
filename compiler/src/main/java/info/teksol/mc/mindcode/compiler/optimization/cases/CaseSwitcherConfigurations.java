package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CaseSwitcherConfigurations(SourcePosition sourcePosition, int configurationsCount) {
}
