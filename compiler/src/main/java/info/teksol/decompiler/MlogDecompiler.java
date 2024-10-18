package info.teksol.decompiler;

import info.teksol.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.Operation;

import java.util.*;

public class MlogDecompiler {
    private final FunctionMapper functionMapper;
    private final Map<Integer, String> labels;
    private final List<Object> content;

    private final Set<String> usedLabels;
    private int labelIndex;

    private final StringBuilder output = new StringBuilder();

    private static final AstContext STATIC_AST_CONTEXT = AstContext.createStaticRootNode();

    public MlogDecompiler(InstructionProcessor instructionProcessor, ParsedMlog input) {
        functionMapper = FunctionMapperFactory.getFunctionMapper(instructionProcessor,
                () -> STATIC_AST_CONTEXT, instructionProcessor.getMessageConsumer());
        labels = input.labels();
        content = input.content();
        usedLabels = new HashSet<>(labels.size());
    }

    public String decompile() {
        extractJumpLabels();
        insertTextLabels();
        collapseExpressions();

        for (Object o : content) {
            if (o instanceof MlogInstruction ix) {
                decompile(ix);
            } else {
                output.append("// ").append(o).append('\n');
            }
        }

        return output.toString();
    }

    private void extractJumpLabels() {
        for (Object object : content) {
            if (object instanceof InstructionExpression ix) {
                if (ix.getOpcode() == Opcode.JUMP) {
                    String target = ix.getArg(0).toMlog();
                    try {
                        Integer index = Integer.parseInt(target);
                        String label = labels.computeIfAbsent(index, ind -> createNewLabel());
                        labels.put(index, label);
                        ix.setArgument(0, new MlogVariable(label));
                    } catch (NumberFormatException ex) {
                        // Do nothing
                    }
                }
            }
        }
    }

    private void insertTextLabels() {
        // Insert labels at proper positions - in reverse order, so that the indexes aren't shifted by insertions
        labels.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .forEach(index -> content.add(index, labels.get(index)));
    }

    private void collapseExpressions() {
        final Set<Integer> toRemove = new TreeSet<>(Comparator.reverseOrder());
        final Set<LogicArgument> nonlinearVariables = findNonlinearVariables();
        final Map<String, Integer> definitions = new HashMap<>();

        for (int ixIndex = 0; ixIndex < content.size(); ixIndex++) {
            if (content.get(ixIndex) instanceof InstructionExpression ix) {
                final List<MlogVariable> variables = new ArrayList<>();
                ix.gatherInputVariables(variables);
                List<LogicArgument> results = ix.outputArgumentsStream().toList();
                for (MlogVariable variable : variables) {
                    if ((results.contains(variable) || !nonlinearVariables.contains(variable)) && definitions.containsKey(variable.toMlog())) {
                        Integer sourceIndex = definitions.get(variable.toMlog());
                        InstructionExpression definition = (InstructionExpression) content.get(sourceIndex);
                        if (definition != null && definition.getOpcode() == Opcode.OP) {
                            Operation operation = Operation.fromMlog(definition.getArg(0).toMlog());
                            OperationExpression expression = new OperationExpression(operation,
                                    new MlogVariable(definition.getArg(2).toMlog()),
                                    operation.getOperands() == 1 ? null : new MlogVariable(definition.getArg(3).toMlog())
                            );
                            ix.replaceVariable(variable, expression);
                            toRemove.add(sourceIndex);
                        }
                    }
                }
            }

            if (content.get(ixIndex) instanceof InstructionExpression ix && isLinear(ix)) {
                if (ix.getOpcode() == Opcode.OP) {
                    LogicArgument arg = ix.getArg(1);
                    definitions.put(arg.toMlog(), ixIndex);
                }
            } else {
                definitions.clear();
            }
        }

        for (int index : toRemove) {
            content.remove(index);
        }
    }

    private boolean isLinear(MlogInstruction ix) {
        return ix.getOpcode() != Opcode.JUMP
                && ix.outputArgumentsStream().map(LogicArgument::toMlog).noneMatch("@counter"::equals);
    }

