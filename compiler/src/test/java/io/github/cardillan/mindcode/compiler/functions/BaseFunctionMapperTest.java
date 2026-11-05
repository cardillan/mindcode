package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.logic.opcodes.*;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static io.github.cardillan.mindcode.logic.opcodes.FunctionMapping.FUNC;
import static io.github.cardillan.mindcode.logic.opcodes.MindustryOpcodeVariants.*;
import static io.github.cardillan.mindcode.logic.opcodes.ProcessorType.S;
import static io.github.cardillan.mindcode.logic.opcodes.ProcessorVersion.V6;
import static io.github.cardillan.mindcode.logic.opcodes.ProcessorVersion.V7;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
class BaseFunctionMapperTest extends AbstractFunctionMapperTest {

    @Test
    void createsAllInstances() {
        assertAll(Stream.of(ProcessorVersion.values()).map(version ->
                () -> createFunctionMapper(MindustryOpcodeVariants.getSpecificOpcodeVariants(version, ProcessorType.W))));
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
