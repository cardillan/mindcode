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
		AT=10, DOT=11, COMMA=12, SEMICOLON=13, ASSIGN=14, PLUS_ASSIGN=15, MINUS_ASSIGN=16, 
		MUL_ASSIGN=17, DIV_ASSIGN=18, MOD_ASSIGN=19, EXP_ASSIGN=20, PLUS=21, MINUS=22, 
		MUL=23, DIV=24, MOD=25, EXP=26, TRUE=27, FALSE=28, STRICT_EQUAL=29, EQUAL=30, 
		NOT_EQUAL=31, GREATER=32, LESS=33, LESS_EQUAL=34, GREATER_EQUAL=35, AND=36, 
		OR=37, NOT=38, LEFT_RBRACKET=39, RIGHT_RBRACKET=40, LEFT_SBRACKET=41, 
		RIGHT_SBRACKET=42, LEFT_CBRACKET=43, RIGHT_CBRACKET=44, NULL=45, INT=46, 
		FLOAT=47, ID=48, REF=49, CRLF=50, WS=51, SL_COMMENT=52;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TO", "UNTIL", "ESCAPED_QUOTE", "LITERAL", "WHILE", "IF", "ELSE", "FOR", 
			"IN", "EXCLUSIVE", "AT", "DOT", "COMMA", "SEMICOLON", "ASSIGN", "PLUS_ASSIGN", 
			"MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "EXP_ASSIGN", 
			"PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", "TRUE", "FALSE", "STRICT_EQUAL", 
			"EQUAL", "NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", "GREATER_EQUAL", 
			"AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_SBRACKET", 
			"RIGHT_SBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "NULL", "INT", "FLOAT", 
			"ID", "REF", "CRLF", "WS", "SL_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'to'", "'until'", null, "'while'", "'if'", "'else'", "'for'", 
			"'in'", "'exclusive'", "'@'", "'.'", "','", "';'", "'='", "'+='", "'-='", 
			"'*='", "'/='", "'%='", "'**='", "'+'", "'-'", "'*'", "'/'", "'%'", "'**'", 
			"'true'", "'false'", "'==='", "'=='", "'!='", "'>'", "'<'", "'<='", "'>='", 
			null, null, null, "'('", "')'", "'['", "']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TO", "UNTIL", "LITERAL", "WHILE", "IF", "ELSE", "FOR", "IN", "EXCLUSIVE", 
			"AT", "DOT", "COMMA", "SEMICOLON", "ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", 
			"MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "EXP_ASSIGN", "PLUS", "MINUS", 
			"MUL", "DIV", "MOD", "EXP", "TRUE", "FALSE", "STRICT_EQUAL", "EQUAL", 
			"NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", "GREATER_EQUAL", "AND", 
			"OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "NULL", "INT", "FLOAT", "ID", "REF", 
			"CRLF", "WS", "SL_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\66\u0144\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4"+
		"\3\4\3\5\3\5\3\5\7\5}\n\5\f\5\16\5\u0080\13\5\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23"+
		"\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3 \3 \3 \3!\3!\3!\3"+
		"\"\3\"\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3&\3&\5&\u00f1\n&\3\'\3\'\3\'"+
		"\3\'\5\'\u00f7\n\'\3(\3(\3(\3(\5(\u00fd\n(\3)\3)\3*\3*\3+\3+\3,\3,\3-"+
		"\3-\3.\3.\3/\3/\3/\3/\3/\3\60\6\60\u0111\n\60\r\60\16\60\u0112\3\61\7"+
		"\61\u0116\n\61\f\61\16\61\u0119\13\61\3\61\3\61\6\61\u011d\n\61\r\61\16"+
		"\61\u011e\3\62\3\62\7\62\u0123\n\62\f\62\16\62\u0126\13\62\3\63\3\63\3"+
		"\63\7\63\u012b\n\63\f\63\16\63\u012e\13\63\3\64\5\64\u0131\n\64\3\64\3"+
		"\64\3\65\6\65\u0136\n\65\r\65\16\65\u0137\3\65\3\65\3\66\3\66\3\66\3\66"+
		"\7\66\u0140\n\66\f\66\16\66\u0143\13\66\3~\2\67\3\3\5\4\7\2\t\5\13\6\r"+
		"\7\17\b\21\t\23\n\25\13\27\f\31\r\33\16\35\17\37\20!\21#\22%\23\'\24)"+
		"\25+\26-\27/\30\61\31\63\32\65\33\67\349\35;\36=\37? A!C\"E#G$I%K&M\'"+
		"O(Q)S*U+W,Y-[.]/_\60a\61c\62e\63g\64i\65k\66\3\2\b\4\2\f\f\17\17\3\2\62"+
		";\5\2C\\aac|\6\2\62;C\\aac|\7\2//\62;C\\aac|\4\2\13\13\"\"\2\u014f\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2"+
		"\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2"+
		"\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K"+
		"\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2"+
		"\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2"+
		"\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\3m\3\2\2\2\5p\3\2\2\2\7v"+
		"\3\2\2\2\ty\3\2\2\2\13\u0083\3\2\2\2\r\u0089\3\2\2\2\17\u008c\3\2\2\2"+
		"\21\u0091\3\2\2\2\23\u0095\3\2\2\2\25\u0098\3\2\2\2\27\u00a2\3\2\2\2\31"+
		"\u00a4\3\2\2\2\33\u00a6\3\2\2\2\35\u00a8\3\2\2\2\37\u00aa\3\2\2\2!\u00ac"+
		"\3\2\2\2#\u00af\3\2\2\2%\u00b2\3\2\2\2\'\u00b5\3\2\2\2)\u00b8\3\2\2\2"+
		"+\u00bb\3\2\2\2-\u00bf\3\2\2\2/\u00c1\3\2\2\2\61\u00c3\3\2\2\2\63\u00c5"+
		"\3\2\2\2\65\u00c7\3\2\2\2\67\u00c9\3\2\2\29\u00cc\3\2\2\2;\u00d1\3\2\2"+
		"\2=\u00d7\3\2\2\2?\u00db\3\2\2\2A\u00de\3\2\2\2C\u00e1\3\2\2\2E\u00e3"+
		"\3\2\2\2G\u00e5\3\2\2\2I\u00e8\3\2\2\2K\u00f0\3\2\2\2M\u00f6\3\2\2\2O"+
		"\u00fc\3\2\2\2Q\u00fe\3\2\2\2S\u0100\3\2\2\2U\u0102\3\2\2\2W\u0104\3\2"+
		"\2\2Y\u0106\3\2\2\2[\u0108\3\2\2\2]\u010a\3\2\2\2_\u0110\3\2\2\2a\u0117"+
		"\3\2\2\2c\u0120\3\2\2\2e\u0127\3\2\2\2g\u0130\3\2\2\2i\u0135\3\2\2\2k"+
		"\u013b\3\2\2\2mn\7v\2\2no\7q\2\2o\4\3\2\2\2pq\7w\2\2qr\7p\2\2rs\7v\2\2"+
		"st\7k\2\2tu\7n\2\2u\6\3\2\2\2vw\7^\2\2wx\7$\2\2x\b\3\2\2\2y~\7$\2\2z}"+
		"\5\7\4\2{}\n\2\2\2|z\3\2\2\2|{\3\2\2\2}\u0080\3\2\2\2~\177\3\2\2\2~|\3"+
		"\2\2\2\177\u0081\3\2\2\2\u0080~\3\2\2\2\u0081\u0082\7$\2\2\u0082\n\3\2"+
		"\2\2\u0083\u0084\7y\2\2\u0084\u0085\7j\2\2\u0085\u0086\7k\2\2\u0086\u0087"+
		"\7n\2\2\u0087\u0088\7g\2\2\u0088\f\3\2\2\2\u0089\u008a\7k\2\2\u008a\u008b"+
		"\7h\2\2\u008b\16\3\2\2\2\u008c\u008d\7g\2\2\u008d\u008e\7n\2\2\u008e\u008f"+
		"\7u\2\2\u008f\u0090\7g\2\2\u0090\20\3\2\2\2\u0091\u0092\7h\2\2\u0092\u0093"+
		"\7q\2\2\u0093\u0094\7t\2\2\u0094\22\3\2\2\2\u0095\u0096\7k\2\2\u0096\u0097"+
		"\7p\2\2\u0097\24\3\2\2\2\u0098\u0099\7g\2\2\u0099\u009a\7z\2\2\u009a\u009b"+
		"\7e\2\2\u009b\u009c\7n\2\2\u009c\u009d\7w\2\2\u009d\u009e\7u\2\2\u009e"+
		"\u009f\7k\2\2\u009f\u00a0\7x\2\2\u00a0\u00a1\7g\2\2\u00a1\26\3\2\2\2\u00a2"+
		"\u00a3\7B\2\2\u00a3\30\3\2\2\2\u00a4\u00a5\7\60\2\2\u00a5\32\3\2\2\2\u00a6"+
		"\u00a7\7.\2\2\u00a7\34\3\2\2\2\u00a8\u00a9\7=\2\2\u00a9\36\3\2\2\2\u00aa"+
		"\u00ab\7?\2\2\u00ab \3\2\2\2\u00ac\u00ad\7-\2\2\u00ad\u00ae\7?\2\2\u00ae"+
		"\"\3\2\2\2\u00af\u00b0\7/\2\2\u00b0\u00b1\7?\2\2\u00b1$\3\2\2\2\u00b2"+
		"\u00b3\7,\2\2\u00b3\u00b4\7?\2\2\u00b4&\3\2\2\2\u00b5\u00b6\7\61\2\2\u00b6"+
		"\u00b7\7?\2\2\u00b7(\3\2\2\2\u00b8\u00b9\7\'\2\2\u00b9\u00ba\7?\2\2\u00ba"+
		"*\3\2\2\2\u00bb\u00bc\7,\2\2\u00bc\u00bd\7,\2\2\u00bd\u00be\7?\2\2\u00be"+
		",\3\2\2\2\u00bf\u00c0\7-\2\2\u00c0.\3\2\2\2\u00c1\u00c2\7/\2\2\u00c2\60"+
		"\3\2\2\2\u00c3\u00c4\7,\2\2\u00c4\62\3\2\2\2\u00c5\u00c6\7\61\2\2\u00c6"+
		"\64\3\2\2\2\u00c7\u00c8\7\'\2\2\u00c8\66\3\2\2\2\u00c9\u00ca\7,\2\2\u00ca"+
		"\u00cb\7,\2\2\u00cb8\3\2\2\2\u00cc\u00cd\7v\2\2\u00cd\u00ce\7t\2\2\u00ce"+
		"\u00cf\7w\2\2\u00cf\u00d0\7g\2\2\u00d0:\3\2\2\2\u00d1\u00d2\7h\2\2\u00d2"+
		"\u00d3\7c\2\2\u00d3\u00d4\7n\2\2\u00d4\u00d5\7u\2\2\u00d5\u00d6\7g\2\2"+
		"\u00d6<\3\2\2\2\u00d7\u00d8\7?\2\2\u00d8\u00d9\7?\2\2\u00d9\u00da\7?\2"+
		"\2\u00da>\3\2\2\2\u00db\u00dc\7?\2\2\u00dc\u00dd\7?\2\2\u00dd@\3\2\2\2"+
		"\u00de\u00df\7#\2\2\u00df\u00e0\7?\2\2\u00e0B\3\2\2\2\u00e1\u00e2\7@\2"+
		"\2\u00e2D\3\2\2\2\u00e3\u00e4\7>\2\2\u00e4F\3\2\2\2\u00e5\u00e6\7>\2\2"+
		"\u00e6\u00e7\7?\2\2\u00e7H\3\2\2\2\u00e8\u00e9\7@\2\2\u00e9\u00ea\7?\2"+
		"\2\u00eaJ\3\2\2\2\u00eb\u00ec\7c\2\2\u00ec\u00ed\7p\2\2\u00ed\u00f1\7"+
		"f\2\2\u00ee\u00ef\7(\2\2\u00ef\u00f1\7(\2\2\u00f0\u00eb\3\2\2\2\u00f0"+
		"\u00ee\3\2\2\2\u00f1L\3\2\2\2\u00f2\u00f3\7q\2\2\u00f3\u00f7\7t\2\2\u00f4"+
		"\u00f5\7~\2\2\u00f5\u00f7\7~\2\2\u00f6\u00f2\3\2\2\2\u00f6\u00f4\3\2\2"+
		"\2\u00f7N\3\2\2\2\u00f8\u00f9\7p\2\2\u00f9\u00fa\7q\2\2\u00fa\u00fd\7"+
		"v\2\2\u00fb\u00fd\7#\2\2\u00fc\u00f8\3\2\2\2\u00fc\u00fb\3\2\2\2\u00fd"+
		"P\3\2\2\2\u00fe\u00ff\7*\2\2\u00ffR\3\2\2\2\u0100\u0101\7+\2\2\u0101T"+
		"\3\2\2\2\u0102\u0103\7]\2\2\u0103V\3\2\2\2\u0104\u0105\7_\2\2\u0105X\3"+
		"\2\2\2\u0106\u0107\7}\2\2\u0107Z\3\2\2\2\u0108\u0109\7\177\2\2\u0109\\"+
		"\3\2\2\2\u010a\u010b\7p\2\2\u010b\u010c\7w\2\2\u010c\u010d\7n\2\2\u010d"+
		"\u010e\7n\2\2\u010e^\3\2\2\2\u010f\u0111\t\3\2\2\u0110\u010f\3\2\2\2\u0111"+
		"\u0112\3\2\2\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113`\3\2\2\2"+
		"\u0114\u0116\t\3\2\2\u0115\u0114\3\2\2\2\u0116\u0119\3\2\2\2\u0117\u0115"+
		"\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u011a\3\2\2\2\u0119\u0117\3\2\2\2\u011a"+
		"\u011c\7\60\2\2\u011b\u011d\t\3\2\2\u011c\u011b\3\2\2\2\u011d\u011e\3"+
		"\2\2\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011fb\3\2\2\2\u0120\u0124"+
		"\t\4\2\2\u0121\u0123\t\5\2\2\u0122\u0121\3\2\2\2\u0123\u0126\3\2\2\2\u0124"+
		"\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125d\3\2\2\2\u0126\u0124\3\2\2\2"+
		"\u0127\u0128\5\27\f\2\u0128\u012c\t\4\2\2\u0129\u012b\t\6\2\2\u012a\u0129"+
		"\3\2\2\2\u012b\u012e\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d"+
		"f\3\2\2\2\u012e\u012c\3\2\2\2\u012f\u0131\7\17\2\2\u0130\u012f\3\2\2\2"+
		"\u0130\u0131\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0133\7\f\2\2\u0133h\3"+
		"\2\2\2\u0134\u0136\t\7\2\2\u0135\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137"+
		"\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0139\3\2\2\2\u0139\u013a\b\65"+
		"\2\2\u013aj\3\2\2\2\u013b\u013c\7\61\2\2\u013c\u013d\7\61\2\2\u013d\u0141"+
		"\3\2\2\2\u013e\u0140\n\2\2\2\u013f\u013e\3\2\2\2\u0140\u0143\3\2\2\2\u0141"+
		"\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142l\3\2\2\2\u0143\u0141\3\2\2\2"+
		"\20\2|~\u00f0\u00f6\u00fc\u0112\u0117\u011e\u0124\u012c\u0130\u0137\u0141"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}