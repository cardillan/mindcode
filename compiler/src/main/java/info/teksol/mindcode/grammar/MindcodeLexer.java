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
		LESS_THAN_EQUAL=43, NOT_EQUAL=44, EQUAL=45, STRICT_EQUAL=46, STRICT_NOT_EQUAL=47, 
		GREATER_THAN_EQUAL=48, GREATER_THAN=49, AND=50, OR=51, SHIFT_LEFT=52, 
		SHIFT_RIGHT=53, BITWISE_AND=54, BITWISE_OR=55, BITWISE_XOR=56, LEFT_SBRACKET=57, 
		RIGHT_SBRACKET=58, LEFT_RBRACKET=59, RIGHT_RBRACKET=60, LEFT_CBRACKET=61, 
		RIGHT_CBRACKET=62, LITERAL=63, FLOAT=64, INT=65, HEXINT=66, ID=67, SL_COMMENT=68, 
		WS=69;
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
			"STRICT_EQUAL", "STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", 
			"AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", 
			"BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", "LITERAL", "FLOAT", 
			"INT", "HEXINT", "ID", "SL_COMMENT", "WS"
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
			"'<'", "'<='", "'!='", "'=='", "'==='", "'!=='", "'>='", "'>'", null, 
			null, "'<<'", "'>>'", "'&'", "'|'", "'^'", "'['", "']'", "'('", "')'", 
			"'{'", "'}'"
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
			"EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", 
			"AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", 
			"BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2G\u01a7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3"+
		"\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3"+
		"\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3"+
		"\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3"+
		" \3 \3!\3!\3!\3!\5!\u0114\n!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3&\3\'\3"+
		"\'\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3"+
		".\3/\3/\3/\3/\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\63\3\63\3"+
		"\63\3\63\3\63\5\63\u014b\n\63\3\64\3\64\3\64\3\64\5\64\u0151\n\64\3\65"+
		"\3\65\3\65\3\66\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3"+
		"=\3>\3>\3?\3?\3@\3@\3@\3A\3A\3A\7A\u0171\nA\fA\16A\u0174\13A\3A\3A\3B"+
		"\3B\3B\3B\3C\3C\7C\u017e\nC\fC\16C\u0181\13C\3D\3D\3D\6D\u0186\nD\rD\16"+
		"D\u0187\3E\3E\7E\u018c\nE\fE\16E\u018f\13E\3F\3F\3F\3F\7F\u0195\nF\fF"+
		"\16F\u0198\13F\3F\5F\u019b\nF\3F\3F\3F\3F\3G\6G\u01a2\nG\rG\16G\u01a3"+
		"\3G\3G\3\u0172\2H\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65"+
		"\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64"+
		"g\65i\66k\67m8o9q:s;u<w=y>{?}@\177\2\u0081A\u0083B\u0085C\u0087D\u0089"+
		"E\u008bF\u008dG\3\2\t\4\2\f\f\17\17\3\2\62;\4\2ZZzz\4\2\62;ch\5\2C\\a"+
		"ac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u01b0\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3"+
		"\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2"+
		"\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2"+
		"Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3"+
		"\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2"+
		"\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2"+
		"\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089"+
		"\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\3\u008f\3\2\2\2\5\u0098\3\2\2"+
		"\2\7\u009e\3\2\2\2\t\u00a3\3\2\2\2\13\u00ac\3\2\2\2\r\u00b0\3\2\2\2\17"+
		"\u00b5\3\2\2\2\21\u00bb\3\2\2\2\23\u00bf\3\2\2\2\25\u00c5\3\2\2\2\27\u00c9"+
		"\3\2\2\2\31\u00ce\3\2\2\2\33\u00d1\3\2\2\2\35\u00d4\3\2\2\2\37\u00d9\3"+
		"\2\2\2!\u00e0\3\2\2\2#\u00e6\3\2\2\2%\u00eb\3\2\2\2\'\u00f0\3\2\2\2)\u00f6"+
		"\3\2\2\2+\u00f8\3\2\2\2-\u00fa\3\2\2\2/\u00fc\3\2\2\2\61\u00fe\3\2\2\2"+
		"\63\u0100\3\2\2\2\65\u0102\3\2\2\2\67\u0104\3\2\2\29\u0106\3\2\2\2;\u0109"+
		"\3\2\2\2=\u010b\3\2\2\2?\u010d\3\2\2\2A\u0113\3\2\2\2C\u0115\3\2\2\2E"+
		"\u0117\3\2\2\2G\u0119\3\2\2\2I\u011b\3\2\2\2K\u011d\3\2\2\2M\u0120\3\2"+
		"\2\2O\u0124\3\2\2\2Q\u0127\3\2\2\2S\u012a\3\2\2\2U\u012d\3\2\2\2W\u012f"+
		"\3\2\2\2Y\u0132\3\2\2\2[\u0135\3\2\2\2]\u0138\3\2\2\2_\u013c\3\2\2\2a"+
		"\u0140\3\2\2\2c\u0143\3\2\2\2e\u014a\3\2\2\2g\u0150\3\2\2\2i\u0152\3\2"+
		"\2\2k\u0155\3\2\2\2m\u0158\3\2\2\2o\u015a\3\2\2\2q\u015c\3\2\2\2s\u015e"+
		"\3\2\2\2u\u0160\3\2\2\2w\u0162\3\2\2\2y\u0164\3\2\2\2{\u0166\3\2\2\2}"+
		"\u0168\3\2\2\2\177\u016a\3\2\2\2\u0081\u016d\3\2\2\2\u0083\u0177\3\2\2"+
		"\2\u0085\u017b\3\2\2\2\u0087\u0182\3\2\2\2\u0089\u0189\3\2\2\2\u008b\u0190"+
		"\3\2\2\2\u008d\u01a1\3\2\2\2\u008f\u0090\7c\2\2\u0090\u0091\7n\2\2\u0091"+
		"\u0092\7n\2\2\u0092\u0093\7q\2\2\u0093\u0094\7e\2\2\u0094\u0095\7c\2\2"+
		"\u0095\u0096\7v\2\2\u0096\u0097\7g\2\2\u0097\4\3\2\2\2\u0098\u0099\7d"+
		"\2\2\u0099\u009a\7t\2\2\u009a\u009b\7g\2\2\u009b\u009c\7c\2\2\u009c\u009d"+
		"\7m\2\2\u009d\6\3\2\2\2\u009e\u009f\7e\2\2\u009f\u00a0\7c\2\2\u00a0\u00a1"+
		"\7u\2\2\u00a1\u00a2\7g\2\2\u00a2\b\3\2\2\2\u00a3\u00a4\7e\2\2\u00a4\u00a5"+
		"\7q\2\2\u00a5\u00a6\7p\2\2\u00a6\u00a7\7v\2\2\u00a7\u00a8\7k\2\2\u00a8"+
		"\u00a9\7p\2\2\u00a9\u00aa\7w\2\2\u00aa\u00ab\7g\2\2\u00ab\n\3\2\2\2\u00ac"+
		"\u00ad\7f\2\2\u00ad\u00ae\7g\2\2\u00ae\u00af\7h\2\2\u00af\f\3\2\2\2\u00b0"+
		"\u00b1\7g\2\2\u00b1\u00b2\7n\2\2\u00b2\u00b3\7u\2\2\u00b3\u00b4\7g\2\2"+
		"\u00b4\16\3\2\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7n\2\2\u00b7\u00b8\7"+
		"u\2\2\u00b8\u00b9\7k\2\2\u00b9\u00ba\7h\2\2\u00ba\20\3\2\2\2\u00bb\u00bc"+
		"\7g\2\2\u00bc\u00bd\7p\2\2\u00bd\u00be\7f\2\2\u00be\22\3\2\2\2\u00bf\u00c0"+
		"\7h\2\2\u00c0\u00c1\7c\2\2\u00c1\u00c2\7n\2\2\u00c2\u00c3\7u\2\2\u00c3"+
		"\u00c4\7g\2\2\u00c4\24\3\2\2\2\u00c5\u00c6\7h\2\2\u00c6\u00c7\7q\2\2\u00c7"+
		"\u00c8\7t\2\2\u00c8\26\3\2\2\2\u00c9\u00ca\7j\2\2\u00ca\u00cb\7g\2\2\u00cb"+
		"\u00cc\7c\2\2\u00cc\u00cd\7r\2\2\u00cd\30\3\2\2\2\u00ce\u00cf\7k\2\2\u00cf"+
		"\u00d0\7h\2\2\u00d0\32\3\2\2\2\u00d1\u00d2\7k\2\2\u00d2\u00d3\7p\2\2\u00d3"+
		"\34\3\2\2\2\u00d4\u00d5\7p\2\2\u00d5\u00d6\7w\2\2\u00d6\u00d7\7n\2\2\u00d7"+
		"\u00d8\7n\2\2\u00d8\36\3\2\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7g\2\2\u00db"+
		"\u00dc\7p\2\2\u00dc\u00dd\7u\2\2\u00dd\u00de\7q\2\2\u00de\u00df\7t\2\2"+
		"\u00df \3\2\2\2\u00e0\u00e1\7u\2\2\u00e1\u00e2\7v\2\2\u00e2\u00e3\7c\2"+
		"\2\u00e3\u00e4\7e\2\2\u00e4\u00e5\7m\2\2\u00e5\"\3\2\2\2\u00e6\u00e7\7"+
		"v\2\2\u00e7\u00e8\7t\2\2\u00e8\u00e9\7w\2\2\u00e9\u00ea\7g\2\2\u00ea$"+
		"\3\2\2\2\u00eb\u00ec\7y\2\2\u00ec\u00ed\7j\2\2\u00ed\u00ee\7g\2\2\u00ee"+
		"\u00ef\7p\2\2\u00ef&\3\2\2\2\u00f0\u00f1\7y\2\2\u00f1\u00f2\7j\2\2\u00f2"+
		"\u00f3\7k\2\2\u00f3\u00f4\7n\2\2\u00f4\u00f5\7g\2\2\u00f5(\3\2\2\2\u00f6"+
		"\u00f7\7?\2\2\u00f7*\3\2\2\2\u00f8\u00f9\7B\2\2\u00f9,\3\2\2\2\u00fa\u00fb"+
		"\7<\2\2\u00fb.\3\2\2\2\u00fc\u00fd\7.\2\2\u00fd\60\3\2\2\2\u00fe\u00ff"+
		"\7\61\2\2\u00ff\62\3\2\2\2\u0100\u0101\7^\2\2\u0101\64\3\2\2\2\u0102\u0103"+
		"\7&\2\2\u0103\66\3\2\2\2\u0104\u0105\7\60\2\2\u01058\3\2\2\2\u0106\u0107"+
		"\7,\2\2\u0107\u0108\7,\2\2\u0108:\3\2\2\2\u0109\u010a\7/\2\2\u010a<\3"+
		"\2\2\2\u010b\u010c\7\'\2\2\u010c>\3\2\2\2\u010d\u010e\7,\2\2\u010e@\3"+
		"\2\2\2\u010f\u0114\7#\2\2\u0110\u0111\7p\2\2\u0111\u0112\7q\2\2\u0112"+
		"\u0114\7v\2\2\u0113\u010f\3\2\2\2\u0113\u0110\3\2\2\2\u0114B\3\2\2\2\u0115"+
		"\u0116\7\u0080\2\2\u0116D\3\2\2\2\u0117\u0118\7-\2\2\u0118F\3\2\2\2\u0119"+
		"\u011a\7A\2\2\u011aH\3\2\2\2\u011b\u011c\7=\2\2\u011cJ\3\2\2\2\u011d\u011e"+
		"\7\61\2\2\u011e\u011f\7?\2\2\u011fL\3\2\2\2\u0120\u0121\7,\2\2\u0121\u0122"+
		"\7,\2\2\u0122\u0123\7?\2\2\u0123N\3\2\2\2\u0124\u0125\7/\2\2\u0125\u0126"+
		"\7?\2\2\u0126P\3\2\2\2\u0127\u0128\7,\2\2\u0128\u0129\7?\2\2\u0129R\3"+
		"\2\2\2\u012a\u012b\7-\2\2\u012b\u012c\7?\2\2\u012cT\3\2\2\2\u012d\u012e"+
		"\7>\2\2\u012eV\3\2\2\2\u012f\u0130\7>\2\2\u0130\u0131\7?\2\2\u0131X\3"+
		"\2\2\2\u0132\u0133\7#\2\2\u0133\u0134\7?\2\2\u0134Z\3\2\2\2\u0135\u0136"+
		"\7?\2\2\u0136\u0137\7?\2\2\u0137\\\3\2\2\2\u0138\u0139\7?\2\2\u0139\u013a"+
		"\7?\2\2\u013a\u013b\7?\2\2\u013b^\3\2\2\2\u013c\u013d\7#\2\2\u013d\u013e"+
		"\7?\2\2\u013e\u013f\7?\2\2\u013f`\3\2\2\2\u0140\u0141\7@\2\2\u0141\u0142"+
		"\7?\2\2\u0142b\3\2\2\2\u0143\u0144\7@\2\2\u0144d\3\2\2\2\u0145\u0146\7"+
		"(\2\2\u0146\u014b\7(\2\2\u0147\u0148\7c\2\2\u0148\u0149\7p\2\2\u0149\u014b"+
		"\7f\2\2\u014a\u0145\3\2\2\2\u014a\u0147\3\2\2\2\u014bf\3\2\2\2\u014c\u014d"+
		"\7~\2\2\u014d\u0151\7~\2\2\u014e\u014f\7q\2\2\u014f\u0151\7t\2\2\u0150"+
		"\u014c\3\2\2\2\u0150\u014e\3\2\2\2\u0151h\3\2\2\2\u0152\u0153\7>\2\2\u0153"+
		"\u0154\7>\2\2\u0154j\3\2\2\2\u0155\u0156\7@\2\2\u0156\u0157\7@\2\2\u0157"+
		"l\3\2\2\2\u0158\u0159\7(\2\2\u0159n\3\2\2\2\u015a\u015b\7~\2\2\u015bp"+
		"\3\2\2\2\u015c\u015d\7`\2\2\u015dr\3\2\2\2\u015e\u015f\7]\2\2\u015ft\3"+
		"\2\2\2\u0160\u0161\7_\2\2\u0161v\3\2\2\2\u0162\u0163\7*\2\2\u0163x\3\2"+
		"\2\2\u0164\u0165\7+\2\2\u0165z\3\2\2\2\u0166\u0167\7}\2\2\u0167|\3\2\2"+
		"\2\u0168\u0169\7\177\2\2\u0169~\3\2\2\2\u016a\u016b\7^\2\2\u016b\u016c"+
		"\7$\2\2\u016c\u0080\3\2\2\2\u016d\u0172\7$\2\2\u016e\u0171\5\177@\2\u016f"+
		"\u0171\n\2\2\2\u0170\u016e\3\2\2\2\u0170\u016f\3\2\2\2\u0171\u0174\3\2"+
		"\2\2\u0172\u0173\3\2\2\2\u0172\u0170\3\2\2\2\u0173\u0175\3\2\2\2\u0174"+
		"\u0172\3\2\2\2\u0175\u0176\7$\2\2\u0176\u0082\3\2\2\2\u0177\u0178\5\u0085"+
		"C\2\u0178\u0179\5\67\34\2\u0179\u017a\5\u0085C\2\u017a\u0084\3\2\2\2\u017b"+
		"\u017f\t\3\2\2\u017c\u017e\t\3\2\2\u017d\u017c\3\2\2\2\u017e\u0181\3\2"+
		"\2\2\u017f\u017d\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0086\3\2\2\2\u0181"+
		"\u017f\3\2\2\2\u0182\u0183\7\62\2\2\u0183\u0185\t\4\2\2\u0184\u0186\t"+
		"\5\2\2\u0185\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0185\3\2\2\2\u0187"+
		"\u0188\3\2\2\2\u0188\u0088\3\2\2\2\u0189\u018d\t\6\2\2\u018a\u018c\t\7"+
		"\2\2\u018b\u018a\3\2\2\2\u018c\u018f\3\2\2\2\u018d\u018b\3\2\2\2\u018d"+
		"\u018e\3\2\2\2\u018e\u008a\3\2\2\2\u018f\u018d\3\2\2\2\u0190\u0191\7\61"+
		"\2\2\u0191\u0192\7\61\2\2\u0192\u0196\3\2\2\2\u0193\u0195\n\2\2\2\u0194"+
		"\u0193\3\2\2\2\u0195\u0198\3\2\2\2\u0196\u0194\3\2\2\2\u0196\u0197\3\2"+
		"\2\2\u0197\u019a\3\2\2\2\u0198\u0196\3\2\2\2\u0199\u019b\7\17\2\2\u019a"+
		"\u0199\3\2\2\2\u019a\u019b\3\2\2\2\u019b\u019c\3\2\2\2\u019c\u019d\7\f"+
		"\2\2\u019d\u019e\3\2\2\2\u019e\u019f\bF\2\2\u019f\u008c\3\2\2\2\u01a0"+
		"\u01a2\t\b\2\2\u01a1\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a1\3\2"+
		"\2\2\u01a3\u01a4\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a6\bG\2\2\u01a6"+
		"\u008e\3\2\2\2\16\2\u0113\u014a\u0150\u0170\u0172\u017f\u0187\u018d\u0196"+
		"\u019a\u01a3\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}