// Generated from Mindcode.g4 by ANTLR 4.13.1
package info.teksol.mindcode.grammar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class MindcodeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ALLOCATE=1, BEGIN=2, BREAK=3, CASE=4, CONST=5, CONTINUE=6, DEF=7, DO=8, 
		ELIF=9, ELSE=10, ELSEIF=11, ELSIF=12, END=13, FALSE=14, FOR=15, HEAP=16, 
		IF=17, IN=18, INLINE=19, LOOP=20, NOINLINE=21, NULL=22, OUT=23, PARAM=24, 
		RETURN=25, REQUIRE=26, SENSOR=27, STACK=28, THEN=29, TRUE=30, VAR=31, 
		VOID=32, WHEN=33, WHILE=34, ASSIGN=35, AT=36, COLON=37, COMMA=38, DIV=39, 
		IDIV=40, DOLLAR=41, DOT=42, EXP=43, MINUS=44, MOD=45, MUL=46, NOT=47, 
		BITWISE_NOT=48, PLUS=49, QUESTION_MARK=50, SEMICOLON=51, HASHSET=52, HASHSTRICT=53, 
		HASHRELAXED=54, EXP_ASSIGN=55, MUL_ASSIGN=56, DIV_ASSIGN=57, IDIV_ASSIGN=58, 
		MOD_ASSIGN=59, PLUS_ASSIGN=60, MINUS_ASSIGN=61, SHIFT_LEFT_ASSIGN=62, 
		SHIFT_RIGHT_ASSIGN=63, BITWISE_AND_ASSIGN=64, BITWISE_OR_ASSIGN=65, BITWISE_XOR_ASSIGN=66, 
		AND_ASSIGN=67, OR_ASSIGN=68, LESS_THAN=69, LESS_THAN_EQUAL=70, NOT_EQUAL=71, 
		EQUAL=72, STRICT_EQUAL=73, STRICT_NOT_EQUAL=74, GREATER_THAN_EQUAL=75, 
		GREATER_THAN=76, AND=77, OR=78, SHIFT_LEFT=79, SHIFT_RIGHT=80, BITWISE_AND=81, 
		BITWISE_OR=82, BITWISE_XOR=83, LEFT_SBRACKET=84, RIGHT_SBRACKET=85, LEFT_RBRACKET=86, 
		RIGHT_RBRACKET=87, LEFT_CBRACKET=88, RIGHT_CBRACKET=89, FORMATTABLE=90, 
		LITERAL=91, FLOAT=92, INT=93, HEXINT=94, BININT=95, ID=96, REM_COMMENT=97, 
		COMMENT=98, SL_COMMENT=99, WS=100;
	public static final int
		RULE_program = 0, RULE_expression_list = 1, RULE_expression_or_rem_comment = 2, 
		RULE_exp_strict_or_relaxed = 3, RULE_expression_strict = 4, RULE_expression_relaxed = 5, 
		RULE_optional_do = 6, RULE_optional_then = 7, RULE_expression = 8, RULE_directive = 9, 
		RULE_directive_list = 10, RULE_require = 11, RULE_indirectpropaccess = 12, 
		RULE_methodaccess = 13, RULE_propaccess = 14, RULE_laccess = 15, RULE_numeric_t = 16, 
		RULE_alloc = 17, RULE_alloc_list = 18, RULE_alloc_range = 19, RULE_const_decl = 20, 
		RULE_param_decl = 21, RULE_fundecl = 22, RULE_arg_decl = 23, RULE_arg_decl_list = 24, 
		RULE_while_expression = 25, RULE_do_while_expression = 26, RULE_for_expression = 27, 
		RULE_iterator = 28, RULE_iterator_list = 29, RULE_value_list = 30, RULE_loop_body = 31, 
		RULE_continue_st = 32, RULE_break_st = 33, RULE_return_st = 34, RULE_range_expression = 35, 
		RULE_init_list = 36, RULE_incr_list = 37, RULE_funcall = 38, RULE_arg = 39, 
		RULE_arg_list = 40, RULE_if_expr = 41, RULE_if_trailer = 42, RULE_case_expr = 43, 
		RULE_alternative_list = 44, RULE_alternative = 45, RULE_when_expression = 46, 
		RULE_when_value_list = 47, RULE_assign = 48, RULE_lvalue = 49, RULE_loop_label = 50, 
		RULE_heap_ref = 51, RULE_global_ref = 52, RULE_unit_ref = 53, RULE_var_ref = 54, 
		RULE_ref = 55, RULE_int_t = 56, RULE_float_t = 57, RULE_literal_t = 58, 
		RULE_formattable_t = 59, RULE_null_t = 60, RULE_bool_t = 61, RULE_true_t = 62, 
		RULE_false_t = 63, RULE_id = 64, RULE_decimal_int = 65, RULE_hex_int = 66, 
		RULE_binary_int = 67;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expression_list", "expression_or_rem_comment", "exp_strict_or_relaxed", 
			"expression_strict", "expression_relaxed", "optional_do", "optional_then", 
			"expression", "directive", "directive_list", "require", "indirectpropaccess", 
			"methodaccess", "propaccess", "laccess", "numeric_t", "alloc", "alloc_list", 
			"alloc_range", "const_decl", "param_decl", "fundecl", "arg_decl", "arg_decl_list", 
			"while_expression", "do_while_expression", "for_expression", "iterator", 
			"iterator_list", "value_list", "loop_body", "continue_st", "break_st", 
			"return_st", "range_expression", "init_list", "incr_list", "funcall", 
			"arg", "arg_list", "if_expr", "if_trailer", "case_expr", "alternative_list", 
			"alternative", "when_expression", "when_value_list", "assign", "lvalue", 
			"loop_label", "heap_ref", "global_ref", "unit_ref", "var_ref", "ref", 
			"int_t", "float_t", "literal_t", "formattable_t", "null_t", "bool_t", 
			"true_t", "false_t", "id", "decimal_int", "hex_int", "binary_int"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'begin'", "'break'", "'case'", "'const'", "'continue'", 
			"'def'", "'do'", "'elif'", "'else'", "'elseif'", "'elsif'", "'end'", 
			"'false'", "'for'", "'heap'", "'if'", "'in'", "'inline'", "'loop'", "'noinline'", 
			"'null'", "'out'", "'param'", "'return'", "'require'", "'sensor'", "'stack'", 
			"'then'", "'true'", "'var'", "'void'", "'when'", "'while'", "'='", "'@'", 
			"':'", "','", "'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", 
			null, "'~'", "'+'", "'?'", "';'", "'#set'", "'#strict'", "'#relaxed'", 
			"'**='", "'*='", "'/='", "'\\='", "'%='", "'+='", "'-='", "'<<='", "'>>='", 
			"'&='", "'|='", "'^='", "'&&='", "'||='", "'<'", "'<='", "'!='", "'=='", 
			"'==='", "'!=='", "'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", "'|'", 
			"'^'", "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BEGIN", "BREAK", "CASE", "CONST", "CONTINUE", "DEF", 
			"DO", "ELIF", "ELSE", "ELSEIF", "ELSIF", "END", "FALSE", "FOR", "HEAP", 
			"IF", "IN", "INLINE", "LOOP", "NOINLINE", "NULL", "OUT", "PARAM", "RETURN", 
			"REQUIRE", "SENSOR", "STACK", "THEN", "TRUE", "VAR", "VOID", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "HASHSET", "HASHSTRICT", "HASHRELAXED", "EXP_ASSIGN", "MUL_ASSIGN", 
			"DIV_ASSIGN", "IDIV_ASSIGN", "MOD_ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", 
			"SHIFT_LEFT_ASSIGN", "SHIFT_RIGHT_ASSIGN", "BITWISE_AND_ASSIGN", "BITWISE_OR_ASSIGN", 
			"BITWISE_XOR_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", 
			"NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", 
			"BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "FORMATTABLE", "LITERAL", 
			"FLOAT", "INT", "HEXINT", "BININT", "ID", "REM_COMMENT", "COMMENT", "SL_COMMENT", 
			"WS"
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


	// This variable will control if semicolons are mandatory or optional
	public boolean strictSyntax = true;

	public MindcodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MindcodeParser.EOF, 0); }
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
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
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(136);
				expression_list(0);
				}
				break;
			}
			setState(139);
			match(EOF);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Expression_listContext extends ParserRuleContext {
		public Expression_or_rem_commentContext expression_or_rem_comment() {
			return getRuleContext(Expression_or_rem_commentContext.class,0);
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
			setState(142);
			expression_or_rem_comment();
			}
			_ctx.stop = _input.LT(-1);
			setState(148);
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
					setState(144);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(145);
					expression_or_rem_comment();
					}
					} 
				}
				setState(150);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Expression_or_rem_commentContext extends ParserRuleContext {
		public Expression_or_rem_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression_or_rem_comment; }
	 
		public Expression_or_rem_commentContext() { }
		public void copyFrom(Expression_or_rem_commentContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Rem_commentContext extends Expression_or_rem_commentContext {
		public TerminalNode REM_COMMENT() { return getToken(MindcodeParser.REM_COMMENT, 0); }
		public Rem_commentContext(Expression_or_rem_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRem_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRem_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRem_comment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Single_expContext extends Expression_or_rem_commentContext {
		public Exp_strict_or_relaxedContext exp_strict_or_relaxed() {
			return getRuleContext(Exp_strict_or_relaxedContext.class,0);
		}
		public Single_expContext(Expression_or_rem_commentContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterSingle_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitSingle_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitSingle_exp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expression_or_rem_commentContext expression_or_rem_comment() throws RecognitionException {
		Expression_or_rem_commentContext _localctx = new Expression_or_rem_commentContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expression_or_rem_comment);
		try {
			setState(153);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new Single_expContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(151);
				exp_strict_or_relaxed();
				}
				break;
			case 2:
				_localctx = new Rem_commentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
				match(REM_COMMENT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Exp_strict_or_relaxedContext extends ParserRuleContext {
		public Expression_strictContext expression_strict() {
			return getRuleContext(Expression_strictContext.class,0);
		}
		public Expression_relaxedContext expression_relaxed() {
			return getRuleContext(Expression_relaxedContext.class,0);
		}
		public Exp_strict_or_relaxedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp_strict_or_relaxed; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExp_strict_or_relaxed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExp_strict_or_relaxed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExp_strict_or_relaxed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Exp_strict_or_relaxedContext exp_strict_or_relaxed() throws RecognitionException {
		Exp_strict_or_relaxedContext _localctx = new Exp_strict_or_relaxedContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_exp_strict_or_relaxed);
		try {
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(155);
				if (!(strictSyntax)) throw new FailedPredicateException(this, "strictSyntax");
				setState(156);
				expression_strict();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
				if (!(strictSyntax == false)) throw new FailedPredicateException(this, "strictSyntax == false");
				setState(158);
				expression_relaxed();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Expression_strictContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(MindcodeParser.SEMICOLON, 0); }
		public Expression_strictContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression_strict; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExpression_strict(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExpression_strict(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExpression_strict(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expression_strictContext expression_strict() throws RecognitionException {
		Expression_strictContext _localctx = new Expression_strictContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_expression_strict);
		try {
			setState(167);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				expression(0);
				setState(162);
				match(SEMICOLON);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				expression(0);
				 notifyErrorListeners(_input.LT(-1), "missing ';'", new MissingSemicolonException(_input.LT(1))); 
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

	@SuppressWarnings("CheckReturnValue")
	public static class Expression_relaxedContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(MindcodeParser.SEMICOLON, 0); }
		public Expression_relaxedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression_relaxed; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExpression_relaxed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExpression_relaxed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExpression_relaxed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expression_relaxedContext expression_relaxed() throws RecognitionException {
		Expression_relaxedContext _localctx = new Expression_relaxedContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression_relaxed);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			expression(0);
			setState(171);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(170);
				match(SEMICOLON);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Optional_doContext extends ParserRuleContext {
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public Optional_doContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optional_do; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterOptional_do(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitOptional_do(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitOptional_do(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Optional_doContext optional_do() throws RecognitionException {
		Optional_doContext _localctx = new Optional_doContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_optional_do);
		try {
			setState(179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(173);
				if (!(strictSyntax)) throw new FailedPredicateException(this, "strictSyntax");
				setState(174);
				match(DO);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(175);
				if (!(strictSyntax == false)) throw new FailedPredicateException(this, "strictSyntax == false");
				setState(177);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(176);
					match(DO);
					}
					break;
				}
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

	@SuppressWarnings("CheckReturnValue")
	public static class Optional_thenContext extends ParserRuleContext {
		public TerminalNode THEN() { return getToken(MindcodeParser.THEN, 0); }
		public Optional_thenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optional_then; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterOptional_then(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitOptional_then(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitOptional_then(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Optional_thenContext optional_then() throws RecognitionException {
		Optional_thenContext _localctx = new Optional_thenContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_optional_then);
		try {
			setState(187);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(181);
				if (!(strictSyntax)) throw new FailedPredicateException(this, "strictSyntax");
				setState(182);
				match(THEN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(183);
				if (!(strictSyntax == false)) throw new FailedPredicateException(this, "strictSyntax == false");
				setState(185);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
				case 1:
					{
					setState(184);
					match(THEN);
					}
					break;
				}
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class ConstantContext extends ExpressionContext {
		public Const_declContext const_decl() {
			return getRuleContext(Const_declContext.class,0);
		}
		public ConstantContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
		public TerminalNode STRICT_NOT_EQUAL() { return getToken(MindcodeParser.STRICT_NOT_EQUAL, 0); }
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Unary_minusContext extends ExpressionContext {
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
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
	@SuppressWarnings("CheckReturnValue")
	public static class Bitwise_not_exprContext extends ExpressionContext {
		public TerminalNode BITWISE_NOT() { return getToken(MindcodeParser.BITWISE_NOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Bitwise_not_exprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBitwise_not_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBitwise_not_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBitwise_not_expr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Indirect_prop_accessContext extends ExpressionContext {
		public IndirectpropaccessContext indirectpropaccess() {
			return getRuleContext(IndirectpropaccessContext.class,0);
		}
		public Indirect_prop_accessContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIndirect_prop_access(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIndirect_prop_access(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIndirect_prop_access(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Compiler_directiveContext extends ExpressionContext {
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public Compiler_directiveContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCompiler_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCompiler_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCompiler_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParameterContext extends ExpressionContext {
		public Param_declContext param_decl() {
			return getRuleContext(Param_declContext.class,0);
		}
		public ParameterContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Literal_minusContext extends ExpressionContext {
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public Numeric_tContext numeric_t() {
			return getRuleContext(Numeric_tContext.class,0);
		}
		public Literal_minusContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_minus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_minus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_minus(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Literal_formattableContext extends ExpressionContext {
		public Formattable_tContext formattable_t() {
			return getRuleContext(Formattable_tContext.class,0);
		}
		public Literal_formattableContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiteral_formattable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiteral_formattable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiteral_formattable(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Continue_expContext extends ExpressionContext {
		public Continue_stContext continue_st() {
			return getRuleContext(Continue_stContext.class,0);
		}
		public Continue_expContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterContinue_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitContinue_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitContinue_exp(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Binop_bitwise_orContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode BITWISE_OR() { return getToken(MindcodeParser.BITWISE_OR, 0); }
		public TerminalNode BITWISE_XOR() { return getToken(MindcodeParser.BITWISE_XOR, 0); }
		public Binop_bitwise_orContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_bitwise_or(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_bitwise_or(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_bitwise_or(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Do_while_loopContext extends ExpressionContext {
		public Do_while_expressionContext do_while_expression() {
			return getRuleContext(Do_while_expressionContext.class,0);
		}
		public Do_while_loopContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterDo_while_loop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitDo_while_loop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitDo_while_loop(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Require_directiveContext extends ExpressionContext {
		public RequireContext require() {
			return getRuleContext(RequireContext.class,0);
		}
		public Require_directiveContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRequire_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRequire_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRequire_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Return_expContext extends ExpressionContext {
		public Return_stContext return_st() {
			return getRuleContext(Return_stContext.class,0);
		}
		public Return_expContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterReturn_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitReturn_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitReturn_exp(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Break_expContext extends ExpressionContext {
		public Break_stContext break_st() {
			return getRuleContext(Break_stContext.class,0);
		}
		public Break_expContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBreak_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBreak_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBreak_exp(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Ternary_opContext extends ExpressionContext {
		public ExpressionContext cond;
		public ExpressionContext true_branch;
		public ExpressionContext false_branch;
		public TerminalNode QUESTION_MARK() { return getToken(MindcodeParser.QUESTION_MARK, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class Binop_andContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(MindcodeParser.AND, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class Binop_orContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OR() { return getToken(MindcodeParser.OR, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class Binop_bitwise_andContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public TerminalNode BITWISE_AND() { return getToken(MindcodeParser.BITWISE_AND, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Binop_bitwise_andContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinop_bitwise_and(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinop_bitwise_and(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinop_bitwise_and(this);
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
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				_localctx = new Compiler_directiveContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(190);
				directive();
				}
				break;
			case 2:
				{
				_localctx = new Require_directiveContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(191);
				require();
				}
				break;
			case 3:
				{
				_localctx = new Literal_minusContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(192);
				match(MINUS);
				setState(193);
				numeric_t();
				}
				break;
			case 4:
				{
				_localctx = new Indirect_prop_accessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(194);
				indirectpropaccess();
				}
				break;
			case 5:
				{
				_localctx = new Case_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(195);
				case_expr();
				}
				break;
			case 6:
				{
				_localctx = new If_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(196);
				if_expr();
				}
				break;
			case 7:
				{
				_localctx = new Function_callContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(197);
				funcall();
				}
				break;
			case 8:
				{
				_localctx = new Property_accessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(198);
				propaccess();
				}
				break;
			case 9:
				{
				_localctx = new Function_declarationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(199);
				fundecl();
				}
				break;
			case 10:
				{
				_localctx = new AllocationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(200);
				alloc();
				}
				break;
			case 11:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(201);
				lvalue();
				}
				break;
			case 12:
				{
				_localctx = new While_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(202);
				while_expression();
				}
				break;
			case 13:
				{
				_localctx = new Do_while_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(203);
				do_while_expression();
				}
				break;
			case 14:
				{
				_localctx = new For_loopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(204);
				for_expression();
				}
				break;
			case 15:
				{
				_localctx = new Not_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(205);
				match(NOT);
				setState(206);
				expression(26);
				}
				break;
			case 16:
				{
				_localctx = new Bitwise_not_exprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(207);
				match(BITWISE_NOT);
				setState(208);
				expression(25);
				}
				break;
			case 17:
				{
				_localctx = new Unary_minusContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(209);
				match(MINUS);
				setState(210);
				expression(23);
				}
				break;
			case 18:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(211);
				assign();
				}
				break;
			case 19:
				{
				_localctx = new ConstantContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(212);
				const_decl();
				}
				break;
			case 20:
				{
				_localctx = new ParameterContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(213);
				param_decl();
				}
				break;
			case 21:
				{
				_localctx = new Literal_stringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(214);
				literal_t();
				}
				break;
			case 22:
				{
				_localctx = new Literal_formattableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(215);
				formattable_t();
				}
				break;
			case 23:
				{
				_localctx = new Literal_numericContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(216);
				numeric_t();
				}
				break;
			case 24:
				{
				_localctx = new Literal_boolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(217);
				bool_t();
				}
				break;
			case 25:
				{
				_localctx = new Literal_nullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(218);
				null_t();
				}
				break;
			case 26:
				{
				_localctx = new Parenthesized_expressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(219);
				match(LEFT_RBRACKET);
				setState(220);
				expression(0);
				setState(221);
				match(RIGHT_RBRACKET);
				}
				break;
			case 27:
				{
				_localctx = new Break_expContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(223);
				break_st();
				}
				break;
			case 28:
				{
				_localctx = new Continue_expContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(224);
				continue_st();
				}
				break;
			case 29:
				{
				_localctx = new Return_expContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(225);
				return_st();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(266);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(264);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
					case 1:
						{
						_localctx = new Binop_expContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_expContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(228);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(229);
						((Binop_expContext)_localctx).op = match(EXP);
						setState(230);
						((Binop_expContext)_localctx).right = expression(25);
						}
						break;
					case 2:
						{
						_localctx = new Binop_mul_div_modContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_mul_div_modContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(231);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(232);
						((Binop_mul_div_modContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 107202383708160L) != 0)) ) {
							((Binop_mul_div_modContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(233);
						((Binop_mul_div_modContext)_localctx).right = expression(23);
						}
						break;
					case 3:
						{
						_localctx = new Binop_plus_minusContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_plus_minusContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(234);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(235);
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
						setState(236);
						((Binop_plus_minusContext)_localctx).right = expression(22);
						}
						break;
					case 4:
						{
						_localctx = new Binop_shiftContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_shiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(237);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(238);
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
						setState(239);
						((Binop_shiftContext)_localctx).right = expression(21);
						}
						break;
					case 5:
						{
						_localctx = new Binop_bitwise_andContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_bitwise_andContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(240);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(241);
						match(BITWISE_AND);
						setState(242);
						((Binop_bitwise_andContext)_localctx).right = expression(20);
						}
						break;
					case 6:
						{
						_localctx = new Binop_bitwise_orContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_bitwise_orContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(243);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(244);
						((Binop_bitwise_orContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BITWISE_OR || _la==BITWISE_XOR) ) {
							((Binop_bitwise_orContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(245);
						((Binop_bitwise_orContext)_localctx).right = expression(19);
						}
						break;
					case 7:
						{
						_localctx = new Binop_inequality_comparisonContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_inequality_comparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(246);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(247);
						((Binop_inequality_comparisonContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 195L) != 0)) ) {
							((Binop_inequality_comparisonContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(248);
						((Binop_inequality_comparisonContext)_localctx).right = expression(18);
						}
						break;
					case 8:
						{
						_localctx = new Binop_equality_comparisonContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_equality_comparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(249);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(250);
						((Binop_equality_comparisonContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & 15L) != 0)) ) {
							((Binop_equality_comparisonContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(251);
						((Binop_equality_comparisonContext)_localctx).right = expression(17);
						}
						break;
					case 9:
						{
						_localctx = new Binop_andContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_andContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(252);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(253);
						((Binop_andContext)_localctx).op = match(AND);
						setState(254);
						((Binop_andContext)_localctx).right = expression(16);
						}
						break;
					case 10:
						{
						_localctx = new Binop_orContext(new ExpressionContext(_parentctx, _parentState));
						((Binop_orContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(255);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(256);
						((Binop_orContext)_localctx).op = match(OR);
						setState(257);
						((Binop_orContext)_localctx).right = expression(15);
						}
						break;
					case 11:
						{
						_localctx = new Ternary_opContext(new ExpressionContext(_parentctx, _parentState));
						((Ternary_opContext)_localctx).cond = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(258);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(259);
						match(QUESTION_MARK);
						{
						setState(260);
						((Ternary_opContext)_localctx).true_branch = expression(0);
						setState(261);
						match(COLON);
						setState(262);
						((Ternary_opContext)_localctx).false_branch = expression(0);
						}
						}
						break;
					}
					} 
				}
				setState(268);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class DirectiveContext extends ParserRuleContext {
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
	 
		public DirectiveContext() { }
		public void copyFrom(DirectiveContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class List_directiveContext extends DirectiveContext {
		public Token option;
		public Directive_listContext values;
		public TerminalNode HASHSET() { return getToken(MindcodeParser.HASHSET, 0); }
		public TerminalNode ID() { return getToken(MindcodeParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public Directive_listContext directive_list() {
			return getRuleContext(Directive_listContext.class,0);
		}
		public List_directiveContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterList_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitList_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitList_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Strict_directiveContext extends DirectiveContext {
		public TerminalNode HASHSTRICT() { return getToken(MindcodeParser.HASHSTRICT, 0); }
		public Strict_directiveContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterStrict_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitStrict_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitStrict_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Numeric_directiveContext extends DirectiveContext {
		public Token option;
		public Token value;
		public TerminalNode HASHSET() { return getToken(MindcodeParser.HASHSET, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public TerminalNode ID() { return getToken(MindcodeParser.ID, 0); }
		public TerminalNode INT() { return getToken(MindcodeParser.INT, 0); }
		public Numeric_directiveContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterNumeric_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitNumeric_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitNumeric_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Boolean_directiveContext extends DirectiveContext {
		public Token option;
		public Bool_tContext value;
		public TerminalNode HASHSET() { return getToken(MindcodeParser.HASHSET, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public TerminalNode ID() { return getToken(MindcodeParser.ID, 0); }
		public Bool_tContext bool_t() {
			return getRuleContext(Bool_tContext.class,0);
		}
		public Boolean_directiveContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBoolean_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBoolean_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBoolean_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Relaxed_directiveContext extends DirectiveContext {
		public TerminalNode HASHRELAXED() { return getToken(MindcodeParser.HASHRELAXED, 0); }
		public Relaxed_directiveContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRelaxed_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRelaxed_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRelaxed_directive(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class String_directiveContext extends DirectiveContext {
		public Token option;
		public Token value;
		public TerminalNode HASHSET() { return getToken(MindcodeParser.HASHSET, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public List<TerminalNode> ID() { return getTokens(MindcodeParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(MindcodeParser.ID, i);
		}
		public String_directiveContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterString_directive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitString_directive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitString_directive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_directive);
		try {
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				_localctx = new Numeric_directiveContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(269);
				match(HASHSET);
				setState(270);
				((Numeric_directiveContext)_localctx).option = match(ID);
				setState(271);
				match(ASSIGN);
				setState(272);
				((Numeric_directiveContext)_localctx).value = match(INT);
				}
				break;
			case 2:
				_localctx = new Boolean_directiveContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(273);
				match(HASHSET);
				setState(274);
				((Boolean_directiveContext)_localctx).option = match(ID);
				setState(275);
				match(ASSIGN);
				setState(276);
				((Boolean_directiveContext)_localctx).value = bool_t();
				}
				break;
			case 3:
				_localctx = new String_directiveContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(277);
				match(HASHSET);
				setState(278);
				((String_directiveContext)_localctx).option = match(ID);
				setState(279);
				match(ASSIGN);
				setState(280);
				((String_directiveContext)_localctx).value = match(ID);
				}
				break;
			case 4:
				_localctx = new List_directiveContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(281);
				match(HASHSET);
				setState(282);
				((List_directiveContext)_localctx).option = match(ID);
				setState(285);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(283);
					match(ASSIGN);
					setState(284);
					((List_directiveContext)_localctx).values = directive_list();
					}
					break;
				}
				}
				break;
			case 5:
				_localctx = new Strict_directiveContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(287);
				match(HASHSTRICT);
				strictSyntax = true;
				}
				break;
			case 6:
				_localctx = new Relaxed_directiveContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(289);
				match(HASHRELAXED);
				strictSyntax = false;
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

	@SuppressWarnings("CheckReturnValue")
	public static class Directive_listContext extends ParserRuleContext {
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public Directive_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterDirective_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitDirective_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitDirective_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Directive_listContext directive_list() throws RecognitionException {
		Directive_listContext _localctx = new Directive_listContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_directive_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			id();
			setState(298);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(294);
					match(COMMA);
					setState(295);
					id();
					}
					} 
				}
				setState(300);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class RequireContext extends ParserRuleContext {
		public RequireContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_require; }
	 
		public RequireContext() { }
		public void copyFrom(RequireContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Require_systemContext extends RequireContext {
		public IdContext file;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Require_systemContext(RequireContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRequire_system(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRequire_system(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRequire_system(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Require_externalContext extends RequireContext {
		public Literal_tContext file;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public Literal_tContext literal_t() {
			return getRuleContext(Literal_tContext.class,0);
		}
		public Require_externalContext(RequireContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRequire_external(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRequire_external(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRequire_external(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RequireContext require() throws RecognitionException {
		RequireContext _localctx = new RequireContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_require);
		try {
			setState(305);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				_localctx = new Require_systemContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				match(REQUIRE);
				setState(302);
				((Require_systemContext)_localctx).file = id();
				}
				break;
			case 2:
				_localctx = new Require_externalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(303);
				match(REQUIRE);
				setState(304);
				((Require_externalContext)_localctx).file = literal_t();
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

	@SuppressWarnings("CheckReturnValue")
	public static class IndirectpropaccessContext extends ParserRuleContext {
		public Var_refContext target;
		public ExpressionContext expr;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public TerminalNode SENSOR() { return getToken(MindcodeParser.SENSOR, 0); }
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public Var_refContext var_ref() {
			return getRuleContext(Var_refContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IndirectpropaccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indirectpropaccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIndirectpropaccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIndirectpropaccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIndirectpropaccess(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndirectpropaccessContext indirectpropaccess() throws RecognitionException {
		IndirectpropaccessContext _localctx = new IndirectpropaccessContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_indirectpropaccess);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307);
			((IndirectpropaccessContext)_localctx).target = var_ref();
			setState(308);
			match(DOT);
			setState(309);
			match(SENSOR);
			setState(310);
			match(LEFT_RBRACKET);
			setState(311);
			((IndirectpropaccessContext)_localctx).expr = expression(0);
			setState(312);
			match(RIGHT_RBRACKET);
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

	@SuppressWarnings("CheckReturnValue")
	public static class MethodaccessContext extends ParserRuleContext {
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
		public MethodaccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodaccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterMethodaccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitMethodaccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitMethodaccess(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodaccessContext methodaccess() throws RecognitionException {
		MethodaccessContext _localctx = new MethodaccessContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_methodaccess);
		try {
			setState(322);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(314);
				var_ref();
				setState(315);
				match(DOT);
				setState(316);
				((MethodaccessContext)_localctx).prop = id();
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(318);
				unit_ref();
				setState(319);
				match(DOT);
				setState(320);
				((MethodaccessContext)_localctx).prop = id();
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

	@SuppressWarnings("CheckReturnValue")
	public static class PropaccessContext extends ParserRuleContext {
		public LaccessContext prop;
		public Var_refContext var_ref() {
			return getRuleContext(Var_refContext.class,0);
		}
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public LaccessContext laccess() {
			return getRuleContext(LaccessContext.class,0);
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
		enterRule(_localctx, 28, RULE_propaccess);
		try {
			setState(332);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(324);
				var_ref();
				setState(325);
				match(DOT);
				setState(326);
				((PropaccessContext)_localctx).prop = laccess();
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(328);
				unit_ref();
				setState(329);
				match(DOT);
				setState(330);
				((PropaccessContext)_localctx).prop = laccess();
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

	@SuppressWarnings("CheckReturnValue")
	public static class LaccessContext extends ParserRuleContext {
		public LaccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_laccess; }
	 
		public LaccessContext() { }
		public void copyFrom(LaccessContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Laccess_strictContext extends LaccessContext {
		public Unit_refContext unit_ref() {
			return getRuleContext(Unit_refContext.class,0);
		}
		public Laccess_strictContext(LaccessContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLaccess_strict(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLaccess_strict(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLaccess_strict(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Laccess_relaxedContext extends LaccessContext {
		public Var_refContext var_ref() {
			return getRuleContext(Var_refContext.class,0);
		}
		public Laccess_relaxedContext(LaccessContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLaccess_relaxed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLaccess_relaxed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLaccess_relaxed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaccessContext laccess() throws RecognitionException {
		LaccessContext _localctx = new LaccessContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_laccess);
		try {
			setState(336);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				_localctx = new Laccess_strictContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(334);
				unit_ref();
				}
				break;
			case ID:
				_localctx = new Laccess_relaxedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(335);
				var_ref();
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 32, RULE_numeric_t);
		try {
			setState(340);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLOAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(338);
				float_t();
				}
				break;
			case INT:
			case HEXINT:
			case BININT:
				enterOuterAlt(_localctx, 2);
				{
				setState(339);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 34, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			match(ALLOCATE);
			setState(343);
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

	@SuppressWarnings("CheckReturnValue")
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
		int _startState = 36;
		enterRecursionRule(_localctx, 36, RULE_alloc_list, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(346);
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
			setState(347);
			match(IN);
			setState(348);
			id();
			setState(350);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(349);
				alloc_range();
				}
				break;
			}
			}
			_ctx.stop = _input.LT(-1);
			setState(362);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Alloc_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_alloc_list);
					setState(352);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(353);
					match(COMMA);
					setState(354);
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
					setState(355);
					match(IN);
					setState(356);
					id();
					setState(358);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
					case 1:
						{
						setState(357);
						alloc_range();
						}
						break;
					}
					}
					} 
				}
				setState(364);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Alloc_rangeContext extends ParserRuleContext {
		public TerminalNode LEFT_SBRACKET() { return getToken(MindcodeParser.LEFT_SBRACKET, 0); }
		public Range_expressionContext range_expression() {
			return getRuleContext(Range_expressionContext.class,0);
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
		enterRule(_localctx, 38, RULE_alloc_range);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			match(LEFT_SBRACKET);
			setState(366);
			range_expression();
			setState(367);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Const_declContext extends ParserRuleContext {
		public IdContext name;
		public ExpressionContext value;
		public TerminalNode CONST() { return getToken(MindcodeParser.CONST, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Const_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_const_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterConst_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitConst_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitConst_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Const_declContext const_decl() throws RecognitionException {
		Const_declContext _localctx = new Const_declContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_const_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			match(CONST);
			setState(370);
			((Const_declContext)_localctx).name = id();
			setState(371);
			match(ASSIGN);
			setState(372);
			((Const_declContext)_localctx).value = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Param_declContext extends ParserRuleContext {
		public IdContext name;
		public ExpressionContext value;
		public TerminalNode PARAM() { return getToken(MindcodeParser.PARAM, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Param_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterParam_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitParam_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitParam_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Param_declContext param_decl() throws RecognitionException {
		Param_declContext _localctx = new Param_declContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_param_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(374);
			match(PARAM);
			setState(375);
			((Param_declContext)_localctx).name = id();
			setState(376);
			match(ASSIGN);
			setState(377);
			((Param_declContext)_localctx).value = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
	public static class FundeclContext extends ParserRuleContext {
		public Token inline;
		public Token def;
		public IdContext name;
		public Arg_decl_listContext args;
		public Expression_listContext body;
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
		public TerminalNode VOID() { return getToken(MindcodeParser.VOID, 0); }
		public TerminalNode DEF() { return getToken(MindcodeParser.DEF, 0); }
		public TerminalNode INLINE() { return getToken(MindcodeParser.INLINE, 0); }
		public TerminalNode NOINLINE() { return getToken(MindcodeParser.NOINLINE, 0); }
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
		enterRule(_localctx, 44, RULE_fundecl);
		int _la;
		try {
			setState(409);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(380);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INLINE || _la==NOINLINE) {
					{
					setState(379);
					((FundeclContext)_localctx).inline = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==INLINE || _la==NOINLINE) ) {
						((FundeclContext)_localctx).inline = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(382);
				((FundeclContext)_localctx).def = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DEF || _la==VOID) ) {
					((FundeclContext)_localctx).def = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(383);
				((FundeclContext)_localctx).name = id();
				setState(384);
				match(LEFT_RBRACKET);
				setState(385);
				((FundeclContext)_localctx).args = arg_decl_list();
				setState(386);
				match(RIGHT_RBRACKET);
				setState(387);
				((FundeclContext)_localctx).body = expression_list(0);
				setState(388);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(391);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INLINE || _la==NOINLINE) {
					{
					setState(390);
					((FundeclContext)_localctx).inline = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==INLINE || _la==NOINLINE) ) {
						((FundeclContext)_localctx).inline = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(393);
				((FundeclContext)_localctx).def = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DEF || _la==VOID) ) {
					((FundeclContext)_localctx).def = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(394);
				((FundeclContext)_localctx).name = id();
				setState(395);
				match(LEFT_RBRACKET);
				setState(396);
				match(RIGHT_RBRACKET);
				setState(397);
				((FundeclContext)_localctx).body = expression_list(0);
				setState(398);
				match(END);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(400);
				if (!(strictSyntax == false)) throw new FailedPredicateException(this, "strictSyntax == false");
				setState(402);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INLINE || _la==NOINLINE) {
					{
					setState(401);
					((FundeclContext)_localctx).inline = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==INLINE || _la==NOINLINE) ) {
						((FundeclContext)_localctx).inline = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(404);
				((FundeclContext)_localctx).def = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DEF || _la==VOID) ) {
					((FundeclContext)_localctx).def = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(405);
				((FundeclContext)_localctx).name = id();
				setState(406);
				((FundeclContext)_localctx).body = expression_list(0);
				setState(407);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Arg_declContext extends ParserRuleContext {
		public Token modifier_in;
		public Token modifier_out;
		public Var_refContext name;
		public Token ellipsis;
		public Var_refContext var_ref() {
			return getRuleContext(Var_refContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public Arg_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterArg_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitArg_decl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitArg_decl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Arg_declContext arg_decl() throws RecognitionException {
		Arg_declContext _localctx = new Arg_declContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_arg_decl);
		int _la;
		try {
			setState(431);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(412);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(411);
					((Arg_declContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(415);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(414);
					((Arg_declContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(417);
				((Arg_declContext)_localctx).name = var_ref();
				setState(421);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(418);
					((Arg_declContext)_localctx).ellipsis = match(DOT);
					setState(419);
					match(DOT);
					setState(420);
					match(DOT);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(423);
				((Arg_declContext)_localctx).modifier_out = match(OUT);
				setState(424);
				((Arg_declContext)_localctx).modifier_in = match(IN);
				setState(425);
				((Arg_declContext)_localctx).name = var_ref();
				setState(429);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT) {
					{
					setState(426);
					((Arg_declContext)_localctx).ellipsis = match(DOT);
					setState(427);
					match(DOT);
					setState(428);
					match(DOT);
					}
				}

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

	@SuppressWarnings("CheckReturnValue")
	public static class Arg_decl_listContext extends ParserRuleContext {
		public List<Arg_declContext> arg_decl() {
			return getRuleContexts(Arg_declContext.class);
		}
		public Arg_declContext arg_decl(int i) {
			return getRuleContext(Arg_declContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
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
		Arg_decl_listContext _localctx = new Arg_decl_listContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_arg_decl_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(433);
			arg_decl();
			setState(438);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(434);
				match(COMMA);
				setState(435);
				arg_decl();
				}
				}
				setState(440);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class While_expressionContext extends ParserRuleContext {
		public Loop_labelContext label;
		public ExpressionContext cond;
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public Optional_doContext optional_do() {
			return getRuleContext(Optional_doContext.class,0);
		}
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
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
		enterRule(_localctx, 50, RULE_while_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(444);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(441);
				((While_expressionContext)_localctx).label = loop_label();
				setState(442);
				match(COLON);
				}
			}

			setState(446);
			match(WHILE);
			setState(447);
			((While_expressionContext)_localctx).cond = expression(0);
			setState(448);
			optional_do();
			setState(449);
			loop_body();
			setState(450);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Do_while_expressionContext extends ParserRuleContext {
		public Loop_labelContext label;
		public ExpressionContext cond;
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode LOOP() { return getToken(MindcodeParser.LOOP, 0); }
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
		}
		public Do_while_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_do_while_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterDo_while_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitDo_while_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitDo_while_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Do_while_expressionContext do_while_expression() throws RecognitionException {
		Do_while_expressionContext _localctx = new Do_while_expressionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_do_while_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(452);
				((Do_while_expressionContext)_localctx).label = loop_label();
				setState(453);
				match(COLON);
				}
			}

			setState(457);
			match(DO);
			setState(458);
			loop_body();
			setState(459);
			match(LOOP);
			setState(460);
			match(WHILE);
			setState(461);
			((Do_while_expressionContext)_localctx).cond = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Iterated_forContext extends For_expressionContext {
		public Loop_labelContext label;
		public Init_listContext init;
		public ExpressionContext cond;
		public Incr_listContext increment;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public Optional_doContext optional_do() {
			return getRuleContext(Optional_doContext.class,0);
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
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
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
	@SuppressWarnings("CheckReturnValue")
	public static class Ranged_forContext extends For_expressionContext {
		public Loop_labelContext label;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public Range_expressionContext range_expression() {
			return getRuleContext(Range_expressionContext.class,0);
		}
		public Optional_doContext optional_do() {
			return getRuleContext(Optional_doContext.class,0);
		}
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
		}
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
	@SuppressWarnings("CheckReturnValue")
	public static class For_each_1Context extends For_expressionContext {
		public Loop_labelContext label;
		public Iterator_listContext iterators;
		public Value_listContext values;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public Optional_doContext optional_do() {
			return getRuleContext(Optional_doContext.class,0);
		}
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public Iterator_listContext iterator_list() {
			return getRuleContext(Iterator_listContext.class,0);
		}
		public Value_listContext value_list() {
			return getRuleContext(Value_listContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
		}
		public For_each_1Context(For_expressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFor_each_1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFor_each_1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFor_each_1(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class For_each_2Context extends For_expressionContext {
		public Loop_labelContext label;
		public Iterator_listContext iterators;
		public Value_listContext values;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public Loop_bodyContext loop_body() {
			return getRuleContext(Loop_bodyContext.class,0);
		}
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public Iterator_listContext iterator_list() {
			return getRuleContext(Iterator_listContext.class,0);
		}
		public Value_listContext value_list() {
			return getRuleContext(Value_listContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
		}
		public For_each_2Context(For_expressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFor_each_2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFor_each_2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFor_each_2(this);
			else return visitor.visitChildren(this);
		}
	}

	public final For_expressionContext for_expression() throws RecognitionException {
		For_expressionContext _localctx = new For_expressionContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_for_expression);
		int _la;
		try {
			setState(519);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				_localctx = new Ranged_forContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(466);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(463);
					((Ranged_forContext)_localctx).label = loop_label();
					setState(464);
					match(COLON);
					}
				}

				setState(468);
				match(FOR);
				setState(469);
				lvalue();
				setState(470);
				match(IN);
				setState(471);
				range_expression();
				setState(472);
				optional_do();
				setState(473);
				loop_body();
				setState(474);
				match(END);
				}
				break;
			case 2:
				_localctx = new Iterated_forContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(479);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(476);
					((Iterated_forContext)_localctx).label = loop_label();
					setState(477);
					match(COLON);
					}
				}

				setState(481);
				match(FOR);
				setState(482);
				((Iterated_forContext)_localctx).init = init_list(0);
				setState(483);
				match(SEMICOLON);
				setState(484);
				((Iterated_forContext)_localctx).cond = expression(0);
				setState(485);
				match(SEMICOLON);
				setState(486);
				((Iterated_forContext)_localctx).increment = incr_list(0);
				setState(487);
				optional_do();
				setState(488);
				loop_body();
				setState(489);
				match(END);
				}
				break;
			case 3:
				_localctx = new For_each_1Context(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(494);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(491);
					((For_each_1Context)_localctx).label = loop_label();
					setState(492);
					match(COLON);
					}
				}

				setState(496);
				match(FOR);
				setState(497);
				((For_each_1Context)_localctx).iterators = iterator_list();
				setState(498);
				match(IN);
				setState(499);
				match(LEFT_RBRACKET);
				setState(500);
				((For_each_1Context)_localctx).values = value_list();
				setState(501);
				match(RIGHT_RBRACKET);
				setState(502);
				optional_do();
				setState(503);
				loop_body();
				setState(504);
				match(END);
				}
				break;
			case 4:
				_localctx = new For_each_2Context(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(509);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(506);
					((For_each_2Context)_localctx).label = loop_label();
					setState(507);
					match(COLON);
					}
				}

				setState(511);
				match(FOR);
				setState(512);
				((For_each_2Context)_localctx).iterators = iterator_list();
				setState(513);
				match(IN);
				setState(514);
				((For_each_2Context)_localctx).values = value_list();
				setState(515);
				match(DO);
				setState(516);
				loop_body();
				setState(517);
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

	@SuppressWarnings("CheckReturnValue")
	public static class IteratorContext extends ParserRuleContext {
		public Token modifier;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public IteratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIterator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIterator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIterator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IteratorContext iterator() throws RecognitionException {
		IteratorContext _localctx = new IteratorContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_iterator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(522);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OUT) {
				{
				setState(521);
				((IteratorContext)_localctx).modifier = match(OUT);
				}
			}

			setState(524);
			lvalue();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Iterator_listContext extends ParserRuleContext {
		public List<IteratorContext> iterator() {
			return getRuleContexts(IteratorContext.class);
		}
		public IteratorContext iterator(int i) {
			return getRuleContext(IteratorContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public Iterator_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterator_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterIterator_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitIterator_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitIterator_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Iterator_listContext iterator_list() throws RecognitionException {
		Iterator_listContext _localctx = new Iterator_listContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_iterator_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526);
			iterator();
			setState(531);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(527);
				match(COMMA);
				setState(528);
				iterator();
				}
				}
				setState(533);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Value_listContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public Value_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterValue_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitValue_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitValue_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_listContext value_list() throws RecognitionException {
		Value_listContext _localctx = new Value_listContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_value_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(534);
			expression(0);
			setState(539);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(535);
				match(COMMA);
				setState(536);
				expression(0);
				}
				}
				setState(541);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Loop_bodyContext extends ParserRuleContext {
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
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
		Loop_bodyContext _localctx = new Loop_bodyContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_loop_body);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			expression_list(0);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Continue_stContext extends ParserRuleContext {
		public Loop_labelContext label;
		public TerminalNode CONTINUE() { return getToken(MindcodeParser.CONTINUE, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
		}
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
		enterRule(_localctx, 64, RULE_continue_st);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(544);
			match(CONTINUE);
			setState(546);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(545);
				((Continue_stContext)_localctx).label = loop_label();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Break_stContext extends ParserRuleContext {
		public Loop_labelContext label;
		public TerminalNode BREAK() { return getToken(MindcodeParser.BREAK, 0); }
		public Loop_labelContext loop_label() {
			return getRuleContext(Loop_labelContext.class,0);
		}
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
		enterRule(_localctx, 66, RULE_break_st);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(548);
			match(BREAK);
			setState(550);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(549);
				((Break_stContext)_localctx).label = loop_label();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Return_stContext extends ParserRuleContext {
		public ExpressionContext retval;
		public TerminalNode RETURN() { return getToken(MindcodeParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Return_stContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_return_st; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterReturn_st(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitReturn_st(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitReturn_st(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Return_stContext return_st() throws RecognitionException {
		Return_stContext _localctx = new Return_stContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_return_st);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(552);
			match(RETURN);
			setState(554);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(553);
				((Return_stContext)_localctx).retval = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Range_expressionContext extends ParserRuleContext {
		public Range_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range_expression; }
	 
		public Range_expressionContext() { }
		public void copyFrom(Range_expressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Inclusive_range_expContext extends Range_expressionContext {
		public ExpressionContext start;
		public ExpressionContext end;
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Inclusive_range_expContext(Range_expressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterInclusive_range_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitInclusive_range_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitInclusive_range_exp(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Exclusive_range_expContext extends Range_expressionContext {
		public ExpressionContext start;
		public ExpressionContext end;
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Exclusive_range_expContext(Range_expressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExclusive_range_exp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExclusive_range_exp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExclusive_range_exp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Range_expressionContext range_expression() throws RecognitionException {
		Range_expressionContext _localctx = new Range_expressionContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_range_expression);
		try {
			setState(567);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				_localctx = new Inclusive_range_expContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(556);
				((Inclusive_range_expContext)_localctx).start = expression(0);
				setState(557);
				match(DOT);
				setState(558);
				match(DOT);
				setState(559);
				((Inclusive_range_expContext)_localctx).end = expression(0);
				}
				break;
			case 2:
				_localctx = new Exclusive_range_expContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(561);
				((Exclusive_range_expContext)_localctx).start = expression(0);
				setState(562);
				match(DOT);
				setState(563);
				match(DOT);
				setState(564);
				match(DOT);
				setState(565);
				((Exclusive_range_expContext)_localctx).end = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
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
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_init_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(570);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(577);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Init_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_init_list);
					setState(572);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(573);
					match(COMMA);
					setState(574);
					expression(0);
					}
					} 
				}
				setState(579);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
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
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_incr_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(581);
			expression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(588);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Incr_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_incr_list);
					setState(583);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(584);
					match(COMMA);
					setState(585);
					expression(0);
					}
					} 
				}
				setState(590);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class FuncallContext extends ParserRuleContext {
		public IdContext name;
		public Arg_listContext params;
		public MethodaccessContext obj;
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public MethodaccessContext methodaccess() {
			return getRuleContext(MethodaccessContext.class,0);
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
		enterRule(_localctx, 76, RULE_funcall);
		try {
			setState(608);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(591);
				match(END);
				setState(592);
				match(LEFT_RBRACKET);
				setState(593);
				match(RIGHT_RBRACKET);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(594);
				((FuncallContext)_localctx).name = id();
				setState(595);
				match(LEFT_RBRACKET);
				setState(596);
				match(RIGHT_RBRACKET);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(598);
				((FuncallContext)_localctx).name = id();
				setState(599);
				match(LEFT_RBRACKET);
				setState(600);
				((FuncallContext)_localctx).params = arg_list();
				setState(601);
				match(RIGHT_RBRACKET);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(603);
				((FuncallContext)_localctx).obj = methodaccess();
				setState(604);
				match(LEFT_RBRACKET);
				setState(605);
				((FuncallContext)_localctx).params = arg_list();
				setState(606);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArgContext extends ParserRuleContext {
		public Token modifier_in;
		public Token modifier_out;
		public ExpressionContext argument;
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
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
		enterRule(_localctx, 78, RULE_arg);
		try {
			setState(622);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(611);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
				case 1:
					{
					setState(610);
					((ArgContext)_localctx).modifier_in = match(IN);
					}
					break;
				}
				setState(614);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(613);
					((ArgContext)_localctx).modifier_out = match(OUT);
					}
					break;
				}
				setState(617);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(616);
					((ArgContext)_localctx).argument = expression(0);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(619);
				((ArgContext)_localctx).modifier_out = match(OUT);
				setState(620);
				((ArgContext)_localctx).modifier_in = match(IN);
				setState(621);
				((ArgContext)_localctx).argument = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Arg_listContext extends ParserRuleContext {
		public List<ArgContext> arg() {
			return getRuleContexts(ArgContext.class);
		}
		public ArgContext arg(int i) {
			return getRuleContext(ArgContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
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
		Arg_listContext _localctx = new Arg_listContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(624);
			arg();
			setState(629);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(625);
				match(COMMA);
				setState(626);
				arg();
				}
				}
				setState(631);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class If_exprContext extends ParserRuleContext {
		public ExpressionContext cond;
		public Expression_listContext true_branch;
		public TerminalNode IF() { return getToken(MindcodeParser.IF, 0); }
		public Optional_thenContext optional_then() {
			return getRuleContext(Optional_thenContext.class,0);
		}
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
		enterRule(_localctx, 82, RULE_if_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			match(IF);
			setState(633);
			((If_exprContext)_localctx).cond = expression(0);
			setState(634);
			optional_then();
			setState(636);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(635);
				((If_exprContext)_localctx).true_branch = expression_list(0);
				}
				break;
			}
			setState(639);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE || _la==ELSIF) {
				{
				setState(638);
				if_trailer();
				}
			}

			setState(641);
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

	@SuppressWarnings("CheckReturnValue")
	public static class If_trailerContext extends ParserRuleContext {
		public Expression_listContext false_branch;
		public ExpressionContext cond;
		public Expression_listContext true_branch;
		public TerminalNode ELSE() { return getToken(MindcodeParser.ELSE, 0); }
		public Expression_listContext expression_list() {
			return getRuleContext(Expression_listContext.class,0);
		}
		public TerminalNode ELSIF() { return getToken(MindcodeParser.ELSIF, 0); }
		public Optional_thenContext optional_then() {
			return getRuleContext(Optional_thenContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public If_trailerContext if_trailer() {
			return getRuleContext(If_trailerContext.class,0);
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
		enterRule(_localctx, 84, RULE_if_trailer);
		int _la;
		try {
			setState(656);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ELSE:
				enterOuterAlt(_localctx, 1);
				{
				setState(643);
				match(ELSE);
				setState(645);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
				case 1:
					{
					setState(644);
					((If_trailerContext)_localctx).false_branch = expression_list(0);
					}
					break;
				}
				}
				break;
			case ELSIF:
				enterOuterAlt(_localctx, 2);
				{
				setState(647);
				match(ELSIF);
				setState(648);
				((If_trailerContext)_localctx).cond = expression(0);
				setState(649);
				optional_then();
				setState(651);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
				case 1:
					{
					setState(650);
					((If_trailerContext)_localctx).true_branch = expression_list(0);
					}
					break;
				}
				setState(654);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE || _la==ELSIF) {
					{
					setState(653);
					if_trailer();
					}
				}

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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 86, RULE_case_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(658);
			match(CASE);
			setState(659);
			((Case_exprContext)_localctx).cond = expression(0);
			setState(661);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHEN) {
				{
				setState(660);
				alternative_list(0);
				}
			}

			setState(665);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(663);
				match(ELSE);
				setState(664);
				((Case_exprContext)_localctx).else_branch = expression_list(0);
				}
			}

			setState(667);
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

	@SuppressWarnings("CheckReturnValue")
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
		int _startState = 88;
		enterRecursionRule(_localctx, 88, RULE_alternative_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(670);
			alternative();
			}
			_ctx.stop = _input.LT(-1);
			setState(676);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Alternative_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_alternative_list);
					setState(672);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(673);
					alternative();
					}
					} 
				}
				setState(678);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class AlternativeContext extends ParserRuleContext {
		public When_value_listContext values;
		public Expression_listContext body;
		public TerminalNode WHEN() { return getToken(MindcodeParser.WHEN, 0); }
		public Optional_thenContext optional_then() {
			return getRuleContext(Optional_thenContext.class,0);
		}
		public When_value_listContext when_value_list() {
			return getRuleContext(When_value_listContext.class,0);
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
		enterRule(_localctx, 90, RULE_alternative);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(679);
			match(WHEN);
			setState(680);
			((AlternativeContext)_localctx).values = when_value_list(0);
			setState(681);
			optional_then();
			setState(683);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(682);
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

	@SuppressWarnings("CheckReturnValue")
	public static class When_expressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Range_expressionContext range_expression() {
			return getRuleContext(Range_expressionContext.class,0);
		}
		public When_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterWhen_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitWhen_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitWhen_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final When_expressionContext when_expression() throws RecognitionException {
		When_expressionContext _localctx = new When_expressionContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_when_expression);
		try {
			setState(687);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(685);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(686);
				range_expression();
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

	@SuppressWarnings("CheckReturnValue")
	public static class When_value_listContext extends ParserRuleContext {
		public When_expressionContext when_expression() {
			return getRuleContext(When_expressionContext.class,0);
		}
		public When_value_listContext when_value_list() {
			return getRuleContext(When_value_listContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public When_value_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when_value_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterWhen_value_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitWhen_value_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitWhen_value_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final When_value_listContext when_value_list() throws RecognitionException {
		return when_value_list(0);
	}

	private When_value_listContext when_value_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		When_value_listContext _localctx = new When_value_listContext(_ctx, _parentState);
		When_value_listContext _prevctx = _localctx;
		int _startState = 94;
		enterRecursionRule(_localctx, 94, RULE_when_value_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(690);
			when_expression();
			}
			_ctx.stop = _input.LT(-1);
			setState(697);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new When_value_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_when_value_list);
					setState(692);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(693);
					match(COMMA);
					setState(694);
					when_expression();
					}
					} 
				}
				setState(699);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
	public static class Compound_assignContext extends AssignContext {
		public LvalueContext target;
		public Token op;
		public ExpressionContext value;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXP_ASSIGN() { return getToken(MindcodeParser.EXP_ASSIGN, 0); }
		public TerminalNode MUL_ASSIGN() { return getToken(MindcodeParser.MUL_ASSIGN, 0); }
		public TerminalNode DIV_ASSIGN() { return getToken(MindcodeParser.DIV_ASSIGN, 0); }
		public TerminalNode IDIV_ASSIGN() { return getToken(MindcodeParser.IDIV_ASSIGN, 0); }
		public TerminalNode MOD_ASSIGN() { return getToken(MindcodeParser.MOD_ASSIGN, 0); }
		public TerminalNode PLUS_ASSIGN() { return getToken(MindcodeParser.PLUS_ASSIGN, 0); }
		public TerminalNode MINUS_ASSIGN() { return getToken(MindcodeParser.MINUS_ASSIGN, 0); }
		public TerminalNode SHIFT_LEFT_ASSIGN() { return getToken(MindcodeParser.SHIFT_LEFT_ASSIGN, 0); }
		public TerminalNode SHIFT_RIGHT_ASSIGN() { return getToken(MindcodeParser.SHIFT_RIGHT_ASSIGN, 0); }
		public TerminalNode BITWISE_AND_ASSIGN() { return getToken(MindcodeParser.BITWISE_AND_ASSIGN, 0); }
		public TerminalNode BITWISE_OR_ASSIGN() { return getToken(MindcodeParser.BITWISE_OR_ASSIGN, 0); }
		public TerminalNode BITWISE_XOR_ASSIGN() { return getToken(MindcodeParser.BITWISE_XOR_ASSIGN, 0); }
		public TerminalNode AND_ASSIGN() { return getToken(MindcodeParser.AND_ASSIGN, 0); }
		public TerminalNode OR_ASSIGN() { return getToken(MindcodeParser.OR_ASSIGN, 0); }
		public Compound_assignContext(AssignContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCompound_assign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCompound_assign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCompound_assign(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignContext assign() throws RecognitionException {
		AssignContext _localctx = new AssignContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_assign);
		int _la;
		try {
			setState(708);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				_localctx = new Simple_assignContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(700);
				((Simple_assignContext)_localctx).target = lvalue();
				setState(701);
				match(ASSIGN);
				setState(702);
				((Simple_assignContext)_localctx).value = expression(0);
				}
				break;
			case 2:
				_localctx = new Compound_assignContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(704);
				((Compound_assignContext)_localctx).target = lvalue();
				setState(705);
				((Compound_assignContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 55)) & ~0x3f) == 0 && ((1L << (_la - 55)) & 16383L) != 0)) ) {
					((Compound_assignContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(706);
				((Compound_assignContext)_localctx).value = expression(0);
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

	@SuppressWarnings("CheckReturnValue")
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
		public MethodaccessContext methodaccess() {
			return getRuleContext(MethodaccessContext.class,0);
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
		enterRule(_localctx, 98, RULE_lvalue);
		try {
			setState(715);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(710);
				unit_ref();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(711);
				global_ref();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(712);
				heap_ref();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(713);
				var_ref();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(714);
				methodaccess();
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

	@SuppressWarnings("CheckReturnValue")
	public static class Loop_labelContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MindcodeParser.ID, 0); }
		public Loop_labelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loop_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLoop_label(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLoop_label(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLoop_label(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Loop_labelContext loop_label() throws RecognitionException {
		Loop_labelContext _localctx = new Loop_labelContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_loop_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(717);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 102, RULE_heap_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(719);
			((Heap_refContext)_localctx).name = id();
			setState(720);
			match(LEFT_SBRACKET);
			setState(721);
			((Heap_refContext)_localctx).address = expression(0);
			setState(722);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 104, RULE_global_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(724);
			match(DOLLAR);
			setState(725);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 106, RULE_unit_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			match(AT);
			setState(728);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 108, RULE_var_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(730);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 110, RULE_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(732);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Int_tContext extends ParserRuleContext {
		public Decimal_intContext decimal_int() {
			return getRuleContext(Decimal_intContext.class,0);
		}
		public Hex_intContext hex_int() {
			return getRuleContext(Hex_intContext.class,0);
		}
		public Binary_intContext binary_int() {
			return getRuleContext(Binary_intContext.class,0);
		}
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
		enterRule(_localctx, 112, RULE_int_t);
		try {
			setState(737);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(734);
				decimal_int();
				}
				break;
			case HEXINT:
				enterOuterAlt(_localctx, 2);
				{
				setState(735);
				hex_int();
				}
				break;
			case BININT:
				enterOuterAlt(_localctx, 3);
				{
				setState(736);
				binary_int();
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 114, RULE_float_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(739);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 116, RULE_literal_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(741);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Formattable_tContext extends ParserRuleContext {
		public TerminalNode FORMATTABLE() { return getToken(MindcodeParser.FORMATTABLE, 0); }
		public Formattable_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formattable_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFormattable_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFormattable_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFormattable_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Formattable_tContext formattable_t() throws RecognitionException {
		Formattable_tContext _localctx = new Formattable_tContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_formattable_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(743);
			match(FORMATTABLE);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 120, RULE_null_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(745);
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 122, RULE_bool_t);
		try {
			setState(749);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
				_localctx = new True_bool_literalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(747);
				true_t();
				}
				break;
			case FALSE:
				_localctx = new False_bool_literalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(748);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 124, RULE_true_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(751);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 126, RULE_false_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(753);
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

	@SuppressWarnings("CheckReturnValue")
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
		enterRule(_localctx, 128, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(755);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Decimal_intContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(MindcodeParser.INT, 0); }
		public Decimal_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimal_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterDecimal_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitDecimal_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitDecimal_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Decimal_intContext decimal_int() throws RecognitionException {
		Decimal_intContext _localctx = new Decimal_intContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_decimal_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(757);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Hex_intContext extends ParserRuleContext {
		public TerminalNode HEXINT() { return getToken(MindcodeParser.HEXINT, 0); }
		public Hex_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hex_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterHex_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitHex_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitHex_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Hex_intContext hex_int() throws RecognitionException {
		Hex_intContext _localctx = new Hex_intContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_hex_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(759);
			match(HEXINT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Binary_intContext extends ParserRuleContext {
		public TerminalNode BININT() { return getToken(MindcodeParser.BININT, 0); }
		public Binary_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binary_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBinary_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBinary_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBinary_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Binary_intContext binary_int() throws RecognitionException {
		Binary_intContext _localctx = new Binary_intContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_binary_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(761);
			match(BININT);
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
		case 3:
			return exp_strict_or_relaxed_sempred((Exp_strict_or_relaxedContext)_localctx, predIndex);
		case 6:
			return optional_do_sempred((Optional_doContext)_localctx, predIndex);
		case 7:
			return optional_then_sempred((Optional_thenContext)_localctx, predIndex);
		case 8:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 18:
			return alloc_list_sempred((Alloc_listContext)_localctx, predIndex);
		case 22:
			return fundecl_sempred((FundeclContext)_localctx, predIndex);
		case 36:
			return init_list_sempred((Init_listContext)_localctx, predIndex);
		case 37:
			return incr_list_sempred((Incr_listContext)_localctx, predIndex);
		case 44:
			return alternative_list_sempred((Alternative_listContext)_localctx, predIndex);
		case 47:
			return when_value_list_sempred((When_value_listContext)_localctx, predIndex);
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
	private boolean exp_strict_or_relaxed_sempred(Exp_strict_or_relaxedContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return strictSyntax;
		case 2:
			return strictSyntax == false;
		}
		return true;
	}
	private boolean optional_do_sempred(Optional_doContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return strictSyntax;
		case 4:
			return strictSyntax == false;
		}
		return true;
	}
	private boolean optional_then_sempred(Optional_thenContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return strictSyntax;
		case 6:
			return strictSyntax == false;
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 24);
		case 8:
			return precpred(_ctx, 22);
		case 9:
			return precpred(_ctx, 21);
		case 10:
			return precpred(_ctx, 20);
		case 11:
			return precpred(_ctx, 19);
		case 12:
			return precpred(_ctx, 18);
		case 13:
			return precpred(_ctx, 17);
		case 14:
			return precpred(_ctx, 16);
		case 15:
			return precpred(_ctx, 15);
		case 16:
			return precpred(_ctx, 14);
		case 17:
			return precpred(_ctx, 13);
		}
		return true;
	}
	private boolean alloc_list_sempred(Alloc_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 18:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean fundecl_sempred(FundeclContext _localctx, int predIndex) {
		switch (predIndex) {
		case 19:
			return strictSyntax == false;
		}
		return true;
	}
	private boolean init_list_sempred(Init_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 20:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean incr_list_sempred(Incr_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 21:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean alternative_list_sempred(Alternative_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 22:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean when_value_list_sempred(When_value_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 23:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001d\u02fc\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"+
		"A\u0007A\u0002B\u0007B\u0002C\u0007C\u0001\u0000\u0003\u0000\u008a\b\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0005\u0001\u0093\b\u0001\n\u0001\f\u0001\u0096\t\u0001\u0001"+
		"\u0002\u0001\u0002\u0003\u0002\u009a\b\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u00a0\b\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00a8\b\u0004\u0001"+
		"\u0005\u0001\u0005\u0003\u0005\u00ac\b\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006\u00b2\b\u0006\u0003\u0006\u00b4\b\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u00ba\b\u0007"+
		"\u0003\u0007\u00bc\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u00e3\b\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u0109\b\b\n"+
		"\b\f\b\u010c\t\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0003\t\u011e\b\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u0124\b\t"+
		"\u0001\n\u0001\n\u0001\n\u0005\n\u0129\b\n\n\n\f\n\u012c\t\n\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0132\b\u000b\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0143\b\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u014d\b\u000e\u0001\u000f\u0001\u000f\u0003\u000f"+
		"\u0151\b\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u0155\b\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0003\u0012\u015f\b\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u0167\b\u0012\u0005"+
		"\u0012\u0169\b\u0012\n\u0012\f\u0012\u016c\t\u0012\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0016\u0003\u0016\u017d\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0003\u0016\u0188\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u0193\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0003\u0016\u019a\b\u0016\u0001\u0017\u0003\u0017\u019d\b\u0017\u0001"+
		"\u0017\u0003\u0017\u01a0\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0003\u0017\u01a6\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u01ae\b\u0017\u0003\u0017\u01b0"+
		"\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018\u01b5\b\u0018"+
		"\n\u0018\f\u0018\u01b8\t\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0003"+
		"\u0019\u01bd\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u01c8"+
		"\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u01d3\b\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u01e0"+
		"\b\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0003\u001b\u01ef\b\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u01fe"+
		"\b\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0208\b\u001b\u0001\u001c\u0003"+
		"\u001c\u020b\b\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0005\u001d\u0212\b\u001d\n\u001d\f\u001d\u0215\t\u001d\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0005\u001e\u021a\b\u001e\n\u001e\f\u001e\u021d"+
		"\t\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0003 \u0223\b \u0001!"+
		"\u0001!\u0003!\u0227\b!\u0001\"\u0001\"\u0003\"\u022b\b\"\u0001#\u0001"+
		"#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0001#\u0003"+
		"#\u0238\b#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0005$\u0240\b$\n"+
		"$\f$\u0243\t$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0005%\u024b\b"+
		"%\n%\f%\u024e\t%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003"+
		"&\u0261\b&\u0001\'\u0003\'\u0264\b\'\u0001\'\u0003\'\u0267\b\'\u0001\'"+
		"\u0003\'\u026a\b\'\u0001\'\u0001\'\u0001\'\u0003\'\u026f\b\'\u0001(\u0001"+
		"(\u0001(\u0005(\u0274\b(\n(\f(\u0277\t(\u0001)\u0001)\u0001)\u0001)\u0003"+
		")\u027d\b)\u0001)\u0003)\u0280\b)\u0001)\u0001)\u0001*\u0001*\u0003*\u0286"+
		"\b*\u0001*\u0001*\u0001*\u0001*\u0003*\u028c\b*\u0001*\u0003*\u028f\b"+
		"*\u0003*\u0291\b*\u0001+\u0001+\u0001+\u0003+\u0296\b+\u0001+\u0001+\u0003"+
		"+\u029a\b+\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0001,\u0005,\u02a3"+
		"\b,\n,\f,\u02a6\t,\u0001-\u0001-\u0001-\u0001-\u0003-\u02ac\b-\u0001."+
		"\u0001.\u0003.\u02b0\b.\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0005"+
		"/\u02b8\b/\n/\f/\u02bb\t/\u00010\u00010\u00010\u00010\u00010\u00010\u0001"+
		"0\u00010\u00030\u02c5\b0\u00011\u00011\u00011\u00011\u00011\u00031\u02cc"+
		"\b1\u00012\u00012\u00013\u00013\u00013\u00013\u00013\u00014\u00014\u0001"+
		"4\u00015\u00015\u00015\u00016\u00016\u00017\u00017\u00018\u00018\u0001"+
		"8\u00038\u02e2\b8\u00019\u00019\u0001:\u0001:\u0001;\u0001;\u0001<\u0001"+
		"<\u0001=\u0001=\u0003=\u02ee\b=\u0001>\u0001>\u0001?\u0001?\u0001@\u0001"+
		"@\u0001A\u0001A\u0001B\u0001B\u0001C\u0001C\u0001C\u0000\u0007\u0002\u0010"+
		"$HJX^D\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080"+
		"\u0082\u0084\u0086\u0000\n\u0002\u0000\'(-.\u0002\u0000,,11\u0001\u0000"+
		"OP\u0001\u0000RS\u0002\u0000EFKL\u0001\u0000GJ\u0002\u0000\u0010\u0010"+
		"\u001c\u001c\u0002\u0000\u0013\u0013\u0015\u0015\u0002\u0000\u0007\u0007"+
		"  \u0001\u00007D\u0330\u0000\u0089\u0001\u0000\u0000\u0000\u0002\u008d"+
		"\u0001\u0000\u0000\u0000\u0004\u0099\u0001\u0000\u0000\u0000\u0006\u009f"+
		"\u0001\u0000\u0000\u0000\b\u00a7\u0001\u0000\u0000\u0000\n\u00a9\u0001"+
		"\u0000\u0000\u0000\f\u00b3\u0001\u0000\u0000\u0000\u000e\u00bb\u0001\u0000"+
		"\u0000\u0000\u0010\u00e2\u0001\u0000\u0000\u0000\u0012\u0123\u0001\u0000"+
		"\u0000\u0000\u0014\u0125\u0001\u0000\u0000\u0000\u0016\u0131\u0001\u0000"+
		"\u0000\u0000\u0018\u0133\u0001\u0000\u0000\u0000\u001a\u0142\u0001\u0000"+
		"\u0000\u0000\u001c\u014c\u0001\u0000\u0000\u0000\u001e\u0150\u0001\u0000"+
		"\u0000\u0000 \u0154\u0001\u0000\u0000\u0000\"\u0156\u0001\u0000\u0000"+
		"\u0000$\u0159\u0001\u0000\u0000\u0000&\u016d\u0001\u0000\u0000\u0000("+
		"\u0171\u0001\u0000\u0000\u0000*\u0176\u0001\u0000\u0000\u0000,\u0199\u0001"+
		"\u0000\u0000\u0000.\u01af\u0001\u0000\u0000\u00000\u01b1\u0001\u0000\u0000"+
		"\u00002\u01bc\u0001\u0000\u0000\u00004\u01c7\u0001\u0000\u0000\u00006"+
		"\u0207\u0001\u0000\u0000\u00008\u020a\u0001\u0000\u0000\u0000:\u020e\u0001"+
		"\u0000\u0000\u0000<\u0216\u0001\u0000\u0000\u0000>\u021e\u0001\u0000\u0000"+
		"\u0000@\u0220\u0001\u0000\u0000\u0000B\u0224\u0001\u0000\u0000\u0000D"+
		"\u0228\u0001\u0000\u0000\u0000F\u0237\u0001\u0000\u0000\u0000H\u0239\u0001"+
		"\u0000\u0000\u0000J\u0244\u0001\u0000\u0000\u0000L\u0260\u0001\u0000\u0000"+
		"\u0000N\u026e\u0001\u0000\u0000\u0000P\u0270\u0001\u0000\u0000\u0000R"+
		"\u0278\u0001\u0000\u0000\u0000T\u0290\u0001\u0000\u0000\u0000V\u0292\u0001"+
		"\u0000\u0000\u0000X\u029d\u0001\u0000\u0000\u0000Z\u02a7\u0001\u0000\u0000"+
		"\u0000\\\u02af\u0001\u0000\u0000\u0000^\u02b1\u0001\u0000\u0000\u0000"+
		"`\u02c4\u0001\u0000\u0000\u0000b\u02cb\u0001\u0000\u0000\u0000d\u02cd"+
		"\u0001\u0000\u0000\u0000f\u02cf\u0001\u0000\u0000\u0000h\u02d4\u0001\u0000"+
		"\u0000\u0000j\u02d7\u0001\u0000\u0000\u0000l\u02da\u0001\u0000\u0000\u0000"+
		"n\u02dc\u0001\u0000\u0000\u0000p\u02e1\u0001\u0000\u0000\u0000r\u02e3"+
		"\u0001\u0000\u0000\u0000t\u02e5\u0001\u0000\u0000\u0000v\u02e7\u0001\u0000"+
		"\u0000\u0000x\u02e9\u0001\u0000\u0000\u0000z\u02ed\u0001\u0000\u0000\u0000"+
		"|\u02ef\u0001\u0000\u0000\u0000~\u02f1\u0001\u0000\u0000\u0000\u0080\u02f3"+
		"\u0001\u0000\u0000\u0000\u0082\u02f5\u0001\u0000\u0000\u0000\u0084\u02f7"+
		"\u0001\u0000\u0000\u0000\u0086\u02f9\u0001\u0000\u0000\u0000\u0088\u008a"+
		"\u0003\u0002\u0001\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a"+
		"\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008c"+
		"\u0005\u0000\u0000\u0001\u008c\u0001\u0001\u0000\u0000\u0000\u008d\u008e"+
		"\u0006\u0001\uffff\uffff\u0000\u008e\u008f\u0003\u0004\u0002\u0000\u008f"+
		"\u0094\u0001\u0000\u0000\u0000\u0090\u0091\n\u0001\u0000\u0000\u0091\u0093"+
		"\u0003\u0004\u0002\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0093\u0096"+
		"\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0094\u0095"+
		"\u0001\u0000\u0000\u0000\u0095\u0003\u0001\u0000\u0000\u0000\u0096\u0094"+
		"\u0001\u0000\u0000\u0000\u0097\u009a\u0003\u0006\u0003\u0000\u0098\u009a"+
		"\u0005a\u0000\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u0098\u0001"+
		"\u0000\u0000\u0000\u009a\u0005\u0001\u0000\u0000\u0000\u009b\u009c\u0004"+
		"\u0003\u0001\u0000\u009c\u00a0\u0003\b\u0004\u0000\u009d\u009e\u0004\u0003"+
		"\u0002\u0000\u009e\u00a0\u0003\n\u0005\u0000\u009f\u009b\u0001\u0000\u0000"+
		"\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u00a0\u0007\u0001\u0000\u0000"+
		"\u0000\u00a1\u00a2\u0003\u0010\b\u0000\u00a2\u00a3\u00053\u0000\u0000"+
		"\u00a3\u00a8\u0001\u0000\u0000\u0000\u00a4\u00a5\u0003\u0010\b\u0000\u00a5"+
		"\u00a6\u0006\u0004\uffff\uffff\u0000\u00a6\u00a8\u0001\u0000\u0000\u0000"+
		"\u00a7\u00a1\u0001\u0000\u0000\u0000\u00a7\u00a4\u0001\u0000\u0000\u0000"+
		"\u00a8\t\u0001\u0000\u0000\u0000\u00a9\u00ab\u0003\u0010\b\u0000\u00aa"+
		"\u00ac\u00053\u0000\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ab\u00ac"+
		"\u0001\u0000\u0000\u0000\u00ac\u000b\u0001\u0000\u0000\u0000\u00ad\u00ae"+
		"\u0004\u0006\u0003\u0000\u00ae\u00b4\u0005\b\u0000\u0000\u00af\u00b1\u0004"+
		"\u0006\u0004\u0000\u00b0\u00b2\u0005\b\u0000\u0000\u00b1\u00b0\u0001\u0000"+
		"\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b4\u0001\u0000"+
		"\u0000\u0000\u00b3\u00ad\u0001\u0000\u0000\u0000\u00b3\u00af\u0001\u0000"+
		"\u0000\u0000\u00b4\r\u0001\u0000\u0000\u0000\u00b5\u00b6\u0004\u0007\u0005"+
		"\u0000\u00b6\u00bc\u0005\u001d\u0000\u0000\u00b7\u00b9\u0004\u0007\u0006"+
		"\u0000\u00b8\u00ba\u0005\u001d\u0000\u0000\u00b9\u00b8\u0001\u0000\u0000"+
		"\u0000\u00b9\u00ba\u0001\u0000\u0000\u0000\u00ba\u00bc\u0001\u0000\u0000"+
		"\u0000\u00bb\u00b5\u0001\u0000\u0000\u0000\u00bb\u00b7\u0001\u0000\u0000"+
		"\u0000\u00bc\u000f\u0001\u0000\u0000\u0000\u00bd\u00be\u0006\b\uffff\uffff"+
		"\u0000\u00be\u00e3\u0003\u0012\t\u0000\u00bf\u00e3\u0003\u0016\u000b\u0000"+
		"\u00c0\u00c1\u0005,\u0000\u0000\u00c1\u00e3\u0003 \u0010\u0000\u00c2\u00e3"+
		"\u0003\u0018\f\u0000\u00c3\u00e3\u0003V+\u0000\u00c4\u00e3\u0003R)\u0000"+
		"\u00c5\u00e3\u0003L&\u0000\u00c6\u00e3\u0003\u001c\u000e\u0000\u00c7\u00e3"+
		"\u0003,\u0016\u0000\u00c8\u00e3\u0003\"\u0011\u0000\u00c9\u00e3\u0003"+
		"b1\u0000\u00ca\u00e3\u00032\u0019\u0000\u00cb\u00e3\u00034\u001a\u0000"+
		"\u00cc\u00e3\u00036\u001b\u0000\u00cd\u00ce\u0005/\u0000\u0000\u00ce\u00e3"+
		"\u0003\u0010\b\u001a\u00cf\u00d0\u00050\u0000\u0000\u00d0\u00e3\u0003"+
		"\u0010\b\u0019\u00d1\u00d2\u0005,\u0000\u0000\u00d2\u00e3\u0003\u0010"+
		"\b\u0017\u00d3\u00e3\u0003`0\u0000\u00d4\u00e3\u0003(\u0014\u0000\u00d5"+
		"\u00e3\u0003*\u0015\u0000\u00d6\u00e3\u0003t:\u0000\u00d7\u00e3\u0003"+
		"v;\u0000\u00d8\u00e3\u0003 \u0010\u0000\u00d9\u00e3\u0003z=\u0000\u00da"+
		"\u00e3\u0003x<\u0000\u00db\u00dc\u0005V\u0000\u0000\u00dc\u00dd\u0003"+
		"\u0010\b\u0000\u00dd\u00de\u0005W\u0000\u0000\u00de\u00e3\u0001\u0000"+
		"\u0000\u0000\u00df\u00e3\u0003B!\u0000\u00e0\u00e3\u0003@ \u0000\u00e1"+
		"\u00e3\u0003D\"\u0000\u00e2\u00bd\u0001\u0000\u0000\u0000\u00e2\u00bf"+
		"\u0001\u0000\u0000\u0000\u00e2\u00c0\u0001\u0000\u0000\u0000\u00e2\u00c2"+
		"\u0001\u0000\u0000\u0000\u00e2\u00c3\u0001\u0000\u0000\u0000\u00e2\u00c4"+
		"\u0001\u0000\u0000\u0000\u00e2\u00c5\u0001\u0000\u0000\u0000\u00e2\u00c6"+
		"\u0001\u0000\u0000\u0000\u00e2\u00c7\u0001\u0000\u0000\u0000\u00e2\u00c8"+
		"\u0001\u0000\u0000\u0000\u00e2\u00c9\u0001\u0000\u0000\u0000\u00e2\u00ca"+
		"\u0001\u0000\u0000\u0000\u00e2\u00cb\u0001\u0000\u0000\u0000\u00e2\u00cc"+
		"\u0001\u0000\u0000\u0000\u00e2\u00cd\u0001\u0000\u0000\u0000\u00e2\u00cf"+
		"\u0001\u0000\u0000\u0000\u00e2\u00d1\u0001\u0000\u0000\u0000\u00e2\u00d3"+
		"\u0001\u0000\u0000\u0000\u00e2\u00d4\u0001\u0000\u0000\u0000\u00e2\u00d5"+
		"\u0001\u0000\u0000\u0000\u00e2\u00d6\u0001\u0000\u0000\u0000\u00e2\u00d7"+
		"\u0001\u0000\u0000\u0000\u00e2\u00d8\u0001\u0000\u0000\u0000\u00e2\u00d9"+
		"\u0001\u0000\u0000\u0000\u00e2\u00da\u0001\u0000\u0000\u0000\u00e2\u00db"+
		"\u0001\u0000\u0000\u0000\u00e2\u00df\u0001\u0000\u0000\u0000\u00e2\u00e0"+
		"\u0001\u0000\u0000\u0000\u00e2\u00e1\u0001\u0000\u0000\u0000\u00e3\u010a"+
		"\u0001\u0000\u0000\u0000\u00e4\u00e5\n\u0018\u0000\u0000\u00e5\u00e6\u0005"+
		"+\u0000\u0000\u00e6\u0109\u0003\u0010\b\u0019\u00e7\u00e8\n\u0016\u0000"+
		"\u0000\u00e8\u00e9\u0007\u0000\u0000\u0000\u00e9\u0109\u0003\u0010\b\u0017"+
		"\u00ea\u00eb\n\u0015\u0000\u0000\u00eb\u00ec\u0007\u0001\u0000\u0000\u00ec"+
		"\u0109\u0003\u0010\b\u0016\u00ed\u00ee\n\u0014\u0000\u0000\u00ee\u00ef"+
		"\u0007\u0002\u0000\u0000\u00ef\u0109\u0003\u0010\b\u0015\u00f0\u00f1\n"+
		"\u0013\u0000\u0000\u00f1\u00f2\u0005Q\u0000\u0000\u00f2\u0109\u0003\u0010"+
		"\b\u0014\u00f3\u00f4\n\u0012\u0000\u0000\u00f4\u00f5\u0007\u0003\u0000"+
		"\u0000\u00f5\u0109\u0003\u0010\b\u0013\u00f6\u00f7\n\u0011\u0000\u0000"+
		"\u00f7\u00f8\u0007\u0004\u0000\u0000\u00f8\u0109\u0003\u0010\b\u0012\u00f9"+
		"\u00fa\n\u0010\u0000\u0000\u00fa\u00fb\u0007\u0005\u0000\u0000\u00fb\u0109"+
		"\u0003\u0010\b\u0011\u00fc\u00fd\n\u000f\u0000\u0000\u00fd\u00fe\u0005"+
		"M\u0000\u0000\u00fe\u0109\u0003\u0010\b\u0010\u00ff\u0100\n\u000e\u0000"+
		"\u0000\u0100\u0101\u0005N\u0000\u0000\u0101\u0109\u0003\u0010\b\u000f"+
		"\u0102\u0103\n\r\u0000\u0000\u0103\u0104\u00052\u0000\u0000\u0104\u0105"+
		"\u0003\u0010\b\u0000\u0105\u0106\u0005%\u0000\u0000\u0106\u0107\u0003"+
		"\u0010\b\u0000\u0107\u0109\u0001\u0000\u0000\u0000\u0108\u00e4\u0001\u0000"+
		"\u0000\u0000\u0108\u00e7\u0001\u0000\u0000\u0000\u0108\u00ea\u0001\u0000"+
		"\u0000\u0000\u0108\u00ed\u0001\u0000\u0000\u0000\u0108\u00f0\u0001\u0000"+
		"\u0000\u0000\u0108\u00f3\u0001\u0000\u0000\u0000\u0108\u00f6\u0001\u0000"+
		"\u0000\u0000\u0108\u00f9\u0001\u0000\u0000\u0000\u0108\u00fc\u0001\u0000"+
		"\u0000\u0000\u0108\u00ff\u0001\u0000\u0000\u0000\u0108\u0102\u0001\u0000"+
		"\u0000\u0000\u0109\u010c\u0001\u0000\u0000\u0000\u010a\u0108\u0001\u0000"+
		"\u0000\u0000\u010a\u010b\u0001\u0000\u0000\u0000\u010b\u0011\u0001\u0000"+
		"\u0000\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010d\u010e\u00054\u0000"+
		"\u0000\u010e\u010f\u0005`\u0000\u0000\u010f\u0110\u0005#\u0000\u0000\u0110"+
		"\u0124\u0005]\u0000\u0000\u0111\u0112\u00054\u0000\u0000\u0112\u0113\u0005"+
		"`\u0000\u0000\u0113\u0114\u0005#\u0000\u0000\u0114\u0124\u0003z=\u0000"+
		"\u0115\u0116\u00054\u0000\u0000\u0116\u0117\u0005`\u0000\u0000\u0117\u0118"+
		"\u0005#\u0000\u0000\u0118\u0124\u0005`\u0000\u0000\u0119\u011a\u00054"+
		"\u0000\u0000\u011a\u011d\u0005`\u0000\u0000\u011b\u011c\u0005#\u0000\u0000"+
		"\u011c\u011e\u0003\u0014\n\u0000\u011d\u011b\u0001\u0000\u0000\u0000\u011d"+
		"\u011e\u0001\u0000\u0000\u0000\u011e\u0124\u0001\u0000\u0000\u0000\u011f"+
		"\u0120\u00055\u0000\u0000\u0120\u0124\u0006\t\uffff\uffff\u0000\u0121"+
		"\u0122\u00056\u0000\u0000\u0122\u0124\u0006\t\uffff\uffff\u0000\u0123"+
		"\u010d\u0001\u0000\u0000\u0000\u0123\u0111\u0001\u0000\u0000\u0000\u0123"+
		"\u0115\u0001\u0000\u0000\u0000\u0123\u0119\u0001\u0000\u0000\u0000\u0123"+
		"\u011f\u0001\u0000\u0000\u0000\u0123\u0121\u0001\u0000\u0000\u0000\u0124"+
		"\u0013\u0001\u0000\u0000\u0000\u0125\u012a\u0003\u0080@\u0000\u0126\u0127"+
		"\u0005&\u0000\u0000\u0127\u0129\u0003\u0080@\u0000\u0128\u0126\u0001\u0000"+
		"\u0000\u0000\u0129\u012c\u0001\u0000\u0000\u0000\u012a\u0128\u0001\u0000"+
		"\u0000\u0000\u012a\u012b\u0001\u0000\u0000\u0000\u012b\u0015\u0001\u0000"+
		"\u0000\u0000\u012c\u012a\u0001\u0000\u0000\u0000\u012d\u012e\u0005\u001a"+
		"\u0000\u0000\u012e\u0132\u0003\u0080@\u0000\u012f\u0130\u0005\u001a\u0000"+
		"\u0000\u0130\u0132\u0003t:\u0000\u0131\u012d\u0001\u0000\u0000\u0000\u0131"+
		"\u012f\u0001\u0000\u0000\u0000\u0132\u0017\u0001\u0000\u0000\u0000\u0133"+
		"\u0134\u0003l6\u0000\u0134\u0135\u0005*\u0000\u0000\u0135\u0136\u0005"+
		"\u001b\u0000\u0000\u0136\u0137\u0005V\u0000\u0000\u0137\u0138\u0003\u0010"+
		"\b\u0000\u0138\u0139\u0005W\u0000\u0000\u0139\u0019\u0001\u0000\u0000"+
		"\u0000\u013a\u013b\u0003l6\u0000\u013b\u013c\u0005*\u0000\u0000\u013c"+
		"\u013d\u0003\u0080@\u0000\u013d\u0143\u0001\u0000\u0000\u0000\u013e\u013f"+
		"\u0003j5\u0000\u013f\u0140\u0005*\u0000\u0000\u0140\u0141\u0003\u0080"+
		"@\u0000\u0141\u0143\u0001\u0000\u0000\u0000\u0142\u013a\u0001\u0000\u0000"+
		"\u0000\u0142\u013e\u0001\u0000\u0000\u0000\u0143\u001b\u0001\u0000\u0000"+
		"\u0000\u0144\u0145\u0003l6\u0000\u0145\u0146\u0005*\u0000\u0000\u0146"+
		"\u0147\u0003\u001e\u000f\u0000\u0147\u014d\u0001\u0000\u0000\u0000\u0148"+
		"\u0149\u0003j5\u0000\u0149\u014a\u0005*\u0000\u0000\u014a\u014b\u0003"+
		"\u001e\u000f\u0000\u014b\u014d\u0001\u0000\u0000\u0000\u014c\u0144\u0001"+
		"\u0000\u0000\u0000\u014c\u0148\u0001\u0000\u0000\u0000\u014d\u001d\u0001"+
		"\u0000\u0000\u0000\u014e\u0151\u0003j5\u0000\u014f\u0151\u0003l6\u0000"+
		"\u0150\u014e\u0001\u0000\u0000\u0000\u0150\u014f\u0001\u0000\u0000\u0000"+
		"\u0151\u001f\u0001\u0000\u0000\u0000\u0152\u0155\u0003r9\u0000\u0153\u0155"+
		"\u0003p8\u0000\u0154\u0152\u0001\u0000\u0000\u0000\u0154\u0153\u0001\u0000"+
		"\u0000\u0000\u0155!\u0001\u0000\u0000\u0000\u0156\u0157\u0005\u0001\u0000"+
		"\u0000\u0157\u0158\u0003$\u0012\u0000\u0158#\u0001\u0000\u0000\u0000\u0159"+
		"\u015a\u0006\u0012\uffff\uffff\u0000\u015a\u015b\u0007\u0006\u0000\u0000"+
		"\u015b\u015c\u0005\u0012\u0000\u0000\u015c\u015e\u0003\u0080@\u0000\u015d"+
		"\u015f\u0003&\u0013\u0000\u015e\u015d\u0001\u0000\u0000\u0000\u015e\u015f"+
		"\u0001\u0000\u0000\u0000\u015f\u016a\u0001\u0000\u0000\u0000\u0160\u0161"+
		"\n\u0001\u0000\u0000\u0161\u0162\u0005&\u0000\u0000\u0162\u0163\u0007"+
		"\u0006\u0000\u0000\u0163\u0164\u0005\u0012\u0000\u0000\u0164\u0166\u0003"+
		"\u0080@\u0000\u0165\u0167\u0003&\u0013\u0000\u0166\u0165\u0001\u0000\u0000"+
		"\u0000\u0166\u0167\u0001\u0000\u0000\u0000\u0167\u0169\u0001\u0000\u0000"+
		"\u0000\u0168\u0160\u0001\u0000\u0000\u0000\u0169\u016c\u0001\u0000\u0000"+
		"\u0000\u016a\u0168\u0001\u0000\u0000\u0000\u016a\u016b\u0001\u0000\u0000"+
		"\u0000\u016b%\u0001\u0000\u0000\u0000\u016c\u016a\u0001\u0000\u0000\u0000"+
		"\u016d\u016e\u0005T\u0000\u0000\u016e\u016f\u0003F#\u0000\u016f\u0170"+
		"\u0005U\u0000\u0000\u0170\'\u0001\u0000\u0000\u0000\u0171\u0172\u0005"+
		"\u0005\u0000\u0000\u0172\u0173\u0003\u0080@\u0000\u0173\u0174\u0005#\u0000"+
		"\u0000\u0174\u0175\u0003\u0010\b\u0000\u0175)\u0001\u0000\u0000\u0000"+
		"\u0176\u0177\u0005\u0018\u0000\u0000\u0177\u0178\u0003\u0080@\u0000\u0178"+
		"\u0179\u0005#\u0000\u0000\u0179\u017a\u0003\u0010\b\u0000\u017a+\u0001"+
		"\u0000\u0000\u0000\u017b\u017d\u0007\u0007\u0000\u0000\u017c\u017b\u0001"+
		"\u0000\u0000\u0000\u017c\u017d\u0001\u0000\u0000\u0000\u017d\u017e\u0001"+
		"\u0000\u0000\u0000\u017e\u017f\u0007\b\u0000\u0000\u017f\u0180\u0003\u0080"+
		"@\u0000\u0180\u0181\u0005V\u0000\u0000\u0181\u0182\u00030\u0018\u0000"+
		"\u0182\u0183\u0005W\u0000\u0000\u0183\u0184\u0003\u0002\u0001\u0000\u0184"+
		"\u0185\u0005\r\u0000\u0000\u0185\u019a\u0001\u0000\u0000\u0000\u0186\u0188"+
		"\u0007\u0007\u0000\u0000\u0187\u0186\u0001\u0000\u0000\u0000\u0187\u0188"+
		"\u0001\u0000\u0000\u0000\u0188\u0189\u0001\u0000\u0000\u0000\u0189\u018a"+
		"\u0007\b\u0000\u0000\u018a\u018b\u0003\u0080@\u0000\u018b\u018c\u0005"+
		"V\u0000\u0000\u018c\u018d\u0005W\u0000\u0000\u018d\u018e\u0003\u0002\u0001"+
		"\u0000\u018e\u018f\u0005\r\u0000\u0000\u018f\u019a\u0001\u0000\u0000\u0000"+
		"\u0190\u0192\u0004\u0016\u0013\u0000\u0191\u0193\u0007\u0007\u0000\u0000"+
		"\u0192\u0191\u0001\u0000\u0000\u0000\u0192\u0193\u0001\u0000\u0000\u0000"+
		"\u0193\u0194\u0001\u0000\u0000\u0000\u0194\u0195\u0007\b\u0000\u0000\u0195"+
		"\u0196\u0003\u0080@\u0000\u0196\u0197\u0003\u0002\u0001\u0000\u0197\u0198"+
		"\u0005\r\u0000\u0000\u0198\u019a\u0001\u0000\u0000\u0000\u0199\u017c\u0001"+
		"\u0000\u0000\u0000\u0199\u0187\u0001\u0000\u0000\u0000\u0199\u0190\u0001"+
		"\u0000\u0000\u0000\u019a-\u0001\u0000\u0000\u0000\u019b\u019d\u0005\u0012"+
		"\u0000\u0000\u019c\u019b\u0001\u0000\u0000\u0000\u019c\u019d\u0001\u0000"+
		"\u0000\u0000\u019d\u019f\u0001\u0000\u0000\u0000\u019e\u01a0\u0005\u0017"+
		"\u0000\u0000\u019f\u019e\u0001\u0000\u0000\u0000\u019f\u01a0\u0001\u0000"+
		"\u0000\u0000\u01a0\u01a1\u0001\u0000\u0000\u0000\u01a1\u01a5\u0003l6\u0000"+
		"\u01a2\u01a3\u0005*\u0000\u0000\u01a3\u01a4\u0005*\u0000\u0000\u01a4\u01a6"+
		"\u0005*\u0000\u0000\u01a5\u01a2\u0001\u0000\u0000\u0000\u01a5\u01a6\u0001"+
		"\u0000\u0000\u0000\u01a6\u01b0\u0001\u0000\u0000\u0000\u01a7\u01a8\u0005"+
		"\u0017\u0000\u0000\u01a8\u01a9\u0005\u0012\u0000\u0000\u01a9\u01ad\u0003"+
		"l6\u0000\u01aa\u01ab\u0005*\u0000\u0000\u01ab\u01ac\u0005*\u0000\u0000"+
		"\u01ac\u01ae\u0005*\u0000\u0000\u01ad\u01aa\u0001\u0000\u0000\u0000\u01ad"+
		"\u01ae\u0001\u0000\u0000\u0000\u01ae\u01b0\u0001\u0000\u0000\u0000\u01af"+
		"\u019c\u0001\u0000\u0000\u0000\u01af\u01a7\u0001\u0000\u0000\u0000\u01b0"+
		"/\u0001\u0000\u0000\u0000\u01b1\u01b6\u0003.\u0017\u0000\u01b2\u01b3\u0005"+
		"&\u0000\u0000\u01b3\u01b5\u0003.\u0017\u0000\u01b4\u01b2\u0001\u0000\u0000"+
		"\u0000\u01b5\u01b8\u0001\u0000\u0000\u0000\u01b6\u01b4\u0001\u0000\u0000"+
		"\u0000\u01b6\u01b7\u0001\u0000\u0000\u0000\u01b71\u0001\u0000\u0000\u0000"+
		"\u01b8\u01b6\u0001\u0000\u0000\u0000\u01b9\u01ba\u0003d2\u0000\u01ba\u01bb"+
		"\u0005%\u0000\u0000\u01bb\u01bd\u0001\u0000\u0000\u0000\u01bc\u01b9\u0001"+
		"\u0000\u0000\u0000\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01be\u0001"+
		"\u0000\u0000\u0000\u01be\u01bf\u0005\"\u0000\u0000\u01bf\u01c0\u0003\u0010"+
		"\b\u0000\u01c0\u01c1\u0003\f\u0006\u0000\u01c1\u01c2\u0003>\u001f\u0000"+
		"\u01c2\u01c3\u0005\r\u0000\u0000\u01c33\u0001\u0000\u0000\u0000\u01c4"+
		"\u01c5\u0003d2\u0000\u01c5\u01c6\u0005%\u0000\u0000\u01c6\u01c8\u0001"+
		"\u0000\u0000\u0000\u01c7\u01c4\u0001\u0000\u0000\u0000\u01c7\u01c8\u0001"+
		"\u0000\u0000\u0000\u01c8\u01c9\u0001\u0000\u0000\u0000\u01c9\u01ca\u0005"+
		"\b\u0000\u0000\u01ca\u01cb\u0003>\u001f\u0000\u01cb\u01cc\u0005\u0014"+
		"\u0000\u0000\u01cc\u01cd\u0005\"\u0000\u0000\u01cd\u01ce\u0003\u0010\b"+
		"\u0000\u01ce5\u0001\u0000\u0000\u0000\u01cf\u01d0\u0003d2\u0000\u01d0"+
		"\u01d1\u0005%\u0000\u0000\u01d1\u01d3\u0001\u0000\u0000\u0000\u01d2\u01cf"+
		"\u0001\u0000\u0000\u0000\u01d2\u01d3\u0001\u0000\u0000\u0000\u01d3\u01d4"+
		"\u0001\u0000\u0000\u0000\u01d4\u01d5\u0005\u000f\u0000\u0000\u01d5\u01d6"+
		"\u0003b1\u0000\u01d6\u01d7\u0005\u0012\u0000\u0000\u01d7\u01d8\u0003F"+
		"#\u0000\u01d8\u01d9\u0003\f\u0006\u0000\u01d9\u01da\u0003>\u001f\u0000"+
		"\u01da\u01db\u0005\r\u0000\u0000\u01db\u0208\u0001\u0000\u0000\u0000\u01dc"+
		"\u01dd\u0003d2\u0000\u01dd\u01de\u0005%\u0000\u0000\u01de\u01e0\u0001"+
		"\u0000\u0000\u0000\u01df\u01dc\u0001\u0000\u0000\u0000\u01df\u01e0\u0001"+
		"\u0000\u0000\u0000\u01e0\u01e1\u0001\u0000\u0000\u0000\u01e1\u01e2\u0005"+
		"\u000f\u0000\u0000\u01e2\u01e3\u0003H$\u0000\u01e3\u01e4\u00053\u0000"+
		"\u0000\u01e4\u01e5\u0003\u0010\b\u0000\u01e5\u01e6\u00053\u0000\u0000"+
		"\u01e6\u01e7\u0003J%\u0000\u01e7\u01e8\u0003\f\u0006\u0000\u01e8\u01e9"+
		"\u0003>\u001f\u0000\u01e9\u01ea\u0005\r\u0000\u0000\u01ea\u0208\u0001"+
		"\u0000\u0000\u0000\u01eb\u01ec\u0003d2\u0000\u01ec\u01ed\u0005%\u0000"+
		"\u0000\u01ed\u01ef\u0001\u0000\u0000\u0000\u01ee\u01eb\u0001\u0000\u0000"+
		"\u0000\u01ee\u01ef\u0001\u0000\u0000\u0000\u01ef\u01f0\u0001\u0000\u0000"+
		"\u0000\u01f0\u01f1\u0005\u000f\u0000\u0000\u01f1\u01f2\u0003:\u001d\u0000"+
		"\u01f2\u01f3\u0005\u0012\u0000\u0000\u01f3\u01f4\u0005V\u0000\u0000\u01f4"+
		"\u01f5\u0003<\u001e\u0000\u01f5\u01f6\u0005W\u0000\u0000\u01f6\u01f7\u0003"+
		"\f\u0006\u0000\u01f7\u01f8\u0003>\u001f\u0000\u01f8\u01f9\u0005\r\u0000"+
		"\u0000\u01f9\u0208\u0001\u0000\u0000\u0000\u01fa\u01fb\u0003d2\u0000\u01fb"+
		"\u01fc\u0005%\u0000\u0000\u01fc\u01fe\u0001\u0000\u0000\u0000\u01fd\u01fa"+
		"\u0001\u0000\u0000\u0000\u01fd\u01fe\u0001\u0000\u0000\u0000\u01fe\u01ff"+
		"\u0001\u0000\u0000\u0000\u01ff\u0200\u0005\u000f\u0000\u0000\u0200\u0201"+
		"\u0003:\u001d\u0000\u0201\u0202\u0005\u0012\u0000\u0000\u0202\u0203\u0003"+
		"<\u001e\u0000\u0203\u0204\u0005\b\u0000\u0000\u0204\u0205\u0003>\u001f"+
		"\u0000\u0205\u0206\u0005\r\u0000\u0000\u0206\u0208\u0001\u0000\u0000\u0000"+
		"\u0207\u01d2\u0001\u0000\u0000\u0000\u0207\u01df\u0001\u0000\u0000\u0000"+
		"\u0207\u01ee\u0001\u0000\u0000\u0000\u0207\u01fd\u0001\u0000\u0000\u0000"+
		"\u02087\u0001\u0000\u0000\u0000\u0209\u020b\u0005\u0017\u0000\u0000\u020a"+
		"\u0209\u0001\u0000\u0000\u0000\u020a\u020b\u0001\u0000\u0000\u0000\u020b"+
		"\u020c\u0001\u0000\u0000\u0000\u020c\u020d\u0003b1\u0000\u020d9\u0001"+
		"\u0000\u0000\u0000\u020e\u0213\u00038\u001c\u0000\u020f\u0210\u0005&\u0000"+
		"\u0000\u0210\u0212\u00038\u001c\u0000\u0211\u020f\u0001\u0000\u0000\u0000"+
		"\u0212\u0215\u0001\u0000\u0000\u0000\u0213\u0211\u0001\u0000\u0000\u0000"+
		"\u0213\u0214\u0001\u0000\u0000\u0000\u0214;\u0001\u0000\u0000\u0000\u0215"+
		"\u0213\u0001\u0000\u0000\u0000\u0216\u021b\u0003\u0010\b\u0000\u0217\u0218"+
		"\u0005&\u0000\u0000\u0218\u021a\u0003\u0010\b\u0000\u0219\u0217\u0001"+
		"\u0000\u0000\u0000\u021a\u021d\u0001\u0000\u0000\u0000\u021b\u0219\u0001"+
		"\u0000\u0000\u0000\u021b\u021c\u0001\u0000\u0000\u0000\u021c=\u0001\u0000"+
		"\u0000\u0000\u021d\u021b\u0001\u0000\u0000\u0000\u021e\u021f\u0003\u0002"+
		"\u0001\u0000\u021f?\u0001\u0000\u0000\u0000\u0220\u0222\u0005\u0006\u0000"+
		"\u0000\u0221\u0223\u0003d2\u0000\u0222\u0221\u0001\u0000\u0000\u0000\u0222"+
		"\u0223\u0001\u0000\u0000\u0000\u0223A\u0001\u0000\u0000\u0000\u0224\u0226"+
		"\u0005\u0003\u0000\u0000\u0225\u0227\u0003d2\u0000\u0226\u0225\u0001\u0000"+
		"\u0000\u0000\u0226\u0227\u0001\u0000\u0000\u0000\u0227C\u0001\u0000\u0000"+
		"\u0000\u0228\u022a\u0005\u0019\u0000\u0000\u0229\u022b\u0003\u0010\b\u0000"+
		"\u022a\u0229\u0001\u0000\u0000\u0000\u022a\u022b\u0001\u0000\u0000\u0000"+
		"\u022bE\u0001\u0000\u0000\u0000\u022c\u022d\u0003\u0010\b\u0000\u022d"+
		"\u022e\u0005*\u0000\u0000\u022e\u022f\u0005*\u0000\u0000\u022f\u0230\u0003"+
		"\u0010\b\u0000\u0230\u0238\u0001\u0000\u0000\u0000\u0231\u0232\u0003\u0010"+
		"\b\u0000\u0232\u0233\u0005*\u0000\u0000\u0233\u0234\u0005*\u0000\u0000"+
		"\u0234\u0235\u0005*\u0000\u0000\u0235\u0236\u0003\u0010\b\u0000\u0236"+
		"\u0238\u0001\u0000\u0000\u0000\u0237\u022c\u0001\u0000\u0000\u0000\u0237"+
		"\u0231\u0001\u0000\u0000\u0000\u0238G\u0001\u0000\u0000\u0000\u0239\u023a"+
		"\u0006$\uffff\uffff\u0000\u023a\u023b\u0003\u0010\b\u0000\u023b\u0241"+
		"\u0001\u0000\u0000\u0000\u023c\u023d\n\u0001\u0000\u0000\u023d\u023e\u0005"+
		"&\u0000\u0000\u023e\u0240\u0003\u0010\b\u0000\u023f\u023c\u0001\u0000"+
		"\u0000\u0000\u0240\u0243\u0001\u0000\u0000\u0000\u0241\u023f\u0001\u0000"+
		"\u0000\u0000\u0241\u0242\u0001\u0000\u0000\u0000\u0242I\u0001\u0000\u0000"+
		"\u0000\u0243\u0241\u0001\u0000\u0000\u0000\u0244\u0245\u0006%\uffff\uffff"+
		"\u0000\u0245\u0246\u0003\u0010\b\u0000\u0246\u024c\u0001\u0000\u0000\u0000"+
		"\u0247\u0248\n\u0001\u0000\u0000\u0248\u0249\u0005&\u0000\u0000\u0249"+
		"\u024b\u0003\u0010\b\u0000\u024a\u0247\u0001\u0000\u0000\u0000\u024b\u024e"+
		"\u0001\u0000\u0000\u0000\u024c\u024a\u0001\u0000\u0000\u0000\u024c\u024d"+
		"\u0001\u0000\u0000\u0000\u024dK\u0001\u0000\u0000\u0000\u024e\u024c\u0001"+
		"\u0000\u0000\u0000\u024f\u0250\u0005\r\u0000\u0000\u0250\u0251\u0005V"+
		"\u0000\u0000\u0251\u0261\u0005W\u0000\u0000\u0252\u0253\u0003\u0080@\u0000"+
		"\u0253\u0254\u0005V\u0000\u0000\u0254\u0255\u0005W\u0000\u0000\u0255\u0261"+
		"\u0001\u0000\u0000\u0000\u0256\u0257\u0003\u0080@\u0000\u0257\u0258\u0005"+
		"V\u0000\u0000\u0258\u0259\u0003P(\u0000\u0259\u025a\u0005W\u0000\u0000"+
		"\u025a\u0261\u0001\u0000\u0000\u0000\u025b\u025c\u0003\u001a\r\u0000\u025c"+
		"\u025d\u0005V\u0000\u0000\u025d\u025e\u0003P(\u0000\u025e\u025f\u0005"+
		"W\u0000\u0000\u025f\u0261\u0001\u0000\u0000\u0000\u0260\u024f\u0001\u0000"+
		"\u0000\u0000\u0260\u0252\u0001\u0000\u0000\u0000\u0260\u0256\u0001\u0000"+
		"\u0000\u0000\u0260\u025b\u0001\u0000\u0000\u0000\u0261M\u0001\u0000\u0000"+
		"\u0000\u0262\u0264\u0005\u0012\u0000\u0000\u0263\u0262\u0001\u0000\u0000"+
		"\u0000\u0263\u0264\u0001\u0000\u0000\u0000\u0264\u0266\u0001\u0000\u0000"+
		"\u0000\u0265\u0267\u0005\u0017\u0000\u0000\u0266\u0265\u0001\u0000\u0000"+
		"\u0000\u0266\u0267\u0001\u0000\u0000\u0000\u0267\u0269\u0001\u0000\u0000"+
		"\u0000\u0268\u026a\u0003\u0010\b\u0000\u0269\u0268\u0001\u0000\u0000\u0000"+
		"\u0269\u026a\u0001\u0000\u0000\u0000\u026a\u026f\u0001\u0000\u0000\u0000"+
		"\u026b\u026c\u0005\u0017\u0000\u0000\u026c\u026d\u0005\u0012\u0000\u0000"+
		"\u026d\u026f\u0003\u0010\b\u0000\u026e\u0263\u0001\u0000\u0000\u0000\u026e"+
		"\u026b\u0001\u0000\u0000\u0000\u026fO\u0001\u0000\u0000\u0000\u0270\u0275"+
		"\u0003N\'\u0000\u0271\u0272\u0005&\u0000\u0000\u0272\u0274\u0003N\'\u0000"+
		"\u0273\u0271\u0001\u0000\u0000\u0000\u0274\u0277\u0001\u0000\u0000\u0000"+
		"\u0275\u0273\u0001\u0000\u0000\u0000\u0275\u0276\u0001\u0000\u0000\u0000"+
		"\u0276Q\u0001\u0000\u0000\u0000\u0277\u0275\u0001\u0000\u0000\u0000\u0278"+
		"\u0279\u0005\u0011\u0000\u0000\u0279\u027a\u0003\u0010\b\u0000\u027a\u027c"+
		"\u0003\u000e\u0007\u0000\u027b\u027d\u0003\u0002\u0001\u0000\u027c\u027b"+
		"\u0001\u0000\u0000\u0000\u027c\u027d\u0001\u0000\u0000\u0000\u027d\u027f"+
		"\u0001\u0000\u0000\u0000\u027e\u0280\u0003T*\u0000\u027f\u027e\u0001\u0000"+
		"\u0000\u0000\u027f\u0280\u0001\u0000\u0000\u0000\u0280\u0281\u0001\u0000"+
		"\u0000\u0000\u0281\u0282\u0005\r\u0000\u0000\u0282S\u0001\u0000\u0000"+
		"\u0000\u0283\u0285\u0005\n\u0000\u0000\u0284\u0286\u0003\u0002\u0001\u0000"+
		"\u0285\u0284\u0001\u0000\u0000\u0000\u0285\u0286\u0001\u0000\u0000\u0000"+
		"\u0286\u0291\u0001\u0000\u0000\u0000\u0287\u0288\u0005\f\u0000\u0000\u0288"+
		"\u0289\u0003\u0010\b\u0000\u0289\u028b\u0003\u000e\u0007\u0000\u028a\u028c"+
		"\u0003\u0002\u0001\u0000\u028b\u028a\u0001\u0000\u0000\u0000\u028b\u028c"+
		"\u0001\u0000\u0000\u0000\u028c\u028e\u0001\u0000\u0000\u0000\u028d\u028f"+
		"\u0003T*\u0000\u028e\u028d\u0001\u0000\u0000\u0000\u028e\u028f\u0001\u0000"+
		"\u0000\u0000\u028f\u0291\u0001\u0000\u0000\u0000\u0290\u0283\u0001\u0000"+
		"\u0000\u0000\u0290\u0287\u0001\u0000\u0000\u0000\u0291U\u0001\u0000\u0000"+
		"\u0000\u0292\u0293\u0005\u0004\u0000\u0000\u0293\u0295\u0003\u0010\b\u0000"+
		"\u0294\u0296\u0003X,\u0000\u0295\u0294\u0001\u0000\u0000\u0000\u0295\u0296"+
		"\u0001\u0000\u0000\u0000\u0296\u0299\u0001\u0000\u0000\u0000\u0297\u0298"+
		"\u0005\n\u0000\u0000\u0298\u029a\u0003\u0002\u0001\u0000\u0299\u0297\u0001"+
		"\u0000\u0000\u0000\u0299\u029a\u0001\u0000\u0000\u0000\u029a\u029b\u0001"+
		"\u0000\u0000\u0000\u029b\u029c\u0005\r\u0000\u0000\u029cW\u0001\u0000"+
		"\u0000\u0000\u029d\u029e\u0006,\uffff\uffff\u0000\u029e\u029f\u0003Z-"+
		"\u0000\u029f\u02a4\u0001\u0000\u0000\u0000\u02a0\u02a1\n\u0001\u0000\u0000"+
		"\u02a1\u02a3\u0003Z-\u0000\u02a2\u02a0\u0001\u0000\u0000\u0000\u02a3\u02a6"+
		"\u0001\u0000\u0000\u0000\u02a4\u02a2\u0001\u0000\u0000\u0000\u02a4\u02a5"+
		"\u0001\u0000\u0000\u0000\u02a5Y\u0001\u0000\u0000\u0000\u02a6\u02a4\u0001"+
		"\u0000\u0000\u0000\u02a7\u02a8\u0005!\u0000\u0000\u02a8\u02a9\u0003^/"+
		"\u0000\u02a9\u02ab\u0003\u000e\u0007\u0000\u02aa\u02ac\u0003\u0002\u0001"+
		"\u0000\u02ab\u02aa\u0001\u0000\u0000\u0000\u02ab\u02ac\u0001\u0000\u0000"+
		"\u0000\u02ac[\u0001\u0000\u0000\u0000\u02ad\u02b0\u0003\u0010\b\u0000"+
		"\u02ae\u02b0\u0003F#\u0000\u02af\u02ad\u0001\u0000\u0000\u0000\u02af\u02ae"+
		"\u0001\u0000\u0000\u0000\u02b0]\u0001\u0000\u0000\u0000\u02b1\u02b2\u0006"+
		"/\uffff\uffff\u0000\u02b2\u02b3\u0003\\.\u0000\u02b3\u02b9\u0001\u0000"+
		"\u0000\u0000\u02b4\u02b5\n\u0001\u0000\u0000\u02b5\u02b6\u0005&\u0000"+
		"\u0000\u02b6\u02b8\u0003\\.\u0000\u02b7\u02b4\u0001\u0000\u0000\u0000"+
		"\u02b8\u02bb\u0001\u0000\u0000\u0000\u02b9\u02b7\u0001\u0000\u0000\u0000"+
		"\u02b9\u02ba\u0001\u0000\u0000\u0000\u02ba_\u0001\u0000\u0000\u0000\u02bb"+
		"\u02b9\u0001\u0000\u0000\u0000\u02bc\u02bd\u0003b1\u0000\u02bd\u02be\u0005"+
		"#\u0000\u0000\u02be\u02bf\u0003\u0010\b\u0000\u02bf\u02c5\u0001\u0000"+
		"\u0000\u0000\u02c0\u02c1\u0003b1\u0000\u02c1\u02c2\u0007\t\u0000\u0000"+
		"\u02c2\u02c3\u0003\u0010\b\u0000\u02c3\u02c5\u0001\u0000\u0000\u0000\u02c4"+
		"\u02bc\u0001\u0000\u0000\u0000\u02c4\u02c0\u0001\u0000\u0000\u0000\u02c5"+
		"a\u0001\u0000\u0000\u0000\u02c6\u02cc\u0003j5\u0000\u02c7\u02cc\u0003"+
		"h4\u0000\u02c8\u02cc\u0003f3\u0000\u02c9\u02cc\u0003l6\u0000\u02ca\u02cc"+
		"\u0003\u001a\r\u0000\u02cb\u02c6\u0001\u0000\u0000\u0000\u02cb\u02c7\u0001"+
		"\u0000\u0000\u0000\u02cb\u02c8\u0001\u0000\u0000\u0000\u02cb\u02c9\u0001"+
		"\u0000\u0000\u0000\u02cb\u02ca\u0001\u0000\u0000\u0000\u02ccc\u0001\u0000"+
		"\u0000\u0000\u02cd\u02ce\u0005`\u0000\u0000\u02cee\u0001\u0000\u0000\u0000"+
		"\u02cf\u02d0\u0003\u0080@\u0000\u02d0\u02d1\u0005T\u0000\u0000\u02d1\u02d2"+
		"\u0003\u0010\b\u0000\u02d2\u02d3\u0005U\u0000\u0000\u02d3g\u0001\u0000"+
		"\u0000\u0000\u02d4\u02d5\u0005)\u0000\u0000\u02d5\u02d6\u0003\u0080@\u0000"+
		"\u02d6i\u0001\u0000\u0000\u0000\u02d7\u02d8\u0005$\u0000\u0000\u02d8\u02d9"+
		"\u0003n7\u0000\u02d9k\u0001\u0000\u0000\u0000\u02da\u02db\u0003\u0080"+
		"@\u0000\u02dbm\u0001\u0000\u0000\u0000\u02dc\u02dd\u0005`\u0000\u0000"+
		"\u02ddo\u0001\u0000\u0000\u0000\u02de\u02e2\u0003\u0082A\u0000\u02df\u02e2"+
		"\u0003\u0084B\u0000\u02e0\u02e2\u0003\u0086C\u0000\u02e1\u02de\u0001\u0000"+
		"\u0000\u0000\u02e1\u02df\u0001\u0000\u0000\u0000\u02e1\u02e0\u0001\u0000"+
		"\u0000\u0000\u02e2q\u0001\u0000\u0000\u0000\u02e3\u02e4\u0005\\\u0000"+
		"\u0000\u02e4s\u0001\u0000\u0000\u0000\u02e5\u02e6\u0005[\u0000\u0000\u02e6"+
		"u\u0001\u0000\u0000\u0000\u02e7\u02e8\u0005Z\u0000\u0000\u02e8w\u0001"+
		"\u0000\u0000\u0000\u02e9\u02ea\u0005\u0016\u0000\u0000\u02eay\u0001\u0000"+
		"\u0000\u0000\u02eb\u02ee\u0003|>\u0000\u02ec\u02ee\u0003~?\u0000\u02ed"+
		"\u02eb\u0001\u0000\u0000\u0000\u02ed\u02ec\u0001\u0000\u0000\u0000\u02ee"+
		"{\u0001\u0000\u0000\u0000\u02ef\u02f0\u0005\u001e\u0000\u0000\u02f0}\u0001"+
		"\u0000\u0000\u0000\u02f1\u02f2\u0005\u000e\u0000\u0000\u02f2\u007f\u0001"+
		"\u0000\u0000\u0000\u02f3\u02f4\u0005`\u0000\u0000\u02f4\u0081\u0001\u0000"+
		"\u0000\u0000\u02f5\u02f6\u0005]\u0000\u0000\u02f6\u0083\u0001\u0000\u0000"+
		"\u0000\u02f7\u02f8\u0005^\u0000\u0000\u02f8\u0085\u0001\u0000\u0000\u0000"+
		"\u02f9\u02fa\u0005_\u0000\u0000\u02fa\u0087\u0001\u0000\u0000\u0000H\u0089"+
		"\u0094\u0099\u009f\u00a7\u00ab\u00b1\u00b3\u00b9\u00bb\u00e2\u0108\u010a"+
		"\u011d\u0123\u012a\u0131\u0142\u014c\u0150\u0154\u015e\u0166\u016a\u017c"+
		"\u0187\u0192\u0199\u019c\u019f\u01a5\u01ad\u01af\u01b6\u01bc\u01c7\u01d2"+
		"\u01df\u01ee\u01fd\u0207\u020a\u0213\u021b\u0222\u0226\u022a\u0237\u0241"+
		"\u024c\u0260\u0263\u0266\u0269\u026e\u0275\u027c\u027f\u0285\u028b\u028e"+
		"\u0290\u0295\u0299\u02a4\u02ab\u02af\u02b9\u02c4\u02cb\u02e1\u02ed";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}