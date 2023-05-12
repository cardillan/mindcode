package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CallGraphCreatorTest extends AbstractGeneratorTest {

    @Test
    void handlesFunctionlessProgram() {
        Assertions.assertDoesNotThrow(() ->
                CallGraphCreator.createFunctionGraph(
                        (Seq) translateToAst("a = 10"),
                        getInstructionProcessor()
                )
        );
    }

    @Test
    void handlesBuiltinFunctions() {
        Assertions.assertDoesNotThrow(() ->
                CallGraphCreator.createFunctionGraph(
                        (Seq) translateToAst("print(a)"),
                        getInstructionProcessor()
                )
        );
    }

    @Test
    void detectsRecursion() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst("""
                        def a  a() end
                        a()
                        """
                ),
                getInstructionProcessor()
        );

        CallGraph.Function function = graph.getFunction("a");

        Assertions.assertTrue(function.isUsed());
        Assertions.assertTrue(function.isRecursive());
        Assertions.assertTrue(function.isRecursiveCall("a"));
    }

    @Test
    void detectsDoubleRecursion() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst("""
                        def a  b() end
                        def b  a() end
                        a()
                        """
                ),
                getInstructionProcessor()
        );

        CallGraph.Function funA = graph.getFunction("a");
        Assertions.assertTrue(funA.isUsed());
        Assertions.assertTrue(funA.isRecursive());
        Assertions.assertTrue(funA.isRecursiveCall("b"));

        CallGraph.Function funB = graph.getFunction("b");
        Assertions.assertTrue(funB.isUsed());
        Assertions.assertTrue(funB.isRecursive());
        Assertions.assertTrue(funB.isRecursiveCall("a"));
    }

    @Test
    void detectsNonrecursiveCalls() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst("""
                        def a  a() b() c() end
                        def b  b() end
                        def c  x=10 end
                        a()
                        """
                ),
                getInstructionProcessor()
        );

        CallGraph.Function funA = graph.getFunction("a");
        Assertions.assertTrue(funA.isRecursive());
        Assertions.assertFalse(funA.isRecursiveCall("b"));
        Assertions.assertFalse(funA.isRecursiveCall("c"));

        CallGraph.Function funB = graph.getFunction("b");
        Assertions.assertTrue(funB.isUsed());
        Assertions.assertTrue(funB.isRecursive());
    }

    @Test
    void detectsIndirectCalls() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst("""
                        def a(n) n + 1       end
                        def b(n) a(n) + 1    end
                        def c(n) a(n) + b(n) end
                        print(c(1))
                        """
                ),
                getInstructionProcessor()
        );

        CallGraph.Function funC = graph.getFunction("c");
        Assertions.assertFalse(funC.isRecursive());
        Assertions.assertTrue (funC.isRepeatedCall("a"));
        Assertions.assertFalse(funC.isRepeatedCall("b"));
    }
}
