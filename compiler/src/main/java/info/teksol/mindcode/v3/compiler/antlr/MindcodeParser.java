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
		Assign=1, At=2, Comma=3, Dot=4, DoubleDot=5, TripleDot=6, DoubleQuote=7, 
		Semicolon=8, Identifier=9, MindustryIdentifier=10, String=11, Binary=12, 
		Hexadecimal=13, Decimal=14, Float=15, HashSet=16, FormattableLiteral=17, 
		RBrace=18, CommentedComment=19, EnhancedComment=20, Comment=21, EmptyComment=22, 
		LineComment=23, NewLine=24, WhiteSpace=25, DirectiveValue=26, DirectiveAssign=27, 
		DirectiveComma=28, DirectiveComment=29, DirectiveLineComment=30, DirectiveWhiteSpace=31, 
		Text=32, EscapeSequence=33, EmptyPlaceholder=34, Interpolation=35, VariablePlaceholder=36, 
		EndOfLine=37, Variable=38, FmtEndOfLine=39, InCmtEndOfLine=40;
	public static final int
		RULE_program = 0, RULE_expressionList = 1, RULE_expression = 2, RULE_directive = 3, 
		RULE_directiveDeclaration = 4, RULE_directiveValues = 5, RULE_formattableContents = 6, 
		RULE_formattablePlaceholder = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "expressionList", "expression", "directive", "directiveDeclaration", 
			"directiveValues", "formattableContents", "formattablePlaceholder"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'@'", null, "'.'", "'..'", "'...'", "'\"'", null, null, 
			null, null, null, null, null, null, "'#set'", null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "'${'", "'$'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Assign", "At", "Comma", "Dot", "DoubleDot", "TripleDot", "DoubleQuote", 
			"Semicolon", "Identifier", "MindustryIdentifier", "String", "Binary", 
			"Hexadecimal", "Decimal", "Float", "HashSet", "FormattableLiteral", "RBrace", 
			"CommentedComment", "EnhancedComment", "Comment", "EmptyComment", "LineComment", 
			"NewLine", "WhiteSpace", "DirectiveValue", "DirectiveAssign", "DirectiveComma", 
			"DirectiveComment", "DirectiveLineComment", "DirectiveWhiteSpace", "Text", 
			"EscapeSequence", "EmptyPlaceholder", "Interpolation", "VariablePlaceholder", 
			"EndOfLine", "Variable", "FmtEndOfLine", "InCmtEndOfLine"
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
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1310208L) != 0)) {
				{
				setState(16);
				expressionList();
				}
			}

			setState(19);
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
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> Semicolon() { return getTokens(MindcodeParser.Semicolon); }
		public TerminalNode Semicolon(int i) {
			return getToken(MindcodeParser.Semicolon, i);
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
			enterOuterAlt(_localctx, 1);
			{
			setState(24); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(21);
				expression();
				setState(22);
				match(Semicolon);
				}
				}
				setState(26); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 1310208L) != 0) );
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
	public static class ExpMindustryIdentifierContext extends ExpressionContext {
		public TerminalNode MindustryIdentifier() { return getToken(MindcodeParser.MindustryIdentifier, 0); }
		public ExpMindustryIdentifierContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpMindustryIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpMindustryIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpMindustryIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpDecimalLiteralContext extends ExpressionContext {
		public TerminalNode Decimal() { return getToken(MindcodeParser.Decimal, 0); }
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
	public static class ExpFormattableLiteralContext extends ExpressionContext {
		public TerminalNode FormattableLiteral() { return getToken(MindcodeParser.FormattableLiteral, 0); }
		public TerminalNode DoubleQuote() { return getToken(MindcodeParser.DoubleQuote, 0); }
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
		public TerminalNode EnhancedComment() { return getToken(MindcodeParser.EnhancedComment, 0); }
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
	public static class ExpStringLiteralContext extends ExpressionContext {
		public TerminalNode String() { return getToken(MindcodeParser.String, 0); }
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
	public static class ExpIdentifierContext extends ExpressionContext {
		public TerminalNode Identifier() { return getToken(MindcodeParser.Identifier, 0); }
		public ExpIdentifierContext(ExpressionContext ctx) { copyFrom(ctx); }
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
	public static class ExpFLoatLiteralContext extends ExpressionContext {
		public TerminalNode Float() { return getToken(MindcodeParser.Float, 0); }
		public ExpFLoatLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterExpFLoatLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitExpFLoatLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitExpFLoatLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpBinaryLiteralContext extends ExpressionContext {
		public TerminalNode Binary() { return getToken(MindcodeParser.Binary, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class ExpHexadecimalLiteralContext extends ExpressionContext {
		public TerminalNode Hexadecimal() { return getToken(MindcodeParser.Hexadecimal, 0); }
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

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expression);
		int _la;
		try {
			setState(51);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HashSet:
				_localctx = new ExpDirectiveContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(28);
				directive();
				}
				break;
			case Identifier:
				_localctx = new ExpIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(29);
				match(Identifier);
				}
				break;
			case MindustryIdentifier:
				_localctx = new ExpMindustryIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(30);
				match(MindustryIdentifier);
				}
				break;
			case EnhancedComment:
				_localctx = new ExpEnhancedCommentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(31);
				match(EnhancedComment);
				setState(35);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 133143986176L) != 0)) {
					{
					{
					setState(32);
					formattableContents();
					}
					}
					setState(37);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case FormattableLiteral:
				_localctx = new ExpFormattableLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(38);
				match(FormattableLiteral);
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 133143986176L) != 0)) {
					{
					{
					setState(39);
					formattableContents();
					}
					}
					setState(44);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(45);
				match(DoubleQuote);
				}
				break;
			case String:
				_localctx = new ExpStringLiteralContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(46);
				match(String);
				}
				break;
			case Binary:
				_localctx = new ExpBinaryLiteralContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(47);
				match(Binary);
				}
				break;
			case Hexadecimal:
				_localctx = new ExpHexadecimalLiteralContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(48);
				match(Hexadecimal);
				}
				break;
			case Decimal:
				_localctx = new ExpDecimalLiteralContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(49);
				match(Decimal);
				}
				break;
			case Float:
				_localctx = new ExpFLoatLiteralContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(50);
				match(Float);
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
	public static class DirectiveContext extends ParserRuleContext {
		public TerminalNode HashSet() { return getToken(MindcodeParser.HashSet, 0); }
		public DirectiveDeclarationContext directiveDeclaration() {
			return getRuleContext(DirectiveDeclarationContext.class,0);
		}
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_directive);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(HashSet);
			setState(54);
			directiveDeclaration();
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
	public static class DirectiveDeclarationContext extends ParserRuleContext {
		public Token option;
		public DirectiveValuesContext value;
		public TerminalNode DirectiveValue() { return getToken(MindcodeParser.DirectiveValue, 0); }
		public TerminalNode DirectiveAssign() { return getToken(MindcodeParser.DirectiveAssign, 0); }
		public DirectiveValuesContext directiveValues() {
			return getRuleContext(DirectiveValuesContext.class,0);
		}
		public DirectiveDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDirectiveDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDirectiveDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDirectiveDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveDeclarationContext directiveDeclaration() throws RecognitionException {
		DirectiveDeclarationContext _localctx = new DirectiveDeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_directiveDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			((DirectiveDeclarationContext)_localctx).option = match(DirectiveValue);
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DirectiveAssign) {
				{
				setState(57);
				match(DirectiveAssign);
				setState(58);
				((DirectiveDeclarationContext)_localctx).value = directiveValues();
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
	public static class DirectiveValuesContext extends ParserRuleContext {
		public List<TerminalNode> DirectiveValue() { return getTokens(MindcodeParser.DirectiveValue); }
		public TerminalNode DirectiveValue(int i) {
			return getToken(MindcodeParser.DirectiveValue, i);
		}
		public List<TerminalNode> DirectiveComma() { return getTokens(MindcodeParser.DirectiveComma); }
		public TerminalNode DirectiveComma(int i) {
			return getToken(MindcodeParser.DirectiveComma, i);
		}
		public DirectiveValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveValues; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDirectiveValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDirectiveValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDirectiveValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveValuesContext directiveValues() throws RecognitionException {
		DirectiveValuesContext _localctx = new DirectiveValuesContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_directiveValues);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(DirectiveValue);
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DirectiveComma) {
				{
				{
				setState(62);
				match(DirectiveComma);
				setState(63);
				match(DirectiveValue);
				}
				}
				setState(68);
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
		public TerminalNode Interpolation() { return getToken(MindcodeParser.Interpolation, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBrace() { return getToken(MindcodeParser.RBrace, 0); }
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
		public TerminalNode Text() { return getToken(MindcodeParser.Text, 0); }
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
		public TerminalNode EscapeSequence() { return getToken(MindcodeParser.EscapeSequence, 0); }
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
		enterRule(_localctx, 12, RULE_formattableContents);
		try {
			setState(76);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Text:
				_localctx = new FmtTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(69);
				match(Text);
				}
				break;
			case EscapeSequence:
				_localctx = new FmtEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				match(EscapeSequence);
				}
				break;
			case Interpolation:
				_localctx = new FmtInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(71);
				match(Interpolation);
				setState(72);
				expression();
				setState(73);
				match(RBrace);
				}
				break;
			case EmptyPlaceholder:
			case VariablePlaceholder:
				_localctx = new FmtPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(75);
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
		public TerminalNode VariablePlaceholder() { return getToken(MindcodeParser.VariablePlaceholder, 0); }
		public TerminalNode Variable() { return getToken(MindcodeParser.Variable, 0); }
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
		public TerminalNode EmptyPlaceholder() { return getToken(MindcodeParser.EmptyPlaceholder, 0); }
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
		enterRule(_localctx, 14, RULE_formattablePlaceholder);
		int _la;
		try {
			setState(83);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EmptyPlaceholder:
				_localctx = new FmtPlaceholderEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(78);
				match(EmptyPlaceholder);
				}
				break;
			case VariablePlaceholder:
				_localctx = new FmtPlaceholderVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(79);
				match(VariablePlaceholder);
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Variable) {
					{
					setState(80);
					((FmtPlaceholderVariableContext)_localctx).id = match(Variable);
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

	public static final String _serializedATN =
		"\u0004\u0001(V\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002"+
		"\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005"+
		"\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001\u0000"+
		"\u0003\u0000\u0012\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0004\u0001\u0019\b\u0001\u000b\u0001\f\u0001\u001a\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\"\b"+
		"\u0002\n\u0002\f\u0002%\t\u0002\u0001\u0002\u0001\u0002\u0005\u0002)\b"+
		"\u0002\n\u0002\f\u0002,\t\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0003\u00024\b\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004<\b"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005A\b\u0005\n\u0005"+
		"\f\u0005D\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006M\b\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0003\u0007R\b\u0007\u0003\u0007T\b\u0007\u0001\u0007"+
		"\u0000\u0000\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0000a\u0000"+
		"\u0011\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004"+
		"3\u0001\u0000\u0000\u0000\u00065\u0001\u0000\u0000\u0000\b8\u0001\u0000"+
		"\u0000\u0000\n=\u0001\u0000\u0000\u0000\fL\u0001\u0000\u0000\u0000\u000e"+
		"S\u0001\u0000\u0000\u0000\u0010\u0012\u0003\u0002\u0001\u0000\u0011\u0010"+
		"\u0001\u0000\u0000\u0000\u0011\u0012\u0001\u0000\u0000\u0000\u0012\u0013"+
		"\u0001\u0000\u0000\u0000\u0013\u0014\u0005\u0000\u0000\u0001\u0014\u0001"+
		"\u0001\u0000\u0000\u0000\u0015\u0016\u0003\u0004\u0002\u0000\u0016\u0017"+
		"\u0005\b\u0000\u0000\u0017\u0019\u0001\u0000\u0000\u0000\u0018\u0015\u0001"+
		"\u0000\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u0018\u0001"+
		"\u0000\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u0003\u0001"+
		"\u0000\u0000\u0000\u001c4\u0003\u0006\u0003\u0000\u001d4\u0005\t\u0000"+
		"\u0000\u001e4\u0005\n\u0000\u0000\u001f#\u0005\u0014\u0000\u0000 \"\u0003"+
		"\f\u0006\u0000! \u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000\u0000#!"+
		"\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$4\u0001\u0000\u0000"+
		"\u0000%#\u0001\u0000\u0000\u0000&*\u0005\u0011\u0000\u0000\')\u0003\f"+
		"\u0006\u0000(\'\u0001\u0000\u0000\u0000),\u0001\u0000\u0000\u0000*(\u0001"+
		"\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+-\u0001\u0000\u0000\u0000"+
		",*\u0001\u0000\u0000\u0000-4\u0005\u0007\u0000\u0000.4\u0005\u000b\u0000"+
		"\u0000/4\u0005\f\u0000\u000004\u0005\r\u0000\u000014\u0005\u000e\u0000"+
		"\u000024\u0005\u000f\u0000\u00003\u001c\u0001\u0000\u0000\u00003\u001d"+
		"\u0001\u0000\u0000\u00003\u001e\u0001\u0000\u0000\u00003\u001f\u0001\u0000"+
		"\u0000\u00003&\u0001\u0000\u0000\u00003.\u0001\u0000\u0000\u00003/\u0001"+
		"\u0000\u0000\u000030\u0001\u0000\u0000\u000031\u0001\u0000\u0000\u0000"+
		"32\u0001\u0000\u0000\u00004\u0005\u0001\u0000\u0000\u000056\u0005\u0010"+
		"\u0000\u000067\u0003\b\u0004\u00007\u0007\u0001\u0000\u0000\u00008;\u0005"+
		"\u001a\u0000\u00009:\u0005\u001b\u0000\u0000:<\u0003\n\u0005\u0000;9\u0001"+
		"\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<\t\u0001\u0000\u0000\u0000"+
		"=B\u0005\u001a\u0000\u0000>?\u0005\u001c\u0000\u0000?A\u0005\u001a\u0000"+
		"\u0000@>\u0001\u0000\u0000\u0000AD\u0001\u0000\u0000\u0000B@\u0001\u0000"+
		"\u0000\u0000BC\u0001\u0000\u0000\u0000C\u000b\u0001\u0000\u0000\u0000"+
		"DB\u0001\u0000\u0000\u0000EM\u0005 \u0000\u0000FM\u0005!\u0000\u0000G"+
		"H\u0005#\u0000\u0000HI\u0003\u0004\u0002\u0000IJ\u0005\u0012\u0000\u0000"+
		"JM\u0001\u0000\u0000\u0000KM\u0003\u000e\u0007\u0000LE\u0001\u0000\u0000"+
		"\u0000LF\u0001\u0000\u0000\u0000LG\u0001\u0000\u0000\u0000LK\u0001\u0000"+
		"\u0000\u0000M\r\u0001\u0000\u0000\u0000NT\u0005\"\u0000\u0000OQ\u0005"+
		"$\u0000\u0000PR\u0005&\u0000\u0000QP\u0001\u0000\u0000\u0000QR\u0001\u0000"+
		"\u0000\u0000RT\u0001\u0000\u0000\u0000SN\u0001\u0000\u0000\u0000SO\u0001"+
		"\u0000\u0000\u0000T\u000f\u0001\u0000\u0000\u0000\n\u0011\u001a#*3;BL"+
		"QS";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}