package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class MemberAccessBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class MemberAccess {
        @Test
        void compilesBasicMemberAccess() {
            assertCompiles("""
                            switch1.enabled = !switch1.enabled;
                            """,
                    createInstruction(SENSOR, var(1), "switch1", "@enabled"),
                    createInstruction(OP, "equal", var(2), var(1), "false"),
                    createInstruction(CONTROL, "enabled", "switch1", var(2))
            );
        }

        @Test
        void compilesMemberAccessAsOutputArgument() {
            assertCompiles("""
                            void foo(out value)
                                value = false;
                            end;
                            
                            foo(out switch1.enabled);
                            """,
                    createInstruction(SET, "__fn1_value", "false"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(CONTROL, "enabled", "switch1", "__fn1_value")
            );
        }
    }

    @Nested
    class PropertyAccess {
        @Test
        void compilesBasicPropertyAccess() {
            assertCompiles("""
                            amount = vault1.@coal;
                            """,
                    createInstruction(SENSOR, var(0), "vault1", "@coal"),
                    createInstruction(SET, "amount", var(0))
            );
        }

        @Test
        void compilesComplexPropertyAccess() {
            assertCompiles("""
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