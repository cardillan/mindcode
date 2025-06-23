package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.processor.Processor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.compiler.generation.variables.OptimizerContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.CustomInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public abstract class AbstractTestBase {
    public static final String SCRIPTS_BASE_DIRECTORY = "src/test/resources/info/teksol/mc/mindcode/tests";

    protected static final SourcePosition EMPTY = SourcePosition.EMPTY;

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

    protected String getScriptsDirectory() {
        return SCRIPTS_BASE_DIRECTORY;
    }

    protected String readFile(String filename) throws IOException {
        Path path = Path.of(getScriptsDirectory(), filename);
        return Files.readString(path);
    }

    protected InputFiles createInputFiles(String source) {
        return InputFiles.fromSource(source);
    }

    protected CompilerProfile createCompilerProfile() {
        return CompilerProfile.fullOptimizations(false)
                .setProcessorVersion(getProcessorVersion())
                .setProcessorEdition(getProcessorEdition())
                .setAutoPrintflush(false)
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

    protected <T extends @Nullable Object> T process(ExpectedMessages expectedMessages, InputFiles inputFiles,
            @Nullable Consumer<Processor> emulatorInitializer, Function<MindcodeCompiler, @Nullable T> resultExtractor) {
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
        process(expectedMessages, createInputFiles(source), null, c -> null);
    }

    protected void assertGeneratesMessage(int line, int column, String message, String source) {
        assertGeneratesMessages(expectedMessages().add(line, column, message), source);
    }

    protected void assertGeneratesMessage(String message, String source) {
        assertGeneratesMessages(expectedMessages().add(message), source);
    }

    protected void assertGeneratesMessageRegex(int line, int column, @Language("RegExp") String pattern, String source) {
        assertGeneratesMessages(expectedMessages().addRegex(line, column, pattern), source);
    }

    protected void assertGeneratesMessageRegex(@Language("RegExp") String pattern, String source) {
        assertGeneratesMessages(expectedMessages().addRegex(pattern), source);
    }

    // Instruction creation

    protected final CompilerProfile profile = createCompilerProfile();
    protected final InstructionProcessor ip = InstructionProcessorFactory.getInstructionProcessorNoValidate(
            ExpectedMessages.throwOnMessage(), profile);

    protected final AstContext mockAstRootContext = AstContext.createRootNode(profile);
    protected final AstContext mockAstContext = mockAstRootContext.createSubcontext(AstSubcontextType.MOCK, 1.0);

    protected static LogicArgument _logic(String str) {
        return new GenericArgument(str);
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
        return new CustomInstruction(mockAstContext, false, false, false, opcode, _logic(args),null);
    }

    protected final LogicInstruction customLabelInstruction(String label) {
        return new CustomInstruction(mockAstContext, false, false, true, label,List.of(), null);
    }

    protected static String q(String str) {
        return '"' + str + '"';
    }

    protected static class TestOptimizerContext extends AbstractMessageEmitter implements OptimizerContext {
        public TestOptimizerContext(MessageConsumer messageConsumer) {
            super(messageConsumer);
        }

        @Override
        public <T> List<T> getDiagnosticData(Class<T> type) {
            return List.of();
        }

        @Override
        public void addDiagnosticData(Object data) {
        }

        @Override
        public <T> void addDiagnosticData(Class<T> dataClass, List<T> data) {
        }
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

    protected final LogicNumber
            K0001       = LogicNumber.create(ip, "0.001"),
            P0_5        = LogicNumber.create(ip, "0.5");

    protected static final LogicNumber
            K1000       = LogicNumber.create(1000),
            P0          = LogicNumber.ZERO,
            P1          = LogicNumber.ONE,
            P2          = LogicNumber.create(2),
            P4          = LogicNumber.create(4),
            P8          = LogicNumber.create(8),
            P9          = LogicNumber.create(9),
            P10         = LogicNumber.create(10),
            P11         = LogicNumber.create(11),
            P255        = LogicNumber.create(255),
            N1          = LogicNumber.create(-1),
            N9          = LogicNumber.create(-9),
            N10         = LogicNumber.create(-10),
            N11         = LogicNumber.create(-11);


    protected static final LogicString
            message     = LogicString.create("message");

    protected static final LogicLabel
            label0      = LogicLabel.symbolic("label0"),
            label1      = LogicLabel.symbolic("label1"),
            marker      = LogicLabel.symbolic("marker"),
            label2      = LogicLabel.symbolic("label2");

    protected static final LogicVariable
            bank1       = LogicVariable.block(EMPTY, "bank1"),
            cell1       = LogicVariable.block(EMPTY, "cell1"),
            conveyor1   = LogicVariable.block(EMPTY, "conveyor1"),
            vault1      = LogicVariable.block(EMPTY, "vault1"),
            unused      = LogicVariable.unusedVariable(),
            C           = LogicVariable.global(new AstIdentifier(EMPTY, "C")),
            a           = LogicVariable.main(new AstIdentifier(EMPTY, "a")),
            b           = LogicVariable.main(new AstIdentifier(EMPTY, "b")),
            c           = LogicVariable.main(new AstIdentifier(EMPTY, "c")),
            d           = LogicVariable.main(new AstIdentifier(EMPTY, "d")),
            another     = LogicVariable.main(new AstIdentifier(EMPTY, "another")),
            divisor     = LogicVariable.main(new AstIdentifier(EMPTY, "divisor")),
            value       = LogicVariable.main(new AstIdentifier(EMPTY, "value")),
            var         = LogicVariable.main(new AstIdentifier(EMPTY, "var")),
            foo         = LogicVariable.main(new AstIdentifier(EMPTY, "foo")),
            result      = LogicVariable.main(new AstIdentifier(EMPTY, "result")),
            ast0        = LogicVariable.ast("__ast0"),
            tmp0        = LogicVariable.temporary("__tmp0"),
            tmp1        = LogicVariable.temporary("__tmp1"),
            tmp2        = LogicVariable.temporary("__tmp2"),
            tmp3        = LogicVariable.temporary("__tmp3"),
            fn0retval   = LogicVariable.fnRetVal("foo", "__fn0retval");

    protected static final LogicBuiltIn
            coal        = LogicBuiltIn.createForUnitTests("@coal",      false),
            lead        = LogicBuiltIn.createForUnitTests("@lead",      false),
            firstItem   = LogicBuiltIn.createForUnitTests("@firstItem", false),
            enabled     = LogicBuiltIn.createForUnitTests("@enabled",   false),
            time        = LogicBuiltIn.createForUnitTests("@time",      false),
            unit        = LogicBuiltIn.createForUnitTests("@unit",      false),
            This        = LogicBuiltIn.createForUnitTests("@this",      false),
            x           = LogicBuiltIn.createForUnitTests("@x",         false),
            y           = LogicBuiltIn.createForUnitTests("@y",         false),
            thisx       = LogicBuiltIn.createForUnitTests("@thisx",     false),
            thisy       = LogicBuiltIn.createForUnitTests("@thisy",     false),
            id          = LogicBuiltIn.createForUnitTests("@id",        false);

    protected static final LogicKeyword
            color       = LogicKeyword.create("color"),
            item        = LogicKeyword.create("item");
}
