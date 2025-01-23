package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstEnhancedComment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.FormattableContent;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingValue;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@NullMarked
public class BuiltinFunctionTextOutputBuilder extends AbstractFunctionBuilder {
    private static final Pattern PLACEHOLDER_MATCHER = Pattern.compile("\\{\\d}");

    public BuiltinFunctionTextOutputBuilder(AbstractBuilder builder) {
        super(builder);
    }

    public ValueStore handlePrintf(AstFunctionCall call) {
        if (!processor.isSupported(Opcode.FORMAT)) {
            error("The printf function requires language target 8 or higher.");
            return LogicVoid.VOID;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> arguments = processArguments(call);
        FunctionArgument.validateAsInput(messageConsumer(), arguments);

        if (arguments.isEmpty()) {
            error(call, "Not enough arguments to the '%s' function (expected 1 or more, found %d).",
                    call.getFunctionName(), call.getArguments().size());
            return LogicVoid.VOID;
        }

        if (arguments.getFirst().getArgumentValue() instanceof LogicString str) {
            long placeholders = PLACEHOLDER_MATCHER.matcher(str.format(processor)).results().count();
            if (placeholders == 0) {
                warn(call, "The 'printf' function is called with a literal format string which doesn't contain any format placeholders.");
            }
            if (placeholders > arguments.size() - 1) {
                warn(call, "The 'printf' function doesn't have enough arguments for placeholders: %d placeholder(s), %d argument(s).",
                        placeholders, arguments.size() - 1);
            } else if (placeholders < arguments.size() - 1) {
                warn(call, "The 'printf' function has more arguments than placeholders: %d placeholder(s), %d argument(s).",
                        placeholders, arguments.size() - 1);
            }
            warn(call, "The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code.");
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
                error(call, "Not enough arguments to the '%s' function (expected 1 or more, found %d).",
                        call.getFunctionName(), arguments.size());
            } else if (formatter.createsNewLine()) {
                formatter.createInstruction(assembler, LogicString.NEW_LINE);
            }
            assembler.clearSubcontextType();
            return LogicVoid.VOID;
        }

        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        ValueStore result = arguments.getFirst().getArgumentValue() instanceof FormattableContent formattable
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
            if (part == MissingValue.FORMATTABLE_PLACEHOLDER) {
                if (enhancedComment) {
                    error(part, "Formattable placeholders not supported in enhanced comments.");
                } else if (index == arguments.size()) {
                    error(sourceElement, "Not enough arguments for formattable placeholders.");
                } else {
                    formatter.createInstruction(assembler, arguments.get(index).getValue(assembler));
                }
                index++;
            } else {
                formatter.createInstruction(assembler, part.getValue(assembler));
            }
        }

        if (index < arguments.size()) {
            error(sourceElement, "Too many arguments for formattable placeholders.");
        }

        return LogicVoid.VOID;
    }

    private ValueStore createPlainOutput(Formatter formatter, List<FunctionArgument> arguments) {
        // The non-formattable variant of the call
        // Just output an instruction for every argument
        arguments.forEach(argument -> formatter.createInstruction(assembler, argument.getValue(assembler)));
        return arguments.getLast().getArgumentValue();
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
