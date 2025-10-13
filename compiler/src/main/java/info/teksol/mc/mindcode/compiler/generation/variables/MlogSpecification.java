package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record MlogSpecification(List<LogicArgument> mlogNames) {
}
