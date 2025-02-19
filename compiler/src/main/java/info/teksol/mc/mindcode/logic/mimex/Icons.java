package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NullMarked
public class Icons {

    public static Map<String, ValueStore> createIconMapAsValueStore() {
        return new HashMap<>(COMBINED_MAP);
    }

    public static boolean isIconName(String name) {
        return COMBINED_MAP.containsKey(name);
    }

    public static LogicLiteral getIconValue(String name) {
        return COMBINED_MAP.get(name);
    }

    public static @Nullable String getContentIcon(ContentType type, String contentName) {
        return CONTENT_MAP.getOrDefault(type, Map.of()).get(contentName);
    }

    public static String translateIcon(String text) {
        LogicLiteral literal = ICON_MAP.get(text);
        return literal == null ? text : literal.format(null);
    }

    public static boolean isIconValue(String text) {
        return REVERSE_MAP.containsKey(text);
    }

    public static void forEachIcon(BiConsumer<String, LogicLiteral> action) {
        COMBINED_MAP.forEach(action);
    }

    public static String decodeIcon(String text) {
        return REVERSE_MAP.getOrDefault(text, '"' + text + '"');
    }

    private static Map<String, LogicString> initializeIconMap() {
        try (InputStream input = Icons.class.getResourceAsStream(RESOURCE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input)));
            return reader.lines()
                    .filter(l -> !l.startsWith("//") && !l.isBlank())
                    .map(l -> l.split(";"))
                    .filter(s -> s.length == 3)
                    .collect(Collectors.toMap(s -> s[0],
                            s -> LogicString.create(String.valueOf((char) Integer.parseInt(s[2])))));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource " + RESOURCE_NAME, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + RESOURCE_NAME, e);
        }
    }

    private static Map<ContentType, Map<String, String>> initializeContentMap() {
        Map<ContentType, Map<String, String>> result = new HashMap<>();
        try (InputStream input = Icons.class.getResourceAsStream(RESOURCE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input)));
            reader.lines()
                    .filter(l -> !l.startsWith("//") && !l.isBlank())
                    .map(l -> l.split(";"))
                    .filter(split -> split.length == 3)
                    .forEach(split -> {
                        ContentType type = ContentType.byName(split[0].split("-")[0]);
                        if (type != null) {
                            result.computeIfAbsent(type, t -> new HashMap<>()).put(split[1], String.valueOf((char) Integer.parseInt(split[2])));
                        }
                    });
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource " + RESOURCE_NAME, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + RESOURCE_NAME, e);
        }
    }

    private static Map<String, String> initializeReverseMap() {
        return ICON_MAP.entrySet().stream()
                .collect(Collectors.toMap(e->e.getValue().format(null), Entry::getKey));
    }

    public static Map<String, LogicString> initializeCombinedIconMap() {
        Map<String, LogicString> result = ICON_MAP.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().replace("-", "_"), Entry::getValue));
        result.putAll(ICON_MAP);
        return result;
    }

    private static final String RESOURCE_NAME = "mimex-icons.txt";

    private static final Map<String, LogicString> ICON_MAP = initializeIconMap();
    private static final Map<String, LogicString> COMBINED_MAP = initializeCombinedIconMap();
    private static final Map<String, String> REVERSE_MAP = initializeReverseMap();

    private static final Map<ContentType, Map<String, String>> CONTENT_MAP = initializeContentMap();
}
