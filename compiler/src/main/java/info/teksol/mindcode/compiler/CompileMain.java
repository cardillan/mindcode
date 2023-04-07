package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.compiler.optimization.Optimization;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static info.teksol.mindcode.compiler.CompilerFacade.compile;

public class CompileMain {
    static private String readStdin() throws IOException  {
        StringBuilder input = new StringBuilder();
        // No filenames? Read directly from stdin.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            input.append(line).append("\n");
        }
        return input.toString();
    }
    
    static private String readFile(String filename) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return out.toString();
        }
    }
    
    static public void main(String[] args) throws IOException {
        List<String> filenames = new ArrayList<>();
        CompilerProfile profile = CompilerProfile.noOptimizations();

        for (String arg : args) {
            if ("-?".equals(arg) || "--help".equals(arg)) {
                showHelp(0);
            } else if (arg.startsWith("-v")) {
                selectVersion(profile, arg.substring(2));
            } else if (arg.equals("-c")) {
                profile.setPrintFinalCode(true);
            } else if (arg.startsWith("-p")) {
                selectPrintLevel(profile, arg.substring(2));
            } else if (arg.startsWith("-o")) {
                selectOptimization(profile, arg.substring(2));
            } else if (arg.startsWith("-d")) {
                selectDebugLevel(profile, arg.substring(2));
            } else if (arg.startsWith("-")) {
                showHelp(2);
            } else {
                filenames.add(arg);
            }
        }

        if (filenames.size() > 3) {
            showHelp(2);
        }
        
        String contents = filenames.isEmpty() ? readStdin() : readFile(filenames.get(0));

        final CompilerOutput result = compile(contents, profile);

        if (!result.hasErrors()) {
            // No errors? Print the compiled code and any messages.
            if (filenames.size() >= 2) {
                Files.write(Path.of(filenames.get(1)), Collections.singletonList(result.getInstructions()));
            } else {
                System.out.println(result.getInstructions());
            }

            if (filenames.size() >= 3) {
                Files.write(Path.of(filenames.get(2)), result.getAllTexts());
            } else {
                result.getAllTexts().forEach(System.err::println);
            }
        } else {
            // Print errors to stderr (and log if given).
            result.getErrors().forEach(System.err::println);
            if (filenames.size() >= 3) {
                Files.write(Path.of(filenames.get(2)), result.getErrors());
            }
            System.exit(1);
        }
    }
    
    static private void selectOptimization(CompilerProfile profile, String flags) {
        if (flags.isEmpty()) {
            // -o alone activates all
            profile.setAllOptimizationLevels(OptimizationLevel.AGGRESSIVE);
            return;
        }
        
        EnumSet<Optimization> result = EnumSet.noneOf(Optimization.class);
        boolean negate = flags.startsWith("-");
        for (int i = negate ? 1 : 0; i < flags.length(); i++) {
            Optimization optimizer = Optimization.forFlag(flags.charAt(i));
            if (optimizer == null) {
                System.err.printf("Unknown optimizer flag %c%n", flags.charAt(i));
                showHelp(2);
            } else {
                result.add(optimizer);
            }
        }

        Optimization.LIST.forEach(o -> profile.setOptimizationLevel(o,
                (negate ^ result.contains(o)) ? OptimizationLevel.AGGRESSIVE : OptimizationLevel.OFF));
    }
    
    static private void selectVersion(CompilerProfile profile, String option) {
        switch(option.toUpperCase()) {
            case "6":  profile.setProcessorVersionEdition(ProcessorVersion.V6, ProcessorEdition.STANDARD_PROCESSOR); break;
            case "7":  profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR); break;
            case "7S": profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR); break;
            case "7W": profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR); break;
            default:   showHelp(2);
        }
    }

    static private void selectDebugLevel(CompilerProfile profile, String option) {
        switch(option) {
            case "0": profile.setDebugLevel(0); break;
            case "1": profile.setDebugLevel(1); break;
            case "2": profile.setDebugLevel(2); break;
            case "3": profile.setDebugLevel(3); break;
            default: showHelp(2);
        }
    }

    static private void selectPrintLevel(CompilerProfile profile, String option) {
        switch(option) {
            case "0": profile.setParseTreeLevel(0); break;
            case "":
            case "1": profile.setParseTreeLevel(1); break;
            case "2": profile.setParseTreeLevel(2); break;
            default: showHelp(2);
        }
    }

    static private final String[] HELP = new String[] {
        "Usage: mindcode [-vVersionEdition] [-c] [-p[Level]] [-dLevel] [-oFlags] [input file] [output file] [log file]",
            "  when input file is not given, input is read from stdin",
            "  when output file is not given, output is written to stdout",
            "  when log file is not given, messages are written to stderr",
            "",
            "-vVersionEdition: selects target processor version and edition:",
            "  6:  Mindustry Logic 6",
            "  7:  Mindustry Logic 7, standard processor (default)",
            "  7s: Mindustry Logic 7, standard processor",
            "  7w: Mindustry Logic 7, world processor",
            "",
            "-c: print compiled code with virtual instructions",
            "",
            "-pLevel: activates parse tree printing into the log file. Possible level values:",
            "  0: no parse tree printing",
            "  1: print parse tree, linearize nested Seq objects [default when no level given]",
            "  2: print parse tree with full indenting",
            "",
            "-dLevel: controls output of debug messages. Possible level values:",
            "  0: no debug output",
            "  1: output modifications made by all optimizers",
            "  2: output modifications made by individual optimizers",
            "  3: output modifications made by individual optimizers and iterations",
            "",
            "-oFlags: specifies which optimizers to use.",
            "  When no flags are given (-o), all optimizers are active",
            "  otherwise -o is followed by characters representing desired optimizers.",
            "Available optimizers are:"
    };
    
    static private void showHelp(int exitCode) {
        Stream.of(HELP).forEach(System.err::println);
        for (Optimization op : Optimization.values()) {
            System.err.printf("  %c: %s%n", op.getFlag(), op.getDescription());
        }
        Optimization op = Optimization.values()[0];
        System.err.printf("%nUse '-' to negate selection, e.g. -o-%c uses all optimization except %s.%n%n", op.getFlag(), op);
        System.exit(exitCode);
    }
}
