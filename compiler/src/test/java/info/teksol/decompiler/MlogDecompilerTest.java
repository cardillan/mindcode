package info.teksol.decompiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MlogDecompilerTest {

    private String decompile(String mlog) {
        return MlogDecompiler.decompile(mlog, false);
    }

    @Test
    void decompilesDrawClear() {
        assertEquals("""
                            clear(r, g, b);
                        """,
                decompile("""
                        draw clear r g b 0 0 0
                        """));
    }

    @Test
    void decompilesDrawPrint() {
        assertEquals("""
                            drawPrint(x, y, bottom);
                        """,
                decompile("""
                        draw print x y bottom 0 0 0
                        """));
    }

    @Test
    void decompilesDrawflush() {
        assertEquals("""
                            drawflush(display1);
                        """,
                decompile("""
                        drawflush display1
                        """));
    }

    @Test
    void decompilesAddition() {
        assertEquals("""
                            a = b + c;
                        """,
                decompile("""
                        op add a b c
                        """));
    }

    @Test
    void collapsesExpressions() {
        assertEquals("""
                            e = (a + b) + d;
                        """,
                decompile("""
                        op add c a b
                        op add e c d
                        """));
    }

    @Test
    void leavesNonlinearExpressions() {
        assertEquals("""
                            c = a + b;
                        // label1:
                            e = c + d;
                        """,
                decompile("""
                        op add c a b
                        label1:
                        op add e c d
                        """));
    }

    @Test
    void collapsesExpressionTwice() {
        assertEquals("""
                            print((((a + b) * (a + b)) + ((a + b) * (a + b))));
                        """,
                decompile("""
                        op add c a b
                        op mul d c c
                        op add f d d
                        print f
                        """));
    }

    @Test
    void propagatesExpressionTwice() {
        assertEquals("""
                            print((a + b));
                            print(((a + b) * (a + b)));
                        """,
                decompile("""
                        op add c a b
                        op mul d c c
                        print c
                        print d
                        """));
    }

    @Test
    void testActualOutput() {
        assertEquals("""
                            goto label0;
                            print("Configurable options:");
                        // label0:
                            MEMORY = cell1;
                            UNIT = @poly;
                            SW_X = 0;
                            SW_Y = 0;
                            NE_X = @mapw;
                            NE_Y = @maph;
                            DOWNGRADE = false;
                            WIDTH = 10;
                            RADIUS = 10;
                            goto label1;
                            print("Don't modify anything below this line.");
                        // label1:
                            TOTAL = 0;
                            __tmp0 = MEMORY[0];
                        if !__tmp0 then goto label2;
                            __fn0_n = MEMORY[1];
                            __fn0retval = min(max(__fn0_n, SW_X), NE_X);
                            x = __fn0retval;
                            __fn0_n = MEMORY[2];
                            __fn0retval = min(max(__fn0_n, SW_Y), NE_Y);
                            y = __fn0retval;
                            dx = -1;
                            __tmp6 = MEMORY[3];
                        if __tmp6 != 1 then goto label3;
                            dx = 1;
                        // label3:
                            dy = -1 * WIDTH;
                            __tmp9 = MEMORY[4];
                        if __tmp9 <= 0 then goto label4;
                            dy = WIDTH;
                        // label4:
                            TOTAL = MEMORY[5];
                            goto label5;
                        // label2:
                            MEMORY[0] = true;
                            MEMORY[1] = SW_X;
                            x = SW_X;
                            MEMORY[2] = SW_Y;
                            y = SW_Y;
                            dx = 1;
                            dy = WIDTH;
                        // label5:
                            __tmp14 = @conveyor;
                        if !DOWNGRADE then goto label6;
                            __tmp14 = @titanium-conveyor;
                        // label6:
                            oldType = __tmp14;
                            __tmp15 = @titanium-conveyor;
                        if !DOWNGRADE then goto label7;
                            __tmp15 = @conveyor;
                        // label7:
                            newType = __tmp15;
                            __tmp32 = WIDTH \\ 2;
                            __tmp16 = switch1.sensor(@enabled);
                        if __tmp16 then goto label8;
                        // label22:
                            __tmp18 = @unit.sensor(@dead);
                            __tmp21 = @unit.sensor(@controller);
                        if !(((__tmp18 === 0) == false) | (__tmp21 != @this)) then goto label9;
                        // label13:
                            ubind(UNIT);
                        if @unit != null then goto label10;
                            print("No unit of type ");
                            print(UNIT);
                            print(" found.");
                            goto label11;
                        // label10:
                            __tmp28 = @unit.sensor(@controlled);
                        if __tmp28 == 0 then goto label12;
                            print("Looking for a free ");
                            print(UNIT);
                            print("...");
                            goto label11;
                        // label12:
                            flag(rand(1000));
                            goto label9;
                        // label11:
                            printflush(message1);
                            goto label13;
                        // label9:
                            __fn0retval = min(max((y + __tmp32), SW_Y), NE_Y);
                            ypos = __fn0retval;
                            __tmp35 = within(x, __fn0retval, RADIUS);
                        if __tmp35 then goto label14;
                        // label15:
                            move(x, __fn0retval);
                            __tmp35 = within(x, __fn0retval, RADIUS);
                        if !__tmp35 then goto label15;
                        // label14:
                            yrep = y;
                        if y > min(NE_Y, ((y + WIDTH) - 1)) then goto label16;
                        // label19:
                            __fn2_building = getBlock(x, yrep, out __fn2_type, out 0);
                        if __fn2_type != __tmp14 then goto label17;
                            __tmp46 = __fn2_building.sensor(@rotation);
                            build(x, yrep, __tmp15, __tmp46, 0);
                        // label18:
                            0 = getBlock(x, yrep, out __fn2_type, out 0);
                        if __fn2_type != __tmp15 then goto label18;
                            TOTAL = TOTAL + 1;
                        // label17:
                            yrep = yrep + 1;
                        if yrep <= __tmp40 then goto label19;
                        // label16:
                            MEMORY[5] = TOTAL;
                            print("Position: ");
                            print(x);
                            print(", ");
                            print(__fn0retval);
                            print("\\nUpgrades: ");
                            print(TOTAL);
                            print("\\n");
                            printflush(message1);
                            x = x + dx;
                        if !((x > NE_X) | (x < SW_X)) then goto label20;
                            dx = -1 * dx;
                            y = y + dy;
                            __fn0retval = min(max(x, SW_X), NE_X);
                            x = __fn0retval;
                            MEMORY[3] = dx;
                        if !((y > NE_Y) | (y < SW_Y)) then goto label21;
                            dy = -1 * dy;
                            __fn0retval = min(max(y, SW_Y), NE_Y);
                            y = __fn0retval;
                            MEMORY[4] = dy;
                        // label21:
                            MEMORY[2] = y;
                        // label20:
                            MEMORY[1] = x;
                            __tmp16 = switch1.sensor(@enabled);
                        if !__tmp16 then goto label22;
                        // label8:
                            end();
                            print("Compiled by Mindcode - github.com/cardillan/mindcode");
                        """,
                decompile("""
                        jump 2 always 0 0
                        print "Configurable options:"
                        set MEMORY cell1
                        set UNIT @poly
                        set SW_X 0
                        set SW_Y 0
                        set NE_X @mapw
                        set NE_Y @maph
                        set DOWNGRADE false
                        set WIDTH 10
                        set RADIUS 10
                        jump 13 always 0 0
                        print "Don't modify anything below this line."
                        set TOTAL 0
                        read __tmp0 MEMORY 0
                        jump 34 equal __tmp0 false
                        read __fn0_n MEMORY 1
                        op max __tmp65 __fn0_n SW_X
                        op min __fn0retval __tmp65 NE_X
                        set x __fn0retval
                        read __fn0_n MEMORY 2
                        op max __tmp65 __fn0_n SW_Y
                        op min __fn0retval __tmp65 NE_Y
                        set y __fn0retval
                        set dx -1
                        read __tmp6 MEMORY 3
                        jump 28 notEqual __tmp6 1
                        set dx 1
                        op mul dy -1 WIDTH
                        read __tmp9 MEMORY 4
                        jump 32 lessThanEq __tmp9 0
                        set dy WIDTH
                        read TOTAL MEMORY 5
                        jump 41 always 0 0
                        write true MEMORY 0
                        write SW_X MEMORY 1
                        set x SW_X
                        write SW_Y MEMORY 2
                        set y SW_Y
                        set dx 1
                        set dy WIDTH
                        set __tmp14 @conveyor
                        jump 44 equal DOWNGRADE false
                        set __tmp14 @titanium-conveyor
                        set oldType __tmp14
                        set __tmp15 @titanium-conveyor
                        jump 48 equal DOWNGRADE false
                        set __tmp15 @conveyor
                        set newType __tmp15
                        op idiv __tmp32 WIDTH 2
                        sensor __tmp16 switch1 @enabled
                        jump 132 notEqual __tmp16 false
                        sensor __tmp18 @unit @dead
                        op strictEqual __tmp19 __tmp18 0
                        op equal __tmp20 __tmp19 false
                        sensor __tmp21 @unit @controller
                        op notEqual __tmp22 __tmp21 @this
                        op or __tmp23 __tmp20 __tmp22
                        jump 76 equal __tmp23 false
                        ubind UNIT
                        jump 65 notEqual @unit null
                        print "No unit of type "
                        print UNIT
                        print " found."
                        jump 74 always 0 0
                        sensor __tmp28 @unit @controlled
                        jump 71 equal __tmp28 0
                        print "Looking for a free "
                        print UNIT
                        print "..."
                        jump 74 always 0 0
                        op rand __tmp31 1000 0
                        ucontrol flag __tmp31 0 0 0 0
                        jump 76 always 0 0
                        printflush message1
                        jump 59 always 0 0
                        op add __fn0_n y __tmp32
                        op max __tmp65 __fn0_n SW_Y
                        op min __fn0retval __tmp65 NE_Y
                        set ypos __fn0retval
                        ucontrol within x __fn0retval RADIUS __tmp35 0
                        jump 85 notEqual __tmp35 false
                        ucontrol move x __fn0retval 0 0 0
                        ucontrol within x __fn0retval RADIUS __tmp35 0
                        jump 82 equal __tmp35 false
                        op add __tmp37 y WIDTH
                        op sub __tmp38 __tmp37 1
                        op min __tmp40 NE_Y __tmp38
                        set yrep y
                        jump 99 greaterThan y __tmp40
                        ucontrol getBlock x yrep __fn2_type __fn2_building 0
                        jump 97 notEqual __fn2_type __tmp14
                        sensor __tmp46 __fn2_building @rotation
                        ucontrol build x yrep __tmp15 __tmp46 0
                        ucontrol getBlock x yrep __fn2_type 0 0
                        jump 94 notEqual __fn2_type __tmp15
                        op add TOTAL TOTAL 1
                        op add yrep yrep 1
                        jump 90 lessThanEq yrep __tmp40
                        write TOTAL MEMORY 5
                        print "Position: "
                        print x
                        print ", "
                        print __fn0retval
                        print "\\nUpgrades: "
                        print TOTAL
                        print "\\n"
                        printflush message1
                        op add x x dx
                        op greaterThan __tmp52 x NE_X
                        op lessThan __tmp53 x SW_X
                        op or __tmp54 __tmp52 __tmp53
                        jump 129 equal __tmp54 false
                        op mul dx -1 dx
                        op add y y dy
                        op max __tmp65 x SW_X
                        op min __fn0retval __tmp65 NE_X
                        set x __fn0retval
                        write dx MEMORY 3
                        op greaterThan __tmp59 y NE_Y
                        op lessThan __tmp60 y SW_Y
                        op or __tmp61 __tmp59 __tmp60
                        jump 128 equal __tmp61 false
                        op mul dy -1 dy
                        op max __tmp65 y SW_Y
                        op min __fn0retval __tmp65 NE_Y
                        set y __fn0retval
                        write dy MEMORY 4
                        write y MEMORY 2
                        write x MEMORY 1
                        sensor __tmp16 switch1 @enabled
                        jump 52 equal __tmp16 false
                        end
                        print "Compiled by Mindcode - github.com/cardillan/mindcode"
                        """));
    }
}