package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;

public interface FunctionMapper {
    String handleFunction(LogicInstructionPipeline pipeline, String functionName, List<String> params);
}