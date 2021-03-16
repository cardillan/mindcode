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
		LITERAL=1, WHILE=2, IF=3, ELSE=4, AT=5, DOT=6, COMMA=7, SEMICOLON=8, ASSIGN=9, 
		PLUS_ASSIGN=10, MINUS_ASSIGN=11, MUL_ASSIGN=12, DIV_ASSIGN=13, MOD_ASSIGN=14, 
		EXP_ASSIGN=15, PLUS=16, MINUS=17, MUL=18, DIV=19, MOD=20, EXP=21, TRUE=22, 
		FALSE=23, STRICT_EQUAL=24, EQUAL=25, NOT_EQUAL=26, GREATER=27, LESS=28, 
		LESS_EQUAL=29, GREATER_EQUAL=30, AND=31, OR=32, NOT=33, LEFT_RBRACKET=34, 
		RIGHT_RBRACKET=35, LEFT_SBRACKET=36, RIGHT_SBRACKET=37, LEFT_CBRACKET=38, 
		RIGHT_CBRACKET=39, NULL=40, INT=41, FLOAT=42, ID=43, REF=44, CRLF=45, 
		WS=46, SL_COMMENT=47;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ESCAPED_QUOTE", "LITERAL", "WHILE", "IF", "ELSE", "AT", "DOT", "COMMA", 
			"SEMICOLON", "ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
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
			null, null, "'while'", "'if'", "'else'", "'@'", "'.'", "','", "';'", 
			"'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'**='", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'**'", "'true'", "'false'", "'==='", "'=='", "'!='", 
			"'>'", "'<'", "'<='", "'>='", null, null, null, "'('", "')'", "'['", 
			"']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LITERAL", "WHILE", "IF", "ELSE", "AT", "DOT", "COMMA", "SEMICOLON", 
			"ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"MOD_ASSIGN", "EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", 
			"TRUE", "FALSE", "STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", 
			"LESS_EQUAL", "GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\61\u0120\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\3\2\3\2\3\2\3\3\3\3\3\3"+
		"\7\3j\n\3\f\3\16\3m\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27"+
		"\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32"+
		"\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37"+
		"\3\37\3 \3 \3 \3!\3!\3!\3!\3!\5!\u00cd\n!\3\"\3\"\3\"\3\"\5\"\u00d3\n"+
		"\"\3#\3#\3#\3#\5#\u00d9\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3"+
		"*\3*\3*\3*\3+\6+\u00ed\n+\r+\16+\u00ee\3,\7,\u00f2\n,\f,\16,\u00f5\13"+
		",\3,\3,\6,\u00f9\n,\r,\16,\u00fa\3-\3-\7-\u00ff\n-\f-\16-\u0102\13-\3"+
		".\3.\3.\7.\u0107\n.\f.\16.\u010a\13.\3/\5/\u010d\n/\3/\3/\3\60\6\60\u0112"+
		"\n\60\r\60\16\60\u0113\3\60\3\60\3\61\3\61\3\61\3\61\7\61\u011c\n\61\f"+
		"\61\16\61\u011f\13\61\3k\2\62\3\2\5\3\7\4\t\5\13\6\r\7\17\b\21\t\23\n"+
		"\25\13\27\f\31\r\33\16\35\17\37\20!\21#\22%\23\'\24)\25+\26-\27/\30\61"+
		"\31\63\32\65\33\67\349\35;\36=\37? A!C\"E#G$I%K&M\'O(Q)S*U+W,Y-[.]/_\60"+
		"a\61\3\2\b\4\2\f\f\17\17\3\2\62;\5\2C\\aac|\6\2\62;C\\aac|\7\2//\62;C"+
		"\\aac|\4\2\13\13\"\"\2\u012b\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E"+
		"\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\3c\3\2\2\2\5f\3\2\2\2\7p\3\2\2\2\tv\3\2\2\2\13"+
		"y\3\2\2\2\r~\3\2\2\2\17\u0080\3\2\2\2\21\u0082\3\2\2\2\23\u0084\3\2\2"+
		"\2\25\u0086\3\2\2\2\27\u0088\3\2\2\2\31\u008b\3\2\2\2\33\u008e\3\2\2\2"+
		"\35\u0091\3\2\2\2\37\u0094\3\2\2\2!\u0097\3\2\2\2#\u009b\3\2\2\2%\u009d"+
		"\3\2\2\2\'\u009f\3\2\2\2)\u00a1\3\2\2\2+\u00a3\3\2\2\2-\u00a5\3\2\2\2"+
		"/\u00a8\3\2\2\2\61\u00ad\3\2\2\2\63\u00b3\3\2\2\2\65\u00b7\3\2\2\2\67"+
		"\u00ba\3\2\2\29\u00bd\3\2\2\2;\u00bf\3\2\2\2=\u00c1\3\2\2\2?\u00c4\3\2"+
		"\2\2A\u00cc\3\2\2\2C\u00d2\3\2\2\2E\u00d8\3\2\2\2G\u00da\3\2\2\2I\u00dc"+
		"\3\2\2\2K\u00de\3\2\2\2M\u00e0\3\2\2\2O\u00e2\3\2\2\2Q\u00e4\3\2\2\2S"+
		"\u00e6\3\2\2\2U\u00ec\3\2\2\2W\u00f3\3\2\2\2Y\u00fc\3\2\2\2[\u0103\3\2"+
		"\2\2]\u010c\3\2\2\2_\u0111\3\2\2\2a\u0117\3\2\2\2cd\7^\2\2de\7$\2\2e\4"+
		"\3\2\2\2fk\7$\2\2gj\5\3\2\2hj\n\2\2\2ig\3\2\2\2ih\3\2\2\2jm\3\2\2\2kl"+
		"\3\2\2\2ki\3\2\2\2ln\3\2\2\2mk\3\2\2\2no\7$\2\2o\6\3\2\2\2pq\7y\2\2qr"+
		"\7j\2\2rs\7k\2\2st\7n\2\2tu\7g\2\2u\b\3\2\2\2vw\7k\2\2wx\7h\2\2x\n\3\2"+
		"\2\2yz\7g\2\2z{\7n\2\2{|\7u\2\2|}\7g\2\2}\f\3\2\2\2~\177\7B\2\2\177\16"+
		"\3\2\2\2\u0080\u0081\7\60\2\2\u0081\20\3\2\2\2\u0082\u0083\7.\2\2\u0083"+
		"\22\3\2\2\2\u0084\u0085\7=\2\2\u0085\24\3\2\2\2\u0086\u0087\7?\2\2\u0087"+
		"\26\3\2\2\2\u0088\u0089\7-\2\2\u0089\u008a\7?\2\2\u008a\30\3\2\2\2\u008b"+
		"\u008c\7/\2\2\u008c\u008d\7?\2\2\u008d\32\3\2\2\2\u008e\u008f\7,\2\2\u008f"+
		"\u0090\7?\2\2\u0090\34\3\2\2\2\u0091\u0092\7\61\2\2\u0092\u0093\7?\2\2"+
		"\u0093\36\3\2\2\2\u0094\u0095\7\'\2\2\u0095\u0096\7?\2\2\u0096 \3\2\2"+
		"\2\u0097\u0098\7,\2\2\u0098\u0099\7,\2\2\u0099\u009a\7?\2\2\u009a\"\3"+
		"\2\2\2\u009b\u009c\7-\2\2\u009c$\3\2\2\2\u009d\u009e\7/\2\2\u009e&\3\2"+
		"\2\2\u009f\u00a0\7,\2\2\u00a0(\3\2\2\2\u00a1\u00a2\7\61\2\2\u00a2*\3\2"+
		"\2\2\u00a3\u00a4\7\'\2\2\u00a4,\3\2\2\2\u00a5\u00a6\7,\2\2\u00a6\u00a7"+
		"\7,\2\2\u00a7.\3\2\2\2\u00a8\u00a9\7v\2\2\u00a9\u00aa\7t\2\2\u00aa\u00ab"+
		"\7w\2\2\u00ab\u00ac\7g\2\2\u00ac\60\3\2\2\2\u00ad\u00ae\7h\2\2\u00ae\u00af"+
		"\7c\2\2\u00af\u00b0\7n\2\2\u00b0\u00b1\7u\2\2\u00b1\u00b2\7g\2\2\u00b2"+
		"\62\3\2\2\2\u00b3\u00b4\7?\2\2\u00b4\u00b5\7?\2\2\u00b5\u00b6\7?\2\2\u00b6"+
		"\64\3\2\2\2\u00b7\u00b8\7?\2\2\u00b8\u00b9\7?\2\2\u00b9\66\3\2\2\2\u00ba"+
		"\u00bb\7#\2\2\u00bb\u00bc\7?\2\2\u00bc8\3\2\2\2\u00bd\u00be\7@\2\2\u00be"+
		":\3\2\2\2\u00bf\u00c0\7>\2\2\u00c0<\3\2\2\2\u00c1\u00c2\7>\2\2\u00c2\u00c3"+
		"\7?\2\2\u00c3>\3\2\2\2\u00c4\u00c5\7@\2\2\u00c5\u00c6\7?\2\2\u00c6@\3"+
		"\2\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7p\2\2\u00c9\u00cd\7f\2\2\u00ca"+
		"\u00cb\7(\2\2\u00cb\u00cd\7(\2\2\u00cc\u00c7\3\2\2\2\u00cc\u00ca\3\2\2"+
		"\2\u00cdB\3\2\2\2\u00ce\u00cf\7q\2\2\u00cf\u00d3\7t\2\2\u00d0\u00d1\7"+
		"~\2\2\u00d1\u00d3\7~\2\2\u00d2\u00ce\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3"+
		"D\3\2\2\2\u00d4\u00d5\7p\2\2\u00d5\u00d6\7q\2\2\u00d6\u00d9\7v\2\2\u00d7"+
		"\u00d9\7#\2\2\u00d8\u00d4\3\2\2\2\u00d8\u00d7\3\2\2\2\u00d9F\3\2\2\2\u00da"+
		"\u00db\7*\2\2\u00dbH\3\2\2\2\u00dc\u00dd\7+\2\2\u00ddJ\3\2\2\2\u00de\u00df"+
		"\7]\2\2\u00dfL\3\2\2\2\u00e0\u00e1\7_\2\2\u00e1N\3\2\2\2\u00e2\u00e3\7"+
		"}\2\2\u00e3P\3\2\2\2\u00e4\u00e5\7\177\2\2\u00e5R\3\2\2\2\u00e6\u00e7"+
		"\7p\2\2\u00e7\u00e8\7w\2\2\u00e8\u00e9\7n\2\2\u00e9\u00ea\7n\2\2\u00ea"+
		"T\3\2\2\2\u00eb\u00ed\t\3\2\2\u00ec\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2"+
		"\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00efV\3\2\2\2\u00f0\u00f2\t"+
		"\3\2\2\u00f1\u00f0\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3"+
		"\u00f4\3\2\2\2\u00f4\u00f6\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6\u00f8\7\60"+
		"\2\2\u00f7\u00f9\t\3\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa"+
		"\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fbX\3\2\2\2\u00fc\u0100\t\4\2\2"+
		"\u00fd\u00ff\t\5\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0102\3\2\2\2\u0100\u00fe"+
		"\3\2\2\2\u0100\u0101\3\2\2\2\u0101Z\3\2\2\2\u0102\u0100\3\2\2\2\u0103"+
		"\u0104\5\r\7\2\u0104\u0108\t\4\2\2\u0105\u0107\t\6\2\2\u0106\u0105\3\2"+
		"\2\2\u0107\u010a\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109"+
		"\\\3\2\2\2\u010a\u0108\3\2\2\2\u010b\u010d\7\17\2\2\u010c\u010b\3\2\2"+
		"\2\u010c\u010d\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u010f\7\f\2\2\u010f^"+
		"\3\2\2\2\u0110\u0112\t\7\2\2\u0111\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113"+
		"\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116\b\60"+
		"\2\2\u0116`\3\2\2\2\u0117\u0118\7\61\2\2\u0118\u0119\7\61\2\2\u0119\u011d"+
		"\3\2\2\2\u011a\u011c\n\2\2\2\u011b\u011a\3\2\2\2\u011c\u011f\3\2\2\2\u011d"+
		"\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011eb\3\2\2\2\u011f\u011d\3\2\2\2"+
		"\20\2ik\u00cc\u00d2\u00d8\u00ee\u00f3\u00fa\u0100\u0108\u010c\u0113\u011d"+
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