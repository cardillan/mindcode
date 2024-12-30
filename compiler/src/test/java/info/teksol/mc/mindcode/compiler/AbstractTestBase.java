package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.InputPosition;
import info.teksol.mc.emulator.processor.Processor;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.CustomInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public abstract class AbstractTestBase {
    protected static final InputPosition EMPTY = InputPosition.EMPTY;

    protected abstract CompilationPhase getTargetPhase();

    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.MAX;
    }

    protected ProcessorEdition getProcessorEdition() {
        return ProcessorEdition.WORLD_PROCESSOR;
    }

    protected void setDebugPrinterProvider(MindcodeCompiler compiler) {
        // Do nothing by default
    }

    protected CompilerProfile createCompilerProfile() {
        return CompilerProfile.experimentalOptimizations()
                .setProcessorVersion(getProcessorVersion())
                .setProcessorEdition(getProcessorEdition())
                .setDebugLevel(3)
                .setPrintStackTrace(true)
                .setRun(true);

    }

    protected ExpectedMessages expectedMessages() {
        return expectedMessages(c -> {});
    }

    protected ExpectedMessages expectedMessages(Consumer<ExpectedMessages> initializer) {
        ExpectedMessages expectedMessages = ExpectedMessages.create();
        initializer.accept(expectedMessages);
        return expectedMessages
                .addLevelsUpTo(MessageLevel.INFO).ignored();
    }

    protected Processor createEmulator(MindcodeCompiler compiler) {
        return new Processor(compiler.instructionProcessor(), compiler.messageConsumer(), 1000);
    }

    protected <T> T process(ExpectedMessages expectedMessages, InputFiles inputFiles,
            @Nullable Consumer<Processor> emulatorInitializer, Function<MindcodeCompiler, T> resultExtractor) {
        expectedMessages.setAccumulateErrors(true);
        MindcodeCompiler compiler = new MindcodeCompiler(getTargetPhase(), expectedMessages,
                createCompilerProfile(), inputFiles);
        if (emulatorInitializer != null) {
            compiler.setEmulatorInitializer(emulatorInitializer);
        }
        setDebugPrinterProvider(compiler);

        compiler.compile();
        expectedMessages.validate();
        expectedMessages.setAccumulateErrors(false);
        return resultExtractor.apply(compiler);
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        process(expectedMessages, InputFiles.fromSource(source), null, c -> null);
    }

    protected void assertGeneratesMessage(String message, String source) {
        assertGeneratesMessages(expectedMessages().add(message), source);
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

    protected static String q(String str) {
        return '"' + str + '"';
    }

    // Common constants for creating instructions
    protected static final Operation
            add         = Operation.ADD,
            sub         = Operation.SUB,
            rand        = Operation.RAND,
            div         = Operation.DIV,
            floor       = Operation.FLOOR,
            idiv        = Operation.IDIV,
            mul         = Operation.MUL;

    protected static final LogicNumber
            K1000       = LogicNumber.get(1000),
            K0001       = LogicNumber.get("0.001", 0.001),
            P0          = LogicNumber.ZERO,
            P0_5        = LogicNumber.get("0.5", 0.5),
            P1          = LogicNumber.ONE,
            P2          = LogicNumber.get(2),
            P4          = LogicNumber.get(4),
            P8          = LogicNumber.get(8),
            P9          = LogicNumber.get(9),
            P10         = LogicNumber.get(10),
            P11         = LogicNumber.get(11),
            P255        = LogicNumber.get(255),
            N1          = LogicNumber.get(-1),
            N9          = LogicNumber.get(-9),
            N10         = LogicNumber.get(-10),
            N11         = LogicNumber.get(-11);


    protected static final LogicString
            message     = LogicString.create("message");

    protected static final LogicLabel
            label0      = LogicLabel.symbolic("label0"),
            label1      = LogicLabel.symbolic("label1"),
            marker      = LogicLabel.symbolic("marker"),
            label2      = LogicLabel.symbolic("label2");

    protected static final LogicVariable
            bank1       = LogicVariable.block("bank1"),
            cell1       = LogicVariable.block("cell1"),
            conveyor1   = LogicVariable.block("conveyor1"),
            vault1      = LogicVariable.block("vault1"),
            unused      = LogicVariable.unusedVariable(),
            C           = LogicVariable.global("C", false),
            a           = LogicVariable.main("a"),
            b           = LogicVariable.main("b"),
            c           = LogicVariable.main("c"),
            d           = LogicVariable.main("d"),
            another     = LogicVariable.main("another"),
            divisor     = LogicVariable.main("divisor"),
            value       = LogicVariable.main("value"),
            var         = LogicVariable.main("var"),
            foo         = LogicVariable.main("foo"),
            result      = LogicVariable.main("result"),
            ast0        = LogicVariable.ast("__ast0"),
            tmp0        = LogicVariable.temporary("__tmp0"),
            tmp1        = LogicVariable.temporary("__tmp1"),
            tmp2        = LogicVariable.temporary("__tmp2"),
            tmp3        = LogicVariable.temporary("__tmp3"),
            fn0retval   = LogicVariable.fnRetVal("foo", "__fn0retval");

    protected static final LogicBuiltIn
            coal        = LogicBuiltIn.create("@coal"),
            lead        = LogicBuiltIn.create("@lead"),
            firstItem   = LogicBuiltIn.create("@firstItem"),
            enabled     = LogicBuiltIn.create("@enabled"),
            time        = LogicBuiltIn.create("@time"),
            unit        = LogicBuiltIn.create("@unit"),
            thiz        = LogicBuiltIn.create("@this"),
            x           = LogicBuiltIn.create("@x"),
            y           = LogicBuiltIn.create("@y"),
            thisx       = LogicBuiltIn.create("@thisx"),
            thisy       = LogicBuiltIn.create("@thisy"),
            id          = LogicBuiltIn.create("@id");

    protected static final LogicKeyword
            color       = LogicKeyword.create("color"),
            item        = LogicKeyword.create("item");
}
