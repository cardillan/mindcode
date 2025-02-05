package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
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
