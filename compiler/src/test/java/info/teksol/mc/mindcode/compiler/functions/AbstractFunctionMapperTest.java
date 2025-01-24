package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.CodeAssemblerContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class AbstractFunctionMapperTest {

    protected static void createFunctionMapper(List<OpcodeVariant> opcodeVariants) {
        InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V6,
                ProcessorEdition.STANDARD_PROCESSOR, opcodeVariants);

        createFunctionMapper(instructionProcessor);
    }

    protected static BaseFunctionMapper createFunctionMapper(InstructionProcessor instructionProcessor) {
        CodeAssemblerContext codeAssemblerContext = new CodeAssemblerContextImpl(
                s -> {},
                CompilerProfile.fullOptimizations(false),
                instructionProcessor,
                AstContext.createStaticRootNode());

        FunctionMapperContext functionMapperContext = new FunctionMapperContextImpl(
                s -> {},
                instructionProcessor,
                new CodeAssembler(codeAssemblerContext));

        return new BaseFunctionMapper(functionMapperContext);
    }

    protected static class FunctionMapperContextImpl extends AbstractMessageEmitter implements FunctionMapperContext {
        private final InstructionProcessor instructionProcessor;
        private final CodeAssembler assembler;

        public FunctionMapperContextImpl(MessageConsumer messageConsumer, InstructionProcessor instructionProcessor,
                CodeAssembler assembler) {
            super(messageConsumer);
            this.instructionProcessor = instructionProcessor;
            this.assembler = assembler;
        }

        @Override
        public InstructionProcessor instructionProcessor() {
            return instructionProcessor;
        }

        @Override
        public CodeAssembler assembler() {
            return assembler;
        }
    }

    protected static class CodeAssemblerContextImpl extends AbstractMessageEmitter implements CodeAssemblerContext {
        private final CompilerProfile compilerProfile;
        private final InstructionProcessor instructionProcessor;
        private final AstContext rootAstContext;

        public CodeAssemblerContextImpl(MessageConsumer messageConsumer, CompilerProfile compilerProfile,
                InstructionProcessor instructionProcessor, AstContext rootAstContext) {
            super(messageConsumer);
            this.compilerProfile = compilerProfile;
            this.instructionProcessor = instructionProcessor;
            this.rootAstContext = rootAstContext;
        }

        @Override
        public CompilerProfile compilerProfile() {
            return compilerProfile;
        }

        @Override
        public InstructionProcessor instructionProcessor() {
            return instructionProcessor;
        }

        @Override
        public AstContext rootAstContext() {
            return rootAstContext;
        }
    }
}
