package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.cmdline.Main.Action;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.schemacode.SchemacodeCompiler;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class CompileSchemacodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Subparser subparser = subparsers.addParser(Action.COMPILE_SCHEMA.getShortcut())
                .aliases("compile-schema", "compile-schematic")
                .description("Compile a schematic definition file into binary msch file.")
                .help("Compile a schematic definition file into binary msch file.");

        subparser.addArgument("-c", "--clipboard")
                .help("encode the created schematic into text representation and paste into clipboard")
                .action(Arguments.storeTrue());

        ArgumentGroup files = subparser.addArgumentGroup("input/output files");

        files.addArgument("input")
                .help("Schematic definition file to be compiled into a binary msch file.")
                .nargs("?")
                .type(inputFileType.acceptSystemIn())
                .setDefault(new File("-"));

        files.addArgument("output")
                .help("Output file to receive the resulting binary Mindustry schematic file (.msch).")
                .nargs("?")
                .type(Arguments.fileType().verifyCanCreate());

        files.addArgument("-l", "--log")
                .help("output file to receive compiler messages; uses stdout/stderr when not specified")
                .nargs("?")
                .type(Arguments.fileType().verifyCanCreate())
                .setDefault(new File("-"));

        ArgumentGroup schematics = subparser.addArgumentGroup("schematic creation");

        schematics.addArgument("-a", "--add-tag")
                .help("defines additional tag(s) to add to the schematic, plain text and symbolic icon names are supported")
                .metavar("TAG")
                .type(String.class)
                .nargs("+")
                .setDefault(List.of());

        addAllCompilerOptions(subparser, defaults);

        return subparser;
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile compilerProfile = createCompilerProfile(arguments);
        compilerProfile.setAdditionalTags(arguments.get("add_tag"));
        File inputFile = arguments.get("input");
        String sourceText = readInput(inputFile);
        Path basePath = isStdInOut(inputFile) ? Paths.get("") : inputFile.toPath().toAbsolutePath().getParent();

        CompilerOutput<byte[]> result = SchemacodeCompiler.compile(sourceText , compilerProfile, basePath);

        File output = resolveOutputFile(inputFile, arguments.get("output"), ".msch");
        File logFile = resolveOutputFile(inputFile, arguments.get("log"), ".log");

        if (!result.hasErrors()) {
            writeOutput(output, result.output());
            List<String> allTexts = result.texts();

            if (arguments.getBoolean("clipboard")) {
                writeToClipboard(Base64.getEncoder().encodeToString(result.output()));
                allTexts.add("");
                allTexts.add("Created schematic was copied to the clipboard.");
            }

            writeOutput(logFile, allTexts, false);

            // Print errors and warnings to console anyway
            if (!isStdInOut(logFile)) {
                result.messages().stream()
                        .filter(m -> m.isError() || m.isWarning())
                        .map(MindcodeMessage::message)
                        .forEach(System.out::println);
            }
        } else {
            // Errors: print just them into stderr
            result.errors().forEach(System.err::println);
            if (!isStdInOut(logFile)) {
                writeOutput(logFile, result.errors(), true);
            }
            System.exit(1);
        }
    }
}

