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
        return CompilationPhase.CALL_GRAPH;
    }

    private CallGraph buildCallGraph(ExpectedMessages expectedMessages, InputFiles inputFiles) {
        return process(expectedMessages, inputFiles, true, MindcodeCompiler::getCallGraph);
    }

    private LogicFunctionV3 find(CallGraph graph, String name) {
        return graph.getFunctions().stream().filter(f -> f.getName().equals(name)).findFirst().orElseThrow();
    }

    protected void assertBuildsCallGraph(ExpectedMessages expectedMessages, String source, CallGraph expected) {
        CallGraph actual = buildCallGraph(expectedMessages, InputFiles.fromSource(source));
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
        CallGraph graph = buildCallGraph(expectedMessages(),
                InputFiles.fromSource("""
                        allocate stack in cell1;
                        def a()  a(); end;
                        a();
                        """));

        LogicFunctionV3 function = find(graph, "a");

        assertAll(
                () -> assertTrue(function.isUsed()),
                () -> assertTrue(function.isRecursive()),
                () -> assertTrue(function.isRecursiveCall(function))
        );
    }

    @Test
    void detectsDoubleRecursion() {
        CallGraph graph = buildCallGraph(expectedMessages(),
                InputFiles.fromSource("""
                        allocate stack in cell1;
                        def a()  b(); end;
                        def b()  a(); end;
                        a();
                        """));

        LogicFunctionV3 funA = find(graph, "a");
        LogicFunctionV3 funB = find(graph, "b");

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
                InputFiles.fromSource("""
                        allocate stack in cell1;
                        def a()  a(); b(); c(); end;
                        def b()  b(); end;
                        def c()  x=10; end;
                        a();
                        """));

        LogicFunctionV3 funA = find(graph, "a");
        LogicFunctionV3 funB = find(graph, "b");
        LogicFunctionV3 funC = find(graph, "c");

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

        LogicFunctionV3 funA = find(graph, "a");
        LogicFunctionV3 funB = find(graph, "b");
        LogicFunctionV3 funC = find(graph, "c");

        assertAll(
                () -> assertFalse(funC.isRecursive()),
                () -> assertTrue(funC.isRepeatedCall(funA)),
                () -> assertFalse(funC.isRepeatedCall(funB))
        );
    }
}