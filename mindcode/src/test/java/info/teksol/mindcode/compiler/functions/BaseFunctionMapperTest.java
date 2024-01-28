package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.FunctionMapping.FUNC;
import static info.teksol.mindcode.logic.MindustryOpcodeVariants.*;
import static info.teksol.mindcode.logic.ProcessorEdition.S;
import static info.teksol.mindcode.logic.ProcessorVersion.V6;
import static info.teksol.mindcode.logic.ProcessorVersion.V7;
import static org.junit.jupiter.api.Assertions.assertThrows;

// This class tests the initialization logic of the BaseFunctionMapper
// Actual mapping of functions to correct instructions is covered by other tests
public class BaseFunctionMapperTest {

    private static BaseFunctionMapper createFunctionMapper(List<OpcodeVariant> opcodeVariants) {
        InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V6,
                ProcessorEdition.STANDARD_PROCESSOR, opcodeVariants);

        AstContext staticAstContext = AstContext.createRootNode();
        return new BaseFunctionMapper(instructionProcessor, () -> staticAstContext, s -> {});
    }

    @Test
    void detectMissingResultArguments() {
        assertThrows(InvalidMetadataException.class, 
                () -> createFunctionMapper(
                        List.of(
                                new OpcodeVariant(V6, V7, S, FUNC, Opcode.GETLINK, out("block"), in("link#"))
                        )
                )
        );
    }

    @Test
    void detectTooManyResultArguments() {
        assertThrows(InvalidMetadataException.class,
                () -> createFunctionMapper(
                        List.of(
                                new OpcodeVariant(V6, V6, S, FUNC, Opcode.UCONTROL,   uctrl("getBlock"),  in("x"), in("y"), res("type"), res("building"))
                        )
                )
        );
    }

    @Test
    void detectFunctionNameCollision1() {
        assertThrows(InvalidMetadataException.class,
                () -> createFunctionMapper(
                        List.of(
                                new OpcodeVariant(V6, V7, S, FUNC, Opcode.PRINTFLUSH, block("to")),
                                new OpcodeVariant(V6, V7, S, FUNC, Opcode.UCONTROL, uctrl("printflush"))
                        )
                )
        );
    }

    @Test
    void detectFunctionNameCollision2() {
        assertThrows(InvalidMetadataException.class,
                () -> createFunctionMapper(
                        List.of(
                                new OpcodeVariant(V6, V7, S, FUNC, Opcode.PRINTFLUSH, unused("0")),
                                new OpcodeVariant(V6, V7, S, FUNC, Opcode.UCONTROL, uctrl("printflush"))
                        )
                )
        );
    }
}
