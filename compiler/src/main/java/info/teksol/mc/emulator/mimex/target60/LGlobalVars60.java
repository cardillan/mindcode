package info.teksol.mc.emulator.mimex.target60;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.LGlobalVarsBase;
import info.teksol.mc.mindcode.logic.mimex.*;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class LGlobalVars60 extends LGlobalVarsBase {
    protected final long startTime = System.currentTimeMillis();
    protected final LVar varTime;
    protected final LVar varTick;

    public LGlobalVars60(MindustryMetadata metadata) {
        super(metadata);

        varTime = put("@time", startTime);
        varTick = put("@tick", 0);
    }

    protected void createContentVariables(MindustryMetadata metadata) {
        //store base content
        for (Item item : metadata.getAllItems()) {
            put(item.name(), item);
        }

        for (Liquid liquid : metadata.getAllLiquids()) {
            put(liquid.name(), liquid);
        }

        for (BlockType block : metadata.getAllBlocks()) {
            String name = block.name();
            if (metadata.isValidBuiltIn(name) && !"hidden".equals(block.visibility())) {
                put(name, block);
            }
        }

        //used as a special value for any environmental solid block
        put("@solid", Objects.requireNonNull(metadata.getBlockByName("@stone-wall")));
        put("@air", Objects.requireNonNull(metadata.getBlockByName("@air")));

        for (Unit type : metadata.getAllUnits()) {
            put(type.name(), type);
        }

        //store sensor constants
        for (LAccess sensor : metadata.getAllLAccesses()) {
            put(sensor.name(), sensor);
        }
    }

    @Override
    public void update(double tick) {
        varTick.numval = tick;
        varTime.numval = startTime + tick / 60 * 1000;
    }

    @Override
    public double getTime() {
        return varTime.numval;
    }
}
