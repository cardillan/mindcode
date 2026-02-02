package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@NullMarked
class AtomicBlockResolverTest {

    @Nested
    class ErrorDetection extends AbstractCodeOutputTest {

        @Override
        protected CompilerProfile createCompilerProfile() {
            return super.createCompilerProfile()
                    .setVolatileAtomic(true)
                    .setTarget(new Target("8"))
                    .setAllOptimizationLevels(OptimizationLevel.EXPERIMENTAL);
        }

        @Test
        void detectsLoops() {
            assertGeneratesMessage(
                    "The atomic block executes a loop.",
                    """
                            param n = 0;
                            atomic
                                for i in 0 ... n do
                                    cell1[i]++;
                                end;
                            end;
                            """);
        }

        @Test
        void detectsMlogLoops() {
            assertGeneratesMessage(
                    "The atomic block executes a loop.",
                    """
                            atomic
                                mlog {
                                    label:
                                    print "foo"
                                    jump label always
                                }
                            end;
                            """);
        }

        @Test
        void detectsNestedBlocks() {
            assertGeneratesMessage(
                    "Nested invocation of atomic blocks.",
                    """
                            noinline void foo()
                                atomic
                                    print("foo");
                                end;
                            end;
                            
                            atomic
                                foo();
                            end;
                            """);
        }

        @Test
        void detectsRecursiveCalls() {
            assertGeneratesMessage(
                    "The atomic block executes a recursive function call.",
                    """
                            allocate stack in cell1;
                            
                            void foo()
                                foo();
                            end;
                            
                            atomic
                                foo();
                            end;
                            """);
        }

        @Test
        void detectsWaits() {
            assertGeneratesMessage(
                    "The atomic block executes a 'wait' instruction.",
                    """
                            atomic
                                wait(1);
                            end;
                            """);
        }

        @Test
        void detectsLongBlock() {
            assertGeneratesMessage(
                    "The atomic block requires 11 steps or more, exceeding the maximum allowed number of 10 steps.",
                    """
                            volatile var x[10];
                            atomic
                                print(x);
                            end;
                            """);
        }

        @Test
        void detectsNoIptSet() {
            assertGeneratesMessage(
                    "Atomic blocks require processor IPT to be set on a world processor. Use the 'setrate' or 'ipt' compiler options.",
                    """
                            #set target = 8w;
                            atomic
                                print("atomic");
                            end;
                            """);
        }
    }

    @Nested
    class VolatileAtomic extends AbstractCodeOutputTest {

        @Override
        protected CompilerProfile createCompilerProfile() {
            return super.createCompilerProfile()
                    .setVolatileAtomic(true)
                    .setTarget(new Target("8"))
                    .setAllOptimizationLevels(OptimizationLevel.EXPERIMENTAL);
        }

        @Test
        void processesSimpleAtomicBlock() {
            // Note the print instruction isn't protected
            assertOutputs("""
                            atomic
                                i = cell1[0]++;
                                print(i);
                            end;
                            """,
                    """
                            wait 0.033334                           # 2.000 ticks for atomic execution of 4 steps at 2 ipt
                            read *tmp1 cell1 0
                            op add *tmp0 *tmp1 1
                            write *tmp0 cell1 0
                            print *tmp1                             # The last atomic block instruction
                            """
            );
        }

        @Test
        void processesNonlinearAtomicBlocks() {
            // The total size of the block exceeds 10 instructions, but the maximal code path does not.
            assertOutputs("""
                            param p = false;
                            volatile var x[5], y[5];
                            
                            atomic
                                if p then print(x); else print(y); end;
                            end;
                            """,
                    """
                            set p false
                            wait 0.058334                           # 3.500 ticks for atomic execution of 7 steps at 2 ipt
                            jump 9 equal p false
                            print .x*0
                            print .x*1
                            print .x*2
                            print .x*3
                            print .x*4
                            jump 0 always 0 0
                            print .y*0
                            print .y*1
                            print .y*2
                            print .y*3
                            print .y*4                              # The last atomic block instruction
                            """
            );
        }

