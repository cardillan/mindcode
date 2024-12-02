// Generated from MindcodeParser.g4 by ANTLR 4.13.1
package info.teksol.mindcode.v3.compiler.antlr;

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
		RETURN=25, REQUIRE=26, STACK=27, THEN=28, TRUE=29, VAR=30, VOID=31, WHEN=32, 
		WHILE=33, EQUAL=34, GREATER_THAN=35, GREATER_THAN_EQUAL=36, LESS_THAN=37, 
		LESS_THAN_EQUAL=38, NOT_EQUAL=39, STRICT_EQUAL=40, STRICT_NOT_EQUAL=41, 
		BITWISE_AND=42, BITWISE_NOT=43, BITWISE_OR=44, BITWISE_XOR=45, BOOLEAN_AND=46, 
		BOOLEAN_NOT=47, BOOLEAN_OR=48, LOGICAL_AND=49, LOGICAL_NOT=50, LOGICAL_OR=51, 
		SHIFT_LEFT=52, SHIFT_RIGHT=53, LPAREN=54, RPAREN=55, LBRACKET=56, RBRACKET=57, 
		DECREMENT=58, DIV=59, IDIV=60, INCREMENT=61, MINUS=62, MOD=63, MUL=64, 
		PLUS=65, POW=66, ASSIGN=67, ASSIGN_BITWISE_AND=68, ASSIGN_BITWISE_OR=69, 
		ASSIGN_BITWISE_XOR=70, ASSIGN_BOOLEAN_AND=71, ASSIGN_BOOLEAN_OR=72, ASSIGN_DIV=73, 
		ASSIGN_IDIV=74, ASSIGN_MINUS=75, ASSIGN_MOD=76, ASSIGN_MUL=77, ASSIGN_PLUS=78, 
		ASSIGN_POW=79, ASSIGN_SHIFT_LEFT=80, ASSIGN_SHIFT_RIGHT=81, AT=82, COLON=83, 
		COMMA=84, DOLLAR=85, DOT=86, DOT2=87, DOT3=88, DOUBLEQUOTE=89, QUESTION=90, 
		SEMICOLON=91, IDENTIFIER=92, EXTIDENTIFIER=93, BUILTINIDENTIFIER=94, STRING=95, 
		BINARY=96, HEXADECIMAL=97, DECIMAL=98, FLOAT=99, HASHSET=100, FORMATTABLELITERAL=101, 
		RBRACE=102, COMMENTEDCOMMENT=103, ENHANCEDCOMMENT=104, DOC_COMMENT=105, 
		COMMENT=106, EMPTYCOMMENT=107, LINECOMMENT=108, NEWLINE=109, WHITESPACE=110, 
		ANY=111, DIRECTIVEVALUE=112, DIRECTIVEASSIGN=113, DIRECTIVECOMMA=114, 
		DIRECTIVECOMMENT=115, DIRECTIVELINECOMMENT=116, DIRECTIVEWHITESPACE=117, 
		TEXT=118, ESCAPESEQUENCE=119, EMPTYPLACEHOLDER=120, INTERPOLATION=121, 
		VARIABLEPLACEHOLDER=122, ENDOFLINE=123, VARIABLE=124, FMTENDOFLINE=125, 
		INCMTENDOFLINE=126;
	public static final int
		RULE_program = 0, RULE_expressionList = 1, RULE_expression = 2, RULE_directive = 3, 
		RULE_directiveValues = 4, RULE_directiveValue = 5, RULE_argument = 6, 
		RULE_optionalArgument = 7, RULE_argumentList = 8, RULE_lvalue = 9, RULE_formattableContents = 10, 
		RULE_formattablePlaceholder = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expressionList", "expression", "directive", "directiveValues", 
			"directiveValue", "argument", "optionalArgument", "argumentList", "lvalue", 
			"formattableContents", "formattablePlaceholder"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'begin'", "'break'", "'case'", "'const'", "'continue'", 
			"'def'", "'do'", "'elif'", "'else'", "'elseif'", "'elsif'", "'end'", 
			"'false'", "'for'", "'heap'", "'if'", "'in'", "'inline'", "'loop'", "'noinline'", 
			"'null'", "'out'", "'param'", "'return'", "'require'", "'stack'", "'then'", 
			"'true'", "'var'", "'void'", "'when'", "'while'", "'=='", "'>'", "'>='", 
			"'<'", "'<='", "'!='", "'==='", "'!=='", "'&'", "'~'", "'|'", "'^'", 
			"'&&'", "'!'", "'||'", "'and'", "'not'", "'or'", "'<<'", "'>>'", "'('", 
			"')'", "'['", "']'", "'--'", "'/'", "'\\'", "'++'", "'-'", "'%'", "'*'", 
			"'+'", "'**'", null, "'&='", "'|='", "'^='", "'&&='", "'||='", "'/='", 
			"'\\='", "'-='", "'%='", "'*='", "'+='", "'**='", "'<<='", "'>>='", "'@'", 
			"':'", null, null, "'.'", "'..'", "'...'", "'\"'", "'?'", null, null, 
			null, null, null, null, null, null, null, "'#set'", null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "'${'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BEGIN", "BREAK", "CASE", "CONST", "CONTINUE", "DEF", 
			"DO", "ELIF", "ELSE", "ELSEIF", "ELSIF", "END", "FALSE", "FOR", "HEAP", 
			"IF", "IN", "INLINE", "LOOP", "NOINLINE", "NULL", "OUT", "PARAM", "RETURN", 
			"REQUIRE", "STACK", "THEN", "TRUE", "VAR", "VOID", "WHEN", "WHILE", "EQUAL", 
			"GREATER_THAN", "GREATER_THAN_EQUAL", "LESS_THAN", "LESS_THAN_EQUAL", 
			"NOT_EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "BITWISE_AND", "BITWISE_NOT", 
			"BITWISE_OR", "BITWISE_XOR", "BOOLEAN_AND", "BOOLEAN_NOT", "BOOLEAN_OR", 
			"LOGICAL_AND", "LOGICAL_NOT", "LOGICAL_OR", "SHIFT_LEFT", "SHIFT_RIGHT", 
			"LPAREN", "RPAREN", "LBRACKET", "RBRACKET", "DECREMENT", "DIV", "IDIV", 
			"INCREMENT", "MINUS", "MOD", "MUL", "PLUS", "POW", "ASSIGN", "ASSIGN_BITWISE_AND", 
			"ASSIGN_BITWISE_OR", "ASSIGN_BITWISE_XOR", "ASSIGN_BOOLEAN_AND", "ASSIGN_BOOLEAN_OR", 
			"ASSIGN_DIV", "ASSIGN_IDIV", "ASSIGN_MINUS", "ASSIGN_MOD", "ASSIGN_MUL", 
			"ASSIGN_PLUS", "ASSIGN_POW", "ASSIGN_SHIFT_LEFT", "ASSIGN_SHIFT_RIGHT", 
			"AT", "COLON", "COMMA", "DOLLAR", "DOT", "DOT2", "DOT3", "DOUBLEQUOTE", 
			"QUESTION", "SEMICOLON", "IDENTIFIER", "EXTIDENTIFIER", "BUILTINIDENTIFIER", 
			"STRING", "BINARY", "HEXADECIMAL", "DECIMAL", "FLOAT", "HASHSET", "FORMATTABLELITERAL", 
			"RBRACE", "COMMENTEDCOMMENT", "ENHANCEDCOMMENT", "DOC_COMMENT", "COMMENT", 
			"EMPTYCOMMENT", "LINECOMMENT", "NEWLINE", "WHITESPACE", "ANY", "DIRECTIVEVALUE", 
			"DIRECTIVEASSIGN", "DIRECTIVECOMMA", "DIRECTIVECOMMENT", "DIRECTIVELINECOMMENT", 
			"DIRECTIVEWHITESPACE", "TEXT", "ESCAPESEQUENCE", "EMPTYPLACEHOLDER", 
			"INTERPOLATION", "VARIABLEPLACEHOLDER", "ENDOFLINE", "VARIABLE", "FMTENDOFLINE", 
			"INCMTENDOFLINE"
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
	public String getGrammarFileName() { return "MindcodeParser.g4"; }

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

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MindcodeParser.EOF, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 7225049244988629252L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 687127658497L) != 0)) {
				{
				setState(24);
				expressionList();
				}
			}

			setState(27);
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
	public static class ExpressionListContext extends ParserRuleContext {
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpressionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpressionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expressionList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(33); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(30);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 7225049244988629252L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 687060549633L) != 0)) {
						{
						setState(29);
						expression(0);
						}
					}

					setState(32);
					match(SEMICOLON);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(35); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
	public static class ExpRequireFileContext extends ExpressionContext {
		public Token file;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public TerminalNode STRING() { return getToken(MindcodeParser.STRING, 0); }
		public ExpRequireFileContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpRequireFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpRequireFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpRequireFile(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBooleanLiteralTrueContext extends ExpressionContext {
		public TerminalNode TRUE() { return getToken(MindcodeParser.TRUE, 0); }
		public ExpBooleanLiteralTrueContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBooleanLiteralTrue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBooleanLiteralTrue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBooleanLiteralTrue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpMultiplicationContext extends ExpressionContext {
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
		public ExpMultiplicationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpMultiplication(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpMultiplication(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpMultiplication(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpAssignmentContext extends ExpressionContext {
		public ExpressionContext target;
		public ExpressionContext value;
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpAssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpExponentiationContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode POW() { return getToken(MindcodeParser.POW, 0); }
		public ExpExponentiationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpExponentiation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpExponentiation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpExponentiation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpRequireLibraryContext extends ExpressionContext {
		public Token library;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpRequireLibraryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpRequireLibrary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpRequireLibrary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpRequireLibrary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpWhileLoopContext extends ExpressionContext {
		public Token label;
		public ExpressionContext condition;
		public ExpressionListContext body;
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ExpWhileLoopContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpWhileLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpWhileLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpWhileLoop(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpTernaryContext extends ExpressionContext {
		public ExpressionContext condition;
		public ExpressionContext trueBranch;
		public ExpressionContext falseBranch;
		public TerminalNode QUESTION() { return getToken(MindcodeParser.QUESTION, 0); }
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpTernaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpTernary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpTernary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpTernary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpDirectiveContext extends ExpressionContext {
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public ExpDirectiveContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpDirective(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpPrefixContext extends ExpressionContext {
		public Token prefix;
		public LvalueContext exp;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode INCREMENT() { return getToken(MindcodeParser.INCREMENT, 0); }
		public TerminalNode DECREMENT() { return getToken(MindcodeParser.DECREMENT, 0); }
		public ExpPrefixContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpPrefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpPrefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpPrefix(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpUnaryContext extends ExpressionContext {
		public Token op;
		public ExpressionContext exp;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BITWISE_NOT() { return getToken(MindcodeParser.BITWISE_NOT, 0); }
		public TerminalNode BOOLEAN_NOT() { return getToken(MindcodeParser.BOOLEAN_NOT, 0); }
		public TerminalNode LOGICAL_NOT() { return getToken(MindcodeParser.LOGICAL_NOT, 0); }
		public TerminalNode PLUS() { return getToken(MindcodeParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(MindcodeParser.MINUS, 0); }
		public ExpUnaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpUnary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpDecimalLiteralContext extends ExpressionContext {
		public TerminalNode DECIMAL() { return getToken(MindcodeParser.DECIMAL, 0); }
		public ExpDecimalLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpNullLiteralContext extends ExpressionContext {
		public TerminalNode NULL() { return getToken(MindcodeParser.NULL, 0); }
		public ExpNullLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpNullLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpNullLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpNullLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpFormattableLiteralContext extends ExpressionContext {
		public TerminalNode FORMATTABLELITERAL() { return getToken(MindcodeParser.FORMATTABLELITERAL, 0); }
		public TerminalNode DOUBLEQUOTE() { return getToken(MindcodeParser.DOUBLEQUOTE, 0); }
		public List<FormattableContentsContext> formattableContents() {
			return getRuleContexts(FormattableContentsContext.class);
		}
		public FormattableContentsContext formattableContents(int i) {
			return getRuleContext(FormattableContentsContext.class,i);
		}
		public ExpFormattableLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpFormattableLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpFormattableLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpFormattableLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpEnhancedCommentContext extends ExpressionContext {
		public TerminalNode ENHANCEDCOMMENT() { return getToken(MindcodeParser.ENHANCEDCOMMENT, 0); }
		public List<FormattableContentsContext> formattableContents() {
			return getRuleContexts(FormattableContentsContext.class);
		}
		public FormattableContentsContext formattableContents(int i) {
			return getRuleContext(FormattableContentsContext.class,i);
		}
		public ExpEnhancedCommentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpEnhancedComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpEnhancedComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpEnhancedComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpInequalityRelationContext extends ExpressionContext {
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
		public ExpInequalityRelationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpInequalityRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpInequalityRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpInequalityRelation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpEqualityRelationContext extends ExpressionContext {
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
		public ExpEqualityRelationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpEqualityRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpEqualityRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpEqualityRelation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpCodeBlockContext extends ExpressionContext {
		public ExpressionListContext exp;
		public TerminalNode BEGIN() { return getToken(MindcodeParser.BEGIN, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ExpCodeBlockContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpCodeBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpCodeBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpCodeBlock(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpCallFunctionContext extends ExpressionContext {
		public Token function;
		public ArgumentListContext args;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ExpCallFunctionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpCallFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpCallFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpCallFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBitwiseOrContext extends ExpressionContext {
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
		public ExpBitwiseOrContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBitwiseOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBitwiseOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBitwiseOr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpFloatLiteralContext extends ExpressionContext {
		public TerminalNode FLOAT() { return getToken(MindcodeParser.FLOAT, 0); }
		public ExpFloatLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpFloatLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpFloatLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpFloatLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpPostfixContext extends ExpressionContext {
		public LvalueContext exp;
		public Token postfix;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode INCREMENT() { return getToken(MindcodeParser.INCREMENT, 0); }
		public TerminalNode DECREMENT() { return getToken(MindcodeParser.DECREMENT, 0); }
		public ExpPostfixContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpPostfix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpPostfix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpPostfix(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpLvalueContext extends ExpressionContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpLvalueContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpLvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpLvalue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpLvalue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpLogicalOrContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode BOOLEAN_OR() { return getToken(MindcodeParser.BOOLEAN_OR, 0); }
		public TerminalNode LOGICAL_OR() { return getToken(MindcodeParser.LOGICAL_OR, 0); }
		public ExpLogicalOrContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpLogicalOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpLogicalOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpLogicalOr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpMemeberAccessContext extends ExpressionContext {
		public ExpressionContext object;
		public Token member;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpMemeberAccessContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpMemeberAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpMemeberAccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpMemeberAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBitwiseAndContext extends ExpressionContext {
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
		public ExpBitwiseAndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBitwiseAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBitwiseAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBitwiseAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpDoWhileLoopContext extends ExpressionContext {
		public Token label;
		public ExpressionListContext body;
		public ExpressionContext condition;
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode LOOP() { return getToken(MindcodeParser.LOOP, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ExpDoWhileLoopContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpDoWhileLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpDoWhileLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpDoWhileLoop(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpCallMethodContext extends ExpressionContext {
		public ExpressionContext object;
		public Token function;
		public ArgumentListContext args;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ExpCallMethodContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpCallMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpCallMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpCallMethod(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpHexadecimalLiteralContext extends ExpressionContext {
		public TerminalNode HEXADECIMAL() { return getToken(MindcodeParser.HEXADECIMAL, 0); }
		public ExpHexadecimalLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpHexadecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpHexadecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpHexadecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBooleanLiteralFalseContext extends ExpressionContext {
		public TerminalNode FALSE() { return getToken(MindcodeParser.FALSE, 0); }
		public ExpBooleanLiteralFalseContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBooleanLiteralFalse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBooleanLiteralFalse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBooleanLiteralFalse(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpAdditionContext extends ExpressionContext {
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
		public ExpAdditionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpAddition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpAddition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpAddition(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpPropertyAccessContext extends ExpressionContext {
		public ExpressionContext object;
		public Token property;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BUILTINIDENTIFIER() { return getToken(MindcodeParser.BUILTINIDENTIFIER, 0); }
		public ExpPropertyAccessContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpPropertyAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpPropertyAccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpPropertyAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpCompoundAssignmentContext extends ExpressionContext {
		public LvalueContext target;
		public Token operation;
		public ExpressionContext value;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ASSIGN_POW() { return getToken(MindcodeParser.ASSIGN_POW, 0); }
		public TerminalNode ASSIGN_MUL() { return getToken(MindcodeParser.ASSIGN_MUL, 0); }
		public TerminalNode ASSIGN_DIV() { return getToken(MindcodeParser.ASSIGN_DIV, 0); }
		public TerminalNode ASSIGN_IDIV() { return getToken(MindcodeParser.ASSIGN_IDIV, 0); }
		public TerminalNode ASSIGN_MOD() { return getToken(MindcodeParser.ASSIGN_MOD, 0); }
		public TerminalNode ASSIGN_PLUS() { return getToken(MindcodeParser.ASSIGN_PLUS, 0); }
		public TerminalNode ASSIGN_MINUS() { return getToken(MindcodeParser.ASSIGN_MINUS, 0); }
		public TerminalNode ASSIGN_SHIFT_LEFT() { return getToken(MindcodeParser.ASSIGN_SHIFT_LEFT, 0); }
		public TerminalNode ASSIGN_SHIFT_RIGHT() { return getToken(MindcodeParser.ASSIGN_SHIFT_RIGHT, 0); }
		public TerminalNode ASSIGN_BITWISE_AND() { return getToken(MindcodeParser.ASSIGN_BITWISE_AND, 0); }
		public TerminalNode ASSIGN_BITWISE_OR() { return getToken(MindcodeParser.ASSIGN_BITWISE_OR, 0); }
		public TerminalNode ASSIGN_BITWISE_XOR() { return getToken(MindcodeParser.ASSIGN_BITWISE_XOR, 0); }
		public TerminalNode ASSIGN_BOOLEAN_AND() { return getToken(MindcodeParser.ASSIGN_BOOLEAN_AND, 0); }
		public TerminalNode ASSIGN_BOOLEAN_OR() { return getToken(MindcodeParser.ASSIGN_BOOLEAN_OR, 0); }
		public ExpCompoundAssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpCompoundAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpCompoundAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpCompoundAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpLogicalAndContext extends ExpressionContext {
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode BOOLEAN_AND() { return getToken(MindcodeParser.BOOLEAN_AND, 0); }
		public TerminalNode LOGICAL_AND() { return getToken(MindcodeParser.LOGICAL_AND, 0); }
		public ExpLogicalAndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpLogicalAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpLogicalAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpLogicalAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpParenthesesContext extends ExpressionContext {
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public ExpParenthesesContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpParentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpParentheses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpStringLiteralContext extends ExpressionContext {
		public TerminalNode STRING() { return getToken(MindcodeParser.STRING, 0); }
		public ExpStringLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBitShiftContext extends ExpressionContext {
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
		public ExpBitShiftContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBitShift(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBitShift(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBitShift(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpCallEndContext extends ExpressionContext {
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public ExpCallEndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpCallEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpCallEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpCallEnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBinaryLiteralContext extends ExpressionContext {
		public TerminalNode BINARY() { return getToken(MindcodeParser.BINARY, 0); }
		public ExpBinaryLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBinaryLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBinaryLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBinaryLiteral(this);
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
			setState(117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				_localctx = new ExpDirectiveContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(38);
				directive();
				}
				break;
			case 2:
				{
				_localctx = new ExpRequireLibraryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(39);
				match(REQUIRE);
				setState(40);
				((ExpRequireLibraryContext)_localctx).library = match(IDENTIFIER);
				}
				break;
			case 3:
				{
				_localctx = new ExpRequireFileContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(41);
				match(REQUIRE);
				setState(42);
				((ExpRequireFileContext)_localctx).file = match(STRING);
				}
				break;
			case 4:
				{
				_localctx = new ExpCodeBlockContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(43);
				match(BEGIN);
				setState(45);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
				case 1:
					{
					setState(44);
					((ExpCodeBlockContext)_localctx).exp = expressionList();
					}
					break;
				}
				setState(47);
				match(END);
				}
				break;
			case 5:
				{
				_localctx = new ExpCallEndContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(48);
				match(END);
				setState(49);
				match(LPAREN);
				setState(50);
				match(RPAREN);
				}
				break;
			case 6:
				{
				_localctx = new ExpCallFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(51);
				((ExpCallFunctionContext)_localctx).function = match(IDENTIFIER);
				setState(52);
				((ExpCallFunctionContext)_localctx).args = argumentList();
				}
				break;
			case 7:
				{
				_localctx = new ExpLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(53);
				lvalue();
				}
				break;
			case 8:
				{
				_localctx = new ExpEnhancedCommentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(54);
				match(ENHANCEDCOMMENT);
				setState(58);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(55);
						formattableContents();
						}
						} 
					}
					setState(60);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				}
				}
				break;
			case 9:
				{
				_localctx = new ExpFormattableLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(61);
				match(FORMATTABLELITERAL);
				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 118)) & ~0x3f) == 0 && ((1L << (_la - 118)) & 31L) != 0)) {
					{
					{
					setState(62);
					formattableContents();
					}
					}
					setState(67);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(68);
				match(DOUBLEQUOTE);
				}
				break;
			case 10:
				{
				_localctx = new ExpStringLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(69);
				match(STRING);
				}
				break;
			case 11:
				{
				_localctx = new ExpBinaryLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(70);
				match(BINARY);
				}
				break;
			case 12:
				{
				_localctx = new ExpHexadecimalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(71);
				match(HEXADECIMAL);
				}
				break;
			case 13:
				{
				_localctx = new ExpDecimalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(72);
				match(DECIMAL);
				}
				break;
			case 14:
				{
				_localctx = new ExpFloatLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(73);
				match(FLOAT);
				}
				break;
			case 15:
				{
				_localctx = new ExpNullLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(74);
				match(NULL);
				}
				break;
			case 16:
				{
				_localctx = new ExpBooleanLiteralTrueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(75);
				match(TRUE);
				}
				break;
			case 17:
				{
				_localctx = new ExpBooleanLiteralFalseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(76);
				match(FALSE);
				}
				break;
			case 18:
				{
				_localctx = new ExpWhileLoopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(77);
					((ExpWhileLoopContext)_localctx).label = match(IDENTIFIER);
					setState(78);
					match(COLON);
					}
				}

				setState(81);
				match(WHILE);
				setState(82);
				((ExpWhileLoopContext)_localctx).condition = expression(0);
				setState(83);
				match(DO);
				setState(85);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(84);
					((ExpWhileLoopContext)_localctx).body = expressionList();
					}
					break;
				}
				setState(87);
				match(END);
				}
				break;
			case 19:
				{
				_localctx = new ExpDoWhileLoopContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(89);
					((ExpDoWhileLoopContext)_localctx).label = match(IDENTIFIER);
					setState(90);
					match(COLON);
					}
				}

				setState(93);
				match(DO);
				setState(95);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(94);
					((ExpDoWhileLoopContext)_localctx).body = expressionList();
					}
					break;
				}
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOOP) {
					{
					setState(97);
					match(LOOP);
					}
				}

				setState(100);
				match(WHILE);
				setState(101);
				((ExpDoWhileLoopContext)_localctx).condition = expression(18);
				}
				break;
			case 20:
				{
				_localctx = new ExpPostfixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(102);
				((ExpPostfixContext)_localctx).exp = lvalue();
				setState(103);
				((ExpPostfixContext)_localctx).postfix = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DECREMENT || _la==INCREMENT) ) {
					((ExpPostfixContext)_localctx).postfix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 21:
				{
				_localctx = new ExpPrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(105);
				((ExpPrefixContext)_localctx).prefix = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DECREMENT || _la==INCREMENT) ) {
					((ExpPrefixContext)_localctx).prefix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(106);
				((ExpPrefixContext)_localctx).exp = lvalue();
				}
				break;
			case 22:
				{
				_localctx = new ExpUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107);
				((ExpUnaryContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 43)) & ~0x3f) == 0 && ((1L << (_la - 43)) & 4718737L) != 0)) ) {
					((ExpUnaryContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(108);
				((ExpUnaryContext)_localctx).exp = expression(15);
				}
				break;
			case 23:
				{
				_localctx = new ExpCompoundAssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(109);
				((ExpCompoundAssignmentContext)_localctx).target = lvalue();
				setState(110);
				((ExpCompoundAssignmentContext)_localctx).operation = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 68)) & ~0x3f) == 0 && ((1L << (_la - 68)) & 16383L) != 0)) ) {
					((ExpCompoundAssignmentContext)_localctx).operation = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(111);
				((ExpCompoundAssignmentContext)_localctx).value = expression(2);
				}
				break;
			case 24:
				{
				_localctx = new ExpParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(113);
				match(LPAREN);
				setState(114);
				expression(0);
				setState(115);
				match(RPAREN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(170);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(168);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						_localctx = new ExpExponentiationContext(new ExpressionContext(_parentctx, _parentState));
						((ExpExponentiationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(119);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(120);
						((ExpExponentiationContext)_localctx).op = match(POW);
						setState(121);
						((ExpExponentiationContext)_localctx).right = expression(15);
						}
						break;
					case 2:
						{
						_localctx = new ExpMultiplicationContext(new ExpressionContext(_parentctx, _parentState));
						((ExpMultiplicationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(122);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(123);
						((ExpMultiplicationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 59)) & ~0x3f) == 0 && ((1L << (_la - 59)) & 51L) != 0)) ) {
							((ExpMultiplicationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(124);
						((ExpMultiplicationContext)_localctx).right = expression(14);
						}
						break;
					case 3:
						{
						_localctx = new ExpAdditionContext(new ExpressionContext(_parentctx, _parentState));
						((ExpAdditionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(125);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(126);
						((ExpAdditionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MINUS || _la==PLUS) ) {
							((ExpAdditionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(127);
						((ExpAdditionContext)_localctx).right = expression(13);
						}
						break;
					case 4:
						{
						_localctx = new ExpBitShiftContext(new ExpressionContext(_parentctx, _parentState));
						((ExpBitShiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(128);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(129);
						((ExpBitShiftContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==SHIFT_LEFT || _la==SHIFT_RIGHT) ) {
							((ExpBitShiftContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(130);
						((ExpBitShiftContext)_localctx).right = expression(12);
						}
						break;
					case 5:
						{
						_localctx = new ExpBitwiseAndContext(new ExpressionContext(_parentctx, _parentState));
						((ExpBitwiseAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(131);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(132);
						((ExpBitwiseAndContext)_localctx).op = match(BITWISE_AND);
						setState(133);
						((ExpBitwiseAndContext)_localctx).right = expression(11);
						}
						break;
					case 6:
						{
						_localctx = new ExpBitwiseOrContext(new ExpressionContext(_parentctx, _parentState));
						((ExpBitwiseOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(134);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(135);
						((ExpBitwiseOrContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BITWISE_OR || _la==BITWISE_XOR) ) {
							((ExpBitwiseOrContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(136);
						((ExpBitwiseOrContext)_localctx).right = expression(10);
						}
						break;
					case 7:
						{
						_localctx = new ExpInequalityRelationContext(new ExpressionContext(_parentctx, _parentState));
						((ExpInequalityRelationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(137);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(138);
						((ExpInequalityRelationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 515396075520L) != 0)) ) {
							((ExpInequalityRelationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(139);
						((ExpInequalityRelationContext)_localctx).right = expression(9);
						}
						break;
					case 8:
						{
						_localctx = new ExpEqualityRelationContext(new ExpressionContext(_parentctx, _parentState));
						((ExpEqualityRelationContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(140);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(141);
						((ExpEqualityRelationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 3865470566400L) != 0)) ) {
							((ExpEqualityRelationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(142);
						((ExpEqualityRelationContext)_localctx).right = expression(8);
						}
						break;
					case 9:
						{
						_localctx = new ExpLogicalAndContext(new ExpressionContext(_parentctx, _parentState));
						((ExpLogicalAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(143);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(144);
						((ExpLogicalAndContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BOOLEAN_AND || _la==LOGICAL_AND) ) {
							((ExpLogicalAndContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(145);
						((ExpLogicalAndContext)_localctx).right = expression(7);
						}
						break;
					case 10:
						{
						_localctx = new ExpLogicalOrContext(new ExpressionContext(_parentctx, _parentState));
						((ExpLogicalOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(146);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(147);
						((ExpLogicalOrContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BOOLEAN_OR || _la==LOGICAL_OR) ) {
							((ExpLogicalOrContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(148);
						((ExpLogicalOrContext)_localctx).right = expression(6);
						}
						break;
					case 11:
						{
						_localctx = new ExpTernaryContext(new ExpressionContext(_parentctx, _parentState));
						((ExpTernaryContext)_localctx).condition = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(149);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(150);
						match(QUESTION);
						setState(151);
						((ExpTernaryContext)_localctx).trueBranch = expression(0);
						setState(152);
						match(COLON);
						setState(153);
						((ExpTernaryContext)_localctx).falseBranch = expression(4);
						}
						break;
					case 12:
						{
						_localctx = new ExpAssignmentContext(new ExpressionContext(_parentctx, _parentState));
						((ExpAssignmentContext)_localctx).target = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(155);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(156);
						match(ASSIGN);
						setState(157);
						((ExpAssignmentContext)_localctx).value = expression(3);
						}
						break;
					case 13:
						{
						_localctx = new ExpCallMethodContext(new ExpressionContext(_parentctx, _parentState));
						((ExpCallMethodContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(158);
						if (!(precpred(_ctx, 33))) throw new FailedPredicateException(this, "precpred(_ctx, 33)");
						setState(159);
						match(DOT);
						setState(160);
						((ExpCallMethodContext)_localctx).function = match(IDENTIFIER);
						setState(161);
						((ExpCallMethodContext)_localctx).args = argumentList();
						}
						break;
					case 14:
						{
						_localctx = new ExpMemeberAccessContext(new ExpressionContext(_parentctx, _parentState));
						((ExpMemeberAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(162);
						if (!(precpred(_ctx, 32))) throw new FailedPredicateException(this, "precpred(_ctx, 32)");
						setState(163);
						match(DOT);
						setState(164);
						((ExpMemeberAccessContext)_localctx).member = match(IDENTIFIER);
						}
						break;
					case 15:
						{
						_localctx = new ExpPropertyAccessContext(new ExpressionContext(_parentctx, _parentState));
						((ExpPropertyAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(165);
						if (!(precpred(_ctx, 31))) throw new FailedPredicateException(this, "precpred(_ctx, 31)");
						setState(166);
						match(DOT);
						setState(167);
						((ExpPropertyAccessContext)_localctx).property = match(BUILTINIDENTIFIER);
						}
						break;
					}
					} 
				}
				setState(172);
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
	public static class DirectiveSetContext extends DirectiveContext {
		public DirectiveValueContext option;
		public DirectiveValuesContext value;
		public TerminalNode HASHSET() { return getToken(MindcodeParser.HASHSET, 0); }
		public DirectiveValueContext directiveValue() {
			return getRuleContext(DirectiveValueContext.class,0);
		}
		public TerminalNode DIRECTIVEASSIGN() { return getToken(MindcodeParser.DIRECTIVEASSIGN, 0); }
		public DirectiveValuesContext directiveValues() {
			return getRuleContext(DirectiveValuesContext.class,0);
		}
		public DirectiveSetContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDirectiveSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDirectiveSet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDirectiveSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_directive);
		try {
			_localctx = new DirectiveSetContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			match(HASHSET);
			setState(174);
			((DirectiveSetContext)_localctx).option = directiveValue();
			setState(177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(175);
				match(DIRECTIVEASSIGN);
				setState(176);
				((DirectiveSetContext)_localctx).value = directiveValues();
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
	public static class DirectiveValuesContext extends ParserRuleContext {
		public DirectiveValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveValues; }
	 
		public DirectiveValuesContext() { }
		public void copyFrom(DirectiveValuesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DirectiveValueListContext extends DirectiveValuesContext {
		public List<DirectiveValueContext> directiveValue() {
			return getRuleContexts(DirectiveValueContext.class);
		}
		public DirectiveValueContext directiveValue(int i) {
			return getRuleContext(DirectiveValueContext.class,i);
		}
		public List<TerminalNode> DIRECTIVECOMMA() { return getTokens(MindcodeParser.DIRECTIVECOMMA); }
		public TerminalNode DIRECTIVECOMMA(int i) {
			return getToken(MindcodeParser.DIRECTIVECOMMA, i);
		}
		public DirectiveValueListContext(DirectiveValuesContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDirectiveValueList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDirectiveValueList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDirectiveValueList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveValuesContext directiveValues() throws RecognitionException {
		DirectiveValuesContext _localctx = new DirectiveValuesContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_directiveValues);
		try {
			int _alt;
			_localctx = new DirectiveValueListContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			directiveValue();
			setState(184);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(180);
					match(DIRECTIVECOMMA);
					setState(181);
					directiveValue();
					}
					} 
				}
				setState(186);
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
	public static class DirectiveValueContext extends ParserRuleContext {
		public TerminalNode DIRECTIVEVALUE() { return getToken(MindcodeParser.DIRECTIVEVALUE, 0); }
		public DirectiveValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDirectiveValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDirectiveValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDirectiveValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveValueContext directiveValue() throws RecognitionException {
		DirectiveValueContext _localctx = new DirectiveValueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_directiveValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(DIRECTIVEVALUE);
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
	public static class ArgumentContext extends ParserRuleContext {
		public Token modifier_in;
		public Token modifier_out;
		public ExpressionContext arg;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_argument);
		int _la;
		try {
			setState(199);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(189);
					((ArgumentContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(193);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(192);
					((ArgumentContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(195);
				((ArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
				((ArgumentContext)_localctx).modifier_out = match(OUT);
				setState(197);
				((ArgumentContext)_localctx).modifier_in = match(IN);
				setState(198);
				((ArgumentContext)_localctx).arg = expression(0);
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
	public static class OptionalArgumentContext extends ParserRuleContext {
		public ArgumentContext argument() {
			return getRuleContext(ArgumentContext.class,0);
		}
		public OptionalArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionalArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterOptionalArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitOptionalArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitOptionalArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptionalArgumentContext optionalArgument() throws RecognitionException {
		OptionalArgumentContext _localctx = new OptionalArgumentContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_optionalArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 7225049244997280004L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 687060549633L) != 0)) {
				{
				setState(201);
				argument();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentListContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public ArgumentContext argument() {
			return getRuleContext(ArgumentContext.class,0);
		}
		public List<OptionalArgumentContext> optionalArgument() {
			return getRuleContexts(OptionalArgumentContext.class);
		}
		public OptionalArgumentContext optionalArgument(int i) {
			return getRuleContext(OptionalArgumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitArgumentList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitArgumentList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_argumentList);
		try {
			int _alt;
			setState(221);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(204);
				match(LPAREN);
				setState(205);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(206);
				match(LPAREN);
				setState(207);
				argument();
				setState(208);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(210);
				match(LPAREN);
				setState(214); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(211);
						optionalArgument();
						setState(212);
						match(COMMA);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(216); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(218);
				optionalArgument();
				setState(219);
				match(RPAREN);
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
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
	 
		public LvalueContext() { }
		public void copyFrom(LvalueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpArrayAccessContext extends LvalueContext {
		public Token array;
		public ExpressionContext index;
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpArrayAccessContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpArrayAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpArrayAccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpArrayAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpIdentifierExtContext extends LvalueContext {
		public Token id;
		public TerminalNode EXTIDENTIFIER() { return getToken(MindcodeParser.EXTIDENTIFIER, 0); }
		public ExpIdentifierExtContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpIdentifierExt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpIdentifierExt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpIdentifierExt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpIdentifierContext extends LvalueContext {
		public Token id;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpIdentifierContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBuiltInIdentifierContext extends LvalueContext {
		public Token builtin;
		public TerminalNode BUILTINIDENTIFIER() { return getToken(MindcodeParser.BUILTINIDENTIFIER, 0); }
		public ExpBuiltInIdentifierContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpBuiltInIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpBuiltInIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpBuiltInIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lvalue);
		try {
			setState(231);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new ExpIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(223);
				((ExpIdentifierContext)_localctx).id = match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new ExpIdentifierExtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(224);
				((ExpIdentifierExtContext)_localctx).id = match(EXTIDENTIFIER);
				}
				break;
			case 3:
				_localctx = new ExpBuiltInIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(225);
				((ExpBuiltInIdentifierContext)_localctx).builtin = match(BUILTINIDENTIFIER);
				}
				break;
			case 4:
				_localctx = new ExpArrayAccessContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(226);
				((ExpArrayAccessContext)_localctx).array = match(IDENTIFIER);
				setState(227);
				match(LBRACKET);
				setState(228);
				((ExpArrayAccessContext)_localctx).index = expression(0);
				setState(229);
				match(RBRACKET);
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
	public static class FormattableContentsContext extends ParserRuleContext {
		public FormattableContentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formattableContents; }
	 
		public FormattableContentsContext() { }
		public void copyFrom(FormattableContentsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FmtInterpolationContext extends FormattableContentsContext {
		public TerminalNode INTERPOLATION() { return getToken(MindcodeParser.INTERPOLATION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(MindcodeParser.RBRACE, 0); }
		public FmtInterpolationContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFmtInterpolation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFmtInterpolation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFmtInterpolation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FmtTextContext extends FormattableContentsContext {
		public TerminalNode TEXT() { return getToken(MindcodeParser.TEXT, 0); }
		public FmtTextContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFmtText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFmtText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFmtText(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FmtEscapedContext extends FormattableContentsContext {
		public TerminalNode ESCAPESEQUENCE() { return getToken(MindcodeParser.ESCAPESEQUENCE, 0); }
		public FmtEscapedContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFmtEscaped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFmtEscaped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFmtEscaped(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FmtPlaceholderContext extends FormattableContentsContext {
		public FormattablePlaceholderContext formattablePlaceholder() {
			return getRuleContext(FormattablePlaceholderContext.class,0);
		}
		public FmtPlaceholderContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFmtPlaceholder(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFmtPlaceholder(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFmtPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormattableContentsContext formattableContents() throws RecognitionException {
		FormattableContentsContext _localctx = new FormattableContentsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_formattableContents);
		try {
			setState(240);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT:
				_localctx = new FmtTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(233);
				match(TEXT);
				}
				break;
			case ESCAPESEQUENCE:
				_localctx = new FmtEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(234);
				match(ESCAPESEQUENCE);
				}
				break;
			case INTERPOLATION:
				_localctx = new FmtInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(235);
				match(INTERPOLATION);
				setState(236);
				expression(0);
				setState(237);
				match(RBRACE);
				}
				break;
			case EMPTYPLACEHOLDER:
			case VARIABLEPLACEHOLDER:
				_localctx = new FmtPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(239);
				formattablePlaceholder();
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
	public static class FormattablePlaceholderContext extends ParserRuleContext {
		public FormattablePlaceholderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formattablePlaceholder; }
	 
		public FormattablePlaceholderContext() { }
		public void copyFrom(FormattablePlaceholderContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FmtPlaceholderVariableContext extends FormattablePlaceholderContext {
		public Token id;
		public TerminalNode VARIABLEPLACEHOLDER() { return getToken(MindcodeParser.VARIABLEPLACEHOLDER, 0); }
		public TerminalNode VARIABLE() { return getToken(MindcodeParser.VARIABLE, 0); }
		public FmtPlaceholderVariableContext(FormattablePlaceholderContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFmtPlaceholderVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFmtPlaceholderVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFmtPlaceholderVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FmtPlaceholderEmptyContext extends FormattablePlaceholderContext {
		public TerminalNode EMPTYPLACEHOLDER() { return getToken(MindcodeParser.EMPTYPLACEHOLDER, 0); }
		public FmtPlaceholderEmptyContext(FormattablePlaceholderContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFmtPlaceholderEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFmtPlaceholderEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFmtPlaceholderEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormattablePlaceholderContext formattablePlaceholder() throws RecognitionException {
		FormattablePlaceholderContext _localctx = new FormattablePlaceholderContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_formattablePlaceholder);
		try {
			setState(247);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EMPTYPLACEHOLDER:
				_localctx = new FmtPlaceholderEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(242);
				match(EMPTYPLACEHOLDER);
				}
				break;
			case VARIABLEPLACEHOLDER:
				_localctx = new FmtPlaceholderVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(243);
				match(VARIABLEPLACEHOLDER);
				setState(245);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(244);
					((FmtPlaceholderVariableContext)_localctx).id = match(VARIABLE);
					}
					break;
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 14);
		case 1:
			return precpred(_ctx, 13);
		case 2:
			return precpred(_ctx, 12);
		case 3:
			return precpred(_ctx, 11);
		case 4:
			return precpred(_ctx, 10);
		case 5:
			return precpred(_ctx, 9);
		case 6:
			return precpred(_ctx, 8);
		case 7:
			return precpred(_ctx, 7);
		case 8:
			return precpred(_ctx, 6);
		case 9:
			return precpred(_ctx, 5);
		case 10:
			return precpred(_ctx, 4);
		case 11:
			return precpred(_ctx, 3);
		case 12:
			return precpred(_ctx, 33);
		case 13:
			return precpred(_ctx, 32);
		case 14:
			return precpred(_ctx, 31);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001~\u00fa\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0003\u0000\u001a\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0003"+
		"\u0001\u001f\b\u0001\u0001\u0001\u0004\u0001\"\b\u0001\u000b\u0001\f\u0001"+
		"#\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002.\b\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0005\u00029\b\u0002\n\u0002\f\u0002<\t\u0002\u0001\u0002"+
		"\u0001\u0002\u0005\u0002@\b\u0002\n\u0002\f\u0002C\t\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002P\b\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002V\b\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\\\b\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002`\b\u0002\u0001\u0002\u0003\u0002"+
		"c\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002v\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\u00a9\b\u0002\n\u0002"+
		"\f\u0002\u00ac\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003\u00b2\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004"+
		"\u00b7\b\u0004\n\u0004\f\u0004\u00ba\t\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0003\u0006\u00bf\b\u0006\u0001\u0006\u0003\u0006\u00c2\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00c8\b\u0006"+
		"\u0001\u0007\u0003\u0007\u00cb\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0004\b\u00d7\b\b\u000b"+
		"\b\f\b\u00d8\u0001\b\u0001\b\u0001\b\u0003\b\u00de\b\b\u0001\t\u0001\t"+
		"\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u00e8\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00f1\b\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00f6\b\u000b\u0003\u000b\u00f8"+
		"\b\u000b\u0001\u000b\u0000\u0001\u0004\f\u0000\u0002\u0004\u0006\b\n\f"+
		"\u000e\u0010\u0012\u0014\u0016\u0000\u000b\u0002\u0000::==\u0005\u0000"+
		"++//22>>AA\u0001\u0000DQ\u0002\u0000;<?@\u0002\u0000>>AA\u0001\u00004"+
		"5\u0001\u0000,-\u0001\u0000#&\u0002\u0000\"\"\')\u0002\u0000..11\u0002"+
		"\u00000033\u012f\u0000\u0019\u0001\u0000\u0000\u0000\u0002!\u0001\u0000"+
		"\u0000\u0000\u0004u\u0001\u0000\u0000\u0000\u0006\u00ad\u0001\u0000\u0000"+
		"\u0000\b\u00b3\u0001\u0000\u0000\u0000\n\u00bb\u0001\u0000\u0000\u0000"+
		"\f\u00c7\u0001\u0000\u0000\u0000\u000e\u00ca\u0001\u0000\u0000\u0000\u0010"+
		"\u00dd\u0001\u0000\u0000\u0000\u0012\u00e7\u0001\u0000\u0000\u0000\u0014"+
		"\u00f0\u0001\u0000\u0000\u0000\u0016\u00f7\u0001\u0000\u0000\u0000\u0018"+
		"\u001a\u0003\u0002\u0001\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u0019"+
		"\u001a\u0001\u0000\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b"+
		"\u001c\u0005\u0000\u0000\u0001\u001c\u0001\u0001\u0000\u0000\u0000\u001d"+
		"\u001f\u0003\u0004\u0002\u0000\u001e\u001d\u0001\u0000\u0000\u0000\u001e"+
		"\u001f\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000\u0000 \"\u0005"+
		"[\u0000\u0000!\u001e\u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000"+
		"#!\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$\u0003\u0001\u0000"+
		"\u0000\u0000%&\u0006\u0002\uffff\uffff\u0000&v\u0003\u0006\u0003\u0000"+
		"\'(\u0005\u001a\u0000\u0000(v\u0005\\\u0000\u0000)*\u0005\u001a\u0000"+
		"\u0000*v\u0005_\u0000\u0000+-\u0005\u0002\u0000\u0000,.\u0003\u0002\u0001"+
		"\u0000-,\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000./\u0001\u0000"+
		"\u0000\u0000/v\u0005\r\u0000\u000001\u0005\r\u0000\u000012\u00056\u0000"+
		"\u00002v\u00057\u0000\u000034\u0005\\\u0000\u00004v\u0003\u0010\b\u0000"+
		"5v\u0003\u0012\t\u00006:\u0005h\u0000\u000079\u0003\u0014\n\u000087\u0001"+
		"\u0000\u0000\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000"+
		":;\u0001\u0000\u0000\u0000;v\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000"+
		"\u0000=A\u0005e\u0000\u0000>@\u0003\u0014\n\u0000?>\u0001\u0000\u0000"+
		"\u0000@C\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000"+
		"\u0000\u0000BD\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000Dv\u0005"+
		"Y\u0000\u0000Ev\u0005_\u0000\u0000Fv\u0005`\u0000\u0000Gv\u0005a\u0000"+
		"\u0000Hv\u0005b\u0000\u0000Iv\u0005c\u0000\u0000Jv\u0005\u0016\u0000\u0000"+
		"Kv\u0005\u001d\u0000\u0000Lv\u0005\u000e\u0000\u0000MN\u0005\\\u0000\u0000"+
		"NP\u0005S\u0000\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000"+
		"PQ\u0001\u0000\u0000\u0000QR\u0005!\u0000\u0000RS\u0003\u0004\u0002\u0000"+
		"SU\u0005\b\u0000\u0000TV\u0003\u0002\u0001\u0000UT\u0001\u0000\u0000\u0000"+
		"UV\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0005\r\u0000\u0000"+
		"Xv\u0001\u0000\u0000\u0000YZ\u0005\\\u0000\u0000Z\\\u0005S\u0000\u0000"+
		"[Y\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000"+
		"\u0000]_\u0005\b\u0000\u0000^`\u0003\u0002\u0001\u0000_^\u0001\u0000\u0000"+
		"\u0000_`\u0001\u0000\u0000\u0000`b\u0001\u0000\u0000\u0000ac\u0005\u0014"+
		"\u0000\u0000ba\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0001"+
		"\u0000\u0000\u0000de\u0005!\u0000\u0000ev\u0003\u0004\u0002\u0012fg\u0003"+
		"\u0012\t\u0000gh\u0007\u0000\u0000\u0000hv\u0001\u0000\u0000\u0000ij\u0007"+
		"\u0000\u0000\u0000jv\u0003\u0012\t\u0000kl\u0007\u0001\u0000\u0000lv\u0003"+
		"\u0004\u0002\u000fmn\u0003\u0012\t\u0000no\u0007\u0002\u0000\u0000op\u0003"+
		"\u0004\u0002\u0002pv\u0001\u0000\u0000\u0000qr\u00056\u0000\u0000rs\u0003"+
		"\u0004\u0002\u0000st\u00057\u0000\u0000tv\u0001\u0000\u0000\u0000u%\u0001"+
		"\u0000\u0000\u0000u\'\u0001\u0000\u0000\u0000u)\u0001\u0000\u0000\u0000"+
		"u+\u0001\u0000\u0000\u0000u0\u0001\u0000\u0000\u0000u3\u0001\u0000\u0000"+
		"\u0000u5\u0001\u0000\u0000\u0000u6\u0001\u0000\u0000\u0000u=\u0001\u0000"+
		"\u0000\u0000uE\u0001\u0000\u0000\u0000uF\u0001\u0000\u0000\u0000uG\u0001"+
		"\u0000\u0000\u0000uH\u0001\u0000\u0000\u0000uI\u0001\u0000\u0000\u0000"+
		"uJ\u0001\u0000\u0000\u0000uK\u0001\u0000\u0000\u0000uL\u0001\u0000\u0000"+
		"\u0000uO\u0001\u0000\u0000\u0000u[\u0001\u0000\u0000\u0000uf\u0001\u0000"+
		"\u0000\u0000ui\u0001\u0000\u0000\u0000uk\u0001\u0000\u0000\u0000um\u0001"+
		"\u0000\u0000\u0000uq\u0001\u0000\u0000\u0000v\u00aa\u0001\u0000\u0000"+
		"\u0000wx\n\u000e\u0000\u0000xy\u0005B\u0000\u0000y\u00a9\u0003\u0004\u0002"+
		"\u000fz{\n\r\u0000\u0000{|\u0007\u0003\u0000\u0000|\u00a9\u0003\u0004"+
		"\u0002\u000e}~\n\f\u0000\u0000~\u007f\u0007\u0004\u0000\u0000\u007f\u00a9"+
		"\u0003\u0004\u0002\r\u0080\u0081\n\u000b\u0000\u0000\u0081\u0082\u0007"+
		"\u0005\u0000\u0000\u0082\u00a9\u0003\u0004\u0002\f\u0083\u0084\n\n\u0000"+
		"\u0000\u0084\u0085\u0005*\u0000\u0000\u0085\u00a9\u0003\u0004\u0002\u000b"+
		"\u0086\u0087\n\t\u0000\u0000\u0087\u0088\u0007\u0006\u0000\u0000\u0088"+
		"\u00a9\u0003\u0004\u0002\n\u0089\u008a\n\b\u0000\u0000\u008a\u008b\u0007"+
		"\u0007\u0000\u0000\u008b\u00a9\u0003\u0004\u0002\t\u008c\u008d\n\u0007"+
		"\u0000\u0000\u008d\u008e\u0007\b\u0000\u0000\u008e\u00a9\u0003\u0004\u0002"+
		"\b\u008f\u0090\n\u0006\u0000\u0000\u0090\u0091\u0007\t\u0000\u0000\u0091"+
		"\u00a9\u0003\u0004\u0002\u0007\u0092\u0093\n\u0005\u0000\u0000\u0093\u0094"+
		"\u0007\n\u0000\u0000\u0094\u00a9\u0003\u0004\u0002\u0006\u0095\u0096\n"+
		"\u0004\u0000\u0000\u0096\u0097\u0005Z\u0000\u0000\u0097\u0098\u0003\u0004"+
		"\u0002\u0000\u0098\u0099\u0005S\u0000\u0000\u0099\u009a\u0003\u0004\u0002"+
		"\u0004\u009a\u00a9\u0001\u0000\u0000\u0000\u009b\u009c\n\u0003\u0000\u0000"+
		"\u009c\u009d\u0005C\u0000\u0000\u009d\u00a9\u0003\u0004\u0002\u0003\u009e"+
		"\u009f\n!\u0000\u0000\u009f\u00a0\u0005V\u0000\u0000\u00a0\u00a1\u0005"+
		"\\\u0000\u0000\u00a1\u00a9\u0003\u0010\b\u0000\u00a2\u00a3\n \u0000\u0000"+
		"\u00a3\u00a4\u0005V\u0000\u0000\u00a4\u00a9\u0005\\\u0000\u0000\u00a5"+
		"\u00a6\n\u001f\u0000\u0000\u00a6\u00a7\u0005V\u0000\u0000\u00a7\u00a9"+
		"\u0005^\u0000\u0000\u00a8w\u0001\u0000\u0000\u0000\u00a8z\u0001\u0000"+
		"\u0000\u0000\u00a8}\u0001\u0000\u0000\u0000\u00a8\u0080\u0001\u0000\u0000"+
		"\u0000\u00a8\u0083\u0001\u0000\u0000\u0000\u00a8\u0086\u0001\u0000\u0000"+
		"\u0000\u00a8\u0089\u0001\u0000\u0000\u0000\u00a8\u008c\u0001\u0000\u0000"+
		"\u0000\u00a8\u008f\u0001\u0000\u0000\u0000\u00a8\u0092\u0001\u0000\u0000"+
		"\u0000\u00a8\u0095\u0001\u0000\u0000\u0000\u00a8\u009b\u0001\u0000\u0000"+
		"\u0000\u00a8\u009e\u0001\u0000\u0000\u0000\u00a8\u00a2\u0001\u0000\u0000"+
		"\u0000\u00a8\u00a5\u0001\u0000\u0000\u0000\u00a9\u00ac\u0001\u0000\u0000"+
		"\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000"+
		"\u0000\u00ab\u0005\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000"+
		"\u0000\u00ad\u00ae\u0005d\u0000\u0000\u00ae\u00b1\u0003\n\u0005\u0000"+
		"\u00af\u00b0\u0005q\u0000\u0000\u00b0\u00b2\u0003\b\u0004\u0000\u00b1"+
		"\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2"+
		"\u0007\u0001\u0000\u0000\u0000\u00b3\u00b8\u0003\n\u0005\u0000\u00b4\u00b5"+
		"\u0005r\u0000\u0000\u00b5\u00b7\u0003\n\u0005\u0000\u00b6\u00b4\u0001"+
		"\u0000\u0000\u0000\u00b7\u00ba\u0001\u0000\u0000\u0000\u00b8\u00b6\u0001"+
		"\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\t\u0001\u0000"+
		"\u0000\u0000\u00ba\u00b8\u0001\u0000\u0000\u0000\u00bb\u00bc\u0005p\u0000"+
		"\u0000\u00bc\u000b\u0001\u0000\u0000\u0000\u00bd\u00bf\u0005\u0012\u0000"+
		"\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00be\u00bf\u0001\u0000\u0000"+
		"\u0000\u00bf\u00c1\u0001\u0000\u0000\u0000\u00c0\u00c2\u0005\u0017\u0000"+
		"\u0000\u00c1\u00c0\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000"+
		"\u0000\u00c2\u00c3\u0001\u0000\u0000\u0000\u00c3\u00c8\u0003\u0004\u0002"+
		"\u0000\u00c4\u00c5\u0005\u0017\u0000\u0000\u00c5\u00c6\u0005\u0012\u0000"+
		"\u0000\u00c6\u00c8\u0003\u0004\u0002\u0000\u00c7\u00be\u0001\u0000\u0000"+
		"\u0000\u00c7\u00c4\u0001\u0000\u0000\u0000\u00c8\r\u0001\u0000\u0000\u0000"+
		"\u00c9\u00cb\u0003\f\u0006\u0000\u00ca\u00c9\u0001\u0000\u0000\u0000\u00ca"+
		"\u00cb\u0001\u0000\u0000\u0000\u00cb\u000f\u0001\u0000\u0000\u0000\u00cc"+
		"\u00cd\u00056\u0000\u0000\u00cd\u00de\u00057\u0000\u0000\u00ce\u00cf\u0005"+
		"6\u0000\u0000\u00cf\u00d0\u0003\f\u0006\u0000\u00d0\u00d1\u00057\u0000"+
		"\u0000\u00d1\u00de\u0001\u0000\u0000\u0000\u00d2\u00d6\u00056\u0000\u0000"+
		"\u00d3\u00d4\u0003\u000e\u0007\u0000\u00d4\u00d5\u0005T\u0000\u0000\u00d5"+
		"\u00d7\u0001\u0000\u0000\u0000\u00d6\u00d3\u0001\u0000\u0000\u0000\u00d7"+
		"\u00d8\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000\u00d8"+
		"\u00d9\u0001\u0000\u0000\u0000\u00d9\u00da\u0001\u0000\u0000\u0000\u00da"+
		"\u00db\u0003\u000e\u0007\u0000\u00db\u00dc\u00057\u0000\u0000\u00dc\u00de"+
		"\u0001\u0000\u0000\u0000\u00dd\u00cc\u0001\u0000\u0000\u0000\u00dd\u00ce"+
		"\u0001\u0000\u0000\u0000\u00dd\u00d2\u0001\u0000\u0000\u0000\u00de\u0011"+
		"\u0001\u0000\u0000\u0000\u00df\u00e8\u0005\\\u0000\u0000\u00e0\u00e8\u0005"+
		"]\u0000\u0000\u00e1\u00e8\u0005^\u0000\u0000\u00e2\u00e3\u0005\\\u0000"+
		"\u0000\u00e3\u00e4\u00058\u0000\u0000\u00e4\u00e5\u0003\u0004\u0002\u0000"+
		"\u00e5\u00e6\u00059\u0000\u0000\u00e6\u00e8\u0001\u0000\u0000\u0000\u00e7"+
		"\u00df\u0001\u0000\u0000\u0000\u00e7\u00e0\u0001\u0000\u0000\u0000\u00e7"+
		"\u00e1\u0001\u0000\u0000\u0000\u00e7\u00e2\u0001\u0000\u0000\u0000\u00e8"+
		"\u0013\u0001\u0000\u0000\u0000\u00e9\u00f1\u0005v\u0000\u0000\u00ea\u00f1"+
		"\u0005w\u0000\u0000\u00eb\u00ec\u0005y\u0000\u0000\u00ec\u00ed\u0003\u0004"+
		"\u0002\u0000\u00ed\u00ee\u0005f\u0000\u0000\u00ee\u00f1\u0001\u0000\u0000"+
		"\u0000\u00ef\u00f1\u0003\u0016\u000b\u0000\u00f0\u00e9\u0001\u0000\u0000"+
		"\u0000\u00f0\u00ea\u0001\u0000\u0000\u0000\u00f0\u00eb\u0001\u0000\u0000"+
		"\u0000\u00f0\u00ef\u0001\u0000\u0000\u0000\u00f1\u0015\u0001\u0000\u0000"+
		"\u0000\u00f2\u00f8\u0005x\u0000\u0000\u00f3\u00f5\u0005z\u0000\u0000\u00f4"+
		"\u00f6\u0005|\u0000\u0000\u00f5\u00f4\u0001\u0000\u0000\u0000\u00f5\u00f6"+
		"\u0001\u0000\u0000\u0000\u00f6\u00f8\u0001\u0000\u0000\u0000\u00f7\u00f2"+
		"\u0001\u0000\u0000\u0000\u00f7\u00f3\u0001\u0000\u0000\u0000\u00f8\u0017"+
		"\u0001\u0000\u0000\u0000\u001a\u0019\u001e#-:AOU[_bu\u00a8\u00aa\u00b1"+
		"\u00b8\u00be\u00c1\u00c7\u00ca\u00d8\u00dd\u00e7\u00f0\u00f5\u00f7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}