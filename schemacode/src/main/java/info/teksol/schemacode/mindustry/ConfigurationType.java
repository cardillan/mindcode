package info.teksol.schemacode.mindustry;

import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.schemacode.config.*;

import java.util.function.Supplier;

public enum ConfigurationType {
    NONE(EmptyConfiguration.class, () -> EmptyConfiguration.EMPTY),
    BOOLEAN(BooleanConfiguration.class, () -> BooleanConfiguration.FALSE),
    COLOR(Color.class, () -> Color.WHITE),
    CONNECTION(Position.class, PositionArray.class, () -> Position.INVALID),
    CONNECTIONS(PositionArray.class, () -> PositionArray.EMPTY),
    INTEGER(IntConfiguration.class, () -> IntConfiguration.ZERO),
    ITEM(ItemConfiguration.class, () -> EmptyConfiguration.EMPTY),
    LIQUID(LiquidConfiguration.class, () -> EmptyConfiguration.EMPTY),
    PROCESSOR(ProcessorConfiguration.class, () -> ProcessorConfiguration.EMPTY),
    TEXT(TextConfiguration.class, () -> TextConfiguration.EMPTY),
    UNIT_PLAN(UnitPlan.class, () -> UnitPlan.EMPTY),
    BLOCK(BlockConfiguration.class, () -> EmptyConfiguration.EMPTY),
    UNIT(UnitConfiguration.class, () -> EmptyConfiguration.EMPTY),
    UNIT_COMMAND(UnitCommandConfiguration.class, () -> EmptyConfiguration.EMPTY),
    UNIT_OR_BLOCK(UnitOrBlockConfiguration.class, () -> EmptyConfiguration.EMPTY),
    ;

    private final Class<? extends Configuration> mindustryClass;
    private final Class<? extends Configuration> schemacodeClass;
    private final Supplier<Configuration> initializer;

    ConfigurationType(Class<? extends Configuration> mindustryClass, Supplier<Configuration> initializer) {
        this.mindustryClass = mindustryClass;
        this.schemacodeClass = mindustryClass;
        this.initializer = initializer;
    }

    ConfigurationType(Class<? extends Configuration> mindustryClass, Class<? extends Configuration> schemacodeClass,
            Supplier<Configuration> initializer) {
        this.mindustryClass = mindustryClass;
        this.schemacodeClass = schemacodeClass;
        this.initializer = initializer;
    }

    public Class<? extends Configuration> getConfigurationClass() {
        return mindustryClass;
    }

    public Class<? extends Configuration> getBuilderConfigurationClass() {
        return schemacodeClass;
    }

    public boolean isCompatible(Configuration configuration) {
        return configuration instanceof EmptyConfiguration
                || mindustryClass.isInstance(configuration)
                || schemacodeClass.isInstance(configuration);
    }

    public static <T extends Configuration> T createEmpty(Class<T> cfgClass) {
        for (ConfigurationType configurationType : values()) {
            if (configurationType.mindustryClass == cfgClass) {
                @SuppressWarnings("unchecked")
                T result = (T) configurationType.initializer.get();
                return result;
            }
        }

        return null;
    }

    public static ConfigurationType fromInstance(Configuration configuration) {
        for (ConfigurationType configurationType : values()) {
            if (configurationType.mindustryClass.isInstance(configuration)) {
                return configurationType;
            }
        }

        return NONE;
    }


    public static ConfigurationType fromBlockType(BlockType blockType) {
        return Implementation.fromBlockType(blockType).configurationType();
    }
}
