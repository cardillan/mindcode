package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

import java.util.List;

public abstract class AstBaseMindcodeNode implements AstMindcodeNode {
    private final InputPosition inputPosition;

    protected AstBaseMindcodeNode(InputPosition inputPosition) {
        this.inputPosition = inputPosition;
    }

    public InputPosition inputPosition() {
        return inputPosition;
    }

    public List<AstNode> getChildren() {
        return List.of();
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.NONE;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BASIC;
    }
}
