package info.teksol.mindcode.mindustry;

import java.util.*;
import java.util.stream.Collectors;

public class LogicInstructionPeepholeOptimizer {
    private static final Set<String> COMPARISON_OPERATORS = Set.of(
            "equal",
            "notEqual",
            "lessThan",
            "lessThanEq",
            "greaterThan",
            "greaterThanEq",
            "strictEqual"
    );

    private static final Map<String, String> inverses = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan",
            "strictEqual", "notEqual"
    );

    private static final Set<String> ARITHMETIC_OPS = Set.of("add", "sub", "mul", "div", "exp", "lessThan", "lessThanEq", "greaterThan", "greaterThanEq", "equal", "strictEqual", "notEqual");

    public static List<LogicInstruction> optimize(List<LogicInstruction> program) {
        final ArrayList<LogicInstruction> optimized = new ArrayList<>(program);

        collapseAdjacentSets(optimized);

        return optimized;
    }

    private static void collapseAdjacentSets(List<LogicInstruction> program) {
        final Map<String, String> replacements = new HashMap<>();

        int i = 0;
        while (i < program.size() - 1) {
            final LogicInstruction here = program.get(i);
            final LogicInstruction next = program.get(i + 1);

            if (isSet(here) && isWrite(next) && here.getArgs().get(0).equals(next.getArgs().get(0))) {
                replacements.put(next.getArgs().get(0), here.getArgs().get(1));
                program.remove(i);
                program.set(
                        i,
                        new LogicInstruction(
                                "write",
                                here.getArgs().get(1),
                                next.getArgs().get(1),
                                next.getArgs().get(2)
                        )
                );

                // Re-examine the previous instruction, in case we can now optimize it
                if (i > 0) i--;
                continue;
            }

            if (isSet(here) && isPrint(next) && here.getArgs().get(0).equals(next.getArgs().get(0))) {
                replacements.put(next.getArgs().get(0), here.getArgs().get(1));
                program.remove(i);
                program.set(
                        i,
                        new LogicInstruction(
                                "print",
                                here.getArgs().get(1)
                        )
                );

                // Re-examine the previous instruction, in case we can now optimize it
                if (i > 0) i--;
                continue;
            }

            if (isOp(here) && isComparison(here) && isJump(next) && next.getArgs().get(1).equals("notEqual")) {
                if (here.getArgs().size() >= 2 && next.getArgs().size() >= 3 && here.getArgs().get(1).equals(next.getArgs().get(2))) {
                    if (!inverses.containsKey(here.getArgs().get(0))) {
                        throw new IllegalArgumentException("Unknown operation passed-in; can't find the inverse of [" + here.getArgs().get(0) + "]");
                    }

                    program.remove(i);
                    program.set(
                            i,
                            new LogicInstruction(
                                    "jump",
                                    next.getArgs().get(0),
                                    inverses.get(here.getArgs().get(0)),
                                    here.getArgs().get(2),
                                    here.getArgs().get(3)
                            )
                    );

                    // Re-examine the previous instruction, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            if (isOp(here) && isSet(next) && ARITHMETIC_OPS.contains(here.getArgs().get(0))) {
                if (here.getArgs().get(1).equals(next.getArgs().get(1))) {
                    replacements.put(next.getArgs().get(1), next.getArgs().get(0));
                    program.remove(i);
                    program.set(
                            i,
                            new LogicInstruction(
                                    "op",
                                    here.getArgs().get(0),
                                    next.getArgs().get(0),
                                    here.getArgs().get(2),
                                    here.getArgs().get(3)
                            )
                    );

                    // Re-examine the previous instruction, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            if (isSet(here) && isOp(next) && ARITHMETIC_OPS.contains(next.getArgs().get(0))) {
                if (here.getArgs().get(0).equals(next.getArgs().get(3))) {
                    replacements.put(next.getArgs().get(3), here.getArgs().get(1));
                    program.remove(i);
                    program.set(
                            i,
                            new LogicInstruction(
                                    "op",
                                    List.of(
                                            next.getArgs().get(0),
                                            next.getArgs().get(1),
                                            next.getArgs().get(2),
                                            here.getArgs().get(1)
                                    )
                            )
                    );

                    // Re-examine the previous instruction, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }

                if (next.getArgs().size() >= 2 && here.getArgs().get(0).equals(next.getArgs().get(2))) {
                    replacements.put(next.getArgs().get(2), here.getArgs().get(1));
                    program.remove(i);
                    program.set(
                            i,
                            new LogicInstruction(
                                    "op",
                                    List.of(
                                            next.getArgs().get(0),
                                            next.getArgs().get(1),
                                            here.getArgs().get(1),
                                            next.getArgs().get(3)
                                    )
                            )
                    );

                    // Re-examine the previous instruction, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            if (isSet(here) && isSet(next)) {
                if (next.getArgs().size() >= 2 && here.getArgs().get(0).equals(next.getArgs().get(1))) {
                    replacements.put(next.getArgs().get(1), here.getArgs().get(1));
                    program.remove(i);
                    program.set(
                            i,
                            new LogicInstruction(
                                    "set",
                                    List.of(
                                            next.getArgs().get(0),
                                            here.getArgs().get(1)
                                    )
                            )
                    );

                    // Re-examine the previous instruction, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            i++;
        }

        applyReplacements(program, replacements);
    }

    private static void applyReplacements(List<LogicInstruction> program, Map<String, String> replacements) {
        final ListIterator<LogicInstruction> iterator = program.listIterator();
        while (iterator.hasNext()) {
            final LogicInstruction instruction = iterator.next();
            final List<String> newArgs = instruction.getArgs().stream().map((arg) -> replacements.getOrDefault(arg, arg)).collect(Collectors.toList());
            if (!newArgs.equals(instruction.getArgs())) {
                iterator.set(new LogicInstruction(instruction.getOpcode(), newArgs));
            }
        }
    }

    private static boolean isComparison(LogicInstruction instruction) {
        if (!instruction.getOpcode().equals("op")) {
            throw new IllegalArgumentException("Only op instructions can be comparisons");
        }

        return COMPARISON_OPERATORS.contains(instruction.getArgs().get(0));
    }

    private static boolean isWrite(LogicInstruction instruction) {
        return instruction.getOpcode().equals("write");
    }

    private static boolean isPrint(LogicInstruction instruction) {
        return instruction.getOpcode().equals("print");
    }

    private static boolean isJump(LogicInstruction instruction) {
        return instruction.getOpcode().equals("jump");
    }

    private static boolean isSet(LogicInstruction instruction) {
        return instruction.getOpcode().equals("set");
    }

    private static boolean isOp(LogicInstruction instruction) {
        return instruction.getOpcode().equals("op");
    }
}
