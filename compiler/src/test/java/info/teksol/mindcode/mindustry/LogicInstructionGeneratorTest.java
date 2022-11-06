package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void convertsComplexAssignment() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("op", "sub", var(1), "bar", var(0)),
                        new LogicInstruction("set", var(2), "3"),
                        new LogicInstruction("op", "mul", var(3), var(1), var(2)),
                        new LogicInstruction("set", "foo", var(3)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("set", "n", var(0)),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(1), "5"),
                        new LogicInstruction("op", "lessThan", var(2), "n", var(1)),
                        new LogicInstruction("jump", var(1001), "notEqual", var(2), "true"),
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("op", "add", var(4), "n", var(3)),
                        new LogicInstruction("set", "n", var(4)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", var(5), "\"n: \""),
                        new LogicInstruction("print", var(5)),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("n = 0\nwhile n < 5\nn += 1\nend\nprint(\"n: \", n)"))
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("op", "not", var(0), "n"),
                        new LogicInstruction("set", "n", var(0)),
                        new LogicInstruction("set", "x", "null"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("n = not n\nx = null"))
        );
    }

    @Test
    void convertsSensorReadings() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", var(0), "foundation1", "@copper"),
                        new LogicInstruction("sensor", var(1), "foundation1", "@itemCapacity"),
                        new LogicInstruction("op", "lessThan", var(2), var(0), var(1)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("op", "notEqual", var(0), "true", "false"),
                        new LogicInstruction("jump", var(1001), "notEqual", var(0), "true"),
                        new LogicInstruction("set", var(1), "\"infinite loop!\""),
                        new LogicInstruction("print", var(1)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("sensor", var(1), "foundation1", "@copper"),
                        new LogicInstruction("sensor", var(2), "tank1", "@water"),
                        new LogicInstruction("op", "strictEqual", var(3), var(1), var(2)),
                        new LogicInstruction("control", "enabled", "conveyor1", var(3)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(2), "4"),
                        new LogicInstruction("read", var(3), "cell2", var(2)),
                        new LogicInstruction("sensor", var(4), "conveyor1", "@enabled"),
                        new LogicInstruction("op", "add", var(5), var(3), var(4)),
                        new LogicInstruction("set", var(6), "3"),
                        new LogicInstruction("write", var(5), "cell1", var(6)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "4"),
                        new LogicInstruction("read", var(1), "cell1", var(0)),
                        new LogicInstruction("set", var(2), "0"),
                        new LogicInstruction("op", "equal", var(3), var(1), var(2)),
                        new LogicInstruction("jump", var(1000), "notEqual", var(3), "true"),
                        new LogicInstruction("set", var(9), "false"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(6), "4"),
                        new LogicInstruction("write", "true", "cell1", var(6)),
                        new LogicInstruction("set", var(7), "1"),
                        new LogicInstruction("op", "add", var(8), "n", var(7)),
                        new LogicInstruction("set", "n", var(8)),
                        new LogicInstruction("set", var(9), var(8)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", "value", var(9)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(2), "9"),
                        new LogicInstruction("set", var(3), "9"),
                        new LogicInstruction("op", "pow", var(4), var(2), var(3)),
                        new LogicInstruction("op", "rand", var(5), var(4)),
                        new LogicInstruction("set", var(6), "0"),
                        new LogicInstruction("write", var(5), "cell1", var(6)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized((Seq) translateToAst("cell1[0] = rand(9**9)"))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("op", "strictEqual", var(0), "@unit", "null"),
                        new LogicInstruction("jump", var(1001), "notEqual", var(0), "true"),
                        new LogicInstruction("ubind", "@poly"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("set", "n", var(0)),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("getlink", var(1), "n"),
                        new LogicInstruction("set", "reactor", var(1)),
                        new LogicInstruction("op", "notEqual", var(2), var(1), "null"),
                        new LogicInstruction("jump", var(1003), "notEqual", var(2), "true"),
                        new LogicInstruction("sensor", var(3), "reactor", "@liquidCapacity"),
                        new LogicInstruction("set", var(4), "0"),
                        new LogicInstruction("op", "greaterThan", var(5), var(3), var(4)),
                        new LogicInstruction("jump", var(1000), "notEqual", var(5), "true"),
                        new LogicInstruction("sensor", var(6), "reactor", "@cryofluid"),
                        new LogicInstruction("sensor", var(7), "reactor", "@liquidCapacity"),
                        new LogicInstruction("op", "div", var(8), var(6), var(7)),
                        new LogicInstruction("set", "pct_avail", var(8)),
                        new LogicInstruction("set", var(10), "0.25"),
                        new LogicInstruction("op", "greaterThanEq", var(11), "pct_avail", var(10)),
                        new LogicInstruction("control", "enabled", "reactor", var(11)),
                        new LogicInstruction("set", var(12), var(11)),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(12), "null"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", var(13), "1"),
                        new LogicInstruction("op", "add", var(14), "n", var(13)),
                        new LogicInstruction("set", "n", var(14)),
                        new LogicInstruction("jump", var(1002), "always"),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "-1"),
                        new LogicInstruction("op", "mul", var(1), "dx", var(0)),
                        new LogicInstruction("set", "dx", var(1)),
                        new LogicInstruction("set", var(2), "-1"),
                        new LogicInstruction("set", "dy", var(2)),
                        new LogicInstruction("set", var(3), "2"),
                        new LogicInstruction("set", var(4), "1"),
                        new LogicInstruction("op", "sub", var(5), var(3), var(4)),
                        new LogicInstruction("set", "dz", var(5)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("set", "a", var(0)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("set", var(1), "0"),
                        new LogicInstruction("ucontrol", "build", "x", "y", "@titanium-conveyor", var(0), var(1)),
                        new LogicInstruction("ucontrol", "getBlock", "x", "y", "b_type", "b_building"),
                        new LogicInstruction("op", "equal", var(2), "b_type", "@titanium-conveyor"),
                        new LogicInstruction("jump", var(1000), "notEqual", var(2), "true"),
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("op", "add", var(4), "n", var(3)),
                        new LogicInstruction("set", "n", var(4)),
                        new LogicInstruction("set", var(5), var(4)),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(5), "null"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("op", "rand", var(1), var(0)),
                        new LogicInstruction("op", "tan", var(2), var(1)),
                        new LogicInstruction("op", "abs", var(3), var(2)),
                        new LogicInstruction("op", "cos", var(4), var(3)),
                        new LogicInstruction("op", "log", var(5), var(4)),
                        new LogicInstruction("op", "sin", var(6), var(5)),
                        new LogicInstruction("op", "floor", var(7), var(6)),
                        new LogicInstruction("op", "ceil", var(8), var(7)),
                        new LogicInstruction("set", "x", var(8)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("set", "n", var(0)),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(1), "17"),
                        new LogicInstruction("op", "lessThanEq", var(2), "n", var(1)),
                        new LogicInstruction("jump", var(1001), "notEqual", var(2), "true"),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("op", "add", var(4), "n", var(3)),
                        new LogicInstruction("set", "n", var(4)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("set", "n", var(0)),

                        // cond
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(1), "17"),
                        new LogicInstruction("op", "lessThan", var(2), "n", var(1)),
                        new LogicInstruction("jump", var(1001), "notEqual", var(2), "true"),

                        // loop body
                        new LogicInstruction("print", "n"),

                        // increment
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("op", "add", var(4), "n", var(3)),
                        new LogicInstruction("set", "n", var(4)),

                        // loop
                        new LogicInstruction("jump", var(1000), "always"),

                        // trailer
                        new LogicInstruction("label", var(1001)),

                        // rest of program
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("set", "i", var(0)),
                        new LogicInstruction("set", var(1), "-5"),
                        new LogicInstruction("set", "j", var(1)),

                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(2), "5"),
                        new LogicInstruction("op", "lessThan", var(3), "i", var(2)),
                        new LogicInstruction("jump", var(1001), "notEqual", var(3), "true"),
                        new LogicInstruction("print", "n"),

                        new LogicInstruction("set", var(4), "1"),
                        new LogicInstruction("op", "sub", var(5), "j", var(4)),
                        new LogicInstruction("set", "j", var(5)),

                        new LogicInstruction("set", var(6), "1"),
                        new LogicInstruction("op", "add", var(7), "i", var(6)),
                        new LogicInstruction("set", "i", var(7)),

                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "42"),
                        new LogicInstruction("set", "b", var(0)),
                        new LogicInstruction("set", "a", var(0)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", "ast0", "n"),
                        new LogicInstruction("set", var(2), "1"),
                        new LogicInstruction("jump", var(1001), "notEqual", var(0), var(2)),
                        new LogicInstruction("set", var(3), "\"1\""),
                        new LogicInstruction("set", var(1), var(3)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", var(4), "2"),
                        new LogicInstruction("jump", var(1002), "notEqual", var(0), var(4)),
                        new LogicInstruction("set", var(5), "\"two\""),
                        new LogicInstruction("set", var(1), var(5)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(6), "\"otherwise\""),
                        new LogicInstruction("set", var(1), var(6)),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("read", var(1), "cell1", var(0)),
                        new LogicInstruction("set", "ast0", var(1)),
                        new LogicInstruction("jump", var(1001), "notEqual", var(2), "ST_EMPTY"),
                        new LogicInstruction("set", var(6), "0"),
                        new LogicInstruction("write", "ST_INITIALIZED", "cell1", var(6)),
                        new LogicInstruction("set", var(3), "ST_INITIALIZED"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("jump", var(1002), "notEqual", var(2), "ST_INITIALIZED"),
                        new LogicInstruction("set", var(9), "0"),
                        new LogicInstruction("write", "ST_DONE", "cell1", var(9)),
                        new LogicInstruction("set", var(3), "ST_DONE"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(3), "null"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("draw", "clear", "r", "g", "b"),
                        new LogicInstruction("draw", "color", "r", "g", "b", "alpha"),
                        new LogicInstruction("draw", "stroke", "width"),
                        new LogicInstruction("draw", "line", "x1", "y1", "x2", "y2"),
                        new LogicInstruction("draw", "rect", "x", "y", "w", "h"),
                        new LogicInstruction("draw", "lineRect", "x", "y", "w", "h"),
                        new LogicInstruction("draw", "poly", "x", "y", "sides", "radius", "rotation"),
                        new LogicInstruction("draw", "linePoly", "x", "y", "sides", "radius", "rotation"),
                        new LogicInstruction("draw", "triangle", "x1", "y1", "x2", "y2", "x3", "y3"),
                        new LogicInstruction("draw", "image", "x", "y", "@copper", "size", "rotation"),
                        new LogicInstruction("drawflush", "display1"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("uradar", "enemy", "ground", "any", "health", "MIN_TO_MAX", "BY_DISTANCE", var(0)),
                        new LogicInstruction("set", "target", var(0)),
                        new LogicInstruction("op", "notEqual", var(1), "target", "null"),
                        new LogicInstruction("jump", var(1002), "notEqual", var(1), "true"),
                        new LogicInstruction("sensor", var(2), "target", "@x"),
                        new LogicInstruction("sensor", var(3), "target", "@y"),
                        new LogicInstruction("set", var(4), "10"),
                        new LogicInstruction("ucontrol", "approach", var(2), var(3), var(4)),
                        new LogicInstruction("sensor", var(5), "target", "@x"),
                        new LogicInstruction("sensor", var(6), "target", "@y"),
                        new LogicInstruction("set", var(7), "10"),
                        new LogicInstruction("ucontrol", "within", var(5), var(6), var(7), var(8)),
                        new LogicInstruction("jump", var(1000), "notEqual", var(8), "true"),
                        new LogicInstruction("sensor", var(9), "target", "@x"),
                        new LogicInstruction("sensor", var(10), "target", "@y"),
                        new LogicInstruction("ucontrol", "target", var(9), var(10), "SHOOT"),
                        new LogicInstruction("set", var(11), "null"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(11), "null"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", var(12), var(11)),
                        new LogicInstruction("jump", var(1003), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(12), "null"),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst("" +
                                "target = uradar(enemy, ground, any, health, MIN_TO_MAX, BY_DISTANCE)\n" +
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
                        new LogicInstruction("ulocate", "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        new LogicInstruction("ulocate", "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        new LogicInstruction("ulocate", "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        new LogicInstruction("ulocate", "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("radar", "enemy", "any", "any", "distance", "salvo1", var(0), var(1)),
                        new LogicInstruction("set", "out", var(1)),
                        new LogicInstruction("set", var(2), "1"),
                        new LogicInstruction("radar", "ally", "flying", "any", "health", "lancer1", var(2), var(3)),
                        new LogicInstruction("set", "out", var(3)),
                        new LogicInstruction("set", "src", "salvo1"),
                        new LogicInstruction("set", var(4), "1"),
                        new LogicInstruction("radar", "enemy", "any", "any", "distance", "src", var(4), var(5)),
                        new LogicInstruction("set", "out", var(5)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("wait", var(0)),
                        new LogicInstruction("set", var(1), "0.001"),
                        new LogicInstruction("wait", var(1)),
                        new LogicInstruction("set", var(2), "1000"),
                        new LogicInstruction("wait", var(2)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("op", "equal", var(0), "some_cond", "false"),
                        new LogicInstruction("jump", var(1000), "notEqual", var(0), "true"),
                        new LogicInstruction("end"),
                        new LogicInstruction("set", var(1), "null"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(1), "null"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("op", "mod", var(1), "@tick", var(0)),
                        new LogicInstruction("set", var(2), "0"),
                        new LogicInstruction("op", "equal", var(3), var(1), var(2)),
                        new LogicInstruction("set", "running", var(3)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(2), "63"),
                        new LogicInstruction("set", var(3), "63"),
                        new LogicInstruction("write", var(2), "cell1", var(3)),

                        // push return address on stack
                        new LogicInstruction("set", var(5), "63"),
                        new LogicInstruction("read", var(6), "cell1", var(5)), // read stack pointer
                        new LogicInstruction("set", var(4), var(6)),
                        new LogicInstruction("set", var(7), "1"),
                        new LogicInstruction("op", "sub", var(8), var(4), var(7)), // calculate new stack pointer
                        new LogicInstruction("set", var(4), var(8)),
                        new LogicInstruction("write", var(1001), "cell1", var(4)), // write value on stack, at stack pointer
                        new LogicInstruction("set", var(12), "63"),
                        new LogicInstruction("write", var(4), "cell1", var(12)), // update stack pointer itself

                        // jump to function
                        new LogicInstruction("set", "@counter", var(1000)),

                        // return label
                        new LogicInstruction("label", var(1001)),

                        // pop return value from stack
                        new LogicInstruction("set", var(15), "63"),
                        new LogicInstruction("read", var(16), "cell1", var(15)), // read stack pointer
                        new LogicInstruction("set", var(14), var(16)),
                        new LogicInstruction("read", var(17), "cell1", var(14)), // read value on stack, at stack pointer
                        new LogicInstruction("set", var(13), var(17)),
                        new LogicInstruction("set", var(18), "1"),
                        new LogicInstruction("op", "add", var(19), var(14), var(18)), // calculate new stack pointer
                        new LogicInstruction("set", var(14), var(19)),
                        new LogicInstruction("set", var(22), "63"),
                        new LogicInstruction("write", var(14), "cell1", var(22)), // update stack pointer itself

                        // continue rest of main script
                        new LogicInstruction("set", "x", var(13)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("printflush", "message1"),

                        // end of main script
                        new LogicInstruction("end"),

                        // generateUnoptimized of function foo
                        new LogicInstruction("label", var(1000)),

                        // no parameters to pop
                        new LogicInstruction("set", var(23), "0"),

                        // pop return address
                        new LogicInstruction("set", var(26), "63"),
                        new LogicInstruction("read", var(27), "cell1", var(26)),
                        new LogicInstruction("set", var(25), var(27)),
                        new LogicInstruction("read", var(28), "cell1", var(25)),
                        new LogicInstruction("set", var(24), var(28)),
                        new LogicInstruction("set", var(29), "1"),
                        new LogicInstruction("op", "add", var(30), var(25), var(29)),
                        new LogicInstruction("set", var(25), var(30)),
                        new LogicInstruction("set", var(33), "63"),
                        new LogicInstruction("write", var(25), "cell1", var(33)),

                        // push return value
                        new LogicInstruction("set", var(35), "63"),
                        new LogicInstruction("read", var(36), "cell1", var(35)),
                        new LogicInstruction("set", var(34), var(36)),
                        new LogicInstruction("set", var(37), "1"),
                        new LogicInstruction("op", "sub", var(38), var(34), var(37)),
                        new LogicInstruction("set", var(34), var(38)),
                        new LogicInstruction("write", var(23), "cell1", var(34)),
                        new LogicInstruction("set", var(42), "63"),
                        new LogicInstruction("write", var(34), "cell1", var(42)),

                        // jump to return address
                        new LogicInstruction("set", "@counter", var(24)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(2), "63"),
                        new LogicInstruction("set", var(3), "63"),
                        new LogicInstruction("write", var(2), "cell1", var(3)),

                        // push return address on stack
                        new LogicInstruction("set", var(4), "4"),
                        new LogicInstruction("set", "boo", var(4)),
                        new LogicInstruction("set", var(5), "2"),

                        // push return address on stack
                        new LogicInstruction("set", var(7), "63"),
                        new LogicInstruction("read", var(8), "cell1", var(7)),
                        new LogicInstruction("set", var(6), var(8)),
                        new LogicInstruction("set", var(9), "1"),
                        new LogicInstruction("op", "sub", var(10), var(6), var(9)),
                        new LogicInstruction("set", var(6), var(10)),
                        new LogicInstruction("write", var(1001), "cell1", var(6)),
                        new LogicInstruction("set", var(14), "63"),
                        new LogicInstruction("write", var(6), "cell1", var(14)),

                        // push n on stack
                        new LogicInstruction("set", var(16), "63"),
                        new LogicInstruction("read", var(17), "cell1", var(16)),
                        new LogicInstruction("set", var(15), var(17)),
                        new LogicInstruction("set", var(18), "1"),
                        new LogicInstruction("op", "sub", var(19), var(15), var(18)),
                        new LogicInstruction("set", var(15), var(19)),
                        new LogicInstruction("write", var(5), "cell1", var(15)),
                        new LogicInstruction("set", var(23), "63"),
                        new LogicInstruction("write", var(15), "cell1", var(23)),

                        // push r on stack
                        new LogicInstruction("set", var(25), "63"),
                        new LogicInstruction("read", var(26), "cell1", var(25)),
                        new LogicInstruction("set", var(24), var(26)),
                        new LogicInstruction("set", var(27), "1"),
                        new LogicInstruction("op", "sub", var(28), var(24), var(27)),
                        new LogicInstruction("set", var(24), var(28)),
                        new LogicInstruction("write", "boo", "cell1", var(24)),
                        new LogicInstruction("set", var(32), "63"),
                        new LogicInstruction("write", var(24), "cell1", var(32)),

                        // jump to function
                        new LogicInstruction("set", "@counter", var(1000)),

                        // function return address
                        new LogicInstruction("label", var(1001)),

                        // pop return value from stack
                        new LogicInstruction("set", var(35), "63"),
                        new LogicInstruction("read", var(36), "cell1", var(35)),
                        new LogicInstruction("set", var(34), var(36)),
                        new LogicInstruction("read", var(37), "cell1", var(34)),
                        new LogicInstruction("set", var(33), var(37)),
                        new LogicInstruction("set", var(38), "1"),
                        new LogicInstruction("op", "add", var(39), var(34), var(38)),
                        new LogicInstruction("set", var(34), var(39)),
                        new LogicInstruction("set", var(42), "63"),
                        new LogicInstruction("write", var(34), "cell1", var(42)),

                        // continue main body
                        new LogicInstruction("set", "x", var(33)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end"),

                        // function foo
                        new LogicInstruction("label", var(1000)),

                        // pop r
                        new LogicInstruction("set", var(45), "63"),
                        new LogicInstruction("read", var(46), "cell1", var(45)),
                        new LogicInstruction("set", var(44), var(46)),
                        new LogicInstruction("read", var(47), "cell1", var(44)),
                        new LogicInstruction("set", var(43), var(47)),
                        new LogicInstruction("set", var(48), "1"),
                        new LogicInstruction("op", "add", var(49), var(44), var(48)),
                        new LogicInstruction("set", var(44), var(49)),
                        new LogicInstruction("set", var(52), "63"),
                        new LogicInstruction("write", var(44), "cell1", var(52)),
                        new LogicInstruction("set", "r", var(43)),

                        // pop n
                        new LogicInstruction("set", var(55), "63"),
                        new LogicInstruction("read", var(56), "cell1", var(55)),
                        new LogicInstruction("set", var(54), var(56)),
                        new LogicInstruction("read", var(57), "cell1", var(54)),
                        new LogicInstruction("set", var(53), var(57)),
                        new LogicInstruction("set", var(58), "1"),
                        new LogicInstruction("op", "add", var(59), var(54), var(58)),
                        new LogicInstruction("set", var(54), var(59)),
                        new LogicInstruction("set", var(62), "63"),
                        new LogicInstruction("write", var(54), "cell1", var(62)),
                        new LogicInstruction("set", "n", var(53)),

                        // execute function body
                        new LogicInstruction("set", var(63), "2"),
                        new LogicInstruction("op", "pow", var(64), "n", "r"),
                        new LogicInstruction("op", "mul", var(65), var(63), var(64)),

                        // pop return address from stack
                        new LogicInstruction("set", var(68), "63"),
                        new LogicInstruction("read", var(69), "cell1", var(68)),
                        new LogicInstruction("set", var(67), var(69)),
                        new LogicInstruction("read", var(70), "cell1", var(67)),
                        new LogicInstruction("set", var(66), var(70)),
                        new LogicInstruction("set", var(71), "1"),
                        new LogicInstruction("op", "add", var(72), var(67), var(71)),
                        new LogicInstruction("set", var(67), var(72)),
                        new LogicInstruction("set", var(75), "63"),
                        new LogicInstruction("write", var(67), "cell1", var(75)),

                        // push return value on stack
                        new LogicInstruction("set", var(77), "63"),
                        new LogicInstruction("read", var(78), "cell1", var(77)),
                        new LogicInstruction("set", var(76), var(78)),
                        new LogicInstruction("set", var(79), "1"),
                        new LogicInstruction("op", "sub", var(80), var(76), var(79)),
                        new LogicInstruction("set", var(76), var(80)),
                        new LogicInstruction("write", var(65), "cell1", var(76)),
                        new LogicInstruction("set", var(84), "63"),
                        new LogicInstruction("write", var(76), "cell1", var(84)),

                        // jump back to caller
                        new LogicInstruction("set", "@counter", var(66)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(2), "63"),
                        new LogicInstruction("set", var(3), "63"),
                        new LogicInstruction("write", var(2), "cell1", var(3)),

                        // prepare parameters
                        new LogicInstruction("set", var(4), "8"),
                        new LogicInstruction("set", "boo", var(4)),
                        new LogicInstruction("set", var(5), "7"),

                        // push return address
                        new LogicInstruction("set", var(7), "63"),
                        new LogicInstruction("read", var(8), "cell1", var(7)),
                        new LogicInstruction("set", var(6), var(8)),
                        new LogicInstruction("set", var(9), "1"),
                        new LogicInstruction("op", "sub", var(10), var(6), var(9)),
                        new LogicInstruction("set", var(6), var(10)),
                        new LogicInstruction("write", var(1002), "cell1", var(6)),
                        new LogicInstruction("set", var(14), "63"),
                        new LogicInstruction("write", var(6), "cell1", var(14)),

                        // push n
                        new LogicInstruction("set", var(16), "63"),
                        new LogicInstruction("read", var(17), "cell1", var(16)),
                        new LogicInstruction("set", var(15), var(17)),
                        new LogicInstruction("set", var(18), "1"),
                        new LogicInstruction("op", "sub", var(19), var(15), var(18)),
                        new LogicInstruction("set", var(15), var(19)),
                        new LogicInstruction("write", var(5), "cell1", var(15)),
                        new LogicInstruction("set", var(23), "63"),
                        new LogicInstruction("write", var(15), "cell1", var(23)),

                        // push r
                        new LogicInstruction("set", var(25), "63"),
                        new LogicInstruction("read", var(26), "cell1", var(25)),
                        new LogicInstruction("set", var(24), var(26)),
                        new LogicInstruction("set", var(27), "1"),
                        new LogicInstruction("op", "sub", var(28), var(24), var(27)),
                        new LogicInstruction("set", var(24), var(28)),
                        new LogicInstruction("write", "boo", "cell1", var(24)),
                        new LogicInstruction("set", var(32), "63"),
                        new LogicInstruction("write", var(24), "cell1", var(32)),

                        // jump to function
                        new LogicInstruction("set", "@counter", var(1000)),

                        // return address
                        new LogicInstruction("label", var(1002)),

                        // pop return value from stack
                        new LogicInstruction("set", var(35), "63"),
                        new LogicInstruction("read", var(36), "cell1", var(35)),
                        new LogicInstruction("set", var(34), var(36)),
                        new LogicInstruction("read", var(37), "cell1", var(34)),
                        new LogicInstruction("set", var(33), var(37)),
                        new LogicInstruction("set", var(38), "1"),
                        new LogicInstruction("op", "add", var(39), var(34), var(38)),
                        new LogicInstruction("set", var(34), var(39)),
                        new LogicInstruction("set", var(42), "63"),
                        new LogicInstruction("write", var(34), "cell1", var(42)),

                        // rest of main body
                        new LogicInstruction("set", "x", var(33)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end"),

                        // def bar
                        new LogicInstruction("label", var(1001)),

                        // pop bar.x
                        new LogicInstruction("set", var(45), "63"),
                        new LogicInstruction("read", var(46), "cell1", var(45)),
                        new LogicInstruction("set", var(44), var(46)),
                        new LogicInstruction("read", var(47), "cell1", var(44)),
                        new LogicInstruction("set", var(43), var(47)),
                        new LogicInstruction("set", var(48), "1"),
                        new LogicInstruction("op", "add", var(49), var(44), var(48)),
                        new LogicInstruction("set", var(44), var(49)),
                        new LogicInstruction("set", var(52), "63"),
                        new LogicInstruction("write", var(44), "cell1", var(52)),
                        new LogicInstruction("set", "x", var(43)),

                        // function body
                        new LogicInstruction("set", var(53), "2"),
                        new LogicInstruction("op", "mul", var(54), var(53), "x"),

                        // pop return address
                        new LogicInstruction("set", var(57), "63"),
                        new LogicInstruction("read", var(58), "cell1", var(57)),
                        new LogicInstruction("set", var(56), var(58)),
                        new LogicInstruction("read", var(59), "cell1", var(56)),
                        new LogicInstruction("set", var(55), var(59)),
                        new LogicInstruction("set", var(60), "1"),
                        new LogicInstruction("op", "add", var(61), var(56), var(60)),
                        new LogicInstruction("set", var(56), var(61)),
                        new LogicInstruction("set", var(64), "63"),
                        new LogicInstruction("write", var(56), "cell1", var(64)),

                        // push return value
                        new LogicInstruction("set", var(66), "63"),
                        new LogicInstruction("read", var(67), "cell1", var(66)),
                        new LogicInstruction("set", var(65), var(67)),
                        new LogicInstruction("set", var(68), "1"),
                        new LogicInstruction("op", "sub", var(69), var(65), var(68)),
                        new LogicInstruction("set", var(65), var(69)),
                        new LogicInstruction("write", var(54), "cell1", var(65)),
                        new LogicInstruction("set", var(73), "63"),
                        new LogicInstruction("write", var(65), "cell1", var(73)),

                        // return
                        new LogicInstruction("set", "@counter", var(55)),
                        new LogicInstruction("end"),

                        // def foo
                        new LogicInstruction("label", var(1000)),

                        // pop foo.r
                        new LogicInstruction("set", var(76), "63"),
                        new LogicInstruction("read", var(77), "cell1", var(76)),
                        new LogicInstruction("set", var(75), var(77)),
                        new LogicInstruction("read", var(78), "cell1", var(75)),
                        new LogicInstruction("set", var(74), var(78)),
                        new LogicInstruction("set", var(79), "1"),
                        new LogicInstruction("op", "add", var(80), var(75), var(79)),
                        new LogicInstruction("set", var(75), var(80)),
                        new LogicInstruction("set", var(83), "63"),
                        new LogicInstruction("write", var(75), "cell1", var(83)),
                        new LogicInstruction("set", "r", var(74)),

                        // pop foo.n
                        new LogicInstruction("set", var(86), "63"),
                        new LogicInstruction("read", var(87), "cell1", var(86)),
                        new LogicInstruction("set", var(85), var(87)),
                        new LogicInstruction("read", var(88), "cell1", var(85)),
                        new LogicInstruction("set", var(84), var(88)),
                        new LogicInstruction("set", var(89), "1"),
                        new LogicInstruction("op", "add", var(90), var(85), var(89)),
                        new LogicInstruction("set", var(85), var(90)),
                        new LogicInstruction("set", var(93), "63"),
                        new LogicInstruction("write", var(85), "cell1", var(93)),
                        new LogicInstruction("set", "n", var(84)),

                        // function body
                        new LogicInstruction("set", var(94), "2"),

                        // push return address
                        new LogicInstruction("set", var(96), "63"),
                        new LogicInstruction("read", var(97), "cell1", var(96)),
                        new LogicInstruction("set", var(95), var(97)),
                        new LogicInstruction("set", var(98), "1"),
                        new LogicInstruction("op", "sub", var(99), var(95), var(98)),
                        new LogicInstruction("set", var(95), var(99)),
                        new LogicInstruction("write", var(1003), "cell1", var(95)),
                        new LogicInstruction("set", var(103), "63"),
                        new LogicInstruction("write", var(95), "cell1", var(103)),

                        // push bar.x
                        new LogicInstruction("set", var(105), "63"),
                        new LogicInstruction("read", var(106), "cell1", var(105)),
                        new LogicInstruction("set", var(104), var(106)),
                        new LogicInstruction("set", var(107), "1"),
                        new LogicInstruction("op", "sub", var(108), var(104), var(107)),
                        new LogicInstruction("set", var(104), var(108)),
                        new LogicInstruction("write", "r", "cell1", var(104)),
                        new LogicInstruction("set", var(112), "63"),
                        new LogicInstruction("write", var(104), "cell1", var(112)),

                        // jump to subroutine
                        new LogicInstruction("set", "@counter", var(1001)),

                        // return address
                        new LogicInstruction("label", var(1003)),

                        // pop return value from stack
                        new LogicInstruction("set", var(115), "63"),
                        new LogicInstruction("read", var(116), "cell1", var(115)),
                        new LogicInstruction("set", var(114), var(116)),
                        new LogicInstruction("read", var(117), "cell1", var(114)),
                        new LogicInstruction("set", var(113), var(117)),
                        new LogicInstruction("set", var(118), "1"),
                        new LogicInstruction("op", "add", var(119), var(114), var(118)),
                        new LogicInstruction("set", var(114), var(119)),
                        new LogicInstruction("set", var(122), "63"),
                        new LogicInstruction("write", var(114), "cell1", var(122)),

                        // function body
                        new LogicInstruction("op", "pow", var(123), "n", var(113)),
                        new LogicInstruction("op", "mul", var(124), var(94), var(123)),

                        // pop return address
                        new LogicInstruction("set", var(127), "63"),
                        new LogicInstruction("read", var(128), "cell1", var(127)),
                        new LogicInstruction("set", var(126), var(128)),
                        new LogicInstruction("read", var(129), "cell1", var(126)),
                        new LogicInstruction("set", var(125), var(129)),
                        new LogicInstruction("set", var(130), "1"),
                        new LogicInstruction("op", "add", var(131), var(126), var(130)),
                        new LogicInstruction("set", var(126), var(131)),
                        new LogicInstruction("set", var(134), "63"),
                        new LogicInstruction("write", var(126), "cell1", var(134)),

                        // push return value
                        new LogicInstruction("set", var(136), "63"),
                        new LogicInstruction("read", var(137), "cell1", var(136)),
                        new LogicInstruction("set", var(135), var(137)),
                        new LogicInstruction("set", var(138), "1"),
                        new LogicInstruction("op", "sub", var(139), var(135), var(138)),
                        new LogicInstruction("set", var(135), var(139)),
                        new LogicInstruction("write", var(124), "cell1", var(135)),
                        new LogicInstruction("set", var(143), "63"),
                        new LogicInstruction("write", var(135), "cell1", var(143)),

                        // return
                        new LogicInstruction("set", "@counter", var(125)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("sensor", var(0), "leader", "@shootX"),
                        new LogicInstruction("sensor", var(1), "leader", "@shootY"),
                        new LogicInstruction("sensor", var(2), "leader", "@shooting"),
                        new LogicInstruction("control", "shoot", "turret", var(0), var(1), var(2)),
                        new LogicInstruction("set", var(3), "14"),
                        new LogicInstruction("set", var(4), "15"),
                        new LogicInstruction("set", var(5), "16"),
                        new LogicInstruction("control", "color", "turret", var(3), var(4), var(5)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", "HEAPPTR", "cell1"),
                        new LogicInstruction("set", var(2), "0"),
                        new LogicInstruction("set", var(3), "0"),
                        new LogicInstruction("write", var(2), "HEAPPTR", var(3)), // write $dx
                        new LogicInstruction("set", var(6), "1"),
                        new LogicInstruction("read", var(7), "HEAPPTR", var(6)), // read $dy
                        new LogicInstruction("set", var(8), "0"),
                        new LogicInstruction("read", var(9), "HEAPPTR", var(8)), // read $dx
                        new LogicInstruction("op", "add", var(10), var(7), var(9)), // tmp10 = $dx + $dy
                        new LogicInstruction("set", var(11), "1"),
                        new LogicInstruction("write", var(10), "HEAPPTR", var(11)), // set $dy
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", "STACKPTR", "cell1"),
                        new LogicInstruction("set", "HEAPPTR", "cell2"),

                        new LogicInstruction("set", var(2), "63"), // init stack
                        new LogicInstruction("set", var(3), "63"),
                        new LogicInstruction("write", var(2), "STACKPTR", var(3)),

                        new LogicInstruction("set", var(7), "63"), // push return address on stack
                        new LogicInstruction("read", var(8), "STACKPTR", var(7)),
                        new LogicInstruction("set", var(6), var(8)),
                        new LogicInstruction("set", var(9), "1"),
                        new LogicInstruction("op", "sub", var(10), var(6), var(9)),
                        new LogicInstruction("set", var(6), var(10)),
                        new LogicInstruction("write", var(1001), "STACKPTR", var(6)),
                        new LogicInstruction("set", var(14), "63"),
                        new LogicInstruction("write", var(6), "STACKPTR", var(14)),

                        new LogicInstruction("set", "@counter", var(1000)), // invoke function

                        new LogicInstruction("label", var(1001)),

                        new LogicInstruction("set", var(17), "63"), // pop return value from stack
                        new LogicInstruction("read", var(18), "STACKPTR", var(17)),
                        new LogicInstruction("set", var(16), var(18)),
                        new LogicInstruction("read", var(19), "STACKPTR", var(16)),
                        new LogicInstruction("set", var(15), var(19)),
                        new LogicInstruction("set", var(20), "1"),
                        new LogicInstruction("op", "add", var(21), var(16), var(20)),
                        new LogicInstruction("set", var(16), var(21)),
                        new LogicInstruction("set", var(24), "63"),
                        new LogicInstruction("write", var(16), "STACKPTR", var(24)),

                        new LogicInstruction("set", var(25), "0"), // write $dx
                        new LogicInstruction("write", var(15), "HEAPPTR", var(25)),

                        new LogicInstruction("end"), // end of main function body

                        new LogicInstruction("label", var(1000)), // generateUnoptimized of delay function

                        new LogicInstruction("set", var(26), "0"), // return value

                        new LogicInstruction("set", var(29), "63"), // pop return address from stack
                        new LogicInstruction("read", var(30), "STACKPTR", var(29)),
                        new LogicInstruction("set", var(28), var(30)),
                        new LogicInstruction("read", var(31), "STACKPTR", var(28)),
                        new LogicInstruction("set", var(27), var(31)),
                        new LogicInstruction("set", var(32), "1"),
                        new LogicInstruction("op", "add", var(33), var(28), var(32)),
                        new LogicInstruction("set", var(28), var(33)),
                        new LogicInstruction("set", var(36), "63"),
                        new LogicInstruction("write", var(28), "STACKPTR", var(36)),

                        new LogicInstruction("set", var(38), "63"), // push return value on stack
                        new LogicInstruction("read", var(39), "STACKPTR", var(38)),
                        new LogicInstruction("set", var(37), var(39)),
                        new LogicInstruction("set", var(40), "1"),
                        new LogicInstruction("op", "sub", var(41), var(37), var(40)),
                        new LogicInstruction("set", var(37), var(41)),
                        new LogicInstruction("write", var(26), "STACKPTR", var(37)),
                        new LogicInstruction("set", var(45), "63"),
                        new LogicInstruction("write", var(37), "STACKPTR", var(45)),

                        new LogicInstruction("set", "@counter", var(27)), // jump back to caller
                        new LogicInstruction("end")

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
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("op", "sqrt", var(1), var(0)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("op", "rand", var(1), var(0)),
                        new LogicInstruction("op", "floor", var(2), var(1)),
                        new LogicInstruction("set", "ast0", var(2)),
                        new LogicInstruction("set", var(5), "0"),
                        new LogicInstruction("jump", var(1001), "notEqual", var(3), var(5)),
                        new LogicInstruction("set", var(6), "1000"),
                        new LogicInstruction("set", var(4), var(6)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", var(7), "1"),
                        new LogicInstruction("jump", var(1002), "notEqual", var(3), var(7)),
                        new LogicInstruction("set", var(4), "null"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(4), "null"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("op", "max", var(1), "y", var(0)),
                        new LogicInstruction("op", "min", var(2), "x", var(1)),
                        new LogicInstruction("set", "r", var(2)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "9842"),
                        new LogicInstruction("set", var(1), "1"),
                        new LogicInstruction("op", "and", var(2), var(0), var(1)),
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("set", var(4), "4"),
                        new LogicInstruction("op", "shl", var(5), var(3), var(4)),
                        new LogicInstruction("op", "xor", var(6), var(2), var(5)),
                        new LogicInstruction("set", var(7), "1"),
                        new LogicInstruction("op", "shr", var(8), "y", var(7)),
                        new LogicInstruction("op", "or", var(9), var(6), var(8)),
                        new LogicInstruction("end")

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
                        new LogicInstruction("set", var(0), "40"),
                        new LogicInstruction("set", var(1), "40"),
                        new LogicInstruction("write", var(0), "cell3", var(1)),
                        new LogicInstruction("set", var(2), "99"),
                        new LogicInstruction("set", var(3), "41"),
                        new LogicInstruction("write", var(2), "cell3", var(3))
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
                        new LogicInstruction("set", var(0), "4"),
                        new LogicInstruction("set", var(1), "8"),
                        new LogicInstruction("op", "len", var(2), var(0), var(1)),
                        new LogicInstruction("set", "length", var(2)),
                        new LogicInstruction("set", var(3), "4"),
                        new LogicInstruction("set", var(4), "8"),
                        new LogicInstruction("op", "angle", var(5), var(3), var(4)),
                        new LogicInstruction("set", "angle", var(5)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "405"),
                        new LogicInstruction("op", "log10", var(1), var(0)),
                        new LogicInstruction("set", "l", var(1)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "4"),
                        new LogicInstruction("set", var(1), "8"),
                        new LogicInstruction("op", "noise", var(2), var(0), var(1)),
                        new LogicInstruction("set", "n", var(2)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "4"),
                        new LogicInstruction("set", var(1), "8"),
                        new LogicInstruction("op", "idiv", var(2), var(0), var(1)),
                        new LogicInstruction("set", "n", var(2)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", "ast0", "n"),
                        new LogicInstruction("set", var(1), "2"),
                        new LogicInstruction("jump", var(1001), "notEqual", "ast0", var(1)),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("set", var(0), "n"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", var(0), "null"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("jump", var(1000), "notEqual", "n", "true"),
                        new LogicInstruction("set", var(0), "null"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(1), "1"),
                        new LogicInstruction("set", var(0), var(1)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("jump", var(1002), "notEqual", "m", "true"),
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("set", var(2), var(3)),
                        new LogicInstruction("jump", var(1003), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(2), "null"),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", "resource", "@silicon"),
                        new LogicInstruction("sensor", var(0), "vault1", "resource"),
                        new LogicInstruction("sensor", var(1), "vault1", "@itemCapacity"),
                        new LogicInstruction("op", "lessThan", var(2), var(0), var(1)),
                        new LogicInstruction("jump", var(1000), "notEqual", var(2), "true"),
                        new LogicInstruction("set", "foo", "true"),
                        new LogicInstruction("set", var(3), "true"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(3), "null"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("sensor", var(0), "vault1", "@graphite"),
                        new LogicInstruction("sensor", var(1), "vault1", "@itemCapacity"),
                        new LogicInstruction("op", "lessThan", var(2), var(0), var(1)),
                        new LogicInstruction("control", "enabled", "conveyor1", var(2)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "\"\\nsm.enabled: \""),
                        new LogicInstruction("sensor", var(1), "smelter1", "@enabled"),
                        new LogicInstruction("jump", var(1000), "notEqual", var(1), "true"),
                        new LogicInstruction("set", var(3), "\"true\""),
                        new LogicInstruction("set", var(2), var(3)),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(4), "\"false\""),
                        new LogicInstruction("set", var(2), var(4)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("print", var(0)),
                        new LogicInstruction("print", var(2)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateUnoptimized(
                        (Seq) translateToAst(
                                "print(\"\\nsm.enabled: \", smelter1.enabled ? \"true\" : \"false\")"
                        )
                )
        );
    }
}
