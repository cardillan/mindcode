package info.teksol.mindcode.mindustry;

import java.util.List;

public enum ArgumentType {
    INPUT           (true),
    BLOCK           (true),
    LABEL           (true),
    OUTPUT,
    DRAW            ("clear", "color", "stroke", "line", "rect", "lineRect", "poly", "linePoly", "triangle", "image"),
    BLOCK_CONTROL   ("enabled", "shoot", "shootp", "configure", "color"),
    RADAR           ("any", "enemy", "ally", "player", "attacker", "flying", "boss", "ground"),
    SORT            ("distance", "health", "shield", "armor", "maxHealth"),
    SENSOR          (true, "@copper", "@lead", "@metaglass", "@graphite", "@sand", "@coal", "@titanium", "@thorium", "@scrap",
                    "@silicon", "@plastanium", "@phase-fabric", "@surge-alloy", "@spore-pod", "@blast-compound", "@pyratite",
                    "@water", "@slag", "@oil", "@cryofluid", "@totalItems", "@firstItem", "@itemCapacity", "@totalLiquids",
                    "@liquidCapacity", "@totalPower", "@powerCapacity", "@powerNetStored", "@powerNetCapacity", "@powerNetIn",
                    "@powerNetOut", "@ammo", "@ammoCapacity", "@health", "@maxHealth", "@heat", "@efficiency", "@timescale",
                    "@dead", "@range", "@rotation", "@x", "@y", "@size", "@shootX", "@shootY", "@shooting", "@boosting",
                    "@mineX", "@mineY", "@mining", "@payloadCount", "@payloadType", "@controlled", "@controller", "@team",
                    "@type", "@flag", "@name", "@config", "@enabled", "@configure"),             
    OPERATION       ("add", "sub", "mul", "div", "idiv", "mod", "pow", "equal", "notEqual", "equal", "notEqual ", 
                    "lessThan", "lessThanEq", "greaterThan", "greaterThanEq", "strictEqual", "land", "or", "and", "xor",
                    "shl", "shr", "not", "max", "min", "angle", "len", "noise", "abs", "log", "log10", "sin", "cos",
                    "tan", "floor", "ceil", "sqrt", "rand"),
    CONDITION       ("equal", "notEqual", "lessThan", "lessThanEq", "greaterThan", "greaterThanEq", "strictEqual", "always"),
    UNIT            (true, "@dagger", "@mace", "@fortress", "@scepter", "@reign",
                    "@nova", "@pulsar", "@quasar", "@vela", "@corvus", 
                    "@crawler", "@atrax", "@spiroct", "@arkyid", "@toxopid",
                    "@flare", "@horizon", "@zenith", "@antumbra", "@eclipse",
                    "@mono", "@poly", "@mega", "@quad", "@oct",
                    "@risso", "@minke", "@bryde", "@sei", "@omura"),
    UNIT_CONTROL    ("idle", "stop", "move", "approach", "boost", "pathfind", "target", "targetp",
                    "itemDrop", "itemTake", "payDrop", "payTake", "mine", "flag", "build", "getBlock", "within"),
    LOCATE          ("ore", "building", "spawn", "damaged"),
    GROUP           ("core", "storage", "generator", "turret", "factory", "repair", "rally", "battery", "resupply", "reactor"),
    ORE             (true, "@copper", "@lead", "@metaglass", "@graphite", "@sand", "@coal", "@titanium", "@thorium", "@scrap",
                    "@silicon", "@plastanium", "@phase-fabric", "@surge-alloy", "@spore-pod", "@blast-compound", "@pyratite"),
    ZERO            ("0"),
    ;
    
    private final boolean input;
    private final List<String> permissibleValues;

    private ArgumentType() {
        this.input = false;
        this.permissibleValues = List.of();
    }

    private ArgumentType(boolean input) {
        this.input = input;
        this.permissibleValues = List.of();
    }

    private ArgumentType(boolean input, String... permissibleValues) {
        this.input = input;
        this.permissibleValues = List.of(permissibleValues);
    }
    
    private ArgumentType(String... permissibleValues) {
        this.input = false;
        this.permissibleValues = List.of(permissibleValues);
    }

    public boolean isInput() {
        return input;
    }
    
    public boolean isOutput() {
        return this == OUTPUT;
    }

    public List<String> getPermissibleValues() {
        return permissibleValues;
    }
    
    /**
     * Checks that the argument is permissible for given argument type. For input and output argument, anything
     * is permissible at the moment (it could be a variable name, a literal, or in some cases a @constant), but 
     * it might be possible to implement more specific checks in the future (including the number of arguments 
     * depending on the configuration of the instruction).
     * For keyword (ie. neither input, nor output) arguments only values from the list are permissible.
     * 
     * @param argument an argument to check
     * @return true if the argument is compatible with this argument type
     */
    public boolean isCompatible(String argument) {
        if (isInput() || isOutput()) {
            return true;
        } else {
            return permissibleValues.contains(argument);
        }
    }
}
