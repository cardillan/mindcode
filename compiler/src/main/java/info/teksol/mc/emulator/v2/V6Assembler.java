package info.teksol.mc.emulator.v2;

import info.teksol.mc.mindcode.logic.mimex.*;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class V6Assembler extends LAssemblerBase {
    private static final int invalidNum = Integer.MIN_VALUE;

    public V6Assembler(LStrings strings) {
        super(new V6Strings(), MindustryMetadata.forVersion(ProcessorVersion.V6));
    }

    @Override
    protected Map<String, LVar> createGlobalVariables() {
        Map<String, LVar> variables = new HashMap<>();

        putConst(variables, "the end", null);

        //add default constants
        putConst(variables, "false", 0);
        putConst(variables, "true", 1);
        putConst(variables, "null", null);

        //special enums
        putConst(variables, "@ctrlProcessor", 1);
        putConst(variables, "@ctrlPlayer", 2);
        putConst(variables, "@ctrlFormation", 3);

        //store base content

        for (Item item : metadata.getAllItems()) {
            putConst(variables, "@" + item.name(), item);
        }

        for (Liquid liquid : metadata.getAllLiquids()) {
            putConst(variables, "@" + liquid.name(), liquid);
        }

        for (BlockType block : metadata.getAllBlocks()) {
            String name = "@" + block.name();
            if (metadata.isValidBuiltIn(name)) {
                putConst(variables, name, block);
            }
        }

        //used as a special value for any environmental solid block
        putConst(variables, "@solid", metadata.getBlockByName("@stone-wall"));
        putConst(variables, "@air", metadata.getBlockByName("@air"));

        for (Unit type : metadata.getAllUnits()) {
            putConst(variables, "@" + type.name(), type);
        }

        //store sensor constants
        for (LAccess sensor : metadata.getAllLAccesses()) {
            putConst(variables, "@" + sensor.name(), sensor);
        }

        return variables;
    }

    @Override
    protected void createDefaultVariables() {
        putVar("@counter").numval = 0;      //instruction counter
        putConst("@time", 0);               //unix timestamp
        putConst("@unit", null);            //currently controlled unit
        putConst("@this", null);            //reference to self
        putConst("@tick", 0);               //global tick
    }

    @Override
    protected double parseDouble(String symbol) {
        double d = parseDoubleInternal(symbol);
        return d == invalidNum ? Double.NaN : d;
    }

    private double parseDoubleInternal(String symbol) {
        //parse hex/binary syntax
        if (symbol.startsWith("0b")) return strings.parseLong(symbol, 2, 2, symbol.length(), invalidNum);
        if (symbol.startsWith("0x")) return strings.parseLong(symbol, 16, 2, symbol.length(), invalidNum);
        return strings.parseDouble(symbol, invalidNum);
    }
}
