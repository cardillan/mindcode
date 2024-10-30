package info.teksol.mindcode.ast;

import info.teksol.mindcode.*;
import info.teksol.mindcode.compiler.MindcodeCompilerMessage;
import info.teksol.mindcode.grammar.MindcodeBaseVisitor;
import info.teksol.mindcode.grammar.MindcodeParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.intellij.lang.annotations.PrintFormat;

import java.util.*;
import java.util.function.Consumer;

// TODO This class throws errors. When refactoring, change it to producing CompilerMessages
public class AstNodeBuilder extends MindcodeBaseVisitor<AstNode> {
    public static final String AST_PREFIX = "__ast";
    private final Consumer<MindcodeMessage> messageConsumer;
    private final InputFile inputFile;
    private final Map<String, Integer> heapAllocations = new HashMap<>();
    private int temp;
    private HeapAllocation allocatedHeap;
    private StackAllocation allocatedStack;

    public AstNodeBuilder(InputFile inputFile, Consumer<MindcodeMessage> messageConsumer) {
        this.inputFile = inputFile;
        this.messageConsumer = messageConsumer;
    }

    public static Seq generate(InputFile inputFile, Consumer<MindcodeMessage> messageConsumer,
            MindcodeParser.ProgramContext program) {
        final AstNodeBuilder builder = new AstNodeBuilder(inputFile, messageConsumer);
        final AstNode node = builder.visit(program);
        return (Seq) node;
    }

