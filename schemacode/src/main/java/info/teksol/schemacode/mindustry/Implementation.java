package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.config.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public enum Implementation {
    ACCELERATOR,
    AIRBLOCK,
    ARMOREDCONDUIT,
    ARMOREDCONVEYOR,
    ATTRIBUTECRAFTER,
    AUTODOOR,
    BASESHIELD,
    BATTERY,
    BEAMDRILL,
    BEAMNODE,
    BUFFEREDITEMBRIDGE(ConfigurationType.CONNECTION),
    BUILDTURRET,
    BURSTDRILL,
    CANVASBLOCK,
    CLIFF,
    CONDUIT,
    CONSTRUCTOR(ConfigurationType.BLOCK),
    CONSTRUCTBLOCK,
    CONSUMEGENERATOR,
    CONTINUOUSLIQUIDTURRET,
    CONTINUOUSTURRET,
    CONVEYOR,
    COREBLOCK,
    DIRECTIONALFORCEPROJECTOR,
    DIRECTIONALUNLOADER(ConfigurationType.ITEM),
    DIRECTIONLIQUIDBRIDGE,
    DOOR(ConfigurationType.BOOLEAN),
    DRILL,
    DUCT,
    DUCTBRIDGE,
    DUCTROUTER(ConfigurationType.ITEM),
    EMPTYFLOOR,
    FLOOR,
    FORCEPROJECTOR,
    FRACKER,
    GENERICCRAFTER,
    HEATCONDUCTOR,
    HEATCRAFTER,
    HEATERGENERATOR,
    HEATPRODUCER,
    IMPACTREACTOR,
    INCINERATOR,
    ITEMBRIDGE(ConfigurationType.CONNECTION),
    ITEMINCINERATOR,
    ITEMSOURCE(ConfigurationType.ITEM),
    ITEMTURRET,
    ITEMVOID,
    JUNCTION,
    LASERTURRET,
    LAUNCHPAD,
    LEGACYCOMMANDCENTER,
    LEGACYMECHPAD,
    LEGACYUNITFACTORY,
    LIGHTBLOCK(ConfigurationType.COLOR),
    LIQUIDBRIDGE(ConfigurationType.CONNECTION),
    LIQUIDJUNCTION,
    LIQUIDROUTER,
    LIQUIDSOURCE(ConfigurationType.LIQUID),
    LIQUIDTURRET,
    LIQUIDVOID,
    LOGICBLOCK(ConfigurationType.PROCESSOR),
    LOGICDISPLAY,
    LONGPOWERNODE(ConfigurationType.CONNECTIONS),
    MASSDRIVER(ConfigurationType.CONNECTION),
    MEMORYBLOCK,
    MENDPROJECTOR,
    MESSAGEBLOCK(ConfigurationType.TEXT),
    NUCLEARREACTOR,
    OREBLOCK,
    OVERDRIVEPROJECTOR,
    OVERFLOWDUCT,
    OVERFLOWGATE,
    OVERLAYFLOOR,
    PAYLOADCONVEYOR,
    PAYLOADDECONSTRUCTOR,
    PAYLOADLOADER,
    PAYLOADMASSDRIVER(ConfigurationType.CONNECTION),
    PAYLOADROUTER,
    PAYLOADSOURCE(ConfigurationType.UNIT_OR_BLOCK),
    PAYLOADUNLOADER,
    PAYLOADVOID,
    POINTDEFENSETURRET,
    POWERDIODE,
    POWERNODE(ConfigurationType.CONNECTIONS),
    POWERSOURCE(ConfigurationType.CONNECTIONS),
    POWERTURRET,
    POWERVOID,
    PROP,
    PUMP,
    RADAR,
    RECONSTRUCTOR(ConfigurationType.UNIT_COMMAND),
    REGENPROJECTOR,
    REPAIRTOWER,
    REPAIRTURRET,
    ROUTER,
    SEABUSH,
    SEAWEED,
    SEPARATOR,
    SHALLOWLIQUID,
    SHIELDWALL,
    SHOCKMINE,
    SHOCKWAVETOWER,
    SOLARGENERATOR,
    SOLIDPUMP,
    SORTER(ConfigurationType.ITEM),
    SPAWNBLOCK,
    STACKCONVEYOR,
    STACKROUTER(ConfigurationType.ITEM),
    STATICTREE,
    STATICWALL,
    STEAMVENT,
    STORAGEBLOCK,
    SWITCHBLOCK(ConfigurationType.BOOLEAN),
    TALLBLOCK,
    THERMALGENERATOR,
    THRUSTER,
    TRACTORBEAMTURRET,
    TREEBLOCK,
    UNITASSEMBLER,
    UNITASSEMBLERMODULE,
    UNITCARGOLOADER,
    UNITCARGOUNLOADPOINT(ConfigurationType.ITEM),
    UNITFACTORY(ConfigurationType.UNIT_PLAN),
    UNLOADER(ConfigurationType.ITEM),
    VARIABLEREACTOR,
    WALL,
    WALLCRAFTER,
    ;

    private final ConfigurationType configurationType;

    Implementation() {
        configurationType = ConfigurationType.NONE;
    }

    Implementation(ConfigurationType configurationType) {
        this.configurationType = configurationType;
    }

    public ConfigurationType configurationType() {
        return configurationType;
    }

    public Class<? extends Configuration> configurationClass() {
        return configurationType.getConfigurationClass();
    }

    private static final Map<String, Implementation> STRING_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(v -> v.name(),v -> v));

    public static Implementation fromBlockType(BlockType blockType) {
        return STRING_MAP.get(blockType.implementation());
    }
}
