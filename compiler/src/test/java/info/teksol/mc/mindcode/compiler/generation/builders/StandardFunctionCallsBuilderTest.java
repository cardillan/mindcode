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
    class Errors {
        @Test
        void refusesUppercaseFunctionParameter() {
            assertGeneratesMessages(
                    expectedMessages().add("Parameter 'N' of function 'foo' uses name reserved for global variables."),
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
            assertGeneratesMessage("Parameter 'switch1' of function 'foo' uses name reserved for linked blocks.",
                    """
                            def foo(switch1)
                                switch1;
                            end;
                            print(foo(1) + foo(2));
                            """
            );
        }

        @Test
        void refusesFormattablesAsArguments() {
            assertGeneratesMessage("A formattable string literal can only be used as a first argument to the print or remark function.",
                    """
                            inline void foo(x) print(x); end;
                            foo($"Hello");
                            """);
        }

        @Test
        void refusesBlockNameAsFunctionOutputArgument() {
            assertGeneratesMessage("Assignment to constant or parameter 'message1' not allowed.",
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
            assertGeneratesMessages(
                    expectedMessages().add("Function 'foo': wrong number of arguments (expected 1, found 0)."),
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
                    expectedMessages().add("Function 'foo': wrong number of arguments (expected 1, found 2)."),
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
                    expectedMessages().addRegex("Expression doesn't have any value\\..*"),
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
                    expectedMessages().addRegex("Expression doesn't have any value\\..*"),
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
            assertGeneratesMessages(
                    expectedMessages().addRegex("Expression doesn't have any value\\..*"),
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
                    expectedMessages().add("Parameter 'a' isn't optional, a value must be provided."),
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
                    expectedMessages().add("Parameter 'a' is declared 'in out' and no 'in' or 'out' argument modifier was used."),
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
                    expectedMessages()
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
                    expectedMessages().add("Parameter 'a' isn't output, 'out' modifier not allowed."),
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
                    expectedMessages().add("Parameter 'a' is output and 'out' modifier was not used."),
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
                            .add(3, 1, "Function 'foo(a, out b)' conflicts with function 'foo(a)'.")
                            .add(4, 1, "Function 'foo(a, in out b)' conflicts with function 'foo(a, b)'.")
                            .add(4, 1, "Function 'foo(a, in out b)' conflicts with function 'foo(a, out b)'."),
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
            assertGeneratesMessages(
                    expectedMessages()
                            .add(2, 1, "Function 'foo(a, b, c, d...)' conflicts with function 'foo(a, b, c...)'."),
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
                            .add(6, 12, "Parameter 'b' isn't output, 'out' modifier not allowed.")
                            .add(7, 11, "Parameter 'c' isn't output, 'out' modifier not allowed.")
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
                            .add(1, 16, "Parameter 'type' is output and 'out' modifier was not used.")
                            .add(1, 22, "Parameter 'floor' is output and 'out' modifier was not used."),
                    """
                            getBlock(x, y, type, floor);
                            """
            );
        }

        @Test
        void refusesNoModifiers() {
            assertGeneratesMessages(expectedMessages().add("Parameter 'x' is output and 'out' modifier was not used."),
                    """
                            void foo(out x)
                                x = 10;
                            end;
                            foo(x);
                            """);
        }

        @Test
        void refusesInModifier() {
            assertGeneratesMessages(expectedMessages().add("Parameter 'x' isn't input, 'in' modifier not allowed."),
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
                    createInstruction(SET, "__fn0_x", q("Hello")),
                    createInstruction(PRINT, "__fn0_x"),
                    createInstruction(LABEL, var(1000))
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
                    createInstruction(SET, "__fn0_x", q("Hello, ")),
                    createInstruction(PRINT, "__fn0_x"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn1_x", q("Dolly")),
                    createInstruction(PRINT, "__fn1_x"),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(SET, "__fn0_x", q("Hello")),
                    createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1001), "__fn0"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, "__fn0_x"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
            );
        }

        @Test
        void compilesNestedFunctionCallsInline() {
            assertCompilesTo("""
                            inline def a(n) n + 1; end;
                            print(a(a(a(4))));
                            """,
                    createInstruction(SET, "__fn0_n", "4"),
                    createInstruction(OP, "add", var(1), "__fn0_n", "1"),
                    createInstruction(SET, var(0), var(1)),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn1_n", var(0)),
                    createInstruction(OP, "add", var(3), "__fn1_n", "1"),
                    createInstruction(SET, var(2), var(3)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "__fn2_n", var(2)),
                    createInstruction(OP, "add", var(5), "__fn2_n", "1"),
                    createInstruction(SET, var(4), var(5)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, var(4))
            );
        }

        @Test
        void compilesNestedFunctionCallsStackless() {
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
        void compilesNestedFunctionCallsRecursive() {
            assertCompilesTo("""
                            allocate stack in bank1[0...512];
                            def a(n) a(n + 1); end;
                            print(a(a(a(4))));
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
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
        void compilesBlocksAndConstantsInUserFunctions() {
            assertCompilesTo("""
                            def foo(block)
                              print(radar(enemy, any, any, distance, block, 1));
                              print(block.radar(ally, flying, any, health, 1));
                              print(radar(enemy, boss, any, distance, lancer1, 1));
                            end;
                            foo(lancer1);
                            """,
                    createInstruction(SET, "__fn0_block", "lancer1"),
                    createInstruction(RADAR, "enemy", "any", "any", "distance", "__fn0_block", "1", var(1)),
                    createInstruction(PRINT, var(1)),
                    createInstruction(RADAR, "ally", "flying", "any", "health", "__fn0_block", "1", var(2)),
                    createInstruction(PRINT, var(2)),
                    createInstruction(RADAR, "enemy", "boss", "any", "distance", "lancer1", "1", var(3)),
                    createInstruction(PRINT, var(3)),
                    createInstruction(SET, var(0), var(3)),
                    createInstruction(LABEL, var(1000))
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
                    createInstruction(SET, "X", "__fn0_x")
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn1_a", "1"),
                    createInstruction(PRINT, "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "__fn2_a", "1"),
                    createInstruction(SET, "__fn2_b", "1"),
                    createInstruction(PRINT, "2"),
                    createInstruction(LABEL, var(1002))
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
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "z", "__fn0_x")
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
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "y", "__fn0_x"),
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "z", "__fn1_x")
            );
        }

        @Test
        void compilesUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(WRITE, "__fn0_x", "bank1", "0")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            
                            noinline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1001), "__fn0"),
                    createInstruction(WRITE, "__fn0_x", "bank1", "0"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(WRITE, "__fn0_x", "cell2", var(1))
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
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1001), "__fn0"),
                    createInstruction(WRITE, "__fn0_x", "cell1", var(1)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
                    createInstruction(SET, "INDEX", "0"),
                    createInstruction(SET, var(0), "INDEX"),
                    createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1001), "__fn0"),
                    createInstruction(WRITE, "__fn0_x", "cell1", var(0)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", "INDEX", "INDEX", "1"),
                    createInstruction(SET, "__fn0_x", "INDEX"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
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
                    createInstruction(SET, "z", "10"),
                    createInstruction(SET, "__fn0_x", "z"),
                    createInstruction(OP, "add", "__fn0_x", "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "z", "__fn0_x")
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
                    createInstruction(SET, "z", "10"),
                    createInstruction(SET, "y", "z"),
                    createInstruction(SET, "__fn0_x", "y"),
                    createInstruction(OP, "add", "__fn0_x", "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "y", "__fn0_x"),
                    createInstruction(SET, "__fn1_x", "z"),
                    createInstruction(OP, "add", "__fn1_x", "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "z", "__fn1_x")

            );
        }

        @Test
        void compilesUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            
                            void foo(in out x)
                                x += 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, "__fn0_x", var(1)),
                    createInstruction(OP, "add", "__fn0_x", "__fn0_x", "10"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(WRITE, "__fn0_x", "bank1", "0")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            
                            noinline void foo(in out x)
                                x += 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, "__fn0_x", var(1)),
                    createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1001), "__fn0"),
                    createInstruction(WRITE, "__fn0_x", "bank1", "0"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", "__fn0_x", "__fn0_x", "10"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
    class RecursiveFunctions {
        @Test
        void compilesIndirectRecursion() {
            assertCompilesTo("""
                            allocate stack in bank1[0...512];
                            def bar(n) 1 - foo(n); end;
                            def foo(n) 1 + bar(n); end;
                            print(foo(4));
                            """,
                    // Setting up
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
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
                    createInstruction(RETURN, "bank1")
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
                    createInstruction(PUSH, "bank1", "__fn0_r"),
                    createInstruction(POP, "bank1", "__fn0_r")
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
                    createInstruction(PUSH, "bank1", "__fn0_n"),
                    createInstruction(POP, "bank1", "__fn0_n")
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
                    // Setting up
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
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
                    createInstruction(SET, var(4), "__fn0_b"),
                    createInstruction(SET, "__fn0_a", var(4)),
                    createInstruction(SET, "__fn0_b", var(3)),
                    createInstruction(CALLREC, "bank1", var(1000), var(1005), "__fn0retval"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(POP, "bank1", "__fn0_b"),
                    createInstruction(POP, "bank1", "__fn0_a"),
                    createInstruction(SET, var(5), "__fn0retval"),
                    // return gdc(...)
                    createInstruction(SET, "__fn0retval", var(5)),
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
            assertGeneratesMessages(
                    expectedMessages().add("Recursive function 'foo' declared 'inline'."),
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
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(SET, "__fn0_x", var(0)),
                    createInstruction(OP, "equal", var(2), "__fn0_x", "0"),
                    createInstruction(JUMP, var(1001), "equal", var(2), "false"),
                    createInstruction(SET, var(1), "10"),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "sub", var(4), "__fn0_x", "20"),
                    createInstruction(SET, var(1), var(4)),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, var(1))
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
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(SET, "__fn0_x", var(0)),
                    createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1001), "__fn0"),
                    createInstruction(PRINT, "__fn0retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "equal", var(1), "__fn0_x", "0"),
                    createInstruction(JUMP, var(1003), "equal", var(1), "false"),
                    createInstruction(SET, "__fn0retval", "10"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "sub", var(3), "__fn0_x", "20"),
                    createInstruction(SET, "__fn0retval", var(3)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
            );
        }

        @Test
        void compilesReturnFromRecursiveFunction() {
            assertCompilesTo("""
                            allocate stack in bank1;
                            
                            def foo(x)
                                if x <= 0 then return 10; end;
                                foo(x - 20);
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(SET, "__sp", "0"),
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(SET, "__fn0_x", var(0)),
                    createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, var(1), "__fn0retval"),
                    createInstruction(PRINT, var(1)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "lessThanEq", var(2), "__fn0_x", "0"),
                    createInstruction(JUMP, var(1003), "equal", var(2), "false"),
                    createInstruction(SET, "__fn0retval", "10"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "sub", var(4), "__fn0_x", "20"),
                    createInstruction(PUSH, "bank1", "__fn0_x"),
                    createInstruction(PUSH, "bank1", var(3)),
                    createInstruction(SET, "__fn0_x", var(4)),
                    createInstruction(CALLREC, "bank1", var(1000), var(1005), "__fn0retval"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(POP, "bank1", var(3)),
                    createInstruction(POP, "bank1", "__fn0_x"),
                    createInstruction(SET, var(5), "__fn0retval"),
                    createInstruction(SET, "__fn0retval", var(5)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(RETURN, "bank1")
            );
        }
    }

    @Nested
    class MindustryErrors {
        @Test
        void refusesWrongAlignment() {
            assertGeneratesMessages(expectedMessages()
                            .add("Invalid value 'fluffyBunny' for parameter 'alignment': allowed values are 'center'," +
                                 " 'top', 'bottom', 'left', 'right', 'topLeft', 'topRight', 'bottomLeft', 'bottomRight'."),
                    "drawPrint(10, 10, fluffyBunny);");
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
                    createInstruction(SET, var(0), "@unit"),
                    createInstruction(SET, "unit", var(0))
            );
        }

        @Test
        void compilesEndFunctionCall() {
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
        void compilesOutputVariables() {
            assertCompilesTo("""
                            building = getBlock(20, 30, out type, out floor);
                            """,
                    createInstruction(UCONTROL, "getBlock", "20", "30", "type", var(0), "floor"),
                    createInstruction(SET, "building", var(0))
            );
        }

        @Test
        void compilesOutputExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            $building = getBlock(20, 30, out $type, out $floor);
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(UCONTROL, "getBlock", "20", "30", var(2), var(5), var(4)),
                    createInstruction(WRITE, var(2), "bank1", "1"),
                    createInstruction(WRITE, var(4), "bank1", "2"),
                    createInstruction(WRITE, var(5), "bank1", "0")
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
        void reportsWrongKeywords() {
            assertGeneratesMessages(expectedMessages()
                            .add("Invalid value 'building' for parameter 'layer': allowed values are 'floor', 'ore', 'block'.")
                            .add("Function 'setblock': wrong number of arguments (expected 4, found 6)."),
                    """
                            setblock(building, @core-nucleus, x, y, 0, 0);
                            """);
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
                    createInstruction(GETLINK, var(0), "1"),
                    createInstruction(PRINTFLUSH, var(0))
            );
        }

        @Test
        void refusesUnknownMethods() {
            assertGeneratesMessages(expectedMessages().add("Undefined function 'fluffyBunny'."),
                    """
                            cell1.fluffyBunny(Hohoho);
                            """);
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
                    createInstruction(DRAW, "clear", "r", "g", "b"),
                    createInstruction(DRAW, "color", "r", "g", "b", "alpha"),
                    createInstruction(DRAW, "stroke", "width"),
                    createInstruction(DRAW, "line", "x1", "y1", "x2", "y2"),
                    createInstruction(DRAW, "rect", "x", "y", "w", "h"),
                    createInstruction(DRAW, "lineRect", "x", "y", "w", "h"),
                    createInstruction(DRAW, "poly", "x", "y", "sides", "radius", "rotation"),
                    createInstruction(DRAW, "linePoly", "x", "y", "sides", "radius", "rotation"),
                    createInstruction(DRAW, "triangle", "x1", "y1", "x2", "y2", "x3", "y3"),
                    createInstruction(DRAW, "image", "x", "y", "@copper", "size", "rotation"),
                    createInstruction(DRAWFLUSH, "display1"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesURadar() {
            assertCompilesTo("""
                            target = uradar(enemy, ground, any, health, MIN_TO_MAX);
                            if target != null then
                                approach(target.@x, target.@y, 10);
                                if within(target.@x, target.@y, 10) then
                                    target(target.@x, target.@y, SHOOT);
                                end;
                            end;
                            """,
                    createInstruction(URADAR, "enemy", "ground", "any", "health", "0", ".MIN_TO_MAX", var(0)),
                    createInstruction(SET, ":target", var(0)),
                    createInstruction(OP, "notEqual", var(1), ":target", "null"),
                    createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                    createInstruction(SENSOR, var(3), ":target", "@x"),
                    createInstruction(SENSOR, var(4), ":target", "@y"),
                    createInstruction(UCONTROL, "approach", var(3), var(4), "10"),
                    createInstruction(SENSOR, var(5), ":target", "@x"),
                    createInstruction(SENSOR, var(6), ":target", "@y"),
                    createInstruction(UCONTROL, "within", var(5), var(6), "10", var(7)),
                    createInstruction(JUMP, var(1002), "equal", var(7), "false"),
                    createInstruction(SENSOR, var(9), ":target", "@x"),
                    createInstruction(SENSOR, var(10), ":target", "@y"),
                    createInstruction(UCONTROL, "target", var(9), var(10), ".SHOOT"),
                    createInstruction(SET, var(8), "null"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(8), "null"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(2), var(8)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesULocate() {
            assertCompilesTo("""
                            ulocate(ore, @surge-alloy, out x, out y);
                            building = ulocate(building, core, ENEMY, out x, out y);
                            building = ulocate(spawn, out x, out y);
                            building = ulocate(damaged, out x, out y);
                            """,
                    createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", ":x", ":y", var(0), var(1)),
                    createInstruction(ULOCATE, "building", "core", ".ENEMY", "@copper", ":x", ":y", var(3), var(2)),
                    createInstruction(SET, ":building", var(2)),
                    createInstruction(ULOCATE, "spawn", "core", "true", "@copper", ":x", ":y", var(5), var(4)),
                    createInstruction(SET, ":building", var(4)),
                    createInstruction(ULOCATE, "damaged", "core", "true", "@copper", ":x", ":y", var(7), var(6)),
                    createInstruction(SET, ":building", var(6))
            );
        }

        @Test
        void compilesRadar() {
            assertCompilesTo("""
                            block = radar(enemy, any, any, distance, salvo1, 1);
                            block = radar(ally, flying, any, health, lancer1, 1);
                            src = salvo1;
                            block = radar(enemy, any, any, distance, src, 1);
                            """,
                    createInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", "1", var(0)),
                    createInstruction(SET, "block", var(0)),
                    createInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", "1", var(1)),
                    createInstruction(SET, "block", var(1)),
                    createInstruction(SET, "src", "salvo1"),
                    createInstruction(RADAR, "enemy", "any", "any", "distance", "src", "1", var(2)),
                    createInstruction(SET, "block", var(2)),
                    createInstruction(END)
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
                    createInstruction(WAIT, "1000"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesNewVersion7Instructions() {
            assertCompilesTo("""
                            col(color);
                            result = asin(a);
                            result = acos(a);
                            result = atan(a);
                            result = lookup(block, index);
                            result = packcolor(r, g, b, a);
                            wait(sec);
                            payEnter();
                            unbind();
                            building = getBlock(x, y);
                            print(building, result);
                            """,
                    createInstruction(DRAW, "col", "color"),
                    createInstruction(OP, "asin", var(0), "a"),
                    createInstruction(SET, "result", var(0)),
                    createInstruction(OP, "acos", var(1), "a"),
                    createInstruction(SET, "result", var(1)),
                    createInstruction(OP, "atan", var(2), "a"),
                    createInstruction(SET, "result", var(2)),
                    createInstruction(LOOKUP, "block", var(3), "index"),
                    createInstruction(SET, "result", var(3)),
                    createInstruction(PACKCOLOR, var(4), "r", "g", "b", "a"),
                    createInstruction(SET, "result", var(4)),
                    createInstruction(WAIT, "sec"),
                    createInstruction(UCONTROL, "payEnter"),
                    createInstruction(UCONTROL, "unbind"),
                    createInstruction(UCONTROL, "getBlock", "x", "y", var(6), var(5), var(7)),
                    createInstruction(SET, "building", var(5)),
                    createInstruction(PRINT, "building"),
                    createInstruction(PRINT, "result"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesNewPathfind() {
            assertCompilesTo("""
                            pathfind(50, 50);
                            """,
                    createInstruction(UCONTROL, "pathfind", "50", "50"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesNewAutoPathfind() {
            assertCompilesTo("""
                            autoPathfind();
                            """,
                    createInstruction(UCONTROL, "autoPathfind"),
                    createInstruction(END)
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
                            drawPrint(10, 10, topRight);
                            translate(3, 4);
                            scale(-1, 1);
                            rotate(90);
                            reset();
                            """,
                    createInstruction(DRAW, "print", "10", "10", "topRight"),
                    createInstruction(DRAW, "translate", "3", "4"),
                    createInstruction(DRAW, "scale", "-1", "1"),
                    createInstruction(DRAW, "rotate", "0", "0", "90"),
                    createInstruction(DRAW, "reset"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesWeatherInstructions() {
            assertCompilesTo(
                    expectedMessages()
                            .add("Built-in variable '@rain' not recognized.")
                            .add("Built-in variable '@fluffyBunny' not recognized.")
                    ,
                    """
                            active = weathersense(@snow);
                            weatherset(@rain, true);
                            weatherset(@fluffyBunny, false);
                            """,
                    createInstruction(WEATHERSENSE, var(0), "@snow"),
                    createInstruction(SET, "active", var(0)),
                    createInstruction(WEATHERSET, "@rain", "true"),
                    createInstruction(WEATHERSET, "@fluffyBunny", "false"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesPlaysound() {
            assertCompilesTo(
                    expectedMessages()
                            .add("Built-in variable '@sfx-railgun' not recognized.")
                            .add("Built-in variable '@sfx-laser' not recognized.")
                    ,
                    """
                            playsound(true, @sfx-railgun, 1, 1, 100, 10, true);
                            playsound(false, @sfx-laser, 1, 1, 0.5, false);
                            """,
                    createInstruction(PLAYSOUND, "true", "@sfx-railgun", "1", "1", "0", "100", "10", "true"),
                    createInstruction(PLAYSOUND, "false", "@sfx-laser", "1", "1", "0.5", "0", "0", "false"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSetMarker() {
            assertCompilesTo("""
                            setmarker(remove, id);
                            setmarker(world, id, boolean);
                            setmarker(minimap, id, boolean);
                            setmarker(autoscale, id, boolean);
                            setmarker(pos, id, x, y);
                            setmarker(endPos, id, x, y);
                            setmarker(drawLayer, id, layer);
                            setmarker(color, id, color);
                            setmarker(radius, id, radius);
                            setmarker(stroke, id, stroke);
                            setmarker(rotation, id, rotation);
                            setmarker(shape, id, sides, fill, outline);
                            setmarker(arc, id, from, to);
                            setmarker(flushText, id, fetch);
                            setmarker(fontSize, id, size);
                            setmarker(textHeight, id, height);
                            setmarker(labelFlags, id, background, outline);
                            setmarker(texture, id, printFlush, name);
                            setmarker(textureSize, id, width, height);
                            setmarker(posi, id, index, x, y);
                            setmarker(uvi, id, index, x, y);
                            setmarker(colori, id, index, color);
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
                            makemarker(shapeText, id, x, y, replace);
                            """,
                    createInstruction(MAKEMARKER, "shapeText", "id", "x", "y", "replace"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesLocaleprint() {
            assertCompilesTo("""
                            localeprint(property);
                            """,
                    createInstruction(LOCALEPRINT, "property"),
                    createInstruction(END)
            );
        }

    }

    @Nested
    class WorldProcessorFunctions {

        @Test
        void compilesGetBlock() {
            assertCompilesTo("""
                            result = getblock(floor, x, y);
                            """,
                    createInstruction(GETBLOCK, "floor", var(0), "x", "y"),
                    createInstruction(SET, "result", var(0)),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSetBlock() {
            assertCompilesTo("""
                            setblock(floor, to, x, y);
                            setblock(ore, to, x, y);
                            setblock(block, to, x, y, team, rotation);
                            """,
                    createInstruction(SETBLOCK, "floor", "to", "x", "y"),
                    createInstruction(SETBLOCK, "ore", "to", "x", "y"),
                    createInstruction(SETBLOCK, "block", "to", "x", "y", "team", "rotation"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSpawn() {
            assertCompilesTo("""
                            result = spawn(unit, x, y, rotation, team);
                            """,
                    createInstruction(SPAWN, "unit", "x", "y", "rotation", "team", var(0)),
                    createInstruction(SET, "result", var(0)),
                    createInstruction(END)
            );
        }

        @Test
        void compilesStatus() {
            assertCompilesTo("""
                            applyStatus(burning, unit, duration);
                            clearStatus(freezing, unit);
                            """,
                    createInstruction(STATUS, "false", "burning", "unit", "duration"),
                    createInstruction(STATUS, "true", "freezing", "unit"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSpawnWave() {
            assertCompilesTo("""
                            spawnwave(x, y, natural);
                            """,
                    createInstruction(SPAWNWAVE, "x", "y", "natural"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSetRule() {
            assertCompilesTo("""
                            setrule(currentWaveTime, value);
                            setrule(waveTimer, value);
                            setrule(waves, value);
                            setrule(wave, value);
                            setrule(waveSpacing, value);
                            setrule(waveSending, value);
                            setrule(attackMode, value);
                            setrule(enemyCoreBuildRadius, value);
                            setrule(dropZoneRadius, value);
                            setrule(unitCap, value);
                            setrule(mapArea, x, y, width, height);
                            setrule(lighting, value);
                            setrule(ambientLight, value);
                            setrule(solarMultiplier, value);
                            setrule(buildSpeed, value, team);
                            setrule(unitBuildSpeed, value, team);
                            setrule(unitCost, value, team);
                            setrule(unitDamage, value, team);
                            setrule(blockHealth, value, team);
                            setrule(blockDamage, value, team);
                            setrule(rtsMinWeight, value, team);
                            setrule(rtsMinSquad, value, team);
                            """,
                    createInstruction(SETRULE, "currentWaveTime", "value"),
                    createInstruction(SETRULE, "waveTimer", "value"),
                    createInstruction(SETRULE, "waves", "value"),
                    createInstruction(SETRULE, "wave", "value"),
                    createInstruction(SETRULE, "waveSpacing", "value"),
                    createInstruction(SETRULE, "waveSending", "value"),
                    createInstruction(SETRULE, "attackMode", "value"),
                    createInstruction(SETRULE, "enemyCoreBuildRadius", "value"),
                    createInstruction(SETRULE, "dropZoneRadius", "value"),
                    createInstruction(SETRULE, "unitCap", "value"),
                    createInstruction(SETRULE, "mapArea", "0", "x", "y", "width", "height"),
                    createInstruction(SETRULE, "lighting", "value"),
                    createInstruction(SETRULE, "ambientLight", "value"),
                    createInstruction(SETRULE, "solarMultiplier", "value"),
                    createInstruction(SETRULE, "buildSpeed", "value", "team"),
                    createInstruction(SETRULE, "unitBuildSpeed", "value", "team"),
                    createInstruction(SETRULE, "unitCost", "value", "team"),
                    createInstruction(SETRULE, "unitDamage", "value", "team"),
                    createInstruction(SETRULE, "blockHealth", "value", "team"),
                    createInstruction(SETRULE, "blockDamage", "value", "team"),
                    createInstruction(SETRULE, "rtsMinWeight", "value", "team"),
                    createInstruction(SETRULE, "rtsMinSquad", "value", "team"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesMessage() {
            assertCompilesTo("""
                            message(notify, @wait);
                            message(mission, @wait);
                            message(announce, duration, out result);
                            message(toast, duration, out result);
                            """,
                    createInstruction(MESSAGE, "notify", "0", "@wait"),
                    createInstruction(MESSAGE, "mission", "0", "@wait"),
                    createInstruction(MESSAGE, "announce", "duration", "result"),
                    createInstruction(MESSAGE, "toast", "duration", "result"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesCutscene() {
            assertCompilesTo("""
                            cutscene(pan, x, y, speed);
                            cutscene(zoom, level);
                            cutscene(stop);
                            """,
                    createInstruction(CUTSCENE, "pan", "x", "y", "speed"),
                    createInstruction(CUTSCENE, "zoom", "level"),
                    createInstruction(CUTSCENE, "stop"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesExplosion() {
            assertCompilesTo("""
                            explosion(team, x, y, radius, damage, air, ground, pierce, true);
                            """,
                    createInstruction(EXPLOSION, "team", "x", "y", "radius", "damage", "air", "ground", "pierce", "true"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSetrate() {
            assertCompilesTo("""
                            setrate(ipt);
                            """,
                    createInstruction(SETRATE, "ipt"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesFetch() {
            assertCompilesTo("""
                            result = fetch(unitCount, team, type);
                            result = fetch(playerCount, team);
                            result = fetch(coreCount, team);
                            result = fetch(buildCount, team, type);
                            result = fetch(unit, team, index, type);
                            result = fetch(player, team, index);
                            result = fetch(core, team, index);
                            result = fetch(build, team, index, type);
                            """,
                    createInstruction(FETCH, "unitCount", var(0), ":team", "0", ":type"),
                    createInstruction(SET, ":result", var(0)),
                    createInstruction(FETCH, "playerCount", var(1), ":team"),
                    createInstruction(SET, ":result", var(1)),
                    createInstruction(FETCH, "coreCount", var(2), ":team"),
                    createInstruction(SET, ":result", var(2)),
                    createInstruction(FETCH, "buildCount", var(3), ":team", "0", ":type"),
                    createInstruction(SET, ":result", var(3)),
                    createInstruction(FETCH, "unit", var(4), ":team", ":index", ":type"),
                    createInstruction(SET, ":result", var(4)),
                    createInstruction(FETCH, "player", var(5), ":team", ":index"),
                    createInstruction(SET, ":result", var(5)),
                    createInstruction(FETCH, "core", var(6), ":team", ":index"),
                    createInstruction(SET, ":result", var(6)),
                    createInstruction(FETCH, "build", var(7), ":team", ":index", ":type"),
                    createInstruction(SET, ":result", var(7))
            );
        }

        @Test
        void compilesGetflag() {
            assertCompilesTo("""
                            result = getflag(flag);
                            """,
                    createInstruction(GETFLAG, var(0), "flag"),
                    createInstruction(SET, "result", var(0)),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSetflag() {
            assertCompilesTo("""
                            setflag(flag, value);
                            """,
                    createInstruction(SETFLAG, "flag", "value"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSetProp() {
            assertCompilesTo("""
                            object.setprop(property, value);
                            """,
                    createInstruction(SETPROP, "property", "object", "value"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesSync() {
            assertCompilesTo("""
                            sync(GLOBAL);
                            """,
                    createInstruction(SYNC, "GLOBAL"),
                    createInstruction(END)
            );
        }

        @Test
        void refusesLocalVariableForSync() {
            assertGeneratesMessages(expectedMessages()
                            .add("A global variable is required in a call to 'sync'."),
                    "sync(local);"
            );
        }

        @Test
        void refusesLiteralForSync() {
            assertGeneratesMessages(expectedMessages()
                            .add("A global variable is required in a call to 'sync'."),
                    "sync(10);"
            );
        }

        @Test
        void compilesEffects() {
            assertCompilesTo("""
                            effect(warn, x, y);
                            effect(cross, x, y);
                            effect(blockFall, x, y, @vault);
                            effect(placeBlock, x, y, size);
                            effect(placeBlockSpark, x, y, size);
                            effect(breakBlock, x, y, size);
                            effect(spawn, x, y);
                            effect(trail, x, y, size, color);
                            effect(breakProp, x, y, size, color);
                            effect(smokeCloud, x, y, color);
                            effect(vapor, x, y, color);
                            effect(hit, x, y, color);
                            effect(hitSquare, x, y, color);
                            effect(shootSmall, x, y, rotation, color);
                            effect(shootBig, x, y, rotation, color);
                            effect(smokeSmall, x, y, color);
                            effect(smokeBig, x, y, color);
                            effect(smokeColor, x, y, rotation, color);
                            effect(smokeSquare, x, y, rotation, color);
                            effect(smokeSquareBig, x, y, rotation, color);
                            effect(spark, x, y, color);
                            effect(sparkBig, x, y, color);
                            effect(sparkShoot, x, y, rotation, color);
                            effect(sparkShootBig, x, y, rotation, color);
                            effect(drill, x, y, color);
                            effect(drillBig, x, y, color);
                            effect(lightBlock, x, y, size, color);
                            effect(explosion, x, y, size);
                            effect(smokePuff, x, y, color);
                            effect(sparkExplosion, x, y, color);
                            effect(crossExplosion, x, y, size, color);
                            effect(wave, x, y, size, color);
                            effect(bubble, x, y);
                            """,
                    createInstruction(EFFECT, "warn", "x", "y"),
                    createInstruction(EFFECT, "cross", "x", "y"),
                    createInstruction(EFFECT, "blockFall", "x", "y", "0", "0", "@vault"),
                    createInstruction(EFFECT, "placeBlock", "x", "y", "size"),
                    createInstruction(EFFECT, "placeBlockSpark", "x", "y", "size"),
                    createInstruction(EFFECT, "breakBlock", "x", "y", "size"),
                    createInstruction(EFFECT, "spawn", "x", "y"),
                    createInstruction(EFFECT, "trail", "x", "y", "size", "color"),
                    createInstruction(EFFECT, "breakProp", "x", "y", "size", "color"),
                    createInstruction(EFFECT, "smokeCloud", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "vapor", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "hit", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "hitSquare", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "shootSmall", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "shootBig", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "smokeSmall", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "smokeBig", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "smokeColor", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "smokeSquare", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "smokeSquareBig", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "spark", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "sparkBig", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "sparkShoot", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "sparkShootBig", "x", "y", "rotation", "color"),
                    createInstruction(EFFECT, "drill", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "drillBig", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "lightBlock", "x", "y", "size", "color"),
                    createInstruction(EFFECT, "explosion", "x", "y", "size"),
                    createInstruction(EFFECT, "smokePuff", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "sparkExplosion", "x", "y", "0", "color"),
                    createInstruction(EFFECT, "crossExplosion", "x", "y", "size", "color"),
                    createInstruction(EFFECT, "wave", "x", "y", "size", "color"),
                    createInstruction(EFFECT, "bubble", "x", "y"),
                    createInstruction(END)
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
                    createInstruction(SET, "__fn0_n", "10"),
                    createInstruction(SET, "__fn0_m", "20"),
                    createInstruction(SET, var(0), "__fn0_m"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "b", "__fn0_m"),
                    createInstruction(SET, "__fn1_n", "10"),
                    createInstruction(SET, "__fn1_m", "20"),
                    createInstruction(SET, var(1), "__fn1_m"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "a", "__fn1_n"),
                    createInstruction(SET, "__fn2_n", "10"),
                    createInstruction(SET, "__fn2_m", "20"),
                    createInstruction(SET, var(2), "__fn2_m"),
                    createInstruction(LABEL, var(1002))
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
                    createInstruction(SETADDR, "__fn0retaddr", var(1003)),
                    createInstruction(CALL, var(1000), "__fn0retval"),
                    createInstruction(GOTOLABEL, var(1003), "__fn0"),
                    createInstruction(SET, var(2), "__fn0retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn0_n", "10"),
                    createInstruction(SET, "__fn0_m", "20"),
                    createInstruction(SET, "__fn0retval", "__fn0_m"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
                            foo();
                            """,
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(SET, "__sp", "0"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "b", "__fn0_m"),
                    createInstruction(SET, var(0), "__fn0retval"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1002), "__fn0retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, "a", "__fn0_n"),
                    createInstruction(SET, var(1), "__fn0retval"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(SET, var(2), "__fn0retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn0_n", "10"),
                    createInstruction(SET, "__fn0_m", "20"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1005), "__fn0retval"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, "__fn0_n", "__fn0_n"),
                    createInstruction(SET, "__fn0_m", "__fn0_m"),
                    createInstruction(SET, var(3), "__fn0retval"),
                    createInstruction(SET, "__fn0retval", var(3)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(RETURN, "bank1")
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
                    createInstruction(SET, "__fn0_n", "a"),
                    createInstruction(SET, "__fn0_m", "b"),
                    createInstruction(PRINT, "__fn0_m"),
                    createInstruction(PRINT, "__fn0_n"),
                    createInstruction(OP, "mul", "__fn0_n", "__fn0_n", "2"),
                    createInstruction(OP, "mul", "__fn0_m", "__fn0_m", "2"),
                    createInstruction(SET, var(0), "__fn0_m"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "a", "__fn0_n"),
                    createInstruction(SET, "__fn1_n", "a"),
                    createInstruction(SET, "__fn1_m", "b"),
                    createInstruction(PRINT, "__fn1_m"),
                    createInstruction(PRINT, "__fn1_n"),
                    createInstruction(OP, "mul", "__fn1_n", "__fn1_n", "2"),
                    createInstruction(OP, "mul", "__fn1_m", "__fn1_m", "2"),
                    createInstruction(SET, var(1), "__fn1_m"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "b", "__fn1_m")
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
                    createInstruction(OP, "mul", "__fn0_n", "__fn0_n", "2"),
                    createInstruction(OP, "mul", "__fn0_m", "__fn0_m", "2"),
                    createInstruction(SET, "__fn0retval", "__fn0_m"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
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
                    createInstruction(OP, "mul", "__fn0_n", "__fn0_n", "2"),
                    createInstruction(OP, "mul", "__fn0_m", "__fn0_m", "2"),
                    createInstruction(SET, "__fn0_n", "__fn0_n"),
                    createInstruction(SET, "__fn0_m", "__fn0_m"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1004), "__fn0retval"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, "__fn0_n", "__fn0_n"),
                    createInstruction(SET, "__fn0_m", "__fn0_m"),
                    createInstruction(SET, var(2), "__fn0retval"),
                    createInstruction(SET, "__fn0retval", var(2)),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(RETURN, "bank1")
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(PRINT, "1"),
                    createInstruction(LABEL, var(1001))
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
                    createInstruction(LABEL, var(1002)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(END)
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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "__fn1_a", "1"),
                    createInstruction(PRINT, q("one")),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "__fn2_a", "1"),
                    createInstruction(SET, "__fn2_b", "2"),
                    createInstruction(PRINT, q("two")),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, q("vararg")),
                    createInstruction(PRINT, "3"),
                    createInstruction(LABEL, var(1003))
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
                    createInstruction(SET, ":fn0:a", "1"),
                    createInstruction(SET, ":fn0:b", "2"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(GOTOLABEL, var(1002), ":fn0"),
                    createInstruction(SET, ":result", ":fn0:c"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, ":fn0:a", "1"),
                    createInstruction(SET, ":fn0:b", "2"),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1004)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(GOTOLABEL, var(1004), ":fn0"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(OP, "add", var(0), ":fn0:a", ":fn0:b"),
                    createInstruction(SET, ":fn0:c", var(0)),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(GOTO, ":fn0*retaddr", ":fn0")
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
                    createInstruction(GOTO, "__fn0retaddr", "__fn0")
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
                    createInstruction(LABEL, var(1111)),
                    createInstruction(JUMP, var(1111), "equal", "bank1", "null"),
                    createInstruction(SET, "__sp", "0"),
                    createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(PRINT, q("foo")),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(RETURN, "bank1")
            );
        }
    }
}