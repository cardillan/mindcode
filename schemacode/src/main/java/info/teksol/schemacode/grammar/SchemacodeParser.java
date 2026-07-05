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
		DISABLED=9, ENABLED=10, END=11, FACING=12, FILE=13, FILENAME=14, FILL=15, 
		GLOBAL=16, HORIZONTAL=17, ITEM=18, LINKS=19, LIQUID=20, LOCAL=21, LOGIC=22, 
		MINDCODE=23, MLOG=24, NAME=25, PARAM=26, PARENT=27, PROCESSOR=28, RANDOM=29, 
		REPLACE=30, REGION=31, RGBA=32, SCHEMATIC=33, TAG=34, TARGET=35, TEXT=36, 
		TO=37, UNIT=38, VERTICAL=39, VIRTUAL=40, ASSIGN=41, COLON=42, COMMA=43, 
		DOT=44, DOT2=45, DOT3=46, MINUS=47, MUL=48, PLUS=49, NORTH=50, SOUTH=51, 
		EAST=52, WEST=53, LEFTPAREN=54, RIGHTPAREN=55, TEXTBLOCK1=56, TEXTBLOCK2=57, 
		TEXTLINE=58, INT=59, SIGNEDINT=60, ID=61, ID_ARRAY=62, TYPE=63, PATTERN=64, 
		VERSION=65, COMMENT=66, SLCOMMENT=67, WS=68, ANY=69, PARAMEND=70, PARAMASSIGN=71, 
		PARAMCOMMENT=72, PARAMSLCOMMENT=73, PARAMWHITESPACE=74, PARAMSTRING=75, 
		PARAMTOKEN=76;
	public static final int
		RULE_definitions = 0, RULE_definition = 1, RULE_schematic = 2, RULE_schematicItem = 3, 
		RULE_attribute = 4, RULE_number = 5, RULE_versionNumber = 6, RULE_regionDefinition = 7, 
		RULE_regionDimensions = 8, RULE_block = 9, RULE_placementMode = 10, RULE_element = 11, 
		RULE_blockId = 12, RULE_labelList = 13, RULE_blockPosition = 14, RULE_position = 15, 
		RULE_coordinates = 16, RULE_relativeCoordinates = 17, RULE_coordinatesRelativeTo = 18, 
		RULE_direction = 19, RULE_configuration = 20, RULE_pattern = 21, RULE_patternSegment = 22, 
		RULE_colorDef = 23, RULE_connectionList = 24, RULE_connection = 25, RULE_processor = 26, 
		RULE_processorLinks = 27, RULE_linkDef = 28, RULE_program = 29, RULE_programSnippet = 30, 
		RULE_textDef = 31, RULE_parametrization = 32, RULE_parameter = 33, RULE_stringValue = 34, 
		RULE_stringLiteral = 35, RULE_simpleStringLiteral = 36;
	private static String[] makeRuleNames() {
		return new String[] {
			"definitions", "definition", "schematic", "schematicItem", "attribute", 
			"number", "versionNumber", "regionDefinition", "regionDimensions", "block", 
			"placementMode", "element", "blockId", "labelList", "blockPosition", 
			"position", "coordinates", "relativeCoordinates", "coordinatesRelativeTo", 
			"direction", "configuration", "pattern", "patternSegment", "colorDef", 
			"connectionList", "connection", "processor", "processorLinks", "linkDef", 
			"program", "programSnippet", "textDef", "parametrization", "parameter", 
			"stringValue", "stringLiteral", "simpleStringLiteral"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'block'", "'connected'", "'command'", "'color'", 
			"'description'", "'dimensions'", "'disabled'", "'enabled'", null, "'facing'", 
			"'file'", "'filename'", "'fill'", "'global'", "'horizontal'", "'item'", 
			"'links'", "'liquid'", "'local'", "'logic'", "'mindcode'", "'mlog'", 
			"'name'", "'param'", "'parent'", "'processor'", "'random'", "'replace'", 
			"'region'", "'rgba'", "'schematic'", "'tag'", "'target'", "'text'", "'to'", 
			"'unit'", "'vertical'", "'virtual'", null, "':'", "','", "'.'", "'..'", 
			"'...'", "'-'", "'*'", "'+'", "'north'", "'south'", "'east'", "'west'", 
			"'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AS", "AT", "BLOCK", "CONNECTED", "COMMAND", "COLOR", "DESCRIPTION", 
			"DIMENSIONS", "DISABLED", "ENABLED", "END", "FACING", "FILE", "FILENAME", 
			"FILL", "GLOBAL", "HORIZONTAL", "ITEM", "LINKS", "LIQUID", "LOCAL", "LOGIC", 
			"MINDCODE", "MLOG", "NAME", "PARAM", "PARENT", "PROCESSOR", "RANDOM", 
			"REPLACE", "REGION", "RGBA", "SCHEMATIC", "TAG", "TARGET", "TEXT", "TO", 
			"UNIT", "VERTICAL", "VIRTUAL", "ASSIGN", "COLON", "COMMA", "DOT", "DOT2", 
			"DOT3", "MINUS", "MUL", "PLUS", "NORTH", "SOUTH", "EAST", "WEST", "LEFTPAREN", 
			"RIGHTPAREN", "TEXTBLOCK1", "TEXTBLOCK2", "TEXTLINE", "INT", "SIGNEDINT", 
			"ID", "ID_ARRAY", "TYPE", "PATTERN", "VERSION", "COMMENT", "SLCOMMENT", 
			"WS", "ANY", "PARAMEND", "PARAMASSIGN", "PARAMCOMMENT", "PARAMSLCOMMENT", 
			"PARAMWHITESPACE", "PARAMSTRING", "PARAMTOKEN"
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
			setState(75); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(74);
				definition();
				}
				}
				setState(77); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCHEMATIC || _la==ID );
			setState(79);
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
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				schematic();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
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
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(85);
				((SchematicContext)_localctx).name = match(ID);
				setState(86);
				match(COLON);
				}
			}

			setState(89);
			match(SCHEMATIC);
			setState(91); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(90);
				((SchematicContext)_localctx).items = schematicItem();
				}
				}
				setState(93); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & -2305842955467865728L) != 0) );
			setState(95);
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
		public RegionDefinitionContext regionDefinition() {
			return getRuleContext(RegionDefinitionContext.class,0);
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
			setState(100);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				attribute();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				regionDefinition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(99);
				block();
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
			setState(126);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				_localctx = new NameContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(102);
				match(NAME);
				setState(103);
				match(ASSIGN);
				setState(104);
				((NameContext)_localctx).name = textDef();
				}
				break;
			case DESCRIPTION:
				_localctx = new DescriptionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
				match(DESCRIPTION);
				setState(106);
				match(ASSIGN);
				setState(107);
				((DescriptionContext)_localctx).description = textDef();
				}
				break;
			case DIMENSIONS:
				_localctx = new DimensionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(108);
				match(DIMENSIONS);
				setState(109);
				match(ASSIGN);
				setState(110);
				coordinates();
				}
				break;
			case TAG:
				_localctx = new SchemaTagContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(111);
				match(TAG);
				setState(112);
				match(ASSIGN);
				setState(113);
				((SchemaTagContext)_localctx).tag = textDef();
				}
				break;
			case FILENAME:
				_localctx = new FilenameContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(114);
				match(FILENAME);
				setState(115);
				match(ASSIGN);
				setState(116);
				((FilenameContext)_localctx).filename = simpleStringLiteral();
				}
				break;
			case TARGET:
				_localctx = new TargetContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(117);
				match(TARGET);
				setState(118);
				match(ASSIGN);
				setState(119);
				((TargetContext)_localctx).version = versionNumber();
				}
				break;
			case MINDCODE:
				_localctx = new MindcodePrologueContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(120);
				match(MINDCODE);
				setState(121);
				match(ASSIGN);
				setState(122);
				((MindcodePrologueContext)_localctx).tag = textDef();
				}
				break;
			case MLOG:
				_localctx = new MlogPrologueContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(123);
				match(MLOG);
				setState(124);
				match(ASSIGN);
				setState(125);
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
			setState(128);
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
			setState(130);
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
	public static class RegionDefinitionContext extends ParserRuleContext {
		public Token name;
		public RegionDimensionsContext dimensions;
		public BlockContext blocks;
		public TerminalNode ASSIGN() { return getToken(SchemacodeParser.ASSIGN, 0); }
		public TerminalNode REGION() { return getToken(SchemacodeParser.REGION, 0); }
		public TerminalNode END() { return getToken(SchemacodeParser.END, 0); }
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public RegionDimensionsContext regionDimensions() {
			return getRuleContext(RegionDimensionsContext.class,0);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public RegionDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_regionDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterRegionDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitRegionDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitRegionDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RegionDefinitionContext regionDefinition() throws RecognitionException {
		RegionDefinitionContext _localctx = new RegionDefinitionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_regionDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			((RegionDefinitionContext)_localctx).name = match(ID);
			setState(133);
			match(ASSIGN);
			setState(134);
			match(REGION);
			setState(136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFTPAREN) {
				{
				setState(135);
				((RegionDefinitionContext)_localctx).dimensions = regionDimensions();
				}
			}

			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2305843007066210304L) != 0)) {
				{
				{
				setState(138);
				((RegionDefinitionContext)_localctx).blocks = block();
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(144);
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
	public static class RegionDimensionsContext extends ParserRuleContext {
		public NumberContext width;
		public NumberContext height;
		public TerminalNode LEFTPAREN() { return getToken(SchemacodeParser.LEFTPAREN, 0); }
		public TerminalNode COMMA() { return getToken(SchemacodeParser.COMMA, 0); }
		public TerminalNode RIGHTPAREN() { return getToken(SchemacodeParser.RIGHTPAREN, 0); }
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public RegionDimensionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_regionDimensions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterRegionDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitRegionDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitRegionDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RegionDimensionsContext regionDimensions() throws RecognitionException {
		RegionDimensionsContext _localctx = new RegionDimensionsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_regionDimensions);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(LEFTPAREN);
			setState(147);
			((RegionDimensionsContext)_localctx).width = number();
			setState(148);
			match(COMMA);
			setState(149);
			((RegionDimensionsContext)_localctx).height = number();
			setState(150);
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
	public static class BlockContext extends ParserRuleContext {
		public LabelListContext labels;
		public ElementContext content;
		public PlacementModeContext placeMode;
		public BlockPositionContext pos;
		public DirectionContext dir;
		public ConfigurationContext cfg;
		public ElementContext element() {
			return getRuleContext(ElementContext.class,0);
		}
		public TerminalNode AT() { return getToken(SchemacodeParser.AT, 0); }
		public LabelListContext labelList() {
			return getRuleContext(LabelListContext.class,0);
		}
		public PlacementModeContext placementMode() {
			return getRuleContext(PlacementModeContext.class,0);
		}
		public BlockPositionContext blockPosition() {
			return getRuleContext(BlockPositionContext.class,0);
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
		enterRule(_localctx, 18, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(152);
				((BlockContext)_localctx).labels = labelList();
				}
				break;
			}
			setState(155);
			((BlockContext)_localctx).content = element();
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FILL || _la==REPLACE) {
				{
				setState(156);
				((BlockContext)_localctx).placeMode = placementMode();
				}
			}

			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AT) {
				{
				setState(159);
				match(AT);
				setState(160);
				((BlockContext)_localctx).pos = blockPosition();
				}
			}

			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FACING) {
				{
				setState(163);
				((BlockContext)_localctx).dir = direction();
				}
			}

			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1443378759288L) != 0)) {
				{
				setState(166);
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
	public static class PlacementModeContext extends ParserRuleContext {
		public TerminalNode FILL() { return getToken(SchemacodeParser.FILL, 0); }
		public TerminalNode REPLACE() { return getToken(SchemacodeParser.REPLACE, 0); }
		public PlacementModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_placementMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterPlacementMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitPlacementMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitPlacementMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlacementModeContext placementMode() throws RecognitionException {
		PlacementModeContext _localctx = new PlacementModeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_placementMode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			_la = _input.LA(1);
			if ( !(_la==FILL || _la==REPLACE) ) {
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
	public static class ElementContext extends ParserRuleContext {
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
	 
		public ElementContext() { }
		public void copyFrom(ElementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NamedRegionContext extends ElementContext {
		public Token elementId;
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public NamedRegionContext(ElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterNamedRegion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitNamedRegion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitNamedRegion(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InlinedRegionContext extends ElementContext {
		public RegionDimensionsContext dimensions;
		public BlockContext blocks;
		public TerminalNode REGION() { return getToken(SchemacodeParser.REGION, 0); }
		public TerminalNode END() { return getToken(SchemacodeParser.END, 0); }
		public RegionDimensionsContext regionDimensions() {
			return getRuleContext(RegionDimensionsContext.class,0);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public InlinedRegionContext(ElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterInlinedRegion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitInlinedRegion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitInlinedRegion(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BlockElementContext extends ElementContext {
		public Token elementType;
		public TerminalNode TYPE() { return getToken(SchemacodeParser.TYPE, 0); }
		public BlockElementContext(ElementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterBlockElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitBlockElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitBlockElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_element);
		int _la;
		try {
			setState(184);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TYPE:
				_localctx = new BlockElementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(171);
				((BlockElementContext)_localctx).elementType = match(TYPE);
				}
				break;
			case ID:
				_localctx = new NamedRegionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(172);
				((NamedRegionContext)_localctx).elementId = match(ID);
				}
				break;
			case REGION:
				_localctx = new InlinedRegionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(173);
				match(REGION);
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LEFTPAREN) {
					{
					setState(174);
					((InlinedRegionContext)_localctx).dimensions = regionDimensions();
					}
				}

				setState(180);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & -2305843007066210304L) != 0)) {
					{
					{
					setState(177);
					((InlinedRegionContext)_localctx).blocks = block();
					}
					}
					setState(182);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(183);
				match(END);
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
	public static class BlockIdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public TerminalNode ID_ARRAY() { return getToken(SchemacodeParser.ID_ARRAY, 0); }
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
		enterRule(_localctx, 24, RULE_blockId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			_la = _input.LA(1);
			if ( !(_la==ID || _la==ID_ARRAY) ) {
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
		enterRule(_localctx, 26, RULE_labelList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			blockId();
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(189);
				match(COMMA);
				setState(190);
				blockId();
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(196);
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
		enterRule(_localctx, 28, RULE_blockPosition);
		int _la;
		try {
			setState(217);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				_localctx = new SimplePositionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(198);
				((SimplePositionContext)_localctx).start = position();
				}
				break;
			case 2:
				_localctx = new InclusiveRangePositionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(199);
				((InclusiveRangePositionContext)_localctx).start = position();
				setState(200);
				match(DOT2);
				setState(201);
				((InclusiveRangePositionContext)_localctx).end = coordinates();
				setState(203);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(202);
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
				setState(205);
				((ExclusiveRangePositionContext)_localctx).start = position();
				setState(206);
				match(DOT3);
				setState(207);
				((ExclusiveRangePositionContext)_localctx).end = coordinates();
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(208);
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
				setState(211);
				((AreaPositionContext)_localctx).start = position();
				setState(212);
				match(MUL);
				setState(213);
				((AreaPositionContext)_localctx).size = coordinates();
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(214);
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
		enterRule(_localctx, 30, RULE_position);
		try {
			setState(222);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFTPAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(219);
				coordinates();
				}
				break;
			case MINUS:
			case PLUS:
				enterOuterAlt(_localctx, 2);
				{
				setState(220);
				relativeCoordinates();
				}
				break;
			case GLOBAL:
			case LOCAL:
			case PARENT:
			case MUL:
			case ID:
			case PATTERN:
				enterOuterAlt(_localctx, 3);
				{
				setState(221);
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
		enterRule(_localctx, 32, RULE_coordinates);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(LEFTPAREN);
			setState(225);
			((CoordinatesContext)_localctx).x = number();
			setState(226);
			match(COMMA);
			setState(227);
			((CoordinatesContext)_localctx).y = number();
			setState(228);
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
		enterRule(_localctx, 34, RULE_relativeCoordinates);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
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
			setState(231);
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
		public PatternContext label;
		public RelativeCoordinatesContext relCoord;
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
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
		enterRule(_localctx, 36, RULE_coordinatesRelativeTo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			((CoordinatesRelativeToContext)_localctx).label = pattern();
			setState(234);
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
		public TerminalNode RANDOM() { return getToken(SchemacodeParser.RANDOM, 0); }
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
		enterRule(_localctx, 38, RULE_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			match(FACING);
			setState(237);
			((DirectionContext)_localctx).dir = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16888499139510272L) != 0)) ) {
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
		public TerminalNode TYPE() { return getToken(SchemacodeParser.TYPE, 0); }
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
		public TerminalNode TYPE() { return getToken(SchemacodeParser.TYPE, 0); }
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
		public TerminalNode TYPE() { return getToken(SchemacodeParser.TYPE, 0); }
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
		public TerminalNode TYPE() { return getToken(SchemacodeParser.TYPE, 0); }
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
		public TerminalNode TYPE() { return getToken(SchemacodeParser.TYPE, 0); }
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
		enterRule(_localctx, 40, RULE_configuration);
		int _la;
		try {
			setState(259);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VIRTUAL:
				_localctx = new VirtualContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(239);
				match(VIRTUAL);
				}
				break;
			case COLOR:
				_localctx = new ColorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(240);
				match(COLOR);
				setState(241);
				colorDef();
				}
				break;
			case CONNECTED:
				_localctx = new ConnectionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(242);
				match(CONNECTED);
				setState(243);
				match(TO);
				setState(244);
				connectionList();
				}
				break;
			case BLOCK:
				_localctx = new BlocktypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(245);
				match(BLOCK);
				setState(246);
				match(TYPE);
				}
				break;
			case COMMAND:
				_localctx = new UnitcommandContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(247);
				match(COMMAND);
				setState(248);
				match(TYPE);
				}
				break;
			case ITEM:
				_localctx = new ItemContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(249);
				match(ITEM);
				setState(250);
				match(TYPE);
				}
				break;
			case LIQUID:
				_localctx = new LiquidContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(251);
				match(LIQUID);
				setState(252);
				match(TYPE);
				}
				break;
			case UNIT:
				_localctx = new UnitContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(253);
				match(UNIT);
				setState(254);
				match(TYPE);
				}
				break;
			case TEXT:
				_localctx = new TextContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(255);
				match(TEXT);
				setState(256);
				((TextContext)_localctx).text = textDef();
				}
				break;
			case DISABLED:
			case ENABLED:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(257);
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
				setState(258);
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
	public static class PatternContext extends ParserRuleContext {
		public List<PatternSegmentContext> patternSegment() {
			return getRuleContexts(PatternSegmentContext.class);
		}
		public PatternSegmentContext patternSegment(int i) {
			return getRuleContext(PatternSegmentContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(SchemacodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(SchemacodeParser.DOT, i);
		}
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_pattern);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(261);
					patternSegment();
					setState(262);
					match(DOT);
					}
					} 
				}
				setState(268);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			setState(269);
			patternSegment();
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
	public static class PatternSegmentContext extends ParserRuleContext {
		public Token text;
		public TerminalNode GLOBAL() { return getToken(SchemacodeParser.GLOBAL, 0); }
		public TerminalNode LOCAL() { return getToken(SchemacodeParser.LOCAL, 0); }
		public TerminalNode PARENT() { return getToken(SchemacodeParser.PARENT, 0); }
		public TerminalNode ID() { return getToken(SchemacodeParser.ID, 0); }
		public TerminalNode MUL() { return getToken(SchemacodeParser.MUL, 0); }
		public TerminalNode PATTERN() { return getToken(SchemacodeParser.PATTERN, 0); }
		public PatternSegmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_patternSegment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterPatternSegment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitPatternSegment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitPatternSegment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternSegmentContext patternSegment() throws RecognitionException {
		PatternSegmentContext _localctx = new PatternSegmentContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_patternSegment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			((PatternSegmentContext)_localctx).text = _input.LT(1);
			_la = _input.LA(1);
			if ( !(((((_la - 16)) & ~0x3f) == 0 && ((1L << (_la - 16)) & 316663643768865L) != 0)) ) {
				((PatternSegmentContext)_localctx).text = (Token)_errHandler.recoverInline(this);
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
		enterRule(_localctx, 46, RULE_colorDef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			match(RGBA);
			setState(274);
			match(LEFTPAREN);
			setState(275);
			((ColorDefContext)_localctx).red = number();
			setState(276);
			match(COMMA);
			setState(277);
			((ColorDefContext)_localctx).green = number();
			setState(278);
			match(COMMA);
			setState(279);
			((ColorDefContext)_localctx).blue = number();
			setState(280);
			match(COMMA);
			setState(281);
			((ColorDefContext)_localctx).alpha = number();
			setState(282);
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
		enterRule(_localctx, 48, RULE_connectionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			connection();
			setState(289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(285);
				match(COMMA);
				setState(286);
				connection();
				}
				}
				setState(291);
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
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
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
		enterRule(_localctx, 50, RULE_connection);
		try {
			setState(295);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFTPAREN:
				_localctx = new ConnAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(292);
				coordinates();
				}
				break;
			case MINUS:
			case PLUS:
				_localctx = new ConnRelContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(293);
				relativeCoordinates();
				}
				break;
			case GLOBAL:
			case LOCAL:
			case PARENT:
			case MUL:
			case ID:
			case PATTERN:
				_localctx = new ConnNameContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(294);
				pattern();
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
		public ParametrizationContext parameters;
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
		public ParametrizationContext parametrization() {
			return getRuleContext(ParametrizationContext.class,0);
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
		enterRule(_localctx, 52, RULE_processor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			match(PROCESSOR);
			setState(299);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LINKS) {
				{
				setState(298);
				((ProcessorContext)_localctx).links = processorLinks();
				}
			}

			setState(307);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MINDCODE:
				{
				setState(301);
				match(MINDCODE);
				setState(302);
				match(ASSIGN);
				setState(303);
				((ProcessorContext)_localctx).mindcode = program();
				}
				break;
			case MLOG:
				{
				setState(304);
				match(MLOG);
				setState(305);
				match(ASSIGN);
				setState(306);
				((ProcessorContext)_localctx).mlog = program();
				}
				break;
			case END:
			case PARAM:
				break;
			default:
				break;
			}
			setState(310);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARAM) {
				{
				setState(309);
				((ProcessorContext)_localctx).parameters = parametrization();
				}
			}

			setState(312);
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
		enterRule(_localctx, 54, RULE_processorLinks);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(LINKS);
			setState(318);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 16)) & ~0x3f) == 0 && ((1L << (_la - 16)) & 316949259094049L) != 0)) {
				{
				{
				setState(315);
				linkDef();
				}
				}
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(321);
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
		public PatternContext linkPattern;
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
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
		enterRule(_localctx, 56, RULE_linkDef);
		int _la;
		try {
			setState(332);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				_localctx = new LinkPatternContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(323);
				((LinkPatternContext)_localctx).linkPattern = pattern();
				}
				break;
			case 2:
				_localctx = new LinkPosContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(324);
				((LinkPosContext)_localctx).linkPos = connection();
				setState(330);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(325);
					match(AS);
					setState(326);
					((LinkPosContext)_localctx).alias = match(ID);
					setState(328);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==VIRTUAL) {
						{
						setState(327);
						((LinkPosContext)_localctx).virtual = match(VIRTUAL);
						}
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
		enterRule(_localctx, 58, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			programSnippet();
			setState(339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS) {
				{
				{
				setState(335);
				match(PLUS);
				setState(336);
				programSnippet();
				}
				}
				setState(341);
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
		enterRule(_localctx, 60, RULE_programSnippet);
		try {
			setState(345);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTBLOCK1:
			case TEXTBLOCK2:
			case TEXTLINE:
			case ID:
				_localctx = new ProgramStringContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(342);
				((ProgramStringContext)_localctx).text = textDef();
				}
				break;
			case FILE:
				_localctx = new ProgramFileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(343);
				match(FILE);
				setState(344);
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
		enterRule(_localctx, 62, RULE_textDef);
		try {
			setState(349);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTBLOCK1:
			case TEXTBLOCK2:
			case TEXTLINE:
				_localctx = new TextLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				((TextLiteralContext)_localctx).reference = stringLiteral();
				}
				break;
			case ID:
				_localctx = new TextIdContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(348);
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
	public static class ParametrizationContext extends ParserRuleContext {
		public TerminalNode PARAM() { return getToken(SchemacodeParser.PARAM, 0); }
		public TerminalNode PARAMEND() { return getToken(SchemacodeParser.PARAMEND, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public ParametrizationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parametrization; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterParametrization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitParametrization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitParametrization(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametrizationContext parametrization() throws RecognitionException {
		ParametrizationContext _localctx = new ParametrizationContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_parametrization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			match(PARAM);
			setState(355);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAMTOKEN) {
				{
				{
				setState(352);
				parameter();
				}
				}
				setState(357);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(358);
			match(PARAMEND);
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
	public static class ParameterContext extends ParserRuleContext {
		public Token variable;
		public Token strValue;
		public Token value;
		public TerminalNode PARAMASSIGN() { return getToken(SchemacodeParser.PARAMASSIGN, 0); }
		public List<TerminalNode> PARAMTOKEN() { return getTokens(SchemacodeParser.PARAMTOKEN); }
		public TerminalNode PARAMTOKEN(int i) {
			return getToken(SchemacodeParser.PARAMTOKEN, i);
		}
		public TerminalNode PARAMSTRING() { return getToken(SchemacodeParser.PARAMSTRING, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeParserListener ) ((SchemacodeParserListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeParserVisitor ) return ((SchemacodeParserVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_parameter);
		try {
			setState(366);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(360);
				((ParameterContext)_localctx).variable = match(PARAMTOKEN);
				setState(361);
				match(PARAMASSIGN);
				setState(362);
				((ParameterContext)_localctx).strValue = match(PARAMSTRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(363);
				((ParameterContext)_localctx).variable = match(PARAMTOKEN);
				setState(364);
				match(PARAMASSIGN);
				setState(365);
				((ParameterContext)_localctx).value = match(PARAMTOKEN);
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
		enterRule(_localctx, 68, RULE_stringValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(368);
			((StringValueContext)_localctx).name = match(ID);
			setState(369);
			match(ASSIGN);
			setState(370);
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
		enterRule(_localctx, 70, RULE_stringLiteral);
		int _la;
		try {
			setState(374);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTLINE:
				_localctx = new TextLineContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(372);
				match(TEXTLINE);
				}
				break;
			case TEXTBLOCK1:
			case TEXTBLOCK2:
				_localctx = new TextBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(373);
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
		enterRule(_localctx, 72, RULE_simpleStringLiteral);
		try {
			_localctx = new SimpleTextLineContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
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
		"\u0004\u0001L\u017b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0001\u0000\u0004\u0000L\b\u0000\u000b\u0000\f"+
		"\u0000M\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001T\b"+
		"\u0001\u0001\u0002\u0001\u0002\u0003\u0002X\b\u0002\u0001\u0002\u0001"+
		"\u0002\u0004\u0002\\\b\u0002\u000b\u0002\f\u0002]\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003e\b\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"\u007f\b\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0089\b\u0007\u0001\u0007"+
		"\u0005\u0007\u008c\b\u0007\n\u0007\f\u0007\u008f\t\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0003\t"+
		"\u009a\b\t\u0001\t\u0001\t\u0003\t\u009e\b\t\u0001\t\u0001\t\u0003\t\u00a2"+
		"\b\t\u0001\t\u0003\t\u00a5\b\t\u0001\t\u0003\t\u00a8\b\t\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00b0\b"+
		"\u000b\u0001\u000b\u0005\u000b\u00b3\b\u000b\n\u000b\f\u000b\u00b6\t\u000b"+
		"\u0001\u000b\u0003\u000b\u00b9\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r"+
		"\u0001\r\u0005\r\u00c0\b\r\n\r\f\r\u00c3\t\r\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00cc\b\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00d2\b\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00d8\b\u000e"+
		"\u0003\u000e\u00da\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f"+
		"\u00df\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0003\u0014\u0104\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015"+
		"\u0109\b\u0015\n\u0015\f\u0015\u010c\t\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018\u0120\b\u0018\n"+
		"\u0018\f\u0018\u0123\t\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0003"+
		"\u0019\u0128\b\u0019\u0001\u001a\u0001\u001a\u0003\u001a\u012c\b\u001a"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0003\u001a\u0134\b\u001a\u0001\u001a\u0003\u001a\u0137\b\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0005\u001b\u013d\b\u001b\n"+
		"\u001b\f\u001b\u0140\t\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0149\b\u001c\u0003"+
		"\u001c\u014b\b\u001c\u0003\u001c\u014d\b\u001c\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0005\u001d\u0152\b\u001d\n\u001d\f\u001d\u0155\t\u001d\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u015a\b\u001e\u0001\u001f\u0001"+
		"\u001f\u0003\u001f\u015e\b\u001f\u0001 \u0001 \u0005 \u0162\b \n \f \u0165"+
		"\t \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0003!\u016f"+
		"\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0003#\u0177\b#\u0001"+
		"$\u0001$\u0001$\u0000\u0000%\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFH\u0000"+
		"\n\u0001\u0000;<\u0002\u0000;;AA\u0002\u0000\u000f\u000f\u001e\u001e\u0001"+
		"\u0000=>\u0002\u0000\u0011\u0011\'\'\u0002\u0000//11\u0002\u0000\u001d"+
		"\u001d25\u0001\u0000\t\n\u0006\u0000\u0010\u0010\u0015\u0015\u001b\u001b"+
		"00==@@\u0001\u000089\u0192\u0000K\u0001\u0000\u0000\u0000\u0002S\u0001"+
		"\u0000\u0000\u0000\u0004W\u0001\u0000\u0000\u0000\u0006d\u0001\u0000\u0000"+
		"\u0000\b~\u0001\u0000\u0000\u0000\n\u0080\u0001\u0000\u0000\u0000\f\u0082"+
		"\u0001\u0000\u0000\u0000\u000e\u0084\u0001\u0000\u0000\u0000\u0010\u0092"+
		"\u0001\u0000\u0000\u0000\u0012\u0099\u0001\u0000\u0000\u0000\u0014\u00a9"+
		"\u0001\u0000\u0000\u0000\u0016\u00b8\u0001\u0000\u0000\u0000\u0018\u00ba"+
		"\u0001\u0000\u0000\u0000\u001a\u00bc\u0001\u0000\u0000\u0000\u001c\u00d9"+
		"\u0001\u0000\u0000\u0000\u001e\u00de\u0001\u0000\u0000\u0000 \u00e0\u0001"+
		"\u0000\u0000\u0000\"\u00e6\u0001\u0000\u0000\u0000$\u00e9\u0001\u0000"+
		"\u0000\u0000&\u00ec\u0001\u0000\u0000\u0000(\u0103\u0001\u0000\u0000\u0000"+
		"*\u010a\u0001\u0000\u0000\u0000,\u010f\u0001\u0000\u0000\u0000.\u0111"+
		"\u0001\u0000\u0000\u00000\u011c\u0001\u0000\u0000\u00002\u0127\u0001\u0000"+
		"\u0000\u00004\u0129\u0001\u0000\u0000\u00006\u013a\u0001\u0000\u0000\u0000"+
		"8\u014c\u0001\u0000\u0000\u0000:\u014e\u0001\u0000\u0000\u0000<\u0159"+
		"\u0001\u0000\u0000\u0000>\u015d\u0001\u0000\u0000\u0000@\u015f\u0001\u0000"+
		"\u0000\u0000B\u016e\u0001\u0000\u0000\u0000D\u0170\u0001\u0000\u0000\u0000"+
		"F\u0176\u0001\u0000\u0000\u0000H\u0178\u0001\u0000\u0000\u0000JL\u0003"+
		"\u0002\u0001\u0000KJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000"+
		"MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000"+
		"\u0000OP\u0005\u0000\u0000\u0001P\u0001\u0001\u0000\u0000\u0000QT\u0003"+
		"\u0004\u0002\u0000RT\u0003D\"\u0000SQ\u0001\u0000\u0000\u0000SR\u0001"+
		"\u0000\u0000\u0000T\u0003\u0001\u0000\u0000\u0000UV\u0005=\u0000\u0000"+
		"VX\u0005*\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000"+
		"XY\u0001\u0000\u0000\u0000Y[\u0005!\u0000\u0000Z\\\u0003\u0006\u0003\u0000"+
		"[Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000][\u0001\u0000\u0000"+
		"\u0000]^\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_`\u0005\u000b"+
		"\u0000\u0000`\u0005\u0001\u0000\u0000\u0000ae\u0003\b\u0004\u0000be\u0003"+
		"\u000e\u0007\u0000ce\u0003\u0012\t\u0000da\u0001\u0000\u0000\u0000db\u0001"+
		"\u0000\u0000\u0000dc\u0001\u0000\u0000\u0000e\u0007\u0001\u0000\u0000"+
		"\u0000fg\u0005\u0019\u0000\u0000gh\u0005)\u0000\u0000h\u007f\u0003>\u001f"+
		"\u0000ij\u0005\u0007\u0000\u0000jk\u0005)\u0000\u0000k\u007f\u0003>\u001f"+
		"\u0000lm\u0005\b\u0000\u0000mn\u0005)\u0000\u0000n\u007f\u0003 \u0010"+
		"\u0000op\u0005\"\u0000\u0000pq\u0005)\u0000\u0000q\u007f\u0003>\u001f"+
		"\u0000rs\u0005\u000e\u0000\u0000st\u0005)\u0000\u0000t\u007f\u0003H$\u0000"+
		"uv\u0005#\u0000\u0000vw\u0005)\u0000\u0000w\u007f\u0003\f\u0006\u0000"+
		"xy\u0005\u0017\u0000\u0000yz\u0005)\u0000\u0000z\u007f\u0003>\u001f\u0000"+
		"{|\u0005\u0018\u0000\u0000|}\u0005)\u0000\u0000}\u007f\u0003>\u001f\u0000"+
		"~f\u0001\u0000\u0000\u0000~i\u0001\u0000\u0000\u0000~l\u0001\u0000\u0000"+
		"\u0000~o\u0001\u0000\u0000\u0000~r\u0001\u0000\u0000\u0000~u\u0001\u0000"+
		"\u0000\u0000~x\u0001\u0000\u0000\u0000~{\u0001\u0000\u0000\u0000\u007f"+
		"\t\u0001\u0000\u0000\u0000\u0080\u0081\u0007\u0000\u0000\u0000\u0081\u000b"+
		"\u0001\u0000\u0000\u0000\u0082\u0083\u0007\u0001\u0000\u0000\u0083\r\u0001"+
		"\u0000\u0000\u0000\u0084\u0085\u0005=\u0000\u0000\u0085\u0086\u0005)\u0000"+
		"\u0000\u0086\u0088\u0005\u001f\u0000\u0000\u0087\u0089\u0003\u0010\b\u0000"+
		"\u0088\u0087\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000"+
		"\u0089\u008d\u0001\u0000\u0000\u0000\u008a\u008c\u0003\u0012\t\u0000\u008b"+
		"\u008a\u0001\u0000\u0000\u0000\u008c\u008f\u0001\u0000\u0000\u0000\u008d"+
		"\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e"+
		"\u0090\u0001\u0000\u0000\u0000\u008f\u008d\u0001\u0000\u0000\u0000\u0090"+
		"\u0091\u0005\u000b\u0000\u0000\u0091\u000f\u0001\u0000\u0000\u0000\u0092"+
		"\u0093\u00056\u0000\u0000\u0093\u0094\u0003\n\u0005\u0000\u0094\u0095"+
		"\u0005+\u0000\u0000\u0095\u0096\u0003\n\u0005\u0000\u0096\u0097\u0005"+
		"7\u0000\u0000\u0097\u0011\u0001\u0000\u0000\u0000\u0098\u009a\u0003\u001a"+
		"\r\u0000\u0099\u0098\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000"+
		"\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009d\u0003\u0016\u000b"+
		"\u0000\u009c\u009e\u0003\u0014\n\u0000\u009d\u009c\u0001\u0000\u0000\u0000"+
		"\u009d\u009e\u0001\u0000\u0000\u0000\u009e\u00a1\u0001\u0000\u0000\u0000"+
		"\u009f\u00a0\u0005\u0002\u0000\u0000\u00a0\u00a2\u0003\u001c\u000e\u0000"+
		"\u00a1\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000"+
		"\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u00a5\u0003&\u0013\u0000\u00a4"+
		"\u00a3\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5"+
		"\u00a7\u0001\u0000\u0000\u0000\u00a6\u00a8\u0003(\u0014\u0000\u00a7\u00a6"+
		"\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\u0013"+
		"\u0001\u0000\u0000\u0000\u00a9\u00aa\u0007\u0002\u0000\u0000\u00aa\u0015"+
		"\u0001\u0000\u0000\u0000\u00ab\u00b9\u0005?\u0000\u0000\u00ac\u00b9\u0005"+
		"=\u0000\u0000\u00ad\u00af\u0005\u001f\u0000\u0000\u00ae\u00b0\u0003\u0010"+
		"\b\u0000\u00af\u00ae\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000"+
		"\u0000\u00b0\u00b4\u0001\u0000\u0000\u0000\u00b1\u00b3\u0003\u0012\t\u0000"+
		"\u00b2\u00b1\u0001\u0000\u0000\u0000\u00b3\u00b6\u0001\u0000\u0000\u0000"+
		"\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000"+
		"\u00b5\u00b7\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000"+
		"\u00b7\u00b9\u0005\u000b\u0000\u0000\u00b8\u00ab\u0001\u0000\u0000\u0000"+
		"\u00b8\u00ac\u0001\u0000\u0000\u0000\u00b8\u00ad\u0001\u0000\u0000\u0000"+
		"\u00b9\u0017\u0001\u0000\u0000\u0000\u00ba\u00bb\u0007\u0003\u0000\u0000"+
		"\u00bb\u0019\u0001\u0000\u0000\u0000\u00bc\u00c1\u0003\u0018\f\u0000\u00bd"+
		"\u00be\u0005+\u0000\u0000\u00be\u00c0\u0003\u0018\f\u0000\u00bf\u00bd"+
		"\u0001\u0000\u0000\u0000\u00c0\u00c3\u0001\u0000\u0000\u0000\u00c1\u00bf"+
		"\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c4"+
		"\u0001\u0000\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c4\u00c5"+
		"\u0005*\u0000\u0000\u00c5\u001b\u0001\u0000\u0000\u0000\u00c6\u00da\u0003"+
		"\u001e\u000f\u0000\u00c7\u00c8\u0003\u001e\u000f\u0000\u00c8\u00c9\u0005"+
		"-\u0000\u0000\u00c9\u00cb\u0003 \u0010\u0000\u00ca\u00cc\u0007\u0004\u0000"+
		"\u0000\u00cb\u00ca\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000"+
		"\u0000\u00cc\u00da\u0001\u0000\u0000\u0000\u00cd\u00ce\u0003\u001e\u000f"+
		"\u0000\u00ce\u00cf\u0005.\u0000\u0000\u00cf\u00d1\u0003 \u0010\u0000\u00d0"+
		"\u00d2\u0007\u0004\u0000\u0000\u00d1\u00d0\u0001\u0000\u0000\u0000\u00d1"+
		"\u00d2\u0001\u0000\u0000\u0000\u00d2\u00da\u0001\u0000\u0000\u0000\u00d3"+
		"\u00d4\u0003\u001e\u000f\u0000\u00d4\u00d5\u00050\u0000\u0000\u00d5\u00d7"+
		"\u0003 \u0010\u0000\u00d6\u00d8\u0007\u0004\u0000\u0000\u00d7\u00d6\u0001"+
		"\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00da\u0001"+
		"\u0000\u0000\u0000\u00d9\u00c6\u0001\u0000\u0000\u0000\u00d9\u00c7\u0001"+
		"\u0000\u0000\u0000\u00d9\u00cd\u0001\u0000\u0000\u0000\u00d9\u00d3\u0001"+
		"\u0000\u0000\u0000\u00da\u001d\u0001\u0000\u0000\u0000\u00db\u00df\u0003"+
		" \u0010\u0000\u00dc\u00df\u0003\"\u0011\u0000\u00dd\u00df\u0003$\u0012"+
		"\u0000\u00de\u00db\u0001\u0000\u0000\u0000\u00de\u00dc\u0001\u0000\u0000"+
		"\u0000\u00de\u00dd\u0001\u0000\u0000\u0000\u00df\u001f\u0001\u0000\u0000"+
		"\u0000\u00e0\u00e1\u00056\u0000\u0000\u00e1\u00e2\u0003\n\u0005\u0000"+
		"\u00e2\u00e3\u0005+\u0000\u0000\u00e3\u00e4\u0003\n\u0005\u0000\u00e4"+
		"\u00e5\u00057\u0000\u0000\u00e5!\u0001\u0000\u0000\u0000\u00e6\u00e7\u0007"+
		"\u0005\u0000\u0000\u00e7\u00e8\u0003 \u0010\u0000\u00e8#\u0001\u0000\u0000"+
		"\u0000\u00e9\u00ea\u0003*\u0015\u0000\u00ea\u00eb\u0003\"\u0011\u0000"+
		"\u00eb%\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005\f\u0000\u0000\u00ed"+
		"\u00ee\u0007\u0006\u0000\u0000\u00ee\'\u0001\u0000\u0000\u0000\u00ef\u0104"+
		"\u0005(\u0000\u0000\u00f0\u00f1\u0005\u0006\u0000\u0000\u00f1\u0104\u0003"+
		".\u0017\u0000\u00f2\u00f3\u0005\u0004\u0000\u0000\u00f3\u00f4\u0005%\u0000"+
		"\u0000\u00f4\u0104\u00030\u0018\u0000\u00f5\u00f6\u0005\u0003\u0000\u0000"+
		"\u00f6\u0104\u0005?\u0000\u0000\u00f7\u00f8\u0005\u0005\u0000\u0000\u00f8"+
		"\u0104\u0005?\u0000\u0000\u00f9\u00fa\u0005\u0012\u0000\u0000\u00fa\u0104"+
		"\u0005?\u0000\u0000\u00fb\u00fc\u0005\u0014\u0000\u0000\u00fc\u0104\u0005"+
		"?\u0000\u0000\u00fd\u00fe\u0005&\u0000\u0000\u00fe\u0104\u0005?\u0000"+
		"\u0000\u00ff\u0100\u0005$\u0000\u0000\u0100\u0104\u0003>\u001f\u0000\u0101"+
		"\u0104\u0007\u0007\u0000\u0000\u0102\u0104\u00034\u001a\u0000\u0103\u00ef"+
		"\u0001\u0000\u0000\u0000\u0103\u00f0\u0001\u0000\u0000\u0000\u0103\u00f2"+
		"\u0001\u0000\u0000\u0000\u0103\u00f5\u0001\u0000\u0000\u0000\u0103\u00f7"+
		"\u0001\u0000\u0000\u0000\u0103\u00f9\u0001\u0000\u0000\u0000\u0103\u00fb"+
		"\u0001\u0000\u0000\u0000\u0103\u00fd\u0001\u0000\u0000\u0000\u0103\u00ff"+
		"\u0001\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000\u0103\u0102"+
		"\u0001\u0000\u0000\u0000\u0104)\u0001\u0000\u0000\u0000\u0105\u0106\u0003"+
		",\u0016\u0000\u0106\u0107\u0005,\u0000\u0000\u0107\u0109\u0001\u0000\u0000"+
		"\u0000\u0108\u0105\u0001\u0000\u0000\u0000\u0109\u010c\u0001\u0000\u0000"+
		"\u0000\u010a\u0108\u0001\u0000\u0000\u0000\u010a\u010b\u0001\u0000\u0000"+
		"\u0000\u010b\u010d\u0001\u0000\u0000\u0000\u010c\u010a\u0001\u0000\u0000"+
		"\u0000\u010d\u010e\u0003,\u0016\u0000\u010e+\u0001\u0000\u0000\u0000\u010f"+
		"\u0110\u0007\b\u0000\u0000\u0110-\u0001\u0000\u0000\u0000\u0111\u0112"+
		"\u0005 \u0000\u0000\u0112\u0113\u00056\u0000\u0000\u0113\u0114\u0003\n"+
		"\u0005\u0000\u0114\u0115\u0005+\u0000\u0000\u0115\u0116\u0003\n\u0005"+
		"\u0000\u0116\u0117\u0005+\u0000\u0000\u0117\u0118\u0003\n\u0005\u0000"+
		"\u0118\u0119\u0005+\u0000\u0000\u0119\u011a\u0003\n\u0005\u0000\u011a"+
		"\u011b\u00057\u0000\u0000\u011b/\u0001\u0000\u0000\u0000\u011c\u0121\u0003"+
		"2\u0019\u0000\u011d\u011e\u0005+\u0000\u0000\u011e\u0120\u00032\u0019"+
		"\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u0120\u0123\u0001\u0000\u0000"+
		"\u0000\u0121\u011f\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000"+
		"\u0000\u01221\u0001\u0000\u0000\u0000\u0123\u0121\u0001\u0000\u0000\u0000"+
		"\u0124\u0128\u0003 \u0010\u0000\u0125\u0128\u0003\"\u0011\u0000\u0126"+
		"\u0128\u0003*\u0015\u0000\u0127\u0124\u0001\u0000\u0000\u0000\u0127\u0125"+
		"\u0001\u0000\u0000\u0000\u0127\u0126\u0001\u0000\u0000\u0000\u01283\u0001"+
		"\u0000\u0000\u0000\u0129\u012b\u0005\u001c\u0000\u0000\u012a\u012c\u0003"+
		"6\u001b\u0000\u012b\u012a\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000"+
		"\u0000\u0000\u012c\u0133\u0001\u0000\u0000\u0000\u012d\u012e\u0005\u0017"+
		"\u0000\u0000\u012e\u012f\u0005)\u0000\u0000\u012f\u0134\u0003:\u001d\u0000"+
		"\u0130\u0131\u0005\u0018\u0000\u0000\u0131\u0132\u0005)\u0000\u0000\u0132"+
		"\u0134\u0003:\u001d\u0000\u0133\u012d\u0001\u0000\u0000\u0000\u0133\u0130"+
		"\u0001\u0000\u0000\u0000\u0133\u0134\u0001\u0000\u0000\u0000\u0134\u0136"+
		"\u0001\u0000\u0000\u0000\u0135\u0137\u0003@ \u0000\u0136\u0135\u0001\u0000"+
		"\u0000\u0000\u0136\u0137\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000"+
		"\u0000\u0000\u0138\u0139\u0005\u000b\u0000\u0000\u01395\u0001\u0000\u0000"+
		"\u0000\u013a\u013e\u0005\u0013\u0000\u0000\u013b\u013d\u00038\u001c\u0000"+
		"\u013c\u013b\u0001\u0000\u0000\u0000\u013d\u0140\u0001\u0000\u0000\u0000"+
		"\u013e\u013c\u0001\u0000\u0000\u0000\u013e\u013f\u0001\u0000\u0000\u0000"+
		"\u013f\u0141\u0001\u0000\u0000\u0000\u0140\u013e\u0001\u0000\u0000\u0000"+
		"\u0141\u0142\u0005\u000b\u0000\u0000\u01427\u0001\u0000\u0000\u0000\u0143"+
		"\u014d\u0003*\u0015\u0000\u0144\u014a\u00032\u0019\u0000\u0145\u0146\u0005"+
		"\u0001\u0000\u0000\u0146\u0148\u0005=\u0000\u0000\u0147\u0149\u0005(\u0000"+
		"\u0000\u0148\u0147\u0001\u0000\u0000\u0000\u0148\u0149\u0001\u0000\u0000"+
		"\u0000\u0149\u014b\u0001\u0000\u0000\u0000\u014a\u0145\u0001\u0000\u0000"+
		"\u0000\u014a\u014b\u0001\u0000\u0000\u0000\u014b\u014d\u0001\u0000\u0000"+
		"\u0000\u014c\u0143\u0001\u0000\u0000\u0000\u014c\u0144\u0001\u0000\u0000"+
		"\u0000\u014d9\u0001\u0000\u0000\u0000\u014e\u0153\u0003<\u001e\u0000\u014f"+
		"\u0150\u00051\u0000\u0000\u0150\u0152\u0003<\u001e\u0000\u0151\u014f\u0001"+
		"\u0000\u0000\u0000\u0152\u0155\u0001\u0000\u0000\u0000\u0153\u0151\u0001"+
		"\u0000\u0000\u0000\u0153\u0154\u0001\u0000\u0000\u0000\u0154;\u0001\u0000"+
		"\u0000\u0000\u0155\u0153\u0001\u0000\u0000\u0000\u0156\u015a\u0003>\u001f"+
		"\u0000\u0157\u0158\u0005\r\u0000\u0000\u0158\u015a\u0003>\u001f\u0000"+
		"\u0159\u0156\u0001\u0000\u0000\u0000\u0159\u0157\u0001\u0000\u0000\u0000"+
		"\u015a=\u0001\u0000\u0000\u0000\u015b\u015e\u0003F#\u0000\u015c\u015e"+
		"\u0005=\u0000\u0000\u015d\u015b\u0001\u0000\u0000\u0000\u015d\u015c\u0001"+
		"\u0000\u0000\u0000\u015e?\u0001\u0000\u0000\u0000\u015f\u0163\u0005\u001a"+
		"\u0000\u0000\u0160\u0162\u0003B!\u0000\u0161\u0160\u0001\u0000\u0000\u0000"+
		"\u0162\u0165\u0001\u0000\u0000\u0000\u0163\u0161\u0001\u0000\u0000\u0000"+
		"\u0163\u0164\u0001\u0000\u0000\u0000\u0164\u0166\u0001\u0000\u0000\u0000"+
		"\u0165\u0163\u0001\u0000\u0000\u0000\u0166\u0167\u0005F\u0000\u0000\u0167"+
		"A\u0001\u0000\u0000\u0000\u0168\u0169\u0005L\u0000\u0000\u0169\u016a\u0005"+
		"G\u0000\u0000\u016a\u016f\u0005K\u0000\u0000\u016b\u016c\u0005L\u0000"+
		"\u0000\u016c\u016d\u0005G\u0000\u0000\u016d\u016f\u0005L\u0000\u0000\u016e"+
		"\u0168\u0001\u0000\u0000\u0000\u016e\u016b\u0001\u0000\u0000\u0000\u016f"+
		"C\u0001\u0000\u0000\u0000\u0170\u0171\u0005=\u0000\u0000\u0171\u0172\u0005"+
		")\u0000\u0000\u0172\u0173\u0003F#\u0000\u0173E\u0001\u0000\u0000\u0000"+
		"\u0174\u0177\u0005:\u0000\u0000\u0175\u0177\u0007\t\u0000\u0000\u0176"+
		"\u0174\u0001\u0000\u0000\u0000\u0176\u0175\u0001\u0000\u0000\u0000\u0177"+
		"G\u0001\u0000\u0000\u0000\u0178\u0179\u0005:\u0000\u0000\u0179I\u0001"+
		"\u0000\u0000\u0000\'MSW]d~\u0088\u008d\u0099\u009d\u00a1\u00a4\u00a7\u00af"+
		"\u00b4\u00b8\u00c1\u00cb\u00d1\u00d7\u00d9\u00de\u0103\u010a\u0121\u0127"+
		"\u012b\u0133\u0136\u013e\u0148\u014a\u014c\u0153\u0159\u015d\u0163\u016e"+
		"\u0176";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
