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
                        op sub *tmp0 :x 20
                        op sub *tmp1 :y 20
                        op add *tmp2 :x 20
                        op sub *tmp3 :y 20
                        op add *tmp4 :x 20
                        op sub *tmp5 :y 20
                        draw triangle *tmp0 *tmp1 *tmp2 *tmp3 *tmp4 *tmp5
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
                        set .STORAGE nucleus1
                        set .MSG message1
                        sensor *tmp0 .STORAGE @itemCapacity
                        set :capacity *tmp0
                        print "capacity: "
                        print :capacity
                        print "\\n"
                        set :n 0
                        op lessThan *tmp1 :n @links
                        jump 43 equal *tmp1 false
                        getlink *tmp2 :n
                        set :building *tmp2
                        sensor *tmp3 :building @type
                        set :type *tmp3
                        op equal *tmp4 :type @conveyor
                        op equal *tmp5 :type @titanium-conveyor
                        op or *tmp6 *tmp4 *tmp5
                        op equal *tmp7 :type @plastanium-conveyor
                        op or *tmp8 *tmp6 *tmp7
                        jump 40 equal *tmp8 false
                        sensor *tmp10 :building @firstItem
                        set :resource *tmp10
                        op notEqual *tmp11 :resource null
                        jump 37 equal *tmp11 false
                        sensor *tmp13 nucleus1 :resource
                        set :level *tmp13
                        set *tmp14 :building
                        op lessThan *tmp16 :level :capacity
                        control enabled *tmp14 *tmp16 0 0 0
                        print "\\n"
                        print :n
                        print ": "
                        print :resource
                        print " @ "
                        print :level
                        set *tmp12 :level
                        jump 38 always 0 0
                        set *tmp12 null
                        set *tmp9 *tmp12
                        jump 41 always 0 0
                        set *tmp9 null
                        op add :n :n 1
                        jump 8 always 0 0
                        printflush .MSG
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE));
    }
}
