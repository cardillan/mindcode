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
                        message(notify, @wait)
                        message(mission, @wait)
                        message(announce, duration, result)
                        message(toast, duration, result)
                        """,
                createInstruction(MESSAGE, "notify", "0", "@wait"),
                createInstruction(MESSAGE, "mission", "0", "@wait"),
                createInstruction(MESSAGE, "announce", "duration", "result"),
                createInstruction(MESSAGE, "toast", "duration", "result"),
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
                        explosion(team, x, y, radius, damage, air, ground, pierce, true)
                        """,
                createInstruction(EXPLOSION, "team", "x", "y", "radius", "damage", "air", "ground", "pierce", "true"),
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

    @Test
    void generatesEffects() {
        assertCompilesTo("""
                        effect(warn, x, y)
                        effect(cross, x, y)
                        effect(blockFall, x, y, @vault)
                        effect(placeBlock, x, y, size)
                        effect(placeBlockSpark, x, y, size)
                        effect(breakBlock, x, y, size)
                        effect(spawn, x, y)
                        effect(trail, x, y, size, color)
                        effect(breakProp, x, y, size, color)
                        effect(smokeCloud, x, y, color)
                        effect(vapor, x, y, color)
                        effect(hit, x, y, color)
                        effect(hitSquare, x, y, color)
                        effect(shootSmall, x, y, rotation, color)
                        effect(shootBig, x, y, rotation, color)
                        effect(smokeSmall, x, y, color)
                        effect(smokeBig, x, y, color)
                        effect(smokeColor, x, y, rotation, color)
                        effect(smokeSquare, x, y, rotation, color)
                        effect(smokeSquareBig, x, y, rotation, color)
                        effect(spark, x, y, color)
                        effect(sparkBig, x, y, color)
                        effect(sparkShoot, x, y, rotation, color)
                        effect(sparkShootBig, x, y, rotation, color)
                        effect(drill, x, y, color)
                        effect(drillBig, x, y, color)
                        effect(lightBlock, x, y, size, color)
                        effect(explosion, x, y, size)
                        effect(smokePuff, x, y, color)
                        effect(sparkExplosion, x, y, color)
                        effect(crossExplosion, x, y, size, color)
                        effect(wave, x, y, size, color)
                        effect(bubble, x, y)
                        """,
                createInstruction(EFFECT, "warn", "x", "y"),
                createInstruction(EFFECT, "cross", "x", "y"),
                createInstruction(EFFECT, "blockFall", "x", "y", "0", "0", "@vault"),
                createInstruction(EFFECT, "placeBlock", "x", "y", "size"),
                createInstruction(EFFECT, "placeBlockSpark", "x", "y", "size"),
                createInstruction(EFFECT, "breakBlock", "x", "y", "size"),
                createInstruction(EFFECT, "spawn", "x", "y"),
                createInstruction(EFFECT, "trail", "x", "y", "size", "color"),
                createInstruction(EFFECT, "breakProp", "x", "y", "size", "color"),
                createInstruction(EFFECT, "smokeCloud", "x", "y", "0", "color"),
                createInstruction(EFFECT, "vapor", "x", "y", "0", "color"),
                createInstruction(EFFECT, "hit", "x", "y", "0", "color"),
                createInstruction(EFFECT, "hitSquare", "x", "y", "0", "color"),
                createInstruction(EFFECT, "shootSmall", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "shootBig", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "smokeSmall", "x", "y", "0", "color"),
                createInstruction(EFFECT, "smokeBig", "x", "y", "0", "color"),
                createInstruction(EFFECT, "smokeColor", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "smokeSquare", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "smokeSquareBig", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "spark", "x", "y", "0", "color"),
                createInstruction(EFFECT, "sparkBig", "x", "y", "0", "color"),
                createInstruction(EFFECT, "sparkShoot", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "sparkShootBig", "x", "y", "rotation", "color"),
                createInstruction(EFFECT, "drill", "x", "y", "0", "color"),
                createInstruction(EFFECT, "drillBig", "x", "y", "0", "color"),
                createInstruction(EFFECT, "lightBlock", "x", "y", "size", "color"),
                createInstruction(EFFECT, "explosion", "x", "y", "size"),
                createInstruction(EFFECT, "smokePuff", "x", "y", "0", "color"),
                createInstruction(EFFECT, "sparkExplosion", "x", "y", "0", "color"),
                createInstruction(EFFECT, "crossExplosion", "x", "y", "size", "color"),
                createInstruction(EFFECT, "wave", "x", "y", "size", "color"),
                createInstruction(EFFECT, "bubble", "x", "y"),
                createInstruction(END)
        );
    }
}
