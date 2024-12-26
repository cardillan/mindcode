package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class FunctionCallsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class InputArgumentFunctions {
        @Test
        void compilesUserFunctionCall() {
            assertCompiles("""
                            void foo(x)
                                print(x);
                            end;
                            
                            foo("Hello");
                            """,
                    createInstruction(SET, "__fn1_x", q("Hello")),
                    createInstruction(PRINT, "__fn1_x"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesMultipleInlineFunctionCalls() {
            assertCompiles("""
                            inline void foo(x)
                                print(x);
                            end;
                            
                            foo("Hello, ");
                            foo("Dolly");
                            """,
                    createInstruction(SET, "__fn1_x", q("Hello, ")),
                    createInstruction(PRINT, "__fn1_x"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "__fn2_x", q("Dolly")),
                    createInstruction(PRINT, "__fn2_x"),
                    createInstruction(LABEL, var(1002))
            );
        }

        @Test
        void compilesNoinlineUserFunctionCall() {
            assertCompiles("""
                            noinline void foo(x)
                                print(x);
                            end;
                            
                            foo("Hello");
                            """,
                    createInstruction(SET, "__fn1_x", q("Hello")),
                    createInstruction(SETADDR, "__fn1retaddr", var(1002)),
                    createInstruction(CALL, var(1001), "__fn1retval"),
                    createInstruction(GOTOLABEL, var(1002), "__fn1"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, "__fn1_x"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(GOTO, "__fn1retaddr", "__fn1")
            );
        }
    }

    @Nested
    class OutputArgumentFunctions {
        @Test
        void compilesUserFunctionCall() {
            assertCompiles("""
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out z);
                            """,
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "z", "__fn1_x")
            );
        }

        @Test
        void compilesMultipleInlineFunctionCalls() {
            assertCompiles("""
                            inline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out y);
                            foo(out z);
                            """,
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "y", "__fn1_x"),
                    createInstruction(SET, "__fn2_x", "10"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, "z", "__fn2_x")
            );
        }

        @Test
        void compilesUserFunctionCallWithExternalVariables() {
            assertCompiles("""
                            allocate heap in bank1;
                            
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(WRITE, "__fn1_x", "bank1", "0")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalVariables() {
            assertCompiles("""
                            allocate heap in bank1;
                            
                            noinline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(SETADDR, "__fn1retaddr", var(1002)),
                    createInstruction(CALL, var(1001), "__fn1retval"),
                    createInstruction(GOTOLABEL, var(1002), "__fn1"),
                    createInstruction(WRITE, "__fn1_x", "bank1", "0"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(GOTO, "__fn1retaddr", "__fn1")
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
    class InputOutputArgumentFunctions {
        @Test
        void compilesUserFunctionCall() {
            assertCompiles("""
                            void foo(in out x)
                                x += 10;
                            end;
                            
                            z = 10;
                            foo(out z);
                            """,
                    createInstruction(SET, "z", "10"),
                    createInstruction(SET, "__fn1_x", "z"),
                    createInstruction(OP, "add", "__fn1_x", "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "z", "__fn1_x")
            );
        }

        @Test
        void compilesMultipleInlineFunctionCalls() {
            assertCompiles("""
                            inline void foo(in out x)
                                x += 10;
                            end;
                            
                            y = z = 10;
                            foo(out y);
                            foo(out z);
                            """,
                    createInstruction(SET, "z", "10"),
                    createInstruction(SET, "y", "z"),
                    createInstruction(SET, "__fn1_x", "y"),
                    createInstruction(OP, "add", "__fn1_x", "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "y", "__fn1_x"),
                    createInstruction(SET, "__fn2_x", "z"),
                    createInstruction(OP, "add", "__fn2_x", "__fn2_x", "10"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, "z", "__fn2_x")

            );
        }

        @Test
        void compilesUserFunctionCallWithExternalVariables() {
            assertCompiles("""
                            allocate heap in bank1;
                            
                            void foo(in out x)
                                x += 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, "__fn1_x", var(1)),
                    createInstruction(OP, "add", "__fn1_x", "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(WRITE, "__fn1_x", "bank1", "0")
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalVariables() {
            assertCompiles("""
                            allocate heap in bank1;
                            
                            noinline void foo(in out x)
                                x += 10;
                            end;
                            
                            foo(out $z);
                            """,
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, "__fn1_x", var(1)),
                    createInstruction(SETADDR, "__fn1retaddr", var(1002)),
                    createInstruction(CALL, var(1001), "__fn1retval"),
                    createInstruction(GOTOLABEL, var(1002), "__fn1"),
                    createInstruction(WRITE, "__fn1_x", "bank1", "0"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", "__fn1_x", "__fn1_x", "10"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(GOTO, "__fn1retaddr", "__fn1")
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
    class MindustryFunctions {
        @Test
        void compilesFunctionCall() {
            assertCompiles("""
                            unit = ubind(@poly);
                            """,
                    createInstruction(UBIND, "@poly"),
                    createInstruction(SET, var(0), "@unit"),
                    createInstruction(SET, "unit", var(0))
            );
        }

        @Test
        void compilesOutputVariables() {
            assertCompiles("""
                            building = getBlock(20, 30, out type, out floor);
                            """,
                    createInstruction(UCONTROL, "getBlock", "20", "30", "type", var(0), "floor"),
                    createInstruction(SET, "building", var(0))
            );
        }

        @Test
        void compilesOutputExternalVariables() {
            assertCompiles("""
                            allocate heap in bank1;
                            $building = getBlock(20, 30, out $type, out $floor);
                            """,
                    createInstruction(UCONTROL, "getBlock", "20", "30", var(2), var(5), var(4)),
                    createInstruction(WRITE, var(2), "bank1", "1"),
                    createInstruction(WRITE, var(4), "bank1", "2"),
                    createInstruction(WRITE, var(5), "bank1", "0")
            );
        }
    }

}