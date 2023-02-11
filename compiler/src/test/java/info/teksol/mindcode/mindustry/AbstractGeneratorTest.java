package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.AbstractAstTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractGeneratorTest extends AbstractAstTest {
    protected final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
    private Set<String> registered = new HashSet<>();
    private Map<String, String> expectedToActual = new TreeMap<>();
    private Map<String, String> actualToExpected = new TreeMap<>();

    protected String var(int id) {
        String key = "___" + id;
        registered.add(key);
        return key;
    }

    protected void assertLogicInstructionsMatch(List<LogicInstruction> expected, List<LogicInstruction> actual) {
        for (int i = 0; i < expected.size() && i < actual.size(); i++) {
            final LogicInstruction left = expected.get(i);
            final LogicInstruction right = actual.get(i);
            if (left.getOpcode().equals(right.getOpcode())) {
                if (matchArgs(left, right)) {
                    continue;
                } else {
                    assertEquals(
                            prettyPrint(replaceVarsIn(expected)),
                            prettyPrint(actual),
                            "Expected\n" + left + "\nbut found\n" + right + "\non row index " + i + "\n" +
                                    "expected->actual: " + expectedToActual + "\n" +
                                    "actual->expected: " + actualToExpected + "\n"
                    );
                }
            }

            assertEquals(prettyPrint(replaceVarsIn(expected)), prettyPrint(actual), "Failed to var instruction at index " + i);
        }

        if (actual.size() != expected.size()) {
            assertEquals(prettyPrint(replaceVarsIn(expected)), prettyPrint(actual),
                    "Expected to have same size, found " + expected.size() + " in expected " +
                            "vs " + actual.size() + " in actual");
        }

        if (!expectedToActual.keySet().containsAll(registered) && registered.containsAll(expectedToActual.keySet())) {
            assertEquals(prettyPrint(replaceVarsIn(expected)), prettyPrint(actual), "Expected all value holes to be used but some were not");
        }
    }

    private List<LogicInstruction> replaceVarsIn(List<LogicInstruction> expected) {
        final List<LogicInstruction> result = new ArrayList<>(expected);
        for (int i = 0; i < result.size(); i++) {
            final LogicInstruction instruction = result.get(i);
            final List<String> newArgs = new ArrayList<>(instruction.getArgs());
            for (int j = 0; j < newArgs.size(); j++) {
                newArgs.set(j, expectedToActual.getOrDefault(newArgs.get(j), newArgs.get(j)));
            }

            if (!newArgs.equals(instruction.getArgs())) {
                result.set(i, new LogicInstruction(instruction.getOpcode(), newArgs));
            }
        }
        return result;
    }

    private boolean matchArgs(LogicInstruction left, LogicInstruction right) {
        if (left.getArgs().size() != right.getArgs().size()) {
            return false;
        }

        for (int i = 0; i < left.getArgs().size(); i++) {
            final String a = left.getArgs().get(i);
            final String b = right.getArgs().get(i);
            if (a.startsWith("___")) {
                if (expectedToActual.containsKey(a)) {
                    // we mapped this hole to a value before -- check that we reference the same value again
                    if (expectedToActual.get(a).equals(b)) {
                        continue;
                    }
                } else if (actualToExpected.containsKey(b)) {
                    // we reversed mapped this value before -- check that it still references the same value
                    if (actualToExpected.get(b).equals(a)) {
                        continue;
                    }
                } else {
                    // this is a new mapping
                    expectedToActual.put(a, b);
                    actualToExpected.put(b, a);
                    continue;
                }
            } else {
                if (a.equals(b)) continue;
            }

            return false;
        }

        return expectedToActual.keySet().containsAll(actualToExpected.values()) && actualToExpected.keySet().containsAll(expectedToActual.values());
    }
}
