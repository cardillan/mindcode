package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstEnhancedComment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingValue;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import static info.teksol.mc.messages.ERR.*;

@NullMarked
public class BuiltinFunctionTextOutputBuilder extends AbstractFunctionBuilder {
    private static final Pattern PLACEHOLDER_MATCHER = Pattern.compile("\\{\\d}");

    public BuiltinFunctionTextOutputBuilder(AbstractBuilder builder) {
        super(builder);
    }

    public ValueStore handleAscii(AstFunctionCall call) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);

        ValueStore result;
        if (validateStandardFunctionArguments(call, arguments, 1)) {
            if (arguments.getFirst().unwrap() instanceof LogicString str && !str.getValue().isEmpty()) {
                result = LogicNumber.create(str.getValue().charAt(0));
            } else {
                error(call, ASCII_INVALID_ARGUMENT);
                result = LogicVoid.VOID;
            }
        } else {
            result = LogicVoid.VOID;
        }

        assembler.clearSubcontextType();
        return result;
    }

    public ValueStore handlePrintf(AstFunctionCall call) {
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(call.getIdentifier(), PRINTF_REQUIRES_TARGET_8);
            return LogicVoid.VOID;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);
        FunctionArgument.validateAsInput(messageConsumer(), arguments);

        if (arguments.isEmpty()) {
            error(call, FUNCTION_CALL_NOT_ENOUGH_ARGS, call.getFunctionName(), 1, call.getArguments().size());
            assembler.clearSubcontextType();
            return LogicVoid.VOID;
        }

        if (arguments.getFirst().unwrap() instanceof LogicString str) {
            long placeholders = PLACEHOLDER_MATCHER.matcher(str.format(processor)).results().count();
            if (placeholders == 0) {
                warn(arguments.getFirst(), WARN.PRINTF_NO_PLACEHOLDERS);
            }
            if (placeholders > arguments.size() - 1) {
                warn(pos(arguments), WARN.PRINTF_NOT_ENOUGH_ARGUMENTS, placeholders, arguments.size() - 1);
            } else if (placeholders < arguments.size() - 1) {
                warn(pos(arguments), WARN.PRINTF_TOO_MANY_ARGUMENTS, placeholders, arguments.size() - 1);
            }
            warn(arguments.getFirst(), WARN.PRINTF_WITH_LITERAL_FORMAT);
        }
        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);

        assembler.createPrint(arguments.getFirst().getValue(assembler));
        arguments.stream().skip(1).forEach(argument -> assembler.createFormat(argument.getValue(assembler)));
        assembler.clearSubcontextType();
        return arguments.isEmpty() ? LogicNull.NULL : arguments.getLast();
    }

    public ValueStore handleTextOutput(AstFunctionCall call, Formatter formatter) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);
        FunctionArgument.validateAsInput(messageConsumer(), arguments);

        if (arguments.isEmpty()) {
            if (formatter.requiresParameter()) {
                error(call, FUNCTION_CALL_NOT_ENOUGH_ARGS, call.getFunctionName(), 1, arguments.size());
            } else if (formatter.createsNewLine()) {
                formatter.createInstruction(assembler, LogicString.NEW_LINE);
            }
            assembler.clearSubcontextType();
            return LogicVoid.VOID;
        }

        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        ValueStore result = arguments.getFirst().unwrap() instanceof FormattableContent formattable
                ? createFormattableOutput(formattable, false, formatter,
                    evaluateExpressionsUncached(formattable.getParts()),
                    arguments.subList(1, arguments.size()))
                : createPlainOutput(formatter, arguments);

        if (formatter.createsNewLine()) {
            formatter.createInstruction(assembler, LogicString.NEW_LINE);
        }

        assembler.clearSubcontextType();
        return result;
    }

    public void handleEnhancedComment(AstEnhancedComment comment) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        // Enhanced comment cannot be reused. No need for evaluateExpressionsUncached.
        List<ValueStore> parts = evaluateExpressions(comment.getParts());
        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        createFormattableOutput(comment, true, Formatter.REMARK, parts, List.of());
        assembler.clearSubcontextType();
    }

    private ValueStore createFormattableOutput(SourceElement sourceElement, boolean enhancedComment,
            Formatter formatter, List<ValueStore> parts, List<FunctionArgument> arguments) {
        int index = 0;
        for (ValueStore part : parts) {
            if (part instanceof MissingValue) {
                if (enhancedComment) {
                    error(part, ENHANCED_COMMENTS_NO_PLACEHOLDERS);
                } else if (index == arguments.size()) {
                    error(part, FORMATTABLE_NOT_ENOUGH_ARGS);
                } else {
                    formatter.createInstruction(assembler, arguments.get(index).getValue(assembler));
                }
                index++;
            } else {
                formatter.createInstruction(assembler, part.getValue(assembler));
            }
        }

        if (index < arguments.size()) {
            error(pos(arguments.get(index), arguments.getLast()), FORMATTABLE_TOO_MANY_ARGS);
        }

        return LogicVoid.VOID;
    }

    private ValueStore createPlainOutput(Formatter formatter, List<FunctionArgument> arguments) {
        // The non-formattable variant of the call
        // Just output an instruction for every argument
        arguments.forEach(argument -> formatter.createInstruction(assembler, argument.getValue(assembler)));
        return arguments.getLast().unwrap();
    }

    public enum Formatter {
        PRINT("print", CodeAssembler::createPrint),
        PRINTLN("println", CodeAssembler::createPrint),
        REMARK("remark", CodeAssembler::createRemark);

        final String function;
        final BiConsumer<CodeAssembler, LogicValue> creator;

        Formatter(String function, BiConsumer<CodeAssembler, LogicValue> creator) {
            this.function = function;
            this.creator = creator;
        }

        void createInstruction(CodeAssembler assembler, LogicValue value) {
            creator.accept(assembler, value);
        }

        boolean createsNewLine() {
            return this == PRINTLN;
        }

        boolean requiresParameter() {
            return this != PRINTLN;
        }
    }
}
