// Generated from Mindcode.g4 by ANTLR 4.9.1
package info.teksol.mindcode.grammar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

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
		INT=81, HEXINT=82, BININT=83, ID=84, SL_COMMENT=85, WS=86;
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
			"LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE", "LITERAL", "DecimalExponent", 
			"DecimalDigits", "FloatLiteral", "IntegerLiteral", "Integer", "Decimal", 
			"Binary", "Hexadecimal", "DecimalDigit", "HexDigit", "Float", "FLOAT", 
			"INT", "HEXINT", "BININT", "ID", "SL_COMMENT", "WS"
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
			"BININT", "ID", "SL_COMMENT", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2X\u0257\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!"+
		"\3!\3\"\3\"\3#\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3\'\5\'\u016d\n\'\3"+
		"(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3-\3.\3.\3.\3/\3/\3/\3"+
		"\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3"+
		"\64\3\64\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\67\3\67\3\67\38\38\38\3"+
		"9\39\39\39\3:\3:\3:\3:\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3>\3?\3?\3?\3?\3"+
		"@\3@\3@\3@\3A\3A\3A\3B\3B\3C\3C\3C\3C\3C\5C\u01c8\nC\3D\3D\3D\3D\5D\u01ce"+
		"\nD\3E\3E\3E\3F\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N"+
		"\3O\3O\3P\3P\3P\3Q\3Q\3Q\7Q\u01ee\nQ\fQ\16Q\u01f1\13Q\3Q\3Q\3R\3R\3R\3"+
		"R\3R\3R\3R\3R\3R\5R\u01fe\nR\3R\3R\3S\6S\u0203\nS\rS\16S\u0204\3T\3T\3"+
		"U\3U\3V\3V\3V\5V\u020e\nV\3W\3W\3X\3X\3X\3X\6X\u0216\nX\rX\16X\u0217\3"+
		"Y\3Y\3Y\3Y\6Y\u021e\nY\rY\16Y\u021f\3Z\3Z\3[\3[\3\\\3\\\5\\\u0228\n\\"+
		"\3\\\3\\\3\\\3\\\5\\\u022e\n\\\5\\\u0230\n\\\3]\3]\3^\3^\3_\3_\3`\3`\3"+
		"a\3a\7a\u023c\na\fa\16a\u023f\13a\3b\3b\3b\3b\7b\u0245\nb\fb\16b\u0248"+
		"\13b\3b\5b\u024b\nb\3b\3b\3b\3b\3c\6c\u0252\nc\rc\16c\u0253\3c\3c\3\u01ef"+
		"\2d\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36"+
		";\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67"+
		"m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089F\u008bG\u008d"+
		"H\u008fI\u0091J\u0093K\u0095L\u0097M\u0099N\u009bO\u009dP\u009f\2\u00a1"+
		"Q\u00a3\2\u00a5\2\u00a7\2\u00a9\2\u00ab\2\u00ad\2\u00af\2\u00b1\2\u00b3"+
		"\2\u00b5\2\u00b7\2\u00b9R\u00bbS\u00bdT\u00bfU\u00c1V\u00c3W\u00c5X\3"+
		"\2\b\4\2\f\f\17\17\4\2GGgg\5\2\62;CHch\5\2C\\aac|\7\2//\62;C\\aac|\5\2"+
		"\13\f\17\17\"\"\2\u025f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2"+
		"Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3"+
		"\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2"+
		"\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2"+
		"w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2"+
		"\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d"+
		"\3\2\2\2\2\u00a1\3\2\2\2\2\u00b9\3\2\2\2\2\u00bb\3\2\2\2\2\u00bd\3\2\2"+
		"\2\2\u00bf\3\2\2\2\2\u00c1\3\2\2\2\2\u00c3\3\2\2\2\2\u00c5\3\2\2\2\3\u00c7"+
		"\3\2\2\2\5\u00d0\3\2\2\2\7\u00d6\3\2\2\2\t\u00db\3\2\2\2\13\u00e1\3\2"+
		"\2\2\r\u00ea\3\2\2\2\17\u00ee\3\2\2\2\21\u00f1\3\2\2\2\23\u00f6\3\2\2"+
		"\2\25\u00fc\3\2\2\2\27\u0100\3\2\2\2\31\u0106\3\2\2\2\33\u010a\3\2\2\2"+
		"\35\u010f\3\2\2\2\37\u0112\3\2\2\2!\u0115\3\2\2\2#\u011c\3\2\2\2%\u0121"+
		"\3\2\2\2\'\u0126\3\2\2\2)\u012d\3\2\2\2+\u0134\3\2\2\2-\u013a\3\2\2\2"+
		"/\u013f\3\2\2\2\61\u0144\3\2\2\2\63\u0149\3\2\2\2\65\u014f\3\2\2\2\67"+
		"\u0151\3\2\2\29\u0153\3\2\2\2;\u0155\3\2\2\2=\u0157\3\2\2\2?\u0159\3\2"+
		"\2\2A\u015b\3\2\2\2C\u015d\3\2\2\2E\u015f\3\2\2\2G\u0162\3\2\2\2I\u0164"+
		"\3\2\2\2K\u0166\3\2\2\2M\u016c\3\2\2\2O\u016e\3\2\2\2Q\u0170\3\2\2\2S"+
		"\u0172\3\2\2\2U\u0174\3\2\2\2W\u0176\3\2\2\2Y\u017b\3\2\2\2[\u017f\3\2"+
		"\2\2]\u0182\3\2\2\2_\u0185\3\2\2\2a\u0188\3\2\2\2c\u018b\3\2\2\2e\u018e"+
		"\3\2\2\2g\u0191\3\2\2\2i\u0195\3\2\2\2k\u0199\3\2\2\2m\u019c\3\2\2\2o"+
		"\u019f\3\2\2\2q\u01a2\3\2\2\2s\u01a6\3\2\2\2u\u01aa\3\2\2\2w\u01ac\3\2"+
		"\2\2y\u01af\3\2\2\2{\u01b2\3\2\2\2}\u01b5\3\2\2\2\177\u01b9\3\2\2\2\u0081"+
		"\u01bd\3\2\2\2\u0083\u01c0\3\2\2\2\u0085\u01c7\3\2\2\2\u0087\u01cd\3\2"+
		"\2\2\u0089\u01cf\3\2\2\2\u008b\u01d2\3\2\2\2\u008d\u01d5\3\2\2\2\u008f"+
		"\u01d7\3\2\2\2\u0091\u01d9\3\2\2\2\u0093\u01db\3\2\2\2\u0095\u01dd\3\2"+
		"\2\2\u0097\u01df\3\2\2\2\u0099\u01e1\3\2\2\2\u009b\u01e3\3\2\2\2\u009d"+
		"\u01e5\3\2\2\2\u009f\u01e7\3\2\2\2\u00a1\u01ea\3\2\2\2\u00a3\u01fd\3\2"+
		"\2\2\u00a5\u0202\3\2\2\2\u00a7\u0206\3\2\2\2\u00a9\u0208\3\2\2\2\u00ab"+
		"\u020d\3\2\2\2\u00ad\u020f\3\2\2\2\u00af\u0211\3\2\2\2\u00b1\u0219\3\2"+
		"\2\2\u00b3\u0221\3\2\2\2\u00b5\u0223\3\2\2\2\u00b7\u022f\3\2\2\2\u00b9"+
		"\u0231\3\2\2\2\u00bb\u0233\3\2\2\2\u00bd\u0235\3\2\2\2\u00bf\u0237\3\2"+
		"\2\2\u00c1\u0239\3\2\2\2\u00c3\u0240\3\2\2\2\u00c5\u0251\3\2\2\2\u00c7"+
		"\u00c8\7c\2\2\u00c8\u00c9\7n\2\2\u00c9\u00ca\7n\2\2\u00ca\u00cb\7q\2\2"+
		"\u00cb\u00cc\7e\2\2\u00cc\u00cd\7c\2\2\u00cd\u00ce\7v\2\2\u00ce\u00cf"+
		"\7g\2\2\u00cf\4\3\2\2\2\u00d0\u00d1\7d\2\2\u00d1\u00d2\7t\2\2\u00d2\u00d3"+
		"\7g\2\2\u00d3\u00d4\7c\2\2\u00d4\u00d5\7m\2\2\u00d5\6\3\2\2\2\u00d6\u00d7"+
		"\7e\2\2\u00d7\u00d8\7c\2\2\u00d8\u00d9\7u\2\2\u00d9\u00da\7g\2\2\u00da"+
		"\b\3\2\2\2\u00db\u00dc\7e\2\2\u00dc\u00dd\7q\2\2\u00dd\u00de\7p\2\2\u00de"+
		"\u00df\7u\2\2\u00df\u00e0\7v\2\2\u00e0\n\3\2\2\2\u00e1\u00e2\7e\2\2\u00e2"+
		"\u00e3\7q\2\2\u00e3\u00e4\7p\2\2\u00e4\u00e5\7v\2\2\u00e5\u00e6\7k\2\2"+
		"\u00e6\u00e7\7p\2\2\u00e7\u00e8\7w\2\2\u00e8\u00e9\7g\2\2\u00e9\f\3\2"+
		"\2\2\u00ea\u00eb\7f\2\2\u00eb\u00ec\7g\2\2\u00ec\u00ed\7h\2\2\u00ed\16"+
		"\3\2\2\2\u00ee\u00ef\7f\2\2\u00ef\u00f0\7q\2\2\u00f0\20\3\2\2\2\u00f1"+
		"\u00f2\7g\2\2\u00f2\u00f3\7n\2\2\u00f3\u00f4\7u\2\2\u00f4\u00f5\7g\2\2"+
		"\u00f5\22\3\2\2\2\u00f6\u00f7\7g\2\2\u00f7\u00f8\7n\2\2\u00f8\u00f9\7"+
		"u\2\2\u00f9\u00fa\7k\2\2\u00fa\u00fb\7h\2\2\u00fb\24\3\2\2\2\u00fc\u00fd"+
		"\7g\2\2\u00fd\u00fe\7p\2\2\u00fe\u00ff\7f\2\2\u00ff\26\3\2\2\2\u0100\u0101"+
		"\7h\2\2\u0101\u0102\7c\2\2\u0102\u0103\7n\2\2\u0103\u0104\7u\2\2\u0104"+
		"\u0105\7g\2\2\u0105\30\3\2\2\2\u0106\u0107\7h\2\2\u0107\u0108\7q\2\2\u0108"+
		"\u0109\7t\2\2\u0109\32\3\2\2\2\u010a\u010b\7j\2\2\u010b\u010c\7g\2\2\u010c"+
		"\u010d\7c\2\2\u010d\u010e\7r\2\2\u010e\34\3\2\2\2\u010f\u0110\7k\2\2\u0110"+
		"\u0111\7h\2\2\u0111\36\3\2\2\2\u0112\u0113\7k\2\2\u0113\u0114\7p\2\2\u0114"+
		" \3\2\2\2\u0115\u0116\7k\2\2\u0116\u0117\7p\2\2\u0117\u0118\7n\2\2\u0118"+
		"\u0119\7k\2\2\u0119\u011a\7p\2\2\u011a\u011b\7g\2\2\u011b\"\3\2\2\2\u011c"+
		"\u011d\7n\2\2\u011d\u011e\7q\2\2\u011e\u011f\7q\2\2\u011f\u0120\7r\2\2"+
		"\u0120$\3\2\2\2\u0121\u0122\7p\2\2\u0122\u0123\7w\2\2\u0123\u0124\7n\2"+
		"\2\u0124\u0125\7n\2\2\u0125&\3\2\2\2\u0126\u0127\7t\2\2\u0127\u0128\7"+
		"g\2\2\u0128\u0129\7v\2\2\u0129\u012a\7w\2\2\u012a\u012b\7t\2\2\u012b\u012c"+
		"\7p\2\2\u012c(\3\2\2\2\u012d\u012e\7u\2\2\u012e\u012f\7g\2\2\u012f\u0130"+
		"\7p\2\2\u0130\u0131\7u\2\2\u0131\u0132\7q\2\2\u0132\u0133\7t\2\2\u0133"+
		"*\3\2\2\2\u0134\u0135\7u\2\2\u0135\u0136\7v\2\2\u0136\u0137\7c\2\2\u0137"+
		"\u0138\7e\2\2\u0138\u0139\7m\2\2\u0139,\3\2\2\2\u013a\u013b\7v\2\2\u013b"+
		"\u013c\7j\2\2\u013c\u013d\7g\2\2\u013d\u013e\7p\2\2\u013e.\3\2\2\2\u013f"+
		"\u0140\7v\2\2\u0140\u0141\7t\2\2\u0141\u0142\7w\2\2\u0142\u0143\7g\2\2"+
		"\u0143\60\3\2\2\2\u0144\u0145\7y\2\2\u0145\u0146\7j\2\2\u0146\u0147\7"+
		"g\2\2\u0147\u0148\7p\2\2\u0148\62\3\2\2\2\u0149\u014a\7y\2\2\u014a\u014b"+
		"\7j\2\2\u014b\u014c\7k\2\2\u014c\u014d\7n\2\2\u014d\u014e\7g\2\2\u014e"+
		"\64\3\2\2\2\u014f\u0150\7?\2\2\u0150\66\3\2\2\2\u0151\u0152\7B\2\2\u0152"+
		"8\3\2\2\2\u0153\u0154\7<\2\2\u0154:\3\2\2\2\u0155\u0156\7.\2\2\u0156<"+
		"\3\2\2\2\u0157\u0158\7\61\2\2\u0158>\3\2\2\2\u0159\u015a\7^\2\2\u015a"+
		"@\3\2\2\2\u015b\u015c\7&\2\2\u015cB\3\2\2\2\u015d\u015e\7\60\2\2\u015e"+
		"D\3\2\2\2\u015f\u0160\7,\2\2\u0160\u0161\7,\2\2\u0161F\3\2\2\2\u0162\u0163"+
		"\7/\2\2\u0163H\3\2\2\2\u0164\u0165\7\'\2\2\u0165J\3\2\2\2\u0166\u0167"+
		"\7,\2\2\u0167L\3\2\2\2\u0168\u016d\7#\2\2\u0169\u016a\7p\2\2\u016a\u016b"+
		"\7q\2\2\u016b\u016d\7v\2\2\u016c\u0168\3\2\2\2\u016c\u0169\3\2\2\2\u016d"+
		"N\3\2\2\2\u016e\u016f\7\u0080\2\2\u016fP\3\2\2\2\u0170\u0171\7-\2\2\u0171"+
		"R\3\2\2\2\u0172\u0173\7A\2\2\u0173T\3\2\2\2\u0174\u0175\7=\2\2\u0175V"+
		"\3\2\2\2\u0176\u0177\7%\2\2\u0177\u0178\7u\2\2\u0178\u0179\7g\2\2\u0179"+
		"\u017a\7v\2\2\u017aX\3\2\2\2\u017b\u017c\7,\2\2\u017c\u017d\7,\2\2\u017d"+
		"\u017e\7?\2\2\u017eZ\3\2\2\2\u017f\u0180\7,\2\2\u0180\u0181\7?\2\2\u0181"+
		"\\\3\2\2\2\u0182\u0183\7\61\2\2\u0183\u0184\7?\2\2\u0184^\3\2\2\2\u0185"+
		"\u0186\7^\2\2\u0186\u0187\7?\2\2\u0187`\3\2\2\2\u0188\u0189\7\'\2\2\u0189"+
		"\u018a\7?\2\2\u018ab\3\2\2\2\u018b\u018c\7-\2\2\u018c\u018d\7?\2\2\u018d"+
		"d\3\2\2\2\u018e\u018f\7/\2\2\u018f\u0190\7?\2\2\u0190f\3\2\2\2\u0191\u0192"+
		"\7>\2\2\u0192\u0193\7>\2\2\u0193\u0194\7?\2\2\u0194h\3\2\2\2\u0195\u0196"+
		"\7@\2\2\u0196\u0197\7@\2\2\u0197\u0198\7?\2\2\u0198j\3\2\2\2\u0199\u019a"+
		"\7(\2\2\u019a\u019b\7?\2\2\u019bl\3\2\2\2\u019c\u019d\7~\2\2\u019d\u019e"+
		"\7?\2\2\u019en\3\2\2\2\u019f\u01a0\7`\2\2\u01a0\u01a1\7?\2\2\u01a1p\3"+
		"\2\2\2\u01a2\u01a3\7(\2\2\u01a3\u01a4\7(\2\2\u01a4\u01a5\7?\2\2\u01a5"+
		"r\3\2\2\2\u01a6\u01a7\7~\2\2\u01a7\u01a8\7~\2\2\u01a8\u01a9\7?\2\2\u01a9"+
		"t\3\2\2\2\u01aa\u01ab\7>\2\2\u01abv\3\2\2\2\u01ac\u01ad\7>\2\2\u01ad\u01ae"+
		"\7?\2\2\u01aex\3\2\2\2\u01af\u01b0\7#\2\2\u01b0\u01b1\7?\2\2\u01b1z\3"+
		"\2\2\2\u01b2\u01b3\7?\2\2\u01b3\u01b4\7?\2\2\u01b4|\3\2\2\2\u01b5\u01b6"+
		"\7?\2\2\u01b6\u01b7\7?\2\2\u01b7\u01b8\7?\2\2\u01b8~\3\2\2\2\u01b9\u01ba"+
		"\7#\2\2\u01ba\u01bb\7?\2\2\u01bb\u01bc\7?\2\2\u01bc\u0080\3\2\2\2\u01bd"+
		"\u01be\7@\2\2\u01be\u01bf\7?\2\2\u01bf\u0082\3\2\2\2\u01c0\u01c1\7@\2"+
		"\2\u01c1\u0084\3\2\2\2\u01c2\u01c3\7(\2\2\u01c3\u01c8\7(\2\2\u01c4\u01c5"+
		"\7c\2\2\u01c5\u01c6\7p\2\2\u01c6\u01c8\7f\2\2\u01c7\u01c2\3\2\2\2\u01c7"+
		"\u01c4\3\2\2\2\u01c8\u0086\3\2\2\2\u01c9\u01ca\7~\2\2\u01ca\u01ce\7~\2"+
		"\2\u01cb\u01cc\7q\2\2\u01cc\u01ce\7t\2\2\u01cd\u01c9\3\2\2\2\u01cd\u01cb"+
		"\3\2\2\2\u01ce\u0088\3\2\2\2\u01cf\u01d0\7>\2\2\u01d0\u01d1\7>\2\2\u01d1"+
		"\u008a\3\2\2\2\u01d2\u01d3\7@\2\2\u01d3\u01d4\7@\2\2\u01d4\u008c\3\2\2"+
		"\2\u01d5\u01d6\7(\2\2\u01d6\u008e\3\2\2\2\u01d7\u01d8\7~\2\2\u01d8\u0090"+
		"\3\2\2\2\u01d9\u01da\7`\2\2\u01da\u0092\3\2\2\2\u01db\u01dc\7]\2\2\u01dc"+
		"\u0094\3\2\2\2\u01dd\u01de\7_\2\2\u01de\u0096\3\2\2\2\u01df\u01e0\7*\2"+
		"\2\u01e0\u0098\3\2\2\2\u01e1\u01e2\7+\2\2\u01e2\u009a\3\2\2\2\u01e3\u01e4"+
		"\7}\2\2\u01e4\u009c\3\2\2\2\u01e5\u01e6\7\177\2\2\u01e6\u009e\3\2\2\2"+
		"\u01e7\u01e8\7^\2\2\u01e8\u01e9\7$\2\2\u01e9\u00a0\3\2\2\2\u01ea\u01ef"+
		"\7$\2\2\u01eb\u01ee\5\u009fP\2\u01ec\u01ee\n\2\2\2\u01ed\u01eb\3\2\2\2"+
		"\u01ed\u01ec\3\2\2\2\u01ee\u01f1\3\2\2\2\u01ef\u01f0\3\2\2\2\u01ef\u01ed"+
		"\3\2\2\2\u01f0\u01f2\3\2\2\2\u01f1\u01ef\3\2\2\2\u01f2\u01f3\7$\2\2\u01f3"+
		"\u00a2\3\2\2\2\u01f4\u01fe\t\3\2\2\u01f5\u01f6\7g\2\2\u01f6\u01fe\7-\2"+
		"\2\u01f7\u01f8\7G\2\2\u01f8\u01fe\7-\2\2\u01f9\u01fa\7g\2\2\u01fa\u01fe"+
		"\7/\2\2\u01fb\u01fc\7G\2\2\u01fc\u01fe\7/\2\2\u01fd\u01f4\3\2\2\2\u01fd"+
		"\u01f5\3\2\2\2\u01fd\u01f7\3\2\2\2\u01fd\u01f9\3\2\2\2\u01fd\u01fb\3\2"+
		"\2\2\u01fe\u01ff\3\2\2\2\u01ff\u0200\5\u00a5S\2\u0200\u00a4\3\2\2\2\u0201"+
		"\u0203\4\62;\2\u0202\u0201\3\2\2\2\u0203\u0204\3\2\2\2\u0204\u0202\3\2"+
		"\2\2\u0204\u0205\3\2\2\2\u0205\u00a6\3\2\2\2\u0206\u0207\5\u00b7\\\2\u0207"+
		"\u00a8\3\2\2\2\u0208\u0209\5\u00abV\2\u0209\u00aa\3\2\2\2\u020a\u020e"+
		"\5\u00adW\2\u020b\u020e\5\u00afX\2\u020c\u020e\5\u00b1Y\2\u020d\u020a"+
		"\3\2\2\2\u020d\u020b\3\2\2\2\u020d\u020c\3\2\2\2\u020e\u00ac\3\2\2\2\u020f"+
		"\u0210\5\u00a5S\2\u0210\u00ae\3\2\2\2\u0211\u0212\7\62\2\2\u0212\u0213"+
		"\7d\2\2\u0213\u0215\3\2\2\2\u0214\u0216\4\62\63\2\u0215\u0214\3\2\2\2"+
		"\u0216\u0217\3\2\2\2\u0217\u0215\3\2\2\2\u0217\u0218\3\2\2\2\u0218\u00b0"+
		"\3\2\2\2\u0219\u021a\7\62\2\2\u021a\u021b\7z\2\2\u021b\u021d\3\2\2\2\u021c"+
		"\u021e\5\u00b5[\2\u021d\u021c\3\2\2\2\u021e\u021f\3\2\2\2\u021f\u021d"+
		"\3\2\2\2\u021f\u0220\3\2\2\2\u0220\u00b2\3\2\2\2\u0221\u0222\4\62;\2\u0222"+
		"\u00b4\3\2\2\2\u0223\u0224\t\4\2\2\u0224\u00b6\3\2\2\2\u0225\u0227\5\u00a5"+
		"S\2\u0226\u0228\5\u00a3R\2\u0227\u0226\3\2\2\2\u0227\u0228\3\2\2\2\u0228"+
		"\u0230\3\2\2\2\u0229\u022a\5\u00a5S\2\u022a\u022b\5C\"\2\u022b\u022d\5"+
		"\u00a5S\2\u022c\u022e\5\u00a3R\2\u022d\u022c\3\2\2\2\u022d\u022e\3\2\2"+
		"\2\u022e\u0230\3\2\2\2\u022f\u0225\3\2\2\2\u022f\u0229\3\2\2\2\u0230\u00b8"+
		"\3\2\2\2\u0231\u0232\5\u00b7\\\2\u0232\u00ba\3\2\2\2\u0233\u0234\5\u00ad"+
		"W\2\u0234\u00bc\3\2\2\2\u0235\u0236\5\u00b1Y\2\u0236\u00be\3\2\2\2\u0237"+
		"\u0238\5\u00afX\2\u0238\u00c0\3\2\2\2\u0239\u023d\t\5\2\2\u023a\u023c"+
		"\t\6\2\2\u023b\u023a\3\2\2\2\u023c\u023f\3\2\2\2\u023d\u023b\3\2\2\2\u023d"+
		"\u023e\3\2\2\2\u023e\u00c2\3\2\2\2\u023f\u023d\3\2\2\2\u0240\u0241\7\61"+
		"\2\2\u0241\u0242\7\61\2\2\u0242\u0246\3\2\2\2\u0243\u0245\n\2\2\2\u0244"+
		"\u0243\3\2\2\2\u0245\u0248\3\2\2\2\u0246\u0244\3\2\2\2\u0246\u0247\3\2"+
		"\2\2\u0247\u024a\3\2\2\2\u0248\u0246\3\2\2\2\u0249\u024b\7\17\2\2\u024a"+
		"\u0249\3\2\2\2\u024a\u024b\3\2\2\2\u024b\u024c\3\2\2\2\u024c\u024d\7\f"+
		"\2\2\u024d\u024e\3\2\2\2\u024e\u024f\bb\2\2\u024f\u00c4\3\2\2\2\u0250"+
		"\u0252\t\7\2\2\u0251\u0250\3\2\2\2\u0252\u0253\3\2\2\2\u0253\u0251\3\2"+
		"\2\2\u0253\u0254\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0256\bc\2\2\u0256"+
		"\u00c6\3\2\2\2\24\2\u016c\u01c7\u01cd\u01ed\u01ef\u01fd\u0204\u020d\u0217"+
		"\u021f\u0227\u022d\u022f\u023d\u0246\u024a\u0253\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}