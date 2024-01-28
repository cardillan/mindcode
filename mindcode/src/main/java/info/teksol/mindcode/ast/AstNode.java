package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.antlr.v4.runtime.Token;

import java.util.List;

public interface AstNode {
    List<AstNode> getChildren();

    Token startToken();

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
