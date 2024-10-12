package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.UnexpectedMessageException;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

// This class tests generation of user-defined function and function calls.
// Original tests from LogicInstructionGeneratorTest class were moved here, although with the new function
// generation mechanism and automatic inlining the generated code is vastly different now.
// New tests suited to the new code generation were added.
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
                        def foo(n) 1 + bar(n); end;
                        def bar(n) 1 - foo(n); end;
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
                        def foo(n) 1 + bar(n); end;
                        def bar(n) 2 * baz(n); end;
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
                        def delay
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
                          foo(n-1);
                        end;

                        $x = 99;
                        print(foo(1) + foo(2));
                        """
                ).instructions().subList(0, 2)
        );
    }

    @Test
    void refusesToDeclareRecursiveFunctionsWhenNoStackAround() {
        assertThrows(UnexpectedMessageException.class,
                () -> generateInstructions("""
                        def foo
                            foo();
                        end;
                        foo();
                        """
                )
        );
    }

    @Test
    void refusesMisplacedStackAllocation() {
        assertThrows(UnexpectedMessageException.class,
                () -> generateInstructions("""
                        while true do
                          allocate stack in cell1;
                        end;
                        """
                )
        );
    }

    @Test
    void refusesRecursiveInlineFunctions() {
        assertThrows(UnexpectedMessageException.class,
                () -> generateInstructions("""
                        allocate stack in cell1;
                        inline def foo(n)
                            foo(n - 1);
                        end;
                        print(foo(1) + foo(2));
                        """
                )
        );
    }

    @Test
    void refusesUppercaseFunctionParameter() {
        assertThrows(UnexpectedMessageException.class,
                () -> generateInstructions("""
                        def foo(N)
                            N;
                        end;
                        print(foo(1) + foo(2));
                        """
                )
        );
    }

    @Test
    void refusesBlockNameAsFunctionParameter() {
        assertThrows(UnexpectedMessageException.class,
                () -> generateInstructions("""
                        def foo(switch1)
                            switch1;
                        end;
                        print(foo(1) + foo(2));
                        """
                )
        );
    }
}
