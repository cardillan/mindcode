package info.teksol.annotations.processor;

import com.squareup.javapoet.*;
import info.teksol.annotations.AstNode;
import info.teksol.annotations.BaseClass;
import org.jspecify.annotations.NullMarked;

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
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"info.teksol.annotations.AstNode", "info.teksol.annotations.BaseClass"})
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class AstNodeAnnotationProcessor extends AbstractProcessor {
    private static final String GENERATED_PACKAGE_NAME = "info.teksol.mc.generated.ast";
    private static final String GENERATED_PACKAGE_NAME_VISITORS = "info.teksol.mc.generated.ast.visitors";
    private static final String NODE_BASE_CLASS = "AstMindcodeNode";

    private static final String VISITOR_ROOT_INTERFACE_NAME = "SingleAstNodeVisitor";
    private static final String VISITOR_INTERFACE_NAME = "AstNodeVisitor";
    private static final String COMPOSED_VISITOR_CLASS_NAME = "ComposedAstNodeVisitor";
    private static final String PRINTER_CLASS_NAME = "AstIndentedPrinter";
    private static final String TOSTRING_CLASS_NAME = "AstNodeToString";

    private String nodePackageName;
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

        List<TypeElement> astNodeTypes = annotations
                .stream()
                .filter(e -> "AstNode".equals(e.getSimpleName().toString()))
                .flatMap(a -> roundEnv.getElementsAnnotatedWith(a).stream())
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                // This is a horrible hack: we need subclasses processed before superclasses.
                // By coincidence, sorting by name works
                .sorted(Comparator.comparing(t -> t.getSimpleName().toString()))
                .toList();

        Element basePackage = astNodeTypes.getFirst().getEnclosingElement();
        if (basePackage.getKind() != ElementKind.PACKAGE) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Expected package, got " + basePackage.getKind());
        }
        nodePackageName = ((PackageElement) basePackage).getQualifiedName().toString();

        generateSingleVisitorInterfaces(astNodeTypes);
        generateVisitorInterface(astNodeTypes);
        generateComposedVisitorClass(astNodeTypes);
        generateAstIndentedPrinter(astNodeTypes);
        return true;
    }

    private String firstLowerCase(String s) {
        return s.isEmpty() ? "" : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private String firstUpperCase(String s) {
        return s.isEmpty() ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void createClass(String packageName, TypeSpec.Builder classBuilder) {
        try {
            JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build()).indent("    ").build();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error generating code: " + e.getMessage());
        }
    }

    private String elementBaseName(TypeElement element) {
        return element.getSimpleName().toString().startsWith("Ast")
                ? element.getSimpleName().toString().substring(3)
                : element.getSimpleName().toString();
    }

    private String visitFunctionName(TypeElement element) {
        return "visit" + elementBaseName(element);
    }

    private String visitorFieldName(TypeElement element) {
        return firstLowerCase(elementBaseName(element)) + "Visitor";
    }

    private void generateSingleVisitorInterfaces(List<TypeElement> astNodeTypes) {
        ClassName baseType = ClassName.get(nodePackageName, NODE_BASE_CLASS);

        ClassName baseInterfaceName = ClassName.get(GENERATED_PACKAGE_NAME_VISITORS, VISITOR_ROOT_INTERFACE_NAME);
        TypeSpec.Builder baseClassBuilder = TypeSpec.interfaceBuilder(baseInterfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("T"));
        createClass(GENERATED_PACKAGE_NAME_VISITORS, baseClassBuilder);

        ParameterizedTypeName baseTypeName = ParameterizedTypeName.get(
                ClassName.get(GENERATED_PACKAGE_NAME_VISITORS, VISITOR_ROOT_INTERFACE_NAME),
                TypeVariableName.get("T"));

        for (TypeElement nodeTypeElement : astNodeTypes) {
            ClassName nodeType = ClassName.get(nodeTypeElement);
            ClassName visitorInterfaceName = ClassName.get(GENERATED_PACKAGE_NAME_VISITORS, nodeType.simpleName() + "Visitor");
            TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(visitorInterfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(baseTypeName)
                    .addAnnotation(NullMarked.class)
                    .addTypeVariable(TypeVariableName.get("T"));

            MethodSpec visitMethod = MethodSpec.methodBuilder(visitFunctionName(nodeTypeElement))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(nodeType, "node")
                    .returns(TypeVariableName.get("T"))
                    .build();
            classBuilder.addMethod(visitMethod);
            createClass(GENERATED_PACKAGE_NAME_VISITORS, classBuilder);
        }
    }

    private void generateVisitorInterface(List<TypeElement> astNodeTypes) {
        ClassName baseType = ClassName.get(nodePackageName, NODE_BASE_CLASS);
        int maxLen = astNodeTypes.stream().mapToInt(e -> e.getSimpleName().toString().length()).max().orElse(0);
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("return switch (astNode)");

        ClassName visitorInterfaceName = ClassName.get(GENERATED_PACKAGE_NAME, VISITOR_INTERFACE_NAME);
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(visitorInterfaceName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NullMarked.class)
                .addTypeVariable(TypeVariableName.get("T"))
                ;

        for (TypeElement nodeTypeElement : astNodeTypes) {
            ClassName nodeType = ClassName.get(nodeTypeElement);
            classBuilder.addSuperinterface(ParameterizedTypeName.get(
                    ClassName.get(GENERATED_PACKAGE_NAME_VISITORS, nodeType.simpleName() + "Visitor"),
                    TypeVariableName.get("T")));

            codeBlockBuilder.add("case $T node$L-> $L(node);\n", nodeType,
                    " ".repeat(maxLen - nodeTypeElement.getSimpleName().toString().length() + 1),
                    visitFunctionName(nodeTypeElement));
        }

        codeBlockBuilder
                .add("default -> throw new IllegalArgumentException(\"Unexpected node type: \" + astNode.getClass().getName());\n")
                .unindent()
                .add("};");

        MethodSpec visitMethod = MethodSpec.methodBuilder("visit")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(baseType, "astNode")
                .returns(TypeVariableName.get("T"))
                .addCode(codeBlockBuilder.build())
                .build();
        classBuilder.addMethod(visitMethod);
        
        MethodSpec visitTreeMethod = MethodSpec.methodBuilder("visitTree")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(baseType, "astNode")
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
//
//        MethodSpec transformMethod = MethodSpec.methodBuilder("transform")
//                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
//                .addParameter(baseType, "astNode")
//                .returns(baseType)
//                .addCode(CodeBlock.builder()
//                        .add("""
//                                return astNode;
//                                """, baseType).build())
//                .build();
//        classBuilder.addMethod(transformMethod);

        createClass(GENERATED_PACKAGE_NAME, classBuilder);
    }

    private void generateComposedVisitorClass(List<TypeElement> astNodeTypes) {
        ClassName astBaseTypeName = ClassName.get(nodePackageName, NODE_BASE_CLASS);
        ClassName visitorInterfaceName = ClassName.get(GENERATED_PACKAGE_NAME, VISITOR_INTERFACE_NAME);
        ClassName visitorClassName = ClassName.get(GENERATED_PACKAGE_NAME, COMPOSED_VISITOR_CLASS_NAME);
        ParameterizedTypeName implementedType = ParameterizedTypeName.get(visitorInterfaceName, TypeVariableName.get("T"));
        TypeSpec.Builder visitorClassBuilder = TypeSpec.classBuilder(visitorClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(implementedType)
                .addAnnotation(NullMarked.class)
                .addTypeVariable(TypeVariableName.get("T"))
                ;

        ParameterizedTypeName functionType = ParameterizedTypeName.get(ClassName.get(Function.class),
                astBaseTypeName, TypeVariableName.get("T"));
        visitorClassBuilder.addField(
                FieldSpec.builder(functionType, "defaultVisitor", Modifier.PRIVATE)
                .build());

        visitorClassBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode("defaultVisitor = node -> null;")
                .build());

        visitorClassBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(functionType, "defaultVisitor")
                .addCode("this.defaultVisitor = $T.requireNonNull(defaultVisitor);", Objects.class)
                .build());

        MethodSpec unhandledVisit = MethodSpec.methodBuilder("unhandledVisit")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(astBaseTypeName, "node")
                .returns(TypeVariableName.get("T"))
                .addCode(CodeBlock.of("return defaultVisitor.apply(node);"))
                .build();
        visitorClassBuilder.addMethod(unhandledVisit);


        CodeBlock.Builder registerMethodCode = CodeBlock.builder();

        for (TypeElement nodeTypeElement : astNodeTypes) {
            String visitorFieldName = visitorFieldName(nodeTypeElement);
            ClassName nodeType = ClassName.get(nodeTypeElement);
            ClassName elementVisitorClass = ClassName.get(GENERATED_PACKAGE_NAME_VISITORS, nodeType.simpleName() + "Visitor");

            ParameterizedTypeName typeName = ParameterizedTypeName.get(
                    elementVisitorClass,
                    TypeVariableName.get("T"));
            visitorClassBuilder.addField(FieldSpec.builder(typeName, visitorFieldName, Modifier.PRIVATE)
                    .initializer("this::unhandledVisit")
                    .build());

            MethodSpec visitMethod = MethodSpec.methodBuilder(visitFunctionName(nodeTypeElement))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(nodeType, "node")
                    .addAnnotation(Override.class)
                    .returns(TypeVariableName.get("T"))
                    .addCode(CodeBlock.builder().add("return $L.$L(node);", visitorFieldName, visitFunctionName(nodeTypeElement)).build())
                    .build();
            visitorClassBuilder.addMethod(visitMethod);

            registerMethodCode.add("""
                    if (visitor instanceof $T v) {
                        $L = v;
                    }
                    """, elementVisitorClass, visitorFieldName);
        }

        ParameterizedTypeName baseTypeName = ParameterizedTypeName.get(
                ClassName.get(GENERATED_PACKAGE_NAME_VISITORS, VISITOR_ROOT_INTERFACE_NAME),
                TypeVariableName.get("T"));

        MethodSpec registerMethod = MethodSpec.methodBuilder("registerVisitor")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(baseTypeName, "visitor")
                .addCode(registerMethodCode.build())
                .build();

        visitorClassBuilder.addMethod(registerMethod);
        createClass(GENERATED_PACKAGE_NAME, visitorClassBuilder);
    }

    private final Set<String> accessorPrefixHas = Set.of("inModifier", "outModifier", "refModifier", "declaration");
    private final Set<String> ignoredFields = Set.of("profile", "sourcePosition", "children", "docComment");

    private String getAccessorMethod(VariableElement field) {
        String name = field.getSimpleName().toString();
        if (field.asType().getKind() == TypeKind.BOOLEAN) {
            if (accessorPrefixHas.contains(name)) {
                return "has" + firstUpperCase(name) + "()";
            } else {
                return "is" + firstUpperCase(name) + "()";
            }
        } else {
            return "get" + firstUpperCase(name) + "()";
        }
    }

    private TypeElement getSuperclass(TypeElement element) {
        DeclaredType superclass = (DeclaredType) element.getSuperclass();
        TypeElement parent = (TypeElement) superclass.asElement();
        return parent.getKind() == ElementKind.CLASS && !parent.getSimpleName().toString().equals("Object")
                ? parent : null;
    }

    private void generateAstIndentedPrinter(List<TypeElement> astNodeTypes) {
        ClassName visitorInterfaceName = ClassName.get(GENERATED_PACKAGE_NAME, VISITOR_INTERFACE_NAME);
        ClassName astPrinterClassName = ClassName.get(GENERATED_PACKAGE_NAME, PRINTER_CLASS_NAME);
        TypeSpec.Builder printerClassBuilder = TypeSpec.classBuilder(astPrinterClassName)
                .superclass(ClassName.get(baseClasses.get(PRINTER_CLASS_NAME)))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NullMarked.class)
                ;
        
        ClassName astToStringClassName = ClassName.get(GENERATED_PACKAGE_NAME, TOSTRING_CLASS_NAME);
        TypeSpec.Builder toStringClassBuilder = TypeSpec.classBuilder(astToStringClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(visitorInterfaceName, ClassName.get(String.class)))
                .addAnnotation(NullMarked.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addField(FieldSpec.builder(astToStringClassName, "INSTANCE",
                                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", astToStringClassName)
                        .build());

        for (TypeElement nodeTypeElement : astNodeTypes) {
            boolean printFlat = nodeTypeElement.getAnnotation(AstNode.class).printFlat();

            List<TypeElement> hierarchy = new ArrayList<>();
            hierarchy.add(nodeTypeElement);
            for (TypeElement parent = getSuperclass(nodeTypeElement); parent != null; parent = getSuperclass(parent)) {
                hierarchy.addFirst(parent);
            }

            List<VariableElement> list = hierarchy.stream()
                    .flatMap(e -> e.getEnclosedElements().stream())
                    .filter(e -> e.getKind() == ElementKind.FIELD)
                    .map(VariableElement.class::cast)
                    .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                    .filter(e -> !ignoredFields.contains(e.getSimpleName().toString()))
                    .toList();

            CodeBlock.Builder printerCode = CodeBlock.builder();
            CodeBlock.Builder toStringCode = CodeBlock.builder();

            if (list.isEmpty()) {
                printerCode.add("newLine(\"$L{}\");", nodeTypeElement.getSimpleName()).add("return null;");
                toStringCode.add("return \"$L{}\";", nodeTypeElement.getSimpleName());
            } else if (printFlat) {
                printerCode.add("print(\"$L{$L = \"); printObject(node.$L); ",
                        nodeTypeElement.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));
                toStringCode.add("return \"$L{$L=\" + node.$L",
                        nodeTypeElement.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));

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
                        nodeTypeElement.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));
                toStringCode.add("return \"$L{$L=\" + node.$L",
                        nodeTypeElement.getSimpleName(), list.getFirst().getSimpleName(), getAccessorMethod(list.getFirst()));
                for (VariableElement field : list.subList(1, list.size())) {
                    printerCode.add(" newLine(\",\");\nprint(\"$L = \"); printObject(node.$L);", field.getSimpleName(), getAccessorMethod(field));
                    toStringCode.add("\n    + \",\\n$L=\" + node.$L", field.getSimpleName(), getAccessorMethod(field));
                }
                printerCode.add("close(\"}\");\nreturn null;");
                toStringCode.add("\n    + \"}\";");
            }

            ClassName nodeType = ClassName.get(nodeTypeElement);
            MethodSpec printMethod = MethodSpec.methodBuilder(visitFunctionName(nodeTypeElement))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(nodeType, "node")
                    .returns(TypeVariableName.get(String.class))
                    .addCode(printerCode.build())
                    .build();

            printerClassBuilder.addMethod(printMethod);

            MethodSpec toStringMethod = MethodSpec.methodBuilder(visitFunctionName(nodeTypeElement))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(nodeType, "node")
                    .returns(TypeVariableName.get(String.class))
                    .addCode(toStringCode.build())
                    .build();

            toStringClassBuilder.addMethod(toStringMethod);

        }

        createClass(GENERATED_PACKAGE_NAME, printerClassBuilder);
        createClass(GENERATED_PACKAGE_NAME, toStringClassBuilder);
    }
}