        @Test
        void processesStacklessFunctionCalls() {
            assertOutputs("""
                            noinline void foo(in out x)
                                print(++x);
                            end;
                            
                            atomic
                                foo(in out cell1[0]);
                            end;
                            """,
                    """
                            wait 0.066667                           # 4.000 ticks for atomic execution of 8 steps at 2 ipt
                            read :foo:x cell1 0
                            set :foo*retaddr 4
                            jump 6 always 0 0
                            write :foo:x cell1 0                    # The last atomic block instruction
                            end
                            op add :foo:x :foo:x 1
                            print :foo:x
                            set @counter :foo*retaddr
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesStacklessFunctionCallsWithoutReturn() {
            // Note the return instruction is not protected
            assertOutputs("""
                            noinline void foo()
                                cell1[0]++;
                            end;
                            
                            atomic
                                foo();
                            end;
                            """,
                    """
                            wait 0.05                               # 3.000 ticks for atomic execution of 6 steps at 2 ipt
                            set :foo*retaddr 3
                            jump 4 always 0 0                       # The last atomic block instruction
                            end
                            read *tmp1 cell1 0
                            op add *tmp0 *tmp1 1
                            write *tmp0 cell1 0
                            set @counter :foo*retaddr
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesArrayAccess() {
            // Note that the entire array access is unprotected
            assertOutputs("""
                            #set use-lookup-arrays = false;
                            #set use-text-jump-tables = false;
                            var x[5];
                            
                            atomic
                                x[cell1[0]]++;
                            end;
                            print(x[0]);
                            """,
                    """
                            wait 0.016667                           # 1.000 ticks for atomic execution of 2 steps at 2 ipt
                            read *tmp1 cell1 0
                            op mul *tmp5 *tmp1 2
                            op add @counter *tmp5 4
                            set .x*elem ".x*0"
                            jump 13 always 0 0
                            set .x*elem ".x*1"
                            jump 13 always 0 0
                            set .x*elem ".x*2"
                            jump 13 always 0 0
                            set .x*elem ".x*3"
                            jump 13 always 0 0
                            set .x*elem ".x*4"
                            read *tmp3 @this .x*elem
                            op add *tmp3 *tmp3 1
                            write *tmp3 @this .x*elem               # The last atomic block instruction
                            print .x*0
                            end
                            draw triangle .x*1 .x*2 .x*3 .x*4 0 0
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesSharedArrayAccess() {
            // Note that the entire array access is unprotected
            assertOutputs("""
                            #set goal = size;
                            #set use-lookup-arrays = false;
                            #set use-text-jump-tables = false;
                            param p = 0;
                            var x[5];
                            
                            atomic
                                x[cell1[0]]++;
                            end;
                            x[p]++;
                            """,
                    """
                            set p 0
                            wait 0.016667                           # 1.000 ticks for atomic execution of 2 steps at 2 ipt
                            read *tmp1 cell1 0
                            set .x*ret 7
                            op mul .x*ind *tmp1 2
                            op mod *tmp7 .x*ind 6
                            op add @counter 18 *tmp7
                            read *tmp3 @this .x*elem
                            op add *tmp3 *tmp3 1
                            write *tmp3 @this .x*elem               # The last atomic block instruction
                            set .x*ret 14
                            op mul .x*ind p 2
                            op mod *tmp8 .x*ind 6
                            op add @counter 18 *tmp8
                            read *tmp5 @this .x*elem
                            op add *tmp5 *tmp5 1
                            write *tmp5 @this .x*elem
                            end
                            select .x*elem lessThan .x*ind 6 ".x*0" ".x*3"
                            set @counter .x*ret
                            select .x*elem lessThan .x*ind 6 ".x*1" ".x*4"
                            set @counter .x*ret
                            set .x*elem ".x*2"
                            set @counter .x*ret
                            draw triangle .x*0 .x*1 .x*2 .x*3 .x*4 0
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesCustomInstructions() {
            // All custom instructions are protected
            assertOutputs("""
                            atomic
                                mlog {
                                    foo bar                         # a custom instruction
                                    jump skip always
                                    print "a"
                                    print "b"
                                    print "c"
                                    skip:
                                    foo bar
                                }
                                mlogSafe("foo", in "bar");
                            end;
                            """,
                    """
                            wait 0.041667                           # 2.500 ticks for atomic execution of 5 steps at 2 ipt
                            foo bar                                 # a custom instruction
                            jump m0_skip always
                            print "a"
                            print "b"
                            print "c"
                            m0_skip:
                            foo bar
                            foo "bar"                               # The last atomic block instruction
                            """
            );
        }
    }

    @Nested
    class NonvolatileAtomic extends AbstractCodeOutputTest {

        @Override
        protected CompilerProfile createCompilerProfile() {
            return super.createCompilerProfile()
                    .setVolatileAtomic(false)
                    .setTarget(new Target("8"))
                    .setAllOptimizationLevels(OptimizationLevel.EXPERIMENTAL);
        }

        @Test
        void processesSimpleAtomicBlock() {
            assertOutputs("""
                            atomic
                                i = cell1[0]++;
                                print(i);
                            end;
                            """,
                    """
                            wait 0.041667                           # 2.500 ticks for atomic execution of 5 steps at 2 ipt
                            read *tmp1 cell1 0
                            op add *tmp0 *tmp1 1
                            write *tmp0 cell1 0
                            print *tmp1                             # The last atomic block instruction
                            """
            );
        }

        @Test
        void processesNonlinearAtomicBlocks() {
            // The total size of the block exceeds 10 instructions, but the maximal code path does not.
            assertOutputs("""
                            param p = false;
                            volatile var x[5], y[5];
                            
                            atomic
                                if p then print(x); else print(y); end;
                            end;
                            """,
                    """
                            set p false
                            wait 0.066667                           # 4.000 ticks for atomic execution of 8 steps at 2 ipt
                            jump 9 equal p false
                            print .x*0
                            print .x*1
                            print .x*2
                            print .x*3
                            print .x*4
                            jump 0 always 0 0
                            print .y*0
                            print .y*1
                            print .y*2
                            print .y*3
                            print .y*4                              # The last atomic block instruction
                            """
            );
        }

        @Test
        void processesStacklessFunctionCalls() {
            assertOutputs("""
                            noinline void foo(in out x)
                                print(++x);
                            end;
                            
                            atomic
                                foo(in out cell1[0]);
                            end;
                            """,
                    """
                            wait 0.066667                           # 4.000 ticks for atomic execution of 8 steps at 2 ipt
                            read :foo:x cell1 0
                            set :foo*retaddr 4
                            jump 6 always 0 0
                            write :foo:x cell1 0                    # The last atomic block instruction
                            end
                            op add :foo:x :foo:x 1
                            print :foo:x
                            set @counter :foo*retaddr
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesStacklessFunctionCallsWithoutReturn() {
            assertOutputs("""
                            noinline void foo()
                                cell1[0]++;
                            end;
                            
                            atomic
                                foo();
                            end;
                            """,
                    """
                            wait 0.058334                           # 3.500 ticks for atomic execution of 7 steps at 2 ipt
                            set :foo*retaddr 3
                            jump 4 always 0 0                       # The last atomic block instruction
                            end
                            read *tmp1 cell1 0
                            op add *tmp0 *tmp1 1
                            write *tmp0 cell1 0
                            set @counter :foo*retaddr
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesArrayAccess() {
            assertOutputs("""
                            #set use-lookup-arrays = false;
                            #set use-text-jump-tables = false;
                            var x[5];
                            
                            atomic
                                x[cell1[0]]++;
                            end;
                            print(x[0]);
                            """,
                    """
                            wait 0.075                              # 4.500 ticks for atomic execution of 9 steps at 2 ipt
                            read *tmp1 cell1 0
                            op mul *tmp5 *tmp1 2
                            op add @counter *tmp5 4
                            set .x*elem ".x*0"
                            jump 13 always 0 0
                            set .x*elem ".x*1"
                            jump 13 always 0 0
                            set .x*elem ".x*2"
                            jump 13 always 0 0
                            set .x*elem ".x*3"
                            jump 13 always 0 0
                            set .x*elem ".x*4"
                            read *tmp3 @this .x*elem
                            op add *tmp3 *tmp3 1
                            write *tmp3 @this .x*elem               # The last atomic block instruction
                            print .x*0
                            end
                            draw triangle .x*1 .x*2 .x*3 .x*4 0 0
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesSharedArrayAccess() {
            // Need a logic processor to fit the code into an atomic block
            assertOutputs("""
                            #set goal = size;
                            #set target = 8l;
                            #set use-lookup-arrays = false;
                            #set use-text-jump-tables = false;
                            param p = 0;
                            var x[5];
                            
                            atomic
                                x[cell1[0]]++;
                            end;
                            x[p]++;
                            """,
                    """
                            set p 0
                            wait 0.022917                           # 1.375 ticks for atomic execution of 11 steps at 8 ipt
                            read *tmp1 cell1 0
                            set .x*ret 7
                            op mul .x*ind *tmp1 2
                            op mod *tmp7 .x*ind 6
                            op add @counter 18 *tmp7
                            read *tmp3 @this .x*elem
                            op add *tmp3 *tmp3 1
                            write *tmp3 @this .x*elem               # The last atomic block instruction
                            set .x*ret 14
                            op mul .x*ind p 2
                            op mod *tmp8 .x*ind 6
                            op add @counter 18 *tmp8
                            read *tmp5 @this .x*elem
                            op add *tmp5 *tmp5 1
                            write *tmp5 @this .x*elem
                            end
                            select .x*elem lessThan .x*ind 6 ".x*0" ".x*3"
                            set @counter .x*ret
                            select .x*elem lessThan .x*ind 6 ".x*1" ".x*4"
                            set @counter .x*ret
                            set .x*elem ".x*2"
                            set @counter .x*ret
                            draw triangle .x*0 .x*1 .x*2 .x*3 .x*4 0
                            print "%s"
                            """.formatted(CompilerProfile.SIGNATURE)
            );
        }

        @Test
        void processesCustomInstructions() {
            // All custom instructions are protected
            assertOutputs("""
                            atomic
                                mlog {
                                    foo bar                         # a custom instruction
                                    jump skip always
                                    print "a"
                                    print "b"
                                    print "c"
                                    skip:
                                    foo bar
                                }
                                mlogSafe("foo", in "bar");
                            end;
                            """,
                    """
                            wait 0.041667                           # 2.500 ticks for atomic execution of 5 steps at 2 ipt
                            foo bar                                 # a custom instruction
                            jump m0_skip always
                            print "a"
                            print "b"
                            print "c"
                            m0_skip:
                            foo bar
                            foo "bar"                               # The last atomic block instruction
                            """
            );
        }
    }
}
