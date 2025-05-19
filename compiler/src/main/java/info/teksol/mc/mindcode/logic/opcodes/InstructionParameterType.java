package info.teksol.mc.mindcode.logic.opcodes;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO Fill in parameter names for all keywords/selectors. Change error message to include a parameter name.

@NullMarked
public enum InstructionParameterType {
    ///  Alignment for the DRAW PRINT instruction. 
    ALIGNMENT       ("alignment", Flags.KEYWORD, MindustryMetadata::getAlignments),

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
    GROUP           ("blockGroup", Flags.KEYWORD, MindustryMetadata::getBlockFlags),

    ///  Non-specific input parameter. Accepts literals and variables 
    INPUT           (Flags.INPUT),

    ///  Non-specific input/output parameter for custom-made instructions. Accepts literals and variables 
    INPUT_OUTPUT    (Flags.INPUT | Flags.OUTPUT),

    ///  A label pseudo-parameter. 
    LABEL           (Flags.INPUT),

    ///  Layer in getBlock instruction. 
    LAYER           ("layer", Flags.KEYWORD, MindustryMetadata::getTileLayers),

    ///  Selector for the ULOCATE instruction. No Flags.FUNCTION! 
    LOCATE          ("locate", Flags.SELECTOR),

    /// A const parameter. Specifies lookup category. The entire instruction is only available in V7;
    /// the parameter keywords therefore aren't version specific.
    LOOKUP          ("itemType", Flags.KEYWORD, MindustryMetadata::getLookableContents),

    ///  Type of message in MESSAGE instruction 
    MAKE_MARKER     ("markerType", Flags.KEYWORD, MindustryMetadata::getMarkerTypes),

    ///  Type of message in MESSAGE instruction 
    MESSAGE         (Flags.SELECTOR),

    ///  Selector for the OP instruction. 
    OPERATION       (Flags.SELECTOR | Flags.FUNCTION),

    ///  Input parameter accepting ore type. 
    ORE             ("oreType", Flags.INPUT, MindustryMetadata::getItemNames),

    ///  Output parameter. Sets a value of a variable in parameter list. 
    OUTPUT          (Flags.OUTPUT),

    ///  A const parameter. Specifies properties of units searchable by radar. 
    RADAR           ("category", Flags.KEYWORD, MindustryMetadata::getRadarTargets),

    ///  A const parameter. Specifies property to sort radar outputs by. 
    RADAR_SORT      ("sortBy", Flags.KEYWORD, MindustryMetadata::getRadarSorts),

    ///  Output parameter. Maps to the return value of a function. 
    RESULT          (Flags.OUTPUT),

    ///  Game rule in SETRULE instruction 
    RULE            (Flags.SELECTOR),

    ///  Scope for the playsound instruction: true=positional, false=global 
    SCOPE           (Flags.SELECTOR),

    ///  Input parameter accepting property id. 
    SENSOR          ("property", Flags.INPUT, MindustryMetadata::getLAccessNames),

    ///  Input parameter accepting settable property id.
    SETTABLE        ("property", Flags.INPUT, MindustryMetadata::getLAccessSettableNames),

    ///  For the SET MARKER instruction
    SET_MARKER      (Flags.SELECTOR),

    ///  Settable layer in SETBLOCK instruction 
    SETTABLE_LAYER  ("layer", Flags.SELECTOR, MindustryMetadata::getTileLayersSettable),

    ///  Sound to play 
    SOUND           ("sound", Flags.INPUT, MindustryMetadata::getSoundNames),

    ///  Unit status in STATUS instruction. 
    STATUS          ("status", Flags.KEYWORD, MindustryMetadata::getStatusEffects),

    /// Expected type of value
    TYPE            ("valueType", Flags.KEYWORD, m -> List.of("any", "notNull", "decimal", "integer", "multiple")),

    ///  Input parameter accepting unit type.
    UNIT            ("unitType", Flags.INPUT, MindustryMetadata::getUnitTypes),

    ///  Selector for the UCONTROL instruction. 
    UNIT_CONTROL    (Flags.SELECTOR | Flags.FUNCTION),

    ///  Non-specific parameter type for generic instructions 
    UNSPECIFIED     (0),

    ///  An unused input parameter. Ignored by given opcode variant. 
    UNUSED          (Flags.UNUSED),

    ///  An unused output parameter. Ignored by given opcode variant, output in some other opcode variant. 
    UNUSED_OUTPUT   (Flags.OUTPUT | Flags.UNUSED),

    WEATHER         ("weather", Flags.INPUT, MindustryMetadata::getWeathers),

    ;

    private final String typeName;
    private final int flags;
    private final @Nullable Function<MindustryMetadata, Collection<String>> keywordsSupplier;

    InstructionParameterType(int flags) {
        this.typeName = name();
        this.flags = flags;
        this.keywordsSupplier = null;
    }

    InstructionParameterType(String typeName, int flags) {
        this.typeName = typeName;
        this.flags = flags;
        this.keywordsSupplier = null;
    }

    InstructionParameterType(String typeName, int flags, Function<MindustryMetadata, Collection<String>> keywordsSupplier) {
        this.typeName = typeName;
        this.flags = flags;
        this.keywordsSupplier = keywordsSupplier;
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

    public Collection<String> getVersionKeywords(ProcessorVersion version) {
        if (keywordsSupplier == null) {
            return List.of();
        } else {
            return keywordsSupplier.apply(MindustryMetadata.forVersion(version));
        }
    }

    public Collection<String> getAllKeywords() {
        if (keywordsSupplier == null) {
            return List.of();
        } else {
            return Arrays.stream(ProcessorVersion.values())
                    .map(this::getVersionKeywords)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(TreeSet::new));
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
