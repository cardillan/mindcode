package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CallGraphCreatorTest extends AbstractGeneratorTest {
    private static final InputPosition POS = InputPosition.EMPTY;

    private static LogicFunction find(CallGraph graph, String name) {
        return graph.getFunctions().stream().filter(f -> f.getName().equals(name)).findFirst().orElseThrow();
    }

    @Test
    void handlesFunctionlessProgram() {
        Assertions.assertDoesNotThrow(() ->
                CallGraphCreator.createCallGraph(
                        (Seq) translateToAst("a = 10;"),
                        ExpectedMessages.none(),
                        createTestCompiler().processor
                )
        );
    }

    @Test
    void handlesBuiltinFunctions() {
        Assertions.assertDoesNotThrow(() ->
                CallGraphCreator.createCallGraph(
                        (Seq) translateToAst("print(a);"),
                        ExpectedMessages.none(),
                        createTestCompiler().processor
                )
        );
    }

    @Test
    void detectsRecursion() {
        CallGraph graph = CallGraphCreator.createCallGraph(
                (Seq) translateToAst("""
                        def a()  a(); end;
                        a();
                        """
                ),
                ExpectedMessages.none(),
                createTestCompiler().processor
        );

        LogicFunction function = find(graph, "a");

        assertAll(
                () -> assertTrue(function.isUsed()),
                () -> assertTrue(function.isRecursive()),
                () -> assertTrue(function.isRecursiveCall(function))
        );
    }

    @Test
    void detectsDoubleRecursion() {
        CallGraph graph = CallGraphCreator.createCallGraph(
                (Seq) translateToAst("""
                        def a()  b(); end;
                        def b()  a(); end;
                        a();
                        """
                ),
                ExpectedMessages.none(),
                createTestCompiler().processor
        );

        LogicFunction funA = find(graph, "a");
        LogicFunction funB = find(graph, "b");

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
        CallGraph graph = CallGraphCreator.createCallGraph(
                (Seq) translateToAst("""
                        def a()  a(); b(); c(); end;
                        def b()  b(); end;
                        def c()  x=10; end;
                        a();
                        """
                ),
                ExpectedMessages.none(),
                createTestCompiler().processor
        );

        LogicFunction funA = find(graph, "a");
        LogicFunction funB = find(graph, "b");
        LogicFunction funC = find(graph, "c");

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
        CallGraph graph = CallGraphCreator.createCallGraph(
                (Seq) translateToAst("""
                        def a(n) n + 1;       end;
                        def b(n) a(n) + 1;    end;
                        def c(n) a(n) + b(n); end;
                        print(c(1));
                        """
                ),
                ExpectedMessages.none(),
                createTestCompiler().processor
        );

        LogicFunction funA = find(graph, "a");
        LogicFunction funB = find(graph, "b");
        LogicFunction funC = find(graph, "c");

        assertAll(
                () -> assertFalse(funC.isRecursive()),
                () -> assertTrue(funC.isRepeatedCall(funA)),
                () -> assertFalse(funC.isRepeatedCall(funB))
        );
    }
}
