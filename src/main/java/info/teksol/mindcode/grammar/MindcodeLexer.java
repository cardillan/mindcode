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
		TO=1, UNTIL=2, LITERAL=3, WHILE=4, IF=5, ELSE=6, FOR=7, IN=8, EXCLUSIVE=9, 
		ALLOCATE=10, HEAP=11, AT=12, DOT=13, COMMA=14, SEMICOLON=15, DOLLAR=16, 
		ASSIGN=17, PLUS_ASSIGN=18, MINUS_ASSIGN=19, MUL_ASSIGN=20, DIV_ASSIGN=21, 
		MOD_ASSIGN=22, EXP_ASSIGN=23, PLUS=24, MINUS=25, MUL=26, DIV=27, MOD=28, 
		EXP=29, TRUE=30, FALSE=31, STRICT_EQUAL=32, EQUAL=33, NOT_EQUAL=34, GREATER=35, 
		LESS=36, LESS_EQUAL=37, GREATER_EQUAL=38, AND=39, OR=40, NOT=41, LEFT_RBRACKET=42, 
		RIGHT_RBRACKET=43, LEFT_SBRACKET=44, RIGHT_SBRACKET=45, LEFT_CBRACKET=46, 
		RIGHT_CBRACKET=47, NULL=48, INT=49, FLOAT=50, ID=51, REF=52, CRLF=53, 
		WS=54, SL_COMMENT=55;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TO", "UNTIL", "ESCAPED_QUOTE", "LITERAL", "WHILE", "IF", "ELSE", "FOR", 
			"IN", "EXCLUSIVE", "ALLOCATE", "HEAP", "AT", "DOT", "COMMA", "SEMICOLON", 
			"DOLLAR", "ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"MOD_ASSIGN", "EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", 
			"TRUE", "FALSE", "STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", 
			"LESS_EQUAL", "GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"NULL", "INT", "FLOAT", "ID", "REF", "CRLF", "WS", "SL_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'to'", "'until'", null, "'while'", "'if'", "'else'", "'for'", 
			"'in'", "'exclusive'", "'allocate'", "'heap'", "'@'", "'.'", "','", "';'", 
			"'$'", "'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'**='", "'+'", 
			"'-'", "'*'", "'/'", "'%'", "'**'", "'true'", "'false'", "'==='", "'=='", 
			"'!='", "'>'", "'<'", "'<='", "'>='", null, null, null, "'('", "')'", 
			"'['", "']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TO", "UNTIL", "LITERAL", "WHILE", "IF", "ELSE", "FOR", "IN", "EXCLUSIVE", 
			"ALLOCATE", "HEAP", "AT", "DOT", "COMMA", "SEMICOLON", "DOLLAR", "ASSIGN", 
			"PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", 
			"EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", "TRUE", "FALSE", 
			"STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", 
			"GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"NULL", "INT", "FLOAT", "ID", "REF", "CRLF", "WS", "SL_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\29\u015a\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\3\2\3\2\3\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\7\5\u0083\n\5\f\5\16\5\u0086\13\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16"+
		"\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37"+
		"\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3"+
		"%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3)\3)\3)\5)\u0107\n)\3*\3*\3*\3"+
		"*\5*\u010d\n*\3+\3+\3+\3+\5+\u0113\n+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60"+
		"\3\61\3\61\3\62\3\62\3\62\3\62\3\62\3\63\6\63\u0127\n\63\r\63\16\63\u0128"+
		"\3\64\7\64\u012c\n\64\f\64\16\64\u012f\13\64\3\64\3\64\6\64\u0133\n\64"+
		"\r\64\16\64\u0134\3\65\3\65\7\65\u0139\n\65\f\65\16\65\u013c\13\65\3\66"+
		"\3\66\3\66\7\66\u0141\n\66\f\66\16\66\u0144\13\66\3\67\5\67\u0147\n\67"+
		"\3\67\3\67\38\68\u014c\n8\r8\168\u014d\38\38\39\39\39\39\79\u0156\n9\f"+
		"9\169\u0159\139\3\u0084\2:\3\3\5\4\7\2\t\5\13\6\r\7\17\b\21\t\23\n\25"+
		"\13\27\f\31\r\33\16\35\17\37\20!\21#\22%\23\'\24)\25+\26-\27/\30\61\31"+
		"\63\32\65\33\67\349\35;\36=\37? A!C\"E#G$I%K&M\'O(Q)S*U+W,Y-[.]/_\60a"+
		"\61c\62e\63g\64i\65k\66m\67o8q9\3\2\b\4\2\f\f\17\17\3\2\62;\5\2C\\aac"+
		"|\6\2\62;C\\aac|\7\2//\62;C\\aac|\4\2\13\13\"\"\2\u0165\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2"+
		"\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2"+
		"M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3"+
		"\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2"+
		"\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\3"+
		"s\3\2\2\2\5v\3\2\2\2\7|\3\2\2\2\t\177\3\2\2\2\13\u0089\3\2\2\2\r\u008f"+
		"\3\2\2\2\17\u0092\3\2\2\2\21\u0097\3\2\2\2\23\u009b\3\2\2\2\25\u009e\3"+
		"\2\2\2\27\u00a8\3\2\2\2\31\u00b1\3\2\2\2\33\u00b6\3\2\2\2\35\u00b8\3\2"+
		"\2\2\37\u00ba\3\2\2\2!\u00bc\3\2\2\2#\u00be\3\2\2\2%\u00c0\3\2\2\2\'\u00c2"+
		"\3\2\2\2)\u00c5\3\2\2\2+\u00c8\3\2\2\2-\u00cb\3\2\2\2/\u00ce\3\2\2\2\61"+
		"\u00d1\3\2\2\2\63\u00d5\3\2\2\2\65\u00d7\3\2\2\2\67\u00d9\3\2\2\29\u00db"+
		"\3\2\2\2;\u00dd\3\2\2\2=\u00df\3\2\2\2?\u00e2\3\2\2\2A\u00e7\3\2\2\2C"+
		"\u00ed\3\2\2\2E\u00f1\3\2\2\2G\u00f4\3\2\2\2I\u00f7\3\2\2\2K\u00f9\3\2"+
		"\2\2M\u00fb\3\2\2\2O\u00fe\3\2\2\2Q\u0106\3\2\2\2S\u010c\3\2\2\2U\u0112"+
		"\3\2\2\2W\u0114\3\2\2\2Y\u0116\3\2\2\2[\u0118\3\2\2\2]\u011a\3\2\2\2_"+
		"\u011c\3\2\2\2a\u011e\3\2\2\2c\u0120\3\2\2\2e\u0126\3\2\2\2g\u012d\3\2"+
		"\2\2i\u0136\3\2\2\2k\u013d\3\2\2\2m\u0146\3\2\2\2o\u014b\3\2\2\2q\u0151"+
		"\3\2\2\2st\7v\2\2tu\7q\2\2u\4\3\2\2\2vw\7w\2\2wx\7p\2\2xy\7v\2\2yz\7k"+
		"\2\2z{\7n\2\2{\6\3\2\2\2|}\7^\2\2}~\7$\2\2~\b\3\2\2\2\177\u0084\7$\2\2"+
		"\u0080\u0083\5\7\4\2\u0081\u0083\n\2\2\2\u0082\u0080\3\2\2\2\u0082\u0081"+
		"\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0085\3\2\2\2\u0084\u0082\3\2\2\2\u0085"+
		"\u0087\3\2\2\2\u0086\u0084\3\2\2\2\u0087\u0088\7$\2\2\u0088\n\3\2\2\2"+
		"\u0089\u008a\7y\2\2\u008a\u008b\7j\2\2\u008b\u008c\7k\2\2\u008c\u008d"+
		"\7n\2\2\u008d\u008e\7g\2\2\u008e\f\3\2\2\2\u008f\u0090\7k\2\2\u0090\u0091"+
		"\7h\2\2\u0091\16\3\2\2\2\u0092\u0093\7g\2\2\u0093\u0094\7n\2\2\u0094\u0095"+
		"\7u\2\2\u0095\u0096\7g\2\2\u0096\20\3\2\2\2\u0097\u0098\7h\2\2\u0098\u0099"+
		"\7q\2\2\u0099\u009a\7t\2\2\u009a\22\3\2\2\2\u009b\u009c\7k\2\2\u009c\u009d"+
		"\7p\2\2\u009d\24\3\2\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7z\2\2\u00a0\u00a1"+
		"\7e\2\2\u00a1\u00a2\7n\2\2\u00a2\u00a3\7w\2\2\u00a3\u00a4\7u\2\2\u00a4"+
		"\u00a5\7k\2\2\u00a5\u00a6\7x\2\2\u00a6\u00a7\7g\2\2\u00a7\26\3\2\2\2\u00a8"+
		"\u00a9\7c\2\2\u00a9\u00aa\7n\2\2\u00aa\u00ab\7n\2\2\u00ab\u00ac\7q\2\2"+
		"\u00ac\u00ad\7e\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af\7v\2\2\u00af\u00b0"+
		"\7g\2\2\u00b0\30\3\2\2\2\u00b1\u00b2\7j\2\2\u00b2\u00b3\7g\2\2\u00b3\u00b4"+
		"\7c\2\2\u00b4\u00b5\7r\2\2\u00b5\32\3\2\2\2\u00b6\u00b7\7B\2\2\u00b7\34"+
		"\3\2\2\2\u00b8\u00b9\7\60\2\2\u00b9\36\3\2\2\2\u00ba\u00bb\7.\2\2\u00bb"+
		" \3\2\2\2\u00bc\u00bd\7=\2\2\u00bd\"\3\2\2\2\u00be\u00bf\7&\2\2\u00bf"+
		"$\3\2\2\2\u00c0\u00c1\7?\2\2\u00c1&\3\2\2\2\u00c2\u00c3\7-\2\2\u00c3\u00c4"+
		"\7?\2\2\u00c4(\3\2\2\2\u00c5\u00c6\7/\2\2\u00c6\u00c7\7?\2\2\u00c7*\3"+
		"\2\2\2\u00c8\u00c9\7,\2\2\u00c9\u00ca\7?\2\2\u00ca,\3\2\2\2\u00cb\u00cc"+
		"\7\61\2\2\u00cc\u00cd\7?\2\2\u00cd.\3\2\2\2\u00ce\u00cf\7\'\2\2\u00cf"+
		"\u00d0\7?\2\2\u00d0\60\3\2\2\2\u00d1\u00d2\7,\2\2\u00d2\u00d3\7,\2\2\u00d3"+
		"\u00d4\7?\2\2\u00d4\62\3\2\2\2\u00d5\u00d6\7-\2\2\u00d6\64\3\2\2\2\u00d7"+
		"\u00d8\7/\2\2\u00d8\66\3\2\2\2\u00d9\u00da\7,\2\2\u00da8\3\2\2\2\u00db"+
		"\u00dc\7\61\2\2\u00dc:\3\2\2\2\u00dd\u00de\7\'\2\2\u00de<\3\2\2\2\u00df"+
		"\u00e0\7,\2\2\u00e0\u00e1\7,\2\2\u00e1>\3\2\2\2\u00e2\u00e3\7v\2\2\u00e3"+
		"\u00e4\7t\2\2\u00e4\u00e5\7w\2\2\u00e5\u00e6\7g\2\2\u00e6@\3\2\2\2\u00e7"+
		"\u00e8\7h\2\2\u00e8\u00e9\7c\2\2\u00e9\u00ea\7n\2\2\u00ea\u00eb\7u\2\2"+
		"\u00eb\u00ec\7g\2\2\u00ecB\3\2\2\2\u00ed\u00ee\7?\2\2\u00ee\u00ef\7?\2"+
		"\2\u00ef\u00f0\7?\2\2\u00f0D\3\2\2\2\u00f1\u00f2\7?\2\2\u00f2\u00f3\7"+
		"?\2\2\u00f3F\3\2\2\2\u00f4\u00f5\7#\2\2\u00f5\u00f6\7?\2\2\u00f6H\3\2"+
		"\2\2\u00f7\u00f8\7@\2\2\u00f8J\3\2\2\2\u00f9\u00fa\7>\2\2\u00faL\3\2\2"+
		"\2\u00fb\u00fc\7>\2\2\u00fc\u00fd\7?\2\2\u00fdN\3\2\2\2\u00fe\u00ff\7"+
		"@\2\2\u00ff\u0100\7?\2\2\u0100P\3\2\2\2\u0101\u0102\7c\2\2\u0102\u0103"+
		"\7p\2\2\u0103\u0107\7f\2\2\u0104\u0105\7(\2\2\u0105\u0107\7(\2\2\u0106"+
		"\u0101\3\2\2\2\u0106\u0104\3\2\2\2\u0107R\3\2\2\2\u0108\u0109\7q\2\2\u0109"+
		"\u010d\7t\2\2\u010a\u010b\7~\2\2\u010b\u010d\7~\2\2\u010c\u0108\3\2\2"+
		"\2\u010c\u010a\3\2\2\2\u010dT\3\2\2\2\u010e\u010f\7p\2\2\u010f\u0110\7"+
		"q\2\2\u0110\u0113\7v\2\2\u0111\u0113\7#\2\2\u0112\u010e\3\2\2\2\u0112"+
		"\u0111\3\2\2\2\u0113V\3\2\2\2\u0114\u0115\7*\2\2\u0115X\3\2\2\2\u0116"+
		"\u0117\7+\2\2\u0117Z\3\2\2\2\u0118\u0119\7]\2\2\u0119\\\3\2\2\2\u011a"+
		"\u011b\7_\2\2\u011b^\3\2\2\2\u011c\u011d\7}\2\2\u011d`\3\2\2\2\u011e\u011f"+
		"\7\177\2\2\u011fb\3\2\2\2\u0120\u0121\7p\2\2\u0121\u0122\7w\2\2\u0122"+
		"\u0123\7n\2\2\u0123\u0124\7n\2\2\u0124d\3\2\2\2\u0125\u0127\t\3\2\2\u0126"+
		"\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2"+
		"\2\2\u0129f\3\2\2\2\u012a\u012c\t\3\2\2\u012b\u012a\3\2\2\2\u012c\u012f"+
		"\3\2\2\2\u012d\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u0130\3\2\2\2\u012f"+
		"\u012d\3\2\2\2\u0130\u0132\7\60\2\2\u0131\u0133\t\3\2\2\u0132\u0131\3"+
		"\2\2\2\u0133\u0134\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135"+
		"h\3\2\2\2\u0136\u013a\t\4\2\2\u0137\u0139\t\5\2\2\u0138\u0137\3\2\2\2"+
		"\u0139\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013bj\3"+
		"\2\2\2\u013c\u013a\3\2\2\2\u013d\u013e\5\33\16\2\u013e\u0142\t\4\2\2\u013f"+
		"\u0141\t\6\2\2\u0140\u013f\3\2\2\2\u0141\u0144\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0142\u0143\3\2\2\2\u0143l\3\2\2\2\u0144\u0142\3\2\2\2\u0145\u0147"+
		"\7\17\2\2\u0146\u0145\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148\3\2\2\2"+
		"\u0148\u0149\7\f\2\2\u0149n\3\2\2\2\u014a\u014c\t\7\2\2\u014b\u014a\3"+
		"\2\2\2\u014c\u014d\3\2\2\2\u014d\u014b\3\2\2\2\u014d\u014e\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0150\b8\2\2\u0150p\3\2\2\2\u0151\u0152\7\61\2\2"+
		"\u0152\u0153\7\61\2\2\u0153\u0157\3\2\2\2\u0154\u0156\n\2\2\2\u0155\u0154"+
		"\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158"+
		"r\3\2\2\2\u0159\u0157\3\2\2\2\20\2\u0082\u0084\u0106\u010c\u0112\u0128"+
		"\u012d\u0134\u013a\u0142\u0146\u014d\u0157\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}