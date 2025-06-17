package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstStatementList;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SetInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.util.TraceFile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class BaseOptimizerTest extends AbstractTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.OPTIMIZER;
    }

    private final AstContext testContext = mockAstContext.createChild(profile,
            new AstStatementList(EMPTY, List.of()),
            AstContextType.NONE);

    private final LogicInstruction
            ix0 = ip.createInstruction(testContext, Opcode.SET, a, P0),
            ix1 = ip.createInstruction(testContext, Opcode.LABEL, label0),
            ix2 = ip.createInstruction(testContext, Opcode.SET, c, P0),
            ix3 = ip.createInstruction(testContext, Opcode.SET, d, P0);

    private final List<LogicInstruction> program = new ArrayList<>(List.of(ix0, ix1, ix2));
    private final OptimizationContext oc = new OptimizationContext(TraceFile.NULL_TRACE, m -> {},
            profile, ip, new TestOptimizerContext(m -> {}),
            program, CallGraph.createEmpty(), AstContext.createRootNode(profile), false);
    private final DummyOptimizer test = new DummyOptimizer(oc);

    @Test
    void handlesInstructionAt() {
        assertEquals(ix0, test.instructionAt(0));
    }

    @Test
    void handlesNegativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> test.instructionAt(-1));
    }

    @Test
    void handlesIndexTooHigh() {
        assertThrows(IndexOutOfBoundsException.class, () -> test.instructionAt(3));
    }

    @Test
    void handlesInstructionBefore() {
        assertEquals(ix0, test.instructionBefore(ix1));
    }

    @Test
    void handlesInstructionBeforeFirst() {
        assertNull(test.instructionBefore(ix0));
    }

    @Test
    void handlesInstructionBeforeNonexistent() {
        assertThrows(NoSuchElementException.class, () -> test.instructionBefore(ix3));
    }

    @Test
    void handlesInstructionAfter() {
        assertEquals(ix2, test.instructionAfter(ix1));
    }

    @Test
    void handlesInstructionAfterLast() {
        assertNull(test.instructionAfter(ix2));
    }

    @Test
    void handlesInstructionAfterNonexistent() {
        assertThrows(NoSuchElementException.class, () -> test.instructionAfter(ix3));
    }

    @Test
    void handlesInstructionSublist() {
        assertEquals(List.of(ix1, ix2),test.instructionSubList(1, 3));
    }

    @Test
    void handlesInstructionStream() {
        assertEquals(program,test.instructionStream().toList());
    }

    @Test
    void handlesInstructionIndex() {
        assertEquals(2, test.instructionIndex(ix2));
    }

    @Test
    void handlesInstructionIndexNonexistent() {
        assertEquals(-1, test.instructionIndex(ix3));
    }

    @Test
    void handlesExistingInstructionIndex() {
        assertEquals(2, test.existingInstructionIndex(ix2));
    }

    @Test
    void handlesExistingInstructionIndexNonexistent() {
        assertThrows(NoSuchElementException.class, () -> test.existingInstructionIndex(ix3));
    }

    @Test
    void handlesFirstInstructionIndexFrom() {
        assertEquals(-1, test.firstInstructionIndex(2, LabelInstruction.class::isInstance));
    }

    @Test
    void handlesFirstInstructionIndex() {
        assertEquals(1, test.firstInstructionIndex(LabelInstruction.class::isInstance));
    }

    @Test
    void handlesFirstInstructionIndexNonexistent() {
        assertEquals(-1, test.firstInstructionIndex(JumpInstruction.class::isInstance));
    }

    @Test
    void handlesLastInstructionIndexFrom() {
        assertEquals(-1, test.lastInstructionIndex(0, LabelInstruction.class::isInstance));
    }

    @Test
    void handlesLastInstructionIndex() {
        assertEquals(1, test.lastInstructionIndex(LabelInstruction.class::isInstance));
    }

    @Test
    void handlesLastInstructionIndexNonexistent() {
        assertEquals(-1, test.lastInstructionIndex(JumpInstruction.class::isInstance));
    }

    @Test
    void handlesLabelledInstructionIndex() {
        assertEquals(2, test.labeledInstructionIndex(label0));
    }

    @Test
    void handlesLabelledInstructionIndexNonexistent() {
        assertThrows(MindcodeInternalError.class, () -> test.labeledInstructionIndex(label1));
    }

    @Test
    void handlesFirstInstructionFrom() {
        assertNull(test.firstInstruction(2, LabelInstruction.class::isInstance));
    }

    @Test
    void handlesFirstInstruction() {
        assertEquals(ix1, test.firstInstruction(LabelInstruction.class::isInstance));
    }

    @Test
    void handlesFirstInstructionNonexistent() {
        assertNull(test.firstInstruction(JumpInstruction.class::isInstance));
    }

    @Test
    void handlesLastInstructionFrom() {
        assertNull(test.lastInstruction(0, LabelInstruction.class::isInstance));
    }

    @Test
    void handlesLastInstruction() {
        assertEquals(ix1, test.lastInstruction(LabelInstruction.class::isInstance));
    }

    @Test
    void handlesLastInstructionNonexistent() {
        assertNull(test.lastInstruction(JumpInstruction.class::isInstance));
    }

    @Test
    void handlesLabelledInstruction() {
        assertEquals(ix2, test.labeledInstruction(label0));
    }

    @Test
    void handlesLabelledInstructionNonexistent() {
        assertThrows(MindcodeInternalError.class, () -> test.labeledInstruction(label1));
    }

    @Test
    void handlesInstructions() {
        assertEquals(List.of(ix0, ix2),test.instructions(SetInstruction.class::isInstance));
    }

    @Test
    void handlesInsertInstruction() {
        test.insertInstruction(1, ix3);
        assertEquals(List.of(ix0, ix3, ix1, ix2), program);
    }

    @Test
    void refusesRepeatedInserts() {
        assertThrows(MindcodeInternalError.class, () -> test.insertInstruction(1, ix2));
    }

    @Test
    void handlesInsertInstructions() {
        LogicList list = test.contextInstructions(mockAstContext).duplicate(true);
        test.insertInstructions(1, list);
        LabelInstruction duplicatedLabel = test.createLabel(mockAstContext, LogicLabel.symbolic("*label0"));
        assertEquals(List.of(ix0, ix0, duplicatedLabel, ix2, ix1, ix2), program);
    }

    @Test
    void handlesReplaceInstruction() {
        test.replaceInstruction(ix1, ix3);
        assertEquals(List.of(ix0, ix3, ix2), program);
    }

    @Test
    void refusesReplaceNonexistentInstruction() {
        assertThrows(NoSuchElementException.class, () -> test.replaceInstruction(ix3, ix3));
    }

    @Test
    void refusesReplaceInstructionWithDuplicate() {
        assertThrows(MindcodeInternalError.class, () -> test.replaceInstruction(ix1, ix2));
    }

    @Test
    void handlesRemoveInstruction() {
        test.removeInstruction(1);
        assertEquals(List.of(ix0, ix2), program);
    }

    @Test
    void handlesRemovePrevious() {
        test.removePrevious(ix2);
        assertEquals(List.of(ix0, ix2), program);
    }

    @Test
    void refusesRemovePreviousNonexistentAnchor() {
        assertThrows(NoSuchElementException.class, () -> test.removePrevious(ix3));
    }

    @Test
    void refusesRemovePreviousOnFirst() {
        assertThrows(IndexOutOfBoundsException.class, () -> test.removePrevious(ix0));
    }

    @Test
    void handlesRemoveFollowing() {
        test.removeFollowing(ix0);
        assertEquals(List.of(ix0, ix2), program);
    }

    @Test
    void refusesRemoveFollowingNonexistentAnchor() {
        assertThrows(NoSuchElementException.class, () -> test.removeFollowing(ix3));
    }

    @Test
    void refusesRemoveFollowingOnFirst() {
        assertThrows(IndexOutOfBoundsException.class, () -> test.removeFollowing(ix2));
    }

    @Test
    void handlesRemoveMatchingInstructions() {
        test.removeMatchingInstructions(LabelInstruction.class::isInstance);
        assertEquals(List.of(ix0, ix2), program);
    }

    @Test
    void obtainsContextLabel() {
        assertEquals(LogicLabel.symbolic("*label0"), test.obtainContextLabel(testContext));
    }

    @Test
    void obtainsExistingContextLabel() {
        LogicLabel ll1 = test.obtainContextLabel(testContext);
        LogicLabel ll2 = test.obtainContextLabel(testContext);
        assertSame(ll1, ll2, "obtainContextLabel unexpectedly created new label");
    }

    @Test
    void obtainsNewContextLabel() {
        LogicLabel ll1 = test.obtainContextLabel(testContext);
        LogicLabel ll2 = test.obtainContextLabel(mockAstContext);
        assertNotSame(ll1, ll2, "obtainContextLabel reused label from different context");
    }


    protected static class DummyOptimizer extends BaseOptimizer {
        DummyOptimizer(OptimizationContext optimizationContext) {
            super(Optimization.PRINT_MERGING, optimizationContext);
        }

        @Override
        protected boolean optimizeProgram(OptimizationPhase phase) {
            return false;
        }
    }
}