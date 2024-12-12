package info.teksol.annotations.processor;

import com.squareup.javapoet.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("info.teksol.annotations.AstNode")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class AstNodeAnnotationProcessor extends AbstractProcessor {
    private static final String PACKAGE_NAME = "info.teksol.generated.ast";
    private static final String INTERFACE_NAME = "AstNodeVisitor";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            generateVisitorInterface(annotatedElements);
        }
        return true;
    }

    private void generateVisitorInterface(Set<? extends Element> annotatedElements) {
        ClassName visitorClassName = ClassName.get(PACKAGE_NAME, INTERFACE_NAME);
        TypeSpec.Builder visitorBuilder = TypeSpec.interfaceBuilder(visitorClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(NullUnmarked.class)
                .addTypeVariable(TypeVariableName.get("T"));

        for (Element element : annotatedElements) {
            if (element.getKind() == ElementKind.CLASS) {
                ClassName className = ClassName.get((TypeElement) element);
                MethodSpec visitMethod = MethodSpec.methodBuilder("visit")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(ParameterSpec.builder(className, "node")
                                        .addAnnotation(NonNull.class).build())
                        .returns(TypeVariableName.get("T"))
                        .build();
                visitorBuilder.addMethod(visitMethod);
            }
        }

        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, visitorBuilder.build())
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write Visitor interface: " + e.getMessage());
        }
    }
}
