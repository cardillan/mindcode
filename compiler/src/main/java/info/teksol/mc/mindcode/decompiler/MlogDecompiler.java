package info.teksol.mc.mindcode.decompiler;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.functions.BaseFunctionMapper;
import info.teksol.mc.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class MlogDecompiler {
    private final FunctionMapper functionMapper;
    private final Map<Integer, String> labels;
    private final List<Object> content;
    private final boolean preamble;

    private final Set<String> usedLabels;
    private int labelIndex;

    private final StringBuilder output = new StringBuilder();

    private static final AstContext STATIC_AST_CONTEXT = AstContext.createStaticRootNode();

    public static String decompile(String mlog) {
        return decompile(mlog, true);
    }

    public static String decompile(String mlog, boolean preamble) {
        InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(
                ProcessorVersion.MAX, ProcessorEdition.W);
        MlogParser mlogParser = new MlogParser(processor, mlog);
        ParsedMlog parsedMlog = mlogParser.parse();
        return new MlogDecompiler(processor, parsedMlog, preamble).decompile();
    }

    public MlogDecompiler(InstructionProcessor instructionProcessor, ParsedMlog input, boolean preamble) {
        this.preamble = preamble;
        functionMapper = new BaseFunctionMapper(instructionProcessor.messageConsumer(), instructionProcessor, null);
        labels = input.labels();
        content = input.content();
        usedLabels = new HashSet<>(labels.size());
    }

    public String decompile() {
        if (!content.isEmpty()) {
            extractJumpLabels();
            insertTextLabels();
            collapseExpressions();

            if (preamble) {
                output.append("""
                        // This is an mlog code partially decompiled into Mindcode.
                        //
                        // NOTE: This code cannot be directly compiled by Mindcode as is!
                        // All the labels, if and goto statements need to be manually revised
                        // and rewritten into loops, conditional statements and functions.
                        
                        // The following directive instructs Mindcode to process code specific
                        // to World processor in the Mindustry Logic version 8. It is not necessary
                        // if you haven't decompiled World processor specific instructions.
                        #set target = 8W;

                        """);
            }

            for (Object o : content) {
                if (o instanceof MlogInstruction ix) {
                    decompile(ix);
                } else {
                    output.append("// ").append(o).append('\n');
                }
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
                        int index = Integer.parseInt(target);
                        if (index >= 0 && index < content.size()) {
                            String label = labels.computeIfAbsent(index, ind -> createNewLabel());
                            labels.put(index, label);
                            ix.setArgument(0, new MlogVariable(label));
                        }
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
                // Needs more checks
                // Self-actualizing assignments must not be collapsed over instructions reading the value.
                //List<LogicArgument> results = ix.outputArgumentsStream().toList();
                for (MlogVariable variable : variables) {
                    if ((/*results.contains(variable) ||*/ !nonlinearVariables.contains(variable)) && definitions.containsKey(variable.toMlog())) {
                        Integer sourceIndex = definitions.get(variable.toMlog());
                        InstructionExpression definition = (InstructionExpression) content.get(sourceIndex);
                        if (definition.getOpcode() == Opcode.OP) {
                            Operation operation = Operation.fromMlog(definition.getArg(0).toMlog());
                            if (operation != null) {
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
        if ((ix.getOpcode() == Opcode.READ || ix.getOpcode() == Opcode.WRITE) && !ix.getArg(2).toMlog().startsWith("\"")) {
            switch (ix.getOpcode()) {
                case READ -> cmdIndent(ix, 0, " = ", 1, "[", 2, "]");
                case WRITE -> cmdIndent(ix, 1, "[", 2, "] = ", 0);
            }
            return;
        }

        String decompiled = functionMapper.decompile(ix);
        if (decompiled == null) {
            switch (ix.getOpcode()) {
                case JUMP   -> processJump(ix);
                case OP     -> processOp(ix);
                case SET    -> cmdIndent(ix, 0, " = ", 1);
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
        Operation operation = Operation.fromMlog(ix.getArg(0).toMlog());
        if (operation != null) {
            if (operation.getOperands() == 2) {
                cmdIndent(ix, 1, " = ", 2, " ", operation.getMindcode(), " ", 3);
            } else {
                cmdIndent(ix, 1, " = ", operation.getMindcode(), 2);
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

        Operation operation = Operation.fromMlog(ix.getArg(1).toMlog());
        if (operation == Operation.EQUAL || operation == Operation.NOT_EQUAL) {
            if ("false".equals(ix.getArg(2).toMlog())) {
                processJumpCondition(ix, 3, operation == Operation.EQUAL);
                return;
            } else if ("false".equals(ix.getArg(3).toMlog())) {
                processJumpCondition(ix, 2, operation == Operation.EQUAL);
                return;
            }
        }

        if (operation != null) {
            cmd(ix, "if ", 2, " ", operation.getMindcode(), " ", 3, " then goto ", 0);
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

