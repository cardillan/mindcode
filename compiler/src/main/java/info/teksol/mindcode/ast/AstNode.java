package info.teksol.mindcode.ast;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

import java.util.List;

public interface AstNode extends AstElement {
    List<AstNode> getChildren();

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
