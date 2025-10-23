package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ArrayOptimizerTest {

    @Nested
    class Target7 extends AbstractOptimizerTest<ArrayOptimizer> {

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
            return super.createCompilerProfile()
                    .setGoal(GenerationGoal.SPEED)
                    .setTarget(ProcessorVersion.V7A, ProcessorEdition.S);
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
                    createInstruction(OP, "add", tmp(1), ".array*1", "1"),
                    createInstruction(JUMP, label(2), "notEqual", "p", "0"),
                    createInstruction(OP, "add", tmp(1), ".array*0", "1"),
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
                    createInstruction(SET, tmp(0), ".array*2"),
                    createInstruction(JUMP, label(3), "notEqual", "p", "1"),
                    createInstruction(SET, tmp(0), ".array*1"),
                    createInstruction(LABEL, label(3)),
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

    @Nested
    class Target8_0 extends AbstractOptimizerTest<ArrayOptimizer> {

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
            return super.createCompilerProfile()
                    .setGoal(GenerationGoal.SPEED)
                    .setTarget(ProcessorVersion.V8A, ProcessorEdition.S);
        }

        @Test
        void inlinesRegularArrays() {
            assertCompilesTo("""
                            #set builtin-evaluation = none;
                            #set text-tables = false;
                            var array[5];
                            param p = 0;
                            
                            ++array[p];
                            print(array);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(OP, "mul", tmp(2), "p", "2"),
                    createInstruction(MULTIJUMP, label(1), tmp(2), "0"),
                    createInstruction(MULTILABEL, label(1)),
                    createInstruction(SET, ".array*elem", q(".array*0")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(2)),
                    createInstruction(SET, ".array*elem", q(".array*1")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ".array*elem", q(".array*2")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ".array*elem", q(".array*3")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(5)),
                    createInstruction(SET, ".array*elem", q(".array*4")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(0), "@this", ".array*elem"),
                    createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                    createInstruction(WRITE, tmp(1), "@this", ".array*elem"),
                    createInstruction(PRINT, ".array*0"),
                    createInstruction(PRINT, ".array*1"),
                    createInstruction(PRINT, ".array*2"),
                    createInstruction(PRINT, ".array*3"),
                    createInstruction(PRINT, ".array*4")
            );
        }
    }

    @Nested
    class TargetLatest extends AbstractOptimizerTest<ArrayOptimizer> {

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
            return super.createCompilerProfile()
                    .setGoal(GenerationGoal.SPEED);
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
                    createInstruction(SELECT, ".array*elem", "equal", "p", "0", q(".array*0"), q(".array*1")),
                    createInstruction(READ, tmp(0), "@this", ".array*elem"),
                    createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                    createInstruction(WRITE, tmp(1), "@this", ".array*elem"),
                    createInstruction(PRINT, ".array*0"),
                    createInstruction(PRINT, ".array*1")
            );
        }

        @Test
        void inlinesSmallArrays3() {
            assertCompilesTo("""
                            #set builtin-evaluation = none;
                            var array[3];
                            param p = 0;
                            
                            ++array[p];
                            print(array);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(SELECT, ".array*elem", "equal", "p", "0", q(".array*0"), q(".array*1")),
                    createInstruction(SELECT, ".array*elem", "equal", "p", "2", q(".array*2"), ".array*elem"),
                    createInstruction(READ, tmp(0), "@this", ".array*elem"),
                    createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                    createInstruction(WRITE, tmp(1), "@this", ".array*elem"),
                    createInstruction(PRINT, ".array*0"),
                    createInstruction(PRINT, ".array*1"),
                    createInstruction(PRINT, ".array*2")
            );
        }

        @Test
        void inlinesAndFoldsSingleJumpTable() {
            assertCompilesTo("""
                            #set instruction-limit = 40;
                            #set builtin-evaluation = none;
                            const array[5] = (1, 2, 3, 4, 5);
                            param p = 0;
                            
                            print(array[p]);
                            print(array[p+1]);
                            print(array[p+2]);
                            #setlocal weight = 2;
                            print(array[p+3]);
                            print(array[p+4]);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(SETADDR, ".array*rret", label(5)),
                    createInstruction(MULTICALL, "p", "0"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(PRINT, ".array*r"),
                    createInstruction(OP, "add", tmp(2), "p", "1"),
                    createInstruction(SETADDR, ".array*rret", label(6)),
                    createInstruction(MULTICALL, tmp(2), "0"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(PRINT, ".array*r"),
                    createInstruction(OP, "add", tmp(6), "p", "2"),
                    createInstruction(SETADDR, ".array*rret", label(7)),
                    createInstruction(MULTICALL, tmp(6), "0"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(PRINT, ".array*r"),
                    createInstruction(OP, "add", tmp(10), "p", "3"),
                    createInstruction(MULTIJUMP, tmp(10), "0", "0"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(SET, tmp(13), "3"),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(SELECT, tmp(13), "lessThan", tmp(10), "3", "2", "5"),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(SELECT, tmp(13), "lessThan", tmp(10), "3", "1", "4"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(PRINT, tmp(13)),
                    createInstruction(OP, "add", tmp(14), "p", "4"),
                    createInstruction(SETADDR, ".array*rret", label(12)),
                    createInstruction(MULTICALL, tmp(14), "0"),
                    createInstruction(LABEL, label(12)),
                    createInstruction(PRINT, ".array*r"),
                    createInstruction(END),
                    createInstruction(MULTILABEL, label(0)),
                    createInstruction(SET, ".array*r", "1"),
                    createInstruction(RETURN, ".array*rret"),
                    createInstruction(MULTILABEL, label(1)),
                    createInstruction(SET, ".array*r", "2"),
                    createInstruction(RETURN, ".array*rret"),
                    createInstruction(MULTILABEL, label(2)),
                    createInstruction(SET, ".array*r", "3"),
                    createInstruction(RETURN, ".array*rret"),
                    createInstruction(MULTILABEL, label(3)),
                    createInstruction(SET, ".array*r", "4"),
                    createInstruction(RETURN, ".array*rret"),
                    createInstruction(MULTILABEL, label(4)),
                    createInstruction(SET, ".array*r", "5"),
                    createInstruction(RETURN, ".array*rret")
            );
        }

        @Test
        void convertsToLookupArrays() {
            assertCompilesTo("""
                            var array[5];
                            param p = 0;
                            
                            ++array[p];
                            print(array);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(LOOKUP, "team", tmp(2), "p"),
                    createInstruction(SENSOR, ".array*elem", tmp(2), "@name"),
                    createInstruction(READ, tmp(0), "@this", ".array*elem"),
                    createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                    createInstruction(WRITE, tmp(1), "@this", ".array*elem"),
                    createInstruction(PRINT, "derelict"),
                    createInstruction(PRINT, "sharded"),
                    createInstruction(PRINT, "crux"),
                    createInstruction(PRINT, "malis"),
                    createInstruction(PRINT, "green")
            );
        }

        @Test
        void convertsFiveLookupArrays() {
            assertCompilesTo("""
                            var a[5], b[5], c[5], d[5], e[5];
                            param p = 0;
                            
                            ++a[p];
                            ++b[p];
                            ++c[p];
                            ++d[p];
                            ++e[p];
                            print(a[0], b[0], c[0], d[0], e[0]);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(LOOKUP, "team", tmp(10), "p"),
                    createInstruction(SENSOR, ".a*elem", tmp(10), "@name"),
                    createInstruction(READ, tmp(0), "@this", ".a*elem"),
                    createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                    createInstruction(WRITE, tmp(1), "@this", ".a*elem"),
                    createInstruction(LOOKUP, "liquid", tmp(12), "p"),
                    createInstruction(SENSOR, ".b*elem", tmp(12), "@name"),
                    createInstruction(READ, tmp(2), "@this", ".b*elem"),
                    createInstruction(OP, "add", tmp(3), tmp(2), "1"),
                    createInstruction(WRITE, tmp(3), "@this", ".b*elem"),
                    createInstruction(LOOKUP, "item", tmp(14), "p"),
                    createInstruction(SENSOR, ".c*elem", tmp(14), "@name"),
                    createInstruction(READ, tmp(4), "@this", ".c*elem"),
                    createInstruction(OP, "add", tmp(5), tmp(4), "1"),
                    createInstruction(WRITE, tmp(5), "@this", ".c*elem"),
                    createInstruction(LOOKUP, "unit", tmp(16), "p"),
                    createInstruction(SENSOR, ".d*elem", tmp(16), "@name"),
                    createInstruction(READ, tmp(6), "@this", ".d*elem"),
                    createInstruction(OP, "add", tmp(7), tmp(6), "1"),
                    createInstruction(WRITE, tmp(7), "@this", ".d*elem"),
                    createInstruction(LOOKUP, "block", tmp(18), "p"),
                    createInstruction(SENSOR, ".e*elem", tmp(18), "@name"),
                    createInstruction(READ, tmp(8), "@this", ".e*elem"),
                    createInstruction(OP, "add", tmp(9), tmp(8), "1"),
                    createInstruction(WRITE, tmp(9), "@this", ".e*elem"),
                    createInstruction(PRINT, "derelict"),
                    createInstruction(PRINT, "water"),
                    createInstruction(PRINT, "copper"),
                    createInstruction(PRINT, "dagger"),
                    createInstruction(PRINT, "graphite-press")
            );
        }

        @Test
        void detectsLookupNameConflicts() {
            assertCompilesTo("""
                            #set goal = size;
                            const SIZE = 4;
                            var a[SIZE];
                            volatile mlog("crux") a1 = 0;
                            volatile mlog("oil") a2 = 0;
                            volatile mlog("lead") a3 = 0;
                            volatile mlog("mace") a4 = 0;
                            volatile mlog("multi-press") a5 = 0;
                            param p = 0;
                            print(++a[p]);
                            """,
                    createInstruction(SET, "crux", "0"),
                    createInstruction(SET, "oil", "0"),
                    createInstruction(SET, "lead", "0"),
                    createInstruction(SET, "mace", "0"),
                    createInstruction(SET, "multi-press", "0"),
                    createInstruction(SET, "p", "0"),
                    createInstruction(SELECT, tmp(2), "equal", "p", "0", q(".a*0"), q(".a*1")),
                    createInstruction(SELECT, tmp(3), "equal", "p", "2", q(".a*2"), q(".a*3")),
                    createInstruction(SELECT, ".a*elem", "lessThan", "p", "2", tmp(2), tmp(3)),
                    createInstruction(READ, tmp(0), "@this", ".a*elem"),
                    createInstruction(OP, "add", tmp(1), tmp(0), "1"),
                    createInstruction(WRITE, tmp(1), "@this", ".a*elem"),
                    createInstruction(PRINT, tmp(1))
            );
        }

        @Test
        void processesLookupDeclarations1() {
            assertCompilesTo("""
                            mlog(:team) var a[5];
                            var b[5];
                            param p = 0;
                            a[p] = b[p];
                            print(a[p], b[p]);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(LOOKUP, "liquid", tmp(5), "p"),
                    createInstruction(SENSOR, ".b*elem", tmp(5), "@name"),
                    createInstruction(READ, tmp(1), "@this", ".b*elem"),
                    createInstruction(LOOKUP, "team", tmp(6), "p"),
                    createInstruction(SENSOR, ".a*elem", tmp(6), "@name"),
                    createInstruction(WRITE, tmp(1), "@this", ".a*elem"),
                    createInstruction(READ, tmp(2), "@this", ".a*elem"),
                    createInstruction(PRINT, tmp(2)),
                    createInstruction(READ, tmp(4), "@this", ".b*elem"),
                    createInstruction(PRINT, tmp(4))
            );
        }

        @Test
        void processesLookupDeclarations2() {
            assertCompilesTo("""
                            mlog(:team) var a[5];
                            var b[5];
                            param p = 0;
                            a[p] = b[p];
                            print(a, b);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(LOOKUP, "liquid", tmp(2), "p"),
                    createInstruction(SENSOR, ".b*elem", tmp(2), "@name"),
                    createInstruction(READ, tmp(1), "@this", ".b*elem"),
                    createInstruction(LOOKUP, "team", tmp(3), "p"),
                    createInstruction(SENSOR, ".a*elem", tmp(3), "@name"),
                    createInstruction(WRITE, tmp(1), "@this", ".a*elem"),
                    createInstruction(PRINT, "derelict"),
                    createInstruction(PRINT, "sharded"),
                    createInstruction(PRINT, "crux"),
                    createInstruction(PRINT, "malis"),
                    createInstruction(PRINT, "green"),
                    createInstruction(PRINT, "water"),
                    createInstruction(PRINT, "slag"),
                    createInstruction(PRINT, "oil"),
                    createInstruction(PRINT, "cryofluid"),
                    createInstruction(PRINT, "neoplasm")
            );
        }
    }
}