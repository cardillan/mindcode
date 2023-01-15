package info.teksol.mindcode.mindustry.optimisation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimisationTest {

    @Test
    void optimisationMappingIsUnambiguous() {
        for (Optimisation o : Optimisation.values()) {
            assertEquals(o, Optimisation.forFlag(o.getFlag()), "Optimisation " + o + " has ambiguous mapping.");
        }
    }

    @Test
    void optimizationFlagsAreUnique() {
        Map<Character, List<Optimisation>> map = Stream.of(Optimisation.values()).collect(Collectors.groupingBy(Optimisation::getFlag));

        for (Map.Entry<Character, List<Optimisation>> e : map.entrySet()) {
            assertEquals(e.getValue().size(), 1, "Character '" + e.getKey() + "' is used to map to more than one optimizer.");
        }
    }
}
