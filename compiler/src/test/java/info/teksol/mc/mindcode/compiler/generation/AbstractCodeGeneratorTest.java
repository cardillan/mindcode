package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class AbstractCodeGeneratorTest extends AbstractTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.COMPILER;
    }

    // INPUT FILES

    protected void compile(ExpectedMessages expectedMessages, InputFiles inputFiles, Consumer<MindcodeCompiler> testEvaluator) {
        process(expectedMessages, inputFiles, null, c -> {
            testEvaluator.accept(c);
            return null;
        });
    }

    protected void compile(ExpectedMessages expectedMessages, String source, Consumer<MindcodeCompiler> testEvaluator) {
        compile(expectedMessages, createInputFiles(source), testEvaluator);
    }

    protected void assertCompilesTo(ExpectedMessages expectedMessages, String source, Predicate<LogicInstruction> filter,
            LogicInstruction... instruction) {
        compile(expectedMessages, createInputFiles(source),
                compiler -> evaluateResults(compiler, filter, List.of(instruction)));
    }

    protected void assertCompilesTo(String source, Predicate<LogicInstruction> filter, LogicInstruction... instruction) {
        assertCompilesTo(expectedMessages(), source, filter, instruction);
    }

    protected void assertCompilesTo(ExpectedMessages expectedMessages, String source, LogicInstruction... expected) {
        compile(expectedMessages, createInputFiles(source),compiler -> evaluateResults(compiler, List.of(expected)));
    }

    protected void assertCompilesTo(String source, LogicInstruction... expected) {
        assertCompilesTo(expectedMessages(), source, expected);
    }

    protected void assertCompiles(String source) {
        assertGeneratesMessages(expectedMessages(), source);
    }

    // EVALUATION MECHANICS

    private List<LogicInstruction> removeEnd(List<LogicInstruction> instructions) {
        int index = CollectionUtils.lastIndexOf(instructions, ix -> ix.getOpcode() != Opcode.END);
        return index == -1 || index == instructions.size() - 1 ? instructions : instructions.subList(0, index + 1);
    }

    private void evaluateResults(MindcodeCompiler compiler, List<LogicInstruction> expected) {
        evaluateResults(removeEnd(expected), removeEnd(compiler.getInstructions()), compiler.getMessages());
    }

    private void evaluateResults(MindcodeCompiler compiler, Predicate<LogicInstruction> filter, List<LogicInstruction> expected) {
        evaluateResults(
                removeEnd(expected),
                removeEnd(compiler.getInstructions().stream().filter(filter).toList()),
                compiler.getMessages());
    }

    protected void evaluateResults(List<LogicInstruction> expected, List<LogicInstruction> actual, List<MindcodeMessage> messages) {
        if (actual.size() != expected.size()) {
            assertFailed(makeVarsIn(expected), actual, createDifferentCodeSizeMessage(actual, messages));
            return;
        }

        for (int index = 0; index < actual.size(); index++) {
            final LogicInstruction expectedIx = expected.get(index);
            final LogicInstruction actualIx = actual.get(index);
            if (expectedIx.getMlogOpcode().equals(actualIx.getMlogOpcode())) {
                if (!matchArgs(expectedIx, actualIx)) {
                    assertFailed(makeVarsIn(expected), actual, createUnmatchedArgumentsMessage(actual, messages, index, expectedIx, actualIx));
                    return;
                }
            } else {
                assertFailed(makeVarsIn(expected), actual, createDifferentOpcodeMessage(actual, messages, index));
                return;
            }
        }

        if (!expectedToActual.keySet().containsAll(registered) && registered.containsAll(expectedToActual.keySet())) {
            // This is not a failed test, this is a bug in test code
            throw new RuntimeException("Expected all value holes to be used but some were not.");
        }
    }

    protected String createDifferentCodeSizeMessage(List<LogicInstruction> actual, List<MindcodeMessage> messages) {
        return "Generated code has unexpected number of instructions\n" +
               formatMessages(messages) + "\n" +
               formatAsCode(actual);
    }

    protected String createUnmatchedArgumentsMessage(List<LogicInstruction> actual, List<MindcodeMessage> messages,
            int index, LogicInstruction expectedIx, LogicInstruction actualIx) {
        return "Expected\n" + expectedIx.toMlog() + "\nbut found\n" + actualIx.toMlog() + "\non row index " + index + "\n" +
               "expected->actual: " + expectedToActual + "\n" +
               "actual->expected: " + actualToExpected + "\n" +
               formatMessages(messages) + "\n" +
               formatAsCode(actual);
    }

    protected String createUnusedVarsMessage(List<LogicInstruction> actual, List<MindcodeMessage> messages,
            int index, LogicInstruction left, LogicInstruction right) {
        return "Expected\n" + left.toMlog() + "\nbut found\n" + right.toMlog() + "\non row index " + index + "\n" +
               "expected->actual: " + expectedToActual + "\n" +
               "actual->expected: " + actualToExpected + "\n" +
               formatMessages(messages) + "\n" +
               formatAsCode(actual);
    }

    protected String createDifferentOpcodeMessage(List<LogicInstruction> actual, List<MindcodeMessage> messages, int index) {
        return "Generated code has different instruction opcodes at index " + index + ":\n" +
               formatMessages(messages) + "\n" +
               formatAsCode(actual);
    }

    private List<LogicInstruction> makeVarsIn(List<LogicInstruction> expected) {
        final List<LogicInstruction> result = new ArrayList<>(expected);
        for (int i = 0; i < result.size(); i++) {
            final LogicInstruction instruction = result.get(i);
            final List<String> newArgs = _str(instruction.getArgs());
            if (newArgs.stream().anyMatch(s -> s.startsWith("___"))) {
                newArgs.replaceAll(arg -> arg.startsWith("___") ? "var(" + arg.substring(3) + ")" : arg);
                result.set(i, createInstructionStr(instruction.getOpcode(), newArgs));
            }
        }
        return result;
    }

    private List<LogicInstruction> replaceVarsIn(List<LogicInstruction> expected) {
        final List<LogicInstruction> result = new ArrayList<>(expected);
        for (int i = 0; i < result.size(); i++) {
            final LogicInstruction instruction = result.get(i);
            final List<String> newArgs = _str(instruction.getArgs());
            newArgs.replaceAll(arg -> expectedToActual.getOrDefault(arg, arg));

            if (!newArgs.equals(_str(instruction.getArgs()))) {
                result.set(i, createInstructionStr(instruction.getOpcode(), newArgs));
            }
        }
        return result;
    }


    private final Set<String> registered = new HashSet<>();
    private final Map<String, String> expectedToActual = new LinkedHashMap<>();
    private final Map<String, String> actualToExpected = new LinkedHashMap<>();

    protected void assertFailed(List<LogicInstruction> expected, List<LogicInstruction> actual, String message) {
        assertEquals(formatAsCode(expected), formatAsCode(actual), message);
    }

    protected String formatMessages(List<MindcodeMessage> messages) {
        return messages.isEmpty()
                ? ""
                : messages.stream().map(MindcodeMessage::message).collect(
                        Collectors.joining("\n", "\nGenerated messages:\n", ""));
    }

    private static final Pattern variablePattern = Pattern.compile("^[.:*]?[a-zA-Z_][^*]*");

    private boolean evaluateDirectly(String value) {
        // Everything except variables needs to be evaluated directly
        // Variables can be substituted, EXCEPT indexed variables
        return !variablePattern.matcher(value).matches();
    }


    private boolean matchArgs(LogicInstruction expected, LogicInstruction actual) {
        if (expected.getArgs().size() != actual.getArgs().size()) {
            return false;
        }

        for (int i = 0; i < expected.getArgs().size(); i++) {
            LogicArgument expectedArg = expected.getArgs().get(i);
            LogicArgument actualArg = actual.getArgs().get(i);

            final String a = expectedArg.toMlog();
            final String b = actualArg.toMlog();

            if (evaluateDirectly(a) || evaluateDirectly(b)) {
                return a.equals(b);
            } else if (expectedToActual.containsKey(a)) {
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

            return false;
        }

        return expectedToActual.keySet().containsAll(actualToExpected.values()) && actualToExpected.keySet().containsAll(expectedToActual.values());
    }

    private String formatAsCode(List<LogicInstruction> actual) {
        StringBuilder str = new StringBuilder();
        str.append("\nInstructions:");
        for (LogicInstruction ix : actual) {
            if (ix.getOpcode() == Opcode.CUSTOM) {
                str.append("\n                customInstruction(\"").append(ix.getMlogOpcode()).append('"');
            } else {
                str.append("\n                createInstruction(").append(ix.getOpcode().name());
            }
            ix.getArgs().forEach(a -> str.append(", ").append(escape(a.toMlog())));
            str.append("),");
        }
        str.deleteCharAt(str.length() - 1);
        str.append("\n\n");
        return str.toString();
    }

    protected String var(int id) {
        String key = "___" + id;
        registered.add(key);
        return key;
    }

    protected String tmp(int id) {
        String key = "*tmp" + id;
        registered.add(key);
        return key;
    }

    protected String label(int id) {
        String key = "*label" + id;
        registered.add(key);
        return key;
    }

    private String escape(String value) {
        if (value.startsWith("tmp(") || value.startsWith("label(")) {
            return value;
        } else if (value.startsWith("*tmp")) {
            return "tmp(" + value.substring(4) + ")";
        } else if (value.startsWith("*label")) {
            return "label(" + value.substring(6) + ")";
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return "q(" + value.replace("\n", "\\n") + ")";
        } else {
            return q(value.replace("\\", "\\\\").replace("\"", "\\\""));
        }
    }
}
