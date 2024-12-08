package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AstBaseMindcodeNode implements AstMindcodeNode {
    private final @NotNull InputPosition inputPosition;

    protected AstBaseMindcodeNode(@NotNull InputPosition inputPosition) {
        this.inputPosition = inputPosition;
    }

    @Override
    public @NotNull InputPosition inputPosition() {
        return inputPosition;
    }

    public @NotNull List<@NotNull AstNode> getChildren() {
        return List.of();
    }

    @Override
    public @NotNull AstContextType getContextType() {
        return AstContextType.NONE;
    }

    @Override
    public @NotNull AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BASIC;
    }
}
