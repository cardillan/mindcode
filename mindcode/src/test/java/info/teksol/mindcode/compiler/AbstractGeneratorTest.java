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

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractGeneratorTest extends AbstractAstTest {
    protected List<CompilerMessage> messages = new ArrayList<>();

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
    protected static Operation     div        = Operation.DIV;
    protected static Operation     floor      = Operation.FLOOR;
    protected static Operation     idiv       = Operation.IDIV;
    protected static Operation     mul        = Operation.MUL;

    protected static LogicNumber   K1000      = LogicNumber.get(1000);
    protected static LogicNumber   K0001      = LogicNumber.get("0.001", 0.001);
    protected static LogicNumber   K0         = LogicNumber.get(0);
    protected static LogicNumber   K1         = LogicNumber.get(1);
    protected static LogicNumber   K255       = LogicNumber.get(255);
    protected static LogicString   message    = LogicString.create("message");

    protected static LogicLabel    label0     = LogicLabel.symbolic("label0");
    protected static LogicLabel    label1     = LogicLabel.symbolic("label1");
    protected static LogicLabel    label2     = LogicLabel.symbolic("label2");

    protected static LogicVariable bank1      = LogicVariable.block("bank1");
    protected static LogicVariable cell1      = LogicVariable.block("cell1");
    protected static LogicVariable conveyor1  = LogicVariable.block("conveyor1");
    protected static LogicVariable vault1     = LogicVariable.block("vault1");

    protected static LogicBuiltIn  coal       = LogicBuiltIn.create("coal");
    protected static LogicBuiltIn  lead       = LogicBuiltIn.create("lead");
    protected static LogicBuiltIn  firstItem  = LogicBuiltIn.create("firstItem");
    protected static LogicBuiltIn  enabled    = LogicBuiltIn.create("enabled");
    protected static LogicBuiltIn  time       = LogicBuiltIn.create("time");
    protected static LogicBuiltIn  unit       = LogicBuiltIn.create("unit");

    protected static LogicKeyword  color      = LogicKeyword.create("color");

    protected static LogicVariable C          = LogicVariable.global("C");

    protected static LogicVariable a          = LogicVariable.main("a");
    protected static LogicVariable b          = LogicVariable.main("b");
    protected static LogicVariable c          = LogicVariable.main("c");
    protected static LogicVariable d          = LogicVariable.main("d");
    protected static LogicVariable another    = LogicVariable.main("another");
    protected static LogicVariable divisor    = LogicVariable.main("divisor");
    protected static LogicVariable value      = LogicVariable.main("value");
    protected static LogicVariable var        = LogicVariable.main("var");

    protected static LogicVariable foo        = LogicVariable.main("foo");
    protected static LogicVariable result     = LogicVariable.main("result");

    protected static LogicVariable ast0       = LogicVariable.ast("__ast0");

    protected static LogicVariable tmp0       = LogicVariable.temporary("__tmp0");
    protected static LogicVariable tmp1       = LogicVariable.temporary("__tmp1");

    protected static LogicVariable fn0retval  = LogicVariable.fnRetVal("__fn0retval");
    protected static LogicVariable retval0    = LogicVariable.retval("__retval0");
}
