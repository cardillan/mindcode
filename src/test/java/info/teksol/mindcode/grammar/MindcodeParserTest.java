package info.teksol.mindcode.grammar;

import info.teksol.mindcode.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MindcodeParserTest extends AbstractParserTest {
    @Test
    void parsesSensorAccess() {
        assertDoesNotThrow(() -> parse("foundation1.copper < 1000"));
        assertDoesNotThrow(() -> parse("foundation1.copper < foundation1.itemCapacity"));
        assertDoesNotThrow(() -> parse("tank1.water < tank1.liquidCapacity"));
    }

    @Test
    void parsesControlStatements() {
        assertDoesNotThrow(() -> parse("conveyor1.enabled = foundation1.copper < foundation1.itemCapacity\n"));
    }

    @Test
    void parsesTheEmptyProgram() {
        assertDoesNotThrow(() -> parse(""));
    }

    @Test
    void parsesSimpleRvalues() {
        assertDoesNotThrow(() -> parse("foo\nbar\n"));
    }

    @Test
    void parsesAssignmentOfCalculations() {
        assertDoesNotThrow(() -> parse("foo = bar ** (n - 2)\n"));
    }

    @Test
    void parsesSimpleWhileLoop() {
        assertDoesNotThrow(() -> parse("n = 5\nwhile n > 0 {\n  n -= 1\n}\n"));
    }

    @Test
    void reportsSyntaxError() {
        final Tuple2<MindcodeParser.ProgramContext, List<String>> actual = parseWithErrors("while");
        assertEquals(1, actual._2.size(), "Expected at least one syntax error report");
    }

    @Test
    void parsesHeapAccesses() {
        assertDoesNotThrow(() -> parse("cell1[0] = cell[1] + 1"));
    }

    @Test
    void parsesIfExpression() {
        assertDoesNotThrow(() -> parse("value = if HEAP[4] == 0 { false\n} else { true\n}"));
        assertDoesNotThrow(() -> parse("if false { n += 1\n}\n"));
    }

    @Test
    void parsesRefs() {
        assertDoesNotThrow(() -> parse("while @unit == null {\nubind(poly)\n}\n"));
    }

    @Test
    void parsesFlagAssignment() {
        assertDoesNotThrow(() -> parse("flag(FLAG)"));
    }

    @Test
    void parsesUnaryMinus() {
        assertDoesNotThrow(() -> parse("-1 * dx"));
    }

    @Test
    void parsesHeapReferencesWithRvalues() {
        assertDoesNotThrow(() -> parse("cell1[dx] = 1"));
    }

    @Test
    void parsesCStyleForLoop() {
        assertDoesNotThrow(() -> parse("for i = 0, j = 0; i < j ; i += 1 {\nprint(j)\n}\n"));
    }

    @Test
    void parsesInclusiveIteratorStyleLoop() {
        assertDoesNotThrow(() -> parse("for n in 1 .. 17 {\nprint(n)\n}"));
    }

    @Test
    void parsesExclusiveIteratorStyleLoop() {
        assertDoesNotThrow(() -> parse("for n in 1 ... 17 {\nprint(n)\n}"));
    }

    @Test
    void parsesHeapAllocation() {
        assertDoesNotThrow(() -> parse("allocate heap in cell4 0 .. 64"));
    }

    @Test
    void parsesGlobalReferences() {
        assertDoesNotThrow(() -> parse("" +
                        "allocate heap in cell2 14 .. 20\n" +
                        "$dx += 1\n" +
                        "$dy = $dx - 4\n"
                )
        );
    }
}
