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
		EXP_ASSIGN=15, PLUS=16, MINUS=17, MUL=18, DIV=19, MOD=20, EXP=21, COPPER=22, 
		LEAD=23, METAGLASS=24, GRAPHITE=25, SAND=26, COAL=27, TITANIUM=28, SCRAP=29, 
		SILICON=30, PYRATITE=31, WATER=32, SLAG=33, CRYOFLUID=34, TRUE=35, FALSE=36, 
		STRICT_EQUAL=37, EQUAL=38, NOT_EQUAL=39, GREATER=40, LESS=41, LESS_EQUAL=42, 
		GREATER_EQUAL=43, AND=44, OR=45, NOT=46, LEFT_RBRACKET=47, RIGHT_RBRACKET=48, 
		LEFT_SBRACKET=49, RIGHT_SBRACKET=50, LEFT_CBRACKET=51, RIGHT_CBRACKET=52, 
		NULL=53, INT=54, FLOAT=55, ID=56, CRLF=57, WS=58;
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
			null, null, "'while'", "'if'", "'else'", "'@'", "'.'", "','", "';'", 
			"'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'**='", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'**'", "'copper'", "'lead'", "'metaglass'", "'graphite'", 
			"'sand'", "'coal'", "'titanium'", "'scrap'", "'silicon'", "'pyratite'", 
			"'water'", "'slag'", "'cryofluid'", "'true'", "'false'", "'==='", "'=='", 
			"'!='", "'>'", "'<'", "'<='", "'>='", null, null, null, "'('", "')'", 
			"'['", "']'", "'{'", "'}'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LITERAL", "WHILE", "IF", "ELSE", "AT", "DOT", "COMMA", "SEMICOLON", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2<\u0183\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\7\3\u0080\n\3\f\3\16\3\u0083\13\3\3\3\3\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t"+
		"\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3"+
		"\25\3\25\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3"+
		"\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3"+
		"\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3"+
		"\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 \3!\3!\3!\3!"+
		"\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$"+
		"\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3(\3"+
		"(\3(\3)\3)\3)\3*\3*\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3.\3.\3.\5.\u0141\n"+
		".\3/\3/\3/\3/\5/\u0147\n/\3\60\3\60\3\60\3\60\5\60\u014d\n\60\3\61\3\61"+
		"\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\3\67\3\67"+
		"\3\67\38\68\u0161\n8\r8\168\u0162\39\79\u0166\n9\f9\169\u0169\139\39\3"+
		"9\69\u016d\n9\r9\169\u016e\3:\3:\7:\u0173\n:\f:\16:\u0176\13:\3;\5;\u0179"+
		"\n;\3;\3;\3<\6<\u017e\n<\r<\16<\u017f\3<\3<\3\u0081\2=\3\2\5\3\7\4\t\5"+
		"\13\6\r\7\17\b\21\t\23\n\25\13\27\f\31\r\33\16\35\17\37\20!\21#\22%\23"+
		"\'\24)\25+\26-\27/\30\61\31\63\32\65\33\67\349\35;\36=\37? A!C\"E#G$I"+
		"%K&M\'O(Q)S*U+W,Y-[.]/_\60a\61c\62e\63g\64i\65k\66m\67o8q9s:u;w<\3\2\7"+
		"\4\2\f\f\17\17\3\2\62;\5\2C\\aac|\6\2\62;C\\aac|\4\2\13\13\"\"\2\u018c"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2"+
		"\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2"+
		"\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2"+
		"\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2"+
		"\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W"+
		"\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2"+
		"\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2"+
		"\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\3y\3\2\2\2\5|\3\2\2\2\7\u0086"+
		"\3\2\2\2\t\u008c\3\2\2\2\13\u008f\3\2\2\2\r\u0094\3\2\2\2\17\u0096\3\2"+
		"\2\2\21\u0098\3\2\2\2\23\u009a\3\2\2\2\25\u009c\3\2\2\2\27\u009e\3\2\2"+
		"\2\31\u00a1\3\2\2\2\33\u00a4\3\2\2\2\35\u00a7\3\2\2\2\37\u00aa\3\2\2\2"+
		"!\u00ad\3\2\2\2#\u00b1\3\2\2\2%\u00b3\3\2\2\2\'\u00b5\3\2\2\2)\u00b7\3"+
		"\2\2\2+\u00b9\3\2\2\2-\u00bb\3\2\2\2/\u00be\3\2\2\2\61\u00c5\3\2\2\2\63"+
		"\u00ca\3\2\2\2\65\u00d4\3\2\2\2\67\u00dd\3\2\2\29\u00e2\3\2\2\2;\u00e7"+
		"\3\2\2\2=\u00f0\3\2\2\2?\u00f6\3\2\2\2A\u00fe\3\2\2\2C\u0107\3\2\2\2E"+
		"\u010d\3\2\2\2G\u0112\3\2\2\2I\u011c\3\2\2\2K\u0121\3\2\2\2M\u0127\3\2"+
		"\2\2O\u012b\3\2\2\2Q\u012e\3\2\2\2S\u0131\3\2\2\2U\u0133\3\2\2\2W\u0135"+
		"\3\2\2\2Y\u0138\3\2\2\2[\u0140\3\2\2\2]\u0146\3\2\2\2_\u014c\3\2\2\2a"+
		"\u014e\3\2\2\2c\u0150\3\2\2\2e\u0152\3\2\2\2g\u0154\3\2\2\2i\u0156\3\2"+
		"\2\2k\u0158\3\2\2\2m\u015a\3\2\2\2o\u0160\3\2\2\2q\u0167\3\2\2\2s\u0170"+
		"\3\2\2\2u\u0178\3\2\2\2w\u017d\3\2\2\2yz\7^\2\2z{\7$\2\2{\4\3\2\2\2|\u0081"+
		"\7$\2\2}\u0080\5\3\2\2~\u0080\n\2\2\2\177}\3\2\2\2\177~\3\2\2\2\u0080"+
		"\u0083\3\2\2\2\u0081\u0082\3\2\2\2\u0081\177\3\2\2\2\u0082\u0084\3\2\2"+
		"\2\u0083\u0081\3\2\2\2\u0084\u0085\7$\2\2\u0085\6\3\2\2\2\u0086\u0087"+
		"\7y\2\2\u0087\u0088\7j\2\2\u0088\u0089\7k\2\2\u0089\u008a\7n\2\2\u008a"+
		"\u008b\7g\2\2\u008b\b\3\2\2\2\u008c\u008d\7k\2\2\u008d\u008e\7h\2\2\u008e"+
		"\n\3\2\2\2\u008f\u0090\7g\2\2\u0090\u0091\7n\2\2\u0091\u0092\7u\2\2\u0092"+
		"\u0093\7g\2\2\u0093\f\3\2\2\2\u0094\u0095\7B\2\2\u0095\16\3\2\2\2\u0096"+
		"\u0097\7\60\2\2\u0097\20\3\2\2\2\u0098\u0099\7.\2\2\u0099\22\3\2\2\2\u009a"+
		"\u009b\7=\2\2\u009b\24\3\2\2\2\u009c\u009d\7?\2\2\u009d\26\3\2\2\2\u009e"+
		"\u009f\7-\2\2\u009f\u00a0\7?\2\2\u00a0\30\3\2\2\2\u00a1\u00a2\7/\2\2\u00a2"+
		"\u00a3\7?\2\2\u00a3\32\3\2\2\2\u00a4\u00a5\7,\2\2\u00a5\u00a6\7?\2\2\u00a6"+
		"\34\3\2\2\2\u00a7\u00a8\7\61\2\2\u00a8\u00a9\7?\2\2\u00a9\36\3\2\2\2\u00aa"+
		"\u00ab\7\'\2\2\u00ab\u00ac\7?\2\2\u00ac \3\2\2\2\u00ad\u00ae\7,\2\2\u00ae"+
		"\u00af\7,\2\2\u00af\u00b0\7?\2\2\u00b0\"\3\2\2\2\u00b1\u00b2\7-\2\2\u00b2"+
		"$\3\2\2\2\u00b3\u00b4\7/\2\2\u00b4&\3\2\2\2\u00b5\u00b6\7,\2\2\u00b6("+
		"\3\2\2\2\u00b7\u00b8\7\61\2\2\u00b8*\3\2\2\2\u00b9\u00ba\7\'\2\2\u00ba"+
		",\3\2\2\2\u00bb\u00bc\7,\2\2\u00bc\u00bd\7,\2\2\u00bd.\3\2\2\2\u00be\u00bf"+
		"\7e\2\2\u00bf\u00c0\7q\2\2\u00c0\u00c1\7r\2\2\u00c1\u00c2\7r\2\2\u00c2"+
		"\u00c3\7g\2\2\u00c3\u00c4\7t\2\2\u00c4\60\3\2\2\2\u00c5\u00c6\7n\2\2\u00c6"+
		"\u00c7\7g\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7f\2\2\u00c9\62\3\2\2\2\u00ca"+
		"\u00cb\7o\2\2\u00cb\u00cc\7g\2\2\u00cc\u00cd\7v\2\2\u00cd\u00ce\7c\2\2"+
		"\u00ce\u00cf\7i\2\2\u00cf\u00d0\7n\2\2\u00d0\u00d1\7c\2\2\u00d1\u00d2"+
		"\7u\2\2\u00d2\u00d3\7u\2\2\u00d3\64\3\2\2\2\u00d4\u00d5\7i\2\2\u00d5\u00d6"+
		"\7t\2\2\u00d6\u00d7\7c\2\2\u00d7\u00d8\7r\2\2\u00d8\u00d9\7j\2\2\u00d9"+
		"\u00da\7k\2\2\u00da\u00db\7v\2\2\u00db\u00dc\7g\2\2\u00dc\66\3\2\2\2\u00dd"+
		"\u00de\7u\2\2\u00de\u00df\7c\2\2\u00df\u00e0\7p\2\2\u00e0\u00e1\7f\2\2"+
		"\u00e18\3\2\2\2\u00e2\u00e3\7e\2\2\u00e3\u00e4\7q\2\2\u00e4\u00e5\7c\2"+
		"\2\u00e5\u00e6\7n\2\2\u00e6:\3\2\2\2\u00e7\u00e8\7v\2\2\u00e8\u00e9\7"+
		"k\2\2\u00e9\u00ea\7v\2\2\u00ea\u00eb\7c\2\2\u00eb\u00ec\7p\2\2\u00ec\u00ed"+
		"\7k\2\2\u00ed\u00ee\7w\2\2\u00ee\u00ef\7o\2\2\u00ef<\3\2\2\2\u00f0\u00f1"+
		"\7u\2\2\u00f1\u00f2\7e\2\2\u00f2\u00f3\7t\2\2\u00f3\u00f4\7c\2\2\u00f4"+
		"\u00f5\7r\2\2\u00f5>\3\2\2\2\u00f6\u00f7\7u\2\2\u00f7\u00f8\7k\2\2\u00f8"+
		"\u00f9\7n\2\2\u00f9\u00fa\7k\2\2\u00fa\u00fb\7e\2\2\u00fb\u00fc\7q\2\2"+
		"\u00fc\u00fd\7p\2\2\u00fd@\3\2\2\2\u00fe\u00ff\7r\2\2\u00ff\u0100\7{\2"+
		"\2\u0100\u0101\7t\2\2\u0101\u0102\7c\2\2\u0102\u0103\7v\2\2\u0103\u0104"+
		"\7k\2\2\u0104\u0105\7v\2\2\u0105\u0106\7g\2\2\u0106B\3\2\2\2\u0107\u0108"+
		"\7y\2\2\u0108\u0109\7c\2\2\u0109\u010a\7v\2\2\u010a\u010b\7g\2\2\u010b"+
		"\u010c\7t\2\2\u010cD\3\2\2\2\u010d\u010e\7u\2\2\u010e\u010f\7n\2\2\u010f"+
		"\u0110\7c\2\2\u0110\u0111\7i\2\2\u0111F\3\2\2\2\u0112\u0113\7e\2\2\u0113"+
		"\u0114\7t\2\2\u0114\u0115\7{\2\2\u0115\u0116\7q\2\2\u0116\u0117\7h\2\2"+
		"\u0117\u0118\7n\2\2\u0118\u0119\7w\2\2\u0119\u011a\7k\2\2\u011a\u011b"+
		"\7f\2\2\u011bH\3\2\2\2\u011c\u011d\7v\2\2\u011d\u011e\7t\2\2\u011e\u011f"+
		"\7w\2\2\u011f\u0120\7g\2\2\u0120J\3\2\2\2\u0121\u0122\7h\2\2\u0122\u0123"+
		"\7c\2\2\u0123\u0124\7n\2\2\u0124\u0125\7u\2\2\u0125\u0126\7g\2\2\u0126"+
		"L\3\2\2\2\u0127\u0128\7?\2\2\u0128\u0129\7?\2\2\u0129\u012a\7?\2\2\u012a"+
		"N\3\2\2\2\u012b\u012c\7?\2\2\u012c\u012d\7?\2\2\u012dP\3\2\2\2\u012e\u012f"+
		"\7#\2\2\u012f\u0130\7?\2\2\u0130R\3\2\2\2\u0131\u0132\7@\2\2\u0132T\3"+
		"\2\2\2\u0133\u0134\7>\2\2\u0134V\3\2\2\2\u0135\u0136\7>\2\2\u0136\u0137"+
		"\7?\2\2\u0137X\3\2\2\2\u0138\u0139\7@\2\2\u0139\u013a\7?\2\2\u013aZ\3"+
		"\2\2\2\u013b\u013c\7c\2\2\u013c\u013d\7p\2\2\u013d\u0141\7f\2\2\u013e"+
		"\u013f\7(\2\2\u013f\u0141\7(\2\2\u0140\u013b\3\2\2\2\u0140\u013e\3\2\2"+
		"\2\u0141\\\3\2\2\2\u0142\u0143\7q\2\2\u0143\u0147\7t\2\2\u0144\u0145\7"+
		"~\2\2\u0145\u0147\7~\2\2\u0146\u0142\3\2\2\2\u0146\u0144\3\2\2\2\u0147"+
		"^\3\2\2\2\u0148\u0149\7p\2\2\u0149\u014a\7q\2\2\u014a\u014d\7v\2\2\u014b"+
		"\u014d\7#\2\2\u014c\u0148\3\2\2\2\u014c\u014b\3\2\2\2\u014d`\3\2\2\2\u014e"+
		"\u014f\7*\2\2\u014fb\3\2\2\2\u0150\u0151\7+\2\2\u0151d\3\2\2\2\u0152\u0153"+
		"\7]\2\2\u0153f\3\2\2\2\u0154\u0155\7_\2\2\u0155h\3\2\2\2\u0156\u0157\7"+
		"}\2\2\u0157j\3\2\2\2\u0158\u0159\7\177\2\2\u0159l\3\2\2\2\u015a\u015b"+
		"\7p\2\2\u015b\u015c\7w\2\2\u015c\u015d\7n\2\2\u015d\u015e\7n\2\2\u015e"+
		"n\3\2\2\2\u015f\u0161\t\3\2\2\u0160\u015f\3\2\2\2\u0161\u0162\3\2\2\2"+
		"\u0162\u0160\3\2\2\2\u0162\u0163\3\2\2\2\u0163p\3\2\2\2\u0164\u0166\t"+
		"\3\2\2\u0165\u0164\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0165\3\2\2\2\u0167"+
		"\u0168\3\2\2\2\u0168\u016a\3\2\2\2\u0169\u0167\3\2\2\2\u016a\u016c\7\60"+
		"\2\2\u016b\u016d\t\3\2\2\u016c\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e"+
		"\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016fr\3\2\2\2\u0170\u0174\t\4\2\2"+
		"\u0171\u0173\t\5\2\2\u0172\u0171\3\2\2\2\u0173\u0176\3\2\2\2\u0174\u0172"+
		"\3\2\2\2\u0174\u0175\3\2\2\2\u0175t\3\2\2\2\u0176\u0174\3\2\2\2\u0177"+
		"\u0179\7\17\2\2\u0178\u0177\3\2\2\2\u0178\u0179\3\2\2\2\u0179\u017a\3"+
		"\2\2\2\u017a\u017b\7\f\2\2\u017bv\3\2\2\2\u017c\u017e\t\6\2\2\u017d\u017c"+
		"\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u017d\3\2\2\2\u017f\u0180\3\2\2\2\u0180"+
		"\u0181\3\2\2\2\u0181\u0182\b<\2\2\u0182x\3\2\2\2\16\2\177\u0081\u0140"+
		"\u0146\u014c\u0162\u0167\u016e\u0174\u0178\u017f\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}