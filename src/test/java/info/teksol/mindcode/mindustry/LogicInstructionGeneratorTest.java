package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void convertsComplexAssignment() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "2"),
                        new LogicInstruction("op", "sub", match(1), "bar", match(0)),
                        new LogicInstruction("set", match(2), "3"),
                        new LogicInstruction("op", "mul", match(3), match(1), match(2)),
                        new LogicInstruction("set", "foo", match(3)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("foo = (bar - 2) * 3"))
        );
    }

    @Test
    void convertsWhileLoopAndPrintFunctionCall() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "0"),
                        new LogicInstruction("set", "n", match(0)),
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(1), "5"),
                        new LogicInstruction("op", "lessThan", match(2), "n", match(1)),
                        new LogicInstruction("jump", "label1", "notEqual", match(2), "true"),
                        new LogicInstruction("set", match(3), "1"),
                        new LogicInstruction("op", "add", match(4), "n", match(3)),
                        new LogicInstruction("set", "n", match(4)),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("set", match(5), "\"n: \""),
                        new LogicInstruction("print", match(5)),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("n = 0\nwhile n < 5\nn += 1\nend\nprint(\"n: \", n)"))
        );
    }

    @Test
    void convertsNullAndUnaryOp() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("op", "not", match(0), "n"),
                        new LogicInstruction("set", "n", match(0)),
                        new LogicInstruction("set", "x", "null"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("n = not n\nx = null"))
        );
    }

    @Test
    void convertsSensorReadings() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("sensor", match(0), "foundation1", "@copper"),
                        new LogicInstruction("sensor", match(1), "foundation1", "@itemCapacity"),
                        new LogicInstruction("op", "lessThan", match(2), match(0), match(1)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("foundation1.copper < foundation1.itemCapacity"))
        );
    }

    @Test
    void convertsBooleanOperations() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("op", "notEqual", match(0), "true", "false"),
                        new LogicInstruction("jump", "label1", "notEqual", match(0), "true"),
                        new LogicInstruction("set", match(1), "\"infinite loop!\""),
                        new LogicInstruction("print", match(1)),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("while true != false\nprint(\"infinite loop!\")\nend\nprintflush(message1)\n")
                )
        );
    }

    @Test
    void convertsControlStatements() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("sensor", match(1), "foundation1", "@copper"),
                        new LogicInstruction("sensor", match(2), "tank1", "@water"),
                        new LogicInstruction("op", "strictEqual", match(3), match(1), match(2)),
                        new LogicInstruction("control", "enabled", "conveyor1", match(3)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("conveyor1.enabled = foundation1.copper === tank1.water")
                )
        );
    }

    @Test
    void convertsHeapAccesses() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(2), "4"),
                        new LogicInstruction("read", match(3), "cell2", match(2)),
                        new LogicInstruction("sensor", match(4), "conveyor1", "@enabled"),
                        new LogicInstruction("op", "add", match(5), match(3), match(4)),
                        new LogicInstruction("set", match(6), "3"),
                        new LogicInstruction("write", match(5), "cell1", match(6)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("cell1[3] = cell2[4] + conveyor1.enabled")
                )
        );
    }

    @Test
    void convertsIfExpression() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "4"),
                        new LogicInstruction("read", match(1), "cell1", match(0)),
                        new LogicInstruction("set", match(2), "0"),
                        new LogicInstruction("op", "equal", match(3), match(1), match(2)),
                        new LogicInstruction("jump", "label0", "notEqual", match(3), "true"),
                        new LogicInstruction("set", match(9), "false"),
                        new LogicInstruction("jump", "label1", "always"),
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(6), "4"),
                        new LogicInstruction("write", "true", "cell1", match(6)),
                        new LogicInstruction("set", match(7), "1"),
                        new LogicInstruction("op", "add", match(8), "n", match(7)),
                        new LogicInstruction("set", "n", match(8)),
                        new LogicInstruction("set", match(9), match(8)),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("set", "value", match(9)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "value = if cell1[4] == 0\nfalse\nelse\ncell1[4] = true\nn += 1\nend\n"
                        )
                )
        );
    }

    @Test
    void convertsFunctionsReturningValues() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(2), "9"),
                        new LogicInstruction("set", match(3), "9"),
                        new LogicInstruction("op", "pow", match(4), match(2), match(3)),
                        new LogicInstruction("op", "rand", match(5), match(4)),
                        new LogicInstruction("set", match(6), "0"),
                        new LogicInstruction("write", match(5), "cell1", match(6)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom((Seq) translateToAst("cell1[0] = rand(9**9)"))
        );
    }

    @Test
    void convertsUbindAndControl() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("op", "strictEqual", match(0), "@unit", "null"),
                        new LogicInstruction("jump", "label1", "notEqual", match(0), "true"),
                        new LogicInstruction("ubind", "@poly"),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "while @unit === null\nubind(@poly)\nend\n")
                )
        );

    }

    @Test
    void convertsReallifeTest1() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "0"),
                        new LogicInstruction("set", "n", match(0)),
                        new LogicInstruction("label", match(1002)),
                        new LogicInstruction("getlink", match(1), "n"),
                        new LogicInstruction("set", "reactor", match(1)),
                        new LogicInstruction("op", "notEqual", match(2), match(1), "null"),
                        new LogicInstruction("jump", match(1003), "notEqual", match(2), "true"),
                        new LogicInstruction("sensor", match(3), "reactor", "@liquidCapacity"),
                        new LogicInstruction("set", match(4), "0"),
                        new LogicInstruction("op", "greaterThan", match(5), match(3), match(4)),
                        new LogicInstruction("jump", match(1000), "notEqual", match(5), "true"),
                        new LogicInstruction("sensor", match(6), "reactor", "@cryofluid"),
                        new LogicInstruction("sensor", match(7), "reactor", "@liquidCapacity"),
                        new LogicInstruction("op", "div", match(8), match(6), match(7)),
                        new LogicInstruction("set", "pct_avail", match(8)),
                        new LogicInstruction("set", match(10), "0.25"),
                        new LogicInstruction("op", "greaterThanEq", match(11), "pct_avail", match(10)),
                        new LogicInstruction("control", "enabled", "reactor", match(11)),
                        new LogicInstruction("set", match(12), match(11)),
                        new LogicInstruction("jump", match(1001), "always"),
                        new LogicInstruction("label", match(1000)),
                        new LogicInstruction("set", match(12), "null"),
                        new LogicInstruction("label", match(1001)),
                        new LogicInstruction("set", match(13), "1"),
                        new LogicInstruction("op", "add", match(14), "n", match(13)),
                        new LogicInstruction("set", "n", match(14)),
                        new LogicInstruction("jump", match(1002), "always"),
                        new LogicInstruction("label", match(1003)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
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
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "-1"),
                        new LogicInstruction("op", "mul", match(1), "dx", match(0)),
                        new LogicInstruction("set", "dx", match(1)),
                        new LogicInstruction("set", match(2), "-1"),
                        new LogicInstruction("set", "dy", match(2)),
                        new LogicInstruction("set", match(3), "2"),
                        new LogicInstruction("set", match(4), "1"),
                        new LogicInstruction("op", "sub", match(5), match(3), match(4)),
                        new LogicInstruction("set", "dz", match(5)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "dx *= -1\ndy = -1\ndz = 2 - 1"
                        )
                )
        );
    }

    @Test
    void removesCommentsFromLogicInstructions() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "1"),
                        new LogicInstruction("set", "a", match(0)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "// Remember that we initialized ourselves\n\na = 1"
                        )
                )
        );
    }

    @Test
    void generatesRefsWithDashInThem() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "1"),
                        new LogicInstruction("set", match(1), "0"),
                        new LogicInstruction("ucontrol", "build", "x", "y", "@titanium-conveyor", match(0), match(1)),
                        new LogicInstruction("ucontrol", "getBlock", "x", "y", "b_type", "b_building"),
                        new LogicInstruction("op", "equal", match(2), "b_type", "@titanium-conveyor"),
                        new LogicInstruction("jump", "label0", "notEqual", match(2), "true"),
                        new LogicInstruction("set", match(3), "1"),
                        new LogicInstruction("op", "add", match(4), "n", match(3)),
                        new LogicInstruction("set", "n", match(4)),
                        new LogicInstruction("set", match(5), match(4)),
                        new LogicInstruction("jump", "label1", "always"),
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(5), "null"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst(
                                "build(x, y, @titanium-conveyor, 1, 0)\ngetBlock(x, y, b_type, b_building)\nif b_type == @titanium-conveyor\nn += 1\nend\n"
                        )
                )
        );
    }

    @Test
    void generatesComplexMathExpression() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "1"),
                        new LogicInstruction("op", "rand", match(1), match(0)),
                        new LogicInstruction("op", "tan", match(2), match(1)),
                        new LogicInstruction("op", "abs", match(3), match(2)),
                        new LogicInstruction("op", "cos", match(4), match(3)),
                        new LogicInstruction("op", "log", match(5), match(4)),
                        new LogicInstruction("op", "sin", match(6), match(5)),
                        new LogicInstruction("op", "floor", match(7), match(6)),
                        new LogicInstruction("op", "ceil", match(8), match(7)),
                        new LogicInstruction("set", "x", match(8)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
                )
        );
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "1"),
                        new LogicInstruction("set", "n", match(0)),
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(1), "17"),
                        new LogicInstruction("op", "lessThanEq", match(2), "n", match(1)),
                        new LogicInstruction("jump", "label1", "notEqual", match(2), "true"),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("set", match(3), "1"),
                        new LogicInstruction("op", "add", match(4), "n", match(3)),
                        new LogicInstruction("set", "n", match(4)),
                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("for n in 1 .. 17\nprint(n)\nend")
                )
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertLogicInstructionListMatches(
                List.of(
                        // init
                        new LogicInstruction("set", match(0), "1"),
                        new LogicInstruction("set", "n", match(0)),

                        // cond
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(1), "17"),
                        new LogicInstruction("op", "lessThan", match(2), "n", match(1)),
                        new LogicInstruction("jump", "label1", "notEqual", match(2), "true"),

                        // loop body
                        new LogicInstruction("print", "n"),

                        // increment
                        new LogicInstruction("set", match(3), "1"),
                        new LogicInstruction("op", "add", match(4), "n", match(3)),
                        new LogicInstruction("set", "n", match(4)),

                        // loop
                        new LogicInstruction("jump", "label0", "always"),

                        // trailer
                        new LogicInstruction("label", "label1"),

                        // rest of program
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("for n in 1 ... 17\nprint(n)\nend\n")
                )
        );
    }

    @Test
    void generatesCStyleComplexForLoop() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "0"),
                        new LogicInstruction("set", "i", match(0)),
                        new LogicInstruction("set", match(1), "-5"),
                        new LogicInstruction("set", "j", match(1)),

                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(2), "5"),
                        new LogicInstruction("op", "lessThan", match(3), "i", match(2)),
                        new LogicInstruction("jump", "label1", "notEqual", match(3), "true"),
                        new LogicInstruction("print", "n"),

                        new LogicInstruction("set", match(4), "1"),
                        new LogicInstruction("op", "sub", match(5), "j", match(4)),
                        new LogicInstruction("set", "j", match(5)),

                        new LogicInstruction("set", match(6), "1"),
                        new LogicInstruction("op", "add", match(7), "i", match(6)),
                        new LogicInstruction("set", "i", match(7)),

                        new LogicInstruction("jump", "label0", "always"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
                )
        );
    }

    @Test
    void supportsAssigningAssignmentResults() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "42"),
                        new LogicInstruction("set", "b", match(0)),
                        new LogicInstruction("set", "a", match(0)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("a = b = 42")
                )
        );
    }

    @Test
    void generatesCaseWhenElse() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", "ast0", "n"),
                        new LogicInstruction("set", match(2), "1"),
                        new LogicInstruction("jump", match(1001), "notEqual", match(0), match(2)),
                        new LogicInstruction("set", match(3), "\"1\""),
                        new LogicInstruction("set", match(1), match(3)),
                        new LogicInstruction("jump", match(1000), "always"),
                        new LogicInstruction("label", match(1001)),
                        new LogicInstruction("set", match(4), "2"),
                        new LogicInstruction("jump", match(1002), "notEqual", match(0), match(4)),
                        new LogicInstruction("set", match(5), "\"two\""),
                        new LogicInstruction("set", match(1), match(5)),
                        new LogicInstruction("jump", match(1000), "always"),
                        new LogicInstruction("label", match(1002)),
                        new LogicInstruction("set", match(6), "\"otherwise\""),
                        new LogicInstruction("set", match(1), match(6)),
                        new LogicInstruction("label", match(1000)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("case n\nwhen 1\n\"1\"\nwhen 2\n\"two\"\nelse\n\"otherwise\"end\n")
                )
        );
    }

    @Test
    void generatesCaseWhen() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "0"),
                        new LogicInstruction("read", match(1), "cell1", match(0)),
                        new LogicInstruction("set", "ast0", match(1)),
                        new LogicInstruction("jump", match(1001), "notEqual", match(2), "ST_EMPTY"),
                        new LogicInstruction("set", match(6), "0"),
                        new LogicInstruction("write", "ST_INITIALIZED", "cell1", match(6)),
                        new LogicInstruction("set", match(3), "ST_INITIALIZED"),
                        new LogicInstruction("jump", match(1000), "always"),
                        new LogicInstruction("label", match(1001)),
                        new LogicInstruction("jump", match(1002), "notEqual", match(2), "ST_INITIALIZED"),
                        new LogicInstruction("set", match(9), "0"),
                        new LogicInstruction("write", "ST_DONE", "cell1", match(9)),
                        new LogicInstruction("set", match(3), "ST_DONE"),
                        new LogicInstruction("jump", match(1000), "always"),
                        new LogicInstruction("label", match(1002)),
                        new LogicInstruction("set", match(3), "null"),
                        new LogicInstruction("label", match(1000)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("allocate heap in cell1[0..10]\ncase $state\nwhen ST_EMPTY\n$state = ST_INITIALIZED\nwhen ST_INITIALIZED\n$state = ST_DONE\nend\n")
                )
        );
    }

    @Test
    void generatesDrawings() {
        assertLogicInstructionListMatches(
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
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("clear(r, g, b)\ncolor(r, g, b, alpha)\nstroke(width)\nline(x1, y1, x2, y2)\nrect(x, y, w, h)\nlineRect(x, y, w, h)\npoly(x, y, sides, radius, rotation)\nlinePoly(x, y, sides, radius, rotation)\ntriangle(x1, y1, x2, y2, x3, y3)\nimage(x, y, @copper, size, rotation)\ndrawflush(display1)\n")
                )
        );
    }

    @Test
    void generatesURadar() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("uradar", "enemy", "ground", "any", "health", "MIN_TO_MAX", "BY_DISTANCE", match(0)),
                        new LogicInstruction("set", "target", match(0)),
                        new LogicInstruction("op", "notEqual", match(1), "target", "null"),
                        new LogicInstruction("jump", match(1002), "notEqual", match(1), "true"),
                        new LogicInstruction("sensor", match(2), "target", "@x"),
                        new LogicInstruction("sensor", match(3), "target", "@y"),
                        new LogicInstruction("set", match(4), "10"),
                        new LogicInstruction("ucontrol", "approach", match(2), match(3), match(4)),
                        new LogicInstruction("sensor", match(5), "target", "@x"),
                        new LogicInstruction("sensor", match(6), "target", "@y"),
                        new LogicInstruction("set", match(7), "10"),
                        new LogicInstruction("ucontrol", "within", match(5), match(6), match(7), match(8)),
                        new LogicInstruction("jump", match(1000), "notEqual", match(8), "true"),
                        new LogicInstruction("sensor", match(9), "target", "@x"),
                        new LogicInstruction("sensor", match(10), "target", "@y"),
                        new LogicInstruction("ucontrol", "target", match(9), match(10), "SHOOT"),
                        new LogicInstruction("set", match(11), "null"),
                        new LogicInstruction("jump", match(1001), "always"),
                        new LogicInstruction("label", match(1000)),
                        new LogicInstruction("set", match(11), "null"),
                        new LogicInstruction("label", match(1001)),
                        new LogicInstruction("set", match(12), match(11)),
                        new LogicInstruction("jump", match(1003), "always"),
                        new LogicInstruction("label", match(1002)),
                        new LogicInstruction("set", match(12), "null"),
                        new LogicInstruction("label", match(1003)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
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
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("ulocate", "ore", "core", "true", "@surge-alloy", "outx", "outy", match(0), match(1)),
                        new LogicInstruction("ulocate", "building", "core", "ENEMY", "@copper", "outx", "outy", match(2), "outbuilding"),
                        new LogicInstruction("ulocate", "spawn", "core", "true", "@copper", "outx", "outy", match(3), "outbuilding"),
                        new LogicInstruction("ulocate", "damaged", "core", "true", "@copper", "outx", "outy", match(4), "outbuilding"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
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
    void generatesEndFromFunctionCall() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("op", "equal", match(0), "some_cond", "false"),
                        new LogicInstruction("jump", "label0", "notEqual", match(0), "true"),
                        new LogicInstruction("end"),
                        new LogicInstruction("set", match(1), "null"),
                        new LogicInstruction("jump", "label1", "always"),
                        new LogicInstruction("label", "label0"),
                        new LogicInstruction("set", match(1), "null"),
                        new LogicInstruction("label", "label1"),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "if some_cond == false\n  end()\nend"
                        )
                )
        );
    }

    @Test
    void generatesModuloOperator() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "2"),
                        new LogicInstruction("op", "mod", match(1), "@tick", match(0)),
                        new LogicInstruction("set", match(2), "0"),
                        new LogicInstruction("op", "equal", match(3), match(1), match(2)),
                        new LogicInstruction("set", "running", match(3)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "running = @tick % 2 == 0"
                        )
                )
        );
    }

    @Test
    void refusesToDeclareFunctionsWhenNoStackAround() {
        assertThrows(MissingStackException.class, () ->
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "def foo\n0\nend\n\n\nfoo()\n"
                        )
                )
        );
    }

    @Test
    void generatesCodeForFunctionCallingAndReturn() {
        assertLogicInstructionListMatches(
                List.of(
                        // setup stack
                        new LogicInstruction("set", match(2), "63"),
                        new LogicInstruction("set", match(3), "63"),
                        new LogicInstruction("write", match(2), "cell1", match(3)),

                        // push return address on stack
                        new LogicInstruction("set", match(5), "63"),
                        new LogicInstruction("read", match(6), "cell1", match(5)), // read stack pointer
                        new LogicInstruction("set", match(4), match(6)),
                        new LogicInstruction("set", match(7), "1"),
                        new LogicInstruction("op", "sub", match(8), match(4), match(7)), // calculate new stack pointer
                        new LogicInstruction("set", match(4), match(8)),
                        new LogicInstruction("write", match(1001), "cell1", match(4)), // write value on stack, at stack pointer
                        new LogicInstruction("set", match(12), "63"),
                        new LogicInstruction("write", match(4), "cell1", match(12)), // update stack pointer itself

                        // jump to function
                        new LogicInstruction("set", "@counter", match(1000)),

                        // return label
                        new LogicInstruction("label", match(1001)),

                        // pop return value from stack
                        new LogicInstruction("set", match(15), "63"),
                        new LogicInstruction("read", match(16), "cell1", match(15)), // read stack pointer
                        new LogicInstruction("set", match(14), match(16)),
                        new LogicInstruction("read", match(17), "cell1", match(14)), // read value on stack, at stack pointer
                        new LogicInstruction("set", match(13), match(17)),
                        new LogicInstruction("set", match(18), "1"),
                        new LogicInstruction("op", "add", match(19), match(14), match(18)), // calculate new stack pointer
                        new LogicInstruction("set", match(14), match(19)),
                        new LogicInstruction("set", match(22), "63"),
                        new LogicInstruction("write", match(14), "cell1", match(22)), // update stack pointer itself

                        // continue rest of main script
                        new LogicInstruction("set", "x", match(13)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("printflush", "message1"),

                        // end of main script
                        new LogicInstruction("end"),

                        // start of function foo
                        new LogicInstruction("label", match(1000)),

                        // no parameters to pop
                        new LogicInstruction("set", match(23), "0"),

                        // pop return address
                        new LogicInstruction("set", match(26), "63"),
                        new LogicInstruction("read", match(27), "cell1", match(26)),
                        new LogicInstruction("set", match(25), match(27)),
                        new LogicInstruction("read", match(28), "cell1", match(25)),
                        new LogicInstruction("set", match(24), match(28)),
                        new LogicInstruction("set", match(29), "1"),
                        new LogicInstruction("op", "add", match(30), match(25), match(29)),
                        new LogicInstruction("set", match(25), match(30)),
                        new LogicInstruction("set", match(33), "63"),
                        new LogicInstruction("write", match(25), "cell1", match(33)),

                        // push return value
                        new LogicInstruction("set", match(35), "63"),
                        new LogicInstruction("read", match(36), "cell1", match(35)),
                        new LogicInstruction("set", match(34), match(36)),
                        new LogicInstruction("set", match(37), "1"),
                        new LogicInstruction("op", "sub", match(38), match(34), match(37)),
                        new LogicInstruction("set", match(34), match(38)),
                        new LogicInstruction("write", match(23), "cell1", match(34)),
                        new LogicInstruction("set", match(42), "63"),
                        new LogicInstruction("write", match(34), "cell1", match(42)),

                        // jump to return address
                        new LogicInstruction("set", "@counter", match(24)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "allocate stack in cell1\ndef foo\n0\nend\n\n\nx = foo()\nprint(x)\nprintflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void passesParametersToFunction() {
        assertLogicInstructionListMatches(
                List.of(
                        // setup stack
                        new LogicInstruction("set", match(2), "63"),
                        new LogicInstruction("set", match(3), "63"),
                        new LogicInstruction("write", match(2), "cell1", match(3)),

                        // push return address on stack
                        new LogicInstruction("set", match(4), "4"),
                        new LogicInstruction("set", "boo", match(4)),
                        new LogicInstruction("set", match(5), "2"),

                        // push return address on stack
                        new LogicInstruction("set", match(7), "63"),
                        new LogicInstruction("read", match(8), "cell1", match(7)),
                        new LogicInstruction("set", match(6), match(8)),
                        new LogicInstruction("set", match(9), "1"),
                        new LogicInstruction("op", "sub", match(10), match(6), match(9)),
                        new LogicInstruction("set", match(6), match(10)),
                        new LogicInstruction("write", "label1", "cell1", match(6)),
                        new LogicInstruction("set", match(14), "63"),
                        new LogicInstruction("write", match(6), "cell1", match(14)),

                        // push n on stack
                        new LogicInstruction("set", match(16), "63"),
                        new LogicInstruction("read", match(17), "cell1", match(16)),
                        new LogicInstruction("set", match(15), match(17)),
                        new LogicInstruction("set", match(18), "1"),
                        new LogicInstruction("op", "sub", match(19), match(15), match(18)),
                        new LogicInstruction("set", match(15), match(19)),
                        new LogicInstruction("write", match(5), "cell1", match(15)),
                        new LogicInstruction("set", match(23), "63"),
                        new LogicInstruction("write", match(15), "cell1", match(23)),

                        // push r on stack
                        new LogicInstruction("set", match(25), "63"),
                        new LogicInstruction("read", match(26), "cell1", match(25)),
                        new LogicInstruction("set", match(24), match(26)),
                        new LogicInstruction("set", match(27), "1"),
                        new LogicInstruction("op", "sub", match(28), match(24), match(27)),
                        new LogicInstruction("set", match(24), match(28)),
                        new LogicInstruction("write", "boo", "cell1", match(24)),
                        new LogicInstruction("set", match(32), "63"),
                        new LogicInstruction("write", match(24), "cell1", match(32)),

                        // jump to function
                        new LogicInstruction("set", "@counter", "label0"),

                        // function return address
                        new LogicInstruction("label", "label1"),

                        // pop return value from stack
                        new LogicInstruction("set", match(35), "63"),
                        new LogicInstruction("read", match(36), "cell1", match(35)),
                        new LogicInstruction("set", match(34), match(36)),
                        new LogicInstruction("read", match(37), "cell1", match(34)),
                        new LogicInstruction("set", match(33), match(37)),
                        new LogicInstruction("set", match(38), "1"),
                        new LogicInstruction("op", "add", match(39), match(34), match(38)),
                        new LogicInstruction("set", match(34), match(39)),
                        new LogicInstruction("set", match(42), "63"),
                        new LogicInstruction("write", match(34), "cell1", match(42)),

                        // continue main body
                        new LogicInstruction("set", "x", match(33)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end"),

                        // function foo
                        new LogicInstruction("label", "label0"),

                        // pop r
                        new LogicInstruction("set", match(45), "63"),
                        new LogicInstruction("read", match(46), "cell1", match(45)),
                        new LogicInstruction("set", match(44), match(46)),
                        new LogicInstruction("read", match(47), "cell1", match(44)),
                        new LogicInstruction("set", match(43), match(47)),
                        new LogicInstruction("set", match(48), "1"),
                        new LogicInstruction("op", "add", match(49), match(44), match(48)),
                        new LogicInstruction("set", match(44), match(49)),
                        new LogicInstruction("set", match(52), "63"),
                        new LogicInstruction("write", match(44), "cell1", match(52)),
                        new LogicInstruction("set", "r", match(43)),

                        // pop n
                        new LogicInstruction("set", match(55), "63"),
                        new LogicInstruction("read", match(56), "cell1", match(55)),
                        new LogicInstruction("set", match(54), match(56)),
                        new LogicInstruction("read", match(57), "cell1", match(54)),
                        new LogicInstruction("set", match(53), match(57)),
                        new LogicInstruction("set", match(58), "1"),
                        new LogicInstruction("op", "add", match(59), match(54), match(58)),
                        new LogicInstruction("set", match(54), match(59)),
                        new LogicInstruction("set", match(62), "63"),
                        new LogicInstruction("write", match(54), "cell1", match(62)),
                        new LogicInstruction("set", "n", match(53)),

                        // execute function body
                        new LogicInstruction("set", match(63), "2"),
                        new LogicInstruction("op", "pow", match(64), "n", "r"),
                        new LogicInstruction("op", "mul", match(65), match(63), match(64)),

                        // pop return address from stack
                        new LogicInstruction("set", match(68), "63"),
                        new LogicInstruction("read", match(69), "cell1", match(68)),
                        new LogicInstruction("set", match(67), match(69)),
                        new LogicInstruction("read", match(70), "cell1", match(67)),
                        new LogicInstruction("set", match(66), match(70)),
                        new LogicInstruction("set", match(71), "1"),
                        new LogicInstruction("op", "add", match(72), match(67), match(71)),
                        new LogicInstruction("set", match(67), match(72)),
                        new LogicInstruction("set", match(75), "63"),
                        new LogicInstruction("write", match(67), "cell1", match(75)),

                        // push return value on stack
                        new LogicInstruction("set", match(77), "63"),
                        new LogicInstruction("read", match(78), "cell1", match(77)),
                        new LogicInstruction("set", match(76), match(78)),
                        new LogicInstruction("set", match(79), "1"),
                        new LogicInstruction("op", "sub", match(80), match(76), match(79)),
                        new LogicInstruction("set", match(76), match(80)),
                        new LogicInstruction("write", match(65), "cell1", match(76)),
                        new LogicInstruction("set", match(84), "63"),
                        new LogicInstruction("write", match(76), "cell1", match(84)),

                        // jump back to caller
                        new LogicInstruction("set", "@counter", match(66)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "allocate stack in cell1\ndef foo(n, r)\n2 * (n ** r)\nend\n\n\nboo = 4\nx = foo(2, boo)\nprint(x)\nprintflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void functionsCanCallOtherFunctions() {
        assertLogicInstructionListMatches(
                List.of(
                        // setup stack
                        new LogicInstruction("set", match(2), "63"),
                        new LogicInstruction("set", match(3), "63"),
                        new LogicInstruction("write", match(2), "cell1", match(3)),

                        // prepare parameters
                        new LogicInstruction("set", match(4), "8"),
                        new LogicInstruction("set", "boo", match(4)),
                        new LogicInstruction("set", match(5), "7"),

                        // push return address
                        new LogicInstruction("set", match(7), "63"),
                        new LogicInstruction("read", match(8), "cell1", match(7)),
                        new LogicInstruction("set", match(6), match(8)),
                        new LogicInstruction("set", match(9), "1"),
                        new LogicInstruction("op", "sub", match(10), match(6), match(9)),
                        new LogicInstruction("set", match(6), match(10)),
                        new LogicInstruction("write", "label2", "cell1", match(6)),
                        new LogicInstruction("set", match(14), "63"),
                        new LogicInstruction("write", match(6), "cell1", match(14)),

                        // push n
                        new LogicInstruction("set", match(16), "63"),
                        new LogicInstruction("read", match(17), "cell1", match(16)),
                        new LogicInstruction("set", match(15), match(17)),
                        new LogicInstruction("set", match(18), "1"),
                        new LogicInstruction("op", "sub", match(19), match(15), match(18)),
                        new LogicInstruction("set", match(15), match(19)),
                        new LogicInstruction("write", match(5), "cell1", match(15)),
                        new LogicInstruction("set", match(23), "63"),
                        new LogicInstruction("write", match(15), "cell1", match(23)),

                        // push r
                        new LogicInstruction("set", match(25), "63"),
                        new LogicInstruction("read", match(26), "cell1", match(25)),
                        new LogicInstruction("set", match(24), match(26)),
                        new LogicInstruction("set", match(27), "1"),
                        new LogicInstruction("op", "sub", match(28), match(24), match(27)),
                        new LogicInstruction("set", match(24), match(28)),
                        new LogicInstruction("write", "boo", "cell1", match(24)),
                        new LogicInstruction("set", match(32), "63"),
                        new LogicInstruction("write", match(24), "cell1", match(32)),

                        // jump to function
                        new LogicInstruction("set", "@counter", "label0"),

                        // return address
                        new LogicInstruction("label", "label2"),

                        // pop return value from stack
                        new LogicInstruction("set", match(35), "63"),
                        new LogicInstruction("read", match(36), "cell1", match(35)),
                        new LogicInstruction("set", match(34), match(36)),
                        new LogicInstruction("read", match(37), "cell1", match(34)),
                        new LogicInstruction("set", match(33), match(37)),
                        new LogicInstruction("set", match(38), "1"),
                        new LogicInstruction("op", "add", match(39), match(34), match(38)),
                        new LogicInstruction("set", match(34), match(39)),
                        new LogicInstruction("set", match(42), "63"),
                        new LogicInstruction("write", match(34), "cell1", match(42)),

                        // rest of main body
                        new LogicInstruction("set", "x", match(33)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end"),

                        // def bar
                        new LogicInstruction("label", "label1"),

                        // pop bar.x
                        new LogicInstruction("set", match(45), "63"),
                        new LogicInstruction("read", match(46), "cell1", match(45)),
                        new LogicInstruction("set", match(44), match(46)),
                        new LogicInstruction("read", match(47), "cell1", match(44)),
                        new LogicInstruction("set", match(43), match(47)),
                        new LogicInstruction("set", match(48), "1"),
                        new LogicInstruction("op", "add", match(49), match(44), match(48)),
                        new LogicInstruction("set", match(44), match(49)),
                        new LogicInstruction("set", match(52), "63"),
                        new LogicInstruction("write", match(44), "cell1", match(52)),
                        new LogicInstruction("set", "x", match(43)),

                        // function body
                        new LogicInstruction("set", match(53), "2"),
                        new LogicInstruction("op", "mul", match(54), match(53), "x"),

                        // pop return address
                        new LogicInstruction("set", match(57), "63"),
                        new LogicInstruction("read", match(58), "cell1", match(57)),
                        new LogicInstruction("set", match(56), match(58)),
                        new LogicInstruction("read", match(59), "cell1", match(56)),
                        new LogicInstruction("set", match(55), match(59)),
                        new LogicInstruction("set", match(60), "1"),
                        new LogicInstruction("op", "add", match(61), match(56), match(60)),
                        new LogicInstruction("set", match(56), match(61)),
                        new LogicInstruction("set", match(64), "63"),
                        new LogicInstruction("write", match(56), "cell1", match(64)),

                        // push return value
                        new LogicInstruction("set", match(66), "63"),
                        new LogicInstruction("read", match(67), "cell1", match(66)),
                        new LogicInstruction("set", match(65), match(67)),
                        new LogicInstruction("set", match(68), "1"),
                        new LogicInstruction("op", "sub", match(69), match(65), match(68)),
                        new LogicInstruction("set", match(65), match(69)),
                        new LogicInstruction("write", match(54), "cell1", match(65)),
                        new LogicInstruction("set", match(73), "63"),
                        new LogicInstruction("write", match(65), "cell1", match(73)),

                        // return
                        new LogicInstruction("set", "@counter", match(55)),
                        new LogicInstruction("end"),

                        // def foo
                        new LogicInstruction("label", "label0"),

                        // pop foo.r
                        new LogicInstruction("set", match(76), "63"),
                        new LogicInstruction("read", match(77), "cell1", match(76)),
                        new LogicInstruction("set", match(75), match(77)),
                        new LogicInstruction("read", match(78), "cell1", match(75)),
                        new LogicInstruction("set", match(74), match(78)),
                        new LogicInstruction("set", match(79), "1"),
                        new LogicInstruction("op", "add", match(80), match(75), match(79)),
                        new LogicInstruction("set", match(75), match(80)),
                        new LogicInstruction("set", match(83), "63"),
                        new LogicInstruction("write", match(75), "cell1", match(83)),
                        new LogicInstruction("set", "r", match(74)),

                        // pop foo.n
                        new LogicInstruction("set", match(86), "63"),
                        new LogicInstruction("read", match(87), "cell1", match(86)),
                        new LogicInstruction("set", match(85), match(87)),
                        new LogicInstruction("read", match(88), "cell1", match(85)),
                        new LogicInstruction("set", match(84), match(88)),
                        new LogicInstruction("set", match(89), "1"),
                        new LogicInstruction("op", "add", match(90), match(85), match(89)),
                        new LogicInstruction("set", match(85), match(90)),
                        new LogicInstruction("set", match(93), "63"),
                        new LogicInstruction("write", match(85), "cell1", match(93)),
                        new LogicInstruction("set", "n", match(84)),

                        // function body
                        new LogicInstruction("set", match(94), "2"),

                        // push return address
                        new LogicInstruction("set", match(96), "63"),
                        new LogicInstruction("read", match(97), "cell1", match(96)),
                        new LogicInstruction("set", match(95), match(97)),
                        new LogicInstruction("set", match(98), "1"),
                        new LogicInstruction("op", "sub", match(99), match(95), match(98)),
                        new LogicInstruction("set", match(95), match(99)),
                        new LogicInstruction("write", "label3", "cell1", match(95)),
                        new LogicInstruction("set", match(103), "63"),
                        new LogicInstruction("write", match(95), "cell1", match(103)),

                        // push bar.x
                        new LogicInstruction("set", match(105), "63"),
                        new LogicInstruction("read", match(106), "cell1", match(105)),
                        new LogicInstruction("set", match(104), match(106)),
                        new LogicInstruction("set", match(107), "1"),
                        new LogicInstruction("op", "sub", match(108), match(104), match(107)),
                        new LogicInstruction("set", match(104), match(108)),
                        new LogicInstruction("write", "r", "cell1", match(104)),
                        new LogicInstruction("set", match(112), "63"),
                        new LogicInstruction("write", match(104), "cell1", match(112)),

                        // jump to subroutine
                        new LogicInstruction("set", "@counter", "label1"),

                        // return address
                        new LogicInstruction("label", "label3"),

                        // pop return value from stack
                        new LogicInstruction("set", match(115), "63"),
                        new LogicInstruction("read", match(116), "cell1", match(115)),
                        new LogicInstruction("set", match(114), match(116)),
                        new LogicInstruction("read", match(117), "cell1", match(114)),
                        new LogicInstruction("set", match(113), match(117)),
                        new LogicInstruction("set", match(118), "1"),
                        new LogicInstruction("op", "add", match(119), match(114), match(118)),
                        new LogicInstruction("set", match(114), match(119)),
                        new LogicInstruction("set", match(122), "63"),
                        new LogicInstruction("write", match(114), "cell1", match(122)),

                        // function body
                        new LogicInstruction("op", "pow", match(123), "n", match(113)),
                        new LogicInstruction("op", "mul", match(124), match(94), match(123)),

                        // pop return address
                        new LogicInstruction("set", match(127), "63"),
                        new LogicInstruction("read", match(128), "cell1", match(127)),
                        new LogicInstruction("set", match(126), match(128)),
                        new LogicInstruction("read", match(129), "cell1", match(126)),
                        new LogicInstruction("set", match(125), match(129)),
                        new LogicInstruction("set", match(130), "1"),
                        new LogicInstruction("op", "add", match(131), match(126), match(130)),
                        new LogicInstruction("set", match(126), match(131)),
                        new LogicInstruction("set", match(134), "63"),
                        new LogicInstruction("write", match(126), "cell1", match(134)),

                        // push return value
                        new LogicInstruction("set", match(136), "63"),
                        new LogicInstruction("read", match(137), "cell1", match(136)),
                        new LogicInstruction("set", match(135), match(137)),
                        new LogicInstruction("set", match(138), "1"),
                        new LogicInstruction("op", "sub", match(139), match(135), match(138)),
                        new LogicInstruction("set", match(135), match(139)),
                        new LogicInstruction("write", match(124), "cell1", match(135)),
                        new LogicInstruction("set", match(143), "63"),
                        new LogicInstruction("write", match(135), "cell1", match(143)),

                        // return
                        new LogicInstruction("set", "@counter", match(125)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "allocate stack in cell1\ndef foo(n, r)\n2 * (n ** bar(r))\nend\n\ndef bar(x)\n2 * x\nend\n\n\nboo = 8\nx = foo(7, boo)\nprint(x)\nprintflush(message1)\n"
                        )
                )
        );
    }

    @Test
    void generatesMultiParameterControlInstruction() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("sensor", match(0), "leader", "@shootX"),
                        new LogicInstruction("sensor", match(1), "leader", "@shootY"),
                        new LogicInstruction("sensor", match(2), "leader", "@shooting"),
                        new LogicInstruction("control", "shoot", "turret", match(0), match(1), match(2)),
                        new LogicInstruction("set", match(3), "14"),
                        new LogicInstruction("set", match(4), "15"),
                        new LogicInstruction("set", match(5), "16"),
                        new LogicInstruction("control", "color", "turret", match(3), match(4), match(5)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "turret.shoot(leader.shootX, leader.shootY, leader.shooting)\nturret.color(14, 15, 16)\n"
                        )
                )
        );
    }

    @Test
    void canIndirectlyReferenceHeap() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", "HEAPPTR", "cell1"),
                        new LogicInstruction("set", match(2), "0"),
                        new LogicInstruction("set", match(3), "0"),
                        new LogicInstruction("write", match(2), "HEAPPTR", match(3)), // write $dx
                        new LogicInstruction("set", match(6), "1"),
                        new LogicInstruction("read", match(7), "HEAPPTR", match(6)), // read $dy
                        new LogicInstruction("set", match(8), "0"),
                        new LogicInstruction("read", match(9), "HEAPPTR", match(8)), // read $dx
                        new LogicInstruction("op", "add", match(10), match(7), match(9)), // tmp10 = $dx + $dy
                        new LogicInstruction("set", match(11), "1"),
                        new LogicInstruction("write", match(10), "HEAPPTR", match(11)), // set $dy
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "set HEAPPTR = cell1\nallocate heap in HEAPPTR[0...4]\n$dx = 0\n\n$dy += $dx"
                        )
                )
        );
    }

    @Test
    void canIndirectlyReferenceStack() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", "STACKPTR", "cell1"),
                        new LogicInstruction("set", "HEAPPTR", "cell2"),

                        new LogicInstruction("set", match(2), "63"), // init stack
                        new LogicInstruction("set", match(3), "63"),
                        new LogicInstruction("write", match(2), "STACKPTR", match(3)),

                        new LogicInstruction("set", match(7), "63"), // push return address on stack
                        new LogicInstruction("read", match(8), "STACKPTR", match(7)),
                        new LogicInstruction("set", match(6), match(8)),
                        new LogicInstruction("set", match(9), "1"),
                        new LogicInstruction("op", "sub", match(10), match(6), match(9)),
                        new LogicInstruction("set", match(6), match(10)),
                        new LogicInstruction("write", "label1", "STACKPTR", match(6)),
                        new LogicInstruction("set", match(14), "63"),
                        new LogicInstruction("write", match(6), "STACKPTR", match(14)),

                        new LogicInstruction("set", "@counter", "label0"), // invoke function

                        new LogicInstruction("label", "label1"),

                        new LogicInstruction("set", match(17), "63"), // pop return value from stack
                        new LogicInstruction("read", match(18), "STACKPTR", match(17)),
                        new LogicInstruction("set", match(16), match(18)),
                        new LogicInstruction("read", match(19), "STACKPTR", match(16)),
                        new LogicInstruction("set", match(15), match(19)),
                        new LogicInstruction("set", match(20), "1"),
                        new LogicInstruction("op", "add", match(21), match(16), match(20)),
                        new LogicInstruction("set", match(16), match(21)),
                        new LogicInstruction("set", match(24), "63"),
                        new LogicInstruction("write", match(16), "STACKPTR", match(24)),

                        new LogicInstruction("set", match(25), "0"), // write $dx
                        new LogicInstruction("write", match(15), "HEAPPTR", match(25)),

                        new LogicInstruction("end"), // end of main function body

                        new LogicInstruction("label", "label0"), // start of delay function

                        new LogicInstruction("set", match(26), "0"), // return value

                        new LogicInstruction("set", match(29), "63"), // pop return address from stack
                        new LogicInstruction("read", match(30), "STACKPTR", match(29)),
                        new LogicInstruction("set", match(28), match(30)),
                        new LogicInstruction("read", match(31), "STACKPTR", match(28)),
                        new LogicInstruction("set", match(27), match(31)),
                        new LogicInstruction("set", match(32), "1"),
                        new LogicInstruction("op", "add", match(33), match(28), match(32)),
                        new LogicInstruction("set", match(28), match(33)),
                        new LogicInstruction("set", match(36), "63"),
                        new LogicInstruction("write", match(28), "STACKPTR", match(36)),

                        new LogicInstruction("set", match(38), "63"), // push return value on stack
                        new LogicInstruction("read", match(39), "STACKPTR", match(38)),
                        new LogicInstruction("set", match(37), match(39)),
                        new LogicInstruction("set", match(40), "1"),
                        new LogicInstruction("op", "sub", match(41), match(37), match(40)),
                        new LogicInstruction("set", match(37), match(41)),
                        new LogicInstruction("write", match(26), "STACKPTR", match(37)),
                        new LogicInstruction("set", match(45), "63"),
                        new LogicInstruction("write", match(37), "STACKPTR", match(45)),

                        new LogicInstruction("set", "@counter", match(27)), // jump back to caller
                        new LogicInstruction("end")

                ),
                LogicInstructionGenerator.generateFrom(
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
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "2"),
                        new LogicInstruction("op", "sqrt", match(1), match(0)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "sqrt(2)"
                        )

                )
        );
    }

    @Test
    void supportsLogiclessCaseWhen() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "2"),
                        new LogicInstruction("op", "rand", match(1), match(0)),
                        new LogicInstruction("op", "floor", match(2), match(1)),
                        new LogicInstruction("set", "ast0", match(2)),
                        new LogicInstruction("set", match(5), "0"),
                        new LogicInstruction("jump", match(1001), "notEqual", match(3), match(5)),
                        new LogicInstruction("set", match(6), "1000"),
                        new LogicInstruction("set", match(4), match(6)),
                        new LogicInstruction("jump", match(1000), "always"),
                        new LogicInstruction("label", match(1001)),
                        new LogicInstruction("set", match(7), "1"),
                        new LogicInstruction("jump", match(1002), "notEqual", match(3), match(7)),
                        new LogicInstruction("set", match(4), "null"),
                        new LogicInstruction("jump", match(1000), "always"),
                        new LogicInstruction("label", match(1002)),
                        new LogicInstruction("set", match(4), "null"),
                        new LogicInstruction("label", match(1000)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "case floor(rand(2))\nwhen 0\n  1000\nwhen 1\n  // no op\nend"
                        )

                )
        );
    }

    @Test
    void supportsMinMaxFunctions() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "2"),
                        new LogicInstruction("op", "max", match(1), "y", match(0)),
                        new LogicInstruction("op", "min", match(2), "x", match(1)),
                        new LogicInstruction("set", "r", match(2)),
                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("" +
                                "r = min(x, max(y, 2))"
                        )

                )
        );
    }

    @Test
    void supportsBitwiseAndOrXorAndShiftLeftOrRight() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "9842"),
                        new LogicInstruction("set", match(1), "1"),
                        new LogicInstruction("op", "and", match(2), match(0), match(1)),
                        new LogicInstruction("set", match(3), "1"),
                        new LogicInstruction("set", match(4), "4"),
                        new LogicInstruction("op", "shl", match(5), match(3), match(4)),
                        new LogicInstruction("op", "xor", match(6), match(2), match(5)),
                        new LogicInstruction("set", match(7), "1"),
                        new LogicInstruction("op", "shr", match(8), "y", match(7)),
                        new LogicInstruction("op", "or", match(9), match(6), match(8)),
                        new LogicInstruction("end")

                ),
                LogicInstructionGenerator.generateFrom(
                        (Seq) translateToAst("(9842 & 1) ^ (1 << 4) | y >> 1\n")
                )
        );
    }
}