    // Linear blocks of code are continuous regions of code not containing labels, jumps and set counter instructions.
    // A linear variable is a variable which isn't read without being previously set in the same linear block of code
    // where the read occurs. If there is a linear block of code where a variable is read without being set by some
    // preceding instruction, such a variable is nonlinear. Nonlinear variables cannot be collapsed into expressions.
    private Set<LogicArgument> findNonlinearVariables() {
        Set<LogicArgument> result = new HashSet<>();
        Set<LogicArgument> reads = new HashSet<>();

        for (int i = content.size() - 1; i >= 0; i--) {
            Object o = content.get(i);
            if (o instanceof MlogInstruction ix && isLinear(ix) ) {
                ix.outputArgumentsStream().forEach(reads::remove);
                ix.inputArgumentsStream().forEach(reads::add);
            } else {
                result.addAll(reads);
                reads.clear();
            }
        }

        return result;
    }

    private String createNewLabel() {
        String label;
        do {
            label = "label" + labelIndex++ + ":";
        } while (usedLabels.contains(label));
        usedLabels.add(label);
        return label;
    }

    private void decompile(MlogInstruction ix) {
        String decompiled = functionMapper.decompile(ix);
        if (decompiled == null) {
            switch (ix.getOpcode()) {
                case JUMP   -> processJump(ix);
                case OP     -> processOp(ix);
                case READ   -> cmdIndent(ix, 0, " = ", 1, "[", 2, "]");
                case SET    -> cmdIndent(ix, 0, " = ", 1);
                case WRITE  -> cmdIndent(ix, 1, "[", 2, "] = ", 0);
                default     -> error(ix);
            }
        } else {
            cmdIndent(decompiled);
        }
    }

    private void error(MlogInstruction ix) {
        output.append("// Cannot decompile: ").append(ix.toMlog()).append('\n');
    }

    private void cmd(MlogInstruction ix, Object... params) {
        for (Object obj : params) {
            if (obj instanceof Integer index) {
                if (index >= ix.getArgs().size()) {
                    output.append("<missing argument>");
                } else {
                    String id = ix.getArg(index).toMlog();
                    int endIndex = id.endsWith(":") ? id.length() - 1 : id.length();
                    output.append(id, 0, endIndex);
                }
            } else {
                output.append(obj);
            }
        }
        output.append(";\n");
    }

    private void cmdIndent(MlogInstruction ix, Object... params) {
        output.append("    ");
        cmd(ix, params);
    }

    private void cmdIndent(String command) {
        output.append("    ").append(command).append(";\n");
    }

    private void processOp(MlogInstruction ix) {
        Operation oper = Operation.fromMlog(ix.getArg(0).toMlog());
        if (oper != null) {
            if (oper.getOperands() == 2) {
                cmdIndent(ix, 1, " = ", 2, " ", oper.getMindcode(), " ", 3);
            } else {
                cmdIndent(ix, 1, " = ", oper.getMindcode(), 2);
            }
        } else {
            error(ix);
        }
    }

    private void processJump(MlogInstruction ix) {
        if ("always".equals(ix.getArg(1).toMlog())) {
            cmdIndent(ix, "goto ", 0);
            return;
        }

        Operation oper = Operation.fromMlog(ix.getArg(1).toMlog());
        if (oper == Operation.EQUAL || oper == Operation.NOT_EQUAL) {
            if ("false".equals(ix.getArg(2).toMlog())) {
                processJumpCondition(ix, 3, oper == Operation.EQUAL);
                return;
            } else if ("false".equals(ix.getArg(3).toMlog())) {
                processJumpCondition(ix, 2, oper == Operation.EQUAL);
                return;
            }
        }

        if (oper != null) {
            cmd(ix, "if ", 2, " ", oper.getMindcode(), " ", 3, " then goto ", 0);
        } else {
            error(ix);
        }
    }

    private void processJumpCondition(MlogInstruction ix, int expressionIndex, boolean invert) {
        if (invert) {
            cmd(ix, "if !", expressionIndex, " then goto ", 0);
        } else {
            cmd(ix, "if ", expressionIndex, " then goto ", 0);
        }
    }
}

