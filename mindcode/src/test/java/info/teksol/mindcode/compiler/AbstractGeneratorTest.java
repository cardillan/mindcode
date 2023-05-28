package info.teksol.mindcode.compiler;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractGeneratorTest extends AbstractAstTest {

    protected final List<CompilerMessage> messages = new ArrayList<>();
    protected final InstructionProcessor instructionProcessor = createInstructionProcessor(createCompilerProfile());


    protected void assertCompilesTo(CompilerProfile profile, Predicate<LogicInstruction> filter,
            String code, LogicInstruction... instructions) {
        List<LogicInstruction> expected = List.of(instructions);
        List<LogicInstruction> actual = generateInstructions(profile, code).instructions();
        if (filter != null) {
            actual = actual.stream().filter(filter).toList();
        }
        assertLogicInstructionsMatch(expected, actual);
    }

    protected void assertCompilesTo(CompilerProfile profile, String code, LogicInstruction... instructions) {
        assertCompilesTo(profile, null, code, instructions);
    }

    protected void assertCompilesTo(Predicate<LogicInstruction> filter, String code, LogicInstruction... instructions) {
        assertCompilesTo(createCompilerProfile(), filter, code, instructions);
    }

    protected void assertCompilesTo(String code, LogicInstruction... instructions) {
        assertCompilesTo(createCompilerProfile(), null, code, instructions);
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

    // Configuration

    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.V7A;
    }

    protected ProcessorEdition getProcessorEdition() {
        return ProcessorEdition.WORLD_PROCESSOR;
    }

    protected CompilerProfile createCompilerProfile() {
        return CompilerProfile.noOptimizations()
                .setProcessorVersion(getProcessorVersion())
                .setProcessorEdition(getProcessorEdition())
                .setDebugLevel(3);
    }

    protected InstructionProcessor createInstructionProcessor(CompilerProfile profile) {
        return InstructionProcessorFactory.getInstructionProcessor(messages::add, profile);
    }

    // Code generation

    protected Seq generateAstTree(String code) {
        return AstNodeBuilder.generate(parse(code));
    }

    // This class always creates unoptimized code.
    // To test functions of optimizers, use AbstractOptimizerTest subclass.
    protected GeneratorOutput generateInstructions(CompilerProfile profile, String code) {
        Seq program = generateAstTree(code);
        final LogicInstructionGenerator generator = new LogicInstructionGenerator(profile, instructionProcessor, messages::add);
        return generator.generate(program);
    }

    protected GeneratorOutput generateInstructions(String code) {
        return generateInstructions(createCompilerProfile(), code);
    }

    // Instruction creation

    protected final AstContext mockAstContext = AstContext.createRootNode();

    protected final LogicInstruction createInstruction(Opcode opcode) {
        return instructionProcessor.createInstruction(mockAstContext, opcode);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(mockAstContext, opcode, _logic(args));
    }

    protected final LogicInstruction createInstructionStr(Opcode opcode, List<String> args) {
        return instructionProcessor.createInstruction(mockAstContext, opcode, _logic(args));
    }

    protected final LogicInstruction createInstruction(Opcode opcode, LogicArgument... args) {
        return instructionProcessor.createInstruction(mockAstContext, opcode, args);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> args) {
        return instructionProcessor.createInstruction(mockAstContext, opcode, args);
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

    protected String formatMessages() {
        return messages.isEmpty() ? "" : "\nGenerated messages:\n"
                + messages.stream().map(CompilerMessage::message).collect(Collectors.joining("\n")) ;
    }

    protected String createDifferentCodeSizeMessage(List<LogicInstruction> actual) {
        return "Generated code has unexpected number of instructions\n" +
                formatMessages() + "\n" +
                formatAsCode(actual);
    }

    protected String createDifferentOpcodeMessage(List<LogicInstruction> actual, int index) {
        return "Generated code has different instruction opcodes at index " + index + ":\n" +
                formatMessages() + "\n" +
                formatAsCode(actual);
    }

    protected String createUnmatchedArgumentsMessage(List<LogicInstruction> actual, int index,
            LogicInstruction left, LogicInstruction right) {
        return "Expected\n" + left + "\nbut found\n" + right + "\non row index " + index + "\n" +
                "expected->actual: " + expectedToActual + "\n" +
                "actual->expected: " + actualToExpected + "\n" +
                formatMessages() + "\n" +
                formatAsCode(actual);
    }

    protected String createUnusedVarsMessage(List<LogicInstruction> actual, int index,
            LogicInstruction left, LogicInstruction right) {
        return "Expected\n" + left + "\nbut found\n" + right + "\non row index " + index + "\n" +
                "expected->actual: " + expectedToActual + "\n" +
                "actual->expected: " + actualToExpected + "\n" +
                formatMessages() + "\n" +
                formatAsCode(actual);
    }

    protected void assertFailed(List<LogicInstruction> expected, List<LogicInstruction> actual, String message) {
        assertEquals(formatAsCode(expected), formatAsCode(actual), message);
    }

    // TODO Investigate whether it would be feasible to first build var maps and then compare the code.
    //      As it is now, if a difference is found early on, subsequent vars are not properly mapped.
    protected void assertLogicInstructionsMatch(List<LogicInstruction> expected, List<LogicInstruction> actual) {
        if (actual.size() != expected.size()) {
            assertFailed(makeVarsIn(expected), actual, createDifferentCodeSizeMessage(actual));
            return;
        }

        for (int index = 0; index < actual.size(); index++) {
            final LogicInstruction left = expected.get(index);
            final LogicInstruction right = actual.get(index);
            if (left.getOpcode().equals(right.getOpcode())) {
                if (!matchArgs(left, right)) {
                    assertFailed(replaceVarsIn(expected), actual, createUnmatchedArgumentsMessage(actual, index, left, right));
                    return;
                }
            } else {
                assertFailed(replaceVarsIn(expected), actual, createDifferentOpcodeMessage(actual, index));
                return;
            }
        }

        if (!expectedToActual.keySet().containsAll(registered) && registered.containsAll(expectedToActual.keySet())) {
            // This is not a failed test, this is a bug in test code
            throw new RuntimeException("Expected all value holes to be used but some were not.");
        }
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
    protected static final Operation     div       = Operation.DIV;                           
    protected static final Operation     floor     = Operation.FLOOR;                         
    protected static final Operation     idiv      = Operation.IDIV;                          
    protected static final Operation     mul       = Operation.MUL;                           
    protected static final LogicNumber   K1000     = LogicNumber.get(1000);                   
    protected static final LogicNumber   K0001     = LogicNumber.get("0.001", 0.001);
    protected static final LogicNumber   K0        = LogicNumber.get(0);                      
    protected static final LogicNumber   K1        = LogicNumber.get(1);                      
    protected static final LogicNumber   K255      = LogicNumber.get(255);                    
    protected static final LogicString   message   = LogicString.create("message");     
    protected static final LogicLabel    label0    = LogicLabel.symbolic("label0");      
    protected static final LogicLabel    label1    = LogicLabel.symbolic("label1");      
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
    protected static final LogicKeyword  color     = LogicKeyword.create("color");       
    protected static final LogicVariable C         = LogicVariable.global("C");           
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
    protected static final LogicVariable fn0retval = LogicVariable.fnRetVal("__fn0retval");
    protected static final LogicVariable retval0   = LogicVariable.retval("__retval0");   
}
