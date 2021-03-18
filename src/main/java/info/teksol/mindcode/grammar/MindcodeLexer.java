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
		ALLOCATE=1, BREAK=2, CASE=3, CONTINUE=4, DEF=5, ELSE=6, END=7, FALSE=8, 
		FOR=9, HEAP=10, IF=11, IN=12, NULL=13, STACK=14, TRUE=15, WHEN=16, WHILE=17, 
		ASSIGN=18, AT=19, COLON=20, COMMA=21, DIV=22, DOLLAR=23, DOT=24, EXP=25, 
		MINUS=26, MOD=27, MUL=28, NOT=29, PLUS=30, QUESTION_MARK=31, SEMICOLON=32, 
		DIV_ASSIGN=33, EXP_ASSIGN=34, MINUS_ASSIGN=35, MUL_ASSIGN=36, PLUS_ASSIGN=37, 
		LESS_THAN=38, LESS_THAN_EQUAL=39, NOT_EQUAL=40, EQUAL=41, STRICT_EQUAL=42, 
		GREATER_THAN_EQUAL=43, GREATER_THAN=44, AND=45, OR=46, LEFT_SBRACKET=47, 
		RIGHT_SBRACKET=48, LEFT_RBRACKET=49, RIGHT_RBRACKET=50, LEFT_CBRACKET=51, 
		RIGHT_CBRACKET=52, LITERAL=53, FLOAT=54, INT=55, ID=56, SL_COMMENT=57, 
		WS=58;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "END", "FALSE", 
			"FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", "WHILE", 
			"ASSIGN", "AT", "COLON", "COMMA", "DIV", "DOLLAR", "DOT", "EXP", "MINUS", 
			"MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", 
			"EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", 
			"LITERAL", "FLOAT", "INT", "ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'", 
			"'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", "'stack'", 
			"'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", "'/'", "'$'", 
			"'.'", null, "'-'", "'%'", "'*'", null, "'+'", "'?'", "';'", "'/='", 
			"'^='", "'-='", "'*='", "'+='", "'<'", "'<='", "'!='", "'=='", "'==='", 
			"'>='", "'>'", null, null, "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "END", 
			"FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "DOLLAR", "DOT", "EXP", 
			"MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", 
			"EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "LITERAL", "FLOAT", 
			"INT", "ID", "SL_COMMENT", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2<\u0168\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4"+
		"\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3"+
		"\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3"+
		"\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\5\32\u00e5\n\32"+
		"\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\5\36\u00f1\n\36\3\37"+
		"\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3\'"+
		"\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3+\3+\3,\3,\3,\3-\3-\3.\3.\3.\3"+
		".\3.\5.\u0121\n.\3/\3/\3/\3/\5/\u0127\n/\3\60\3\60\3\61\3\61\3\62\3\62"+
		"\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3\67\7\67\u013b"+
		"\n\67\f\67\16\67\u013e\13\67\3\67\3\67\38\38\38\38\39\39\79\u0148\n9\f"+
		"9\169\u014b\139\3:\3:\7:\u014f\n:\f:\16:\u0152\13:\3;\3;\3;\3;\7;\u0158"+
		"\n;\f;\16;\u015b\13;\3;\5;\u015e\n;\3;\3;\3<\6<\u0163\n<\r<\16<\u0164"+
		"\3<\3<\3\u013c\2=\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65"+
		"\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64"+
		"g\65i\66k\2m\67o8q9s:u;w<\3\2\7\4\2\f\f\17\17\3\2\62;\4\2C\\c|\7\2//\62"+
		";C\\aac|\5\2\13\f\17\17\"\"\2\u0171\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2"+
		"\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3"+
		"\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2"+
		"\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2"+
		"\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2["+
		"\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2"+
		"\2\2\2i\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2"+
		"\2w\3\2\2\2\3y\3\2\2\2\5\u0082\3\2\2\2\7\u0088\3\2\2\2\t\u008d\3\2\2\2"+
		"\13\u0096\3\2\2\2\r\u009a\3\2\2\2\17\u009f\3\2\2\2\21\u00a3\3\2\2\2\23"+
		"\u00a9\3\2\2\2\25\u00ad\3\2\2\2\27\u00b2\3\2\2\2\31\u00b5\3\2\2\2\33\u00b8"+
		"\3\2\2\2\35\u00bd\3\2\2\2\37\u00c3\3\2\2\2!\u00c8\3\2\2\2#\u00cd\3\2\2"+
		"\2%\u00d3\3\2\2\2\'\u00d5\3\2\2\2)\u00d7\3\2\2\2+\u00d9\3\2\2\2-\u00db"+
		"\3\2\2\2/\u00dd\3\2\2\2\61\u00df\3\2\2\2\63\u00e4\3\2\2\2\65\u00e6\3\2"+
		"\2\2\67\u00e8\3\2\2\29\u00ea\3\2\2\2;\u00f0\3\2\2\2=\u00f2\3\2\2\2?\u00f4"+
		"\3\2\2\2A\u00f6\3\2\2\2C\u00f8\3\2\2\2E\u00fb\3\2\2\2G\u00fe\3\2\2\2I"+
		"\u0101\3\2\2\2K\u0104\3\2\2\2M\u0107\3\2\2\2O\u0109\3\2\2\2Q\u010c\3\2"+
		"\2\2S\u010f\3\2\2\2U\u0112\3\2\2\2W\u0116\3\2\2\2Y\u0119\3\2\2\2[\u0120"+
		"\3\2\2\2]\u0126\3\2\2\2_\u0128\3\2\2\2a\u012a\3\2\2\2c\u012c\3\2\2\2e"+
		"\u012e\3\2\2\2g\u0130\3\2\2\2i\u0132\3\2\2\2k\u0134\3\2\2\2m\u0137\3\2"+
		"\2\2o\u0141\3\2\2\2q\u0145\3\2\2\2s\u014c\3\2\2\2u\u0153\3\2\2\2w\u0162"+
		"\3\2\2\2yz\7c\2\2z{\7n\2\2{|\7n\2\2|}\7q\2\2}~\7e\2\2~\177\7c\2\2\177"+
		"\u0080\7v\2\2\u0080\u0081\7g\2\2\u0081\4\3\2\2\2\u0082\u0083\7d\2\2\u0083"+
		"\u0084\7t\2\2\u0084\u0085\7g\2\2\u0085\u0086\7c\2\2\u0086\u0087\7m\2\2"+
		"\u0087\6\3\2\2\2\u0088\u0089\7e\2\2\u0089\u008a\7c\2\2\u008a\u008b\7u"+
		"\2\2\u008b\u008c\7g\2\2\u008c\b\3\2\2\2\u008d\u008e\7e\2\2\u008e\u008f"+
		"\7q\2\2\u008f\u0090\7p\2\2\u0090\u0091\7v\2\2\u0091\u0092\7k\2\2\u0092"+
		"\u0093\7p\2\2\u0093\u0094\7w\2\2\u0094\u0095\7g\2\2\u0095\n\3\2\2\2\u0096"+
		"\u0097\7f\2\2\u0097\u0098\7g\2\2\u0098\u0099\7h\2\2\u0099\f\3\2\2\2\u009a"+
		"\u009b\7g\2\2\u009b\u009c\7n\2\2\u009c\u009d\7u\2\2\u009d\u009e\7g\2\2"+
		"\u009e\16\3\2\2\2\u009f\u00a0\7g\2\2\u00a0\u00a1\7p\2\2\u00a1\u00a2\7"+
		"f\2\2\u00a2\20\3\2\2\2\u00a3\u00a4\7h\2\2\u00a4\u00a5\7c\2\2\u00a5\u00a6"+
		"\7n\2\2\u00a6\u00a7\7u\2\2\u00a7\u00a8\7g\2\2\u00a8\22\3\2\2\2\u00a9\u00aa"+
		"\7h\2\2\u00aa\u00ab\7q\2\2\u00ab\u00ac\7t\2\2\u00ac\24\3\2\2\2\u00ad\u00ae"+
		"\7j\2\2\u00ae\u00af\7g\2\2\u00af\u00b0\7c\2\2\u00b0\u00b1\7r\2\2\u00b1"+
		"\26\3\2\2\2\u00b2\u00b3\7k\2\2\u00b3\u00b4\7h\2\2\u00b4\30\3\2\2\2\u00b5"+
		"\u00b6\7k\2\2\u00b6\u00b7\7p\2\2\u00b7\32\3\2\2\2\u00b8\u00b9\7p\2\2\u00b9"+
		"\u00ba\7w\2\2\u00ba\u00bb\7n\2\2\u00bb\u00bc\7n\2\2\u00bc\34\3\2\2\2\u00bd"+
		"\u00be\7u\2\2\u00be\u00bf\7v\2\2\u00bf\u00c0\7c\2\2\u00c0\u00c1\7e\2\2"+
		"\u00c1\u00c2\7m\2\2\u00c2\36\3\2\2\2\u00c3\u00c4\7v\2\2\u00c4\u00c5\7"+
		"t\2\2\u00c5\u00c6\7w\2\2\u00c6\u00c7\7g\2\2\u00c7 \3\2\2\2\u00c8\u00c9"+
		"\7y\2\2\u00c9\u00ca\7j\2\2\u00ca\u00cb\7g\2\2\u00cb\u00cc\7p\2\2\u00cc"+
		"\"\3\2\2\2\u00cd\u00ce\7y\2\2\u00ce\u00cf\7j\2\2\u00cf\u00d0\7k\2\2\u00d0"+
		"\u00d1\7n\2\2\u00d1\u00d2\7g\2\2\u00d2$\3\2\2\2\u00d3\u00d4\7?\2\2\u00d4"+
		"&\3\2\2\2\u00d5\u00d6\7B\2\2\u00d6(\3\2\2\2\u00d7\u00d8\7<\2\2\u00d8*"+
		"\3\2\2\2\u00d9\u00da\7.\2\2\u00da,\3\2\2\2\u00db\u00dc\7\61\2\2\u00dc"+
		".\3\2\2\2\u00dd\u00de\7&\2\2\u00de\60\3\2\2\2\u00df\u00e0\7\60\2\2\u00e0"+
		"\62\3\2\2\2\u00e1\u00e5\7`\2\2\u00e2\u00e3\7,\2\2\u00e3\u00e5\7,\2\2\u00e4"+
		"\u00e1\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\64\3\2\2\2\u00e6\u00e7\7/\2\2"+
		"\u00e7\66\3\2\2\2\u00e8\u00e9\7\'\2\2\u00e98\3\2\2\2\u00ea\u00eb\7,\2"+
		"\2\u00eb:\3\2\2\2\u00ec\u00f1\7#\2\2\u00ed\u00ee\7p\2\2\u00ee\u00ef\7"+
		"q\2\2\u00ef\u00f1\7v\2\2\u00f0\u00ec\3\2\2\2\u00f0\u00ed\3\2\2\2\u00f1"+
		"<\3\2\2\2\u00f2\u00f3\7-\2\2\u00f3>\3\2\2\2\u00f4\u00f5\7A\2\2\u00f5@"+
		"\3\2\2\2\u00f6\u00f7\7=\2\2\u00f7B\3\2\2\2\u00f8\u00f9\7\61\2\2\u00f9"+
		"\u00fa\7?\2\2\u00faD\3\2\2\2\u00fb\u00fc\7`\2\2\u00fc\u00fd\7?\2\2\u00fd"+
		"F\3\2\2\2\u00fe\u00ff\7/\2\2\u00ff\u0100\7?\2\2\u0100H\3\2\2\2\u0101\u0102"+
		"\7,\2\2\u0102\u0103\7?\2\2\u0103J\3\2\2\2\u0104\u0105\7-\2\2\u0105\u0106"+
		"\7?\2\2\u0106L\3\2\2\2\u0107\u0108\7>\2\2\u0108N\3\2\2\2\u0109\u010a\7"+
		">\2\2\u010a\u010b\7?\2\2\u010bP\3\2\2\2\u010c\u010d\7#\2\2\u010d\u010e"+
		"\7?\2\2\u010eR\3\2\2\2\u010f\u0110\7?\2\2\u0110\u0111\7?\2\2\u0111T\3"+
		"\2\2\2\u0112\u0113\7?\2\2\u0113\u0114\7?\2\2\u0114\u0115\7?\2\2\u0115"+
		"V\3\2\2\2\u0116\u0117\7@\2\2\u0117\u0118\7?\2\2\u0118X\3\2\2\2\u0119\u011a"+
		"\7@\2\2\u011aZ\3\2\2\2\u011b\u011c\7(\2\2\u011c\u0121\7(\2\2\u011d\u011e"+
		"\7c\2\2\u011e\u011f\7p\2\2\u011f\u0121\7f\2\2\u0120\u011b\3\2\2\2\u0120"+
		"\u011d\3\2\2\2\u0121\\\3\2\2\2\u0122\u0123\7~\2\2\u0123\u0127\7~\2\2\u0124"+
		"\u0125\7q\2\2\u0125\u0127\7t\2\2\u0126\u0122\3\2\2\2\u0126\u0124\3\2\2"+
		"\2\u0127^\3\2\2\2\u0128\u0129\7]\2\2\u0129`\3\2\2\2\u012a\u012b\7_\2\2"+
		"\u012bb\3\2\2\2\u012c\u012d\7*\2\2\u012dd\3\2\2\2\u012e\u012f\7+\2\2\u012f"+
		"f\3\2\2\2\u0130\u0131\7}\2\2\u0131h\3\2\2\2\u0132\u0133\7\177\2\2\u0133"+
		"j\3\2\2\2\u0134\u0135\7^\2\2\u0135\u0136\7$\2\2\u0136l\3\2\2\2\u0137\u013c"+
		"\7$\2\2\u0138\u013b\5k\66\2\u0139\u013b\n\2\2\2\u013a\u0138\3\2\2\2\u013a"+
		"\u0139\3\2\2\2\u013b\u013e\3\2\2\2\u013c\u013d\3\2\2\2\u013c\u013a\3\2"+
		"\2\2\u013d\u013f\3\2\2\2\u013e\u013c\3\2\2\2\u013f\u0140\7$\2\2\u0140"+
		"n\3\2\2\2\u0141\u0142\5q9\2\u0142\u0143\5\61\31\2\u0143\u0144\5q9\2\u0144"+
		"p\3\2\2\2\u0145\u0149\t\3\2\2\u0146\u0148\t\3\2\2\u0147\u0146\3\2\2\2"+
		"\u0148\u014b\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014ar\3"+
		"\2\2\2\u014b\u0149\3\2\2\2\u014c\u0150\t\4\2\2\u014d\u014f\t\5\2\2\u014e"+
		"\u014d\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2"+
		"\2\2\u0151t\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u0154\7\61\2\2\u0154\u0155"+
		"\7\61\2\2\u0155\u0159\3\2\2\2\u0156\u0158\n\2\2\2\u0157\u0156\3\2\2\2"+
		"\u0158\u015b\3\2\2\2\u0159\u0157\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015d"+
		"\3\2\2\2\u015b\u0159\3\2\2\2\u015c\u015e\7\17\2\2\u015d\u015c\3\2\2\2"+
		"\u015d\u015e\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u0160\7\f\2\2\u0160v\3"+
		"\2\2\2\u0161\u0163\t\6\2\2\u0162\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164"+
		"\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0167\b<"+
		"\2\2\u0167x\3\2\2\2\16\2\u00e4\u00f0\u0120\u0126\u013a\u013c\u0149\u0150"+
		"\u0159\u015d\u0164\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}