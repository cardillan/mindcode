package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.emulator.MindustryString;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralString;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

import java.util.Objects;

public class LogicString extends AbstractArgument implements LogicLiteral {

    public static final LogicString NEW_LINE = create("\\n");

    private final String stringValue;
    private final String literal;

    private LogicString(String stringValue) {
        super(ArgumentType.STRING_LITERAL);
        this.stringValue = Objects.requireNonNull(stringValue);
        this.literal = "\"" + stringValue + "\"";
    }

    @Override
    public String format(InstructionProcessor instructionProcessor) {
        return stringValue;
    }

    @Override
    public String toMlog() {
        return literal;
    }

    @Override
    public String toString() {
        return "LogicString{" +
                "stringValue='" + stringValue + '\'' +
                '}';
    }

    public static LogicString create(String value) {
        return new LogicString(value);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public boolean isObject() {
        return true;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public double getDoubleValue() {
        return 1.0;
    }

    @Override
    public long getLongValue() {
        return 1;
    }

    @Override
    public Object getObject() {
        return new MindustryString(stringValue);
    }

    @Override
    public AstMindcodeNode asAstNode(InputPosition position) {
        return new AstLiteralString(position, stringValue);
    }
}

