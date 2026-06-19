// Generated from SchemacodeParser.g4 by ANTLR 4.13.1
package info.teksol.schemacode.grammar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SchemacodeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AS=1, AT=2, BLOCK=3, CONNECTED=4, COMMAND=5, COLOR=6, DESCRIPTION=7, DIMENSIONS=8, 
		DISABLED=9, ENABLED=10, END=11, FACING=12, FILE=13, FILENAME=14, HORIZONTAL=15, 
		ITEM=16, LINKS=17, LIQUID=18, LOGIC=19, MINDCODE=20, MLOG=21, NAME=22, 
		PROCESSOR=23, RGBA=24, SCHEMATIC=25, TAG=26, TARGET=27, TEXT=28, TO=29, 
		UNIT=30, VERTICAL=31, VIRTUAL=32, ASSIGN=33, COLON=34, COMMA=35, DOT=36, 
		DOT2=37, DOT3=38, MINUS=39, MUL=40, PLUS=41, NORTH=42, SOUTH=43, EAST=44, 
		WEST=45, LEFTPAREN=46, RIGHTPAREN=47, TEXTBLOCK1=48, TEXTBLOCK2=49, TEXTLINE=50, 
		INT=51, SIGNEDINT=52, ID=53, BLOCKID=54, REF=55, PATTERN=56, VERSION=57, 
		COMMENT=58, SLCOMMENT=59, WS=60, ANY=61;
	public static final int
		RULE_definitions = 0, RULE_definition = 1, RULE_schematic = 2, RULE_schematicItem = 3, 
		RULE_attribute = 4, RULE_number = 5, RULE_versionNumber = 6, RULE_block = 7, 
		RULE_blockId = 8, RULE_labelList = 9, RULE_blockPosition = 10, RULE_position = 11, 
		RULE_coordinates = 12, RULE_relativeCoordinates = 13, RULE_coordinatesRelativeTo = 14, 
		RULE_direction = 15, RULE_configuration = 16, RULE_colorDef = 17, RULE_connectionList = 18, 
		RULE_connection = 19, RULE_processor = 20, RULE_processorLinks = 21, RULE_linkDef = 22, 
		RULE_program = 23, RULE_programSnippet = 24, RULE_textDef = 25, RULE_stringValue = 26, 
		RULE_stringLiteral = 27, RULE_simpleStringLiteral = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"definitions", "definition", "schematic", "schematicItem", "attribute", 
			"number", "versionNumber", "block", "blockId", "labelList", "blockPosition", 
			"position", "coordinates", "relativeCoordinates", "coordinatesRelativeTo", 
			"direction", "configuration", "colorDef", "connectionList", "connection", 
			"processor", "processorLinks", "linkDef", "program", "programSnippet", 
			"textDef", "stringValue", "stringLiteral", "simpleStringLiteral"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'block'", "'connected'", "'command'", "'color'", 
			"'description'", "'dimensions'", "'disabled'", "'enabled'", "'end'", 
			"'facing'", "'file'", "'filename'", "'horizontal'", "'item'", "'links'", 
			"'liquid'", "'logic'", "'mindcode'", "'mlog'", "'name'", "'processor'", 
			"'rgba'", "'schematic'", "'tag'", "'target'", "'text'", "'to'", "'unit'", 
			"'vertical'", "'virtual'", "'='", "':'", "','", "'.'", "'..'", "'...'", 
			"'-'", "'*'", "'+'", "'north'", "'south'", "'east'", "'west'", "'('", 
			"')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AS", "AT", "BLOCK", "CONNECTED", "COMMAND", "COLOR", "DESCRIPTION", 
			"DIMENSIONS", "DISABLED", "ENABLED", "END", "FACING", "FILE", "FILENAME", 
			"HORIZONTAL", "ITEM", "LINKS", "LIQUID", "LOGIC", "MINDCODE", "MLOG", 
			"NAME", "PROCESSOR", "RGBA", "SCHEMATIC", "TAG", "TARGET", "TEXT", "TO", 
			"UNIT", "VERTICAL", "VIRTUAL", "ASSIGN", "COLON", "COMMA", "DOT", "DOT2", 
			"DOT3", "MINUS", "MUL", "PLUS", "NORTH", "SOUTH", "EAST", "WEST", "LEFTPAREN", 
			"RIGHTPAREN", "TEXTBLOCK1", "TEXTBLOCK2", "TEXTLINE", "INT", "SIGNEDINT", 
			"ID", "BLOCKID", "REF", "PATTERN", "VERSION", "COMMENT", "SLCOMMENT", 
			"WS", "ANY"
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

	@Override
	public String getGrammarFileName() { return "SchemacodeParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SchemacodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DefinitionsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SchemacodeParser.EOF, 0); }
		public List<DefinitionContext> definition() {
			return getRuleContexts(DefinitionContext.class);
		}
		public DefinitionContext definition(int i) {
			return getRuleContext(DefinitionContext.class,i);
		}
		public DefinitionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definitions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterDefinitions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitDefinitions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitDefinitions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionsContext definitions() throws RecognitionException {
		DefinitionsContext _localctx = new DefinitionsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_definitions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(58);
				definition();
				}
				}
				setState(61); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCHEMATIC || _la==ID );
			setState(63);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DefinitionContext extends ParserRuleContext {
		public SchematicContext schematic() {
			return getRuleContext(SchematicContext.class,0);
		}
		public StringValueContext stringValue() {
			return getRuleContext(StringValueContext.class,0);
		}
		public DefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionContext definition() throws RecognitionException {
		DefinitionContext _localctx = new DefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_definition);
		try {
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				schematic();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				stringValue();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SchematicContext extends ParserRuleContext {
		public Token name;
		public SchematicItemContext items;
		public TerminalNode SCHEMATIC() { return getToken(SchemacodeParser.SCHEMATIC, 0); }
		public TerminalNode END() { return getToken(SchemacodeParser.END, 0); }
		public TerminalNode COLON() { return getToken(SchemacodeParser.COLON, 0); }
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public List<SchematicItemContext> schematicItem() {
			return getRuleContexts(SchematicItemContext.class);
		}
		public SchematicItemContext schematicItem(int i) {
			return getRuleContext(SchematicItemContext.class,i);
		}
		public SchematicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schematic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterSchematic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitSchematic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitSchematic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchematicContext schematic() throws RecognitionException {
		SchematicContext _localctx = new SchematicContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_schematic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(69);
				((SchematicContext)_localctx).name = match(ID);
				setState(70);
				match(COLON);
				}
			}

			setState(73);
			match(SCHEMATIC);
			setState(75); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(74);
				((SchematicContext)_localctx).items = schematicItem();
				}
				}
				setState(77); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 63050394991870336L) != 0) );
			setState(79);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SchematicItemContext extends ParserRuleContext {
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public SchematicItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schematicItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterSchematicItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitSchematicItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitSchematicItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchematicItemContext schematicItem() throws RecognitionException {
		SchematicItemContext _localctx = new SchematicItemContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_schematicItem);
		try {
			setState(83);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DESCRIPTION:
			case DIMENSIONS:
			case FILENAME:
			case MINDCODE:
			case MLOG:
			case NAME:
			case TAG:
			case TARGET:
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				attribute();
				}
				break;
			case ID:
			case BLOCKID:
			case REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
				block();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeContext extends ParserRuleContext {
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
	 
		public AttributeContext() { }
		public void copyFrom(AttributeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FilenameContext extends AttributeContext {
		public SimpleStringLiteralContext filename;
		public TerminalNode FILENAME() { return getToken(SchemacodeParser.FILENAME, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public SimpleStringLiteralContext simpleStringLiteral() {
			return getRuleContext(SimpleStringLiteralContext.class,0);
		}
		public FilenameContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterFilename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitFilename(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitFilename(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MindcodePrologueContext extends AttributeContext {
		public TextDefContext tag;
		public TerminalNode MINDCODE() { return getToken(SchemacodeParser.MINDCODE, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public MindcodePrologueContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterMindcodePrologue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitMindcodePrologue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitMindcodePrologue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends AttributeContext {
		public TextDefContext name;
		public TerminalNode NAME() { return getToken(SchemacodeParser.NAME, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public NameContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SchemaTagContext extends AttributeContext {
		public TextDefContext tag;
		public TerminalNode TAG() { return getToken(SchemacodeParser.TAG, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public SchemaTagContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterSchemaTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitSchemaTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitSchemaTag(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DescriptionContext extends AttributeContext {
		public TextDefContext description;
		public TerminalNode DESCRIPTION() { return getToken(SchemacodeParser.DESCRIPTION, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public DescriptionContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitDescription(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MlogPrologueContext extends AttributeContext {
		public TextDefContext tag;
		public TerminalNode MLOG() { return getToken(SchemacodeParser.MLOG, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public MlogPrologueContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterMlogPrologue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitMlogPrologue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitMlogPrologue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DimensionsContext extends AttributeContext {
		public TerminalNode DIMENSIONS() { return getToken(SchemacodeParser.DIMENSIONS, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public DimensionsContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitDimensions(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TargetContext extends AttributeContext {
		public VersionNumberContext version;
		public TerminalNode TARGET() { return getToken(SchemacodeParser.TARGET, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public VersionNumberContext versionNumber() {
			return getRuleContext(VersionNumberContext.class,0);
		}
		public TargetContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterTarget(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitTarget(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitTarget(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_attribute);
		try {
			setState(109);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				_localctx = new NameContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				match(NAME);
				setState(86);
				match(ASSIGN);
				setState(87);
				((NameContext)_localctx).name = textDef();
				}
				break;
			case DESCRIPTION:
				_localctx = new DescriptionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(88);
				match(DESCRIPTION);
				setState(89);
				match(ASSIGN);
				setState(90);
				((DescriptionContext)_localctx).description = textDef();
				}
				break;
			case DIMENSIONS:
				_localctx = new DimensionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(91);
				match(DIMENSIONS);
				setState(92);
				match(ASSIGN);
				setState(93);
				coordinates();
				}
				break;
			case TAG:
				_localctx = new SchemaTagContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(94);
				match(TAG);
				setState(95);
				match(ASSIGN);
				setState(96);
				((SchemaTagContext)_localctx).tag = textDef();
				}
				break;
			case FILENAME:
				_localctx = new FilenameContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(97);
				match(FILENAME);
				setState(98);
				match(ASSIGN);
				setState(99);
				((FilenameContext)_localctx).filename = simpleStringLiteral();
				}
				break;
			case TARGET:
				_localctx = new TargetContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(100);
				match(TARGET);
				setState(101);
				match(ASSIGN);
				setState(102);
				((TargetContext)_localctx).version = versionNumber();
				}
				break;
			case MINDCODE:
				_localctx = new MindcodePrologueContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(103);
				match(MINDCODE);
				setState(104);
				match(ASSIGN);
				setState(105);
				((MindcodePrologueContext)_localctx).tag = textDef();
				}
				break;
			case MLOG:
				_localctx = new MlogPrologueContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(106);
				match(MLOG);
				setState(107);
				match(ASSIGN);
				setState(108);
				((MlogPrologueContext)_localctx).tag = textDef();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NumberContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SchemacodeParser.INT, 0); }
		public TerminalNode SIGNEDINT() { return getToken(SchemacodeParser.SIGNEDINT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==SIGNEDINT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VersionNumberContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(SchemacodeParser.INT, 0); }
		public TerminalNode VERSION() { return getToken(SchemacodeParser.VERSION, 0); }
		public VersionNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_versionNumber; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterVersionNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitVersionNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitVersionNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VersionNumberContext versionNumber() throws RecognitionException {
		VersionNumberContext _localctx = new VersionNumberContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_versionNumber);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==VERSION) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public LabelListContext labels;
		public Token type;
		public BlockPositionContext pos;
		public DirectionContext dir;
		public ConfigurationContext cfg;
		public TerminalNode AT() { return getToken(SchemacodeParser.AT, 0); }
		public TerminalNode REF() { return getToken(SchemacodeParser.REF, 0); }
		public BlockPositionContext blockPosition() {
			return getRuleContext(BlockPositionContext.class,0);
		}
		public LabelListContext labelList() {
			return getRuleContext(LabelListContext.class,0);
		}
		public DirectionContext direction() {
			return getRuleContext(DirectionContext.class,0);
		}
		public ConfigurationContext configuration() {
			return getRuleContext(ConfigurationContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID || _la==BLOCKID) {
				{
				setState(115);
				((BlockContext)_localctx).labels = labelList();
				}
			}

			setState(118);
			((BlockContext)_localctx).type = match(REF);
			setState(119);
			match(AT);
			setState(120);
			((BlockContext)_localctx).pos = blockPosition();
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FACING) {
				{
				setState(121);
				((BlockContext)_localctx).dir = direction();
				}
			}

			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 5645862520L) != 0)) {
				{
				setState(124);
				((BlockContext)_localctx).cfg = configuration();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockIdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public TerminalNode BLOCKID() { return getToken(SchemacodeParser.BLOCKID, 0); }
		public BlockIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterBlockId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitBlockId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitBlockId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockIdContext blockId() throws RecognitionException {
		BlockIdContext _localctx = new BlockIdContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_blockId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_la = _input.LA(1);
			if ( !(_la==ID || _la==BLOCKID) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LabelListContext extends ParserRuleContext {
		public List<BlockIdContext> blockId() {
			return getRuleContexts(BlockIdContext.class);
		}
		public BlockIdContext blockId(int i) {
			return getRuleContext(BlockIdContext.class,i);
		}
		public TerminalNode COLON() { return getToken(SchemacodeParser.COLON, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SchemacodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SchemacodeParser.COMMA, i);
		}
		public LabelListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterLabelList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitLabelList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitLabelList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelListContext labelList() throws RecognitionException {
		LabelListContext _localctx = new LabelListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_labelList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			blockId();
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(130);
				match(COMMA);
				setState(131);
				blockId();
				}
				}
				setState(136);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(137);
			match(COLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockPositionContext extends ParserRuleContext {
		public BlockPositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockPosition; }
	 
		public BlockPositionContext() { }
		public void copyFrom(BlockPositionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SimplePositionContext extends BlockPositionContext {
		public PositionContext start;
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public SimplePositionContext(BlockPositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterSimplePosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitSimplePosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitSimplePosition(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InclusiveRangePositionContext extends BlockPositionContext {
		public PositionContext start;
		public CoordinatesContext end;
		public Token orientation;
		public TerminalNode DOT2() { return getToken(SchemacodeParser.DOT2, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode HORIZONTAL() { return getToken(SchemacodeParser.HORIZONTAL, 0); }
		public TerminalNode VERTICAL() { return getToken(SchemacodeParser.VERTICAL, 0); }
		public InclusiveRangePositionContext(BlockPositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterInclusiveRangePosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitInclusiveRangePosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitInclusiveRangePosition(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExclusiveRangePositionContext extends BlockPositionContext {
		public PositionContext start;
		public CoordinatesContext end;
		public Token orientation;
		public TerminalNode DOT3() { return getToken(SchemacodeParser.DOT3, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode HORIZONTAL() { return getToken(SchemacodeParser.HORIZONTAL, 0); }
		public TerminalNode VERTICAL() { return getToken(SchemacodeParser.VERTICAL, 0); }
		public ExclusiveRangePositionContext(BlockPositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterExclusiveRangePosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitExclusiveRangePosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitExclusiveRangePosition(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AreaPositionContext extends BlockPositionContext {
		public PositionContext start;
		public CoordinatesContext size;
		public Token orientation;
		public TerminalNode MUL() { return getToken(SchemacodeParser.MUL, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode HORIZONTAL() { return getToken(SchemacodeParser.HORIZONTAL, 0); }
		public TerminalNode VERTICAL() { return getToken(SchemacodeParser.VERTICAL, 0); }
		public AreaPositionContext(BlockPositionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterAreaPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitAreaPosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitAreaPosition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockPositionContext blockPosition() throws RecognitionException {
		BlockPositionContext _localctx = new BlockPositionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_blockPosition);
		int _la;
		try {
			setState(158);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new SimplePositionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(139);
				((SimplePositionContext)_localctx).start = position();
				}
				break;
			case 2:
				_localctx = new InclusiveRangePositionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
				((InclusiveRangePositionContext)_localctx).start = position();
				setState(141);
				match(DOT2);
				setState(142);
				((InclusiveRangePositionContext)_localctx).end = coordinates();
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(143);
					((InclusiveRangePositionContext)_localctx).orientation = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==HORIZONTAL || _la==VERTICAL) ) {
						((InclusiveRangePositionContext)_localctx).orientation = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				break;
			case 3:
				_localctx = new ExclusiveRangePositionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(146);
				((ExclusiveRangePositionContext)_localctx).start = position();
				setState(147);
				match(DOT3);
				setState(148);
				((ExclusiveRangePositionContext)_localctx).end = coordinates();
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(149);
					((ExclusiveRangePositionContext)_localctx).orientation = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==HORIZONTAL || _la==VERTICAL) ) {
						((ExclusiveRangePositionContext)_localctx).orientation = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				break;
			case 4:
				_localctx = new AreaPositionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(152);
				((AreaPositionContext)_localctx).start = position();
				setState(153);
				match(MUL);
				setState(154);
				((AreaPositionContext)_localctx).size = coordinates();
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(155);
					((AreaPositionContext)_localctx).orientation = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==HORIZONTAL || _la==VERTICAL) ) {
						((AreaPositionContext)_localctx).orientation = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PositionContext extends ParserRuleContext {
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public CoordinatesRelativeToContext coordinatesRelativeTo() {
			return getRuleContext(CoordinatesRelativeToContext.class,0);
		}
		public PositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_position; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitPosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitPosition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PositionContext position() throws RecognitionException {
		PositionContext _localctx = new PositionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_position);
		try {
			setState(163);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFTPAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(160);
				coordinates();
				}
				break;
			case MINUS:
			case PLUS:
				enterOuterAlt(_localctx, 2);
				{
				setState(161);
				relativeCoordinates();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(162);
				coordinatesRelativeTo();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CoordinatesContext extends ParserRuleContext {
		public NumberContext x;
		public NumberContext y;
		public TerminalNode LEFTPAREN() { return getToken(SchemacodeParser.LEFTPAREN, 0); }
		public TerminalNode COMMA() { return getToken(SchemacodeParser.COMMA, 0); }
		public TerminalNode RIGHTPAREN() { return getToken(SchemacodeParser.RIGHTPAREN, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public CoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesContext coordinates() throws RecognitionException {
		CoordinatesContext _localctx = new CoordinatesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_coordinates);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			match(LEFTPAREN);
			setState(166);
			((CoordinatesContext)_localctx).x = number();
			setState(167);
			match(COMMA);
			setState(168);
			((CoordinatesContext)_localctx).y = number();
			setState(169);
			match(RIGHTPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelativeCoordinatesContext extends ParserRuleContext {
		public Token op;
		public CoordinatesContext coord;
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(SchemacodeParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(SchemacodeParser.MINUS, 0); }
		public RelativeCoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relativeCoordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterRelativeCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitRelativeCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitRelativeCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelativeCoordinatesContext relativeCoordinates() throws RecognitionException {
		RelativeCoordinatesContext _localctx = new RelativeCoordinatesContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_relativeCoordinates);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			((RelativeCoordinatesContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==MINUS || _la==PLUS) ) {
				((RelativeCoordinatesContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(172);
			((RelativeCoordinatesContext)_localctx).coord = coordinates();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CoordinatesRelativeToContext extends ParserRuleContext {
		public Token label;
		public RelativeCoordinatesContext relCoord;
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public CoordinatesRelativeToContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinatesRelativeTo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterCoordinatesRelativeTo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitCoordinatesRelativeTo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitCoordinatesRelativeTo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesRelativeToContext coordinatesRelativeTo() throws RecognitionException {
		CoordinatesRelativeToContext _localctx = new CoordinatesRelativeToContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_coordinatesRelativeTo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			((CoordinatesRelativeToContext)_localctx).label = match(ID);
			setState(175);
			((CoordinatesRelativeToContext)_localctx).relCoord = relativeCoordinates();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DirectionContext extends ParserRuleContext {
		public Token dir;
		public TerminalNode FACING() { return getToken(SchemacodeParser.FACING, 0); }
		public TerminalNode NORTH() { return getToken(SchemacodeParser.NORTH, 0); }
		public TerminalNode SOUTH() { return getToken(SchemacodeParser.SOUTH, 0); }
		public TerminalNode EAST() { return getToken(SchemacodeParser.EAST, 0); }
		public TerminalNode WEST() { return getToken(SchemacodeParser.WEST, 0); }
		public DirectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterDirection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitDirection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitDirection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectionContext direction() throws RecognitionException {
		DirectionContext _localctx = new DirectionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			match(FACING);
			setState(178);
			((DirectionContext)_localctx).dir = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 65970697666560L) != 0)) ) {
				((DirectionContext)_localctx).dir = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConfigurationContext extends ParserRuleContext {
		public ConfigurationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_configuration; }
	 
		public ConfigurationContext() { }
		public void copyFrom(ConfigurationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VirtualContext extends ConfigurationContext {
		public TerminalNode VIRTUAL() { return getToken(SchemacodeParser.VIRTUAL, 0); }
		public VirtualContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterVirtual(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitVirtual(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitVirtual(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ItemContext extends ConfigurationContext {
		public TerminalNode ITEM() { return getToken(SchemacodeParser.ITEM, 0); }
		public TerminalNode REF() { return getToken(SchemacodeParser.REF, 0); }
		public ItemContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitItem(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnitContext extends ConfigurationContext {
		public TerminalNode UNIT() { return getToken(SchemacodeParser.UNIT, 0); }
		public TerminalNode REF() { return getToken(SchemacodeParser.REF, 0); }
		public UnitContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitUnit(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class UnitcommandContext extends ConfigurationContext {
		public TerminalNode COMMAND() { return getToken(SchemacodeParser.COMMAND, 0); }
		public TerminalNode REF() { return getToken(SchemacodeParser.REF, 0); }
		public UnitcommandContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterUnitcommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitUnitcommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitUnitcommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanContext extends ConfigurationContext {
		public Token status;
		public TerminalNode ENABLED() { return getToken(SchemacodeParser.ENABLED, 0); }
		public TerminalNode DISABLED() { return getToken(SchemacodeParser.DISABLED, 0); }
		public BooleanContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ColorContext extends ConfigurationContext {
		public TerminalNode COLOR() { return getToken(SchemacodeParser.COLOR, 0); }
		public ColorDefContext colorDef() {
			return getRuleContext(ColorDefContext.class,0);
		}
		public ColorContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitColor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiquidContext extends ConfigurationContext {
		public TerminalNode LIQUID() { return getToken(SchemacodeParser.LIQUID, 0); }
		public TerminalNode REF() { return getToken(SchemacodeParser.REF, 0); }
		public LiquidContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterLiquid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitLiquid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitLiquid(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BlocktypeContext extends ConfigurationContext {
		public TerminalNode BLOCK() { return getToken(SchemacodeParser.BLOCK, 0); }
		public TerminalNode REF() { return getToken(SchemacodeParser.REF, 0); }
		public BlocktypeContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterBlocktype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitBlocktype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitBlocktype(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextContext extends ConfigurationContext {
		public TextDefContext text;
		public TerminalNode TEXT() { return getToken(SchemacodeParser.TEXT, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public TextContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitText(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LogicContext extends ConfigurationContext {
		public ProcessorContext def;
		public ProcessorContext processor() {
			return getRuleContext(ProcessorContext.class,0);
		}
		public LogicContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterLogic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitLogic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitLogic(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConnectionsContext extends ConfigurationContext {
		public TerminalNode CONNECTED() { return getToken(SchemacodeParser.CONNECTED, 0); }
		public TerminalNode TO() { return getToken(SchemacodeParser.TO, 0); }
		public ConnectionListContext connectionList() {
			return getRuleContext(ConnectionListContext.class,0);
		}
		public ConnectionsContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterConnections(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitConnections(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitConnections(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConfigurationContext configuration() throws RecognitionException {
		ConfigurationContext _localctx = new ConfigurationContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_configuration);
		int _la;
		try {
			setState(200);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VIRTUAL:
				_localctx = new VirtualContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(180);
				match(VIRTUAL);
				}
				break;
			case COLOR:
				_localctx = new ColorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(181);
				match(COLOR);
				setState(182);
				colorDef();
				}
				break;
			case CONNECTED:
				_localctx = new ConnectionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(183);
				match(CONNECTED);
				setState(184);
				match(TO);
				setState(185);
				connectionList();
				}
				break;
			case BLOCK:
				_localctx = new BlocktypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(186);
				match(BLOCK);
				setState(187);
				match(REF);
				}
				break;
			case COMMAND:
				_localctx = new UnitcommandContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(188);
				match(COMMAND);
				setState(189);
				match(REF);
				}
				break;
			case ITEM:
				_localctx = new ItemContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(190);
				match(ITEM);
				setState(191);
				match(REF);
				}
				break;
			case LIQUID:
				_localctx = new LiquidContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(192);
				match(LIQUID);
				setState(193);
				match(REF);
				}
				break;
			case UNIT:
				_localctx = new UnitContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(194);
				match(UNIT);
				setState(195);
				match(REF);
				}
				break;
			case TEXT:
				_localctx = new TextContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(196);
				match(TEXT);
				setState(197);
				((TextContext)_localctx).text = textDef();
				}
				break;
			case DISABLED:
			case ENABLED:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(198);
				((BooleanContext)_localctx).status = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==DISABLED || _la==ENABLED) ) {
					((BooleanContext)_localctx).status = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case PROCESSOR:
				_localctx = new LogicContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(199);
				((LogicContext)_localctx).def = processor();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColorDefContext extends ParserRuleContext {
		public NumberContext red;
		public NumberContext green;
		public NumberContext blue;
		public NumberContext alpha;
		public TerminalNode RGBA() { return getToken(SchemacodeParser.RGBA, 0); }
		public TerminalNode LEFTPAREN() { return getToken(SchemacodeParser.LEFTPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SchemacodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SchemacodeParser.COMMA, i);
		}
		public TerminalNode RIGHTPAREN() { return getToken(SchemacodeParser.RIGHTPAREN, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public ColorDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colorDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterColorDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitColorDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitColorDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColorDefContext colorDef() throws RecognitionException {
		ColorDefContext _localctx = new ColorDefContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_colorDef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(RGBA);
			setState(203);
			match(LEFTPAREN);
			setState(204);
			((ColorDefContext)_localctx).red = number();
			setState(205);
			match(COMMA);
			setState(206);
			((ColorDefContext)_localctx).green = number();
			setState(207);
			match(COMMA);
			setState(208);
			((ColorDefContext)_localctx).blue = number();
			setState(209);
			match(COMMA);
			setState(210);
			((ColorDefContext)_localctx).alpha = number();
			setState(211);
			match(RIGHTPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConnectionListContext extends ParserRuleContext {
		public List<ConnectionContext> connection() {
			return getRuleContexts(ConnectionContext.class);
		}
		public ConnectionContext connection(int i) {
			return getRuleContext(ConnectionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SchemacodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SchemacodeParser.COMMA, i);
		}
		public ConnectionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connectionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterConnectionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitConnectionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitConnectionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConnectionListContext connectionList() throws RecognitionException {
		ConnectionListContext _localctx = new ConnectionListContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_connectionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			connection();
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(214);
				match(COMMA);
				setState(215);
				connection();
				}
				}
				setState(220);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConnectionContext extends ParserRuleContext {
		public ConnectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connection; }
	 
		public ConnectionContext() { }
		public void copyFrom(ConnectionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConnAbsContext extends ConnectionContext {
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public ConnAbsContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterConnAbs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitConnAbs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitConnAbs(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConnNameContext extends ConnectionContext {
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public ConnNameContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterConnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitConnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitConnName(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConnRelContext extends ConnectionContext {
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public ConnRelContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterConnRel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitConnRel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitConnRel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConnectionContext connection() throws RecognitionException {
		ConnectionContext _localctx = new ConnectionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_connection);
		try {
			setState(224);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFTPAREN:
				_localctx = new ConnAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(221);
				coordinates();
				}
				break;
			case MINUS:
			case PLUS:
				_localctx = new ConnRelContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(222);
				relativeCoordinates();
				}
				break;
			case ID:
				_localctx = new ConnNameContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(223);
				match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProcessorContext extends ParserRuleContext {
		public ProcessorLinksContext links;
		public ProgramContext mindcode;
		public ProgramContext mlog;
		public TerminalNode PROCESSOR() { return getToken(SchemacodeParser.PROCESSOR, 0); }
		public TerminalNode END() { return getToken(SchemacodeParser.END, 0); }
		public TerminalNode MINDCODE() { return getToken(SchemacodeParser.MINDCODE, 0); }
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TerminalNode MLOG() { return getToken(SchemacodeParser.MLOG, 0); }
		public ProcessorLinksContext processorLinks() {
			return getRuleContext(ProcessorLinksContext.class,0);
		}
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public ProcessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_processor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterProcessor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitProcessor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitProcessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessorContext processor() throws RecognitionException {
		ProcessorContext _localctx = new ProcessorContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_processor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			match(PROCESSOR);
			setState(228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LINKS) {
				{
				setState(227);
				((ProcessorContext)_localctx).links = processorLinks();
				}
			}

			setState(236);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MINDCODE:
				{
				setState(230);
				match(MINDCODE);
				setState(231);
				match(ASSIGN);
				setState(232);
				((ProcessorContext)_localctx).mindcode = program();
				}
				break;
			case MLOG:
				{
				setState(233);
				match(MLOG);
				setState(234);
				match(ASSIGN);
				setState(235);
				((ProcessorContext)_localctx).mlog = program();
				}
				break;
			case END:
				break;
			default:
				break;
			}
			setState(238);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProcessorLinksContext extends ParserRuleContext {
		public TerminalNode LINKS() { return getToken(SchemacodeParser.LINKS, 0); }
		public TerminalNode END() { return getToken(SchemacodeParser.END, 0); }
		public List<LinkDefContext> linkDef() {
			return getRuleContexts(LinkDefContext.class);
		}
		public LinkDefContext linkDef(int i) {
			return getRuleContext(LinkDefContext.class,i);
		}
		public ProcessorLinksContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_processorLinks; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterProcessorLinks(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitProcessorLinks(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitProcessorLinks(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessorLinksContext processorLinks() throws RecognitionException {
		ProcessorLinksContext _localctx = new ProcessorLinksContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_processorLinks);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			match(LINKS);
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 81139010327543808L) != 0)) {
				{
				{
				setState(241);
				linkDef();
				}
				}
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(247);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LinkDefContext extends ParserRuleContext {
		public LinkDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_linkDef; }
	 
		public LinkDefContext() { }
		public void copyFrom(LinkDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LinkPatternContext extends LinkDefContext {
		public Token linkPattern;
		public TerminalNode MUL() { return getToken(SchemacodeParser.MUL, 0); }
		public TerminalNode PATTERN() { return getToken(SchemacodeParser.PATTERN, 0); }
		public LinkPatternContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterLinkPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitLinkPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitLinkPattern(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LinkPosContext extends LinkDefContext {
		public ConnectionContext linkPos;
		public Token alias;
		public Token virtual;
		public ConnectionContext connection() {
			return getRuleContext(ConnectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(SchemacodeParser.AS, 0); }
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public TerminalNode VIRTUAL() { return getToken(SchemacodeParser.VIRTUAL, 0); }
		public LinkPosContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterLinkPos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitLinkPos(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitLinkPos(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LinkDefContext linkDef() throws RecognitionException {
		LinkDefContext _localctx = new LinkDefContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_linkDef);
		int _la;
		try {
			setState(258);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MUL:
			case PATTERN:
				_localctx = new LinkPatternContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(249);
				((LinkPatternContext)_localctx).linkPattern = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==MUL || _la==PATTERN) ) {
					((LinkPatternContext)_localctx).linkPattern = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case MINUS:
			case PLUS:
			case LEFTPAREN:
			case ID:
				_localctx = new LinkPosContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(250);
				((LinkPosContext)_localctx).linkPos = connection();
				setState(256);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(251);
					match(AS);
					setState(252);
					((LinkPosContext)_localctx).alias = match(ID);
					setState(254);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==VIRTUAL) {
						{
						setState(253);
						((LinkPosContext)_localctx).virtual = match(VIRTUAL);
						}
					}

					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public List<ProgramSnippetContext> programSnippet() {
			return getRuleContexts(ProgramSnippetContext.class);
		}
		public ProgramSnippetContext programSnippet(int i) {
			return getRuleContext(ProgramSnippetContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(SchemacodeParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(SchemacodeParser.PLUS, i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			programSnippet();
			setState(265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS) {
				{
				{
				setState(261);
				match(PLUS);
				setState(262);
				programSnippet();
				}
				}
				setState(267);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramSnippetContext extends ParserRuleContext {
		public ProgramSnippetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_programSnippet; }
	 
		public ProgramSnippetContext() { }
		public void copyFrom(ProgramSnippetContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ProgramStringContext extends ProgramSnippetContext {
		public TextDefContext text;
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public ProgramStringContext(ProgramSnippetContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterProgramString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitProgramString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitProgramString(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ProgramFileContext extends ProgramSnippetContext {
		public TextDefContext file;
		public TerminalNode FILE() { return getToken(SchemacodeParser.FILE, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public ProgramFileContext(ProgramSnippetContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterProgramFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitProgramFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitProgramFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramSnippetContext programSnippet() throws RecognitionException {
		ProgramSnippetContext _localctx = new ProgramSnippetContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_programSnippet);
		try {
			setState(271);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTBLOCK1:
			case TEXTBLOCK2:
			case TEXTLINE:
			case ID:
				_localctx = new ProgramStringContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(268);
				((ProgramStringContext)_localctx).text = textDef();
				}
				break;
			case FILE:
				_localctx = new ProgramFileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(269);
				match(FILE);
				setState(270);
				((ProgramFileContext)_localctx).file = textDef();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TextDefContext extends ParserRuleContext {
		public TextDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textDef; }
	 
		public TextDefContext() { }
		public void copyFrom(TextDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextIdContext extends TextDefContext {
		public Token name;
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public TextIdContext(TextDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterTextId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitTextId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitTextId(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextLiteralContext extends TextDefContext {
		public StringLiteralContext reference;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public TextLiteralContext(TextDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterTextLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitTextLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextDefContext textDef() throws RecognitionException {
		TextDefContext _localctx = new TextDefContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_textDef);
		try {
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTBLOCK1:
			case TEXTBLOCK2:
			case TEXTLINE:
				_localctx = new TextLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(273);
				((TextLiteralContext)_localctx).reference = stringLiteral();
				}
				break;
			case ID:
				_localctx = new TextIdContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(274);
				((TextIdContext)_localctx).name = match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringValueContext extends ParserRuleContext {
		public Token name;
		public StringLiteralContext string;
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public StringValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterStringValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitStringValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitStringValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringValueContext stringValue() throws RecognitionException {
		StringValueContext _localctx = new StringValueContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_stringValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			((StringValueContext)_localctx).name = match(ID);
			setState(278);
			match(ASSIGN);
			setState(279);
			((StringValueContext)_localctx).string = stringLiteral();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringLiteralContext extends ParserRuleContext {
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
	 
		public StringLiteralContext() { }
		public void copyFrom(StringLiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextLineContext extends StringLiteralContext {
		public TerminalNode TEXTLINE() { return getToken(SchemacodeParser.TEXTLINE, 0); }
		public TextLineContext(StringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterTextLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitTextLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitTextLine(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextBlockContext extends StringLiteralContext {
		public TerminalNode TEXTBLOCK1() { return getToken(SchemacodeParser.TEXTBLOCK1, 0); }
		public TerminalNode TEXTBLOCK2() { return getToken(SchemacodeParser.TEXTBLOCK2, 0); }
		public TextBlockContext(StringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterTextBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitTextBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitTextBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_stringLiteral);
		int _la;
		try {
			setState(283);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTLINE:
				_localctx = new TextLineContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(281);
				match(TEXTLINE);
				}
				break;
			case TEXTBLOCK1:
			case TEXTBLOCK2:
				_localctx = new TextBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(282);
				_la = _input.LA(1);
				if ( !(_la==TEXTBLOCK1 || _la==TEXTBLOCK2) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SimpleStringLiteralContext extends ParserRuleContext {
		public SimpleStringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleStringLiteral; }
	 
		public SimpleStringLiteralContext() { }
		public void copyFrom(SimpleStringLiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SimpleTextLineContext extends SimpleStringLiteralContext {
		public TerminalNode TEXTLINE() { return getToken(SchemacodeParser.TEXTLINE, 0); }
		public SimpleTextLineContext(SimpleStringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterSimpleTextLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitSimpleTextLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitSimpleTextLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleStringLiteralContext simpleStringLiteral() throws RecognitionException {
		SimpleStringLiteralContext _localctx = new SimpleStringLiteralContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_simpleStringLiteral);
		try {
			_localctx = new SimpleTextLineContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(TEXTLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001=\u0120\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0001\u0000\u0004\u0000<\b\u0000\u000b\u0000"+
		"\f\u0000=\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001"+
		"D\b\u0001\u0001\u0002\u0001\u0002\u0003\u0002H\b\u0002\u0001\u0002\u0001"+
		"\u0002\u0004\u0002L\b\u0002\u000b\u0002\f\u0002M\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0003\u0003T\b\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004n\b\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0003\u0007"+
		"u\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007"+
		"{\b\u0007\u0001\u0007\u0003\u0007~\b\u0007\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\t\u0005\t\u0085\b\t\n\t\f\t\u0088\t\t\u0001\t\u0001\t\u0001\n"+
		"\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u0091\b\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0003\n\u0097\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u009d"+
		"\b\n\u0003\n\u009f\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u00a4\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r"+
		"\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00c9\b\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0005\u0012\u00d9\b\u0012\n\u0012\f\u0012\u00dc\t\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00e1\b\u0013\u0001\u0014"+
		"\u0001\u0014\u0003\u0014\u00e5\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u00ed\b\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0001\u0015\u0005\u0015\u00f3\b\u0015\n\u0015"+
		"\f\u0015\u00f6\t\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00ff\b\u0016\u0003\u0016"+
		"\u0101\b\u0016\u0003\u0016\u0103\b\u0016\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0005\u0017\u0108\b\u0017\n\u0017\f\u0017\u010b\t\u0017\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u0110\b\u0018\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u0114\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001b\u0001\u001b\u0003\u001b\u011c\b\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0000\u0000\u001d\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468\u0000\t\u0001"+
		"\u000034\u0002\u00003399\u0001\u000056\u0002\u0000\u000f\u000f\u001f\u001f"+
		"\u0002\u0000\'\'))\u0001\u0000*-\u0001\u0000\t\n\u0002\u0000((88\u0001"+
		"\u000001\u0132\u0000;\u0001\u0000\u0000\u0000\u0002C\u0001\u0000\u0000"+
		"\u0000\u0004G\u0001\u0000\u0000\u0000\u0006S\u0001\u0000\u0000\u0000\b"+
		"m\u0001\u0000\u0000\u0000\no\u0001\u0000\u0000\u0000\fq\u0001\u0000\u0000"+
		"\u0000\u000et\u0001\u0000\u0000\u0000\u0010\u007f\u0001\u0000\u0000\u0000"+
		"\u0012\u0081\u0001\u0000\u0000\u0000\u0014\u009e\u0001\u0000\u0000\u0000"+
		"\u0016\u00a3\u0001\u0000\u0000\u0000\u0018\u00a5\u0001\u0000\u0000\u0000"+
		"\u001a\u00ab\u0001\u0000\u0000\u0000\u001c\u00ae\u0001\u0000\u0000\u0000"+
		"\u001e\u00b1\u0001\u0000\u0000\u0000 \u00c8\u0001\u0000\u0000\u0000\""+
		"\u00ca\u0001\u0000\u0000\u0000$\u00d5\u0001\u0000\u0000\u0000&\u00e0\u0001"+
		"\u0000\u0000\u0000(\u00e2\u0001\u0000\u0000\u0000*\u00f0\u0001\u0000\u0000"+
		"\u0000,\u0102\u0001\u0000\u0000\u0000.\u0104\u0001\u0000\u0000\u00000"+
		"\u010f\u0001\u0000\u0000\u00002\u0113\u0001\u0000\u0000\u00004\u0115\u0001"+
		"\u0000\u0000\u00006\u011b\u0001\u0000\u0000\u00008\u011d\u0001\u0000\u0000"+
		"\u0000:<\u0003\u0002\u0001\u0000;:\u0001\u0000\u0000\u0000<=\u0001\u0000"+
		"\u0000\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>?\u0001"+
		"\u0000\u0000\u0000?@\u0005\u0000\u0000\u0001@\u0001\u0001\u0000\u0000"+
		"\u0000AD\u0003\u0004\u0002\u0000BD\u00034\u001a\u0000CA\u0001\u0000\u0000"+
		"\u0000CB\u0001\u0000\u0000\u0000D\u0003\u0001\u0000\u0000\u0000EF\u0005"+
		"5\u0000\u0000FH\u0005\"\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001"+
		"\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IK\u0005\u0019\u0000\u0000"+
		"JL\u0003\u0006\u0003\u0000KJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000"+
		"\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NO\u0001\u0000"+
		"\u0000\u0000OP\u0005\u000b\u0000\u0000P\u0005\u0001\u0000\u0000\u0000"+
		"QT\u0003\b\u0004\u0000RT\u0003\u000e\u0007\u0000SQ\u0001\u0000\u0000\u0000"+
		"SR\u0001\u0000\u0000\u0000T\u0007\u0001\u0000\u0000\u0000UV\u0005\u0016"+
		"\u0000\u0000VW\u0005!\u0000\u0000Wn\u00032\u0019\u0000XY\u0005\u0007\u0000"+
		"\u0000YZ\u0005!\u0000\u0000Zn\u00032\u0019\u0000[\\\u0005\b\u0000\u0000"+
		"\\]\u0005!\u0000\u0000]n\u0003\u0018\f\u0000^_\u0005\u001a\u0000\u0000"+
		"_`\u0005!\u0000\u0000`n\u00032\u0019\u0000ab\u0005\u000e\u0000\u0000b"+
		"c\u0005!\u0000\u0000cn\u00038\u001c\u0000de\u0005\u001b\u0000\u0000ef"+
		"\u0005!\u0000\u0000fn\u0003\f\u0006\u0000gh\u0005\u0014\u0000\u0000hi"+
		"\u0005!\u0000\u0000in\u00032\u0019\u0000jk\u0005\u0015\u0000\u0000kl\u0005"+
		"!\u0000\u0000ln\u00032\u0019\u0000mU\u0001\u0000\u0000\u0000mX\u0001\u0000"+
		"\u0000\u0000m[\u0001\u0000\u0000\u0000m^\u0001\u0000\u0000\u0000ma\u0001"+
		"\u0000\u0000\u0000md\u0001\u0000\u0000\u0000mg\u0001\u0000\u0000\u0000"+
		"mj\u0001\u0000\u0000\u0000n\t\u0001\u0000\u0000\u0000op\u0007\u0000\u0000"+
		"\u0000p\u000b\u0001\u0000\u0000\u0000qr\u0007\u0001\u0000\u0000r\r\u0001"+
		"\u0000\u0000\u0000su\u0003\u0012\t\u0000ts\u0001\u0000\u0000\u0000tu\u0001"+
		"\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vw\u00057\u0000\u0000wx\u0005"+
		"\u0002\u0000\u0000xz\u0003\u0014\n\u0000y{\u0003\u001e\u000f\u0000zy\u0001"+
		"\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{}\u0001\u0000\u0000\u0000"+
		"|~\u0003 \u0010\u0000}|\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000"+
		"~\u000f\u0001\u0000\u0000\u0000\u007f\u0080\u0007\u0002\u0000\u0000\u0080"+
		"\u0011\u0001\u0000\u0000\u0000\u0081\u0086\u0003\u0010\b\u0000\u0082\u0083"+
		"\u0005#\u0000\u0000\u0083\u0085\u0003\u0010\b\u0000\u0084\u0082\u0001"+
		"\u0000\u0000\u0000\u0085\u0088\u0001\u0000\u0000\u0000\u0086\u0084\u0001"+
		"\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0089\u0001"+
		"\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0089\u008a\u0005"+
		"\"\u0000\u0000\u008a\u0013\u0001\u0000\u0000\u0000\u008b\u009f\u0003\u0016"+
		"\u000b\u0000\u008c\u008d\u0003\u0016\u000b\u0000\u008d\u008e\u0005%\u0000"+
		"\u0000\u008e\u0090\u0003\u0018\f\u0000\u008f\u0091\u0007\u0003\u0000\u0000"+
		"\u0090\u008f\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000"+
		"\u0091\u009f\u0001\u0000\u0000\u0000\u0092\u0093\u0003\u0016\u000b\u0000"+
		"\u0093\u0094\u0005&\u0000\u0000\u0094\u0096\u0003\u0018\f\u0000\u0095"+
		"\u0097\u0007\u0003\u0000\u0000\u0096\u0095\u0001\u0000\u0000\u0000\u0096"+
		"\u0097\u0001\u0000\u0000\u0000\u0097\u009f\u0001\u0000\u0000\u0000\u0098"+
		"\u0099\u0003\u0016\u000b\u0000\u0099\u009a\u0005(\u0000\u0000\u009a\u009c"+
		"\u0003\u0018\f\u0000\u009b\u009d\u0007\u0003\u0000\u0000\u009c\u009b\u0001"+
		"\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u009f\u0001"+
		"\u0000\u0000\u0000\u009e\u008b\u0001\u0000\u0000\u0000\u009e\u008c\u0001"+
		"\u0000\u0000\u0000\u009e\u0092\u0001\u0000\u0000\u0000\u009e\u0098\u0001"+
		"\u0000\u0000\u0000\u009f\u0015\u0001\u0000\u0000\u0000\u00a0\u00a4\u0003"+
		"\u0018\f\u0000\u00a1\u00a4\u0003\u001a\r\u0000\u00a2\u00a4\u0003\u001c"+
		"\u000e\u0000\u00a3\u00a0\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000"+
		"\u0000\u0000\u00a3\u00a2\u0001\u0000\u0000\u0000\u00a4\u0017\u0001\u0000"+
		"\u0000\u0000\u00a5\u00a6\u0005.\u0000\u0000\u00a6\u00a7\u0003\n\u0005"+
		"\u0000\u00a7\u00a8\u0005#\u0000\u0000\u00a8\u00a9\u0003\n\u0005\u0000"+
		"\u00a9\u00aa\u0005/\u0000\u0000\u00aa\u0019\u0001\u0000\u0000\u0000\u00ab"+
		"\u00ac\u0007\u0004\u0000\u0000\u00ac\u00ad\u0003\u0018\f\u0000\u00ad\u001b"+
		"\u0001\u0000\u0000\u0000\u00ae\u00af\u00055\u0000\u0000\u00af\u00b0\u0003"+
		"\u001a\r\u0000\u00b0\u001d\u0001\u0000\u0000\u0000\u00b1\u00b2\u0005\f"+
		"\u0000\u0000\u00b2\u00b3\u0007\u0005\u0000\u0000\u00b3\u001f\u0001\u0000"+
		"\u0000\u0000\u00b4\u00c9\u0005 \u0000\u0000\u00b5\u00b6\u0005\u0006\u0000"+
		"\u0000\u00b6\u00c9\u0003\"\u0011\u0000\u00b7\u00b8\u0005\u0004\u0000\u0000"+
		"\u00b8\u00b9\u0005\u001d\u0000\u0000\u00b9\u00c9\u0003$\u0012\u0000\u00ba"+
		"\u00bb\u0005\u0003\u0000\u0000\u00bb\u00c9\u00057\u0000\u0000\u00bc\u00bd"+
		"\u0005\u0005\u0000\u0000\u00bd\u00c9\u00057\u0000\u0000\u00be\u00bf\u0005"+
		"\u0010\u0000\u0000\u00bf\u00c9\u00057\u0000\u0000\u00c0\u00c1\u0005\u0012"+
		"\u0000\u0000\u00c1\u00c9\u00057\u0000\u0000\u00c2\u00c3\u0005\u001e\u0000"+
		"\u0000\u00c3\u00c9\u00057\u0000\u0000\u00c4\u00c5\u0005\u001c\u0000\u0000"+
		"\u00c5\u00c9\u00032\u0019\u0000\u00c6\u00c9\u0007\u0006\u0000\u0000\u00c7"+
		"\u00c9\u0003(\u0014\u0000\u00c8\u00b4\u0001\u0000\u0000\u0000\u00c8\u00b5"+
		"\u0001\u0000\u0000\u0000\u00c8\u00b7\u0001\u0000\u0000\u0000\u00c8\u00ba"+
		"\u0001\u0000\u0000\u0000\u00c8\u00bc\u0001\u0000\u0000\u0000\u00c8\u00be"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c0\u0001\u0000\u0000\u0000\u00c8\u00c2"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c4\u0001\u0000\u0000\u0000\u00c8\u00c6"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c9!\u0001"+
		"\u0000\u0000\u0000\u00ca\u00cb\u0005\u0018\u0000\u0000\u00cb\u00cc\u0005"+
		".\u0000\u0000\u00cc\u00cd\u0003\n\u0005\u0000\u00cd\u00ce\u0005#\u0000"+
		"\u0000\u00ce\u00cf\u0003\n\u0005\u0000\u00cf\u00d0\u0005#\u0000\u0000"+
		"\u00d0\u00d1\u0003\n\u0005\u0000\u00d1\u00d2\u0005#\u0000\u0000\u00d2"+
		"\u00d3\u0003\n\u0005\u0000\u00d3\u00d4\u0005/\u0000\u0000\u00d4#\u0001"+
		"\u0000\u0000\u0000\u00d5\u00da\u0003&\u0013\u0000\u00d6\u00d7\u0005#\u0000"+
		"\u0000\u00d7\u00d9\u0003&\u0013\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000"+
		"\u00d9\u00dc\u0001\u0000\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000"+
		"\u00da\u00db\u0001\u0000\u0000\u0000\u00db%\u0001\u0000\u0000\u0000\u00dc"+
		"\u00da\u0001\u0000\u0000\u0000\u00dd\u00e1\u0003\u0018\f\u0000\u00de\u00e1"+
		"\u0003\u001a\r\u0000\u00df\u00e1\u00055\u0000\u0000\u00e0\u00dd\u0001"+
		"\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00df\u0001"+
		"\u0000\u0000\u0000\u00e1\'\u0001\u0000\u0000\u0000\u00e2\u00e4\u0005\u0017"+
		"\u0000\u0000\u00e3\u00e5\u0003*\u0015\u0000\u00e4\u00e3\u0001\u0000\u0000"+
		"\u0000\u00e4\u00e5\u0001\u0000\u0000\u0000\u00e5\u00ec\u0001\u0000\u0000"+
		"\u0000\u00e6\u00e7\u0005\u0014\u0000\u0000\u00e7\u00e8\u0005!\u0000\u0000"+
		"\u00e8\u00ed\u0003.\u0017\u0000\u00e9\u00ea\u0005\u0015\u0000\u0000\u00ea"+
		"\u00eb\u0005!\u0000\u0000\u00eb\u00ed\u0003.\u0017\u0000\u00ec\u00e6\u0001"+
		"\u0000\u0000\u0000\u00ec\u00e9\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001"+
		"\u0000\u0000\u0000\u00ed\u00ee\u0001\u0000\u0000\u0000\u00ee\u00ef\u0005"+
		"\u000b\u0000\u0000\u00ef)\u0001\u0000\u0000\u0000\u00f0\u00f4\u0005\u0011"+
		"\u0000\u0000\u00f1\u00f3\u0003,\u0016\u0000\u00f2\u00f1\u0001\u0000\u0000"+
		"\u0000\u00f3\u00f6\u0001\u0000\u0000\u0000\u00f4\u00f2\u0001\u0000\u0000"+
		"\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f7\u0001\u0000\u0000"+
		"\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f7\u00f8\u0005\u000b\u0000"+
		"\u0000\u00f8+\u0001\u0000\u0000\u0000\u00f9\u0103\u0007\u0007\u0000\u0000"+
		"\u00fa\u0100\u0003&\u0013\u0000\u00fb\u00fc\u0005\u0001\u0000\u0000\u00fc"+
		"\u00fe\u00055\u0000\u0000\u00fd\u00ff\u0005 \u0000\u0000\u00fe\u00fd\u0001"+
		"\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff\u0101\u0001"+
		"\u0000\u0000\u0000\u0100\u00fb\u0001\u0000\u0000\u0000\u0100\u0101\u0001"+
		"\u0000\u0000\u0000\u0101\u0103\u0001\u0000\u0000\u0000\u0102\u00f9\u0001"+
		"\u0000\u0000\u0000\u0102\u00fa\u0001\u0000\u0000\u0000\u0103-\u0001\u0000"+
		"\u0000\u0000\u0104\u0109\u00030\u0018\u0000\u0105\u0106\u0005)\u0000\u0000"+
		"\u0106\u0108\u00030\u0018\u0000\u0107\u0105\u0001\u0000\u0000\u0000\u0108"+
		"\u010b\u0001\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u0109"+
		"\u010a\u0001\u0000\u0000\u0000\u010a/\u0001\u0000\u0000\u0000\u010b\u0109"+
		"\u0001\u0000\u0000\u0000\u010c\u0110\u00032\u0019\u0000\u010d\u010e\u0005"+
		"\r\u0000\u0000\u010e\u0110\u00032\u0019\u0000\u010f\u010c\u0001\u0000"+
		"\u0000\u0000\u010f\u010d\u0001\u0000\u0000\u0000\u01101\u0001\u0000\u0000"+
		"\u0000\u0111\u0114\u00036\u001b\u0000\u0112\u0114\u00055\u0000\u0000\u0113"+
		"\u0111\u0001\u0000\u0000\u0000\u0113\u0112\u0001\u0000\u0000\u0000\u0114"+
		"3\u0001\u0000\u0000\u0000\u0115\u0116\u00055\u0000\u0000\u0116\u0117\u0005"+
		"!\u0000\u0000\u0117\u0118\u00036\u001b\u0000\u01185\u0001\u0000\u0000"+
		"\u0000\u0119\u011c\u00052\u0000\u0000\u011a\u011c\u0007\b\u0000\u0000"+
		"\u011b\u0119\u0001\u0000\u0000\u0000\u011b\u011a\u0001\u0000\u0000\u0000"+
		"\u011c7\u0001\u0000\u0000\u0000\u011d\u011e\u00052\u0000\u0000\u011e9"+
		"\u0001\u0000\u0000\u0000\u001c=CGMSmtz}\u0086\u0090\u0096\u009c\u009e"+
		"\u00a3\u00c8\u00da\u00e0\u00e4\u00ec\u00f4\u00fe\u0100\u0102\u0109\u010f"+
		"\u0113\u011b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
