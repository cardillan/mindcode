package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstVariableModifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ModifierParametrization<E>(AstVariableModifier node, @Nullable E parametrization) {
}
