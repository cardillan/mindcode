// Generated from MindcodeParser.g4 by ANTLR 4.13.1
package info.teksol.mc.mindcode.compiler.antlr;

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
		ALLOCATE=1, BEGIN=2, BREAK=3, CACHED=4, CASE=5, CONST=6, CONTINUE=7, DEF=8, 
		DO=9, ELIF=10, ELSE=11, ELSEIF=12, ELSIF=13, END=14, EXTERNAL=15, FALSE=16, 
		FOR=17, HEAP=18, IF=19, IN=20, INLINE=21, LINKED=22, LOOP=23, NOINIT=24, 
		NOINLINE=25, NULL=26, OUT=27, PARAM=28, RETURN=29, REQUIRE=30, STACK=31, 
		THEN=32, TRUE=33, VAR=34, VOID=35, VOLATILE=36, WHEN=37, WHILE=38, EQUAL=39, 
		GREATER_THAN=40, GREATER_THAN_EQUAL=41, LESS_THAN=42, LESS_THAN_EQUAL=43, 
		NOT_EQUAL=44, STRICT_EQUAL=45, STRICT_NOT_EQUAL=46, BITWISE_AND=47, BITWISE_NOT=48, 
		BITWISE_OR=49, BITWISE_XOR=50, BOOLEAN_AND=51, BOOLEAN_NOT=52, BOOLEAN_OR=53, 
		LOGICAL_AND=54, LOGICAL_NOT=55, LOGICAL_OR=56, SHIFT_LEFT=57, SHIFT_RIGHT=58, 
		LPAREN=59, RPAREN=60, LBRACKET=61, RBRACKET=62, DECREMENT=63, DIV=64, 
		IDIV=65, INCREMENT=66, MINUS=67, MOD=68, MUL=69, PLUS=70, POW=71, ASSIGN=72, 
		ASSIGN_BITWISE_AND=73, ASSIGN_BITWISE_OR=74, ASSIGN_BITWISE_XOR=75, ASSIGN_BOOLEAN_AND=76, 
		ASSIGN_BOOLEAN_OR=77, ASSIGN_DIV=78, ASSIGN_IDIV=79, ASSIGN_MINUS=80, 
		ASSIGN_MOD=81, ASSIGN_MUL=82, ASSIGN_PLUS=83, ASSIGN_POW=84, ASSIGN_SHIFT_LEFT=85, 
		ASSIGN_SHIFT_RIGHT=86, AT=87, COLON=88, COMMA=89, DOLLAR=90, DOT=91, DOT2=92, 
		DOT3=93, DOUBLEQUOTE=94, QUESTION=95, SEMICOLON=96, IDENTIFIER=97, EXTIDENTIFIER=98, 
		BUILTINIDENTIFIER=99, STRING=100, COLOR=101, BINARY=102, HEXADECIMAL=103, 
		DECIMAL=104, FLOAT=105, HASHSET=106, FORMATTABLELITERAL=107, RBRACE=108, 
		COMMENTEDCOMMENT=109, ENHANCEDCOMMENT=110, DOC_COMMENT=111, COMMENT=112, 
		EMPTYCOMMENT=113, LINECOMMENT=114, NEWLINE=115, WHITESPACE=116, UNKNOWN_CHAR=117, 
		DIRECTIVEVALUE=118, DIRECTIVEASSIGN=119, DIRECTIVECOMMA=120, DIRECTIVECOMMENT=121, 
		DIRECTIVELINECOMMENT=122, DIRECTIVEWHITESPACE=123, TEXT=124, ESCAPESEQUENCE=125, 
		EMPTYPLACEHOLDER=126, INTERPOLATION=127, VARIABLEPLACEHOLDER=128, ENDOFLINE=129, 
		VARIABLE=130, FMTENDOFLINE=131;
	public static final int
		RULE_astModule = 0, RULE_astStatementList = 1, RULE_expressionList = 2, 
		RULE_statement = 3, RULE_declarationOrExpressionList = 4, RULE_variableDeclaration = 5, 
		RULE_declModifier = 6, RULE_typeSpec = 7, RULE_variableSpecList = 8, RULE_variableSpecification = 9, 
		RULE_valueList = 10, RULE_lvalue = 11, RULE_expression = 12, RULE_formattableContents = 13, 
		RULE_formattablePlaceholder = 14, RULE_directive = 15, RULE_directiveValues = 16, 
		RULE_astDirectiveValue = 17, RULE_allocations = 18, RULE_astAllocation = 19, 
		RULE_parameterList = 20, RULE_astFunctionParameter = 21, RULE_astFunctionArgument = 22, 
		RULE_astOptionalFunctionArgument = 23, RULE_argumentList = 24, RULE_caseAlternatives = 25, 
		RULE_astCaseAlternative = 26, RULE_whenValueList = 27, RULE_whenValue = 28, 
		RULE_astRange = 29, RULE_elsifBranches = 30, RULE_elsifBranch = 31, RULE_loopIteratorList = 32, 
		RULE_astLoopIterator = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"astModule", "astStatementList", "expressionList", "statement", "declarationOrExpressionList", 
			"variableDeclaration", "declModifier", "typeSpec", "variableSpecList", 
			"variableSpecification", "valueList", "lvalue", "expression", "formattableContents", 
			"formattablePlaceholder", "directive", "directiveValues", "astDirectiveValue", 
			"allocations", "astAllocation", "parameterList", "astFunctionParameter", 
			"astFunctionArgument", "astOptionalFunctionArgument", "argumentList", 
			"caseAlternatives", "astCaseAlternative", "whenValueList", "whenValue", 
			"astRange", "elsifBranches", "elsifBranch", "loopIteratorList", "astLoopIterator"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'begin'", "'break'", "'cached'", "'case'", "'const'", 
			"'continue'", "'def'", "'do'", "'elif'", "'else'", "'elseif'", "'elsif'", 
			"'end'", "'external'", "'false'", "'for'", "'heap'", "'if'", "'in'", 
			"'inline'", "'linked'", "'loop'", "'noinit'", "'noinline'", "'null'", 
			"'out'", "'param'", "'return'", "'require'", "'stack'", "'then'", "'true'", 
			"'var'", "'void'", "'volatile'", "'when'", "'while'", "'=='", "'>'", 
			"'>='", "'<'", "'<='", "'!='", "'==='", "'!=='", "'&'", "'~'", "'|'", 
			"'^'", "'&&'", "'!'", "'||'", "'and'", "'not'", "'or'", "'<<'", "'>>'", 
			"'('", "')'", "'['", "']'", "'--'", "'/'", "'\\'", "'++'", "'-'", "'%'", 
			"'*'", "'+'", "'**'", null, "'&='", "'|='", "'^='", "'&&='", "'||='", 
			"'/='", "'\\='", "'-='", "'%='", "'*='", "'+='", "'**='", "'<<='", "'>>='", 
			"'@'", "':'", null, null, "'.'", "'..'", "'...'", "'\"'", "'?'", null, 
			null, null, null, null, null, null, null, null, null, "'#set'", null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, "'${'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BEGIN", "BREAK", "CACHED", "CASE", "CONST", "CONTINUE", 
			"DEF", "DO", "ELIF", "ELSE", "ELSEIF", "ELSIF", "END", "EXTERNAL", "FALSE", 
			"FOR", "HEAP", "IF", "IN", "INLINE", "LINKED", "LOOP", "NOINIT", "NOINLINE", 
			"NULL", "OUT", "PARAM", "RETURN", "REQUIRE", "STACK", "THEN", "TRUE", 
			"VAR", "VOID", "VOLATILE", "WHEN", "WHILE", "EQUAL", "GREATER_THAN", 
			"GREATER_THAN_EQUAL", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "STRICT_EQUAL", 
			"STRICT_NOT_EQUAL", "BITWISE_AND", "BITWISE_NOT", "BITWISE_OR", "BITWISE_XOR", 
			"BOOLEAN_AND", "BOOLEAN_NOT", "BOOLEAN_OR", "LOGICAL_AND", "LOGICAL_NOT", 
			"LOGICAL_OR", "SHIFT_LEFT", "SHIFT_RIGHT", "LPAREN", "RPAREN", "LBRACKET", 
			"RBRACKET", "DECREMENT", "DIV", "IDIV", "INCREMENT", "MINUS", "MOD", 
			"MUL", "PLUS", "POW", "ASSIGN", "ASSIGN_BITWISE_AND", "ASSIGN_BITWISE_OR", 
			"ASSIGN_BITWISE_XOR", "ASSIGN_BOOLEAN_AND", "ASSIGN_BOOLEAN_OR", "ASSIGN_DIV", 
			"ASSIGN_IDIV", "ASSIGN_MINUS", "ASSIGN_MOD", "ASSIGN_MUL", "ASSIGN_PLUS", 
			"ASSIGN_POW", "ASSIGN_SHIFT_LEFT", "ASSIGN_SHIFT_RIGHT", "AT", "COLON", 
			"COMMA", "DOLLAR", "DOT", "DOT2", "DOT3", "DOUBLEQUOTE", "QUESTION", 
			"SEMICOLON", "IDENTIFIER", "EXTIDENTIFIER", "BUILTINIDENTIFIER", "STRING", 
			"COLOR", "BINARY", "HEXADECIMAL", "DECIMAL", "FLOAT", "HASHSET", "FORMATTABLELITERAL", 
			"RBRACE", "COMMENTEDCOMMENT", "ENHANCEDCOMMENT", "DOC_COMMENT", "COMMENT", 
			"EMPTYCOMMENT", "LINECOMMENT", "NEWLINE", "WHITESPACE", "UNKNOWN_CHAR", 
			"DIRECTIVEVALUE", "DIRECTIVEASSIGN", "DIRECTIVECOMMA", "DIRECTIVECOMMENT", 
			"DIRECTIVELINECOMMENT", "DIRECTIVEWHITESPACE", "TEXT", "ESCAPESEQUENCE", 
			"EMPTYPLACEHOLDER", "INTERPOLATION", "VARIABLEPLACEHOLDER", "ENDOFLINE", 
			"VARIABLE", "FMTENDOFLINE"
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
	public static class AstModuleContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MindcodeParser.EOF, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astModule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstModule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstModule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstModuleContext astModule() throws RecognitionException {
		AstModuleContext _localctx = new AstModuleContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_astModule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097007197830146L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 21989158813715L) != 0)) {
				{
				setState(68);
				astStatementList();
				}
			}

			setState(71);
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
	public static class AstStatementListContext extends ParserRuleContext {
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public AstStatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astStatementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstStatementList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstStatementList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstStatementListContext astStatementList() throws RecognitionException {
		AstStatementListContext _localctx = new AstStatementListContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_astStatementList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(77); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(74);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097007197830146L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 21988085071891L) != 0)) {
						{
						setState(73);
						statement();
						}
					}

					setState(76);
					match(SEMICOLON);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(79); 
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
	public static class ExpressionListContext extends ParserRuleContext {
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
		enterRule(_localctx, 4, RULE_expressionList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(81);
					expression(0);
					setState(82);
					match(COMMA);
					}
					} 
				}
				setState(88);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(89);
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

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstVariableDeclarationContext extends StatementContext {
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public AstVariableDeclarationContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstEnhancedCommentContext extends StatementContext {
		public TerminalNode ENHANCEDCOMMENT() { return getToken(MindcodeParser.ENHANCEDCOMMENT, 0); }
		public List<FormattableContentsContext> formattableContents() {
			return getRuleContexts(FormattableContentsContext.class);
		}
		public FormattableContentsContext formattableContents(int i) {
			return getRuleContext(FormattableContentsContext.class,i);
		}
		public AstEnhancedCommentContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstEnhancedComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstEnhancedComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstEnhancedComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstDoWhileLoopStatementContext extends StatementContext {
		public Token label;
		public AstStatementListContext body;
		public Token loop;
		public ExpressionContext condition;
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public TerminalNode LOOP() { return getToken(MindcodeParser.LOOP, 0); }
		public AstDoWhileLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDoWhileLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDoWhileLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDoWhileLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstBreakStatementContext extends StatementContext {
		public Token label;
		public TerminalNode BREAK() { return getToken(MindcodeParser.BREAK, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstBreakStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstBreakStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstCodeBlockContext extends StatementContext {
		public AstStatementListContext exp;
		public TerminalNode BEGIN() { return getToken(MindcodeParser.BEGIN, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstCodeBlockContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstCodeBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstCodeBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstCodeBlock(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstParameterContext extends StatementContext {
		public Token name;
		public ExpressionContext value;
		public TerminalNode PARAM() { return getToken(MindcodeParser.PARAM, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstParameterContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstParameter(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstAllocationsContext extends StatementContext {
		public TerminalNode ALLOCATE() { return getToken(MindcodeParser.ALLOCATE, 0); }
		public AllocationsContext allocations() {
			return getRuleContext(AllocationsContext.class,0);
		}
		public AstAllocationsContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstAllocations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstAllocations(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstAllocations(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstForEachLoopStatementContext extends StatementContext {
		public Token label;
		public TypeSpecContext type;
		public LoopIteratorListContext iterators;
		public ExpressionListContext values;
		public AstStatementListContext body;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public LoopIteratorListContext loopIteratorList() {
			return getRuleContext(LoopIteratorListContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TypeSpecContext typeSpec() {
			return getRuleContext(TypeSpecContext.class,0);
		}
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstForEachLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstForEachLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstForEachLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstForEachLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstRangedForLoopStatementContext extends StatementContext {
		public Token label;
		public TypeSpecContext type;
		public LvalueContext control;
		public AstRangeContext range;
		public AstStatementListContext body;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public AstRangeContext astRange() {
			return getRuleContext(AstRangeContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TypeSpecContext typeSpec() {
			return getRuleContext(TypeSpecContext.class,0);
		}
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstRangedForLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstRangedForLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstRangedForLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstRangedForLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstWhileLoopStatementContext extends StatementContext {
		public Token label;
		public ExpressionContext condition;
		public AstStatementListContext body;
		public TerminalNode WHILE() { return getToken(MindcodeParser.WHILE, 0); }
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstWhileLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstWhileLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstWhileLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstWhileLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstDirectiveContext extends StatementContext {
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public AstDirectiveContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDirective(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstReturnStatementContext extends StatementContext {
		public ExpressionContext value;
		public TerminalNode RETURN() { return getToken(MindcodeParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstReturnStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstFunctionDeclarationContext extends StatementContext {
		public Token inline;
		public Token type;
		public Token name;
		public ParameterListContext params;
		public AstStatementListContext body;
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode VOID() { return getToken(MindcodeParser.VOID, 0); }
		public TerminalNode DEF() { return getToken(MindcodeParser.DEF, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public TerminalNode INLINE() { return getToken(MindcodeParser.INLINE, 0); }
		public TerminalNode NOINLINE() { return getToken(MindcodeParser.NOINLINE, 0); }
		public AstFunctionDeclarationContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstContinueStatementContext extends StatementContext {
		public Token label;
		public TerminalNode CONTINUE() { return getToken(MindcodeParser.CONTINUE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstContinueStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstContinueStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstContinueStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstConstantContext extends StatementContext {
		public Token name;
		public ExpressionContext value;
		public TerminalNode CONST() { return getToken(MindcodeParser.CONST, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstConstantContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstConstant(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstIteratedForLoopStatementContext extends StatementContext {
		public Token label;
		public DeclarationOrExpressionListContext init;
		public ExpressionContext condition;
		public ExpressionListContext update;
		public AstStatementListContext body;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public DeclarationOrExpressionListContext declarationOrExpressionList() {
			return getRuleContext(DeclarationOrExpressionListContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstIteratedForLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstIteratedForLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstIteratedForLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstIteratedForLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstRequireLibraryContext extends StatementContext {
		public Token library;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstRequireLibraryContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstRequireLibrary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstRequireLibrary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstRequireLibrary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstRequireFileContext extends StatementContext {
		public Token file;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public TerminalNode STRING() { return getToken(MindcodeParser.STRING, 0); }
		public AstRequireFileContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstRequireFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstRequireFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstRequireFile(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstExpressionContext extends StatementContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstExpressionContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_statement);
		int _la;
		try {
			setState(223);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				_localctx = new AstExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(91);
				expression(0);
				}
				break;
			case 2:
				_localctx = new AstDirectiveContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(92);
				directive();
				}
				break;
			case 3:
				_localctx = new AstVariableDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(93);
				variableDeclaration();
				}
				break;
			case 4:
				_localctx = new AstEnhancedCommentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(94);
				match(ENHANCEDCOMMENT);
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 124)) & ~0x3f) == 0 && ((1L << (_la - 124)) & 31L) != 0)) {
					{
					{
					setState(95);
					formattableContents();
					}
					}
					setState(100);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 5:
				_localctx = new AstAllocationsContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(101);
				match(ALLOCATE);
				setState(102);
				allocations();
				}
				break;
			case 6:
				_localctx = new AstConstantContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(103);
				match(CONST);
				setState(104);
				((AstConstantContext)_localctx).name = match(IDENTIFIER);
				setState(105);
				match(ASSIGN);
				setState(106);
				((AstConstantContext)_localctx).value = expression(0);
				}
				break;
			case 7:
				_localctx = new AstParameterContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(107);
				match(PARAM);
				setState(108);
				((AstParameterContext)_localctx).name = match(IDENTIFIER);
				setState(109);
				match(ASSIGN);
				setState(110);
				((AstParameterContext)_localctx).value = expression(0);
				}
				break;
			case 8:
				_localctx = new AstRequireFileContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(111);
				match(REQUIRE);
				setState(112);
				((AstRequireFileContext)_localctx).file = match(STRING);
				}
				break;
			case 9:
				_localctx = new AstRequireLibraryContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(113);
				match(REQUIRE);
				setState(114);
				((AstRequireLibraryContext)_localctx).library = match(IDENTIFIER);
				}
				break;
			case 10:
				_localctx = new AstFunctionDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INLINE || _la==NOINLINE) {
					{
					setState(115);
					((AstFunctionDeclarationContext)_localctx).inline = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==INLINE || _la==NOINLINE) ) {
						((AstFunctionDeclarationContext)_localctx).inline = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(118);
				((AstFunctionDeclarationContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DEF || _la==VOID) ) {
					((AstFunctionDeclarationContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(119);
				((AstFunctionDeclarationContext)_localctx).name = match(IDENTIFIER);
				setState(120);
				((AstFunctionDeclarationContext)_localctx).params = parameterList();
				setState(122);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(121);
					((AstFunctionDeclarationContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(124);
				match(END);
				}
				break;
			case 11:
				_localctx = new AstForEachLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(126);
					((AstForEachLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(127);
					match(COLON);
					}
				}

				setState(130);
				match(FOR);
				setState(132);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR) {
					{
					setState(131);
					((AstForEachLoopStatementContext)_localctx).type = typeSpec();
					}
				}

				setState(134);
				((AstForEachLoopStatementContext)_localctx).iterators = loopIteratorList();
				setState(135);
				match(IN);
				setState(136);
				((AstForEachLoopStatementContext)_localctx).values = expressionList();
				setState(137);
				match(DO);
				setState(139);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(138);
					((AstForEachLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(141);
				match(END);
				}
				break;
			case 12:
				_localctx = new AstIteratedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(143);
					((AstIteratedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(144);
					match(COLON);
					}
				}

				setState(147);
				match(FOR);
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097318350307280L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 3296387399699L) != 0)) {
					{
					setState(148);
					((AstIteratedForLoopStatementContext)_localctx).init = declarationOrExpressionList();
					}
				}

				setState(151);
				match(SEMICOLON);
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097404270657504L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 3296387399699L) != 0)) {
					{
					setState(152);
					((AstIteratedForLoopStatementContext)_localctx).condition = expression(0);
					}
				}

				setState(155);
				match(SEMICOLON);
				setState(157);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097404270657504L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 3296387399699L) != 0)) {
					{
					setState(156);
					((AstIteratedForLoopStatementContext)_localctx).update = expressionList();
					}
				}

				setState(159);
				match(DO);
				setState(161);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(160);
					((AstIteratedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(163);
				match(END);
				}
				break;
			case 13:
				_localctx = new AstRangedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(166);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(164);
					((AstRangedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(165);
					match(COLON);
					}
				}

				setState(168);
				match(FOR);
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR) {
					{
					setState(169);
					((AstRangedForLoopStatementContext)_localctx).type = typeSpec();
					}
				}

				setState(172);
				((AstRangedForLoopStatementContext)_localctx).control = lvalue();
				setState(173);
				match(IN);
				setState(174);
				((AstRangedForLoopStatementContext)_localctx).range = astRange();
				setState(175);
				match(DO);
				setState(177);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(176);
					((AstRangedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(179);
				match(END);
				}
				break;
			case 14:
				_localctx = new AstWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(181);
					((AstWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(182);
					match(COLON);
					}
				}

				setState(185);
				match(WHILE);
				setState(186);
				((AstWhileLoopStatementContext)_localctx).condition = expression(0);
				setState(187);
				match(DO);
				setState(189);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
				case 1:
					{
					setState(188);
					((AstWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(191);
				match(END);
				}
				break;
			case 15:
				_localctx = new AstDoWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(193);
					((AstDoWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(194);
					match(COLON);
					}
				}

				setState(197);
				match(DO);
				setState(199);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(198);
					((AstDoWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOOP) {
					{
					setState(201);
					((AstDoWhileLoopStatementContext)_localctx).loop = match(LOOP);
					}
				}

				setState(204);
				match(WHILE);
				setState(205);
				((AstDoWhileLoopStatementContext)_localctx).condition = expression(0);
				}
				break;
			case 16:
				_localctx = new AstBreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(206);
				match(BREAK);
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(207);
					((AstBreakStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 17:
				_localctx = new AstContinueStatementContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(210);
				match(CONTINUE);
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(211);
					((AstContinueStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 18:
				_localctx = new AstReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(214);
				match(RETURN);
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097404270657504L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 3296387399699L) != 0)) {
					{
					setState(215);
					((AstReturnStatementContext)_localctx).value = expression(0);
					}
				}

				}
				break;
			case 19:
				_localctx = new AstCodeBlockContext(_localctx);
				enterOuterAlt(_localctx, 19);
				{
				setState(218);
				match(BEGIN);
				setState(220);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(219);
					((AstCodeBlockContext)_localctx).exp = astStatementList();
					}
					break;
				}
				setState(222);
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
	public static class DeclarationOrExpressionListContext extends ParserRuleContext {
		public VariableDeclarationContext decl;
		public ExpressionListContext expList;
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public DeclarationOrExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declarationOrExpressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDeclarationOrExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDeclarationOrExpressionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDeclarationOrExpressionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationOrExpressionListContext declarationOrExpressionList() throws RecognitionException {
		DeclarationOrExpressionListContext _localctx = new DeclarationOrExpressionListContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_declarationOrExpressionList);
		try {
			setState(227);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CACHED:
			case EXTERNAL:
			case LINKED:
			case NOINIT:
			case VAR:
			case VOLATILE:
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				((DeclarationOrExpressionListContext)_localctx).decl = variableDeclaration();
				}
				break;
			case CASE:
			case END:
			case FALSE:
			case IF:
			case NULL:
			case TRUE:
			case BITWISE_NOT:
			case BOOLEAN_NOT:
			case LOGICAL_NOT:
			case LPAREN:
			case DECREMENT:
			case INCREMENT:
			case MINUS:
			case PLUS:
			case IDENTIFIER:
			case EXTIDENTIFIER:
			case BUILTINIDENTIFIER:
			case STRING:
			case COLOR:
			case BINARY:
			case HEXADECIMAL:
			case DECIMAL:
			case FLOAT:
			case FORMATTABLELITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(226);
				((DeclarationOrExpressionListContext)_localctx).expList = expressionList();
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
	public static class VariableDeclarationContext extends ParserRuleContext {
		public DeclModifierContext modifiers;
		public TypeSpecContext type;
		public VariableSpecListContext variables;
		public TypeSpecContext typeSpec() {
			return getRuleContext(TypeSpecContext.class,0);
		}
		public VariableSpecListContext variableSpecList() {
			return getRuleContext(VariableSpecListContext.class,0);
		}
		public List<DeclModifierContext> declModifier() {
			return getRuleContexts(DeclModifierContext.class);
		}
		public DeclModifierContext declModifier(int i) {
			return getRuleContext(DeclModifierContext.class,i);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variableDeclaration);
		int _la;
		try {
			setState(245);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68740481040L) != 0)) {
					{
					{
					setState(229);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(234);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(235);
				((VariableDeclarationContext)_localctx).type = typeSpec();
				setState(236);
				((VariableDeclarationContext)_localctx).variables = variableSpecList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(239); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(238);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(241); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 68740481040L) != 0) );
				setState(243);
				((VariableDeclarationContext)_localctx).variables = variableSpecList();
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
	public static class DeclModifierContext extends ParserRuleContext {
		public Token modifier;
		public TerminalNode EXTERNAL() { return getToken(MindcodeParser.EXTERNAL, 0); }
		public TerminalNode LINKED() { return getToken(MindcodeParser.LINKED, 0); }
		public TerminalNode CACHED() { return getToken(MindcodeParser.CACHED, 0); }
		public TerminalNode VOLATILE() { return getToken(MindcodeParser.VOLATILE, 0); }
		public TerminalNode NOINIT() { return getToken(MindcodeParser.NOINIT, 0); }
		public DeclModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterDeclModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitDeclModifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitDeclModifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclModifierContext declModifier() throws RecognitionException {
		DeclModifierContext _localctx = new DeclModifierContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_declModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			((DeclModifierContext)_localctx).modifier = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 68740481040L) != 0)) ) {
				((DeclModifierContext)_localctx).modifier = (Token)_errHandler.recoverInline(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class TypeSpecContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(MindcodeParser.VAR, 0); }
		public TypeSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterTypeSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitTypeSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitTypeSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeSpecContext typeSpec() throws RecognitionException {
		TypeSpecContext _localctx = new TypeSpecContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_typeSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			match(VAR);
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
	public static class VariableSpecListContext extends ParserRuleContext {
		public List<VariableSpecificationContext> variableSpecification() {
			return getRuleContexts(VariableSpecificationContext.class);
		}
		public VariableSpecificationContext variableSpecification(int i) {
			return getRuleContext(VariableSpecificationContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public VariableSpecListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableSpecList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterVariableSpecList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitVariableSpecList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitVariableSpecList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableSpecListContext variableSpecList() throws RecognitionException {
		VariableSpecListContext _localctx = new VariableSpecListContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_variableSpecList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(251);
					variableSpecification();
					setState(252);
					match(COMMA);
					}
					} 
				}
				setState(258);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			setState(259);
			variableSpecification();
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
	public static class VariableSpecificationContext extends ParserRuleContext {
		public Token id;
		public ExpressionContext exp;
		public ExpressionContext length;
		public ValueListContext values;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode EXTIDENTIFIER() { return getToken(MindcodeParser.EXTIDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public ValueListContext valueList() {
			return getRuleContext(ValueListContext.class,0);
		}
		public VariableSpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableSpecification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterVariableSpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitVariableSpecification(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitVariableSpecification(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableSpecificationContext variableSpecification() throws RecognitionException {
		VariableSpecificationContext _localctx = new VariableSpecificationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_variableSpecification);
		int _la;
		try {
			setState(276);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(261);
				((VariableSpecificationContext)_localctx).id = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==IDENTIFIER || _la==EXTIDENTIFIER) ) {
					((VariableSpecificationContext)_localctx).id = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(262);
					match(ASSIGN);
					setState(263);
					((VariableSpecificationContext)_localctx).exp = expression(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(266);
				((VariableSpecificationContext)_localctx).id = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==IDENTIFIER || _la==EXTIDENTIFIER) ) {
					((VariableSpecificationContext)_localctx).id = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(267);
				match(LBRACKET);
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097404270657504L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 3296387399699L) != 0)) {
					{
					setState(268);
					((VariableSpecificationContext)_localctx).length = expression(0);
					}
				}

				setState(271);
				match(RBRACKET);
				setState(274);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(272);
					match(ASSIGN);
					setState(273);
					((VariableSpecificationContext)_localctx).values = valueList();
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
	public static class ValueListContext extends ParserRuleContext {
		public ExpressionListContext values;
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ValueListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_valueList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterValueList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitValueList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitValueList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueListContext valueList() throws RecognitionException {
		ValueListContext _localctx = new ValueListContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_valueList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(LPAREN);
			setState(279);
			((ValueListContext)_localctx).values = expressionList();
			setState(280);
			match(RPAREN);
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
	public static class AstIdentifierContext extends LvalueContext {
		public Token id;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstIdentifierContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstBuiltInIdentifierContext extends LvalueContext {
		public Token builtin;
		public TerminalNode BUILTINIDENTIFIER() { return getToken(MindcodeParser.BUILTINIDENTIFIER, 0); }
		public AstBuiltInIdentifierContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstBuiltInIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstBuiltInIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstBuiltInIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstIdentifierExtContext extends LvalueContext {
		public Token id;
		public TerminalNode EXTIDENTIFIER() { return getToken(MindcodeParser.EXTIDENTIFIER, 0); }
		public AstIdentifierExtContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstIdentifierExt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstIdentifierExt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstIdentifierExt(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstArrayAccessContext extends LvalueContext {
		public Token array;
		public ExpressionContext index;
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstArrayAccessContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstArrayAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstArrayAccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstArrayAccess(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_lvalue);
		try {
			setState(290);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				_localctx = new AstIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(282);
				((AstIdentifierContext)_localctx).id = match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new AstIdentifierExtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(283);
				((AstIdentifierExtContext)_localctx).id = match(EXTIDENTIFIER);
				}
				break;
			case 3:
				_localctx = new AstBuiltInIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(284);
				((AstBuiltInIdentifierContext)_localctx).builtin = match(BUILTINIDENTIFIER);
				}
				break;
			case 4:
				_localctx = new AstArrayAccessContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(285);
				((AstArrayAccessContext)_localctx).array = match(IDENTIFIER);
				setState(286);
				match(LBRACKET);
				setState(287);
				((AstArrayAccessContext)_localctx).index = expression(0);
				setState(288);
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
	public static class AstOperatorBinaryAddContext extends ExpressionContext {
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
		public AstOperatorBinaryAddContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryAdd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryAdd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMethodCallContext extends ExpressionContext {
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
		public AstMethodCallContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMethodCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMethodCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryExpContext extends ExpressionContext {
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
		public AstOperatorBinaryExpContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryExp(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryEqualityContext extends ExpressionContext {
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
		public AstOperatorBinaryEqualityContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryEquality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryEquality(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryEquality(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorTernaryContext extends ExpressionContext {
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
		public AstOperatorTernaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorTernary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorTernary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorTernary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorIncDecPostfixContext extends ExpressionContext {
		public LvalueContext exp;
		public Token postfix;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode INCREMENT() { return getToken(MindcodeParser.INCREMENT, 0); }
		public TerminalNode DECREMENT() { return getToken(MindcodeParser.DECREMENT, 0); }
		public AstOperatorIncDecPostfixContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorIncDecPostfix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorIncDecPostfix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorIncDecPostfix(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstIfExpressionContext extends ExpressionContext {
		public ExpressionContext condition;
		public AstStatementListContext trueBranch;
		public ElsifBranchesContext elsif;
		public AstStatementListContext falseBranch;
		public TerminalNode IF() { return getToken(MindcodeParser.IF, 0); }
		public TerminalNode THEN() { return getToken(MindcodeParser.THEN, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(MindcodeParser.ELSE, 0); }
		public List<AstStatementListContext> astStatementList() {
			return getRuleContexts(AstStatementListContext.class);
		}
		public AstStatementListContext astStatementList(int i) {
			return getRuleContext(AstStatementListContext.class,i);
		}
		public ElsifBranchesContext elsifBranches() {
			return getRuleContext(ElsifBranchesContext.class,0);
		}
		public AstIfExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstIfExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstIfExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstIfExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralBooleanContext extends ExpressionContext {
		public Token value;
		public TerminalNode TRUE() { return getToken(MindcodeParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(MindcodeParser.FALSE, 0); }
		public AstLiteralBooleanContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralNullContext extends ExpressionContext {
		public TerminalNode NULL() { return getToken(MindcodeParser.NULL, 0); }
		public AstLiteralNullContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralNull(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralNull(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryInequalityContext extends ExpressionContext {
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
		public AstOperatorBinaryInequalityContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryInequality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryInequality(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryInequality(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorUnaryContext extends ExpressionContext {
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
		public AstOperatorUnaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorUnary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralBinaryContext extends ExpressionContext {
		public TerminalNode BINARY() { return getToken(MindcodeParser.BINARY, 0); }
		public AstLiteralBinaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralBinary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralBinary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralBinary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstFormattableLiteralContext extends ExpressionContext {
		public TerminalNode FORMATTABLELITERAL() { return getToken(MindcodeParser.FORMATTABLELITERAL, 0); }
		public TerminalNode DOUBLEQUOTE() { return getToken(MindcodeParser.DOUBLEQUOTE, 0); }
		public List<FormattableContentsContext> formattableContents() {
			return getRuleContexts(FormattableContentsContext.class);
		}
		public FormattableContentsContext formattableContents(int i) {
			return getRuleContext(FormattableContentsContext.class,i);
		}
		public AstFormattableLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstFormattableLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstFormattableLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstFormattableLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryBitwiseAndContext extends ExpressionContext {
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
		public AstOperatorBinaryBitwiseAndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryBitwiseAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryBitwiseAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryBitwiseAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryBitwiseOrContext extends ExpressionContext {
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
		public AstOperatorBinaryBitwiseOrContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryBitwiseOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryBitwiseOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryBitwiseOr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstCaseExpressionContext extends ExpressionContext {
		public ExpressionContext exp;
		public CaseAlternativesContext alternatives;
		public AstStatementListContext elseBranch;
		public TerminalNode CASE() { return getToken(MindcodeParser.CASE, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(MindcodeParser.ELSE, 0); }
		public CaseAlternativesContext caseAlternatives() {
			return getRuleContext(CaseAlternativesContext.class,0);
		}
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstCaseExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstCaseExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstCaseExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstCaseExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstPropertyAccessContext extends ExpressionContext {
		public ExpressionContext object;
		public Token property;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BUILTINIDENTIFIER() { return getToken(MindcodeParser.BUILTINIDENTIFIER, 0); }
		public AstPropertyAccessContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstPropertyAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstPropertyAccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstPropertyAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralFloatContext extends ExpressionContext {
		public TerminalNode FLOAT() { return getToken(MindcodeParser.FLOAT, 0); }
		public AstLiteralFloatContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralFloat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralFloat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralFloat(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstFunctionCallEndContext extends ExpressionContext {
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public AstFunctionCallEndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstFunctionCallEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstFunctionCallEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstFunctionCallEnd(this);
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
	public static class AstLiteralDecimalContext extends ExpressionContext {
		public TerminalNode DECIMAL() { return getToken(MindcodeParser.DECIMAL, 0); }
		public AstLiteralDecimalContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralDecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralDecimal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralDecimal(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryMulContext extends ExpressionContext {
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
		public AstOperatorBinaryMulContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryMul(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryMul(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryMul(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryShiftContext extends ExpressionContext {
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
		public AstOperatorBinaryShiftContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryShift(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryShift(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryShift(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralHexadecimalContext extends ExpressionContext {
		public TerminalNode HEXADECIMAL() { return getToken(MindcodeParser.HEXADECIMAL, 0); }
		public AstLiteralHexadecimalContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralHexadecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralHexadecimal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralHexadecimal(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralStringContext extends ExpressionContext {
		public TerminalNode STRING() { return getToken(MindcodeParser.STRING, 0); }
		public AstLiteralStringContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralString(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstFunctionCallContext extends ExpressionContext {
		public Token function;
		public ArgumentListContext args;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public AstFunctionCallContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMemberAccessContext extends ExpressionContext {
		public ExpressionContext object;
		public Token member;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstMemberAccessContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMemberAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMemberAccess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMemberAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstParenthesesContext extends ExpressionContext {
		public ExpressionContext exp;
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstParenthesesContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstParentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstParentheses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralColorContext extends ExpressionContext {
		public TerminalNode COLOR() { return getToken(MindcodeParser.COLOR, 0); }
		public AstLiteralColorContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralColor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryLogicalOrContext extends ExpressionContext {
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
		public AstOperatorBinaryLogicalOrContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryLogicalOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryLogicalOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryLogicalOr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorIncDecPrefixContext extends ExpressionContext {
		public Token prefix;
		public LvalueContext exp;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode INCREMENT() { return getToken(MindcodeParser.INCREMENT, 0); }
		public TerminalNode DECREMENT() { return getToken(MindcodeParser.DECREMENT, 0); }
		public AstOperatorIncDecPrefixContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorIncDecPrefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorIncDecPrefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorIncDecPrefix(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstOperatorBinaryLogicalAndContext extends ExpressionContext {
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
		public AstOperatorBinaryLogicalAndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryLogicalAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryLogicalAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryLogicalAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstAssignmentContext extends ExpressionContext {
		public ExpressionContext target;
		public Token operation;
		public ExpressionContext value;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode ASSIGN() { return getToken(MindcodeParser.ASSIGN, 0); }
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
		public AstAssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstAssignment(this);
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
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				_localctx = new ExpLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(293);
				lvalue();
				}
				break;
			case 2:
				{
				_localctx = new AstFunctionCallEndContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(294);
				match(END);
				setState(295);
				match(LPAREN);
				setState(296);
				match(RPAREN);
				}
				break;
			case 3:
				{
				_localctx = new AstFunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(297);
				((AstFunctionCallContext)_localctx).function = match(IDENTIFIER);
				setState(298);
				((AstFunctionCallContext)_localctx).args = argumentList();
				}
				break;
			case 4:
				{
				_localctx = new AstCaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(299);
				match(CASE);
				setState(300);
				((AstCaseExpressionContext)_localctx).exp = expression(0);
				setState(302);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHEN) {
					{
					setState(301);
					((AstCaseExpressionContext)_localctx).alternatives = caseAlternatives();
					}
				}

				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(304);
					match(ELSE);
					setState(305);
					((AstCaseExpressionContext)_localctx).elseBranch = astStatementList();
					}
				}

				setState(308);
				match(END);
				}
				break;
			case 5:
				{
				_localctx = new AstIfExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(310);
				match(IF);
				setState(311);
				((AstIfExpressionContext)_localctx).condition = expression(0);
				setState(312);
				match(THEN);
				setState(314);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
				case 1:
					{
					setState(313);
					((AstIfExpressionContext)_localctx).trueBranch = astStatementList();
					}
					break;
				}
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSIF) {
					{
					setState(316);
					((AstIfExpressionContext)_localctx).elsif = elsifBranches();
					}
				}

				setState(323);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(319);
					match(ELSE);
					setState(321);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
					case 1:
						{
						setState(320);
						((AstIfExpressionContext)_localctx).falseBranch = astStatementList();
						}
						break;
					}
					}
				}

				setState(325);
				match(END);
				}
				break;
			case 6:
				{
				_localctx = new AstFormattableLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(327);
				match(FORMATTABLELITERAL);
				setState(331);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 124)) & ~0x3f) == 0 && ((1L << (_la - 124)) & 31L) != 0)) {
					{
					{
					setState(328);
					formattableContents();
					}
					}
					setState(333);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(334);
				match(DOUBLEQUOTE);
				}
				break;
			case 7:
				{
				_localctx = new AstLiteralStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(335);
				match(STRING);
				}
				break;
			case 8:
				{
				_localctx = new AstLiteralColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(336);
				match(COLOR);
				}
				break;
			case 9:
				{
				_localctx = new AstLiteralBinaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(337);
				match(BINARY);
				}
				break;
			case 10:
				{
				_localctx = new AstLiteralHexadecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(338);
				match(HEXADECIMAL);
				}
				break;
			case 11:
				{
				_localctx = new AstLiteralDecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(339);
				match(DECIMAL);
				}
				break;
			case 12:
				{
				_localctx = new AstLiteralFloatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(340);
				match(FLOAT);
				}
				break;
			case 13:
				{
				_localctx = new AstLiteralNullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(341);
				match(NULL);
				}
				break;
			case 14:
				{
				_localctx = new AstLiteralBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(342);
				((AstLiteralBooleanContext)_localctx).value = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==FALSE || _la==TRUE) ) {
					((AstLiteralBooleanContext)_localctx).value = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 15:
				{
				_localctx = new AstOperatorIncDecPostfixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(343);
				((AstOperatorIncDecPostfixContext)_localctx).exp = lvalue();
				setState(344);
				((AstOperatorIncDecPostfixContext)_localctx).postfix = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DECREMENT || _la==INCREMENT) ) {
					((AstOperatorIncDecPostfixContext)_localctx).postfix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 16:
				{
				_localctx = new AstOperatorIncDecPrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(346);
				((AstOperatorIncDecPrefixContext)_localctx).prefix = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DECREMENT || _la==INCREMENT) ) {
					((AstOperatorIncDecPrefixContext)_localctx).prefix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(347);
				((AstOperatorIncDecPrefixContext)_localctx).exp = lvalue();
				}
				break;
			case 17:
				{
				_localctx = new AstOperatorUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(348);
				((AstOperatorUnaryContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 48)) & ~0x3f) == 0 && ((1L << (_la - 48)) & 4718737L) != 0)) ) {
					((AstOperatorUnaryContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(349);
				((AstOperatorUnaryContext)_localctx).exp = expression(14);
				}
				break;
			case 18:
				{
				_localctx = new AstParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(350);
				match(LPAREN);
				setState(351);
				((AstParenthesesContext)_localctx).exp = expression(0);
				setState(352);
				match(RPAREN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(407);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(405);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
					case 1:
						{
						_localctx = new AstOperatorBinaryExpContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryExpContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(356);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(357);
						((AstOperatorBinaryExpContext)_localctx).op = match(POW);
						setState(358);
						((AstOperatorBinaryExpContext)_localctx).right = expression(14);
						}
						break;
					case 2:
						{
						_localctx = new AstOperatorBinaryMulContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryMulContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(359);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(360);
						((AstOperatorBinaryMulContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 51L) != 0)) ) {
							((AstOperatorBinaryMulContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(361);
						((AstOperatorBinaryMulContext)_localctx).right = expression(13);
						}
						break;
					case 3:
						{
						_localctx = new AstOperatorBinaryAddContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryAddContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(362);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(363);
						((AstOperatorBinaryAddContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==MINUS || _la==PLUS) ) {
							((AstOperatorBinaryAddContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(364);
						((AstOperatorBinaryAddContext)_localctx).right = expression(12);
						}
						break;
					case 4:
						{
						_localctx = new AstOperatorBinaryShiftContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryShiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(365);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(366);
						((AstOperatorBinaryShiftContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==SHIFT_LEFT || _la==SHIFT_RIGHT) ) {
							((AstOperatorBinaryShiftContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(367);
						((AstOperatorBinaryShiftContext)_localctx).right = expression(11);
						}
						break;
					case 5:
						{
						_localctx = new AstOperatorBinaryBitwiseAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(368);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(369);
						((AstOperatorBinaryBitwiseAndContext)_localctx).op = match(BITWISE_AND);
						setState(370);
						((AstOperatorBinaryBitwiseAndContext)_localctx).right = expression(10);
						}
						break;
					case 6:
						{
						_localctx = new AstOperatorBinaryBitwiseOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(371);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(372);
						((AstOperatorBinaryBitwiseOrContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BITWISE_OR || _la==BITWISE_XOR) ) {
							((AstOperatorBinaryBitwiseOrContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(373);
						((AstOperatorBinaryBitwiseOrContext)_localctx).right = expression(9);
						}
						break;
					case 7:
						{
						_localctx = new AstOperatorBinaryInequalityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryInequalityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(374);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(375);
						((AstOperatorBinaryInequalityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16492674416640L) != 0)) ) {
							((AstOperatorBinaryInequalityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(376);
						((AstOperatorBinaryInequalityContext)_localctx).right = expression(8);
						}
						break;
					case 8:
						{
						_localctx = new AstOperatorBinaryEqualityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryEqualityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(377);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(378);
						((AstOperatorBinaryEqualityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 123695058124800L) != 0)) ) {
							((AstOperatorBinaryEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(379);
						((AstOperatorBinaryEqualityContext)_localctx).right = expression(7);
						}
						break;
					case 9:
						{
						_localctx = new AstOperatorBinaryLogicalAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryLogicalAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(380);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(381);
						((AstOperatorBinaryLogicalAndContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BOOLEAN_AND || _la==LOGICAL_AND) ) {
							((AstOperatorBinaryLogicalAndContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(382);
						((AstOperatorBinaryLogicalAndContext)_localctx).right = expression(6);
						}
						break;
					case 10:
						{
						_localctx = new AstOperatorBinaryLogicalOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryLogicalOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(383);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(384);
						((AstOperatorBinaryLogicalOrContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BOOLEAN_OR || _la==LOGICAL_OR) ) {
							((AstOperatorBinaryLogicalOrContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(385);
						((AstOperatorBinaryLogicalOrContext)_localctx).right = expression(5);
						}
						break;
					case 11:
						{
						_localctx = new AstOperatorTernaryContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorTernaryContext)_localctx).condition = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(386);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(387);
						match(QUESTION);
						setState(388);
						((AstOperatorTernaryContext)_localctx).trueBranch = expression(0);
						setState(389);
						match(COLON);
						setState(390);
						((AstOperatorTernaryContext)_localctx).falseBranch = expression(3);
						}
						break;
					case 12:
						{
						_localctx = new AstAssignmentContext(new ExpressionContext(_parentctx, _parentState));
						((AstAssignmentContext)_localctx).target = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(392);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(393);
						((AstAssignmentContext)_localctx).operation = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 72)) & ~0x3f) == 0 && ((1L << (_la - 72)) & 32767L) != 0)) ) {
							((AstAssignmentContext)_localctx).operation = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(394);
						((AstAssignmentContext)_localctx).value = expression(2);
						}
						break;
					case 13:
						{
						_localctx = new AstMethodCallContext(new ExpressionContext(_parentctx, _parentState));
						((AstMethodCallContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(395);
						if (!(precpred(_ctx, 30))) throw new FailedPredicateException(this, "precpred(_ctx, 30)");
						setState(396);
						match(DOT);
						setState(397);
						((AstMethodCallContext)_localctx).function = match(IDENTIFIER);
						setState(398);
						((AstMethodCallContext)_localctx).args = argumentList();
						}
						break;
					case 14:
						{
						_localctx = new AstMemberAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstMemberAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(399);
						if (!(precpred(_ctx, 29))) throw new FailedPredicateException(this, "precpred(_ctx, 29)");
						setState(400);
						match(DOT);
						setState(401);
						((AstMemberAccessContext)_localctx).member = match(IDENTIFIER);
						}
						break;
					case 15:
						{
						_localctx = new AstPropertyAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstPropertyAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(402);
						if (!(precpred(_ctx, 28))) throw new FailedPredicateException(this, "precpred(_ctx, 28)");
						setState(403);
						match(DOT);
						setState(404);
						((AstPropertyAccessContext)_localctx).property = match(BUILTINIDENTIFIER);
						}
						break;
					}
					} 
				}
				setState(409);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
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
	public static class FormattableInterpolationContext extends FormattableContentsContext {
		public TerminalNode INTERPOLATION() { return getToken(MindcodeParser.INTERPOLATION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(MindcodeParser.RBRACE, 0); }
		public FormattableInterpolationContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFormattableInterpolation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFormattableInterpolation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFormattableInterpolation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FormattableEscapedContext extends FormattableContentsContext {
		public TerminalNode ESCAPESEQUENCE() { return getToken(MindcodeParser.ESCAPESEQUENCE, 0); }
		public FormattableEscapedContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFormattableEscaped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFormattableEscaped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFormattableEscaped(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlaceholderContext extends FormattableContentsContext {
		public FormattablePlaceholderContext formattablePlaceholder() {
			return getRuleContext(FormattablePlaceholderContext.class,0);
		}
		public PlaceholderContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterPlaceholder(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitPlaceholder(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitPlaceholder(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FormattableTextContext extends FormattableContentsContext {
		public TerminalNode TEXT() { return getToken(MindcodeParser.TEXT, 0); }
		public FormattableTextContext(FormattableContentsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFormattableText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFormattableText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFormattableText(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormattableContentsContext formattableContents() throws RecognitionException {
		FormattableContentsContext _localctx = new FormattableContentsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_formattableContents);
		try {
			setState(417);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT:
				_localctx = new FormattableTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(410);
				match(TEXT);
				}
				break;
			case ESCAPESEQUENCE:
				_localctx = new FormattableEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(411);
				match(ESCAPESEQUENCE);
				}
				break;
			case INTERPOLATION:
				_localctx = new FormattableInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(412);
				match(INTERPOLATION);
				setState(413);
				expression(0);
				setState(414);
				match(RBRACE);
				}
				break;
			case EMPTYPLACEHOLDER:
			case VARIABLEPLACEHOLDER:
				_localctx = new PlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(416);
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
	public static class FormattablePlaceholderVariableContext extends FormattablePlaceholderContext {
		public Token id;
		public TerminalNode VARIABLEPLACEHOLDER() { return getToken(MindcodeParser.VARIABLEPLACEHOLDER, 0); }
		public TerminalNode VARIABLE() { return getToken(MindcodeParser.VARIABLE, 0); }
		public FormattablePlaceholderVariableContext(FormattablePlaceholderContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFormattablePlaceholderVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFormattablePlaceholderVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFormattablePlaceholderVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FormattablePlaceholderEmptyContext extends FormattablePlaceholderContext {
		public TerminalNode EMPTYPLACEHOLDER() { return getToken(MindcodeParser.EMPTYPLACEHOLDER, 0); }
		public FormattablePlaceholderEmptyContext(FormattablePlaceholderContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterFormattablePlaceholderEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitFormattablePlaceholderEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitFormattablePlaceholderEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormattablePlaceholderContext formattablePlaceholder() throws RecognitionException {
		FormattablePlaceholderContext _localctx = new FormattablePlaceholderContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_formattablePlaceholder);
		int _la;
		try {
			setState(424);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EMPTYPLACEHOLDER:
				_localctx = new FormattablePlaceholderEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(419);
				match(EMPTYPLACEHOLDER);
				}
				break;
			case VARIABLEPLACEHOLDER:
				_localctx = new FormattablePlaceholderVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(420);
				match(VARIABLEPLACEHOLDER);
				setState(422);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VARIABLE) {
					{
					setState(421);
					((FormattablePlaceholderVariableContext)_localctx).id = match(VARIABLE);
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
	public static class AstDirectiveSetContext extends DirectiveContext {
		public AstDirectiveValueContext option;
		public DirectiveValuesContext value;
		public TerminalNode HASHSET() { return getToken(MindcodeParser.HASHSET, 0); }
		public AstDirectiveValueContext astDirectiveValue() {
			return getRuleContext(AstDirectiveValueContext.class,0);
		}
		public TerminalNode DIRECTIVEASSIGN() { return getToken(MindcodeParser.DIRECTIVEASSIGN, 0); }
		public DirectiveValuesContext directiveValues() {
			return getRuleContext(DirectiveValuesContext.class,0);
		}
		public AstDirectiveSetContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDirectiveSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDirectiveSet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDirectiveSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_directive);
		int _la;
		try {
			_localctx = new AstDirectiveSetContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(426);
			match(HASHSET);
			setState(427);
			((AstDirectiveSetContext)_localctx).option = astDirectiveValue();
			setState(430);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DIRECTIVEASSIGN) {
				{
				setState(428);
				match(DIRECTIVEASSIGN);
				setState(429);
				((AstDirectiveSetContext)_localctx).value = directiveValues();
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
		public List<AstDirectiveValueContext> astDirectiveValue() {
			return getRuleContexts(AstDirectiveValueContext.class);
		}
		public AstDirectiveValueContext astDirectiveValue(int i) {
			return getRuleContext(AstDirectiveValueContext.class,i);
		}
		public List<TerminalNode> DIRECTIVECOMMA() { return getTokens(MindcodeParser.DIRECTIVECOMMA); }
		public TerminalNode DIRECTIVECOMMA(int i) {
			return getToken(MindcodeParser.DIRECTIVECOMMA, i);
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
		enterRule(_localctx, 32, RULE_directiveValues);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(432);
					astDirectiveValue();
					setState(433);
					match(DIRECTIVECOMMA);
					}
					} 
				}
				setState(439);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			}
			setState(440);
			astDirectiveValue();
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
	public static class AstDirectiveValueContext extends ParserRuleContext {
		public TerminalNode DIRECTIVEVALUE() { return getToken(MindcodeParser.DIRECTIVEVALUE, 0); }
		public AstDirectiveValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astDirectiveValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDirectiveValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDirectiveValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDirectiveValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstDirectiveValueContext astDirectiveValue() throws RecognitionException {
		AstDirectiveValueContext _localctx = new AstDirectiveValueContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_astDirectiveValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(442);
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
	public static class AllocationsContext extends ParserRuleContext {
		public List<AstAllocationContext> astAllocation() {
			return getRuleContexts(AstAllocationContext.class);
		}
		public AstAllocationContext astAllocation(int i) {
			return getRuleContext(AstAllocationContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public AllocationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_allocations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAllocations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAllocations(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAllocations(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocationsContext allocations() throws RecognitionException {
		AllocationsContext _localctx = new AllocationsContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_allocations);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(449);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(444);
					astAllocation();
					setState(445);
					match(COMMA);
					}
					} 
				}
				setState(451);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,53,_ctx);
			}
			setState(452);
			astAllocation();
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
	public static class AstAllocationContext extends ParserRuleContext {
		public Token type;
		public Token id;
		public AstRangeContext range;
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode HEAP() { return getToken(MindcodeParser.HEAP, 0); }
		public TerminalNode STACK() { return getToken(MindcodeParser.STACK, 0); }
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public AstRangeContext astRange() {
			return getRuleContext(AstRangeContext.class,0);
		}
		public AstAllocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astAllocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstAllocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstAllocation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstAllocation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstAllocationContext astAllocation() throws RecognitionException {
		AstAllocationContext _localctx = new AstAllocationContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_astAllocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			((AstAllocationContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==HEAP || _la==STACK) ) {
				((AstAllocationContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(455);
			match(IN);
			setState(456);
			((AstAllocationContext)_localctx).id = match(IDENTIFIER);
			setState(461);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACKET) {
				{
				setState(457);
				match(LBRACKET);
				setState(458);
				((AstAllocationContext)_localctx).range = astRange();
				setState(459);
				match(RBRACKET);
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
	public static class ParameterListContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public List<AstFunctionParameterContext> astFunctionParameter() {
			return getRuleContexts(AstFunctionParameterContext.class);
		}
		public AstFunctionParameterContext astFunctionParameter(int i) {
			return getRuleContext(AstFunctionParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_parameterList);
		try {
			int _alt;
			setState(477);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(463);
				match(LPAREN);
				setState(464);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(465);
				match(LPAREN);
				setState(471);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(466);
						astFunctionParameter();
						setState(467);
						match(COMMA);
						}
						} 
					}
					setState(473);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
				}
				setState(474);
				astFunctionParameter();
				setState(475);
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
	public static class AstFunctionParameterContext extends ParserRuleContext {
		public Token modifier_in;
		public Token modifier_out;
		public Token name;
		public Token varargs;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public TerminalNode DOT3() { return getToken(MindcodeParser.DOT3, 0); }
		public AstFunctionParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astFunctionParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstFunctionParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstFunctionParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstFunctionParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstFunctionParameterContext astFunctionParameter() throws RecognitionException {
		AstFunctionParameterContext _localctx = new AstFunctionParameterContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_astFunctionParameter);
		int _la;
		try {
			setState(495);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(480);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(479);
					((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(483);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(482);
					((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(485);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(487);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(486);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(489);
				((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
				setState(490);
				((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
				setState(491);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(493);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(492);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
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
	public static class AstFunctionArgumentContext extends ParserRuleContext {
		public Token modifier_in;
		public Token modifier_out;
		public ExpressionContext arg;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public AstFunctionArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astFunctionArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstFunctionArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstFunctionArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstFunctionArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstFunctionArgumentContext astFunctionArgument() throws RecognitionException {
		AstFunctionArgumentContext _localctx = new AstFunctionArgumentContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_astFunctionArgument);
		int _la;
		try {
			setState(507);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(498);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(497);
					((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(501);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(500);
					((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(503);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(504);
				((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
				setState(505);
				((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
				setState(506);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
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
	public static class AstOptionalFunctionArgumentContext extends ParserRuleContext {
		public AstFunctionArgumentContext astFunctionArgument() {
			return getRuleContext(AstFunctionArgumentContext.class,0);
		}
		public AstOptionalFunctionArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astOptionalFunctionArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOptionalFunctionArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOptionalFunctionArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOptionalFunctionArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstOptionalFunctionArgumentContext astOptionalFunctionArgument() throws RecognitionException {
		AstOptionalFunctionArgumentContext _localctx = new AstOptionalFunctionArgumentContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_astOptionalFunctionArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(510);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & -8606097404135391200L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 3296387399699L) != 0)) {
				{
				setState(509);
				astFunctionArgument();
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
		public AstFunctionArgumentContext astFunctionArgument() {
			return getRuleContext(AstFunctionArgumentContext.class,0);
		}
		public List<AstOptionalFunctionArgumentContext> astOptionalFunctionArgument() {
			return getRuleContexts(AstOptionalFunctionArgumentContext.class);
		}
		public AstOptionalFunctionArgumentContext astOptionalFunctionArgument(int i) {
			return getRuleContext(AstOptionalFunctionArgumentContext.class,i);
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
		enterRule(_localctx, 48, RULE_argumentList);
		try {
			int _alt;
			setState(529);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(512);
				match(LPAREN);
				setState(513);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(514);
				match(LPAREN);
				setState(515);
				astFunctionArgument();
				setState(516);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(518);
				match(LPAREN);
				setState(522); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(519);
						astOptionalFunctionArgument();
						setState(520);
						match(COMMA);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(524); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(526);
				astOptionalFunctionArgument();
				setState(527);
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
	public static class CaseAlternativesContext extends ParserRuleContext {
		public List<AstCaseAlternativeContext> astCaseAlternative() {
			return getRuleContexts(AstCaseAlternativeContext.class);
		}
		public AstCaseAlternativeContext astCaseAlternative(int i) {
			return getRuleContext(AstCaseAlternativeContext.class,i);
		}
		public CaseAlternativesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseAlternatives; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterCaseAlternatives(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitCaseAlternatives(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitCaseAlternatives(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseAlternativesContext caseAlternatives() throws RecognitionException {
		CaseAlternativesContext _localctx = new CaseAlternativesContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_caseAlternatives);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(531);
				astCaseAlternative();
				}
				}
				setState(534); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WHEN );
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
	public static class AstCaseAlternativeContext extends ParserRuleContext {
		public WhenValueListContext values;
		public AstStatementListContext body;
		public TerminalNode WHEN() { return getToken(MindcodeParser.WHEN, 0); }
		public TerminalNode THEN() { return getToken(MindcodeParser.THEN, 0); }
		public WhenValueListContext whenValueList() {
			return getRuleContext(WhenValueListContext.class,0);
		}
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstCaseAlternativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astCaseAlternative; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstCaseAlternative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstCaseAlternative(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstCaseAlternative(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstCaseAlternativeContext astCaseAlternative() throws RecognitionException {
		AstCaseAlternativeContext _localctx = new AstCaseAlternativeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_astCaseAlternative);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(536);
			match(WHEN);
			setState(537);
			((AstCaseAlternativeContext)_localctx).values = whenValueList();
			setState(538);
			match(THEN);
			setState(540);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				{
				setState(539);
				((AstCaseAlternativeContext)_localctx).body = astStatementList();
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
	public static class WhenValueListContext extends ParserRuleContext {
		public List<WhenValueContext> whenValue() {
			return getRuleContexts(WhenValueContext.class);
		}
		public WhenValueContext whenValue(int i) {
			return getRuleContext(WhenValueContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public WhenValueListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenValueList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterWhenValueList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitWhenValueList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitWhenValueList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenValueListContext whenValueList() throws RecognitionException {
		WhenValueListContext _localctx = new WhenValueListContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_whenValueList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(547);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(542);
					whenValue();
					setState(543);
					match(COMMA);
					}
					} 
				}
				setState(549);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
			}
			setState(550);
			whenValue();
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
	public static class WhenValueContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstRangeContext astRange() {
			return getRuleContext(AstRangeContext.class,0);
		}
		public WhenValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterWhenValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitWhenValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitWhenValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenValueContext whenValue() throws RecognitionException {
		WhenValueContext _localctx = new WhenValueContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_whenValue);
		try {
			setState(554);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(552);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(553);
				astRange();
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
	public static class AstRangeContext extends ParserRuleContext {
		public ExpressionContext firstValue;
		public Token operator;
		public ExpressionContext lastValue;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode DOT2() { return getToken(MindcodeParser.DOT2, 0); }
		public TerminalNode DOT3() { return getToken(MindcodeParser.DOT3, 0); }
		public AstRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astRange; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstRangeContext astRange() throws RecognitionException {
		AstRangeContext _localctx = new AstRangeContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_astRange);
		try {
			setState(564);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(556);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(557);
				((AstRangeContext)_localctx).operator = match(DOT2);
				setState(558);
				((AstRangeContext)_localctx).lastValue = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(560);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(561);
				((AstRangeContext)_localctx).operator = match(DOT3);
				setState(562);
				((AstRangeContext)_localctx).lastValue = expression(0);
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
	public static class ElsifBranchesContext extends ParserRuleContext {
		public List<ElsifBranchContext> elsifBranch() {
			return getRuleContexts(ElsifBranchContext.class);
		}
		public ElsifBranchContext elsifBranch(int i) {
			return getRuleContext(ElsifBranchContext.class,i);
		}
		public ElsifBranchesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elsifBranches; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterElsifBranches(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitElsifBranches(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitElsifBranches(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElsifBranchesContext elsifBranches() throws RecognitionException {
		ElsifBranchesContext _localctx = new ElsifBranchesContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_elsifBranches);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(567); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(566);
				elsifBranch();
				}
				}
				setState(569); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ELSIF );
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
	public static class ElsifBranchContext extends ParserRuleContext {
		public ExpressionContext condition;
		public AstStatementListContext body;
		public TerminalNode ELSIF() { return getToken(MindcodeParser.ELSIF, 0); }
		public TerminalNode THEN() { return getToken(MindcodeParser.THEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public ElsifBranchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elsifBranch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterElsifBranch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitElsifBranch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitElsifBranch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElsifBranchContext elsifBranch() throws RecognitionException {
		ElsifBranchContext _localctx = new ElsifBranchContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_elsifBranch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(571);
			match(ELSIF);
			setState(572);
			((ElsifBranchContext)_localctx).condition = expression(0);
			setState(573);
			match(THEN);
			setState(575);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				{
				setState(574);
				((ElsifBranchContext)_localctx).body = astStatementList();
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
	public static class LoopIteratorListContext extends ParserRuleContext {
		public List<AstLoopIteratorContext> astLoopIterator() {
			return getRuleContexts(AstLoopIteratorContext.class);
		}
		public AstLoopIteratorContext astLoopIterator(int i) {
			return getRuleContext(AstLoopIteratorContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public LoopIteratorListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loopIteratorList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterLoopIteratorList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitLoopIteratorList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitLoopIteratorList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoopIteratorListContext loopIteratorList() throws RecognitionException {
		LoopIteratorListContext _localctx = new LoopIteratorListContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_loopIteratorList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(582);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,75,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(577);
					astLoopIterator();
					setState(578);
					match(COMMA);
					}
					} 
				}
				setState(584);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,75,_ctx);
			}
			setState(585);
			astLoopIterator();
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
	public static class AstLoopIteratorContext extends ParserRuleContext {
		public Token modifier;
		public LvalueContext variable;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public AstLoopIteratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astLoopIterator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLoopIterator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLoopIterator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLoopIterator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstLoopIteratorContext astLoopIterator() throws RecognitionException {
		AstLoopIteratorContext _localctx = new AstLoopIteratorContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_astLoopIterator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(588);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OUT) {
				{
				setState(587);
				((AstLoopIteratorContext)_localctx).modifier = match(OUT);
				}
			}

			setState(590);
			((AstLoopIteratorContext)_localctx).variable = lvalue();
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
		case 12:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 13);
		case 1:
			return precpred(_ctx, 12);
		case 2:
			return precpred(_ctx, 11);
		case 3:
			return precpred(_ctx, 10);
		case 4:
			return precpred(_ctx, 9);
		case 5:
			return precpred(_ctx, 8);
		case 6:
			return precpred(_ctx, 7);
		case 7:
			return precpred(_ctx, 6);
		case 8:
			return precpred(_ctx, 5);
		case 9:
			return precpred(_ctx, 4);
		case 10:
			return precpred(_ctx, 3);
		case 11:
			return precpred(_ctx, 2);
		case 12:
			return precpred(_ctx, 30);
		case 13:
			return precpred(_ctx, 29);
		case 14:
			return precpred(_ctx, 28);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0083\u0251\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0001\u0000"+
		"\u0003\u0000F\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0003\u0001"+
		"K\b\u0001\u0001\u0001\u0004\u0001N\b\u0001\u000b\u0001\f\u0001O\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0005\u0002U\b\u0002\n\u0002\f\u0002X\t"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0005\u0003a\b\u0003\n\u0003\f\u0003d\t\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003u\b\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003{\b\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u0081\b\u0003\u0001\u0003\u0001"+
		"\u0003\u0003\u0003\u0085\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u008c\b\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u0092\b\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003\u0096\b\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u009a\b\u0003"+
		"\u0001\u0003\u0001\u0003\u0003\u0003\u009e\b\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003\u00a2\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"\u00a7\b\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00ab\b\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00b2"+
		"\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00b8"+
		"\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00be"+
		"\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00c4"+
		"\b\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00c8\b\u0003\u0001\u0003"+
		"\u0003\u0003\u00cb\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003\u00d1\b\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00d5\b"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00d9\b\u0003\u0001\u0003\u0001"+
		"\u0003\u0003\u0003\u00dd\b\u0003\u0001\u0003\u0003\u0003\u00e0\b\u0003"+
		"\u0001\u0004\u0001\u0004\u0003\u0004\u00e4\b\u0004\u0001\u0005\u0005\u0005"+
		"\u00e7\b\u0005\n\u0005\f\u0005\u00ea\t\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0004\u0005\u00f0\b\u0005\u000b\u0005\f\u0005\u00f1"+
		"\u0001\u0005\u0001\u0005\u0003\u0005\u00f6\b\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0005\b\u00ff\b\b\n\b"+
		"\f\b\u0102\t\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0003\t\u0109\b"+
		"\t\u0001\t\u0001\t\u0001\t\u0003\t\u010e\b\t\u0001\t\u0001\t\u0001\t\u0003"+
		"\t\u0113\b\t\u0003\t\u0115\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0123\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u012f\b\f\u0001"+
		"\f\u0001\f\u0003\f\u0133\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0003\f\u013b\b\f\u0001\f\u0003\f\u013e\b\f\u0001\f\u0001\f\u0003\f"+
		"\u0142\b\f\u0003\f\u0144\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u014a"+
		"\b\f\n\f\f\f\u014d\t\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u0163\b\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0005\f\u0196\b\f\n\f\f\f\u0199\t\f\u0001\r\u0001\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u01a2\b\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0003\u000e\u01a7\b\u000e\u0003\u000e\u01a9\b"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u01af"+
		"\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u01b4\b\u0010"+
		"\n\u0010\f\u0010\u01b7\t\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u01c0\b\u0012\n"+
		"\u0012\f\u0012\u01c3\t\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003"+
		"\u0013\u01ce\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0005\u0014\u01d6\b\u0014\n\u0014\f\u0014\u01d9\t\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u01de\b\u0014\u0001\u0015"+
		"\u0003\u0015\u01e1\b\u0015\u0001\u0015\u0003\u0015\u01e4\b\u0015\u0001"+
		"\u0015\u0001\u0015\u0003\u0015\u01e8\b\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0003\u0015\u01ee\b\u0015\u0003\u0015\u01f0\b\u0015"+
		"\u0001\u0016\u0003\u0016\u01f3\b\u0016\u0001\u0016\u0003\u0016\u01f6\b"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01fc"+
		"\b\u0016\u0001\u0017\u0003\u0017\u01ff\b\u0017\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0004\u0018\u020b\b\u0018\u000b\u0018\f\u0018"+
		"\u020c\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u0212\b\u0018\u0001"+
		"\u0019\u0004\u0019\u0215\b\u0019\u000b\u0019\f\u0019\u0216\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u021d\b\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0005\u001b\u0222\b\u001b\n\u001b\f\u001b\u0225"+
		"\t\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0003\u001c\u022b"+
		"\b\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0235\b\u001d\u0001\u001e\u0004"+
		"\u001e\u0238\b\u001e\u000b\u001e\f\u001e\u0239\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0003\u001f\u0240\b\u001f\u0001 \u0001 \u0001"+
		" \u0005 \u0245\b \n \f \u0248\t \u0001 \u0001 \u0001!\u0003!\u024d\b!"+
		"\u0001!\u0001!\u0001!\u0000\u0001\u0018\"\u0000\u0002\u0004\u0006\b\n"+
		"\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246"+
		"8:<>@B\u0000\u0011\u0002\u0000\u0015\u0015\u0019\u0019\u0002\u0000\b\b"+
		"##\u0005\u0000\u0004\u0004\u000f\u000f\u0016\u0016\u0018\u0018$$\u0001"+
		"\u0000ab\u0002\u0000\u0010\u0010!!\u0002\u0000??BB\u0005\u0000004477C"+
		"CFF\u0002\u0000@ADE\u0002\u0000CCFF\u0001\u00009:\u0001\u000012\u0001"+
		"\u0000(+\u0002\u0000\'\',.\u0002\u00003366\u0002\u00005588\u0001\u0000"+
		"HV\u0002\u0000\u0012\u0012\u001f\u001f\u02ae\u0000E\u0001\u0000\u0000"+
		"\u0000\u0002M\u0001\u0000\u0000\u0000\u0004V\u0001\u0000\u0000\u0000\u0006"+
		"\u00df\u0001\u0000\u0000\u0000\b\u00e3\u0001\u0000\u0000\u0000\n\u00f5"+
		"\u0001\u0000\u0000\u0000\f\u00f7\u0001\u0000\u0000\u0000\u000e\u00f9\u0001"+
		"\u0000\u0000\u0000\u0010\u0100\u0001\u0000\u0000\u0000\u0012\u0114\u0001"+
		"\u0000\u0000\u0000\u0014\u0116\u0001\u0000\u0000\u0000\u0016\u0122\u0001"+
		"\u0000\u0000\u0000\u0018\u0162\u0001\u0000\u0000\u0000\u001a\u01a1\u0001"+
		"\u0000\u0000\u0000\u001c\u01a8\u0001\u0000\u0000\u0000\u001e\u01aa\u0001"+
		"\u0000\u0000\u0000 \u01b5\u0001\u0000\u0000\u0000\"\u01ba\u0001\u0000"+
		"\u0000\u0000$\u01c1\u0001\u0000\u0000\u0000&\u01c6\u0001\u0000\u0000\u0000"+
		"(\u01dd\u0001\u0000\u0000\u0000*\u01ef\u0001\u0000\u0000\u0000,\u01fb"+
		"\u0001\u0000\u0000\u0000.\u01fe\u0001\u0000\u0000\u00000\u0211\u0001\u0000"+
		"\u0000\u00002\u0214\u0001\u0000\u0000\u00004\u0218\u0001\u0000\u0000\u0000"+
		"6\u0223\u0001\u0000\u0000\u00008\u022a\u0001\u0000\u0000\u0000:\u0234"+
		"\u0001\u0000\u0000\u0000<\u0237\u0001\u0000\u0000\u0000>\u023b\u0001\u0000"+
		"\u0000\u0000@\u0246\u0001\u0000\u0000\u0000B\u024c\u0001\u0000\u0000\u0000"+
		"DF\u0003\u0002\u0001\u0000ED\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000"+
		"\u0000FG\u0001\u0000\u0000\u0000GH\u0005\u0000\u0000\u0001H\u0001\u0001"+
		"\u0000\u0000\u0000IK\u0003\u0006\u0003\u0000JI\u0001\u0000\u0000\u0000"+
		"JK\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000LN\u0005`\u0000\u0000"+
		"MJ\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000"+
		"\u0000OP\u0001\u0000\u0000\u0000P\u0003\u0001\u0000\u0000\u0000QR\u0003"+
		"\u0018\f\u0000RS\u0005Y\u0000\u0000SU\u0001\u0000\u0000\u0000TQ\u0001"+
		"\u0000\u0000\u0000UX\u0001\u0000\u0000\u0000VT\u0001\u0000\u0000\u0000"+
		"VW\u0001\u0000\u0000\u0000WY\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000"+
		"\u0000YZ\u0003\u0018\f\u0000Z\u0005\u0001\u0000\u0000\u0000[\u00e0\u0003"+
		"\u0018\f\u0000\\\u00e0\u0003\u001e\u000f\u0000]\u00e0\u0003\n\u0005\u0000"+
		"^b\u0005n\u0000\u0000_a\u0003\u001a\r\u0000`_\u0001\u0000\u0000\u0000"+
		"ad\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000"+
		"\u0000c\u00e0\u0001\u0000\u0000\u0000db\u0001\u0000\u0000\u0000ef\u0005"+
		"\u0001\u0000\u0000f\u00e0\u0003$\u0012\u0000gh\u0005\u0006\u0000\u0000"+
		"hi\u0005a\u0000\u0000ij\u0005H\u0000\u0000j\u00e0\u0003\u0018\f\u0000"+
		"kl\u0005\u001c\u0000\u0000lm\u0005a\u0000\u0000mn\u0005H\u0000\u0000n"+
		"\u00e0\u0003\u0018\f\u0000op\u0005\u001e\u0000\u0000p\u00e0\u0005d\u0000"+
		"\u0000qr\u0005\u001e\u0000\u0000r\u00e0\u0005a\u0000\u0000su\u0007\u0000"+
		"\u0000\u0000ts\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000uv\u0001"+
		"\u0000\u0000\u0000vw\u0007\u0001\u0000\u0000wx\u0005a\u0000\u0000xz\u0003"+
		"(\u0014\u0000y{\u0003\u0002\u0001\u0000zy\u0001\u0000\u0000\u0000z{\u0001"+
		"\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|}\u0005\u000e\u0000\u0000"+
		"}\u00e0\u0001\u0000\u0000\u0000~\u007f\u0005a\u0000\u0000\u007f\u0081"+
		"\u0005X\u0000\u0000\u0080~\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000"+
		"\u0000\u0000\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u0084\u0005\u0011"+
		"\u0000\u0000\u0083\u0085\u0003\u000e\u0007\u0000\u0084\u0083\u0001\u0000"+
		"\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000"+
		"\u0000\u0000\u0086\u0087\u0003@ \u0000\u0087\u0088\u0005\u0014\u0000\u0000"+
		"\u0088\u0089\u0003\u0004\u0002\u0000\u0089\u008b\u0005\t\u0000\u0000\u008a"+
		"\u008c\u0003\u0002\u0001\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008b"+
		"\u008c\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d"+
		"\u008e\u0005\u000e\u0000\u0000\u008e\u00e0\u0001\u0000\u0000\u0000\u008f"+
		"\u0090\u0005a\u0000\u0000\u0090\u0092\u0005X\u0000\u0000\u0091\u008f\u0001"+
		"\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0001"+
		"\u0000\u0000\u0000\u0093\u0095\u0005\u0011\u0000\u0000\u0094\u0096\u0003"+
		"\b\u0004\u0000\u0095\u0094\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0099\u0005`\u0000"+
		"\u0000\u0098\u009a\u0003\u0018\f\u0000\u0099\u0098\u0001\u0000\u0000\u0000"+
		"\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000"+
		"\u009b\u009d\u0005`\u0000\u0000\u009c\u009e\u0003\u0004\u0002\u0000\u009d"+
		"\u009c\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e"+
		"\u009f\u0001\u0000\u0000\u0000\u009f\u00a1\u0005\t\u0000\u0000\u00a0\u00a2"+
		"\u0003\u0002\u0001\u0000\u00a1\u00a0\u0001\u0000\u0000\u0000\u00a1\u00a2"+
		"\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u00e0"+
		"\u0005\u000e\u0000\u0000\u00a4\u00a5\u0005a\u0000\u0000\u00a5\u00a7\u0005"+
		"X\u0000\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000"+
		"\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u00aa\u0005\u0011"+
		"\u0000\u0000\u00a9\u00ab\u0003\u000e\u0007\u0000\u00aa\u00a9\u0001\u0000"+
		"\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000"+
		"\u0000\u0000\u00ac\u00ad\u0003\u0016\u000b\u0000\u00ad\u00ae\u0005\u0014"+
		"\u0000\u0000\u00ae\u00af\u0003:\u001d\u0000\u00af\u00b1\u0005\t\u0000"+
		"\u0000\u00b0\u00b2\u0003\u0002\u0001\u0000\u00b1\u00b0\u0001\u0000\u0000"+
		"\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000"+
		"\u0000\u00b3\u00b4\u0005\u000e\u0000\u0000\u00b4\u00e0\u0001\u0000\u0000"+
		"\u0000\u00b5\u00b6\u0005a\u0000\u0000\u00b6\u00b8\u0005X\u0000\u0000\u00b7"+
		"\u00b5\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8"+
		"\u00b9\u0001\u0000\u0000\u0000\u00b9\u00ba\u0005&\u0000\u0000\u00ba\u00bb"+
		"\u0003\u0018\f\u0000\u00bb\u00bd\u0005\t\u0000\u0000\u00bc\u00be\u0003"+
		"\u0002\u0001\u0000\u00bd\u00bc\u0001\u0000\u0000\u0000\u00bd\u00be\u0001"+
		"\u0000\u0000\u0000\u00be\u00bf\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005"+
		"\u000e\u0000\u0000\u00c0\u00e0\u0001\u0000\u0000\u0000\u00c1\u00c2\u0005"+
		"a\u0000\u0000\u00c2\u00c4\u0005X\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000"+
		"\u0000\u00c3\u00c4\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000"+
		"\u0000\u00c5\u00c7\u0005\t\u0000\u0000\u00c6\u00c8\u0003\u0002\u0001\u0000"+
		"\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000"+
		"\u00c8\u00ca\u0001\u0000\u0000\u0000\u00c9\u00cb\u0005\u0017\u0000\u0000"+
		"\u00ca\u00c9\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000"+
		"\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005&\u0000\u0000\u00cd"+
		"\u00e0\u0003\u0018\f\u0000\u00ce\u00d0\u0005\u0003\u0000\u0000\u00cf\u00d1"+
		"\u0005a\u0000\u0000\u00d0\u00cf\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001"+
		"\u0000\u0000\u0000\u00d1\u00e0\u0001\u0000\u0000\u0000\u00d2\u00d4\u0005"+
		"\u0007\u0000\u0000\u00d3\u00d5\u0005a\u0000\u0000\u00d4\u00d3\u0001\u0000"+
		"\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000\u00d5\u00e0\u0001\u0000"+
		"\u0000\u0000\u00d6\u00d8\u0005\u001d\u0000\u0000\u00d7\u00d9\u0003\u0018"+
		"\f\u0000\u00d8\u00d7\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000"+
		"\u0000\u00d9\u00e0\u0001\u0000\u0000\u0000\u00da\u00dc\u0005\u0002\u0000"+
		"\u0000\u00db\u00dd\u0003\u0002\u0001\u0000\u00dc\u00db\u0001\u0000\u0000"+
		"\u0000\u00dc\u00dd\u0001\u0000\u0000\u0000\u00dd\u00de\u0001\u0000\u0000"+
		"\u0000\u00de\u00e0\u0005\u000e\u0000\u0000\u00df[\u0001\u0000\u0000\u0000"+
		"\u00df\\\u0001\u0000\u0000\u0000\u00df]\u0001\u0000\u0000\u0000\u00df"+
		"^\u0001\u0000\u0000\u0000\u00dfe\u0001\u0000\u0000\u0000\u00dfg\u0001"+
		"\u0000\u0000\u0000\u00dfk\u0001\u0000\u0000\u0000\u00dfo\u0001\u0000\u0000"+
		"\u0000\u00dfq\u0001\u0000\u0000\u0000\u00dft\u0001\u0000\u0000\u0000\u00df"+
		"\u0080\u0001\u0000\u0000\u0000\u00df\u0091\u0001\u0000\u0000\u0000\u00df"+
		"\u00a6\u0001\u0000\u0000\u0000\u00df\u00b7\u0001\u0000\u0000\u0000\u00df"+
		"\u00c3\u0001\u0000\u0000\u0000\u00df\u00ce\u0001\u0000\u0000\u0000\u00df"+
		"\u00d2\u0001\u0000\u0000\u0000\u00df\u00d6\u0001\u0000\u0000\u0000\u00df"+
		"\u00da\u0001\u0000\u0000\u0000\u00e0\u0007\u0001\u0000\u0000\u0000\u00e1"+
		"\u00e4\u0003\n\u0005\u0000\u00e2\u00e4\u0003\u0004\u0002\u0000\u00e3\u00e1"+
		"\u0001\u0000\u0000\u0000\u00e3\u00e2\u0001\u0000\u0000\u0000\u00e4\t\u0001"+
		"\u0000\u0000\u0000\u00e5\u00e7\u0003\f\u0006\u0000\u00e6\u00e5\u0001\u0000"+
		"\u0000\u0000\u00e7\u00ea\u0001\u0000\u0000\u0000\u00e8\u00e6\u0001\u0000"+
		"\u0000\u0000\u00e8\u00e9\u0001\u0000\u0000\u0000\u00e9\u00eb\u0001\u0000"+
		"\u0000\u0000\u00ea\u00e8\u0001\u0000\u0000\u0000\u00eb\u00ec\u0003\u000e"+
		"\u0007\u0000\u00ec\u00ed\u0003\u0010\b\u0000\u00ed\u00f6\u0001\u0000\u0000"+
		"\u0000\u00ee\u00f0\u0003\f\u0006\u0000\u00ef\u00ee\u0001\u0000\u0000\u0000"+
		"\u00f0\u00f1\u0001\u0000\u0000\u0000\u00f1\u00ef\u0001\u0000\u0000\u0000"+
		"\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2\u00f3\u0001\u0000\u0000\u0000"+
		"\u00f3\u00f4\u0003\u0010\b\u0000\u00f4\u00f6\u0001\u0000\u0000\u0000\u00f5"+
		"\u00e8\u0001\u0000\u0000\u0000\u00f5\u00ef\u0001\u0000\u0000\u0000\u00f6"+
		"\u000b\u0001\u0000\u0000\u0000\u00f7\u00f8\u0007\u0002\u0000\u0000\u00f8"+
		"\r\u0001\u0000\u0000\u0000\u00f9\u00fa\u0005\"\u0000\u0000\u00fa\u000f"+
		"\u0001\u0000\u0000\u0000\u00fb\u00fc\u0003\u0012\t\u0000\u00fc\u00fd\u0005"+
		"Y\u0000\u0000\u00fd\u00ff\u0001\u0000\u0000\u0000\u00fe\u00fb\u0001\u0000"+
		"\u0000\u0000\u00ff\u0102\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000"+
		"\u0000\u0000\u0100\u0101\u0001\u0000\u0000\u0000\u0101\u0103\u0001\u0000"+
		"\u0000\u0000\u0102\u0100\u0001\u0000\u0000\u0000\u0103\u0104\u0003\u0012"+
		"\t\u0000\u0104\u0011\u0001\u0000\u0000\u0000\u0105\u0108\u0007\u0003\u0000"+
		"\u0000\u0106\u0107\u0005H\u0000\u0000\u0107\u0109\u0003\u0018\f\u0000"+
		"\u0108\u0106\u0001\u0000\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000"+
		"\u0109\u0115\u0001\u0000\u0000\u0000\u010a\u010b\u0007\u0003\u0000\u0000"+
		"\u010b\u010d\u0005=\u0000\u0000\u010c\u010e\u0003\u0018\f\u0000\u010d"+
		"\u010c\u0001\u0000\u0000\u0000\u010d\u010e\u0001\u0000\u0000\u0000\u010e"+
		"\u010f\u0001\u0000\u0000\u0000\u010f\u0112\u0005>\u0000\u0000\u0110\u0111"+
		"\u0005H\u0000\u0000\u0111\u0113\u0003\u0014\n\u0000\u0112\u0110\u0001"+
		"\u0000\u0000\u0000\u0112\u0113\u0001\u0000\u0000\u0000\u0113\u0115\u0001"+
		"\u0000\u0000\u0000\u0114\u0105\u0001\u0000\u0000\u0000\u0114\u010a\u0001"+
		"\u0000\u0000\u0000\u0115\u0013\u0001\u0000\u0000\u0000\u0116\u0117\u0005"+
		";\u0000\u0000\u0117\u0118\u0003\u0004\u0002\u0000\u0118\u0119\u0005<\u0000"+
		"\u0000\u0119\u0015\u0001\u0000\u0000\u0000\u011a\u0123\u0005a\u0000\u0000"+
		"\u011b\u0123\u0005b\u0000\u0000\u011c\u0123\u0005c\u0000\u0000\u011d\u011e"+
		"\u0005a\u0000\u0000\u011e\u011f\u0005=\u0000\u0000\u011f\u0120\u0003\u0018"+
		"\f\u0000\u0120\u0121\u0005>\u0000\u0000\u0121\u0123\u0001\u0000\u0000"+
		"\u0000\u0122\u011a\u0001\u0000\u0000\u0000\u0122\u011b\u0001\u0000\u0000"+
		"\u0000\u0122\u011c\u0001\u0000\u0000\u0000\u0122\u011d\u0001\u0000\u0000"+
		"\u0000\u0123\u0017\u0001\u0000\u0000\u0000\u0124\u0125\u0006\f\uffff\uffff"+
		"\u0000\u0125\u0163\u0003\u0016\u000b\u0000\u0126\u0127\u0005\u000e\u0000"+
		"\u0000\u0127\u0128\u0005;\u0000\u0000\u0128\u0163\u0005<\u0000\u0000\u0129"+
		"\u012a\u0005a\u0000\u0000\u012a\u0163\u00030\u0018\u0000\u012b\u012c\u0005"+
		"\u0005\u0000\u0000\u012c\u012e\u0003\u0018\f\u0000\u012d\u012f\u00032"+
		"\u0019\u0000\u012e\u012d\u0001\u0000\u0000\u0000\u012e\u012f\u0001\u0000"+
		"\u0000\u0000\u012f\u0132\u0001\u0000\u0000\u0000\u0130\u0131\u0005\u000b"+
		"\u0000\u0000\u0131\u0133\u0003\u0002\u0001\u0000\u0132\u0130\u0001\u0000"+
		"\u0000\u0000\u0132\u0133\u0001\u0000\u0000\u0000\u0133\u0134\u0001\u0000"+
		"\u0000\u0000\u0134\u0135\u0005\u000e\u0000\u0000\u0135\u0163\u0001\u0000"+
		"\u0000\u0000\u0136\u0137\u0005\u0013\u0000\u0000\u0137\u0138\u0003\u0018"+
		"\f\u0000\u0138\u013a\u0005 \u0000\u0000\u0139\u013b\u0003\u0002\u0001"+
		"\u0000\u013a\u0139\u0001\u0000\u0000\u0000\u013a\u013b\u0001\u0000\u0000"+
		"\u0000\u013b\u013d\u0001\u0000\u0000\u0000\u013c\u013e\u0003<\u001e\u0000"+
		"\u013d\u013c\u0001\u0000\u0000\u0000\u013d\u013e\u0001\u0000\u0000\u0000"+
		"\u013e\u0143\u0001\u0000\u0000\u0000\u013f\u0141\u0005\u000b\u0000\u0000"+
		"\u0140\u0142\u0003\u0002\u0001\u0000\u0141\u0140\u0001\u0000\u0000\u0000"+
		"\u0141\u0142\u0001\u0000\u0000\u0000\u0142\u0144\u0001\u0000\u0000\u0000"+
		"\u0143\u013f\u0001\u0000\u0000\u0000\u0143\u0144\u0001\u0000\u0000\u0000"+
		"\u0144\u0145\u0001\u0000\u0000\u0000\u0145\u0146\u0005\u000e\u0000\u0000"+
		"\u0146\u0163\u0001\u0000\u0000\u0000\u0147\u014b\u0005k\u0000\u0000\u0148"+
		"\u014a\u0003\u001a\r\u0000\u0149\u0148\u0001\u0000\u0000\u0000\u014a\u014d"+
		"\u0001\u0000\u0000\u0000\u014b\u0149\u0001\u0000\u0000\u0000\u014b\u014c"+
		"\u0001\u0000\u0000\u0000\u014c\u014e\u0001\u0000\u0000\u0000\u014d\u014b"+
		"\u0001\u0000\u0000\u0000\u014e\u0163\u0005^\u0000\u0000\u014f\u0163\u0005"+
		"d\u0000\u0000\u0150\u0163\u0005e\u0000\u0000\u0151\u0163\u0005f\u0000"+
		"\u0000\u0152\u0163\u0005g\u0000\u0000\u0153\u0163\u0005h\u0000\u0000\u0154"+
		"\u0163\u0005i\u0000\u0000\u0155\u0163\u0005\u001a\u0000\u0000\u0156\u0163"+
		"\u0007\u0004\u0000\u0000\u0157\u0158\u0003\u0016\u000b\u0000\u0158\u0159"+
		"\u0007\u0005\u0000\u0000\u0159\u0163\u0001\u0000\u0000\u0000\u015a\u015b"+
		"\u0007\u0005\u0000\u0000\u015b\u0163\u0003\u0016\u000b\u0000\u015c\u015d"+
		"\u0007\u0006\u0000\u0000\u015d\u0163\u0003\u0018\f\u000e\u015e\u015f\u0005"+
		";\u0000\u0000\u015f\u0160\u0003\u0018\f\u0000\u0160\u0161\u0005<\u0000"+
		"\u0000\u0161\u0163\u0001\u0000\u0000\u0000\u0162\u0124\u0001\u0000\u0000"+
		"\u0000\u0162\u0126\u0001\u0000\u0000\u0000\u0162\u0129\u0001\u0000\u0000"+
		"\u0000\u0162\u012b\u0001\u0000\u0000\u0000\u0162\u0136\u0001\u0000\u0000"+
		"\u0000\u0162\u0147\u0001\u0000\u0000\u0000\u0162\u014f\u0001\u0000\u0000"+
		"\u0000\u0162\u0150\u0001\u0000\u0000\u0000\u0162\u0151\u0001\u0000\u0000"+
		"\u0000\u0162\u0152\u0001\u0000\u0000\u0000\u0162\u0153\u0001\u0000\u0000"+
		"\u0000\u0162\u0154\u0001\u0000\u0000\u0000\u0162\u0155\u0001\u0000\u0000"+
		"\u0000\u0162\u0156\u0001\u0000\u0000\u0000\u0162\u0157\u0001\u0000\u0000"+
		"\u0000\u0162\u015a\u0001\u0000\u0000\u0000\u0162\u015c\u0001\u0000\u0000"+
		"\u0000\u0162\u015e\u0001\u0000\u0000\u0000\u0163\u0197\u0001\u0000\u0000"+
		"\u0000\u0164\u0165\n\r\u0000\u0000\u0165\u0166\u0005G\u0000\u0000\u0166"+
		"\u0196\u0003\u0018\f\u000e\u0167\u0168\n\f\u0000\u0000\u0168\u0169\u0007"+
		"\u0007\u0000\u0000\u0169\u0196\u0003\u0018\f\r\u016a\u016b\n\u000b\u0000"+
		"\u0000\u016b\u016c\u0007\b\u0000\u0000\u016c\u0196\u0003\u0018\f\f\u016d"+
		"\u016e\n\n\u0000\u0000\u016e\u016f\u0007\t\u0000\u0000\u016f\u0196\u0003"+
		"\u0018\f\u000b\u0170\u0171\n\t\u0000\u0000\u0171\u0172\u0005/\u0000\u0000"+
		"\u0172\u0196\u0003\u0018\f\n\u0173\u0174\n\b\u0000\u0000\u0174\u0175\u0007"+
		"\n\u0000\u0000\u0175\u0196\u0003\u0018\f\t\u0176\u0177\n\u0007\u0000\u0000"+
		"\u0177\u0178\u0007\u000b\u0000\u0000\u0178\u0196\u0003\u0018\f\b\u0179"+
		"\u017a\n\u0006\u0000\u0000\u017a\u017b\u0007\f\u0000\u0000\u017b\u0196"+
		"\u0003\u0018\f\u0007\u017c\u017d\n\u0005\u0000\u0000\u017d\u017e\u0007"+
		"\r\u0000\u0000\u017e\u0196\u0003\u0018\f\u0006\u017f\u0180\n\u0004\u0000"+
		"\u0000\u0180\u0181\u0007\u000e\u0000\u0000\u0181\u0196\u0003\u0018\f\u0005"+
		"\u0182\u0183\n\u0003\u0000\u0000\u0183\u0184\u0005_\u0000\u0000\u0184"+
		"\u0185\u0003\u0018\f\u0000\u0185\u0186\u0005X\u0000\u0000\u0186\u0187"+
		"\u0003\u0018\f\u0003\u0187\u0196\u0001\u0000\u0000\u0000\u0188\u0189\n"+
		"\u0002\u0000\u0000\u0189\u018a\u0007\u000f\u0000\u0000\u018a\u0196\u0003"+
		"\u0018\f\u0002\u018b\u018c\n\u001e\u0000\u0000\u018c\u018d\u0005[\u0000"+
		"\u0000\u018d\u018e\u0005a\u0000\u0000\u018e\u0196\u00030\u0018\u0000\u018f"+
		"\u0190\n\u001d\u0000\u0000\u0190\u0191\u0005[\u0000\u0000\u0191\u0196"+
		"\u0005a\u0000\u0000\u0192\u0193\n\u001c\u0000\u0000\u0193\u0194\u0005"+
		"[\u0000\u0000\u0194\u0196\u0005c\u0000\u0000\u0195\u0164\u0001\u0000\u0000"+
		"\u0000\u0195\u0167\u0001\u0000\u0000\u0000\u0195\u016a\u0001\u0000\u0000"+
		"\u0000\u0195\u016d\u0001\u0000\u0000\u0000\u0195\u0170\u0001\u0000\u0000"+
		"\u0000\u0195\u0173\u0001\u0000\u0000\u0000\u0195\u0176\u0001\u0000\u0000"+
		"\u0000\u0195\u0179\u0001\u0000\u0000\u0000\u0195\u017c\u0001\u0000\u0000"+
		"\u0000\u0195\u017f\u0001\u0000\u0000\u0000\u0195\u0182\u0001\u0000\u0000"+
		"\u0000\u0195\u0188\u0001\u0000\u0000\u0000\u0195\u018b\u0001\u0000\u0000"+
		"\u0000\u0195\u018f\u0001\u0000\u0000\u0000\u0195\u0192\u0001\u0000\u0000"+
		"\u0000\u0196\u0199\u0001\u0000\u0000\u0000\u0197\u0195\u0001\u0000\u0000"+
		"\u0000\u0197\u0198\u0001\u0000\u0000\u0000\u0198\u0019\u0001\u0000\u0000"+
		"\u0000\u0199\u0197\u0001\u0000\u0000\u0000\u019a\u01a2\u0005|\u0000\u0000"+
		"\u019b\u01a2\u0005}\u0000\u0000\u019c\u019d\u0005\u007f\u0000\u0000\u019d"+
		"\u019e\u0003\u0018\f\u0000\u019e\u019f\u0005l\u0000\u0000\u019f\u01a2"+
		"\u0001\u0000\u0000\u0000\u01a0\u01a2\u0003\u001c\u000e\u0000\u01a1\u019a"+
		"\u0001\u0000\u0000\u0000\u01a1\u019b\u0001\u0000\u0000\u0000\u01a1\u019c"+
		"\u0001\u0000\u0000\u0000\u01a1\u01a0\u0001\u0000\u0000\u0000\u01a2\u001b"+
		"\u0001\u0000\u0000\u0000\u01a3\u01a9\u0005~\u0000\u0000\u01a4\u01a6\u0005"+
		"\u0080\u0000\u0000\u01a5\u01a7\u0005\u0082\u0000\u0000\u01a6\u01a5\u0001"+
		"\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000\u0000\u0000\u01a7\u01a9\u0001"+
		"\u0000\u0000\u0000\u01a8\u01a3\u0001\u0000\u0000\u0000\u01a8\u01a4\u0001"+
		"\u0000\u0000\u0000\u01a9\u001d\u0001\u0000\u0000\u0000\u01aa\u01ab\u0005"+
		"j\u0000\u0000\u01ab\u01ae\u0003\"\u0011\u0000\u01ac\u01ad\u0005w\u0000"+
		"\u0000\u01ad\u01af\u0003 \u0010\u0000\u01ae\u01ac\u0001\u0000\u0000\u0000"+
		"\u01ae\u01af\u0001\u0000\u0000\u0000\u01af\u001f\u0001\u0000\u0000\u0000"+
		"\u01b0\u01b1\u0003\"\u0011\u0000\u01b1\u01b2\u0005x\u0000\u0000\u01b2"+
		"\u01b4\u0001\u0000\u0000\u0000\u01b3\u01b0\u0001\u0000\u0000\u0000\u01b4"+
		"\u01b7\u0001\u0000\u0000\u0000\u01b5\u01b3\u0001\u0000\u0000\u0000\u01b5"+
		"\u01b6\u0001\u0000\u0000\u0000\u01b6\u01b8\u0001\u0000\u0000\u0000\u01b7"+
		"\u01b5\u0001\u0000\u0000\u0000\u01b8\u01b9\u0003\"\u0011\u0000\u01b9!"+
		"\u0001\u0000\u0000\u0000\u01ba\u01bb\u0005v\u0000\u0000\u01bb#\u0001\u0000"+
		"\u0000\u0000\u01bc\u01bd\u0003&\u0013\u0000\u01bd\u01be\u0005Y\u0000\u0000"+
		"\u01be\u01c0\u0001\u0000\u0000\u0000\u01bf\u01bc\u0001\u0000\u0000\u0000"+
		"\u01c0\u01c3\u0001\u0000\u0000\u0000\u01c1\u01bf\u0001\u0000\u0000\u0000"+
		"\u01c1\u01c2\u0001\u0000\u0000\u0000\u01c2\u01c4\u0001\u0000\u0000\u0000"+
		"\u01c3\u01c1\u0001\u0000\u0000\u0000\u01c4\u01c5\u0003&\u0013\u0000\u01c5"+
		"%\u0001\u0000\u0000\u0000\u01c6\u01c7\u0007\u0010\u0000\u0000\u01c7\u01c8"+
		"\u0005\u0014\u0000\u0000\u01c8\u01cd\u0005a\u0000\u0000\u01c9\u01ca\u0005"+
		"=\u0000\u0000\u01ca\u01cb\u0003:\u001d\u0000\u01cb\u01cc\u0005>\u0000"+
		"\u0000\u01cc\u01ce\u0001\u0000\u0000\u0000\u01cd\u01c9\u0001\u0000\u0000"+
		"\u0000\u01cd\u01ce\u0001\u0000\u0000\u0000\u01ce\'\u0001\u0000\u0000\u0000"+
		"\u01cf\u01d0\u0005;\u0000\u0000\u01d0\u01de\u0005<\u0000\u0000\u01d1\u01d7"+
		"\u0005;\u0000\u0000\u01d2\u01d3\u0003*\u0015\u0000\u01d3\u01d4\u0005Y"+
		"\u0000\u0000\u01d4\u01d6\u0001\u0000\u0000\u0000\u01d5\u01d2\u0001\u0000"+
		"\u0000\u0000\u01d6\u01d9\u0001\u0000\u0000\u0000\u01d7\u01d5\u0001\u0000"+
		"\u0000\u0000\u01d7\u01d8\u0001\u0000\u0000\u0000\u01d8\u01da\u0001\u0000"+
		"\u0000\u0000\u01d9\u01d7\u0001\u0000\u0000\u0000\u01da\u01db\u0003*\u0015"+
		"\u0000\u01db\u01dc\u0005<\u0000\u0000\u01dc\u01de\u0001\u0000\u0000\u0000"+
		"\u01dd\u01cf\u0001\u0000\u0000\u0000\u01dd\u01d1\u0001\u0000\u0000\u0000"+
		"\u01de)\u0001\u0000\u0000\u0000\u01df\u01e1\u0005\u0014\u0000\u0000\u01e0"+
		"\u01df\u0001\u0000\u0000\u0000\u01e0\u01e1\u0001\u0000\u0000\u0000\u01e1"+
		"\u01e3\u0001\u0000\u0000\u0000\u01e2\u01e4\u0005\u001b\u0000\u0000\u01e3"+
		"\u01e2\u0001\u0000\u0000\u0000\u01e3\u01e4\u0001\u0000\u0000\u0000\u01e4"+
		"\u01e5\u0001\u0000\u0000\u0000\u01e5\u01e7\u0005a\u0000\u0000\u01e6\u01e8"+
		"\u0005]\u0000\u0000\u01e7\u01e6\u0001\u0000\u0000\u0000\u01e7\u01e8\u0001"+
		"\u0000\u0000\u0000\u01e8\u01f0\u0001\u0000\u0000\u0000\u01e9\u01ea\u0005"+
		"\u001b\u0000\u0000\u01ea\u01eb\u0005\u0014\u0000\u0000\u01eb\u01ed\u0005"+
		"a\u0000\u0000\u01ec\u01ee\u0005]\u0000\u0000\u01ed\u01ec\u0001\u0000\u0000"+
		"\u0000\u01ed\u01ee\u0001\u0000\u0000\u0000\u01ee\u01f0\u0001\u0000\u0000"+
		"\u0000\u01ef\u01e0\u0001\u0000\u0000\u0000\u01ef\u01e9\u0001\u0000\u0000"+
		"\u0000\u01f0+\u0001\u0000\u0000\u0000\u01f1\u01f3\u0005\u0014\u0000\u0000"+
		"\u01f2\u01f1\u0001\u0000\u0000\u0000\u01f2\u01f3\u0001\u0000\u0000\u0000"+
		"\u01f3\u01f5\u0001\u0000\u0000\u0000\u01f4\u01f6\u0005\u001b\u0000\u0000"+
		"\u01f5\u01f4\u0001\u0000\u0000\u0000\u01f5\u01f6\u0001\u0000\u0000\u0000"+
		"\u01f6\u01f7\u0001\u0000\u0000\u0000\u01f7\u01fc\u0003\u0018\f\u0000\u01f8"+
		"\u01f9\u0005\u001b\u0000\u0000\u01f9\u01fa\u0005\u0014\u0000\u0000\u01fa"+
		"\u01fc\u0003\u0018\f\u0000\u01fb\u01f2\u0001\u0000\u0000\u0000\u01fb\u01f8"+
		"\u0001\u0000\u0000\u0000\u01fc-\u0001\u0000\u0000\u0000\u01fd\u01ff\u0003"+
		",\u0016\u0000\u01fe\u01fd\u0001\u0000\u0000\u0000\u01fe\u01ff\u0001\u0000"+
		"\u0000\u0000\u01ff/\u0001\u0000\u0000\u0000\u0200\u0201\u0005;\u0000\u0000"+
		"\u0201\u0212\u0005<\u0000\u0000\u0202\u0203\u0005;\u0000\u0000\u0203\u0204"+
		"\u0003,\u0016\u0000\u0204\u0205\u0005<\u0000\u0000\u0205\u0212\u0001\u0000"+
		"\u0000\u0000\u0206\u020a\u0005;\u0000\u0000\u0207\u0208\u0003.\u0017\u0000"+
		"\u0208\u0209\u0005Y\u0000\u0000\u0209\u020b\u0001\u0000\u0000\u0000\u020a"+
		"\u0207\u0001\u0000\u0000\u0000\u020b\u020c\u0001\u0000\u0000\u0000\u020c"+
		"\u020a\u0001\u0000\u0000\u0000\u020c\u020d\u0001\u0000\u0000\u0000\u020d"+
		"\u020e\u0001\u0000\u0000\u0000\u020e\u020f\u0003.\u0017\u0000\u020f\u0210"+
		"\u0005<\u0000\u0000\u0210\u0212\u0001\u0000\u0000\u0000\u0211\u0200\u0001"+
		"\u0000\u0000\u0000\u0211\u0202\u0001\u0000\u0000\u0000\u0211\u0206\u0001"+
		"\u0000\u0000\u0000\u02121\u0001\u0000\u0000\u0000\u0213\u0215\u00034\u001a"+
		"\u0000\u0214\u0213\u0001\u0000\u0000\u0000\u0215\u0216\u0001\u0000\u0000"+
		"\u0000\u0216\u0214\u0001\u0000\u0000\u0000\u0216\u0217\u0001\u0000\u0000"+
		"\u0000\u02173\u0001\u0000\u0000\u0000\u0218\u0219\u0005%\u0000\u0000\u0219"+
		"\u021a\u00036\u001b\u0000\u021a\u021c\u0005 \u0000\u0000\u021b\u021d\u0003"+
		"\u0002\u0001\u0000\u021c\u021b\u0001\u0000\u0000\u0000\u021c\u021d\u0001"+
		"\u0000\u0000\u0000\u021d5\u0001\u0000\u0000\u0000\u021e\u021f\u00038\u001c"+
		"\u0000\u021f\u0220\u0005Y\u0000\u0000\u0220\u0222\u0001\u0000\u0000\u0000"+
		"\u0221\u021e\u0001\u0000\u0000\u0000\u0222\u0225\u0001\u0000\u0000\u0000"+
		"\u0223\u0221\u0001\u0000\u0000\u0000\u0223\u0224\u0001\u0000\u0000\u0000"+
		"\u0224\u0226\u0001\u0000\u0000\u0000\u0225\u0223\u0001\u0000\u0000\u0000"+
		"\u0226\u0227\u00038\u001c\u0000\u02277\u0001\u0000\u0000\u0000\u0228\u022b"+
		"\u0003\u0018\f\u0000\u0229\u022b\u0003:\u001d\u0000\u022a\u0228\u0001"+
		"\u0000\u0000\u0000\u022a\u0229\u0001\u0000\u0000\u0000\u022b9\u0001\u0000"+
		"\u0000\u0000\u022c\u022d\u0003\u0018\f\u0000\u022d\u022e\u0005\\\u0000"+
		"\u0000\u022e\u022f\u0003\u0018\f\u0000\u022f\u0235\u0001\u0000\u0000\u0000"+
		"\u0230\u0231\u0003\u0018\f\u0000\u0231\u0232\u0005]\u0000\u0000\u0232"+
		"\u0233\u0003\u0018\f\u0000\u0233\u0235\u0001\u0000\u0000\u0000\u0234\u022c"+
		"\u0001\u0000\u0000\u0000\u0234\u0230\u0001\u0000\u0000\u0000\u0235;\u0001"+
		"\u0000\u0000\u0000\u0236\u0238\u0003>\u001f\u0000\u0237\u0236\u0001\u0000"+
		"\u0000\u0000\u0238\u0239\u0001\u0000\u0000\u0000\u0239\u0237\u0001\u0000"+
		"\u0000\u0000\u0239\u023a\u0001\u0000\u0000\u0000\u023a=\u0001\u0000\u0000"+
		"\u0000\u023b\u023c\u0005\r\u0000\u0000\u023c\u023d\u0003\u0018\f\u0000"+
		"\u023d\u023f\u0005 \u0000\u0000\u023e\u0240\u0003\u0002\u0001\u0000\u023f"+
		"\u023e\u0001\u0000\u0000\u0000\u023f\u0240\u0001\u0000\u0000\u0000\u0240"+
		"?\u0001\u0000\u0000\u0000\u0241\u0242\u0003B!\u0000\u0242\u0243\u0005"+
		"Y\u0000\u0000\u0243\u0245\u0001\u0000\u0000\u0000\u0244\u0241\u0001\u0000"+
		"\u0000\u0000\u0245\u0248\u0001\u0000\u0000\u0000\u0246\u0244\u0001\u0000"+
		"\u0000\u0000\u0246\u0247\u0001\u0000\u0000\u0000\u0247\u0249\u0001\u0000"+
		"\u0000\u0000\u0248\u0246\u0001\u0000\u0000\u0000\u0249\u024a\u0003B!\u0000"+
		"\u024aA\u0001\u0000\u0000\u0000\u024b\u024d\u0005\u001b\u0000\u0000\u024c"+
		"\u024b\u0001\u0000\u0000\u0000\u024c\u024d\u0001\u0000\u0000\u0000\u024d"+
		"\u024e\u0001\u0000\u0000\u0000\u024e\u024f\u0003\u0016\u000b\u0000\u024f"+
		"C\u0001\u0000\u0000\u0000MEJOVbtz\u0080\u0084\u008b\u0091\u0095\u0099"+
		"\u009d\u00a1\u00a6\u00aa\u00b1\u00b7\u00bd\u00c3\u00c7\u00ca\u00d0\u00d4"+
		"\u00d8\u00dc\u00df\u00e3\u00e8\u00f1\u00f5\u0100\u0108\u010d\u0112\u0114"+
		"\u0122\u012e\u0132\u013a\u013d\u0141\u0143\u014b\u0162\u0195\u0197\u01a1"+
		"\u01a6\u01a8\u01ae\u01b5\u01c1\u01cd\u01d7\u01dd\u01e0\u01e3\u01e7\u01ed"+
		"\u01ef\u01f2\u01f5\u01fb\u01fe\u020c\u0211\u0216\u021c\u0223\u022a\u0234"+
		"\u0239\u023f\u0246\u024c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}