package info.teksol.mindcode.v3.compiler.callgraph;

import info.teksol.mindcode.v3.CompilationPhase;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.mindcode.v3.MindcodeCompiler;
import info.teksol.mindcode.v3.compiler.ast.AbstractAstBuilderTest;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallGraphCreatorTest extends AbstractAstBuilderTest {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.COMPILER;
    }

    private CallGraph buildCallGraph(ExpectedMessages expectedMessages, InputFiles inputFiles) {
        return process(expectedMessages, inputFiles, true, MindcodeCompiler::getCallGraph);
    }

    private info.teksol.mindcode.v3.compiler.callgraph.LogicFunction find(info.teksol.mindcode.v3.compiler.callgraph.CallGraph graph, String name) {
        return graph.getFunctions().stream().filter(f -> f.getName().equals(name)).findFirst().orElseThrow();
    }

    protected void assertBuildsCallGraph(ExpectedMessages expectedMessages, String source, info.teksol.mindcode.v3.compiler.callgraph.CallGraph expected) {
        info.teksol.mindcode.v3.compiler.callgraph.CallGraph actual = buildCallGraph(expectedMessages, InputFiles.fromSource(source));
        assertEquals(expected, actual);
    }

    protected void assertGeneratesMessages(ExpectedMessages expectedMessages, String source) {
        buildCallGraph(expectedMessages, InputFiles.fromSource(source));
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
        info.teksol.mindcode.v3.compiler.callgraph.CallGraph graph = buildCallGraph(expectedMessages(),
                InputFiles.fromSource("""
                        def a()  a(); end;
                        a();
                        """));

        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction function = find(graph, "a");

        assertAll(
                () -> assertTrue(function.isUsed()),
                () -> assertTrue(function.isRecursive()),
                () -> assertTrue(function.isRecursiveCall(function))
        );
    }

    @Test
    void detectsDoubleRecursion() {
        info.teksol.mindcode.v3.compiler.callgraph.CallGraph graph = buildCallGraph(expectedMessages(),
                InputFiles.fromSource("""
                        def a()  b(); end;
                        def b()  a(); end;
                        a();
                        """));

        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funA = find(graph, "a");
        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funB = find(graph, "b");

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
        info.teksol.mindcode.v3.compiler.callgraph.CallGraph graph = buildCallGraph(expectedMessages(),
                InputFiles.fromSource("""
                        def a()  a(); b(); c(); end;
                        def b()  b(); end;
                        def c()  x=10; end;
                        a();
                        """));

        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funA = find(graph, "a");
        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funB = find(graph, "b");
        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funC = find(graph, "c");

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
                InputFiles.fromSource("""
                        def a(n) n + 1;       end;
                        def b(n) a(n) + 1;    end;
                        def c(n) a(n) + b(n); end;
                        print(c(1));
                        """));

        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funA = find(graph, "a");
        info.teksol.mindcode.v3.compiler.callgraph.LogicFunction funB = find(graph, "b");
        LogicFunction funC = find(graph, "c");

        assertAll(
                () -> assertFalse(funC.isRecursive()),
                () -> assertTrue(funC.isRepeatedCall(funA)),
                () -> assertFalse(funC.isRepeatedCall(funB))
        );
    }
}