package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class MemberAccessBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class MemberAccess {
        @Test
        void compilesBasicMemberAccess() {
            assertCompilesTo("""
                            switch1.enabled = !switch1.enabled;
                            """,
                    createInstruction(SENSOR, var(1), "switch1", "@enabled"),
                    createInstruction(OP, "equal", var(2), var(1), "false"),
                    createInstruction(CONTROL, "enabled", "switch1", var(2))
            );
        }

        @Test
        void compilesMemberAccessAsOutputArgument() {
            assertCompilesTo("""
                            void foo(out value)
                                value = false;
                            end;
                            
                            foo(out switch1.enabled);
                            """,
                    createInstruction(SET, "__fn0_value", "false"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(CONTROL, "enabled", "switch1", "__fn0_value")
            );
        }

        @Test
        void compilesMemberAccessAsUnchangedOutputArgument() {
            assertCompilesTo("""
                            void foo(out value)
                                value = false;
                                A = switch2;
                            end;
                            
                            A = switch1;
                            foo(out A.enabled);
                            """,
                    createInstruction(SET, "A", "switch1"),
                    createInstruction(SET, var(0), "A"),
                    createInstruction(SET, "__fn0_value", "false"),
                    createInstruction(SET, "A", "switch2"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(CONTROL, "enabled", var(0), "__fn0_value")
            );
        }
    }


    @Nested
    class MemberErrors {
        @Test
        void refusesUnknownProperty() {
            assertGeneratesMessages(expectedMessages().add("Unknown property 'foo'."),
                    """
                            vault1.foo = true;
                            """);
        }

        @Test
        void refusesFormattable() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            $"alora".enabled = false;
                            """);
        }

        @Test
        void refusesArray() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            var a[2];
                            a.enabled = true;
                            """);
        }

        @Test
        void refusesExternalArrayElement() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            allocate heap in cell1;
                            external a[2];
                            a[1].enabled = false;
                            """);
        }
    }

    @Nested
    class PropertyAccess {
        @Test
        void compilesBasicPropertyAccess() {
            assertCompilesTo("""
                            amount = vault1.@blast-compound;
                            """,
                    createInstruction(SENSOR, var(0), "vault1", "@blast-compound"),
                    createInstruction(SET, "amount", var(0))
            );
        }

        @Test
        void compilesComplexPropertyAccess() {
            assertCompilesTo("""
                            id = vault1.@firstItem.@id;
                            """,
                    createInstruction(SENSOR, var(0), "vault1", "@firstItem"),
                    createInstruction(SENSOR, var(1), var(0), "@id"),
                    createInstruction(SET, "id", var(1))
            );
        }

        @Test
        void compilesArrayElementPropertyAccess() {
            assertCompilesTo("""
                            var a[2];
                            for i in 0 ... 2 do
                                a[i] = getlink(i);
                                print(a[i].@type);
                            end;
                            """,
                    createInstruction(SET, ":i", "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "greaterThanEq", ":i", "2"),
                    createInstruction(SET, tmp(0), ":i"),
                    createInstruction(GETLINK, tmp(2), ":i"),
                    createInstruction(WRITEARR, tmp(2), ".a[]", tmp(0)),
                    createInstruction(SET, tmp(3), ":i"),
                    createInstruction(READARR, tmp(4), ".a[]", tmp(3)),
                    createInstruction(SENSOR, tmp(5), tmp(4), "@type"),
                    createInstruction(PRINT, tmp(5)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
            );
        }
    }

    @Nested
    class PropertyErrors {
        @Test
        void refusesUnknownProperty() {
            assertGeneratesMessages(expectedMessages().add("Built-in variable '@foo' not recognized."),
                    """
                            a = vault1.@foo;
                            """);
        }

        @Test
        void refusesFormattable() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            x = $"alora".@enabled;
                            """);
        }

        @Test
        void refusesArray() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            var a[2];
                            print(a.@enabled);
                            """);
        }

        @Test
        void refusesExternalArrayElement() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            allocate heap in cell1;
                            external a[2];
                            print(a[1].@enabled);
                            """);
        }
    }
}