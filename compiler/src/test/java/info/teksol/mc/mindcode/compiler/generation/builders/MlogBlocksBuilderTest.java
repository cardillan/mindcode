package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.PRINT;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;

@NullMarked
class MlogBlocksBuilderTest extends AbstractCodeGeneratorTest {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.OPTIMIZER;
    }

    @Test
    void compilesSimpleMlogBlock() {
        assertCompilesTo("""
                        mlog {
                            print "Hello World!"
                        }
                        """,
                customInstruction("print", q("Hello World!"))
        );
    }

    @Test
    void compilesMlogBlockWithLiterals() {
        assertCompilesTo("""
                        mlog {
                            print "a"
                            print @coal
                            print 123
                            print +123
                            print -123
                            print 1.23
                            print -1.23
                            print +1.23
                            print 1.23e15
                            print -1.23e15
                            print +1.23e15
                            print 0b0011;
                            print 0x3456789ABCDEF;
                            print 0xfedcba9876543;
                            print +0b0011;
                            print +0x3456789ABCDEF;
                            print +0xfedcba9876543;
                            print -0b0011;
                            print -0x3456789ABCDEF;
                            print -0xfedcba9876543;
                            print 'A'
                            print '\\n'
                            print %123456
                            print %12345678
                            print %[red]
                        }
                        """,
                customInstruction("print", q("a")),
                customInstruction("print", "@coal"),
                customInstruction("print", "123"),
                customInstruction("print", "+123"),
                customInstruction("print", "-123"),
                customInstruction("print", "1.23"),
                customInstruction("print", "-1.23"),
                customInstruction("print", "1.23"),
                customInstruction("print", "1230000000000000"),
                customInstruction("print", "-1230000000000000"),
                customInstruction("print", "1230000000000000"),
                customInstruction("print", "0b0011"),
                customInstruction("print", "0x3456789ABCDEF"),
                customInstruction("print", "0xfedcba9876543"),
                customInstruction("print", "+0b0011"),
                customInstruction("print", "+0x3456789ABCDEF"),
                customInstruction("print", "+0xfedcba9876543"),
                customInstruction("print", "-0b0011"),
                customInstruction("print", "-0x3456789ABCDEF"),
                customInstruction("print", "-0xfedcba9876543"),
                customInstruction("print", "65"),
                customInstruction("print", "10"),
                customInstruction("print", "%123456"),
                customInstruction("print", "%12345678"),
                customInstruction("print", "%[red]")
        );
    }

    @Test
    void compilesMlogBlockWithLabels() {
        assertCompilesTo("""
                        mlog {
                            label:
                            call label
                            jump label always
                        }
                        """,
                customInstruction("m0_label:"),
                customInstruction("call", "m0_label"),
                customInstruction("jump", "m0_label", "always")
        );
    }

    @Test
    void compilesTwoMlogBlocksWithLabels() {
        assertCompilesTo("""
                        mlog {
                            label:
                            jump label always
                        }
                        mlog {
                            label:
                            jump label always
                        }
                        """,
                customInstruction("m0_label:"),
                customInstruction("jump", "m0_label", "always"),
                customInstruction("m1_label:"),
                customInstruction("jump", "m1_label", "always")
        );
    }

    @Test
    void compilesMlogBlockWithVariables() {
        assertCompilesTo("""
                        #set mlog-block-optimization = false;
                        var foo = 5, bar = 10;
                        mlog (in foo, out baz) {
                            print foo
                            print :foo
                            print :$foo
                            op add baz $foo $bar
                        }
                        print(baz);
                        """,
                createInstruction(SET, ".foo", "5"),
                createInstruction(SET, ".bar", "10"),
                customInstruction("print", ".foo"),
                customInstruction("print", "foo"),
                customInstruction("print", "$foo"),
                customInstruction("op", "add", ":baz", ".foo", ".bar"),
                createInstruction(PRINT, ":baz")
        );
    }

    @Test
    void processesKeywordTokens() {
        assertCompilesTo("""
                        #set mlog-block-optimization = false;
                        var foo = 10;
                        mlog (in foo) {
                            :print :"Hello"         // Supports string literals as raw tokens
                            label:
                            :jump label :always
                            foo foo                 // The opcode won't be substituted
                            :foo :foo
                        }
                        """,
                createInstruction(SET, ".foo", "10"),
                customInstruction("print", q("Hello")),
                customInstruction("m0_label:"),
                customInstruction("jump", "m0_label", "always"),
                customInstruction("foo", ".foo"),
                customInstruction("foo", "foo")
        );
    }

    @Test
    void optimizesMlogBlockVariables() {
        assertCompilesTo("""
                        foo = 10;
                        mlog {
                            print $foo
                        }
                        """,
                customInstruction("print", "10")
        );
    }

    @Test
    void supportsLocalJumps() {
        assertCompilesTo("""
                        mlog {
                            jump label always
                            label:
                            print "Hello World!"
                        }
                        """,
                customInstruction("jump", "m0_label", "always"),
                customInstruction("m0_label:"),
                customInstruction("print", q("Hello World!"))
        );
    }

    @Test
    void doesNotMergeAcrossMlogBlock() {
        assertCompilesTo("""
                        print("a", "b");
                        mlog {
                            set x 10
                        }
                        print("c", "d");
                        """,
                createInstruction(PRINT, q("ab")),
                customInstruction("set", "x", "10"),
                createInstruction(PRINT, q("cd"))
        );
    }

    @Test
    void generatesUninitializedWarning() {
        assertGeneratesMessage(
                "Variable 'foo' is not initialized.",
                "var foo; mlog { set $foo 10 } print(foo);");
    }

    @Test
    void refusesConflictingLabels() {
        assertGeneratesMessage(
                "Label 'label:' conflicts with a declared input or output variable.",
                "mlog (label) { label: }");
    }

    @Test
    void refusesDuplicateLabels() {
        assertGeneratesMessage(
                "Label 'label' is already declared in this mlog block.",
                "mlog { label: ; label: }");
    }

    @Test
    void refusesInvalidLabels() {
        assertGeneratesMessage(
                "A label must contain only alphanumeric characters.",
                "mlog  { la()bel: }");
    }

    @Test
    void refusesUnresolvedLabels() {
        assertGeneratesMessage(
                "Label 'label' is not defined in this mlog block.",
                "mlog { jump label always 0 0 }");
    }

    @Test
    void refusesUnknownVariables() {
        assertGeneratesMessage(
                "Variable 'foo' not defined outside the mlog block.",
                "mlog { print $foo }");
    }

    @Test
    void refusesUnwritableOutputVariables() {
        assertGeneratesMessage(
                "Assignment to constant or parameter 'p' not allowed.",
                "param p = 10; mlog (out p) { set p 5 }");
    }

    @Test
    void refusesDeclaredComplexVariables() {
        assertGeneratesMessage(
                "Variable 'foo' is not a plain mlog variable and cannot be accessed in an mlog block.",
                "external(cell1[0]) foo; mlog (in foo) { print bar }");
    }

    @Test
    void refusesUndeclaredComplexVariables() {
        assertGeneratesMessage(
                "Variable 'foo' is not a plain mlog variable and cannot be accessed in an mlog block.",
                "external(cell1[0]) foo; mlog { print $foo }");
    }
}
