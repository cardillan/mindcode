package info.teksol.mindcode.logic;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static info.teksol.mindcode.logic.ProcessorVersion.V6;
import static info.teksol.mindcode.logic.ProcessorVersion.V7;

public enum LogicParameter {
    /** Non-specific input parameter. Accepts literals and variables */
    INPUT           (Flags.INPUT),

    /** Input parameter accepting blocks (buildings). */
    BLOCK           (Flags.INPUT),

    /** A label pseudo-parameter. */
    LABEL           (Flags.INPUT),

    /** Output parameter. Sets a value of a variable in parameter list. */
    OUTPUT          (Flags.OUTPUT),

    /** Output parameter. Maps to the return value of a function. */
    RESULT          (Flags.OUTPUT),

    /** Selector for the DRAW instruction. */
    DRAW            (Flags.SELECTOR | Flags.FUNCTION),

    /** Selector for the CONTROL instruction. */
    BLOCK_CONTROL   (Flags.SELECTOR | Flags.FUNCTION),

    /** A const parameter. Specifies properties of units searchable by radar. */
    RADAR           (Flags.KEYWORD, "any", "enemy", "ally", "player", "attacker", "flying", "boss", "ground"),

    /** A const parameter. Specifies property to sort radar outputs by. */
    SORT            (Flags.KEYWORD, "distance", "health", "shield", "armor", "maxHealth"),
    
    /** Input parameter accepting property id. */
    SENSOR          (Flags.INPUT,
            allVersions(
                    "@copper", "@lead", "@metaglass", "@graphite", "@sand", "@coal",
                    "@titanium", "@thorium", "@scrap", "@silicon", "@plastanium", "@phase-fabric",
                    "@surge-alloy", "@spore-pod", "@blast-compound", "@pyratite",
                    "@water", "@slag", "@oil", "@cryofluid",
                    "@totalItems", "@firstItem", "@totalLiquids", "@totalPower", "@itemCapacity", "@liquidCapacity",
                    "@powerCapacity", "@powerNetStored", "@powerNetCapacity", "@powerNetIn", "@powerNetOut",
                    "@ammo", "@ammoCapacity", "@health", "@maxHealth", "@heat", "@efficiency", "@timescale", "@rotation",
                    "@x", "@y", "@shootX", "@shootY", "@size", "@dead", "@range", "@shooting", "@boosting",
                    "@mineX", "@mineY", "@mining", "@team", "@type", "@flag", "@controlled", "@controller",
                    "@name", "@payloadCount", "@payloadType", "@enabled", "@config"),
            specificVersion(V6,
                    "@commanded", "@configure"),
            specificVersion(V7,
                    "@beryllium", "@tungsten", "@oxide", "@carbide",
                    "@neoplasm", "@arkycite", "@ozone", "@hydrogen", "@nitrogen", "@cyanogen",
                    "@progress", "@speed", "@color")
    ),

    /** Selector for the OP instruction. */
    OPERATION       (Flags.SELECTOR | Flags.FUNCTION),

    /** Selector for the JUMP instruction. */
    CONDITION       (Flags.SELECTOR),

    /** Input parameter accepting unit type. */
    UNIT            (Flags.INPUT,
            allVersions(
                    "@dagger", "@mace", "@fortress", "@scepter", "@reign",
                    "@nova", "@pulsar", "@quasar", "@vela", "@corvus", 
                    "@crawler", "@atrax", "@spiroct", "@arkyid", "@toxopid",
                    "@flare", "@horizon", "@zenith", "@antumbra", "@eclipse",
                    "@mono", "@poly", "@mega", "@quad", "@oct",
                    "@risso", "@minke", "@bryde", "@sei", "@omura",
                    "@alpha", "@beta", "@gamma"),
            specificVersion(V7,
                    "@retusa", "@oxynoe", "@cyerce", "@aegires", "@navanax",
                    "@stell", "@locus", "@precept", "@vanquish", "@conquer",
                    "@merui", "@cleroi", "@anthicus", "@tecta", "@collaris",
                    "@elude", "@avert", "@obviate", "@quell", "@disrupt",
                    "@evoke", "@incite", "@emanate")
    ),

    /** Selector for the UCONTROL instruction. */
    UNIT_CONTROL    (Flags.SELECTOR | Flags.FUNCTION),

    /** Selector for the ULOCATE instruction. No Flags.FUNCTION! */
    LOCATE          (Flags.SELECTOR),

    /** A const parameter. Specifies group of buildings to locate. */
    GROUP           (Flags.KEYWORD,
            allVersions(
                    "core", "storage", "generator", "turret", "factory", "repair", "battery", "reactor"),
            specificVersion(V6,
                    "rally", "resupply")
    ),
    /** Input parameter accepting ore type. */
    ORE             (Flags.INPUT,
            allVersions(
                    "@copper", "@lead", "@metaglass", "@graphite", "@sand", "@coal",
                    "@titanium", "@thorium", "@scrap", "@silicon", "@plastanium", "@phase-fabric",
                    "@surge-alloy", "@spore-pod", "@blast-compound", "@pyratite"),
            specificVersion(V7,
                    "@beryllium", "@tungsten", "@oxide", "@carbide", "@fissile-matter", "@dormant-cyst")
    ),

