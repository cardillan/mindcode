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
		Identifier=1, StringLiteral=2, Assign=3, Comma=4, Dot=5, DoubleDot=6, 
		TripleDot=7, DoubleQuote=8, Semicolon=9, Binary=10, Hexadecimal=11, Decimal=12, 
		Float=13, HashSet=14, FormattableLiteral=15, RBrace=16, CommentedComment=17, 
		EnhancedComment=18, Comment=19, EmptyComment=20, LineComment=21, WhiteSpace=22, 
		DirectiveValue=23, DirectiveAssign=24, DirectiveComma=25, DirectiveComment=26, 
		DirectiveLineComment=27, DirectiveWhiteSpace=28, Text=29, EscapeSequence=30, 
		EmptyPlaceholder=31, Interpolation=32, VariablePlaceholder=33, EndOfLine=34, 
		Variable=35;
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
			null, null, null, null, null, "'.'", "'..'", "'...'", null, null, null, 
			null, null, null, "'#set'", "'$\"'", "'}'", null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "'${'", "'$'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Identifier", "StringLiteral", "Assign", "Comma", "Dot", "DoubleDot", 
			"TripleDot", "DoubleQuote", "Semicolon", "Binary", "Hexadecimal", "Decimal", 
			"Float", "HashSet", "FormattableLiteral", "RBrace", "CommentedComment", 
			"EnhancedComment", "Comment", "EmptyComment", "LineComment", "WhiteSpace", 
			"DirectiveValue", "DirectiveAssign", "DirectiveComma", "DirectiveComment", 
			"DirectiveLineComment", "DirectiveWhiteSpace", "Text", "EscapeSequence", 
			"EmptyPlaceholder", "Interpolation", "VariablePlaceholder", "EndOfLine", 
			"Variable"
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
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 311302L) != 0)) {
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
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 311302L) != 0) );
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
		public TerminalNode StringLiteral() { return getToken(MindcodeParser.StringLiteral, 0); }
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
			setState(46);
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
			case EnhancedComment:
				_localctx = new ExpEnhancedCommentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(30);
				match(EnhancedComment);
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16642998272L) != 0)) {
					{
					{
					setState(31);
					formattableContents();
					}
					}
					setState(36);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case FormattableLiteral:
				_localctx = new ExpFormattableLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(37);
				match(FormattableLiteral);
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16642998272L) != 0)) {
					{
					{
					setState(38);
					formattableContents();
					}
					}
					setState(43);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(44);
				match(DoubleQuote);
				}
				break;
			case StringLiteral:
				_localctx = new ExpStringLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(45);
				match(StringLiteral);
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
			setState(48);
			match(HashSet);
			setState(49);
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
			setState(51);
			((DirectiveDeclarationContext)_localctx).option = match(DirectiveValue);
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DirectiveAssign) {
				{
				setState(52);
				match(DirectiveAssign);
				setState(53);
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
			setState(56);
			match(DirectiveValue);
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DirectiveComma) {
				{
				{
				setState(57);
				match(DirectiveComma);
				setState(58);
				match(DirectiveValue);
				}
				}
				setState(63);
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
			setState(71);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Text:
				_localctx = new FmtTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(64);
				match(Text);
				}
				break;
			case EscapeSequence:
				_localctx = new FmtEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(65);
				match(EscapeSequence);
				}
				break;
			case Interpolation:
				_localctx = new FmtInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(66);
				match(Interpolation);
				setState(67);
				expression();
				setState(68);
				match(RBrace);
				}
				break;
			case EmptyPlaceholder:
			case VariablePlaceholder:
				_localctx = new FmtPlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(70);
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
		public Token id;
		public TerminalNode EmptyPlaceholder() { return getToken(MindcodeParser.EmptyPlaceholder, 0); }
		public TerminalNode VariablePlaceholder() { return getToken(MindcodeParser.VariablePlaceholder, 0); }
		public TerminalNode Variable() { return getToken(MindcodeParser.Variable, 0); }
		public FormattablePlaceholderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formattablePlaceholder; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFormattablePlaceholder(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFormattablePlaceholder(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFormattablePlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormattablePlaceholderContext formattablePlaceholder() throws RecognitionException {
		FormattablePlaceholderContext _localctx = new FormattablePlaceholderContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_formattablePlaceholder);
		int _la;
		try {
			setState(78);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EmptyPlaceholder:
				enterOuterAlt(_localctx, 1);
				{
				setState(73);
				match(EmptyPlaceholder);
				}
				break;
			case VariablePlaceholder:
				enterOuterAlt(_localctx, 2);
				{
				setState(74);
				match(VariablePlaceholder);
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Variable) {
					{
					setState(75);
					((FormattablePlaceholderContext)_localctx).id = match(Variable);
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
		"\u0004\u0001#Q\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002"+
		"\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005"+
		"\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001\u0000"+
		"\u0003\u0000\u0012\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0004\u0001\u0019\b\u0001\u000b\u0001\f\u0001\u001a\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002!\b\u0002\n\u0002"+
		"\f\u0002$\t\u0002\u0001\u0002\u0001\u0002\u0005\u0002(\b\u0002\n\u0002"+
		"\f\u0002+\t\u0002\u0001\u0002\u0001\u0002\u0003\u0002/\b\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"7\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005<\b\u0005\n\u0005"+
		"\f\u0005?\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006H\b\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0003\u0007M\b\u0007\u0003\u0007O\b\u0007\u0001\u0007"+
		"\u0000\u0000\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0000W\u0000"+
		"\u0011\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004"+
		".\u0001\u0000\u0000\u0000\u00060\u0001\u0000\u0000\u0000\b3\u0001\u0000"+
		"\u0000\u0000\n8\u0001\u0000\u0000\u0000\fG\u0001\u0000\u0000\u0000\u000e"+
		"N\u0001\u0000\u0000\u0000\u0010\u0012\u0003\u0002\u0001\u0000\u0011\u0010"+
		"\u0001\u0000\u0000\u0000\u0011\u0012\u0001\u0000\u0000\u0000\u0012\u0013"+
		"\u0001\u0000\u0000\u0000\u0013\u0014\u0005\u0000\u0000\u0001\u0014\u0001"+
		"\u0001\u0000\u0000\u0000\u0015\u0016\u0003\u0004\u0002\u0000\u0016\u0017"+
		"\u0005\t\u0000\u0000\u0017\u0019\u0001\u0000\u0000\u0000\u0018\u0015\u0001"+
		"\u0000\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u0018\u0001"+
		"\u0000\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u0003\u0001"+
		"\u0000\u0000\u0000\u001c/\u0003\u0006\u0003\u0000\u001d/\u0005\u0001\u0000"+
		"\u0000\u001e\"\u0005\u0012\u0000\u0000\u001f!\u0003\f\u0006\u0000 \u001f"+
		"\u0001\u0000\u0000\u0000!$\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000"+
		"\u0000\"#\u0001\u0000\u0000\u0000#/\u0001\u0000\u0000\u0000$\"\u0001\u0000"+
		"\u0000\u0000%)\u0005\u000f\u0000\u0000&(\u0003\f\u0006\u0000\'&\u0001"+
		"\u0000\u0000\u0000(+\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000"+
		")*\u0001\u0000\u0000\u0000*,\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000"+
		"\u0000,/\u0005\b\u0000\u0000-/\u0005\u0002\u0000\u0000.\u001c\u0001\u0000"+
		"\u0000\u0000.\u001d\u0001\u0000\u0000\u0000.\u001e\u0001\u0000\u0000\u0000"+
		".%\u0001\u0000\u0000\u0000.-\u0001\u0000\u0000\u0000/\u0005\u0001\u0000"+
		"\u0000\u000001\u0005\u000e\u0000\u000012\u0003\b\u0004\u00002\u0007\u0001"+
		"\u0000\u0000\u000036\u0005\u0017\u0000\u000045\u0005\u0018\u0000\u0000"+
		"57\u0003\n\u0005\u000064\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u0000"+
		"7\t\u0001\u0000\u0000\u00008=\u0005\u0017\u0000\u00009:\u0005\u0019\u0000"+
		"\u0000:<\u0005\u0017\u0000\u0000;9\u0001\u0000\u0000\u0000<?\u0001\u0000"+
		"\u0000\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>\u000b"+
		"\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000@H\u0005\u001d\u0000"+
		"\u0000AH\u0005\u001e\u0000\u0000BC\u0005 \u0000\u0000CD\u0003\u0004\u0002"+
		"\u0000DE\u0005\u0010\u0000\u0000EH\u0001\u0000\u0000\u0000FH\u0003\u000e"+
		"\u0007\u0000G@\u0001\u0000\u0000\u0000GA\u0001\u0000\u0000\u0000GB\u0001"+
		"\u0000\u0000\u0000GF\u0001\u0000\u0000\u0000H\r\u0001\u0000\u0000\u0000"+
		"IO\u0005\u001f\u0000\u0000JL\u0005!\u0000\u0000KM\u0005#\u0000\u0000L"+
		"K\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MO\u0001\u0000\u0000"+
		"\u0000NI\u0001\u0000\u0000\u0000NJ\u0001\u0000\u0000\u0000O\u000f\u0001"+
		"\u0000\u0000\u0000\n\u0011\u001a\").6=GLN";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}