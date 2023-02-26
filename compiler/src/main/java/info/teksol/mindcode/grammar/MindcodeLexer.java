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
		END=9, FALSE=10, FOR=11, HEAP=12, IF=13, IN=14, INLINE=15, LOOP=16, NULL=17, 
		SENSOR=18, STACK=19, THEN=20, TRUE=21, WHEN=22, WHILE=23, ASSIGN=24, AT=25, 
		COLON=26, COMMA=27, DIV=28, IDIV=29, DOLLAR=30, DOT=31, EXP=32, MINUS=33, 
		MOD=34, MUL=35, NOT=36, BITWISE_NOT=37, PLUS=38, QUESTION_MARK=39, SEMICOLON=40, 
		DIV_ASSIGN=41, EXP_ASSIGN=42, MINUS_ASSIGN=43, MUL_ASSIGN=44, PLUS_ASSIGN=45, 
		LESS_THAN=46, LESS_THAN_EQUAL=47, NOT_EQUAL=48, EQUAL=49, STRICT_EQUAL=50, 
		STRICT_NOT_EQUAL=51, GREATER_THAN_EQUAL=52, GREATER_THAN=53, AND=54, OR=55, 
		SHIFT_LEFT=56, SHIFT_RIGHT=57, BITWISE_AND=58, BITWISE_OR=59, BITWISE_XOR=60, 
		LEFT_SBRACKET=61, RIGHT_SBRACKET=62, LEFT_RBRACKET=63, RIGHT_RBRACKET=64, 
		LEFT_CBRACKET=65, RIGHT_CBRACKET=66, LITERAL=67, FLOAT=68, INT=69, HEXINT=70, 
		ID=71, SL_COMMENT=72, WS=73;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "DO", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", "LOOP", "NULL", 
			"SENSOR", "STACK", "THEN", "TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", 
			"COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", 
			"NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", 
			"EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", 
			"GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", 
			"BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"ESCAPED_QUOTE", "LITERAL", "FLOAT", "INT", "HEXINT", "ID", "SL_COMMENT", 
			"WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'do'", 
			"'else'", "'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", 
			"'inline'", "'loop'", "'null'", "'sensor'", "'stack'", "'then'", "'true'", 
			"'when'", "'while'", "'='", "'@'", "':'", "','", "'/'", "'\\'", "'$'", 
			"'.'", "'**'", "'-'", "'%'", "'*'", null, "'~'", "'+'", "'?'", "';'", 
			"'/='", "'**='", "'-='", "'*='", "'+='", "'<'", "'<='", "'!='", "'=='", 
			"'==='", "'!=='", "'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", "'|'", 
			"'^'", "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "DO", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", "LOOP", "NULL", 
			"SENSOR", "STACK", "THEN", "TRUE", "WHEN", "WHILE", "ASSIGN", "AT", "COLON", 
			"COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", "MINUS", "MOD", "MUL", 
			"NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN", 
			"EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN", 
			"LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "STRICT_NOT_EQUAL", 
			"GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", 
			"BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", 
			"LEFT_RBRACKET", "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", 
			"LITERAL", "FLOAT", "INT", "HEXINT", "ID", "SL_COMMENT", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2K\u01c3\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3"+
		"\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3"+
		"\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25"+
		"\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36"+
		"\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3%\5%\u0130\n%"+
		"\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3*\3+\3+\3+\3+\3,\3,\3,\3-\3-\3-\3.\3"+
		".\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63"+
		"\3\63\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67\3\67"+
		"\3\67\5\67\u0167\n\67\38\38\38\38\58\u016d\n8\39\39\39\3:\3:\3:\3;\3;"+
		"\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\3A\3A\3B\3B\3C\3C\3D\3D\3D\3E\3E\3E\7E"+
		"\u018d\nE\fE\16E\u0190\13E\3E\3E\3F\3F\3F\3F\3G\3G\7G\u019a\nG\fG\16G"+
		"\u019d\13G\3H\3H\3H\6H\u01a2\nH\rH\16H\u01a3\3I\3I\7I\u01a8\nI\fI\16I"+
		"\u01ab\13I\3J\3J\3J\3J\7J\u01b1\nJ\fJ\16J\u01b4\13J\3J\5J\u01b7\nJ\3J"+
		"\3J\3J\3J\3K\6K\u01be\nK\rK\16K\u01bf\3K\3K\3\u018e\2L\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K"+
		"\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177"+
		"A\u0081B\u0083C\u0085D\u0087\2\u0089E\u008bF\u008dG\u008fH\u0091I\u0093"+
		"J\u0095K\3\2\t\4\2\f\f\17\17\3\2\62;\4\2ZZzz\4\2\62;ch\5\2C\\aac|\7\2"+
		"//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u01cc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
		"\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2"+
		"A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3"+
		"\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2"+
		"\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2"+
		"g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3"+
		"\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3"+
		"\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0089\3\2\2\2"+
		"\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093"+
		"\3\2\2\2\2\u0095\3\2\2\2\3\u0097\3\2\2\2\5\u00a0\3\2\2\2\7\u00a6\3\2\2"+
		"\2\t\u00ab\3\2\2\2\13\u00b4\3\2\2\2\r\u00b8\3\2\2\2\17\u00bb\3\2\2\2\21"+
		"\u00c0\3\2\2\2\23\u00c6\3\2\2\2\25\u00ca\3\2\2\2\27\u00d0\3\2\2\2\31\u00d4"+
		"\3\2\2\2\33\u00d9\3\2\2\2\35\u00dc\3\2\2\2\37\u00df\3\2\2\2!\u00e6\3\2"+
		"\2\2#\u00eb\3\2\2\2%\u00f0\3\2\2\2\'\u00f7\3\2\2\2)\u00fd\3\2\2\2+\u0102"+
		"\3\2\2\2-\u0107\3\2\2\2/\u010c\3\2\2\2\61\u0112\3\2\2\2\63\u0114\3\2\2"+
		"\2\65\u0116\3\2\2\2\67\u0118\3\2\2\29\u011a\3\2\2\2;\u011c\3\2\2\2=\u011e"+
		"\3\2\2\2?\u0120\3\2\2\2A\u0122\3\2\2\2C\u0125\3\2\2\2E\u0127\3\2\2\2G"+
		"\u0129\3\2\2\2I\u012f\3\2\2\2K\u0131\3\2\2\2M\u0133\3\2\2\2O\u0135\3\2"+
		"\2\2Q\u0137\3\2\2\2S\u0139\3\2\2\2U\u013c\3\2\2\2W\u0140\3\2\2\2Y\u0143"+
		"\3\2\2\2[\u0146\3\2\2\2]\u0149\3\2\2\2_\u014b\3\2\2\2a\u014e\3\2\2\2c"+
		"\u0151\3\2\2\2e\u0154\3\2\2\2g\u0158\3\2\2\2i\u015c\3\2\2\2k\u015f\3\2"+
		"\2\2m\u0166\3\2\2\2o\u016c\3\2\2\2q\u016e\3\2\2\2s\u0171\3\2\2\2u\u0174"+
		"\3\2\2\2w\u0176\3\2\2\2y\u0178\3\2\2\2{\u017a\3\2\2\2}\u017c\3\2\2\2\177"+
		"\u017e\3\2\2\2\u0081\u0180\3\2\2\2\u0083\u0182\3\2\2\2\u0085\u0184\3\2"+
		"\2\2\u0087\u0186\3\2\2\2\u0089\u0189\3\2\2\2\u008b\u0193\3\2\2\2\u008d"+
		"\u0197\3\2\2\2\u008f\u019e\3\2\2\2\u0091\u01a5\3\2\2\2\u0093\u01ac\3\2"+
		"\2\2\u0095\u01bd\3\2\2\2\u0097\u0098\7c\2\2\u0098\u0099\7n\2\2\u0099\u009a"+
		"\7n\2\2\u009a\u009b\7q\2\2\u009b\u009c\7e\2\2\u009c\u009d\7c\2\2\u009d"+
		"\u009e\7v\2\2\u009e\u009f\7g\2\2\u009f\4\3\2\2\2\u00a0\u00a1\7d\2\2\u00a1"+
		"\u00a2\7t\2\2\u00a2\u00a3\7g\2\2\u00a3\u00a4\7c\2\2\u00a4\u00a5\7m\2\2"+
		"\u00a5\6\3\2\2\2\u00a6\u00a7\7e\2\2\u00a7\u00a8\7c\2\2\u00a8\u00a9\7u"+
		"\2\2\u00a9\u00aa\7g\2\2\u00aa\b\3\2\2\2\u00ab\u00ac\7e\2\2\u00ac\u00ad"+
		"\7q\2\2\u00ad\u00ae\7p\2\2\u00ae\u00af\7v\2\2\u00af\u00b0\7k\2\2\u00b0"+
		"\u00b1\7p\2\2\u00b1\u00b2\7w\2\2\u00b2\u00b3\7g\2\2\u00b3\n\3\2\2\2\u00b4"+
		"\u00b5\7f\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7h\2\2\u00b7\f\3\2\2\2\u00b8"+
		"\u00b9\7f\2\2\u00b9\u00ba\7q\2\2\u00ba\16\3\2\2\2\u00bb\u00bc\7g\2\2\u00bc"+
		"\u00bd\7n\2\2\u00bd\u00be\7u\2\2\u00be\u00bf\7g\2\2\u00bf\20\3\2\2\2\u00c0"+
		"\u00c1\7g\2\2\u00c1\u00c2\7n\2\2\u00c2\u00c3\7u\2\2\u00c3\u00c4\7k\2\2"+
		"\u00c4\u00c5\7h\2\2\u00c5\22\3\2\2\2\u00c6\u00c7\7g\2\2\u00c7\u00c8\7"+
		"p\2\2\u00c8\u00c9\7f\2\2\u00c9\24\3\2\2\2\u00ca\u00cb\7h\2\2\u00cb\u00cc"+
		"\7c\2\2\u00cc\u00cd\7n\2\2\u00cd\u00ce\7u\2\2\u00ce\u00cf\7g\2\2\u00cf"+
		"\26\3\2\2\2\u00d0\u00d1\7h\2\2\u00d1\u00d2\7q\2\2\u00d2\u00d3\7t\2\2\u00d3"+
		"\30\3\2\2\2\u00d4\u00d5\7j\2\2\u00d5\u00d6\7g\2\2\u00d6\u00d7\7c\2\2\u00d7"+
		"\u00d8\7r\2\2\u00d8\32\3\2\2\2\u00d9\u00da\7k\2\2\u00da\u00db\7h\2\2\u00db"+
		"\34\3\2\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de\7p\2\2\u00de\36\3\2\2\2\u00df"+
		"\u00e0\7k\2\2\u00e0\u00e1\7p\2\2\u00e1\u00e2\7n\2\2\u00e2\u00e3\7k\2\2"+
		"\u00e3\u00e4\7p\2\2\u00e4\u00e5\7g\2\2\u00e5 \3\2\2\2\u00e6\u00e7\7n\2"+
		"\2\u00e7\u00e8\7q\2\2\u00e8\u00e9\7q\2\2\u00e9\u00ea\7r\2\2\u00ea\"\3"+
		"\2\2\2\u00eb\u00ec\7p\2\2\u00ec\u00ed\7w\2\2\u00ed\u00ee\7n\2\2\u00ee"+
		"\u00ef\7n\2\2\u00ef$\3\2\2\2\u00f0\u00f1\7u\2\2\u00f1\u00f2\7g\2\2\u00f2"+
		"\u00f3\7p\2\2\u00f3\u00f4\7u\2\2\u00f4\u00f5\7q\2\2\u00f5\u00f6\7t\2\2"+
		"\u00f6&\3\2\2\2\u00f7\u00f8\7u\2\2\u00f8\u00f9\7v\2\2\u00f9\u00fa\7c\2"+
		"\2\u00fa\u00fb\7e\2\2\u00fb\u00fc\7m\2\2\u00fc(\3\2\2\2\u00fd\u00fe\7"+
		"v\2\2\u00fe\u00ff\7j\2\2\u00ff\u0100\7g\2\2\u0100\u0101\7p\2\2\u0101*"+
		"\3\2\2\2\u0102\u0103\7v\2\2\u0103\u0104\7t\2\2\u0104\u0105\7w\2\2\u0105"+
		"\u0106\7g\2\2\u0106,\3\2\2\2\u0107\u0108\7y\2\2\u0108\u0109\7j\2\2\u0109"+
		"\u010a\7g\2\2\u010a\u010b\7p\2\2\u010b.\3\2\2\2\u010c\u010d\7y\2\2\u010d"+
		"\u010e\7j\2\2\u010e\u010f\7k\2\2\u010f\u0110\7n\2\2\u0110\u0111\7g\2\2"+
		"\u0111\60\3\2\2\2\u0112\u0113\7?\2\2\u0113\62\3\2\2\2\u0114\u0115\7B\2"+
		"\2\u0115\64\3\2\2\2\u0116\u0117\7<\2\2\u0117\66\3\2\2\2\u0118\u0119\7"+
		".\2\2\u01198\3\2\2\2\u011a\u011b\7\61\2\2\u011b:\3\2\2\2\u011c\u011d\7"+
		"^\2\2\u011d<\3\2\2\2\u011e\u011f\7&\2\2\u011f>\3\2\2\2\u0120\u0121\7\60"+
		"\2\2\u0121@\3\2\2\2\u0122\u0123\7,\2\2\u0123\u0124\7,\2\2\u0124B\3\2\2"+
		"\2\u0125\u0126\7/\2\2\u0126D\3\2\2\2\u0127\u0128\7\'\2\2\u0128F\3\2\2"+
		"\2\u0129\u012a\7,\2\2\u012aH\3\2\2\2\u012b\u0130\7#\2\2\u012c\u012d\7"+
		"p\2\2\u012d\u012e\7q\2\2\u012e\u0130\7v\2\2\u012f\u012b\3\2\2\2\u012f"+
		"\u012c\3\2\2\2\u0130J\3\2\2\2\u0131\u0132\7\u0080\2\2\u0132L\3\2\2\2\u0133"+
		"\u0134\7-\2\2\u0134N\3\2\2\2\u0135\u0136\7A\2\2\u0136P\3\2\2\2\u0137\u0138"+
		"\7=\2\2\u0138R\3\2\2\2\u0139\u013a\7\61\2\2\u013a\u013b\7?\2\2\u013bT"+
		"\3\2\2\2\u013c\u013d\7,\2\2\u013d\u013e\7,\2\2\u013e\u013f\7?\2\2\u013f"+
		"V\3\2\2\2\u0140\u0141\7/\2\2\u0141\u0142\7?\2\2\u0142X\3\2\2\2\u0143\u0144"+
		"\7,\2\2\u0144\u0145\7?\2\2\u0145Z\3\2\2\2\u0146\u0147\7-\2\2\u0147\u0148"+
		"\7?\2\2\u0148\\\3\2\2\2\u0149\u014a\7>\2\2\u014a^\3\2\2\2\u014b\u014c"+
		"\7>\2\2\u014c\u014d\7?\2\2\u014d`\3\2\2\2\u014e\u014f\7#\2\2\u014f\u0150"+
		"\7?\2\2\u0150b\3\2\2\2\u0151\u0152\7?\2\2\u0152\u0153\7?\2\2\u0153d\3"+
		"\2\2\2\u0154\u0155\7?\2\2\u0155\u0156\7?\2\2\u0156\u0157\7?\2\2\u0157"+
		"f\3\2\2\2\u0158\u0159\7#\2\2\u0159\u015a\7?\2\2\u015a\u015b\7?\2\2\u015b"+
		"h\3\2\2\2\u015c\u015d\7@\2\2\u015d\u015e\7?\2\2\u015ej\3\2\2\2\u015f\u0160"+
		"\7@\2\2\u0160l\3\2\2\2\u0161\u0162\7(\2\2\u0162\u0167\7(\2\2\u0163\u0164"+
		"\7c\2\2\u0164\u0165\7p\2\2\u0165\u0167\7f\2\2\u0166\u0161\3\2\2\2\u0166"+
		"\u0163\3\2\2\2\u0167n\3\2\2\2\u0168\u0169\7~\2\2\u0169\u016d\7~\2\2\u016a"+
		"\u016b\7q\2\2\u016b\u016d\7t\2\2\u016c\u0168\3\2\2\2\u016c\u016a\3\2\2"+
		"\2\u016dp\3\2\2\2\u016e\u016f\7>\2\2\u016f\u0170\7>\2\2\u0170r\3\2\2\2"+
		"\u0171\u0172\7@\2\2\u0172\u0173\7@\2\2\u0173t\3\2\2\2\u0174\u0175\7(\2"+
		"\2\u0175v\3\2\2\2\u0176\u0177\7~\2\2\u0177x\3\2\2\2\u0178\u0179\7`\2\2"+
		"\u0179z\3\2\2\2\u017a\u017b\7]\2\2\u017b|\3\2\2\2\u017c\u017d\7_\2\2\u017d"+
		"~\3\2\2\2\u017e\u017f\7*\2\2\u017f\u0080\3\2\2\2\u0180\u0181\7+\2\2\u0181"+
		"\u0082\3\2\2\2\u0182\u0183\7}\2\2\u0183\u0084\3\2\2\2\u0184\u0185\7\177"+
		"\2\2\u0185\u0086\3\2\2\2\u0186\u0187\7^\2\2\u0187\u0188\7$\2\2\u0188\u0088"+
		"\3\2\2\2\u0189\u018e\7$\2\2\u018a\u018d\5\u0087D\2\u018b\u018d\n\2\2\2"+
		"\u018c\u018a\3\2\2\2\u018c\u018b\3\2\2\2\u018d\u0190\3\2\2\2\u018e\u018f"+
		"\3\2\2\2\u018e\u018c\3\2\2\2\u018f\u0191\3\2\2\2\u0190\u018e\3\2\2\2\u0191"+
		"\u0192\7$\2\2\u0192\u008a\3\2\2\2\u0193\u0194\5\u008dG\2\u0194\u0195\5"+
		"? \2\u0195\u0196\5\u008dG\2\u0196\u008c\3\2\2\2\u0197\u019b\t\3\2\2\u0198"+
		"\u019a\t\3\2\2\u0199\u0198\3\2\2\2\u019a\u019d\3\2\2\2\u019b\u0199\3\2"+
		"\2\2\u019b\u019c\3\2\2\2\u019c\u008e\3\2\2\2\u019d\u019b\3\2\2\2\u019e"+
		"\u019f\7\62\2\2\u019f\u01a1\t\4\2\2\u01a0\u01a2\t\5\2\2\u01a1\u01a0\3"+
		"\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a1\3\2\2\2\u01a3\u01a4\3\2\2\2\u01a4"+
		"\u0090\3\2\2\2\u01a5\u01a9\t\6\2\2\u01a6\u01a8\t\7\2\2\u01a7\u01a6\3\2"+
		"\2\2\u01a8\u01ab\3\2\2\2\u01a9\u01a7\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa"+
		"\u0092\3\2\2\2\u01ab\u01a9\3\2\2\2\u01ac\u01ad\7\61\2\2\u01ad\u01ae\7"+
		"\61\2\2\u01ae\u01b2\3\2\2\2\u01af\u01b1\n\2\2\2\u01b0\u01af\3\2\2\2\u01b1"+
		"\u01b4\3\2\2\2\u01b2\u01b0\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u01b6\3\2"+
		"\2\2\u01b4\u01b2\3\2\2\2\u01b5\u01b7\7\17\2\2\u01b6\u01b5\3\2\2\2\u01b6"+
		"\u01b7\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01b9\7\f\2\2\u01b9\u01ba\3\2"+
		"\2\2\u01ba\u01bb\bJ\2\2\u01bb\u0094\3\2\2\2\u01bc\u01be\t\b\2\2\u01bd"+
		"\u01bc\3\2\2\2\u01be\u01bf\3\2\2\2\u01bf\u01bd\3\2\2\2\u01bf\u01c0\3\2"+
		"\2\2\u01c0\u01c1\3\2\2\2\u01c1\u01c2\bK\2\2\u01c2\u0096\3\2\2\2\16\2\u012f"+
		"\u0166\u016c\u018c\u018e\u019b\u01a3\u01a9\u01b2\u01b6\u01bf\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}