package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import java.util.function.Consumer;

public class FunctionMapperFactory {

    public static FunctionMapper getFunctionMapper(InstructionProcessor instructionProcessor, Consumer<String> messageConsumer) {
        return new BaseFunctionMapper(instructionProcessor, messageConsumer);
    }

    private FunctionMapperFactory() { }
}
