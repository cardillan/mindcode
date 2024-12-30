package info.teksol.mc.mindcode.compiler.postprocess;


import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SortCategory;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class LogicInstructionLabelResolverTest extends AbstractCodeOutputTest {

    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertOutputs("""
                        while true do
                            n = n + 1;
                        end;
                        """,
                """
                        jump 4 equal true false
                        op add __tmp0 n 1
                        set n __tmp0
                        jump 0 always 0 0
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE)
        );
    }


    @Test
    void processesSortVariables() {
        assertOutputs("""
                        #set sort-variables = params, globals;
                        param MAX = 10;
                        A = 20;
                        i = 3;
                        print(MAX, A, i);
                        """,
                """
                        packcolor 0 MAX A null null
                        set MAX 10
                        set A 20
                        set i 3
                        print MAX
                        print A
                        print i
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE)
        );
    }


    @Test
    void resolvesDisabledRemarks() {
        assertOutputs("""
                        #set remarks = none;
                        remark("This is a remark");
                        print("Hello");
                        """,
                """
                        print "Hello"
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE)
        );
    }

    @Test
    void resolvesPassiveRemarks() {
        assertOutputs("""
                        #set remarks = passive;
                        remark("This is a remark");
                        print("Hello");
                        """,
                """
                        jump 2 always 0 0
                        print "This is a remark"
                        print "Hello"
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE)
        );
    }

    @Test
    void resolvesActiveRemarks() {
        assertOutputs("""
                        #set remarks = active;
                        remark("This is a remark");
                        print("Hello");
                        """,
                """
                        print "This is a remark"
                        print "Hello"
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE)
        );
    }

    @Test
    void resolvesVirtualInstructions() {
        assertEquals(
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
                        ip,
                        profile,
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
    void sortsCategories() {
        LogicArgument global1 = LogicVariable.global("b1", false);
        LogicArgument global2 = LogicVariable.global("b2", false);
        LogicArgument main1 = LogicVariable.global("a1", false);
        LogicArgument main2 = LogicVariable.global("a2", false);
        LogicArgument param = LogicParameter.parameter("p", LogicString.create("x"));
        Set<LogicArgument> variables = new HashSet<>(List.of(global1, global2, main1, main2, param));

        List<LogicArgument> expected = List.of(main1, main2, global1, global2, param);
        List<LogicArgument> actual = LogicInstructionLabelResolver.orderVariables(variables, List.of(SortCategory.ALL, SortCategory.PARAMS));

        assertEquals(expected, actual);
    }
}
