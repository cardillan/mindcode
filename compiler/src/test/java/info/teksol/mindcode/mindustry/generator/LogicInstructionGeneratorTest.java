package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.CompilerProfile;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void convertsComplexAssignment() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "2"),
                        createInstruction(OP, "sub", var(1), "bar", var(0)),
                        createInstruction(SET, var(2), "3"),
                        createInstruction(OP, "mul", var(3), var(1), var(2)),
                        createInstruction(SET, "foo", var(3)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "0"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "5"),
                        createInstruction(OP, "lessThan", var(2), "n", var(1)),
                        createInstruction(JUMP, var(1001), "equal", var(2), "false"),
                        createInstruction(SET, var(3), "1"),
                        createInstruction(OP, "add", var(4), "n", var(3)),
                        createInstruction(SET, "n", var(4)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(5), "\"n: \""),
                        createInstruction(PRINT, var(5)),
                        createInstruction(PRINT, "n"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("n = 0\nwhile n < 5\nn += 1\nend\nprint(\"n: \", n)"))
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "not", var(0), "a"),
                        createInstruction(SET, "a", var(0)),
                        createInstruction(OP, "equal", var(1), "b", "false"),
                        createInstruction(SET, "b", var(1)),
                        createInstruction(OP, "equal", var(2), "c", "false"),
                        createInstruction(SET, "c", var(2)),
                        createInstruction(SET, "x", "null"),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("a = ~a\nb = !b\nc = not c\nx = null"))
        );
    }

    @Test
    void convertsSensorReadings() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "foundation1", "@copper"),
                        createInstruction(SENSOR, var(1), "foundation1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "notEqual", var(0), "true", "false"),
                        createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                        createInstruction(SET, var(1), "\"infinite loop!\""),
                        createInstruction(PRINT, var(1)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("while true != false\nprint(\"infinite loop!\")\nend\nprintflush(message1)\n")
                )
        );
    }

    @Test
    void convertsControlStatements() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(1), "foundation1", "@copper"),
                        createInstruction(SENSOR, var(2), "tank1", "@water"),
                        createInstruction(OP, "strictEqual", var(3), var(1), var(2)),
                        createInstruction(CONTROL, "enabled", "conveyor1", var(3)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("conveyor1.enabled = foundation1.copper === tank1.water")
                )
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(2), "4"),
                        createInstruction(READ, var(3), "cell2", var(2)),
                        createInstruction(SENSOR, var(4), "conveyor1", "@enabled"),
                        createInstruction(OP, "add", var(5), var(3), var(4)),
                        createInstruction(SET, var(6), "3"),
                        createInstruction(WRITE, var(5), "cell1", var(6)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("cell1[3] = cell2[4] + conveyor1.enabled")
                )
        );
    }

    @Test
    void convertsIfExpression() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "4"),
                        createInstruction(READ, var(1), "cell1", var(0)),
                        createInstruction(SET, var(2), "0"),
                        createInstruction(OP, "equal", var(3), var(1), var(2)),
                        createInstruction(JUMP, var(1000), "equal", var(3), "false"),
                        createInstruction(SET, var(9), "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(6), "4"),
                        createInstruction(WRITE, "true", "cell1", var(6)),
                        createInstruction(SET, var(7), "1"),
                        createInstruction(OP, "add", var(8), "n", var(7)),
                        createInstruction(SET, "n", var(8)),
                        createInstruction(SET, var(9), var(8)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "value", var(9)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "value = if cell1[4] == 0\nfalse\nelse\ncell1[4] = true\nn += 1\nend\n"
                        )
                )
        );
    }

    @Test
    void convertsFunctionsReturningValues() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(2), "9"),
                        createInstruction(SET, var(3), "9"),
                        createInstruction(OP, "pow", var(4), var(2), var(3)),
                        createInstruction(OP, "rand", var(5), var(4)),
                        createInstruction(SET, var(6), "0"),
                        createInstruction(WRITE, var(5), "cell1", var(6)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("cell1[0] = rand(9**9)"))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "strictEqual", var(0), "@unit", "null"),
                        createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                        createInstruction(UBIND, "@poly"),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "while @unit === null\nubind(@poly)\nend\n")
                )
        );

    }

    @Test
    void convertsReallifeTest1() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "0"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(GETLINK, var(1), "n"),
                        createInstruction(SET, "reactor", var(1)),
                        createInstruction(OP, "notEqual", var(2), var(1), "null"),
                        createInstruction(JUMP, var(1003), "equal", var(2), "false"),
                        createInstruction(SENSOR, var(3), "reactor", "@liquidCapacity"),
                        createInstruction(SET, var(4), "0"),
                        createInstruction(OP, "greaterThan", var(5), var(3), var(4)),
                        createInstruction(JUMP, var(1000), "equal", var(5), "false"),
                        createInstruction(SENSOR, var(6), "reactor", "@cryofluid"),
                        createInstruction(SENSOR, var(7), "reactor", "@liquidCapacity"),
                        createInstruction(OP, "div", var(8), var(6), var(7)),
                        createInstruction(SET, "pct_avail", var(8)),
                        createInstruction(SET, var(10), "0.25"),
                        createInstruction(OP, "greaterThanEq", var(11), "pct_avail", var(10)),
                        createInstruction(CONTROL, "enabled", "reactor", var(11)),
                        createInstruction(SET, var(12), var(11)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(12), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(13), "1"),
                        createInstruction(OP, "add", var(14), "n", var(13)),
                        createInstruction(SET, "n", var(14)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "" +
                                        "n = 0\n" +
                                        "\n" +
                                        "while (reactor = getlink(n)) != null\n" +
                                        "  if reactor.liquidCapacity > 0\n" +
                                        "    pct_avail = reactor.cryofluid / reactor.liquidCapacity\n" +
                                        "    reactor.enabled = pct_avail >= 0.25\n" +
                                        "  end\n" +
                                        "\n" +
                                        "  n += 1\n" +
                                        "end\n" +
                                        ""
                        )
                )
        );
    }

    @Test
    void convertsUnaryMinus() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "-1"),
                        createInstruction(OP, "mul", var(1), "dx", var(0)),
                        createInstruction(SET, "dx", var(1)),
                        createInstruction(SET, var(2), "-1"),
                        createInstruction(SET, "dy", var(2)),
                        createInstruction(SET, var(3), "2"),
                        createInstruction(SET, var(4), "1"),
                        createInstruction(OP, "sub", var(5), var(3), var(4)),
                        createInstruction(SET, "dz", var(5)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "dx *= -1\ndy = -1\ndz = 2 - 1"
                        )
                )
        );
    }

    @Test
    void removesCommentsFromLogicInstructions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(SET, "a", var(0)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "// Remember that we initialized ourselves\n\na = 1"
                        )
                )
        );
    }

    @Test
    void generatesRefsWithDashInThem() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(SET, var(1), "0"),
                        createInstruction(UCONTROL, "build", "x", "y", "@titanium-conveyor", var(0), var(1)),
                        createInstruction(UCONTROL, "getBlock", "x", "y", "b_type", "b_building", "b_floor"),
                        createInstruction(OP, "equal", var(3), "b_type", "@titanium-conveyor"),
                        createInstruction(JUMP, var(1000), "equal", var(3), "false"),
                        createInstruction(SET, var(4), "1"),
                        createInstruction(OP, "add", var(5), "n", var(4)),
                        createInstruction(SET, "n", var(5)),
                        createInstruction(SET, var(6), var(5)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(6), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "build(x, y, @titanium-conveyor, 1, 0)\ngetBlock(x, y, b_type, b_building, b_floor)\nif b_type == @titanium-conveyor\nn += 1\nend\n"
                        )
                )
        );
    }

    @Test
    void generatesComplexMathExpression() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(OP, "rand", var(1), var(0)),
                        createInstruction(OP, "tan", var(2), var(1)),
                        createInstruction(OP, "abs", var(3), var(2)),
                        createInstruction(OP, "cos", var(4), var(3)),
                        createInstruction(OP, "log", var(5), var(4)),
                        createInstruction(OP, "sin", var(6), var(5)),
                        createInstruction(OP, "floor", var(7), var(6)),
                        createInstruction(OP, "ceil", var(8), var(7)),
                        createInstruction(SET, "x", var(8)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
                )
        );
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "17"),
                        createInstruction(OP, "lessThanEq", var(2), "n", var(1)),
                        createInstruction(JUMP, var(1001), "equal", var(2), "false"),
                        createInstruction(PRINT, "n"),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(SET, var(3), "1"),
                        createInstruction(OP, "add", var(4), "n", var(3)),
                        createInstruction(SET, "n", var(4)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for n in 1 .. 17\nprint(n)\nend")
                )
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        // init
                        createInstruction(SET, var(0), "1"),
                        createInstruction(SET, "n", var(0)),

                        // cond
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "17"),
                        createInstruction(OP, "lessThan", var(2), "n", var(1)),
                        createInstruction(JUMP, var(1001), "equal", var(2), "false"),

                        // loop body
                        createInstruction(PRINT, "n"),

                        // continue label
                        createInstruction(LABEL, var(1010)),

                        // increment
                        createInstruction(SET, var(3), "1"),
                        createInstruction(OP, "add", var(4), "n", var(3)),
                        createInstruction(SET, "n", var(4)),

                        // loop
                        createInstruction(JUMP, var(1000), "always"),

                        // trailer
                        createInstruction(LABEL, var(1001)),

                        // rest of program
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for n in 1 ... 17\nprint(n)\nend\n")
                )
        );
    }

    @Test
    void parsesRangeExpressionLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        // init
                        createInstruction(SET, "n", "a"),

                        // cond
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "lessThan", var(0), "n", "b"),
                        createInstruction(JUMP, var(1001), "equal", var(0), "false"),

                        // loop body
                        createInstruction(PRINT, "n"),

                        // continue label
                        createInstruction(LABEL, var(1010)),

                        // increment
                        createInstruction(SET, var(1), "1"),
                        createInstruction(OP, "add", var(2), "n", var(1)),
                        createInstruction(SET, "n", var(2)),

                        // loop
                        createInstruction(JUMP, var(1000), "always"),

                        // trailer
                        createInstruction(LABEL, var(1001)),

                        // rest of program
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for n in a ... b\nprint(n)\nend\n")
                )
        );
    }
    @Test
    void generatesCStyleComplexForLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "0"),
                        createInstruction(SET, "i", var(0)),
                        createInstruction(SET, var(1), "-5"),
                        createInstruction(SET, "j", var(1)),

                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(2), "5"),
                        createInstruction(OP, "lessThan", var(3), "i", var(2)),
                        createInstruction(JUMP, var(1001), "equal", var(3), "false"),
                        createInstruction(PRINT, "n"),

                        createInstruction(LABEL, var(1010)),

                        createInstruction(SET, var(4), "1"),
                        createInstruction(OP, "sub", var(5), "j", var(4)),
                        createInstruction(SET, "j", var(5)),

                        createInstruction(SET, var(6), "1"),
                        createInstruction(OP, "add", var(7), "i", var(6)),
                        createInstruction(SET, "i", var(7)),

                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
                )
        );
    }

    @Test
    void supportsAssigningAssignmentResults() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "42"),
                        createInstruction(SET, "b", var(0)),
                        createInstruction(SET, "a", var(0)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("a = b = 42")
                )
        );
    }

    @Test
    void generatesCaseWhenElse() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),
                        createInstruction(SET, var(2), "1"),
                        createInstruction(JUMP, var(1001), "equal", var(0), var(2)),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(3), "\"1\""),
                        createInstruction(SET, var(1), var(3)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(4), "2"),
                        createInstruction(JUMP, var(1003), "equal", var(0), var(4)),
                        createInstruction(JUMP, var(1004), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(5), "\"two\""),
                        createInstruction(SET, var(1), var(5)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(6), "\"otherwise\""),
                        createInstruction(SET, var(1), var(6)),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("case n\nwhen 1\n\"1\"\nwhen 2\n\"two\"\nelse\n\"otherwise\"end\n")
                )
        );
    }

    @Test
    void generatesCaseWhen() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "0"),
                        createInstruction(READ, var(1), "cell1", var(0)),
                        createInstruction(SET, var(100), var(1)),
                        createInstruction(JUMP, var(1002), "equal", var(100), "ST_EMPTY"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(3), "0"),
                        createInstruction(WRITE, "ST_INITIALIZED", "cell1", var(3)),
                        createInstruction(SET, var(2), "ST_INITIALIZED"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1004), "equal", var(100), "ST_INITIALIZED"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(4), "0"),
                        createInstruction(WRITE, "ST_DONE", "cell1", var(4)),
                        createInstruction(SET, var(2), "ST_DONE"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(2), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("allocate heap in cell1[0..10]\ncase $state\nwhen ST_EMPTY\n$state = ST_INITIALIZED\nwhen ST_INITIALIZED\n$state = ST_DONE\nend\n")
                )
        );
    }

    @Test
    void generatesCaseWhenMultiple() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(100), "n"),

                        // First alternative
                        createInstruction(SET, var(1), "1"),
                        createInstruction(JUMP, var(1002), "equal", var(100), var(1)),
                        createInstruction(SET, var(2), "2"),
                        createInstruction(JUMP, var(1002), "equal", var(100), var(2)),
                        createInstruction(SET, var(3), "3"),
                        createInstruction(JUMP, var(1002), "equal", var(100), var(3)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(4), "\"Few\""),
                        createInstruction(SET, var(0), var(4)),
                        createInstruction(JUMP, var(1000), "always"),

                        // Second alternative
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(5), "4"),
                        createInstruction(JUMP, var(1004), "equal", var(100), var(5)),
                        createInstruction(SET, var(6), "5"),
                        createInstruction(JUMP, var(1004), "equal", var(100), var(6)),
                        createInstruction(SET, var(7), "6"),
                        createInstruction(JUMP, var(1004), "equal", var(100), var(7)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(8), "\"Several\""),
                        createInstruction(SET, var(0), var(8)),
                        createInstruction(JUMP, var(1000), "always"),

                        /// Else branch
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(9), "\"Many\""),
                        createInstruction(SET, var(0), var(9)),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("case n\nwhen 1, 2, 3\n\"Few\"\nwhen 4,5,6\n\"Several\"\nelse\n\"Many\"end\n")
                )
        );
    }

    @Test
    void generatesCaseWhenMultipleWithRanges() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),
                        createInstruction(SET, var(1), "0"),
                        createInstruction(JUMP, var(1003), "lessThan", "__ast0", var(1)),
                        createInstruction(SET, var(2), "4"),
                        createInstruction(JUMP, var(1002), "lessThanEq", "__ast0", var(2)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(3), "6"),
                        createInstruction(JUMP, var(1004), "lessThan", "__ast0", var(3)),
                        createInstruction(SET, var(4), "8"),
                        createInstruction(JUMP, var(1002), "lessThanEq", "__ast0", var(4)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(5), "10"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", var(5)),
                        createInstruction(SET, var(6), "12"),
                        createInstruction(JUMP, var(1002), "equal", "__ast0", var(6)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(7), "\"A number I like\""),
                        createInstruction(SET, var(0), var(7)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("case n when 0 .. 4, 6 .. 8, 10, 12 \"A number I like\" end")
                )
        );
    }

    @Test
    void generatesDrawings() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, "clear", "r", "g", "b"),
                        createInstruction(DRAW, "color", "r", "g", "b", "alpha"),
                        createInstruction(DRAW, "stroke", "width"),
                        createInstruction(DRAW, "line", "x1", "y1", "x2", "y2"),
                        createInstruction(DRAW, "rect", "x", "y", "w", "h"),
                        createInstruction(DRAW, "lineRect", "x", "y", "w", "h"),
                        createInstruction(DRAW, "poly", "x", "y", "sides", "radius", "rotation"),
                        createInstruction(DRAW, "linePoly", "x", "y", "sides", "radius", "rotation"),
                        createInstruction(DRAW, "triangle", "x1", "y1", "x2", "y2", "x3", "y3"),
                        createInstruction(DRAW, "image", "x", "y", "@copper", "size", "rotation"),
                        createInstruction(DRAWFLUSH, "display1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("clear(r, g, b)\ncolor(r, g, b, alpha)\nstroke(width)\nline(x1, y1, x2, y2)\nrect(x, y, w, h)\nlineRect(x, y, w, h)\npoly(x, y, sides, radius, rotation)\nlinePoly(x, y, sides, radius, rotation)\ntriangle(x1, y1, x2, y2, x3, y3)\nimage(x, y, @copper, size, rotation)\ndrawflush(display1)\n")
                )
        );
    }

    @Test
    void generatesURadar() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(URADAR, "enemy", "ground", "any", "health", "0", "MIN_TO_MAX", var(0)),
                        createInstruction(SET, "target", var(0)),
                        createInstruction(OP, "notEqual", var(1), "target", "null"),
                        createInstruction(JUMP, var(1002), "equal", var(1), "false"),
                        createInstruction(SENSOR, var(2), "target", "@x"),
                        createInstruction(SENSOR, var(3), "target", "@y"),
                        createInstruction(SET, var(4), "10"),
                        createInstruction(UCONTROL, "approach", var(2), var(3), var(4)),
                        createInstruction(SENSOR, var(5), "target", "@x"),
                        createInstruction(SENSOR, var(6), "target", "@y"),
                        createInstruction(SET, var(7), "10"),
                        createInstruction(UCONTROL, "within", var(5), var(6), var(7), var(8)),
                        createInstruction(JUMP, var(1000), "equal", var(8), "false"),
                        createInstruction(SENSOR, var(9), "target", "@x"),
                        createInstruction(SENSOR, var(10), "target", "@y"),
                        createInstruction(UCONTROL, "target", var(9), var(10), "SHOOT"),
                        createInstruction(SET, var(11), "null"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(11), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(12), var(11)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(12), "null"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "target = uradar(enemy, ground, any, health, MIN_TO_MAX)\n" +
                                "if target != null\n" +
                                "  approach(target.x, target.y, 10)\n" +
                                "  if within(target.x, target.y, 10)\n" +
                                "    target(target.x, target.y, SHOOT)\n" +
                                "  end\n" +
                                "end\n"
                        )
                )
        );
    }

    @Test
    void generatesULocate() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        createInstruction(ULOCATE, "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        createInstruction(ULOCATE, "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "ulocate(ore, @surge-alloy, outx, outy)\n" +
                                "ulocate(building, core, ENEMY, outx, outy, outbuilding)\n" +
                                "ulocate(spawn, outx, outy, outbuilding)\n" +
                                "ulocate(damaged, outx, outy, outbuilding)\n"
                        )
                )
        );
    }

    @Test
    void generatesRadar() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", var(0), var(1)),
                        createInstruction(SET, "out", var(1)),
                        createInstruction(SET, var(2), "1"),
                        createInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", var(2), var(3)),
                        createInstruction(SET, "out", var(3)),
                        createInstruction(SET, "src", "salvo1"),
                        createInstruction(SET, var(4), "1"),
                        createInstruction(RADAR, "enemy", "any", "any", "distance", "src", var(4), var(5)),
                        createInstruction(SET, "out", var(5)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "out = radar(enemy, any, any, distance, salvo1, 1)\n" +
                                "out = radar(ally, flying, any, health, lancer1, 1)\n" +
                                "src = salvo1\n" +
                                "out = radar(enemy, any, any, distance, src, 1)\n"
                        )
                )
        );  
    }

    @Test
    void generatesWait() {
        setInstructionProcessor(InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR));
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(WAIT, var(0)),
                        createInstruction(SET, var(1), "0.001"),
                        createInstruction(WAIT, var(1)),
                        createInstruction(SET, var(2), "1000"),
                        createInstruction(WAIT, var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("wait(1)\nwait(0.001)\nwait(1000)")
                )
        );  
    }

    @Test
    void generatesEndFromFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "equal", var(0), "some_cond", "false"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(END),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "if some_cond == false\n  end()\nend"
                        )
                )
        );
    }

    @Test
    void generatesModuloOperator() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "2"),
                        createInstruction(OP, "mod", var(1), "@tick", var(0)),
                        createInstruction(SET, var(2), "0"),
                        createInstruction(OP, "equal", var(3), var(1), var(2)),
                        createInstruction(SET, "running", var(3)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "running = @tick % 2 == 0"
                        )
                )
        );
    }

    @Test
    void refusesToDeclareFunctionsWhenNoStackAround() {
        assertThrows(MissingStackException.class, () ->
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "def foo\n0\nend\n\n\nfoo()\n"
                        )
                )
        );
    }

    @Test
    void generatesCodeForFunctionCallingAndReturn() {
        assertLogicInstructionsMatch(
                List.of(
                        // setup stack
                        createInstruction(SET, var(2), "63"),
                        createInstruction(SET, var(3), "63"),
                        createInstruction(WRITE, var(2), "cell1", var(3)),

                        // push return address on stack
                        createInstruction(SET, var(5), "63"),
                        createInstruction(READ, var(6), "cell1", var(5)), // read stack pointer
                        createInstruction(SET, var(4), var(6)),
                        createInstruction(SET, var(7), "1"),
                        createInstruction(OP, "sub", var(8), var(4), var(7)), // calculate new stack pointer
                        createInstruction(SET, var(4), var(8)),
                        createInstruction(WRITE, var(1001), "cell1", var(4)), // write value on stack, at stack pointer
                        createInstruction(SET, var(12), "63"),
                        createInstruction(WRITE, var(4), "cell1", var(12)), // update stack pointer itself

                        // jump to function
                        createInstruction(SET, "@counter", var(1000)),

                        // return label
                        createInstruction(LABEL, var(1001)),

                        // pop return value from stack
                        createInstruction(SET, var(15), "63"),
                        createInstruction(READ, var(16), "cell1", var(15)), // read stack pointer
                        createInstruction(SET, var(14), var(16)),
                        createInstruction(READ, var(17), "cell1", var(14)), // read value on stack, at stack pointer
                        createInstruction(SET, var(13), var(17)),
                        createInstruction(SET, var(18), "1"),
                        createInstruction(OP, "add", var(19), var(14), var(18)), // calculate new stack pointer
                        createInstruction(SET, var(14), var(19)),
                        createInstruction(SET, var(22), "63"),
                        createInstruction(WRITE, var(14), "cell1", var(22)), // update stack pointer itself

                        // continue rest of main script
                        createInstruction(SET, "x", var(13)),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINTFLUSH, "message1"),

                        // end of main script
                        createInstruction(END),

                        // generateUnoptimized of function foo
                        createInstruction(LABEL, var(1000)),

                        // no parameters to pop
                        createInstruction(SET, var(23), "0"),

                        // pop return address
                        createInstruction(SET, var(26), "63"),
                        createInstruction(READ, var(27), "cell1", var(26)),
                        createInstruction(SET, var(25), var(27)),
                        createInstruction(READ, var(28), "cell1", var(25)),
                        createInstruction(SET, var(24), var(28)),
                        createInstruction(SET, var(29), "1"),
                        createInstruction(OP, "add", var(30), var(25), var(29)),
                        createInstruction(SET, var(25), var(30)),
                        createInstruction(SET, var(33), "63"),
                        createInstruction(WRITE, var(25), "cell1", var(33)),

                        // push return value
                        createInstruction(SET, var(35), "63"),
                        createInstruction(READ, var(36), "cell1", var(35)),
                        createInstruction(SET, var(34), var(36)),
                        createInstruction(SET, var(37), "1"),
                        createInstruction(OP, "sub", var(38), var(34), var(37)),
                        createInstruction(SET, var(34), var(38)),
                        createInstruction(WRITE, var(23), "cell1", var(34)),
                        createInstruction(SET, var(42), "63"),
                        createInstruction(WRITE, var(34), "cell1", var(42)),

                        // jump to return address
                        createInstruction(SET, "@counter", var(24)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "allocate stack in cell1\ndef foo\n0\nend\n\n\nx = foo()\nprint(x)\nprintflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void passesParametersToFunction() {
        assertLogicInstructionsMatch(
                List.of(
                        // setup stack
                        createInstruction(SET, var(2), "63"),
                        createInstruction(SET, var(3), "63"),
                        createInstruction(WRITE, var(2), "cell1", var(3)),

                        // push return address on stack
                        createInstruction(SET, var(4), "4"),
                        createInstruction(SET, "boo", var(4)),
                        createInstruction(SET, var(5), "2"),

                        // push return address on stack
                        createInstruction(SET, var(7), "63"),
                        createInstruction(READ, var(8), "cell1", var(7)),
                        createInstruction(SET, var(6), var(8)),
                        createInstruction(SET, var(9), "1"),
                        createInstruction(OP, "sub", var(10), var(6), var(9)),
                        createInstruction(SET, var(6), var(10)),
                        createInstruction(WRITE, var(1001), "cell1", var(6)),
                        createInstruction(SET, var(14), "63"),
                        createInstruction(WRITE, var(6), "cell1", var(14)),

                        // push n on stack
                        createInstruction(SET, var(16), "63"),
                        createInstruction(READ, var(17), "cell1", var(16)),
                        createInstruction(SET, var(15), var(17)),
                        createInstruction(SET, var(18), "1"),
                        createInstruction(OP, "sub", var(19), var(15), var(18)),
                        createInstruction(SET, var(15), var(19)),
                        createInstruction(WRITE, var(5), "cell1", var(15)),
                        createInstruction(SET, var(23), "63"),
                        createInstruction(WRITE, var(15), "cell1", var(23)),

                        // push r on stack
                        createInstruction(SET, var(25), "63"),
                        createInstruction(READ, var(26), "cell1", var(25)),
                        createInstruction(SET, var(24), var(26)),
                        createInstruction(SET, var(27), "1"),
                        createInstruction(OP, "sub", var(28), var(24), var(27)),
                        createInstruction(SET, var(24), var(28)),
                        createInstruction(WRITE, "boo", "cell1", var(24)),
                        createInstruction(SET, var(32), "63"),
                        createInstruction(WRITE, var(24), "cell1", var(32)),

                        // jump to function
                        createInstruction(SET, "@counter", var(1000)),

                        // function return address
                        createInstruction(LABEL, var(1001)),

                        // pop return value from stack
                        createInstruction(SET, var(35), "63"),
                        createInstruction(READ, var(36), "cell1", var(35)),
                        createInstruction(SET, var(34), var(36)),
                        createInstruction(READ, var(37), "cell1", var(34)),
                        createInstruction(SET, var(33), var(37)),
                        createInstruction(SET, var(38), "1"),
                        createInstruction(OP, "add", var(39), var(34), var(38)),
                        createInstruction(SET, var(34), var(39)),
                        createInstruction(SET, var(42), "63"),
                        createInstruction(WRITE, var(34), "cell1", var(42)),

                        // continue main body
                        createInstruction(SET, "x", var(33)),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END),

                        // function foo
                        createInstruction(LABEL, var(1000)),

                        // pop r
                        createInstruction(SET, var(45), "63"),
                        createInstruction(READ, var(46), "cell1", var(45)),
                        createInstruction(SET, var(44), var(46)),
                        createInstruction(READ, var(47), "cell1", var(44)),
                        createInstruction(SET, var(43), var(47)),
                        createInstruction(SET, var(48), "1"),
                        createInstruction(OP, "add", var(49), var(44), var(48)),
                        createInstruction(SET, var(44), var(49)),
                        createInstruction(SET, var(52), "63"),
                        createInstruction(WRITE, var(44), "cell1", var(52)),
                        createInstruction(SET, "r", var(43)),

                        // pop n
                        createInstruction(SET, var(55), "63"),
                        createInstruction(READ, var(56), "cell1", var(55)),
                        createInstruction(SET, var(54), var(56)),
                        createInstruction(READ, var(57), "cell1", var(54)),
                        createInstruction(SET, var(53), var(57)),
                        createInstruction(SET, var(58), "1"),
                        createInstruction(OP, "add", var(59), var(54), var(58)),
                        createInstruction(SET, var(54), var(59)),
                        createInstruction(SET, var(62), "63"),
                        createInstruction(WRITE, var(54), "cell1", var(62)),
                        createInstruction(SET, "n", var(53)),

                        // execute function body
                        createInstruction(SET, var(63), "2"),
                        createInstruction(OP, "pow", var(64), "n", "r"),
                        createInstruction(OP, "mul", var(65), var(63), var(64)),

                        // pop return address from stack
                        createInstruction(SET, var(68), "63"),
                        createInstruction(READ, var(69), "cell1", var(68)),
                        createInstruction(SET, var(67), var(69)),
                        createInstruction(READ, var(70), "cell1", var(67)),
                        createInstruction(SET, var(66), var(70)),
                        createInstruction(SET, var(71), "1"),
                        createInstruction(OP, "add", var(72), var(67), var(71)),
                        createInstruction(SET, var(67), var(72)),
                        createInstruction(SET, var(75), "63"),
                        createInstruction(WRITE, var(67), "cell1", var(75)),

                        // push return value on stack
                        createInstruction(SET, var(77), "63"),
                        createInstruction(READ, var(78), "cell1", var(77)),
                        createInstruction(SET, var(76), var(78)),
                        createInstruction(SET, var(79), "1"),
                        createInstruction(OP, "sub", var(80), var(76), var(79)),
                        createInstruction(SET, var(76), var(80)),
                        createInstruction(WRITE, var(65), "cell1", var(76)),
                        createInstruction(SET, var(84), "63"),
                        createInstruction(WRITE, var(76), "cell1", var(84)),

                        // jump back to caller
                        createInstruction(SET, "@counter", var(66)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "allocate stack in cell1\ndef foo(n, r)\n2 * (n ** r)\nend\n\n\nboo = 4\nx = foo(2, boo)\nprint(x)\nprintflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void functionsCanCallOtherFunctions() {
        assertLogicInstructionsMatch(
                List.of(
                        // setup stack
                        createInstruction(SET, var(2), "63"),
                        createInstruction(SET, var(3), "63"),
                        createInstruction(WRITE, var(2), "cell1", var(3)),

                        // prepare parameters
                        createInstruction(SET, var(4), "8"),
                        createInstruction(SET, "boo", var(4)),
                        createInstruction(SET, var(5), "7"),

                        // push return address
                        createInstruction(SET, var(7), "63"),
                        createInstruction(READ, var(8), "cell1", var(7)),
                        createInstruction(SET, var(6), var(8)),
                        createInstruction(SET, var(9), "1"),
                        createInstruction(OP, "sub", var(10), var(6), var(9)),
                        createInstruction(SET, var(6), var(10)),
                        createInstruction(WRITE, var(1002), "cell1", var(6)),
                        createInstruction(SET, var(14), "63"),
                        createInstruction(WRITE, var(6), "cell1", var(14)),

                        // push n
                        createInstruction(SET, var(16), "63"),
                        createInstruction(READ, var(17), "cell1", var(16)),
                        createInstruction(SET, var(15), var(17)),
                        createInstruction(SET, var(18), "1"),
                        createInstruction(OP, "sub", var(19), var(15), var(18)),
                        createInstruction(SET, var(15), var(19)),
                        createInstruction(WRITE, var(5), "cell1", var(15)),
                        createInstruction(SET, var(23), "63"),
                        createInstruction(WRITE, var(15), "cell1", var(23)),

                        // push r
                        createInstruction(SET, var(25), "63"),
                        createInstruction(READ, var(26), "cell1", var(25)),
                        createInstruction(SET, var(24), var(26)),
                        createInstruction(SET, var(27), "1"),
                        createInstruction(OP, "sub", var(28), var(24), var(27)),
                        createInstruction(SET, var(24), var(28)),
                        createInstruction(WRITE, "boo", "cell1", var(24)),
                        createInstruction(SET, var(32), "63"),
                        createInstruction(WRITE, var(24), "cell1", var(32)),

                        // jump to function
                        createInstruction(SET, "@counter", var(1000)),

                        // return address
                        createInstruction(LABEL, var(1002)),

                        // pop return value from stack
                        createInstruction(SET, var(35), "63"),
                        createInstruction(READ, var(36), "cell1", var(35)),
                        createInstruction(SET, var(34), var(36)),
                        createInstruction(READ, var(37), "cell1", var(34)),
                        createInstruction(SET, var(33), var(37)),
                        createInstruction(SET, var(38), "1"),
                        createInstruction(OP, "add", var(39), var(34), var(38)),
                        createInstruction(SET, var(34), var(39)),
                        createInstruction(SET, var(42), "63"),
                        createInstruction(WRITE, var(34), "cell1", var(42)),

                        // rest of main body
                        createInstruction(SET, "x", var(33)),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END),

                        // def bar
                        createInstruction(LABEL, var(1001)),

                        // pop bar.x
                        createInstruction(SET, var(45), "63"),
                        createInstruction(READ, var(46), "cell1", var(45)),
                        createInstruction(SET, var(44), var(46)),
                        createInstruction(READ, var(47), "cell1", var(44)),
                        createInstruction(SET, var(43), var(47)),
                        createInstruction(SET, var(48), "1"),
                        createInstruction(OP, "add", var(49), var(44), var(48)),
                        createInstruction(SET, var(44), var(49)),
                        createInstruction(SET, var(52), "63"),
                        createInstruction(WRITE, var(44), "cell1", var(52)),
                        createInstruction(SET, "x", var(43)),

                        // function body
                        createInstruction(SET, var(53), "2"),
                        createInstruction(OP, "mul", var(54), var(53), "x"),

                        // pop return address
                        createInstruction(SET, var(57), "63"),
                        createInstruction(READ, var(58), "cell1", var(57)),
                        createInstruction(SET, var(56), var(58)),
                        createInstruction(READ, var(59), "cell1", var(56)),
                        createInstruction(SET, var(55), var(59)),
                        createInstruction(SET, var(60), "1"),
                        createInstruction(OP, "add", var(61), var(56), var(60)),
                        createInstruction(SET, var(56), var(61)),
                        createInstruction(SET, var(64), "63"),
                        createInstruction(WRITE, var(56), "cell1", var(64)),

                        // push return value
                        createInstruction(SET, var(66), "63"),
                        createInstruction(READ, var(67), "cell1", var(66)),
                        createInstruction(SET, var(65), var(67)),
                        createInstruction(SET, var(68), "1"),
                        createInstruction(OP, "sub", var(69), var(65), var(68)),
                        createInstruction(SET, var(65), var(69)),
                        createInstruction(WRITE, var(54), "cell1", var(65)),
                        createInstruction(SET, var(73), "63"),
                        createInstruction(WRITE, var(65), "cell1", var(73)),

                        // return
                        createInstruction(SET, "@counter", var(55)),
                        createInstruction(END),

                        // def foo
                        createInstruction(LABEL, var(1000)),

                        // pop foo.r
                        createInstruction(SET, var(76), "63"),
                        createInstruction(READ, var(77), "cell1", var(76)),
                        createInstruction(SET, var(75), var(77)),
                        createInstruction(READ, var(78), "cell1", var(75)),
                        createInstruction(SET, var(74), var(78)),
                        createInstruction(SET, var(79), "1"),
                        createInstruction(OP, "add", var(80), var(75), var(79)),
                        createInstruction(SET, var(75), var(80)),
                        createInstruction(SET, var(83), "63"),
                        createInstruction(WRITE, var(75), "cell1", var(83)),
                        createInstruction(SET, "r", var(74)),

                        // pop foo.n
                        createInstruction(SET, var(86), "63"),
                        createInstruction(READ, var(87), "cell1", var(86)),
                        createInstruction(SET, var(85), var(87)),
                        createInstruction(READ, var(88), "cell1", var(85)),
                        createInstruction(SET, var(84), var(88)),
                        createInstruction(SET, var(89), "1"),
                        createInstruction(OP, "add", var(90), var(85), var(89)),
                        createInstruction(SET, var(85), var(90)),
                        createInstruction(SET, var(93), "63"),
                        createInstruction(WRITE, var(85), "cell1", var(93)),
                        createInstruction(SET, "n", var(84)),

                        // function body
                        createInstruction(SET, var(94), "2"),

                        // push return address
                        createInstruction(SET, var(96), "63"),
                        createInstruction(READ, var(97), "cell1", var(96)),
                        createInstruction(SET, var(95), var(97)),
                        createInstruction(SET, var(98), "1"),
                        createInstruction(OP, "sub", var(99), var(95), var(98)),
                        createInstruction(SET, var(95), var(99)),
                        createInstruction(WRITE, var(1003), "cell1", var(95)),
                        createInstruction(SET, var(103), "63"),
                        createInstruction(WRITE, var(95), "cell1", var(103)),

                        // push bar.x
                        createInstruction(SET, var(105), "63"),
                        createInstruction(READ, var(106), "cell1", var(105)),
                        createInstruction(SET, var(104), var(106)),
                        createInstruction(SET, var(107), "1"),
                        createInstruction(OP, "sub", var(108), var(104), var(107)),
                        createInstruction(SET, var(104), var(108)),
                        createInstruction(WRITE, "r", "cell1", var(104)),
                        createInstruction(SET, var(112), "63"),
                        createInstruction(WRITE, var(104), "cell1", var(112)),

                        // jump to subroutine
                        createInstruction(SET, "@counter", var(1001)),

                        // return address
                        createInstruction(LABEL, var(1003)),

                        // pop return value from stack
                        createInstruction(SET, var(115), "63"),
                        createInstruction(READ, var(116), "cell1", var(115)),
                        createInstruction(SET, var(114), var(116)),
                        createInstruction(READ, var(117), "cell1", var(114)),
                        createInstruction(SET, var(113), var(117)),
                        createInstruction(SET, var(118), "1"),
                        createInstruction(OP, "add", var(119), var(114), var(118)),
                        createInstruction(SET, var(114), var(119)),
                        createInstruction(SET, var(122), "63"),
                        createInstruction(WRITE, var(114), "cell1", var(122)),

                        // function body
                        createInstruction(OP, "pow", var(123), "n", var(113)),
                        createInstruction(OP, "mul", var(124), var(94), var(123)),

                        // pop return address
                        createInstruction(SET, var(127), "63"),
                        createInstruction(READ, var(128), "cell1", var(127)),
                        createInstruction(SET, var(126), var(128)),
                        createInstruction(READ, var(129), "cell1", var(126)),
                        createInstruction(SET, var(125), var(129)),
                        createInstruction(SET, var(130), "1"),
                        createInstruction(OP, "add", var(131), var(126), var(130)),
                        createInstruction(SET, var(126), var(131)),
                        createInstruction(SET, var(134), "63"),
                        createInstruction(WRITE, var(126), "cell1", var(134)),

                        // push return value
                        createInstruction(SET, var(136), "63"),
                        createInstruction(READ, var(137), "cell1", var(136)),
                        createInstruction(SET, var(135), var(137)),
                        createInstruction(SET, var(138), "1"),
                        createInstruction(OP, "sub", var(139), var(135), var(138)),
                        createInstruction(SET, var(135), var(139)),
                        createInstruction(WRITE, var(124), "cell1", var(135)),
                        createInstruction(SET, var(143), "63"),
                        createInstruction(WRITE, var(135), "cell1", var(143)),

                        // return
                        createInstruction(SET, "@counter", var(125)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "allocate stack in cell1\ndef foo(n, r)\n2 * (n ** bar(r))\nend\n\ndef bar(x)\n2 * x\nend\n\n\nboo = 8\nx = foo(7, boo)\nprint(x)\nprintflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void generatesMultiParameterControlInstruction() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "leader", "@shootX"),
                        createInstruction(SENSOR, var(1), "leader", "@shootY"),
                        createInstruction(SENSOR, var(2), "leader", "@shooting"),
                        createInstruction(CONTROL, "shoot", "turret", var(0), var(1), var(2)),
                        createInstruction(SET, var(3), "14"),
                        createInstruction(SET, var(4), "15"),
                        createInstruction(SET, var(5), "16"),
                        createInstruction(CONTROL, "color", "turret", var(3), var(4), var(5)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "turret.shoot(leader.shootX, leader.shootY, leader.shooting)\nturret.color(14, 15, 16)\n"
                        )
                )
        );
    }

    @Test
    void canIndirectlyReferenceHeap() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "HEAPPTR", "cell1"),
                        createInstruction(SET, var(2), "0"),
                        createInstruction(SET, var(3), "0"),
                        createInstruction(WRITE, var(2), "HEAPPTR", var(3)), // write $dx
                        createInstruction(SET, var(6), "1"),
                        createInstruction(READ, var(7), "HEAPPTR", var(6)), // read $dy
                        createInstruction(SET, var(8), "0"),
                        createInstruction(READ, var(9), "HEAPPTR", var(8)), // read $dx
                        createInstruction(OP, "add", var(10), var(7), var(9)), // tmp10 = $dx + $dy
                        createInstruction(SET, var(11), "1"),
                        createInstruction(WRITE, var(10), "HEAPPTR", var(11)), // set $dy
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "set HEAPPTR = cell1\nallocate heap in HEAPPTR[0...4]\n$dx = 0\n\n$dy += $dx"
                        )
                )
        );
    }

    @Test
    void canIndirectlyReferenceStack() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "STACKPTR", "cell1"),
                        createInstruction(SET, "HEAPPTR", "cell2"),

                        createInstruction(SET, var(2), "63"), // init stack
                        createInstruction(SET, var(3), "63"),
                        createInstruction(WRITE, var(2), "STACKPTR", var(3)),

                        createInstruction(SET, var(7), "63"), // push return address on stack
                        createInstruction(READ, var(8), "STACKPTR", var(7)),
                        createInstruction(SET, var(6), var(8)),
                        createInstruction(SET, var(9), "1"),
                        createInstruction(OP, "sub", var(10), var(6), var(9)),
                        createInstruction(SET, var(6), var(10)),
                        createInstruction(WRITE, var(1001), "STACKPTR", var(6)),
                        createInstruction(SET, var(14), "63"),
                        createInstruction(WRITE, var(6), "STACKPTR", var(14)),

                        createInstruction(SET, "@counter", var(1000)), // invoke function

                        createInstruction(LABEL, var(1001)),

                        createInstruction(SET, var(17), "63"), // pop return value from stack
                        createInstruction(READ, var(18), "STACKPTR", var(17)),
                        createInstruction(SET, var(16), var(18)),
                        createInstruction(READ, var(19), "STACKPTR", var(16)),
                        createInstruction(SET, var(15), var(19)),
                        createInstruction(SET, var(20), "1"),
                        createInstruction(OP, "add", var(21), var(16), var(20)),
                        createInstruction(SET, var(16), var(21)),
                        createInstruction(SET, var(24), "63"),
                        createInstruction(WRITE, var(16), "STACKPTR", var(24)),

                        createInstruction(SET, var(25), "0"), // write $dx
                        createInstruction(WRITE, var(15), "HEAPPTR", var(25)),

                        createInstruction(END), // end of main function body

                        createInstruction(LABEL, var(1000)), // generateUnoptimized of delay function

                        createInstruction(SET, var(26), "0"), // return value

                        createInstruction(SET, var(29), "63"), // pop return address from stack
                        createInstruction(READ, var(30), "STACKPTR", var(29)),
                        createInstruction(SET, var(28), var(30)),
                        createInstruction(READ, var(31), "STACKPTR", var(28)),
                        createInstruction(SET, var(27), var(31)),
                        createInstruction(SET, var(32), "1"),
                        createInstruction(OP, "add", var(33), var(28), var(32)),
                        createInstruction(SET, var(28), var(33)),
                        createInstruction(SET, var(36), "63"),
                        createInstruction(WRITE, var(28), "STACKPTR", var(36)),

                        createInstruction(SET, var(38), "63"), // push return value on stack
                        createInstruction(READ, var(39), "STACKPTR", var(38)),
                        createInstruction(SET, var(37), var(39)),
                        createInstruction(SET, var(40), "1"),
                        createInstruction(OP, "sub", var(41), var(37), var(40)),
                        createInstruction(SET, var(37), var(41)),
                        createInstruction(WRITE, var(26), "STACKPTR", var(37)),
                        createInstruction(SET, var(45), "63"),
                        createInstruction(WRITE, var(37), "STACKPTR", var(45)),

                        createInstruction(SET, "@counter", var(27)), // jump back to caller
                        createInstruction(END)

                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "set STACKPTR = cell1\n" +
                                "set HEAPPTR = cell2\n" +
                                "allocate heap in HEAPPTR[0...16], stack in STACKPTR\n" +
                                "def delay\n" +
                                "0\n" +
                                "end\n" +
                                "\n" +
                                "$dx = delay()\n"
                        )
                )
        );
    }

    @Test
    void supportsTheSqrtFunction() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "2"),
                        createInstruction(OP, "sqrt", var(1), var(0)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "sqrt(2)"
                        )

                )
        );
    }

    @Test
    void supportsLogiclessCaseWhen() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "2"),
                        createInstruction(OP, "rand", var(1), var(0)),
                        createInstruction(OP, "floor", var(2), var(1)),
                        createInstruction(SET, var(3), var(2)),
                        createInstruction(SET, var(5), "0"),
                        createInstruction(JUMP, var(1001), "equal", var(3), var(5)),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(6), "1000"),
                        createInstruction(SET, var(4), var(6)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(7), "1"),
                        createInstruction(JUMP, var(1003), "equal", var(3), var(7)),
                        createInstruction(JUMP, var(1004), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(4), "null"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(4), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "case floor(rand(2))\nwhen 0\n  1000\nwhen 1\n  // no op\nend"
                        )

                )
        );
    }

    @Test
    void supportsMinMaxFunctions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "2"),
                        createInstruction(OP, "max", var(1), "y", var(0)),
                        createInstruction(OP, "min", var(2), "x", var(1)),
                        createInstruction(SET, "r", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "r = min(x, max(y, 2))"
                        )

                )
        );
    }

    @Test
    void supportsBitwiseAndOrXorAndShiftLeftOrRight() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "9842"),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(OP, "and", var(2), var(0), var(1)),
                        createInstruction(SET, var(3), "1"),
                        createInstruction(SET, var(4), "4"),
                        createInstruction(OP, "shl", var(5), var(3), var(4)),
                        createInstruction(OP, "xor", var(6), var(2), var(5)),
                        createInstruction(SET, var(7), "1"),
                        createInstruction(OP, "shr", var(8), "y", var(7)),
                        createInstruction(OP, "or", var(9), var(6), var(8)),
                        createInstruction(END)

                ),
                generateUnoptimized(
                        (Seq) translateToAst("(9842 & 1) ^ (1 << 4) | y >> 1\n")
                )
        );
    }

    @Test
    void allocateStackInNonStandardWayProducesCorrectCode() {
        // in this test, we're only concerned with whether or not the top of the stack is respected, and whether or
        // not the start of heap is respected. Everything else superfluous.
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "40"),
                        createInstruction(SET, var(1), "40"),
                        createInstruction(WRITE, var(0), "cell3", var(1)),
                        createInstruction(SET, var(2), "99"),
                        createInstruction(SET, var(3), "41"),
                        createInstruction(WRITE, var(2), "cell3", var(3))
                ),
                generateUnoptimized(
                        (Seq) translateToAst("allocate stack in cell3[0..40], heap in cell3[41...64]\ndef foo(n)\n2*n\nend\n\n$x = 99\nprint(foo(1) + foo(2))\n")
                ).subList(0, 6)
        );
    }

    @Test
    void generatesVectorLengthAndAngleCalls() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "4"),
                        createInstruction(SET, var(1), "8"),
                        createInstruction(OP, "len", var(2), var(0), var(1)),
                        createInstruction(SET, "length", var(2)),
                        createInstruction(SET, var(3), "4"),
                        createInstruction(SET, var(4), "8"),
                        createInstruction(OP, "angle", var(5), var(3), var(4)),
                        createInstruction(SET, "angle", var(5)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("length = len(4, 8)\nangle = angle(4, 8)\n")
                )
        );
    }

    @Test
    void generatesLog10Call() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "405"),
                        createInstruction(OP, "log10", var(1), var(0)),
                        createInstruction(SET, "l", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("l = log10(405)\n")
                )
        );
    }

    @Test
    void generatesNoiseCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "4"),
                        createInstruction(SET, var(1), "8"),
                        createInstruction(OP, "noise", var(2), var(0), var(1)),
                        createInstruction(SET, "n", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("n = noise(4, 8)\n")
                )
        );
    }

    @Test
    void generatesIntegerDivision() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "4"),
                        createInstruction(SET, var(1), "8"),
                        createInstruction(OP, "idiv", var(2), var(0), var(1)),
                        createInstruction(SET, "n", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("n = 4 \\ 8\n")
                )
        );
    }

    @Test
    void generatesCorrectCodeWhenCaseBranchIsCommentedOut() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__ast0", "n"),
                        createInstruction(SET, var(1), "2"),
                        createInstruction(JUMP, var(1001), "equal", "__ast0", var(1)),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "n"),
                        createInstruction(SET, var(0), "n"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "case n\n" +
                                "// when 1\n" +
                                "when 2\n" +
                                "print(n)\n" +
                                "end\n")
                )
        );
    }

    @Test
    void generatesCorrectCodeWhenIfExpressionHasCommentedOutSections() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "equal", "n", "false"),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(SET, var(0), var(1)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1002), "equal", "m", "false"),
                        createInstruction(SET, var(3), "1"),
                        createInstruction(SET, var(2), var(3)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(2), "null"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "if n\n" +
                                "// no op\n" +
                                "else\n" +
                                "1\n" +
                                "end\n" +
                                "if m\n" +
                                "1\n" +
                                "else\n" +
                                "// 2\n" +
                                "end\n")
                )
        );
    }

    @Test
    void correctlyParsesIndirectPropertyReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "resource", "@silicon"),
                        createInstruction(SENSOR, var(0), "vault1", "resource"),
                        createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        createInstruction(JUMP, var(1000), "equal", var(2), "false"),
                        createInstruction(SET, "foo", "true"),
                        createInstruction(SET, var(3), "true"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(3), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "" +
                                        "resource = @silicon\n" +
                                        "if vault1.sensor(resource) < vault1.itemCapacity\n" +
                                        "  foo = true\n" +
                                        "end\n"
                        )
                )
        );
    }

    @Test
    void correctlyParsesDirectIndirectPropertyReference() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "vault1", "@graphite"),
                        createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        createInstruction(CONTROL, "enabled", "conveyor1", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "conveyor1.enabled = vault1.sensor(@graphite) < vault1.sensor(@itemCapacity)"
                        )
                )
        );
    }

    @Test
    void correctlyGeneratesTernaryOperatorLogic() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "\"\\nsm.enabled: \""),
                        createInstruction(SENSOR, var(1), "smelter1", "@enabled"),
                        createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        createInstruction(SET, var(3), "\"true\""),
                        createInstruction(SET, var(2), var(3)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(4), "\"false\""),
                        createInstruction(SET, var(2), var(4)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, var(0)),
                        createInstruction(PRINT, var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "print(\"\\nsm.enabled: \", smelter1.enabled ? \"true\" : \"false\")"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesNestedTernaryOperators() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "greaterThan", var(0), "b", "c"),
                        createInstruction(JUMP, var(1005), "equal", var(0), "false"),
                        createInstruction(SET, var(2), "1"),
                        createInstruction(SET, var(1), var(2)),
                        createInstruction(JUMP, var(1013), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(OP, "greaterThan", var(3), "d", "e"),
                        createInstruction(JUMP, var(1010), "equal", var(3), "false"),
                        createInstruction(SET, var(5), "2"),
                        createInstruction(SET, var(4), var(5)),
                        createInstruction(JUMP, var(1012), "always"),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(SET, var(6), "3"),
                        createInstruction(SET, var(4), var(6)),
                        createInstruction(LABEL, var(1012)),
                        createInstruction(SET, var(1), var(4)),
                        createInstruction(LABEL, var(1013)),
                        createInstruction(SET, "a", var(1)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "a = (b > c) ? 1 : (d > e) ? 2 : 3"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesUnaryLiteralMinusExpressions() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(SET, "a", "-7"),
                        createInstruction(OP, "mul", "b", "-1", "a"),
                        createInstruction(OP, "mul", "c", "-5", "b"),
                        createInstruction(OP, "mul", var(0), "-1", "c"),
                        createInstruction(OP, "mul", "d", "2", var(0)),
                        createInstruction(OP, "pow", var(1), "d", "2"),
                        createInstruction(OP, "mul", "e", "-1", var(1)),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "a = -7\n" +
                                "b = -a\n" +
                                "c = -5 * b\n" +
                                "d = 2 * -c\n" +
                                "e = -d ** 2"
                        ),
                        new CompilerProfile(Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.OUTPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesTernaryOpPriority() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(OP, "greaterThan", var(0), "b", "c"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(SET, var(1), "b"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "c"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "a", var(1)),
                        createInstruction(OP, "mul", var(2), "e", "f"),
                        createInstruction(SET, "e", var(2)),
                        createInstruction(OP, "mul", var(3), "-1", var(2)),
                        createInstruction(OP, "add", "d", "d", var(3)),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "a = b > c ? b : c\n" +
                                "d += -e *= f"
                        ),
                        new CompilerProfile(Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.OUTPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesBitwiseOpPriority() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(OP, "and", var(0), "c", "d"),
                        createInstruction(OP, "or", "a", "b", var(0)),
                        createInstruction(OP, "not", var(1), "f"),
                        createInstruction(OP, "and", "e", var(1), "g"),
                        createInstruction(OP, "and", var(2), "h", "31"),
                        createInstruction(OP, "equal", "g", var(2), "15"),
                        createInstruction(OP, "and", var(3), "y", "15"),
                        createInstruction(OP, "and", var(4), "z", "7"),
                        createInstruction(OP, "land", "x", var(3), var(4)),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "a = b | c & d\n" +
                                "e = ~f & g\n" +
                                "g = h & 31 == 15\n" +
                                "x = y & 15 and z & 7"
                        ),
                        new CompilerProfile(Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.OUTPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesStrictNotEqual() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "@unit", "@dead"),
                        createInstruction(OP, "strictEqual", var(1), var(0), "null"),
                        createInstruction(OP, "equal", var(2), var(1), "false"),
                        createInstruction(SET, "a", var(2)),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(
                                "a = @unit.dead !== null\nprint(a)"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesBreakContinue() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1002), "equal", "a", "false"),
                        createInstruction(JUMP, var(1003), "equal", "b", "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(JUMP, var(1004), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1005), "equal", "c", "false"),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(JUMP, var(1006), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "\"End\""),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "while a\n" +
                                "  if b\n" +
                                "    continue\n" +
                                "  elsif c\n" +
                                "    break\n" +
                                "  end\n" +
                                "end\n" +
                                "print(\"End\")"
                        ),
                        new CompilerProfile(Optimisation.DEAD_CODE_ELIMINATION, Optimisation.INPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesNestedLoopBreaks() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(LABEL, var(0)),
                        createInstruction(JUMP, var(1), "equal", "a", "false"),
                        createInstruction(PRINT, "a"),
                        createInstruction(LABEL, var(2)),
                        createInstruction(JUMP, var(3), "equal", "b", "false"),
                        createInstruction(PRINT, "b"),
                        createInstruction(JUMP, var(4), "equal", "c", "false"),
                        createInstruction(PRINT, "c"),
                        createInstruction(JUMP, var(3), "always"),
                        createInstruction(JUMP, var(5), "always"),
                        createInstruction(LABEL, var(4)),
                        createInstruction(LABEL, var(5)),
                        createInstruction(PRINT, "d"),
                        createInstruction(JUMP, var(3), "always"),
                        createInstruction(LABEL, var(10)),
                        createInstruction(JUMP, var(2), "always"),
                        createInstruction(LABEL, var(3)),
                        createInstruction(PRINT, "e"),
                        createInstruction(JUMP, var(1), "always"),
                        createInstruction(LABEL, var(11)),
                        createInstruction(JUMP, var(0), "always"),
                        createInstruction(LABEL, var(1)),
                        createInstruction(PRINT, "f"),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "while a\n" +
                                "  print(a)\n" +
                                "  while b\n" +
                                "    print(b)\n" +
                                "    if c\n" +
                                "      print(c)\n" +
                                "      break\n" +
                                "    end\n" +
                                "    print(d)\n" +
                                "    break\n" +
                                "  end\n" +
                                "  print(e)\n" +
                                "  break\n" +
                                "end\n" +
                                "print(f)"
                        ),
                        new CompilerProfile(Optimisation.DEAD_CODE_ELIMINATION, Optimisation.INPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void refusesBreaksOutsideLoop() {
        assertThrows(GenerationException.class, () ->
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "while a\n  print(a)\nend\nbreak"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesBreakAndContinueWithLabel() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1002), "equal", "a", "false"),
                        createInstruction(PRINT, "\"In outer\""),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1005), "equal", "b", "false"),
                        createInstruction(PRINT, "\"In inner\""),
                        createInstruction(JUMP, var(1002), "always"),        // break loop Outer
                        createInstruction(PRINT, "\"After break\""),
                        createInstruction(JUMP, var(1004), "always"),        // continue loop Inner
                        createInstruction(LABEL, var(1004)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(PRINT, "\"After inner\""),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "\"After outer\""),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "Outer:\n" +
                                "while a\n" +
                                "  print(\"In outer\")\n" +
                                "  Inner:\n" +
                                "  while b\n" +
                                "    print(\"In inner\")\n" +
                                "    break Outer\n" +
                                "    print(\"After break\")\n" +
                                "    continue Inner\n" +
                                "  end\n" +
                                "  print(\"After inner\")\n" +
                                "end\n" +
                                "print(\"After outer\")"
                        ),
                        new CompilerProfile(Optimisation.DEAD_CODE_ELIMINATION, Optimisation.INPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void generatesDoWhileLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "\"Blocks:\""),
                        createInstruction(PRINT, var(0)),
                        createInstruction(SET, "n", "@links"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(OP, "sub", var(2), "n", var(1)),
                        createInstruction(SET, "n", var(2)),
                        createInstruction(GETLINK, var(3), "n"),
                        createInstruction(SET, "block", var(3)),
                        createInstruction(SET, var(4), "\"\\n\""),
                        createInstruction(PRINT, var(4)),
                        createInstruction(PRINT, "block"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(5), "0"),
                        createInstruction(OP, "greaterThan", var(6), "n", var(5)),
                        createInstruction(JUMP, var(1000), "notEqual", var(6), "false"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("" +
                                "print(\"Blocks:\")\n" +
                                "n = @links\n" +
                                "do\n" +
                                "  n -= 1\n" +
                                "  block = getlink(n)\n" +
                                "  print(\"\\n\", block)\n" +
                                "loop while n > 0\n" +
                                "printflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void generatesDoWhileLoopWithBreakAndContinue() {
        assertLogicInstructionsMatch(List.of(
                        createInstruction(PRINT, "\"Blocks:\""),
                        createInstruction(SET, "n", "@links"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(2), "n", "1"),
                        createInstruction(SET, "n", var(2)),
                        createInstruction(GETLINK, var(3), "n"),
                        createInstruction(SET, "block", var(3)),
                        createInstruction(SENSOR, var(4), "block", "@type"),
                        createInstruction(JUMP, var(1001), "equal", var(4), "@sorter"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(PRINT, "block"),
                        createInstruction(SENSOR, var(8), "block", "@type"),
                        createInstruction(JUMP, var(1002), "equal", var(8), "@unloader"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "greaterThan", "n", "0"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst("" +
                                "print(\"Blocks:\")\n" +
                                "n = @links\n" +
                                "MainLoop:\n" +
                                "do\n" +
                                "  n = n - 1\n" +
                                "  block = getlink(n)\n" +
                                "  if block.type == @sorter\n" +
                                "    continue MainLoop\n" +
                                "  end\n" +
                                "  print(\"\\n\", block)\n" +
                                "  if block.type == @unloader\n" +
                                "    break MainLoop\n" +
                                "  end\n" +
                                "loop while n > 0\n" +
                                "printflush(message1)"
                        ),
                        new CompilerProfile(Optimisation.DEAD_CODE_ELIMINATION, Optimisation.SINGLE_STEP_JUMP_ELIMINATION,
                                Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.JUMP_OVER_JUMP_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void convertsForEachAndPrintFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), var(1003)),
                        createInstruction(SET, "a", "@mono"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(0), var(1004)),
                        createInstruction(SET, "a", "@poly"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(0), var(1002)),
                        createInstruction(SET, "a", "@mega"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "a"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "@counter", var(0)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                generateUnoptimized((Seq) translateToAst("for a in (@mono, @poly, @mega)\nprint(a)\nend"))
        );
    }
}
