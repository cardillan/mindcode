package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@Order(99)
class LogicInstructionGeneratorTest extends AbstractGeneratorTest {

    @Test
    void canIndirectlyReferenceHeap() {
        assertCompilesTo("""
                        HEAPPTR = cell1;
                        allocate heap in HEAPPTR[0...4];
                        $dx = 0;
                        $dy += $dx;
                        """,
                createInstruction(SET, "HEAPPTR", "cell1"),
                createInstruction(WRITE, "0", "HEAPPTR", "0"),
                createInstruction(READ, var(0), "HEAPPTR", "1"),
                createInstruction(READ, var(1), "HEAPPTR", "0"),
                createInstruction(OP, "add", var(2), var(0), var(1)),
                createInstruction(WRITE, var(2), "HEAPPTR", "1"),
                createInstruction(END)
        );
    }

    @Test
    void compilesAssigningAssignmentResults() {
        assertCompilesTo(
                "a = b = 42;",
                createInstruction(SET, "b", "42"),
                createInstruction(SET, "a", "b"),
                createInstruction(END)
        );
    }

    @Test
    void compilesBitwiseAndOrXorAndShiftLeftOrRight() {
        assertCompilesTo(
                "(9842 & x) ^ (z << 4) | y >> 1;\n",
                createInstruction(OP, "and", var(0), "9842", "x"),
                createInstruction(OP, "shl", var(1), "z", "4"),
                createInstruction(OP, "xor", var(2), var(0), var(1)),
                createInstruction(OP, "shr", var(3), "y", "1"),
                createInstruction(OP, "or", var(4), var(2), var(3)),
                createInstruction(END)
        );
    }

    @Test
    void compilesBitwiseOpPriority() {
        assertCompilesTo("""
                        a = b | c & d;
                        e = ~f & g;
                        g = h & 31 == 15;
                        x = y & 15 and z & 7;
                        """,
                createInstruction(OP, "and", var(0), "c", "d"),
                createInstruction(OP, "or", var(1), "b", var(0)),
                createInstruction(SET, "a", var(1)),
                createInstruction(OP, "not", var(2), "f"),
                createInstruction(OP, "and", var(3), var(2), "g"),
                createInstruction(SET, "e", var(3)),
                createInstruction(OP, "and", var(4), "h", "31"),
                createInstruction(OP, "equal", var(5), var(4), "15"),
                createInstruction(SET, "g", var(5)),
                createInstruction(OP, "and", var(6), "y", "15"),
                createInstruction(OP, "and", var(7), "z", "7"),
                createInstruction(OP, "land", var(8), var(6), var(7)),
                createInstruction(SET, "x", var(8)),
                createInstruction(END)
        );
    }

