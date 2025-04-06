package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Icons {
    public static final String RESOURCE_NAME = "mimex-icons.txt";

    private final Map<String, LogicString> iconMap;
    private final Map<ContentType, Map<String, String>> contentMap;
    private final Map<String, LogicString> combinedIconMap;
    private final Map<String, String> reverseIconMap;

    Icons(String mimexVersion) {
        iconMap = initializeIconMap(mimexVersion);
        contentMap = initializeContentMap(mimexVersion);
        combinedIconMap = initializeCombinedIconMap();
        reverseIconMap = initializeReverseIconMap();
    }

    public Map<String, ValueStore> createIconMapAsValueStore() {
        return new HashMap<>(combinedIconMap);
    }

    public boolean isIconName(String name) {
        return combinedIconMap.containsKey(name);
    }

    public LogicLiteral getIconValue(String name) {
        return combinedIconMap.get(name);
    }

    public @Nullable String getContentIcon(ContentType type, String contentName) {
        return contentMap.getOrDefault(type, Map.of()).get(contentName);
    }

    // Schematics

    public String translateIcon(String text) {
        LogicLiteral literal = iconMap.get(text);
        return literal == null ? text : literal.format(null);
    }

    public boolean isIconValue(String text) {
        return reverseIconMap.containsKey(text);
    }

    public String decodeIcon(String text) {
        return reverseIconMap.getOrDefault(text, '"' + text + '"');
    }

    public void forEachIcon(BiConsumer<String, LogicLiteral> action) {
        combinedIconMap.forEach(action);
    }

    // INITIALIZATION

    private static Map<String, LogicString> initializeIconMap(String mimexVersion) {
        String resourceName = mimexVersion + "/" + RESOURCE_NAME;

        try (InputStream input = Icons.class.getResourceAsStream(resourceName)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input)));
            return reader.lines()
                    .filter(l -> !l.startsWith("//") && !l.isBlank())
                    .map(l -> l.split(";"))
                    .filter(s -> s.length == 3)
                    .collect(Collectors.toMap(s -> s[0],
                            s -> LogicString.create(String.valueOf((char) Integer.parseInt(s[2])))));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource " + resourceName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + resourceName, e);
        }
    }

    private Map<String, LogicString> initializeCombinedIconMap() {
        Map<String, LogicString> result = iconMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().replace("-", "_"), Map.Entry::getValue));
        result.putAll(iconMap);
        return result;
    }

    private Map<String, String> initializeReverseIconMap() {
        return iconMap.entrySet().stream()
                .collect(Collectors.toMap(e->e.getValue().format(null), Map.Entry::getKey));
    }

    private static Map<ContentType, Map<String, String>> initializeContentMap(String mimexVersion) {
        String resourceName = mimexVersion + "/" + RESOURCE_NAME;
        Map<ContentType, Map<String, String>> result = new HashMap<>();
        try (InputStream input = Icons.class.getResourceAsStream(resourceName)) {
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
            throw new RuntimeException("Cannot read resource " + resourceName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + resourceName, e);
        }
    }
}
