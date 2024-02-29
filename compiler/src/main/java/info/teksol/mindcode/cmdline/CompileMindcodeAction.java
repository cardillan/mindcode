package info.teksol.mindcode.cmdline;

import info.teksol.mindcode.cmdline.Main.Action;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.File;
import java.util.List;

import static info.teksol.mindcode.compiler.CompilerFacade.compile;

public class CompileMindcodeAction extends ActionHandler {

    @Override
    Subparser appendSubparser(Subparsers subparsers, FileArgumentType inputFileType) {
        Subparser subparser = subparsers.addParser(Action.COMPILE_MINDCODE.getShortcut())
                .aliases("compile-mindcode")
                .description("Compile a mindcode source file into text mlog file.")
                .help("Compile a mindcode source file into text mlog file.");

        subparser.addArgument("-c", "--clipboard")
                .help("copy compiled mlog code to clipboard")
                .action(Arguments.storeTrue());

        ArgumentGroup files = subparser.addArgumentGroup("input/output files");

        files.addArgument("input")
                .help("Mindcode file to be compiled into an mlog file; uses stdin when not specified.")
                .nargs("?")
                .type(inputFileType.acceptSystemIn())
                .setDefault(new File("-"));

        files.addArgument("output")
                .help("Output file to receive compiled mlog code; uses input file with .mlog extension when not specified, " +
                        "or stdout when input is stdin. Use \"-\" to force stdout output.")
                .nargs("?")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate());

        files.addArgument("-l", "--log")
                .help("Output file to receive compiler messages; uses input file with .log extension when no file is specified.")
                .type(Arguments.fileType().acceptSystemIn().verifyCanCreate())
                .nargs("?")
                .setDefault(new File("-"));

        configureMindcodeCompiler(subparser);

        return subparser;
    }

    @Override
    void handle(Namespace arguments) {
        CompilerProfile compilerProfile = createCompilerProfile(arguments);
        String sourceCode = readInput(arguments.get("input"));

        final CompilerOutput<String> result = compile(sourceCode, compilerProfile);

        File output = resolveOutputFile(arguments.get("input"), arguments.get("output"), ".mlog");
        File logFile = resolveOutputFile(arguments.get("input"), arguments.get("log"), ".log");
        boolean mlogToStdErr = isStdInOut(output);

        if (!result.hasErrors()) {
            writeOutput(output, result.output(), false);
            List<String> allTexts = result.texts();

            if (arguments.getBoolean("clipboard")) {
                writeToClipboard(result.output());
                allTexts.add("");
                allTexts.add("Compiled code was copied to the clipboard.");
            }

            // If mlog gets written to stdout, write log to stderr
            writeOutput(logFile, allTexts, mlogToStdErr);

            // Print errors and warnings to console anyway
            if (!isStdInOut(logFile)) {
                result.messages().stream()
                        .filter(m -> m.isError() || m.isWarning())
                        .map(CompilerMessage::message)
                        .forEach(mlogToStdErr ? System.err::println : System.out::println);
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
