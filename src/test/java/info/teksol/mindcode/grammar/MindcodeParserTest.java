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
    /*
        @copper
        @lead
        @metaglass
        @graphite
        @sand
        @coal
        @titanium
        @scrap
        @silicon
        @pyratite

        @water
        @slag
        @cryofluid

        totalItems
        firstItem
        totalLiquids
        totalPower
        itemCapacity
        liquidCapacity
        powerCapacity
        powerNetStored
        powerNetCapacity
        powerNetIn
        powerNetOut
        ammo
        ammoCapacity
        health
        maxHealth
        heat
        efficiency
        timescale
        rotation
        x
        y
        shootX
        shootY
        size
        dead
        range
        shooting
        boosting
        mineX
        mineY
        mining
        team
        type
        flag
        controlled
        controller
        commanded
        name
        config
        payloadCount
        payloadType
        enabled
        configure
    */

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
    void parsesUnitReferences() {
        assertDoesNotThrow(() -> parse("while @unit == null {\n  @unit = ubind(poly)\n}\n"));
    }
}
