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
		ASSIGN=18, AT=19, COLON=20, COMMA=21, DIV=22, IDIV=23, DOLLAR=24, DOT=25, 
		EXP=26, MINUS=27, MOD=28, MUL=29, NOT=30, PLUS=31, QUESTION_MARK=32, SEMICOLON=33, 
		DIV_ASSIGN=34, EXP_ASSIGN=35, MINUS_ASSIGN=36, MUL_ASSIGN=37, PLUS_ASSIGN=38, 
		LESS_THAN=39, LESS_THAN_EQUAL=40, NOT_EQUAL=41, EQUAL=42, STRICT_EQUAL=43, 
		GREATER_THAN_EQUAL=44, GREATER_THAN=45, AND=46, OR=47, SHIFT_LEFT=48, 
		SHIFT_RIGHT=49, BITWISE_AND=50, BITWISE_OR=51, BITWISE_XOR=52, LEFT_SBRACKET=53, 
		RIGHT_SBRACKET=54, LEFT_RBRACKET=55, RIGHT_RBRACKET=56, LEFT_CBRACKET=57, 
		RIGHT_CBRACKET=58, LITERAL=59, FLOAT=60, INT=61, ID=62, SL_COMMENT=63, 
		WS=64;
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
			"ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", 
			"MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", 
			"EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL", 
			"GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", 
			"BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", 
			"RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", 
			"LITERAL", "FLOAT", "INT", "ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'", 
			"'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", "'stack'", 
			"'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", "'/'", "'\\'", 
			"'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, "'+'", "'?'", "';'", 
			"'/='", "'**='", "'-='", "'*='", "'+='", "'<'", "'<='", "'!='", "'=='", 
			"'==='", "'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", "'|'", "'^'", 
			"'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "END", 
			"FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2B\u0181\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3"+
		"\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3"+
		"\37\3\37\5\37\u00fd\n\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3$\3$\3$\3$\3%\3"+
		"%\3%\3&\3&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,\3,\3"+
		",\3-\3-\3-\3.\3.\3/\3/\3/\3/\3/\5/\u012e\n/\3\60\3\60\3\60\3\60\5\60\u0134"+
		"\n\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66"+
		"\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3<\3=\3=\3=\7=\u0154\n="+
		"\f=\16=\u0157\13=\3=\3=\3>\3>\3>\3>\3?\3?\7?\u0161\n?\f?\16?\u0164\13"+
		"?\3@\3@\7@\u0168\n@\f@\16@\u016b\13@\3A\3A\3A\3A\7A\u0171\nA\fA\16A\u0174"+
		"\13A\3A\5A\u0177\nA\3A\3A\3B\6B\u017c\nB\rB\16B\u017d\3B\3B\3\u0155\2"+
		"C\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37"+
		"= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o"+
		"9q:s;u<w\2y={>}?\177@\u0081A\u0083B\3\2\7\4\2\f\f\17\17\3\2\62;\5\2C\\"+
		"aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u0189\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3"+
		"\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2"+
		"\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2"+
		"Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3"+
		"\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2"+
		"\2\2s\3\2\2\2\2u\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2"+
		"\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\3\u0085\3\2\2\2\5\u008e\3\2\2\2\7\u0094"+
		"\3\2\2\2\t\u0099\3\2\2\2\13\u00a2\3\2\2\2\r\u00a6\3\2\2\2\17\u00ab\3\2"+
		"\2\2\21\u00af\3\2\2\2\23\u00b5\3\2\2\2\25\u00b9\3\2\2\2\27\u00be\3\2\2"+
		"\2\31\u00c1\3\2\2\2\33\u00c4\3\2\2\2\35\u00c9\3\2\2\2\37\u00cf\3\2\2\2"+
		"!\u00d4\3\2\2\2#\u00d9\3\2\2\2%\u00df\3\2\2\2\'\u00e1\3\2\2\2)\u00e3\3"+
		"\2\2\2+\u00e5\3\2\2\2-\u00e7\3\2\2\2/\u00e9\3\2\2\2\61\u00eb\3\2\2\2\63"+
		"\u00ed\3\2\2\2\65\u00ef\3\2\2\2\67\u00f2\3\2\2\29\u00f4\3\2\2\2;\u00f6"+
		"\3\2\2\2=\u00fc\3\2\2\2?\u00fe\3\2\2\2A\u0100\3\2\2\2C\u0102\3\2\2\2E"+
		"\u0104\3\2\2\2G\u0107\3\2\2\2I\u010b\3\2\2\2K\u010e\3\2\2\2M\u0111\3\2"+
		"\2\2O\u0114\3\2\2\2Q\u0116\3\2\2\2S\u0119\3\2\2\2U\u011c\3\2\2\2W\u011f"+
		"\3\2\2\2Y\u0123\3\2\2\2[\u0126\3\2\2\2]\u012d\3\2\2\2_\u0133\3\2\2\2a"+
		"\u0135\3\2\2\2c\u0138\3\2\2\2e\u013b\3\2\2\2g\u013d\3\2\2\2i\u013f\3\2"+
		"\2\2k\u0141\3\2\2\2m\u0143\3\2\2\2o\u0145\3\2\2\2q\u0147\3\2\2\2s\u0149"+
		"\3\2\2\2u\u014b\3\2\2\2w\u014d\3\2\2\2y\u0150\3\2\2\2{\u015a\3\2\2\2}"+
		"\u015e\3\2\2\2\177\u0165\3\2\2\2\u0081\u016c\3\2\2\2\u0083\u017b\3\2\2"+
		"\2\u0085\u0086\7c\2\2\u0086\u0087\7n\2\2\u0087\u0088\7n\2\2\u0088\u0089"+
		"\7q\2\2\u0089\u008a\7e\2\2\u008a\u008b\7c\2\2\u008b\u008c\7v\2\2\u008c"+
		"\u008d\7g\2\2\u008d\4\3\2\2\2\u008e\u008f\7d\2\2\u008f\u0090\7t\2\2\u0090"+
		"\u0091\7g\2\2\u0091\u0092\7c\2\2\u0092\u0093\7m\2\2\u0093\6\3\2\2\2\u0094"+
		"\u0095\7e\2\2\u0095\u0096\7c\2\2\u0096\u0097\7u\2\2\u0097\u0098\7g\2\2"+
		"\u0098\b\3\2\2\2\u0099\u009a\7e\2\2\u009a\u009b\7q\2\2\u009b\u009c\7p"+
		"\2\2\u009c\u009d\7v\2\2\u009d\u009e\7k\2\2\u009e\u009f\7p\2\2\u009f\u00a0"+
		"\7w\2\2\u00a0\u00a1\7g\2\2\u00a1\n\3\2\2\2\u00a2\u00a3\7f\2\2\u00a3\u00a4"+
		"\7g\2\2\u00a4\u00a5\7h\2\2\u00a5\f\3\2\2\2\u00a6\u00a7\7g\2\2\u00a7\u00a8"+
		"\7n\2\2\u00a8\u00a9\7u\2\2\u00a9\u00aa\7g\2\2\u00aa\16\3\2\2\2\u00ab\u00ac"+
		"\7g\2\2\u00ac\u00ad\7p\2\2\u00ad\u00ae\7f\2\2\u00ae\20\3\2\2\2\u00af\u00b0"+
		"\7h\2\2\u00b0\u00b1\7c\2\2\u00b1\u00b2\7n\2\2\u00b2\u00b3\7u\2\2\u00b3"+
		"\u00b4\7g\2\2\u00b4\22\3\2\2\2\u00b5\u00b6\7h\2\2\u00b6\u00b7\7q\2\2\u00b7"+
		"\u00b8\7t\2\2\u00b8\24\3\2\2\2\u00b9\u00ba\7j\2\2\u00ba\u00bb\7g\2\2\u00bb"+
		"\u00bc\7c\2\2\u00bc\u00bd\7r\2\2\u00bd\26\3\2\2\2\u00be\u00bf\7k\2\2\u00bf"+
		"\u00c0\7h\2\2\u00c0\30\3\2\2\2\u00c1\u00c2\7k\2\2\u00c2\u00c3\7p\2\2\u00c3"+
		"\32\3\2\2\2\u00c4\u00c5\7p\2\2\u00c5\u00c6\7w\2\2\u00c6\u00c7\7n\2\2\u00c7"+
		"\u00c8\7n\2\2\u00c8\34\3\2\2\2\u00c9\u00ca\7u\2\2\u00ca\u00cb\7v\2\2\u00cb"+
		"\u00cc\7c\2\2\u00cc\u00cd\7e\2\2\u00cd\u00ce\7m\2\2\u00ce\36\3\2\2\2\u00cf"+
		"\u00d0\7v\2\2\u00d0\u00d1\7t\2\2\u00d1\u00d2\7w\2\2\u00d2\u00d3\7g\2\2"+
		"\u00d3 \3\2\2\2\u00d4\u00d5\7y\2\2\u00d5\u00d6\7j\2\2\u00d6\u00d7\7g\2"+
		"\2\u00d7\u00d8\7p\2\2\u00d8\"\3\2\2\2\u00d9\u00da\7y\2\2\u00da\u00db\7"+
		"j\2\2\u00db\u00dc\7k\2\2\u00dc\u00dd\7n\2\2\u00dd\u00de\7g\2\2\u00de$"+
		"\3\2\2\2\u00df\u00e0\7?\2\2\u00e0&\3\2\2\2\u00e1\u00e2\7B\2\2\u00e2(\3"+
		"\2\2\2\u00e3\u00e4\7<\2\2\u00e4*\3\2\2\2\u00e5\u00e6\7.\2\2\u00e6,\3\2"+
		"\2\2\u00e7\u00e8\7\61\2\2\u00e8.\3\2\2\2\u00e9\u00ea\7^\2\2\u00ea\60\3"+
		"\2\2\2\u00eb\u00ec\7&\2\2\u00ec\62\3\2\2\2\u00ed\u00ee\7\60\2\2\u00ee"+
		"\64\3\2\2\2\u00ef\u00f0\7,\2\2\u00f0\u00f1\7,\2\2\u00f1\66\3\2\2\2\u00f2"+
		"\u00f3\7/\2\2\u00f38\3\2\2\2\u00f4\u00f5\7\'\2\2\u00f5:\3\2\2\2\u00f6"+
		"\u00f7\7,\2\2\u00f7<\3\2\2\2\u00f8\u00fd\7#\2\2\u00f9\u00fa\7p\2\2\u00fa"+
		"\u00fb\7q\2\2\u00fb\u00fd\7v\2\2\u00fc\u00f8\3\2\2\2\u00fc\u00f9\3\2\2"+
		"\2\u00fd>\3\2\2\2\u00fe\u00ff\7-\2\2\u00ff@\3\2\2\2\u0100\u0101\7A\2\2"+
		"\u0101B\3\2\2\2\u0102\u0103\7=\2\2\u0103D\3\2\2\2\u0104\u0105\7\61\2\2"+
		"\u0105\u0106\7?\2\2\u0106F\3\2\2\2\u0107\u0108\7,\2\2\u0108\u0109\7,\2"+
		"\2\u0109\u010a\7?\2\2\u010aH\3\2\2\2\u010b\u010c\7/\2\2\u010c\u010d\7"+
		"?\2\2\u010dJ\3\2\2\2\u010e\u010f\7,\2\2\u010f\u0110\7?\2\2\u0110L\3\2"+
		"\2\2\u0111\u0112\7-\2\2\u0112\u0113\7?\2\2\u0113N\3\2\2\2\u0114\u0115"+
		"\7>\2\2\u0115P\3\2\2\2\u0116\u0117\7>\2\2\u0117\u0118\7?\2\2\u0118R\3"+
		"\2\2\2\u0119\u011a\7#\2\2\u011a\u011b\7?\2\2\u011bT\3\2\2\2\u011c\u011d"+
		"\7?\2\2\u011d\u011e\7?\2\2\u011eV\3\2\2\2\u011f\u0120\7?\2\2\u0120\u0121"+
		"\7?\2\2\u0121\u0122\7?\2\2\u0122X\3\2\2\2\u0123\u0124\7@\2\2\u0124\u0125"+
		"\7?\2\2\u0125Z\3\2\2\2\u0126\u0127\7@\2\2\u0127\\\3\2\2\2\u0128\u0129"+
		"\7(\2\2\u0129\u012e\7(\2\2\u012a\u012b\7c\2\2\u012b\u012c\7p\2\2\u012c"+
		"\u012e\7f\2\2\u012d\u0128\3\2\2\2\u012d\u012a\3\2\2\2\u012e^\3\2\2\2\u012f"+
		"\u0130\7~\2\2\u0130\u0134\7~\2\2\u0131\u0132\7q\2\2\u0132\u0134\7t\2\2"+
		"\u0133\u012f\3\2\2\2\u0133\u0131\3\2\2\2\u0134`\3\2\2\2\u0135\u0136\7"+
		">\2\2\u0136\u0137\7>\2\2\u0137b\3\2\2\2\u0138\u0139\7@\2\2\u0139\u013a"+
		"\7@\2\2\u013ad\3\2\2\2\u013b\u013c\7(\2\2\u013cf\3\2\2\2\u013d\u013e\7"+
		"~\2\2\u013eh\3\2\2\2\u013f\u0140\7`\2\2\u0140j\3\2\2\2\u0141\u0142\7]"+
		"\2\2\u0142l\3\2\2\2\u0143\u0144\7_\2\2\u0144n\3\2\2\2\u0145\u0146\7*\2"+
		"\2\u0146p\3\2\2\2\u0147\u0148\7+\2\2\u0148r\3\2\2\2\u0149\u014a\7}\2\2"+
		"\u014at\3\2\2\2\u014b\u014c\7\177\2\2\u014cv\3\2\2\2\u014d\u014e\7^\2"+
		"\2\u014e\u014f\7$\2\2\u014fx\3\2\2\2\u0150\u0155\7$\2\2\u0151\u0154\5"+
		"w<\2\u0152\u0154\n\2\2\2\u0153\u0151\3\2\2\2\u0153\u0152\3\2\2\2\u0154"+
		"\u0157\3\2\2\2\u0155\u0156\3\2\2\2\u0155\u0153\3\2\2\2\u0156\u0158\3\2"+
		"\2\2\u0157\u0155\3\2\2\2\u0158\u0159\7$\2\2\u0159z\3\2\2\2\u015a\u015b"+
		"\5}?\2\u015b\u015c\5\63\32\2\u015c\u015d\5}?\2\u015d|\3\2\2\2\u015e\u0162"+
		"\t\3\2\2\u015f\u0161\t\3\2\2\u0160\u015f\3\2\2\2\u0161\u0164\3\2\2\2\u0162"+
		"\u0160\3\2\2\2\u0162\u0163\3\2\2\2\u0163~\3\2\2\2\u0164\u0162\3\2\2\2"+
		"\u0165\u0169\t\4\2\2\u0166\u0168\t\5\2\2\u0167\u0166\3\2\2\2\u0168\u016b"+
		"\3\2\2\2\u0169\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u0080\3\2\2\2\u016b"+
		"\u0169\3\2\2\2\u016c\u016d\7\61\2\2\u016d\u016e\7\61\2\2\u016e\u0172\3"+
		"\2\2\2\u016f\u0171\n\2\2\2\u0170\u016f\3\2\2\2\u0171\u0174\3\2\2\2\u0172"+
		"\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0176\3\2\2\2\u0174\u0172\3\2"+
		"\2\2\u0175\u0177\7\17\2\2\u0176\u0175\3\2\2\2\u0176\u0177\3\2\2\2\u0177"+
		"\u0178\3\2\2\2\u0178\u0179\7\f\2\2\u0179\u0082\3\2\2\2\u017a\u017c\t\6"+
		"\2\2\u017b\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017b\3\2\2\2\u017d"+
		"\u017e\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\bB\2\2\u0180\u0084\3\2"+
		"\2\2\r\2\u00fc\u012d\u0133\u0153\u0155\u0162\u0169\u0172\u0176\u017d\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}