    /**
     * A const parameter. Specifies lookup category. The entire instruction is only available in V7;
     * the parameter keywords therefore aren't version specific.
     */
    LOOKUP          (Flags.KEYWORD, "block", "unit", "item", "liquid"),

    /** Layer in getBlock instruction. */
    LAYER           (Flags.KEYWORD, "floor", "ore", "block", "building"),

    /** Settable layer in SETBLOCK instruction */
    SETTABLE_LAYER  (Flags.SELECTOR),

    /** True/false to set/clear status in STATUS instruction. */
    CLEAR           (Flags.SELECTOR | Flags.FUNCTION),

    /** Unit status in STATUS instruction. */
    STATUS          (Flags.KEYWORD, "burning", "freezing", "unmoving", "wet", "melting", "sapped", "electrified",
            "spore-slowed", "tarred", "overdrive", "boss", "shocked", "blasted"),

    /** Game rule in SETRULE instruction */
    RULE            (Flags.SELECTOR),

    /** Type of message in MESSAGE instruction */
    MESSAGE         (Flags.SELECTOR),

    /** Type of cut scene in CUTSCENE instruction */
    CUTSCENE        (Flags.SELECTOR),

    /** Item to fetch in Type of cut scene in FETCH instruction */
    FETCH           (Flags.SELECTOR),

    /** An unused input parameter. Ignored by given opcode variant. */
    UNUSED          (Flags.UNUSED),

    /** An unused output parameter. Ignored by given opcode variant, output in some other opcode variant. */
    UNUSED_OUTPUT   (Flags.OUTPUT | Flags.UNUSED),

    /** Non-specific parameter type for generic instructions */
    UNSPECIFIED     (0),
    ;
    
    private final int flags;
    private final List<parameterValues> allowedValues;

    LogicParameter(int flags) {
        this.flags = flags;
        this.allowedValues = List.of();
    }

    LogicParameter(int flags, String... keywords) {
        this.flags = flags;
        this.allowedValues = List.of(allVersions(keywords));
    }
    
    LogicParameter(int flags, parameterValues... keywords) {
        this.flags = flags;
        this.allowedValues = List.of(keywords);
    }

    /**
     * @return true if this parameter type determines the name of the function
     */
    public boolean isFunctionName() {
        return (flags & Flags.FUNCTION) != 0;
    }

    /**
     * @return true if this parameter is a constant value from a list, but not a selector.
     */
    public boolean isKeyword() {
        return flags == Flags.KEYWORD;
    }
    
    /**
     * @return true if this parameter can read a variable
     */
    public boolean isInput() {
        return (flags & Flags.INPUT) != 0;
    }

    /**
     * @return true if this parameter can write to a variable
     */
    public boolean isOutput() {
        return (flags & Flags.OUTPUT) != 0;
    }

    /**
     * @return true if this parameter can read or write to a variable
     */
    public boolean isInputOrOutput() {
        return (flags & (Flags.INPUT + Flags.OUTPUT)) != 0;
    }

    /**
     * @return true if this parameter type determines the variant of the instruction
     */
    public boolean isSelector() {
        return (flags & Flags.SELECTOR) != 0;
    }

    /**
     * @return true if this parameter can write to a variable
     */
    public boolean isUnused() {
        return (flags & Flags.UNUSED) != 0;
    }

    /**
     * @return true if the value of this parameter must be one from the allowed values list
     */
    public boolean restrictValues() {
        return (flags & (Flags.INPUT | Flags.OUTPUT | Flags.UNUSED)) == 0;
    }

    public List<parameterValues> getAllowedValues() {
        return allowedValues;
    }

    private static parameterValues allVersions(String... keywords) {
        return new parameterValues(Set.copyOf(EnumSet.allOf(ProcessorVersion.class)), Set.of(keywords));
    }

    private static parameterValues specificVersion(ProcessorVersion version, String... keywords) {
        return new parameterValues(Set.of(version), Set.of(keywords));
    }

    public static class parameterValues {
        public final Set<ProcessorVersion> versions;
        public final Set<String> values;

        private parameterValues(Set<ProcessorVersion> versions, Set<String> keywords) {
            this.versions = versions;
            this.values = keywords;
        }
    }

    private static final class Flags {
        /** Keyword parameter (cannot use a variable). */
        private static final int KEYWORD    = 0;

        /** Input parameter (can use a variable). */
        private static final int INPUT      = 1;

        /** Output parameter (must use a variable for output value). */
        private static final int OUTPUT     = 2;

        /**  Opcode-selecting parameter. Possible values are given by existing opcode variants for given version. */
        private static final int SELECTOR   = 4;

        /** Defines name of a function(or property). Must be a selector. */
        private static final int FUNCTION   = 8;

        /** Unused parameter. Doesn't map to Mindcode functions. */
        private static final int UNUSED     = 16;
    }
}
