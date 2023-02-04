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
		STACK=18, THEN=19, TRUE=20, WHEN=21, WHILE=22, ASSIGN=23, AT=24, COLON=25, 
		COMMA=26, DIV=27, IDIV=28, DOLLAR=29, DOT=30, EXP=31, MINUS=32, MOD=33, 
		MUL=34, NOT=35, BITWISE_NOT=36, PLUS=37, QUESTION_MARK=38, SEMICOLON=39, 
		DIV_ASSIGN=40, EXP_ASSIGN=41, MINUS_ASSIGN=42, MUL_ASSIGN=43, PLUS_ASSIGN=44, 
		LESS_THAN=45, LESS_THAN_EQUAL=46, NOT_EQUAL=47, EQUAL=48, STRICT_EQUAL=49, 
		STRICT_NOT_EQUAL=50, GREATER_THAN_EQUAL=51, GREATER_THAN=52, AND=53, OR=54, 
		SHIFT_LEFT=55, SHIFT_RIGHT=56, BITWISE_AND=57, BITWISE_OR=58, BITWISE_XOR=59, 
		LEFT_SBRACKET=60, RIGHT_SBRACKET=61, LEFT_RBRACKET=62, RIGHT_RBRACKET=63, 
		LEFT_CBRACKET=64, RIGHT_CBRACKET=65, LITERAL=66, FLOAT=67, INT=68, HEXINT=69, 
		ID=70, SL_COMMENT=71, WS=72;
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
			"STACK", "THEN", "TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", 
			"DIV", "IDIV", "DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", 
			"BITWISE_NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", "EXP_ASSIGN", 
			"MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", 
			"NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", 
			"BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", 
			"LITERAL", "FLOAT", "INT", "HEXINT", "ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'do'", 
			"'else'", "'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", 
			"'loop'", "'null'", "'sensor'", "'stack'", "'then'", "'true'", "'when'", 
			"'while'", "'='", "'@'", "':'", "','", "'/'", "'\\'", "'$'", "'.'", "'**'", 
			"'-'", "'%'", "'*'", null, "'~'", "'+'", "'?'", "';'", "'/='", "'**='", 
			"'-='", "'*='", "'+='", "'<'", "'<='", "'!='", "'=='", "'==='", "'!=='", 
			"'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", "'|'", "'^'", "'['", 
			"']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "DO", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "LOOP", "NULL", "SENSOR", 
			"STACK", "THEN", "TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", 
			"DIV", "IDIV", "DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", 
			"BITWISE_NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", "EXP_ASSIGN", 
			"MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", 
			"NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", 
			"BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "LITERAL", "FLOAT", 
			"INT", "HEXINT", "ID", "SL_COMMENT", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2J\u01ba\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3"+
		"\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n"+
		"\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32"+
		"\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3 \3!\3!\3\""+
		"\3\"\3#\3#\3$\3$\3$\3$\5$\u0127\n$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3)"+
		"\3*\3*\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3/\3/\3/\3\60\3\60\3\60"+
		"\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\64\3\64\3\64"+
		"\3\65\3\65\3\66\3\66\3\66\3\66\3\66\5\66\u015e\n\66\3\67\3\67\3\67\3\67"+
		"\5\67\u0164\n\67\38\38\38\39\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3"+
		"?\3@\3@\3A\3A\3B\3B\3C\3C\3C\3D\3D\3D\7D\u0184\nD\fD\16D\u0187\13D\3D"+
		"\3D\3E\3E\3E\3E\3F\3F\7F\u0191\nF\fF\16F\u0194\13F\3G\3G\3G\6G\u0199\n"+
		"G\rG\16G\u019a\3H\3H\7H\u019f\nH\fH\16H\u01a2\13H\3I\3I\3I\3I\7I\u01a8"+
		"\nI\fI\16I\u01ab\13I\3I\5I\u01ae\nI\3I\3I\3I\3I\3J\6J\u01b5\nJ\rJ\16J"+
		"\u01b6\3J\3J\3\u0185\2K\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63"+
		"\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62"+
		"c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085\2\u0087"+
		"D\u0089E\u008bF\u008dG\u008fH\u0091I\u0093J\3\2\t\4\2\f\f\17\17\3\2\62"+
		";\4\2ZZzz\4\2\62;ch\5\2C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u01c3"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
		"\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2"+
		"\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2"+
		"{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0087"+
		"\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2"+
		"\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\3\u0095\3\2\2\2\5\u009e\3\2\2\2\7\u00a4"+
		"\3\2\2\2\t\u00a9\3\2\2\2\13\u00b2\3\2\2\2\r\u00b6\3\2\2\2\17\u00b9\3\2"+
		"\2\2\21\u00be\3\2\2\2\23\u00c4\3\2\2\2\25\u00c8\3\2\2\2\27\u00ce\3\2\2"+
		"\2\31\u00d2\3\2\2\2\33\u00d7\3\2\2\2\35\u00da\3\2\2\2\37\u00dd\3\2\2\2"+
		"!\u00e2\3\2\2\2#\u00e7\3\2\2\2%\u00ee\3\2\2\2\'\u00f4\3\2\2\2)\u00f9\3"+
		"\2\2\2+\u00fe\3\2\2\2-\u0103\3\2\2\2/\u0109\3\2\2\2\61\u010b\3\2\2\2\63"+
		"\u010d\3\2\2\2\65\u010f\3\2\2\2\67\u0111\3\2\2\29\u0113\3\2\2\2;\u0115"+
		"\3\2\2\2=\u0117\3\2\2\2?\u0119\3\2\2\2A\u011c\3\2\2\2C\u011e\3\2\2\2E"+
		"\u0120\3\2\2\2G\u0126\3\2\2\2I\u0128\3\2\2\2K\u012a\3\2\2\2M\u012c\3\2"+
		"\2\2O\u012e\3\2\2\2Q\u0130\3\2\2\2S\u0133\3\2\2\2U\u0137\3\2\2\2W\u013a"+
		"\3\2\2\2Y\u013d\3\2\2\2[\u0140\3\2\2\2]\u0142\3\2\2\2_\u0145\3\2\2\2a"+
		"\u0148\3\2\2\2c\u014b\3\2\2\2e\u014f\3\2\2\2g\u0153\3\2\2\2i\u0156\3\2"+
		"\2\2k\u015d\3\2\2\2m\u0163\3\2\2\2o\u0165\3\2\2\2q\u0168\3\2\2\2s\u016b"+
		"\3\2\2\2u\u016d\3\2\2\2w\u016f\3\2\2\2y\u0171\3\2\2\2{\u0173\3\2\2\2}"+
		"\u0175\3\2\2\2\177\u0177\3\2\2\2\u0081\u0179\3\2\2\2\u0083\u017b\3\2\2"+
		"\2\u0085\u017d\3\2\2\2\u0087\u0180\3\2\2\2\u0089\u018a\3\2\2\2\u008b\u018e"+
		"\3\2\2\2\u008d\u0195\3\2\2\2\u008f\u019c\3\2\2\2\u0091\u01a3\3\2\2\2\u0093"+
		"\u01b4\3\2\2\2\u0095\u0096\7c\2\2\u0096\u0097\7n\2\2\u0097\u0098\7n\2"+
		"\2\u0098\u0099\7q\2\2\u0099\u009a\7e\2\2\u009a\u009b\7c\2\2\u009b\u009c"+
		"\7v\2\2\u009c\u009d\7g\2\2\u009d\4\3\2\2\2\u009e\u009f\7d\2\2\u009f\u00a0"+
		"\7t\2\2\u00a0\u00a1\7g\2\2\u00a1\u00a2\7c\2\2\u00a2\u00a3\7m\2\2\u00a3"+
		"\6\3\2\2\2\u00a4\u00a5\7e\2\2\u00a5\u00a6\7c\2\2\u00a6\u00a7\7u\2\2\u00a7"+
		"\u00a8\7g\2\2\u00a8\b\3\2\2\2\u00a9\u00aa\7e\2\2\u00aa\u00ab\7q\2\2\u00ab"+
		"\u00ac\7p\2\2\u00ac\u00ad\7v\2\2\u00ad\u00ae\7k\2\2\u00ae\u00af\7p\2\2"+
		"\u00af\u00b0\7w\2\2\u00b0\u00b1\7g\2\2\u00b1\n\3\2\2\2\u00b2\u00b3\7f"+
		"\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5\7h\2\2\u00b5\f\3\2\2\2\u00b6\u00b7"+
		"\7f\2\2\u00b7\u00b8\7q\2\2\u00b8\16\3\2\2\2\u00b9\u00ba\7g\2\2\u00ba\u00bb"+
		"\7n\2\2\u00bb\u00bc\7u\2\2\u00bc\u00bd\7g\2\2\u00bd\20\3\2\2\2\u00be\u00bf"+
		"\7g\2\2\u00bf\u00c0\7n\2\2\u00c0\u00c1\7u\2\2\u00c1\u00c2\7k\2\2\u00c2"+
		"\u00c3\7h\2\2\u00c3\22\3\2\2\2\u00c4\u00c5\7g\2\2\u00c5\u00c6\7p\2\2\u00c6"+
		"\u00c7\7f\2\2\u00c7\24\3\2\2\2\u00c8\u00c9\7h\2\2\u00c9\u00ca\7c\2\2\u00ca"+
		"\u00cb\7n\2\2\u00cb\u00cc\7u\2\2\u00cc\u00cd\7g\2\2\u00cd\26\3\2\2\2\u00ce"+
		"\u00cf\7h\2\2\u00cf\u00d0\7q\2\2\u00d0\u00d1\7t\2\2\u00d1\30\3\2\2\2\u00d2"+
		"\u00d3\7j\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7c\2\2\u00d5\u00d6\7r\2\2"+
		"\u00d6\32\3\2\2\2\u00d7\u00d8\7k\2\2\u00d8\u00d9\7h\2\2\u00d9\34\3\2\2"+
		"\2\u00da\u00db\7k\2\2\u00db\u00dc\7p\2\2\u00dc\36\3\2\2\2\u00dd\u00de"+
		"\7n\2\2\u00de\u00df\7q\2\2\u00df\u00e0\7q\2\2\u00e0\u00e1\7r\2\2\u00e1"+
		" \3\2\2\2\u00e2\u00e3\7p\2\2\u00e3\u00e4\7w\2\2\u00e4\u00e5\7n\2\2\u00e5"+
		"\u00e6\7n\2\2\u00e6\"\3\2\2\2\u00e7\u00e8\7u\2\2\u00e8\u00e9\7g\2\2\u00e9"+
		"\u00ea\7p\2\2\u00ea\u00eb\7u\2\2\u00eb\u00ec\7q\2\2\u00ec\u00ed\7t\2\2"+
		"\u00ed$\3\2\2\2\u00ee\u00ef\7u\2\2\u00ef\u00f0\7v\2\2\u00f0\u00f1\7c\2"+
		"\2\u00f1\u00f2\7e\2\2\u00f2\u00f3\7m\2\2\u00f3&\3\2\2\2\u00f4\u00f5\7"+
		"v\2\2\u00f5\u00f6\7j\2\2\u00f6\u00f7\7g\2\2\u00f7\u00f8\7p\2\2\u00f8("+
		"\3\2\2\2\u00f9\u00fa\7v\2\2\u00fa\u00fb\7t\2\2\u00fb\u00fc\7w\2\2\u00fc"+
		"\u00fd\7g\2\2\u00fd*\3\2\2\2\u00fe\u00ff\7y\2\2\u00ff\u0100\7j\2\2\u0100"+
		"\u0101\7g\2\2\u0101\u0102\7p\2\2\u0102,\3\2\2\2\u0103\u0104\7y\2\2\u0104"+
		"\u0105\7j\2\2\u0105\u0106\7k\2\2\u0106\u0107\7n\2\2\u0107\u0108\7g\2\2"+
		"\u0108.\3\2\2\2\u0109\u010a\7?\2\2\u010a\60\3\2\2\2\u010b\u010c\7B\2\2"+
		"\u010c\62\3\2\2\2\u010d\u010e\7<\2\2\u010e\64\3\2\2\2\u010f\u0110\7.\2"+
		"\2\u0110\66\3\2\2\2\u0111\u0112\7\61\2\2\u01128\3\2\2\2\u0113\u0114\7"+
		"^\2\2\u0114:\3\2\2\2\u0115\u0116\7&\2\2\u0116<\3\2\2\2\u0117\u0118\7\60"+
		"\2\2\u0118>\3\2\2\2\u0119\u011a\7,\2\2\u011a\u011b\7,\2\2\u011b@\3\2\2"+
		"\2\u011c\u011d\7/\2\2\u011dB\3\2\2\2\u011e\u011f\7\'\2\2\u011fD\3\2\2"+
		"\2\u0120\u0121\7,\2\2\u0121F\3\2\2\2\u0122\u0127\7#\2\2\u0123\u0124\7"+
		"p\2\2\u0124\u0125\7q\2\2\u0125\u0127\7v\2\2\u0126\u0122\3\2\2\2\u0126"+
		"\u0123\3\2\2\2\u0127H\3\2\2\2\u0128\u0129\7\u0080\2\2\u0129J\3\2\2\2\u012a"+
		"\u012b\7-\2\2\u012bL\3\2\2\2\u012c\u012d\7A\2\2\u012dN\3\2\2\2\u012e\u012f"+
		"\7=\2\2\u012fP\3\2\2\2\u0130\u0131\7\61\2\2\u0131\u0132\7?\2\2\u0132R"+
		"\3\2\2\2\u0133\u0134\7,\2\2\u0134\u0135\7,\2\2\u0135\u0136\7?\2\2\u0136"+
		"T\3\2\2\2\u0137\u0138\7/\2\2\u0138\u0139\7?\2\2\u0139V\3\2\2\2\u013a\u013b"+
		"\7,\2\2\u013b\u013c\7?\2\2\u013cX\3\2\2\2\u013d\u013e\7-\2\2\u013e\u013f"+
		"\7?\2\2\u013fZ\3\2\2\2\u0140\u0141\7>\2\2\u0141\\\3\2\2\2\u0142\u0143"+
		"\7>\2\2\u0143\u0144\7?\2\2\u0144^\3\2\2\2\u0145\u0146\7#\2\2\u0146\u0147"+
		"\7?\2\2\u0147`\3\2\2\2\u0148\u0149\7?\2\2\u0149\u014a\7?\2\2\u014ab\3"+
		"\2\2\2\u014b\u014c\7?\2\2\u014c\u014d\7?\2\2\u014d\u014e\7?\2\2\u014e"+
		"d\3\2\2\2\u014f\u0150\7#\2\2\u0150\u0151\7?\2\2\u0151\u0152\7?\2\2\u0152"+
		"f\3\2\2\2\u0153\u0154\7@\2\2\u0154\u0155\7?\2\2\u0155h\3\2\2\2\u0156\u0157"+
		"\7@\2\2\u0157j\3\2\2\2\u0158\u0159\7(\2\2\u0159\u015e\7(\2\2\u015a\u015b"+
		"\7c\2\2\u015b\u015c\7p\2\2\u015c\u015e\7f\2\2\u015d\u0158\3\2\2\2\u015d"+
		"\u015a\3\2\2\2\u015el\3\2\2\2\u015f\u0160\7~\2\2\u0160\u0164\7~\2\2\u0161"+
		"\u0162\7q\2\2\u0162\u0164\7t\2\2\u0163\u015f\3\2\2\2\u0163\u0161\3\2\2"+
		"\2\u0164n\3\2\2\2\u0165\u0166\7>\2\2\u0166\u0167\7>\2\2\u0167p\3\2\2\2"+
		"\u0168\u0169\7@\2\2\u0169\u016a\7@\2\2\u016ar\3\2\2\2\u016b\u016c\7(\2"+
		"\2\u016ct\3\2\2\2\u016d\u016e\7~\2\2\u016ev\3\2\2\2\u016f\u0170\7`\2\2"+
		"\u0170x\3\2\2\2\u0171\u0172\7]\2\2\u0172z\3\2\2\2\u0173\u0174\7_\2\2\u0174"+
		"|\3\2\2\2\u0175\u0176\7*\2\2\u0176~\3\2\2\2\u0177\u0178\7+\2\2\u0178\u0080"+
		"\3\2\2\2\u0179\u017a\7}\2\2\u017a\u0082\3\2\2\2\u017b\u017c\7\177\2\2"+
		"\u017c\u0084\3\2\2\2\u017d\u017e\7^\2\2\u017e\u017f\7$\2\2\u017f\u0086"+
		"\3\2\2\2\u0180\u0185\7$\2\2\u0181\u0184\5\u0085C\2\u0182\u0184\n\2\2\2"+
		"\u0183\u0181\3\2\2\2\u0183\u0182\3\2\2\2\u0184\u0187\3\2\2\2\u0185\u0186"+
		"\3\2\2\2\u0185\u0183\3\2\2\2\u0186\u0188\3\2\2\2\u0187\u0185\3\2\2\2\u0188"+
		"\u0189\7$\2\2\u0189\u0088\3\2\2\2\u018a\u018b\5\u008bF\2\u018b\u018c\5"+
		"=\37\2\u018c\u018d\5\u008bF\2\u018d\u008a\3\2\2\2\u018e\u0192\t\3\2\2"+
		"\u018f\u0191\t\3\2\2\u0190\u018f\3\2\2\2\u0191\u0194\3\2\2\2\u0192\u0190"+
		"\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u008c\3\2\2\2\u0194\u0192\3\2\2\2\u0195"+
		"\u0196\7\62\2\2\u0196\u0198\t\4\2\2\u0197\u0199\t\5\2\2\u0198\u0197\3"+
		"\2\2\2\u0199\u019a\3\2\2\2\u019a\u0198\3\2\2\2\u019a\u019b\3\2\2\2\u019b"+
		"\u008e\3\2\2\2\u019c\u01a0\t\6\2\2\u019d\u019f\t\7\2\2\u019e\u019d\3\2"+
		"\2\2\u019f\u01a2\3\2\2\2\u01a0\u019e\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1"+
		"\u0090\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a3\u01a4\7\61\2\2\u01a4\u01a5\7"+
		"\61\2\2\u01a5\u01a9\3\2\2\2\u01a6\u01a8\n\2\2\2\u01a7\u01a6\3\2\2\2\u01a8"+
		"\u01ab\3\2\2\2\u01a9\u01a7\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01ad\3\2"+
		"\2\2\u01ab\u01a9\3\2\2\2\u01ac\u01ae\7\17\2\2\u01ad\u01ac\3\2\2\2\u01ad"+
		"\u01ae\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u01b0\7\f\2\2\u01b0\u01b1\3\2"+
		"\2\2\u01b1\u01b2\bI\2\2\u01b2\u0092\3\2\2\2\u01b3\u01b5\t\b\2\2\u01b4"+
		"\u01b3\3\2\2\2\u01b5\u01b6\3\2\2\2\u01b6\u01b4\3\2\2\2\u01b6\u01b7\3\2"+
		"\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01b9\bJ\2\2\u01b9\u0094\3\2\2\2\16\2\u0126"+
		"\u015d\u0163\u0183\u0185\u0192\u019a\u01a0\u01a9\u01ad\u01b6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}