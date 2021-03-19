package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LogicInstructionPrinterTest extends AbstractAstTest {
    @Test
    void printsURadarAndUControl() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("" +
                                                "target = uradar(enemy, ground, any, health, MIN_TO_MAX, BY_DISTANCE)\n" +
                                                "if target != null\n" +
                                                "  approach(target.x, target.y, 10)\n" +
                                                "  if within(target.x, target.y, 10)\n" +
                                                "    target(target.x, target.y, SHOOT)\n" +
                                                "  end\n" +
                                                "end\n"
                                        )
                                )
                        )
                )
        );
    }

    @Test
    void printsULocate() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("" +
                                                "ulocate(ore, @surge-alloy, outx, outy)\n" +
                                                "ulocate(building, core, ENEMY, outx, outy, outbuilding)\n" +
                                                "ulocate(spawn, outx, outy, outbuilding)\n" +
                                                "ulocate(damaged, outx, outy, outbuilding)\n"
                                        )
                                )
                        )
                )
        );
    }
}
