package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.MissingFunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class BuiltinFunctionMlogBuilder extends AbstractFunctionBuilder {

    public BuiltinFunctionMlogBuilder(AbstractBuilder builder) {
        super(builder);
    }

    public ValueStore handleMlog(AstFunctionCall call, boolean safe, boolean text) {
        if (call.getArguments().isEmpty()) {
            error(call, ERR.FUNCTION_CALL_NOT_ENOUGH_ARGS,
                    call.getFunctionName(), 1, call.getArguments().size());
            return LogicVoid.VOID;
        }

        assembler.setSubcontextType(AstSubcontextType.ARGUMENTS, 1.0);
        final List<FunctionArgument> args = processArguments(call);

        if (args.getFirst().hasInModifier() || args.getFirst().hasOutModifier()) {
            error(args.getFirst(), ERR.MLOG_FIRST_ARGUMENT_NO_MODIFIERS, call.getFunctionName());
        }

        final String opcode;
        if (args.getFirst().unwrap() instanceof LogicString str) {
            opcode = str.format(processor);
        } else {
            error(args.getFirst(), ERR.MLOG_FIRST_ARGUMENT_NOT_LITERAL, call.getFunctionName());
            opcode = "noop";
        }

        List<LogicArgument> arguments = new ArrayList<>();
        List<InstructionParameterType> parameters = new ArrayList<>();
        for (FunctionArgument arg : args.subList(1, args.size())) {
            if (arg.unwrap() == LogicVariable.INVALID && !(arg instanceof MissingFunctionArgument)) {
                // The error has been reported elsewhere
            } else if (!arg.hasValue()) {
                error(arg, ERR.MLOG_UNSPECIFIED_ARGUMENT, call.getFunctionName());
            } else if (arg.unwrap() instanceof LogicKeyword keyword) {
                if (arg.hasOutModifier() || arg.hasInModifier()) {
                    error(arg, ERR.MLOG_IN_OUT_KEYWORD_NOT_ALLOWED, call.getFunctionName());
                }
                arguments.add(keyword);
                parameters.add(InstructionParameterType.UNSPECIFIED);
            } else if (arg.unwrap() instanceof LogicString str) {
                if (arg.hasOutModifier()) {
                    error(arg, ERR.MLOG_OUT_STRING_NOT_ALLOWED, call.getFunctionName());
                }
                arguments.add(arg.hasInModifier() ? str : LogicKeyword.create(str.sourcePosition(), str.format(processor)));
                parameters.add(arg.hasInModifier() ? InstructionParameterType.INPUT : InstructionParameterType.UNSPECIFIED);
            } else if (arg.unwrap() instanceof LogicLiteral lit) {
                if (arg.hasOutModifier() || arg.hasInModifier()) {
                    error(arg, ERR.MLOG_IN_OUT_LITERAL_NOT_ALLOWED, call.getFunctionName());
                }
                arguments.add(lit);
                parameters.add(InstructionParameterType.INPUT);
            } else {
                LogicValue value = arg.getValue(assembler);
                if (!value.isUserVariable()) {
                    error(arg, ERR.MLOG_NO_LITERAL_OR_VARIABLE, call.getFunctionName());
                } else {
                    if (!arg.hasInModifier() && !arg.hasOutModifier()) {
                        error(arg, ERR.MLOG_VARIABLE_WITHOUT_MODIFIERS, call.getFunctionName());
                    }
                    arguments.add(value);
                    if (arg.hasOutModifier()) {
                        parameters.add(arg.hasInModifier() ? InstructionParameterType.INPUT_OUTPUT : InstructionParameterType.OUTPUT);
                    } else {
                        parameters.add(InstructionParameterType.INPUT);
                    }
                }
            }
        }

        assembler.createCustomInstruction(safe, text, opcode, arguments, parameters);
        assembler.setSubcontextType(AstSubcontextType.SYSTEM_CALL, 1.0);
        assembler.clearSubcontextType();

        return LogicVoid.VOID;
    }
}
