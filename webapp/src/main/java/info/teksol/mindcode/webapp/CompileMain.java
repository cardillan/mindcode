package info.teksol.mindcode.webapp;

import info.teksol.mindcode.Tuple2;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static info.teksol.mindcode.webapp.CompilerFacade.compile;

public class CompileMain {
    static private String readFile(String filename) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return out.toString();
        }
    }

    static private boolean compileMindcode(String contents, boolean optimise) {
        final Tuple2<String, List<String>> result = compile(contents, optimise);

        final String compiledCode = result._1;
        final List<String> syntaxErrors = result._2;

        if (!syntaxErrors.isEmpty()) {
            // Print errors to stderr.
            for (int i = 0; i < syntaxErrors.size(); i++) {
                System.err.println(syntaxErrors.get(i));
            }
            return false;
        } else {
            // No errors? Print the compiled code.
            System.out.println(compiledCode);
            return true;
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

        if (filename == null) {
            // No filenames? Read directly from stdin.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String contents = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                contents += line + "\n";
            }
            compileMindcode(contents, optimise);

        } else {
            String contents = readFile(filename);
            if (!compileMindcode(contents, optimise)) {
                System.exit(1);
            }
        }
    }
}
