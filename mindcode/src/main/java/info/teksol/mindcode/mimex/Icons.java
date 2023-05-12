package info.teksol.mindcode.mimex;

import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Icons {

    public static Map<String, LogicLiteral> getIcons() {
        return new HashMap<>(ICON_MAP);
    }

    public static String translateIcon(String text) {
        LogicLiteral literal = ICON_MAP.get(text);
        return literal == null ? text : literal.format();
    }

    public static boolean isIcon(String text) {
        return REVERSE_MAP.containsKey(text);
    }

    public static String decodeIcon(String text) {
        return REVERSE_MAP.getOrDefault(text, '"' + text + '"');
    }

    private static Map<String, LogicLiteral> initializeIconMap() {
        try (InputStream input = Icons.class.getResourceAsStream(RESOURCE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            return reader.lines()
                    .filter(l -> !l.startsWith("//") && !l.isBlank())
                    .map(l -> l.split(";"))
                    .filter(s -> s.length == 2)
                    .collect(Collectors.toMap(s -> s[0],
                            s -> LogicString.create(String.valueOf((char) Integer.parseInt(s[1])))));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource " + RESOURCE_NAME, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + RESOURCE_NAME, e);
        }
    }

    private static Map<String, String> initializeReverseMap() {
        return ICON_MAP.entrySet().stream()
                .collect(Collectors.toMap(e->e.getValue().format(), Entry::getKey));
    }

    private static final String RESOURCE_NAME = "mimex-icons.txt";

    private static final Map<String, LogicLiteral> ICON_MAP = initializeIconMap();
    private static final Map<String, String> REVERSE_MAP = initializeReverseMap();
}
