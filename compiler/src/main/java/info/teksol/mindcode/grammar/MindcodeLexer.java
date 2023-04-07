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
		NULL=18, RETURN=19, SENSOR=20, STACK=21, THEN=22, TRUE=23, WHEN=24, WHILE=25, 
		ASSIGN=26, AT=27, COLON=28, COMMA=29, DIV=30, IDIV=31, DOLLAR=32, DOT=33, 
		EXP=34, MINUS=35, MOD=36, MUL=37, NOT=38, BITWISE_NOT=39, PLUS=40, QUESTION_MARK=41, 
		SEMICOLON=42, HASHSET=43, EXP_ASSIGN=44, MUL_ASSIGN=45, DIV_ASSIGN=46, 
		IDIV_ASSIGN=47, MOD_ASSIGN=48, PLUS_ASSIGN=49, MINUS_ASSIGN=50, SHIFT_LEFT_ASSIGN=51, 
		SHIFT_RIGHT_ASSIGN=52, BITWISE_AND_ASSIGN=53, BITWISE_OR_ASSIGN=54, BITWISE_XOR_ASSIGN=55, 
		AND_ASSIGN=56, OR_ASSIGN=57, LESS_THAN=58, LESS_THAN_EQUAL=59, NOT_EQUAL=60, 
		EQUAL=61, STRICT_EQUAL=62, STRICT_NOT_EQUAL=63, GREATER_THAN_EQUAL=64, 
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
			"NULL", "RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", "WHILE", 
			"ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", "EXP", 
			"MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "HASHSET", "EXP_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "IDIV_ASSIGN", 
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
			"'in'", "'inline'", "'loop'", "'null'", "'return'", "'sensor'", "'stack'", 
			"'then'", "'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", 
			"'/'", "'\\'", "'$'", "'.'", "'**'", "'-'", "'%'", "'*'", null, "'~'", 
			"'+'", "'?'", "';'", "'#set'", "'**='", "'*='", "'/='", "'\\='", "'%='", 
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
			"LOOP", "NULL", "RETURN", "SENSOR", "STACK", "THEN", "TRUE", "WHEN", 
			"WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "IDIV", "DOLLAR", "DOT", 
			"EXP", "MINUS", "MOD", "MUL", "NOT", "BITWISE_NOT", "PLUS", "QUESTION_MARK", 
			"SEMICOLON", "HASHSET", "EXP_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "IDIV_ASSIGN", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2W\u020c\b\1\4\2\t"+
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
		"\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\30"+
		"\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\3#\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3\'\5\'\u0155\n\'\3(\3("+
		"\3)\3)\3*\3*\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3-\3.\3.\3.\3/\3/\3/\3\60\3"+
		"\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3"+
		"\64\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3\67\38\38\38\39\39\3"+
		"9\39\3:\3:\3:\3:\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3>\3?\3?\3?\3?\3@\3@\3"+
		"@\3@\3A\3A\3A\3B\3B\3C\3C\3C\3C\3C\5C\u01b0\nC\3D\3D\3D\3D\5D\u01b6\n"+
		"D\3E\3E\3E\3F\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N\3"+
		"O\3O\3P\3P\3P\3Q\3Q\3Q\7Q\u01d6\nQ\fQ\16Q\u01d9\13Q\3Q\3Q\3R\3R\3R\3R"+
		"\3S\3S\7S\u01e3\nS\fS\16S\u01e6\13S\3T\3T\3T\6T\u01eb\nT\rT\16T\u01ec"+
		"\3U\3U\7U\u01f1\nU\fU\16U\u01f4\13U\3V\3V\3V\3V\7V\u01fa\nV\fV\16V\u01fd"+
		"\13V\3V\5V\u0200\nV\3V\3V\3V\3V\3W\6W\u0207\nW\rW\16W\u0208\3W\3W\3\u01d7"+
		"\2X\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36"+
		";\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67"+
		"m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089F\u008bG\u008d"+
		"H\u008fI\u0091J\u0093K\u0095L\u0097M\u0099N\u009bO\u009dP\u009f\2\u00a1"+
		"Q\u00a3R\u00a5S\u00a7T\u00a9U\u00abV\u00adW\3\2\t\4\2\f\f\17\17\3\2\62"+
		";\4\2ZZzz\4\2\62;ch\5\2C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u0215"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
		"\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2"+
		"\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2"+
		"{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085"+
		"\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2"+
		"\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097"+
		"\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\2\u00a1\3\2\2"+
		"\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab"+
		"\3\2\2\2\2\u00ad\3\2\2\2\3\u00af\3\2\2\2\5\u00b8\3\2\2\2\7\u00be\3\2\2"+
		"\2\t\u00c3\3\2\2\2\13\u00c9\3\2\2\2\r\u00d2\3\2\2\2\17\u00d6\3\2\2\2\21"+
		"\u00d9\3\2\2\2\23\u00de\3\2\2\2\25\u00e4\3\2\2\2\27\u00e8\3\2\2\2\31\u00ee"+
		"\3\2\2\2\33\u00f2\3\2\2\2\35\u00f7\3\2\2\2\37\u00fa\3\2\2\2!\u00fd\3\2"+
		"\2\2#\u0104\3\2\2\2%\u0109\3\2\2\2\'\u010e\3\2\2\2)\u0115\3\2\2\2+\u011c"+
		"\3\2\2\2-\u0122\3\2\2\2/\u0127\3\2\2\2\61\u012c\3\2\2\2\63\u0131\3\2\2"+
		"\2\65\u0137\3\2\2\2\67\u0139\3\2\2\29\u013b\3\2\2\2;\u013d\3\2\2\2=\u013f"+
		"\3\2\2\2?\u0141\3\2\2\2A\u0143\3\2\2\2C\u0145\3\2\2\2E\u0147\3\2\2\2G"+
		"\u014a\3\2\2\2I\u014c\3\2\2\2K\u014e\3\2\2\2M\u0154\3\2\2\2O\u0156\3\2"+
		"\2\2Q\u0158\3\2\2\2S\u015a\3\2\2\2U\u015c\3\2\2\2W\u015e\3\2\2\2Y\u0163"+
		"\3\2\2\2[\u0167\3\2\2\2]\u016a\3\2\2\2_\u016d\3\2\2\2a\u0170\3\2\2\2c"+
		"\u0173\3\2\2\2e\u0176\3\2\2\2g\u0179\3\2\2\2i\u017d\3\2\2\2k\u0181\3\2"+
		"\2\2m\u0184\3\2\2\2o\u0187\3\2\2\2q\u018a\3\2\2\2s\u018e\3\2\2\2u\u0192"+
		"\3\2\2\2w\u0194\3\2\2\2y\u0197\3\2\2\2{\u019a\3\2\2\2}\u019d\3\2\2\2\177"+
		"\u01a1\3\2\2\2\u0081\u01a5\3\2\2\2\u0083\u01a8\3\2\2\2\u0085\u01af\3\2"+
		"\2\2\u0087\u01b5\3\2\2\2\u0089\u01b7\3\2\2\2\u008b\u01ba\3\2\2\2\u008d"+
		"\u01bd\3\2\2\2\u008f\u01bf\3\2\2\2\u0091\u01c1\3\2\2\2\u0093\u01c3\3\2"+
		"\2\2\u0095\u01c5\3\2\2\2\u0097\u01c7\3\2\2\2\u0099\u01c9\3\2\2\2\u009b"+
		"\u01cb\3\2\2\2\u009d\u01cd\3\2\2\2\u009f\u01cf\3\2\2\2\u00a1\u01d2\3\2"+
		"\2\2\u00a3\u01dc\3\2\2\2\u00a5\u01e0\3\2\2\2\u00a7\u01e7\3\2\2\2\u00a9"+
		"\u01ee\3\2\2\2\u00ab\u01f5\3\2\2\2\u00ad\u0206\3\2\2\2\u00af\u00b0\7c"+
		"\2\2\u00b0\u00b1\7n\2\2\u00b1\u00b2\7n\2\2\u00b2\u00b3\7q\2\2\u00b3\u00b4"+
		"\7e\2\2\u00b4\u00b5\7c\2\2\u00b5\u00b6\7v\2\2\u00b6\u00b7\7g\2\2\u00b7"+
		"\4\3\2\2\2\u00b8\u00b9\7d\2\2\u00b9\u00ba\7t\2\2\u00ba\u00bb\7g\2\2\u00bb"+
		"\u00bc\7c\2\2\u00bc\u00bd\7m\2\2\u00bd\6\3\2\2\2\u00be\u00bf\7e\2\2\u00bf"+
		"\u00c0\7c\2\2\u00c0\u00c1\7u\2\2\u00c1\u00c2\7g\2\2\u00c2\b\3\2\2\2\u00c3"+
		"\u00c4\7e\2\2\u00c4\u00c5\7q\2\2\u00c5\u00c6\7p\2\2\u00c6\u00c7\7u\2\2"+
		"\u00c7\u00c8\7v\2\2\u00c8\n\3\2\2\2\u00c9\u00ca\7e\2\2\u00ca\u00cb\7q"+
		"\2\2\u00cb\u00cc\7p\2\2\u00cc\u00cd\7v\2\2\u00cd\u00ce\7k\2\2\u00ce\u00cf"+
		"\7p\2\2\u00cf\u00d0\7w\2\2\u00d0\u00d1\7g\2\2\u00d1\f\3\2\2\2\u00d2\u00d3"+
		"\7f\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7h\2\2\u00d5\16\3\2\2\2\u00d6\u00d7"+
		"\7f\2\2\u00d7\u00d8\7q\2\2\u00d8\20\3\2\2\2\u00d9\u00da\7g\2\2\u00da\u00db"+
		"\7n\2\2\u00db\u00dc\7u\2\2\u00dc\u00dd\7g\2\2\u00dd\22\3\2\2\2\u00de\u00df"+
		"\7g\2\2\u00df\u00e0\7n\2\2\u00e0\u00e1\7u\2\2\u00e1\u00e2\7k\2\2\u00e2"+
		"\u00e3\7h\2\2\u00e3\24\3\2\2\2\u00e4\u00e5\7g\2\2\u00e5\u00e6\7p\2\2\u00e6"+
		"\u00e7\7f\2\2\u00e7\26\3\2\2\2\u00e8\u00e9\7h\2\2\u00e9\u00ea\7c\2\2\u00ea"+
		"\u00eb\7n\2\2\u00eb\u00ec\7u\2\2\u00ec\u00ed\7g\2\2\u00ed\30\3\2\2\2\u00ee"+
		"\u00ef\7h\2\2\u00ef\u00f0\7q\2\2\u00f0\u00f1\7t\2\2\u00f1\32\3\2\2\2\u00f2"+
		"\u00f3\7j\2\2\u00f3\u00f4\7g\2\2\u00f4\u00f5\7c\2\2\u00f5\u00f6\7r\2\2"+
		"\u00f6\34\3\2\2\2\u00f7\u00f8\7k\2\2\u00f8\u00f9\7h\2\2\u00f9\36\3\2\2"+
		"\2\u00fa\u00fb\7k\2\2\u00fb\u00fc\7p\2\2\u00fc \3\2\2\2\u00fd\u00fe\7"+
		"k\2\2\u00fe\u00ff\7p\2\2\u00ff\u0100\7n\2\2\u0100\u0101\7k\2\2\u0101\u0102"+
		"\7p\2\2\u0102\u0103\7g\2\2\u0103\"\3\2\2\2\u0104\u0105\7n\2\2\u0105\u0106"+
		"\7q\2\2\u0106\u0107\7q\2\2\u0107\u0108\7r\2\2\u0108$\3\2\2\2\u0109\u010a"+
		"\7p\2\2\u010a\u010b\7w\2\2\u010b\u010c\7n\2\2\u010c\u010d\7n\2\2\u010d"+
		"&\3\2\2\2\u010e\u010f\7t\2\2\u010f\u0110\7g\2\2\u0110\u0111\7v\2\2\u0111"+
		"\u0112\7w\2\2\u0112\u0113\7t\2\2\u0113\u0114\7p\2\2\u0114(\3\2\2\2\u0115"+
		"\u0116\7u\2\2\u0116\u0117\7g\2\2\u0117\u0118\7p\2\2\u0118\u0119\7u\2\2"+
		"\u0119\u011a\7q\2\2\u011a\u011b\7t\2\2\u011b*\3\2\2\2\u011c\u011d\7u\2"+
		"\2\u011d\u011e\7v\2\2\u011e\u011f\7c\2\2\u011f\u0120\7e\2\2\u0120\u0121"+
		"\7m\2\2\u0121,\3\2\2\2\u0122\u0123\7v\2\2\u0123\u0124\7j\2\2\u0124\u0125"+
		"\7g\2\2\u0125\u0126\7p\2\2\u0126.\3\2\2\2\u0127\u0128\7v\2\2\u0128\u0129"+
		"\7t\2\2\u0129\u012a\7w\2\2\u012a\u012b\7g\2\2\u012b\60\3\2\2\2\u012c\u012d"+
		"\7y\2\2\u012d\u012e\7j\2\2\u012e\u012f\7g\2\2\u012f\u0130\7p\2\2\u0130"+
		"\62\3\2\2\2\u0131\u0132\7y\2\2\u0132\u0133\7j\2\2\u0133\u0134\7k\2\2\u0134"+
		"\u0135\7n\2\2\u0135\u0136\7g\2\2\u0136\64\3\2\2\2\u0137\u0138\7?\2\2\u0138"+
		"\66\3\2\2\2\u0139\u013a\7B\2\2\u013a8\3\2\2\2\u013b\u013c\7<\2\2\u013c"+
		":\3\2\2\2\u013d\u013e\7.\2\2\u013e<\3\2\2\2\u013f\u0140\7\61\2\2\u0140"+
		">\3\2\2\2\u0141\u0142\7^\2\2\u0142@\3\2\2\2\u0143\u0144\7&\2\2\u0144B"+
		"\3\2\2\2\u0145\u0146\7\60\2\2\u0146D\3\2\2\2\u0147\u0148\7,\2\2\u0148"+
		"\u0149\7,\2\2\u0149F\3\2\2\2\u014a\u014b\7/\2\2\u014bH\3\2\2\2\u014c\u014d"+
		"\7\'\2\2\u014dJ\3\2\2\2\u014e\u014f\7,\2\2\u014fL\3\2\2\2\u0150\u0155"+
		"\7#\2\2\u0151\u0152\7p\2\2\u0152\u0153\7q\2\2\u0153\u0155\7v\2\2\u0154"+
		"\u0150\3\2\2\2\u0154\u0151\3\2\2\2\u0155N\3\2\2\2\u0156\u0157\7\u0080"+
		"\2\2\u0157P\3\2\2\2\u0158\u0159\7-\2\2\u0159R\3\2\2\2\u015a\u015b\7A\2"+
		"\2\u015bT\3\2\2\2\u015c\u015d\7=\2\2\u015dV\3\2\2\2\u015e\u015f\7%\2\2"+
		"\u015f\u0160\7u\2\2\u0160\u0161\7g\2\2\u0161\u0162\7v\2\2\u0162X\3\2\2"+
		"\2\u0163\u0164\7,\2\2\u0164\u0165\7,\2\2\u0165\u0166\7?\2\2\u0166Z\3\2"+
		"\2\2\u0167\u0168\7,\2\2\u0168\u0169\7?\2\2\u0169\\\3\2\2\2\u016a\u016b"+
		"\7\61\2\2\u016b\u016c\7?\2\2\u016c^\3\2\2\2\u016d\u016e\7^\2\2\u016e\u016f"+
		"\7?\2\2\u016f`\3\2\2\2\u0170\u0171\7\'\2\2\u0171\u0172\7?\2\2\u0172b\3"+
		"\2\2\2\u0173\u0174\7-\2\2\u0174\u0175\7?\2\2\u0175d\3\2\2\2\u0176\u0177"+
		"\7/\2\2\u0177\u0178\7?\2\2\u0178f\3\2\2\2\u0179\u017a\7>\2\2\u017a\u017b"+
		"\7>\2\2\u017b\u017c\7?\2\2\u017ch\3\2\2\2\u017d\u017e\7@\2\2\u017e\u017f"+
		"\7@\2\2\u017f\u0180\7?\2\2\u0180j\3\2\2\2\u0181\u0182\7(\2\2\u0182\u0183"+
		"\7?\2\2\u0183l\3\2\2\2\u0184\u0185\7~\2\2\u0185\u0186\7?\2\2\u0186n\3"+
		"\2\2\2\u0187\u0188\7`\2\2\u0188\u0189\7?\2\2\u0189p\3\2\2\2\u018a\u018b"+
		"\7(\2\2\u018b\u018c\7(\2\2\u018c\u018d\7?\2\2\u018dr\3\2\2\2\u018e\u018f"+
		"\7~\2\2\u018f\u0190\7~\2\2\u0190\u0191\7?\2\2\u0191t\3\2\2\2\u0192\u0193"+
		"\7>\2\2\u0193v\3\2\2\2\u0194\u0195\7>\2\2\u0195\u0196\7?\2\2\u0196x\3"+
		"\2\2\2\u0197\u0198\7#\2\2\u0198\u0199\7?\2\2\u0199z\3\2\2\2\u019a\u019b"+
		"\7?\2\2\u019b\u019c\7?\2\2\u019c|\3\2\2\2\u019d\u019e\7?\2\2\u019e\u019f"+
		"\7?\2\2\u019f\u01a0\7?\2\2\u01a0~\3\2\2\2\u01a1\u01a2\7#\2\2\u01a2\u01a3"+
		"\7?\2\2\u01a3\u01a4\7?\2\2\u01a4\u0080\3\2\2\2\u01a5\u01a6\7@\2\2\u01a6"+
		"\u01a7\7?\2\2\u01a7\u0082\3\2\2\2\u01a8\u01a9\7@\2\2\u01a9\u0084\3\2\2"+
		"\2\u01aa\u01ab\7(\2\2\u01ab\u01b0\7(\2\2\u01ac\u01ad\7c\2\2\u01ad\u01ae"+
		"\7p\2\2\u01ae\u01b0\7f\2\2\u01af\u01aa\3\2\2\2\u01af\u01ac\3\2\2\2\u01b0"+
		"\u0086\3\2\2\2\u01b1\u01b2\7~\2\2\u01b2\u01b6\7~\2\2\u01b3\u01b4\7q\2"+
		"\2\u01b4\u01b6\7t\2\2\u01b5\u01b1\3\2\2\2\u01b5\u01b3\3\2\2\2\u01b6\u0088"+
		"\3\2\2\2\u01b7\u01b8\7>\2\2\u01b8\u01b9\7>\2\2\u01b9\u008a\3\2\2\2\u01ba"+
		"\u01bb\7@\2\2\u01bb\u01bc\7@\2\2\u01bc\u008c\3\2\2\2\u01bd\u01be\7(\2"+
		"\2\u01be\u008e\3\2\2\2\u01bf\u01c0\7~\2\2\u01c0\u0090\3\2\2\2\u01c1\u01c2"+
		"\7`\2\2\u01c2\u0092\3\2\2\2\u01c3\u01c4\7]\2\2\u01c4\u0094\3\2\2\2\u01c5"+
		"\u01c6\7_\2\2\u01c6\u0096\3\2\2\2\u01c7\u01c8\7*\2\2\u01c8\u0098\3\2\2"+
		"\2\u01c9\u01ca\7+\2\2\u01ca\u009a\3\2\2\2\u01cb\u01cc\7}\2\2\u01cc\u009c"+
		"\3\2\2\2\u01cd\u01ce\7\177\2\2\u01ce\u009e\3\2\2\2\u01cf\u01d0\7^\2\2"+
		"\u01d0\u01d1\7$\2\2\u01d1\u00a0\3\2\2\2\u01d2\u01d7\7$\2\2\u01d3\u01d6"+
		"\5\u009fP\2\u01d4\u01d6\n\2\2\2\u01d5\u01d3\3\2\2\2\u01d5\u01d4\3\2\2"+
		"\2\u01d6\u01d9\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d7\u01d5\3\2\2\2\u01d8\u01da"+
		"\3\2\2\2\u01d9\u01d7\3\2\2\2\u01da\u01db\7$\2\2\u01db\u00a2\3\2\2\2\u01dc"+
		"\u01dd\5\u00a5S\2\u01dd\u01de\5C\"\2\u01de\u01df\5\u00a5S\2\u01df\u00a4"+
		"\3\2\2\2\u01e0\u01e4\t\3\2\2\u01e1\u01e3\t\3\2\2\u01e2\u01e1\3\2\2\2\u01e3"+
		"\u01e6\3\2\2\2\u01e4\u01e2\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u00a6\3\2"+
		"\2\2\u01e6\u01e4\3\2\2\2\u01e7\u01e8\7\62\2\2\u01e8\u01ea\t\4\2\2\u01e9"+
		"\u01eb\t\5\2\2\u01ea\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ea\3\2"+
		"\2\2\u01ec\u01ed\3\2\2\2\u01ed\u00a8\3\2\2\2\u01ee\u01f2\t\6\2\2\u01ef"+
		"\u01f1\t\7\2\2\u01f0\u01ef\3\2\2\2\u01f1\u01f4\3\2\2\2\u01f2\u01f0\3\2"+
		"\2\2\u01f2\u01f3\3\2\2\2\u01f3\u00aa\3\2\2\2\u01f4\u01f2\3\2\2\2\u01f5"+
		"\u01f6\7\61\2\2\u01f6\u01f7\7\61\2\2\u01f7\u01fb\3\2\2\2\u01f8\u01fa\n"+
		"\2\2\2\u01f9\u01f8\3\2\2\2\u01fa\u01fd\3\2\2\2\u01fb\u01f9\3\2\2\2\u01fb"+
		"\u01fc\3\2\2\2\u01fc\u01ff\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fe\u0200\7\17"+
		"\2\2\u01ff\u01fe\3\2\2\2\u01ff\u0200\3\2\2\2\u0200\u0201\3\2\2\2\u0201"+
		"\u0202\7\f\2\2\u0202\u0203\3\2\2\2\u0203\u0204\bV\2\2\u0204\u00ac\3\2"+
		"\2\2\u0205\u0207\t\b\2\2\u0206\u0205\3\2\2\2\u0207\u0208\3\2\2\2\u0208"+
		"\u0206\3\2\2\2\u0208\u0209\3\2\2\2\u0209\u020a\3\2\2\2\u020a\u020b\bW"+
		"\2\2\u020b\u00ae\3\2\2\2\16\2\u0154\u01af\u01b5\u01d5\u01d7\u01e4\u01ec"+
		"\u01f2\u01fb\u01ff\u0208\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}