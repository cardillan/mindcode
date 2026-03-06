package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.emulator.ExecutorResults;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.*;
import info.teksol.mindcode.cmdline.mlogwatcher.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NullMarked
abstract class ActionHandler {
    static final Object NOTHING = new Object();
    private final List<BiConsumer<CompilerProfile, Namespace>> optionSetters = new ArrayList<>();

    abstract Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults);

    abstract void handle(Namespace arguments);

    private record OptionChoice<T>(CompilerOptionValue<T> option) implements ArgumentChoice {
        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object val) {
            return option.valueType.isInstance(val) && option.accepts((T) val, _ -> {});
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
                .choices(new OptionChoice<>(option))
                .setDefault(NOTHING);

        if (option.getValueType() == OptimizationLevel.class) {
            argument.metavar("LEVEL");
        }

        if (option instanceof BooleanCompilerOptionValue booleanOption) {
            argument.type(Arguments.booleanType(booleanOption.getTrueValue(), booleanOption.getFalseValue()));
        } else if (option instanceof TargetCompilerOptionValue targetOption) {
            argument.type(this::convertTarget);
        } else if (option.getValueType().isEnum()) {
            argument.type(LowerCaseEnumArgumentType.forClass(option.getValueType()));
        } else {
            argument.type(option.getValueType());
        }

        if (option.multiplicity == OptionMultiplicity.ZERO) {
            argument.setConst(option.getConstValue()).action(Arguments.storeConst());
        } else if (option.multiplicity == OptionMultiplicity.ZERO_OR_ONCE) {
            argument.setConst(option.getConstValue());
        }

        if (!option.multiplicity.nargs.isEmpty()) {
            argument.nargs(option.multiplicity.nargs);
        }

        if (option.multiplicity.matchesMultiple()) {
            optionSetters.add((profile, arguments) -> {
                Object value = arguments.get(option.optionName);
                if (value instanceof List<?>) {
                    profile.getOption(option.option).setValues(arguments.getList(option.optionName));
                }
            });
        } else {
            optionSetters.add((profile, arguments) -> {
                Object value = arguments.get(option.optionName);
                if (value != NOTHING) {
                    profile.getOption(option.option).setValue(value);
                }
            });
        }

        return argument;
    }

    public Target convertTarget(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        Target target = Target.fromStringNullable(value);
        if (target == null) {
            throw new ArgumentParserException("Could not convert target", parser, arg);
        } else {
            return target;
        }
    }

    void addOutputFileOption(ArgumentGroup files, boolean acceptSystemIn, String help, String... flags) {
        FileArgumentType fileArgumentType = acceptSystemIn
                ? Arguments.fileType().acceptSystemIn().verifyCanCreate()
                : Arguments.fileType().verifyCanCreate();

        files.addArgument(flags).help(help).nargs("?").type(fileArgumentType);
    }

    void addOutputDirectoryOption(ArgumentGroup files) {
        files.addArgument("--output-directory")
                .dest("output-directory")
                .help("specifies the directory where the output files will be placed.")
                .type(Arguments.fileType().verifyIsDirectory());
    }

    void addMlogWatcherOptions(ArgumentContainer container, ToolAppAction action) {
        String description = switch (action) {
            case COMPILE_MINDCODE -> """
                    invoke an specific Mlog Watcher operation on the compiled mlog code (default: update)
                        update           send it to the selected processor
                        update-all       send it to processors with the exact same version on the active map
                        upgrade-all      send it to processors with the same or lower version on the active map
                        force-update-all send it to processors matching program ID (regardless of the version)""";

            case PROCESS_MLOG -> """
                    use Mlog Watcher to obtain or send the mlog code from/to the game (default: update).
                        extract     load code from the selected processor in the game
                        update      send code loaded from a file to the selected processor""";

            case COMPILE_SCHEMA -> """
                    invoke an specific Mlog Watcher operation on the created schematic (default: update)
                        update      update the schematic with the same name in the schematics library
                        add         add a new copy of the schematic to the schematics library""";

            case PROCESS_SCHEMA -> """
                    use Mlog Watcher to obtain or send the schematic from/to the game (default: update).
                        extract     load schematic from the schematic shown on the info screen in-game
                        update      update the schematic with the same name in the schematics library
                        add         add a new copy of the schematic to the schematics library""";
        };

        container.addArgument("-w", "--watcher")
                .help(description)
                .type(LowerCaseEnumArgumentType.forEnum(MlogWatcherCommand.class, c -> c.supports(action)))
                .nargs("?")
                .setDefault(NOTHING)
                .setConst(MlogWatcherCommand.UPDATE);

        container.addArgument("--watcher-version")
                .help("specifies the version of the Mlog Watcher mod")
                .type(LowerCaseEnumArgumentType.forEnum(MlogWatcherVersion.class))
                .setDefault(MlogWatcherVersion.V0);

        container.addArgument("--watcher-port")
                .help("port number for communication with Mlog Watcher")
                .choices(Arguments.range(0, 65535))
                .type(Integer.class)
                .setDefault(9992);

        container.addArgument("--watcher-timeout")
                .help("timeout in milliseconds when trying to establish a connection to Mlog Watcher")
                .choices(Arguments.range(0, 3_600_000))
                .type(Integer.class)
                .setDefault(500);
    }

    void addCompilerOptions(ArgumentContainer container, Map<Enum<?>, CompilerOptionValue<?>> options, OptionCategory category) {
        for (CompilerOptionValue<?> option : options.values()) {
            if (option.getAvailability().isCommandline() && option.getCategory() == category) {
                createArgument(container, option);
            }
        }
    }

    void addCompilerOptions(Subparser subparser, Map<Enum<?>, CompilerOptionValue<?>> options, OptionCategory category) {
        ArgumentGroup container = subparser.addArgumentGroup(category.title);
        String description = category.description.replaceAll("\\s+", " ").trim();
        if (!description.isEmpty()) {
            container.description(description);
        }

        addCompilerOptions(container, options, category);
    }

    void addAllCompilerOptions(Subparser subparser, Map<Enum<?>, CompilerOptionValue<?>> options, OptionCategory... categories) {
        for (OptionCategory category : categories) {
            addCompilerOptions(subparser, options, category);
        }
    }

    /// Creates a compiler profile.
    ///
    /// @param schematic when true, the profile is being created for the schematic builder
    /// @param arguments command line arguments
    /// @return created compiler profile
    protected CompilerProfile createCompilerProfile(boolean schematic, Namespace arguments) {
        CompilerProfile profile = CompilerProfile.fullOptimizations(schematic, false);
        for (BiConsumer<CompilerProfile, Namespace> setter : optionSetters) {
            setter.accept(profile, arguments);
        }
        return profile;
    }

    static boolean isStdInOut(@Nullable File file) {
        return file != null && file.getPath().equals("-");
    }

    static File resolveOutputFile(File inputFile, @Nullable File outputDirectory, @Nullable File outputFile, String extension) {
        return resolveOutputFile(inputFile, outputDirectory, outputFile, extension, "");
    }

    /// Returns the output file determined from information provided on the command line (input file, output directory,
    ///  output file) and a default extension + default file name.
    /// Rules:
    /// - outputFile is null
    ///   - inputFile is stdin: returns stdout
    ///   - constructs a file name:
    ///     - inputFile is null: default file name (and a default extension if not specified by the default fle name)
    ///     - inputFile is specified: input file name plus default extension
    ///   - constructs an output directory:
    ///     - outputDirectory is specified: outputDirectory
    ///     - inputFile is specified: input file's parent directory
    ///     - otherwise no specific deirectory
    ///   - returns the output directory and output file from above
    /// - outputFile is not null
    ///   - outputFile represents stdout: returns stdout
    ///   - outputFile is a simple file name:
    ///     - outputDirectory is specified: returns outputFile evaluated against outputDirectory
    ///     - otherwise, returns outputFile as is
    ///   - outputFile is a path (absolute or relative): returns outputFile as is
    static File resolveOutputFile(@Nullable File inputFile, @Nullable File outputDirectory, @Nullable File outputFile,
            String extension, String defaultFileName) {
        if (outputFile == null) {
            if (isStdInOut(inputFile)) {
                return inputFile;
            } else {
                String name = inputFile == null ? defaultFileName : inputFile.getName();
                int inputExt = name.lastIndexOf('.');
                File outputDir = outputDirectory != null ? outputDirectory :
                        inputFile != null ? inputFile.getParentFile() : null;
                String fileName = (inputExt < 0 ? name : name.substring(0, inputExt)) + extension;
                return new File(outputDir, fileName);
            }
        } else {
            return !isStdInOut(outputFile) && outputFile.getParent() == null && outputDirectory != null
                    ? new File(outputDirectory, outputFile.getName())
                    : outputFile;
        }
    }

    static void readFile(InputFiles inputFiles, File file) {
        String source = readInput(file);
        Path path = isStdInOut(file) ? Path.of("") : file.toPath();
        inputFiles.registerFile(path, source);
    }

    static void readFile(InputFiles inputFiles, File file, @Nullable ExcerptSpecification excerpt) {
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

    protected void processEmulatorResults(EmulatorMessageEmitter emulatorMessages, Emulator emulator, boolean outputProfiling) {
        emulatorMessages.info("");
        emulatorMessages.info("Emulator output (%,d steps):", emulator.getExecutionSteps());

        for (ExecutorResults executor : emulator.getExecutorResults()) {
            if (emulator.getExecutorCount() > 1) {
                emulatorMessages.info("%n*** Output of processor %s:", executor.getProcessorId());
            }
            String textBufferOutput = executor.getFormattedOutput();
            if (!textBufferOutput.isEmpty()) {
                emulatorMessages.info(textBufferOutput);
            } else {
                emulatorMessages.info("The program didn't generate any output.");
            }
        }

        if (!emulator.getAllAssertions().isEmpty()) {
            emulatorMessages.info("The program generated the following assertions:");
            emulator.getAllAssertions().forEach(a -> emulatorMessages.addMessage(a.createMessage()));
        }

        if (outputProfiling) {
            for (ExecutorResults executor : emulator.getExecutorResults()) {
                if (emulator.getExecutorCount() == 1) {
                    emulatorMessages.debug("\n*** Code profiling result:\n");
                } else {
                    emulatorMessages.debug("%n*** Code profiling result of processor %s:%n", executor.getProcessorId());
                }

                emulatorMessages.debug(String.join("\n", executor.getFormattedProfile()));
            }
        }
    }

    protected @Nullable MlogWatcherCommand getMlogWatcherCommand(Namespace arguments) {
        return arguments.get("watcher") instanceof MlogWatcherCommand c ? c : null;
    }

    protected @Nullable MlogWatcherClient createMlogWatcherClient(Namespace arguments, ToolMessageEmitter messageEmitter) {
        Object value = arguments.get("watcher");
        if (value == NOTHING) return null;

        if (value instanceof MlogWatcherCommand) {
            MlogWatcherVersion version = arguments.get("watcher_version");
            int port = arguments.getInt("watcher_port");
            int timeout = arguments.getInt("watcher_timeout");
            MlogWatcherClient client = switch (version) {
                case V0 -> new LegacyMlogWatcherClient(messageEmitter, port, timeout, false);
                case V1 -> new MlogWatcherClientImpl(messageEmitter, port, timeout, false);
            };
            return client.connect() ? client : null;
        } else {
            throw new IllegalArgumentException("Invalid value for --watcher: " + value);
        }
    }

}
