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
		As=1, At=2, Connected=3, Color=4, Description=5, Dimensions=6, Disabled=7, 
		Enabled=8, End=9, Facing=10, File=11, Item=12, Links=13, Liquid=14, Logic=15, 
		Mindcode=16, Mlog=17, Name=18, Processor=19, Rgba=20, Schematic=21, Tag=22, 
		Text=23, To=24, Unit=25, Virtual=26, Assign=27, Colon=28, Comma=29, Dot=30, 
		Minus=31, Plus=32, North=33, South=34, East=35, West=36, LeftParen=37, 
		RightParen=38, TextBlock1=39, TextBlock2=40, TextLine=41, Int=42, Id=43, 
		Ref=44, Pattern=45, SLComment=46, Ws=47, Any=48;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"As", "At", "Connected", "Color", "Description", "Dimensions", "Disabled", 
			"Enabled", "End", "Facing", "File", "Item", "Links", "Liquid", "Logic", 
			"Mindcode", "Mlog", "Name", "Processor", "Rgba", "Schematic", "Tag", 
			"Text", "To", "Unit", "Virtual", "Assign", "Colon", "Comma", "Dot", "Minus", 
			"Plus", "North", "South", "East", "West", "LeftParen", "RightParen", 
			"TextBlock1", "TextBlock2", "TextLine", "Int", "Id", "Ref", "Pattern", 
			"SLComment", "Ws", "Any"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'connected'", "'color'", "'description'", "'dimensions'", 
			"'disabled'", "'enabled'", "'end'", "'facing'", "'file'", "'item'", "'links'", 
			"'liquid'", "'logic'", "'mindcode'", "'mlog'", "'name'", "'processor'", 
			"'rgba'", "'schematic'", "'tag'", "'text'", "'to'", "'unit'", "'virtual'", 
			"'='", "':'", "','", "'.'", "'-'", "'+'", "'north'", "'south'", "'east'", 
			"'west'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "As", "At", "Connected", "Color", "Description", "Dimensions", 
			"Disabled", "Enabled", "End", "Facing", "File", "Item", "Links", "Liquid", 
			"Logic", "Mindcode", "Mlog", "Name", "Processor", "Rgba", "Schematic", 
			"Tag", "Text", "To", "Unit", "Virtual", "Assign", "Colon", "Comma", "Dot", 
			"Minus", "Plus", "North", "South", "East", "West", "LeftParen", "RightParen", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\62\u019e\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\3\2\3\2\3\2\3\3\3\3\3\3"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f"+
		"\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32"+
		"\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3"+
		"#\3#\3#\3#\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\3(\3(\3("+
		"\7(\u013a\n(\f(\16(\u013d\13(\3(\3(\7(\u0141\n(\f(\16(\u0144\13(\3(\3"+
		"(\3(\3(\3)\3)\3)\3)\3)\7)\u014f\n)\f)\16)\u0152\13)\3)\3)\7)\u0156\n)"+
		"\f)\16)\u0159\13)\3)\3)\3)\3)\3*\3*\7*\u0161\n*\f*\16*\u0164\13*\3*\3"+
		"*\3+\5+\u0169\n+\3+\6+\u016c\n+\r+\16+\u016d\3,\3,\7,\u0172\n,\f,\16,"+
		"\u0175\13,\3-\3-\3-\7-\u017a\n-\f-\16-\u017d\13-\3.\3.\7.\u0181\n.\f."+
		"\16.\u0184\13.\3/\3/\3/\3/\7/\u018a\n/\f/\16/\u018d\13/\3/\5/\u0190\n"+
		"/\3/\3/\3/\3/\3\60\6\60\u0197\n\60\r\60\16\60\u0198\3\60\3\60\3\61\3\61"+
		"\5\u0142\u0157\u0162\2\62\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a"+
		"\62\3\2\13\4\2\13\13\"\"\4\2\f\f\17\17\4\2--//\3\2\62;\5\2C\\aac|\7\2"+
		"//\62;C\\aac|\6\2,,C\\aac|\b\2,,//\62;C\\aac|\5\2\13\f\17\17\"\"\2\u01aa"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
		"\2\2\2\3c\3\2\2\2\5f\3\2\2\2\7i\3\2\2\2\ts\3\2\2\2\13y\3\2\2\2\r\u0085"+
		"\3\2\2\2\17\u0090\3\2\2\2\21\u0099\3\2\2\2\23\u00a1\3\2\2\2\25\u00a5\3"+
		"\2\2\2\27\u00ac\3\2\2\2\31\u00b1\3\2\2\2\33\u00b6\3\2\2\2\35\u00bc\3\2"+
		"\2\2\37\u00c3\3\2\2\2!\u00c9\3\2\2\2#\u00d2\3\2\2\2%\u00d7\3\2\2\2\'\u00dc"+
		"\3\2\2\2)\u00e6\3\2\2\2+\u00eb\3\2\2\2-\u00f5\3\2\2\2/\u00f9\3\2\2\2\61"+
		"\u00fe\3\2\2\2\63\u0101\3\2\2\2\65\u0106\3\2\2\2\67\u010e\3\2\2\29\u0110"+
		"\3\2\2\2;\u0112\3\2\2\2=\u0114\3\2\2\2?\u0116\3\2\2\2A\u0118\3\2\2\2C"+
		"\u011a\3\2\2\2E\u0120\3\2\2\2G\u0126\3\2\2\2I\u012b\3\2\2\2K\u0130\3\2"+
		"\2\2M\u0132\3\2\2\2O\u0134\3\2\2\2Q\u0149\3\2\2\2S\u015e\3\2\2\2U\u0168"+
		"\3\2\2\2W\u016f\3\2\2\2Y\u0176\3\2\2\2[\u017e\3\2\2\2]\u0185\3\2\2\2_"+
		"\u0196\3\2\2\2a\u019c\3\2\2\2cd\7c\2\2de\7u\2\2e\4\3\2\2\2fg\7c\2\2gh"+
		"\7v\2\2h\6\3\2\2\2ij\7e\2\2jk\7q\2\2kl\7p\2\2lm\7p\2\2mn\7g\2\2no\7e\2"+
		"\2op\7v\2\2pq\7g\2\2qr\7f\2\2r\b\3\2\2\2st\7e\2\2tu\7q\2\2uv\7n\2\2vw"+
		"\7q\2\2wx\7t\2\2x\n\3\2\2\2yz\7f\2\2z{\7g\2\2{|\7u\2\2|}\7e\2\2}~\7t\2"+
		"\2~\177\7k\2\2\177\u0080\7r\2\2\u0080\u0081\7v\2\2\u0081\u0082\7k\2\2"+
		"\u0082\u0083\7q\2\2\u0083\u0084\7p\2\2\u0084\f\3\2\2\2\u0085\u0086\7f"+
		"\2\2\u0086\u0087\7k\2\2\u0087\u0088\7o\2\2\u0088\u0089\7g\2\2\u0089\u008a"+
		"\7p\2\2\u008a\u008b\7u\2\2\u008b\u008c\7k\2\2\u008c\u008d\7q\2\2\u008d"+
		"\u008e\7p\2\2\u008e\u008f\7u\2\2\u008f\16\3\2\2\2\u0090\u0091\7f\2\2\u0091"+
		"\u0092\7k\2\2\u0092\u0093\7u\2\2\u0093\u0094\7c\2\2\u0094\u0095\7d\2\2"+
		"\u0095\u0096\7n\2\2\u0096\u0097\7g\2\2\u0097\u0098\7f\2\2\u0098\20\3\2"+
		"\2\2\u0099\u009a\7g\2\2\u009a\u009b\7p\2\2\u009b\u009c\7c\2\2\u009c\u009d"+
		"\7d\2\2\u009d\u009e\7n\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7f\2\2\u00a0"+
		"\22\3\2\2\2\u00a1\u00a2\7g\2\2\u00a2\u00a3\7p\2\2\u00a3\u00a4\7f\2\2\u00a4"+
		"\24\3\2\2\2\u00a5\u00a6\7h\2\2\u00a6\u00a7\7c\2\2\u00a7\u00a8\7e\2\2\u00a8"+
		"\u00a9\7k\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7i\2\2\u00ab\26\3\2\2\2\u00ac"+
		"\u00ad\7h\2\2\u00ad\u00ae\7k\2\2\u00ae\u00af\7n\2\2\u00af\u00b0\7g\2\2"+
		"\u00b0\30\3\2\2\2\u00b1\u00b2\7k\2\2\u00b2\u00b3\7v\2\2\u00b3\u00b4\7"+
		"g\2\2\u00b4\u00b5\7o\2\2\u00b5\32\3\2\2\2\u00b6\u00b7\7n\2\2\u00b7\u00b8"+
		"\7k\2\2\u00b8\u00b9\7p\2\2\u00b9\u00ba\7m\2\2\u00ba\u00bb\7u\2\2\u00bb"+
		"\34\3\2\2\2\u00bc\u00bd\7n\2\2\u00bd\u00be\7k\2\2\u00be\u00bf\7s\2\2\u00bf"+
		"\u00c0\7w\2\2\u00c0\u00c1\7k\2\2\u00c1\u00c2\7f\2\2\u00c2\36\3\2\2\2\u00c3"+
		"\u00c4\7n\2\2\u00c4\u00c5\7q\2\2\u00c5\u00c6\7i\2\2\u00c6\u00c7\7k\2\2"+
		"\u00c7\u00c8\7e\2\2\u00c8 \3\2\2\2\u00c9\u00ca\7o\2\2\u00ca\u00cb\7k\2"+
		"\2\u00cb\u00cc\7p\2\2\u00cc\u00cd\7f\2\2\u00cd\u00ce\7e\2\2\u00ce\u00cf"+
		"\7q\2\2\u00cf\u00d0\7f\2\2\u00d0\u00d1\7g\2\2\u00d1\"\3\2\2\2\u00d2\u00d3"+
		"\7o\2\2\u00d3\u00d4\7n\2\2\u00d4\u00d5\7q\2\2\u00d5\u00d6\7i\2\2\u00d6"+
		"$\3\2\2\2\u00d7\u00d8\7p\2\2\u00d8\u00d9\7c\2\2\u00d9\u00da\7o\2\2\u00da"+
		"\u00db\7g\2\2\u00db&\3\2\2\2\u00dc\u00dd\7r\2\2\u00dd\u00de\7t\2\2\u00de"+
		"\u00df\7q\2\2\u00df\u00e0\7e\2\2\u00e0\u00e1\7g\2\2\u00e1\u00e2\7u\2\2"+
		"\u00e2\u00e3\7u\2\2\u00e3\u00e4\7q\2\2\u00e4\u00e5\7t\2\2\u00e5(\3\2\2"+
		"\2\u00e6\u00e7\7t\2\2\u00e7\u00e8\7i\2\2\u00e8\u00e9\7d\2\2\u00e9\u00ea"+
		"\7c\2\2\u00ea*\3\2\2\2\u00eb\u00ec\7u\2\2\u00ec\u00ed\7e\2\2\u00ed\u00ee"+
		"\7j\2\2\u00ee\u00ef\7g\2\2\u00ef\u00f0\7o\2\2\u00f0\u00f1\7c\2\2\u00f1"+
		"\u00f2\7v\2\2\u00f2\u00f3\7k\2\2\u00f3\u00f4\7e\2\2\u00f4,\3\2\2\2\u00f5"+
		"\u00f6\7v\2\2\u00f6\u00f7\7c\2\2\u00f7\u00f8\7i\2\2\u00f8.\3\2\2\2\u00f9"+
		"\u00fa\7v\2\2\u00fa\u00fb\7g\2\2\u00fb\u00fc\7z\2\2\u00fc\u00fd\7v\2\2"+
		"\u00fd\60\3\2\2\2\u00fe\u00ff\7v\2\2\u00ff\u0100\7q\2\2\u0100\62\3\2\2"+
		"\2\u0101\u0102\7w\2\2\u0102\u0103\7p\2\2\u0103\u0104\7k\2\2\u0104\u0105"+
		"\7v\2\2\u0105\64\3\2\2\2\u0106\u0107\7x\2\2\u0107\u0108\7k\2\2\u0108\u0109"+
		"\7t\2\2\u0109\u010a\7v\2\2\u010a\u010b\7w\2\2\u010b\u010c\7c\2\2\u010c"+
		"\u010d\7n\2\2\u010d\66\3\2\2\2\u010e\u010f\7?\2\2\u010f8\3\2\2\2\u0110"+
		"\u0111\7<\2\2\u0111:\3\2\2\2\u0112\u0113\7.\2\2\u0113<\3\2\2\2\u0114\u0115"+
		"\7\60\2\2\u0115>\3\2\2\2\u0116\u0117\7/\2\2\u0117@\3\2\2\2\u0118\u0119"+
		"\7-\2\2\u0119B\3\2\2\2\u011a\u011b\7p\2\2\u011b\u011c\7q\2\2\u011c\u011d"+
		"\7t\2\2\u011d\u011e\7v\2\2\u011e\u011f\7j\2\2\u011fD\3\2\2\2\u0120\u0121"+
		"\7u\2\2\u0121\u0122\7q\2\2\u0122\u0123\7w\2\2\u0123\u0124\7v\2\2\u0124"+
		"\u0125\7j\2\2\u0125F\3\2\2\2\u0126\u0127\7g\2\2\u0127\u0128\7c\2\2\u0128"+
		"\u0129\7u\2\2\u0129\u012a\7v\2\2\u012aH\3\2\2\2\u012b\u012c\7y\2\2\u012c"+
		"\u012d\7g\2\2\u012d\u012e\7u\2\2\u012e\u012f\7v\2\2\u012fJ\3\2\2\2\u0130"+
		"\u0131\7*\2\2\u0131L\3\2\2\2\u0132\u0133\7+\2\2\u0133N\3\2\2\2\u0134\u0135"+
		"\7$\2\2\u0135\u0136\7$\2\2\u0136\u0137\7$\2\2\u0137\u013b\3\2\2\2\u0138"+
		"\u013a\t\2\2\2\u0139\u0138\3\2\2\2\u013a\u013d\3\2\2\2\u013b\u0139\3\2"+
		"\2\2\u013b\u013c\3\2\2\2\u013c\u013e\3\2\2\2\u013d\u013b\3\2\2\2\u013e"+
		"\u0142\t\3\2\2\u013f\u0141\13\2\2\2\u0140\u013f\3\2\2\2\u0141\u0144\3"+
		"\2\2\2\u0142\u0143\3\2\2\2\u0142\u0140\3\2\2\2\u0143\u0145\3\2\2\2\u0144"+
		"\u0142\3\2\2\2\u0145\u0146\7$\2\2\u0146\u0147\7$\2\2\u0147\u0148\7$\2"+
		"\2\u0148P\3\2\2\2\u0149\u014a\7)\2\2\u014a\u014b\7)\2\2\u014b\u014c\7"+
		")\2\2\u014c\u0150\3\2\2\2\u014d\u014f\t\2\2\2\u014e\u014d\3\2\2\2\u014f"+
		"\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u0153\3\2"+
		"\2\2\u0152\u0150\3\2\2\2\u0153\u0157\t\3\2\2\u0154\u0156\13\2\2\2\u0155"+
		"\u0154\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0158\3\2\2\2\u0157\u0155\3\2"+
		"\2\2\u0158\u015a\3\2\2\2\u0159\u0157\3\2\2\2\u015a\u015b\7)\2\2\u015b"+
		"\u015c\7)\2\2\u015c\u015d\7)\2\2\u015dR\3\2\2\2\u015e\u0162\7$\2\2\u015f"+
		"\u0161\n\3\2\2\u0160\u015f\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0163\3\2"+
		"\2\2\u0162\u0160\3\2\2\2\u0163\u0165\3\2\2\2\u0164\u0162\3\2\2\2\u0165"+
		"\u0166\7$\2\2\u0166T\3\2\2\2\u0167\u0169\t\4\2\2\u0168\u0167\3\2\2\2\u0168"+
		"\u0169\3\2\2\2\u0169\u016b\3\2\2\2\u016a\u016c\t\5\2\2\u016b\u016a\3\2"+
		"\2\2\u016c\u016d\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e"+
		"V\3\2\2\2\u016f\u0173\t\6\2\2\u0170\u0172\t\7\2\2\u0171\u0170\3\2\2\2"+
		"\u0172\u0175\3\2\2\2\u0173\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174X\3"+
		"\2\2\2\u0175\u0173\3\2\2\2\u0176\u0177\7B\2\2\u0177\u017b\t\6\2\2\u0178"+
		"\u017a\t\7\2\2\u0179\u0178\3\2\2\2\u017a\u017d\3\2\2\2\u017b\u0179\3\2"+
		"\2\2\u017b\u017c\3\2\2\2\u017cZ\3\2\2\2\u017d\u017b\3\2\2\2\u017e\u0182"+
		"\t\b\2\2\u017f\u0181\t\t\2\2\u0180\u017f\3\2\2\2\u0181\u0184\3\2\2\2\u0182"+
		"\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\\\3\2\2\2\u0184\u0182\3\2\2\2"+
		"\u0185\u0186\7\61\2\2\u0186\u0187\7\61\2\2\u0187\u018b\3\2\2\2\u0188\u018a"+
		"\n\3\2\2\u0189\u0188\3\2\2\2\u018a\u018d\3\2\2\2\u018b\u0189\3\2\2\2\u018b"+
		"\u018c\3\2\2\2\u018c\u018f\3\2\2\2\u018d\u018b\3\2\2\2\u018e\u0190\7\17"+
		"\2\2\u018f\u018e\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u0191\3\2\2\2\u0191"+
		"\u0192\7\f\2\2\u0192\u0193\3\2\2\2\u0193\u0194\b/\2\2\u0194^\3\2\2\2\u0195"+
		"\u0197\t\n\2\2\u0196\u0195\3\2\2\2\u0197\u0198\3\2\2\2\u0198\u0196\3\2"+
		"\2\2\u0198\u0199\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u019b\b\60\2\2\u019b"+
		"`\3\2\2\2\u019c\u019d\13\2\2\2\u019db\3\2\2\2\20\2\u013b\u0142\u0150\u0157"+
		"\u0162\u0168\u016d\u0173\u017b\u0182\u018b\u018f\u0198\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}