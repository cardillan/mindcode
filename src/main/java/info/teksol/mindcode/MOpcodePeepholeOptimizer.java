package info.teksol.mindcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MOpcodePeepholeOptimizer {
    private static final Set<String> COMPARISON_OPERATORS = Set.of(
            "equal",
            "notEqual",
            "lessThan",
            "lessThanEq",
            "greaterThan",
            "greaterThanEq",
            "strictEqual"
    );

    public static List<MOpcode> optimize(List<MOpcode> program) {
        final ArrayList<MOpcode> optimized = new ArrayList<>(program);

        collapseAdjacentSets(optimized);

        return optimized;
    }

    private static void collapseAdjacentSets(List<MOpcode> program) {
        int i = 0;
        while (i < program.size() - 1) {
            final MOpcode here = program.get(i);
            final MOpcode next = program.get(i + 1);

            if (isSet(here) && isWrite(next) && here.getArgs().get(0).equals(next.getArgs().get(0))) {
                program.remove(i);
                program.set(
                        i,
                        new MOpcode(
                                "write",
                                here.getArgs().get(1),
                                next.getArgs().get(1),
                                next.getArgs().get(2)
                        )
                );

                // Re-examine the previous node, in case we can now optimize it
                if (i > 0) i--;
                continue;
            }

            if (isSet(here) && isPrint(next) && here.getArgs().get(0).equals(next.getArgs().get(0))) {
                program.remove(i);
                program.set(
                        i,
                        new MOpcode(
                                "print",
                                here.getArgs().get(1)
                        )
                );

                // Re-examine the previous node, in case we can now optimize it
                if (i > 0) i--;
                continue;
            }

            if (isOp(here) && isComparison(here) && isJump(next) && next.getArgs().get(1).equals("notEqual")) {
                if (here.getArgs().get(1).equals(next.getArgs().get(2))) {
                    program.remove(i);
                    program.set(
                            i,
                            new MOpcode(
                                    "jump",
                                    next.getArgs().get(0),
                                    invertCondition(here.getArgs().get(0)),
                                    here.getArgs().get(2),
                                    here.getArgs().get(3)
                            )
                    );

                    // Re-examine the previous node, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            if (isOp(here) && isSet(next)) {
                if (here.getArgs().get(1).equals(next.getArgs().get(1))) {
                    program.remove(i);
                    program.set(
                            i,
                            new MOpcode(
                                    "op",
                                    here.getArgs().get(0),
                                    next.getArgs().get(0),
                                    here.getArgs().get(2),
                                    here.getArgs().get(3)
                            )
                    );

                    // Re-examine the previous node, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            if (isSet(here) && isOp(next)) {
                if (here.getArgs().get(0).equals(next.getArgs().get(3))) {
                    program.remove(i);
                    program.set(
                            i,
                            new MOpcode(
                                    "op",
                                    List.of(
                                            next.getArgs().get(0),
                                            next.getArgs().get(1),
                                            next.getArgs().get(2),
                                            here.getArgs().get(1)
                                    )
                            )
                    );

                    // Re-examine the previous node, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }

                if (here.getArgs().get(0).equals(next.getArgs().get(2))) {
                    program.remove(i);
                    program.set(
                            i,
                            new MOpcode(
                                    "op",
                                    List.of(
                                            next.getArgs().get(0),
                                            next.getArgs().get(1),
                                            here.getArgs().get(1),
                                            next.getArgs().get(3)
                                    )
                            )
                    );

                    // Re-examine the previous node, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            if (isSet(here) && isSet(next)) {
                if (here.getArgs().get(0).equals(next.getArgs().get(1))) {
                    program.remove(i);
                    program.set(
                            i,
                            new MOpcode(
                                    "set",
                                    List.of(
                                            next.getArgs().get(0),
                                            here.getArgs().get(1)
                                    )
                            )
                    );

                    // Re-examine the previous node, in case we can now optimize it
                    if (i > 0) i--;
                    continue;
                }
            }

            i++;
        }
    }

    private static String invertCondition(String cond) {
        switch (cond) {
            case "lessThan":
                return "greaterThanEq";

            default:
                throw new IllegalArgumentException("Don't know how to optimize [" + cond + "] into its reverse");
        }
    }

    private static boolean isComparison(MOpcode node) {
        if (!node.getOpcode().equals("op")) {
            throw new IllegalArgumentException("Only op opcodes can be comparisons");
        }

        return COMPARISON_OPERATORS.contains(node.getArgs().get(0));
    }

    private static boolean isWrite(MOpcode node) {
        return node.getOpcode().equals("write");
    }

    private static boolean isPrint(MOpcode node) {
        return node.getOpcode().equals("print");
    }

    private static boolean isJump(MOpcode node) {
        return node.getOpcode().equals("jump");
    }

    private static boolean isSet(MOpcode node) {
        return node.getOpcode().equals("set");
    }

    private static boolean isOp(MOpcode node) {
        return node.getOpcode().equals("op");
    }
}
