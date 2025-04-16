package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion.*;

// TODO Fill in parameter names for all keywords/selectors. Change error message to include a parameter name.

@NullMarked
public enum InstructionParameterType {
    ///  Alignment for the DRAW PRINT instruction. 
    ALIGNMENT       ("alignment", Flags.KEYWORD, "center", "top", "bottom", "left", "right",
            "topLeft", "topRight", "bottomLeft", "bottomRight"),

    /// Mindcode's array - a ValueStore instance
    ARRAY           (Flags.SPECIAL),

    ///  Input parameter accepting blocks (buildings).
    BLOCK           (Flags.INPUT),

    ///  Selector for the CONTROL instruction. 
    BLOCK_CONTROL   (Flags.SELECTOR | Flags.FUNCTION),

    ///  A boolean parameter - expected as an input 
    BOOL            (Flags.INPUT),

    ///  True/false to set/clear status in STATUS instruction. 
    CLEAR           (Flags.SELECTOR | Flags.FUNCTION),

    ///  Selector for the JUMP instruction. 
    CONDITION       (Flags.SELECTOR),

    ///  Type of cut scene in CUTSCENE instruction 
    CUTSCENE        (Flags.SELECTOR),

    ///  Selector for the DRAW instruction. 
    DRAW            (Flags.SELECTOR | Flags.FUNCTION),

    ///  Type of visual effect 
    EFFECT          (Flags.SELECTOR),

    ///  Item to fetch in FETCH instruction 
    FETCH           (Flags.SELECTOR),

    ///  An input parameter requiring a global variable - see the SYNC instruction. 
    GLOBAL          (Flags.GLOBAL | Flags.INPUT | Flags.OUTPUT),

    ///  A const parameter. Specifies group of buildings to locate. 
    GROUP           ("blockGroup", Flags.KEYWORD,
            allVersions(
                    "core", "storage", "generator", "turret", "factory", "repair", "battery", "reactor"),
            specificVersion(V6,
                    "rally", "resupply")
    ),

    ///  Non-specific input parameter. Accepts literals and variables 
    INPUT           (Flags.INPUT),

    ///  Non-specific input/output parameter for custom-made instructions. Accepts literals and variables 
    INPUT_OUTPUT    (Flags.INPUT | Flags.OUTPUT),

    ///  A label pseudo-parameter. 
    LABEL           (Flags.INPUT),

    ///  Layer in getBlock instruction. 
    LAYER           ("layer", Flags.KEYWORD, "floor", "ore", "block", "building"),

    ///  Selector for the ULOCATE instruction. No Flags.FUNCTION! 
    LOCATE          ("locate", Flags.SELECTOR),

    /// A const parameter. Specifies lookup category. The entire instruction is only available in V7;
    /// the parameter keywords therefore aren't version specific.
    LOOKUP          ("itemType", Flags.KEYWORD, "block", "unit", "item", "liquid"),

    ///  Type of message in MESSAGE instruction 
    MAKE_MARKER     ("markerType", Flags.KEYWORD, "shapeText", "point", "shape", "text", "line", "texture", "quad"),

    ///  Type of message in MESSAGE instruction 
    MESSAGE         (Flags.SELECTOR),

    ///  Selector for the OP instruction. 
    OPERATION       (Flags.SELECTOR | Flags.FUNCTION),

    ///  Input parameter accepting ore type. 
    ORE             ("oreType", Flags.INPUT,
            allVersions(
                    "@copper", "@lead", "@metaglass", "@graphite", "@sand", "@coal",
                    "@titanium", "@thorium", "@scrap", "@silicon", "@plastanium", "@phase-fabric",
                    "@surge-alloy", "@spore-pod", "@blast-compound", "@pyratite"),
            specificVersions(V7, MAX,
                    "@beryllium", "@tungsten", "@oxide", "@carbide", "@fissile-matter", "@dormant-cyst")
    ),

    ///  Output parameter. Sets a value of a variable in parameter list. 
    OUTPUT          (Flags.OUTPUT),

    ///  A const parameter. Specifies properties of units searchable by radar. 
    RADAR           ("category", Flags.KEYWORD, "any", "enemy", "ally", "player", "attacker", "flying", "boss", "ground"),

    ///  A const parameter. Specifies property to sort radar outputs by. 
    RADAR_SORT      ("sortBy", Flags.KEYWORD, "distance", "health", "shield", "armor", "maxHealth"),

    ///  Output parameter. Maps to the return value of a function. 
    RESULT          (Flags.OUTPUT),

    ///  Game rule in SETRULE instruction 
    RULE            (Flags.SELECTOR),

    ///  Scope for the playsound instruction: true=positional, false=global 
    SCOPE           (Flags.SELECTOR),

