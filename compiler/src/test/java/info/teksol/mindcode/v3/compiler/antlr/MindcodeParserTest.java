package info.teksol.mindcode.v3.compiler.antlr;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MindcodeParserTest extends AbstractParserTest {

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
        void refusesInvalidArrays() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 1, "Parse error: .*")
                            .addRegex(1, 7, "Parse error: .*")
                            .addRegex(2, 7, "Parse error: .*")
                            .addRegex(3, 5, "Parse error: .*")
                            .addRegex(3, 7, "Parse error: .*")
                            .addRegex(4, 9, "Parse error: .*")
                            .addRegex(5, 5, "Parse error: .*")
                    ,
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
                    a=b;
                    a=b=true;
                    a+=b*=c**=d;
                    a=b?c:d;
                    a/=b?c:d;
                    @var=x;
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
        void acceptsCodeBlocks() {
            assertParses("""
                    a;
                    begin
                        b;
                    end;
                    c;
                    """);
        }

        @Test
        void acceptsMultipleSemicolons() {
            assertParses("""
                    ;
                    identifier;;
                    ;
                    """);
        }

        @Test
        void refusesMissingSemicolon() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(3, 1, "Parse error: .*")
                            .addRegex(5, 1, "Parse error: .*")
                    ,
                    """
                            id
                            // This is a comment
                            id
                            """);
        }

        @Test
        void refusesUnbalancedBegin() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(4, 1, "Parse error: .*"),
                    """
                            begin
                                id;
                            """);
        }

        @Test
        void refusesUnbalancedEnd() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(2, 1, "Parse error: .*"),
                    """
                                id;
                            end;
                            """);
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
    class Directives {
        @Test
        void parsesDirectives() {
            assertParses("""
                    #set option;
                    #set option = value;
                    #set option = value,value;
                    #set option = 7;
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
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 12, "Parse error: .*"),
                    """
                            identifier.$identifier;
                            """);
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
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 40, "Parse error: .*"),
                    """
                            /// This is a comment with multiline ${
                                interpolation}
                            """);
        }

        @Test
        void refusesNestedEnhancedComments() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex("Parse error: .*").atLeast(1)
                    ,
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
                    ~a + !b ** c / (d * -e) % f \\ g << h >> i;
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
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 4, "Parse error: .*")
                    ,
                    """
                            ++a--;
                            """);
        }

        @Test
        void refusesWrongTernaryOperator1() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 6, "Parse error: .*")
                    ,
                    """
                            a ? b;
                            """);
        }

        @Test
        void refusesWrongTernaryOperator2() {
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(1, 3, "Parse error: .*")
                    ,
                    """
                            a : b;
                            """);
        }
    }

    @Nested
    class Formattable {
        @Test
        void parsesFormattableWithValue() {
            assertParses("""
                    $"foo$bar";"baz";
                    """);
        }

        @Test
        void parsesFormattableWithEscape() {
            assertParses("""
                    $"foo\\$bar";
                    """);
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
            assertGeneratesMessages(
                    expectedMessages()
                            .addRegex(2, 5, "Parse error: .*")
                    ,
                    """
                            $"Formattable with ${
                                $"a nested"
                            } interpolation";
                            """);
        }
    }

    @Nested
    class Functions {
        @Test
        void parsesFunctionCalls() {
            assertParses("""
                    end();
                    a();
                    a(0);
                    a(0, 1);
                    a(out x, in y);
                    a(, in y);
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
                    null;
                    true;
                    false;
                    """);
        }
    }
}
