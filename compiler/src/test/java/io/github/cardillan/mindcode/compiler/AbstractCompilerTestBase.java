package io.github.cardillan.mindcode.compiler;

import io.github.cardillan.mindcode.AbstractTestBase;
import io.github.cardillan.mindcode.common.InputFiles;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstIdentifier;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstModule;
import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.compiler.astcontext.TestNode;
import io.github.cardillan.mindcode.compiler.callgraph.CallGraph;
import io.github.cardillan.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import io.github.cardillan.mindcode.compiler.generation.StackTracker;
import io.github.cardillan.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mindcode.compiler.generation.variables.OptimizerContext;
import io.github.cardillan.mindcode.compiler.generation.variables.StandardNameCreator;
import io.github.cardillan.mindcode.emulator.blocks.LogicBlock;
import io.github.cardillan.mindcode.logic.arguments.*;
import io.github.cardillan.mindcode.logic.arguments.arrays.ArrayConstructorContext;
import io.github.cardillan.mindcode.logic.instructions.CustomInstruction;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessorFactory;
import io.github.cardillan.mindcode.logic.instructions.LogicInstruction;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import io.github.cardillan.mindcode.logic.opcodes.ProcessorType;
import io.github.cardillan.mindcode.logic.opcodes.ProcessorVersion;
import io.github.cardillan.mindcode.messages.AbstractMessageEmitter;
import io.github.cardillan.mindcode.messages.ExpectedMessages;
import io.github.cardillan.mindcode.messages.MessageConsumer;
import io.github.cardillan.mindcode.messages.MessageLevel;
import io.github.cardillan.mindcode.profile.CompilerProfile;
import io.github.cardillan.mindcode.profile.GlobalCompilerProfile;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public abstract class AbstractCompilerTestBase extends AbstractTestBase implements ArrayConstructorContext {

    @AfterEach
    void clearContext() {
        ContextFactory.clearContexts();
    }

    public static final String SCRIPTS_BASE_DIRECTORY = "src/test/resources/io/github/cardillan/mindcode/tests";

    protected static final SourcePosition EMPTY = SourcePosition.EMPTY;

    protected static final AstModule MAIN_MODULE = new AstModule(EMPTY,
            null, List.of(), Collections.emptySortedSet(), true);

    protected static final CallGraph EMPTY_CALL_GRAPH = CallGraph.createEmpty(MAIN_MODULE);

    protected abstract CompilationPhase getTargetPhase();

    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.MAX;
    }

    protected ProcessorType getProcessorType() {
        return ProcessorType.WORLD_PROCESSOR;
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
        return CompilerProfile.fullOptimizations(false, false)
                .setTarget(getProcessorVersion(), getProcessorType())
                .setAutoPrintflush(false)
                .setDebugMessages(3)
                .setPrintStackTrace(true)
                .setPrintCodeSize(false)
                .setRun(false);
    }

    protected ExpectedMessages expectedMessages() {
        return expectedMessages(
                m -> m.addRegex("The limit of \\d+ executable instructions has been exceeded\\.").ignored());
    }

    protected ExpectedMessages expectedMessages(Consumer<ExpectedMessages> initializer) {
        ExpectedMessages expectedMessages = ExpectedMessages.create();
        initializer.accept(expectedMessages);
        return expectedMessages
                .addLevelsUpTo(MessageLevel.INFO).ignored();
    }

    protected <T extends @Nullable Object> T process(ExpectedMessages expectedMessages, InputFiles inputFiles,
            @Nullable Consumer<LogicBlock> logicBlockInitializer, Function<MindcodeCompiler, @Nullable T> resultExtractor) {
        expectedMessages.setAccumulateErrors(true);
        MindcodeCompiler compiler = new MindcodeCompiler(getTargetPhase(), expectedMessages,
                createCompilerProfile(), inputFiles);
        if (logicBlockInitializer != null) {
            compiler.setLogicBlockInitializer(logicBlockInitializer);
        }
        setDebugPrinterProvider(compiler);

        //System.out.println(compiler.compilerProfile().encode());
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
    protected static final NameCreator nameCreator = new StandardNameCreator(false);

    protected final CompilerProfile profile = createCompilerProfile();
    protected final InstructionProcessor ip = InstructionProcessorFactory.getInstructionProcessorNoValidate(
            ExpectedMessages.throwOnMessage(), nameCreator, profile);

    protected final TestNode testNode = new TestNode(profile, AstContextType.ROOT, AstSubcontextType.BASIC);

    protected final AstContext mockAstRootContext = AstContext.createRootNode(profile);
    protected final AstContext mockAstContext = mockAstRootContext.createChild(testNode, AstContextType.CODE);

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
        return '"' + str.replace("\n", "\\n") + '"';
    }

    protected class TestOptimizerContext extends AbstractMessageEmitter implements OptimizerContext {
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

        @Override
        public AstContext rootAstContext() {
            return mockAstRootContext;
        }

        @Override
        public StackTracker stackTracker() {
            return StackTracker.mockInternalStack();
        }

        @Override
        public CallGraph callGraph() {
            return EMPTY_CALL_GRAPH;
        }

        public NameCreator nameCreator() {
            return nameCreator;
        }
    }

    protected static LogicVariable block(String name, String blockType) {
        return LogicVariable.block(EMPTY, name, blockType);
    }

    protected static LogicVariable global(String name) {
        return LogicVariable.global(new AstIdentifier(EMPTY, name), "." + name);
    }

    @SuppressWarnings("ConfusingMainMethod")
    protected static LogicVariable main(String name) {
        return LogicVariable.main(new AstIdentifier(EMPTY, name), ":" + name);
    }

    // Common constants for creating instructions
    protected static final Operation
            add         = Operation.ADD,
            div         = Operation.DIV,
            floor       = Operation.FLOOR,
            idiv        = Operation.IDIV,
            mul         = Operation.MUL,
            rand        = Operation.RAND,
            round       = Operation.ROUND,
            sub         = Operation.SUB;

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
            message     = LogicString.createSimple("message");

    protected static final LogicLabel
            label0      = LogicLabel.symbolic("label0"),
            label1      = LogicLabel.symbolic("label1"),
            marker      = LogicLabel.symbolic("marker"),
            label2      = LogicLabel.symbolic("label2");

    protected static final LogicVariable
            bank1       = block("bank1", "@memory-bank"),
            cell1       = block("cell1", "@memory-cell"),
            conveyor1   = block("conveyor1", "@conveyor"),
            vault1      = block("vault1", "@vault"),
            unused      = LogicVariable.unusedVariable(),
            C           = global("C"),
            a           = main("a"),
            b           = main("b"),
            c           = main("c"),
            d           = main("d"),
            another     = main("another"),
            divisor     = main("divisor"),
            value       = main("value"),
            var         = main("var"),
            foo         = main("foo"),
            result      = main("result"),
            ast0        = LogicVariable.ast("*ast0"),
            tmp0        = LogicVariable.temporary(nameCreator, 0),
            tmp1        = LogicVariable.temporary(nameCreator, 1),
            tmp2        = LogicVariable.temporary(nameCreator, 2),
            tmp3        = LogicVariable.temporary(nameCreator, 3),
            fn0retval   = LogicVariable.fnRetVal("foo", ":fn0");

    protected static final LogicBuiltIn
            coal        = LogicBuiltIn.createForUnitTests("@coal",      false),
            oreCoal     = LogicBuiltIn.createForUnitTests("@ore-coal",  false),
            lead        = LogicBuiltIn.createForUnitTests("@lead",      false),
            firstItem   = LogicBuiltIn.createForUnitTests("@firstItem", false),
            enabled     = LogicBuiltIn.createForUnitTests("@enabled",   false),
            time        = LogicBuiltIn.createForUnitTests("@time",      false),
            unit        = LogicBuiltIn.createForUnitTests("@unit",      false),
            thiz        = LogicBuiltIn.createForUnitTests("@this",      false),
            x           = LogicBuiltIn.createForUnitTests("@x",         false),
            y           = LogicBuiltIn.createForUnitTests("@y",         false),
            thisx       = LogicBuiltIn.createForUnitTests("@thisx",     false),
            thisy       = LogicBuiltIn.createForUnitTests("@thisy",     false),
            name        = LogicBuiltIn.createForUnitTests("@name",      false),
            id          = LogicBuiltIn.createForUnitTests("@id",        false);

    protected static final LogicKeyword
            color       = LogicKeyword.create("color"),
            item        = LogicKeyword.create("item");


    @Override
    public GlobalCompilerProfile globalCompilerProfile() {
        return profile;
    }

    @Override
    public InstructionProcessor instructionProcessor() {
        return ip;
    }

    @Override
    public NameCreator nameCreator() {
        return nameCreator;
    }

    @Override
    public AstContext rootAstContext() {
        return mockAstRootContext;
    }
}
