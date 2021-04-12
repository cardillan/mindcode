// Generated from /Users/francois/Projects/mindcode/src/main/java/info/teksol/mindcode/grammar/Mindcode.g4 by ANTLR 4.9.1
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
		FALSE=9, FOR=10, HEAP=11, IF=12, IN=13, NULL=14, STACK=15, TRUE=16, WHEN=17, 
		WHILE=18, ASSIGN=19, AT=20, COLON=21, COMMA=22, DIV=23, IDIV=24, DOLLAR=25, 
		DOT=26, EXP=27, MINUS=28, MOD=29, MUL=30, NOT=31, PLUS=32, QUESTION_MARK=33, 
		SEMICOLON=34, DIV_ASSIGN=35, EXP_ASSIGN=36, MINUS_ASSIGN=37, MUL_ASSIGN=38, 
		PLUS_ASSIGN=39, LESS_THAN=40, LESS_THAN_EQUAL=41, NOT_EQUAL=42, EQUAL=43, 
		STRICT_EQUAL=44, GREATER_THAN_EQUAL=45, GREATER_THAN=46, AND=47, OR=48, 
		SHIFT_LEFT=49, SHIFT_RIGHT=50, BITWISE_AND=51, BITWISE_OR=52, BITWISE_XOR=53, 
		LEFT_SBRACKET=54, RIGHT_SBRACKET=55, LEFT_RBRACKET=56, RIGHT_RBRACKET=57, 
		LEFT_CBRACKET=58, RIGHT_CBRACKET=59, LITERAL=60, FLOAT=61, INT=62, ID=63, 
		SL_COMMENT=64, WS=65;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", "END", 
			"FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", 
			"DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", 
			"LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", 
			"BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"ESCAPED_QUOTE", "LITERAL", "FLOAT", "INT", "ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'", 
			"'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", 
			"'stack'", "'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", 
			"'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, "'+'", 
			"'?'", "';'", "'/='", "'**='", "'-='", "'*='", "'+='", "'<'", "'<='", 
			"'!='", "'=='", "'==='", "'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", 
			"'|'", "'^'", "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", 
			"DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", 
			"LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", 
			"BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"LITERAL", "FLOAT", "INT", "ID", "SL_COMMENT", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2C\u018b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27"+
		"\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35"+
		"\3\36\3\36\3\37\3\37\3 \3 \3 \3 \5 \u0105\n \3!\3!\3\"\3\"\3#\3#\3$\3"+
		"$\3$\3%\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3*\3*\3*\3+\3+\3"+
		"+\3,\3,\3,\3-\3-\3-\3-\3.\3.\3.\3/\3/\3\60\3\60\3\60\3\60\3\60\5\60\u0136"+
		"\n\60\3\61\3\61\3\61\3\61\5\61\u013c\n\61\3\62\3\62\3\62\3\63\3\63\3\63"+
		"\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3"+
		"<\3=\3=\3=\3>\3>\3>\7>\u015c\n>\f>\16>\u015f\13>\3>\3>\3?\3?\3?\3?\3@"+
		"\3@\7@\u0169\n@\f@\16@\u016c\13@\3A\3A\7A\u0170\nA\fA\16A\u0173\13A\3"+
		"B\3B\3B\3B\7B\u0179\nB\fB\16B\u017c\13B\3B\5B\u017f\nB\3B\3B\3B\3B\3C"+
		"\6C\u0186\nC\rC\16C\u0187\3C\3C\3\u015d\2D\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+"+
		"U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y\2{>}?\177@\u0081"+
		"A\u0083B\u0085C\3\2\7\4\2\f\f\17\17\3\2\62;\5\2C\\aac|\7\2//\62;C\\aa"+
		"c|\5\2\13\f\17\17\"\"\2\u0193\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2"+
		"\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2"+
		"+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2"+
		"\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2"+
		"C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3"+
		"\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2"+
		"\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2"+
		"i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3"+
		"\2\2\2\2w\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2"+
		"\2\u0083\3\2\2\2\2\u0085\3\2\2\2\3\u0087\3\2\2\2\5\u0090\3\2\2\2\7\u0096"+
		"\3\2\2\2\t\u009b\3\2\2\2\13\u00a4\3\2\2\2\r\u00a8\3\2\2\2\17\u00ad\3\2"+
		"\2\2\21\u00b3\3\2\2\2\23\u00b7\3\2\2\2\25\u00bd\3\2\2\2\27\u00c1\3\2\2"+
		"\2\31\u00c6\3\2\2\2\33\u00c9\3\2\2\2\35\u00cc\3\2\2\2\37\u00d1\3\2\2\2"+
		"!\u00d7\3\2\2\2#\u00dc\3\2\2\2%\u00e1\3\2\2\2\'\u00e7\3\2\2\2)\u00e9\3"+
		"\2\2\2+\u00eb\3\2\2\2-\u00ed\3\2\2\2/\u00ef\3\2\2\2\61\u00f1\3\2\2\2\63"+
		"\u00f3\3\2\2\2\65\u00f5\3\2\2\2\67\u00f7\3\2\2\29\u00fa\3\2\2\2;\u00fc"+
		"\3\2\2\2=\u00fe\3\2\2\2?\u0104\3\2\2\2A\u0106\3\2\2\2C\u0108\3\2\2\2E"+
		"\u010a\3\2\2\2G\u010c\3\2\2\2I\u010f\3\2\2\2K\u0113\3\2\2\2M\u0116\3\2"+
		"\2\2O\u0119\3\2\2\2Q\u011c\3\2\2\2S\u011e\3\2\2\2U\u0121\3\2\2\2W\u0124"+
		"\3\2\2\2Y\u0127\3\2\2\2[\u012b\3\2\2\2]\u012e\3\2\2\2_\u0135\3\2\2\2a"+
		"\u013b\3\2\2\2c\u013d\3\2\2\2e\u0140\3\2\2\2g\u0143\3\2\2\2i\u0145\3\2"+
		"\2\2k\u0147\3\2\2\2m\u0149\3\2\2\2o\u014b\3\2\2\2q\u014d\3\2\2\2s\u014f"+
		"\3\2\2\2u\u0151\3\2\2\2w\u0153\3\2\2\2y\u0155\3\2\2\2{\u0158\3\2\2\2}"+
		"\u0162\3\2\2\2\177\u0166\3\2\2\2\u0081\u016d\3\2\2\2\u0083\u0174\3\2\2"+
		"\2\u0085\u0185\3\2\2\2\u0087\u0088\7c\2\2\u0088\u0089\7n\2\2\u0089\u008a"+
		"\7n\2\2\u008a\u008b\7q\2\2\u008b\u008c\7e\2\2\u008c\u008d\7c\2\2\u008d"+
		"\u008e\7v\2\2\u008e\u008f\7g\2\2\u008f\4\3\2\2\2\u0090\u0091\7d\2\2\u0091"+
		"\u0092\7t\2\2\u0092\u0093\7g\2\2\u0093\u0094\7c\2\2\u0094\u0095\7m\2\2"+
		"\u0095\6\3\2\2\2\u0096\u0097\7e\2\2\u0097\u0098\7c\2\2\u0098\u0099\7u"+
		"\2\2\u0099\u009a\7g\2\2\u009a\b\3\2\2\2\u009b\u009c\7e\2\2\u009c\u009d"+
		"\7q\2\2\u009d\u009e\7p\2\2\u009e\u009f\7v\2\2\u009f\u00a0\7k\2\2\u00a0"+
		"\u00a1\7p\2\2\u00a1\u00a2\7w\2\2\u00a2\u00a3\7g\2\2\u00a3\n\3\2\2\2\u00a4"+
		"\u00a5\7f\2\2\u00a5\u00a6\7g\2\2\u00a6\u00a7\7h\2\2\u00a7\f\3\2\2\2\u00a8"+
		"\u00a9\7g\2\2\u00a9\u00aa\7n\2\2\u00aa\u00ab\7u\2\2\u00ab\u00ac\7g\2\2"+
		"\u00ac\16\3\2\2\2\u00ad\u00ae\7g\2\2\u00ae\u00af\7n\2\2\u00af\u00b0\7"+
		"u\2\2\u00b0\u00b1\7k\2\2\u00b1\u00b2\7h\2\2\u00b2\20\3\2\2\2\u00b3\u00b4"+
		"\7g\2\2\u00b4\u00b5\7p\2\2\u00b5\u00b6\7f\2\2\u00b6\22\3\2\2\2\u00b7\u00b8"+
		"\7h\2\2\u00b8\u00b9\7c\2\2\u00b9\u00ba\7n\2\2\u00ba\u00bb\7u\2\2\u00bb"+
		"\u00bc\7g\2\2\u00bc\24\3\2\2\2\u00bd\u00be\7h\2\2\u00be\u00bf\7q\2\2\u00bf"+
		"\u00c0\7t\2\2\u00c0\26\3\2\2\2\u00c1\u00c2\7j\2\2\u00c2\u00c3\7g\2\2\u00c3"+
		"\u00c4\7c\2\2\u00c4\u00c5\7r\2\2\u00c5\30\3\2\2\2\u00c6\u00c7\7k\2\2\u00c7"+
		"\u00c8\7h\2\2\u00c8\32\3\2\2\2\u00c9\u00ca\7k\2\2\u00ca\u00cb\7p\2\2\u00cb"+
		"\34\3\2\2\2\u00cc\u00cd\7p\2\2\u00cd\u00ce\7w\2\2\u00ce\u00cf\7n\2\2\u00cf"+
		"\u00d0\7n\2\2\u00d0\36\3\2\2\2\u00d1\u00d2\7u\2\2\u00d2\u00d3\7v\2\2\u00d3"+
		"\u00d4\7c\2\2\u00d4\u00d5\7e\2\2\u00d5\u00d6\7m\2\2\u00d6 \3\2\2\2\u00d7"+
		"\u00d8\7v\2\2\u00d8\u00d9\7t\2\2\u00d9\u00da\7w\2\2\u00da\u00db\7g\2\2"+
		"\u00db\"\3\2\2\2\u00dc\u00dd\7y\2\2\u00dd\u00de\7j\2\2\u00de\u00df\7g"+
		"\2\2\u00df\u00e0\7p\2\2\u00e0$\3\2\2\2\u00e1\u00e2\7y\2\2\u00e2\u00e3"+
		"\7j\2\2\u00e3\u00e4\7k\2\2\u00e4\u00e5\7n\2\2\u00e5\u00e6\7g\2\2\u00e6"+
		"&\3\2\2\2\u00e7\u00e8\7?\2\2\u00e8(\3\2\2\2\u00e9\u00ea\7B\2\2\u00ea*"+
		"\3\2\2\2\u00eb\u00ec\7<\2\2\u00ec,\3\2\2\2\u00ed\u00ee\7.\2\2\u00ee.\3"+
		"\2\2\2\u00ef\u00f0\7\61\2\2\u00f0\60\3\2\2\2\u00f1\u00f2\7^\2\2\u00f2"+
		"\62\3\2\2\2\u00f3\u00f4\7&\2\2\u00f4\64\3\2\2\2\u00f5\u00f6\7\60\2\2\u00f6"+
		"\66\3\2\2\2\u00f7\u00f8\7,\2\2\u00f8\u00f9\7,\2\2\u00f98\3\2\2\2\u00fa"+
		"\u00fb\7/\2\2\u00fb:\3\2\2\2\u00fc\u00fd\7\'\2\2\u00fd<\3\2\2\2\u00fe"+
		"\u00ff\7,\2\2\u00ff>\3\2\2\2\u0100\u0105\7#\2\2\u0101\u0102\7p\2\2\u0102"+
		"\u0103\7q\2\2\u0103\u0105\7v\2\2\u0104\u0100\3\2\2\2\u0104\u0101\3\2\2"+
		"\2\u0105@\3\2\2\2\u0106\u0107\7-\2\2\u0107B\3\2\2\2\u0108\u0109\7A\2\2"+
		"\u0109D\3\2\2\2\u010a\u010b\7=\2\2\u010bF\3\2\2\2\u010c\u010d\7\61\2\2"+
		"\u010d\u010e\7?\2\2\u010eH\3\2\2\2\u010f\u0110\7,\2\2\u0110\u0111\7,\2"+
		"\2\u0111\u0112\7?\2\2\u0112J\3\2\2\2\u0113\u0114\7/\2\2\u0114\u0115\7"+
		"?\2\2\u0115L\3\2\2\2\u0116\u0117\7,\2\2\u0117\u0118\7?\2\2\u0118N\3\2"+
		"\2\2\u0119\u011a\7-\2\2\u011a\u011b\7?\2\2\u011bP\3\2\2\2\u011c\u011d"+
		"\7>\2\2\u011dR\3\2\2\2\u011e\u011f\7>\2\2\u011f\u0120\7?\2\2\u0120T\3"+
		"\2\2\2\u0121\u0122\7#\2\2\u0122\u0123\7?\2\2\u0123V\3\2\2\2\u0124\u0125"+
		"\7?\2\2\u0125\u0126\7?\2\2\u0126X\3\2\2\2\u0127\u0128\7?\2\2\u0128\u0129"+
		"\7?\2\2\u0129\u012a\7?\2\2\u012aZ\3\2\2\2\u012b\u012c\7@\2\2\u012c\u012d"+
		"\7?\2\2\u012d\\\3\2\2\2\u012e\u012f\7@\2\2\u012f^\3\2\2\2\u0130\u0131"+
		"\7(\2\2\u0131\u0136\7(\2\2\u0132\u0133\7c\2\2\u0133\u0134\7p\2\2\u0134"+
		"\u0136\7f\2\2\u0135\u0130\3\2\2\2\u0135\u0132\3\2\2\2\u0136`\3\2\2\2\u0137"+
		"\u0138\7~\2\2\u0138\u013c\7~\2\2\u0139\u013a\7q\2\2\u013a\u013c\7t\2\2"+
		"\u013b\u0137\3\2\2\2\u013b\u0139\3\2\2\2\u013cb\3\2\2\2\u013d\u013e\7"+
		">\2\2\u013e\u013f\7>\2\2\u013fd\3\2\2\2\u0140\u0141\7@\2\2\u0141\u0142"+
		"\7@\2\2\u0142f\3\2\2\2\u0143\u0144\7(\2\2\u0144h\3\2\2\2\u0145\u0146\7"+
		"~\2\2\u0146j\3\2\2\2\u0147\u0148\7`\2\2\u0148l\3\2\2\2\u0149\u014a\7]"+
		"\2\2\u014an\3\2\2\2\u014b\u014c\7_\2\2\u014cp\3\2\2\2\u014d\u014e\7*\2"+
		"\2\u014er\3\2\2\2\u014f\u0150\7+\2\2\u0150t\3\2\2\2\u0151\u0152\7}\2\2"+
		"\u0152v\3\2\2\2\u0153\u0154\7\177\2\2\u0154x\3\2\2\2\u0155\u0156\7^\2"+
		"\2\u0156\u0157\7$\2\2\u0157z\3\2\2\2\u0158\u015d\7$\2\2\u0159\u015c\5"+
		"y=\2\u015a\u015c\n\2\2\2\u015b\u0159\3\2\2\2\u015b\u015a\3\2\2\2\u015c"+
		"\u015f\3\2\2\2\u015d\u015e\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u0160\3\2"+
		"\2\2\u015f\u015d\3\2\2\2\u0160\u0161\7$\2\2\u0161|\3\2\2\2\u0162\u0163"+
		"\5\177@\2\u0163\u0164\5\65\33\2\u0164\u0165\5\177@\2\u0165~\3\2\2\2\u0166"+
		"\u016a\t\3\2\2\u0167\u0169\t\3\2\2\u0168\u0167\3\2\2\2\u0169\u016c\3\2"+
		"\2\2\u016a\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u0080\3\2\2\2\u016c"+
		"\u016a\3\2\2\2\u016d\u0171\t\4\2\2\u016e\u0170\t\5\2\2\u016f\u016e\3\2"+
		"\2\2\u0170\u0173\3\2\2\2\u0171\u016f\3\2\2\2\u0171\u0172\3\2\2\2\u0172"+
		"\u0082\3\2\2\2\u0173\u0171\3\2\2\2\u0174\u0175\7\61\2\2\u0175\u0176\7"+
		"\61\2\2\u0176\u017a\3\2\2\2\u0177\u0179\n\2\2\2\u0178\u0177\3\2\2\2\u0179"+
		"\u017c\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2\2\2\u017b\u017e\3\2"+
		"\2\2\u017c\u017a\3\2\2\2\u017d\u017f\7\17\2\2\u017e\u017d\3\2\2\2\u017e"+
		"\u017f\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0181\7\f\2\2\u0181\u0182\3\2"+
		"\2\2\u0182\u0183\bB\2\2\u0183\u0084\3\2\2\2\u0184\u0186\t\6\2\2\u0185"+
		"\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0185\3\2\2\2\u0187\u0188\3\2"+
		"\2\2\u0188\u0189\3\2\2\2\u0189\u018a\bC\2\2\u018a\u0086\3\2\2\2\r\2\u0104"+
		"\u0135\u013b\u015b\u015d\u016a\u0171\u017a\u017e\u0187\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}