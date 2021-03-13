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
		LITERAL=1, WHILE=2, IF=3, ELSE=4, DOT=5, COMMA=6, SEMICOLON=7, ASSIGN=8, 
		PLUS_ASSIGN=9, MINUS_ASSIGN=10, MUL_ASSIGN=11, DIV_ASSIGN=12, MOD_ASSIGN=13, 
		EXP_ASSIGN=14, PLUS=15, MINUS=16, MUL=17, DIV=18, MOD=19, EXP=20, COPPER=21, 
		LEAD=22, METAGLASS=23, GRAPHITE=24, SAND=25, COAL=26, TITANIUM=27, SCRAP=28, 
		SILICON=29, PYRATITE=30, WATER=31, SLAG=32, CRYOFLUID=33, TRUE=34, FALSE=35, 
		STRICT_EQUAL=36, EQUAL=37, NOT_EQUAL=38, GREATER=39, LESS=40, LESS_EQUAL=41, 
		GREATER_EQUAL=42, AND=43, OR=44, NOT=45, LEFT_RBRACKET=46, RIGHT_RBRACKET=47, 
		LEFT_SBRACKET=48, RIGHT_SBRACKET=49, LEFT_CBRACKET=50, RIGHT_CBRACKET=51, 
		NULL=52, INT=53, FLOAT=54, ID=55, CRLF=56, WS=57;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ESCAPED_QUOTE", "LITERAL", "WHILE", "IF", "ELSE", "DOT", "COMMA", "SEMICOLON", 
			"ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"MOD_ASSIGN", "EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", 
			"COPPER", "LEAD", "METAGLASS", "GRAPHITE", "SAND", "COAL", "TITANIUM", 
			"SCRAP", "SILICON", "PYRATITE", "WATER", "SLAG", "CRYOFLUID", "TRUE", 
			"FALSE", "STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", 
			"GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"NULL", "INT", "FLOAT", "ID", "CRLF", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'while'", "'if'", "'else'", "'.'", "','", "';'", "'='", 
			"'+='", "'-='", "'*='", "'/='", "'%='", "'**='", "'+'", "'-'", "'*'", 
			"'/'", "'%'", "'**'", "'copper'", "'lead'", "'metaglass'", "'graphite'", 
			"'sand'", "'coal'", "'titanium'", "'scrap'", "'silicon'", "'pyratite'", 
			"'water'", "'slag'", "'cryofluid'", "'true'", "'false'", "'==='", "'=='", 
			"'!='", "'>'", "'<'", "'<='", "'>='", null, null, null, "'('", "')'", 
			"'['", "']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LITERAL", "WHILE", "IF", "ELSE", "DOT", "COMMA", "SEMICOLON", 
			"ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"MOD_ASSIGN", "EXP_ASSIGN", "PLUS", "MINUS", "MUL", "DIV", "MOD", "EXP", 
			"COPPER", "LEAD", "METAGLASS", "GRAPHITE", "SAND", "COAL", "TITANIUM", 
			"SCRAP", "SILICON", "PYRATITE", "WATER", "SLAG", "CRYOFLUID", "TRUE", 
			"FALSE", "STRICT_EQUAL", "EQUAL", "NOT_EQUAL", "GREATER", "LESS", "LESS_EQUAL", 
			"GREATER_EQUAL", "AND", "OR", "NOT", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"NULL", "INT", "FLOAT", "ID", "CRLF", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2;\u017f\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\3\2\3\2\3\2"+
		"\3\3\3\3\3\3\7\3~\n\3\f\3\16\3\u0081\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3"+
		"\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3"+
		"\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3"+
		"\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3"+
		"\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3"+
		"\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3"+
		"\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3"+
		" \3 \3 \3 \3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#"+
		"\3#\3#\3#\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\3\'\3("+
		"\3(\3(\3)\3)\3*\3*\3+\3+\3+\3,\3,\3,\3-\3-\3-\3-\3-\5-\u013d\n-\3.\3."+
		"\3.\3.\5.\u0143\n.\3/\3/\3/\3/\5/\u0149\n/\3\60\3\60\3\61\3\61\3\62\3"+
		"\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\67\6\67\u015d"+
		"\n\67\r\67\16\67\u015e\38\78\u0162\n8\f8\168\u0165\138\38\38\68\u0169"+
		"\n8\r8\168\u016a\39\39\79\u016f\n9\f9\169\u0172\139\3:\5:\u0175\n:\3:"+
		"\3:\3;\6;\u017a\n;\r;\16;\u017b\3;\3;\3\177\2<\3\2\5\3\7\4\t\5\13\6\r"+
		"\7\17\b\21\t\23\n\25\13\27\f\31\r\33\16\35\17\37\20!\21#\22%\23\'\24)"+
		"\25+\26-\27/\30\61\31\63\32\65\33\67\349\35;\36=\37? A!C\"E#G$I%K&M\'"+
		"O(Q)S*U+W,Y-[.]/_\60a\61c\62e\63g\64i\65k\66m\67o8q9s:u;\3\2\7\4\2\f\f"+
		"\17\17\3\2\62;\5\2C\\aac|\6\2\62;C\\aac|\4\2\13\13\"\"\2\u0188\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3"+
		"\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2"+
		"\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2"+
		"Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3"+
		"\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2"+
		"\2\2s\3\2\2\2\2u\3\2\2\2\3w\3\2\2\2\5z\3\2\2\2\7\u0084\3\2\2\2\t\u008a"+
		"\3\2\2\2\13\u008d\3\2\2\2\r\u0092\3\2\2\2\17\u0094\3\2\2\2\21\u0096\3"+
		"\2\2\2\23\u0098\3\2\2\2\25\u009a\3\2\2\2\27\u009d\3\2\2\2\31\u00a0\3\2"+
		"\2\2\33\u00a3\3\2\2\2\35\u00a6\3\2\2\2\37\u00a9\3\2\2\2!\u00ad\3\2\2\2"+
		"#\u00af\3\2\2\2%\u00b1\3\2\2\2\'\u00b3\3\2\2\2)\u00b5\3\2\2\2+\u00b7\3"+
		"\2\2\2-\u00ba\3\2\2\2/\u00c1\3\2\2\2\61\u00c6\3\2\2\2\63\u00d0\3\2\2\2"+
		"\65\u00d9\3\2\2\2\67\u00de\3\2\2\29\u00e3\3\2\2\2;\u00ec\3\2\2\2=\u00f2"+
		"\3\2\2\2?\u00fa\3\2\2\2A\u0103\3\2\2\2C\u0109\3\2\2\2E\u010e\3\2\2\2G"+
		"\u0118\3\2\2\2I\u011d\3\2\2\2K\u0123\3\2\2\2M\u0127\3\2\2\2O\u012a\3\2"+
		"\2\2Q\u012d\3\2\2\2S\u012f\3\2\2\2U\u0131\3\2\2\2W\u0134\3\2\2\2Y\u013c"+
		"\3\2\2\2[\u0142\3\2\2\2]\u0148\3\2\2\2_\u014a\3\2\2\2a\u014c\3\2\2\2c"+
		"\u014e\3\2\2\2e\u0150\3\2\2\2g\u0152\3\2\2\2i\u0154\3\2\2\2k\u0156\3\2"+
		"\2\2m\u015c\3\2\2\2o\u0163\3\2\2\2q\u016c\3\2\2\2s\u0174\3\2\2\2u\u0179"+
		"\3\2\2\2wx\7^\2\2xy\7$\2\2y\4\3\2\2\2z\177\7$\2\2{~\5\3\2\2|~\n\2\2\2"+
		"}{\3\2\2\2}|\3\2\2\2~\u0081\3\2\2\2\177\u0080\3\2\2\2\177}\3\2\2\2\u0080"+
		"\u0082\3\2\2\2\u0081\177\3\2\2\2\u0082\u0083\7$\2\2\u0083\6\3\2\2\2\u0084"+
		"\u0085\7y\2\2\u0085\u0086\7j\2\2\u0086\u0087\7k\2\2\u0087\u0088\7n\2\2"+
		"\u0088\u0089\7g\2\2\u0089\b\3\2\2\2\u008a\u008b\7k\2\2\u008b\u008c\7h"+
		"\2\2\u008c\n\3\2\2\2\u008d\u008e\7g\2\2\u008e\u008f\7n\2\2\u008f\u0090"+
		"\7u\2\2\u0090\u0091\7g\2\2\u0091\f\3\2\2\2\u0092\u0093\7\60\2\2\u0093"+
		"\16\3\2\2\2\u0094\u0095\7.\2\2\u0095\20\3\2\2\2\u0096\u0097\7=\2\2\u0097"+
		"\22\3\2\2\2\u0098\u0099\7?\2\2\u0099\24\3\2\2\2\u009a\u009b\7-\2\2\u009b"+
		"\u009c\7?\2\2\u009c\26\3\2\2\2\u009d\u009e\7/\2\2\u009e\u009f\7?\2\2\u009f"+
		"\30\3\2\2\2\u00a0\u00a1\7,\2\2\u00a1\u00a2\7?\2\2\u00a2\32\3\2\2\2\u00a3"+
		"\u00a4\7\61\2\2\u00a4\u00a5\7?\2\2\u00a5\34\3\2\2\2\u00a6\u00a7\7\'\2"+
		"\2\u00a7\u00a8\7?\2\2\u00a8\36\3\2\2\2\u00a9\u00aa\7,\2\2\u00aa\u00ab"+
		"\7,\2\2\u00ab\u00ac\7?\2\2\u00ac \3\2\2\2\u00ad\u00ae\7-\2\2\u00ae\"\3"+
		"\2\2\2\u00af\u00b0\7/\2\2\u00b0$\3\2\2\2\u00b1\u00b2\7,\2\2\u00b2&\3\2"+
		"\2\2\u00b3\u00b4\7\61\2\2\u00b4(\3\2\2\2\u00b5\u00b6\7\'\2\2\u00b6*\3"+
		"\2\2\2\u00b7\u00b8\7,\2\2\u00b8\u00b9\7,\2\2\u00b9,\3\2\2\2\u00ba\u00bb"+
		"\7e\2\2\u00bb\u00bc\7q\2\2\u00bc\u00bd\7r\2\2\u00bd\u00be\7r\2\2\u00be"+
		"\u00bf\7g\2\2\u00bf\u00c0\7t\2\2\u00c0.\3\2\2\2\u00c1\u00c2\7n\2\2\u00c2"+
		"\u00c3\7g\2\2\u00c3\u00c4\7c\2\2\u00c4\u00c5\7f\2\2\u00c5\60\3\2\2\2\u00c6"+
		"\u00c7\7o\2\2\u00c7\u00c8\7g\2\2\u00c8\u00c9\7v\2\2\u00c9\u00ca\7c\2\2"+
		"\u00ca\u00cb\7i\2\2\u00cb\u00cc\7n\2\2\u00cc\u00cd\7c\2\2\u00cd\u00ce"+
		"\7u\2\2\u00ce\u00cf\7u\2\2\u00cf\62\3\2\2\2\u00d0\u00d1\7i\2\2\u00d1\u00d2"+
		"\7t\2\2\u00d2\u00d3\7c\2\2\u00d3\u00d4\7r\2\2\u00d4\u00d5\7j\2\2\u00d5"+
		"\u00d6\7k\2\2\u00d6\u00d7\7v\2\2\u00d7\u00d8\7g\2\2\u00d8\64\3\2\2\2\u00d9"+
		"\u00da\7u\2\2\u00da\u00db\7c\2\2\u00db\u00dc\7p\2\2\u00dc\u00dd\7f\2\2"+
		"\u00dd\66\3\2\2\2\u00de\u00df\7e\2\2\u00df\u00e0\7q\2\2\u00e0\u00e1\7"+
		"c\2\2\u00e1\u00e2\7n\2\2\u00e28\3\2\2\2\u00e3\u00e4\7v\2\2\u00e4\u00e5"+
		"\7k\2\2\u00e5\u00e6\7v\2\2\u00e6\u00e7\7c\2\2\u00e7\u00e8\7p\2\2\u00e8"+
		"\u00e9\7k\2\2\u00e9\u00ea\7w\2\2\u00ea\u00eb\7o\2\2\u00eb:\3\2\2\2\u00ec"+
		"\u00ed\7u\2\2\u00ed\u00ee\7e\2\2\u00ee\u00ef\7t\2\2\u00ef\u00f0\7c\2\2"+
		"\u00f0\u00f1\7r\2\2\u00f1<\3\2\2\2\u00f2\u00f3\7u\2\2\u00f3\u00f4\7k\2"+
		"\2\u00f4\u00f5\7n\2\2\u00f5\u00f6\7k\2\2\u00f6\u00f7\7e\2\2\u00f7\u00f8"+
		"\7q\2\2\u00f8\u00f9\7p\2\2\u00f9>\3\2\2\2\u00fa\u00fb\7r\2\2\u00fb\u00fc"+
		"\7{\2\2\u00fc\u00fd\7t\2\2\u00fd\u00fe\7c\2\2\u00fe\u00ff\7v\2\2\u00ff"+
		"\u0100\7k\2\2\u0100\u0101\7v\2\2\u0101\u0102\7g\2\2\u0102@\3\2\2\2\u0103"+
		"\u0104\7y\2\2\u0104\u0105\7c\2\2\u0105\u0106\7v\2\2\u0106\u0107\7g\2\2"+
		"\u0107\u0108\7t\2\2\u0108B\3\2\2\2\u0109\u010a\7u\2\2\u010a\u010b\7n\2"+
		"\2\u010b\u010c\7c\2\2\u010c\u010d\7i\2\2\u010dD\3\2\2\2\u010e\u010f\7"+
		"e\2\2\u010f\u0110\7t\2\2\u0110\u0111\7{\2\2\u0111\u0112\7q\2\2\u0112\u0113"+
		"\7h\2\2\u0113\u0114\7n\2\2\u0114\u0115\7w\2\2\u0115\u0116\7k\2\2\u0116"+
		"\u0117\7f\2\2\u0117F\3\2\2\2\u0118\u0119\7v\2\2\u0119\u011a\7t\2\2\u011a"+
		"\u011b\7w\2\2\u011b\u011c\7g\2\2\u011cH\3\2\2\2\u011d\u011e\7h\2\2\u011e"+
		"\u011f\7c\2\2\u011f\u0120\7n\2\2\u0120\u0121\7u\2\2\u0121\u0122\7g\2\2"+
		"\u0122J\3\2\2\2\u0123\u0124\7?\2\2\u0124\u0125\7?\2\2\u0125\u0126\7?\2"+
		"\2\u0126L\3\2\2\2\u0127\u0128\7?\2\2\u0128\u0129\7?\2\2\u0129N\3\2\2\2"+
		"\u012a\u012b\7#\2\2\u012b\u012c\7?\2\2\u012cP\3\2\2\2\u012d\u012e\7@\2"+
		"\2\u012eR\3\2\2\2\u012f\u0130\7>\2\2\u0130T\3\2\2\2\u0131\u0132\7>\2\2"+
		"\u0132\u0133\7?\2\2\u0133V\3\2\2\2\u0134\u0135\7@\2\2\u0135\u0136\7?\2"+
		"\2\u0136X\3\2\2\2\u0137\u0138\7c\2\2\u0138\u0139\7p\2\2\u0139\u013d\7"+
		"f\2\2\u013a\u013b\7(\2\2\u013b\u013d\7(\2\2\u013c\u0137\3\2\2\2\u013c"+
		"\u013a\3\2\2\2\u013dZ\3\2\2\2\u013e\u013f\7q\2\2\u013f\u0143\7t\2\2\u0140"+
		"\u0141\7~\2\2\u0141\u0143\7~\2\2\u0142\u013e\3\2\2\2\u0142\u0140\3\2\2"+
		"\2\u0143\\\3\2\2\2\u0144\u0145\7p\2\2\u0145\u0146\7q\2\2\u0146\u0149\7"+
		"v\2\2\u0147\u0149\7#\2\2\u0148\u0144\3\2\2\2\u0148\u0147\3\2\2\2\u0149"+
		"^\3\2\2\2\u014a\u014b\7*\2\2\u014b`\3\2\2\2\u014c\u014d\7+\2\2\u014db"+
		"\3\2\2\2\u014e\u014f\7]\2\2\u014fd\3\2\2\2\u0150\u0151\7_\2\2\u0151f\3"+
		"\2\2\2\u0152\u0153\7}\2\2\u0153h\3\2\2\2\u0154\u0155\7\177\2\2\u0155j"+
		"\3\2\2\2\u0156\u0157\7p\2\2\u0157\u0158\7w\2\2\u0158\u0159\7n\2\2\u0159"+
		"\u015a\7n\2\2\u015al\3\2\2\2\u015b\u015d\t\3\2\2\u015c\u015b\3\2\2\2\u015d"+
		"\u015e\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015fn\3\2\2\2"+
		"\u0160\u0162\t\3\2\2\u0161\u0160\3\2\2\2\u0162\u0165\3\2\2\2\u0163\u0161"+
		"\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0166\3\2\2\2\u0165\u0163\3\2\2\2\u0166"+
		"\u0168\7\60\2\2\u0167\u0169\t\3\2\2\u0168\u0167\3\2\2\2\u0169\u016a\3"+
		"\2\2\2\u016a\u0168\3\2\2\2\u016a\u016b\3\2\2\2\u016bp\3\2\2\2\u016c\u0170"+
		"\t\4\2\2\u016d\u016f\t\5\2\2\u016e\u016d\3\2\2\2\u016f\u0172\3\2\2\2\u0170"+
		"\u016e\3\2\2\2\u0170\u0171\3\2\2\2\u0171r\3\2\2\2\u0172\u0170\3\2\2\2"+
		"\u0173\u0175\7\17\2\2\u0174\u0173\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0176"+
		"\3\2\2\2\u0176\u0177\7\f\2\2\u0177t\3\2\2\2\u0178\u017a\t\6\2\2\u0179"+
		"\u0178\3\2\2\2\u017a\u017b\3\2\2\2\u017b\u0179\3\2\2\2\u017b\u017c\3\2"+
		"\2\2\u017c\u017d\3\2\2\2\u017d\u017e\b;\2\2\u017ev\3\2\2\2\16\2}\177\u013c"+
		"\u0142\u0148\u015e\u0163\u016a\u0170\u0174\u017b\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}