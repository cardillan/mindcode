package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.ExpectedMessages;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

// This class tests generation of user-defined function and function calls.
// Original tests from LogicInstructionGeneratorTest class were moved here, although with the new function
// generation mechanism and automatic inlining the generated code is vastly different now.
// New tests suited to the new code generation were added.
@Order(99)
public class LogicInstructionGeneratorFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesImplicitInlineFunctions() {
        assertCompilesTo("""
                        def foo(n)
                            n;
                        end;
                        print(foo(3));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(SET, var(0), "__fn0_n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void respectsNoinlineFunctions() {
        assertCompilesTo("""
                        noinline def foo(n)
                            n;
                        end;
                        print(foo(3));
                        """,
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(PRINT, "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0retval", "__fn0_n"),
                createInstruction(LABEL, var(1002)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void generatesStacklessFunctions() {
        assertCompilesTo("""
                        def foo(n)
                          n;
                        end;
                        print(foo(3));
                        print(foo(4));
                        """,
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(PRINT, var(1)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0retval", "__fn0_n"),
                createInstruction(LABEL, var(1003)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void generatesExplicitInlineFunctions() {
        assertCompilesTo("""
                        inline def foo(n)
                          n;
                        end;
                        print(foo(3));
                        print(foo(4));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(SET, var(0), "__fn0_n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", "4"),
                createInstruction(SET, var(1), "__fn1_n"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, var(1)),
                createInstruction(END)
        );
    }

    @Test
    void generatesRecursiveFunctions() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(n)
                          foo(n);
                        end;
                        print(foo(3));
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(SET, "__fn0_n", "__fn0_n"),
                createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(POP, "bank1", "__fn0_n"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(1)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void generatesIndirectRecursion() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def bar(n) 1 - foo(n); end;
                        def foo(n) 1 + bar(n); end;
                        print(foo(4));
                        """,
                createInstruction(SET, "__sp", "0"),
                // call foo
                createInstruction(SET, "__fn1_n", "4"),
                createInstruction(CALLREC, "bank1", var(1001), var(1002), "__fn1retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(0), "__fn1retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                // def foo
                createInstruction(LABEL, var(1000)),
                // call bar
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(SET, "__fn1_n", "__fn0_n"),
                createInstruction(CALLREC, "bank1", var(1001), var(1004), "__fn1retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(POP, "bank1", "__fn0_n"),
                createInstruction(SET, var(1), "__fn1retval"),
                createInstruction(OP, "sub", var(2), "1", var(1)),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(LABEL, var(1003)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END),
                // def bar
                createInstruction(LABEL, var(1001)),
                // call foo
                createInstruction(PUSH, "bank1", "__fn1_n"),
                createInstruction(SET, "__fn0_n", "__fn1_n"),
                createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn1_n"),
                createInstruction(SET, var(3), "__fn0retval"),
                createInstruction(OP, "add", var(4), "1", var(3)),
                createInstruction(SET, "__fn1retval", var(4)),
                createInstruction(LABEL, var(1005)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void passesParametersToInlineFunction() {
        assertCompilesTo("""
                        def foo(n, r)
                            2 * (n ** r);
                        end;

                        boo = 4;
                        x = foo(3, boo);
                        print(x);
                        printflush(message1);
                        """,
                createInstruction(SET, "boo", "4"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(SET, "__fn0_r", "boo"),
                createInstruction(OP, "pow", var(1), "__fn0_n", "__fn0_r"),
                createInstruction(OP, "mul", var(2), "2", var(1)),
                createInstruction(SET, var(0), var(2)),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "x", var(0)),
                createInstruction(PRINT, "x"),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END)
        );
    }

    @Test
    void functionsCanCallOtherFunctionsInline() {
        assertCompilesTo("""
                        def foo(n) 1 + bar(n); end;
                        def bar(n) 2 * baz(n); end;
                        def baz(n) 3 ** n; end;
                        print(foo(4));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", "__fn0_n"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn2_n", "__fn1_n"),
                createInstruction(OP, "pow", var(3), "3", "__fn2_n"),
                createInstruction(SET, var(2), var(3)),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "mul", var(4), "2", var(2)),
                createInstruction(SET, var(1), var(4)),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", var(5), "1", var(1)),
                createInstruction(SET, var(0), var(5)),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void functionsCanCallOtherFunctionsStackless() {
        assertCompilesTo("""
                        def bar(n) 2 * baz(n); end;
                        def foo(n) 1 + bar(n); end;
                        def baz(n) 3 ** n; end;
                        foo(0);
                        bar(0);
                        baz(0);
                        print(foo(4));
                        """,
                // call foo
                createInstruction(SET, "__fn1_n", "0"),
                createInstruction(SETADDR, "__fn1retaddr", var(1003)),
                createInstruction(CALL, var(1001), "__fn1retval"),
                createInstruction(GOTOLABEL, var(1003), "__fn1"),
                createInstruction(SET, var(0), "__fn1retval"),
                // call bar
                createInstruction(SET, "__fn0_n", "0"),
                createInstruction(SETADDR, "__fn0retaddr", var(1004)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1004), "__fn0"),
                createInstruction(SET, var(1), "__fn0retval"),
                // call baz
                createInstruction(SET, "__fn2_n", "0"),
                createInstruction(SETADDR, "__fn2retaddr", var(1005)),
                createInstruction(CALL, var(1002), "__fn2retval"),
                createInstruction(GOTOLABEL, var(1005), "__fn2"),
                createInstruction(SET, var(2), "__fn2retval"),
                // call foo (again)
                createInstruction(SET, "__fn1_n", "4"),
                createInstruction(SETADDR, "__fn1retaddr", var(1006)),
                createInstruction(CALL, var(1001), "__fn1retval"),
                createInstruction(GOTOLABEL, var(1006), "__fn1"),
                createInstruction(SET, var(3), "__fn1retval"),
                createInstruction(PRINT, var(3)),
                createInstruction(END),
                // def bar
                createInstruction(LABEL, var(1000)),
                // call baz
                createInstruction(SET, "__fn2_n", "__fn0_n"),
                createInstruction(SETADDR, "__fn2retaddr", var(1008)),
                createInstruction(CALL, var(1002), "__fn2retval"),
                createInstruction(GOTOLABEL, var(1008), "__fn2"),
                createInstruction(OP, "mul", var(4), "2", "__fn2retval"),
                createInstruction(SET, "__fn0retval", var(4)),
                createInstruction(LABEL, var(1007)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END),
                // def foo
                createInstruction(LABEL, var(1001)),
                // call bar
                createInstruction(SET, "__fn0_n", "__fn1_n"),
                createInstruction(SETADDR, "__fn0retaddr", var(1010)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1010), "__fn0"),
                createInstruction(OP, "add", var(5), "1", "__fn0retval"),
                createInstruction(SET, "__fn1retval", var(5)),
                createInstruction(LABEL, var(1009)),
                createInstruction(GOTO, "__fn1retaddr", "__fn1"),
                createInstruction(END),
                // def baz
                createInstruction(LABEL, var(1002)),
                createInstruction(OP, "pow", var(6), "3", "__fn2_n"),
                createInstruction(SET, "__fn2retval", var(6)),
                createInstruction(LABEL, var(1011)),
                createInstruction(GOTO, "__fn2retaddr", "__fn2"),
                createInstruction(END)
        );
    }

    @Test
    void canIndirectlyReferenceStack() {
        assertCompilesTo("""
                        STACKPTR = cell1;
                        HEAPPTR = cell2;
                        allocate heap in HEAPPTR[0...16], stack in STACKPTR;
                        def delay()
                            0;
                            delay();
                        end;
                        $dx = delay();
                        """,
                // Setting up stack
                createInstruction(SET, "STACKPTR", "cell1"),
                createInstruction(SET, "HEAPPTR", "cell2"),
                createInstruction(SET, "__sp", "0"),
                // Function call
                createInstruction(CALLREC, "STACKPTR", var(1000), var(1002), "__fn0retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(WRITE, var(1), "HEAPPTR", "0"),
                createInstruction(END),
                // Function definition
                createInstruction(LABEL, var(1000)),
                createInstruction(CALLREC, "STACKPTR", var(1000), var(1004), "__fn0retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, var(2), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(LABEL, var(1003)),
                createInstruction(RETURN, "STACKPTR"),
                createInstruction(END)
        );
    }

    @Test
    void handlesNestedFunctionCallsInline() {
        assertCompilesTo("""
                        inline def a(n) n + 1; end;
                        print(a(a(a(4))));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(OP, "add", var(1), "__fn0_n", "1"),
                createInstruction(SET, var(0), var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", var(0)),
                createInstruction(OP, "add", var(3), "__fn1_n", "1"),
                createInstruction(SET, var(2), var(3)),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn2_n", var(2)),
                createInstruction(OP, "add", var(5), "__fn2_n", "1"),
                createInstruction(SET, var(4), var(5)),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, var(4)),
                createInstruction(END)
        );
    }

    @Test
    void handlesNestedFunctionCallsStackless() {
        assertCompilesTo("""
                        def a(n)
                            n + 1;
                        end;
                        print(a(a(a(4))));
                        """,
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SET, "__fn0_n", var(0)),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(SET, "__fn0_n", var(1)),
                createInstruction(SETADDR, "__fn0retaddr", var(1003)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1003), "__fn0"),
                createInstruction(SET, var(2), "__fn0retval"),
                createInstruction(PRINT, var(2)),
                createInstruction(END),
                // def a
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "add", var(3), "__fn0_n", "1"),
                createInstruction(SET, "__fn0retval", var(3)),
                createInstruction(LABEL, var(1004)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void handlesNestedFunctionCallsRecursive() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def a(n) a(n + 1); end;
                        print(a(a(a(4))));
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SET, "__fn0_n", var(0)),
                createInstruction(CALLREC, "bank1", var(1000), var(1002), "__fn0retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(SET, "__fn0_n", var(1)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(2), "__fn0retval"),
                createInstruction(PRINT, var(2)),
                createInstruction(END),
                // def a
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "add", var(3), "__fn0_n", "1"),
                // call a
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(SET, "__fn0_n", var(3)),
                createInstruction(CALLREC, "bank1", var(1000), var(1005), "__fn0retval"),
                createInstruction(LABEL, var(1005)),
                createInstruction(POP, "bank1", "__fn0_n"),
                createInstruction(SET, var(4), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(4)),
                createInstruction(LABEL, var(1004)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void generatesRecursiveFibonacci() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def fib(n)
                            n < 2 ? n : fib(n - 1) + fib(n - 2);
                        end;
                        print(fib(10));
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                // def fib
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "lessThan", var(1), "__fn0_n", "2"),
                createInstruction(JUMP, var(1003), "equal", var(1), "false"),
                createInstruction(SET, var(2), "__fn0_n"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "sub", var(3), "__fn0_n", "1"),
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(SET, "__fn0_n", var(3)),
                createInstruction(CALLREC, "bank1", var(1000), var(1005), "__fn0retval"),
                createInstruction(LABEL, var(1005)),
                createInstruction(POP, "bank1", "__fn0_n"),
                createInstruction(SET, var(4), "__fn0retval"),
                createInstruction(OP, "sub", var(5), "__fn0_n", "2"),
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(PUSH, "bank1", var(4)),
                createInstruction(SET, "__fn0_n", var(5)),
                createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", var(4)),
                createInstruction(POP, "bank1", "__fn0_n"),
                createInstruction(SET, var(6), "__fn0retval"),
                createInstruction(OP, "add", var(7), var(4), var(6)),
                createInstruction(SET, var(2), var(7)),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void doesNotPushInlineFunctionVariables() {
        // Verifies that locals inside inline functions (which by definition are limited to the function scope)
        // aren't pushed to the stack in recursive functions
        assertCompilesTo(PushOrPopInstruction.class::isInstance,
                """
                        allocate stack in bank1[0...512];
                        def foo(r)
                            bar(r);
                            foo(r - 1);
                        end;
                        inline def bar(n)
                            print(n);
                        end;
                        foo(1);
                        """,
                createInstruction(PUSH, "bank1", "__fn0_r"),
                createInstruction(POP, "bank1", "__fn0_r")
        );
    }

    @Test
    void preservesBlocksAndConstantsInUserFunctions() {
        assertCompilesTo("""
                        def foo(block)
                          print(radar(enemy, any, any, distance, block, 1));
                          print(block.radar(ally, flying, any, health, 1));
                          print(radar(enemy, boss, any, distance, lancer1, 1));
                        end;
                        foo(lancer1);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_block", "lancer1"),
                createInstruction(RADAR, "enemy", "any", "any", "distance", "__fn0_block", "1", var(1)),
                createInstruction(PRINT, var(1)),
                createInstruction(RADAR, "ally", "flying", "any", "health", "__fn0_block", "1", var(2)),
                createInstruction(PRINT, var(2)),
                createInstruction(RADAR, "enemy", "boss", "any", "distance", "lancer1", "1", var(3)),
                createInstruction(PRINT, var(3)),
                createInstruction(SET, var(0), var(3)),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void handlesInlineReturn() {
        assertCompilesTo("""
                        def a(n)
                            if n % 2 == 1 then
                                return "odd";
                            else
                                return "even";
                            end;
                        end;
                        print(a(0));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "0"),
                createInstruction(OP, "mod", var(1), "__fn0_n", "2"),
                createInstruction(OP, "equal", var(2), var(1), "1"),
                createInstruction(JUMP, var(1002), "equal", var(2), "false"),
                createInstruction(SET, var(0), q("odd")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(SET, var(3), "null"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(0), q("even")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(SET, var(3), "null"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(0), var(3)),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void handlesStacklessReturn() {
        assertCompilesTo("""
                        def a(n)
                            if n % 2 == 1 then
                                return "odd";
                            else
                                return "even";
                            end;
                        end;
                        print(a(0));
                        print(a(1));
                        """,
                createInstruction(SET, "__fn0_n", "0"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(SET, "__fn0_n", "1"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(PRINT, var(1)),
                createInstruction(END),
                // def a
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "mod", var(2), "__fn0_n", "2"),
                createInstruction(OP, "equal", var(3), var(2), "1"),
                createInstruction(JUMP, var(1004), "equal", var(3), "false"),
                createInstruction(SET, "__fn0retval", q("odd")),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(SET, var(4), "null"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0retval", q("even")),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(SET, var(4), "null"),
                createInstruction(LABEL, var(1005)),
                createInstruction(SET, "__fn0retval", var(4)),
                createInstruction(LABEL, var(1003)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void pushesParametersToStack() {
        assertCompilesTo(
                ix -> ix instanceof PushOrPopInstruction p && p.getVariable().getName().equals("n"),
                """
                        allocate stack in bank1;
                        def foo(n)
                            foo(n - 1);
                        end;
                        foo(5);
                        """,
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(POP, "bank1", "__fn0_n")
        );
    }

    @Test
    void pushesLocalVariablesToStack() {
        assertCompilesTo(
                ix -> ix instanceof PushOrPopInstruction p && p.getVariable().getName().equals("a"),
                """
                        allocate stack in bank1;
                        def foo(n)
                            a = n - 1;
                            foo(a);
                        end;
                        foo(5);
                        """,
                createInstruction(PUSH, "bank1", "__fn0_a"),
                createInstruction(POP, "bank1", "__fn0_a")
        );
    }

    @Test
    void handlesRecursiveReturn() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def gdc(a,b)
                            if b == 0 then
                                return a;
                            else
                                return gdc(b, a % b);
                            end;
                        end;
                        print(gdc(115, 78));
                        """,
                createInstruction(SET, "__sp", "0"),
                // call gdc
                createInstruction(SET, "__fn0_a", "115"),
                createInstruction(SET, "__fn0_b", "78"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                // def gdc
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "equal", var(1), "__fn0_b", "0"),
                createInstruction(JUMP, var(1003), "equal", var(1), "false"),
                // return a
                createInstruction(SET, "__fn0retval", "__fn0_a"),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(SET, var(2), "null"),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                // call gdc
                createInstruction(OP, "mod", var(3), "__fn0_a", "__fn0_b"),
                createInstruction(PUSH, "bank1", "__fn0_a"),
                createInstruction(PUSH, "bank1", "__fn0_b"),
                createInstruction(SET, "__fn0_a", "__fn0_b"),
                createInstruction(SET, "__fn0_b", var(3)),
                createInstruction(CALLREC, "bank1", var(1000), var(1005), "__fn0retval"),
                createInstruction(LABEL, var(1005)),
                createInstruction(POP, "bank1", "__fn0_b"),
                createInstruction(POP, "bank1", "__fn0_a"),
                createInstruction(SET, var(4), "__fn0retval"),
                // return gdc(...)
                createInstruction(SET, "__fn0retval", var(4)),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void handlesNestedInlineReturn() {
        assertCompilesTo("""
                        def foo(n) return 1 + bar(n); end;
                        def bar(n) return 2 * baz(n); end;
                        def baz(n) return 3 ** n; end;
                        print(foo(4));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", "__fn0_n"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn2_n", "__fn1_n"),
                createInstruction(OP, "pow", var(3), "3", "__fn2_n"),
                createInstruction(SET, var(2), var(3)),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "mul", var(4), "2", var(2)),
                createInstruction(SET, var(1), var(4)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", var(5), "1", var(1)),
                createInstruction(SET, var(0), var(5)),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void handlesNestedFunctionCallsInlineWithReturn() {
        assertCompilesTo("""
                        inline def a(n) return n + 1; end;
                        print(a(a(a(4))));
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(OP, "add", var(1), "__fn0_n", "1"),
                createInstruction(SET, var(0), var(1)),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(SET, var(0), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", var(0)),
                createInstruction(OP, "add", var(3), "__fn1_n", "1"),
                createInstruction(SET, var(2), var(3)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(SET, var(2), "null"),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn2_n", var(2)),
                createInstruction(OP, "add", var(5), "__fn2_n", "1"),
                createInstruction(SET, var(4), var(5)),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(SET, var(4), "null"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, var(4)),
                createInstruction(END)
        );
    }

    @Test
    void accessesMainVariables() {
        assertCompilesTo(
                ix -> ix instanceof SetInstruction set && set.getResult().getName().equals("X"),
                """
                        def setx(x)
                            X = x;
                        end;
                        setx(7);
                        """,
                createInstruction(SET, "X", "__fn0_x")
        );
    }

    @Test
    void allocateStackInNonStandardWayProducesCorrectCode() {
        // in this test, we're only concerned with whether the top of the stack is respected, and whether
        // the start of heap is respected. Everything else superfluous.
        assertLogicInstructionsMatch(
                createTestCompiler(),
                List.of(
                        createInstruction(SET, "__sp", "20"),
                        createInstruction(WRITE, "99", "cell3", "41")
                ),
                generateInstructions("""
                        allocate stack in cell3[20..40], heap in cell3[41...64];
                        def foo(n)
                          foo(n - 1);
                        end;

                        $x = 99;
                        print(foo(1) + foo(2));
                        """
                ).instructions().subList(0, 2)
        );
    }

    @Test
    void refusesToDeclareRecursiveFunctionsWhenNoStackAround() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Function 'foo' is recursive and no stack was allocated."),
                """
                        def foo()
                            foo();
                        end;
                        foo();
                        """
        );
    }

    @Test
    void refusesMisplacedStackAllocation() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Stack allocation must not be declared within a function."),
                """
                        def foo()
                            allocate stack in cell1;
                        end;
                        """
        );
    }

    @Test
    void refusesRecursiveInlineFunctions() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Recursive function 'foo' declared 'inline'."),
                """
                        allocate stack in cell1;
                        inline def foo(n)
                            foo(n - 1);
                        end;
                        print(foo(1) + foo(2));
                        """
        );
    }

    @Test
    void refusesUppercaseFunctionParameter() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'N' of function 'foo' uses name reserved for global variables."),
                """
                        def foo(N)
                            N;
                        end;
                        print(foo(1) + foo(2));
                        """
        );
    }

    @Test
    void refusesBlockNameAsFunctionParameter() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'switch1' of function 'foo' uses name reserved for linked blocks."),
                """
                        def foo(switch1)
                            switch1;
                        end;
                        print(foo(1) + foo(2));
                        """
        );
    }

    @Test
    void refusesMissingArguments() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Function 'foo': wrong number of arguments (expected 1, found 0)."),
                """
                        def foo(a)
                            a;
                        end;
                        foo();
                        """
        );
    }

    @Test
    void refusesTooManyArguments() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Function 'foo': wrong number of arguments (expected 1, found 2)."),
                """
                        def foo(a)
                            a;
                        end;
                        foo(1, 2);
                        """
        );
    }

    @Test
    void refusesVoidVarRefAssignments() {
        assertGeneratesMessages(
                ExpectedMessages.create().addRegex("Expression doesn't have any value\\..*"),
                """
                        void foo()
                            null;
                        end;
                        a = foo();
                        """
        );
    }

    @Test
    void refusesVoidVarRefAssignments2() {
        assertGeneratesMessages(
                ExpectedMessages.create().addRegex("Expression doesn't have any value\\..*"),
                """
                        a = printflush(message1);
                        """
        );
    }

    @Test
    void refusesVoidArgument() {
        assertGeneratesMessages(
                ExpectedMessages.create().addRegex("Expression doesn't have any value\\..*").repeat(2),
                """
                        print(printflush(message1));
                        printflush(printflush(message1));
                        """
        );
    }

    @Test
    void refusesVoidInReturns() {
        assertGeneratesMessages(
                ExpectedMessages.create().addRegex("Expression doesn't have any value\\..*"),
                """
                        def foo()
                            return printflush(message1);
                        end;
                        foo();
                        """
        );
    }

    @Test
    void refusesMissingArgument() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'a' isn't optional, a value must be provided."),
                """
                        def foo(a, b)
                            a + b;
                        end;
                        foo(, 1);
                        """
        );
    }

    @Test
    void reportsMissingModifiers() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'a' is declared 'in out' and no 'in' or 'out' argument modifier was used."),
                """
                        def foo(in out a)
                            a = a + 1;
                        end;
                        x = 0;
                        foo(x);
                        """
        );
    }

    @Test
    void refusesWrongInModifier() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add("Parameter 'a' isn't input, 'in' modifier not allowed."),
                """
                        def foo(out a)
                            a = 10;
                        end;
                        foo(in 1);
                        """
        );
    }

    @Test
    void refusesWrongOutModifier() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'a' isn't output, 'out' modifier not allowed."),
                """
                        def foo(a)
                            a + 1;
                        end;
                        foo(out x);
                        """
        );
    }

    @Test
    void reportsMissingOutModifier() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Parameter 'a' is output and 'out' modifier was not used."),
                """
                        def foo(out a)
                            a = 10;
                        end;
                        foo(x);
                        """
        );
    }

    @Test
    void handlesOptionalArguments() {
        assertCompilesTo("""
                        inline def foo(out a)
                            a = 10;
                            20;
                        end;
                        print(foo());
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_a", "10"),
                createInstruction(SET, var(0), "20"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void handlesOptionalArguments2() {
        assertCompilesTo("""
                        inline def foo(out a)
                            a = 10;
                            20;
                        end;
                        foo(out x);
                        print(foo());
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_a", "10"),
                createInstruction(SET, var(0), "20"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "x", "__fn0_a"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_a", "10"),
                createInstruction(SET, var(1), "20"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, var(1)),
                createInstruction(END)
        );
    }

    @Test
    void handlesOverloadedFunctions() {
        assertCompilesTo("""
                        void foo() print(0); end;
                        void foo(a) print(1); end;
                        void foo(a, b) print(2); end;
                        foo();
                        foo(1);
                        foo(1,1);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "0"),
                createInstruction(LABEL, var(1001)),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_a", "1"),
                createInstruction(PRINT, "1"),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn2_a", "1"),
                createInstruction(SET, "__fn2_b", "1"),
                createInstruction(PRINT, "2"),
                createInstruction(LABEL, var(1005)),
                createInstruction(END)
        );
    }

    @Test
    void handlesCaseExpressionsAsFunctionParameter() {
        assertGeneratesMessages(
                ExpectedMessages.none(),
                """
                        def foo(n)
                            print(n);
                        end;
                        foo(case 1 when 1 then 2; end);
                        """);
    }

    @Test
    void reportsFunctionConflicts() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(2, 1, "Function 'foo(a, out b)' clashes with function 'foo(a)'.")
                        .add(3, 1, "Function 'foo(a, b)' clashes with function 'foo(a, out b)'."),
                """
                        inline void foo(a) print(a); end;
                        inline void foo(a, out b) print(a); b = a; end;
                        inline void foo(a, b) print(a, b); end;
                        """
        );
    }

    @Test
    void reportsVarargFunctionConflict() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(2, 1, "Function 'foo(a, b, c, d...)' clashes with function 'foo(a, b, c)'."),
                """
                        inline void foo(a, b, c) print(a, b, c); end;
                        inline void foo(a, b, c, d...) print(a, b, c); end;
                        """
        );
    }

    @Test
    void reportsFunctionCallMismatch() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(5, 1, "Function 'foo': wrong number of arguments (expected at least 3, found 2).")
                        .add(6, 12, "Parameter 'b' isn't output, 'out' modifier not allowed.")
                        .add(7, 11, "Parameter 'c' isn't output, 'out' modifier not allowed.")
                ,
                """
                        inline void foo(a) print(a); end;
                        inline void foo(out a, b) print(b); a = b; end;
                        inline void foo(a, b, c, d...) print(a, b); end;
                        
                        foo(1, 2);
                        foo(out a, out b);
                        foo(a, b, out c);
                        """
        );
    }

    @Test
    void generatesMissingOutModifierWarning() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(1, 16, "Parameter 'type' is output and 'out' modifier was not used, assuming 'out'. Omitting 'out' modifiers is deprecated.")
                        .add(1, 22, "Parameter 'floor' is output and 'out' modifier was not used, assuming 'out'. Omitting 'out' modifiers is deprecated."),
                """
                        getBlock(x, y, type, floor);
                        """
        );
    }

    @Test
    void generatesKebabCaseFunctionNameWarning() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add(1, 1, "Function 'foo-bar': kebab-case identifiers are deprecated."),
                """
                        def foo-bar()
                            null;
                        end;
                        """
        );
    }
}
