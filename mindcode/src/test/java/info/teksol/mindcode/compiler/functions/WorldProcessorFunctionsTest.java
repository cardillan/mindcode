package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorldProcessorFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesGetBlock() {
        assertCompilesTo("""
                        result = getblock(floor, x, y)
                        """,
                createInstruction(GETBLOCK, "floor", var(0), "x", "y"),
                createInstruction(SET, "result", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void generatesSetBlock() {
        assertCompilesTo("""
                        setblock(floor, to, x, y)
                        setblock(ore, to, x, y)
                        setblock(block, to, x, y, team, rotation)
                        """,
                createInstruction(SETBLOCK, "floor", "to", "x", "y"),
                createInstruction(SETBLOCK, "ore", "to", "x", "y"),
                createInstruction(SETBLOCK, "block", "to", "x", "y", "team", "rotation"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSpawn() {
        assertCompilesTo("""
                        result = spawn(unit, x, y, rotation, team)
                        """,
                createInstruction(SPAWN, "unit", "x", "y", "rotation", "team", var(0)),
                createInstruction(SET, "result", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void generatesStatus() {
        assertCompilesTo("""
                        applyStatus(burning, unit, duration)
                        clearStatus(freezing, unit)
                        """,
                createInstruction(STATUS, "false", "burning", "unit", "duration"),
                createInstruction(STATUS, "true", "freezing", "unit"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSpawnWave() {
        assertCompilesTo("""
                        spawnwave(x, y, natural)
                        """,
                createInstruction(SPAWNWAVE, "x", "y", "natural"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSetRule() {
        assertCompilesTo("""
                        setrule(currentWaveTime, value)
                        setrule(waveTimer, value)
                        setrule(waves, value)
                        setrule(wave, value)
                        setrule(waveSpacing, value)
                        setrule(waveSending, value)
                        setrule(attackMode, value)
                        setrule(enemyCoreBuildRadius, value)
                        setrule(dropZoneRadius, value)
                        setrule(unitCap, value)
                        setrule(mapArea, x, y, width, height)
                        setrule(lighting, value)
                        setrule(ambientLight, value)
                        setrule(solarMultiplier, value)
                        setrule(buildSpeed, value, team)
                        setrule(unitBuildSpeed, value, team)
                        setrule(unitCost, value, team)
                        setrule(unitDamage, value, team)
                        setrule(blockHealth, value, team)
                        setrule(blockDamage, value, team)
                        setrule(rtsMinWeight, value, team)
                        setrule(rtsMinSquad, value, team)
                        """,
                createInstruction(SETRULE, "currentWaveTime", "value"),
                createInstruction(SETRULE, "waveTimer", "value"),
                createInstruction(SETRULE, "waves", "value"),
                createInstruction(SETRULE, "wave", "value"),
                createInstruction(SETRULE, "waveSpacing", "value"),
                createInstruction(SETRULE, "waveSending", "value"),
                createInstruction(SETRULE, "attackMode", "value"),
                createInstruction(SETRULE, "enemyCoreBuildRadius", "value"),
                createInstruction(SETRULE, "dropZoneRadius", "value"),
                createInstruction(SETRULE, "unitCap", "value"),
                createInstruction(SETRULE, "mapArea", "0", "x", "y", "width", "height"),
                createInstruction(SETRULE, "lighting", "value"),
                createInstruction(SETRULE, "ambientLight", "value"),
                createInstruction(SETRULE, "solarMultiplier", "value"),
                createInstruction(SETRULE, "buildSpeed", "value", "team"),
                createInstruction(SETRULE, "unitBuildSpeed", "value", "team"),
                createInstruction(SETRULE, "unitCost", "value", "team"),
                createInstruction(SETRULE, "unitDamage", "value", "team"),
                createInstruction(SETRULE, "blockHealth", "value", "team"),
                createInstruction(SETRULE, "blockDamage", "value", "team"),
                createInstruction(SETRULE, "rtsMinWeight", "value", "team"),
                createInstruction(SETRULE, "rtsMinSquad", "value", "team"),
                createInstruction(END)
        );
    }

    @Test
    void generatesMessage() {
        assertCompilesTo("""
                        message(notify)
                        message(mission)
                        message(announce, duration)
                        message(toast, duration)
                        """,
                createInstruction(MESSAGE, "notify"),
                createInstruction(MESSAGE, "mission"),
                createInstruction(MESSAGE, "announce", "duration"),
                createInstruction(MESSAGE, "toast", "duration"),
                createInstruction(END)
        );
    }

    @Test
    void generatesCutscene() {
        assertCompilesTo("""
                        cutscene(pan, x, y, speed)
                        cutscene(zoom, level)
                        cutscene(stop)
                        """,
                createInstruction(CUTSCENE, "pan", "x", "y", "speed"),
                createInstruction(CUTSCENE, "zoom", "level"),
                createInstruction(CUTSCENE, "stop"),
                createInstruction(END)
        );
    }

    @Test
    void generatesExplosion() {
        assertCompilesTo("""
                        explosion(team, x, y, radius, damage, air, ground, pierce)
                        """,
                createInstruction(EXPLOSION, "team", "x", "y", "radius", "damage", "air", "ground", "pierce"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSetrate() {
        assertCompilesTo("""
                        setrate(ipt)
                        """,
                createInstruction(SETRATE, "ipt"),
                createInstruction(END)
        );
    }

    @Test
    void generatesFetch() {
        assertCompilesTo("""
                        result = fetch(unitCount, team)
                        result = fetch(playerCount, team)
                        result = fetch(coreCount, team)
                        result = fetch(buildCount, team, type)
                        result = fetch(unit, team, index)
                        result = fetch(player, team, index)
                        result = fetch(core, team, index)
                        result = fetch(build, team, index, type)
                        """,
                createInstruction(FETCH, "unitCount", var(0), "team"),
                createInstruction(SET, "result", var(0)),
                createInstruction(FETCH, "playerCount", var(1), "team"),
                createInstruction(SET, "result", var(1)),
                createInstruction(FETCH, "coreCount", var(2), "team"),
                createInstruction(SET, "result", var(2)),
                createInstruction(FETCH, "buildCount", var(3), "team", "0", "type"),
                createInstruction(SET, "result", var(3)),
                createInstruction(FETCH, "unit", var(4), "team", "index"),
                createInstruction(SET, "result", var(4)),
                createInstruction(FETCH, "player", var(5), "team", "index"),
                createInstruction(SET, "result", var(5)),
                createInstruction(FETCH, "core", var(6), "team", "index"),
                createInstruction(SET, "result", var(6)),
                createInstruction(FETCH, "build", var(7), "team", "index", "type"),
                createInstruction(SET, "result", var(7)),
                createInstruction(END)
        );
    }

    @Test
    void generatesGetflag() {
        assertCompilesTo("""
                        result = getflag(flag)
                        """,
                createInstruction(GETFLAG, var(0), "flag"),
                createInstruction(SET, "result", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void generatesSetflag() {
        assertCompilesTo("""
                        setflag(flag, value)
                        """,
                createInstruction(SETFLAG, "flag", "value"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSetProp() {
        assertCompilesTo("""
                        object.setprop(property, value)
                        """,
                createInstruction(SETPROP, "property", "object", "value"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSync() {
        assertCompilesTo("""
                        sync(GLOBAL)
                        """,
                createInstruction(SYNC, "GLOBAL"),
                createInstruction(END)
        );
    }

    @Test
    void refusesLocalVariableForSync() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        sync(local)
                        """
                )
        );
    }

    @Test
    void refusesLiteralForSync() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        sync(10)
                        """
                )
        );
    }
}
