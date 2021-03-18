package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicInstructionPeepholeOptimizerTest extends AbstractAstTest {
    @Test
    void collapsesSetThenPrint() {
        assertEquals(
                List.of(
                        new LogicInstruction("print", "\"n: \""),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("end")
                ),
                LogicInstructionPeepholeOptimizer.optimize(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "print(\"n: \", n)"
                                )
                        )
                )
        );
    }

    @Test
    void collapsesJumps() {
        assertEquals(
                List.of(
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("jump", "label1", "lessThanEq", "n", "0"),
                        new LogicInstruction("op", "add", "n", "n", "1"),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),

                        new LogicInstruction("label", "label2"),
                        new LogicInstruction("jump", "label3", "notEqual", "n", "null"),
                        new LogicInstruction("op", "add", "n", "n", "1"),
                        new LogicInstruction("jump", "label2", "always"),
                        new LogicInstruction("label", "label3"),
                        new LogicInstruction("end")
                ),
                LogicInstructionPeepholeOptimizer.optimize(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "while n > 0\nn += 1\nend\nwhile n === null\nn += 1\nend\n"
                                )
                        )
                )
        );
    }

    @Test
    void collapsesPlusAssignments() {
        assertEquals(
                List.of(
                        new LogicInstruction("op", "add", "n", "n", "1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionPeepholeOptimizer.optimize(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "n += 1"
                                )
                        )
                )
        );
    }


    @Test
    void collapsesAdjacentAssignments() {
        assertEquals(
                List.of(
                        new LogicInstruction("op", "add", "result", "1", "2"),
                        new LogicInstruction("end")
                ),
                LogicInstructionPeepholeOptimizer.optimize(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "result = 1 + 2"
                                )
                        )
                )
        );
    }

    @Test
    void collapsesSimpleAssignments() {
        assertEquals(
                List.of(
                        new LogicInstruction("set", "n", "0"),
                        new LogicInstruction("end")
                ),
                LogicInstructionPeepholeOptimizer.optimize(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "n = 0\n"
                                )
                        )
                )
        );
    }

    @Test
    void collapsesSetWrite() {
        assertEquals(
                List.of(
                        new LogicInstruction("write", "4", "cell1", "2"),
                        new LogicInstruction("end")
                ),
                LogicInstructionPeepholeOptimizer.optimize(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst(
                                        "cell1[2] = 4"
                                )
                        )
                )
        );
    }


    @Test
    void collapsesConditionals() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("read", "tmp1", "cell1", "4"),
                                new LogicInstruction("jump", "label0", "notEqual", "tmp1", "0"),
                                new LogicInstruction("set", "tmp9", "false"),
                                new LogicInstruction("jump", "label1", "always"),
                                new LogicInstruction("label", "label0"),
                                new LogicInstruction("write", "true", "cell1", "4"),
                                new LogicInstruction("op", "add", "n", "n", "1"),
                                new LogicInstruction("set", "tmp9", "n"),
                                new LogicInstruction("label", "label1"),
                                new LogicInstruction("set", "value", "tmp9"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionPeepholeOptimizer.optimize(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst(
                                                "value = if cell1[4] == 0\nfalse\nelse\ncell1[4] = true\nn += 1\nend\n"
                                        )
                                )
                        )
                )
        );
    }
}