    @Test
    void compilesBooleanOperations() {
        assertCompilesTo("""
                        while z != true do
                            print("infinite loop!");
                        end;
                        printflush(message1);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "notEqual", var(0), "z", "true"),
                createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                createInstruction(PRINT, "\"infinite loop!\""),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END)
        );
    }

    @Test
    void compilesBreakAndContinueWithLabel() {
        assertCompilesTo("""
                        Outer:
                        while a do
                            print("In outer");
                            Inner:
                            while b do
                                print("In inner");
                                break Outer;
                                print("After break");
                                continue Inner;
                            end;
                            print("After inner");
                        end;
                        print("After outer");
                        """,
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
        );
    }

    @Test
    void compilesBreakContinue() {
        assertCompilesTo("""
                        while a do
                            if b then
                                continue;
                            elsif c then
                                break;
                            end;
                        end;
                        print("End");
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "a", "false"),
                createInstruction(JUMP, var(1003), "equal", "b", "false"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(SET, var(0), "null"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "c", "false"),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(SET, var(1), "null"),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, var(0), var(1)),
                createInstruction(LABEL, var(1004)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("End")),
                createInstruction(END)
        );
    }

    @Test
    void compilesCStyleComplexForLoop() {
        assertCompilesTo("""
                        for i = 0, j = -5; i < 5; j -= 1, i += 1 do
                            print(n);
                        end;
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(SET, "j", "-5"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "lessThan", var(0), "i", "5"),
                createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                createInstruction(PRINT, "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "sub", var(1), "j", "1"),
                createInstruction(SET, "j", var(1)),
                createInstruction(OP, "add", var(2), "i", "1"),
                createInstruction(SET, "i", var(2)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesCaseWhen() {
        assertCompilesTo("""
                        allocate heap in cell1[0..10];
                        case $state
                            when ST_EMPTY       then $state = ST_INITIALIZED;
                            when ST_INITIALIZED then $state = ST_DONE;
                        end;
                        """,
                createInstruction(READ, var(0), "cell1", "0"),
                createInstruction(SET, "__ast0", var(0)),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "ST_EMPTY"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(WRITE, "ST_INITIALIZED", "cell1", "0"),
                createInstruction(SET, var(1), "ST_INITIALIZED"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "ST_INITIALIZED"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(WRITE, "ST_DONE", "cell1", "0"),
                createInstruction(SET, var(1), "ST_DONE"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void compilesCaseWhenMultiple() {
        assertCompilesTo("""
                        case n
                            when 1, 2, 3 then "Few";
                            when 4, 5, 6 then "Several";
                            else              "Many";
                        end;
                        """,
                createInstruction(SET, "__ast0", "n"),

                // First alternative
                createInstruction(JUMP, var(1002), "equal", "__ast0", "1"),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "2"),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "3"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(0), "\"Few\""),
                createInstruction(JUMP, var(1000), "always"),

                // Second alternative
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "4"),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "5"),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "6"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, var(0), "\"Several\""),
                createInstruction(JUMP, var(1000), "always"),

                /// Else branch
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(0), "\"Many\""),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void compilesCaseWhenMultipleWithRanges() {
        assertCompilesTo("""
                        case n
                            when 0 .. 4, 6 .. 8, 10, 12 then
                                "A number I like";
                        end;
                        """,
                createInstruction(SET, "__ast0", "n"),
                createInstruction(JUMP, var(1003), "lessThan", "__ast0", "0"),
                createInstruction(JUMP, var(1002), "lessThanEq", "__ast0", "4"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1004), "lessThan", "__ast0", "6"),
                createInstruction(JUMP, var(1002), "lessThanEq", "__ast0", "8"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "10"),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "12"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(0), "\"A number I like\""),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void compilesCaseWhenThenElse() {
        assertCompilesTo("""
                        case n
                            when 1 then "1";
                            when 2 then "two";
                            else        "otherwise";
                        end;
                        """,
                createInstruction(SET, "__ast0", "n"),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "1"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(0), q("1")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "2"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, var(0), q("two")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(0), q("otherwise")),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void compilesComplexAssignment() {
        assertCompilesTo(
                "foo = (bar - 2) * 3;",
                createInstruction(OP, "sub", var(0), "bar", "2"),
                createInstruction(OP, "mul", var(1), var(0), "3"),
                createInstruction(SET, "foo", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesComplexMathExpression() {
        assertCompilesTo(
                "x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))));",
                createInstruction(OP, "rand", var(0), "1"),
                createInstruction(OP, "tan", var(1), var(0)),
                createInstruction(OP, "abs", var(2), var(1)),
                createInstruction(OP, "cos", var(3), var(2)),
                createInstruction(OP, "log", var(4), var(3)),
                createInstruction(OP, "sin", var(5), var(4)),
                createInstruction(OP, "floor", var(6), var(5)),
                createInstruction(OP, "ceil", var(7), var(6)),
                createInstruction(SET, "x", var(7)),
                createInstruction(END)
        );
    }

    @Test
    void compilesConstant() {
        assertCompilesTo(
                "const a = 10; print(a);",
                createInstruction(PRINT, "10"),
                createInstruction(END)
        );
    }

    @Test
    void compilesControlStatements() {
        assertCompilesTo(
                "conveyor1.enabled = foundation1.@copper === tank1.@water;",
                createInstruction(SENSOR, var(1), "foundation1", "@copper"),
                createInstruction(SENSOR, var(2), "tank1", "@water"),
                createInstruction(OP, "strictEqual", var(3), var(1), var(2)),
                createInstruction(CONTROL, "enabled", "conveyor1", var(3)),
                createInstruction(END)
        );
    }

    @Test
    void compilesCorrectCodeWhenCaseBranchIsCommentedOut() {
        assertCompilesTo("""
                        case n
                            // when 1 then
                            when 2 then
                                print(n);
                        end;
                        """,
                createInstruction(SET, "__ast0", "n"),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "2"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "n"),
                createInstruction(SET, var(0), "n"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void compilesCorrectCodeWhenIfExpressionHasCommentedOutSections() {
        assertCompilesTo("""
                        if n then
                            // no op
                        else
                            1;
                        end;
                        if m then
                            1;
                        else
                            // 2
                        end;
                        """,
                createInstruction(JUMP, var(1000), "equal", "n", "false"),
                createInstruction(SET, var(0), "null"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(0), "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1002), "equal", "m", "false"),
                createInstruction(SET, var(1), "1"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1003)),
                createInstruction(END)
        );
    }

    @Test
    void compilesDirectIndirectPropertyReference() {
        assertCompilesTo(
                "conveyor1.enabled = vault1.sensor(@graphite) < vault1.sensor(@itemCapacity);",
                createInstruction(SENSOR, var(0), "vault1", "@graphite"),
                createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                createInstruction(CONTROL, "enabled", "conveyor1", var(2)),
                createInstruction(END)
        );
    }

    @Test
    void compilesDoWhileLoop() {
        assertCompilesTo("""
                        print("Blocks:");
                        n = @links;
                        do
                          n -= 1;
                          block = getlink(n);
                          print(block);
                        loop while n > 0;
                        printflush(message1);
                        """,
                createInstruction(PRINT, q("Blocks:")),
                createInstruction(SET, var(0), "@links"),
                createInstruction(SET, "n", var(0)),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(1), "n", "1"),
                createInstruction(SET, "n", var(1)),
                createInstruction(GETLINK, var(2), "n"),
                createInstruction(SET, "block", var(2)),
                createInstruction(PRINT, "block"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "greaterThan", var(3), "n", "0"),
                createInstruction(JUMP, var(1000), "notEqual", var(3), "false"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END)
        );
    }

    @Test
    void compilesEndFromFunctionCall() {
        assertCompilesTo(
                "if some_cond == false then end(); end;",
                createInstruction(OP, "equal", var(0), "some_cond", "false"),
                createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                createInstruction(END),
                createInstruction(SET, var(1), "null"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void compilesExclusiveIteratorStyleLoop() {
        assertCompilesTo(
                "for n in 1 ... 17 do print(n); end;",
                createInstruction(SET, "n", "1"),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "n", "17"),
                createInstruction(PRINT, "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesForEachAndPrintFunctionCall() {
        assertCompilesTo("""
                        for a in @mono, @poly, @mega do
                            print(a);
                        end;
                        """,
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "a", "@mono"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, "a", "@poly"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1004), "marker0"),
                createInstruction(SETADDR, var(0), var(1005)),
                createInstruction(SET, "a", "@mega"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "a"),
                createInstruction(LABEL, var(1000)),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1005), "marker0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesFunctionsReturningValues() {
        assertCompilesTo(
                "z = 9; cell1[0] = rand(z**9);",
                createInstruction(SET, "z", "9"),
                createInstruction(OP, "pow", var(0), "z", "9"),
                createInstruction(OP, "rand", var(1), var(0)),
                createInstruction(WRITE, var(1), "cell1", "0"),
                createInstruction(END)
        );
    }

    @Test
    void compilesHeapAccesses() {
        assertCompilesTo(
                "cell1[3] = cell2[4] + conveyor1.@enabled;",
                createInstruction(READ, var(0), "cell2", "4"),
                createInstruction(SENSOR, var(1), "conveyor1", "@enabled"),
                createInstruction(OP, "add", var(2), var(0), var(1)),
                createInstruction(WRITE, var(2), "cell1", "3"),
                createInstruction(END)
        );
    }

    @Test
    void compilesIfThenExpression() {
        assertCompilesTo("""
                        value = if cell1[4] == 0 then
                            false;
                        else
                            cell1[4] = true;
                            n += 1;
                        end;
                        """,
                createInstruction(READ, var(0), "cell1", "4"),
                createInstruction(OP, "equal", var(1), var(0), "0"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(SET, var(2), "false"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(WRITE, "true", "cell1", "4"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(SET, var(2), "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "value", var(2)),
                createInstruction(END)
        );
    }

    @Test
    void compilesInclusiveIteratorStyleLoop() {
        assertCompilesTo(
                "for n in 1 .. 17 do print(n); end;",
                createInstruction(SET, "n", "1"),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThan", "n", "17"),
                createInstruction(PRINT, "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesIndirectPropertyReference() {
        assertCompilesTo("""
                        resource = @silicon;
                        if vault1.sensor(resource) < vault1.@itemCapacity then
                            foo = true;
                        end;
                        """,
                createInstruction(SET, "resource", "@silicon"),
                createInstruction(SENSOR, var(0), "vault1", "resource"),
                createInstruction(SENSOR, var(1), "vault1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                createInstruction(JUMP, var(1000), "equal", var(2), "false"),
                createInstruction(SET, "foo", "true"),
                createInstruction(SET, var(3), "foo"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(3), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void compilesIntegerDivision() {
        assertCompilesTo(
                "n = 4 \\ z;",
                createInstruction(OP, "idiv", var(0), "4", "z"),
                createInstruction(SET, "n", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void compilesLog10Call() {
        assertCompilesTo(
                "l = log10(x);",
                createInstruction(OP, "log10", var(0), "x"),
                createInstruction(SET, "l", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void compilesListIterationLoop() {
        assertCompilesTo(
                "for i, j in 1, 2, 3, 4 do print(i, j); end;",
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "i", "1"),
                createInstruction(SET, "j", "2"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, "i", "3"),
                createInstruction(SET, "j", "4"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "i"),
                createInstruction(PRINT, "j"),
                createInstruction(LABEL, var(1000)),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1004), "marker0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }


    @Test
    void compilesListIterationLoopWithModifications() {
        assertCompilesTo("""
                        for out i, out j in a, b, c, d do
                            i = 1;
                            j = 2;
                        end;
                        """,
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "i", "a"),
                createInstruction(SET, "j", "b"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SET, "a", "i"),
                createInstruction(SET, "b", "j"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, "i", "c"),
                createInstruction(SET, "j", "d"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "i", "1"),
                createInstruction(SET, "j", "2"),
                createInstruction(LABEL, var(1000)),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1004), "marker0"),
                createInstruction(SET, "c", "i"),
                createInstruction(SET, "d", "j"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesMainMemoryVariable() {
        assertCompilesTo(
                "memory = cell1; memory[0] = rand(9**9);",
                createInstruction(SET, "memory", "cell1"),
                createInstruction(OP, "rand", var(0), "387420489"),
                createInstruction(WRITE, var(0), "memory", "0"),
                createInstruction(END)
        );
    }

    @Test
    void compilesMemoryAccessThroughParameter() {
        assertCompilesTo("""
                        param mem = bank1;
                        mem[0] = 5;
                        """,
                createInstruction(SET, "mem", "bank1"),
                createInstruction(WRITE, "5", "mem", "0"),
                createInstruction(END)
        );
    }

    @Test
    void compilesMinMaxFunctions() {
        assertCompilesTo(
                "r = min(x, max(y, 2));",
                createInstruction(OP, "max", var(0), "y", "2"),
                createInstruction(OP, "min", var(1), "x", var(0)),
                createInstruction(SET, "r", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesMultiMinMaxFunctions() {
        assertCompilesTo("""
                        x = min(a, b, c, d);
                        y = max(a, b, c, d);
                        """,
                createInstruction(OP, "min", var(0), "a", "b"),
                createInstruction(OP, "min", var(0), var(0), "c"),
                createInstruction(OP, "min", var(0), var(0), "d"),
                createInstruction(SET, "x", var(0)),
                createInstruction(OP, "max", var(1), "a", "b"),
                createInstruction(OP, "max", var(1), var(1), "c"),
                createInstruction(OP, "max", var(1), var(1), "d"),
                createInstruction(SET, "y", var(1)),
                createInstruction(END)
        );
    }


    @Test
    void reportsWrongNumberOfMinMxParameters() {
        assertGeneratesMessages(ExpectedMessages.create()
                        .add("Not enough arguments to the 'min' function (expected 2 or more, found 0).")
                        .add("Not enough arguments to the 'max' function (expected 2 or more, found 0).")
                        .add("Not enough arguments to the 'min' function (expected 2 or more, found 1).")
                        .add("Not enough arguments to the 'max' function (expected 2 or more, found 1)."),
                """
                        a = min();
                        b = max();
                        c = min(a);
                        d = max(b);
                        """
        );
    }


    @Test
    void compilesModuloOperator() {
        assertCompilesTo(
                "running = @tick % 2 == 0;",
                createInstruction(OP, "mod", var(0), "@tick", "2"),
                createInstruction(OP, "equal", var(1), var(0), "0"),
                createInstruction(SET, "running", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesMultiParameterControlInstruction() {
        assertCompilesTo("""
                        turret.shoot(leader.@shootX, leader.@shootY, leader.@shooting);
                        turret.color(14, 15, 16);
                        """,
                createInstruction(SENSOR, var(0), "leader", "@shootX"),
                createInstruction(SENSOR, var(1), "leader", "@shootY"),
                createInstruction(SENSOR, var(2), "leader", "@shooting"),
                createInstruction(CONTROL, "shoot", "turret", var(0), var(1), var(2)),
                createInstruction(CONTROL, "color", "turret", "14", "15", "16"),
                createInstruction(END)
        );
    }

    @Test
    void compilesNestedLoopBreaks() {
        assertCompilesTo("""
                        while a do
                            print(a);
                            while b do
                                print(b);
                                if c then
                                    print(c);
                                    break;
                                end;
                                print(d);
                                break;
                            end;
                            print(e);
                            break;
                        end;
                        print(f);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "a", "false"),
                createInstruction(PRINT, "a"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "b", "false"),
                createInstruction(PRINT, "b"),
                createInstruction(JUMP, var(1006), "equal", "c", "false"),
                createInstruction(PRINT, "c"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(SET, var(0), "null"),
                createInstruction(JUMP, var(1007), "always"),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1007)),
                createInstruction(PRINT, "d"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "e"),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "f"),
                createInstruction(END)
        );
    }

    @Test
    void compilesNestedTernaryOperators() {
        assertCompilesTo(
                "a = (b > c) ? 1 : (d > e) ? 2 : 3;",
                createInstruction(OP, "greaterThan", var(0), "b", "c"),
                createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                createInstruction(SET, var(1), "1"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "greaterThan", var(2), "d", "e"),
                createInstruction(JUMP, var(1002), "equal", var(2), "false"),
                createInstruction(SET, var(3), "2"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(3), "3"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(1), var(3)),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "a", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesNoOpCaseWhen() {
        assertCompilesTo("""
                        case floor(rand(2))
                            when 0 then
                                1000;
                            when 1 then
                                // no op
                            end;
                        """,
                createInstruction(OP, "rand", var(0), "2"),
                createInstruction(OP, "floor", var(1), var(0)),
                createInstruction(SET, "__ast0", var(1)),
                createInstruction(JUMP, var(1002), "equal", "__ast0", "0"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(2), "1000"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1004), "equal", "__ast0", "1"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, var(2), "null"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1000)),
                createInstruction(END)
        );
    }

    @Test
    void compilesNoiseCall() {
        assertCompilesTo(
                "n = noise(4, 8);",
                createInstruction(OP, "noise", var(0), "4", "8"),
                createInstruction(SET, "n", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void compilesNullAndUnaryOp() {
        assertCompilesTo("""
                        a = ~a;
                        b = !b;
                        c = not c;
                        x = null;
                        """,
                createInstruction(OP, "not", var(0), "a"),
                createInstruction(SET, "a", var(0)),
                createInstruction(OP, "equal", var(1), "b", "false"),
                createInstruction(SET, "b", var(1)),
                createInstruction(OP, "equal", var(2), "c", "false"),
                createInstruction(SET, "c", var(2)),
                createInstruction(SET, "x", "null"),
                createInstruction(END)
        );
    }

    @Test
    void compilesParameter() {
        assertCompilesTo("""
                        param a = 10;
                        print(a);
                        """,
                createInstruction(SET, "a", "10"),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void compilesParameterWithBuiltInValueOrBLock() {
        assertCompilesTo("""
                        param a = @flare;
                        param b = message1;
                        
                        print(a, b);
                        """,
                createInstruction(SET, "a", "@flare"),
                createInstruction(SET, "b", "message1"),
                createInstruction(PRINT, "a"),
                createInstruction(PRINT, "b"),
                createInstruction(END)
        );
    }

    @Test
    void compilesRangeExpressionLoop() {
        assertCompilesTo(
                "for n in a ... b do print(n); end;",
                createInstruction(SET, var(0), "b"),
                createInstruction(SET, "n", "a"),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "n", var(0)),
                createInstruction(PRINT, "n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesRealLifeTest1() {
        assertCompilesTo("""
                        n = 0;
                        while (reactor = getlink(n)) != null do
                            if reactor.@liquidCapacity > 0 then
                                pct_avail = reactor.@cryofluid / reactor.@liquidCapacity;
                                reactor.enabled = pct_avail >= 0.25;
                            end;
                            n += 1;
                        end;
                        """,
                createInstruction(SET, "n", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(GETLINK, var(0), "n"),
                createInstruction(SET, "reactor", var(0)),
                createInstruction(OP, "notEqual", var(1), "reactor", "null"),
                createInstruction(JUMP, var(1002), "equal", var(1), "false"),
                createInstruction(SENSOR, var(2), "reactor", "@liquidCapacity"),
                createInstruction(OP, "greaterThan", var(3), var(2), "0"),
                createInstruction(JUMP, var(1003), "equal", var(3), "false"),
                createInstruction(SENSOR, var(5), "reactor", "@cryofluid"),
                createInstruction(SENSOR, var(6), "reactor", "@liquidCapacity"),
                createInstruction(OP, "div", var(7), var(5), var(6)),
                createInstruction(SET, "pct_avail", var(7)),
                createInstruction(OP, "greaterThanEq", var(8), "pct_avail", "0.25"),
                createInstruction(CONTROL, "enabled", "reactor", var(8)),
                createInstruction(SET, var(4), var(8)),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(4), "null"),
                createInstruction(LABEL, var(1004)),
                createInstruction(OP, "add", var(9), "n", "1"),
                createInstruction(SET, "n", var(9)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void compilesRefsWithDashInThem() {
        assertCompilesTo("""
                        build(x, y, @titanium-conveyor, 1, 0);
                        b_building = getBlock(x, y, out b_type, out b_floor);
                        if b_type == @titanium-conveyor then
                            n += 1;
                        end;
                        """,
                createInstruction(UCONTROL, "build", "x", "y", "@titanium-conveyor", "1", "0"),
                createInstruction(UCONTROL, "getBlock", "x", "y", "b_type", var(0), "b_floor"),
                createInstruction(SET, "b_building", var(0)),
                createInstruction(OP, "equal", var(1), "b_type", "@titanium-conveyor"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(OP, "add", var(3), "n", "1"),
                createInstruction(SET, "n", var(3)),
                createInstruction(SET, var(2), "n"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void compilesSensorReadings() {
        assertCompilesTo(
                "foundation1.@copper < foundation1.@itemCapacity;",
                createInstruction(SENSOR, var(0), "foundation1", "@copper"),
                createInstruction(SENSOR, var(1), "foundation1", "@itemCapacity"),
                createInstruction(OP, "lessThan", var(2), var(0), var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesStrictNotEqual() {
        assertCompilesTo(
                "a = @unit.@dead !== null; print(a);",
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", var(1), var(0), "null"),
                createInstruction(OP, "equal", var(2), var(1), "false"),
                createInstruction(SET, "a", var(2)),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void compilesTernaryOpPriority() {
        assertCompilesTo("""
                        a = b > c ? b : c;
                        d += -e *= f;
                        """,
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
                createInstruction(OP, "mul", var(3), "-1", "e"),
                createInstruction(OP, "add", var(4), "d", var(3)),
                createInstruction(SET, "d", var(4)),
                createInstruction(END)
        );
    }

    @Test
    void compilesTernaryOperatorLogic() {
        assertCompilesTo("""
                        print("sm.enabled: ", smelter1.@enabled ? "true" : "false");
                        """,
                createInstruction(SENSOR, var(0), "smelter1", "@enabled"),
                createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                createInstruction(SET, var(1), "\"true\""),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(1), "\"false\""),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "\"sm.enabled: \""),
                createInstruction(PRINT, var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesTheSqrtFunction() {
        assertCompilesTo(
                "sqrt(z);",
                createInstruction(OP, "sqrt", var(0), "z"),
                createInstruction(END)
        );
    }

    @Test
    void compilesUbindAndControl() {
        assertCompilesTo("""
                        while @unit === null do
                            ubind(@poly);
                            move(10, 10);
                        end;
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "strictEqual", var(0), "@unit", "null"),
                createInstruction(JUMP, var(1001), "equal", var(0), "false"),
                createInstruction(UBIND, "@poly"),
                createInstruction(UCONTROL, "move", "10", "10"),
                createInstruction(LABEL, var(1010)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void compilesUnaryLiteralMinusExpressions() {
        assertCompilesTo("""
                        a = -7;
                        b = -a;
                        c = -5 * b;
                        d = 2 * -c;
                        e = -d ** 2;
                        """,
                createInstruction(SET, "a", "-7"),
                createInstruction(OP, "mul", var(0), "-1", "a"),
                createInstruction(SET, "b", var(0)),
                createInstruction(OP, "mul", var(1), "-5", "b"),
                createInstruction(SET, "c", var(1)),
                createInstruction(OP, "mul", var(2), "-1", "c"),
                createInstruction(OP, "mul", var(3), "2", var(2)),
                createInstruction(SET, "d", var(3)),
                createInstruction(OP, "pow", var(4), "d", "2"),
                createInstruction(OP, "mul", var(5), "-1", var(4)),
                createInstruction(SET, "e", var(5)),
                createInstruction(END)
        );
    }

    @Test
    void compilesUnaryMinus() {
        assertCompilesTo(
                "dx *= -1; dy = -1; dz = dy - 1;",
                createInstruction(OP, "mul", var(0), "dx", "-1"),
                createInstruction(SET, "dx", var(0)),
                createInstruction(SET, "dy", "-1"),
                createInstruction(OP, "sub", var(1), "dy", "1"),
                createInstruction(SET, "dz", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesVectorLengthAndAngleCalls() {
        assertCompilesTo(
                "length = len(x, y);\nangle = angle(x, y);\n",
                createInstruction(OP, "len", var(0), "x", "y"),
                createInstruction(SET, "length", var(0)),
                createInstruction(OP, "angle", var(1), "x", "y"),
                createInstruction(SET, "angle", var(1)),
                createInstruction(END)
        );
    }

    @Test
    void compilesWhileLoopAndPrintFunctionCall() {
        assertCompilesTo("""
                        n = 0;
                        while n < 5 do
                            n += 1;
                        end;
                        print("n: ", n);
                        """,
                createInstruction(SET, "n", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "lessThan", var(0), "n", "5"),
                createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                createInstruction(OP, "add", var(1), "n", "1"),
                createInstruction(SET, "n", var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "\"n: \""),
                createInstruction(PRINT, "n"),
                createInstruction(END)
        );
    }

    @Test
    void recognizesIconConstants() {
        assertCompilesTo("""
                        print(ITEM_COAL + "X");
                        """,
                createInstruction(PRINT, q("\uF833X")),
                createInstruction(END)
        );
    }

    @Test
    void recognizesTurretAsBlockName() {
        assertCompilesTo("""
                        def foo()
                            turret1.@health;
                        end;
                        print(foo());
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SENSOR, var(1), "turret1", "@health"),
                createInstruction(SET, var(0), var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void refusesAssignmentsToBlockNames() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Assignment to variable 'switch1' not allowed (name reserved for linked blocks)."),
                "switch1 = 5;"
        );
    }

    @Test
    void refusesBlockNamesAsFunctionParameters() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'switch1' of function 'foo' uses name reserved for linked blocks."),
                "def foo(switch1) false; end; foo(5);"
        );
    }

    @Test
    void refusesBlockNamesAsOutputArguments() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Using argument 'switch1' in a call to 'getBlock' not allowed (name reserved for linked blocks)."),
                "getBlock(10, 20, switch1);"
        );
    }

    @Test
    void refusesBreaksOutsideLoop() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("'break' statement outside of a do/while/for loop."),
                """
                        while a do
                            print(a);
                        end;
                        break;
                        """
        );
    }

    @Test
    void refusesConflictingConstantAndVariable() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Assignment to constant or parameter 'a' not allowed."),
                "const a = 10; a = 20;"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Cannot redefine variable or function parameter 'a' as a constant."),
                "a = 10; const a = 20;"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Assignment to constant or parameter 'a' not allowed."),
                "const a = 10; a *= 2;"
        );
    }

    @Test
    void refusesConflictingConstants() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Multiple declarations of 'a'."),
                "const a = 10; const a = 20;"
        );
    }

    @Test
    void refusesConflictingFunctionParameter() {
        assertAll(
                () -> assertGeneratesMessages(
                        ExpectedMessages.none(),
                        "a = 10; def foo(a) print(a); end; foo(5);"),
                () -> assertGeneratesMessages(
                        ExpectedMessages.create().add("Function 'foo': parameter name 'a' conflicts with existing constant or global parameter."),
                        "const a = 10; def foo(a) print(a); end; foo(5);"),
                () -> assertGeneratesMessages(
                        ExpectedMessages.create().add("Function 'foo': parameter name 'a' conflicts with existing constant or global parameter."),
                        "param a = 10; def foo(a) print(a); end; foo(5);")
        );
    }

    @Test
    void refusesConflictingParameterAndConstant() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Multiple declarations of 'a'."),
                "param a = 10; const a = 20;"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Multiple declarations of 'a'."),
                "const a = 10; param a = 20;"
        );
    }

    @Test
    void refusesConflictingParameterAndVariable() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Assignment to constant or parameter 'a' not allowed."),
                "param a = 10; a = 20;"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Cannot redefine variable or function parameter 'a' as a program parameter."),
                "a = 10; param a = 20;"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Assignment to constant or parameter 'a' not allowed."),
                "param a = 10; a *= 2;"
        );
    }

    @Test
    void refusesConflictingParameters() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Multiple declarations of 'a'."),
                "param a = 10; param a = 20;"
        );
    }

    @Test
    void refusesNonConstantParameters() {
        final String message = "Parameter declaration of 'a' does not use a literal value, linked block name or constant built-in variable.";
        assertGeneratesMessages(ExpectedMessages.create().add(message), "param a = 2 * 4;");
        assertGeneratesMessages(ExpectedMessages.create().add(message), "param a = @unit;");
        assertGeneratesMessages(ExpectedMessages.create().add(message), "param a = rand(5);");
        assertGeneratesMessages(ExpectedMessages.create().add(message), "param a = B;");
    }

    @Test
    void refusesIterationListsWithWrongSize() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("The number of values in the list (3) must be an integer multiple of the number of iterators (2)."),
                "for i, j in 1, 2, 3 do print(i, j); end;"
        );
    }

    @Test
    void refusesMisplacedFormattableLiterals() {
        assertGeneratesMessages(
                ExpectedMessages.none(),
                "inline def foo(x) print(x); end; foo(\"Non-formattable\");"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Formattable string not allowed here. It can only be used with 'print', 'println' and 'format' functions."),
                "i = $\"Formattable\";"
        );
        assertGeneratesMessages(
                ExpectedMessages.create().add("Formattable string not allowed here. It can only be used with 'print', 'println' and 'format' functions."),
                "inline def foo(x) print(x); end; foo($\"Formattable\");"
        );
    }

    @Test
    void removesCommentsFromLogicInstructions() {
        assertCompilesTo("""
                        // Remember that we initialized ourselves
                        a = 1;
                        """,
                createInstruction(SET, "a", "1"),
                createInstruction(END)
        );
    }

    @Test
    void resolvesConstantExpressions() {
        assertCompilesTo("""
                        a = 1 + 2;
                        b = 1 / 0;
                        c = min(3, 4);
                        d = 1 < 2 ? b : c;
                        e = abs(-3);
                        f = ~1;
                        g = not false;
                        h = -(2 + 3);
                        i = 1 / 10000;
                        """,
                createInstruction(SET, "a", "3"),
                createInstruction(SET, "b", "null"),
                createInstruction(SET, "c", "3"),
                createInstruction(SET, "d", "b"),
                createInstruction(SET, "e", "3"),
                createInstruction(SET, "f", "-2"),
                createInstruction(SET, "g", "true"),
                createInstruction(SET, "h", "-5"),
                createInstruction(SET, "i", "0.0001"),
                createInstruction(END)
        );
    }

    @Test
    void resolvesConstantStrictEqual() {
        assertCompilesTo(
                "a = 0 === null;",
                createInstruction(SET, "a", "false"),
                createInstruction(END)
        );
    }

    @Test
    void throwsAnOutOfHeapSpaceExceptionWhenUsingMoreHeapSpaceThanAllocated() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Allocated heap is too small! Increase the size of the allocation, or switch to a Memory Bank to give the heap even more space."),
                "allocate heap in cell1[0 .. 1];\n$dx = $dy = $dz;"
        );
    }

    @Test
    void recogizesRemarks() {
        assertCompilesTo("""
                        // This is a comment
                        /// This is a remark
                        """,
                createInstruction(REMARK, q("This is a remark")),
                createInstruction(END)
        );
    }

    @Test
    void compilesPrintfLogic8() {
        assertCompilesTo(createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V8A)),
                ExpectedMessages.create().add(
                        "The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code."),
                """
                        printf("{1}", "a");
                        """,
                createInstruction(PRINT, q("{1}")),
                createInstruction(FORMAT, q("a")),
                createInstruction(END)
        );
    }

    @Test
    void warnsNotEnoughArgumentsPrintfLogic8() {
        assertCompilesTo(createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V8A)),
                ExpectedMessages.create()
                        .add("The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code.")
                        .add("The 'printf' function doesn't have enough arguments for placeholders: 2 placeholder(s), 1 argument(s)."),
                """
                        printf("{1}{2}", "a");
                        """,
                createInstruction(PRINT, q("{1}{2}")),
                createInstruction(FORMAT, q("a")),
                createInstruction(END)
        );
    }

    @Test
    void warnsTooManyArgumentsPrintfLogic8() {
        assertCompilesTo(createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V8A)),
                ExpectedMessages.create()
                        .add("The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code.")
                        .add("The 'printf' function has more arguments than placeholders: 1 placeholder(s), 2 argument(s)."),
                """
                        printf("{1}", "a", "b");
                        """,
                createInstruction(PRINT, q("{1}")),
                createInstruction(FORMAT, q("a")),
                createInstruction(FORMAT, q("b")),
                createInstruction(END)
        );
    }

    @Test
    void refusesAssignmentsToBuiltIns() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Assignment to built-in variable '@counter' not allowed."),
                "@counter = 1;"
        );
    }


    @Test
    void compilesMlogFunction() {
        assertCompilesTo("""
                        a = 10;
                        mlog("foo", in a, out b);
                        print(b);
                        """,
                createInstruction(SET, "a", "10"),
                customInstruction("foo", "a", "b"),
                createInstruction(PRINT, "b"),
                createInstruction(END)
        );
    }

    @Test
    void refusesInvalidMlogArguments() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(1,  6, "First argument to the 'mlog' function must be a string literal.")
                        .add(2, 13, "All arguments to the 'mlog' function need to be specified.")
                        .add(2, 15, "All arguments to the 'mlog' function must be literals or user variables.")
                        .add(3, 13, "A variable passed to the 'mlog' function must use the 'in' and/or 'out' modifiers.")
                        .add(4, 13, "A string literal passed to the 'mlog' function must not use an 'out' modifier.")
                        .add(4, 24, "A numeric literal passed to the 'mlog' function must not use any modifier.")
                        .add(4, 30, "A numeric literal passed to the 'mlog' function must not use any modifier.")
                ,
                """
                        mlog(foo);
                        mlog("foo", , x * y);
                        mlog("foo", x);
                        mlog("foo", out "bar", in 0, out 1);
                        """
        );
    }

    @Test
    void generatesKebabCaseVariableNameWarning() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(1, 1, "Identifier 'foo-bar': kebab-case identifiers are deprecated."),
                """
                        foo-bar = 1;
                        """
        );
    }

    @Test
    void generatesNoValueWarnings() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(6, 9, "Expression doesn't have any value. Using no-value expressions in assignments is deprecated.")
                        .add(7, 11, "Expression doesn't have any value. Using no-value expressions in function calls is deprecated.")
                        .add(8, 12, "Expression doesn't have any value. Using no-value expressions in return statements is deprecated."),
                """
                        void foo()
                            null;
                        end;
                        
                        def bar()
                            x = foo();
                            print(foo());
                            return foo();
                        end;
                        
                        bar();
                        """
        );
    }

    @Test
    void generatesMissingBuiltInPrefixWarnings() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(1, 14, "Built-in variable 'coal': omitting the '@' prefix from built-in variable names is deprecated."),
                """
                        print(vault1.coal);
                        """
        );
    }
}
