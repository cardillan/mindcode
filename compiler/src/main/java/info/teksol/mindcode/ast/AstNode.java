package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

import java.util.List;

public interface AstNode {
    List<AstNode> getChildren();

    InputPosition getInputPosition();

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
