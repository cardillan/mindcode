// Generated from /Users/francois/Projects/mindcode/src/main/java/info/teksol/mindcode/grammar/Mindcode.g4 by ANTLR 4.9.1
package info.teksol.mindcode.grammar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MindcodeLexer extends Lexer {
    public static final int
            ALLOCATE = 1, BREAK = 2, CASE = 3, CONTINUE = 4, DEF = 5, ELSE = 6, END = 7, FALSE = 8,
            FOR = 9, HEAP = 10, IF = 11, IN = 12, NULL = 13, STACK = 14, TRUE = 15, WHEN = 16, WHILE = 17,
            ASSIGN = 18, AT = 19, COLON = 20, COMMA = 21, DIV = 22, DOLLAR = 23, DOT = 24, EXP = 25,
            MINUS = 26, MOD = 27, MUL = 28, NOT = 29, PLUS = 30, QUESTION_MARK = 31, SEMICOLON = 32,
            DIV_ASSIGN = 33, EXP_ASSIGN = 34, MINUS_ASSIGN = 35, MUL_ASSIGN = 36, PLUS_ASSIGN = 37,
            LESS_THAN = 38, LESS_THAN_EQUAL = 39, NOT_EQUAL = 40, EQUAL = 41, STRICT_EQUAL = 42,
            GREATER_THAN_EQUAL = 43, GREATER_THAN = 44, AND = 45, OR = 46, SHIFT_LEFT = 47,
            SHIFT_RIGHT = 48, BITWISE_AND = 49, BITWISE_OR = 50, BITWISE_XOR = 51, LEFT_SBRACKET = 52,
            RIGHT_SBRACKET = 53, LEFT_RBRACKET = 54, RIGHT_RBRACKET = 55, LEFT_CBRACKET = 56,
            RIGHT_CBRACKET = 57, LITERAL = 58, FLOAT = 59, INT = 60, ID = 61, SL_COMMENT = 62,
            WS = 63;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2A\u017d\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
                    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
                    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
                    "\4>\t>\4?\t?\4@\t@\4A\tA\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5" +
                    "\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3" +
                    "\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3" +
                    "\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3" +
                    "\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3" +
                    "\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3" +
                    "\32\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\5\36\u00f9" +
                    "\n\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3#\3#\3$\3$\3$\3%\3%\3%" +
                    "\3&\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3*\3+\3+\3+\3+\3,\3,\3,\3-\3" +
                    "-\3.\3.\3.\3.\3.\5.\u012a\n.\3/\3/\3/\3/\5/\u0130\n/\3\60\3\60\3\60\3" +
                    "\61\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3" +
                    "\67\38\38\39\39\3:\3:\3;\3;\3;\3<\3<\3<\7<\u0150\n<\f<\16<\u0153\13<\3" +
                    "<\3<\3=\3=\3=\3=\3>\3>\7>\u015d\n>\f>\16>\u0160\13>\3?\3?\7?\u0164\n?" +
                    "\f?\16?\u0167\13?\3@\3@\3@\3@\7@\u016d\n@\f@\16@\u0170\13@\3@\5@\u0173" +
                    "\n@\3@\3@\3A\6A\u0178\nA\rA\16A\u0179\3A\3A\3\u0151\2B\3\3\5\4\7\5\t\6" +
                    "\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24" +
                    "\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K" +
                    "\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u\2w<y={>}?" +
                    "\177@\u0081A\3\2\7\4\2\f\f\17\17\3\2\62;\5\2C\\aac|\7\2//\62;C\\aac|\5" +
                    "\2\13\f\17\17\"\"\2\u0185\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2" +
                    "\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2" +
                    "\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3" +
                    "\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2" +
                    "\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67" +
                    "\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2" +
                    "\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2" +
                    "\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]" +
                    "\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2" +
                    "\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2w\3\2\2\2" +
                    "\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\3\u0083" +
                    "\3\2\2\2\5\u008c\3\2\2\2\7\u0092\3\2\2\2\t\u0097\3\2\2\2\13\u00a0\3\2" +
                    "\2\2\r\u00a4\3\2\2\2\17\u00a9\3\2\2\2\21\u00ad\3\2\2\2\23\u00b3\3\2\2" +
                    "\2\25\u00b7\3\2\2\2\27\u00bc\3\2\2\2\31\u00bf\3\2\2\2\33\u00c2\3\2\2\2" +
                    "\35\u00c7\3\2\2\2\37\u00cd\3\2\2\2!\u00d2\3\2\2\2#\u00d7\3\2\2\2%\u00dd" +
                    "\3\2\2\2\'\u00df\3\2\2\2)\u00e1\3\2\2\2+\u00e3\3\2\2\2-\u00e5\3\2\2\2" +
                    "/\u00e7\3\2\2\2\61\u00e9\3\2\2\2\63\u00eb\3\2\2\2\65\u00ee\3\2\2\2\67" +
                    "\u00f0\3\2\2\29\u00f2\3\2\2\2;\u00f8\3\2\2\2=\u00fa\3\2\2\2?\u00fc\3\2" +
                    "\2\2A\u00fe\3\2\2\2C\u0100\3\2\2\2E\u0103\3\2\2\2G\u0107\3\2\2\2I\u010a" +
                    "\3\2\2\2K\u010d\3\2\2\2M\u0110\3\2\2\2O\u0112\3\2\2\2Q\u0115\3\2\2\2S" +
                    "\u0118\3\2\2\2U\u011b\3\2\2\2W\u011f\3\2\2\2Y\u0122\3\2\2\2[\u0129\3\2" +
                    "\2\2]\u012f\3\2\2\2_\u0131\3\2\2\2a\u0134\3\2\2\2c\u0137\3\2\2\2e\u0139" +
                    "\3\2\2\2g\u013b\3\2\2\2i\u013d\3\2\2\2k\u013f\3\2\2\2m\u0141\3\2\2\2o" +
                    "\u0143\3\2\2\2q\u0145\3\2\2\2s\u0147\3\2\2\2u\u0149\3\2\2\2w\u014c\3\2" +
                    "\2\2y\u0156\3\2\2\2{\u015a\3\2\2\2}\u0161\3\2\2\2\177\u0168\3\2\2\2\u0081" +
                    "\u0177\3\2\2\2\u0083\u0084\7c\2\2\u0084\u0085\7n\2\2\u0085\u0086\7n\2" +
                    "\2\u0086\u0087\7q\2\2\u0087\u0088\7e\2\2\u0088\u0089\7c\2\2\u0089\u008a" +
                    "\7v\2\2\u008a\u008b\7g\2\2\u008b\4\3\2\2\2\u008c\u008d\7d\2\2\u008d\u008e" +
                    "\7t\2\2\u008e\u008f\7g\2\2\u008f\u0090\7c\2\2\u0090\u0091\7m\2\2\u0091" +
                    "\6\3\2\2\2\u0092\u0093\7e\2\2\u0093\u0094\7c\2\2\u0094\u0095\7u\2\2\u0095" +
                    "\u0096\7g\2\2\u0096\b\3\2\2\2\u0097\u0098\7e\2\2\u0098\u0099\7q\2\2\u0099" +
                    "\u009a\7p\2\2\u009a\u009b\7v\2\2\u009b\u009c\7k\2\2\u009c\u009d\7p\2\2" +
                    "\u009d\u009e\7w\2\2\u009e\u009f\7g\2\2\u009f\n\3\2\2\2\u00a0\u00a1\7f" +
                    "\2\2\u00a1\u00a2\7g\2\2\u00a2\u00a3\7h\2\2\u00a3\f\3\2\2\2\u00a4\u00a5" +
                    "\7g\2\2\u00a5\u00a6\7n\2\2\u00a6\u00a7\7u\2\2\u00a7\u00a8\7g\2\2\u00a8" +
                    "\16\3\2\2\2\u00a9\u00aa\7g\2\2\u00aa\u00ab\7p\2\2\u00ab\u00ac\7f\2\2\u00ac" +
                    "\20\3\2\2\2\u00ad\u00ae\7h\2\2\u00ae\u00af\7c\2\2\u00af\u00b0\7n\2\2\u00b0" +
                    "\u00b1\7u\2\2\u00b1\u00b2\7g\2\2\u00b2\22\3\2\2\2\u00b3\u00b4\7h\2\2\u00b4" +
                    "\u00b5\7q\2\2\u00b5\u00b6\7t\2\2\u00b6\24\3\2\2\2\u00b7\u00b8\7j\2\2\u00b8" +
                    "\u00b9\7g\2\2\u00b9\u00ba\7c\2\2\u00ba\u00bb\7r\2\2\u00bb\26\3\2\2\2\u00bc" +
                    "\u00bd\7k\2\2\u00bd\u00be\7h\2\2\u00be\30\3\2\2\2\u00bf\u00c0\7k\2\2\u00c0" +
                    "\u00c1\7p\2\2\u00c1\32\3\2\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7w\2\2\u00c4" +
                    "\u00c5\7n\2\2\u00c5\u00c6\7n\2\2\u00c6\34\3\2\2\2\u00c7\u00c8\7u\2\2\u00c8" +
                    "\u00c9\7v\2\2\u00c9\u00ca\7c\2\2\u00ca\u00cb\7e\2\2\u00cb\u00cc\7m\2\2" +
                    "\u00cc\36\3\2\2\2\u00cd\u00ce\7v\2\2\u00ce\u00cf\7t\2\2\u00cf\u00d0\7" +
                    "w\2\2\u00d0\u00d1\7g\2\2\u00d1 \3\2\2\2\u00d2\u00d3\7y\2\2\u00d3\u00d4" +
                    "\7j\2\2\u00d4\u00d5\7g\2\2\u00d5\u00d6\7p\2\2\u00d6\"\3\2\2\2\u00d7\u00d8" +
                    "\7y\2\2\u00d8\u00d9\7j\2\2\u00d9\u00da\7k\2\2\u00da\u00db\7n\2\2\u00db" +
                    "\u00dc\7g\2\2\u00dc$\3\2\2\2\u00dd\u00de\7?\2\2\u00de&\3\2\2\2\u00df\u00e0" +
                    "\7B\2\2\u00e0(\3\2\2\2\u00e1\u00e2\7<\2\2\u00e2*\3\2\2\2\u00e3\u00e4\7" +
                    ".\2\2\u00e4,\3\2\2\2\u00e5\u00e6\7\61\2\2\u00e6.\3\2\2\2\u00e7\u00e8\7" +
                    "&\2\2\u00e8\60\3\2\2\2\u00e9\u00ea\7\60\2\2\u00ea\62\3\2\2\2\u00eb\u00ec" +
                    "\7,\2\2\u00ec\u00ed\7,\2\2\u00ed\64\3\2\2\2\u00ee\u00ef\7/\2\2\u00ef\66" +
                    "\3\2\2\2\u00f0\u00f1\7\'\2\2\u00f18\3\2\2\2\u00f2\u00f3\7,\2\2\u00f3:" +
                    "\3\2\2\2\u00f4\u00f9\7#\2\2\u00f5\u00f6\7p\2\2\u00f6\u00f7\7q\2\2\u00f7" +
                    "\u00f9\7v\2\2\u00f8\u00f4\3\2\2\2\u00f8\u00f5\3\2\2\2\u00f9<\3\2\2\2\u00fa" +
                    "\u00fb\7-\2\2\u00fb>\3\2\2\2\u00fc\u00fd\7A\2\2\u00fd@\3\2\2\2\u00fe\u00ff" +
                    "\7=\2\2\u00ffB\3\2\2\2\u0100\u0101\7\61\2\2\u0101\u0102\7?\2\2\u0102D" +
                    "\3\2\2\2\u0103\u0104\7,\2\2\u0104\u0105\7,\2\2\u0105\u0106\7?\2\2\u0106" +
                    "F\3\2\2\2\u0107\u0108\7/\2\2\u0108\u0109\7?\2\2\u0109H\3\2\2\2\u010a\u010b" +
                    "\7,\2\2\u010b\u010c\7?\2\2\u010cJ\3\2\2\2\u010d\u010e\7-\2\2\u010e\u010f" +
                    "\7?\2\2\u010fL\3\2\2\2\u0110\u0111\7>\2\2\u0111N\3\2\2\2\u0112\u0113\7" +
                    ">\2\2\u0113\u0114\7?\2\2\u0114P\3\2\2\2\u0115\u0116\7#\2\2\u0116\u0117" +
                    "\7?\2\2\u0117R\3\2\2\2\u0118\u0119\7?\2\2\u0119\u011a\7?\2\2\u011aT\3" +
                    "\2\2\2\u011b\u011c\7?\2\2\u011c\u011d\7?\2\2\u011d\u011e\7?\2\2\u011e" +
                    "V\3\2\2\2\u011f\u0120\7@\2\2\u0120\u0121\7?\2\2\u0121X\3\2\2\2\u0122\u0123" +
                    "\7@\2\2\u0123Z\3\2\2\2\u0124\u0125\7(\2\2\u0125\u012a\7(\2\2\u0126\u0127" +
                    "\7c\2\2\u0127\u0128\7p\2\2\u0128\u012a\7f\2\2\u0129\u0124\3\2\2\2\u0129" +
                    "\u0126\3\2\2\2\u012a\\\3\2\2\2\u012b\u012c\7~\2\2\u012c\u0130\7~\2\2\u012d" +
                    "\u012e\7q\2\2\u012e\u0130\7t\2\2\u012f\u012b\3\2\2\2\u012f\u012d\3\2\2" +
                    "\2\u0130^\3\2\2\2\u0131\u0132\7>\2\2\u0132\u0133\7>\2\2\u0133`\3\2\2\2" +
                    "\u0134\u0135\7@\2\2\u0135\u0136\7@\2\2\u0136b\3\2\2\2\u0137\u0138\7(\2" +
                    "\2\u0138d\3\2\2\2\u0139\u013a\7~\2\2\u013af\3\2\2\2\u013b\u013c\7`\2\2" +
                    "\u013ch\3\2\2\2\u013d\u013e\7]\2\2\u013ej\3\2\2\2\u013f\u0140\7_\2\2\u0140" +
                    "l\3\2\2\2\u0141\u0142\7*\2\2\u0142n\3\2\2\2\u0143\u0144\7+\2\2\u0144p" +
                    "\3\2\2\2\u0145\u0146\7}\2\2\u0146r\3\2\2\2\u0147\u0148\7\177\2\2\u0148" +
                    "t\3\2\2\2\u0149\u014a\7^\2\2\u014a\u014b\7$\2\2\u014bv\3\2\2\2\u014c\u0151" +
                    "\7$\2\2\u014d\u0150\5u;\2\u014e\u0150\n\2\2\2\u014f\u014d\3\2\2\2\u014f" +
                    "\u014e\3\2\2\2\u0150\u0153\3\2\2\2\u0151\u0152\3\2\2\2\u0151\u014f\3\2" +
                    "\2\2\u0152\u0154\3\2\2\2\u0153\u0151\3\2\2\2\u0154\u0155\7$\2\2\u0155" +
                    "x\3\2\2\2\u0156\u0157\5{>\2\u0157\u0158\5\61\31\2\u0158\u0159\5{>\2\u0159" +
                    "z\3\2\2\2\u015a\u015e\t\3\2\2\u015b\u015d\t\3\2\2\u015c\u015b\3\2\2\2" +
                    "\u015d\u0160\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f|\3" +
                    "\2\2\2\u0160\u015e\3\2\2\2\u0161\u0165\t\4\2\2\u0162\u0164\t\5\2\2\u0163" +
                    "\u0162\3\2\2\2\u0164\u0167\3\2\2\2\u0165\u0163\3\2\2\2\u0165\u0166\3\2" +
                    "\2\2\u0166~\3\2\2\2\u0167\u0165\3\2\2\2\u0168\u0169\7\61\2\2\u0169\u016a" +
                    "\7\61\2\2\u016a\u016e\3\2\2\2\u016b\u016d\n\2\2\2\u016c\u016b\3\2\2\2" +
                    "\u016d\u0170\3\2\2\2\u016e\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016f\u0172" +
                    "\3\2\2\2\u0170\u016e\3\2\2\2\u0171\u0173\7\17\2\2\u0172\u0171\3\2\2\2" +
                    "\u0172\u0173\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0175\7\f\2\2\u0175\u0080" +
                    "\3\2\2\2\u0176\u0178\t\6\2\2\u0177\u0176\3\2\2\2\u0178\u0179\3\2\2\2\u0179" +
                    "\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u017b\3\2\2\2\u017b\u017c\bA" +
                    "\2\2\u017c\u0082\3\2\2\2\r\2\u00f8\u0129\u012f\u014f\u0151\u015e\u0165" +
                    "\u016e\u0172\u0179\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    static {
        RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION);
    }

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

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public MindcodeLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "END", "FALSE",
                "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN", "WHILE",
                "ASSIGN", "AT", "COLON", "COMMA", "DIV", "DOLLAR", "DOT", "EXP", "MINUS",
                "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN",
                "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN",
                "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL",
                "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND",
                "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET",
                "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "ESCAPED_QUOTE",
                "LITERAL", "FLOAT", "INT", "ID", "SL_COMMENT", "WS"
        };
    }

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'allocate'", "'break'", "'case'", "'continue'", "'def'", "'else'",
                "'end'", "'false'", "'for'", "'heap'", "'if'", "'in'", "'null'", "'stack'",
                "'true'", "'when'", "'while'", "'='", "'@'", "':'", "','", "'/'", "'$'",
                "'.'", "'**'", "'-'", "'%'", "'*'", null, "'+'", "'?'", "';'", "'/='",
                "'**='", "'-='", "'*='", "'+='", "'<'", "'<='", "'!='", "'=='", "'==='",
                "'>='", "'>'", null, null, "'<<'", "'>>'", "'&'", "'|'", "'^'", "'['",
                "']'", "'('", "')'", "'{'", "'}'"
        };
    }

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "ALLOCATE", "BREAK", "CASE", "CONTINUE", "DEF", "ELSE", "END",
                "FALSE", "FOR", "HEAP", "IF", "IN", "NULL", "STACK", "TRUE", "WHEN",
                "WHILE", "ASSIGN", "AT", "COLON", "COMMA", "DIV", "DOLLAR", "DOT", "EXP",
                "MINUS", "MOD", "MUL", "NOT", "PLUS", "QUESTION_MARK", "SEMICOLON", "DIV_ASSIGN",
                "EXP_ASSIGN", "MINUS_ASSIGN", "MUL_ASSIGN", "PLUS_ASSIGN", "LESS_THAN",
                "LESS_THAN_EQUAL", "NOT_EQUAL", "EQUAL", "STRICT_EQUAL", "GREATER_THAN_EQUAL",
                "GREATER_THAN", "AND", "OR", "SHIFT_LEFT", "SHIFT_RIGHT", "BITWISE_AND",
                "BITWISE_OR", "BITWISE_XOR", "LEFT_SBRACKET", "RIGHT_SBRACKET", "LEFT_RBRACKET",
                "RIGHT_RBRACKET", "LEFT_CBRACKET", "RIGHT_CBRACKET", "LITERAL", "FLOAT",
                "INT", "ID", "SL_COMMENT", "WS"
        };
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

    @Override
    public String getGrammarFileName() {
        return "Mindcode.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }
}