    ///  Input parameter accepting property id. 
    SENSOR          ("property", Flags.INPUT,
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
            specificVersions(V7, MAX,
                    "@beryllium", "@tungsten", "@oxide", "@carbide",
                    "@neoplasm", "@arkycite", "@ozone", "@hydrogen", "@nitrogen", "@cyanogen",
                    "@progress", "@speed", "@color"),
            specificVersions(V7A, MAX,
                    "@id"),
            specificVersions(V8A, MAX,
                    "@currentAmmoType", "@armor", "@velocityX", "@velocityY",
                    "@cameraX", "@cameraY", "@cameraWidth", "@cameraHeight", "@solid")
    ),

    ///  For the SET MARKER instruction 
    SET_MARKER      (Flags.SELECTOR),

    ///  Settable layer in SETBLOCK instruction 
    SETTABLE_LAYER  ("layer", Flags.SELECTOR, "floor", "ore", "block"),

    ///  Sound to play 
    SOUND           ("sound", Flags.INPUT, "@sfx-artillery", "@sfx-bang", "@sfx-beam", "@sfx-bigshot", "@sfx-bioLoop",
            "@sfx-blaster", "@sfx-bolt", "@sfx-boom", "@sfx-break", "@sfx-build", "@sfx-buttonClick", "@sfx-cannon", "@sfx-click",
            "@sfx-combustion", "@sfx-conveyor", "@sfx-corexplode", "@sfx-cutter", "@sfx-door", "@sfx-drill", "@sfx-drillCharge",
            "@sfx-drillImpact", "@sfx-dullExplosion", "@sfx-electricHum", "@sfx-explosion", "@sfx-explosionbig", "@sfx-extractLoop",
            "@sfx-fire", "@sfx-flame", "@sfx-flame2", "@sfx-flux", "@sfx-glow", "@sfx-grinding", "@sfx-hum", "@sfx-largeCannon",
            "@sfx-largeExplosion", "@sfx-laser", "@sfx-laserbeam", "@sfx-laserbig", "@sfx-laserblast", "@sfx-lasercharge",
            "@sfx-lasercharge2", "@sfx-lasershoot", "@sfx-machine", "@sfx-malignShoot", "@sfx-mediumCannon", "@sfx-minebeam",
            "@sfx-mineDeploy", "@sfx-missile", "@sfx-missileLarge", "@sfx-missileLaunch", "@sfx-missileSmall", "@sfx-missileTrail",
            "@sfx-mud", "@sfx-noammo", "@sfx-pew", "@sfx-place", "@sfx-plantBreak", "@sfx-plasmaboom", "@sfx-plasmadrop", "@sfx-pulse",
            "@sfx-pulseBlast", "@sfx-railgun", "@sfx-rain", "@sfx-release", "@sfx-respawn", "@sfx-respawning", "@sfx-rockBreak",
            "@sfx-sap", "@sfx-shield", "@sfx-shockBlast", "@sfx-shoot", "@sfx-shootAlt", "@sfx-shootAltLong", "@sfx-shootBig",
            "@sfx-shootSmite", "@sfx-shootSnap", "@sfx-shotgun", "@sfx-smelter", "@sfx-spark", "@sfx-spellLoop", "@sfx-splash",
            "@sfx-spray", "@sfx-steam", "@sfx-swish", "@sfx-techloop", "@sfx-thruster", "@sfx-titanExplosion", "@sfx-torch",
            "@sfx-tractorbeam", "@sfx-wave", "@sfx-wind", "@sfx-wind2", "@sfx-wind3", "@sfx-windhowl"),

    ///  Unit status in STATUS instruction. 
    STATUS          ("status", Flags.KEYWORD, "burning", "freezing", "unmoving", "wet", "melting", "sapped", "electrified",
            "spore-slowed", "tarred", "overdrive", "boss", "shocked", "blasted"),

    /// Expected type of value
    TYPE            ("valueType", Flags.KEYWORD, "any", "notNull", "decimal", "integer", "multiple"),

    ///  Input parameter accepting unit type.
    UNIT            ("unitType", Flags.INPUT,
            allVersions(
                    "@dagger", "@mace", "@fortress", "@scepter", "@reign",
                    "@nova", "@pulsar", "@quasar", "@vela", "@corvus",
                    "@crawler", "@atrax", "@spiroct", "@arkyid", "@toxopid",
                    "@flare", "@horizon", "@zenith", "@antumbra", "@eclipse",
                    "@mono", "@poly", "@mega", "@quad", "@oct",
                    "@risso", "@minke", "@bryde", "@sei", "@omura",
                    "@alpha", "@beta", "@gamma"),
            specificVersions(V7, MAX,
                    "@retusa", "@oxynoe", "@cyerce", "@aegires", "@navanax",
                    "@stell", "@locus", "@precept", "@vanquish", "@conquer",
                    "@merui", "@cleroi", "@anthicus", "@tecta", "@collaris",
                    "@elude", "@avert", "@obviate", "@quell", "@disrupt",
                    "@evoke", "@incite", "@emanate")
    ),

