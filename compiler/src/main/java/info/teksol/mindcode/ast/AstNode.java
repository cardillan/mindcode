package info.teksol.mindcode.ast;

import info.teksol.mindcode.AstCommonNode;

import java.util.List;

public interface AstNode extends AstCommonNode {
    List<AstNode> getChildren();
}
