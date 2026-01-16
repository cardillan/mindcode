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
		ALLOCATE=1, ATOMIC=2, BEGIN=3, BREAK=4, CACHED=5, CASE=6, CONST=7, CONTINUE=8, 
		DEBUG=9, DESCENDING=10, DEF=11, DO=12, ELIF=13, ELSE=14, ELSEIF=15, ELSIF=16, 
		END=17, EXPORT=18, EXTERNAL=19, FALSE=20, GUARDED=21, FOR=22, HEAP=23, 
		IF=24, IN=25, INLINE=26, LINKED=27, MODULE=28, MLOG=29, NOINIT=30, NOINLINE=31, 
		NULL=32, OUT=33, PARAM=34, PRIVATE=35, PUBLIC=36, REF=37, REMOTE=38, REQUIRE=39, 
		RETURN=40, STACK=41, THEN=42, TRUE=43, VAR=44, VOID=45, VOLATILE=46, WHEN=47, 
		WHILE=48, EQUAL=49, GREATER_THAN=50, GREATER_THAN_EQUAL=51, LESS_THAN=52, 
		LESS_THAN_EQUAL=53, NOT_EQUAL=54, STRICT_EQUAL=55, STRICT_NOT_EQUAL=56, 
		BITWISE_AND=57, BITWISE_NOT=58, BITWISE_OR=59, BITWISE_XOR=60, BOOLEAN_AND=61, 
		BOOLEAN_NOT=62, BOOLEAN_OR=63, LOGICAL_AND=64, LOGICAL_NOT=65, LOGICAL_OR=66, 
		SHIFT_LEFT=67, SHIFT_RIGHT=68, USHIFT_RIGHT=69, LPAREN=70, RPAREN=71, 
		LBRACKET=72, RBRACKET=73, DECREMENT=74, DIV=75, IDIV=76, INCREMENT=77, 
		MINUS=78, MOD=79, EMOD=80, MUL=81, PLUS=82, POW=83, ASSIGN=84, ASSIGN_BITWISE_AND=85, 
		ASSIGN_BITWISE_OR=86, ASSIGN_BITWISE_XOR=87, ASSIGN_BOOLEAN_AND=88, ASSIGN_BOOLEAN_OR=89, 
		ASSIGN_DIV=90, ASSIGN_IDIV=91, ASSIGN_MINUS=92, ASSIGN_MOD=93, ASSIGN_EMOD=94, 
		ASSIGN_MUL=95, ASSIGN_PLUS=96, ASSIGN_POW=97, ASSIGN_SHIFT_LEFT=98, ASSIGN_SHIFT_RIGHT=99, 
		ASSIGN_USHIFT_RIGHT=100, AT=101, COLON=102, COMMA=103, DOLLAR=104, DOT=105, 
		DOT2=106, DOT3=107, DOUBLEQUOTE=108, QUESTION=109, SEMICOLON=110, IDENTIFIER=111, 
		EXTIDENTIFIER=112, BUILTINIDENTIFIER=113, KEYWORD=114, STRING=115, COLOR=116, 
		NAMEDCOLOR=117, BINARY=118, HEXADECIMAL=119, DECIMAL=120, FLOAT=121, CHAR=122, 
		LBRACE=123, HASHDECLARE=124, HASHSET=125, HASHSETLOCAL=126, FORMATTABLELITERAL=127, 
		RBRACE=128, COMMENTEDCOMMENT=129, ENHANCEDCOMMENT=130, DOC_COMMENT=131, 
		COMMENT=132, EMPTYCOMMENT=133, LINECOMMENT=134, NEWLINE=135, WHITESPACE=136, 
		UNKNOWN_CHAR=137, MLOGCOMMENT=138, MLOGSILENTCOMMENT=139, MLOGSTRING=140, 
		MLOGCOLOR=141, MLOGNAMEDCOLOR=142, MLOGBINARY=143, MLOGHEXADECIMAL=144, 
		MLOGDECIMAL=145, MLOGFLOAT=146, MLOGCHAR=147, MLOGBUILTIN=148, MLOGLABEL=149, 
		MLOGTOKEN=150, MLOGSEPARATOR=151, MLOGWHITESPACE=152, MLOG_UNKNOWN=153, 
		DIRECTIVEVALUE=154, DIRECTIVEASSIGN=155, DIRECTIVECOMMA=156, DIRECTIVECOMMENT=157, 
		DIRECTIVELINECOMMENT=158, DIRECTIVEWHITESPACE=159, TEXT=160, ESCAPESEQUENCE=161, 
		EMPTYPLACEHOLDER=162, INTERPOLATION=163, VARIABLEPLACEHOLDER=164, ENDOFLINE=165, 
		VARIABLE=166, FMTENDOFLINE=167;
	public static final int
		RULE_astModule = 0, RULE_astStatementList = 1, RULE_expressionList = 2, 
		RULE_identifierList = 3, RULE_mlogBlock = 4, RULE_mlogSeparators = 5, 
		RULE_mlogStatement = 6, RULE_mlogInstruction = 7, RULE_mlogTokenOrLiteral = 8, 
		RULE_mlogVariableList = 9, RULE_astMlogVariable = 10, RULE_statement = 11, 
		RULE_declarationOrExpressionList = 12, RULE_variableDeclaration = 13, 
		RULE_declModifier = 14, RULE_typeSpec = 15, RULE_variableSpecList = 16, 
		RULE_variableSpecification = 17, RULE_valueList = 18, RULE_lvalue = 19, 
		RULE_expression = 20, RULE_formattableContents = 21, RULE_formattablePlaceholder = 22, 
		RULE_directive = 23, RULE_directiveValues = 24, RULE_astDirectiveValue = 25, 
		RULE_astKeywordOrBuiltin = 26, RULE_astKeywordOrBuiltinList = 27, RULE_allocations = 28, 
		RULE_astAllocation = 29, RULE_parameterList = 30, RULE_astFunctionParameter = 31, 
		RULE_astFunctionArgument = 32, RULE_astOptionalFunctionArgument = 33, 
		RULE_argumentList = 34, RULE_caseAlternatives = 35, RULE_astCaseAlternative = 36, 
		RULE_whenValueList = 37, RULE_whenValue = 38, RULE_astRange = 39, RULE_elsifBranches = 40, 
		RULE_elsifBranch = 41, RULE_iteratorsValuesGroups = 42, RULE_astIteratorsValuesGroup = 43, 
		RULE_iteratorGroup = 44, RULE_astIterator = 45;
	private static String[] makeRuleNames() {
		return new String[] {
			"astModule", "astStatementList", "expressionList", "identifierList", 
			"mlogBlock", "mlogSeparators", "mlogStatement", "mlogInstruction", "mlogTokenOrLiteral", 
			"mlogVariableList", "astMlogVariable", "statement", "declarationOrExpressionList", 
			"variableDeclaration", "declModifier", "typeSpec", "variableSpecList", 
			"variableSpecification", "valueList", "lvalue", "expression", "formattableContents", 
			"formattablePlaceholder", "directive", "directiveValues", "astDirectiveValue", 
			"astKeywordOrBuiltin", "astKeywordOrBuiltinList", "allocations", "astAllocation", 
			"parameterList", "astFunctionParameter", "astFunctionArgument", "astOptionalFunctionArgument", 
			"argumentList", "caseAlternatives", "astCaseAlternative", "whenValueList", 
			"whenValue", "astRange", "elsifBranches", "elsifBranch", "iteratorsValuesGroups", 
			"astIteratorsValuesGroup", "iteratorGroup", "astIterator"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'atomic'", "'begin'", "'break'", "'cached'", "'case'", 
			"'const'", "'continue'", "'debug'", "'descending'", "'def'", "'do'", 
			"'elif'", "'else'", "'elseif'", "'elsif'", "'end'", "'export'", "'external'", 
			"'false'", "'guarded'", "'for'", "'heap'", "'if'", "'in'", "'inline'", 
			"'linked'", "'module'", "'mlog'", "'noinit'", "'noinline'", "'null'", 
			"'out'", "'param'", "'private'", "'public'", "'ref'", "'remote'", "'require'", 
			"'return'", "'stack'", "'then'", "'true'", "'var'", "'void'", "'volatile'", 
			"'when'", "'while'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'==='", 
			"'!=='", "'&'", "'~'", "'|'", "'^'", "'&&'", "'!'", "'||'", "'and'", 
			"'not'", "'or'", "'<<'", "'>>'", "'>>>'", "'('", "')'", "'['", "']'", 
			"'--'", "'/'", "'\\'", "'++'", "'-'", "'%'", "'%%'", "'*'", "'+'", "'**'", 
			null, "'&='", "'|='", "'^='", "'&&='", "'||='", "'/='", "'\\='", "'-='", 
			"'%='", "'%%='", "'*='", "'+='", "'**='", "'<<='", "'>>='", "'>>>='", 
			"'@'", "':'", null, null, "'.'", "'..'", "'...'", "'\"'", "'?'", null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "'#declare'", "'#set'", "'#setlocal'", null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "'${'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "ATOMIC", "BEGIN", "BREAK", "CACHED", "CASE", "CONST", 
			"CONTINUE", "DEBUG", "DESCENDING", "DEF", "DO", "ELIF", "ELSE", "ELSEIF", 
			"ELSIF", "END", "EXPORT", "EXTERNAL", "FALSE", "GUARDED", "FOR", "HEAP", 
			"IF", "IN", "INLINE", "LINKED", "MODULE", "MLOG", "NOINIT", "NOINLINE", 
			"NULL", "OUT", "PARAM", "PRIVATE", "PUBLIC", "REF", "REMOTE", "REQUIRE", 
			"RETURN", "STACK", "THEN", "TRUE", "VAR", "VOID", "VOLATILE", "WHEN", 
			"WHILE", "EQUAL", "GREATER_THAN", "GREATER_THAN_EQUAL", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "BITWISE_AND", 
			"BITWISE_NOT", "BITWISE_OR", "BITWISE_XOR", "BOOLEAN_AND", "BOOLEAN_NOT", 
			"BOOLEAN_OR", "LOGICAL_AND", "LOGICAL_NOT", "LOGICAL_OR", "SHIFT_LEFT", 
			"SHIFT_RIGHT", "USHIFT_RIGHT", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", 
			"DECREMENT", "DIV", "IDIV", "INCREMENT", "MINUS", "MOD", "EMOD", "MUL", 
			"PLUS", "POW", "ASSIGN", "ASSIGN_BITWISE_AND", "ASSIGN_BITWISE_OR", "ASSIGN_BITWISE_XOR", 
			"ASSIGN_BOOLEAN_AND", "ASSIGN_BOOLEAN_OR", "ASSIGN_DIV", "ASSIGN_IDIV", 
			"ASSIGN_MINUS", "ASSIGN_MOD", "ASSIGN_EMOD", "ASSIGN_MUL", "ASSIGN_PLUS", 
			"ASSIGN_POW", "ASSIGN_SHIFT_LEFT", "ASSIGN_SHIFT_RIGHT", "ASSIGN_USHIFT_RIGHT", 
			"AT", "COLON", "COMMA", "DOLLAR", "DOT", "DOT2", "DOT3", "DOUBLEQUOTE", 
			"QUESTION", "SEMICOLON", "IDENTIFIER", "EXTIDENTIFIER", "BUILTINIDENTIFIER", 
			"KEYWORD", "STRING", "COLOR", "NAMEDCOLOR", "BINARY", "HEXADECIMAL", 
			"DECIMAL", "FLOAT", "CHAR", "LBRACE", "HASHDECLARE", "HASHSET", "HASHSETLOCAL", 
			"FORMATTABLELITERAL", "RBRACE", "COMMENTEDCOMMENT", "ENHANCEDCOMMENT", 
			"DOC_COMMENT", "COMMENT", "EMPTYCOMMENT", "LINECOMMENT", "NEWLINE", "WHITESPACE", 
			"UNKNOWN_CHAR", "MLOGCOMMENT", "MLOGSILENTCOMMENT", "MLOGSTRING", "MLOGCOLOR", 
			"MLOGNAMEDCOLOR", "MLOGBINARY", "MLOGHEXADECIMAL", "MLOGDECIMAL", "MLOGFLOAT", 
			"MLOGCHAR", "MLOGBUILTIN", "MLOGLABEL", "MLOGTOKEN", "MLOGSEPARATOR", 
			"MLOGWHITESPACE", "MLOG_UNKNOWN", "DIRECTIVEVALUE", "DIRECTIVEASSIGN", 
			"DIRECTIVECOMMA", "DIRECTIVECOMMENT", "DIRECTIVELINECOMMENT", "DIRECTIVEWHITESPACE", 
			"TEXT", "ESCAPESEQUENCE", "EMPTYPLACEHOLDER", "INTERPOLATION", "VARIABLEPLACEHOLDER", 
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
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4900331760824228862L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 8935106476331119137L) != 0) || _la==ENHANCEDCOMMENT) {
				{
				setState(92);
				astStatementList();
				}
			}

			setState(95);
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
			setState(101); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(98);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4900331760824228862L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 8935071291959030305L) != 0) || _la==ENHANCEDCOMMENT) {
						{
						setState(97);
						statement();
						}
					}

					setState(100);
					match(SEMICOLON);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(103); 
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
			setState(110);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(105);
					expression(0);
					setState(106);
					match(COMMA);
					}
					} 
				}
				setState(112);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(113);
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
			setState(119);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(115);
					match(IDENTIFIER);
					setState(116);
					match(COMMA);
					}
					} 
				}
				setState(121);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(122);
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
	public static class MlogBlockContext extends ParserRuleContext {
		public List<MlogStatementContext> mlogStatement() {
			return getRuleContexts(MlogStatementContext.class);
		}
		public MlogStatementContext mlogStatement(int i) {
			return getRuleContext(MlogStatementContext.class,i);
		}
		public List<MlogSeparatorsContext> mlogSeparators() {
			return getRuleContexts(MlogSeparatorsContext.class);
		}
		public MlogSeparatorsContext mlogSeparators(int i) {
			return getRuleContext(MlogSeparatorsContext.class,i);
		}
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public List<TerminalNode> MLOGSEPARATOR() { return getTokens(MindcodeParser.MLOGSEPARATOR); }
		public TerminalNode MLOGSEPARATOR(int i) {
			return getToken(MindcodeParser.MLOGSEPARATOR, i);
		}
		public MlogBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mlogBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterMlogBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitMlogBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitMlogBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MlogBlockContext mlogBlock() throws RecognitionException {
		MlogBlockContext _localctx = new MlogBlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_mlogBlock);
		int _la;
		try {
			int _alt;
			setState(163);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(125);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
				case 1:
					{
					setState(124);
					mlogSeparators();
					}
					break;
				}
				setState(132);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(127);
						mlogStatement();
						setState(128);
						mlogSeparators();
						}
						} 
					}
					setState(134);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				}
				setState(135);
				mlogStatement();
				setState(137);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(136);
					mlogSeparators();
					}
					break;
				}
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(139);
					match(MLOGWHITESPACE);
					}
					}
					setState(144);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(154);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(148);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==MLOGWHITESPACE) {
							{
							{
							setState(145);
							match(MLOGWHITESPACE);
							}
							}
							setState(150);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(151);
						match(MLOGSEPARATOR);
						}
						} 
					}
					setState(156);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				}
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(157);
					match(MLOGWHITESPACE);
					}
					}
					setState(162);
					_errHandler.sync(this);
					_la = _input.LA(1);
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
	public static class MlogSeparatorsContext extends ParserRuleContext {
		public List<TerminalNode> MLOGSEPARATOR() { return getTokens(MindcodeParser.MLOGSEPARATOR); }
		public TerminalNode MLOGSEPARATOR(int i) {
			return getToken(MindcodeParser.MLOGSEPARATOR, i);
		}
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public MlogSeparatorsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mlogSeparators; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterMlogSeparators(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitMlogSeparators(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitMlogSeparators(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MlogSeparatorsContext mlogSeparators() throws RecognitionException {
		MlogSeparatorsContext _localctx = new MlogSeparatorsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_mlogSeparators);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(172); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(168);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==MLOGWHITESPACE) {
						{
						{
						setState(165);
						match(MLOGWHITESPACE);
						}
						}
						setState(170);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(171);
					match(MLOGSEPARATOR);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(174); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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
	public static class MlogStatementContext extends ParserRuleContext {
		public MlogStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mlogStatement; }
	 
		public MlogStatementContext() { }
		public void copyFrom(MlogStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogLabelContext extends MlogStatementContext {
		public Token ws;
		public Token label;
		public TerminalNode MLOGLABEL() { return getToken(MindcodeParser.MLOGLABEL, 0); }
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public AstMlogLabelContext(MlogStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogLabel(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogLabelWithCommentContext extends MlogStatementContext {
		public Token ws;
		public Token label;
		public Token whitespace;
		public Token comment;
		public TerminalNode MLOGLABEL() { return getToken(MindcodeParser.MLOGLABEL, 0); }
		public TerminalNode MLOGCOMMENT() { return getToken(MindcodeParser.MLOGCOMMENT, 0); }
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public AstMlogLabelWithCommentContext(MlogStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogLabelWithComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogLabelWithComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogLabelWithComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogCommentContext extends MlogStatementContext {
		public Token ws;
		public Token comment;
		public TerminalNode MLOGCOMMENT() { return getToken(MindcodeParser.MLOGCOMMENT, 0); }
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public AstMlogCommentContext(MlogStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogComment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogInstructionContext extends MlogStatementContext {
		public Token ws;
		public MlogInstructionContext mlogInstruction() {
			return getRuleContext(MlogInstructionContext.class,0);
		}
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public AstMlogInstructionContext(MlogStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogInstruction(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogInstructionWithCommentContext extends MlogStatementContext {
		public Token ws;
		public Token whitespace;
		public Token comment;
		public MlogInstructionContext mlogInstruction() {
			return getRuleContext(MlogInstructionContext.class,0);
		}
		public TerminalNode MLOGCOMMENT() { return getToken(MindcodeParser.MLOGCOMMENT, 0); }
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public AstMlogInstructionWithCommentContext(MlogStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogInstructionWithComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogInstructionWithComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogInstructionWithComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MlogStatementContext mlogStatement() throws RecognitionException {
		MlogStatementContext _localctx = new MlogStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_mlogStatement);
		int _la;
		try {
			setState(220);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new AstMlogLabelContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(176);
					((AstMlogLabelContext)_localctx).ws = match(MLOGWHITESPACE);
					}
					}
					setState(181);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(182);
				((AstMlogLabelContext)_localctx).label = match(MLOGLABEL);
				}
				break;
			case 2:
				_localctx = new AstMlogLabelWithCommentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(186);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(183);
					((AstMlogLabelWithCommentContext)_localctx).ws = match(MLOGWHITESPACE);
					}
					}
					setState(188);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(189);
				((AstMlogLabelWithCommentContext)_localctx).label = match(MLOGLABEL);
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MLOGWHITESPACE) {
					{
					setState(190);
					((AstMlogLabelWithCommentContext)_localctx).whitespace = match(MLOGWHITESPACE);
					}
				}

				setState(193);
				((AstMlogLabelWithCommentContext)_localctx).comment = match(MLOGCOMMENT);
				}
				break;
			case 3:
				_localctx = new AstMlogInstructionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(194);
					((AstMlogInstructionContext)_localctx).ws = match(MLOGWHITESPACE);
					}
					}
					setState(199);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(200);
				mlogInstruction();
				}
				break;
			case 4:
				_localctx = new AstMlogInstructionWithCommentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(201);
					((AstMlogInstructionWithCommentContext)_localctx).ws = match(MLOGWHITESPACE);
					}
					}
					setState(206);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(207);
				mlogInstruction();
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MLOGWHITESPACE) {
					{
					setState(208);
					((AstMlogInstructionWithCommentContext)_localctx).whitespace = match(MLOGWHITESPACE);
					}
				}

				setState(211);
				((AstMlogInstructionWithCommentContext)_localctx).comment = match(MLOGCOMMENT);
				}
				break;
			case 5:
				_localctx = new AstMlogCommentContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==MLOGWHITESPACE) {
					{
					{
					setState(213);
					((AstMlogCommentContext)_localctx).ws = match(MLOGWHITESPACE);
					}
					}
					setState(218);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(219);
				((AstMlogCommentContext)_localctx).comment = match(MLOGCOMMENT);
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
	public static class MlogInstructionContext extends ParserRuleContext {
		public Token opcode;
		public MlogTokenOrLiteralContext mlogTokens;
		public TerminalNode MLOGTOKEN() { return getToken(MindcodeParser.MLOGTOKEN, 0); }
		public List<MlogTokenOrLiteralContext> mlogTokenOrLiteral() {
			return getRuleContexts(MlogTokenOrLiteralContext.class);
		}
		public MlogTokenOrLiteralContext mlogTokenOrLiteral(int i) {
			return getRuleContext(MlogTokenOrLiteralContext.class,i);
		}
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public MlogInstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mlogInstruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterMlogInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitMlogInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitMlogInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MlogInstructionContext mlogInstruction() throws RecognitionException {
		MlogInstructionContext _localctx = new MlogInstructionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_mlogInstruction);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			((MlogInstructionContext)_localctx).opcode = match(MLOGTOKEN);
			setState(231);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(224); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(223);
						match(MLOGWHITESPACE);
						}
						}
						setState(226); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==MLOGWHITESPACE );
					setState(228);
					((MlogInstructionContext)_localctx).mlogTokens = mlogTokenOrLiteral();
					}
					} 
				}
				setState(233);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
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
	public static class MlogTokenOrLiteralContext extends ParserRuleContext {
		public MlogTokenOrLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mlogTokenOrLiteral; }
	 
		public MlogTokenOrLiteralContext() { }
		public void copyFrom(MlogTokenOrLiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogNamedColorContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGNAMEDCOLOR() { return getToken(MindcodeParser.MLOGNAMEDCOLOR, 0); }
		public AstMlogNamedColorContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogNamedColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogNamedColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogNamedColor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogBuiltinContext extends MlogTokenOrLiteralContext {
		public Token builtin;
		public TerminalNode MLOGBUILTIN() { return getToken(MindcodeParser.MLOGBUILTIN, 0); }
		public AstMlogBuiltinContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogBuiltin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogBuiltin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogBuiltin(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogTokenContext extends MlogTokenOrLiteralContext {
		public Token token;
		public TerminalNode MLOGTOKEN() { return getToken(MindcodeParser.MLOGTOKEN, 0); }
		public AstMlogTokenContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogToken(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogToken(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogToken(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogStringContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGSTRING() { return getToken(MindcodeParser.MLOGSTRING, 0); }
		public AstMlogStringContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogString(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogBinaryContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGBINARY() { return getToken(MindcodeParser.MLOGBINARY, 0); }
		public AstMlogBinaryContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogBinary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogBinary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogBinary(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogHexadecimalContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGHEXADECIMAL() { return getToken(MindcodeParser.MLOGHEXADECIMAL, 0); }
		public AstMlogHexadecimalContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogHexadecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogHexadecimal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogHexadecimal(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogCharContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGCHAR() { return getToken(MindcodeParser.MLOGCHAR, 0); }
		public AstMlogCharContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogChar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogChar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogChar(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogColorContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGCOLOR() { return getToken(MindcodeParser.MLOGCOLOR, 0); }
		public AstMlogColorContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogColor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogFloatContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGFLOAT() { return getToken(MindcodeParser.MLOGFLOAT, 0); }
		public AstMlogFloatContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogFloat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogFloat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogFloat(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogDecimalContext extends MlogTokenOrLiteralContext {
		public Token literal;
		public TerminalNode MLOGDECIMAL() { return getToken(MindcodeParser.MLOGDECIMAL, 0); }
		public AstMlogDecimalContext(MlogTokenOrLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogDecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogDecimal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogDecimal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MlogTokenOrLiteralContext mlogTokenOrLiteral() throws RecognitionException {
		MlogTokenOrLiteralContext _localctx = new MlogTokenOrLiteralContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_mlogTokenOrLiteral);
		try {
			setState(244);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MLOGTOKEN:
				_localctx = new AstMlogTokenContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(234);
				((AstMlogTokenContext)_localctx).token = match(MLOGTOKEN);
				}
				break;
			case MLOGBUILTIN:
				_localctx = new AstMlogBuiltinContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(235);
				((AstMlogBuiltinContext)_localctx).builtin = match(MLOGBUILTIN);
				}
				break;
			case MLOGSTRING:
				_localctx = new AstMlogStringContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(236);
				((AstMlogStringContext)_localctx).literal = match(MLOGSTRING);
				}
				break;
			case MLOGCOLOR:
				_localctx = new AstMlogColorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(237);
				((AstMlogColorContext)_localctx).literal = match(MLOGCOLOR);
				}
				break;
			case MLOGNAMEDCOLOR:
				_localctx = new AstMlogNamedColorContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(238);
				((AstMlogNamedColorContext)_localctx).literal = match(MLOGNAMEDCOLOR);
				}
				break;
			case MLOGBINARY:
				_localctx = new AstMlogBinaryContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(239);
				((AstMlogBinaryContext)_localctx).literal = match(MLOGBINARY);
				}
				break;
			case MLOGHEXADECIMAL:
				_localctx = new AstMlogHexadecimalContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(240);
				((AstMlogHexadecimalContext)_localctx).literal = match(MLOGHEXADECIMAL);
				}
				break;
			case MLOGDECIMAL:
				_localctx = new AstMlogDecimalContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(241);
				((AstMlogDecimalContext)_localctx).literal = match(MLOGDECIMAL);
				}
				break;
			case MLOGFLOAT:
				_localctx = new AstMlogFloatContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(242);
				((AstMlogFloatContext)_localctx).literal = match(MLOGFLOAT);
				}
				break;
			case MLOGCHAR:
				_localctx = new AstMlogCharContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(243);
				((AstMlogCharContext)_localctx).literal = match(MLOGCHAR);
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
	public static class MlogVariableListContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public List<AstMlogVariableContext> astMlogVariable() {
			return getRuleContexts(AstMlogVariableContext.class);
		}
		public AstMlogVariableContext astMlogVariable(int i) {
			return getRuleContext(AstMlogVariableContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MindcodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MindcodeParser.COMMA, i);
		}
		public MlogVariableListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mlogVariableList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterMlogVariableList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitMlogVariableList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitMlogVariableList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MlogVariableListContext mlogVariableList() throws RecognitionException {
		MlogVariableListContext _localctx = new MlogVariableListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_mlogVariableList);
		try {
			int _alt;
			setState(260);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(246);
				match(LPAREN);
				setState(247);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				match(LPAREN);
				setState(254);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(249);
						astMlogVariable();
						setState(250);
						match(COMMA);
						}
						} 
					}
					setState(256);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
				}
				setState(257);
				astMlogVariable();
				setState(258);
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
	public static class AstMlogVariableContext extends ParserRuleContext {
		public Token modifier_in;
		public Token modifier_out;
		public Token name;
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode OUT() { return getToken(MindcodeParser.OUT, 0); }
		public AstMlogVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_astMlogVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AstMlogVariableContext astMlogVariable() throws RecognitionException {
		AstMlogVariableContext _localctx = new AstMlogVariableContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_astMlogVariable);
		int _la;
		try {
			setState(272);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(262);
					((AstMlogVariableContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(266);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(265);
					((AstMlogVariableContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(268);
				((AstMlogVariableContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(269);
				((AstMlogVariableContext)_localctx).modifier_out = match(OUT);
				setState(270);
				((AstMlogVariableContext)_localctx).modifier_in = match(IN);
				setState(271);
				((AstMlogVariableContext)_localctx).name = match(IDENTIFIER);
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
	public static class AstDebugBlockContext extends StatementContext {
		public AstStatementListContext exp;
		public TerminalNode DEBUG() { return getToken(MindcodeParser.DEBUG, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstDebugBlockContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDebugBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDebugBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDebugBlock(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AstMlogBlockContext extends StatementContext {
		public MlogVariableListContext variables;
		public MlogBlockContext block;
		public TerminalNode MLOG() { return getToken(MindcodeParser.MLOG, 0); }
		public TerminalNode LBRACE() { return getToken(MindcodeParser.LBRACE, 0); }
		public MlogBlockContext mlogBlock() {
			return getRuleContext(MlogBlockContext.class,0);
		}
		public MlogVariableListContext mlogVariableList() {
			return getRuleContext(MlogVariableListContext.class,0);
		}
		public AstMlogBlockContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstMlogBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstMlogBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstMlogBlock(this);
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
		public Token debug;
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
		public TerminalNode DEBUG() { return getToken(MindcodeParser.DEBUG, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public TerminalNode INLINE() { return getToken(MindcodeParser.INLINE, 0); }
		public TerminalNode NOINLINE() { return getToken(MindcodeParser.NOINLINE, 0); }
		public TerminalNode EXPORT() { return getToken(MindcodeParser.EXPORT, 0); }
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
	@SuppressWarnings("CheckReturnValue")
	public static class AstAtomicBlockContext extends StatementContext {
		public AstStatementListContext exp;
		public TerminalNode ATOMIC() { return getToken(MindcodeParser.ATOMIC, 0); }
		public TerminalNode END() { return getToken(MindcodeParser.END, 0); }
		public AstStatementListContext astStatementList() {
			return getRuleContext(AstStatementListContext.class,0);
		}
		public AstAtomicBlockContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstAtomicBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstAtomicBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstAtomicBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_statement);
		int _la;
		try {
			setState(426);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				_localctx = new AstExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(274);
				expression(0);
				}
				break;
			case 2:
				_localctx = new AstDirectiveContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(275);
				directive();
				}
				break;
			case 3:
				_localctx = new AstVariableDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(276);
				variableDeclaration();
				}
				break;
			case 4:
				_localctx = new AstModuleDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(277);
				match(MODULE);
				setState(278);
				((AstModuleDeclarationContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 5:
				_localctx = new AstEnhancedCommentContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(279);
				match(ENHANCEDCOMMENT);
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 160)) & ~0x3f) == 0 && ((1L << (_la - 160)) & 31L) != 0)) {
					{
					{
					setState(280);
					formattableContents();
					}
					}
					setState(285);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 6:
				_localctx = new AstAllocationsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(286);
				match(ALLOCATE);
				setState(287);
				allocations();
				}
				break;
			case 7:
				_localctx = new AstParameterContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(288);
				match(PARAM);
				setState(289);
				((AstParameterContext)_localctx).name = match(IDENTIFIER);
				setState(290);
				match(ASSIGN);
				setState(291);
				((AstParameterContext)_localctx).value = expression(0);
				}
				break;
			case 8:
				_localctx = new AstRequireFileContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(292);
				match(REQUIRE);
				setState(293);
				((AstRequireFileContext)_localctx).file = match(STRING);
				setState(296);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REMOTE) {
					{
					setState(294);
					match(REMOTE);
					setState(295);
					((AstRequireFileContext)_localctx).processors = identifierList();
					}
				}

				}
				break;
			case 9:
				_localctx = new AstRequireLibraryContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(298);
				match(REQUIRE);
				setState(299);
				((AstRequireLibraryContext)_localctx).library = match(IDENTIFIER);
				setState(302);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REMOTE) {
					{
					setState(300);
					match(REMOTE);
					setState(301);
					((AstRequireLibraryContext)_localctx).processors = identifierList();
					}
				}

				}
				break;
			case 10:
				_localctx = new AstFunctionDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DEBUG) {
					{
					setState(304);
					((AstFunctionDeclarationContext)_localctx).debug = match(DEBUG);
					}
				}

				setState(308);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 277092761600L) != 0)) {
					{
					setState(307);
					((AstFunctionDeclarationContext)_localctx).callType = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 277092761600L) != 0)) ) {
						((AstFunctionDeclarationContext)_localctx).callType = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(310);
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
				setState(311);
				((AstFunctionDeclarationContext)_localctx).name = match(IDENTIFIER);
				setState(312);
				((AstFunctionDeclarationContext)_localctx).params = parameterList();
				setState(314);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(313);
					((AstFunctionDeclarationContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(316);
				match(END);
				}
				break;
			case 11:
				_localctx = new AstForEachLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(318);
					((AstForEachLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(319);
					match(COLON);
					}
				}

				setState(322);
				match(FOR);
				setState(323);
				((AstForEachLoopStatementContext)_localctx).iterators = iteratorsValuesGroups();
				setState(324);
				match(DO);
				setState(326);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(325);
					((AstForEachLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(328);
				match(END);
				}
				break;
			case 12:
				_localctx = new AstIteratedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(330);
					((AstIteratedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(331);
					match(COLON);
					}
				}

				setState(334);
				match(FOR);
				setState(336);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4900013432540889312L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 4899846025835065889L) != 0)) {
					{
					setState(335);
					((AstIteratedForLoopStatementContext)_localctx).init = declarationOrExpressionList();
					}
				}

				setState(338);
				match(SEMICOLON);
				setState(340);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4899925195521916992L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 4899846025835065889L) != 0)) {
					{
					setState(339);
					((AstIteratedForLoopStatementContext)_localctx).condition = expression(0);
					}
				}

				setState(342);
				match(SEMICOLON);
				setState(344);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4899925195521916992L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 4899846025835065889L) != 0)) {
					{
					setState(343);
					((AstIteratedForLoopStatementContext)_localctx).update = expressionList();
					}
				}

				setState(346);
				match(DO);
				setState(348);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
				case 1:
					{
					setState(347);
					((AstIteratedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(350);
				match(END);
				}
				break;
			case 13:
				_localctx = new AstRangedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(353);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(351);
					((AstRangedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(352);
					match(COLON);
					}
				}

				setState(355);
				match(FOR);
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR) {
					{
					setState(356);
					((AstRangedForLoopStatementContext)_localctx).type = typeSpec();
					}
				}

				setState(359);
				((AstRangedForLoopStatementContext)_localctx).control = lvalue();
				setState(360);
				match(IN);
				setState(361);
				((AstRangedForLoopStatementContext)_localctx).range = astRange();
				setState(363);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DESCENDING) {
					{
					setState(362);
					match(DESCENDING);
					}
				}

				setState(365);
				match(DO);
				setState(367);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
				case 1:
					{
					setState(366);
					((AstRangedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(369);
				match(END);
				}
				break;
			case 14:
				_localctx = new AstWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(373);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(371);
					((AstWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(372);
					match(COLON);
					}
				}

				setState(375);
				match(WHILE);
				setState(376);
				((AstWhileLoopStatementContext)_localctx).condition = expression(0);
				setState(377);
				match(DO);
				setState(379);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
				case 1:
					{
					setState(378);
					((AstWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(381);
				match(END);
				}
				break;
			case 15:
				_localctx = new AstDoWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(385);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(383);
					((AstDoWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(384);
					match(COLON);
					}
				}

				setState(387);
				match(DO);
				setState(389);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
				case 1:
					{
					setState(388);
					((AstDoWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(391);
				match(WHILE);
				setState(392);
				((AstDoWhileLoopStatementContext)_localctx).condition = expression(0);
				}
				break;
			case 16:
				_localctx = new AstBreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(393);
				match(BREAK);
				setState(395);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(394);
					((AstBreakStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 17:
				_localctx = new AstContinueStatementContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(397);
				match(CONTINUE);
				setState(399);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(398);
					((AstContinueStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 18:
				_localctx = new AstReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(401);
				match(RETURN);
				setState(403);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4899925195521916992L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 4899846025835065889L) != 0)) {
					{
					setState(402);
					((AstReturnStatementContext)_localctx).value = expression(0);
					}
				}

				}
				break;
			case 19:
				_localctx = new AstAtomicBlockContext(_localctx);
				enterOuterAlt(_localctx, 19);
				{
				setState(405);
				match(ATOMIC);
				setState(407);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
				case 1:
					{
					setState(406);
					((AstAtomicBlockContext)_localctx).exp = astStatementList();
					}
					break;
				}
				setState(409);
				match(END);
				}
				break;
			case 20:
				_localctx = new AstCodeBlockContext(_localctx);
				enterOuterAlt(_localctx, 20);
				{
				setState(410);
				match(BEGIN);
				setState(412);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
				case 1:
					{
					setState(411);
					((AstCodeBlockContext)_localctx).exp = astStatementList();
					}
					break;
				}
				setState(414);
				match(END);
				}
				break;
			case 21:
				_localctx = new AstDebugBlockContext(_localctx);
				enterOuterAlt(_localctx, 21);
				{
				setState(415);
				match(DEBUG);
				setState(417);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
				case 1:
					{
					setState(416);
					((AstDebugBlockContext)_localctx).exp = astStatementList();
					}
					break;
				}
				setState(419);
				match(END);
				}
				break;
			case 22:
				_localctx = new AstMlogBlockContext(_localctx);
				enterOuterAlt(_localctx, 22);
				{
				setState(420);
				match(MLOG);
				setState(422);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LPAREN) {
					{
					setState(421);
					((AstMlogBlockContext)_localctx).variables = mlogVariableList();
					}
				}

				setState(424);
				match(LBRACE);
				setState(425);
				((AstMlogBlockContext)_localctx).block = mlogBlock();
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
		enterRule(_localctx, 24, RULE_declarationOrExpressionList);
		try {
			setState(430);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(428);
				((DeclarationOrExpressionListContext)_localctx).decl = variableDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(429);
				((DeclarationOrExpressionListContext)_localctx).expList = expressionList();
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
		enterRule(_localctx, 26, RULE_variableDeclaration);
		int _la;
		try {
			setState(448);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(435);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 70645369798816L) != 0)) {
					{
					{
					setState(432);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(437);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(438);
				((VariableDeclarationContext)_localctx).type = typeSpec();
				setState(439);
				((VariableDeclarationContext)_localctx).variables = variableSpecList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(442); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(441);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(444); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 70645369798816L) != 0) );
				setState(446);
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
		public ExpressionListContext mlog;
		public Token processor;
		public TerminalNode CONST() { return getToken(MindcodeParser.CONST, 0); }
		public TerminalNode CACHED() { return getToken(MindcodeParser.CACHED, 0); }
		public TerminalNode EXPORT() { return getToken(MindcodeParser.EXPORT, 0); }
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
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public TerminalNode GUARDED() { return getToken(MindcodeParser.GUARDED, 0); }
		public TerminalNode LINKED() { return getToken(MindcodeParser.LINKED, 0); }
		public TerminalNode MLOG() { return getToken(MindcodeParser.MLOG, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
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
		enterRule(_localctx, 28, RULE_declModifier);
		int _la;
		try {
			setState(510);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(450);
				((DeclModifierContext)_localctx).modifier = match(CONST);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(451);
				((DeclModifierContext)_localctx).modifier = match(CACHED);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(452);
				((DeclModifierContext)_localctx).modifier = match(EXPORT);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(453);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(455);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
				case 1:
					{
					setState(454);
					((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
					}
					break;
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(457);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(458);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(459);
				match(LBRACKET);
				setState(460);
				((DeclModifierContext)_localctx).index = expression(0);
				setState(461);
				match(RBRACKET);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(463);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(464);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(465);
				match(LBRACKET);
				setState(466);
				((DeclModifierContext)_localctx).range = astRange();
				setState(467);
				match(RBRACKET);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(469);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(470);
				match(LPAREN);
				setState(472);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(471);
					((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
					}
				}

				setState(474);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(475);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(476);
				match(LPAREN);
				setState(477);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(478);
				match(LBRACKET);
				setState(479);
				((DeclModifierContext)_localctx).index = expression(0);
				setState(480);
				match(RBRACKET);
				setState(481);
				match(RPAREN);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(483);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(484);
				match(LPAREN);
				setState(485);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(486);
				match(LBRACKET);
				setState(487);
				((DeclModifierContext)_localctx).range = astRange();
				setState(488);
				match(RBRACKET);
				setState(489);
				match(RPAREN);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(491);
				((DeclModifierContext)_localctx).modifier = match(GUARDED);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(492);
				((DeclModifierContext)_localctx).modifier = match(LINKED);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(493);
				((DeclModifierContext)_localctx).modifier = match(MLOG);
				setState(494);
				match(LPAREN);
				setState(495);
				((DeclModifierContext)_localctx).mlog = expressionList();
				setState(496);
				match(RPAREN);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(498);
				((DeclModifierContext)_localctx).modifier = match(NOINIT);
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(499);
				((DeclModifierContext)_localctx).modifier = match(REMOTE);
				setState(501);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
				case 1:
					{
					setState(500);
					((DeclModifierContext)_localctx).processor = match(IDENTIFIER);
					}
					break;
				}
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(503);
				((DeclModifierContext)_localctx).modifier = match(REMOTE);
				setState(504);
				match(LPAREN);
				setState(506);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(505);
					((DeclModifierContext)_localctx).processor = match(IDENTIFIER);
					}
				}

				setState(508);
				match(RPAREN);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(509);
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
		enterRule(_localctx, 30, RULE_typeSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
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
		enterRule(_localctx, 32, RULE_variableSpecList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(514);
					variableSpecification();
					setState(515);
					match(COMMA);
					}
					} 
				}
				setState(521);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,69,_ctx);
			}
			setState(522);
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
		enterRule(_localctx, 34, RULE_variableSpecification);
		int _la;
		try {
			setState(539);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(524);
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
				setState(527);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(525);
					match(ASSIGN);
					setState(526);
					((VariableSpecificationContext)_localctx).exp = expression(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(529);
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
				setState(530);
				match(LBRACKET);
				setState(532);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4899925195521916992L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 4899846025835065889L) != 0)) {
					{
					setState(531);
					((VariableSpecificationContext)_localctx).length = expression(0);
					}
				}

				setState(534);
				match(RBRACKET);
				setState(537);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(535);
					match(ASSIGN);
					setState(536);
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
		enterRule(_localctx, 36, RULE_valueList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			match(LPAREN);
			setState(542);
			((ValueListContext)_localctx).values = expressionList();
			setState(543);
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
		enterRule(_localctx, 38, RULE_lvalue);
		int _la;
		try {
			setState(572);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				_localctx = new AstIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(545);
				((AstIdentifierContext)_localctx).id = match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new AstIdentifierExtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(546);
				((AstIdentifierExtContext)_localctx).id = match(EXTIDENTIFIER);
				}
				break;
			case 3:
				_localctx = new AstBuiltInIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(547);
				((AstBuiltInIdentifierContext)_localctx).builtin = match(BUILTINIDENTIFIER);
				}
				break;
			case 4:
				_localctx = new AstArrayAccessContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(548);
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
				setState(549);
				match(LBRACKET);
				setState(550);
				((AstArrayAccessContext)_localctx).index = expression(0);
				setState(551);
				match(RBRACKET);
				}
				break;
			case 5:
				_localctx = new AstSubarrayContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(553);
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
				setState(554);
				match(LBRACKET);
				setState(555);
				((AstSubarrayContext)_localctx).range = astRange();
				setState(556);
				match(RBRACKET);
				}
				break;
			case 6:
				_localctx = new AstRemoteArrayContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(558);
				((AstRemoteArrayContext)_localctx).processor = match(IDENTIFIER);
				setState(559);
				match(DOT);
				setState(560);
				((AstRemoteArrayContext)_localctx).array = match(IDENTIFIER);
				setState(561);
				match(LBRACKET);
				setState(562);
				((AstRemoteArrayContext)_localctx).index = expression(0);
				setState(563);
				match(RBRACKET);
				}
				break;
			case 7:
				_localctx = new AstRemoteSubarrayContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(565);
				((AstRemoteSubarrayContext)_localctx).processor = match(IDENTIFIER);
				setState(566);
				match(DOT);
				setState(567);
				((AstRemoteSubarrayContext)_localctx).array = match(IDENTIFIER);
				setState(568);
				match(LBRACKET);
				setState(569);
				((AstRemoteSubarrayContext)_localctx).range = astRange();
				setState(570);
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
	public static class AstOperatorBinaryOrContext extends ExpressionContext {
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
		public AstOperatorBinaryOrContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryOr(this);
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
	public static class AstOperatorBinaryInRangeContext extends ExpressionContext {
		public ExpressionContext value;
		public Token negation;
		public Token op;
		public ExpressionContext firstValue;
		public Token operator;
		public ExpressionContext lastValue;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public TerminalNode DOT2() { return getToken(MindcodeParser.DOT2, 0); }
		public TerminalNode DOT3() { return getToken(MindcodeParser.DOT3, 0); }
		public TerminalNode BOOLEAN_NOT() { return getToken(MindcodeParser.BOOLEAN_NOT, 0); }
		public TerminalNode LOGICAL_NOT() { return getToken(MindcodeParser.LOGICAL_NOT, 0); }
		public AstOperatorBinaryInRangeContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryInRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryInRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryInRange(this);
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
	public static class AstOperatorBinaryAndContext extends ExpressionContext {
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
		public AstOperatorBinaryAndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryAnd(this);
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
		public Token string;
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
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(MindcodeParser.IDENTIFIER, 0); }
		public TerminalNode MLOG() { return getToken(MindcodeParser.MLOG, 0); }
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
	public static class AstOperatorBinaryInListContext extends ExpressionContext {
		public ExpressionContext value;
		public Token negation;
		public Token op;
		public WhenValueListContext values;
		public TerminalNode LPAREN() { return getToken(MindcodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MindcodeParser.RPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IN() { return getToken(MindcodeParser.IN, 0); }
		public WhenValueListContext whenValueList() {
			return getRuleContext(WhenValueListContext.class,0);
		}
		public TerminalNode BOOLEAN_NOT() { return getToken(MindcodeParser.BOOLEAN_NOT, 0); }
		public TerminalNode LOGICAL_NOT() { return getToken(MindcodeParser.LOGICAL_NOT, 0); }
		public AstOperatorBinaryInListContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstOperatorBinaryInList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstOperatorBinaryInList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstOperatorBinaryInList(this);
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
		int _startState = 40;
		enterRecursionRule(_localctx, 40, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(639);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				_localctx = new ExpLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(575);
				lvalue();
				}
				break;
			case 2:
				{
				_localctx = new AstKeywordContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(576);
				match(KEYWORD);
				}
				break;
			case 3:
				{
				_localctx = new AstFunctionCallEndContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(577);
				match(END);
				setState(578);
				match(LPAREN);
				setState(579);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new AstFunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(580);
				((AstFunctionCallContext)_localctx).function = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==MLOG || _la==IDENTIFIER) ) {
					((AstFunctionCallContext)_localctx).function = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(581);
				((AstFunctionCallContext)_localctx).args = argumentList();
				}
				break;
			case 5:
				{
				_localctx = new AstCaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(582);
				match(CASE);
				setState(583);
				((AstCaseExpressionContext)_localctx).exp = expression(0);
				setState(585);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHEN) {
					{
					setState(584);
					((AstCaseExpressionContext)_localctx).alternatives = caseAlternatives();
					}
				}

				setState(589);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(587);
					match(ELSE);
					setState(588);
					((AstCaseExpressionContext)_localctx).elseBranch = astStatementList();
					}
				}

				setState(591);
				match(END);
				}
				break;
			case 6:
				{
				_localctx = new AstIfExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(593);
				match(IF);
				setState(594);
				((AstIfExpressionContext)_localctx).condition = expression(0);
				setState(595);
				match(THEN);
				setState(597);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
				case 1:
					{
					setState(596);
					((AstIfExpressionContext)_localctx).trueBranch = astStatementList();
					}
					break;
				}
				setState(600);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSIF) {
					{
					setState(599);
					((AstIfExpressionContext)_localctx).elsif = elsifBranches();
					}
				}

				setState(606);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(602);
					match(ELSE);
					setState(604);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
					case 1:
						{
						setState(603);
						((AstIfExpressionContext)_localctx).falseBranch = astStatementList();
						}
						break;
					}
					}
				}

				setState(608);
				match(END);
				}
				break;
			case 7:
				{
				_localctx = new AstFormattableLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(610);
				match(FORMATTABLELITERAL);
				setState(614);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 160)) & ~0x3f) == 0 && ((1L << (_la - 160)) & 31L) != 0)) {
					{
					{
					setState(611);
					formattableContents();
					}
					}
					setState(616);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(617);
				match(DOUBLEQUOTE);
				}
				break;
			case 8:
				{
				_localctx = new AstLiteralStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(618);
				((AstLiteralStringContext)_localctx).string = match(STRING);
				}
				break;
			case 9:
				{
				_localctx = new AstLiteralColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(619);
				match(COLOR);
				}
				break;
			case 10:
				{
				_localctx = new AstLiteralNamedColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(620);
				match(NAMEDCOLOR);
				}
				break;
			case 11:
				{
				_localctx = new AstLiteralBinaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(621);
				match(BINARY);
				}
				break;
			case 12:
				{
				_localctx = new AstLiteralHexadecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(622);
				match(HEXADECIMAL);
				}
				break;
			case 13:
				{
				_localctx = new AstLiteralDecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(623);
				match(DECIMAL);
				}
				break;
			case 14:
				{
				_localctx = new AstLiteralFloatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(624);
				match(FLOAT);
				}
				break;
			case 15:
				{
				_localctx = new AstLiteralCharContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(625);
				match(CHAR);
				}
				break;
			case 16:
				{
				_localctx = new AstLiteralNullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(626);
				match(NULL);
				}
				break;
			case 17:
				{
				_localctx = new AstLiteralBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(627);
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
				setState(628);
				((AstOperatorIncDecPostfixContext)_localctx).exp = lvalue();
				setState(629);
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
				setState(631);
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
				setState(632);
				((AstOperatorIncDecPrefixContext)_localctx).exp = lvalue();
				}
				break;
			case 20:
				{
				_localctx = new AstOperatorUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(633);
				((AstOperatorUnaryContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 58)) & ~0x3f) == 0 && ((1L << (_la - 58)) & 17825937L) != 0)) ) {
					((AstOperatorUnaryContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(634);
				((AstOperatorUnaryContext)_localctx).exp = expression(16);
				}
				break;
			case 21:
				{
				_localctx = new AstParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(635);
				match(LPAREN);
				setState(636);
				((AstParenthesesContext)_localctx).exp = expression(0);
				setState(637);
				match(RPAREN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(710);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(708);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
					case 1:
						{
						_localctx = new AstOperatorBinaryExpContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryExpContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(641);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(642);
						((AstOperatorBinaryExpContext)_localctx).op = match(POW);
						setState(643);
						((AstOperatorBinaryExpContext)_localctx).right = expression(16);
						}
						break;
					case 2:
						{
						_localctx = new AstOperatorBinaryMulContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryMulContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(644);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(645);
						((AstOperatorBinaryMulContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & 115L) != 0)) ) {
							((AstOperatorBinaryMulContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(646);
						((AstOperatorBinaryMulContext)_localctx).right = expression(15);
						}
						break;
					case 3:
						{
						_localctx = new AstOperatorBinaryAddContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryAddContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(647);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(648);
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
						setState(649);
						((AstOperatorBinaryAddContext)_localctx).right = expression(14);
						}
						break;
					case 4:
						{
						_localctx = new AstOperatorBinaryShiftContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryShiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(650);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(651);
						((AstOperatorBinaryShiftContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & 7L) != 0)) ) {
							((AstOperatorBinaryShiftContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(652);
						((AstOperatorBinaryShiftContext)_localctx).right = expression(13);
						}
						break;
					case 5:
						{
						_localctx = new AstOperatorBinaryBitwiseAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(653);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(654);
						((AstOperatorBinaryBitwiseAndContext)_localctx).op = match(BITWISE_AND);
						setState(655);
						((AstOperatorBinaryBitwiseAndContext)_localctx).right = expression(12);
						}
						break;
					case 6:
						{
						_localctx = new AstOperatorBinaryBitwiseOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(656);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(657);
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
						setState(658);
						((AstOperatorBinaryBitwiseOrContext)_localctx).right = expression(11);
						}
						break;
					case 7:
						{
						_localctx = new AstOperatorBinaryInRangeContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryInRangeContext)_localctx).value = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(659);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(661);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==BOOLEAN_NOT || _la==LOGICAL_NOT) {
							{
							setState(660);
							((AstOperatorBinaryInRangeContext)_localctx).negation = _input.LT(1);
							_la = _input.LA(1);
							if ( !(_la==BOOLEAN_NOT || _la==LOGICAL_NOT) ) {
								((AstOperatorBinaryInRangeContext)_localctx).negation = (Token)_errHandler.recoverInline(this);
							}
							else {
								if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
							}
						}

						setState(663);
						((AstOperatorBinaryInRangeContext)_localctx).op = match(IN);
						setState(664);
						((AstOperatorBinaryInRangeContext)_localctx).firstValue = expression(0);
						setState(665);
						((AstOperatorBinaryInRangeContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==DOT2 || _la==DOT3) ) {
							((AstOperatorBinaryInRangeContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(666);
						((AstOperatorBinaryInRangeContext)_localctx).lastValue = expression(10);
						}
						break;
					case 8:
						{
						_localctx = new AstOperatorBinaryInequalityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryInequalityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(668);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(669);
						((AstOperatorBinaryInequalityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16888498602639360L) != 0)) ) {
							((AstOperatorBinaryInequalityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(670);
						((AstOperatorBinaryInequalityContext)_localctx).right = expression(8);
						}
						break;
					case 9:
						{
						_localctx = new AstOperatorBinaryEqualityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryEqualityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(671);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(672);
						((AstOperatorBinaryEqualityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 126663739519795200L) != 0)) ) {
							((AstOperatorBinaryEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(673);
						((AstOperatorBinaryEqualityContext)_localctx).right = expression(7);
						}
						break;
					case 10:
						{
						_localctx = new AstOperatorBinaryAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(674);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(675);
						((AstOperatorBinaryAndContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BOOLEAN_AND || _la==LOGICAL_AND) ) {
							((AstOperatorBinaryAndContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(676);
						((AstOperatorBinaryAndContext)_localctx).right = expression(6);
						}
						break;
					case 11:
						{
						_localctx = new AstOperatorBinaryOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(677);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(678);
						((AstOperatorBinaryOrContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==BOOLEAN_OR || _la==LOGICAL_OR) ) {
							((AstOperatorBinaryOrContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(679);
						((AstOperatorBinaryOrContext)_localctx).right = expression(5);
						}
						break;
					case 12:
						{
						_localctx = new AstOperatorTernaryContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorTernaryContext)_localctx).condition = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(680);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(681);
						match(QUESTION);
						setState(682);
						((AstOperatorTernaryContext)_localctx).trueBranch = expression(0);
						setState(683);
						match(COLON);
						setState(684);
						((AstOperatorTernaryContext)_localctx).falseBranch = expression(3);
						}
						break;
					case 13:
						{
						_localctx = new AstAssignmentContext(new ExpressionContext(_parentctx, _parentState));
						((AstAssignmentContext)_localctx).target = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(686);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(687);
						((AstAssignmentContext)_localctx).operation = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & 131071L) != 0)) ) {
							((AstAssignmentContext)_localctx).operation = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(688);
						((AstAssignmentContext)_localctx).value = expression(2);
						}
						break;
					case 14:
						{
						_localctx = new AstMethodCallContext(new ExpressionContext(_parentctx, _parentState));
						((AstMethodCallContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(689);
						if (!(precpred(_ctx, 34))) throw new FailedPredicateException(this, "precpred(_ctx, 34)");
						setState(690);
						match(DOT);
						setState(691);
						((AstMethodCallContext)_localctx).function = match(IDENTIFIER);
						setState(692);
						((AstMethodCallContext)_localctx).args = argumentList();
						}
						break;
					case 15:
						{
						_localctx = new AstMemberAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstMemberAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(693);
						if (!(precpred(_ctx, 33))) throw new FailedPredicateException(this, "precpred(_ctx, 33)");
						setState(694);
						match(DOT);
						setState(695);
						((AstMemberAccessContext)_localctx).member = match(IDENTIFIER);
						}
						break;
					case 16:
						{
						_localctx = new AstPropertyAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstPropertyAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(696);
						if (!(precpred(_ctx, 32))) throw new FailedPredicateException(this, "precpred(_ctx, 32)");
						setState(697);
						match(DOT);
						setState(698);
						((AstPropertyAccessContext)_localctx).property = match(BUILTINIDENTIFIER);
						}
						break;
					case 17:
						{
						_localctx = new AstOperatorBinaryInListContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryInListContext)_localctx).value = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(699);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(701);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==BOOLEAN_NOT || _la==LOGICAL_NOT) {
							{
							setState(700);
							((AstOperatorBinaryInListContext)_localctx).negation = _input.LT(1);
							_la = _input.LA(1);
							if ( !(_la==BOOLEAN_NOT || _la==LOGICAL_NOT) ) {
								((AstOperatorBinaryInListContext)_localctx).negation = (Token)_errHandler.recoverInline(this);
							}
							else {
								if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
							}
						}

						setState(703);
						((AstOperatorBinaryInListContext)_localctx).op = match(IN);
						setState(704);
						match(LPAREN);
						setState(705);
						((AstOperatorBinaryInListContext)_localctx).values = whenValueList();
						setState(706);
						match(RPAREN);
						}
						break;
					}
					} 
				}
				setState(712);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
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
		enterRule(_localctx, 42, RULE_formattableContents);
		try {
			setState(720);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT:
				_localctx = new FormattableTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(713);
				match(TEXT);
				}
				break;
			case ESCAPESEQUENCE:
				_localctx = new FormattableEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(714);
				match(ESCAPESEQUENCE);
				}
				break;
			case INTERPOLATION:
				_localctx = new FormattableInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(715);
				match(INTERPOLATION);
				setState(716);
				expression(0);
				setState(717);
				match(RBRACE);
				}
				break;
			case EMPTYPLACEHOLDER:
			case VARIABLEPLACEHOLDER:
				_localctx = new PlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(719);
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
		enterRule(_localctx, 44, RULE_formattablePlaceholder);
		int _la;
		try {
			setState(727);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EMPTYPLACEHOLDER:
				_localctx = new FormattablePlaceholderEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(722);
				match(EMPTYPLACEHOLDER);
				}
				break;
			case VARIABLEPLACEHOLDER:
				_localctx = new FormattablePlaceholderVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(723);
				match(VARIABLEPLACEHOLDER);
				setState(725);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VARIABLE) {
					{
					setState(724);
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
	@SuppressWarnings("CheckReturnValue")
	public static class AstDirectiveSetLocalContext extends DirectiveContext {
		public AstDirectiveValueContext option;
		public DirectiveValuesContext value;
		public TerminalNode HASHSETLOCAL() { return getToken(MindcodeParser.HASHSETLOCAL, 0); }
		public AstDirectiveValueContext astDirectiveValue() {
			return getRuleContext(AstDirectiveValueContext.class,0);
		}
		public TerminalNode DIRECTIVEASSIGN() { return getToken(MindcodeParser.DIRECTIVEASSIGN, 0); }
		public DirectiveValuesContext directiveValues() {
			return getRuleContext(DirectiveValuesContext.class,0);
		}
		public AstDirectiveSetLocalContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).enterAstDirectiveSetLocal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MindcodeParserListener ) ((MindcodeParserListener)listener).exitAstDirectiveSetLocal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MindcodeParserVisitor ) return ((MindcodeParserVisitor<? extends T>)visitor).visitAstDirectiveSetLocal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_directive);
		int _la;
		try {
			setState(744);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HASHSET:
				_localctx = new AstDirectiveSetContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(729);
				match(HASHSET);
				setState(730);
				((AstDirectiveSetContext)_localctx).option = astDirectiveValue();
				setState(733);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DIRECTIVEASSIGN) {
					{
					setState(731);
					match(DIRECTIVEASSIGN);
					setState(732);
					((AstDirectiveSetContext)_localctx).value = directiveValues();
					}
				}

				}
				break;
			case HASHSETLOCAL:
				_localctx = new AstDirectiveSetLocalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(735);
				match(HASHSETLOCAL);
				setState(736);
				((AstDirectiveSetLocalContext)_localctx).option = astDirectiveValue();
				setState(739);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DIRECTIVEASSIGN) {
					{
					setState(737);
					match(DIRECTIVEASSIGN);
					setState(738);
					((AstDirectiveSetLocalContext)_localctx).value = directiveValues();
					}
				}

				}
				break;
			case HASHDECLARE:
				_localctx = new AstDirectiveDeclareContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(741);
				match(HASHDECLARE);
				setState(742);
				((AstDirectiveDeclareContext)_localctx).category = match(IDENTIFIER);
				setState(743);
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
		enterRule(_localctx, 48, RULE_directiveValues);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(751);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(746);
					astDirectiveValue();
					setState(747);
					match(DIRECTIVECOMMA);
					}
					} 
				}
				setState(753);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			}
			setState(754);
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
		enterRule(_localctx, 50, RULE_astDirectiveValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(756);
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
		enterRule(_localctx, 52, RULE_astKeywordOrBuiltin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(758);
			_la = _input.LA(1);
			if ( !(((((_la - 111)) & ~0x3f) == 0 && ((1L << (_la - 111)) & 13L) != 0)) ) {
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
		enterRule(_localctx, 54, RULE_astKeywordOrBuiltinList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(765);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(760);
					astKeywordOrBuiltin();
					setState(761);
					match(COMMA);
					}
					} 
				}
				setState(767);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			}
			setState(768);
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
		enterRule(_localctx, 56, RULE_allocations);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(775);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,95,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(770);
					astAllocation();
					setState(771);
					match(COMMA);
					}
					} 
				}
				setState(777);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,95,_ctx);
			}
			setState(778);
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
		enterRule(_localctx, 58, RULE_astAllocation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(780);
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
			setState(781);
			match(IN);
			setState(782);
			((AstAllocationContext)_localctx).id = match(IDENTIFIER);
			setState(787);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACKET) {
				{
				setState(783);
				match(LBRACKET);
				setState(784);
				((AstAllocationContext)_localctx).range = astRange();
				setState(785);
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
		enterRule(_localctx, 60, RULE_parameterList);
		try {
			int _alt;
			setState(803);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(789);
				match(LPAREN);
				setState(790);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(791);
				match(LPAREN);
				setState(797);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,97,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(792);
						astFunctionParameter();
						setState(793);
						match(COMMA);
						}
						} 
					}
					setState(799);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,97,_ctx);
				}
				setState(800);
				astFunctionParameter();
				setState(801);
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
		enterRule(_localctx, 62, RULE_astFunctionParameter);
		int _la;
		try {
			setState(826);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(806);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(805);
					((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(809);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(808);
					((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(811);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(813);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(812);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(815);
				((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
				setState(816);
				((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
				setState(817);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(819);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(818);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(821);
				((AstFunctionParameterContext)_localctx).modifier_ref = match(REF);
				setState(822);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(824);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(823);
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
		enterRule(_localctx, 64, RULE_astFunctionArgument);
		int _la;
		try {
			setState(840);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(829);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(828);
					((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(832);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(831);
					((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(834);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(835);
				((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
				setState(836);
				((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
				setState(837);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(838);
				((AstFunctionArgumentContext)_localctx).modifier_ref = match(REF);
				setState(839);
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
		enterRule(_localctx, 66, RULE_astOptionalFunctionArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(843);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4899925341584359488L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 4899846025835065889L) != 0)) {
				{
				setState(842);
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
		enterRule(_localctx, 68, RULE_argumentList);
		try {
			int _alt;
			setState(862);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(845);
				match(LPAREN);
				setState(846);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(847);
				match(LPAREN);
				setState(848);
				astFunctionArgument();
				setState(849);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(851);
				match(LPAREN);
				setState(855); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(852);
						astOptionalFunctionArgument();
						setState(853);
						match(COMMA);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(857); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,109,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(859);
				astOptionalFunctionArgument();
				setState(860);
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
		enterRule(_localctx, 70, RULE_caseAlternatives);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(865); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(864);
				astCaseAlternative();
				}
				}
				setState(867); 
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
		enterRule(_localctx, 72, RULE_astCaseAlternative);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(869);
			match(WHEN);
			setState(870);
			((AstCaseAlternativeContext)_localctx).values = whenValueList();
			setState(871);
			match(THEN);
			setState(873);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				{
				setState(872);
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
		enterRule(_localctx, 74, RULE_whenValueList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(880);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(875);
					whenValue();
					setState(876);
					match(COMMA);
					}
					} 
				}
				setState(882);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
			}
			setState(883);
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
		enterRule(_localctx, 76, RULE_whenValue);
		try {
			setState(887);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(885);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(886);
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
		enterRule(_localctx, 78, RULE_astRange);
		try {
			setState(897);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(889);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(890);
				((AstRangeContext)_localctx).operator = match(DOT2);
				setState(891);
				((AstRangeContext)_localctx).lastValue = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(893);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(894);
				((AstRangeContext)_localctx).operator = match(DOT3);
				setState(895);
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
		enterRule(_localctx, 80, RULE_elsifBranches);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(900); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(899);
				elsifBranch();
				}
				}
				setState(902); 
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
		enterRule(_localctx, 82, RULE_elsifBranch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(904);
			match(ELSIF);
			setState(905);
			((ElsifBranchContext)_localctx).condition = expression(0);
			setState(906);
			match(THEN);
			setState(908);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,117,_ctx) ) {
			case 1:
				{
				setState(907);
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
		enterRule(_localctx, 84, RULE_iteratorsValuesGroups);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(915);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(910);
					astIteratorsValuesGroup();
					setState(911);
					match(SEMICOLON);
					}
					} 
				}
				setState(917);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,118,_ctx);
			}
			setState(918);
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
		enterRule(_localctx, 86, RULE_astIteratorsValuesGroup);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(920);
			iteratorGroup();
			setState(921);
			match(IN);
			setState(922);
			expressionList();
			setState(924);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DESCENDING) {
				{
				setState(923);
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
		enterRule(_localctx, 88, RULE_iteratorGroup);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(927);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR) {
				{
				setState(926);
				((IteratorGroupContext)_localctx).type = typeSpec();
				}
			}

			setState(934);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(929);
					astIterator();
					setState(930);
					match(COMMA);
					}
					} 
				}
				setState(936);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,121,_ctx);
			}
			setState(937);
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
		enterRule(_localctx, 90, RULE_astIterator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(940);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OUT) {
				{
				setState(939);
				((AstIteratorContext)_localctx).modifier = match(OUT);
				}
			}

			setState(942);
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
		case 20:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 15);
		case 1:
			return precpred(_ctx, 14);
		case 2:
			return precpred(_ctx, 13);
		case 3:
			return precpred(_ctx, 12);
		case 4:
			return precpred(_ctx, 11);
		case 5:
			return precpred(_ctx, 10);
		case 6:
			return precpred(_ctx, 9);
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
			return precpred(_ctx, 2);
		case 13:
			return precpred(_ctx, 34);
		case 14:
			return precpred(_ctx, 33);
		case 15:
			return precpred(_ctx, 32);
		case 16:
			return precpred(_ctx, 8);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u00a7\u03b1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007"+
		",\u0002-\u0007-\u0001\u0000\u0003\u0000^\b\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0003\u0001c\b\u0001\u0001\u0001\u0004\u0001f\b\u0001\u000b"+
		"\u0001\f\u0001g\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002m\b\u0002"+
		"\n\u0002\f\u0002p\t\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0005\u0003v\b\u0003\n\u0003\f\u0003y\t\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0003\u0004~\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0005\u0004\u0083\b\u0004\n\u0004\f\u0004\u0086\t\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004\u008a\b\u0004\u0001\u0004\u0005\u0004\u008d\b\u0004"+
		"\n\u0004\f\u0004\u0090\t\u0004\u0001\u0004\u0005\u0004\u0093\b\u0004\n"+
		"\u0004\f\u0004\u0096\t\u0004\u0001\u0004\u0005\u0004\u0099\b\u0004\n\u0004"+
		"\f\u0004\u009c\t\u0004\u0001\u0004\u0005\u0004\u009f\b\u0004\n\u0004\f"+
		"\u0004\u00a2\t\u0004\u0003\u0004\u00a4\b\u0004\u0001\u0005\u0005\u0005"+
		"\u00a7\b\u0005\n\u0005\f\u0005\u00aa\t\u0005\u0001\u0005\u0004\u0005\u00ad"+
		"\b\u0005\u000b\u0005\f\u0005\u00ae\u0001\u0006\u0005\u0006\u00b2\b\u0006"+
		"\n\u0006\f\u0006\u00b5\t\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00b9"+
		"\b\u0006\n\u0006\f\u0006\u00bc\t\u0006\u0001\u0006\u0001\u0006\u0003\u0006"+
		"\u00c0\b\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00c4\b\u0006\n\u0006"+
		"\f\u0006\u00c7\t\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00cb\b\u0006"+
		"\n\u0006\f\u0006\u00ce\t\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00d2"+
		"\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u00d7\b\u0006"+
		"\n\u0006\f\u0006\u00da\t\u0006\u0001\u0006\u0003\u0006\u00dd\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0004\u0007\u00e1\b\u0007\u000b\u0007\f\u0007\u00e2"+
		"\u0001\u0007\u0005\u0007\u00e6\b\u0007\n\u0007\f\u0007\u00e9\t\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0003\b\u00f5\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005"+
		"\t\u00fd\b\t\n\t\f\t\u0100\t\t\u0001\t\u0001\t\u0001\t\u0003\t\u0105\b"+
		"\t\u0001\n\u0003\n\u0108\b\n\u0001\n\u0003\n\u010b\b\n\u0001\n\u0001\n"+
		"\u0001\n\u0001\n\u0003\n\u0111\b\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u011a\b\u000b"+
		"\n\u000b\f\u000b\u011d\t\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u0129\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u012f\b\u000b\u0001\u000b\u0003\u000b\u0132\b\u000b"+
		"\u0001\u000b\u0003\u000b\u0135\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u013b\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0141\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0147\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u014d\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0151\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0155\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\u0159\b\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000b\u015d\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0162"+
		"\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0166\b\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u016c\b\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0170\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0176\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u017c\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0182\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0186\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u018c\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0190\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\u0194\b\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000b\u0198\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u019d"+
		"\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u01a2\b\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u01a7\b\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u01ab\b\u000b\u0001\f\u0001\f\u0003\f\u01af\b"+
		"\f\u0001\r\u0005\r\u01b2\b\r\n\r\f\r\u01b5\t\r\u0001\r\u0001\r\u0001\r"+
		"\u0001\r\u0004\r\u01bb\b\r\u000b\r\f\r\u01bc\u0001\r\u0001\r\u0003\r\u01c1"+
		"\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003"+
		"\u000e\u01c8\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u01d9"+
		"\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u01f6\b\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u01fb\b\u000e\u0001\u000e\u0001"+
		"\u000e\u0003\u000e\u01ff\b\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0005\u0010\u0206\b\u0010\n\u0010\f\u0010\u0209\t\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011"+
		"\u0210\b\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u0215\b"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u021a\b\u0011\u0003"+
		"\u0011\u021c\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u023d\b\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u024a\b\u0014\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u024e\b\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u0256\b\u0014\u0001"+
		"\u0014\u0003\u0014\u0259\b\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u025d"+
		"\b\u0014\u0003\u0014\u025f\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0005\u0014\u0265\b\u0014\n\u0014\f\u0014\u0268\t\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u0280\b\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u0296\b\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003"+
		"\u0014\u02be\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0005\u0014\u02c5\b\u0014\n\u0014\f\u0014\u02c8\t\u0014\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0003\u0015\u02d1\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u02d6\b\u0016\u0003\u0016\u02d8\b\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0003\u0017\u02de\b\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0003\u0017\u02e4\b\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0003\u0017\u02e9\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0005"+
		"\u0018\u02ee\b\u0018\n\u0018\f\u0018\u02f1\t\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0005\u001b\u02fc\b\u001b\n\u001b\f\u001b\u02ff\t\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u0306"+
		"\b\u001c\n\u001c\f\u001c\u0309\t\u001c\u0001\u001c\u0001\u001c\u0001\u001d"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0003\u001d\u0314\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0005\u001e\u031c\b\u001e\n\u001e\f\u001e\u031f"+
		"\t\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u0324\b\u001e"+
		"\u0001\u001f\u0003\u001f\u0327\b\u001f\u0001\u001f\u0003\u001f\u032a\b"+
		"\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u032e\b\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0334\b\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0003\u001f\u0339\b\u001f\u0003\u001f\u033b\b\u001f"+
		"\u0001 \u0003 \u033e\b \u0001 \u0003 \u0341\b \u0001 \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0003 \u0349\b \u0001!\u0003!\u034c\b!\u0001\"\u0001\""+
		"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0004"+
		"\"\u0358\b\"\u000b\"\f\"\u0359\u0001\"\u0001\"\u0001\"\u0003\"\u035f\b"+
		"\"\u0001#\u0004#\u0362\b#\u000b#\f#\u0363\u0001$\u0001$\u0001$\u0001$"+
		"\u0003$\u036a\b$\u0001%\u0001%\u0001%\u0005%\u036f\b%\n%\f%\u0372\t%\u0001"+
		"%\u0001%\u0001&\u0001&\u0003&\u0378\b&\u0001\'\u0001\'\u0001\'\u0001\'"+
		"\u0001\'\u0001\'\u0001\'\u0001\'\u0003\'\u0382\b\'\u0001(\u0004(\u0385"+
		"\b(\u000b(\f(\u0386\u0001)\u0001)\u0001)\u0001)\u0003)\u038d\b)\u0001"+
		"*\u0001*\u0001*\u0005*\u0392\b*\n*\f*\u0395\t*\u0001*\u0001*\u0001+\u0001"+
		"+\u0001+\u0001+\u0003+\u039d\b+\u0001,\u0003,\u03a0\b,\u0001,\u0001,\u0001"+
		",\u0005,\u03a5\b,\n,\f,\u03a8\t,\u0001,\u0001,\u0001-\u0003-\u03ad\b-"+
		"\u0001-\u0001-\u0001-\u0000\u0001(.\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDF"+
		"HJLNPRTVXZ\u0000\u0014\u0004\u0000\u0012\u0012\u001a\u001a\u001f\u001f"+
		"&&\u0002\u0000\u000b\u000b--\u0001\u0000op\u0002\u0000\u001d\u001doo\u0002"+
		"\u0000\u0014\u0014++\u0002\u0000JJMM\u0005\u0000::>>AANNRR\u0002\u0000"+
		"KLOQ\u0002\u0000NNRR\u0001\u0000CE\u0001\u0000;<\u0002\u0000>>AA\u0001"+
		"\u0000jk\u0001\u000025\u0002\u00001168\u0002\u0000==@@\u0002\u0000??B"+
		"B\u0001\u0000Td\u0002\u0000ooqr\u0002\u0000\u0017\u0017))\u0457\u0000"+
		"]\u0001\u0000\u0000\u0000\u0002e\u0001\u0000\u0000\u0000\u0004n\u0001"+
		"\u0000\u0000\u0000\u0006w\u0001\u0000\u0000\u0000\b\u00a3\u0001\u0000"+
		"\u0000\u0000\n\u00ac\u0001\u0000\u0000\u0000\f\u00dc\u0001\u0000\u0000"+
		"\u0000\u000e\u00de\u0001\u0000\u0000\u0000\u0010\u00f4\u0001\u0000\u0000"+
		"\u0000\u0012\u0104\u0001\u0000\u0000\u0000\u0014\u0110\u0001\u0000\u0000"+
		"\u0000\u0016\u01aa\u0001\u0000\u0000\u0000\u0018\u01ae\u0001\u0000\u0000"+
		"\u0000\u001a\u01c0\u0001\u0000\u0000\u0000\u001c\u01fe\u0001\u0000\u0000"+
		"\u0000\u001e\u0200\u0001\u0000\u0000\u0000 \u0207\u0001\u0000\u0000\u0000"+
		"\"\u021b\u0001\u0000\u0000\u0000$\u021d\u0001\u0000\u0000\u0000&\u023c"+
		"\u0001\u0000\u0000\u0000(\u027f\u0001\u0000\u0000\u0000*\u02d0\u0001\u0000"+
		"\u0000\u0000,\u02d7\u0001\u0000\u0000\u0000.\u02e8\u0001\u0000\u0000\u0000"+
		"0\u02ef\u0001\u0000\u0000\u00002\u02f4\u0001\u0000\u0000\u00004\u02f6"+
		"\u0001\u0000\u0000\u00006\u02fd\u0001\u0000\u0000\u00008\u0307\u0001\u0000"+
		"\u0000\u0000:\u030c\u0001\u0000\u0000\u0000<\u0323\u0001\u0000\u0000\u0000"+
		">\u033a\u0001\u0000\u0000\u0000@\u0348\u0001\u0000\u0000\u0000B\u034b"+
		"\u0001\u0000\u0000\u0000D\u035e\u0001\u0000\u0000\u0000F\u0361\u0001\u0000"+
		"\u0000\u0000H\u0365\u0001\u0000\u0000\u0000J\u0370\u0001\u0000\u0000\u0000"+
		"L\u0377\u0001\u0000\u0000\u0000N\u0381\u0001\u0000\u0000\u0000P\u0384"+
		"\u0001\u0000\u0000\u0000R\u0388\u0001\u0000\u0000\u0000T\u0393\u0001\u0000"+
		"\u0000\u0000V\u0398\u0001\u0000\u0000\u0000X\u039f\u0001\u0000\u0000\u0000"+
		"Z\u03ac\u0001\u0000\u0000\u0000\\^\u0003\u0002\u0001\u0000]\\\u0001\u0000"+
		"\u0000\u0000]^\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_`\u0005"+
		"\u0000\u0000\u0001`\u0001\u0001\u0000\u0000\u0000ac\u0003\u0016\u000b"+
		"\u0000ba\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0001\u0000"+
		"\u0000\u0000df\u0005n\u0000\u0000eb\u0001\u0000\u0000\u0000fg\u0001\u0000"+
		"\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000h\u0003"+
		"\u0001\u0000\u0000\u0000ij\u0003(\u0014\u0000jk\u0005g\u0000\u0000km\u0001"+
		"\u0000\u0000\u0000li\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000"+
		"nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000\u0000"+
		"\u0000pn\u0001\u0000\u0000\u0000qr\u0003(\u0014\u0000r\u0005\u0001\u0000"+
		"\u0000\u0000st\u0005o\u0000\u0000tv\u0005g\u0000\u0000us\u0001\u0000\u0000"+
		"\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000"+
		"\u0000\u0000xz\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z{\u0005"+
		"o\u0000\u0000{\u0007\u0001\u0000\u0000\u0000|~\u0003\n\u0005\u0000}|\u0001"+
		"\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u0084\u0001\u0000\u0000"+
		"\u0000\u007f\u0080\u0003\f\u0006\u0000\u0080\u0081\u0003\n\u0005\u0000"+
		"\u0081\u0083\u0001\u0000\u0000\u0000\u0082\u007f\u0001\u0000\u0000\u0000"+
		"\u0083\u0086\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000"+
		"\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0087\u0001\u0000\u0000\u0000"+
		"\u0086\u0084\u0001\u0000\u0000\u0000\u0087\u0089\u0003\f\u0006\u0000\u0088"+
		"\u008a\u0003\n\u0005\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a"+
		"\u0001\u0000\u0000\u0000\u008a\u008e\u0001\u0000\u0000\u0000\u008b\u008d"+
		"\u0005\u0098\u0000\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008d\u0090"+
		"\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008f"+
		"\u0001\u0000\u0000\u0000\u008f\u00a4\u0001\u0000\u0000\u0000\u0090\u008e"+
		"\u0001\u0000\u0000\u0000\u0091\u0093\u0005\u0098\u0000\u0000\u0092\u0091"+
		"\u0001\u0000\u0000\u0000\u0093\u0096\u0001\u0000\u0000\u0000\u0094\u0092"+
		"\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u0097"+
		"\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0097\u0099"+
		"\u0005\u0097\u0000\u0000\u0098\u0094\u0001\u0000\u0000\u0000\u0099\u009c"+
		"\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u009b"+
		"\u0001\u0000\u0000\u0000\u009b\u00a0\u0001\u0000\u0000\u0000\u009c\u009a"+
		"\u0001\u0000\u0000\u0000\u009d\u009f\u0005\u0098\u0000\u0000\u009e\u009d"+
		"\u0001\u0000\u0000\u0000\u009f\u00a2\u0001\u0000\u0000\u0000\u00a0\u009e"+
		"\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a4"+
		"\u0001\u0000\u0000\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000\u00a3}\u0001"+
		"\u0000\u0000\u0000\u00a3\u009a\u0001\u0000\u0000\u0000\u00a4\t\u0001\u0000"+
		"\u0000\u0000\u00a5\u00a7\u0005\u0098\u0000\u0000\u00a6\u00a5\u0001\u0000"+
		"\u0000\u0000\u00a7\u00aa\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000"+
		"\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u00ab\u0001\u0000"+
		"\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00ab\u00ad\u0005\u0097"+
		"\u0000\u0000\u00ac\u00a8\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000"+
		"\u0000\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000"+
		"\u0000\u0000\u00af\u000b\u0001\u0000\u0000\u0000\u00b0\u00b2\u0005\u0098"+
		"\u0000\u0000\u00b1\u00b0\u0001\u0000\u0000\u0000\u00b2\u00b5\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b1\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000"+
		"\u0000\u0000\u00b4\u00b6\u0001\u0000\u0000\u0000\u00b5\u00b3\u0001\u0000"+
		"\u0000\u0000\u00b6\u00dd\u0005\u0095\u0000\u0000\u00b7\u00b9\u0005\u0098"+
		"\u0000\u0000\u00b8\u00b7\u0001\u0000\u0000\u0000\u00b9\u00bc\u0001\u0000"+
		"\u0000\u0000\u00ba\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000"+
		"\u0000\u0000\u00bb\u00bd\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000"+
		"\u0000\u0000\u00bd\u00bf\u0005\u0095\u0000\u0000\u00be\u00c0\u0005\u0098"+
		"\u0000\u0000\u00bf\u00be\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000"+
		"\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1\u00dd\u0005\u008a"+
		"\u0000\u0000\u00c2\u00c4\u0005\u0098\u0000\u0000\u00c3\u00c2\u0001\u0000"+
		"\u0000\u0000\u00c4\u00c7\u0001\u0000\u0000\u0000\u00c5\u00c3\u0001\u0000"+
		"\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6\u00c8\u0001\u0000"+
		"\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c8\u00dd\u0003\u000e"+
		"\u0007\u0000\u00c9\u00cb\u0005\u0098\u0000\u0000\u00ca\u00c9\u0001\u0000"+
		"\u0000\u0000\u00cb\u00ce\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000"+
		"\u0000\u0000\u00cc\u00cd\u0001\u0000\u0000\u0000\u00cd\u00cf\u0001\u0000"+
		"\u0000\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00cf\u00d1\u0003\u000e"+
		"\u0007\u0000\u00d0\u00d2\u0005\u0098\u0000\u0000\u00d1\u00d0\u0001\u0000"+
		"\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000"+
		"\u0000\u0000\u00d3\u00d4\u0005\u008a\u0000\u0000\u00d4\u00dd\u0001\u0000"+
		"\u0000\u0000\u00d5\u00d7\u0005\u0098\u0000\u0000\u00d6\u00d5\u0001\u0000"+
		"\u0000\u0000\u00d7\u00da\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000"+
		"\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9\u00db\u0001\u0000"+
		"\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000\u00db\u00dd\u0005\u008a"+
		"\u0000\u0000\u00dc\u00b3\u0001\u0000\u0000\u0000\u00dc\u00ba\u0001\u0000"+
		"\u0000\u0000\u00dc\u00c5\u0001\u0000\u0000\u0000\u00dc\u00cc\u0001\u0000"+
		"\u0000\u0000\u00dc\u00d8\u0001\u0000\u0000\u0000\u00dd\r\u0001\u0000\u0000"+
		"\u0000\u00de\u00e7\u0005\u0096\u0000\u0000\u00df\u00e1\u0005\u0098\u0000"+
		"\u0000\u00e0\u00df\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000"+
		"\u0000\u00e2\u00e0\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000"+
		"\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e6\u0003\u0010\b\u0000"+
		"\u00e5\u00e0\u0001\u0000\u0000\u0000\u00e6\u00e9\u0001\u0000\u0000\u0000"+
		"\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e7\u00e8\u0001\u0000\u0000\u0000"+
		"\u00e8\u000f\u0001\u0000\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000\u0000"+
		"\u00ea\u00f5\u0005\u0096\u0000\u0000\u00eb\u00f5\u0005\u0094\u0000\u0000"+
		"\u00ec\u00f5\u0005\u008c\u0000\u0000\u00ed\u00f5\u0005\u008d\u0000\u0000"+
		"\u00ee\u00f5\u0005\u008e\u0000\u0000\u00ef\u00f5\u0005\u008f\u0000\u0000"+
		"\u00f0\u00f5\u0005\u0090\u0000\u0000\u00f1\u00f5\u0005\u0091\u0000\u0000"+
		"\u00f2\u00f5\u0005\u0092\u0000\u0000\u00f3\u00f5\u0005\u0093\u0000\u0000"+
		"\u00f4\u00ea\u0001\u0000\u0000\u0000\u00f4\u00eb\u0001\u0000\u0000\u0000"+
		"\u00f4\u00ec\u0001\u0000\u0000\u0000\u00f4\u00ed\u0001\u0000\u0000\u0000"+
		"\u00f4\u00ee\u0001\u0000\u0000\u0000\u00f4\u00ef\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f0\u0001\u0000\u0000\u0000\u00f4\u00f1\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f2\u0001\u0000\u0000\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000"+
		"\u00f5\u0011\u0001\u0000\u0000\u0000\u00f6\u00f7\u0005F\u0000\u0000\u00f7"+
		"\u0105\u0005G\u0000\u0000\u00f8\u00fe\u0005F\u0000\u0000\u00f9\u00fa\u0003"+
		"\u0014\n\u0000\u00fa\u00fb\u0005g\u0000\u0000\u00fb\u00fd\u0001\u0000"+
		"\u0000\u0000\u00fc\u00f9\u0001\u0000\u0000\u0000\u00fd\u0100\u0001\u0000"+
		"\u0000\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000"+
		"\u0000\u0000\u00ff\u0101\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000"+
		"\u0000\u0000\u0101\u0102\u0003\u0014\n\u0000\u0102\u0103\u0005G\u0000"+
		"\u0000\u0103\u0105\u0001\u0000\u0000\u0000\u0104\u00f6\u0001\u0000\u0000"+
		"\u0000\u0104\u00f8\u0001\u0000\u0000\u0000\u0105\u0013\u0001\u0000\u0000"+
		"\u0000\u0106\u0108\u0005\u0019\u0000\u0000\u0107\u0106\u0001\u0000\u0000"+
		"\u0000\u0107\u0108\u0001\u0000\u0000\u0000\u0108\u010a\u0001\u0000\u0000"+
		"\u0000\u0109\u010b\u0005!\u0000\u0000\u010a\u0109\u0001\u0000\u0000\u0000"+
		"\u010a\u010b\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000\u0000"+
		"\u010c\u0111\u0005o\u0000\u0000\u010d\u010e\u0005!\u0000\u0000\u010e\u010f"+
		"\u0005\u0019\u0000\u0000\u010f\u0111\u0005o\u0000\u0000\u0110\u0107\u0001"+
		"\u0000\u0000\u0000\u0110\u010d\u0001\u0000\u0000\u0000\u0111\u0015\u0001"+
		"\u0000\u0000\u0000\u0112\u01ab\u0003(\u0014\u0000\u0113\u01ab\u0003.\u0017"+
		"\u0000\u0114\u01ab\u0003\u001a\r\u0000\u0115\u0116\u0005\u001c\u0000\u0000"+
		"\u0116\u01ab\u0005o\u0000\u0000\u0117\u011b\u0005\u0082\u0000\u0000\u0118"+
		"\u011a\u0003*\u0015\u0000\u0119\u0118\u0001\u0000\u0000\u0000\u011a\u011d"+
		"\u0001\u0000\u0000\u0000\u011b\u0119\u0001\u0000\u0000\u0000\u011b\u011c"+
		"\u0001\u0000\u0000\u0000\u011c\u01ab\u0001\u0000\u0000\u0000\u011d\u011b"+
		"\u0001\u0000\u0000\u0000\u011e\u011f\u0005\u0001\u0000\u0000\u011f\u01ab"+
		"\u00038\u001c\u0000\u0120\u0121\u0005\"\u0000\u0000\u0121\u0122\u0005"+
		"o\u0000\u0000\u0122\u0123\u0005T\u0000\u0000\u0123\u01ab\u0003(\u0014"+
		"\u0000\u0124\u0125\u0005\'\u0000\u0000\u0125\u0128\u0005s\u0000\u0000"+
		"\u0126\u0127\u0005&\u0000\u0000\u0127\u0129\u0003\u0006\u0003\u0000\u0128"+
		"\u0126\u0001\u0000\u0000\u0000\u0128\u0129\u0001\u0000\u0000\u0000\u0129"+
		"\u01ab\u0001\u0000\u0000\u0000\u012a\u012b\u0005\'\u0000\u0000\u012b\u012e"+
		"\u0005o\u0000\u0000\u012c\u012d\u0005&\u0000\u0000\u012d\u012f\u0003\u0006"+
		"\u0003\u0000\u012e\u012c\u0001\u0000\u0000\u0000\u012e\u012f\u0001\u0000"+
		"\u0000\u0000\u012f\u01ab\u0001\u0000\u0000\u0000\u0130\u0132\u0005\t\u0000"+
		"\u0000\u0131\u0130\u0001\u0000\u0000\u0000\u0131\u0132\u0001\u0000\u0000"+
		"\u0000\u0132\u0134\u0001\u0000\u0000\u0000\u0133\u0135\u0007\u0000\u0000"+
		"\u0000\u0134\u0133\u0001\u0000\u0000\u0000\u0134\u0135\u0001\u0000\u0000"+
		"\u0000\u0135\u0136\u0001\u0000\u0000\u0000\u0136\u0137\u0007\u0001\u0000"+
		"\u0000\u0137\u0138\u0005o\u0000\u0000\u0138\u013a\u0003<\u001e\u0000\u0139"+
		"\u013b\u0003\u0002\u0001\u0000\u013a\u0139\u0001\u0000\u0000\u0000\u013a"+
		"\u013b\u0001\u0000\u0000\u0000\u013b\u013c\u0001\u0000\u0000\u0000\u013c"+
		"\u013d\u0005\u0011\u0000\u0000\u013d\u01ab\u0001\u0000\u0000\u0000\u013e"+
		"\u013f\u0005o\u0000\u0000\u013f\u0141\u0005f\u0000\u0000\u0140\u013e\u0001"+
		"\u0000\u0000\u0000\u0140\u0141\u0001\u0000\u0000\u0000\u0141\u0142\u0001"+
		"\u0000\u0000\u0000\u0142\u0143\u0005\u0016\u0000\u0000\u0143\u0144\u0003"+
		"T*\u0000\u0144\u0146\u0005\f\u0000\u0000\u0145\u0147\u0003\u0002\u0001"+
		"\u0000\u0146\u0145\u0001\u0000\u0000\u0000\u0146\u0147\u0001\u0000\u0000"+
		"\u0000\u0147\u0148\u0001\u0000\u0000\u0000\u0148\u0149\u0005\u0011\u0000"+
		"\u0000\u0149\u01ab\u0001\u0000\u0000\u0000\u014a\u014b\u0005o\u0000\u0000"+
		"\u014b\u014d\u0005f\u0000\u0000\u014c\u014a\u0001\u0000\u0000\u0000\u014c"+
		"\u014d\u0001\u0000\u0000\u0000\u014d\u014e\u0001\u0000\u0000\u0000\u014e"+
		"\u0150\u0005\u0016\u0000\u0000\u014f\u0151\u0003\u0018\f\u0000\u0150\u014f"+
		"\u0001\u0000\u0000\u0000\u0150\u0151\u0001\u0000\u0000\u0000\u0151\u0152"+
		"\u0001\u0000\u0000\u0000\u0152\u0154\u0005n\u0000\u0000\u0153\u0155\u0003"+
		"(\u0014\u0000\u0154\u0153\u0001\u0000\u0000\u0000\u0154\u0155\u0001\u0000"+
		"\u0000\u0000\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u0158\u0005n\u0000"+
		"\u0000\u0157\u0159\u0003\u0004\u0002\u0000\u0158\u0157\u0001\u0000\u0000"+
		"\u0000\u0158\u0159\u0001\u0000\u0000\u0000\u0159\u015a\u0001\u0000\u0000"+
		"\u0000\u015a\u015c\u0005\f\u0000\u0000\u015b\u015d\u0003\u0002\u0001\u0000"+
		"\u015c\u015b\u0001\u0000\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000"+
		"\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u01ab\u0005\u0011\u0000\u0000"+
		"\u015f\u0160\u0005o\u0000\u0000\u0160\u0162\u0005f\u0000\u0000\u0161\u015f"+
		"\u0001\u0000\u0000\u0000\u0161\u0162\u0001\u0000\u0000\u0000\u0162\u0163"+
		"\u0001\u0000\u0000\u0000\u0163\u0165\u0005\u0016\u0000\u0000\u0164\u0166"+
		"\u0003\u001e\u000f\u0000\u0165\u0164\u0001\u0000\u0000\u0000\u0165\u0166"+
		"\u0001\u0000\u0000\u0000\u0166\u0167\u0001\u0000\u0000\u0000\u0167\u0168"+
		"\u0003&\u0013\u0000\u0168\u0169\u0005\u0019\u0000\u0000\u0169\u016b\u0003"+
		"N\'\u0000\u016a\u016c\u0005\n\u0000\u0000\u016b\u016a\u0001\u0000\u0000"+
		"\u0000\u016b\u016c\u0001\u0000\u0000\u0000\u016c\u016d\u0001\u0000\u0000"+
		"\u0000\u016d\u016f\u0005\f\u0000\u0000\u016e\u0170\u0003\u0002\u0001\u0000"+
		"\u016f\u016e\u0001\u0000\u0000\u0000\u016f\u0170\u0001\u0000\u0000\u0000"+
		"\u0170\u0171\u0001\u0000\u0000\u0000\u0171\u0172\u0005\u0011\u0000\u0000"+
		"\u0172\u01ab\u0001\u0000\u0000\u0000\u0173\u0174\u0005o\u0000\u0000\u0174"+
		"\u0176\u0005f\u0000\u0000\u0175\u0173\u0001\u0000\u0000\u0000\u0175\u0176"+
		"\u0001\u0000\u0000\u0000\u0176\u0177\u0001\u0000\u0000\u0000\u0177\u0178"+
		"\u00050\u0000\u0000\u0178\u0179\u0003(\u0014\u0000\u0179\u017b\u0005\f"+
		"\u0000\u0000\u017a\u017c\u0003\u0002\u0001\u0000\u017b\u017a\u0001\u0000"+
		"\u0000\u0000\u017b\u017c\u0001\u0000\u0000\u0000\u017c\u017d\u0001\u0000"+
		"\u0000\u0000\u017d\u017e\u0005\u0011\u0000\u0000\u017e\u01ab\u0001\u0000"+
		"\u0000\u0000\u017f\u0180\u0005o\u0000\u0000\u0180\u0182\u0005f\u0000\u0000"+
		"\u0181\u017f\u0001\u0000\u0000\u0000\u0181\u0182\u0001\u0000\u0000\u0000"+
		"\u0182\u0183\u0001\u0000\u0000\u0000\u0183\u0185\u0005\f\u0000\u0000\u0184"+
		"\u0186\u0003\u0002\u0001\u0000\u0185\u0184\u0001\u0000\u0000\u0000\u0185"+
		"\u0186\u0001\u0000\u0000\u0000\u0186\u0187\u0001\u0000\u0000\u0000\u0187"+
		"\u0188\u00050\u0000\u0000\u0188\u01ab\u0003(\u0014\u0000\u0189\u018b\u0005"+
		"\u0004\u0000\u0000\u018a\u018c\u0005o\u0000\u0000\u018b\u018a\u0001\u0000"+
		"\u0000\u0000\u018b\u018c\u0001\u0000\u0000\u0000\u018c\u01ab\u0001\u0000"+
		"\u0000\u0000\u018d\u018f\u0005\b\u0000\u0000\u018e\u0190\u0005o\u0000"+
		"\u0000\u018f\u018e\u0001\u0000\u0000\u0000\u018f\u0190\u0001\u0000\u0000"+
		"\u0000\u0190\u01ab\u0001\u0000\u0000\u0000\u0191\u0193\u0005(\u0000\u0000"+
		"\u0192\u0194\u0003(\u0014\u0000\u0193\u0192\u0001\u0000\u0000\u0000\u0193"+
		"\u0194\u0001\u0000\u0000\u0000\u0194\u01ab\u0001\u0000\u0000\u0000\u0195"+
		"\u0197\u0005\u0002\u0000\u0000\u0196\u0198\u0003\u0002\u0001\u0000\u0197"+
		"\u0196\u0001\u0000\u0000\u0000\u0197\u0198\u0001\u0000\u0000\u0000\u0198"+
		"\u0199\u0001\u0000\u0000\u0000\u0199\u01ab\u0005\u0011\u0000\u0000\u019a"+
		"\u019c\u0005\u0003\u0000\u0000\u019b\u019d\u0003\u0002\u0001\u0000\u019c"+
		"\u019b\u0001\u0000\u0000\u0000\u019c\u019d\u0001\u0000\u0000\u0000\u019d"+
		"\u019e\u0001\u0000\u0000\u0000\u019e\u01ab\u0005\u0011\u0000\u0000\u019f"+
		"\u01a1\u0005\t\u0000\u0000\u01a0\u01a2\u0003\u0002\u0001\u0000\u01a1\u01a0"+
		"\u0001\u0000\u0000\u0000\u01a1\u01a2\u0001\u0000\u0000\u0000\u01a2\u01a3"+
		"\u0001\u0000\u0000\u0000\u01a3\u01ab\u0005\u0011\u0000\u0000\u01a4\u01a6"+
		"\u0005\u001d\u0000\u0000\u01a5\u01a7\u0003\u0012\t\u0000\u01a6\u01a5\u0001"+
		"\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000\u0000\u0000\u01a7\u01a8\u0001"+
		"\u0000\u0000\u0000\u01a8\u01a9\u0005{\u0000\u0000\u01a9\u01ab\u0003\b"+
		"\u0004\u0000\u01aa\u0112\u0001\u0000\u0000\u0000\u01aa\u0113\u0001\u0000"+
		"\u0000\u0000\u01aa\u0114\u0001\u0000\u0000\u0000\u01aa\u0115\u0001\u0000"+
		"\u0000\u0000\u01aa\u0117\u0001\u0000\u0000\u0000\u01aa\u011e\u0001\u0000"+
		"\u0000\u0000\u01aa\u0120\u0001\u0000\u0000\u0000\u01aa\u0124\u0001\u0000"+
		"\u0000\u0000\u01aa\u012a\u0001\u0000\u0000\u0000\u01aa\u0131\u0001\u0000"+
		"\u0000\u0000\u01aa\u0140\u0001\u0000\u0000\u0000\u01aa\u014c\u0001\u0000"+
		"\u0000\u0000\u01aa\u0161\u0001\u0000\u0000\u0000\u01aa\u0175\u0001\u0000"+
		"\u0000\u0000\u01aa\u0181\u0001\u0000\u0000\u0000\u01aa\u0189\u0001\u0000"+
		"\u0000\u0000\u01aa\u018d\u0001\u0000\u0000\u0000\u01aa\u0191\u0001\u0000"+
		"\u0000\u0000\u01aa\u0195\u0001\u0000\u0000\u0000\u01aa\u019a\u0001\u0000"+
		"\u0000\u0000\u01aa\u019f\u0001\u0000\u0000\u0000\u01aa\u01a4\u0001\u0000"+
		"\u0000\u0000\u01ab\u0017\u0001\u0000\u0000\u0000\u01ac\u01af\u0003\u001a"+
		"\r\u0000\u01ad\u01af\u0003\u0004\u0002\u0000\u01ae\u01ac\u0001\u0000\u0000"+
		"\u0000\u01ae\u01ad\u0001\u0000\u0000\u0000\u01af\u0019\u0001\u0000\u0000"+
		"\u0000\u01b0\u01b2\u0003\u001c\u000e\u0000\u01b1\u01b0\u0001\u0000\u0000"+
		"\u0000\u01b2\u01b5\u0001\u0000\u0000\u0000\u01b3\u01b1\u0001\u0000\u0000"+
		"\u0000\u01b3\u01b4\u0001\u0000\u0000\u0000\u01b4\u01b6\u0001\u0000\u0000"+
		"\u0000\u01b5\u01b3\u0001\u0000\u0000\u0000\u01b6\u01b7\u0003\u001e\u000f"+
		"\u0000\u01b7\u01b8\u0003 \u0010\u0000\u01b8\u01c1\u0001\u0000\u0000\u0000"+
		"\u01b9\u01bb\u0003\u001c\u000e\u0000\u01ba\u01b9\u0001\u0000\u0000\u0000"+
		"\u01bb\u01bc\u0001\u0000\u0000\u0000\u01bc\u01ba\u0001\u0000\u0000\u0000"+
		"\u01bc\u01bd\u0001\u0000\u0000\u0000\u01bd\u01be\u0001\u0000\u0000\u0000"+
		"\u01be\u01bf\u0003 \u0010\u0000\u01bf\u01c1\u0001\u0000\u0000\u0000\u01c0"+
		"\u01b3\u0001\u0000\u0000\u0000\u01c0\u01ba\u0001\u0000\u0000\u0000\u01c1"+
		"\u001b\u0001\u0000\u0000\u0000\u01c2\u01ff\u0005\u0007\u0000\u0000\u01c3"+
		"\u01ff\u0005\u0005\u0000\u0000\u01c4\u01ff\u0005\u0012\u0000\u0000\u01c5"+
		"\u01c7\u0005\u0013\u0000\u0000\u01c6\u01c8\u0005o\u0000\u0000\u01c7\u01c6"+
		"\u0001\u0000\u0000\u0000\u01c7\u01c8\u0001\u0000\u0000\u0000\u01c8\u01ff"+
		"\u0001\u0000\u0000\u0000\u01c9\u01ca\u0005\u0013\u0000\u0000\u01ca\u01cb"+
		"\u0005o\u0000\u0000\u01cb\u01cc\u0005H\u0000\u0000\u01cc\u01cd\u0003("+
		"\u0014\u0000\u01cd\u01ce\u0005I\u0000\u0000\u01ce\u01ff\u0001\u0000\u0000"+
		"\u0000\u01cf\u01d0\u0005\u0013\u0000\u0000\u01d0\u01d1\u0005o\u0000\u0000"+
		"\u01d1\u01d2\u0005H\u0000\u0000\u01d2\u01d3\u0003N\'\u0000\u01d3\u01d4"+
		"\u0005I\u0000\u0000\u01d4\u01ff\u0001\u0000\u0000\u0000\u01d5\u01d6\u0005"+
		"\u0013\u0000\u0000\u01d6\u01d8\u0005F\u0000\u0000\u01d7\u01d9\u0005o\u0000"+
		"\u0000\u01d8\u01d7\u0001\u0000\u0000\u0000\u01d8\u01d9\u0001\u0000\u0000"+
		"\u0000\u01d9\u01da\u0001\u0000\u0000\u0000\u01da\u01ff\u0005G\u0000\u0000"+
		"\u01db\u01dc\u0005\u0013\u0000\u0000\u01dc\u01dd\u0005F\u0000\u0000\u01dd"+
		"\u01de\u0005o\u0000\u0000\u01de\u01df\u0005H\u0000\u0000\u01df\u01e0\u0003"+
		"(\u0014\u0000\u01e0\u01e1\u0005I\u0000\u0000\u01e1\u01e2\u0005G\u0000"+
		"\u0000\u01e2\u01ff\u0001\u0000\u0000\u0000\u01e3\u01e4\u0005\u0013\u0000"+
		"\u0000\u01e4\u01e5\u0005F\u0000\u0000\u01e5\u01e6\u0005o\u0000\u0000\u01e6"+
		"\u01e7\u0005H\u0000\u0000\u01e7\u01e8\u0003N\'\u0000\u01e8\u01e9\u0005"+
		"I\u0000\u0000\u01e9\u01ea\u0005G\u0000\u0000\u01ea\u01ff\u0001\u0000\u0000"+
		"\u0000\u01eb\u01ff\u0005\u0015\u0000\u0000\u01ec\u01ff\u0005\u001b\u0000"+
		"\u0000\u01ed\u01ee\u0005\u001d\u0000\u0000\u01ee\u01ef\u0005F\u0000\u0000"+
		"\u01ef\u01f0\u0003\u0004\u0002\u0000\u01f0\u01f1\u0005G\u0000\u0000\u01f1"+
		"\u01ff\u0001\u0000\u0000\u0000\u01f2\u01ff\u0005\u001e\u0000\u0000\u01f3"+
		"\u01f5\u0005&\u0000\u0000\u01f4\u01f6\u0005o\u0000\u0000\u01f5\u01f4\u0001"+
		"\u0000\u0000\u0000\u01f5\u01f6\u0001\u0000\u0000\u0000\u01f6\u01ff\u0001"+
		"\u0000\u0000\u0000\u01f7\u01f8\u0005&\u0000\u0000\u01f8\u01fa\u0005F\u0000"+
		"\u0000\u01f9\u01fb\u0005o\u0000\u0000\u01fa\u01f9\u0001\u0000\u0000\u0000"+
		"\u01fa\u01fb\u0001\u0000\u0000\u0000\u01fb\u01fc\u0001\u0000\u0000\u0000"+
		"\u01fc\u01ff\u0005G\u0000\u0000\u01fd\u01ff\u0005.\u0000\u0000\u01fe\u01c2"+
		"\u0001\u0000\u0000\u0000\u01fe\u01c3\u0001\u0000\u0000\u0000\u01fe\u01c4"+
		"\u0001\u0000\u0000\u0000\u01fe\u01c5\u0001\u0000\u0000\u0000\u01fe\u01c9"+
		"\u0001\u0000\u0000\u0000\u01fe\u01cf\u0001\u0000\u0000\u0000\u01fe\u01d5"+
		"\u0001\u0000\u0000\u0000\u01fe\u01db\u0001\u0000\u0000\u0000\u01fe\u01e3"+
		"\u0001\u0000\u0000\u0000\u01fe\u01eb\u0001\u0000\u0000\u0000\u01fe\u01ec"+
		"\u0001\u0000\u0000\u0000\u01fe\u01ed\u0001\u0000\u0000\u0000\u01fe\u01f2"+
		"\u0001\u0000\u0000\u0000\u01fe\u01f3\u0001\u0000\u0000\u0000\u01fe\u01f7"+
		"\u0001\u0000\u0000\u0000\u01fe\u01fd\u0001\u0000\u0000\u0000\u01ff\u001d"+
		"\u0001\u0000\u0000\u0000\u0200\u0201\u0005,\u0000\u0000\u0201\u001f\u0001"+
		"\u0000\u0000\u0000\u0202\u0203\u0003\"\u0011\u0000\u0203\u0204\u0005g"+
		"\u0000\u0000\u0204\u0206\u0001\u0000\u0000\u0000\u0205\u0202\u0001\u0000"+
		"\u0000\u0000\u0206\u0209\u0001\u0000\u0000\u0000\u0207\u0205\u0001\u0000"+
		"\u0000\u0000\u0207\u0208\u0001\u0000\u0000\u0000\u0208\u020a\u0001\u0000"+
		"\u0000\u0000\u0209\u0207\u0001\u0000\u0000\u0000\u020a\u020b\u0003\"\u0011"+
		"\u0000\u020b!\u0001\u0000\u0000\u0000\u020c\u020f\u0007\u0002\u0000\u0000"+
		"\u020d\u020e\u0005T\u0000\u0000\u020e\u0210\u0003(\u0014\u0000\u020f\u020d"+
		"\u0001\u0000\u0000\u0000\u020f\u0210\u0001\u0000\u0000\u0000\u0210\u021c"+
		"\u0001\u0000\u0000\u0000\u0211\u0212\u0007\u0002\u0000\u0000\u0212\u0214"+
		"\u0005H\u0000\u0000\u0213\u0215\u0003(\u0014\u0000\u0214\u0213\u0001\u0000"+
		"\u0000\u0000\u0214\u0215\u0001\u0000\u0000\u0000\u0215\u0216\u0001\u0000"+
		"\u0000\u0000\u0216\u0219\u0005I\u0000\u0000\u0217\u0218\u0005T\u0000\u0000"+
		"\u0218\u021a\u0003$\u0012\u0000\u0219\u0217\u0001\u0000\u0000\u0000\u0219"+
		"\u021a\u0001\u0000\u0000\u0000\u021a\u021c\u0001\u0000\u0000\u0000\u021b"+
		"\u020c\u0001\u0000\u0000\u0000\u021b\u0211\u0001\u0000\u0000\u0000\u021c"+
		"#\u0001\u0000\u0000\u0000\u021d\u021e\u0005F\u0000\u0000\u021e\u021f\u0003"+
		"\u0004\u0002\u0000\u021f\u0220\u0005G\u0000\u0000\u0220%\u0001\u0000\u0000"+
		"\u0000\u0221\u023d\u0005o\u0000\u0000\u0222\u023d\u0005p\u0000\u0000\u0223"+
		"\u023d\u0005q\u0000\u0000\u0224\u0225\u0007\u0002\u0000\u0000\u0225\u0226"+
		"\u0005H\u0000\u0000\u0226\u0227\u0003(\u0014\u0000\u0227\u0228\u0005I"+
		"\u0000\u0000\u0228\u023d\u0001\u0000\u0000\u0000\u0229\u022a\u0007\u0002"+
		"\u0000\u0000\u022a\u022b\u0005H\u0000\u0000\u022b\u022c\u0003N\'\u0000"+
		"\u022c\u022d\u0005I\u0000\u0000\u022d\u023d\u0001\u0000\u0000\u0000\u022e"+
		"\u022f\u0005o\u0000\u0000\u022f\u0230\u0005i\u0000\u0000\u0230\u0231\u0005"+
		"o\u0000\u0000\u0231\u0232\u0005H\u0000\u0000\u0232\u0233\u0003(\u0014"+
		"\u0000\u0233\u0234\u0005I\u0000\u0000\u0234\u023d\u0001\u0000\u0000\u0000"+
		"\u0235\u0236\u0005o\u0000\u0000\u0236\u0237\u0005i\u0000\u0000\u0237\u0238"+
		"\u0005o\u0000\u0000\u0238\u0239\u0005H\u0000\u0000\u0239\u023a\u0003N"+
		"\'\u0000\u023a\u023b\u0005I\u0000\u0000\u023b\u023d\u0001\u0000\u0000"+
		"\u0000\u023c\u0221\u0001\u0000\u0000\u0000\u023c\u0222\u0001\u0000\u0000"+
		"\u0000\u023c\u0223\u0001\u0000\u0000\u0000\u023c\u0224\u0001\u0000\u0000"+
		"\u0000\u023c\u0229\u0001\u0000\u0000\u0000\u023c\u022e\u0001\u0000\u0000"+
		"\u0000\u023c\u0235\u0001\u0000\u0000\u0000\u023d\'\u0001\u0000\u0000\u0000"+
		"\u023e\u023f\u0006\u0014\uffff\uffff\u0000\u023f\u0280\u0003&\u0013\u0000"+
		"\u0240\u0280\u0005r\u0000\u0000\u0241\u0242\u0005\u0011\u0000\u0000\u0242"+
		"\u0243\u0005F\u0000\u0000\u0243\u0280\u0005G\u0000\u0000\u0244\u0245\u0007"+
		"\u0003\u0000\u0000\u0245\u0280\u0003D\"\u0000\u0246\u0247\u0005\u0006"+
		"\u0000\u0000\u0247\u0249\u0003(\u0014\u0000\u0248\u024a\u0003F#\u0000"+
		"\u0249\u0248\u0001\u0000\u0000\u0000\u0249\u024a\u0001\u0000\u0000\u0000"+
		"\u024a\u024d\u0001\u0000\u0000\u0000\u024b\u024c\u0005\u000e\u0000\u0000"+
		"\u024c\u024e\u0003\u0002\u0001\u0000\u024d\u024b\u0001\u0000\u0000\u0000"+
		"\u024d\u024e\u0001\u0000\u0000\u0000\u024e\u024f\u0001\u0000\u0000\u0000"+
		"\u024f\u0250\u0005\u0011\u0000\u0000\u0250\u0280\u0001\u0000\u0000\u0000"+
		"\u0251\u0252\u0005\u0018\u0000\u0000\u0252\u0253\u0003(\u0014\u0000\u0253"+
		"\u0255\u0005*\u0000\u0000\u0254\u0256\u0003\u0002\u0001\u0000\u0255\u0254"+
		"\u0001\u0000\u0000\u0000\u0255\u0256\u0001\u0000\u0000\u0000\u0256\u0258"+
		"\u0001\u0000\u0000\u0000\u0257\u0259\u0003P(\u0000\u0258\u0257\u0001\u0000"+
		"\u0000\u0000\u0258\u0259\u0001\u0000\u0000\u0000\u0259\u025e\u0001\u0000"+
		"\u0000\u0000\u025a\u025c\u0005\u000e\u0000\u0000\u025b\u025d\u0003\u0002"+
		"\u0001\u0000\u025c\u025b\u0001\u0000\u0000\u0000\u025c\u025d\u0001\u0000"+
		"\u0000\u0000\u025d\u025f\u0001\u0000\u0000\u0000\u025e\u025a\u0001\u0000"+
		"\u0000\u0000\u025e\u025f\u0001\u0000\u0000\u0000\u025f\u0260\u0001\u0000"+
		"\u0000\u0000\u0260\u0261\u0005\u0011\u0000\u0000\u0261\u0280\u0001\u0000"+
		"\u0000\u0000\u0262\u0266\u0005\u007f\u0000\u0000\u0263\u0265\u0003*\u0015"+
		"\u0000\u0264\u0263\u0001\u0000\u0000\u0000\u0265\u0268\u0001\u0000\u0000"+
		"\u0000\u0266\u0264\u0001\u0000\u0000\u0000\u0266\u0267\u0001\u0000\u0000"+
		"\u0000\u0267\u0269\u0001\u0000\u0000\u0000\u0268\u0266\u0001\u0000\u0000"+
		"\u0000\u0269\u0280\u0005l\u0000\u0000\u026a\u0280\u0005s\u0000\u0000\u026b"+
		"\u0280\u0005t\u0000\u0000\u026c\u0280\u0005u\u0000\u0000\u026d\u0280\u0005"+
		"v\u0000\u0000\u026e\u0280\u0005w\u0000\u0000\u026f\u0280\u0005x\u0000"+
		"\u0000\u0270\u0280\u0005y\u0000\u0000\u0271\u0280\u0005z\u0000\u0000\u0272"+
		"\u0280\u0005 \u0000\u0000\u0273\u0280\u0007\u0004\u0000\u0000\u0274\u0275"+
		"\u0003&\u0013\u0000\u0275\u0276\u0007\u0005\u0000\u0000\u0276\u0280\u0001"+
		"\u0000\u0000\u0000\u0277\u0278\u0007\u0005\u0000\u0000\u0278\u0280\u0003"+
		"&\u0013\u0000\u0279\u027a\u0007\u0006\u0000\u0000\u027a\u0280\u0003(\u0014"+
		"\u0010\u027b\u027c\u0005F\u0000\u0000\u027c\u027d\u0003(\u0014\u0000\u027d"+
		"\u027e\u0005G\u0000\u0000\u027e\u0280\u0001\u0000\u0000\u0000\u027f\u023e"+
		"\u0001\u0000\u0000\u0000\u027f\u0240\u0001\u0000\u0000\u0000\u027f\u0241"+
		"\u0001\u0000\u0000\u0000\u027f\u0244\u0001\u0000\u0000\u0000\u027f\u0246"+
		"\u0001\u0000\u0000\u0000\u027f\u0251\u0001\u0000\u0000\u0000\u027f\u0262"+
		"\u0001\u0000\u0000\u0000\u027f\u026a\u0001\u0000\u0000\u0000\u027f\u026b"+
		"\u0001\u0000\u0000\u0000\u027f\u026c\u0001\u0000\u0000\u0000\u027f\u026d"+
		"\u0001\u0000\u0000\u0000\u027f\u026e\u0001\u0000\u0000\u0000\u027f\u026f"+
		"\u0001\u0000\u0000\u0000\u027f\u0270\u0001\u0000\u0000\u0000\u027f\u0271"+
		"\u0001\u0000\u0000\u0000\u027f\u0272\u0001\u0000\u0000\u0000\u027f\u0273"+
		"\u0001\u0000\u0000\u0000\u027f\u0274\u0001\u0000\u0000\u0000\u027f\u0277"+
		"\u0001\u0000\u0000\u0000\u027f\u0279\u0001\u0000\u0000\u0000\u027f\u027b"+
		"\u0001\u0000\u0000\u0000\u0280\u02c6\u0001\u0000\u0000\u0000\u0281\u0282"+
		"\n\u000f\u0000\u0000\u0282\u0283\u0005S\u0000\u0000\u0283\u02c5\u0003"+
		"(\u0014\u0010\u0284\u0285\n\u000e\u0000\u0000\u0285\u0286\u0007\u0007"+
		"\u0000\u0000\u0286\u02c5\u0003(\u0014\u000f\u0287\u0288\n\r\u0000\u0000"+
		"\u0288\u0289\u0007\b\u0000\u0000\u0289\u02c5\u0003(\u0014\u000e\u028a"+
		"\u028b\n\f\u0000\u0000\u028b\u028c\u0007\t\u0000\u0000\u028c\u02c5\u0003"+
		"(\u0014\r\u028d\u028e\n\u000b\u0000\u0000\u028e\u028f\u00059\u0000\u0000"+
		"\u028f\u02c5\u0003(\u0014\f\u0290\u0291\n\n\u0000\u0000\u0291\u0292\u0007"+
		"\n\u0000\u0000\u0292\u02c5\u0003(\u0014\u000b\u0293\u0295\n\t\u0000\u0000"+
		"\u0294\u0296\u0007\u000b\u0000\u0000\u0295\u0294\u0001\u0000\u0000\u0000"+
		"\u0295\u0296\u0001\u0000\u0000\u0000\u0296\u0297\u0001\u0000\u0000\u0000"+
		"\u0297\u0298\u0005\u0019\u0000\u0000\u0298\u0299\u0003(\u0014\u0000\u0299"+
		"\u029a\u0007\f\u0000\u0000\u029a\u029b\u0003(\u0014\n\u029b\u02c5\u0001"+
		"\u0000\u0000\u0000\u029c\u029d\n\u0007\u0000\u0000\u029d\u029e\u0007\r"+
		"\u0000\u0000\u029e\u02c5\u0003(\u0014\b\u029f\u02a0\n\u0006\u0000\u0000"+
		"\u02a0\u02a1\u0007\u000e\u0000\u0000\u02a1\u02c5\u0003(\u0014\u0007\u02a2"+
		"\u02a3\n\u0005\u0000\u0000\u02a3\u02a4\u0007\u000f\u0000\u0000\u02a4\u02c5"+
		"\u0003(\u0014\u0006\u02a5\u02a6\n\u0004\u0000\u0000\u02a6\u02a7\u0007"+
		"\u0010\u0000\u0000\u02a7\u02c5\u0003(\u0014\u0005\u02a8\u02a9\n\u0003"+
		"\u0000\u0000\u02a9\u02aa\u0005m\u0000\u0000\u02aa\u02ab\u0003(\u0014\u0000"+
		"\u02ab\u02ac\u0005f\u0000\u0000\u02ac\u02ad\u0003(\u0014\u0003\u02ad\u02c5"+
		"\u0001\u0000\u0000\u0000\u02ae\u02af\n\u0002\u0000\u0000\u02af\u02b0\u0007"+
		"\u0011\u0000\u0000\u02b0\u02c5\u0003(\u0014\u0002\u02b1\u02b2\n\"\u0000"+
		"\u0000\u02b2\u02b3\u0005i\u0000\u0000\u02b3\u02b4\u0005o\u0000\u0000\u02b4"+
		"\u02c5\u0003D\"\u0000\u02b5\u02b6\n!\u0000\u0000\u02b6\u02b7\u0005i\u0000"+
		"\u0000\u02b7\u02c5\u0005o\u0000\u0000\u02b8\u02b9\n \u0000\u0000\u02b9"+
		"\u02ba\u0005i\u0000\u0000\u02ba\u02c5\u0005q\u0000\u0000\u02bb\u02bd\n"+
		"\b\u0000\u0000\u02bc\u02be\u0007\u000b\u0000\u0000\u02bd\u02bc\u0001\u0000"+
		"\u0000\u0000\u02bd\u02be\u0001\u0000\u0000\u0000\u02be\u02bf\u0001\u0000"+
		"\u0000\u0000\u02bf\u02c0\u0005\u0019\u0000\u0000\u02c0\u02c1\u0005F\u0000"+
		"\u0000\u02c1\u02c2\u0003J%\u0000\u02c2\u02c3\u0005G\u0000\u0000\u02c3"+
		"\u02c5\u0001\u0000\u0000\u0000\u02c4\u0281\u0001\u0000\u0000\u0000\u02c4"+
		"\u0284\u0001\u0000\u0000\u0000\u02c4\u0287\u0001\u0000\u0000\u0000\u02c4"+
		"\u028a\u0001\u0000\u0000\u0000\u02c4\u028d\u0001\u0000\u0000\u0000\u02c4"+
		"\u0290\u0001\u0000\u0000\u0000\u02c4\u0293\u0001\u0000\u0000\u0000\u02c4"+
		"\u029c\u0001\u0000\u0000\u0000\u02c4\u029f\u0001\u0000\u0000\u0000\u02c4"+
		"\u02a2\u0001\u0000\u0000\u0000\u02c4\u02a5\u0001\u0000\u0000\u0000\u02c4"+
		"\u02a8\u0001\u0000\u0000\u0000\u02c4\u02ae\u0001\u0000\u0000\u0000\u02c4"+
		"\u02b1\u0001\u0000\u0000\u0000\u02c4\u02b5\u0001\u0000\u0000\u0000\u02c4"+
		"\u02b8\u0001\u0000\u0000\u0000\u02c4\u02bb\u0001\u0000\u0000\u0000\u02c5"+
		"\u02c8\u0001\u0000\u0000\u0000\u02c6\u02c4\u0001\u0000\u0000\u0000\u02c6"+
		"\u02c7\u0001\u0000\u0000\u0000\u02c7)\u0001\u0000\u0000\u0000\u02c8\u02c6"+
		"\u0001\u0000\u0000\u0000\u02c9\u02d1\u0005\u00a0\u0000\u0000\u02ca\u02d1"+
		"\u0005\u00a1\u0000\u0000\u02cb\u02cc\u0005\u00a3\u0000\u0000\u02cc\u02cd"+
		"\u0003(\u0014\u0000\u02cd\u02ce\u0005\u0080\u0000\u0000\u02ce\u02d1\u0001"+
		"\u0000\u0000\u0000\u02cf\u02d1\u0003,\u0016\u0000\u02d0\u02c9\u0001\u0000"+
		"\u0000\u0000\u02d0\u02ca\u0001\u0000\u0000\u0000\u02d0\u02cb\u0001\u0000"+
		"\u0000\u0000\u02d0\u02cf\u0001\u0000\u0000\u0000\u02d1+\u0001\u0000\u0000"+
		"\u0000\u02d2\u02d8\u0005\u00a2\u0000\u0000\u02d3\u02d5\u0005\u00a4\u0000"+
		"\u0000\u02d4\u02d6\u0005\u00a6\u0000\u0000\u02d5\u02d4\u0001\u0000\u0000"+
		"\u0000\u02d5\u02d6\u0001\u0000\u0000\u0000\u02d6\u02d8\u0001\u0000\u0000"+
		"\u0000\u02d7\u02d2\u0001\u0000\u0000\u0000\u02d7\u02d3\u0001\u0000\u0000"+
		"\u0000\u02d8-\u0001\u0000\u0000\u0000\u02d9\u02da\u0005}\u0000\u0000\u02da"+
		"\u02dd\u00032\u0019\u0000\u02db\u02dc\u0005\u009b\u0000\u0000\u02dc\u02de"+
		"\u00030\u0018\u0000\u02dd\u02db\u0001\u0000\u0000\u0000\u02dd\u02de\u0001"+
		"\u0000\u0000\u0000\u02de\u02e9\u0001\u0000\u0000\u0000\u02df\u02e0\u0005"+
		"~\u0000\u0000\u02e0\u02e3\u00032\u0019\u0000\u02e1\u02e2\u0005\u009b\u0000"+
		"\u0000\u02e2\u02e4\u00030\u0018\u0000\u02e3\u02e1\u0001\u0000\u0000\u0000"+
		"\u02e3\u02e4\u0001\u0000\u0000\u0000\u02e4\u02e9\u0001\u0000\u0000\u0000"+
		"\u02e5\u02e6\u0005|\u0000\u0000\u02e6\u02e7\u0005o\u0000\u0000\u02e7\u02e9"+
		"\u00036\u001b\u0000\u02e8\u02d9\u0001\u0000\u0000\u0000\u02e8\u02df\u0001"+
		"\u0000\u0000\u0000\u02e8\u02e5\u0001\u0000\u0000\u0000\u02e9/\u0001\u0000"+
		"\u0000\u0000\u02ea\u02eb\u00032\u0019\u0000\u02eb\u02ec\u0005\u009c\u0000"+
		"\u0000\u02ec\u02ee\u0001\u0000\u0000\u0000\u02ed\u02ea\u0001\u0000\u0000"+
		"\u0000\u02ee\u02f1\u0001\u0000\u0000\u0000\u02ef\u02ed\u0001\u0000\u0000"+
		"\u0000\u02ef\u02f0\u0001\u0000\u0000\u0000\u02f0\u02f2\u0001\u0000\u0000"+
		"\u0000\u02f1\u02ef\u0001\u0000\u0000\u0000\u02f2\u02f3\u00032\u0019\u0000"+
		"\u02f31\u0001\u0000\u0000\u0000\u02f4\u02f5\u0005\u009a\u0000\u0000\u02f5"+
		"3\u0001\u0000\u0000\u0000\u02f6\u02f7\u0007\u0012\u0000\u0000\u02f75\u0001"+
		"\u0000\u0000\u0000\u02f8\u02f9\u00034\u001a\u0000\u02f9\u02fa\u0005g\u0000"+
		"\u0000\u02fa\u02fc\u0001\u0000\u0000\u0000\u02fb\u02f8\u0001\u0000\u0000"+
		"\u0000\u02fc\u02ff\u0001\u0000\u0000\u0000\u02fd\u02fb\u0001\u0000\u0000"+
		"\u0000\u02fd\u02fe\u0001\u0000\u0000\u0000\u02fe\u0300\u0001\u0000\u0000"+
		"\u0000\u02ff\u02fd\u0001\u0000\u0000\u0000\u0300\u0301\u00034\u001a\u0000"+
		"\u03017\u0001\u0000\u0000\u0000\u0302\u0303\u0003:\u001d\u0000\u0303\u0304"+
		"\u0005g\u0000\u0000\u0304\u0306\u0001\u0000\u0000\u0000\u0305\u0302\u0001"+
		"\u0000\u0000\u0000\u0306\u0309\u0001\u0000\u0000\u0000\u0307\u0305\u0001"+
		"\u0000\u0000\u0000\u0307\u0308\u0001\u0000\u0000\u0000\u0308\u030a\u0001"+
		"\u0000\u0000\u0000\u0309\u0307\u0001\u0000\u0000\u0000\u030a\u030b\u0003"+
		":\u001d\u0000\u030b9\u0001\u0000\u0000\u0000\u030c\u030d\u0007\u0013\u0000"+
		"\u0000\u030d\u030e\u0005\u0019\u0000\u0000\u030e\u0313\u0005o\u0000\u0000"+
		"\u030f\u0310\u0005H\u0000\u0000\u0310\u0311\u0003N\'\u0000\u0311\u0312"+
		"\u0005I\u0000\u0000\u0312\u0314\u0001\u0000\u0000\u0000\u0313\u030f\u0001"+
		"\u0000\u0000\u0000\u0313\u0314\u0001\u0000\u0000\u0000\u0314;\u0001\u0000"+
		"\u0000\u0000\u0315\u0316\u0005F\u0000\u0000\u0316\u0324\u0005G\u0000\u0000"+
		"\u0317\u031d\u0005F\u0000\u0000\u0318\u0319\u0003>\u001f\u0000\u0319\u031a"+
		"\u0005g\u0000\u0000\u031a\u031c\u0001\u0000\u0000\u0000\u031b\u0318\u0001"+
		"\u0000\u0000\u0000\u031c\u031f\u0001\u0000\u0000\u0000\u031d\u031b\u0001"+
		"\u0000\u0000\u0000\u031d\u031e\u0001\u0000\u0000\u0000\u031e\u0320\u0001"+
		"\u0000\u0000\u0000\u031f\u031d\u0001\u0000\u0000\u0000\u0320\u0321\u0003"+
		">\u001f\u0000\u0321\u0322\u0005G\u0000\u0000\u0322\u0324\u0001\u0000\u0000"+
		"\u0000\u0323\u0315\u0001\u0000\u0000\u0000\u0323\u0317\u0001\u0000\u0000"+
		"\u0000\u0324=\u0001\u0000\u0000\u0000\u0325\u0327\u0005\u0019\u0000\u0000"+
		"\u0326\u0325\u0001\u0000\u0000\u0000\u0326\u0327\u0001\u0000\u0000\u0000"+
		"\u0327\u0329\u0001\u0000\u0000\u0000\u0328\u032a\u0005!\u0000\u0000\u0329"+
		"\u0328\u0001\u0000\u0000\u0000\u0329\u032a\u0001\u0000\u0000\u0000\u032a"+
		"\u032b\u0001\u0000\u0000\u0000\u032b\u032d\u0005o\u0000\u0000\u032c\u032e"+
		"\u0005k\u0000\u0000\u032d\u032c\u0001\u0000\u0000\u0000\u032d\u032e\u0001"+
		"\u0000\u0000\u0000\u032e\u033b\u0001\u0000\u0000\u0000\u032f\u0330\u0005"+
		"!\u0000\u0000\u0330\u0331\u0005\u0019\u0000\u0000\u0331\u0333\u0005o\u0000"+
		"\u0000\u0332\u0334\u0005k\u0000\u0000\u0333\u0332\u0001\u0000\u0000\u0000"+
		"\u0333\u0334\u0001\u0000\u0000\u0000\u0334\u033b\u0001\u0000\u0000\u0000"+
		"\u0335\u0336\u0005%\u0000\u0000\u0336\u0338\u0005o\u0000\u0000\u0337\u0339"+
		"\u0005k\u0000\u0000\u0338\u0337\u0001\u0000\u0000\u0000\u0338\u0339\u0001"+
		"\u0000\u0000\u0000\u0339\u033b\u0001\u0000\u0000\u0000\u033a\u0326\u0001"+
		"\u0000\u0000\u0000\u033a\u032f\u0001\u0000\u0000\u0000\u033a\u0335\u0001"+
		"\u0000\u0000\u0000\u033b?\u0001\u0000\u0000\u0000\u033c\u033e\u0005\u0019"+
		"\u0000\u0000\u033d\u033c\u0001\u0000\u0000\u0000\u033d\u033e\u0001\u0000"+
		"\u0000\u0000\u033e\u0340\u0001\u0000\u0000\u0000\u033f\u0341\u0005!\u0000"+
		"\u0000\u0340\u033f\u0001\u0000\u0000\u0000\u0340\u0341\u0001\u0000\u0000"+
		"\u0000\u0341\u0342\u0001\u0000\u0000\u0000\u0342\u0349\u0003(\u0014\u0000"+
		"\u0343\u0344\u0005!\u0000\u0000\u0344\u0345\u0005\u0019\u0000\u0000\u0345"+
		"\u0349\u0003(\u0014\u0000\u0346\u0347\u0005%\u0000\u0000\u0347\u0349\u0003"+
		"(\u0014\u0000\u0348\u033d\u0001\u0000\u0000\u0000\u0348\u0343\u0001\u0000"+
		"\u0000\u0000\u0348\u0346\u0001\u0000\u0000\u0000\u0349A\u0001\u0000\u0000"+
		"\u0000\u034a\u034c\u0003@ \u0000\u034b\u034a\u0001\u0000\u0000\u0000\u034b"+
		"\u034c\u0001\u0000\u0000\u0000\u034cC\u0001\u0000\u0000\u0000\u034d\u034e"+
		"\u0005F\u0000\u0000\u034e\u035f\u0005G\u0000\u0000\u034f\u0350\u0005F"+
		"\u0000\u0000\u0350\u0351\u0003@ \u0000\u0351\u0352\u0005G\u0000\u0000"+
		"\u0352\u035f\u0001\u0000\u0000\u0000\u0353\u0357\u0005F\u0000\u0000\u0354"+
		"\u0355\u0003B!\u0000\u0355\u0356\u0005g\u0000\u0000\u0356\u0358\u0001"+
		"\u0000\u0000\u0000\u0357\u0354\u0001\u0000\u0000\u0000\u0358\u0359\u0001"+
		"\u0000\u0000\u0000\u0359\u0357\u0001\u0000\u0000\u0000\u0359\u035a\u0001"+
		"\u0000\u0000\u0000\u035a\u035b\u0001\u0000\u0000\u0000\u035b\u035c\u0003"+
		"B!\u0000\u035c\u035d\u0005G\u0000\u0000\u035d\u035f\u0001\u0000\u0000"+
		"\u0000\u035e\u034d\u0001\u0000\u0000\u0000\u035e\u034f\u0001\u0000\u0000"+
		"\u0000\u035e\u0353\u0001\u0000\u0000\u0000\u035fE\u0001\u0000\u0000\u0000"+
		"\u0360\u0362\u0003H$\u0000\u0361\u0360\u0001\u0000\u0000\u0000\u0362\u0363"+
		"\u0001\u0000\u0000\u0000\u0363\u0361\u0001\u0000\u0000\u0000\u0363\u0364"+
		"\u0001\u0000\u0000\u0000\u0364G\u0001\u0000\u0000\u0000\u0365\u0366\u0005"+
		"/\u0000\u0000\u0366\u0367\u0003J%\u0000\u0367\u0369\u0005*\u0000\u0000"+
		"\u0368\u036a\u0003\u0002\u0001\u0000\u0369\u0368\u0001\u0000\u0000\u0000"+
		"\u0369\u036a\u0001\u0000\u0000\u0000\u036aI\u0001\u0000\u0000\u0000\u036b"+
		"\u036c\u0003L&\u0000\u036c\u036d\u0005g\u0000\u0000\u036d\u036f\u0001"+
		"\u0000\u0000\u0000\u036e\u036b\u0001\u0000\u0000\u0000\u036f\u0372\u0001"+
		"\u0000\u0000\u0000\u0370\u036e\u0001\u0000\u0000\u0000\u0370\u0371\u0001"+
		"\u0000\u0000\u0000\u0371\u0373\u0001\u0000\u0000\u0000\u0372\u0370\u0001"+
		"\u0000\u0000\u0000\u0373\u0374\u0003L&\u0000\u0374K\u0001\u0000\u0000"+
		"\u0000\u0375\u0378\u0003(\u0014\u0000\u0376\u0378\u0003N\'\u0000\u0377"+
		"\u0375\u0001\u0000\u0000\u0000\u0377\u0376\u0001\u0000\u0000\u0000\u0378"+
		"M\u0001\u0000\u0000\u0000\u0379\u037a\u0003(\u0014\u0000\u037a\u037b\u0005"+
		"j\u0000\u0000\u037b\u037c\u0003(\u0014\u0000\u037c\u0382\u0001\u0000\u0000"+
		"\u0000\u037d\u037e\u0003(\u0014\u0000\u037e\u037f\u0005k\u0000\u0000\u037f"+
		"\u0380\u0003(\u0014\u0000\u0380\u0382\u0001\u0000\u0000\u0000\u0381\u0379"+
		"\u0001\u0000\u0000\u0000\u0381\u037d\u0001\u0000\u0000\u0000\u0382O\u0001"+
		"\u0000\u0000\u0000\u0383\u0385\u0003R)\u0000\u0384\u0383\u0001\u0000\u0000"+
		"\u0000\u0385\u0386\u0001\u0000\u0000\u0000\u0386\u0384\u0001\u0000\u0000"+
		"\u0000\u0386\u0387\u0001\u0000\u0000\u0000\u0387Q\u0001\u0000\u0000\u0000"+
		"\u0388\u0389\u0005\u0010\u0000\u0000\u0389\u038a\u0003(\u0014\u0000\u038a"+
		"\u038c\u0005*\u0000\u0000\u038b\u038d\u0003\u0002\u0001\u0000\u038c\u038b"+
		"\u0001\u0000\u0000\u0000\u038c\u038d\u0001\u0000\u0000\u0000\u038dS\u0001"+
		"\u0000\u0000\u0000\u038e\u038f\u0003V+\u0000\u038f\u0390\u0005n\u0000"+
		"\u0000\u0390\u0392\u0001\u0000\u0000\u0000\u0391\u038e\u0001\u0000\u0000"+
		"\u0000\u0392\u0395\u0001\u0000\u0000\u0000\u0393\u0391\u0001\u0000\u0000"+
		"\u0000\u0393\u0394\u0001\u0000\u0000\u0000\u0394\u0396\u0001\u0000\u0000"+
		"\u0000\u0395\u0393\u0001\u0000\u0000\u0000\u0396\u0397\u0003V+\u0000\u0397"+
		"U\u0001\u0000\u0000\u0000\u0398\u0399\u0003X,\u0000\u0399\u039a\u0005"+
		"\u0019\u0000\u0000\u039a\u039c\u0003\u0004\u0002\u0000\u039b\u039d\u0005"+
		"\n\u0000\u0000\u039c\u039b\u0001\u0000\u0000\u0000\u039c\u039d\u0001\u0000"+
		"\u0000\u0000\u039dW\u0001\u0000\u0000\u0000\u039e\u03a0\u0003\u001e\u000f"+
		"\u0000\u039f\u039e\u0001\u0000\u0000\u0000\u039f\u03a0\u0001\u0000\u0000"+
		"\u0000\u03a0\u03a6\u0001\u0000\u0000\u0000\u03a1\u03a2\u0003Z-\u0000\u03a2"+
		"\u03a3\u0005g\u0000\u0000\u03a3\u03a5\u0001\u0000\u0000\u0000\u03a4\u03a1"+
		"\u0001\u0000\u0000\u0000\u03a5\u03a8\u0001\u0000\u0000\u0000\u03a6\u03a4"+
		"\u0001\u0000\u0000\u0000\u03a6\u03a7\u0001\u0000\u0000\u0000\u03a7\u03a9"+
		"\u0001\u0000\u0000\u0000\u03a8\u03a6\u0001\u0000\u0000\u0000\u03a9\u03aa"+
		"\u0003Z-\u0000\u03aaY\u0001\u0000\u0000\u0000\u03ab\u03ad\u0005!\u0000"+
		"\u0000\u03ac\u03ab\u0001\u0000\u0000\u0000\u03ac\u03ad\u0001\u0000\u0000"+
		"\u0000\u03ad\u03ae\u0001\u0000\u0000\u0000\u03ae\u03af\u0003&\u0013\u0000"+
		"\u03af[\u0001\u0000\u0000\u0000{]bgnw}\u0084\u0089\u008e\u0094\u009a\u00a0"+
		"\u00a3\u00a8\u00ae\u00b3\u00ba\u00bf\u00c5\u00cc\u00d1\u00d8\u00dc\u00e2"+
		"\u00e7\u00f4\u00fe\u0104\u0107\u010a\u0110\u011b\u0128\u012e\u0131\u0134"+
		"\u013a\u0140\u0146\u014c\u0150\u0154\u0158\u015c\u0161\u0165\u016b\u016f"+
		"\u0175\u017b\u0181\u0185\u018b\u018f\u0193\u0197\u019c\u01a1\u01a6\u01aa"+
		"\u01ae\u01b3\u01bc\u01c0\u01c7\u01d8\u01f5\u01fa\u01fe\u0207\u020f\u0214"+
		"\u0219\u021b\u023c\u0249\u024d\u0255\u0258\u025c\u025e\u0266\u027f\u0295"+
		"\u02bd\u02c4\u02c6\u02d0\u02d5\u02d7\u02dd\u02e3\u02e8\u02ef\u02fd\u0307"+
		"\u0313\u031d\u0323\u0326\u0329\u032d\u0333\u0338\u033a\u033d\u0340\u0348"+
		"\u034b\u0359\u035e\u0363\u0369\u0370\u0377\u0381\u0386\u038c\u0393\u039c"+
		"\u039f\u03a6\u03ac";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
