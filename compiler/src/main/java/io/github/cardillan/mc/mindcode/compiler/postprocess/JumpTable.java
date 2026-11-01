package io.github.cardillan.mc.mindcode.compiler.postprocess;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicLabel;
import io.github.cardillan.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record JumpTable(String tableId, boolean usesTextTable, LogicLabel label, LogicLabel marker,
                        List<LogicInstruction> instructions, List<LogicLabel> branchLabels) {

}
