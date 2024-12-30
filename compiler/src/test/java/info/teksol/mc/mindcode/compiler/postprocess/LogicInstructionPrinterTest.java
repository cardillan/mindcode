package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

@NullMarked
class LogicInstructionPrinterTest extends AbstractCodeOutputTest {

    @Test
    void outputsDrawsTriangles() {
        assertOutputs("""
                        triangle(x - 20, y - 20, x + 20, y - 20, x + 20, y - 20);
                        """,
                """
                        op sub __tmp0 x 20
                        op sub __tmp1 y 20
                        op add __tmp2 x 20
                        op sub __tmp3 y 20
                        op add __tmp4 x 20
                        op sub __tmp5 y 20
                        draw triangle __tmp0 __tmp1 __tmp2 __tmp3 __tmp4 __tmp5
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE));
    }

    @Test
    void outputsRealLifeScript() {
        assertOutputs("""
                        STORAGE = nucleus1;
                        MSG = message1;
                        capacity = STORAGE.@itemCapacity;
                        
                        println("capacity: ", capacity);
                        
                        for n = 0 ; n < @links ; n += 1 do
                            building = getlink(n);
                            type = building.@type;
                            if type == @conveyor
                                    or type == @titanium-conveyor
                                    or type == @plastanium-conveyor then
                                resource = building.@firstItem;
                                if resource != null then
                                    level = nucleus1.sensor(resource);
                                    building.enabled = level < capacity;
                                    print("\\n", n, ": ", resource, " @ ", level);
                                end;
                            end;
                        end;
                        printflush(MSG);
                        """,
                """
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
                        jump 40 equal __tmp8 false
                        sensor __tmp10 building @firstItem
                        set resource __tmp10
                        op notEqual __tmp11 resource null
                        jump 37 equal __tmp11 false
                        sensor __tmp13 nucleus1 resource
                        set level __tmp13
                        set __tmp14 building
                        op lessThan __tmp16 level capacity
                        control enabled __tmp14 __tmp16 0 0 0
                        print "\\n"
                        print n
                        print ": "
                        print resource
                        print " @ "
                        print level
                        set __tmp12 level
                        jump 38 always 0 0
                        set __tmp12 null
                        set __tmp9 __tmp12
                        jump 41 always 0 0
                        set __tmp9 null
                        op add n n 1
                        jump 8 always 0 0
                        printflush MSG
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE));
    }
}
