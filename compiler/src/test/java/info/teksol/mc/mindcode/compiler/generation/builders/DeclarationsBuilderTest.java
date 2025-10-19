package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class DeclarationsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class BuiltinDeclarations {
        @Test
        void compilesBuiltinDeclaration() {
            assertCompiles("#declare builtin @fluffy-bunny;");
        }

        @Test
        void compilesCustomBuiltin() {
            assertCompiles("""
                    #declare builtin @fluffy-bunny;
                    print(@fluffy-bunny);
                    """);
        }

        @Test
        void compilesCustomKeywordInFunctionCall() {
            assertCompiles("""
                    #declare blockGroup :logic;
                    ulocate(:building, :logic, true);
                    """);
        }


        @Test
        void compilesCustomLinkedBLockName() {
            assertCompiles("""
                    #declare linkedBlock station;
                    linked station1;
                    """);
        }

        @Test
        void reportsUnknownBuiltins() {
            assertGeneratesMessage(
                    "Built-in variable '@fluffy-bunny' not recognized.",
                    "print(@fluffy-bunny);");
        }

        @Test
        void reportsUnknownKeywords() {
            assertGeneratesMessageRegex(
                    "Invalid value ':what' for keyword parameter: allowed values are.*",
                    "ulocate(:what, :core, true);");
        }

        @Test
        void reportsUnknownBlockNames() {
            assertGeneratesMessage(
                    "Linked variable name 'station1' doesn't correspond to any known linked block name.",
                    "linked station1;");
        }

        @Test
        void reportsWrongBuiltins() {
            assertGeneratesMessage(
                    "A custom built-in identifier is expected.",
                    "#declare builtin fluffyBunny;");
        }

        @Test
        void reportsWrongKeywords() {
            assertGeneratesMessage(
                    "A custom keyword is expected.",
                    "#declare alignment @fluffy-bunny;");
        }

        @Test
        void reportsWrongIdentifier() {
            assertGeneratesMessage(
                    "A custom block name is expected.",
                    "#declare linkedBlock @fluffy-bunny;");
        }

        @Test
        void reportsWrongCategory() {
            assertGeneratesMessage(
                    "Unknown keyword category 'fluffyBunny'.",
                    "#declare fluffyBunny @humpty-dumpty;");
        }
    }

    @Nested
    class ConstantDeclarations {
        @Test
        void compilesBinaryLiteralConstants() {
            assertCompilesTo("const c = 0b101; a = c;", createInstruction(SET, "a", "0b101"));
        }

        @Test
        void compilesBooleanLiteralConstants() {
            assertCompilesTo("const c = true; a = c;", createInstruction(SET, "a", "true"));
            assertCompilesTo("const c = false; a = c;", createInstruction(SET, "a", "false"));
        }

        @Test
        void compilesDecimalLiteralConstants() {
            assertCompilesTo("const c = 10; a = c;", createInstruction(SET, "a", "10"));
        }

        @Test
        void compilesFloatLiteralConstants() {
            assertCompilesTo("const c = 1e70; a = c;", createInstruction(SET, "a", "1E70"));
        }

        @Test
        void compilesNullLiteralConstants() {
            assertCompilesTo("const c = null; a = c;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesBuiltInConstants() {
            assertCompilesTo("const c = @coal; a = c;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesBlockConstants() {
            assertCompilesTo("const c = cell1; a = c;", createInstruction(SET, "a", "cell1"));
        }

        @Test
        void compilesExpressionConstantsFromLiterals() {
            assertCompilesTo("const c = 10 + 20; a = c;", createInstruction(SET, "a", "30"));
        }

        @Test
        void compilesExpressionConstantsFromConstants() {
            assertCompilesTo("const b = 10; const c = b + 20; a = c;", createInstruction(SET, "a", "30"));
        }

        @Test
        void compilesExpressionConstantsWithParentheses() {
            assertCompilesTo("""
                            const A = 10;
                            const B = 20;
                            const C = A * (B + 1);
                            print(C);
                            """,
                    createInstruction(PRINT, "210")
            );
        }

        @Test
        void compilesExpressionConstantsWithTernaryOps() {
            assertCompilesTo("""
                            const SLANTED = false;
                            const HEIGHT    = 12;
                            const HALF      = HEIGHT / 2 + (SLANTED ? 1 : 0);
                            print(HALF);
                            """,
                    createInstruction(PRINT, "6")
            );
        }

        @Test
        void compilesConstantsUsedInFunctions() {
            assertCompilesTo("""
                            const A = 10;
                            
                            foo();
                            void foo()
                                print(A);
                            end;
                            """,
                    createInstruction(PRINT, "10"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesFunctionWithParameterIdenticalToConstant() {
            assertCompilesTo("""
                            const a = 10;
                            
                            void foo(a)
                                print(a);
                            end;
                            foo(5);
                            """,
                    createInstruction(SET, "__fn0_a", "5"),
                    createInstruction(PRINT, "__fn0_a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void refusesNonConstantExpressions() {
            assertGeneratesMessage(
                    "Value assigned to constant 'a' is not a constant expression.",
                    "const a = b;");
        }

        @Test
        void refusesVolatileExpressions() {
            assertGeneratesMessage(
                    "Value assigned to constant 'a' is not a constant expression.",
                    "const a = @time;");
        }

        @Test
        void refusesMlog7UnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e70' does not have a valid mlog representation.",
                    "#set target = 7; const a = 1e70;");
        }

        @Test
        void refusesUnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e400' does not have a valid mlog representation.",
                    "const a = 1e400;");
        }

        @Test
        void refusesMultipleConstDeclarations() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "const a = 10; const a = 10;");
        }

        @Test
        void refusesMultipleParamDeclarations() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "const a = 10; param a = 10;");
        }

        @Test
        void refusesDeclarationOverLocalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "a = 10; const a = 10;");
        }

        @Test
        void refusesDeclarationOverGlobalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'A'.",
                    "A = 10; const A = 10;");
        }
    }

    @Nested
    class ExternalArrayDeclarations {
        @Test
        void compilesUninitializedArrayDeclarations() {
            assertCompiles("allocate heap in cell1; external a[10];");
        }

        @Test
        void compilesInitializedArrayDeclarations() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[3] = (1, 2, 3);
                            """,
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2")
            );
        }

        @Test
        void compilesInitializedArrayDeclarationsNoSize() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[] = (1, 2, 3);
                            """,
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2")
            );
        }

        @Test
        void compilesInitializedArrayDeclarationsNoSizeExternalStorage() {
            assertCompilesTo("""
                            external(cell1) a[] = (1, 2, 3);
                            """,
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2")
            );
        }

        @Test
        void reportsUnknownArraySize() {
            assertGeneratesMessage(
                    "Array size not specified.",
                    "allocate heap in cell1; external a[];");
        }

        @Test
        void reportsNonConstantArraySize() {
            assertGeneratesMessage(
                    "Array size must be constant.",
                    "allocate heap in cell1; external a[n];");
        }

        @Test
        void reportsNonIntegerArraySize() {
            assertGeneratesMessage(
                    "Array size must be an integer.",
                    "allocate heap in cell1; external a[0.75];");
        }

        @Test
        void reportsNegativeArraySize() {
            assertGeneratesMessage(
                    "Array size out of range (1 .. 2048).",
                    "allocate heap in cell1; external a[-10];");
        }

        @Test
        void reportsArraySizeMismatch() {
            assertGeneratesMessage(
                    "Number of initial values provided doesn't match the declared array size.",
                    "allocate heap in cell1[0 ... 10]; external a[10] = (1, 2, 3);");
        }

        @Test
        void reportsInsufficientHeapCapacity() {
            assertGeneratesMessage(
                    "Not enough capacity in external storage for array 'a' (required 100, available 64).",
                    "allocate heap in cell1; external a[100];");
        }

        @Test
        void reportsInsufficientHeapCapacity2() {
            assertGeneratesMessage(
                    "Not enough capacity in external storage for array 'b' (required 100, available 54).",
                    "allocate heap in cell1; external a[10], b[100];");
        }

        @Test
        void refusesLocalArray() {
            assertGeneratesMessages(expectedMessages()
                            .add("The 'external' modifier cannot be used to declare local variables.")
                            .add("Arrays must be declared in the global scope.")
                            .add("No heap allocated for external variables."),
                    """
                            begin
                                external a[10];
                            end;
                            """);
        }

        @Test
        void refusesCachedArray() {
            assertGeneratesMessage(
                    "Arrays cannot be declared 'cached'.",
                    "allocate heap in cell1; external cached a[10];");
        }
    }

    @Nested
    class HeapAllocationDeclarations {

        @Test
        void acceptsMemoryDeclarationConstant() {
            assertCompiles("""
                    const memory = cell1;
                    allocate heap in memory[0 ... 64];
                    """);
        }

        @Test
        void acceptsMemoryDeclarationParameter() {
            assertCompiles("""
                    param memory = bank1;
                    allocate heap in memory[0 ... 512];
                    """);
        }

        @Test
        void compilesExternalVariables() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            $A = $B;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "1"),
                    createInstruction(WRITE, tmp(1), "bank1", "0")
            );
        }

        @Test
        void refusesInvalidMemoryDeclarationConstant() {
            assertGeneratesMessages(
                    expectedMessages().add("Cannot use 'memory' as external storage."),
                    """
                            const memory = @coal;
                            allocate heap in memory;
                            """);
        }

        @Test
        void refusesInvalidMemoryDeclarationParameter() {
            assertGeneratesMessages(
                    expectedMessages().add("Cannot use value assigned to parameter 'memory' as a memory for external storage."),
                    """
                            param memory = @coal;
                            allocate heap in memory;
                            """);
        }

        @Test
        void refusesMultipleDeclarations() {
            assertGeneratesMessages(
                    expectedMessages().add("Multiple heap allocation declarations."),
                    """
                            allocate heap in memory;
                            allocate heap in bank1;
                            """);
        }

        @Test
        void refusesNonConstantRanges() {
            assertGeneratesMessages(
                    expectedMessages().add("External storage declaration must specify constant range."),
                    "allocate heap in bank1[a .. 10];");
            assertGeneratesMessages(
                    expectedMessages().add("External storage declaration must specify constant range."),
                    "allocate heap in bank1[0 .. b];");
        }

        @Test
        void refusesNonIntegerRanges() {
            assertGeneratesMessages(
                    expectedMessages().add("External storage declaration must specify integer range."),
                    "allocate heap in bank1[10.5 .. 20];");
            assertGeneratesMessages(
                    expectedMessages().add("External storage declaration must specify integer range."),
                    "allocate heap in bank1[0 .. 15.8];");
        }

        // World cells have capacities of 2048. Need to make these into warnings.
        //@Test
        void refusesOutOfRangeRanges() {
            assertCompiles("allocate heap in bank1[0 ... 512];");
            assertCompiles("allocate heap in bank1[0 .. 511];");
            assertGeneratesMessages(
                    expectedMessages().add("external storage memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[0 ... 513];");
            assertGeneratesMessages(
                    expectedMessages().add("external storage memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[0 .. 512];");
            assertGeneratesMessages(
                    expectedMessages().add("external storage memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[-1 .. 0];");
            assertGeneratesMessages(
                    expectedMessages().add("external storage memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[512 ... 512];");
        }

        @Test
        void refusesEmptyRanges() {
            assertCompiles("allocate heap in bank1[0 .. 0];");
            assertCompiles("allocate heap in bank1[1 ... 2];");
            assertGeneratesMessages(
                    expectedMessages().add("Empty or invalid external storage memory range."),
                    "allocate heap in bank1[0 ... 0];");
            assertGeneratesMessages(
                    expectedMessages().add("Empty or invalid external storage memory range."),
                    "allocate heap in bank1[1 .. 0];");
        }

        @Test
        void refusesTooManyVariables() {
            // Make sure each excess variable is reported only once
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Not enough capacity in external storage for '$B'.")
                            .add("Not enough capacity in external storage for '$C'."),
                    """
                            allocate heap in cell1[0 ... 1];
                            $A = $B;
                            $B = $C;
                            """);
        }

        @Test
        void refusesMissingHeapDeclaration() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("No heap allocated for external variables.").repeat(2),
                    "$A = $B; $B = 10;");

            assertGeneratesMessages(
                    expectedMessages()
                            .add("No heap allocated for external variables."),
                    """
                            $A = 10;
                            allocate heap in cell1[0 ... 1];
                            $B = $A;
                            """);
        }

        @Test
        void refusesDeclarationInsideCodeBlock() {
            assertGeneratesMessage(
                    "Statement or declaration not allowed within a main code block or function.",
                    "begin allocate heap in cell1; end;");
        }
    }

    @Nested
    class InternalArrayDeclarations {
        @Test
        void compilesUninitializedArrayDeclarations() {
            assertCompiles("var a[10];");
        }

        @Test
        void compilesInitializedArrayDeclarations() {
            assertCompilesTo(
                    "var a[3] = (1, 2, 3);",
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3")
            );
        }

        @Test
        void compilesInitializedArrayDeclarationsNoSize() {
            assertCompilesTo(
                    "var a[] = (1, 2, 3);",
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3")
            );
        }

        @Test
        void reportsUnknownArraySize() {
            assertGeneratesMessage(
                    "Array size not specified.",
                    "var a[];");
        }

        @Test
        void reportsNonConstantArraySize() {
            assertGeneratesMessage(
                    "Array size must be constant.",
                    "var a[n];");
        }

        @Test
        void reportsNonIntegerArraySize() {
            assertGeneratesMessage(
                    "Array size must be an integer.",
                    "var a[0.75];");
        }

        @Test
        void reportsNegativeArraySize() {
            assertGeneratesMessage(
                    "Array size out of range (1 .. 1000).",
                    "var a[-10];");
        }

        @Test
        void reportsTooLargeArraySize() {
            assertGeneratesMessage(
                    "Array size out of range (1 .. 1000).",
                    "var a[1500];");
        }

        @Test
        void reportsArraySizeMismatch() {
            assertGeneratesMessage(
                    "Number of initial values provided doesn't match the declared array size.",
                    "var a[10] = (1, 2, 3);");
        }

        @Test
        void refusesLocalArray() {
            assertGeneratesMessage(
                    "Arrays must be declared in the global scope.",
                    "begin var a[10]; end;");
        }

        @Test
        void refusesLinkedArray() {
            assertGeneratesMessage(
                    "Arrays cannot be declared 'linked'.",
                    "linked a[10];");
        }

        @Test
        void refusesNoinitArray() {
            assertGeneratesMessage(
                    "Arrays cannot be declared 'noinit'.",
                    "noinit a[10];");
        }

        @Test
        void refuseNonConstantElementOfConstantArray() {
            assertGeneratesMessage(
                    "Value assigned to a constant array element is not a constant expression.",
                    "const a[] = (b);");
        }


        @Test
        void refuseUninitializedConstantArray() {
            assertGeneratesMessage(
                    "Constant array must be initialized.",
                    "const a[10];");
        }

        @Test
        void refusesCachedArray() {
            assertGeneratesMessages(expectedMessages()
                            .add("Arrays cannot be declared 'cached'.")
                            .add("Modifier 'cached' requires the 'external' or 'remote' modifier."),
                    "cached a[10];");
        }
    }

    @Nested
    class ModuleDeclarations {

        @Test
        void compilesModule() {
            assertCompiles("module a;");
        }

        @Test
        void reportsMultipleDeclarations() {
            assertGeneratesMessage(
                    "Multiple module declarations in one source file are not allowed.",
                    "module a; module b;");
        }
    }

    @Nested
    class KeywordDeclarations {
        @Test
        void compilesKeywordDeclaration() {
            assertCompiles("#declare radarTarget :marine;");
        }

        @Test
        void compilesCustomKeywordInFunctionCall() {
            assertCompiles("""
                    #declare blockGroup :fluffyBunny;
                    ulocate(:building, :fluffyBunny, true);
                    """);
        }

        @Test
        void reportsWrongKeywords() {
            assertGeneratesMessage(
                    "A custom keyword is expected.",
                    "#declare radarTarget @marine;");
        }

        @Test
        void reportsUnsupportedCategory() {
            assertGeneratesMessage(
                    "Keyword category 'alignment' is not available in current language target.",
                    "#set target = 7; #declare alignment :random;");
        }
    }

    @Nested
    class ParameterDeclarations {
        @Test
        void compilesLiteralParameters() {
            assertCompilesTo("param a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompilesTo("param a = true;", createInstruction(SET, "a", "true"));
            assertCompilesTo("param a = false;", createInstruction(SET, "a", "false"));
            assertCompilesTo("param a = 10;", createInstruction(SET, "a", "10"));
            assertCompilesTo("param a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesBuiltInParameters() {
            assertCompilesTo("param a = @coal;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesBlockParameters() {
            assertCompilesTo("param a = cell1;", createInstruction(SET, "a", "cell1"));
        }

        @Test
        void refusesNonConstantExpressions() {
            assertGeneratesMessage(
                    "Value assigned to parameter 'b' is not a constant expression.",
                    "param a = 10; param b = a;");
        }

        @Test
        void refusesVolatileExpressions() {
            assertGeneratesMessage(
                    "Value assigned to parameter 'a' is not a constant expression.",
                    "param a = @unit;");
        }

        @Test
        void refusesMlog7UnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e70' does not have a valid mlog representation.",
                    "#set target = 7; param a = 1e70;\n");
        }

        @Test
        void refusesUnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e400' does not have a valid mlog representation.",
                    "param a = 1e400;");
        }

        @Test
        void refusesMultipleParamDeclarations() {
            assertGeneratesMessage("Multiple declarations of 'a'.",
                    "param a = 10; param a = 10;");
        }

        @Test
        void refusesMultipleConstDeclarations() {
            assertGeneratesMessage("Multiple declarations of 'a'.",
                    "param a = 10; const a = 10;");
        }

        @Test
        void refusesDeclarationOverLocalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "a = 10; param a = 10;");
        }

        @Test
        void refusesDeclarationOverGlobalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'A'.",
                    "A = 10; param A = 10;");
        }

        @Test
        void refusesBlockNameAsParameterName() {
            assertGeneratesMessage(
                    "Name 'switch1' is reserved for linked blocks.",
                    "param switch1 = 10;");
        }

        @Test
        void refusesFormattablesAsParameters() {
            assertGeneratesMessage(
                    "Value assigned to parameter 'a' is not a constant expression.",
                    """
                            param a = $"Hello";
                            """);
        }
    }

    @Nested
    class VariableDeclarations {
        @Test
        void compilesVariableDeclarationWithLiteralInitializations() {
            assertCompilesTo("var a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompilesTo("var a = true;", createInstruction(SET, "a", "true"));
            assertCompilesTo("var a = false;", createInstruction(SET, "a", "false"));
            assertCompilesTo("var a = 10;", createInstruction(SET, "a", "10"));
            assertCompilesTo("var a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesVariableDeclarationWithBuiltInInitializations() {
            assertCompilesTo("var a = @coal;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesHeapVariableDeclarations() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a;
                            external b = a;
                            print(b);
                            """,
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(WRITE, tmp(0), "cell1", "1"),
                    createInstruction(READ, tmp(2), "cell1", "1"),
                    createInstruction(PRINT, tmp(2))
            );
        }


        @Test
        void compilesExternalVariableDeclarations() {
            assertCompilesTo("""
                            external(cell1[0]) a;
                            external(cell1[1]) b = a;
                            print(b);
                            """,
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(WRITE, tmp(0), "cell1", "1"),
                    createInstruction(READ, tmp(2), "cell1", "1"),
                    createInstruction(PRINT, tmp(2))
            );
        }

        @Test
        void compilesGuardedVariables() {
            assertCompilesTo("""
                            guarded switch1, message1;
                            printflush(message1);
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "switch1", "null"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "message1", "null"),
                    createInstruction(PRINTFLUSH, "message1")
            );
        }

        @Test
        void compilesLocalVariableDeclarations() {
            assertCompilesTo("""
                            inline void foo()
                                var a = 10;
                                print(a);
                            end;
                            
                            foo();
                            foo();
                            """,
                    createInstruction(SET, ":fn0:a", "10"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":fn1:a", "10"),
                    createInstruction(PRINT, ":fn1:a"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesLocalVariableDeclarationsOverGlobalVariable() {
            assertCompilesTo("""
                            var a = 1;
                            
                            inline void foo()
                                var a = 10;
                                print(a);
                            end;
                            
                            print(a);
                            foo();
                            """,
                    createInstruction(SET, ".a", "1"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(SET, ":fn0:a", "10"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void compilesMlogVariables() {
            assertCompilesTo("""
                            mlog("abc") var x = 10;
                            mlog("def") y = 2;
                            noinit mlog("ghi") var z;
                            volatile mlog("jkl") var w;
                            mlog("mno" + "pqr") var v = 123;
                            print(z, w);
                            """,
                    createInstruction(SET, "abc", "10"),
                    createInstruction(SET, "def", "2"),
                    createInstruction(SET, "mnopqr", "123"),
                    createInstruction(PRINT, "ghi"),
                    createInstruction(SET, tmp(0), "jkl"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void compilesRemoteVariables() {
            assertCompilesTo("""
                            remote(processor1) a, x;
                            remote(processor1) mlog("b") b;
                            remote(processor1) var c, y;
                            remote(processor1) mlog("d") var d;
                            print(a, b, c, d);
                            """,
                    createInstruction(READ, tmp(0), "processor1", q(".a")),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "processor1", q("b")),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(2), "processor1", q(".c")),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(READ, tmp(4), "processor1", q("d")),
                    createInstruction(PRINT, tmp(4))
            );
        }

        @Test
        void compilesCachedRemoteVariables() {
            assertCompilesTo("""
                            cached remote(processor1) a, b = 1;
                            a++;
                            b++;
                            """,
                    createInstruction(READ, ".a", "processor1", q(".a")),
                    createInstruction(SET, ".b", "1"),
                    createInstruction(WRITE, ".b", "processor1", q(".b")),
                    createInstruction(SET, tmp(0), ".a"),
                    createInstruction(OP, "add", ".a", ".a", "1"),
                    createInstruction(WRITE, ".a", "processor1", q(".a")),
                    createInstruction(SET, tmp(1), ".b"),
                    createInstruction(OP, "add", ".b", ".b", "1"),
                    createInstruction(WRITE, ".b", "processor1", q(".b"))
            );
        }

        @Test
        void compilesCachedNoinitRemoteVariables() {
            assertCompilesTo("""
                            cached noinit remote(processor1) a;
                            a = 2;
                            print(a++);
                            """,
                    createInstruction(SET, ".a", "2"),
                    createInstruction(WRITE, ".a", "processor1", q(".a")),
                    createInstruction(SET, tmp(0), ".a"),
                    createInstruction(OP, "add", ".a", ".a", "1"),
                    createInstruction(WRITE, ".a", "processor1", q(".a")),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void compilesMainVariableDeclarationsOverGlobalVariable() {
            assertCompilesTo("""
                            var a = 1;
                            
                            inline void foo()
                                print(a);
                            end;
                            
                            begin
                                var a = 10;
                                print(a);
                                foo();
                            end;
                            """,
                    createInstruction(SET, ".a", "1"),
                    createInstruction(SET, ":a", "10"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void compilesGlobalDeclarationOverLocalVariable() {
            assertCompiles("a = 10; var a = 10;");
        }

        @Test
        void compilesUninitializedVariable() {
            assertCompiles("noinit var a;");
        }

        @Test
        void refusesMultipleVariableDeclarations() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "var a = 10; var a = 10;");
        }

        @Test
        void refusesMultipleParamDeclarations() {
            assertGeneratesMessages(expectedMessages()
                            .add("Multiple declarations of 'a'.")
                            .add("Assignment to constant or parameter 'a' not allowed."),
                    "param a = 10; var a = 10;");
        }

        @Test
        void refusesMultipleConstDeclarations() {
            assertGeneratesMessages(expectedMessages()
                            .add("Multiple declarations of 'a'.")
                            .add("Assignment to constant or parameter 'a' not allowed."),
                    "const a = 10; var a = 10;");
        }

        @Test
        void refusesDeclarationOverLinkedVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'message1'.",
                    """
                            printflush(message1);
                            linked message1;
                            """);
        }

        @Test
        void refusesDeclarationOverGlobalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'A'.",
                    "A = 10; var A = 10;");
        }

        @Test
        void refusesFormattablesAsParameters() {
            assertGeneratesMessage(
                    "A formattable string literal can only be used as a first argument to the print(), println() or remark() functions.",
                    """
                            var a = $"Hello";
                            """);
        }

        @Test
        void reportsNonstandardBlockName() {
            assertGeneratesMessage(
                    "Linked variable name 'foobar' doesn't correspond to any known linked block name.",
                    "linked foobar;");
        }

        @Test
        void reportsWrongUninitializedModifier() {
            assertGeneratesMessage(
                    "Variable declared as 'noinit' cannot be initialized.",
                    "noinit var a = 10;");
        }

        @Test
        void refusesTooSmallExternalSpace() {
            assertGeneratesMessage(
                    "Not enough capacity in external storage for 'c'.",
                    "external(cell1[0 .. 1]) a, b, c;");
        }

        @Test
        void refusesConflictingModifiers() {
            assertGeneratesMessages(expectedMessages()
                            .add("Modifiers 'external' and 'export' are mutually exclusive.").repeat(2),
                    "export external(cell1) var a = 10;");
        }

        @Test
        void refusesMultipleMlogVariableSpecifications() {
            assertGeneratesMessage(
                    "Only one variable may be specified within an `mlog` declaration.",
                    """
                            mlog("a") var a, b;
                            """);
        }
    }

    @Nested
    class VariableDeclarationsInStrictSyntax {

        @Test
        void compilesExternalVariableDeclarations() {
            assertCompilesTo("""
                            #set syntax = strict;
                            linked cell1;
                            external(cell1[0]) a;
                            external(cell1[1]) b = a;
                            begin print(b); end;
                            """,
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(WRITE, tmp(0), "cell1", "1"),
                    createInstruction(READ, tmp(2), "cell1", "1"),
                    createInstruction(PRINT, tmp(2))
            );
        }

        @Test
        void compilesGuardedVariables() {
            assertCompilesTo("""
                            #set syntax = strict;
                            guarded switch1, message1;
                            begin printflush(message1); end;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "switch1", "null"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "message1", "null"),
                    createInstruction(PRINTFLUSH, "message1")
            );
        }

        @Test
        void compilesLocalVariableDeclarations() {
            assertCompilesTo("""
                            #set syntax = strict;
                            inline void foo()
                                var a = 10;
                                print(a);
                            end;
                            
                            begin
                                foo();
                                foo();
                            end;
                            """,
                    createInstruction(SET, ":fn0:a", "10"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":fn1:a", "10"),
                    createInstruction(PRINT, ":fn1:a"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesLocalVariableDeclarationsOverGlobalVariable() {
            assertCompilesTo("""
                            #set syntax = strict;
                            var a = 1;
                            
                            inline void foo()
                                var a = 10;
                                print(a);
                            end;
                            
                            begin
                                print(a);
                                foo();
                            end;
                            """,
                    createInstruction(SET, ".a", "1"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(SET, ":fn0:a", "10"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void compilesMlogVariables() {
            assertCompilesTo("""
                            #set syntax = strict;
                            mlog("abc") var x = 10;
                            mlog("def") y = 2;
                            noinit mlog("ghi") var z;
                            volatile mlog("jkl") var w;
                            begin print(z, w); end;
                            """,
                    createInstruction(SET, "abc", "10"),
                    createInstruction(SET, "def", "2"),
                    createInstruction(PRINT, "ghi"),
                    createInstruction(SET, tmp(0), "jkl"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void compilesRemoteVariables() {
            assertCompilesTo("""
                            #set syntax = strict;
                            linked processor1;
                            remote(processor1) a, x;
                            remote(processor1) mlog("b") b;
                            remote(processor1) var c, y;
                            remote(processor1) mlog("d" + "0") var d;
                            begin print(a, b, c, d); end;
                            """,
                    createInstruction(READ, tmp(0), "processor1", q(".a")),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "processor1", q("b")),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(2), "processor1", q(".c")),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(READ, tmp(4), "processor1", q("d0")),
                    createInstruction(PRINT, tmp(4))
            );
        }

        @Test
        void compilesMainVariableDeclarationsOverGlobalVariable() {
            assertCompilesTo("""
                            #set syntax = strict;
                            var a = 1;
                            
                            inline void foo()
                                print(a);
                            end;
                            
                            begin
                                var a = 10;
                                print(a);
                                foo();
                            end;
                            """,
                    createInstruction(SET, ".a", "1"),
                    createInstruction(SET, ":a", "10"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void compilesUninitializedVariable() {
            assertCompiles("#set syntax = strict; noinit var a;");
        }
    }
}
