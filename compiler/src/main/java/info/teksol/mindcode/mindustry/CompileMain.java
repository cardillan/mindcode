package info.teksol.mindcode.mindustry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static info.teksol.mindcode.mindustry.CompilerFacade.compile;

public class CompileMain {
    static private String readFile(String filename) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return out.toString();
        }
    }

    static private boolean compileMindcode(String contents, boolean optimise) {
        final CompilerOutput result = compile(contents, optimise);

        if (result.getErrors().isEmpty()) {
            // No errors? Print the compiled code and any messages.
            System.out.println(result.getInstructions());
            result.getMessages().forEach(System.err::println);
            return true;
        } else {
            // Print errors to stderr.
            result.getErrors().forEach(System.err::println);
            return false;
        }
    }

    static public void main(String[] args) throws IOException {
        boolean optimise = false;
        String filename = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o")) {
                optimise = true;
            } else if (filename == null) {
                // Store the first non-option string found.
                filename = args[i];
            }
        }

        String contents = "";

        if (filename == null) {
            // No filenames? Read directly from stdin.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            while ((line = br.readLine()) != null) {
                contents += line + "\n";
            }
        } else {
            contents = readFile(filename);
        }

        if (!compileMindcode(contents, optimise)) {
            System.exit(1);
        }
    }
}
