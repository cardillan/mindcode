package io.github.cardillan.mindcode.compiler.postprocess;

import io.github.cardillan.mindcode.profile.CompilerProfile;
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
                        """.formatted(CompilerProfile.SIGNATURE_STATIC));
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
                        jump 42 equal *tmp1 false
                        getlink *tmp2 :n
                        set :building *tmp2
                        sensor *tmp3 :building @type
                        set :type *tmp3
                        set *tmp5 :type
                        jump 19 equal *tmp5 @conveyor
                        jump 19 equal *tmp5 @titanium-conveyor
                        jump 19 equal *tmp5 @plastanium-conveyor
                        jump 39 always 0 0
                        sensor *tmp6 :building @firstItem
                        set :resource *tmp6
                        op notEqual *tmp7 :resource null
                        jump 36 equal *tmp7 false
                        sensor *tmp9 nucleus1 :resource
                        set :level *tmp9
                        set *tmp10 :building
                        op lessThan *tmp11 :level :capacity
                        control enabled *tmp10 *tmp11 0 0 0
                        print "\\n"
                        print :n
                        print ": "
                        print :resource
                        print " @ "
                        print :level
                        set *tmp8 :level
                        jump 37 always 0 0
                        set *tmp8 null
                        set *tmp4 *tmp8
                        jump 40 always 0 0
                        set *tmp4 null
                        op add :n :n 1
                        jump 8 always 0 0
                        printflush .MSG
                        end
                        print "%s"
                        """.formatted(CompilerProfile.SIGNATURE_STATIC));
    }
}
