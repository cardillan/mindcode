package info.teksol.mc.mindcode;

import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.AbstractAstBuilderTest;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.profile.options.*;
import info.teksol.mc.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

@NullMarked
@Order(5)
public class DocGeneratorTest {
    private static final String PREFIX = "#generate";
    private static final String SOURCE_FILE = "src/test/resources/library/doc/SYSTEM-LIBRARY_template.markdown";
    private static final String TARGET_FILE = "../doc/syntax/SYSTEM-LIBRARY.markdown";
    private static final String LIBRARY_DIRECTORY = "src/main/resources/library";
    private static final String FOOTPRINT = "@footprint";
    private static final String FOOTPRINT2 = "@footprint2";

    @Test
    void generateLibraryDocumentation() throws IOException {
        Path path = Path.of(SOURCE_FILE);

        try (final PrintWriter w = new PrintWriter(TARGET_FILE, StandardCharsets.UTF_8); Stream<String> lineStream = Files.lines(path)) {
            DocGenerator docGenerator = new DocGenerator(w);
            lineStream.forEach(docGenerator::processTemplateLine);
        }
    }

    private static class DocGenerator extends AbstractAstBuilderTest {
        private final PrintWriter writer;

        public DocGenerator(PrintWriter writer) {
            this.writer = writer;
        }

        protected CompilerProfile createCompilerProfile() {
            return super.createCompilerProfile().setRun(false);
        }

        private void processTemplateLine(String line) {
            if (line.startsWith(PREFIX)) {
                processAllLibraries();
            } else {
                writer.println(line);
            }
        }

