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
		TO=1, UNTIL=2, LITERAL=3, WHILE=4, IF=5, ELSE=6, FOR=7, IN=8, EXCLUSIVE=9, 
		ALLOCATE=10, HEAP=11, AT=12, DOT=13, COMMA=14, SEMICOLON=15, DOLLAR=16, 
		ASSIGN=17, PLUS_ASSIGN=18, MINUS_ASSIGN=19, MUL_ASSIGN=20, DIV_ASSIGN=21, 
		MOD_ASSIGN=22, EXP_ASSIGN=23, PLUS=24, MINUS=25, MUL=26, DIV=27, MOD=28, 
		EXP=29, TRUE=30, FALSE=31, STRICT_EQUAL=32, EQUAL=33, NOT_EQUAL=34, GREATER=35, 
		LESS=36, LESS_EQUAL=37, GREATER_EQUAL=38, AND=39, OR=40, NOT=41, LEFT_RBRACKET=42, 
		RIGHT_RBRACKET=43, LEFT_SBRACKET=44, RIGHT_SBRACKET=45, LEFT_CBRACKET=46, 
		RIGHT_CBRACKET=47, NULL=48, INT=49, FLOAT=50, ID=51, REF=52, CRLF=53, 
		WS=54, SL_COMMENT=55;
	public static final int
		RULE_program = 0, RULE_expression_list = 1, RULE_expression = 2, RULE_heap_allocation = 3, 
		RULE_for_statement = 4, RULE_range = 5, RULE_init_expr = 6, RULE_cond_expr = 7, 
		RULE_loop_expr = 8, RULE_control_statement = 9, RULE_while_statement = 10, 
		RULE_block_body = 11, RULE_block_statement_list = 12, RULE_lvalue = 13, 
		RULE_global = 14, RULE_rvalue = 15, RULE_numeric = 16, RULE_unary_minus = 17, 
		RULE_ref = 18, RULE_funcall = 19, RULE_params_list = 20, RULE_if_expression = 21, 
		RULE_heap_ref = 22, RULE_address = 23, RULE_sensor_read = 24, RULE_assignment = 25, 
		RULE_id = 26, RULE_literal_t = 27, RULE_float_t = 28, RULE_int_t = 29, 
		RULE_bool_t = 30, RULE_null_t = 31, RULE_terminator = 32, RULE_crlf = 33, 
		RULE_resource = 34, RULE_single_line_comment = 35;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expression_list", "expression", "heap_allocation", "for_statement", 
			"range", "init_expr", "cond_expr", "loop_expr", "control_statement", 
			"while_statement", "block_body", "block_statement_list", "lvalue", "global", 
			"rvalue", "numeric", "unary_minus", "ref", "funcall", "params_list", 
			"if_expression", "heap_ref", "address", "sensor_read", "assignment", 
			"id", "literal_t", "float_t", "int_t", "bool_t", "null_t", "terminator", 
			"crlf", "resource", "single_line_comment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'to'", "'until'", null, "'while'", "'if'", "'else'", "'for'", 
			"'in'", "'exclusive'", "'allocate'", "'heap'", "'@'", "'.'", "','", "';'", 
			"'$'", "'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'**='", "'+'", 
			"'-'", "'*'", "'/'", "'%'", "'**'", "'true'", "'false'", "'==='", "'=='", 
			"'!='", "'>'", "'<'", "'<='", "'>='", null, null, null, "'('", "')'", 
			"'['", "']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TO", "UNTIL", "LITERAL", "WHILE", "IF", "ELSE", "FOR", "IN", "EXCLUSIVE", 
			"ALLOCATE", "HEAP", "AT", "DOT", "COMMA", "SEMICOLON", "DOLLAR", "ASSIGN", 
			"PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", 
			"EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", "TRUE", "FALSE", 
			"STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", 
			"GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
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
			setState(72);
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
			setState(79);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
			case WHILE:
			case IF:
			case FOR:
			case ALLOCATE:
			case DOLLAR:
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
				setState(75);
				expression();
				setState(76);
				terminator(0);
				}
				break;
			case EOF:
			case SEMICOLON:
			case CRLF:
				{
				setState(78);
				terminator(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(87);
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
					setState(81);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(82);
					expression();
					setState(83);
					terminator(0);
					}
					} 
				}
				setState(89);
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
		public For_statementContext for_statement() {
			return getRuleContext(For_statementContext.class,0);
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
		public Heap_allocationContext heap_allocation() {
			return getRuleContext(Heap_allocationContext.class,0);
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
			setState(96);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				while_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				for_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(92);
				control_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(93);
				rvalue(0);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(94);
				single_line_comment();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(95);
				heap_allocation();
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

	public static class Heap_allocationContext extends ParserRuleContext {
		public IdContext name;
		public TerminalNode ALLOCATE() { return getToken(MindcodeParser.ALLOCATE, 0); }
		public TerminalNode HEAP() { return getToken(MindcodeParser.HEAP, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public Heap_allocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_heap_allocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterHeap_allocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitHeap_allocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitHeap_allocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Heap_allocationContext heap_allocation() throws RecognitionException {
		Heap_allocationContext _localctx = new Heap_allocationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_heap_allocation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(ALLOCATE);
			setState(99);
			match(HEAP);
			setState(100);
			match(IN);
			setState(101);
			((Heap_allocationContext)_localctx).name = id();
			setState(102);
			range();
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

	public static class For_statementContext extends ParserRuleContext {
		public For_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_statement; }
	 
		public For_statementContext() { }
		public void copyFrom(For_statementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CStyleLoopContext extends For_statementContext {
		public Block_bodyContext body;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public Init_exprContext init_expr() {
			return getRuleContext(Init_exprContext.class,0);
		}
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public Cond_exprContext cond_expr() {
			return getRuleContext(Cond_exprContext.class,0);
		}
		public Loop_exprContext loop_expr() {
			return getRuleContext(Loop_exprContext.class,0);
		}
		public TerminalNode LEFT_CBRACKET() { return getToken(MindcodeParser.LEFT_CBRACKET, 0); }
		public TerminalNode RIGHT_CBRACKET() { return getToken(MindcodeParser.RIGHT_CBRACKET, 0); }
		public Block_bodyContext block_body() {
			return getRuleContext(Block_bodyContext.class,0);
		}
		public CrlfContext crlf() {
			return getRuleContext(CrlfContext.class,0);
		}
		public CStyleLoopContext(For_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCStyleLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCStyleLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCStyleLoop(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RangeStyleLoopContext extends For_statementContext {
		public LvalueContext name;
		public Block_bodyContext body;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public TerminalNode LEFT_CBRACKET() { return getToken(MindcodeParser.LEFT_CBRACKET, 0); }
		public TerminalNode RIGHT_CBRACKET() { return getToken(MindcodeParser.RIGHT_CBRACKET, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public Block_bodyContext block_body() {
			return getRuleContext(Block_bodyContext.class,0);
		}
		public CrlfContext crlf() {
			return getRuleContext(CrlfContext.class,0);
		}
		public RangeStyleLoopContext(For_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterRangeStyleLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitRangeStyleLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitRangeStyleLoop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final For_statementContext for_statement() throws RecognitionException {
		For_statementContext _localctx = new For_statementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_for_statement);
		int _la;
		try {
			setState(128);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new CStyleLoopContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(104);
				match(FOR);
				setState(105);
				init_expr();
				setState(106);
				match(SEMICOLON);
				setState(107);
				cond_expr();
				setState(108);
				match(SEMICOLON);
				setState(109);
				loop_expr();
				setState(110);
				match(LEFT_CBRACKET);
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CRLF) {
					{
					setState(111);
					crlf();
					}
				}

				setState(114);
				((CStyleLoopContext)_localctx).body = block_body();
				setState(115);
				match(RIGHT_CBRACKET);
				}
				break;
			case 2:
				_localctx = new RangeStyleLoopContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				match(FOR);
				setState(118);
				((RangeStyleLoopContext)_localctx).name = lvalue();
				setState(119);
				match(IN);
				setState(120);
				range();
				setState(121);
				match(LEFT_CBRACKET);
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CRLF) {
					{
					setState(122);
					crlf();
					}
				}

				setState(125);
				((RangeStyleLoopContext)_localctx).body = block_body();
				setState(126);
				match(RIGHT_CBRACKET);
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
	public static class ExclusiveRangeContext extends RangeContext {
		public RvalueContext firstValue;
		public RvalueContext lastValue;
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public List<RvalueContext> rvalue() {
			return getRuleContexts(RvalueContext.class);
		}
		public RvalueContext rvalue(int i) {
			return getRuleContext(RvalueContext.class,i);
		}
		public ExclusiveRangeContext(RangeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterExclusiveRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitExclusiveRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitExclusiveRange(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InclusiveRangeContext extends RangeContext {
		public RvalueContext firstValue;
		public RvalueContext lastValue;
		public List<TerminalNode> DOT() { return getTokens(MindcodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MindcodeParser.DOT, i);
		}
		public List<RvalueContext> rvalue() {
			return getRuleContexts(RvalueContext.class);
		}
		public RvalueContext rvalue(int i) {
			return getRuleContext(RvalueContext.class,i);
		}
		public InclusiveRangeContext(RangeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterInclusiveRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitInclusiveRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitInclusiveRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RangeContext range() throws RecognitionException {
		RangeContext _localctx = new RangeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_range);
		try {
			setState(141);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new ExclusiveRangeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				((ExclusiveRangeContext)_localctx).firstValue = rvalue(0);
				setState(131);
				match(DOT);
				setState(132);
				match(DOT);
				setState(133);
				match(DOT);
				setState(134);
				((ExclusiveRangeContext)_localctx).lastValue = rvalue(0);
				}
				break;
			case 2:
				_localctx = new InclusiveRangeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(136);
				((InclusiveRangeContext)_localctx).firstValue = rvalue(0);
				setState(137);
				match(DOT);
				setState(138);
				match(DOT);
				setState(139);
				((InclusiveRangeContext)_localctx).lastValue = rvalue(0);
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

	public static class Init_exprContext extends ParserRuleContext {
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public Init_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_init_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterInit_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitInit_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitInit_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Init_exprContext init_expr() throws RecognitionException {
		Init_exprContext _localctx = new Init_exprContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_init_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR || _la==ID) {
				{
				setState(143);
				assignment();
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(144);
					match(COMMA);
					setState(145);
					assignment();
					}
					}
					setState(150);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
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

	public static class Cond_exprContext extends ParserRuleContext {
		public RvalueContext rvalue() {
			return getRuleContext(RvalueContext.class,0);
		}
		public Cond_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterCond_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitCond_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitCond_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Cond_exprContext cond_expr() throws RecognitionException {
		Cond_exprContext _localctx = new Cond_exprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_cond_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
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

	public static class Loop_exprContext extends ParserRuleContext {
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public Loop_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loop_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLoop_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLoop_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLoop_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Loop_exprContext loop_expr() throws RecognitionException {
		Loop_exprContext _localctx = new Loop_exprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_loop_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DOLLAR || _la==ID) {
				{
				setState(155);
				assignment();
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(156);
					match(COMMA);
					setState(157);
					assignment();
					}
					}
					setState(162);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
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
		enterRule(_localctx, 18, RULE_control_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			((Control_statementContext)_localctx).target = id();
			setState(166);
			match(DOT);
			setState(167);
			((Control_statementContext)_localctx).property = id();
			setState(168);
			match(ASSIGN);
			setState(169);
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
		enterRule(_localctx, 20, RULE_while_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			match(WHILE);
			setState(172);
			rvalue(0);
			setState(173);
			match(LEFT_CBRACKET);
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CRLF) {
				{
				setState(174);
				crlf();
				}
			}

			setState(177);
			block_body();
			setState(178);
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
		enterRule(_localctx, 22, RULE_block_body);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
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
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_block_statement_list, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(183);
			expression();
			setState(184);
			terminator(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(192);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Block_statement_listContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_block_statement_list);
					setState(186);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(187);
					expression();
					setState(188);
					terminator(0);
					}
					} 
				}
				setState(194);
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

	public static class LvalueContext extends ParserRuleContext {
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
	 
		public LvalueContext() { }
		public void copyFrom(LvalueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LocalvarContext extends LvalueContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public LocalvarContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterLocalvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitLocalvar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitLocalvar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GlobalvarContext extends LvalueContext {
		public GlobalContext global() {
			return getRuleContext(GlobalContext.class,0);
		}
		public GlobalvarContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterGlobalvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitGlobalvar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitGlobalvar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_lvalue);
		try {
			setState(197);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				_localctx = new LocalvarContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(195);
				id();
				}
				break;
			case DOLLAR:
				_localctx = new GlobalvarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
				global();
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

	public static class GlobalContext extends ParserRuleContext {
		public IdContext name;
		public TerminalNode DOLLAR() { return getToken(MindcodeParser.DOLLAR, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public GlobalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).enterGlobal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeListener ) ((MindcodeListener)listener).exitGlobal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeVisitor ) return ((MindcodeVisitor<? extends T>)visitor).visitGlobal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlobalContext global() throws RecognitionException {
		GlobalContext _localctx = new GlobalContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_global);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(DOLLAR);
			setState(200);
			((GlobalContext)_localctx).name = id();
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
		int _startState = 30;
		enterRecursionRule(_localctx, 30, RULE_rvalue, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(203);
				lvalue();
				}
				break;
			case 2:
				{
				setState(204);
				assignment();
				}
				break;
			case 3:
				{
				setState(205);
				if_expression();
				}
				break;
			case 4:
				{
				setState(206);
				funcall();
				}
				break;
			case 5:
				{
				setState(207);
				ref();
				}
				break;
			case 6:
				{
				setState(208);
				literal_t();
				}
				break;
			case 7:
				{
				setState(209);
				bool_t();
				}
				break;
			case 8:
				{
				setState(210);
				numeric();
				}
				break;
			case 9:
				{
				setState(211);
				null_t();
				}
				break;
			case 10:
				{
				setState(212);
				sensor_read();
				}
				break;
			case 11:
				{
				setState(213);
				heap_ref();
				}
				break;
			case 12:
				{
				setState(214);
				((RvalueContext)_localctx).op = match(NOT);
				setState(215);
				rvalue(7);
				}
				break;
			case 13:
				{
				setState(216);
				match(LEFT_RBRACKET);
				setState(217);
				rvalue(0);
				setState(218);
				match(RIGHT_RBRACKET);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(242);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(240);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
					case 1:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(222);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(223);
						((RvalueContext)_localctx).op = match(EXP);
						setState(224);
						rvalue(9);
						}
						break;
					case 2:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(225);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(226);
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
						setState(227);
						rvalue(7);
						}
						break;
					case 3:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(228);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(229);
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
						setState(230);
						rvalue(6);
						}
						break;
					case 4:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(231);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(232);
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
						setState(233);
						rvalue(5);
						}
						break;
					case 5:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(234);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(235);
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
						setState(236);
						rvalue(4);
						}
						break;
					case 6:
						{
						_localctx = new RvalueContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_rvalue);
						setState(237);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(238);
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
						setState(239);
						rvalue(3);
						}
						break;
					}
					} 
				}
				setState(244);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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
		enterRule(_localctx, 32, RULE_numeric);
		try {
			setState(253);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				float_t();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(246);
				int_t();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(247);
				unary_minus();
				setState(248);
				float_t();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(250);
				unary_minus();
				setState(251);
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
		enterRule(_localctx, 34, RULE_unary_minus);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
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
		enterRule(_localctx, 36, RULE_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
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
		enterRule(_localctx, 38, RULE_funcall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(259);
			((FuncallContext)_localctx).name = id();
			setState(260);
			match(LEFT_RBRACKET);
			setState(261);
			params_list();
			setState(262);
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
		enterRule(_localctx, 40, RULE_params_list);
		int _la;
		try {
			setState(271);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(265);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LITERAL) | (1L << IF) | (1L << DOLLAR) | (1L << MINUS) | (1L << TRUE) | (1L << FALSE) | (1L << NOT) | (1L << LEFT_RBRACKET) | (1L << NULL) | (1L << INT) | (1L << FLOAT) | (1L << ID) | (1L << REF))) != 0)) {
					{
					setState(264);
					rvalue(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(267);
				rvalue(0);
				setState(268);
				match(COMMA);
				setState(269);
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
		enterRule(_localctx, 42, RULE_if_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			match(IF);
			setState(274);
			((If_expressionContext)_localctx).cond = rvalue(0);
			setState(275);
			match(LEFT_CBRACKET);
			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - -1)) & ~0x3f) == 0 && ((1L << (_la - -1)) & ((1L << (EOF - -1)) | (1L << (SEMICOLON - -1)) | (1L << (CRLF - -1)))) != 0)) {
				{
				setState(276);
				terminator(0);
				}
			}

			setState(279);
			((If_expressionContext)_localctx).true_branch = block_body();
			setState(280);
			match(RIGHT_CBRACKET);
			setState(289);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(281);
				match(ELSE);
				setState(282);
				match(LEFT_CBRACKET);
				setState(284);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - -1)) & ~0x3f) == 0 && ((1L << (_la - -1)) & ((1L << (EOF - -1)) | (1L << (SEMICOLON - -1)) | (1L << (CRLF - -1)))) != 0)) {
					{
					setState(283);
					terminator(0);
					}
				}

				setState(286);
				((If_expressionContext)_localctx).false_branch = block_body();
				setState(287);
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
		enterRule(_localctx, 44, RULE_heap_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			((Heap_refContext)_localctx).target = id();
			setState(292);
			match(LEFT_SBRACKET);
			setState(293);
			((Heap_refContext)_localctx).addr = address();
			setState(294);
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
		enterRule(_localctx, 46, RULE_address);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
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
		enterRule(_localctx, 48, RULE_sensor_read);
		try {
			setState(306);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(298);
				((Sensor_readContext)_localctx).target = id();
				setState(299);
				match(DOT);
				setState(300);
				resource();
				}
				break;
			case REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
				((Sensor_readContext)_localctx).unit = ref();
				setState(303);
				match(DOT);
				setState(304);
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
		enterRule(_localctx, 50, RULE_assignment);
		int _la;
		try {
			setState(324);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(308);
				((AssignmentContext)_localctx).target = lvalue();
				setState(309);
				match(ASSIGN);
				setState(310);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(312);
				((AssignmentContext)_localctx).target = lvalue();
				setState(313);
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
				setState(314);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(316);
				heap_ref();
				setState(317);
				match(ASSIGN);
				setState(318);
				((AssignmentContext)_localctx).value = rvalue(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(320);
				heap_ref();
				setState(321);
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
				setState(322);
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
		enterRule(_localctx, 52, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
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
		enterRule(_localctx, 54, RULE_literal_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
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
		enterRule(_localctx, 56, RULE_float_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
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
		enterRule(_localctx, 58, RULE_int_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
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
		enterRule(_localctx, 60, RULE_bool_t);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
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
		enterRule(_localctx, 62, RULE_null_t);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
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
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_terminator, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SEMICOLON:
				{
				setState(339);
				match(SEMICOLON);
				}
				break;
			case CRLF:
				{
				setState(340);
				crlf();
				}
				break;
			case EOF:
				{
				setState(341);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(350);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(348);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
					case 1:
						{
						_localctx = new TerminatorContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_terminator);
						setState(344);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(345);
						match(SEMICOLON);
						}
						break;
					case 2:
						{
						_localctx = new TerminatorContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_terminator);
						setState(346);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(347);
						crlf();
						}
						break;
					}
					} 
				}
				setState(352);
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
		enterRule(_localctx, 66, RULE_crlf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(353);
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
		enterRule(_localctx, 68, RULE_resource);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
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
		enterRule(_localctx, 70, RULE_single_line_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(357);
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
		case 12:
			return block_statement_list_sempred((Block_statement_listContext)_localctx, predIndex);
		case 15:
			return rvalue_sempred((RvalueContext)_localctx, predIndex);
		case 32:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\39\u016a\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3R\n\3\3\3"+
		"\3\3\3\3\3\3\7\3X\n\3\f\3\16\3[\13\3\3\4\3\4\3\4\3\4\3\4\3\4\5\4c\n\4"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6s\n\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6~\n\6\3\6\3\6\3\6\5\6\u0083\n\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u0090\n\7\3\b\3\b\3\b\7"+
		"\b\u0095\n\b\f\b\16\b\u0098\13\b\5\b\u009a\n\b\3\t\3\t\3\n\3\n\3\n\7\n"+
		"\u00a1\n\n\f\n\16\n\u00a4\13\n\5\n\u00a6\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\f\3\f\3\f\3\f\5\f\u00b2\n\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\7\16\u00c1\n\16\f\16\16\16\u00c4\13\16\3\17"+
		"\3\17\5\17\u00c8\n\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u00df\n\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\7\21\u00f3\n\21\f\21\16\21\u00f6\13\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0100\n\22\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\5\26\u010c\n\26\3\26\3\26\3\26\3\26\5\26\u0112"+
		"\n\26\3\27\3\27\3\27\3\27\5\27\u0118\n\27\3\27\3\27\3\27\3\27\3\27\5\27"+
		"\u011f\n\27\3\27\3\27\3\27\5\27\u0124\n\27\3\30\3\30\3\30\3\30\3\30\3"+
		"\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u0135\n\32\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\5\33\u0147\n\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3"+
		"!\3!\3\"\3\"\3\"\3\"\5\"\u0159\n\"\3\"\3\"\3\"\3\"\7\"\u015f\n\"\f\"\16"+
		"\"\u0162\13\"\3#\3#\3$\3$\3%\3%\3%\2\6\4\32 B&\2\4\6\b\n\f\16\20\22\24"+
		"\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFH\2\t\3\2\34\36\3\2\32\33"+
		"\3\2%(\3\2\"$\3\2)*\3\2\24\31\3\2 !\2\u0179\2J\3\2\2\2\4Q\3\2\2\2\6b\3"+
		"\2\2\2\bd\3\2\2\2\n\u0082\3\2\2\2\f\u008f\3\2\2\2\16\u0099\3\2\2\2\20"+
		"\u009b\3\2\2\2\22\u00a5\3\2\2\2\24\u00a7\3\2\2\2\26\u00ad\3\2\2\2\30\u00b6"+
		"\3\2\2\2\32\u00b8\3\2\2\2\34\u00c7\3\2\2\2\36\u00c9\3\2\2\2 \u00de\3\2"+
		"\2\2\"\u00ff\3\2\2\2$\u0101\3\2\2\2&\u0103\3\2\2\2(\u0105\3\2\2\2*\u0111"+
		"\3\2\2\2,\u0113\3\2\2\2.\u0125\3\2\2\2\60\u012a\3\2\2\2\62\u0134\3\2\2"+
		"\2\64\u0146\3\2\2\2\66\u0148\3\2\2\28\u014a\3\2\2\2:\u014c\3\2\2\2<\u014e"+
		"\3\2\2\2>\u0150\3\2\2\2@\u0152\3\2\2\2B\u0158\3\2\2\2D\u0163\3\2\2\2F"+
		"\u0165\3\2\2\2H\u0167\3\2\2\2JK\5\4\3\2K\3\3\2\2\2LM\b\3\1\2MN\5\6\4\2"+
		"NO\5B\"\2OR\3\2\2\2PR\5B\"\2QL\3\2\2\2QP\3\2\2\2RY\3\2\2\2ST\f\4\2\2T"+
		"U\5\6\4\2UV\5B\"\2VX\3\2\2\2WS\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z"+
		"\5\3\2\2\2[Y\3\2\2\2\\c\5\26\f\2]c\5\n\6\2^c\5\24\13\2_c\5 \21\2`c\5H"+
		"%\2ac\5\b\5\2b\\\3\2\2\2b]\3\2\2\2b^\3\2\2\2b_\3\2\2\2b`\3\2\2\2ba\3\2"+
		"\2\2c\7\3\2\2\2de\7\f\2\2ef\7\r\2\2fg\7\n\2\2gh\5\66\34\2hi\5\f\7\2i\t"+
		"\3\2\2\2jk\7\t\2\2kl\5\16\b\2lm\7\21\2\2mn\5\20\t\2no\7\21\2\2op\5\22"+
		"\n\2pr\7\60\2\2qs\5D#\2rq\3\2\2\2rs\3\2\2\2st\3\2\2\2tu\5\30\r\2uv\7\61"+
		"\2\2v\u0083\3\2\2\2wx\7\t\2\2xy\5\34\17\2yz\7\n\2\2z{\5\f\7\2{}\7\60\2"+
		"\2|~\5D#\2}|\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\5\30\r\2\u0080\u0081"+
		"\7\61\2\2\u0081\u0083\3\2\2\2\u0082j\3\2\2\2\u0082w\3\2\2\2\u0083\13\3"+
		"\2\2\2\u0084\u0085\5 \21\2\u0085\u0086\7\17\2\2\u0086\u0087\7\17\2\2\u0087"+
		"\u0088\7\17\2\2\u0088\u0089\5 \21\2\u0089\u0090\3\2\2\2\u008a\u008b\5"+
		" \21\2\u008b\u008c\7\17\2\2\u008c\u008d\7\17\2\2\u008d\u008e\5 \21\2\u008e"+
		"\u0090\3\2\2\2\u008f\u0084\3\2\2\2\u008f\u008a\3\2\2\2\u0090\r\3\2\2\2"+
		"\u0091\u0096\5\64\33\2\u0092\u0093\7\20\2\2\u0093\u0095\5\64\33\2\u0094"+
		"\u0092\3\2\2\2\u0095\u0098\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2"+
		"\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0099\u0091\3\2\2\2\u0099"+
		"\u009a\3\2\2\2\u009a\17\3\2\2\2\u009b\u009c\5 \21\2\u009c\21\3\2\2\2\u009d"+
		"\u00a2\5\64\33\2\u009e\u009f\7\20\2\2\u009f\u00a1\5\64\33\2\u00a0\u009e"+
		"\3\2\2\2\u00a1\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3"+
		"\u00a6\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u009d\3\2\2\2\u00a5\u00a6\3\2"+
		"\2\2\u00a6\23\3\2\2\2\u00a7\u00a8\5\66\34\2\u00a8\u00a9\7\17\2\2\u00a9"+
		"\u00aa\5\66\34\2\u00aa\u00ab\7\23\2\2\u00ab\u00ac\5 \21\2\u00ac\25\3\2"+
		"\2\2\u00ad\u00ae\7\6\2\2\u00ae\u00af\5 \21\2\u00af\u00b1\7\60\2\2\u00b0"+
		"\u00b2\5D#\2\u00b1\u00b0\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b3\3\2\2"+
		"\2\u00b3\u00b4\5\30\r\2\u00b4\u00b5\7\61\2\2\u00b5\27\3\2\2\2\u00b6\u00b7"+
		"\5\32\16\2\u00b7\31\3\2\2\2\u00b8\u00b9\b\16\1\2\u00b9\u00ba\5\6\4\2\u00ba"+
		"\u00bb\5B\"\2\u00bb\u00c2\3\2\2\2\u00bc\u00bd\f\3\2\2\u00bd\u00be\5\6"+
		"\4\2\u00be\u00bf\5B\"\2\u00bf\u00c1\3\2\2\2\u00c0\u00bc\3\2\2\2\u00c1"+
		"\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\33\3\2\2"+
		"\2\u00c4\u00c2\3\2\2\2\u00c5\u00c8\5\66\34\2\u00c6\u00c8\5\36\20\2\u00c7"+
		"\u00c5\3\2\2\2\u00c7\u00c6\3\2\2\2\u00c8\35\3\2\2\2\u00c9\u00ca\7\22\2"+
		"\2\u00ca\u00cb\5\66\34\2\u00cb\37\3\2\2\2\u00cc\u00cd\b\21\1\2\u00cd\u00df"+
		"\5\34\17\2\u00ce\u00df\5\64\33\2\u00cf\u00df\5,\27\2\u00d0\u00df\5(\25"+
		"\2\u00d1\u00df\5&\24\2\u00d2\u00df\58\35\2\u00d3\u00df\5> \2\u00d4\u00df"+
		"\5\"\22\2\u00d5\u00df\5@!\2\u00d6\u00df\5\62\32\2\u00d7\u00df\5.\30\2"+
		"\u00d8\u00d9\7+\2\2\u00d9\u00df\5 \21\t\u00da\u00db\7,\2\2\u00db\u00dc"+
		"\5 \21\2\u00dc\u00dd\7-\2\2\u00dd\u00df\3\2\2\2\u00de\u00cc\3\2\2\2\u00de"+
		"\u00ce\3\2\2\2\u00de\u00cf\3\2\2\2\u00de\u00d0\3\2\2\2\u00de\u00d1\3\2"+
		"\2\2\u00de\u00d2\3\2\2\2\u00de\u00d3\3\2\2\2\u00de\u00d4\3\2\2\2\u00de"+
		"\u00d5\3\2\2\2\u00de\u00d6\3\2\2\2\u00de\u00d7\3\2\2\2\u00de\u00d8\3\2"+
		"\2\2\u00de\u00da\3\2\2\2\u00df\u00f4\3\2\2\2\u00e0\u00e1\f\n\2\2\u00e1"+
		"\u00e2\7\37\2\2\u00e2\u00f3\5 \21\13\u00e3\u00e4\f\b\2\2\u00e4\u00e5\t"+
		"\2\2\2\u00e5\u00f3\5 \21\t\u00e6\u00e7\f\7\2\2\u00e7\u00e8\t\3\2\2\u00e8"+
		"\u00f3\5 \21\b\u00e9\u00ea\f\6\2\2\u00ea\u00eb\t\4\2\2\u00eb\u00f3\5 "+
		"\21\7\u00ec\u00ed\f\5\2\2\u00ed\u00ee\t\5\2\2\u00ee\u00f3\5 \21\6\u00ef"+
		"\u00f0\f\4\2\2\u00f0\u00f1\t\6\2\2\u00f1\u00f3\5 \21\5\u00f2\u00e0\3\2"+
		"\2\2\u00f2\u00e3\3\2\2\2\u00f2\u00e6\3\2\2\2\u00f2\u00e9\3\2\2\2\u00f2"+
		"\u00ec\3\2\2\2\u00f2\u00ef\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2"+
		"\2\2\u00f4\u00f5\3\2\2\2\u00f5!\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7\u0100"+
		"\5:\36\2\u00f8\u0100\5<\37\2\u00f9\u00fa\5$\23\2\u00fa\u00fb\5:\36\2\u00fb"+
		"\u0100\3\2\2\2\u00fc\u00fd\5$\23\2\u00fd\u00fe\5<\37\2\u00fe\u0100\3\2"+
		"\2\2\u00ff\u00f7\3\2\2\2\u00ff\u00f8\3\2\2\2\u00ff\u00f9\3\2\2\2\u00ff"+
		"\u00fc\3\2\2\2\u0100#\3\2\2\2\u0101\u0102\7\33\2\2\u0102%\3\2\2\2\u0103"+
		"\u0104\7\66\2\2\u0104\'\3\2\2\2\u0105\u0106\5\66\34\2\u0106\u0107\7,\2"+
		"\2\u0107\u0108\5*\26\2\u0108\u0109\7-\2\2\u0109)\3\2\2\2\u010a\u010c\5"+
		" \21\2\u010b\u010a\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u0112\3\2\2\2\u010d"+
		"\u010e\5 \21\2\u010e\u010f\7\20\2\2\u010f\u0110\5*\26\2\u0110\u0112\3"+
		"\2\2\2\u0111\u010b\3\2\2\2\u0111\u010d\3\2\2\2\u0112+\3\2\2\2\u0113\u0114"+
		"\7\7\2\2\u0114\u0115\5 \21\2\u0115\u0117\7\60\2\2\u0116\u0118\5B\"\2\u0117"+
		"\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a\5\30"+
		"\r\2\u011a\u0123\7\61\2\2\u011b\u011c\7\b\2\2\u011c\u011e\7\60\2\2\u011d"+
		"\u011f\5B\"\2\u011e\u011d\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\3\2"+
		"\2\2\u0120\u0121\5\30\r\2\u0121\u0122\7\61\2\2\u0122\u0124\3\2\2\2\u0123"+
		"\u011b\3\2\2\2\u0123\u0124\3\2\2\2\u0124-\3\2\2\2\u0125\u0126\5\66\34"+
		"\2\u0126\u0127\7.\2\2\u0127\u0128\5\60\31\2\u0128\u0129\7/\2\2\u0129/"+
		"\3\2\2\2\u012a\u012b\5 \21\2\u012b\61\3\2\2\2\u012c\u012d\5\66\34\2\u012d"+
		"\u012e\7\17\2\2\u012e\u012f\5F$\2\u012f\u0135\3\2\2\2\u0130\u0131\5&\24"+
		"\2\u0131\u0132\7\17\2\2\u0132\u0133\5F$\2\u0133\u0135\3\2\2\2\u0134\u012c"+
		"\3\2\2\2\u0134\u0130\3\2\2\2\u0135\63\3\2\2\2\u0136\u0137\5\34\17\2\u0137"+
		"\u0138\7\23\2\2\u0138\u0139\5 \21\2\u0139\u0147\3\2\2\2\u013a\u013b\5"+
		"\34\17\2\u013b\u013c\t\7\2\2\u013c\u013d\5 \21\2\u013d\u0147\3\2\2\2\u013e"+
		"\u013f\5.\30\2\u013f\u0140\7\23\2\2\u0140\u0141\5 \21\2\u0141\u0147\3"+
		"\2\2\2\u0142\u0143\5.\30\2\u0143\u0144\t\7\2\2\u0144\u0145\5 \21\2\u0145"+
		"\u0147\3\2\2\2\u0146\u0136\3\2\2\2\u0146\u013a\3\2\2\2\u0146\u013e\3\2"+
		"\2\2\u0146\u0142\3\2\2\2\u0147\65\3\2\2\2\u0148\u0149\7\65\2\2\u0149\67"+
		"\3\2\2\2\u014a\u014b\7\5\2\2\u014b9\3\2\2\2\u014c\u014d\7\64\2\2\u014d"+
		";\3\2\2\2\u014e\u014f\7\63\2\2\u014f=\3\2\2\2\u0150\u0151\t\b\2\2\u0151"+
		"?\3\2\2\2\u0152\u0153\7\62\2\2\u0153A\3\2\2\2\u0154\u0155\b\"\1\2\u0155"+
		"\u0159\7\21\2\2\u0156\u0159\5D#\2\u0157\u0159\7\2\2\3\u0158\u0154\3\2"+
		"\2\2\u0158\u0156\3\2\2\2\u0158\u0157\3\2\2\2\u0159\u0160\3\2\2\2\u015a"+
		"\u015b\f\7\2\2\u015b\u015f\7\21\2\2\u015c\u015d\f\6\2\2\u015d\u015f\5"+
		"D#\2\u015e\u015a\3\2\2\2\u015e\u015c\3\2\2\2\u015f\u0162\3\2\2\2\u0160"+
		"\u015e\3\2\2\2\u0160\u0161\3\2\2\2\u0161C\3\2\2\2\u0162\u0160\3\2\2\2"+
		"\u0163\u0164\7\67\2\2\u0164E\3\2\2\2\u0165\u0166\5\66\34\2\u0166G\3\2"+
		"\2\2\u0167\u0168\79\2\2\u0168I\3\2\2\2\36QYbr}\u0082\u008f\u0096\u0099"+
		"\u00a2\u00a5\u00b1\u00c2\u00c7\u00de\u00f2\u00f4\u00ff\u010b\u0111\u0117"+
		"\u011e\u0123\u0134\u0146\u0158\u015e\u0160";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}