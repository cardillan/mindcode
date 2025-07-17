package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ArrayOptimizerTest extends AbstractOptimizerTest<ArrayOptimizer> {


    @Override
    protected @Nullable Class<ArrayOptimizer> getTestedClass() {
        return ArrayOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Test
    void inlinesSmallArrays1() {
        assertCompilesTo("""
                        var array[1];
                        param p = 0;
                        
                        ++array[p];
                        print(array);
                        """,
                createInstruction(OP, "add", ".array*0", ".array*0", "1"),
                createInstruction(PRINT, ".array*0")
        );
    }

    @Test
    void inlinesSmallArrays2() {
        assertCompilesTo("""
                        var array[2];
                        param p = 0;
                        
                        ++array[p];
                        print(array);
                        """,
                createInstruction(SET, "p", "0"),
                createInstruction(SELECT, tmp(0), "equal", "p", "0", ".array*0", ".array*1"),
                createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                createInstruction(JUMP, label(2), "notEqual", "p", "0"),
                createInstruction(SET, ".array*0", tmp(1)),
                createInstruction(JUMP, label(3), "always"),
                createInstruction(LABEL, label(2)),
                createInstruction(SET, ".array*1", tmp(1)),
                createInstruction(LABEL, label(3)),
                createInstruction(PRINT, ".array*0"),
                createInstruction(PRINT, ".array*1")
        );
    }

    @Test
    void inlinesSmallArrays3() {
        assertCompilesTo("""
                        var array[3];
                        param p = 0;
                        
                        ++array[p];
                        print(array);
                        """,
                createInstruction(SET, "p", "0"),
                createInstruction(JUMP, label(0), "notEqual", "p", "0"),
                createInstruction(SET, tmp(0), ".array*0"),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(SELECT, tmp(0), "equal", "p", "1", ".array*1", ".array*2"),
                createInstruction(LABEL, label(1)),
                createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                createInstruction(JUMP, label(4), "notEqual", "p", "0"),
                createInstruction(SET, ".array*0", tmp(1)),
                createInstruction(JUMP, label(5), "always"),
                createInstruction(LABEL, label(4)),
                createInstruction(JUMP, label(6), "notEqual", "p", "1"),
                createInstruction(SET, ".array*1", tmp(1)),
                createInstruction(JUMP, label(7), "always"),
                createInstruction(LABEL, label(6)),
                createInstruction(SET, ".array*2", tmp(1)),
                createInstruction(LABEL, label(7)),
                createInstruction(LABEL, label(5)),
                createInstruction(PRINT, ".array*0"),
                createInstruction(PRINT, ".array*1"),
                createInstruction(PRINT, ".array*2")

        );
    }

    @Test
    void inlinesRegularArrays() {
        assertCompilesTo("""
                        var array[4];
                        param p = 0;
                        
                        ++array[p];
                        print(array);
                        """,
                createInstruction(SET, "p", "0"),
                createInstruction(OP, "mul", tmp(2), "p", "2"),
                createInstruction(MULTIJUMP, label(1), tmp(2), "0"),
                createInstruction(MULTILABEL, label(1)),
                createInstruction(SET, tmp(0), ".array*0"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(MULTILABEL, label(2)),
                createInstruction(SET, tmp(0), ".array*1"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(MULTILABEL, label(3)),
                createInstruction(SET, tmp(0), ".array*2"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(MULTILABEL, label(4)),
                createInstruction(SET, tmp(0), ".array*3"),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                createInstruction(MULTIJUMP, label(7), tmp(2), "0"),
                createInstruction(MULTILABEL, label(7)),
                createInstruction(SET, ".array*0", tmp(1)),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(8)),
                createInstruction(SET, ".array*1", tmp(1)),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(9)),
                createInstruction(SET, ".array*2", tmp(1)),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(10)),
                createInstruction(SET, ".array*3", tmp(1)),
                createInstruction(LABEL, label(6)),
                createInstruction(PRINT, ".array*0"),
                createInstruction(PRINT, ".array*1"),
                createInstruction(PRINT, ".array*2"),
                createInstruction(PRINT, ".array*3")
        );
    }

    @Test
    void inlinesSingleJumpTable() {
        assertCompilesTo("""
                        #set instruction-limit = 42;
                        var array[5] = (1, 2, 3, 4, 5);
                        param p = 0;
                        
                        print(array[p]);
                        print(array[p+1]);
                        print(array[p+2]);
                        """,
                createInstruction(SET, ".array*0", "1"),
                createInstruction(SET, ".array*1", "2"),
                createInstruction(SET, ".array*2", "3"),
                createInstruction(SET, ".array*3", "4"),
                createInstruction(SET, ".array*4", "5"),
                createInstruction(SET, "p", "0"),
                createInstruction(OP, "mul", tmp(7), "p", "2"),
                createInstruction(MULTIJUMP, label(7), tmp(7), "0"),
                createInstruction(MULTILABEL, label(7)),
                createInstruction(SET, tmp(0), ".array*0"),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(8)),
                createInstruction(SET, tmp(0), ".array*1"),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(9)),
                createInstruction(SET, tmp(0), ".array*2"),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(10)),
                createInstruction(SET, tmp(0), ".array*3"),
                createInstruction(JUMP, label(6), "always"),
                createInstruction(MULTILABEL, label(11)),
                createInstruction(SET, tmp(0), ".array*4"),
                createInstruction(LABEL, label(6)),
                createInstruction(PRINT, tmp(0)),
                createInstruction(OP, "add", tmp(2), "p", "1"),
                createInstruction(SETADDR, ".array*rret", label(13)),
                createInstruction(OP, "mul", tmp(8), tmp(2), "2"),
                createInstruction(MULTICALL, label(0), tmp(8)),
                createInstruction(LABEL, label(13)),
                createInstruction(PRINT, ".array*r"),
                createInstruction(OP, "add", tmp(5), "p", "2"),
                createInstruction(SETADDR, ".array*rret", label(14)),
                createInstruction(OP, "mul", tmp(9), tmp(5), "2"),
                createInstruction(MULTICALL, label(0), tmp(9)),
                createInstruction(LABEL, label(14)),
                createInstruction(PRINT, ".array*r"),
                createInstruction(END),
                createInstruction(MULTILABEL, label(0)),
                createInstruction(SET, ".array*r", ".array*0"),
                createInstruction(RETURN, ".array*rret"),
                createInstruction(MULTILABEL, label(1)),
                createInstruction(SET, ".array*r", ".array*1"),
                createInstruction(RETURN, ".array*rret"),
                createInstruction(MULTILABEL, label(2)),
                createInstruction(SET, ".array*r", ".array*2"),
                createInstruction(RETURN, ".array*rret"),
                createInstruction(MULTILABEL, label(3)),
                createInstruction(SET, ".array*r", ".array*3"),
                createInstruction(RETURN, ".array*rret"),
                createInstruction(MULTILABEL, label(4)),
                createInstruction(SET, ".array*r", ".array*4"),
                createInstruction(RETURN, ".array*rret")
        );
    }

}