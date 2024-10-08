package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.antlr.v4.runtime.Token;

import java.util.List;

public interface AstNode {
    List<AstNode> getChildren();

    Token startToken();

    SourceFile sourceFile();

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
