package info.teksol.mc.mindcode.compiler.optimization;

//class LoopUnrollerTest {
//
//    private static Predicate<LogicInstruction> excludeLabelInstructions() {
//        return ix -> !(ix instanceof LabelInstruction);
//    }
//
//    private static class LoopUnrollerTestBase extends AbstractOptimizerTest<LoopUnroller> {
//    }
//
//    @Nested
//    class BasicOptimizationsTest extends LoopUnrollerTestBase {
//        public BasicOptimizationsTest() {
//            super(OptimizationLevel.BASIC);
//        }
//
//        @Test
//        void ignoresDegenerateLoops() {
//            assertCompilesTo("""
//                            i = 0;
//                            while i < 1000 do
//                                print(i);
//                            end;
//                            """,
//                    createInstruction(PRINT, "0")
//            );
//        }
//
//        @Test
//        void preservesNonIntegerLoops() {
//            assertCompilesTo("""
//                            for i = 0; i <= 1.0; i += 0.1 do
//                                print(i);
//                            end;
//                            """,
//                    createInstruction(SET, "i", "0"),
//                    createInstruction(LABEL, var(1003)),
//                    createInstruction(PRINT, "i"),
//                    createInstruction(OP, "add", "i", "i", "0.1"),
//                    createInstruction(JUMP, var(1003), "lessThanEq", "i", "1")
//            );
//        }
//
//        @Test
//        void preservesShiftingLoop() {
//            assertCompilesTo("""
//                            for k = 1; k < 100000; k <<= 1 do
//                                print($" $k");
//                            end;
//                            """,
//                    createInstruction(SET, "k", "1"),
//                    createInstruction(LABEL, var(1003)),
//                    createInstruction(PRINT, q(" ")),
//                    createInstruction(PRINT, "k"),
//                    createInstruction(OP, "shl", "k", "k", "1"),
//                    createInstruction(JUMP, var(1003), "lessThan", "k", "100000")
//            );
//        }
//
//        @Test
//        void preservesBranchedIterations() {
//            assertCompilesTo("""
//                            i = 0;
//                            while i < 10 do
//                                i = i + (i % 2 ? 1 : 2);
//                                print(i);
//                            end;
//                            """,
//                    createInstruction(SET, "i", "0"),
//                    createInstruction(LABEL, var(1005)),
//                    createInstruction(OP, "mod", var(1), "i", "2"),
//                    createInstruction(JUMP, var(1003), "equal", var(1), "false"),
//                    createInstruction(OP, "add", "i", "i", "1"),
//                    createInstruction(JUMP, var(1004), "always"),
//                    createInstruction(LABEL, var(1003)),
//                    createInstruction(OP, "add", "i", "i", "2"),
//                    createInstruction(LABEL, var(1004)),
//                    createInstruction(PRINT, "i"),
//                    createInstruction(JUMP, var(1005), "lessThan", "i", "10")
//            );
//        }
//
//        @Test
//        void preservesEmptyLoops() {
//            assertCompilesTo("""
//                    for i = -5; i < -10; i += 1 do
//                        print("a");
//                    end;
//                    """
//            );
//        }
//
//        @Test
//        void preservesNestedLoops() {
//            assertCompilesTo("""
//                            i = 0;
//                            while i < 10 do
//                                while i < 5 do
//                                    i += 1;
//                                end;
//                                print(i);
//                            end;
//                            """,
//                    createInstruction(SET, "i", "0"),
//                    createInstruction(LABEL, var(1006)),
//                    createInstruction(JUMP, var(1005), "greaterThanEq", "i", "5"),
//                    createInstruction(LABEL, var(1007)),
//                    createInstruction(OP, "add", "i", "i", "1"),
//                    createInstruction(JUMP, var(1007), "lessThan", "i", "5"),
//                    createInstruction(LABEL, var(1005)),
//                    createInstruction(PRINT, "i"),
//                    createInstruction(JUMP, var(1006), "lessThan", "i", "10")
//            );
//        }
//
//        @Test
//        void processesEntryCondition() {
//            assertCompilesTo("""
//                            #set loop-optimization = none;
//                            for i in 1 .. 3 do
//                                print(i);
//                            end;
//                            """,
//                    createInstruction(PRINT, q("123"))
//            );
//        }
//
//        @Test
//        void processesExitCondition() {
//            assertCompilesTo("""
//                            #set loop-optimization = none;
//                            i = 1;
//                            do
//                                print(i);
//                                i += 1;
//                            while i <= 3;
//                            """,
//                    createInstruction(PRINT, q("123"))
//            );
//        }
//
//        @Test
//        void unrollsComplexLoop() {
//            assertCompilesTo("""
//                            i = 0;
//                            while i < 6 do
//                                i += 2;
//                                print(i);
//                                i -= 1;
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("213243546576"))
//            );
//        }
//
//        @Test
//        void unrollsCStyleLoop() {
//            assertCompilesTo("""
//                            for i = 0; i < 10; i += 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("0123456789"))
//            );
//        }
//
//        @Test
//        void unrollsDecreasingLoopExclusive() {
//            assertCompilesTo("""
//                            for i = 9; i > 2; i -= 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("9876543"))
//            );
//        }
//
//        @Test
//        void unrollsDecreasingLoopInclusive() {
//            assertCompilesTo("""
//                            for i = 9; i >= 0; i -= 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("9876543210"))
//            );
//        }
//
//        @Test
//        void unrollsDoWhileLoop() {
//            assertCompilesTo("""
//                            i = 0;
//                            do
//                                print(i);
//                                i += 1;
//                            while i < 10;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("0123456789"))
//            );
//        }
//
//        @Test
//        void unrollsExclusiveRangeIterationLoop() {
//            assertCompilesTo("""
//                            for i in 0 ... 10 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("0123456789"))
//            );
//        }
//
//        @Test
//        void unrollsListIterationLoops() {
//            assertCompilesTo("""
//                            for i in 1, 2, 3 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("123"))
//            );
//        }
//
//        @Test
//        void unrollsInclusiveRangeIterationLoop() {
//            assertCompilesTo("""
//                            for i in 0 .. 9 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("0123456789"))
//            );
//        }
//
//        @Test
//        void unrollsListIterationLoopWithBreak() {
//            assertCompilesTo("""
//                            for i in 1, 2, 3 do
//                                print(i);
//                                if i == 2 then break; end;
//                            end;
//                            """,
//                    createInstruction(PRINT, q("12"))
//            );
//        }
//
//        @Test
//        void unrollsLoopWithExpressionInCondition() {
//            assertCompilesTo("""
//                            i = 0;
//                            while (i += 1) < 10 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("123456789"))
//            );
//        }
//
//        @Test
//        void unrollsLoopsWithBreak() {
//            assertCompilesTo("""
//                            for i in 0 .. 10 do
//                                if i == 6 then break; end;
//                                print(i);
//                            end;
//                            print(".");
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("012345."))
//            );
//        }
//
//        @Test
//        void unrollsLoopsWithContinue() {
//            assertCompilesTo("""
//                            for i in 0 .. 9 do
//                                if i % 2 then continue; end;
//                                print(i);
//                            end;
//                            print(".");
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("02468."))
//            );
//        }
//
//        @Test
//        void unrollsNegativeLoop() {
//            assertCompilesTo("""
//                            for i = -5; i > -10; i -= 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("-5-6-7-8-9"))
//            );
//        }
//
//        @Test
//        void unrollsNegativeLoop2() {
//            assertCompilesTo("""
//                            for i = -9; i <= -5; i += 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("-9-8-7-6-5"))
//            );
//        }
//
//        @Test
//        void unrollsNegativeLoop3() {
//            assertCompilesTo("""
//                            for i = 5; i >= -5; i -= 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("543210-1-2-3-4-5"))
//            );
//        }
//
//        @Test
//        void unrollsNestedLoops() {
//            assertCompilesTo("""
//                            for i in 1 .. 5 do
//                                for j in i .. 5 do
//                                    print(" ", 10 * i + j);
//                                end;
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q(" 11 12 13 14 15 22 23 24 25 33 34 35 44 45 55"))
//            );
//        }
//
//        @Test
//        void unrollsSingleIterationLoop() {
//            assertCompilesTo("""
//                            for i in 1 .. 1 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, "1")
//            );
//        }
//
//        @Test
//        void unrollsUnevenLoop() {
//            assertCompilesTo("""
//                            for i = 0; i < 3; i += 2 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("02"))
//            );
//        }
//
//        @Test
//        void unrollsUnevenLoop2() {
//            assertCompilesTo("""
//                            for i = 0; i <= 3; i += 2 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("02"))
//            );
//        }
//
//        @Test
//        void unrollsUnevenLoop3() {
//            assertCompilesTo("""
//                            for i = 0; i <= 4; i += 2 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("024"))
//            );
//        }
//
//        @Test
//        void unrollsUpdatesInConditions() {
//            assertCompilesTo("""
//                            i = 0;
//                            while (i += 1) < 10 do
//                                print(i);
//                            end;
//                            """,
//                    excludeLabelInstructions(),
//                    createInstruction(PRINT, q("123456789"))
//            );
//        }
//
//    }
//
//    @Nested
//    class FullOptimizationsTest extends LoopUnrollerTestBase {
//        public FullOptimizationsTest() {
//            super(OptimizationLevel.EXPERIMENTAL);
//        }
//
//    }
//}