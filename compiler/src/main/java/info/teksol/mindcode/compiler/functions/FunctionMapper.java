package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.generator.MessageEmitter;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.ProcessorEdition;

import java.util.List;
import java.util.function.Consumer;

public interface FunctionMapper extends MessageEmitter {

    LogicValue handleFunction(AstNode astNode, Consumer<LogicInstruction> program, String functionName, List<LogicValue> arguments);

    LogicValue handleProperty(AstNode astNode, Consumer<LogicInstruction> program, String propertyName, LogicValue target, List<LogicValue> arguments);

    String decompile(MlogInstruction instruction);

    List<FunctionSample> generateSamples();

    record FunctionSample(int order, String name, String functionCall, LogicInstruction instruction,
                          ProcessorEdition edition, String note) {
    }
}
