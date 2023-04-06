package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import java.util.List;

public interface FunctionMapper {

    String handleFunction(LogicInstructionPipeline pipeline, String functionName, List<String> params);

    String handleProperty(LogicInstructionPipeline pipeline, String propertyName, String target, List<String> params);

    List<FunctionSample> generateSamples();

    class FunctionSample {
        public final int order;
        public final String name;
        public final String functionCall;
        public final LogicInstruction instruction;
        public final ProcessorEdition edition;
        public final String note;

        public FunctionSample(int order, String name, String functionCall, LogicInstruction instruction,
                ProcessorEdition edition, String note) {
            this.order = order;
            this.name = name;
            this.functionCall = functionCall;
            this.instruction = instruction;
            this.edition = edition;
            this.note = note;
        }
    }
}
