package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import java.util.function.Supplier;

public interface FunctionMapper {
    String handleFunction(LogicInstructionPipeline pipeline, String functionName, List<String> params,
            Supplier<String> tmpGenerator);
}
