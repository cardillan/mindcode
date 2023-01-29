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
		FALSE=9, FOR=10, HEAP=11, IF=12, IN=13, LOOP=14, NULL=15, SENSOR=16, STACK=17, 
		TRUE=18, WHEN=19, WHILE=20, ASSIGN=21, AT=22, COLON=23, COMMA=24, DIV=25, 
		IDIV=26, DOLLAR=27, DOT=28, EXP=29, MINUS=30, MOD=31, MUL=32, NOT=33, 
		BITWISE_NOT=34, PLUS=35, QUESTION_MARK=36, SEMICOLON=37, DIV_ASSIGN=38, 
		EXP_ASSIGN=39, MINUS_ASSIGN=40, MUL_ASSIGN=41, PLUS_ASSIGN=42, LESS_THAN=43, 
		LESS_THAN_EQUAL=44, NOT_EQUAL=45, EQUAL=46, STRICT_EQUAL=47, STRICT_NOT_EQUAL=48, 
		GREATER_THAN_EQUAL=49, GREATER_THAN=50, AND=51, OR=52, SHIFT_LEFT=53, 
		SHIFT_RIGHT=54, BITWISE_AND=55, BITWISE_OR=56, BITWISE_XOR=57, LEFT_SBRACKET=58, 
		RIGHT_SBRACKET=59, LEFT_RBRACKET=60, RIGHT_RBRACKET=61, LEFT_CBRACKET=62, 
		RIGHT_CBRACKET=63, LITERAL=64, FLOAT=65, INT=66, HEXINT=67, ID=68, SL_COMMENT=69, 
		WS=70;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", "END", 
			"FALSE", "FOR", "HEAP", "IF", "IN", "LOOP", "NULL", "SENSOR", "STACK", 
			"TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", 
			"DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", 
			"PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", 
			"MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", 
			"EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", 
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
			"'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'loop'", 
			"'null'", "'sensor'", "'stack'", "'true'", "'when'", "'while'", "'='", 
			"'@'", "':'", "','", "'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", 
			"'*'", null, "'~'", "'+'", "'?'", "';'", "'/='", "'**='", "'-='", "'*='", 
			"'+='", "'<'", "'<='", "'!='", "'=='", "'==='", "'!=='", "'>='", "'>'", 
			null, null, "'<<'", "'>>'", "'&'", "'|'", "'^'", "'['", "']'", "'('", 
			"')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "LOOP", "NULL", "SENSOR", 
			"STACK", "TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", 
			"IDIV", "DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2H\u01ae\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23"+
		"\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27"+
		"\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3\"\5\"\u011b\n\"\3#\3#\3"+
		"$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3+\3"+
		",\3,\3-\3-\3-\3.\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61"+
		"\3\62\3\62\3\62\3\63\3\63\3\64\3\64\3\64\3\64\3\64\5\64\u0152\n\64\3\65"+
		"\3\65\3\65\3\65\5\65\u0158\n\65\3\66\3\66\3\66\3\67\3\67\3\67\38\38\3"+
		"9\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\3A\3A\3A\3B\3B\3B\7B\u0178"+
		"\nB\fB\16B\u017b\13B\3B\3B\3C\3C\3C\3C\3D\3D\7D\u0185\nD\fD\16D\u0188"+
		"\13D\3E\3E\3E\6E\u018d\nE\rE\16E\u018e\3F\3F\7F\u0193\nF\fF\16F\u0196"+
		"\13F\3G\3G\3G\3G\7G\u019c\nG\fG\16G\u019f\13G\3G\5G\u01a2\nG\3G\3G\3G"+
		"\3G\3H\6H\u01a9\nH\rH\16H\u01aa\3H\3H\3\u0179\2I\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O"+
		")Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081"+
		"\2\u0083B\u0085C\u0087D\u0089E\u008bF\u008dG\u008fH\3\2\t\4\2\f\f\17\17"+
		"\3\2\62;\4\2ZZzz\4\2\62;ch\5\2C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17"+
		"\"\"\2\u01b7\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2"+
		"\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2"+
		"\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3"+
		"\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2"+
		"\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2"+
		"S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3"+
		"\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2"+
		"\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2"+
		"y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0083\3\2\2\2\2\u0085"+
		"\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2"+
		"\2\2\u008f\3\2\2\2\3\u0091\3\2\2\2\5\u009a\3\2\2\2\7\u00a0\3\2\2\2\t\u00a5"+
		"\3\2\2\2\13\u00ae\3\2\2\2\r\u00b2\3\2\2\2\17\u00b7\3\2\2\2\21\u00bd\3"+
		"\2\2\2\23\u00c1\3\2\2\2\25\u00c7\3\2\2\2\27\u00cb\3\2\2\2\31\u00d0\3\2"+
		"\2\2\33\u00d3\3\2\2\2\35\u00d6\3\2\2\2\37\u00db\3\2\2\2!\u00e0\3\2\2\2"+
		"#\u00e7\3\2\2\2%\u00ed\3\2\2\2\'\u00f2\3\2\2\2)\u00f7\3\2\2\2+\u00fd\3"+
		"\2\2\2-\u00ff\3\2\2\2/\u0101\3\2\2\2\61\u0103\3\2\2\2\63\u0105\3\2\2\2"+
		"\65\u0107\3\2\2\2\67\u0109\3\2\2\29\u010b\3\2\2\2;\u010d\3\2\2\2=\u0110"+
		"\3\2\2\2?\u0112\3\2\2\2A\u0114\3\2\2\2C\u011a\3\2\2\2E\u011c\3\2\2\2G"+
		"\u011e\3\2\2\2I\u0120\3\2\2\2K\u0122\3\2\2\2M\u0124\3\2\2\2O\u0127\3\2"+
		"\2\2Q\u012b\3\2\2\2S\u012e\3\2\2\2U\u0131\3\2\2\2W\u0134\3\2\2\2Y\u0136"+
		"\3\2\2\2[\u0139\3\2\2\2]\u013c\3\2\2\2_\u013f\3\2\2\2a\u0143\3\2\2\2c"+
		"\u0147\3\2\2\2e\u014a\3\2\2\2g\u0151\3\2\2\2i\u0157\3\2\2\2k\u0159\3\2"+
		"\2\2m\u015c\3\2\2\2o\u015f\3\2\2\2q\u0161\3\2\2\2s\u0163\3\2\2\2u\u0165"+
		"\3\2\2\2w\u0167\3\2\2\2y\u0169\3\2\2\2{\u016b\3\2\2\2}\u016d\3\2\2\2\177"+
		"\u016f\3\2\2\2\u0081\u0171\3\2\2\2\u0083\u0174\3\2\2\2\u0085\u017e\3\2"+
		"\2\2\u0087\u0182\3\2\2\2\u0089\u0189\3\2\2\2\u008b\u0190\3\2\2\2\u008d"+
		"\u0197\3\2\2\2\u008f\u01a8\3\2\2\2\u0091\u0092\7c\2\2\u0092\u0093\7n\2"+
		"\2\u0093\u0094\7n\2\2\u0094\u0095\7q\2\2\u0095\u0096\7e\2\2\u0096\u0097"+
		"\7c\2\2\u0097\u0098\7v\2\2\u0098\u0099\7g\2\2\u0099\4\3\2\2\2\u009a\u009b"+
		"\7d\2\2\u009b\u009c\7t\2\2\u009c\u009d\7g\2\2\u009d\u009e\7c\2\2\u009e"+
		"\u009f\7m\2\2\u009f\6\3\2\2\2\u00a0\u00a1\7e\2\2\u00a1\u00a2\7c\2\2\u00a2"+
		"\u00a3\7u\2\2\u00a3\u00a4\7g\2\2\u00a4\b\3\2\2\2\u00a5\u00a6\7e\2\2\u00a6"+
		"\u00a7\7q\2\2\u00a7\u00a8\7p\2\2\u00a8\u00a9\7v\2\2\u00a9\u00aa\7k\2\2"+
		"\u00aa\u00ab\7p\2\2\u00ab\u00ac\7w\2\2\u00ac\u00ad\7g\2\2\u00ad\n\3\2"+
		"\2\2\u00ae\u00af\7f\2\2\u00af\u00b0\7g\2\2\u00b0\u00b1\7h\2\2\u00b1\f"+
		"\3\2\2\2\u00b2\u00b3\7g\2\2\u00b3\u00b4\7n\2\2\u00b4\u00b5\7u\2\2\u00b5"+
		"\u00b6\7g\2\2\u00b6\16\3\2\2\2\u00b7\u00b8\7g\2\2\u00b8\u00b9\7n\2\2\u00b9"+
		"\u00ba\7u\2\2\u00ba\u00bb\7k\2\2\u00bb\u00bc\7h\2\2\u00bc\20\3\2\2\2\u00bd"+
		"\u00be\7g\2\2\u00be\u00bf\7p\2\2\u00bf\u00c0\7f\2\2\u00c0\22\3\2\2\2\u00c1"+
		"\u00c2\7h\2\2\u00c2\u00c3\7c\2\2\u00c3\u00c4\7n\2\2\u00c4\u00c5\7u\2\2"+
		"\u00c5\u00c6\7g\2\2\u00c6\24\3\2\2\2\u00c7\u00c8\7h\2\2\u00c8\u00c9\7"+
		"q\2\2\u00c9\u00ca\7t\2\2\u00ca\26\3\2\2\2\u00cb\u00cc\7j\2\2\u00cc\u00cd"+
		"\7g\2\2\u00cd\u00ce\7c\2\2\u00ce\u00cf\7r\2\2\u00cf\30\3\2\2\2\u00d0\u00d1"+
		"\7k\2\2\u00d1\u00d2\7h\2\2\u00d2\32\3\2\2\2\u00d3\u00d4\7k\2\2\u00d4\u00d5"+
		"\7p\2\2\u00d5\34\3\2\2\2\u00d6\u00d7\7n\2\2\u00d7\u00d8\7q\2\2\u00d8\u00d9"+
		"\7q\2\2\u00d9\u00da\7r\2\2\u00da\36\3\2\2\2\u00db\u00dc\7p\2\2\u00dc\u00dd"+
		"\7w\2\2\u00dd\u00de\7n\2\2\u00de\u00df\7n\2\2\u00df \3\2\2\2\u00e0\u00e1"+
		"\7u\2\2\u00e1\u00e2\7g\2\2\u00e2\u00e3\7p\2\2\u00e3\u00e4\7u\2\2\u00e4"+
		"\u00e5\7q\2\2\u00e5\u00e6\7t\2\2\u00e6\"\3\2\2\2\u00e7\u00e8\7u\2\2\u00e8"+
		"\u00e9\7v\2\2\u00e9\u00ea\7c\2\2\u00ea\u00eb\7e\2\2\u00eb\u00ec\7m\2\2"+
		"\u00ec$\3\2\2\2\u00ed\u00ee\7v\2\2\u00ee\u00ef\7t\2\2\u00ef\u00f0\7w\2"+
		"\2\u00f0\u00f1\7g\2\2\u00f1&\3\2\2\2\u00f2\u00f3\7y\2\2\u00f3\u00f4\7"+
		"j\2\2\u00f4\u00f5\7g\2\2\u00f5\u00f6\7p\2\2\u00f6(\3\2\2\2\u00f7\u00f8"+
		"\7y\2\2\u00f8\u00f9\7j\2\2\u00f9\u00fa\7k\2\2\u00fa\u00fb\7n\2\2\u00fb"+
		"\u00fc\7g\2\2\u00fc*\3\2\2\2\u00fd\u00fe\7?\2\2\u00fe,\3\2\2\2\u00ff\u0100"+
		"\7B\2\2\u0100.\3\2\2\2\u0101\u0102\7<\2\2\u0102\60\3\2\2\2\u0103\u0104"+
		"\7.\2\2\u0104\62\3\2\2\2\u0105\u0106\7\61\2\2\u0106\64\3\2\2\2\u0107\u0108"+
		"\7^\2\2\u0108\66\3\2\2\2\u0109\u010a\7&\2\2\u010a8\3\2\2\2\u010b\u010c"+
		"\7\60\2\2\u010c:\3\2\2\2\u010d\u010e\7,\2\2\u010e\u010f\7,\2\2\u010f<"+
		"\3\2\2\2\u0110\u0111\7/\2\2\u0111>\3\2\2\2\u0112\u0113\7\'\2\2\u0113@"+
		"\3\2\2\2\u0114\u0115\7,\2\2\u0115B\3\2\2\2\u0116\u011b\7#\2\2\u0117\u0118"+
		"\7p\2\2\u0118\u0119\7q\2\2\u0119\u011b\7v\2\2\u011a\u0116\3\2\2\2\u011a"+
		"\u0117\3\2\2\2\u011bD\3\2\2\2\u011c\u011d\7\u0080\2\2\u011dF\3\2\2\2\u011e"+
		"\u011f\7-\2\2\u011fH\3\2\2\2\u0120\u0121\7A\2\2\u0121J\3\2\2\2\u0122\u0123"+
		"\7=\2\2\u0123L\3\2\2\2\u0124\u0125\7\61\2\2\u0125\u0126\7?\2\2\u0126N"+
		"\3\2\2\2\u0127\u0128\7,\2\2\u0128\u0129\7,\2\2\u0129\u012a\7?\2\2\u012a"+
		"P\3\2\2\2\u012b\u012c\7/\2\2\u012c\u012d\7?\2\2\u012dR\3\2\2\2\u012e\u012f"+
		"\7,\2\2\u012f\u0130\7?\2\2\u0130T\3\2\2\2\u0131\u0132\7-\2\2\u0132\u0133"+
		"\7?\2\2\u0133V\3\2\2\2\u0134\u0135\7>\2\2\u0135X\3\2\2\2\u0136\u0137\7"+
		">\2\2\u0137\u0138\7?\2\2\u0138Z\3\2\2\2\u0139\u013a\7#\2\2\u013a\u013b"+
		"\7?\2\2\u013b\\\3\2\2\2\u013c\u013d\7?\2\2\u013d\u013e\7?\2\2\u013e^\3"+
		"\2\2\2\u013f\u0140\7?\2\2\u0140\u0141\7?\2\2\u0141\u0142\7?\2\2\u0142"+
		"`\3\2\2\2\u0143\u0144\7#\2\2\u0144\u0145\7?\2\2\u0145\u0146\7?\2\2\u0146"+
		"b\3\2\2\2\u0147\u0148\7@\2\2\u0148\u0149\7?\2\2\u0149d\3\2\2\2\u014a\u014b"+
		"\7@\2\2\u014bf\3\2\2\2\u014c\u014d\7(\2\2\u014d\u0152\7(\2\2\u014e\u014f"+
		"\7c\2\2\u014f\u0150\7p\2\2\u0150\u0152\7f\2\2\u0151\u014c\3\2\2\2\u0151"+
		"\u014e\3\2\2\2\u0152h\3\2\2\2\u0153\u0154\7~\2\2\u0154\u0158\7~\2\2\u0155"+
		"\u0156\7q\2\2\u0156\u0158\7t\2\2\u0157\u0153\3\2\2\2\u0157\u0155\3\2\2"+
		"\2\u0158j\3\2\2\2\u0159\u015a\7>\2\2\u015a\u015b\7>\2\2\u015bl\3\2\2\2"+
		"\u015c\u015d\7@\2\2\u015d\u015e\7@\2\2\u015en\3\2\2\2\u015f\u0160\7(\2"+
		"\2\u0160p\3\2\2\2\u0161\u0162\7~\2\2\u0162r\3\2\2\2\u0163\u0164\7`\2\2"+
		"\u0164t\3\2\2\2\u0165\u0166\7]\2\2\u0166v\3\2\2\2\u0167\u0168\7_\2\2\u0168"+
		"x\3\2\2\2\u0169\u016a\7*\2\2\u016az\3\2\2\2\u016b\u016c\7+\2\2\u016c|"+
		"\3\2\2\2\u016d\u016e\7}\2\2\u016e~\3\2\2\2\u016f\u0170\7\177\2\2\u0170"+
		"\u0080\3\2\2\2\u0171\u0172\7^\2\2\u0172\u0173\7$\2\2\u0173\u0082\3\2\2"+
		"\2\u0174\u0179\7$\2\2\u0175\u0178\5\u0081A\2\u0176\u0178\n\2\2\2\u0177"+
		"\u0175\3\2\2\2\u0177\u0176\3\2\2\2\u0178\u017b\3\2\2\2\u0179\u017a\3\2"+
		"\2\2\u0179\u0177\3\2\2\2\u017a\u017c\3\2\2\2\u017b\u0179\3\2\2\2\u017c"+
		"\u017d\7$\2\2\u017d\u0084\3\2\2\2\u017e\u017f\5\u0087D\2\u017f\u0180\5"+
		"9\35\2\u0180\u0181\5\u0087D\2\u0181\u0086\3\2\2\2\u0182\u0186\t\3\2\2"+
		"\u0183\u0185\t\3\2\2\u0184\u0183\3\2\2\2\u0185\u0188\3\2\2\2\u0186\u0184"+
		"\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0088\3\2\2\2\u0188\u0186\3\2\2\2\u0189"+
		"\u018a\7\62\2\2\u018a\u018c\t\4\2\2\u018b\u018d\t\5\2\2\u018c\u018b\3"+
		"\2\2\2\u018d\u018e\3\2\2\2\u018e\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f"+
		"\u008a\3\2\2\2\u0190\u0194\t\6\2\2\u0191\u0193\t\7\2\2\u0192\u0191\3\2"+
		"\2\2\u0193\u0196\3\2\2\2\u0194\u0192\3\2\2\2\u0194\u0195\3\2\2\2\u0195"+
		"\u008c\3\2\2\2\u0196\u0194\3\2\2\2\u0197\u0198\7\61\2\2\u0198\u0199\7"+
		"\61\2\2\u0199\u019d\3\2\2\2\u019a\u019c\n\2\2\2\u019b\u019a\3\2\2\2\u019c"+
		"\u019f\3\2\2\2\u019d\u019b\3\2\2\2\u019d\u019e\3\2\2\2\u019e\u01a1\3\2"+
		"\2\2\u019f\u019d\3\2\2\2\u01a0\u01a2\7\17\2\2\u01a1\u01a0\3\2\2\2\u01a1"+
		"\u01a2\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a4\7\f\2\2\u01a4\u01a5\3\2"+
		"\2\2\u01a5\u01a6\bG\2\2\u01a6\u008e\3\2\2\2\u01a7\u01a9\t\b\2\2\u01a8"+
		"\u01a7\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01a8\3\2\2\2\u01aa\u01ab\3\2"+
		"\2\2\u01ab\u01ac\3\2\2\2\u01ac\u01ad\bH\2\2\u01ad\u0090\3\2\2\2\16\2\u011a"+
		"\u0151\u0157\u0177\u0179\u0186\u018e\u0194\u019d\u01a1\u01aa\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}