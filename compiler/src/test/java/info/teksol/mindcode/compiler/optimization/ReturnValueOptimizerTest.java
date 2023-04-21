package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class ReturnValueOptimizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.RETURN_VALUE_OPTIMIZATION);

    @Test
    void optimizesFnRetVal() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, fn0retval),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, fn0retval),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesOtherVariable() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, tmp1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, tmp1),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesWithLocalJumps() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, tmp1),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(LABEL, label0),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, label0, Condition.ALWAYS),
                        createInstruction(LABEL, label0),
                        createInstruction(PRINT, tmp1),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesFunctionCallsWithoutFnRetVal() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, unit),
                createInstruction(CALLREC, bank1, label0, label1),
                createInstruction(LABEL, label1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(CALLREC, bank1, label0, label1),
                        createInstruction(LABEL, label1),
                        createInstruction(PRINT, unit),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresMultipleUses() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, tmp1),
                createInstruction(SET, a, retval0),
                createInstruction(SET, b, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, a, retval0),
                createInstruction(SET, retval0, tmp1),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonlinearCodeJumps() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, tmp1),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(SET, a, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonlinearCodeGoto() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(LABEL, label0).withMarker("marker"),
                createInstruction(SET, retval0, tmp0),
                createInstruction(GOTO, tmp1).withMarker("marker"),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonlinearCodeEnd() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, tmp1),
                createInstruction(END),
                createInstruction(SET, a, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresModifiedVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, a),
                createInstruction(SET, a, K1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresFunctionCallsWithFnRetVal() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, fn0retval),
                createInstruction(CALLREC, bank1, label0, label1),
                createInstruction(LABEL, label1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresFunctionCallsWithGlobals() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, C),
                createInstruction(CALLREC, bank1, label0, label1),
                createInstruction(LABEL, label1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresVolatiles() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, retval0, time),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void optimizesInlineFunction() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        def foo(x)
                            x
                        end
                        print(foo(1))
                        """)
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0_x", "1"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "__fn0_x"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesStacklessFunction() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        def foo(x)
                            x
                        end
                        print(foo(1), foo(2))
                        """)
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__fn0_x", "1"),
                        createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                        createInstruction(CALL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(0), "__fn0retval"),
                        createInstruction(SET, "__fn0_x", "2"),
                        createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                        createInstruction(CALL, var(1000)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, var(0)),
                        createInstruction(PRINT, "__fn0retval"),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0retval", "__fn0_x"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(GOTO, "__fn0retaddr"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesRecursiveFunction() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        allocate stack in bank1
                        def foo(x)
                            foo(x - 1)
                        end
                        print(foo(1))
                        """)
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__sp", "0"),
                        createInstruction(SET, "__fn0_x", "1"),
                        createInstruction(CALLREC, "bank1", var(1000), var(1001)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "__fn0retval"),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                        createInstruction(PUSH, "bank1", "__fn0_x"),
                        createInstruction(SET, "__fn0_x", var(1)),
                        createInstruction(CALLREC, "bank1", var(1000), var(1003)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(POP, "bank1", "__fn0_x"),
                        createInstruction(SET, "__fn0retval", "__fn0retval"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(RETURN, "bank1"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }


    @Test
    void ignoresVolatileInSCript() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        def foo(x)
                            @time
                        end
                        print(foo(1))
                        """)
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0_x", "1"),
                        createInstruction(SET, var(0), "@time"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, var(0)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}