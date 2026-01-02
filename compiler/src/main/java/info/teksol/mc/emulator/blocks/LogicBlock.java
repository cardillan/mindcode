package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.LExecutor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LogicBlock extends MindustryBuilding {
    private final LExecutor executor;

    public LogicBlock(String name, MindustryContent type, LExecutor executor) {
        super(name, type);
        this.executor = executor;
    }

    public @Nullable LVar getOptionalVar(String name) {
        return executor.getOptionalVar(name);
    }
}
