package info.teksol.annotations.processor;

import com.squareup.javapoet.*;
import info.teksol.annotations.AstNode;
import info.teksol.annotations.BaseClass;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"info.teksol.annotations.AstNode", "info.teksol.annotations.BaseClass"})
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class AstNodeAnnotationProcessor extends AbstractProcessor {
    private static final String PACKAGE_NAME = "info.teksol.generated.ast";
    private static final String INTERFACE_NAME = "AstNodeVisitor";
    private static final String PRINTER_CLASS_NAME = "AstIndentedPrinter";
    private static final String TOSTRING_CLASS_NAME = "AstNodeToString";
    private static final String NODE_BASE_CLASS = "AstMindcodeNode";

    private Map<String, TypeElement> baseClasses;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        baseClasses = annotations
                .stream()
                .filter(e -> "BaseClass".equals(e.getSimpleName().toString()))
                .flatMap(a -> roundEnv.getElementsAnnotatedWith(a).stream())
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .collect(Collectors.toMap(e -> e.getAnnotation(BaseClass.class).value(), e -> e));

        List<TypeElement> astNodes = annotations
                .stream()
                .filter(e -> "AstNode".equals(e.getSimpleName().toString()))
                .flatMap(a -> roundEnv.getElementsAnnotatedWith(a).stream())
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .toList();

        generateVisitorInterface(astNodes);
        generateAstIndentedPrinter(astNodes);
        return true;
    }

    private String visitFunctionName(TypeElement element) {
        return element.getSimpleName().toString().startsWith("Ast")
                ? "visit" + element.getSimpleName().toString().substring(3)
                : "visit" + element.getSimpleName().toString();
    }

    private void generateVisitorInterface(List<TypeElement> elements) {
        Element pkg = elements.getFirst().getEnclosingElement();
        if (pkg.getKind() != ElementKind.PACKAGE) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Expected package, got " + pkg.getKind());
        }
        ClassName baseType = ClassName.get(((PackageElement) pkg).getQualifiedName().toString(), NODE_BASE_CLASS);
        int maxLen = elements.stream().mapToInt(e -> e.getSimpleName().toString().length()).max().orElse(0);
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("return switch (astNode)");

        ClassName visitorInterfaceName = ClassName.get(PACKAGE_NAME, INTERFACE_NAME);
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(visitorInterfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NullUnmarked.class)
                .addTypeVariable(TypeVariableName.get("T"));

        for (TypeElement element : elements) {
            ClassName className = ClassName.get(element);
            MethodSpec visitMethod = MethodSpec.methodBuilder(visitFunctionName(element))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(ParameterSpec.builder(className, "node")
                            .addAnnotation(NonNull.class).build())
                    .returns(TypeVariableName.get("T"))
                    .build();
            classBuilder.addMethod(visitMethod);

            codeBlockBuilder.add("case $T node$L-> $L(node);\n", className,
                    " ".repeat(maxLen - element.getSimpleName().toString().length() + 1),
                    visitFunctionName(element));
        }

        codeBlockBuilder
                .add("default -> throw new IllegalArgumentException(\"Unexpected node type: \" + astNode.getClass().getName());\n")
                .unindent()
                .add("};");

        MethodSpec visitMethod = MethodSpec.methodBuilder("visit")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(baseType, "astNode")
                        .addAnnotation(NonNull.class).build())
                .returns(TypeVariableName.get("T"))
                .addCode(codeBlockBuilder.build())
                .build();
        classBuilder.addMethod(visitMethod);
        
        MethodSpec visitTreeMethod = MethodSpec.methodBuilder("visitTree")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(baseType, "astNode")
                        .addAnnotation(NonNull.class).build())
                .addParameter(ParameterSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(BinaryOperator.class),
                                TypeVariableName.get("T")), "accumulator").build())
                .returns(TypeVariableName.get("T"))
                .addCode(CodeBlock.builder()
                        .add("""
                                T value = visit(astNode);
                                for ($T child : astNode.getChildren()) {
                                    value = accumulator.apply(value, visitTree(child, accumulator));
                                }
                                return value;
                                """, baseType).build())
                .build();
        classBuilder.addMethod(visitTreeMethod);

        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, classBuilder.build())
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write AstNodeVisitor interface: " + e.getMessage());
        }
    }

    private final Set<String> accessorPrefixHas = Set.of("inModifier", "outModifier");
    private final Set<String> ignoredFields = Set.of("inputPosition", "children", "docComment");

    private String getAccessorMethod(VariableElement field) {
        String name = field.getSimpleName().toString();
        if (accessorPrefixHas.contains(name)) {
            return "has" + name.substring(0, 1).toUpperCase() + name.substring(1) + "()";
        } else if (field.asType().getKind() == TypeKind.BOOLEAN) {
            return "is" + name.substring(0, 1).toUpperCase() + name.substring(1) + "()";
        } else {
            return "get" + name.substring(0, 1).toUpperCase() + name.substring(1) + "()";
        }
    }

    private TypeElement getSuperclass(TypeElement element) {
        DeclaredType superclass = (DeclaredType) element.getSuperclass();
        TypeElement parent = (TypeElement) superclass.asElement();
        return parent.getKind() == ElementKind.CLASS && !parent.getSimpleName().toString().equals("Object")
                ? parent : null;
    }

    private void generateAstIndentedPrinter(List<TypeElement> elements) {
        ClassName visitorInterfaceName = ClassName.get(PACKAGE_NAME, INTERFACE_NAME);
        ClassName astPrinterClassName = ClassName.get(PACKAGE_NAME, PRINTER_CLASS_NAME);
        TypeSpec.Builder printerClassBuilder = TypeSpec.classBuilder(astPrinterClassName)
                .superclass(ClassName.get(baseClasses.get(PRINTER_CLASS_NAME)))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NullUnmarked.class)
                ;
        
        ClassName astToStringClassName = ClassName.get(PACKAGE_NAME, TOSTRING_CLASS_NAME);
        TypeSpec.Builder toStringClassBuilder = TypeSpec.classBuilder(astToStringClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(visitorInterfaceName, ClassName.get(String.class)))
                .addAnnotation(NullUnmarked.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addField(FieldSpec.builder(astToStringClassName, "INSTANCE",
                                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", astToStringClassName)
                        .build());

        for (TypeElement element : elements) {
            boolean printFlat = element.getAnnotation(AstNode.class).printFlat();

            List<TypeElement> hierarchy = new ArrayList<>();
            hierarchy.add(element);
            for (TypeElement parent = getSuperclass(element); parent != null; parent = getSuperclass(parent)) {
                hierarchy.addFirst(parent);
            }

            List<VariableElement> list = hierarchy.stream()
                    .flatMap(e -> e.getEnclosedElements().stream())
                    .filter(e -> e.getKind() == ElementKind.FIELD)
                    .map(VariableElement.class::cast)
                    .filter(e -> !ignoredFields.contains(e.getSimpleName().toString()))
                    .toList();

            CodeBlock.Builder printerCode = CodeBlock.builder();
            CodeBlock.Builder toStringCode = CodeBlock.builder();

            if (list.isEmpty()) {
                printerCode.add("newLine(\"$L{}\");", element.getSimpleName()).add("return null;");
                toStringCode.add("return \"$L{}\";", element.getSimpleName());
            } else if (printFlat) {
                printerCode.add("print(\"$L{$L = \"); printObject(node.$L); ",
                        element.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));
                toStringCode.add("return \"$L{$L=\" + node.$L",
                        element.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));

                for (VariableElement field : list.subList(1, list.size())) {
                    printerCode.add("\nprint(\", $L = \"); printObject(node.$L); ", field.getSimpleName(), getAccessorMethod(field));
                    toStringCode.add("\n    + \", $L=\" + node.$L", field.getSimpleName(), getAccessorMethod(field));
                }
                printerCode.add("""
                        print("}");
                        return null;
                        """);
                toStringCode.add("\n    + \"}\";");
            } else {
                printerCode.add("open(\"$L {\");\nprint(\"$L = \"); printObject(node.$L);",
                        element.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));
                toStringCode.add("return \"$L{$L=\" + node.$L",
                        element.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));
                for (VariableElement field : list.subList(1, list.size())) {
                    printerCode.add(" newLine(\",\");\nprint(\"$L = \"); printObject(node.$L);", field.getSimpleName(), getAccessorMethod(field));
                    toStringCode.add("\n    + \",\\n$L=\" + node.$L", field.getSimpleName(), getAccessorMethod(field));
                }
                printerCode.add("close(\"}\");\nreturn null;");
                toStringCode.add("\n    + \"}\";");
            }

            ClassName className = ClassName.get(element);
            MethodSpec printMethod = MethodSpec.methodBuilder(visitFunctionName(element))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(ParameterSpec.builder(className, "node")
                            .addAnnotation(NonNull.class).build())
                    .returns(TypeVariableName.get(String.class))
                    .addCode(printerCode.build())
                    .build();

            printerClassBuilder.addMethod(printMethod);

            MethodSpec toStringMethod = MethodSpec.methodBuilder(visitFunctionName(element))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(ParameterSpec.builder(className, "node")
                            .addAnnotation(NonNull.class).build())
                    .returns(TypeVariableName.get(String.class))
                    .addCode(toStringCode.build())
                    .build();

            toStringClassBuilder.addMethod(toStringMethod);

        }

        try {
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, printerClassBuilder.build())
                    .indent("    ")
                    .build();

            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write AstIndentedPrinter class: " + e.getMessage());
        }

        try {
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, toStringClassBuilder.build())
                    .indent("    ")
                    .build();

            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write AstNodeToString class: " + e.getMessage());
        }
    }
}
