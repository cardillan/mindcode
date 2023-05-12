package info.teksol.schemacode.mindustry;

import info.teksol.schemacode.config.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Item implements Configuration {
    COPPER("@copper"),
    LEAD("@lead"),
    METAGLASS("@metaglass"),
    GRAPHITE("@graphite"),
    SAND("@sand"),
    COAL("@coal"),
    TITANIUM("@titanium"),
    THORIUM("@thorium"),
    SCRAP("@scrap"),
    SILICON("@silicon"),
    PLASTANIUM("@plastanium"),
    PHASE_FABRIC("@phase-fabric"),
    SURGE_ALLOY("@surge-alloy"),
    SPORE_POD("@spore-pod"),
    BLAST_COMPOUND("@blast-compound"),
    PYRATITE("@pyratite"),
    BERYLLIUM("@beryllium"),
    TUNGSTEN("@tungsten"),
    OXIDE("@oxide"),
    CARBIDE("@carbide"),
    FISSILE_MATTER("@fissile-matter"),
    DORMANT_CYST("@dormant-cyst"),
    ;

    private final String name;

    Item(String name) {
        this.name = name;
    }

    public int getId() {
        return ordinal();
    }

    public String getName() {
        return name;
    }

    public static Item forIndex(int index) {
        return (index >= 0 && index < LIST.size()) ? LIST.get(index) : LIST.get(0);
    }

    public static Item forName(String name) {
        return MAP.get(name);
    }

    @Override
    public String toString(){
        return "Item(name=" + name + ")";
    }

    private static final List<Item> LIST = List.of(values());
    private static final Map<String, Item> MAP = LIST.stream().collect(Collectors.toMap(Item::getName, i -> i));
}
