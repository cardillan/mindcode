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
    }

    @Nested
    class Errors {
        @Test
        void refusesUnknownProperty() {
            assertGeneratesMessages(expectedMessages().add("Unknown property 'foo'."),
                    """
                            a = vault1.foo;
                            """);
        }

        @Test
        void refusesInvalidExpression() {
            assertGeneratesMessages(expectedMessages().add("Cannot invoke properties on this expression."),
                    """
                            $"alora".enabled = true;
                            """);
        }
    }
}