    ///  Selector for the UCONTROL instruction. 
    UNIT_CONTROL    (Flags.SELECTOR | Flags.FUNCTION),

    ///  Non-specific parameter type for generic instructions 
    UNSPECIFIED     (0),

    ///  An unused input parameter. Ignored by given opcode variant. 
    UNUSED          (Flags.UNUSED),

    ///  An unused output parameter. Ignored by given opcode variant, output in some other opcode variant. 
    UNUSED_OUTPUT   (Flags.OUTPUT | Flags.UNUSED),

    WEATHER         ("weather", Flags.INPUT, "@snowing", "@rain", "@sandstorm", "@sporestorm", "@fog", "@suspend-particles"),

    ;

    private final String typeName;
    private final int flags;
    private final List<ParameterValues> allowedValues;

    InstructionParameterType(int flags) {
        this.typeName = name();
        this.flags = flags;
        this.allowedValues = List.of();
    }

    InstructionParameterType(String typeName, int flags) {
        this.typeName = typeName;
        this.flags = flags;
        this.allowedValues = List.of();
    }

    InstructionParameterType(String typeName, int flags, String... keywords) {
        this.typeName = typeName;
        this.flags = flags;
        this.allowedValues = List.of(allVersions(keywords));
    }

    InstructionParameterType(String typeName, int flags, ParameterValues... keywords) {
        this.typeName = typeName;
        this.flags = flags;
        this.allowedValues = List.of(keywords);
    }

    public String getTypeName() {
        return typeName;
    }

    /// @return true if this parameter type determines the name of the function
    public boolean isFunctionName() {
        return (flags & Flags.FUNCTION) != 0;
    }

    /// @return true if this parameter is a constant value from a list, but not a selector.
    public boolean isKeyword() {
        return flags == Flags.KEYWORD;
    }

    /// @return true if this parameter can read a variable
    public boolean isInput() {
        return (flags & Flags.INPUT) != 0;
    }



    /// @return true if this parameter can write to a variable
    public boolean isOutput() {
        return (flags & Flags.OUTPUT) != 0;
    }

    /// @return true if this parameter can read or write to a variable
    public boolean isInputOrOutput() {
        return (flags & (Flags.INPUT + Flags.OUTPUT)) != 0;
    }

    /// @return true if this parameter type determines the variant of the instruction
    public boolean isSelector() {
        return (flags & Flags.SELECTOR) != 0;
    }

    /// @return true if this parameter is unused
    public boolean isUnused() {
        return (flags & Flags.UNUSED) != 0;
    }

    /// @return true if this parameter must be a global variable
    public boolean isGlobal() {
        return (flags & Flags.GLOBAL) != 0;
    }

    /// @return true if the value of this parameter must be one from the allowed values list
    public boolean restrictValues() {
        return (flags & (Flags.INPUT | Flags.OUTPUT | Flags.UNUSED | Flags.SPECIAL)) == 0;
    }

    public List<ParameterValues> getAllowedValues() {
        return allowedValues;
    }

    private static ParameterValues allVersions(String... keywords) {
        return new ParameterValues(Set.copyOf(EnumSet.allOf(ProcessorVersion.class)), List.of(keywords));
    }

    private static ParameterValues specificVersion(ProcessorVersion version, String... keywords) {
        return new ParameterValues(Set.of(version), List.of(keywords));
    }

    private static ParameterValues specificVersions(ProcessorVersion minVersion, ProcessorVersion maxVersion, String... keywords) {
        return new ParameterValues(ProcessorVersion.matching(minVersion, maxVersion), List.of(keywords));
    }

    public static class ParameterValues {
        public final Set<ProcessorVersion> versions;
        public final List<String> values;

        private ParameterValues(Set<ProcessorVersion> versions, List<String> keywords) {
            this.versions = versions;
            this.values = keywords;
        }
    }

    // Note: FUNCTION >> SELECTOR >> KEYWORD

    private static final class Flags {
        /// Keyword parameter (cannot use a variable).
        private static final int KEYWORD = 0;

        /// Input parameter (can use a variable).
        private static final int INPUT = 1;

        /// Output parameter (must use a variable for output value).
        private static final int OUTPUT = 2;

        /// Opcode-variant-selecting parameter. Possible values are given by existing opcode variants for given version.
        private static final int SELECTOR = 4;

        /// Defines the name of a function (or property). Must be a selector.
        private static final int FUNCTION = 8;

        /// Unused parameter. Doesn't map to Mindcode functions.
        private static final int UNUSED = 16;

        /// Parameter requiring a global variable (see the SYNC instruction).
        private static final int GLOBAL = 32;

        /// Parameter not corresponding to an existing mlog parameter type (e.g. array)
        private static final int SPECIAL = 64;
    }
}
