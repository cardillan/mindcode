package io.github.cardillan.mc.mindcode.compiler.generation.variables;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record MlogSpecification(List<LogicArgument> mlogNames) {
}
