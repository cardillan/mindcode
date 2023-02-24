package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CallGraphCreatorTest extends AbstractGeneratorTest {

    @Test
    void handlesFunctionlessProgram() {
        Assertions.assertDoesNotThrow(() -> {
            CallGraph graph = CallGraphCreator.createFunctionGraph(
                    (Seq) translateToAst("a = 10")
            );

        });
    }

    @Test
    void handlesBuiltinFunctions() {
        Assertions.assertDoesNotThrow(() -> {
            CallGraph graph = CallGraphCreator.createFunctionGraph(
                    (Seq) translateToAst("print(a)")
            );

        });
    }

    @Test
    void detectsRecursion() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst(""
                        + "def a  a() end "
                        + "a()"
                )
        );

        CallGraph.Function function = graph.getFunction("a");

        Assertions.assertTrue(function.isUsed());
        Assertions.assertTrue(function.isRecursive());
        Assertions.assertTrue(function.isCallRecursive("a"));
    }

    @Test
    void detectsDoubleRecursion() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst(""
                        + "def a  b() end "
                        + "def b  a() end "
                        + "a()"
                )
        );

        CallGraph.Function funA = graph.getFunction("a");
        Assertions.assertTrue(funA.isUsed());
        Assertions.assertTrue(funA.isRecursive());
        Assertions.assertTrue(funA.isCallRecursive("b"));

        CallGraph.Function funB = graph.getFunction("b");
        Assertions.assertTrue(funB.isUsed());
        Assertions.assertTrue(funB.isRecursive());
        Assertions.assertTrue(funB.isCallRecursive("a"));
    }

    @Test
    void detectsNonrecursiveCalls() {
        CallGraph graph = CallGraphCreator.createFunctionGraph(
                (Seq) translateToAst(""
                        + "def a  a() b() c() end "
                        + "def b  b() end "
                        + "def c  x=10 end "
                        + "a()"
                )
        );

        CallGraph.Function funA = graph.getFunction("a");
        Assertions.assertTrue(funA.isRecursive());
        Assertions.assertFalse(funA.isCallRecursive("b"));
        Assertions.assertFalse(funA.isCallRecursive("c"));

        CallGraph.Function funB = graph.getFunction("b");
        Assertions.assertTrue(funB.isUsed());
        Assertions.assertTrue(funB.isRecursive());
    }
}
