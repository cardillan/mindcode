package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record JumpTable(String tableId, boolean usesTextTable, LogicLabel label, LogicLabel marker,
                        List<LogicInstruction> instructions, List<LogicLabel> branchLabels) {

}
