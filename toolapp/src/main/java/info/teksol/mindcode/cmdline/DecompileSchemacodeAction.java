package info.teksol.mindcode.cmdline;

import info.teksol.mc.profile.CompilerProfile;
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
import java.util.function.BiConsumer;

@NullMarked
public class DecompileSchemacodeAction extends ActionHandler {
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
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile profile) {
        Subparser subparser = subparsers.addParser(ToolAppAction.DECOMPILE_SCHEMA.getShortcut())
                .aliases("decompile-schema", "decompile-schematic")
                .description("Decompile a binary msch file into schematic definition file.")
                .help("Decompile a binary msch file into schematic definition file.");

        subparser.addArgument("input")
                .help("Mindustry schematic file to be decompiled into Schematic Definition File.")
                .type(inputFileType);

        subparser.addArgument("-o", "--output")
                .help("Output file to receive compiled mlog code; uses input file name with .sdf extension if not specified.")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        subparser.addArgument("--output-directory")
                .dest("output-directory")
                .help("show program's version number and exit")
                .type(Arguments.fileType().verifyIsDirectory());

        createArgument(subparser, "positions",
                (decompiler, arguments, name) -> decompiler.setRelativePositions(arguments.getBoolean(name)),
                "-p", "--relative-positions")
                .help("use relative coordinates for block positions where possible")
                .action(Arguments.storeTrue())
                .setDefault(false);

        createArgument(subparser, "positions",
                (decompiler, arguments, name) -> decompiler.setRelativePositions(arguments.getBoolean(name)),
                "-P", "--absolute-positions")
                .help("use absolute coordinates for block positions")
                .action(Arguments.storeFalse())
                .setDefault(false);

        createArgument(subparser, "connections",
                (decompiler, arguments, name) -> decompiler.setRelativeConnections(arguments.getBoolean(name)),
                "-c", "--relative-connections")
                .help("use relative coordinates for connections")
                .action(Arguments.storeTrue())
                .setDefault(true);

        createArgument(subparser, "connections",
                (decompiler, arguments, name) -> decompiler.setRelativeConnections(arguments.getBoolean(name)),
                "-C", "--absolute-connections")
                .help("use absolute coordinates for connections")
                .action(Arguments.storeFalse())
                .setDefault(true);

        createArgument(subparser, "links",
                (decompiler, arguments, name) -> decompiler.setRelativeLinks(arguments.getBoolean(name)),
                "-l", "--relative-links")
                .help("use relative coordinates for processor links")
                .action(Arguments.storeTrue())
                .setDefault(false);

        createArgument(subparser, "links",
                (decompiler, arguments, name) -> decompiler.setRelativeLinks(arguments.getBoolean(name)),
                "-L", "--absolute-links")
                .help("use absolute coordinates for processor links")
                .dest("links")
                .action(Arguments.storeFalse())
                .setDefault(false);

        createArgument(subparser, "sort-order",
                (decompiler, arguments, name) -> decompiler.setBlockOrder(arguments.get(name)),
                "-s", "--sort-order")
                .help("specifies how to order blocks in the decompiled schematic definition file")
                .type(LowerCaseEnumArgumentType.forEnum(BlockOrder.class))
                .setDefault(BlockOrder.ORIGINAL);

        createArgument(subparser, "direction",
                (decompiler, arguments, name) -> decompiler.setDirectionLevel(arguments.get(name)),
                "-d", "--direction")
                .help("specifies when to include direction clause in decompiled schematic definition file: " +
                        "only for blocks affected by rotation, only for block with non-default direction, " +
                        "or for all blocks")
                .type(LowerCaseEnumArgumentType.forEnum(DirectionLevel.class))
                .setDefault(DirectionLevel.ROTATABLE);

        return subparser;
    }

    void configureDecompiler(Decompiler decompiler, Namespace arguments) {
        optionSetters.forEach(setter -> setter.accept(decompiler, arguments));
    }

    @Override
    void handle(Namespace arguments) {
        final File inputFile = arguments.get("input");
        final File outputDirectory = arguments.get("output-directory");
        final File outputFile = arguments.get("output");
        final File output = resolveOutputFile(inputFile, outputDirectory, outputFile, ".sdf");

        try (FileInputStream fis = new FileInputStream(inputFile)) {
            Schematic schematic = SchematicsIO.read(inputFile.getName(), fis);
            Decompiler decompiler = new Decompiler(schematic);
            configureDecompiler(decompiler, arguments);
            String schemaDefinition = decompiler.buildCode();

            writeOutput(output, schemaDefinition);
        } catch (IOException e) {
            throw new info.teksol.mindcode.cmdline.ProcessingException(e, "Error reading file '%s': %s", inputFile.getPath(), e.getMessage());
        }
    }
}
