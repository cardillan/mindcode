// Generated from Schemacode.g4 by ANTLR 4.13.1
package info.teksol.schemacode.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SchemacodeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

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

	@SuppressWarnings("CheckReturnValue")
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
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 26388283523168L) != 0) );
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 109597080L) != 0)) {
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 128849018880L) != 0)) ) {
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 44124346515456L) != 0)) {
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
	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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

	@SuppressWarnings("CheckReturnValue")
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
	@SuppressWarnings("CheckReturnValue")
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
		"\u0004\u00010\u00df\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0001\u0000\u0004\u00000\b\u0000\u000b\u0000"+
		"\f\u00001\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001"+
		"8\b\u0001\u0001\u0002\u0001\u0002\u0003\u0002<\b\u0002\u0001\u0002\u0001"+
		"\u0002\u0004\u0002@\b\u0002\u000b\u0002\f\u0002A\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0003\u0003H\b\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004V\b\u0004"+
		"\u0001\u0005\u0003\u0005Y\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005_\b\u0005\u0001\u0005\u0003\u0005b\b\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0005\u0006g\b\u0006\n\u0006\f\u0006j\t"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0003"+
		"\u0007q\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003"+
		"\f\u0092\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e"+
		"\u00a2\b\u000e\n\u000e\f\u000e\u00a5\t\u000e\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u00aa\b\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u00ae"+
		"\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00b4"+
		"\b\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0005\u0011\u00ba"+
		"\b\u0011\n\u0011\f\u0011\u00bd\t\u0011\u0001\u0011\u0001\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00c6\b\u0012"+
		"\u0003\u0012\u00c8\b\u0012\u0003\u0012\u00ca\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00d1\b\u0013\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u00d5\b\u0014\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0003\u0016\u00dd\b\u0016\u0001"+
		"\u0016\u0000\u0000\u0017\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,\u0000\u0004\u0001\u0000\u001f"+
		" \u0001\u0000!$\u0001\u0000\u0007\b\u0001\u0000\'(\u00ea\u0000/\u0001"+
		"\u0000\u0000\u0000\u00027\u0001\u0000\u0000\u0000\u0004;\u0001\u0000\u0000"+
		"\u0000\u0006G\u0001\u0000\u0000\u0000\bU\u0001\u0000\u0000\u0000\nX\u0001"+
		"\u0000\u0000\u0000\fc\u0001\u0000\u0000\u0000\u000ep\u0001\u0000\u0000"+
		"\u0000\u0010r\u0001\u0000\u0000\u0000\u0012x\u0001\u0000\u0000\u0000\u0014"+
		"{\u0001\u0000\u0000\u0000\u0016~\u0001\u0000\u0000\u0000\u0018\u0091\u0001"+
		"\u0000\u0000\u0000\u001a\u0093\u0001\u0000\u0000\u0000\u001c\u009e\u0001"+
		"\u0000\u0000\u0000\u001e\u00a9\u0001\u0000\u0000\u0000 \u00ab\u0001\u0000"+
		"\u0000\u0000\"\u00b7\u0001\u0000\u0000\u0000$\u00c9\u0001\u0000\u0000"+
		"\u0000&\u00d0\u0001\u0000\u0000\u0000(\u00d4\u0001\u0000\u0000\u0000*"+
		"\u00d6\u0001\u0000\u0000\u0000,\u00dc\u0001\u0000\u0000\u0000.0\u0003"+
		"\u0002\u0001\u0000/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u0000"+
		"1/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000023\u0001\u0000\u0000"+
		"\u000034\u0005\u0000\u0000\u00014\u0001\u0001\u0000\u0000\u000058\u0003"+
		"\u0004\u0002\u000068\u0003*\u0015\u000075\u0001\u0000\u0000\u000076\u0001"+
		"\u0000\u0000\u00008\u0003\u0001\u0000\u0000\u00009:\u0005+\u0000\u0000"+
		":<\u0005\u001c\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000"+
		"\u0000<=\u0001\u0000\u0000\u0000=?\u0005\u0015\u0000\u0000>@\u0003\u0006"+
		"\u0003\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A?\u0001"+
		"\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000"+
		"CD\u0005\t\u0000\u0000D\u0005\u0001\u0000\u0000\u0000EH\u0003\b\u0004"+
		"\u0000FH\u0003\n\u0005\u0000GE\u0001\u0000\u0000\u0000GF\u0001\u0000\u0000"+
		"\u0000H\u0007\u0001\u0000\u0000\u0000IJ\u0005\u0012\u0000\u0000JK\u0005"+
		"\u001b\u0000\u0000KV\u0003(\u0014\u0000LM\u0005\u0005\u0000\u0000MN\u0005"+
		"\u001b\u0000\u0000NV\u0003(\u0014\u0000OP\u0005\u0006\u0000\u0000PQ\u0005"+
		"\u001b\u0000\u0000QV\u0003\u0010\b\u0000RS\u0005\u0016\u0000\u0000ST\u0005"+
		"\u001b\u0000\u0000TV\u0003(\u0014\u0000UI\u0001\u0000\u0000\u0000UL\u0001"+
		"\u0000\u0000\u0000UO\u0001\u0000\u0000\u0000UR\u0001\u0000\u0000\u0000"+
		"V\t\u0001\u0000\u0000\u0000WY\u0003\f\u0006\u0000XW\u0001\u0000\u0000"+
		"\u0000XY\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0005,\u0000"+
		"\u0000[\\\u0005\u0002\u0000\u0000\\^\u0003\u000e\u0007\u0000]_\u0003\u0016"+
		"\u000b\u0000^]\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_a\u0001"+
		"\u0000\u0000\u0000`b\u0003\u0018\f\u0000a`\u0001\u0000\u0000\u0000ab\u0001"+
		"\u0000\u0000\u0000b\u000b\u0001\u0000\u0000\u0000ch\u0005+\u0000\u0000"+
		"de\u0005\u001d\u0000\u0000eg\u0005+\u0000\u0000fd\u0001\u0000\u0000\u0000"+
		"gj\u0001\u0000\u0000\u0000hf\u0001\u0000\u0000\u0000hi\u0001\u0000\u0000"+
		"\u0000ik\u0001\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000kl\u0005\u001c"+
		"\u0000\u0000l\r\u0001\u0000\u0000\u0000mq\u0003\u0010\b\u0000nq\u0003"+
		"\u0012\t\u0000oq\u0003\u0014\n\u0000pm\u0001\u0000\u0000\u0000pn\u0001"+
		"\u0000\u0000\u0000po\u0001\u0000\u0000\u0000q\u000f\u0001\u0000\u0000"+
		"\u0000rs\u0005%\u0000\u0000st\u0005*\u0000\u0000tu\u0005\u001d\u0000\u0000"+
		"uv\u0005*\u0000\u0000vw\u0005&\u0000\u0000w\u0011\u0001\u0000\u0000\u0000"+
		"xy\u0007\u0000\u0000\u0000yz\u0003\u0010\b\u0000z\u0013\u0001\u0000\u0000"+
		"\u0000{|\u0005+\u0000\u0000|}\u0003\u0012\t\u0000}\u0015\u0001\u0000\u0000"+
		"\u0000~\u007f\u0005\n\u0000\u0000\u007f\u0080\u0007\u0001\u0000\u0000"+
		"\u0080\u0017\u0001\u0000\u0000\u0000\u0081\u0092\u0005\u001a\u0000\u0000"+
		"\u0082\u0083\u0005\u0004\u0000\u0000\u0083\u0092\u0003\u001a\r\u0000\u0084"+
		"\u0085\u0005\u0003\u0000\u0000\u0085\u0086\u0005\u0018\u0000\u0000\u0086"+
		"\u0092\u0003\u001c\u000e\u0000\u0087\u0088\u0005\f\u0000\u0000\u0088\u0092"+
		"\u0005,\u0000\u0000\u0089\u008a\u0005\u000e\u0000\u0000\u008a\u0092\u0005"+
		",\u0000\u0000\u008b\u008c\u0005\u0019\u0000\u0000\u008c\u0092\u0005,\u0000"+
		"\u0000\u008d\u008e\u0005\u0017\u0000\u0000\u008e\u0092\u0003(\u0014\u0000"+
		"\u008f\u0092\u0007\u0002\u0000\u0000\u0090\u0092\u0003 \u0010\u0000\u0091"+
		"\u0081\u0001\u0000\u0000\u0000\u0091\u0082\u0001\u0000\u0000\u0000\u0091"+
		"\u0084\u0001\u0000\u0000\u0000\u0091\u0087\u0001\u0000\u0000\u0000\u0091"+
		"\u0089\u0001\u0000\u0000\u0000\u0091\u008b\u0001\u0000\u0000\u0000\u0091"+
		"\u008d\u0001\u0000\u0000\u0000\u0091\u008f\u0001\u0000\u0000\u0000\u0091"+
		"\u0090\u0001\u0000\u0000\u0000\u0092\u0019\u0001\u0000\u0000\u0000\u0093"+
		"\u0094\u0005\u0014\u0000\u0000\u0094\u0095\u0005%\u0000\u0000\u0095\u0096"+
		"\u0005*\u0000\u0000\u0096\u0097\u0005\u001d\u0000\u0000\u0097\u0098\u0005"+
		"*\u0000\u0000\u0098\u0099\u0005\u001d\u0000\u0000\u0099\u009a\u0005*\u0000"+
		"\u0000\u009a\u009b\u0005\u001d\u0000\u0000\u009b\u009c\u0005*\u0000\u0000"+
		"\u009c\u009d\u0005&\u0000\u0000\u009d\u001b\u0001\u0000\u0000\u0000\u009e"+
		"\u00a3\u0003\u001e\u000f\u0000\u009f\u00a0\u0005\u001d\u0000\u0000\u00a0"+
		"\u00a2\u0003\u001e\u000f\u0000\u00a1\u009f\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a5\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3"+
		"\u00a4\u0001\u0000\u0000\u0000\u00a4\u001d\u0001\u0000\u0000\u0000\u00a5"+
		"\u00a3\u0001\u0000\u0000\u0000\u00a6\u00aa\u0003\u0010\b\u0000\u00a7\u00aa"+
		"\u0003\u0012\t\u0000\u00a8\u00aa\u0005+\u0000\u0000\u00a9\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00a9\u00a8\u0001"+
		"\u0000\u0000\u0000\u00aa\u001f\u0001\u0000\u0000\u0000\u00ab\u00ad\u0005"+
		"\u0013\u0000\u0000\u00ac\u00ae\u0003\"\u0011\u0000\u00ad\u00ac\u0001\u0000"+
		"\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00b3\u0001\u0000"+
		"\u0000\u0000\u00af\u00b0\u0005\u0010\u0000\u0000\u00b0\u00b4\u0003&\u0013"+
		"\u0000\u00b1\u00b2\u0005\u0011\u0000\u0000\u00b2\u00b4\u0003&\u0013\u0000"+
		"\u00b3\u00af\u0001\u0000\u0000\u0000\u00b3\u00b1\u0001\u0000\u0000\u0000"+
		"\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000"+
		"\u00b5\u00b6\u0005\t\u0000\u0000\u00b6!\u0001\u0000\u0000\u0000\u00b7"+
		"\u00bb\u0005\r\u0000\u0000\u00b8\u00ba\u0003$\u0012\u0000\u00b9\u00b8"+
		"\u0001\u0000\u0000\u0000\u00ba\u00bd\u0001\u0000\u0000\u0000\u00bb\u00b9"+
		"\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00be"+
		"\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00be\u00bf"+
		"\u0005\t\u0000\u0000\u00bf#\u0001\u0000\u0000\u0000\u00c0\u00ca\u0005"+
		"-\u0000\u0000\u00c1\u00c7\u0003\u001e\u000f\u0000\u00c2\u00c3\u0005\u0001"+
		"\u0000\u0000\u00c3\u00c5\u0005+\u0000\u0000\u00c4\u00c6\u0005\u001a\u0000"+
		"\u0000\u00c5\u00c4\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000"+
		"\u0000\u00c6\u00c8\u0001\u0000\u0000\u0000\u00c7\u00c2\u0001\u0000\u0000"+
		"\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8\u00ca\u0001\u0000\u0000"+
		"\u0000\u00c9\u00c0\u0001\u0000\u0000\u0000\u00c9\u00c1\u0001\u0000\u0000"+
		"\u0000\u00ca%\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005\u001b\u0000\u0000"+
		"\u00cc\u00d1\u0003(\u0014\u0000\u00cd\u00ce\u0005\u001b\u0000\u0000\u00ce"+
		"\u00cf\u0005\u000b\u0000\u0000\u00cf\u00d1\u0003(\u0014\u0000\u00d0\u00cb"+
		"\u0001\u0000\u0000\u0000\u00d0\u00cd\u0001\u0000\u0000\u0000\u00d1\'\u0001"+
		"\u0000\u0000\u0000\u00d2\u00d5\u0003,\u0016\u0000\u00d3\u00d5\u0005+\u0000"+
		"\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d4\u00d3\u0001\u0000\u0000"+
		"\u0000\u00d5)\u0001\u0000\u0000\u0000\u00d6\u00d7\u0005+\u0000\u0000\u00d7"+
		"\u00d8\u0005\u001b\u0000\u0000\u00d8\u00d9\u0003,\u0016\u0000\u00d9+\u0001"+
		"\u0000\u0000\u0000\u00da\u00dd\u0005)\u0000\u0000\u00db\u00dd\u0007\u0003"+
		"\u0000\u0000\u00dc\u00da\u0001\u0000\u0000\u0000\u00dc\u00db\u0001\u0000"+
		"\u0000\u0000\u00dd-\u0001\u0000\u0000\u0000\u001717;AGUX^ahp\u0091\u00a3"+
		"\u00a9\u00ad\u00b3\u00bb\u00c5\u00c7\u00c9\u00d0\u00d4\u00dc";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}