package info.teksol.mindcode.grammar;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.ExpectedMessages;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Order(99)
class MindcodeParserTest extends AbstractParserTest {

    @Test
    void parsesDecimalNumber() {
        assertDoesNotThrow(() -> parse("0123456789;"));
    }

    @Test
    void parsesHexadecimalNumber() {
        assertDoesNotThrow(() -> parse("0x0123456789ABCDEFabcdef;"));
    }

    @Test
    void parsesBinaryNumber() {
        assertDoesNotThrow(() -> parse("0b010101;"));
    }

    @Test
    void parsesFloatingPointNumber() {
        assertDoesNotThrow(() -> parse("1.0;"));
        assertDoesNotThrow(() -> parse("0.0;"));
        assertDoesNotThrow(() -> parse("1e5;"));
        assertDoesNotThrow(() -> parse("1e+5;"));
        assertDoesNotThrow(() -> parse("1e-5;"));
        assertDoesNotThrow(() -> parse("1E10;"));
        assertDoesNotThrow(() -> parse("1E+10;"));
        assertDoesNotThrow(() -> parse("1E-10;"));
        assertDoesNotThrow(() -> parse("2.5E10;"));
        assertDoesNotThrow(() -> parse("2.5E+10;"));
        assertDoesNotThrow(() -> parse("2.5E-10;"));
    }

    @Test
    void parsesFunctionDeclaration() {
        assertDoesNotThrow(() -> parse("def foo(a, in b, in out c, out d) print(a); end;"));
    }

    @Test
    void parsesTheEmptyProgram() {
        assertDoesNotThrow(() -> parse(""));
    }

    @Test
    void parsesSensorAccess() {
        assertAll(
                () -> parse("foundation1.copper < 1000;"),
                () -> parse("foundation1.copper < foundation1.itemCapacity;"),
                () -> parse("tank1.water < tank1.liquidCapacity;")
        );
    }

    @Test
    void parsesControlStatements() {
        assertDoesNotThrow(() -> parse("conveyor1.enabled = foundation1.copper < foundation1.itemCapacity;"));
    }

    @Test
    void parsesSimpleRvalues() {
        assertDoesNotThrow(() -> parse("foo;\nbar;\n"));
    }

    @Test
    void parsesAssignmentOfCalculations() {
        assertDoesNotThrow(() -> parse("foo = bar ** (n - 2);"));
    }

    @Test
    void parsesSimpleWhileLoop() {
        assertDoesNotThrow(() -> parse("n = 5;\nwhile n > 0 do\nn -= 1;\nend;"));
    }

    @Test
    void reportsSyntaxError() {
        List<MindcodeMessage> errors = parseWithErrors("while");
        assertEquals(1, errors.size(), "Expected one syntax error");
    }

    @Test
    void parsesHeapAccesses() {
        assertDoesNotThrow(() -> parse("cell1[0] = cell[1] + 1;"));
    }

    @Test
    void parsesIfExpression() {
        assertAll(
                () -> parse("value = HEAP[4] == 0 ? false : true;"),
                () -> parse("if false then\nn += 1;\nend;")
        );
    }

    @Test
    void parsesRefs() {
        assertDoesNotThrow(() -> parse("while @unit == null do\nubind(poly);\nend;\n"));
    }

    @Test
    void parsesFlagAssignment() {
        assertDoesNotThrow(() -> parse("flag(FLAG);"));
    }

    @Test
    void parsesUnaryMinus() {
        assertDoesNotThrow(() -> parse("-1 * dx;"));
    }

    @Test
    void parsesHeapReferencesWithRvalues() {
        assertDoesNotThrow(() -> parse("cell1[dx] = 1;"));
    }

    @Test
    void parsesCStyleForLoop() {
        assertDoesNotThrow(() -> parse("for i = 0, j = 0; i < j ; i += 1 do\nprint(j);\nend;"));
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertDoesNotThrow(() -> parse("for n in 1 .. 17 do\nprint(n);\nend;\n"));
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertDoesNotThrow(() -> parse("for n in 1 ... 17 do\nprint(n);\nend;\n"));
    }

    @Test
    void parsesHeapAllocation() {
        assertDoesNotThrow(() -> parse("allocate heap in cell4[0 .. 64];"));
    }

    @Test
    void parsesGlobalReferences() {
        assertDoesNotThrow(() -> parse("""
                        allocate heap in cell2[14 .. 20];
                        $dx += 1;
                        $dy = $dx - 4;
                        """
                )
        );
    }

    @Test
    void parsesRemarkComments() {
        assertDoesNotThrow(() -> parse("""
                        // This is a comment
                        /// This is a remark
                        """
                )
        );
    }

    @Test
    void refusesInvalidInputs() {
        List<MindcodeMessage> messages = parseWithErrors("1..0;");
        ExpectedMessages.create()
                .add(1, 2, "Parse error: missing ';' before '.'")
                .addRegex(1, 2, "Parse error: '\\.': mismatched input '\\.' expecting \\{.*\\}")
                .validate(messages);
    }

    @Test
    void refusesDoubleInModifiers() {
        List<MindcodeMessage> messages = parseWithErrors("def f1(in in a) print(a); end;");
        ExpectedMessages.create()
                .add(1, 11, "Parse error: unexpected 'in'")
                .add(1, 15, "Parse error: missing ';' before ')'")
                .addRegex(1, 15, "Parse error: '\\)': mismatched input '\\)' expecting.*")
                .validate(messages);
    }

    @Test
    void refusesInOutInModifiers() {
        List<MindcodeMessage> messages = parseWithErrors("def f1(in out in a) print(a); end;");
        ExpectedMessages.create()
                .add(1, 15, "Parse error: unexpected 'in'")
                .add(1, 19, "Parse error: missing ';' before ')'")
                .addRegex(1, 19, "Parse error: '\\)': mismatched input '\\)' expecting.*")
                .validate(messages);
    }

    @Test
    void refusesDoubleOutModifiers() {
        List<MindcodeMessage> messages = parseWithErrors("def f1(out out a) print(a); end;");
        ExpectedMessages.create()
                .add(1, 12, "Parse error: unexpected 'out'")
                .add(1, 17, "Parse error: missing ';' before ')'")
                .addRegex(1, 17, "Parse error: '\\)': mismatched input '\\)' expecting.*")
                .validate(messages);
    }

    @Test
    void refusesOutInOutModifiers() {
        List<MindcodeMessage> messages = parseWithErrors("def f1(out in out a) print(a); end;");
        ExpectedMessages.create()
                .add(1, 15, "Parse error: unexpected 'out'")
                .add(1, 20, "Parse error: missing ';' before ')'")
                .addRegex(1, 20, "Parse error: '\\)': mismatched input '\\)' expecting.*")
                .validate(messages);
    }
}
