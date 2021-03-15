package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AstNodeBuilderTest extends AbstractAstTest {
    @Test
    void parsesSimpleAssignment() {
        assertEquals(
                new Seq(
                        new VarAssignment(
                                "a",
                                new NumericLiteral("1")
                        )
                ),
                translateToAst("a = 1")

        );
    }

    @Test
    void parsesUsefulWhileLoop() {
        assertEquals(
                new Seq(
                        new VarAssignment("n", new NumericLiteral("5")),
                        new WhileStatement(
                                new BinaryOp(new VarRef("n"), ">", new NumericLiteral("0")),
                                new VarAssignment(
                                        "n",
                                        new BinaryOp(
                                                new VarRef("n"),
                                                "-",
                                                new NumericLiteral("1")
                                        )
                                ))
                ),
                translateToAst("n = 5\nwhile n > 0 {\nn -= 1\n}\n")
        );
    }

    @Test
    void parsesUnaryOperation() {
        assertEquals(
                new Seq(
                        new UnaryOp("not", new VarRef("ready"))
                ),
                translateToAst("not ready")
        );
    }

    @Test
    void parsesParenthesis() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new BinaryOp(
                                        new NumericLiteral("1"),
                                        "+",
                                        new NumericLiteral("2")
                                ),
                                "*",
                                new NumericLiteral("3")
                        )
                ),
                translateToAst("(1 + 2) * 3")
        );
    }

    @Test
    void respectsArithmeticOrderOfOperations() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new NumericLiteral("1"),
                                "+",
                                new BinaryOp(
                                        new NumericLiteral("2"),
                                        "*",
                                        new NumericLiteral("3")
                                )
                        )
                ),
                translateToAst("1 + 2 * 3")
        );
    }

    @Test
    void parsesComplexConditionalExpression() {
        assertEquals(
                new Seq(
                        new UnaryOp("not",
                                new BinaryOp(
                                        new BinaryOp(
                                                new VarRef("a"),
                                                "<",
                                                new VarRef("b")
                                        ),
                                        "and",
                                        new BinaryOp(
                                                new VarRef("c"),
                                                ">",
                                                new BinaryOp(
                                                        new BinaryOp(
                                                                new NumericLiteral("4"),
                                                                "*",
                                                                new VarRef("r")
                                                        ),
                                                        ">",
                                                        new NumericLiteral("5")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("not (a < b and c > (4 * r > 5))")
        );
    }

    @Test
    void parsesFunctionCalls() {
        assertEquals(
                new Seq(
                        new Seq(
                                new Seq(
                                        new FunctionCall(
                                                "print",
                                                List.of(
                                                        new StringLiteral("\"a\": "),
                                                        new VarRef("a")
                                                )
                                        ),
                                        new FunctionCall("random", List.of())
                                ),
                                new FunctionCall("print", List.of(new VarRef("r")))),
                        new FunctionCall(
                                "print",
                                List.of(
                                        new StringLiteral("\\nb: "),
                                        new BinaryOp(
                                                new VarRef("b"),
                                                "/",
                                                new NumericLiteral("3.1415")
                                        )
                                )
                        )


                ),
                translateToAst("print(\"\\\"a\\\": \", a)\nrandom()\nprint(r)\nprint(\"\\nb: \", b / 3.1415)")
        );
    }

    @Test
    void parsesSensorReading() {
        assertEquals(
                new Seq(
                        new BinaryOp(
                                new SensorReading("foundation1", "@copper"),
                                "<",
                                new SensorReading("foundation1", "@itemCapacity")
                        ),
                        new BinaryOp(
                                new SensorReading("reactor1", "@cryofluid"),
                                "<",
                                new NumericLiteral("10")
                        )
                ),
                translateToAst("foundation1.copper < foundation1.itemCapacity\nreactor1.cryofluid < 10")
        );
    }

    @Test
    void parsesControl() {
        assertEquals(
                new Seq(
                        new Control(
                                "conveyor1",
                                "enabled",
                                new BinaryOp(
                                        new SensorReading("CORE", "@copper"),
                                        "<",
                                        new SensorReading("CORE", "@itemCapacity")
                                )
                        )
                ),
                translateToAst("conveyor1.enabled = CORE.copper < CORE.itemCapacity")
        );
    }

    @Test
    void parsesHeapAccesses() {
        assertEquals(
                new Seq(
                        new HeapWrite(
                                "cell2",
                                new NumericLiteral("1"),
                                new HeapRead("cell3", "0")
                        )
                ),
                translateToAst("cell2[1] = cell3[0]")
        );
    }

    @Test
    void parsesIfElseExpression() {
        assertEquals(
                new Seq(
                        new VarAssignment(
                                "value",
                                new IfExpression(
                                        new BinaryOp(
                                                new HeapRead("HEAP", "4"),
                                                "==",
                                                new NumericLiteral("0")
                                        ),
                                        new BooleanLiteral(false),
                                        new Seq(
                                                new HeapWrite("HEAP",
                                                        new NumericLiteral("4"),
                                                        new BooleanLiteral(true)
                                                ),
                                                new VarAssignment(
                                                        "n",
                                                        new BinaryOp(
                                                                new VarRef("n"),
                                                                "+",
                                                                new NumericLiteral("1")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("value = if HEAP[4] == 0 { false\n} else { HEAP[4] = true\nn += 1\n}")
        );
    }

    @Test
    void parsesIfExpression() {
        assertEquals(
                new Seq(
                        new IfExpression(
                                new BinaryOp(
                                        new VarRef("n"),
                                        ">",
                                        new NumericLiteral("4")
                                ),
                                new HeapWrite(
                                        "HEAP",
                                        new NumericLiteral("2"),
                                        new BinaryOp(
                                                new HeapRead("HEAP", "2"),
                                                "+",
                                                new NumericLiteral("1")
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if n > 4 { HEAP[2] += 1\n}\n}")
        );
    }

    @Test
    void parsesExponentiationAssignment() {
        assertEquals(
                new Seq(
                        new HeapWrite(
                                "cell1",
                                new NumericLiteral("0"),
                                new FunctionCall(
                                        "rand",
                                        List.of(
                                                new BinaryOp(
                                                        new NumericLiteral("9"),
                                                        "**",
                                                        new NumericLiteral("9")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("cell1[0] = rand(9**9)")
        );
    }

    @Test
    void parsesUnitReferences() {
        assertEquals(
                new Seq(
                        new NoOp(),
                        new WhileStatement(
                                new BinaryOp(
                                        new UnitRef("unit"),
                                        "===",
                                        new NullLiteral()
                                ),
                                new FunctionCall(
                                        "ubind",
                                        List.of(new VarRef("poly"))
                                )
                        )
                ),
                translateToAst("while @unit === null {\nubind(poly)\n}\n")
        );
    }

    @Test
    void parsesFlagAssignment() {
        assertEquals(
                new Seq(
                        new NoOp(),
                        new FunctionCall(
                                "flag",
                                List.of(new VarRef("FLAG"))
                        )
                ),
                translateToAst("flag(FLAG)")
        );
    }

    @Test
    void parsesUnaryMinus() {
        assertEquals(
                new Seq(
                        new Seq(
                                new VarAssignment(
                                        "dx",
                                        new BinaryOp(
                                                new VarRef("dx"),
                                                "*",
                                                new NumericLiteral("-1")
                                        )
                                ),
                                new VarAssignment("dy", new NumericLiteral("-1"))
                        ),
                        new VarAssignment(
                                "dz",
                                new BinaryOp(
                                        new NumericLiteral("2"),
                                        "-",
                                        new NumericLiteral("1")
                                )
                        )
                ),
                translateToAst("dx *= -1;dy = -1; dz = 2 - 1")
        );
    }

    @Test
    void parsesHeapReferencesWithRvalues() {
        assertEquals(
                new Seq(
                        new HeapWrite(
                                "cell1",
                                new VarRef("dx"),
                                new NumericLiteral("1")
                        )
                ),
                translateToAst("cell1[dx] = 1")
        );
    }
}
