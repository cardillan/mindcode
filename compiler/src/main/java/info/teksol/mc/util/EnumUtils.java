package info.teksol.mc.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnumUtils {

    public static <E extends Enum<E>> Map<String, E> createValueMap(E[] values) {
        Map<String, E> result = new HashMap<>();
        for (E value : values) {
            String name = value.name().toLowerCase(Locale.US);
            result.put(name, value);
            result.put(name.replace('_', '-'), value);
        }
        return result;
    }
}
