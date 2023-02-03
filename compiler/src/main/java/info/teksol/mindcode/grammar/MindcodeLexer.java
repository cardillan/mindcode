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
		ALLOCATE=1, BREAK=2, CASE=3, CONTINUE=4, DEF=5, DO=6, ELSE=7, ELSIF=8, 
		END=9, FALSE=10, FOR=11, HEAP=12, IF=13, IN=14, LOOP=15, NULL=16, SENSOR=17, 
		STACK=18, TRUE=19, WHEN=20, WHILE=21, ASSIGN=22, AT=23, COLON=24, COMMA=25, 
		DIV=26, IDIV=27, DOLLAR=28, DOT=29, EXP=30, MINUS=31, MOD=32, MUL=33, 
		NOT=34, BITWISE_NOT=35, PLUS=36, QUESTION_MARK=37, SEMICOLON=38, DIV_ASSIGN=39, 
		EXP_ASSIGN=40, MINUS_ASSIGN=41, MUL_ASSIGN=42, PLUS_ASSIGN=43, LESS_THAN=44, 
		LESS_THAN_EQUAL=45, NOT_EQUAL=46, EQUAL=47, STRICT_EQUAL=48, STRICT_NOT_EQUAL=49, 
		GREATER_THAN_EQUAL=50, GREATER_THAN=51, AND=52, OR=53, SHIFT_LEFT=54, 
		SHIFT_RIGHT=55, BITWISE_AND=56, BITWISE_OR=57, BITWISE_XOR=58, LEFT_SBRACKET=59, 
		RIGHT_SBRACKET=60, LEFT_RBRACKET=61, RIGHT_RBRACKET=62, LEFT_CBRACKET=63, 
		RIGHT_CBRACKET=64, LITERAL=65, FLOAT=66, INT=67, HEXINT=68, ID=69, SL_COMMENT=70, 
		WS=71;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "DO", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "LOOP", "NULL", "SENSOR", 
			"STACK", "TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", 
			"IDIV", "DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", 
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
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'do'", 
			"'else'", "'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", 
			"'loop'", "'null'", "'sensor'", "'stack'", "'true'", "'when'", "'while'", 
			"'='", "'@'", "':'", "','", "'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", 
			"'%'", "'*'", null, "'~'", "'+'", "'?'", "';'", "'/='", "'**='", "'-='", 
			"'*='", "'+='", "'<'", "'<='", "'!='", "'=='", "'==='", "'!=='", "'>='", 
			"'>'", null, null, "'<<'", "'>>'", "'&'", "'|'", "'^'", "'['", "']'", 
			"'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "DO", "ELSE", "ELSIF", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2I\u01b3\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7"+
		"\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34"+
		"\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\5"+
		"#\u0120\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\3)\3)\3*\3*\3*\3+"+
		"\3+\3+\3,\3,\3,\3-\3-\3.\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61"+
		"\3\61\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\65\3\65\3\65\3\65"+
		"\3\65\5\65\u0157\n\65\3\66\3\66\3\66\3\66\5\66\u015d\n\66\3\67\3\67\3"+
		"\67\38\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\3A\3A\3B"+
		"\3B\3B\3C\3C\3C\7C\u017d\nC\fC\16C\u0180\13C\3C\3C\3D\3D\3D\3D\3E\3E\7"+
		"E\u018a\nE\fE\16E\u018d\13E\3F\3F\3F\6F\u0192\nF\rF\16F\u0193\3G\3G\7"+
		"G\u0198\nG\fG\16G\u019b\13G\3H\3H\3H\3H\7H\u01a1\nH\fH\16H\u01a4\13H\3"+
		"H\5H\u01a7\nH\3H\3H\3H\3H\3I\6I\u01ae\nI\rI\16I\u01af\3I\3I\3\u017e\2"+
		"J\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37"+
		"= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o"+
		"9q:s;u<w=y>{?}@\177A\u0081B\u0083\2\u0085C\u0087D\u0089E\u008bF\u008d"+
		"G\u008fH\u0091I\3\2\t\4\2\f\f\17\17\3\2\62;\4\2ZZzz\4\2\62;ch\5\2C\\a"+
		"ac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u01bc\2\3\3\2\2\2\2\5\3\2\2"+
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
		"\177\3\2\2\2\2\u0081\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3"+
		"\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2"+
		"\3\u0093\3\2\2\2\5\u009c\3\2\2\2\7\u00a2\3\2\2\2\t\u00a7\3\2\2\2\13\u00b0"+
		"\3\2\2\2\r\u00b4\3\2\2\2\17\u00b7\3\2\2\2\21\u00bc\3\2\2\2\23\u00c2\3"+
		"\2\2\2\25\u00c6\3\2\2\2\27\u00cc\3\2\2\2\31\u00d0\3\2\2\2\33\u00d5\3\2"+
		"\2\2\35\u00d8\3\2\2\2\37\u00db\3\2\2\2!\u00e0\3\2\2\2#\u00e5\3\2\2\2%"+
		"\u00ec\3\2\2\2\'\u00f2\3\2\2\2)\u00f7\3\2\2\2+\u00fc\3\2\2\2-\u0102\3"+
		"\2\2\2/\u0104\3\2\2\2\61\u0106\3\2\2\2\63\u0108\3\2\2\2\65\u010a\3\2\2"+
		"\2\67\u010c\3\2\2\29\u010e\3\2\2\2;\u0110\3\2\2\2=\u0112\3\2\2\2?\u0115"+
		"\3\2\2\2A\u0117\3\2\2\2C\u0119\3\2\2\2E\u011f\3\2\2\2G\u0121\3\2\2\2I"+
		"\u0123\3\2\2\2K\u0125\3\2\2\2M\u0127\3\2\2\2O\u0129\3\2\2\2Q\u012c\3\2"+
		"\2\2S\u0130\3\2\2\2U\u0133\3\2\2\2W\u0136\3\2\2\2Y\u0139\3\2\2\2[\u013b"+
		"\3\2\2\2]\u013e\3\2\2\2_\u0141\3\2\2\2a\u0144\3\2\2\2c\u0148\3\2\2\2e"+
		"\u014c\3\2\2\2g\u014f\3\2\2\2i\u0156\3\2\2\2k\u015c\3\2\2\2m\u015e\3\2"+
		"\2\2o\u0161\3\2\2\2q\u0164\3\2\2\2s\u0166\3\2\2\2u\u0168\3\2\2\2w\u016a"+
		"\3\2\2\2y\u016c\3\2\2\2{\u016e\3\2\2\2}\u0170\3\2\2\2\177\u0172\3\2\2"+
		"\2\u0081\u0174\3\2\2\2\u0083\u0176\3\2\2\2\u0085\u0179\3\2\2\2\u0087\u0183"+
		"\3\2\2\2\u0089\u0187\3\2\2\2\u008b\u018e\3\2\2\2\u008d\u0195\3\2\2\2\u008f"+
		"\u019c\3\2\2\2\u0091\u01ad\3\2\2\2\u0093\u0094\7c\2\2\u0094\u0095\7n\2"+
		"\2\u0095\u0096\7n\2\2\u0096\u0097\7q\2\2\u0097\u0098\7e\2\2\u0098\u0099"+
		"\7c\2\2\u0099\u009a\7v\2\2\u009a\u009b\7g\2\2\u009b\4\3\2\2\2\u009c\u009d"+
		"\7d\2\2\u009d\u009e\7t\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7c\2\2\u00a0"+
		"\u00a1\7m\2\2\u00a1\6\3\2\2\2\u00a2\u00a3\7e\2\2\u00a3\u00a4\7c\2\2\u00a4"+
		"\u00a5\7u\2\2\u00a5\u00a6\7g\2\2\u00a6\b\3\2\2\2\u00a7\u00a8\7e\2\2\u00a8"+
		"\u00a9\7q\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7v\2\2\u00ab\u00ac\7k\2\2"+
		"\u00ac\u00ad\7p\2\2\u00ad\u00ae\7w\2\2\u00ae\u00af\7g\2\2\u00af\n\3\2"+
		"\2\2\u00b0\u00b1\7f\2\2\u00b1\u00b2\7g\2\2\u00b2\u00b3\7h\2\2\u00b3\f"+
		"\3\2\2\2\u00b4\u00b5\7f\2\2\u00b5\u00b6\7q\2\2\u00b6\16\3\2\2\2\u00b7"+
		"\u00b8\7g\2\2\u00b8\u00b9\7n\2\2\u00b9\u00ba\7u\2\2\u00ba\u00bb\7g\2\2"+
		"\u00bb\20\3\2\2\2\u00bc\u00bd\7g\2\2\u00bd\u00be\7n\2\2\u00be\u00bf\7"+
		"u\2\2\u00bf\u00c0\7k\2\2\u00c0\u00c1\7h\2\2\u00c1\22\3\2\2\2\u00c2\u00c3"+
		"\7g\2\2\u00c3\u00c4\7p\2\2\u00c4\u00c5\7f\2\2\u00c5\24\3\2\2\2\u00c6\u00c7"+
		"\7h\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7n\2\2\u00c9\u00ca\7u\2\2\u00ca"+
		"\u00cb\7g\2\2\u00cb\26\3\2\2\2\u00cc\u00cd\7h\2\2\u00cd\u00ce\7q\2\2\u00ce"+
		"\u00cf\7t\2\2\u00cf\30\3\2\2\2\u00d0\u00d1\7j\2\2\u00d1\u00d2\7g\2\2\u00d2"+
		"\u00d3\7c\2\2\u00d3\u00d4\7r\2\2\u00d4\32\3\2\2\2\u00d5\u00d6\7k\2\2\u00d6"+
		"\u00d7\7h\2\2\u00d7\34\3\2\2\2\u00d8\u00d9\7k\2\2\u00d9\u00da\7p\2\2\u00da"+
		"\36\3\2\2\2\u00db\u00dc\7n\2\2\u00dc\u00dd\7q\2\2\u00dd\u00de\7q\2\2\u00de"+
		"\u00df\7r\2\2\u00df \3\2\2\2\u00e0\u00e1\7p\2\2\u00e1\u00e2\7w\2\2\u00e2"+
		"\u00e3\7n\2\2\u00e3\u00e4\7n\2\2\u00e4\"\3\2\2\2\u00e5\u00e6\7u\2\2\u00e6"+
		"\u00e7\7g\2\2\u00e7\u00e8\7p\2\2\u00e8\u00e9\7u\2\2\u00e9\u00ea\7q\2\2"+
		"\u00ea\u00eb\7t\2\2\u00eb$\3\2\2\2\u00ec\u00ed\7u\2\2\u00ed\u00ee\7v\2"+
		"\2\u00ee\u00ef\7c\2\2\u00ef\u00f0\7e\2\2\u00f0\u00f1\7m\2\2\u00f1&\3\2"+
		"\2\2\u00f2\u00f3\7v\2\2\u00f3\u00f4\7t\2\2\u00f4\u00f5\7w\2\2\u00f5\u00f6"+
		"\7g\2\2\u00f6(\3\2\2\2\u00f7\u00f8\7y\2\2\u00f8\u00f9\7j\2\2\u00f9\u00fa"+
		"\7g\2\2\u00fa\u00fb\7p\2\2\u00fb*\3\2\2\2\u00fc\u00fd\7y\2\2\u00fd\u00fe"+
		"\7j\2\2\u00fe\u00ff\7k\2\2\u00ff\u0100\7n\2\2\u0100\u0101\7g\2\2\u0101"+
		",\3\2\2\2\u0102\u0103\7?\2\2\u0103.\3\2\2\2\u0104\u0105\7B\2\2\u0105\60"+
		"\3\2\2\2\u0106\u0107\7<\2\2\u0107\62\3\2\2\2\u0108\u0109\7.\2\2\u0109"+
		"\64\3\2\2\2\u010a\u010b\7\61\2\2\u010b\66\3\2\2\2\u010c\u010d\7^\2\2\u010d"+
		"8\3\2\2\2\u010e\u010f\7&\2\2\u010f:\3\2\2\2\u0110\u0111\7\60\2\2\u0111"+
		"<\3\2\2\2\u0112\u0113\7,\2\2\u0113\u0114\7,\2\2\u0114>\3\2\2\2\u0115\u0116"+
		"\7/\2\2\u0116@\3\2\2\2\u0117\u0118\7\'\2\2\u0118B\3\2\2\2\u0119\u011a"+
		"\7,\2\2\u011aD\3\2\2\2\u011b\u0120\7#\2\2\u011c\u011d\7p\2\2\u011d\u011e"+
		"\7q\2\2\u011e\u0120\7v\2\2\u011f\u011b\3\2\2\2\u011f\u011c\3\2\2\2\u0120"+
		"F\3\2\2\2\u0121\u0122\7\u0080\2\2\u0122H\3\2\2\2\u0123\u0124\7-\2\2\u0124"+
		"J\3\2\2\2\u0125\u0126\7A\2\2\u0126L\3\2\2\2\u0127\u0128\7=\2\2\u0128N"+
		"\3\2\2\2\u0129\u012a\7\61\2\2\u012a\u012b\7?\2\2\u012bP\3\2\2\2\u012c"+
		"\u012d\7,\2\2\u012d\u012e\7,\2\2\u012e\u012f\7?\2\2\u012fR\3\2\2\2\u0130"+
		"\u0131\7/\2\2\u0131\u0132\7?\2\2\u0132T\3\2\2\2\u0133\u0134\7,\2\2\u0134"+
		"\u0135\7?\2\2\u0135V\3\2\2\2\u0136\u0137\7-\2\2\u0137\u0138\7?\2\2\u0138"+
		"X\3\2\2\2\u0139\u013a\7>\2\2\u013aZ\3\2\2\2\u013b\u013c\7>\2\2\u013c\u013d"+
		"\7?\2\2\u013d\\\3\2\2\2\u013e\u013f\7#\2\2\u013f\u0140\7?\2\2\u0140^\3"+
		"\2\2\2\u0141\u0142\7?\2\2\u0142\u0143\7?\2\2\u0143`\3\2\2\2\u0144\u0145"+
		"\7?\2\2\u0145\u0146\7?\2\2\u0146\u0147\7?\2\2\u0147b\3\2\2\2\u0148\u0149"+
		"\7#\2\2\u0149\u014a\7?\2\2\u014a\u014b\7?\2\2\u014bd\3\2\2\2\u014c\u014d"+
		"\7@\2\2\u014d\u014e\7?\2\2\u014ef\3\2\2\2\u014f\u0150\7@\2\2\u0150h\3"+
		"\2\2\2\u0151\u0152\7(\2\2\u0152\u0157\7(\2\2\u0153\u0154\7c\2\2\u0154"+
		"\u0155\7p\2\2\u0155\u0157\7f\2\2\u0156\u0151\3\2\2\2\u0156\u0153\3\2\2"+
		"\2\u0157j\3\2\2\2\u0158\u0159\7~\2\2\u0159\u015d\7~\2\2\u015a\u015b\7"+
		"q\2\2\u015b\u015d\7t\2\2\u015c\u0158\3\2\2\2\u015c\u015a\3\2\2\2\u015d"+
		"l\3\2\2\2\u015e\u015f\7>\2\2\u015f\u0160\7>\2\2\u0160n\3\2\2\2\u0161\u0162"+
		"\7@\2\2\u0162\u0163\7@\2\2\u0163p\3\2\2\2\u0164\u0165\7(\2\2\u0165r\3"+
		"\2\2\2\u0166\u0167\7~\2\2\u0167t\3\2\2\2\u0168\u0169\7`\2\2\u0169v\3\2"+
		"\2\2\u016a\u016b\7]\2\2\u016bx\3\2\2\2\u016c\u016d\7_\2\2\u016dz\3\2\2"+
		"\2\u016e\u016f\7*\2\2\u016f|\3\2\2\2\u0170\u0171\7+\2\2\u0171~\3\2\2\2"+
		"\u0172\u0173\7}\2\2\u0173\u0080\3\2\2\2\u0174\u0175\7\177\2\2\u0175\u0082"+
		"\3\2\2\2\u0176\u0177\7^\2\2\u0177\u0178\7$\2\2\u0178\u0084\3\2\2\2\u0179"+
		"\u017e\7$\2\2\u017a\u017d\5\u0083B\2\u017b\u017d\n\2\2\2\u017c\u017a\3"+
		"\2\2\2\u017c\u017b\3\2\2\2\u017d\u0180\3\2\2\2\u017e\u017f\3\2\2\2\u017e"+
		"\u017c\3\2\2\2\u017f\u0181\3\2\2\2\u0180\u017e\3\2\2\2\u0181\u0182\7$"+
		"\2\2\u0182\u0086\3\2\2\2\u0183\u0184\5\u0089E\2\u0184\u0185\5;\36\2\u0185"+
		"\u0186\5\u0089E\2\u0186\u0088\3\2\2\2\u0187\u018b\t\3\2\2\u0188\u018a"+
		"\t\3\2\2\u0189\u0188\3\2\2\2\u018a\u018d\3\2\2\2\u018b\u0189\3\2\2\2\u018b"+
		"\u018c\3\2\2\2\u018c\u008a\3\2\2\2\u018d\u018b\3\2\2\2\u018e\u018f\7\62"+
		"\2\2\u018f\u0191\t\4\2\2\u0190\u0192\t\5\2\2\u0191\u0190\3\2\2\2\u0192"+
		"\u0193\3\2\2\2\u0193\u0191\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u008c\3\2"+
		"\2\2\u0195\u0199\t\6\2\2\u0196\u0198\t\7\2\2\u0197\u0196\3\2\2\2\u0198"+
		"\u019b\3\2\2\2\u0199\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u008e\3\2"+
		"\2\2\u019b\u0199\3\2\2\2\u019c\u019d\7\61\2\2\u019d\u019e\7\61\2\2\u019e"+
		"\u01a2\3\2\2\2\u019f\u01a1\n\2\2\2\u01a0\u019f\3\2\2\2\u01a1\u01a4\3\2"+
		"\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a6\3\2\2\2\u01a4"+
		"\u01a2\3\2\2\2\u01a5\u01a7\7\17\2\2\u01a6\u01a5\3\2\2\2\u01a6\u01a7\3"+
		"\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01a9\7\f\2\2\u01a9\u01aa\3\2\2\2\u01aa"+
		"\u01ab\bH\2\2\u01ab\u0090\3\2\2\2\u01ac\u01ae\t\b\2\2\u01ad\u01ac\3\2"+
		"\2\2\u01ae\u01af\3\2\2\2\u01af\u01ad\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0"+
		"\u01b1\3\2\2\2\u01b1\u01b2\bI\2\2\u01b2\u0092\3\2\2\2\16\2\u011f\u0156"+
		"\u015c\u017c\u017e\u018b\u0193\u0199\u01a2\u01a6\u01af\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}