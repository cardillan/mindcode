package info.teksol.mindcode.mindustry;

import edu.emory.mathcs.backport.java.util.Collections;
import info.teksol.mindcode.mindustry.optimisation.Optimisation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static info.teksol.mindcode.mindustry.CompilerFacade.compile;

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
        CompileProfile profile = CompileProfile.noOptimizations();

        for (String arg : args) {
            if ("-?".equals(arg)) {
                showHelp(0);
            } else if (arg.equals("-p")) {
                profile.setPrintParseTree(true);
            } else if (arg.startsWith("-o")) {
                selectOptimisation(profile, arg.substring(2));
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

        if (result.getErrors().isEmpty()) {
            // No errors? Print the compiled code and any messages.
            if (filenames.size() >= 2) {
                Files.write(Path.of(filenames.get(1)), Collections.singletonList(result.getInstructions()));
            } else {
                System.out.println(result.getInstructions());
            }

            if (filenames.size() >= 3) {
                Files.write(Path.of(filenames.get(2)), result.getMessages());
            } else {
                result.getMessages().forEach(System.err::println);
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
    
    static private void selectOptimisation(CompileProfile profile, String flags) {
        if (flags.isEmpty()) {
            // -o alone activates all
            profile.setOptimisations(EnumSet.allOf(Optimisation.class));
            return;
        }
        
        EnumSet<Optimisation> result = EnumSet.noneOf(Optimisation.class);
        boolean negate = flags.startsWith("-");
        for (int i = negate ? 1 : 0; i < flags.length(); i++) {
            Optimisation optimizer = Optimisation.forFlag(flags.charAt(i));
            if (optimizer == null) {
                System.err.printf("Unknown optimizer flag %c%n", flags.charAt(i));
                showHelp(2);
            } else {
                result.add(optimizer);
            }
        }

        profile.setOptimisations(negate ? EnumSet.complementOf(result) : result);
    }
    
    static private final String[] HELP = new String[] {
        "Usage: mindcode.bat [-p] [-oflags] [input file] [output file] [log file]",
            "  when input file is not given, input is read from stdin",
            "  when output file is not given, output is written to stdout",
            "  when log file is not given, messages are written to stderr",
            "",
            "-p: prints parse tree into log file",
            "-oflags: specifies which optimisers to use.",
            "  When no flags are given (-o), all optimisers are active",
            "  otherwise -o is followed by characters representing desired optimisers.",
            "",
            "Available optimisers are:"
    };
    
    static private void showHelp(int exitCode) {
        Stream.of(HELP).forEach(System.err::println);
        for (Optimisation op : Optimisation.values()) {
            System.err.printf("  %c: %s%n", op.getFlag(), op.getDescription());
        }
        Optimisation op = Optimisation.values()[0];
        System.err.printf("%nUse '-' to negate selection, eg. -o-%c uses all optimisation except %s.%n%n", op.getFlag(), op);
        System.exit(exitCode);
    }
}
