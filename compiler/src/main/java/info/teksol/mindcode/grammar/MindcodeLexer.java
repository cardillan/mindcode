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
		RETURN=18, SENSOR=19, STACK=20, THEN=21, TRUE=22, WHEN=23, WHILE=24, ASSIGN=25, 
		AT=26, COLON=27, COMMA=28, DIV=29, IDIV=30, DOLLAR=31, DOT=32, EXP=33, 
		MINUS=34, MOD=35, MUL=36, NOT=37, BITWISE_NOT=38, PLUS=39, QUESTION_MARK=40, 
		SEMICOLON=41, DIV_ASSIGN=42, EXP_ASSIGN=43, MINUS_ASSIGN=44, MUL_ASSIGN=45, 
		PLUS_ASSIGN=46, LESS_THAN=47, LESS_THAN_EQUAL=48, NOT_EQUAL=49, EQUAL=50, 
		STRICT_EQUAL=51, STRICT_NOT_EQUAL=52, GREATER_THAN_EQUAL=53, GREATER_THAN=54, 
		AND=55, OR=56, SHIFT_LEFT=57, SHIFT_RIGHT=58, BITWISE_AND=59, BITWISE_OR=60, 
		BITWISE_XOR=61, LEFT_SBRACKET=62, RIGHT_SBRACKET=63, LEFT_RBRACKET=64, 
		RIGHT_RBRACKET=65, LEFT_CBRACKET=66, RIGHT_CBRACKET=67, LITERAL=68, FLOAT=69, 
		INT=70, HEXINT=71, ID=72, SL_COMMENT=73, WS=74;
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
			"RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", "WHILE", "ASSIGN", 
			"AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", "MINUS", 
			"MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", 
			"DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", 
			"LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
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
			null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'do'", 
			"'else'", "'elsif'", "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", 
			"'inline'", "'loop'", "'null'", "'return'", "'sensor'", "'stack'", "'then'", 
			"'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", "'/'", "'\\'", 
			"'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, "'~'", "'+'", "'?'", 
			"';'", "'/='", "'**='", "'-='", "'*='", "'+='", "'<'", "'<='", "'!='", 
			"'=='", "'==='", "'!=='", "'>='", "'>'", null, null, "'<<'", "'>>'", 
			"'&'", "'|'", "'^'", "'['", "']'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "DO", "ELSE", "ELSIF", 
			"END", "FALSE", "FOR", "HEAP", "IF", "IN", "INLINE", "LOOP", "NULL", 
			"RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", "WHILE", "ASSIGN", 
			"AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", "MINUS", 
			"MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", 
			"DIV_ASSIGN", "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", 
			"LESS_THAN", "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2L\u01cc\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r"+
		"\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3"+
		"\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\33\3"+
		"\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3"+
		"#\3#\3$\3$\3%\3%\3&\3&\3&\3&\5&\u0139\n&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+"+
		"\3+\3+\3,\3,\3,\3,\3-\3-\3-\3.\3.\3.\3/\3/\3/\3\60\3\60\3\61\3\61\3\61"+
		"\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65"+
		"\3\66\3\66\3\66\3\67\3\67\38\38\38\38\38\58\u0170\n8\39\39\39\39\59\u0176"+
		"\n9\3:\3:\3:\3;\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\3A\3A\3B\3B\3C\3C"+
		"\3D\3D\3E\3E\3E\3F\3F\3F\7F\u0196\nF\fF\16F\u0199\13F\3F\3F\3G\3G\3G\3"+
		"G\3H\3H\7H\u01a3\nH\fH\16H\u01a6\13H\3I\3I\3I\6I\u01ab\nI\rI\16I\u01ac"+
		"\3J\3J\7J\u01b1\nJ\fJ\16J\u01b4\13J\3K\3K\3K\3K\7K\u01ba\nK\fK\16K\u01bd"+
		"\13K\3K\5K\u01c0\nK\3K\3K\3K\3K\3L\6L\u01c7\nL\rL\16L\u01c8\3L\3L\3\u0197"+
		"\2M\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36"+
		";\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67"+
		"m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089\2\u008bF\u008d"+
		"G\u008fH\u0091I\u0093J\u0095K\u0097L\3\2\t\4\2\f\f\17\17\3\2\62;\4\2Z"+
		"Zzz\4\2\62;ch\5\2C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u01d5\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2"+
		"\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2"+
		"\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U"+
		"\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2"+
		"\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2"+
		"\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{"+
		"\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085"+
		"\3\2\2\2\2\u0087\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2"+
		"\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\3\u0099"+
		"\3\2\2\2\5\u00a2\3\2\2\2\7\u00a8\3\2\2\2\t\u00ad\3\2\2\2\13\u00b6\3\2"+
		"\2\2\r\u00ba\3\2\2\2\17\u00bd\3\2\2\2\21\u00c2\3\2\2\2\23\u00c8\3\2\2"+
		"\2\25\u00cc\3\2\2\2\27\u00d2\3\2\2\2\31\u00d6\3\2\2\2\33\u00db\3\2\2\2"+
		"\35\u00de\3\2\2\2\37\u00e1\3\2\2\2!\u00e8\3\2\2\2#\u00ed\3\2\2\2%\u00f2"+
		"\3\2\2\2\'\u00f9\3\2\2\2)\u0100\3\2\2\2+\u0106\3\2\2\2-\u010b\3\2\2\2"+
		"/\u0110\3\2\2\2\61\u0115\3\2\2\2\63\u011b\3\2\2\2\65\u011d\3\2\2\2\67"+
		"\u011f\3\2\2\29\u0121\3\2\2\2;\u0123\3\2\2\2=\u0125\3\2\2\2?\u0127\3\2"+
		"\2\2A\u0129\3\2\2\2C\u012b\3\2\2\2E\u012e\3\2\2\2G\u0130\3\2\2\2I\u0132"+
		"\3\2\2\2K\u0138\3\2\2\2M\u013a\3\2\2\2O\u013c\3\2\2\2Q\u013e\3\2\2\2S"+
		"\u0140\3\2\2\2U\u0142\3\2\2\2W\u0145\3\2\2\2Y\u0149\3\2\2\2[\u014c\3\2"+
		"\2\2]\u014f\3\2\2\2_\u0152\3\2\2\2a\u0154\3\2\2\2c\u0157\3\2\2\2e\u015a"+
		"\3\2\2\2g\u015d\3\2\2\2i\u0161\3\2\2\2k\u0165\3\2\2\2m\u0168\3\2\2\2o"+
		"\u016f\3\2\2\2q\u0175\3\2\2\2s\u0177\3\2\2\2u\u017a\3\2\2\2w\u017d\3\2"+
		"\2\2y\u017f\3\2\2\2{\u0181\3\2\2\2}\u0183\3\2\2\2\177\u0185\3\2\2\2\u0081"+
		"\u0187\3\2\2\2\u0083\u0189\3\2\2\2\u0085\u018b\3\2\2\2\u0087\u018d\3\2"+
		"\2\2\u0089\u018f\3\2\2\2\u008b\u0192\3\2\2\2\u008d\u019c\3\2\2\2\u008f"+
		"\u01a0\3\2\2\2\u0091\u01a7\3\2\2\2\u0093\u01ae\3\2\2\2\u0095\u01b5\3\2"+
		"\2\2\u0097\u01c6\3\2\2\2\u0099\u009a\7c\2\2\u009a\u009b\7n\2\2\u009b\u009c"+
		"\7n\2\2\u009c\u009d\7q\2\2\u009d\u009e\7e\2\2\u009e\u009f\7c\2\2\u009f"+
		"\u00a0\7v\2\2\u00a0\u00a1\7g\2\2\u00a1\4\3\2\2\2\u00a2\u00a3\7d\2\2\u00a3"+
		"\u00a4\7t\2\2\u00a4\u00a5\7g\2\2\u00a5\u00a6\7c\2\2\u00a6\u00a7\7m\2\2"+
		"\u00a7\6\3\2\2\2\u00a8\u00a9\7e\2\2\u00a9\u00aa\7c\2\2\u00aa\u00ab\7u"+
		"\2\2\u00ab\u00ac\7g\2\2\u00ac\b\3\2\2\2\u00ad\u00ae\7e\2\2\u00ae\u00af"+
		"\7q\2\2\u00af\u00b0\7p\2\2\u00b0\u00b1\7v\2\2\u00b1\u00b2\7k\2\2\u00b2"+
		"\u00b3\7p\2\2\u00b3\u00b4\7w\2\2\u00b4\u00b5\7g\2\2\u00b5\n\3\2\2\2\u00b6"+
		"\u00b7\7f\2\2\u00b7\u00b8\7g\2\2\u00b8\u00b9\7h\2\2\u00b9\f\3\2\2\2\u00ba"+
		"\u00bb\7f\2\2\u00bb\u00bc\7q\2\2\u00bc\16\3\2\2\2\u00bd\u00be\7g\2\2\u00be"+
		"\u00bf\7n\2\2\u00bf\u00c0\7u\2\2\u00c0\u00c1\7g\2\2\u00c1\20\3\2\2\2\u00c2"+
		"\u00c3\7g\2\2\u00c3\u00c4\7n\2\2\u00c4\u00c5\7u\2\2\u00c5\u00c6\7k\2\2"+
		"\u00c6\u00c7\7h\2\2\u00c7\22\3\2\2\2\u00c8\u00c9\7g\2\2\u00c9\u00ca\7"+
		"p\2\2\u00ca\u00cb\7f\2\2\u00cb\24\3\2\2\2\u00cc\u00cd\7h\2\2\u00cd\u00ce"+
		"\7c\2\2\u00ce\u00cf\7n\2\2\u00cf\u00d0\7u\2\2\u00d0\u00d1\7g\2\2\u00d1"+
		"\26\3\2\2\2\u00d2\u00d3\7h\2\2\u00d3\u00d4\7q\2\2\u00d4\u00d5\7t\2\2\u00d5"+
		"\30\3\2\2\2\u00d6\u00d7\7j\2\2\u00d7\u00d8\7g\2\2\u00d8\u00d9\7c\2\2\u00d9"+
		"\u00da\7r\2\2\u00da\32\3\2\2\2\u00db\u00dc\7k\2\2\u00dc\u00dd\7h\2\2\u00dd"+
		"\34\3\2\2\2\u00de\u00df\7k\2\2\u00df\u00e0\7p\2\2\u00e0\36\3\2\2\2\u00e1"+
		"\u00e2\7k\2\2\u00e2\u00e3\7p\2\2\u00e3\u00e4\7n\2\2\u00e4\u00e5\7k\2\2"+
		"\u00e5\u00e6\7p\2\2\u00e6\u00e7\7g\2\2\u00e7 \3\2\2\2\u00e8\u00e9\7n\2"+
		"\2\u00e9\u00ea\7q\2\2\u00ea\u00eb\7q\2\2\u00eb\u00ec\7r\2\2\u00ec\"\3"+
		"\2\2\2\u00ed\u00ee\7p\2\2\u00ee\u00ef\7w\2\2\u00ef\u00f0\7n\2\2\u00f0"+
		"\u00f1\7n\2\2\u00f1$\3\2\2\2\u00f2\u00f3\7t\2\2\u00f3\u00f4\7g\2\2\u00f4"+
		"\u00f5\7v\2\2\u00f5\u00f6\7w\2\2\u00f6\u00f7\7t\2\2\u00f7\u00f8\7p\2\2"+
		"\u00f8&\3\2\2\2\u00f9\u00fa\7u\2\2\u00fa\u00fb\7g\2\2\u00fb\u00fc\7p\2"+
		"\2\u00fc\u00fd\7u\2\2\u00fd\u00fe\7q\2\2\u00fe\u00ff\7t\2\2\u00ff(\3\2"+
		"\2\2\u0100\u0101\7u\2\2\u0101\u0102\7v\2\2\u0102\u0103\7c\2\2\u0103\u0104"+
		"\7e\2\2\u0104\u0105\7m\2\2\u0105*\3\2\2\2\u0106\u0107\7v\2\2\u0107\u0108"+
		"\7j\2\2\u0108\u0109\7g\2\2\u0109\u010a\7p\2\2\u010a,\3\2\2\2\u010b\u010c"+
		"\7v\2\2\u010c\u010d\7t\2\2\u010d\u010e\7w\2\2\u010e\u010f\7g\2\2\u010f"+
		".\3\2\2\2\u0110\u0111\7y\2\2\u0111\u0112\7j\2\2\u0112\u0113\7g\2\2\u0113"+
		"\u0114\7p\2\2\u0114\60\3\2\2\2\u0115\u0116\7y\2\2\u0116\u0117\7j\2\2\u0117"+
		"\u0118\7k\2\2\u0118\u0119\7n\2\2\u0119\u011a\7g\2\2\u011a\62\3\2\2\2\u011b"+
		"\u011c\7?\2\2\u011c\64\3\2\2\2\u011d\u011e\7B\2\2\u011e\66\3\2\2\2\u011f"+
		"\u0120\7<\2\2\u01208\3\2\2\2\u0121\u0122\7.\2\2\u0122:\3\2\2\2\u0123\u0124"+
		"\7\61\2\2\u0124<\3\2\2\2\u0125\u0126\7^\2\2\u0126>\3\2\2\2\u0127\u0128"+
		"\7&\2\2\u0128@\3\2\2\2\u0129\u012a\7\60\2\2\u012aB\3\2\2\2\u012b\u012c"+
		"\7,\2\2\u012c\u012d\7,\2\2\u012dD\3\2\2\2\u012e\u012f\7/\2\2\u012fF\3"+
		"\2\2\2\u0130\u0131\7\'\2\2\u0131H\3\2\2\2\u0132\u0133\7,\2\2\u0133J\3"+
		"\2\2\2\u0134\u0139\7#\2\2\u0135\u0136\7p\2\2\u0136\u0137\7q\2\2\u0137"+
		"\u0139\7v\2\2\u0138\u0134\3\2\2\2\u0138\u0135\3\2\2\2\u0139L\3\2\2\2\u013a"+
		"\u013b\7\u0080\2\2\u013bN\3\2\2\2\u013c\u013d\7-\2\2\u013dP\3\2\2\2\u013e"+
		"\u013f\7A\2\2\u013fR\3\2\2\2\u0140\u0141\7=\2\2\u0141T\3\2\2\2\u0142\u0143"+
		"\7\61\2\2\u0143\u0144\7?\2\2\u0144V\3\2\2\2\u0145\u0146\7,\2\2\u0146\u0147"+
		"\7,\2\2\u0147\u0148\7?\2\2\u0148X\3\2\2\2\u0149\u014a\7/\2\2\u014a\u014b"+
		"\7?\2\2\u014bZ\3\2\2\2\u014c\u014d\7,\2\2\u014d\u014e\7?\2\2\u014e\\\3"+
		"\2\2\2\u014f\u0150\7-\2\2\u0150\u0151\7?\2\2\u0151^\3\2\2\2\u0152\u0153"+
		"\7>\2\2\u0153`\3\2\2\2\u0154\u0155\7>\2\2\u0155\u0156\7?\2\2\u0156b\3"+
		"\2\2\2\u0157\u0158\7#\2\2\u0158\u0159\7?\2\2\u0159d\3\2\2\2\u015a\u015b"+
		"\7?\2\2\u015b\u015c\7?\2\2\u015cf\3\2\2\2\u015d\u015e\7?\2\2\u015e\u015f"+
		"\7?\2\2\u015f\u0160\7?\2\2\u0160h\3\2\2\2\u0161\u0162\7#\2\2\u0162\u0163"+
		"\7?\2\2\u0163\u0164\7?\2\2\u0164j\3\2\2\2\u0165\u0166\7@\2\2\u0166\u0167"+
		"\7?\2\2\u0167l\3\2\2\2\u0168\u0169\7@\2\2\u0169n\3\2\2\2\u016a\u016b\7"+
		"(\2\2\u016b\u0170\7(\2\2\u016c\u016d\7c\2\2\u016d\u016e\7p\2\2\u016e\u0170"+
		"\7f\2\2\u016f\u016a\3\2\2\2\u016f\u016c\3\2\2\2\u0170p\3\2\2\2\u0171\u0172"+
		"\7~\2\2\u0172\u0176\7~\2\2\u0173\u0174\7q\2\2\u0174\u0176\7t\2\2\u0175"+
		"\u0171\3\2\2\2\u0175\u0173\3\2\2\2\u0176r\3\2\2\2\u0177\u0178\7>\2\2\u0178"+
		"\u0179\7>\2\2\u0179t\3\2\2\2\u017a\u017b\7@\2\2\u017b\u017c\7@\2\2\u017c"+
		"v\3\2\2\2\u017d\u017e\7(\2\2\u017ex\3\2\2\2\u017f\u0180\7~\2\2\u0180z"+
		"\3\2\2\2\u0181\u0182\7`\2\2\u0182|\3\2\2\2\u0183\u0184\7]\2\2\u0184~\3"+
		"\2\2\2\u0185\u0186\7_\2\2\u0186\u0080\3\2\2\2\u0187\u0188\7*\2\2\u0188"+
		"\u0082\3\2\2\2\u0189\u018a\7+\2\2\u018a\u0084\3\2\2\2\u018b\u018c\7}\2"+
		"\2\u018c\u0086\3\2\2\2\u018d\u018e\7\177\2\2\u018e\u0088\3\2\2\2\u018f"+
		"\u0190\7^\2\2\u0190\u0191\7$\2\2\u0191\u008a\3\2\2\2\u0192\u0197\7$\2"+
		"\2\u0193\u0196\5\u0089E\2\u0194\u0196\n\2\2\2\u0195\u0193\3\2\2\2\u0195"+
		"\u0194\3\2\2\2\u0196\u0199\3\2\2\2\u0197\u0198\3\2\2\2\u0197\u0195\3\2"+
		"\2\2\u0198\u019a\3\2\2\2\u0199\u0197\3\2\2\2\u019a\u019b\7$\2\2\u019b"+
		"\u008c\3\2\2\2\u019c\u019d\5\u008fH\2\u019d\u019e\5A!\2\u019e\u019f\5"+
		"\u008fH\2\u019f\u008e\3\2\2\2\u01a0\u01a4\t\3\2\2\u01a1\u01a3\t\3\2\2"+
		"\u01a2\u01a1\3\2\2\2\u01a3\u01a6\3\2\2\2\u01a4\u01a2\3\2\2\2\u01a4\u01a5"+
		"\3\2\2\2\u01a5\u0090\3\2\2\2\u01a6\u01a4\3\2\2\2\u01a7\u01a8\7\62\2\2"+
		"\u01a8\u01aa\t\4\2\2\u01a9\u01ab\t\5\2\2\u01aa\u01a9\3\2\2\2\u01ab\u01ac"+
		"\3\2\2\2\u01ac\u01aa\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u0092\3\2\2\2\u01ae"+
		"\u01b2\t\6\2\2\u01af\u01b1\t\7\2\2\u01b0\u01af\3\2\2\2\u01b1\u01b4\3\2"+
		"\2\2\u01b2\u01b0\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u0094\3\2\2\2\u01b4"+
		"\u01b2\3\2\2\2\u01b5\u01b6\7\61\2\2\u01b6\u01b7\7\61\2\2\u01b7\u01bb\3"+
		"\2\2\2\u01b8\u01ba\n\2\2\2\u01b9\u01b8\3\2\2\2\u01ba\u01bd\3\2\2\2\u01bb"+
		"\u01b9\3\2\2\2\u01bb\u01bc\3\2\2\2\u01bc\u01bf\3\2\2\2\u01bd\u01bb\3\2"+
		"\2\2\u01be\u01c0\7\17\2\2\u01bf\u01be\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0"+
		"\u01c1\3\2\2\2\u01c1\u01c2\7\f\2\2\u01c2\u01c3\3\2\2\2\u01c3\u01c4\bK"+
		"\2\2\u01c4\u0096\3\2\2\2\u01c5\u01c7\t\b\2\2\u01c6\u01c5\3\2\2\2\u01c7"+
		"\u01c8\3\2\2\2\u01c8\u01c6\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9\u01ca\3\2"+
		"\2\2\u01ca\u01cb\bL\2\2\u01cb\u0098\3\2\2\2\16\2\u0138\u016f\u0175\u0195"+
		"\u0197\u01a4\u01ac\u01b2\u01bb\u01bf\u01c8\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}