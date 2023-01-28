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
		ALLOCATE=1, BREAK=2, CASE=3, CONTINUE=4, DEF=5, ELSE=6, ELSIF=7, END=8, 
		FALSE=9, FOR=10, HEAP=11, IF=12, IN=13, NULL=14, SENSOR=15, STACK=16, 
		TRUE=17, WHEN=18, WHILE=19, ASSIGN=20, AT=21, COLON=22, COMMA=23, DIV=24, 
		IDIV=25, DOLLAR=26, DOT=27, EXP=28, MINUS=29, MOD=30, MUL=31, NOT=32, 
		BITWISE_NOT=33, PLUS=34, QUESTION_MARK=35, SEMICOLON=36, DIV_ASSIGN=37, 
		EXP_ASSIGN=38, MINUS_ASSIGN=39, MUL_ASSIGN=40, PLUS_ASSIGN=41, LESS_THAN=42, 
		LESS_THAN_EQUAL=43, NOT_EQUAL=44, EQUAL=45, STRICT_EQUAL=46, GREATER_THAN_EQUAL=47, 
		GREATER_THAN=48, AND=49, OR=50, SHIFT_LEFT=51, SHIFT_RIGHT=52, BITWISE_AND=53, 
		BITWISE_OR=54, BITWISE_XOR=55, LEFT_SBRACKET=56, RIGHT_SBRACKET=57, LEFT_RBRACKET=58, 
		RIGHT_RBRACKET=59, LEFT_CBRACKET=60, RIGHT_CBRACKET=61, LITERAL=62, FLOAT=63, 
		INT=64, HEXINT=65, ID=66, SL_COMMENT=67, WS=68;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", "END", 
			"FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "SENSOR", "STACK", "TRUE", 
			"WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", 
			"DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", 
			"PLUS_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", 
			"STRICT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", 
			"SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", 
			"RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_CBRACKET", 
			"RIGHT_CBRACKET", "ESCAPED_QUOTE", "LITERAL", "FLOAT", "INT", "HEXINT", 
			"ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'", 
			"'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", 
			"'sensor'", "'stack'", "'true'", "'when'", "'while'", "'='", "'@'", "':'", 
			"','", "'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, 
			"'~'", "'+'", "'?'", "';'", "'/='", "'**='", "'-='", "'*='", "'+='", 
			"'<'", "'<='", "'!='", "'=='", "'==='", "'>='", "'>'", null, null, "'<<'", 
			"'>>'", "'&'", "'|'", "'^'", "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "SENSOR", "STACK", 
			"TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", 
			"DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", 
			"PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", 
			"MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", 
			"EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", "AND", 
			"OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2F\u01a1\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3"+
		"\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3"+
		"\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 "+
		"\3!\3!\3!\3!\5!\u0112\n!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3&\3\'\3\'\3"+
		"\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3.\3/"+
		"\3/\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62\3\62\3\62\3\62\3\62\5\62\u0145"+
		"\n\62\3\63\3\63\3\63\3\63\5\63\u014b\n\63\3\64\3\64\3\64\3\65\3\65\3\65"+
		"\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3"+
		"?\3@\3@\3@\7@\u016b\n@\f@\16@\u016e\13@\3@\3@\3A\3A\3A\3A\3B\3B\7B\u0178"+
		"\nB\fB\16B\u017b\13B\3C\3C\3C\6C\u0180\nC\rC\16C\u0181\3D\3D\7D\u0186"+
		"\nD\fD\16D\u0189\13D\3E\3E\3E\3E\7E\u018f\nE\fE\16E\u0192\13E\3E\5E\u0195"+
		"\nE\3E\3E\3E\3E\3F\6F\u019c\nF\rF\16F\u019d\3F\3F\3\u016c\2G\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C"+
		"#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w"+
		"=y>{?}\2\177@\u0081A\u0083B\u0085C\u0087D\u0089E\u008bF\3\2\t\4\2\f\f"+
		"\17\17\3\2\62;\4\2ZZzz\4\2\62;ch\5\2C\\aac|\7\2//\62;C\\aac|\5\2\13\f"+
		"\17\17\"\"\2\u01aa\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E"+
		"\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2"+
		"\2\2\2y\3\2\2\2\2{\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2"+
		"\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\3\u008d"+
		"\3\2\2\2\5\u0096\3\2\2\2\7\u009c\3\2\2\2\t\u00a1\3\2\2\2\13\u00aa\3\2"+
		"\2\2\r\u00ae\3\2\2\2\17\u00b3\3\2\2\2\21\u00b9\3\2\2\2\23\u00bd\3\2\2"+
		"\2\25\u00c3\3\2\2\2\27\u00c7\3\2\2\2\31\u00cc\3\2\2\2\33\u00cf\3\2\2\2"+
		"\35\u00d2\3\2\2\2\37\u00d7\3\2\2\2!\u00de\3\2\2\2#\u00e4\3\2\2\2%\u00e9"+
		"\3\2\2\2\'\u00ee\3\2\2\2)\u00f4\3\2\2\2+\u00f6\3\2\2\2-\u00f8\3\2\2\2"+
		"/\u00fa\3\2\2\2\61\u00fc\3\2\2\2\63\u00fe\3\2\2\2\65\u0100\3\2\2\2\67"+
		"\u0102\3\2\2\29\u0104\3\2\2\2;\u0107\3\2\2\2=\u0109\3\2\2\2?\u010b\3\2"+
		"\2\2A\u0111\3\2\2\2C\u0113\3\2\2\2E\u0115\3\2\2\2G\u0117\3\2\2\2I\u0119"+
		"\3\2\2\2K\u011b\3\2\2\2M\u011e\3\2\2\2O\u0122\3\2\2\2Q\u0125\3\2\2\2S"+
		"\u0128\3\2\2\2U\u012b\3\2\2\2W\u012d\3\2\2\2Y\u0130\3\2\2\2[\u0133\3\2"+
		"\2\2]\u0136\3\2\2\2_\u013a\3\2\2\2a\u013d\3\2\2\2c\u0144\3\2\2\2e\u014a"+
		"\3\2\2\2g\u014c\3\2\2\2i\u014f\3\2\2\2k\u0152\3\2\2\2m\u0154\3\2\2\2o"+
		"\u0156\3\2\2\2q\u0158\3\2\2\2s\u015a\3\2\2\2u\u015c\3\2\2\2w\u015e\3\2"+
		"\2\2y\u0160\3\2\2\2{\u0162\3\2\2\2}\u0164\3\2\2\2\177\u0167\3\2\2\2\u0081"+
		"\u0171\3\2\2\2\u0083\u0175\3\2\2\2\u0085\u017c\3\2\2\2\u0087\u0183\3\2"+
		"\2\2\u0089\u018a\3\2\2\2\u008b\u019b\3\2\2\2\u008d\u008e\7c\2\2\u008e"+
		"\u008f\7n\2\2\u008f\u0090\7n\2\2\u0090\u0091\7q\2\2\u0091\u0092\7e\2\2"+
		"\u0092\u0093\7c\2\2\u0093\u0094\7v\2\2\u0094\u0095\7g\2\2\u0095\4\3\2"+
		"\2\2\u0096\u0097\7d\2\2\u0097\u0098\7t\2\2\u0098\u0099\7g\2\2\u0099\u009a"+
		"\7c\2\2\u009a\u009b\7m\2\2\u009b\6\3\2\2\2\u009c\u009d\7e\2\2\u009d\u009e"+
		"\7c\2\2\u009e\u009f\7u\2\2\u009f\u00a0\7g\2\2\u00a0\b\3\2\2\2\u00a1\u00a2"+
		"\7e\2\2\u00a2\u00a3\7q\2\2\u00a3\u00a4\7p\2\2\u00a4\u00a5\7v\2\2\u00a5"+
		"\u00a6\7k\2\2\u00a6\u00a7\7p\2\2\u00a7\u00a8\7w\2\2\u00a8\u00a9\7g\2\2"+
		"\u00a9\n\3\2\2\2\u00aa\u00ab\7f\2\2\u00ab\u00ac\7g\2\2\u00ac\u00ad\7h"+
		"\2\2\u00ad\f\3\2\2\2\u00ae\u00af\7g\2\2\u00af\u00b0\7n\2\2\u00b0\u00b1"+
		"\7u\2\2\u00b1\u00b2\7g\2\2\u00b2\16\3\2\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5"+
		"\7n\2\2\u00b5\u00b6\7u\2\2\u00b6\u00b7\7k\2\2\u00b7\u00b8\7h\2\2\u00b8"+
		"\20\3\2\2\2\u00b9\u00ba\7g\2\2\u00ba\u00bb\7p\2\2\u00bb\u00bc\7f\2\2\u00bc"+
		"\22\3\2\2\2\u00bd\u00be\7h\2\2\u00be\u00bf\7c\2\2\u00bf\u00c0\7n\2\2\u00c0"+
		"\u00c1\7u\2\2\u00c1\u00c2\7g\2\2\u00c2\24\3\2\2\2\u00c3\u00c4\7h\2\2\u00c4"+
		"\u00c5\7q\2\2\u00c5\u00c6\7t\2\2\u00c6\26\3\2\2\2\u00c7\u00c8\7j\2\2\u00c8"+
		"\u00c9\7g\2\2\u00c9\u00ca\7c\2\2\u00ca\u00cb\7r\2\2\u00cb\30\3\2\2\2\u00cc"+
		"\u00cd\7k\2\2\u00cd\u00ce\7h\2\2\u00ce\32\3\2\2\2\u00cf\u00d0\7k\2\2\u00d0"+
		"\u00d1\7p\2\2\u00d1\34\3\2\2\2\u00d2\u00d3\7p\2\2\u00d3\u00d4\7w\2\2\u00d4"+
		"\u00d5\7n\2\2\u00d5\u00d6\7n\2\2\u00d6\36\3\2\2\2\u00d7\u00d8\7u\2\2\u00d8"+
		"\u00d9\7g\2\2\u00d9\u00da\7p\2\2\u00da\u00db\7u\2\2\u00db\u00dc\7q\2\2"+
		"\u00dc\u00dd\7t\2\2\u00dd \3\2\2\2\u00de\u00df\7u\2\2\u00df\u00e0\7v\2"+
		"\2\u00e0\u00e1\7c\2\2\u00e1\u00e2\7e\2\2\u00e2\u00e3\7m\2\2\u00e3\"\3"+
		"\2\2\2\u00e4\u00e5\7v\2\2\u00e5\u00e6\7t\2\2\u00e6\u00e7\7w\2\2\u00e7"+
		"\u00e8\7g\2\2\u00e8$\3\2\2\2\u00e9\u00ea\7y\2\2\u00ea\u00eb\7j\2\2\u00eb"+
		"\u00ec\7g\2\2\u00ec\u00ed\7p\2\2\u00ed&\3\2\2\2\u00ee\u00ef\7y\2\2\u00ef"+
		"\u00f0\7j\2\2\u00f0\u00f1\7k\2\2\u00f1\u00f2\7n\2\2\u00f2\u00f3\7g\2\2"+
		"\u00f3(\3\2\2\2\u00f4\u00f5\7?\2\2\u00f5*\3\2\2\2\u00f6\u00f7\7B\2\2\u00f7"+
		",\3\2\2\2\u00f8\u00f9\7<\2\2\u00f9.\3\2\2\2\u00fa\u00fb\7.\2\2\u00fb\60"+
		"\3\2\2\2\u00fc\u00fd\7\61\2\2\u00fd\62\3\2\2\2\u00fe\u00ff\7^\2\2\u00ff"+
		"\64\3\2\2\2\u0100\u0101\7&\2\2\u0101\66\3\2\2\2\u0102\u0103\7\60\2\2\u0103"+
		"8\3\2\2\2\u0104\u0105\7,\2\2\u0105\u0106\7,\2\2\u0106:\3\2\2\2\u0107\u0108"+
		"\7/\2\2\u0108<\3\2\2\2\u0109\u010a\7\'\2\2\u010a>\3\2\2\2\u010b\u010c"+
		"\7,\2\2\u010c@\3\2\2\2\u010d\u0112\7#\2\2\u010e\u010f\7p\2\2\u010f\u0110"+
		"\7q\2\2\u0110\u0112\7v\2\2\u0111\u010d\3\2\2\2\u0111\u010e\3\2\2\2\u0112"+
		"B\3\2\2\2\u0113\u0114\7\u0080\2\2\u0114D\3\2\2\2\u0115\u0116\7-\2\2\u0116"+
		"F\3\2\2\2\u0117\u0118\7A\2\2\u0118H\3\2\2\2\u0119\u011a\7=\2\2\u011aJ"+
		"\3\2\2\2\u011b\u011c\7\61\2\2\u011c\u011d\7?\2\2\u011dL\3\2\2\2\u011e"+
		"\u011f\7,\2\2\u011f\u0120\7,\2\2\u0120\u0121\7?\2\2\u0121N\3\2\2\2\u0122"+
		"\u0123\7/\2\2\u0123\u0124\7?\2\2\u0124P\3\2\2\2\u0125\u0126\7,\2\2\u0126"+
		"\u0127\7?\2\2\u0127R\3\2\2\2\u0128\u0129\7-\2\2\u0129\u012a\7?\2\2\u012a"+
		"T\3\2\2\2\u012b\u012c\7>\2\2\u012cV\3\2\2\2\u012d\u012e\7>\2\2\u012e\u012f"+
		"\7?\2\2\u012fX\3\2\2\2\u0130\u0131\7#\2\2\u0131\u0132\7?\2\2\u0132Z\3"+
		"\2\2\2\u0133\u0134\7?\2\2\u0134\u0135\7?\2\2\u0135\\\3\2\2\2\u0136\u0137"+
		"\7?\2\2\u0137\u0138\7?\2\2\u0138\u0139\7?\2\2\u0139^\3\2\2\2\u013a\u013b"+
		"\7@\2\2\u013b\u013c\7?\2\2\u013c`\3\2\2\2\u013d\u013e\7@\2\2\u013eb\3"+
		"\2\2\2\u013f\u0140\7(\2\2\u0140\u0145\7(\2\2\u0141\u0142\7c\2\2\u0142"+
		"\u0143\7p\2\2\u0143\u0145\7f\2\2\u0144\u013f\3\2\2\2\u0144\u0141\3\2\2"+
		"\2\u0145d\3\2\2\2\u0146\u0147\7~\2\2\u0147\u014b\7~\2\2\u0148\u0149\7"+
		"q\2\2\u0149\u014b\7t\2\2\u014a\u0146\3\2\2\2\u014a\u0148\3\2\2\2\u014b"+
		"f\3\2\2\2\u014c\u014d\7>\2\2\u014d\u014e\7>\2\2\u014eh\3\2\2\2\u014f\u0150"+
		"\7@\2\2\u0150\u0151\7@\2\2\u0151j\3\2\2\2\u0152\u0153\7(\2\2\u0153l\3"+
		"\2\2\2\u0154\u0155\7~\2\2\u0155n\3\2\2\2\u0156\u0157\7`\2\2\u0157p\3\2"+
		"\2\2\u0158\u0159\7]\2\2\u0159r\3\2\2\2\u015a\u015b\7_\2\2\u015bt\3\2\2"+
		"\2\u015c\u015d\7*\2\2\u015dv\3\2\2\2\u015e\u015f\7+\2\2\u015fx\3\2\2\2"+
		"\u0160\u0161\7}\2\2\u0161z\3\2\2\2\u0162\u0163\7\177\2\2\u0163|\3\2\2"+
		"\2\u0164\u0165\7^\2\2\u0165\u0166\7$\2\2\u0166~\3\2\2\2\u0167\u016c\7"+
		"$\2\2\u0168\u016b\5}?\2\u0169\u016b\n\2\2\2\u016a\u0168\3\2\2\2\u016a"+
		"\u0169\3\2\2\2\u016b\u016e\3\2\2\2\u016c\u016d\3\2\2\2\u016c\u016a\3\2"+
		"\2\2\u016d\u016f\3\2\2\2\u016e\u016c\3\2\2\2\u016f\u0170\7$\2\2\u0170"+
		"\u0080\3\2\2\2\u0171\u0172\5\u0083B\2\u0172\u0173\5\67\34\2\u0173\u0174"+
		"\5\u0083B\2\u0174\u0082\3\2\2\2\u0175\u0179\t\3\2\2\u0176\u0178\t\3\2"+
		"\2\u0177\u0176\3\2\2\2\u0178\u017b\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a"+
		"\3\2\2\2\u017a\u0084\3\2\2\2\u017b\u0179\3\2\2\2\u017c\u017d\7\62\2\2"+
		"\u017d\u017f\t\4\2\2\u017e\u0180\t\5\2\2\u017f\u017e\3\2\2\2\u0180\u0181"+
		"\3\2\2\2\u0181\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0086\3\2\2\2\u0183"+
		"\u0187\t\6\2\2\u0184\u0186\t\7\2\2\u0185\u0184\3\2\2\2\u0186\u0189\3\2"+
		"\2\2\u0187\u0185\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0088\3\2\2\2\u0189"+
		"\u0187\3\2\2\2\u018a\u018b\7\61\2\2\u018b\u018c\7\61\2\2\u018c\u0190\3"+
		"\2\2\2\u018d\u018f\n\2\2\2\u018e\u018d\3\2\2\2\u018f\u0192\3\2\2\2\u0190"+
		"\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0194\3\2\2\2\u0192\u0190\3\2"+
		"\2\2\u0193\u0195\7\17\2\2\u0194\u0193\3\2\2\2\u0194\u0195\3\2\2\2\u0195"+
		"\u0196\3\2\2\2\u0196\u0197\7\f\2\2\u0197\u0198\3\2\2\2\u0198\u0199\bE"+
		"\2\2\u0199\u008a\3\2\2\2\u019a\u019c\t\b\2\2\u019b\u019a\3\2\2\2\u019c"+
		"\u019d\3\2\2\2\u019d\u019b\3\2\2\2\u019d\u019e\3\2\2\2\u019e\u019f\3\2"+
		"\2\2\u019f\u01a0\bF\2\2\u01a0\u008c\3\2\2\2\16\2\u0111\u0144\u014a\u016a"+
		"\u016c\u0179\u0181\u0187\u0190\u0194\u019d\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}