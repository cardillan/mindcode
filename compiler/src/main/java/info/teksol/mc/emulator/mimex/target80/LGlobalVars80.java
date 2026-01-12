package info.teksol.mc.emulator.mimex.target80;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.LGlobalVarsBase;
import info.teksol.mc.mindcode.logic.mimex.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LGlobalVars80 extends LGlobalVarsBase {
    protected final LVar varTime;
    protected final LVar varTick;
    protected final LVar varSecond;
    protected final LVar varMinute;
    protected final LVar varWave;
    protected final LVar varWaveTime;
    protected final LVar varMapW;
    protected final LVar varMapH;

    public LGlobalVars80(MindustryMetadata metadata, boolean privileged) {
        super(metadata, privileged);

        varTime = getExisting("@time");
        varTick = getExisting("@tick");
        varSecond = getExisting("@second");
        varMinute = getExisting("@minute");
        varWave = getExisting("@waveNumber");
        varWaveTime = getExisting("@waveTime");
        varMapW = getExisting("@mapw");
        varMapH = getExisting("@maph");
    }

    protected void createContentVariables(MindustryMetadata metadata) {
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

        for (Unit type : metadata.getAllUnits()) {
            put(type.name(), type);
        }

        for (Weather weather : metadata.getAllWeathers()) {
            put(weather.name(), weather);
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
