package info.teksol.mindcode;

import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

/// Common ancestor for AstNode and AstMindcodeNode
// FINISH Remove after retiring old compiler, replace by AstMindcodeNode
public interface AstCommonNode extends AstElement {

    AstContextType getContextType();

    AstSubcontextType getSubcontextType();
}
