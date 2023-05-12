package info.teksol.mindcode.cmdline;

import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schema.Decompiler;
import info.teksol.schemacode.schema.Schematics;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DecompileSchemacodeAction extends ActionHandler {

    @Override
    void configureSubparsers(Subparsers subparsers, FileArgumentType inputFileType) {
        Subparser subparser = subparsers.addParser("ds")
                .aliases("decompile-schema")
                .description("Decompile a binary msch file into schema definition file.")
                .help("Decompile a binary msch file into schema definition file.");

        subparser.addArgument("input")
                .help("Mindustry schema file to be decompiled into Schema Definition File")
                .type(inputFileType);

        subparser.addArgument("output")
                .help("Output file to receive compiled mlog code; uses input file name with .sdf extension if not specified")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        subparser.addArgument("-p", "--relative-positions")
                .help("Use relative coordinates for block positions where possible")
                .dest("positions")
                .action(Arguments.storeTrue())
                .setDefault(false);

        subparser.addArgument("-P", "--absolute-positions")
                .help("Use absolute coordinates for block positions")
                .dest("positions")
                .action(Arguments.storeFalse())
                .setDefault(false);

        subparser.addArgument("-c", "--relative-connections")
                .help("Use relative coordinates for connections")
                .dest("connections")
                .action(Arguments.storeTrue())
                .setDefault(true);

        subparser.addArgument("-C", "--absolute-connections")
                .help("Use absolute coordinates for connections")
                .dest("connections")
                .action(Arguments.storeFalse())
                .setDefault(true);

        subparser.addArgument("-l", "--relative-links")
                .help("Use relative coordinates for processor links")
                .dest("links")
                .action(Arguments.storeTrue())
                .setDefault(false);

        subparser.addArgument("-L", "--absolute-links")
                .help("Use absolute coordinates for processor links")
                .dest("links")
                .action(Arguments.storeFalse())
                .setDefault(false);
    }

    @Override
    void handle(Namespace arguments) {
        File input = arguments.get("input");
        File output = resolveOutputFile(input, arguments.get("output"), ".sdf");

        try (FileInputStream fis = new FileInputStream(input)) {
            Schematics schematics = SchematicsIO.read(fis);
            Decompiler decompiler = new Decompiler(schematics);
            decompiler.setRelativePositions(arguments.getBoolean("positions"));
            decompiler.setRelativeConnections(arguments.getBoolean("connections"));
            decompiler.setRelativeLinks(arguments.getBoolean("links"));
            String schemaDefinition = decompiler.buildCode();

            writeOutput(output, schemaDefinition, false);
        } catch (IOException e) {
            throw new info.teksol.mindcode.cmdline.ProcessingException(e, "Error reading file %s.", input.getPath());
        }
    }
}
