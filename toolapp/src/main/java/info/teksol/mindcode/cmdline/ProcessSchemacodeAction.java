package info.teksol.mindcode.cmdline;

import info.teksol.mc.common.PositionFormatter;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionCategory;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherClient;
import info.teksol.mindcode.cmdline.mlogwatcher.MlogWatcherCommand;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schematics.BlockOrder;
import info.teksol.schemacode.schematics.Decompiler;
import info.teksol.schemacode.schematics.DirectionLevel;
import info.teksol.schemacode.schematics.Schematic;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.*;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@NullMarked
public class ProcessSchemacodeAction extends ActionHandler {
    private final List<BiConsumer<Decompiler, Namespace>> optionSetters = new ArrayList<>();

    protected interface OptionSetter {
        void set(Decompiler profile, Namespace arguments, String optionName);
    }

    protected Argument createArgument(ArgumentContainer container, String optionName,
            OptionSetter optionSetter, String... nameOrFlags) {
        Argument argument = container.addArgument(nameOrFlags);
        optionSetters.add((profile, arguments) -> optionSetter.set(profile, arguments, optionName));
        return argument.dest(optionName);
    }

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Map<Enum<?>, CompilerOptionValue<?>> options = defaults.getOptions();

        Subparser subparser = subparsers.addParser(ToolAppAction.DECOMPILE_SCHEMA.getShortcut())
                .aliases("process-schematic", "ds", "decompile-schematic")
                .description("Load schematic from a binary msch file or the in-game Schematics Library for further processing (decompiling " +
                        "into a Schemacode definition file, running on the internal emulator or sending to the in-game library)")
                .help("Load and process a schematic from a file or the in-game Schematics Library.");

        ArgumentGroup files = subparser.addArgumentGroup("Input/output");

        files.addArgument("input")
                .help("Mindustry schematic file to be decompiled into Schematic Definition File. When -w extract is used, the input file must not be specified.")
                .nargs("?")
                .type(inputFileType);

        addOutputFileOption(files, false,
                "output file to receive the schematics loaded or extracted from the game in binary format.",
                "--output-msch");

        addOutputFileOption(files, true,
                "output file to receive a decompiled schematic definition file.",
                "--output-decompiled");

        addOutputDirectoryOption(files);

        addMlogWatcherOptions(files, ToolAppAction.DECOMPILE_SCHEMA);

        ArgumentGroup decompilation = subparser.addArgumentGroup("Decompilation options");

        createArgument(decompilation, "positions",
                (decompiler, arguments, name) -> decompiler.setRelativePositions(arguments.getBoolean(name)),
                "-p", "--relative-positions")
                .help("use relative coordinates for block positions where possible")
                .action(Arguments.storeTrue())
                .setDefault(false);

        createArgument(decompilation, "positions",
                (decompiler, arguments, name) -> decompiler.setRelativePositions(arguments.getBoolean(name)),
                "-P", "--absolute-positions")
                .help("use absolute coordinates for block positions")
                .action(Arguments.storeFalse())
                .setDefault(false);

        createArgument(decompilation, "connections",
                (decompiler, arguments, name) -> decompiler.setRelativeConnections(arguments.getBoolean(name)),
                "-c", "--relative-connections")
                .help("use relative coordinates for connections")
                .action(Arguments.storeTrue())
                .setDefault(true);

        createArgument(decompilation, "connections",
                (decompiler, arguments, name) -> decompiler.setRelativeConnections(arguments.getBoolean(name)),
                "-C", "--absolute-connections")
                .help("use absolute coordinates for connections")
                .action(Arguments.storeFalse())
                .setDefault(true);

        createArgument(decompilation, "links",
                (decompiler, arguments, name) -> decompiler.setRelativeLinks(arguments.getBoolean(name)),
                "-l", "--relative-links")
                .help("use relative coordinates for processor links")
                .action(Arguments.storeTrue())
                .setDefault(false);

        createArgument(decompilation, "links",
                (decompiler, arguments, name) -> decompiler.setRelativeLinks(arguments.getBoolean(name)),
                "-L", "--absolute-links")
                .help("use absolute coordinates for processor links")
                .dest("links")
                .action(Arguments.storeFalse())
                .setDefault(false);

        createArgument(decompilation, "sort-order",
                (decompiler, arguments, name) -> decompiler.setBlockOrder(arguments.get(name)),
                "-s", "--sort-order")
                .help("specifies how to order blocks in the decompiled schematic definition file")
                .type(LowerCaseEnumArgumentType.forEnum(BlockOrder.class))
                .setDefault(BlockOrder.ORIGINAL);

