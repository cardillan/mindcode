package io.github.cardillan.mc.mindcode.logic.arguments;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LogicLiteral extends LogicValue {

    @Override
    default boolean isLvalue() {
        return false;
    }

    default boolean isNull() {
        return this == LogicNull.NULL;
    }

    @Override
    default LogicLiteral withSourcePosition(SourcePosition sourcePosition) {
        return this;
    }

    AstMindcodeNode asAstNode(SourcePosition position);
}
