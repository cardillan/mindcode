package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.ProcessorEdition;

import java.util.List;

public interface FunctionMapper {

    LogicValue handleFunction(LogicInstructionPipeline pipeline, String functionName, List<LogicValue> params);

    LogicValue handleProperty(LogicInstructionPipeline pipeline, String propertyName, LogicValue target, List<LogicValue> params);

    List<FunctionSample> generateSamples();

    record FunctionSample(int order, String name, String functionCall, LogicInstruction instruction,
                          ProcessorEdition edition, String note) {
    }
}
