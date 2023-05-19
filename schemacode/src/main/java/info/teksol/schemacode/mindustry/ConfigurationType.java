package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.config.BooleanConfiguration;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.IntConfiguration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.config.TextConfiguration;

import java.util.function.Supplier;

public enum ConfigurationType {
    NONE(EmptyConfiguration.class, () -> EmptyConfiguration.EMPTY),
    BOOLEAN(BooleanConfiguration.class, () -> BooleanConfiguration.FALSE),
    COLOR(Color.class, () -> Color.WHITE),
    CONNECTION(Position.class, PositionArray.class, () -> Position.INVALID),
    CONNECTIONS(PositionArray.class, () -> PositionArray.EMPTY),
    INTEGER(IntConfiguration.class, () -> IntConfiguration.ZERO),
    ITEM(Item.class, () -> EmptyConfiguration.EMPTY),
    LIQUID(Liquid.class, () -> EmptyConfiguration.EMPTY),
    PROCESSOR(ProcessorConfiguration.class, () -> ProcessorConfiguration.EMPTY),
    TEXT(TextConfiguration.class, () -> TextConfiguration.EMPTY),
    UNIT_PLAN(UnitPlan.class, () -> UnitPlan.EMPTY),
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
                //noinspection unchecked
                return (T) configurationType.initializer.get();
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
}
