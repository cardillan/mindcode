package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.List;
import java.util.Objects;

public class FunctionCall extends ControlBlockAstNode {
    private final String functionName;
    private final List<FunctionArgument> arguments;

    FunctionCall(InputPosition inputPosition, String functionName, FunctionArgument... arguments) {
        this(inputPosition, functionName, List.of(arguments));
    }

    FunctionCall(InputPosition inputPosition, String functionName, List<FunctionArgument> arguments) {
        super(inputPosition, arguments);
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionCall functionCall = (FunctionCall) o;
        return Objects.equals(functionName, functionCall.functionName) &&
                Objects.equals(arguments, functionCall.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, arguments);
    }

    @Override
    public String toString() {
        return "FunctionCall{" +
                "functionName='" + functionName + '\'' +
                ", arguments=" + arguments +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CALL;
    }
}
