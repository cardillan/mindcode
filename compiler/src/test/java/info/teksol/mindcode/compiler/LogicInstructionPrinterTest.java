package info.teksol.mindcode.compiler;

import info.teksol.util.UnexpectedMessageException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Order(99)
class LogicInstructionPrinterTest extends AbstractGeneratorTest {
    @Test
    void printsURadarAndUControl() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(ip,
                        LogicInstructionLabelResolver.resolve(
                                ip,
                                profile,
                                generateInstructions("""
                                        target = uradar(enemy, ground, any, health, MIN_TO_MAX);
                                        if target != null then
                                            approach(target.@x, target.@y, 10);
                                            if within(target.@x, target.@y, 10) then
                                                target(target.@x, target.@y, SHOOT);
                                            end;
                                        end;
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
                                profile,
                                generateInstructions("""
                                        ulocate(ore, @surge-alloy, out x, out y);
                                        ulocate(building, core, ENEMY, out x, out y, out building);
                                        ulocate(spawn, out x, out y, out building);
                                        ulocate(damaged, out x, out y, out building);
                                        """
                                ).instructions()
                        )
                )
        );
    }


    @Test
    void realLifeScripts1() {
        TestCompiler compiler = createTestCompiler();
        assertThrows(UnexpectedMessageException.class, () ->
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                compiler.profile,
                                generateInstructions(compiler,
                                        "itemDrop(found, @silicon, @unit.@totalItems);"
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
                                compiler.profile,
                                generateInstructions("""
                                        leader = getlink(0);
                                        count = 1;
                                        while count < @links do
                                            turret = getlink(count);
                                            turret.shoot(leader.@shootX, leader.@shootY, leader.@shooting);
                                            count = count + 1;
                                        end;
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
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE),
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                compiler.profile,
                                generateInstructions("""
                                        triangle(x - 20, y - 20, x + 20, y - 20, x + 20, y - 20);
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
                        jump 44 equal __tmp1 false
                        getlink __tmp2 n
                        set building __tmp2
                        sensor __tmp3 building @type
                        set type __tmp3
                        op equal __tmp4 type @conveyor
                        op equal __tmp5 type @titanium-conveyor
                        op or __tmp6 __tmp4 __tmp5
                        op equal __tmp7 type @plastanium-conveyor
                        op or __tmp8 __tmp6 __tmp7
                        op notEqual __tmp9 __tmp8 false
                        jump 40 equal __tmp9 false
                        sensor __tmp11 building @firstItem
                        set resource __tmp11
                        op notEqual __tmp12 resource null
                        jump 37 equal __tmp12 false
                        sensor __tmp14 nucleus1 resource
                        set level __tmp14
                        op lessThan __tmp15 level capacity
                        control enabled building __tmp15 0 0 0
                        print "\\n"
                        print n
                        print ": "
                        print resource
                        print " @ "
                        print level
                        set __tmp13 level
                        jump 38 always 0 0
                        set __tmp13 null
                        set __tmp10 __tmp13
                        jump 41 always 0 0
                        set __tmp10 null
                        op add __tmp16 n 1
                        set n __tmp16
                        jump 8 always 0 0
                        printflush MSG
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE),
                LogicInstructionPrinter.toString(compiler.processor,
                        LogicInstructionLabelResolver.resolve(
                                compiler.processor,
                                compiler.profile,
                                generateInstructions("""
                                        STORAGE = nucleus1;
                                        MSG = message1;
                                        capacity = STORAGE.@itemCapacity;

                                        print("capacity: ", capacity, "\\n");

                                        for n = 0 ; n < @links ; n += 1 do
                                            building = getlink(n);
                                            type = building.@type;
                                            if type == @conveyor
                                                    || type == @titanium-conveyor
                                                    || type == @plastanium-conveyor then
                                                resource = building.@firstItem;
                                                if resource != null then
                                                    level = nucleus1.sensor(resource);
                                                    building.enabled = level < capacity;
                                                    print("\\n", n, ": ", resource, " @ ", level);
                                                end;
                                            end;
                                        end;
                                        printflush(MSG);
                                        """
                                ).instructions()
                        )
                )
        );
    }
}
