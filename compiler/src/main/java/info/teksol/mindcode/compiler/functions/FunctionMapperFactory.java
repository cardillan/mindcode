package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.MessageConsumer;

import java.util.function.Supplier;

public class FunctionMapperFactory {

    public static FunctionMapper getFunctionMapper(InstructionProcessor instructionProcessor, Supplier<AstContext> astContextSupplier,
            MessageConsumer messageConsumer) {
        return new BaseFunctionMapper(instructionProcessor, astContextSupplier, messageConsumer);
    }

    private FunctionMapperFactory() { }
}
