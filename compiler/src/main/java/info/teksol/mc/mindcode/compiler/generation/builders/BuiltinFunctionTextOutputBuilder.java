package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstEnhancedComment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingValue;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static info.teksol.mc.messages.ERR.*;

@NullMarked
public class BuiltinFunctionTextOutputBuilder extends AbstractFunctionBuilder {
    private static final Pattern PLACEHOLDER_MATCHER = Pattern.compile("\\{\\d}");

    public BuiltinFunctionTextOutputBuilder(AbstractCodeBuilder builder) {
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

    public ValueStore handleChar(AstFunctionCall call) {
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(call.getIdentifier(), FUNCTION_REQUIRES_TARGET_8, call.getFunctionName());
            return LogicVoid.VOID;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);

        ValueStore result;
        if (validateStandardFunctionArguments(call, arguments, 2)) {
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            LogicValue text = arguments.get(0).getValue(assembler);
            LogicValue index = arguments.get(1).getValue(assembler);
            LogicVariable output = assembler.nextNodeResultTemp();
            assembler.createRead(output, text, index);
            result = output;
        } else {
            result = LogicVoid.VOID;
        }

        assembler.clearSubcontextType();
        return result;
    }

    public ValueStore handleEncode(AstFunctionCall call) {
        // Encode is compile-time evaluated. If it gets there, it couldn't be evaluated, and the error has already been reported.
        return LogicVariable.INVALID;
    }

    public ValueStore handleStrlen(AstFunctionCall call) {
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(call.getIdentifier(), FUNCTION_REQUIRES_TARGET_8, call.getFunctionName());
            return LogicNull.NULL;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);

        ValueStore result;
        if (validateStandardFunctionArguments(call, arguments, 1)) {
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            LogicValue text = arguments.getFirst().getValue(assembler);
            LogicVariable output = assembler.nextNodeResultTemp();
            assembler.createSensor(output, text, LogicBuiltIn.SIZE);
            result = output;
        } else {
            result = LogicNull.NULL;
        }

        assembler.clearSubcontextType();
        return result;
    }

    public ValueStore handleError(AstFunctionCall call) {
        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        List<FunctionArgument> arguments = processArguments(call);
        FunctionArgument.validateAsInput(messageConsumer(), arguments);

        if (arguments.isEmpty()) {
            error(call, FUNCTION_CALL_NOT_ENOUGH_ARGS, call.getFunctionName(), 1, call.getArguments().size());
        } else {
            assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
            List<LogicValue> finalArgs = arguments.getFirst().unwrap() instanceof FormattableContent formattable
                    ? createFormattableErrorOutput(
                    evaluateExpressionsUncached(formattable.getParts()), arguments.subList(1, arguments.size()))
                    : createPlainErrorOutput(arguments);

            if (call.getProfile().isErrorFunction()) {
                Consumer<List<LogicValue>> errorAssembler = switch (call.getProfile().getErrorReporting()) {
                    case NONE -> _ -> {};
                    case ASSERT -> mlogAssertionsErrorAssembler();
                    case MINIMAL, SIMPLE, DESCRIBED -> builtinErrorAssembler();
                };

                errorAssembler.accept(finalArgs);
            }
        }

        assembler.clearSubcontextType();
        return LogicVoid.VOID;
    }

    private List<LogicValue> createFormattableErrorOutput(List<ValueStore> parts, List<FunctionArgument> arguments) {
        int index = 0;
        StringBuilder sbr = new StringBuilder();
        List<LogicValue> values = new ArrayList<>();
        for (ValueStore part : parts) {
            if (part instanceof LogicString str) {
                sbr.append(str.getValue());
            } else {
                LogicValue value;
                if (part instanceof MissingValue) {
                    if (index == arguments.size()) {
                        error(part, FORMATTABLE_NOT_ENOUGH_ARGS);
                        continue;
                    }
                    value = arguments.get(index++).getValue(assembler);
                } else {
                    value = part.getValue(assembler);
                }

                if (value instanceof LogicLiteral lit) {
                    sbr.append(lit.format(processor));
                } else {
                    values.add(value);
                    sbr.append("[[").append(values.size()).append("]");
                }
            }
        }

        if (index < arguments.size()) {
            error(pos(arguments.get(index), arguments.getLast()), FORMATTABLE_TOO_MANY_ARGS);
        }

        values.addFirst(LogicString.create(sbr.toString()));
        return values;
    }




    private List<LogicValue> createPlainErrorOutput(List<FunctionArgument> arguments) {
        return arguments.stream().map(argument -> argument.getValue(assembler)).toList();
    }

    private Consumer<List<LogicValue>> builtinErrorAssembler() {
        return values -> {
            for (int index = 0; index < values.size(); index++) {
                assembler.createSet(LogicVariable.error(index), values.get(index));
            }
            assembler.createStop();
        };
    }

    private Consumer<List<LogicValue>> mlogAssertionsErrorAssembler() {
        return values -> {
            List<LogicArgument> list = new ArrayList<>(Collections.nCopies(10, LogicString.create("")));
            for (int index = 0; index < 10; index++) {
                if (index >= values.size()) break;
                list.set(index, values.get(index));
            }
            assembler.createInstruction(Opcode.ERROR, list);
        };
    }

    public ValueStore handlePrintf(AstFunctionCall call) {
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(call.getIdentifier(), FUNCTION_REQUIRES_TARGET_8, call.getFunctionName());
            return LogicNull.NULL;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);
        FunctionArgument.validateAsInput(messageConsumer(), arguments);

        if (arguments.isEmpty()) {
            error(call, FUNCTION_CALL_NOT_ENOUGH_ARGS, call.getFunctionName(), 1, call.getArguments().size());
            assembler.clearSubcontextType();
            return LogicNull.NULL;
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
        createPlainOutput(1, Formatter.FORMAT, arguments);
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
                : createPlainOutput(0, formatter, arguments);

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

    private ValueStore createPlainOutput(int start, Formatter formatter, List<FunctionArgument> arguments) {
        ValueStore result = LogicVariable.INVALID;

        for (int i = start; i < arguments.size(); i++) {
            FunctionArgument argument = arguments.get(i);
            if (i < arguments.size() - 1) {
                formatter.createInstruction(assembler, argument.getValue(assembler));
            } else {
                ValueStore unwrapped = argument.unwrap();
                if (unwrapped.isComplex() || unwrapped instanceof LogicArgument arg && arg.isVolatile()) {
                    LogicVariable temp = assembler.nextNodeResultTemp();
                    argument.readValue(assembler, temp);
                    formatter.createInstruction(assembler, temp);
                    result = temp;
                } else {
                    formatter.createInstruction(assembler, unwrapped.getValue(assembler));
                    result = unwrapped;
                }
            }
        }

        return result;
    }

    public enum Formatter {
        PRINT("print", CodeAssembler::createPrint),
        PRINTLN("println", CodeAssembler::createPrint),
        REMARK("remark", CodeAssembler::createRemark),
        FORMAT("", CodeAssembler::createFormat),
        ;

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

    private interface ErrorAssembler {
        void create(CodeAssembler assembler, List<ValueStore> values);
    }
}
