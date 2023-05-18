package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class WorldProcessorFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesGetBlock() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETBLOCK, "floor", "result", "x", "y"),
                        createInstruction(PRINT, "result"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                result = getblock(floor, x, y)
                                print(result)
                                """
                        )
                )
        );
    }

    @Test
    void generatesSetBlock() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SETBLOCK, "floor", "to", "x", "y"),
                        createInstruction(SETBLOCK, "ore", "to", "x", "y"),
                        createInstruction(SETBLOCK, "block", "to", "x", "y", "team", "rotation"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                setblock(floor, to, x, y)
                                setblock(ore, to, x, y)
                                setblock(block, to, x, y, team, rotation)
                                """
                        )
                )
        );
    }

    @Test
    void generatesSpawn() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SPAWN, "unit", "x", "y", "rotation", "team", "result"),
                        createInstruction(PRINT, "result"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                result = spawn(unit, x, y, rotation, team)
                                print(result)
                                """
                        )
                )
        );
    }

    @Test
    void generatesStatus() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(STATUS, "false", "burning", "unit", "duration"),
                        createInstruction(STATUS, "true", "freezing", "unit"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                applyStatus(burning, unit, duration)
                                clearStatus(freezing, unit)
                                """
                        )
                )
        );
    }

    @Test
    void generatesSpawnWave() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SPAWNWAVE, "x", "y", "natural"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                spawnwave(x, y, natural)
                                """
                        )
                )
        );
    }

    @Test
    void generatesSetRule() {
        assertLogicInstructionsMatch(
                List.of(
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
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
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
                                """
                        )
                )
        );
    }

    @Test
    void generatesMessage() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(MESSAGE, "notify"),
                        createInstruction(MESSAGE, "mission"),
                        createInstruction(MESSAGE, "announce", "duration"),
                        createInstruction(MESSAGE, "toast", "duration"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                message(notify)
                                message(mission)
                                message(announce, duration)
                                message(toast, duration)
                                """
                        )
                )
        );
    }

    @Test
    void generatesCutscene() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(CUTSCENE, "pan", "x", "y", "speed"),
                        createInstruction(CUTSCENE, "zoom", "level"),
                        createInstruction(CUTSCENE, "stop"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                cutscene(pan, x, y, speed)
                                cutscene(zoom, level)
                                cutscene(stop)
                                """
                        )
                )
        );
    }

    @Test
    void generatesExplosion() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(EXPLOSION, "team", "x", "y", "radius", "damage", "air", "ground", "pierce"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                explosion(team, x, y, radius, damage, air, ground, pierce)
                                """
                        )
                )
        );
    }

    @Test
    void generatesSetrate() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SETRATE, "ipt"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                setrate(ipt)
                                """
                        )
                )
        );
    }

    @Test
    void generatesFetch() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(FETCH, "unitCount", "result", "team"),
                        createInstruction(FETCH, "playerCount", "result", "team"),
                        createInstruction(FETCH, "coreCount", "result", "team"),
                        createInstruction(FETCH, "buildCount", "result", "team", "0", "type"),
                        createInstruction(FETCH, "unit", "result", "team", "index"),
                        createInstruction(FETCH, "player", "result", "team", "index"),
                        createInstruction(FETCH, "core", "result", "team", "index"),
                        createInstruction(FETCH, "build", "result", "team", "index", "type"),
                        createInstruction(PRINT, "result"),
                        createInstruction(END)
                ),
                generateAndOptimize((
                                Seq) translateToAst("""
                                result = fetch(unitCount, team)
                                result = fetch(playerCount, team)
                                result = fetch(coreCount, team)
                                result = fetch(buildCount, team, type)
                                result = fetch(unit, team, index)
                                result = fetch(player, team, index)
                                result = fetch(core, team, index)
                                result = fetch(build, team, index, type)
                                print(result)
                                """
                        )
                )
        );
    }

    @Test
    void generatesGetflag() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETFLAG, "result", "flag"),
                        createInstruction(PRINT, "result"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                result = getflag(flag)
                                print(result)
                                """
                        )
                )
        );
    }

    @Test
    void generatesSetflag() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SETFLAG, "flag", "value"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                setflag(flag, value)
                                """
                        )
                )
        );
    }


    @Test
    void generatesSetProp() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SETPROP, "property", "object", "value"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                object.setprop(property, value)
                                """
                        )
                )
        );
    }

}
