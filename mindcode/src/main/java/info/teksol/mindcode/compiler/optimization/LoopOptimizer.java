package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.List;

// TODO: doc
public class LoopOptimizer extends GlobalOptimizer {

    public LoopOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        int count = 0;

        LogicIterator iterator = createIterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof JumpInstruction closing && closing.isUnconditional()) {
                // If the target of this instruction is a conditional jump which jumps to a label right below this
                // instruction, this is a loop
                try (LogicIterator openingIterator = createIteratorAtLabelledInstruction(closing.getTarget())) {
                    // Gather instructions used to set up the loop
                    List<LogicInstruction> conditionInit = new ArrayList<>();

                    while (openingIterator.hasNext() && openingIterator.nextIndex() < iterator.nextIndex()) {

                        LogicInstruction openingIx = openingIterator.next();
                        if (openingIx instanceof JumpInstruction jump && jump.getCondition().hasInverse()) {
                            if (targetsLoopExit(jump, iterator) && isLocalized(conditionInit)) {
                                LogicLabel bodyLabel = getOpeningLabel(openingIterator);
                                insertInstructions(iterator, conditionInit);
                                iterator.set(createJump(bodyLabel, jump.getCondition().inverse(),
                                        jump.getFirstOperand(), jump.getSecondOperand()));
                                count++;
                                break;
                            }
                        } else if (openingIx instanceof OpInstruction op && openingIterator.peek(0) instanceof JumpInstruction jump
                                && op.getOperation().isCondition() && op.getResult().getType() == ArgumentType.TMP_VARIABLE
                                && jump.getCondition() == Condition.EQUAL && jump.getFirstOperand().equals(op.getResult())
                                && jump.getSecondOperand().equals(LogicBoolean.FALSE)) {
                            if (targetsLoopExit(jump, iterator) && isLocalized(conditionInit)) {
                                openingIterator.next(); // Read the jump instruction
                                LogicLabel bodyLabel = getOpeningLabel(openingIterator);
                                insertInstructions(iterator, conditionInit);
                                iterator.set(createJump(bodyLabel, op.getOperation().toCondition(),
                                        op.getFirstOperand(), op.getSecondOperand()));
                                count++;
                                break;
                            }
                        } else if (goal == GenerationGoal.SPEED) {
                            conditionInit.add(openingIx);
                            // TODO Compute the limit from saved instructions and available space
                            if (conditionInit.size() > 3) {
                                break;
                            }
                        } else {
                            // Goal is not speed: do not add instructions!
                            break;
                        }
                    }
                }
            }
        }
        iterator.close();

        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d loop jumps improved by %s.", count, getClass().getSimpleName());
        }

        return false;
    }

    private void insertInstructions(LogicIterator iterator, List<LogicInstruction> instructions) {
        if (!instructions.isEmpty()) {
            try (LogicIterator adder = iterator.copy()) {
                adder.previous();
                instructions.forEach(adder::add);
            }
        }
    }

    private boolean targetsLoopExit(JumpInstruction jump, LogicIterator iterator) {
        try (LogicIterator followingIterator = createIteratorAtLabel(jump.getTarget())) {
            return (iterator.nextIndex() <= followingIterator.nextIndex()
                    && iterator.between(followingIterator).allMatch(ix -> ix instanceof LabelInstruction));
        }
    }

    private LogicLabel getOpeningLabel(LogicIterator openingIterator) {
        if (openingIterator.next() instanceof LabelInstruction label) {
            return label.getLabel();
        } else {
            // Ensure there's a label immediately after opening
            LogicLabel label = instructionProcessor.nextLabel();
            openingIterator.previous(); // Undo next() in the if condition
            openingIterator.add(createLabel(label));
            return label;
        }
    }
}
