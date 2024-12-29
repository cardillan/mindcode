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
        void compilesUserFunctionCallWithExternalMemory() {
            assertCompiles("""
                            void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out cell2[cell1[0]]);
                            """,
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(WRITE, "__fn1_x", "cell2", var(1))
            );
        }

        @Test
        void compilesNoinlineUserFunctionCallWithExternalMemory() {
            assertCompiles("""
                            noinline void foo(out x)
                                x = 10;
                            end;
                            
                            foo(out cell1[cell1[0]]);
                            """,
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(SETADDR, "__fn1retaddr", var(1002)),
                    createInstruction(CALL, var(1001), "__fn1retval"),
                    createInstruction(GOTOLABEL, var(1002), "__fn1"),
                    createInstruction(WRITE, "__fn1_x", "cell1", var(1)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(SET, "__fn1_x", "10"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(GOTO, "__fn1retaddr", "__fn1")
            );
        }


        @Test
        void compilesNoinlineUserFunctionCallWithExternalMemoryAndGlobalIndex() {
            assertCompiles("""
                            noinline void foo(out x)
                                x = ++INDEX;
                            end;
                            
                            INDEX = 0;
                            foo(out cell1[INDEX]);
                            """,
                    createInstruction(SET, "INDEX", "0"),
                    createInstruction(SET, var(0), "INDEX"),
                    createInstruction(SETADDR, "__fn1retaddr", var(1002)),
                    createInstruction(CALL, var(1001), "__fn1retval"),
                    createInstruction(GOTOLABEL, var(1002), "__fn1"),
                    createInstruction(WRITE, "__fn1_x", "cell1", var(0)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", "INDEX", "INDEX", "1"),
                    createInstruction(SET, "__fn1_x", "INDEX"),
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
    class ReturnStatements {
        @Test
        void compilesReturnFromInlineFunction() {
            assertCompiles("""
                            def foo(x)
                                if x == 0 then return 10; end;
                                x - 20;
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(SET, "__fn1_x", var(0)),
                    createInstruction(OP, "equal", var(2), "__fn1_x", "0"),
                    createInstruction(JUMP, var(1002), "equal", var(2), "false"),
                    createInstruction(SET, var(1), "10"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(OP, "sub", var(4), "__fn1_x", "20"),
                    createInstruction(SET, var(1), var(4)),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(PRINT, var(1))

            );
        }

        @Test
        void compilesReturnFromNoinlineFunction() {
            assertCompiles("""
                            noinline def foo(x)
                                if x == 0 then return 10; end;
                                x - 20;
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(SET, "__fn1_x", var(0)),
                    createInstruction(SETADDR, "__fn1retaddr", var(1002)),
                    createInstruction(CALL, var(1001), "__fn1retval"),
                    createInstruction(GOTOLABEL, var(1002), "__fn1"),
                    createInstruction(PRINT, "__fn1retval"),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "equal", var(1), "__fn1_x", "0"),
                    createInstruction(JUMP, var(1005), "equal", var(1), "false"),
                    createInstruction(SET, "__fn1retval", "10"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(JUMP, var(1006), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "sub", var(3), "__fn1_x", "20"),
                    createInstruction(SET, "__fn1retval", var(3)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(GOTO, "__fn1retaddr", "__fn1")
            );
        }

        @Test
        void compilesReturnFromRecursiveFunction() {
            assertCompiles("""
                            allocate stack in bank1;
                            
                            def foo(x)
                                if x <= 0 then return 10; end;
                                foo(x - 20);
                            end;
                            
                            print(foo(rand(10)));
                            """,
                    createInstruction(SET, "__sp", "0"),
                    createInstruction(OP, "rand", var(0), "10"),
                    createInstruction(SET, "__fn1_x", var(0)),
                    createInstruction(CALLREC, "bank1", var(1001), var(1002), "__fn1retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(1), "__fn1retval"),
                    createInstruction(PRINT, var(1)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThanEq", var(2), "__fn1_x", "0"),
                    createInstruction(JUMP, var(1005), "equal", var(2), "false"),
                    createInstruction(SET, "__fn1retval", "10"),
                    createInstruction(JUMP, var(1004), "always"),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(JUMP, var(1006), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(SET, var(3), "null"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "sub", var(4), "__fn1_x", "20"),
                    createInstruction(PUSH, "bank1", var(4)),
                    createInstruction(PUSH, "bank1", var(3)),
                    createInstruction(PUSH, "bank1", "__fn1_x"),
                    createInstruction(SET, "__fn1_x", var(4)),
                    createInstruction(CALLREC, "bank1", var(1001), var(1007), "__fn1retval"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(POP, "bank1", "__fn1_x"),
                    createInstruction(POP, "bank1", var(3)),
                    createInstruction(POP, "bank1", var(4)),
                    createInstruction(SET, var(5), "__fn1retval"),
                    createInstruction(SET, "__fn1retval", var(5)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(RETURN, "bank1")
            );
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

        @Test
        void reportsWrongKeywords() {
            assertGeneratesMessages(expectedMessages()
                            .add("Invalid value 'building' for parameter 'layer': allowed values are 'floor', 'ore', 'block'."),
                    """
                            setblock(building, @core-nucleus, x, y, 0, 0);
                            """);
        }
    }

    @Nested
    class MindustryMethods {
        @Test
        void compilesSimpleMethodCall() {
            assertCompiles("""
                            message1.printflush();
                            """,
                    createInstruction(PRINTFLUSH, "message1")
            );
        }

        @Test
        void compilesChainedMethodCall() {
            assertCompiles("""
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
}