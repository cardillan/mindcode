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
		ALLOCATE=1, BREAK=2, CASE=3, CONST=4, CONTINUE=5, DEF=6, DO=7, ELSE=8, 
		ELSIF=9, END=10, FALSE=11, FOR=12, HEAP=13, IF=14, IN=15, INLINE=16, LOOP=17, 
		NULL=18, PRINTF=19, RETURN=20, SENSOR=21, STACK=22, THEN=23, TRUE=24, 
		WHEN=25, WHILE=26, ASSIGN=27, AT=28, COLON=29, COMMA=30, DIV=31, IDIV=32, 
		DOLLAR=33, DOT=34, EXP=35, MINUS=36, MOD=37, MUL=38, NOT=39, BITWISE_NOT=40, 
		PLUS=41, QUESTION_MARK=42, SEMICOLON=43, EXP_ASSIGN=44, MUL_ASSIGN=45, 
		DIV_ASSIGN=46, IDIV_ASSIGN=47, MOD_ASSIGN=48, PLUS_ASSIGN=49, MINUS_ASSIGN=50, 
		SHIFT_LEFT_ASSIGN=51, SHIFT_RIGHT_ASSIGN=52, BITWISE_AND_ASSIGN=53, BITWISE_OR_ASSIGN=54, 
		BITWISE_XOR_ASSIGN=55, AND_ASSIGN=56, OR_ASSIGN=57, LESS_THAN=58, LESS_THAN_EQUAL=59, 
		NOT_EQUAL=60, EQUAL=61, STRICT_EQUAL=62, STRICT_NOT_EQUAL=63, GREATER_THAN_EQUAL=64, 
		GREATER_THAN=65, AND=66, OR=67, SHIFT_LEFT=68, SHIFT_RIGHT=69, BITWISE_AND=70, 
		BITWISE_OR=71, BITWISE_XOR=72, LEFT_SBRACKET=73, RIGHT_SBRACKET=74, LEFT_RBRACKET=75, 
		RIGHT_RBRACKET=76, LEFT_CBRACKET=77, RIGHT_CBRACKET=78, LITERAL=79, FLOAT=80, 
		INT=81, HEXINT=82, ID=83, SL_COMMENT=84, WS=85;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ALLOCATE", "BREAK", "CASE", "CONST", "CONTINUE", "DEF", "DO", "ELSE", 
			"ELSIF", "END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", "LOOP", 
			"NULL", "PRINTF", "RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "EXP_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "IDIV_ASSIGN", 
			"MOD_ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "SHIFT_LEFT_ASSIGN", "SHIFT_RIGHT_ASSIGN", 
			"BITWISE_AND_ASSIGN", "BITWISE_OR_ASSIGN", "BITWISE_XOR_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", 
			"SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", "LITERAL", "FLOAT", 
			"INT", "HEXINT", "ID", "SL_COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'allocate'", "'break'", "'case'", "'const'", "'continue'", "'def'", 
			"'do'", "'else'", "'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", 
			"'in'", "'inline'", "'loop'", "'null'", "'printf'", "'return'", "'sensor'", 
			"'stack'", "'then'", "'true'", "'when'", "'while'", "'='", "'@'", "':'", 
			"','", "'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, 
			"'~'", "'+'", "'?'", "';'", "'**='", "'*='", "'/='", "'\\='", "'%='", 
			"'+='", "'-='", "'<<='", "'>>='", "'&='", "'|='", "'^='", "'&&='", "'||='", 
			"'<'", "'<='", "'!='", "'=='", "'==='", "'!=='", "'>='", "'>'", null, 
			null, "'<<'", "'>>'", "'&'", "'|'", "'^'", "'['", "']'", "'('", "')'", 
			"'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONST", "CONTINUE", "DEF", "DO", 
			"ELSE", "ELSIF", "END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", 
			"LOOP", "NULL", "PRINTF", "RETURN", "SENSOR", "STACK", "THEN", "TRUE", 
			"WHEN", "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", 
			"DOT", "EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "EXP_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "IDIV_ASSIGN", 
			"MOD_ASSIGN", "PLUS_ASSIGN", "MINUS_ASSIGN", "SHIFT_LEFT_ASSIGN", "SHIFT_RIGHT_ASSIGN", 
			"BITWISE_AND_ASSIGN", "BITWISE_OR_ASSIGN", "BITWISE_XOR_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
			"STRICT_NOT_EQUAL", "GREATER_THAN_EQUAL", "GREATER_THAN", "AND", "OR", 
			"SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND", "BITWISE_OR", "BITWISE_XOR", 
			"LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET", "RIGHT_RBRACKET", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2W\u020e\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3"+
		"\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36"+
		"\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3"+
		"(\3(\3(\5(\u015c\n(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3-\3-\3.\3.\3.\3/\3"+
		"/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64"+
		"\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3\67\38\3"+
		"8\38\39\39\39\39\3:\3:\3:\3:\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3>\3?\3?\3"+
		"?\3?\3@\3@\3@\3@\3A\3A\3A\3B\3B\3C\3C\3C\3C\3C\5C\u01b2\nC\3D\3D\3D\3"+
		"D\5D\u01b8\nD\3E\3E\3E\3F\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L\3"+
		"M\3M\3N\3N\3O\3O\3P\3P\3P\3Q\3Q\3Q\7Q\u01d8\nQ\fQ\16Q\u01db\13Q\3Q\3Q"+
		"\3R\3R\3R\3R\3S\3S\7S\u01e5\nS\fS\16S\u01e8\13S\3T\3T\3T\6T\u01ed\nT\r"+
		"T\16T\u01ee\3U\3U\7U\u01f3\nU\fU\16U\u01f6\13U\3V\3V\3V\3V\7V\u01fc\n"+
		"V\fV\16V\u01ff\13V\3V\5V\u0202\nV\3V\3V\3V\3V\3W\6W\u0209\nW\rW\16W\u020a"+
		"\3W\3W\3\u01d9\2X\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65"+
		"\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64"+
		"g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089"+
		"F\u008bG\u008dH\u008fI\u0091J\u0093K\u0095L\u0097M\u0099N\u009bO\u009d"+
		"P\u009f\2\u00a1Q\u00a3R\u00a5S\u00a7T\u00a9U\u00abV\u00adW\3\2\t\4\2\f"+
		"\f\17\17\3\2\62;\4\2ZZzz\4\2\62;ch\5\2C\\aac|\7\2//\62;C\\aac|\5\2\13"+
		"\f\17\17\"\"\2\u0217\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2"+
		"\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2"+
		"\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q"+
		"\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2"+
		"\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w"+
		"\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2"+
		"\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d"+
		"\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2"+
		"\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\3\u00af\3\2\2\2\5\u00b8"+
		"\3\2\2\2\7\u00be\3\2\2\2\t\u00c3\3\2\2\2\13\u00c9\3\2\2\2\r\u00d2\3\2"+
		"\2\2\17\u00d6\3\2\2\2\21\u00d9\3\2\2\2\23\u00de\3\2\2\2\25\u00e4\3\2\2"+
		"\2\27\u00e8\3\2\2\2\31\u00ee\3\2\2\2\33\u00f2\3\2\2\2\35\u00f7\3\2\2\2"+
		"\37\u00fa\3\2\2\2!\u00fd\3\2\2\2#\u0104\3\2\2\2%\u0109\3\2\2\2\'\u010e"+
		"\3\2\2\2)\u0115\3\2\2\2+\u011c\3\2\2\2-\u0123\3\2\2\2/\u0129\3\2\2\2\61"+
		"\u012e\3\2\2\2\63\u0133\3\2\2\2\65\u0138\3\2\2\2\67\u013e\3\2\2\29\u0140"+
		"\3\2\2\2;\u0142\3\2\2\2=\u0144\3\2\2\2?\u0146\3\2\2\2A\u0148\3\2\2\2C"+
		"\u014a\3\2\2\2E\u014c\3\2\2\2G\u014e\3\2\2\2I\u0151\3\2\2\2K\u0153\3\2"+
		"\2\2M\u0155\3\2\2\2O\u015b\3\2\2\2Q\u015d\3\2\2\2S\u015f\3\2\2\2U\u0161"+
		"\3\2\2\2W\u0163\3\2\2\2Y\u0165\3\2\2\2[\u0169\3\2\2\2]\u016c\3\2\2\2_"+
		"\u016f\3\2\2\2a\u0172\3\2\2\2c\u0175\3\2\2\2e\u0178\3\2\2\2g\u017b\3\2"+
		"\2\2i\u017f\3\2\2\2k\u0183\3\2\2\2m\u0186\3\2\2\2o\u0189\3\2\2\2q\u018c"+
		"\3\2\2\2s\u0190\3\2\2\2u\u0194\3\2\2\2w\u0196\3\2\2\2y\u0199\3\2\2\2{"+
		"\u019c\3\2\2\2}\u019f\3\2\2\2\177\u01a3\3\2\2\2\u0081\u01a7\3\2\2\2\u0083"+
		"\u01aa\3\2\2\2\u0085\u01b1\3\2\2\2\u0087\u01b7\3\2\2\2\u0089\u01b9\3\2"+
		"\2\2\u008b\u01bc\3\2\2\2\u008d\u01bf\3\2\2\2\u008f\u01c1\3\2\2\2\u0091"+
		"\u01c3\3\2\2\2\u0093\u01c5\3\2\2\2\u0095\u01c7\3\2\2\2\u0097\u01c9\3\2"+
		"\2\2\u0099\u01cb\3\2\2\2\u009b\u01cd\3\2\2\2\u009d\u01cf\3\2\2\2\u009f"+
		"\u01d1\3\2\2\2\u00a1\u01d4\3\2\2\2\u00a3\u01de\3\2\2\2\u00a5\u01e2\3\2"+
		"\2\2\u00a7\u01e9\3\2\2\2\u00a9\u01f0\3\2\2\2\u00ab\u01f7\3\2\2\2\u00ad"+
		"\u0208\3\2\2\2\u00af\u00b0\7c\2\2\u00b0\u00b1\7n\2\2\u00b1\u00b2\7n\2"+
		"\2\u00b2\u00b3\7q\2\2\u00b3\u00b4\7e\2\2\u00b4\u00b5\7c\2\2\u00b5\u00b6"+
		"\7v\2\2\u00b6\u00b7\7g\2\2\u00b7\4\3\2\2\2\u00b8\u00b9\7d\2\2\u00b9\u00ba"+
		"\7t\2\2\u00ba\u00bb\7g\2\2\u00bb\u00bc\7c\2\2\u00bc\u00bd\7m\2\2\u00bd"+
		"\6\3\2\2\2\u00be\u00bf\7e\2\2\u00bf\u00c0\7c\2\2\u00c0\u00c1\7u\2\2\u00c1"+
		"\u00c2\7g\2\2\u00c2\b\3\2\2\2\u00c3\u00c4\7e\2\2\u00c4\u00c5\7q\2\2\u00c5"+
		"\u00c6\7p\2\2\u00c6\u00c7\7u\2\2\u00c7\u00c8\7v\2\2\u00c8\n\3\2\2\2\u00c9"+
		"\u00ca\7e\2\2\u00ca\u00cb\7q\2\2\u00cb\u00cc\7p\2\2\u00cc\u00cd\7v\2\2"+
		"\u00cd\u00ce\7k\2\2\u00ce\u00cf\7p\2\2\u00cf\u00d0\7w\2\2\u00d0\u00d1"+
		"\7g\2\2\u00d1\f\3\2\2\2\u00d2\u00d3\7f\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5"+
		"\7h\2\2\u00d5\16\3\2\2\2\u00d6\u00d7\7f\2\2\u00d7\u00d8\7q\2\2\u00d8\20"+
		"\3\2\2\2\u00d9\u00da\7g\2\2\u00da\u00db\7n\2\2\u00db\u00dc\7u\2\2\u00dc"+
		"\u00dd\7g\2\2\u00dd\22\3\2\2\2\u00de\u00df\7g\2\2\u00df\u00e0\7n\2\2\u00e0"+
		"\u00e1\7u\2\2\u00e1\u00e2\7k\2\2\u00e2\u00e3\7h\2\2\u00e3\24\3\2\2\2\u00e4"+
		"\u00e5\7g\2\2\u00e5\u00e6\7p\2\2\u00e6\u00e7\7f\2\2\u00e7\26\3\2\2\2\u00e8"+
		"\u00e9\7h\2\2\u00e9\u00ea\7c\2\2\u00ea\u00eb\7n\2\2\u00eb\u00ec\7u\2\2"+
		"\u00ec\u00ed\7g\2\2\u00ed\30\3\2\2\2\u00ee\u00ef\7h\2\2\u00ef\u00f0\7"+
		"q\2\2\u00f0\u00f1\7t\2\2\u00f1\32\3\2\2\2\u00f2\u00f3\7j\2\2\u00f3\u00f4"+
		"\7g\2\2\u00f4\u00f5\7c\2\2\u00f5\u00f6\7r\2\2\u00f6\34\3\2\2\2\u00f7\u00f8"+
		"\7k\2\2\u00f8\u00f9\7h\2\2\u00f9\36\3\2\2\2\u00fa\u00fb\7k\2\2\u00fb\u00fc"+
		"\7p\2\2\u00fc \3\2\2\2\u00fd\u00fe\7k\2\2\u00fe\u00ff\7p\2\2\u00ff\u0100"+
		"\7n\2\2\u0100\u0101\7k\2\2\u0101\u0102\7p\2\2\u0102\u0103\7g\2\2\u0103"+
		"\"\3\2\2\2\u0104\u0105\7n\2\2\u0105\u0106\7q\2\2\u0106\u0107\7q\2\2\u0107"+
		"\u0108\7r\2\2\u0108$\3\2\2\2\u0109\u010a\7p\2\2\u010a\u010b\7w\2\2\u010b"+
		"\u010c\7n\2\2\u010c\u010d\7n\2\2\u010d&\3\2\2\2\u010e\u010f\7r\2\2\u010f"+
		"\u0110\7t\2\2\u0110\u0111\7k\2\2\u0111\u0112\7p\2\2\u0112\u0113\7v\2\2"+
		"\u0113\u0114\7h\2\2\u0114(\3\2\2\2\u0115\u0116\7t\2\2\u0116\u0117\7g\2"+
		"\2\u0117\u0118\7v\2\2\u0118\u0119\7w\2\2\u0119\u011a\7t\2\2\u011a\u011b"+
		"\7p\2\2\u011b*\3\2\2\2\u011c\u011d\7u\2\2\u011d\u011e\7g\2\2\u011e\u011f"+
		"\7p\2\2\u011f\u0120\7u\2\2\u0120\u0121\7q\2\2\u0121\u0122\7t\2\2\u0122"+
		",\3\2\2\2\u0123\u0124\7u\2\2\u0124\u0125\7v\2\2\u0125\u0126\7c\2\2\u0126"+
		"\u0127\7e\2\2\u0127\u0128\7m\2\2\u0128.\3\2\2\2\u0129\u012a\7v\2\2\u012a"+
		"\u012b\7j\2\2\u012b\u012c\7g\2\2\u012c\u012d\7p\2\2\u012d\60\3\2\2\2\u012e"+
		"\u012f\7v\2\2\u012f\u0130\7t\2\2\u0130\u0131\7w\2\2\u0131\u0132\7g\2\2"+
		"\u0132\62\3\2\2\2\u0133\u0134\7y\2\2\u0134\u0135\7j\2\2\u0135\u0136\7"+
		"g\2\2\u0136\u0137\7p\2\2\u0137\64\3\2\2\2\u0138\u0139\7y\2\2\u0139\u013a"+
		"\7j\2\2\u013a\u013b\7k\2\2\u013b\u013c\7n\2\2\u013c\u013d\7g\2\2\u013d"+
		"\66\3\2\2\2\u013e\u013f\7?\2\2\u013f8\3\2\2\2\u0140\u0141\7B\2\2\u0141"+
		":\3\2\2\2\u0142\u0143\7<\2\2\u0143<\3\2\2\2\u0144\u0145\7.\2\2\u0145>"+
		"\3\2\2\2\u0146\u0147\7\61\2\2\u0147@\3\2\2\2\u0148\u0149\7^\2\2\u0149"+
		"B\3\2\2\2\u014a\u014b\7&\2\2\u014bD\3\2\2\2\u014c\u014d\7\60\2\2\u014d"+
		"F\3\2\2\2\u014e\u014f\7,\2\2\u014f\u0150\7,\2\2\u0150H\3\2\2\2\u0151\u0152"+
		"\7/\2\2\u0152J\3\2\2\2\u0153\u0154\7\'\2\2\u0154L\3\2\2\2\u0155\u0156"+
		"\7,\2\2\u0156N\3\2\2\2\u0157\u015c\7#\2\2\u0158\u0159\7p\2\2\u0159\u015a"+
		"\7q\2\2\u015a\u015c\7v\2\2\u015b\u0157\3\2\2\2\u015b\u0158\3\2\2\2\u015c"+
		"P\3\2\2\2\u015d\u015e\7\u0080\2\2\u015eR\3\2\2\2\u015f\u0160\7-\2\2\u0160"+
		"T\3\2\2\2\u0161\u0162\7A\2\2\u0162V\3\2\2\2\u0163\u0164\7=\2\2\u0164X"+
		"\3\2\2\2\u0165\u0166\7,\2\2\u0166\u0167\7,\2\2\u0167\u0168\7?\2\2\u0168"+
		"Z\3\2\2\2\u0169\u016a\7,\2\2\u016a\u016b\7?\2\2\u016b\\\3\2\2\2\u016c"+
		"\u016d\7\61\2\2\u016d\u016e\7?\2\2\u016e^\3\2\2\2\u016f\u0170\7^\2\2\u0170"+
		"\u0171\7?\2\2\u0171`\3\2\2\2\u0172\u0173\7\'\2\2\u0173\u0174\7?\2\2\u0174"+
		"b\3\2\2\2\u0175\u0176\7-\2\2\u0176\u0177\7?\2\2\u0177d\3\2\2\2\u0178\u0179"+
		"\7/\2\2\u0179\u017a\7?\2\2\u017af\3\2\2\2\u017b\u017c\7>\2\2\u017c\u017d"+
		"\7>\2\2\u017d\u017e\7?\2\2\u017eh\3\2\2\2\u017f\u0180\7@\2\2\u0180\u0181"+
		"\7@\2\2\u0181\u0182\7?\2\2\u0182j\3\2\2\2\u0183\u0184\7(\2\2\u0184\u0185"+
		"\7?\2\2\u0185l\3\2\2\2\u0186\u0187\7~\2\2\u0187\u0188\7?\2\2\u0188n\3"+
		"\2\2\2\u0189\u018a\7`\2\2\u018a\u018b\7?\2\2\u018bp\3\2\2\2\u018c\u018d"+
		"\7(\2\2\u018d\u018e\7(\2\2\u018e\u018f\7?\2\2\u018fr\3\2\2\2\u0190\u0191"+
		"\7~\2\2\u0191\u0192\7~\2\2\u0192\u0193\7?\2\2\u0193t\3\2\2\2\u0194\u0195"+
		"\7>\2\2\u0195v\3\2\2\2\u0196\u0197\7>\2\2\u0197\u0198\7?\2\2\u0198x\3"+
		"\2\2\2\u0199\u019a\7#\2\2\u019a\u019b\7?\2\2\u019bz\3\2\2\2\u019c\u019d"+
		"\7?\2\2\u019d\u019e\7?\2\2\u019e|\3\2\2\2\u019f\u01a0\7?\2\2\u01a0\u01a1"+
		"\7?\2\2\u01a1\u01a2\7?\2\2\u01a2~\3\2\2\2\u01a3\u01a4\7#\2\2\u01a4\u01a5"+
		"\7?\2\2\u01a5\u01a6\7?\2\2\u01a6\u0080\3\2\2\2\u01a7\u01a8\7@\2\2\u01a8"+
		"\u01a9\7?\2\2\u01a9\u0082\3\2\2\2\u01aa\u01ab\7@\2\2\u01ab\u0084\3\2\2"+
		"\2\u01ac\u01ad\7(\2\2\u01ad\u01b2\7(\2\2\u01ae\u01af\7c\2\2\u01af\u01b0"+
		"\7p\2\2\u01b0\u01b2\7f\2\2\u01b1\u01ac\3\2\2\2\u01b1\u01ae\3\2\2\2\u01b2"+
		"\u0086\3\2\2\2\u01b3\u01b4\7~\2\2\u01b4\u01b8\7~\2\2\u01b5\u01b6\7q\2"+
		"\2\u01b6\u01b8\7t\2\2\u01b7\u01b3\3\2\2\2\u01b7\u01b5\3\2\2\2\u01b8\u0088"+
		"\3\2\2\2\u01b9\u01ba\7>\2\2\u01ba\u01bb\7>\2\2\u01bb\u008a\3\2\2\2\u01bc"+
		"\u01bd\7@\2\2\u01bd\u01be\7@\2\2\u01be\u008c\3\2\2\2\u01bf\u01c0\7(\2"+
		"\2\u01c0\u008e\3\2\2\2\u01c1\u01c2\7~\2\2\u01c2\u0090\3\2\2\2\u01c3\u01c4"+
		"\7`\2\2\u01c4\u0092\3\2\2\2\u01c5\u01c6\7]\2\2\u01c6\u0094\3\2\2\2\u01c7"+
		"\u01c8\7_\2\2\u01c8\u0096\3\2\2\2\u01c9\u01ca\7*\2\2\u01ca\u0098\3\2\2"+
		"\2\u01cb\u01cc\7+\2\2\u01cc\u009a\3\2\2\2\u01cd\u01ce\7}\2\2\u01ce\u009c"+
		"\3\2\2\2\u01cf\u01d0\7\177\2\2\u01d0\u009e\3\2\2\2\u01d1\u01d2\7^\2\2"+
		"\u01d2\u01d3\7$\2\2\u01d3\u00a0\3\2\2\2\u01d4\u01d9\7$\2\2\u01d5\u01d8"+
		"\5\u009fP\2\u01d6\u01d8\n\2\2\2\u01d7\u01d5\3\2\2\2\u01d7\u01d6\3\2\2"+
		"\2\u01d8\u01db\3\2\2\2\u01d9\u01da\3\2\2\2\u01d9\u01d7\3\2\2\2\u01da\u01dc"+
		"\3\2\2\2\u01db\u01d9\3\2\2\2\u01dc\u01dd\7$\2\2\u01dd\u00a2\3\2\2\2\u01de"+
		"\u01df\5\u00a5S\2\u01df\u01e0\5E#\2\u01e0\u01e1\5\u00a5S\2\u01e1\u00a4"+
		"\3\2\2\2\u01e2\u01e6\t\3\2\2\u01e3\u01e5\t\3\2\2\u01e4\u01e3\3\2\2\2\u01e5"+
		"\u01e8\3\2\2\2\u01e6\u01e4\3\2\2\2\u01e6\u01e7\3\2\2\2\u01e7\u00a6\3\2"+
		"\2\2\u01e8\u01e6\3\2\2\2\u01e9\u01ea\7\62\2\2\u01ea\u01ec\t\4\2\2\u01eb"+
		"\u01ed\t\5\2\2\u01ec\u01eb\3\2\2\2\u01ed\u01ee\3\2\2\2\u01ee\u01ec\3\2"+
		"\2\2\u01ee\u01ef\3\2\2\2\u01ef\u00a8\3\2\2\2\u01f0\u01f4\t\6\2\2\u01f1"+
		"\u01f3\t\7\2\2\u01f2\u01f1\3\2\2\2\u01f3\u01f6\3\2\2\2\u01f4\u01f2\3\2"+
		"\2\2\u01f4\u01f5\3\2\2\2\u01f5\u00aa\3\2\2\2\u01f6\u01f4\3\2\2\2\u01f7"+
		"\u01f8\7\61\2\2\u01f8\u01f9\7\61\2\2\u01f9\u01fd\3\2\2\2\u01fa\u01fc\n"+
		"\2\2\2\u01fb\u01fa\3\2\2\2\u01fc\u01ff\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd"+
		"\u01fe\3\2\2\2\u01fe\u0201\3\2\2\2\u01ff\u01fd\3\2\2\2\u0200\u0202\7\17"+
		"\2\2\u0201\u0200\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0203\3\2\2\2\u0203"+
		"\u0204\7\f\2\2\u0204\u0205\3\2\2\2\u0205\u0206\bV\2\2\u0206\u00ac\3\2"+
		"\2\2\u0207\u0209\t\b\2\2\u0208\u0207\3\2\2\2\u0209\u020a\3\2\2\2\u020a"+
		"\u0208\3\2\2\2\u020a\u020b\3\2\2\2\u020b\u020c\3\2\2\2\u020c\u020d\bW"+
		"\2\2\u020d\u00ae\3\2\2\2\16\2\u015b\u01b1\u01b7\u01d7\u01d9\u01e6\u01ee"+
		"\u01f4\u01fd\u0201\u020a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}