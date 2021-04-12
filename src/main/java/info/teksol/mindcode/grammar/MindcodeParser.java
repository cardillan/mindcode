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
		ALLOCATE=1, BREAK=2, CASE=3, CONTINUE=4, DEF=5, ELSE=6, ELSIF=7, END=8, 
		FALSE=9, FOR=10, HEAP=11, IF=12, IN=13, NULL=14, STACK=15, TRUE=16, WHEN=17, 
		WHILE=18, ASSIGN=19, AT=20, COLON=21, COMMA=22, DIV=23, IDIV=24, DOLLAR=25, 
		DOT=26, EXP=27, MINUS=28, MOD=29, MUL=30, NOT=31, PLUS=32, QUESTION_MARK=33, 
		SEMICOLON=34, DIV_ASSIGN=35, EXP_ASSIGN=36, MINUS_ASSIGN=37, MUL_ASSIGN=38, 
		PLUS_ASSIGN=39, LESS_THAN=40, LESS_THAN_EQUAL=41, NOT_EQUAL=42, EQUAL=43, 
		STRICT_EQUAL=44, GREATER_THAN_EQUAL=45, GREATER_THAN=46, AND=47, OR=48, 
		SHIFT_LEFT=49, SHIFT_RIGHT=50, BITWISE_AND=51, BITWISE_OR=52, BITWISE_XOR=53, 
		LEFT_SBRACKET=54, RIGHT_SBRACKET=55, LEFT_RBRACKET=56, RIGHT_RBRACKET=57, 
		LEFT_CBRACKET=58, RIGHT_CBRACKET=59, LITERAL=60, FLOAT=61, INT=62, ID=63, 
		SL_COMMENT=64, WS=65;
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
		RULE_bool_t = 37, RULE_true_t = 38, RULE_false_t = 39, RULE_id = 40;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expression_list", "expression", "propaccess", "numeric_t", 
			"alloc", "alloc_list", "alloc_range", "fundecl", "arg_decl_list", "while_expression", 
			"for_expression", "loop_body", "continue_st", "break_st", "range", "init_list", 
			"incr_list", "funcall", "arg_list", "arg", "if_expr", "if_trailer", "case_expr", 
			"alternative_list", "alternative", "assign", "lvalue", "heap_ref", "global_ref", 
			"unit_ref", "var_ref", "ref", "int_t", "float_t", "literal_t", "null_t", 
			"bool_t", "true_t", "false_t", "id"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'", 
			"'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", 
			"'stack'", "'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", 
			"'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, "'+'", 
			"'?'", "';'", "'/='", "'**='", "'-='", "'*='", "'+='", "'<'", "'<='", 
			"'!='", "'=='", "'==='", "'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", 
			"'|'", "'^'", "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", 
			"DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", 
			"LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", 
			"BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"LITERAL", "FLOAT", "INT", "ID", "SL_COMMENT", "WS"
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
			setState(86);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALLOCATE:
			case CASE:
			case DEF:
			case END:
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
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				expression_list(0);
				setState(83);
				match(EOF);
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 2);
				{
				setState(85);
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
		public TerminalNode SEMICOLON() { return getToken(MindcodeParser.SEMICOLON, 0); }
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
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(89);
				expression(0);
				}
				break;
			case 2:
				{
				setState(90);
				expression(0);
				setState(91);
				match(SEMICOLON);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(102);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(100);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						_localctx = new Expression_listContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression_list);
						setState(95);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(96);
						expression(0);
						}
						break;
					case 2:
						{
						_localctx = new Expression_listContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression_list);
						setState(97);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(98);
						match(SEMICOLON);
						setState(99);
						expression(0);
						}
						break;
					}
					} 
				}
				setState(104);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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
	public static class Binop_bitwise_opContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode BITWISE_AND() { return getToken(MindcodeParser.BITWISE_AND, 0); }
		public TerminalNode BITWISE_OR() { return getToken(MindcodeParser.BITWISE_OR, 0); }
		public TerminalNode BITWISE_XOR() { return getToken(MindcodeParser.BITWISE_XOR, 0); }
		public Binop_bitwise_opContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_bitwise_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_bitwise_op(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_bitwise_op(this);
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
		public TerminalNode IDIV() { return getToken(MindcodeParser.IDIV, 0); }
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
	public static class Binop_shiftContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode SHIFT_LEFT() { return getToken(MindcodeParser.SHIFT_LEFT, 0); }
		public TerminalNode SHIFT_RIGHT() { return getToken(MindcodeParser.SHIFT_RIGHT, 0); }
		public Binop_shiftContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_shift(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_shift(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_shift(this);
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
			setState(128);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				_localctx = new Property_accessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(106);
				propaccess();
				}
				break;
			case 2:
				{
				_localctx = new Case_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107);
				case_expr();
				}
				break;
			case 3:
				{
				_localctx = new If_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(108);
				if_expr();
				}
				break;
			case 4:
				{
				_localctx = new Function_callContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(109);
				funcall();
				}
				break;
			case 5:
				{
				_localctx = new Function_declarationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(110);
				fundecl();
				}
				break;
			case 6:
				{
				_localctx = new AllocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(111);
				alloc();
				}
				break;
			case 7:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(112);
				assign();
				}
				break;
			case 8:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(113);
				lvalue();
				}
				break;
			case 9:
				{
				_localctx = new While_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(114);
				while_expression();
				}
				break;
			case 10:
				{
				_localctx = new For_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(115);
				for_expression();
				}
				break;
			case 11:
				{
				_localctx = new Not_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116);
				match(NOT);
				setState(117);
				expression(15);
				}
				break;
			case 12:
				{
				_localctx = new Literal_stringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(118);
				literal_t();
				}
				break;
			case 13:
				{
				_localctx = new Literal_numericContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(119);
				numeric_t();
				}
				break;
			case 14:
				{
				_localctx = new Literal_boolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(120);
				bool_t();
				}
				break;
			case 15:
				{
				_localctx = new Unary_minusContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(121);
				match(MINUS);
				setState(122);
				numeric_t();
				}
				break;
			case 16:
				{
				_localctx = new Literal_nullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(123);
				null_t();
				}
				break;
			case 17:
				{
				_localctx = new Parenthesized_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(124);
				match(LEFT_RBRACKET);
				setState(125);
				expression(0);
				setState(126);
				match(RIGHT_RBRACKET);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(165);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(163);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new Ternary_opContext(new ExpressionContext(_parentctx, _parentState));
						((Ternary_opContext)_localctx).cond = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(130);
						if (!(precpred(_ctx, 26))) throw new FailedPredicateException(this, "precpred(_ctx, 26)");
						setState(131);
						match(QUESTION_MARK);
						setState(132);
						((Ternary_opContext)_localctx).true_branch = expression(0);
						setState(133);
						match(COLON);
						setState(134);
						((Ternary_opContext)_localctx).false_branch = expression(27);
						}
						break;
					case 2:
						{
						_localctx = new Binop_expContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_expContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(136);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(137);
						((Binop_expContext)_localctx).op = match(EXP);
						setState(138);
						((Binop_expContext)_localctx).right = expression(17);
						}
						break;
					case 3:
						{
						_localctx = new Binop_mul_div_modContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_mul_div_modContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(139);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(140);
						((Binop_mul_div_modContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DIV) | (1L << IDIV) | (1L << MOD) | (1L << MUL))) != 0)) ) {
							((Binop_mul_div_modContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(141);
						((Binop_mul_div_modContext)_localctx).right = expression(15);
						}
						break;
					case 4:
						{
						_localctx = new Binop_plus_minusContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_plus_minusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(142);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(143);
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
						setState(144);
						((Binop_plus_minusContext)_localctx).right = expression(14);
						}
						break;
					case 5:
						{
						_localctx = new Binop_shiftContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_shiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(145);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(146);
						((Binop_shiftContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==SHIFT_LEFT || _la==SHIFT_RIGHT) ) {
							((Binop_shiftContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(147);
						((Binop_shiftContext)_localctx).right = expression(13);
						}
						break;
					case 6:
						{
						_localctx = new Binop_inequality_comparisonContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_inequality_comparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(148);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(149);
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
						setState(150);
						((Binop_inequality_comparisonContext)_localctx).right = expression(12);
						}
						break;
					case 7:
						{
						_localctx = new Binop_equality_comparisonContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_equality_comparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(151);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(152);
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
						setState(153);
						((Binop_equality_comparisonContext)_localctx).right = expression(11);
						}
						break;
					case 8:
						{
						_localctx = new Binop_bitwise_opContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_bitwise_opContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(154);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(155);
						((Binop_bitwise_opContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BITWISE_AND) | (1L << BITWISE_OR) | (1L << BITWISE_XOR))) != 0)) ) {
							((Binop_bitwise_opContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(156);
						((Binop_bitwise_opContext)_localctx).right = expression(10);
						}
						break;
					case 9:
						{
						_localctx = new Binop_andContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_andContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(157);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(158);
						match(AND);
						setState(159);
						((Binop_andContext)_localctx).right = expression(9);
						}
						break;
					case 10:
						{
						_localctx = new Binop_orContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_orContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(160);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(161);
						match(OR);
						setState(162);
						((Binop_orContext)_localctx).right = expression(8);
						}
						break;
					}
					} 
				}
				setState(167);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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
			setState(176);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(168);
				var_ref();
				setState(169);
				match(DOT);
				setState(170);
				((PropaccessContext)_localctx).prop = id();
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(172);
				unit_ref();
				setState(173);
				match(DOT);
				setState(174);
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
			setState(180);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLOAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(178);
				float_t();
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(179);
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
			setState(182);
			match(ALLOCATE);
			setState(183);
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
			setState(186);
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
			setState(187);
			match(IN);
			setState(188);
			id();
			setState(190);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(189);
				alloc_range();
				}
				break;
			}
			}
			_ctx.stop = _input.LT(-1);
			setState(202);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Alloc_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_alloc_list);
					setState(192);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(193);
					match(COMMA);
					setState(194);
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
					setState(195);
					match(IN);
					setState(196);
					id();
					setState(198);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						setState(197);
						alloc_range();
						}
						break;
					}
					}
					} 
				}
				setState(204);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
			setState(205);
			match(LEFT_SBRACKET);
			setState(206);
			range();
			setState(207);
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
		public IdContext name;
		public Arg_decl_listContext args;
		public Expression_listContext body;
		public TerminalNode DEF() { return getToken(MindcodeParser.DEF, 0); }
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Arg_decl_listContext arg_decl_list() {
			return getRuleContext(Arg_decl_listContext.class,0);
		}
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
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
			setState(222);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				match(DEF);
				setState(210);
				((FundeclContext)_localctx).name = id();
				setState(211);
				match(LEFT_RBRACKET);
				setState(212);
				((FundeclContext)_localctx).args = arg_decl_list(0);
				setState(213);
				match(RIGHT_RBRACKET);
				setState(214);
				((FundeclContext)_localctx).body = expression_list(0);
				setState(215);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(217);
				match(DEF);
				setState(218);
				((FundeclContext)_localctx).name = id();
				setState(219);
				((FundeclContext)_localctx).body = expression_list(0);
				setState(220);
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
			{
			setState(225);
			lvalue();
			}
			_ctx.stop = _input.LT(-1);
			setState(232);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Arg_decl_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_arg_decl_list);
					setState(227);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(228);
					match(COMMA);
					setState(229);
					lvalue();
					}
					} 
				}
				setState(234);
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
			setState(235);
			match(WHILE);
			setState(236);
			((While_expressionContext)_localctx).cond = expression(0);
			setState(237);
			loop_body(0);
			setState(238);
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
			setState(256);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				_localctx = new Ranged_forContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(240);
				match(FOR);
				setState(241);
				lvalue();
				setState(242);
				match(IN);
				setState(243);
				range();
				setState(244);
				loop_body(0);
				setState(245);
				match(END);
				}
				break;
			case 2:
				_localctx = new Iterated_forContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(247);
				match(FOR);
				setState(248);
				((Iterated_forContext)_localctx).init = init_list(0);
				setState(249);
				match(SEMICOLON);
				setState(250);
				((Iterated_forContext)_localctx).cond = expression(0);
				setState(251);
				match(SEMICOLON);
				setState(252);
				((Iterated_forContext)_localctx).increment = incr_list(0);
				setState(253);
				loop_body(0);
				setState(254);
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
			setState(262);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALLOCATE:
			case CASE:
			case DEF:
			case END:
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
				{
				setState(259);
				expression_list(0);
				}
				break;
			case BREAK:
				{
				setState(260);
				break_st();
				}
				break;
			case CONTINUE:
				{
				setState(261);
				continue_st();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(272);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(270);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
					case 1:
						{
						_localctx = new Loop_bodyContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_loop_body);
						setState(264);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(265);
						expression_list(0);
						}
						break;
					case 2:
						{
						_localctx = new Loop_bodyContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_loop_body);
						setState(266);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(267);
						break_st();
						}
						break;
					case 3:
						{
						_localctx = new Loop_bodyContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_loop_body);
						setState(268);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(269);
						continue_st();
						}
						break;
					}
					} 
				}
				setState(274);
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
			setState(275);
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
			setState(277);
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
			setState(290);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				_localctx = new Inclusive_rangeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(279);
				((Inclusive_rangeContext)_localctx).start = int_t();
				setState(280);
				match(DOT);
				setState(281);
				match(DOT);
				setState(282);
				((Inclusive_rangeContext)_localctx).end = int_t();
				}
				break;
			case 2:
				_localctx = new Exclusive_rangeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(284);
				((Exclusive_rangeContext)_localctx).start = int_t();
				setState(285);
				match(DOT);
				setState(286);
				match(DOT);
				setState(287);
				match(DOT);
				setState(288);
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
			setState(293);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(300);
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
					setState(295);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(296);
					match(COMMA);
					setState(297);
					expression(0);
					}
					} 
				}
				setState(302);
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
			setState(304);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(311);
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
					setState(306);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(307);
					match(COMMA);
					setState(308);
					expression(0);
					}
					} 
				}
				setState(313);
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
		public PropaccessContext obj;
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public PropaccessContext propaccess() {
			return getRuleContext(PropaccessContext.class,0);
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
			setState(331);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(314);
				match(END);
				setState(315);
				match(LEFT_RBRACKET);
				setState(316);
				match(RIGHT_RBRACKET);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(317);
				((FuncallContext)_localctx).name = id();
				setState(318);
				match(LEFT_RBRACKET);
				setState(319);
				match(RIGHT_RBRACKET);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(321);
				((FuncallContext)_localctx).name = id();
				setState(322);
				match(LEFT_RBRACKET);
				setState(323);
				((FuncallContext)_localctx).params = arg_list(0);
				setState(324);
				match(RIGHT_RBRACKET);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(326);
				((FuncallContext)_localctx).obj = propaccess();
				setState(327);
				match(LEFT_RBRACKET);
				setState(328);
				((FuncallContext)_localctx).params = arg_list(0);
				setState(329);
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
			setState(334);
			arg();
			}
			_ctx.stop = _input.LT(-1);
			setState(341);
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
					setState(336);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(337);
					match(COMMA);
					setState(338);
					arg();
					}
					} 
				}
				setState(343);
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
			setState(344);
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
		public If_trailerContext if_trailer() {
			return getRuleContext(If_trailerContext.class,0);
		}
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
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
			setState(346);
			match(IF);
			setState(347);
			((If_exprContext)_localctx).cond = expression(0);
			setState(349);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(348);
				((If_exprContext)_localctx).true_branch = expression_list(0);
				}
				break;
			}
			setState(352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE || _la==ELSIF) {
				{
				setState(351);
				if_trailer();
				}
			}

			setState(354);
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
		public TerminalNode ELSIF() { return getToken(MindcodeParser.ELSIF, 0); }
		public If_trailerContext if_trailer() {
			return getRuleContext(If_trailerContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IF() { return getToken(MindcodeParser.IF, 0); }
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
		int _la;
		try {
			setState(375);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(356);
				match(ELSE);
				setState(358);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(357);
					((If_trailerContext)_localctx).false_branch = expression_list(0);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(360);
				match(ELSIF);
				setState(361);
				((If_trailerContext)_localctx).cond = expression(0);
				setState(363);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ALLOCATE) | (1L << CASE) | (1L << DEF) | (1L << END) | (1L << FALSE) | (1L << FOR) | (1L << IF) | (1L << NULL) | (1L << TRUE) | (1L << WHILE) | (1L << AT) | (1L << DOLLAR) | (1L << MINUS) | (1L << NOT) | (1L << LEFT_RBRACKET) | (1L << LITERAL) | (1L << FLOAT) | (1L << INT) | (1L << ID))) != 0)) {
					{
					setState(362);
					((If_trailerContext)_localctx).true_branch = expression_list(0);
					}
				}

				setState(365);
				if_trailer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(367);
				match(ELSE);
				setState(368);
				match(IF);
				setState(369);
				((If_trailerContext)_localctx).cond = expression(0);
				setState(371);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ALLOCATE) | (1L << CASE) | (1L << DEF) | (1L << END) | (1L << FALSE) | (1L << FOR) | (1L << IF) | (1L << NULL) | (1L << TRUE) | (1L << WHILE) | (1L << AT) | (1L << DOLLAR) | (1L << MINUS) | (1L << NOT) | (1L << LEFT_RBRACKET) | (1L << LITERAL) | (1L << FLOAT) | (1L << INT) | (1L << ID))) != 0)) {
					{
					setState(370);
					((If_trailerContext)_localctx).true_branch = expression_list(0);
					}
				}

				setState(373);
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
			setState(377);
			match(CASE);
			setState(378);
			((Case_exprContext)_localctx).cond = expression(0);
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHEN) {
				{
				setState(379);
				alternative_list(0);
				}
			}

			setState(384);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(382);
				match(ELSE);
				setState(383);
				((Case_exprContext)_localctx).else_branch = expression_list(0);
				}
			}

			setState(386);
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
			setState(389);
			alternative();
			}
			_ctx.stop = _input.LT(-1);
			setState(395);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Alternative_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_alternative_list);
					setState(391);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(392);
					alternative();
					}
					} 
				}
				setState(397);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
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
			setState(398);
			match(WHEN);
			setState(399);
			((AlternativeContext)_localctx).value = expression(0);
			setState(401);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(400);
				((AlternativeContext)_localctx).body = expression_list(0);
				}
				break;
			}
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
			setState(419);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				_localctx = new Simple_assignContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(403);
				((Simple_assignContext)_localctx).target = lvalue();
				setState(404);
				match(ASSIGN);
				setState(405);
				((Simple_assignContext)_localctx).value = expression(0);
				}
				break;
			case 2:
				_localctx = new Exp_assignContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(407);
				((Exp_assignContext)_localctx).target = lvalue();
				setState(408);
				match(EXP_ASSIGN);
				setState(409);
				((Exp_assignContext)_localctx).value = expression(0);
				}
				break;
			case 3:
				_localctx = new Binop_mul_div_assignContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(411);
				((Binop_mul_div_assignContext)_localctx).target = lvalue();
				setState(412);
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
				setState(413);
				((Binop_mul_div_assignContext)_localctx).value = expression(0);
				}
				break;
			case 4:
				_localctx = new Binop_plus_minus_assignContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(415);
				((Binop_plus_minus_assignContext)_localctx).target = lvalue();
				setState(416);
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
				setState(417);
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
			setState(426);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(421);
				unit_ref();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(422);
				global_ref();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(423);
				heap_ref();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(424);
				var_ref();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(425);
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
			setState(428);
			((Heap_refContext)_localctx).name = id();
			setState(429);
			match(LEFT_SBRACKET);
			setState(430);
			((Heap_refContext)_localctx).address = expression(0);
			setState(431);
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
			setState(433);
			match(DOLLAR);
			setState(434);
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
			setState(436);
			match(AT);
			setState(437);
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
			setState(439);
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
			setState(441);
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
			setState(443);
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
			setState(445);
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
			setState(447);
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
			setState(449);
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
			setState(453);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
				_localctx = new True_bool_literalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(451);
				true_t();
				}
				break;
			case FALSE:
				_localctx = new False_bool_literalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(452);
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
			setState(455);
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
			setState(457);
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
			setState(459);
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
			return precpred(_ctx, 2);
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 26);
		case 3:
			return precpred(_ctx, 16);
		case 4:
			return precpred(_ctx, 14);
		case 5:
			return precpred(_ctx, 13);
		case 6:
			return precpred(_ctx, 12);
		case 7:
			return precpred(_ctx, 11);
		case 8:
			return precpred(_ctx, 10);
		case 9:
			return precpred(_ctx, 9);
		case 10:
			return precpred(_ctx, 8);
		case 11:
			return precpred(_ctx, 7);
		}
		return true;
	}
	private boolean alloc_list_sempred(Alloc_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arg_decl_list_sempred(Arg_decl_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 13:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean loop_body_sempred(Loop_bodyContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return precpred(_ctx, 6);
		case 15:
			return precpred(_ctx, 5);
		case 16:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean init_list_sempred(Init_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 17:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean incr_list_sempred(Incr_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 18:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean arg_list_sempred(Arg_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 19:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean alternative_list_sempred(Alternative_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 20:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3C\u01d0\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\3\2\3\2"+
		"\3\2\3\2\5\2Y\n\2\3\3\3\3\3\3\3\3\3\3\5\3`\n\3\3\3\3\3\3\3\3\3\3\3\7\3"+
		"g\n\3\f\3\16\3j\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u0083\n\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4\u00a6\n\4\f\4\16"+
		"\4\u00a9\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u00b3\n\5\3\6\3\6\5"+
		"\6\u00b7\n\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\5\b\u00c1\n\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\5\b\u00c9\n\b\7\b\u00cb\n\b\f\b\16\b\u00ce\13\b\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00e1\n"+
		"\n\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00e9\n\13\f\13\16\13\u00ec\13\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\5\r\u0103\n\r\3\16\3\16\3\16\3\16\5\16\u0109\n\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\7\16\u0111\n\16\f\16\16\16\u0114\13\16\3\17"+
		"\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\5\21\u0125\n\21\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u012d\n\22\f\22\16"+
		"\22\u0130\13\22\3\23\3\23\3\23\3\23\3\23\3\23\7\23\u0138\n\23\f\23\16"+
		"\23\u013b\13\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u014e\n\24\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\7\25\u0156\n\25\f\25\16\25\u0159\13\25\3\26\3\26\3\27\3\27\3\27"+
		"\5\27\u0160\n\27\3\27\5\27\u0163\n\27\3\27\3\27\3\30\3\30\5\30\u0169\n"+
		"\30\3\30\3\30\3\30\5\30\u016e\n\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30"+
		"\u0176\n\30\3\30\3\30\5\30\u017a\n\30\3\31\3\31\3\31\5\31\u017f\n\31\3"+
		"\31\3\31\5\31\u0183\n\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\7\32\u018c"+
		"\n\32\f\32\16\32\u018f\13\32\3\33\3\33\3\33\5\33\u0194\n\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\5\34\u01a6\n\34\3\35\3\35\3\35\3\35\3\35\5\35\u01ad\n\35\3\36\3\36\3"+
		"\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%"+
		"\3&\3&\3\'\3\'\5\'\u01c8\n\'\3(\3(\3)\3)\3*\3*\3*\2\13\4\6\16\24\32\""+
		"$(\62+\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<"+
		">@BDFHJLNPR\2\13\4\2\31\32\37 \4\2\36\36\"\"\3\2\63\64\4\2*+/\60\3\2,"+
		".\3\2\65\67\4\2\r\r\21\21\4\2%%((\4\2\'\'))\2\u01eb\2X\3\2\2\2\4_\3\2"+
		"\2\2\6\u0082\3\2\2\2\b\u00b2\3\2\2\2\n\u00b6\3\2\2\2\f\u00b8\3\2\2\2\16"+
		"\u00bb\3\2\2\2\20\u00cf\3\2\2\2\22\u00e0\3\2\2\2\24\u00e2\3\2\2\2\26\u00ed"+
		"\3\2\2\2\30\u0102\3\2\2\2\32\u0108\3\2\2\2\34\u0115\3\2\2\2\36\u0117\3"+
		"\2\2\2 \u0124\3\2\2\2\"\u0126\3\2\2\2$\u0131\3\2\2\2&\u014d\3\2\2\2(\u014f"+
		"\3\2\2\2*\u015a\3\2\2\2,\u015c\3\2\2\2.\u0179\3\2\2\2\60\u017b\3\2\2\2"+
		"\62\u0186\3\2\2\2\64\u0190\3\2\2\2\66\u01a5\3\2\2\28\u01ac\3\2\2\2:\u01ae"+
		"\3\2\2\2<\u01b3\3\2\2\2>\u01b6\3\2\2\2@\u01b9\3\2\2\2B\u01bb\3\2\2\2D"+
		"\u01bd\3\2\2\2F\u01bf\3\2\2\2H\u01c1\3\2\2\2J\u01c3\3\2\2\2L\u01c7\3\2"+
		"\2\2N\u01c9\3\2\2\2P\u01cb\3\2\2\2R\u01cd\3\2\2\2TU\5\4\3\2UV\7\2\2\3"+
		"VY\3\2\2\2WY\7\2\2\3XT\3\2\2\2XW\3\2\2\2Y\3\3\2\2\2Z[\b\3\1\2[`\5\6\4"+
		"\2\\]\5\6\4\2]^\7$\2\2^`\3\2\2\2_Z\3\2\2\2_\\\3\2\2\2`h\3\2\2\2ab\f\4"+
		"\2\2bg\5\6\4\2cd\f\3\2\2de\7$\2\2eg\5\6\4\2fa\3\2\2\2fc\3\2\2\2gj\3\2"+
		"\2\2hf\3\2\2\2hi\3\2\2\2i\5\3\2\2\2jh\3\2\2\2kl\b\4\1\2l\u0083\5\b\5\2"+
		"m\u0083\5\60\31\2n\u0083\5,\27\2o\u0083\5&\24\2p\u0083\5\22\n\2q\u0083"+
		"\5\f\7\2r\u0083\5\66\34\2s\u0083\58\35\2t\u0083\5\26\f\2u\u0083\5\30\r"+
		"\2vw\7!\2\2w\u0083\5\6\4\21x\u0083\5H%\2y\u0083\5\n\6\2z\u0083\5L\'\2"+
		"{|\7\36\2\2|\u0083\5\n\6\2}\u0083\5J&\2~\177\7:\2\2\177\u0080\5\6\4\2"+
		"\u0080\u0081\7;\2\2\u0081\u0083\3\2\2\2\u0082k\3\2\2\2\u0082m\3\2\2\2"+
		"\u0082n\3\2\2\2\u0082o\3\2\2\2\u0082p\3\2\2\2\u0082q\3\2\2\2\u0082r\3"+
		"\2\2\2\u0082s\3\2\2\2\u0082t\3\2\2\2\u0082u\3\2\2\2\u0082v\3\2\2\2\u0082"+
		"x\3\2\2\2\u0082y\3\2\2\2\u0082z\3\2\2\2\u0082{\3\2\2\2\u0082}\3\2\2\2"+
		"\u0082~\3\2\2\2\u0083\u00a7\3\2\2\2\u0084\u0085\f\34\2\2\u0085\u0086\7"+
		"#\2\2\u0086\u0087\5\6\4\2\u0087\u0088\7\27\2\2\u0088\u0089\5\6\4\35\u0089"+
		"\u00a6\3\2\2\2\u008a\u008b\f\22\2\2\u008b\u008c\7\35\2\2\u008c\u00a6\5"+
		"\6\4\23\u008d\u008e\f\20\2\2\u008e\u008f\t\2\2\2\u008f\u00a6\5\6\4\21"+
		"\u0090\u0091\f\17\2\2\u0091\u0092\t\3\2\2\u0092\u00a6\5\6\4\20\u0093\u0094"+
		"\f\16\2\2\u0094\u0095\t\4\2\2\u0095\u00a6\5\6\4\17\u0096\u0097\f\r\2\2"+
		"\u0097\u0098\t\5\2\2\u0098\u00a6\5\6\4\16\u0099\u009a\f\f\2\2\u009a\u009b"+
		"\t\6\2\2\u009b\u00a6\5\6\4\r\u009c\u009d\f\13\2\2\u009d\u009e\t\7\2\2"+
		"\u009e\u00a6\5\6\4\f\u009f\u00a0\f\n\2\2\u00a0\u00a1\7\61\2\2\u00a1\u00a6"+
		"\5\6\4\13\u00a2\u00a3\f\t\2\2\u00a3\u00a4\7\62\2\2\u00a4\u00a6\5\6\4\n"+
		"\u00a5\u0084\3\2\2\2\u00a5\u008a\3\2\2\2\u00a5\u008d\3\2\2\2\u00a5\u0090"+
		"\3\2\2\2\u00a5\u0093\3\2\2\2\u00a5\u0096\3\2\2\2\u00a5\u0099\3\2\2\2\u00a5"+
		"\u009c\3\2\2\2\u00a5\u009f\3\2\2\2\u00a5\u00a2\3\2\2\2\u00a6\u00a9\3\2"+
		"\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\7\3\2\2\2\u00a9\u00a7"+
		"\3\2\2\2\u00aa\u00ab\5@!\2\u00ab\u00ac\7\34\2\2\u00ac\u00ad\5R*\2\u00ad"+
		"\u00b3\3\2\2\2\u00ae\u00af\5> \2\u00af\u00b0\7\34\2\2\u00b0\u00b1\5R*"+
		"\2\u00b1\u00b3\3\2\2\2\u00b2\u00aa\3\2\2\2\u00b2\u00ae\3\2\2\2\u00b3\t"+
		"\3\2\2\2\u00b4\u00b7\5F$\2\u00b5\u00b7\5D#\2\u00b6\u00b4\3\2\2\2\u00b6"+
		"\u00b5\3\2\2\2\u00b7\13\3\2\2\2\u00b8\u00b9\7\3\2\2\u00b9\u00ba\5\16\b"+
		"\2\u00ba\r\3\2\2\2\u00bb\u00bc\b\b\1\2\u00bc\u00bd\t\b\2\2\u00bd\u00be"+
		"\7\17\2\2\u00be\u00c0\5R*\2\u00bf\u00c1\5\20\t\2\u00c0\u00bf\3\2\2\2\u00c0"+
		"\u00c1\3\2\2\2\u00c1\u00cc\3\2\2\2\u00c2\u00c3\f\3\2\2\u00c3\u00c4\7\30"+
		"\2\2\u00c4\u00c5\t\b\2\2\u00c5\u00c6\7\17\2\2\u00c6\u00c8\5R*\2\u00c7"+
		"\u00c9\5\20\t\2\u00c8\u00c7\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cb\3"+
		"\2\2\2\u00ca\u00c2\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc"+
		"\u00cd\3\2\2\2\u00cd\17\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d0\78\2\2"+
		"\u00d0\u00d1\5 \21\2\u00d1\u00d2\79\2\2\u00d2\21\3\2\2\2\u00d3\u00d4\7"+
		"\7\2\2\u00d4\u00d5\5R*\2\u00d5\u00d6\7:\2\2\u00d6\u00d7\5\24\13\2\u00d7"+
		"\u00d8\7;\2\2\u00d8\u00d9\5\4\3\2\u00d9\u00da\7\n\2\2\u00da\u00e1\3\2"+
		"\2\2\u00db\u00dc\7\7\2\2\u00dc\u00dd\5R*\2\u00dd\u00de\5\4\3\2\u00de\u00df"+
		"\7\n\2\2\u00df\u00e1\3\2\2\2\u00e0\u00d3\3\2\2\2\u00e0\u00db\3\2\2\2\u00e1"+
		"\23\3\2\2\2\u00e2\u00e3\b\13\1\2\u00e3\u00e4\58\35\2\u00e4\u00ea\3\2\2"+
		"\2\u00e5\u00e6\f\3\2\2\u00e6\u00e7\7\30\2\2\u00e7\u00e9\58\35\2\u00e8"+
		"\u00e5\3\2\2\2\u00e9\u00ec\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2"+
		"\2\2\u00eb\25\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ed\u00ee\7\24\2\2\u00ee\u00ef"+
		"\5\6\4\2\u00ef\u00f0\5\32\16\2\u00f0\u00f1\7\n\2\2\u00f1\27\3\2\2\2\u00f2"+
		"\u00f3\7\f\2\2\u00f3\u00f4\58\35\2\u00f4\u00f5\7\17\2\2\u00f5\u00f6\5"+
		" \21\2\u00f6\u00f7\5\32\16\2\u00f7\u00f8\7\n\2\2\u00f8\u0103\3\2\2\2\u00f9"+
		"\u00fa\7\f\2\2\u00fa\u00fb\5\"\22\2\u00fb\u00fc\7$\2\2\u00fc\u00fd\5\6"+
		"\4\2\u00fd\u00fe\7$\2\2\u00fe\u00ff\5$\23\2\u00ff\u0100\5\32\16\2\u0100"+
		"\u0101\7\n\2\2\u0101\u0103\3\2\2\2\u0102\u00f2\3\2\2\2\u0102\u00f9\3\2"+
		"\2\2\u0103\31\3\2\2\2\u0104\u0105\b\16\1\2\u0105\u0109\5\4\3\2\u0106\u0109"+
		"\5\36\20\2\u0107\u0109\5\34\17\2\u0108\u0104\3\2\2\2\u0108\u0106\3\2\2"+
		"\2\u0108\u0107\3\2\2\2\u0109\u0112\3\2\2\2\u010a\u010b\f\b\2\2\u010b\u0111"+
		"\5\4\3\2\u010c\u010d\f\7\2\2\u010d\u0111\5\36\20\2\u010e\u010f\f\6\2\2"+
		"\u010f\u0111\5\34\17\2\u0110\u010a\3\2\2\2\u0110\u010c\3\2\2\2\u0110\u010e"+
		"\3\2\2\2\u0111\u0114\3\2\2\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113"+
		"\33\3\2\2\2\u0114\u0112\3\2\2\2\u0115\u0116\7\6\2\2\u0116\35\3\2\2\2\u0117"+
		"\u0118\7\4\2\2\u0118\37\3\2\2\2\u0119\u011a\5D#\2\u011a\u011b\7\34\2\2"+
		"\u011b\u011c\7\34\2\2\u011c\u011d\5D#\2\u011d\u0125\3\2\2\2\u011e\u011f"+
		"\5D#\2\u011f\u0120\7\34\2\2\u0120\u0121\7\34\2\2\u0121\u0122\7\34\2\2"+
		"\u0122\u0123\5D#\2\u0123\u0125\3\2\2\2\u0124\u0119\3\2\2\2\u0124\u011e"+
		"\3\2\2\2\u0125!\3\2\2\2\u0126\u0127\b\22\1\2\u0127\u0128\5\6\4\2\u0128"+
		"\u012e\3\2\2\2\u0129\u012a\f\3\2\2\u012a\u012b\7\30\2\2\u012b\u012d\5"+
		"\6\4\2\u012c\u0129\3\2\2\2\u012d\u0130\3\2\2\2\u012e\u012c\3\2\2\2\u012e"+
		"\u012f\3\2\2\2\u012f#\3\2\2\2\u0130\u012e\3\2\2\2\u0131\u0132\b\23\1\2"+
		"\u0132\u0133\5\6\4\2\u0133\u0139\3\2\2\2\u0134\u0135\f\3\2\2\u0135\u0136"+
		"\7\30\2\2\u0136\u0138\5\6\4\2\u0137\u0134\3\2\2\2\u0138\u013b\3\2\2\2"+
		"\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a%\3\2\2\2\u013b\u0139\3"+
		"\2\2\2\u013c\u013d\7\n\2\2\u013d\u013e\7:\2\2\u013e\u014e\7;\2\2\u013f"+
		"\u0140\5R*\2\u0140\u0141\7:\2\2\u0141\u0142\7;\2\2\u0142\u014e\3\2\2\2"+
		"\u0143\u0144\5R*\2\u0144\u0145\7:\2\2\u0145\u0146\5(\25\2\u0146\u0147"+
		"\7;\2\2\u0147\u014e\3\2\2\2\u0148\u0149\5\b\5\2\u0149\u014a\7:\2\2\u014a"+
		"\u014b\5(\25\2\u014b\u014c\7;\2\2\u014c\u014e\3\2\2\2\u014d\u013c\3\2"+
		"\2\2\u014d\u013f\3\2\2\2\u014d\u0143\3\2\2\2\u014d\u0148\3\2\2\2\u014e"+
		"\'\3\2\2\2\u014f\u0150\b\25\1\2\u0150\u0151\5*\26\2\u0151\u0157\3\2\2"+
		"\2\u0152\u0153\f\3\2\2\u0153\u0154\7\30\2\2\u0154\u0156\5*\26\2\u0155"+
		"\u0152\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2"+
		"\2\2\u0158)\3\2\2\2\u0159\u0157\3\2\2\2\u015a\u015b\5\6\4\2\u015b+\3\2"+
		"\2\2\u015c\u015d\7\16\2\2\u015d\u015f\5\6\4\2\u015e\u0160\5\4\3\2\u015f"+
		"\u015e\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0162\3\2\2\2\u0161\u0163\5."+
		"\30\2\u0162\u0161\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0164\3\2\2\2\u0164"+
		"\u0165\7\n\2\2\u0165-\3\2\2\2\u0166\u0168\7\b\2\2\u0167\u0169\5\4\3\2"+
		"\u0168\u0167\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u017a\3\2\2\2\u016a\u016b"+
		"\7\t\2\2\u016b\u016d\5\6\4\2\u016c\u016e\5\4\3\2\u016d\u016c\3\2\2\2\u016d"+
		"\u016e\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0170\5.\30\2\u0170\u017a\3\2"+
		"\2\2\u0171\u0172\7\b\2\2\u0172\u0173\7\16\2\2\u0173\u0175\5\6\4\2\u0174"+
		"\u0176\5\4\3\2\u0175\u0174\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\3\2"+
		"\2\2\u0177\u0178\5.\30\2\u0178\u017a\3\2\2\2\u0179\u0166\3\2\2\2\u0179"+
		"\u016a\3\2\2\2\u0179\u0171\3\2\2\2\u017a/\3\2\2\2\u017b\u017c\7\5\2\2"+
		"\u017c\u017e\5\6\4\2\u017d\u017f\5\62\32\2\u017e\u017d\3\2\2\2\u017e\u017f"+
		"\3\2\2\2\u017f\u0182\3\2\2\2\u0180\u0181\7\b\2\2\u0181\u0183\5\4\3\2\u0182"+
		"\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0185\7\n"+
		"\2\2\u0185\61\3\2\2\2\u0186\u0187\b\32\1\2\u0187\u0188\5\64\33\2\u0188"+
		"\u018d\3\2\2\2\u0189\u018a\f\3\2\2\u018a\u018c\5\64\33\2\u018b\u0189\3"+
		"\2\2\2\u018c\u018f\3\2\2\2\u018d\u018b\3\2\2\2\u018d\u018e\3\2\2\2\u018e"+
		"\63\3\2\2\2\u018f\u018d\3\2\2\2\u0190\u0191\7\23\2\2\u0191\u0193\5\6\4"+
		"\2\u0192\u0194\5\4\3\2\u0193\u0192\3\2\2\2\u0193\u0194\3\2\2\2\u0194\65"+
		"\3\2\2\2\u0195\u0196\58\35\2\u0196\u0197\7\25\2\2\u0197\u0198\5\6\4\2"+
		"\u0198\u01a6\3\2\2\2\u0199\u019a\58\35\2\u019a\u019b\7&\2\2\u019b\u019c"+
		"\5\6\4\2\u019c\u01a6\3\2\2\2\u019d\u019e\58\35\2\u019e\u019f\t\t\2\2\u019f"+
		"\u01a0\5\6\4\2\u01a0\u01a6\3\2\2\2\u01a1\u01a2\58\35\2\u01a2\u01a3\t\n"+
		"\2\2\u01a3\u01a4\5\6\4\2\u01a4\u01a6\3\2\2\2\u01a5\u0195\3\2\2\2\u01a5"+
		"\u0199\3\2\2\2\u01a5\u019d\3\2\2\2\u01a5\u01a1\3\2\2\2\u01a6\67\3\2\2"+
		"\2\u01a7\u01ad\5> \2\u01a8\u01ad\5<\37\2\u01a9\u01ad\5:\36\2\u01aa\u01ad"+
		"\5@!\2\u01ab\u01ad\5\b\5\2\u01ac\u01a7\3\2\2\2\u01ac\u01a8\3\2\2\2\u01ac"+
		"\u01a9\3\2\2\2\u01ac\u01aa\3\2\2\2\u01ac\u01ab\3\2\2\2\u01ad9\3\2\2\2"+
		"\u01ae\u01af\5R*\2\u01af\u01b0\78\2\2\u01b0\u01b1\5\6\4\2\u01b1\u01b2"+
		"\79\2\2\u01b2;\3\2\2\2\u01b3\u01b4\7\33\2\2\u01b4\u01b5\5R*\2\u01b5=\3"+
		"\2\2\2\u01b6\u01b7\7\26\2\2\u01b7\u01b8\5B\"\2\u01b8?\3\2\2\2\u01b9\u01ba"+
		"\5R*\2\u01baA\3\2\2\2\u01bb\u01bc\7A\2\2\u01bcC\3\2\2\2\u01bd\u01be\7"+
		"@\2\2\u01beE\3\2\2\2\u01bf\u01c0\7?\2\2\u01c0G\3\2\2\2\u01c1\u01c2\7>"+
		"\2\2\u01c2I\3\2\2\2\u01c3\u01c4\7\20\2\2\u01c4K\3\2\2\2\u01c5\u01c8\5"+
		"N(\2\u01c6\u01c8\5P)\2\u01c7\u01c5\3\2\2\2\u01c7\u01c6\3\2\2\2\u01c8M"+
		"\3\2\2\2\u01c9\u01ca\7\22\2\2\u01caO\3\2\2\2\u01cb\u01cc\7\13\2\2\u01cc"+
		"Q\3\2\2\2\u01cd\u01ce\7A\2\2\u01ceS\3\2\2\2&X_fh\u0082\u00a5\u00a7\u00b2"+
		"\u00b6\u00c0\u00c8\u00cc\u00e0\u00ea\u0102\u0108\u0110\u0112\u0124\u012e"+
		"\u0139\u014d\u0157\u015f\u0162\u0168\u016d\u0175\u0179\u017e\u0182\u018d"+
		"\u0193\u01a5\u01ac\u01c7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}