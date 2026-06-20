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
		PARAM=23, PROCESSOR=24, RGBA=25, SCHEMATIC=26, TAG=27, TARGET=28, TEXT=29, 
		TO=30, UNIT=31, VERTICAL=32, VIRTUAL=33, ASSIGN=34, COLON=35, COMMA=36, 
		DOT=37, DOT2=38, DOT3=39, MINUS=40, MUL=41, PLUS=42, NORTH=43, SOUTH=44, 
		EAST=45, WEST=46, LEFTPAREN=47, RIGHTPAREN=48, TEXTBLOCK1=49, TEXTBLOCK2=50, 
		TEXTLINE=51, INT=52, SIGNEDINT=53, ID=54, BLOCKID=55, REF=56, PATTERN=57, 
		VERSION=58, COMMENT=59, SLCOMMENT=60, WS=61, ANY=62, PARAMEND=63, PARAMASSIGN=64, 
		PARAMCOMMENT=65, PARAMSLCOMMENT=66, PARAMWHITESPACE=67, PARAMSTRING=68, 
		PARAMTOKEN=69;
	public static final int
		RULE_definitions = 0, RULE_definition = 1, RULE_schematic = 2, RULE_schematicItem = 3, 
		RULE_attribute = 4, RULE_number = 5, RULE_versionNumber = 6, RULE_block = 7, 
		RULE_blockId = 8, RULE_labelList = 9, RULE_blockPosition = 10, RULE_position = 11, 
		RULE_coordinates = 12, RULE_relativeCoordinates = 13, RULE_coordinatesRelativeTo = 14, 
		RULE_direction = 15, RULE_configuration = 16, RULE_colorDef = 17, RULE_connectionList = 18, 
		RULE_connection = 19, RULE_processor = 20, RULE_processorLinks = 21, RULE_linkDef = 22, 
		RULE_program = 23, RULE_programSnippet = 24, RULE_textDef = 25, RULE_parametrization = 26, 
		RULE_parameter = 27, RULE_stringValue = 28, RULE_stringLiteral = 29, RULE_simpleStringLiteral = 30;
	private static String[] makeRuleNames() {
		return new String[] {
			"definitions", "definition", "schematic", "schematicItem", "attribute", 
			"number", "versionNumber", "block", "blockId", "labelList", "blockPosition", 
			"position", "coordinates", "relativeCoordinates", "coordinatesRelativeTo", 
			"direction", "configuration", "colorDef", "connectionList", "connection", 
			"processor", "processorLinks", "linkDef", "program", "programSnippet", 
			"textDef", "parametrization", "parameter", "stringValue", "stringLiteral", 
			"simpleStringLiteral"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'as'", "'at'", "'block'", "'connected'", "'command'", "'color'", 
			"'description'", "'dimensions'", "'disabled'", "'enabled'", null, "'facing'", 
			"'file'", "'filename'", "'horizontal'", "'item'", "'links'", "'liquid'", 
			"'logic'", "'mindcode'", "'mlog'", "'name'", "'param'", "'processor'", 
			"'rgba'", "'schematic'", "'tag'", "'target'", "'text'", "'to'", "'unit'", 
			"'vertical'", "'virtual'", null, "':'", "','", "'.'", "'..'", "'...'", 
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
			"NAME", "PARAM", "PROCESSOR", "RGBA", "SCHEMATIC", "TAG", "TARGET", "TEXT", 
			"TO", "UNIT", "VERTICAL", "VIRTUAL", "ASSIGN", "COLON", "COMMA", "DOT", 
			"DOT2", "DOT3", "MINUS", "MUL", "PLUS", "NORTH", "SOUTH", "EAST", "WEST", 
			"LEFTPAREN", "RIGHTPAREN", "TEXTBLOCK1", "TEXTBLOCK2", "TEXTLINE", "INT", 
			"SIGNEDINT", "ID", "BLOCKID", "REF", "PATTERN", "VERSION", "COMMENT", 
			"SLCOMMENT", "WS", "ANY", "PARAMEND", "PARAMASSIGN", "PARAMCOMMENT", 
			"PARAMSLCOMMENT", "PARAMWHITESPACE", "PARAMSTRING", "PARAMTOKEN"
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
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				definition();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SCHEMATIC || _la==ID );
			setState(67);
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
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(69);
				schematic();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
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
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(73);
				((SchematicContext)_localctx).name = match(ID);
				setState(74);
				match(COLON);
				}
			}

			setState(77);
			match(SCHEMATIC);
			setState(79); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(78);
				((SchematicContext)_localctx).items = schematicItem();
				}
				}
				setState(81); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 126100789976383872L) != 0) );
			setState(83);
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
			setState(87);
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
				setState(85);
				attribute();
				}
				break;
			case ID:
			case BLOCKID:
			case REF:
				enterOuterAlt(_localctx, 2);
				{
				setState(86);
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
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				_localctx = new NameContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(89);
				match(NAME);
				setState(90);
				match(ASSIGN);
				setState(91);
				((NameContext)_localctx).name = textDef();
				}
				break;
			case DESCRIPTION:
				_localctx = new DescriptionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(92);
				match(DESCRIPTION);
				setState(93);
				match(ASSIGN);
				setState(94);
				((DescriptionContext)_localctx).description = textDef();
				}
				break;
			case DIMENSIONS:
				_localctx = new DimensionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(95);
				match(DIMENSIONS);
				setState(96);
				match(ASSIGN);
				setState(97);
				coordinates();
				}
				break;
			case TAG:
				_localctx = new SchemaTagContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(98);
				match(TAG);
				setState(99);
				match(ASSIGN);
				setState(100);
				((SchemaTagContext)_localctx).tag = textDef();
				}
				break;
			case FILENAME:
				_localctx = new FilenameContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(101);
				match(FILENAME);
				setState(102);
				match(ASSIGN);
				setState(103);
				((FilenameContext)_localctx).filename = simpleStringLiteral();
				}
				break;
			case TARGET:
				_localctx = new TargetContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(104);
				match(TARGET);
				setState(105);
				match(ASSIGN);
				setState(106);
				((TargetContext)_localctx).version = versionNumber();
				}
				break;
			case MINDCODE:
				_localctx = new MindcodePrologueContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(107);
				match(MINDCODE);
				setState(108);
				match(ASSIGN);
				setState(109);
				((MindcodePrologueContext)_localctx).tag = textDef();
				}
				break;
			case MLOG:
				_localctx = new MlogPrologueContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(110);
				match(MLOG);
				setState(111);
				match(ASSIGN);
				setState(112);
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
			setState(115);
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
			setState(117);
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
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID || _la==BLOCKID) {
				{
				setState(119);
				((BlockContext)_localctx).labels = labelList();
				}
			}

			setState(122);
			((BlockContext)_localctx).type = match(REF);
			setState(123);
			match(AT);
			setState(124);
			((BlockContext)_localctx).pos = blockPosition();
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FACING) {
				{
				setState(125);
				((BlockContext)_localctx).dir = direction();
				}
			}

			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 11291395704L) != 0)) {
				{
				setState(128);
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
			setState(131);
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
			setState(133);
			blockId();
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(134);
				match(COMMA);
				setState(135);
				blockId();
				}
				}
				setState(140);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(141);
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
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new SimplePositionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(143);
				((SimplePositionContext)_localctx).start = position();
				}
				break;
			case 2:
				_localctx = new InclusiveRangePositionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				((InclusiveRangePositionContext)_localctx).start = position();
				setState(145);
				match(DOT2);
				setState(146);
				((InclusiveRangePositionContext)_localctx).end = coordinates();
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(147);
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
				setState(150);
				((ExclusiveRangePositionContext)_localctx).start = position();
				setState(151);
				match(DOT3);
				setState(152);
				((ExclusiveRangePositionContext)_localctx).end = coordinates();
				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(153);
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
				setState(156);
				((AreaPositionContext)_localctx).start = position();
				setState(157);
				match(MUL);
				setState(158);
				((AreaPositionContext)_localctx).size = coordinates();
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HORIZONTAL || _la==VERTICAL) {
					{
					setState(159);
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
			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFTPAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				coordinates();
				}
				break;
			case MINUS:
			case PLUS:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				relativeCoordinates();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(166);
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
			setState(169);
			match(LEFTPAREN);
			setState(170);
			((CoordinatesContext)_localctx).x = number();
			setState(171);
			match(COMMA);
			setState(172);
			((CoordinatesContext)_localctx).y = number();
			setState(173);
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
			setState(175);
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
			setState(176);
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
			setState(178);
			((CoordinatesRelativeToContext)_localctx).label = match(ID);
			setState(179);
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
			setState(181);
			match(FACING);
			setState(182);
			((DirectionContext)_localctx).dir = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 131941395333120L) != 0)) ) {
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
			setState(204);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VIRTUAL:
				_localctx = new VirtualContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(184);
				match(VIRTUAL);
				}
				break;
			case COLOR:
				_localctx = new ColorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				match(COLOR);
				setState(186);
				colorDef();
				}
				break;
			case CONNECTED:
				_localctx = new ConnectionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(187);
				match(CONNECTED);
				setState(188);
				match(TO);
				setState(189);
				connectionList();
				}
				break;
			case BLOCK:
				_localctx = new BlocktypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(190);
				match(BLOCK);
				setState(191);
				match(REF);
				}
				break;
			case COMMAND:
				_localctx = new UnitcommandContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(192);
				match(COMMAND);
				setState(193);
				match(REF);
				}
				break;
			case ITEM:
				_localctx = new ItemContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(194);
				match(ITEM);
				setState(195);
				match(REF);
				}
				break;
			case LIQUID:
				_localctx = new LiquidContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(196);
				match(LIQUID);
				setState(197);
				match(REF);
				}
				break;
			case UNIT:
				_localctx = new UnitContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(198);
				match(UNIT);
				setState(199);
				match(REF);
				}
				break;
			case TEXT:
				_localctx = new TextContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(200);
				match(TEXT);
				setState(201);
				((TextContext)_localctx).text = textDef();
				}
				break;
			case DISABLED:
			case ENABLED:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(202);
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
				setState(203);
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
			setState(206);
			match(RGBA);
			setState(207);
			match(LEFTPAREN);
			setState(208);
			((ColorDefContext)_localctx).red = number();
			setState(209);
			match(COMMA);
			setState(210);
			((ColorDefContext)_localctx).green = number();
			setState(211);
			match(COMMA);
			setState(212);
			((ColorDefContext)_localctx).blue = number();
			setState(213);
			match(COMMA);
			setState(214);
			((ColorDefContext)_localctx).alpha = number();
			setState(215);
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
			setState(217);
			connection();
			setState(222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(218);
				match(COMMA);
				setState(219);
				connection();
				}
				}
				setState(224);
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
			setState(228);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEFTPAREN:
				_localctx = new ConnAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				coordinates();
				}
				break;
			case MINUS:
			case PLUS:
				_localctx = new ConnRelContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(226);
				relativeCoordinates();
				}
				break;
			case ID:
				_localctx = new ConnNameContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(227);
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
		enterRule(_localctx, 40, RULE_processor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			match(PROCESSOR);
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LINKS) {
				{
				setState(231);
				((ProcessorContext)_localctx).links = processorLinks();
				}
			}

			setState(240);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MINDCODE:
				{
				setState(234);
				match(MINDCODE);
				setState(235);
				match(ASSIGN);
				setState(236);
				((ProcessorContext)_localctx).mindcode = program();
				}
				break;
			case MLOG:
				{
				setState(237);
				match(MLOG);
				setState(238);
				match(ASSIGN);
				setState(239);
				((ProcessorContext)_localctx).mlog = program();
				}
				break;
			case END:
			case PARAM:
				break;
			default:
				break;
			}
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARAM) {
				{
				setState(242);
				((ProcessorContext)_localctx).parameters = parametrization();
				}
			}

			setState(245);
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
			setState(247);
			match(LINKS);
			setState(251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 162278020655087616L) != 0)) {
				{
				{
				setState(248);
				linkDef();
				}
				}
				setState(253);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(254);
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
			setState(265);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MUL:
			case PATTERN:
				_localctx = new LinkPatternContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(256);
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
				setState(257);
				((LinkPosContext)_localctx).linkPos = connection();
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(258);
					match(AS);
					setState(259);
					((LinkPosContext)_localctx).alias = match(ID);
					setState(261);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==VIRTUAL) {
						{
						setState(260);
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
			setState(267);
			programSnippet();
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS) {
				{
				{
				setState(268);
				match(PLUS);
				setState(269);
				programSnippet();
				}
				}
				setState(274);
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
			setState(278);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTBLOCK1:
			case TEXTBLOCK2:
			case TEXTLINE:
			case ID:
				_localctx = new ProgramStringContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(275);
				((ProgramStringContext)_localctx).text = textDef();
				}
				break;
			case FILE:
				_localctx = new ProgramFileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(276);
				match(FILE);
				setState(277);
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
			setState(282);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTBLOCK1:
			case TEXTBLOCK2:
			case TEXTLINE:
				_localctx = new TextLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(280);
				((TextLiteralContext)_localctx).reference = stringLiteral();
				}
				break;
			case ID:
				_localctx = new TextIdContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(281);
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
		enterRule(_localctx, 52, RULE_parametrization);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			match(PARAM);
			setState(288);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PARAMTOKEN) {
				{
				{
				setState(285);
				parameter();
				}
				}
				setState(290);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(291);
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
		enterRule(_localctx, 54, RULE_parameter);
		try {
			setState(299);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(293);
				((ParameterContext)_localctx).variable = match(PARAMTOKEN);
				setState(294);
				match(PARAMASSIGN);
				setState(295);
				((ParameterContext)_localctx).strValue = match(PARAMSTRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(296);
				((ParameterContext)_localctx).variable = match(PARAMTOKEN);
				setState(297);
				match(PARAMASSIGN);
				setState(298);
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
		enterRule(_localctx, 56, RULE_stringValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			((StringValueContext)_localctx).name = match(ID);
			setState(302);
			match(ASSIGN);
			setState(303);
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
		enterRule(_localctx, 58, RULE_stringLiteral);
		int _la;
		try {
			setState(307);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXTLINE:
				_localctx = new TextLineContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(305);
				match(TEXTLINE);
				}
				break;
			case TEXTBLOCK1:
			case TEXTBLOCK2:
				_localctx = new TextBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(306);
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
		enterRule(_localctx, 60, RULE_simpleStringLiteral);
		try {
			_localctx = new SimpleTextLineContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
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
		"\u0004\u0001E\u0138\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0001\u0000\u0004\u0000@\b\u0000\u000b\u0000\f\u0000A\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0003\u0001H\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0003\u0002L\b\u0002\u0001\u0002\u0001\u0002\u0004\u0002P\b\u0002"+
		"\u000b\u0002\f\u0002Q\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0003\u0003X\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u0004r\b\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0003\u0007y\b\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u007f\b\u0007\u0001\u0007"+
		"\u0003\u0007\u0082\b\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005"+
		"\t\u0089\b\t\n\t\f\t\u008c\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n"+
		"\u0001\n\u0001\n\u0003\n\u0095\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003"+
		"\n\u009b\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00a1\b\n\u0003\n"+
		"\u00a3\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00a8\b\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0003\u0010\u00cd\b\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0005\u0012\u00dd\b\u0012\n\u0012\f\u0012\u00e0\t\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0003\u0013\u00e5\b\u0013\u0001\u0014\u0001\u0014\u0003"+
		"\u0014\u00e9\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u00f1\b\u0014\u0001\u0014\u0003\u0014\u00f4"+
		"\b\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0005\u0015\u00fa"+
		"\b\u0015\n\u0015\f\u0015\u00fd\t\u0015\u0001\u0015\u0001\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u0106\b\u0016"+
		"\u0003\u0016\u0108\b\u0016\u0003\u0016\u010a\b\u0016\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0005\u0017\u010f\b\u0017\n\u0017\f\u0017\u0112\t\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u0117\b\u0018\u0001\u0019"+
		"\u0001\u0019\u0003\u0019\u011b\b\u0019\u0001\u001a\u0001\u001a\u0005\u001a"+
		"\u011f\b\u001a\n\u001a\f\u001a\u0122\t\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003"+
		"\u001b\u012c\b\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001d\u0001\u001d\u0003\u001d\u0134\b\u001d\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0000\u0000\u001f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<\u0000\t\u0001\u0000"+
		"45\u0002\u000044::\u0001\u000067\u0002\u0000\u000f\u000f  \u0002\u0000"+
		"((**\u0001\u0000+.\u0001\u0000\t\n\u0002\u0000))99\u0001\u000012\u014b"+
		"\u0000?\u0001\u0000\u0000\u0000\u0002G\u0001\u0000\u0000\u0000\u0004K"+
		"\u0001\u0000\u0000\u0000\u0006W\u0001\u0000\u0000\u0000\bq\u0001\u0000"+
		"\u0000\u0000\ns\u0001\u0000\u0000\u0000\fu\u0001\u0000\u0000\u0000\u000e"+
		"x\u0001\u0000\u0000\u0000\u0010\u0083\u0001\u0000\u0000\u0000\u0012\u0085"+
		"\u0001\u0000\u0000\u0000\u0014\u00a2\u0001\u0000\u0000\u0000\u0016\u00a7"+
		"\u0001\u0000\u0000\u0000\u0018\u00a9\u0001\u0000\u0000\u0000\u001a\u00af"+
		"\u0001\u0000\u0000\u0000\u001c\u00b2\u0001\u0000\u0000\u0000\u001e\u00b5"+
		"\u0001\u0000\u0000\u0000 \u00cc\u0001\u0000\u0000\u0000\"\u00ce\u0001"+
		"\u0000\u0000\u0000$\u00d9\u0001\u0000\u0000\u0000&\u00e4\u0001\u0000\u0000"+
		"\u0000(\u00e6\u0001\u0000\u0000\u0000*\u00f7\u0001\u0000\u0000\u0000,"+
		"\u0109\u0001\u0000\u0000\u0000.\u010b\u0001\u0000\u0000\u00000\u0116\u0001"+
		"\u0000\u0000\u00002\u011a\u0001\u0000\u0000\u00004\u011c\u0001\u0000\u0000"+
		"\u00006\u012b\u0001\u0000\u0000\u00008\u012d\u0001\u0000\u0000\u0000:"+
		"\u0133\u0001\u0000\u0000\u0000<\u0135\u0001\u0000\u0000\u0000>@\u0003"+
		"\u0002\u0001\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000"+
		"A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000"+
		"\u0000CD\u0005\u0000\u0000\u0001D\u0001\u0001\u0000\u0000\u0000EH\u0003"+
		"\u0004\u0002\u0000FH\u00038\u001c\u0000GE\u0001\u0000\u0000\u0000GF\u0001"+
		"\u0000\u0000\u0000H\u0003\u0001\u0000\u0000\u0000IJ\u00056\u0000\u0000"+
		"JL\u0005#\u0000\u0000KI\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000"+
		"LM\u0001\u0000\u0000\u0000MO\u0005\u001a\u0000\u0000NP\u0003\u0006\u0003"+
		"\u0000ON\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QO\u0001\u0000"+
		"\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0005"+
		"\u000b\u0000\u0000T\u0005\u0001\u0000\u0000\u0000UX\u0003\b\u0004\u0000"+
		"VX\u0003\u000e\u0007\u0000WU\u0001\u0000\u0000\u0000WV\u0001\u0000\u0000"+
		"\u0000X\u0007\u0001\u0000\u0000\u0000YZ\u0005\u0016\u0000\u0000Z[\u0005"+
		"\"\u0000\u0000[r\u00032\u0019\u0000\\]\u0005\u0007\u0000\u0000]^\u0005"+
		"\"\u0000\u0000^r\u00032\u0019\u0000_`\u0005\b\u0000\u0000`a\u0005\"\u0000"+
		"\u0000ar\u0003\u0018\f\u0000bc\u0005\u001b\u0000\u0000cd\u0005\"\u0000"+
		"\u0000dr\u00032\u0019\u0000ef\u0005\u000e\u0000\u0000fg\u0005\"\u0000"+
		"\u0000gr\u0003<\u001e\u0000hi\u0005\u001c\u0000\u0000ij\u0005\"\u0000"+
		"\u0000jr\u0003\f\u0006\u0000kl\u0005\u0014\u0000\u0000lm\u0005\"\u0000"+
		"\u0000mr\u00032\u0019\u0000no\u0005\u0015\u0000\u0000op\u0005\"\u0000"+
		"\u0000pr\u00032\u0019\u0000qY\u0001\u0000\u0000\u0000q\\\u0001\u0000\u0000"+
		"\u0000q_\u0001\u0000\u0000\u0000qb\u0001\u0000\u0000\u0000qe\u0001\u0000"+
		"\u0000\u0000qh\u0001\u0000\u0000\u0000qk\u0001\u0000\u0000\u0000qn\u0001"+
		"\u0000\u0000\u0000r\t\u0001\u0000\u0000\u0000st\u0007\u0000\u0000\u0000"+
		"t\u000b\u0001\u0000\u0000\u0000uv\u0007\u0001\u0000\u0000v\r\u0001\u0000"+
		"\u0000\u0000wy\u0003\u0012\t\u0000xw\u0001\u0000\u0000\u0000xy\u0001\u0000"+
		"\u0000\u0000yz\u0001\u0000\u0000\u0000z{\u00058\u0000\u0000{|\u0005\u0002"+
		"\u0000\u0000|~\u0003\u0014\n\u0000}\u007f\u0003\u001e\u000f\u0000~}\u0001"+
		"\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0081\u0001\u0000"+
		"\u0000\u0000\u0080\u0082\u0003 \u0010\u0000\u0081\u0080\u0001\u0000\u0000"+
		"\u0000\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u000f\u0001\u0000\u0000"+
		"\u0000\u0083\u0084\u0007\u0002\u0000\u0000\u0084\u0011\u0001\u0000\u0000"+
		"\u0000\u0085\u008a\u0003\u0010\b\u0000\u0086\u0087\u0005$\u0000\u0000"+
		"\u0087\u0089\u0003\u0010\b\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0089"+
		"\u008c\u0001\u0000\u0000\u0000\u008a\u0088\u0001\u0000\u0000\u0000\u008a"+
		"\u008b\u0001\u0000\u0000\u0000\u008b\u008d\u0001\u0000\u0000\u0000\u008c"+
		"\u008a\u0001\u0000\u0000\u0000\u008d\u008e\u0005#\u0000\u0000\u008e\u0013"+
		"\u0001\u0000\u0000\u0000\u008f\u00a3\u0003\u0016\u000b\u0000\u0090\u0091"+
		"\u0003\u0016\u000b\u0000\u0091\u0092\u0005&\u0000\u0000\u0092\u0094\u0003"+
		"\u0018\f\u0000\u0093\u0095\u0007\u0003\u0000\u0000\u0094\u0093\u0001\u0000"+
		"\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u00a3\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0003\u0016\u000b\u0000\u0097\u0098\u0005\'\u0000"+
		"\u0000\u0098\u009a\u0003\u0018\f\u0000\u0099\u009b\u0007\u0003\u0000\u0000"+
		"\u009a\u0099\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000"+
		"\u009b\u00a3\u0001\u0000\u0000\u0000\u009c\u009d\u0003\u0016\u000b\u0000"+
		"\u009d\u009e\u0005)\u0000\u0000\u009e\u00a0\u0003\u0018\f\u0000\u009f"+
		"\u00a1\u0007\u0003\u0000\u0000\u00a0\u009f\u0001\u0000\u0000\u0000\u00a0"+
		"\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a3\u0001\u0000\u0000\u0000\u00a2"+
		"\u008f\u0001\u0000\u0000\u0000\u00a2\u0090\u0001\u0000\u0000\u0000\u00a2"+
		"\u0096\u0001\u0000\u0000\u0000\u00a2\u009c\u0001\u0000\u0000\u0000\u00a3"+
		"\u0015\u0001\u0000\u0000\u0000\u00a4\u00a8\u0003\u0018\f\u0000\u00a5\u00a8"+
		"\u0003\u001a\r\u0000\u00a6\u00a8\u0003\u001c\u000e\u0000\u00a7\u00a4\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a7\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a8\u0017\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005"+
		"/\u0000\u0000\u00aa\u00ab\u0003\n\u0005\u0000\u00ab\u00ac\u0005$\u0000"+
		"\u0000\u00ac\u00ad\u0003\n\u0005\u0000\u00ad\u00ae\u00050\u0000\u0000"+
		"\u00ae\u0019\u0001\u0000\u0000\u0000\u00af\u00b0\u0007\u0004\u0000\u0000"+
		"\u00b0\u00b1\u0003\u0018\f\u0000\u00b1\u001b\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b3\u00056\u0000\u0000\u00b3\u00b4\u0003\u001a\r\u0000\u00b4\u001d"+
		"\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005\f\u0000\u0000\u00b6\u00b7\u0007"+
		"\u0005\u0000\u0000\u00b7\u001f\u0001\u0000\u0000\u0000\u00b8\u00cd\u0005"+
		"!\u0000\u0000\u00b9\u00ba\u0005\u0006\u0000\u0000\u00ba\u00cd\u0003\""+
		"\u0011\u0000\u00bb\u00bc\u0005\u0004\u0000\u0000\u00bc\u00bd\u0005\u001e"+
		"\u0000\u0000\u00bd\u00cd\u0003$\u0012\u0000\u00be\u00bf\u0005\u0003\u0000"+
		"\u0000\u00bf\u00cd\u00058\u0000\u0000\u00c0\u00c1\u0005\u0005\u0000\u0000"+
		"\u00c1\u00cd\u00058\u0000\u0000\u00c2\u00c3\u0005\u0010\u0000\u0000\u00c3"+
		"\u00cd\u00058\u0000\u0000\u00c4\u00c5\u0005\u0012\u0000\u0000\u00c5\u00cd"+
		"\u00058\u0000\u0000\u00c6\u00c7\u0005\u001f\u0000\u0000\u00c7\u00cd\u0005"+
		"8\u0000\u0000\u00c8\u00c9\u0005\u001d\u0000\u0000\u00c9\u00cd\u00032\u0019"+
		"\u0000\u00ca\u00cd\u0007\u0006\u0000\u0000\u00cb\u00cd\u0003(\u0014\u0000"+
		"\u00cc\u00b8\u0001\u0000\u0000\u0000\u00cc\u00b9\u0001\u0000\u0000\u0000"+
		"\u00cc\u00bb\u0001\u0000\u0000\u0000\u00cc\u00be\u0001\u0000\u0000\u0000"+
		"\u00cc\u00c0\u0001\u0000\u0000\u0000\u00cc\u00c2\u0001\u0000\u0000\u0000"+
		"\u00cc\u00c4\u0001\u0000\u0000\u0000\u00cc\u00c6\u0001\u0000\u0000\u0000"+
		"\u00cc\u00c8\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000"+
		"\u00cc\u00cb\u0001\u0000\u0000\u0000\u00cd!\u0001\u0000\u0000\u0000\u00ce"+
		"\u00cf\u0005\u0019\u0000\u0000\u00cf\u00d0\u0005/\u0000\u0000\u00d0\u00d1"+
		"\u0003\n\u0005\u0000\u00d1\u00d2\u0005$\u0000\u0000\u00d2\u00d3\u0003"+
		"\n\u0005\u0000\u00d3\u00d4\u0005$\u0000\u0000\u00d4\u00d5\u0003\n\u0005"+
		"\u0000\u00d5\u00d6\u0005$\u0000\u0000\u00d6\u00d7\u0003\n\u0005\u0000"+
		"\u00d7\u00d8\u00050\u0000\u0000\u00d8#\u0001\u0000\u0000\u0000\u00d9\u00de"+
		"\u0003&\u0013\u0000\u00da\u00db\u0005$\u0000\u0000\u00db\u00dd\u0003&"+
		"\u0013\u0000\u00dc\u00da\u0001\u0000\u0000\u0000\u00dd\u00e0\u0001\u0000"+
		"\u0000\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00de\u00df\u0001\u0000"+
		"\u0000\u0000\u00df%\u0001\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000"+
		"\u0000\u00e1\u00e5\u0003\u0018\f\u0000\u00e2\u00e5\u0003\u001a\r\u0000"+
		"\u00e3\u00e5\u00056\u0000\u0000\u00e4\u00e1\u0001\u0000\u0000\u0000\u00e4"+
		"\u00e2\u0001\u0000\u0000\u0000\u00e4\u00e3\u0001\u0000\u0000\u0000\u00e5"+
		"\'\u0001\u0000\u0000\u0000\u00e6\u00e8\u0005\u0018\u0000\u0000\u00e7\u00e9"+
		"\u0003*\u0015\u0000\u00e8\u00e7\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001"+
		"\u0000\u0000\u0000\u00e9\u00f0\u0001\u0000\u0000\u0000\u00ea\u00eb\u0005"+
		"\u0014\u0000\u0000\u00eb\u00ec\u0005\"\u0000\u0000\u00ec\u00f1\u0003."+
		"\u0017\u0000\u00ed\u00ee\u0005\u0015\u0000\u0000\u00ee\u00ef\u0005\"\u0000"+
		"\u0000\u00ef\u00f1\u0003.\u0017\u0000\u00f0\u00ea\u0001\u0000\u0000\u0000"+
		"\u00f0\u00ed\u0001\u0000\u0000\u0000\u00f0\u00f1\u0001\u0000\u0000\u0000"+
		"\u00f1\u00f3\u0001\u0000\u0000\u0000\u00f2\u00f4\u00034\u001a\u0000\u00f3"+
		"\u00f2\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4"+
		"\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f6\u0005\u000b\u0000\u0000\u00f6"+
		")\u0001\u0000\u0000\u0000\u00f7\u00fb\u0005\u0011\u0000\u0000\u00f8\u00fa"+
		"\u0003,\u0016\u0000\u00f9\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fd\u0001"+
		"\u0000\u0000\u0000\u00fb\u00f9\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001"+
		"\u0000\u0000\u0000\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd\u00fb\u0001"+
		"\u0000\u0000\u0000\u00fe\u00ff\u0005\u000b\u0000\u0000\u00ff+\u0001\u0000"+
		"\u0000\u0000\u0100\u010a\u0007\u0007\u0000\u0000\u0101\u0107\u0003&\u0013"+
		"\u0000\u0102\u0103\u0005\u0001\u0000\u0000\u0103\u0105\u00056\u0000\u0000"+
		"\u0104\u0106\u0005!\u0000\u0000\u0105\u0104\u0001\u0000\u0000\u0000\u0105"+
		"\u0106\u0001\u0000\u0000\u0000\u0106\u0108\u0001\u0000\u0000\u0000\u0107"+
		"\u0102\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000\u0108"+
		"\u010a\u0001\u0000\u0000\u0000\u0109\u0100\u0001\u0000\u0000\u0000\u0109"+
		"\u0101\u0001\u0000\u0000\u0000\u010a-\u0001\u0000\u0000\u0000\u010b\u0110"+
		"\u00030\u0018\u0000\u010c\u010d\u0005*\u0000\u0000\u010d\u010f\u00030"+
		"\u0018\u0000\u010e\u010c\u0001\u0000\u0000\u0000\u010f\u0112\u0001\u0000"+
		"\u0000\u0000\u0110\u010e\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000"+
		"\u0000\u0000\u0111/\u0001\u0000\u0000\u0000\u0112\u0110\u0001\u0000\u0000"+
		"\u0000\u0113\u0117\u00032\u0019\u0000\u0114\u0115\u0005\r\u0000\u0000"+
		"\u0115\u0117\u00032\u0019\u0000\u0116\u0113\u0001\u0000\u0000\u0000\u0116"+
		"\u0114\u0001\u0000\u0000\u0000\u01171\u0001\u0000\u0000\u0000\u0118\u011b"+
		"\u0003:\u001d\u0000\u0119\u011b\u00056\u0000\u0000\u011a\u0118\u0001\u0000"+
		"\u0000\u0000\u011a\u0119\u0001\u0000\u0000\u0000\u011b3\u0001\u0000\u0000"+
		"\u0000\u011c\u0120\u0005\u0017\u0000\u0000\u011d\u011f\u00036\u001b\u0000"+
		"\u011e\u011d\u0001\u0000\u0000\u0000\u011f\u0122\u0001\u0000\u0000\u0000"+
		"\u0120\u011e\u0001\u0000\u0000\u0000\u0120\u0121\u0001\u0000\u0000\u0000"+
		"\u0121\u0123\u0001\u0000\u0000\u0000\u0122\u0120\u0001\u0000\u0000\u0000"+
		"\u0123\u0124\u0005?\u0000\u0000\u01245\u0001\u0000\u0000\u0000\u0125\u0126"+
		"\u0005E\u0000\u0000\u0126\u0127\u0005@\u0000\u0000\u0127\u012c\u0005D"+
		"\u0000\u0000\u0128\u0129\u0005E\u0000\u0000\u0129\u012a\u0005@\u0000\u0000"+
		"\u012a\u012c\u0005E\u0000\u0000\u012b\u0125\u0001\u0000\u0000\u0000\u012b"+
		"\u0128\u0001\u0000\u0000\u0000\u012c7\u0001\u0000\u0000\u0000\u012d\u012e"+
		"\u00056\u0000\u0000\u012e\u012f\u0005\"\u0000\u0000\u012f\u0130\u0003"+
		":\u001d\u0000\u01309\u0001\u0000\u0000\u0000\u0131\u0134\u00053\u0000"+
		"\u0000\u0132\u0134\u0007\b\u0000\u0000\u0133\u0131\u0001\u0000\u0000\u0000"+
		"\u0133\u0132\u0001\u0000\u0000\u0000\u0134;\u0001\u0000\u0000\u0000\u0135"+
		"\u0136\u00053\u0000\u0000\u0136=\u0001\u0000\u0000\u0000\u001fAGKQWqx"+
		"~\u0081\u008a\u0094\u009a\u00a0\u00a2\u00a7\u00cc\u00de\u00e4\u00e8\u00f0"+
		"\u00f3\u00fb\u0105\u0107\u0109\u0110\u0116\u011a\u0120\u012b\u0133";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
