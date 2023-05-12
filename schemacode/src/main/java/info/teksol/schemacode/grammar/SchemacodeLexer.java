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
		Mlog=16, Name=17, Processor=18, Schematic=19, Tag=20, Text=21, To=22, 
		Virtual=23, Assign=24, Colon=25, Comma=26, Dot=27, Minus=28, Plus=29, 
		North=30, South=31, East=32, West=33, LeftParen=34, RightParen=35, TextBlock1=36, 
		TextBlock2=37, TextLine=38, Int=39, Id=40, Ref=41, Pattern=42, SLComment=43, 
		Ws=44, Any=45;
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
			"Mlog", "Name", "Processor", "Schematic", "Tag", "Text", "To", "Virtual", 
			"Assign", "Colon", "Comma", "Dot", "Minus", "Plus", "North", "South", 
			"East", "West", "LeftParen", "RightParen", "TextBlock1", "TextBlock2", 
			"TextLine", "Int", "Id", "Ref", "Pattern", "SLComment", "Ws", "Any"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'connected'", "'description'", "'dimensions'", 
			"'disabled'", "'enabled'", "'end'", "'facing'", "'file'", "'item'", "'links'", 
			"'liquid'", "'logic'", "'mindcode'", "'mlog'", "'name'", "'processor'", 
			"'schematic'", "'tag'", "'text'", "'to'", "'virtual'", "'='", "':'", 
			"','", "'.'", "'-'", "'+'", "'north'", "'south'", "'east'", "'west'", 
			"'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "As", "At", "Connected", "Description", "Dimensions", "Disabled", 
			"Enabled", "End", "Facing", "File", "Item", "Links", "Liquid", "Logic", 
			"Mindcode", "Mlog", "Name", "Processor", "Schematic", "Tag", "Text", 
			"To", "Virtual", "Assign", "Colon", "Comma", "Dot", "Minus", "Plus", 
			"North", "South", "East", "West", "LeftParen", "RightParen", "TextBlock1", 
			"TextBlock2", "TextLine", "Int", "Id", "Ref", "Pattern", "SLComment", 
			"Ws", "Any"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2/\u0188\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3"+
		"\"\3\"\3\"\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3%\3%\7%\u0124\n%\f%\16%\u0127"+
		"\13%\3%\3%\7%\u012b\n%\f%\16%\u012e\13%\3%\3%\3%\3%\3&\3&\3&\3&\3&\7&"+
		"\u0139\n&\f&\16&\u013c\13&\3&\3&\7&\u0140\n&\f&\16&\u0143\13&\3&\3&\3"+
		"&\3&\3\'\3\'\7\'\u014b\n\'\f\'\16\'\u014e\13\'\3\'\3\'\3(\5(\u0153\n("+
		"\3(\6(\u0156\n(\r(\16(\u0157\3)\3)\7)\u015c\n)\f)\16)\u015f\13)\3*\3*"+
		"\3*\7*\u0164\n*\f*\16*\u0167\13*\3+\3+\7+\u016b\n+\f+\16+\u016e\13+\3"+
		",\3,\3,\3,\7,\u0174\n,\f,\16,\u0177\13,\3,\5,\u017a\n,\3,\3,\3,\3,\3-"+
		"\6-\u0181\n-\r-\16-\u0182\3-\3-\3.\3.\5\u012c\u0141\u014c\2/\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C"+
		"#E$G%I&K\'M(O)Q*S+U,W-Y.[/\3\2\13\4\2\13\13\"\"\4\2\f\f\17\17\4\2--//"+
		"\3\2\62;\5\2C\\aac|\7\2//\62;C\\aac|\6\2,,C\\aac|\b\2,,//\62;C\\aac|\5"+
		"\2\13\f\17\17\"\"\2\u0194\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\3]"+
		"\3\2\2\2\5`\3\2\2\2\7c\3\2\2\2\tm\3\2\2\2\13y\3\2\2\2\r\u0084\3\2\2\2"+
		"\17\u008d\3\2\2\2\21\u0095\3\2\2\2\23\u0099\3\2\2\2\25\u00a0\3\2\2\2\27"+
		"\u00a5\3\2\2\2\31\u00aa\3\2\2\2\33\u00b0\3\2\2\2\35\u00b7\3\2\2\2\37\u00bd"+
		"\3\2\2\2!\u00c6\3\2\2\2#\u00cb\3\2\2\2%\u00d0\3\2\2\2\'\u00da\3\2\2\2"+
		")\u00e4\3\2\2\2+\u00e8\3\2\2\2-\u00ed\3\2\2\2/\u00f0\3\2\2\2\61\u00f8"+
		"\3\2\2\2\63\u00fa\3\2\2\2\65\u00fc\3\2\2\2\67\u00fe\3\2\2\29\u0100\3\2"+
		"\2\2;\u0102\3\2\2\2=\u0104\3\2\2\2?\u010a\3\2\2\2A\u0110\3\2\2\2C\u0115"+
		"\3\2\2\2E\u011a\3\2\2\2G\u011c\3\2\2\2I\u011e\3\2\2\2K\u0133\3\2\2\2M"+
		"\u0148\3\2\2\2O\u0152\3\2\2\2Q\u0159\3\2\2\2S\u0160\3\2\2\2U\u0168\3\2"+
		"\2\2W\u016f\3\2\2\2Y\u0180\3\2\2\2[\u0186\3\2\2\2]^\7c\2\2^_\7u\2\2_\4"+
		"\3\2\2\2`a\7c\2\2ab\7v\2\2b\6\3\2\2\2cd\7e\2\2de\7q\2\2ef\7p\2\2fg\7p"+
		"\2\2gh\7g\2\2hi\7e\2\2ij\7v\2\2jk\7g\2\2kl\7f\2\2l\b\3\2\2\2mn\7f\2\2"+
		"no\7g\2\2op\7u\2\2pq\7e\2\2qr\7t\2\2rs\7k\2\2st\7r\2\2tu\7v\2\2uv\7k\2"+
		"\2vw\7q\2\2wx\7p\2\2x\n\3\2\2\2yz\7f\2\2z{\7k\2\2{|\7o\2\2|}\7g\2\2}~"+
		"\7p\2\2~\177\7u\2\2\177\u0080\7k\2\2\u0080\u0081\7q\2\2\u0081\u0082\7"+
		"p\2\2\u0082\u0083\7u\2\2\u0083\f\3\2\2\2\u0084\u0085\7f\2\2\u0085\u0086"+
		"\7k\2\2\u0086\u0087\7u\2\2\u0087\u0088\7c\2\2\u0088\u0089\7d\2\2\u0089"+
		"\u008a\7n\2\2\u008a\u008b\7g\2\2\u008b\u008c\7f\2\2\u008c\16\3\2\2\2\u008d"+
		"\u008e\7g\2\2\u008e\u008f\7p\2\2\u008f\u0090\7c\2\2\u0090\u0091\7d\2\2"+
		"\u0091\u0092\7n\2\2\u0092\u0093\7g\2\2\u0093\u0094\7f\2\2\u0094\20\3\2"+
		"\2\2\u0095\u0096\7g\2\2\u0096\u0097\7p\2\2\u0097\u0098\7f\2\2\u0098\22"+
		"\3\2\2\2\u0099\u009a\7h\2\2\u009a\u009b\7c\2\2\u009b\u009c\7e\2\2\u009c"+
		"\u009d\7k\2\2\u009d\u009e\7p\2\2\u009e\u009f\7i\2\2\u009f\24\3\2\2\2\u00a0"+
		"\u00a1\7h\2\2\u00a1\u00a2\7k\2\2\u00a2\u00a3\7n\2\2\u00a3\u00a4\7g\2\2"+
		"\u00a4\26\3\2\2\2\u00a5\u00a6\7k\2\2\u00a6\u00a7\7v\2\2\u00a7\u00a8\7"+
		"g\2\2\u00a8\u00a9\7o\2\2\u00a9\30\3\2\2\2\u00aa\u00ab\7n\2\2\u00ab\u00ac"+
		"\7k\2\2\u00ac\u00ad\7p\2\2\u00ad\u00ae\7m\2\2\u00ae\u00af\7u\2\2\u00af"+
		"\32\3\2\2\2\u00b0\u00b1\7n\2\2\u00b1\u00b2\7k\2\2\u00b2\u00b3\7s\2\2\u00b3"+
		"\u00b4\7w\2\2\u00b4\u00b5\7k\2\2\u00b5\u00b6\7f\2\2\u00b6\34\3\2\2\2\u00b7"+
		"\u00b8\7n\2\2\u00b8\u00b9\7q\2\2\u00b9\u00ba\7i\2\2\u00ba\u00bb\7k\2\2"+
		"\u00bb\u00bc\7e\2\2\u00bc\36\3\2\2\2\u00bd\u00be\7o\2\2\u00be\u00bf\7"+
		"k\2\2\u00bf\u00c0\7p\2\2\u00c0\u00c1\7f\2\2\u00c1\u00c2\7e\2\2\u00c2\u00c3"+
		"\7q\2\2\u00c3\u00c4\7f\2\2\u00c4\u00c5\7g\2\2\u00c5 \3\2\2\2\u00c6\u00c7"+
		"\7o\2\2\u00c7\u00c8\7n\2\2\u00c8\u00c9\7q\2\2\u00c9\u00ca\7i\2\2\u00ca"+
		"\"\3\2\2\2\u00cb\u00cc\7p\2\2\u00cc\u00cd\7c\2\2\u00cd\u00ce\7o\2\2\u00ce"+
		"\u00cf\7g\2\2\u00cf$\3\2\2\2\u00d0\u00d1\7r\2\2\u00d1\u00d2\7t\2\2\u00d2"+
		"\u00d3\7q\2\2\u00d3\u00d4\7e\2\2\u00d4\u00d5\7g\2\2\u00d5\u00d6\7u\2\2"+
		"\u00d6\u00d7\7u\2\2\u00d7\u00d8\7q\2\2\u00d8\u00d9\7t\2\2\u00d9&\3\2\2"+
		"\2\u00da\u00db\7u\2\2\u00db\u00dc\7e\2\2\u00dc\u00dd\7j\2\2\u00dd\u00de"+
		"\7g\2\2\u00de\u00df\7o\2\2\u00df\u00e0\7c\2\2\u00e0\u00e1\7v\2\2\u00e1"+
		"\u00e2\7k\2\2\u00e2\u00e3\7e\2\2\u00e3(\3\2\2\2\u00e4\u00e5\7v\2\2\u00e5"+
		"\u00e6\7c\2\2\u00e6\u00e7\7i\2\2\u00e7*\3\2\2\2\u00e8\u00e9\7v\2\2\u00e9"+
		"\u00ea\7g\2\2\u00ea\u00eb\7z\2\2\u00eb\u00ec\7v\2\2\u00ec,\3\2\2\2\u00ed"+
		"\u00ee\7v\2\2\u00ee\u00ef\7q\2\2\u00ef.\3\2\2\2\u00f0\u00f1\7x\2\2\u00f1"+
		"\u00f2\7k\2\2\u00f2\u00f3\7t\2\2\u00f3\u00f4\7v\2\2\u00f4\u00f5\7w\2\2"+
		"\u00f5\u00f6\7c\2\2\u00f6\u00f7\7n\2\2\u00f7\60\3\2\2\2\u00f8\u00f9\7"+
		"?\2\2\u00f9\62\3\2\2\2\u00fa\u00fb\7<\2\2\u00fb\64\3\2\2\2\u00fc\u00fd"+
		"\7.\2\2\u00fd\66\3\2\2\2\u00fe\u00ff\7\60\2\2\u00ff8\3\2\2\2\u0100\u0101"+
		"\7/\2\2\u0101:\3\2\2\2\u0102\u0103\7-\2\2\u0103<\3\2\2\2\u0104\u0105\7"+
		"p\2\2\u0105\u0106\7q\2\2\u0106\u0107\7t\2\2\u0107\u0108\7v\2\2\u0108\u0109"+
		"\7j\2\2\u0109>\3\2\2\2\u010a\u010b\7u\2\2\u010b\u010c\7q\2\2\u010c\u010d"+
		"\7w\2\2\u010d\u010e\7v\2\2\u010e\u010f\7j\2\2\u010f@\3\2\2\2\u0110\u0111"+
		"\7g\2\2\u0111\u0112\7c\2\2\u0112\u0113\7u\2\2\u0113\u0114\7v\2\2\u0114"+
		"B\3\2\2\2\u0115\u0116\7y\2\2\u0116\u0117\7g\2\2\u0117\u0118\7u\2\2\u0118"+
		"\u0119\7v\2\2\u0119D\3\2\2\2\u011a\u011b\7*\2\2\u011bF\3\2\2\2\u011c\u011d"+
		"\7+\2\2\u011dH\3\2\2\2\u011e\u011f\7$\2\2\u011f\u0120\7$\2\2\u0120\u0121"+
		"\7$\2\2\u0121\u0125\3\2\2\2\u0122\u0124\t\2\2\2\u0123\u0122\3\2\2\2\u0124"+
		"\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0128\3\2"+
		"\2\2\u0127\u0125\3\2\2\2\u0128\u012c\t\3\2\2\u0129\u012b\13\2\2\2\u012a"+
		"\u0129\3\2\2\2\u012b\u012e\3\2\2\2\u012c\u012d\3\2\2\2\u012c\u012a\3\2"+
		"\2\2\u012d\u012f\3\2\2\2\u012e\u012c\3\2\2\2\u012f\u0130\7$\2\2\u0130"+
		"\u0131\7$\2\2\u0131\u0132\7$\2\2\u0132J\3\2\2\2\u0133\u0134\7)\2\2\u0134"+
		"\u0135\7)\2\2\u0135\u0136\7)\2\2\u0136\u013a\3\2\2\2\u0137\u0139\t\2\2"+
		"\2\u0138\u0137\3\2\2\2\u0139\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b"+
		"\3\2\2\2\u013b\u013d\3\2\2\2\u013c\u013a\3\2\2\2\u013d\u0141\t\3\2\2\u013e"+
		"\u0140\13\2\2\2\u013f\u013e\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u0142\3"+
		"\2\2\2\u0141\u013f\3\2\2\2\u0142\u0144\3\2\2\2\u0143\u0141\3\2\2\2\u0144"+
		"\u0145\7)\2\2\u0145\u0146\7)\2\2\u0146\u0147\7)\2\2\u0147L\3\2\2\2\u0148"+
		"\u014c\7$\2\2\u0149\u014b\n\3\2\2\u014a\u0149\3\2\2\2\u014b\u014e\3\2"+
		"\2\2\u014c\u014d\3\2\2\2\u014c\u014a\3\2\2\2\u014d\u014f\3\2\2\2\u014e"+
		"\u014c\3\2\2\2\u014f\u0150\7$\2\2\u0150N\3\2\2\2\u0151\u0153\t\4\2\2\u0152"+
		"\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0155\3\2\2\2\u0154\u0156\t\5"+
		"\2\2\u0155\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0155\3\2\2\2\u0157"+
		"\u0158\3\2\2\2\u0158P\3\2\2\2\u0159\u015d\t\6\2\2\u015a\u015c\t\7\2\2"+
		"\u015b\u015a\3\2\2\2\u015c\u015f\3\2\2\2\u015d\u015b\3\2\2\2\u015d\u015e"+
		"\3\2\2\2\u015eR\3\2\2\2\u015f\u015d\3\2\2\2\u0160\u0161\7B\2\2\u0161\u0165"+
		"\t\6\2\2\u0162\u0164\t\7\2\2\u0163\u0162\3\2\2\2\u0164\u0167\3\2\2\2\u0165"+
		"\u0163\3\2\2\2\u0165\u0166\3\2\2\2\u0166T\3\2\2\2\u0167\u0165\3\2\2\2"+
		"\u0168\u016c\t\b\2\2\u0169\u016b\t\t\2\2\u016a\u0169\3\2\2\2\u016b\u016e"+
		"\3\2\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016dV\3\2\2\2\u016e"+
		"\u016c\3\2\2\2\u016f\u0170\7\61\2\2\u0170\u0171\7\61\2\2\u0171\u0175\3"+
		"\2\2\2\u0172\u0174\n\3\2\2\u0173\u0172\3\2\2\2\u0174\u0177\3\2\2\2\u0175"+
		"\u0173\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0179\3\2\2\2\u0177\u0175\3\2"+
		"\2\2\u0178\u017a\7\17\2\2\u0179\u0178\3\2\2\2\u0179\u017a\3\2\2\2\u017a"+
		"\u017b\3\2\2\2\u017b\u017c\7\f\2\2\u017c\u017d\3\2\2\2\u017d\u017e\b,"+
		"\2\2\u017eX\3\2\2\2\u017f\u0181\t\n\2\2\u0180\u017f\3\2\2\2\u0181\u0182"+
		"\3\2\2\2\u0182\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184\3\2\2\2\u0184"+
		"\u0185\b-\2\2\u0185Z\3\2\2\2\u0186\u0187\13\2\2\2\u0187\\\3\2\2\2\20\2"+
		"\u0125\u012c\u013a\u0141\u014c\u0152\u0157\u015d\u0165\u016c\u0175\u0179"+
		"\u0182\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}