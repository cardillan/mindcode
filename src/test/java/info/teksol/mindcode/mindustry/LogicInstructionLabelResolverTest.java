package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicInstructionLabelResolverTest extends AbstractAstTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("jump", "5", "notEqual", "true", "true"),
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("op", "add", "tmp1", "n", "tmp0"),
                                new LogicInstruction("set", "n", "tmp1"),
                                new LogicInstruction("jump", "0", "always"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("while true\nn = n + 1\nend\n")
                                )
                        )
                )
        );
    }

    @Test
    void resolvesFunctionCallsAndReturns() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("set", "tmp2", "63"),
                                new LogicInstruction("set", "tmp3", "63"),
                                new LogicInstruction("write", "tmp2", "cell1", "tmp3"),
                                new LogicInstruction("set", "tmp5", "63"),
                                new LogicInstruction("read", "tmp6", "cell1", "tmp5"),
                                new LogicInstruction("set", "tmp4", "tmp6"),
                                new LogicInstruction("set", "tmp7", "1"),
                                new LogicInstruction("op", "sub", "tmp8", "tmp4", "tmp7"),
                                new LogicInstruction("set", "tmp4", "tmp8"),
                                new LogicInstruction("write", "13", "cell1", "tmp4"),
                                new LogicInstruction("set", "tmp12", "63"),
                                new LogicInstruction("write", "tmp4", "cell1", "tmp12"),
                                new LogicInstruction("set", "@counter", "24"),
                                new LogicInstruction("set", "tmp15", "63"),
                                new LogicInstruction("read", "tmp16", "cell1", "tmp15"),
                                new LogicInstruction("set", "tmp14", "tmp16"),
                                new LogicInstruction("read", "tmp17", "cell1", "tmp14"),
                                new LogicInstruction("set", "tmp13", "tmp17"),
                                new LogicInstruction("set", "tmp18", "1"),
                                new LogicInstruction("op", "add", "tmp19", "tmp14", "tmp18"),
                                new LogicInstruction("set", "tmp14", "tmp19"),
                                new LogicInstruction("set", "tmp22", "63"),
                                new LogicInstruction("write", "tmp14", "cell1", "tmp22"),
                                new LogicInstruction("end"),
                                new LogicInstruction("set", "tmp23", "1"),
                                new LogicInstruction("set", "tmp26", "63"),
                                new LogicInstruction("read", "tmp27", "cell1", "tmp26"),
                                new LogicInstruction("set", "tmp25", "tmp27"),
                                new LogicInstruction("read", "tmp28", "cell1", "tmp25"),
                                new LogicInstruction("set", "tmp24", "tmp28"),
                                new LogicInstruction("set", "tmp29", "1"),
                                new LogicInstruction("op", "add", "tmp30", "tmp25", "tmp29"),
                                new LogicInstruction("set", "tmp25", "tmp30"),
                                new LogicInstruction("set", "tmp33", "63"),
                                new LogicInstruction("write", "tmp25", "cell1", "tmp33"),
                                new LogicInstruction("set", "tmp35", "63"),
                                new LogicInstruction("read", "tmp36", "cell1", "tmp35"),
                                new LogicInstruction("set", "tmp34", "tmp36"),
                                new LogicInstruction("set", "tmp37", "1"),
                                new LogicInstruction("op", "sub", "tmp38", "tmp34", "tmp37"),
                                new LogicInstruction("set", "tmp34", "tmp38"),
                                new LogicInstruction("write", "tmp23", "cell1, tmp34"),
                                new LogicInstruction("set", "tmp42, 63"),
                                new LogicInstruction("write", "tmp34", "cell1", "tmp42"),
                                new LogicInstruction("set", "@counter", "tmp24"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("allocate stack in cell1\n\ndef bar\n1\nend\n\nbar()")
                                )
                        )
                )
        );
    }
}
