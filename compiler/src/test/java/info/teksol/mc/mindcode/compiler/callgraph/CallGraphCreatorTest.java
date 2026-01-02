package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.AbstractCompilerTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class CallGraphCreatorTest extends AbstractCompilerTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.CALL_GRAPH;
    }

    private CallGraph buildCallGraph(ExpectedMessages expectedMessages, InputFiles inputFiles) {
        return Objects.requireNonNull(process(expectedMessages, inputFiles, null,
                MindcodeCompiler::getCallGraph));
    }

    private MindcodeFunction find(CallGraph graph, String name) {
        return graph.getFunctions().stream().filter(f -> f.getName().equals(name)).findFirst().orElseThrow();
    }

    protected void assertBuildsCallGraph(ExpectedMessages expectedMessages, String source, CallGraph expected) {
        CallGraph actual = buildCallGraph(expectedMessages, createInputFiles(source));
        assertEquals(expected, actual);
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        buildCallGraph(expectedMessages, createInputFiles(source));
    }


    @Test
    void handlesFunctionlessProgram() {
        assertGeneratesMessages(expectedMessages(), "a = 10;");
    }

    @Test
    void handlesBuiltinFunctions() {
        assertGeneratesMessages(expectedMessages(), "print(a);");
    }

    @Test
    void detectsRecursion() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        allocate stack in cell1;
                        def a()  a(); end;
                        a();
                        """));

        MindcodeFunction function = find(graph, "a");

        assertAll(
                () -> assertTrue(function.isUsed()),
                () -> assertTrue(function.isRecursive()),
                () -> assertTrue(function.isRecursiveCall(function))
        );
    }

    @Test
    void detectsDoubleRecursion() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        allocate stack in cell1;
                        def a()  b(); end;
                        def b()  a(); end;
                        a();
                        """));

        MindcodeFunction funA = find(graph, "a");
        MindcodeFunction funB = find(graph, "b");

        assertAll(
                () -> assertTrue(funA.isUsed()),
                () -> assertTrue(funA.isRecursive()),
                () -> assertTrue(funA.isRecursiveCall(funB)),

                () -> assertTrue(funB.isUsed()),
                () -> assertTrue(funB.isRecursive()),
                () -> assertTrue(funB.isRecursiveCall(funA))
        );
    }

    @Test
    void detectsNonRecursiveCalls() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        allocate stack in cell1;
                        def a()  a(); b(); c(); end;
                        def b()  b(); end;
                        def c()  x=10; end;
                        a();
                        """));

        MindcodeFunction funA = find(graph, "a");
        MindcodeFunction funB = find(graph, "b");
        MindcodeFunction funC = find(graph, "c");

        assertAll(
                () -> assertTrue(funA.isRecursive()),
                () -> assertFalse(funA.isRecursiveCall(funB)),
                () -> assertFalse(funA.isRecursiveCall(funC)),
                () -> assertTrue(funB.isUsed()),
                () -> assertTrue(funB.isRecursive())
        );
    }

    @Test
    void detectsIndirectCalls() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        def a(n) n + 1;       end;
                        def b(n) a(n) + 1;    end;
                        def c(n) a(n) + b(n); end;
                        print(c(1));
                        """));

        MindcodeFunction funA = find(graph, "a");
        MindcodeFunction funB = find(graph, "b");
        MindcodeFunction funC = find(graph, "c");

        assertAll(
                () -> assertFalse(funC.isRecursive()),
                () -> assertTrue(funC.isRepeatedCall(funA)),
                () -> assertFalse(funC.isRepeatedCall(funB))
        );
    }

    @Test
    void computersReferencesCorrectly() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        void foo() bar(); bar(); end;
                        void bar() baz(); baz(); end;
                        void baz() println("Called"); end;
                        foo();
                        """));

        MindcodeFunction foo = find(graph, "foo");
        MindcodeFunction bar = find(graph, "bar");
        MindcodeFunction baz = find(graph, "baz");

        assertAll(
                () -> assertEquals(1, foo.getPlacementCount(), "foo use count not correct"),
                () -> assertEquals(2, bar.getPlacementCount(), "bar use count not correct"),
                () -> assertEquals(2, baz.getPlacementCount(), "baz use count not correct")
        );
    }

    @Test
    void computersReferencesCorrectlyForInlineFunctions1() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        inline void foo() bar(); bar(); end;
                        inline void bar() baz(); baz(); end;
                        inline void baz() println("Called"); end;
                        foo();
                        """));

        MindcodeFunction foo = find(graph, "foo");
        MindcodeFunction bar = find(graph, "bar");
        MindcodeFunction baz = find(graph, "baz");

        assertAll(
                () -> assertEquals(1, foo.getPlacementCount(), "foo use count not correct"),
                () -> assertEquals(2, bar.getPlacementCount(), "bar use count not correct"),
                () -> assertEquals(4, baz.getPlacementCount(), "baz use count not correct")
        );
    }

    @Test
    void computersReferencesCorrectlyForInlineFunctions2() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        void foo() bar(); bar(); end;
                        inline void bar() baz(); baz(); end;
                        inline void baz() println("Called"); end;
                        foo(); foo();
                        """));

        MindcodeFunction foo = find(graph, "foo");
        MindcodeFunction bar = find(graph, "bar");
        MindcodeFunction baz = find(graph, "baz");

        assertAll(
                () -> assertEquals(2, foo.getPlacementCount(), "foo use count not correct"),
                () -> assertEquals(2, bar.getPlacementCount(), "bar use count not correct"),
                () -> assertEquals(4, baz.getPlacementCount(), "baz use count not correct")
        );
    }

    @Test
    void computersReferencesCorrectlyForInlineFunctions3() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                createInputFiles("""
                        inline void foo() bar(); bar(); end;
                        void bar() baz(); baz(); end;
                        inline void baz() println("Called"); end;
                        foo(); foo();
                        """));

        MindcodeFunction foo = find(graph, "foo");
        MindcodeFunction bar = find(graph, "bar");
        MindcodeFunction baz = find(graph, "baz");

        assertAll(
                () -> assertEquals(2, foo.getPlacementCount(), "foo use count not correct"),
                () -> assertEquals(4, bar.getPlacementCount(), "bar use count not correct"),
                () -> assertEquals(2, baz.getPlacementCount(), "baz use count not correct")
        );
    }
}
