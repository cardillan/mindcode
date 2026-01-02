package info.teksol.mindcode.exttest;

import com.amihaiemil.eoyaml.*;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.profile.options.CompilerOptionFactory;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.EmulatorOptions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@NullMarked
public class ConfigurationReader {

    private static final Map<String, YamlMapping> cache = new HashMap<>();
    private static @Nullable YamlMapping settings;

    public static @Nullable Configuration loadConfiguration(String settingsFileName) {
        try {
            settings = Yaml.createYamlInput(new File(settingsFileName)).readYamlMapping();

            int threads = integer(settings, settings, "threads", 1);
            String testSuite = string(settings, settings, "test-suite");
            String resultPath = string(settings, settings, "output-path");
            boolean fullTests = bool(settings, settings, "full-tests");
            int sampleMultiplier = integer(settings, settings, "sample-multiplier", 1);

            YamlSequence configs = settings.yamlSequence(testSuite);

            Configuration configuration = new Configuration(threads, resultPath, fullTests, sampleMultiplier,
                    new ArrayList<>());
            for (YamlNode node : configs.values()) {
                parseTestConfiguration(configuration, node.asMapping());
            }

            if (configuration.configurations().isEmpty()) {
                System.err.println("No test configurations found.");
            }

            return configuration;
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        } catch (ParseSettingException | ClassCastException e) {
            System.err.println("Invalid configuration: " + e.getMessage());
        }

        return null;
    }

    private static YamlMapping loadDefaults(String section) {
        assert settings != null;
        return cache.computeIfAbsent(section, settings::yamlMapping);
    }

    private static final AtomicInteger sourceCounter = new AtomicInteger();
    private static void parseTestConfiguration(Configuration configuration, YamlMapping mapping) throws IOException {
        String defaultSection = string(mapping, mapping, "defaults", "defaults");
        YamlMapping defaults = loadDefaults(defaultSection);
        String source = string(mapping, defaults, "source", "");
        InputFiles inputFiles = InputFiles.create();

        // We pretend the source file is a library to have it parsed just once and cached.
        // Each source file needs a unique file name. Since libraries are registered by name (no path),
        // we're safe as long as Mindcode doesn't include a library named "__tmp" plus a number.
        // TODO: Implement a proper caching mechanism for these tests.
        inputFiles.registerLibraryFile(Path.of("__tmp" + sourceCounter.incrementAndGet()),
                Files.readString(Path.of(source)));

        SequencedMap<Enum<?>, List<Object>> settings = getSettings(mapping, defaults);
        int sampleCount = integer(mapping, defaults, "samples");
        int failureLimit = integer(mapping, defaults, "failure-limit");
        boolean caseSwitchingTest = bool(mapping, defaults, "case-switching-test");
        boolean run = bool(mapping, defaults, "run");

        configuration.addTestConfiguration(
                source,
                inputFiles,
                settings,
                sampleCount,
                failureLimit,
                caseSwitchingTest ? 1 : 0,
                run);
    }

    private static final Set<Enum<?>> ignoredOptions = Set.of(EmulatorOptions.RUN);

    private static SequencedMap<Enum<?>, List<Object>> getSettings(YamlMapping mapping, YamlMapping defaults) {
        SequencedMap<Enum<?>, List<Object>> result = new LinkedHashMap<>();
        Map<Enum<?>, CompilerOptionValue<?>> compilerOptions = CompilerOptionFactory.createCompilerOptions(false);

        for (CompilerOptionValue<?> option : compilerOptions.values()) {
            if (ignoredOptions.contains(option.getOption())) continue;

            YamlSequence values = sequence(mapping, defaults, option.getOptionName());
            if (values != null) {
                result.put(option.getOption(),
                        values.values().stream()
                                .map(YamlNode::asScalar)
                                .map(Scalar::value)
                                .map(String::trim)
                                .map(value -> parseSetting(option, value))
                                .toList());
            }
        }

        return result;
    }

    private static Object parseSetting(CompilerOptionValue<?> option, String value) throws ParseSettingException {
        Object converted = option.convert(value);
        if (converted == null) {
            throw new ParseSettingException(option.getOptionName(), value);
        }
        return converted;
    }

    private static @Nullable YamlNode node(YamlMapping mapping, YamlMapping defaults, String setting) {
        YamlNode node = mapping.value(setting);
        return node != null ? node : defaults.value(setting);
    }

    private static @Nullable YamlSequence sequence(YamlMapping mapping, YamlMapping defaults, String setting) {
        YamlNode node = node(mapping, defaults, setting);
        return node != null ? node.asSequence() : null;
    }

    private static int integer(YamlMapping mapping, YamlMapping defaults, String setting) {
        YamlNode node = node(mapping, defaults, setting);
        if (node == null) {
            throw new MissingSettingException(setting);
        }
        return parseInteger(setting, node.asScalar().value());
    }

    private static int integer(YamlMapping mapping, YamlMapping defaults, String setting, int defaultValue) {
        YamlNode node = node(mapping, defaults, setting);
        return node != null ? parseInteger(setting, node.asScalar().value()) : defaultValue;
    }

    private static String string(YamlMapping mapping, YamlMapping defaults, String setting) {
        YamlNode node = node(mapping, defaults, setting);
        if (node == null) {
            throw new MissingSettingException(setting);
        }
        return node.asScalar().value();
    }

    private static String string(YamlMapping mapping, YamlMapping defaults, String setting, String defaultValue) {
        YamlNode node = node(mapping, defaults, setting);
        return node != null ? node.asScalar().value() : defaultValue;
    }

    private static boolean bool(YamlMapping mapping, YamlMapping defaults, String setting) {
        YamlNode node = node(mapping, defaults, setting);
        if (node == null) {
            throw new MissingSettingException(setting);
        }
        return parseBoolean(setting, node.asScalar().value());
    }

    private static int parseInteger(String setting, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidSettingException(setting, value);
        }
    }

    private static boolean parseBoolean(String setting, @Nullable String value) {
        if (value == null) {
            return false;
        } else if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new InvalidSettingException(setting, value);
        }
    }


    private static class InvalidSettingException extends RuntimeException {
        private final String setting;

        public InvalidSettingException(String message, String setting) {
            super(message);
            this.setting = setting;
        }

        public String getSetting() {
            return setting;
        }
    }

    private static class ParseSettingException extends InvalidSettingException {
        private final String value;

        public ParseSettingException(String setting, String value) {
            super("Cannot parse the value of '" + setting + "': '" + value + "'.", setting);
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static class MissingSettingException extends InvalidSettingException {

        public MissingSettingException(String setting) {
            super("The value of '" + setting + "' must be provided.", setting);
        }
    }
}
