package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ParsingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AstNodeBuilderTest extends AbstractAstTest {
    @Test
    void parsesTheEmptyProgram() {
        assertEquals(
                new Seq(new NoOp()),
                translateToAst("")
        );
    }

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
                                new HeapRead("cell3", new NumericLiteral("0"))
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
                                                new HeapRead("cell1", new NumericLiteral("4")),
                                                "==",
                                                new NumericLiteral("0")
                                        ),
                                        new BooleanLiteral(false),
                                        new Seq(
                                                new HeapWrite("cell1",
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
                translateToAst("value = if cell1[4] == 0 { false\n} else { cell1[4] = true\nn += 1\n}")
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
                                        "cell1",
                                        new NumericLiteral("2"),
                                        new BinaryOp(
                                                new HeapRead("cell1", new NumericLiteral("2")),
                                                "+",
                                                new NumericLiteral("1")
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if n > 4 { cell1[2] += 1\n}\n}")
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
    void parsesRefs() {
        assertEquals(
                new Seq(
                        new NoOp(),
                        new WhileStatement(
                                new BinaryOp(
                                        new Ref("unit"),
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

    @Test
    void parsesComments() {
        assertEquals(
                new Seq(
                        // We only remove the leading `//`, because we don't know what kind of formatting
                        // the end-user applied, so we have to keep the spaces in place
                        new Comment(" Remember that we initialized ourselves"),
                        new VarAssignment("wasInitialized", new NumericLiteral("1"))
                ),
                translateToAst(
                        "// Remember that we initialized ourselves\nwasInitialized = 1\n"
                )
        );
    }

    @Test
    void parsesRefsWithDashInThem() {
        assertEquals(
                new Seq(
                        new FunctionCall(
                                "build",
                                List.of(
                                        new VarRef("x"),
                                        new VarRef("y"),
                                        new Ref("titanium-conveyor"),
                                        new NumericLiteral("0"),
                                        new NumericLiteral("0")
                                )
                        )
                ),
                translateToAst("build(x, y, @titanium-conveyor, 0, 0)")
        );
    }

    @Test
    void parsesIfElseIf() {
        assertEquals(
                new Seq(
                        new IfExpression(
                                new BinaryOp(
                                        new VarRef("state"),
                                        "==",
                                        new NumericLiteral("1")),
                                new FunctionCall(
                                        "print",
                                        List.of(new VarRef("m"))),
                                new IfExpression(
                                        new BinaryOp(
                                                new VarRef("state"),
                                                "==",
                                                new NumericLiteral("2")
                                        ),
                                        new FunctionCall(
                                                "print",
                                                List.of(new VarRef("n")
                                                )
                                        ),
                                        new NoOp()
                                )
                        )
                ),
                translateToAst("if state == 1 {\nprint(m)\n} else {\nif state == 2 {\nprint(n)\n}\n}\n")
        );
    }

    @Test
    void parsesMathFuncations() {
        assertEquals(
                new Seq(
                        new VarAssignment(
                                "x",
                                new FunctionCall(
                                        "ceil",
                                        new FunctionCall(
                                                "floor",
                                                new FunctionCall(
                                                        "sin",
                                                        new FunctionCall(
                                                                "log",
                                                                new FunctionCall(
                                                                        "cos",
                                                                        new FunctionCall(
                                                                                "abs",
                                                                                new FunctionCall(
                                                                                        "tan",
                                                                                        new FunctionCall("rand",
                                                                                                new NumericLiteral("1")
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))))")
        );
    }

    @Test
    void parsesAddressCalculationReferences() {
        assertEquals(
                new Seq(
                        new HeapWrite(
                                "cell1",
                                new VarRef("ptr"),
                                new BinaryOp(
                                        new HeapRead(
                                                "cell1",
                                                new BinaryOp(
                                                        new VarRef("ptr"),
                                                        "-",
                                                        new NumericLiteral("1")
                                                )
                                        ),
                                        "+",
                                        new HeapRead(
                                                "cell1",
                                                new BinaryOp(
                                                        new VarRef("ptr"),
                                                        "-",
                                                        new NumericLiteral("2")
                                                )
                                        )

                                )
                        )
                ),
                translateToAst("cell1[ptr] = cell1[ptr - 1] + cell1[ptr - 2]")
        );
    }

    @Test
    void rejects_STACK_ReservedKeywordsInVarRef() {
        assertThrows(ParsingException.class, () -> translateToAst("cell1[STACK] = 0"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInVarRef() {
        assertThrows(ParsingException.class, () -> translateToAst("cell1[HEAP] = 0"));
    }

    @Test
    void rejects_STACK_ReservedKeywordsInHeapWrite() {
        assertThrows(ParsingException.class, () -> translateToAst("STACK[1] = 0"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInHeapWrite() {
        assertThrows(ParsingException.class, () -> translateToAst("HEAP[1] = 0"));
    }

    @Test
    void rejects_STACK_ReservedKeywordsInHeapRead() {
        assertThrows(ParsingException.class, () -> translateToAst("STACK[0]"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInHeapRead() {
        assertThrows(ParsingException.class, () -> translateToAst("HEAP[0]"));
    }

    @Test
    void rejects_STACK_ReservedKeywordsInVarAssignment() {
        assertThrows(ParsingException.class, () -> translateToAst("STACK = 0"));
    }

    @Test
    void rejects_HEAP_ReservedKeywordsInVarAssignment() {
        assertThrows(ParsingException.class, () -> translateToAst("HEAP = 0"));
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(
                        new VarAssignment("n", new NumericLiteral("1")),
                        new WhileStatement(
                                new BinaryOp(
                                        new VarRef("n"),
                                        "<=",
                                        new NumericLiteral("17")
                                ),
                                new Seq(
                                        new FunctionCall("print", new VarRef("n")),
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
                ),
                translateToAst("for n in 1 .. 17 {\nprint(n)\n}")
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(
                        new VarAssignment("n", new NumericLiteral("1")),
                        new WhileStatement(
                                new BinaryOp(
                                        new VarRef("n"),
                                        "<",
                                        new NumericLiteral("17")
                                ),
                                new Seq(
                                        new FunctionCall("print", new VarRef("n")),
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
                ),
                translateToAst("for n in 1 ... 17 {\nprint(n)\n}")
        );
    }

    @Test
    void parsesCStyleLoop() {
        assertEquals(
                new Seq(
                        new Seq(
                                new VarAssignment("i", new NumericLiteral("0")),
                                new VarAssignment("j", new NumericLiteral("-5"))
                        ),
                        new WhileStatement(
                                new BinaryOp(
                                        new VarRef("i"),
                                        "<",
                                        new NumericLiteral("5")
                                ),
                                new Seq(
                                        new FunctionCall("print", new VarRef("n")),
                                        new Seq(
                                                new VarAssignment(
                                                        "j",
                                                        new BinaryOp(
                                                                new VarRef("j"),
                                                                "-",
                                                                new NumericLiteral("1")
                                                        )
                                                ),
                                                new VarAssignment(
                                                        "i",
                                                        new BinaryOp(
                                                                new VarRef("i"),
                                                                "+",
                                                                new NumericLiteral("1")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1 {\nprint(n)\n}\n")
        );
    }

    @Test
    void parsesHeapAllocationWithExclusiveRange() {
        assertEquals(
                new Seq(
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2 0 ... 64")
        );
    }

    @Test
    void parsesHeapAllocationWithInclusiveRange() {
        assertEquals(
                new Seq(
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2 0 .. 30")
        );
    }

    @Test
    void heapAllocationOnlyAcceptsNumericLiteralForRangeDeclaration() {
        assertThrows(InvalidHeapAllocationException.class, () -> translateToAst("allocate heap in cell4 n .. k"));
        assertThrows(InvalidHeapAllocationException.class, () -> translateToAst("allocate heap in cell4 (0+1) .. max_val"));
    }

    @Test
    void parsesGlobalReferences() {
        assertEquals(
                new Seq(
                        new Seq(
                                new NoOp(),
                                new HeapWrite("cell2", new NumericLiteral("4"), new NumericLiteral("1"))
                        ),
                        new HeapWrite(
                                "cell2",
                                new NumericLiteral("5"),
                                new BinaryOp(
                                        new HeapRead(
                                                "cell2",
                                                new NumericLiteral("4")
                                        ),
                                        "+",
                                        new NumericLiteral("42")
                                )
                        )
                ),
                translateToAst("allocate heap in cell2 4 .. 5\n$dx = 1;$dy = $dx + 42")
        );
    }

    @Test
    void rejectsHeapUsageWhenUnallocated() {
        assertThrows(UnallocatedHeapException.class, () -> translateToAst("$dx = 1"));
    }

    @Test
    void throwsAnOutOfHeapSpaceExceptionWhenUsingMoreHeapSpaceThanAllocated() {
        assertThrows(OutOfHeapSpaceException.class, () -> translateToAst("allocate heap in cell1 0 .. 1\n$dx = $dy = $dz"));
    }
}
