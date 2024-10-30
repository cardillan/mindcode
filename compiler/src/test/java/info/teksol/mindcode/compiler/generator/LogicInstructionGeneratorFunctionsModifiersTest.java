package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.ExpectedMessages;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

// This class tests generation of user-defined function and function calls
// with respect to using input/output and vararg modifiers
@Order(99)
public class LogicInstructionGeneratorFunctionsModifiersTest extends AbstractGeneratorTest {

    @Test
    void acceptsAllSupportedModifiersInFunctionDeclaration() {
        assertGeneratesMessages(
                ExpectedMessages.none(),
                """
                        def foo(a, in b, out c, in out d, out in e)
                            x = a + b + d + e;
                            c = d = e = 2 * x;
                        end;
                        """
        );
    }

    @Test
    void acceptsAllSupportedModifiersInFunctionCall() {
        assertGeneratesMessages(
                ExpectedMessages.none(),
                """
                        def foo(in b, out c, in out d)
                            null;
                        end;
                        
                        s = t = 0;
                        foo(in 10, out s, in out t);
                        foo(10, , in t);
                        """
        );
    }

    @Test
    void compilesInlineOutParameters() {
        assertCompilesTo("""
                        inline def foo(out n)
                            n = 10;
                            20;
                        end;
                        print(foo(out z), z);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(SET, var(0), "20"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "z", "__fn0_n"),
                createInstruction(PRINT, var(0)),
                createInstruction(PRINT, "z"),
                createInstruction(END)
        );
    }

    @Test
    void compilesStacklessOutParameters() {
        assertCompilesTo("""
                        noinline def foo(out n)
                            n = 10;
                            20;
                        end;
                        print(foo(out z), z);
                        """,
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, "z", "__fn0_n"),
                createInstruction(PRINT, "__fn0retval"),
                createInstruction(PRINT, "z"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(SET, "__fn0retval", "20"),
                createInstruction(LABEL, var(1002)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }


    @Test
    void compilesRecursiveOutParameters() {
        assertCompilesTo("""
                        allocate stack in bank1;
                        def foo(out n)
                            n = 4;
                            foo();
                        end;
                        print(foo(out z), z);
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "z", "__fn0_n"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(PRINT, "z"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(1)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void compilesInlineUnusedOutParameters() {
        assertCompilesTo("""
                        inline def foo(out n, out m)
                            n = 10;
                            m = 20;
                        end;
                        foo(, out b);
                        foo(out a);
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(SET, "__fn0_m", "20"),
                createInstruction(SET, var(0), "__fn0_m"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "b", "__fn0_m"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", "10"),
                createInstruction(SET, "__fn1_m", "20"),
                createInstruction(SET, var(1), "__fn1_m"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, "a", "__fn1_n"),
                createInstruction(END)
        );
    }

    @Test
    void compilesStacklessUnusedOutParameters() {
        assertCompilesTo("""
                        noinline def foo(out n, out m)
                            n = 10;
                            m = 20;
                        end;
                        foo(, out b);
                        foo(out a);
                        """,
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, "b", "__fn0_m"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(SET, "a", "__fn0_n"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(SET, "__fn0_m", "20"),
                createInstruction(SET, "__fn0retval", "__fn0_m"),
                createInstruction(LABEL, var(1003)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void compilesRecursiveUnusedOutParameters() {
        assertCompilesTo("""
                        allocate stack in bank1;
                        noinline def foo(out n, out m)
                            n = 10;
                            m = 20;
                            foo(out n, out m);
                        end;
                        foo(, out b);
                        foo(out a);
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "b", "__fn0_m"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(CALLREC, "bank1", var(1000), var(1002), "__fn0retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "a", "__fn0_n"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(SET, "__fn0_m", "20"),
                createInstruction(CALLREC, "bank1", var(1000), var(1004), "__fn0retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0_n", "__fn0_n"),
                createInstruction(SET, "__fn0_m", "__fn0_m"),
                createInstruction(SET, var(2), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(LABEL, var(1003)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void compilesInlineUnusedInOutParameters() {
        assertCompilesTo("""
                        inline def foo(in out n, in out m)
                            print(m, n);
                            n *= 2;
                            m *= 2;
                        end;
                        a = b = 1;
                        foo(out a, in b);
                        foo(in a, out b);
                        """,
                createInstruction(SET, "b", "1"),
                createInstruction(SET, "a", "b"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "a"),
                createInstruction(SET, "__fn0_m", "b"),
                createInstruction(PRINT, "__fn0_m"),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(OP, "mul", var(1), "__fn0_n", "2"),
                createInstruction(SET, "__fn0_n", var(1)),
                createInstruction(OP, "mul", var(2), "__fn0_m", "2"),
                createInstruction(SET, "__fn0_m", var(2)),
                createInstruction(SET, var(0), "__fn0_m"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "a", "__fn0_n"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", "a"),
                createInstruction(SET, "__fn1_m", "b"),
                createInstruction(PRINT, "__fn1_m"),
                createInstruction(PRINT, "__fn1_n"),
                createInstruction(OP, "mul", var(4), "__fn1_n", "2"),
                createInstruction(SET, "__fn1_n", var(4)),
                createInstruction(OP, "mul", var(5), "__fn1_m", "2"),
                createInstruction(SET, "__fn1_m", var(5)),
                createInstruction(SET, var(3), "__fn1_m"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, "b", "__fn1_m"),
                createInstruction(END)
        );
    }

    @Test
    void compilesStacklessUnusedInOutParameters() {
        assertCompilesTo("""
                        noinline def foo(in out n, in out m)
                            print(m, n);
                            n *= 2;
                            m *= 2;
                        end;
                        a = b = 1;
                        foo(out a, in b);
                        foo(in a, out b);
                        """,
                createInstruction(SET, "b", "1"),
                createInstruction(SET, "a", "b"),
                createInstruction(SET, "__fn0_n", "a"),
                createInstruction(SET, "__fn0_m", "b"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, "a", "__fn0_n"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SET, "__fn0_n", "a"),
                createInstruction(SET, "__fn0_m", "b"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(SET, "b", "__fn0_m"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "__fn0_m"),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(OP, "mul", var(2), "__fn0_n", "2"),
                createInstruction(SET, "__fn0_n", var(2)),
                createInstruction(OP, "mul", var(3), "__fn0_m", "2"),
                createInstruction(SET, "__fn0_m", var(3)),
                createInstruction(SET, "__fn0retval", "__fn0_m"),
                createInstruction(LABEL, var(1003)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void compilesRecursiveUnusedInOutParameters() {
        assertCompilesTo("""
                        allocate stack in bank1;
                        def foo(in out n, in out m)
                            print(m, n);
                            n *= 2;
                            m *= 2;
                            foo(out n, out m);
                        end;
                        a = b = 1;
                        foo(out a, in b);
                        foo(in a, out b);
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "b", "1"),
                createInstruction(SET, "a", "b"),
                createInstruction(SET, "__fn0_n", "a"),
                createInstruction(SET, "__fn0_m", "b"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "a", "__fn0_n"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SET, "__fn0_n", "a"),
                createInstruction(SET, "__fn0_m", "b"),
                createInstruction(CALLREC, "bank1", var(1000), var(1002), "__fn0retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "b", "__fn0_m"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "__fn0_m"),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(OP, "mul", var(2), "__fn0_n", "2"),
                createInstruction(SET, "__fn0_n", var(2)),
                createInstruction(OP, "mul", var(3), "__fn0_m", "2"),
                createInstruction(SET, "__fn0_m", var(3)),
                createInstruction(SET, "__fn0_n", "__fn0_n"),
                createInstruction(SET, "__fn0_m", "__fn0_m"),
                createInstruction(CALLREC, "bank1", var(1000), var(1004), "__fn0retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0_n", "__fn0_n"),
                createInstruction(SET, "__fn0_m", "__fn0_m"),
                createInstruction(SET, var(4), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(4)),
                createInstruction(LABEL, var(1003)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void compilesInlineVoidFunction() {
        // Note: the function doesn't set a function return variable
        assertCompilesTo("""
                        inline void foo()
                            print("foo");
                        end;
                        foo();
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("foo")),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void compilesStacklessVoidFunction() {
        // Note: the function doesn't set a function return variable
        assertCompilesTo("""
                        noinline void foo()
                            print("foo");
                        end;
                        foo();
                        """,
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("foo")),
                createInstruction(LABEL, var(1002)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void compilesRecursiveVoidFunction() {
        // Note: the function doesn't set a function return variable
        assertCompilesTo("""
                        allocate stack in bank1;
                        void foo()
                            foo();
                            print("foo");
                        end;
                        foo();
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, q("foo")),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }
}
