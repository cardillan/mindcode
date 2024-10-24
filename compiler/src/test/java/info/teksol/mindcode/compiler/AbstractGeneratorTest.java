package info.teksol.mindcode.compiler;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractGeneratorTest extends AbstractAstTest {

    // TODO Separate compiler messages from emulator messages
    protected class TestCompiler {
        private final List<MindcodeMessage> messages = new ArrayList<>();
        private final List<MindcodeMessage> readOnlyMessages = Collections.unmodifiableList(messages);
        public final CompilerProfile profile;
        public final InstructionProcessor processor;
        public final LogicInstructionGenerator generator;
        public AstContext rootContext;

        public TestCompiler(CompilerProfile profile) {
            this.profile = profile;
            processor = createInstructionProcessor(profile, this::addMessage);
            generator = new LogicInstructionGenerator(profile, processor, this::addMessage);
        }

        public void addMessage(MindcodeMessage message) {
            messages.add(message);
        }

        public List<MindcodeMessage> getMessages() {
            return readOnlyMessages;
        }

        public List<MindcodeMessage> getErrorsAndWarnings() {
            return messages.stream().filter(MindcodeMessage::isErrorOrWarning).toList();
        }

        public AstContext getRootContext() {
            return rootContext;
        }

        public void setRootContext(AstContext rootContext) {
            this.rootContext = rootContext;
        }

        public TestCompiler() {
            this(createCompilerProfile());
        }
    }

    protected TestCompiler createTestCompiler() {
        return new TestCompiler();
    }

    protected TestCompiler createTestCompiler(CompilerProfile profile) {
        return new TestCompiler(profile);
    }

    protected Predicate<String> ignore(String... values) {
        List<String> list = Arrays.stream(values).map(String::trim).toList();
        return s -> list.contains(s.trim());
    }

    protected Predicate<String> ignoreRegex(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return s -> pattern.matcher(s.trim()).matches();
    }

    protected void assertCompilesTo(TestCompiler compiler, Predicate<LogicInstruction> filter,
            ExpectedMessages expectedMessages, String code, LogicInstruction... instructions) {
        List<LogicInstruction> expected = List.of(instructions);
        List<LogicInstruction> actual = generateInstructionsNoMsgValidation(compiler, code).instructions();
        if (filter != null) {
            actual = actual.stream().filter(filter).toList();
        }
        assertMessagesAndLogicInstructionsMatch(compiler, expected, actual, expectedMessages);
    }

    protected void assertCompilesTo(TestCompiler compiler, ExpectedMessages expectedMessages, String code,
            LogicInstruction... instructions) {
        assertCompilesTo(compiler, null, expectedMessages, code, instructions);
    }

    protected void assertCompilesTo(TestCompiler compiler, String code,
            ExpectedMessages expectedMessages, LogicInstruction... instructions) {
        assertCompilesTo(compiler, null, expectedMessages, code, instructions);
    }

    protected void assertCompilesTo(Predicate<LogicInstruction> filter, ExpectedMessages expectedMessages,
            String code, LogicInstruction... instructions) {
        assertCompilesTo(createTestCompiler(), filter, expectedMessages, code, instructions);
    }

    protected void assertCompilesTo(ExpectedMessages expectedMessages, String code, LogicInstruction... instructions) {
        assertCompilesTo(createTestCompiler(), null, expectedMessages, code, instructions);
    }

    protected void assertCompilesTo(TestCompiler compiler, Predicate<LogicInstruction> filter,
            String code, LogicInstruction... instructions) {
        assertCompilesTo(compiler, filter, ExpectedMessages.none(), code, instructions);
    }

    protected void assertCompilesTo(TestCompiler compiler, String code, LogicInstruction... instructions) {
        assertCompilesTo(compiler, null, ExpectedMessages.none(), code, instructions);
    }

    protected void assertCompilesTo(Predicate<LogicInstruction> filter, String code, LogicInstruction... instructions) {
        assertCompilesTo(createTestCompiler(), filter, ExpectedMessages.none(), code, instructions);
    }

    protected void assertCompilesTo(String code, LogicInstruction... instructions) {
        assertCompilesTo(createTestCompiler(), null, ExpectedMessages.none(), code, instructions);
    }

    protected void assertGeneratesMessages(TestCompiler compiler, ExpectedMessages expectedMessages, String code) {
        assertCompilesTo(compiler, ix -> false, expectedMessages, code);
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String code) {
        assertGeneratesMessages(createTestCompiler(), expectedMessages, code);
    }

    // General utility

    protected static String q(String str) {
        return '"' + str + '"';
    }

    protected static LogicArgument _logic(String str) {
        return new BaseArgument(str);
    }

    protected static List<LogicArgument> _logic(String... arguments) {
        return Arrays.stream(arguments).map(AbstractGeneratorTest::_logic).toList();
    }

    protected static List<LogicArgument> _logic(List<String> arguments) {
        return arguments.stream().map(AbstractGeneratorTest::_logic).toList();
    }

    protected static List<String> _str(List<LogicArgument> arguments) {
        return arguments.stream().map(LogicArgument::toMlog).collect(Collectors.toCollection(ArrayList::new));
    }

    protected String extractWarnings(List<MindcodeMessage> messages) {
        return messages.stream()
                .filter(MindcodeMessage::isWarning)
                .map(MindcodeMessage::message)
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    // Configuration

    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.MAX;
    }

    protected ProcessorEdition getProcessorEdition() {
        return ProcessorEdition.WORLD_PROCESSOR;
    }

    protected CompilerProfile createCompilerProfile() {
        return CompilerProfile.noOptimizations(false)
                .setProcessorVersion(getProcessorVersion())
                .setProcessorEdition(getProcessorEdition())
                .setDebugLevel(3);
    }

    protected InstructionProcessor createInstructionProcessor(CompilerProfile profile, Consumer<MindcodeMessage> messageConsumer) {
        return InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);
    }

    // Code generation

    protected Seq generateAstTree(String code) {
        return AstNodeBuilder.generate(InputFile.createSourceFile(code),
                ExpectedMessages.none(),
                parse(code));
    }

    // This class always creates unoptimized code.
    // To test functions of optimizers, use AbstractOptimizerTest subclass.
    // DOES NOT VALIDATE MESSAGES
    protected GeneratorOutput generateInstructionsNoMsgValidation(TestCompiler compiler, String code) {
        long parse = System.nanoTime();
        Seq program = generateAstTree(code);
        compiler.addMessage(new TimingMessage("Parse", ((System.nanoTime() - parse) / 1_000_000L)));

        long compile = System.nanoTime();
        GeneratorOutput output = compiler.generator.generate(program);
        compiler.addMessage(new TimingMessage("Compile", ((System.nanoTime() - compile) / 1_000_000L)));
        compiler.setRootContext(output.rootAstContext());

        return output;
    }

    protected GeneratorOutput generateInstructions(TestCompiler compiler, String code) {
        GeneratorOutput generatorOutput = generateInstructionsNoMsgValidation(compiler, code);
        ExpectedMessages.none(true).validate(compiler.getErrorsAndWarnings());
        return generatorOutput;
    }

    protected GeneratorOutput generateInstructions(String code) {
        return generateInstructions(createTestCompiler(), code);
    }

    // Instruction creation

    protected final CompilerProfile profile = createCompilerProfile();
    protected final InstructionProcessor ip = createInstructionProcessor(profile, ExpectedMessages.none(true));

    protected final AstContext mockAstRootContext = AstContext.createRootNode(profile);
    protected final AstContext mockAstContext = mockAstRootContext.createSubcontext(AstSubcontextType.BASIC, 1.0);

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

    // Test evaluation

    private final Set<String> registered = new HashSet<>();
    private final Map<String, String> expectedToActual = new LinkedHashMap<>();
    private final Map<String, String> actualToExpected = new LinkedHashMap<>();

    protected String var(int id) {
        String key = "___" + id;
        registered.add(key);
        return key;
    }

    protected String formatMessages(TestCompiler compiler) {
        return compiler.getMessages().isEmpty() ? "" : "\nGenerated messages:\n"
                + compiler.getMessages().stream().map(MindcodeMessage::message).collect(Collectors.joining("\n")) ;
    }

    protected String createDifferentCodeSizeMessage(TestCompiler compiler, List<LogicInstruction> actual) {
        return "Generated code has unexpected number of instructions\n" +
                formatMessages(compiler) + "\n" +
                formatAsCode(actual);
    }

    protected String createDifferentOpcodeMessage(TestCompiler compiler, List<LogicInstruction> actual, int index) {
        return "Generated code has different instruction opcodes at index " + index + ":\n" +
                formatMessages(compiler) + "\n" +
                formatAsCode(actual);
    }

    protected String createUnmatchedArgumentsMessage(TestCompiler compiler, List<LogicInstruction> actual, int index,
            LogicInstruction left, LogicInstruction right) {
        return "Expected\n" + left + "\nbut found\n" + right + "\non row index " + index + "\n" +
                "expected->actual: " + expectedToActual + "\n" +
                "actual->expected: " + actualToExpected + "\n" +
                formatMessages(compiler) + "\n" +
                formatAsCode(actual);
    }

    protected String createUnusedVarsMessage(TestCompiler compiler, List<LogicInstruction> actual, int index,
            LogicInstruction left, LogicInstruction right) {
        return "Expected\n" + left + "\nbut found\n" + right + "\non row index " + index + "\n" +
                "expected->actual: " + expectedToActual + "\n" +
                "actual->expected: " + actualToExpected + "\n" +
                formatMessages(compiler) + "\n" +
                formatAsCode(actual);
    }

    protected void assertFailed(List<LogicInstruction> expected, List<LogicInstruction> actual, String message) {
        assertEquals(formatAsCode(expected), formatAsCode(actual), message);
    }

    // TODO Investigate whether it would be feasible to first build var maps and then compare the code.
    //      As it is now, if a difference is found early on, subsequent vars are not properly mapped.
    private void assertLogicInstructionsMatch0(TestCompiler compiler, List<LogicInstruction> expected, List<LogicInstruction> actual) {
        if (actual.size() != expected.size()) {
            assertFailed(makeVarsIn(expected), actual, createDifferentCodeSizeMessage(compiler, actual));
            return;
        }

        for (int index = 0; index < actual.size(); index++) {
            final LogicInstruction left = expected.get(index);
            final LogicInstruction right = actual.get(index);
            if (left.getOpcode().equals(right.getOpcode())) {
                if (!matchArgs(left, right)) {
                    assertFailed(makeVarsIn(expected), actual, createUnmatchedArgumentsMessage(compiler, actual, index, left, right));
                    return;
                }
            } else {
                assertFailed(makeVarsIn(expected), actual, createDifferentOpcodeMessage(compiler, actual, index));
                return;
            }
        }

        if (!expectedToActual.keySet().containsAll(registered) && registered.containsAll(expectedToActual.keySet())) {
            // This is not a failed test, this is a bug in test code
            throw new RuntimeException("Expected all value holes to be used but some were not.");
        }
    }

    protected void assertNoUnexpectedMessages(TestCompiler compiler, ExpectedMessages expectedMessages) {
        expectedMessages.validate(compiler.getErrorsAndWarnings());
    }

    protected void assertMessagesAndLogicInstructionsMatch(TestCompiler compiler, List<LogicInstruction> expected,
            List<LogicInstruction> actual, ExpectedMessages expectedMessages) {
        assertAll(
                () -> assertLogicInstructionsMatch0(compiler, expected, actual),
                () -> expectedMessages.validate(compiler.getErrorsAndWarnings())
        );
    }

    protected void assertLogicInstructionsMatch(TestCompiler compiler, List<LogicInstruction> expected,
            List<LogicInstruction> actual) {
        assertMessagesAndLogicInstructionsMatch(compiler, expected, actual, ExpectedMessages.none());
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

    private String formatAsCode(List<LogicInstruction> program) {
        StringBuilder str = new StringBuilder();
        str.append("\nInstructions:");
        for (LogicInstruction ix : program) {
            str.append("\n                createInstruction(").append(ix.getOpcode().name());
            ix.getArgs().forEach(a -> str.append(", ").append(escape(a.toMlog())));
            str.append("),");
        }
        str.deleteCharAt(str.length() - 1);
        str.append("\n\n");
        return str.toString();
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
    protected static final LogicBuiltIn  coal      = LogicBuiltIn.create("coal");        
    protected static final LogicBuiltIn  lead      = LogicBuiltIn.create("lead");        
    protected static final LogicBuiltIn  firstItem = LogicBuiltIn.create("firstItem");   
    protected static final LogicBuiltIn  enabled   = LogicBuiltIn.create("enabled");     
    protected static final LogicBuiltIn  time      = LogicBuiltIn.create("time");        
    protected static final LogicBuiltIn  unit      = LogicBuiltIn.create("unit");        
    protected static final LogicBuiltIn  thiz      = LogicBuiltIn.create("this");
    protected static final LogicBuiltIn  x         = LogicBuiltIn.create("x");
    protected static final LogicBuiltIn  y         = LogicBuiltIn.create("y");
    protected static final LogicBuiltIn  thisx     = LogicBuiltIn.create("thisx");
    protected static final LogicBuiltIn  thisy     = LogicBuiltIn.create("thisy");
    protected static final LogicBuiltIn  id        = LogicBuiltIn.create("id");
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
