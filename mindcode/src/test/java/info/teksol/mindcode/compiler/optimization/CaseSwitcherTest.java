package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class CaseSwitcherTest extends AbstractOptimizerTest<CaseSwitcher> {

    @Override
    protected Class<CaseSwitcher> getTestedClass() {
        return CaseSwitcher.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Test
    void processesBasicSwitch() {
        assertCompilesTo("""
                        i = rand(10);
                        case i
                            when 0 then print(0);
                            when 1 then print(1);
                            when 2 then print(2);
                            when 3 then print(3);
                            when 4 then print(4);
                            when 5 then print(5);
                            when 6 then print(6);
                            when 7 then print(7);
                            when 8 then print(8);
                            when 9 then print(9);
                            else print("oh no!");
                        end;
                        print("end");
                        """,
                createInstruction(OP, "rand", "i", "10"),
                createInstruction(JUMP, var(1021), "lessThan", "i", "0"),
                createInstruction(JUMP, var(1021), "greaterThan", "i", "9"),
                createInstruction(GOTOOFFSET, var(1023), "i", "0", var(1022)),
                createInstruction(GOTOLABEL, var(1023), var(1022)),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(GOTOLABEL, var(1024), var(1022)),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(GOTOLABEL, var(1025), var(1022)),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(GOTOLABEL, var(1026), var(1022)),
                createInstruction(JUMP, var(1008), "always"),
                createInstruction(GOTOLABEL, var(1027), var(1022)),
                createInstruction(JUMP, var(1010), "always"),
                createInstruction(GOTOLABEL, var(1028), var(1022)),
                createInstruction(JUMP, var(1012), "always"),
                createInstruction(GOTOLABEL, var(1029), var(1022)),
                createInstruction(JUMP, var(1014), "always"),
                createInstruction(GOTOLABEL, var(1030), var(1022)),
                createInstruction(JUMP, var(1016), "always"),
                createInstruction(GOTOLABEL, var(1031), var(1022)),
                createInstruction(JUMP, var(1018), "always"),
                createInstruction(GOTOLABEL, var(1032), var(1022)),
                createInstruction(JUMP, var(1020), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "0"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1006)),
                createInstruction(PRINT, "2"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1008)),
                createInstruction(PRINT, "3"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1010)),
                createInstruction(PRINT, "4"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1012)),
                createInstruction(PRINT, "5"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1014)),
                createInstruction(PRINT, "6"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1016)),
                createInstruction(PRINT, "7"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1018)),
                createInstruction(PRINT, "8"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1020)),
                createInstruction(PRINT, "9"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1021)),
                createInstruction(PRINT, q("oh no!")),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("end"))
        );
    }

    @Test
    void processesBasicSwitchNoElse() {
        assertCompilesTo("""
                        i = rand(10);
                        case i
                            when 0 then print(0);
                            when 1 then print(1);
                            when 2 then print(2);
                            when 3 then print(3);
                            when 4 then print(4);
                            when 5 then print(5);
                            when 6 then print(6);
                            when 7 then print(7);
                            when 8 then print(8);
                            when 9 then print(9);
                        end;
                        print("end");
                        """,
                createInstruction(OP, "rand", "i", "10"),
                createInstruction(JUMP, var(1021), "lessThan", "i", "0"),
                createInstruction(JUMP, var(1021), "greaterThan", "i", "9"),
                createInstruction(GOTOOFFSET, var(1023), "i", "0", var(1022)),
                createInstruction(GOTOLABEL, var(1023), var(1022)),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(GOTOLABEL, var(1024), var(1022)),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(GOTOLABEL, var(1025), var(1022)),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(GOTOLABEL, var(1026), var(1022)),
                createInstruction(JUMP, var(1008), "always"),
                createInstruction(GOTOLABEL, var(1027), var(1022)),
                createInstruction(JUMP, var(1010), "always"),
                createInstruction(GOTOLABEL, var(1028), var(1022)),
                createInstruction(JUMP, var(1012), "always"),
                createInstruction(GOTOLABEL, var(1029), var(1022)),
                createInstruction(JUMP, var(1014), "always"),
                createInstruction(GOTOLABEL, var(1030), var(1022)),
                createInstruction(JUMP, var(1016), "always"),
                createInstruction(GOTOLABEL, var(1031), var(1022)),
                createInstruction(JUMP, var(1018), "always"),
                createInstruction(GOTOLABEL, var(1032), var(1022)),
                createInstruction(JUMP, var(1020), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "0"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1006)),
                createInstruction(PRINT, "2"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1008)),
                createInstruction(PRINT, "3"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1010)),
                createInstruction(PRINT, "4"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1012)),
                createInstruction(PRINT, "5"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1014)),
                createInstruction(PRINT, "6"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1016)),
                createInstruction(PRINT, "7"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1018)),
                createInstruction(PRINT, "8"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1020)),
                createInstruction(PRINT, "9"),
                createInstruction(LABEL, var(1021)),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, q("end"))
        );
    }

    @Test
    void processesReturningSwitchWithElse() {
        assertCompilesTo("""
                        value = floor(rand(20));
                        text = case value
                            when 0 then "None";
                            when 1 then "One";
                            when 2, 3, 4 then "A few";
                            when 5, 6, 7, 8 then "Several";
                            when 10 then "Ten";
                            else "I don't known this number!";
                        end;
                        print(text);
                        """,
                createInstruction(OP, "rand", var(0), "20"),
                createInstruction(OP, "floor", "value", var(0)),
                createInstruction(JUMP, var(1011), "lessThan", "value", "0"),
                createInstruction(JUMP, var(1011), "greaterThan", "value", "10"),
                createInstruction(GOTOOFFSET, var(1013), "value", "0", var(1012)),
                createInstruction(GOTOLABEL, var(1013), var(1012)),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(GOTOLABEL, var(1014), var(1012)),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(GOTOLABEL, var(1015), var(1012)),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(GOTOLABEL, var(1016), var(1012)),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(GOTOLABEL, var(1017), var(1012)),
                createInstruction(JUMP, var(1006), "always"),
                createInstruction(GOTOLABEL, var(1018), var(1012)),
                createInstruction(JUMP, var(1008), "always"),
                createInstruction(GOTOLABEL, var(1019), var(1012)),
                createInstruction(JUMP, var(1008), "always"),
                createInstruction(GOTOLABEL, var(1020), var(1012)),
                createInstruction(JUMP, var(1008), "always"),
                createInstruction(GOTOLABEL, var(1021), var(1012)),
                createInstruction(JUMP, var(1008), "always"),
                createInstruction(GOTOLABEL, var(1022), var(1012)),
                createInstruction(JUMP, var(1011), "always"),
                createInstruction(GOTOLABEL, var(1023), var(1012)),
                createInstruction(JUMP, var(1010), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(2), q("None")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, var(2), q("One")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, var(2), q("A few")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1008)),
                createInstruction(SET, var(2), q("Several")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1010)),
                createInstruction(SET, var(2), q("Ten")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1011)),
                createInstruction(SET, var(2), q("I don't known this number!")),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, var(2))
        );
    }
}