package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructorContext;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContextFactory {
    private static final ThreadLocal<Map<Class<? extends CompilerContext>, Object>> contexts = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<@Nullable MindcodeCompiler> compilerContext = new ThreadLocal<>();

    public static <E extends CompilerContext> void setContext(Class<E> clazz, E context) {
        contexts.get().put(clazz, context);
    }

    public static <E> void removeContext(Class<E> clazz) {
        contexts.get().remove(clazz);
    }

    public static void clearContexts() {
        contexts.get().clear();
        compilerContext.remove();
    }

    static void setCompilerContext(MindcodeCompiler compiler) {
        compilerContext.set(compiler);
    }

    @SuppressWarnings("unchecked")
    private static <E extends CompilerContext> E getContext(Class<E> clazz) {
        E context = (E) contexts.get().get(clazz);
        return context == null ? (E) getMasterContext() : context;
    }


    // Root method for getting compiler contexts
    // Allows finding all out-of-line usages of compiler context through call hierarchy.
    public static MindcodeCompiler getMasterContext() {
        return Objects.requireNonNull(compilerContext.get());
    }



    public static ArrayConstructorContext getArrayConstructorContext() {
        return getContext(ArrayConstructorContext.class);
    }

    public static void setArrayConstructorContext(ArrayConstructorContext context) {
        setContext(ArrayConstructorContext.class, context);
    }

    public static void clearArrayConstructorContext() {
        removeContext(ArrayConstructorContext.class);
    }



    public static MessageContext getMessageContext() {
        return getContext(MessageContext.class);
    }

    public static void setMessageContext(MessageContext context) {
        setContext(MessageContext.class, context);
    }

    public static void clearMessageContext() {
        removeContext(MessageContext.class);
    }



    public static ForcedVariableContext getForcedVariableContext() {
        return getContext(ForcedVariableContext.class);
    }

    public static void setForcedVariableContext(ForcedVariableContext context) {
        setContext(ForcedVariableContext.class, context);
    }

    public static void clearForcedVariableContext() {
        removeContext(ForcedVariableContext.class);
    }
}
