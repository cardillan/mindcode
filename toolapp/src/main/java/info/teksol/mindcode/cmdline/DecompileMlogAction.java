package info.teksol.mindcode.cmdline;

import info.teksol.mc.mindcode.decompiler.MlogDecompiler;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.cmdline.Main.Action;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.jspecify.annotations.NullMarked;

import java.io.File;

@NullMarked
public class DecompileMlogAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType, CompilerProfile defaults) {
        Subparser subparser = subparsers.addParser(Action.DECOMPILE_MLOG.getShortcut())
                .aliases("decompile-mlog")
                .description("Partially decompile a text mlog file into Mindcode source file.")
                .help("Decompile a text mlog file into Mindcode source, leaving jumps and unknown instructions unresolved.");

        subparser.addArgument("input")
                .help("Mlog text file to be decompiled into Mindcode source file.")
                .type(inputFileType);

        subparser.addArgument("-o", "--output")
                .help("Output file to receive decompiled Mindcode; uses input file name with .dmnd extension if not specified.")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        subparser.addArgument("--output-directory")
                .dest("output-directory")
                .help("show program's version number and exit")
                .type(Arguments.fileType().verifyIsDirectory());

        return subparser;
    }

    @Override
    void handle(Namespace arguments) {
        final File inputFile = arguments.get("input");
        final File outputDirectory = arguments.get("output-directory");
        final File outputFile = arguments.get("output");
        final File output = resolveOutputFile(inputFile, outputDirectory, outputFile, ".dmnd");
        String mlog = readInput(inputFile);
        String decompiled = MlogDecompiler.decompile(mlog);
        writeOutput(output, decompiled);
    }
}
