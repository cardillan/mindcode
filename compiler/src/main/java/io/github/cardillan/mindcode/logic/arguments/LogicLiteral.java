package io.github.cardillan.mindcode.logic.arguments;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstMindcodeNode;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LogicLiteral extends LogicValue {

    @Override
    default boolean isLvalue() {
        return false;
    }

    @Override
    default LogicLiteral withSourcePosition(SourcePosition sourcePosition) {
        return this;
    }

    AstMindcodeNode asAstNode(SourcePosition position);
}
