package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralNull;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

public enum LogicNull implements LogicLiteral {
    NULL;

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return "null";
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
        return "null";
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }

    @Override
    public long getLongValue() {
        return 0;
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public AstMindcodeNode asAstNode(InputPosition position) {
        return new AstLiteralNull(position, "null");
    }
}
