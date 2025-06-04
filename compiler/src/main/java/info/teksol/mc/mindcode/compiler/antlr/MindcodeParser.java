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
		ALLOCATE=1, BEGIN=2, BREAK=3, CACHED=4, CASE=5, CONST=6, CONTINUE=7, DESCENDING=8, 
		DEF=9, DO=10, ELIF=11, ELSE=12, ELSEIF=13, ELSIF=14, END=15, EXTERNAL=16, 
		FALSE=17, GUARDED=18, FOR=19, HEAP=20, IF=21, IN=22, INLINE=23, LINKED=24, 
		LOOP=25, MODULE=26, NOINIT=27, NOINLINE=28, NULL=29, OUT=30, PARAM=31, 
		REF=32, REMOTE=33, REQUIRE=34, RETURN=35, STACK=36, THEN=37, TRUE=38, 
		VAR=39, VOID=40, VOLATILE=41, WHEN=42, WHILE=43, EQUAL=44, GREATER_THAN=45, 
		GREATER_THAN_EQUAL=46, LESS_THAN=47, LESS_THAN_EQUAL=48, NOT_EQUAL=49, 
		STRICT_EQUAL=50, STRICT_NOT_EQUAL=51, BITWISE_AND=52, BITWISE_NOT=53, 
		BITWISE_OR=54, BITWISE_XOR=55, BOOLEAN_AND=56, BOOLEAN_NOT=57, BOOLEAN_OR=58, 
		LOGICAL_AND=59, LOGICAL_NOT=60, LOGICAL_OR=61, SHIFT_LEFT=62, SHIFT_RIGHT=63, 
		USHIFT_RIGHT=64, LPAREN=65, RPAREN=66, LBRACKET=67, RBRACKET=68, DECREMENT=69, 
		DIV=70, IDIV=71, INCREMENT=72, MINUS=73, MOD=74, EMOD=75, MUL=76, PLUS=77, 
		POW=78, ASSIGN=79, ASSIGN_BITWISE_AND=80, ASSIGN_BITWISE_OR=81, ASSIGN_BITWISE_XOR=82, 
		ASSIGN_BOOLEAN_AND=83, ASSIGN_BOOLEAN_OR=84, ASSIGN_DIV=85, ASSIGN_IDIV=86, 
		ASSIGN_MINUS=87, ASSIGN_MOD=88, ASSIGN_EMOD=89, ASSIGN_MUL=90, ASSIGN_PLUS=91, 
		ASSIGN_POW=92, ASSIGN_SHIFT_LEFT=93, ASSIGN_SHIFT_RIGHT=94, ASSIGN_USHIFT_RIGHT=95, 
		AT=96, COLON=97, COMMA=98, DOLLAR=99, DOT=100, DOT2=101, DOT3=102, DOUBLEQUOTE=103, 
		QUESTION=104, SEMICOLON=105, IDENTIFIER=106, EXTIDENTIFIER=107, BUILTINIDENTIFIER=108, 
		KEYWORD=109, STRING=110, COLOR=111, NAMEDCOLOR=112, BINARY=113, HEXADECIMAL=114, 
		DECIMAL=115, FLOAT=116, CHAR=117, HASHDECLARE=118, HASHSET=119, FORMATTABLELITERAL=120, 
		RBRACE=121, COMMENTEDCOMMENT=122, ENHANCEDCOMMENT=123, DOC_COMMENT=124, 
		COMMENT=125, EMPTYCOMMENT=126, LINECOMMENT=127, NEWLINE=128, WHITESPACE=129, 
		UNKNOWN_CHAR=130, DIRECTIVEVALUE=131, DIRECTIVEASSIGN=132, DIRECTIVECOMMA=133, 
		DIRECTIVECOMMENT=134, DIRECTIVELINECOMMENT=135, DIRECTIVEWHITESPACE=136, 
		TEXT=137, ESCAPESEQUENCE=138, EMPTYPLACEHOLDER=139, INTERPOLATION=140, 
		VARIABLEPLACEHOLDER=141, ENDOFLINE=142, VARIABLE=143, FMTENDOFLINE=144;
	public static final int
		RULE_astModule = 0, RULE_astStatementList = 1, RULE_expressionList = 2, 
		RULE_identifierList = 3, RULE_statement = 4, RULE_declarationOrExpressionList = 5, 
		RULE_variableDeclaration = 6, RULE_declModifier = 7, RULE_typeSpec = 8, 
		RULE_variableSpecList = 9, RULE_variableSpecification = 10, RULE_valueList = 11, 
		RULE_lvalue = 12, RULE_expression = 13, RULE_formattableContents = 14, 
		RULE_formattablePlaceholder = 15, RULE_directive = 16, RULE_directiveValues = 17, 
		RULE_astDirectiveValue = 18, RULE_astKeywordOrBuiltin = 19, RULE_astKeywordOrBuiltinList = 20, 
		RULE_allocations = 21, RULE_astAllocation = 22, RULE_parameterList = 23, 
		RULE_astFunctionParameter = 24, RULE_astFunctionArgument = 25, RULE_astOptionalFunctionArgument = 26, 
		RULE_argumentList = 27, RULE_caseAlternatives = 28, RULE_astCaseAlternative = 29, 
		RULE_whenValueList = 30, RULE_whenValue = 31, RULE_astRange = 32, RULE_elsifBranches = 33, 
		RULE_elsifBranch = 34, RULE_iteratorsValuesGroups = 35, RULE_astIteratorsValuesGroup = 36, 
		RULE_iteratorGroup = 37, RULE_astIterator = 38;
	private static String[] makeRuleNames() {
		return new String[] {
			"astModule", "astStatementList", "expressionList", "identifierList", 
			"statement", "declarationOrExpressionList", "variableDeclaration", "declModifier", 
			"typeSpec", "variableSpecList", "variableSpecification", "valueList", 
			"lvalue", "expression", "formattableContents", "formattablePlaceholder", 
			"directive", "directiveValues", "astDirectiveValue", "astKeywordOrBuiltin", 
			"astKeywordOrBuiltinList", "allocations", "astAllocation", "parameterList", 
			"astFunctionParameter", "astFunctionArgument", "astOptionalFunctionArgument", 
			"argumentList", "caseAlternatives", "astCaseAlternative", "whenValueList", 
			"whenValue", "astRange", "elsifBranches", "elsifBranch", "iteratorsValuesGroups", 
			"astIteratorsValuesGroup", "iteratorGroup", "astIterator"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'begin'", "'break'", "'cached'", "'case'", "'const'", 
			"'continue'", "'descending'", "'def'", "'do'", "'elif'", "'else'", "'elseif'", 
			"'elsif'", "'end'", "'external'", "'false'", "'guarded'", "'for'", "'heap'", 
			"'if'", "'in'", "'inline'", "'linked'", "'loop'", "'module'", "'noinit'", 
			"'noinline'", "'null'", "'out'", "'param'", "'ref'", "'remote'", "'require'", 
			"'return'", "'stack'", "'then'", "'true'", "'var'", "'void'", "'volatile'", 
			"'when'", "'while'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'==='", 
			"'!=='", "'&'", "'~'", "'|'", "'^'", "'&&'", "'!'", "'||'", "'and'", 
			"'not'", "'or'", "'<<'", "'>>'", "'>>>'", "'('", "')'", "'['", "']'", 
			"'--'", "'/'", "'\\'", "'++'", "'-'", "'%'", "'%%'", "'*'", "'+'", "'**'", 
			null, "'&='", "'|='", "'^='", "'&&='", "'||='", "'/='", "'\\='", "'-='", 
			"'%='", "'%%='", "'*='", "'+='", "'**='", "'<<='", "'>>='", "'>>>='", 
			"'@'", "':'", null, null, "'.'", "'..'", "'...'", "'\"'", "'?'", null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'#declare'", "'#set'", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"'${'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BEGIN", "BREAK", "CACHED", "CASE", "CONST", "CONTINUE", 
			"DESCENDING", "DEF", "DO", "ELIF", "ELSE", "ELSEIF", "ELSIF", "END", 
			"EXTERNAL", "FALSE", "GUARDED", "FOR", "HEAP", "IF", "IN", "INLINE", 
			"LINKED", "LOOP", "MODULE", "NOINIT", "NOINLINE", "NULL", "OUT", "PARAM", 
			"REF", "REMOTE", "REQUIRE", "RETURN", "STACK", "THEN", "TRUE", "VAR", 
			"VOID", "VOLATILE", "WHEN", "WHILE", "EQUAL", "GREATER_THAN", "GREATER_THAN_EQUAL", 
			"LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", 
			"BITWISE_AND", "BITWISE_NOT", "BITWISE_OR", "BITWISE_XOR", "BOOLEAN_AND", 
			"BOOLEAN_NOT", "BOOLEAN_OR", "LOGICAL_AND", "LOGICAL_NOT", "LOGICAL_OR", 
			"SHIFT_LEFT", "SHIFT_RIGHT", "USHIFT_RIGHT", "LPAREN", "RPAREN", "LBRACKET", 
			"RBRACKET", "DECREMENT", "DIV", "IDIV", "INCREMENT", "MINUS", "MOD", 
			"EMOD", "MUL", "PLUS", "POW", "ASSIGN", "ASSIGN_BITWISE_AND", "ASSIGN_BITWISE_OR", 
			"ASSIGN_BITWISE_XOR", "ASSIGN_BOOLEAN_AND", "ASSIGN_BOOLEAN_OR", "ASSIGN_DIV", 
			"ASSIGN_IDIV", "ASSIGN_MINUS", "ASSIGN_MOD", "ASSIGN_EMOD", "ASSIGN_MUL", 
			"ASSIGN_PLUS", "ASSIGN_POW", "ASSIGN_SHIFT_LEFT", "ASSIGN_SHIFT_RIGHT", 
			"ASSIGN_USHIFT_RIGHT", "AT", "COLON", "COMMA", "DOLLAR", "DOT", "DOT2", 
			"DOT3", "DOUBLEQUOTE", "QUESTION", "SEMICOLON", "IDENTIFIER", "EXTIDENTIFIER", 
			"BUILTINIDENTIFIER", "KEYWORD", "STRING", "COLOR", "NAMEDCOLOR", "BINARY", 
			"HEXADECIMAL", "DECIMAL", "FLOAT", "CHAR", "HASHDECLARE", "HASHSET", 
			"FORMATTABLELITERAL", "RBRACE", "COMMENTEDCOMMENT", "ENHANCEDCOMMENT", 
			"DOC_COMMENT", "COMMENT", "EMPTYCOMMENT", "LINECOMMENT", "NEWLINE", "WHITESPACE", 
			"UNKNOWN_CHAR", "DIRECTIVEVALUE", "DIRECTIVEASSIGN", "DIRECTIVECOMMA", 
			"DIRECTIVECOMMENT", "DIRECTIVELINECOMMENT", "DIRECTIVEWHITESPACE", "TEXT", 
			"ESCAPESEQUENCE", "EMPTYPLACEHOLDER", "INTERPOLATION", "VARIABLEPLACEHOLDER", 
			"ENDOFLINE", "VARIABLE", "FMTENDOFLINE"
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
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306056874511009534L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 360286870678016401L) != 0)) {
				{
				setState(78);
				astStatementList();
				}
			}

			setState(81);
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
			setState(87); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(84);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306056874511009534L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 360285771166388625L) != 0)) {
						{
						setState(83);
						statement();
						}
					}

					setState(86);
					match(SEMICOLON);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(89); 
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
			setState(96);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(91);
					expression(0);
					setState(92);
					match(COMMA);
					}
					} 
				}
				setState(98);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(99);
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
	public static class IdentifierListContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(MindcodeParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(MindcodeParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public IdentifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitIdentifierList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitIdentifierList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierListContext identifierList() throws RecognitionException {
		IdentifierListContext _localctx = new IdentifierListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_identifierList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(101);
					match(IDENTIFIER);
					setState(102);
					match(COMMA);
					}
					} 
				}
				setState(107);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(108);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
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
		public IteratorsValuesGroupsContext iterators;
		public AstStatementListContext body;
		public TerminalNode FOR() { return getToken(MindcodeParser.FOR, 0); }
		public TerminalNode DO() { return getToken(MindcodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public IteratorsValuesGroupsContext iteratorsValuesGroups() {
			return getRuleContext(IteratorsValuesGroupsContext.class,0);
		}
		public TerminalNode COLON() { return getToken(MindcodeParser.COLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
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
		public TerminalNode DESCENDING() { return getToken(MindcodeParser.DESCENDING, 0); }
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
		public Token callType;
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
		public TerminalNode REMOTE() { return getToken(MindcodeParser.REMOTE, 0); }
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
	public static class AstModuleDeclarationContext extends StatementContext {
		public Token name;
		public TerminalNode MODULE() { return getToken(MindcodeParser.MODULE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstModuleDeclarationContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstModuleDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstModuleDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstModuleDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstRequireLibraryContext extends StatementContext {
		public Token library;
		public IdentifierListContext processors;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode REMOTE() { return getToken(MindcodeParser.REMOTE, 0); }
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
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
		public IdentifierListContext processors;
		public TerminalNode REQUIRE() { return getToken(MindcodeParser.REQUIRE, 0); }
		public TerminalNode STRING() { return getToken(MindcodeParser.STRING, 0); }
		public TerminalNode REMOTE() { return getToken(MindcodeParser.REMOTE, 0); }
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
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
		enterRule(_localctx, 8, RULE_statement);
		int _la;
		try {
			setState(246);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				_localctx = new AstExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(110);
				expression(0);
				}
				break;
			case 2:
				_localctx = new AstDirectiveContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				directive();
				}
				break;
			case 3:
				_localctx = new AstVariableDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(112);
				variableDeclaration();
				}
				break;
			case 4:
				_localctx = new AstModuleDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(113);
				match(MODULE);
				setState(114);
				((AstModuleDeclarationContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 5:
				_localctx = new AstEnhancedCommentContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(115);
				match(ENHANCEDCOMMENT);
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 137)) & ~0x3f) == 0 && ((1L << (_la - 137)) & 31L) != 0)) {
					{
					{
					setState(116);
					formattableContents();
					}
					}
					setState(121);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 6:
				_localctx = new AstAllocationsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(122);
				match(ALLOCATE);
				setState(123);
				allocations();
				}
				break;
			case 7:
				_localctx = new AstParameterContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(124);
				match(PARAM);
				setState(125);
				((AstParameterContext)_localctx).name = match(IDENTIFIER);
				setState(126);
				match(ASSIGN);
				setState(127);
				((AstParameterContext)_localctx).value = expression(0);
				}
				break;
			case 8:
				_localctx = new AstRequireFileContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(128);
				match(REQUIRE);
				setState(129);
				((AstRequireFileContext)_localctx).file = match(STRING);
				setState(132);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REMOTE) {
					{
					setState(130);
					match(REMOTE);
					setState(131);
					((AstRequireFileContext)_localctx).processors = identifierList();
					}
				}

				}
				break;
			case 9:
				_localctx = new AstRequireLibraryContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(134);
				match(REQUIRE);
				setState(135);
				((AstRequireLibraryContext)_localctx).library = match(IDENTIFIER);
				setState(138);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REMOTE) {
					{
					setState(136);
					match(REMOTE);
					setState(137);
					((AstRequireLibraryContext)_localctx).processors = identifierList();
					}
				}

				}
				break;
			case 10:
				_localctx = new AstFunctionDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8866758656L) != 0)) {
					{
					setState(140);
					((AstFunctionDeclarationContext)_localctx).callType = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8866758656L) != 0)) ) {
						((AstFunctionDeclarationContext)_localctx).callType = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(143);
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
				setState(144);
				((AstFunctionDeclarationContext)_localctx).name = match(IDENTIFIER);
				setState(145);
				((AstFunctionDeclarationContext)_localctx).params = parameterList();
				setState(147);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(146);
					((AstFunctionDeclarationContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(149);
				match(END);
				}
				break;
			case 11:
				_localctx = new AstForEachLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(151);
					((AstForEachLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(152);
					match(COLON);
					}
				}

				setState(155);
				match(FOR);
				setState(156);
				((AstForEachLoopStatementContext)_localctx).iterators = iteratorsValuesGroups();
				setState(157);
				match(DO);
				setState(159);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(158);
					((AstForEachLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(161);
				match(END);
				}
				break;
			case 12:
				_localctx = new AstIteratedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(163);
					((AstIteratedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(164);
					match(COLON);
					}
				}

				setState(167);
				match(FOR);
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306046924874809456L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 45033797250453905L) != 0)) {
					{
					setState(168);
					((AstIteratedForLoopStatementContext)_localctx).init = declarationOrExpressionList();
					}
				}

				setState(171);
				match(SEMICOLON);
				setState(173);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306044167354482720L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 45033797250453905L) != 0)) {
					{
					setState(172);
					((AstIteratedForLoopStatementContext)_localctx).condition = expression(0);
					}
				}

				setState(175);
				match(SEMICOLON);
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306044167354482720L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 45033797250453905L) != 0)) {
					{
					setState(176);
					((AstIteratedForLoopStatementContext)_localctx).update = expressionList();
					}
				}

				setState(179);
				match(DO);
				setState(181);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(180);
					((AstIteratedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(183);
				match(END);
				}
				break;
			case 13:
				_localctx = new AstRangedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(186);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(184);
					((AstRangedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(185);
					match(COLON);
					}
				}

				setState(188);
				match(FOR);
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR) {
					{
					setState(189);
					((AstRangedForLoopStatementContext)_localctx).type = typeSpec();
					}
				}

				setState(192);
				((AstRangedForLoopStatementContext)_localctx).control = lvalue();
				setState(193);
				match(IN);
				setState(194);
				((AstRangedForLoopStatementContext)_localctx).range = astRange();
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DESCENDING) {
					{
					setState(195);
					match(DESCENDING);
					}
				}

				setState(198);
				match(DO);
				setState(200);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(199);
					((AstRangedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(202);
				match(END);
				}
				break;
			case 14:
				_localctx = new AstWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(206);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(204);
					((AstWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(205);
					match(COLON);
					}
				}

				setState(208);
				match(WHILE);
				setState(209);
				((AstWhileLoopStatementContext)_localctx).condition = expression(0);
				setState(210);
				match(DO);
				setState(212);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
				case 1:
					{
					setState(211);
					((AstWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(214);
				match(END);
				}
				break;
			case 15:
				_localctx = new AstDoWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(216);
					((AstDoWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(217);
					match(COLON);
					}
				}

				setState(220);
				match(DO);
				setState(222);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
				case 1:
					{
					setState(221);
					((AstDoWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(225);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOOP) {
					{
					setState(224);
					((AstDoWhileLoopStatementContext)_localctx).loop = match(LOOP);
					}
				}

				setState(227);
				match(WHILE);
				setState(228);
				((AstDoWhileLoopStatementContext)_localctx).condition = expression(0);
				}
				break;
			case 16:
				_localctx = new AstBreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(229);
				match(BREAK);
				setState(231);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(230);
					((AstBreakStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 17:
				_localctx = new AstContinueStatementContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(233);
				match(CONTINUE);
				setState(235);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(234);
					((AstContinueStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 18:
				_localctx = new AstReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(237);
				match(RETURN);
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306044167354482720L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 45033797250453905L) != 0)) {
					{
					setState(238);
					((AstReturnStatementContext)_localctx).value = expression(0);
					}
				}

				}
				break;
			case 19:
				_localctx = new AstCodeBlockContext(_localctx);
				enterOuterAlt(_localctx, 19);
				{
				setState(241);
				match(BEGIN);
				setState(243);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(242);
					((AstCodeBlockContext)_localctx).exp = astStatementList();
					}
					break;
				}
				setState(245);
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
		enterRule(_localctx, 10, RULE_declarationOrExpressionList);
		try {
			setState(250);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CACHED:
			case CONST:
			case EXTERNAL:
			case GUARDED:
			case LINKED:
			case NOINIT:
			case REMOTE:
			case VAR:
			case VOLATILE:
				enterOuterAlt(_localctx, 1);
				{
				setState(248);
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
			case KEYWORD:
			case STRING:
			case COLOR:
			case NAMEDCOLOR:
			case BINARY:
			case HEXADECIMAL:
			case DECIMAL:
			case FLOAT:
			case CHAR:
			case FORMATTABLELITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(249);
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
		enterRule(_localctx, 12, RULE_variableDeclaration);
		int _la;
		try {
			setState(268);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2207764512848L) != 0)) {
					{
					{
					setState(252);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(257);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(258);
				((VariableDeclarationContext)_localctx).type = typeSpec();
				setState(259);
				((VariableDeclarationContext)_localctx).variables = variableSpecList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(262); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(261);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(264); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 2207764512848L) != 0) );
				setState(266);
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
		public Token memory;
		public ExpressionContext index;
		public AstRangeContext range;
		public TerminalNode CONST() { return getToken(MindcodeParser.CONST, 0); }
		public TerminalNode CACHED() { return getToken(MindcodeParser.CACHED, 0); }
		public TerminalNode EXTERNAL() { return getToken(MindcodeParser.EXTERNAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstRangeContext astRange() {
			return getRuleContext(AstRangeContext.class,0);
		}
		public TerminalNode GUARDED() { return getToken(MindcodeParser.GUARDED, 0); }
		public TerminalNode LINKED() { return getToken(MindcodeParser.LINKED, 0); }
		public TerminalNode NOINIT() { return getToken(MindcodeParser.NOINIT, 0); }
		public TerminalNode REMOTE() { return getToken(MindcodeParser.REMOTE, 0); }
		public TerminalNode VOLATILE() { return getToken(MindcodeParser.VOLATILE, 0); }
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
		enterRule(_localctx, 14, RULE_declModifier);
		try {
			setState(293);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(270);
				((DeclModifierContext)_localctx).modifier = match(CONST);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(271);
				((DeclModifierContext)_localctx).modifier = match(CACHED);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(272);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(274);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
				case 1:
					{
					setState(273);
					((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(276);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(277);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(278);
				match(LBRACKET);
				setState(279);
				((DeclModifierContext)_localctx).index = expression(0);
				setState(280);
				match(RBRACKET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(282);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(283);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(284);
				match(LBRACKET);
				setState(285);
				((DeclModifierContext)_localctx).range = astRange();
				setState(286);
				match(RBRACKET);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(288);
				((DeclModifierContext)_localctx).modifier = match(GUARDED);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(289);
				((DeclModifierContext)_localctx).modifier = match(LINKED);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(290);
				((DeclModifierContext)_localctx).modifier = match(NOINIT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(291);
				((DeclModifierContext)_localctx).modifier = match(REMOTE);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(292);
				((DeclModifierContext)_localctx).modifier = match(VOLATILE);
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
		enterRule(_localctx, 16, RULE_typeSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
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
		enterRule(_localctx, 18, RULE_variableSpecList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(297);
					variableSpecification();
					setState(298);
					match(COMMA);
					}
					} 
				}
				setState(304);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			}
			setState(305);
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
		enterRule(_localctx, 20, RULE_variableSpecification);
		int _la;
		try {
			setState(322);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(307);
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
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(308);
					match(ASSIGN);
					setState(309);
					((VariableSpecificationContext)_localctx).exp = expression(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(312);
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
				setState(313);
				match(LBRACKET);
				setState(315);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306044167354482720L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 45033797250453905L) != 0)) {
					{
					setState(314);
					((VariableSpecificationContext)_localctx).length = expression(0);
					}
				}

				setState(317);
				match(RBRACKET);
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(318);
					match(ASSIGN);
					setState(319);
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
		enterRule(_localctx, 22, RULE_valueList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
			match(LPAREN);
			setState(325);
			((ValueListContext)_localctx).values = expressionList();
			setState(326);
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
	public static class AstSubarrayContext extends LvalueContext {
		public Token array;
		public AstRangeContext range;
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public AstRangeContext astRange() {
			return getRuleContext(AstRangeContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode EXTIDENTIFIER() { return getToken(MindcodeParser.EXTIDENTIFIER, 0); }
		public AstSubarrayContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstSubarray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstSubarray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstSubarray(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstRemoteSubarrayContext extends LvalueContext {
		public Token processor;
		public Token array;
		public AstRangeContext range;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(MindcodeParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(MindcodeParser.IDENTIFIER, i);
		}
		public AstRangeContext astRange() {
			return getRuleContext(AstRangeContext.class,0);
		}
		public AstRemoteSubarrayContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstRemoteSubarray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstRemoteSubarray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstRemoteSubarray(this);
			else return visitor.visitChildren(this);
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
	public static class AstRemoteArrayContext extends LvalueContext {
		public Token processor;
		public Token array;
		public ExpressionContext index;
		public TerminalNode DOT() { return getToken(MindcodeParser.DOT, 0); }
		public TerminalNode LBRACKET() { return getToken(MindcodeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(MindcodeParser.RBRACKET, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(MindcodeParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(MindcodeParser.IDENTIFIER, i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AstRemoteArrayContext(LvalueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstRemoteArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstRemoteArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstRemoteArray(this);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode EXTIDENTIFIER() { return getToken(MindcodeParser.EXTIDENTIFIER, 0); }
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
		enterRule(_localctx, 24, RULE_lvalue);
		int _la;
		try {
			setState(355);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				_localctx = new AstIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(328);
				((AstIdentifierContext)_localctx).id = match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new AstIdentifierExtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(329);
				((AstIdentifierExtContext)_localctx).id = match(EXTIDENTIFIER);
				}
				break;
			case 3:
				_localctx = new AstBuiltInIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(330);
				((AstBuiltInIdentifierContext)_localctx).builtin = match(BUILTINIDENTIFIER);
				}
				break;
			case 4:
				_localctx = new AstArrayAccessContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(331);
				((AstArrayAccessContext)_localctx).array = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==IDENTIFIER || _la==EXTIDENTIFIER) ) {
					((AstArrayAccessContext)_localctx).array = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(332);
				match(LBRACKET);
				setState(333);
				((AstArrayAccessContext)_localctx).index = expression(0);
				setState(334);
				match(RBRACKET);
				}
				break;
			case 5:
				_localctx = new AstSubarrayContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(336);
				((AstSubarrayContext)_localctx).array = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==IDENTIFIER || _la==EXTIDENTIFIER) ) {
					((AstSubarrayContext)_localctx).array = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(337);
				match(LBRACKET);
				setState(338);
				((AstSubarrayContext)_localctx).range = astRange();
				setState(339);
				match(RBRACKET);
				}
				break;
			case 6:
				_localctx = new AstRemoteArrayContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(341);
				((AstRemoteArrayContext)_localctx).processor = match(IDENTIFIER);
				setState(342);
				match(DOT);
				setState(343);
				((AstRemoteArrayContext)_localctx).array = match(IDENTIFIER);
				setState(344);
				match(LBRACKET);
				setState(345);
				((AstRemoteArrayContext)_localctx).index = expression(0);
				setState(346);
				match(RBRACKET);
				}
				break;
			case 7:
				_localctx = new AstRemoteSubarrayContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(348);
				((AstRemoteSubarrayContext)_localctx).processor = match(IDENTIFIER);
				setState(349);
				match(DOT);
				setState(350);
				((AstRemoteSubarrayContext)_localctx).array = match(IDENTIFIER);
				setState(351);
				match(LBRACKET);
				setState(352);
				((AstRemoteSubarrayContext)_localctx).range = astRange();
				setState(353);
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
	public static class AstKeywordContext extends ExpressionContext {
		public TerminalNode KEYWORD() { return getToken(MindcodeParser.KEYWORD, 0); }
		public AstKeywordContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstKeyword(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstKeyword(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstLiteralNamedColorContext extends ExpressionContext {
		public TerminalNode NAMEDCOLOR() { return getToken(MindcodeParser.NAMEDCOLOR, 0); }
		public AstLiteralNamedColorContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralNamedColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralNamedColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralNamedColor(this);
			else return visitor.visitChildren(this);
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
		public TerminalNode EMOD() { return getToken(MindcodeParser.EMOD, 0); }
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
	public static class AstLiteralCharContext extends ExpressionContext {
		public TerminalNode CHAR() { return getToken(MindcodeParser.CHAR, 0); }
		public AstLiteralCharContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstLiteralChar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstLiteralChar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstLiteralChar(this);
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
		public TerminalNode USHIFT_RIGHT() { return getToken(MindcodeParser.USHIFT_RIGHT, 0); }
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
		public TerminalNode ASSIGN_EMOD() { return getToken(MindcodeParser.ASSIGN_EMOD, 0); }
		public TerminalNode ASSIGN_PLUS() { return getToken(MindcodeParser.ASSIGN_PLUS, 0); }
		public TerminalNode ASSIGN_MINUS() { return getToken(MindcodeParser.ASSIGN_MINUS, 0); }
		public TerminalNode ASSIGN_SHIFT_LEFT() { return getToken(MindcodeParser.ASSIGN_SHIFT_LEFT, 0); }
		public TerminalNode ASSIGN_SHIFT_RIGHT() { return getToken(MindcodeParser.ASSIGN_SHIFT_RIGHT, 0); }
		public TerminalNode ASSIGN_USHIFT_RIGHT() { return getToken(MindcodeParser.ASSIGN_USHIFT_RIGHT, 0); }
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
		int _startState = 26;
		enterRecursionRule(_localctx, 26, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				{
				_localctx = new ExpLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(358);
				lvalue();
				}
				break;
			case 2:
				{
				_localctx = new AstKeywordContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(359);
				match(KEYWORD);
				}
				break;
			case 3:
				{
				_localctx = new AstFunctionCallEndContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(360);
				match(END);
				setState(361);
				match(LPAREN);
				setState(362);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new AstFunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(363);
				((AstFunctionCallContext)_localctx).function = match(IDENTIFIER);
				setState(364);
				((AstFunctionCallContext)_localctx).args = argumentList();
				}
				break;
			case 5:
				{
				_localctx = new AstCaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(365);
				match(CASE);
				setState(366);
				((AstCaseExpressionContext)_localctx).exp = expression(0);
				setState(368);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHEN) {
					{
					setState(367);
					((AstCaseExpressionContext)_localctx).alternatives = caseAlternatives();
					}
				}

				setState(372);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(370);
					match(ELSE);
					setState(371);
					((AstCaseExpressionContext)_localctx).elseBranch = astStatementList();
					}
				}

				setState(374);
				match(END);
				}
				break;
			case 6:
				{
				_localctx = new AstIfExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(376);
				match(IF);
				setState(377);
				((AstIfExpressionContext)_localctx).condition = expression(0);
				setState(378);
				match(THEN);
				setState(380);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(379);
					((AstIfExpressionContext)_localctx).trueBranch = astStatementList();
					}
					break;
				}
				setState(383);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSIF) {
					{
					setState(382);
					((AstIfExpressionContext)_localctx).elsif = elsifBranches();
					}
				}

				setState(389);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(385);
					match(ELSE);
					setState(387);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
					case 1:
						{
						setState(386);
						((AstIfExpressionContext)_localctx).falseBranch = astStatementList();
						}
						break;
					}
					}
				}

				setState(391);
				match(END);
				}
				break;
			case 7:
				{
				_localctx = new AstFormattableLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(393);
				match(FORMATTABLELITERAL);
				setState(397);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 137)) & ~0x3f) == 0 && ((1L << (_la - 137)) & 31L) != 0)) {
					{
					{
					setState(394);
					formattableContents();
					}
					}
					setState(399);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(400);
				match(DOUBLEQUOTE);
				}
				break;
			case 8:
				{
				_localctx = new AstLiteralStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(401);
				match(STRING);
				}
				break;
			case 9:
				{
				_localctx = new AstLiteralColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(402);
				match(COLOR);
				}
				break;
			case 10:
				{
				_localctx = new AstLiteralNamedColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(403);
				match(NAMEDCOLOR);
				}
				break;
			case 11:
				{
				_localctx = new AstLiteralBinaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(404);
				match(BINARY);
				}
				break;
			case 12:
				{
				_localctx = new AstLiteralHexadecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(405);
				match(HEXADECIMAL);
				}
				break;
			case 13:
				{
				_localctx = new AstLiteralDecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(406);
				match(DECIMAL);
				}
				break;
			case 14:
				{
				_localctx = new AstLiteralFloatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(407);
				match(FLOAT);
				}
				break;
			case 15:
				{
				_localctx = new AstLiteralCharContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(408);
				match(CHAR);
				}
				break;
			case 16:
				{
				_localctx = new AstLiteralNullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(409);
				match(NULL);
				}
				break;
			case 17:
				{
				_localctx = new AstLiteralBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(410);
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
			case 18:
				{
				_localctx = new AstOperatorIncDecPostfixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(411);
				((AstOperatorIncDecPostfixContext)_localctx).exp = lvalue();
				setState(412);
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
			case 19:
				{
				_localctx = new AstOperatorIncDecPrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(414);
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
				setState(415);
				((AstOperatorIncDecPrefixContext)_localctx).exp = lvalue();
				}
				break;
			case 20:
				{
				_localctx = new AstOperatorUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(416);
				((AstOperatorUnaryContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 53)) & ~0x3f) == 0 && ((1L << (_la - 53)) & 17825937L) != 0)) ) {
					((AstOperatorUnaryContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(417);
				((AstOperatorUnaryContext)_localctx).exp = expression(14);
				}
				break;
			case 21:
				{
				_localctx = new AstParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(418);
				match(LPAREN);
				setState(419);
				((AstParenthesesContext)_localctx).exp = expression(0);
				setState(420);
				match(RPAREN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(475);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(473);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
					case 1:
						{
						_localctx = new AstOperatorBinaryExpContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryExpContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(424);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(425);
						((AstOperatorBinaryExpContext)_localctx).op = match(POW);
						setState(426);
						((AstOperatorBinaryExpContext)_localctx).right = expression(14);
						}
						break;
					case 2:
						{
						_localctx = new AstOperatorBinaryMulContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryMulContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(427);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(428);
						((AstOperatorBinaryMulContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & 115L) != 0)) ) {
							((AstOperatorBinaryMulContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(429);
						((AstOperatorBinaryMulContext)_localctx).right = expression(13);
						}
						break;
					case 3:
						{
						_localctx = new AstOperatorBinaryAddContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryAddContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(430);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(431);
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
						setState(432);
						((AstOperatorBinaryAddContext)_localctx).right = expression(12);
						}
						break;
					case 4:
						{
						_localctx = new AstOperatorBinaryShiftContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryShiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(433);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(434);
						((AstOperatorBinaryShiftContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 7L) != 0)) ) {
							((AstOperatorBinaryShiftContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(435);
						((AstOperatorBinaryShiftContext)_localctx).right = expression(11);
						}
						break;
					case 5:
						{
						_localctx = new AstOperatorBinaryBitwiseAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(436);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(437);
						((AstOperatorBinaryBitwiseAndContext)_localctx).op = match(BITWISE_AND);
						setState(438);
						((AstOperatorBinaryBitwiseAndContext)_localctx).right = expression(10);
						}
						break;
					case 6:
						{
						_localctx = new AstOperatorBinaryBitwiseOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(439);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(440);
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
						setState(441);
						((AstOperatorBinaryBitwiseOrContext)_localctx).right = expression(9);
						}
						break;
					case 7:
						{
						_localctx = new AstOperatorBinaryInequalityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryInequalityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(442);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(443);
						((AstOperatorBinaryInequalityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 527765581332480L) != 0)) ) {
							((AstOperatorBinaryInequalityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(444);
						((AstOperatorBinaryInequalityContext)_localctx).right = expression(8);
						}
						break;
					case 8:
						{
						_localctx = new AstOperatorBinaryEqualityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryEqualityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(445);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(446);
						((AstOperatorBinaryEqualityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 3958241859993600L) != 0)) ) {
							((AstOperatorBinaryEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(447);
						((AstOperatorBinaryEqualityContext)_localctx).right = expression(7);
						}
						break;
					case 9:
						{
						_localctx = new AstOperatorBinaryLogicalAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryLogicalAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(448);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(449);
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
						setState(450);
						((AstOperatorBinaryLogicalAndContext)_localctx).right = expression(6);
						}
						break;
					case 10:
						{
						_localctx = new AstOperatorBinaryLogicalOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryLogicalOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(451);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(452);
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
						setState(453);
						((AstOperatorBinaryLogicalOrContext)_localctx).right = expression(5);
						}
						break;
					case 11:
						{
						_localctx = new AstOperatorTernaryContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorTernaryContext)_localctx).condition = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(454);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(455);
						match(QUESTION);
						setState(456);
						((AstOperatorTernaryContext)_localctx).trueBranch = expression(0);
						setState(457);
						match(COLON);
						setState(458);
						((AstOperatorTernaryContext)_localctx).falseBranch = expression(3);
						}
						break;
					case 12:
						{
						_localctx = new AstAssignmentContext(new ExpressionContext(_parentctx, _parentState));
						((AstAssignmentContext)_localctx).target = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(460);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(461);
						((AstAssignmentContext)_localctx).operation = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 79)) & ~0x3f) == 0 && ((1L << (_la - 79)) & 131071L) != 0)) ) {
							((AstAssignmentContext)_localctx).operation = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(462);
						((AstAssignmentContext)_localctx).value = expression(2);
						}
						break;
					case 13:
						{
						_localctx = new AstMethodCallContext(new ExpressionContext(_parentctx, _parentState));
						((AstMethodCallContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(463);
						if (!(precpred(_ctx, 32))) throw new FailedPredicateException(this, "precpred(_ctx, 32)");
						setState(464);
						match(DOT);
						setState(465);
						((AstMethodCallContext)_localctx).function = match(IDENTIFIER);
						setState(466);
						((AstMethodCallContext)_localctx).args = argumentList();
						}
						break;
					case 14:
						{
						_localctx = new AstMemberAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstMemberAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(467);
						if (!(precpred(_ctx, 31))) throw new FailedPredicateException(this, "precpred(_ctx, 31)");
						setState(468);
						match(DOT);
						setState(469);
						((AstMemberAccessContext)_localctx).member = match(IDENTIFIER);
						}
						break;
					case 15:
						{
						_localctx = new AstPropertyAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstPropertyAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(470);
						if (!(precpred(_ctx, 30))) throw new FailedPredicateException(this, "precpred(_ctx, 30)");
						setState(471);
						match(DOT);
						setState(472);
						((AstPropertyAccessContext)_localctx).property = match(BUILTINIDENTIFIER);
						}
						break;
					}
					} 
				}
				setState(477);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,52,_ctx);
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
		enterRule(_localctx, 28, RULE_formattableContents);
		try {
			setState(485);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT:
				_localctx = new FormattableTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(478);
				match(TEXT);
				}
				break;
			case ESCAPESEQUENCE:
				_localctx = new FormattableEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(479);
				match(ESCAPESEQUENCE);
				}
				break;
			case INTERPOLATION:
				_localctx = new FormattableInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(480);
				match(INTERPOLATION);
				setState(481);
				expression(0);
				setState(482);
				match(RBRACE);
				}
				break;
			case EMPTYPLACEHOLDER:
			case VARIABLEPLACEHOLDER:
				_localctx = new PlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(484);
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
		enterRule(_localctx, 30, RULE_formattablePlaceholder);
		int _la;
		try {
			setState(492);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EMPTYPLACEHOLDER:
				_localctx = new FormattablePlaceholderEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(487);
				match(EMPTYPLACEHOLDER);
				}
				break;
			case VARIABLEPLACEHOLDER:
				_localctx = new FormattablePlaceholderVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(488);
				match(VARIABLEPLACEHOLDER);
				setState(490);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VARIABLE) {
					{
					setState(489);
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
	@SuppressWarnings("CheckReturnValue")
	public static class AstDirectiveDeclareContext extends DirectiveContext {
		public Token category;
		public AstKeywordOrBuiltinListContext elements;
		public TerminalNode HASHDECLARE() { return getToken(MindcodeParser.HASHDECLARE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstKeywordOrBuiltinListContext astKeywordOrBuiltinList() {
			return getRuleContext(AstKeywordOrBuiltinListContext.class,0);
		}
		public AstDirectiveDeclareContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDirectiveDeclare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDirectiveDeclare(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDirectiveDeclare(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_directive);
		int _la;
		try {
			setState(503);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HASHSET:
				_localctx = new AstDirectiveSetContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(494);
				match(HASHSET);
				setState(495);
				((AstDirectiveSetContext)_localctx).option = astDirectiveValue();
				setState(498);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DIRECTIVEASSIGN) {
					{
					setState(496);
					match(DIRECTIVEASSIGN);
					setState(497);
					((AstDirectiveSetContext)_localctx).value = directiveValues();
					}
				}

				}
				break;
			case HASHDECLARE:
				_localctx = new AstDirectiveDeclareContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(500);
				match(HASHDECLARE);
				setState(501);
				((AstDirectiveDeclareContext)_localctx).category = match(IDENTIFIER);
				setState(502);
				((AstDirectiveDeclareContext)_localctx).elements = astKeywordOrBuiltinList();
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
		enterRule(_localctx, 34, RULE_directiveValues);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(510);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(505);
					astDirectiveValue();
					setState(506);
					match(DIRECTIVECOMMA);
					}
					} 
				}
				setState(512);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			}
			setState(513);
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
		enterRule(_localctx, 36, RULE_astDirectiveValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515);
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
	public static class AstKeywordOrBuiltinContext extends ParserRuleContext {
		public TerminalNode KEYWORD() { return getToken(MindcodeParser.KEYWORD, 0); }
		public TerminalNode BUILTINIDENTIFIER() { return getToken(MindcodeParser.BUILTINIDENTIFIER, 0); }
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public AstKeywordOrBuiltinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astKeywordOrBuiltin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstKeywordOrBuiltin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstKeywordOrBuiltin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstKeywordOrBuiltin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstKeywordOrBuiltinContext astKeywordOrBuiltin() throws RecognitionException {
		AstKeywordOrBuiltinContext _localctx = new AstKeywordOrBuiltinContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_astKeywordOrBuiltin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(517);
			_la = _input.LA(1);
			if ( !(((((_la - 106)) & ~0x3f) == 0 && ((1L << (_la - 106)) & 13L) != 0)) ) {
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

	@SuppressWarnings("CheckReturnValue")
	public static class AstKeywordOrBuiltinListContext extends ParserRuleContext {
		public List<AstKeywordOrBuiltinContext> astKeywordOrBuiltin() {
			return getRuleContexts(AstKeywordOrBuiltinContext.class);
		}
		public AstKeywordOrBuiltinContext astKeywordOrBuiltin(int i) {
			return getRuleContext(AstKeywordOrBuiltinContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public AstKeywordOrBuiltinListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astKeywordOrBuiltinList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstKeywordOrBuiltinList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstKeywordOrBuiltinList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstKeywordOrBuiltinList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstKeywordOrBuiltinListContext astKeywordOrBuiltinList() throws RecognitionException {
		AstKeywordOrBuiltinListContext _localctx = new AstKeywordOrBuiltinListContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_astKeywordOrBuiltinList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(519);
					astKeywordOrBuiltin();
					setState(520);
					match(COMMA);
					}
					} 
				}
				setState(526);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
			}
			setState(527);
			astKeywordOrBuiltin();
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 42, RULE_allocations);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(534);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(529);
					astAllocation();
					setState(530);
					match(COMMA);
					}
					} 
				}
				setState(536);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
			}
			setState(537);
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
		enterRule(_localctx, 44, RULE_astAllocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(539);
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
			setState(540);
			match(IN);
			setState(541);
			((AstAllocationContext)_localctx).id = match(IDENTIFIER);
			setState(546);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACKET) {
				{
				setState(542);
				match(LBRACKET);
				setState(543);
				((AstAllocationContext)_localctx).range = astRange();
				setState(544);
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
		enterRule(_localctx, 46, RULE_parameterList);
		try {
			int _alt;
			setState(562);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(548);
				match(LPAREN);
				setState(549);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(550);
				match(LPAREN);
				setState(556);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(551);
						astFunctionParameter();
						setState(552);
						match(COMMA);
						}
						} 
					}
					setState(558);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
				}
				setState(559);
				astFunctionParameter();
				setState(560);
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
		public Token modifier_ref;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public TerminalNode DOT3() { return getToken(MindcodeParser.DOT3, 0); }
		public TerminalNode REF() { return getToken(MindcodeParser.REF, 0); }
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
		enterRule(_localctx, 48, RULE_astFunctionParameter);
		int _la;
		try {
			setState(585);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(565);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(564);
					((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(568);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(567);
					((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(570);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(572);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(571);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(574);
				((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
				setState(575);
				((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
				setState(576);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(578);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(577);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(580);
				((AstFunctionParameterContext)_localctx).modifier_ref = match(REF);
				setState(581);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(583);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(582);
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
		public Token modifier_ref;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public TerminalNode REF() { return getToken(MindcodeParser.REF, 0); }
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
		enterRule(_localctx, 50, RULE_astFunctionArgument);
		int _la;
		try {
			setState(599);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(588);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(587);
					((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(591);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(590);
					((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(593);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(594);
				((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
				setState(595);
				((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
				setState(596);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(597);
				((AstFunctionArgumentContext)_localctx).modifier_ref = match(REF);
				setState(598);
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
		enterRule(_localctx, 52, RULE_astOptionalFunctionArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(602);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1306044172727386144L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 45033797250453905L) != 0)) {
				{
				setState(601);
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
		enterRule(_localctx, 54, RULE_argumentList);
		try {
			int _alt;
			setState(621);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(604);
				match(LPAREN);
				setState(605);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(606);
				match(LPAREN);
				setState(607);
				astFunctionArgument();
				setState(608);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(610);
				match(LPAREN);
				setState(614); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(611);
						astOptionalFunctionArgument();
						setState(612);
						match(COMMA);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(616); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,74,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(618);
				astOptionalFunctionArgument();
				setState(619);
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
		enterRule(_localctx, 56, RULE_caseAlternatives);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(624); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(623);
				astCaseAlternative();
				}
				}
				setState(626); 
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
		enterRule(_localctx, 58, RULE_astCaseAlternative);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(628);
			match(WHEN);
			setState(629);
			((AstCaseAlternativeContext)_localctx).values = whenValueList();
			setState(630);
			match(THEN);
			setState(632);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				{
				setState(631);
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
		enterRule(_localctx, 60, RULE_whenValueList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(639);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(634);
					whenValue();
					setState(635);
					match(COMMA);
					}
					} 
				}
				setState(641);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			}
			setState(642);
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
		enterRule(_localctx, 62, RULE_whenValue);
		try {
			setState(646);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(644);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(645);
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
		enterRule(_localctx, 64, RULE_astRange);
		try {
			setState(656);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(648);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(649);
				((AstRangeContext)_localctx).operator = match(DOT2);
				setState(650);
				((AstRangeContext)_localctx).lastValue = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(652);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(653);
				((AstRangeContext)_localctx).operator = match(DOT3);
				setState(654);
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
		enterRule(_localctx, 66, RULE_elsifBranches);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(658);
				elsifBranch();
				}
				}
				setState(661); 
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
		enterRule(_localctx, 68, RULE_elsifBranch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(663);
			match(ELSIF);
			setState(664);
			((ElsifBranchContext)_localctx).condition = expression(0);
			setState(665);
			match(THEN);
			setState(667);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(666);
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
	public static class IteratorsValuesGroupsContext extends ParserRuleContext {
		public List<AstIteratorsValuesGroupContext> astIteratorsValuesGroup() {
			return getRuleContexts(AstIteratorsValuesGroupContext.class);
		}
		public AstIteratorsValuesGroupContext astIteratorsValuesGroup(int i) {
			return getRuleContext(AstIteratorsValuesGroupContext.class,i);
		}
		public List<TerminalNode> SEMICOLON() { return getTokens(MindcodeParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(MindcodeParser.SEMICOLON, i);
		}
		public IteratorsValuesGroupsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iteratorsValuesGroups; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterIteratorsValuesGroups(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitIteratorsValuesGroups(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitIteratorsValuesGroups(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IteratorsValuesGroupsContext iteratorsValuesGroups() throws RecognitionException {
		IteratorsValuesGroupsContext _localctx = new IteratorsValuesGroupsContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_iteratorsValuesGroups);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(674);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(669);
					astIteratorsValuesGroup();
					setState(670);
					match(SEMICOLON);
					}
					} 
				}
				setState(676);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
			}
			setState(677);
			astIteratorsValuesGroup();
			}
		}
		catch (RecognitionException re) {
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
	public static class AstIteratorsValuesGroupContext extends ParserRuleContext {
		public IteratorGroupContext iteratorGroup() {
			return getRuleContext(IteratorGroupContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode DESCENDING() { return getToken(MindcodeParser.DESCENDING, 0); }
		public AstIteratorsValuesGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astIteratorsValuesGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstIteratorsValuesGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstIteratorsValuesGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstIteratorsValuesGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstIteratorsValuesGroupContext astIteratorsValuesGroup() throws RecognitionException {
		AstIteratorsValuesGroupContext _localctx = new AstIteratorsValuesGroupContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_astIteratorsValuesGroup);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(679);
			iteratorGroup();
			setState(680);
			match(IN);
			setState(681);
			expressionList();
			setState(683);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DESCENDING) {
				{
				setState(682);
				match(DESCENDING);
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
	public static class IteratorGroupContext extends ParserRuleContext {
		public TypeSpecContext type;
		public List<AstIteratorContext> astIterator() {
			return getRuleContexts(AstIteratorContext.class);
		}
		public AstIteratorContext astIterator(int i) {
			return getRuleContext(AstIteratorContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public TypeSpecContext typeSpec() {
			return getRuleContext(TypeSpecContext.class,0);
		}
		public IteratorGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iteratorGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterIteratorGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitIteratorGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitIteratorGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IteratorGroupContext iteratorGroup() throws RecognitionException {
		IteratorGroupContext _localctx = new IteratorGroupContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_iteratorGroup);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR) {
				{
				setState(685);
				((IteratorGroupContext)_localctx).type = typeSpec();
				}
			}

			setState(693);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(688);
					astIterator();
					setState(689);
					match(COMMA);
					}
					} 
				}
				setState(695);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
			}
			setState(696);
			astIterator();
			}
		}
		catch (RecognitionException re) {
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
	public static class AstIteratorContext extends ParserRuleContext {
		public Token modifier;
		public LvalueContext variable;
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public AstIteratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astIterator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstIterator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstIterator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstIterator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstIteratorContext astIterator() throws RecognitionException {
		AstIteratorContext _localctx = new AstIteratorContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_astIterator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(699);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OUT) {
				{
				setState(698);
				((AstIteratorContext)_localctx).modifier = match(OUT);
				}
			}

			setState(701);
			((AstIteratorContext)_localctx).variable = lvalue();
			}
		}
		catch (RecognitionException re) {
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
		case 13:
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
			return precpred(_ctx, 32);
		case 13:
			return precpred(_ctx, 31);
		case 14:
			return precpred(_ctx, 30);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0090\u02c0\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0001\u0000"+
		"\u0003\u0000P\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0003\u0001"+
		"U\b\u0001\u0001\u0001\u0004\u0001X\b\u0001\u000b\u0001\f\u0001Y\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0005\u0002_\b\u0002\n\u0002\f\u0002b\t"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0005\u0003h\b"+
		"\u0003\n\u0003\f\u0003k\t\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005"+
		"\u0004v\b\u0004\n\u0004\f\u0004y\t\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0003\u0004\u0085\b\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0003\u0004\u008b\b\u0004\u0001\u0004\u0003\u0004\u008e"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u0094"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u009a"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00a0"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00a6"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00aa\b\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004\u00ae\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"\u00b2\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00b6\b\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00bb\b\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004\u00bf\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004\u00c5\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00c9"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00cf"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00d5"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00db"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00df\b\u0004\u0001\u0004"+
		"\u0003\u0004\u00e2\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004\u00e8\b\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00ec\b"+
		"\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00f0\b\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004\u00f4\b\u0004\u0001\u0004\u0003\u0004\u00f7\b\u0004"+
		"\u0001\u0005\u0001\u0005\u0003\u0005\u00fb\b\u0005\u0001\u0006\u0005\u0006"+
		"\u00fe\b\u0006\n\u0006\f\u0006\u0101\t\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0004\u0006\u0107\b\u0006\u000b\u0006\f\u0006\u0108"+
		"\u0001\u0006\u0001\u0006\u0003\u0006\u010d\b\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0003\u0007\u0113\b\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0126\b\u0007\u0001\b"+
		"\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u012d\b\t\n\t\f\t\u0130\t\t\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0001\n\u0003\n\u0137\b\n\u0001\n\u0001\n\u0001"+
		"\n\u0003\n\u013c\b\n\u0001\n\u0001\n\u0001\n\u0003\n\u0141\b\n\u0003\n"+
		"\u0143\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u0164"+
		"\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0003\r\u0171\b\r\u0001\r\u0001\r\u0003\r\u0175\b\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u017d\b\r\u0001"+
		"\r\u0003\r\u0180\b\r\u0001\r\u0001\r\u0003\r\u0184\b\r\u0003\r\u0186\b"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\r\u018c\b\r\n\r\f\r\u018f\t\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u01a7\b\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0005\r\u01da\b\r\n\r\f\r\u01dd\t\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e"+
		"\u01e6\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u01eb\b"+
		"\u000f\u0003\u000f\u01ed\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0003\u0010\u01f3\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003"+
		"\u0010\u01f8\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u01fd"+
		"\b\u0011\n\u0011\f\u0011\u0200\t\u0011\u0001\u0011\u0001\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0005\u0014\u020b\b\u0014\n\u0014\f\u0014\u020e\t\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u0215\b\u0015\n"+
		"\u0015\f\u0015\u0218\t\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003"+
		"\u0016\u0223\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0005\u0017\u022b\b\u0017\n\u0017\f\u0017\u022e\t\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0233\b\u0017\u0001\u0018"+
		"\u0003\u0018\u0236\b\u0018\u0001\u0018\u0003\u0018\u0239\b\u0018\u0001"+
		"\u0018\u0001\u0018\u0003\u0018\u023d\b\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0003\u0018\u0243\b\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0003\u0018\u0248\b\u0018\u0003\u0018\u024a\b\u0018\u0001\u0019"+
		"\u0003\u0019\u024d\b\u0019\u0001\u0019\u0003\u0019\u0250\b\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003"+
		"\u0019\u0258\b\u0019\u0001\u001a\u0003\u001a\u025b\b\u001a\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0004\u001b\u0267\b\u001b\u000b\u001b"+
		"\f\u001b\u0268\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u026e\b"+
		"\u001b\u0001\u001c\u0004\u001c\u0271\b\u001c\u000b\u001c\f\u001c\u0272"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0279\b\u001d"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u027e\b\u001e\n\u001e"+
		"\f\u001e\u0281\t\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f"+
		"\u0003\u001f\u0287\b\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0003 \u0291\b \u0001!\u0004!\u0294\b!\u000b!\f!\u0295\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0003\"\u029c\b\"\u0001#\u0001#\u0001#\u0005"+
		"#\u02a1\b#\n#\f#\u02a4\t#\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0003"+
		"$\u02ac\b$\u0001%\u0003%\u02af\b%\u0001%\u0001%\u0001%\u0005%\u02b4\b"+
		"%\n%\f%\u02b7\t%\u0001%\u0001%\u0001&\u0003&\u02bc\b&\u0001&\u0001&\u0001"+
		"&\u0000\u0001\u001a\'\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJL\u0000\u0011"+
		"\u0003\u0000\u0017\u0017\u001c\u001c!!\u0002\u0000\t\t((\u0001\u0000j"+
		"k\u0002\u0000\u0011\u0011&&\u0002\u0000EEHH\u0005\u00005599<<IIMM\u0002"+
		"\u0000FGJL\u0002\u0000IIMM\u0001\u0000>@\u0001\u000067\u0001\u0000-0\u0002"+
		"\u0000,,13\u0002\u000088;;\u0002\u0000::==\u0001\u0000O_\u0002\u0000j"+
		"jlm\u0002\u0000\u0014\u0014$$\u0333\u0000O\u0001\u0000\u0000\u0000\u0002"+
		"W\u0001\u0000\u0000\u0000\u0004`\u0001\u0000\u0000\u0000\u0006i\u0001"+
		"\u0000\u0000\u0000\b\u00f6\u0001\u0000\u0000\u0000\n\u00fa\u0001\u0000"+
		"\u0000\u0000\f\u010c\u0001\u0000\u0000\u0000\u000e\u0125\u0001\u0000\u0000"+
		"\u0000\u0010\u0127\u0001\u0000\u0000\u0000\u0012\u012e\u0001\u0000\u0000"+
		"\u0000\u0014\u0142\u0001\u0000\u0000\u0000\u0016\u0144\u0001\u0000\u0000"+
		"\u0000\u0018\u0163\u0001\u0000\u0000\u0000\u001a\u01a6\u0001\u0000\u0000"+
		"\u0000\u001c\u01e5\u0001\u0000\u0000\u0000\u001e\u01ec\u0001\u0000\u0000"+
		"\u0000 \u01f7\u0001\u0000\u0000\u0000\"\u01fe\u0001\u0000\u0000\u0000"+
		"$\u0203\u0001\u0000\u0000\u0000&\u0205\u0001\u0000\u0000\u0000(\u020c"+
		"\u0001\u0000\u0000\u0000*\u0216\u0001\u0000\u0000\u0000,\u021b\u0001\u0000"+
		"\u0000\u0000.\u0232\u0001\u0000\u0000\u00000\u0249\u0001\u0000\u0000\u0000"+
		"2\u0257\u0001\u0000\u0000\u00004\u025a\u0001\u0000\u0000\u00006\u026d"+
		"\u0001\u0000\u0000\u00008\u0270\u0001\u0000\u0000\u0000:\u0274\u0001\u0000"+
		"\u0000\u0000<\u027f\u0001\u0000\u0000\u0000>\u0286\u0001\u0000\u0000\u0000"+
		"@\u0290\u0001\u0000\u0000\u0000B\u0293\u0001\u0000\u0000\u0000D\u0297"+
		"\u0001\u0000\u0000\u0000F\u02a2\u0001\u0000\u0000\u0000H\u02a7\u0001\u0000"+
		"\u0000\u0000J\u02ae\u0001\u0000\u0000\u0000L\u02bb\u0001\u0000\u0000\u0000"+
		"NP\u0003\u0002\u0001\u0000ON\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000"+
		"\u0000PQ\u0001\u0000\u0000\u0000QR\u0005\u0000\u0000\u0001R\u0001\u0001"+
		"\u0000\u0000\u0000SU\u0003\b\u0004\u0000TS\u0001\u0000\u0000\u0000TU\u0001"+
		"\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VX\u0005i\u0000\u0000WT\u0001"+
		"\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000"+
		"YZ\u0001\u0000\u0000\u0000Z\u0003\u0001\u0000\u0000\u0000[\\\u0003\u001a"+
		"\r\u0000\\]\u0005b\u0000\u0000]_\u0001\u0000\u0000\u0000^[\u0001\u0000"+
		"\u0000\u0000_b\u0001\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000`a\u0001"+
		"\u0000\u0000\u0000ac\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000"+
		"cd\u0003\u001a\r\u0000d\u0005\u0001\u0000\u0000\u0000ef\u0005j\u0000\u0000"+
		"fh\u0005b\u0000\u0000ge\u0001\u0000\u0000\u0000hk\u0001\u0000\u0000\u0000"+
		"ig\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jl\u0001\u0000\u0000"+
		"\u0000ki\u0001\u0000\u0000\u0000lm\u0005j\u0000\u0000m\u0007\u0001\u0000"+
		"\u0000\u0000n\u00f7\u0003\u001a\r\u0000o\u00f7\u0003 \u0010\u0000p\u00f7"+
		"\u0003\f\u0006\u0000qr\u0005\u001a\u0000\u0000r\u00f7\u0005j\u0000\u0000"+
		"sw\u0005{\u0000\u0000tv\u0003\u001c\u000e\u0000ut\u0001\u0000\u0000\u0000"+
		"vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000"+
		"\u0000x\u00f7\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z{\u0005"+
		"\u0001\u0000\u0000{\u00f7\u0003*\u0015\u0000|}\u0005\u001f\u0000\u0000"+
		"}~\u0005j\u0000\u0000~\u007f\u0005O\u0000\u0000\u007f\u00f7\u0003\u001a"+
		"\r\u0000\u0080\u0081\u0005\"\u0000\u0000\u0081\u0084\u0005n\u0000\u0000"+
		"\u0082\u0083\u0005!\u0000\u0000\u0083\u0085\u0003\u0006\u0003\u0000\u0084"+
		"\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085"+
		"\u00f7\u0001\u0000\u0000\u0000\u0086\u0087\u0005\"\u0000\u0000\u0087\u008a"+
		"\u0005j\u0000\u0000\u0088\u0089\u0005!\u0000\u0000\u0089\u008b\u0003\u0006"+
		"\u0003\u0000\u008a\u0088\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000"+
		"\u0000\u0000\u008b\u00f7\u0001\u0000\u0000\u0000\u008c\u008e\u0007\u0000"+
		"\u0000\u0000\u008d\u008c\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000"+
		"\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0090\u0007\u0001"+
		"\u0000\u0000\u0090\u0091\u0005j\u0000\u0000\u0091\u0093\u0003.\u0017\u0000"+
		"\u0092\u0094\u0003\u0002\u0001\u0000\u0093\u0092\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000"+
		"\u0095\u0096\u0005\u000f\u0000\u0000\u0096\u00f7\u0001\u0000\u0000\u0000"+
		"\u0097\u0098\u0005j\u0000\u0000\u0098\u009a\u0005a\u0000\u0000\u0099\u0097"+
		"\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009b"+
		"\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0013\u0000\u0000\u009c\u009d"+
		"\u0003F#\u0000\u009d\u009f\u0005\n\u0000\u0000\u009e\u00a0\u0003\u0002"+
		"\u0001\u0000\u009f\u009e\u0001\u0000\u0000\u0000\u009f\u00a0\u0001\u0000"+
		"\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005\u000f"+
		"\u0000\u0000\u00a2\u00f7\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005j\u0000"+
		"\u0000\u00a4\u00a6\u0005a\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000"+
		"\u00a7\u00a9\u0005\u0013\u0000\u0000\u00a8\u00aa\u0003\n\u0005\u0000\u00a9"+
		"\u00a8\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa"+
		"\u00ab\u0001\u0000\u0000\u0000\u00ab\u00ad\u0005i\u0000\u0000\u00ac\u00ae"+
		"\u0003\u001a\r\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001"+
		"\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b1\u0005"+
		"i\u0000\u0000\u00b0\u00b2\u0003\u0004\u0002\u0000\u00b1\u00b0\u0001\u0000"+
		"\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b5\u0005\n\u0000\u0000\u00b4\u00b6\u0003\u0002\u0001"+
		"\u0000\u00b5\u00b4\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000"+
		"\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00f7\u0005\u000f\u0000"+
		"\u0000\u00b8\u00b9\u0005j\u0000\u0000\u00b9\u00bb\u0005a\u0000\u0000\u00ba"+
		"\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb"+
		"\u00bc\u0001\u0000\u0000\u0000\u00bc\u00be\u0005\u0013\u0000\u0000\u00bd"+
		"\u00bf\u0003\u0010\b\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00be\u00bf"+
		"\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1"+
		"\u0003\u0018\f\u0000\u00c1\u00c2\u0005\u0016\u0000\u0000\u00c2\u00c4\u0003"+
		"@ \u0000\u00c3\u00c5\u0005\b\u0000\u0000\u00c4\u00c3\u0001\u0000\u0000"+
		"\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000"+
		"\u0000\u00c6\u00c8\u0005\n\u0000\u0000\u00c7\u00c9\u0003\u0002\u0001\u0000"+
		"\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000"+
		"\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00cb\u0005\u000f\u0000\u0000"+
		"\u00cb\u00f7\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005j\u0000\u0000\u00cd"+
		"\u00cf\u0005a\u0000\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf"+
		"\u0001\u0000\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d1"+
		"\u0005+\u0000\u0000\u00d1\u00d2\u0003\u001a\r\u0000\u00d2\u00d4\u0005"+
		"\n\u0000\u0000\u00d3\u00d5\u0003\u0002\u0001\u0000\u00d4\u00d3\u0001\u0000"+
		"\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000"+
		"\u0000\u0000\u00d6\u00d7\u0005\u000f\u0000\u0000\u00d7\u00f7\u0001\u0000"+
		"\u0000\u0000\u00d8\u00d9\u0005j\u0000\u0000\u00d9\u00db\u0005a\u0000\u0000"+
		"\u00da\u00d8\u0001\u0000\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000"+
		"\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc\u00de\u0005\n\u0000\u0000\u00dd"+
		"\u00df\u0003\u0002\u0001\u0000\u00de\u00dd\u0001\u0000\u0000\u0000\u00de"+
		"\u00df\u0001\u0000\u0000\u0000\u00df\u00e1\u0001\u0000\u0000\u0000\u00e0"+
		"\u00e2\u0005\u0019\u0000\u0000\u00e1\u00e0\u0001\u0000\u0000\u0000\u00e1"+
		"\u00e2\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000\u00e3"+
		"\u00e4\u0005+\u0000\u0000\u00e4\u00f7\u0003\u001a\r\u0000\u00e5\u00e7"+
		"\u0005\u0003\u0000\u0000\u00e6\u00e8\u0005j\u0000\u0000\u00e7\u00e6\u0001"+
		"\u0000\u0000\u0000\u00e7\u00e8\u0001\u0000\u0000\u0000\u00e8\u00f7\u0001"+
		"\u0000\u0000\u0000\u00e9\u00eb\u0005\u0007\u0000\u0000\u00ea\u00ec\u0005"+
		"j\u0000\u0000\u00eb\u00ea\u0001\u0000\u0000\u0000\u00eb\u00ec\u0001\u0000"+
		"\u0000\u0000\u00ec\u00f7\u0001\u0000\u0000\u0000\u00ed\u00ef\u0005#\u0000"+
		"\u0000\u00ee\u00f0\u0003\u001a\r\u0000\u00ef\u00ee\u0001\u0000\u0000\u0000"+
		"\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0\u00f7\u0001\u0000\u0000\u0000"+
		"\u00f1\u00f3\u0005\u0002\u0000\u0000\u00f2\u00f4\u0003\u0002\u0001\u0000"+
		"\u00f3\u00f2\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f7\u0005\u000f\u0000\u0000"+
		"\u00f6n\u0001\u0000\u0000\u0000\u00f6o\u0001\u0000\u0000\u0000\u00f6p"+
		"\u0001\u0000\u0000\u0000\u00f6q\u0001\u0000\u0000\u0000\u00f6s\u0001\u0000"+
		"\u0000\u0000\u00f6z\u0001\u0000\u0000\u0000\u00f6|\u0001\u0000\u0000\u0000"+
		"\u00f6\u0080\u0001\u0000\u0000\u0000\u00f6\u0086\u0001\u0000\u0000\u0000"+
		"\u00f6\u008d\u0001\u0000\u0000\u0000\u00f6\u0099\u0001\u0000\u0000\u0000"+
		"\u00f6\u00a5\u0001\u0000\u0000\u0000\u00f6\u00ba\u0001\u0000\u0000\u0000"+
		"\u00f6\u00ce\u0001\u0000\u0000\u0000\u00f6\u00da\u0001\u0000\u0000\u0000"+
		"\u00f6\u00e5\u0001\u0000\u0000\u0000\u00f6\u00e9\u0001\u0000\u0000\u0000"+
		"\u00f6\u00ed\u0001\u0000\u0000\u0000\u00f6\u00f1\u0001\u0000\u0000\u0000"+
		"\u00f7\t\u0001\u0000\u0000\u0000\u00f8\u00fb\u0003\f\u0006\u0000\u00f9"+
		"\u00fb\u0003\u0004\u0002\u0000\u00fa\u00f8\u0001\u0000\u0000\u0000\u00fa"+
		"\u00f9\u0001\u0000\u0000\u0000\u00fb\u000b\u0001\u0000\u0000\u0000\u00fc"+
		"\u00fe\u0003\u000e\u0007\u0000\u00fd\u00fc\u0001\u0000\u0000\u0000\u00fe"+
		"\u0101\u0001\u0000\u0000\u0000\u00ff\u00fd\u0001\u0000\u0000\u0000\u00ff"+
		"\u0100\u0001\u0000\u0000\u0000\u0100\u0102\u0001\u0000\u0000\u0000\u0101"+
		"\u00ff\u0001\u0000\u0000\u0000\u0102\u0103\u0003\u0010\b\u0000\u0103\u0104"+
		"\u0003\u0012\t\u0000\u0104\u010d\u0001\u0000\u0000\u0000\u0105\u0107\u0003"+
		"\u000e\u0007\u0000\u0106\u0105\u0001\u0000\u0000\u0000\u0107\u0108\u0001"+
		"\u0000\u0000\u0000\u0108\u0106\u0001\u0000\u0000\u0000\u0108\u0109\u0001"+
		"\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a\u010b\u0003"+
		"\u0012\t\u0000\u010b\u010d\u0001\u0000\u0000\u0000\u010c\u00ff\u0001\u0000"+
		"\u0000\u0000\u010c\u0106\u0001\u0000\u0000\u0000\u010d\r\u0001\u0000\u0000"+
		"\u0000\u010e\u0126\u0005\u0006\u0000\u0000\u010f\u0126\u0005\u0004\u0000"+
		"\u0000\u0110\u0112\u0005\u0010\u0000\u0000\u0111\u0113\u0005j\u0000\u0000"+
		"\u0112\u0111\u0001\u0000\u0000\u0000\u0112\u0113\u0001\u0000\u0000\u0000"+
		"\u0113\u0126\u0001\u0000\u0000\u0000\u0114\u0115\u0005\u0010\u0000\u0000"+
		"\u0115\u0116\u0005j\u0000\u0000\u0116\u0117\u0005C\u0000\u0000\u0117\u0118"+
		"\u0003\u001a\r\u0000\u0118\u0119\u0005D\u0000\u0000\u0119\u0126\u0001"+
		"\u0000\u0000\u0000\u011a\u011b\u0005\u0010\u0000\u0000\u011b\u011c\u0005"+
		"j\u0000\u0000\u011c\u011d\u0005C\u0000\u0000\u011d\u011e\u0003@ \u0000"+
		"\u011e\u011f\u0005D\u0000\u0000\u011f\u0126\u0001\u0000\u0000\u0000\u0120"+
		"\u0126\u0005\u0012\u0000\u0000\u0121\u0126\u0005\u0018\u0000\u0000\u0122"+
		"\u0126\u0005\u001b\u0000\u0000\u0123\u0126\u0005!\u0000\u0000\u0124\u0126"+
		"\u0005)\u0000\u0000\u0125\u010e\u0001\u0000\u0000\u0000\u0125\u010f\u0001"+
		"\u0000\u0000\u0000\u0125\u0110\u0001\u0000\u0000\u0000\u0125\u0114\u0001"+
		"\u0000\u0000\u0000\u0125\u011a\u0001\u0000\u0000\u0000\u0125\u0120\u0001"+
		"\u0000\u0000\u0000\u0125\u0121\u0001\u0000\u0000\u0000\u0125\u0122\u0001"+
		"\u0000\u0000\u0000\u0125\u0123\u0001\u0000\u0000\u0000\u0125\u0124\u0001"+
		"\u0000\u0000\u0000\u0126\u000f\u0001\u0000\u0000\u0000\u0127\u0128\u0005"+
		"\'\u0000\u0000\u0128\u0011\u0001\u0000\u0000\u0000\u0129\u012a\u0003\u0014"+
		"\n\u0000\u012a\u012b\u0005b\u0000\u0000\u012b\u012d\u0001\u0000\u0000"+
		"\u0000\u012c\u0129\u0001\u0000\u0000\u0000\u012d\u0130\u0001\u0000\u0000"+
		"\u0000\u012e\u012c\u0001\u0000\u0000\u0000\u012e\u012f\u0001\u0000\u0000"+
		"\u0000\u012f\u0131\u0001\u0000\u0000\u0000\u0130\u012e\u0001\u0000\u0000"+
		"\u0000\u0131\u0132\u0003\u0014\n\u0000\u0132\u0013\u0001\u0000\u0000\u0000"+
		"\u0133\u0136\u0007\u0002\u0000\u0000\u0134\u0135\u0005O\u0000\u0000\u0135"+
		"\u0137\u0003\u001a\r\u0000\u0136\u0134\u0001\u0000\u0000\u0000\u0136\u0137"+
		"\u0001\u0000\u0000\u0000\u0137\u0143\u0001\u0000\u0000\u0000\u0138\u0139"+
		"\u0007\u0002\u0000\u0000\u0139\u013b\u0005C\u0000\u0000\u013a\u013c\u0003"+
		"\u001a\r\u0000\u013b\u013a\u0001\u0000\u0000\u0000\u013b\u013c\u0001\u0000"+
		"\u0000\u0000\u013c\u013d\u0001\u0000\u0000\u0000\u013d\u0140\u0005D\u0000"+
		"\u0000\u013e\u013f\u0005O\u0000\u0000\u013f\u0141\u0003\u0016\u000b\u0000"+
		"\u0140\u013e\u0001\u0000\u0000\u0000\u0140\u0141\u0001\u0000\u0000\u0000"+
		"\u0141\u0143\u0001\u0000\u0000\u0000\u0142\u0133\u0001\u0000\u0000\u0000"+
		"\u0142\u0138\u0001\u0000\u0000\u0000\u0143\u0015\u0001\u0000\u0000\u0000"+
		"\u0144\u0145\u0005A\u0000\u0000\u0145\u0146\u0003\u0004\u0002\u0000\u0146"+
		"\u0147\u0005B\u0000\u0000\u0147\u0017\u0001\u0000\u0000\u0000\u0148\u0164"+
		"\u0005j\u0000\u0000\u0149\u0164\u0005k\u0000\u0000\u014a\u0164\u0005l"+
		"\u0000\u0000\u014b\u014c\u0007\u0002\u0000\u0000\u014c\u014d\u0005C\u0000"+
		"\u0000\u014d\u014e\u0003\u001a\r\u0000\u014e\u014f\u0005D\u0000\u0000"+
		"\u014f\u0164\u0001\u0000\u0000\u0000\u0150\u0151\u0007\u0002\u0000\u0000"+
		"\u0151\u0152\u0005C\u0000\u0000\u0152\u0153\u0003@ \u0000\u0153\u0154"+
		"\u0005D\u0000\u0000\u0154\u0164\u0001\u0000\u0000\u0000\u0155\u0156\u0005"+
		"j\u0000\u0000\u0156\u0157\u0005d\u0000\u0000\u0157\u0158\u0005j\u0000"+
		"\u0000\u0158\u0159\u0005C\u0000\u0000\u0159\u015a\u0003\u001a\r\u0000"+
		"\u015a\u015b\u0005D\u0000\u0000\u015b\u0164\u0001\u0000\u0000\u0000\u015c"+
		"\u015d\u0005j\u0000\u0000\u015d\u015e\u0005d\u0000\u0000\u015e\u015f\u0005"+
		"j\u0000\u0000\u015f\u0160\u0005C\u0000\u0000\u0160\u0161\u0003@ \u0000"+
		"\u0161\u0162\u0005D\u0000\u0000\u0162\u0164\u0001\u0000\u0000\u0000\u0163"+
		"\u0148\u0001\u0000\u0000\u0000\u0163\u0149\u0001\u0000\u0000\u0000\u0163"+
		"\u014a\u0001\u0000\u0000\u0000\u0163\u014b\u0001\u0000\u0000\u0000\u0163"+
		"\u0150\u0001\u0000\u0000\u0000\u0163\u0155\u0001\u0000\u0000\u0000\u0163"+
		"\u015c\u0001\u0000\u0000\u0000\u0164\u0019\u0001\u0000\u0000\u0000\u0165"+
		"\u0166\u0006\r\uffff\uffff\u0000\u0166\u01a7\u0003\u0018\f\u0000\u0167"+
		"\u01a7\u0005m\u0000\u0000\u0168\u0169\u0005\u000f\u0000\u0000\u0169\u016a"+
		"\u0005A\u0000\u0000\u016a\u01a7\u0005B\u0000\u0000\u016b\u016c\u0005j"+
		"\u0000\u0000\u016c\u01a7\u00036\u001b\u0000\u016d\u016e\u0005\u0005\u0000"+
		"\u0000\u016e\u0170\u0003\u001a\r\u0000\u016f\u0171\u00038\u001c\u0000"+
		"\u0170\u016f\u0001\u0000\u0000\u0000\u0170\u0171\u0001\u0000\u0000\u0000"+
		"\u0171\u0174\u0001\u0000\u0000\u0000\u0172\u0173\u0005\f\u0000\u0000\u0173"+
		"\u0175\u0003\u0002\u0001\u0000\u0174\u0172\u0001\u0000\u0000\u0000\u0174"+
		"\u0175\u0001\u0000\u0000\u0000\u0175\u0176\u0001\u0000\u0000\u0000\u0176"+
		"\u0177\u0005\u000f\u0000\u0000\u0177\u01a7\u0001\u0000\u0000\u0000\u0178"+
		"\u0179\u0005\u0015\u0000\u0000\u0179\u017a\u0003\u001a\r\u0000\u017a\u017c"+
		"\u0005%\u0000\u0000\u017b\u017d\u0003\u0002\u0001\u0000\u017c\u017b\u0001"+
		"\u0000\u0000\u0000\u017c\u017d\u0001\u0000\u0000\u0000\u017d\u017f\u0001"+
		"\u0000\u0000\u0000\u017e\u0180\u0003B!\u0000\u017f\u017e\u0001\u0000\u0000"+
		"\u0000\u017f\u0180\u0001\u0000\u0000\u0000\u0180\u0185\u0001\u0000\u0000"+
		"\u0000\u0181\u0183\u0005\f\u0000\u0000\u0182\u0184\u0003\u0002\u0001\u0000"+
		"\u0183\u0182\u0001\u0000\u0000\u0000\u0183\u0184\u0001\u0000\u0000\u0000"+
		"\u0184\u0186\u0001\u0000\u0000\u0000\u0185\u0181\u0001\u0000\u0000\u0000"+
		"\u0185\u0186\u0001\u0000\u0000\u0000\u0186\u0187\u0001\u0000\u0000\u0000"+
		"\u0187\u0188\u0005\u000f\u0000\u0000\u0188\u01a7\u0001\u0000\u0000\u0000"+
		"\u0189\u018d\u0005x\u0000\u0000\u018a\u018c\u0003\u001c\u000e\u0000\u018b"+
		"\u018a\u0001\u0000\u0000\u0000\u018c\u018f\u0001\u0000\u0000\u0000\u018d"+
		"\u018b\u0001\u0000\u0000\u0000\u018d\u018e\u0001\u0000\u0000\u0000\u018e"+
		"\u0190\u0001\u0000\u0000\u0000\u018f\u018d\u0001\u0000\u0000\u0000\u0190"+
		"\u01a7\u0005g\u0000\u0000\u0191\u01a7\u0005n\u0000\u0000\u0192\u01a7\u0005"+
		"o\u0000\u0000\u0193\u01a7\u0005p\u0000\u0000\u0194\u01a7\u0005q\u0000"+
		"\u0000\u0195\u01a7\u0005r\u0000\u0000\u0196\u01a7\u0005s\u0000\u0000\u0197"+
		"\u01a7\u0005t\u0000\u0000\u0198\u01a7\u0005u\u0000\u0000\u0199\u01a7\u0005"+
		"\u001d\u0000\u0000\u019a\u01a7\u0007\u0003\u0000\u0000\u019b\u019c\u0003"+
		"\u0018\f\u0000\u019c\u019d\u0007\u0004\u0000\u0000\u019d\u01a7\u0001\u0000"+
		"\u0000\u0000\u019e\u019f\u0007\u0004\u0000\u0000\u019f\u01a7\u0003\u0018"+
		"\f\u0000\u01a0\u01a1\u0007\u0005\u0000\u0000\u01a1\u01a7\u0003\u001a\r"+
		"\u000e\u01a2\u01a3\u0005A\u0000\u0000\u01a3\u01a4\u0003\u001a\r\u0000"+
		"\u01a4\u01a5\u0005B\u0000\u0000\u01a5\u01a7\u0001\u0000\u0000\u0000\u01a6"+
		"\u0165\u0001\u0000\u0000\u0000\u01a6\u0167\u0001\u0000\u0000\u0000\u01a6"+
		"\u0168\u0001\u0000\u0000\u0000\u01a6\u016b\u0001\u0000\u0000\u0000\u01a6"+
		"\u016d\u0001\u0000\u0000\u0000\u01a6\u0178\u0001\u0000\u0000\u0000\u01a6"+
		"\u0189\u0001\u0000\u0000\u0000\u01a6\u0191\u0001\u0000\u0000\u0000\u01a6"+
		"\u0192\u0001\u0000\u0000\u0000\u01a6\u0193\u0001\u0000\u0000\u0000\u01a6"+
		"\u0194\u0001\u0000\u0000\u0000\u01a6\u0195\u0001\u0000\u0000\u0000\u01a6"+
		"\u0196\u0001\u0000\u0000\u0000\u01a6\u0197\u0001\u0000\u0000\u0000\u01a6"+
		"\u0198\u0001\u0000\u0000\u0000\u01a6\u0199\u0001\u0000\u0000\u0000\u01a6"+
		"\u019a\u0001\u0000\u0000\u0000\u01a6\u019b\u0001\u0000\u0000\u0000\u01a6"+
		"\u019e\u0001\u0000\u0000\u0000\u01a6\u01a0\u0001\u0000\u0000\u0000\u01a6"+
		"\u01a2\u0001\u0000\u0000\u0000\u01a7\u01db\u0001\u0000\u0000\u0000\u01a8"+
		"\u01a9\n\r\u0000\u0000\u01a9\u01aa\u0005N\u0000\u0000\u01aa\u01da\u0003"+
		"\u001a\r\u000e\u01ab\u01ac\n\f\u0000\u0000\u01ac\u01ad\u0007\u0006\u0000"+
		"\u0000\u01ad\u01da\u0003\u001a\r\r\u01ae\u01af\n\u000b\u0000\u0000\u01af"+
		"\u01b0\u0007\u0007\u0000\u0000\u01b0\u01da\u0003\u001a\r\f\u01b1\u01b2"+
		"\n\n\u0000\u0000\u01b2\u01b3\u0007\b\u0000\u0000\u01b3\u01da\u0003\u001a"+
		"\r\u000b\u01b4\u01b5\n\t\u0000\u0000\u01b5\u01b6\u00054\u0000\u0000\u01b6"+
		"\u01da\u0003\u001a\r\n\u01b7\u01b8\n\b\u0000\u0000\u01b8\u01b9\u0007\t"+
		"\u0000\u0000\u01b9\u01da\u0003\u001a\r\t\u01ba\u01bb\n\u0007\u0000\u0000"+
		"\u01bb\u01bc\u0007\n\u0000\u0000\u01bc\u01da\u0003\u001a\r\b\u01bd\u01be"+
		"\n\u0006\u0000\u0000\u01be\u01bf\u0007\u000b\u0000\u0000\u01bf\u01da\u0003"+
		"\u001a\r\u0007\u01c0\u01c1\n\u0005\u0000\u0000\u01c1\u01c2\u0007\f\u0000"+
		"\u0000\u01c2\u01da\u0003\u001a\r\u0006\u01c3\u01c4\n\u0004\u0000\u0000"+
		"\u01c4\u01c5\u0007\r\u0000\u0000\u01c5\u01da\u0003\u001a\r\u0005\u01c6"+
		"\u01c7\n\u0003\u0000\u0000\u01c7\u01c8\u0005h\u0000\u0000\u01c8\u01c9"+
		"\u0003\u001a\r\u0000\u01c9\u01ca\u0005a\u0000\u0000\u01ca\u01cb\u0003"+
		"\u001a\r\u0003\u01cb\u01da\u0001\u0000\u0000\u0000\u01cc\u01cd\n\u0002"+
		"\u0000\u0000\u01cd\u01ce\u0007\u000e\u0000\u0000\u01ce\u01da\u0003\u001a"+
		"\r\u0002\u01cf\u01d0\n \u0000\u0000\u01d0\u01d1\u0005d\u0000\u0000\u01d1"+
		"\u01d2\u0005j\u0000\u0000\u01d2\u01da\u00036\u001b\u0000\u01d3\u01d4\n"+
		"\u001f\u0000\u0000\u01d4\u01d5\u0005d\u0000\u0000\u01d5\u01da\u0005j\u0000"+
		"\u0000\u01d6\u01d7\n\u001e\u0000\u0000\u01d7\u01d8\u0005d\u0000\u0000"+
		"\u01d8\u01da\u0005l\u0000\u0000\u01d9\u01a8\u0001\u0000\u0000\u0000\u01d9"+
		"\u01ab\u0001\u0000\u0000\u0000\u01d9\u01ae\u0001\u0000\u0000\u0000\u01d9"+
		"\u01b1\u0001\u0000\u0000\u0000\u01d9\u01b4\u0001\u0000\u0000\u0000\u01d9"+
		"\u01b7\u0001\u0000\u0000\u0000\u01d9\u01ba\u0001\u0000\u0000\u0000\u01d9"+
		"\u01bd\u0001\u0000\u0000\u0000\u01d9\u01c0\u0001\u0000\u0000\u0000\u01d9"+
		"\u01c3\u0001\u0000\u0000\u0000\u01d9\u01c6\u0001\u0000\u0000\u0000\u01d9"+
		"\u01cc\u0001\u0000\u0000\u0000\u01d9\u01cf\u0001\u0000\u0000\u0000\u01d9"+
		"\u01d3\u0001\u0000\u0000\u0000\u01d9\u01d6\u0001\u0000\u0000\u0000\u01da"+
		"\u01dd\u0001\u0000\u0000\u0000\u01db\u01d9\u0001\u0000\u0000\u0000\u01db"+
		"\u01dc\u0001\u0000\u0000\u0000\u01dc\u001b\u0001\u0000\u0000\u0000\u01dd"+
		"\u01db\u0001\u0000\u0000\u0000\u01de\u01e6\u0005\u0089\u0000\u0000\u01df"+
		"\u01e6\u0005\u008a\u0000\u0000\u01e0\u01e1\u0005\u008c\u0000\u0000\u01e1"+
		"\u01e2\u0003\u001a\r\u0000\u01e2\u01e3\u0005y\u0000\u0000\u01e3\u01e6"+
		"\u0001\u0000\u0000\u0000\u01e4\u01e6\u0003\u001e\u000f\u0000\u01e5\u01de"+
		"\u0001\u0000\u0000\u0000\u01e5\u01df\u0001\u0000\u0000\u0000\u01e5\u01e0"+
		"\u0001\u0000\u0000\u0000\u01e5\u01e4\u0001\u0000\u0000\u0000\u01e6\u001d"+
		"\u0001\u0000\u0000\u0000\u01e7\u01ed\u0005\u008b\u0000\u0000\u01e8\u01ea"+
		"\u0005\u008d\u0000\u0000\u01e9\u01eb\u0005\u008f\u0000\u0000\u01ea\u01e9"+
		"\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001\u0000\u0000\u0000\u01eb\u01ed"+
		"\u0001\u0000\u0000\u0000\u01ec\u01e7\u0001\u0000\u0000\u0000\u01ec\u01e8"+
		"\u0001\u0000\u0000\u0000\u01ed\u001f\u0001\u0000\u0000\u0000\u01ee\u01ef"+
		"\u0005w\u0000\u0000\u01ef\u01f2\u0003$\u0012\u0000\u01f0\u01f1\u0005\u0084"+
		"\u0000\u0000\u01f1\u01f3\u0003\"\u0011\u0000\u01f2\u01f0\u0001\u0000\u0000"+
		"\u0000\u01f2\u01f3\u0001\u0000\u0000\u0000\u01f3\u01f8\u0001\u0000\u0000"+
		"\u0000\u01f4\u01f5\u0005v\u0000\u0000\u01f5\u01f6\u0005j\u0000\u0000\u01f6"+
		"\u01f8\u0003(\u0014\u0000\u01f7\u01ee\u0001\u0000\u0000\u0000\u01f7\u01f4"+
		"\u0001\u0000\u0000\u0000\u01f8!\u0001\u0000\u0000\u0000\u01f9\u01fa\u0003"+
		"$\u0012\u0000\u01fa\u01fb\u0005\u0085\u0000\u0000\u01fb\u01fd\u0001\u0000"+
		"\u0000\u0000\u01fc\u01f9\u0001\u0000\u0000\u0000\u01fd\u0200\u0001\u0000"+
		"\u0000\u0000\u01fe\u01fc\u0001\u0000\u0000\u0000\u01fe\u01ff\u0001\u0000"+
		"\u0000\u0000\u01ff\u0201\u0001\u0000\u0000\u0000\u0200\u01fe\u0001\u0000"+
		"\u0000\u0000\u0201\u0202\u0003$\u0012\u0000\u0202#\u0001\u0000\u0000\u0000"+
		"\u0203\u0204\u0005\u0083\u0000\u0000\u0204%\u0001\u0000\u0000\u0000\u0205"+
		"\u0206\u0007\u000f\u0000\u0000\u0206\'\u0001\u0000\u0000\u0000\u0207\u0208"+
		"\u0003&\u0013\u0000\u0208\u0209\u0005b\u0000\u0000\u0209\u020b\u0001\u0000"+
		"\u0000\u0000\u020a\u0207\u0001\u0000\u0000\u0000\u020b\u020e\u0001\u0000"+
		"\u0000\u0000\u020c\u020a\u0001\u0000\u0000\u0000\u020c\u020d\u0001\u0000"+
		"\u0000\u0000\u020d\u020f\u0001\u0000\u0000\u0000\u020e\u020c\u0001\u0000"+
		"\u0000\u0000\u020f\u0210\u0003&\u0013\u0000\u0210)\u0001\u0000\u0000\u0000"+
		"\u0211\u0212\u0003,\u0016\u0000\u0212\u0213\u0005b\u0000\u0000\u0213\u0215"+
		"\u0001\u0000\u0000\u0000\u0214\u0211\u0001\u0000\u0000\u0000\u0215\u0218"+
		"\u0001\u0000\u0000\u0000\u0216\u0214\u0001\u0000\u0000\u0000\u0216\u0217"+
		"\u0001\u0000\u0000\u0000\u0217\u0219\u0001\u0000\u0000\u0000\u0218\u0216"+
		"\u0001\u0000\u0000\u0000\u0219\u021a\u0003,\u0016\u0000\u021a+\u0001\u0000"+
		"\u0000\u0000\u021b\u021c\u0007\u0010\u0000\u0000\u021c\u021d\u0005\u0016"+
		"\u0000\u0000\u021d\u0222\u0005j\u0000\u0000\u021e\u021f\u0005C\u0000\u0000"+
		"\u021f\u0220\u0003@ \u0000\u0220\u0221\u0005D\u0000\u0000\u0221\u0223"+
		"\u0001\u0000\u0000\u0000\u0222\u021e\u0001\u0000\u0000\u0000\u0222\u0223"+
		"\u0001\u0000\u0000\u0000\u0223-\u0001\u0000\u0000\u0000\u0224\u0225\u0005"+
		"A\u0000\u0000\u0225\u0233\u0005B\u0000\u0000\u0226\u022c\u0005A\u0000"+
		"\u0000\u0227\u0228\u00030\u0018\u0000\u0228\u0229\u0005b\u0000\u0000\u0229"+
		"\u022b\u0001\u0000\u0000\u0000\u022a\u0227\u0001\u0000\u0000\u0000\u022b"+
		"\u022e\u0001\u0000\u0000\u0000\u022c\u022a\u0001\u0000\u0000\u0000\u022c"+
		"\u022d\u0001\u0000\u0000\u0000\u022d\u022f\u0001\u0000\u0000\u0000\u022e"+
		"\u022c\u0001\u0000\u0000\u0000\u022f\u0230\u00030\u0018\u0000\u0230\u0231"+
		"\u0005B\u0000\u0000\u0231\u0233\u0001\u0000\u0000\u0000\u0232\u0224\u0001"+
		"\u0000\u0000\u0000\u0232\u0226\u0001\u0000\u0000\u0000\u0233/\u0001\u0000"+
		"\u0000\u0000\u0234\u0236\u0005\u0016\u0000\u0000\u0235\u0234\u0001\u0000"+
		"\u0000\u0000\u0235\u0236\u0001\u0000\u0000\u0000\u0236\u0238\u0001\u0000"+
		"\u0000\u0000\u0237\u0239\u0005\u001e\u0000\u0000\u0238\u0237\u0001\u0000"+
		"\u0000\u0000\u0238\u0239\u0001\u0000\u0000\u0000\u0239\u023a\u0001\u0000"+
		"\u0000\u0000\u023a\u023c\u0005j\u0000\u0000\u023b\u023d\u0005f\u0000\u0000"+
		"\u023c\u023b\u0001\u0000\u0000\u0000\u023c\u023d\u0001\u0000\u0000\u0000"+
		"\u023d\u024a\u0001\u0000\u0000\u0000\u023e\u023f\u0005\u001e\u0000\u0000"+
		"\u023f\u0240\u0005\u0016\u0000\u0000\u0240\u0242\u0005j\u0000\u0000\u0241"+
		"\u0243\u0005f\u0000\u0000\u0242\u0241\u0001\u0000\u0000\u0000\u0242\u0243"+
		"\u0001\u0000\u0000\u0000\u0243\u024a\u0001\u0000\u0000\u0000\u0244\u0245"+
		"\u0005 \u0000\u0000\u0245\u0247\u0005j\u0000\u0000\u0246\u0248\u0005f"+
		"\u0000\u0000\u0247\u0246\u0001\u0000\u0000\u0000\u0247\u0248\u0001\u0000"+
		"\u0000\u0000\u0248\u024a\u0001\u0000\u0000\u0000\u0249\u0235\u0001\u0000"+
		"\u0000\u0000\u0249\u023e\u0001\u0000\u0000\u0000\u0249\u0244\u0001\u0000"+
		"\u0000\u0000\u024a1\u0001\u0000\u0000\u0000\u024b\u024d\u0005\u0016\u0000"+
		"\u0000\u024c\u024b\u0001\u0000\u0000\u0000\u024c\u024d\u0001\u0000\u0000"+
		"\u0000\u024d\u024f\u0001\u0000\u0000\u0000\u024e\u0250\u0005\u001e\u0000"+
		"\u0000\u024f\u024e\u0001\u0000\u0000\u0000\u024f\u0250\u0001\u0000\u0000"+
		"\u0000\u0250\u0251\u0001\u0000\u0000\u0000\u0251\u0258\u0003\u001a\r\u0000"+
		"\u0252\u0253\u0005\u001e\u0000\u0000\u0253\u0254\u0005\u0016\u0000\u0000"+
		"\u0254\u0258\u0003\u001a\r\u0000\u0255\u0256\u0005 \u0000\u0000\u0256"+
		"\u0258\u0003\u001a\r\u0000\u0257\u024c\u0001\u0000\u0000\u0000\u0257\u0252"+
		"\u0001\u0000\u0000\u0000\u0257\u0255\u0001\u0000\u0000\u0000\u02583\u0001"+
		"\u0000\u0000\u0000\u0259\u025b\u00032\u0019\u0000\u025a\u0259\u0001\u0000"+
		"\u0000\u0000\u025a\u025b\u0001\u0000\u0000\u0000\u025b5\u0001\u0000\u0000"+
		"\u0000\u025c\u025d\u0005A\u0000\u0000\u025d\u026e\u0005B\u0000\u0000\u025e"+
		"\u025f\u0005A\u0000\u0000\u025f\u0260\u00032\u0019\u0000\u0260\u0261\u0005"+
		"B\u0000\u0000\u0261\u026e\u0001\u0000\u0000\u0000\u0262\u0266\u0005A\u0000"+
		"\u0000\u0263\u0264\u00034\u001a\u0000\u0264\u0265\u0005b\u0000\u0000\u0265"+
		"\u0267\u0001\u0000\u0000\u0000\u0266\u0263\u0001\u0000\u0000\u0000\u0267"+
		"\u0268\u0001\u0000\u0000\u0000\u0268\u0266\u0001\u0000\u0000\u0000\u0268"+
		"\u0269\u0001\u0000\u0000\u0000\u0269\u026a\u0001\u0000\u0000\u0000\u026a"+
		"\u026b\u00034\u001a\u0000\u026b\u026c\u0005B\u0000\u0000\u026c\u026e\u0001"+
		"\u0000\u0000\u0000\u026d\u025c\u0001\u0000\u0000\u0000\u026d\u025e\u0001"+
		"\u0000\u0000\u0000\u026d\u0262\u0001\u0000\u0000\u0000\u026e7\u0001\u0000"+
		"\u0000\u0000\u026f\u0271\u0003:\u001d\u0000\u0270\u026f\u0001\u0000\u0000"+
		"\u0000\u0271\u0272\u0001\u0000\u0000\u0000\u0272\u0270\u0001\u0000\u0000"+
		"\u0000\u0272\u0273\u0001\u0000\u0000\u0000\u02739\u0001\u0000\u0000\u0000"+
		"\u0274\u0275\u0005*\u0000\u0000\u0275\u0276\u0003<\u001e\u0000\u0276\u0278"+
		"\u0005%\u0000\u0000\u0277\u0279\u0003\u0002\u0001\u0000\u0278\u0277\u0001"+
		"\u0000\u0000\u0000\u0278\u0279\u0001\u0000\u0000\u0000\u0279;\u0001\u0000"+
		"\u0000\u0000\u027a\u027b\u0003>\u001f\u0000\u027b\u027c\u0005b\u0000\u0000"+
		"\u027c\u027e\u0001\u0000\u0000\u0000\u027d\u027a\u0001\u0000\u0000\u0000"+
		"\u027e\u0281\u0001\u0000\u0000\u0000\u027f\u027d\u0001\u0000\u0000\u0000"+
		"\u027f\u0280\u0001\u0000\u0000\u0000\u0280\u0282\u0001\u0000\u0000\u0000"+
		"\u0281\u027f\u0001\u0000\u0000\u0000\u0282\u0283\u0003>\u001f\u0000\u0283"+
		"=\u0001\u0000\u0000\u0000\u0284\u0287\u0003\u001a\r\u0000\u0285\u0287"+
		"\u0003@ \u0000\u0286\u0284\u0001\u0000\u0000\u0000\u0286\u0285\u0001\u0000"+
		"\u0000\u0000\u0287?\u0001\u0000\u0000\u0000\u0288\u0289\u0003\u001a\r"+
		"\u0000\u0289\u028a\u0005e\u0000\u0000\u028a\u028b\u0003\u001a\r\u0000"+
		"\u028b\u0291\u0001\u0000\u0000\u0000\u028c\u028d\u0003\u001a\r\u0000\u028d"+
		"\u028e\u0005f\u0000\u0000\u028e\u028f\u0003\u001a\r\u0000\u028f\u0291"+
		"\u0001\u0000\u0000\u0000\u0290\u0288\u0001\u0000\u0000\u0000\u0290\u028c"+
		"\u0001\u0000\u0000\u0000\u0291A\u0001\u0000\u0000\u0000\u0292\u0294\u0003"+
		"D\"\u0000\u0293\u0292\u0001\u0000\u0000\u0000\u0294\u0295\u0001\u0000"+
		"\u0000\u0000\u0295\u0293\u0001\u0000\u0000\u0000\u0295\u0296\u0001\u0000"+
		"\u0000\u0000\u0296C\u0001\u0000\u0000\u0000\u0297\u0298\u0005\u000e\u0000"+
		"\u0000\u0298\u0299\u0003\u001a\r\u0000\u0299\u029b\u0005%\u0000\u0000"+
		"\u029a\u029c\u0003\u0002\u0001\u0000\u029b\u029a\u0001\u0000\u0000\u0000"+
		"\u029b\u029c\u0001\u0000\u0000\u0000\u029cE\u0001\u0000\u0000\u0000\u029d"+
		"\u029e\u0003H$\u0000\u029e\u029f\u0005i\u0000\u0000\u029f\u02a1\u0001"+
		"\u0000\u0000\u0000\u02a0\u029d\u0001\u0000\u0000\u0000\u02a1\u02a4\u0001"+
		"\u0000\u0000\u0000\u02a2\u02a0\u0001\u0000\u0000\u0000\u02a2\u02a3\u0001"+
		"\u0000\u0000\u0000\u02a3\u02a5\u0001\u0000\u0000\u0000\u02a4\u02a2\u0001"+
		"\u0000\u0000\u0000\u02a5\u02a6\u0003H$\u0000\u02a6G\u0001\u0000\u0000"+
		"\u0000\u02a7\u02a8\u0003J%\u0000\u02a8\u02a9\u0005\u0016\u0000\u0000\u02a9"+
		"\u02ab\u0003\u0004\u0002\u0000\u02aa\u02ac\u0005\b\u0000\u0000\u02ab\u02aa"+
		"\u0001\u0000\u0000\u0000\u02ab\u02ac\u0001\u0000\u0000\u0000\u02acI\u0001"+
		"\u0000\u0000\u0000\u02ad\u02af\u0003\u0010\b\u0000\u02ae\u02ad\u0001\u0000"+
		"\u0000\u0000\u02ae\u02af\u0001\u0000\u0000\u0000\u02af\u02b5\u0001\u0000"+
		"\u0000\u0000\u02b0\u02b1\u0003L&\u0000\u02b1\u02b2\u0005b\u0000\u0000"+
		"\u02b2\u02b4\u0001\u0000\u0000\u0000\u02b3\u02b0\u0001\u0000\u0000\u0000"+
		"\u02b4\u02b7\u0001\u0000\u0000\u0000\u02b5\u02b3\u0001\u0000\u0000\u0000"+
		"\u02b5\u02b6\u0001\u0000\u0000\u0000\u02b6\u02b8\u0001\u0000\u0000\u0000"+
		"\u02b7\u02b5\u0001\u0000\u0000\u0000\u02b8\u02b9\u0003L&\u0000\u02b9K"+
		"\u0001\u0000\u0000\u0000\u02ba\u02bc\u0005\u001e\u0000\u0000\u02bb\u02ba"+
		"\u0001\u0000\u0000\u0000\u02bb\u02bc\u0001\u0000\u0000\u0000\u02bc\u02bd"+
		"\u0001\u0000\u0000\u0000\u02bd\u02be\u0003\u0018\f\u0000\u02beM\u0001"+
		"\u0000\u0000\u0000XOTY`iw\u0084\u008a\u008d\u0093\u0099\u009f\u00a5\u00a9"+
		"\u00ad\u00b1\u00b5\u00ba\u00be\u00c4\u00c8\u00ce\u00d4\u00da\u00de\u00e1"+
		"\u00e7\u00eb\u00ef\u00f3\u00f6\u00fa\u00ff\u0108\u010c\u0112\u0125\u012e"+
		"\u0136\u013b\u0140\u0142\u0163\u0170\u0174\u017c\u017f\u0183\u0185\u018d"+
		"\u01a6\u01d9\u01db\u01e5\u01ea\u01ec\u01f2\u01f7\u01fe\u020c\u0216\u0222"+
		"\u022c\u0232\u0235\u0238\u023c\u0242\u0247\u0249\u024c\u024f\u0257\u025a"+
		"\u0268\u026d\u0272\u0278\u027f\u0286\u0290\u0295\u029b\u02a2\u02ab\u02ae"+
		"\u02b5\u02bb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}