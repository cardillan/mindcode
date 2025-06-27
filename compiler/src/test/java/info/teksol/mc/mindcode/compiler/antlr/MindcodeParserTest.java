package info.teksol.mc.mindcode.compiler.antlr;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@NullMarked
class MindcodeParserTest extends AbstractParserTest {

    @Nested
    class PerformanceAndErrors {
        @Test
        void parsesVeryComplexExpression() {
            long start = System.nanoTime();
            assertParses("""
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                        X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or
                    not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12;
                    """
            );
            long milliseconds = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Complex expression parse time: " + milliseconds + " ms");
            Assertions.assertTrue(milliseconds < 10000, "Parsing took " + milliseconds + " ms; more than 10000 ms.");
        }

        @Test
        void reportsMissingSemicolonsAtProperPlaces() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add(1, 28, "Parse error: missing semicolon after '8'")
                            .add(2, 28, "Parse error: missing semicolon after '6'")
                            .add(4, 33, "Parse error: missing semicolon after '3'")
                            .add(6, 31, "Parse error: missing semicolon after '5000'")
                            .add(8, 13, "Parse error: missing semicolon after 'dome1'")
                            .add(10, 68, "Parse error: missing semicolon after ')'")
                            .add(11, 25, "Parse error: missing semicolon after ')'")
                            .add(12, 17, "Parse error: missing semicolon after 'dome1'")
                            .add(13, 4, "Parse error: missing semicolon after 'end'")
                    ,
                    """
                            const RADIUS_WITHIN     = 8
                            const RADIUS_APPROACH   = 6
                            
                            const SUPPLY_INTERVAL   = 50 - 3        // Some comment
                            
                            const UNIT_CHECK_TIME   = 5000          // Some comment
                            
                            DOME = dome1
                            while DOME == null do
                                print("[gold]Waiting for an overdrive dome to be connected...")
                                printflush(message1)
                                DOME = dome1
                            end
                            """
            );
        }
    }

    @Nested
    class Arrays {
        @Test
        void parsesArrays() {
            assertParses("""
                    array[index];
                    array[array[index]];
                    """
            );
        }

        @Test
        void parsesRemoteArrays() {
            assertParses("""
                    processor1.array[index];
                    processor1.array[processor1.array[index]];
                    """
            );
        }

        @Test
        void parsesSubarrays() {
            assertParses("""
                    array[3 .. 8];
                    """
            );
        }

        @Test
        void parsesRemoteSubarrays() {
            assertParses("""
                    processor1.array[3 .. 8];
                    """
            );
        }
        @Test
        void refusesInvalidArrays() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 1, "Parse error: .*")
                            .addRegex(1, 7, "Parse error: .*")
                            .addRegex(2, 7, "Parse error: .*")
                            .addRegex(3, 5, "Parse error: .*")
                            .addRegex(3, 7, "Parse error: .*")
                            .addRegex(4, 9, "Parse error: .*")
                            .addRegex(5, 5, "Parse error: .*"),
                    """
                            [index];
                            array[];
                            a + [b];
                            a[index]b;
                            a[b][c];
                            """);
        }
    }

    @Nested
    class Assignments {
        @Test
        void parsesAssignments() {
            assertParses("""
                    a = b;
                    a = b = true;
                    a += b *= c **= d;
                    a = b ? c : d;
                    a /= b ? c : d;
                    @var = x;
                    getblock(n).enabled = true;
                    a.b = 10;
                    
                    // Grammar allows these unwanted constructs
                    // They will be detected and discarded by AST builder or code generator.
                    (a + b) = c;
                    a.@x = true;
                    a(1) = x;
                    null = x;
                    """);
        }
    }

    @Nested
    class BasicStructure {
        @Test
        void parsesEmptryProgram() {
            assertParses("");
        }

        @Test
        void parsesCodeBlocks() {
            assertParses("""
                    a;
                    begin
                        b;
                    end;
                    c;
                    """);
        }

        @Test
        void refusesUnbalancedBegin() {
            assertGeneratesMessageRegex(4, 1, "Parse error: .*",
                    """
                            begin
                                id;
                            """);
        }

        @Test
        void refusesUnbalancedEnd() {
            assertGeneratesMessageRegex(2, 1, "Parse error: .*",
                    """
                                id;
                            end;
                            """);
        }
    }

    @Nested
    class CaseExpressions {
        @Test
        void parsesCaseExpressions() {
            assertParses("""
                    f = case a
                        when 1 then x; y; z;
                        when 2, 3, 4 then y;
                        when 5 .. 10, 15 ... 20 then z;
                        else w;
                    end;
                    
                    g = case x
                    end;
                    
                    h = case z
                        else 0;
                    end;
                    """);
        }

        @Test
        void refusesMultipleElseBranches() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 31, "Parse error: .*")
                            .addRegex(1, 39, "Parse error: .*"),
                    "case a when 1 then 0; else 1; else 2; end;");
        }

        @Test
        void refusesEmptyWhenList() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 13, "Parse error: .*")
                            .addRegex(1, 19, "Parse error: .*"),
                    "case a when then 0; else 1; end;");
        }

        @Test
        void refusesMissingExpression() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 6, "Parse error: .*")
                            .addRegex(1, 13, "Parse error: .*")
                            .addRegex(1, 21, "Parse error: .*"),
                    "case when 1 then 0; else 1; end;");
        }
    }

    @Nested
    class Comments {
        @Test
        void parsesComments() {
            assertParses("""
                    /*
                       Multiline comment
                    */
                    // Comment
                    /// Enhanced comment
                    //// Commented comment
                    identifier;
                    """);
        }

        @Test
        void refusesNestedComments() {
            assertGeneratesMessages(
                    expectedMessages().addRegex(1, 26, "Parse error: .*").atLeast(1),
                    """
                            /*  /* Nested comment */ */
                            identifier;
                            """);
        }
    }

    @Nested
    class Declarations {
        @Test
        void parsesDeclarations() {
            assertParses("""
                    /** Comment 1 */
                    param x = 10;
                    /** Comment 2 */
                    /** Comment 3 */
                    const y = 10;
                    
                    var a = 10, b = func();
                    external var $x = 5, $y;
                    mlog "abc" var abc;
                    remote a = 10;
                    remote p1 var b;
                    remote p1 "c" var d;
                    linked cell1, message1;
                    
                    allocate stack in cell1;
                    allocate heap in HEAPPTR;
                    allocate stack in cell2[10 .. 22];
                    allocate heap in bank1[1 ... 256];
                    allocate heap in cell1, stack in cell2[0 .. 30];
                    allocate stack in cell1, heap in cell2[0 .. 30];
                    """);
        }

        @Test
        void parsesArrayDeclarations() {
            assertParses("""
                    var a[];
                    var a[10];
                    var a[] = (1, 2, 3);
                    var a[3] = (1, 2, 3);
                    var a[], b;
                    var a[10], b;
                    var a[] = (1, 2, 3), b;
                    var a[3] = (1, 2, 3), b;
                    var b = 5, a[];
                    var b = 5, a[10];
                    var b = 5, a[] = (1, 2, 3);
                    var b = 5, a[3] = (1, 2, 3);
                    var c[], a[], d[];
                    var c[], a[10], d[];
                    var c[], a[] = (1, 2, 3), d[];
                    var c[], a[3] = (1, 2, 3), d[];
                    """);
        }

        @Test
        void refusesInvalidParamIdentifiers1() {
            assertGeneratesMessageRegex(1, 8, "Parse error: .*",
                    "param x.y = 10;");
        }

        @Test
        void refusesInvalidParamIdentifiers2() {
            assertGeneratesMessageRegex(1, 9, "Parse error: .*",
                    "param a + b = 10;");
        }

        @Test
        void refusesInvalidParamIdentifiers3() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 8, "Parse error: .*")
                            .addRegex(1, 9, "Parse error: .*"),
                    "param a() = 10;");
        }

        @Test
        void refusesInvalidParamIdentifiers4() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 7, "Parse error: .*")
                            .addRegex(1, 9, "Parse error: .*"),
                    "param [a] = 10;");
        }

        @Test
        void refusesInvalidConstIdentifiers1() {
            assertGeneratesMessageRegex(1, 8, "Parse error: .*",
                    "const x.y = 10;");
        }

        @Test
        void refusesInvalidConstIdentifiers2() {
            assertGeneratesMessageRegex(1, 8, "Parse error: .*",
                    "const a + b = 10;");
        }

        @Test
        void refusesInvalidConstIdentifiers3() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 8, "Parse error: .*")
                            .addRegex(1, 9, "Parse error: .*"),
                    "const a() = 10;");
        }

        @Test
        void refusesInvalidConstIdentifiers4() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 7, "Parse error: .*"),
                    "const [a] = 10;");
        }

        @Test
        void refusesInvalidAllocationType() {
            assertGeneratesMessageRegex(1, 10, "Parse error: .*",
                    "allocate memory in cell1[10 .. 20];");
        }

        @Test
        void refusesInvalidAllocationRange1() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 23, "Parse error: .*")
                            .addRegex(1, 27, "Parse error: .*"),
                    "allocate heap in cell1(10 .. 20);");
        }

        @Test
        void refusesInvalidAllocationRange2() {
            assertGeneratesMessageRegex(1, 29, "Parse error: .*",
                    "allocate heap in cell1[10 . 20];");
        }

        @Test
        void refusesInvalidAllocationRange3() {
            assertGeneratesMessageRegex(1, 30, "Parse error: .*",
                    "allocate heap in cell1[10 .... 20];");
        }

        @Test
        void refusesInvalidAllocationRange4() {
            assertGeneratesMessageRegex(1, 26, "Parse error: .*",
                    "allocate heap in cell1[10];");
        }

        @Test
        void refusesInvalidAllocationRange5() {
            assertGeneratesMessageRegex(1, 18, "Parse error: .*",
                    "allocate heap in $cell1[10 .. 20];");
        }

        @Test
        void refusesModifierAfterTypeSpec() {
            assertGeneratesMessageRegex(1, 5, "Parse error: .*",
                    "var external a;");
        }

        @Test
        void refusesRepeatedTypeSpec() {
            assertGeneratesMessageRegex(1, 5, "Parse error: .*",
                    "var var a;");
        }
    }

    @Nested
    class Directives {
        @Test
        void parsesSetDirectives() {
            assertParses("""
                    #set option;
                    #set option = value;
                    #set option = value,value;
                    #set option = 7;
                    """);
        }

        @Test
        void parsesDeclareDirectives() {
            assertParses("""
                    #declare category :keyword;
                    #declare category :keyword1, :keyword2;
                    #declare category @builtin;
                    #declare category @builtin1, @builtin2;
                    #declare category identifier;
                    #declare category identifier1, identifier2;
                    #declare category :keyword, @builtin, identifier;
                    """);
        }
    }

    @Nested
    class Elements {
        @Test
        void parsesBasicElements() {
            assertParses("""
                    identifier;
                    @built-in-identifier;
                    object.@property;
                    (a ? b : c).@property;
                    vault1.@firstItem.@id;
                    $external;
                    id.id;
                    """);
        }

        @Test
        void refusesNonProperty() {
            assertGeneratesMessageRegex(1, 12, "Parse error: .*",
                    "identifier.$identifier;");
        }
    }

    @Nested
    class EnhancedComments {
        @Test
        void parsesEnhancedCommentWithInterpolation() {
            assertParses("""
                    /// This is a comment with ${ interpolation }
                    """);
        }

        @Test
        void refusesEnhancedCommentWithMultilineInterpolation() {
            assertGeneratesMessageRegex(1, 40, "Parse error: .*",
                    """
                            /// This is a comment with multiline ${
                                interpolation}
                            """);
        }

        @Test
        void refusesNestedEnhancedComments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex("Parse error: .*").atLeast(1),
                    """
                            /// Nested ${ /// interpolation }
                            """);
        }

        @Test
        void refusesEnhancedCommentWithDoubleQuotes() {
            assertGeneratesMessages(
                    expectedMessages().add("Parse error: token recognition error at: '\"'").repeat(2),
                    """
                            /// "Quotes"
                            """);
        }

        @Test
        void refusesEnhancedCommentWithDoubleQuotesAfterPlaceholder() {
            assertGeneratesMessages(
                    expectedMessages().add("Parse error: token recognition error at: '\"'").repeat(2),
                    """
                            /// $foo" foo$" foo
                            """);
        }
    }

    @Nested
    class Expressions {
        @Test
        void parsesExpressions() {
            assertParses("""
                    ~a + !b ** c / (d * -e) % f \\ g << h >> i >>> j %% k;
                    a and b & c && d;
                    e or f || g | h;
                    a < b <= c > d >= e;
                    f == g === h != i !== j;
                    a ? b ? c : d : e ? f : g;
                    """);
        }

        @Test
        void parsesIncrementDecrement() {
            assertParses("""
                    ++a;
                    --b;
                    c++;
                    d--;
                    x[a]++;
                    --a[b];
                    """);
        }

        @Test
        void refusesWrongOperators() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(2, 5, "Parse error: .*")
                            .addRegex(3, 1, "Parse error: .*")
                    ,
                    """
                            a * * b;
                            a - / b;
                            *;
                            """);
        }

        @Test
        void refusesWrongPrefixPostfix() {
            assertGeneratesMessageRegex(1, 4, "Parse error: .*",
                    "++a--;");
        }

        @Test
        void refusesWrongTernaryOperator1() {
            assertGeneratesMessageRegex(1, 6, "Parse error: .*",
                    "a ? b;");
        }

        @Test
        void refusesWrongTernaryOperator2() {
            assertGeneratesMessageRegex(1, 5, "Parse error: .*",
                    "a : b;");
        }
    }

    @Nested
    class Formattables {
        @Test
        void parsesFormattableWithValue() {
            assertParses("$\"foo$bar\";\"baz\";");
        }

        @Test
        void parsesFormattableWithEscape() {
            assertParses("$\"foo\\$bar\";");
        }

        @Test
        void parsesMultilineFormattable() {
            assertParses("""
                    $"Formattable with ${
                        multiline
                    } interpolation";
                    """);
        }

        @Test
        void parsesMultipleFormattables() {
            assertParses("$\"Formattable with ${first} and ${second} interpolation\";");
        }

        @Test
        void parsesBasicFormattables() {
            assertParses("""
                    $"Formattable";
                    $"Formattable with $value";
                    $"Formattable with ${interpolation}";
                    $"Formattable with ${ interpolation }";
                    $"Formattable with ${ $externalVariable }";
                    $"Formattable with $ a placeholder";
                    $"Formattable with ${
                        multiline
                    } interpolation";
                    """);
        }

        @Test
        void refusesNestedFormattable() {
            assertGeneratesMessageRegex(2, 5, "Parse error: .*",
                    """
                            $"Formattable with ${
                                $"a nested"
                            } interpolation";
                            """);
        }
    }

    @Nested
    class ForEachLoops {
        @Test
        void parsesForEachLoop() {
            assertParses("""
                    for i in a, b, c do print(i); end;
                    label: for i, j in a, b, c, d do print(i + j); end;
                    for i, out j in a, b, c, d do j = 2 * i; end;
                    """);
        }

        @Test
        void parsesForEachLoopWithDeclaration() {
            assertParses("""
                    for var i in a, b, c do print(i); end;
                    label: for var i, j in a, b, c, d do print(i + j); end;
                    for var i, out j in a, b, c, d do j = 2 * i; end;
                    """);
        }

        @Test
        void refusesMissingIterator() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 22, "Parse error: .*"),
                    "for in a, b, c do x; end;");
        }

        @Test
        void refusesMissingValues() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 10, "Parse error: .*")
                            .addRegex(1, 16, "Parse error: .*"),
                    "for i in do x; end;");
        }

        @Test
        void refusesMisplacedOutValues() {
            assertGeneratesMessageRegex(1, 13, "Parse error: .*",
                    "for i in a, out b, c do x; end;");
        }

        @Test
        void refusesMissingIn() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 7, "Parse error: .*")
                            .addRegex(1, 21, "Parse error: .*"),
                    "for i a, b, c do x; end;");
        }

        @Test
        void refusesMisplacedTypeSpecification() {
            assertGeneratesMessageRegex(1, 12, "Parse error: .*",
                    "for out i, var j in a, b, c do print(i); end;");
        }
    }

    @Nested
    class FunctionCalls {
        @Test
        void parsesFunctionCalls() {
            assertParses("""
                    end();
                    a();
                    a(0);
                    a(0, 1);
                    a(out x, in y);
                    a(, in y);
                    a(ref x, in y);
                    """);
        }

        @Test
        void parsesMethodCalls() {
            assertParses("""
                    object.a();
                    object.a(0);
                    object.a(0, 1);
                    object.a(out x, in y);
                    object.a(, in y);
                    object.a(ref x, in y);
                    """);
        }

        @Test
        void parsesChainedCalls() {
            assertParses("""
                    a().b().c();
                    getlink(n).printflush();
                    """);
        }

        @Test
        void parsesFunctionsInExpressions() {
            assertParses("""
                    x.a(z) + y.b(z);
                    a(b(c()));
                    """);
        }

        @Test
        void refusesFunctionBuiltInName() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 6, "Parse error: .*"),
                    "@foo();");
        }

        @Test
        void refusesFunctionExternalName() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 6, "Parse error: .*"),
                    "$foo();");
        }

        @Test
        void refusesWrongFunctionModifiers() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 8, "Parse error: .*"),
                    "foo(in in a);");
        }

        @Test
        void refusesWrongFunctionArguments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 10, "Parse error: .*"),
                    "foo(in a b);");
        }

        @Test
        void refusesInRefFunctionModifiers() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 8, "Parse error: .*"),
                    "foo(in ref a);");
        }


        @Test
        void refusesRefOutFunctionModifiers() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 9, "Parse error: .*"),
                    "foo(ref out a);");
        }

        @Test
        void refusesModifiersWithoutArguments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 7, "Parse error: .*"),
                    "foo(in, out);");
        }

        @Test
        void refusesRefWithoutArguments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 8, "Parse error: .*"),
                    "foo(ref);");
        }
    }

    @Nested
    class FunctionDeclarations {
        @Test
        void parsesFunctionDeclarations() {
            assertParses("""
                    inline def foo(x...) end;
                    noinline def bar(x..., y...) end;
                    void baz() x; end;
                    void quux(in a, out b, in out c, out in d) a + b + c + d; end;
                    void corge(ref array) array[0] = 1; end;
                    """);
        }

        @Test
        void refusesBothModifiers() {
            assertGeneratesMessageRegex(1, 8, "Parse error: .*",
                    "inline noinline def foo() end;");
        }

        @Test
        void refusesExternalName() {
            assertGeneratesMessageRegex(1, 6, "Parse error: .*",
                    "void $foo() end;");
        }

        @Test
        void refusesMissingParameter() {
            assertGeneratesMessageRegex(1, 13, "Parse error: .*",
                    "void foo(a, , b) end;");
        }

        @Test
        void refusesWrongParameterModifiers() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 14, "Parse error: .*")
                            .addRegex(1, 19, "Parse error: .*"),
                    "void foo(out out b) end;");
        }

        @Test
        void refusesInRefParameterModifiers() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 13, "Parse error: .*")
                            .addRegex(1, 18, "Parse error: .*"),
                    "void foo(in ref a) end;");
        }

        @Test
        void refusesRefOutParameterModifiers() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 14, "Parse error: .*")
                            .addRegex(1, 19, "Parse error: .*"),
                    "void foo(ref out a) end;");
        }
    }

    @Nested
    class IfExpressions {
        @Test
        void parsesIfExpressions() {
            assertParses("""
                    if a then b; end;
                    if a then else end;
                    if a then b; c; else d; e; end;
                    if a then b; elsif c then d; else e; end;
                    if a then elsif c then else end;
                    """);
        }

        @Test
        void refusesMissingExpressions() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 4, "Parse error: .*")
                            .addRegex(1, 10, "Parse error: .*"),
                    "if then a; end;");
        }

        @Test
        void refusesMissingIfBranch() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 1, "Parse error: .*")
                            .addRegex(1, 9, "Parse error: .*"),
                    "elsif a then b; end;");
        }

        @Test
        void refusesMultipleElseBranches() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 22, "Parse error: .*")
                            .addRegex(1, 30, "Parse error: .*"),
                    "if a then b; else c; else d; end;");
        }

        @Test
        void refusesWrongOrder() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 22, "Parse error: .*")
                            .addRegex(1, 38, "Parse error: .*"),
                    "if a then b; else c; elsif g then d; end;");
        }

        @Test
        void refusesMissingSemicolons() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 16, "Parse error: .*")
                            .addRegex(1, 24, "Parse error: .*")
                            .addRegex(2, 1, "Parse error: .*"),
                    "a = if b then c else d end;");
        }
    }

    @Nested
    class IteratedForLoops {
        @Test
        void parsesForLoops() {
            assertParses("""
                    for i = 0; i < 10; i++ do print(i); end;
                    label: for ;; do end;
                    for i = 0, j = 100; i < j; i++, j-- do print(i, j); end;
                    """);
        }

        @Test
        void parsesForLoopsWithDeclarations() {
            assertParses("""
                    for var i = 0; i < 10; i++ do print(i); end;
                    for var i = 0, j = 100; i < j; i++, j-- do print(i, j); end;
                    """);
        }

        @Test
        void refusesMissingDo() {
            assertGeneratesMessageRegex(1, 24, "Parse error: .*",
                    "for i = 0; i < 10; i++ print(i); end;");
        }

        @Test
        void refusesMissingSemicolon() {
            assertGeneratesMessageRegex(1, 18, "Parse error: .*",
                    "for i = 0; i < 10 do print(i); end;");
        }

        @Test
        void refusesMisplacedTypeSpecification() {
            assertGeneratesMessageRegex(1, 13, "Parse error: .*",
                    "for i = 10, var j = 20; i < j; i++, j-- do print(i); end;");
        }
    }

    @Nested
    class Literals {
        @Test
        void parsesStringLiterals() {
            assertParses("""
                    "";
                    "A string literal";
                    """);
        }

        @Test
        void parsesColorLiterals() {
            assertParses("""
                    %abcdef;
                    %123456;
                    %ABCDEF00;
                    %12345678;
                    %[red];
                    """);
        }

        @Test
        void parsesFloatLiterals() {
            assertParses("""
                    1.0;
                    0.0;
                    .5;
                    1e5;
                    1e+5;
                    1e-5;
                    1E10;
                    1E+10;
                    1E-10;
                    2.5E10;
                    2.5E+10;
                    2.5E-10;
                    .5E10;
                    .5E+10;
                    .5E-10;
                    """);
        }

        @Test
        void parsesNumericLiterals() {
            assertParses("""
                    0b0011;
                    0x0123456789ABCDEF;
                    0xfedcba9876543210;
                    0;
                    01;
                    123;
                    """);
        }

        @Test
        void parsesOtherLiterals() {
            assertParses("""
                    '\\n';
                    null;
                    true;
                    false;
                    'A';
                    '\\'';
                    """);
        }

        @Test
        void refusesQuotesWithinLiterals() {
            assertGeneratesMessageRegex(1, 12, "Parse error: .*",
                    "a = \"Hi, \\\"friend\\\"\";");
        }

        @Test
        void refusesEmptyCharLiteral() {
            assertGeneratesMessageRegex(1, 5, "Parse error: .*",
                    "a = '';\n");
        }

        @Test
        void refusesEmptyNamedColorLiteral() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 6, "Parse error: .*"),
                    "a = %[];");
        }

        @Test
        void refusesUnclosedNamedColorLiteral() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 6, "Parse error: .*")
                            .addRegex(1, 12, "Parse error: .*"),
                    "a = %[red;n");
        }


        @Test
        void refusesMalformedNamedColorLiteral() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 6, "Parse error: .*")
                            .addRegex(1, 19, "Parse error: .*"),
                    "a = %[fluffy-bunny];");
        }

        @Test
        void refusesTooLongCharLiteral() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 5, "Parse error: .*")
                            .addRegex(1, 8, "Parse error: .*")
                    ,
                    """
                            a = '\\x';
                            """);
        }
    }

    @Nested
    class MlogBlocks {
        @Test
        void parsesEmptyMlogBlock() {
            assertParses("""
                    mlog { }
                    mlog {
                        //
                    }
                    mlog () {;;;}
                    """);
        }

        @Test
        void parsesMlogBlockWithVariousWhitespace() {
            assertParses("""
                    mlog {a}
                    mlog { a}
                    mlog {a }
                    mlog { a }
                    mlog {a;}
                    mlog { a;}
                    mlog {a; }
                    mlog { a; }
                    mlog {;a}
                    mlog { ;a}
                    mlog {;a }
                    mlog { ;a }

                    mlog {a ;}
                    mlog { a ;}
                    mlog {a ; }
                    mlog { a ; }
                    mlog { ;a}
                    mlog {  ;a}
                    mlog { ;a }
                    mlog {  ;a }

                    mlog {a; }
                    mlog { a; }
                    mlog {a;  }
                    mlog { a;  }
                    mlog {; a}
                    mlog { ; a}
                    mlog {; a }
                    mlog { ; a }

                    mlog {a ; }
                    mlog { a ; }
                    mlog {a ;  }
                    mlog { a ;  }
                    mlog { ; a}
                    mlog {  ; a}
                    mlog { ; a }
                    mlog {  ; a }
                    """);
        }

        @Test
        void parsesBlockWithoutWhitespace() {
            assertParses("mlog{a;a b;c}");
        }

        @Test
        void parsesStringLiterals() {
            assertParses("""
                    mlog {
                        print "A string literal"
                        print ""
                    }
                    """);
        }

        @Test
        void parsesColorLiterals() {
            assertParses("""
                    mlog {
                        print %abcdef
                        print %123456
                        print %ABCDEF00
                        print %ABCDEF00
                        print %[red]
                    }
                    """);
        }

        @Test
        void parsesFloatLiterals() {
            assertParses("""
                    mlog {
                        print 1.0
                        print 0.0
                        print .5
                        print 1e5
                        print 1e+5
                        print 1e-5
                        print 1E10
                        print 1E+10
                        print 1E-10
                        print 2.5E10
                        print 2.5E+10
                        print 2.5E-10
                        print .5E10
                        print .5E+10
                        print .5E-10

                        print +1.0
                        print +0.0
                        print +.5
                        print +1e5
                        print +1e+5
                        print +1e-5
                        print +1E10
                        print +1E+10
                        print +1E-10
                        print +2.5E10
                        print +2.5E+10
                        print +2.5E-10
                        print +.5E10
                        print +.5E+10
                        print +.5E-10

                        print -1.0
                        print -0.0
                        print -.5
                        print -1e5
                        print -1e+5
                        print -1e-5
                        print -1E10
                        print -1E+10
                        print -1E-10
                        print -2.5E10
                        print -2.5E+10
                        print -2.5E-10
                        print -.5E10
                        print -.5E+10
                        print -.5E-10
                    }
                    """);
        }

        @Test
        void parsesNumericLiterals() {
            assertParses("""
                    mlog {
                        print 0b0011
                        print 0x0123456789ABCDEF
                        print 0xfedcba9876543210
                        print 0
                        print 01
                        print 123
                        print +0b0011
                        print +0x0123456789ABCDEF
                        print +0xfedcba9876543210
                        print +0
                        print +01
                        print +123
                        print -0b0011
                        print -0x0123456789ABCDEF
                        print -0xfedcba9876543210
                        print -0
                        print -01
                        print -123
                    }
                    """);
        }

        @Test
        void parsesOtherLiterals() {
            assertParses("""
                    mlog {
                        print null
                        print true
                        print false
                        print 'A'
                        print '\\''
                        print '\\n'
                    }
                    """);
        }

        @Test
        void refusesQuotesWithinStringLiterals() {
            assertGeneratesMessageRegex(2, 18, "Parse error: .*",
                    """
                            mlog {
                                print "Hi, \\"friend\\""
                            }
                            """);
        }

        @Test
        void refusesEmptyCharLiteral() {
            assertGeneratesMessageRegex(1, 18, "Parse error: .*",
                    "mlog { printchar '' }");
        }

        @Test
        void refusesTooLongCharLiteral() {
            assertGeneratesMessageRegex(
                    1, 18, "Parse error: .*",
                    "mlog { printchar '\\x' }");
        }
    }

    @Nested
    class Requires {
        @Test
        void parsesLibraryRequire() {
            assertParses("require units;");
        }

        @Test
        void parsesFileRequire() {
            assertParses("""
                    require "file.mnd";
                    """);
        }

        @Test
        void parsesRemoteFileRequire() {
            assertParses("""
                    require "file.mnd" remote processor1;
                    """);
        }

        @Test
        void parsesMultipleRemoteFileRequire() {
            assertParses("""
                    require "file.mnd" remote processor1, processor2, processor3;
                    """);
        }
    }

    @Nested
    class RangedForLoops {
        @Test
        void parsesRangedForLoop() {
            assertParses("""
                    Outer:
                    for i in 0 ... 10 do
                        Inner:
                        for j in A+10 .. B + 20 do
                            print(i);
                        end;
                    end;
                    """);
        }

        @Test
        void parsesRangedForLoopWithDeclaration() {
            assertParses("""
                    Outer:
                    for var i in 0 ... 10 do
                        Inner:
                        for var j in A+10 .. B + 20 do
                            print(i);
                        end;
                    end;
                    """);
        }

        @Test
        void refusesMissingDo() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 19, "Parse error: .*")
                            .addRegex(1, 22, "Parse error: .*"),
                    "for i in 0 ... 10 a; end;");
        }

        @Test
        void refusesMultipleControlVariables() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 15, "Parse error: .*")
                            .addRegex(1, 25, "Parse error: .*"),
                    "for i, j in 0 ... 10 a; end;");
        }
    }

    @Nested
    class Semicolons {
        @Test
        void parsesMultipleSemicolons() {
            assertParses("""
                    ;;
                    print(a);;
                    b;;
                    """);
        }

        @Test
        void refusesMissingSemicolon() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 2, "Parse error: .*")
                            .addRegex(2, 2, "Parse error: .*")
                            .addRegex(3, 2, "Parse error: .*")
                            .addRegex(4, 2, "Parse error: .*"),
                    """
                            a
                            b
                            c
                            d
                            """);
        }

        @Test
        void refusesMissingSemicolonBeforeComment() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 3, "Parse error: .*")
                            .addRegex(3, 3, "Parse error: .*"),
                    """
                            xx
                            // This is a comment
                            yy
                            """);
        }
    }

    @Nested
    class Statements {
        @Test
        void parsesBreakContinueReturn() {
            assertParses("""
                    break;
                    continue;
                    return;
                    break label;
                    continue label;
                    return value;
                    """);
        }

        @Test
        void refusesInvalidLabels() {
            assertGeneratesMessageRegex(1, 8, "Parse error: .*",
                    "break x + 1;");
        }

        @Test
        void refusesInvalidValues() {
            assertGeneratesMessageRegex(1, 7, "Parse error: .*",
                    "return for i in 0 ... 10 do a; end;");
        }
    }

    @Nested
    class WhileLoops {
        @Test
        void parsesForEachLoops() {
            assertParses("""
                    while true do foo(); end;
                    while foo() < bar() do baz(); end;
                    while a do
                        do
                            print(b);
                        while b != a;
                    end;
                    do while message1 == null;
                    """);
        }

        @Test
        void refusesWhileDoMissingCondition() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 7, "Parse error: .*")
                            .addRegex(1, 15, "Parse error: .*"),
                    "while do foo(); end;");
        }

        @Test
        void refusesWhileDoMissingDo() {
            assertGeneratesMessageRegex(1, 13, "Parse error: .*",
                    "while a < b foo(); end;");
        }

        @Test
        void refusesDoWhileMissingCondition() {
            assertGeneratesMessageRegex(1, 16, "Parse error: .*",
                    "do foo(); while;");
        }

        @Test
        void refusesDoWhileMissingDo() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 12, "Parse error: .*")
                            .addRegex(2, 1, "Parse error: .*"),
                    "while a < b;");
        }
    }
}
