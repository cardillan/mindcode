package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.CustomInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.CompilationPhase;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.mindcode.v3.MindcodeCompiler;
import info.teksol.mindcode.v3.compiler.antlr.AbstractParserTest;
import info.teksol.util.ExpectedMessages;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractCodeGeneratorTest extends AbstractParserTest {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.COMPILER;
    }

    protected void compile(ExpectedMessages expectedMessages, InputFiles inputFiles, boolean validate,
            Consumer<MindcodeCompiler> testEvaluator) {
        process(expectedMessages, inputFiles, validate, c -> {
            testEvaluator.accept(c);
            return null;
        });
    }

    protected void assertCompiles(ExpectedMessages expectedMessages, String source, List<LogicInstruction> expected) {
        List<MindcodeMessage> messages = new ArrayList<>();
        expectedMessages.setLogger(messages::add);
        compile(expectedMessages, InputFiles.fromSource(source), true,
                compiler -> evaluateResults(compiler, expected, messages));
    }

    protected void assertCompiles(ExpectedMessages expectedMessages, String source, LogicInstruction... expected) {
        assertCompiles(expectedMessages, source, List.of(expected));
    }

    protected void assertCompiles(String source, List<LogicInstruction> expected) {
        assertCompiles(expectedMessages(), source, expected);
    }

    protected void assertCompiles(String source, LogicInstruction... expected) {
        assertCompiles(expectedMessages(), source, List.of(expected));
    }

    @Override
    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        compile(expectedMessages, InputFiles.fromSource(source), true, c -> {});
    }

    protected void assertCompiles(String source) {
        compile(expectedMessages(), InputFiles.fromSource(source), true, c -> {});
    }

    // EVALUATION MECHANICS

    private void evaluateResults(MindcodeCompiler compiler, List<LogicInstruction> expected, List<MindcodeMessage> messages) {
        List<LogicInstruction> actual = compiler.getInstructions();
        if (actual.size() != expected.size()) {
            assertFailed(makeVarsIn(expected), actual, createDifferentCodeSizeMessage(actual, messages));
            return;
        }

        for (int index = 0; index < actual.size(); index++) {
            final LogicInstruction left = expected.get(index);
            final LogicInstruction right = actual.get(index);
            if (left.getMlogOpcode().equals(right.getMlogOpcode())) {
                if (!matchArgs(left, right)) {
                    assertFailed(makeVarsIn(expected), actual, createUnmatchedArgumentsMessage(actual, messages, index, left, right));
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
            int index, LogicInstruction left, LogicInstruction right) {
        return "Expected\n" + left.toMlog() + "\nbut found\n" + right.toMlog() + "\non row index " + index + "\n" +
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

    protected String var(int id) {
        String key = "___" + id;
        registered.add(key);
        return key;
    }

    protected void assertFailed(List<LogicInstruction> expected, List<LogicInstruction> actual, String message) {
        assertEquals(formatAsCode(expected), formatAsCode(actual), message);
    }

    protected String formatMessages(List<MindcodeMessage> messages) {
        return messages.isEmpty()
                ? ""
                : messages.stream().map(MindcodeMessage::message).collect(
                        Collectors.joining("\n", "\nGenerated messages:\n", ""));
    }

    private boolean matchArgs(LogicInstruction left, LogicInstruction right) {
        if (left.getArgs().size() != right.getArgs().size()) {
            return false;
        }

        for (int i = 0; i < left.getArgs().size(); i++) {
            final String a = left.getArgs().get(i).toMlog();
            final String b = right.getArgs().get(i).toMlog();
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

    private String escape(String value) {
        if (value.startsWith("var(")) {
            return value;
        } else if (value.startsWith("__tmp")) {
            return "var(" + value.substring(5) + ")";
        } else if (value.startsWith("__retval")) {
            return "var(" + value.substring(8) + ")";
        } else if (value.startsWith("__label")) {
            return "var(" + (1000 + Integer.parseInt(value.substring(7))) + ")";
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return "q(" + value.replace("\n", "\\n") + ")";
        } else {
            return q(value.replace("\\", "\\\\").replace("\"", "\\\""));
        }
    }

    protected static String q(String str) {
        return '"' + str + '"';
    }

    // Instruction creation

    protected final CompilerProfile profile = createCompilerProfile();
    protected final InstructionProcessor ip = InstructionProcessorFactory.getInstructionProcessor(ExpectedMessages.throwOnMessage(), profile);

    protected final AstContext mockAstRootContext = AstContext.createRootNode(profile);
    protected final AstContext mockAstContext = mockAstRootContext.createSubcontext(AstSubcontextType.BASIC, 1.0);

    protected static LogicArgument _logic(String str) {
        return new BaseArgument(str);
    }

    protected static List<LogicArgument> _logic(String... arguments) {
        return Arrays.stream(arguments).map(AbstractCodeGeneratorTest::_logic).toList();
    }

    protected static List<LogicArgument> _logic(List<String> arguments) {
        return arguments.stream().map(AbstractCodeGeneratorTest::_logic).toList();
    }

    protected static List<String> _str(List<LogicArgument> arguments) {
        return arguments.stream().map(LogicArgument::toMlog).collect(Collectors.toCollection(ArrayList::new));
    }

    protected final LogicInstruction createInstruction(Opcode opcode) {
        return ip.createInstruction(mockAstContext, opcode);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return ip.createInstruction(mockAstContext, opcode, _logic(args));
    }

    protected final LogicInstruction createInstructionStr(Opcode opcode, List<String> args) {
        return ip.createInstruction(mockAstContext, opcode, _logic(args));
    }

    protected final LogicInstruction createInstruction(Opcode opcode, LogicArgument... args) {
        return ip.createInstruction(mockAstContext, opcode, args);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> args) {
        return ip.createInstruction(mockAstContext, opcode, args);
    }

    protected final LogicInstruction customInstruction(String opcode, String... args) {
        return new CustomInstruction(mockAstContext, false, opcode, _logic(args),null);
    }

    // Common constants for creating instructions
    protected static final Operation     add       = Operation.ADD;
    protected static final Operation     sub       = Operation.SUB;
    protected static final Operation     rand      = Operation.RAND;
    protected static final Operation     div       = Operation.DIV;
    protected static final Operation     floor     = Operation.FLOOR;
    protected static final Operation     idiv      = Operation.IDIV;
    protected static final Operation     mul       = Operation.MUL;
    protected static final LogicNumber   K1000     = LogicNumber.get(1000);
    protected static final LogicNumber   K0001     = LogicNumber.get("0.001", 0.001);
    protected static final LogicNumber   P0        = LogicNumber.ZERO;
    protected static final LogicNumber   P0_5      = LogicNumber.get("0.5", 0.5);
    protected static final LogicNumber   P1        = LogicNumber.ONE;
    protected static final LogicNumber   P2        = LogicNumber.get(2);
    protected static final LogicNumber   P4        = LogicNumber.get(4);
    protected static final LogicNumber   P8        = LogicNumber.get(8);
    protected static final LogicNumber   P9        = LogicNumber.get(9);
    protected static final LogicNumber   P10       = LogicNumber.get(10);
    protected static final LogicNumber   P11       = LogicNumber.get(11);
    protected static final LogicNumber   P255      = LogicNumber.get(255);
    protected static final LogicNumber   N1        = LogicNumber.get(-1);
    protected static final LogicNumber   N9        = LogicNumber.get(-9);
    protected static final LogicNumber   N10       = LogicNumber.get(-10);
    protected static final LogicNumber   N11       = LogicNumber.get(-11);
    protected static final LogicString   message   = LogicString.create("message");
    protected static final LogicLabel    label0    = LogicLabel.symbolic("label0");
    protected static final LogicLabel    label1    = LogicLabel.symbolic("label1");
    protected static final LogicLabel    marker    = LogicLabel.symbolic("marker");
    protected static final LogicLabel    label2    = LogicLabel.symbolic("label2");
    protected static final LogicVariable bank1     = LogicVariable.block("bank1");
    protected static final LogicVariable cell1     = LogicVariable.block("cell1");
    protected static final LogicVariable conveyor1 = LogicVariable.block("conveyor1");
    protected static final LogicVariable vault1    = LogicVariable.block("vault1");
    protected static final LogicVariable unused    = LogicVariable.unusedVariable();
    protected static final LogicBuiltIn  coal      = LogicBuiltIn.create("@coal");
    protected static final LogicBuiltIn  lead      = LogicBuiltIn.create("@lead");
    protected static final LogicBuiltIn  firstItem = LogicBuiltIn.create("@firstItem");
    protected static final LogicBuiltIn  enabled   = LogicBuiltIn.create("@enabled");
    protected static final LogicBuiltIn  time      = LogicBuiltIn.create("@time");
    protected static final LogicBuiltIn  unit      = LogicBuiltIn.create("@unit");
    protected static final LogicBuiltIn  thiz      = LogicBuiltIn.create("@this");
    protected static final LogicBuiltIn  x         = LogicBuiltIn.create("@x");
    protected static final LogicBuiltIn  y         = LogicBuiltIn.create("@y");
    protected static final LogicBuiltIn  thisx     = LogicBuiltIn.create("@thisx");
    protected static final LogicBuiltIn  thisy     = LogicBuiltIn.create("@thisy");
    protected static final LogicBuiltIn  id        = LogicBuiltIn.create("@id");
    protected static final LogicKeyword  color     = LogicKeyword.create("color");
    protected static final LogicKeyword  item      = LogicKeyword.create("item");
    protected static final LogicVariable C         = LogicVariable.global("C", false);
    protected static final LogicVariable a         = LogicVariable.main("a");
    protected static final LogicVariable b         = LogicVariable.main("b");
    protected static final LogicVariable c         = LogicVariable.main("c");
    protected static final LogicVariable d         = LogicVariable.main("d");
    protected static final LogicVariable another   = LogicVariable.main("another");
    protected static final LogicVariable divisor   = LogicVariable.main("divisor");
    protected static final LogicVariable value     = LogicVariable.main("value");
    protected static final LogicVariable var       = LogicVariable.main("var");
    protected static final LogicVariable foo       = LogicVariable.main("foo");
    protected static final LogicVariable result    = LogicVariable.main("result");
    protected static final LogicVariable ast0      = LogicVariable.ast("__ast0");
    protected static final LogicVariable tmp0      = LogicVariable.temporary("__tmp0");
    protected static final LogicVariable tmp1      = LogicVariable.temporary("__tmp1");
    protected static final LogicVariable tmp2      = LogicVariable.temporary("__tmp2");
    protected static final LogicVariable tmp3      = LogicVariable.temporary("__tmp3");
    protected static final LogicVariable fn0retval = LogicVariable.fnRetVal("foo", "__fn0retval");
}
