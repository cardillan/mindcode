package io.github.cardillan.mindcode.compiler.postprocess;

import io.github.cardillan.mindcode.compiler.optimization.OptimizationLevel;
import io.github.cardillan.mindcode.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@NullMarked
public class NodeTransformationsTest extends AbstractCodeOutputTest {

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile()
                .setSignature(false)
                .setAllOptimizationLevels(OptimizationLevel.EXPERIMENTAL);
    }

    @Nested
    class IfTransformations {
        @Test
        void convertsElsifBranches() {
            assertOutputs("""
                            param a = 10;
                            
                            if a == 0 then
                                b = 'A';
                            elsif a == 3 or a == 6 or a == 9 then
                                b = 'B';
                            elsif a in 10 ... 20 then
                                b = 'C';
                            elsif a in (30, 40, 50) then
                                b = 'D';
                            else
                                b = 'E';
                            end;
                            printchar(b);
                            """,
                    """
                            set a 10
                            read *tmp0 "AEEBEEBEEBCCCCCCCCCCEEEEEEEEEEDEEEEEEEEEDEEEEEEEEED" a
                            select :b strictEqual *tmp0 null 69 *tmp0
                            printchar :b
                            """);
        }

        @Test
        void convertsNestedIfs() {
            assertOutputs("""
                            param a = 10;
                            
                            if a == 0 then
                                b = 'A';
                            else
                                if a == 3 or a == 6 or a == 9 then
                                    b = 'B';
                                else
                                    if a in 10 ... 20 then
                                        b = 'C';
                                    else
                                        if a in (30, 40, 50) then
                                            b = 'D';
                                        else
                                            b = 'E';
                                        end;
                                    end;
                                end;
                            end;
                            printchar(b);
                            """,
                    """
                            set a 10
                            read *tmp0 "AEEBEEBEEBCCCCCCCCCCEEEEEEEEEEDEEEEEEEEEDEEEEEEEEED" a
                            select :b strictEqual *tmp0 null 69 *tmp0
                            printchar :b
                            """);
        }
    }
}
