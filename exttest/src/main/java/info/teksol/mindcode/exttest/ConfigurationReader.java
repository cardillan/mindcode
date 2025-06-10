package info.teksol.mindcode.exttest;

import com.amihaiemil.eoyaml.*;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Map<Optimization, List<OptimizationLevel>> optimizationLevels = getOptimizationLevels(mapping, defaults);
        List<GenerationGoal> generationGoals = getGenerationGoals(mapping, defaults);
        List<Boolean> symbolicLabels = getSymbolicLabels(mapping, defaults);
        int sampleCount = integer(mapping, defaults, "samples");
        int failureLimit = integer(mapping, defaults, "failure-limit");
        boolean caseSwitchingTest = bool(mapping, defaults, "case-switching-test");
        boolean run = bool(mapping, defaults, "run");

        configuration.addTestConfiguration(
                source,
                inputFiles,
                optimizationLevels,
                generationGoals,
                symbolicLabels,
                sampleCount,
                failureLimit,
                caseSwitchingTest ? 1 : 0,
                run);
    }

    private static Map<Optimization, List<OptimizationLevel>> getOptimizationLevels(YamlMapping mapping, YamlMapping defaults) {
        Map<Optimization, List<OptimizationLevel>> result = new HashMap<>();
        for (Optimization optimization : Optimization.values()) {
            YamlSequence values = sequence(mapping, defaults, optimization.getOptionName());
            if (values == null) {
                result.put(optimization, List.of(OptimizationLevel.NONE));
            } else {
                result.put(optimization,
                        values.values().stream()
                                .map(YamlNode::asScalar)
                                .map(Scalar::value)
                                .map(String::trim)
                                .map(s -> parseLevel(optimization.getOptionName(), s))
                                .toList());
            }
        }

        return result;
    }

    private static OptimizationLevel parseLevel(String settings, String level) throws ParseSettingException {
        OptimizationLevel optimizationLevel = OptimizationLevel.byName(level);
        if (optimizationLevel == null) {
            throw new ParseSettingException(settings, level);
        }
        return optimizationLevel;
    }

    private static List<GenerationGoal> getGenerationGoals(YamlMapping mapping, YamlMapping defaults) {
        YamlSequence values = sequence(mapping, defaults, "goal");
        if (values == null) {
            return List.of(GenerationGoal.SPEED);
        } else {
            return values.values().stream()
                    .map(YamlNode::asScalar)
                    .map(Scalar::value)
                    .map(String::trim)
                    .map(s -> parseGoal("goal", s))
                    .toList();
        }
    }

    private static List<Boolean> getSymbolicLabels(YamlMapping mapping, YamlMapping defaults) {
        YamlSequence values = sequence(mapping, defaults, "symbolic-labels");
        if (values == null) {
            return List.of(Boolean.FALSE);
        } else {
            return values.values().stream()
                    .map(YamlNode::asScalar)
                    .map(Scalar::value)
                    .map(String::trim)
                    .map(Boolean::parseBoolean)
                    .toList();
        }
    }

    private static GenerationGoal parseGoal(String settings, String goal) throws ParseSettingException {
        GenerationGoal generationGoal = GenerationGoal.byName(goal);
        if (generationGoal == null) {
            throw new ParseSettingException(settings, goal);
        }
        return generationGoal;
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