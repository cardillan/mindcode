package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import java.util.function.Consumer;

public class FunctionMapperFactory {

    public static FunctionMapper getFunctionMapper(InstructionProcessor instructionProcessor, Consumer<CompilerMessage> messageConsumer) {
        return new BaseFunctionMapper(instructionProcessor, messageConsumer);
    }

    private FunctionMapperFactory() { }
}
