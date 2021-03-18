// Generated from /Users/francois/Projects/mindcode/src/main/java/info/teksol/mindcode/grammar/Mindcode.g4 by ANTLR 4.9.1
package info.teksol.mindcode.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MindcodeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ALLOCATE=1, BREAK=2, CASE=3, CONTINUE=4, DEF=5, ELSE=6, END=7, FALSE=8, 
		FOR=9, HEAP=10, IF=11, IN=12, NULL=13, STACK=14, TRUE=15, WHEN=16, WHILE=17, 
		ASSIGN=18, AT=19, COLON=20, COMMA=21, DIV=22, DOLLAR=23, DOT=24, EXP=25, 
		MINUS=26, MOD=27, MUL=28, NOT=29, PLUS=30, QUESTION_MARK=31, SEMICOLON=32, 
		DIV_ASSIGN=33, EXP_ASSIGN=34, MINUS_ASSIGN=35, MUL_ASSIGN=36, PLUS_ASSIGN=37, 
		LESS_THAN=38, LESS_THAN_EQUAL=39, NOT_EQUAL=40, EQUAL=41, STRICT_EQUAL=42, 
		GREATER_THAN_EQUAL=43, GREATER_THAN=44, AND=45, OR=46, LEFT_SBRACKET=47, 
		RIGHT_SBRACKET=48, LEFT_RBRACKET=49, RIGHT_RBRACKET=50, LEFT_CBRACKET=51, 
		RIGHT_CBRACKET=52, LITERAL=53, FLOAT=54, INT=55, ID=56, SL_COMMENT=57, 
		WS=58;
	public static final int
		RULE_program = 0, RULE_expression_list = 1, RULE_expression = 2, RULE_propaccess = 3, 
		RULE_numeric_t = 4, RULE_alloc = 5, RULE_alloc_list = 6, RULE_alloc_range = 7, 
		RULE_fundecl = 8, RULE_arg_decl_list = 9, RULE_while_expression = 10, 
		RULE_for_expression = 11, RULE_loop_body = 12, RULE_continue_st = 13, 
		RULE_break_st = 14, RULE_range = 15, RULE_init_list = 16, RULE_incr_list = 17, 
		RULE_funcall = 18, RULE_arg_list = 19, RULE_arg = 20, RULE_if_expr = 21, 
		RULE_if_trailer = 22, RULE_case_expr = 23, RULE_alternative_list = 24, 
		RULE_alternative = 25, RULE_assign = 26, RULE_lvalue = 27, RULE_heap_ref = 28, 
		RULE_global_ref = 29, RULE_unit_ref = 30, RULE_var_ref = 31, RULE_ref = 32, 
		RULE_int_t = 33, RULE_float_t = 34, RULE_literal_t = 35, RULE_null_t = 36, 
		RULE_bool_t = 37, RULE_true_t = 38, RULE_false_t = 39, RULE_id = 40, RULE_a_comment = 41;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expression_list", "expression", "propaccess", "numeric_t", 
			"alloc", "alloc_list", "alloc_range", "fundecl", "arg_decl_list", "while_expression", 
			"for_expression", "loop_body", "continue_st", "break_st", "range", "init_list", 
			"incr_list", "funcall", "arg_list", "arg", "if_expr", "if_trailer", "case_expr", 
			"alternative_list", "alternative", "assign", "lvalue", "heap_ref", "global_ref", 
			"unit_ref", "var_ref", "ref", "int_t", "float_t", "literal_t", "null_t", 
			"bool_t", "true_t", "false_t", "id", "a_comment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'", 
			"'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", "'stack'", 
			"'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", "'/'", "'$'", 
			"'.'", null, "'-'", "'%'", "'*'", null, "'+'", "'?'", "';'", "'/='", 
			"'^='", "'-='", "'*='", "'+='", "'<'", "'<='", "'!='", "'=='", "'==='", 
			"'>='", "'>'", null, null, "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "END", 
			"FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "DOLLAR", "DOT", "EXP", 
			"MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", 
			"EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "LITERAL", "FLOAT", 
			"INT", "ID", "SL_COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Mindcode.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MindcodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public TerminalNode EOF() { return getToken(MindcodeParser.EOF, 0); }
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			setState(88);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALLOCATE:
			case CASE:
			case DEF:
			case FALSE:
			case FOR:
			case IF:
			case NULL:
			case TRUE:
			case WHILE:
			case AT:
			case DOLLAR:
			case MINUS:
			case NOT:
			case LEFT_RBRACKET:
			case LITERAL:
			case FLOAT:
			case INT:
			case ID:
			case SL_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(84);
				expression_list(0);
				setState(85);
				match(EOF);
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 2);
				{
				setState(87);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Expression_listContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public Expression_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExpression_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExpression_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExpression_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expression_listContext expression_list() throws RecognitionException {
		return expression_list(0);
	}

	private Expression_listContext expression_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Expression_listContext _localctx = new Expression_listContext(_ctx, _parentState);
		Expression_listContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expression_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(91);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(97);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Expression_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expression_list);
					setState(93);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(94);
					expression(0);
					}
					} 
				}
				setState(99);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Binop_equality_comparisonContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode NOT_EQUAL() { return getToken(MindcodeParser.NOT_EQUAL, 0); }
		public TerminalNode EQUAL() { return getToken(MindcodeParser.EQUAL, 0); }
		public TerminalNode STRICT_EQUAL() { return getToken(MindcodeParser.STRICT_EQUAL, 0); }
		public Binop_equality_comparisonContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_equality_comparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_equality_comparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_equality_comparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Function_callContext extends ExpressionContext {
		public FuncallContext funcall() {
			return getRuleContext(FuncallContext.class,0);
		}
		public Function_callContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFunction_call(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFunction_call(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFunction_call(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class While_loopContext extends ExpressionContext {
		public While_expressionContext while_expression() {
			return getRuleContext(While_expressionContext.class,0);
		}
		public While_loopContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterWhile_loop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitWhile_loop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitWhile_loop(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Literal_boolContext extends ExpressionContext {
		public Bool_tContext bool_t() {
			return getRuleContext(Bool_tContext.class,0);
		}
		public Literal_boolContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_bool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_bool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_bool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Unary_minusContext extends ExpressionContext {
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public Numeric_tContext numeric_t() {
			return getRuleContext(Numeric_tContext.class,0);
		}
		public Unary_minusContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterUnary_minus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitUnary_minus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitUnary_minus(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Property_accessContext extends ExpressionContext {
		public PropaccessContext propaccess() {
			return getRuleContext(PropaccessContext.class,0);
		}
		public Property_accessContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterProperty_access(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitProperty_access(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitProperty_access(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Literal_stringContext extends ExpressionContext {
		public Literal_tContext literal_t() {
			return getRuleContext(Literal_tContext.class,0);
		}
		public Literal_stringContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_string(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_string(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_string(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Not_exprContext extends ExpressionContext {
		public TerminalNode NOT() { return getToken(MindcodeParser.NOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Not_exprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterNot_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitNot_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitNot_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Literal_nullContext extends ExpressionContext {
		public Null_tContext null_t() {
			return getRuleContext(Null_tContext.class,0);
		}
		public Literal_nullContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_null(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_null(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_null(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class For_loopContext extends ExpressionContext {
		public For_expressionContext for_expression() {
			return getRuleContext(For_expressionContext.class,0);
		}
		public For_loopContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFor_loop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFor_loop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFor_loop(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueContext extends ExpressionContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ValueContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Function_declarationContext extends ExpressionContext {
		public FundeclContext fundecl() {
			return getRuleContext(FundeclContext.class,0);
		}
		public Function_declarationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFunction_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFunction_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFunction_declaration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AllocationContext extends ExpressionContext {
		public AllocContext alloc() {
			return getRuleContext(AllocContext.class,0);
		}
		public AllocationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAllocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAllocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAllocation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class If_expressionContext extends ExpressionContext {
		public If_exprContext if_expr() {
			return getRuleContext(If_exprContext.class,0);
		}
		public If_expressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIf_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIf_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIf_expression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignmentContext extends ExpressionContext {
		public AssignContext assign() {
			return getRuleContext(AssignContext.class,0);
		}
		public AssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Literal_numericContext extends ExpressionContext {
		public Numeric_tContext numeric_t() {
			return getRuleContext(Numeric_tContext.class,0);
		}
		public Literal_numericContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_numeric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_numeric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_numeric(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Case_expressionContext extends ExpressionContext {
		public Case_exprContext case_expr() {
			return getRuleContext(Case_exprContext.class,0);
		}
		public Case_expressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCase_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCase_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCase_expression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_plus_minusContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(MindcodeParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public Binop_plus_minusContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_plus_minus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_plus_minus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_plus_minus(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_mul_div_modContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode MUL() { return getToken(MindcodeParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(MindcodeParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(MindcodeParser.MOD, 0); }
		public Binop_mul_div_modContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_mul_div_mod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_mul_div_mod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_mul_div_mod(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_inequality_comparisonContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LESS_THAN() { return getToken(MindcodeParser.LESS_THAN, 0); }
		public TerminalNode LESS_THAN_EQUAL() { return getToken(MindcodeParser.LESS_THAN_EQUAL, 0); }
		public TerminalNode GREATER_THAN_EQUAL() { return getToken(MindcodeParser.GREATER_THAN_EQUAL, 0); }
		public TerminalNode GREATER_THAN() { return getToken(MindcodeParser.GREATER_THAN, 0); }
		public Binop_inequality_comparisonContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_inequality_comparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_inequality_comparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_inequality_comparison(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Parenthesized_expressionContext extends ExpressionContext {
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public Parenthesized_expressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterParenthesized_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitParenthesized_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitParenthesized_expression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CommentContext extends ExpressionContext {
		public A_commentContext a_comment() {
			return getRuleContext(A_commentContext.class,0);
		}
		public CommentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ternary_opContext extends ExpressionContext {
		public ExpressionContext cond;
		public ExpressionContext true_branch;
		public ExpressionContext false_branch;
		public TerminalNode QUESTION_MARK() { return getToken(MindcodeParser.QUESTION_MARK, 0); }
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Ternary_opContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterTernary_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitTernary_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitTernary_op(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_expContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode EXP() { return getToken(MindcodeParser.EXP, 0); }
		public Binop_expContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_exp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_andContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public TerminalNode AND() { return getToken(MindcodeParser.AND, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Binop_andContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_and(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_and(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_and(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_orContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public TerminalNode OR() { return getToken(MindcodeParser.OR, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Binop_orContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_or(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_or(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_or(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				_localctx = new CommentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(101);
				a_comment();
				}
				break;
			case 2:
				{
				_localctx = new Property_accessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102);
				propaccess();
				}
				break;
			case 3:
				{
				_localctx = new Case_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(103);
				case_expr();
				}
				break;
			case 4:
				{
				_localctx = new If_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(104);
				if_expr();
				}
				break;
			case 5:
				{
				_localctx = new Function_callContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(105);
				funcall();
				}
				break;
			case 6:
				{
				_localctx = new Function_declarationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(106);
				fundecl();
				}
				break;
			case 7:
				{
				_localctx = new AllocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107);
				alloc();
				}
				break;
			case 8:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				assign();
				}
				break;
			case 9:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(109);
				lvalue();
				}
				break;
			case 10:
				{
				_localctx = new While_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(110);
				while_expression();
				}
				break;
			case 11:
				{
				_localctx = new For_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(111);
				for_expression();
				}
				break;
			case 12:
				{
				_localctx = new Not_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(112);
				match(NOT);
				setState(113);
				expression(13);
				}
				break;
			case 13:
				{
				_localctx = new Literal_stringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(114);
				literal_t();
				}
				break;
			case 14:
				{
				_localctx = new Literal_numericContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(115);
				numeric_t();
				}
				break;
			case 15:
				{
				_localctx = new Literal_boolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116);
				bool_t();
				}
				break;
			case 16:
				{
				_localctx = new Unary_minusContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(117);
				match(MINUS);
				setState(118);
				numeric_t();
				}
				break;
			case 17:
				{
				_localctx = new Literal_nullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(119);
				null_t();
				}
				break;
			case 18:
				{
				_localctx = new Parenthesized_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(120);
				match(LEFT_RBRACKET);
				setState(121);
				expression(0);
				setState(122);
				match(RIGHT_RBRACKET);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(155);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(153);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new Ternary_opContext(new ExpressionContext(_parentctx, _parentState));
						((Ternary_opContext)_localctx).cond = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(126);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(127);
						match(QUESTION_MARK);
						setState(128);
						((Ternary_opContext)_localctx).true_branch = expression(0);
						setState(129);
						match(COLON);
						setState(130);
						((Ternary_opContext)_localctx).false_branch = expression(25);
						}
						break;
					case 2:
						{
						_localctx = new Binop_expContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_expContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(132);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(133);
						((Binop_expContext)_localctx).op = match(EXP);
						setState(134);
						((Binop_expContext)_localctx).right = expression(15);
						}
						break;
					case 3:
						{
						_localctx = new Binop_mul_div_modContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_mul_div_modContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(135);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(136);
						((Binop_mul_div_modContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DIV) | (1L << MOD) | (1L << MUL))) != 0)) ) {
							((Binop_mul_div_modContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(137);
						((Binop_mul_div_modContext)_localctx).right = expression(13);
						}
						break;
					case 4:
						{
						_localctx = new Binop_plus_minusContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_plus_minusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(138);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(139);
						((Binop_plus_minusContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MINUS || _la==PLUS) ) {
							((Binop_plus_minusContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(140);
						((Binop_plus_minusContext)_localctx).right = expression(12);
						}
						break;
					case 5:
						{
						_localctx = new Binop_inequality_comparisonContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_inequality_comparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(141);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(142);
						((Binop_inequality_comparisonContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LESS_THAN) | (1L << LESS_THAN_EQUAL) | (1L << GREATER_THAN_EQUAL) | (1L << GREATER_THAN))) != 0)) ) {
							((Binop_inequality_comparisonContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(143);
						((Binop_inequality_comparisonContext)_localctx).right = expression(11);
						}
						break;
					case 6:
						{
						_localctx = new Binop_equality_comparisonContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_equality_comparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(144);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(145);
						((Binop_equality_comparisonContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NOT_EQUAL) | (1L << EQUAL) | (1L << STRICT_EQUAL))) != 0)) ) {
							((Binop_equality_comparisonContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(146);
						((Binop_equality_comparisonContext)_localctx).right = expression(10);
						}
						break;
					case 7:
						{
						_localctx = new Binop_andContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_andContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(147);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(148);
						match(AND);
						setState(149);
						((Binop_andContext)_localctx).right = expression(9);
						}
						break;
					case 8:
						{
						_localctx = new Binop_orContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_orContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(150);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(151);
						match(OR);
						setState(152);
						((Binop_orContext)_localctx).right = expression(8);
						}
						break;
					}
					} 
				}
				setState(157);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PropaccessContext extends ParserRuleContext {
		public IdContext prop;
		public Var_refContext var_ref() {
			return getRuleContext(Var_refContext.class,0);
		}
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Unit_refContext unit_ref() {
			return getRuleContext(Unit_refContext.class,0);
		}
		public PropaccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_propaccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterPropaccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitPropaccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitPropaccess(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PropaccessContext propaccess() throws RecognitionException {
		PropaccessContext _localctx = new PropaccessContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_propaccess);
		try {
			setState(166);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(158);
				var_ref();
				setState(159);
				match(DOT);
				setState(160);
				((PropaccessContext)_localctx).prop = id();
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(162);
				unit_ref();
				setState(163);
				match(DOT);
				setState(164);
				((PropaccessContext)_localctx).prop = id();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Numeric_tContext extends ParserRuleContext {
		public Float_tContext float_t() {
			return getRuleContext(Float_tContext.class,0);
		}
		public Int_tContext int_t() {
			return getRuleContext(Int_tContext.class,0);
		}
		public Numeric_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterNumeric_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitNumeric_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitNumeric_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Numeric_tContext numeric_t() throws RecognitionException {
		Numeric_tContext _localctx = new Numeric_tContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_numeric_t);
		try {
			setState(170);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLOAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(168);
				float_t();
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(169);
				int_t();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AllocContext extends ParserRuleContext {
		public TerminalNode ALLOCATE() { return getToken(MindcodeParser.ALLOCATE, 0); }
		public Alloc_listContext alloc_list() {
			return getRuleContext(Alloc_listContext.class,0);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(ALLOCATE);
			setState(173);
			alloc_list(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Alloc_listContext extends ParserRuleContext {
		public Token type;
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode HEAP() { return getToken(MindcodeParser.HEAP, 0); }
		public TerminalNode STACK() { return getToken(MindcodeParser.STACK, 0); }
		public Alloc_rangeContext alloc_range() {
			return getRuleContext(Alloc_rangeContext.class,0);
		}
		public Alloc_listContext alloc_list() {
			return getRuleContext(Alloc_listContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public Alloc_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAlloc_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAlloc_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAlloc_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Alloc_listContext alloc_list() throws RecognitionException {
		return alloc_list(0);
	}

	private Alloc_listContext alloc_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Alloc_listContext _localctx = new Alloc_listContext(_ctx, _parentState);
		Alloc_listContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_alloc_list, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(176);
			((Alloc_listContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==HEAP || _la==STACK) ) {
				((Alloc_listContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(177);
			match(IN);
			setState(178);
			id();
			setState(180);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(179);
				alloc_range();
				}
				break;
			}
			}
			_ctx.stop = _input.LT(-1);
			setState(192);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Alloc_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_alloc_list);
					setState(182);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(183);
					match(COMMA);
					setState(184);
					((Alloc_listContext)_localctx).type = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==HEAP || _la==STACK) ) {
						((Alloc_listContext)_localctx).type = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(185);
					match(IN);
					setState(186);
					id();
					setState(188);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						setState(187);
						alloc_range();
						}
						break;
					}
					}
					} 
				}
				setState(194);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Alloc_rangeContext extends ParserRuleContext {
		public TerminalNode LEFT_SBRACKET() { return getToken(MindcodeParser.LEFT_SBRACKET, 0); }
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public TerminalNode RIGHT_SBRACKET() { return getToken(MindcodeParser.RIGHT_SBRACKET, 0); }
		public Alloc_rangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc_range; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAlloc_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAlloc_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAlloc_range(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Alloc_rangeContext alloc_range() throws RecognitionException {
		Alloc_rangeContext _localctx = new Alloc_rangeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_alloc_range);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(LEFT_SBRACKET);
			setState(196);
			range();
			setState(197);
			match(RIGHT_SBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FundeclContext extends ParserRuleContext {
		public TerminalNode DEF() { return getToken(MindcodeParser.DEF, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public Arg_decl_listContext arg_decl_list() {
			return getRuleContext(Arg_decl_listContext.class,0);
		}
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public FundeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fundecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFundecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFundecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFundecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FundeclContext fundecl() throws RecognitionException {
		FundeclContext _localctx = new FundeclContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_fundecl);
		try {
			setState(212);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(199);
				match(DEF);
				setState(200);
				id();
				setState(201);
				match(LEFT_RBRACKET);
				setState(202);
				arg_decl_list(0);
				setState(203);
				match(RIGHT_RBRACKET);
				setState(204);
				expression_list(0);
				setState(205);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(207);
				match(DEF);
				setState(208);
				id();
				setState(209);
				expression_list(0);
				setState(210);
				match(END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Arg_decl_listContext extends ParserRuleContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Arg_decl_listContext arg_decl_list() {
			return getRuleContext(Arg_decl_listContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public Arg_decl_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg_decl_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterArg_decl_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitArg_decl_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitArg_decl_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arg_decl_listContext arg_decl_list() throws RecognitionException {
		return arg_decl_list(0);
	}

	private Arg_decl_listContext arg_decl_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Arg_decl_listContext _localctx = new Arg_decl_listContext(_ctx, _parentState);
		Arg_decl_listContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_arg_decl_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(215);
				lvalue();
				}
				break;
			case 2:
				{
				setState(216);
				lvalue();
				setState(217);
				match(ASSIGN);
				setState(218);
				expression(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(233);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(231);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						_localctx = new Arg_decl_listContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_arg_decl_list);
						setState(222);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(223);
						match(COMMA);
						setState(224);
						lvalue();
						}
						break;
					case 2:
						{
						_localctx = new Arg_decl_listContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_arg_decl_list);
						setState(225);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(226);
						match(COMMA);
						setState(227);
						lvalue();
						setState(228);
						match(ASSIGN);
						setState(229);
						expression(0);
						}
						break;
					}
					} 
				}
				setState(235);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class While_expressionContext extends ParserRuleContext {
		public ExpressionContext cond;
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public While_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_while_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterWhile_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitWhile_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitWhile_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final While_expressionContext while_expression() throws RecognitionException {
		While_expressionContext _localctx = new While_expressionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_while_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			match(WHILE);
			setState(237);
			((While_expressionContext)_localctx).cond = expression(0);
			setState(238);
			loop_body(0);
			setState(239);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class For_expressionContext extends ParserRuleContext {
		public For_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_expression; }
	 
		public For_expressionContext() { }
		public void copyFrom(For_expressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Iterated_forContext extends For_expressionContext {
		public Init_listContext init;
		public ExpressionContext cond;
		public Incr_listContext increment;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public Init_listContext init_list() {
			return getRuleContext(Init_listContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Incr_listContext incr_list() {
			return getRuleContext(Incr_listContext.class,0);
		}
		public Iterated_forContext(For_expressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIterated_for(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIterated_for(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIterated_for(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Ranged_forContext extends For_expressionContext {
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public Ranged_forContext(For_expressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRanged_for(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRanged_for(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRanged_for(this);
			else return visitor.visitChildren(this);
		}
	}

	public final For_expressionContext for_expression() throws RecognitionException {
		For_expressionContext _localctx = new For_expressionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_for_expression);
		try {
			setState(257);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				_localctx = new Ranged_forContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(241);
				match(FOR);
				setState(242);
				lvalue();
				setState(243);
				match(IN);
				setState(244);
				range();
				setState(245);
				loop_body(0);
				setState(246);
				match(END);
				}
				break;
			case 2:
				_localctx = new Iterated_forContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				match(FOR);
				setState(249);
				((Iterated_forContext)_localctx).init = init_list(0);
				setState(250);
				match(SEMICOLON);
				setState(251);
				((Iterated_forContext)_localctx).cond = expression(0);
				setState(252);
				match(SEMICOLON);
				setState(253);
				((Iterated_forContext)_localctx).increment = incr_list(0);
				setState(254);
				loop_body(0);
				setState(255);
				match(END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Loop_bodyContext extends ParserRuleContext {
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public Break_stContext break_st() {
			return getRuleContext(Break_stContext.class,0);
		}
		public Continue_stContext continue_st() {
			return getRuleContext(Continue_stContext.class,0);
		}
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public Loop_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loop_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLoop_body(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLoop_body(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLoop_body(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Loop_bodyContext loop_body() throws RecognitionException {
		return loop_body(0);
	}

	private Loop_bodyContext loop_body(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Loop_bodyContext _localctx = new Loop_bodyContext(_ctx, _parentState);
		Loop_bodyContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_loop_body, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALLOCATE:
			case CASE:
			case DEF:
			case FALSE:
			case FOR:
			case IF:
			case NULL:
			case TRUE:
			case WHILE:
			case AT:
			case DOLLAR:
			case MINUS:
			case NOT:
			case LEFT_RBRACKET:
			case LITERAL:
			case FLOAT:
			case INT:
			case ID:
			case SL_COMMENT:
				{
				setState(260);
				expression_list(0);
				}
				break;
			case BREAK:
				{
				setState(261);
				break_st();
				}
				break;
			case CONTINUE:
				{
				setState(262);
				continue_st();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(273);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(271);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
					case 1:
						{
						_localctx = new Loop_bodyContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_loop_body);
						setState(265);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(266);
						expression_list(0);
						}
						break;
					case 2:
						{
						_localctx = new Loop_bodyContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_loop_body);
						setState(267);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(268);
						break_st();
						}
						break;
					case 3:
						{
						_localctx = new Loop_bodyContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_loop_body);
						setState(269);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(270);
						continue_st();
						}
						break;
					}
					} 
				}
				setState(275);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Continue_stContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(MindcodeParser.CONTINUE, 0); }
		public Continue_stContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continue_st; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterContinue_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitContinue_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitContinue_st(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Continue_stContext continue_st() throws RecognitionException {
		Continue_stContext _localctx = new Continue_stContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_continue_st);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(276);
			match(CONTINUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Break_stContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(MindcodeParser.BREAK, 0); }
		public Break_stContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_break_st; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBreak_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBreak_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBreak_st(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Break_stContext break_st() throws RecognitionException {
		Break_stContext _localctx = new Break_stContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_break_st);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(BREAK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RangeContext extends ParserRuleContext {
		public RangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range; }
	 
		public RangeContext() { }
		public void copyFrom(RangeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Inclusive_rangeContext extends RangeContext {
		public Int_tContext start;
		public Int_tContext end;
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public List<Int_tContext> int_t() {
			return getRuleContexts(Int_tContext.class);
		}
		public Int_tContext int_t(int i) {
			return getRuleContext(Int_tContext.class,i);
		}
		public Inclusive_rangeContext(RangeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterInclusive_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitInclusive_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitInclusive_range(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Exclusive_rangeContext extends RangeContext {
		public Int_tContext start;
		public Int_tContext end;
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public List<Int_tContext> int_t() {
			return getRuleContexts(Int_tContext.class);
		}
		public Int_tContext int_t(int i) {
			return getRuleContext(Int_tContext.class,i);
		}
		public Exclusive_rangeContext(RangeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExclusive_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExclusive_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExclusive_range(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RangeContext range() throws RecognitionException {
		RangeContext _localctx = new RangeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_range);
		try {
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				_localctx = new Inclusive_rangeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(280);
				((Inclusive_rangeContext)_localctx).start = int_t();
				setState(281);
				match(DOT);
				setState(282);
				match(DOT);
				setState(283);
				((Inclusive_rangeContext)_localctx).end = int_t();
				}
				break;
			case 2:
				_localctx = new Exclusive_rangeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(285);
				((Exclusive_rangeContext)_localctx).start = int_t();
				setState(286);
				match(DOT);
				setState(287);
				match(DOT);
				setState(288);
				match(DOT);
				setState(289);
				((Exclusive_rangeContext)_localctx).end = int_t();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Init_listContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Init_listContext init_list() {
			return getRuleContext(Init_listContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public Init_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_init_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterInit_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitInit_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitInit_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Init_listContext init_list() throws RecognitionException {
		return init_list(0);
	}

	private Init_listContext init_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Init_listContext _localctx = new Init_listContext(_ctx, _parentState);
		Init_listContext _prevctx = _localctx;
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_init_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(294);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(301);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Init_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_init_list);
					setState(296);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(297);
					match(COMMA);
					setState(298);
					expression(0);
					}
					} 
				}
				setState(303);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Incr_listContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Incr_listContext incr_list() {
			return getRuleContext(Incr_listContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public Incr_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_incr_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIncr_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIncr_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIncr_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Incr_listContext incr_list() throws RecognitionException {
		return incr_list(0);
	}

	private Incr_listContext incr_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Incr_listContext _localctx = new Incr_listContext(_ctx, _parentState);
		Incr_listContext _prevctx = _localctx;
		int _startState = 34;
		enterRecursionRule(_localctx, 34, RULE_incr_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(305);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(312);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Incr_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_incr_list);
					setState(307);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(308);
					match(COMMA);
					setState(309);
					expression(0);
					}
					} 
				}
				setState(314);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FuncallContext extends ParserRuleContext {
		public IdContext name;
		public Arg_listContext params;
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public FuncallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFuncall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFuncall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFuncall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncallContext funcall() throws RecognitionException {
		FuncallContext _localctx = new FuncallContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_funcall);
		try {
			setState(324);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(315);
				((FuncallContext)_localctx).name = id();
				setState(316);
				match(LEFT_RBRACKET);
				setState(317);
				match(RIGHT_RBRACKET);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(319);
				((FuncallContext)_localctx).name = id();
				setState(320);
				match(LEFT_RBRACKET);
				setState(321);
				((FuncallContext)_localctx).params = arg_list(0);
				setState(322);
				match(RIGHT_RBRACKET);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Arg_listContext extends ParserRuleContext {
		public ArgContext arg() {
			return getRuleContext(ArgContext.class,0);
		}
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public Arg_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterArg_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitArg_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitArg_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arg_listContext arg_list() throws RecognitionException {
		return arg_list(0);
	}

	private Arg_listContext arg_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Arg_listContext _localctx = new Arg_listContext(_ctx, _parentState);
		Arg_listContext _prevctx = _localctx;
		int _startState = 38;
		enterRecursionRule(_localctx, 38, RULE_arg_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(327);
			arg();
			}
			_ctx.stop = _input.LT(-1);
			setState(334);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Arg_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_arg_list);
					setState(329);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(330);
					match(COMMA);
					setState(331);
					arg();
					}
					} 
				}
				setState(336);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ArgContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgContext arg() throws RecognitionException {
		ArgContext _localctx = new ArgContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_arg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_exprContext extends ParserRuleContext {
		public ExpressionContext cond;
		public Expression_listContext true_branch;
		public TerminalNode IF() { return getToken(MindcodeParser.IF, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public If_trailerContext if_trailer() {
			return getRuleContext(If_trailerContext.class,0);
		}
		public If_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIf_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIf_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIf_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_exprContext if_expr() throws RecognitionException {
		If_exprContext _localctx = new If_exprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_if_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(339);
			match(IF);
			setState(340);
			((If_exprContext)_localctx).cond = expression(0);
			setState(341);
			((If_exprContext)_localctx).true_branch = expression_list(0);
			setState(343);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(342);
				if_trailer();
				}
			}

			setState(345);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_trailerContext extends ParserRuleContext {
		public Expression_listContext false_branch;
		public ExpressionContext cond;
		public Expression_listContext true_branch;
		public TerminalNode ELSE() { return getToken(MindcodeParser.ELSE, 0); }
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public TerminalNode IF() { return getToken(MindcodeParser.IF, 0); }
		public If_trailerContext if_trailer() {
			return getRuleContext(If_trailerContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public If_trailerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_trailer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIf_trailer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIf_trailer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIf_trailer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_trailerContext if_trailer() throws RecognitionException {
		If_trailerContext _localctx = new If_trailerContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_if_trailer);
		try {
			setState(355);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				match(ELSE);
				setState(348);
				((If_trailerContext)_localctx).false_branch = expression_list(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(349);
				match(ELSE);
				setState(350);
				match(IF);
				setState(351);
				((If_trailerContext)_localctx).cond = expression(0);
				setState(352);
				((If_trailerContext)_localctx).true_branch = expression_list(0);
				setState(353);
				if_trailer();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Case_exprContext extends ParserRuleContext {
		public ExpressionContext cond;
		public Expression_listContext else_branch;
		public TerminalNode CASE() { return getToken(MindcodeParser.CASE, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Alternative_listContext alternative_list() {
			return getRuleContext(Alternative_listContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(MindcodeParser.ELSE, 0); }
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public Case_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_case_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCase_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCase_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCase_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Case_exprContext case_expr() throws RecognitionException {
		Case_exprContext _localctx = new Case_exprContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_case_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
			match(CASE);
			setState(358);
			((Case_exprContext)_localctx).cond = expression(0);
			setState(360);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHEN) {
				{
				setState(359);
				alternative_list(0);
				}
			}

			setState(364);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(362);
				match(ELSE);
				setState(363);
				((Case_exprContext)_localctx).else_branch = expression_list(0);
				}
			}

			setState(366);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Alternative_listContext extends ParserRuleContext {
		public AlternativeContext alternative() {
			return getRuleContext(AlternativeContext.class,0);
		}
		public Alternative_listContext alternative_list() {
			return getRuleContext(Alternative_listContext.class,0);
		}
		public Alternative_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alternative_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAlternative_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAlternative_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAlternative_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Alternative_listContext alternative_list() throws RecognitionException {
		return alternative_list(0);
	}

	private Alternative_listContext alternative_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Alternative_listContext _localctx = new Alternative_listContext(_ctx, _parentState);
		Alternative_listContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_alternative_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(369);
			alternative();
			}
			_ctx.stop = _input.LT(-1);
			setState(375);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Alternative_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_alternative_list);
					setState(371);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(372);
					alternative();
					}
					} 
				}
				setState(377);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class AlternativeContext extends ParserRuleContext {
		public ExpressionContext value;
		public Expression_listContext body;
		public TerminalNode WHEN() { return getToken(MindcodeParser.WHEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public AlternativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alternative; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAlternative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAlternative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAlternative(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AlternativeContext alternative() throws RecognitionException {
		AlternativeContext _localctx = new AlternativeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_alternative);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(378);
			match(WHEN);
			setState(379);
			((AlternativeContext)_localctx).value = expression(0);
			setState(380);
			((AlternativeContext)_localctx).body = expression_list(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignContext extends ParserRuleContext {
		public AssignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign; }
	 
		public AssignContext() { }
		public void copyFrom(AssignContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Exp_assignContext extends AssignContext {
		public LvalueContext target;
		public ExpressionContext value;
		public TerminalNode EXP_ASSIGN() { return getToken(MindcodeParser.EXP_ASSIGN, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Exp_assignContext(AssignContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExp_assign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExp_assign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExp_assign(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_plus_minus_assignContext extends AssignContext {
		public LvalueContext target;
		public Token op;
		public ExpressionContext value;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode PLUS_ASSIGN() { return getToken(MindcodeParser.PLUS_ASSIGN, 0); }
		public TerminalNode MINUS_ASSIGN() { return getToken(MindcodeParser.MINUS_ASSIGN, 0); }
		public Binop_plus_minus_assignContext(AssignContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_plus_minus_assign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_plus_minus_assign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_plus_minus_assign(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Binop_mul_div_assignContext extends AssignContext {
		public LvalueContext target;
		public Token op;
		public ExpressionContext value;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode MUL_ASSIGN() { return getToken(MindcodeParser.MUL_ASSIGN, 0); }
		public TerminalNode DIV_ASSIGN() { return getToken(MindcodeParser.DIV_ASSIGN, 0); }
		public Binop_mul_div_assignContext(AssignContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_mul_div_assign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_mul_div_assign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_mul_div_assign(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class Simple_assignContext extends AssignContext {
		public LvalueContext target;
		public ExpressionContext value;
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Simple_assignContext(AssignContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterSimple_assign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitSimple_assign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitSimple_assign(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignContext assign() throws RecognitionException {
		AssignContext _localctx = new AssignContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_assign);
		int _la;
		try {
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				_localctx = new Simple_assignContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(382);
				((Simple_assignContext)_localctx).target = lvalue();
				setState(383);
				match(ASSIGN);
				setState(384);
				((Simple_assignContext)_localctx).value = expression(0);
				}
				break;
			case 2:
				_localctx = new Exp_assignContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(386);
				((Exp_assignContext)_localctx).target = lvalue();
				setState(387);
				match(EXP_ASSIGN);
				setState(388);
				((Exp_assignContext)_localctx).value = expression(0);
				}
				break;
			case 3:
				_localctx = new Binop_mul_div_assignContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(390);
				((Binop_mul_div_assignContext)_localctx).target = lvalue();
				setState(391);
				((Binop_mul_div_assignContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DIV_ASSIGN || _la==MUL_ASSIGN) ) {
					((Binop_mul_div_assignContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(392);
				((Binop_mul_div_assignContext)_localctx).value = expression(0);
				}
				break;
			case 4:
				_localctx = new Binop_plus_minus_assignContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(394);
				((Binop_plus_minus_assignContext)_localctx).target = lvalue();
				setState(395);
				((Binop_plus_minus_assignContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==MINUS_ASSIGN || _la==PLUS_ASSIGN) ) {
					((Binop_plus_minus_assignContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(396);
				((Binop_plus_minus_assignContext)_localctx).value = expression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LvalueContext extends ParserRuleContext {
		public Unit_refContext unit_ref() {
			return getRuleContext(Unit_refContext.class,0);
		}
		public Global_refContext global_ref() {
			return getRuleContext(Global_refContext.class,0);
		}
		public Heap_refContext heap_ref() {
			return getRuleContext(Heap_refContext.class,0);
		}
		public Var_refContext var_ref() {
			return getRuleContext(Var_refContext.class,0);
		}
		public PropaccessContext propaccess() {
			return getRuleContext(PropaccessContext.class,0);
		}
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLvalue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLvalue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_lvalue);
		try {
			setState(405);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(400);
				unit_ref();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(401);
				global_ref();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(402);
				heap_ref();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(403);
				var_ref();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(404);
				propaccess();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Heap_refContext extends ParserRuleContext {
		public IdContext name;
		public ExpressionContext address;
		public TerminalNode LEFT_SBRACKET() { return getToken(MindcodeParser.LEFT_SBRACKET, 0); }
		public TerminalNode RIGHT_SBRACKET() { return getToken(MindcodeParser.RIGHT_SBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Heap_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_heap_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterHeap_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitHeap_ref(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitHeap_ref(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Heap_refContext heap_ref() throws RecognitionException {
		Heap_refContext _localctx = new Heap_refContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_heap_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			((Heap_refContext)_localctx).name = id();
			setState(408);
			match(LEFT_SBRACKET);
			setState(409);
			((Heap_refContext)_localctx).address = expression(0);
			setState(410);
			match(RIGHT_SBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Global_refContext extends ParserRuleContext {
		public IdContext name;
		public TerminalNode DOLLAR() { return getToken(MindcodeParser.DOLLAR, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Global_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterGlobal_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitGlobal_ref(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitGlobal_ref(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Global_refContext global_ref() throws RecognitionException {
		Global_refContext _localctx = new Global_refContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_global_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			match(DOLLAR);
			setState(413);
			((Global_refContext)_localctx).name = id();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unit_refContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(MindcodeParser.AT, 0); }
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public Unit_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unit_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterUnit_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitUnit_ref(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitUnit_ref(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Unit_refContext unit_ref() throws RecognitionException {
		Unit_refContext _localctx = new Unit_refContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_unit_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			match(AT);
			setState(416);
			ref();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Var_refContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Var_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterVar_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitVar_ref(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitVar_ref(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_refContext var_ref() throws RecognitionException {
		Var_refContext _localctx = new Var_refContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_var_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			id();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RefContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MindcodeParser.ID, 0); }
		public RefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RefContext ref() throws RecognitionException {
		RefContext _localctx = new RefContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Int_tContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(MindcodeParser.INT, 0); }
		public Int_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_int_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterInt_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitInt_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitInt_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Int_tContext int_t() throws RecognitionException {
		Int_tContext _localctx = new Int_tContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_int_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Float_tContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(MindcodeParser.FLOAT, 0); }
		public Float_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_float_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFloat_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFloat_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFloat_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Float_tContext float_t() throws RecognitionException {
		Float_tContext _localctx = new Float_tContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_float_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(424);
			match(FLOAT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Literal_tContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(MindcodeParser.LITERAL, 0); }
		public Literal_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Literal_tContext literal_t() throws RecognitionException {
		Literal_tContext _localctx = new Literal_tContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_literal_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(426);
			match(LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Null_tContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(MindcodeParser.NULL, 0); }
		public Null_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterNull_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitNull_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitNull_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Null_tContext null_t() throws RecognitionException {
		Null_tContext _localctx = new Null_tContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_null_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bool_tContext extends ParserRuleContext {
		public Bool_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool_t; }
	 
		public Bool_tContext() { }
		public void copyFrom(Bool_tContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class True_bool_literalContext extends Bool_tContext {
		public True_tContext true_t() {
			return getRuleContext(True_tContext.class,0);
		}
		public True_bool_literalContext(Bool_tContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterTrue_bool_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitTrue_bool_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitTrue_bool_literal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class False_bool_literalContext extends Bool_tContext {
		public False_tContext false_t() {
			return getRuleContext(False_tContext.class,0);
		}
		public False_bool_literalContext(Bool_tContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFalse_bool_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFalse_bool_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFalse_bool_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bool_tContext bool_t() throws RecognitionException {
		Bool_tContext _localctx = new Bool_tContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_bool_t);
		try {
			setState(432);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
				_localctx = new True_bool_literalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(430);
				true_t();
				}
				break;
			case FALSE:
				_localctx = new False_bool_literalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(431);
				false_t();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class True_tContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(MindcodeParser.TRUE, 0); }
		public True_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_true_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterTrue_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitTrue_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitTrue_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final True_tContext true_t() throws RecognitionException {
		True_tContext _localctx = new True_tContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_true_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(434);
			match(TRUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class False_tContext extends ParserRuleContext {
		public TerminalNode FALSE() { return getToken(MindcodeParser.FALSE, 0); }
		public False_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_false_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFalse_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFalse_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFalse_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final False_tContext false_t() throws RecognitionException {
		False_tContext _localctx = new False_tContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_false_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			match(FALSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MindcodeParser.ID, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class A_commentContext extends ParserRuleContext {
		public TerminalNode SL_COMMENT() { return getToken(MindcodeParser.SL_COMMENT, 0); }
		public A_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_a_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterA_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitA_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitA_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final A_commentContext a_comment() throws RecognitionException {
		A_commentContext _localctx = new A_commentContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_a_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			match(SL_COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expression_list_sempred((Expression_listContext)_localctx, predIndex);
		case 2:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 6:
			return alloc_list_sempred((Alloc_listContext)_localctx, predIndex);
		case 9:
			return arg_decl_list_sempred((Arg_decl_listContext)_localctx, predIndex);
		case 12:
			return loop_body_sempred((Loop_bodyContext)_localctx, predIndex);
		case 16:
			return init_list_sempred((Init_listContext)_localctx, predIndex);
		case 17:
			return incr_list_sempred((Incr_listContext)_localctx, predIndex);
		case 19:
			return arg_list_sempred((Arg_listContext)_localctx, predIndex);
		case 24:
			return alternative_list_sempred((Alternative_listContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_list_sempred(Expression_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 24);
		case 2:
			return precpred(_ctx, 14);
		case 3:
			return precpred(_ctx, 12);
		case 4:
			return precpred(_ctx, 11);
		case 5:
			return precpred(_ctx, 10);
		case 6:
			return precpred(_ctx, 9);
		case 7:
			return precpred(_ctx, 8);
		case 8:
			return precpred(_ctx, 7);
		}
		return true;
	}
	private boolean alloc_list_sempred(Alloc_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arg_decl_list_sempred(Arg_decl_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return precpred(_ctx, 3);
		case 11:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean loop_body_sempred(Loop_bodyContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return precpred(_ctx, 6);
		case 13:
			return precpred(_ctx, 5);
		case 14:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean init_list_sempred(Init_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 15:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean incr_list_sempred(Incr_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 16:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arg_list_sempred(Arg_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 17:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean alternative_list_sempred(Alternative_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 18:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3<\u01bd\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\3\2\3\2\3\2\5\2[\n\2\3\3\3\3\3\3\3\3\3\3\7\3b\n\3\f\3\16\3e\13\3\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\5\4\177\n\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\7\4\u009c\n\4\f\4\16\4\u009f\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\5\5\u00a9\n\5\3\6\3\6\5\6\u00ad\n\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\5"+
		"\b\u00b7\n\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00bf\n\b\7\b\u00c1\n\b\f\b\16"+
		"\b\u00c4\13\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\5\n\u00d7\n\n\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u00df\n"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00ea\n\13\f\13"+
		"\16\13\u00ed\13\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u0104\n\r\3\16\3\16\3\16\3\16\5\16"+
		"\u010a\n\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u0112\n\16\f\16\16\16\u0115"+
		"\13\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\5\21\u0126\n\21\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u012e\n"+
		"\22\f\22\16\22\u0131\13\22\3\23\3\23\3\23\3\23\3\23\3\23\7\23\u0139\n"+
		"\23\f\23\16\23\u013c\13\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\5\24\u0147\n\24\3\25\3\25\3\25\3\25\3\25\3\25\7\25\u014f\n\25\f\25\16"+
		"\25\u0152\13\25\3\26\3\26\3\27\3\27\3\27\3\27\5\27\u015a\n\27\3\27\3\27"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u0166\n\30\3\31\3\31\3\31"+
		"\5\31\u016b\n\31\3\31\3\31\5\31\u016f\n\31\3\31\3\31\3\32\3\32\3\32\3"+
		"\32\3\32\7\32\u0178\n\32\f\32\16\32\u017b\13\32\3\33\3\33\3\33\3\33\3"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3"+
		"\34\3\34\5\34\u0191\n\34\3\35\3\35\3\35\3\35\3\35\5\35\u0198\n\35\3\36"+
		"\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3"+
		"$\3%\3%\3&\3&\3\'\3\'\5\'\u01b3\n\'\3(\3(\3)\3)\3*\3*\3+\3+\3+\2\13\4"+
		"\6\16\24\32\"$(\62,\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<>@BDFHJLNPRT\2\t\4\2\30\30\35\36\4\2\34\34  \4\2()-.\3\2*"+
		",\4\2\f\f\20\20\4\2##&&\4\2%%\'\'\2\u01ce\2Z\3\2\2\2\4\\\3\2\2\2\6~\3"+
		"\2\2\2\b\u00a8\3\2\2\2\n\u00ac\3\2\2\2\f\u00ae\3\2\2\2\16\u00b1\3\2\2"+
		"\2\20\u00c5\3\2\2\2\22\u00d6\3\2\2\2\24\u00de\3\2\2\2\26\u00ee\3\2\2\2"+
		"\30\u0103\3\2\2\2\32\u0109\3\2\2\2\34\u0116\3\2\2\2\36\u0118\3\2\2\2 "+
		"\u0125\3\2\2\2\"\u0127\3\2\2\2$\u0132\3\2\2\2&\u0146\3\2\2\2(\u0148\3"+
		"\2\2\2*\u0153\3\2\2\2,\u0155\3\2\2\2.\u0165\3\2\2\2\60\u0167\3\2\2\2\62"+
		"\u0172\3\2\2\2\64\u017c\3\2\2\2\66\u0190\3\2\2\28\u0197\3\2\2\2:\u0199"+
		"\3\2\2\2<\u019e\3\2\2\2>\u01a1\3\2\2\2@\u01a4\3\2\2\2B\u01a6\3\2\2\2D"+
		"\u01a8\3\2\2\2F\u01aa\3\2\2\2H\u01ac\3\2\2\2J\u01ae\3\2\2\2L\u01b2\3\2"+
		"\2\2N\u01b4\3\2\2\2P\u01b6\3\2\2\2R\u01b8\3\2\2\2T\u01ba\3\2\2\2VW\5\4"+
		"\3\2WX\7\2\2\3X[\3\2\2\2Y[\7\2\2\3ZV\3\2\2\2ZY\3\2\2\2[\3\3\2\2\2\\]\b"+
		"\3\1\2]^\5\6\4\2^c\3\2\2\2_`\f\3\2\2`b\5\6\4\2a_\3\2\2\2be\3\2\2\2ca\3"+
		"\2\2\2cd\3\2\2\2d\5\3\2\2\2ec\3\2\2\2fg\b\4\1\2g\177\5T+\2h\177\5\b\5"+
		"\2i\177\5\60\31\2j\177\5,\27\2k\177\5&\24\2l\177\5\22\n\2m\177\5\f\7\2"+
		"n\177\5\66\34\2o\177\58\35\2p\177\5\26\f\2q\177\5\30\r\2rs\7\37\2\2s\177"+
		"\5\6\4\17t\177\5H%\2u\177\5\n\6\2v\177\5L\'\2wx\7\34\2\2x\177\5\n\6\2"+
		"y\177\5J&\2z{\7\63\2\2{|\5\6\4\2|}\7\64\2\2}\177\3\2\2\2~f\3\2\2\2~h\3"+
		"\2\2\2~i\3\2\2\2~j\3\2\2\2~k\3\2\2\2~l\3\2\2\2~m\3\2\2\2~n\3\2\2\2~o\3"+
		"\2\2\2~p\3\2\2\2~q\3\2\2\2~r\3\2\2\2~t\3\2\2\2~u\3\2\2\2~v\3\2\2\2~w\3"+
		"\2\2\2~y\3\2\2\2~z\3\2\2\2\177\u009d\3\2\2\2\u0080\u0081\f\32\2\2\u0081"+
		"\u0082\7!\2\2\u0082\u0083\5\6\4\2\u0083\u0084\7\26\2\2\u0084\u0085\5\6"+
		"\4\33\u0085\u009c\3\2\2\2\u0086\u0087\f\20\2\2\u0087\u0088\7\33\2\2\u0088"+
		"\u009c\5\6\4\21\u0089\u008a\f\16\2\2\u008a\u008b\t\2\2\2\u008b\u009c\5"+
		"\6\4\17\u008c\u008d\f\r\2\2\u008d\u008e\t\3\2\2\u008e\u009c\5\6\4\16\u008f"+
		"\u0090\f\f\2\2\u0090\u0091\t\4\2\2\u0091\u009c\5\6\4\r\u0092\u0093\f\13"+
		"\2\2\u0093\u0094\t\5\2\2\u0094\u009c\5\6\4\f\u0095\u0096\f\n\2\2\u0096"+
		"\u0097\7/\2\2\u0097\u009c\5\6\4\13\u0098\u0099\f\t\2\2\u0099\u009a\7\60"+
		"\2\2\u009a\u009c\5\6\4\n\u009b\u0080\3\2\2\2\u009b\u0086\3\2\2\2\u009b"+
		"\u0089\3\2\2\2\u009b\u008c\3\2\2\2\u009b\u008f\3\2\2\2\u009b\u0092\3\2"+
		"\2\2\u009b\u0095\3\2\2\2\u009b\u0098\3\2\2\2\u009c\u009f\3\2\2\2\u009d"+
		"\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e\7\3\2\2\2\u009f\u009d\3\2\2\2"+
		"\u00a0\u00a1\5@!\2\u00a1\u00a2\7\32\2\2\u00a2\u00a3\5R*\2\u00a3\u00a9"+
		"\3\2\2\2\u00a4\u00a5\5> \2\u00a5\u00a6\7\32\2\2\u00a6\u00a7\5R*\2\u00a7"+
		"\u00a9\3\2\2\2\u00a8\u00a0\3\2\2\2\u00a8\u00a4\3\2\2\2\u00a9\t\3\2\2\2"+
		"\u00aa\u00ad\5F$\2\u00ab\u00ad\5D#\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab\3"+
		"\2\2\2\u00ad\13\3\2\2\2\u00ae\u00af\7\3\2\2\u00af\u00b0\5\16\b\2\u00b0"+
		"\r\3\2\2\2\u00b1\u00b2\b\b\1\2\u00b2\u00b3\t\6\2\2\u00b3\u00b4\7\16\2"+
		"\2\u00b4\u00b6\5R*\2\u00b5\u00b7\5\20\t\2\u00b6\u00b5\3\2\2\2\u00b6\u00b7"+
		"\3\2\2\2\u00b7\u00c2\3\2\2\2\u00b8\u00b9\f\3\2\2\u00b9\u00ba\7\27\2\2"+
		"\u00ba\u00bb\t\6\2\2\u00bb\u00bc\7\16\2\2\u00bc\u00be\5R*\2\u00bd\u00bf"+
		"\5\20\t\2\u00be\u00bd\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c1\3\2\2\2"+
		"\u00c0\u00b8\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3"+
		"\3\2\2\2\u00c3\17\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00c6\7\61\2\2\u00c6"+
		"\u00c7\5 \21\2\u00c7\u00c8\7\62\2\2\u00c8\21\3\2\2\2\u00c9\u00ca\7\7\2"+
		"\2\u00ca\u00cb\5R*\2\u00cb\u00cc\7\63\2\2\u00cc\u00cd\5\24\13\2\u00cd"+
		"\u00ce\7\64\2\2\u00ce\u00cf\5\4\3\2\u00cf\u00d0\7\t\2\2\u00d0\u00d7\3"+
		"\2\2\2\u00d1\u00d2\7\7\2\2\u00d2\u00d3\5R*\2\u00d3\u00d4\5\4\3\2\u00d4"+
		"\u00d5\7\t\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00c9\3\2\2\2\u00d6\u00d1\3\2"+
		"\2\2\u00d7\23\3\2\2\2\u00d8\u00d9\b\13\1\2\u00d9\u00df\58\35\2\u00da\u00db"+
		"\58\35\2\u00db\u00dc\7\24\2\2\u00dc\u00dd\5\6\4\2\u00dd\u00df\3\2\2\2"+
		"\u00de\u00d8\3\2\2\2\u00de\u00da\3\2\2\2\u00df\u00eb\3\2\2\2\u00e0\u00e1"+
		"\f\5\2\2\u00e1\u00e2\7\27\2\2\u00e2\u00ea\58\35\2\u00e3\u00e4\f\3\2\2"+
		"\u00e4\u00e5\7\27\2\2\u00e5\u00e6\58\35\2\u00e6\u00e7\7\24\2\2\u00e7\u00e8"+
		"\5\6\4\2\u00e8\u00ea\3\2\2\2\u00e9\u00e0\3\2\2\2\u00e9\u00e3\3\2\2\2\u00ea"+
		"\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\25\3\2\2"+
		"\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\7\23\2\2\u00ef\u00f0\5\6\4\2\u00f0"+
		"\u00f1\5\32\16\2\u00f1\u00f2\7\t\2\2\u00f2\27\3\2\2\2\u00f3\u00f4\7\13"+
		"\2\2\u00f4\u00f5\58\35\2\u00f5\u00f6\7\16\2\2\u00f6\u00f7\5 \21\2\u00f7"+
		"\u00f8\5\32\16\2\u00f8\u00f9\7\t\2\2\u00f9\u0104\3\2\2\2\u00fa\u00fb\7"+
		"\13\2\2\u00fb\u00fc\5\"\22\2\u00fc\u00fd\7\"\2\2\u00fd\u00fe\5\6\4\2\u00fe"+
		"\u00ff\7\"\2\2\u00ff\u0100\5$\23\2\u0100\u0101\5\32\16\2\u0101\u0102\7"+
		"\t\2\2\u0102\u0104\3\2\2\2\u0103\u00f3\3\2\2\2\u0103\u00fa\3\2\2\2\u0104"+
		"\31\3\2\2\2\u0105\u0106\b\16\1\2\u0106\u010a\5\4\3\2\u0107\u010a\5\36"+
		"\20\2\u0108\u010a\5\34\17\2\u0109\u0105\3\2\2\2\u0109\u0107\3\2\2\2\u0109"+
		"\u0108\3\2\2\2\u010a\u0113\3\2\2\2\u010b\u010c\f\b\2\2\u010c\u0112\5\4"+
		"\3\2\u010d\u010e\f\7\2\2\u010e\u0112\5\36\20\2\u010f\u0110\f\6\2\2\u0110"+
		"\u0112\5\34\17\2\u0111\u010b\3\2\2\2\u0111\u010d\3\2\2\2\u0111\u010f\3"+
		"\2\2\2\u0112\u0115\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114"+
		"\33\3\2\2\2\u0115\u0113\3\2\2\2\u0116\u0117\7\6\2\2\u0117\35\3\2\2\2\u0118"+
		"\u0119\7\4\2\2\u0119\37\3\2\2\2\u011a\u011b\5D#\2\u011b\u011c\7\32\2\2"+
		"\u011c\u011d\7\32\2\2\u011d\u011e\5D#\2\u011e\u0126\3\2\2\2\u011f\u0120"+
		"\5D#\2\u0120\u0121\7\32\2\2\u0121\u0122\7\32\2\2\u0122\u0123\7\32\2\2"+
		"\u0123\u0124\5D#\2\u0124\u0126\3\2\2\2\u0125\u011a\3\2\2\2\u0125\u011f"+
		"\3\2\2\2\u0126!\3\2\2\2\u0127\u0128\b\22\1\2\u0128\u0129\5\6\4\2\u0129"+
		"\u012f\3\2\2\2\u012a\u012b\f\3\2\2\u012b\u012c\7\27\2\2\u012c\u012e\5"+
		"\6\4\2\u012d\u012a\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2\2\2\u012f"+
		"\u0130\3\2\2\2\u0130#\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0133\b\23\1\2"+
		"\u0133\u0134\5\6\4\2\u0134\u013a\3\2\2\2\u0135\u0136\f\3\2\2\u0136\u0137"+
		"\7\27\2\2\u0137\u0139\5\6\4\2\u0138\u0135\3\2\2\2\u0139\u013c\3\2\2\2"+
		"\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b%\3\2\2\2\u013c\u013a\3"+
		"\2\2\2\u013d\u013e\5R*\2\u013e\u013f\7\63\2\2\u013f\u0140\7\64\2\2\u0140"+
		"\u0147\3\2\2\2\u0141\u0142\5R*\2\u0142\u0143\7\63\2\2\u0143\u0144\5(\25"+
		"\2\u0144\u0145\7\64\2\2\u0145\u0147\3\2\2\2\u0146\u013d\3\2\2\2\u0146"+
		"\u0141\3\2\2\2\u0147\'\3\2\2\2\u0148\u0149\b\25\1\2\u0149\u014a\5*\26"+
		"\2\u014a\u0150\3\2\2\2\u014b\u014c\f\3\2\2\u014c\u014d\7\27\2\2\u014d"+
		"\u014f\5*\26\2\u014e\u014b\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2"+
		"\2\2\u0150\u0151\3\2\2\2\u0151)\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u0154"+
		"\5\6\4\2\u0154+\3\2\2\2\u0155\u0156\7\r\2\2\u0156\u0157\5\6\4\2\u0157"+
		"\u0159\5\4\3\2\u0158\u015a\5.\30\2\u0159\u0158\3\2\2\2\u0159\u015a\3\2"+
		"\2\2\u015a\u015b\3\2\2\2\u015b\u015c\7\t\2\2\u015c-\3\2\2\2\u015d\u015e"+
		"\7\b\2\2\u015e\u0166\5\4\3\2\u015f\u0160\7\b\2\2\u0160\u0161\7\r\2\2\u0161"+
		"\u0162\5\6\4\2\u0162\u0163\5\4\3\2\u0163\u0164\5.\30\2\u0164\u0166\3\2"+
		"\2\2\u0165\u015d\3\2\2\2\u0165\u015f\3\2\2\2\u0166/\3\2\2\2\u0167\u0168"+
		"\7\5\2\2\u0168\u016a\5\6\4\2\u0169\u016b\5\62\32\2\u016a\u0169\3\2\2\2"+
		"\u016a\u016b\3\2\2\2\u016b\u016e\3\2\2\2\u016c\u016d\7\b\2\2\u016d\u016f"+
		"\5\4\3\2\u016e\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\3\2\2\2\u0170"+
		"\u0171\7\t\2\2\u0171\61\3\2\2\2\u0172\u0173\b\32\1\2\u0173\u0174\5\64"+
		"\33\2\u0174\u0179\3\2\2\2\u0175\u0176\f\3\2\2\u0176\u0178\5\64\33\2\u0177"+
		"\u0175\3\2\2\2\u0178\u017b\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a\3\2"+
		"\2\2\u017a\63\3\2\2\2\u017b\u0179\3\2\2\2\u017c\u017d\7\22\2\2\u017d\u017e"+
		"\5\6\4\2\u017e\u017f\5\4\3\2\u017f\65\3\2\2\2\u0180\u0181\58\35\2\u0181"+
		"\u0182\7\24\2\2\u0182\u0183\5\6\4\2\u0183\u0191\3\2\2\2\u0184\u0185\5"+
		"8\35\2\u0185\u0186\7$\2\2\u0186\u0187\5\6\4\2\u0187\u0191\3\2\2\2\u0188"+
		"\u0189\58\35\2\u0189\u018a\t\7\2\2\u018a\u018b\5\6\4\2\u018b\u0191\3\2"+
		"\2\2\u018c\u018d\58\35\2\u018d\u018e\t\b\2\2\u018e\u018f\5\6\4\2\u018f"+
		"\u0191\3\2\2\2\u0190\u0180\3\2\2\2\u0190\u0184\3\2\2\2\u0190\u0188\3\2"+
		"\2\2\u0190\u018c\3\2\2\2\u0191\67\3\2\2\2\u0192\u0198\5> \2\u0193\u0198"+
		"\5<\37\2\u0194\u0198\5:\36\2\u0195\u0198\5@!\2\u0196\u0198\5\b\5\2\u0197"+
		"\u0192\3\2\2\2\u0197\u0193\3\2\2\2\u0197\u0194\3\2\2\2\u0197\u0195\3\2"+
		"\2\2\u0197\u0196\3\2\2\2\u01989\3\2\2\2\u0199\u019a\5R*\2\u019a\u019b"+
		"\7\61\2\2\u019b\u019c\5\6\4\2\u019c\u019d\7\62\2\2\u019d;\3\2\2\2\u019e"+
		"\u019f\7\31\2\2\u019f\u01a0\5R*\2\u01a0=\3\2\2\2\u01a1\u01a2\7\25\2\2"+
		"\u01a2\u01a3\5B\"\2\u01a3?\3\2\2\2\u01a4\u01a5\5R*\2\u01a5A\3\2\2\2\u01a6"+
		"\u01a7\7:\2\2\u01a7C\3\2\2\2\u01a8\u01a9\79\2\2\u01a9E\3\2\2\2\u01aa\u01ab"+
		"\78\2\2\u01abG\3\2\2\2\u01ac\u01ad\7\67\2\2\u01adI\3\2\2\2\u01ae\u01af"+
		"\7\17\2\2\u01afK\3\2\2\2\u01b0\u01b3\5N(\2\u01b1\u01b3\5P)\2\u01b2\u01b0"+
		"\3\2\2\2\u01b2\u01b1\3\2\2\2\u01b3M\3\2\2\2\u01b4\u01b5\7\21\2\2\u01b5"+
		"O\3\2\2\2\u01b6\u01b7\7\n\2\2\u01b7Q\3\2\2\2\u01b8\u01b9\7:\2\2\u01b9"+
		"S\3\2\2\2\u01ba\u01bb\7;\2\2\u01bbU\3\2\2\2!Zc~\u009b\u009d\u00a8\u00ac"+
		"\u00b6\u00be\u00c2\u00d6\u00de\u00e9\u00eb\u0103\u0109\u0111\u0113\u0125"+
		"\u012f\u013a\u0146\u0150\u0159\u0165\u016a\u016e\u0179\u0190\u0197\u01b2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}