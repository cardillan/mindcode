package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicInstructionPrinterTest extends AbstractGeneratorTest {
    @Test
    void printsURadarAndUControl() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(ip,
                        LogicInstructionLabelResolver.resolve(
                                ip,
                                generateInstructions("""
                                        target = uradar(enemy, ground, any, health, MIN_TO_MAX)
                                        if target != null
                                            approach(target.x, target.y, 10)
                                            if within(target.x, target.y, 10)
                                                target(target.x, target.y, SHOOT)
                                            end
                                        end
                                        """
                                ).instructions()
                        )
                )
        );
    }

    @Test
    void printsULocate() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(ip,
                        LogicInstructionLabelResolver.resolve(
                                ip,
                                generateInstructions("""
                                        ulocate(ore, @surge-alloy, outx, outy)
                                        ulocate(building, core, ENEMY, outx, outy, outbuilding)
                                        ulocate(spawn, outx, outy, outbuilding)
                                        ulocate(damaged, outx, outy, outbuilding)
                                        """
                                ).instructions()
                        )
                )
        );
    }


    @Test
    void realLifeScripts1() {
        TestCompiler compiler = createTestCompiler();
        assertThrows(MindcodeException.class, () ->
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                generateInstructions(compiler,
                                        "itemDrop(found, @silicon, @unit.totalItems)"
                                ).instructions()
                        )
                )
        );
    }

    @Test
    void realLifeScripts2() {
        TestCompiler compiler = createTestCompiler();
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                generateInstructions("""
                                        leader = getlink(0)
                                        count = 1
                                        while count < @links
                                            turret = getlink(count)
                                            turret.shoot(leader.shootX, leader.shootY, leader.shooting)
                                            count = count + 1
                                        end
                                        """
                                ).instructions()
                        )
                )
        );
    }

    @Test
    void correctlyDrawsTriangles() {
        TestCompiler compiler = createTestCompiler();
        assertEquals("""
                        op sub __tmp0 x 20
                        op sub __tmp1 y 20
                        op add __tmp2 x 20
                        op sub __tmp3 y 20
                        op add __tmp4 x 20
                        op sub __tmp5 y 20
                        draw triangle __tmp0 __tmp1 __tmp2 __tmp3 __tmp4 __tmp5
                        end
                        """,
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                generateInstructions("""
                                        triangle(x - 20, y - 20, x + 20, y - 20, x + 20, y - 20)
                                        """).instructions()
                        )
                )
        );
    }

    @Test
    void realLifeScripts() {
        TestCompiler compiler = createTestCompiler();
        assertEquals("""
                        set STORAGE nucleus1
                        set MSG message1
                        sensor __tmp0 STORAGE @itemCapacity
                        set capacity __tmp0
                        print "capacity: "
                        print capacity
                        print "\\n"
                        set n 0
                        op lessThan __tmp1 n @links
                        jump 43 equal __tmp1 false
                        getlink __tmp2 n
                        set building __tmp2
                        sensor __tmp3 building @type
                        set type __tmp3
                        op equal __tmp4 type @conveyor
                        op equal __tmp5 type @titanium-conveyor
                        op or __tmp6 __tmp4 __tmp5
                        op equal __tmp7 type @plastanium-conveyor
                        op or __tmp8 __tmp6 __tmp7
                        jump 39 equal __tmp8 false
                        sensor __tmp10 building @firstItem
                        set resource __tmp10
                        op notEqual __tmp11 resource null
                        jump 36 equal __tmp11 false
                        sensor __tmp13 nucleus1 @resource
                        set level __tmp13
                        op lessThan __tmp14 level capacity
                        control enabled building __tmp14 0 0 0
                        print "\\n"
                        print n
                        print ": "
                        print resource
                        print " @ "
                        print level
                        set __tmp12 level
                        jump 37 always 0 0
                        set __tmp12 null
                        set __tmp9 __tmp12
                        jump 40 always 0 0
                        set __tmp9 null
                        op add __tmp15 n 1
                        set n __tmp15
                        jump 8 always 0 0
                        printflush MSG
                        end
                        """,
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                generateInstructions("""
                                        STORAGE = nucleus1
                                        MSG = message1
                                        capacity = STORAGE.itemCapacity
                                                                                        
                                        print("capacity: ", capacity, "\\n")
                                                                                        
                                        for n = 0 ; n < @links ; n += 1
                                            building = getlink(n)
                                            type = building.type
                                            if type == @conveyor
                                                    || type == @titanium-conveyor
                                                    || type == @plastanium-conveyor
                                                resource = building.firstItem
                                                if resource != null
                                                    level = nucleus1.resource
                                                    building.enabled = level < capacity
                                                    print("\\n", n, ": ", resource, " @ ", level)
                                                end
                                            end
                                        end
                                        printflush(MSG)
                                        """
                                ).instructions()
                        )
                )
        );
    }
}
