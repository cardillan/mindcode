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
    CONNECTION(Position.class, PositionArray.class, () -> Position.INVALID),
    CONNECTIONS(PositionArray.class, () -> PositionArray.EMPTY),
    INTEGER(IntConfiguration.class, () -> IntConfiguration.ZERO),
    ITEM(Item.class, () -> EmptyConfiguration.EMPTY),
    LIQUID(Liquid.class, () -> EmptyConfiguration.EMPTY),
    PROCESSOR(ProcessorConfiguration.class, () -> ProcessorConfiguration.EMPTY),
    TEXT(TextConfiguration.class, () -> TextConfiguration.EMPTY),
    UNIT_PLAN(UnitPlan.class, () -> UnitPlan.EMPTY),
    ;

    private final Class<? extends Configuration> cfgClass;
    private final Class<? extends Configuration> secondaryClass;

    private final Supplier<Configuration> initializer;

    ConfigurationType(Class<? extends Configuration> cfgClass, Supplier<Configuration> initializer) {
        this.cfgClass = cfgClass;
        this.initializer = initializer;
        this.secondaryClass = null;
    }

    ConfigurationType(Class<? extends Configuration> cfgClass, Class<? extends Configuration> secondaryClass,
            Supplier<Configuration> initializer) {
        this.cfgClass = cfgClass;
        this.secondaryClass = secondaryClass;
        this.initializer = initializer;
    }

    public Class<? extends Configuration> getConfigurationClass() {
        return cfgClass;
    }

    public Class<? extends Configuration> getBuilderConfigurationClass() {
        return secondaryClass == null ? cfgClass : secondaryClass;
    }

    public boolean isCompatible(Configuration configuration) {
        return cfgClass.isInstance(configuration) || secondaryClass != null && secondaryClass.isInstance(configuration);
    }

    public static <T extends Configuration> T createEmpty(Class<T> cfgClass) {
        for (ConfigurationType configurationType : values()) {
            if (configurationType.cfgClass == cfgClass) {
                //noinspection unchecked
                return (T) configurationType.initializer.get();
            }
        }

        return null;
    }

    public static ConfigurationType fromInstance(Configuration configuration) {
        for (ConfigurationType configurationType : values()) {
            if (configurationType.cfgClass.isInstance(configuration)) {
                return configurationType;
            }
        }

        return NONE;
    }
}
