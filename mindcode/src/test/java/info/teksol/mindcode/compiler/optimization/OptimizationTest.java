package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimizationTest {

    @Test
    void optimizationMappingIsUnambiguous() {
        for (Optimization o : Optimization.values()) {
            assertEquals(o, Optimization.forFlag(o.getFlag()), "Optimization " + o + " has ambiguous mapping.");
        }
    }

    @Test
    void optimizationFlagsAreUnique() {
        Map<Character, List<Optimization>> map = Stream.of(Optimization.values()).collect(Collectors.groupingBy(Optimization::getFlag));

        for (Map.Entry<Character, List<Optimization>> e : map.entrySet()) {
            assertEquals(e.getValue().size(), 1, "Character '" + e.getKey() + "' is used to map to more than one optimizer.");
        }
    }
}
