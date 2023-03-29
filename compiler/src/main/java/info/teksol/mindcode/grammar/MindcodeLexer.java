// Generated from Mindcode.g4 by ANTLR 4.9.1
package info.teksol.mindcode.grammar;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MindcodeLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ALLOCATE=1, BREAK=2, CASE=3, CONST=4, CONTINUE=5, DEF=6, DO=7, ELSE=8, 
		ELSIF=9, END=10, FALSE=11, FOR=12, HEAP=13, IF=14, IN=15, INLINE=16, LOOP=17, 
		NULL=18, RETURN=19, SENSOR=20, STACK=21, THEN=22, TRUE=23, WHEN=24, WHILE=25, 
		ASSIGN=26, AT=27, COLON=28, COMMA=29, DIV=30, IDIV=31, DOLLAR=32, DOT=33, 
		EXP=34, MINUS=35, MOD=36, MUL=37, NOT=38, BITWISE_NOT=39, PLUS=40, QUESTION_MARK=41, 
		SEMICOLON=42, EXP_ASSIGN=43, MUL_ASSIGN=44, DIV_ASSIGN=45, IDIV_ASSIGN=46, 
		MOD_ASSIGN=47, PLUS_ASSIGN=48, MINUS_ASSIGN=49, SHIFT_LEFT_ASSIGN=50, 
		SHIFT_RIGHT_ASSIGN=51, BITWISE_AND_ASSIGN=52, BITWISE_OR_ASSIGN=53, BITWISE_XOR_ASSIGN=54, 
		AND_ASSIGN=55, OR_ASSIGN=56, LESS_THAN=57, LESS_THAN_EQUAL=58, NOT_EQUAL=59, 
		EQUAL=60, STRICT_EQUAL=61, STRICT_NOT_EQUAL=62, GREATER_THAN_EQUAL=63, 
		GREATER_THAN=64, AND=65, OR=66, SHIFT_LEFT=67, SHIFT_RIGHT=68, BITWISE_AND=69, 
		BITWISE_OR=70, BITWISE_XOR=71, LEFT_SBRACKET=72, RIGHT_SBRACKET=73, LEFT_RBRACKET=74, 
		RIGHT_RBRACKET=75, LEFT_CBRACKET=76, RIGHT_CBRACKET=77, LITERAL=78, FLOAT=79, 
		INT=80, HEXINT=81, ID=82, SL_COMMENT=83, WS=84;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONST", "CONTINUE", "DEF", "DO", "ELSE", 
			"ELSIF", "END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", "LOOP", 
			"NULL", "RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", "WHILE", 
			"ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", 
			"MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "EXP_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "IDIV_ASSIGN", 
			"MOD_ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "SHIFT_LEFT_ASSIGN", "SHIFT_RIGHT_ASSIGN", 
			"BITWISE_AND_ASSIGN", "BITWISE_OR_ASSIGN", "BITWISE_XOR_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", 
			"SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", "LITERAL", "FLOAT", 
			"INT", "HEXINT", "ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'const'", "'continue'", "'def'", 
			"'do'", "'else'", "'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", 
			"'in'", "'inline'", "'loop'", "'null'", "'return'", "'sensor'", "'stack'", 
			"'then'", "'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", 
			"'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, "'~'", 
			"'+'", "'?'", "';'", "'**='", "'*='", "'/='", "'\\='", "'%='", "'+='", 
			"'-='", "'<<='", "'>>='", "'&='", "'|='", "'^='", "'&&='", "'||='", "'<'", 
			"'<='", "'!='", "'=='", "'==='", "'!=='", "'>='", "'>'", null, null, 
			"'<<'", "'>>'", "'&'", "'|'", "'^'", "'['", "']'", "'('", "')'", "'{'", 
			"'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONST", "CONTINUE", "DEF", "DO", 
			"ELSE", "ELSIF", "END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", 
			"LOOP", "NULL", "RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "EXP_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "IDIV_ASSIGN", 
			"MOD_ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "SHIFT_LEFT_ASSIGN", "SHIFT_RIGHT_ASSIGN", 
			"BITWISE_AND_ASSIGN", "BITWISE_OR_ASSIGN", "BITWISE_XOR_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", 
			"SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "LITERAL", "FLOAT", "INT", "HEXINT", 
			"ID", "SL_COMMENT", "WS"
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


	public MindcodeLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mindcode.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2V\u0205\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r"+
		"\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\""+
		"\3#\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3\'\5\'\u0153\n\'\3(\3(\3)\3)"+
		"\3*\3*\3+\3+\3,\3,\3,\3,\3-\3-\3-\3.\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\61"+
		"\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\65"+
		"\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3\67\38\38\38\38\39\39\39\39\3:\3"+
		":\3;\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3>\3>\3?\3?\3?\3?\3@\3@\3@\3A\3A\3"+
		"B\3B\3B\3B\3B\5B\u01a9\nB\3C\3C\3C\3C\5C\u01af\nC\3D\3D\3D\3E\3E\3E\3"+
		"F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N\3O\3O\3O\3P\3P\3"+
		"P\7P\u01cf\nP\fP\16P\u01d2\13P\3P\3P\3Q\3Q\3Q\3Q\3R\3R\7R\u01dc\nR\fR"+
		"\16R\u01df\13R\3S\3S\3S\6S\u01e4\nS\rS\16S\u01e5\3T\3T\7T\u01ea\nT\fT"+
		"\16T\u01ed\13T\3U\3U\3U\3U\7U\u01f3\nU\fU\16U\u01f6\13U\3U\5U\u01f9\n"+
		"U\3U\3U\3U\3U\3V\6V\u0200\nV\rV\16V\u0201\3V\3V\3\u01d0\2W\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G"+
		"%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{"+
		"?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089F\u008bG\u008dH\u008fI\u0091"+
		"J\u0093K\u0095L\u0097M\u0099N\u009bO\u009d\2\u009fP\u00a1Q\u00a3R\u00a5"+
		"S\u00a7T\u00a9U\u00abV\3\2\t\4\2\f\f\17\17\3\2\62;\4\2ZZzz\4\2\62;ch\5"+
		"\2C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u020e\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2"+
		"\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2"+
		"\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K"+
		"\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2"+
		"\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2"+
		"\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q"+
		"\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2"+
		"\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087"+
		"\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2"+
		"\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099"+
		"\3\2\2\2\2\u009b\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2"+
		"\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\3\u00ad"+
		"\3\2\2\2\5\u00b6\3\2\2\2\7\u00bc\3\2\2\2\t\u00c1\3\2\2\2\13\u00c7\3\2"+
		"\2\2\r\u00d0\3\2\2\2\17\u00d4\3\2\2\2\21\u00d7\3\2\2\2\23\u00dc\3\2\2"+
		"\2\25\u00e2\3\2\2\2\27\u00e6\3\2\2\2\31\u00ec\3\2\2\2\33\u00f0\3\2\2\2"+
		"\35\u00f5\3\2\2\2\37\u00f8\3\2\2\2!\u00fb\3\2\2\2#\u0102\3\2\2\2%\u0107"+
		"\3\2\2\2\'\u010c\3\2\2\2)\u0113\3\2\2\2+\u011a\3\2\2\2-\u0120\3\2\2\2"+
		"/\u0125\3\2\2\2\61\u012a\3\2\2\2\63\u012f\3\2\2\2\65\u0135\3\2\2\2\67"+
		"\u0137\3\2\2\29\u0139\3\2\2\2;\u013b\3\2\2\2=\u013d\3\2\2\2?\u013f\3\2"+
		"\2\2A\u0141\3\2\2\2C\u0143\3\2\2\2E\u0145\3\2\2\2G\u0148\3\2\2\2I\u014a"+
		"\3\2\2\2K\u014c\3\2\2\2M\u0152\3\2\2\2O\u0154\3\2\2\2Q\u0156\3\2\2\2S"+
		"\u0158\3\2\2\2U\u015a\3\2\2\2W\u015c\3\2\2\2Y\u0160\3\2\2\2[\u0163\3\2"+
		"\2\2]\u0166\3\2\2\2_\u0169\3\2\2\2a\u016c\3\2\2\2c\u016f\3\2\2\2e\u0172"+
		"\3\2\2\2g\u0176\3\2\2\2i\u017a\3\2\2\2k\u017d\3\2\2\2m\u0180\3\2\2\2o"+
		"\u0183\3\2\2\2q\u0187\3\2\2\2s\u018b\3\2\2\2u\u018d\3\2\2\2w\u0190\3\2"+
		"\2\2y\u0193\3\2\2\2{\u0196\3\2\2\2}\u019a\3\2\2\2\177\u019e\3\2\2\2\u0081"+
		"\u01a1\3\2\2\2\u0083\u01a8\3\2\2\2\u0085\u01ae\3\2\2\2\u0087\u01b0\3\2"+
		"\2\2\u0089\u01b3\3\2\2\2\u008b\u01b6\3\2\2\2\u008d\u01b8\3\2\2\2\u008f"+
		"\u01ba\3\2\2\2\u0091\u01bc\3\2\2\2\u0093\u01be\3\2\2\2\u0095\u01c0\3\2"+
		"\2\2\u0097\u01c2\3\2\2\2\u0099\u01c4\3\2\2\2\u009b\u01c6\3\2\2\2\u009d"+
		"\u01c8\3\2\2\2\u009f\u01cb\3\2\2\2\u00a1\u01d5\3\2\2\2\u00a3\u01d9\3\2"+
		"\2\2\u00a5\u01e0\3\2\2\2\u00a7\u01e7\3\2\2\2\u00a9\u01ee\3\2\2\2\u00ab"+
		"\u01ff\3\2\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af\7n\2\2\u00af\u00b0\7n\2"+
		"\2\u00b0\u00b1\7q\2\2\u00b1\u00b2\7e\2\2\u00b2\u00b3\7c\2\2\u00b3\u00b4"+
		"\7v\2\2\u00b4\u00b5\7g\2\2\u00b5\4\3\2\2\2\u00b6\u00b7\7d\2\2\u00b7\u00b8"+
		"\7t\2\2\u00b8\u00b9\7g\2\2\u00b9\u00ba\7c\2\2\u00ba\u00bb\7m\2\2\u00bb"+
		"\6\3\2\2\2\u00bc\u00bd\7e\2\2\u00bd\u00be\7c\2\2\u00be\u00bf\7u\2\2\u00bf"+
		"\u00c0\7g\2\2\u00c0\b\3\2\2\2\u00c1\u00c2\7e\2\2\u00c2\u00c3\7q\2\2\u00c3"+
		"\u00c4\7p\2\2\u00c4\u00c5\7u\2\2\u00c5\u00c6\7v\2\2\u00c6\n\3\2\2\2\u00c7"+
		"\u00c8\7e\2\2\u00c8\u00c9\7q\2\2\u00c9\u00ca\7p\2\2\u00ca\u00cb\7v\2\2"+
		"\u00cb\u00cc\7k\2\2\u00cc\u00cd\7p\2\2\u00cd\u00ce\7w\2\2\u00ce\u00cf"+
		"\7g\2\2\u00cf\f\3\2\2\2\u00d0\u00d1\7f\2\2\u00d1\u00d2\7g\2\2\u00d2\u00d3"+
		"\7h\2\2\u00d3\16\3\2\2\2\u00d4\u00d5\7f\2\2\u00d5\u00d6\7q\2\2\u00d6\20"+
		"\3\2\2\2\u00d7\u00d8\7g\2\2\u00d8\u00d9\7n\2\2\u00d9\u00da\7u\2\2\u00da"+
		"\u00db\7g\2\2\u00db\22\3\2\2\2\u00dc\u00dd\7g\2\2\u00dd\u00de\7n\2\2\u00de"+
		"\u00df\7u\2\2\u00df\u00e0\7k\2\2\u00e0\u00e1\7h\2\2\u00e1\24\3\2\2\2\u00e2"+
		"\u00e3\7g\2\2\u00e3\u00e4\7p\2\2\u00e4\u00e5\7f\2\2\u00e5\26\3\2\2\2\u00e6"+
		"\u00e7\7h\2\2\u00e7\u00e8\7c\2\2\u00e8\u00e9\7n\2\2\u00e9\u00ea\7u\2\2"+
		"\u00ea\u00eb\7g\2\2\u00eb\30\3\2\2\2\u00ec\u00ed\7h\2\2\u00ed\u00ee\7"+
		"q\2\2\u00ee\u00ef\7t\2\2\u00ef\32\3\2\2\2\u00f0\u00f1\7j\2\2\u00f1\u00f2"+
		"\7g\2\2\u00f2\u00f3\7c\2\2\u00f3\u00f4\7r\2\2\u00f4\34\3\2\2\2\u00f5\u00f6"+
		"\7k\2\2\u00f6\u00f7\7h\2\2\u00f7\36\3\2\2\2\u00f8\u00f9\7k\2\2\u00f9\u00fa"+
		"\7p\2\2\u00fa \3\2\2\2\u00fb\u00fc\7k\2\2\u00fc\u00fd\7p\2\2\u00fd\u00fe"+
		"\7n\2\2\u00fe\u00ff\7k\2\2\u00ff\u0100\7p\2\2\u0100\u0101\7g\2\2\u0101"+
		"\"\3\2\2\2\u0102\u0103\7n\2\2\u0103\u0104\7q\2\2\u0104\u0105\7q\2\2\u0105"+
		"\u0106\7r\2\2\u0106$\3\2\2\2\u0107\u0108\7p\2\2\u0108\u0109\7w\2\2\u0109"+
		"\u010a\7n\2\2\u010a\u010b\7n\2\2\u010b&\3\2\2\2\u010c\u010d\7t\2\2\u010d"+
		"\u010e\7g\2\2\u010e\u010f\7v\2\2\u010f\u0110\7w\2\2\u0110\u0111\7t\2\2"+
		"\u0111\u0112\7p\2\2\u0112(\3\2\2\2\u0113\u0114\7u\2\2\u0114\u0115\7g\2"+
		"\2\u0115\u0116\7p\2\2\u0116\u0117\7u\2\2\u0117\u0118\7q\2\2\u0118\u0119"+
		"\7t\2\2\u0119*\3\2\2\2\u011a\u011b\7u\2\2\u011b\u011c\7v\2\2\u011c\u011d"+
		"\7c\2\2\u011d\u011e\7e\2\2\u011e\u011f\7m\2\2\u011f,\3\2\2\2\u0120\u0121"+
		"\7v\2\2\u0121\u0122\7j\2\2\u0122\u0123\7g\2\2\u0123\u0124\7p\2\2\u0124"+
		".\3\2\2\2\u0125\u0126\7v\2\2\u0126\u0127\7t\2\2\u0127\u0128\7w\2\2\u0128"+
		"\u0129\7g\2\2\u0129\60\3\2\2\2\u012a\u012b\7y\2\2\u012b\u012c\7j\2\2\u012c"+
		"\u012d\7g\2\2\u012d\u012e\7p\2\2\u012e\62\3\2\2\2\u012f\u0130\7y\2\2\u0130"+
		"\u0131\7j\2\2\u0131\u0132\7k\2\2\u0132\u0133\7n\2\2\u0133\u0134\7g\2\2"+
		"\u0134\64\3\2\2\2\u0135\u0136\7?\2\2\u0136\66\3\2\2\2\u0137\u0138\7B\2"+
		"\2\u01388\3\2\2\2\u0139\u013a\7<\2\2\u013a:\3\2\2\2\u013b\u013c\7.\2\2"+
		"\u013c<\3\2\2\2\u013d\u013e\7\61\2\2\u013e>\3\2\2\2\u013f\u0140\7^\2\2"+
		"\u0140@\3\2\2\2\u0141\u0142\7&\2\2\u0142B\3\2\2\2\u0143\u0144\7\60\2\2"+
		"\u0144D\3\2\2\2\u0145\u0146\7,\2\2\u0146\u0147\7,\2\2\u0147F\3\2\2\2\u0148"+
		"\u0149\7/\2\2\u0149H\3\2\2\2\u014a\u014b\7\'\2\2\u014bJ\3\2\2\2\u014c"+
		"\u014d\7,\2\2\u014dL\3\2\2\2\u014e\u0153\7#\2\2\u014f\u0150\7p\2\2\u0150"+
		"\u0151\7q\2\2\u0151\u0153\7v\2\2\u0152\u014e\3\2\2\2\u0152\u014f\3\2\2"+
		"\2\u0153N\3\2\2\2\u0154\u0155\7\u0080\2\2\u0155P\3\2\2\2\u0156\u0157\7"+
		"-\2\2\u0157R\3\2\2\2\u0158\u0159\7A\2\2\u0159T\3\2\2\2\u015a\u015b\7="+
		"\2\2\u015bV\3\2\2\2\u015c\u015d\7,\2\2\u015d\u015e\7,\2\2\u015e\u015f"+
		"\7?\2\2\u015fX\3\2\2\2\u0160\u0161\7,\2\2\u0161\u0162\7?\2\2\u0162Z\3"+
		"\2\2\2\u0163\u0164\7\61\2\2\u0164\u0165\7?\2\2\u0165\\\3\2\2\2\u0166\u0167"+
		"\7^\2\2\u0167\u0168\7?\2\2\u0168^\3\2\2\2\u0169\u016a\7\'\2\2\u016a\u016b"+
		"\7?\2\2\u016b`\3\2\2\2\u016c\u016d\7-\2\2\u016d\u016e\7?\2\2\u016eb\3"+
		"\2\2\2\u016f\u0170\7/\2\2\u0170\u0171\7?\2\2\u0171d\3\2\2\2\u0172\u0173"+
		"\7>\2\2\u0173\u0174\7>\2\2\u0174\u0175\7?\2\2\u0175f\3\2\2\2\u0176\u0177"+
		"\7@\2\2\u0177\u0178\7@\2\2\u0178\u0179\7?\2\2\u0179h\3\2\2\2\u017a\u017b"+
		"\7(\2\2\u017b\u017c\7?\2\2\u017cj\3\2\2\2\u017d\u017e\7~\2\2\u017e\u017f"+
		"\7?\2\2\u017fl\3\2\2\2\u0180\u0181\7`\2\2\u0181\u0182\7?\2\2\u0182n\3"+
		"\2\2\2\u0183\u0184\7(\2\2\u0184\u0185\7(\2\2\u0185\u0186\7?\2\2\u0186"+
		"p\3\2\2\2\u0187\u0188\7~\2\2\u0188\u0189\7~\2\2\u0189\u018a\7?\2\2\u018a"+
		"r\3\2\2\2\u018b\u018c\7>\2\2\u018ct\3\2\2\2\u018d\u018e\7>\2\2\u018e\u018f"+
		"\7?\2\2\u018fv\3\2\2\2\u0190\u0191\7#\2\2\u0191\u0192\7?\2\2\u0192x\3"+
		"\2\2\2\u0193\u0194\7?\2\2\u0194\u0195\7?\2\2\u0195z\3\2\2\2\u0196\u0197"+
		"\7?\2\2\u0197\u0198\7?\2\2\u0198\u0199\7?\2\2\u0199|\3\2\2\2\u019a\u019b"+
		"\7#\2\2\u019b\u019c\7?\2\2\u019c\u019d\7?\2\2\u019d~\3\2\2\2\u019e\u019f"+
		"\7@\2\2\u019f\u01a0\7?\2\2\u01a0\u0080\3\2\2\2\u01a1\u01a2\7@\2\2\u01a2"+
		"\u0082\3\2\2\2\u01a3\u01a4\7(\2\2\u01a4\u01a9\7(\2\2\u01a5\u01a6\7c\2"+
		"\2\u01a6\u01a7\7p\2\2\u01a7\u01a9\7f\2\2\u01a8\u01a3\3\2\2\2\u01a8\u01a5"+
		"\3\2\2\2\u01a9\u0084\3\2\2\2\u01aa\u01ab\7~\2\2\u01ab\u01af\7~\2\2\u01ac"+
		"\u01ad\7q\2\2\u01ad\u01af\7t\2\2\u01ae\u01aa\3\2\2\2\u01ae\u01ac\3\2\2"+
		"\2\u01af\u0086\3\2\2\2\u01b0\u01b1\7>\2\2\u01b1\u01b2\7>\2\2\u01b2\u0088"+
		"\3\2\2\2\u01b3\u01b4\7@\2\2\u01b4\u01b5\7@\2\2\u01b5\u008a\3\2\2\2\u01b6"+
		"\u01b7\7(\2\2\u01b7\u008c\3\2\2\2\u01b8\u01b9\7~\2\2\u01b9\u008e\3\2\2"+
		"\2\u01ba\u01bb\7`\2\2\u01bb\u0090\3\2\2\2\u01bc\u01bd\7]\2\2\u01bd\u0092"+
		"\3\2\2\2\u01be\u01bf\7_\2\2\u01bf\u0094\3\2\2\2\u01c0\u01c1\7*\2\2\u01c1"+
		"\u0096\3\2\2\2\u01c2\u01c3\7+\2\2\u01c3\u0098\3\2\2\2\u01c4\u01c5\7}\2"+
		"\2\u01c5\u009a\3\2\2\2\u01c6\u01c7\7\177\2\2\u01c7\u009c\3\2\2\2\u01c8"+
		"\u01c9\7^\2\2\u01c9\u01ca\7$\2\2\u01ca\u009e\3\2\2\2\u01cb\u01d0\7$\2"+
		"\2\u01cc\u01cf\5\u009dO\2\u01cd\u01cf\n\2\2\2\u01ce\u01cc\3\2\2\2\u01ce"+
		"\u01cd\3\2\2\2\u01cf\u01d2\3\2\2\2\u01d0\u01d1\3\2\2\2\u01d0\u01ce\3\2"+
		"\2\2\u01d1\u01d3\3\2\2\2\u01d2\u01d0\3\2\2\2\u01d3\u01d4\7$\2\2\u01d4"+
		"\u00a0\3\2\2\2\u01d5\u01d6\5\u00a3R\2\u01d6\u01d7\5C\"\2\u01d7\u01d8\5"+
		"\u00a3R\2\u01d8\u00a2\3\2\2\2\u01d9\u01dd\t\3\2\2\u01da\u01dc\t\3\2\2"+
		"\u01db\u01da\3\2\2\2\u01dc\u01df\3\2\2\2\u01dd\u01db\3\2\2\2\u01dd\u01de"+
		"\3\2\2\2\u01de\u00a4\3\2\2\2\u01df\u01dd\3\2\2\2\u01e0\u01e1\7\62\2\2"+
		"\u01e1\u01e3\t\4\2\2\u01e2\u01e4\t\5\2\2\u01e3\u01e2\3\2\2\2\u01e4\u01e5"+
		"\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6\u00a6\3\2\2\2\u01e7"+
		"\u01eb\t\6\2\2\u01e8\u01ea\t\7\2\2\u01e9\u01e8\3\2\2\2\u01ea\u01ed\3\2"+
		"\2\2\u01eb\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u00a8\3\2\2\2\u01ed"+
		"\u01eb\3\2\2\2\u01ee\u01ef\7\61\2\2\u01ef\u01f0\7\61\2\2\u01f0\u01f4\3"+
		"\2\2\2\u01f1\u01f3\n\2\2\2\u01f2\u01f1\3\2\2\2\u01f3\u01f6\3\2\2\2\u01f4"+
		"\u01f2\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u01f8\3\2\2\2\u01f6\u01f4\3\2"+
		"\2\2\u01f7\u01f9\7\17\2\2\u01f8\u01f7\3\2\2\2\u01f8\u01f9\3\2\2\2\u01f9"+
		"\u01fa\3\2\2\2\u01fa\u01fb\7\f\2\2\u01fb\u01fc\3\2\2\2\u01fc\u01fd\bU"+
		"\2\2\u01fd\u00aa\3\2\2\2\u01fe\u0200\t\b\2\2\u01ff\u01fe\3\2\2\2\u0200"+
		"\u0201\3\2\2\2\u0201\u01ff\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0203\3\2"+
		"\2\2\u0203\u0204\bV\2\2\u0204\u00ac\3\2\2\2\16\2\u0152\u01a8\u01ae\u01ce"+
		"\u01d0\u01dd\u01e5\u01eb\u01f4\u01f8\u0201\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}