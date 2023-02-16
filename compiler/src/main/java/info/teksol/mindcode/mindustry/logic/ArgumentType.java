package info.teksol.mindcode.mindustry.logic;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static info.teksol.mindcode.mindustry.logic.ProcessorVersion.*;

public enum ArgumentType {
    /** Non-specific input argument. Accepts literals and variables */
    INPUT           (Flags.INPUT),

    /** Input argument accepting blocks (buildings). */
    BLOCK           (Flags.INPUT),

    /** A label pseudo-argument. */
    LABEL           (Flags.INPUT),

    /** Output argument. Sets a value of a variable in argument list. */
    OUTPUT          (Flags.OUTPUT),

    /** Output argument. Maps to the return value of a function. */
    RESULT          (Flags.OUTPUT),

    /** Selector for the DRAW instruction. */
    DRAW            (Flags.SELECTOR | Flags.FUNCTION),

    /** Selector for the CONTROL instruction. */
    BLOCK_CONTROL   (Flags.SELECTOR | Flags.FUNCTION),

    /** A const argument. Specifies properties of units searchable by radar. */
    RADAR           (Flags.CONST, "any", "enemy", "ally", "player", "attacker", "flying", "boss", "ground"),

    /** A const argument. Specifies property to sort radar outputs by. */
    SORT            (Flags.CONST, "distance", "health", "shield", "armor", "maxHealth"),
    
    /** Input argument accepting property id. */
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

    /** Input argument accepting unit type. */
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

    /** A const argument. Specifies group of buildings to locate. */
    GROUP           (Flags.CONST, "core", "storage", "generator", "turret", "factory", "repair", "rally", "battery", "resupply", "reactor"),

    /** Input argument accepting ore type. */
    ORE             (Flags.INPUT,
            allVersions(
                    "@copper", "@lead", "@metaglass", "@graphite", "@sand", "@coal",
                    "@titanium", "@thorium", "@scrap", "@silicon", "@plastanium", "@phase-fabric",
                    "@surge-alloy", "@spore-pod", "@blast-compound", "@pyratite"),
            specificVersion(V7,
                    "@beryllium", "@tungsten", "@oxide", "@carbide", "@fissile-matter", "@dormant-cyst")
    ),

    /**
     * A const argument. Specifies lookup category. The entire instruction is only available in V7;
     * the argument keywords therefore aren't version specific.
     */
    LOOKUP          (Flags.CONST, "block", "unit", "item", "liquid"),

    /** Layer in getBlock instruction. */
    LAYER           (Flags.CONST, "floor", "ore", "block", "building"),

    /** Settable layer in SETBLOCK instruction */
    LAYER_SET       (Flags.SELECTOR),

    /** True/false to set/clear status in STATUS instruction. */
    CLEAR           (Flags.SELECTOR | Flags.FUNCTION),

    /** Unit status in STATUS instruction. */
    STATUS          (Flags.CONST, "burning", "freezing", "unmoving", "wet", "melting", "sapped", "electrified",
            "spore-slowed", "tarred", "overdrive", "boss", "shocked", "blasted"),

    /** Game rule in SETRULE instruction */
    RULE            (Flags.SELECTOR),

    /** Type of message in MESSAGE instruction */
    MESSAGE         (Flags.SELECTOR),

    /** Type of cut scene in CUTSCENE instruction */
    CUTSCENE        (Flags.SELECTOR),

    /** Item to fetch in Type of cut scene in FETCH instruction */
    FETCH           (Flags.SELECTOR),

    /** An unused input argument. Ignored by given opcode variant. */
    UNUSED          (Flags.UNUSED),

    // TODO: might not be necessary, needs investigation
    /** An unused output argument. Ignored by given opcode variant, output in some other opcode variant. */
    UNUSED_OUTPUT   (Flags.OUTPUT | Flags.UNUSED),
    ;
    
    private final int flags;
    private final List<ArgumentValues> allowedValues;

    private ArgumentType(int flags) {
        this.flags = flags;
        this.allowedValues = List.of();
    }

    private ArgumentType(int flags, String... keywords) {
        this.flags = flags;
        this.allowedValues = List.of(allVersions(keywords));
    }
    
    private ArgumentType(int flags, ArgumentValues... keywords) {
        this.flags = flags;
        this.allowedValues = List.of(keywords);
    }

    /**
     * @return true if this argument type determines the name of the function
     */
    public boolean isFunctionName() {
        return (flags & Flags.FUNCTION) != 0;
    }

    /**
     * @return true if this argument can read a variable
     */
    public boolean isInput() {
        return (flags & Flags.INPUT) != 0;
    }
    
    /**
     * @return true if this argument can write to a variable
     */
    public boolean isOutput() {
        return (flags & Flags.OUTPUT) != 0;
    }

    /**
     * @return true if this argument type determines the variant of the instruction
     */
    public boolean isSelector() {
        return (flags & Flags.SELECTOR) != 0;
    }

    /**
     * @return true if this argument can write to a variable
     */
    public boolean isUnused() {
        return (flags & Flags.UNUSED) != 0;
    }

    /**
     * @return true if the value of this argument must be one from the allowed values list
     */
    public boolean restrictValues() {
        return (flags & (Flags.INPUT | Flags.OUTPUT | Flags.UNUSED)) == 0;
    }

    public List<ArgumentValues> getAllowedValues() {
        return allowedValues;
    }

    private static ArgumentValues allVersions(String... keywords) {
        return new ArgumentValues(Set.copyOf(EnumSet.allOf(ProcessorVersion.class)), Set.of(keywords));
    }

    private static ArgumentValues specificVersion(ProcessorVersion version, String... keywords) {
        return new ArgumentValues(Set.of(version), Set.of(keywords));
    }

    public static class ArgumentValues {
        public final Set<ProcessorVersion> versions;
        public final Set<String> values;

        private ArgumentValues(Set<ProcessorVersion> versions, Set<String> keywords) {
            this.versions = versions;
            this.values = keywords;
        }
    }

    private static final class Flags {
        /** Constant argument (cannot use a variable). */
        private static final int CONST      = 0;

        /** Input argument (can use a variable). */
        private static final int INPUT      = 1;

        /** Output argument (must use a variable for output value). */
        private static final int OUTPUT     = 2;

        /**  Opcode-selecting argument. Possible values are given by existing opcode variants for given version. */
        private static final int SELECTOR   = 4;

        /** Defines name of a function(or property). Must be a selector. */
        private static final int FUNCTION   = 8;

        /** Unused argument. Doesn't map to Mindcode functions. */
        private static final int UNUSED     = 16;
    }
}
