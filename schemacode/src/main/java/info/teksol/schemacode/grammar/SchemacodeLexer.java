// Generated from Schemacode.g4 by ANTLR 4.9.1
package info.teksol.schemacode.grammar;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SchemacodeLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		As=1, At=2, Connected=3, Description=4, Dimensions=5, Disabled=6, Enabled=7, 
		End=8, Facing=9, File=10, Item=11, Links=12, Liquid=13, Logic=14, Mindcode=15, 
		Mlog=16, Name=17, Processor=18, Rgb=19, Schematic=20, Tag=21, Text=22, 
		To=23, Unit=24, Virtual=25, Assign=26, Colon=27, Comma=28, Dot=29, Minus=30, 
		Plus=31, North=32, South=33, East=34, West=35, LeftParen=36, RightParen=37, 
		TextBlock1=38, TextBlock2=39, TextLine=40, Int=41, Id=42, Ref=43, Pattern=44, 
		SLComment=45, Ws=46, Any=47;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"As", "At", "Connected", "Description", "Dimensions", "Disabled", "Enabled", 
			"End", "Facing", "File", "Item", "Links", "Liquid", "Logic", "Mindcode", 
			"Mlog", "Name", "Processor", "Rgb", "Schematic", "Tag", "Text", "To", 
			"Unit", "Virtual", "Assign", "Colon", "Comma", "Dot", "Minus", "Plus", 
			"North", "South", "East", "West", "LeftParen", "RightParen", "TextBlock1", 
			"TextBlock2", "TextLine", "Int", "Id", "Ref", "Pattern", "SLComment", 
			"Ws", "Any"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'connected'", "'description'", "'dimensions'", 
			"'disabled'", "'enabled'", "'end'", "'facing'", "'file'", "'item'", "'links'", 
			"'liquid'", "'logic'", "'mindcode'", "'mlog'", "'name'", "'processor'", 
			"'rgb'", "'schematic'", "'tag'", "'text'", "'to'", "'unit'", "'virtual'", 
			"'='", "':'", "','", "'.'", "'-'", "'+'", "'north'", "'south'", "'east'", 
			"'west'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "As", "At", "Connected", "Description", "Dimensions", "Disabled", 
			"Enabled", "End", "Facing", "File", "Item", "Links", "Liquid", "Logic", 
			"Mindcode", "Mlog", "Name", "Processor", "Rgb", "Schematic", "Tag", "Text", 
			"To", "Unit", "Virtual", "Assign", "Colon", "Comma", "Dot", "Minus", 
			"Plus", "North", "South", "East", "West", "LeftParen", "RightParen", 
			"TextBlock1", "TextBlock2", "TextLine", "Int", "Id", "Ref", "Pattern", 
			"SLComment", "Ws", "Any"
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


	public SchemacodeLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Schemacode.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\61\u0195\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!"+
		"\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$"+
		"\3%\3%\3&\3&\3\'\3\'\3\'\3\'\3\'\7\'\u0131\n\'\f\'\16\'\u0134\13\'\3\'"+
		"\3\'\7\'\u0138\n\'\f\'\16\'\u013b\13\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3("+
		"\7(\u0146\n(\f(\16(\u0149\13(\3(\3(\7(\u014d\n(\f(\16(\u0150\13(\3(\3"+
		"(\3(\3(\3)\3)\7)\u0158\n)\f)\16)\u015b\13)\3)\3)\3*\5*\u0160\n*\3*\6*"+
		"\u0163\n*\r*\16*\u0164\3+\3+\7+\u0169\n+\f+\16+\u016c\13+\3,\3,\3,\7,"+
		"\u0171\n,\f,\16,\u0174\13,\3-\3-\7-\u0178\n-\f-\16-\u017b\13-\3.\3.\3"+
		".\3.\7.\u0181\n.\f.\16.\u0184\13.\3.\5.\u0187\n.\3.\3.\3.\3.\3/\6/\u018e"+
		"\n/\r/\16/\u018f\3/\3/\3\60\3\60\5\u0139\u014e\u0159\2\61\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G"+
		"%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61\3\2\13\4\2\13\13\"\"\4\2\f\f\17\17\4\2"+
		"--//\3\2\62;\5\2C\\aac|\7\2//\62;C\\aac|\6\2,,C\\aac|\b\2,,//\62;C\\a"+
		"ac|\5\2\13\f\17\17\"\"\2\u01a1\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2"+
		"\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2"+
		"+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2"+
		"\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2"+
		"C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3"+
		"\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2"+
		"\2\2]\3\2\2\2\2_\3\2\2\2\3a\3\2\2\2\5d\3\2\2\2\7g\3\2\2\2\tq\3\2\2\2\13"+
		"}\3\2\2\2\r\u0088\3\2\2\2\17\u0091\3\2\2\2\21\u0099\3\2\2\2\23\u009d\3"+
		"\2\2\2\25\u00a4\3\2\2\2\27\u00a9\3\2\2\2\31\u00ae\3\2\2\2\33\u00b4\3\2"+
		"\2\2\35\u00bb\3\2\2\2\37\u00c1\3\2\2\2!\u00ca\3\2\2\2#\u00cf\3\2\2\2%"+
		"\u00d4\3\2\2\2\'\u00de\3\2\2\2)\u00e2\3\2\2\2+\u00ec\3\2\2\2-\u00f0\3"+
		"\2\2\2/\u00f5\3\2\2\2\61\u00f8\3\2\2\2\63\u00fd\3\2\2\2\65\u0105\3\2\2"+
		"\2\67\u0107\3\2\2\29\u0109\3\2\2\2;\u010b\3\2\2\2=\u010d\3\2\2\2?\u010f"+
		"\3\2\2\2A\u0111\3\2\2\2C\u0117\3\2\2\2E\u011d\3\2\2\2G\u0122\3\2\2\2I"+
		"\u0127\3\2\2\2K\u0129\3\2\2\2M\u012b\3\2\2\2O\u0140\3\2\2\2Q\u0155\3\2"+
		"\2\2S\u015f\3\2\2\2U\u0166\3\2\2\2W\u016d\3\2\2\2Y\u0175\3\2\2\2[\u017c"+
		"\3\2\2\2]\u018d\3\2\2\2_\u0193\3\2\2\2ab\7c\2\2bc\7u\2\2c\4\3\2\2\2de"+
		"\7c\2\2ef\7v\2\2f\6\3\2\2\2gh\7e\2\2hi\7q\2\2ij\7p\2\2jk\7p\2\2kl\7g\2"+
		"\2lm\7e\2\2mn\7v\2\2no\7g\2\2op\7f\2\2p\b\3\2\2\2qr\7f\2\2rs\7g\2\2st"+
		"\7u\2\2tu\7e\2\2uv\7t\2\2vw\7k\2\2wx\7r\2\2xy\7v\2\2yz\7k\2\2z{\7q\2\2"+
		"{|\7p\2\2|\n\3\2\2\2}~\7f\2\2~\177\7k\2\2\177\u0080\7o\2\2\u0080\u0081"+
		"\7g\2\2\u0081\u0082\7p\2\2\u0082\u0083\7u\2\2\u0083\u0084\7k\2\2\u0084"+
		"\u0085\7q\2\2\u0085\u0086\7p\2\2\u0086\u0087\7u\2\2\u0087\f\3\2\2\2\u0088"+
		"\u0089\7f\2\2\u0089\u008a\7k\2\2\u008a\u008b\7u\2\2\u008b\u008c\7c\2\2"+
		"\u008c\u008d\7d\2\2\u008d\u008e\7n\2\2\u008e\u008f\7g\2\2\u008f\u0090"+
		"\7f\2\2\u0090\16\3\2\2\2\u0091\u0092\7g\2\2\u0092\u0093\7p\2\2\u0093\u0094"+
		"\7c\2\2\u0094\u0095\7d\2\2\u0095\u0096\7n\2\2\u0096\u0097\7g\2\2\u0097"+
		"\u0098\7f\2\2\u0098\20\3\2\2\2\u0099\u009a\7g\2\2\u009a\u009b\7p\2\2\u009b"+
		"\u009c\7f\2\2\u009c\22\3\2\2\2\u009d\u009e\7h\2\2\u009e\u009f\7c\2\2\u009f"+
		"\u00a0\7e\2\2\u00a0\u00a1\7k\2\2\u00a1\u00a2\7p\2\2\u00a2\u00a3\7i\2\2"+
		"\u00a3\24\3\2\2\2\u00a4\u00a5\7h\2\2\u00a5\u00a6\7k\2\2\u00a6\u00a7\7"+
		"n\2\2\u00a7\u00a8\7g\2\2\u00a8\26\3\2\2\2\u00a9\u00aa\7k\2\2\u00aa\u00ab"+
		"\7v\2\2\u00ab\u00ac\7g\2\2\u00ac\u00ad\7o\2\2\u00ad\30\3\2\2\2\u00ae\u00af"+
		"\7n\2\2\u00af\u00b0\7k\2\2\u00b0\u00b1\7p\2\2\u00b1\u00b2\7m\2\2\u00b2"+
		"\u00b3\7u\2\2\u00b3\32\3\2\2\2\u00b4\u00b5\7n\2\2\u00b5\u00b6\7k\2\2\u00b6"+
		"\u00b7\7s\2\2\u00b7\u00b8\7w\2\2\u00b8\u00b9\7k\2\2\u00b9\u00ba\7f\2\2"+
		"\u00ba\34\3\2\2\2\u00bb\u00bc\7n\2\2\u00bc\u00bd\7q\2\2\u00bd\u00be\7"+
		"i\2\2\u00be\u00bf\7k\2\2\u00bf\u00c0\7e\2\2\u00c0\36\3\2\2\2\u00c1\u00c2"+
		"\7o\2\2\u00c2\u00c3\7k\2\2\u00c3\u00c4\7p\2\2\u00c4\u00c5\7f\2\2\u00c5"+
		"\u00c6\7e\2\2\u00c6\u00c7\7q\2\2\u00c7\u00c8\7f\2\2\u00c8\u00c9\7g\2\2"+
		"\u00c9 \3\2\2\2\u00ca\u00cb\7o\2\2\u00cb\u00cc\7n\2\2\u00cc\u00cd\7q\2"+
		"\2\u00cd\u00ce\7i\2\2\u00ce\"\3\2\2\2\u00cf\u00d0\7p\2\2\u00d0\u00d1\7"+
		"c\2\2\u00d1\u00d2\7o\2\2\u00d2\u00d3\7g\2\2\u00d3$\3\2\2\2\u00d4\u00d5"+
		"\7r\2\2\u00d5\u00d6\7t\2\2\u00d6\u00d7\7q\2\2\u00d7\u00d8\7e\2\2\u00d8"+
		"\u00d9\7g\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7u\2\2\u00db\u00dc\7q\2\2"+
		"\u00dc\u00dd\7t\2\2\u00dd&\3\2\2\2\u00de\u00df\7t\2\2\u00df\u00e0\7i\2"+
		"\2\u00e0\u00e1\7d\2\2\u00e1(\3\2\2\2\u00e2\u00e3\7u\2\2\u00e3\u00e4\7"+
		"e\2\2\u00e4\u00e5\7j\2\2\u00e5\u00e6\7g\2\2\u00e6\u00e7\7o\2\2\u00e7\u00e8"+
		"\7c\2\2\u00e8\u00e9\7v\2\2\u00e9\u00ea\7k\2\2\u00ea\u00eb\7e\2\2\u00eb"+
		"*\3\2\2\2\u00ec\u00ed\7v\2\2\u00ed\u00ee\7c\2\2\u00ee\u00ef\7i\2\2\u00ef"+
		",\3\2\2\2\u00f0\u00f1\7v\2\2\u00f1\u00f2\7g\2\2\u00f2\u00f3\7z\2\2\u00f3"+
		"\u00f4\7v\2\2\u00f4.\3\2\2\2\u00f5\u00f6\7v\2\2\u00f6\u00f7\7q\2\2\u00f7"+
		"\60\3\2\2\2\u00f8\u00f9\7w\2\2\u00f9\u00fa\7p\2\2\u00fa\u00fb\7k\2\2\u00fb"+
		"\u00fc\7v\2\2\u00fc\62\3\2\2\2\u00fd\u00fe\7x\2\2\u00fe\u00ff\7k\2\2\u00ff"+
		"\u0100\7t\2\2\u0100\u0101\7v\2\2\u0101\u0102\7w\2\2\u0102\u0103\7c\2\2"+
		"\u0103\u0104\7n\2\2\u0104\64\3\2\2\2\u0105\u0106\7?\2\2\u0106\66\3\2\2"+
		"\2\u0107\u0108\7<\2\2\u01088\3\2\2\2\u0109\u010a\7.\2\2\u010a:\3\2\2\2"+
		"\u010b\u010c\7\60\2\2\u010c<\3\2\2\2\u010d\u010e\7/\2\2\u010e>\3\2\2\2"+
		"\u010f\u0110\7-\2\2\u0110@\3\2\2\2\u0111\u0112\7p\2\2\u0112\u0113\7q\2"+
		"\2\u0113\u0114\7t\2\2\u0114\u0115\7v\2\2\u0115\u0116\7j\2\2\u0116B\3\2"+
		"\2\2\u0117\u0118\7u\2\2\u0118\u0119\7q\2\2\u0119\u011a\7w\2\2\u011a\u011b"+
		"\7v\2\2\u011b\u011c\7j\2\2\u011cD\3\2\2\2\u011d\u011e\7g\2\2\u011e\u011f"+
		"\7c\2\2\u011f\u0120\7u\2\2\u0120\u0121\7v\2\2\u0121F\3\2\2\2\u0122\u0123"+
		"\7y\2\2\u0123\u0124\7g\2\2\u0124\u0125\7u\2\2\u0125\u0126\7v\2\2\u0126"+
		"H\3\2\2\2\u0127\u0128\7*\2\2\u0128J\3\2\2\2\u0129\u012a\7+\2\2\u012aL"+
		"\3\2\2\2\u012b\u012c\7$\2\2\u012c\u012d\7$\2\2\u012d\u012e\7$\2\2\u012e"+
		"\u0132\3\2\2\2\u012f\u0131\t\2\2\2\u0130\u012f\3\2\2\2\u0131\u0134\3\2"+
		"\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134"+
		"\u0132\3\2\2\2\u0135\u0139\t\3\2\2\u0136\u0138\13\2\2\2\u0137\u0136\3"+
		"\2\2\2\u0138\u013b\3\2\2\2\u0139\u013a\3\2\2\2\u0139\u0137\3\2\2\2\u013a"+
		"\u013c\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u013d\7$\2\2\u013d\u013e\7$\2"+
		"\2\u013e\u013f\7$\2\2\u013fN\3\2\2\2\u0140\u0141\7)\2\2\u0141\u0142\7"+
		")\2\2\u0142\u0143\7)\2\2\u0143\u0147\3\2\2\2\u0144\u0146\t\2\2\2\u0145"+
		"\u0144\3\2\2\2\u0146\u0149\3\2\2\2\u0147\u0145\3\2\2\2\u0147\u0148\3\2"+
		"\2\2\u0148\u014a\3\2\2\2\u0149\u0147\3\2\2\2\u014a\u014e\t\3\2\2\u014b"+
		"\u014d\13\2\2\2\u014c\u014b\3\2\2\2\u014d\u0150\3\2\2\2\u014e\u014f\3"+
		"\2\2\2\u014e\u014c\3\2\2\2\u014f\u0151\3\2\2\2\u0150\u014e\3\2\2\2\u0151"+
		"\u0152\7)\2\2\u0152\u0153\7)\2\2\u0153\u0154\7)\2\2\u0154P\3\2\2\2\u0155"+
		"\u0159\7$\2\2\u0156\u0158\n\3\2\2\u0157\u0156\3\2\2\2\u0158\u015b\3\2"+
		"\2\2\u0159\u015a\3\2\2\2\u0159\u0157\3\2\2\2\u015a\u015c\3\2\2\2\u015b"+
		"\u0159\3\2\2\2\u015c\u015d\7$\2\2\u015dR\3\2\2\2\u015e\u0160\t\4\2\2\u015f"+
		"\u015e\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0162\3\2\2\2\u0161\u0163\t\5"+
		"\2\2\u0162\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0162\3\2\2\2\u0164"+
		"\u0165\3\2\2\2\u0165T\3\2\2\2\u0166\u016a\t\6\2\2\u0167\u0169\t\7\2\2"+
		"\u0168\u0167\3\2\2\2\u0169\u016c\3\2\2\2\u016a\u0168\3\2\2\2\u016a\u016b"+
		"\3\2\2\2\u016bV\3\2\2\2\u016c\u016a\3\2\2\2\u016d\u016e\7B\2\2\u016e\u0172"+
		"\t\6\2\2\u016f\u0171\t\7\2\2\u0170\u016f\3\2\2\2\u0171\u0174\3\2\2\2\u0172"+
		"\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173X\3\2\2\2\u0174\u0172\3\2\2\2"+
		"\u0175\u0179\t\b\2\2\u0176\u0178\t\t\2\2\u0177\u0176\3\2\2\2\u0178\u017b"+
		"\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017aZ\3\2\2\2\u017b"+
		"\u0179\3\2\2\2\u017c\u017d\7\61\2\2\u017d\u017e\7\61\2\2\u017e\u0182\3"+
		"\2\2\2\u017f\u0181\n\3\2\2\u0180\u017f\3\2\2\2\u0181\u0184\3\2\2\2\u0182"+
		"\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0186\3\2\2\2\u0184\u0182\3\2"+
		"\2\2\u0185\u0187\7\17\2\2\u0186\u0185\3\2\2\2\u0186\u0187\3\2\2\2\u0187"+
		"\u0188\3\2\2\2\u0188\u0189\7\f\2\2\u0189\u018a\3\2\2\2\u018a\u018b\b."+
		"\2\2\u018b\\\3\2\2\2\u018c\u018e\t\n\2\2\u018d\u018c\3\2\2\2\u018e\u018f"+
		"\3\2\2\2\u018f\u018d\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u0191\3\2\2\2\u0191"+
		"\u0192\b/\2\2\u0192^\3\2\2\2\u0193\u0194\13\2\2\2\u0194`\3\2\2\2\20\2"+
		"\u0132\u0139\u0147\u014e\u0159\u015f\u0164\u016a\u0172\u0179\u0182\u0186"+
		"\u018f\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}