package info.teksol.mindcode.ast;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.MindcodeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AstNodeBuilderTest extends AbstractAstTest {
    @Test
    void parsesTheEmptyProgram() {
        assertEquals(
                new Seq(null, null, new NoOp()),
                translateToAst("")
        );
    }

    @Test
    void parsesLiteralInt() {
        assertEquals(
                new Seq(null, null, 
                        new NumericLiteral(null, null, "156")
                ),
                translateToAst("156")

        );
    }

    @Test
    void parsesLiteralFloat() {
        assertEquals(
                new Seq(null, null, 
                        new NumericLiteral(null, null, "156.156")
                ),
                translateToAst("156.156")

        );
    }

    @Test
    void parsesLiteralString() {
        assertEquals(
                new Seq(null, null, 
                        new StringLiteral(null, null, "156")
                ),
                translateToAst("\"156\"")

        );
    }

    @Test
    void parsesVarRef() {
        assertEquals(
                new Seq(null, null, 
                        new VarRef(null, null, "a")
                ),
                translateToAst("a")

        );
    }

    @Test
    void parsesSimpleAssignment() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new VarRef(null, null, "a"),
                                new NumericLiteral(null, null, "1")
                        )
                ),
                translateToAst("a = 1")

        );
    }

    @Test
    void parsesSimpleBinOpPlusMinus() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, 
                                new BinaryOp(null, null, 
                                        new NumericLiteral(null, null, "1"),
                                        "+",
                                        new NumericLiteral(null, null, "2")
                                ),
                                "-",
                                new NumericLiteral(null, null, "3")
                        )
                ),
                translateToAst("1 + 2 - 3")

        );
    }

    @Test
    void parsesSimpleBinOpExp() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, 
                                new NumericLiteral(null, null, "3.1415"),
                                "**",
                                new NumericLiteral(null, null, "2")
                        )
                ),
                translateToAst("3.1415 ** 2")

        );
    }

    @Test
    void parsesSimpleBinOpMulDiv() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, 
                                new BinaryOp(null, null, 
                                        new NumericLiteral(null, null, "1"),
                                        "*",
                                        new NumericLiteral(null, null, "2")
                                ),
                                "/",
                                new NumericLiteral(null, null, "3")
                        )
                ),
                translateToAst("1 * 2 / 3")

        );
    }

    @Test
    void parsesUnaryOperation() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, new VarRef(null, null, "ready"), "==", new BooleanLiteral(null, null, false))
                ),
                translateToAst("not ready")
        );
    }

    @Test
    void respectsArithmeticOrderOfOperations() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, 
                                new NumericLiteral(null, null, "1"),
                                "+",
                                new BinaryOp(null, null, 
                                        new NumericLiteral(null, null, "2"),
                                        "*",
                                        new NumericLiteral(null, null, "3")
                                )
                        )
                ),
                translateToAst("1 + 2 * 3")
        );
    }

    @Test
    void parsesParenthesis() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, 
                                new BinaryOp(null, null, 
                                        new NumericLiteral(null, null, "1"),
                                        "+",
                                        new NumericLiteral(null, null, "2")
                                ),
                                "*",
                                new NumericLiteral(null, null, "3")
                        )
                ),
                translateToAst("(1 + 2) * 3")
        );
    }

    @Test
    void parsesComplexConditionalExpression() {
        assertEquals(
                new Seq(null, null, 
                        new BinaryOp(null, null, 
                                new BoolBinaryOp(null, null, 
                                        new BinaryOp(null, null, 
                                                new VarRef(null, null, "a"),
                                                "<",
                                                new VarRef(null, null, "b")
                                        ),
                                        "and",
                                        new BinaryOp(null, null, 
                                                new VarRef(null, null, "c"),
                                                ">",
                                                new BinaryOp(null, null, 
                                                        new BinaryOp(null, null, 
                                                                new NumericLiteral(null, null, "4"),
                                                                "*",
                                                                new VarRef(null, null, "r")
                                                        ),
                                                        ">",
                                                        new NumericLiteral(null, null, "5")
                                                )
                                        )
                                ),
                                "==",
                                new BooleanLiteral(null, null, false)
                        )
                ),
                translateToAst("not (a < b and c > (4 * r > 5))")
        );
    }

    @Test
    void parsesFunctionCalls() {
        assertEquals(
                new Seq(null, null, 
                        new Seq(null, null, 
                                new Seq(null, null, 
                                        new Seq(null, null, 
                                                new FunctionCall(null, null, 
                                                        "print",
                                                        List.of(
                                                                new StringLiteral(null, null, "\"a\": "),
                                                                new VarRef(null, null, "a")
                                                        )
                                                )
                                        ),
                                        new FunctionCall(null, null, "random", List.of())
                                ),
                                new FunctionCall(null, null, "print", List.of(new VarRef(null, null, "r")))
                        ),
                        new FunctionCall(null, null, 
                                "print",
                                List.of(
                                        new StringLiteral(null, null, "\\nb: "),
                                        new BinaryOp(null, null, 
                                                new VarRef(null, null, "b"),
                                                "/",
                                                new NumericLiteral(null, null, "3.1415")
                                        )
                                )
                        )

                ),
                translateToAst("print(\"\\\"a\\\": \", a)\nrandom()\nprint(r)\nprint(\"\\nb: \", b / 3.1415)")


        );
    }

    @Test
    void parsesUnaryMinus() {
        assertEquals(
                new Seq(null, null, 
                        new Seq(null, null, 
                                new Seq(null, null, 
                                        new Assignment(null, null, 
                                                new VarRef(null, null, "dx"),
                                                new BinaryOp(null, null, 
                                                        new VarRef(null, null, "dx"),
                                                        "*",
                                                        new NumericLiteral(null, null, "-1")
                                                )
                                        )
                                ),
                                new Assignment(null, null, new VarRef(null, null, "dy"), new NumericLiteral(null, null, "-1"))
                        ),
                        new Assignment(null, null, 
                                new VarRef(null, null, "dz"),
                                new BinaryOp(null, null, 
                                        new NumericLiteral(null, null, "2"),
                                        "-",
                                        new NumericLiteral(null, null, "1")
                                )
                        )
                ),
                translateToAst("dx *= -1\ndy = -1\ndz = 2 - 1")
        );
    }

    @Test
    void parsesHeapAccesses() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new HeapAccess(null, null, "cell2", new NumericLiteral(null, null, "1")),
                                new HeapAccess(null, null, "cell3", new NumericLiteral(null, null, "0"))
                        )
                ),
                translateToAst("cell2[1] = cell3[0]")
        );
    }

    @Test
    void parsesGlobalReferences() {
        assertEquals(
                new Seq(null, null, 
                        new Seq(null, null, 
                                new Seq(null, null, new HeapAllocation(null, null, "cell2", 4, 5), new NoOp()),
                                new Assignment(null, null, 
                                        new HeapAccess(null, null, "cell2", 0),
                                        new NumericLiteral(null, null, "1")
                                )
                        ),
                        new Assignment(null, null, 
                                new HeapAccess(null, null, "cell2", 1),
                                new BinaryOp(null, null, 
                                        new HeapAccess(null, null, "cell2", 0),
                                        "+",
                                        new NumericLiteral(null, null, "42")
                                )
                        )
                ),
                translateToAst("allocate heap in cell2[4..5]\n$dx = 1\n$dy = $dx + 42")
        );
    }


    @Test
    void parsesExponentiationAssignment() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new HeapAccess(null, null, "cell1" ,new NumericLiteral(null, null, "0")),
                                new FunctionCall(null, null, 
                                        "rand",
                                        List.of(
                                                new BinaryOp(null, null, 
                                                        new NumericLiteral(null, null, "9"),
                                                        "**",
                                                        new NumericLiteral(null, null, "9")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("cell1[0] = rand(9**9)")
        );
    }

    @Test
    void parsesHeapReferencesWithRvalues() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new HeapAccess(null, null, "cell1", new VarRef(null, null, "dx")),
                                new NumericLiteral(null, null, "1")
                        )
                ),
                translateToAst("cell1[dx] = 1")
        );
    }

    @Test
    void parsesComments() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, new VarRef(null, null, "wasInitialized"), new NumericLiteral(null, null, "1"))
                ),
                translateToAst(
                        "// Remember that we initialized ourselves\n// This is required otherwise we'll repeat ourselves\nwasInitialized = 1\n"
                )
        );
    }

    @Test
    void parsesMathFunctions() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new VarRef(null, null, "x"),
                                new FunctionCall(null, null, 
                                        "ceil",
                                        new FunctionCall(null, null, 
                                                "floor",
                                                new FunctionCall(null, null, 
                                                        "sin",
                                                        new FunctionCall(null, null, 
                                                                "log",
                                                                new FunctionCall(null, null, 
                                                                        "cos",
                                                                        new FunctionCall(null, null, 
                                                                                "abs",
                                                                                new FunctionCall(null, null, 
                                                                                        "tan",
                                                                                        new FunctionCall(null, null, "rand",
                                                                                                new NumericLiteral(null, null, "1")
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
    void parsesRefsWithDashInThem() {
        assertEquals(
                new Seq(null, null, 
                        new FunctionCall(null, null, 
                                "build",
                                List.of(
                                        new VarRef(null, null, "x"),
                                        new VarRef(null, null, "y"),
                                        new Ref(null, null, "titanium-conveyor"),
                                        new NumericLiteral(null, null, "0"),
                                        new NumericLiteral(null, null, "0")
                                )
                        )
                ),
                translateToAst("build(x, y, @titanium-conveyor, 0, 0)")
        );
    }

    @Test
    void parsesFlagAssignment() {
        assertEquals(
                new Seq(null, null, 
                        new NoOp(),
                        new FunctionCall(null, null, 
                                "flag",
                                List.of(new VarRef(null, null, "FLAG"))
                        )
                ),
                translateToAst("flag(FLAG)")
        );
    }

    @Test
    void parsesAddressCalculationReferences() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new HeapAccess(null, null, "cell1", new VarRef(null, null, "ptr")),
                                new BinaryOp(null, null, 
                                        new HeapAccess(null, null, 
                                                "cell1",
                                                new BinaryOp(null, null, 
                                                        new VarRef(null, null, "ptr"),
                                                        "-",
                                                        new NumericLiteral(null, null, "1")
                                                )
                                        ),
                                        "+",
                                        new HeapAccess(null, null, 
                                                "cell1",
                                                new BinaryOp(null, null, 
                                                        new VarRef(null, null, "ptr"),
                                                        "-",
                                                        new NumericLiteral(null, null, "2")
                                                )
                                        )

                                )
                        )
                ),
                translateToAst("cell1[ptr] = cell1[ptr - 1] + cell1[ptr - 2]")
        );
    }

    @Test
    void parsesHeapAllocationWithExclusiveRange() {
        assertEquals(
                new Seq(null, null, 
                        new HeapAllocation(null, null, "cell2", new ExclusiveRange(null, null, new NumericLiteral(null, null, 0), new NumericLiteral(null, null, 64))),
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2[ 0 ... 64 ]")
        );
    }

    @Test
    void parsesHeapAllocationWithInclusiveRange() {
        assertEquals(
                new Seq(null, null, 
                        new HeapAllocation(null, null, "cell2", 0, 30),
                        new NoOp()
                ),
                translateToAst("allocate heap in cell2[0 .. 30]")
        );
    }

    @Test
    void rejectsHeapUsageWhenUnallocated() {
        assertThrows(MindcodeException.class, () -> translateToAst("$dx = 1"));
    }

    @Test
    void parsesPropertyAccesses() {
        assertEquals(
                new Seq(null, null, 
                        new Seq(null, null, 
                                new BinaryOp(null, null, 
                                        new PropertyAccess(null, null, new VarRef(null, null, "foundation1"), new Ref(null, null, "copper")),
                                        "<",
                                        new PropertyAccess(null, null, new VarRef(null, null, "foundation1"), new Ref(null, null, "itemCapacity"))
                                )
                        ),
                        new BinaryOp(null, null, 
                                new PropertyAccess(null, null, new VarRef(null, null, "reactor1"), new Ref(null, null, "cryofluid")),
                                "<",
                                new NumericLiteral(null, null, "10")
                        )
                ),
                translateToAst("foundation1.copper < foundation1.itemCapacity\nreactor1.cryofluid < 10")
        );
    }

    @Test
    void parsesWriteToPropertyAccess() {
        assertEquals(
                new Seq(null, null, 
                        new Assignment(null, null, 
                                new PropertyAccess(null, null, new VarRef(null, null, "conveyor1"), new Ref(null, null, "enabled")),
                                new BinaryOp(null, null, 
                                        new PropertyAccess(null, null, new VarRef(null, null, "CORE"), new Ref(null, null, "copper")),
                                        "<",
                                        new PropertyAccess(null, null, new VarRef(null, null, "CORE"), new Ref(null, null, "itemCapacity"))
                                )
                        )
                ),
                translateToAst("conveyor1.enabled = CORE.copper < CORE.itemCapacity")
        );
    }

    @Test
    void parsesUsefulWhileLoop() {
        assertEquals(
                new Seq(null, null, 
                        new Seq(null, null, 
                                new Assignment(null, null, new VarRef(null, null, "n"), new NumericLiteral(null, null, "5"))
                        ),
                        new WhileExpression(null, null, null,
                                new NoOp(),
                                new BinaryOp(null, null, new VarRef(null, null, "n"), ">", new NumericLiteral(null, null, "0")),
                                new Seq(null, null, 
                                        new Assignment(null, null, 
                                                new VarRef(null, null, "n"),
                                                new BinaryOp(null, null, 
                                                        new VarRef(null, null, "n"),
                                                        "-",
                                                        new NumericLiteral(null, null, "1")
                                                )
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("n = 5\nwhile n > 0\nn -= 1\nend\n")
        );
    }


    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(null, null, 
                        new RangedForExpression(null, null,
                                null,
                                new VarRef(null, null, "n"),
                                new InclusiveRange(null, null, new NumericLiteral(null, null, 1), new NumericLiteral(null, null, 17)),
                                new Seq(null, null, new FunctionCall(null, null, "print", new VarRef(null, null, "n")))
                        )
                ),
                translateToAst("for n in 1 .. 17\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertEquals(
                new Seq(null, null, 
                        new RangedForExpression(null, null,
                                null,
                                new VarRef(null, null, "n"),
                                new ExclusiveRange(null, null, new NumericLiteral(null, null, 1), new NumericLiteral(null, null, 17)),
                                new Seq(null, null, new FunctionCall(null, null, "print", new VarRef(null, null, "n")))
                        )
                ),
                translateToAst("for n in 1 ... 17\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesForEachLoop() {
        assertEquals(
                new Seq(null, null, 
                        new ForEachExpression(null, null,
                                null,
                                List.of(
                                        new Iterator(null, null, true, false, new VarRef(null, null, "a"))
                                ),
                                List.of(
                                        new Ref(null, null, "mono"),
                                        new Ref(null, null, "poly"),
                                        new Ref(null, null, "mega")
                                ),
                                new Seq(null, null, 
                                        new FunctionCall(null, null, 
                                                "print",
                                                List.of(new VarRef(null, null, "a"))
                                        )
                                )
                        )
                ),
                translateToAst("for a in (@mono, @poly, @mega)\nprint(a)\nend")
        );
    }

    @Test
    void parsesCStyleLoop() {
        assertEquals(new Seq(null, null,
                        new WhileExpression(null, null, null,
                                new Seq(null, null,
                                        new Assignment(null, null, new VarRef(null, null, "i"), new NumericLiteral(null, null, "0")),
                                        new Assignment(null, null, new VarRef(null, null, "j"), new NumericLiteral(null, null, "-5"))
                                ),
                                new BinaryOp(null, null,
                                        new VarRef(null, null, "i"),
                                        "<",
                                        new NumericLiteral(null, null, "5")
                                ),
                                new Seq(null, null,
                                        new FunctionCall(null, null, "print", new VarRef(null, null, "n"))
                                ),
                                new Seq(null, null,
                                        new Assignment(null, null,
                                                new VarRef(null, null, "j"),
                                                new BinaryOp(null, null,
                                                        new VarRef(null, null, "j"),
                                                        "-",
                                                        new NumericLiteral(null, null, "1")
                                                )
                                        ),
                                        new Assignment(null, null,
                                                new VarRef(null, null, "i"),
                                                new BinaryOp(null, null,
                                                        new VarRef(null, null, "i"),
                                                        "+",
                                                        new NumericLiteral(null, null, "1")
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("for i = 0, j = -5; i < 5; j -= 1, i += 1\nprint(n)\nend\n")
        );
    }

    @Test
    void parsesRefs() {
        assertEquals(
                new Seq(null, null, 
                        new NoOp(),
                        new WhileExpression(null, null, null,
                                new NoOp(),
                                new BinaryOp(null, null,
                                        new Ref(null, null, "unit"),
                                        "===",
                                        new NullLiteral(null, null)
                                ),
                                new Seq(null, null, 
                                        new FunctionCall(null, null, 
                                                "ubind",
                                                List.of(new VarRef(null, null, "poly"))
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("while @unit === null\nubind(poly)\nend")
        );
    }


    @Test
    void parsesIfExpression() {
        assertEquals(
                new Seq(null, null, 
                        new IfExpression(null, null,
                                new BinaryOp(null, null,
                                        new VarRef(null, null, "n"),
                                        ">",
                                        new NumericLiteral(null, null, "4")
                                ),
                                new Seq(null, null,
                                        new Assignment(null, null, 
                                                new HeapAccess(null, null, "cell1", new NumericLiteral(null, null, "2")),
                                                new BinaryOp(null, null,
                                                        new HeapAccess(null, null, "cell1", new NumericLiteral(null, null, "2")),
                                                        "+",
                                                        new NumericLiteral(null, null, "1")
                                                )
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if n > 4 cell1[2] += 1 end")
        );
    }

    @Test
    void parsesIfElseExpression() {
        assertEquals(
                new Seq(null, null,
                        new Assignment(null, null, 
                                new VarRef(null, null, "value"),
                                new IfExpression(null, null,
                                        new BinaryOp(null, null,
                                                new HeapAccess(null, null, "cell1", new NumericLiteral(null, null, "4")),
                                                "==",
                                                new NumericLiteral(null, null, "0")
                                        ),
                                        new Seq(null, null, new BooleanLiteral(null, null, false)),
                                        new Seq(null, null,
                                                new Seq(null, null,
                                                        new Assignment(null, null, 
                                                                new HeapAccess(null, null, "cell1", new NumericLiteral(null, null, "4")),
                                                                new BooleanLiteral(null, null, true)
                                                        )
                                                ),
                                                new Assignment(null, null, 
                                                        new VarRef(null, null, "n"),
                                                        new BinaryOp(null, null,
                                                                new VarRef(null, null, "n"),
                                                                "+",
                                                                new NumericLiteral(null, null, "1")
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                translateToAst("value = if cell1[4] == 0\nfalse\nelse\ncell1[4] = true\nn += 1\nend\n")
        );
    }

    @Test
    void parsesIfElseIf() {
        assertEquals(
                new Seq(null, null,
                        new IfExpression(null, null,
                                new BinaryOp(null, null,
                                        new VarRef(null, null, "state"),
                                        "==",
                                        new NumericLiteral(null, null, "1")),
                                new Seq(null, null, new FunctionCall(null, null, 
                                        "print",
                                        List.of(new VarRef(null, null, "m")))),
                                new Seq(null, null, new IfExpression(null, null,
                                        new BinaryOp(null, null,
                                                new VarRef(null, null, "state"),
                                                "==",
                                                new NumericLiteral(null, null, "2")),
                                        new Seq(null, null, new FunctionCall(null, null, 
                                                "print",
                                                List.of(new VarRef(null, null, "n")))),
                                        new NoOp())))),
                translateToAst("if state == 1\nprint(m)\nelse\nif state == 2\nprint(n)\nend\nend\n")
        );
    }

    @Test
    void parsesCaseWhen() {
        assertEquals(
                new Seq(null, null,
                        new Assignment(null, null, new VarRef(null, null, "__ast0"), new VarRef(null, null, "n")),
                        new CaseExpression(null, null,
                                new VarRef(null, null, "__ast0"),
                                List.of(
                                        new CaseAlternative(null, null, new NumericLiteral(null, null, "1"), new Seq(null, null, new StringLiteral(null, null, "1"))),
                                        new CaseAlternative(null, null, new NumericLiteral(null, null, "2"), new Seq(null, null, new StringLiteral(null, null, "two")))
                                ),
                                new Seq(null, null, new StringLiteral(null, null, "otherwise"))
                        )
                ),
                translateToAst("case n\nwhen 1\n\"1\"\nwhen 2\n\"two\"\nelse\n\"otherwise\"end\n")
        );
    }

    @Test
    void parsesCaseWhenMultipleWithRange() {
        assertEquals(
                new Seq(null, null,
                        new Assignment(null, null, new VarRef(null, null, "__ast0"), new VarRef(null, null, "n")),
                        new CaseExpression(null, null,
                                new VarRef(null, null, "__ast0"),
                                List.of(
                                        new CaseAlternative(null, null,
                                                List.of(
                                                        new InclusiveRange(null, null, new NumericLiteral(null, null, "0"), new NumericLiteral(null, null, "4")),
                                                        new InclusiveRange(null, null, new NumericLiteral(null, null, "6"), new NumericLiteral(null, null, "8")),
                                                        new NumericLiteral(null, null, "10"),
                                                        new NumericLiteral(null, null, "12")
                                                ),
                                                new Seq(null, null, new StringLiteral(null, null, "A number I like"))
                                        )
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("case n when 0 .. 4, 6 .. 8, 10, 12 \"A number I like\" end")
        );
    }

    @Test
    void acceptsSemicolonAsStatementSeparator() {
        assertEquals(
                new Seq(null, null,
                        new Seq(null, null,
                                new Assignment(null, null, new VarRef(null, null, "a"), new NumericLiteral(null, null, "0"))
                        ),
                        new Assignment(null, null, new VarRef(null, null, "b"), new NumericLiteral(null, null, "1"))
                ),
                translateToAst("a=0;b=1")
        );
    }

    @Test
    void supportsCallingTheEndFunction() {
        assertEquals(
                new Seq(null, null,
                        new IfExpression(null, null,
                                new BinaryOp(null, null,
                                        new VarRef(null, null, "some_cond"),
                                        "==",
                                        new BooleanLiteral(null, null, false)
                                ),
                                new Seq(null, null,
                                        new FunctionCall(null, null, "end")
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("if some_cond == false\nend()\nend")
        );
    }

    @Test
    void supportsModuloOperator() {
        assertEquals(
                new Seq(null, null,
                        new Assignment(null, null, 
                                new VarRef(null, null, "running"),
                                new BinaryOp(null, null,
                                        new BinaryOp(null, null,
                                                new Ref(null, null, "tick"),
                                                "%",
                                                new NumericLiteral(null, null, "2")
                                        ),
                                        "==",
                                        new NumericLiteral(null, null, "0")
                                )
                        )
                ),
                translateToAst("running = @tick % 2 == 0")
        );
    }

    @Test
    void supportsDeclaringAStack() {
        assertEquals(
                new Seq(null, null, new StackAllocation(null, null, "cell1", 0, 63)),
                translateToAst("allocate stack in cell1[0..63]")
        );
    }

    @Test
    void rejectsDualStackAllocation() {
        assertThrows(MindcodeException.class, () ->
                translateToAst("allocate stack in cell1[0...64], stack in cell2[0...512]")
        );
    }

    @Test
    void supportFunctionDeclarations() {
        assertEquals(
                prettyPrint(
                        new Seq(null, null,
                                new FunctionDeclaration(null, null, false, false,
                                        "delay",
                                        List.of(),
                                        new Seq(null, null,
                                                new Seq(null, null,
                                                        new Seq(null, null,
                                                                new Assignment(null, null, 
                                                                        new VarRef(null, null, "n"), new NumericLiteral(null, null, "0")
                                                                )
                                                        ),
                                                        new Assignment(null, null, 
                                                                new VarRef(null, null, "deadline"),
                                                                new BinaryOp(null, null,
                                                                        new Ref(null, null, "tick"),
                                                                        "+",
                                                                        new NumericLiteral(null, null, "60")
                                                                )
                                                        )
                                                ),
                                                new WhileExpression(null, null, null,
                                                        new NoOp(),
                                                        new BinaryOp(null, null,
                                                                new Ref(null, null, "tick"),
                                                                "<",
                                                                new VarRef(null, null, "deadline")
                                                        ),
                                                        new Seq(null, null,
                                                                new Assignment(null, null, 
                                                                        new VarRef(null, null, "n"),
                                                                        new BinaryOp(null, null, new VarRef(null, null, "n"),
                                                                                "+",
                                                                                new NumericLiteral(null, null, "1")
                                                                        )
                                                                )
                                                        ),
                                                        new NoOp()
                                                )
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def delay
                                  n = 0
                                  deadline = @tick + 60
                                  while @tick < deadline
                                    n += 1
                                  end
                                end
                                """)
                )
        );
    }

    @Test
    void supportsCallingDeclaredFunctions() {
        assertEquals(
                prettyPrint(
                        new Seq(null, null,
                                new Seq(null, null,
                                        new FunctionDeclaration(null, null, false, false,
                                                "foo",
                                                List.of(),
                                                new Seq(null, null,
                                                        new Assignment(null, null, new VarRef(null, null, "n"),
                                                                new BinaryOp(null, null, new VarRef(null, null, "n"), "+", new NumericLiteral(null, null, "1"))
                                                        )
                                                )
                                        )
                                ),
                                new FunctionCall(null, null, "foo")
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def foo
                                n=n+1
                                end
                                foo()
                                """
                        )
                )
        );
    }

    @Test
    void supportsDeclaringFunctionsThatAcceptParameters() {
        assertEquals(
                prettyPrint(
                        new Seq(null, null,
                                new Seq(null, null,
                                        new FunctionDeclaration(null, null, false, false,
                                                "foo",
                                                List.of(new VarRef(null, null, "s")),
                                                new Seq(null, null,
                                                        new BinaryOp(null, null, new VarRef(null, null, "s"), "+", new NumericLiteral(null, null, "1"))
                                                )
                                        )
                                ),
                                new FunctionCall(null, null, "foo", new NumericLiteral(null, null, "1"))
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def foo(s)
                                  s + 1
                                end
                                foo(1)
                                """
                        )
                )
        );
    }

    @Test
    void supportsDeclaringFunctionsWithInitializers() {
        assertEquals(
                prettyPrint(
                        new Seq(null, null,
                                new Seq(null, null,
                                        new FunctionDeclaration(null, null, false, false,
                                                "foo",
                                                List.of(new VarRef(null, null, "s"), new VarRef(null, null, "r")),
                                                new Seq(null, null,
                                                        new BinaryOp(null, null,
                                                                new BinaryOp(null, null, new VarRef(null, null, "s"), "+", new NumericLiteral(null, null, "1")),
                                                                "+",
                                                                new VarRef(null, null, "r")
                                                        )
                                                )
                                        )
                                ),
                                new FunctionCall(null, null, "foo", new NumericLiteral(null, null, "1"), new NumericLiteral(null, null, "6"))
                        )
                ),
                prettyPrint(
                        translateToAst("""
                                def foo(s, r)
                                  s + 1 + r
                                end
                                foo(1, 6)
                                """
                        )
                )
        );
    }

    @Test
    void supportsControllingBuildingsThroughPropAccessFunctionCalls() {
        assertEquals(
                prettyPrint(
                        new Seq(null, null,
                                new Control(null, null,
                                        new VarRef(null, null, "turret"),
                                        "shoot",
                                        List.of(
                                                new PropertyAccess(null, null, new VarRef(null, null, "leader"), new Ref(null, null, "shootX")),
                                                new PropertyAccess(null, null, new VarRef(null, null, "leader"), new Ref(null, null, "shootY")),
                                                new PropertyAccess(null, null, new VarRef(null, null, "leader"), new Ref(null, null, "shooting"))
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("turret.shoot(leader.shootX, leader.shootY, leader.shooting)\n")
                )
        );
    }

    @Test
    void supportsBitwiseAndOrXorShiftLeftAndShiftRight() {
        assertEquals(
                prettyPrint(
                        new Seq(null, null,
                                new BinaryOp(null, null,
                                        new BinaryOp(null, null,
                                                new BinaryOp(null, null,
                                                        new NumericLiteral(null, null, "9842"),
                                                        "&",
                                                        new NumericLiteral(null, null, "1")
                                                ),
                                                "^",
                                                new BinaryOp(null, null,
                                                        new NumericLiteral(null, null, "1"),
                                                        "<<",
                                                        new NumericLiteral(null, null, "4")
                                                )
                                        ),
                                        "|",
                                        new BinaryOp(null, null,
                                                new VarRef(null, null, "y"),
                                                ">>",
                                                new NumericLiteral(null, null, "1")
                                        )
                                )
                        )
                ),
                prettyPrint(
                        translateToAst("(9842 & 1) ^ (1 << 4) | y >> 1\n")
                )
        );
    }

    @Test
    void correctlyParsesElsif() {
        assertEquals(
                new Seq(null, null,
                        new IfExpression(null, null,
                                new BooleanLiteral(null, null, false),
                                new Seq(null, null, new NumericLiteral(null, null, 1)),
                                new IfExpression(null, null,
                                        new BooleanLiteral(null, null, true),
                                        new Seq(null, null, new NumericLiteral(null, null, 2)),
                                        new Seq(null, null, new NumericLiteral(null, null, 3))
                                )
                        )
                ),
                translateToAst(
                        "if false 1 elsif true 2 else 3 end"
                )
        );
    }

    @Test
    void correctlyParsesIndirectPropertyReference() {
        assertEquals(
                new Seq(null, null,
                        new Seq(null, null,
                                new Assignment(null, null, new VarRef(null, null, "resource"), new Ref(null, null, "silicon"))
                        ),
                        new IfExpression(null, null,
                                new BinaryOp(null, null,
                                        new PropertyAccess(null, null, new VarRef(null, null, "vault1"), new VarRef(null, null, "resource")),
                                        "<",
                                        new PropertyAccess(null, null, new VarRef(null, null, "vault1"), new Ref(null, null, "itemCapacity"))
                                ),
                                new Seq(null, null,
                                        new FunctionCall(null, null, "harvest", new VarRef(null, null, "vault1"), new VarRef(null, null, "resource"))
                                ),
                                new NoOp()
                        )
                ),
                translateToAst("""
                                resource = @silicon
                                if vault1.sensor(resource) < vault1.itemCapacity
                                  harvest(vault1, resource)
                                end
                                """
                )
        );
    }

    @Test
    void correctlyReadsTernaryOperator() {
        assertEquals(
                new Seq(null, null,
                        new FunctionCall(null, null, 
                                "print",
                                new StringLiteral(null, null, "\\nsm.enabled: "),
                                new IfExpression(null, null,
                                        new PropertyAccess(null, null,
                                                new VarRef(null, null, "smelter1"),
                                                new Ref(null, null, "enabled")
                                        ),
                                        new StringLiteral(null, null, "true"),
                                        new StringLiteral(null, null, "false")
                                )
                        )
                ),
                translateToAst("print(\"\\nsm.enabled: \", smelter1.enabled ? \"true\" : \"false\")")
        );
    }

    @Test
    void correctlyParsesStrictNotEqual() {
        assertEquals(
                new Seq(null, null,
                        new Assignment(null, null, 
                                new VarRef(null, null, "a"),
                                new BinaryOp(null, null,
                                        new BinaryOp(null, null,
                                                new Ref(null, null, "unit"),
                                                "===",
                                                new NullLiteral(null, null)
                                        ),
                                        "==",
                                        new BooleanLiteral(null, null, false)
                                )
                        )
                ),
                translateToAst("a = @unit !== null")
        );
    }

    @Test
    void correctlyParsesBreakContinue() {
        assertEquals(
                new Seq(null, null,
                        new Seq(null, null,
                                new WhileExpression(null, null, null,
                                        new NoOp(),
                                        new VarRef(null, null, "a"),
                                        new Seq(null, null,
                                                new IfExpression(null, null,
                                                        new VarRef(null, null, "b"),
                                                        new Seq(null, null, new ContinueStatement(null, null, null)),
                                                        new IfExpression(null, null,
                                                                new VarRef(null, null, "c"),
                                                                new Seq(null, null, new BreakStatement(null, null, null)),
                                                                new NoOp()
                                                        )
                                                )
                                        ),
                                        new NoOp()
                                )
                        ),
                        new FunctionCall(null, null, "print", new StringLiteral(null, null, "End"))
                ),

                translateToAst("""
                        while a
                          if b
                            continue
                          elsif c
                            break
                          end
                        end
                        print("End")
                        """
                )
        );
    }

    @Test
    void parsesListDirective() {
        assertEquals(
                new Seq(null, null,
                        new Directive(null, null, "sort-variables", "PARAMS,GLOBALS")
                ),
                translateToAst("#set sort-variables = PARAMS, GLOBALS;")

        );
    }

    @Test
    void parsesEmptyListDirective() {
        assertEquals(
                new Seq(null, null,
                        new Directive(null, null, "sort-variables", "")
                ),
                translateToAst("#set sort-variables;")

        );
    }
}
