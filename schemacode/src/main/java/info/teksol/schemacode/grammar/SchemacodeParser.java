// Generated from Schemacode.g4 by ANTLR 4.9.1
package info.teksol.schemacode.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SchemacodeParser extends Parser {
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
	public static final int
		RULE_definitions = 0, RULE_definition = 1, RULE_schematic = 2, RULE_schematicItem = 3, 
		RULE_attribute = 4, RULE_block = 5, RULE_labelList = 6, RULE_position = 7, 
		RULE_coordinates = 8, RULE_relativeCoordinates = 9, RULE_coordinatesRelativeTo = 10, 
		RULE_direction = 11, RULE_configuration = 12, RULE_colorDef = 13, RULE_connectionList = 14, 
		RULE_connection = 15, RULE_processor = 16, RULE_processorLinks = 17, RULE_linkDef = 18, 
		RULE_program = 19, RULE_textDef = 20, RULE_stringValue = 21, RULE_stringLiteral = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"definitions", "definition", "schematic", "schematicItem", "attribute", 
			"block", "labelList", "position", "coordinates", "relativeCoordinates", 
			"coordinatesRelativeTo", "direction", "configuration", "colorDef", "connectionList", 
			"connection", "processor", "processorLinks", "linkDef", "program", "textDef", 
			"stringValue", "stringLiteral"
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

	@Override
	public String getGrammarFileName() { return "Schemacode.g4"; }

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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDefinitions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDefinitions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDefinitions(this);
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
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(46);
				definition();
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Schematic || _la==Id );
			setState(51);
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefinitionContext definition() throws RecognitionException {
		DefinitionContext _localctx = new DefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_definition);
		try {
			setState(55);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(53);
				schematic();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(54);
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

	public static class SchematicContext extends ParserRuleContext {
		public Token name;
		public SchematicItemContext items;
		public TerminalNode Schematic() { return getToken(SchemacodeParser.Schematic, 0); }
		public TerminalNode End() { return getToken(SchemacodeParser.End, 0); }
		public TerminalNode Colon() { return getToken(SchemacodeParser.Colon, 0); }
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterSchematic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitSchematic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitSchematic(this);
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
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(57);
				((SchematicContext)_localctx).name = match(Id);
				setState(58);
				match(Colon);
				}
			}

			setState(61);
			match(Schematic);
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				((SchematicContext)_localctx).items = schematicItem();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Description) | (1L << Dimensions) | (1L << Name) | (1L << Tag) | (1L << Id) | (1L << Ref))) != 0) );
			setState(67);
			match(End);
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterSchematicItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitSchematicItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitSchematicItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchematicItemContext schematicItem() throws RecognitionException {
		SchematicItemContext _localctx = new SchematicItemContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_schematicItem);
		try {
			setState(71);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Description:
			case Dimensions:
			case Name:
			case Tag:
				enterOuterAlt(_localctx, 1);
				{
				setState(69);
				attribute();
				}
				break;
			case Id:
			case Ref:
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
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
	public static class NameContext extends AttributeContext {
		public TextDefContext name;
		public TerminalNode Name() { return getToken(SchemacodeParser.Name, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public NameContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SchemaTagContext extends AttributeContext {
		public TextDefContext tag;
		public TerminalNode Tag() { return getToken(SchemacodeParser.Tag, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public SchemaTagContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterSchemaTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitSchemaTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitSchemaTag(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DescriptionContext extends AttributeContext {
		public TextDefContext description;
		public TerminalNode Description() { return getToken(SchemacodeParser.Description, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public DescriptionContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDescription(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DimensionsContext extends AttributeContext {
		public TerminalNode Dimensions() { return getToken(SchemacodeParser.Dimensions, 0); }
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public DimensionsContext(AttributeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDimensions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDimensions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDimensions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_attribute);
		try {
			setState(85);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Name:
				_localctx = new NameContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(73);
				match(Name);
				setState(74);
				match(Assign);
				setState(75);
				((NameContext)_localctx).name = textDef();
				}
				break;
			case Description:
				_localctx = new DescriptionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				match(Description);
				setState(77);
				match(Assign);
				setState(78);
				((DescriptionContext)_localctx).description = textDef();
				}
				break;
			case Dimensions:
				_localctx = new DimensionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(79);
				match(Dimensions);
				setState(80);
				match(Assign);
				setState(81);
				coordinates();
				}
				break;
			case Tag:
				_localctx = new SchemaTagContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(82);
				match(Tag);
				setState(83);
				match(Assign);
				setState(84);
				((SchemaTagContext)_localctx).tag = textDef();
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

	public static class BlockContext extends ParserRuleContext {
		public LabelListContext labels;
		public Token type;
		public PositionContext pos;
		public DirectionContext dir;
		public ConfigurationContext cfg;
		public TerminalNode At() { return getToken(SchemacodeParser.At, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Id) {
				{
				setState(87);
				((BlockContext)_localctx).labels = labelList();
				}
			}

			setState(90);
			((BlockContext)_localctx).type = match(Ref);
			setState(91);
			match(At);
			setState(92);
			((BlockContext)_localctx).pos = position();
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Facing) {
				{
				setState(93);
				((BlockContext)_localctx).dir = direction();
				}
			}

			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Connected) | (1L << Color) | (1L << Disabled) | (1L << Enabled) | (1L << Item) | (1L << Liquid) | (1L << Processor) | (1L << Text) | (1L << Unit) | (1L << Virtual))) != 0)) {
				{
				setState(96);
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

	public static class LabelListContext extends ParserRuleContext {
		public List<TerminalNode> Id() { return getTokens(SchemacodeParser.Id); }
		public TerminalNode Id(int i) {
			return getToken(SchemacodeParser.Id, i);
		}
		public TerminalNode Colon() { return getToken(SchemacodeParser.Colon, 0); }
		public List<TerminalNode> Comma() { return getTokens(SchemacodeParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SchemacodeParser.Comma, i);
		}
		public LabelListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLabelList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLabelList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLabelList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelListContext labelList() throws RecognitionException {
		LabelListContext _localctx = new LabelListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_labelList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(Id);
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(100);
				match(Comma);
				setState(101);
				match(Id);
				}
				}
				setState(106);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(107);
			match(Colon);
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitPosition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitPosition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PositionContext position() throws RecognitionException {
		PositionContext _localctx = new PositionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_position);
		try {
			setState(112);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LeftParen:
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				coordinates();
				}
				break;
			case Minus:
			case Plus:
				enterOuterAlt(_localctx, 2);
				{
				setState(110);
				relativeCoordinates();
				}
				break;
			case Id:
				enterOuterAlt(_localctx, 3);
				{
				setState(111);
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

	public static class CoordinatesContext extends ParserRuleContext {
		public Token x;
		public Token y;
		public TerminalNode LeftParen() { return getToken(SchemacodeParser.LeftParen, 0); }
		public TerminalNode Comma() { return getToken(SchemacodeParser.Comma, 0); }
		public TerminalNode RightParen() { return getToken(SchemacodeParser.RightParen, 0); }
		public List<TerminalNode> Int() { return getTokens(SchemacodeParser.Int); }
		public TerminalNode Int(int i) {
			return getToken(SchemacodeParser.Int, i);
		}
		public CoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesContext coordinates() throws RecognitionException {
		CoordinatesContext _localctx = new CoordinatesContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_coordinates);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(LeftParen);
			setState(115);
			((CoordinatesContext)_localctx).x = match(Int);
			setState(116);
			match(Comma);
			setState(117);
			((CoordinatesContext)_localctx).y = match(Int);
			setState(118);
			match(RightParen);
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

	public static class RelativeCoordinatesContext extends ParserRuleContext {
		public Token op;
		public CoordinatesContext coord;
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode Plus() { return getToken(SchemacodeParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(SchemacodeParser.Minus, 0); }
		public RelativeCoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relativeCoordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterRelativeCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitRelativeCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitRelativeCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelativeCoordinatesContext relativeCoordinates() throws RecognitionException {
		RelativeCoordinatesContext _localctx = new RelativeCoordinatesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_relativeCoordinates);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			((RelativeCoordinatesContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==Minus || _la==Plus) ) {
				((RelativeCoordinatesContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(121);
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

	public static class CoordinatesRelativeToContext extends ParserRuleContext {
		public Token label;
		public RelativeCoordinatesContext relCoord;
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public CoordinatesRelativeToContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinatesRelativeTo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterCoordinatesRelativeTo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitCoordinatesRelativeTo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitCoordinatesRelativeTo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesRelativeToContext coordinatesRelativeTo() throws RecognitionException {
		CoordinatesRelativeToContext _localctx = new CoordinatesRelativeToContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_coordinatesRelativeTo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			((CoordinatesRelativeToContext)_localctx).label = match(Id);
			setState(124);
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

	public static class DirectionContext extends ParserRuleContext {
		public Token dir;
		public TerminalNode Facing() { return getToken(SchemacodeParser.Facing, 0); }
		public TerminalNode North() { return getToken(SchemacodeParser.North, 0); }
		public TerminalNode South() { return getToken(SchemacodeParser.South, 0); }
		public TerminalNode East() { return getToken(SchemacodeParser.East, 0); }
		public TerminalNode West() { return getToken(SchemacodeParser.West, 0); }
		public DirectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterDirection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitDirection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitDirection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectionContext direction() throws RecognitionException {
		DirectionContext _localctx = new DirectionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_direction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(Facing);
			setState(127);
			((DirectionContext)_localctx).dir = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << North) | (1L << South) | (1L << East) | (1L << West))) != 0)) ) {
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
	public static class VirtualContext extends ConfigurationContext {
		public TerminalNode Virtual() { return getToken(SchemacodeParser.Virtual, 0); }
		public VirtualContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterVirtual(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitVirtual(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitVirtual(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ItemContext extends ConfigurationContext {
		public TerminalNode Item() { return getToken(SchemacodeParser.Item, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public ItemContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitItem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnitContext extends ConfigurationContext {
		public TerminalNode Unit() { return getToken(SchemacodeParser.Unit, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public UnitContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitUnit(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanContext extends ConfigurationContext {
		public Token status;
		public TerminalNode Enabled() { return getToken(SchemacodeParser.Enabled, 0); }
		public TerminalNode Disabled() { return getToken(SchemacodeParser.Disabled, 0); }
		public BooleanContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ColorContext extends ConfigurationContext {
		public TerminalNode Color() { return getToken(SchemacodeParser.Color, 0); }
		public ColorDefContext colorDef() {
			return getRuleContext(ColorDefContext.class,0);
		}
		public ColorContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitColor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiquidContext extends ConfigurationContext {
		public TerminalNode Liquid() { return getToken(SchemacodeParser.Liquid, 0); }
		public TerminalNode Ref() { return getToken(SchemacodeParser.Ref, 0); }
		public LiquidContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLiquid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLiquid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLiquid(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TextContext extends ConfigurationContext {
		public TextDefContext text;
		public TerminalNode Text() { return getToken(SchemacodeParser.Text, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public TextContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitText(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicContext extends ConfigurationContext {
		public ProcessorContext def;
		public ProcessorContext processor() {
			return getRuleContext(ProcessorContext.class,0);
		}
		public LogicContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLogic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLogic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLogic(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConnectionsContext extends ConfigurationContext {
		public TerminalNode Connected() { return getToken(SchemacodeParser.Connected, 0); }
		public TerminalNode To() { return getToken(SchemacodeParser.To, 0); }
		public ConnectionListContext connectionList() {
			return getRuleContext(ConnectionListContext.class,0);
		}
		public ConnectionsContext(ConfigurationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnections(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnections(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnections(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConfigurationContext configuration() throws RecognitionException {
		ConfigurationContext _localctx = new ConfigurationContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_configuration);
		int _la;
		try {
			setState(145);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Virtual:
				_localctx = new VirtualContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				match(Virtual);
				}
				break;
			case Color:
				_localctx = new ColorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				match(Color);
				setState(131);
				colorDef();
				}
				break;
			case Connected:
				_localctx = new ConnectionsContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(132);
				match(Connected);
				setState(133);
				match(To);
				setState(134);
				connectionList();
				}
				break;
			case Item:
				_localctx = new ItemContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(135);
				match(Item);
				setState(136);
				match(Ref);
				}
				break;
			case Liquid:
				_localctx = new LiquidContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(137);
				match(Liquid);
				setState(138);
				match(Ref);
				}
				break;
			case Unit:
				_localctx = new UnitContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(139);
				match(Unit);
				setState(140);
				match(Ref);
				}
				break;
			case Text:
				_localctx = new TextContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(141);
				match(Text);
				setState(142);
				((TextContext)_localctx).text = textDef();
				}
				break;
			case Disabled:
			case Enabled:
				_localctx = new BooleanContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(143);
				((BooleanContext)_localctx).status = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==Disabled || _la==Enabled) ) {
					((BooleanContext)_localctx).status = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case Processor:
				_localctx = new LogicContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(144);
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

	public static class ColorDefContext extends ParserRuleContext {
		public Token red;
		public Token green;
		public Token blue;
		public Token alpha;
		public TerminalNode Rgba() { return getToken(SchemacodeParser.Rgba, 0); }
		public TerminalNode LeftParen() { return getToken(SchemacodeParser.LeftParen, 0); }
		public List<TerminalNode> Comma() { return getTokens(SchemacodeParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SchemacodeParser.Comma, i);
		}
		public TerminalNode RightParen() { return getToken(SchemacodeParser.RightParen, 0); }
		public List<TerminalNode> Int() { return getTokens(SchemacodeParser.Int); }
		public TerminalNode Int(int i) {
			return getToken(SchemacodeParser.Int, i);
		}
		public ColorDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colorDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterColorDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitColorDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitColorDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColorDefContext colorDef() throws RecognitionException {
		ColorDefContext _localctx = new ColorDefContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_colorDef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(Rgba);
			setState(148);
			match(LeftParen);
			setState(149);
			((ColorDefContext)_localctx).red = match(Int);
			setState(150);
			match(Comma);
			setState(151);
			((ColorDefContext)_localctx).green = match(Int);
			setState(152);
			match(Comma);
			setState(153);
			((ColorDefContext)_localctx).blue = match(Int);
			setState(154);
			match(Comma);
			setState(155);
			((ColorDefContext)_localctx).alpha = match(Int);
			setState(156);
			match(RightParen);
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

	public static class ConnectionListContext extends ParserRuleContext {
		public List<ConnectionContext> connection() {
			return getRuleContexts(ConnectionContext.class);
		}
		public ConnectionContext connection(int i) {
			return getRuleContext(ConnectionContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(SchemacodeParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(SchemacodeParser.Comma, i);
		}
		public ConnectionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connectionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnectionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnectionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnectionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConnectionListContext connectionList() throws RecognitionException {
		ConnectionListContext _localctx = new ConnectionListContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_connectionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			connection();
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(159);
				match(Comma);
				setState(160);
				connection();
				}
				}
				setState(165);
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
	public static class ConnAbsContext extends ConnectionContext {
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public ConnAbsContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnAbs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnAbs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnAbs(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConnNameContext extends ConnectionContext {
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public ConnNameContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnName(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConnRelContext extends ConnectionContext {
		public RelativeCoordinatesContext relativeCoordinates() {
			return getRuleContext(RelativeCoordinatesContext.class,0);
		}
		public ConnRelContext(ConnectionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterConnRel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitConnRel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitConnRel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConnectionContext connection() throws RecognitionException {
		ConnectionContext _localctx = new ConnectionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_connection);
		try {
			setState(169);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LeftParen:
				_localctx = new ConnAbsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(166);
				coordinates();
				}
				break;
			case Minus:
			case Plus:
				_localctx = new ConnRelContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(167);
				relativeCoordinates();
				}
				break;
			case Id:
				_localctx = new ConnNameContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(168);
				match(Id);
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

	public static class ProcessorContext extends ParserRuleContext {
		public ProcessorLinksContext links;
		public ProgramContext mindcode;
		public ProgramContext mlog;
		public TerminalNode Processor() { return getToken(SchemacodeParser.Processor, 0); }
		public TerminalNode End() { return getToken(SchemacodeParser.End, 0); }
		public TerminalNode Mindcode() { return getToken(SchemacodeParser.Mindcode, 0); }
		public TerminalNode Mlog() { return getToken(SchemacodeParser.Mlog, 0); }
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProcessor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProcessor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProcessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessorContext processor() throws RecognitionException {
		ProcessorContext _localctx = new ProcessorContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_processor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			match(Processor);
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Links) {
				{
				setState(172);
				((ProcessorContext)_localctx).links = processorLinks();
				}
			}

			setState(179);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Mindcode:
				{
				setState(175);
				match(Mindcode);
				setState(176);
				((ProcessorContext)_localctx).mindcode = program();
				}
				break;
			case Mlog:
				{
				setState(177);
				match(Mlog);
				setState(178);
				((ProcessorContext)_localctx).mlog = program();
				}
				break;
			case End:
				break;
			default:
				break;
			}
			setState(181);
			match(End);
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

	public static class ProcessorLinksContext extends ParserRuleContext {
		public TerminalNode Links() { return getToken(SchemacodeParser.Links, 0); }
		public TerminalNode End() { return getToken(SchemacodeParser.End, 0); }
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
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProcessorLinks(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProcessorLinks(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProcessorLinks(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessorLinksContext processorLinks() throws RecognitionException {
		ProcessorLinksContext _localctx = new ProcessorLinksContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_processorLinks);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			match(Links);
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Minus) | (1L << Plus) | (1L << LeftParen) | (1L << Id) | (1L << Pattern))) != 0)) {
				{
				{
				setState(184);
				linkDef();
				}
				}
				setState(189);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(190);
			match(End);
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
	public static class LinkPatternContext extends LinkDefContext {
		public Token linkPattern;
		public TerminalNode Pattern() { return getToken(SchemacodeParser.Pattern, 0); }
		public LinkPatternContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLinkPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLinkPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLinkPattern(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LinkPosContext extends LinkDefContext {
		public ConnectionContext linkPos;
		public Token alias;
		public Token virtual;
		public ConnectionContext connection() {
			return getRuleContext(ConnectionContext.class,0);
		}
		public TerminalNode As() { return getToken(SchemacodeParser.As, 0); }
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public TerminalNode Virtual() { return getToken(SchemacodeParser.Virtual, 0); }
		public LinkPosContext(LinkDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterLinkPos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitLinkPos(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitLinkPos(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LinkDefContext linkDef() throws RecognitionException {
		LinkDefContext _localctx = new LinkDefContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_linkDef);
		int _la;
		try {
			setState(201);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Pattern:
				_localctx = new LinkPatternContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(192);
				((LinkPatternContext)_localctx).linkPattern = match(Pattern);
				}
				break;
			case Minus:
			case Plus:
			case LeftParen:
			case Id:
				_localctx = new LinkPosContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(193);
				((LinkPosContext)_localctx).linkPos = connection();
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==As) {
					{
					setState(194);
					match(As);
					setState(195);
					((LinkPosContext)_localctx).alias = match(Id);
					setState(197);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==Virtual) {
						{
						setState(196);
						((LinkPosContext)_localctx).virtual = match(Virtual);
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

	public static class ProgramContext extends ParserRuleContext {
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	 
		public ProgramContext() { }
		public void copyFrom(ProgramContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ProgramStringContext extends ProgramContext {
		public TextDefContext text;
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public ProgramStringContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProgramString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProgramString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProgramString(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ProgramFileContext extends ProgramContext {
		public TextDefContext file;
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TerminalNode File() { return getToken(SchemacodeParser.File, 0); }
		public TextDefContext textDef() {
			return getRuleContext(TextDefContext.class,0);
		}
		public ProgramFileContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterProgramFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitProgramFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitProgramFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_program);
		try {
			setState(208);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				_localctx = new ProgramStringContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(203);
				match(Assign);
				setState(204);
				((ProgramStringContext)_localctx).text = textDef();
				}
				break;
			case 2:
				_localctx = new ProgramFileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(205);
				match(Assign);
				setState(206);
				match(File);
				setState(207);
				((ProgramFileContext)_localctx).file = textDef();
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
	public static class TextIdContext extends TextDefContext {
		public Token name;
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public TextIdContext(TextDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TextLiteralContext extends TextDefContext {
		public StringLiteralContext reference;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public TextLiteralContext(TextDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextDefContext textDef() throws RecognitionException {
		TextDefContext _localctx = new TextDefContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_textDef);
		try {
			setState(212);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TextBlock1:
			case TextBlock2:
			case TextLine:
				_localctx = new TextLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(210);
				((TextLiteralContext)_localctx).reference = stringLiteral();
				}
				break;
			case Id:
				_localctx = new TextIdContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(211);
				((TextIdContext)_localctx).name = match(Id);
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

	public static class StringValueContext extends ParserRuleContext {
		public Token name;
		public StringLiteralContext string;
		public TerminalNode Assign() { return getToken(SchemacodeParser.Assign, 0); }
		public TerminalNode Id() { return getToken(SchemacodeParser.Id, 0); }
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public StringValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterStringValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitStringValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitStringValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringValueContext stringValue() throws RecognitionException {
		StringValueContext _localctx = new StringValueContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stringValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			((StringValueContext)_localctx).name = match(Id);
			setState(215);
			match(Assign);
			setState(216);
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
	public static class TextLineContext extends StringLiteralContext {
		public TerminalNode TextLine() { return getToken(SchemacodeParser.TextLine, 0); }
		public TextLineContext(StringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextLine(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TextBlockContext extends StringLiteralContext {
		public TerminalNode TextBlock1() { return getToken(SchemacodeParser.TextBlock1, 0); }
		public TerminalNode TextBlock2() { return getToken(SchemacodeParser.TextBlock2, 0); }
		public TextBlockContext(StringLiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).enterTextBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SchemacodeListener ) ((SchemacodeListener)listener).exitTextBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SchemacodeVisitor ) return ((SchemacodeVisitor<? extends T>)visitor).visitTextBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_stringLiteral);
		int _la;
		try {
			setState(220);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TextLine:
				_localctx = new TextLineContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(218);
				match(TextLine);
				}
				break;
			case TextBlock1:
			case TextBlock2:
				_localctx = new TextBlockContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(219);
				_la = _input.LA(1);
				if ( !(_la==TextBlock1 || _la==TextBlock2) ) {
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\62\u00e1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\6\2\62"+
		"\n\2\r\2\16\2\63\3\2\3\2\3\3\3\3\5\3:\n\3\3\4\3\4\5\4>\n\4\3\4\3\4\6\4"+
		"B\n\4\r\4\16\4C\3\4\3\4\3\5\3\5\5\5J\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\5\6X\n\6\3\7\5\7[\n\7\3\7\3\7\3\7\3\7\5\7a\n\7\3\7"+
		"\5\7d\n\7\3\b\3\b\3\b\7\bi\n\b\f\b\16\bl\13\b\3\b\3\b\3\t\3\t\3\t\5\t"+
		"s\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\5\16\u0094\n\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\7\20\u00a4\n\20\f\20\16\20\u00a7\13\20\3\21"+
		"\3\21\3\21\5\21\u00ac\n\21\3\22\3\22\5\22\u00b0\n\22\3\22\3\22\3\22\3"+
		"\22\5\22\u00b6\n\22\3\22\3\22\3\23\3\23\7\23\u00bc\n\23\f\23\16\23\u00bf"+
		"\13\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\5\24\u00c8\n\24\5\24\u00ca\n"+
		"\24\5\24\u00cc\n\24\3\25\3\25\3\25\3\25\3\25\5\25\u00d3\n\25\3\26\3\26"+
		"\5\26\u00d7\n\26\3\27\3\27\3\27\3\27\3\30\3\30\5\30\u00df\n\30\3\30\2"+
		"\2\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\6\3\2!\"\3\2#"+
		"&\3\2\t\n\3\2)*\2\u00ec\2\61\3\2\2\2\49\3\2\2\2\6=\3\2\2\2\bI\3\2\2\2"+
		"\nW\3\2\2\2\fZ\3\2\2\2\16e\3\2\2\2\20r\3\2\2\2\22t\3\2\2\2\24z\3\2\2\2"+
		"\26}\3\2\2\2\30\u0080\3\2\2\2\32\u0093\3\2\2\2\34\u0095\3\2\2\2\36\u00a0"+
		"\3\2\2\2 \u00ab\3\2\2\2\"\u00ad\3\2\2\2$\u00b9\3\2\2\2&\u00cb\3\2\2\2"+
		"(\u00d2\3\2\2\2*\u00d6\3\2\2\2,\u00d8\3\2\2\2.\u00de\3\2\2\2\60\62\5\4"+
		"\3\2\61\60\3\2\2\2\62\63\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\65\3\2"+
		"\2\2\65\66\7\2\2\3\66\3\3\2\2\2\67:\5\6\4\28:\5,\27\29\67\3\2\2\298\3"+
		"\2\2\2:\5\3\2\2\2;<\7-\2\2<>\7\36\2\2=;\3\2\2\2=>\3\2\2\2>?\3\2\2\2?A"+
		"\7\27\2\2@B\5\b\5\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DE\3\2\2\2"+
		"EF\7\13\2\2F\7\3\2\2\2GJ\5\n\6\2HJ\5\f\7\2IG\3\2\2\2IH\3\2\2\2J\t\3\2"+
		"\2\2KL\7\24\2\2LM\7\35\2\2MX\5*\26\2NO\7\7\2\2OP\7\35\2\2PX\5*\26\2QR"+
		"\7\b\2\2RS\7\35\2\2SX\5\22\n\2TU\7\30\2\2UV\7\35\2\2VX\5*\26\2WK\3\2\2"+
		"\2WN\3\2\2\2WQ\3\2\2\2WT\3\2\2\2X\13\3\2\2\2Y[\5\16\b\2ZY\3\2\2\2Z[\3"+
		"\2\2\2[\\\3\2\2\2\\]\7.\2\2]^\7\4\2\2^`\5\20\t\2_a\5\30\r\2`_\3\2\2\2"+
		"`a\3\2\2\2ac\3\2\2\2bd\5\32\16\2cb\3\2\2\2cd\3\2\2\2d\r\3\2\2\2ej\7-\2"+
		"\2fg\7\37\2\2gi\7-\2\2hf\3\2\2\2il\3\2\2\2jh\3\2\2\2jk\3\2\2\2km\3\2\2"+
		"\2lj\3\2\2\2mn\7\36\2\2n\17\3\2\2\2os\5\22\n\2ps\5\24\13\2qs\5\26\f\2"+
		"ro\3\2\2\2rp\3\2\2\2rq\3\2\2\2s\21\3\2\2\2tu\7\'\2\2uv\7,\2\2vw\7\37\2"+
		"\2wx\7,\2\2xy\7(\2\2y\23\3\2\2\2z{\t\2\2\2{|\5\22\n\2|\25\3\2\2\2}~\7"+
		"-\2\2~\177\5\24\13\2\177\27\3\2\2\2\u0080\u0081\7\f\2\2\u0081\u0082\t"+
		"\3\2\2\u0082\31\3\2\2\2\u0083\u0094\7\34\2\2\u0084\u0085\7\6\2\2\u0085"+
		"\u0094\5\34\17\2\u0086\u0087\7\5\2\2\u0087\u0088\7\32\2\2\u0088\u0094"+
		"\5\36\20\2\u0089\u008a\7\16\2\2\u008a\u0094\7.\2\2\u008b\u008c\7\20\2"+
		"\2\u008c\u0094\7.\2\2\u008d\u008e\7\33\2\2\u008e\u0094\7.\2\2\u008f\u0090"+
		"\7\31\2\2\u0090\u0094\5*\26\2\u0091\u0094\t\4\2\2\u0092\u0094\5\"\22\2"+
		"\u0093\u0083\3\2\2\2\u0093\u0084\3\2\2\2\u0093\u0086\3\2\2\2\u0093\u0089"+
		"\3\2\2\2\u0093\u008b\3\2\2\2\u0093\u008d\3\2\2\2\u0093\u008f\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0093\u0092\3\2\2\2\u0094\33\3\2\2\2\u0095\u0096\7\26\2"+
		"\2\u0096\u0097\7\'\2\2\u0097\u0098\7,\2\2\u0098\u0099\7\37\2\2\u0099\u009a"+
		"\7,\2\2\u009a\u009b\7\37\2\2\u009b\u009c\7,\2\2\u009c\u009d\7\37\2\2\u009d"+
		"\u009e\7,\2\2\u009e\u009f\7(\2\2\u009f\35\3\2\2\2\u00a0\u00a5\5 \21\2"+
		"\u00a1\u00a2\7\37\2\2\u00a2\u00a4\5 \21\2\u00a3\u00a1\3\2\2\2\u00a4\u00a7"+
		"\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\37\3\2\2\2\u00a7"+
		"\u00a5\3\2\2\2\u00a8\u00ac\5\22\n\2\u00a9\u00ac\5\24\13\2\u00aa\u00ac"+
		"\7-\2\2\u00ab\u00a8\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00aa\3\2\2\2\u00ac"+
		"!\3\2\2\2\u00ad\u00af\7\25\2\2\u00ae\u00b0\5$\23\2\u00af\u00ae\3\2\2\2"+
		"\u00af\u00b0\3\2\2\2\u00b0\u00b5\3\2\2\2\u00b1\u00b2\7\22\2\2\u00b2\u00b6"+
		"\5(\25\2\u00b3\u00b4\7\23\2\2\u00b4\u00b6\5(\25\2\u00b5\u00b1\3\2\2\2"+
		"\u00b5\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b8"+
		"\7\13\2\2\u00b8#\3\2\2\2\u00b9\u00bd\7\17\2\2\u00ba\u00bc\5&\24\2\u00bb"+
		"\u00ba\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2"+
		"\2\2\u00be\u00c0\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c1\7\13\2\2\u00c1"+
		"%\3\2\2\2\u00c2\u00cc\7/\2\2\u00c3\u00c9\5 \21\2\u00c4\u00c5\7\3\2\2\u00c5"+
		"\u00c7\7-\2\2\u00c6\u00c8\7\34\2\2\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2"+
		"\2\2\u00c8\u00ca\3\2\2\2\u00c9\u00c4\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca"+
		"\u00cc\3\2\2\2\u00cb\u00c2\3\2\2\2\u00cb\u00c3\3\2\2\2\u00cc\'\3\2\2\2"+
		"\u00cd\u00ce\7\35\2\2\u00ce\u00d3\5*\26\2\u00cf\u00d0\7\35\2\2\u00d0\u00d1"+
		"\7\r\2\2\u00d1\u00d3\5*\26\2\u00d2\u00cd\3\2\2\2\u00d2\u00cf\3\2\2\2\u00d3"+
		")\3\2\2\2\u00d4\u00d7\5.\30\2\u00d5\u00d7\7-\2\2\u00d6\u00d4\3\2\2\2\u00d6"+
		"\u00d5\3\2\2\2\u00d7+\3\2\2\2\u00d8\u00d9\7-\2\2\u00d9\u00da\7\35\2\2"+
		"\u00da\u00db\5.\30\2\u00db-\3\2\2\2\u00dc\u00df\7+\2\2\u00dd\u00df\t\5"+
		"\2\2\u00de\u00dc\3\2\2\2\u00de\u00dd\3\2\2\2\u00df/\3\2\2\2\31\639=CI"+
		"WZ`cjr\u0093\u00a5\u00ab\u00af\u00b5\u00bd\u00c7\u00c9\u00cb\u00d2\u00d6"+
		"\u00de";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}