        private void processAllLibraries() {
            try (Stream<Path> stream = Files.list(Paths.get(LIBRARY_DIRECTORY))) {
                List<Path> files = stream
                        .filter(f -> f.toString().endsWith(".mnd"))
                        //.filter(f -> f.toString().endsWith("units.mnd"))
                        .toList();
                assertFalse(files.isEmpty(), "Expected to find at least one script in " + LIBRARY_DIRECTORY + "; found none");
                for (Path file : files) {
                    processLibrary(file);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Nullable String libraryName;
        List<AstFunctionDeclaration> functions = new ArrayList<>();
        List<AstVariablesDeclaration> constants = new ArrayList<>();
        List<AstParameter> parameters = new ArrayList<>();
        @Nullable AstFunctionDeclaration declaration;

        private void processLibrary(Path file) throws IOException {
            String fileName = file.getFileName().toString();
            libraryName = fileName.substring(0, fileName.lastIndexOf("."));
            String code = Files.readString(file);

            // Parse and process the module
            functions.clear();
            constants.clear();
            parameters.clear();

            AstModule node = build(expectedMessages(), createInputFiles(code));
            processNode(node);

            writer.println();
            writer.println("# " + StringUtils.firstUpperCase(libraryName) + " library");
            writer.println();
            processModuleDoc(code);

            if (!parameters.isEmpty()) {
                writer.println();
                writer.println("## Program parameters");
                parameters.stream().filter(parameter -> !parameter.getParameterName().startsWith("_")).forEach(this::processParameter);
            }

            if (constants.stream().flatMap(d -> d.getVariables().stream()).anyMatch(specification -> !specification.getName().startsWith("_"))) {
                writer.println();
                writer.println("## Constants");
                constants.forEach(
                        declaration -> declaration.getVariables().stream()
                                .filter(specification -> !specification.getName().startsWith("_"))
                                .forEach(specification -> processConstant(declaration, specification))
                );
            }

            if (!functions.isEmpty()) {
                writer.println();
                writer.println("## Functions");
                functions.stream().filter(function -> !function.getName().startsWith("_")).forEach(this::processFunction);
            }
        }

        private void processModuleDoc(String code) {
            for (String line : code.lines().toList()) {
                if (!line.startsWith("//")) {
                    return;
                } else if (line.startsWith("//*")) {
                    writer.println(line.substring(line.startsWith("//* ") ? 4 : 3).trim());
                }
            }
        }

        private void processNode(AstMindcodeNode node) {
            switch (node) {
                case AstParameter n -> parameters.add(n);
                case AstFunctionDeclaration n -> functions.add(n);
                case AstVariablesDeclaration n when n.isConstantDeclaration() -> constants.add(n);
                default -> {
                }
            }

            node.getChildren().forEach(this::processNode);
        }

        private void printValue(AstMindcodeNode node) {
            if (node instanceof AstLiteral literal) {
                writer.print(literal.getLiteral());
            }
        }

        private void processParameter(AstParameter parameter) {
            writer.println();
            writer.println("### " + parameter.getParameterName());
            writer.println();

            writer.print("**Definition:** `param ");
            writer.print(parameter.getParameterName());
            writer.print(" = ");
            printValue(parameter.getValue());
            writer.println(";`");

            if (parameter.getDocComment() != null) {
                writer.println();
                writeDocComment(parameter.getDocComment().getComment());
            }
        }

        private void processConstant(AstVariablesDeclaration declaration, AstVariableSpecification specification) {
            writer.println();
            writer.println("### " + specification.getName());
            writer.println();

            writer.print("**Definition:** `const ");
            writer.print(specification.getName());
            writer.print(" = ");
            printValue(specification.getExpressions().getFirst());
            writer.println(";`");

            if (declaration.getDocComment() != null) {
                writer.println();
                writeDocComment(declaration.getDocComment().getComment());
            }
        }

        private void processFunction(AstFunctionDeclaration declaration) {
            this.declaration = declaration;
            writer.println();
            writer.println("### " + declaration.getName());
            writer.println();
            writeFunction();

            writer.println();
            writer.printf("| %-30s | %19s | %18s |%n", "Compiled code size when...", "optimized for speed", "optimized for size");
            writer.printf("|-%-30s-|-%19s:|-%18s:|%n", dashes(30), dashes(19), dashes(18));

            AstDocComment docComment = declaration.getDocComment();
            if (declaration.isVarargs() || docComment != null && docComment.getComment().contains(FOOTPRINT2)) {
                List<FootprintConfig> footprintConfigs = processFootprints(docComment);
                footprintConfigs.forEach(footprintConfig -> {
                    int speed = measureFootprint(footprintConfig, GenerationGoal.SPEED);
                    int size = measureFootprint(footprintConfig, GenerationGoal.SIZE);
                    writer.printf("| %-30s | %19s | %18s |%n", footprintConfig.title, speed, size);
                });
            } else {
                int speed = measureFootprint(null, GenerationGoal.SPEED);
                int size = measureFootprint(null, GenerationGoal.SIZE);
                if (!declaration.isNoinline()) {
                    writer.printf("| %-30s | %19s | %18s |%n", "Inlined function", speed, size);
                } else {
                    // Implement if noinline function is ever added to the library
                    throw new UnsupportedOperationException("Size calculation for noinline functions is not supported");
                }
                if (!declaration.isInline()) {
                    int callSize = declaration.computeCallSize();
                    // +1 for the return from the function call
                    writer.printf("| %-30s | %19s | %18s |%n", "Function body", speed + 1, size + 1);
                    writer.printf("| %-30s | %19s | %18s |%n", "Function call", callSize, callSize);
                }
            }

            if (docComment != null) {
                writer.println();
                writeDocComment(docComment.getComment());
            }
        }

        private final List<FootprintConfig> defaultFootprintConfigs = List.of(
                new FootprintConfig("Five arguments in total", null, 5, false),
                new FootprintConfig("Ten arguments in total", null, 10, false),
                new FootprintConfig("Twenty arguments in total", null, 20, false)
        );

        private List<FootprintConfig> processFootprints(@Nullable AstDocComment docComment) {
            if (docComment == null) {
                return defaultFootprintConfigs;
            } else {
                List<FootprintConfig> list = docComment.getComment().lines().filter(l -> l.contains(FOOTPRINT)).map(this::processFootprint).toList();
                return list.isEmpty() ? defaultFootprintConfigs : list;
            }
        }

        private FootprintConfig processFootprint(String directive) {
            if (directive.contains(FOOTPRINT2)) {
                return processFootprint2(directive);
            }

            int index = directive.indexOf(FOOTPRINT);
            String trimmed = directive.substring(index + FOOTPRINT.length()).trim();
            int index2 = trimmed.indexOf(':');
            if (index2 == -1) {
                throw new MindcodeInternalError("Malformed %s directive", FOOTPRINT);
            }
            String title = trimmed.substring(0, index2);
            String declaration = trimmed.substring(index2 + 1).trim();
            if (declaration.startsWith("*")) {
                return new FootprintConfig(title, null, Integer.parseInt(declaration.substring(1)), false);
            } else {
                return new FootprintConfig(title, declaration, 0, false);
            }
        }

        private FootprintConfig processFootprint2(String directive) {
            int index = directive.indexOf(FOOTPRINT2);
            String trimmed = directive.substring(index + FOOTPRINT2.length()).trim();
            int index2 = trimmed.indexOf(':');
            if (index2 == -1) {
                throw new MindcodeInternalError("Malformed %s directive", FOOTPRINT2);
            }
            String title = trimmed.substring(0, index2);
            String declaration = trimmed.substring(index2 + 1).trim();
            return new FootprintConfig(title, declaration, 0, true);
        }

        private int measureFootprint(@Nullable FootprintConfig footprintConfig, GenerationGoal goal) {
            Objects.requireNonNull(declaration);
            int createdParams = 0;

            StringBuilder code = new StringBuilder()
                    .append("require ").append(libraryName).append(";\n")
                    .append("\n");

            List<AstFunctionParameter> inputs = declaration.getParameters().stream()
                    .filter(p -> !p.isVarargs() && p.isInput())
                    .toList();

            createdParams += inputs.size();
            inputs.forEach(parameter -> code.append("param prm_").append(parameter.getName()).append(" = 0;\n"));

            List<String> additionalOutputs = new ArrayList<>();
            declaration.getParameters().stream()
                    .filter(p -> !p.isVarargs() && p.isOutput())
                    .map(AstFunctionParameter::getName)
                    .map(name -> "prm_" + name)
                    .forEach(additionalOutputs::add);

            boolean customCode = footprintConfig != null && footprintConfig.code;
            String varargs = null;
            if (footprintConfig != null) {
                if (customCode) {
                    assert footprintConfig.declarations != null;
                    code.append(footprintConfig.declarations).append("\n");
                    createdParams += countOccurrences(footprintConfig.declarations, " param ");
                } else if (footprintConfig.declarations == null) {
                    int count = footprintConfig.argCount - declaration.getParameters().size() + 1;
                    if (count < 0) {
                        throw new MindcodeInternalError("Invalid argument count");
                    }
                    for (int i = 0; i < count; i++) {
                        code.append("param prm_").append(i).append(" = ").append(i).append(";\n");
                    }
                    createdParams += count;
                    varargs = IntStream.range(0, count).mapToObj(i -> "prm_" + i).collect(Collectors.joining(", "));
                } else {
                    varargs = footprintConfig.declarations;
                    for (String vararg : footprintConfig.declarations.split(" *, *")) {
                        int index = vararg.indexOf("prm_");
                        if (index != -1) {
                            String name = vararg.substring(index);
                            code.append("param ").append(name).append(" = ").append(name.substring(4)).append(";\n");
                            createdParams++;
                        }
                        if (vararg.startsWith("out ")) {
                            additionalOutputs.add(vararg.substring(4));
                        }
                    }
                }
            }

            if (!customCode) {
                code.append("\n");
                if (declaration.getDataType() != DataType.VOID) code.append("print(");
                code.append(declaration.getName()).append("(");

                boolean first = true;
                for (AstFunctionParameter parameter : declaration.getParameters()) {
                    if (first) {
                        first = false;
                    } else {
                        code.append(", ");
                    }
                    if (parameter.isVarargs()) {
                        if (varargs != null) {
                            code.append(varargs);
                        }
                    } else {
                        if (parameter.isOutput()) code.append("out ");
                        code.append("prm_").append(parameter.getName());
                    }
                }
                code.append(")");
                if (declaration.getDataType() != DataType.VOID) code.append(")");
                code.append(";\n");
            }

            if (!additionalOutputs.isEmpty()) {
                code.append(additionalOutputs.stream().collect(Collectors.joining(",", "print(", ");\n")));
            }

            //System.out.println(code);

            CompilerProfile profile = createCompilerProfile()
                    .setTarget(ProcessorVersion.MAX, ProcessorEdition.W)
                    .setAllOptimizationLevels(OptimizationLevel.EXPERIMENTAL)
                    .setGoal(goal)
                    .setSignature(false)
                    .setLibraryPrecedence(true);

            MindcodeCompiler compiler = new MindcodeCompiler(CompilationPhase.ALL,
                    expectedMessages().add("The 'loop' keyword is deprecated. Use just 'while' instead.").ignored(),
                    profile,
                    createInputFiles(code.toString()));

            compiler.compile();

            return compiler.getInstructions().size()
                   - (declaration.getDataType() == DataType.VOID ? 0 : 1)
                   - createdParams
                   - additionalOutputs.size();
        }

        private void writeFunction() {
            Objects.requireNonNull(declaration);
            writer.print("**Definition:** `");
            if (declaration.isInline()) writer.print("inline ");
            if (declaration.isNoinline()) writer.print("noinline ");
            writer.print(declaration.getDataType() == DataType.VOID ? "void " : "def ");
            writer.print(declaration.getName());
            writer.print("(");
            boolean first = true;
            for (AstFunctionParameter parameter : declaration.getParameters()) {
                if (first) {
                    first = false;
                } else {
                    writer.print(", ");
                }
                if (parameter.hasInModifier()) writer.print("in ");
                if (parameter.hasOutModifier()) writer.print("out ");
                writer.print(parameter.getName().substring(parameter.getName().charAt(0) == '_' ? 1 : 0));
                if (parameter.isVarargs()) writer.print("...");
            }
            writer.println(")`");
        }

        private void writeDocComment(String docComment) {
            String commentText = docComment.substring(3, docComment.length() - 2);
            if (!commentText.contains("\n")) {
                writer.println(commentText.trim());
            } else {
                commentText.lines().forEach(this::writeLine);
            }
        }

        private void writeLine(String line) {
            String trimmed = line.trim();
            if (!trimmed.contains(FOOTPRINT)) {
                if (trimmed.startsWith("*")) {
                    writer.println(trimmed.substring(trimmed.startsWith("* ") ? 2 : 1));
                } else if (!trimmed.isEmpty()) {
                    writer.println(line);
                }
            }
        }

        private record FootprintConfig(String title, @Nullable String declarations, int argCount, boolean code) {
        }

        private String dashes(int count) {
            final char[] array = new char[count];
            Arrays.fill(array, '-');
            return new String(array);
        }
    }

    private static int countOccurrences(String str, String sub) {
        if (sub.isEmpty()) return 0;

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length(); // move past this match to avoid overlapping
        }

        return count;
    }

    @Test
    <T> void verifyOptionList() throws IOException {
        Map<Enum<?>, CompilerOptionValue<?>> compilerOptions = CompilerOptionFactory.createCompilerOptions(false);
        List<CompilerOption> list = compilerOptions.values().stream()
                .map(CompilerOption.class::cast)
                .filter(option -> option.getCategory() != OptionCategory.DEBUGGING)
                .filter(option -> option.getAvailability() == OptionAvailability.UNIVERSAL || option.getAvailability() == OptionAvailability.DIRECTIVE)
                .sorted(Comparator.comparing(CompilerOption::getOptionName))
                .toList();

        final String TEMPLATE = "| %-31s | %-6s | %-23s | %-18s |%n";
        System.out.printf(TEMPLATE, "Option", "Scope", "Category", "Semantic stability");
        System.out.printf("|---------------------------------|--------|-------------------------|--------------------|%n");
        list.forEach(option -> System.out.printf(TEMPLATE, option.getOptionName(), option.getScope().name().toLowerCase(),
                option.getCategory().title, option.getStability().name().toLowerCase()));
    }
}
