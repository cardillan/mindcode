package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.logic.instructions.PushOrPopInstruction;
import info.teksol.mc.mindcode.logic.instructions.SetInstruction;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class StandardFunctionCallsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class Arrays {
        @Test
        void compilesArrayAsArgument() {
            assertCompilesTo("""
                            var a[] = (1, 2, 3);
                            inline void foo(args...)
                                print(args);
                            end;
                            foo(a);
                            foo(1);
                            """,
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, "1"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesTwoArraysAsArgument() {
            assertCompilesTo("""
                            var a[] = (1, 2, 3), b[] = (4, 5, 6);
                            inline void foo(args...)
                                print(args);
                            end;
                            foo(a, b);
                            """,
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(SET, ".b*0", "4"),
                    createInstruction(SET, ".b*1", "5"),
                    createInstruction(SET, ".b*2", "6"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(PRINT, ".b*0"),
                    createInstruction(PRINT, ".b*1"),
                    createInstruction(PRINT, ".b*2"),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void resolvesFunctionsWithArrays() {
            assertCompilesTo("""
                            var a[] = (1, 2, 3);
                            inline void foo(a) print("one"); end;
                            inline void foo(a, b) print("two"); end;
                            inline void foo(a...) print("vararg", length(a)); end;
                            
                            foo();
                            foo(a[2 .. 2]);
                            foo(a[1 .. 2]);
                            foo(a[0 .. 2]);
                            """,
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(PRINT, q("vararg")),
                    createInstruction(PRINT, "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":fn1:a", ".a*2"),
                    createInstruction(PRINT, q("one")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":fn2:a", ".a*1"),
                    createInstruction(SET, ":fn2:b", ".a*2"),
                    createInstruction(PRINT, q("two")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, q("vararg")),
                    createInstruction(PRINT, "3"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void CompilesArraysAsOutArguments() {
            assertCompilesTo("""
                            var a[] = (1, 2);
                            inline void foo(args...)
                                for out i in args do i++; end;
                            end;
                            foo(out a);
                            """,
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ":foo:i", ".a*0"),
                    createInstruction(SETADDR, tmp(0), label(4)),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ".a*0", ":foo:i"),
                    createInstruction(SET, ":foo:i", ".a*1"),
                    createInstruction(SETADDR, tmp(0), label(5)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(1), ":foo:i"),
                    createInstruction(OP, "add", ":foo:i", ":foo:i", "1"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(SET, ".a*1", ":foo:i"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(LABEL, label(0))
            );
        }
    }

    @Nested
    class DebugFunctions {
        @Test
        void compilesDebugFunctionWithDebug() {
            assertCompilesTo("""
                            #set debug;
                            
                            debug void foo()
                                print("foo");
                            end;
                            
                            foo();
                            print("bar");
                            """,
                    createInstruction(PRINT, q("foo")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("bar"))
            );
        }

        @Test
        void skipsDebugFunctionWithoutDebug() {
            assertCompilesTo("""
                            debug void foo()
                                print("foo");
                            end;
                            
                            foo();
                            print("bar");
                            """,
                    createInstruction(PRINT, q("bar"))
            );
        }
    }

    @Nested
    class Errors {
        @Test
        void refusesUppercaseFunctionParameter() {
            assertGeneratesMessage(
                    "Parameter 'N' of function 'foo' uses name reserved for global variables.",
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
            assertGeneratesMessage(
                    "Parameter 'switch1' of function 'foo' uses name reserved for linked blocks.",
                    """
                            def foo(switch1)
                                switch1;
                            end;
                            print(foo(1) + foo(2));
                            """
            );
        }

        @Test
        void refusesWrongSizedArraysAsArguments() {
            assertGeneratesMessage(
                    "Function 'foo': wrong number of arguments (expected 1, found 10).",
                    """
                            inline void foo(x) print(x); end;
                            var a[10];
                            foo(a);
                            """);
        }

        @Test
        void refusesMissingOutModifier() {
            assertGeneratesMessage(
                    "Function is trying to assign a value to an argument not declared 'out'.",
                    """
                            var a[1];
                            inline void foo(args...)
                                for out i in args do i = 2; end;
                            end;
                            foo(a);
                            """);
        }

        @Test
        void refusesBlockNameAsFunctionOutputArgument() {
            assertGeneratesMessage(
                    "Assignment to constant or parameter 'message1' not allowed.",
                    """
                            def foo(out n)
                                n = 10;
                            end;
                            foo(out message1);
                            """
            );
        }

        @Test
        void refusesMissingArguments() {
            assertGeneratesMessage(
                    "Function 'foo': wrong number of arguments (expected 1, found 0).",
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
            assertGeneratesMessage(
                    "Function 'foo': wrong number of arguments (expected 1, found 2).",
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
            assertGeneratesMessageRegex(
                    "Expression doesn't have any value\\..*",
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
            assertGeneratesMessageRegex(
                    "Expression doesn't have any value\\..*",
                    """
                            a = printflush(message1);
                            """
            );
        }

        @Test
        void refusesVoidArgument() {
            assertGeneratesMessages(
                    expectedMessages().addRegex("Expression doesn't have any value\\..*").repeat(2),
                    """
                            print(printflush(message1));
                            printflush(printflush(message1));
                            """
            );
        }

        @Test
        void refusesVoidInReturns() {
            assertGeneratesMessageRegex(
                    "Expression doesn't have any value\\..*",
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
            assertGeneratesMessage(
                    "Parameter 'a' isn't optional, a value must be provided.",
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
            assertGeneratesMessage(
                    "Parameter 'a' is declared 'in out' and no 'in' or 'out' argument modifier was used.",
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
            assertGeneratesMessage(
                    "Parameter 'a' isn't input, 'in' modifier not allowed.",
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
            assertGeneratesMessage(
                    "Parameter 'a' isn't output, 'out' argument modifier not allowed.",
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
            assertGeneratesMessage(
                    "Parameter 'a' is output and 'out' argument modifier was not used.",
                    """
                            def foo(out a)
                                a = 10;
                            end;
                            foo(x);
                            """
            );
        }

        @Test
        void reportsFunctionConflicts() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(3, 13, "Function 'foo(a, out b)' conflicts with function 'foo(a)'.")
                            .add(4, 13, "Function 'foo(a, in out b)' conflicts with function 'foo(a, b)'.")
                            .add(4, 13, "Function 'foo(a, in out b)' conflicts with function 'foo(a, out b)'."),
                    """
                            inline void foo(a) print(a); end;
                            inline void foo(a, b) print(a, b); end;
                            inline void foo(a, out b) print(a); b = a; end;
                            inline void foo(a, in out b) print(a, b); b = a; end;
                            """
            );
        }

        @Test
        void reportsVarargFunctionsConflict() {
            assertGeneratesMessage(2, 13,
                    "Function 'foo(a, b, c, d...)' conflicts with function 'foo(a, b, c...)'.",
                    """
                            inline void foo(a, b, c...) print(a, b); end;
                            inline void foo(a, b, c, d...) print(a, b, c); end;
                            """
            );
        }

        @Test
        void doesNotReportStandardVarargFunctionsConflict() {
            assertCompiles("""
                    inline void foo(a, b, c) print(a, b); end;
                    inline void foo(a, b, c, d...) print(a, b, c); end;
                    """
            );
        }

        @Test
        void reportsUnresolvedFunctionCalls() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(5, 1, "Cannot resolve function 'foo'.")
                            .add(6, 1, "Cannot resolve function 'foo'.")
                            .add(7, 1, "Cannot resolve function 'foo'.")
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
        void reportsWrongFunctionArguments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(5, 1, "Function 'foo': wrong number of arguments (expected 1, found 2).")
                            .add(6, 12, "Parameter 'b' isn't output, 'out' argument modifier not allowed.")
                            .add(7, 11, "Parameter 'c' isn't output, 'out' argument modifier not allowed.")
                    ,
                    """
                            inline void foo(a) print(a); end;
                            inline void bar(out a, b) print(b); a = b; end;
                            inline void baz(a, b, c, d...) print(a, b); end;
                            
                            foo(1, 2);
                            bar(out a, out b);
                            baz(a, b, out c);
                            """
            );
        }

        @Test
        void refusesNoOutModifiersInSystemCalls() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(1, 16, "Parameter 'type' is output and 'out' argument modifier was not used.")
                            .add(1, 22, "Parameter 'floor' is output and 'out' argument modifier was not used."),
                    """
                            getBlock(x, y, type, floor);
                            """
            );
        }

        @Test
        void refusesNoModifiers() {
            assertGeneratesMessage(
                    "Parameter 'x' is output and 'out' argument modifier was not used.",
                    """
                            void foo(out x)
                                x = 10;
                            end;
                            foo(x);
                            """);
        }

        @Test
        void refusesInModifier() {
            assertGeneratesMessage(
                    "Parameter 'x' isn't input, 'in' modifier not allowed.",
                    """
                            void foo(out x)
                                x = 10;
                            end;
                            foo(in x);
                            """);
        }
    }

    @Nested
    class InputArgumentFunctions {
        @Test
        void compilesUserFunctionCall() {
            assertCompilesTo("""
                            void foo(x)
                                print(x);
                            end;
                            
                            foo("Hello");
                            """,
                    createInstruction(SET, ":fn0:x", q("Hello")),
                    createInstruction(PRINT, ":fn0:x"),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesMultipleInlineFunctionCalls() {
            assertCompilesTo("""
                            inline void foo(x)
                                print(x);
                            end;
                            
                            foo("Hello, ");
                            foo("Dolly");
                            """,
                    createInstruction(SET, ":fn0:x", q("Hello, ")),
                    createInstruction(PRINT, ":fn0:x"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":fn1:x", q("Dolly")),
                    createInstruction(PRINT, ":fn1:x"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesNoinlineUserFunctionCall() {
            assertCompilesTo("""
                            noinline void foo(x)
                                print(x);
                            end;
                            
                            foo("Hello");
                            """,
                    createInstruction(SET, ":foo:x", q("Hello")),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, ":foo:x"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesNestedFunctionCallsInline() {
            assertCompilesTo("""
                            param p = 4;
                            inline def a(n) n + 1; end;
                            print(a(a(a(p))));
                            """,
                    createInstruction(SET, "p", "4"),
                    createInstruction(SET, ":a:n", "p"),
                    createInstruction(OP, "add", tmp(1), ":a:n", "1"),
                    createInstruction(SET, tmp(0), tmp(1)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":a.1:n", tmp(0)),
                    createInstruction(OP, "add", tmp(3), ":a.1:n", "1"),
                    createInstruction(SET, tmp(2), tmp(3)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":a.2:n", tmp(2)),
                    createInstruction(OP, "add", tmp(5), ":a.2:n", "1"),
                    createInstruction(SET, tmp(4), tmp(5)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, tmp(4))
            );
        }

        @Test
        void compilesNestedFunctionCallsStackless() {
            assertCompilesTo("""
                            noinline def a(n)
                                n + 1;
                            end;
                            print(a(a(a(4))));
                            """,
                    createInstruction(SET, ":a:n", "4"),
                    createInstruction(SETADDR, ":a*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":a*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(0), ":a*retval"),
                    createInstruction(SET, ":a:n", tmp(0)),
                    createInstruction(SETADDR, ":a*retaddr", label(2)),
                    createInstruction(CALL, label(0), "*invalid", ":a*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(1), ":a*retval"),
                    createInstruction(SET, ":a:n", tmp(1)),
                    createInstruction(SETADDR, ":a*retaddr", label(3)),
                    createInstruction(CALL, label(0), "*invalid", ":a*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), ":a*retval"),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(3), ":a:n", "1"),
                    createInstruction(SET, ":a*retval", tmp(3)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(RETURN, ":a*retaddr")
            );
        }

        @Test
        void compilesNestedFunctionCallsRecursive() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate stack in bank1[0...512];
                            def a(n) a(n + 1); end;
                            print(a(a(a(4))));
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(SET, ":a.0:n", "4"),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":a*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), ":a*retval"),
                    createInstruction(SET, ":a.0:n", tmp(0)),
                    createInstruction(CALLREC, "bank1", label(0), label(3), ":a*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), ":a*retval"),
                    createInstruction(SET, ":a.0:n", tmp(1)),
                    createInstruction(CALLREC, "bank1", label(0), label(4), ":a*retval"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(2), ":a*retval"),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(END),
                    // def a
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(3), ":a.0:n", "1"),
                    // call a
                    createInstruction(PUSH, "bank1", ":a.0:n"),
                    createInstruction(SET, ":a.0:n", tmp(3)),
                    createInstruction(CALLREC, "bank1", label(0), label(6), ":a*retval"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(POP, "bank1", ":a.0:n"),
                    createInstruction(SET, tmp(4), ":a*retval"),
                    createInstruction(SET, ":a*retval", tmp(4)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        @Test
        void compilesBlocksAndConstantsInUserFunctions() {
            assertCompilesTo("""
                            def foo(block)
                              print(radar(:enemy, :any, :any, :distance, block, 1));
                              print(block.radar(:ally, :flying, :any, :health, 1));
                              print(radar(:enemy, :boss, :any, :distance, lancer1, 1));
                            end;
                            foo(lancer1);
                            """,
                    createInstruction(SET, ":fn0:block", "lancer1"),
                    createInstruction(RADAR, "enemy", "any", "any", "distance", ":fn0:block", "1", tmp(1)),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(RADAR, "ally", "flying", "any", "health", ":fn0:block", "1", tmp(2)),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(RADAR, "enemy", "boss", "any", "distance", "lancer1", "1", tmp(3)),
                    createInstruction(PRINT, tmp(3)),
                    createInstruction(SET, tmp(0), tmp(3)),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesFormattableStringAsInlineArgument() {
            assertCompilesTo("""
                            inline void foo(x) print(x); end;
                            foo($"Hello");
                            """,
                    createInstruction(PRINT, q("Hello")),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesAccessToMainVariables() {
            assertCompilesTo("""
                            void setx(x)
                                X = x;
                            end;
                            setx(7);
                            """,
                    ix -> ix instanceof SetInstruction set && set.getResult().getName().equals("X"),
                    createInstruction(SET, ".X", ":fn0:x")
            );
        }

        @Test
        void handlesCaseExpressionsAsFunctionArguments() {
            assertCompiles("""
                    def foo(n)
                        print(n);
                    end;
                    foo(case 1 when 1 then 2; end);
                    """);
        }

        @Test
        void compilesOverloadedFunctions() {
            assertCompilesTo("""
                            void foo() print(0); end;
                            void foo(a) print(1); end;
                            void foo(a, b) print(2); end;
                            foo();
                            foo(1);
                            foo(1, 1);
                            """,
                    createInstruction(PRINT, "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":fn1:a", "1"),
                    createInstruction(PRINT, "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":fn2:a", "1"),
                    createInstruction(SET, ":fn2:b", "1"),
                    createInstruction(PRINT, "2"),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class OutputArgumentFunctions {
        @Test
        void compilesUserFunctionCall() {
            assertCompilesTo("""
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out z);
                            """,
                    createInstruction(SET, ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":z", ":fn0:x")
            );
        }

        @Test
        void compilesMultipleInlineFunctionCalls() {
            assertCompilesTo("""
                            inline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out y);
                            foo(out z);
                            """,
                    createInstruction(SET, ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":y", ":fn0:x"),
                    createInstruction(SET, ":fn1:x", "10"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":z", ":fn1:x")
            );
        }

        @Test
        void compilesUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate heap in bank1;
                            
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, tmp(1111)),
                    createInstruction(JUMP, tmp(1111), "equal", "bank1", "null"),
                    createInstruction(SET, ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(WRITE, ":fn0:x", "bank1", "0")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate heap in bank1;
                            
                            noinline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SETADDR, ":foo*retaddr", label(2)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(WRITE, ":foo:x", "bank1", "0"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":foo:x", "10"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesUserFunctionCallWithExternalMemory() {
            assertCompilesTo("""
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out cell2[cell1[0]]);
                            """,
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(SET, ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(WRITE, ":fn0:x", "cell2", tmp(1))
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalMemory() {
            assertCompilesTo("""
                            noinline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out cell1[cell1[0]]);
                            """,
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(WRITE, ":foo:x", "cell1", tmp(1)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":foo:x", "10"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalMemoryAndGlobalIndex() {
            assertCompilesTo("""
                            noinline void foo(out x)
                                x = ++INDEX;
                            end;
                            
                            INDEX = 0;
                            foo(out cell1[INDEX]);
                            """,
                    createInstruction(SET, ".INDEX", "0"),
                    createInstruction(SET, tmp(0), ".INDEX"),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(WRITE, ":foo:x", "cell1", tmp(0)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ".INDEX", ".INDEX", "1"),
                    createInstruction(SET, ":foo:x", ".INDEX"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")

            );
        }

        @Test
        void compilesRecursiveOutParameters() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate stack in bank1;
                            def foo(out n)
                                n = 4;
                                foo();
                            end;
                            print(foo(out z), z);
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":z", ":foo:n"),
                    createInstruction(SET, tmp(0), ":foo*retval"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(PRINT, ":z"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":foo:n", "4"),
                    createInstruction(CALLREC, "bank1", label(0), label(4), ":foo*retval"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(1), ":foo*retval"),
                    createInstruction(SET, ":foo*retval", tmp(1)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURNREC, "bank1")
            );
        }
    }

    @Nested
    class InputOutputArgumentFunctions {
        @Test
        void compilesUserFunctionCall() {
            assertCompilesTo("""
                            void foo(in out x)
                                x += 10;
                            end;
                            
                            z = 10;
                            foo(out z);
                            """,
                    createInstruction(SET, ":z", "10"),
                    createInstruction(SET, ":fn0:x", ":z"),
                    createInstruction(OP, "add", ":fn0:x", ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":z", ":fn0:x")
            );
        }

        @Test
        void compilesMultipleInlineFunctionCalls() {
            assertCompilesTo("""
                            inline void foo(in out x)
                                x += 10;
                            end;
                            
                            y = z = 10;
                            foo(out y);
                            foo(out z);
                            """,
                    createInstruction(SET, ":z", "10"),
                    createInstruction(SET, ":y", ":z"),
                    createInstruction(SET, ":fn0:x", ":y"),
                    createInstruction(OP, "add", ":fn0:x", ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":y", ":fn0:x"),
                    createInstruction(SET, ":fn1:x", ":z"),
                    createInstruction(OP, "add", ":fn1:x", ":fn1:x", "10"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":z", ":fn1:x")

            );
        }

        @Test
        void compilesUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate heap in bank1;
                            
                            void foo(in out x)
                                x += 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, tmp(1111)),
                    createInstruction(JUMP, tmp(1111), "equal", "bank1", "null"),
                    createInstruction(READ, tmp(0), "bank1", "0"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(SET, ":fn0:x", tmp(1)),
                    createInstruction(OP, "add", ":fn0:x", ":fn0:x", "10"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(WRITE, ":fn0:x", "bank1", "0")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate heap in bank1;
                            
                            noinline void foo(in out x)
                                x += 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(READ, tmp(0), "bank1", "0"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(SET, ":foo:x", tmp(1)),
                    createInstruction(SETADDR, ":foo*retaddr", label(2)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(WRITE, ":foo:x", "bank1", "0"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":foo:x", ":foo:x", "10"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void refusesNoModifiers() {
            assertGeneratesMessages(expectedMessages().add("Parameter 'x' is declared 'in out' and no 'in' or 'out' argument modifier was used."),
                    """
                            void foo(in out x)
                                x = x + 1;
                            end;
                            foo(x);
                            """);
        }
    }

    @Nested
    class ReferenceFunctions {
        @Test
        void compilesReferenceArgument() {
            assertCompilesTo("""
                            var a = 10;
                            inline def foo(ref n)
                                print(a);
                                n++;
                                print(a);
                            end;
                            foo(ref a);
                            """,
                    createInstruction(SET, ".a", "10"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(SET, tmp(1), ".a"),
                    createInstruction(OP, "add", ".a", ".a", "1"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(SET, tmp(0), ".a"),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesArrayReferenceArgument() {
            // Verifies that locals inside inline functions (which by definition are limited to the function scope)
            // aren't pushed to the stack in recursive functions
            assertCompilesTo("""
                            var a[2] = (1, 2);
                            inline def foo(ref array)
                                print(array[floor(rand(length(array)) + 1)]);
                            end;
                            foo(ref a);
                            """,
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(OP, "rand", tmp(1), "2"),
                    createInstruction(OP, "add", tmp(2), tmp(1), "1"),
                    createInstruction(OP, "floor", tmp(3), tmp(2)),
                    createInstruction(SET, tmp(4), tmp(3)),
                    createInstruction(READARR, tmp(6), ".a[]", tmp(4)),
                    createInstruction(PRINT, tmp(6)),
                    createInstruction(SET, tmp(0), tmp(6)),
                    createInstruction(LABEL, label(0))
            );
        }
    }

    @Nested
    class RecursiveFunctions {
        @Test
        void compilesIndirectRecursion() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate stack in bank1[0...512];
                            def bar(n) 1 - foo(n); end;
                            def foo(n) 1 + bar(n); end;
                            print(foo(4));
                            """,
                    // Setting up
                    createInstruction(LABEL, label(2)),
                    createInstruction(JUMP, label(2), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    // call foo
                    createInstruction(SET, ":foo:n", "4"),
                    createInstruction(CALLREC, "bank1", label(1), label(3), ":foo*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(0), ":foo*retval"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(END),
                    // def foo
                    createInstruction(LABEL, label(1)),
                    // call bar
                    createInstruction(PUSH, "bank1", ":foo:n"),
                    createInstruction(SET, ":bar:n", ":foo:n"),
                    createInstruction(CALLREC, "bank1", label(0), label(5), ":bar*retval"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(POP, "bank1", ":foo:n"),
                    createInstruction(SET, tmp(1), ":bar*retval"),
                    createInstruction(OP, "add", tmp(2), "1", tmp(1)),
                    createInstruction(SET, ":foo*retval", tmp(2)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(RETURNREC, "bank1"),
                    createInstruction(END),
                    // def bar
                    createInstruction(LABEL, label(0)),
                    // call foo
                    createInstruction(PUSH, "bank1", ":bar:n"),
                    createInstruction(SET, ":foo:n", ":bar:n"),
                    createInstruction(CALLREC, "bank1", label(1), label(7), ":foo*retval"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(POP, "bank1", ":bar:n"),
                    createInstruction(SET, tmp(3), ":foo*retval"),
                    createInstruction(OP, "sub", tmp(4), "1", tmp(3)),
                    createInstruction(SET, ":bar*retval", tmp(4)),
                    createInstruction(LABEL, label(6)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        @Test
        void doesNotPushInlineFunctionVariables() {
            // Verifies that locals inside inline functions (which by definition are limited to the function scope)
            // aren't pushed to the stack in recursive functions
            assertCompilesTo("""
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
                    PushOrPopInstruction.class::isInstance,
                    createInstruction(PUSH, "bank1", ":fn0:r"),
                    createInstruction(POP, "bank1", ":fn0:r")
            );
        }

        @Test
        void pushesParametersToStack() {
            assertCompilesTo("""
                            allocate stack in bank1;
                            def foo(n)
                                foo(n - 1);
                            end;
                            foo(5);
                            """,
                    ix -> ix instanceof PushOrPopInstruction p && p.getVariable().getName().equals("n"),
                    createInstruction(PUSH, "bank1", ":fn0:n"),
                    createInstruction(POP, "bank1", ":fn0:n")
            );
        }

        @Test
        void pushesLocalVariablesToStack() {
            assertCompilesTo("""
                            allocate stack in bank1;
                            def foo(n)
                                a = n - 1;
                                foo(a);
                            end;
                            foo(5);
                            """,
                    ix -> ix instanceof PushOrPopInstruction p && p.getVariable().getName().equals("a"),
                    createInstruction(PUSH, "bank1", ":fn0:a"),
                    createInstruction(POP, "bank1", ":fn0:a")
            );
        }

        @Test
        void handlesRecursiveReturn() {
            assertCompilesTo("""
                            guarded bank1;
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
                    // Setting up
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    // call gdc
                    createInstruction(SET, ":gdc:a", "115"),
                    createInstruction(SET, ":gdc:b", "78"),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":gdc*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), ":gdc*retval"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(END),
                    // def gdc
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "equal", tmp(1), ":gdc:b", "0"),
                    createInstruction(JUMP, label(4), "equal", tmp(1), "false"),
                    // return a
                    createInstruction(SET, ":gdc*retval", ":gdc:a"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(LABEL, label(4)),
                    // call gdc
                    createInstruction(OP, "mod", tmp(3), ":gdc:a", ":gdc:b"),
                    createInstruction(PUSH, "bank1", ":gdc:a"),
                    createInstruction(PUSH, "bank1", ":gdc:b"),
                    createInstruction(SET, tmp(4), ":gdc:b"),
                    createInstruction(SET, ":gdc:a", tmp(4)),
                    createInstruction(SET, ":gdc:b", tmp(3)),
                    createInstruction(CALLREC, "bank1", label(0), label(6), ":gdc*retval"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(POP, "bank1", ":gdc:b"),
                    createInstruction(POP, "bank1", ":gdc:a"),
                    createInstruction(SET, tmp(5), ":gdc*retval"),
                    // return gdc(...)
                    createInstruction(SET, ":gdc*retval", tmp(5)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, ":gdc*retval", tmp(2)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

        @Test
        void refusesToDeclareRecursiveFunctionsWhenNoStackAround() {
            assertGeneratesMessages(
                    expectedMessages().add("Function 'foo' is recursive and no stack was allocated."),
                    """
                            def foo()
                                foo();
                            end;
                            foo();
                            """
            );
        }

        @Test
        void ignoresRecursiveFunctionsWhenNotCalled() {
            assertCompiles("""
                    def foo()
                        foo();
                    end;
                    """
            );
        }

        @Test
        void refusesRecursiveInlineFunctions() {
            assertGeneratesMessage(
                    "Recursive function 'foo' declared 'inline'.",
                    """
                            allocate stack in cell1;
                            inline def foo(n)
                                foo(n - 1);
                            end;
                            print(foo(1) + foo(2));
                            """
            );
        }
    }

    @Nested
    class RemoteFunctions {

        @Test
        void compilesRemoteVoidFunction() {
            assertCompilesTo("""
                            module test;
                            
                            export void foo(in a, out b)
                                b = 2 * a;
                            end;
                            """,
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, "*signature", q("7633f8c34aded96b:v1")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(WAIT, "1e12"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "mul", tmp(0), "2", ":foo:a"),
                    createInstruction(SET, ":foo:b", tmp(0)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, ":foo*finished", "true"),
                    createInstruction(JUMP, label(2), "always")
            );
        }

        @Test
        void compilesRemoteDefFunction() {
            assertCompilesTo("""
                            module test;
                            
                            export def foo(in out a)
                                return a++ / 2;
                            end;
                            """,
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, "*signature", q("1ea5821adeacf3c4:v1")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(WAIT, "1e12"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(0), ":foo:a"),
                    createInstruction(OP, "add", ":foo:a", ":foo:a", "1"),
                    createInstruction(OP, "div", tmp(1), tmp(0), "2"),
                    createInstruction(SET, ":foo*retval", tmp(1)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(SET, ":foo*retval", "null"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, ":foo*finished", "true"),
                    createInstruction(JUMP, label(2), "always")
            );
        }

        @Test
        void refusesOverloadedRemoteFunctions() {
            assertGeneratesMessage(
                    "Remote function 'foo()' conflicts with remote function 'foo(a)': names of remote functions must be unique.",
                    """
                            module test;
                            
                            export def foo(a) end;
                            export void foo() end;
                            """
            );
        }
    }

    @Nested
    class ReturnStatements {
        @Test
        void compilesReturnFromInlineFunction() {
            assertCompilesTo("""
                            def foo(x)
                                if x == 0 then return 10; end;
                                x - 20;
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(OP, "rand", tmp(0), "10"),
                    createInstruction(SET, ":fn0:x", tmp(0)),
                    createInstruction(OP, "equal", tmp(2), ":fn0:x", "0"),
                    createInstruction(JUMP, label(1), "equal", tmp(2), "false"),
                    createInstruction(SET, tmp(1), "10"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(SET, tmp(3), "null"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(3), "null"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(OP, "sub", tmp(4), ":fn0:x", "20"),
                    createInstruction(SET, tmp(1), tmp(4)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(1))
            );
        }

        @Test
        void compilesReturnFromNoinlineFunction() {
            assertCompilesTo("""
                            noinline def foo(x)
                                if x == 0 then return 10; end;
                                x - 20;
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(OP, "rand", tmp(0), "10"),
                    createInstruction(SET, ":foo:x", tmp(0)),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, ":foo*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "equal", tmp(1), ":foo:x", "0"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "false"),
                    createInstruction(SET, ":foo*retval", "10"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "sub", tmp(3), ":foo:x", "20"),
                    createInstruction(SET, ":foo*retval", tmp(3)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesReturnFromRecursiveFunction() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate stack in bank1;
                            
                            def foo(x)
                                if x <= 0 then return 10; end;
                                foo(x - 20);
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(OP, "rand", tmp(0), "10"),
                    createInstruction(SET, ":foo:x", tmp(0)),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(1), ":foo*retval"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "lessThanEq", tmp(2), ":foo:x", "0"),
                    createInstruction(JUMP, label(4), "equal", tmp(2), "false"),
                    createInstruction(SET, ":foo*retval", "10"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(SET, tmp(3), "null"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(3), "null"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "sub", tmp(4), ":foo:x", "20"),
                    createInstruction(PUSH, "bank1", ":foo:x"),
                    createInstruction(PUSH, "bank1", tmp(3)),
                    createInstruction(SET, ":foo:x", tmp(4)),
                    createInstruction(CALLREC, "bank1", label(0), label(6), ":foo*retval"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(POP, "bank1", tmp(3)),
                    createInstruction(POP, "bank1", ":foo:x"),
                    createInstruction(SET, tmp(5), ":foo*retval"),
                    createInstruction(SET, ":foo*retval", tmp(5)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURNREC, "bank1")
            );
        }
    }

    @Nested
    class MindustryErrors {
        @Test
        void refusesWrongAlignment() {
            assertGeneratesMessageRegex(
                    "Invalid value ':fluffyBunny' for keyword parameter: allowed values are .*",
                    "ulocate(:building, :fluffyBunny, true);");
        }
    }

    @Nested
    class MindustryFunctions {
        @Test
        void compilesFunctionCall() {
            assertCompilesTo("""
                            unit = ubind(@poly);
                            """,
                    createInstruction(UBIND, "@poly"),
                    createInstruction(SET, tmp(0), "@unit"),
                    createInstruction(SET, ":unit", tmp(0))
            );
        }

        @Test
        void compilesEndFunctionCall() {
            assertCompilesTo(
                    "if some_cond == false then end(); end;",
                    createInstruction(OP, "equal", tmp(0), ":some_cond", "false"),
                    createInstruction(JUMP, label(0), "equal", tmp(0), "false"),
                    createInstruction(END),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesOutputVariables() {
            assertCompilesTo("""
                            building = getBlock(20, 30, out type, out floor);
                            """,
                    createInstruction(UCONTROL, "getBlock", "20", "30", ":type", tmp(0), ":floor"),
                    createInstruction(SET, ":building", tmp(0))
            );
        }

        @Test
        void compilesOutputExternalVariables() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate heap in bank1;
                            $building = getBlock(20, 30, out $type, out $floor);
                            """,
                    createInstruction(LABEL, tmp(1111)),
                    createInstruction(JUMP, tmp(1111), "equal", "bank1", "null"),
                    createInstruction(UCONTROL, "getBlock", "20", "30", tmp(2), tmp(5), tmp(4)),
                    createInstruction(WRITE, tmp(2), "bank1", "1"),
                    createInstruction(WRITE, tmp(4), "bank1", "2"),
                    createInstruction(WRITE, tmp(5), "bank1", "0")
            );
        }

        @Test
        void compilesComplexMathExpression() {
            assertCompilesTo(
                    "x = ceil(floor(sin(log(cos(abs(tan(rand(1))))))));",
                    createInstruction(OP, "rand", tmp(0), "1"),
                    createInstruction(OP, "tan", tmp(1), tmp(0)),
                    createInstruction(OP, "abs", tmp(2), tmp(1)),
                    createInstruction(OP, "cos", tmp(3), tmp(2)),
                    createInstruction(OP, "log", tmp(4), tmp(3)),
                    createInstruction(OP, "sin", tmp(5), tmp(4)),
                    createInstruction(OP, "floor", tmp(6), tmp(5)),
                    createInstruction(OP, "ceil", tmp(7), tmp(6)),
                    createInstruction(SET, ":x", tmp(7))
            );
        }

        @Test
        void reportsWrongKeywords() {
            assertGeneratesMessage(
                    "Invalid value ':building' for keyword parameter: allowed values are ':floor', ':ore', ':block'.",
                    "setblock(:building, @core-nucleus, x, y, 0, 0);"
            );
        }
    }

    @Nested
    class MindustryMethods {
        @Test
        void compilesSimpleMethodCall() {
            assertCompilesTo("""
                            message1.printflush();
                            """,
                    createInstruction(PRINTFLUSH, "message1")
            );
        }

        @Test
        void compilesChainedMethodCall() {
            assertCompilesTo("""
                            getlink(1).printflush();
                            """,
                    createInstruction(GETLINK, tmp(0), "1"),
                    createInstruction(PRINTFLUSH, tmp(0))
            );
        }

        @Test
        void refusesUnknownMethods() {
            assertGeneratesMessage(
                    "Unknown method 'fluffyBunny'.",
                    "cell1.fluffyBunny(Hohoho);"
            );
        }

        @Test
        void refusesWrongArguments() {
            assertGeneratesMessages(expectedMessages().add("Function 'printflush': wrong number of arguments (expected 0, found 1)."),
                    """
                            message1.printflush("Yadada");
                            """);
        }
    }

    @Nested
    class Mindustry7Functions {

        @Test
        void compilesDrawings() {
            assertCompilesTo("""
                            clear(r, g, b);
                            color(r, g, b, alpha);
                            stroke(width);
                            line(x1, y1, x2, y2);
                            rect(x, y, w, h);
                            lineRect(x, y, w, h);
                            poly(x, y, sides, radius, rotation);
                            linePoly(x, y, sides, radius, rotation);
                            triangle(x1, y1, x2, y2, x3, y3);
                            image(x, y, @copper, size, rotation);
                            drawflush(display1);
                            """,
                    createInstruction(DRAW, "clear", ":r", ":g", ":b"),
                    createInstruction(DRAW, "color", ":r", ":g", ":b", ":alpha"),
                    createInstruction(DRAW, "stroke", ":width"),
                    createInstruction(DRAW, "line", ":x1", ":y1", ":x2", ":y2"),
                    createInstruction(DRAW, "rect", ":x", ":y", ":w", ":h"),
                    createInstruction(DRAW, "lineRect", ":x", ":y", ":w", ":h"),
                    createInstruction(DRAW, "poly", ":x", ":y", ":sides", ":radius", ":rotation"),
                    createInstruction(DRAW, "linePoly", ":x", ":y", ":sides", ":radius", ":rotation"),
                    createInstruction(DRAW, "triangle", ":x1", ":y1", ":x2", ":y2", ":x3", ":y3"),
                    createInstruction(DRAW, "image", ":x", ":y", "@copper", ":size", ":rotation"),
                    createInstruction(DRAWFLUSH, "display1")
            );
        }

        @Test
        void compilesURadar() {
            assertCompilesTo("""
                            target = uradar(:enemy, :ground, :any, :health, MIN_TO_MAX);
                            if target != null then
                                approach(target.@x, target.@y, 10);
                                if within(target.@x, target.@y, 10) then
                                    target(target.@x, target.@y, SHOOT);
                                end;
                            end;
                            """,
                    createInstruction(URADAR, "enemy", "ground", "any", "health", "0", ".MIN_TO_MAX", tmp(0)),
                    createInstruction(SET, ":target", tmp(0)),
                    createInstruction(OP, "notEqual", tmp(1), ":target", "null"),
                    createInstruction(JUMP, label(0), "equal", tmp(1), "false"),
                    createInstruction(SENSOR, tmp(3), ":target", "@x"),
                    createInstruction(SENSOR, tmp(4), ":target", "@y"),
                    createInstruction(UCONTROL, "approach", tmp(3), tmp(4), "10"),
                    createInstruction(SENSOR, tmp(5), ":target", "@x"),
                    createInstruction(SENSOR, tmp(6), ":target", "@y"),
                    createInstruction(UCONTROL, "within", tmp(5), tmp(6), "10", tmp(7)),
                    createInstruction(JUMP, label(2), "equal", tmp(7), "false"),
                    createInstruction(SENSOR, tmp(9), ":target", "@x"),
                    createInstruction(SENSOR, label(10), ":target", "@y"),
                    createInstruction(UCONTROL, "target", tmp(9), label(10), ".SHOOT"),
                    createInstruction(SET, tmp(8), "null"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(8), "null"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), tmp(8)),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesULocate() {
            assertCompilesTo("""
                            ulocate(:ore, @surge-alloy, out x, out y);
                            building = ulocate(:building, :core, ENEMY, out x, out y);
                            building = ulocate(:spawn, out x, out y);
                            building = ulocate(:damaged, out x, out y);
                            """,
                    createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", ":x", ":y", tmp(0), tmp(1)),
                    createInstruction(ULOCATE, "building", "core", ".ENEMY", "@copper", ":x", ":y", tmp(3), tmp(2)),
                    createInstruction(SET, ":building", tmp(2)),
                    createInstruction(ULOCATE, "spawn", "core", "true", "@copper", ":x", ":y", tmp(5), tmp(4)),
                    createInstruction(SET, ":building", tmp(4)),
                    createInstruction(ULOCATE, "damaged", "core", "true", "@copper", ":x", ":y", tmp(7), tmp(6)),
                    createInstruction(SET, ":building", tmp(6))
            );
        }

        @Test
        void compilesRadar() {
            assertCompilesTo("""
                            block = radar(:enemy, :any, :any, :distance, salvo1, 1);
                            block = radar(:ally, :flying, :any, :health, lancer1, 1);
                            src = salvo1;
                            block = radar(:enemy, :any, :any, :distance, src, 1);
                            """,
                    createInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", "1", tmp(0)),
                    createInstruction(SET, ":block", tmp(0)),
                    createInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", "1", tmp(1)),
                    createInstruction(SET, ":block", tmp(1)),
                    createInstruction(SET, ":src", "salvo1"),
                    createInstruction(RADAR, "enemy", "any", "any", "distance", ":src", "1", tmp(2)),
                    createInstruction(SET, ":block", tmp(2))
            );
        }

        @Test
        void compilesWait() {
            assertCompilesTo("""
                            wait(1);
                            wait(0.001);
                            wait(1000);
                            """,
                    createInstruction(WAIT, "1"),
                    createInstruction(WAIT, "0.001"),
                    createInstruction(WAIT, "1000")
            );
        }

        @Test
        void compilesNewVersion7Instructions() {
            assertCompilesTo("""
                            col(color);
                            result = asin(a);
                            result = acos(a);
                            result = atan(a);
                            result = lookup(:block, index);
                            result = packcolor(r, g, b, a);
                            wait(sec);
                            payEnter();
                            unbind();
                            building = getBlock(x, y);
                            print(building, result);
                            """,
                    createInstruction(DRAW, "col", ":color"),
                    createInstruction(OP, "asin", tmp(0), ":a"),
                    createInstruction(SET, ":result", tmp(0)),
                    createInstruction(OP, "acos", tmp(1), ":a"),
                    createInstruction(SET, ":result", tmp(1)),
                    createInstruction(OP, "atan", tmp(2), ":a"),
                    createInstruction(SET, ":result", tmp(2)),
                    createInstruction(LOOKUP, "block", tmp(3), ":index"),
                    createInstruction(SET, ":result", tmp(3)),
                    createInstruction(PACKCOLOR, tmp(4), ":r", ":g", ":b", ":a"),
                    createInstruction(SET, ":result", tmp(4)),
                    createInstruction(WAIT, ":sec"),
                    createInstruction(UCONTROL, "payEnter"),
                    createInstruction(UCONTROL, "unbind"),
                    createInstruction(UCONTROL, "getBlock", ":x", ":y", tmp(6), tmp(5), tmp(7)),
                    createInstruction(SET, ":building", tmp(5)),
                    createInstruction(PRINT, ":building"),
                    createInstruction(PRINT, ":result")
            );
        }

        @Test
        void compilesNewPathfind() {
            assertCompilesTo("""
                            pathfind(50, 50);
                            """,
                    createInstruction(UCONTROL, "pathfind", "50", "50")
            );
        }

        @Test
        void compilesNewAutoPathfind() {
            assertCompilesTo("""
                            autoPathfind();
                            """,
                    createInstruction(UCONTROL, "autoPathfind")
            );
        }

    }

    @Nested
    class Mindustry8Functions {

        @Test
        void compilesNewProperties() {
            assertGeneratesMessages(expectedMessages(),
                    """
                            a = getlink(0);
                            print(a.@currentAmmoType);
                            print(a.@armor);
                            print(a.@velocityX);
                            print(a.@velocityY);
                            print(a.@cameraX);
                            print(a.@cameraY);
                            print(a.@cameraWidth);
                            print(a.@cameraHeight);
                            """);
        }

        @Test
        void compilesNewDrawInstructions() {
            assertCompilesTo("""
                            drawPrint(10, 10, @topRight);
                            translate(3, 4);
                            scale(-1, 1);
                            rotate(90);
                            reset();
                            """,
                    createInstruction(DRAW, "print", "10", "10", "@topRight"),
                    createInstruction(DRAW, "translate", "3", "4"),
                    createInstruction(DRAW, "scale", "-1", "1"),
                    createInstruction(DRAW, "rotate", "0", "0", "90"),
                    createInstruction(DRAW, "reset")
            );
        }

        @Test
        void compilesWeatherInstructions() {
            assertCompilesTo(
                    expectedMessages().add("Built-in variable '@fluffyBunny' not recognized."),
                    """
                            active = weathersense(@snow);
                            weatherset(@rain, true);
                            weatherset(@fluffyBunny, false);
                            """,
                    createInstruction(WEATHERSENSE, tmp(0), "@snow"),
                    createInstruction(SET, ":active", tmp(0)),
                    createInstruction(WEATHERSET, "@rain", "true"),
                    createInstruction(WEATHERSET, "@fluffyBunny", "false")
            );
        }

        @Test
        void compilesPlaysound() {
            assertCompilesTo("""
                            playsound(true, @sfx-railgun, 1, 1, 100, 10, true);
                            playsound(false, @sfx-laser, 1, 1, 0.5, false);
                            """,
                    createInstruction(PLAYSOUND, "true", "@sfx-railgun", "1", "1", "0", "100", "10", "true"),
                    createInstruction(PLAYSOUND, "false", "@sfx-laser", "1", "1", "0.5", "0", "0", "false")
            );
        }

        @Test
        void compilesSetMarker() {
            assertCompilesTo("""
                            setmarker(:remove, id);
                            setmarker(:world, id, boolean);
                            setmarker(:minimap, id, boolean);
                            setmarker(:autoscale, id, boolean);
                            setmarker(:pos, id, x, y);
                            setmarker(:endPos, id, x, y);
                            setmarker(:drawLayer, id, layer);
                            setmarker(:color, id, color);
                            setmarker(:radius, id, radius);
                            setmarker(:stroke, id, stroke);
                            setmarker(:rotation, id, rotation);
                            setmarker(:shape, id, sides, fill, outline);
                            setmarker(:arc, id, from, to);
                            setmarker(:flushText, id, fetch);
                            setmarker(:fontSize, id, size);
                            setmarker(:textHeight, id, height);
                            setmarker(:labelFlags, id, background, outline);
                            setmarker(:texture, id, printFlush, name);
                            setmarker(:textureSize, id, width, height);
                            setmarker(:posi, id, index, x, y);
                            setmarker(:uvi, id, index, x, y);
                            setmarker(:colori, id, index, color);
                            """,
                    createInstruction(SETMARKER, "remove", ":id"),
                    createInstruction(SETMARKER, "world", ":id", ":boolean"),
                    createInstruction(SETMARKER, "minimap", ":id", ":boolean"),
                    createInstruction(SETMARKER, "autoscale", ":id", ":boolean"),
                    createInstruction(SETMARKER, "pos", ":id", ":x", ":y"),
                    createInstruction(SETMARKER, "endPos", ":id", ":x", ":y"),
                    createInstruction(SETMARKER, "drawLayer", ":id", ":layer"),
                    createInstruction(SETMARKER, "color", ":id", ":color"),
                    createInstruction(SETMARKER, "radius", ":id", ":radius"),
                    createInstruction(SETMARKER, "stroke", ":id", ":stroke"),
                    createInstruction(SETMARKER, "rotation", ":id", ":rotation"),
                    createInstruction(SETMARKER, "shape", ":id", ":sides", ":fill", ":outline"),
                    createInstruction(SETMARKER, "arc", ":id", ":from", ":to"),
                    createInstruction(SETMARKER, "flushText", ":id", ":fetch"),
                    createInstruction(SETMARKER, "fontSize", ":id", ":size"),
                    createInstruction(SETMARKER, "textHeight", ":id", ":height"),
                    createInstruction(SETMARKER, "labelFlags", ":id", ":background", ":outline"),
                    createInstruction(SETMARKER, "texture", ":id", ":printFlush", ":name"),
                    createInstruction(SETMARKER, "textureSize", ":id", ":width", ":height"),
                    createInstruction(SETMARKER, "posi", ":id", ":index", ":x", ":y"),
                    createInstruction(SETMARKER, "uvi", ":id", ":index", ":x", ":y"),
                    createInstruction(SETMARKER, "colori", ":id", ":index", ":color")
            );
        }

        @Test
        void compilesMakeMarker() {
            assertCompilesTo("""
                            makemarker(:shapeText, id, x, y, replace);
                            """,
                    createInstruction(MAKEMARKER, "shapeText", ":id", ":x", ":y", ":replace")
            );
        }

        @Test
        void compilesLocaleprint() {
            assertCompilesTo("""
                            localeprint(property);
                            """,
                    createInstruction(LOCALEPRINT, ":property")
            );
        }

        @Test
        void compilesPrintChar() {
            assertCompilesTo("""
                            printchar(65);
                            """,
                    createInstruction(PRINTCHAR, "65")
            );
        }

        @Test
        void compilesOpSignInstruction() {
            assertCompilesTo("""
                            print(sign(a));
                            """,
                    createInstruction(OP, "sign", tmp(0), ":a"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void compilesUnpackColorInstruction() {
            assertCompilesTo("""
                            unpackcolor(out r, out g, out b, out a, %[red]);
                            """,
                    createInstruction(UNPACKCOLOR, ":r", ":g", ":b", ":a", "%[red]")
            );
        }
    }

    @Nested
    class WorldProcessorFunctions {

        @Test
        void compilesGetBlock() {
            assertCompilesTo("""
                            result = getblock(:floor, x, y);
                            """,
                    createInstruction(GETBLOCK, "floor", tmp(0), ":x", ":y"),
                    createInstruction(SET, ":result", tmp(0))
            );
        }

        @Test
        void compilesSetBlock() {
            assertCompilesTo("""
                            setblock(:floor, to, x, y);
                            setblock(:ore, to, x, y);
                            setblock(:block, to, x, y, team, rotation);
                            """,
                    createInstruction(SETBLOCK, "floor", ":to", ":x", ":y"),
                    createInstruction(SETBLOCK, "ore", ":to", ":x", ":y"),
                    createInstruction(SETBLOCK, "block", ":to", ":x", ":y", ":team", ":rotation")
            );
        }

        @Test
        void compilesSpawn() {
            assertCompilesTo("""
                            result = spawn(unit, x, y, rotation, team);
                            """,
                    createInstruction(SPAWN, ":unit", ":x", ":y", ":rotation", ":team", tmp(0)),
                    createInstruction(SET, ":result", tmp(0))
            );
        }

        @Test
        void compilesStatus() {
            assertCompilesTo("""
                            applyStatus(:burning, unit, duration);
                            clearStatus(:freezing, unit);
                            """,
                    createInstruction(STATUS, "false", "burning", ":unit", ":duration"),
                    createInstruction(STATUS, "true", "freezing", ":unit")
            );
        }

        @Test
        void compilesSpawnWave() {
            assertCompilesTo("""
                            spawnwave(x, y, natural);
                            """,
                    createInstruction(SPAWNWAVE, ":x", ":y", ":natural")
            );
        }

        @Test
        void compilesSetRule() {
            assertCompilesTo("""
                            setrule(:currentWaveTime, value);
                            setrule(:waveTimer, value);
                            setrule(:waves, value);
                            setrule(:wave, value);
                            setrule(:waveSpacing, value);
                            setrule(:waveSending, value);
                            setrule(:attackMode, value);
                            setrule(:enemyCoreBuildRadius, value);
                            setrule(:dropZoneRadius, value);
                            setrule(:unitCap, value);
                            setrule(:mapArea, x, y, width, height);
                            setrule(:lighting, value);
                            setrule(:ambientLight, value);
                            setrule(:solarMultiplier, value);
                            setrule(:buildSpeed, value, team);
                            setrule(:unitBuildSpeed, value, team);
                            setrule(:unitCost, value, team);
                            setrule(:unitDamage, value, team);
                            setrule(:blockHealth, value, team);
                            setrule(:blockDamage, value, team);
                            setrule(:rtsMinWeight, value, team);
                            setrule(:rtsMinSquad, value, team);
                            """,
                    createInstruction(SETRULE, "currentWaveTime", ":value"),
                    createInstruction(SETRULE, "waveTimer", ":value"),
                    createInstruction(SETRULE, "waves", ":value"),
                    createInstruction(SETRULE, "wave", ":value"),
                    createInstruction(SETRULE, "waveSpacing", ":value"),
                    createInstruction(SETRULE, "waveSending", ":value"),
                    createInstruction(SETRULE, "attackMode", ":value"),
                    createInstruction(SETRULE, "enemyCoreBuildRadius", ":value"),
                    createInstruction(SETRULE, "dropZoneRadius", ":value"),
                    createInstruction(SETRULE, "unitCap", ":value"),
                    createInstruction(SETRULE, "mapArea", "0", ":x", ":y", ":width", ":height"),
                    createInstruction(SETRULE, "lighting", ":value"),
                    createInstruction(SETRULE, "ambientLight", ":value"),
                    createInstruction(SETRULE, "solarMultiplier", ":value"),
                    createInstruction(SETRULE, "buildSpeed", ":value", ":team"),
                    createInstruction(SETRULE, "unitBuildSpeed", ":value", ":team"),
                    createInstruction(SETRULE, "unitCost", ":value", ":team"),
                    createInstruction(SETRULE, "unitDamage", ":value", ":team"),
                    createInstruction(SETRULE, "blockHealth", ":value", ":team"),
                    createInstruction(SETRULE, "blockDamage", ":value", ":team"),
                    createInstruction(SETRULE, "rtsMinWeight", ":value", ":team"),
                    createInstruction(SETRULE, "rtsMinSquad", ":value", ":team")
            );
        }

        @Test
        void compilesMessage() {
            assertCompilesTo("""
                            message(:notify, @wait);
                            message(:mission, @wait);
                            message(:announce, duration, out result);
                            message(:toast, duration, out result);
                            """,
                    createInstruction(MESSAGE, "notify", "0", "@wait"),
                    createInstruction(MESSAGE, "mission", "0", "@wait"),
                    createInstruction(MESSAGE, "announce", ":duration", ":result"),
                    createInstruction(MESSAGE, "toast", ":duration", ":result")
            );
        }

        @Test
        void compilesCutscene() {
            assertCompilesTo("""
                            cutscene(:pan, x, y, speed);
                            cutscene(:zoom, level);
                            cutscene(:stop);
                            """,
                    createInstruction(CUTSCENE, "pan", ":x", ":y", ":speed"),
                    createInstruction(CUTSCENE, "zoom", ":level"),
                    createInstruction(CUTSCENE, "stop")
            );
        }

        @Test
        void compilesExplosion() {
            assertCompilesTo("""
                            explosion(team, x, y, radius, damage, air, ground, pierce, true);
                            """,
                    createInstruction(EXPLOSION, ":team", ":x", ":y", ":radius", ":damage", ":air", ":ground", ":pierce", "true")
            );
        }

        @Test
        void compilesSetrate() {
            assertCompilesTo("""
                            setrate(ipt);
                            """,
                    createInstruction(SETRATE, ":ipt")
            );
        }

        @Test
        void compilesFetch() {
            assertCompilesTo("""
                            result = fetch(:unitCount, team, type);
                            result = fetch(:playerCount, team);
                            result = fetch(:coreCount, team);
                            result = fetch(:buildCount, team, type);
                            result = fetch(:unit, team, index, type);
                            result = fetch(:player, team, index);
                            result = fetch(:core, team, index);
                            result = fetch(:build, team, index, type);
                            """,
                    createInstruction(FETCH, "unitCount", tmp(0), ":team", "0", ":type"),
                    createInstruction(SET, ":result", tmp(0)),
                    createInstruction(FETCH, "playerCount", tmp(1), ":team"),
                    createInstruction(SET, ":result", tmp(1)),
                    createInstruction(FETCH, "coreCount", tmp(2), ":team"),
                    createInstruction(SET, ":result", tmp(2)),
                    createInstruction(FETCH, "buildCount", tmp(3), ":team", "0", ":type"),
                    createInstruction(SET, ":result", tmp(3)),
                    createInstruction(FETCH, "unit", tmp(4), ":team", ":index", ":type"),
                    createInstruction(SET, ":result", tmp(4)),
                    createInstruction(FETCH, "player", tmp(5), ":team", ":index"),
                    createInstruction(SET, ":result", tmp(5)),
                    createInstruction(FETCH, "core", tmp(6), ":team", ":index"),
                    createInstruction(SET, ":result", tmp(6)),
                    createInstruction(FETCH, "build", tmp(7), ":team", ":index", ":type"),
                    createInstruction(SET, ":result", tmp(7))
            );
        }

        @Test
        void compilesGetflag() {
            assertCompilesTo("""
                            result = getflag(flag);
                            """,
                    createInstruction(GETFLAG, tmp(0), ":flag"),
                    createInstruction(SET, ":result", tmp(0))
            );
        }

        @Test
        void compilesSetflag() {
            assertCompilesTo("""
                            setflag(flag, value);
                            """,
                    createInstruction(SETFLAG, ":flag", ":value")
            );
        }

        @Test
        void compilesSetProp() {
            assertCompilesTo("""
                            object.setprop(property, value);
                            """,
                    createInstruction(SETPROP, ":property", ":object", ":value")
            );
        }

        @Test
        void compilesSync() {
            assertCompilesTo("""
                            volatile global;
                            sync(global);
                            """,
                    createInstruction(SYNC, ".global")
            );
        }

        @Test
        void refusesLocalVariableForSync() {
            assertGeneratesMessages(expectedMessages()
                            .add("A 'volatile' variable is required in a call to 'sync'."),
                    "sync(local);"
            );
        }

        @Test
        void refusesLiteralForSync() {
            assertGeneratesMessages(expectedMessages()
                            .add("A 'volatile' variable is required in a call to 'sync'."),
                    "sync(10);"
            );
        }

        @Test
        void compilesEffects() {
            assertCompilesTo("""
                            effect(:warn, x, y);
                            effect(:cross, x, y);
                            effect(:blockFall, x, y, @vault);
                            effect(:placeBlock, x, y, size);
                            effect(:placeBlockSpark, x, y, size);
                            effect(:breakBlock, x, y, size);
                            effect(:spawn, x, y);
                            effect(:trail, x, y, size, color);
                            effect(:breakProp, x, y, size, color);
                            effect(:smokeCloud, x, y, color);
                            effect(:vapor, x, y, color);
                            effect(:hit, x, y, color);
                            effect(:hitSquare, x, y, color);
                            effect(:shootSmall, x, y, rotation, color);
                            effect(:shootBig, x, y, rotation, color);
                            effect(:smokeSmall, x, y, color);
                            effect(:smokeBig, x, y, color);
                            effect(:smokeColor, x, y, rotation, color);
                            effect(:smokeSquare, x, y, rotation, color);
                            effect(:smokeSquareBig, x, y, rotation, color);
                            effect(:spark, x, y, color);
                            effect(:sparkBig, x, y, color);
                            effect(:sparkShoot, x, y, rotation, color);
                            effect(:sparkShootBig, x, y, rotation, color);
                            effect(:drill, x, y, color);
                            effect(:drillBig, x, y, color);
                            effect(:lightBlock, x, y, size, color);
                            effect(:explosion, x, y, size);
                            effect(:smokePuff, x, y, color);
                            effect(:sparkExplosion, x, y, color);
                            effect(:crossExplosion, x, y, size, color);
                            effect(:wave, x, y, size, color);
                            effect(:bubble, x, y);
                            """,
                    createInstruction(EFFECT, "warn", ":x", ":y"),
                    createInstruction(EFFECT, "cross", ":x", ":y"),
                    createInstruction(EFFECT, "blockFall", ":x", ":y", "0", "0", "@vault"),
                    createInstruction(EFFECT, "placeBlock", ":x", ":y", ":size"),
                    createInstruction(EFFECT, "placeBlockSpark", ":x", ":y", ":size"),
                    createInstruction(EFFECT, "breakBlock", ":x", ":y", ":size"),
                    createInstruction(EFFECT, "spawn", ":x", ":y"),
                    createInstruction(EFFECT, "trail", ":x", ":y", ":size", ":color"),
                    createInstruction(EFFECT, "breakProp", ":x", ":y", ":size", ":color"),
                    createInstruction(EFFECT, "smokeCloud", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "vapor", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "hit", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "hitSquare", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "shootSmall", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "shootBig", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "smokeSmall", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "smokeBig", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "smokeColor", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "smokeSquare", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "smokeSquareBig", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "spark", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "sparkBig", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "sparkShoot", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "sparkShootBig", ":x", ":y", ":rotation", ":color"),
                    createInstruction(EFFECT, "drill", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "drillBig", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "lightBlock", ":x", ":y", ":size", ":color"),
                    createInstruction(EFFECT, "explosion", ":x", ":y", ":size"),
                    createInstruction(EFFECT, "smokePuff", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "sparkExplosion", ":x", ":y", "0", ":color"),
                    createInstruction(EFFECT, "crossExplosion", ":x", ":y", ":size", ":color"),
                    createInstruction(EFFECT, "wave", ":x", ":y", ":size", ":color"),
                    createInstruction(EFFECT, "bubble", ":x", ":y")
            );
        }

    }

    @Nested
    class Modifiers {

        @Test
        void compilesAllSupportedModifiersInFunctionDeclaration() {
            assertCompiles("""
                    def foo(a, in b, out c, in out d, out in e)
                        x = a + b + d + e;
                        c = d = e = 2 * x;
                    end;
                    """);
        }

        @Test
        void compilesAllSupportedModifiersInFunctionCall() {
            assertCompiles("""
                    def foo(in b, out c, in out d)
                        null;
                    end;
                    
                    s = t = 0;
                    foo(in 10, out s, in out t);
                    foo(10, , in t);
                    """);
        }
    }

    @Nested
    class UnusedArguments {
        @Test
        void compilesInlineUnusedOutParameters() {
            assertCompilesTo("""
                            inline def foo(out n, out m)
                                n = 10;
                                m = 20;
                            end;
                            foo(, out b);
                            foo(out a);
                            foo();
                            """,
                    createInstruction(SET, ":fn0:n", "10"),
                    createInstruction(SET, ":fn0:m", "20"),
                    createInstruction(SET, tmp(0), ":fn0:m"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":b", ":fn0:m"),
                    createInstruction(SET, ":fn1:n", "10"),
                    createInstruction(SET, ":fn1:m", "20"),
                    createInstruction(SET, tmp(1), ":fn1:m"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":a", ":fn1:n"),
                    createInstruction(SET, ":fn2:n", "10"),
                    createInstruction(SET, ":fn2:m", "20"),
                    createInstruction(SET, tmp(2), ":fn2:m"),
                    createInstruction(LABEL, label(2))
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
                            foo();
                            """,
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":b", ":foo:m"),
                    createInstruction(SET, tmp(0), ":foo*retval"),
                    createInstruction(SETADDR, ":foo*retaddr", label(2)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":a", ":foo:n"),
                    createInstruction(SET, tmp(1), ":foo*retval"),
                    createInstruction(SETADDR, ":foo*retaddr", label(3)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), ":foo*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":foo:n", "10"),
                    createInstruction(SET, ":foo:m", "20"),
                    createInstruction(SET, ":foo*retval", ":foo:m"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesRecursiveUnusedOutParameters() {
            assertCompilesTo("""
                            guarded bank1;
                            allocate stack in bank1;
                            noinline def foo(out n, out m)
                                n = 10;
                                m = 20;
                                foo(out n, out m);
                            end;
                            foo(, out b);
                            foo(out a);
                            foo();
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":b", ":foo:m"),
                    createInstruction(SET, tmp(0), ":foo*retval"),
                    createInstruction(CALLREC, "bank1", label(0), label(3), ":foo*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, ":a", ":foo:n"),
                    createInstruction(SET, tmp(1), ":foo*retval"),
                    createInstruction(CALLREC, "bank1", label(0), label(4), ":foo*retval"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(2), ":foo*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":foo:n", "10"),
                    createInstruction(SET, ":foo:m", "20"),
                    createInstruction(CALLREC, "bank1", label(0), label(6), ":foo*retval"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, ":foo:n", ":foo:n"),
                    createInstruction(SET, ":foo:m", ":foo:m"),
                    createInstruction(SET, tmp(3), ":foo*retval"),
                    createInstruction(SET, ":foo*retval", tmp(3)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(RETURNREC, "bank1")
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
                    createInstruction(SET, ":b", "1"),
                    createInstruction(SET, ":a", ":b"),
                    createInstruction(SET, ":fn0:n", ":a"),
                    createInstruction(SET, ":fn0:m", ":b"),
                    createInstruction(PRINT, ":fn0:m"),
                    createInstruction(PRINT, ":fn0:n"),
                    createInstruction(OP, "mul", ":fn0:n", ":fn0:n", "2"),
                    createInstruction(OP, "mul", ":fn0:m", ":fn0:m", "2"),
                    createInstruction(SET, tmp(0), ":fn0:m"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":a", ":fn0:n"),
                    createInstruction(SET, ":fn1:n", ":a"),
                    createInstruction(SET, ":fn1:m", ":b"),
                    createInstruction(PRINT, ":fn1:m"),
                    createInstruction(PRINT, ":fn1:n"),
                    createInstruction(OP, "mul", ":fn1:n", ":fn1:n", "2"),
                    createInstruction(OP, "mul", ":fn1:m", ":fn1:m", "2"),
                    createInstruction(SET, tmp(1), ":fn1:m"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":b", ":fn1:m")
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
                    createInstruction(SET, ":b", "1"),
                    createInstruction(SET, ":a", ":b"),
                    createInstruction(SET, ":foo:n", ":a"),
                    createInstruction(SET, ":foo:m", ":b"),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":a", ":foo:n"),
                    createInstruction(SET, tmp(0), ":foo*retval"),
                    createInstruction(SET, ":foo:n", ":a"),
                    createInstruction(SET, ":foo:m", ":b"),
                    createInstruction(SETADDR, ":foo*retaddr", label(2)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":b", ":foo:m"),
                    createInstruction(SET, tmp(1), ":foo*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, ":foo:m"),
                    createInstruction(PRINT, ":foo:n"),
                    createInstruction(OP, "mul", ":foo:n", ":foo:n", "2"),
                    createInstruction(OP, "mul", ":foo:m", ":foo:m", "2"),
                    createInstruction(SET, ":foo*retval", ":foo:m"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesRecursiveUnusedInOutParameters() {
            assertCompilesTo("""
                            guarded bank1;
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
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(SET, ":b", "1"),
                    createInstruction(SET, ":a", ":b"),
                    createInstruction(SET, ":foo:n", ":a"),
                    createInstruction(SET, ":foo:m", ":b"),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":a", ":foo:n"),
                    createInstruction(SET, tmp(0), ":foo*retval"),
                    createInstruction(SET, ":foo:n", ":a"),
                    createInstruction(SET, ":foo:m", ":b"),
                    createInstruction(CALLREC, "bank1", label(0), label(3), ":foo*retval"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, ":b", ":foo:m"),
                    createInstruction(SET, tmp(1), ":foo*retval"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, ":foo:m"),
                    createInstruction(PRINT, ":foo:n"),
                    createInstruction(OP, "mul", ":foo:n", ":foo:n", "2"),
                    createInstruction(OP, "mul", ":foo:m", ":foo:m", "2"),
                    createInstruction(SET, ":foo:n", ":foo:n"),
                    createInstruction(SET, ":foo:m", ":foo:m"),
                    createInstruction(CALLREC, "bank1", label(0), label(5), ":foo*retval"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, ":foo:n", ":foo:n"),
                    createInstruction(SET, ":foo:m", ":foo:m"),
                    createInstruction(SET, tmp(2), ":foo*retval"),
                    createInstruction(SET, ":foo*retval", tmp(2)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(RETURNREC, "bank1")
            );
        }

    }

    @Nested
    class VarargFunctions {
        @Test
        void compilesVarargFunction() {
            assertCompilesTo("""
                            inline void foo(args...)
                                print(args);
                            end;
                            foo(1, 2, 3);
                            foo(1);
                            """,
                    createInstruction(PRINT, "1"),
                    createInstruction(PRINT, "2"),
                    createInstruction(PRINT, "3"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, "1"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesVarargFunctionCall() {
            assertCompilesTo("""
                            inline void foo(args...)
                                print(args);
                            end;
                            inline void bar(args...)
                                foo(args, args);
                            end;
                            bar(1, 2, 3);
                            """,
                    createInstruction(PRINT, "1"),
                    createInstruction(PRINT, "2"),
                    createInstruction(PRINT, "3"),
                    createInstruction(PRINT, "1"),
                    createInstruction(PRINT, "2"),
                    createInstruction(PRINT, "3"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void resolvesVarargFunctions() {
            assertCompilesTo("""
                            inline void foo(a) print("one"); end;
                            inline void foo(a, b) print("two"); end;
                            inline void foo(a...) print("vararg", length(a)); end;
                            
                            foo();
                            foo(1);
                            foo(1, 2);
                            foo(1, 2, 3);
                            """,
                    createInstruction(PRINT, q("vararg")),
                    createInstruction(PRINT, "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, ":fn1:a", "1"),
                    createInstruction(PRINT, q("one")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":fn2:a", "1"),
                    createInstruction(SET, ":fn2:b", "2"),
                    createInstruction(PRINT, q("two")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, q("vararg")),
                    createInstruction(PRINT, "3"),
                    createInstruction(LABEL, label(3))
            );
        }

        @Test
        void resolvesVarargFunctions2() {
            assertCompilesTo("""
                            #set optimization = none;
                            inline void foo(arg...)
                                bar(arg);
                            end;
                            
                            void bar(a, b, out c)
                                c = a + b;
                            end;
                            
                            foo(1, 2, out result);
                            foo(1, 2);
                            """,
                    createInstruction(SET, ":bar:a", "1"),
                    createInstruction(SET, ":bar:b", "2"),
                    createInstruction(SETADDR, ":bar*retaddr", label(2)),
                    createInstruction(CALL, label(0), "*invalid", ":bar*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, ":result", ":bar:c"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, ":bar:a", "1"),
                    createInstruction(SET, ":bar:b", "2"),
                    createInstruction(SETADDR, ":bar*retaddr", label(4)),
                    createInstruction(CALL, label(0), "*invalid", ":bar*retval"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(0), ":bar:a", ":bar:b"),
                    createInstruction(SET, ":bar:c", tmp(0)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(RETURN, ":bar*retaddr")
            );
        }
    }

    @Nested
    class VoidFunctions {

        @Test
        void compilesInlineVoidFunction() {
            // Note: the function doesn't set a function return variable
            assertCompilesTo("""
                            inline void foo()
                                print("foo");
                            end;
                            foo();
                            """,
                    createInstruction(PRINT, q("foo")),
                    createInstruction(LABEL, label(1))
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
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("foo")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }

        @Test
        void compilesRecursiveVoidFunction() {
            // Note: the function doesn't set a function return variable
            assertCompilesTo("""
                            guarded bank1;
                            allocate stack in bank1;
                            void foo()
                                foo();
                                print("foo");
                            end;
                            foo();
                            """,
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                    createInstruction(SET, "*sp", "0"),
                    createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(CALLREC, "bank1", label(0), label(4), ":foo*retval"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(PRINT, q("foo")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(RETURNREC, "bank1")
            );
        }
    }
}
