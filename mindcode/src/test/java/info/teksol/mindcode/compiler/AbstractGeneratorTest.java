package info.teksol.mindcode.compiler;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationPipeline;
import info.teksol.mindcode.logic.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractGeneratorTest extends AbstractAstTest {
    protected final List<CompilerMessage> messages = new ArrayList<>();

    protected InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(
            messages::add, ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);

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

    // Instruction creation
    protected final LogicInstruction createInstruction(Opcode opcode) {
        return instructionProcessor.createInstruction(opcode);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, _logic(args));
    }

    protected final LogicInstruction createInstructionStr(Opcode opcode, List<String> args) {
        return instructionProcessor.createInstruction(opcode, _logic(args));
    }

    protected final LogicInstruction createInstruction(Opcode opcode, LogicArgument... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> args) {
        return instructionProcessor.createInstruction(opcode, args);
    }


    public static List<LogicInstruction> generateAndOptimize(InstructionProcessor instructionProcessor, Seq program, CompilerProfile profile) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineForProfile(instructionProcessor,
                terminus, profile, new NullDebugPrinter(), s -> {});
        LogicInstructionGenerator generator = new LogicInstructionGenerator(CompilerProfile.noOptimizations(),
                instructionProcessor, FunctionMapperFactory.getFunctionMapper(instructionProcessor, s -> {}), pipeline);
        generator.start(program);
        pipeline.flush();
        return terminus.getResult();
    }

    protected CompilerProfile getCompilerProfile() {
        return CompilerProfile.fullOptimizations();
    }

    protected List<LogicInstruction> compile(String code) {
        return LogicInstructionLabelResolver.resolve(instructionProcessor,
                generateAndOptimize(instructionProcessor, (Seq) translateToAst(code), getCompilerProfile())
        );
    }

    protected List<LogicInstruction> generateAndOptimize(Seq program, Optimization... optimizations) {
        return generateAndOptimize(instructionProcessor, program, new CompilerProfile(optimizations));
    }

    protected List<LogicInstruction> generateAndOptimize(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.fullOptimizations());
    }

    protected List<LogicInstruction> generateUnoptimized(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.noOptimizations());
    }

    protected void generateInto(LogicInstructionPipeline pipeline, Seq program) {
        LogicInstructionGenerator generator = new LogicInstructionGenerator(CompilerProfile.noOptimizations(),
                instructionProcessor, FunctionMapperFactory.getFunctionMapper(instructionProcessor, s -> {}), pipeline);
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

    private String formatAsCode(List<LogicInstruction> program) {
        StringBuilder str = new StringBuilder();
        if (true) {
            str.append("\nInstructions:");
            for (LogicInstruction ix : program) {
                str.append("\n                        createInstruction(").append(ix.getOpcode().name());
                ix.getArgs().forEach(a -> str.append(", ").append(escape(a.toMlog())));
                str.append("),");
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
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return "q(" + value + ")";
        } else {
            return q(value.replace("\\", "\\\\").replace("\"", "\\\""));
        }
    }

    // Common constants for creating instructions
    protected static final Operation     div        = Operation.DIV;
    protected static final Operation     floor      = Operation.FLOOR;
    protected static final Operation     idiv       = Operation.IDIV;
    protected static final Operation     mul        = Operation.MUL;

    protected static final LogicNumber   K1000      = LogicNumber.get(1000);
    protected static final LogicNumber   K0001      = LogicNumber.get("0.001", 0.001);
    protected static final LogicNumber   K0         = LogicNumber.get(0);
    protected static final LogicNumber   K1         = LogicNumber.get(1);
    protected static final LogicNumber   K255       = LogicNumber.get(255);
    protected static final LogicString   message    = LogicString.create("message");

    protected static final LogicLabel    label0     = LogicLabel.symbolic("label0");
    protected static final LogicLabel    label1     = LogicLabel.symbolic("label1");
    protected static final LogicLabel    label2     = LogicLabel.symbolic("label2");

    protected static final LogicVariable bank1      = LogicVariable.block("bank1");
    protected static final LogicVariable cell1      = LogicVariable.block("cell1");
    protected static final LogicVariable conveyor1  = LogicVariable.block("conveyor1");
    protected static final LogicVariable vault1     = LogicVariable.block("vault1");

    protected static final LogicBuiltIn  coal       = LogicBuiltIn.create("coal");
    protected static final LogicBuiltIn  lead       = LogicBuiltIn.create("lead");
    protected static final LogicBuiltIn  firstItem  = LogicBuiltIn.create("firstItem");
    protected static final LogicBuiltIn  enabled    = LogicBuiltIn.create("enabled");
    protected static final LogicBuiltIn  time       = LogicBuiltIn.create("time");
    protected static final LogicBuiltIn  unit       = LogicBuiltIn.create("unit");

    protected static final LogicKeyword  color      = LogicKeyword.create("color");

    protected static final LogicVariable C          = LogicVariable.global("C");

    protected static final LogicVariable a          = LogicVariable.main("a");
    protected static final LogicVariable b          = LogicVariable.main("b");
    protected static final LogicVariable c          = LogicVariable.main("c");
    protected static final LogicVariable d          = LogicVariable.main("d");
    protected static final LogicVariable another    = LogicVariable.main("another");
    protected static final LogicVariable divisor    = LogicVariable.main("divisor");
    protected static final LogicVariable value      = LogicVariable.main("value");
    protected static final LogicVariable var        = LogicVariable.main("var");

    protected static final LogicVariable foo        = LogicVariable.main("foo");
    protected static final LogicVariable result     = LogicVariable.main("result");

    protected static final LogicVariable ast0       = LogicVariable.ast("__ast0");

    protected static final LogicVariable tmp0       = LogicVariable.temporary("__tmp0");
    protected static final LogicVariable tmp1       = LogicVariable.temporary("__tmp1");

    protected static final LogicVariable fn0retval  = LogicVariable.fnRetVal("__fn0retval");
    protected static final LogicVariable retval0    = LogicVariable.retval("__retval0");
}
