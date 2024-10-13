package info.teksol.mindcode.compiler;

import info.teksol.mindcode.logic.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
class LogicInstructionLabelResolverTest extends AbstractGeneratorTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        TestCompiler compiler = createTestCompiler();
        assertLogicInstructionsMatch(compiler,
                List.of(
                        createInstruction(JUMP, "4", "equal", "true", "false"),
                        createInstruction(OP, "add", var(0), "n", "1"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(JUMP, "0", "always"),
                        createInstruction(END),
                        createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
                ),
                LogicInstructionLabelResolver.resolve(
                        compiler.processor,
                        compiler.profile,
                        generateInstructions("while true do n = n + 1; end;").instructions()
                )
        );
    }

    @Test
    void resolvesVirtualInstructions() {
        TestCompiler compiler = createTestCompiler();
        assertLogicInstructionsMatch(compiler,
                List.of(
                        createInstruction(JUMP, "11", "always"),
                        createInstruction(WRITE, "a", "cell1", "__sp"),
                        createInstruction(OP, "add", "__sp", "__sp", "1"),
                        createInstruction(OP, "sub", "__sp", "__sp", "1"),
                        createInstruction(READ, "a", "cell1", "__sp"),
                        createInstruction(WRITE, "11", "cell1", "__sp"),
                        createInstruction(OP, "add", "__sp", "__sp", "1"),
                        createInstruction(SET, "@counter", "8"),
                        createInstruction(OP, "sub", "__sp", "__sp", "1"),
                        createInstruction(READ, "__tmp0", "cell1", "__sp"),
                        createInstruction(SET, "@counter", "__tmp0"),
                        createInstruction(END),
                        createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
                ),
                LogicInstructionLabelResolver.resolve(
                        compiler.processor,
                        compiler.profile,
                        List.of(
                                createInstruction(JUMP, label0, Condition.ALWAYS),
                                createInstruction(PUSH, cell1, a),
                                createInstruction(POP, cell1, a),
                                createInstruction(CALLREC, cell1, label1, label2, fn0retval),
                                createInstruction(LABEL, label1),
                                createInstruction(RETURN, cell1),
                                createInstruction(LABEL, label2),
                                createInstruction(LABEL, label0),
                                createInstruction(END)
                        )
                )
        );
    }

    @Test
    void resolvesDisabledRemarks() {
        TestCompiler compiler = createTestCompiler();
        compiler.profile.setRemarks(Remarks.NONE);
        assertLogicInstructionsMatch(compiler,
                List.of(
                        createInstruction(PRINT, q("Hello")),
                        createInstruction(END),
                        createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
                ),
                LogicInstructionLabelResolver.resolve(
                        compiler.processor,
                        compiler.profile,
                        generateInstructions("""
                                remark("This is a remark");
                                print("Hello");
                                """
                        ).instructions()
                )
        );
    }

    @Test
    void resolvesPassiveRemarks() {
        TestCompiler compiler = createTestCompiler();
        compiler.profile.setRemarks(Remarks.PASSIVE);
        assertLogicInstructionsMatch(compiler,
                List.of(
                        createInstruction(JUMP, "2", "always"),
                        createInstruction(PRINT, q("This is a remark")),
                        createInstruction(PRINT, q("Hello")),
                        createInstruction(END),
                        createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
                ),
                LogicInstructionLabelResolver.resolve(
                        compiler.processor,
                        compiler.profile,
                        generateInstructions("""
                                remark("This is a remark");
                                print("Hello");
                                """
                        ).instructions()
                )
        );
    }

    @Test
    void resolvesActiveRemarks() {
        TestCompiler compiler = createTestCompiler();
        compiler.profile.setRemarks(Remarks.ACTIVE);
        assertLogicInstructionsMatch(compiler,
                List.of(
                        createInstruction(PRINT, q("This is a remark")),
                        createInstruction(PRINT, q("Hello")),
                        createInstruction(END),
                        createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
                ),
                LogicInstructionLabelResolver.resolve(
                        compiler.processor,
                        compiler.profile,
                        generateInstructions("""
                                remark("This is a remark");
                                print("Hello");
                                """
                        ).instructions()
                )
        );
    }

    @Test
    void processesSortVariables() {
        TestCompiler compiler = createTestCompiler();
        compiler.profile.setSortVariables(List.of(SortCategory.PARAMS, SortCategory.GLOBALS));
        assertLogicInstructionsMatch(compiler,
                List.of(
                        createInstruction(PACKCOLOR, "0", "MAX", "A", "null", "null"),
                        createInstruction(SET, "MAX", "10"),
                        createInstruction(SET, "A", "20"),
                        createInstruction(SET, "i", "3"),
                        createInstruction(PRINT, "MAX"),
                        createInstruction(PRINT, "A"),
                        createInstruction(PRINT, "i"),
                        createInstruction(END),
                        createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
                ),
                LogicInstructionLabelResolver.resolve(
                        compiler.processor,
                        compiler.profile,
                        generateInstructions("""
                                param MAX = 10;
                                A = 20;
                                i = 3;
                                print(MAX, A, i);
                                """
                        ).instructions()
                )
        );
    }

    @Test
    void sortsCategories() {
        LogicArgument global1 = LogicVariable.global("b1");
        LogicArgument global2 = LogicVariable.global("b2");
        LogicArgument main1 = LogicVariable.global("a1");
        LogicArgument main2 = LogicVariable.global("a2");
        LogicArgument param = LogicParameter.parameter("p", LogicString.create("x"));
        Set<LogicArgument> variables = new HashSet<>(List.of(global1, global2, main1, main2, param));

        List<LogicArgument> expected = List.of(main1, main2, global1, global2, param);
        List<LogicArgument> actual = LogicInstructionLabelResolver.orderVariables(variables, List.of(SortCategory.ALL, SortCategory.PARAMS));

        Assertions.assertEquals(expected, actual);
    }
}
