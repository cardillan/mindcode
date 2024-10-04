package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.ProcessorEdition;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.Consumer;

public interface FunctionMapper {

    LogicValue handleFunction(Token token, Consumer<LogicInstruction> program, String functionName, List<LogicValue> arguments);

    LogicValue handleProperty(Token token, Consumer<LogicInstruction> program, String propertyName, LogicValue target, List<LogicValue> arguments);

    List<FunctionSample> generateSamples();

    record FunctionSample(int order, String name, String functionCall, LogicInstruction instruction,
                          ProcessorEdition edition, String note) {
    }
}
