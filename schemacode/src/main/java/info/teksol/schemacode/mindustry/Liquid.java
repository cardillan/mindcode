package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.config.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Liquid implements Configuration {
    WATER("@water"),
    SLAG("@slag"),
    OIL("@oil"),
    CRYOFLUID("@cryofluid"),
    NEOPLASM("@neoplasm"),
    ARKYCITE("@arkycite"),
    GALLIUM("@gallium"),
    OZONE("@ozone"),
    HYDROGEN("@hydrogen"),
    NITROGEN("@nitrogen"),
    CYANOGEN("@cyanogen"),
    ;

    private final String name;

    Liquid(String name) {
        this.name = name;
    }

    public int getId() {
        return ordinal();
    }

    public String getName() {
        return name;
    }

    public static Liquid forIndex(int index) {
        return (index >= 0 && index < LIST.size()) ? LIST.get(index) : LIST.get(0);
    }

    public static Liquid forName(String name) {
        return MAP.get(name);
    }

    @Override
    public String toString(){
        return "Item(name=" + name + ")";
    }

    private static final List<Liquid> LIST = List.of(values());
    private static final Map<String, Liquid> MAP = LIST.stream().collect(Collectors.toMap(Liquid::getName, i -> i));
}
