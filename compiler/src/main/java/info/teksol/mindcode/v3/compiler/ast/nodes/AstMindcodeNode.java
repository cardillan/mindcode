package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

public interface AstMindcodeNode extends AstElement {

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
