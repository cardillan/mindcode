package info.teksol.mc.mindcode.tests.interceptor;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

@NullMarked
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
    void preservesLastGlobalInFunc() {
        testCode("""
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
        testCode("""
                        #set print-merging = none;
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
        testCode("""
                        Y1 = rand(0);
                        DIR1 = @thisx;
                        if DIR1 == 1 then
                            Y1 = Y1 + 1;
                        else
                            Y1 = Y1 - 1;
                        end;
                        print(Y1);
                        """,
                "1");
    }
}
