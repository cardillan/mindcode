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
		LITERAL=1, WHILE=2, IF=3, ELSE=4, AT=5, DOT=6, COMMA=7, SEMICOLON=8, ASSIGN=9, 
		PLUS_ASSIGN=10, MINUS_ASSIGN=11, MUL_ASSIGN=12, DIV_ASSIGN=13, MOD_ASSIGN=14, 
		EXP_ASSIGN=15, PLUS=16, MINUS=17, MUL=18, DIV=19, MOD=20, EXP=21, TRUE=22, 
		FALSE=23, STRICT_EQUAL=24, EQUAL=25, NOT_EQUAL=26, GREATER=27, LESS=28, 
		LESS_EQUAL=29, GREATER_EQUAL=30, AND=31, OR=32, NOT=33, LEFT_RBRACKET=34, 
		RIGHT_RBRACKET=35, LEFT_SBRACKET=36, RIGHT_SBRACKET=37, LEFT_CBRACKET=38, 
		RIGHT_CBRACKET=39, NULL=40, INT=41, FLOAT=42, ID=43, REF=44, CRLF=45, 
		WS=46, SL_COMMENT=47;
	public static final int
		RULE_program = 0, RULE_expression_list = 1, RULE_expression = 2, RULE_control_statement = 3, 
		RULE_while_statement = 4, RULE_block_body = 5, RULE_block_statement_list = 6, 
		RULE_lvalue = 7, RULE_rvalue = 8, RULE_numeric = 9, RULE_unary_minus = 10, 
		RULE_ref = 11, RULE_funcall = 12, RULE_params_list = 13, RULE_if_expression = 14, 
		RULE_heap_ref = 15, RULE_address = 16, RULE_sensor_read = 17, RULE_assignment = 18, 
		RULE_id = 19, RULE_literal_t = 20, RULE_float_t = 21, RULE_int_t = 22, 
		RULE_bool_t = 23, RULE_null_t = 24, RULE_terminator = 25, RULE_crlf = 26, 
		RULE_resource = 27, RULE_single_line_comment = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expression_list", "expression", "control_statement", "while_statement", 
			"block_body", "block_statement_list", "lvalue", "rvalue", "numeric", 
			"unary_minus", "ref", "funcall", "params_list", "if_expression", "heap_ref", 
			"address", "sensor_read", "assignment", "id", "literal_t", "float_t", 
			"int_t", "bool_t", "null_t", "terminator", "crlf", "resource", "single_line_comment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'while'", "'if'", "'else'", "'@'", "'.'", "','", "';'", 
			"'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'**='", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'**'", "'true'", "'false'", "'==='", "'=='", "'!='", 
			"'>'", "'<'", "'<='", "'>='", null, null, null, "'('", "')'", "'['", 
			"']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LITERAL", "WHILE", "IF", "ELSE", "AT", "DOT", "COMMA", "SEMICOLON", 
			"ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"MOD_ASSIGN", "EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", 
			"TRUE", "FALSE", "STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", 
			"LESS_EQUAL", "GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"NULL", "INT", "FLOAT", "ID", "REF", "CRLF", "WS", "SL_COMMENT"
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
			setState(58);
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
			setState(65);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
			case WHILE:
			case IF:
			case MINUS:
			case TRUE:
			case FALSE:
			case NOT:
			case LEFT_RBRACKET:
			case NULL:
			case INT:
			case FLOAT:
			case ID:
			case REF:
			case SL_COMMENT:
				{
				setState(61);
				expression();
				setState(62);
				terminator(0);
				}
				break;
			case EOF:
			case SEMICOLON:
			case CRLF:
				{
				setState(64);
				terminator(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(73);
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
					setState(67);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(68);
					expression();
					setState(69);
					terminator(0);
					}
					} 
				}
				setState(75);
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
		public Control_statementContext control_statement() {
			return getRuleContext(Control_statementContext.class,0);
		}
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public Single_line_commentContext single_line_comment() {
			return getRuleContext(Single_line_commentContext.class,0);
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
			setState(80);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(76);
				while_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(77);
				control_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(78);
				rvalue(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(79);
				single_line_comment();
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
			setState(82);
			((Control_statementContext)_localctx).target = id();
			setState(83);
			match(DOT);
			setState(84);
			((Control_statementContext)_localctx).property = id();
			setState(85);
			match(ASSIGN);
			setState(86);
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
		enterRule(_localctx, 8, RULE_while_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(WHILE);
			setState(89);
			rvalue(0);
			setState(90);
			match(LEFT_CBRACKET);
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CRLF) {
				{
				setState(91);
				crlf();
				}
			}

			setState(94);
			block_body();
			setState(95);
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
		enterRule(_localctx, 10, RULE_block_body);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
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
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_block_statement_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(100);
			expression();
			setState(101);
			terminator(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(109);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Block_statement_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_block_statement_list);
					setState(103);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(104);
					expression();
					setState(105);
					terminator(0);
					}
					} 
				}
				setState(111);
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
		enterRule(_localctx, 14, RULE_lvalue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
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
		public If_expressionContext if_expression() {
			return getRuleContext(If_expressionContext.class,0);
		}
		public FuncallContext funcall() {
			return getRuleContext(FuncallContext.class,0);
		}
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public Literal_tContext literal_t() {
			return getRuleContext(Literal_tContext.class,0);
		}
		public Bool_tContext bool_t() {
			return getRuleContext(Bool_tContext.class,0);
		}
		public NumericContext numeric() {
			return getRuleContext(NumericContext.class,0);
		}
		public Null_tContext null_t() {
			return getRuleContext(Null_tContext.class,0);
		}
		public Sensor_readContext sensor_read() {
			return getRuleContext(Sensor_readContext.class,0);
		}
		public Heap_refContext heap_ref() {
			return getRuleContext(Heap_refContext.class,0);
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
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_rvalue, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(115);
				lvalue();
				}
				break;
			case 2:
				{
				setState(116);
				assignment();
				}
				break;
			case 3:
				{
				setState(117);
				if_expression();
				}
				break;
			case 4:
				{
				setState(118);
				funcall();
				}
				break;
			case 5:
				{
				setState(119);
				ref();
				}
				break;
			case 6:
				{
				setState(120);
				literal_t();
				}
				break;
			case 7:
				{
				setState(121);
				bool_t();
				}
				break;
			case 8:
				{
				setState(122);
				numeric();
				}
				break;
			case 9:
				{
				setState(123);
				null_t();
				}
				break;
			case 10:
				{
				setState(124);
				sensor_read();
				}
				break;
			case 11:
				{
				setState(125);
				heap_ref();
				}
				break;
			case 12:
				{
				setState(126);
				((RvalueContext)_localctx).op = match(NOT);
				setState(127);
				rvalue(7);
				}
				break;
			case 13:
				{
				setState(128);
				match(LEFT_RBRACKET);
				setState(129);
				rvalue(0);
				setState(130);
				match(RIGHT_RBRACKET);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(154);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(152);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
					case 1:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(134);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(135);
						((RvalueContext)_localctx).op = match(EXP);
						setState(136);
						rvalue(9);
						}
						break;
					case 2:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(137);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(138);
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
						setState(139);
						rvalue(7);
						}
						break;
					case 3:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(140);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(141);
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
						setState(142);
						rvalue(6);
						}
						break;
					case 4:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(143);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(144);
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
						setState(145);
						rvalue(5);
						}
						break;
					case 5:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(146);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(147);
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
						setState(148);
						rvalue(4);
						}
						break;
					case 6:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(149);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(150);
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
						setState(151);
						rvalue(3);
						}
						break;
					}
					} 
				}
				setState(156);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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

	public static class NumericContext extends ParserRuleContext {
		public Float_tContext float_t() {
			return getRuleContext(Float_tContext.class,0);
		}
		public Int_tContext int_t() {
			return getRuleContext(Int_tContext.class,0);
		}
		public Unary_minusContext unary_minus() {
			return getRuleContext(Unary_minusContext.class,0);
		}
		public NumericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterNumeric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitNumeric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitNumeric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumericContext numeric() throws RecognitionException {
		NumericContext _localctx = new NumericContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_numeric);
		try {
			setState(165);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				float_t();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				int_t();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(159);
				unary_minus();
				setState(160);
				float_t();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(162);
				unary_minus();
				setState(163);
				int_t();
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

	public static class Unary_minusContext extends ParserRuleContext {
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public Unary_minusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_minus; }
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

	public final Unary_minusContext unary_minus() throws RecognitionException {
		Unary_minusContext _localctx = new Unary_minusContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_unary_minus);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			match(MINUS);
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
		public Token name;
		public TerminalNode REF() { return getToken(MindcodeParser.REF, 0); }
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
		enterRule(_localctx, 22, RULE_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			((RefContext)_localctx).name = match(REF);
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

	public static class FuncallContext extends ParserRuleContext {
		public IdContext name;
		public TerminalNode LEFT_RBRACKET() { return getToken(MindcodeParser.LEFT_RBRACKET, 0); }
		public Params_listContext params_list() {
			return getRuleContext(Params_listContext.class,0);
		}
		public TerminalNode RIGHT_RBRACKET() { return getToken(MindcodeParser.RIGHT_RBRACKET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
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
		enterRule(_localctx, 24, RULE_funcall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			((FuncallContext)_localctx).name = id();
			setState(172);
			match(LEFT_RBRACKET);
			setState(173);
			params_list();
			setState(174);
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
		enterRule(_localctx, 26, RULE_params_list);
		int _la;
		try {
			setState(183);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LITERAL) | (1L << IF) | (1L << MINUS) | (1L << TRUE) | (1L << FALSE) | (1L << NOT) | (1L << LEFT_RBRACKET) | (1L << NULL) | (1L << INT) | (1L << FLOAT) | (1L << ID) | (1L << REF))) != 0)) {
					{
					setState(176);
					rvalue(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(179);
				rvalue(0);
				setState(180);
				match(COMMA);
				setState(181);
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

	public static class If_expressionContext extends ParserRuleContext {
		public RvalueContext cond;
		public Block_bodyContext true_branch;
		public Block_bodyContext false_branch;
		public TerminalNode IF() { return getToken(MindcodeParser.IF, 0); }
		public List<TerminalNode> LEFT_CBRACKET() { return getTokens(MindcodeParser.LEFT_CBRACKET); }
		public TerminalNode LEFT_CBRACKET(int i) {
			return getToken(MindcodeParser.LEFT_CBRACKET, i);
		}
		public List<TerminalNode> RIGHT_CBRACKET() { return getTokens(MindcodeParser.RIGHT_CBRACKET); }
		public TerminalNode RIGHT_CBRACKET(int i) {
			return getToken(MindcodeParser.RIGHT_CBRACKET, i);
		}
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public List<Block_bodyContext> block_body() {
			return getRuleContexts(Block_bodyContext.class);
		}
		public Block_bodyContext block_body(int i) {
			return getRuleContext(Block_bodyContext.class,i);
		}
		public List<TerminatorContext> terminator() {
			return getRuleContexts(TerminatorContext.class);
		}
		public TerminatorContext terminator(int i) {
			return getRuleContext(TerminatorContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(MindcodeParser.ELSE, 0); }
		public If_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_expression; }
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

	public final If_expressionContext if_expression() throws RecognitionException {
		If_expressionContext _localctx = new If_expressionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_if_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			match(IF);
			setState(186);
			((If_expressionContext)_localctx).cond = rvalue(0);
			setState(187);
			match(LEFT_CBRACKET);
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - -1)) & ~0x3f) == 0 && ((1L << (_la - -1)) & ((1L << (EOF - -1)) | (1L << (SEMICOLON - -1)) | (1L << (CRLF - -1)))) != 0)) {
				{
				setState(188);
				terminator(0);
				}
			}

			setState(191);
			((If_expressionContext)_localctx).true_branch = block_body();
			setState(192);
			match(RIGHT_CBRACKET);
			setState(201);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(193);
				match(ELSE);
				setState(194);
				match(LEFT_CBRACKET);
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - -1)) & ~0x3f) == 0 && ((1L << (_la - -1)) & ((1L << (EOF - -1)) | (1L << (SEMICOLON - -1)) | (1L << (CRLF - -1)))) != 0)) {
					{
					setState(195);
					terminator(0);
					}
				}

				setState(198);
				((If_expressionContext)_localctx).false_branch = block_body();
				setState(199);
				match(RIGHT_CBRACKET);
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

	public static class Heap_refContext extends ParserRuleContext {
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
		enterRule(_localctx, 30, RULE_heap_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			((Heap_refContext)_localctx).target = id();
			setState(204);
			match(LEFT_SBRACKET);
			setState(205);
			((Heap_refContext)_localctx).addr = address();
			setState(206);
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
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
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
		enterRule(_localctx, 32, RULE_address);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			rvalue(0);
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
		public RefContext unit;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ResourceContext resource() {
			return getRuleContext(ResourceContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
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
		enterRule(_localctx, 34, RULE_sensor_read);
		try {
			setState(218);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(210);
				((Sensor_readContext)_localctx).target = id();
				setState(211);
				match(DOT);
				setState(212);
				resource();
				}
				break;
			case REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(214);
				((Sensor_readContext)_localctx).unit = ref();
				setState(215);
				match(DOT);
				setState(216);
				resource();
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
		public Heap_refContext heap_ref() {
			return getRuleContext(Heap_refContext.class,0);
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
		enterRule(_localctx, 36, RULE_assignment);
		int _la;
		try {
			setState(236);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(220);
				((AssignmentContext)_localctx).target = lvalue();
				setState(221);
				match(ASSIGN);
				setState(222);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(224);
				((AssignmentContext)_localctx).target = lvalue();
				setState(225);
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
				setState(226);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(228);
				heap_ref();
				setState(229);
				match(ASSIGN);
				setState(230);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(232);
				heap_ref();
				setState(233);
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
				setState(234);
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
		enterRule(_localctx, 38, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
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
		enterRule(_localctx, 40, RULE_literal_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
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
		enterRule(_localctx, 42, RULE_float_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
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
		enterRule(_localctx, 44, RULE_int_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
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
		enterRule(_localctx, 46, RULE_bool_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
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
		enterRule(_localctx, 48, RULE_null_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
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
		int _startState = 50;
		enterRecursionRule(_localctx, 50, RULE_terminator, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SEMICOLON:
				{
				setState(251);
				match(SEMICOLON);
				}
				break;
			case CRLF:
				{
				setState(252);
				crlf();
				}
				break;
			case EOF:
				{
				setState(253);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(262);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(260);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
					case 1:
						{
						_localctx = new TerminatorContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_terminator);
						setState(256);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(257);
						match(SEMICOLON);
						}
						break;
					case 2:
						{
						_localctx = new TerminatorContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_terminator);
						setState(258);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(259);
						crlf();
						}
						break;
					}
					} 
				}
				setState(264);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
		enterRule(_localctx, 52, RULE_crlf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
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
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
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
		enterRule(_localctx, 54, RULE_resource);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
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

	public static class Single_line_commentContext extends ParserRuleContext {
		public TerminalNode SL_COMMENT() { return getToken(MindcodeParser.SL_COMMENT, 0); }
		public Single_line_commentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_single_line_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterSingle_line_comment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitSingle_line_comment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitSingle_line_comment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Single_line_commentContext single_line_comment() throws RecognitionException {
		Single_line_commentContext _localctx = new Single_line_commentContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_single_line_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(269);
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
		case 6:
			return block_statement_list_sempred((Block_statement_listContext)_localctx, predIndex);
		case 8:
			return rvalue_sempred((RvalueContext)_localctx, predIndex);
		case 25:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\61\u0112\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3\3\3\3"+
		"\3\3\3\3\5\3D\n\3\3\3\3\3\3\3\3\3\7\3J\n\3\f\3\16\3M\13\3\3\4\3\4\3\4"+
		"\3\4\5\4S\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\5\6_\n\6\3\6\3\6"+
		"\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\bn\n\b\f\b\16\bq\13\b\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\5\n\u0087\n\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u009b\n\n\f\n\16\n\u009e\13\n\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\5\13\u00a8\n\13\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\17\5\17\u00b4\n\17\3\17\3\17\3\17\3\17\5\17\u00ba\n"+
		"\17\3\20\3\20\3\20\3\20\5\20\u00c0\n\20\3\20\3\20\3\20\3\20\3\20\5\20"+
		"\u00c7\n\20\3\20\3\20\3\20\5\20\u00cc\n\20\3\21\3\21\3\21\3\21\3\21\3"+
		"\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00dd\n\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\5\24\u00ef\n\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31"+
		"\3\32\3\32\3\33\3\33\3\33\3\33\5\33\u0101\n\33\3\33\3\33\3\33\3\33\7\33"+
		"\u0107\n\33\f\33\16\33\u010a\13\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36"+
		"\2\6\4\16\22\64\37\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:\2\t\3\2\24\26\3\2\22\23\3\2\35 \3\2\32\34\3\2!\"\3\2\f\21"+
		"\3\2\30\31\2\u011d\2<\3\2\2\2\4C\3\2\2\2\6R\3\2\2\2\bT\3\2\2\2\nZ\3\2"+
		"\2\2\fc\3\2\2\2\16e\3\2\2\2\20r\3\2\2\2\22\u0086\3\2\2\2\24\u00a7\3\2"+
		"\2\2\26\u00a9\3\2\2\2\30\u00ab\3\2\2\2\32\u00ad\3\2\2\2\34\u00b9\3\2\2"+
		"\2\36\u00bb\3\2\2\2 \u00cd\3\2\2\2\"\u00d2\3\2\2\2$\u00dc\3\2\2\2&\u00ee"+
		"\3\2\2\2(\u00f0\3\2\2\2*\u00f2\3\2\2\2,\u00f4\3\2\2\2.\u00f6\3\2\2\2\60"+
		"\u00f8\3\2\2\2\62\u00fa\3\2\2\2\64\u0100\3\2\2\2\66\u010b\3\2\2\28\u010d"+
		"\3\2\2\2:\u010f\3\2\2\2<=\5\4\3\2=\3\3\2\2\2>?\b\3\1\2?@\5\6\4\2@A\5\64"+
		"\33\2AD\3\2\2\2BD\5\64\33\2C>\3\2\2\2CB\3\2\2\2DK\3\2\2\2EF\f\4\2\2FG"+
		"\5\6\4\2GH\5\64\33\2HJ\3\2\2\2IE\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2"+
		"L\5\3\2\2\2MK\3\2\2\2NS\5\n\6\2OS\5\b\5\2PS\5\22\n\2QS\5:\36\2RN\3\2\2"+
		"\2RO\3\2\2\2RP\3\2\2\2RQ\3\2\2\2S\7\3\2\2\2TU\5(\25\2UV\7\b\2\2VW\5(\25"+
		"\2WX\7\13\2\2XY\5\22\n\2Y\t\3\2\2\2Z[\7\4\2\2[\\\5\22\n\2\\^\7(\2\2]_"+
		"\5\66\34\2^]\3\2\2\2^_\3\2\2\2_`\3\2\2\2`a\5\f\7\2ab\7)\2\2b\13\3\2\2"+
		"\2cd\5\16\b\2d\r\3\2\2\2ef\b\b\1\2fg\5\6\4\2gh\5\64\33\2ho\3\2\2\2ij\f"+
		"\3\2\2jk\5\6\4\2kl\5\64\33\2ln\3\2\2\2mi\3\2\2\2nq\3\2\2\2om\3\2\2\2o"+
		"p\3\2\2\2p\17\3\2\2\2qo\3\2\2\2rs\5(\25\2s\21\3\2\2\2tu\b\n\1\2u\u0087"+
		"\5\20\t\2v\u0087\5&\24\2w\u0087\5\36\20\2x\u0087\5\32\16\2y\u0087\5\30"+
		"\r\2z\u0087\5*\26\2{\u0087\5\60\31\2|\u0087\5\24\13\2}\u0087\5\62\32\2"+
		"~\u0087\5$\23\2\177\u0087\5 \21\2\u0080\u0081\7#\2\2\u0081\u0087\5\22"+
		"\n\t\u0082\u0083\7$\2\2\u0083\u0084\5\22\n\2\u0084\u0085\7%\2\2\u0085"+
		"\u0087\3\2\2\2\u0086t\3\2\2\2\u0086v\3\2\2\2\u0086w\3\2\2\2\u0086x\3\2"+
		"\2\2\u0086y\3\2\2\2\u0086z\3\2\2\2\u0086{\3\2\2\2\u0086|\3\2\2\2\u0086"+
		"}\3\2\2\2\u0086~\3\2\2\2\u0086\177\3\2\2\2\u0086\u0080\3\2\2\2\u0086\u0082"+
		"\3\2\2\2\u0087\u009c\3\2\2\2\u0088\u0089\f\n\2\2\u0089\u008a\7\27\2\2"+
		"\u008a\u009b\5\22\n\13\u008b\u008c\f\b\2\2\u008c\u008d\t\2\2\2\u008d\u009b"+
		"\5\22\n\t\u008e\u008f\f\7\2\2\u008f\u0090\t\3\2\2\u0090\u009b\5\22\n\b"+
		"\u0091\u0092\f\6\2\2\u0092\u0093\t\4\2\2\u0093\u009b\5\22\n\7\u0094\u0095"+
		"\f\5\2\2\u0095\u0096\t\5\2\2\u0096\u009b\5\22\n\6\u0097\u0098\f\4\2\2"+
		"\u0098\u0099\t\6\2\2\u0099\u009b\5\22\n\5\u009a\u0088\3\2\2\2\u009a\u008b"+
		"\3\2\2\2\u009a\u008e\3\2\2\2\u009a\u0091\3\2\2\2\u009a\u0094\3\2\2\2\u009a"+
		"\u0097\3\2\2\2\u009b\u009e\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2"+
		"\2\2\u009d\23\3\2\2\2\u009e\u009c\3\2\2\2\u009f\u00a8\5,\27\2\u00a0\u00a8"+
		"\5.\30\2\u00a1\u00a2\5\26\f\2\u00a2\u00a3\5,\27\2\u00a3\u00a8\3\2\2\2"+
		"\u00a4\u00a5\5\26\f\2\u00a5\u00a6\5.\30\2\u00a6\u00a8\3\2\2\2\u00a7\u009f"+
		"\3\2\2\2\u00a7\u00a0\3\2\2\2\u00a7\u00a1\3\2\2\2\u00a7\u00a4\3\2\2\2\u00a8"+
		"\25\3\2\2\2\u00a9\u00aa\7\23\2\2\u00aa\27\3\2\2\2\u00ab\u00ac\7.\2\2\u00ac"+
		"\31\3\2\2\2\u00ad\u00ae\5(\25\2\u00ae\u00af\7$\2\2\u00af\u00b0\5\34\17"+
		"\2\u00b0\u00b1\7%\2\2\u00b1\33\3\2\2\2\u00b2\u00b4\5\22\n\2\u00b3\u00b2"+
		"\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00ba\3\2\2\2\u00b5\u00b6\5\22\n\2"+
		"\u00b6\u00b7\7\t\2\2\u00b7\u00b8\5\34\17\2\u00b8\u00ba\3\2\2\2\u00b9\u00b3"+
		"\3\2\2\2\u00b9\u00b5\3\2\2\2\u00ba\35\3\2\2\2\u00bb\u00bc\7\5\2\2\u00bc"+
		"\u00bd\5\22\n\2\u00bd\u00bf\7(\2\2\u00be\u00c0\5\64\33\2\u00bf\u00be\3"+
		"\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2\5\f\7\2\u00c2"+
		"\u00cb\7)\2\2\u00c3\u00c4\7\6\2\2\u00c4\u00c6\7(\2\2\u00c5\u00c7\5\64"+
		"\33\2\u00c6\u00c5\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8"+
		"\u00c9\5\f\7\2\u00c9\u00ca\7)\2\2\u00ca\u00cc\3\2\2\2\u00cb\u00c3\3\2"+
		"\2\2\u00cb\u00cc\3\2\2\2\u00cc\37\3\2\2\2\u00cd\u00ce\5(\25\2\u00ce\u00cf"+
		"\7&\2\2\u00cf\u00d0\5\"\22\2\u00d0\u00d1\7\'\2\2\u00d1!\3\2\2\2\u00d2"+
		"\u00d3\5\22\n\2\u00d3#\3\2\2\2\u00d4\u00d5\5(\25\2\u00d5\u00d6\7\b\2\2"+
		"\u00d6\u00d7\58\35\2\u00d7\u00dd\3\2\2\2\u00d8\u00d9\5\30\r\2\u00d9\u00da"+
		"\7\b\2\2\u00da\u00db\58\35\2\u00db\u00dd\3\2\2\2\u00dc\u00d4\3\2\2\2\u00dc"+
		"\u00d8\3\2\2\2\u00dd%\3\2\2\2\u00de\u00df\5\20\t\2\u00df\u00e0\7\13\2"+
		"\2\u00e0\u00e1\5\22\n\2\u00e1\u00ef\3\2\2\2\u00e2\u00e3\5\20\t\2\u00e3"+
		"\u00e4\t\7\2\2\u00e4\u00e5\5\22\n\2\u00e5\u00ef\3\2\2\2\u00e6\u00e7\5"+
		" \21\2\u00e7\u00e8\7\13\2\2\u00e8\u00e9\5\22\n\2\u00e9\u00ef\3\2\2\2\u00ea"+
		"\u00eb\5 \21\2\u00eb\u00ec\t\7\2\2\u00ec\u00ed\5\22\n\2\u00ed\u00ef\3"+
		"\2\2\2\u00ee\u00de\3\2\2\2\u00ee\u00e2\3\2\2\2\u00ee\u00e6\3\2\2\2\u00ee"+
		"\u00ea\3\2\2\2\u00ef\'\3\2\2\2\u00f0\u00f1\7-\2\2\u00f1)\3\2\2\2\u00f2"+
		"\u00f3\7\3\2\2\u00f3+\3\2\2\2\u00f4\u00f5\7,\2\2\u00f5-\3\2\2\2\u00f6"+
		"\u00f7\7+\2\2\u00f7/\3\2\2\2\u00f8\u00f9\t\b\2\2\u00f9\61\3\2\2\2\u00fa"+
		"\u00fb\7*\2\2\u00fb\63\3\2\2\2\u00fc\u00fd\b\33\1\2\u00fd\u0101\7\n\2"+
		"\2\u00fe\u0101\5\66\34\2\u00ff\u0101\7\2\2\3\u0100\u00fc\3\2\2\2\u0100"+
		"\u00fe\3\2\2\2\u0100\u00ff\3\2\2\2\u0101\u0108\3\2\2\2\u0102\u0103\f\7"+
		"\2\2\u0103\u0107\7\n\2\2\u0104\u0105\f\6\2\2\u0105\u0107\5\66\34\2\u0106"+
		"\u0102\3\2\2\2\u0106\u0104\3\2\2\2\u0107\u010a\3\2\2\2\u0108\u0106\3\2"+
		"\2\2\u0108\u0109\3\2\2\2\u0109\65\3\2\2\2\u010a\u0108\3\2\2\2\u010b\u010c"+
		"\7/\2\2\u010c\67\3\2\2\2\u010d\u010e\5(\25\2\u010e9\3\2\2\2\u010f\u0110"+
		"\7\61\2\2\u0110;\3\2\2\2\25CKR^o\u0086\u009a\u009c\u00a7\u00b3\u00b9\u00bf"+
		"\u00c6\u00cb\u00dc\u00ee\u0100\u0106\u0108";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}