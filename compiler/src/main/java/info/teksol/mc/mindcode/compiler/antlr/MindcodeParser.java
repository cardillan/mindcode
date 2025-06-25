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
		LOOP=25, MODULE=26, MLOG=27, NOINIT=28, NOINLINE=29, NULL=30, OUT=31, 
		PARAM=32, REF=33, REMOTE=34, REQUIRE=35, RETURN=36, STACK=37, THEN=38, 
		TRUE=39, VAR=40, VOID=41, VOLATILE=42, WHEN=43, WHILE=44, EQUAL=45, GREATER_THAN=46, 
		GREATER_THAN_EQUAL=47, LESS_THAN=48, LESS_THAN_EQUAL=49, NOT_EQUAL=50, 
		STRICT_EQUAL=51, STRICT_NOT_EQUAL=52, BITWISE_AND=53, BITWISE_NOT=54, 
		BITWISE_OR=55, BITWISE_XOR=56, BOOLEAN_AND=57, BOOLEAN_NOT=58, BOOLEAN_OR=59, 
		LOGICAL_AND=60, LOGICAL_NOT=61, LOGICAL_OR=62, SHIFT_LEFT=63, SHIFT_RIGHT=64, 
		USHIFT_RIGHT=65, LPAREN=66, RPAREN=67, LBRACKET=68, RBRACKET=69, DECREMENT=70, 
		DIV=71, IDIV=72, INCREMENT=73, MINUS=74, MOD=75, EMOD=76, MUL=77, PLUS=78, 
		POW=79, ASSIGN=80, ASSIGN_BITWISE_AND=81, ASSIGN_BITWISE_OR=82, ASSIGN_BITWISE_XOR=83, 
		ASSIGN_BOOLEAN_AND=84, ASSIGN_BOOLEAN_OR=85, ASSIGN_DIV=86, ASSIGN_IDIV=87, 
		ASSIGN_MINUS=88, ASSIGN_MOD=89, ASSIGN_EMOD=90, ASSIGN_MUL=91, ASSIGN_PLUS=92, 
		ASSIGN_POW=93, ASSIGN_SHIFT_LEFT=94, ASSIGN_SHIFT_RIGHT=95, ASSIGN_USHIFT_RIGHT=96, 
		AT=97, COLON=98, COMMA=99, DOLLAR=100, DOT=101, DOT2=102, DOT3=103, DOUBLEQUOTE=104, 
		QUESTION=105, SEMICOLON=106, IDENTIFIER=107, EXTIDENTIFIER=108, BUILTINIDENTIFIER=109, 
		KEYWORD=110, STRING=111, COLOR=112, NAMEDCOLOR=113, BINARY=114, HEXADECIMAL=115, 
		DECIMAL=116, FLOAT=117, CHAR=118, LBRACE=119, HASHDECLARE=120, HASHSET=121, 
		FORMATTABLELITERAL=122, RBRACE=123, COMMENTEDCOMMENT=124, ENHANCEDCOMMENT=125, 
		DOC_COMMENT=126, COMMENT=127, EMPTYCOMMENT=128, LINECOMMENT=129, NEWLINE=130, 
		WHITESPACE=131, UNKNOWN_CHAR=132, MLOGCOMMENT=133, MLOGSILENTCOMMENT=134, 
		MLOGSTRING=135, MLOGCOLOR=136, MLOGNAMEDCOLOR=137, MLOGBINARY=138, MLOGHEXADECIMAL=139, 
		MLOGDECIMAL=140, MLOGFLOAT=141, MLOGCHAR=142, MLOGBUILTIN=143, MLOGLABEL=144, 
		MLOGTOKEN=145, MLOGSEPARATOR=146, MLOGWHITESPACE=147, MLOG_UNKNOWN=148, 
		DIRECTIVEVALUE=149, DIRECTIVEASSIGN=150, DIRECTIVECOMMA=151, DIRECTIVECOMMENT=152, 
		DIRECTIVELINECOMMENT=153, DIRECTIVEWHITESPACE=154, TEXT=155, ESCAPESEQUENCE=156, 
		EMPTYPLACEHOLDER=157, INTERPOLATION=158, VARIABLEPLACEHOLDER=159, ENDOFLINE=160, 
		VARIABLE=161, FMTENDOFLINE=162;
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
			null, "'allocate'", "'begin'", "'break'", "'cached'", "'case'", "'const'", 
			"'continue'", "'descending'", "'def'", "'do'", "'elif'", "'else'", "'elseif'", 
			"'elsif'", "'end'", "'external'", "'false'", "'guarded'", "'for'", "'heap'", 
			"'if'", "'in'", "'inline'", "'linked'", "'loop'", "'module'", "'mlog'", 
			"'noinit'", "'noinline'", "'null'", "'out'", "'param'", "'ref'", "'remote'", 
			"'require'", "'return'", "'stack'", "'then'", "'true'", "'var'", "'void'", 
			"'volatile'", "'when'", "'while'", "'=='", "'>'", "'>='", "'<'", "'<='", 
			"'!='", "'==='", "'!=='", "'&'", "'~'", "'|'", "'^'", "'&&'", "'!'", 
			"'||'", "'and'", "'not'", "'or'", "'<<'", "'>>'", "'>>>'", "'('", "')'", 
			"'['", "']'", "'--'", "'/'", "'\\'", "'++'", "'-'", "'%'", "'%%'", "'*'", 
			"'+'", "'**'", null, "'&='", "'|='", "'^='", "'&&='", "'||='", "'/='", 
			"'\\='", "'-='", "'%='", "'%%='", "'*='", "'+='", "'**='", "'<<='", "'>>='", 
			"'>>>='", "'@'", "':'", null, null, "'.'", "'..'", "'...'", "'\"'", "'?'", 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "'#declare'", "'#set'", null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "'${'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BEGIN", "BREAK", "CACHED", "CASE", "CONST", "CONTINUE", 
			"DESCENDING", "DEF", "DO", "ELIF", "ELSE", "ELSEIF", "ELSIF", "END", 
			"EXTERNAL", "FALSE", "GUARDED", "FOR", "HEAP", "IF", "IN", "INLINE", 
			"LINKED", "LOOP", "MODULE", "MLOG", "NOINIT", "NOINLINE", "NULL", "OUT", 
			"PARAM", "REF", "REMOTE", "REQUIRE", "RETURN", "STACK", "THEN", "TRUE", 
			"VAR", "VOID", "VOLATILE", "WHEN", "WHILE", "EQUAL", "GREATER_THAN", 
			"GREATER_THAN_EQUAL", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "STRICT_EQUAL", 
			"STRICT_NOT_EQUAL", "BITWISE_AND", "BITWISE_NOT", "BITWISE_OR", "BITWISE_XOR", 
			"BOOLEAN_AND", "BOOLEAN_NOT", "BOOLEAN_OR", "LOGICAL_AND", "LOGICAL_NOT", 
			"LOGICAL_OR", "SHIFT_LEFT", "SHIFT_RIGHT", "USHIFT_RIGHT", "LPAREN", 
			"RPAREN", "LBRACKET", "RBRACKET", "DECREMENT", "DIV", "IDIV", "INCREMENT", 
			"MINUS", "MOD", "EMOD", "MUL", "PLUS", "POW", "ASSIGN", "ASSIGN_BITWISE_AND", 
			"ASSIGN_BITWISE_OR", "ASSIGN_BITWISE_XOR", "ASSIGN_BOOLEAN_AND", "ASSIGN_BOOLEAN_OR", 
			"ASSIGN_DIV", "ASSIGN_IDIV", "ASSIGN_MINUS", "ASSIGN_MOD", "ASSIGN_EMOD", 
			"ASSIGN_MUL", "ASSIGN_PLUS", "ASSIGN_POW", "ASSIGN_SHIFT_LEFT", "ASSIGN_SHIFT_RIGHT", 
			"ASSIGN_USHIFT_RIGHT", "AT", "COLON", "COMMA", "DOLLAR", "DOT", "DOT2", 
			"DOT3", "DOUBLEQUOTE", "QUESTION", "SEMICOLON", "IDENTIFIER", "EXTIDENTIFIER", 
			"BUILTINIDENTIFIER", "KEYWORD", "STRING", "COLOR", "NAMEDCOLOR", "BINARY", 
			"HEXADECIMAL", "DECIMAL", "FLOAT", "CHAR", "LBRACE", "HASHDECLARE", "HASHSET", 
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
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612113749060847358L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 711567641612915089L) != 0)) {
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
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612113749060847358L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 711566542101287313L) != 0)) {
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
		public List<MlogSeparatorsContext> mlogSeparators() {
			return getRuleContexts(MlogSeparatorsContext.class);
		}
		public MlogSeparatorsContext mlogSeparators(int i) {
			return getRuleContext(MlogSeparatorsContext.class,i);
		}
		public List<MlogStatementContext> mlogStatement() {
			return getRuleContexts(MlogStatementContext.class);
		}
		public MlogStatementContext mlogStatement(int i) {
			return getRuleContext(MlogStatementContext.class,i);
		}
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
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
			setState(136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 133)) & ~0x3f) == 0 && ((1L << (_la - 133)) & 6145L) != 0)) {
				{
				setState(135);
				mlogStatement();
				}
			}

			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MLOGWHITESPACE) {
				{
				{
				setState(138);
				match(MLOGWHITESPACE);
				}
				}
				setState(143);
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
	public static class MlogSeparatorsContext extends ParserRuleContext {
		public List<TerminalNode> MLOGWHITESPACE() { return getTokens(MindcodeParser.MLOGWHITESPACE); }
		public TerminalNode MLOGWHITESPACE(int i) {
			return getToken(MindcodeParser.MLOGWHITESPACE, i);
		}
		public List<TerminalNode> MLOGSEPARATOR() { return getTokens(MindcodeParser.MLOGSEPARATOR); }
		public TerminalNode MLOGSEPARATOR(int i) {
			return getToken(MindcodeParser.MLOGSEPARATOR, i);
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
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MLOGWHITESPACE) {
				{
				{
				setState(144);
				match(MLOGWHITESPACE);
				}
				}
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(157); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(150);
				match(MLOGSEPARATOR);
				setState(154);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(151);
						match(MLOGWHITESPACE);
						}
						} 
					}
					setState(156);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				}
				}
				}
				setState(159); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==MLOGSEPARATOR );
			}
		}
		catch (RecognitionException re) {
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
		public Token label;
		public TerminalNode MLOGLABEL() { return getToken(MindcodeParser.MLOGLABEL, 0); }
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
	public static class AstMlogCommentContext extends MlogStatementContext {
		public Token comment;
		public TerminalNode MLOGCOMMENT() { return getToken(MindcodeParser.MLOGCOMMENT, 0); }
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
		public MlogInstructionContext mlogInstruction() {
			return getRuleContext(MlogInstructionContext.class,0);
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
		public Token whitespace;
		public Token comment;
		public MlogInstructionContext mlogInstruction() {
			return getRuleContext(MlogInstructionContext.class,0);
		}
		public TerminalNode MLOGCOMMENT() { return getToken(MindcodeParser.MLOGCOMMENT, 0); }
		public TerminalNode MLOGWHITESPACE() { return getToken(MindcodeParser.MLOGWHITESPACE, 0); }
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
			setState(170);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new AstMlogLabelContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				((AstMlogLabelContext)_localctx).label = match(MLOGLABEL);
				}
				break;
			case 2:
				_localctx = new AstMlogInstructionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(162);
				mlogInstruction();
				}
				break;
			case 3:
				_localctx = new AstMlogInstructionWithCommentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(163);
				mlogInstruction();
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MLOGWHITESPACE) {
					{
					setState(164);
					((AstMlogInstructionWithCommentContext)_localctx).whitespace = match(MLOGWHITESPACE);
					}
				}

				setState(167);
				((AstMlogInstructionWithCommentContext)_localctx).comment = match(MLOGCOMMENT);
				}
				break;
			case 4:
				_localctx = new AstMlogCommentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(169);
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
			setState(172);
			((MlogInstructionContext)_localctx).opcode = match(MLOGTOKEN);
			setState(181);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(174); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(173);
						match(MLOGWHITESPACE);
						}
						}
						setState(176); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==MLOGWHITESPACE );
					setState(178);
					((MlogInstructionContext)_localctx).mlogTokens = mlogTokenOrLiteral();
					}
					} 
				}
				setState(183);
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
			setState(194);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MLOGTOKEN:
				_localctx = new AstMlogTokenContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(184);
				((AstMlogTokenContext)_localctx).token = match(MLOGTOKEN);
				}
				break;
			case MLOGBUILTIN:
				_localctx = new AstMlogBuiltinContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				((AstMlogBuiltinContext)_localctx).builtin = match(MLOGBUILTIN);
				}
				break;
			case MLOGSTRING:
				_localctx = new AstMlogStringContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(186);
				((AstMlogStringContext)_localctx).literal = match(MLOGSTRING);
				}
				break;
			case MLOGCOLOR:
				_localctx = new AstMlogColorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(187);
				((AstMlogColorContext)_localctx).literal = match(MLOGCOLOR);
				}
				break;
			case MLOGNAMEDCOLOR:
				_localctx = new AstMlogNamedColorContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(188);
				((AstMlogNamedColorContext)_localctx).literal = match(MLOGNAMEDCOLOR);
				}
				break;
			case MLOGBINARY:
				_localctx = new AstMlogBinaryContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(189);
				((AstMlogBinaryContext)_localctx).literal = match(MLOGBINARY);
				}
				break;
			case MLOGHEXADECIMAL:
				_localctx = new AstMlogHexadecimalContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(190);
				((AstMlogHexadecimalContext)_localctx).literal = match(MLOGHEXADECIMAL);
				}
				break;
			case MLOGDECIMAL:
				_localctx = new AstMlogDecimalContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(191);
				((AstMlogDecimalContext)_localctx).literal = match(MLOGDECIMAL);
				}
				break;
			case MLOGFLOAT:
				_localctx = new AstMlogFloatContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(192);
				((AstMlogFloatContext)_localctx).literal = match(MLOGFLOAT);
				}
				break;
			case MLOGCHAR:
				_localctx = new AstMlogCharContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(193);
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
			setState(210);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(196);
				match(LPAREN);
				setState(197);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(198);
				match(LPAREN);
				setState(204);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(199);
						astMlogVariable();
						setState(200);
						match(COMMA);
						}
						} 
					}
					setState(206);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
				}
				setState(207);
				astMlogVariable();
				setState(208);
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
			setState(222);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(212);
					((AstMlogVariableContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(215);
					((AstMlogVariableContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(218);
				((AstMlogVariableContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(219);
				((AstMlogVariableContext)_localctx).modifier_out = match(OUT);
				setState(220);
				((AstMlogVariableContext)_localctx).modifier_in = match(IN);
				setState(221);
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
		enterRule(_localctx, 22, RULE_statement);
		int _la;
		try {
			setState(366);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				_localctx = new AstExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(224);
				expression(0);
				}
				break;
			case 2:
				_localctx = new AstDirectiveContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(225);
				directive();
				}
				break;
			case 3:
				_localctx = new AstVariableDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(226);
				variableDeclaration();
				}
				break;
			case 4:
				_localctx = new AstModuleDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(227);
				match(MODULE);
				setState(228);
				((AstModuleDeclarationContext)_localctx).name = match(IDENTIFIER);
				}
				break;
			case 5:
				_localctx = new AstEnhancedCommentContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(229);
				match(ENHANCEDCOMMENT);
				setState(233);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 155)) & ~0x3f) == 0 && ((1L << (_la - 155)) & 31L) != 0)) {
					{
					{
					setState(230);
					formattableContents();
					}
					}
					setState(235);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 6:
				_localctx = new AstAllocationsContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(236);
				match(ALLOCATE);
				setState(237);
				allocations();
				}
				break;
			case 7:
				_localctx = new AstParameterContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(238);
				match(PARAM);
				setState(239);
				((AstParameterContext)_localctx).name = match(IDENTIFIER);
				setState(240);
				match(ASSIGN);
				setState(241);
				((AstParameterContext)_localctx).value = expression(0);
				}
				break;
			case 8:
				_localctx = new AstRequireFileContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(242);
				match(REQUIRE);
				setState(243);
				((AstRequireFileContext)_localctx).file = match(STRING);
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REMOTE) {
					{
					setState(244);
					match(REMOTE);
					setState(245);
					((AstRequireFileContext)_localctx).processors = identifierList();
					}
				}

				}
				break;
			case 9:
				_localctx = new AstRequireLibraryContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(248);
				match(REQUIRE);
				setState(249);
				((AstRequireLibraryContext)_localctx).library = match(IDENTIFIER);
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REMOTE) {
					{
					setState(250);
					match(REMOTE);
					setState(251);
					((AstRequireLibraryContext)_localctx).processors = identifierList();
					}
				}

				}
				break;
			case 10:
				_localctx = new AstFunctionDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 17725128704L) != 0)) {
					{
					setState(254);
					((AstFunctionDeclarationContext)_localctx).callType = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 17725128704L) != 0)) ) {
						((AstFunctionDeclarationContext)_localctx).callType = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(257);
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
				setState(258);
				((AstFunctionDeclarationContext)_localctx).name = match(IDENTIFIER);
				setState(259);
				((AstFunctionDeclarationContext)_localctx).params = parameterList();
				setState(261);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(260);
					((AstFunctionDeclarationContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(263);
				match(END);
				}
				break;
			case 11:
				_localctx = new AstForEachLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(267);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(265);
					((AstForEachLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(266);
					match(COLON);
					}
				}

				setState(269);
				match(FOR);
				setState(270);
				((AstForEachLoopStatementContext)_localctx).iterators = iteratorsValuesGroups();
				setState(271);
				match(DO);
				setState(273);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
				case 1:
					{
					setState(272);
					((AstForEachLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(275);
				match(END);
				}
				break;
			case 12:
				_localctx = new AstIteratedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(279);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(277);
					((AstIteratedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(278);
					match(COLON);
					}
				}

				setState(281);
				match(FOR);
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612093849864470640L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 81062594269417873L) != 0)) {
					{
					setState(282);
					((AstIteratedForLoopStatementContext)_localctx).init = declarationOrExpressionList();
					}
				}

				setState(285);
				match(SEMICOLON);
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612088334840922144L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 81062594269417873L) != 0)) {
					{
					setState(286);
					((AstIteratedForLoopStatementContext)_localctx).condition = expression(0);
					}
				}

				setState(289);
				match(SEMICOLON);
				setState(291);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612088334840922144L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 81062594269417873L) != 0)) {
					{
					setState(290);
					((AstIteratedForLoopStatementContext)_localctx).update = expressionList();
					}
				}

				setState(293);
				match(DO);
				setState(295);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
				case 1:
					{
					setState(294);
					((AstIteratedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(297);
				match(END);
				}
				break;
			case 13:
				_localctx = new AstRangedForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(300);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(298);
					((AstRangedForLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(299);
					match(COLON);
					}
				}

				setState(302);
				match(FOR);
				setState(304);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR) {
					{
					setState(303);
					((AstRangedForLoopStatementContext)_localctx).type = typeSpec();
					}
				}

				setState(306);
				((AstRangedForLoopStatementContext)_localctx).control = lvalue();
				setState(307);
				match(IN);
				setState(308);
				((AstRangedForLoopStatementContext)_localctx).range = astRange();
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DESCENDING) {
					{
					setState(309);
					match(DESCENDING);
					}
				}

				setState(312);
				match(DO);
				setState(314);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(313);
					((AstRangedForLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(316);
				match(END);
				}
				break;
			case 14:
				_localctx = new AstWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(318);
					((AstWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(319);
					match(COLON);
					}
				}

				setState(322);
				match(WHILE);
				setState(323);
				((AstWhileLoopStatementContext)_localctx).condition = expression(0);
				setState(324);
				match(DO);
				setState(326);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
				case 1:
					{
					setState(325);
					((AstWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(328);
				match(END);
				}
				break;
			case 15:
				_localctx = new AstDoWhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(330);
					((AstDoWhileLoopStatementContext)_localctx).label = match(IDENTIFIER);
					setState(331);
					match(COLON);
					}
				}

				setState(334);
				match(DO);
				setState(336);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
				case 1:
					{
					setState(335);
					((AstDoWhileLoopStatementContext)_localctx).body = astStatementList();
					}
					break;
				}
				setState(339);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LOOP) {
					{
					setState(338);
					((AstDoWhileLoopStatementContext)_localctx).loop = match(LOOP);
					}
				}

				setState(341);
				match(WHILE);
				setState(342);
				((AstDoWhileLoopStatementContext)_localctx).condition = expression(0);
				}
				break;
			case 16:
				_localctx = new AstBreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(343);
				match(BREAK);
				setState(345);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(344);
					((AstBreakStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 17:
				_localctx = new AstContinueStatementContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(347);
				match(CONTINUE);
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(348);
					((AstContinueStatementContext)_localctx).label = match(IDENTIFIER);
					}
				}

				}
				break;
			case 18:
				_localctx = new AstReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(351);
				match(RETURN);
				setState(353);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612088334840922144L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 81062594269417873L) != 0)) {
					{
					setState(352);
					((AstReturnStatementContext)_localctx).value = expression(0);
					}
				}

				}
				break;
			case 19:
				_localctx = new AstCodeBlockContext(_localctx);
				enterOuterAlt(_localctx, 19);
				{
				setState(355);
				match(BEGIN);
				setState(357);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
				case 1:
					{
					setState(356);
					((AstCodeBlockContext)_localctx).exp = astStatementList();
					}
					break;
				}
				setState(359);
				match(END);
				}
				break;
			case 20:
				_localctx = new AstMlogBlockContext(_localctx);
				enterOuterAlt(_localctx, 20);
				{
				setState(360);
				match(MLOG);
				setState(362);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LPAREN) {
					{
					setState(361);
					((AstMlogBlockContext)_localctx).variables = mlogVariableList();
					}
				}

				setState(364);
				match(LBRACE);
				setState(365);
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
			setState(370);
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
				setState(368);
				((DeclarationOrExpressionListContext)_localctx).decl = variableDeclaration();
				}
				break;
			case CASE:
			case END:
			case FALSE:
			case IF:
			case MLOG:
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
				setState(369);
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
		enterRule(_localctx, 26, RULE_variableDeclaration);
		int _la;
		try {
			setState(388);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(375);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4415511920720L) != 0)) {
					{
					{
					setState(372);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(377);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(378);
				((VariableDeclarationContext)_localctx).type = typeSpec();
				setState(379);
				((VariableDeclarationContext)_localctx).variables = variableSpecList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(382); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(381);
					((VariableDeclarationContext)_localctx).modifiers = declModifier();
					}
					}
					setState(384); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 4415511920720L) != 0) );
				setState(386);
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
		enterRule(_localctx, 28, RULE_declModifier);
		try {
			setState(413);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(390);
				((DeclModifierContext)_localctx).modifier = match(CONST);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(391);
				((DeclModifierContext)_localctx).modifier = match(CACHED);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(392);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(394);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(393);
					((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(396);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(397);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(398);
				match(LBRACKET);
				setState(399);
				((DeclModifierContext)_localctx).index = expression(0);
				setState(400);
				match(RBRACKET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(402);
				((DeclModifierContext)_localctx).modifier = match(EXTERNAL);
				setState(403);
				((DeclModifierContext)_localctx).memory = match(IDENTIFIER);
				setState(404);
				match(LBRACKET);
				setState(405);
				((DeclModifierContext)_localctx).range = astRange();
				setState(406);
				match(RBRACKET);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(408);
				((DeclModifierContext)_localctx).modifier = match(GUARDED);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(409);
				((DeclModifierContext)_localctx).modifier = match(LINKED);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(410);
				((DeclModifierContext)_localctx).modifier = match(NOINIT);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(411);
				((DeclModifierContext)_localctx).modifier = match(REMOTE);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(412);
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
			setState(415);
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
			setState(422);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(417);
					variableSpecification();
					setState(418);
					match(COMMA);
					}
					} 
				}
				setState(424);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			}
			setState(425);
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
			setState(442);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(427);
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
				setState(430);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(428);
					match(ASSIGN);
					setState(429);
					((VariableSpecificationContext)_localctx).exp = expression(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(432);
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
				setState(433);
				match(LBRACKET);
				setState(435);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612088334840922144L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 81062594269417873L) != 0)) {
					{
					setState(434);
					((VariableSpecificationContext)_localctx).length = expression(0);
					}
				}

				setState(437);
				match(RBRACKET);
				setState(440);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(438);
					match(ASSIGN);
					setState(439);
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
			setState(444);
			match(LPAREN);
			setState(445);
			((ValueListContext)_localctx).values = expressionList();
			setState(446);
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
			setState(475);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				_localctx = new AstIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(448);
				((AstIdentifierContext)_localctx).id = match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new AstIdentifierExtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(449);
				((AstIdentifierExtContext)_localctx).id = match(EXTIDENTIFIER);
				}
				break;
			case 3:
				_localctx = new AstBuiltInIdentifierContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(450);
				((AstBuiltInIdentifierContext)_localctx).builtin = match(BUILTINIDENTIFIER);
				}
				break;
			case 4:
				_localctx = new AstArrayAccessContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(451);
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
				setState(452);
				match(LBRACKET);
				setState(453);
				((AstArrayAccessContext)_localctx).index = expression(0);
				setState(454);
				match(RBRACKET);
				}
				break;
			case 5:
				_localctx = new AstSubarrayContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(456);
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
				setState(457);
				match(LBRACKET);
				setState(458);
				((AstSubarrayContext)_localctx).range = astRange();
				setState(459);
				match(RBRACKET);
				}
				break;
			case 6:
				_localctx = new AstRemoteArrayContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(461);
				((AstRemoteArrayContext)_localctx).processor = match(IDENTIFIER);
				setState(462);
				match(DOT);
				setState(463);
				((AstRemoteArrayContext)_localctx).array = match(IDENTIFIER);
				setState(464);
				match(LBRACKET);
				setState(465);
				((AstRemoteArrayContext)_localctx).index = expression(0);
				setState(466);
				match(RBRACKET);
				}
				break;
			case 7:
				_localctx = new AstRemoteSubarrayContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(468);
				((AstRemoteSubarrayContext)_localctx).processor = match(IDENTIFIER);
				setState(469);
				match(DOT);
				setState(470);
				((AstRemoteSubarrayContext)_localctx).array = match(IDENTIFIER);
				setState(471);
				match(LBRACKET);
				setState(472);
				((AstRemoteSubarrayContext)_localctx).range = astRange();
				setState(473);
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
		int _startState = 40;
		enterRecursionRule(_localctx, 40, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				{
				_localctx = new ExpLvalueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(478);
				lvalue();
				}
				break;
			case 2:
				{
				_localctx = new AstKeywordContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(479);
				match(KEYWORD);
				}
				break;
			case 3:
				{
				_localctx = new AstFunctionCallEndContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(480);
				match(END);
				setState(481);
				match(LPAREN);
				setState(482);
				match(RPAREN);
				}
				break;
			case 4:
				{
				_localctx = new AstFunctionCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(483);
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
				setState(484);
				((AstFunctionCallContext)_localctx).args = argumentList();
				}
				break;
			case 5:
				{
				_localctx = new AstCaseExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(485);
				match(CASE);
				setState(486);
				((AstCaseExpressionContext)_localctx).exp = expression(0);
				setState(488);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WHEN) {
					{
					setState(487);
					((AstCaseExpressionContext)_localctx).alternatives = caseAlternatives();
					}
				}

				setState(492);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(490);
					match(ELSE);
					setState(491);
					((AstCaseExpressionContext)_localctx).elseBranch = astStatementList();
					}
				}

				setState(494);
				match(END);
				}
				break;
			case 6:
				{
				_localctx = new AstIfExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(496);
				match(IF);
				setState(497);
				((AstIfExpressionContext)_localctx).condition = expression(0);
				setState(498);
				match(THEN);
				setState(500);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
				case 1:
					{
					setState(499);
					((AstIfExpressionContext)_localctx).trueBranch = astStatementList();
					}
					break;
				}
				setState(503);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSIF) {
					{
					setState(502);
					((AstIfExpressionContext)_localctx).elsif = elsifBranches();
					}
				}

				setState(509);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(505);
					match(ELSE);
					setState(507);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
					case 1:
						{
						setState(506);
						((AstIfExpressionContext)_localctx).falseBranch = astStatementList();
						}
						break;
					}
					}
				}

				setState(511);
				match(END);
				}
				break;
			case 7:
				{
				_localctx = new AstFormattableLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(513);
				match(FORMATTABLELITERAL);
				setState(517);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 155)) & ~0x3f) == 0 && ((1L << (_la - 155)) & 31L) != 0)) {
					{
					{
					setState(514);
					formattableContents();
					}
					}
					setState(519);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(520);
				match(DOUBLEQUOTE);
				}
				break;
			case 8:
				{
				_localctx = new AstLiteralStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(521);
				match(STRING);
				}
				break;
			case 9:
				{
				_localctx = new AstLiteralColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(522);
				match(COLOR);
				}
				break;
			case 10:
				{
				_localctx = new AstLiteralNamedColorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(523);
				match(NAMEDCOLOR);
				}
				break;
			case 11:
				{
				_localctx = new AstLiteralBinaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(524);
				match(BINARY);
				}
				break;
			case 12:
				{
				_localctx = new AstLiteralHexadecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(525);
				match(HEXADECIMAL);
				}
				break;
			case 13:
				{
				_localctx = new AstLiteralDecimalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(526);
				match(DECIMAL);
				}
				break;
			case 14:
				{
				_localctx = new AstLiteralFloatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(527);
				match(FLOAT);
				}
				break;
			case 15:
				{
				_localctx = new AstLiteralCharContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(528);
				match(CHAR);
				}
				break;
			case 16:
				{
				_localctx = new AstLiteralNullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(529);
				match(NULL);
				}
				break;
			case 17:
				{
				_localctx = new AstLiteralBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(530);
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
				setState(531);
				((AstOperatorIncDecPostfixContext)_localctx).exp = lvalue();
				setState(532);
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
				setState(534);
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
				setState(535);
				((AstOperatorIncDecPrefixContext)_localctx).exp = lvalue();
				}
				break;
			case 20:
				{
				_localctx = new AstOperatorUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(536);
				((AstOperatorUnaryContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 54)) & ~0x3f) == 0 && ((1L << (_la - 54)) & 17825937L) != 0)) ) {
					((AstOperatorUnaryContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(537);
				((AstOperatorUnaryContext)_localctx).exp = expression(14);
				}
				break;
			case 21:
				{
				_localctx = new AstParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(538);
				match(LPAREN);
				setState(539);
				((AstParenthesesContext)_localctx).exp = expression(0);
				setState(540);
				match(RPAREN);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(595);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(593);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
					case 1:
						{
						_localctx = new AstOperatorBinaryExpContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryExpContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(544);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(545);
						((AstOperatorBinaryExpContext)_localctx).op = match(POW);
						setState(546);
						((AstOperatorBinaryExpContext)_localctx).right = expression(14);
						}
						break;
					case 2:
						{
						_localctx = new AstOperatorBinaryMulContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryMulContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(547);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(548);
						((AstOperatorBinaryMulContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & 115L) != 0)) ) {
							((AstOperatorBinaryMulContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(549);
						((AstOperatorBinaryMulContext)_localctx).right = expression(13);
						}
						break;
					case 3:
						{
						_localctx = new AstOperatorBinaryAddContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryAddContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(550);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(551);
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
						setState(552);
						((AstOperatorBinaryAddContext)_localctx).right = expression(12);
						}
						break;
					case 4:
						{
						_localctx = new AstOperatorBinaryShiftContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryShiftContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(553);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(554);
						((AstOperatorBinaryShiftContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & 7L) != 0)) ) {
							((AstOperatorBinaryShiftContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(555);
						((AstOperatorBinaryShiftContext)_localctx).right = expression(11);
						}
						break;
					case 5:
						{
						_localctx = new AstOperatorBinaryBitwiseAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(556);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(557);
						((AstOperatorBinaryBitwiseAndContext)_localctx).op = match(BITWISE_AND);
						setState(558);
						((AstOperatorBinaryBitwiseAndContext)_localctx).right = expression(10);
						}
						break;
					case 6:
						{
						_localctx = new AstOperatorBinaryBitwiseOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryBitwiseOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(559);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(560);
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
						setState(561);
						((AstOperatorBinaryBitwiseOrContext)_localctx).right = expression(9);
						}
						break;
					case 7:
						{
						_localctx = new AstOperatorBinaryInequalityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryInequalityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(562);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(563);
						((AstOperatorBinaryInequalityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1055531162664960L) != 0)) ) {
							((AstOperatorBinaryInequalityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(564);
						((AstOperatorBinaryInequalityContext)_localctx).right = expression(8);
						}
						break;
					case 8:
						{
						_localctx = new AstOperatorBinaryEqualityContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryEqualityContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(565);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(566);
						((AstOperatorBinaryEqualityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 7916483719987200L) != 0)) ) {
							((AstOperatorBinaryEqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(567);
						((AstOperatorBinaryEqualityContext)_localctx).right = expression(7);
						}
						break;
					case 9:
						{
						_localctx = new AstOperatorBinaryLogicalAndContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryLogicalAndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(568);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(569);
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
						setState(570);
						((AstOperatorBinaryLogicalAndContext)_localctx).right = expression(6);
						}
						break;
					case 10:
						{
						_localctx = new AstOperatorBinaryLogicalOrContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorBinaryLogicalOrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(571);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(572);
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
						setState(573);
						((AstOperatorBinaryLogicalOrContext)_localctx).right = expression(5);
						}
						break;
					case 11:
						{
						_localctx = new AstOperatorTernaryContext(new ExpressionContext(_parentctx, _parentState));
						((AstOperatorTernaryContext)_localctx).condition = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(574);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(575);
						match(QUESTION);
						setState(576);
						((AstOperatorTernaryContext)_localctx).trueBranch = expression(0);
						setState(577);
						match(COLON);
						setState(578);
						((AstOperatorTernaryContext)_localctx).falseBranch = expression(3);
						}
						break;
					case 12:
						{
						_localctx = new AstAssignmentContext(new ExpressionContext(_parentctx, _parentState));
						((AstAssignmentContext)_localctx).target = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(580);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(581);
						((AstAssignmentContext)_localctx).operation = _input.LT(1);
						_la = _input.LA(1);
						if ( !(((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & 131071L) != 0)) ) {
							((AstAssignmentContext)_localctx).operation = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(582);
						((AstAssignmentContext)_localctx).value = expression(2);
						}
						break;
					case 13:
						{
						_localctx = new AstMethodCallContext(new ExpressionContext(_parentctx, _parentState));
						((AstMethodCallContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(583);
						if (!(precpred(_ctx, 32))) throw new FailedPredicateException(this, "precpred(_ctx, 32)");
						setState(584);
						match(DOT);
						setState(585);
						((AstMethodCallContext)_localctx).function = match(IDENTIFIER);
						setState(586);
						((AstMethodCallContext)_localctx).args = argumentList();
						}
						break;
					case 14:
						{
						_localctx = new AstMemberAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstMemberAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(587);
						if (!(precpred(_ctx, 31))) throw new FailedPredicateException(this, "precpred(_ctx, 31)");
						setState(588);
						match(DOT);
						setState(589);
						((AstMemberAccessContext)_localctx).member = match(IDENTIFIER);
						}
						break;
					case 15:
						{
						_localctx = new AstPropertyAccessContext(new ExpressionContext(_parentctx, _parentState));
						((AstPropertyAccessContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(590);
						if (!(precpred(_ctx, 30))) throw new FailedPredicateException(this, "precpred(_ctx, 30)");
						setState(591);
						match(DOT);
						setState(592);
						((AstPropertyAccessContext)_localctx).property = match(BUILTINIDENTIFIER);
						}
						break;
					}
					} 
				}
				setState(597);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
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
			setState(605);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT:
				_localctx = new FormattableTextContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(598);
				match(TEXT);
				}
				break;
			case ESCAPESEQUENCE:
				_localctx = new FormattableEscapedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(599);
				match(ESCAPESEQUENCE);
				}
				break;
			case INTERPOLATION:
				_localctx = new FormattableInterpolationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(600);
				match(INTERPOLATION);
				setState(601);
				expression(0);
				setState(602);
				match(RBRACE);
				}
				break;
			case EMPTYPLACEHOLDER:
			case VARIABLEPLACEHOLDER:
				_localctx = new PlaceholderContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(604);
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
			setState(612);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EMPTYPLACEHOLDER:
				_localctx = new FormattablePlaceholderEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(607);
				match(EMPTYPLACEHOLDER);
				}
				break;
			case VARIABLEPLACEHOLDER:
				_localctx = new FormattablePlaceholderVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(608);
				match(VARIABLEPLACEHOLDER);
				setState(610);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VARIABLE) {
					{
					setState(609);
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
		enterRule(_localctx, 46, RULE_directive);
		int _la;
		try {
			setState(623);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case HASHSET:
				_localctx = new AstDirectiveSetContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(614);
				match(HASHSET);
				setState(615);
				((AstDirectiveSetContext)_localctx).option = astDirectiveValue();
				setState(618);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DIRECTIVEASSIGN) {
					{
					setState(616);
					match(DIRECTIVEASSIGN);
					setState(617);
					((AstDirectiveSetContext)_localctx).value = directiveValues();
					}
				}

				}
				break;
			case HASHDECLARE:
				_localctx = new AstDirectiveDeclareContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(620);
				match(HASHDECLARE);
				setState(621);
				((AstDirectiveDeclareContext)_localctx).category = match(IDENTIFIER);
				setState(622);
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
			setState(630);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(625);
					astDirectiveValue();
					setState(626);
					match(DIRECTIVECOMMA);
					}
					} 
				}
				setState(632);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
			}
			setState(633);
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
			setState(635);
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
			setState(637);
			_la = _input.LA(1);
			if ( !(((((_la - 107)) & ~0x3f) == 0 && ((1L << (_la - 107)) & 13L) != 0)) ) {
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
			setState(644);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(639);
					astKeywordOrBuiltin();
					setState(640);
					match(COMMA);
					}
					} 
				}
				setState(646);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
			}
			setState(647);
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
			setState(654);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(649);
					astAllocation();
					setState(650);
					match(COMMA);
					}
					} 
				}
				setState(656);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			}
			setState(657);
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
			setState(659);
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
			setState(660);
			match(IN);
			setState(661);
			((AstAllocationContext)_localctx).id = match(IDENTIFIER);
			setState(666);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACKET) {
				{
				setState(662);
				match(LBRACKET);
				setState(663);
				((AstAllocationContext)_localctx).range = astRange();
				setState(664);
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
			setState(682);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(668);
				match(LPAREN);
				setState(669);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(670);
				match(LPAREN);
				setState(676);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(671);
						astFunctionParameter();
						setState(672);
						match(COMMA);
						}
						} 
					}
					setState(678);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
				}
				setState(679);
				astFunctionParameter();
				setState(680);
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
			setState(705);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(685);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(684);
					((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(688);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(687);
					((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(690);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(692);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(691);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(694);
				((AstFunctionParameterContext)_localctx).modifier_out = match(OUT);
				setState(695);
				((AstFunctionParameterContext)_localctx).modifier_in = match(IN);
				setState(696);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(698);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(697);
					((AstFunctionParameterContext)_localctx).varargs = match(DOT3);
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(700);
				((AstFunctionParameterContext)_localctx).modifier_ref = match(REF);
				setState(701);
				((AstFunctionParameterContext)_localctx).name = match(IDENTIFIER);
				setState(703);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DOT3) {
					{
					setState(702);
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
			setState(719);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(708);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IN) {
					{
					setState(707);
					((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
					}
				}

				setState(711);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(710);
					((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
					}
				}

				setState(713);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(714);
				((AstFunctionArgumentContext)_localctx).modifier_out = match(OUT);
				setState(715);
				((AstFunctionArgumentContext)_localctx).modifier_in = match(IN);
				setState(716);
				((AstFunctionArgumentContext)_localctx).arg = expression(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(717);
				((AstFunctionArgumentContext)_localctx).modifier_ref = match(REF);
				setState(718);
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
			setState(722);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2612088345582534688L) != 0) || ((((_la - 66)) & ~0x3f) == 0 && ((1L << (_la - 66)) & 81062594269417873L) != 0)) {
				{
				setState(721);
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
			setState(741);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(724);
				match(LPAREN);
				setState(725);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(726);
				match(LPAREN);
				setState(727);
				astFunctionArgument();
				setState(728);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(730);
				match(LPAREN);
				setState(734); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(731);
						astOptionalFunctionArgument();
						setState(732);
						match(COMMA);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(736); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(738);
				astOptionalFunctionArgument();
				setState(739);
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
			setState(744); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(743);
				astCaseAlternative();
				}
				}
				setState(746); 
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
			setState(748);
			match(WHEN);
			setState(749);
			((AstCaseAlternativeContext)_localctx).values = whenValueList();
			setState(750);
			match(THEN);
			setState(752);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				{
				setState(751);
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
			setState(759);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,96,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(754);
					whenValue();
					setState(755);
					match(COMMA);
					}
					} 
				}
				setState(761);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,96,_ctx);
			}
			setState(762);
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
			setState(766);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(764);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(765);
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
			setState(776);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(768);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(769);
				((AstRangeContext)_localctx).operator = match(DOT2);
				setState(770);
				((AstRangeContext)_localctx).lastValue = expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(772);
				((AstRangeContext)_localctx).firstValue = expression(0);
				setState(773);
				((AstRangeContext)_localctx).operator = match(DOT3);
				setState(774);
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
			setState(779); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(778);
				elsifBranch();
				}
				}
				setState(781); 
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
			setState(783);
			match(ELSIF);
			setState(784);
			((ElsifBranchContext)_localctx).condition = expression(0);
			setState(785);
			match(THEN);
			setState(787);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,100,_ctx) ) {
			case 1:
				{
				setState(786);
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
			setState(794);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,101,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(789);
					astIteratorsValuesGroup();
					setState(790);
					match(SEMICOLON);
					}
					} 
				}
				setState(796);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,101,_ctx);
			}
			setState(797);
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
			setState(799);
			iteratorGroup();
			setState(800);
			match(IN);
			setState(801);
			expressionList();
			setState(803);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DESCENDING) {
				{
				setState(802);
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
			setState(806);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR) {
				{
				setState(805);
				((IteratorGroupContext)_localctx).type = typeSpec();
				}
			}

			setState(813);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,104,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(808);
					astIterator();
					setState(809);
					match(COMMA);
					}
					} 
				}
				setState(815);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,104,_ctx);
			}
			setState(816);
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
			setState(819);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OUT) {
				{
				setState(818);
				((AstIteratorContext)_localctx).modifier = match(OUT);
				}
			}

			setState(821);
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
		"\u0004\u0001\u00a2\u0338\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
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
		"\u0005\u0004\u0083\b\u0004\n\u0004\f\u0004\u0086\t\u0004\u0001\u0004\u0003"+
		"\u0004\u0089\b\u0004\u0001\u0004\u0005\u0004\u008c\b\u0004\n\u0004\f\u0004"+
		"\u008f\t\u0004\u0001\u0005\u0005\u0005\u0092\b\u0005\n\u0005\f\u0005\u0095"+
		"\t\u0005\u0001\u0005\u0001\u0005\u0005\u0005\u0099\b\u0005\n\u0005\f\u0005"+
		"\u009c\t\u0005\u0004\u0005\u009e\b\u0005\u000b\u0005\f\u0005\u009f\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00a6\b\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00ab\b\u0006\u0001\u0007\u0001"+
		"\u0007\u0004\u0007\u00af\b\u0007\u000b\u0007\f\u0007\u00b0\u0001\u0007"+
		"\u0005\u0007\u00b4\b\u0007\n\u0007\f\u0007\u00b7\t\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003"+
		"\b\u00c3\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t\u00cb"+
		"\b\t\n\t\f\t\u00ce\t\t\u0001\t\u0001\t\u0001\t\u0003\t\u00d3\b\t\u0001"+
		"\n\u0003\n\u00d6\b\n\u0001\n\u0003\n\u00d9\b\n\u0001\n\u0001\n\u0001\n"+
		"\u0001\n\u0003\n\u00df\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u00e8\b\u000b\n\u000b"+
		"\f\u000b\u00eb\t\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000b\u00f7\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000b\u00fd\b\u000b\u0001\u000b\u0003\u000b\u0100\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0106\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u010c\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0112\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0118\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\u011c\b\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000b\u0120\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0124\b\u000b"+
		"\u0001\u000b\u0001\u000b\u0003\u000b\u0128\b\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u012d\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0131\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0137\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u013b\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0141\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0147\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u014d\b\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\u0151\b\u000b\u0001\u000b\u0003\u000b\u0154"+
		"\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u015a"+
		"\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u015e\b\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000b\u0162\b\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0166\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u016b\b"+
		"\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u016f\b\u000b\u0001\f\u0001"+
		"\f\u0003\f\u0173\b\f\u0001\r\u0005\r\u0176\b\r\n\r\f\r\u0179\t\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0004\r\u017f\b\r\u000b\r\f\r\u0180\u0001\r"+
		"\u0001\r\u0003\r\u0185\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0003\u000e\u018b\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u019e\b\u000e\u0001\u000f\u0001\u000f\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0005\u0010\u01a5\b\u0010\n\u0010\f\u0010\u01a8"+
		"\t\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0003"+
		"\u0011\u01af\b\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u01b4"+
		"\b\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u01b9\b\u0011"+
		"\u0003\u0011\u01bb\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u01dc\b\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u01e9\b\u0014"+
		"\u0001\u0014\u0001\u0014\u0003\u0014\u01ed\b\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u01f5\b\u0014"+
		"\u0001\u0014\u0003\u0014\u01f8\b\u0014\u0001\u0014\u0001\u0014\u0003\u0014"+
		"\u01fc\b\u0014\u0003\u0014\u01fe\b\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0005\u0014\u0204\b\u0014\n\u0014\f\u0014\u0207\t\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u021f\b\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0005\u0014\u0252\b\u0014\n\u0014\f\u0014\u0255\t\u0014\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0003\u0015\u025e\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003"+
		"\u0016\u0263\b\u0016\u0003\u0016\u0265\b\u0016\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0003\u0017\u026b\b\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0003\u0017\u0270\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0005\u0018\u0275\b\u0018\n\u0018\f\u0018\u0278\t\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0005\u001b\u0283\b\u001b\n\u001b\f\u001b\u0286\t\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c"+
		"\u028d\b\u001c\n\u001c\f\u001c\u0290\t\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0003\u001d\u029b\b\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u02a3\b\u001e\n\u001e\f\u001e"+
		"\u02a6\t\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u02ab\b"+
		"\u001e\u0001\u001f\u0003\u001f\u02ae\b\u001f\u0001\u001f\u0003\u001f\u02b1"+
		"\b\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u02b5\b\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u02bb\b\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0003\u001f\u02c0\b\u001f\u0003\u001f\u02c2\b"+
		"\u001f\u0001 \u0003 \u02c5\b \u0001 \u0003 \u02c8\b \u0001 \u0001 \u0001"+
		" \u0001 \u0001 \u0001 \u0003 \u02d0\b \u0001!\u0003!\u02d3\b!\u0001\""+
		"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001"+
		"\"\u0004\"\u02df\b\"\u000b\"\f\"\u02e0\u0001\"\u0001\"\u0001\"\u0003\""+
		"\u02e6\b\"\u0001#\u0004#\u02e9\b#\u000b#\f#\u02ea\u0001$\u0001$\u0001"+
		"$\u0001$\u0003$\u02f1\b$\u0001%\u0001%\u0001%\u0005%\u02f6\b%\n%\f%\u02f9"+
		"\t%\u0001%\u0001%\u0001&\u0001&\u0003&\u02ff\b&\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0003\'\u0309\b\'\u0001(\u0004"+
		"(\u030c\b(\u000b(\f(\u030d\u0001)\u0001)\u0001)\u0001)\u0003)\u0314\b"+
		")\u0001*\u0001*\u0001*\u0005*\u0319\b*\n*\f*\u031c\t*\u0001*\u0001*\u0001"+
		"+\u0001+\u0001+\u0001+\u0003+\u0324\b+\u0001,\u0003,\u0327\b,\u0001,\u0001"+
		",\u0001,\u0005,\u032c\b,\n,\f,\u032f\t,\u0001,\u0001,\u0001-\u0003-\u0334"+
		"\b-\u0001-\u0001-\u0001-\u0000\u0001(.\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDF"+
		"HJLNPRTVXZ\u0000\u0012\u0003\u0000\u0017\u0017\u001d\u001d\"\"\u0002\u0000"+
		"\t\t))\u0001\u0000kl\u0002\u0000\u001b\u001bkk\u0002\u0000\u0011\u0011"+
		"\'\'\u0002\u0000FFII\u0005\u000066::==JJNN\u0002\u0000GHKM\u0002\u0000"+
		"JJNN\u0001\u0000?A\u0001\u000078\u0001\u0000.1\u0002\u0000--24\u0002\u0000"+
		"99<<\u0002\u0000;;>>\u0001\u0000P`\u0002\u0000kkmn\u0002\u0000\u0014\u0014"+
		"%%\u03c1\u0000]\u0001\u0000\u0000\u0000\u0002e\u0001\u0000\u0000\u0000"+
		"\u0004n\u0001\u0000\u0000\u0000\u0006w\u0001\u0000\u0000\u0000\b}\u0001"+
		"\u0000\u0000\u0000\n\u0093\u0001\u0000\u0000\u0000\f\u00aa\u0001\u0000"+
		"\u0000\u0000\u000e\u00ac\u0001\u0000\u0000\u0000\u0010\u00c2\u0001\u0000"+
		"\u0000\u0000\u0012\u00d2\u0001\u0000\u0000\u0000\u0014\u00de\u0001\u0000"+
		"\u0000\u0000\u0016\u016e\u0001\u0000\u0000\u0000\u0018\u0172\u0001\u0000"+
		"\u0000\u0000\u001a\u0184\u0001\u0000\u0000\u0000\u001c\u019d\u0001\u0000"+
		"\u0000\u0000\u001e\u019f\u0001\u0000\u0000\u0000 \u01a6\u0001\u0000\u0000"+
		"\u0000\"\u01ba\u0001\u0000\u0000\u0000$\u01bc\u0001\u0000\u0000\u0000"+
		"&\u01db\u0001\u0000\u0000\u0000(\u021e\u0001\u0000\u0000\u0000*\u025d"+
		"\u0001\u0000\u0000\u0000,\u0264\u0001\u0000\u0000\u0000.\u026f\u0001\u0000"+
		"\u0000\u00000\u0276\u0001\u0000\u0000\u00002\u027b\u0001\u0000\u0000\u0000"+
		"4\u027d\u0001\u0000\u0000\u00006\u0284\u0001\u0000\u0000\u00008\u028e"+
		"\u0001\u0000\u0000\u0000:\u0293\u0001\u0000\u0000\u0000<\u02aa\u0001\u0000"+
		"\u0000\u0000>\u02c1\u0001\u0000\u0000\u0000@\u02cf\u0001\u0000\u0000\u0000"+
		"B\u02d2\u0001\u0000\u0000\u0000D\u02e5\u0001\u0000\u0000\u0000F\u02e8"+
		"\u0001\u0000\u0000\u0000H\u02ec\u0001\u0000\u0000\u0000J\u02f7\u0001\u0000"+
		"\u0000\u0000L\u02fe\u0001\u0000\u0000\u0000N\u0308\u0001\u0000\u0000\u0000"+
		"P\u030b\u0001\u0000\u0000\u0000R\u030f\u0001\u0000\u0000\u0000T\u031a"+
		"\u0001\u0000\u0000\u0000V\u031f\u0001\u0000\u0000\u0000X\u0326\u0001\u0000"+
		"\u0000\u0000Z\u0333\u0001\u0000\u0000\u0000\\^\u0003\u0002\u0001\u0000"+
		"]\\\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000"+
		"\u0000_`\u0005\u0000\u0000\u0001`\u0001\u0001\u0000\u0000\u0000ac\u0003"+
		"\u0016\u000b\u0000ba\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000"+
		"cd\u0001\u0000\u0000\u0000df\u0005j\u0000\u0000eb\u0001\u0000\u0000\u0000"+
		"fg\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000"+
		"\u0000h\u0003\u0001\u0000\u0000\u0000ij\u0003(\u0014\u0000jk\u0005c\u0000"+
		"\u0000km\u0001\u0000\u0000\u0000li\u0001\u0000\u0000\u0000mp\u0001\u0000"+
		"\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001"+
		"\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0003(\u0014\u0000r\u0005"+
		"\u0001\u0000\u0000\u0000st\u0005k\u0000\u0000tv\u0005c\u0000\u0000us\u0001"+
		"\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000"+
		"wx\u0001\u0000\u0000\u0000xz\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000"+
		"\u0000z{\u0005k\u0000\u0000{\u0007\u0001\u0000\u0000\u0000|~\u0003\n\u0005"+
		"\u0000}|\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u0084\u0001"+
		"\u0000\u0000\u0000\u007f\u0080\u0003\f\u0006\u0000\u0080\u0081\u0003\n"+
		"\u0005\u0000\u0081\u0083\u0001\u0000\u0000\u0000\u0082\u007f\u0001\u0000"+
		"\u0000\u0000\u0083\u0086\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000"+
		"\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0088\u0001\u0000"+
		"\u0000\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0087\u0089\u0003\f\u0006"+
		"\u0000\u0088\u0087\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000"+
		"\u0000\u0089\u008d\u0001\u0000\u0000\u0000\u008a\u008c\u0005\u0093\u0000"+
		"\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008c\u008f\u0001\u0000\u0000"+
		"\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000"+
		"\u0000\u008e\t\u0001\u0000\u0000\u0000\u008f\u008d\u0001\u0000\u0000\u0000"+
		"\u0090\u0092\u0005\u0093\u0000\u0000\u0091\u0090\u0001\u0000\u0000\u0000"+
		"\u0092\u0095\u0001\u0000\u0000\u0000\u0093\u0091\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u009d\u0001\u0000\u0000\u0000"+
		"\u0095\u0093\u0001\u0000\u0000\u0000\u0096\u009a\u0005\u0092\u0000\u0000"+
		"\u0097\u0099\u0005\u0093\u0000\u0000\u0098\u0097\u0001\u0000\u0000\u0000"+
		"\u0099\u009c\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000"+
		"\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009e\u0001\u0000\u0000\u0000"+
		"\u009c\u009a\u0001\u0000\u0000\u0000\u009d\u0096\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000"+
		"\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u000b\u0001\u0000\u0000\u0000"+
		"\u00a1\u00ab\u0005\u0090\u0000\u0000\u00a2\u00ab\u0003\u000e\u0007\u0000"+
		"\u00a3\u00a5\u0003\u000e\u0007\u0000\u00a4\u00a6\u0005\u0093\u0000\u0000"+
		"\u00a5\u00a4\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005\u0085\u0000\u0000"+
		"\u00a8\u00ab\u0001\u0000\u0000\u0000\u00a9\u00ab\u0005\u0085\u0000\u0000"+
		"\u00aa\u00a1\u0001\u0000\u0000\u0000\u00aa\u00a2\u0001\u0000\u0000\u0000"+
		"\u00aa\u00a3\u0001\u0000\u0000\u0000\u00aa\u00a9\u0001\u0000\u0000\u0000"+
		"\u00ab\r\u0001\u0000\u0000\u0000\u00ac\u00b5\u0005\u0091\u0000\u0000\u00ad"+
		"\u00af\u0005\u0093\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000\u00af"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b4\u0003\u0010\b\u0000\u00b3\u00ae\u0001\u0000\u0000\u0000\u00b4\u00b7"+
		"\u0001\u0000\u0000\u0000\u00b5\u00b3\u0001\u0000\u0000\u0000\u00b5\u00b6"+
		"\u0001\u0000\u0000\u0000\u00b6\u000f\u0001\u0000\u0000\u0000\u00b7\u00b5"+
		"\u0001\u0000\u0000\u0000\u00b8\u00c3\u0005\u0091\u0000\u0000\u00b9\u00c3"+
		"\u0005\u008f\u0000\u0000\u00ba\u00c3\u0005\u0087\u0000\u0000\u00bb\u00c3"+
		"\u0005\u0088\u0000\u0000\u00bc\u00c3\u0005\u0089\u0000\u0000\u00bd\u00c3"+
		"\u0005\u008a\u0000\u0000\u00be\u00c3\u0005\u008b\u0000\u0000\u00bf\u00c3"+
		"\u0005\u008c\u0000\u0000\u00c0\u00c3\u0005\u008d\u0000\u0000\u00c1\u00c3"+
		"\u0005\u008e\u0000\u0000\u00c2\u00b8\u0001\u0000\u0000\u0000\u00c2\u00b9"+
		"\u0001\u0000\u0000\u0000\u00c2\u00ba\u0001\u0000\u0000\u0000\u00c2\u00bb"+
		"\u0001\u0000\u0000\u0000\u00c2\u00bc\u0001\u0000\u0000\u0000\u00c2\u00bd"+
		"\u0001\u0000\u0000\u0000\u00c2\u00be\u0001\u0000\u0000\u0000\u00c2\u00bf"+
		"\u0001\u0000\u0000\u0000\u00c2\u00c0\u0001\u0000\u0000\u0000\u00c2\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c3\u0011\u0001\u0000\u0000\u0000\u00c4\u00c5"+
		"\u0005B\u0000\u0000\u00c5\u00d3\u0005C\u0000\u0000\u00c6\u00cc\u0005B"+
		"\u0000\u0000\u00c7\u00c8\u0003\u0014\n\u0000\u00c8\u00c9\u0005c\u0000"+
		"\u0000\u00c9\u00cb\u0001\u0000\u0000\u0000\u00ca\u00c7\u0001\u0000\u0000"+
		"\u0000\u00cb\u00ce\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000"+
		"\u0000\u00cc\u00cd\u0001\u0000\u0000\u0000\u00cd\u00cf\u0001\u0000\u0000"+
		"\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00cf\u00d0\u0003\u0014\n\u0000"+
		"\u00d0\u00d1\u0005C\u0000\u0000\u00d1\u00d3\u0001\u0000\u0000\u0000\u00d2"+
		"\u00c4\u0001\u0000\u0000\u0000\u00d2\u00c6\u0001\u0000\u0000\u0000\u00d3"+
		"\u0013\u0001\u0000\u0000\u0000\u00d4\u00d6\u0005\u0016\u0000\u0000\u00d5"+
		"\u00d4\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000\u00d6"+
		"\u00d8\u0001\u0000\u0000\u0000\u00d7\u00d9\u0005\u001f\u0000\u0000\u00d8"+
		"\u00d7\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9"+
		"\u00da\u0001\u0000\u0000\u0000\u00da\u00df\u0005k\u0000\u0000\u00db\u00dc"+
		"\u0005\u001f\u0000\u0000\u00dc\u00dd\u0005\u0016\u0000\u0000\u00dd\u00df"+
		"\u0005k\u0000\u0000\u00de\u00d5\u0001\u0000\u0000\u0000\u00de\u00db\u0001"+
		"\u0000\u0000\u0000\u00df\u0015\u0001\u0000\u0000\u0000\u00e0\u016f\u0003"+
		"(\u0014\u0000\u00e1\u016f\u0003.\u0017\u0000\u00e2\u016f\u0003\u001a\r"+
		"\u0000\u00e3\u00e4\u0005\u001a\u0000\u0000\u00e4\u016f\u0005k\u0000\u0000"+
		"\u00e5\u00e9\u0005}\u0000\u0000\u00e6\u00e8\u0003*\u0015\u0000\u00e7\u00e6"+
		"\u0001\u0000\u0000\u0000\u00e8\u00eb\u0001\u0000\u0000\u0000\u00e9\u00e7"+
		"\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea\u016f"+
		"\u0001\u0000\u0000\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00ec\u00ed"+
		"\u0005\u0001\u0000\u0000\u00ed\u016f\u00038\u001c\u0000\u00ee\u00ef\u0005"+
		" \u0000\u0000\u00ef\u00f0\u0005k\u0000\u0000\u00f0\u00f1\u0005P\u0000"+
		"\u0000\u00f1\u016f\u0003(\u0014\u0000\u00f2\u00f3\u0005#\u0000\u0000\u00f3"+
		"\u00f6\u0005o\u0000\u0000\u00f4\u00f5\u0005\"\u0000\u0000\u00f5\u00f7"+
		"\u0003\u0006\u0003\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f6\u00f7"+
		"\u0001\u0000\u0000\u0000\u00f7\u016f\u0001\u0000\u0000\u0000\u00f8\u00f9"+
		"\u0005#\u0000\u0000\u00f9\u00fc\u0005k\u0000\u0000\u00fa\u00fb\u0005\""+
		"\u0000\u0000\u00fb\u00fd\u0003\u0006\u0003\u0000\u00fc\u00fa\u0001\u0000"+
		"\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000\u00fd\u016f\u0001\u0000"+
		"\u0000\u0000\u00fe\u0100\u0007\u0000\u0000\u0000\u00ff\u00fe\u0001\u0000"+
		"\u0000\u0000\u00ff\u0100\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000"+
		"\u0000\u0000\u0101\u0102\u0007\u0001\u0000\u0000\u0102\u0103\u0005k\u0000"+
		"\u0000\u0103\u0105\u0003<\u001e\u0000\u0104\u0106\u0003\u0002\u0001\u0000"+
		"\u0105\u0104\u0001\u0000\u0000\u0000\u0105\u0106\u0001\u0000\u0000\u0000"+
		"\u0106\u0107\u0001\u0000\u0000\u0000\u0107\u0108\u0005\u000f\u0000\u0000"+
		"\u0108\u016f\u0001\u0000\u0000\u0000\u0109\u010a\u0005k\u0000\u0000\u010a"+
		"\u010c\u0005b\u0000\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010b\u010c"+
		"\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d\u010e"+
		"\u0005\u0013\u0000\u0000\u010e\u010f\u0003T*\u0000\u010f\u0111\u0005\n"+
		"\u0000\u0000\u0110\u0112\u0003\u0002\u0001\u0000\u0111\u0110\u0001\u0000"+
		"\u0000\u0000\u0111\u0112\u0001\u0000\u0000\u0000\u0112\u0113\u0001\u0000"+
		"\u0000\u0000\u0113\u0114\u0005\u000f\u0000\u0000\u0114\u016f\u0001\u0000"+
		"\u0000\u0000\u0115\u0116\u0005k\u0000\u0000\u0116\u0118\u0005b\u0000\u0000"+
		"\u0117\u0115\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000"+
		"\u0118\u0119\u0001\u0000\u0000\u0000\u0119\u011b\u0005\u0013\u0000\u0000"+
		"\u011a\u011c\u0003\u0018\f\u0000\u011b\u011a\u0001\u0000\u0000\u0000\u011b"+
		"\u011c\u0001\u0000\u0000\u0000\u011c\u011d\u0001\u0000\u0000\u0000\u011d"+
		"\u011f\u0005j\u0000\u0000\u011e\u0120\u0003(\u0014\u0000\u011f\u011e\u0001"+
		"\u0000\u0000\u0000\u011f\u0120\u0001\u0000\u0000\u0000\u0120\u0121\u0001"+
		"\u0000\u0000\u0000\u0121\u0123\u0005j\u0000\u0000\u0122\u0124\u0003\u0004"+
		"\u0002\u0000\u0123\u0122\u0001\u0000\u0000\u0000\u0123\u0124\u0001\u0000"+
		"\u0000\u0000\u0124\u0125\u0001\u0000\u0000\u0000\u0125\u0127\u0005\n\u0000"+
		"\u0000\u0126\u0128\u0003\u0002\u0001\u0000\u0127\u0126\u0001\u0000\u0000"+
		"\u0000\u0127\u0128\u0001\u0000\u0000\u0000\u0128\u0129\u0001\u0000\u0000"+
		"\u0000\u0129\u016f\u0005\u000f\u0000\u0000\u012a\u012b\u0005k\u0000\u0000"+
		"\u012b\u012d\u0005b\u0000\u0000\u012c\u012a\u0001\u0000\u0000\u0000\u012c"+
		"\u012d\u0001\u0000\u0000\u0000\u012d\u012e\u0001\u0000\u0000\u0000\u012e"+
		"\u0130\u0005\u0013\u0000\u0000\u012f\u0131\u0003\u001e\u000f\u0000\u0130"+
		"\u012f\u0001\u0000\u0000\u0000\u0130\u0131\u0001\u0000\u0000\u0000\u0131"+
		"\u0132\u0001\u0000\u0000\u0000\u0132\u0133\u0003&\u0013\u0000\u0133\u0134"+
		"\u0005\u0016\u0000\u0000\u0134\u0136\u0003N\'\u0000\u0135\u0137\u0005"+
		"\b\u0000\u0000\u0136\u0135\u0001\u0000\u0000\u0000\u0136\u0137\u0001\u0000"+
		"\u0000\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u013a\u0005\n\u0000"+
		"\u0000\u0139\u013b\u0003\u0002\u0001\u0000\u013a\u0139\u0001\u0000\u0000"+
		"\u0000\u013a\u013b\u0001\u0000\u0000\u0000\u013b\u013c\u0001\u0000\u0000"+
		"\u0000\u013c\u013d\u0005\u000f\u0000\u0000\u013d\u016f\u0001\u0000\u0000"+
		"\u0000\u013e\u013f\u0005k\u0000\u0000\u013f\u0141\u0005b\u0000\u0000\u0140"+
		"\u013e\u0001\u0000\u0000\u0000\u0140\u0141\u0001\u0000\u0000\u0000\u0141"+
		"\u0142\u0001\u0000\u0000\u0000\u0142\u0143\u0005,\u0000\u0000\u0143\u0144"+
		"\u0003(\u0014\u0000\u0144\u0146\u0005\n\u0000\u0000\u0145\u0147\u0003"+
		"\u0002\u0001\u0000\u0146\u0145\u0001\u0000\u0000\u0000\u0146\u0147\u0001"+
		"\u0000\u0000\u0000\u0147\u0148\u0001\u0000\u0000\u0000\u0148\u0149\u0005"+
		"\u000f\u0000\u0000\u0149\u016f\u0001\u0000\u0000\u0000\u014a\u014b\u0005"+
		"k\u0000\u0000\u014b\u014d\u0005b\u0000\u0000\u014c\u014a\u0001\u0000\u0000"+
		"\u0000\u014c\u014d\u0001\u0000\u0000\u0000\u014d\u014e\u0001\u0000\u0000"+
		"\u0000\u014e\u0150\u0005\n\u0000\u0000\u014f\u0151\u0003\u0002\u0001\u0000"+
		"\u0150\u014f\u0001\u0000\u0000\u0000\u0150\u0151\u0001\u0000\u0000\u0000"+
		"\u0151\u0153\u0001\u0000\u0000\u0000\u0152\u0154\u0005\u0019\u0000\u0000"+
		"\u0153\u0152\u0001\u0000\u0000\u0000\u0153\u0154\u0001\u0000\u0000\u0000"+
		"\u0154\u0155\u0001\u0000\u0000\u0000\u0155\u0156\u0005,\u0000\u0000\u0156"+
		"\u016f\u0003(\u0014\u0000\u0157\u0159\u0005\u0003\u0000\u0000\u0158\u015a"+
		"\u0005k\u0000\u0000\u0159\u0158\u0001\u0000\u0000\u0000\u0159\u015a\u0001"+
		"\u0000\u0000\u0000\u015a\u016f\u0001\u0000\u0000\u0000\u015b\u015d\u0005"+
		"\u0007\u0000\u0000\u015c\u015e\u0005k\u0000\u0000\u015d\u015c\u0001\u0000"+
		"\u0000\u0000\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u016f\u0001\u0000"+
		"\u0000\u0000\u015f\u0161\u0005$\u0000\u0000\u0160\u0162\u0003(\u0014\u0000"+
		"\u0161\u0160\u0001\u0000\u0000\u0000\u0161\u0162\u0001\u0000\u0000\u0000"+
		"\u0162\u016f\u0001\u0000\u0000\u0000\u0163\u0165\u0005\u0002\u0000\u0000"+
		"\u0164\u0166\u0003\u0002\u0001\u0000\u0165\u0164\u0001\u0000\u0000\u0000"+
		"\u0165\u0166\u0001\u0000\u0000\u0000\u0166\u0167\u0001\u0000\u0000\u0000"+
		"\u0167\u016f\u0005\u000f\u0000\u0000\u0168\u016a\u0005\u001b\u0000\u0000"+
		"\u0169\u016b\u0003\u0012\t\u0000\u016a\u0169\u0001\u0000\u0000\u0000\u016a"+
		"\u016b\u0001\u0000\u0000\u0000\u016b\u016c\u0001\u0000\u0000\u0000\u016c"+
		"\u016d\u0005w\u0000\u0000\u016d\u016f\u0003\b\u0004\u0000\u016e\u00e0"+
		"\u0001\u0000\u0000\u0000\u016e\u00e1\u0001\u0000\u0000\u0000\u016e\u00e2"+
		"\u0001\u0000\u0000\u0000\u016e\u00e3\u0001\u0000\u0000\u0000\u016e\u00e5"+
		"\u0001\u0000\u0000\u0000\u016e\u00ec\u0001\u0000\u0000\u0000\u016e\u00ee"+
		"\u0001\u0000\u0000\u0000\u016e\u00f2\u0001\u0000\u0000\u0000\u016e\u00f8"+
		"\u0001\u0000\u0000\u0000\u016e\u00ff\u0001\u0000\u0000\u0000\u016e\u010b"+
		"\u0001\u0000\u0000\u0000\u016e\u0117\u0001\u0000\u0000\u0000\u016e\u012c"+
		"\u0001\u0000\u0000\u0000\u016e\u0140\u0001\u0000\u0000\u0000\u016e\u014c"+
		"\u0001\u0000\u0000\u0000\u016e\u0157\u0001\u0000\u0000\u0000\u016e\u015b"+
		"\u0001\u0000\u0000\u0000\u016e\u015f\u0001\u0000\u0000\u0000\u016e\u0163"+
		"\u0001\u0000\u0000\u0000\u016e\u0168\u0001\u0000\u0000\u0000\u016f\u0017"+
		"\u0001\u0000\u0000\u0000\u0170\u0173\u0003\u001a\r\u0000\u0171\u0173\u0003"+
		"\u0004\u0002\u0000\u0172\u0170\u0001\u0000\u0000\u0000\u0172\u0171\u0001"+
		"\u0000\u0000\u0000\u0173\u0019\u0001\u0000\u0000\u0000\u0174\u0176\u0003"+
		"\u001c\u000e\u0000\u0175\u0174\u0001\u0000\u0000\u0000\u0176\u0179\u0001"+
		"\u0000\u0000\u0000\u0177\u0175\u0001\u0000\u0000\u0000\u0177\u0178\u0001"+
		"\u0000\u0000\u0000\u0178\u017a\u0001\u0000\u0000\u0000\u0179\u0177\u0001"+
		"\u0000\u0000\u0000\u017a\u017b\u0003\u001e\u000f\u0000\u017b\u017c\u0003"+
		" \u0010\u0000\u017c\u0185\u0001\u0000\u0000\u0000\u017d\u017f\u0003\u001c"+
		"\u000e\u0000\u017e\u017d\u0001\u0000\u0000\u0000\u017f\u0180\u0001\u0000"+
		"\u0000\u0000\u0180\u017e\u0001\u0000\u0000\u0000\u0180\u0181\u0001\u0000"+
		"\u0000\u0000\u0181\u0182\u0001\u0000\u0000\u0000\u0182\u0183\u0003 \u0010"+
		"\u0000\u0183\u0185\u0001\u0000\u0000\u0000\u0184\u0177\u0001\u0000\u0000"+
		"\u0000\u0184\u017e\u0001\u0000\u0000\u0000\u0185\u001b\u0001\u0000\u0000"+
		"\u0000\u0186\u019e\u0005\u0006\u0000\u0000\u0187\u019e\u0005\u0004\u0000"+
		"\u0000\u0188\u018a\u0005\u0010\u0000\u0000\u0189\u018b\u0005k\u0000\u0000"+
		"\u018a\u0189\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000"+
		"\u018b\u019e\u0001\u0000\u0000\u0000\u018c\u018d\u0005\u0010\u0000\u0000"+
		"\u018d\u018e\u0005k\u0000\u0000\u018e\u018f\u0005D\u0000\u0000\u018f\u0190"+
		"\u0003(\u0014\u0000\u0190\u0191\u0005E\u0000\u0000\u0191\u019e\u0001\u0000"+
		"\u0000\u0000\u0192\u0193\u0005\u0010\u0000\u0000\u0193\u0194\u0005k\u0000"+
		"\u0000\u0194\u0195\u0005D\u0000\u0000\u0195\u0196\u0003N\'\u0000\u0196"+
		"\u0197\u0005E\u0000\u0000\u0197\u019e\u0001\u0000\u0000\u0000\u0198\u019e"+
		"\u0005\u0012\u0000\u0000\u0199\u019e\u0005\u0018\u0000\u0000\u019a\u019e"+
		"\u0005\u001c\u0000\u0000\u019b\u019e\u0005\"\u0000\u0000\u019c\u019e\u0005"+
		"*\u0000\u0000\u019d\u0186\u0001\u0000\u0000\u0000\u019d\u0187\u0001\u0000"+
		"\u0000\u0000\u019d\u0188\u0001\u0000\u0000\u0000\u019d\u018c\u0001\u0000"+
		"\u0000\u0000\u019d\u0192\u0001\u0000\u0000\u0000\u019d\u0198\u0001\u0000"+
		"\u0000\u0000\u019d\u0199\u0001\u0000\u0000\u0000\u019d\u019a\u0001\u0000"+
		"\u0000\u0000\u019d\u019b\u0001\u0000\u0000\u0000\u019d\u019c\u0001\u0000"+
		"\u0000\u0000\u019e\u001d\u0001\u0000\u0000\u0000\u019f\u01a0\u0005(\u0000"+
		"\u0000\u01a0\u001f\u0001\u0000\u0000\u0000\u01a1\u01a2\u0003\"\u0011\u0000"+
		"\u01a2\u01a3\u0005c\u0000\u0000\u01a3\u01a5\u0001\u0000\u0000\u0000\u01a4"+
		"\u01a1\u0001\u0000\u0000\u0000\u01a5\u01a8\u0001\u0000\u0000\u0000\u01a6"+
		"\u01a4\u0001\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000\u0000\u0000\u01a7"+
		"\u01a9\u0001\u0000\u0000\u0000\u01a8\u01a6\u0001\u0000\u0000\u0000\u01a9"+
		"\u01aa\u0003\"\u0011\u0000\u01aa!\u0001\u0000\u0000\u0000\u01ab\u01ae"+
		"\u0007\u0002\u0000\u0000\u01ac\u01ad\u0005P\u0000\u0000\u01ad\u01af\u0003"+
		"(\u0014\u0000\u01ae\u01ac\u0001\u0000\u0000\u0000\u01ae\u01af\u0001\u0000"+
		"\u0000\u0000\u01af\u01bb\u0001\u0000\u0000\u0000\u01b0\u01b1\u0007\u0002"+
		"\u0000\u0000\u01b1\u01b3\u0005D\u0000\u0000\u01b2\u01b4\u0003(\u0014\u0000"+
		"\u01b3\u01b2\u0001\u0000\u0000\u0000\u01b3\u01b4\u0001\u0000\u0000\u0000"+
		"\u01b4\u01b5\u0001\u0000\u0000\u0000\u01b5\u01b8\u0005E\u0000\u0000\u01b6"+
		"\u01b7\u0005P\u0000\u0000\u01b7\u01b9\u0003$\u0012\u0000\u01b8\u01b6\u0001"+
		"\u0000\u0000\u0000\u01b8\u01b9\u0001\u0000\u0000\u0000\u01b9\u01bb\u0001"+
		"\u0000\u0000\u0000\u01ba\u01ab\u0001\u0000\u0000\u0000\u01ba\u01b0\u0001"+
		"\u0000\u0000\u0000\u01bb#\u0001\u0000\u0000\u0000\u01bc\u01bd\u0005B\u0000"+
		"\u0000\u01bd\u01be\u0003\u0004\u0002\u0000\u01be\u01bf\u0005C\u0000\u0000"+
		"\u01bf%\u0001\u0000\u0000\u0000\u01c0\u01dc\u0005k\u0000\u0000\u01c1\u01dc"+
		"\u0005l\u0000\u0000\u01c2\u01dc\u0005m\u0000\u0000\u01c3\u01c4\u0007\u0002"+
		"\u0000\u0000\u01c4\u01c5\u0005D\u0000\u0000\u01c5\u01c6\u0003(\u0014\u0000"+
		"\u01c6\u01c7\u0005E\u0000\u0000\u01c7\u01dc\u0001\u0000\u0000\u0000\u01c8"+
		"\u01c9\u0007\u0002\u0000\u0000\u01c9\u01ca\u0005D\u0000\u0000\u01ca\u01cb"+
		"\u0003N\'\u0000\u01cb\u01cc\u0005E\u0000\u0000\u01cc\u01dc\u0001\u0000"+
		"\u0000\u0000\u01cd\u01ce\u0005k\u0000\u0000\u01ce\u01cf\u0005e\u0000\u0000"+
		"\u01cf\u01d0\u0005k\u0000\u0000\u01d0\u01d1\u0005D\u0000\u0000\u01d1\u01d2"+
		"\u0003(\u0014\u0000\u01d2\u01d3\u0005E\u0000\u0000\u01d3\u01dc\u0001\u0000"+
		"\u0000\u0000\u01d4\u01d5\u0005k\u0000\u0000\u01d5\u01d6\u0005e\u0000\u0000"+
		"\u01d6\u01d7\u0005k\u0000\u0000\u01d7\u01d8\u0005D\u0000\u0000\u01d8\u01d9"+
		"\u0003N\'\u0000\u01d9\u01da\u0005E\u0000\u0000\u01da\u01dc\u0001\u0000"+
		"\u0000\u0000\u01db\u01c0\u0001\u0000\u0000\u0000\u01db\u01c1\u0001\u0000"+
		"\u0000\u0000\u01db\u01c2\u0001\u0000\u0000\u0000\u01db\u01c3\u0001\u0000"+
		"\u0000\u0000\u01db\u01c8\u0001\u0000\u0000\u0000\u01db\u01cd\u0001\u0000"+
		"\u0000\u0000\u01db\u01d4\u0001\u0000\u0000\u0000\u01dc\'\u0001\u0000\u0000"+
		"\u0000\u01dd\u01de\u0006\u0014\uffff\uffff\u0000\u01de\u021f\u0003&\u0013"+
		"\u0000\u01df\u021f\u0005n\u0000\u0000\u01e0\u01e1\u0005\u000f\u0000\u0000"+
		"\u01e1\u01e2\u0005B\u0000\u0000\u01e2\u021f\u0005C\u0000\u0000\u01e3\u01e4"+
		"\u0007\u0003\u0000\u0000\u01e4\u021f\u0003D\"\u0000\u01e5\u01e6\u0005"+
		"\u0005\u0000\u0000\u01e6\u01e8\u0003(\u0014\u0000\u01e7\u01e9\u0003F#"+
		"\u0000\u01e8\u01e7\u0001\u0000\u0000\u0000\u01e8\u01e9\u0001\u0000\u0000"+
		"\u0000\u01e9\u01ec\u0001\u0000\u0000\u0000\u01ea\u01eb\u0005\f\u0000\u0000"+
		"\u01eb\u01ed\u0003\u0002\u0001\u0000\u01ec\u01ea\u0001\u0000\u0000\u0000"+
		"\u01ec\u01ed\u0001\u0000\u0000\u0000\u01ed\u01ee\u0001\u0000\u0000\u0000"+
		"\u01ee\u01ef\u0005\u000f\u0000\u0000\u01ef\u021f\u0001\u0000\u0000\u0000"+
		"\u01f0\u01f1\u0005\u0015\u0000\u0000\u01f1\u01f2\u0003(\u0014\u0000\u01f2"+
		"\u01f4\u0005&\u0000\u0000\u01f3\u01f5\u0003\u0002\u0001\u0000\u01f4\u01f3"+
		"\u0001\u0000\u0000\u0000\u01f4\u01f5\u0001\u0000\u0000\u0000\u01f5\u01f7"+
		"\u0001\u0000\u0000\u0000\u01f6\u01f8\u0003P(\u0000\u01f7\u01f6\u0001\u0000"+
		"\u0000\u0000\u01f7\u01f8\u0001\u0000\u0000\u0000\u01f8\u01fd\u0001\u0000"+
		"\u0000\u0000\u01f9\u01fb\u0005\f\u0000\u0000\u01fa\u01fc\u0003\u0002\u0001"+
		"\u0000\u01fb\u01fa\u0001\u0000\u0000\u0000\u01fb\u01fc\u0001\u0000\u0000"+
		"\u0000\u01fc\u01fe\u0001\u0000\u0000\u0000\u01fd\u01f9\u0001\u0000\u0000"+
		"\u0000\u01fd\u01fe\u0001\u0000\u0000\u0000\u01fe\u01ff\u0001\u0000\u0000"+
		"\u0000\u01ff\u0200\u0005\u000f\u0000\u0000\u0200\u021f\u0001\u0000\u0000"+
		"\u0000\u0201\u0205\u0005z\u0000\u0000\u0202\u0204\u0003*\u0015\u0000\u0203"+
		"\u0202\u0001\u0000\u0000\u0000\u0204\u0207\u0001\u0000\u0000\u0000\u0205"+
		"\u0203\u0001\u0000\u0000\u0000\u0205\u0206\u0001\u0000\u0000\u0000\u0206"+
		"\u0208\u0001\u0000\u0000\u0000\u0207\u0205\u0001\u0000\u0000\u0000\u0208"+
		"\u021f\u0005h\u0000\u0000\u0209\u021f\u0005o\u0000\u0000\u020a\u021f\u0005"+
		"p\u0000\u0000\u020b\u021f\u0005q\u0000\u0000\u020c\u021f\u0005r\u0000"+
		"\u0000\u020d\u021f\u0005s\u0000\u0000\u020e\u021f\u0005t\u0000\u0000\u020f"+
		"\u021f\u0005u\u0000\u0000\u0210\u021f\u0005v\u0000\u0000\u0211\u021f\u0005"+
		"\u001e\u0000\u0000\u0212\u021f\u0007\u0004\u0000\u0000\u0213\u0214\u0003"+
		"&\u0013\u0000\u0214\u0215\u0007\u0005\u0000\u0000\u0215\u021f\u0001\u0000"+
		"\u0000\u0000\u0216\u0217\u0007\u0005\u0000\u0000\u0217\u021f\u0003&\u0013"+
		"\u0000\u0218\u0219\u0007\u0006\u0000\u0000\u0219\u021f\u0003(\u0014\u000e"+
		"\u021a\u021b\u0005B\u0000\u0000\u021b\u021c\u0003(\u0014\u0000\u021c\u021d"+
		"\u0005C\u0000\u0000\u021d\u021f\u0001\u0000\u0000\u0000\u021e\u01dd\u0001"+
		"\u0000\u0000\u0000\u021e\u01df\u0001\u0000\u0000\u0000\u021e\u01e0\u0001"+
		"\u0000\u0000\u0000\u021e\u01e3\u0001\u0000\u0000\u0000\u021e\u01e5\u0001"+
		"\u0000\u0000\u0000\u021e\u01f0\u0001\u0000\u0000\u0000\u021e\u0201\u0001"+
		"\u0000\u0000\u0000\u021e\u0209\u0001\u0000\u0000\u0000\u021e\u020a\u0001"+
		"\u0000\u0000\u0000\u021e\u020b\u0001\u0000\u0000\u0000\u021e\u020c\u0001"+
		"\u0000\u0000\u0000\u021e\u020d\u0001\u0000\u0000\u0000\u021e\u020e\u0001"+
		"\u0000\u0000\u0000\u021e\u020f\u0001\u0000\u0000\u0000\u021e\u0210\u0001"+
		"\u0000\u0000\u0000\u021e\u0211\u0001\u0000\u0000\u0000\u021e\u0212\u0001"+
		"\u0000\u0000\u0000\u021e\u0213\u0001\u0000\u0000\u0000\u021e\u0216\u0001"+
		"\u0000\u0000\u0000\u021e\u0218\u0001\u0000\u0000\u0000\u021e\u021a\u0001"+
		"\u0000\u0000\u0000\u021f\u0253\u0001\u0000\u0000\u0000\u0220\u0221\n\r"+
		"\u0000\u0000\u0221\u0222\u0005O\u0000\u0000\u0222\u0252\u0003(\u0014\u000e"+
		"\u0223\u0224\n\f\u0000\u0000\u0224\u0225\u0007\u0007\u0000\u0000\u0225"+
		"\u0252\u0003(\u0014\r\u0226\u0227\n\u000b\u0000\u0000\u0227\u0228\u0007"+
		"\b\u0000\u0000\u0228\u0252\u0003(\u0014\f\u0229\u022a\n\n\u0000\u0000"+
		"\u022a\u022b\u0007\t\u0000\u0000\u022b\u0252\u0003(\u0014\u000b\u022c"+
		"\u022d\n\t\u0000\u0000\u022d\u022e\u00055\u0000\u0000\u022e\u0252\u0003"+
		"(\u0014\n\u022f\u0230\n\b\u0000\u0000\u0230\u0231\u0007\n\u0000\u0000"+
		"\u0231\u0252\u0003(\u0014\t\u0232\u0233\n\u0007\u0000\u0000\u0233\u0234"+
		"\u0007\u000b\u0000\u0000\u0234\u0252\u0003(\u0014\b\u0235\u0236\n\u0006"+
		"\u0000\u0000\u0236\u0237\u0007\f\u0000\u0000\u0237\u0252\u0003(\u0014"+
		"\u0007\u0238\u0239\n\u0005\u0000\u0000\u0239\u023a\u0007\r\u0000\u0000"+
		"\u023a\u0252\u0003(\u0014\u0006\u023b\u023c\n\u0004\u0000\u0000\u023c"+
		"\u023d\u0007\u000e\u0000\u0000\u023d\u0252\u0003(\u0014\u0005\u023e\u023f"+
		"\n\u0003\u0000\u0000\u023f\u0240\u0005i\u0000\u0000\u0240\u0241\u0003"+
		"(\u0014\u0000\u0241\u0242\u0005b\u0000\u0000\u0242\u0243\u0003(\u0014"+
		"\u0003\u0243\u0252\u0001\u0000\u0000\u0000\u0244\u0245\n\u0002\u0000\u0000"+
		"\u0245\u0246\u0007\u000f\u0000\u0000\u0246\u0252\u0003(\u0014\u0002\u0247"+
		"\u0248\n \u0000\u0000\u0248\u0249\u0005e\u0000\u0000\u0249\u024a\u0005"+
		"k\u0000\u0000\u024a\u0252\u0003D\"\u0000\u024b\u024c\n\u001f\u0000\u0000"+
		"\u024c\u024d\u0005e\u0000\u0000\u024d\u0252\u0005k\u0000\u0000\u024e\u024f"+
		"\n\u001e\u0000\u0000\u024f\u0250\u0005e\u0000\u0000\u0250\u0252\u0005"+
		"m\u0000\u0000\u0251\u0220\u0001\u0000\u0000\u0000\u0251\u0223\u0001\u0000"+
		"\u0000\u0000\u0251\u0226\u0001\u0000\u0000\u0000\u0251\u0229\u0001\u0000"+
		"\u0000\u0000\u0251\u022c\u0001\u0000\u0000\u0000\u0251\u022f\u0001\u0000"+
		"\u0000\u0000\u0251\u0232\u0001\u0000\u0000\u0000\u0251\u0235\u0001\u0000"+
		"\u0000\u0000\u0251\u0238\u0001\u0000\u0000\u0000\u0251\u023b\u0001\u0000"+
		"\u0000\u0000\u0251\u023e\u0001\u0000\u0000\u0000\u0251\u0244\u0001\u0000"+
		"\u0000\u0000\u0251\u0247\u0001\u0000\u0000\u0000\u0251\u024b\u0001\u0000"+
		"\u0000\u0000\u0251\u024e\u0001\u0000\u0000\u0000\u0252\u0255\u0001\u0000"+
		"\u0000\u0000\u0253\u0251\u0001\u0000\u0000\u0000\u0253\u0254\u0001\u0000"+
		"\u0000\u0000\u0254)\u0001\u0000\u0000\u0000\u0255\u0253\u0001\u0000\u0000"+
		"\u0000\u0256\u025e\u0005\u009b\u0000\u0000\u0257\u025e\u0005\u009c\u0000"+
		"\u0000\u0258\u0259\u0005\u009e\u0000\u0000\u0259\u025a\u0003(\u0014\u0000"+
		"\u025a\u025b\u0005{\u0000\u0000\u025b\u025e\u0001\u0000\u0000\u0000\u025c"+
		"\u025e\u0003,\u0016\u0000\u025d\u0256\u0001\u0000\u0000\u0000\u025d\u0257"+
		"\u0001\u0000\u0000\u0000\u025d\u0258\u0001\u0000\u0000\u0000\u025d\u025c"+
		"\u0001\u0000\u0000\u0000\u025e+\u0001\u0000\u0000\u0000\u025f\u0265\u0005"+
		"\u009d\u0000\u0000\u0260\u0262\u0005\u009f\u0000\u0000\u0261\u0263\u0005"+
		"\u00a1\u0000\u0000\u0262\u0261\u0001\u0000\u0000\u0000\u0262\u0263\u0001"+
		"\u0000\u0000\u0000\u0263\u0265\u0001\u0000\u0000\u0000\u0264\u025f\u0001"+
		"\u0000\u0000\u0000\u0264\u0260\u0001\u0000\u0000\u0000\u0265-\u0001\u0000"+
		"\u0000\u0000\u0266\u0267\u0005y\u0000\u0000\u0267\u026a\u00032\u0019\u0000"+
		"\u0268\u0269\u0005\u0096\u0000\u0000\u0269\u026b\u00030\u0018\u0000\u026a"+
		"\u0268\u0001\u0000\u0000\u0000\u026a\u026b\u0001\u0000\u0000\u0000\u026b"+
		"\u0270\u0001\u0000\u0000\u0000\u026c\u026d\u0005x\u0000\u0000\u026d\u026e"+
		"\u0005k\u0000\u0000\u026e\u0270\u00036\u001b\u0000\u026f\u0266\u0001\u0000"+
		"\u0000\u0000\u026f\u026c\u0001\u0000\u0000\u0000\u0270/\u0001\u0000\u0000"+
		"\u0000\u0271\u0272\u00032\u0019\u0000\u0272\u0273\u0005\u0097\u0000\u0000"+
		"\u0273\u0275\u0001\u0000\u0000\u0000\u0274\u0271\u0001\u0000\u0000\u0000"+
		"\u0275\u0278\u0001\u0000\u0000\u0000\u0276\u0274\u0001\u0000\u0000\u0000"+
		"\u0276\u0277\u0001\u0000\u0000\u0000\u0277\u0279\u0001\u0000\u0000\u0000"+
		"\u0278\u0276\u0001\u0000\u0000\u0000\u0279\u027a\u00032\u0019\u0000\u027a"+
		"1\u0001\u0000\u0000\u0000\u027b\u027c\u0005\u0095\u0000\u0000\u027c3\u0001"+
		"\u0000\u0000\u0000\u027d\u027e\u0007\u0010\u0000\u0000\u027e5\u0001\u0000"+
		"\u0000\u0000\u027f\u0280\u00034\u001a\u0000\u0280\u0281\u0005c\u0000\u0000"+
		"\u0281\u0283\u0001\u0000\u0000\u0000\u0282\u027f\u0001\u0000\u0000\u0000"+
		"\u0283\u0286\u0001\u0000\u0000\u0000\u0284\u0282\u0001\u0000\u0000\u0000"+
		"\u0284\u0285\u0001\u0000\u0000\u0000\u0285\u0287\u0001\u0000\u0000\u0000"+
		"\u0286\u0284\u0001\u0000\u0000\u0000\u0287\u0288\u00034\u001a\u0000\u0288"+
		"7\u0001\u0000\u0000\u0000\u0289\u028a\u0003:\u001d\u0000\u028a\u028b\u0005"+
		"c\u0000\u0000\u028b\u028d\u0001\u0000\u0000\u0000\u028c\u0289\u0001\u0000"+
		"\u0000\u0000\u028d\u0290\u0001\u0000\u0000\u0000\u028e\u028c\u0001\u0000"+
		"\u0000\u0000\u028e\u028f\u0001\u0000\u0000\u0000\u028f\u0291\u0001\u0000"+
		"\u0000\u0000\u0290\u028e\u0001\u0000\u0000\u0000\u0291\u0292\u0003:\u001d"+
		"\u0000\u02929\u0001\u0000\u0000\u0000\u0293\u0294\u0007\u0011\u0000\u0000"+
		"\u0294\u0295\u0005\u0016\u0000\u0000\u0295\u029a\u0005k\u0000\u0000\u0296"+
		"\u0297\u0005D\u0000\u0000\u0297\u0298\u0003N\'\u0000\u0298\u0299\u0005"+
		"E\u0000\u0000\u0299\u029b\u0001\u0000\u0000\u0000\u029a\u0296\u0001\u0000"+
		"\u0000\u0000\u029a\u029b\u0001\u0000\u0000\u0000\u029b;\u0001\u0000\u0000"+
		"\u0000\u029c\u029d\u0005B\u0000\u0000\u029d\u02ab\u0005C\u0000\u0000\u029e"+
		"\u02a4\u0005B\u0000\u0000\u029f\u02a0\u0003>\u001f\u0000\u02a0\u02a1\u0005"+
		"c\u0000\u0000\u02a1\u02a3\u0001\u0000\u0000\u0000\u02a2\u029f\u0001\u0000"+
		"\u0000\u0000\u02a3\u02a6\u0001\u0000\u0000\u0000\u02a4\u02a2\u0001\u0000"+
		"\u0000\u0000\u02a4\u02a5\u0001\u0000\u0000\u0000\u02a5\u02a7\u0001\u0000"+
		"\u0000\u0000\u02a6\u02a4\u0001\u0000\u0000\u0000\u02a7\u02a8\u0003>\u001f"+
		"\u0000\u02a8\u02a9\u0005C\u0000\u0000\u02a9\u02ab\u0001\u0000\u0000\u0000"+
		"\u02aa\u029c\u0001\u0000\u0000\u0000\u02aa\u029e\u0001\u0000\u0000\u0000"+
		"\u02ab=\u0001\u0000\u0000\u0000\u02ac\u02ae\u0005\u0016\u0000\u0000\u02ad"+
		"\u02ac\u0001\u0000\u0000\u0000\u02ad\u02ae\u0001\u0000\u0000\u0000\u02ae"+
		"\u02b0\u0001\u0000\u0000\u0000\u02af\u02b1\u0005\u001f\u0000\u0000\u02b0"+
		"\u02af\u0001\u0000\u0000\u0000\u02b0\u02b1\u0001\u0000\u0000\u0000\u02b1"+
		"\u02b2\u0001\u0000\u0000\u0000\u02b2\u02b4\u0005k\u0000\u0000\u02b3\u02b5"+
		"\u0005g\u0000\u0000\u02b4\u02b3\u0001\u0000\u0000\u0000\u02b4\u02b5\u0001"+
		"\u0000\u0000\u0000\u02b5\u02c2\u0001\u0000\u0000\u0000\u02b6\u02b7\u0005"+
		"\u001f\u0000\u0000\u02b7\u02b8\u0005\u0016\u0000\u0000\u02b8\u02ba\u0005"+
		"k\u0000\u0000\u02b9\u02bb\u0005g\u0000\u0000\u02ba\u02b9\u0001\u0000\u0000"+
		"\u0000\u02ba\u02bb\u0001\u0000\u0000\u0000\u02bb\u02c2\u0001\u0000\u0000"+
		"\u0000\u02bc\u02bd\u0005!\u0000\u0000\u02bd\u02bf\u0005k\u0000\u0000\u02be"+
		"\u02c0\u0005g\u0000\u0000\u02bf\u02be\u0001\u0000\u0000\u0000\u02bf\u02c0"+
		"\u0001\u0000\u0000\u0000\u02c0\u02c2\u0001\u0000\u0000\u0000\u02c1\u02ad"+
		"\u0001\u0000\u0000\u0000\u02c1\u02b6\u0001\u0000\u0000\u0000\u02c1\u02bc"+
		"\u0001\u0000\u0000\u0000\u02c2?\u0001\u0000\u0000\u0000\u02c3\u02c5\u0005"+
		"\u0016\u0000\u0000\u02c4\u02c3\u0001\u0000\u0000\u0000\u02c4\u02c5\u0001"+
		"\u0000\u0000\u0000\u02c5\u02c7\u0001\u0000\u0000\u0000\u02c6\u02c8\u0005"+
		"\u001f\u0000\u0000\u02c7\u02c6\u0001\u0000\u0000\u0000\u02c7\u02c8\u0001"+
		"\u0000\u0000\u0000\u02c8\u02c9\u0001\u0000\u0000\u0000\u02c9\u02d0\u0003"+
		"(\u0014\u0000\u02ca\u02cb\u0005\u001f\u0000\u0000\u02cb\u02cc\u0005\u0016"+
		"\u0000\u0000\u02cc\u02d0\u0003(\u0014\u0000\u02cd\u02ce\u0005!\u0000\u0000"+
		"\u02ce\u02d0\u0003(\u0014\u0000\u02cf\u02c4\u0001\u0000\u0000\u0000\u02cf"+
		"\u02ca\u0001\u0000\u0000\u0000\u02cf\u02cd\u0001\u0000\u0000\u0000\u02d0"+
		"A\u0001\u0000\u0000\u0000\u02d1\u02d3\u0003@ \u0000\u02d2\u02d1\u0001"+
		"\u0000\u0000\u0000\u02d2\u02d3\u0001\u0000\u0000\u0000\u02d3C\u0001\u0000"+
		"\u0000\u0000\u02d4\u02d5\u0005B\u0000\u0000\u02d5\u02e6\u0005C\u0000\u0000"+
		"\u02d6\u02d7\u0005B\u0000\u0000\u02d7\u02d8\u0003@ \u0000\u02d8\u02d9"+
		"\u0005C\u0000\u0000\u02d9\u02e6\u0001\u0000\u0000\u0000\u02da\u02de\u0005"+
		"B\u0000\u0000\u02db\u02dc\u0003B!\u0000\u02dc\u02dd\u0005c\u0000\u0000"+
		"\u02dd\u02df\u0001\u0000\u0000\u0000\u02de\u02db\u0001\u0000\u0000\u0000"+
		"\u02df\u02e0\u0001\u0000\u0000\u0000\u02e0\u02de\u0001\u0000\u0000\u0000"+
		"\u02e0\u02e1\u0001\u0000\u0000\u0000\u02e1\u02e2\u0001\u0000\u0000\u0000"+
		"\u02e2\u02e3\u0003B!\u0000\u02e3\u02e4\u0005C\u0000\u0000\u02e4\u02e6"+
		"\u0001\u0000\u0000\u0000\u02e5\u02d4\u0001\u0000\u0000\u0000\u02e5\u02d6"+
		"\u0001\u0000\u0000\u0000\u02e5\u02da\u0001\u0000\u0000\u0000\u02e6E\u0001"+
		"\u0000\u0000\u0000\u02e7\u02e9\u0003H$\u0000\u02e8\u02e7\u0001\u0000\u0000"+
		"\u0000\u02e9\u02ea\u0001\u0000\u0000\u0000\u02ea\u02e8\u0001\u0000\u0000"+
		"\u0000\u02ea\u02eb\u0001\u0000\u0000\u0000\u02ebG\u0001\u0000\u0000\u0000"+
		"\u02ec\u02ed\u0005+\u0000\u0000\u02ed\u02ee\u0003J%\u0000\u02ee\u02f0"+
		"\u0005&\u0000\u0000\u02ef\u02f1\u0003\u0002\u0001\u0000\u02f0\u02ef\u0001"+
		"\u0000\u0000\u0000\u02f0\u02f1\u0001\u0000\u0000\u0000\u02f1I\u0001\u0000"+
		"\u0000\u0000\u02f2\u02f3\u0003L&\u0000\u02f3\u02f4\u0005c\u0000\u0000"+
		"\u02f4\u02f6\u0001\u0000\u0000\u0000\u02f5\u02f2\u0001\u0000\u0000\u0000"+
		"\u02f6\u02f9\u0001\u0000\u0000\u0000\u02f7\u02f5\u0001\u0000\u0000\u0000"+
		"\u02f7\u02f8\u0001\u0000\u0000\u0000\u02f8\u02fa\u0001\u0000\u0000\u0000"+
		"\u02f9\u02f7\u0001\u0000\u0000\u0000\u02fa\u02fb\u0003L&\u0000\u02fbK"+
		"\u0001\u0000\u0000\u0000\u02fc\u02ff\u0003(\u0014\u0000\u02fd\u02ff\u0003"+
		"N\'\u0000\u02fe\u02fc\u0001\u0000\u0000\u0000\u02fe\u02fd\u0001\u0000"+
		"\u0000\u0000\u02ffM\u0001\u0000\u0000\u0000\u0300\u0301\u0003(\u0014\u0000"+
		"\u0301\u0302\u0005f\u0000\u0000\u0302\u0303\u0003(\u0014\u0000\u0303\u0309"+
		"\u0001\u0000\u0000\u0000\u0304\u0305\u0003(\u0014\u0000\u0305\u0306\u0005"+
		"g\u0000\u0000\u0306\u0307\u0003(\u0014\u0000\u0307\u0309\u0001\u0000\u0000"+
		"\u0000\u0308\u0300\u0001\u0000\u0000\u0000\u0308\u0304\u0001\u0000\u0000"+
		"\u0000\u0309O\u0001\u0000\u0000\u0000\u030a\u030c\u0003R)\u0000\u030b"+
		"\u030a\u0001\u0000\u0000\u0000\u030c\u030d\u0001\u0000\u0000\u0000\u030d"+
		"\u030b\u0001\u0000\u0000\u0000\u030d\u030e\u0001\u0000\u0000\u0000\u030e"+
		"Q\u0001\u0000\u0000\u0000\u030f\u0310\u0005\u000e\u0000\u0000\u0310\u0311"+
		"\u0003(\u0014\u0000\u0311\u0313\u0005&\u0000\u0000\u0312\u0314\u0003\u0002"+
		"\u0001\u0000\u0313\u0312\u0001\u0000\u0000\u0000\u0313\u0314\u0001\u0000"+
		"\u0000\u0000\u0314S\u0001\u0000\u0000\u0000\u0315\u0316\u0003V+\u0000"+
		"\u0316\u0317\u0005j\u0000\u0000\u0317\u0319\u0001\u0000\u0000\u0000\u0318"+
		"\u0315\u0001\u0000\u0000\u0000\u0319\u031c\u0001\u0000\u0000\u0000\u031a"+
		"\u0318\u0001\u0000\u0000\u0000\u031a\u031b\u0001\u0000\u0000\u0000\u031b"+
		"\u031d\u0001\u0000\u0000\u0000\u031c\u031a\u0001\u0000\u0000\u0000\u031d"+
		"\u031e\u0003V+\u0000\u031eU\u0001\u0000\u0000\u0000\u031f\u0320\u0003"+
		"X,\u0000\u0320\u0321\u0005\u0016\u0000\u0000\u0321\u0323\u0003\u0004\u0002"+
		"\u0000\u0322\u0324\u0005\b\u0000\u0000\u0323\u0322\u0001\u0000\u0000\u0000"+
		"\u0323\u0324\u0001\u0000\u0000\u0000\u0324W\u0001\u0000\u0000\u0000\u0325"+
		"\u0327\u0003\u001e\u000f\u0000\u0326\u0325\u0001\u0000\u0000\u0000\u0326"+
		"\u0327\u0001\u0000\u0000\u0000\u0327\u032d\u0001\u0000\u0000\u0000\u0328"+
		"\u0329\u0003Z-\u0000\u0329\u032a\u0005c\u0000\u0000\u032a\u032c\u0001"+
		"\u0000\u0000\u0000\u032b\u0328\u0001\u0000\u0000\u0000\u032c\u032f\u0001"+
		"\u0000\u0000\u0000\u032d\u032b\u0001\u0000\u0000\u0000\u032d\u032e\u0001"+
		"\u0000\u0000\u0000\u032e\u0330\u0001\u0000\u0000\u0000\u032f\u032d\u0001"+
		"\u0000\u0000\u0000\u0330\u0331\u0003Z-\u0000\u0331Y\u0001\u0000\u0000"+
		"\u0000\u0332\u0334\u0005\u001f\u0000\u0000\u0333\u0332\u0001\u0000\u0000"+
		"\u0000\u0333\u0334\u0001\u0000\u0000\u0000\u0334\u0335\u0001\u0000\u0000"+
		"\u0000\u0335\u0336\u0003&\u0013\u0000\u0336[\u0001\u0000\u0000\u0000j"+
		"]bgnw}\u0084\u0088\u008d\u0093\u009a\u009f\u00a5\u00aa\u00b0\u00b5\u00c2"+
		"\u00cc\u00d2\u00d5\u00d8\u00de\u00e9\u00f6\u00fc\u00ff\u0105\u010b\u0111"+
		"\u0117\u011b\u011f\u0123\u0127\u012c\u0130\u0136\u013a\u0140\u0146\u014c"+
		"\u0150\u0153\u0159\u015d\u0161\u0165\u016a\u016e\u0172\u0177\u0180\u0184"+
		"\u018a\u019d\u01a6\u01ae\u01b3\u01b8\u01ba\u01db\u01e8\u01ec\u01f4\u01f7"+
		"\u01fb\u01fd\u0205\u021e\u0251\u0253\u025d\u0262\u0264\u026a\u026f\u0276"+
		"\u0284\u028e\u029a\u02a4\u02aa\u02ad\u02b0\u02b4\u02ba\u02bf\u02c1\u02c4"+
		"\u02c7\u02cf\u02d2\u02e0\u02e5\u02ea\u02f0\u02f7\u02fe\u0308\u030d\u0313"+
		"\u031a\u0323\u0326\u032d\u0333";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}