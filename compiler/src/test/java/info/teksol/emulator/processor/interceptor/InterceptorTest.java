package info.teksol.emulator.processor.interceptor;

import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

public class InterceptorTest extends AbstractInterceptorTest {

    @Override
    protected String getScriptsDirectory() {
        return "";
    }

    @Test
    void optimizesCaseExpressions() {
        testCode("""
                        param x = 1;
                        a = case x
                            when 0 then 0;
                        end;
                        print(a);
                        """,
                "null"
        );
    }

    @Test
    void optimizesConstantCaseExpressions() {
        testCode("""
                        def foo(n)
                            case n
                                when 0 then return 0;
                            end;
                        
                            n + 1;
                        end;
                        
                        print(foo(5));
                        """,
                "6"
        );
    }

    @Test
    void preservesLastGlobalInFuncOnExperimental() {
        testCode("""
                        #set optimization = experimental;
                        A = 0;
                        foo();
                        print(A);
                        
                        noinline def foo()
                            A = 1;
                        end;
                        """,
                "1"
        );
    }

    @Test
    void optimizesListIterationLoopWithOutputs() {
        testCode("""
                        #set optimization = experimental;
                        param count = 2;
                        a = b = 2;
                        for i in 1 .. count do
                            for out j in a, b do
                                if i < count then j = 1; end;
                            end;
                        end;
                        
                        print(a + b);
                        """,
                "2"
        );
    }

    @Test
    void compilesProperComparison() {
        testCode(createTestCompiler(createCompilerProfile()
                        .setOptimizationLevel(Optimization.PRINT_MERGING, OptimizationLevel.NONE)),
                """
                        #set print-merging = false;
                        inline def eval(b)
                            b ? "T" : "F";
                        end;
                        
                        inline def compare(a, b)
                            print(eval(a > b), eval(a < b));
                        end;
                        
                        param A = 0;
                        compare(A, A);
                        """,
                "F", "F"
        );
    }

    @Test
    void solveCurrentProblem() {
        testCode(createTestCompiler(createCompilerProfile()
                        .setProcessorVersion(ProcessorVersion.MAX)
                        .setAllOptimizationLevels(OptimizationLevel.NONE)
                        .decode("57340723201")
                ),
                """
                        inline def frac(_x)
                            _x % 1;
                        end;
                        
                        void printExactSlow(_n)
                            if _n < 0 then
                                print("-");
                                _n = abs(_n);
                             end;
                            _exp = floor(log10(_n));
                            _base = _n * 10 ** -_exp;
                            print(floor(_base), ".");
                        
                            for _digit in 1 .. 15 do
                                _base = frac(_base) * 10;
                                print(floor(_base));
                            end;
                        
                            if _exp != 0 then
                                print("E", _exp);
                            end;
                        end;
                        
                        assertPrints("-1.234500000000000E-50", printExactSlow(-1.2345e-50), "printExactSlow(-1.2345e-50)");
                        """);
    }
}
