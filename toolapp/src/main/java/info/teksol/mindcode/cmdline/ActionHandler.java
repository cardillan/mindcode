package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.BooleanCompilerOptionValue;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mc.profile.options.OptionMultiplicity;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

abstract class ActionHandler {
    private final List<BiConsumer<CompilerProfile, Namespace>> optionSetters = new ArrayList<>();

    abstract Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults);

    abstract void handle(Namespace arguments);

    private record OptionChoice<T>(CompilerOptionValue<T> option) implements ArgumentChoice {
        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object val) {
            return option.valueType.isInstance(val) && option.accepts((T) val);
        }

        @Override
        public String textualFormat() {
            return option.acceptedValuesDescription();
        }
    }

    protected Argument createArgument(ArgumentContainer container, CompilerOptionValue<?> option) {
        Argument argument = container.addArgument(option.getNameOrFlag())
                .dest(option.optionName)
                .help(option.description)
                .choices(new OptionChoice<>(option));

        boolean optimizationLevel = option.getValueType() == OptimizationLevel.class;

        if (optimizationLevel) {
            // Default for optimization levels is null
            argument.metavar("LEVEL");
        } else {
            argument.setDefault(option.getDefaultValue());
        }

        if (option instanceof BooleanCompilerOptionValue booleanOption) {
            argument.type(Arguments.booleanType(booleanOption.getTrueValue(), booleanOption.getFalseValue()));
        } else if (option.getValueType().isEnum()) {
            argument.type(LowerCaseEnumArgumentType.forClass(option.getValueType()));
        } else {
            argument.type(option.getValueType());
        }

        if (!option.multiplicity.nargs.isEmpty()) {
            argument.nargs(option.multiplicity.nargs);
        } else if (option.multiplicity == OptionMultiplicity.ZERO) {
            argument.setConst(option.getConstValue()).action(Arguments.storeConst());
        }

        if (option.multiplicity.matchesMultiple()) {
            optionSetters.add((profile, arguments) -> profile.getOption(option.option).setValues(arguments.getList(option.optionName)));
        } else if (option.getValueType() == OptimizationLevel.class) {
            optionSetters.add((profile, arguments) -> {
                Object value = arguments.get(option.optionName);
                if (value != null) {
                    profile.getOption(option.option).setValue(value);
                }
            });
        } else {
            optionSetters.add((profile, arguments) -> {
                Object value = Objects.requireNonNullElse(arguments.get(option.optionName), option.getConstValue());
                profile.getOption(option.option).setValue(value);
            });
        }

        return argument;
    }

    void addCompilerOptions(Subparser subparser, Map<Enum<?>, CompilerOptionValue<?>> options, OptionCategory category) {
        ArgumentGroup container = subparser.addArgumentGroup(category.title);
        String description = category.description.replaceAll("\\s+", " ").trim();
        if (!description.isEmpty()) {
            container.description(description);
        }

        for (CompilerOptionValue<?> option : options.values()) {
            if (option.getAvailability().isCommandline() && option.getCategory() == category) {
                createArgument(container, option);
            }
        }
    }

    void addAllCompilerOptions(Subparser subparser, Map<Enum<?>, CompilerOptionValue<?>> options, OptionCategory... categories) {
        for (OptionCategory category : categories) {
            addCompilerOptions(subparser, options, category);
        }
    }

    protected CompilerProfile createCompilerProfile(Namespace arguments) {
        CompilerProfile profile = CompilerProfile.fullOptimizations(false);
        for (BiConsumer<CompilerProfile, Namespace> setter : optionSetters) {
            setter.accept(profile, arguments);
        }
        return profile;
    }

    static boolean isStdInOut(File file) {
        return file.getPath().equals("-");
    }

    static File resolveOutputFile(File inputFile, File outputFile, String extension) {
        if (outputFile == null) {
            if (isStdInOut(inputFile)) {
                return inputFile;
            } else {
                String name = inputFile.getName();
                int inputExt = name.lastIndexOf('.');
                if (inputExt < 0) {
                    return new File(inputFile.getPath() + extension);
                } else {
                    String path = inputFile.getPath();
                    int outputExt = inputExt - name.length() + path.length();
                    return new File(path.substring(0, outputExt) + extension);
                }
            }
        } else {
            return outputFile;
        }
    }

    static void readFile(InputFiles inputFiles, File file) {
        String source = readInput(file);
        Path path = isStdInOut(file) ? Path.of("") : file.toPath();
        inputFiles.registerFile(path, source);
    }

    static void readFile(InputFiles inputFiles, File file, ExcerptSpecification excerpt) {
        String source = readInput(file);
        Path path = isStdInOut(file) ? Path.of("") : file.toPath();
        inputFiles.registerFile(path, excerpt == null ? source : excerpt.apply(source));
    }

    static String readInput(File inputFile) {
        if (isStdInOut(inputFile)) {
            return readStdin();
        } else {
            try {
                return Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ProcessingException(e, "Error reading file '%s'.", inputFile.getPath());
            }
        }
    }

    static String readStdin() {
        try {
            // Not exactly sure about encodings here. Keep the default.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.lines().collect(Collectors.joining("\n"));
        } catch (UncheckedIOException e) {
            throw new ProcessingException(e, "Error reading from stdin.");
        }
    }

    static void writeOutputToFile(File outputFile, List<String> data) {
        try {
            Files.write(outputFile.toPath(), data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ProcessingException(e, "Error writing file %s.", outputFile.getPath());
        }
    }

    static void writeOutput(File outputFile, List<String> data, boolean useErrorOutput) {
        if (isStdInOut(outputFile)) {
            data.forEach(useErrorOutput ? System.err::println : System.out::println);
        } else {
            writeOutputToFile(outputFile, data);
        }
    }

    static void writeOutput(File outputFile, String data) {
        writeOutput(outputFile, List.of(data), false);
    }

    static void writeOutput(File outputFile, byte[] data) {
        try {
            Files.write(outputFile.toPath(), data);
        } catch (IOException e) {
            throw new ProcessingException(e, "Error writing file %s.", outputFile.getPath());
        }
    }

    static void writeToClipboard(String string) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(string);
        c.setContents(data, data);
    }

    static void outputMessages(CompilerOutput<?> result, File outputFile, File logFile, PositionFormatter positionFormatter) {
        // If mlog gets written to stdout, write log to stderr
        if (isStdInOut(logFile)) {
            boolean alwaysErr = isStdInOut(outputFile);
            result.messages().forEach(m -> (alwaysErr || m.isErrorOrWarning() ? System.err : System.out).println(m.formatMessage(positionFormatter)));
        } else {
            writeOutput(logFile, result.texts(m -> m.formatMessage(positionFormatter)), isStdInOut(outputFile));
            // Print errors and warnings to stderr anyway
            result.messages().stream()
                    .filter(m -> m.isErrorOrWarning() || m.isInfo())
                    .forEach(m -> (m.isErrorOrWarning() ? System.err : System.out).println(m.formatMessage(positionFormatter)));
        }

    }

    static ConsoleMessageLogger createMessageLogger(File outputFile, File logFile, PositionFormatter positionFormatter) {
        if (isStdInOut(logFile)) {
            // If mlog gets written to stdout, write log to stderr
            return new ConsoleMessageLogger(positionFormatter, MessageLevel.DEBUG, isStdInOut(outputFile));
        } else {
            return new ConsoleMessageLogger(positionFormatter, MessageLevel.INFO, isStdInOut(outputFile));
        }
    }
}
