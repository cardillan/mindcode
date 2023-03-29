package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.functions.FunctionMapperFactory;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.mindcode.mindustry.optimisation.NullDebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import info.teksol.mindcode.mindustry.optimisation.OptimisationPipeline;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractGeneratorTest extends AbstractAstTest {
    private InstructionProcessor instructionProcessor =
            InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);

    protected final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
    private final Set<String> registered = new HashSet<>();
    private final Map<String, String> expectedToActual = new TreeMap<>();
    private final Map<String, String> actualToExpected = new TreeMap<>();

    // Instruction factory selection
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

    public static List<LogicInstruction> generateAndOptimize(InstructionProcessor instructionProcessor, Seq program, CompilerProfile profile) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        LogicInstructionPipeline pipeline = OptimisationPipeline.createPipelineForProfile(instructionProcessor,
                terminus, profile, new NullDebugPrinter(), s -> {});
        LogicInstructionGenerator generator = new LogicInstructionGenerator(instructionProcessor,
                FunctionMapperFactory.getFunctionMapper(instructionProcessor, s -> {}), pipeline);
        generator.start(program);
        pipeline.flush();
        return terminus.getResult();
    }

    protected List<LogicInstruction> compile(String code) {
        return LogicInstructionLabelResolver.resolve(instructionProcessor,
                generateAndOptimize(instructionProcessor,
                        (Seq) translateToAst(code),
                        CompilerProfile.fullOptimizations()
                )
        );
    }

    protected List<LogicInstruction> generateAndOptimize(Seq program, Optimisation... optimisations) {
        return generateAndOptimize(instructionProcessor, program, new CompilerProfile(optimisations));
    }

    protected List<LogicInstruction> generateAndOptimize(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.fullOptimizations());
    }

    protected List<LogicInstruction> generateUnoptimized(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.noOptimizations());
    }

    protected void generateInto(LogicInstructionPipeline pipeline, Seq program) {
        LogicInstructionGenerator generator = new LogicInstructionGenerator(instructionProcessor,
                FunctionMapperFactory.getFunctionMapper(instructionProcessor, s -> {}), pipeline);
        generator.start(program);
        pipeline.flush();
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
                                    "actual->expected: " + actualToExpected + "\n" +
                                    formatAsCode(actual)
                    );
                }
            }

            assertEquals(prettyPrint(replaceVarsIn(expected)), prettyPrint(actual), "Failed to var instruction at index " + i
                    + formatAsCode(actual));
        }

        if (actual.size() != expected.size()) {
            assertEquals(prettyPrint(replaceVarsIn(expected)), prettyPrint(actual),
                    "Expected to have same size, found " + expected.size() + " in expected " +
                            "vs " + actual.size() + " in actual" + formatAsCode(actual));
        }

        if (!expectedToActual.keySet().containsAll(registered) && registered.containsAll(expectedToActual.keySet())) {
            assertEquals(prettyPrint(replaceVarsIn(expected)), prettyPrint(actual),
                    "Expected all value holes to be used but some were not" + formatAsCode(actual));
        }
    }

    private List<LogicInstruction> replaceVarsIn(List<LogicInstruction> expected) {
        final List<LogicInstruction> result = new ArrayList<>(expected);
        for (int i = 0; i < result.size(); i++) {
            final LogicInstruction instruction = result.get(i);
            final List<String> newArgs = new ArrayList<>(instruction.getArgs());
            newArgs.replaceAll(arg -> expectedToActual.getOrDefault(arg, arg));

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

    private String formatAsCode(List<LogicInstruction> program) {
        StringBuilder str = new StringBuilder();
        if (true) {
            str.append("\nInstructions:");
            for (LogicInstruction ix : program) {
                str.append("\n                        createInstruction(").append(ix.getOpcode().name());
                ix.getArgs().forEach(a -> str.append(", ").append(escape(a)));
                str.append("),").toString();
            }
            str.deleteCharAt(str.length() - 1);
            str.append("\n\n");
            return str.toString();
        } else {
            return "";
        }
    }

    private String escape(String value) {
        if (value.startsWith("__tmp")) {
            return "var(" + value.substring(5) + ")";
        } else if (value.startsWith("__retval")) {
            return "var(" + value.substring(8) + ")";
        } else if (value.startsWith("__label")) {
            return "var(" + (1000 + Integer.parseInt(value.substring(7))) + ")";
        } else {
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    }
}
