package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.mindustry.Opcode.*;

import info.teksol.mindcode.mindustry.optimisation.Optimisation;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void convertsComplexAssignment() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "2"),
                        new LogicInstruction(OP, "sub", var(1), "bar", var(0)),
                        new LogicInstruction(SET, var(2), "3"),
                        new LogicInstruction(OP, "mul", var(3), var(1), var(2)),
                        new LogicInstruction(SET, "foo", var(3)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(SET, "n", var(0)),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "5"),
                        new LogicInstruction(OP, "lessThan", var(2), "n", var(1)),
                        new LogicInstruction(JUMP, var(1001), "equal", var(2), "false"),
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(OP, "add", var(4), "n", var(3)),
                        new LogicInstruction(SET, "n", var(4)),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, var(5), "\"n: \""),
                        new LogicInstruction(PRINT, var(5)),
                        new LogicInstruction(PRINT, "n"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("n = 0\nwhile n < 5\nn += 1\nend\nprint(\"n: \", n)"))
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "not", var(0), "a"),
                        new LogicInstruction(SET, "a", var(0)),
                        new LogicInstruction(OP, "equal", var(1), "b", "false"),
                        new LogicInstruction(SET, "b", var(1)),
                        new LogicInstruction(OP, "equal", var(2), "c", "false"),
                        new LogicInstruction(SET, "c", var(2)),
                        new LogicInstruction(SET, "x", "null"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("a = ~a\nb = !b\nc = not c\nx = null"))
        );
    }

    @Test
    void convertsSensorReadings() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, var(0), "foundation1", "@copper"),
                        new LogicInstruction(SENSOR, var(1), "foundation1", "@itemCapacity"),
                        new LogicInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(OP, "notEqual", var(0), "true", "false"),
                        new LogicInstruction(JUMP, var(1001), "equal", var(0), "false"),
                        new LogicInstruction(SET, var(1), "\"infinite loop!\""),
                        new LogicInstruction(PRINT, var(1)),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINTFLUSH, "message1"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("while true != false\nprint(\"infinite loop!\")\nend\nprintflush(message1)\n")
                )
        );
    }

    @Test
    void convertsControlStatements() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, var(1), "foundation1", "@copper"),
                        new LogicInstruction(SENSOR, var(2), "tank1", "@water"),
                        new LogicInstruction(OP, "strictEqual", var(3), var(1), var(2)),
                        new LogicInstruction(CONTROL, "enabled", "conveyor1", var(3)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("conveyor1.enabled = foundation1.copper === tank1.water")
                )
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(2), "4"),
                        new LogicInstruction(READ, var(3), "cell2", var(2)),
                        new LogicInstruction(SENSOR, var(4), "conveyor1", "@enabled"),
                        new LogicInstruction(OP, "add", var(5), var(3), var(4)),
                        new LogicInstruction(SET, var(6), "3"),
                        new LogicInstruction(WRITE, var(5), "cell1", var(6)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("cell1[3] = cell2[4] + conveyor1.enabled")
                )
        );
    }

    @Test
    void convertsIfExpression() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "4"),
                        new LogicInstruction(READ, var(1), "cell1", var(0)),
                        new LogicInstruction(SET, var(2), "0"),
                        new LogicInstruction(OP, "equal", var(3), var(1), var(2)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(3), "false"),
                        new LogicInstruction(SET, var(9), "false"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(6), "4"),
                        new LogicInstruction(WRITE, "true", "cell1", var(6)),
                        new LogicInstruction(SET, var(7), "1"),
                        new LogicInstruction(OP, "add", var(8), "n", var(7)),
                        new LogicInstruction(SET, "n", var(8)),
                        new LogicInstruction(SET, var(9), var(8)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, "value", var(9)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(2), "9"),
                        new LogicInstruction(SET, var(3), "9"),
                        new LogicInstruction(OP, "pow", var(4), var(2), var(3)),
                        new LogicInstruction(OP, "rand", var(5), var(4)),
                        new LogicInstruction(SET, var(6), "0"),
                        new LogicInstruction(WRITE, var(5), "cell1", var(6)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("cell1[0] = rand(9**9)"))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(OP, "strictEqual", var(0), "@unit", "null"),
                        new LogicInstruction(JUMP, var(1001), "equal", var(0), "false"),
                        new LogicInstruction(UBIND, "@poly"),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst(
                                "while @unit === null\nubind(@poly)\nend\n")
                )
        );

    }

    @Test
    void convertsReallifeTest1() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(SET, "n", var(0)),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(GETLINK, var(1), "n"),
                        new LogicInstruction(SET, "reactor", var(1)),
                        new LogicInstruction(OP, "notEqual", var(2), var(1), "null"),
                        new LogicInstruction(JUMP, var(1003), "equal", var(2), "false"),
                        new LogicInstruction(SENSOR, var(3), "reactor", "@liquidCapacity"),
                        new LogicInstruction(SET, var(4), "0"),
                        new LogicInstruction(OP, "greaterThan", var(5), var(3), var(4)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(5), "false"),
                        new LogicInstruction(SENSOR, var(6), "reactor", "@cryofluid"),
                        new LogicInstruction(SENSOR, var(7), "reactor", "@liquidCapacity"),
                        new LogicInstruction(OP, "div", var(8), var(6), var(7)),
                        new LogicInstruction(SET, "pct_avail", var(8)),
                        new LogicInstruction(SET, var(10), "0.25"),
                        new LogicInstruction(OP, "greaterThanEq", var(11), "pct_avail", var(10)),
                        new LogicInstruction(CONTROL, "enabled", "reactor", var(11)),
                        new LogicInstruction(SET, var(12), var(11)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(12), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, var(13), "1"),
                        new LogicInstruction(OP, "add", var(14), "n", var(13)),
                        new LogicInstruction(SET, "n", var(14)),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(JUMP, var(1002), "always"),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "-1"),
                        new LogicInstruction(OP, "mul", var(1), "dx", var(0)),
                        new LogicInstruction(SET, "dx", var(1)),
                        new LogicInstruction(SET, var(2), "-1"),
                        new LogicInstruction(SET, "dy", var(2)),
                        new LogicInstruction(SET, var(3), "2"),
                        new LogicInstruction(SET, var(4), "1"),
                        new LogicInstruction(OP, "sub", var(5), var(3), var(4)),
                        new LogicInstruction(SET, "dz", var(5)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(SET, "a", var(0)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(SET, var(1), "0"),
                        new LogicInstruction(UCONTROL, "build", "x", "y", "@titanium-conveyor", var(0), var(1)),
                        new LogicInstruction(UCONTROL, "getBlock", "x", "y", "b_type", "b_building"),
                        new LogicInstruction(OP, "equal", var(2), "b_type", "@titanium-conveyor"),
                        new LogicInstruction(JUMP, var(1000), "equal", var(2), "false"),
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(OP, "add", var(4), "n", var(3)),
                        new LogicInstruction(SET, "n", var(4)),
                        new LogicInstruction(SET, var(5), var(4)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(5), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst(
                                "build(x, y, @titanium-conveyor, 1, 0)\ngetBlock(x, y, b_type, b_building)\nif b_type == @titanium-conveyor\nn += 1\nend\n"
                        )
                )
        );
    }

    @Test
    void generatesComplexMathExpression() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(OP, "rand", var(1), var(0)),
                        new LogicInstruction(OP, "tan", var(2), var(1)),
                        new LogicInstruction(OP, "abs", var(3), var(2)),
                        new LogicInstruction(OP, "cos", var(4), var(3)),
                        new LogicInstruction(OP, "log", var(5), var(4)),
                        new LogicInstruction(OP, "sin", var(6), var(5)),
                        new LogicInstruction(OP, "floor", var(7), var(6)),
                        new LogicInstruction(OP, "ceil", var(8), var(7)),
                        new LogicInstruction(SET, "x", var(8)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
                )
        );
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(SET, "n", var(0)),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "17"),
                        new LogicInstruction(OP, "lessThanEq", var(2), "n", var(1)),
                        new LogicInstruction(JUMP, var(1001), "equal", var(2), "false"),
                        new LogicInstruction(PRINT, "n"),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(OP, "add", var(4), "n", var(3)),
                        new LogicInstruction(SET, "n", var(4)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("for n in 1 .. 17\nprint(n)\nend")
                )
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        // init
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(SET, "n", var(0)),

                        // cond
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "17"),
                        new LogicInstruction(OP, "lessThan", var(2), "n", var(1)),
                        new LogicInstruction(JUMP, var(1001), "equal", var(2), "false"),

                        // loop body
                        new LogicInstruction(PRINT, "n"),

                        // continue label
                        new LogicInstruction(LABEL, var(1010)),

                        // increment
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(OP, "add", var(4), "n", var(3)),
                        new LogicInstruction(SET, "n", var(4)),

                        // loop
                        new LogicInstruction(JUMP, var(1000), "always"),

                        // trailer
                        new LogicInstruction(LABEL, var(1001)),

                        // rest of program
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("for n in 1 ... 17\nprint(n)\nend\n")
                )
        );
    }

    @Test
    void generatesCStyleComplexForLoop() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(SET, "i", var(0)),
                        new LogicInstruction(SET, var(1), "-5"),
                        new LogicInstruction(SET, "j", var(1)),

                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(2), "5"),
                        new LogicInstruction(OP, "lessThan", var(3), "i", var(2)),
                        new LogicInstruction(JUMP, var(1001), "equal", var(3), "false"),
                        new LogicInstruction(PRINT, "n"),

                        new LogicInstruction(LABEL, var(1010)),

                        new LogicInstruction(SET, var(4), "1"),
                        new LogicInstruction(OP, "sub", var(5), "j", var(4)),
                        new LogicInstruction(SET, "j", var(5)),

                        new LogicInstruction(SET, var(6), "1"),
                        new LogicInstruction(OP, "add", var(7), "i", var(6)),
                        new LogicInstruction(SET, "i", var(7)),

                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
                )
        );
    }

    @Test
    void supportsAssigningAssignmentResults() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "42"),
                        new LogicInstruction(SET, "b", var(0)),
                        new LogicInstruction(SET, "a", var(0)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("a = b = 42")
                )
        );
    }

    @Test
    void generatesCaseWhenElse() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "__ast0", "n"),
                        new LogicInstruction(SET, var(2), "1"),
                        new LogicInstruction(JUMP, var(1001), "notEqual", var(0), var(2)),
                        new LogicInstruction(SET, var(3), "\"1\""),
                        new LogicInstruction(SET, var(1), var(3)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, var(4), "2"),
                        new LogicInstruction(JUMP, var(1002), "notEqual", var(0), var(4)),
                        new LogicInstruction(SET, var(5), "\"two\""),
                        new LogicInstruction(SET, var(1), var(5)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(6), "\"otherwise\""),
                        new LogicInstruction(SET, var(1), var(6)),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("case n\nwhen 1\n\"1\"\nwhen 2\n\"two\"\nelse\n\"otherwise\"end\n")
                )
        );
    }

    @Test
    void generatesCaseWhen() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(READ, var(1), "cell1", var(0)),
                        new LogicInstruction(SET, "__ast0", var(1)),
                        new LogicInstruction(JUMP, var(1001), "notEqual", var(2), "ST_EMPTY"),
                        new LogicInstruction(SET, var(6), "0"),
                        new LogicInstruction(WRITE, "ST_INITIALIZED", "cell1", var(6)),
                        new LogicInstruction(SET, var(3), "ST_INITIALIZED"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(JUMP, var(1002), "notEqual", var(2), "ST_INITIALIZED"),
                        new LogicInstruction(SET, var(9), "0"),
                        new LogicInstruction(WRITE, "ST_DONE", "cell1", var(9)),
                        new LogicInstruction(SET, var(3), "ST_DONE"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(3), "null"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("allocate heap in cell1[0..10]\ncase $state\nwhen ST_EMPTY\n$state = ST_INITIALIZED\nwhen ST_INITIALIZED\n$state = ST_DONE\nend\n")
                )
        );
    }

    @Test
    void generatesDrawings() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(DRAW, "clear", "r", "g", "b"),
                        new LogicInstruction(DRAW, "color", "r", "g", "b", "alpha"),
                        new LogicInstruction(DRAW, "stroke", "width"),
                        new LogicInstruction(DRAW, "line", "x1", "y1", "x2", "y2"),
                        new LogicInstruction(DRAW, "rect", "x", "y", "w", "h"),
                        new LogicInstruction(DRAW, "lineRect", "x", "y", "w", "h"),
                        new LogicInstruction(DRAW, "poly", "x", "y", "sides", "radius", "rotation"),
                        new LogicInstruction(DRAW, "linePoly", "x", "y", "sides", "radius", "rotation"),
                        new LogicInstruction(DRAW, "triangle", "x1", "y1", "x2", "y2", "x3", "y3"),
                        new LogicInstruction(DRAW, "image", "x", "y", "@copper", "size", "rotation"),
                        new LogicInstruction(DRAWFLUSH, "display1"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("clear(r, g, b)\ncolor(r, g, b, alpha)\nstroke(width)\nline(x1, y1, x2, y2)\nrect(x, y, w, h)\nlineRect(x, y, w, h)\npoly(x, y, sides, radius, rotation)\nlinePoly(x, y, sides, radius, rotation)\ntriangle(x1, y1, x2, y2, x3, y3)\nimage(x, y, @copper, size, rotation)\ndrawflush(display1)\n")
                )
        );
    }

    @Test
    void generatesURadar() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(URADAR, "enemy", "ground", "any", "health", "0", "MIN_TO_MAX", var(0)),
                        new LogicInstruction(SET, "target", var(0)),
                        new LogicInstruction(OP, "notEqual", var(1), "target", "null"),
                        new LogicInstruction(JUMP, var(1002), "equal", var(1), "false"),
                        new LogicInstruction(SENSOR, var(2), "target", "@x"),
                        new LogicInstruction(SENSOR, var(3), "target", "@y"),
                        new LogicInstruction(SET, var(4), "10"),
                        new LogicInstruction(UCONTROL, "approach", var(2), var(3), var(4)),
                        new LogicInstruction(SENSOR, var(5), "target", "@x"),
                        new LogicInstruction(SENSOR, var(6), "target", "@y"),
                        new LogicInstruction(SET, var(7), "10"),
                        new LogicInstruction(UCONTROL, "within", var(5), var(6), var(7), var(8)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(8), "false"),
                        new LogicInstruction(SENSOR, var(9), "target", "@x"),
                        new LogicInstruction(SENSOR, var(10), "target", "@y"),
                        new LogicInstruction(UCONTROL, "target", var(9), var(10), "SHOOT"),
                        new LogicInstruction(SET, var(11), "null"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(11), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, var(12), var(11)),
                        new LogicInstruction(JUMP, var(1003), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(12), "null"),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        new LogicInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        new LogicInstruction(ULOCATE, "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        new LogicInstruction(ULOCATE, "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", var(0), var(1)),
                        new LogicInstruction(SET, "out", var(1)),
                        new LogicInstruction(SET, var(2), "1"),
                        new LogicInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", var(2), var(3)),
                        new LogicInstruction(SET, "out", var(3)),
                        new LogicInstruction(SET, "src", "salvo1"),
                        new LogicInstruction(SET, var(4), "1"),
                        new LogicInstruction(RADAR, "enemy", "any", "any", "distance", "src", var(4), var(5)),
                        new LogicInstruction(SET, "out", var(5)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("" +
                                "out = radar(enemy, any, any, distance, salvo1, 1)\n" +
                                "out = radar(ally, flying, any, health, lancer1, 1)\n" +
                                "src = salvo1\n" +
                                "out = radar(enemy, any, any, distance, src, 1, out)\n"
                        )
                )
        );  
    }

    @Test
    void generatesWait() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(WAIT, var(0)),
                        new LogicInstruction(SET, var(1), "0.001"),
                        new LogicInstruction(WAIT, var(1)),
                        new LogicInstruction(SET, var(2), "1000"),
                        new LogicInstruction(WAIT, var(2)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("wait(1)\nwait(0.001)\nwait(1000)")
                )
        );  
    }

    @Test
    void generatesEndFromFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "equal", var(0), "some_cond", "false"),
                        new LogicInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        new LogicInstruction(END),
                        new LogicInstruction(SET, var(1), "null"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "2"),
                        new LogicInstruction(OP, "mod", var(1), "@tick", var(0)),
                        new LogicInstruction(SET, var(2), "0"),
                        new LogicInstruction(OP, "equal", var(3), var(1), var(2)),
                        new LogicInstruction(SET, "running", var(3)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("" +
                                "running = @tick % 2 == 0"
                        )
                )
        );
    }

    @Test
    void refusesToDeclareFunctionsWhenNoStackAround() {
        assertThrows(MissingStackException.class, () ->
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(2), "63"),
                        new LogicInstruction(SET, var(3), "63"),
                        new LogicInstruction(WRITE, var(2), "cell1", var(3)),

                        // push return address on stack
                        new LogicInstruction(SET, var(5), "63"),
                        new LogicInstruction(READ, var(6), "cell1", var(5)), // read stack pointer
                        new LogicInstruction(SET, var(4), var(6)),
                        new LogicInstruction(SET, var(7), "1"),
                        new LogicInstruction(OP, "sub", var(8), var(4), var(7)), // calculate new stack pointer
                        new LogicInstruction(SET, var(4), var(8)),
                        new LogicInstruction(WRITE, var(1001), "cell1", var(4)), // write value on stack, at stack pointer
                        new LogicInstruction(SET, var(12), "63"),
                        new LogicInstruction(WRITE, var(4), "cell1", var(12)), // update stack pointer itself

                        // jump to function
                        new LogicInstruction(SET, "@counter", var(1000)),

                        // return label
                        new LogicInstruction(LABEL, var(1001)),

                        // pop return value from stack
                        new LogicInstruction(SET, var(15), "63"),
                        new LogicInstruction(READ, var(16), "cell1", var(15)), // read stack pointer
                        new LogicInstruction(SET, var(14), var(16)),
                        new LogicInstruction(READ, var(17), "cell1", var(14)), // read value on stack, at stack pointer
                        new LogicInstruction(SET, var(13), var(17)),
                        new LogicInstruction(SET, var(18), "1"),
                        new LogicInstruction(OP, "add", var(19), var(14), var(18)), // calculate new stack pointer
                        new LogicInstruction(SET, var(14), var(19)),
                        new LogicInstruction(SET, var(22), "63"),
                        new LogicInstruction(WRITE, var(14), "cell1", var(22)), // update stack pointer itself

                        // continue rest of main script
                        new LogicInstruction(SET, "x", var(13)),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(PRINTFLUSH, "message1"),

                        // end of main script
                        new LogicInstruction(END),

                        // generateUnoptimized of function foo
                        new LogicInstruction(LABEL, var(1000)),

                        // no parameters to pop
                        new LogicInstruction(SET, var(23), "0"),

                        // pop return address
                        new LogicInstruction(SET, var(26), "63"),
                        new LogicInstruction(READ, var(27), "cell1", var(26)),
                        new LogicInstruction(SET, var(25), var(27)),
                        new LogicInstruction(READ, var(28), "cell1", var(25)),
                        new LogicInstruction(SET, var(24), var(28)),
                        new LogicInstruction(SET, var(29), "1"),
                        new LogicInstruction(OP, "add", var(30), var(25), var(29)),
                        new LogicInstruction(SET, var(25), var(30)),
                        new LogicInstruction(SET, var(33), "63"),
                        new LogicInstruction(WRITE, var(25), "cell1", var(33)),

                        // push return value
                        new LogicInstruction(SET, var(35), "63"),
                        new LogicInstruction(READ, var(36), "cell1", var(35)),
                        new LogicInstruction(SET, var(34), var(36)),
                        new LogicInstruction(SET, var(37), "1"),
                        new LogicInstruction(OP, "sub", var(38), var(34), var(37)),
                        new LogicInstruction(SET, var(34), var(38)),
                        new LogicInstruction(WRITE, var(23), "cell1", var(34)),
                        new LogicInstruction(SET, var(42), "63"),
                        new LogicInstruction(WRITE, var(34), "cell1", var(42)),

                        // jump to return address
                        new LogicInstruction(SET, "@counter", var(24)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(2), "63"),
                        new LogicInstruction(SET, var(3), "63"),
                        new LogicInstruction(WRITE, var(2), "cell1", var(3)),

                        // push return address on stack
                        new LogicInstruction(SET, var(4), "4"),
                        new LogicInstruction(SET, "boo", var(4)),
                        new LogicInstruction(SET, var(5), "2"),

                        // push return address on stack
                        new LogicInstruction(SET, var(7), "63"),
                        new LogicInstruction(READ, var(8), "cell1", var(7)),
                        new LogicInstruction(SET, var(6), var(8)),
                        new LogicInstruction(SET, var(9), "1"),
                        new LogicInstruction(OP, "sub", var(10), var(6), var(9)),
                        new LogicInstruction(SET, var(6), var(10)),
                        new LogicInstruction(WRITE, var(1001), "cell1", var(6)),
                        new LogicInstruction(SET, var(14), "63"),
                        new LogicInstruction(WRITE, var(6), "cell1", var(14)),

                        // push n on stack
                        new LogicInstruction(SET, var(16), "63"),
                        new LogicInstruction(READ, var(17), "cell1", var(16)),
                        new LogicInstruction(SET, var(15), var(17)),
                        new LogicInstruction(SET, var(18), "1"),
                        new LogicInstruction(OP, "sub", var(19), var(15), var(18)),
                        new LogicInstruction(SET, var(15), var(19)),
                        new LogicInstruction(WRITE, var(5), "cell1", var(15)),
                        new LogicInstruction(SET, var(23), "63"),
                        new LogicInstruction(WRITE, var(15), "cell1", var(23)),

                        // push r on stack
                        new LogicInstruction(SET, var(25), "63"),
                        new LogicInstruction(READ, var(26), "cell1", var(25)),
                        new LogicInstruction(SET, var(24), var(26)),
                        new LogicInstruction(SET, var(27), "1"),
                        new LogicInstruction(OP, "sub", var(28), var(24), var(27)),
                        new LogicInstruction(SET, var(24), var(28)),
                        new LogicInstruction(WRITE, "boo", "cell1", var(24)),
                        new LogicInstruction(SET, var(32), "63"),
                        new LogicInstruction(WRITE, var(24), "cell1", var(32)),

                        // jump to function
                        new LogicInstruction(SET, "@counter", var(1000)),

                        // function return address
                        new LogicInstruction(LABEL, var(1001)),

                        // pop return value from stack
                        new LogicInstruction(SET, var(35), "63"),
                        new LogicInstruction(READ, var(36), "cell1", var(35)),
                        new LogicInstruction(SET, var(34), var(36)),
                        new LogicInstruction(READ, var(37), "cell1", var(34)),
                        new LogicInstruction(SET, var(33), var(37)),
                        new LogicInstruction(SET, var(38), "1"),
                        new LogicInstruction(OP, "add", var(39), var(34), var(38)),
                        new LogicInstruction(SET, var(34), var(39)),
                        new LogicInstruction(SET, var(42), "63"),
                        new LogicInstruction(WRITE, var(34), "cell1", var(42)),

                        // continue main body
                        new LogicInstruction(SET, "x", var(33)),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(PRINTFLUSH, "message1"),
                        new LogicInstruction(END),

                        // function foo
                        new LogicInstruction(LABEL, var(1000)),

                        // pop r
                        new LogicInstruction(SET, var(45), "63"),
                        new LogicInstruction(READ, var(46), "cell1", var(45)),
                        new LogicInstruction(SET, var(44), var(46)),
                        new LogicInstruction(READ, var(47), "cell1", var(44)),
                        new LogicInstruction(SET, var(43), var(47)),
                        new LogicInstruction(SET, var(48), "1"),
                        new LogicInstruction(OP, "add", var(49), var(44), var(48)),
                        new LogicInstruction(SET, var(44), var(49)),
                        new LogicInstruction(SET, var(52), "63"),
                        new LogicInstruction(WRITE, var(44), "cell1", var(52)),
                        new LogicInstruction(SET, "r", var(43)),

                        // pop n
                        new LogicInstruction(SET, var(55), "63"),
                        new LogicInstruction(READ, var(56), "cell1", var(55)),
                        new LogicInstruction(SET, var(54), var(56)),
                        new LogicInstruction(READ, var(57), "cell1", var(54)),
                        new LogicInstruction(SET, var(53), var(57)),
                        new LogicInstruction(SET, var(58), "1"),
                        new LogicInstruction(OP, "add", var(59), var(54), var(58)),
                        new LogicInstruction(SET, var(54), var(59)),
                        new LogicInstruction(SET, var(62), "63"),
                        new LogicInstruction(WRITE, var(54), "cell1", var(62)),
                        new LogicInstruction(SET, "n", var(53)),

                        // execute function body
                        new LogicInstruction(SET, var(63), "2"),
                        new LogicInstruction(OP, "pow", var(64), "n", "r"),
                        new LogicInstruction(OP, "mul", var(65), var(63), var(64)),

                        // pop return address from stack
                        new LogicInstruction(SET, var(68), "63"),
                        new LogicInstruction(READ, var(69), "cell1", var(68)),
                        new LogicInstruction(SET, var(67), var(69)),
                        new LogicInstruction(READ, var(70), "cell1", var(67)),
                        new LogicInstruction(SET, var(66), var(70)),
                        new LogicInstruction(SET, var(71), "1"),
                        new LogicInstruction(OP, "add", var(72), var(67), var(71)),
                        new LogicInstruction(SET, var(67), var(72)),
                        new LogicInstruction(SET, var(75), "63"),
                        new LogicInstruction(WRITE, var(67), "cell1", var(75)),

                        // push return value on stack
                        new LogicInstruction(SET, var(77), "63"),
                        new LogicInstruction(READ, var(78), "cell1", var(77)),
                        new LogicInstruction(SET, var(76), var(78)),
                        new LogicInstruction(SET, var(79), "1"),
                        new LogicInstruction(OP, "sub", var(80), var(76), var(79)),
                        new LogicInstruction(SET, var(76), var(80)),
                        new LogicInstruction(WRITE, var(65), "cell1", var(76)),
                        new LogicInstruction(SET, var(84), "63"),
                        new LogicInstruction(WRITE, var(76), "cell1", var(84)),

                        // jump back to caller
                        new LogicInstruction(SET, "@counter", var(66)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(2), "63"),
                        new LogicInstruction(SET, var(3), "63"),
                        new LogicInstruction(WRITE, var(2), "cell1", var(3)),

                        // prepare parameters
                        new LogicInstruction(SET, var(4), "8"),
                        new LogicInstruction(SET, "boo", var(4)),
                        new LogicInstruction(SET, var(5), "7"),

                        // push return address
                        new LogicInstruction(SET, var(7), "63"),
                        new LogicInstruction(READ, var(8), "cell1", var(7)),
                        new LogicInstruction(SET, var(6), var(8)),
                        new LogicInstruction(SET, var(9), "1"),
                        new LogicInstruction(OP, "sub", var(10), var(6), var(9)),
                        new LogicInstruction(SET, var(6), var(10)),
                        new LogicInstruction(WRITE, var(1002), "cell1", var(6)),
                        new LogicInstruction(SET, var(14), "63"),
                        new LogicInstruction(WRITE, var(6), "cell1", var(14)),

                        // push n
                        new LogicInstruction(SET, var(16), "63"),
                        new LogicInstruction(READ, var(17), "cell1", var(16)),
                        new LogicInstruction(SET, var(15), var(17)),
                        new LogicInstruction(SET, var(18), "1"),
                        new LogicInstruction(OP, "sub", var(19), var(15), var(18)),
                        new LogicInstruction(SET, var(15), var(19)),
                        new LogicInstruction(WRITE, var(5), "cell1", var(15)),
                        new LogicInstruction(SET, var(23), "63"),
                        new LogicInstruction(WRITE, var(15), "cell1", var(23)),

                        // push r
                        new LogicInstruction(SET, var(25), "63"),
                        new LogicInstruction(READ, var(26), "cell1", var(25)),
                        new LogicInstruction(SET, var(24), var(26)),
                        new LogicInstruction(SET, var(27), "1"),
                        new LogicInstruction(OP, "sub", var(28), var(24), var(27)),
                        new LogicInstruction(SET, var(24), var(28)),
                        new LogicInstruction(WRITE, "boo", "cell1", var(24)),
                        new LogicInstruction(SET, var(32), "63"),
                        new LogicInstruction(WRITE, var(24), "cell1", var(32)),

                        // jump to function
                        new LogicInstruction(SET, "@counter", var(1000)),

                        // return address
                        new LogicInstruction(LABEL, var(1002)),

                        // pop return value from stack
                        new LogicInstruction(SET, var(35), "63"),
                        new LogicInstruction(READ, var(36), "cell1", var(35)),
                        new LogicInstruction(SET, var(34), var(36)),
                        new LogicInstruction(READ, var(37), "cell1", var(34)),
                        new LogicInstruction(SET, var(33), var(37)),
                        new LogicInstruction(SET, var(38), "1"),
                        new LogicInstruction(OP, "add", var(39), var(34), var(38)),
                        new LogicInstruction(SET, var(34), var(39)),
                        new LogicInstruction(SET, var(42), "63"),
                        new LogicInstruction(WRITE, var(34), "cell1", var(42)),

                        // rest of main body
                        new LogicInstruction(SET, "x", var(33)),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(PRINTFLUSH, "message1"),
                        new LogicInstruction(END),

                        // def bar
                        new LogicInstruction(LABEL, var(1001)),

                        // pop bar.x
                        new LogicInstruction(SET, var(45), "63"),
                        new LogicInstruction(READ, var(46), "cell1", var(45)),
                        new LogicInstruction(SET, var(44), var(46)),
                        new LogicInstruction(READ, var(47), "cell1", var(44)),
                        new LogicInstruction(SET, var(43), var(47)),
                        new LogicInstruction(SET, var(48), "1"),
                        new LogicInstruction(OP, "add", var(49), var(44), var(48)),
                        new LogicInstruction(SET, var(44), var(49)),
                        new LogicInstruction(SET, var(52), "63"),
                        new LogicInstruction(WRITE, var(44), "cell1", var(52)),
                        new LogicInstruction(SET, "x", var(43)),

                        // function body
                        new LogicInstruction(SET, var(53), "2"),
                        new LogicInstruction(OP, "mul", var(54), var(53), "x"),

                        // pop return address
                        new LogicInstruction(SET, var(57), "63"),
                        new LogicInstruction(READ, var(58), "cell1", var(57)),
                        new LogicInstruction(SET, var(56), var(58)),
                        new LogicInstruction(READ, var(59), "cell1", var(56)),
                        new LogicInstruction(SET, var(55), var(59)),
                        new LogicInstruction(SET, var(60), "1"),
                        new LogicInstruction(OP, "add", var(61), var(56), var(60)),
                        new LogicInstruction(SET, var(56), var(61)),
                        new LogicInstruction(SET, var(64), "63"),
                        new LogicInstruction(WRITE, var(56), "cell1", var(64)),

                        // push return value
                        new LogicInstruction(SET, var(66), "63"),
                        new LogicInstruction(READ, var(67), "cell1", var(66)),
                        new LogicInstruction(SET, var(65), var(67)),
                        new LogicInstruction(SET, var(68), "1"),
                        new LogicInstruction(OP, "sub", var(69), var(65), var(68)),
                        new LogicInstruction(SET, var(65), var(69)),
                        new LogicInstruction(WRITE, var(54), "cell1", var(65)),
                        new LogicInstruction(SET, var(73), "63"),
                        new LogicInstruction(WRITE, var(65), "cell1", var(73)),

                        // return
                        new LogicInstruction(SET, "@counter", var(55)),
                        new LogicInstruction(END),

                        // def foo
                        new LogicInstruction(LABEL, var(1000)),

                        // pop foo.r
                        new LogicInstruction(SET, var(76), "63"),
                        new LogicInstruction(READ, var(77), "cell1", var(76)),
                        new LogicInstruction(SET, var(75), var(77)),
                        new LogicInstruction(READ, var(78), "cell1", var(75)),
                        new LogicInstruction(SET, var(74), var(78)),
                        new LogicInstruction(SET, var(79), "1"),
                        new LogicInstruction(OP, "add", var(80), var(75), var(79)),
                        new LogicInstruction(SET, var(75), var(80)),
                        new LogicInstruction(SET, var(83), "63"),
                        new LogicInstruction(WRITE, var(75), "cell1", var(83)),
                        new LogicInstruction(SET, "r", var(74)),

                        // pop foo.n
                        new LogicInstruction(SET, var(86), "63"),
                        new LogicInstruction(READ, var(87), "cell1", var(86)),
                        new LogicInstruction(SET, var(85), var(87)),
                        new LogicInstruction(READ, var(88), "cell1", var(85)),
                        new LogicInstruction(SET, var(84), var(88)),
                        new LogicInstruction(SET, var(89), "1"),
                        new LogicInstruction(OP, "add", var(90), var(85), var(89)),
                        new LogicInstruction(SET, var(85), var(90)),
                        new LogicInstruction(SET, var(93), "63"),
                        new LogicInstruction(WRITE, var(85), "cell1", var(93)),
                        new LogicInstruction(SET, "n", var(84)),

                        // function body
                        new LogicInstruction(SET, var(94), "2"),

                        // push return address
                        new LogicInstruction(SET, var(96), "63"),
                        new LogicInstruction(READ, var(97), "cell1", var(96)),
                        new LogicInstruction(SET, var(95), var(97)),
                        new LogicInstruction(SET, var(98), "1"),
                        new LogicInstruction(OP, "sub", var(99), var(95), var(98)),
                        new LogicInstruction(SET, var(95), var(99)),
                        new LogicInstruction(WRITE, var(1003), "cell1", var(95)),
                        new LogicInstruction(SET, var(103), "63"),
                        new LogicInstruction(WRITE, var(95), "cell1", var(103)),

                        // push bar.x
                        new LogicInstruction(SET, var(105), "63"),
                        new LogicInstruction(READ, var(106), "cell1", var(105)),
                        new LogicInstruction(SET, var(104), var(106)),
                        new LogicInstruction(SET, var(107), "1"),
                        new LogicInstruction(OP, "sub", var(108), var(104), var(107)),
                        new LogicInstruction(SET, var(104), var(108)),
                        new LogicInstruction(WRITE, "r", "cell1", var(104)),
                        new LogicInstruction(SET, var(112), "63"),
                        new LogicInstruction(WRITE, var(104), "cell1", var(112)),

                        // jump to subroutine
                        new LogicInstruction(SET, "@counter", var(1001)),

                        // return address
                        new LogicInstruction(LABEL, var(1003)),

                        // pop return value from stack
                        new LogicInstruction(SET, var(115), "63"),
                        new LogicInstruction(READ, var(116), "cell1", var(115)),
                        new LogicInstruction(SET, var(114), var(116)),
                        new LogicInstruction(READ, var(117), "cell1", var(114)),
                        new LogicInstruction(SET, var(113), var(117)),
                        new LogicInstruction(SET, var(118), "1"),
                        new LogicInstruction(OP, "add", var(119), var(114), var(118)),
                        new LogicInstruction(SET, var(114), var(119)),
                        new LogicInstruction(SET, var(122), "63"),
                        new LogicInstruction(WRITE, var(114), "cell1", var(122)),

                        // function body
                        new LogicInstruction(OP, "pow", var(123), "n", var(113)),
                        new LogicInstruction(OP, "mul", var(124), var(94), var(123)),

                        // pop return address
                        new LogicInstruction(SET, var(127), "63"),
                        new LogicInstruction(READ, var(128), "cell1", var(127)),
                        new LogicInstruction(SET, var(126), var(128)),
                        new LogicInstruction(READ, var(129), "cell1", var(126)),
                        new LogicInstruction(SET, var(125), var(129)),
                        new LogicInstruction(SET, var(130), "1"),
                        new LogicInstruction(OP, "add", var(131), var(126), var(130)),
                        new LogicInstruction(SET, var(126), var(131)),
                        new LogicInstruction(SET, var(134), "63"),
                        new LogicInstruction(WRITE, var(126), "cell1", var(134)),

                        // push return value
                        new LogicInstruction(SET, var(136), "63"),
                        new LogicInstruction(READ, var(137), "cell1", var(136)),
                        new LogicInstruction(SET, var(135), var(137)),
                        new LogicInstruction(SET, var(138), "1"),
                        new LogicInstruction(OP, "sub", var(139), var(135), var(138)),
                        new LogicInstruction(SET, var(135), var(139)),
                        new LogicInstruction(WRITE, var(124), "cell1", var(135)),
                        new LogicInstruction(SET, var(143), "63"),
                        new LogicInstruction(WRITE, var(135), "cell1", var(143)),

                        // return
                        new LogicInstruction(SET, "@counter", var(125)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SENSOR, var(0), "leader", "@shootX"),
                        new LogicInstruction(SENSOR, var(1), "leader", "@shootY"),
                        new LogicInstruction(SENSOR, var(2), "leader", "@shooting"),
                        new LogicInstruction(CONTROL, "shoot", "turret", var(0), var(1), var(2)),
                        new LogicInstruction(SET, var(3), "14"),
                        new LogicInstruction(SET, var(4), "15"),
                        new LogicInstruction(SET, var(5), "16"),
                        new LogicInstruction(CONTROL, "color", "turret", var(3), var(4), var(5)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, "HEAPPTR", "cell1"),
                        new LogicInstruction(SET, var(2), "0"),
                        new LogicInstruction(SET, var(3), "0"),
                        new LogicInstruction(WRITE, var(2), "HEAPPTR", var(3)), // write $dx
                        new LogicInstruction(SET, var(6), "1"),
                        new LogicInstruction(READ, var(7), "HEAPPTR", var(6)), // read $dy
                        new LogicInstruction(SET, var(8), "0"),
                        new LogicInstruction(READ, var(9), "HEAPPTR", var(8)), // read $dx
                        new LogicInstruction(OP, "add", var(10), var(7), var(9)), // tmp10 = $dx + $dy
                        new LogicInstruction(SET, var(11), "1"),
                        new LogicInstruction(WRITE, var(10), "HEAPPTR", var(11)), // set $dy
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, "STACKPTR", "cell1"),
                        new LogicInstruction(SET, "HEAPPTR", "cell2"),

                        new LogicInstruction(SET, var(2), "63"), // init stack
                        new LogicInstruction(SET, var(3), "63"),
                        new LogicInstruction(WRITE, var(2), "STACKPTR", var(3)),

                        new LogicInstruction(SET, var(7), "63"), // push return address on stack
                        new LogicInstruction(READ, var(8), "STACKPTR", var(7)),
                        new LogicInstruction(SET, var(6), var(8)),
                        new LogicInstruction(SET, var(9), "1"),
                        new LogicInstruction(OP, "sub", var(10), var(6), var(9)),
                        new LogicInstruction(SET, var(6), var(10)),
                        new LogicInstruction(WRITE, var(1001), "STACKPTR", var(6)),
                        new LogicInstruction(SET, var(14), "63"),
                        new LogicInstruction(WRITE, var(6), "STACKPTR", var(14)),

                        new LogicInstruction(SET, "@counter", var(1000)), // invoke function

                        new LogicInstruction(LABEL, var(1001)),

                        new LogicInstruction(SET, var(17), "63"), // pop return value from stack
                        new LogicInstruction(READ, var(18), "STACKPTR", var(17)),
                        new LogicInstruction(SET, var(16), var(18)),
                        new LogicInstruction(READ, var(19), "STACKPTR", var(16)),
                        new LogicInstruction(SET, var(15), var(19)),
                        new LogicInstruction(SET, var(20), "1"),
                        new LogicInstruction(OP, "add", var(21), var(16), var(20)),
                        new LogicInstruction(SET, var(16), var(21)),
                        new LogicInstruction(SET, var(24), "63"),
                        new LogicInstruction(WRITE, var(16), "STACKPTR", var(24)),

                        new LogicInstruction(SET, var(25), "0"), // write $dx
                        new LogicInstruction(WRITE, var(15), "HEAPPTR", var(25)),

                        new LogicInstruction(END), // end of main function body

                        new LogicInstruction(LABEL, var(1000)), // generateUnoptimized of delay function

                        new LogicInstruction(SET, var(26), "0"), // return value

                        new LogicInstruction(SET, var(29), "63"), // pop return address from stack
                        new LogicInstruction(READ, var(30), "STACKPTR", var(29)),
                        new LogicInstruction(SET, var(28), var(30)),
                        new LogicInstruction(READ, var(31), "STACKPTR", var(28)),
                        new LogicInstruction(SET, var(27), var(31)),
                        new LogicInstruction(SET, var(32), "1"),
                        new LogicInstruction(OP, "add", var(33), var(28), var(32)),
                        new LogicInstruction(SET, var(28), var(33)),
                        new LogicInstruction(SET, var(36), "63"),
                        new LogicInstruction(WRITE, var(28), "STACKPTR", var(36)),

                        new LogicInstruction(SET, var(38), "63"), // push return value on stack
                        new LogicInstruction(READ, var(39), "STACKPTR", var(38)),
                        new LogicInstruction(SET, var(37), var(39)),
                        new LogicInstruction(SET, var(40), "1"),
                        new LogicInstruction(OP, "sub", var(41), var(37), var(40)),
                        new LogicInstruction(SET, var(37), var(41)),
                        new LogicInstruction(WRITE, var(26), "STACKPTR", var(37)),
                        new LogicInstruction(SET, var(45), "63"),
                        new LogicInstruction(WRITE, var(37), "STACKPTR", var(45)),

                        new LogicInstruction(SET, "@counter", var(27)), // jump back to caller
                        new LogicInstruction(END)

                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "2"),
                        new LogicInstruction(OP, "sqrt", var(1), var(0)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "2"),
                        new LogicInstruction(OP, "rand", var(1), var(0)),
                        new LogicInstruction(OP, "floor", var(2), var(1)),
                        new LogicInstruction(SET, "__ast0", var(2)),
                        new LogicInstruction(SET, var(5), "0"),
                        new LogicInstruction(JUMP, var(1001), "notEqual", var(3), var(5)),
                        new LogicInstruction(SET, var(6), "1000"),
                        new LogicInstruction(SET, var(4), var(6)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, var(7), "1"),
                        new LogicInstruction(JUMP, var(1002), "notEqual", var(3), var(7)),
                        new LogicInstruction(SET, var(4), "null"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(4), "null"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "2"),
                        new LogicInstruction(OP, "max", var(1), "y", var(0)),
                        new LogicInstruction(OP, "min", var(2), "x", var(1)),
                        new LogicInstruction(SET, "r", var(2)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "9842"),
                        new LogicInstruction(SET, var(1), "1"),
                        new LogicInstruction(OP, "and", var(2), var(0), var(1)),
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(SET, var(4), "4"),
                        new LogicInstruction(OP, "shl", var(5), var(3), var(4)),
                        new LogicInstruction(OP, "xor", var(6), var(2), var(5)),
                        new LogicInstruction(SET, var(7), "1"),
                        new LogicInstruction(OP, "shr", var(8), "y", var(7)),
                        new LogicInstruction(OP, "or", var(9), var(6), var(8)),
                        new LogicInstruction(END)

                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "40"),
                        new LogicInstruction(SET, var(1), "40"),
                        new LogicInstruction(WRITE, var(0), "cell3", var(1)),
                        new LogicInstruction(SET, var(2), "99"),
                        new LogicInstruction(SET, var(3), "41"),
                        new LogicInstruction(WRITE, var(2), "cell3", var(3))
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("allocate stack in cell3[0..40], heap in cell3[41...64]\ndef foo(n)\n2*n\nend\n\n$x = 99\nprint(foo(1) + foo(2))\n")
                ).subList(0, 6)
        );
    }

    @Test
    void generatesVectorLengthAndAngleCalls() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "4"),
                        new LogicInstruction(SET, var(1), "8"),
                        new LogicInstruction(OP, "len", var(2), var(0), var(1)),
                        new LogicInstruction(SET, "length", var(2)),
                        new LogicInstruction(SET, var(3), "4"),
                        new LogicInstruction(SET, var(4), "8"),
                        new LogicInstruction(OP, "angle", var(5), var(3), var(4)),
                        new LogicInstruction(SET, "angle", var(5)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("length = len(4, 8)\nangle = angle(4, 8)\n")
                )
        );
    }

    @Test
    void generatesLog10Call() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "405"),
                        new LogicInstruction(OP, "log10", var(1), var(0)),
                        new LogicInstruction(SET, "l", var(1)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("l = log10(405)\n")
                )
        );
    }

    @Test
    void generatesNoiseCall() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "4"),
                        new LogicInstruction(SET, var(1), "8"),
                        new LogicInstruction(OP, "noise", var(2), var(0), var(1)),
                        new LogicInstruction(SET, "n", var(2)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("n = noise(4, 8)\n")
                )
        );
    }

    @Test
    void generatesIntegerDivision() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "4"),
                        new LogicInstruction(SET, var(1), "8"),
                        new LogicInstruction(OP, "idiv", var(2), var(0), var(1)),
                        new LogicInstruction(SET, "n", var(2)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("n = 4 \\ 8\n")
                )
        );
    }

    @Test
    void generatesCorrectCodeWhenCaseBranchIsCommentedOut() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "__ast0", "n"),
                        new LogicInstruction(SET, var(1), "2"),
                        new LogicInstruction(JUMP, var(1001), "notEqual", "__ast0", var(1)),
                        new LogicInstruction(PRINT, "n"),
                        new LogicInstruction(SET, var(0), "n"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, var(0), "null"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(JUMP, var(1000), "equal", "n", "false"),
                        new LogicInstruction(SET, var(0), "null"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "1"),
                        new LogicInstruction(SET, var(0), var(1)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(JUMP, var(1002), "equal", "m", "false"),
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(SET, var(2), var(3)),
                        new LogicInstruction(JUMP, var(1003), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(2), "null"),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, "resource", "@silicon"),
                        new LogicInstruction(SENSOR, var(0), "vault1", "resource"),
                        new LogicInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                        new LogicInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(2), "false"),
                        new LogicInstruction(SET, "foo", "true"),
                        new LogicInstruction(SET, var(3), "true"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(3), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SENSOR, var(0), "vault1", "@graphite"),
                        new LogicInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                        new LogicInstruction(OP, "lessThan", var(2), var(0), var(1)),
                        new LogicInstruction(CONTROL, "enabled", "conveyor1", var(2)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(SET, var(0), "\"\\nsm.enabled: \""),
                        new LogicInstruction(SENSOR, var(1), "smelter1", "@enabled"),
                        new LogicInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        new LogicInstruction(SET, var(3), "\"true\""),
                        new LogicInstruction(SET, var(2), var(3)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(4), "\"false\""),
                        new LogicInstruction(SET, var(2), var(4)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINT, var(0)),
                        new LogicInstruction(PRINT, var(2)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
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
                        new LogicInstruction(OP, "greaterThan", var(0), "b", "c"),
                        new LogicInstruction(JUMP, var(1005), "equal", var(0), "false"),
                        new LogicInstruction(SET, var(2), "1"),
                        new LogicInstruction(SET, var(1), var(2)),
                        new LogicInstruction(JUMP, var(1013), "always"),
                        new LogicInstruction(LABEL, var(1005)),
                        new LogicInstruction(OP, "greaterThan", var(3), "d", "e"),
                        new LogicInstruction(JUMP, var(1010), "equal", var(3), "false"),
                        new LogicInstruction(SET, var(5), "2"),
                        new LogicInstruction(SET, var(4), var(5)),
                        new LogicInstruction(JUMP, var(1012), "always"),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(SET, var(6), "3"),
                        new LogicInstruction(SET, var(4), var(6)),
                        new LogicInstruction(LABEL, var(1012)),
                        new LogicInstruction(SET, var(1), var(4)),
                        new LogicInstruction(LABEL, var(1013)),
                        new LogicInstruction(SET, "a", var(1)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst(
                                "a = (b > c) ? 1 : (d > e) ? 2 : 3"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesUnaryLiteralMinusExpressions() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "a", "-7"),
                        new LogicInstruction(OP, "mul", "b", "-1", "a"),
                        new LogicInstruction(OP, "mul", "c", "-5", "b"),
                        new LogicInstruction(OP, "mul", var(0), "-1", "c"),
                        new LogicInstruction(OP, "mul", "d", "2", var(0)),
                        new LogicInstruction(OP, "pow", var(1), "d", "2"),
                        new LogicInstruction(OP, "mul", "e", "-1", var(1)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateAndOptimize(
                        (Seq) translateToAst("" +
                                "a = -7\n" +
                                "b = -a\n" +
                                "c = -5 * b\n" +
                                "d = 2 * -c\n" +
                                "e = -d ** 2"
                        ),
                        new CompileProfile(Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.OUTPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesTernaryOpPriority() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "greaterThan", var(0), "b", "c"),
                        new LogicInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        new LogicInstruction(SET, var(1), "b"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "c"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, "a", var(1)),
                        new LogicInstruction(OP, "mul", var(2), "e", "f"),
                        new LogicInstruction(SET, "e", var(2)),
                        new LogicInstruction(OP, "mul", var(3), "-1", var(2)),
                        new LogicInstruction(OP, "add", "d", "d", var(3)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateAndOptimize(
                        (Seq) translateToAst("" +
                                "a = b > c ? b : c\n" +
                                "d += -e *= f"
                        ),
                        new CompileProfile(Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.OUTPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesBitwiseOpPriority() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "and", var(0), "c", "d"),
                        new LogicInstruction(OP, "or", "a", "b", var(0)),
                        new LogicInstruction(OP, "not", var(1), "f"),
                        new LogicInstruction(OP, "and", "e", var(1), "g"),
                        new LogicInstruction(OP, "and", var(2), "h", "31"),
                        new LogicInstruction(OP, "equal", "g", var(2), "15"),
                        new LogicInstruction(OP, "and", var(3), "y", "15"),
                        new LogicInstruction(OP, "and", var(4), "z", "7"),
                        new LogicInstruction(OP, "land", "x", var(3), var(4)),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateAndOptimize(
                        (Seq) translateToAst("" +
                                "a = b | c & d\n" +
                                "e = ~f & g\n" +
                                "g = h & 31 == 15\n" +
                                "x = y & 15 and z & 7"
                        ),
                        new CompileProfile(Optimisation.INPUT_TEMPS_ELIMINATION, Optimisation.OUTPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesStrictNotEqual() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, var(0), "@unit", "@dead"),
                        new LogicInstruction(OP, "strictEqual", var(1), var(0), "null"),
                        new LogicInstruction(OP, "equal", var(2), var(1), "false"),
                        new LogicInstruction(SET, "a", var(2)),
                        new LogicInstruction(PRINT, "a"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst(
                                "a = @unit.dead !== null\nprint(a)"
                        )
                )
        );
    }

    @Test
    void correctlyHandlesBreakContinue() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(JUMP, var(1002), "equal", "a", "false"),
                        new LogicInstruction(JUMP, var(1003), "equal", "b", "false"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(JUMP, var(1004), "always"),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(JUMP, var(1005), "equal", "c", "false"),
                        new LogicInstruction(JUMP, var(1002), "always"),
                        new LogicInstruction(JUMP, var(1006), "always"),
                        new LogicInstruction(LABEL, var(1005)),
                        new LogicInstruction(LABEL, var(1006)),
                        new LogicInstruction(LABEL, var(1004)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(PRINT, "\"End\""),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateAndOptimize(
                        (Seq) translateToAst("" +
                                "while a\n" +
                                "  if b\n" +
                                "    continue\n" +
                                "  elsif c\n" +
                                "    break\n" +
                                "  end\n" +
                                "end\n" +
                                "print(\"End\")"
                        ),
                        new CompileProfile(Optimisation.DEAD_CODE_ELIMINATION, Optimisation.INPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void correctlyHandlesNestedLoopBreaks() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(LABEL, var(0)),
                        new LogicInstruction(JUMP, var(1), "equal", "a", "false"),
                        new LogicInstruction(PRINT, "a"),
                        new LogicInstruction(LABEL, var(2)),
                        new LogicInstruction(JUMP, var(3), "equal", "b", "false"),
                        new LogicInstruction(PRINT, "b"),
                        new LogicInstruction(JUMP, var(4), "equal", "c", "false"),
                        new LogicInstruction(PRINT, "c"),
                        new LogicInstruction(JUMP, var(3), "always"),
                        new LogicInstruction(JUMP, var(5), "always"),
                        new LogicInstruction(LABEL, var(4)),
                        new LogicInstruction(LABEL, var(5)),
                        new LogicInstruction(PRINT, "d"),
                        new LogicInstruction(JUMP, var(3), "always"),
                        new LogicInstruction(LABEL, var(10)),
                        new LogicInstruction(JUMP, var(2), "always"),
                        new LogicInstruction(LABEL, var(3)),
                        new LogicInstruction(PRINT, "e"),
                        new LogicInstruction(JUMP, var(1), "always"),
                        new LogicInstruction(LABEL, var(11)),
                        new LogicInstruction(JUMP, var(0), "always"),
                        new LogicInstruction(LABEL, var(1)),
                        new LogicInstruction(PRINT, "f"),
                        new LogicInstruction(END)
                ),
                LogicInstructionGenerator.generateAndOptimize(
                        (Seq) translateToAst("" +
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
                        new CompileProfile(Optimisation.DEAD_CODE_ELIMINATION, Optimisation.INPUT_TEMPS_ELIMINATION),
                        message -> {}
                )
        );
    }

    @Test
    void refusesBreaksOutsideLoop() {
        assertThrows(GenerationException.class, () ->
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("" +
                                "while a\n  print(a)\nend\nbreak"
                        )
                )
        );
    }
}
