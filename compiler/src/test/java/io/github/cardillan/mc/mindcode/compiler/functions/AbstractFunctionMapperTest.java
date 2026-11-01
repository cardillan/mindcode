package io.github.cardillan.mc.mindcode.compiler.functions;

import io.github.cardillan.mc.messages.AbstractMessageEmitter;
import io.github.cardillan.mc.messages.MessageConsumer;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeAssembler;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeAssemblerContext;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.StandardNameCreator;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.Variables;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.VariablesContext;
import io.github.cardillan.mc.mindcode.logic.instructions.InstructionProcessor;
import io.github.cardillan.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import io.github.cardillan.mc.mindcode.logic.mimex.MindustryMetadata;
import io.github.cardillan.mc.mindcode.logic.opcodes.OpcodeVariant;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorType;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorVersion;
import io.github.cardillan.mc.profile.CompilerProfile;
import io.github.cardillan.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class AbstractFunctionMapperTest {

    protected static void createFunctionMapper(List<OpcodeVariant> opcodeVariants) {
        InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V6,
                ProcessorType.MICRO_PROCESSOR, new StandardNameCreator((false)), opcodeVariants);

        createFunctionMapper(instructionProcessor);
    }

    protected static BaseFunctionMapper createFunctionMapper(InstructionProcessor instructionProcessor) {
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false, false);

        CodeAssemblerContext codeAssemblerContext = new CodeAssemblerContextImpl(
                s -> {},
                compilerProfile,
                instructionProcessor,
                AstContext.createStaticRootNode());

        FunctionMapperContext functionMapperContext = new FunctionMapperContextImpl(
                s -> {},
                compilerProfile,
                instructionProcessor,
                new CodeAssembler(codeAssemblerContext));

        return new BaseFunctionMapper(functionMapperContext);
    }

    protected static class VariablesContextImpl extends AbstractMessageEmitter implements VariablesContext {
        private final CompilerProfile compilerProfile;
        private final InstructionProcessor instructionProcessor;
        private final NameCreator nameCreator;

        public VariablesContextImpl(MessageConsumer messageConsumer, CompilerProfile compilerProfile,
                InstructionProcessor instructionProcessor) {
            super(messageConsumer);
            this.compilerProfile = compilerProfile;
            this.instructionProcessor = instructionProcessor;
            this.nameCreator = new StandardNameCreator(false);
        }

        @Override
        public CompilerProfile globalCompilerProfile() {
            return compilerProfile;
        }

        @Override
        public InstructionProcessor instructionProcessor() {
            return instructionProcessor;
        }

        @Override
        public NameCreator nameCreator() {
            return nameCreator;
        }

        @Override
        public MindustryMetadata metadata() {
            return instructionProcessor.getMetadata();
        }

        @Override
        public @Nullable Map<String, String> schematicLinks() {
            return null;
        }
    }


    protected static class CodeAssemblerContextImpl extends VariablesContextImpl implements CodeAssemblerContext {
        private final AstContext rootAstContext;
        private final CompilerProfile compilerProfile;
        private final Variables variables;

        public CodeAssemblerContextImpl(MessageConsumer messageConsumer, CompilerProfile compilerProfile,
                InstructionProcessor instructionProcessor, AstContext rootAstContext) {
            super(messageConsumer, compilerProfile, instructionProcessor);
            this.rootAstContext = rootAstContext;
            this.compilerProfile = compilerProfile;
            this.variables = new Variables(this);
        }

        @Override
        public CompilerProfile globalCompilerProfile() {
            return compilerProfile;
        }

        @Override
        public AstContext rootAstContext() {
            return rootAstContext;
        }

        @Override
        public Variables variables() {
            return variables;
        }
    }

    protected static class FunctionMapperContextImpl extends AbstractMessageEmitter implements FunctionMapperContext {
        private final GlobalCompilerProfile globalCompilerProfile;
        private final InstructionProcessor instructionProcessor;
        private final CodeAssembler assembler;

        @Override
        public GlobalCompilerProfile globalCompilerProfile() {
            return globalCompilerProfile;
        }

        public FunctionMapperContextImpl(MessageConsumer messageConsumer, GlobalCompilerProfile globalCompilerProfile,
                InstructionProcessor instructionProcessor, CodeAssembler assembler) {
            super(messageConsumer);
            this.globalCompilerProfile = globalCompilerProfile;
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
}
