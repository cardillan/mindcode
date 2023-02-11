package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.instructions.BaseInstructionProcessor;
import info.teksol.mindcode.mindustry.logic.Opcode;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

public class AbstractGeneratorTest extends AbstractAstTest {
    // Static instances; too expensive to be created for each test
    protected static final InstructionProcessor BASE_INSTRUCTION_FACTORY = new BaseInstructionProcessor();

    protected final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
    private final Set<String> registered = new HashSet<>();
    private final Map<String, String> expectedToActual = new TreeMap<>();
    private final Map<String, String> actualToExpected = new TreeMap<>();

    // Instruction factory selection
    private InstructionProcessor instructionProcessor = BASE_INSTRUCTION_FACTORY;

    protected void setInstructionProcessor(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }

    protected InstructionProcessor getInstructionProcessor() {
        return instructionProcessor;
    }

    // Instruction creation
    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, List<String> args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    // Program compilation
    protected List<LogicInstruction> generateAndOptimize(Seq program, CompilerProfile profile, Consumer<String> messageConsumer) {
        return LogicInstructionGenerator.generateAndOptimize(instructionProcessor, program, profile, messageConsumer);
    }

    protected List<LogicInstruction> generateAndOptimize(Seq program) {
        return LogicInstructionGenerator.generateAndOptimize(instructionProcessor, program);
    }

    protected void generateInto(LogicInstructionPipeline pipeline, Seq program) {
        LogicInstructionGenerator.generateInto(instructionProcessor, pipeline, program);
    }

    protected List<LogicInstruction> generateUnoptimized(Seq program) {
        return LogicInstructionGenerator.generateUnoptimized(instructionProcessor, program);
    }

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
                result.set(i, createInstruction(instruction.getOpcode(), newArgs));
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
