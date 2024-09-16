package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FunctionMapperFactory {

    public static FunctionMapper getFunctionMapper(InstructionProcessor instructionProcessor, Supplier<AstContext> astContextSupplier,
            Consumer<CompilerMessage> messageConsumer) {
        return new BaseFunctionMapper( instructionProcessor, astContextSupplier, messageConsumer);
    }

    private FunctionMapperFactory() { }
}
