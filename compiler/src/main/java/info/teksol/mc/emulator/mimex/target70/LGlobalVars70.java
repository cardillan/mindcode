package info.teksol.mc.emulator.mimex.target70;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.LGlobalVarsBase;
import info.teksol.mc.mindcode.logic.mimex.*;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class LGlobalVars70 extends LGlobalVarsBase {
    protected final LVar varTime;
    protected final LVar varTick;
    protected final LVar varSecond;
    protected final LVar varMinute;
    protected final LVar varWave;
    protected final LVar varWaveTime;

    public LGlobalVars70(MindustryMetadata metadata) {
        super(metadata);

        varTime = getExisting("@time");
        varTick = getExisting("@tick");
        varSecond = getExisting("@second");
        varMinute = getExisting("@minute");
        varWave = getExisting("@waveNumber");
        varWaveTime = getExisting("@waveTime");
    }

    protected void createContentVariables(MindustryMetadata metadata) {
        Map<String, LVar> variables = new HashMap<>();

        //store base content
        for (Team team : metadata.getAllTeams()) {
            put(team.name(), team);
        }

        for (Item item : metadata.getAllItems()) {
            put(item.name(), item);
        }

        for (Liquid liquid : metadata.getAllLiquids()) {
            put(liquid.name(), liquid);
        }

        for (BlockType block : metadata.getAllBlocks()) {
            String name = block.name();
            //only register blocks that have no item equivalent (this skips sand)
            if(metadata.getItemByName(name) == null & !block.legacy()){
                put(name, block);
            }
        }

        //used as a special value for any environmental solid block
        put("@solid", Objects.requireNonNull(metadata.getBlockByName("@stone-wall")));

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
        varTime.numval = tick / 60.0 * 1000.0;
        varSecond.numval = tick / 60f;
        varMinute.numval = tick / 60f / 60f;
    }
}
