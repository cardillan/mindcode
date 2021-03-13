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
		LITERAL=1, WHILE=2, DOT=3, COMMA=4, SEMICOLON=5, ASSIGN=6, PLUS_ASSIGN=7, 
		MINUS_ASSIGN=8, MUL_ASSIGN=9, DIV_ASSIGN=10, MOD_ASSIGN=11, EXP_ASSIGN=12, 
		PLUS=13, MINUS=14, MUL=15, DIV=16, MOD=17, EXP=18, COPPER=19, LEAD=20, 
		METAGLASS=21, GRAPHITE=22, SAND=23, COAL=24, TITANIUM=25, SCRAP=26, SILICON=27, 
		PYRATITE=28, WATER=29, SLAG=30, CRYOFLUID=31, TRUE=32, FALSE=33, STRICT_EQUAL=34, 
		EQUAL=35, NOT_EQUAL=36, GREATER=37, LESS=38, LESS_EQUAL=39, GREATER_EQUAL=40, 
		AND=41, OR=42, NOT=43, LEFT_RBRACKET=44, RIGHT_RBRACKET=45, LEFT_SBRACKET=46, 
		RIGHT_SBRACKET=47, LEFT_CBRACKET=48, RIGHT_CBRACKET=49, NULL=50, INT=51, 
		FLOAT=52, ID=53, CRLF=54, WS=55;
	public static final int
		RULE_program = 0, RULE_expression_list = 1, RULE_expression = 2, RULE_control_statement = 3, 
		RULE_funcall_statement = 4, RULE_params_list = 5, RULE_while_statement = 6, 
		RULE_block_body = 7, RULE_block_statement_list = 8, RULE_lvalue = 9, RULE_rvalue = 10, 
		RULE_heap_read = 11, RULE_address = 12, RULE_sensor_read = 13, RULE_assignment = 14, 
		RULE_id = 15, RULE_literal_t = 16, RULE_float_t = 17, RULE_int_t = 18, 
		RULE_bool_t = 19, RULE_null_t = 20, RULE_terminator = 21, RULE_crlf = 22, 
		RULE_resource = 23, RULE_liquid = 24, RULE_sensor = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expression_list", "expression", "control_statement", "funcall_statement", 
			"params_list", "while_statement", "block_body", "block_statement_list", 
			"lvalue", "rvalue", "heap_read", "address", "sensor_read", "assignment", 
			"id", "literal_t", "float_t", "int_t", "bool_t", "null_t", "terminator", 
			"crlf", "resource", "liquid", "sensor"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'while'", "'.'", "','", "';'", "'='", "'+='", "'-='", "'*='", 
			"'/='", "'%='", "'**='", "'+'", "'-'", "'*'", "'/'", "'%'", "'**'", "'copper'", 
			"'lead'", "'metaglass'", "'graphite'", "'sand'", "'coal'", "'titanium'", 
			"'scrap'", "'silicon'", "'pyratite'", "'water'", "'slag'", "'cryofluid'", 
			"'true'", "'false'", "'==='", "'=='", "'!='", "'>'", "'<'", "'<='", "'>='", 
			null, null, null, "'('", "')'", "'['", "']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LITERAL", "WHILE", "DOT", "COMMA", "SEMICOLON", "ASSIGN", "PLUS_ASSIGN", 
			"MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "EXP_ASSIGN", 
			"PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", "COPPER", "LEAD", "METAGLASS", 
			"GRAPHITE", "SAND", "COAL", "TITANIUM", "SCRAP", "SILICON", "PYRATITE", 
			"WATER", "SLAG", "CRYOFLUID", "TRUE", "FALSE", "STRICT_EQUAL", "EQUAL", 
			"NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", "GREATER_EQUAL", "AND", 
			"OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "NULL", "INT", "FLOAT", "ID", "CRLF", 
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

	public MindcodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
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
			setState(52);
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

	public static class Expression_listContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminatorContext terminator() {
			return getRuleContext(TerminatorContext.class,0);
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
			setState(59);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
			case WHILE:
			case TRUE:
			case FALSE:
			case NOT:
			case LEFT_RBRACKET:
			case NULL:
			case INT:
			case FLOAT:
			case ID:
				{
				setState(55);
				expression();
				setState(56);
				terminator(0);
				}
				break;
			case EOF:
			case SEMICOLON:
			case CRLF:
				{
				setState(58);
				terminator(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(67);
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
					setState(61);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(62);
					expression();
					setState(63);
					terminator(0);
					}
					} 
				}
				setState(69);
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
		public While_statementContext while_statement() {
			return getRuleContext(While_statementContext.class,0);
		}
		public Funcall_statementContext funcall_statement() {
			return getRuleContext(Funcall_statementContext.class,0);
		}
		public Control_statementContext control_statement() {
			return getRuleContext(Control_statementContext.class,0);
		}
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expression);
		try {
			setState(74);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				while_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				funcall_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(72);
				control_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(73);
				rvalue(0);
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

	public static class Control_statementContext extends ParserRuleContext {
		public IdContext target;
		public IdContext property;
		public RvalueContext value;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public Control_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_control_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterControl_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitControl_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitControl_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Control_statementContext control_statement() throws RecognitionException {
		Control_statementContext _localctx = new Control_statementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_control_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			((Control_statementContext)_localctx).target = id();
			setState(77);
			match(DOT);
			setState(78);
			((Control_statementContext)_localctx).property = id();
			setState(79);
			match(ASSIGN);
			setState(80);
			((Control_statementContext)_localctx).value = rvalue(0);
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

	public static class Funcall_statementContext extends ParserRuleContext {
		public IdContext name;
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public Params_listContext params_list() {
			return getRuleContext(Params_listContext.class,0);
		}
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Funcall_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcall_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterFuncall_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitFuncall_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitFuncall_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Funcall_statementContext funcall_statement() throws RecognitionException {
		Funcall_statementContext _localctx = new Funcall_statementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_funcall_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			((Funcall_statementContext)_localctx).name = id();
			setState(83);
			match(LEFT_RBRACKET);
			setState(84);
			params_list();
			setState(85);
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

	public static class Params_listContext extends ParserRuleContext {
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(MindcodeParser.COMMA, 0); }
		public Params_listContext params_list() {
			return getRuleContext(Params_listContext.class,0);
		}
		public Params_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_params_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterParams_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitParams_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitParams_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Params_listContext params_list() throws RecognitionException {
		Params_listContext _localctx = new Params_listContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_params_list);
		int _la;
		try {
			setState(94);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LITERAL) | (1L << TRUE) | (1L << FALSE) | (1L << NOT) | (1L << LEFT_RBRACKET) | (1L << NULL) | (1L << INT) | (1L << FLOAT) | (1L << ID))) != 0)) {
					{
					setState(87);
					rvalue(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(90);
				rvalue(0);
				setState(91);
				match(COMMA);
				setState(92);
				params_list();
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

	public static class While_statementContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public TerminalNode LEFT_CBRACKET() { return getToken(MindcodeParser.LEFT_CBRACKET, 0); }
		public Block_bodyContext block_body() {
			return getRuleContext(Block_bodyContext.class,0);
		}
		public TerminalNode RIGHT_CBRACKET() { return getToken(MindcodeParser.RIGHT_CBRACKET, 0); }
		public CrlfContext crlf() {
			return getRuleContext(CrlfContext.class,0);
		}
		public While_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_while_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterWhile_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitWhile_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitWhile_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final While_statementContext while_statement() throws RecognitionException {
		While_statementContext _localctx = new While_statementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_while_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(WHILE);
			setState(97);
			rvalue(0);
			setState(98);
			match(LEFT_CBRACKET);
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CRLF) {
				{
				setState(99);
				crlf();
				}
			}

			setState(102);
			block_body();
			setState(103);
			match(RIGHT_CBRACKET);
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

	public static class Block_bodyContext extends ParserRuleContext {
		public Block_statement_listContext block_statement_list() {
			return getRuleContext(Block_statement_listContext.class,0);
		}
		public Block_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBlock_body(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBlock_body(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBlock_body(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Block_bodyContext block_body() throws RecognitionException {
		Block_bodyContext _localctx = new Block_bodyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_block_body);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			block_statement_list(0);
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

	public static class Block_statement_listContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminatorContext terminator() {
			return getRuleContext(TerminatorContext.class,0);
		}
		public Block_statement_listContext block_statement_list() {
			return getRuleContext(Block_statement_listContext.class,0);
		}
		public Block_statement_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block_statement_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBlock_statement_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBlock_statement_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBlock_statement_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Block_statement_listContext block_statement_list() throws RecognitionException {
		return block_statement_list(0);
	}

	private Block_statement_listContext block_statement_list(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Block_statement_listContext _localctx = new Block_statement_listContext(_ctx, _parentState);
		Block_statement_listContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_block_statement_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(108);
			expression();
			setState(109);
			terminator(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(117);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Block_statement_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_block_statement_list);
					setState(111);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(112);
					expression();
					setState(113);
					terminator(0);
					}
					} 
				}
				setState(119);
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

	public static class LvalueContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
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
		enterRule(_localctx, 18, RULE_lvalue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
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

	public static class RvalueContext extends ParserRuleContext {
		public Token op;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public Literal_tContext literal_t() {
			return getRuleContext(Literal_tContext.class,0);
		}
		public Bool_tContext bool_t() {
			return getRuleContext(Bool_tContext.class,0);
		}
		public Float_tContext float_t() {
			return getRuleContext(Float_tContext.class,0);
		}
		public Int_tContext int_t() {
			return getRuleContext(Int_tContext.class,0);
		}
		public Null_tContext null_t() {
			return getRuleContext(Null_tContext.class,0);
		}
		public Sensor_readContext sensor_read() {
			return getRuleContext(Sensor_readContext.class,0);
		}
		public Heap_readContext heap_read() {
			return getRuleContext(Heap_readContext.class,0);
		}
		public List<RvalueContext> rvalue() {
			return getRuleContexts(RvalueContext.class);
		}
		public RvalueContext rvalue(int i) {
			return getRuleContext(RvalueContext.class,i);
		}
		public TerminalNode NOT() { return getToken(MindcodeParser.NOT, 0); }
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public TerminalNode EXP() { return getToken(MindcodeParser.EXP, 0); }
		public TerminalNode MUL() { return getToken(MindcodeParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(MindcodeParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(MindcodeParser.MOD, 0); }
		public TerminalNode PLUS() { return getToken(MindcodeParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public TerminalNode LESS() { return getToken(MindcodeParser.LESS, 0); }
		public TerminalNode GREATER() { return getToken(MindcodeParser.GREATER, 0); }
		public TerminalNode LESS_EQUAL() { return getToken(MindcodeParser.LESS_EQUAL, 0); }
		public TerminalNode GREATER_EQUAL() { return getToken(MindcodeParser.GREATER_EQUAL, 0); }
		public TerminalNode STRICT_EQUAL() { return getToken(MindcodeParser.STRICT_EQUAL, 0); }
		public TerminalNode EQUAL() { return getToken(MindcodeParser.EQUAL, 0); }
		public TerminalNode NOT_EQUAL() { return getToken(MindcodeParser.NOT_EQUAL, 0); }
		public TerminalNode OR() { return getToken(MindcodeParser.OR, 0); }
		public TerminalNode AND() { return getToken(MindcodeParser.AND, 0); }
		public RvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRvalue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRvalue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RvalueContext rvalue() throws RecognitionException {
		return rvalue(0);
	}

	private RvalueContext rvalue(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RvalueContext _localctx = new RvalueContext(_ctx, _parentState);
		RvalueContext _prevctx = _localctx;
		int _startState = 20;
		enterRecursionRule(_localctx, 20, RULE_rvalue, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(123);
				lvalue();
				}
				break;
			case 2:
				{
				setState(124);
				assignment();
				}
				break;
			case 3:
				{
				setState(125);
				literal_t();
				}
				break;
			case 4:
				{
				setState(126);
				bool_t();
				}
				break;
			case 5:
				{
				setState(127);
				float_t();
				}
				break;
			case 6:
				{
				setState(128);
				int_t();
				}
				break;
			case 7:
				{
				setState(129);
				null_t();
				}
				break;
			case 8:
				{
				setState(130);
				sensor_read();
				}
				break;
			case 9:
				{
				setState(131);
				heap_read();
				}
				break;
			case 10:
				{
				setState(132);
				((RvalueContext)_localctx).op = match(NOT);
				setState(133);
				rvalue(7);
				}
				break;
			case 11:
				{
				setState(134);
				match(LEFT_RBRACKET);
				setState(135);
				rvalue(0);
				setState(136);
				match(RIGHT_RBRACKET);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(160);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(158);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(140);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(141);
						((RvalueContext)_localctx).op = match(EXP);
						setState(142);
						rvalue(9);
						}
						break;
					case 2:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(143);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(144);
						((RvalueContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) ) {
							((RvalueContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(145);
						rvalue(7);
						}
						break;
					case 3:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(146);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(147);
						((RvalueContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((RvalueContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(148);
						rvalue(6);
						}
						break;
					case 4:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(149);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(150);
						((RvalueContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GREATER) | (1L << LESS) | (1L << LESS_EQUAL) | (1L << GREATER_EQUAL))) != 0)) ) {
							((RvalueContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(151);
						rvalue(5);
						}
						break;
					case 5:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(152);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(153);
						((RvalueContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRICT_EQUAL) | (1L << EQUAL) | (1L << NOT_EQUAL))) != 0)) ) {
							((RvalueContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(154);
						rvalue(4);
						}
						break;
					case 6:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(155);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(156);
						((RvalueContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==AND || _la==OR) ) {
							((RvalueContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(157);
						rvalue(3);
						}
						break;
					}
					} 
				}
				setState(162);
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

	public static class Heap_readContext extends ParserRuleContext {
		public IdContext target;
		public AddressContext addr;
		public TerminalNode LEFT_SBRACKET() { return getToken(MindcodeParser.LEFT_SBRACKET, 0); }
		public TerminalNode RIGHT_SBRACKET() { return getToken(MindcodeParser.RIGHT_SBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public AddressContext address() {
			return getRuleContext(AddressContext.class,0);
		}
		public Heap_readContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_heap_read; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterHeap_read(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitHeap_read(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitHeap_read(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Heap_readContext heap_read() throws RecognitionException {
		Heap_readContext _localctx = new Heap_readContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_heap_read);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			((Heap_readContext)_localctx).target = id();
			setState(164);
			match(LEFT_SBRACKET);
			setState(165);
			((Heap_readContext)_localctx).addr = address();
			setState(166);
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

	public static class AddressContext extends ParserRuleContext {
		public Int_tContext int_t() {
			return getRuleContext(Int_tContext.class,0);
		}
		public AddressContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_address; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterAddress(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitAddress(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitAddress(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddressContext address() throws RecognitionException {
		AddressContext _localctx = new AddressContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_address);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			int_t();
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

	public static class Sensor_readContext extends ParserRuleContext {
		public IdContext target;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public LiquidContext liquid() {
			return getRuleContext(LiquidContext.class,0);
		}
		public SensorContext sensor() {
			return getRuleContext(SensorContext.class,0);
		}
		public Sensor_readContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sensor_read; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterSensor_read(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitSensor_read(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitSensor_read(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sensor_readContext sensor_read() throws RecognitionException {
		Sensor_readContext _localctx = new Sensor_readContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_sensor_read);
		try {
			setState(182);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(170);
				((Sensor_readContext)_localctx).target = id();
				setState(171);
				match(DOT);
				setState(172);
				resource();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(174);
				((Sensor_readContext)_localctx).target = id();
				setState(175);
				match(DOT);
				setState(176);
				liquid();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(178);
				((Sensor_readContext)_localctx).target = id();
				setState(179);
				match(DOT);
				setState(180);
				sensor();
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

	public static class AssignmentContext extends ParserRuleContext {
		public LvalueContext target;
		public RvalueContext value;
		public Token op;
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public TerminalNode PLUS_ASSIGN() { return getToken(MindcodeParser.PLUS_ASSIGN, 0); }
		public TerminalNode MINUS_ASSIGN() { return getToken(MindcodeParser.MINUS_ASSIGN, 0); }
		public TerminalNode MUL_ASSIGN() { return getToken(MindcodeParser.MUL_ASSIGN, 0); }
		public TerminalNode DIV_ASSIGN() { return getToken(MindcodeParser.DIV_ASSIGN, 0); }
		public TerminalNode MOD_ASSIGN() { return getToken(MindcodeParser.MOD_ASSIGN, 0); }
		public TerminalNode EXP_ASSIGN() { return getToken(MindcodeParser.EXP_ASSIGN, 0); }
		public Heap_readContext heap_read() {
			return getRuleContext(Heap_readContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
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

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_assignment);
		int _la;
		try {
			setState(200);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(184);
				((AssignmentContext)_localctx).target = lvalue();
				setState(185);
				match(ASSIGN);
				setState(186);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(188);
				((AssignmentContext)_localctx).target = lvalue();
				setState(189);
				((AssignmentContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS_ASSIGN) | (1L << MINUS_ASSIGN) | (1L << MUL_ASSIGN) | (1L << DIV_ASSIGN) | (1L << MOD_ASSIGN) | (1L << EXP_ASSIGN))) != 0)) ) {
					((AssignmentContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(190);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(192);
				heap_read();
				setState(193);
				match(ASSIGN);
				setState(194);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(196);
				heap_read();
				setState(197);
				((AssignmentContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS_ASSIGN) | (1L << MINUS_ASSIGN) | (1L << MUL_ASSIGN) | (1L << DIV_ASSIGN) | (1L << MOD_ASSIGN) | (1L << EXP_ASSIGN))) != 0)) ) {
					((AssignmentContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(198);
				((AssignmentContext)_localctx).value = rvalue(0);
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
		enterRule(_localctx, 30, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
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
		enterRule(_localctx, 32, RULE_literal_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
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
		enterRule(_localctx, 34, RULE_float_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
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
		enterRule(_localctx, 36, RULE_int_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
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

	public static class Bool_tContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(MindcodeParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(MindcodeParser.FALSE, 0); }
		public Bool_tContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bool_t; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterBool_t(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitBool_t(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitBool_t(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Bool_tContext bool_t() throws RecognitionException {
		Bool_tContext _localctx = new Bool_tContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_bool_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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
		enterRule(_localctx, 40, RULE_null_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
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

	public static class TerminatorContext extends ParserRuleContext {
		public TerminalNode SEMICOLON() { return getToken(MindcodeParser.SEMICOLON, 0); }
		public CrlfContext crlf() {
			return getRuleContext(CrlfContext.class,0);
		}
		public TerminalNode EOF() { return getToken(MindcodeParser.EOF, 0); }
		public TerminatorContext terminator() {
			return getRuleContext(TerminatorContext.class,0);
		}
		public TerminatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terminator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterTerminator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitTerminator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitTerminator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TerminatorContext terminator() throws RecognitionException {
		return terminator(0);
	}

	private TerminatorContext terminator(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TerminatorContext _localctx = new TerminatorContext(_ctx, _parentState);
		TerminatorContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_terminator, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SEMICOLON:
				{
				setState(215);
				match(SEMICOLON);
				}
				break;
			case CRLF:
				{
				setState(216);
				crlf();
				}
				break;
			case EOF:
				{
				setState(217);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(226);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(224);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
					case 1:
						{
						_localctx = new TerminatorContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_terminator);
						setState(220);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(221);
						match(SEMICOLON);
						}
						break;
					case 2:
						{
						_localctx = new TerminatorContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_terminator);
						setState(222);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(223);
						crlf();
						}
						break;
					}
					} 
				}
				setState(228);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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

	public static class CrlfContext extends ParserRuleContext {
		public TerminalNode CRLF() { return getToken(MindcodeParser.CRLF, 0); }
		public CrlfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_crlf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCrlf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCrlf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCrlf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CrlfContext crlf() throws RecognitionException {
		CrlfContext _localctx = new CrlfContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_crlf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(CRLF);
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

	public static class ResourceContext extends ParserRuleContext {
		public TerminalNode COPPER() { return getToken(MindcodeParser.COPPER, 0); }
		public TerminalNode LEAD() { return getToken(MindcodeParser.LEAD, 0); }
		public TerminalNode METAGLASS() { return getToken(MindcodeParser.METAGLASS, 0); }
		public TerminalNode GRAPHITE() { return getToken(MindcodeParser.GRAPHITE, 0); }
		public TerminalNode SAND() { return getToken(MindcodeParser.SAND, 0); }
		public TerminalNode COAL() { return getToken(MindcodeParser.COAL, 0); }
		public TerminalNode TITANIUM() { return getToken(MindcodeParser.TITANIUM, 0); }
		public TerminalNode SCRAP() { return getToken(MindcodeParser.SCRAP, 0); }
		public TerminalNode SILICON() { return getToken(MindcodeParser.SILICON, 0); }
		public TerminalNode PYRATITE() { return getToken(MindcodeParser.PYRATITE, 0); }
		public ResourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterResource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitResource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitResource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResourceContext resource() throws RecognitionException {
		ResourceContext _localctx = new ResourceContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_resource);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COPPER) | (1L << LEAD) | (1L << METAGLASS) | (1L << GRAPHITE) | (1L << SAND) | (1L << COAL) | (1L << TITANIUM) | (1L << SCRAP) | (1L << SILICON) | (1L << PYRATITE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class LiquidContext extends ParserRuleContext {
		public TerminalNode WATER() { return getToken(MindcodeParser.WATER, 0); }
		public TerminalNode SLAG() { return getToken(MindcodeParser.SLAG, 0); }
		public TerminalNode CRYOFLUID() { return getToken(MindcodeParser.CRYOFLUID, 0); }
		public LiquidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_liquid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLiquid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLiquid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLiquid(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiquidContext liquid() throws RecognitionException {
		LiquidContext _localctx = new LiquidContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_liquid);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << WATER) | (1L << SLAG) | (1L << CRYOFLUID))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class SensorContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public SensorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sensor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterSensor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitSensor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitSensor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SensorContext sensor() throws RecognitionException {
		SensorContext _localctx = new SensorContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_sensor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expression_list_sempred((Expression_listContext)_localctx, predIndex);
		case 8:
			return block_statement_list_sempred((Block_statement_listContext)_localctx, predIndex);
		case 10:
			return rvalue_sempred((RvalueContext)_localctx, predIndex);
		case 21:
			return terminator_sempred((TerminatorContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_list_sempred(Expression_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean block_statement_list_sempred(Block_statement_listContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean rvalue_sempred(RvalueContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 8);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean terminator_sempred(TerminatorContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 5);
		case 9:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\39\u00f0\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3>\n\3\3\3\3\3\3\3"+
		"\3\3\7\3D\n\3\f\3\16\3G\13\3\3\4\3\4\3\4\3\4\5\4M\n\4\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\5\7[\n\7\3\7\3\7\3\7\3\7\5\7a\n\7\3\b"+
		"\3\b\3\b\3\b\5\bg\n\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\7\nv\n\n\f\n\16\ny\13\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u008d\n\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u00a1\n\f\f\f\16"+
		"\f\u00a4\13\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00b9\n\17\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00cb"+
		"\n\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27"+
		"\3\27\3\27\3\27\5\27\u00dd\n\27\3\27\3\27\3\27\3\27\7\27\u00e3\n\27\f"+
		"\27\16\27\u00e6\13\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\2\6"+
		"\4\22\26,\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\2\13\3\2\21\23\3\2\17\20\3\2\'*\3\2$&\3\2+,\3\2\t\16\3\2\"#\3\2\25\36"+
		"\3\2\37!\2\u00f7\2\66\3\2\2\2\4=\3\2\2\2\6L\3\2\2\2\bN\3\2\2\2\nT\3\2"+
		"\2\2\f`\3\2\2\2\16b\3\2\2\2\20k\3\2\2\2\22m\3\2\2\2\24z\3\2\2\2\26\u008c"+
		"\3\2\2\2\30\u00a5\3\2\2\2\32\u00aa\3\2\2\2\34\u00b8\3\2\2\2\36\u00ca\3"+
		"\2\2\2 \u00cc\3\2\2\2\"\u00ce\3\2\2\2$\u00d0\3\2\2\2&\u00d2\3\2\2\2(\u00d4"+
		"\3\2\2\2*\u00d6\3\2\2\2,\u00dc\3\2\2\2.\u00e7\3\2\2\2\60\u00e9\3\2\2\2"+
		"\62\u00eb\3\2\2\2\64\u00ed\3\2\2\2\66\67\5\4\3\2\67\3\3\2\2\289\b\3\1"+
		"\29:\5\6\4\2:;\5,\27\2;>\3\2\2\2<>\5,\27\2=8\3\2\2\2=<\3\2\2\2>E\3\2\2"+
		"\2?@\f\4\2\2@A\5\6\4\2AB\5,\27\2BD\3\2\2\2C?\3\2\2\2DG\3\2\2\2EC\3\2\2"+
		"\2EF\3\2\2\2F\5\3\2\2\2GE\3\2\2\2HM\5\16\b\2IM\5\n\6\2JM\5\b\5\2KM\5\26"+
		"\f\2LH\3\2\2\2LI\3\2\2\2LJ\3\2\2\2LK\3\2\2\2M\7\3\2\2\2NO\5 \21\2OP\7"+
		"\5\2\2PQ\5 \21\2QR\7\b\2\2RS\5\26\f\2S\t\3\2\2\2TU\5 \21\2UV\7.\2\2VW"+
		"\5\f\7\2WX\7/\2\2X\13\3\2\2\2Y[\5\26\f\2ZY\3\2\2\2Z[\3\2\2\2[a\3\2\2\2"+
		"\\]\5\26\f\2]^\7\6\2\2^_\5\f\7\2_a\3\2\2\2`Z\3\2\2\2`\\\3\2\2\2a\r\3\2"+
		"\2\2bc\7\4\2\2cd\5\26\f\2df\7\62\2\2eg\5.\30\2fe\3\2\2\2fg\3\2\2\2gh\3"+
		"\2\2\2hi\5\20\t\2ij\7\63\2\2j\17\3\2\2\2kl\5\22\n\2l\21\3\2\2\2mn\b\n"+
		"\1\2no\5\6\4\2op\5,\27\2pw\3\2\2\2qr\f\3\2\2rs\5\6\4\2st\5,\27\2tv\3\2"+
		"\2\2uq\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x\23\3\2\2\2yw\3\2\2\2z{\5"+
		" \21\2{\25\3\2\2\2|}\b\f\1\2}\u008d\5\24\13\2~\u008d\5\36\20\2\177\u008d"+
		"\5\"\22\2\u0080\u008d\5(\25\2\u0081\u008d\5$\23\2\u0082\u008d\5&\24\2"+
		"\u0083\u008d\5*\26\2\u0084\u008d\5\34\17\2\u0085\u008d\5\30\r\2\u0086"+
		"\u0087\7-\2\2\u0087\u008d\5\26\f\t\u0088\u0089\7.\2\2\u0089\u008a\5\26"+
		"\f\2\u008a\u008b\7/\2\2\u008b\u008d\3\2\2\2\u008c|\3\2\2\2\u008c~\3\2"+
		"\2\2\u008c\177\3\2\2\2\u008c\u0080\3\2\2\2\u008c\u0081\3\2\2\2\u008c\u0082"+
		"\3\2\2\2\u008c\u0083\3\2\2\2\u008c\u0084\3\2\2\2\u008c\u0085\3\2\2\2\u008c"+
		"\u0086\3\2\2\2\u008c\u0088\3\2\2\2\u008d\u00a2\3\2\2\2\u008e\u008f\f\n"+
		"\2\2\u008f\u0090\7\24\2\2\u0090\u00a1\5\26\f\13\u0091\u0092\f\b\2\2\u0092"+
		"\u0093\t\2\2\2\u0093\u00a1\5\26\f\t\u0094\u0095\f\7\2\2\u0095\u0096\t"+
		"\3\2\2\u0096\u00a1\5\26\f\b\u0097\u0098\f\6\2\2\u0098\u0099\t\4\2\2\u0099"+
		"\u00a1\5\26\f\7\u009a\u009b\f\5\2\2\u009b\u009c\t\5\2\2\u009c\u00a1\5"+
		"\26\f\6\u009d\u009e\f\4\2\2\u009e\u009f\t\6\2\2\u009f\u00a1\5\26\f\5\u00a0"+
		"\u008e\3\2\2\2\u00a0\u0091\3\2\2\2\u00a0\u0094\3\2\2\2\u00a0\u0097\3\2"+
		"\2\2\u00a0\u009a\3\2\2\2\u00a0\u009d\3\2\2\2\u00a1\u00a4\3\2\2\2\u00a2"+
		"\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\27\3\2\2\2\u00a4\u00a2\3\2\2"+
		"\2\u00a5\u00a6\5 \21\2\u00a6\u00a7\7\60\2\2\u00a7\u00a8\5\32\16\2\u00a8"+
		"\u00a9\7\61\2\2\u00a9\31\3\2\2\2\u00aa\u00ab\5&\24\2\u00ab\33\3\2\2\2"+
		"\u00ac\u00ad\5 \21\2\u00ad\u00ae\7\5\2\2\u00ae\u00af\5\60\31\2\u00af\u00b9"+
		"\3\2\2\2\u00b0\u00b1\5 \21\2\u00b1\u00b2\7\5\2\2\u00b2\u00b3\5\62\32\2"+
		"\u00b3\u00b9\3\2\2\2\u00b4\u00b5\5 \21\2\u00b5\u00b6\7\5\2\2\u00b6\u00b7"+
		"\5\64\33\2\u00b7\u00b9\3\2\2\2\u00b8\u00ac\3\2\2\2\u00b8\u00b0\3\2\2\2"+
		"\u00b8\u00b4\3\2\2\2\u00b9\35\3\2\2\2\u00ba\u00bb\5\24\13\2\u00bb\u00bc"+
		"\7\b\2\2\u00bc\u00bd\5\26\f\2\u00bd\u00cb\3\2\2\2\u00be\u00bf\5\24\13"+
		"\2\u00bf\u00c0\t\7\2\2\u00c0\u00c1\5\26\f\2\u00c1\u00cb\3\2\2\2\u00c2"+
		"\u00c3\5\30\r\2\u00c3\u00c4\7\b\2\2\u00c4\u00c5\5\26\f\2\u00c5\u00cb\3"+
		"\2\2\2\u00c6\u00c7\5\30\r\2\u00c7\u00c8\t\7\2\2\u00c8\u00c9\5\26\f\2\u00c9"+
		"\u00cb\3\2\2\2\u00ca\u00ba\3\2\2\2\u00ca\u00be\3\2\2\2\u00ca\u00c2\3\2"+
		"\2\2\u00ca\u00c6\3\2\2\2\u00cb\37\3\2\2\2\u00cc\u00cd\7\67\2\2\u00cd!"+
		"\3\2\2\2\u00ce\u00cf\7\3\2\2\u00cf#\3\2\2\2\u00d0\u00d1\7\66\2\2\u00d1"+
		"%\3\2\2\2\u00d2\u00d3\7\65\2\2\u00d3\'\3\2\2\2\u00d4\u00d5\t\b\2\2\u00d5"+
		")\3\2\2\2\u00d6\u00d7\7\64\2\2\u00d7+\3\2\2\2\u00d8\u00d9\b\27\1\2\u00d9"+
		"\u00dd\7\7\2\2\u00da\u00dd\5.\30\2\u00db\u00dd\7\2\2\3\u00dc\u00d8\3\2"+
		"\2\2\u00dc\u00da\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd\u00e4\3\2\2\2\u00de"+
		"\u00df\f\7\2\2\u00df\u00e3\7\7\2\2\u00e0\u00e1\f\6\2\2\u00e1\u00e3\5."+
		"\30\2\u00e2\u00de\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5-\3\2\2\2\u00e6\u00e4\3\2\2\2"+
		"\u00e7\u00e8\78\2\2\u00e8/\3\2\2\2\u00e9\u00ea\t\t\2\2\u00ea\61\3\2\2"+
		"\2\u00eb\u00ec\t\n\2\2\u00ec\63\3\2\2\2\u00ed\u00ee\5 \21\2\u00ee\65\3"+
		"\2\2\2\21=ELZ`fw\u008c\u00a0\u00a2\u00b8\u00ca\u00dc\u00e2\u00e4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}