    private void error(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.error(position, format, args));
    }

    private void warn(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.warn(position, format, args));
    }

    @Override
    public AstNode visit(ParseTree tree) {
        if (tree instanceof ParserRuleContext ctx && ctx.exception != null) {
            throw new ParserAbort();
        }
        return super.visit(tree);
    }

    private InputPosition pos(Token token) {
        return InputPosition.create(inputFile, token);
    }

    @Override
    public AstNode visitRelaxed_directive(MindcodeParser.Relaxed_directiveContext ctx) {
        warn(pos(ctx.getStart()), "The relaxed syntax is deprecated.");
        return new NoOp();
    }

    @Override
    public AstNode visitStrict_directive(MindcodeParser.Strict_directiveContext ctx) {
        return new NoOp();
    }

    @Override
    protected AstNode aggregateResult(AstNode aggregate, AstNode nextResult) {
        if (nextResult != null) return nextResult;
        return aggregate;
    }

    private void allocateHeap(MindcodeParser.Alloc_listContext ctx) {
        if (allocatedHeap != null) {
            error(pos(ctx.getStart()), "Multiple heap allocations.");
        }

        final String name = ctx.id().getText();
        final Range range;
        if (ctx.alloc_range() != null) {
            range = (Range) visit(ctx.alloc_range());
        } else {
            range = new ExclusiveRange(pos(ctx.getStart()),
                    new NumericLiteral(pos(ctx.getStart()), "0"), new NumericLiteral(pos(ctx.getStart()), "64"));
        }

        allocatedHeap = new HeapAllocation(pos(ctx.getStart()), name, range);
    }

    private void allocateStack(MindcodeParser.Alloc_listContext ctx) {
        if (allocatedStack != null) {
            error(pos(ctx.getStart()), "Multiple stack allocations.");
        }

        final String name = ctx.id().getText();
        if (ctx.alloc_range() != null) {
            allocatedStack = new StackAllocation(pos(ctx.getStart()), name, (Range) visit(ctx.alloc_range()));
        } else {
            allocatedStack = new StackAllocation(pos(ctx.getStart()), name);
        }
    }

    private List<AstNode> createListOfIterators(MindcodeParser.Iterator_listContext values) {
        return values.iterator().stream()
                .map(this::visit)
                .toList();
    }

    private List<AstNode> createListOfValues(MindcodeParser.Value_listContext values) {
        return values.expression().stream()
                .map(this::visit)
                .toList();
    }

    private List<AstNode> createListOfValues(MindcodeParser.When_value_listContext values) {
        final List<AstNode> result = new ArrayList<>();

        if (values != null) {
            final AstNode nodes;
            if (values.when_value_list() != null) {
                nodes = visit(values.when_value_list());
            } else {
                nodes = new NoOp();
            }

            gatherValues(new Seq(pos(values.getStart()), nodes, visit(values.when_expression())), result);
        }

        return result;
    }

    private void gatherAlternatives(AstNode alternative, List<CaseAlternative> accumulator) {
        if (alternative instanceof CaseAlternative caseAlternative) {
            accumulator.add(caseAlternative);
        } else if (alternative instanceof Seq seq) {
            gatherAlternatives(seq.getRest(), accumulator);
            gatherAlternatives(seq.getLast(), accumulator);
        }
    }

    private void gatherArgs(AstNode arg, List<AstNode> accumulator) {
        if (arg instanceof Seq seq) {
            gatherArgs(seq.getRest(), accumulator);
            gatherArgs(seq.getLast(), accumulator);
        } else if (arg instanceof NoOp) {
            // ignore
        } else {
            accumulator.add(arg);
        }
    }

    private void gatherValues(AstNode arg, List<AstNode> accumulator) {
        if (arg instanceof Seq seq) {
            gatherArgs(seq.getRest(), accumulator);
            gatherArgs(seq.getLast(), accumulator);
        } else if (arg instanceof NoOp) {
            // ignore
        } else {
            accumulator.add(arg);
        }
    }

    private String nextTemp() {
        return AST_PREFIX + temp++;
    }

    @Override
    public AstNode visitAllocation(MindcodeParser.AllocationContext ctx) {
        MindcodeParser.Alloc_listContext alloc_list = ctx.alloc().alloc_list();
        while (alloc_list != null) {
            switch (alloc_list.type.getText()) {
                case "heap"  -> allocateHeap(alloc_list);
                case "stack" -> allocateStack(alloc_list);
            }

            alloc_list = alloc_list.alloc_list();
        }

        return new Seq(pos(ctx.getStart()),
                Objects.requireNonNullElseGet(allocatedHeap, NoOp::new),
                Objects.requireNonNullElseGet(allocatedStack, NoOp::new)
        );
    }

    @Override
    public AstNode visitAlternative(MindcodeParser.AlternativeContext ctx) {
        return new CaseAlternative(pos(ctx.getStart()),
                createListOfValues(ctx.values),
                ctx.body == null ? new Seq(pos(ctx.getStart()), new NoOp()) : visit(ctx.body));
    }

    @Override
    public AstNode visitAlternative_list(MindcodeParser.Alternative_listContext ctx) {
        if (ctx.alternative_list() != null) {
            return new Seq(pos(ctx.getStart()), visit(ctx.alternative_list()), visit(ctx.alternative()));
        } else {
            return visit(ctx.alternative());
        }
    }

    @Override
    public FunctionArgument visitArg(MindcodeParser.ArgContext ctx) {
        if (ctx.argument != null) {
            return new FunctionArgument(pos(ctx.getStart()),
                    visit(ctx.argument),
                    ctx.modifier_in != null,
                    ctx.modifier_out != null);
        } else {
            return new FunctionArgument(pos(ctx.getStart()),
                    ctx.modifier_in != null,
                    ctx.modifier_out != null);
        }
    }

    @Override
    public AstNode visitBinop_and(MindcodeParser.Binop_andContext ctx) {
        return ctx.op.getText().equals("and")
                ? new BoolBinaryOp(pos(ctx.getStart()), visit(ctx.left), ctx.op.getText(), visit(ctx.right))
                : new BinaryOp(pos(ctx.getStart()), visit(ctx.left), ctx.op.getText(), visit(ctx.right));
    }

    @Override
    public AstNode visitBinop_bitwise_and(MindcodeParser.Binop_bitwise_andContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                visit(ctx.left),
                "&",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_bitwise_or(MindcodeParser.Binop_bitwise_orContext ctx) {
        return new BinaryOp(pos(ctx.getStart()), visit(ctx.left), ctx.op.getText(), visit(ctx.right));
    }

    @Override
    public AstNode visitBinop_equality_comparison(MindcodeParser.Binop_equality_comparisonContext ctx) {
        if (ctx.op.getText().equals("!==")) {
            return new BinaryOp(pos(ctx.getStart()),
                    new BinaryOp(pos(ctx.getStart()),
                            visit(ctx.left),
                            "===",
                            visit(ctx.right)
                    ),
                    "==",
                    new BooleanLiteral(pos(ctx.getStart()), false)
            );
        } else {
            return new BinaryOp(pos(ctx.getStart()),
                    visit(ctx.left),
                    ctx.op.getText(),
                    visit(ctx.right)
            );
        }
    }

    @Override
    public AstNode visitBinop_exp(MindcodeParser.Binop_expContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                visit(ctx.left),
                "**",
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_inequality_comparison(MindcodeParser.Binop_inequality_comparisonContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_mul_div_mod(MindcodeParser.Binop_mul_div_modContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_or(MindcodeParser.Binop_orContext ctx) {
        return ctx.op.getText().equals("or")
                ? new BoolBinaryOp(pos(ctx.getStart()), visit(ctx.left), ctx.op.getText(), visit(ctx.right))
                : new BinaryOp(pos(ctx.getStart()), visit(ctx.left), ctx.op.getText(), visit(ctx.right));
    }

    @Override
    public AstNode visitBinop_plus_minus(MindcodeParser.Binop_plus_minusContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                visit(ctx.left),
                ctx.op.getText(),
                visit(ctx.right)
        );
    }

    @Override
    public AstNode visitBinop_shift(MindcodeParser.Binop_shiftContext ctx) {
        return new BinaryOp(pos(ctx.getStart()), visit(ctx.left), ctx.op.getText(), visit(ctx.right));
    }

    @Override
    public AstNode visitBitwise_not_expr(MindcodeParser.Bitwise_not_exprContext ctx) {
        return new UnaryOp(pos(ctx.getStart()), "~", visit(ctx.expression()));
    }

    @Override
    public AstNode visitBreak_exp(MindcodeParser.Break_expContext ctx) {
        String label = ctx.break_st().label == null ? null : ctx.break_st().label.getText();
        return new BreakStatement(pos(ctx.getStart()), label);
    }

    @Override
    public AstNode visitCase_expression(MindcodeParser.Case_expressionContext ctx) {
        final String tmp = nextTemp();
        final AstNode elseBranch;
        if (ctx.case_expr().else_branch != null) {
            elseBranch = visit(ctx.case_expr().else_branch);
        } else {
            elseBranch = new NoOp();
        }

        final List<CaseAlternative> alternatives = new ArrayList<>();
        gatherAlternatives(visit(ctx.case_expr().alternative_list()), alternatives);

        return new Seq(pos(ctx.getStart()),
                new Assignment(pos(ctx.getStart()),
                        new VarRef(pos(ctx.getStart()), tmp),
                        visit(ctx.case_expr().cond)),
                new CaseExpression(pos(ctx.getStart()),
                        new VarRef(pos(ctx.getStart()), tmp),
                        alternatives,
                        elseBranch
                )
        );
    }

    @Override
    public AstNode visitCompound_assign(MindcodeParser.Compound_assignContext ctx) {
        final AstNode target = visit(ctx.target);
        return new Assignment(pos(ctx.getStart()),
                target,
                new BinaryOp(pos(ctx.getStart()),
                        target,
                        ctx.op.getText().replace("=", ""),
                        visit(ctx.value)
                )
        );
    }

    @Override
    public AstNode visitConst_decl(MindcodeParser.Const_declContext ctx) {
        final String name = ctx.name.getText();
        final AstNode value = visit(ctx.value);
        return new Constant(pos(ctx.getStart()), name, value);
    }

    @Override
    public AstNode visitContinue_exp(MindcodeParser.Continue_expContext ctx) {
        String label = ctx.continue_st().label == null ? null : ctx.continue_st().label.getText();
        return new ContinueStatement(pos(ctx.getStart()), label);
    }

    @Override
    public AstNode visitDo_while_expression(MindcodeParser.Do_while_expressionContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new DoWhileExpression(pos(ctx.getStart()), label, visit(ctx.loop_body()), visit(ctx.cond));
    }

    @Override
    public AstNode visitExclusive_range_exp(MindcodeParser.Exclusive_range_expContext ctx) {
        return new ExclusiveRange(pos(ctx.getStart()),visit(ctx.start), visit(ctx.end));
    }

    @Override
    public AstNode visitExpression_list(MindcodeParser.Expression_listContext ctx) {
        if (ctx.expression_list() != null) {
            final AstNode rest = visit(ctx.expression_list());
            final AstNode expr = visit(ctx.expression_or_rem_comment());
            return new Seq(pos(ctx.getStart()), rest, expr);
        } else if (ctx.expression_or_rem_comment() != null) {
            final AstNode expr = visit(ctx.expression_or_rem_comment());
            if (expr instanceof Seq) return expr;
            return new Seq(pos(ctx.getStart()), expr);
        } else {
            return new NoOp();
        }
    }

    @Override
    public AstNode visitFalse_bool_literal(MindcodeParser.False_bool_literalContext ctx) {
        return new BooleanLiteral(pos(ctx.getStart()), false);
    }

    @Override
    public AstNode visitFloat_t(MindcodeParser.Float_tContext ctx) {
        return new NumericLiteral(pos(ctx.getStart()), ctx.getText());
    }

    @Override
    public AstNode visitFor_each_1(MindcodeParser.For_each_1Context ctx) {
        warn(pos(ctx.values.getStart()), "Using parentheses around value list in list iteration loops is deprecated.");
        String label = ctx.label == null ? null : ctx.label.getText();
        return new ForEachExpression(pos(ctx.getStart()), label,
                createListOfIterators(ctx.iterators),
                createListOfValues(ctx.values),
                visit(ctx.loop_body()));
    }

    @Override
    public AstNode visitFor_each_2(MindcodeParser.For_each_2Context ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new ForEachExpression(pos(ctx.getStart()), label,
                createListOfIterators(ctx.iterators),
                createListOfValues(ctx.values),
                visit(ctx.loop_body()));
    }

    @Override
    public AstNode visitFuncall(MindcodeParser.FuncallContext ctx) {
        final List<FunctionArgument> arguments = ctx.params != null
                ? ctx.params.arg().stream().map(this::visitArg).toList()
                : List.of();

        if (ctx.END() != null) {
            // The end function is a bit special: because the keyword "end" is also used to
            // close blocks, the grammar needed to special-case the end function directly.
            return new FunctionCall(pos(ctx.getStart()), "end");
        } else if (ctx.obj != null) {
            final AstNode target;
            if (ctx.obj.unit_ref() != null) {
                target = visit(ctx.obj.unit_ref());
            } else if (ctx.obj.var_ref() != null) {
                target = visit(ctx.obj.var_ref());
            } else {
                throw new MindcodeInternalError("Failed to parse function call on a property access at " + ctx.getText());
            }

            return new Control(pos(ctx.getStart()), target, ctx.obj.prop.getText(), arguments);
        } else {
            final String name = ctx.name.getText();
            return new FunctionCall(pos(ctx.getStart()), name, arguments);
        }
    }

    @Override
    public AstNode visitFunction_declaration(MindcodeParser.Function_declarationContext ctx) {
        final String strInline = ctx.fundecl().inline == null ? null : ctx.fundecl().inline.getText();
        final String strType = ctx.fundecl().def.getText();
        final boolean inline = "inline".equals(strInline);
        final boolean noinline = "noinline".equals(strInline);
        final boolean procedure = "void".equals(strType);
        final List<FunctionParameter> parameters = ctx.fundecl().args != null
                ? ctx.fundecl().args.arg_decl().stream().map(this::visitArg_decl).toList()
                : List.of();


        int offset = inline ? 1 : 0;
        if (parameters.size() > offset) {
            parameters.subList(0, parameters.size() - offset).stream()
                    .filter(FunctionParameter::isVarArgs)
                    .forEach(p -> error(p.getInputPosition(),
                            "Only the last parameter of an inline function can be declared as vararg."));
        }

        return new FunctionDeclaration(pos(ctx.getStart()), inline, noinline, procedure,
                ctx.fundecl().name.getText(), parameters,
                visit(ctx.fundecl().body)
        );
    }

    @Override
    public FunctionParameter visitArg_decl(MindcodeParser.Arg_declContext ctx) {
        return new FunctionParameter(pos(ctx.getStart()),
                ctx.name.getText(),
                ctx.modifier_in != null,
                ctx.modifier_out != null,
                ctx.ellipsis != null);
    }

    @Override
    public AstNode visitGlobal_ref(MindcodeParser.Global_refContext ctx) {
        if (allocatedHeap == null) {
            error(pos(ctx.getStart()), "The heap must be allocated before using it.");
        }

        final String name = ctx.name.getText();
        final int location;
        if (heapAllocations.containsKey(name)) {
            location = heapAllocations.get(name);
        } else {
            location = heapAllocations.size();
            heapAllocations.put(name, location);
        }

        return new HeapAccess(pos(ctx.getStart()), allocatedHeap.getName(), location);
    }

    @Override
    public AstNode visitHeap_ref(MindcodeParser.Heap_refContext ctx) {
        return new HeapAccess(pos(ctx.getStart()),
                ctx.name.getText(),
                visit(ctx.address)
        );
    }

    @Override
    public AstNode visitIf_expression(MindcodeParser.If_expressionContext ctx) {
        final AstNode trailer;
        if (ctx.if_expr().if_trailer() != null) {
            trailer = visit(ctx.if_expr().if_trailer());
        } else {
            trailer = new NoOp();
        }

        return new IfExpression(pos(ctx.getStart()),
                visit(ctx.if_expr().cond),
                ctx.if_expr().true_branch == null ? new Seq(pos(ctx.getStart()), new NoOp()) : visit(ctx.if_expr().true_branch),
                trailer
        );
    }

    @Override
    public AstNode visitIf_trailer(MindcodeParser.If_trailerContext ctx) {
        if (ctx.ELSIF() != null) {
            final AstNode trailer;
            if (ctx.if_trailer() != null) {
                trailer = visit(ctx.if_trailer());
            } else {
                trailer = new NoOp();
            }

            return new IfExpression(pos(ctx.getStart()),
                    visit(ctx.cond),
                    ctx.true_branch == null ? new Seq(pos(ctx.getStart()), new NoOp()) : visit(ctx.true_branch),
                    trailer);
        } else if (ctx.ELSE() != null) {
            return ctx.false_branch == null ? new Seq(pos(ctx.getStart()), new NoOp()) : visit(ctx.false_branch);
        } else {
            throw new MindcodeInternalError("Unhandled if/elsif/else; neither ELSIF nor ELSE were true in " + ctx.getText());
        }
    }

    @Override
    public AstNode visitInclusive_range_exp(MindcodeParser.Inclusive_range_expContext ctx) {
        return new InclusiveRange(pos(ctx.getStart()),visit(ctx.start), visit(ctx.end));
    }

    @Override
    public AstNode visitIncr_list(MindcodeParser.Incr_listContext ctx) {
        if (ctx.incr_list() != null) {
            final AstNode down = visit(ctx.incr_list());
            return new Seq(pos(ctx.getStart()), down, visit(ctx.expression()));
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public AstNode visitIndirectpropaccess(MindcodeParser.IndirectpropaccessContext ctx) {
        return new PropertyAccess(pos(ctx.getStart()), visit(ctx.target), visit(ctx.expr));
    }

    @Override
    public AstNode visitInit_list(MindcodeParser.Init_listContext ctx) {
        if (ctx.init_list() != null) {
            final AstNode down = visit(ctx.init_list());
            return new Seq(pos(ctx.getStart()), down, visit(ctx.expression()));
        } else {
            return visit(ctx.expression());
        }
    }

    @Override
    public AstNode visitInt_t(MindcodeParser.Int_tContext ctx) {
        return new NumericLiteral(pos(ctx.getStart()), ctx.getText());
    }

    @Override
    public AstNode visitIterated_for(MindcodeParser.Iterated_forContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new WhileExpression(pos(ctx.getStart()),
                label,
                visit(ctx.init),
                visit(ctx.cond),
                visit(ctx.loop_body()),
                visit(ctx.increment)
        );
    }

    @Override
    public AstNode visitIterator(MindcodeParser.IteratorContext ctx) {
        // At this point, no other modifier is allowed by the syntax
        boolean outModifier = ctx.modifier != null;
        final VarRef varRef = (VarRef) visit(ctx.lvalue());
        return new Iterator(pos(ctx.getStart()), true, outModifier, varRef);
    }

    @Override
    public AstNode visitLiteral_minus(MindcodeParser.Literal_minusContext ctx) {
        return new NumericLiteral(pos(ctx.getStart()), ctx.getText());
    }

    @Override
    public AstNode visitLiteral_null(MindcodeParser.Literal_nullContext ctx) {
        return new NullLiteral(pos(ctx.getStart()));
    }

    @Override
    public AstNode visitLiteral_string(MindcodeParser.Literal_stringContext ctx) {
        final String str = ctx.getText();
        String value = str.substring(1, str.length() - 1);
        if (value.contains("\\\"")) {
            warn(pos(ctx.getStart()), "Usage of double quotes in string literals is deprecated.");
            value = value.replace("\\\"", "'");

        }
        return new StringLiteral(pos(ctx.getStart()), value.replaceAll("\\\\\"", "\""));
    }

    @Override
    public AstNode visitList_directive(MindcodeParser.List_directiveContext ctx) {
        if (ctx.directive_list() == null) {
            return new Directive(pos(ctx.getStart()),
                    new DirectiveText(pos(ctx.option), ctx.option.getText()),
                    List.of());
        } else {
            List<DirectiveText> values = ctx.values.id().stream()
                    .map(id -> new DirectiveText(pos(id.getStart()), id.getText()))
                    .toList();

            return new Directive(pos(ctx.getStart()),
                    new DirectiveText(pos(ctx.option), ctx.option.getText()),
                    values);
        }
    }

    @Override
    public AstNode visitLiteral_formattable(MindcodeParser.Literal_formattableContext ctx) {
        final String str = ctx.getText();
        return new FormattableLiteral(pos(ctx.getStart()), str.substring(2, str.length() - 1));
    }

    @Override
    public AstNode visitNot_expr(MindcodeParser.Not_exprContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                visit(ctx.expression()),
                "==",
                new BooleanLiteral(pos(ctx.getStart()), false));
    }

    @Override
    public AstNode visitNumeric_directive(MindcodeParser.Numeric_directiveContext ctx) {
        return new Directive(pos(ctx.getStart()),
                new DirectiveText(pos(ctx.option), ctx.option.getText()),
                new DirectiveText(pos(ctx.value), ctx.value.getText()));
    }

    @Override
    public AstNode visitParam_decl(MindcodeParser.Param_declContext ctx) {
        final String name = ctx.name.getText();
        final AstNode value = visit(ctx.value);
        return new ProgramParameter(pos(ctx.getStart()), name, value);
    }

    @Override
    public AstNode visitProgram(MindcodeParser.ProgramContext ctx) {
        final AstNode parent = super.visitProgram(ctx);
        if (parent == null) return new Seq(pos(ctx.getStart()), new NoOp());
        return parent;
    }

    @Override
    public AstNode visitPropaccess(MindcodeParser.PropaccessContext ctx) {
        final AstNode target;
        if (ctx.var_ref() != null) {
            target = visit(ctx.var_ref());
        } else if (ctx.unit_ref() != null) {
            target = visit(ctx.unit_ref());
        } else {
            throw new MindcodeInternalError("Expected var ref or unit ref in " + ctx.getText());
        }

        return new PropertyAccess(pos(ctx.getStart()), target, new Ref(pos(ctx.getStart()), ctx.prop.getText()));
    }

    @Override
    public AstNode visitRanged_for(MindcodeParser.Ranged_forContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        final AstNode var = visit(ctx.lvalue());
        final Range range = (Range) visit(ctx.range_expression());
        final AstNode body = visit(ctx.loop_body());
        return new RangedForExpression(pos(ctx.getStart()), label, var, range, body);
    }

    @Override
    public AstNode visitReturn_exp(MindcodeParser.Return_expContext ctx) {
        AstNode retval = ctx.return_st().retval == null ? new VoidLiteral(pos(ctx.getStart())) : visit(ctx.return_st().retval);
        return new ReturnStatement(pos(ctx.getStart()), retval);
    }

    @Override
    public AstNode visitSimple_assign(MindcodeParser.Simple_assignContext ctx) {
        final AstNode lvalue = visit(ctx.target);
        final AstNode value = visit(ctx.value);
        return new Assignment(pos(ctx.getStart()),lvalue, value);
    }

    @Override
    public AstNode visitString_directive(MindcodeParser.String_directiveContext ctx) {
        return new Directive(pos(ctx.getStart()),
                new DirectiveText(pos(ctx.option), ctx.option.getText()),
                new DirectiveText(pos(ctx.value), ctx.value.getText()));
    }

    @Override
    public AstNode visitTernary_op(MindcodeParser.Ternary_opContext ctx) {
        return new IfExpression(pos(ctx.getStart()),
                visit(ctx.cond),
                visit(ctx.true_branch),
                visit(ctx.false_branch)
        );
    }

    @Override
    public AstNode visitTrue_bool_literal(MindcodeParser.True_bool_literalContext ctx) {
        return new BooleanLiteral(pos(ctx.getStart()), true);
    }

    @Override
    public AstNode visitUnary_minus(MindcodeParser.Unary_minusContext ctx) {
        return new BinaryOp(pos(ctx.getStart()),
                new NumericLiteral(pos(ctx.getStart()), "-1"),
                "*",
                visit(ctx.expression()));
    }

    @Override
    public AstNode visitUnit_ref(MindcodeParser.Unit_refContext ctx) {
        return new Ref(pos(ctx.getStart()), ctx.ref().getText());
    }

    @Override
    public AstNode visitVar_ref(MindcodeParser.Var_refContext ctx) {
        return new VarRef(pos(ctx.getStart()),  ctx.getText());
    }

    @Override
    public AstNode visitWhen_value_list(MindcodeParser.When_value_listContext ctx) {
        if (ctx.when_value_list()!= null) {
            return new Seq(pos(ctx.getStart()), visit(ctx.when_value_list()), visit(ctx.when_expression()));
        } else {
            final AstNode last = visit(ctx.when_expression());
            return new Seq(pos(ctx.getStart()), last);
        }
    }

    @Override
    public AstNode visitWhile_expression(MindcodeParser.While_expressionContext ctx) {
        String label = ctx.label == null ? null : ctx.label.getText();
        return new WhileExpression(pos(ctx.getStart()),label, new NoOp(),
                visit(ctx.cond), visit(ctx.loop_body()), new NoOp());
    }


    @Override
    public AstNode visitRem_comment(MindcodeParser.Rem_commentContext ctx) {
        String str = ctx.getText().substring(3).strip();
        StringLiteral text = new StringLiteral(pos(ctx.getStart()), str);
        FunctionArgument argument = new FunctionArgument(pos(ctx.getStart()), text, false, false);
        return new FunctionCall(pos(ctx.getStart()), "remark", argument);
    }
}
