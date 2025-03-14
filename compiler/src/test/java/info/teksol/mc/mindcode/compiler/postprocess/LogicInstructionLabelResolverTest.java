package info.teksol.mc.mindcode.compiler.postprocess;


import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SortCategory;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class LogicInstructionLabelResolverTest extends AbstractCodeOutputTest {

    @Test
    void initializesRemoteVariables() {
        assertOutputs("""
                        module foo;
                        remote v;
                        remote a[10];
                        """,
                """
                        set *mainProcessor @this
                        wait 1e12
                        end
                        draw triangle .v .a*0 .a*1 .a*2 .a*3 .a*4
                        draw triangle .a*5 .a*6 .a*7 .a*8 .a*9 0
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE)
        );
    }

    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertOutputs("""
                        while true do
                            n = n + 1;
                        end;
                        """,
                """
                        jump 4 equal true false
                        op add *tmp0 :n 1
                        set :n *tmp0
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
                        jump 2 always 0 0
                        draw triangle MAX .A 0 0 0 0
                        set MAX 10
                        set .A 20
                        set :i 3
                        print MAX
                        print .A
                        print :i
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
        String expected = Stream.of(
                createInstruction(JUMP, "11", "always"),
                createInstruction(WRITE, ":a", "cell1", "*sp"),
                createInstruction(OP, "add", "*sp", "*sp", "1"),
                createInstruction(OP, "sub", "*sp", "*sp", "1"),
                createInstruction(READ, ":a", "cell1", "*sp"),
                createInstruction(WRITE, "11", "cell1", "*sp"),
                createInstruction(OP, "add", "*sp", "*sp", "1"),
                createInstruction(JUMP, "8", "always"),
                createInstruction(OP, "sub", "*sp", "*sp", "1"),
                createInstruction(READ, "*tmp0", "cell1", "*sp"),
                createInstruction(SET, "@counter", "*tmp0"),
                createInstruction(END),
                createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
        ).map(LogicInstruction::toMlog).collect(Collectors.joining("\n"));

        String actual = LogicInstructionLabelResolver.resolve(
                profile, ip,
                List.of(
                        createInstruction(JUMP, label0, Condition.ALWAYS),
                        createInstruction(PUSH, cell1, a),
                        createInstruction(POP, cell1, a),
                        createInstruction(CALLREC, cell1, label1, label2, fn0retval),
                        createInstruction(LABEL, label1),
                        createInstruction(RETURNREC, cell1),
                        createInstruction(LABEL, label2),
                        createInstruction(LABEL, label0),
                        createInstruction(END)
                )
        ).stream().map(LogicInstruction::toMlog).collect(Collectors.joining("\n"));

        assertEquals(expected, actual);
    }

    @Test
    void sortsCategories() {
        LogicArgument global1 = LogicVariable.global(new AstIdentifier(EMPTY,"b1"));
        LogicArgument global2 = LogicVariable.global(new AstIdentifier(EMPTY, "b2"));
        LogicArgument main1 = LogicVariable.global(new AstIdentifier(EMPTY, "a1"));
        LogicArgument main2 = LogicVariable.global(new AstIdentifier(EMPTY, "a2"));
        LogicArgument param = LogicParameter.parameter(new AstIdentifier(EMPTY, "p"), LogicString.create("x"));
        Set<LogicArgument> variables = new HashSet<>(List.of(global1, global2, main1, main2, param));

        List<LogicArgument> expected = List.of(main1, main2, global1, global2, param);
        List<LogicArgument> actual = LogicInstructionLabelResolver.orderVariables(variables, List.of(SortCategory.ALL, SortCategory.PARAMS));

        assertEquals(expected, actual);
    }
}