        createArgument(decompilation, "direction",
                (decompiler, arguments, name) -> decompiler.setDirectionLevel(arguments.get(name)),
                "-d", "--direction")
                .help("specifies when to include direction clause in decompiled schematic definition file: " +
                        "only for blocks affected by rotation, only for block with non-default direction, " +
                        "or for all blocks")
                .type(LowerCaseEnumArgumentType.forEnum(DirectionLevel.class))
                .setDefault(DirectionLevel.ROTATABLE);

        addAllCompilerOptions(subparser, options, OptionCategory.EMULATOR);

        return subparser;
    }

    void configureDecompiler(Decompiler decompiler, Namespace arguments) {
        optionSetters.forEach(setter -> setter.accept(decompiler, arguments));
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile profile = createCompilerProfile(false, arguments);

        final PositionFormatter positionFormatter = sp -> sp.formatForIde(profile.getFileReferences());
        ConsoleMessageLogger messageLogger = ConsoleMessageLogger.create(positionFormatter, false, true);
        ToolMessageEmitter toolMessages = new ToolMessageEmitter(messageLogger);

        final MlogWatcherCommand command = arguments.get("watcher");
        final File inputFile = arguments.get("input");
        final File outputDirectory = arguments.get("output-directory");

        if (inputFile == null && command != MlogWatcherCommand.EXTRACT) {
            toolMessages.error("No input file or Mlog Watcher source specified.");
            return;
        } else if (inputFile != null && command == MlogWatcherCommand.EXTRACT) {
            toolMessages.error("Both an input file and an Mlog Watcher source specified.");
            return;
        }

        MlogWatcherClient mlogWatcherClient = createMlogWatcherClient(arguments, toolMessages, profile.isPrintStackTrace());
        try {
            Schematic schematic;
            if (command == MlogWatcherCommand.EXTRACT) {
                // Couldn't connect, error already reported
                if (mlogWatcherClient == null) return;
                String encoded = mlogWatcherClient.extractSelectedSchematic();

                // Couldn't load, error already reported
                if (encoded == null || encoded.isEmpty()) return;

                try {
                    schematic = SchematicsIO.decode(encoded);
                } catch (IOException e) {
                    throw new ProcessingException(e, "Error decoding schematics loaded from MlogWatcher: %s", e.getMessage());
                }
            } else {
                try (FileInputStream fis = new FileInputStream(inputFile)) {
                    schematic = SchematicsIO.read(inputFile.getName(), fis);
                } catch (IOException e) {
                    throw new ProcessingException(e, "Error reading file '%s': %s", inputFile.getPath(), e.getMessage());
                }
            }

            if (profile.isRun()) {
                EmulatorSchematic emulatorSchematic = schematic.toEmulatorSchematic(SchematicsMetadata.getMetadata());
                Emulator emulator = new BasicEmulator(messageLogger, profile, emulatorSchematic);
                emulator.run(profile.getStepLimit());

                EmulatorMessageEmitter emulatorMessages = new EmulatorMessageEmitter(messageLogger);
                processEmulatorResults(emulatorMessages, emulator, profile.isOutputProfiling());
            }

            final File outputMsch = arguments.get("output_msch");
            if (outputMsch != null) {
                final File output = resolveOutputFile(inputFile, outputDirectory, outputMsch, ".msch", "mlog-watcher");
                writeOutput(output, SchematicsIO.encode(schematic));
            }

            final File outputSdf = arguments.get("output_decompiled");
            if (outputSdf != null) {
                Decompiler decompiler = new Decompiler(schematic);
                configureDecompiler(decompiler, arguments);
                String schemaDefinition = decompiler.buildCode();
                final File output = resolveOutputFile(inputFile, outputDirectory, outputSdf, ".sdf", "mlog-watcher");
                writeOutput(output, schemaDefinition);
            }

            if (mlogWatcherClient != null) {
                switch (command) {
                    case UPDATE -> mlogWatcherClient.updateSchematic(SchematicsIO.encode(schematic), true);
                    case ADD -> mlogWatcherClient.updateSchematic(SchematicsIO.encode(schematic), false);
                }
            }
        } catch (IOException ex) {
            throw new ProcessingException(ex, "Error processing schematic: %s", ex.getMessage());
        } finally {
            if (mlogWatcherClient != null) {
                mlogWatcherClient.close();
            }
        }
    }
}
