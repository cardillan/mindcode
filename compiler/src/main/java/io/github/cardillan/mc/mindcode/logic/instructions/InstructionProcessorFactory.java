package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.messages.MessageConsumer;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mc.mindcode.logic.instructions.BaseInstructionProcessor.InstructionProcessorParameters;
import io.github.cardillan.mc.mindcode.logic.opcodes.OpcodeVariant;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorType;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorVersion;
import io.github.cardillan.mc.profile.CompilerProfile;
import io.github.cardillan.mc.util.EscapeClass;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class InstructionProcessorFactory {
    private static final MessageConsumer nullMessageConsumer = msg -> {};

    public static InstructionProcessor getInstructionProcessor(MessageConsumer messageConsumer, NameCreator nameCreator, CompilerProfile profile) {
        return create(new InstructionProcessorParameters(messageConsumer, profile.getProcessorVersion(),
                profile.getProcessorType(), nameCreator, true, profile.isNoArgumentPadding(), profile.isEncodeZeroCharacters(),
                profile.getUnicodeEscapes()));
    }

    public static InstructionProcessor getInstructionProcessorNoValidate(MessageConsumer messageConsumer, NameCreator nameCreator, CompilerProfile profile) {
        return create(new InstructionProcessorParameters(messageConsumer, profile.getProcessorVersion(),
                profile.getProcessorType(), nameCreator, false, profile.isNoArgumentPadding(), profile.isEncodeZeroCharacters(),
                profile.getUnicodeEscapes()));
    }

    public static InstructionProcessor getInstructionProcessor(MessageConsumer messageConsumer,
            ProcessorVersion version, ProcessorType type, NameCreator nameCreator) {
        return create(new InstructionProcessorParameters(messageConsumer, version, type, nameCreator,
                false, false, false, EscapeClass.NON_PRINTABLE));
    }

    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorType type, NameCreator nameCreator) {
        return create(new InstructionProcessorParameters(nullMessageConsumer, version, type, nameCreator,
                true, false, false, EscapeClass.NON_PRINTABLE));
    }

    // To be used by unit tests - returns new, non-cached instances based off whatever processor is given
    public static InstructionProcessor getInstructionProcessor(ProcessorVersion version, ProcessorType type,
            NameCreator nameCreator, List<OpcodeVariant> opcodeVariants) {
        return create(new InstructionProcessorParameters(nullMessageConsumer, version, type, nameCreator,
                true, false, false, EscapeClass.NON_PRINTABLE, opcodeVariants));
    }

    private static InstructionProcessor create(InstructionProcessorParameters parameters) {
        return switch (parameters.version()) {
            case V6, V7, V7A -> new MindustryInstructionProcessor7(parameters);
            case V8A, V8B -> new MindustryInstructionProcessor8(parameters);
            case V8C, MAX -> new MindustryInstructionProcessor82(parameters);
        };
    }

    private InstructionProcessorFactory() { }
}
